package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFontSize;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFontSizeImpl extends XmlComplexContentImpl implements CTFontSize
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTFontSizeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public double getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontSizeImpl.VAL$0);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTFontSizeImpl.VAL$0);
        }
    }
    
    public void setVal(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontSizeImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFontSizeImpl.VAL$0);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetVal(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTFontSizeImpl.VAL$0);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTFontSizeImpl.VAL$0);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
