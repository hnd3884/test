package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.openxmlformats.schemas.presentationml.x2006.main.STBookmarkIdSeed;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTModifyVerifier;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.openxmlformats.schemas.presentationml.x2006.main.CTKinsoku;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCustomerDataList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPhotoAlbum;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCustomShowList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTEmbeddedFontList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSmartTags;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideSize;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTHandoutMasterIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMasterIdList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterIdList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPresentation;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPresentationImpl extends XmlComplexContentImpl implements CTPresentation
{
    private static final long serialVersionUID = 1L;
    private static final QName SLDMASTERIDLST$0;
    private static final QName NOTESMASTERIDLST$2;
    private static final QName HANDOUTMASTERIDLST$4;
    private static final QName SLDIDLST$6;
    private static final QName SLDSZ$8;
    private static final QName NOTESSZ$10;
    private static final QName SMARTTAGS$12;
    private static final QName EMBEDDEDFONTLST$14;
    private static final QName CUSTSHOWLST$16;
    private static final QName PHOTOALBUM$18;
    private static final QName CUSTDATALST$20;
    private static final QName KINSOKU$22;
    private static final QName DEFAULTTEXTSTYLE$24;
    private static final QName MODIFYVERIFIER$26;
    private static final QName EXTLST$28;
    private static final QName SERVERZOOM$30;
    private static final QName FIRSTSLIDENUM$32;
    private static final QName SHOWSPECIALPLSONTITLESLD$34;
    private static final QName RTL$36;
    private static final QName REMOVEPERSONALINFOONSAVE$38;
    private static final QName COMPATMODE$40;
    private static final QName STRICTFIRSTANDLASTCHARS$42;
    private static final QName EMBEDTRUETYPEFONTS$44;
    private static final QName SAVESUBSETFONTS$46;
    private static final QName AUTOCOMPRESSPICTURES$48;
    private static final QName BOOKMARKIDSEED$50;
    
    public CTPresentationImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTSlideMasterIdList getSldMasterIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSlideMasterIdList list = (CTSlideMasterIdList)this.get_store().find_element_user(CTPresentationImpl.SLDMASTERIDLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetSldMasterIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPresentationImpl.SLDMASTERIDLST$0) != 0;
        }
    }
    
    public void setSldMasterIdLst(final CTSlideMasterIdList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPresentationImpl.SLDMASTERIDLST$0, 0, (short)1);
    }
    
    public CTSlideMasterIdList addNewSldMasterIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideMasterIdList)this.get_store().add_element_user(CTPresentationImpl.SLDMASTERIDLST$0);
        }
    }
    
    public void unsetSldMasterIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPresentationImpl.SLDMASTERIDLST$0, 0);
        }
    }
    
    public CTNotesMasterIdList getNotesMasterIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNotesMasterIdList list = (CTNotesMasterIdList)this.get_store().find_element_user(CTPresentationImpl.NOTESMASTERIDLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetNotesMasterIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPresentationImpl.NOTESMASTERIDLST$2) != 0;
        }
    }
    
    public void setNotesMasterIdLst(final CTNotesMasterIdList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPresentationImpl.NOTESMASTERIDLST$2, 0, (short)1);
    }
    
    public CTNotesMasterIdList addNewNotesMasterIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNotesMasterIdList)this.get_store().add_element_user(CTPresentationImpl.NOTESMASTERIDLST$2);
        }
    }
    
    public void unsetNotesMasterIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPresentationImpl.NOTESMASTERIDLST$2, 0);
        }
    }
    
    public CTHandoutMasterIdList getHandoutMasterIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHandoutMasterIdList list = (CTHandoutMasterIdList)this.get_store().find_element_user(CTPresentationImpl.HANDOUTMASTERIDLST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetHandoutMasterIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPresentationImpl.HANDOUTMASTERIDLST$4) != 0;
        }
    }
    
    public void setHandoutMasterIdLst(final CTHandoutMasterIdList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPresentationImpl.HANDOUTMASTERIDLST$4, 0, (short)1);
    }
    
    public CTHandoutMasterIdList addNewHandoutMasterIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHandoutMasterIdList)this.get_store().add_element_user(CTPresentationImpl.HANDOUTMASTERIDLST$4);
        }
    }
    
    public void unsetHandoutMasterIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPresentationImpl.HANDOUTMASTERIDLST$4, 0);
        }
    }
    
    public CTSlideIdList getSldIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSlideIdList list = (CTSlideIdList)this.get_store().find_element_user(CTPresentationImpl.SLDIDLST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetSldIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPresentationImpl.SLDIDLST$6) != 0;
        }
    }
    
    public void setSldIdLst(final CTSlideIdList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPresentationImpl.SLDIDLST$6, 0, (short)1);
    }
    
    public CTSlideIdList addNewSldIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideIdList)this.get_store().add_element_user(CTPresentationImpl.SLDIDLST$6);
        }
    }
    
    public void unsetSldIdLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPresentationImpl.SLDIDLST$6, 0);
        }
    }
    
    public CTSlideSize getSldSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSlideSize ctSlideSize = (CTSlideSize)this.get_store().find_element_user(CTPresentationImpl.SLDSZ$8, 0);
            if (ctSlideSize == null) {
                return null;
            }
            return ctSlideSize;
        }
    }
    
    public boolean isSetSldSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPresentationImpl.SLDSZ$8) != 0;
        }
    }
    
    public void setSldSz(final CTSlideSize ctSlideSize) {
        this.generatedSetterHelperImpl((XmlObject)ctSlideSize, CTPresentationImpl.SLDSZ$8, 0, (short)1);
    }
    
    public CTSlideSize addNewSldSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideSize)this.get_store().add_element_user(CTPresentationImpl.SLDSZ$8);
        }
    }
    
    public void unsetSldSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPresentationImpl.SLDSZ$8, 0);
        }
    }
    
    public CTPositiveSize2D getNotesSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositiveSize2D ctPositiveSize2D = (CTPositiveSize2D)this.get_store().find_element_user(CTPresentationImpl.NOTESSZ$10, 0);
            if (ctPositiveSize2D == null) {
                return null;
            }
            return ctPositiveSize2D;
        }
    }
    
    public void setNotesSz(final CTPositiveSize2D ctPositiveSize2D) {
        this.generatedSetterHelperImpl((XmlObject)ctPositiveSize2D, CTPresentationImpl.NOTESSZ$10, 0, (short)1);
    }
    
    public CTPositiveSize2D addNewNotesSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveSize2D)this.get_store().add_element_user(CTPresentationImpl.NOTESSZ$10);
        }
    }
    
    public CTSmartTags getSmartTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSmartTags ctSmartTags = (CTSmartTags)this.get_store().find_element_user(CTPresentationImpl.SMARTTAGS$12, 0);
            if (ctSmartTags == null) {
                return null;
            }
            return ctSmartTags;
        }
    }
    
    public boolean isSetSmartTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPresentationImpl.SMARTTAGS$12) != 0;
        }
    }
    
    public void setSmartTags(final CTSmartTags ctSmartTags) {
        this.generatedSetterHelperImpl((XmlObject)ctSmartTags, CTPresentationImpl.SMARTTAGS$12, 0, (short)1);
    }
    
    public CTSmartTags addNewSmartTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSmartTags)this.get_store().add_element_user(CTPresentationImpl.SMARTTAGS$12);
        }
    }
    
    public void unsetSmartTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPresentationImpl.SMARTTAGS$12, 0);
        }
    }
    
    public CTEmbeddedFontList getEmbeddedFontLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmbeddedFontList list = (CTEmbeddedFontList)this.get_store().find_element_user(CTPresentationImpl.EMBEDDEDFONTLST$14, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetEmbeddedFontLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPresentationImpl.EMBEDDEDFONTLST$14) != 0;
        }
    }
    
    public void setEmbeddedFontLst(final CTEmbeddedFontList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPresentationImpl.EMBEDDEDFONTLST$14, 0, (short)1);
    }
    
    public CTEmbeddedFontList addNewEmbeddedFontLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmbeddedFontList)this.get_store().add_element_user(CTPresentationImpl.EMBEDDEDFONTLST$14);
        }
    }
    
    public void unsetEmbeddedFontLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPresentationImpl.EMBEDDEDFONTLST$14, 0);
        }
    }
    
    public CTCustomShowList getCustShowLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomShowList list = (CTCustomShowList)this.get_store().find_element_user(CTPresentationImpl.CUSTSHOWLST$16, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetCustShowLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPresentationImpl.CUSTSHOWLST$16) != 0;
        }
    }
    
    public void setCustShowLst(final CTCustomShowList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPresentationImpl.CUSTSHOWLST$16, 0, (short)1);
    }
    
    public CTCustomShowList addNewCustShowLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomShowList)this.get_store().add_element_user(CTPresentationImpl.CUSTSHOWLST$16);
        }
    }
    
    public void unsetCustShowLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPresentationImpl.CUSTSHOWLST$16, 0);
        }
    }
    
    public CTPhotoAlbum getPhotoAlbum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPhotoAlbum ctPhotoAlbum = (CTPhotoAlbum)this.get_store().find_element_user(CTPresentationImpl.PHOTOALBUM$18, 0);
            if (ctPhotoAlbum == null) {
                return null;
            }
            return ctPhotoAlbum;
        }
    }
    
    public boolean isSetPhotoAlbum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPresentationImpl.PHOTOALBUM$18) != 0;
        }
    }
    
    public void setPhotoAlbum(final CTPhotoAlbum ctPhotoAlbum) {
        this.generatedSetterHelperImpl((XmlObject)ctPhotoAlbum, CTPresentationImpl.PHOTOALBUM$18, 0, (short)1);
    }
    
    public CTPhotoAlbum addNewPhotoAlbum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPhotoAlbum)this.get_store().add_element_user(CTPresentationImpl.PHOTOALBUM$18);
        }
    }
    
    public void unsetPhotoAlbum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPresentationImpl.PHOTOALBUM$18, 0);
        }
    }
    
    public CTCustomerDataList getCustDataLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomerDataList list = (CTCustomerDataList)this.get_store().find_element_user(CTPresentationImpl.CUSTDATALST$20, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetCustDataLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPresentationImpl.CUSTDATALST$20) != 0;
        }
    }
    
    public void setCustDataLst(final CTCustomerDataList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPresentationImpl.CUSTDATALST$20, 0, (short)1);
    }
    
    public CTCustomerDataList addNewCustDataLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomerDataList)this.get_store().add_element_user(CTPresentationImpl.CUSTDATALST$20);
        }
    }
    
    public void unsetCustDataLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPresentationImpl.CUSTDATALST$20, 0);
        }
    }
    
    public CTKinsoku getKinsoku() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTKinsoku ctKinsoku = (CTKinsoku)this.get_store().find_element_user(CTPresentationImpl.KINSOKU$22, 0);
            if (ctKinsoku == null) {
                return null;
            }
            return ctKinsoku;
        }
    }
    
    public boolean isSetKinsoku() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPresentationImpl.KINSOKU$22) != 0;
        }
    }
    
    public void setKinsoku(final CTKinsoku ctKinsoku) {
        this.generatedSetterHelperImpl((XmlObject)ctKinsoku, CTPresentationImpl.KINSOKU$22, 0, (short)1);
    }
    
    public CTKinsoku addNewKinsoku() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTKinsoku)this.get_store().add_element_user(CTPresentationImpl.KINSOKU$22);
        }
    }
    
    public void unsetKinsoku() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPresentationImpl.KINSOKU$22, 0);
        }
    }
    
    public CTTextListStyle getDefaultTextStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextListStyle ctTextListStyle = (CTTextListStyle)this.get_store().find_element_user(CTPresentationImpl.DEFAULTTEXTSTYLE$24, 0);
            if (ctTextListStyle == null) {
                return null;
            }
            return ctTextListStyle;
        }
    }
    
    public boolean isSetDefaultTextStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPresentationImpl.DEFAULTTEXTSTYLE$24) != 0;
        }
    }
    
    public void setDefaultTextStyle(final CTTextListStyle ctTextListStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTextListStyle, CTPresentationImpl.DEFAULTTEXTSTYLE$24, 0, (short)1);
    }
    
    public CTTextListStyle addNewDefaultTextStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextListStyle)this.get_store().add_element_user(CTPresentationImpl.DEFAULTTEXTSTYLE$24);
        }
    }
    
    public void unsetDefaultTextStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPresentationImpl.DEFAULTTEXTSTYLE$24, 0);
        }
    }
    
    public CTModifyVerifier getModifyVerifier() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTModifyVerifier ctModifyVerifier = (CTModifyVerifier)this.get_store().find_element_user(CTPresentationImpl.MODIFYVERIFIER$26, 0);
            if (ctModifyVerifier == null) {
                return null;
            }
            return ctModifyVerifier;
        }
    }
    
    public boolean isSetModifyVerifier() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPresentationImpl.MODIFYVERIFIER$26) != 0;
        }
    }
    
    public void setModifyVerifier(final CTModifyVerifier ctModifyVerifier) {
        this.generatedSetterHelperImpl((XmlObject)ctModifyVerifier, CTPresentationImpl.MODIFYVERIFIER$26, 0, (short)1);
    }
    
    public CTModifyVerifier addNewModifyVerifier() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTModifyVerifier)this.get_store().add_element_user(CTPresentationImpl.MODIFYVERIFIER$26);
        }
    }
    
    public void unsetModifyVerifier() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPresentationImpl.MODIFYVERIFIER$26, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTPresentationImpl.EXTLST$28, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPresentationImpl.EXTLST$28) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPresentationImpl.EXTLST$28, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTPresentationImpl.EXTLST$28);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPresentationImpl.EXTLST$28, 0);
        }
    }
    
    public int getServerZoom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.SERVERZOOM$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPresentationImpl.SERVERZOOM$30);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPercentage xgetServerZoom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage = (STPercentage)this.get_store().find_attribute_user(CTPresentationImpl.SERVERZOOM$30);
            if (stPercentage == null) {
                stPercentage = (STPercentage)this.get_default_attribute_value(CTPresentationImpl.SERVERZOOM$30);
            }
            return stPercentage;
        }
    }
    
    public boolean isSetServerZoom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPresentationImpl.SERVERZOOM$30) != null;
        }
    }
    
    public void setServerZoom(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.SERVERZOOM$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPresentationImpl.SERVERZOOM$30);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetServerZoom(final STPercentage stPercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage2 = (STPercentage)this.get_store().find_attribute_user(CTPresentationImpl.SERVERZOOM$30);
            if (stPercentage2 == null) {
                stPercentage2 = (STPercentage)this.get_store().add_attribute_user(CTPresentationImpl.SERVERZOOM$30);
            }
            stPercentage2.set((XmlObject)stPercentage);
        }
    }
    
    public void unsetServerZoom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPresentationImpl.SERVERZOOM$30);
        }
    }
    
    public int getFirstSlideNum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.FIRSTSLIDENUM$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPresentationImpl.FIRSTSLIDENUM$32);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetFirstSlideNum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt = (XmlInt)this.get_store().find_attribute_user(CTPresentationImpl.FIRSTSLIDENUM$32);
            if (xmlInt == null) {
                xmlInt = (XmlInt)this.get_default_attribute_value(CTPresentationImpl.FIRSTSLIDENUM$32);
            }
            return xmlInt;
        }
    }
    
    public boolean isSetFirstSlideNum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPresentationImpl.FIRSTSLIDENUM$32) != null;
        }
    }
    
    public void setFirstSlideNum(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.FIRSTSLIDENUM$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPresentationImpl.FIRSTSLIDENUM$32);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetFirstSlideNum(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(CTPresentationImpl.FIRSTSLIDENUM$32);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(CTPresentationImpl.FIRSTSLIDENUM$32);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetFirstSlideNum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPresentationImpl.FIRSTSLIDENUM$32);
        }
    }
    
    public boolean getShowSpecialPlsOnTitleSld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.SHOWSPECIALPLSONTITLESLD$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPresentationImpl.SHOWSPECIALPLSONTITLESLD$34);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowSpecialPlsOnTitleSld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPresentationImpl.SHOWSPECIALPLSONTITLESLD$34);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPresentationImpl.SHOWSPECIALPLSONTITLESLD$34);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowSpecialPlsOnTitleSld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPresentationImpl.SHOWSPECIALPLSONTITLESLD$34) != null;
        }
    }
    
    public void setShowSpecialPlsOnTitleSld(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.SHOWSPECIALPLSONTITLESLD$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPresentationImpl.SHOWSPECIALPLSONTITLESLD$34);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowSpecialPlsOnTitleSld(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPresentationImpl.SHOWSPECIALPLSONTITLESLD$34);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPresentationImpl.SHOWSPECIALPLSONTITLESLD$34);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowSpecialPlsOnTitleSld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPresentationImpl.SHOWSPECIALPLSONTITLESLD$34);
        }
    }
    
    public boolean getRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.RTL$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPresentationImpl.RTL$36);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPresentationImpl.RTL$36);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPresentationImpl.RTL$36);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPresentationImpl.RTL$36) != null;
        }
    }
    
    public void setRtl(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.RTL$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPresentationImpl.RTL$36);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetRtl(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPresentationImpl.RTL$36);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPresentationImpl.RTL$36);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetRtl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPresentationImpl.RTL$36);
        }
    }
    
    public boolean getRemovePersonalInfoOnSave() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.REMOVEPERSONALINFOONSAVE$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPresentationImpl.REMOVEPERSONALINFOONSAVE$38);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetRemovePersonalInfoOnSave() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPresentationImpl.REMOVEPERSONALINFOONSAVE$38);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPresentationImpl.REMOVEPERSONALINFOONSAVE$38);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetRemovePersonalInfoOnSave() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPresentationImpl.REMOVEPERSONALINFOONSAVE$38) != null;
        }
    }
    
    public void setRemovePersonalInfoOnSave(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.REMOVEPERSONALINFOONSAVE$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPresentationImpl.REMOVEPERSONALINFOONSAVE$38);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetRemovePersonalInfoOnSave(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPresentationImpl.REMOVEPERSONALINFOONSAVE$38);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPresentationImpl.REMOVEPERSONALINFOONSAVE$38);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetRemovePersonalInfoOnSave() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPresentationImpl.REMOVEPERSONALINFOONSAVE$38);
        }
    }
    
    public boolean getCompatMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.COMPATMODE$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPresentationImpl.COMPATMODE$40);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCompatMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPresentationImpl.COMPATMODE$40);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPresentationImpl.COMPATMODE$40);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetCompatMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPresentationImpl.COMPATMODE$40) != null;
        }
    }
    
    public void setCompatMode(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.COMPATMODE$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPresentationImpl.COMPATMODE$40);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCompatMode(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPresentationImpl.COMPATMODE$40);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPresentationImpl.COMPATMODE$40);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCompatMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPresentationImpl.COMPATMODE$40);
        }
    }
    
    public boolean getStrictFirstAndLastChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.STRICTFIRSTANDLASTCHARS$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPresentationImpl.STRICTFIRSTANDLASTCHARS$42);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetStrictFirstAndLastChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPresentationImpl.STRICTFIRSTANDLASTCHARS$42);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPresentationImpl.STRICTFIRSTANDLASTCHARS$42);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetStrictFirstAndLastChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPresentationImpl.STRICTFIRSTANDLASTCHARS$42) != null;
        }
    }
    
    public void setStrictFirstAndLastChars(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.STRICTFIRSTANDLASTCHARS$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPresentationImpl.STRICTFIRSTANDLASTCHARS$42);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetStrictFirstAndLastChars(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPresentationImpl.STRICTFIRSTANDLASTCHARS$42);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPresentationImpl.STRICTFIRSTANDLASTCHARS$42);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetStrictFirstAndLastChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPresentationImpl.STRICTFIRSTANDLASTCHARS$42);
        }
    }
    
    public boolean getEmbedTrueTypeFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.EMBEDTRUETYPEFONTS$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPresentationImpl.EMBEDTRUETYPEFONTS$44);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetEmbedTrueTypeFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPresentationImpl.EMBEDTRUETYPEFONTS$44);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPresentationImpl.EMBEDTRUETYPEFONTS$44);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetEmbedTrueTypeFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPresentationImpl.EMBEDTRUETYPEFONTS$44) != null;
        }
    }
    
    public void setEmbedTrueTypeFonts(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.EMBEDTRUETYPEFONTS$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPresentationImpl.EMBEDTRUETYPEFONTS$44);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetEmbedTrueTypeFonts(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPresentationImpl.EMBEDTRUETYPEFONTS$44);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPresentationImpl.EMBEDTRUETYPEFONTS$44);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetEmbedTrueTypeFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPresentationImpl.EMBEDTRUETYPEFONTS$44);
        }
    }
    
    public boolean getSaveSubsetFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.SAVESUBSETFONTS$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPresentationImpl.SAVESUBSETFONTS$46);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSaveSubsetFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPresentationImpl.SAVESUBSETFONTS$46);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPresentationImpl.SAVESUBSETFONTS$46);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSaveSubsetFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPresentationImpl.SAVESUBSETFONTS$46) != null;
        }
    }
    
    public void setSaveSubsetFonts(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.SAVESUBSETFONTS$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPresentationImpl.SAVESUBSETFONTS$46);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSaveSubsetFonts(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPresentationImpl.SAVESUBSETFONTS$46);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPresentationImpl.SAVESUBSETFONTS$46);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSaveSubsetFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPresentationImpl.SAVESUBSETFONTS$46);
        }
    }
    
    public boolean getAutoCompressPictures() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.AUTOCOMPRESSPICTURES$48);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPresentationImpl.AUTOCOMPRESSPICTURES$48);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAutoCompressPictures() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPresentationImpl.AUTOCOMPRESSPICTURES$48);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPresentationImpl.AUTOCOMPRESSPICTURES$48);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetAutoCompressPictures() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPresentationImpl.AUTOCOMPRESSPICTURES$48) != null;
        }
    }
    
    public void setAutoCompressPictures(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.AUTOCOMPRESSPICTURES$48);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPresentationImpl.AUTOCOMPRESSPICTURES$48);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAutoCompressPictures(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPresentationImpl.AUTOCOMPRESSPICTURES$48);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPresentationImpl.AUTOCOMPRESSPICTURES$48);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAutoCompressPictures() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPresentationImpl.AUTOCOMPRESSPICTURES$48);
        }
    }
    
    public long getBookmarkIdSeed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.BOOKMARKIDSEED$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPresentationImpl.BOOKMARKIDSEED$50);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STBookmarkIdSeed xgetBookmarkIdSeed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBookmarkIdSeed stBookmarkIdSeed = (STBookmarkIdSeed)this.get_store().find_attribute_user(CTPresentationImpl.BOOKMARKIDSEED$50);
            if (stBookmarkIdSeed == null) {
                stBookmarkIdSeed = (STBookmarkIdSeed)this.get_default_attribute_value(CTPresentationImpl.BOOKMARKIDSEED$50);
            }
            return stBookmarkIdSeed;
        }
    }
    
    public boolean isSetBookmarkIdSeed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPresentationImpl.BOOKMARKIDSEED$50) != null;
        }
    }
    
    public void setBookmarkIdSeed(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPresentationImpl.BOOKMARKIDSEED$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPresentationImpl.BOOKMARKIDSEED$50);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetBookmarkIdSeed(final STBookmarkIdSeed stBookmarkIdSeed) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBookmarkIdSeed stBookmarkIdSeed2 = (STBookmarkIdSeed)this.get_store().find_attribute_user(CTPresentationImpl.BOOKMARKIDSEED$50);
            if (stBookmarkIdSeed2 == null) {
                stBookmarkIdSeed2 = (STBookmarkIdSeed)this.get_store().add_attribute_user(CTPresentationImpl.BOOKMARKIDSEED$50);
            }
            stBookmarkIdSeed2.set((XmlObject)stBookmarkIdSeed);
        }
    }
    
    public void unsetBookmarkIdSeed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPresentationImpl.BOOKMARKIDSEED$50);
        }
    }
    
    static {
        SLDMASTERIDLST$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "sldMasterIdLst");
        NOTESMASTERIDLST$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "notesMasterIdLst");
        HANDOUTMASTERIDLST$4 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "handoutMasterIdLst");
        SLDIDLST$6 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "sldIdLst");
        SLDSZ$8 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "sldSz");
        NOTESSZ$10 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "notesSz");
        SMARTTAGS$12 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "smartTags");
        EMBEDDEDFONTLST$14 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "embeddedFontLst");
        CUSTSHOWLST$16 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "custShowLst");
        PHOTOALBUM$18 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "photoAlbum");
        CUSTDATALST$20 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "custDataLst");
        KINSOKU$22 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "kinsoku");
        DEFAULTTEXTSTYLE$24 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "defaultTextStyle");
        MODIFYVERIFIER$26 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "modifyVerifier");
        EXTLST$28 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
        SERVERZOOM$30 = new QName("", "serverZoom");
        FIRSTSLIDENUM$32 = new QName("", "firstSlideNum");
        SHOWSPECIALPLSONTITLESLD$34 = new QName("", "showSpecialPlsOnTitleSld");
        RTL$36 = new QName("", "rtl");
        REMOVEPERSONALINFOONSAVE$38 = new QName("", "removePersonalInfoOnSave");
        COMPATMODE$40 = new QName("", "compatMode");
        STRICTFIRSTANDLASTCHARS$42 = new QName("", "strictFirstAndLastChars");
        EMBEDTRUETYPEFONTS$44 = new QName("", "embedTrueTypeFonts");
        SAVESUBSETFONTS$46 = new QName("", "saveSubsetFonts");
        AUTOCOMPRESSPICTURES$48 = new QName("", "autoCompressPictures");
        BOOKMARKIDSEED$50 = new QName("", "bookmarkIdSeed");
    }
}
