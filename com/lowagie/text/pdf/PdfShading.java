package com.lowagie.text.pdf;

import java.io.IOException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.awt.Color;

public class PdfShading
{
    protected PdfDictionary shading;
    protected PdfWriter writer;
    protected int shadingType;
    protected ColorDetails colorDetails;
    protected PdfName shadingName;
    protected PdfIndirectReference shadingReference;
    private Color cspace;
    protected float[] bBox;
    protected boolean antiAlias;
    
    protected PdfShading(final PdfWriter writer) {
        this.antiAlias = false;
        this.writer = writer;
    }
    
    protected void setColorSpace(final Color color) {
        this.cspace = color;
        final int type = ExtendedColor.getType(color);
        PdfObject colorSpace = null;
        Label_0102: {
            switch (type) {
                case 1: {
                    colorSpace = PdfName.DEVICEGRAY;
                    break Label_0102;
                }
                case 2: {
                    colorSpace = PdfName.DEVICECMYK;
                    break Label_0102;
                }
                case 3: {
                    final SpotColor spot = (SpotColor)color;
                    this.colorDetails = this.writer.addSimple(spot.getPdfSpotColor());
                    colorSpace = this.colorDetails.getIndirectReference();
                    break Label_0102;
                }
                case 4:
                case 5: {
                    throwColorSpaceError();
                    break;
                }
            }
            colorSpace = PdfName.DEVICERGB;
        }
        this.shading.put(PdfName.COLORSPACE, colorSpace);
    }
    
    public Color getColorSpace() {
        return this.cspace;
    }
    
    public static void throwColorSpaceError() {
        throw new IllegalArgumentException(MessageLocalization.getComposedMessage("a.tiling.or.shading.pattern.cannot.be.used.as.a.color.space.in.a.shading.pattern"));
    }
    
