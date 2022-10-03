package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetCalcPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSheetCalcPrImpl extends XmlComplexContentImpl implements CTSheetCalcPr
{
    private static final long serialVersionUID = 1L;
    private static final QName FULLCALCONLOAD$0;
    
    public CTSheetCalcPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public boolean getFullCalcOnLoad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetCalcPrImpl.FULLCALCONLOAD$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetCalcPrImpl.FULLCALCONLOAD$0);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFullCalcOnLoad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetCalcPrImpl.FULLCALCONLOAD$0);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetCalcPrImpl.FULLCALCONLOAD$0);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFullCalcOnLoad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetCalcPrImpl.FULLCALCONLOAD$0) != null;
        }
    }
    
    public void setFullCalcOnLoad(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetCalcPrImpl.FULLCALCONLOAD$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetCalcPrImpl.FULLCALCONLOAD$0);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFullCalcOnLoad(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetCalcPrImpl.FULLCALCONLOAD$0);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetCalcPrImpl.FULLCALCONLOAD$0);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFullCalcOnLoad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetCalcPrImpl.FULLCALCONLOAD$0);
        }
    }
    
    static {
        FULLCALCONLOAD$0 = new QName("", "fullCalcOnLoad");
    }
}
