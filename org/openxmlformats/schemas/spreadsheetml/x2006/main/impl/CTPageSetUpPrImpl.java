package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageSetUpPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPageSetUpPrImpl extends XmlComplexContentImpl implements CTPageSetUpPr
{
    private static final long serialVersionUID = 1L;
    private static final QName AUTOPAGEBREAKS$0;
    private static final QName FITTOPAGE$2;
    
    public CTPageSetUpPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public boolean getAutoPageBreaks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetUpPrImpl.AUTOPAGEBREAKS$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetUpPrImpl.AUTOPAGEBREAKS$0);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAutoPageBreaks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetUpPrImpl.AUTOPAGEBREAKS$0);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPageSetUpPrImpl.AUTOPAGEBREAKS$0);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetAutoPageBreaks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetUpPrImpl.AUTOPAGEBREAKS$0) != null;
        }
    }
    
    public void setAutoPageBreaks(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetUpPrImpl.AUTOPAGEBREAKS$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetUpPrImpl.AUTOPAGEBREAKS$0);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAutoPageBreaks(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetUpPrImpl.AUTOPAGEBREAKS$0);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPageSetUpPrImpl.AUTOPAGEBREAKS$0);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAutoPageBreaks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetUpPrImpl.AUTOPAGEBREAKS$0);
        }
    }
    
    public boolean getFitToPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetUpPrImpl.FITTOPAGE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetUpPrImpl.FITTOPAGE$2);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFitToPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetUpPrImpl.FITTOPAGE$2);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPageSetUpPrImpl.FITTOPAGE$2);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFitToPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetUpPrImpl.FITTOPAGE$2) != null;
        }
    }
    
    public void setFitToPage(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetUpPrImpl.FITTOPAGE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetUpPrImpl.FITTOPAGE$2);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFitToPage(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetUpPrImpl.FITTOPAGE$2);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPageSetUpPrImpl.FITTOPAGE$2);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFitToPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetUpPrImpl.FITTOPAGE$2);
        }
    }
    
    static {
        AUTOPAGEBREAKS$0 = new QName("", "autoPageBreaks");
        FITTOPAGE$2 = new QName("", "fitToPage");
    }
}