    public static void checkCompatibleColors(final Color c1, final Color c2) {
        final int type1 = ExtendedColor.getType(c1);
        final int type2 = ExtendedColor.getType(c2);
        if (type1 != type2) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("both.colors.must.be.of.the.same.type"));
        }
        if (type1 == 3 && ((SpotColor)c1).getPdfSpotColor() != ((SpotColor)c2).getPdfSpotColor()) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.spot.color.must.be.the.same.only.the.tint.can.vary"));
        }
        if (type1 == 4 || type1 == 5) {
            throwColorSpaceError();
        }
    }
    
    public static float[] getColorArray(final Color color) {
        final int type = ExtendedColor.getType(color);
        switch (type) {
            case 1: {
                return new float[] { ((GrayColor)color).getGray() };
            }
            case 2: {
                final CMYKColor cmyk = (CMYKColor)color;
                return new float[] { cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack() };
            }
            case 3: {
                return new float[] { ((SpotColor)color).getTint() };
            }
            case 0: {
                return new float[] { color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f };
            }
            default: {
                throwColorSpaceError();
                return null;
            }
        }
    }
    
    public static PdfShading type1(final PdfWriter writer, final Color colorSpace, final float[] domain, final float[] tMatrix, final PdfFunction function) {
        final PdfShading sp = new PdfShading(writer);
        sp.shading = new PdfDictionary();
        sp.shadingType = 1;
        sp.shading.put(PdfName.SHADINGTYPE, new PdfNumber(sp.shadingType));
        sp.setColorSpace(colorSpace);
        if (domain != null) {
            sp.shading.put(PdfName.DOMAIN, new PdfArray(domain));
        }
        if (tMatrix != null) {
            sp.shading.put(PdfName.MATRIX, new PdfArray(tMatrix));
        }
        sp.shading.put(PdfName.FUNCTION, function.getReference());
        return sp;
    }
    
    public static PdfShading type2(final PdfWriter writer, final Color colorSpace, final float[] coords, final float[] domain, final PdfFunction function, final boolean[] extend) {
        final PdfShading sp = new PdfShading(writer);
        sp.shading = new PdfDictionary();
        sp.shadingType = 2;
        sp.shading.put(PdfName.SHADINGTYPE, new PdfNumber(sp.shadingType));
        sp.setColorSpace(colorSpace);
        sp.shading.put(PdfName.COORDS, new PdfArray(coords));
        if (domain != null) {
            sp.shading.put(PdfName.DOMAIN, new PdfArray(domain));
        }
        sp.shading.put(PdfName.FUNCTION, function.getReference());
        if (extend != null && (extend[0] || extend[1])) {
            final PdfArray array = new PdfArray(extend[0] ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
            array.add(extend[1] ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
            sp.shading.put(PdfName.EXTEND, array);
        }
        return sp;
    }
    
    public static PdfShading type3(final PdfWriter writer, final Color colorSpace, final float[] coords, final float[] domain, final PdfFunction function, final boolean[] extend) {
        final PdfShading sp = type2(writer, colorSpace, coords, domain, function, extend);
        sp.shadingType = 3;
        sp.shading.put(PdfName.SHADINGTYPE, new PdfNumber(sp.shadingType));
        return sp;
    }
    
    public static PdfShading simpleAxial(final PdfWriter writer, final float x0, final float y0, final float x1, final float y1, final Color startColor, final Color endColor, final boolean extendStart, final boolean extendEnd) {
        checkCompatibleColors(startColor, endColor);
        final PdfFunction function = PdfFunction.type2(writer, new float[] { 0.0f, 1.0f }, null, getColorArray(startColor), getColorArray(endColor), 1.0f);
        return type2(writer, startColor, new float[] { x0, y0, x1, y1 }, null, function, new boolean[] { extendStart, extendEnd });
    }
    
    public static PdfShading simpleAxial(final PdfWriter writer, final float x0, final float y0, final float x1, final float y1, final Color startColor, final Color endColor) {
        return simpleAxial(writer, x0, y0, x1, y1, startColor, endColor, true, true);
    }
    
    public static PdfShading simpleRadial(final PdfWriter writer, final float x0, final float y0, final float r0, final float x1, final float y1, final float r1, final Color startColor, final Color endColor, final boolean extendStart, final boolean extendEnd) {
        checkCompatibleColors(startColor, endColor);
        final PdfFunction function = PdfFunction.type2(writer, new float[] { 0.0f, 1.0f }, null, getColorArray(startColor), getColorArray(endColor), 1.0f);
        return type3(writer, startColor, new float[] { x0, y0, r0, x1, y1, r1 }, null, function, new boolean[] { extendStart, extendEnd });
    }
    
    public static PdfShading simpleRadial(final PdfWriter writer, final float x0, final float y0, final float r0, final float x1, final float y1, final float r1, final Color startColor, final Color endColor) {
        return simpleRadial(writer, x0, y0, r0, x1, y1, r1, startColor, endColor, true, true);
    }
    
    PdfName getShadingName() {
        return this.shadingName;
    }
    
    PdfIndirectReference getShadingReference() {
        if (this.shadingReference == null) {
            this.shadingReference = this.writer.getPdfIndirectReference();
        }
        return this.shadingReference;
    }
    
    void setName(final int number) {
        this.shadingName = new PdfName("Sh" + number);
    }
    
    void addToBody() throws IOException {
        if (this.bBox != null) {
            this.shading.put(PdfName.BBOX, new PdfArray(this.bBox));
        }
        if (this.antiAlias) {
            this.shading.put(PdfName.ANTIALIAS, PdfBoolean.PDFTRUE);
        }
        this.writer.addToBody(this.shading, this.getShadingReference());
    }
    
    PdfWriter getWriter() {
        return this.writer;
    }
    
    ColorDetails getColorDetails() {
        return this.colorDetails;
    }
    
    public float[] getBBox() {
        return this.bBox;
    }
    
    public void setBBox(final float[] bBox) {
        if (bBox.length != 4) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("bbox.must.be.a.4.element.array"));
        }
        this.bBox = bBox;
    }
    
    public boolean isAntiAlias() {
        return this.antiAlias;
    }
    
    public void setAntiAlias(final boolean antiAlias) {
        this.antiAlias = antiAlias;
    }
}
