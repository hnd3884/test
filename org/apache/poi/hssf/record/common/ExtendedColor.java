package org.apache.poi.hssf.record.common;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.Removal;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.common.Duplicatable;

public final class ExtendedColor implements Duplicatable
{
    public static final int TYPE_AUTO = 0;
    public static final int TYPE_INDEXED = 1;
    public static final int TYPE_RGB = 2;
    public static final int TYPE_THEMED = 3;
    public static final int TYPE_UNSET = 4;
    public static final int THEME_DARK_1 = 0;
    public static final int THEME_LIGHT_1 = 1;
    public static final int THEME_DARK_2 = 2;
    public static final int THEME_LIGHT_2 = 3;
    public static final int THEME_ACCENT_1 = 4;
    public static final int THEME_ACCENT_2 = 5;
    public static final int THEME_ACCENT_3 = 6;
    public static final int THEME_ACCENT_4 = 7;
    public static final int THEME_ACCENT_5 = 8;
    public static final int THEME_ACCENT_6 = 9;
    public static final int THEME_HYPERLINK = 10;
    public static final int THEME_FOLLOWED_HYPERLINK = 11;
    private int type;
    private int colorIndex;
    private byte[] rgba;
    private int themeIndex;
    private double tint;
    
    public ExtendedColor() {
        this.type = 1;
        this.colorIndex = 0;
        this.tint = 0.0;
    }
    
    public ExtendedColor(final ExtendedColor other) {
        this.type = other.type;
        this.tint = other.tint;
        this.colorIndex = other.colorIndex;
        this.rgba = (byte[])((other.rgba == null) ? null : ((byte[])other.rgba.clone()));
        this.themeIndex = other.themeIndex;
    }
    
    public ExtendedColor(final LittleEndianInput in) {
        this.type = in.readInt();
        if (this.type == 1) {
            this.colorIndex = in.readInt();
        }
        else if (this.type == 2) {
            in.readFully(this.rgba = new byte[4]);
        }
        else if (this.type == 3) {
            this.themeIndex = in.readInt();
        }
        else {
            in.readInt();
        }
        this.tint = in.readDouble();
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public int getColorIndex() {
        return this.colorIndex;
    }
    
    public void setColorIndex(final int colorIndex) {
        this.colorIndex = colorIndex;
    }
    
    public byte[] getRGBA() {
        return this.rgba;
    }
    
    public void setRGBA(final byte[] rgba) {
        this.rgba = (byte[])((rgba == null) ? null : ((byte[])rgba.clone()));
    }
    
    public int getThemeIndex() {
        return this.themeIndex;
    }
    
    public void setThemeIndex(final int themeIndex) {
        this.themeIndex = themeIndex;
    }
    
    public double getTint() {
        return this.tint;
    }
    
    public void setTint(final double tint) {
        if (tint < -1.0 || tint > 1.0) {
            throw new IllegalArgumentException("Tint/Shade must be between -1 and +1");
        }
        this.tint = tint;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("    [Extended Color]\n");
        buffer.append("          .type  = ").append(this.type).append("\n");
        buffer.append("          .tint  = ").append(this.tint).append("\n");
        buffer.append("          .c_idx = ").append(this.colorIndex).append("\n");
        buffer.append("          .rgba  = ").append(HexDump.toHex(this.rgba)).append("\n");
        buffer.append("          .t_idx = ").append(this.themeIndex).append("\n");
        buffer.append("    [/Extended Color]\n");
        return buffer.toString();
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public ExtendedColor clone() {
        return this.copy();
    }
    
    @Override
    public ExtendedColor copy() {
        return new ExtendedColor(this);
    }
    
    public int getDataLength() {
        return 16;
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeInt(this.type);
        if (this.type == 1) {
            out.writeInt(this.colorIndex);
        }
        else if (this.type == 2) {
            out.write(this.rgba);
        }
        else if (this.type == 3) {
            out.writeInt(this.themeIndex);
        }
        else {
            out.writeInt(0);
        }
        out.writeDouble(this.tint);
    }
}
