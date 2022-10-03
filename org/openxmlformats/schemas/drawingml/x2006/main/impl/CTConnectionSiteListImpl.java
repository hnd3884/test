package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnectionSite;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnectionSiteList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTConnectionSiteListImpl extends XmlComplexContentImpl implements CTConnectionSiteList
{
    private static final long serialVersionUID = 1L;
    private static final QName CXN$0;
    
    public CTConnectionSiteListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTConnectionSite> getCxnList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CxnList extends AbstractList<CTConnectionSite>
            {
                @Override
                public CTConnectionSite get(final int n) {
                    return CTConnectionSiteListImpl.this.getCxnArray(n);
                }
                
                @Override
                public CTConnectionSite set(final int n, final CTConnectionSite ctConnectionSite) {
                    final CTConnectionSite cxnArray = CTConnectionSiteListImpl.this.getCxnArray(n);
                    CTConnectionSiteListImpl.this.setCxnArray(n, ctConnectionSite);
                    return cxnArray;
                }
                
                @Override
                public void add(final int n, final CTConnectionSite ctConnectionSite) {
                    CTConnectionSiteListImpl.this.insertNewCxn(n).set((XmlObject)ctConnectionSite);
                }
                
                @Override
                public CTConnectionSite remove(final int n) {
                    final CTConnectionSite cxnArray = CTConnectionSiteListImpl.this.getCxnArray(n);
                    CTConnectionSiteListImpl.this.removeCxn(n);
                    return cxnArray;
                }
                
                @Override
                public int size() {
                    return CTConnectionSiteListImpl.this.sizeOfCxnArray();
                }
            }
            return new CxnList();
        }
    }
    
    @Deprecated
    public CTConnectionSite[] getCxnArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTConnectionSiteListImpl.CXN$0, (List)list);
            final CTConnectionSite[] array = new CTConnectionSite[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTConnectionSite getCxnArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTConnectionSite ctConnectionSite = (CTConnectionSite)this.get_store().find_element_user(CTConnectionSiteListImpl.CXN$0, n);
            if (ctConnectionSite == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctConnectionSite;
        }
    }
    
    public int sizeOfCxnArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTConnectionSiteListImpl.CXN$0);
        }
    }
    
    public void setCxnArray(final CTConnectionSite[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTConnectionSiteListImpl.CXN$0);
    }
    
    public void setCxnArray(final int n, final CTConnectionSite ctConnectionSite) {
        this.generatedSetterHelperImpl((XmlObject)ctConnectionSite, CTConnectionSiteListImpl.CXN$0, n, (short)2);
    }
    
    public CTConnectionSite insertNewCxn(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTConnectionSite)this.get_store().insert_element_user(CTConnectionSiteListImpl.CXN$0, n);
        }
    }
    
    public CTConnectionSite addNewCxn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTConnectionSite)this.get_store().add_element_user(CTConnectionSiteListImpl.CXN$0);
        }
    }
    
    public void removeCxn(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTConnectionSiteListImpl.CXN$0, n);
        }
    }
    
    static {
        CXN$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "cxn");
    }
}
