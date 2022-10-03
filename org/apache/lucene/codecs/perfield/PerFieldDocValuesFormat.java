package org.apache.lucene.codecs.perfield;

import org.apache.lucene.util.Accountables;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.DocValuesType;
import java.util.Iterator;
import java.util.IdentityHashMap;
import java.util.TreeMap;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.FieldInfo;
import java.util.HashMap;
import java.util.Map;
import java.io.Closeable;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.index.SegmentReadState;
import java.io.IOException;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.codecs.DocValuesFormat;

public abstract class PerFieldDocValuesFormat extends DocValuesFormat
{
    public static final String PER_FIELD_NAME = "PerFieldDV40";
    public static final String PER_FIELD_FORMAT_KEY;
    public static final String PER_FIELD_SUFFIX_KEY;
    
    public PerFieldDocValuesFormat() {
        super("PerFieldDV40");
    }
    
    @Override
    public final DocValuesConsumer fieldsConsumer(final SegmentWriteState state) throws IOException {
        return new FieldsWriter(state);
    }
    
    static String getSuffix(final String formatName, final String suffix) {
        return formatName + "_" + suffix;
    }
    
    static String getFullSegmentSuffix(final String outerSegmentSuffix, final String segmentSuffix) {
        if (outerSegmentSuffix.length() == 0) {
            return segmentSuffix;
        }
        return outerSegmentSuffix + "_" + segmentSuffix;
    }
    
    @Override
    public final DocValuesProducer fieldsProducer(final SegmentReadState state) throws IOException {
        return new FieldsReader(state);
    }
    
    public abstract DocValuesFormat getDocValuesFormatForField(final String p0);
    
    static {
        PER_FIELD_FORMAT_KEY = PerFieldDocValuesFormat.class.getSimpleName() + ".format";
        PER_FIELD_SUFFIX_KEY = PerFieldDocValuesFormat.class.getSimpleName() + ".suffix";
    }
    
    static class ConsumerAndSuffix implements Closeable
    {
        DocValuesConsumer consumer;
        int suffix;
        
        @Override
        public void close() throws IOException {
            this.consumer.close();
        }
    }
    
    private class FieldsWriter extends DocValuesConsumer
    {
        private final Map<DocValuesFormat, ConsumerAndSuffix> formats;
        private final Map<String, Integer> suffixes;
        private final SegmentWriteState segmentWriteState;
        
        public FieldsWriter(final SegmentWriteState state) {
            this.formats = new HashMap<DocValuesFormat, ConsumerAndSuffix>();
            this.suffixes = new HashMap<String, Integer>();
            this.segmentWriteState = state;
        }
        
        @Override
        public void addNumericField(final FieldInfo field, final Iterable<Number> values) throws IOException {
            this.getInstance(field).addNumericField(field, values);
        }
        
        @Override
        public void addBinaryField(final FieldInfo field, final Iterable<BytesRef> values) throws IOException {
            this.getInstance(field).addBinaryField(field, values);
        }
        
        @Override
        public void addSortedField(final FieldInfo field, final Iterable<BytesRef> values, final Iterable<Number> docToOrd) throws IOException {
            this.getInstance(field).addSortedField(field, values, docToOrd);
        }
        
        @Override
        public void addSortedNumericField(final FieldInfo field, final Iterable<Number> docToValueCount, final Iterable<Number> values) throws IOException {
            this.getInstance(field).addSortedNumericField(field, docToValueCount, values);
        }
        
        @Override
        public void addSortedSetField(final FieldInfo field, final Iterable<BytesRef> values, final Iterable<Number> docToOrdCount, final Iterable<Number> ords) throws IOException {
            this.getInstance(field).addSortedSetField(field, values, docToOrdCount, ords);
        }
        
