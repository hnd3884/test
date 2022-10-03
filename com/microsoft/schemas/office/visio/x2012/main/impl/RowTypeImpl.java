package com.microsoft.schemas.office.visio.x2012.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import com.microsoft.schemas.office.visio.x2012.main.TriggerType;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.RowType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class RowTypeImpl extends XmlComplexContentImpl implements RowType
{
    private static final long serialVersionUID = 1L;
    private static final QName CELL$0;
    private static final QName TRIGGER$2;
    private static final QName N$4;
    private static final QName LOCALNAME$6;
    private static final QName IX$8;
    private static final QName T$10;
    private static final QName DEL$12;
    
    public RowTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CellType> getCellList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CellList extends AbstractList<CellType>
            {
                @Override
                public CellType get(final int n) {
                    return RowTypeImpl.this.getCellArray(n);
                }
                
                @Override
                public CellType set(final int n, final CellType cellType) {
                    final CellType cellArray = RowTypeImpl.this.getCellArray(n);
                    RowTypeImpl.this.setCellArray(n, cellType);
                    return cellArray;
                }
                
                @Override
                public void add(final int n, final CellType cellType) {
                    RowTypeImpl.this.insertNewCell(n).set((XmlObject)cellType);
                }
                
                @Override
                public CellType remove(final int n) {
                    final CellType cellArray = RowTypeImpl.this.getCellArray(n);
                    RowTypeImpl.this.removeCell(n);
                    return cellArray;
                }
                
                @Override
                public int size() {
                    return RowTypeImpl.this.sizeOfCellArray();
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
            this.get_store().find_all_element_users(RowTypeImpl.CELL$0, (List)list);
            final CellType[] array = new CellType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CellType getCellArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CellType cellType = (CellType)this.get_store().find_element_user(RowTypeImpl.CELL$0, n);
            if (cellType == null) {
                throw new IndexOutOfBoundsException();
            }
            return cellType;
        }
    }
    
    public int sizeOfCellArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(RowTypeImpl.CELL$0);
        }
    }
    
    public void setCellArray(final CellType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, RowTypeImpl.CELL$0);
    }
    
    public void setCellArray(final int n, final CellType cellType) {
        this.generatedSetterHelperImpl((XmlObject)cellType, RowTypeImpl.CELL$0, n, (short)2);
    }
    
    public CellType insertNewCell(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CellType)this.get_store().insert_element_user(RowTypeImpl.CELL$0, n);
        }
    }
    
    public CellType addNewCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CellType)this.get_store().add_element_user(RowTypeImpl.CELL$0);
        }
    }
    
    public void removeCell(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RowTypeImpl.CELL$0, n);
        }
    }
    
    public List<TriggerType> getTriggerList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TriggerList extends AbstractList<TriggerType>
            {
                @Override
                public TriggerType get(final int n) {
                    return RowTypeImpl.this.getTriggerArray(n);
                }
                
                @Override
                public TriggerType set(final int n, final TriggerType triggerType) {
                    final TriggerType triggerArray = RowTypeImpl.this.getTriggerArray(n);
                    RowTypeImpl.this.setTriggerArray(n, triggerType);
                    return triggerArray;
                }
                
                @Override
                public void add(final int n, final TriggerType triggerType) {
                    RowTypeImpl.this.insertNewTrigger(n).set((XmlObject)triggerType);
                }
                
                @Override
                public TriggerType remove(final int n) {
                    final TriggerType triggerArray = RowTypeImpl.this.getTriggerArray(n);
                    RowTypeImpl.this.removeTrigger(n);
                    return triggerArray;
                }
                
                @Override
                public int size() {
                    return RowTypeImpl.this.sizeOfTriggerArray();
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
            this.get_store().find_all_element_users(RowTypeImpl.TRIGGER$2, (List)list);
            final TriggerType[] array = new TriggerType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public TriggerType getTriggerArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final TriggerType triggerType = (TriggerType)this.get_store().find_element_user(RowTypeImpl.TRIGGER$2, n);
            if (triggerType == null) {
                throw new IndexOutOfBoundsException();
            }
            return triggerType;
        }
    }
    
    public int sizeOfTriggerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(RowTypeImpl.TRIGGER$2);
        }
    }
    
    public void setTriggerArray(final TriggerType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, RowTypeImpl.TRIGGER$2);
    }
    
    public void setTriggerArray(final int n, final TriggerType triggerType) {
        this.generatedSetterHelperImpl((XmlObject)triggerType, RowTypeImpl.TRIGGER$2, n, (short)2);
    }
    
    public TriggerType insertNewTrigger(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (TriggerType)this.get_store().insert_element_user(RowTypeImpl.TRIGGER$2, n);
        }
    }
    
    public TriggerType addNewTrigger() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (TriggerType)this.get_store().add_element_user(RowTypeImpl.TRIGGER$2);
        }
    }
    
    public void removeTrigger(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RowTypeImpl.TRIGGER$2, n);
        }
    }
    
    public String getN() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(RowTypeImpl.N$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetN() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(RowTypeImpl.N$4);
        }
    }
    
    public boolean isSetN() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(RowTypeImpl.N$4) != null;
        }
    }
    
    public void setN(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(RowTypeImpl.N$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(RowTypeImpl.N$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetN(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(RowTypeImpl.N$4);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(RowTypeImpl.N$4);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetN() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(RowTypeImpl.N$4);
        }
    }
    
    public String getLocalName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(RowTypeImpl.LOCALNAME$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetLocalName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(RowTypeImpl.LOCALNAME$6);
        }
    }
    
    public boolean isSetLocalName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(RowTypeImpl.LOCALNAME$6) != null;
        }
    }
    
    public void setLocalName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(RowTypeImpl.LOCALNAME$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(RowTypeImpl.LOCALNAME$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetLocalName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(RowTypeImpl.LOCALNAME$6);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(RowTypeImpl.LOCALNAME$6);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetLocalName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(RowTypeImpl.LOCALNAME$6);
        }
    }
    
    public long getIX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(RowTypeImpl.IX$8);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetIX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(RowTypeImpl.IX$8);
        }
    }
    
    public boolean isSetIX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(RowTypeImpl.IX$8) != null;
        }
    }
    
    public void setIX(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(RowTypeImpl.IX$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(RowTypeImpl.IX$8);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetIX(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(RowTypeImpl.IX$8);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(RowTypeImpl.IX$8);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetIX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(RowTypeImpl.IX$8);
        }
    }
    
    public String getT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(RowTypeImpl.T$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(RowTypeImpl.T$10);
        }
    }
    
    public boolean isSetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(RowTypeImpl.T$10) != null;
        }
    }
    
    public void setT(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(RowTypeImpl.T$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(RowTypeImpl.T$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetT(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(RowTypeImpl.T$10);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(RowTypeImpl.T$10);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(RowTypeImpl.T$10);
        }
    }
    
    public boolean getDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(RowTypeImpl.DEL$12);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(RowTypeImpl.DEL$12);
        }
    }
    
    public boolean isSetDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(RowTypeImpl.DEL$12) != null;
        }
    }
    
    public void setDel(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(RowTypeImpl.DEL$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(RowTypeImpl.DEL$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDel(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(RowTypeImpl.DEL$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(RowTypeImpl.DEL$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(RowTypeImpl.DEL$12);
        }
    }
    
    static {
        CELL$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Cell");
        TRIGGER$2 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Trigger");
        N$4 = new QName("", "N");
        LOCALNAME$6 = new QName("", "LocalName");
        IX$8 = new QName("", "IX");
        T$10 = new QName("", "T");
        DEL$12 = new QName("", "Del");
    }
}
