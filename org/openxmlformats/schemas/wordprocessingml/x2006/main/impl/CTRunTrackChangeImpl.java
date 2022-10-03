package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.officeDocument.x2006.math.CTSSup;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTSSubSup;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTSSub;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTSPre;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTRad;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTPhant;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTNary;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTM;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTLimUpp;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTLimLow;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTGroupChr;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTFunc;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTF;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTEqArr;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTD;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTBorderBox;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTBox;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTBar;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTAcc;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkup;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMoveBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPerm;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPermStart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTProofErr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSmartTagRun;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCustomXmlRun;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRunTrackChange;

public class CTRunTrackChangeImpl extends CTTrackChangeImpl implements CTRunTrackChange
{
    private static final long serialVersionUID = 1L;
    private static final QName CUSTOMXML$0;
    private static final QName SMARTTAG$2;
    private static final QName SDT$4;
    private static final QName R$6;
    private static final QName PROOFERR$8;
    private static final QName PERMSTART$10;
    private static final QName PERMEND$12;
    private static final QName BOOKMARKSTART$14;
    private static final QName BOOKMARKEND$16;
    private static final QName MOVEFROMRANGESTART$18;
    private static final QName MOVEFROMRANGEEND$20;
    private static final QName MOVETORANGESTART$22;
    private static final QName MOVETORANGEEND$24;
    private static final QName COMMENTRANGESTART$26;
    private static final QName COMMENTRANGEEND$28;
    private static final QName CUSTOMXMLINSRANGESTART$30;
    private static final QName CUSTOMXMLINSRANGEEND$32;
    private static final QName CUSTOMXMLDELRANGESTART$34;
    private static final QName CUSTOMXMLDELRANGEEND$36;
    private static final QName CUSTOMXMLMOVEFROMRANGESTART$38;
    private static final QName CUSTOMXMLMOVEFROMRANGEEND$40;
    private static final QName CUSTOMXMLMOVETORANGESTART$42;
    private static final QName CUSTOMXMLMOVETORANGEEND$44;
    private static final QName INS$46;
    private static final QName DEL$48;
    private static final QName MOVEFROM$50;
    private static final QName MOVETO$52;
    private static final QName OMATHPARA$54;
    private static final QName OMATH$56;
    private static final QName ACC$58;
    private static final QName BAR$60;
    private static final QName BOX$62;
    private static final QName BORDERBOX$64;
    private static final QName D$66;
    private static final QName EQARR$68;
    private static final QName F$70;
    private static final QName FUNC$72;
    private static final QName GROUPCHR$74;
    private static final QName LIMLOW$76;
    private static final QName LIMUPP$78;
    private static final QName M$80;
    private static final QName NARY$82;
    private static final QName PHANT$84;
    private static final QName RAD$86;
    private static final QName SPRE$88;
    private static final QName SSUB$90;
    private static final QName SSUBSUP$92;
    private static final QName SSUP$94;
    private static final QName R2$96;
    
