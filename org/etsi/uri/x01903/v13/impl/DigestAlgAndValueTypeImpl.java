package org.etsi.uri.x01903.v13.impl;

import org.w3.x2000.x09.xmldsig.DigestValueType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.w3.x2000.x09.xmldsig.DigestMethodType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.DigestAlgAndValueType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class DigestAlgAndValueTypeImpl extends XmlComplexContentImpl implements DigestAlgAndValueType
{
    private static final long serialVersionUID = 1L;
    private static final QName DIGESTMETHOD$0;
    private static final QName DIGESTVALUE$2;
    
    public DigestAlgAndValueTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public DigestMethodType getDigestMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final DigestMethodType digestMethodType = (DigestMethodType)this.get_store().find_element_user(DigestAlgAndValueTypeImpl.DIGESTMETHOD$0, 0);
            if (digestMethodType == null) {
                return null;
            }
            return digestMethodType;
        }
    }
    
    public void setDigestMethod(final DigestMethodType digestMethodType) {
        this.generatedSetterHelperImpl((XmlObject)digestMethodType, DigestAlgAndValueTypeImpl.DIGESTMETHOD$0, 0, (short)1);
    }
    
    public DigestMethodType addNewDigestMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (DigestMethodType)this.get_store().add_element_user(DigestAlgAndValueTypeImpl.DIGESTMETHOD$0);
        }
    }
    
    public byte[] getDigestValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(DigestAlgAndValueTypeImpl.DIGESTVALUE$2, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public DigestValueType xgetDigestValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (DigestValueType)this.get_store().find_element_user(DigestAlgAndValueTypeImpl.DIGESTVALUE$2, 0);
        }
    }
    
    public void setDigestValue(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(DigestAlgAndValueTypeImpl.DIGESTVALUE$2, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(DigestAlgAndValueTypeImpl.DIGESTVALUE$2);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetDigestValue(final DigestValueType digestValueType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            DigestValueType digestValueType2 = (DigestValueType)this.get_store().find_element_user(DigestAlgAndValueTypeImpl.DIGESTVALUE$2, 0);
            if (digestValueType2 == null) {
                digestValueType2 = (DigestValueType)this.get_store().add_element_user(DigestAlgAndValueTypeImpl.DIGESTVALUE$2);
            }
            digestValueType2.set((XmlObject)digestValueType);
        }
    }
    
    static {
        DIGESTMETHOD$0 = new QName("http://www.w3.org/2000/09/xmldsig#", "DigestMethod");
        DIGESTVALUE$2 = new QName("http://www.w3.org/2000/09/xmldsig#", "DigestValue");
    }
}
