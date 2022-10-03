package org.apache.lucene.codecs.lucene53;

import java.util.Iterator;
import org.apache.lucene.index.FieldInfo;
import java.io.IOException;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.codecs.NormsConsumer;

class Lucene53NormsConsumer extends NormsConsumer
{
    IndexOutput data;
    IndexOutput meta;
    final int maxDoc;
    
    Lucene53NormsConsumer(final SegmentWriteState state, final String dataCodec, final String dataExtension, final String metaCodec, final String metaExtension) throws IOException {
        boolean success = false;
        try {
            final String dataName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, dataExtension);
            CodecUtil.writeIndexHeader(this.data = state.directory.createOutput(dataName, state.context), dataCodec, 0, state.segmentInfo.getId(), state.segmentSuffix);
            final String metaName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, metaExtension);
            CodecUtil.writeIndexHeader(this.meta = state.directory.createOutput(metaName, state.context), metaCodec, 0, state.segmentInfo.getId(), state.segmentSuffix);
            this.maxDoc = state.segmentInfo.maxDoc();
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(this);
            }
        }
    }
    
    @Override
    public void addNormsField(final FieldInfo field, final Iterable<Number> values) throws IOException {
        this.meta.writeVInt(field.number);
        long minValue = Long.MAX_VALUE;
        long maxValue = Long.MIN_VALUE;
        int count = 0;
        for (final Number nv : values) {
            if (nv == null) {
                throw new IllegalStateException("illegal norms data for field " + field.name + ", got null for value: " + count);
            }
            final long v = nv.longValue();
            minValue = Math.min(minValue, v);
            maxValue = Math.max(maxValue, v);
            ++count;
        }
        if (count != this.maxDoc) {
            throw new IllegalStateException("illegal norms data for field " + field.name + ", expected count=" + this.maxDoc + ", got=" + count);
        }
        if (minValue == maxValue) {
            this.addConstant(minValue);
        }
        else if (minValue >= -128L && maxValue <= 127L) {
            this.addByte1(values);
        }
        else if (minValue >= -32768L && maxValue <= 32767L) {
            this.addByte2(values);
        }
        else if (minValue >= -2147483648L && maxValue <= 2147483647L) {
            this.addByte4(values);
        }
        else {
            this.addByte8(values);
        }
    }
    
    private void addConstant(final long constant) throws IOException {
        this.meta.writeByte((byte)0);
        this.meta.writeLong(constant);
    }
    
    private void addByte1(final Iterable<Number> values) throws IOException {
        this.meta.writeByte((byte)1);
        this.meta.writeLong(this.data.getFilePointer());
        for (final Number value : values) {
            this.data.writeByte(value.byteValue());
        }
    }
    
    private void addByte2(final Iterable<Number> values) throws IOException {
        this.meta.writeByte((byte)2);
        this.meta.writeLong(this.data.getFilePointer());
        for (final Number value : values) {
            this.data.writeShort(value.shortValue());
        }
    }
    
    private void addByte4(final Iterable<Number> values) throws IOException {
        this.meta.writeByte((byte)4);
        this.meta.writeLong(this.data.getFilePointer());
        for (final Number value : values) {
            this.data.writeInt(value.intValue());
        }
    }
    
    private void addByte8(final Iterable<Number> values) throws IOException {
        this.meta.writeByte((byte)8);
        this.meta.writeLong(this.data.getFilePointer());
        for (final Number value : values) {
            this.data.writeLong(value.longValue());
        }
    }
    
    @Override
    public void close() throws IOException {
        boolean success = false;
        try {
            if (this.meta != null) {
                this.meta.writeVInt(-1);
                CodecUtil.writeFooter(this.meta);
            }
            if (this.data != null) {
                CodecUtil.writeFooter(this.data);
            }
            success = true;
        }
        finally {
            if (success) {
                IOUtils.close(this.data, this.meta);
            }
            else {
                IOUtils.closeWhileHandlingException(this.data, this.meta);
            }
            final IndexOutput indexOutput = null;
            this.data = indexOutput;
            this.meta = indexOutput;
        }
    }
}
