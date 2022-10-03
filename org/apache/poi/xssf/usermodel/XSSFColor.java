package org.apache.poi.xssf.usermodel;

import java.util.Arrays;
import org.apache.poi.util.Internal;
import org.apache.poi.ss.usermodel.IndexedColors;
import java.awt.Color;
import org.apache.poi.util.Removal;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.apache.poi.ss.usermodel.ExtendedColor;

public class XSSFColor extends ExtendedColor
{
    private final CTColor ctColor;
    private final IndexedColorMap indexedColorMap;
    
    public static XSSFColor from(final CTColor color, final IndexedColorMap map) {
        return (color == null) ? null : new XSSFColor(color, map);
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public XSSFColor(final CTColor color) {
        this(color, new DefaultIndexedColorMap());
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public XSSFColor(final CTColor color, final IndexedColorMap map) {
        this.ctColor = color;
        this.indexedColorMap = map;
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public XSSFColor() {
        this(CTColor.Factory.newInstance(), new DefaultIndexedColorMap());
    }
    
    public XSSFColor(final IndexedColorMap colorMap) {
        this(CTColor.Factory.newInstance(), colorMap);
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public XSSFColor(final Color clr) {
        this(clr, new DefaultIndexedColorMap());
    }
    
    public XSSFColor(final Color clr, final IndexedColorMap map) {
        this(map);
        this.setColor(clr);
    }
    
    public XSSFColor(final byte[] rgb, final IndexedColorMap colorMap) {
        this(CTColor.Factory.newInstance(), colorMap);
        this.ctColor.setRgb(rgb);
    }
    
    public XSSFColor(final IndexedColors indexedColor, final IndexedColorMap colorMap) {
        this(CTColor.Factory.newInstance(), colorMap);
        this.ctColor.setIndexed((long)indexedColor.index);
    }
    
    public boolean isAuto() {
        return this.ctColor.getAuto();
    }
    
    public void setAuto(final boolean auto) {
        this.ctColor.setAuto(auto);
    }
    
    public boolean isIndexed() {
        return this.ctColor.isSetIndexed();
    }
    
    public boolean isRGB() {
        return this.ctColor.isSetRgb();
    }
    
    public boolean isThemed() {
        return this.ctColor.isSetTheme();
    }
    
    public boolean hasAlpha() {
        return this.ctColor.isSetRgb() && this.ctColor.getRgb().length == 4;
    }
    
    public boolean hasTint() {
        return this.ctColor.isSetTint() && this.ctColor.getTint() != 0.0;
    }
    
    public short getIndex() {
        return (short)this.ctColor.getIndexed();
    }
    
    public short getIndexed() {
        return this.getIndex();
    }
    
    public void setIndexed(final int indexed) {
        this.ctColor.setIndexed((long)indexed);
    }
    
    public byte[] getRGB() {
        final byte[] rgb = this.getRGBOrARGB();
        if (rgb == null) {
            return null;
        }
        if (rgb.length == 4) {
            final byte[] tmp = new byte[3];
            System.arraycopy(rgb, 1, tmp, 0, 3);
            return tmp;
        }
        return rgb;
    }
    
    public byte[] getARGB() {
        final byte[] rgb = this.getRGBOrARGB();
        if (rgb == null) {
            return null;
        }
        if (rgb.length == 3) {
            final byte[] tmp = new byte[4];
            tmp[0] = -1;
            System.arraycopy(rgb, 0, tmp, 1, 3);
            return tmp;
        }
        return rgb;
    }
    
    protected byte[] getStoredRBG() {
        return this.ctColor.getRgb();
    }
    
    protected byte[] getIndexedRGB() {
        if (!this.isIndexed()) {
            return null;
        }
        if (this.indexedColorMap != null) {
            return this.indexedColorMap.getRGB(this.getIndex());
        }
        return DefaultIndexedColorMap.getDefaultRGB(this.getIndex());
    }
    
    public void setRGB(final byte[] rgb) {
        this.ctColor.setRgb(rgb);
    }
    
    public int getTheme() {
        return (int)this.ctColor.getTheme();
    }
    
    public void setTheme(final int theme) {
        this.ctColor.setTheme((long)theme);
    }
    
    public double getTint() {
        return this.ctColor.getTint();
    }
    
    public void setTint(final double tint) {
        this.ctColor.setTint(tint);
    }
    
    @Internal
    public CTColor getCTColor() {
        return this.ctColor;
    }
    
    public static XSSFColor toXSSFColor(final org.apache.poi.ss.usermodel.Color color) {
        if (color != null && !(color instanceof XSSFColor)) {
            throw new IllegalArgumentException("Only XSSFColor objects are supported, but had " + color.getClass());
        }
        return (XSSFColor)color;
    }
    
    public int hashCode() {
        return this.ctColor.toString().hashCode();
    }
    
    private boolean sameIndexed(final XSSFColor other) {
        return this.isIndexed() == other.isIndexed() && (!this.isIndexed() || this.getIndexed() == other.getIndexed());
    }
    
    private boolean sameARGB(final XSSFColor other) {
        return this.isRGB() == other.isRGB() && (!this.isRGB() || Arrays.equals(this.getARGB(), other.getARGB()));
    }
    
    private boolean sameTheme(final XSSFColor other) {
        return this.isThemed() == other.isThemed() && (!this.isThemed() || this.getTheme() == other.getTheme());
    }
    
    private boolean sameTint(final XSSFColor other) {
        return this.hasTint() == other.hasTint() && (!this.hasTint() || this.getTint() == other.getTint());
    }
    
    private boolean sameAuto(final XSSFColor other) {
        return this.isAuto() == other.isAuto();
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof XSSFColor)) {
            return false;
        }
        final XSSFColor other = (XSSFColor)o;
        return this.sameARGB(other) && this.sameTheme(other) && this.sameIndexed(other) && this.sameTint(other) && this.sameAuto(other);
    }
}
