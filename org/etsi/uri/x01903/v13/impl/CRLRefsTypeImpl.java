package org.etsi.uri.x01903.v13.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.etsi.uri.x01903.v13.CRLRefType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.CRLRefsType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CRLRefsTypeImpl extends XmlComplexContentImpl implements CRLRefsType
{
    private static final long serialVersionUID = 1L;
    private static final QName CRLREF$0;
    
    public CRLRefsTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CRLRefType> getCRLRefList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CRLRefList extends AbstractList<CRLRefType>
            {
                @Override
                public CRLRefType get(final int n) {
                    return CRLRefsTypeImpl.this.getCRLRefArray(n);
                }
                
                @Override
                public CRLRefType set(final int n, final CRLRefType crlRefType) {
                    final CRLRefType crlRefArray = CRLRefsTypeImpl.this.getCRLRefArray(n);
                    CRLRefsTypeImpl.this.setCRLRefArray(n, crlRefType);
                    return crlRefArray;
                }
                
                @Override
                public void add(final int n, final CRLRefType crlRefType) {
                    CRLRefsTypeImpl.this.insertNewCRLRef(n).set((XmlObject)crlRefType);
                }
                
                @Override
                public CRLRefType remove(final int n) {
                    final CRLRefType crlRefArray = CRLRefsTypeImpl.this.getCRLRefArray(n);
                    CRLRefsTypeImpl.this.removeCRLRef(n);
                    return crlRefArray;
                }
                
                @Override
                public int size() {
                    return CRLRefsTypeImpl.this.sizeOfCRLRefArray();
                }
            }
            return new CRLRefList();
        }
    }
    
    @Deprecated
    public CRLRefType[] getCRLRefArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CRLRefsTypeImpl.CRLREF$0, (List)list);
            final CRLRefType[] array = new CRLRefType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CRLRefType getCRLRefArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CRLRefType crlRefType = (CRLRefType)this.get_store().find_element_user(CRLRefsTypeImpl.CRLREF$0, n);
            if (crlRefType == null) {
                throw new IndexOutOfBoundsException();
            }
            return crlRefType;
        }
    }
    
    public int sizeOfCRLRefArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CRLRefsTypeImpl.CRLREF$0);
        }
    }
    
    public void setCRLRefArray(final CRLRefType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CRLRefsTypeImpl.CRLREF$0);
    }
    
    public void setCRLRefArray(final int n, final CRLRefType crlRefType) {
        this.generatedSetterHelperImpl((XmlObject)crlRefType, CRLRefsTypeImpl.CRLREF$0, n, (short)2);
    }
    
    public CRLRefType insertNewCRLRef(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CRLRefType)this.get_store().insert_element_user(CRLRefsTypeImpl.CRLREF$0, n);
        }
    }
    
    public CRLRefType addNewCRLRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CRLRefType)this.get_store().add_element_user(CRLRefsTypeImpl.CRLREF$0);
        }
    }
    
    public void removeCRLRef(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CRLRefsTypeImpl.CRLREF$0, n);
        }
    }
    
    static {
        CRLREF$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "CRLRef");
    }
}
