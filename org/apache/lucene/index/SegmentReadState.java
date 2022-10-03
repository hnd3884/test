package org.apache.lucene.index;

import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.Directory;

public class SegmentReadState
{
    public final Directory directory;
    public final SegmentInfo segmentInfo;
    public final FieldInfos fieldInfos;
    public final IOContext context;
    public final String segmentSuffix;
    
    public SegmentReadState(final Directory dir, final SegmentInfo info, final FieldInfos fieldInfos, final IOContext context) {
        this(dir, info, fieldInfos, context, "");
    }
    
    public SegmentReadState(final Directory dir, final SegmentInfo info, final FieldInfos fieldInfos, final IOContext context, final String segmentSuffix) {
        this.directory = dir;
        this.segmentInfo = info;
        this.fieldInfos = fieldInfos;
        this.context = context;
        this.segmentSuffix = segmentSuffix;
    }
    
    public SegmentReadState(final SegmentReadState other, final String newSegmentSuffix) {
        this.directory = other.directory;
        this.segmentInfo = other.segmentInfo;
        this.fieldInfos = other.fieldInfos;
        this.context = other.context;
        this.segmentSuffix = newSegmentSuffix;
    }
}
