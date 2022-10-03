package org.apache.poi.hssf.record.common;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.RecordInputStream;

public final class FeatSmartTag implements SharedFeature
{
    private byte[] data;
    
    public FeatSmartTag() {
        this.data = new byte[0];
    }
    
    public FeatSmartTag(final FeatSmartTag other) {
        this.data = (byte[])((other.data == null) ? null : ((byte[])other.data.clone()));
    }
    
    public FeatSmartTag(final RecordInputStream in) {
        this.data = in.readRemainder();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(" [FEATURE SMART TAGS]\n");
        buffer.append(" [/FEATURE SMART TAGS]\n");
        return buffer.toString();
    }
    
    @Override
    public int getDataSize() {
        return this.data.length;
    }
    
    @Override
    public void serialize(final LittleEndianOutput out) {
        out.write(this.data);
    }
    
    @Override
    public FeatSmartTag copy() {
        return new FeatSmartTag(this);
    }
}
