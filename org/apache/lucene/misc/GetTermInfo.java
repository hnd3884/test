package org.apache.lucene.misc;

import org.apache.lucene.index.IndexReader;
import java.util.Locale;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.Paths;
import org.apache.lucene.util.SuppressForbidden;

@SuppressForbidden(reason = "System.out required: command line tool")
public class GetTermInfo
{
    public static void main(final String[] args) throws Exception {
        FSDirectory dir = null;
        String inputStr = null;
        String field = null;
        if (args.length == 3) {
            dir = FSDirectory.open(Paths.get(args[0], new String[0]));
            field = args[1];
            inputStr = args[2];
        }
        else {
            usage();
            System.exit(1);
        }
        getTermInfo((Directory)dir, new Term(field, inputStr));
    }
    
    public static void getTermInfo(final Directory dir, final Term term) throws Exception {
        final IndexReader reader = (IndexReader)DirectoryReader.open(dir);
        System.out.printf(Locale.ROOT, "%s:%s \t totalTF = %,d \t doc freq = %,d \n", term.field(), term.text(), reader.totalTermFreq(term), reader.docFreq(term));
    }
    
    private static void usage() {
        System.out.println("\n\nusage:\n\tjava " + GetTermInfo.class.getName() + " <index dir> field term \n\n");
    }
}
