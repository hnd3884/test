package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import java.awt.Color;

public class PdfSpotColor
{
    public PdfName name;
    public Color altcs;
    
    public PdfSpotColor(final String name, final Color altcs) {
        this.name = new PdfName(name);
        this.altcs = altcs;
    }
    
    public Color getAlternativeCS() {
        return this.altcs;
    }
    
    protected PdfObject getSpotObject(final PdfWriter writer) {
        final PdfArray array = new PdfArray(PdfName.SEPARATION);
        array.add(this.name);
        PdfFunction func = null;
        if (this.altcs instanceof ExtendedColor) {
            final int type = ((ExtendedColor)this.altcs).type;
            switch (type) {
                case 1: {
                    array.add(PdfName.DEVICEGRAY);
                    func = PdfFunction.type2(writer, new float[] { 0.0f, 1.0f }, null, new float[] { 0.0f }, new float[] { ((GrayColor)this.altcs).getGray() }, 1.0f);
                    break;
                }
                case 2: {
                    array.add(PdfName.DEVICECMYK);
                    final CMYKColor cmyk = (CMYKColor)this.altcs;
                    func = PdfFunction.type2(writer, new float[] { 0.0f, 1.0f }, null, new float[] { 0.0f, 0.0f, 0.0f, 0.0f }, new float[] { cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack() }, 1.0f);
                    break;
                }
                default: {
                    throw new RuntimeException(MessageLocalization.getComposedMessage("only.rgb.gray.and.cmyk.are.supported.as.alternative.color.spaces"));
                }
            }
        }
        else {
            array.add(PdfName.DEVICERGB);
            func = PdfFunction.type2(writer, new float[] { 0.0f, 1.0f }, null, new float[] { 1.0f, 1.0f, 1.0f }, new float[] { this.altcs.getRed() / 255.0f, this.altcs.getGreen() / 255.0f, this.altcs.getBlue() / 255.0f }, 1.0f);
        }
        array.add(func.getReference());
        return array;
    }
}