    public CTRunTrackChangeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public List<CTCustomXmlRun> getCustomXmlList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlList extends AbstractList<CTCustomXmlRun>
            {
                @Override
                public CTCustomXmlRun get(final int n) {
                    return CTRunTrackChangeImpl.this.getCustomXmlArray(n);
                }
                
                @Override
                public CTCustomXmlRun set(final int n, final CTCustomXmlRun ctCustomXmlRun) {
                    final CTCustomXmlRun customXmlArray = CTRunTrackChangeImpl.this.getCustomXmlArray(n);
                    CTRunTrackChangeImpl.this.setCustomXmlArray(n, ctCustomXmlRun);
                    return customXmlArray;
                }
                
                @Override
                public void add(final int n, final CTCustomXmlRun ctCustomXmlRun) {
                    CTRunTrackChangeImpl.this.insertNewCustomXml(n).set((XmlObject)ctCustomXmlRun);
                }
                
                @Override
                public CTCustomXmlRun remove(final int n) {
                    final CTCustomXmlRun customXmlArray = CTRunTrackChangeImpl.this.getCustomXmlArray(n);
                    CTRunTrackChangeImpl.this.removeCustomXml(n);
                    return customXmlArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfCustomXmlArray();
                }
            }
            return new CustomXmlList();
        }
    }
    
    @Deprecated
    @Override
    public CTCustomXmlRun[] getCustomXmlArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.CUSTOMXML$0, (List)list);
            final CTCustomXmlRun[] array = new CTCustomXmlRun[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTCustomXmlRun getCustomXmlArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomXmlRun ctCustomXmlRun = (CTCustomXmlRun)this.get_store().find_element_user(CTRunTrackChangeImpl.CUSTOMXML$0, n);
            if (ctCustomXmlRun == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCustomXmlRun;
        }
    }
    
    @Override
    public int sizeOfCustomXmlArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.CUSTOMXML$0);
        }
    }
    
    @Override
    public void setCustomXmlArray(final CTCustomXmlRun[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.CUSTOMXML$0);
    }
    
    @Override
    public void setCustomXmlArray(final int n, final CTCustomXmlRun ctCustomXmlRun) {
        this.generatedSetterHelperImpl((XmlObject)ctCustomXmlRun, CTRunTrackChangeImpl.CUSTOMXML$0, n, (short)2);
    }
    
    @Override
    public CTCustomXmlRun insertNewCustomXml(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomXmlRun)this.get_store().insert_element_user(CTRunTrackChangeImpl.CUSTOMXML$0, n);
        }
    }
    
    @Override
    public CTCustomXmlRun addNewCustomXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomXmlRun)this.get_store().add_element_user(CTRunTrackChangeImpl.CUSTOMXML$0);
        }
    }
    
    @Override
    public void removeCustomXml(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.CUSTOMXML$0, n);
        }
    }
    
    @Override
    public List<CTSmartTagRun> getSmartTagList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SmartTagList extends AbstractList<CTSmartTagRun>
            {
                @Override
                public CTSmartTagRun get(final int n) {
                    return CTRunTrackChangeImpl.this.getSmartTagArray(n);
                }
                
                @Override
                public CTSmartTagRun set(final int n, final CTSmartTagRun ctSmartTagRun) {
                    final CTSmartTagRun smartTagArray = CTRunTrackChangeImpl.this.getSmartTagArray(n);
                    CTRunTrackChangeImpl.this.setSmartTagArray(n, ctSmartTagRun);
                    return smartTagArray;
                }
                
                @Override
                public void add(final int n, final CTSmartTagRun ctSmartTagRun) {
                    CTRunTrackChangeImpl.this.insertNewSmartTag(n).set((XmlObject)ctSmartTagRun);
                }
                
                @Override
                public CTSmartTagRun remove(final int n) {
                    final CTSmartTagRun smartTagArray = CTRunTrackChangeImpl.this.getSmartTagArray(n);
                    CTRunTrackChangeImpl.this.removeSmartTag(n);
                    return smartTagArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfSmartTagArray();
                }
            }
            return new SmartTagList();
        }
    }
    
    @Deprecated
    @Override
    public CTSmartTagRun[] getSmartTagArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.SMARTTAG$2, (List)list);
            final CTSmartTagRun[] array = new CTSmartTagRun[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTSmartTagRun getSmartTagArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSmartTagRun ctSmartTagRun = (CTSmartTagRun)this.get_store().find_element_user(CTRunTrackChangeImpl.SMARTTAG$2, n);
            if (ctSmartTagRun == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSmartTagRun;
        }
    }
    
    @Override
    public int sizeOfSmartTagArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.SMARTTAG$2);
        }
    }
    
    @Override
    public void setSmartTagArray(final CTSmartTagRun[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.SMARTTAG$2);
    }
    
    @Override
    public void setSmartTagArray(final int n, final CTSmartTagRun ctSmartTagRun) {
        this.generatedSetterHelperImpl((XmlObject)ctSmartTagRun, CTRunTrackChangeImpl.SMARTTAG$2, n, (short)2);
    }
    
    @Override
    public CTSmartTagRun insertNewSmartTag(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSmartTagRun)this.get_store().insert_element_user(CTRunTrackChangeImpl.SMARTTAG$2, n);
        }
    }
    
    @Override
    public CTSmartTagRun addNewSmartTag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSmartTagRun)this.get_store().add_element_user(CTRunTrackChangeImpl.SMARTTAG$2);
        }
    }
    
    @Override
    public void removeSmartTag(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.SMARTTAG$2, n);
        }
    }
    
    @Override
    public List<CTSdtRun> getSdtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SdtList extends AbstractList<CTSdtRun>
            {
                @Override
                public CTSdtRun get(final int n) {
                    return CTRunTrackChangeImpl.this.getSdtArray(n);
                }
                
                @Override
                public CTSdtRun set(final int n, final CTSdtRun ctSdtRun) {
                    final CTSdtRun sdtArray = CTRunTrackChangeImpl.this.getSdtArray(n);
                    CTRunTrackChangeImpl.this.setSdtArray(n, ctSdtRun);
                    return sdtArray;
                }
                
                @Override
                public void add(final int n, final CTSdtRun ctSdtRun) {
                    CTRunTrackChangeImpl.this.insertNewSdt(n).set((XmlObject)ctSdtRun);
                }
                
                @Override
                public CTSdtRun remove(final int n) {
                    final CTSdtRun sdtArray = CTRunTrackChangeImpl.this.getSdtArray(n);
                    CTRunTrackChangeImpl.this.removeSdt(n);
                    return sdtArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfSdtArray();
                }
            }
            return new SdtList();
        }
    }
    
    @Deprecated
    @Override
    public CTSdtRun[] getSdtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.SDT$4, (List)list);
            final CTSdtRun[] array = new CTSdtRun[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTSdtRun getSdtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtRun ctSdtRun = (CTSdtRun)this.get_store().find_element_user(CTRunTrackChangeImpl.SDT$4, n);
            if (ctSdtRun == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSdtRun;
        }
    }
    
    @Override
    public int sizeOfSdtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.SDT$4);
        }
    }
    
    @Override
    public void setSdtArray(final CTSdtRun[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.SDT$4);
    }
    
    @Override
    public void setSdtArray(final int n, final CTSdtRun ctSdtRun) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtRun, CTRunTrackChangeImpl.SDT$4, n, (short)2);
    }
    
    @Override
    public CTSdtRun insertNewSdt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtRun)this.get_store().insert_element_user(CTRunTrackChangeImpl.SDT$4, n);
        }
    }
    
    @Override
    public CTSdtRun addNewSdt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtRun)this.get_store().add_element_user(CTRunTrackChangeImpl.SDT$4);
        }
    }
    
    @Override
    public void removeSdt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.SDT$4, n);
        }
    }
    
    @Override
    public List<CTR> getRList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RList extends AbstractList<CTR>
            {
                @Override
                public CTR get(final int n) {
                    return CTRunTrackChangeImpl.this.getRArray(n);
                }
                
                @Override
                public CTR set(final int n, final CTR ctr) {
                    final CTR rArray = CTRunTrackChangeImpl.this.getRArray(n);
                    CTRunTrackChangeImpl.this.setRArray(n, ctr);
                    return rArray;
                }
                
                @Override
                public void add(final int n, final CTR ctr) {
                    CTRunTrackChangeImpl.this.insertNewR(n).set((XmlObject)ctr);
                }
                
                @Override
                public CTR remove(final int n) {
                    final CTR rArray = CTRunTrackChangeImpl.this.getRArray(n);
                    CTRunTrackChangeImpl.this.removeR(n);
                    return rArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfRArray();
                }
            }
            return new RList();
        }
    }
    
    @Deprecated
    @Override
    public CTR[] getRArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.R$6, (List)list);
            final CTR[] array = new CTR[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTR getRArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTR ctr = (CTR)this.get_store().find_element_user(CTRunTrackChangeImpl.R$6, n);
            if (ctr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctr;
        }
    }
    
    @Override
    public int sizeOfRArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.R$6);
        }
    }
    
    @Override
    public void setRArray(final CTR[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.R$6);
    }
    
    @Override
    public void setRArray(final int n, final CTR ctr) {
        this.generatedSetterHelperImpl((XmlObject)ctr, CTRunTrackChangeImpl.R$6, n, (short)2);
    }
    
    @Override
    public CTR insertNewR(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTR)this.get_store().insert_element_user(CTRunTrackChangeImpl.R$6, n);
        }
    }
    
    @Override
    public CTR addNewR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTR)this.get_store().add_element_user(CTRunTrackChangeImpl.R$6);
        }
    }
    
    @Override
    public void removeR(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.R$6, n);
        }
    }
    
    @Override
    public List<CTProofErr> getProofErrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ProofErrList extends AbstractList<CTProofErr>
            {
                @Override
                public CTProofErr get(final int n) {
                    return CTRunTrackChangeImpl.this.getProofErrArray(n);
                }
                
                @Override
                public CTProofErr set(final int n, final CTProofErr ctProofErr) {
                    final CTProofErr proofErrArray = CTRunTrackChangeImpl.this.getProofErrArray(n);
                    CTRunTrackChangeImpl.this.setProofErrArray(n, ctProofErr);
                    return proofErrArray;
                }
                
                @Override
                public void add(final int n, final CTProofErr ctProofErr) {
                    CTRunTrackChangeImpl.this.insertNewProofErr(n).set((XmlObject)ctProofErr);
                }
                
                @Override
                public CTProofErr remove(final int n) {
                    final CTProofErr proofErrArray = CTRunTrackChangeImpl.this.getProofErrArray(n);
                    CTRunTrackChangeImpl.this.removeProofErr(n);
                    return proofErrArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfProofErrArray();
                }
            }
            return new ProofErrList();
        }
    }
    
    @Deprecated
    @Override
    public CTProofErr[] getProofErrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.PROOFERR$8, (List)list);
            final CTProofErr[] array = new CTProofErr[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTProofErr getProofErrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTProofErr ctProofErr = (CTProofErr)this.get_store().find_element_user(CTRunTrackChangeImpl.PROOFERR$8, n);
            if (ctProofErr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctProofErr;
        }
    }
    
    @Override
    public int sizeOfProofErrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.PROOFERR$8);
        }
    }
    
    @Override
    public void setProofErrArray(final CTProofErr[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.PROOFERR$8);
    }
    
    @Override
    public void setProofErrArray(final int n, final CTProofErr ctProofErr) {
        this.generatedSetterHelperImpl((XmlObject)ctProofErr, CTRunTrackChangeImpl.PROOFERR$8, n, (short)2);
    }
    
    @Override
    public CTProofErr insertNewProofErr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProofErr)this.get_store().insert_element_user(CTRunTrackChangeImpl.PROOFERR$8, n);
        }
    }
    
    @Override
    public CTProofErr addNewProofErr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProofErr)this.get_store().add_element_user(CTRunTrackChangeImpl.PROOFERR$8);
        }
    }
    
    @Override
    public void removeProofErr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.PROOFERR$8, n);
        }
    }
    
    @Override
    public List<CTPermStart> getPermStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PermStartList extends AbstractList<CTPermStart>
            {
                @Override
                public CTPermStart get(final int n) {
                    return CTRunTrackChangeImpl.this.getPermStartArray(n);
                }
                
                @Override
                public CTPermStart set(final int n, final CTPermStart ctPermStart) {
                    final CTPermStart permStartArray = CTRunTrackChangeImpl.this.getPermStartArray(n);
                    CTRunTrackChangeImpl.this.setPermStartArray(n, ctPermStart);
                    return permStartArray;
                }
                
                @Override
                public void add(final int n, final CTPermStart ctPermStart) {
                    CTRunTrackChangeImpl.this.insertNewPermStart(n).set((XmlObject)ctPermStart);
                }
                
                @Override
                public CTPermStart remove(final int n) {
                    final CTPermStart permStartArray = CTRunTrackChangeImpl.this.getPermStartArray(n);
                    CTRunTrackChangeImpl.this.removePermStart(n);
                    return permStartArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfPermStartArray();
                }
            }
            return new PermStartList();
        }
    }
    
    @Deprecated
    @Override
    public CTPermStart[] getPermStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.PERMSTART$10, (List)list);
            final CTPermStart[] array = new CTPermStart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTPermStart getPermStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPermStart ctPermStart = (CTPermStart)this.get_store().find_element_user(CTRunTrackChangeImpl.PERMSTART$10, n);
            if (ctPermStart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPermStart;
        }
    }
    
    @Override
    public int sizeOfPermStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.PERMSTART$10);
        }
    }
    
    @Override
    public void setPermStartArray(final CTPermStart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.PERMSTART$10);
    }
    
    @Override
    public void setPermStartArray(final int n, final CTPermStart ctPermStart) {
        this.generatedSetterHelperImpl((XmlObject)ctPermStart, CTRunTrackChangeImpl.PERMSTART$10, n, (short)2);
    }
    
    @Override
    public CTPermStart insertNewPermStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPermStart)this.get_store().insert_element_user(CTRunTrackChangeImpl.PERMSTART$10, n);
        }
    }
    
    @Override
    public CTPermStart addNewPermStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPermStart)this.get_store().add_element_user(CTRunTrackChangeImpl.PERMSTART$10);
        }
    }
    
    @Override
    public void removePermStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.PERMSTART$10, n);
        }
    }
    
    @Override
    public List<CTPerm> getPermEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PermEndList extends AbstractList<CTPerm>
            {
                @Override
                public CTPerm get(final int n) {
                    return CTRunTrackChangeImpl.this.getPermEndArray(n);
                }
                
                @Override
                public CTPerm set(final int n, final CTPerm ctPerm) {
                    final CTPerm permEndArray = CTRunTrackChangeImpl.this.getPermEndArray(n);
                    CTRunTrackChangeImpl.this.setPermEndArray(n, ctPerm);
                    return permEndArray;
                }
                
                @Override
                public void add(final int n, final CTPerm ctPerm) {
                    CTRunTrackChangeImpl.this.insertNewPermEnd(n).set((XmlObject)ctPerm);
                }
                
                @Override
                public CTPerm remove(final int n) {
                    final CTPerm permEndArray = CTRunTrackChangeImpl.this.getPermEndArray(n);
                    CTRunTrackChangeImpl.this.removePermEnd(n);
                    return permEndArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfPermEndArray();
                }
            }
            return new PermEndList();
        }
    }
    
    @Deprecated
    @Override
    public CTPerm[] getPermEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.PERMEND$12, (List)list);
            final CTPerm[] array = new CTPerm[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTPerm getPermEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPerm ctPerm = (CTPerm)this.get_store().find_element_user(CTRunTrackChangeImpl.PERMEND$12, n);
            if (ctPerm == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPerm;
        }
    }
    
    @Override
    public int sizeOfPermEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.PERMEND$12);
        }
    }
    
    @Override
    public void setPermEndArray(final CTPerm[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.PERMEND$12);
    }
    
    @Override
    public void setPermEndArray(final int n, final CTPerm ctPerm) {
        this.generatedSetterHelperImpl((XmlObject)ctPerm, CTRunTrackChangeImpl.PERMEND$12, n, (short)2);
    }
    
    @Override
    public CTPerm insertNewPermEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerm)this.get_store().insert_element_user(CTRunTrackChangeImpl.PERMEND$12, n);
        }
    }
    
    @Override
    public CTPerm addNewPermEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerm)this.get_store().add_element_user(CTRunTrackChangeImpl.PERMEND$12);
        }
    }
    
    @Override
    public void removePermEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.PERMEND$12, n);
        }
    }
    
    @Override
    public List<CTBookmark> getBookmarkStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BookmarkStartList extends AbstractList<CTBookmark>
            {
                @Override
                public CTBookmark get(final int n) {
                    return CTRunTrackChangeImpl.this.getBookmarkStartArray(n);
                }
                
                @Override
                public CTBookmark set(final int n, final CTBookmark ctBookmark) {
                    final CTBookmark bookmarkStartArray = CTRunTrackChangeImpl.this.getBookmarkStartArray(n);
                    CTRunTrackChangeImpl.this.setBookmarkStartArray(n, ctBookmark);
                    return bookmarkStartArray;
                }
                
                @Override
                public void add(final int n, final CTBookmark ctBookmark) {
                    CTRunTrackChangeImpl.this.insertNewBookmarkStart(n).set((XmlObject)ctBookmark);
                }
                
                @Override
                public CTBookmark remove(final int n) {
                    final CTBookmark bookmarkStartArray = CTRunTrackChangeImpl.this.getBookmarkStartArray(n);
                    CTRunTrackChangeImpl.this.removeBookmarkStart(n);
                    return bookmarkStartArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfBookmarkStartArray();
                }
            }
            return new BookmarkStartList();
        }
    }
    
    @Deprecated
    @Override
    public CTBookmark[] getBookmarkStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.BOOKMARKSTART$14, (List)list);
            final CTBookmark[] array = new CTBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTBookmark getBookmarkStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBookmark ctBookmark = (CTBookmark)this.get_store().find_element_user(CTRunTrackChangeImpl.BOOKMARKSTART$14, n);
            if (ctBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBookmark;
        }
    }
    
    @Override
    public int sizeOfBookmarkStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.BOOKMARKSTART$14);
        }
    }
    
    @Override
    public void setBookmarkStartArray(final CTBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.BOOKMARKSTART$14);
    }
    
    @Override
    public void setBookmarkStartArray(final int n, final CTBookmark ctBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctBookmark, CTRunTrackChangeImpl.BOOKMARKSTART$14, n, (short)2);
    }
    
    @Override
    public CTBookmark insertNewBookmarkStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookmark)this.get_store().insert_element_user(CTRunTrackChangeImpl.BOOKMARKSTART$14, n);
        }
    }
    
    @Override
    public CTBookmark addNewBookmarkStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookmark)this.get_store().add_element_user(CTRunTrackChangeImpl.BOOKMARKSTART$14);
        }
    }
    
    @Override
    public void removeBookmarkStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.BOOKMARKSTART$14, n);
        }
    }
    
    @Override
    public List<CTMarkupRange> getBookmarkEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BookmarkEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTRunTrackChangeImpl.this.getBookmarkEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange bookmarkEndArray = CTRunTrackChangeImpl.this.getBookmarkEndArray(n);
                    CTRunTrackChangeImpl.this.setBookmarkEndArray(n, ctMarkupRange);
                    return bookmarkEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTRunTrackChangeImpl.this.insertNewBookmarkEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange bookmarkEndArray = CTRunTrackChangeImpl.this.getBookmarkEndArray(n);
                    CTRunTrackChangeImpl.this.removeBookmarkEnd(n);
                    return bookmarkEndArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfBookmarkEndArray();
                }
            }
            return new BookmarkEndList();
        }
    }
    
    @Deprecated
    @Override
    public CTMarkupRange[] getBookmarkEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.BOOKMARKEND$16, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTMarkupRange getBookmarkEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTRunTrackChangeImpl.BOOKMARKEND$16, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    @Override
    public int sizeOfBookmarkEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.BOOKMARKEND$16);
        }
    }
    
    @Override
    public void setBookmarkEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.BOOKMARKEND$16);
    }
    
    @Override
    public void setBookmarkEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTRunTrackChangeImpl.BOOKMARKEND$16, n, (short)2);
    }
    
    @Override
    public CTMarkupRange insertNewBookmarkEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTRunTrackChangeImpl.BOOKMARKEND$16, n);
        }
    }
    
    @Override
    public CTMarkupRange addNewBookmarkEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTRunTrackChangeImpl.BOOKMARKEND$16);
        }
    }
    
    @Override
    public void removeBookmarkEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.BOOKMARKEND$16, n);
        }
    }
    
    @Override
    public List<CTMoveBookmark> getMoveFromRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromRangeStartList extends AbstractList<CTMoveBookmark>
            {
                @Override
                public CTMoveBookmark get(final int n) {
                    return CTRunTrackChangeImpl.this.getMoveFromRangeStartArray(n);
                }
                
                @Override
                public CTMoveBookmark set(final int n, final CTMoveBookmark ctMoveBookmark) {
                    final CTMoveBookmark moveFromRangeStartArray = CTRunTrackChangeImpl.this.getMoveFromRangeStartArray(n);
                    CTRunTrackChangeImpl.this.setMoveFromRangeStartArray(n, ctMoveBookmark);
                    return moveFromRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMoveBookmark ctMoveBookmark) {
                    CTRunTrackChangeImpl.this.insertNewMoveFromRangeStart(n).set((XmlObject)ctMoveBookmark);
                }
                
                @Override
                public CTMoveBookmark remove(final int n) {
                    final CTMoveBookmark moveFromRangeStartArray = CTRunTrackChangeImpl.this.getMoveFromRangeStartArray(n);
                    CTRunTrackChangeImpl.this.removeMoveFromRangeStart(n);
                    return moveFromRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfMoveFromRangeStartArray();
                }
            }
            return new MoveFromRangeStartList();
        }
    }
    
    @Deprecated
    @Override
    public CTMoveBookmark[] getMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.MOVEFROMRANGESTART$18, (List)list);
            final CTMoveBookmark[] array = new CTMoveBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTMoveBookmark getMoveFromRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMoveBookmark ctMoveBookmark = (CTMoveBookmark)this.get_store().find_element_user(CTRunTrackChangeImpl.MOVEFROMRANGESTART$18, n);
            if (ctMoveBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMoveBookmark;
        }
    }
    
    @Override
    public int sizeOfMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.MOVEFROMRANGESTART$18);
        }
    }
    
    @Override
    public void setMoveFromRangeStartArray(final CTMoveBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.MOVEFROMRANGESTART$18);
    }
    
    @Override
    public void setMoveFromRangeStartArray(final int n, final CTMoveBookmark ctMoveBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctMoveBookmark, CTRunTrackChangeImpl.MOVEFROMRANGESTART$18, n, (short)2);
    }
    
    @Override
    public CTMoveBookmark insertNewMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().insert_element_user(CTRunTrackChangeImpl.MOVEFROMRANGESTART$18, n);
        }
    }
    
    @Override
    public CTMoveBookmark addNewMoveFromRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().add_element_user(CTRunTrackChangeImpl.MOVEFROMRANGESTART$18);
        }
    }
    
    @Override
    public void removeMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.MOVEFROMRANGESTART$18, n);
        }
    }
    
    @Override
    public List<CTMarkupRange> getMoveFromRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTRunTrackChangeImpl.this.getMoveFromRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange moveFromRangeEndArray = CTRunTrackChangeImpl.this.getMoveFromRangeEndArray(n);
                    CTRunTrackChangeImpl.this.setMoveFromRangeEndArray(n, ctMarkupRange);
                    return moveFromRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTRunTrackChangeImpl.this.insertNewMoveFromRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange moveFromRangeEndArray = CTRunTrackChangeImpl.this.getMoveFromRangeEndArray(n);
                    CTRunTrackChangeImpl.this.removeMoveFromRangeEnd(n);
                    return moveFromRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfMoveFromRangeEndArray();
                }
            }
            return new MoveFromRangeEndList();
        }
    }
    
    @Deprecated
    @Override
    public CTMarkupRange[] getMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.MOVEFROMRANGEEND$20, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTMarkupRange getMoveFromRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTRunTrackChangeImpl.MOVEFROMRANGEEND$20, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    @Override
    public int sizeOfMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.MOVEFROMRANGEEND$20);
        }
    }
    
    @Override
    public void setMoveFromRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.MOVEFROMRANGEEND$20);
    }
    
    @Override
    public void setMoveFromRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTRunTrackChangeImpl.MOVEFROMRANGEEND$20, n, (short)2);
    }
    
    @Override
    public CTMarkupRange insertNewMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTRunTrackChangeImpl.MOVEFROMRANGEEND$20, n);
        }
    }
    
    @Override
    public CTMarkupRange addNewMoveFromRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTRunTrackChangeImpl.MOVEFROMRANGEEND$20);
        }
    }
    
    @Override
    public void removeMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.MOVEFROMRANGEEND$20, n);
        }
    }
    
    @Override
    public List<CTMoveBookmark> getMoveToRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToRangeStartList extends AbstractList<CTMoveBookmark>
            {
                @Override
                public CTMoveBookmark get(final int n) {
                    return CTRunTrackChangeImpl.this.getMoveToRangeStartArray(n);
                }
                
                @Override
                public CTMoveBookmark set(final int n, final CTMoveBookmark ctMoveBookmark) {
                    final CTMoveBookmark moveToRangeStartArray = CTRunTrackChangeImpl.this.getMoveToRangeStartArray(n);
                    CTRunTrackChangeImpl.this.setMoveToRangeStartArray(n, ctMoveBookmark);
                    return moveToRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMoveBookmark ctMoveBookmark) {
                    CTRunTrackChangeImpl.this.insertNewMoveToRangeStart(n).set((XmlObject)ctMoveBookmark);
                }
                
                @Override
                public CTMoveBookmark remove(final int n) {
                    final CTMoveBookmark moveToRangeStartArray = CTRunTrackChangeImpl.this.getMoveToRangeStartArray(n);
                    CTRunTrackChangeImpl.this.removeMoveToRangeStart(n);
                    return moveToRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfMoveToRangeStartArray();
                }
            }
            return new MoveToRangeStartList();
        }
    }
    
    @Deprecated
    @Override
    public CTMoveBookmark[] getMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.MOVETORANGESTART$22, (List)list);
            final CTMoveBookmark[] array = new CTMoveBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTMoveBookmark getMoveToRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMoveBookmark ctMoveBookmark = (CTMoveBookmark)this.get_store().find_element_user(CTRunTrackChangeImpl.MOVETORANGESTART$22, n);
            if (ctMoveBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMoveBookmark;
        }
    }
    
    @Override
    public int sizeOfMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.MOVETORANGESTART$22);
        }
    }
    
    @Override
    public void setMoveToRangeStartArray(final CTMoveBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.MOVETORANGESTART$22);
    }
    
    @Override
    public void setMoveToRangeStartArray(final int n, final CTMoveBookmark ctMoveBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctMoveBookmark, CTRunTrackChangeImpl.MOVETORANGESTART$22, n, (short)2);
    }
    
    @Override
    public CTMoveBookmark insertNewMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().insert_element_user(CTRunTrackChangeImpl.MOVETORANGESTART$22, n);
        }
    }
    
    @Override
    public CTMoveBookmark addNewMoveToRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().add_element_user(CTRunTrackChangeImpl.MOVETORANGESTART$22);
        }
    }
    
    @Override
    public void removeMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.MOVETORANGESTART$22, n);
        }
    }
    
    @Override
    public List<CTMarkupRange> getMoveToRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTRunTrackChangeImpl.this.getMoveToRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange moveToRangeEndArray = CTRunTrackChangeImpl.this.getMoveToRangeEndArray(n);
                    CTRunTrackChangeImpl.this.setMoveToRangeEndArray(n, ctMarkupRange);
                    return moveToRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTRunTrackChangeImpl.this.insertNewMoveToRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange moveToRangeEndArray = CTRunTrackChangeImpl.this.getMoveToRangeEndArray(n);
                    CTRunTrackChangeImpl.this.removeMoveToRangeEnd(n);
                    return moveToRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfMoveToRangeEndArray();
                }
            }
            return new MoveToRangeEndList();
        }
    }
    
    @Deprecated
    @Override
    public CTMarkupRange[] getMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.MOVETORANGEEND$24, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTMarkupRange getMoveToRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTRunTrackChangeImpl.MOVETORANGEEND$24, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    @Override
    public int sizeOfMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.MOVETORANGEEND$24);
        }
    }
    
    @Override
    public void setMoveToRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.MOVETORANGEEND$24);
    }
    
    @Override
    public void setMoveToRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTRunTrackChangeImpl.MOVETORANGEEND$24, n, (short)2);
    }
    
    @Override
    public CTMarkupRange insertNewMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTRunTrackChangeImpl.MOVETORANGEEND$24, n);
        }
    }
    
    @Override
    public CTMarkupRange addNewMoveToRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTRunTrackChangeImpl.MOVETORANGEEND$24);
        }
    }
    
    @Override
    public void removeMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.MOVETORANGEEND$24, n);
        }
    }
    
    @Override
    public List<CTMarkupRange> getCommentRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentRangeStartList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTRunTrackChangeImpl.this.getCommentRangeStartArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange commentRangeStartArray = CTRunTrackChangeImpl.this.getCommentRangeStartArray(n);
                    CTRunTrackChangeImpl.this.setCommentRangeStartArray(n, ctMarkupRange);
                    return commentRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTRunTrackChangeImpl.this.insertNewCommentRangeStart(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange commentRangeStartArray = CTRunTrackChangeImpl.this.getCommentRangeStartArray(n);
                    CTRunTrackChangeImpl.this.removeCommentRangeStart(n);
                    return commentRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfCommentRangeStartArray();
                }
            }
            return new CommentRangeStartList();
        }
    }
    
    @Deprecated
    @Override
    public CTMarkupRange[] getCommentRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.COMMENTRANGESTART$26, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTMarkupRange getCommentRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTRunTrackChangeImpl.COMMENTRANGESTART$26, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    @Override
    public int sizeOfCommentRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.COMMENTRANGESTART$26);
        }
    }
    
    @Override
    public void setCommentRangeStartArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.COMMENTRANGESTART$26);
    }
    
    @Override
    public void setCommentRangeStartArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTRunTrackChangeImpl.COMMENTRANGESTART$26, n, (short)2);
    }
    
    @Override
    public CTMarkupRange insertNewCommentRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTRunTrackChangeImpl.COMMENTRANGESTART$26, n);
        }
    }
    
    @Override
    public CTMarkupRange addNewCommentRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTRunTrackChangeImpl.COMMENTRANGESTART$26);
        }
    }
    
    @Override
    public void removeCommentRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.COMMENTRANGESTART$26, n);
        }
    }
    
    @Override
    public List<CTMarkupRange> getCommentRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTRunTrackChangeImpl.this.getCommentRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange commentRangeEndArray = CTRunTrackChangeImpl.this.getCommentRangeEndArray(n);
                    CTRunTrackChangeImpl.this.setCommentRangeEndArray(n, ctMarkupRange);
                    return commentRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTRunTrackChangeImpl.this.insertNewCommentRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange commentRangeEndArray = CTRunTrackChangeImpl.this.getCommentRangeEndArray(n);
                    CTRunTrackChangeImpl.this.removeCommentRangeEnd(n);
                    return commentRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfCommentRangeEndArray();
                }
            }
            return new CommentRangeEndList();
        }
    }
    
    @Deprecated
    @Override
    public CTMarkupRange[] getCommentRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.COMMENTRANGEEND$28, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTMarkupRange getCommentRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTRunTrackChangeImpl.COMMENTRANGEEND$28, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    @Override
    public int sizeOfCommentRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.COMMENTRANGEEND$28);
        }
    }
    
    @Override
    public void setCommentRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.COMMENTRANGEEND$28);
    }
    
    @Override
    public void setCommentRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTRunTrackChangeImpl.COMMENTRANGEEND$28, n, (short)2);
    }
    
    @Override
    public CTMarkupRange insertNewCommentRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTRunTrackChangeImpl.COMMENTRANGEEND$28, n);
        }
    }
    
    @Override
    public CTMarkupRange addNewCommentRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTRunTrackChangeImpl.COMMENTRANGEEND$28);
        }
    }
    
    @Override
    public void removeCommentRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.COMMENTRANGEEND$28, n);
        }
    }
    
    @Override
    public List<CTTrackChange> getCustomXmlInsRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlInsRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTRunTrackChangeImpl.this.getCustomXmlInsRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlInsRangeStartArray = CTRunTrackChangeImpl.this.getCustomXmlInsRangeStartArray(n);
                    CTRunTrackChangeImpl.this.setCustomXmlInsRangeStartArray(n, ctTrackChange);
                    return customXmlInsRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTRunTrackChangeImpl.this.insertNewCustomXmlInsRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlInsRangeStartArray = CTRunTrackChangeImpl.this.getCustomXmlInsRangeStartArray(n);
                    CTRunTrackChangeImpl.this.removeCustomXmlInsRangeStart(n);
                    return customXmlInsRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfCustomXmlInsRangeStartArray();
                }
            }
            return new CustomXmlInsRangeStartList();
        }
    }
    
    @Deprecated
    @Override
    public CTTrackChange[] getCustomXmlInsRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.CUSTOMXMLINSRANGESTART$30, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTTrackChange getCustomXmlInsRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTRunTrackChangeImpl.CUSTOMXMLINSRANGESTART$30, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    @Override
    public int sizeOfCustomXmlInsRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.CUSTOMXMLINSRANGESTART$30);
        }
    }
    
    @Override
    public void setCustomXmlInsRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.CUSTOMXMLINSRANGESTART$30);
    }
    
    @Override
    public void setCustomXmlInsRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTRunTrackChangeImpl.CUSTOMXMLINSRANGESTART$30, n, (short)2);
    }
    
    @Override
    public CTTrackChange insertNewCustomXmlInsRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTRunTrackChangeImpl.CUSTOMXMLINSRANGESTART$30, n);
        }
    }
    
    @Override
    public CTTrackChange addNewCustomXmlInsRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTRunTrackChangeImpl.CUSTOMXMLINSRANGESTART$30);
        }
    }
    
    @Override
    public void removeCustomXmlInsRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.CUSTOMXMLINSRANGESTART$30, n);
        }
    }
    
    @Override
    public List<CTMarkup> getCustomXmlInsRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlInsRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTRunTrackChangeImpl.this.getCustomXmlInsRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlInsRangeEndArray = CTRunTrackChangeImpl.this.getCustomXmlInsRangeEndArray(n);
                    CTRunTrackChangeImpl.this.setCustomXmlInsRangeEndArray(n, ctMarkup);
                    return customXmlInsRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTRunTrackChangeImpl.this.insertNewCustomXmlInsRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlInsRangeEndArray = CTRunTrackChangeImpl.this.getCustomXmlInsRangeEndArray(n);
                    CTRunTrackChangeImpl.this.removeCustomXmlInsRangeEnd(n);
                    return customXmlInsRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfCustomXmlInsRangeEndArray();
                }
            }
            return new CustomXmlInsRangeEndList();
        }
    }
    
    @Deprecated
    @Override
    public CTMarkup[] getCustomXmlInsRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.CUSTOMXMLINSRANGEEND$32, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTMarkup getCustomXmlInsRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTRunTrackChangeImpl.CUSTOMXMLINSRANGEEND$32, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    @Override
    public int sizeOfCustomXmlInsRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.CUSTOMXMLINSRANGEEND$32);
        }
    }
    
    @Override
    public void setCustomXmlInsRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.CUSTOMXMLINSRANGEEND$32);
    }
    
    @Override
    public void setCustomXmlInsRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTRunTrackChangeImpl.CUSTOMXMLINSRANGEEND$32, n, (short)2);
    }
    
    @Override
    public CTMarkup insertNewCustomXmlInsRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTRunTrackChangeImpl.CUSTOMXMLINSRANGEEND$32, n);
        }
    }
    
    @Override
    public CTMarkup addNewCustomXmlInsRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTRunTrackChangeImpl.CUSTOMXMLINSRANGEEND$32);
        }
    }
    
    @Override
    public void removeCustomXmlInsRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.CUSTOMXMLINSRANGEEND$32, n);
        }
    }
    
    @Override
    public List<CTTrackChange> getCustomXmlDelRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlDelRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTRunTrackChangeImpl.this.getCustomXmlDelRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlDelRangeStartArray = CTRunTrackChangeImpl.this.getCustomXmlDelRangeStartArray(n);
                    CTRunTrackChangeImpl.this.setCustomXmlDelRangeStartArray(n, ctTrackChange);
                    return customXmlDelRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTRunTrackChangeImpl.this.insertNewCustomXmlDelRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlDelRangeStartArray = CTRunTrackChangeImpl.this.getCustomXmlDelRangeStartArray(n);
                    CTRunTrackChangeImpl.this.removeCustomXmlDelRangeStart(n);
                    return customXmlDelRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfCustomXmlDelRangeStartArray();
                }
            }
            return new CustomXmlDelRangeStartList();
        }
    }
    
    @Deprecated
    @Override
    public CTTrackChange[] getCustomXmlDelRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.CUSTOMXMLDELRANGESTART$34, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTTrackChange getCustomXmlDelRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTRunTrackChangeImpl.CUSTOMXMLDELRANGESTART$34, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    @Override
    public int sizeOfCustomXmlDelRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.CUSTOMXMLDELRANGESTART$34);
        }
    }
    
    @Override
    public void setCustomXmlDelRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.CUSTOMXMLDELRANGESTART$34);
    }
    
    @Override
    public void setCustomXmlDelRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTRunTrackChangeImpl.CUSTOMXMLDELRANGESTART$34, n, (short)2);
    }
    
    @Override
    public CTTrackChange insertNewCustomXmlDelRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTRunTrackChangeImpl.CUSTOMXMLDELRANGESTART$34, n);
        }
    }
    
    @Override
    public CTTrackChange addNewCustomXmlDelRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTRunTrackChangeImpl.CUSTOMXMLDELRANGESTART$34);
        }
    }
    
    @Override
    public void removeCustomXmlDelRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.CUSTOMXMLDELRANGESTART$34, n);
        }
    }
    
    @Override
    public List<CTMarkup> getCustomXmlDelRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlDelRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTRunTrackChangeImpl.this.getCustomXmlDelRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlDelRangeEndArray = CTRunTrackChangeImpl.this.getCustomXmlDelRangeEndArray(n);
                    CTRunTrackChangeImpl.this.setCustomXmlDelRangeEndArray(n, ctMarkup);
                    return customXmlDelRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTRunTrackChangeImpl.this.insertNewCustomXmlDelRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlDelRangeEndArray = CTRunTrackChangeImpl.this.getCustomXmlDelRangeEndArray(n);
                    CTRunTrackChangeImpl.this.removeCustomXmlDelRangeEnd(n);
                    return customXmlDelRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfCustomXmlDelRangeEndArray();
                }
            }
            return new CustomXmlDelRangeEndList();
        }
    }
    
    @Deprecated
    @Override
    public CTMarkup[] getCustomXmlDelRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.CUSTOMXMLDELRANGEEND$36, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTMarkup getCustomXmlDelRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTRunTrackChangeImpl.CUSTOMXMLDELRANGEEND$36, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    @Override
    public int sizeOfCustomXmlDelRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.CUSTOMXMLDELRANGEEND$36);
        }
    }
    
    @Override
    public void setCustomXmlDelRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.CUSTOMXMLDELRANGEEND$36);
    }
    
    @Override
    public void setCustomXmlDelRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTRunTrackChangeImpl.CUSTOMXMLDELRANGEEND$36, n, (short)2);
    }
    
    @Override
    public CTMarkup insertNewCustomXmlDelRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTRunTrackChangeImpl.CUSTOMXMLDELRANGEEND$36, n);
        }
    }
    
    @Override
    public CTMarkup addNewCustomXmlDelRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTRunTrackChangeImpl.CUSTOMXMLDELRANGEEND$36);
        }
    }
    
    @Override
    public void removeCustomXmlDelRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.CUSTOMXMLDELRANGEEND$36, n);
        }
    }
    
    @Override
    public List<CTTrackChange> getCustomXmlMoveFromRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveFromRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTRunTrackChangeImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlMoveFromRangeStartArray = CTRunTrackChangeImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                    CTRunTrackChangeImpl.this.setCustomXmlMoveFromRangeStartArray(n, ctTrackChange);
                    return customXmlMoveFromRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTRunTrackChangeImpl.this.insertNewCustomXmlMoveFromRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlMoveFromRangeStartArray = CTRunTrackChangeImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                    CTRunTrackChangeImpl.this.removeCustomXmlMoveFromRangeStart(n);
                    return customXmlMoveFromRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfCustomXmlMoveFromRangeStartArray();
                }
            }
            return new CustomXmlMoveFromRangeStartList();
        }
    }
    
    @Deprecated
    @Override
    public CTTrackChange[] getCustomXmlMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.CUSTOMXMLMOVEFROMRANGESTART$38, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTTrackChange getCustomXmlMoveFromRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTRunTrackChangeImpl.CUSTOMXMLMOVEFROMRANGESTART$38, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    @Override
    public int sizeOfCustomXmlMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.CUSTOMXMLMOVEFROMRANGESTART$38);
        }
    }
    
    @Override
    public void setCustomXmlMoveFromRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.CUSTOMXMLMOVEFROMRANGESTART$38);
    }
    
    @Override
    public void setCustomXmlMoveFromRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTRunTrackChangeImpl.CUSTOMXMLMOVEFROMRANGESTART$38, n, (short)2);
    }
    
    @Override
    public CTTrackChange insertNewCustomXmlMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTRunTrackChangeImpl.CUSTOMXMLMOVEFROMRANGESTART$38, n);
        }
    }
    
    @Override
    public CTTrackChange addNewCustomXmlMoveFromRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTRunTrackChangeImpl.CUSTOMXMLMOVEFROMRANGESTART$38);
        }
    }
    
    @Override
    public void removeCustomXmlMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.CUSTOMXMLMOVEFROMRANGESTART$38, n);
        }
    }
    
    @Override
    public List<CTMarkup> getCustomXmlMoveFromRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveFromRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTRunTrackChangeImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlMoveFromRangeEndArray = CTRunTrackChangeImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                    CTRunTrackChangeImpl.this.setCustomXmlMoveFromRangeEndArray(n, ctMarkup);
                    return customXmlMoveFromRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTRunTrackChangeImpl.this.insertNewCustomXmlMoveFromRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlMoveFromRangeEndArray = CTRunTrackChangeImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                    CTRunTrackChangeImpl.this.removeCustomXmlMoveFromRangeEnd(n);
                    return customXmlMoveFromRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfCustomXmlMoveFromRangeEndArray();
                }
            }
            return new CustomXmlMoveFromRangeEndList();
        }
    }
    
    @Deprecated
    @Override
    public CTMarkup[] getCustomXmlMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.CUSTOMXMLMOVEFROMRANGEEND$40, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTMarkup getCustomXmlMoveFromRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTRunTrackChangeImpl.CUSTOMXMLMOVEFROMRANGEEND$40, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    @Override
    public int sizeOfCustomXmlMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.CUSTOMXMLMOVEFROMRANGEEND$40);
        }
    }
    
    @Override
    public void setCustomXmlMoveFromRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.CUSTOMXMLMOVEFROMRANGEEND$40);
    }
    
    @Override
    public void setCustomXmlMoveFromRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTRunTrackChangeImpl.CUSTOMXMLMOVEFROMRANGEEND$40, n, (short)2);
    }
    
    @Override
    public CTMarkup insertNewCustomXmlMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTRunTrackChangeImpl.CUSTOMXMLMOVEFROMRANGEEND$40, n);
        }
    }
    
    @Override
    public CTMarkup addNewCustomXmlMoveFromRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTRunTrackChangeImpl.CUSTOMXMLMOVEFROMRANGEEND$40);
        }
    }
    
    @Override
    public void removeCustomXmlMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.CUSTOMXMLMOVEFROMRANGEEND$40, n);
        }
    }
    
    @Override
    public List<CTTrackChange> getCustomXmlMoveToRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveToRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTRunTrackChangeImpl.this.getCustomXmlMoveToRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlMoveToRangeStartArray = CTRunTrackChangeImpl.this.getCustomXmlMoveToRangeStartArray(n);
                    CTRunTrackChangeImpl.this.setCustomXmlMoveToRangeStartArray(n, ctTrackChange);
                    return customXmlMoveToRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTRunTrackChangeImpl.this.insertNewCustomXmlMoveToRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlMoveToRangeStartArray = CTRunTrackChangeImpl.this.getCustomXmlMoveToRangeStartArray(n);
                    CTRunTrackChangeImpl.this.removeCustomXmlMoveToRangeStart(n);
                    return customXmlMoveToRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfCustomXmlMoveToRangeStartArray();
                }
            }
            return new CustomXmlMoveToRangeStartList();
        }
    }
    
    @Deprecated
    @Override
    public CTTrackChange[] getCustomXmlMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.CUSTOMXMLMOVETORANGESTART$42, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTTrackChange getCustomXmlMoveToRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTRunTrackChangeImpl.CUSTOMXMLMOVETORANGESTART$42, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    @Override
    public int sizeOfCustomXmlMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.CUSTOMXMLMOVETORANGESTART$42);
        }
    }
    
    @Override
    public void setCustomXmlMoveToRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.CUSTOMXMLMOVETORANGESTART$42);
    }
    
    @Override
    public void setCustomXmlMoveToRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTRunTrackChangeImpl.CUSTOMXMLMOVETORANGESTART$42, n, (short)2);
    }
    
    @Override
    public CTTrackChange insertNewCustomXmlMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTRunTrackChangeImpl.CUSTOMXMLMOVETORANGESTART$42, n);
        }
    }
    
    @Override
    public CTTrackChange addNewCustomXmlMoveToRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTRunTrackChangeImpl.CUSTOMXMLMOVETORANGESTART$42);
        }
    }
    
    @Override
    public void removeCustomXmlMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.CUSTOMXMLMOVETORANGESTART$42, n);
        }
    }
    
    @Override
    public List<CTMarkup> getCustomXmlMoveToRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveToRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTRunTrackChangeImpl.this.getCustomXmlMoveToRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlMoveToRangeEndArray = CTRunTrackChangeImpl.this.getCustomXmlMoveToRangeEndArray(n);
                    CTRunTrackChangeImpl.this.setCustomXmlMoveToRangeEndArray(n, ctMarkup);
                    return customXmlMoveToRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTRunTrackChangeImpl.this.insertNewCustomXmlMoveToRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlMoveToRangeEndArray = CTRunTrackChangeImpl.this.getCustomXmlMoveToRangeEndArray(n);
                    CTRunTrackChangeImpl.this.removeCustomXmlMoveToRangeEnd(n);
                    return customXmlMoveToRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfCustomXmlMoveToRangeEndArray();
                }
            }
            return new CustomXmlMoveToRangeEndList();
        }
    }
    
    @Deprecated
    @Override
    public CTMarkup[] getCustomXmlMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.CUSTOMXMLMOVETORANGEEND$44, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTMarkup getCustomXmlMoveToRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTRunTrackChangeImpl.CUSTOMXMLMOVETORANGEEND$44, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    @Override
    public int sizeOfCustomXmlMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.CUSTOMXMLMOVETORANGEEND$44);
        }
    }
    
    @Override
    public void setCustomXmlMoveToRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.CUSTOMXMLMOVETORANGEEND$44);
    }
    
    @Override
    public void setCustomXmlMoveToRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTRunTrackChangeImpl.CUSTOMXMLMOVETORANGEEND$44, n, (short)2);
    }
    
    @Override
    public CTMarkup insertNewCustomXmlMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTRunTrackChangeImpl.CUSTOMXMLMOVETORANGEEND$44, n);
        }
    }
    
    @Override
    public CTMarkup addNewCustomXmlMoveToRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTRunTrackChangeImpl.CUSTOMXMLMOVETORANGEEND$44);
        }
    }
    
    @Override
    public void removeCustomXmlMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.CUSTOMXMLMOVETORANGEEND$44, n);
        }
    }
    
    @Override
    public List<CTRunTrackChange> getInsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class InsList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTRunTrackChangeImpl.this.getInsArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange insArray = CTRunTrackChangeImpl.this.getInsArray(n);
                    CTRunTrackChangeImpl.this.setInsArray(n, ctRunTrackChange);
                    return insArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTRunTrackChangeImpl.this.insertNewIns(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange insArray = CTRunTrackChangeImpl.this.getInsArray(n);
                    CTRunTrackChangeImpl.this.removeIns(n);
                    return insArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfInsArray();
                }
            }
            return new InsList();
        }
    }
    
    @Deprecated
    @Override
    public CTRunTrackChange[] getInsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.INS$46, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTRunTrackChange getInsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTRunTrackChangeImpl.INS$46, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    @Override
    public int sizeOfInsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.INS$46);
        }
    }
    
    @Override
    public void setInsArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.INS$46);
    }
    
    @Override
    public void setInsArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTRunTrackChangeImpl.INS$46, n, (short)2);
    }
    
    @Override
    public CTRunTrackChange insertNewIns(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTRunTrackChangeImpl.INS$46, n);
        }
    }
    
    @Override
    public CTRunTrackChange addNewIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTRunTrackChangeImpl.INS$46);
        }
    }
    
    @Override
    public void removeIns(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.INS$46, n);
        }
    }
    
    @Override
    public List<CTRunTrackChange> getDelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DelList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTRunTrackChangeImpl.this.getDelArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange delArray = CTRunTrackChangeImpl.this.getDelArray(n);
                    CTRunTrackChangeImpl.this.setDelArray(n, ctRunTrackChange);
                    return delArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTRunTrackChangeImpl.this.insertNewDel(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange delArray = CTRunTrackChangeImpl.this.getDelArray(n);
                    CTRunTrackChangeImpl.this.removeDel(n);
                    return delArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfDelArray();
                }
            }
            return new DelList();
        }
    }
    
    @Deprecated
    @Override
    public CTRunTrackChange[] getDelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.DEL$48, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTRunTrackChange getDelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTRunTrackChangeImpl.DEL$48, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    @Override
    public int sizeOfDelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.DEL$48);
        }
    }
    
    @Override
    public void setDelArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.DEL$48);
    }
    
    @Override
    public void setDelArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTRunTrackChangeImpl.DEL$48, n, (short)2);
    }
    
    @Override
    public CTRunTrackChange insertNewDel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTRunTrackChangeImpl.DEL$48, n);
        }
    }
    
    @Override
    public CTRunTrackChange addNewDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTRunTrackChangeImpl.DEL$48);
        }
    }
    
    @Override
    public void removeDel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.DEL$48, n);
        }
    }
    
    @Override
    public List<CTRunTrackChange> getMoveFromList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTRunTrackChangeImpl.this.getMoveFromArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange moveFromArray = CTRunTrackChangeImpl.this.getMoveFromArray(n);
                    CTRunTrackChangeImpl.this.setMoveFromArray(n, ctRunTrackChange);
                    return moveFromArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTRunTrackChangeImpl.this.insertNewMoveFrom(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange moveFromArray = CTRunTrackChangeImpl.this.getMoveFromArray(n);
                    CTRunTrackChangeImpl.this.removeMoveFrom(n);
                    return moveFromArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfMoveFromArray();
                }
            }
            return new MoveFromList();
        }
    }
    
    @Deprecated
    @Override
    public CTRunTrackChange[] getMoveFromArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.MOVEFROM$50, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTRunTrackChange getMoveFromArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTRunTrackChangeImpl.MOVEFROM$50, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    @Override
    public int sizeOfMoveFromArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.MOVEFROM$50);
        }
    }
    
    @Override
    public void setMoveFromArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.MOVEFROM$50);
    }
    
    @Override
    public void setMoveFromArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTRunTrackChangeImpl.MOVEFROM$50, n, (short)2);
    }
    
    @Override
    public CTRunTrackChange insertNewMoveFrom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTRunTrackChangeImpl.MOVEFROM$50, n);
        }
    }
    
    @Override
    public CTRunTrackChange addNewMoveFrom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTRunTrackChangeImpl.MOVEFROM$50);
        }
    }
    
    @Override
    public void removeMoveFrom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.MOVEFROM$50, n);
        }
    }
    
    @Override
    public List<CTRunTrackChange> getMoveToList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTRunTrackChangeImpl.this.getMoveToArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange moveToArray = CTRunTrackChangeImpl.this.getMoveToArray(n);
                    CTRunTrackChangeImpl.this.setMoveToArray(n, ctRunTrackChange);
                    return moveToArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTRunTrackChangeImpl.this.insertNewMoveTo(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange moveToArray = CTRunTrackChangeImpl.this.getMoveToArray(n);
                    CTRunTrackChangeImpl.this.removeMoveTo(n);
                    return moveToArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfMoveToArray();
                }
            }
            return new MoveToList();
        }
    }
    
    @Deprecated
    @Override
    public CTRunTrackChange[] getMoveToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.MOVETO$52, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTRunTrackChange getMoveToArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTRunTrackChangeImpl.MOVETO$52, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    @Override
    public int sizeOfMoveToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.MOVETO$52);
        }
    }
    
    @Override
    public void setMoveToArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.MOVETO$52);
    }
    
    @Override
    public void setMoveToArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTRunTrackChangeImpl.MOVETO$52, n, (short)2);
    }
    
    @Override
    public CTRunTrackChange insertNewMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTRunTrackChangeImpl.MOVETO$52, n);
        }
    }
    
    @Override
    public CTRunTrackChange addNewMoveTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTRunTrackChangeImpl.MOVETO$52);
        }
    }
    
    @Override
    public void removeMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.MOVETO$52, n);
        }
    }
    
    @Override
    public List<CTOMathPara> getOMathParaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OMathParaList extends AbstractList<CTOMathPara>
            {
                @Override
                public CTOMathPara get(final int n) {
                    return CTRunTrackChangeImpl.this.getOMathParaArray(n);
                }
                
                @Override
                public CTOMathPara set(final int n, final CTOMathPara ctoMathPara) {
                    final CTOMathPara oMathParaArray = CTRunTrackChangeImpl.this.getOMathParaArray(n);
                    CTRunTrackChangeImpl.this.setOMathParaArray(n, ctoMathPara);
                    return oMathParaArray;
                }
                
                @Override
                public void add(final int n, final CTOMathPara ctoMathPara) {
                    CTRunTrackChangeImpl.this.insertNewOMathPara(n).set((XmlObject)ctoMathPara);
                }
                
                @Override
                public CTOMathPara remove(final int n) {
                    final CTOMathPara oMathParaArray = CTRunTrackChangeImpl.this.getOMathParaArray(n);
                    CTRunTrackChangeImpl.this.removeOMathPara(n);
                    return oMathParaArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfOMathParaArray();
                }
            }
            return new OMathParaList();
        }
    }
    
    @Deprecated
    @Override
    public CTOMathPara[] getOMathParaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.OMATHPARA$54, (List)list);
            final CTOMathPara[] array = new CTOMathPara[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTOMathPara getOMathParaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOMathPara ctoMathPara = (CTOMathPara)this.get_store().find_element_user(CTRunTrackChangeImpl.OMATHPARA$54, n);
            if (ctoMathPara == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctoMathPara;
        }
    }
    
    @Override
    public int sizeOfOMathParaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.OMATHPARA$54);
        }
    }
    
    @Override
    public void setOMathParaArray(final CTOMathPara[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.OMATHPARA$54);
    }
    
    @Override
    public void setOMathParaArray(final int n, final CTOMathPara ctoMathPara) {
        this.generatedSetterHelperImpl((XmlObject)ctoMathPara, CTRunTrackChangeImpl.OMATHPARA$54, n, (short)2);
    }
    
    @Override
    public CTOMathPara insertNewOMathPara(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMathPara)this.get_store().insert_element_user(CTRunTrackChangeImpl.OMATHPARA$54, n);
        }
    }
    
    @Override
    public CTOMathPara addNewOMathPara() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMathPara)this.get_store().add_element_user(CTRunTrackChangeImpl.OMATHPARA$54);
        }
    }
    
    @Override
    public void removeOMathPara(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.OMATHPARA$54, n);
        }
    }
    
    @Override
    public List<CTOMath> getOMathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OMathList extends AbstractList<CTOMath>
            {
                @Override
                public CTOMath get(final int n) {
                    return CTRunTrackChangeImpl.this.getOMathArray(n);
                }
                
                @Override
                public CTOMath set(final int n, final CTOMath ctoMath) {
                    final CTOMath oMathArray = CTRunTrackChangeImpl.this.getOMathArray(n);
                    CTRunTrackChangeImpl.this.setOMathArray(n, ctoMath);
                    return oMathArray;
                }
                
                @Override
                public void add(final int n, final CTOMath ctoMath) {
                    CTRunTrackChangeImpl.this.insertNewOMath(n).set((XmlObject)ctoMath);
                }
                
                @Override
                public CTOMath remove(final int n) {
                    final CTOMath oMathArray = CTRunTrackChangeImpl.this.getOMathArray(n);
                    CTRunTrackChangeImpl.this.removeOMath(n);
                    return oMathArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfOMathArray();
                }
            }
            return new OMathList();
        }
    }
    
    @Deprecated
    @Override
    public CTOMath[] getOMathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.OMATH$56, (List)list);
            final CTOMath[] array = new CTOMath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTOMath getOMathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOMath ctoMath = (CTOMath)this.get_store().find_element_user(CTRunTrackChangeImpl.OMATH$56, n);
            if (ctoMath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctoMath;
        }
    }
    
    @Override
    public int sizeOfOMathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.OMATH$56);
        }
    }
    
    @Override
    public void setOMathArray(final CTOMath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.OMATH$56);
    }
    
    @Override
    public void setOMathArray(final int n, final CTOMath ctoMath) {
        this.generatedSetterHelperImpl((XmlObject)ctoMath, CTRunTrackChangeImpl.OMATH$56, n, (short)2);
    }
    
    @Override
    public CTOMath insertNewOMath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMath)this.get_store().insert_element_user(CTRunTrackChangeImpl.OMATH$56, n);
        }
    }
    
    @Override
    public CTOMath addNewOMath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMath)this.get_store().add_element_user(CTRunTrackChangeImpl.OMATH$56);
        }
    }
    
    @Override
    public void removeOMath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.OMATH$56, n);
        }
    }
    
    @Override
    public List<CTAcc> getAccList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AccList extends AbstractList<CTAcc>
            {
                @Override
                public CTAcc get(final int n) {
                    return CTRunTrackChangeImpl.this.getAccArray(n);
                }
                
                @Override
                public CTAcc set(final int n, final CTAcc ctAcc) {
                    final CTAcc accArray = CTRunTrackChangeImpl.this.getAccArray(n);
                    CTRunTrackChangeImpl.this.setAccArray(n, ctAcc);
                    return accArray;
                }
                
                @Override
                public void add(final int n, final CTAcc ctAcc) {
                    CTRunTrackChangeImpl.this.insertNewAcc(n).set((XmlObject)ctAcc);
                }
                
                @Override
                public CTAcc remove(final int n) {
                    final CTAcc accArray = CTRunTrackChangeImpl.this.getAccArray(n);
                    CTRunTrackChangeImpl.this.removeAcc(n);
                    return accArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfAccArray();
                }
            }
            return new AccList();
        }
    }
    
    @Deprecated
    @Override
    public CTAcc[] getAccArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.ACC$58, (List)list);
            final CTAcc[] array = new CTAcc[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTAcc getAccArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAcc ctAcc = (CTAcc)this.get_store().find_element_user(CTRunTrackChangeImpl.ACC$58, n);
            if (ctAcc == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAcc;
        }
    }
    
    @Override
    public int sizeOfAccArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.ACC$58);
        }
    }
    
    @Override
    public void setAccArray(final CTAcc[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.ACC$58);
    }
    
    @Override
    public void setAccArray(final int n, final CTAcc ctAcc) {
        this.generatedSetterHelperImpl((XmlObject)ctAcc, CTRunTrackChangeImpl.ACC$58, n, (short)2);
    }
    
    @Override
    public CTAcc insertNewAcc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAcc)this.get_store().insert_element_user(CTRunTrackChangeImpl.ACC$58, n);
        }
    }
    
    @Override
    public CTAcc addNewAcc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAcc)this.get_store().add_element_user(CTRunTrackChangeImpl.ACC$58);
        }
    }
    
    @Override
    public void removeAcc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.ACC$58, n);
        }
    }
    
    @Override
    public List<CTBar> getBarList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BarList extends AbstractList<CTBar>
            {
                @Override
                public CTBar get(final int n) {
                    return CTRunTrackChangeImpl.this.getBarArray(n);
                }
                
                @Override
                public CTBar set(final int n, final CTBar ctBar) {
                    final CTBar barArray = CTRunTrackChangeImpl.this.getBarArray(n);
                    CTRunTrackChangeImpl.this.setBarArray(n, ctBar);
                    return barArray;
                }
                
                @Override
                public void add(final int n, final CTBar ctBar) {
                    CTRunTrackChangeImpl.this.insertNewBar(n).set((XmlObject)ctBar);
                }
                
                @Override
                public CTBar remove(final int n) {
                    final CTBar barArray = CTRunTrackChangeImpl.this.getBarArray(n);
                    CTRunTrackChangeImpl.this.removeBar(n);
                    return barArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfBarArray();
                }
            }
            return new BarList();
        }
    }
    
    @Deprecated
    @Override
    public CTBar[] getBarArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.BAR$60, (List)list);
            final CTBar[] array = new CTBar[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTBar getBarArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBar ctBar = (CTBar)this.get_store().find_element_user(CTRunTrackChangeImpl.BAR$60, n);
            if (ctBar == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBar;
        }
    }
    
    @Override
    public int sizeOfBarArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.BAR$60);
        }
    }
    
    @Override
    public void setBarArray(final CTBar[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.BAR$60);
    }
    
    @Override
    public void setBarArray(final int n, final CTBar ctBar) {
        this.generatedSetterHelperImpl((XmlObject)ctBar, CTRunTrackChangeImpl.BAR$60, n, (short)2);
    }
    
    @Override
    public CTBar insertNewBar(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBar)this.get_store().insert_element_user(CTRunTrackChangeImpl.BAR$60, n);
        }
    }
    
    @Override
    public CTBar addNewBar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBar)this.get_store().add_element_user(CTRunTrackChangeImpl.BAR$60);
        }
    }
    
    @Override
    public void removeBar(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.BAR$60, n);
        }
    }
    
    @Override
    public List<CTBox> getBoxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BoxList extends AbstractList<CTBox>
            {
                @Override
                public CTBox get(final int n) {
                    return CTRunTrackChangeImpl.this.getBoxArray(n);
                }
                
                @Override
                public CTBox set(final int n, final CTBox ctBox) {
                    final CTBox boxArray = CTRunTrackChangeImpl.this.getBoxArray(n);
                    CTRunTrackChangeImpl.this.setBoxArray(n, ctBox);
                    return boxArray;
                }
                
                @Override
                public void add(final int n, final CTBox ctBox) {
                    CTRunTrackChangeImpl.this.insertNewBox(n).set((XmlObject)ctBox);
                }
                
                @Override
                public CTBox remove(final int n) {
                    final CTBox boxArray = CTRunTrackChangeImpl.this.getBoxArray(n);
                    CTRunTrackChangeImpl.this.removeBox(n);
                    return boxArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfBoxArray();
                }
            }
            return new BoxList();
        }
    }
    
    @Deprecated
    @Override
    public CTBox[] getBoxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.BOX$62, (List)list);
            final CTBox[] array = new CTBox[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTBox getBoxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBox ctBox = (CTBox)this.get_store().find_element_user(CTRunTrackChangeImpl.BOX$62, n);
            if (ctBox == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBox;
        }
    }
    
    @Override
    public int sizeOfBoxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.BOX$62);
        }
    }
    
    @Override
    public void setBoxArray(final CTBox[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.BOX$62);
    }
    
    @Override
    public void setBoxArray(final int n, final CTBox ctBox) {
        this.generatedSetterHelperImpl((XmlObject)ctBox, CTRunTrackChangeImpl.BOX$62, n, (short)2);
    }
    
    @Override
    public CTBox insertNewBox(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBox)this.get_store().insert_element_user(CTRunTrackChangeImpl.BOX$62, n);
        }
    }
    
    @Override
    public CTBox addNewBox() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBox)this.get_store().add_element_user(CTRunTrackChangeImpl.BOX$62);
        }
    }
    
    @Override
    public void removeBox(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.BOX$62, n);
        }
    }
    
    @Override
    public List<CTBorderBox> getBorderBoxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BorderBoxList extends AbstractList<CTBorderBox>
            {
                @Override
                public CTBorderBox get(final int n) {
                    return CTRunTrackChangeImpl.this.getBorderBoxArray(n);
                }
                
                @Override
                public CTBorderBox set(final int n, final CTBorderBox ctBorderBox) {
                    final CTBorderBox borderBoxArray = CTRunTrackChangeImpl.this.getBorderBoxArray(n);
                    CTRunTrackChangeImpl.this.setBorderBoxArray(n, ctBorderBox);
                    return borderBoxArray;
                }
                
                @Override
                public void add(final int n, final CTBorderBox ctBorderBox) {
                    CTRunTrackChangeImpl.this.insertNewBorderBox(n).set((XmlObject)ctBorderBox);
                }
                
                @Override
                public CTBorderBox remove(final int n) {
                    final CTBorderBox borderBoxArray = CTRunTrackChangeImpl.this.getBorderBoxArray(n);
                    CTRunTrackChangeImpl.this.removeBorderBox(n);
                    return borderBoxArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfBorderBoxArray();
                }
            }
            return new BorderBoxList();
        }
    }
    
    @Deprecated
    @Override
    public CTBorderBox[] getBorderBoxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.BORDERBOX$64, (List)list);
            final CTBorderBox[] array = new CTBorderBox[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTBorderBox getBorderBoxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorderBox ctBorderBox = (CTBorderBox)this.get_store().find_element_user(CTRunTrackChangeImpl.BORDERBOX$64, n);
            if (ctBorderBox == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBorderBox;
        }
    }
    
    @Override
    public int sizeOfBorderBoxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.BORDERBOX$64);
        }
    }
    
    @Override
    public void setBorderBoxArray(final CTBorderBox[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.BORDERBOX$64);
    }
    
    @Override
    public void setBorderBoxArray(final int n, final CTBorderBox ctBorderBox) {
        this.generatedSetterHelperImpl((XmlObject)ctBorderBox, CTRunTrackChangeImpl.BORDERBOX$64, n, (short)2);
    }
    
    @Override
    public CTBorderBox insertNewBorderBox(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorderBox)this.get_store().insert_element_user(CTRunTrackChangeImpl.BORDERBOX$64, n);
        }
    }
    
    @Override
    public CTBorderBox addNewBorderBox() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorderBox)this.get_store().add_element_user(CTRunTrackChangeImpl.BORDERBOX$64);
        }
    }
    
    @Override
    public void removeBorderBox(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.BORDERBOX$64, n);
        }
    }
    
    @Override
    public List<CTD> getDList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DList extends AbstractList<CTD>
            {
                @Override
                public CTD get(final int n) {
                    return CTRunTrackChangeImpl.this.getDArray(n);
                }
                
                @Override
                public CTD set(final int n, final CTD ctd) {
                    final CTD dArray = CTRunTrackChangeImpl.this.getDArray(n);
                    CTRunTrackChangeImpl.this.setDArray(n, ctd);
                    return dArray;
                }
                
                @Override
                public void add(final int n, final CTD ctd) {
                    CTRunTrackChangeImpl.this.insertNewD(n).set((XmlObject)ctd);
                }
                
                @Override
                public CTD remove(final int n) {
                    final CTD dArray = CTRunTrackChangeImpl.this.getDArray(n);
                    CTRunTrackChangeImpl.this.removeD(n);
                    return dArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfDArray();
                }
            }
            return new DList();
        }
    }
    
    @Deprecated
    @Override
    public CTD[] getDArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.D$66, (List)list);
            final CTD[] array = new CTD[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTD getDArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTD ctd = (CTD)this.get_store().find_element_user(CTRunTrackChangeImpl.D$66, n);
            if (ctd == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctd;
        }
    }
    
    @Override
    public int sizeOfDArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.D$66);
        }
    }
    
    @Override
    public void setDArray(final CTD[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.D$66);
    }
    
    @Override
    public void setDArray(final int n, final CTD ctd) {
        this.generatedSetterHelperImpl((XmlObject)ctd, CTRunTrackChangeImpl.D$66, n, (short)2);
    }
    
    @Override
    public CTD insertNewD(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTD)this.get_store().insert_element_user(CTRunTrackChangeImpl.D$66, n);
        }
    }
    
    @Override
    public CTD addNewD() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTD)this.get_store().add_element_user(CTRunTrackChangeImpl.D$66);
        }
    }
    
    @Override
    public void removeD(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.D$66, n);
        }
    }
    
    @Override
    public List<CTEqArr> getEqArrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class EqArrList extends AbstractList<CTEqArr>
            {
                @Override
                public CTEqArr get(final int n) {
                    return CTRunTrackChangeImpl.this.getEqArrArray(n);
                }
                
                @Override
                public CTEqArr set(final int n, final CTEqArr ctEqArr) {
                    final CTEqArr eqArrArray = CTRunTrackChangeImpl.this.getEqArrArray(n);
                    CTRunTrackChangeImpl.this.setEqArrArray(n, ctEqArr);
                    return eqArrArray;
                }
                
                @Override
                public void add(final int n, final CTEqArr ctEqArr) {
                    CTRunTrackChangeImpl.this.insertNewEqArr(n).set((XmlObject)ctEqArr);
                }
                
                @Override
                public CTEqArr remove(final int n) {
                    final CTEqArr eqArrArray = CTRunTrackChangeImpl.this.getEqArrArray(n);
                    CTRunTrackChangeImpl.this.removeEqArr(n);
                    return eqArrArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfEqArrArray();
                }
            }
            return new EqArrList();
        }
    }
    
    @Deprecated
    @Override
    public CTEqArr[] getEqArrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.EQARR$68, (List)list);
            final CTEqArr[] array = new CTEqArr[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTEqArr getEqArrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEqArr ctEqArr = (CTEqArr)this.get_store().find_element_user(CTRunTrackChangeImpl.EQARR$68, n);
            if (ctEqArr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEqArr;
        }
    }
    
    @Override
    public int sizeOfEqArrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.EQARR$68);
        }
    }
    
    @Override
    public void setEqArrArray(final CTEqArr[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.EQARR$68);
    }
    
    @Override
    public void setEqArrArray(final int n, final CTEqArr ctEqArr) {
        this.generatedSetterHelperImpl((XmlObject)ctEqArr, CTRunTrackChangeImpl.EQARR$68, n, (short)2);
    }
    
    @Override
    public CTEqArr insertNewEqArr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEqArr)this.get_store().insert_element_user(CTRunTrackChangeImpl.EQARR$68, n);
        }
    }
    
    @Override
    public CTEqArr addNewEqArr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEqArr)this.get_store().add_element_user(CTRunTrackChangeImpl.EQARR$68);
        }
    }
    
    @Override
    public void removeEqArr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.EQARR$68, n);
        }
    }
    
    @Override
    public List<CTF> getFList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FList extends AbstractList<CTF>
            {
                @Override
                public CTF get(final int n) {
                    return CTRunTrackChangeImpl.this.getFArray(n);
                }
                
                @Override
                public CTF set(final int n, final CTF ctf) {
                    final CTF fArray = CTRunTrackChangeImpl.this.getFArray(n);
                    CTRunTrackChangeImpl.this.setFArray(n, ctf);
                    return fArray;
                }
                
                @Override
                public void add(final int n, final CTF ctf) {
                    CTRunTrackChangeImpl.this.insertNewF(n).set((XmlObject)ctf);
                }
                
                @Override
                public CTF remove(final int n) {
                    final CTF fArray = CTRunTrackChangeImpl.this.getFArray(n);
                    CTRunTrackChangeImpl.this.removeF(n);
                    return fArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfFArray();
                }
            }
            return new FList();
        }
    }
    
    @Deprecated
    @Override
    public CTF[] getFArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.F$70, (List)list);
            final CTF[] array = new CTF[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTF getFArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTF ctf = (CTF)this.get_store().find_element_user(CTRunTrackChangeImpl.F$70, n);
            if (ctf == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctf;
        }
    }
    
    @Override
    public int sizeOfFArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.F$70);
        }
    }
    
    @Override
    public void setFArray(final CTF[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.F$70);
    }
    
    @Override
    public void setFArray(final int n, final CTF ctf) {
        this.generatedSetterHelperImpl((XmlObject)ctf, CTRunTrackChangeImpl.F$70, n, (short)2);
    }
    
    @Override
    public CTF insertNewF(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTF)this.get_store().insert_element_user(CTRunTrackChangeImpl.F$70, n);
        }
    }
    
    @Override
    public CTF addNewF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTF)this.get_store().add_element_user(CTRunTrackChangeImpl.F$70);
        }
    }
    
    @Override
    public void removeF(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.F$70, n);
        }
    }
    
    @Override
    public List<CTFunc> getFuncList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FuncList extends AbstractList<CTFunc>
            {
                @Override
                public CTFunc get(final int n) {
                    return CTRunTrackChangeImpl.this.getFuncArray(n);
                }
                
                @Override
                public CTFunc set(final int n, final CTFunc ctFunc) {
                    final CTFunc funcArray = CTRunTrackChangeImpl.this.getFuncArray(n);
                    CTRunTrackChangeImpl.this.setFuncArray(n, ctFunc);
                    return funcArray;
                }
                
                @Override
                public void add(final int n, final CTFunc ctFunc) {
                    CTRunTrackChangeImpl.this.insertNewFunc(n).set((XmlObject)ctFunc);
                }
                
                @Override
                public CTFunc remove(final int n) {
                    final CTFunc funcArray = CTRunTrackChangeImpl.this.getFuncArray(n);
                    CTRunTrackChangeImpl.this.removeFunc(n);
                    return funcArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfFuncArray();
                }
            }
            return new FuncList();
        }
    }
    
    @Deprecated
    @Override
    public CTFunc[] getFuncArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.FUNC$72, (List)list);
            final CTFunc[] array = new CTFunc[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTFunc getFuncArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFunc ctFunc = (CTFunc)this.get_store().find_element_user(CTRunTrackChangeImpl.FUNC$72, n);
            if (ctFunc == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFunc;
        }
    }
    
    @Override
    public int sizeOfFuncArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.FUNC$72);
        }
    }
    
    @Override
    public void setFuncArray(final CTFunc[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.FUNC$72);
    }
    
    @Override
    public void setFuncArray(final int n, final CTFunc ctFunc) {
        this.generatedSetterHelperImpl((XmlObject)ctFunc, CTRunTrackChangeImpl.FUNC$72, n, (short)2);
    }
    
    @Override
    public CTFunc insertNewFunc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFunc)this.get_store().insert_element_user(CTRunTrackChangeImpl.FUNC$72, n);
        }
    }
    
    @Override
    public CTFunc addNewFunc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFunc)this.get_store().add_element_user(CTRunTrackChangeImpl.FUNC$72);
        }
    }
    
    @Override
    public void removeFunc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.FUNC$72, n);
        }
    }
    
    @Override
    public List<CTGroupChr> getGroupChrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GroupChrList extends AbstractList<CTGroupChr>
            {
                @Override
                public CTGroupChr get(final int n) {
                    return CTRunTrackChangeImpl.this.getGroupChrArray(n);
                }
                
                @Override
                public CTGroupChr set(final int n, final CTGroupChr ctGroupChr) {
                    final CTGroupChr groupChrArray = CTRunTrackChangeImpl.this.getGroupChrArray(n);
                    CTRunTrackChangeImpl.this.setGroupChrArray(n, ctGroupChr);
                    return groupChrArray;
                }
                
                @Override
                public void add(final int n, final CTGroupChr ctGroupChr) {
                    CTRunTrackChangeImpl.this.insertNewGroupChr(n).set((XmlObject)ctGroupChr);
                }
                
                @Override
                public CTGroupChr remove(final int n) {
                    final CTGroupChr groupChrArray = CTRunTrackChangeImpl.this.getGroupChrArray(n);
                    CTRunTrackChangeImpl.this.removeGroupChr(n);
                    return groupChrArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfGroupChrArray();
                }
            }
            return new GroupChrList();
        }
    }
    
    @Deprecated
    @Override
    public CTGroupChr[] getGroupChrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.GROUPCHR$74, (List)list);
            final CTGroupChr[] array = new CTGroupChr[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTGroupChr getGroupChrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupChr ctGroupChr = (CTGroupChr)this.get_store().find_element_user(CTRunTrackChangeImpl.GROUPCHR$74, n);
            if (ctGroupChr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctGroupChr;
        }
    }
    
    @Override
    public int sizeOfGroupChrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.GROUPCHR$74);
        }
    }
    
    @Override
    public void setGroupChrArray(final CTGroupChr[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.GROUPCHR$74);
    }
    
    @Override
    public void setGroupChrArray(final int n, final CTGroupChr ctGroupChr) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupChr, CTRunTrackChangeImpl.GROUPCHR$74, n, (short)2);
    }
    
    @Override
    public CTGroupChr insertNewGroupChr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupChr)this.get_store().insert_element_user(CTRunTrackChangeImpl.GROUPCHR$74, n);
        }
    }
    
    @Override
    public CTGroupChr addNewGroupChr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupChr)this.get_store().add_element_user(CTRunTrackChangeImpl.GROUPCHR$74);
        }
    }
    
    @Override
    public void removeGroupChr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.GROUPCHR$74, n);
        }
    }
    
    @Override
    public List<CTLimLow> getLimLowList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LimLowList extends AbstractList<CTLimLow>
            {
                @Override
                public CTLimLow get(final int n) {
                    return CTRunTrackChangeImpl.this.getLimLowArray(n);
                }
                
                @Override
                public CTLimLow set(final int n, final CTLimLow ctLimLow) {
                    final CTLimLow limLowArray = CTRunTrackChangeImpl.this.getLimLowArray(n);
                    CTRunTrackChangeImpl.this.setLimLowArray(n, ctLimLow);
                    return limLowArray;
                }
                
                @Override
                public void add(final int n, final CTLimLow ctLimLow) {
                    CTRunTrackChangeImpl.this.insertNewLimLow(n).set((XmlObject)ctLimLow);
                }
                
                @Override
                public CTLimLow remove(final int n) {
                    final CTLimLow limLowArray = CTRunTrackChangeImpl.this.getLimLowArray(n);
                    CTRunTrackChangeImpl.this.removeLimLow(n);
                    return limLowArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfLimLowArray();
                }
            }
            return new LimLowList();
        }
    }
    
    @Deprecated
    @Override
    public CTLimLow[] getLimLowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.LIMLOW$76, (List)list);
            final CTLimLow[] array = new CTLimLow[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTLimLow getLimLowArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLimLow ctLimLow = (CTLimLow)this.get_store().find_element_user(CTRunTrackChangeImpl.LIMLOW$76, n);
            if (ctLimLow == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctLimLow;
        }
    }
    
    @Override
    public int sizeOfLimLowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.LIMLOW$76);
        }
    }
    
    @Override
    public void setLimLowArray(final CTLimLow[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.LIMLOW$76);
    }
    
    @Override
    public void setLimLowArray(final int n, final CTLimLow ctLimLow) {
        this.generatedSetterHelperImpl((XmlObject)ctLimLow, CTRunTrackChangeImpl.LIMLOW$76, n, (short)2);
    }
    
    @Override
    public CTLimLow insertNewLimLow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLimLow)this.get_store().insert_element_user(CTRunTrackChangeImpl.LIMLOW$76, n);
        }
    }
    
    @Override
    public CTLimLow addNewLimLow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLimLow)this.get_store().add_element_user(CTRunTrackChangeImpl.LIMLOW$76);
        }
    }
    
    @Override
    public void removeLimLow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.LIMLOW$76, n);
        }
    }
    
    @Override
    public List<CTLimUpp> getLimUppList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LimUppList extends AbstractList<CTLimUpp>
            {
                @Override
                public CTLimUpp get(final int n) {
                    return CTRunTrackChangeImpl.this.getLimUppArray(n);
                }
                
                @Override
                public CTLimUpp set(final int n, final CTLimUpp ctLimUpp) {
                    final CTLimUpp limUppArray = CTRunTrackChangeImpl.this.getLimUppArray(n);
                    CTRunTrackChangeImpl.this.setLimUppArray(n, ctLimUpp);
                    return limUppArray;
                }
                
                @Override
                public void add(final int n, final CTLimUpp ctLimUpp) {
                    CTRunTrackChangeImpl.this.insertNewLimUpp(n).set((XmlObject)ctLimUpp);
                }
                
                @Override
                public CTLimUpp remove(final int n) {
                    final CTLimUpp limUppArray = CTRunTrackChangeImpl.this.getLimUppArray(n);
                    CTRunTrackChangeImpl.this.removeLimUpp(n);
                    return limUppArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfLimUppArray();
                }
            }
            return new LimUppList();
        }
    }
    
    @Deprecated
    @Override
    public CTLimUpp[] getLimUppArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.LIMUPP$78, (List)list);
            final CTLimUpp[] array = new CTLimUpp[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTLimUpp getLimUppArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLimUpp ctLimUpp = (CTLimUpp)this.get_store().find_element_user(CTRunTrackChangeImpl.LIMUPP$78, n);
            if (ctLimUpp == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctLimUpp;
        }
    }
    
    @Override
    public int sizeOfLimUppArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.LIMUPP$78);
        }
    }
    
    @Override
    public void setLimUppArray(final CTLimUpp[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.LIMUPP$78);
    }
    
    @Override
    public void setLimUppArray(final int n, final CTLimUpp ctLimUpp) {
        this.generatedSetterHelperImpl((XmlObject)ctLimUpp, CTRunTrackChangeImpl.LIMUPP$78, n, (short)2);
    }
    
    @Override
    public CTLimUpp insertNewLimUpp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLimUpp)this.get_store().insert_element_user(CTRunTrackChangeImpl.LIMUPP$78, n);
        }
    }
    
    @Override
    public CTLimUpp addNewLimUpp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLimUpp)this.get_store().add_element_user(CTRunTrackChangeImpl.LIMUPP$78);
        }
    }
    
    @Override
    public void removeLimUpp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.LIMUPP$78, n);
        }
    }
    
    @Override
    public List<CTM> getMList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MList extends AbstractList<CTM>
            {
                @Override
                public CTM get(final int n) {
                    return CTRunTrackChangeImpl.this.getMArray(n);
                }
                
                @Override
                public CTM set(final int n, final CTM ctm) {
                    final CTM mArray = CTRunTrackChangeImpl.this.getMArray(n);
                    CTRunTrackChangeImpl.this.setMArray(n, ctm);
                    return mArray;
                }
                
                @Override
                public void add(final int n, final CTM ctm) {
                    CTRunTrackChangeImpl.this.insertNewM(n).set((XmlObject)ctm);
                }
                
                @Override
                public CTM remove(final int n) {
                    final CTM mArray = CTRunTrackChangeImpl.this.getMArray(n);
                    CTRunTrackChangeImpl.this.removeM(n);
                    return mArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfMArray();
                }
            }
            return new MList();
        }
    }
    
    @Deprecated
    @Override
    public CTM[] getMArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.M$80, (List)list);
            final CTM[] array = new CTM[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTM getMArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTM ctm = (CTM)this.get_store().find_element_user(CTRunTrackChangeImpl.M$80, n);
            if (ctm == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctm;
        }
    }
    
    @Override
    public int sizeOfMArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.M$80);
        }
    }
    
    @Override
    public void setMArray(final CTM[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.M$80);
    }
    
    @Override
    public void setMArray(final int n, final CTM ctm) {
        this.generatedSetterHelperImpl((XmlObject)ctm, CTRunTrackChangeImpl.M$80, n, (short)2);
    }
    
    @Override
    public CTM insertNewM(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTM)this.get_store().insert_element_user(CTRunTrackChangeImpl.M$80, n);
        }
    }
    
    @Override
    public CTM addNewM() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTM)this.get_store().add_element_user(CTRunTrackChangeImpl.M$80);
        }
    }
    
    @Override
    public void removeM(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.M$80, n);
        }
    }
    
    @Override
    public List<CTNary> getNaryList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class NaryList extends AbstractList<CTNary>
            {
                @Override
                public CTNary get(final int n) {
                    return CTRunTrackChangeImpl.this.getNaryArray(n);
                }
                
                @Override
                public CTNary set(final int n, final CTNary ctNary) {
                    final CTNary naryArray = CTRunTrackChangeImpl.this.getNaryArray(n);
                    CTRunTrackChangeImpl.this.setNaryArray(n, ctNary);
                    return naryArray;
                }
                
                @Override
                public void add(final int n, final CTNary ctNary) {
                    CTRunTrackChangeImpl.this.insertNewNary(n).set((XmlObject)ctNary);
                }
                
                @Override
                public CTNary remove(final int n) {
                    final CTNary naryArray = CTRunTrackChangeImpl.this.getNaryArray(n);
                    CTRunTrackChangeImpl.this.removeNary(n);
                    return naryArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfNaryArray();
                }
            }
            return new NaryList();
        }
    }
    
    @Deprecated
    @Override
    public CTNary[] getNaryArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.NARY$82, (List)list);
            final CTNary[] array = new CTNary[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTNary getNaryArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNary ctNary = (CTNary)this.get_store().find_element_user(CTRunTrackChangeImpl.NARY$82, n);
            if (ctNary == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctNary;
        }
    }
    
    @Override
    public int sizeOfNaryArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.NARY$82);
        }
    }
    
    @Override
    public void setNaryArray(final CTNary[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.NARY$82);
    }
    
    @Override
    public void setNaryArray(final int n, final CTNary ctNary) {
        this.generatedSetterHelperImpl((XmlObject)ctNary, CTRunTrackChangeImpl.NARY$82, n, (short)2);
    }
    
    @Override
    public CTNary insertNewNary(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNary)this.get_store().insert_element_user(CTRunTrackChangeImpl.NARY$82, n);
        }
    }
    
    @Override
    public CTNary addNewNary() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNary)this.get_store().add_element_user(CTRunTrackChangeImpl.NARY$82);
        }
    }
    
    @Override
    public void removeNary(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.NARY$82, n);
        }
    }
    
    @Override
    public List<CTPhant> getPhantList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PhantList extends AbstractList<CTPhant>
            {
                @Override
                public CTPhant get(final int n) {
                    return CTRunTrackChangeImpl.this.getPhantArray(n);
                }
                
                @Override
                public CTPhant set(final int n, final CTPhant ctPhant) {
                    final CTPhant phantArray = CTRunTrackChangeImpl.this.getPhantArray(n);
                    CTRunTrackChangeImpl.this.setPhantArray(n, ctPhant);
                    return phantArray;
                }
                
                @Override
                public void add(final int n, final CTPhant ctPhant) {
                    CTRunTrackChangeImpl.this.insertNewPhant(n).set((XmlObject)ctPhant);
                }
                
                @Override
                public CTPhant remove(final int n) {
                    final CTPhant phantArray = CTRunTrackChangeImpl.this.getPhantArray(n);
                    CTRunTrackChangeImpl.this.removePhant(n);
                    return phantArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfPhantArray();
                }
            }
            return new PhantList();
        }
    }
    
    @Deprecated
    @Override
    public CTPhant[] getPhantArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.PHANT$84, (List)list);
            final CTPhant[] array = new CTPhant[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTPhant getPhantArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPhant ctPhant = (CTPhant)this.get_store().find_element_user(CTRunTrackChangeImpl.PHANT$84, n);
            if (ctPhant == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPhant;
        }
    }
    
    @Override
    public int sizeOfPhantArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.PHANT$84);
        }
    }
    
    @Override
    public void setPhantArray(final CTPhant[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.PHANT$84);
    }
    
    @Override
    public void setPhantArray(final int n, final CTPhant ctPhant) {
        this.generatedSetterHelperImpl((XmlObject)ctPhant, CTRunTrackChangeImpl.PHANT$84, n, (short)2);
    }
    
    @Override
    public CTPhant insertNewPhant(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPhant)this.get_store().insert_element_user(CTRunTrackChangeImpl.PHANT$84, n);
        }
    }
    
    @Override
    public CTPhant addNewPhant() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPhant)this.get_store().add_element_user(CTRunTrackChangeImpl.PHANT$84);
        }
    }
    
    @Override
    public void removePhant(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.PHANT$84, n);
        }
    }
    
    @Override
    public List<CTRad> getRadList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RadList extends AbstractList<CTRad>
            {
                @Override
                public CTRad get(final int n) {
                    return CTRunTrackChangeImpl.this.getRadArray(n);
                }
                
                @Override
                public CTRad set(final int n, final CTRad ctRad) {
                    final CTRad radArray = CTRunTrackChangeImpl.this.getRadArray(n);
                    CTRunTrackChangeImpl.this.setRadArray(n, ctRad);
                    return radArray;
                }
                
                @Override
                public void add(final int n, final CTRad ctRad) {
                    CTRunTrackChangeImpl.this.insertNewRad(n).set((XmlObject)ctRad);
                }
                
                @Override
                public CTRad remove(final int n) {
                    final CTRad radArray = CTRunTrackChangeImpl.this.getRadArray(n);
                    CTRunTrackChangeImpl.this.removeRad(n);
                    return radArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfRadArray();
                }
            }
            return new RadList();
        }
    }
    
    @Deprecated
    @Override
    public CTRad[] getRadArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.RAD$86, (List)list);
            final CTRad[] array = new CTRad[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTRad getRadArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRad ctRad = (CTRad)this.get_store().find_element_user(CTRunTrackChangeImpl.RAD$86, n);
            if (ctRad == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRad;
        }
    }
    
    @Override
    public int sizeOfRadArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.RAD$86);
        }
    }
    
    @Override
    public void setRadArray(final CTRad[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.RAD$86);
    }
    
    @Override
    public void setRadArray(final int n, final CTRad ctRad) {
        this.generatedSetterHelperImpl((XmlObject)ctRad, CTRunTrackChangeImpl.RAD$86, n, (short)2);
    }
    
    @Override
    public CTRad insertNewRad(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRad)this.get_store().insert_element_user(CTRunTrackChangeImpl.RAD$86, n);
        }
    }
    
    @Override
    public CTRad addNewRad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRad)this.get_store().add_element_user(CTRunTrackChangeImpl.RAD$86);
        }
    }
    
    @Override
    public void removeRad(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.RAD$86, n);
        }
    }
    
    @Override
    public List<CTSPre> getSPreList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SPreList extends AbstractList<CTSPre>
            {
                @Override
                public CTSPre get(final int n) {
                    return CTRunTrackChangeImpl.this.getSPreArray(n);
                }
                
                @Override
                public CTSPre set(final int n, final CTSPre ctsPre) {
                    final CTSPre sPreArray = CTRunTrackChangeImpl.this.getSPreArray(n);
                    CTRunTrackChangeImpl.this.setSPreArray(n, ctsPre);
                    return sPreArray;
                }
                
                @Override
                public void add(final int n, final CTSPre ctsPre) {
                    CTRunTrackChangeImpl.this.insertNewSPre(n).set((XmlObject)ctsPre);
                }
                
                @Override
                public CTSPre remove(final int n) {
                    final CTSPre sPreArray = CTRunTrackChangeImpl.this.getSPreArray(n);
                    CTRunTrackChangeImpl.this.removeSPre(n);
                    return sPreArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfSPreArray();
                }
            }
            return new SPreList();
        }
    }
    
    @Deprecated
    @Override
    public CTSPre[] getSPreArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.SPRE$88, (List)list);
            final CTSPre[] array = new CTSPre[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTSPre getSPreArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSPre ctsPre = (CTSPre)this.get_store().find_element_user(CTRunTrackChangeImpl.SPRE$88, n);
            if (ctsPre == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctsPre;
        }
    }
    
    @Override
    public int sizeOfSPreArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.SPRE$88);
        }
    }
    
    @Override
    public void setSPreArray(final CTSPre[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.SPRE$88);
    }
    
    @Override
    public void setSPreArray(final int n, final CTSPre ctsPre) {
        this.generatedSetterHelperImpl((XmlObject)ctsPre, CTRunTrackChangeImpl.SPRE$88, n, (short)2);
    }
    
    @Override
    public CTSPre insertNewSPre(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSPre)this.get_store().insert_element_user(CTRunTrackChangeImpl.SPRE$88, n);
        }
    }
    
    @Override
    public CTSPre addNewSPre() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSPre)this.get_store().add_element_user(CTRunTrackChangeImpl.SPRE$88);
        }
    }
    
    @Override
    public void removeSPre(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.SPRE$88, n);
        }
    }
    
    @Override
    public List<CTSSub> getSSubList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SSubList extends AbstractList<CTSSub>
            {
                @Override
                public CTSSub get(final int n) {
                    return CTRunTrackChangeImpl.this.getSSubArray(n);
                }
                
                @Override
                public CTSSub set(final int n, final CTSSub ctsSub) {
                    final CTSSub sSubArray = CTRunTrackChangeImpl.this.getSSubArray(n);
                    CTRunTrackChangeImpl.this.setSSubArray(n, ctsSub);
                    return sSubArray;
                }
                
                @Override
                public void add(final int n, final CTSSub ctsSub) {
                    CTRunTrackChangeImpl.this.insertNewSSub(n).set((XmlObject)ctsSub);
                }
                
                @Override
                public CTSSub remove(final int n) {
                    final CTSSub sSubArray = CTRunTrackChangeImpl.this.getSSubArray(n);
                    CTRunTrackChangeImpl.this.removeSSub(n);
                    return sSubArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfSSubArray();
                }
            }
            return new SSubList();
        }
    }
    
    @Deprecated
    @Override
    public CTSSub[] getSSubArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.SSUB$90, (List)list);
            final CTSSub[] array = new CTSSub[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTSSub getSSubArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSSub ctsSub = (CTSSub)this.get_store().find_element_user(CTRunTrackChangeImpl.SSUB$90, n);
            if (ctsSub == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctsSub;
        }
    }
    
    @Override
    public int sizeOfSSubArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.SSUB$90);
        }
    }
    
    @Override
    public void setSSubArray(final CTSSub[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.SSUB$90);
    }
    
    @Override
    public void setSSubArray(final int n, final CTSSub ctsSub) {
        this.generatedSetterHelperImpl((XmlObject)ctsSub, CTRunTrackChangeImpl.SSUB$90, n, (short)2);
    }
    
    @Override
    public CTSSub insertNewSSub(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSSub)this.get_store().insert_element_user(CTRunTrackChangeImpl.SSUB$90, n);
        }
    }
    
    @Override
    public CTSSub addNewSSub() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSSub)this.get_store().add_element_user(CTRunTrackChangeImpl.SSUB$90);
        }
    }
    
    @Override
    public void removeSSub(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.SSUB$90, n);
        }
    }
    
    @Override
    public List<CTSSubSup> getSSubSupList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SSubSupList extends AbstractList<CTSSubSup>
            {
                @Override
                public CTSSubSup get(final int n) {
                    return CTRunTrackChangeImpl.this.getSSubSupArray(n);
                }
                
                @Override
                public CTSSubSup set(final int n, final CTSSubSup ctsSubSup) {
                    final CTSSubSup sSubSupArray = CTRunTrackChangeImpl.this.getSSubSupArray(n);
                    CTRunTrackChangeImpl.this.setSSubSupArray(n, ctsSubSup);
                    return sSubSupArray;
                }
                
                @Override
                public void add(final int n, final CTSSubSup ctsSubSup) {
                    CTRunTrackChangeImpl.this.insertNewSSubSup(n).set((XmlObject)ctsSubSup);
                }
                
                @Override
                public CTSSubSup remove(final int n) {
                    final CTSSubSup sSubSupArray = CTRunTrackChangeImpl.this.getSSubSupArray(n);
                    CTRunTrackChangeImpl.this.removeSSubSup(n);
                    return sSubSupArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfSSubSupArray();
                }
            }
            return new SSubSupList();
        }
    }
    
    @Deprecated
    @Override
    public CTSSubSup[] getSSubSupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.SSUBSUP$92, (List)list);
            final CTSSubSup[] array = new CTSSubSup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTSSubSup getSSubSupArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSSubSup ctsSubSup = (CTSSubSup)this.get_store().find_element_user(CTRunTrackChangeImpl.SSUBSUP$92, n);
            if (ctsSubSup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctsSubSup;
        }
    }
    
    @Override
    public int sizeOfSSubSupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.SSUBSUP$92);
        }
    }
    
    @Override
    public void setSSubSupArray(final CTSSubSup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.SSUBSUP$92);
    }
    
    @Override
    public void setSSubSupArray(final int n, final CTSSubSup ctsSubSup) {
        this.generatedSetterHelperImpl((XmlObject)ctsSubSup, CTRunTrackChangeImpl.SSUBSUP$92, n, (short)2);
    }
    
    @Override
    public CTSSubSup insertNewSSubSup(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSSubSup)this.get_store().insert_element_user(CTRunTrackChangeImpl.SSUBSUP$92, n);
        }
    }
    
    @Override
    public CTSSubSup addNewSSubSup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSSubSup)this.get_store().add_element_user(CTRunTrackChangeImpl.SSUBSUP$92);
        }
    }
    
    @Override
    public void removeSSubSup(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.SSUBSUP$92, n);
        }
    }
    
    @Override
    public List<CTSSup> getSSupList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SSupList extends AbstractList<CTSSup>
            {
                @Override
                public CTSSup get(final int n) {
                    return CTRunTrackChangeImpl.this.getSSupArray(n);
                }
                
                @Override
                public CTSSup set(final int n, final CTSSup ctsSup) {
                    final CTSSup sSupArray = CTRunTrackChangeImpl.this.getSSupArray(n);
                    CTRunTrackChangeImpl.this.setSSupArray(n, ctsSup);
                    return sSupArray;
                }
                
                @Override
                public void add(final int n, final CTSSup ctsSup) {
                    CTRunTrackChangeImpl.this.insertNewSSup(n).set((XmlObject)ctsSup);
                }
                
                @Override
                public CTSSup remove(final int n) {
                    final CTSSup sSupArray = CTRunTrackChangeImpl.this.getSSupArray(n);
                    CTRunTrackChangeImpl.this.removeSSup(n);
                    return sSupArray;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfSSupArray();
                }
            }
            return new SSupList();
        }
    }
    
    @Deprecated
    @Override
    public CTSSup[] getSSupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.SSUP$94, (List)list);
            final CTSSup[] array = new CTSSup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public CTSSup getSSupArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSSup ctsSup = (CTSSup)this.get_store().find_element_user(CTRunTrackChangeImpl.SSUP$94, n);
            if (ctsSup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctsSup;
        }
    }
    
    @Override
    public int sizeOfSSupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.SSUP$94);
        }
    }
    
    @Override
    public void setSSupArray(final CTSSup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.SSUP$94);
    }
    
    @Override
    public void setSSupArray(final int n, final CTSSup ctsSup) {
        this.generatedSetterHelperImpl((XmlObject)ctsSup, CTRunTrackChangeImpl.SSUP$94, n, (short)2);
    }
    
    @Override
    public CTSSup insertNewSSup(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSSup)this.get_store().insert_element_user(CTRunTrackChangeImpl.SSUP$94, n);
        }
    }
    
    @Override
    public CTSSup addNewSSup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSSup)this.get_store().add_element_user(CTRunTrackChangeImpl.SSUP$94);
        }
    }
    
    @Override
    public void removeSSup(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.SSUP$94, n);
        }
    }
    
    @Override
    public List<org.openxmlformats.schemas.officeDocument.x2006.math.CTR> getR2List() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class R2List extends AbstractList<org.openxmlformats.schemas.officeDocument.x2006.math.CTR>
            {
                @Override
                public org.openxmlformats.schemas.officeDocument.x2006.math.CTR get(final int n) {
                    return CTRunTrackChangeImpl.this.getR2Array(n);
                }
                
                @Override
                public org.openxmlformats.schemas.officeDocument.x2006.math.CTR set(final int n, final org.openxmlformats.schemas.officeDocument.x2006.math.CTR ctr) {
                    final org.openxmlformats.schemas.officeDocument.x2006.math.CTR r2Array = CTRunTrackChangeImpl.this.getR2Array(n);
                    CTRunTrackChangeImpl.this.setR2Array(n, ctr);
                    return r2Array;
                }
                
                @Override
                public void add(final int n, final org.openxmlformats.schemas.officeDocument.x2006.math.CTR ctr) {
                    CTRunTrackChangeImpl.this.insertNewR2(n).set((XmlObject)ctr);
                }
                
                @Override
                public org.openxmlformats.schemas.officeDocument.x2006.math.CTR remove(final int n) {
                    final org.openxmlformats.schemas.officeDocument.x2006.math.CTR r2Array = CTRunTrackChangeImpl.this.getR2Array(n);
                    CTRunTrackChangeImpl.this.removeR2(n);
                    return r2Array;
                }
                
                @Override
                public int size() {
                    return CTRunTrackChangeImpl.this.sizeOfR2Array();
                }
            }
            return new R2List();
        }
    }
    
    @Deprecated
    @Override
    public org.openxmlformats.schemas.officeDocument.x2006.math.CTR[] getR2Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRunTrackChangeImpl.R2$96, (List)list);
            final org.openxmlformats.schemas.officeDocument.x2006.math.CTR[] array = new org.openxmlformats.schemas.officeDocument.x2006.math.CTR[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    @Override
    public org.openxmlformats.schemas.officeDocument.x2006.math.CTR getR2Array(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final org.openxmlformats.schemas.officeDocument.x2006.math.CTR ctr = (org.openxmlformats.schemas.officeDocument.x2006.math.CTR)this.get_store().find_element_user(CTRunTrackChangeImpl.R2$96, n);
            if (ctr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctr;
        }
    }
    
    @Override
    public int sizeOfR2Array() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRunTrackChangeImpl.R2$96);
        }
    }
    
    @Override
    public void setR2Array(final org.openxmlformats.schemas.officeDocument.x2006.math.CTR[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRunTrackChangeImpl.R2$96);
    }
    
    @Override
    public void setR2Array(final int n, final org.openxmlformats.schemas.officeDocument.x2006.math.CTR ctr) {
        this.generatedSetterHelperImpl((XmlObject)ctr, CTRunTrackChangeImpl.R2$96, n, (short)2);
    }
    
    @Override
    public org.openxmlformats.schemas.officeDocument.x2006.math.CTR insertNewR2(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (org.openxmlformats.schemas.officeDocument.x2006.math.CTR)this.get_store().insert_element_user(CTRunTrackChangeImpl.R2$96, n);
        }
    }
    
    @Override
    public org.openxmlformats.schemas.officeDocument.x2006.math.CTR addNewR2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (org.openxmlformats.schemas.officeDocument.x2006.math.CTR)this.get_store().add_element_user(CTRunTrackChangeImpl.R2$96);
        }
    }
    
    @Override
    public void removeR2(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRunTrackChangeImpl.R2$96, n);
        }
    }
    
    static {
        CUSTOMXML$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXml");
        SMARTTAG$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "smartTag");
        SDT$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sdt");
        R$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "r");
        PROOFERR$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "proofErr");
        PERMSTART$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "permStart");
        PERMEND$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "permEnd");
        BOOKMARKSTART$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookmarkStart");
        BOOKMARKEND$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookmarkEnd");
        MOVEFROMRANGESTART$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFromRangeStart");
        MOVEFROMRANGEEND$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFromRangeEnd");
        MOVETORANGESTART$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveToRangeStart");
        MOVETORANGEEND$24 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveToRangeEnd");
        COMMENTRANGESTART$26 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "commentRangeStart");
        COMMENTRANGEEND$28 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "commentRangeEnd");
        CUSTOMXMLINSRANGESTART$30 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlInsRangeStart");
        CUSTOMXMLINSRANGEEND$32 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlInsRangeEnd");
        CUSTOMXMLDELRANGESTART$34 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlDelRangeStart");
        CUSTOMXMLDELRANGEEND$36 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlDelRangeEnd");
        CUSTOMXMLMOVEFROMRANGESTART$38 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveFromRangeStart");
        CUSTOMXMLMOVEFROMRANGEEND$40 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveFromRangeEnd");
        CUSTOMXMLMOVETORANGESTART$42 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveToRangeStart");
        CUSTOMXMLMOVETORANGEEND$44 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveToRangeEnd");
        INS$46 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ins");
        DEL$48 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "del");
        MOVEFROM$50 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFrom");
        MOVETO$52 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveTo");
        OMATHPARA$54 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "oMathPara");
        OMATH$56 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "oMath");
        ACC$58 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "acc");
        BAR$60 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "bar");
        BOX$62 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "box");
        BORDERBOX$64 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "borderBox");
        D$66 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "d");
        EQARR$68 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "eqArr");
        F$70 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "f");
        FUNC$72 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "func");
        GROUPCHR$74 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "groupChr");
        LIMLOW$76 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "limLow");
        LIMUPP$78 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "limUpp");
        M$80 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "m");
        NARY$82 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "nary");
        PHANT$84 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "phant");
        RAD$86 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "rad");
        SPRE$88 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "sPre");
        SSUB$90 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "sSub");
        SSUBSUP$92 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "sSubSup");
        SSUP$94 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "sSup");
        R2$96 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "r");
    }
}
