package org.apache.lucene.index;

import org.apache.lucene.codecs.FieldsConsumer;
import org.apache.lucene.codecs.TermVectorsWriter;
import org.apache.lucene.codecs.StoredFieldsWriter;
import java.util.Iterator;
import org.apache.lucene.codecs.NormsConsumer;
import org.apache.lucene.codecs.DocValuesConsumer;
import java.io.IOException;
import org.apache.lucene.util.InfoStream;
import java.util.List;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.store.Directory;

final class SegmentMerger
{
    private final Directory directory;
    private final Codec codec;
    private final IOContext context;
    final MergeState mergeState;
    private final FieldInfos.Builder fieldInfosBuilder;
    
    SegmentMerger(final List<CodecReader> readers, final SegmentInfo segmentInfo, final InfoStream infoStream, final Directory dir, final FieldInfos.FieldNumbers fieldNumbers, final IOContext context) throws IOException {
        if (context.context != IOContext.Context.MERGE) {
            throw new IllegalArgumentException("IOContext.context should be MERGE; got: " + context.context);
        }
        this.mergeState = new MergeState(readers, segmentInfo, infoStream);
        this.directory = dir;
        this.codec = segmentInfo.getCodec();
        this.context = context;
        this.fieldInfosBuilder = new FieldInfos.Builder(fieldNumbers);
    }
    
    boolean shouldMerge() {
        return this.mergeState.segmentInfo.maxDoc() > 0;
    }
    
    MergeState merge() throws IOException {
        if (!this.shouldMerge()) {
            throw new IllegalStateException("Merge would result in 0 document segment");
        }
        this.mergeFieldInfos();
        long t0 = 0L;
        if (this.mergeState.infoStream.isEnabled("SM")) {
            t0 = System.nanoTime();
        }
        int numMerged = this.mergeFields();
        if (this.mergeState.infoStream.isEnabled("SM")) {
            final long t2 = System.nanoTime();
            this.mergeState.infoStream.message("SM", (t2 - t0) / 1000000L + " msec to merge stored fields [" + numMerged + " docs]");
        }
        assert numMerged == this.mergeState.segmentInfo.maxDoc() : "numMerged=" + numMerged + " vs mergeState.segmentInfo.maxDoc()=" + this.mergeState.segmentInfo.maxDoc();
        final SegmentWriteState segmentWriteState = new SegmentWriteState(this.mergeState.infoStream, this.directory, this.mergeState.segmentInfo, this.mergeState.mergeFieldInfos, null, this.context);
        if (this.mergeState.infoStream.isEnabled("SM")) {
            t0 = System.nanoTime();
        }
        this.mergeTerms(segmentWriteState);
        if (this.mergeState.infoStream.isEnabled("SM")) {
            final long t3 = System.nanoTime();
            this.mergeState.infoStream.message("SM", (t3 - t0) / 1000000L + " msec to merge postings [" + numMerged + " docs]");
        }
        if (this.mergeState.infoStream.isEnabled("SM")) {
            t0 = System.nanoTime();
        }
        if (this.mergeState.mergeFieldInfos.hasDocValues()) {
            this.mergeDocValues(segmentWriteState);
        }
        if (this.mergeState.infoStream.isEnabled("SM")) {
            final long t3 = System.nanoTime();
            this.mergeState.infoStream.message("SM", (t3 - t0) / 1000000L + " msec to merge doc values [" + numMerged + " docs]");
        }
        if (this.mergeState.mergeFieldInfos.hasNorms()) {
            if (this.mergeState.infoStream.isEnabled("SM")) {
                t0 = System.nanoTime();
            }
            this.mergeNorms(segmentWriteState);
            if (this.mergeState.infoStream.isEnabled("SM")) {
                final long t3 = System.nanoTime();
                this.mergeState.infoStream.message("SM", (t3 - t0) / 1000000L + " msec to merge norms [" + numMerged + " docs]");
            }
        }
        if (this.mergeState.mergeFieldInfos.hasVectors()) {
            if (this.mergeState.infoStream.isEnabled("SM")) {
                t0 = System.nanoTime();
            }
            numMerged = this.mergeVectors();
            if (this.mergeState.infoStream.isEnabled("SM")) {
                final long t3 = System.nanoTime();
                this.mergeState.infoStream.message("SM", (t3 - t0) / 1000000L + " msec to merge vectors [" + numMerged + " docs]");
            }
            assert numMerged == this.mergeState.segmentInfo.maxDoc();
        }
        if (this.mergeState.infoStream.isEnabled("SM")) {
            t0 = System.nanoTime();
        }
        this.codec.fieldInfosFormat().write(this.directory, this.mergeState.segmentInfo, "", this.mergeState.mergeFieldInfos, this.context);
        if (this.mergeState.infoStream.isEnabled("SM")) {
            final long t3 = System.nanoTime();
            this.mergeState.infoStream.message("SM", (t3 - t0) / 1000000L + " msec to write field infos [" + numMerged + " docs]");
        }
        return this.mergeState;
    }
    
    private void mergeDocValues(final SegmentWriteState segmentWriteState) throws IOException {
        try (final DocValuesConsumer consumer = this.codec.docValuesFormat().fieldsConsumer(segmentWriteState)) {
            consumer.merge(this.mergeState);
        }
    }
    
    private void mergeNorms(final SegmentWriteState segmentWriteState) throws IOException {
        try (final NormsConsumer consumer = this.codec.normsFormat().normsConsumer(segmentWriteState)) {
            consumer.merge(this.mergeState);
        }
    }
    
    public void mergeFieldInfos() throws IOException {
        for (final FieldInfos readerFieldInfos : this.mergeState.fieldInfos) {
            for (final FieldInfo fi : readerFieldInfos) {
                this.fieldInfosBuilder.add(fi);
            }
        }
        this.mergeState.mergeFieldInfos = this.fieldInfosBuilder.finish();
    }
    
    private int mergeFields() throws IOException {
        try (final StoredFieldsWriter fieldsWriter = this.codec.storedFieldsFormat().fieldsWriter(this.directory, this.mergeState.segmentInfo, this.context)) {
            return fieldsWriter.merge(this.mergeState);
        }
    }
    
    private int mergeVectors() throws IOException {
        try (final TermVectorsWriter termVectorsWriter = this.codec.termVectorsFormat().vectorsWriter(this.directory, this.mergeState.segmentInfo, this.context)) {
            return termVectorsWriter.merge(this.mergeState);
        }
    }
    
    private void mergeTerms(final SegmentWriteState segmentWriteState) throws IOException {
        try (final FieldsConsumer consumer = this.codec.postingsFormat().fieldsConsumer(segmentWriteState)) {
            consumer.merge(this.mergeState);
        }
    }
}
