package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStretchInfoProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTStretchInfoPropertiesImpl extends XmlComplexContentImpl implements CTStretchInfoProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName FILLRECT$0;
    
    public CTStretchInfoPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTRelativeRect getFillRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRelativeRect ctRelativeRect = (CTRelativeRect)this.get_store().find_element_user(CTStretchInfoPropertiesImpl.FILLRECT$0, 0);
            if (ctRelativeRect == null) {
                return null;
            }
            return ctRelativeRect;
        }
    }
    
    public boolean isSetFillRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStretchInfoPropertiesImpl.FILLRECT$0) != 0;
        }
    }
    
    public void setFillRect(final CTRelativeRect ctRelativeRect) {
        this.generatedSetterHelperImpl((XmlObject)ctRelativeRect, CTStretchInfoPropertiesImpl.FILLRECT$0, 0, (short)1);
    }
    
    public CTRelativeRect addNewFillRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRelativeRect)this.get_store().add_element_user(CTStretchInfoPropertiesImpl.FILLRECT$0);
        }
    }
    
    public void unsetFillRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStretchInfoPropertiesImpl.FILLRECT$0, 0);
        }
    }
    
    static {
        FILLRECT$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fillRect");
    }
}
