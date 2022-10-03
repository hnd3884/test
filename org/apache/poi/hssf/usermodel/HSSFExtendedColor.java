package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.ExtendedColor;

public class HSSFExtendedColor extends ExtendedColor
{
    private org.apache.poi.hssf.record.common.ExtendedColor color;
    
    public HSSFExtendedColor(final org.apache.poi.hssf.record.common.ExtendedColor color) {
        this.color = color;
    }
    
    protected org.apache.poi.hssf.record.common.ExtendedColor getExtendedColor() {
        return this.color;
    }
    
    @Override
    public boolean isAuto() {
        return this.color.getType() == 0;
    }
    
    @Override
    public boolean isIndexed() {
        return this.color.getType() == 1;
    }
    
    @Override
    public boolean isRGB() {
        return this.color.getType() == 2;
    }
    
    @Override
    public boolean isThemed() {
        return this.color.getType() == 3;
    }
    
    @Override
    public short getIndex() {
        return (short)this.color.getColorIndex();
    }
    
    @Override
    public int getTheme() {
        return this.color.getThemeIndex();
    }
    
    @Override
    public byte[] getRGB() {
        final byte[] rgb = new byte[3];
        final byte[] rgba = this.color.getRGBA();
        if (rgba == null) {
            return null;
        }
        System.arraycopy(rgba, 0, rgb, 0, 3);
        return rgb;
    }
    
    @Override
    public byte[] getARGB() {
        final byte[] argb = new byte[4];
        final byte[] rgba = this.color.getRGBA();
        if (rgba == null) {
            return null;
        }
        System.arraycopy(rgba, 0, argb, 1, 3);
        argb[0] = rgba[3];
        return argb;
    }
    
    @Override
    protected byte[] getStoredRBG() {
        return this.getARGB();
    }
    
    @Override
    public void setRGB(final byte[] rgb) {
        if (rgb.length == 3) {
            final byte[] rgba = new byte[4];
            System.arraycopy(rgb, 0, rgba, 0, 3);
            rgba[3] = -1;
        }
        else {
            final byte a = rgb[0];
            rgb[0] = rgb[1];
            rgb[1] = rgb[2];
            rgb[2] = rgb[3];
            rgb[3] = a;
            this.color.setRGBA(rgb);
        }
        this.color.setType(2);
    }
    
    @Override
    public double getTint() {
        return this.color.getTint();
    }
    
    @Override
    public void setTint(final double tint) {
        this.color.setTint(tint);
    }
    
    @Override
    protected byte[] getIndexedRGB() {
        if (this.isIndexed() && this.getIndex() > 0) {
            final int indexNum = this.getIndex();
            final HSSFColor indexed = HSSFColor.getIndexHash().get(indexNum);
            if (indexed != null) {
                final byte[] rgb = { (byte)indexed.getTriplet()[0], (byte)indexed.getTriplet()[1], (byte)indexed.getTriplet()[2] };
                return rgb;
            }
        }
        return null;
    }
}
