package com.sun.imageio.plugins.png;

import java.util.Locale;
import javax.imageio.ImageWriteParam;

class PNGImageWriteParam extends ImageWriteParam
{
    public PNGImageWriteParam(final Locale locale) {
        this.canWriteProgressive = true;
        this.locale = locale;
    }
}
