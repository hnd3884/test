package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.STPathShadeType;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPathShadeProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPathShadePropertiesImpl extends XmlComplexContentImpl implements CTPathShadeProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName FILLTORECT$0;
    private static final QName PATH$2;
    
    public CTPathShadePropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTRelativeRect getFillToRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRelativeRect ctRelativeRect = (CTRelativeRect)this.get_store().find_element_user(CTPathShadePropertiesImpl.FILLTORECT$0, 0);
            if (ctRelativeRect == null) {
                return null;
            }
            return ctRelativeRect;
        }
    }
    
    public boolean isSetFillToRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPathShadePropertiesImpl.FILLTORECT$0) != 0;
        }
    }
    
    public void setFillToRect(final CTRelativeRect ctRelativeRect) {
        this.generatedSetterHelperImpl((XmlObject)ctRelativeRect, CTPathShadePropertiesImpl.FILLTORECT$0, 0, (short)1);
    }
    
    public CTRelativeRect addNewFillToRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRelativeRect)this.get_store().add_element_user(CTPathShadePropertiesImpl.FILLTORECT$0);
        }
    }
    
    public void unsetFillToRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPathShadePropertiesImpl.FILLTORECT$0, 0);
        }
    }
    
    public STPathShadeType.Enum getPath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathShadePropertiesImpl.PATH$2);
            if (simpleValue == null) {
                return null;
            }
            return (STPathShadeType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPathShadeType xgetPath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPathShadeType)this.get_store().find_attribute_user(CTPathShadePropertiesImpl.PATH$2);
        }
    }
    
    public boolean isSetPath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPathShadePropertiesImpl.PATH$2) != null;
        }
    }
    
    public void setPath(final STPathShadeType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPathShadePropertiesImpl.PATH$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPathShadePropertiesImpl.PATH$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetPath(final STPathShadeType stPathShadeType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPathShadeType stPathShadeType2 = (STPathShadeType)this.get_store().find_attribute_user(CTPathShadePropertiesImpl.PATH$2);
            if (stPathShadeType2 == null) {
                stPathShadeType2 = (STPathShadeType)this.get_store().add_attribute_user(CTPathShadePropertiesImpl.PATH$2);
            }
            stPathShadeType2.set((XmlObject)stPathShadeType);
        }
    }
    
    public void unsetPath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPathShadePropertiesImpl.PATH$2);
        }
    }
    
    static {
        FILLTORECT$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fillToRect");
        PATH$2 = new QName("", "path");
    }
}
