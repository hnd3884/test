package org.etsi.uri.x01903.v13.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.etsi.uri.x01903.v13.CertIDType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.CertIDListType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CertIDListTypeImpl extends XmlComplexContentImpl implements CertIDListType
{
    private static final long serialVersionUID = 1L;
    private static final QName CERT$0;
    
    public CertIDListTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CertIDType> getCertList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CertList extends AbstractList<CertIDType>
            {
                @Override
                public CertIDType get(final int n) {
                    return CertIDListTypeImpl.this.getCertArray(n);
                }
                
                @Override
                public CertIDType set(final int n, final CertIDType certIDType) {
                    final CertIDType certArray = CertIDListTypeImpl.this.getCertArray(n);
                    CertIDListTypeImpl.this.setCertArray(n, certIDType);
                    return certArray;
                }
                
                @Override
                public void add(final int n, final CertIDType certIDType) {
                    CertIDListTypeImpl.this.insertNewCert(n).set((XmlObject)certIDType);
                }
                
                @Override
                public CertIDType remove(final int n) {
                    final CertIDType certArray = CertIDListTypeImpl.this.getCertArray(n);
                    CertIDListTypeImpl.this.removeCert(n);
                    return certArray;
                }
                
                @Override
                public int size() {
                    return CertIDListTypeImpl.this.sizeOfCertArray();
                }
            }
            return new CertList();
        }
    }
    
    @Deprecated
    public CertIDType[] getCertArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CertIDListTypeImpl.CERT$0, (List)list);
            final CertIDType[] array = new CertIDType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CertIDType getCertArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CertIDType certIDType = (CertIDType)this.get_store().find_element_user(CertIDListTypeImpl.CERT$0, n);
            if (certIDType == null) {
                throw new IndexOutOfBoundsException();
            }
            return certIDType;
        }
    }
    
    public int sizeOfCertArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CertIDListTypeImpl.CERT$0);
        }
    }
    
    public void setCertArray(final CertIDType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CertIDListTypeImpl.CERT$0);
    }
    
    public void setCertArray(final int n, final CertIDType certIDType) {
        this.generatedSetterHelperImpl((XmlObject)certIDType, CertIDListTypeImpl.CERT$0, n, (short)2);
    }
    
    public CertIDType insertNewCert(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CertIDType)this.get_store().insert_element_user(CertIDListTypeImpl.CERT$0, n);
        }
    }
    
    public CertIDType addNewCert() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CertIDType)this.get_store().add_element_user(CertIDListTypeImpl.CERT$0);
        }
    }
    
    public void removeCert(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CertIDListTypeImpl.CERT$0, n);
        }
    }
    
    static {
        CERT$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "Cert");
    }
}
