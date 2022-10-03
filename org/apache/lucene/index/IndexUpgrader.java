package org.apache.lucene.index;

import java.util.Collection;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.Analyzer;
import java.nio.file.Path;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.util.CommandLineUtil;
import java.nio.file.Paths;
import org.apache.lucene.util.PrintStreamInfoStream;
import java.io.IOException;
import org.apache.lucene.util.SuppressForbidden;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;

public final class IndexUpgrader
{
    private static final String LOG_PREFIX = "IndexUpgrader";
    private final Directory dir;
    private final IndexWriterConfig iwc;
    private final boolean deletePriorCommits;
    
    @SuppressForbidden(reason = "System.out required: command line tool")
    private static void printUsage() {
        System.err.println("Upgrades an index so all segments created with a previous Lucene version are rewritten.");
        System.err.println("Usage:");
        System.err.println("  java " + IndexUpgrader.class.getName() + " [-delete-prior-commits] [-verbose] [-dir-impl X] indexDir");
        System.err.println("This tool keeps only the last commit in an index; for this");
        System.err.println("reason, if the incoming index has more than one commit, the tool");
        System.err.println("refuses to run by default. Specify -delete-prior-commits to override");
        System.err.println("this, allowing the tool to delete all but the last commit.");
        System.err.println("Specify a " + FSDirectory.class.getSimpleName() + " implementation through the -dir-impl option to force its use. If no package is specified the " + FSDirectory.class.getPackage().getName() + " package will be used.");
        System.err.println("WARNING: This tool may reorder document IDs!");
        System.exit(1);
    }
    
    public static void main(final String[] args) throws IOException {
        parseArgs(args).upgrade();
    }
    
    @SuppressForbidden(reason = "System.out required: command line tool")
    static IndexUpgrader parseArgs(final String[] args) throws IOException {
        String path = null;
        boolean deletePriorCommits = false;
        InfoStream out = null;
        String dirImpl = null;
        for (int i = 0; i < args.length; ++i) {
            final String arg = args[i];
            if ("-delete-prior-commits".equals(arg)) {
                deletePriorCommits = true;
            }
            else if ("-verbose".equals(arg)) {
                out = new PrintStreamInfoStream(System.out);
            }
            else if ("-dir-impl".equals(arg)) {
                if (i == args.length - 1) {
                    System.out.println("ERROR: missing value for -dir-impl option");
                    System.exit(1);
                }
                ++i;
                dirImpl = args[i];
            }
            else if (path == null) {
                path = arg;
            }
            else {
                printUsage();
            }
        }
        if (path == null) {
            printUsage();
        }
        final Path p = Paths.get(path, new String[0]);
        Directory dir = null;
        if (dirImpl == null) {
            dir = FSDirectory.open(p);
        }
        else {
            dir = CommandLineUtil.newFSDirectory(dirImpl, p);
        }
        return new IndexUpgrader(dir, out, deletePriorCommits);
    }
    
    public IndexUpgrader(final Directory dir) {
        this(dir, new IndexWriterConfig(null), false);
    }
    
    public IndexUpgrader(final Directory dir, final InfoStream infoStream, final boolean deletePriorCommits) {
        this(dir, new IndexWriterConfig(null), deletePriorCommits);
        if (null != infoStream) {
            this.iwc.setInfoStream(infoStream);
        }
    }
    
    public IndexUpgrader(final Directory dir, final IndexWriterConfig iwc, final boolean deletePriorCommits) {
        this.dir = dir;
        this.iwc = iwc;
        this.deletePriorCommits = deletePriorCommits;
    }
    
    public void upgrade() throws IOException {
        if (!DirectoryReader.indexExists(this.dir)) {
            throw new IndexNotFoundException(this.dir.toString());
        }
        if (!this.deletePriorCommits) {
            final Collection<IndexCommit> commits = DirectoryReader.listCommits(this.dir);
            if (commits.size() > 1) {
                throw new IllegalArgumentException("This tool was invoked to not delete prior commit points, but the following commits were found: " + commits);
            }
        }
        this.iwc.setMergePolicy(new UpgradeIndexMergePolicy(this.iwc.getMergePolicy()));
        this.iwc.setIndexDeletionPolicy(new KeepOnlyLastCommitDeletionPolicy());
        try (final IndexWriter w = new IndexWriter(this.dir, this.iwc)) {
            final InfoStream infoStream = this.iwc.getInfoStream();
            if (infoStream.isEnabled("IndexUpgrader")) {
                infoStream.message("IndexUpgrader", "Upgrading all pre-" + Version.LATEST + " segments of index directory '" + this.dir + "' to version " + Version.LATEST + "...");
            }
            w.forceMerge(1);
            if (infoStream.isEnabled("IndexUpgrader")) {
                infoStream.message("IndexUpgrader", "All segments upgraded to version " + Version.LATEST);
                infoStream.message("IndexUpgrader", "Enforcing commit to rewrite all index metadata...");
            }
            w.setCommitData(w.getCommitData());
            assert w.hasUncommittedChanges();
            w.commit();
            if (infoStream.isEnabled("IndexUpgrader")) {
                infoStream.message("IndexUpgrader", "Committed upgraded metadata to index.");
            }
        }
    }
}
