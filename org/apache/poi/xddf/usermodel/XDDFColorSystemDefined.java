package org.apache.poi.xddf.usermodel;

import org.apache.xmlbeans.XmlObject;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSystemColor;

public class XDDFColorSystemDefined extends XDDFColor
{
    private CTSystemColor color;
    
    public XDDFColorSystemDefined(final SystemColor color) {
        this(CTSystemColor.Factory.newInstance(), CTColor.Factory.newInstance());
        this.setValue(color);
    }
    
    @Internal
    protected XDDFColorSystemDefined(final CTSystemColor color) {
        this(color, null);
    }
    
    @Internal
    protected XDDFColorSystemDefined(final CTSystemColor color, final CTColor container) {
        super(container);
        this.color = color;
    }
    
    @Internal
    @Override
    protected XmlObject getXmlObject() {
        return (XmlObject)this.color;
    }
    
    public SystemColor getValue() {
        return SystemColor.valueOf(this.color.getVal());
    }
    
    public void setValue(final SystemColor value) {
        this.color.setVal(value.underlying);
    }
    
    public byte[] getLastColor() {
        if (this.color.isSetLastClr()) {
            return this.color.getLastClr();
        }
        return null;
    }
    
    public void setLastColor(final byte[] last) {
        if (last == null) {
            if (this.color.isSetLastClr()) {
                this.color.unsetLastClr();
            }
        }
        else {
            this.color.setLastClr(last);
        }
    }
}
