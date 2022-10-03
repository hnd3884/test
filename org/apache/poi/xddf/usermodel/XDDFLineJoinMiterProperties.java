package org.apache.poi.xddf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineJoinMiterProperties;

public class XDDFLineJoinMiterProperties implements XDDFLineJoinProperties
{
    private CTLineJoinMiterProperties join;
    
    public XDDFLineJoinMiterProperties() {
        this(CTLineJoinMiterProperties.Factory.newInstance());
    }
    
    protected XDDFLineJoinMiterProperties(final CTLineJoinMiterProperties join) {
        this.join = join;
    }
    
    @Internal
    protected CTLineJoinMiterProperties getXmlObject() {
        return this.join;
    }
    
    public Integer getLimit() {
        if (this.join.isSetLim()) {
            return this.join.getLim();
        }
        return null;
    }
    
    public void setLimit(final Integer limit) {
        if (limit == null) {
            if (this.join.isSetLim()) {
                this.join.unsetLim();
            }
        }
        else {
            this.join.setLim((int)limit);
        }
    }
}
