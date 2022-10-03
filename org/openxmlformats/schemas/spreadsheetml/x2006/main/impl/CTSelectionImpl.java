package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSqref;
import java.util.List;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPane;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSelection;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSelectionImpl extends XmlComplexContentImpl implements CTSelection
{
    private static final long serialVersionUID = 1L;
    private static final QName PANE$0;
    private static final QName ACTIVECELL$2;
    private static final QName ACTIVECELLID$4;
    private static final QName SQREF$6;
    
    public CTSelectionImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STPane.Enum getPane() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSelectionImpl.PANE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSelectionImpl.PANE$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STPane.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPane xgetPane() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPane stPane = (STPane)this.get_store().find_attribute_user(CTSelectionImpl.PANE$0);
            if (stPane == null) {
                stPane = (STPane)this.get_default_attribute_value(CTSelectionImpl.PANE$0);
            }
            return stPane;
        }
    }
    
    public boolean isSetPane() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSelectionImpl.PANE$0) != null;
        }
    }
    
    public void setPane(final STPane.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSelectionImpl.PANE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSelectionImpl.PANE$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetPane(final STPane stPane) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPane stPane2 = (STPane)this.get_store().find_attribute_user(CTSelectionImpl.PANE$0);
            if (stPane2 == null) {
                stPane2 = (STPane)this.get_store().add_attribute_user(CTSelectionImpl.PANE$0);
            }
            stPane2.set((XmlObject)stPane);
        }
    }
    
    public void unsetPane() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSelectionImpl.PANE$0);
        }
    }
    
    public String getActiveCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSelectionImpl.ACTIVECELL$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STCellRef xgetActiveCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCellRef)this.get_store().find_attribute_user(CTSelectionImpl.ACTIVECELL$2);
        }
    }
    
    public boolean isSetActiveCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSelectionImpl.ACTIVECELL$2) != null;
        }
    }
    
    public void setActiveCell(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSelectionImpl.ACTIVECELL$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSelectionImpl.ACTIVECELL$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetActiveCell(final STCellRef stCellRef) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCellRef stCellRef2 = (STCellRef)this.get_store().find_attribute_user(CTSelectionImpl.ACTIVECELL$2);
            if (stCellRef2 == null) {
                stCellRef2 = (STCellRef)this.get_store().add_attribute_user(CTSelectionImpl.ACTIVECELL$2);
            }
            stCellRef2.set((XmlObject)stCellRef);
        }
    }
    
    public void unsetActiveCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSelectionImpl.ACTIVECELL$2);
        }
    }
    
    public long getActiveCellId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSelectionImpl.ACTIVECELLID$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSelectionImpl.ACTIVECELLID$4);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetActiveCellId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSelectionImpl.ACTIVECELLID$4);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTSelectionImpl.ACTIVECELLID$4);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetActiveCellId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSelectionImpl.ACTIVECELLID$4) != null;
        }
    }
    
    public void setActiveCellId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSelectionImpl.ACTIVECELLID$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSelectionImpl.ACTIVECELLID$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetActiveCellId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSelectionImpl.ACTIVECELLID$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTSelectionImpl.ACTIVECELLID$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetActiveCellId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSelectionImpl.ACTIVECELLID$4);
        }
    }
    
    public List getSqref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSelectionImpl.SQREF$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSelectionImpl.SQREF$6);
            }
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getListValue();
        }
    }
    
    public STSqref xgetSqref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSqref stSqref = (STSqref)this.get_store().find_attribute_user(CTSelectionImpl.SQREF$6);
            if (stSqref == null) {
                stSqref = (STSqref)this.get_default_attribute_value(CTSelectionImpl.SQREF$6);
            }
            return stSqref;
        }
    }
    
    public boolean isSetSqref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSelectionImpl.SQREF$6) != null;
        }
    }
    
    public void setSqref(final List listValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSelectionImpl.SQREF$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSelectionImpl.SQREF$6);
            }
            simpleValue.setListValue(listValue);
        }
    }
    
    public void xsetSqref(final STSqref stSqref) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSqref stSqref2 = (STSqref)this.get_store().find_attribute_user(CTSelectionImpl.SQREF$6);
            if (stSqref2 == null) {
                stSqref2 = (STSqref)this.get_store().add_attribute_user(CTSelectionImpl.SQREF$6);
            }
            stSqref2.set((XmlObject)stSqref);
        }
    }
    
    public void unsetSqref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSelectionImpl.SQREF$6);
        }
    }
    
    static {
        PANE$0 = new QName("", "pane");
        ACTIVECELL$2 = new QName("", "activeCell");
        ACTIVECELLID$4 = new QName("", "activeCellId");
        SQREF$6 = new QName("", "sqref");
    }
}
