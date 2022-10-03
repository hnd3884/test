package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOutlinePr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTOutlinePrImpl extends XmlComplexContentImpl implements CTOutlinePr
{
    private static final long serialVersionUID = 1L;
    private static final QName APPLYSTYLES$0;
    private static final QName SUMMARYBELOW$2;
    private static final QName SUMMARYRIGHT$4;
    private static final QName SHOWOUTLINESYMBOLS$6;
    
    public CTOutlinePrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public boolean getApplyStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOutlinePrImpl.APPLYSTYLES$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOutlinePrImpl.APPLYSTYLES$0);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetApplyStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTOutlinePrImpl.APPLYSTYLES$0);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTOutlinePrImpl.APPLYSTYLES$0);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetApplyStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOutlinePrImpl.APPLYSTYLES$0) != null;
        }
    }
    
    public void setApplyStyles(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOutlinePrImpl.APPLYSTYLES$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOutlinePrImpl.APPLYSTYLES$0);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetApplyStyles(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTOutlinePrImpl.APPLYSTYLES$0);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTOutlinePrImpl.APPLYSTYLES$0);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetApplyStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOutlinePrImpl.APPLYSTYLES$0);
        }
    }
    
    public boolean getSummaryBelow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOutlinePrImpl.SUMMARYBELOW$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOutlinePrImpl.SUMMARYBELOW$2);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSummaryBelow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTOutlinePrImpl.SUMMARYBELOW$2);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTOutlinePrImpl.SUMMARYBELOW$2);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSummaryBelow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOutlinePrImpl.SUMMARYBELOW$2) != null;
        }
    }
    
    public void setSummaryBelow(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOutlinePrImpl.SUMMARYBELOW$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOutlinePrImpl.SUMMARYBELOW$2);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSummaryBelow(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTOutlinePrImpl.SUMMARYBELOW$2);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTOutlinePrImpl.SUMMARYBELOW$2);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSummaryBelow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOutlinePrImpl.SUMMARYBELOW$2);
        }
    }
    
    public boolean getSummaryRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOutlinePrImpl.SUMMARYRIGHT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOutlinePrImpl.SUMMARYRIGHT$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSummaryRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTOutlinePrImpl.SUMMARYRIGHT$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTOutlinePrImpl.SUMMARYRIGHT$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSummaryRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOutlinePrImpl.SUMMARYRIGHT$4) != null;
        }
    }
    
    public void setSummaryRight(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOutlinePrImpl.SUMMARYRIGHT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOutlinePrImpl.SUMMARYRIGHT$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSummaryRight(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTOutlinePrImpl.SUMMARYRIGHT$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTOutlinePrImpl.SUMMARYRIGHT$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSummaryRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOutlinePrImpl.SUMMARYRIGHT$4);
        }
    }
    
    public boolean getShowOutlineSymbols() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOutlinePrImpl.SHOWOUTLINESYMBOLS$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOutlinePrImpl.SHOWOUTLINESYMBOLS$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowOutlineSymbols() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTOutlinePrImpl.SHOWOUTLINESYMBOLS$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTOutlinePrImpl.SHOWOUTLINESYMBOLS$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowOutlineSymbols() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOutlinePrImpl.SHOWOUTLINESYMBOLS$6) != null;
        }
    }
    
    public void setShowOutlineSymbols(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOutlinePrImpl.SHOWOUTLINESYMBOLS$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOutlinePrImpl.SHOWOUTLINESYMBOLS$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowOutlineSymbols(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTOutlinePrImpl.SHOWOUTLINESYMBOLS$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTOutlinePrImpl.SHOWOUTLINESYMBOLS$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowOutlineSymbols() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOutlinePrImpl.SHOWOUTLINESYMBOLS$6);
        }
    }
    
    static {
        APPLYSTYLES$0 = new QName("", "applyStyles");
        SUMMARYBELOW$2 = new QName("", "summaryBelow");
        SUMMARYRIGHT$4 = new QName("", "summaryRight");
        SHOWOUTLINESYMBOLS$6 = new QName("", "showOutlineSymbols");
    }
}
