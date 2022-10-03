package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextFontSize;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletSizePoint;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextBulletSizePointImpl extends XmlComplexContentImpl implements CTTextBulletSizePoint
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTTextBulletSizePointImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBulletSizePointImpl.VAL$0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STTextFontSize xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextFontSize)this.get_store().find_attribute_user(CTTextBulletSizePointImpl.VAL$0);
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBulletSizePointImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBulletSizePointImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBulletSizePointImpl.VAL$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetVal(final STTextFontSize stTextFontSize) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextFontSize stTextFontSize2 = (STTextFontSize)this.get_store().find_attribute_user(CTTextBulletSizePointImpl.VAL$0);
            if (stTextFontSize2 == null) {
                stTextFontSize2 = (STTextFontSize)this.get_store().add_attribute_user(CTTextBulletSizePointImpl.VAL$0);
            }
            stTextFontSize2.set((XmlObject)stTextFontSize);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBulletSizePointImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
