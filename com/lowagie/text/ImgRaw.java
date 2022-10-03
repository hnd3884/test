package com.lowagie.text;

import com.lowagie.text.error_messages.MessageLocalization;
import java.net.URL;

public class ImgRaw extends Image
{
    ImgRaw(final Image image) {
        super(image);
    }
    
    public ImgRaw(final int width, final int height, final int components, final int bpc, final byte[] data) throws BadElementException {
        super((URL)null);
        this.type = 34;
        this.setTop(this.scaledHeight = (float)height);
        this.setRight(this.scaledWidth = (float)width);
        if (components != 1 && components != 3 && components != 4) {
            throw new BadElementException(MessageLocalization.getComposedMessage("components.must.be.1.3.or.4"));
        }
        if (bpc != 1 && bpc != 2 && bpc != 4 && bpc != 8) {
            throw new BadElementException(MessageLocalization.getComposedMessage("bits.per.component.must.be.1.2.4.or.8"));
        }
        this.colorspace = components;
        this.bpc = bpc;
        this.rawData = data;
        this.plainWidth = this.getWidth();
        this.plainHeight = this.getHeight();
    }
}
