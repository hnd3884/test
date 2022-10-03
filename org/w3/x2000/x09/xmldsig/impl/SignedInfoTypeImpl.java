package org.w3.x2000.x09.xmldsig.impl;

import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import java.util.AbstractList;
import org.w3.x2000.x09.xmldsig.ReferenceType;
import java.util.List;
import org.w3.x2000.x09.xmldsig.SignatureMethodType;
import org.apache.xmlbeans.XmlObject;
import org.w3.x2000.x09.xmldsig.CanonicalizationMethodType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.w3.x2000.x09.xmldsig.SignedInfoType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SignedInfoTypeImpl extends XmlComplexContentImpl implements SignedInfoType
{
    private static final long serialVersionUID = 1L;
    private static final QName CANONICALIZATIONMETHOD$0;
    private static final QName SIGNATUREMETHOD$2;
    private static final QName REFERENCE$4;
    private static final QName ID$6;
    
    public SignedInfoTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CanonicalizationMethodType getCanonicalizationMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CanonicalizationMethodType canonicalizationMethodType = (CanonicalizationMethodType)this.get_store().find_element_user(SignedInfoTypeImpl.CANONICALIZATIONMETHOD$0, 0);
            if (canonicalizationMethodType == null) {
                return null;
            }
            return canonicalizationMethodType;
        }
    }
    
    public void setCanonicalizationMethod(final CanonicalizationMethodType canonicalizationMethodType) {
        this.generatedSetterHelperImpl((XmlObject)canonicalizationMethodType, SignedInfoTypeImpl.CANONICALIZATIONMETHOD$0, 0, (short)1);
    }
    
    public CanonicalizationMethodType addNewCanonicalizationMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CanonicalizationMethodType)this.get_store().add_element_user(SignedInfoTypeImpl.CANONICALIZATIONMETHOD$0);
        }
    }
    
    public SignatureMethodType getSignatureMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SignatureMethodType signatureMethodType = (SignatureMethodType)this.get_store().find_element_user(SignedInfoTypeImpl.SIGNATUREMETHOD$2, 0);
            if (signatureMethodType == null) {
                return null;
            }
            return signatureMethodType;
        }
    }
    
    public void setSignatureMethod(final SignatureMethodType signatureMethodType) {
        this.generatedSetterHelperImpl((XmlObject)signatureMethodType, SignedInfoTypeImpl.SIGNATUREMETHOD$2, 0, (short)1);
    }
    
    public SignatureMethodType addNewSignatureMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (SignatureMethodType)this.get_store().add_element_user(SignedInfoTypeImpl.SIGNATUREMETHOD$2);
        }
    }
    
    public List<ReferenceType> getReferenceList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ReferenceList extends AbstractList<ReferenceType>
            {
                @Override
                public ReferenceType get(final int n) {
                    return SignedInfoTypeImpl.this.getReferenceArray(n);
                }
                
                @Override
                public ReferenceType set(final int n, final ReferenceType referenceType) {
                    final ReferenceType referenceArray = SignedInfoTypeImpl.this.getReferenceArray(n);
                    SignedInfoTypeImpl.this.setReferenceArray(n, referenceType);
                    return referenceArray;
                }
                
                @Override
                public void add(final int n, final ReferenceType referenceType) {
                    SignedInfoTypeImpl.this.insertNewReference(n).set((XmlObject)referenceType);
                }
                
                @Override
                public ReferenceType remove(final int n) {
                    final ReferenceType referenceArray = SignedInfoTypeImpl.this.getReferenceArray(n);
                    SignedInfoTypeImpl.this.removeReference(n);
                    return referenceArray;
                }
                
                @Override
                public int size() {
                    return SignedInfoTypeImpl.this.sizeOfReferenceArray();
                }
            }
            return new ReferenceList();
        }
    }
    
    @Deprecated
    public ReferenceType[] getReferenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(SignedInfoTypeImpl.REFERENCE$4, (List)list);
            final ReferenceType[] array = new ReferenceType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public ReferenceType getReferenceArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ReferenceType referenceType = (ReferenceType)this.get_store().find_element_user(SignedInfoTypeImpl.REFERENCE$4, n);
            if (referenceType == null) {
                throw new IndexOutOfBoundsException();
            }
            return referenceType;
        }
    }
    
    public int sizeOfReferenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SignedInfoTypeImpl.REFERENCE$4);
        }
    }
    
    public void setReferenceArray(final ReferenceType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, SignedInfoTypeImpl.REFERENCE$4);
    }
    
    public void setReferenceArray(final int n, final ReferenceType referenceType) {
        this.generatedSetterHelperImpl((XmlObject)referenceType, SignedInfoTypeImpl.REFERENCE$4, n, (short)2);
    }
    
    public ReferenceType insertNewReference(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ReferenceType)this.get_store().insert_element_user(SignedInfoTypeImpl.REFERENCE$4, n);
        }
    }
    
    public ReferenceType addNewReference() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ReferenceType)this.get_store().add_element_user(SignedInfoTypeImpl.REFERENCE$4);
        }
    }
    
    public void removeReference(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SignedInfoTypeImpl.REFERENCE$4, n);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SignedInfoTypeImpl.ID$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlID)this.get_store().find_attribute_user(SignedInfoTypeImpl.ID$6);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(SignedInfoTypeImpl.ID$6) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SignedInfoTypeImpl.ID$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(SignedInfoTypeImpl.ID$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlID xmlID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID xmlID2 = (XmlID)this.get_store().find_attribute_user(SignedInfoTypeImpl.ID$6);
            if (xmlID2 == null) {
                xmlID2 = (XmlID)this.get_store().add_attribute_user(SignedInfoTypeImpl.ID$6);
            }
            xmlID2.set((XmlObject)xmlID);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(SignedInfoTypeImpl.ID$6);
        }
    }
    
    static {
        CANONICALIZATIONMETHOD$0 = new QName("http://www.w3.org/2000/09/xmldsig#", "CanonicalizationMethod");
        SIGNATUREMETHOD$2 = new QName("http://www.w3.org/2000/09/xmldsig#", "SignatureMethod");
        REFERENCE$4 = new QName("http://www.w3.org/2000/09/xmldsig#", "Reference");
        ID$6 = new QName("", "Id");
    }
}
