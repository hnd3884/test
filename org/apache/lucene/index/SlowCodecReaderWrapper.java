package org.apache.lucene.index;

import java.util.Iterator;
import java.util.Collections;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import org.apache.lucene.util.Bits;
import java.io.IOException;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.codecs.NormsProducer;
import org.apache.lucene.codecs.StoredFieldsReader;
import org.apache.lucene.codecs.TermVectorsReader;

public final class SlowCodecReaderWrapper
{
    private SlowCodecReaderWrapper() {
    }
    
    public static CodecReader wrap(final LeafReader reader) throws IOException {
        if (reader instanceof CodecReader) {
            return (CodecReader)reader;
        }
        reader.checkIntegrity();
        return new CodecReader() {
            @Override
            public TermVectorsReader getTermVectorsReader() {
                reader.ensureOpen();
                return readerToTermVectorsReader(reader);
            }
            
            @Override
            public StoredFieldsReader getFieldsReader() {
                reader.ensureOpen();
                return readerToStoredFieldsReader(reader);
            }
            
            @Override
            public NormsProducer getNormsReader() {
                reader.ensureOpen();
                return readerToNormsProducer(reader);
            }
            
            @Override
            public DocValuesProducer getDocValuesReader() {
                reader.ensureOpen();
                return readerToDocValuesProducer(reader);
            }
            
            @Override
            public FieldsProducer getPostingsReader() {
                reader.ensureOpen();
                try {
                    return readerToFieldsProducer(reader);
                }
                catch (final IOException bogus) {
                    throw new AssertionError((Object)bogus);
                }
            }
            
            @Override
            public FieldInfos getFieldInfos() {
                return reader.getFieldInfos();
            }
            
            @Override
            public Bits getLiveDocs() {
                return reader.getLiveDocs();
            }
            
            @Override
            public int numDocs() {
                return reader.numDocs();
            }
            
            @Override
            public int maxDoc() {
                return reader.maxDoc();
            }
            
            @Override
            public void addCoreClosedListener(final CoreClosedListener listener) {
                reader.addCoreClosedListener(listener);
            }
            
            @Override
            public void removeCoreClosedListener(final CoreClosedListener listener) {
                reader.removeCoreClosedListener(listener);
            }
        };
    }
    
    private static NormsProducer readerToNormsProducer(final LeafReader reader) {
        return new NormsProducer() {
            @Override
            public NumericDocValues getNorms(final FieldInfo field) throws IOException {
                return reader.getNormValues(field.name);
            }
            
            @Override
            public void checkIntegrity() throws IOException {
            }
            
            @Override
            public void close() {
            }
            
            @Override
            public long ramBytesUsed() {
                return 0L;
            }
            
            @Override
            public Collection<Accountable> getChildResources() {
                return (Collection<Accountable>)Collections.emptyList();
            }
        };
    }
    
    private static DocValuesProducer readerToDocValuesProducer(final LeafReader reader) {
        return new DocValuesProducer() {
            @Override
            public NumericDocValues getNumeric(final FieldInfo field) throws IOException {
                return reader.getNumericDocValues(field.name);
            }
            
            @Override
            public BinaryDocValues getBinary(final FieldInfo field) throws IOException {
                return reader.getBinaryDocValues(field.name);
            }
            
            @Override
            public SortedDocValues getSorted(final FieldInfo field) throws IOException {
                return reader.getSortedDocValues(field.name);
            }
            
            @Override
            public SortedNumericDocValues getSortedNumeric(final FieldInfo field) throws IOException {
                return reader.getSortedNumericDocValues(field.name);
            }
            
            @Override
            public SortedSetDocValues getSortedSet(final FieldInfo field) throws IOException {
                return reader.getSortedSetDocValues(field.name);
            }
            
            @Override
            public Bits getDocsWithField(final FieldInfo field) throws IOException {
                return reader.getDocsWithField(field.name);
            }
            
            @Override
            public void checkIntegrity() throws IOException {
            }
            
            @Override
            public void close() {
            }
            
            @Override
            public long ramBytesUsed() {
                return 0L;
            }
            
            @Override
            public Collection<Accountable> getChildResources() {
                return (Collection<Accountable>)Collections.emptyList();
            }
        };
    }
    
    private static StoredFieldsReader readerToStoredFieldsReader(final LeafReader reader) {
        return new StoredFieldsReader() {
            @Override
            public void visitDocument(final int docID, final StoredFieldVisitor visitor) throws IOException {
                reader.document(docID, visitor);
            }
            
            @Override
            public StoredFieldsReader clone() {
                return readerToStoredFieldsReader(reader);
            }
            
            @Override
            public void checkIntegrity() throws IOException {
            }
            
            @Override
            public void close() {
            }
            
            @Override
            public long ramBytesUsed() {
                return 0L;
            }
            
            @Override
            public Collection<Accountable> getChildResources() {
                return (Collection<Accountable>)Collections.emptyList();
            }
        };
    }
    
    private static TermVectorsReader readerToTermVectorsReader(final LeafReader reader) {
        return new TermVectorsReader() {
            @Override
            public Fields get(final int docID) throws IOException {
                return reader.getTermVectors(docID);
            }
            
            @Override
            public TermVectorsReader clone() {
                return readerToTermVectorsReader(reader);
            }
            
            @Override
            public void checkIntegrity() throws IOException {
            }
            
            @Override
            public void close() {
            }
            
            @Override
            public long ramBytesUsed() {
                return 0L;
            }
            
            @Override
            public Collection<Accountable> getChildResources() {
                return (Collection<Accountable>)Collections.emptyList();
            }
        };
    }
    
    private static FieldsProducer readerToFieldsProducer(final LeafReader reader) throws IOException {
        final Fields fields = reader.fields();
        return new FieldsProducer() {
            @Override
            public Iterator<String> iterator() {
                return fields.iterator();
            }
            
            @Override
            public Terms terms(final String field) throws IOException {
                return fields.terms(field);
            }
            
            @Override
            public int size() {
                return fields.size();
            }
            
            @Override
            public void checkIntegrity() throws IOException {
            }
            
            @Override
            public void close() {
            }
            
            @Override
            public long ramBytesUsed() {
                return 0L;
            }
            
            @Override
            public Collection<Accountable> getChildResources() {
                return (Collection<Accountable>)Collections.emptyList();
            }
        };
    }
}
