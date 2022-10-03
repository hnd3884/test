package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTField;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFieldImpl extends XmlComplexContentImpl implements CTField
{
    private static final long serialVersionUID = 1L;
    private static final QName X$0;
    
    public CTFieldImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFieldImpl.X$0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_attribute_user(CTFieldImpl.X$0);
        }
    }
    
    public void setX(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFieldImpl.X$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFieldImpl.X$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetX(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(CTFieldImpl.X$0);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(CTFieldImpl.X$0);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    static {
        X$0 = new QName("", "x");
    }
}
