package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMappingOverride;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommonSlideData;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesSlide;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNotesSlideImpl extends XmlComplexContentImpl implements CTNotesSlide
{
    private static final long serialVersionUID = 1L;
    private static final QName CSLD$0;
    private static final QName CLRMAPOVR$2;
    private static final QName EXTLST$4;
    private static final QName SHOWMASTERSP$6;
    private static final QName SHOWMASTERPHANIM$8;
    
    public CTNotesSlideImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTCommonSlideData getCSld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCommonSlideData ctCommonSlideData = (CTCommonSlideData)this.get_store().find_element_user(CTNotesSlideImpl.CSLD$0, 0);
            if (ctCommonSlideData == null) {
                return null;
            }
            return ctCommonSlideData;
        }
    }
    
    public void setCSld(final CTCommonSlideData ctCommonSlideData) {
        this.generatedSetterHelperImpl((XmlObject)ctCommonSlideData, CTNotesSlideImpl.CSLD$0, 0, (short)1);
    }
    
    public CTCommonSlideData addNewCSld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCommonSlideData)this.get_store().add_element_user(CTNotesSlideImpl.CSLD$0);
        }
    }
    
    public CTColorMappingOverride getClrMapOvr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColorMappingOverride ctColorMappingOverride = (CTColorMappingOverride)this.get_store().find_element_user(CTNotesSlideImpl.CLRMAPOVR$2, 0);
            if (ctColorMappingOverride == null) {
                return null;
            }
            return ctColorMappingOverride;
        }
    }
    
    public boolean isSetClrMapOvr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNotesSlideImpl.CLRMAPOVR$2) != 0;
        }
    }
    
    public void setClrMapOvr(final CTColorMappingOverride ctColorMappingOverride) {
        this.generatedSetterHelperImpl((XmlObject)ctColorMappingOverride, CTNotesSlideImpl.CLRMAPOVR$2, 0, (short)1);
    }
    
    public CTColorMappingOverride addNewClrMapOvr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorMappingOverride)this.get_store().add_element_user(CTNotesSlideImpl.CLRMAPOVR$2);
        }
    }
    
    public void unsetClrMapOvr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNotesSlideImpl.CLRMAPOVR$2, 0);
        }
    }
    
    public CTExtensionListModify getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionListModify ctExtensionListModify = (CTExtensionListModify)this.get_store().find_element_user(CTNotesSlideImpl.EXTLST$4, 0);
            if (ctExtensionListModify == null) {
                return null;
            }
            return ctExtensionListModify;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNotesSlideImpl.EXTLST$4) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionListModify ctExtensionListModify) {
        this.generatedSetterHelperImpl((XmlObject)ctExtensionListModify, CTNotesSlideImpl.EXTLST$4, 0, (short)1);
    }
    
    public CTExtensionListModify addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionListModify)this.get_store().add_element_user(CTNotesSlideImpl.EXTLST$4);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNotesSlideImpl.EXTLST$4, 0);
        }
    }
    
    public boolean getShowMasterSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNotesSlideImpl.SHOWMASTERSP$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTNotesSlideImpl.SHOWMASTERSP$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowMasterSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTNotesSlideImpl.SHOWMASTERSP$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTNotesSlideImpl.SHOWMASTERSP$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowMasterSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTNotesSlideImpl.SHOWMASTERSP$6) != null;
        }
    }
    
    public void setShowMasterSp(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNotesSlideImpl.SHOWMASTERSP$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNotesSlideImpl.SHOWMASTERSP$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowMasterSp(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTNotesSlideImpl.SHOWMASTERSP$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTNotesSlideImpl.SHOWMASTERSP$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowMasterSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTNotesSlideImpl.SHOWMASTERSP$6);
        }
    }
    
    public boolean getShowMasterPhAnim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNotesSlideImpl.SHOWMASTERPHANIM$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTNotesSlideImpl.SHOWMASTERPHANIM$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowMasterPhAnim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTNotesSlideImpl.SHOWMASTERPHANIM$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTNotesSlideImpl.SHOWMASTERPHANIM$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowMasterPhAnim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTNotesSlideImpl.SHOWMASTERPHANIM$8) != null;
        }
    }
    
    public void setShowMasterPhAnim(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNotesSlideImpl.SHOWMASTERPHANIM$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNotesSlideImpl.SHOWMASTERPHANIM$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowMasterPhAnim(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTNotesSlideImpl.SHOWMASTERPHANIM$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTNotesSlideImpl.SHOWMASTERPHANIM$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowMasterPhAnim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTNotesSlideImpl.SHOWMASTERPHANIM$8);
        }
    }
    
    static {
        CSLD$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cSld");
        CLRMAPOVR$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "clrMapOvr");
        EXTLST$4 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
        SHOWMASTERSP$6 = new QName("", "showMasterSp");
        SHOWMASTERPHANIM$8 = new QName("", "showMasterPhAnim");
    }
}
