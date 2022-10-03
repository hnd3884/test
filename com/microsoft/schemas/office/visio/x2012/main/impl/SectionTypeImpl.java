package com.microsoft.schemas.office.visio.x2012.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import com.microsoft.schemas.office.visio.x2012.main.RowType;
import com.microsoft.schemas.office.visio.x2012.main.TriggerType;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.SectionType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SectionTypeImpl extends XmlComplexContentImpl implements SectionType
{
    private static final long serialVersionUID = 1L;
    private static final QName CELL$0;
    private static final QName TRIGGER$2;
    private static final QName ROW$4;
    private static final QName N$6;
    private static final QName DEL$8;
    private static final QName IX$10;
    
    public SectionTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CellType> getCellList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CellList extends AbstractList<CellType>
            {
                @Override
                public CellType get(final int n) {
                    return SectionTypeImpl.this.getCellArray(n);
                }
                
                @Override
                public CellType set(final int n, final CellType cellType) {
                    final CellType cellArray = SectionTypeImpl.this.getCellArray(n);
                    SectionTypeImpl.this.setCellArray(n, cellType);
                    return cellArray;
                }
                
                @Override
                public void add(final int n, final CellType cellType) {
                    SectionTypeImpl.this.insertNewCell(n).set((XmlObject)cellType);
                }
                
                @Override
                public CellType remove(final int n) {
                    final CellType cellArray = SectionTypeImpl.this.getCellArray(n);
                    SectionTypeImpl.this.removeCell(n);
                    return cellArray;
                }
                
                @Override
                public int size() {
                    return SectionTypeImpl.this.sizeOfCellArray();
                }
            }
            return new CellList();
        }
    }
    
    @Deprecated
    public CellType[] getCellArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(SectionTypeImpl.CELL$0, (List)list);
            final CellType[] array = new CellType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CellType getCellArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CellType cellType = (CellType)this.get_store().find_element_user(SectionTypeImpl.CELL$0, n);
            if (cellType == null) {
                throw new IndexOutOfBoundsException();
            }
            return cellType;
        }
    }
    
    public int sizeOfCellArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SectionTypeImpl.CELL$0);
        }
    }
    
    public void setCellArray(final CellType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, SectionTypeImpl.CELL$0);
    }
    
    public void setCellArray(final int n, final CellType cellType) {
        this.generatedSetterHelperImpl((XmlObject)cellType, SectionTypeImpl.CELL$0, n, (short)2);
    }
    
    public CellType insertNewCell(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CellType)this.get_store().insert_element_user(SectionTypeImpl.CELL$0, n);
        }
    }
    
    public CellType addNewCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CellType)this.get_store().add_element_user(SectionTypeImpl.CELL$0);
        }
    }
    
    public void removeCell(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SectionTypeImpl.CELL$0, n);
        }
    }
    
    public List<TriggerType> getTriggerList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TriggerList extends AbstractList<TriggerType>
            {
                @Override
                public TriggerType get(final int n) {
                    return SectionTypeImpl.this.getTriggerArray(n);
                }
                
                @Override
                public TriggerType set(final int n, final TriggerType triggerType) {
                    final TriggerType triggerArray = SectionTypeImpl.this.getTriggerArray(n);
                    SectionTypeImpl.this.setTriggerArray(n, triggerType);
                    return triggerArray;
                }
                
                @Override
                public void add(final int n, final TriggerType triggerType) {
                    SectionTypeImpl.this.insertNewTrigger(n).set((XmlObject)triggerType);
                }
                
                @Override
                public TriggerType remove(final int n) {
                    final TriggerType triggerArray = SectionTypeImpl.this.getTriggerArray(n);
                    SectionTypeImpl.this.removeTrigger(n);
                    return triggerArray;
                }
                
                @Override
                public int size() {
                    return SectionTypeImpl.this.sizeOfTriggerArray();
                }
            }
            return new TriggerList();
        }
    }
    
    @Deprecated
    public TriggerType[] getTriggerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(SectionTypeImpl.TRIGGER$2, (List)list);
            final TriggerType[] array = new TriggerType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public TriggerType getTriggerArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final TriggerType triggerType = (TriggerType)this.get_store().find_element_user(SectionTypeImpl.TRIGGER$2, n);
            if (triggerType == null) {
                throw new IndexOutOfBoundsException();
            }
            return triggerType;
        }
    }
    
    public int sizeOfTriggerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SectionTypeImpl.TRIGGER$2);
        }
    }
    
    public void setTriggerArray(final TriggerType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, SectionTypeImpl.TRIGGER$2);
    }
    
    public void setTriggerArray(final int n, final TriggerType triggerType) {
        this.generatedSetterHelperImpl((XmlObject)triggerType, SectionTypeImpl.TRIGGER$2, n, (short)2);
    }
    
    public TriggerType insertNewTrigger(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (TriggerType)this.get_store().insert_element_user(SectionTypeImpl.TRIGGER$2, n);
        }
    }
    
    public TriggerType addNewTrigger() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (TriggerType)this.get_store().add_element_user(SectionTypeImpl.TRIGGER$2);
        }
    }
    
    public void removeTrigger(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SectionTypeImpl.TRIGGER$2, n);
        }
    }
    
    public List<RowType> getRowList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RowList extends AbstractList<RowType>
            {
                @Override
                public RowType get(final int n) {
                    return SectionTypeImpl.this.getRowArray(n);
                }
                
                @Override
                public RowType set(final int n, final RowType rowType) {
                    final RowType rowArray = SectionTypeImpl.this.getRowArray(n);
                    SectionTypeImpl.this.setRowArray(n, rowType);
                    return rowArray;
                }
                
                @Override
                public void add(final int n, final RowType rowType) {
                    SectionTypeImpl.this.insertNewRow(n).set((XmlObject)rowType);
                }
                
                @Override
                public RowType remove(final int n) {
                    final RowType rowArray = SectionTypeImpl.this.getRowArray(n);
                    SectionTypeImpl.this.removeRow(n);
                    return rowArray;
                }
                
                @Override
                public int size() {
                    return SectionTypeImpl.this.sizeOfRowArray();
                }
            }
            return new RowList();
        }
    }
    
    @Deprecated
    public RowType[] getRowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(SectionTypeImpl.ROW$4, (List)list);
            final RowType[] array = new RowType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public RowType getRowArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final RowType rowType = (RowType)this.get_store().find_element_user(SectionTypeImpl.ROW$4, n);
            if (rowType == null) {
                throw new IndexOutOfBoundsException();
            }
            return rowType;
        }
    }
    
    public int sizeOfRowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SectionTypeImpl.ROW$4);
        }
    }
    
    public void setRowArray(final RowType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, SectionTypeImpl.ROW$4);
    }
    
    public void setRowArray(final int n, final RowType rowType) {
        this.generatedSetterHelperImpl((XmlObject)rowType, SectionTypeImpl.ROW$4, n, (short)2);
    }
    
    public RowType insertNewRow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (RowType)this.get_store().insert_element_user(SectionTypeImpl.ROW$4, n);
        }
    }
    
    public RowType addNewRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (RowType)this.get_store().add_element_user(SectionTypeImpl.ROW$4);
        }
    }
    
    public void removeRow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SectionTypeImpl.ROW$4, n);
        }
    }
    
    public String getN() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SectionTypeImpl.N$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetN() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(SectionTypeImpl.N$6);
        }
    }
    
    public void setN(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SectionTypeImpl.N$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(SectionTypeImpl.N$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetN(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(SectionTypeImpl.N$6);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(SectionTypeImpl.N$6);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public boolean getDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SectionTypeImpl.DEL$8);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(SectionTypeImpl.DEL$8);
        }
    }
    
    public boolean isSetDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(SectionTypeImpl.DEL$8) != null;
        }
    }
    
    public void setDel(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SectionTypeImpl.DEL$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(SectionTypeImpl.DEL$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDel(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(SectionTypeImpl.DEL$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(SectionTypeImpl.DEL$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(SectionTypeImpl.DEL$8);
        }
    }
    
    public long getIX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SectionTypeImpl.IX$10);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetIX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(SectionTypeImpl.IX$10);
        }
    }
    
    public boolean isSetIX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(SectionTypeImpl.IX$10) != null;
        }
    }
    
    public void setIX(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SectionTypeImpl.IX$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(SectionTypeImpl.IX$10);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetIX(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(SectionTypeImpl.IX$10);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(SectionTypeImpl.IX$10);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetIX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(SectionTypeImpl.IX$10);
        }
    }
    
    static {
        CELL$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Cell");
        TRIGGER$2 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Trigger");
        ROW$4 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Row");
        N$6 = new QName("", "N");
        DEL$8 = new QName("", "Del");
        IX$10 = new QName("", "IX");
    }
}
