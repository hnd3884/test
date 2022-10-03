package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPictureLocking;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualPictureProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNonVisualPicturePropertiesImpl extends XmlComplexContentImpl implements CTNonVisualPictureProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName PICLOCKS$0;
    private static final QName EXTLST$2;
    private static final QName PREFERRELATIVERESIZE$4;
    
    public CTNonVisualPicturePropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTPictureLocking getPicLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPictureLocking ctPictureLocking = (CTPictureLocking)this.get_store().find_element_user(CTNonVisualPicturePropertiesImpl.PICLOCKS$0, 0);
            if (ctPictureLocking == null) {
                return null;
            }
            return ctPictureLocking;
        }
    }
    
    public boolean isSetPicLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNonVisualPicturePropertiesImpl.PICLOCKS$0) != 0;
        }
    }
    
    public void setPicLocks(final CTPictureLocking ctPictureLocking) {
        this.generatedSetterHelperImpl((XmlObject)ctPictureLocking, CTNonVisualPicturePropertiesImpl.PICLOCKS$0, 0, (short)1);
    }
    
    public CTPictureLocking addNewPicLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPictureLocking)this.get_store().add_element_user(CTNonVisualPicturePropertiesImpl.PICLOCKS$0);
        }
    }
    
    public void unsetPicLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNonVisualPicturePropertiesImpl.PICLOCKS$0, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTNonVisualPicturePropertiesImpl.EXTLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNonVisualPicturePropertiesImpl.EXTLST$2) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTNonVisualPicturePropertiesImpl.EXTLST$2, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTNonVisualPicturePropertiesImpl.EXTLST$2);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNonVisualPicturePropertiesImpl.EXTLST$2, 0);
        }
    }
    
    public boolean getPreferRelativeResize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNonVisualPicturePropertiesImpl.PREFERRELATIVERESIZE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTNonVisualPicturePropertiesImpl.PREFERRELATIVERESIZE$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPreferRelativeResize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTNonVisualPicturePropertiesImpl.PREFERRELATIVERESIZE$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTNonVisualPicturePropertiesImpl.PREFERRELATIVERESIZE$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPreferRelativeResize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTNonVisualPicturePropertiesImpl.PREFERRELATIVERESIZE$4) != null;
        }
    }
    
    public void setPreferRelativeResize(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNonVisualPicturePropertiesImpl.PREFERRELATIVERESIZE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNonVisualPicturePropertiesImpl.PREFERRELATIVERESIZE$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPreferRelativeResize(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTNonVisualPicturePropertiesImpl.PREFERRELATIVERESIZE$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTNonVisualPicturePropertiesImpl.PREFERRELATIVERESIZE$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPreferRelativeResize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTNonVisualPicturePropertiesImpl.PREFERRELATIVERESIZE$4);
        }
    }
    
    static {
        PICLOCKS$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "picLocks");
        EXTLST$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        PREFERRELATIVERESIZE$4 = new QName("", "preferRelativeResize");
    }
}
