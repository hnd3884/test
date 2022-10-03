package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterTextStyles;
import org.openxmlformats.schemas.presentationml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideTiming;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideTransition;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideLayoutIdList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMapping;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommonSlideData;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMaster;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSlideMasterImpl extends XmlComplexContentImpl implements CTSlideMaster
{
    private static final long serialVersionUID = 1L;
    private static final QName CSLD$0;
    private static final QName CLRMAP$2;
    private static final QName SLDLAYOUTIDLST$4;
    private static final QName TRANSITION$6;
    private static final QName TIMING$8;
    private static final QName HF$10;
    private static final QName TXSTYLES$12;
    private static final QName EXTLST$14;
    private static final QName PRESERVE$16;
    
    public CTSlideMasterImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTCommonSlideData getCSld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCommonSlideData ctCommonSlideData = (CTCommonSlideData)this.get_store().find_element_user(CTSlideMasterImpl.CSLD$0, 0);
            if (ctCommonSlideData == null) {
                return null;
            }
            return ctCommonSlideData;
        }
    }
    
    public void setCSld(final CTCommonSlideData ctCommonSlideData) {
        this.generatedSetterHelperImpl((XmlObject)ctCommonSlideData, CTSlideMasterImpl.CSLD$0, 0, (short)1);
    }
    
    public CTCommonSlideData addNewCSld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCommonSlideData)this.get_store().add_element_user(CTSlideMasterImpl.CSLD$0);
        }
    }
    
    public CTColorMapping getClrMap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColorMapping ctColorMapping = (CTColorMapping)this.get_store().find_element_user(CTSlideMasterImpl.CLRMAP$2, 0);
            if (ctColorMapping == null) {
                return null;
            }
            return ctColorMapping;
        }
    }
    
    public void setClrMap(final CTColorMapping ctColorMapping) {
        this.generatedSetterHelperImpl((XmlObject)ctColorMapping, CTSlideMasterImpl.CLRMAP$2, 0, (short)1);
    }
    
    public CTColorMapping addNewClrMap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorMapping)this.get_store().add_element_user(CTSlideMasterImpl.CLRMAP$2);
        }
    }
    
    public CTSlideLayoutIdList getSldLayoutIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSlideLayoutIdList list = (CTSlideLayoutIdList)this.get_store().find_element_user(CTSlideMasterImpl.SLDLAYOUTIDLST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetSldLayoutIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideMasterImpl.SLDLAYOUTIDLST$4) != 0;
        }
    }
    
    public void setSldLayoutIdLst(final CTSlideLayoutIdList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTSlideMasterImpl.SLDLAYOUTIDLST$4, 0, (short)1);
    }
    
    public CTSlideLayoutIdList addNewSldLayoutIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideLayoutIdList)this.get_store().add_element_user(CTSlideMasterImpl.SLDLAYOUTIDLST$4);
        }
    }
    
    public void unsetSldLayoutIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideMasterImpl.SLDLAYOUTIDLST$4, 0);
        }
    }
    
    public CTSlideTransition getTransition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSlideTransition ctSlideTransition = (CTSlideTransition)this.get_store().find_element_user(CTSlideMasterImpl.TRANSITION$6, 0);
            if (ctSlideTransition == null) {
                return null;
            }
            return ctSlideTransition;
        }
    }
    
    public boolean isSetTransition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideMasterImpl.TRANSITION$6) != 0;
        }
    }
    
    public void setTransition(final CTSlideTransition ctSlideTransition) {
        this.generatedSetterHelperImpl((XmlObject)ctSlideTransition, CTSlideMasterImpl.TRANSITION$6, 0, (short)1);
    }
    
    public CTSlideTransition addNewTransition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideTransition)this.get_store().add_element_user(CTSlideMasterImpl.TRANSITION$6);
        }
    }
    
    public void unsetTransition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideMasterImpl.TRANSITION$6, 0);
        }
    }
    
    public CTSlideTiming getTiming() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSlideTiming ctSlideTiming = (CTSlideTiming)this.get_store().find_element_user(CTSlideMasterImpl.TIMING$8, 0);
            if (ctSlideTiming == null) {
                return null;
            }
            return ctSlideTiming;
        }
    }
    
    public boolean isSetTiming() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideMasterImpl.TIMING$8) != 0;
        }
    }
    
    public void setTiming(final CTSlideTiming ctSlideTiming) {
        this.generatedSetterHelperImpl((XmlObject)ctSlideTiming, CTSlideMasterImpl.TIMING$8, 0, (short)1);
    }
    
    public CTSlideTiming addNewTiming() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideTiming)this.get_store().add_element_user(CTSlideMasterImpl.TIMING$8);
        }
    }
    
    public void unsetTiming() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideMasterImpl.TIMING$8, 0);
        }
    }
    
    public CTHeaderFooter getHf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHeaderFooter ctHeaderFooter = (CTHeaderFooter)this.get_store().find_element_user(CTSlideMasterImpl.HF$10, 0);
            if (ctHeaderFooter == null) {
                return null;
            }
            return ctHeaderFooter;
        }
    }
    
    public boolean isSetHf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideMasterImpl.HF$10) != 0;
        }
    }
    
    public void setHf(final CTHeaderFooter ctHeaderFooter) {
        this.generatedSetterHelperImpl((XmlObject)ctHeaderFooter, CTSlideMasterImpl.HF$10, 0, (short)1);
    }
    
    public CTHeaderFooter addNewHf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHeaderFooter)this.get_store().add_element_user(CTSlideMasterImpl.HF$10);
        }
    }
    
    public void unsetHf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideMasterImpl.HF$10, 0);
        }
    }
    
    public CTSlideMasterTextStyles getTxStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSlideMasterTextStyles ctSlideMasterTextStyles = (CTSlideMasterTextStyles)this.get_store().find_element_user(CTSlideMasterImpl.TXSTYLES$12, 0);
            if (ctSlideMasterTextStyles == null) {
                return null;
            }
            return ctSlideMasterTextStyles;
        }
    }
    
    public boolean isSetTxStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideMasterImpl.TXSTYLES$12) != 0;
        }
    }
    
    public void setTxStyles(final CTSlideMasterTextStyles ctSlideMasterTextStyles) {
        this.generatedSetterHelperImpl((XmlObject)ctSlideMasterTextStyles, CTSlideMasterImpl.TXSTYLES$12, 0, (short)1);
    }
    
    public CTSlideMasterTextStyles addNewTxStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideMasterTextStyles)this.get_store().add_element_user(CTSlideMasterImpl.TXSTYLES$12);
        }
    }
    
    public void unsetTxStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideMasterImpl.TXSTYLES$12, 0);
        }
    }
    
    public CTExtensionListModify getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionListModify ctExtensionListModify = (CTExtensionListModify)this.get_store().find_element_user(CTSlideMasterImpl.EXTLST$14, 0);
            if (ctExtensionListModify == null) {
                return null;
            }
            return ctExtensionListModify;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideMasterImpl.EXTLST$14) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionListModify ctExtensionListModify) {
        this.generatedSetterHelperImpl((XmlObject)ctExtensionListModify, CTSlideMasterImpl.EXTLST$14, 0, (short)1);
    }
    
    public CTExtensionListModify addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionListModify)this.get_store().add_element_user(CTSlideMasterImpl.EXTLST$14);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideMasterImpl.EXTLST$14, 0);
        }
    }
    
    public boolean getPreserve() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideMasterImpl.PRESERVE$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSlideMasterImpl.PRESERVE$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPreserve() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSlideMasterImpl.PRESERVE$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSlideMasterImpl.PRESERVE$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPreserve() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSlideMasterImpl.PRESERVE$16) != null;
        }
    }
    
    public void setPreserve(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideMasterImpl.PRESERVE$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSlideMasterImpl.PRESERVE$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPreserve(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSlideMasterImpl.PRESERVE$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSlideMasterImpl.PRESERVE$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPreserve() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSlideMasterImpl.PRESERVE$16);
        }
    }
    
    static {
        CSLD$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cSld");
        CLRMAP$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "clrMap");
        SLDLAYOUTIDLST$4 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "sldLayoutIdLst");
        TRANSITION$6 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "transition");
        TIMING$8 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "timing");
        HF$10 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "hf");
        TXSTYLES$12 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "txStyles");
        EXTLST$14 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
        PRESERVE$16 = new QName("", "preserve");
    }
}
