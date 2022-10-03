package org.apache.poi.xslf.util;

import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.Internal;

@Internal
class WMFHandler extends EMFHandler
{
    @Override
    protected String getContentType() {
        return PictureData.PictureType.WMF.contentType;
    }
}
