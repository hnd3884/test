package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPrintOptions;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPrintOptionsImpl extends XmlComplexContentImpl implements CTPrintOptions
{
    private static final long serialVersionUID = 1L;
    private static final QName HORIZONTALCENTERED$0;
    private static final QName VERTICALCENTERED$2;
    private static final QName HEADINGS$4;
    private static final QName GRIDLINES$6;
    private static final QName GRIDLINESSET$8;
    
    public CTPrintOptionsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public boolean getHorizontalCentered() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPrintOptionsImpl.HORIZONTALCENTERED$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPrintOptionsImpl.HORIZONTALCENTERED$0);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHorizontalCentered() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPrintOptionsImpl.HORIZONTALCENTERED$0);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPrintOptionsImpl.HORIZONTALCENTERED$0);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetHorizontalCentered() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPrintOptionsImpl.HORIZONTALCENTERED$0) != null;
        }
    }
    
    public void setHorizontalCentered(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPrintOptionsImpl.HORIZONTALCENTERED$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPrintOptionsImpl.HORIZONTALCENTERED$0);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHorizontalCentered(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPrintOptionsImpl.HORIZONTALCENTERED$0);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPrintOptionsImpl.HORIZONTALCENTERED$0);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHorizontalCentered() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPrintOptionsImpl.HORIZONTALCENTERED$0);
        }
    }
    
    public boolean getVerticalCentered() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPrintOptionsImpl.VERTICALCENTERED$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPrintOptionsImpl.VERTICALCENTERED$2);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetVerticalCentered() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPrintOptionsImpl.VERTICALCENTERED$2);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPrintOptionsImpl.VERTICALCENTERED$2);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetVerticalCentered() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPrintOptionsImpl.VERTICALCENTERED$2) != null;
        }
    }
    
    public void setVerticalCentered(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPrintOptionsImpl.VERTICALCENTERED$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPrintOptionsImpl.VERTICALCENTERED$2);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetVerticalCentered(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPrintOptionsImpl.VERTICALCENTERED$2);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPrintOptionsImpl.VERTICALCENTERED$2);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetVerticalCentered() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPrintOptionsImpl.VERTICALCENTERED$2);
        }
    }
    
    public boolean getHeadings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPrintOptionsImpl.HEADINGS$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPrintOptionsImpl.HEADINGS$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHeadings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPrintOptionsImpl.HEADINGS$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPrintOptionsImpl.HEADINGS$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetHeadings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPrintOptionsImpl.HEADINGS$4) != null;
        }
    }
    
    public void setHeadings(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPrintOptionsImpl.HEADINGS$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPrintOptionsImpl.HEADINGS$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHeadings(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPrintOptionsImpl.HEADINGS$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPrintOptionsImpl.HEADINGS$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHeadings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPrintOptionsImpl.HEADINGS$4);
        }
    }
    
    public boolean getGridLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPrintOptionsImpl.GRIDLINES$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPrintOptionsImpl.GRIDLINES$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetGridLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPrintOptionsImpl.GRIDLINES$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPrintOptionsImpl.GRIDLINES$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetGridLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPrintOptionsImpl.GRIDLINES$6) != null;
        }
    }
    
    public void setGridLines(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPrintOptionsImpl.GRIDLINES$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPrintOptionsImpl.GRIDLINES$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetGridLines(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPrintOptionsImpl.GRIDLINES$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPrintOptionsImpl.GRIDLINES$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetGridLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPrintOptionsImpl.GRIDLINES$6);
        }
    }
    
    public boolean getGridLinesSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPrintOptionsImpl.GRIDLINESSET$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPrintOptionsImpl.GRIDLINESSET$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetGridLinesSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPrintOptionsImpl.GRIDLINESSET$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPrintOptionsImpl.GRIDLINESSET$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetGridLinesSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPrintOptionsImpl.GRIDLINESSET$8) != null;
        }
    }
    
    public void setGridLinesSet(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPrintOptionsImpl.GRIDLINESSET$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPrintOptionsImpl.GRIDLINESSET$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetGridLinesSet(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPrintOptionsImpl.GRIDLINESSET$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPrintOptionsImpl.GRIDLINESSET$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetGridLinesSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPrintOptionsImpl.GRIDLINESSET$8);
        }
    }
    
    static {
        HORIZONTALCENTERED$0 = new QName("", "horizontalCentered");
        VERTICALCENTERED$2 = new QName("", "verticalCentered");
        HEADINGS$4 = new QName("", "headings");
        GRIDLINES$6 = new QName("", "gridLines");
        GRIDLINESSET$8 = new QName("", "gridLinesSet");
    }
}
