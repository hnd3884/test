package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTAnchorClientData;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTAnchorClientDataImpl extends XmlComplexContentImpl implements CTAnchorClientData
{
    private static final long serialVersionUID = 1L;
    private static final QName FLOCKSWITHSHEET$0;
    private static final QName FPRINTSWITHSHEET$2;
    
    public CTAnchorClientDataImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public boolean getFLocksWithSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorClientDataImpl.FLOCKSWITHSHEET$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTAnchorClientDataImpl.FLOCKSWITHSHEET$0);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFLocksWithSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTAnchorClientDataImpl.FLOCKSWITHSHEET$0);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTAnchorClientDataImpl.FLOCKSWITHSHEET$0);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFLocksWithSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTAnchorClientDataImpl.FLOCKSWITHSHEET$0) != null;
        }
    }
    
    public void setFLocksWithSheet(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorClientDataImpl.FLOCKSWITHSHEET$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAnchorClientDataImpl.FLOCKSWITHSHEET$0);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFLocksWithSheet(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTAnchorClientDataImpl.FLOCKSWITHSHEET$0);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTAnchorClientDataImpl.FLOCKSWITHSHEET$0);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFLocksWithSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTAnchorClientDataImpl.FLOCKSWITHSHEET$0);
        }
    }
    
    public boolean getFPrintsWithSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorClientDataImpl.FPRINTSWITHSHEET$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTAnchorClientDataImpl.FPRINTSWITHSHEET$2);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFPrintsWithSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTAnchorClientDataImpl.FPRINTSWITHSHEET$2);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTAnchorClientDataImpl.FPRINTSWITHSHEET$2);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFPrintsWithSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTAnchorClientDataImpl.FPRINTSWITHSHEET$2) != null;
        }
    }
    
    public void setFPrintsWithSheet(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAnchorClientDataImpl.FPRINTSWITHSHEET$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAnchorClientDataImpl.FPRINTSWITHSHEET$2);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFPrintsWithSheet(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTAnchorClientDataImpl.FPRINTSWITHSHEET$2);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTAnchorClientDataImpl.FPRINTSWITHSHEET$2);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFPrintsWithSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTAnchorClientDataImpl.FPRINTSWITHSHEET$2);
        }
    }
    
    static {
        FLOCKSWITHSHEET$0 = new QName("", "fLocksWithSheet");
        FPRINTSWITHSHEET$2 = new QName("", "fPrintsWithSheet");
    }
}
