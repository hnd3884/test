package org.apache.poi.xddf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineJoinRound;

public class XDDFLineJoinRoundProperties implements XDDFLineJoinProperties
{
    private CTLineJoinRound join;
    
    public XDDFLineJoinRoundProperties() {
        this(CTLineJoinRound.Factory.newInstance());
    }
    
    protected XDDFLineJoinRoundProperties(final CTLineJoinRound join) {
        this.join = join;
    }
    
    @Internal
    protected CTLineJoinRound getXmlObject() {
        return this.join;
    }
}
