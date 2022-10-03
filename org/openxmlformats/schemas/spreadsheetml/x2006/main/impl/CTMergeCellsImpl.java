package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMergeCell;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMergeCells;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTMergeCellsImpl extends XmlComplexContentImpl implements CTMergeCells
{
    private static final long serialVersionUID = 1L;
    private static final QName MERGECELL$0;
    private static final QName COUNT$2;
    
    public CTMergeCellsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTMergeCell> getMergeCellList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MergeCellList extends AbstractList<CTMergeCell>
            {
                @Override
                public CTMergeCell get(final int n) {
                    return CTMergeCellsImpl.this.getMergeCellArray(n);
                }
                
                @Override
                public CTMergeCell set(final int n, final CTMergeCell ctMergeCell) {
                    final CTMergeCell mergeCellArray = CTMergeCellsImpl.this.getMergeCellArray(n);
                    CTMergeCellsImpl.this.setMergeCellArray(n, ctMergeCell);
                    return mergeCellArray;
                }
                
                @Override
                public void add(final int n, final CTMergeCell ctMergeCell) {
                    CTMergeCellsImpl.this.insertNewMergeCell(n).set((XmlObject)ctMergeCell);
                }
                
                @Override
                public CTMergeCell remove(final int n) {
                    final CTMergeCell mergeCellArray = CTMergeCellsImpl.this.getMergeCellArray(n);
                    CTMergeCellsImpl.this.removeMergeCell(n);
                    return mergeCellArray;
                }
                
                @Override
                public int size() {
                    return CTMergeCellsImpl.this.sizeOfMergeCellArray();
                }
            }
            return new MergeCellList();
        }
    }
    
    @Deprecated
    public CTMergeCell[] getMergeCellArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTMergeCellsImpl.MERGECELL$0, (List)list);
            final CTMergeCell[] array = new CTMergeCell[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMergeCell getMergeCellArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMergeCell ctMergeCell = (CTMergeCell)this.get_store().find_element_user(CTMergeCellsImpl.MERGECELL$0, n);
            if (ctMergeCell == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMergeCell;
        }
    }
    
    public int sizeOfMergeCellArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTMergeCellsImpl.MERGECELL$0);
        }
    }
    
    public void setMergeCellArray(final CTMergeCell[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTMergeCellsImpl.MERGECELL$0);
    }
    
    public void setMergeCellArray(final int n, final CTMergeCell ctMergeCell) {
        this.generatedSetterHelperImpl((XmlObject)ctMergeCell, CTMergeCellsImpl.MERGECELL$0, n, (short)2);
    }
    
    public CTMergeCell insertNewMergeCell(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMergeCell)this.get_store().insert_element_user(CTMergeCellsImpl.MERGECELL$0, n);
        }
    }
    
    public CTMergeCell addNewMergeCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMergeCell)this.get_store().add_element_user(CTMergeCellsImpl.MERGECELL$0);
        }
    }
    
    public void removeMergeCell(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTMergeCellsImpl.MERGECELL$0, n);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMergeCellsImpl.COUNT$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTMergeCellsImpl.COUNT$2);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTMergeCellsImpl.COUNT$2) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMergeCellsImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTMergeCellsImpl.COUNT$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTMergeCellsImpl.COUNT$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTMergeCellsImpl.COUNT$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTMergeCellsImpl.COUNT$2);
        }
    }
    
    static {
        MERGECELL$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "mergeCell");
        COUNT$2 = new QName("", "count");
    }
}
