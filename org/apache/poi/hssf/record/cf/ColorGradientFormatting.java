package org.apache.poi.hssf.record.cf;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianInput;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.poi.hssf.record.common.ExtendedColor;
import org.apache.poi.util.BitField;
import org.apache.poi.util.POILogger;
import org.apache.poi.common.Duplicatable;

public final class ColorGradientFormatting implements Duplicatable
{
    private static final POILogger log;
    private static final BitField clamp;
    private static final BitField background;
    private byte options;
    private ColorGradientThreshold[] thresholds;
    private ExtendedColor[] colors;
    
    public ColorGradientFormatting() {
        this.options = 3;
        this.thresholds = new ColorGradientThreshold[3];
        this.colors = new ExtendedColor[3];
    }
    
    public ColorGradientFormatting(final ColorGradientFormatting other) {
        this.options = other.options;
        if (other.thresholds != null) {
            this.thresholds = Stream.of(other.thresholds).map((Function<? super ColorGradientThreshold, ?>)ColorGradientThreshold::copy).toArray(ColorGradientThreshold[]::new);
        }
        if (other.colors != null) {
            this.colors = Stream.of(other.colors).map((Function<? super ExtendedColor, ?>)ExtendedColor::copy).toArray(ExtendedColor[]::new);
        }
    }
    
    public ColorGradientFormatting(final LittleEndianInput in) {
        in.readShort();
        in.readByte();
        final int numI = in.readByte();
        final int numG = in.readByte();
        if (numI != numG) {
            ColorGradientFormatting.log.log(5, "Inconsistent Color Gradient defintion, found " + numI + " vs " + numG + " entries");
        }
        this.options = in.readByte();
        this.thresholds = new ColorGradientThreshold[numI];
        for (int i = 0; i < this.thresholds.length; ++i) {
            this.thresholds[i] = new ColorGradientThreshold(in);
        }
        this.colors = new ExtendedColor[numG];
        for (int i = 0; i < this.colors.length; ++i) {
            in.readDouble();
            this.colors[i] = new ExtendedColor(in);
        }
    }
    
    public int getNumControlPoints() {
        return this.thresholds.length;
    }
    
    public void setNumControlPoints(final int num) {
        if (num != this.thresholds.length) {
            final ColorGradientThreshold[] nt = new ColorGradientThreshold[num];
            final ExtendedColor[] nc = new ExtendedColor[num];
            final int copy = Math.min(this.thresholds.length, num);
            System.arraycopy(this.thresholds, 0, nt, 0, copy);
            System.arraycopy(this.colors, 0, nc, 0, copy);
            this.thresholds = nt;
            this.colors = nc;
            this.updateThresholdPositions();
        }
    }
    
    public ColorGradientThreshold[] getThresholds() {
        return this.thresholds;
    }
    
    public void setThresholds(final ColorGradientThreshold[] thresholds) {
        this.thresholds = (ColorGradientThreshold[])((thresholds == null) ? null : ((ColorGradientThreshold[])thresholds.clone()));
        this.updateThresholdPositions();
    }
    
    public ExtendedColor[] getColors() {
        return this.colors;
    }
    
    public void setColors(final ExtendedColor[] colors) {
        this.colors = (ExtendedColor[])((colors == null) ? null : ((ExtendedColor[])colors.clone()));
    }
    
    public boolean isClampToCurve() {
        return this.getOptionFlag(ColorGradientFormatting.clamp);
    }
    
    public boolean isAppliesToBackground() {
        return this.getOptionFlag(ColorGradientFormatting.background);
    }
    
    private boolean getOptionFlag(final BitField field) {
        return field.isSet(this.options);
    }
    
    private void updateThresholdPositions() {
        final double step = 1.0 / (this.thresholds.length - 1);
        for (int i = 0; i < this.thresholds.length; ++i) {
            this.thresholds[i].setPosition(step * i);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("    [Color Gradient Formatting]\n");
        buffer.append("          .clamp     = ").append(this.isClampToCurve()).append("\n");
        buffer.append("          .background= ").append(this.isAppliesToBackground()).append("\n");
        for (final Threshold t : this.thresholds) {
            buffer.append(t);
        }
        for (final ExtendedColor c : this.colors) {
            buffer.append(c);
        }
        buffer.append("    [/Color Gradient Formatting]\n");
        return buffer.toString();
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public ColorGradientFormatting clone() {
        return this.copy();
    }
    
    @Override
    public ColorGradientFormatting copy() {
        return new ColorGradientFormatting(this);
    }
    
    public int getDataLength() {
        int len = 6;
        for (final Threshold t : this.thresholds) {
            len += t.getDataLength();
        }
        for (final ExtendedColor c : this.colors) {
            len += c.getDataLength();
            len += 8;
        }
        return len;
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(0);
        out.writeByte(0);
        out.writeByte(this.thresholds.length);
        out.writeByte(this.thresholds.length);
        out.writeByte(this.options);
        for (final ColorGradientThreshold t : this.thresholds) {
            t.serialize(out);
        }
        final double step = 1.0 / (this.colors.length - 1);
        for (int i = 0; i < this.colors.length; ++i) {
            out.writeDouble(i * step);
            final ExtendedColor c = this.colors[i];
            c.serialize(out);
        }
    }
    
    static {
        log = POILogFactory.getLogger(ColorGradientFormatting.class);
        clamp = BitFieldFactory.getInstance(1);
        background = BitFieldFactory.getInstance(2);
    }
}
