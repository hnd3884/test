package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideTiming;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideTransition;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMappingOverride;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommonSlideData;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSlideImpl extends XmlComplexContentImpl implements CTSlide
{
    private static final long serialVersionUID = 1L;
    private static final QName CSLD$0;
    private static final QName CLRMAPOVR$2;
    private static final QName TRANSITION$4;
    private static final QName TIMING$6;
    private static final QName EXTLST$8;
    private static final QName SHOWMASTERSP$10;
    private static final QName SHOWMASTERPHANIM$12;
    private static final QName SHOW$14;
    
    public CTSlideImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTCommonSlideData getCSld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCommonSlideData ctCommonSlideData = (CTCommonSlideData)this.get_store().find_element_user(CTSlideImpl.CSLD$0, 0);
            if (ctCommonSlideData == null) {
                return null;
            }
            return ctCommonSlideData;
        }
    }
    
    public void setCSld(final CTCommonSlideData ctCommonSlideData) {
        this.generatedSetterHelperImpl((XmlObject)ctCommonSlideData, CTSlideImpl.CSLD$0, 0, (short)1);
    }
    
    public CTCommonSlideData addNewCSld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCommonSlideData)this.get_store().add_element_user(CTSlideImpl.CSLD$0);
        }
    }
    
    public CTColorMappingOverride getClrMapOvr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColorMappingOverride ctColorMappingOverride = (CTColorMappingOverride)this.get_store().find_element_user(CTSlideImpl.CLRMAPOVR$2, 0);
            if (ctColorMappingOverride == null) {
                return null;
            }
            return ctColorMappingOverride;
        }
    }
    
    public boolean isSetClrMapOvr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideImpl.CLRMAPOVR$2) != 0;
        }
    }
    
    public void setClrMapOvr(final CTColorMappingOverride ctColorMappingOverride) {
        this.generatedSetterHelperImpl((XmlObject)ctColorMappingOverride, CTSlideImpl.CLRMAPOVR$2, 0, (short)1);
    }
    
    public CTColorMappingOverride addNewClrMapOvr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorMappingOverride)this.get_store().add_element_user(CTSlideImpl.CLRMAPOVR$2);
        }
    }
    
    public void unsetClrMapOvr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideImpl.CLRMAPOVR$2, 0);
        }
    }
    
    public CTSlideTransition getTransition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSlideTransition ctSlideTransition = (CTSlideTransition)this.get_store().find_element_user(CTSlideImpl.TRANSITION$4, 0);
            if (ctSlideTransition == null) {
                return null;
            }
            return ctSlideTransition;
        }
    }
    
    public boolean isSetTransition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideImpl.TRANSITION$4) != 0;
        }
    }
    
    public void setTransition(final CTSlideTransition ctSlideTransition) {
        this.generatedSetterHelperImpl((XmlObject)ctSlideTransition, CTSlideImpl.TRANSITION$4, 0, (short)1);
    }
    
    public CTSlideTransition addNewTransition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideTransition)this.get_store().add_element_user(CTSlideImpl.TRANSITION$4);
        }
    }
    
    public void unsetTransition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideImpl.TRANSITION$4, 0);
        }
    }
    
    public CTSlideTiming getTiming() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSlideTiming ctSlideTiming = (CTSlideTiming)this.get_store().find_element_user(CTSlideImpl.TIMING$6, 0);
            if (ctSlideTiming == null) {
                return null;
            }
            return ctSlideTiming;
        }
    }
    
    public boolean isSetTiming() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideImpl.TIMING$6) != 0;
        }
    }
    
    public void setTiming(final CTSlideTiming ctSlideTiming) {
        this.generatedSetterHelperImpl((XmlObject)ctSlideTiming, CTSlideImpl.TIMING$6, 0, (short)1);
    }
    
    public CTSlideTiming addNewTiming() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideTiming)this.get_store().add_element_user(CTSlideImpl.TIMING$6);
        }
    }
    
    public void unsetTiming() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideImpl.TIMING$6, 0);
        }
    }
    
    public CTExtensionListModify getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionListModify ctExtensionListModify = (CTExtensionListModify)this.get_store().find_element_user(CTSlideImpl.EXTLST$8, 0);
            if (ctExtensionListModify == null) {
                return null;
            }
            return ctExtensionListModify;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideImpl.EXTLST$8) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionListModify ctExtensionListModify) {
        this.generatedSetterHelperImpl((XmlObject)ctExtensionListModify, CTSlideImpl.EXTLST$8, 0, (short)1);
    }
    
    public CTExtensionListModify addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionListModify)this.get_store().add_element_user(CTSlideImpl.EXTLST$8);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideImpl.EXTLST$8, 0);
        }
    }
    
    public boolean getShowMasterSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideImpl.SHOWMASTERSP$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSlideImpl.SHOWMASTERSP$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowMasterSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSlideImpl.SHOWMASTERSP$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSlideImpl.SHOWMASTERSP$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowMasterSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSlideImpl.SHOWMASTERSP$10) != null;
        }
    }
    
    public void setShowMasterSp(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideImpl.SHOWMASTERSP$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSlideImpl.SHOWMASTERSP$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowMasterSp(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSlideImpl.SHOWMASTERSP$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSlideImpl.SHOWMASTERSP$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowMasterSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSlideImpl.SHOWMASTERSP$10);
        }
    }
    
    public boolean getShowMasterPhAnim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideImpl.SHOWMASTERPHANIM$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSlideImpl.SHOWMASTERPHANIM$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowMasterPhAnim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSlideImpl.SHOWMASTERPHANIM$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSlideImpl.SHOWMASTERPHANIM$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowMasterPhAnim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSlideImpl.SHOWMASTERPHANIM$12) != null;
        }
    }
    
    public void setShowMasterPhAnim(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideImpl.SHOWMASTERPHANIM$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSlideImpl.SHOWMASTERPHANIM$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowMasterPhAnim(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSlideImpl.SHOWMASTERPHANIM$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSlideImpl.SHOWMASTERPHANIM$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowMasterPhAnim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSlideImpl.SHOWMASTERPHANIM$12);
        }
    }
    
    public boolean getShow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideImpl.SHOW$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSlideImpl.SHOW$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSlideImpl.SHOW$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSlideImpl.SHOW$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSlideImpl.SHOW$14) != null;
        }
    }
    
    public void setShow(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideImpl.SHOW$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSlideImpl.SHOW$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShow(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSlideImpl.SHOW$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSlideImpl.SHOW$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSlideImpl.SHOW$14);
        }
    }
    
    static {
        CSLD$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cSld");
        CLRMAPOVR$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "clrMapOvr");
        TRANSITION$4 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "transition");
        TIMING$6 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "timing");
        EXTLST$8 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
        SHOWMASTERSP$10 = new QName("", "showMasterSp");
        SHOWMASTERPHANIM$12 = new QName("", "showMasterPhAnim");
        SHOW$14 = new QName("", "show");
    }
}
