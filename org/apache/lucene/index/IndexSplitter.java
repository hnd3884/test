package org.apache.lucene.index;

import java.util.Iterator;
import java.util.Collection;
import java.nio.file.CopyOption;
import java.util.Map;
import java.util.HashMap;
import java.nio.file.attribute.FileAttribute;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.io.IOException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.SuppressForbidden;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.Path;
import org.apache.lucene.store.FSDirectory;

public class IndexSplitter
{
    public SegmentInfos infos;
    FSDirectory fsDir;
    Path dir;
    
    @SuppressForbidden(reason = "System.out required: command line tool")
    public static void main(final String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: IndexSplitter <srcDir> -l (list the segments and their sizes)");
            System.err.println("IndexSplitter <srcDir> <destDir> <segments>+");
            System.err.println("IndexSplitter <srcDir> -d (delete the following segments)");
            return;
        }
        final Path srcDir = Paths.get(args[0], new String[0]);
        final IndexSplitter is = new IndexSplitter(srcDir);
        if (!Files.exists(srcDir, new LinkOption[0])) {
            throw new Exception("srcdir:" + srcDir.toAbsolutePath() + " doesn't exist");
        }
        if (args[1].equals("-l")) {
            is.listSegments();
        }
        else if (args[1].equals("-d")) {
            final List<String> segs = new ArrayList<String>();
            for (int x = 2; x < args.length; ++x) {
                segs.add(args[x]);
            }
            is.remove(segs.toArray(new String[0]));
        }
        else {
            final Path targetDir = Paths.get(args[1], new String[0]);
            final List<String> segs2 = new ArrayList<String>();
            for (int x2 = 2; x2 < args.length; ++x2) {
                segs2.add(args[x2]);
            }
            is.split(targetDir, segs2.toArray(new String[0]));
        }
    }
    
    public IndexSplitter(final Path dir) throws IOException {
        this.dir = dir;
        this.fsDir = FSDirectory.open(dir);
        this.infos = SegmentInfos.readLatestCommit((Directory)this.fsDir);
    }
    
    @SuppressForbidden(reason = "System.out required: command line tool")
    public void listSegments() throws IOException {
        final DecimalFormat formatter = new DecimalFormat("###,###.###", DecimalFormatSymbols.getInstance(Locale.ROOT));
        for (int x = 0; x < this.infos.size(); ++x) {
            final SegmentCommitInfo info = this.infos.info(x);
            final String sizeStr = formatter.format(info.sizeInBytes());
            System.out.println(info.info.name + " " + sizeStr);
        }
    }
    
    private int getIdx(final String name) {
        for (int x = 0; x < this.infos.size(); ++x) {
            if (name.equals(this.infos.info(x).info.name)) {
                return x;
            }
        }
        return -1;
    }
    
    private SegmentCommitInfo getInfo(final String name) {
        for (int x = 0; x < this.infos.size(); ++x) {
            if (name.equals(this.infos.info(x).info.name)) {
                return this.infos.info(x);
            }
        }
        return null;
    }
    
    public void remove(final String[] segs) throws IOException {
        for (final String n : segs) {
            final int idx = this.getIdx(n);
            this.infos.remove(idx);
        }
        this.infos.changed();
        this.infos.commit((Directory)this.fsDir);
    }
    
    public void split(final Path destDir, final String[] segs) throws IOException {
        Files.createDirectories(destDir, (FileAttribute<?>[])new FileAttribute[0]);
        final FSDirectory destFSDir = FSDirectory.open(destDir);
        final SegmentInfos destInfos = new SegmentInfos();
        destInfos.counter = this.infos.counter;
        for (final String n : segs) {
            final SegmentCommitInfo infoPerCommit = this.getInfo(n);
            final SegmentInfo info = infoPerCommit.info;
            final SegmentInfo newInfo = new SegmentInfo((Directory)destFSDir, info.getVersion(), info.name, info.maxDoc(), info.getUseCompoundFile(), info.getCodec(), info.getDiagnostics(), info.getId(), (Map)new HashMap());
            destInfos.add(new SegmentCommitInfo(newInfo, infoPerCommit.getDelCount(), infoPerCommit.getDelGen(), infoPerCommit.getFieldInfosGen(), infoPerCommit.getDocValuesGen()));
            final Collection<String> files = infoPerCommit.files();
            for (final String srcName : files) {
                final Path srcFile = this.dir.resolve(srcName);
                final Path destFile = destDir.resolve(srcName);
                Files.copy(srcFile, destFile, new CopyOption[0]);
            }
        }
        destInfos.changed();
        destInfos.commit((Directory)destFSDir);
    }
}
