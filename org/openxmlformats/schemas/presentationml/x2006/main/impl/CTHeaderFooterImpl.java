package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTHeaderFooter;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTHeaderFooterImpl extends XmlComplexContentImpl implements CTHeaderFooter
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTLST$0;
    private static final QName SLDNUM$2;
    private static final QName HDR$4;
    private static final QName FTR$6;
    private static final QName DT$8;
    
    public CTHeaderFooterImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTExtensionListModify getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionListModify ctExtensionListModify = (CTExtensionListModify)this.get_store().find_element_user(CTHeaderFooterImpl.EXTLST$0, 0);
            if (ctExtensionListModify == null) {
                return null;
            }
            return ctExtensionListModify;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHeaderFooterImpl.EXTLST$0) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionListModify ctExtensionListModify) {
        this.generatedSetterHelperImpl((XmlObject)ctExtensionListModify, CTHeaderFooterImpl.EXTLST$0, 0, (short)1);
    }
    
    public CTExtensionListModify addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionListModify)this.get_store().add_element_user(CTHeaderFooterImpl.EXTLST$0);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHeaderFooterImpl.EXTLST$0, 0);
        }
    }
    
    public boolean getSldNum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeaderFooterImpl.SLDNUM$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTHeaderFooterImpl.SLDNUM$2);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSldNum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTHeaderFooterImpl.SLDNUM$2);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTHeaderFooterImpl.SLDNUM$2);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSldNum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHeaderFooterImpl.SLDNUM$2) != null;
        }
    }
    
    public void setSldNum(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeaderFooterImpl.SLDNUM$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHeaderFooterImpl.SLDNUM$2);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSldNum(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTHeaderFooterImpl.SLDNUM$2);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTHeaderFooterImpl.SLDNUM$2);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSldNum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHeaderFooterImpl.SLDNUM$2);
        }
    }
    
    public boolean getHdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeaderFooterImpl.HDR$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTHeaderFooterImpl.HDR$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTHeaderFooterImpl.HDR$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTHeaderFooterImpl.HDR$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetHdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHeaderFooterImpl.HDR$4) != null;
        }
    }
    
    public void setHdr(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeaderFooterImpl.HDR$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHeaderFooterImpl.HDR$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHdr(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTHeaderFooterImpl.HDR$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTHeaderFooterImpl.HDR$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHeaderFooterImpl.HDR$4);
        }
    }
    
    public boolean getFtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeaderFooterImpl.FTR$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTHeaderFooterImpl.FTR$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTHeaderFooterImpl.FTR$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTHeaderFooterImpl.FTR$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHeaderFooterImpl.FTR$6) != null;
        }
    }
    
    public void setFtr(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeaderFooterImpl.FTR$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHeaderFooterImpl.FTR$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFtr(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTHeaderFooterImpl.FTR$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTHeaderFooterImpl.FTR$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHeaderFooterImpl.FTR$6);
        }
    }
    
    public boolean getDt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeaderFooterImpl.DT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTHeaderFooterImpl.DT$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTHeaderFooterImpl.DT$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTHeaderFooterImpl.DT$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHeaderFooterImpl.DT$8) != null;
        }
    }
    
    public void setDt(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHeaderFooterImpl.DT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHeaderFooterImpl.DT$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDt(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTHeaderFooterImpl.DT$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTHeaderFooterImpl.DT$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHeaderFooterImpl.DT$8);
        }
    }
    
    static {
        EXTLST$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
        SLDNUM$2 = new QName("", "sldNum");
        HDR$4 = new QName("", "hdr");
        FTR$6 = new QName("", "ftr");
        DT$8 = new QName("", "dt");
    }
}
