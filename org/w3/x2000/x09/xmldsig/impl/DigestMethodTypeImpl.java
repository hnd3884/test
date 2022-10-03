package org.w3.x2000.x09.xmldsig.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.w3.x2000.x09.xmldsig.DigestMethodType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class DigestMethodTypeImpl extends XmlComplexContentImpl implements DigestMethodType
{
    private static final long serialVersionUID = 1L;
    private static final QName ALGORITHM$0;
    
    public DigestMethodTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getAlgorithm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(DigestMethodTypeImpl.ALGORITHM$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlAnyURI xgetAlgorithm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlAnyURI)this.get_store().find_attribute_user(DigestMethodTypeImpl.ALGORITHM$0);
        }
    }
    
    public void setAlgorithm(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(DigestMethodTypeImpl.ALGORITHM$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(DigestMethodTypeImpl.ALGORITHM$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetAlgorithm(final XmlAnyURI xmlAnyURI) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI xmlAnyURI2 = (XmlAnyURI)this.get_store().find_attribute_user(DigestMethodTypeImpl.ALGORITHM$0);
            if (xmlAnyURI2 == null) {
                xmlAnyURI2 = (XmlAnyURI)this.get_store().add_attribute_user(DigestMethodTypeImpl.ALGORITHM$0);
            }
            xmlAnyURI2.set((XmlObject)xmlAnyURI);
        }
    }
    
    static {
        ALGORITHM$0 = new QName("", "Algorithm");
    }
}
