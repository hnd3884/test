package org.apache.poi.xddf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineJoinBevel;

public class XDDFLineJoinBevelProperties implements XDDFLineJoinProperties
{
    private CTLineJoinBevel join;
    
    public XDDFLineJoinBevelProperties() {
        this(CTLineJoinBevel.Factory.newInstance());
    }
    
    protected XDDFLineJoinBevelProperties(final CTLineJoinBevel join) {
        this.join = join;
    }
    
    @Internal
    protected CTLineJoinBevel getXmlObject() {
        return this.join;
    }
}
