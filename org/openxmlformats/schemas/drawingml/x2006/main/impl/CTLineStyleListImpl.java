package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineStyleList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLineStyleListImpl extends XmlComplexContentImpl implements CTLineStyleList
{
    private static final long serialVersionUID = 1L;
    private static final QName LN$0;
    
    public CTLineStyleListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTLineProperties> getLnList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LnList extends AbstractList<CTLineProperties>
            {
                @Override
                public CTLineProperties get(final int n) {
                    return CTLineStyleListImpl.this.getLnArray(n);
                }
                
                @Override
                public CTLineProperties set(final int n, final CTLineProperties ctLineProperties) {
                    final CTLineProperties lnArray = CTLineStyleListImpl.this.getLnArray(n);
                    CTLineStyleListImpl.this.setLnArray(n, ctLineProperties);
                    return lnArray;
                }
                
                @Override
                public void add(final int n, final CTLineProperties ctLineProperties) {
                    CTLineStyleListImpl.this.insertNewLn(n).set((XmlObject)ctLineProperties);
                }
                
                @Override
                public CTLineProperties remove(final int n) {
                    final CTLineProperties lnArray = CTLineStyleListImpl.this.getLnArray(n);
                    CTLineStyleListImpl.this.removeLn(n);
                    return lnArray;
                }
                
                @Override
                public int size() {
                    return CTLineStyleListImpl.this.sizeOfLnArray();
                }
            }
            return new LnList();
        }
    }
    
    @Deprecated
    public CTLineProperties[] getLnArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTLineStyleListImpl.LN$0, (List)list);
            final CTLineProperties[] array = new CTLineProperties[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTLineProperties getLnArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineProperties ctLineProperties = (CTLineProperties)this.get_store().find_element_user(CTLineStyleListImpl.LN$0, n);
            if (ctLineProperties == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctLineProperties;
        }
    }
    
    public int sizeOfLnArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineStyleListImpl.LN$0);
        }
    }
    
    public void setLnArray(final CTLineProperties[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTLineStyleListImpl.LN$0);
    }
    
    public void setLnArray(final int n, final CTLineProperties ctLineProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctLineProperties, CTLineStyleListImpl.LN$0, n, (short)2);
    }
    
    public CTLineProperties insertNewLn(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineProperties)this.get_store().insert_element_user(CTLineStyleListImpl.LN$0, n);
        }
    }
    
    public CTLineProperties addNewLn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineProperties)this.get_store().add_element_user(CTLineStyleListImpl.LN$0);
        }
    }
    
    public void removeLn(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineStyleListImpl.LN$0, n);
        }
    }
    
    static {
        LN$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ln");
    }
}
