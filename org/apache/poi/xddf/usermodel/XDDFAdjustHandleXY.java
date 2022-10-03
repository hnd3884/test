package org.apache.poi.xddf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTXYAdjustHandle;

public class XDDFAdjustHandleXY
{
    private CTXYAdjustHandle handle;
    
    @Internal
    public XDDFAdjustHandleXY(final CTXYAdjustHandle handle) {
        this.handle = handle;
    }
    
    @Internal
    public CTXYAdjustHandle getXmlObject() {
        return this.handle;
    }
}
