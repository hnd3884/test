package org.apache.poi.xddf.usermodel;

import org.apache.xmlbeans.XmlObject;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;

public class XDDFColorSchemeBased extends XDDFColor
{
    private CTSchemeColor color;
    
    public XDDFColorSchemeBased(final SchemeColor color) {
        this(CTSchemeColor.Factory.newInstance(), CTColor.Factory.newInstance());
        this.setValue(color);
    }
    
    @Internal
    protected XDDFColorSchemeBased(final CTSchemeColor color) {
        this(color, null);
    }
    
    @Internal
    protected XDDFColorSchemeBased(final CTSchemeColor color, final CTColor container) {
        super(container);
        this.color = color;
    }
    
    @Internal
    @Override
    protected XmlObject getXmlObject() {
        return (XmlObject)this.color;
    }
    
    public SchemeColor getValue() {
        return SchemeColor.valueOf(this.color.getVal());
    }
    
    public void setValue(final SchemeColor scheme) {
        this.color.setVal(scheme.underlying);
    }
}
