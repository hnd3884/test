package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTHeaderFooter;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTHeaderFooterImpl extends XmlComplexContentImpl implements CTHeaderFooter
{
    private static final long serialVersionUID = 1L;
    private static final QName ODDHEADER$0;
    private static final QName ODDFOOTER$2;
    private static final QName EVENHEADER$4;
    private static final QName EVENFOOTER$6;
    private static final QName FIRSTHEADER$8;
    private static final QName FIRSTFOOTER$10;
    private static final QName ALIGNWITHMARGINS$12;
    private static final QName DIFFERENTODDEVEN$14;
    private static final QName DIFFERENTFIRST$16;
    
    public CTHeaderFooterImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getOddHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTHeaderFooterImpl.ODDHEADER$0, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetOddHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_element_user(CTHeaderFooterImpl.ODDHEADER$0, 0);
        }
    }
    
    public boolean isSetOddHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHeaderFooterImpl.ODDHEADER$0) != 0;
        }
    }
    
    public void setOddHeader(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTHeaderFooterImpl.ODDHEADER$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTHeaderFooterImpl.ODDHEADER$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetOddHeader(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_element_user(CTHeaderFooterImpl.ODDHEADER$0, 0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_element_user(CTHeaderFooterImpl.ODDHEADER$0);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetOddHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHeaderFooterImpl.ODDHEADER$0, 0);
        }
    }
    
    public String getOddFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTHeaderFooterImpl.ODDFOOTER$2, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetOddFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_element_user(CTHeaderFooterImpl.ODDFOOTER$2, 0);
        }
    }
    
    public boolean isSetOddFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHeaderFooterImpl.ODDFOOTER$2) != 0;
        }
    }
    
    public void setOddFooter(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTHeaderFooterImpl.ODDFOOTER$2, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTHeaderFooterImpl.ODDFOOTER$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetOddFooter(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_element_user(CTHeaderFooterImpl.ODDFOOTER$2, 0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_element_user(CTHeaderFooterImpl.ODDFOOTER$2);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetOddFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHeaderFooterImpl.ODDFOOTER$2, 0);
        }
    }
    
    public String getEvenHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTHeaderFooterImpl.EVENHEADER$4, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetEvenHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_element_user(CTHeaderFooterImpl.EVENHEADER$4, 0);
        }
    }
    
    public boolean isSetEvenHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHeaderFooterImpl.EVENHEADER$4) != 0;
        }
    }
    
    public void setEvenHeader(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTHeaderFooterImpl.EVENHEADER$4, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTHeaderFooterImpl.EVENHEADER$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetEvenHeader(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_element_user(CTHeaderFooterImpl.EVENHEADER$4, 0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_element_user(CTHeaderFooterImpl.EVENHEADER$4);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetEvenHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHeaderFooterImpl.EVENHEADER$4, 0);
        }
    }
    
    public String getEvenFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTHeaderFooterImpl.EVENFOOTER$6, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetEvenFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_element_user(CTHeaderFooterImpl.EVENFOOTER$6, 0);
        }
    }
    
    public boolean isSetEvenFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHeaderFooterImpl.EVENFOOTER$6) != 0;
        }
    }
    
    public void setEvenFooter(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTHeaderFooterImpl.EVENFOOTER$6, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTHeaderFooterImpl.EVENFOOTER$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetEvenFooter(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_element_user(CTHeaderFooterImpl.EVENFOOTER$6, 0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_element_user(CTHeaderFooterImpl.EVENFOOTER$6);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetEvenFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHeaderFooterImpl.EVENFOOTER$6, 0);
        }
    }
    
    public String getFirstHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTHeaderFooterImpl.FIRSTHEADER$8, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetFirstHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_element_user(CTHeaderFooterImpl.FIRSTHEADER$8, 0);
        }
    }
    
    public boolean isSetFirstHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHeaderFooterImpl.FIRSTHEADER$8) != 0;
        }
    }
    
    public void setFirstHeader(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTHeaderFooterImpl.FIRSTHEADER$8, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTHeaderFooterImpl.FIRSTHEADER$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFirstHeader(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_element_user(CTHeaderFooterImpl.FIRSTHEADER$8, 0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_element_user(CTHeaderFooterImpl.FIRSTHEADER$8);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetFirstHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHeaderFooterImpl.FIRSTHEADER$8, 0);
        }
    }
    
    public String getFirstFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTHeaderFooterImpl.FIRSTFOOTER$10, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetFirstFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_element_user(CTHeaderFooterImpl.FIRSTFOOTER$10, 0);
        }
    }
    
    public boolean isSetFirstFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHeaderFooterImpl.FIRSTFOOTER$10) != 0;
        }
    }
    
    public void setFirstFooter(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTHeaderFooterImpl.FIRSTFOOTER$10, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTHeaderFooterImpl.FIRSTFOOTER$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFirstFooter(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_element_user(CTHeaderFooterImpl.FIRSTFOOTER$10, 0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_element_user(CTHeaderFooterImpl.FIRSTFOOTER$10);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetFirstFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHeaderFooterImpl.FIRSTFOOTER$10, 0);
        }
    }
    
    public boolean getAlignWithMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeaderFooterImpl.ALIGNWITHMARGINS$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTHeaderFooterImpl.ALIGNWITHMARGINS$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAlignWithMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTHeaderFooterImpl.ALIGNWITHMARGINS$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTHeaderFooterImpl.ALIGNWITHMARGINS$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetAlignWithMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHeaderFooterImpl.ALIGNWITHMARGINS$12) != null;
        }
    }
    
    public void setAlignWithMargins(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeaderFooterImpl.ALIGNWITHMARGINS$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHeaderFooterImpl.ALIGNWITHMARGINS$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAlignWithMargins(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTHeaderFooterImpl.ALIGNWITHMARGINS$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTHeaderFooterImpl.ALIGNWITHMARGINS$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAlignWithMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHeaderFooterImpl.ALIGNWITHMARGINS$12);
        }
    }
    
    public boolean getDifferentOddEven() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeaderFooterImpl.DIFFERENTODDEVEN$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTHeaderFooterImpl.DIFFERENTODDEVEN$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDifferentOddEven() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTHeaderFooterImpl.DIFFERENTODDEVEN$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTHeaderFooterImpl.DIFFERENTODDEVEN$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDifferentOddEven() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHeaderFooterImpl.DIFFERENTODDEVEN$14) != null;
        }
    }
    
    public void setDifferentOddEven(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeaderFooterImpl.DIFFERENTODDEVEN$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHeaderFooterImpl.DIFFERENTODDEVEN$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDifferentOddEven(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTHeaderFooterImpl.DIFFERENTODDEVEN$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTHeaderFooterImpl.DIFFERENTODDEVEN$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDifferentOddEven() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHeaderFooterImpl.DIFFERENTODDEVEN$14);
        }
    }
    
    public boolean getDifferentFirst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeaderFooterImpl.DIFFERENTFIRST$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTHeaderFooterImpl.DIFFERENTFIRST$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDifferentFirst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTHeaderFooterImpl.DIFFERENTFIRST$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTHeaderFooterImpl.DIFFERENTFIRST$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDifferentFirst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHeaderFooterImpl.DIFFERENTFIRST$16) != null;
        }
    }
    
    public void setDifferentFirst(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeaderFooterImpl.DIFFERENTFIRST$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHeaderFooterImpl.DIFFERENTFIRST$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDifferentFirst(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTHeaderFooterImpl.DIFFERENTFIRST$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTHeaderFooterImpl.DIFFERENTFIRST$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDifferentFirst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHeaderFooterImpl.DIFFERENTFIRST$16);
        }
    }
    
    static {
        ODDHEADER$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "oddHeader");
        ODDFOOTER$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "oddFooter");
        EVENHEADER$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "evenHeader");
        EVENFOOTER$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "evenFooter");
        FIRSTHEADER$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "firstHeader");
        FIRSTFOOTER$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "firstFooter");
        ALIGNWITHMARGINS$12 = new QName("", "alignWithMargins");
        DIFFERENTODDEVEN$14 = new QName("", "differentOddEven");
        DIFFERENTFIRST$16 = new QName("", "differentFirst");
    }
}
