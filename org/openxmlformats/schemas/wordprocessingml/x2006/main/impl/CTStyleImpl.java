package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STString;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblStylePr;
import java.util.List;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPrBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLongHexNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTStyleImpl extends XmlComplexContentImpl implements CTStyle
{
    private static final long serialVersionUID = 1L;
    private static final QName NAME$0;
    private static final QName ALIASES$2;
    private static final QName BASEDON$4;
    private static final QName NEXT$6;
    private static final QName LINK$8;
    private static final QName AUTOREDEFINE$10;
    private static final QName HIDDEN$12;
    private static final QName UIPRIORITY$14;
    private static final QName SEMIHIDDEN$16;
    private static final QName UNHIDEWHENUSED$18;
    private static final QName QFORMAT$20;
    private static final QName LOCKED$22;
    private static final QName PERSONAL$24;
    private static final QName PERSONALCOMPOSE$26;
    private static final QName PERSONALREPLY$28;
    private static final QName RSID$30;
    private static final QName PPR$32;
    private static final QName RPR$34;
    private static final QName TBLPR$36;
    private static final QName TRPR$38;
    private static final QName TCPR$40;
    private static final QName TBLSTYLEPR$42;
    private static final QName TYPE$44;
    private static final QName STYLEID$46;
    private static final QName DEFAULT$48;
    private static final QName CUSTOMSTYLE$50;
    
    public CTStyleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTString getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTStyleImpl.NAME$0, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.NAME$0) != 0;
        }
    }
    
    public void setName(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTStyleImpl.NAME$0, 0, (short)1);
    }
    
    public CTString addNewName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTStyleImpl.NAME$0);
        }
    }
    
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.NAME$0, 0);
        }
    }
    
    public CTString getAliases() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTStyleImpl.ALIASES$2, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetAliases() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.ALIASES$2) != 0;
        }
    }
    
    public void setAliases(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTStyleImpl.ALIASES$2, 0, (short)1);
    }
    
    public CTString addNewAliases() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTStyleImpl.ALIASES$2);
        }
    }
    
    public void unsetAliases() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.ALIASES$2, 0);
        }
    }
    
    public CTString getBasedOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTStyleImpl.BASEDON$4, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetBasedOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.BASEDON$4) != 0;
        }
    }
    
    public void setBasedOn(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTStyleImpl.BASEDON$4, 0, (short)1);
    }
    
    public CTString addNewBasedOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTStyleImpl.BASEDON$4);
        }
    }
    
    public void unsetBasedOn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.BASEDON$4, 0);
        }
    }
    
    public CTString getNext() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTStyleImpl.NEXT$6, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetNext() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.NEXT$6) != 0;
        }
    }
    
    public void setNext(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTStyleImpl.NEXT$6, 0, (short)1);
    }
    
    public CTString addNewNext() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTStyleImpl.NEXT$6);
        }
    }
    
    public void unsetNext() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.NEXT$6, 0);
        }
    }
    
    public CTString getLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTStyleImpl.LINK$8, 0);
            if (ctString == null) {
                return null;
            }
            return ctString;
        }
    }
    
    public boolean isSetLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.LINK$8) != 0;
        }
    }
    
    public void setLink(final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTStyleImpl.LINK$8, 0, (short)1);
    }
    
    public CTString addNewLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTStyleImpl.LINK$8);
        }
    }
    
    public void unsetLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.LINK$8, 0);
        }
    }
    
    public CTOnOff getAutoRedefine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTStyleImpl.AUTOREDEFINE$10, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetAutoRedefine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.AUTOREDEFINE$10) != 0;
        }
    }
    
    public void setAutoRedefine(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTStyleImpl.AUTOREDEFINE$10, 0, (short)1);
    }
    
    public CTOnOff addNewAutoRedefine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTStyleImpl.AUTOREDEFINE$10);
        }
    }
    
    public void unsetAutoRedefine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.AUTOREDEFINE$10, 0);
        }
    }
    
    public CTOnOff getHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTStyleImpl.HIDDEN$12, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.HIDDEN$12) != 0;
        }
    }
    
    public void setHidden(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTStyleImpl.HIDDEN$12, 0, (short)1);
    }
    
    public CTOnOff addNewHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTStyleImpl.HIDDEN$12);
        }
    }
    
    public void unsetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.HIDDEN$12, 0);
        }
    }
    
    public CTDecimalNumber getUiPriority() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTStyleImpl.UIPRIORITY$14, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetUiPriority() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.UIPRIORITY$14) != 0;
        }
    }
    
    public void setUiPriority(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTStyleImpl.UIPRIORITY$14, 0, (short)1);
    }
    
    public CTDecimalNumber addNewUiPriority() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTStyleImpl.UIPRIORITY$14);
        }
    }
    
    public void unsetUiPriority() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.UIPRIORITY$14, 0);
        }
    }
    
    public CTOnOff getSemiHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTStyleImpl.SEMIHIDDEN$16, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetSemiHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.SEMIHIDDEN$16) != 0;
        }
    }
    
    public void setSemiHidden(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTStyleImpl.SEMIHIDDEN$16, 0, (short)1);
    }
    
    public CTOnOff addNewSemiHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTStyleImpl.SEMIHIDDEN$16);
        }
    }
    
    public void unsetSemiHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.SEMIHIDDEN$16, 0);
        }
    }
    
    public CTOnOff getUnhideWhenUsed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTStyleImpl.UNHIDEWHENUSED$18, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetUnhideWhenUsed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.UNHIDEWHENUSED$18) != 0;
        }
    }
    
    public void setUnhideWhenUsed(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTStyleImpl.UNHIDEWHENUSED$18, 0, (short)1);
    }
    
    public CTOnOff addNewUnhideWhenUsed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTStyleImpl.UNHIDEWHENUSED$18);
        }
    }
    
    public void unsetUnhideWhenUsed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.UNHIDEWHENUSED$18, 0);
        }
    }
    
    public CTOnOff getQFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTStyleImpl.QFORMAT$20, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetQFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.QFORMAT$20) != 0;
        }
    }
    
    public void setQFormat(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTStyleImpl.QFORMAT$20, 0, (short)1);
    }
    
    public CTOnOff addNewQFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTStyleImpl.QFORMAT$20);
        }
    }
    
    public void unsetQFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.QFORMAT$20, 0);
        }
    }
    
    public CTOnOff getLocked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTStyleImpl.LOCKED$22, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetLocked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.LOCKED$22) != 0;
        }
    }
    
    public void setLocked(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTStyleImpl.LOCKED$22, 0, (short)1);
    }
    
    public CTOnOff addNewLocked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTStyleImpl.LOCKED$22);
        }
    }
    
    public void unsetLocked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.LOCKED$22, 0);
        }
    }
    
    public CTOnOff getPersonal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTStyleImpl.PERSONAL$24, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetPersonal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.PERSONAL$24) != 0;
        }
    }
    
    public void setPersonal(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTStyleImpl.PERSONAL$24, 0, (short)1);
    }
    
    public CTOnOff addNewPersonal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTStyleImpl.PERSONAL$24);
        }
    }
    
    public void unsetPersonal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.PERSONAL$24, 0);
        }
    }
    
    public CTOnOff getPersonalCompose() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTStyleImpl.PERSONALCOMPOSE$26, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetPersonalCompose() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.PERSONALCOMPOSE$26) != 0;
        }
    }
    
    public void setPersonalCompose(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTStyleImpl.PERSONALCOMPOSE$26, 0, (short)1);
    }
    
    public CTOnOff addNewPersonalCompose() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTStyleImpl.PERSONALCOMPOSE$26);
        }
    }
    
    public void unsetPersonalCompose() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.PERSONALCOMPOSE$26, 0);
        }
    }
    
    public CTOnOff getPersonalReply() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTStyleImpl.PERSONALREPLY$28, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetPersonalReply() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.PERSONALREPLY$28) != 0;
        }
    }
    
    public void setPersonalReply(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTStyleImpl.PERSONALREPLY$28, 0, (short)1);
    }
    
    public CTOnOff addNewPersonalReply() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTStyleImpl.PERSONALREPLY$28);
        }
    }
    
    public void unsetPersonalReply() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.PERSONALREPLY$28, 0);
        }
    }
    
    public CTLongHexNumber getRsid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLongHexNumber ctLongHexNumber = (CTLongHexNumber)this.get_store().find_element_user(CTStyleImpl.RSID$30, 0);
            if (ctLongHexNumber == null) {
                return null;
            }
            return ctLongHexNumber;
        }
    }
    
    public boolean isSetRsid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.RSID$30) != 0;
        }
    }
    
    public void setRsid(final CTLongHexNumber ctLongHexNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctLongHexNumber, CTStyleImpl.RSID$30, 0, (short)1);
    }
    
    public CTLongHexNumber addNewRsid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLongHexNumber)this.get_store().add_element_user(CTStyleImpl.RSID$30);
        }
    }
    
    public void unsetRsid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.RSID$30, 0);
        }
    }
    
    public CTPPr getPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPPr ctpPr = (CTPPr)this.get_store().find_element_user(CTStyleImpl.PPR$32, 0);
            if (ctpPr == null) {
                return null;
            }
            return ctpPr;
        }
    }
    
    public boolean isSetPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.PPR$32) != 0;
        }
    }
    
    public void setPPr(final CTPPr ctpPr) {
        this.generatedSetterHelperImpl((XmlObject)ctpPr, CTStyleImpl.PPR$32, 0, (short)1);
    }
    
    public CTPPr addNewPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPPr)this.get_store().add_element_user(CTStyleImpl.PPR$32);
        }
    }
    
    public void unsetPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.PPR$32, 0);
        }
    }
    
    public CTRPr getRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRPr ctrPr = (CTRPr)this.get_store().find_element_user(CTStyleImpl.RPR$34, 0);
            if (ctrPr == null) {
                return null;
            }
            return ctrPr;
        }
    }
    
    public boolean isSetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.RPR$34) != 0;
        }
    }
    
    public void setRPr(final CTRPr ctrPr) {
        this.generatedSetterHelperImpl((XmlObject)ctrPr, CTStyleImpl.RPR$34, 0, (short)1);
    }
    
    public CTRPr addNewRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRPr)this.get_store().add_element_user(CTStyleImpl.RPR$34);
        }
    }
    
    public void unsetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.RPR$34, 0);
        }
    }
    
    public CTTblPrBase getTblPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblPrBase ctTblPrBase = (CTTblPrBase)this.get_store().find_element_user(CTStyleImpl.TBLPR$36, 0);
            if (ctTblPrBase == null) {
                return null;
            }
            return ctTblPrBase;
        }
    }
    
    public boolean isSetTblPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.TBLPR$36) != 0;
        }
    }
    
    public void setTblPr(final CTTblPrBase ctTblPrBase) {
        this.generatedSetterHelperImpl((XmlObject)ctTblPrBase, CTStyleImpl.TBLPR$36, 0, (short)1);
    }
    
    public CTTblPrBase addNewTblPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblPrBase)this.get_store().add_element_user(CTStyleImpl.TBLPR$36);
        }
    }
    
    public void unsetTblPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.TBLPR$36, 0);
        }
    }
    
    public CTTrPr getTrPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrPr ctTrPr = (CTTrPr)this.get_store().find_element_user(CTStyleImpl.TRPR$38, 0);
            if (ctTrPr == null) {
                return null;
            }
            return ctTrPr;
        }
    }
    
    public boolean isSetTrPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.TRPR$38) != 0;
        }
    }
    
    public void setTrPr(final CTTrPr ctTrPr) {
        this.generatedSetterHelperImpl((XmlObject)ctTrPr, CTStyleImpl.TRPR$38, 0, (short)1);
    }
    
    public CTTrPr addNewTrPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrPr)this.get_store().add_element_user(CTStyleImpl.TRPR$38);
        }
    }
    
    public void unsetTrPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.TRPR$38, 0);
        }
    }
    
    public CTTcPr getTcPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTcPr ctTcPr = (CTTcPr)this.get_store().find_element_user(CTStyleImpl.TCPR$40, 0);
            if (ctTcPr == null) {
                return null;
            }
            return ctTcPr;
        }
    }
    
    public boolean isSetTcPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.TCPR$40) != 0;
        }
    }
    
    public void setTcPr(final CTTcPr ctTcPr) {
        this.generatedSetterHelperImpl((XmlObject)ctTcPr, CTStyleImpl.TCPR$40, 0, (short)1);
    }
    
    public CTTcPr addNewTcPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTcPr)this.get_store().add_element_user(CTStyleImpl.TCPR$40);
        }
    }
    
    public void unsetTcPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.TCPR$40, 0);
        }
    }
    
    public List<CTTblStylePr> getTblStylePrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TblStylePrList extends AbstractList<CTTblStylePr>
            {
                @Override
                public CTTblStylePr get(final int n) {
                    return CTStyleImpl.this.getTblStylePrArray(n);
                }
                
                @Override
                public CTTblStylePr set(final int n, final CTTblStylePr ctTblStylePr) {
                    final CTTblStylePr tblStylePrArray = CTStyleImpl.this.getTblStylePrArray(n);
                    CTStyleImpl.this.setTblStylePrArray(n, ctTblStylePr);
                    return tblStylePrArray;
                }
                
                @Override
                public void add(final int n, final CTTblStylePr ctTblStylePr) {
                    CTStyleImpl.this.insertNewTblStylePr(n).set((XmlObject)ctTblStylePr);
                }
                
                @Override
                public CTTblStylePr remove(final int n) {
                    final CTTblStylePr tblStylePrArray = CTStyleImpl.this.getTblStylePrArray(n);
                    CTStyleImpl.this.removeTblStylePr(n);
                    return tblStylePrArray;
                }
                
                @Override
                public int size() {
                    return CTStyleImpl.this.sizeOfTblStylePrArray();
                }
            }
            return new TblStylePrList();
        }
    }
    
    @Deprecated
    public CTTblStylePr[] getTblStylePrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTStyleImpl.TBLSTYLEPR$42, (List)list);
            final CTTblStylePr[] array = new CTTblStylePr[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTblStylePr getTblStylePrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblStylePr ctTblStylePr = (CTTblStylePr)this.get_store().find_element_user(CTStyleImpl.TBLSTYLEPR$42, n);
            if (ctTblStylePr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTblStylePr;
        }
    }
    
    public int sizeOfTblStylePrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStyleImpl.TBLSTYLEPR$42);
        }
    }
    
    public void setTblStylePrArray(final CTTblStylePr[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTStyleImpl.TBLSTYLEPR$42);
    }
    
    public void setTblStylePrArray(final int n, final CTTblStylePr ctTblStylePr) {
        this.generatedSetterHelperImpl((XmlObject)ctTblStylePr, CTStyleImpl.TBLSTYLEPR$42, n, (short)2);
    }
    
    public CTTblStylePr insertNewTblStylePr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblStylePr)this.get_store().insert_element_user(CTStyleImpl.TBLSTYLEPR$42, n);
        }
    }
    
    public CTTblStylePr addNewTblStylePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblStylePr)this.get_store().add_element_user(CTStyleImpl.TBLSTYLEPR$42);
        }
    }
    
    public void removeTblStylePr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStyleImpl.TBLSTYLEPR$42, n);
        }
    }
    
    public STStyleType.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStyleImpl.TYPE$44);
            if (simpleValue == null) {
                return null;
            }
            return (STStyleType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STStyleType xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STStyleType)this.get_store().find_attribute_user(CTStyleImpl.TYPE$44);
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStyleImpl.TYPE$44) != null;
        }
    }
    
    public void setType(final STStyleType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStyleImpl.TYPE$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStyleImpl.TYPE$44);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STStyleType stStyleType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STStyleType stStyleType2 = (STStyleType)this.get_store().find_attribute_user(CTStyleImpl.TYPE$44);
            if (stStyleType2 == null) {
                stStyleType2 = (STStyleType)this.get_store().add_attribute_user(CTStyleImpl.TYPE$44);
            }
            stStyleType2.set((XmlObject)stStyleType);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStyleImpl.TYPE$44);
        }
    }
    
    public String getStyleId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStyleImpl.STYLEID$46);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetStyleId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTStyleImpl.STYLEID$46);
        }
    }
    
    public boolean isSetStyleId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStyleImpl.STYLEID$46) != null;
        }
    }
    
    public void setStyleId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStyleImpl.STYLEID$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStyleImpl.STYLEID$46);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetStyleId(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTStyleImpl.STYLEID$46);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTStyleImpl.STYLEID$46);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public void unsetStyleId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStyleImpl.STYLEID$46);
        }
    }
    
    public STOnOff.Enum getDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStyleImpl.DEFAULT$48);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTStyleImpl.DEFAULT$48);
        }
    }
    
    public boolean isSetDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStyleImpl.DEFAULT$48) != null;
        }
    }
    
    public void setDefault(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStyleImpl.DEFAULT$48);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStyleImpl.DEFAULT$48);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDefault(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTStyleImpl.DEFAULT$48);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTStyleImpl.DEFAULT$48);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStyleImpl.DEFAULT$48);
        }
    }
    
    public STOnOff.Enum getCustomStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStyleImpl.CUSTOMSTYLE$50);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetCustomStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTStyleImpl.CUSTOMSTYLE$50);
        }
    }
    
    public boolean isSetCustomStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStyleImpl.CUSTOMSTYLE$50) != null;
        }
    }
    
    public void setCustomStyle(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStyleImpl.CUSTOMSTYLE$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStyleImpl.CUSTOMSTYLE$50);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCustomStyle(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTStyleImpl.CUSTOMSTYLE$50);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTStyleImpl.CUSTOMSTYLE$50);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetCustomStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStyleImpl.CUSTOMSTYLE$50);
        }
    }
    
    static {
        NAME$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "name");
        ALIASES$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "aliases");
        BASEDON$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "basedOn");
        NEXT$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "next");
        LINK$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "link");
        AUTOREDEFINE$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "autoRedefine");
        HIDDEN$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hidden");
        UIPRIORITY$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "uiPriority");
        SEMIHIDDEN$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "semiHidden");
        UNHIDEWHENUSED$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "unhideWhenUsed");
        QFORMAT$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "qFormat");
        LOCKED$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "locked");
        PERSONAL$24 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "personal");
        PERSONALCOMPOSE$26 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "personalCompose");
        PERSONALREPLY$28 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "personalReply");
        RSID$30 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsid");
        PPR$32 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pPr");
        RPR$34 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rPr");
        TBLPR$36 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblPr");
        TRPR$38 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "trPr");
        TCPR$40 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tcPr");
        TBLSTYLEPR$42 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblStylePr");
        TYPE$44 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "type");
        STYLEID$46 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "styleId");
        DEFAULT$48 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "default");
        CUSTOMSTYLE$50 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customStyle");
    }
}
