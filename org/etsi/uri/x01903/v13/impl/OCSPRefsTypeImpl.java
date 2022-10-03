package org.etsi.uri.x01903.v13.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.etsi.uri.x01903.v13.OCSPRefType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.OCSPRefsType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class OCSPRefsTypeImpl extends XmlComplexContentImpl implements OCSPRefsType
{
    private static final long serialVersionUID = 1L;
    private static final QName OCSPREF$0;
    
    public OCSPRefsTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<OCSPRefType> getOCSPRefList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OCSPRefList extends AbstractList<OCSPRefType>
            {
                @Override
                public OCSPRefType get(final int n) {
                    return OCSPRefsTypeImpl.this.getOCSPRefArray(n);
                }
                
                @Override
                public OCSPRefType set(final int n, final OCSPRefType ocspRefType) {
                    final OCSPRefType ocspRefArray = OCSPRefsTypeImpl.this.getOCSPRefArray(n);
                    OCSPRefsTypeImpl.this.setOCSPRefArray(n, ocspRefType);
                    return ocspRefArray;
                }
                
                @Override
                public void add(final int n, final OCSPRefType ocspRefType) {
                    OCSPRefsTypeImpl.this.insertNewOCSPRef(n).set((XmlObject)ocspRefType);
                }
                
                @Override
                public OCSPRefType remove(final int n) {
                    final OCSPRefType ocspRefArray = OCSPRefsTypeImpl.this.getOCSPRefArray(n);
                    OCSPRefsTypeImpl.this.removeOCSPRef(n);
                    return ocspRefArray;
                }
                
                @Override
                public int size() {
                    return OCSPRefsTypeImpl.this.sizeOfOCSPRefArray();
                }
            }
            return new OCSPRefList();
        }
    }
    
    @Deprecated
    public OCSPRefType[] getOCSPRefArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(OCSPRefsTypeImpl.OCSPREF$0, (List)list);
            final OCSPRefType[] array = new OCSPRefType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public OCSPRefType getOCSPRefArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final OCSPRefType ocspRefType = (OCSPRefType)this.get_store().find_element_user(OCSPRefsTypeImpl.OCSPREF$0, n);
            if (ocspRefType == null) {
                throw new IndexOutOfBoundsException();
            }
            return ocspRefType;
        }
    }
    
    public int sizeOfOCSPRefArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(OCSPRefsTypeImpl.OCSPREF$0);
        }
    }
    
    public void setOCSPRefArray(final OCSPRefType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, OCSPRefsTypeImpl.OCSPREF$0);
    }
    
    public void setOCSPRefArray(final int n, final OCSPRefType ocspRefType) {
        this.generatedSetterHelperImpl((XmlObject)ocspRefType, OCSPRefsTypeImpl.OCSPREF$0, n, (short)2);
    }
    
    public OCSPRefType insertNewOCSPRef(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (OCSPRefType)this.get_store().insert_element_user(OCSPRefsTypeImpl.OCSPREF$0, n);
        }
    }
    
    public OCSPRefType addNewOCSPRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (OCSPRefType)this.get_store().add_element_user(OCSPRefsTypeImpl.OCSPREF$0);
        }
    }
    
    public void removeOCSPRef(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(OCSPRefsTypeImpl.OCSPREF$0, n);
        }
    }
    
    static {
        OCSPREF$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "OCSPRef");
    }
}
