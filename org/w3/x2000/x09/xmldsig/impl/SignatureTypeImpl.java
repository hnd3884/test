package org.w3.x2000.x09.xmldsig.impl;

import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import java.util.AbstractList;
import org.w3.x2000.x09.xmldsig.ObjectType;
import java.util.List;
import org.w3.x2000.x09.xmldsig.KeyInfoType;
import org.w3.x2000.x09.xmldsig.SignatureValueType;
import org.apache.xmlbeans.XmlObject;
import org.w3.x2000.x09.xmldsig.SignedInfoType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.w3.x2000.x09.xmldsig.SignatureType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SignatureTypeImpl extends XmlComplexContentImpl implements SignatureType
{
    private static final long serialVersionUID = 1L;
    private static final QName SIGNEDINFO$0;
    private static final QName SIGNATUREVALUE$2;
    private static final QName KEYINFO$4;
    private static final QName OBJECT$6;
    private static final QName ID$8;
    
    public SignatureTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public SignedInfoType getSignedInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SignedInfoType signedInfoType = (SignedInfoType)this.get_store().find_element_user(SignatureTypeImpl.SIGNEDINFO$0, 0);
            if (signedInfoType == null) {
                return null;
            }
            return signedInfoType;
        }
    }
    
    public void setSignedInfo(final SignedInfoType signedInfoType) {
        this.generatedSetterHelperImpl((XmlObject)signedInfoType, SignatureTypeImpl.SIGNEDINFO$0, 0, (short)1);
    }
    
    public SignedInfoType addNewSignedInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (SignedInfoType)this.get_store().add_element_user(SignatureTypeImpl.SIGNEDINFO$0);
        }
    }
    
    public SignatureValueType getSignatureValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SignatureValueType signatureValueType = (SignatureValueType)this.get_store().find_element_user(SignatureTypeImpl.SIGNATUREVALUE$2, 0);
            if (signatureValueType == null) {
                return null;
            }
            return signatureValueType;
        }
    }
    
    public void setSignatureValue(final SignatureValueType signatureValueType) {
        this.generatedSetterHelperImpl((XmlObject)signatureValueType, SignatureTypeImpl.SIGNATUREVALUE$2, 0, (short)1);
    }
    
    public SignatureValueType addNewSignatureValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (SignatureValueType)this.get_store().add_element_user(SignatureTypeImpl.SIGNATUREVALUE$2);
        }
    }
    
    public KeyInfoType getKeyInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final KeyInfoType keyInfoType = (KeyInfoType)this.get_store().find_element_user(SignatureTypeImpl.KEYINFO$4, 0);
            if (keyInfoType == null) {
                return null;
            }
            return keyInfoType;
        }
    }
    
    public boolean isSetKeyInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SignatureTypeImpl.KEYINFO$4) != 0;
        }
    }
    
    public void setKeyInfo(final KeyInfoType keyInfoType) {
        this.generatedSetterHelperImpl((XmlObject)keyInfoType, SignatureTypeImpl.KEYINFO$4, 0, (short)1);
    }
    
    public KeyInfoType addNewKeyInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (KeyInfoType)this.get_store().add_element_user(SignatureTypeImpl.KEYINFO$4);
        }
    }
    
    public void unsetKeyInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SignatureTypeImpl.KEYINFO$4, 0);
        }
    }
    
    public List<ObjectType> getObjectList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ObjectList extends AbstractList<ObjectType>
            {
                @Override
                public ObjectType get(final int n) {
                    return SignatureTypeImpl.this.getObjectArray(n);
                }
                
                @Override
                public ObjectType set(final int n, final ObjectType objectType) {
                    final ObjectType objectArray = SignatureTypeImpl.this.getObjectArray(n);
                    SignatureTypeImpl.this.setObjectArray(n, objectType);
                    return objectArray;
                }
                
                @Override
                public void add(final int n, final ObjectType objectType) {
                    SignatureTypeImpl.this.insertNewObject(n).set((XmlObject)objectType);
                }
                
                @Override
                public ObjectType remove(final int n) {
                    final ObjectType objectArray = SignatureTypeImpl.this.getObjectArray(n);
                    SignatureTypeImpl.this.removeObject(n);
                    return objectArray;
                }
                
                @Override
                public int size() {
                    return SignatureTypeImpl.this.sizeOfObjectArray();
                }
            }
            return new ObjectList();
        }
    }
    
    @Deprecated
    public ObjectType[] getObjectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(SignatureTypeImpl.OBJECT$6, (List)list);
            final ObjectType[] array = new ObjectType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public ObjectType getObjectArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ObjectType objectType = (ObjectType)this.get_store().find_element_user(SignatureTypeImpl.OBJECT$6, n);
            if (objectType == null) {
                throw new IndexOutOfBoundsException();
            }
            return objectType;
        }
    }
    
    public int sizeOfObjectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SignatureTypeImpl.OBJECT$6);
        }
    }
    
    public void setObjectArray(final ObjectType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, SignatureTypeImpl.OBJECT$6);
    }
    
    public void setObjectArray(final int n, final ObjectType objectType) {
        this.generatedSetterHelperImpl((XmlObject)objectType, SignatureTypeImpl.OBJECT$6, n, (short)2);
    }
    
    public ObjectType insertNewObject(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ObjectType)this.get_store().insert_element_user(SignatureTypeImpl.OBJECT$6, n);
        }
    }
    
    public ObjectType addNewObject() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ObjectType)this.get_store().add_element_user(SignatureTypeImpl.OBJECT$6);
        }
    }
    
    public void removeObject(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SignatureTypeImpl.OBJECT$6, n);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SignatureTypeImpl.ID$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlID)this.get_store().find_attribute_user(SignatureTypeImpl.ID$8);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(SignatureTypeImpl.ID$8) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SignatureTypeImpl.ID$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(SignatureTypeImpl.ID$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlID xmlID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID xmlID2 = (XmlID)this.get_store().find_attribute_user(SignatureTypeImpl.ID$8);
            if (xmlID2 == null) {
                xmlID2 = (XmlID)this.get_store().add_attribute_user(SignatureTypeImpl.ID$8);
            }
            xmlID2.set((XmlObject)xmlID);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(SignatureTypeImpl.ID$8);
        }
    }
    
    static {
        SIGNEDINFO$0 = new QName("http://www.w3.org/2000/09/xmldsig#", "SignedInfo");
        SIGNATUREVALUE$2 = new QName("http://www.w3.org/2000/09/xmldsig#", "SignatureValue");
        KEYINFO$4 = new QName("http://www.w3.org/2000/09/xmldsig#", "KeyInfo");
        OBJECT$6 = new QName("http://www.w3.org/2000/09/xmldsig#", "Object");
        ID$8 = new QName("", "Id");
    }
}
