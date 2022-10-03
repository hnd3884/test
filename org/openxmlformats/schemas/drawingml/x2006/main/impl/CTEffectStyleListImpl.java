package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectStyleItem;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectStyleList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTEffectStyleListImpl extends XmlComplexContentImpl implements CTEffectStyleList
{
    private static final long serialVersionUID = 1L;
    private static final QName EFFECTSTYLE$0;
    
    public CTEffectStyleListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTEffectStyleItem> getEffectStyleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class EffectStyleList extends AbstractList<CTEffectStyleItem>
            {
                @Override
                public CTEffectStyleItem get(final int n) {
                    return CTEffectStyleListImpl.this.getEffectStyleArray(n);
                }
                
                @Override
                public CTEffectStyleItem set(final int n, final CTEffectStyleItem ctEffectStyleItem) {
                    final CTEffectStyleItem effectStyleArray = CTEffectStyleListImpl.this.getEffectStyleArray(n);
                    CTEffectStyleListImpl.this.setEffectStyleArray(n, ctEffectStyleItem);
                    return effectStyleArray;
                }
                
                @Override
                public void add(final int n, final CTEffectStyleItem ctEffectStyleItem) {
                    CTEffectStyleListImpl.this.insertNewEffectStyle(n).set((XmlObject)ctEffectStyleItem);
                }
                
                @Override
                public CTEffectStyleItem remove(final int n) {
                    final CTEffectStyleItem effectStyleArray = CTEffectStyleListImpl.this.getEffectStyleArray(n);
                    CTEffectStyleListImpl.this.removeEffectStyle(n);
                    return effectStyleArray;
                }
                
                @Override
                public int size() {
                    return CTEffectStyleListImpl.this.sizeOfEffectStyleArray();
                }
            }
            return new EffectStyleList();
        }
    }
    
    @Deprecated
    public CTEffectStyleItem[] getEffectStyleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTEffectStyleListImpl.EFFECTSTYLE$0, (List)list);
            final CTEffectStyleItem[] array = new CTEffectStyleItem[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEffectStyleItem getEffectStyleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectStyleItem ctEffectStyleItem = (CTEffectStyleItem)this.get_store().find_element_user(CTEffectStyleListImpl.EFFECTSTYLE$0, n);
            if (ctEffectStyleItem == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEffectStyleItem;
        }
    }
    
    public int sizeOfEffectStyleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectStyleListImpl.EFFECTSTYLE$0);
        }
    }
    
    public void setEffectStyleArray(final CTEffectStyleItem[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectStyleListImpl.EFFECTSTYLE$0);
    }
    
    public void setEffectStyleArray(final int n, final CTEffectStyleItem ctEffectStyleItem) {
        this.generatedSetterHelperImpl((XmlObject)ctEffectStyleItem, CTEffectStyleListImpl.EFFECTSTYLE$0, n, (short)2);
    }
    
    public CTEffectStyleItem insertNewEffectStyle(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectStyleItem)this.get_store().insert_element_user(CTEffectStyleListImpl.EFFECTSTYLE$0, n);
        }
    }
    
    public CTEffectStyleItem addNewEffectStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectStyleItem)this.get_store().add_element_user(CTEffectStyleListImpl.EFFECTSTYLE$0);
        }
    }
    
    public void removeEffectStyle(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectStyleListImpl.EFFECTSTYLE$0, n);
        }
    }
    
    static {
        EFFECTSTYLE$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectStyle");
    }
}
