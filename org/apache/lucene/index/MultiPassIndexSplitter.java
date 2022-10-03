package org.apache.lucene.index;

import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;
import java.util.Iterator;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import java.io.IOException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.SuppressForbidden;

@SuppressForbidden(reason = "System.out required: command line tool")
public class MultiPassIndexSplitter
{
    public void split(final IndexReader in, final Directory[] outputs, final boolean seq) throws IOException {
        if (outputs == null || outputs.length < 2) {
            throw new IOException("Invalid number of outputs.");
        }
        if (in == null || in.numDocs() < 2) {
            throw new IOException("Not enough documents for splitting");
        }
        final int numParts = outputs.length;
        final FakeDeleteIndexReader input = new FakeDeleteIndexReader(in);
        final int maxDoc = input.maxDoc();
        final int partLen = maxDoc / numParts;
        for (int i = 0; i < numParts; ++i) {
            input.undeleteAll();
            if (seq) {
                final int lo = partLen * i;
                final int hi = lo + partLen;
                for (int j = 0; j < lo; ++j) {
                    input.deleteDocument(j);
                }
                if (i < numParts - 1) {
                    for (int j = hi; j < maxDoc; ++j) {
                        input.deleteDocument(j);
                    }
                }
            }
            else {
                for (int k = 0; k < maxDoc; ++k) {
                    if ((k + numParts - i) % numParts != 0) {
                        input.deleteDocument(k);
                    }
                }
            }
            final IndexWriter w = new IndexWriter(outputs[i], new IndexWriterConfig((Analyzer)null).setOpenMode(IndexWriterConfig.OpenMode.CREATE));
            System.err.println("Writing part " + (i + 1) + " ...");
            final List<? extends FakeDeleteLeafIndexReader> sr = input.getSequentialSubReaders();
            w.addIndexes((CodecReader[])sr.toArray(new CodecReader[sr.size()]));
            w.close();
        }
        System.err.println("Done.");
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length < 5) {
            System.err.println("Usage: MultiPassIndexSplitter -out <outputDir> -num <numParts> [-seq] <inputIndex1> [<inputIndex2 ...]");
            System.err.println("\tinputIndex\tpath to input index, multiple values are ok");
            System.err.println("\t-out ouputDir\tpath to output directory to contain partial indexes");
            System.err.println("\t-num numParts\tnumber of parts to produce");
            System.err.println("\t-seq\tsequential docid-range split (default is round-robin)");
            System.exit(-1);
        }
        final ArrayList<IndexReader> indexes = new ArrayList<IndexReader>();
        String outDir = null;
        int numParts = -1;
        boolean seq = false;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-out")) {
                outDir = args[++i];
            }
            else if (args[i].equals("-num")) {
                numParts = Integer.parseInt(args[++i]);
            }
            else if (args[i].equals("-seq")) {
                seq = true;
            }
            else {
                final Path file = Paths.get(args[i], new String[0]);
                if (!Files.isDirectory(file, new LinkOption[0])) {
                    System.err.println("Invalid input path - skipping: " + file);
                }
                else {
                    final Directory dir = (Directory)FSDirectory.open(file);
                    try {
                        if (!DirectoryReader.indexExists(dir)) {
                            System.err.println("Invalid input index - skipping: " + file);
                            continue;
                        }
                    }
                    catch (final Exception e) {
                        System.err.println("Invalid input index - skipping: " + file);
                        continue;
                    }
                    indexes.add((IndexReader)DirectoryReader.open(dir));
                }
            }
        }
        if (outDir == null) {
            throw new Exception("Required argument missing: -out outputDir");
        }
        if (numParts < 2) {
            throw new Exception("Invalid value of required argument: -num numParts");
        }
        if (indexes.size() == 0) {
            throw new Exception("No input indexes to process");
        }
        final Path out = Paths.get(outDir, new String[0]);
        Files.createDirectories(out, (FileAttribute<?>[])new FileAttribute[0]);
        final Directory[] dirs = new Directory[numParts];
        for (int j = 0; j < numParts; ++j) {
            dirs[j] = (Directory)FSDirectory.open(out.resolve("part-" + j));
        }
        final MultiPassIndexSplitter splitter = new MultiPassIndexSplitter();
        IndexReader input;
        if (indexes.size() == 1) {
            input = indexes.get(0);
        }
        else {
            input = (IndexReader)new MultiReader((IndexReader[])indexes.toArray(new IndexReader[indexes.size()]));
        }
        splitter.split(input, dirs, seq);
    }
    
    private static final class FakeDeleteIndexReader extends BaseCompositeReader<FakeDeleteLeafIndexReader>
    {
        public FakeDeleteIndexReader(final IndexReader reader) throws IOException {
            super((IndexReader[])initSubReaders(reader));
        }
        
        private static FakeDeleteLeafIndexReader[] initSubReaders(final IndexReader reader) throws IOException {
            final List<LeafReaderContext> leaves = reader.leaves();
            final FakeDeleteLeafIndexReader[] subs = new FakeDeleteLeafIndexReader[leaves.size()];
            int i = 0;
            for (final LeafReaderContext ctx : leaves) {
                subs[i++] = new FakeDeleteLeafIndexReader(SlowCodecReaderWrapper.wrap(ctx.reader()));
            }
            return subs;
        }
        
        public void deleteDocument(final int docID) {
            final int i = this.readerIndex(docID);
            this.getSequentialSubReaders().get(i).deleteDocument(docID - this.readerBase(i));
        }
        
        public void undeleteAll() {
            for (final FakeDeleteLeafIndexReader r : this.getSequentialSubReaders()) {
                r.undeleteAll();
            }
        }
        
        protected void doClose() {
        }
    }
    
    private static final class FakeDeleteLeafIndexReader extends FilterCodecReader
    {
        FixedBitSet liveDocs;
        
        public FakeDeleteLeafIndexReader(final CodecReader reader) {
            super(reader);
            this.undeleteAll();
        }
        
        public int numDocs() {
            return this.liveDocs.cardinality();
        }
        
        public void undeleteAll() {
            final int maxDoc = this.in.maxDoc();
            this.liveDocs = new FixedBitSet(this.in.maxDoc());
            if (this.in.hasDeletions()) {
                final Bits oldLiveDocs = this.in.getLiveDocs();
                assert oldLiveDocs != null;
                for (int i = 0; i < maxDoc; ++i) {
                    if (oldLiveDocs.get(i)) {
                        this.liveDocs.set(i);
                    }
                }
            }
            else {
                this.liveDocs.set(0, maxDoc);
            }
        }
        
        public void deleteDocument(final int n) {
            this.liveDocs.clear(n);
        }
        
        public Bits getLiveDocs() {
            return (Bits)this.liveDocs;
        }
    }
}
