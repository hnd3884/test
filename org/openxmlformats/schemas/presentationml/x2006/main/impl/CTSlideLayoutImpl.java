package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.presentationml.x2006.main.STSlideLayoutType;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.presentationml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideTiming;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideTransition;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMappingOverride;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommonSlideData;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideLayout;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSlideLayoutImpl extends XmlComplexContentImpl implements CTSlideLayout
{
    private static final long serialVersionUID = 1L;
    private static final QName CSLD$0;
    private static final QName CLRMAPOVR$2;
    private static final QName TRANSITION$4;
    private static final QName TIMING$6;
    private static final QName HF$8;
    private static final QName EXTLST$10;
    private static final QName SHOWMASTERSP$12;
    private static final QName SHOWMASTERPHANIM$14;
    private static final QName MATCHINGNAME$16;
    private static final QName TYPE$18;
    private static final QName PRESERVE$20;
    private static final QName USERDRAWN$22;
    
    public CTSlideLayoutImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTCommonSlideData getCSld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCommonSlideData ctCommonSlideData = (CTCommonSlideData)this.get_store().find_element_user(CTSlideLayoutImpl.CSLD$0, 0);
            if (ctCommonSlideData == null) {
                return null;
            }
            return ctCommonSlideData;
        }
    }
    
    public void setCSld(final CTCommonSlideData ctCommonSlideData) {
        this.generatedSetterHelperImpl((XmlObject)ctCommonSlideData, CTSlideLayoutImpl.CSLD$0, 0, (short)1);
    }
    
    public CTCommonSlideData addNewCSld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCommonSlideData)this.get_store().add_element_user(CTSlideLayoutImpl.CSLD$0);
        }
    }
    
    public CTColorMappingOverride getClrMapOvr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColorMappingOverride ctColorMappingOverride = (CTColorMappingOverride)this.get_store().find_element_user(CTSlideLayoutImpl.CLRMAPOVR$2, 0);
            if (ctColorMappingOverride == null) {
                return null;
            }
            return ctColorMappingOverride;
        }
    }
    
    public boolean isSetClrMapOvr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideLayoutImpl.CLRMAPOVR$2) != 0;
        }
    }
    
    public void setClrMapOvr(final CTColorMappingOverride ctColorMappingOverride) {
        this.generatedSetterHelperImpl((XmlObject)ctColorMappingOverride, CTSlideLayoutImpl.CLRMAPOVR$2, 0, (short)1);
    }
    
    public CTColorMappingOverride addNewClrMapOvr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorMappingOverride)this.get_store().add_element_user(CTSlideLayoutImpl.CLRMAPOVR$2);
        }
    }
    
    public void unsetClrMapOvr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideLayoutImpl.CLRMAPOVR$2, 0);
        }
    }
    
    public CTSlideTransition getTransition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSlideTransition ctSlideTransition = (CTSlideTransition)this.get_store().find_element_user(CTSlideLayoutImpl.TRANSITION$4, 0);
            if (ctSlideTransition == null) {
                return null;
            }
            return ctSlideTransition;
        }
    }
    
    public boolean isSetTransition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideLayoutImpl.TRANSITION$4) != 0;
        }
    }
    
    public void setTransition(final CTSlideTransition ctSlideTransition) {
        this.generatedSetterHelperImpl((XmlObject)ctSlideTransition, CTSlideLayoutImpl.TRANSITION$4, 0, (short)1);
    }
    
    public CTSlideTransition addNewTransition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideTransition)this.get_store().add_element_user(CTSlideLayoutImpl.TRANSITION$4);
        }
    }
    
    public void unsetTransition() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideLayoutImpl.TRANSITION$4, 0);
        }
    }
    
    public CTSlideTiming getTiming() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSlideTiming ctSlideTiming = (CTSlideTiming)this.get_store().find_element_user(CTSlideLayoutImpl.TIMING$6, 0);
            if (ctSlideTiming == null) {
                return null;
            }
            return ctSlideTiming;
        }
    }
    
    public boolean isSetTiming() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideLayoutImpl.TIMING$6) != 0;
        }
    }
    
    public void setTiming(final CTSlideTiming ctSlideTiming) {
        this.generatedSetterHelperImpl((XmlObject)ctSlideTiming, CTSlideLayoutImpl.TIMING$6, 0, (short)1);
    }
    
    public CTSlideTiming addNewTiming() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideTiming)this.get_store().add_element_user(CTSlideLayoutImpl.TIMING$6);
        }
    }
    
    public void unsetTiming() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideLayoutImpl.TIMING$6, 0);
        }
    }
    
    public CTHeaderFooter getHf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHeaderFooter ctHeaderFooter = (CTHeaderFooter)this.get_store().find_element_user(CTSlideLayoutImpl.HF$8, 0);
            if (ctHeaderFooter == null) {
                return null;
            }
            return ctHeaderFooter;
        }
    }
    
    public boolean isSetHf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideLayoutImpl.HF$8) != 0;
        }
    }
    
    public void setHf(final CTHeaderFooter ctHeaderFooter) {
        this.generatedSetterHelperImpl((XmlObject)ctHeaderFooter, CTSlideLayoutImpl.HF$8, 0, (short)1);
    }
    
    public CTHeaderFooter addNewHf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHeaderFooter)this.get_store().add_element_user(CTSlideLayoutImpl.HF$8);
        }
    }
    
    public void unsetHf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideLayoutImpl.HF$8, 0);
        }
    }
    
    public CTExtensionListModify getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionListModify ctExtensionListModify = (CTExtensionListModify)this.get_store().find_element_user(CTSlideLayoutImpl.EXTLST$10, 0);
            if (ctExtensionListModify == null) {
                return null;
            }
            return ctExtensionListModify;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideLayoutImpl.EXTLST$10) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionListModify ctExtensionListModify) {
        this.generatedSetterHelperImpl((XmlObject)ctExtensionListModify, CTSlideLayoutImpl.EXTLST$10, 0, (short)1);
    }
    
    public CTExtensionListModify addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionListModify)this.get_store().add_element_user(CTSlideLayoutImpl.EXTLST$10);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideLayoutImpl.EXTLST$10, 0);
        }
    }
    
    public boolean getShowMasterSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideLayoutImpl.SHOWMASTERSP$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSlideLayoutImpl.SHOWMASTERSP$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowMasterSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSlideLayoutImpl.SHOWMASTERSP$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSlideLayoutImpl.SHOWMASTERSP$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowMasterSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSlideLayoutImpl.SHOWMASTERSP$12) != null;
        }
    }
    
    public void setShowMasterSp(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideLayoutImpl.SHOWMASTERSP$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSlideLayoutImpl.SHOWMASTERSP$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowMasterSp(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSlideLayoutImpl.SHOWMASTERSP$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSlideLayoutImpl.SHOWMASTERSP$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowMasterSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSlideLayoutImpl.SHOWMASTERSP$12);
        }
    }
    
    public boolean getShowMasterPhAnim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideLayoutImpl.SHOWMASTERPHANIM$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSlideLayoutImpl.SHOWMASTERPHANIM$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowMasterPhAnim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSlideLayoutImpl.SHOWMASTERPHANIM$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSlideLayoutImpl.SHOWMASTERPHANIM$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowMasterPhAnim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSlideLayoutImpl.SHOWMASTERPHANIM$14) != null;
        }
    }
    
    public void setShowMasterPhAnim(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideLayoutImpl.SHOWMASTERPHANIM$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSlideLayoutImpl.SHOWMASTERPHANIM$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowMasterPhAnim(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSlideLayoutImpl.SHOWMASTERPHANIM$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSlideLayoutImpl.SHOWMASTERPHANIM$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowMasterPhAnim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSlideLayoutImpl.SHOWMASTERPHANIM$14);
        }
    }
    
    public String getMatchingName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideLayoutImpl.MATCHINGNAME$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSlideLayoutImpl.MATCHINGNAME$16);
            }
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetMatchingName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString = (XmlString)this.get_store().find_attribute_user(CTSlideLayoutImpl.MATCHINGNAME$16);
            if (xmlString == null) {
                xmlString = (XmlString)this.get_default_attribute_value(CTSlideLayoutImpl.MATCHINGNAME$16);
            }
            return xmlString;
        }
    }
    
    public boolean isSetMatchingName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSlideLayoutImpl.MATCHINGNAME$16) != null;
        }
    }
    
    public void setMatchingName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideLayoutImpl.MATCHINGNAME$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSlideLayoutImpl.MATCHINGNAME$16);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetMatchingName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTSlideLayoutImpl.MATCHINGNAME$16);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTSlideLayoutImpl.MATCHINGNAME$16);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetMatchingName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSlideLayoutImpl.MATCHINGNAME$16);
        }
    }
    
    public STSlideLayoutType.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideLayoutImpl.TYPE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSlideLayoutImpl.TYPE$18);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STSlideLayoutType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STSlideLayoutType xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSlideLayoutType stSlideLayoutType = (STSlideLayoutType)this.get_store().find_attribute_user(CTSlideLayoutImpl.TYPE$18);
            if (stSlideLayoutType == null) {
                stSlideLayoutType = (STSlideLayoutType)this.get_default_attribute_value(CTSlideLayoutImpl.TYPE$18);
            }
            return stSlideLayoutType;
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSlideLayoutImpl.TYPE$18) != null;
        }
    }
    
    public void setType(final STSlideLayoutType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideLayoutImpl.TYPE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSlideLayoutImpl.TYPE$18);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STSlideLayoutType stSlideLayoutType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSlideLayoutType stSlideLayoutType2 = (STSlideLayoutType)this.get_store().find_attribute_user(CTSlideLayoutImpl.TYPE$18);
            if (stSlideLayoutType2 == null) {
                stSlideLayoutType2 = (STSlideLayoutType)this.get_store().add_attribute_user(CTSlideLayoutImpl.TYPE$18);
            }
            stSlideLayoutType2.set((XmlObject)stSlideLayoutType);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSlideLayoutImpl.TYPE$18);
        }
    }
    
    public boolean getPreserve() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideLayoutImpl.PRESERVE$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSlideLayoutImpl.PRESERVE$20);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPreserve() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSlideLayoutImpl.PRESERVE$20);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSlideLayoutImpl.PRESERVE$20);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPreserve() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSlideLayoutImpl.PRESERVE$20) != null;
        }
    }
    
    public void setPreserve(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideLayoutImpl.PRESERVE$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSlideLayoutImpl.PRESERVE$20);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPreserve(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSlideLayoutImpl.PRESERVE$20);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSlideLayoutImpl.PRESERVE$20);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPreserve() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSlideLayoutImpl.PRESERVE$20);
        }
    }
    
    public boolean getUserDrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideLayoutImpl.USERDRAWN$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSlideLayoutImpl.USERDRAWN$22);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetUserDrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSlideLayoutImpl.USERDRAWN$22);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSlideLayoutImpl.USERDRAWN$22);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetUserDrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSlideLayoutImpl.USERDRAWN$22) != null;
        }
    }
    
    public void setUserDrawn(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSlideLayoutImpl.USERDRAWN$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSlideLayoutImpl.USERDRAWN$22);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetUserDrawn(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSlideLayoutImpl.USERDRAWN$22);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSlideLayoutImpl.USERDRAWN$22);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetUserDrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSlideLayoutImpl.USERDRAWN$22);
        }
    }
    
    static {
        CSLD$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cSld");
        CLRMAPOVR$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "clrMapOvr");
        TRANSITION$4 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "transition");
        TIMING$6 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "timing");
        HF$8 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "hf");
        EXTLST$10 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
        SHOWMASTERSP$12 = new QName("", "showMasterSp");
        SHOWMASTERPHANIM$14 = new QName("", "showMasterPhAnim");
        MATCHINGNAME$16 = new QName("", "matchingName");
        TYPE$18 = new QName("", "type");
        PRESERVE$20 = new QName("", "preserve");
        USERDRAWN$22 = new QName("", "userDrawn");
    }
}
