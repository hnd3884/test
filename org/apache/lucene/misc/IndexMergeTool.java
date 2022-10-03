package org.apache.lucene.misc;

import java.io.IOException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.Paths;
import org.apache.lucene.util.SuppressForbidden;

@SuppressForbidden(reason = "System.out required: command line tool")
public class IndexMergeTool
{
    public static void main(final String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: IndexMergeTool <mergedIndex> <index1> <index2> [index3] ...");
            System.exit(1);
        }
        final FSDirectory mergedIndex = FSDirectory.open(Paths.get(args[0], new String[0]));
        final IndexWriter writer = new IndexWriter((Directory)mergedIndex, new IndexWriterConfig((Analyzer)null).setOpenMode(IndexWriterConfig.OpenMode.CREATE));
        final Directory[] indexes = new Directory[args.length - 1];
        for (int i = 1; i < args.length; ++i) {
            indexes[i - 1] = (Directory)FSDirectory.open(Paths.get(args[i], new String[0]));
        }
        System.out.println("Merging...");
        writer.addIndexes(indexes);
        System.out.println("Full merge...");
        writer.forceMerge(1);
        writer.close();
        System.out.println("Done.");
    }
}
