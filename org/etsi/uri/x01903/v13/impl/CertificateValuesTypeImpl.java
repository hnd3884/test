package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SimpleValue;
import org.etsi.uri.x01903.v13.AnyType;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.etsi.uri.x01903.v13.EncapsulatedPKIDataType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.CertificateValuesType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CertificateValuesTypeImpl extends XmlComplexContentImpl implements CertificateValuesType
{
    private static final long serialVersionUID = 1L;
    private static final QName ENCAPSULATEDX509CERTIFICATE$0;
    private static final QName OTHERCERTIFICATE$2;
    private static final QName ID$4;
    
    public CertificateValuesTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<EncapsulatedPKIDataType> getEncapsulatedX509CertificateList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class EncapsulatedX509CertificateList extends AbstractList<EncapsulatedPKIDataType>
            {
                @Override
                public EncapsulatedPKIDataType get(final int n) {
                    return CertificateValuesTypeImpl.this.getEncapsulatedX509CertificateArray(n);
                }
                
                @Override
                public EncapsulatedPKIDataType set(final int n, final EncapsulatedPKIDataType encapsulatedPKIDataType) {
                    final EncapsulatedPKIDataType encapsulatedX509CertificateArray = CertificateValuesTypeImpl.this.getEncapsulatedX509CertificateArray(n);
                    CertificateValuesTypeImpl.this.setEncapsulatedX509CertificateArray(n, encapsulatedPKIDataType);
                    return encapsulatedX509CertificateArray;
                }
                
                @Override
                public void add(final int n, final EncapsulatedPKIDataType encapsulatedPKIDataType) {
                    CertificateValuesTypeImpl.this.insertNewEncapsulatedX509Certificate(n).set((XmlObject)encapsulatedPKIDataType);
                }
                
                @Override
                public EncapsulatedPKIDataType remove(final int n) {
                    final EncapsulatedPKIDataType encapsulatedX509CertificateArray = CertificateValuesTypeImpl.this.getEncapsulatedX509CertificateArray(n);
                    CertificateValuesTypeImpl.this.removeEncapsulatedX509Certificate(n);
                    return encapsulatedX509CertificateArray;
                }
                
                @Override
                public int size() {
                    return CertificateValuesTypeImpl.this.sizeOfEncapsulatedX509CertificateArray();
                }
            }
            return new EncapsulatedX509CertificateList();
        }
    }
    
    @Deprecated
    public EncapsulatedPKIDataType[] getEncapsulatedX509CertificateArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CertificateValuesTypeImpl.ENCAPSULATEDX509CERTIFICATE$0, (List)list);
            final EncapsulatedPKIDataType[] array = new EncapsulatedPKIDataType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public EncapsulatedPKIDataType getEncapsulatedX509CertificateArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final EncapsulatedPKIDataType encapsulatedPKIDataType = (EncapsulatedPKIDataType)this.get_store().find_element_user(CertificateValuesTypeImpl.ENCAPSULATEDX509CERTIFICATE$0, n);
            if (encapsulatedPKIDataType == null) {
                throw new IndexOutOfBoundsException();
            }
            return encapsulatedPKIDataType;
        }
    }
    
    public int sizeOfEncapsulatedX509CertificateArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CertificateValuesTypeImpl.ENCAPSULATEDX509CERTIFICATE$0);
        }
    }
    
    public void setEncapsulatedX509CertificateArray(final EncapsulatedPKIDataType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CertificateValuesTypeImpl.ENCAPSULATEDX509CERTIFICATE$0);
    }
    
    public void setEncapsulatedX509CertificateArray(final int n, final EncapsulatedPKIDataType encapsulatedPKIDataType) {
        this.generatedSetterHelperImpl((XmlObject)encapsulatedPKIDataType, CertificateValuesTypeImpl.ENCAPSULATEDX509CERTIFICATE$0, n, (short)2);
    }
    
    public EncapsulatedPKIDataType insertNewEncapsulatedX509Certificate(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (EncapsulatedPKIDataType)this.get_store().insert_element_user(CertificateValuesTypeImpl.ENCAPSULATEDX509CERTIFICATE$0, n);
        }
    }
    
    public EncapsulatedPKIDataType addNewEncapsulatedX509Certificate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (EncapsulatedPKIDataType)this.get_store().add_element_user(CertificateValuesTypeImpl.ENCAPSULATEDX509CERTIFICATE$0);
        }
    }
    
    public void removeEncapsulatedX509Certificate(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CertificateValuesTypeImpl.ENCAPSULATEDX509CERTIFICATE$0, n);
        }
    }
    
    public List<AnyType> getOtherCertificateList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OtherCertificateList extends AbstractList<AnyType>
            {
                @Override
                public AnyType get(final int n) {
                    return CertificateValuesTypeImpl.this.getOtherCertificateArray(n);
                }
                
                @Override
                public AnyType set(final int n, final AnyType anyType) {
                    final AnyType otherCertificateArray = CertificateValuesTypeImpl.this.getOtherCertificateArray(n);
                    CertificateValuesTypeImpl.this.setOtherCertificateArray(n, anyType);
                    return otherCertificateArray;
                }
                
                @Override
                public void add(final int n, final AnyType anyType) {
                    CertificateValuesTypeImpl.this.insertNewOtherCertificate(n).set((XmlObject)anyType);
                }
                
                @Override
                public AnyType remove(final int n) {
                    final AnyType otherCertificateArray = CertificateValuesTypeImpl.this.getOtherCertificateArray(n);
                    CertificateValuesTypeImpl.this.removeOtherCertificate(n);
                    return otherCertificateArray;
                }
                
                @Override
                public int size() {
                    return CertificateValuesTypeImpl.this.sizeOfOtherCertificateArray();
                }
            }
            return new OtherCertificateList();
        }
    }
    
    @Deprecated
    public AnyType[] getOtherCertificateArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CertificateValuesTypeImpl.OTHERCERTIFICATE$2, (List)list);
            final AnyType[] array = new AnyType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public AnyType getOtherCertificateArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final AnyType anyType = (AnyType)this.get_store().find_element_user(CertificateValuesTypeImpl.OTHERCERTIFICATE$2, n);
            if (anyType == null) {
                throw new IndexOutOfBoundsException();
            }
            return anyType;
        }
    }
    
    public int sizeOfOtherCertificateArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CertificateValuesTypeImpl.OTHERCERTIFICATE$2);
        }
    }
    
    public void setOtherCertificateArray(final AnyType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CertificateValuesTypeImpl.OTHERCERTIFICATE$2);
    }
    
    public void setOtherCertificateArray(final int n, final AnyType anyType) {
        this.generatedSetterHelperImpl((XmlObject)anyType, CertificateValuesTypeImpl.OTHERCERTIFICATE$2, n, (short)2);
    }
    
    public AnyType insertNewOtherCertificate(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (AnyType)this.get_store().insert_element_user(CertificateValuesTypeImpl.OTHERCERTIFICATE$2, n);
        }
    }
    
    public AnyType addNewOtherCertificate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (AnyType)this.get_store().add_element_user(CertificateValuesTypeImpl.OTHERCERTIFICATE$2);
        }
    }
    
    public void removeOtherCertificate(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CertificateValuesTypeImpl.OTHERCERTIFICATE$2, n);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CertificateValuesTypeImpl.ID$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlID)this.get_store().find_attribute_user(CertificateValuesTypeImpl.ID$4);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CertificateValuesTypeImpl.ID$4) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CertificateValuesTypeImpl.ID$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CertificateValuesTypeImpl.ID$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlID xmlID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID xmlID2 = (XmlID)this.get_store().find_attribute_user(CertificateValuesTypeImpl.ID$4);
            if (xmlID2 == null) {
                xmlID2 = (XmlID)this.get_store().add_attribute_user(CertificateValuesTypeImpl.ID$4);
            }
            xmlID2.set((XmlObject)xmlID);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CertificateValuesTypeImpl.ID$4);
        }
    }
    
    static {
        ENCAPSULATEDX509CERTIFICATE$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "EncapsulatedX509Certificate");
        OTHERCERTIFICATE$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "OtherCertificate");
        ID$4 = new QName("", "Id");
    }
}
