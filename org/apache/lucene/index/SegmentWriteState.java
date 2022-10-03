package org.apache.lucene.index;

import org.apache.lucene.store.IOContext;
import org.apache.lucene.util.MutableBits;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.InfoStream;

public class SegmentWriteState
{
    public final InfoStream infoStream;
    public final Directory directory;
    public final SegmentInfo segmentInfo;
    public final FieldInfos fieldInfos;
    public int delCountOnFlush;
    public final BufferedUpdates segUpdates;
    public MutableBits liveDocs;
    public final String segmentSuffix;
    public final IOContext context;
    
    public SegmentWriteState(final InfoStream infoStream, final Directory directory, final SegmentInfo segmentInfo, final FieldInfos fieldInfos, final BufferedUpdates segUpdates, final IOContext context) {
        this(infoStream, directory, segmentInfo, fieldInfos, segUpdates, context, "");
    }
    
    public SegmentWriteState(final InfoStream infoStream, final Directory directory, final SegmentInfo segmentInfo, final FieldInfos fieldInfos, final BufferedUpdates segUpdates, final IOContext context, final String segmentSuffix) {
        this.infoStream = infoStream;
        this.segUpdates = segUpdates;
        this.directory = directory;
        this.segmentInfo = segmentInfo;
        this.fieldInfos = fieldInfos;
        assert this.assertSegmentSuffix(segmentSuffix);
        this.segmentSuffix = segmentSuffix;
        this.context = context;
    }
    
    public SegmentWriteState(final SegmentWriteState state, final String segmentSuffix) {
        this.infoStream = state.infoStream;
        this.directory = state.directory;
        this.segmentInfo = state.segmentInfo;
        this.fieldInfos = state.fieldInfos;
        this.context = state.context;
        this.segmentSuffix = segmentSuffix;
        this.segUpdates = state.segUpdates;
        this.delCountOnFlush = state.delCountOnFlush;
        this.liveDocs = state.liveDocs;
    }
    
    private boolean assertSegmentSuffix(final String segmentSuffix) {
        assert segmentSuffix != null;
        if (segmentSuffix.isEmpty()) {
            return true;
        }
        final int numParts = segmentSuffix.split("_").length;
        if (numParts == 2) {
            return true;
        }
        if (numParts == 1) {
            Long.parseLong(segmentSuffix, 36);
            return true;
        }
        return false;
    }
}
