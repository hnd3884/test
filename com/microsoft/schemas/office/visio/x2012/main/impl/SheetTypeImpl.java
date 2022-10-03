package com.microsoft.schemas.office.visio.x2012.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import com.microsoft.schemas.office.visio.x2012.main.SectionType;
import com.microsoft.schemas.office.visio.x2012.main.TriggerType;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.SheetType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SheetTypeImpl extends XmlComplexContentImpl implements SheetType
{
    private static final long serialVersionUID = 1L;
    private static final QName CELL$0;
    private static final QName TRIGGER$2;
    private static final QName SECTION$4;
    private static final QName LINESTYLE$6;
    private static final QName FILLSTYLE$8;
    private static final QName TEXTSTYLE$10;
    
    public SheetTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CellType> getCellList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CellList extends AbstractList<CellType>
            {
                @Override
                public CellType get(final int n) {
                    return SheetTypeImpl.this.getCellArray(n);
                }
                
                @Override
                public CellType set(final int n, final CellType cellType) {
                    final CellType cellArray = SheetTypeImpl.this.getCellArray(n);
                    SheetTypeImpl.this.setCellArray(n, cellType);
                    return cellArray;
                }
                
                @Override
                public void add(final int n, final CellType cellType) {
                    SheetTypeImpl.this.insertNewCell(n).set((XmlObject)cellType);
                }
                
                @Override
                public CellType remove(final int n) {
                    final CellType cellArray = SheetTypeImpl.this.getCellArray(n);
                    SheetTypeImpl.this.removeCell(n);
                    return cellArray;
                }
                
                @Override
                public int size() {
                    return SheetTypeImpl.this.sizeOfCellArray();
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
            this.get_store().find_all_element_users(SheetTypeImpl.CELL$0, (List)list);
            final CellType[] array = new CellType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CellType getCellArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CellType cellType = (CellType)this.get_store().find_element_user(SheetTypeImpl.CELL$0, n);
            if (cellType == null) {
                throw new IndexOutOfBoundsException();
            }
            return cellType;
        }
    }
    
    public int sizeOfCellArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SheetTypeImpl.CELL$0);
        }
    }
    
    public void setCellArray(final CellType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, SheetTypeImpl.CELL$0);
    }
    
    public void setCellArray(final int n, final CellType cellType) {
        this.generatedSetterHelperImpl((XmlObject)cellType, SheetTypeImpl.CELL$0, n, (short)2);
    }
    
    public CellType insertNewCell(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CellType)this.get_store().insert_element_user(SheetTypeImpl.CELL$0, n);
        }
    }
    
    public CellType addNewCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CellType)this.get_store().add_element_user(SheetTypeImpl.CELL$0);
        }
    }
    
    public void removeCell(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SheetTypeImpl.CELL$0, n);
        }
    }
    
    public List<TriggerType> getTriggerList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TriggerList extends AbstractList<TriggerType>
            {
                @Override
                public TriggerType get(final int n) {
                    return SheetTypeImpl.this.getTriggerArray(n);
                }
                
                @Override
                public TriggerType set(final int n, final TriggerType triggerType) {
                    final TriggerType triggerArray = SheetTypeImpl.this.getTriggerArray(n);
                    SheetTypeImpl.this.setTriggerArray(n, triggerType);
                    return triggerArray;
                }
                
                @Override
                public void add(final int n, final TriggerType triggerType) {
                    SheetTypeImpl.this.insertNewTrigger(n).set((XmlObject)triggerType);
                }
                
                @Override
                public TriggerType remove(final int n) {
                    final TriggerType triggerArray = SheetTypeImpl.this.getTriggerArray(n);
                    SheetTypeImpl.this.removeTrigger(n);
                    return triggerArray;
                }
                
                @Override
                public int size() {
                    return SheetTypeImpl.this.sizeOfTriggerArray();
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
            this.get_store().find_all_element_users(SheetTypeImpl.TRIGGER$2, (List)list);
            final TriggerType[] array = new TriggerType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public TriggerType getTriggerArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final TriggerType triggerType = (TriggerType)this.get_store().find_element_user(SheetTypeImpl.TRIGGER$2, n);
            if (triggerType == null) {
                throw new IndexOutOfBoundsException();
            }
            return triggerType;
        }
    }
    
    public int sizeOfTriggerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SheetTypeImpl.TRIGGER$2);
        }
    }
    
    public void setTriggerArray(final TriggerType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, SheetTypeImpl.TRIGGER$2);
    }
    
    public void setTriggerArray(final int n, final TriggerType triggerType) {
        this.generatedSetterHelperImpl((XmlObject)triggerType, SheetTypeImpl.TRIGGER$2, n, (short)2);
    }
    
    public TriggerType insertNewTrigger(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (TriggerType)this.get_store().insert_element_user(SheetTypeImpl.TRIGGER$2, n);
        }
    }
    
    public TriggerType addNewTrigger() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (TriggerType)this.get_store().add_element_user(SheetTypeImpl.TRIGGER$2);
        }
    }
    
    public void removeTrigger(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SheetTypeImpl.TRIGGER$2, n);
        }
    }
    
    public List<SectionType> getSectionList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SectionList extends AbstractList<SectionType>
            {
                @Override
                public SectionType get(final int n) {
                    return SheetTypeImpl.this.getSectionArray(n);
                }
                
                @Override
                public SectionType set(final int n, final SectionType sectionType) {
                    final SectionType sectionArray = SheetTypeImpl.this.getSectionArray(n);
                    SheetTypeImpl.this.setSectionArray(n, sectionType);
                    return sectionArray;
                }
                
                @Override
                public void add(final int n, final SectionType sectionType) {
                    SheetTypeImpl.this.insertNewSection(n).set((XmlObject)sectionType);
                }
                
                @Override
                public SectionType remove(final int n) {
                    final SectionType sectionArray = SheetTypeImpl.this.getSectionArray(n);
                    SheetTypeImpl.this.removeSection(n);
                    return sectionArray;
                }
                
                @Override
                public int size() {
                    return SheetTypeImpl.this.sizeOfSectionArray();
                }
            }
            return new SectionList();
        }
    }
    
    @Deprecated
    public SectionType[] getSectionArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(SheetTypeImpl.SECTION$4, (List)list);
            final SectionType[] array = new SectionType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public SectionType getSectionArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SectionType sectionType = (SectionType)this.get_store().find_element_user(SheetTypeImpl.SECTION$4, n);
            if (sectionType == null) {
                throw new IndexOutOfBoundsException();
            }
            return sectionType;
        }
    }
    
    public int sizeOfSectionArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SheetTypeImpl.SECTION$4);
        }
    }
    
    public void setSectionArray(final SectionType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, SheetTypeImpl.SECTION$4);
    }
    
    public void setSectionArray(final int n, final SectionType sectionType) {
        this.generatedSetterHelperImpl((XmlObject)sectionType, SheetTypeImpl.SECTION$4, n, (short)2);
    }
    
    public SectionType insertNewSection(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (SectionType)this.get_store().insert_element_user(SheetTypeImpl.SECTION$4, n);
        }
    }
    
    public SectionType addNewSection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (SectionType)this.get_store().add_element_user(SheetTypeImpl.SECTION$4);
        }
    }
    
    public void removeSection(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SheetTypeImpl.SECTION$4, n);
        }
    }
    
    public long getLineStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SheetTypeImpl.LINESTYLE$6);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetLineStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(SheetTypeImpl.LINESTYLE$6);
        }
    }
    
    public boolean isSetLineStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(SheetTypeImpl.LINESTYLE$6) != null;
        }
    }
    
    public void setLineStyle(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SheetTypeImpl.LINESTYLE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(SheetTypeImpl.LINESTYLE$6);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetLineStyle(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(SheetTypeImpl.LINESTYLE$6);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(SheetTypeImpl.LINESTYLE$6);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetLineStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(SheetTypeImpl.LINESTYLE$6);
        }
    }
    
    public long getFillStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SheetTypeImpl.FILLSTYLE$8);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetFillStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(SheetTypeImpl.FILLSTYLE$8);
        }
    }
    
    public boolean isSetFillStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(SheetTypeImpl.FILLSTYLE$8) != null;
        }
    }
    
    public void setFillStyle(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SheetTypeImpl.FILLSTYLE$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(SheetTypeImpl.FILLSTYLE$8);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetFillStyle(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(SheetTypeImpl.FILLSTYLE$8);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(SheetTypeImpl.FILLSTYLE$8);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetFillStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(SheetTypeImpl.FILLSTYLE$8);
        }
    }
    
    public long getTextStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SheetTypeImpl.TEXTSTYLE$10);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetTextStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(SheetTypeImpl.TEXTSTYLE$10);
        }
    }
    
    public boolean isSetTextStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(SheetTypeImpl.TEXTSTYLE$10) != null;
        }
    }
    
    public void setTextStyle(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SheetTypeImpl.TEXTSTYLE$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(SheetTypeImpl.TEXTSTYLE$10);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetTextStyle(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(SheetTypeImpl.TEXTSTYLE$10);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(SheetTypeImpl.TEXTSTYLE$10);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetTextStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(SheetTypeImpl.TEXTSTYLE$10);
        }
    }
    
    static {
        CELL$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Cell");
        TRIGGER$2 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Trigger");
        SECTION$4 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Section");
        LINESTYLE$6 = new QName("", "LineStyle");
        FILLSTYLE$8 = new QName("", "FillStyle");
        TEXTSTYLE$10 = new QName("", "TextStyle");
    }
}
