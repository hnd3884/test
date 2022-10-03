package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBreak;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageBreak;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPageBreakImpl extends XmlComplexContentImpl implements CTPageBreak
{
    private static final long serialVersionUID = 1L;
    private static final QName BRK$0;
    private static final QName COUNT$2;
    private static final QName MANUALBREAKCOUNT$4;
    
    public CTPageBreakImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTBreak> getBrkList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BrkList extends AbstractList<CTBreak>
            {
                @Override
                public CTBreak get(final int n) {
                    return CTPageBreakImpl.this.getBrkArray(n);
                }
                
                @Override
                public CTBreak set(final int n, final CTBreak ctBreak) {
                    final CTBreak brkArray = CTPageBreakImpl.this.getBrkArray(n);
                    CTPageBreakImpl.this.setBrkArray(n, ctBreak);
                    return brkArray;
                }
                
                @Override
                public void add(final int n, final CTBreak ctBreak) {
                    CTPageBreakImpl.this.insertNewBrk(n).set((XmlObject)ctBreak);
                }
                
                @Override
                public CTBreak remove(final int n) {
                    final CTBreak brkArray = CTPageBreakImpl.this.getBrkArray(n);
                    CTPageBreakImpl.this.removeBrk(n);
                    return brkArray;
                }
                
                @Override
                public int size() {
                    return CTPageBreakImpl.this.sizeOfBrkArray();
                }
            }
            return new BrkList();
        }
    }
    
    @Deprecated
    public CTBreak[] getBrkArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPageBreakImpl.BRK$0, (List)list);
            final CTBreak[] array = new CTBreak[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBreak getBrkArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBreak ctBreak = (CTBreak)this.get_store().find_element_user(CTPageBreakImpl.BRK$0, n);
            if (ctBreak == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBreak;
        }
    }
    
    public int sizeOfBrkArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPageBreakImpl.BRK$0);
        }
    }
    
    public void setBrkArray(final CTBreak[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPageBreakImpl.BRK$0);
    }
    
    public void setBrkArray(final int n, final CTBreak ctBreak) {
        this.generatedSetterHelperImpl((XmlObject)ctBreak, CTPageBreakImpl.BRK$0, n, (short)2);
    }
    
    public CTBreak insertNewBrk(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBreak)this.get_store().insert_element_user(CTPageBreakImpl.BRK$0, n);
        }
    }
    
    public CTBreak addNewBrk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBreak)this.get_store().add_element_user(CTPageBreakImpl.BRK$0);
        }
    }
    
    public void removeBrk(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPageBreakImpl.BRK$0, n);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageBreakImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageBreakImpl.COUNT$2);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageBreakImpl.COUNT$2);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPageBreakImpl.COUNT$2);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageBreakImpl.COUNT$2) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageBreakImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageBreakImpl.COUNT$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageBreakImpl.COUNT$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPageBreakImpl.COUNT$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageBreakImpl.COUNT$2);
        }
    }
    
    public long getManualBreakCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageBreakImpl.MANUALBREAKCOUNT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageBreakImpl.MANUALBREAKCOUNT$4);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetManualBreakCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageBreakImpl.MANUALBREAKCOUNT$4);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPageBreakImpl.MANUALBREAKCOUNT$4);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetManualBreakCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageBreakImpl.MANUALBREAKCOUNT$4) != null;
        }
    }
    
    public void setManualBreakCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageBreakImpl.MANUALBREAKCOUNT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageBreakImpl.MANUALBREAKCOUNT$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetManualBreakCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageBreakImpl.MANUALBREAKCOUNT$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPageBreakImpl.MANUALBREAKCOUNT$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetManualBreakCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageBreakImpl.MANUALBREAKCOUNT$4);
        }
    }
    
    static {
        BRK$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "brk");
        COUNT$2 = new QName("", "count");
        MANUALBREAKCOUNT$4 = new QName("", "manualBreakCount");
    }
}
