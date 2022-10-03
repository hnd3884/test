package org.openxmlformats.schemas.xpackage.x2006.digitalSignature.impl;

import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.STValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.STFormat;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.CTSignatureTime;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSignatureTimeImpl extends XmlComplexContentImpl implements CTSignatureTime
{
    private static final long serialVersionUID = 1L;
    private static final QName FORMAT$0;
    private static final QName VALUE$2;
    
    public CTSignatureTimeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureTimeImpl.FORMAT$0, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STFormat xgetFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STFormat)this.get_store().find_element_user(CTSignatureTimeImpl.FORMAT$0, 0);
        }
    }
    
    public void setFormat(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureTimeImpl.FORMAT$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureTimeImpl.FORMAT$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFormat(final STFormat stFormat) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFormat stFormat2 = (STFormat)this.get_store().find_element_user(CTSignatureTimeImpl.FORMAT$0, 0);
            if (stFormat2 == null) {
                stFormat2 = (STFormat)this.get_store().add_element_user(CTSignatureTimeImpl.FORMAT$0);
            }
            stFormat2.set((XmlObject)stFormat);
        }
    }
    
    public String getValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureTimeImpl.VALUE$2, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STValue xgetValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STValue)this.get_store().find_element_user(CTSignatureTimeImpl.VALUE$2, 0);
        }
    }
    
    public void setValue(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureTimeImpl.VALUE$2, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureTimeImpl.VALUE$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetValue(final STValue stValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STValue stValue2 = (STValue)this.get_store().find_element_user(CTSignatureTimeImpl.VALUE$2, 0);
            if (stValue2 == null) {
                stValue2 = (STValue)this.get_store().add_element_user(CTSignatureTimeImpl.VALUE$2);
            }
            stValue2.set((XmlObject)stValue);
        }
    }
    
    static {
        FORMAT$0 = new QName("http://schemas.openxmlformats.org/package/2006/digital-signature", "Format");
        VALUE$2 = new QName("http://schemas.openxmlformats.org/package/2006/digital-signature", "Value");
    }
}
