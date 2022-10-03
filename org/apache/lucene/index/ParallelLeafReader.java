package org.apache.lucene.index;

import org.apache.lucene.util.Bits;
import java.util.Iterator;
import java.util.Collection;
import java.util.TreeMap;
import java.util.Map;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.io.IOException;
import java.util.SortedMap;
import java.util.Set;

public class ParallelLeafReader extends LeafReader
{
    private final FieldInfos fieldInfos;
    private final ParallelFields fields;
    private final LeafReader[] parallelReaders;
    private final LeafReader[] storedFieldsReaders;
    private final Set<LeafReader> completeReaderSet;
    private final boolean closeSubReaders;
    private final int maxDoc;
    private final int numDocs;
    private final boolean hasDeletions;
    private final SortedMap<String, LeafReader> fieldToReader;
    private final SortedMap<String, LeafReader> tvFieldToReader;
    
    public ParallelLeafReader(final LeafReader... readers) throws IOException {
        this(true, readers);
    }
    
    public ParallelLeafReader(final boolean closeSubReaders, final LeafReader... readers) throws IOException {
        this(closeSubReaders, readers, readers);
    }
    
    public ParallelLeafReader(final boolean closeSubReaders, final LeafReader[] readers, final LeafReader[] storedFieldsReaders) throws IOException {
        this.fields = new ParallelFields();
        this.completeReaderSet = Collections.newSetFromMap(new IdentityHashMap<LeafReader, Boolean>());
        this.fieldToReader = new TreeMap<String, LeafReader>();
        this.tvFieldToReader = new TreeMap<String, LeafReader>();
        this.closeSubReaders = closeSubReaders;
        if (readers.length == 0 && storedFieldsReaders.length > 0) {
            throw new IllegalArgumentException("There must be at least one main reader if storedFieldsReaders are used.");
        }
        this.parallelReaders = readers.clone();
        this.storedFieldsReaders = storedFieldsReaders.clone();
        if (this.parallelReaders.length > 0) {
            final LeafReader first = this.parallelReaders[0];
            this.maxDoc = first.maxDoc();
            this.numDocs = first.numDocs();
            this.hasDeletions = first.hasDeletions();
        }
        else {
            final int n = 0;
            this.numDocs = n;
            this.maxDoc = n;
            this.hasDeletions = false;
        }
        Collections.addAll(this.completeReaderSet, this.parallelReaders);
        Collections.addAll(this.completeReaderSet, this.storedFieldsReaders);
        for (final LeafReader reader : this.completeReaderSet) {
            if (reader.maxDoc() != this.maxDoc) {
                throw new IllegalArgumentException("All readers must have same maxDoc: " + this.maxDoc + "!=" + reader.maxDoc());
            }
        }
        final FieldInfos.Builder builder = new FieldInfos.Builder();
        for (final LeafReader reader2 : this.parallelReaders) {
            final FieldInfos readerFieldInfos = reader2.getFieldInfos();
            for (final FieldInfo fieldInfo : readerFieldInfos) {
                if (!this.fieldToReader.containsKey(fieldInfo.name)) {
                    builder.add(fieldInfo);
                    this.fieldToReader.put(fieldInfo.name, reader2);
                    if (!fieldInfo.hasVectors()) {
                        continue;
                    }
                    this.tvFieldToReader.put(fieldInfo.name, reader2);
                }
            }
        }
        this.fieldInfos = builder.finish();
        for (final LeafReader reader2 : this.parallelReaders) {
            final Fields readerFields = reader2.fields();
            for (final String field : readerFields) {
                if (this.fieldToReader.get(field) == reader2) {
                    this.fields.addField(field, readerFields.terms(field));
                }
            }
        }
        for (final LeafReader reader3 : this.completeReaderSet) {
            if (!closeSubReaders) {
                reader3.incRef();
            }
            reader3.registerParentReader(this);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder("ParallelLeafReader(");
        final Iterator<LeafReader> iter = this.completeReaderSet.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(", ");
            }
        }
        return buffer.append(')').toString();
    }
    
    @Override
    public void addCoreClosedListener(final CoreClosedListener listener) {
        LeafReader.addCoreClosedListenerAsReaderClosedListener(this, listener);
    }
    
    @Override
    public void removeCoreClosedListener(final CoreClosedListener listener) {
        LeafReader.removeCoreClosedListenerAsReaderClosedListener(this, listener);
    }
    
    @Override
    public FieldInfos getFieldInfos() {
        return this.fieldInfos;
    }
    
    @Override
    public Bits getLiveDocs() {
        this.ensureOpen();
        return this.hasDeletions ? this.parallelReaders[0].getLiveDocs() : null;
    }
    
    @Override
    public Fields fields() {
        this.ensureOpen();
        return this.fields;
    }
    
    @Override
    public int numDocs() {
        return this.numDocs;
    }
    
    @Override
    public int maxDoc() {
        return this.maxDoc;
    }
    
    @Override
    public void document(final int docID, final StoredFieldVisitor visitor) throws IOException {
        this.ensureOpen();
        for (final LeafReader reader : this.storedFieldsReaders) {
            reader.document(docID, visitor);
        }
    }
    
    @Override
    public Fields getTermVectors(final int docID) throws IOException {
        this.ensureOpen();
        ParallelFields fields = null;
        for (final Map.Entry<String, LeafReader> ent : this.tvFieldToReader.entrySet()) {
            final String fieldName = ent.getKey();
            final Terms vector = ent.getValue().getTermVector(docID, fieldName);
            if (vector != null) {
                if (fields == null) {
                    fields = new ParallelFields();
                }
                fields.addField(fieldName, vector);
            }
        }
        return fields;
    }
    
    @Override
    protected synchronized void doClose() throws IOException {
        IOException ioe = null;
        for (final LeafReader reader : this.completeReaderSet) {
            try {
                if (this.closeSubReaders) {
                    reader.close();
                }
                else {
                    reader.decRef();
                }
            }
            catch (final IOException e) {
                if (ioe != null) {
                    continue;
                }
                ioe = e;
            }
        }
        if (ioe != null) {
            throw ioe;
        }
    }
    
    @Override
    public NumericDocValues getNumericDocValues(final String field) throws IOException {
        this.ensureOpen();
        final LeafReader reader = this.fieldToReader.get(field);
        return (reader == null) ? null : reader.getNumericDocValues(field);
    }
    
    @Override
    public BinaryDocValues getBinaryDocValues(final String field) throws IOException {
        this.ensureOpen();
        final LeafReader reader = this.fieldToReader.get(field);
        return (reader == null) ? null : reader.getBinaryDocValues(field);
    }
    
    @Override
    public SortedDocValues getSortedDocValues(final String field) throws IOException {
        this.ensureOpen();
        final LeafReader reader = this.fieldToReader.get(field);
        return (reader == null) ? null : reader.getSortedDocValues(field);
    }
    
    @Override
    public SortedNumericDocValues getSortedNumericDocValues(final String field) throws IOException {
        this.ensureOpen();
        final LeafReader reader = this.fieldToReader.get(field);
        return (reader == null) ? null : reader.getSortedNumericDocValues(field);
    }
    
    @Override
    public SortedSetDocValues getSortedSetDocValues(final String field) throws IOException {
        this.ensureOpen();
        final LeafReader reader = this.fieldToReader.get(field);
        return (reader == null) ? null : reader.getSortedSetDocValues(field);
    }
    
    @Override
    public Bits getDocsWithField(final String field) throws IOException {
        this.ensureOpen();
        final LeafReader reader = this.fieldToReader.get(field);
        return (reader == null) ? null : reader.getDocsWithField(field);
    }
    
    @Override
    public NumericDocValues getNormValues(final String field) throws IOException {
        this.ensureOpen();
        final LeafReader reader = this.fieldToReader.get(field);
        final NumericDocValues values = (reader == null) ? null : reader.getNormValues(field);
        return values;
    }
    
    @Override
    public void checkIntegrity() throws IOException {
        this.ensureOpen();
        for (final LeafReader reader : this.completeReaderSet) {
            reader.checkIntegrity();
        }
    }
    
    public LeafReader[] getParallelReaders() {
        this.ensureOpen();
        return this.parallelReaders;
    }
    
    private final class ParallelFields extends Fields
    {
        final Map<String, Terms> fields;
        
        ParallelFields() {
            this.fields = new TreeMap<String, Terms>();
        }
        
        void addField(final String fieldName, final Terms terms) {
            this.fields.put(fieldName, terms);
        }
        
        @Override
        public Iterator<String> iterator() {
            return Collections.unmodifiableSet((Set<? extends String>)this.fields.keySet()).iterator();
        }
        
        @Override
        public Terms terms(final String field) {
            return this.fields.get(field);
        }
        
        @Override
        public int size() {
            return this.fields.size();
        }
    }
}