        private DocValuesConsumer getInstance(final FieldInfo field) throws IOException {
            DocValuesFormat format = null;
            if (field.getDocValuesGen() != -1L) {
                final String formatName = field.getAttribute(PerFieldDocValuesFormat.PER_FIELD_FORMAT_KEY);
                if (formatName != null) {
                    format = DocValuesFormat.forName(formatName);
                }
            }
            if (format == null) {
                format = PerFieldDocValuesFormat.this.getDocValuesFormatForField(field.name);
            }
            if (format == null) {
                throw new IllegalStateException("invalid null DocValuesFormat for field=\"" + field.name + "\"");
            }
            final String formatName = format.getName();
            String previousValue = field.putAttribute(PerFieldDocValuesFormat.PER_FIELD_FORMAT_KEY, formatName);
            if (field.getDocValuesGen() == -1L && previousValue != null) {
                throw new IllegalStateException("found existing value for " + PerFieldDocValuesFormat.PER_FIELD_FORMAT_KEY + ", field=" + field.name + ", old=" + previousValue + ", new=" + formatName);
            }
            Integer suffix = null;
            ConsumerAndSuffix consumer = this.formats.get(format);
            if (consumer == null) {
                if (field.getDocValuesGen() != -1L) {
                    final String suffixAtt = field.getAttribute(PerFieldDocValuesFormat.PER_FIELD_SUFFIX_KEY);
                    if (suffixAtt != null) {
                        suffix = Integer.valueOf(suffixAtt);
                    }
                }
                if (suffix == null) {
                    suffix = this.suffixes.get(formatName);
                    if (suffix == null) {
                        suffix = 0;
                    }
                    else {
                        ++suffix;
                    }
                }
                this.suffixes.put(formatName, suffix);
                final String segmentSuffix = PerFieldDocValuesFormat.getFullSegmentSuffix(this.segmentWriteState.segmentSuffix, PerFieldDocValuesFormat.getSuffix(formatName, Integer.toString(suffix)));
                consumer = new ConsumerAndSuffix();
                consumer.consumer = format.fieldsConsumer(new SegmentWriteState(this.segmentWriteState, segmentSuffix));
                consumer.suffix = suffix;
                this.formats.put(format, consumer);
            }
            else {
                assert this.suffixes.containsKey(formatName);
                suffix = consumer.suffix;
            }
            previousValue = field.putAttribute(PerFieldDocValuesFormat.PER_FIELD_SUFFIX_KEY, Integer.toString(suffix));
            if (field.getDocValuesGen() == -1L && previousValue != null) {
                throw new IllegalStateException("found existing value for " + PerFieldDocValuesFormat.PER_FIELD_SUFFIX_KEY + ", field=" + field.name + ", old=" + previousValue + ", new=" + suffix);
            }
            return consumer.consumer;
        }
        
        @Override
        public void close() throws IOException {
            IOUtils.close(this.formats.values());
        }
    }
    
    private class FieldsReader extends DocValuesProducer
    {
        private final Map<String, DocValuesProducer> fields;
        private final Map<String, DocValuesProducer> formats;
        
        FieldsReader(final FieldsReader other) throws IOException {
            this.fields = new TreeMap<String, DocValuesProducer>();
            this.formats = new HashMap<String, DocValuesProducer>();
            final Map<DocValuesProducer, DocValuesProducer> oldToNew = new IdentityHashMap<DocValuesProducer, DocValuesProducer>();
            for (final Map.Entry<String, DocValuesProducer> ent : other.formats.entrySet()) {
                final DocValuesProducer values = ent.getValue().getMergeInstance();
                this.formats.put(ent.getKey(), values);
                oldToNew.put(ent.getValue(), values);
            }
            for (final Map.Entry<String, DocValuesProducer> ent : other.fields.entrySet()) {
                final DocValuesProducer producer = oldToNew.get(ent.getValue());
                assert producer != null;
                this.fields.put(ent.getKey(), producer);
            }
        }
        
