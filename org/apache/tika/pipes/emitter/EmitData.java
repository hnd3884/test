package org.apache.tika.pipes.emitter;

import java.util.Iterator;
import org.apache.tika.metadata.Metadata;
import java.util.List;
import java.io.Serializable;

public class EmitData implements Serializable
{
    private static final long serialVersionUID = -3861669115439125268L;
    private final EmitKey emitKey;
    private final List<Metadata> metadataList;
    
    public EmitData(final EmitKey emitKey, final List<Metadata> metadataList) {
        this.emitKey = emitKey;
        this.metadataList = metadataList;
    }
    
    public EmitKey getEmitKey() {
        return this.emitKey;
    }
    
    public List<Metadata> getMetadataList() {
        return this.metadataList;
    }
    
    public long getEstimatedSizeBytes() {
        return estimateSizeInBytes(this.getEmitKey().getEmitKey(), this.getMetadataList());
    }
    
    private static long estimateSizeInBytes(final String id, final List<Metadata> metadataList) {
        long sz = 36 + id.length() * 2;
        for (final Metadata m : metadataList) {
            for (final String n : m.names()) {
                sz += 36 + n.length() * 2;
                for (final String v : m.getValues(n)) {
                    sz += 36 + v.length() * 2;
                }
            }
        }
        return sz;
    }
    
    @Override
    public String toString() {
        return "EmitData{emitKey=" + this.emitKey + ", metadataList=" + this.metadataList + '}';
    }
}
