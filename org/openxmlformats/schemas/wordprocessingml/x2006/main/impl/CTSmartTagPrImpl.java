package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAttr;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSmartTagPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSmartTagPrImpl extends XmlComplexContentImpl implements CTSmartTagPr
{
    private static final long serialVersionUID = 1L;
    private static final QName ATTR$0;
    
    public CTSmartTagPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTAttr> getAttrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AttrList extends AbstractList<CTAttr>
            {
                @Override
                public CTAttr get(final int n) {
                    return CTSmartTagPrImpl.this.getAttrArray(n);
                }
                
                @Override
                public CTAttr set(final int n, final CTAttr ctAttr) {
                    final CTAttr attrArray = CTSmartTagPrImpl.this.getAttrArray(n);
                    CTSmartTagPrImpl.this.setAttrArray(n, ctAttr);
                    return attrArray;
                }
                
                @Override
                public void add(final int n, final CTAttr ctAttr) {
                    CTSmartTagPrImpl.this.insertNewAttr(n).set((XmlObject)ctAttr);
                }
                
                @Override
                public CTAttr remove(final int n) {
                    final CTAttr attrArray = CTSmartTagPrImpl.this.getAttrArray(n);
                    CTSmartTagPrImpl.this.removeAttr(n);
                    return attrArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagPrImpl.this.sizeOfAttrArray();
                }
            }
            return new AttrList();
        }
    }
    
    @Deprecated
    public CTAttr[] getAttrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagPrImpl.ATTR$0, (List)list);
            final CTAttr[] array = new CTAttr[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAttr getAttrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAttr ctAttr = (CTAttr)this.get_store().find_element_user(CTSmartTagPrImpl.ATTR$0, n);
            if (ctAttr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAttr;
        }
    }
    
    public int sizeOfAttrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagPrImpl.ATTR$0);
        }
    }
    
    public void setAttrArray(final CTAttr[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagPrImpl.ATTR$0);
    }
    
    public void setAttrArray(final int n, final CTAttr ctAttr) {
        this.generatedSetterHelperImpl((XmlObject)ctAttr, CTSmartTagPrImpl.ATTR$0, n, (short)2);
    }
    
    public CTAttr insertNewAttr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAttr)this.get_store().insert_element_user(CTSmartTagPrImpl.ATTR$0, n);
        }
    }
    
    public CTAttr addNewAttr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAttr)this.get_store().add_element_user(CTSmartTagPrImpl.ATTR$0);
        }
    }
    
    public void removeAttr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagPrImpl.ATTR$0, n);
        }
    }
    
    static {
        ATTR$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "attr");
    }
}