        public FieldsReader(final SegmentReadState readState) throws IOException {
            this.fields = new TreeMap<String, DocValuesProducer>();
            this.formats = new HashMap<String, DocValuesProducer>();
            boolean success = false;
            try {
                for (final FieldInfo fi : readState.fieldInfos) {
                    if (fi.getDocValuesType() != DocValuesType.NONE) {
                        final String fieldName = fi.name;
                        final String formatName = fi.getAttribute(PerFieldDocValuesFormat.PER_FIELD_FORMAT_KEY);
                        if (formatName == null) {
                            continue;
                        }
                        final String suffix = fi.getAttribute(PerFieldDocValuesFormat.PER_FIELD_SUFFIX_KEY);
                        if (suffix == null) {
                            throw new IllegalStateException("missing attribute: " + PerFieldDocValuesFormat.PER_FIELD_SUFFIX_KEY + " for field: " + fieldName);
                        }
                        final DocValuesFormat format = DocValuesFormat.forName(formatName);
                        final String segmentSuffix = PerFieldDocValuesFormat.getFullSegmentSuffix(readState.segmentSuffix, PerFieldDocValuesFormat.getSuffix(formatName, suffix));
                        if (!this.formats.containsKey(segmentSuffix)) {
                            this.formats.put(segmentSuffix, format.fieldsProducer(new SegmentReadState(readState, segmentSuffix)));
                        }
                        this.fields.put(fieldName, this.formats.get(segmentSuffix));
                    }
                }
                success = true;
            }
            finally {
                if (!success) {
                    IOUtils.closeWhileHandlingException(this.formats.values());
                }
            }
        }
        
        @Override
        public NumericDocValues getNumeric(final FieldInfo field) throws IOException {
            final DocValuesProducer producer = this.fields.get(field.name);
            return (producer == null) ? null : producer.getNumeric(field);
        }
        
        @Override
        public BinaryDocValues getBinary(final FieldInfo field) throws IOException {
            final DocValuesProducer producer = this.fields.get(field.name);
            return (producer == null) ? null : producer.getBinary(field);
        }
        
        @Override
        public SortedDocValues getSorted(final FieldInfo field) throws IOException {
            final DocValuesProducer producer = this.fields.get(field.name);
            return (producer == null) ? null : producer.getSorted(field);
        }
        
        @Override
        public SortedNumericDocValues getSortedNumeric(final FieldInfo field) throws IOException {
            final DocValuesProducer producer = this.fields.get(field.name);
            return (producer == null) ? null : producer.getSortedNumeric(field);
        }
        
        @Override
        public SortedSetDocValues getSortedSet(final FieldInfo field) throws IOException {
            final DocValuesProducer producer = this.fields.get(field.name);
            return (producer == null) ? null : producer.getSortedSet(field);
        }
        
        @Override
        public Bits getDocsWithField(final FieldInfo field) throws IOException {
            final DocValuesProducer producer = this.fields.get(field.name);
            return (producer == null) ? null : producer.getDocsWithField(field);
        }
        
        @Override
        public void close() throws IOException {
            IOUtils.close(this.formats.values());
        }
        
        @Override
        public long ramBytesUsed() {
            long size = 0L;
            for (final Map.Entry<String, DocValuesProducer> entry : this.formats.entrySet()) {
                size += entry.getKey().length() * 2 + entry.getValue().ramBytesUsed();
            }
            return size;
        }
        
        @Override
        public Collection<Accountable> getChildResources() {
            return Accountables.namedAccountables("format", this.formats);
        }
        
        @Override
        public void checkIntegrity() throws IOException {
            for (final DocValuesProducer format : this.formats.values()) {
                format.checkIntegrity();
            }
        }
        
        @Override
        public DocValuesProducer getMergeInstance() throws IOException {
            return new FieldsReader(this);
        }
        
        @Override
        public String toString() {
            return "PerFieldDocValues(formats=" + this.formats.size() + ")";
        }
    }
}
