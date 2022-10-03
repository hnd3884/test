package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextBulletSizePercent;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletSizePercent;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextBulletSizePercentImpl extends XmlComplexContentImpl implements CTTextBulletSizePercent
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTTextBulletSizePercentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBulletSizePercentImpl.VAL$0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STTextBulletSizePercent xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextBulletSizePercent)this.get_store().find_attribute_user(CTTextBulletSizePercentImpl.VAL$0);
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBulletSizePercentImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBulletSizePercentImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBulletSizePercentImpl.VAL$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetVal(final STTextBulletSizePercent stTextBulletSizePercent) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextBulletSizePercent stTextBulletSizePercent2 = (STTextBulletSizePercent)this.get_store().find_attribute_user(CTTextBulletSizePercentImpl.VAL$0);
            if (stTextBulletSizePercent2 == null) {
                stTextBulletSizePercent2 = (STTextBulletSizePercent)this.get_store().add_attribute_user(CTTextBulletSizePercentImpl.VAL$0);
            }
            stTextBulletSizePercent2.set((XmlObject)stTextBulletSizePercent);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBulletSizePercentImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
