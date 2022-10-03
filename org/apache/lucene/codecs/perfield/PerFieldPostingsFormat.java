package org.apache.lucene.codecs.perfield;

import org.apache.lucene.util.Accountables;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.index.Terms;
import java.util.Collections;
import org.apache.lucene.index.IndexOptions;
import java.util.IdentityHashMap;
import java.util.TreeMap;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.util.IOUtils;
import java.util.Iterator;
import org.apache.lucene.index.FilterLeafReader;
import java.util.Map;
import java.util.HashMap;
import org.apache.lucene.index.Fields;
import java.util.ArrayList;
import java.io.Closeable;
import java.util.List;
import java.util.TreeSet;
import java.util.Set;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.index.SegmentReadState;
import java.io.IOException;
import org.apache.lucene.codecs.FieldsConsumer;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.codecs.PostingsFormat;

public abstract class PerFieldPostingsFormat extends PostingsFormat
{
    public static final String PER_FIELD_NAME = "PerField40";
    public static final String PER_FIELD_FORMAT_KEY;
    public static final String PER_FIELD_SUFFIX_KEY;
    
    public PerFieldPostingsFormat() {
        super("PerField40");
    }
    
    static String getSuffix(final String formatName, final String suffix) {
        return formatName + "_" + suffix;
    }
    
    static String getFullSegmentSuffix(final String fieldName, final String outerSegmentSuffix, final String segmentSuffix) {
        if (outerSegmentSuffix.length() == 0) {
            return segmentSuffix;
        }
        throw new IllegalStateException("cannot embed PerFieldPostingsFormat inside itself (field \"" + fieldName + "\" returned PerFieldPostingsFormat)");
    }
    
    @Override
    public final FieldsConsumer fieldsConsumer(final SegmentWriteState state) throws IOException {
        return new FieldsWriter(state);
    }
    
    @Override
    public final FieldsProducer fieldsProducer(final SegmentReadState state) throws IOException {
        return new FieldsReader(state);
    }
    
    public abstract PostingsFormat getPostingsFormatForField(final String p0);
    
    static {
        PER_FIELD_FORMAT_KEY = PerFieldPostingsFormat.class.getSimpleName() + ".format";
        PER_FIELD_SUFFIX_KEY = PerFieldPostingsFormat.class.getSimpleName() + ".suffix";
    }
    
    static class FieldsGroup
    {
        final Set<String> fields;
        int suffix;
        SegmentWriteState state;
        
        FieldsGroup() {
            this.fields = new TreeSet<String>();
        }
    }
    
    private class FieldsWriter extends FieldsConsumer
    {
        final SegmentWriteState writeState;
        final List<Closeable> toClose;
        
        public FieldsWriter(final SegmentWriteState writeState) {
            this.toClose = new ArrayList<Closeable>();
            this.writeState = writeState;
        }
        
        @Override
        public void write(final Fields fields) throws IOException {
            final Map<PostingsFormat, FieldsGroup> formatToGroups = new HashMap<PostingsFormat, FieldsGroup>();
            final Map<String, Integer> suffixes = new HashMap<String, Integer>();
            for (final String field : fields) {
                final FieldInfo fieldInfo = this.writeState.fieldInfos.fieldInfo(field);
                final PostingsFormat format = PerFieldPostingsFormat.this.getPostingsFormatForField(field);
                if (format == null) {
                    throw new IllegalStateException("invalid null PostingsFormat for field=\"" + field + "\"");
                }
                final String formatName = format.getName();
                FieldsGroup group = formatToGroups.get(format);
                if (group == null) {
                    Integer suffix = suffixes.get(formatName);
                    if (suffix == null) {
                        suffix = 0;
                    }
                    else {
                        ++suffix;
                    }
                    suffixes.put(formatName, suffix);
                    final String segmentSuffix = PerFieldPostingsFormat.getFullSegmentSuffix(field, this.writeState.segmentSuffix, PerFieldPostingsFormat.getSuffix(formatName, Integer.toString(suffix)));
                    group = new FieldsGroup();
                    group.state = new SegmentWriteState(this.writeState, segmentSuffix);
                    group.suffix = suffix;
                    formatToGroups.put(format, group);
                }
                else if (!suffixes.containsKey(formatName)) {
                    throw new IllegalStateException("no suffix for format name: " + formatName + ", expected: " + group.suffix);
                }
                group.fields.add(field);
                String previousValue = fieldInfo.putAttribute(PerFieldPostingsFormat.PER_FIELD_FORMAT_KEY, formatName);
                if (previousValue != null) {
                    throw new IllegalStateException("found existing value for " + PerFieldPostingsFormat.PER_FIELD_FORMAT_KEY + ", field=" + fieldInfo.name + ", old=" + previousValue + ", new=" + formatName);
                }
                previousValue = fieldInfo.putAttribute(PerFieldPostingsFormat.PER_FIELD_SUFFIX_KEY, Integer.toString(group.suffix));
                if (previousValue != null) {
                    throw new IllegalStateException("found existing value for " + PerFieldPostingsFormat.PER_FIELD_SUFFIX_KEY + ", field=" + fieldInfo.name + ", old=" + previousValue + ", new=" + group.suffix);
                }
            }
            boolean success = false;
            try {
                for (final Map.Entry<PostingsFormat, FieldsGroup> ent : formatToGroups.entrySet()) {
                    final PostingsFormat format = ent.getKey();
                    final FieldsGroup group2 = ent.getValue();
                    final Fields maskedFields = new FilterLeafReader.FilterFields(fields) {
                        @Override
                        public Iterator<String> iterator() {
                            return group2.fields.iterator();
                        }
                    };
                    final FieldsConsumer consumer = format.fieldsConsumer(group2.state);
                    this.toClose.add(consumer);
                    consumer.write(maskedFields);
                }
                success = true;
            }
            finally {
                if (!success) {
                    IOUtils.closeWhileHandlingException(this.toClose);
                }
            }
        }
        
