package org.apache.poi.hssf.record.cf;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.common.Duplicatable;

public final class ColorGradientThreshold extends Threshold implements Duplicatable
{
    private double position;
    
    public ColorGradientThreshold() {
        this.position = 0.0;
    }
    
    public ColorGradientThreshold(final ColorGradientThreshold other) {
        super(other);
        this.position = other.position;
    }
    
    public ColorGradientThreshold(final LittleEndianInput in) {
        super(in);
        this.position = in.readDouble();
    }
    
    public double getPosition() {
        return this.position;
    }
    
    public void setPosition(final double position) {
        this.position = position;
    }
    
    @Override
    public int getDataLength() {
        return super.getDataLength() + 8;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public ColorGradientThreshold clone() {
        return this.copy();
    }
    
    @Override
    public ColorGradientThreshold copy() {
        return new ColorGradientThreshold(this);
    }
    
    @Override
    public void serialize(final LittleEndianOutput out) {
        super.serialize(out);
        out.writeDouble(this.position);
    }
}
