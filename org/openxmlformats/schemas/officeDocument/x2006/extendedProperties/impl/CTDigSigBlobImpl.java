package org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTDigSigBlob;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDigSigBlobImpl extends XmlComplexContentImpl implements CTDigSigBlob
{
    private static final long serialVersionUID = 1L;
    private static final QName BLOB$0;
    
    public CTDigSigBlobImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public byte[] getBlob() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTDigSigBlobImpl.BLOB$0, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetBlob() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_element_user(CTDigSigBlobImpl.BLOB$0, 0);
        }
    }
    
    public void setBlob(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTDigSigBlobImpl.BLOB$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTDigSigBlobImpl.BLOB$0);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetBlob(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_element_user(CTDigSigBlobImpl.BLOB$0, 0);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_element_user(CTDigSigBlobImpl.BLOB$0);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    static {
        BLOB$0 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "blob");
    }
}
