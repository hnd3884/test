package org.apache.poi.xddf.usermodel.text;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacing;

public abstract class XDDFSpacing
{
    protected CTTextSpacing spacing;
    
    @Internal
    protected XDDFSpacing(final CTTextSpacing spacing) {
        this.spacing = spacing;
    }
    
    public abstract Kind getType();
    
    @Internal
    protected CTTextSpacing getXmlObject() {
        return this.spacing;
    }
    
    public enum Kind
    {
        PERCENT, 
        POINTS;
    }
}
