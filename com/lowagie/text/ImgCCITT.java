package com.lowagie.text;

import com.lowagie.text.error_messages.MessageLocalization;
import java.net.URL;

public class ImgCCITT extends Image
{
    ImgCCITT(final Image image) {
        super(image);
    }
    
    public ImgCCITT(final int width, final int height, final boolean reverseBits, final int typeCCITT, final int parameters, final byte[] data) throws BadElementException {
        super((URL)null);
        if (typeCCITT != 256 && typeCCITT != 257 && typeCCITT != 258) {
            throw new BadElementException(MessageLocalization.getComposedMessage("the.ccitt.compression.type.must.be.ccittg4.ccittg3.1d.or.ccittg3.2d"));
        }
        if (reverseBits) {
            throw new BadElementException("Reversing bits is not supported");
        }
        this.type = 34;
        this.setTop(this.scaledHeight = (float)height);
        this.setRight(this.scaledWidth = (float)width);
        this.colorspace = parameters;
        this.bpc = typeCCITT;
        this.rawData = data;
        this.plainWidth = this.getWidth();
        this.plainHeight = this.getHeight();
    }
}
