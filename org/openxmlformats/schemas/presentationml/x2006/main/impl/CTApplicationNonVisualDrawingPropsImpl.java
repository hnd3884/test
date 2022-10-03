package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCustomerDataList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTQuickTimeFile;
import org.openxmlformats.schemas.drawingml.x2006.main.CTVideoFile;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAudioFile;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEmbeddedWAVAudioFile;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAudioCD;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPlaceholder;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTApplicationNonVisualDrawingProps;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTApplicationNonVisualDrawingPropsImpl extends XmlComplexContentImpl implements CTApplicationNonVisualDrawingProps
{
    private static final long serialVersionUID = 1L;
    private static final QName PH$0;
    private static final QName AUDIOCD$2;
    private static final QName WAVAUDIOFILE$4;
    private static final QName AUDIOFILE$6;
    private static final QName VIDEOFILE$8;
    private static final QName QUICKTIMEFILE$10;
    private static final QName CUSTDATALST$12;
    private static final QName EXTLST$14;
    private static final QName ISPHOTO$16;
    private static final QName USERDRAWN$18;
    
    public CTApplicationNonVisualDrawingPropsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTPlaceholder getPh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPlaceholder ctPlaceholder = (CTPlaceholder)this.get_store().find_element_user(CTApplicationNonVisualDrawingPropsImpl.PH$0, 0);
            if (ctPlaceholder == null) {
                return null;
            }
            return ctPlaceholder;
        }
    }
    
    public boolean isSetPh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTApplicationNonVisualDrawingPropsImpl.PH$0) != 0;
        }
    }
    
    public void setPh(final CTPlaceholder ctPlaceholder) {
        this.generatedSetterHelperImpl((XmlObject)ctPlaceholder, CTApplicationNonVisualDrawingPropsImpl.PH$0, 0, (short)1);
    }
    
    public CTPlaceholder addNewPh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPlaceholder)this.get_store().add_element_user(CTApplicationNonVisualDrawingPropsImpl.PH$0);
        }
    }
    
    public void unsetPh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTApplicationNonVisualDrawingPropsImpl.PH$0, 0);
        }
    }
    
    public CTAudioCD getAudioCd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAudioCD ctAudioCD = (CTAudioCD)this.get_store().find_element_user(CTApplicationNonVisualDrawingPropsImpl.AUDIOCD$2, 0);
            if (ctAudioCD == null) {
                return null;
            }
            return ctAudioCD;
        }
    }
    
    public boolean isSetAudioCd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTApplicationNonVisualDrawingPropsImpl.AUDIOCD$2) != 0;
        }
    }
    
    public void setAudioCd(final CTAudioCD ctAudioCD) {
        this.generatedSetterHelperImpl((XmlObject)ctAudioCD, CTApplicationNonVisualDrawingPropsImpl.AUDIOCD$2, 0, (short)1);
    }
    
    public CTAudioCD addNewAudioCd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAudioCD)this.get_store().add_element_user(CTApplicationNonVisualDrawingPropsImpl.AUDIOCD$2);
        }
    }
    
    public void unsetAudioCd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTApplicationNonVisualDrawingPropsImpl.AUDIOCD$2, 0);
        }
    }
    
    public CTEmbeddedWAVAudioFile getWavAudioFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmbeddedWAVAudioFile ctEmbeddedWAVAudioFile = (CTEmbeddedWAVAudioFile)this.get_store().find_element_user(CTApplicationNonVisualDrawingPropsImpl.WAVAUDIOFILE$4, 0);
            if (ctEmbeddedWAVAudioFile == null) {
                return null;
            }
            return ctEmbeddedWAVAudioFile;
        }
    }
    
    public boolean isSetWavAudioFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTApplicationNonVisualDrawingPropsImpl.WAVAUDIOFILE$4) != 0;
        }
    }
    
    public void setWavAudioFile(final CTEmbeddedWAVAudioFile ctEmbeddedWAVAudioFile) {
        this.generatedSetterHelperImpl((XmlObject)ctEmbeddedWAVAudioFile, CTApplicationNonVisualDrawingPropsImpl.WAVAUDIOFILE$4, 0, (short)1);
    }
    
    public CTEmbeddedWAVAudioFile addNewWavAudioFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmbeddedWAVAudioFile)this.get_store().add_element_user(CTApplicationNonVisualDrawingPropsImpl.WAVAUDIOFILE$4);
        }
    }
    
    public void unsetWavAudioFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTApplicationNonVisualDrawingPropsImpl.WAVAUDIOFILE$4, 0);
        }
    }
    
    public CTAudioFile getAudioFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAudioFile ctAudioFile = (CTAudioFile)this.get_store().find_element_user(CTApplicationNonVisualDrawingPropsImpl.AUDIOFILE$6, 0);
            if (ctAudioFile == null) {
                return null;
            }
            return ctAudioFile;
        }
    }
    
    public boolean isSetAudioFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTApplicationNonVisualDrawingPropsImpl.AUDIOFILE$6) != 0;
        }
    }
    
    public void setAudioFile(final CTAudioFile ctAudioFile) {
        this.generatedSetterHelperImpl((XmlObject)ctAudioFile, CTApplicationNonVisualDrawingPropsImpl.AUDIOFILE$6, 0, (short)1);
    }
    
    public CTAudioFile addNewAudioFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAudioFile)this.get_store().add_element_user(CTApplicationNonVisualDrawingPropsImpl.AUDIOFILE$6);
        }
    }
    
    public void unsetAudioFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTApplicationNonVisualDrawingPropsImpl.AUDIOFILE$6, 0);
        }
    }
    
    public CTVideoFile getVideoFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTVideoFile ctVideoFile = (CTVideoFile)this.get_store().find_element_user(CTApplicationNonVisualDrawingPropsImpl.VIDEOFILE$8, 0);
            if (ctVideoFile == null) {
                return null;
            }
            return ctVideoFile;
        }
    }
    
    public boolean isSetVideoFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTApplicationNonVisualDrawingPropsImpl.VIDEOFILE$8) != 0;
        }
    }
    
    public void setVideoFile(final CTVideoFile ctVideoFile) {
        this.generatedSetterHelperImpl((XmlObject)ctVideoFile, CTApplicationNonVisualDrawingPropsImpl.VIDEOFILE$8, 0, (short)1);
    }
    
    public CTVideoFile addNewVideoFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVideoFile)this.get_store().add_element_user(CTApplicationNonVisualDrawingPropsImpl.VIDEOFILE$8);
        }
    }
    
    public void unsetVideoFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTApplicationNonVisualDrawingPropsImpl.VIDEOFILE$8, 0);
        }
    }
    
    public CTQuickTimeFile getQuickTimeFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTQuickTimeFile ctQuickTimeFile = (CTQuickTimeFile)this.get_store().find_element_user(CTApplicationNonVisualDrawingPropsImpl.QUICKTIMEFILE$10, 0);
            if (ctQuickTimeFile == null) {
                return null;
            }
            return ctQuickTimeFile;
        }
    }
    
    public boolean isSetQuickTimeFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTApplicationNonVisualDrawingPropsImpl.QUICKTIMEFILE$10) != 0;
        }
    }
    
    public void setQuickTimeFile(final CTQuickTimeFile ctQuickTimeFile) {
        this.generatedSetterHelperImpl((XmlObject)ctQuickTimeFile, CTApplicationNonVisualDrawingPropsImpl.QUICKTIMEFILE$10, 0, (short)1);
    }
    
    public CTQuickTimeFile addNewQuickTimeFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTQuickTimeFile)this.get_store().add_element_user(CTApplicationNonVisualDrawingPropsImpl.QUICKTIMEFILE$10);
        }
    }
    
    public void unsetQuickTimeFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTApplicationNonVisualDrawingPropsImpl.QUICKTIMEFILE$10, 0);
        }
    }
    
    public CTCustomerDataList getCustDataLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomerDataList list = (CTCustomerDataList)this.get_store().find_element_user(CTApplicationNonVisualDrawingPropsImpl.CUSTDATALST$12, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetCustDataLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTApplicationNonVisualDrawingPropsImpl.CUSTDATALST$12) != 0;
        }
    }
    
    public void setCustDataLst(final CTCustomerDataList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTApplicationNonVisualDrawingPropsImpl.CUSTDATALST$12, 0, (short)1);
    }
    
    public CTCustomerDataList addNewCustDataLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomerDataList)this.get_store().add_element_user(CTApplicationNonVisualDrawingPropsImpl.CUSTDATALST$12);
        }
    }
    
    public void unsetCustDataLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTApplicationNonVisualDrawingPropsImpl.CUSTDATALST$12, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTApplicationNonVisualDrawingPropsImpl.EXTLST$14, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTApplicationNonVisualDrawingPropsImpl.EXTLST$14) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTApplicationNonVisualDrawingPropsImpl.EXTLST$14, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTApplicationNonVisualDrawingPropsImpl.EXTLST$14);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTApplicationNonVisualDrawingPropsImpl.EXTLST$14, 0);
        }
    }
    
    public boolean getIsPhoto() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTApplicationNonVisualDrawingPropsImpl.ISPHOTO$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTApplicationNonVisualDrawingPropsImpl.ISPHOTO$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetIsPhoto() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTApplicationNonVisualDrawingPropsImpl.ISPHOTO$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTApplicationNonVisualDrawingPropsImpl.ISPHOTO$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetIsPhoto() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTApplicationNonVisualDrawingPropsImpl.ISPHOTO$16) != null;
        }
    }
    
    public void setIsPhoto(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTApplicationNonVisualDrawingPropsImpl.ISPHOTO$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTApplicationNonVisualDrawingPropsImpl.ISPHOTO$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetIsPhoto(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTApplicationNonVisualDrawingPropsImpl.ISPHOTO$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTApplicationNonVisualDrawingPropsImpl.ISPHOTO$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetIsPhoto() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTApplicationNonVisualDrawingPropsImpl.ISPHOTO$16);
        }
    }
    
    public boolean getUserDrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTApplicationNonVisualDrawingPropsImpl.USERDRAWN$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTApplicationNonVisualDrawingPropsImpl.USERDRAWN$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetUserDrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTApplicationNonVisualDrawingPropsImpl.USERDRAWN$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTApplicationNonVisualDrawingPropsImpl.USERDRAWN$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetUserDrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTApplicationNonVisualDrawingPropsImpl.USERDRAWN$18) != null;
        }
    }
    
    public void setUserDrawn(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTApplicationNonVisualDrawingPropsImpl.USERDRAWN$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTApplicationNonVisualDrawingPropsImpl.USERDRAWN$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetUserDrawn(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTApplicationNonVisualDrawingPropsImpl.USERDRAWN$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTApplicationNonVisualDrawingPropsImpl.USERDRAWN$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetUserDrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTApplicationNonVisualDrawingPropsImpl.USERDRAWN$18);
        }
    }
    
    static {
        PH$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "ph");
        AUDIOCD$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "audioCd");
        WAVAUDIOFILE$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "wavAudioFile");
        AUDIOFILE$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "audioFile");
        VIDEOFILE$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "videoFile");
        QUICKTIMEFILE$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "quickTimeFile");
        CUSTDATALST$12 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "custDataLst");
        EXTLST$14 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
        ISPHOTO$16 = new QName("", "isPhoto");
        USERDRAWN$18 = new QName("", "userDrawn");
    }
}
