package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import java.awt.Color;
import com.lowagie.text.Rectangle;

public abstract class Barcode
{
    public static final int EAN13 = 1;
    public static final int EAN8 = 2;
    public static final int UPCA = 3;
    public static final int UPCE = 4;
    public static final int SUPP2 = 5;
    public static final int SUPP5 = 6;
    public static final int POSTNET = 7;
    public static final int PLANET = 8;
    public static final int CODE128 = 9;
    public static final int CODE128_UCC = 10;
    public static final int CODE128_RAW = 11;
    public static final int CODABAR = 12;
    protected float x;
    protected float n;
    protected BaseFont font;
    protected float size;
    protected float baseline;
    protected float barHeight;
    protected int textAlignment;
    protected boolean generateChecksum;
    protected boolean checksumText;
    protected boolean startStopText;
    protected boolean extended;
    protected String code;
    protected boolean guardBars;
    protected int codeType;
    protected float inkSpreading;
    protected String altText;
    
    public Barcode() {
        this.code = "";
        this.inkSpreading = 0.0f;
    }
    
    public float getX() {
        return this.x;
    }
    
    public void setX(final float x) {
        this.x = x;
    }
    
    public float getN() {
        return this.n;
    }
    
    public void setN(final float n) {
        this.n = n;
    }
    
    public BaseFont getFont() {
        return this.font;
    }
    
    public void setFont(final BaseFont font) {
        this.font = font;
    }
    
    public float getSize() {
        return this.size;
    }
    
    public void setSize(final float size) {
        this.size = size;
    }
    
    public float getBaseline() {
        return this.baseline;
    }
    
    public void setBaseline(final float baseline) {
        this.baseline = baseline;
    }
    
    public float getBarHeight() {
        return this.barHeight;
    }
    
    public void setBarHeight(final float barHeight) {
        this.barHeight = barHeight;
    }
    
    public int getTextAlignment() {
        return this.textAlignment;
    }
    
    public void setTextAlignment(final int textAlignment) {
        this.textAlignment = textAlignment;
    }
    
    public boolean isGenerateChecksum() {
        return this.generateChecksum;
    }
    
    public void setGenerateChecksum(final boolean generateChecksum) {
        this.generateChecksum = generateChecksum;
    }
    
    public boolean isChecksumText() {
        return this.checksumText;
    }
    
    public void setChecksumText(final boolean checksumText) {
        this.checksumText = checksumText;
    }
    
    public boolean isStartStopText() {
        return this.startStopText;
    }
    
    public void setStartStopText(final boolean startStopText) {
        this.startStopText = startStopText;
    }
    
    public boolean isExtended() {
        return this.extended;
    }
    
    public void setExtended(final boolean extended) {
        this.extended = extended;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public void setCode(final String code) {
        this.code = code;
    }
    
    public boolean isGuardBars() {
        return this.guardBars;
    }
    
    public void setGuardBars(final boolean guardBars) {
        this.guardBars = guardBars;
    }
    
    public int getCodeType() {
        return this.codeType;
    }
    
    public void setCodeType(final int codeType) {
        this.codeType = codeType;
    }
    
    public abstract Rectangle getBarcodeSize();
    
    public abstract Rectangle placeBarcode(final PdfContentByte p0, final Color p1, final Color p2);
    
    public PdfTemplate createTemplateWithBarcode(final PdfContentByte cb, final Color barColor, final Color textColor) {
        final PdfTemplate tp = cb.createTemplate(0.0f, 0.0f);
        final Rectangle rect = this.placeBarcode(tp, barColor, textColor);
        tp.setBoundingBox(rect);
        return tp;
    }
    
    public Image createImageWithBarcode(final PdfContentByte cb, final Color barColor, final Color textColor) {
        try {
            return Image.getInstance(this.createTemplateWithBarcode(cb, barColor, textColor));
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public abstract java.awt.Image createAwtImage(final Color p0, final Color p1);
    
    public float getInkSpreading() {
        return this.inkSpreading;
    }
    
    public void setInkSpreading(final float inkSpreading) {
        this.inkSpreading = inkSpreading;
    }
    
    public String getAltText() {
        return this.altText;
    }
    
    public void setAltText(final String altText) {
        this.altText = altText;
    }
}
