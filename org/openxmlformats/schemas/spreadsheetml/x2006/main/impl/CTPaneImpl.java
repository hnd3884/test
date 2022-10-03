package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPaneState;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPane;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPane;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPaneImpl extends XmlComplexContentImpl implements CTPane
{
    private static final long serialVersionUID = 1L;
    private static final QName XSPLIT$0;
    private static final QName YSPLIT$2;
    private static final QName TOPLEFTCELL$4;
    private static final QName ACTIVEPANE$6;
    private static final QName STATE$8;
    
    public CTPaneImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public double getXSplit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPaneImpl.XSPLIT$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPaneImpl.XSPLIT$0);
            }
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetXSplit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble = (XmlDouble)this.get_store().find_attribute_user(CTPaneImpl.XSPLIT$0);
            if (xmlDouble == null) {
                xmlDouble = (XmlDouble)this.get_default_attribute_value(CTPaneImpl.XSPLIT$0);
            }
            return xmlDouble;
        }
    }
    
    public boolean isSetXSplit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPaneImpl.XSPLIT$0) != null;
        }
    }
    
    public void setXSplit(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPaneImpl.XSPLIT$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPaneImpl.XSPLIT$0);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetXSplit(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTPaneImpl.XSPLIT$0);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTPaneImpl.XSPLIT$0);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public void unsetXSplit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPaneImpl.XSPLIT$0);
        }
    }
    
    public double getYSplit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPaneImpl.YSPLIT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPaneImpl.YSPLIT$2);
            }
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetYSplit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble = (XmlDouble)this.get_store().find_attribute_user(CTPaneImpl.YSPLIT$2);
            if (xmlDouble == null) {
                xmlDouble = (XmlDouble)this.get_default_attribute_value(CTPaneImpl.YSPLIT$2);
            }
            return xmlDouble;
        }
    }
    
    public boolean isSetYSplit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPaneImpl.YSPLIT$2) != null;
        }
    }
    
    public void setYSplit(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPaneImpl.YSPLIT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPaneImpl.YSPLIT$2);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetYSplit(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTPaneImpl.YSPLIT$2);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTPaneImpl.YSPLIT$2);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public void unsetYSplit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPaneImpl.YSPLIT$2);
        }
    }
    
    public String getTopLeftCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPaneImpl.TOPLEFTCELL$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STCellRef xgetTopLeftCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCellRef)this.get_store().find_attribute_user(CTPaneImpl.TOPLEFTCELL$4);
        }
    }
    
    public boolean isSetTopLeftCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPaneImpl.TOPLEFTCELL$4) != null;
        }
    }
    
    public void setTopLeftCell(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPaneImpl.TOPLEFTCELL$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPaneImpl.TOPLEFTCELL$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTopLeftCell(final STCellRef stCellRef) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCellRef stCellRef2 = (STCellRef)this.get_store().find_attribute_user(CTPaneImpl.TOPLEFTCELL$4);
            if (stCellRef2 == null) {
                stCellRef2 = (STCellRef)this.get_store().add_attribute_user(CTPaneImpl.TOPLEFTCELL$4);
            }
            stCellRef2.set((XmlObject)stCellRef);
        }
    }
    
    public void unsetTopLeftCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPaneImpl.TOPLEFTCELL$4);
        }
    }
    
    public STPane.Enum getActivePane() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPaneImpl.ACTIVEPANE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPaneImpl.ACTIVEPANE$6);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STPane.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPane xgetActivePane() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPane stPane = (STPane)this.get_store().find_attribute_user(CTPaneImpl.ACTIVEPANE$6);
            if (stPane == null) {
                stPane = (STPane)this.get_default_attribute_value(CTPaneImpl.ACTIVEPANE$6);
            }
            return stPane;
        }
    }
    
    public boolean isSetActivePane() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPaneImpl.ACTIVEPANE$6) != null;
        }
    }
    
    public void setActivePane(final STPane.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPaneImpl.ACTIVEPANE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPaneImpl.ACTIVEPANE$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetActivePane(final STPane stPane) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPane stPane2 = (STPane)this.get_store().find_attribute_user(CTPaneImpl.ACTIVEPANE$6);
            if (stPane2 == null) {
                stPane2 = (STPane)this.get_store().add_attribute_user(CTPaneImpl.ACTIVEPANE$6);
            }
            stPane2.set((XmlObject)stPane);
        }
    }
    
    public void unsetActivePane() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPaneImpl.ACTIVEPANE$6);
        }
    }
    
    public STPaneState.Enum getState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPaneImpl.STATE$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPaneImpl.STATE$8);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STPaneState.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPaneState xgetState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPaneState stPaneState = (STPaneState)this.get_store().find_attribute_user(CTPaneImpl.STATE$8);
            if (stPaneState == null) {
                stPaneState = (STPaneState)this.get_default_attribute_value(CTPaneImpl.STATE$8);
            }
            return stPaneState;
        }
    }
    
    public boolean isSetState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPaneImpl.STATE$8) != null;
        }
    }
    
    public void setState(final STPaneState.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPaneImpl.STATE$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPaneImpl.STATE$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetState(final STPaneState stPaneState) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPaneState stPaneState2 = (STPaneState)this.get_store().find_attribute_user(CTPaneImpl.STATE$8);
            if (stPaneState2 == null) {
                stPaneState2 = (STPaneState)this.get_store().add_attribute_user(CTPaneImpl.STATE$8);
            }
            stPaneState2.set((XmlObject)stPaneState);
        }
    }
    
    public void unsetState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPaneImpl.STATE$8);
        }
    }
    
    static {
        XSPLIT$0 = new QName("", "xSplit");
        YSPLIT$2 = new QName("", "ySplit");
        TOPLEFTCELL$4 = new QName("", "topLeftCell");
        ACTIVEPANE$6 = new QName("", "activePane");
        STATE$8 = new QName("", "state");
    }
}