        @Override
        public void close() throws IOException {
            IOUtils.close(this.toClose);
        }
    }
    
    private static class FieldsReader extends FieldsProducer
    {
        private static final long BASE_RAM_BYTES_USED;
        private final Map<String, FieldsProducer> fields;
        private final Map<String, FieldsProducer> formats;
        private final String segment;
        
        FieldsReader(final FieldsReader other) throws IOException {
            this.fields = new TreeMap<String, FieldsProducer>();
            this.formats = new HashMap<String, FieldsProducer>();
            final Map<FieldsProducer, FieldsProducer> oldToNew = new IdentityHashMap<FieldsProducer, FieldsProducer>();
            for (final Map.Entry<String, FieldsProducer> ent : other.formats.entrySet()) {
                final FieldsProducer values = ent.getValue().getMergeInstance();
                this.formats.put(ent.getKey(), values);
                oldToNew.put(ent.getValue(), values);
            }
            for (final Map.Entry<String, FieldsProducer> ent : other.fields.entrySet()) {
                final FieldsProducer producer = oldToNew.get(ent.getValue());
                assert producer != null;
                this.fields.put(ent.getKey(), producer);
            }
            this.segment = other.segment;
        }
        
        public FieldsReader(final SegmentReadState readState) throws IOException {
            this.fields = new TreeMap<String, FieldsProducer>();
            this.formats = new HashMap<String, FieldsProducer>();
            boolean success = false;
            try {
                for (final FieldInfo fi : readState.fieldInfos) {
                    if (fi.getIndexOptions() != IndexOptions.NONE) {
                        final String fieldName = fi.name;
                        final String formatName = fi.getAttribute(PerFieldPostingsFormat.PER_FIELD_FORMAT_KEY);
                        if (formatName == null) {
                            continue;
                        }
                        final String suffix = fi.getAttribute(PerFieldPostingsFormat.PER_FIELD_SUFFIX_KEY);
                        if (suffix == null) {
                            throw new IllegalStateException("missing attribute: " + PerFieldPostingsFormat.PER_FIELD_SUFFIX_KEY + " for field: " + fieldName);
                        }
                        final PostingsFormat format = PostingsFormat.forName(formatName);
                        final String segmentSuffix = PerFieldPostingsFormat.getSuffix(formatName, suffix);
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
            this.segment = readState.segmentInfo.name;
        }
        
        @Override
        public Iterator<String> iterator() {
            return Collections.unmodifiableSet((Set<? extends String>)this.fields.keySet()).iterator();
        }
        
        @Override
        public Terms terms(final String field) throws IOException {
            final FieldsProducer fieldsProducer = this.fields.get(field);
            return (fieldsProducer == null) ? null : fieldsProducer.terms(field);
        }
        
        @Override
        public int size() {
            return this.fields.size();
        }
        
        @Override
        public void close() throws IOException {
            IOUtils.close(this.formats.values());
        }
        
        @Override
        public long ramBytesUsed() {
            long ramBytesUsed = FieldsReader.BASE_RAM_BYTES_USED;
            ramBytesUsed += this.fields.size() * 2L * RamUsageEstimator.NUM_BYTES_OBJECT_REF;
            ramBytesUsed += this.formats.size() * 2L * RamUsageEstimator.NUM_BYTES_OBJECT_REF;
            for (final Map.Entry<String, FieldsProducer> entry : this.formats.entrySet()) {
                ramBytesUsed += entry.getValue().ramBytesUsed();
            }
            return ramBytesUsed;
        }
        
        @Override
        public Collection<Accountable> getChildResources() {
            return Accountables.namedAccountables("format", this.formats);
        }
        
        @Override
        public void checkIntegrity() throws IOException {
            for (final FieldsProducer producer : this.formats.values()) {
                producer.checkIntegrity();
            }
        }
        
        @Override
        public FieldsProducer getMergeInstance() throws IOException {
            return new FieldsReader(this);
        }
        
        @Override
        public String toString() {
            return "PerFieldPostings(segment=" + this.segment + " formats=" + this.formats.size() + ")";
        }
        
        static {
            BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(FieldsReader.class);
        }
    }
}
