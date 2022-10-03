package com.sun.imageio.plugins.gif;

import java.util.Locale;
import javax.imageio.ImageWriteParam;

class GIFImageWriteParam extends ImageWriteParam
{
    GIFImageWriteParam(final Locale locale) {
        super(locale);
        this.canWriteCompressed = true;
        this.canWriteProgressive = true;
        this.compressionTypes = new String[] { "LZW", "lzw" };
        this.compressionType = this.compressionTypes[0];
    }
    
    @Override
    public void setCompressionMode(final int compressionMode) {
        if (compressionMode == 0) {
            throw new UnsupportedOperationException("MODE_DISABLED is not supported.");
        }
        super.setCompressionMode(compressionMode);
    }
}
