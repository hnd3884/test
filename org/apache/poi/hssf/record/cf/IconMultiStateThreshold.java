package org.apache.poi.hssf.record.cf;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.common.Duplicatable;

public final class IconMultiStateThreshold extends Threshold implements Duplicatable
{
    public static final byte EQUALS_EXCLUDE = 0;
    public static final byte EQUALS_INCLUDE = 1;
    private byte equals;
    
    public IconMultiStateThreshold() {
        this.equals = 1;
    }
    
    public IconMultiStateThreshold(final IconMultiStateThreshold other) {
        super(other);
        this.equals = other.equals;
    }
    
    public IconMultiStateThreshold(final LittleEndianInput in) {
        super(in);
        this.equals = in.readByte();
        in.readInt();
    }
    
    public byte getEquals() {
        return this.equals;
    }
    
    public void setEquals(final byte equals) {
        this.equals = equals;
    }
    
    @Override
    public int getDataLength() {
        return super.getDataLength() + 5;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public IconMultiStateThreshold clone() {
        return this.copy();
    }
    
    @Override
    public IconMultiStateThreshold copy() {
        return new IconMultiStateThreshold(this);
    }
    
    @Override
    public void serialize(final LittleEndianOutput out) {
        super.serialize(out);
        out.writeByte(this.equals);
        out.writeInt(0);
    }
}
