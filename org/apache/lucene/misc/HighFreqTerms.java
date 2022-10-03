package org.apache.lucene.misc;

import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.PriorityQueue;
import java.util.Iterator;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.util.SuppressForbidden;
import org.apache.lucene.index.IndexReader;
import java.util.Comparator;
import org.apache.lucene.store.Directory;
import java.util.Locale;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.Paths;

public class HighFreqTerms
{
    public static final int DEFAULT_NUMTERMS = 100;
    
    @SuppressForbidden(reason = "System.out required: command line tool")
    public static void main(final String[] args) throws Exception {
        String field = null;
        int numTerms = 100;
        if (args.length == 0 || args.length > 4) {
            usage();
            System.exit(1);
        }
        final Directory dir = (Directory)FSDirectory.open(Paths.get(args[0], new String[0]));
        Comparator<TermStats> comparator = new DocFreqComparator();
        for (int i = 1; i < args.length; ++i) {
            if (args[i].equals("-t")) {
                comparator = new TotalTermFreqComparator();
            }
            else {
                try {
                    numTerms = Integer.parseInt(args[i]);
                }
                catch (final NumberFormatException e) {
                    field = args[i];
                }
            }
        }
        final IndexReader reader = (IndexReader)DirectoryReader.open(dir);
        final TermStats[] terms = getHighFreqTerms(reader, numTerms, field, comparator);
        for (int j = 0; j < terms.length; ++j) {
            System.out.printf(Locale.ROOT, "%s:%s \t totalTF = %,d \t docFreq = %,d \n", terms[j].field, terms[j].termtext.utf8ToString(), terms[j].totalTermFreq, terms[j].docFreq);
        }
        reader.close();
    }
    
    @SuppressForbidden(reason = "System.out required: command line tool")
    private static void usage() {
        System.out.println("\n\njava org.apache.lucene.misc.HighFreqTerms <index dir> [-t] [number_terms] [field]\n\t -t: order by totalTermFreq\n\n");
    }
    
    public static TermStats[] getHighFreqTerms(final IndexReader reader, final int numTerms, final String field, final Comparator<TermStats> comparator) throws Exception {
        TermStatsQueue tiq = null;
        if (field != null) {
            final Terms terms = MultiFields.getTerms(reader, field);
            if (terms == null) {
                throw new RuntimeException("field " + field + " not found");
            }
            final TermsEnum termsEnum = terms.iterator();
            tiq = new TermStatsQueue(numTerms, comparator);
            tiq.fill(field, termsEnum);
        }
        else {
            final Fields fields = MultiFields.getFields(reader);
            if (fields.size() == 0) {
                throw new RuntimeException("no fields found for this index");
            }
            tiq = new TermStatsQueue(numTerms, comparator);
            for (final String fieldName : fields) {
                final Terms terms2 = fields.terms(fieldName);
                if (terms2 != null) {
                    tiq.fill(fieldName, terms2.iterator());
                }
            }
        }
        final TermStats[] result = new TermStats[tiq.size()];
        int count = tiq.size() - 1;
        while (tiq.size() != 0) {
            result[count] = (TermStats)tiq.pop();
            --count;
        }
        return result;
    }
    
    public static final class DocFreqComparator implements Comparator<TermStats>
    {
        @Override
        public int compare(final TermStats a, final TermStats b) {
            int res = Long.compare(a.docFreq, b.docFreq);
            if (res == 0) {
                res = a.field.compareTo(b.field);
                if (res == 0) {
                    res = a.termtext.compareTo(b.termtext);
                }
            }
            return res;
        }
    }
    
    public static final class TotalTermFreqComparator implements Comparator<TermStats>
    {
        @Override
        public int compare(final TermStats a, final TermStats b) {
            int res = Long.compare(a.totalTermFreq, b.totalTermFreq);
            if (res == 0) {
                res = a.field.compareTo(b.field);
                if (res == 0) {
                    res = a.termtext.compareTo(b.termtext);
                }
            }
            return res;
        }
    }
    
    static final class TermStatsQueue extends PriorityQueue<TermStats>
    {
        final Comparator<TermStats> comparator;
        
        TermStatsQueue(final int size, final Comparator<TermStats> comparator) {
            super(size);
            this.comparator = comparator;
        }
        
        protected boolean lessThan(final TermStats termInfoA, final TermStats termInfoB) {
            return this.comparator.compare(termInfoA, termInfoB) < 0;
        }
        
        protected void fill(final String field, final TermsEnum termsEnum) throws IOException {
            BytesRef term = null;
            while ((term = termsEnum.next()) != null) {
                this.insertWithOverflow((Object)new TermStats(field, term, termsEnum.docFreq(), termsEnum.totalTermFreq()));
            }
        }
    }
}
