package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STString;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRel;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRunTrackChange;
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
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCustomXmlRun;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSmartTagPr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSmartTagRun;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSmartTagRunImpl extends XmlComplexContentImpl implements CTSmartTagRun
{
    private static final long serialVersionUID = 1L;
    private static final QName SMARTTAGPR$0;
    private static final QName CUSTOMXML$2;
    private static final QName SMARTTAG$4;
    private static final QName SDT$6;
    private static final QName R$8;
    private static final QName PROOFERR$10;
    private static final QName PERMSTART$12;
    private static final QName PERMEND$14;
    private static final QName BOOKMARKSTART$16;
    private static final QName BOOKMARKEND$18;
    private static final QName MOVEFROMRANGESTART$20;
    private static final QName MOVEFROMRANGEEND$22;
    private static final QName MOVETORANGESTART$24;
    private static final QName MOVETORANGEEND$26;
    private static final QName COMMENTRANGESTART$28;
    private static final QName COMMENTRANGEEND$30;
    private static final QName CUSTOMXMLINSRANGESTART$32;
    private static final QName CUSTOMXMLINSRANGEEND$34;
    private static final QName CUSTOMXMLDELRANGESTART$36;
    private static final QName CUSTOMXMLDELRANGEEND$38;
    private static final QName CUSTOMXMLMOVEFROMRANGESTART$40;
    private static final QName CUSTOMXMLMOVEFROMRANGEEND$42;
    private static final QName CUSTOMXMLMOVETORANGESTART$44;
    private static final QName CUSTOMXMLMOVETORANGEEND$46;
    private static final QName INS$48;
    private static final QName DEL$50;
    private static final QName MOVEFROM$52;
    private static final QName MOVETO$54;
    private static final QName OMATHPARA$56;
    private static final QName OMATH$58;
    private static final QName FLDSIMPLE$60;
    private static final QName HYPERLINK$62;
    private static final QName SUBDOC$64;
    private static final QName URI$66;
    private static final QName ELEMENT$68;
    
    public CTSmartTagRunImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTSmartTagPr getSmartTagPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSmartTagPr ctSmartTagPr = (CTSmartTagPr)this.get_store().find_element_user(CTSmartTagRunImpl.SMARTTAGPR$0, 0);
            if (ctSmartTagPr == null) {
                return null;
            }
            return ctSmartTagPr;
        }
    }
    
    public boolean isSetSmartTagPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.SMARTTAGPR$0) != 0;
        }
    }
    
    public void setSmartTagPr(final CTSmartTagPr ctSmartTagPr) {
        this.generatedSetterHelperImpl((XmlObject)ctSmartTagPr, CTSmartTagRunImpl.SMARTTAGPR$0, 0, (short)1);
    }
    
    public CTSmartTagPr addNewSmartTagPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSmartTagPr)this.get_store().add_element_user(CTSmartTagRunImpl.SMARTTAGPR$0);
        }
    }
    
    public void unsetSmartTagPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.SMARTTAGPR$0, 0);
        }
    }
    
    public List<CTCustomXmlRun> getCustomXmlList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlList extends AbstractList<CTCustomXmlRun>
            {
                @Override
                public CTCustomXmlRun get(final int n) {
                    return CTSmartTagRunImpl.this.getCustomXmlArray(n);
                }
                
                @Override
                public CTCustomXmlRun set(final int n, final CTCustomXmlRun ctCustomXmlRun) {
                    final CTCustomXmlRun customXmlArray = CTSmartTagRunImpl.this.getCustomXmlArray(n);
                    CTSmartTagRunImpl.this.setCustomXmlArray(n, ctCustomXmlRun);
                    return customXmlArray;
                }
                
                @Override
                public void add(final int n, final CTCustomXmlRun ctCustomXmlRun) {
                    CTSmartTagRunImpl.this.insertNewCustomXml(n).set((XmlObject)ctCustomXmlRun);
                }
                
                @Override
                public CTCustomXmlRun remove(final int n) {
                    final CTCustomXmlRun customXmlArray = CTSmartTagRunImpl.this.getCustomXmlArray(n);
                    CTSmartTagRunImpl.this.removeCustomXml(n);
                    return customXmlArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfCustomXmlArray();
                }
            }
            return new CustomXmlList();
        }
    }
    
    @Deprecated
    public CTCustomXmlRun[] getCustomXmlArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.CUSTOMXML$2, (List)list);
            final CTCustomXmlRun[] array = new CTCustomXmlRun[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCustomXmlRun getCustomXmlArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomXmlRun ctCustomXmlRun = (CTCustomXmlRun)this.get_store().find_element_user(CTSmartTagRunImpl.CUSTOMXML$2, n);
            if (ctCustomXmlRun == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCustomXmlRun;
        }
    }
    
    public int sizeOfCustomXmlArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.CUSTOMXML$2);
        }
    }
    
    public void setCustomXmlArray(final CTCustomXmlRun[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.CUSTOMXML$2);
    }
    
    public void setCustomXmlArray(final int n, final CTCustomXmlRun ctCustomXmlRun) {
        this.generatedSetterHelperImpl((XmlObject)ctCustomXmlRun, CTSmartTagRunImpl.CUSTOMXML$2, n, (short)2);
    }
    
    public CTCustomXmlRun insertNewCustomXml(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomXmlRun)this.get_store().insert_element_user(CTSmartTagRunImpl.CUSTOMXML$2, n);
        }
    }
    
    public CTCustomXmlRun addNewCustomXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomXmlRun)this.get_store().add_element_user(CTSmartTagRunImpl.CUSTOMXML$2);
        }
    }
    
    public void removeCustomXml(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.CUSTOMXML$2, n);
        }
    }
    
    public List<CTSmartTagRun> getSmartTagList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SmartTagList extends AbstractList<CTSmartTagRun>
            {
                @Override
                public CTSmartTagRun get(final int n) {
                    return CTSmartTagRunImpl.this.getSmartTagArray(n);
                }
                
                @Override
                public CTSmartTagRun set(final int n, final CTSmartTagRun ctSmartTagRun) {
                    final CTSmartTagRun smartTagArray = CTSmartTagRunImpl.this.getSmartTagArray(n);
                    CTSmartTagRunImpl.this.setSmartTagArray(n, ctSmartTagRun);
                    return smartTagArray;
                }
                
                @Override
                public void add(final int n, final CTSmartTagRun ctSmartTagRun) {
                    CTSmartTagRunImpl.this.insertNewSmartTag(n).set((XmlObject)ctSmartTagRun);
                }
                
                @Override
                public CTSmartTagRun remove(final int n) {
                    final CTSmartTagRun smartTagArray = CTSmartTagRunImpl.this.getSmartTagArray(n);
                    CTSmartTagRunImpl.this.removeSmartTag(n);
                    return smartTagArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfSmartTagArray();
                }
            }
            return new SmartTagList();
        }
    }
    
    @Deprecated
    public CTSmartTagRun[] getSmartTagArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.SMARTTAG$4, (List)list);
            final CTSmartTagRun[] array = new CTSmartTagRun[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSmartTagRun getSmartTagArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSmartTagRun ctSmartTagRun = (CTSmartTagRun)this.get_store().find_element_user(CTSmartTagRunImpl.SMARTTAG$4, n);
            if (ctSmartTagRun == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSmartTagRun;
        }
    }
    
    public int sizeOfSmartTagArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.SMARTTAG$4);
        }
    }
    
    public void setSmartTagArray(final CTSmartTagRun[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.SMARTTAG$4);
    }
    
    public void setSmartTagArray(final int n, final CTSmartTagRun ctSmartTagRun) {
        this.generatedSetterHelperImpl((XmlObject)ctSmartTagRun, CTSmartTagRunImpl.SMARTTAG$4, n, (short)2);
    }
    
    public CTSmartTagRun insertNewSmartTag(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSmartTagRun)this.get_store().insert_element_user(CTSmartTagRunImpl.SMARTTAG$4, n);
        }
    }
    
    public CTSmartTagRun addNewSmartTag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSmartTagRun)this.get_store().add_element_user(CTSmartTagRunImpl.SMARTTAG$4);
        }
    }
    
    public void removeSmartTag(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.SMARTTAG$4, n);
        }
    }
    
    public List<CTSdtRun> getSdtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SdtList extends AbstractList<CTSdtRun>
            {
                @Override
                public CTSdtRun get(final int n) {
                    return CTSmartTagRunImpl.this.getSdtArray(n);
                }
                
                @Override
                public CTSdtRun set(final int n, final CTSdtRun ctSdtRun) {
                    final CTSdtRun sdtArray = CTSmartTagRunImpl.this.getSdtArray(n);
                    CTSmartTagRunImpl.this.setSdtArray(n, ctSdtRun);
                    return sdtArray;
                }
                
                @Override
                public void add(final int n, final CTSdtRun ctSdtRun) {
                    CTSmartTagRunImpl.this.insertNewSdt(n).set((XmlObject)ctSdtRun);
                }
                
                @Override
                public CTSdtRun remove(final int n) {
                    final CTSdtRun sdtArray = CTSmartTagRunImpl.this.getSdtArray(n);
                    CTSmartTagRunImpl.this.removeSdt(n);
                    return sdtArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfSdtArray();
                }
            }
            return new SdtList();
        }
    }
    
    @Deprecated
    public CTSdtRun[] getSdtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.SDT$6, (List)list);
            final CTSdtRun[] array = new CTSdtRun[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSdtRun getSdtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtRun ctSdtRun = (CTSdtRun)this.get_store().find_element_user(CTSmartTagRunImpl.SDT$6, n);
            if (ctSdtRun == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSdtRun;
        }
    }
    
    public int sizeOfSdtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.SDT$6);
        }
    }
    
    public void setSdtArray(final CTSdtRun[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.SDT$6);
    }
    
    public void setSdtArray(final int n, final CTSdtRun ctSdtRun) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtRun, CTSmartTagRunImpl.SDT$6, n, (short)2);
    }
    
    public CTSdtRun insertNewSdt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtRun)this.get_store().insert_element_user(CTSmartTagRunImpl.SDT$6, n);
        }
    }
    
    public CTSdtRun addNewSdt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtRun)this.get_store().add_element_user(CTSmartTagRunImpl.SDT$6);
        }
    }
    
    public void removeSdt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.SDT$6, n);
        }
    }
    
    public List<CTR> getRList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RList extends AbstractList<CTR>
            {
                @Override
                public CTR get(final int n) {
                    return CTSmartTagRunImpl.this.getRArray(n);
                }
                
                @Override
                public CTR set(final int n, final CTR ctr) {
                    final CTR rArray = CTSmartTagRunImpl.this.getRArray(n);
                    CTSmartTagRunImpl.this.setRArray(n, ctr);
                    return rArray;
                }
                
                @Override
                public void add(final int n, final CTR ctr) {
                    CTSmartTagRunImpl.this.insertNewR(n).set((XmlObject)ctr);
                }
                
                @Override
                public CTR remove(final int n) {
                    final CTR rArray = CTSmartTagRunImpl.this.getRArray(n);
                    CTSmartTagRunImpl.this.removeR(n);
                    return rArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfRArray();
                }
            }
            return new RList();
        }
    }
    
    @Deprecated
    public CTR[] getRArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.R$8, (List)list);
            final CTR[] array = new CTR[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTR getRArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTR ctr = (CTR)this.get_store().find_element_user(CTSmartTagRunImpl.R$8, n);
            if (ctr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctr;
        }
    }
    
    public int sizeOfRArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.R$8);
        }
    }
    
    public void setRArray(final CTR[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.R$8);
    }
    
    public void setRArray(final int n, final CTR ctr) {
        this.generatedSetterHelperImpl((XmlObject)ctr, CTSmartTagRunImpl.R$8, n, (short)2);
    }
    
    public CTR insertNewR(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTR)this.get_store().insert_element_user(CTSmartTagRunImpl.R$8, n);
        }
    }
    
    public CTR addNewR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTR)this.get_store().add_element_user(CTSmartTagRunImpl.R$8);
        }
    }
    
    public void removeR(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.R$8, n);
        }
    }
    
    public List<CTProofErr> getProofErrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ProofErrList extends AbstractList<CTProofErr>
            {
                @Override
                public CTProofErr get(final int n) {
                    return CTSmartTagRunImpl.this.getProofErrArray(n);
                }
                
                @Override
                public CTProofErr set(final int n, final CTProofErr ctProofErr) {
                    final CTProofErr proofErrArray = CTSmartTagRunImpl.this.getProofErrArray(n);
                    CTSmartTagRunImpl.this.setProofErrArray(n, ctProofErr);
                    return proofErrArray;
                }
                
                @Override
                public void add(final int n, final CTProofErr ctProofErr) {
                    CTSmartTagRunImpl.this.insertNewProofErr(n).set((XmlObject)ctProofErr);
                }
                
                @Override
                public CTProofErr remove(final int n) {
                    final CTProofErr proofErrArray = CTSmartTagRunImpl.this.getProofErrArray(n);
                    CTSmartTagRunImpl.this.removeProofErr(n);
                    return proofErrArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfProofErrArray();
                }
            }
            return new ProofErrList();
        }
    }
    
    @Deprecated
    public CTProofErr[] getProofErrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.PROOFERR$10, (List)list);
            final CTProofErr[] array = new CTProofErr[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTProofErr getProofErrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTProofErr ctProofErr = (CTProofErr)this.get_store().find_element_user(CTSmartTagRunImpl.PROOFERR$10, n);
            if (ctProofErr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctProofErr;
        }
    }
    
    public int sizeOfProofErrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.PROOFERR$10);
        }
    }
    
    public void setProofErrArray(final CTProofErr[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.PROOFERR$10);
    }
    
    public void setProofErrArray(final int n, final CTProofErr ctProofErr) {
        this.generatedSetterHelperImpl((XmlObject)ctProofErr, CTSmartTagRunImpl.PROOFERR$10, n, (short)2);
    }
    
    public CTProofErr insertNewProofErr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProofErr)this.get_store().insert_element_user(CTSmartTagRunImpl.PROOFERR$10, n);
        }
    }
    
    public CTProofErr addNewProofErr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProofErr)this.get_store().add_element_user(CTSmartTagRunImpl.PROOFERR$10);
        }
    }
    
    public void removeProofErr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.PROOFERR$10, n);
        }
    }
    
    public List<CTPermStart> getPermStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PermStartList extends AbstractList<CTPermStart>
            {
                @Override
                public CTPermStart get(final int n) {
                    return CTSmartTagRunImpl.this.getPermStartArray(n);
                }
                
                @Override
                public CTPermStart set(final int n, final CTPermStart ctPermStart) {
                    final CTPermStart permStartArray = CTSmartTagRunImpl.this.getPermStartArray(n);
                    CTSmartTagRunImpl.this.setPermStartArray(n, ctPermStart);
                    return permStartArray;
                }
                
                @Override
                public void add(final int n, final CTPermStart ctPermStart) {
                    CTSmartTagRunImpl.this.insertNewPermStart(n).set((XmlObject)ctPermStart);
                }
                
                @Override
                public CTPermStart remove(final int n) {
                    final CTPermStart permStartArray = CTSmartTagRunImpl.this.getPermStartArray(n);
                    CTSmartTagRunImpl.this.removePermStart(n);
                    return permStartArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfPermStartArray();
                }
            }
            return new PermStartList();
        }
    }
    
    @Deprecated
    public CTPermStart[] getPermStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.PERMSTART$12, (List)list);
            final CTPermStart[] array = new CTPermStart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPermStart getPermStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPermStart ctPermStart = (CTPermStart)this.get_store().find_element_user(CTSmartTagRunImpl.PERMSTART$12, n);
            if (ctPermStart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPermStart;
        }
    }
    
    public int sizeOfPermStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.PERMSTART$12);
        }
    }
    
    public void setPermStartArray(final CTPermStart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.PERMSTART$12);
    }
    
    public void setPermStartArray(final int n, final CTPermStart ctPermStart) {
        this.generatedSetterHelperImpl((XmlObject)ctPermStart, CTSmartTagRunImpl.PERMSTART$12, n, (short)2);
    }
    
    public CTPermStart insertNewPermStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPermStart)this.get_store().insert_element_user(CTSmartTagRunImpl.PERMSTART$12, n);
        }
    }
    
    public CTPermStart addNewPermStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPermStart)this.get_store().add_element_user(CTSmartTagRunImpl.PERMSTART$12);
        }
    }
    
    public void removePermStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.PERMSTART$12, n);
        }
    }
    
    public List<CTPerm> getPermEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PermEndList extends AbstractList<CTPerm>
            {
                @Override
                public CTPerm get(final int n) {
                    return CTSmartTagRunImpl.this.getPermEndArray(n);
                }
                
                @Override
                public CTPerm set(final int n, final CTPerm ctPerm) {
                    final CTPerm permEndArray = CTSmartTagRunImpl.this.getPermEndArray(n);
                    CTSmartTagRunImpl.this.setPermEndArray(n, ctPerm);
                    return permEndArray;
                }
                
                @Override
                public void add(final int n, final CTPerm ctPerm) {
                    CTSmartTagRunImpl.this.insertNewPermEnd(n).set((XmlObject)ctPerm);
                }
                
                @Override
                public CTPerm remove(final int n) {
                    final CTPerm permEndArray = CTSmartTagRunImpl.this.getPermEndArray(n);
                    CTSmartTagRunImpl.this.removePermEnd(n);
                    return permEndArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfPermEndArray();
                }
            }
            return new PermEndList();
        }
    }
    
    @Deprecated
    public CTPerm[] getPermEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.PERMEND$14, (List)list);
            final CTPerm[] array = new CTPerm[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPerm getPermEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPerm ctPerm = (CTPerm)this.get_store().find_element_user(CTSmartTagRunImpl.PERMEND$14, n);
            if (ctPerm == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPerm;
        }
    }
    
    public int sizeOfPermEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.PERMEND$14);
        }
    }
    
    public void setPermEndArray(final CTPerm[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.PERMEND$14);
    }
    
    public void setPermEndArray(final int n, final CTPerm ctPerm) {
        this.generatedSetterHelperImpl((XmlObject)ctPerm, CTSmartTagRunImpl.PERMEND$14, n, (short)2);
    }
    
    public CTPerm insertNewPermEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerm)this.get_store().insert_element_user(CTSmartTagRunImpl.PERMEND$14, n);
        }
    }
    
    public CTPerm addNewPermEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerm)this.get_store().add_element_user(CTSmartTagRunImpl.PERMEND$14);
        }
    }
    
    public void removePermEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.PERMEND$14, n);
        }
    }
    
    public List<CTBookmark> getBookmarkStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BookmarkStartList extends AbstractList<CTBookmark>
            {
                @Override
                public CTBookmark get(final int n) {
                    return CTSmartTagRunImpl.this.getBookmarkStartArray(n);
                }
                
                @Override
                public CTBookmark set(final int n, final CTBookmark ctBookmark) {
                    final CTBookmark bookmarkStartArray = CTSmartTagRunImpl.this.getBookmarkStartArray(n);
                    CTSmartTagRunImpl.this.setBookmarkStartArray(n, ctBookmark);
                    return bookmarkStartArray;
                }
                
                @Override
                public void add(final int n, final CTBookmark ctBookmark) {
                    CTSmartTagRunImpl.this.insertNewBookmarkStart(n).set((XmlObject)ctBookmark);
                }
                
                @Override
                public CTBookmark remove(final int n) {
                    final CTBookmark bookmarkStartArray = CTSmartTagRunImpl.this.getBookmarkStartArray(n);
                    CTSmartTagRunImpl.this.removeBookmarkStart(n);
                    return bookmarkStartArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfBookmarkStartArray();
                }
            }
            return new BookmarkStartList();
        }
    }
    
    @Deprecated
    public CTBookmark[] getBookmarkStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.BOOKMARKSTART$16, (List)list);
            final CTBookmark[] array = new CTBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBookmark getBookmarkStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBookmark ctBookmark = (CTBookmark)this.get_store().find_element_user(CTSmartTagRunImpl.BOOKMARKSTART$16, n);
            if (ctBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBookmark;
        }
    }
    
    public int sizeOfBookmarkStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.BOOKMARKSTART$16);
        }
    }
    
    public void setBookmarkStartArray(final CTBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.BOOKMARKSTART$16);
    }
    
    public void setBookmarkStartArray(final int n, final CTBookmark ctBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctBookmark, CTSmartTagRunImpl.BOOKMARKSTART$16, n, (short)2);
    }
    
    public CTBookmark insertNewBookmarkStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookmark)this.get_store().insert_element_user(CTSmartTagRunImpl.BOOKMARKSTART$16, n);
        }
    }
    
    public CTBookmark addNewBookmarkStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookmark)this.get_store().add_element_user(CTSmartTagRunImpl.BOOKMARKSTART$16);
        }
    }
    
    public void removeBookmarkStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.BOOKMARKSTART$16, n);
        }
    }
    
    public List<CTMarkupRange> getBookmarkEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BookmarkEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTSmartTagRunImpl.this.getBookmarkEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange bookmarkEndArray = CTSmartTagRunImpl.this.getBookmarkEndArray(n);
                    CTSmartTagRunImpl.this.setBookmarkEndArray(n, ctMarkupRange);
                    return bookmarkEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTSmartTagRunImpl.this.insertNewBookmarkEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange bookmarkEndArray = CTSmartTagRunImpl.this.getBookmarkEndArray(n);
                    CTSmartTagRunImpl.this.removeBookmarkEnd(n);
                    return bookmarkEndArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfBookmarkEndArray();
                }
            }
            return new BookmarkEndList();
        }
    }
    
    @Deprecated
    public CTMarkupRange[] getBookmarkEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.BOOKMARKEND$18, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getBookmarkEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTSmartTagRunImpl.BOOKMARKEND$18, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfBookmarkEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.BOOKMARKEND$18);
        }
    }
    
    public void setBookmarkEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.BOOKMARKEND$18);
    }
    
    public void setBookmarkEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTSmartTagRunImpl.BOOKMARKEND$18, n, (short)2);
    }
    
    public CTMarkupRange insertNewBookmarkEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTSmartTagRunImpl.BOOKMARKEND$18, n);
        }
    }
    
    public CTMarkupRange addNewBookmarkEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTSmartTagRunImpl.BOOKMARKEND$18);
        }
    }
    
    public void removeBookmarkEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.BOOKMARKEND$18, n);
        }
    }
    
    public List<CTMoveBookmark> getMoveFromRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromRangeStartList extends AbstractList<CTMoveBookmark>
            {
                @Override
                public CTMoveBookmark get(final int n) {
                    return CTSmartTagRunImpl.this.getMoveFromRangeStartArray(n);
                }
                
                @Override
                public CTMoveBookmark set(final int n, final CTMoveBookmark ctMoveBookmark) {
                    final CTMoveBookmark moveFromRangeStartArray = CTSmartTagRunImpl.this.getMoveFromRangeStartArray(n);
                    CTSmartTagRunImpl.this.setMoveFromRangeStartArray(n, ctMoveBookmark);
                    return moveFromRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMoveBookmark ctMoveBookmark) {
                    CTSmartTagRunImpl.this.insertNewMoveFromRangeStart(n).set((XmlObject)ctMoveBookmark);
                }
                
                @Override
                public CTMoveBookmark remove(final int n) {
                    final CTMoveBookmark moveFromRangeStartArray = CTSmartTagRunImpl.this.getMoveFromRangeStartArray(n);
                    CTSmartTagRunImpl.this.removeMoveFromRangeStart(n);
                    return moveFromRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfMoveFromRangeStartArray();
                }
            }
            return new MoveFromRangeStartList();
        }
    }
    
    @Deprecated
    public CTMoveBookmark[] getMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.MOVEFROMRANGESTART$20, (List)list);
            final CTMoveBookmark[] array = new CTMoveBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMoveBookmark getMoveFromRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMoveBookmark ctMoveBookmark = (CTMoveBookmark)this.get_store().find_element_user(CTSmartTagRunImpl.MOVEFROMRANGESTART$20, n);
            if (ctMoveBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMoveBookmark;
        }
    }
    
    public int sizeOfMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.MOVEFROMRANGESTART$20);
        }
    }
    
    public void setMoveFromRangeStartArray(final CTMoveBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.MOVEFROMRANGESTART$20);
    }
    
    public void setMoveFromRangeStartArray(final int n, final CTMoveBookmark ctMoveBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctMoveBookmark, CTSmartTagRunImpl.MOVEFROMRANGESTART$20, n, (short)2);
    }
    
    public CTMoveBookmark insertNewMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().insert_element_user(CTSmartTagRunImpl.MOVEFROMRANGESTART$20, n);
        }
    }
    
    public CTMoveBookmark addNewMoveFromRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().add_element_user(CTSmartTagRunImpl.MOVEFROMRANGESTART$20);
        }
    }
    
    public void removeMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.MOVEFROMRANGESTART$20, n);
        }
    }
    
    public List<CTMarkupRange> getMoveFromRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTSmartTagRunImpl.this.getMoveFromRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange moveFromRangeEndArray = CTSmartTagRunImpl.this.getMoveFromRangeEndArray(n);
                    CTSmartTagRunImpl.this.setMoveFromRangeEndArray(n, ctMarkupRange);
                    return moveFromRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTSmartTagRunImpl.this.insertNewMoveFromRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange moveFromRangeEndArray = CTSmartTagRunImpl.this.getMoveFromRangeEndArray(n);
                    CTSmartTagRunImpl.this.removeMoveFromRangeEnd(n);
                    return moveFromRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfMoveFromRangeEndArray();
                }
            }
            return new MoveFromRangeEndList();
        }
    }
    
    @Deprecated
    public CTMarkupRange[] getMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.MOVEFROMRANGEEND$22, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getMoveFromRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTSmartTagRunImpl.MOVEFROMRANGEEND$22, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.MOVEFROMRANGEEND$22);
        }
    }
    
    public void setMoveFromRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.MOVEFROMRANGEEND$22);
    }
    
    public void setMoveFromRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTSmartTagRunImpl.MOVEFROMRANGEEND$22, n, (short)2);
    }
    
    public CTMarkupRange insertNewMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTSmartTagRunImpl.MOVEFROMRANGEEND$22, n);
        }
    }
    
    public CTMarkupRange addNewMoveFromRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTSmartTagRunImpl.MOVEFROMRANGEEND$22);
        }
    }
    
    public void removeMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.MOVEFROMRANGEEND$22, n);
        }
    }
    
    public List<CTMoveBookmark> getMoveToRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToRangeStartList extends AbstractList<CTMoveBookmark>
            {
                @Override
                public CTMoveBookmark get(final int n) {
                    return CTSmartTagRunImpl.this.getMoveToRangeStartArray(n);
                }
                
                @Override
                public CTMoveBookmark set(final int n, final CTMoveBookmark ctMoveBookmark) {
                    final CTMoveBookmark moveToRangeStartArray = CTSmartTagRunImpl.this.getMoveToRangeStartArray(n);
                    CTSmartTagRunImpl.this.setMoveToRangeStartArray(n, ctMoveBookmark);
                    return moveToRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMoveBookmark ctMoveBookmark) {
                    CTSmartTagRunImpl.this.insertNewMoveToRangeStart(n).set((XmlObject)ctMoveBookmark);
                }
                
                @Override
                public CTMoveBookmark remove(final int n) {
                    final CTMoveBookmark moveToRangeStartArray = CTSmartTagRunImpl.this.getMoveToRangeStartArray(n);
                    CTSmartTagRunImpl.this.removeMoveToRangeStart(n);
                    return moveToRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfMoveToRangeStartArray();
                }
            }
            return new MoveToRangeStartList();
        }
    }
    
    @Deprecated
    public CTMoveBookmark[] getMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.MOVETORANGESTART$24, (List)list);
            final CTMoveBookmark[] array = new CTMoveBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMoveBookmark getMoveToRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMoveBookmark ctMoveBookmark = (CTMoveBookmark)this.get_store().find_element_user(CTSmartTagRunImpl.MOVETORANGESTART$24, n);
            if (ctMoveBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMoveBookmark;
        }
    }
    
    public int sizeOfMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.MOVETORANGESTART$24);
        }
    }
    
    public void setMoveToRangeStartArray(final CTMoveBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.MOVETORANGESTART$24);
    }
    
    public void setMoveToRangeStartArray(final int n, final CTMoveBookmark ctMoveBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctMoveBookmark, CTSmartTagRunImpl.MOVETORANGESTART$24, n, (short)2);
    }
    
    public CTMoveBookmark insertNewMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().insert_element_user(CTSmartTagRunImpl.MOVETORANGESTART$24, n);
        }
    }
    
    public CTMoveBookmark addNewMoveToRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().add_element_user(CTSmartTagRunImpl.MOVETORANGESTART$24);
        }
    }
    
    public void removeMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.MOVETORANGESTART$24, n);
        }
    }
    
    public List<CTMarkupRange> getMoveToRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTSmartTagRunImpl.this.getMoveToRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange moveToRangeEndArray = CTSmartTagRunImpl.this.getMoveToRangeEndArray(n);
                    CTSmartTagRunImpl.this.setMoveToRangeEndArray(n, ctMarkupRange);
                    return moveToRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTSmartTagRunImpl.this.insertNewMoveToRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange moveToRangeEndArray = CTSmartTagRunImpl.this.getMoveToRangeEndArray(n);
                    CTSmartTagRunImpl.this.removeMoveToRangeEnd(n);
                    return moveToRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfMoveToRangeEndArray();
                }
            }
            return new MoveToRangeEndList();
        }
    }
    
    @Deprecated
    public CTMarkupRange[] getMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.MOVETORANGEEND$26, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getMoveToRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTSmartTagRunImpl.MOVETORANGEEND$26, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.MOVETORANGEEND$26);
        }
    }
    
    public void setMoveToRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.MOVETORANGEEND$26);
    }
    
    public void setMoveToRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTSmartTagRunImpl.MOVETORANGEEND$26, n, (short)2);
    }
    
    public CTMarkupRange insertNewMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTSmartTagRunImpl.MOVETORANGEEND$26, n);
        }
    }
    
    public CTMarkupRange addNewMoveToRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTSmartTagRunImpl.MOVETORANGEEND$26);
        }
    }
    
    public void removeMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.MOVETORANGEEND$26, n);
        }
    }
    
    public List<CTMarkupRange> getCommentRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentRangeStartList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTSmartTagRunImpl.this.getCommentRangeStartArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange commentRangeStartArray = CTSmartTagRunImpl.this.getCommentRangeStartArray(n);
                    CTSmartTagRunImpl.this.setCommentRangeStartArray(n, ctMarkupRange);
                    return commentRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTSmartTagRunImpl.this.insertNewCommentRangeStart(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange commentRangeStartArray = CTSmartTagRunImpl.this.getCommentRangeStartArray(n);
                    CTSmartTagRunImpl.this.removeCommentRangeStart(n);
                    return commentRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfCommentRangeStartArray();
                }
            }
            return new CommentRangeStartList();
        }
    }
    
    @Deprecated
    public CTMarkupRange[] getCommentRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.COMMENTRANGESTART$28, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getCommentRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTSmartTagRunImpl.COMMENTRANGESTART$28, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfCommentRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.COMMENTRANGESTART$28);
        }
    }
    
    public void setCommentRangeStartArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.COMMENTRANGESTART$28);
    }
    
    public void setCommentRangeStartArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTSmartTagRunImpl.COMMENTRANGESTART$28, n, (short)2);
    }
    
    public CTMarkupRange insertNewCommentRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTSmartTagRunImpl.COMMENTRANGESTART$28, n);
        }
    }
    
    public CTMarkupRange addNewCommentRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTSmartTagRunImpl.COMMENTRANGESTART$28);
        }
    }
    
    public void removeCommentRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.COMMENTRANGESTART$28, n);
        }
    }
    
    public List<CTMarkupRange> getCommentRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTSmartTagRunImpl.this.getCommentRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange commentRangeEndArray = CTSmartTagRunImpl.this.getCommentRangeEndArray(n);
                    CTSmartTagRunImpl.this.setCommentRangeEndArray(n, ctMarkupRange);
                    return commentRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTSmartTagRunImpl.this.insertNewCommentRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange commentRangeEndArray = CTSmartTagRunImpl.this.getCommentRangeEndArray(n);
                    CTSmartTagRunImpl.this.removeCommentRangeEnd(n);
                    return commentRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfCommentRangeEndArray();
                }
            }
            return new CommentRangeEndList();
        }
    }
    
    @Deprecated
    public CTMarkupRange[] getCommentRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.COMMENTRANGEEND$30, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getCommentRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTSmartTagRunImpl.COMMENTRANGEEND$30, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfCommentRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.COMMENTRANGEEND$30);
        }
    }
    
    public void setCommentRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.COMMENTRANGEEND$30);
    }
    
    public void setCommentRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTSmartTagRunImpl.COMMENTRANGEEND$30, n, (short)2);
    }
    
    public CTMarkupRange insertNewCommentRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTSmartTagRunImpl.COMMENTRANGEEND$30, n);
        }
    }
    
    public CTMarkupRange addNewCommentRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTSmartTagRunImpl.COMMENTRANGEEND$30);
        }
    }
    
    public void removeCommentRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.COMMENTRANGEEND$30, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlInsRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlInsRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTSmartTagRunImpl.this.getCustomXmlInsRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlInsRangeStartArray = CTSmartTagRunImpl.this.getCustomXmlInsRangeStartArray(n);
                    CTSmartTagRunImpl.this.setCustomXmlInsRangeStartArray(n, ctTrackChange);
                    return customXmlInsRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTSmartTagRunImpl.this.insertNewCustomXmlInsRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlInsRangeStartArray = CTSmartTagRunImpl.this.getCustomXmlInsRangeStartArray(n);
                    CTSmartTagRunImpl.this.removeCustomXmlInsRangeStart(n);
                    return customXmlInsRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfCustomXmlInsRangeStartArray();
                }
            }
            return new CustomXmlInsRangeStartList();
        }
    }
    
    @Deprecated
    public CTTrackChange[] getCustomXmlInsRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.CUSTOMXMLINSRANGESTART$32, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlInsRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTSmartTagRunImpl.CUSTOMXMLINSRANGESTART$32, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlInsRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.CUSTOMXMLINSRANGESTART$32);
        }
    }
    
    public void setCustomXmlInsRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.CUSTOMXMLINSRANGESTART$32);
    }
    
    public void setCustomXmlInsRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTSmartTagRunImpl.CUSTOMXMLINSRANGESTART$32, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlInsRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTSmartTagRunImpl.CUSTOMXMLINSRANGESTART$32, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlInsRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTSmartTagRunImpl.CUSTOMXMLINSRANGESTART$32);
        }
    }
    
    public void removeCustomXmlInsRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.CUSTOMXMLINSRANGESTART$32, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlInsRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlInsRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTSmartTagRunImpl.this.getCustomXmlInsRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlInsRangeEndArray = CTSmartTagRunImpl.this.getCustomXmlInsRangeEndArray(n);
                    CTSmartTagRunImpl.this.setCustomXmlInsRangeEndArray(n, ctMarkup);
                    return customXmlInsRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTSmartTagRunImpl.this.insertNewCustomXmlInsRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlInsRangeEndArray = CTSmartTagRunImpl.this.getCustomXmlInsRangeEndArray(n);
                    CTSmartTagRunImpl.this.removeCustomXmlInsRangeEnd(n);
                    return customXmlInsRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfCustomXmlInsRangeEndArray();
                }
            }
            return new CustomXmlInsRangeEndList();
        }
    }
    
    @Deprecated
    public CTMarkup[] getCustomXmlInsRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.CUSTOMXMLINSRANGEEND$34, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlInsRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTSmartTagRunImpl.CUSTOMXMLINSRANGEEND$34, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlInsRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.CUSTOMXMLINSRANGEEND$34);
        }
    }
    
    public void setCustomXmlInsRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.CUSTOMXMLINSRANGEEND$34);
    }
    
    public void setCustomXmlInsRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTSmartTagRunImpl.CUSTOMXMLINSRANGEEND$34, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlInsRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTSmartTagRunImpl.CUSTOMXMLINSRANGEEND$34, n);
        }
    }
    
    public CTMarkup addNewCustomXmlInsRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTSmartTagRunImpl.CUSTOMXMLINSRANGEEND$34);
        }
    }
    
    public void removeCustomXmlInsRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.CUSTOMXMLINSRANGEEND$34, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlDelRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlDelRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTSmartTagRunImpl.this.getCustomXmlDelRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlDelRangeStartArray = CTSmartTagRunImpl.this.getCustomXmlDelRangeStartArray(n);
                    CTSmartTagRunImpl.this.setCustomXmlDelRangeStartArray(n, ctTrackChange);
                    return customXmlDelRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTSmartTagRunImpl.this.insertNewCustomXmlDelRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlDelRangeStartArray = CTSmartTagRunImpl.this.getCustomXmlDelRangeStartArray(n);
                    CTSmartTagRunImpl.this.removeCustomXmlDelRangeStart(n);
                    return customXmlDelRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfCustomXmlDelRangeStartArray();
                }
            }
            return new CustomXmlDelRangeStartList();
        }
    }
    
    @Deprecated
    public CTTrackChange[] getCustomXmlDelRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.CUSTOMXMLDELRANGESTART$36, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlDelRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTSmartTagRunImpl.CUSTOMXMLDELRANGESTART$36, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlDelRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.CUSTOMXMLDELRANGESTART$36);
        }
    }
    
    public void setCustomXmlDelRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.CUSTOMXMLDELRANGESTART$36);
    }
    
    public void setCustomXmlDelRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTSmartTagRunImpl.CUSTOMXMLDELRANGESTART$36, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlDelRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTSmartTagRunImpl.CUSTOMXMLDELRANGESTART$36, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlDelRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTSmartTagRunImpl.CUSTOMXMLDELRANGESTART$36);
        }
    }
    
    public void removeCustomXmlDelRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.CUSTOMXMLDELRANGESTART$36, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlDelRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlDelRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTSmartTagRunImpl.this.getCustomXmlDelRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlDelRangeEndArray = CTSmartTagRunImpl.this.getCustomXmlDelRangeEndArray(n);
                    CTSmartTagRunImpl.this.setCustomXmlDelRangeEndArray(n, ctMarkup);
                    return customXmlDelRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTSmartTagRunImpl.this.insertNewCustomXmlDelRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlDelRangeEndArray = CTSmartTagRunImpl.this.getCustomXmlDelRangeEndArray(n);
                    CTSmartTagRunImpl.this.removeCustomXmlDelRangeEnd(n);
                    return customXmlDelRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfCustomXmlDelRangeEndArray();
                }
            }
            return new CustomXmlDelRangeEndList();
        }
    }
    
    @Deprecated
    public CTMarkup[] getCustomXmlDelRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.CUSTOMXMLDELRANGEEND$38, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlDelRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTSmartTagRunImpl.CUSTOMXMLDELRANGEEND$38, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlDelRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.CUSTOMXMLDELRANGEEND$38);
        }
    }
    
    public void setCustomXmlDelRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.CUSTOMXMLDELRANGEEND$38);
    }
    
    public void setCustomXmlDelRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTSmartTagRunImpl.CUSTOMXMLDELRANGEEND$38, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlDelRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTSmartTagRunImpl.CUSTOMXMLDELRANGEEND$38, n);
        }
    }
    
    public CTMarkup addNewCustomXmlDelRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTSmartTagRunImpl.CUSTOMXMLDELRANGEEND$38);
        }
    }
    
    public void removeCustomXmlDelRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.CUSTOMXMLDELRANGEEND$38, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlMoveFromRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveFromRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTSmartTagRunImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlMoveFromRangeStartArray = CTSmartTagRunImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                    CTSmartTagRunImpl.this.setCustomXmlMoveFromRangeStartArray(n, ctTrackChange);
                    return customXmlMoveFromRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTSmartTagRunImpl.this.insertNewCustomXmlMoveFromRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlMoveFromRangeStartArray = CTSmartTagRunImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                    CTSmartTagRunImpl.this.removeCustomXmlMoveFromRangeStart(n);
                    return customXmlMoveFromRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfCustomXmlMoveFromRangeStartArray();
                }
            }
            return new CustomXmlMoveFromRangeStartList();
        }
    }
    
    @Deprecated
    public CTTrackChange[] getCustomXmlMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.CUSTOMXMLMOVEFROMRANGESTART$40, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlMoveFromRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTSmartTagRunImpl.CUSTOMXMLMOVEFROMRANGESTART$40, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.CUSTOMXMLMOVEFROMRANGESTART$40);
        }
    }
    
    public void setCustomXmlMoveFromRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.CUSTOMXMLMOVEFROMRANGESTART$40);
    }
    
    public void setCustomXmlMoveFromRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTSmartTagRunImpl.CUSTOMXMLMOVEFROMRANGESTART$40, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTSmartTagRunImpl.CUSTOMXMLMOVEFROMRANGESTART$40, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlMoveFromRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTSmartTagRunImpl.CUSTOMXMLMOVEFROMRANGESTART$40);
        }
    }
    
    public void removeCustomXmlMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.CUSTOMXMLMOVEFROMRANGESTART$40, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlMoveFromRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveFromRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTSmartTagRunImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlMoveFromRangeEndArray = CTSmartTagRunImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                    CTSmartTagRunImpl.this.setCustomXmlMoveFromRangeEndArray(n, ctMarkup);
                    return customXmlMoveFromRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTSmartTagRunImpl.this.insertNewCustomXmlMoveFromRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlMoveFromRangeEndArray = CTSmartTagRunImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                    CTSmartTagRunImpl.this.removeCustomXmlMoveFromRangeEnd(n);
                    return customXmlMoveFromRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfCustomXmlMoveFromRangeEndArray();
                }
            }
            return new CustomXmlMoveFromRangeEndList();
        }
    }
    
    @Deprecated
    public CTMarkup[] getCustomXmlMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.CUSTOMXMLMOVEFROMRANGEEND$42, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlMoveFromRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTSmartTagRunImpl.CUSTOMXMLMOVEFROMRANGEEND$42, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.CUSTOMXMLMOVEFROMRANGEEND$42);
        }
    }
    
    public void setCustomXmlMoveFromRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.CUSTOMXMLMOVEFROMRANGEEND$42);
    }
    
    public void setCustomXmlMoveFromRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTSmartTagRunImpl.CUSTOMXMLMOVEFROMRANGEEND$42, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTSmartTagRunImpl.CUSTOMXMLMOVEFROMRANGEEND$42, n);
        }
    }
    
    public CTMarkup addNewCustomXmlMoveFromRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTSmartTagRunImpl.CUSTOMXMLMOVEFROMRANGEEND$42);
        }
    }
    
    public void removeCustomXmlMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.CUSTOMXMLMOVEFROMRANGEEND$42, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlMoveToRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveToRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTSmartTagRunImpl.this.getCustomXmlMoveToRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlMoveToRangeStartArray = CTSmartTagRunImpl.this.getCustomXmlMoveToRangeStartArray(n);
                    CTSmartTagRunImpl.this.setCustomXmlMoveToRangeStartArray(n, ctTrackChange);
                    return customXmlMoveToRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTSmartTagRunImpl.this.insertNewCustomXmlMoveToRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlMoveToRangeStartArray = CTSmartTagRunImpl.this.getCustomXmlMoveToRangeStartArray(n);
                    CTSmartTagRunImpl.this.removeCustomXmlMoveToRangeStart(n);
                    return customXmlMoveToRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfCustomXmlMoveToRangeStartArray();
                }
            }
            return new CustomXmlMoveToRangeStartList();
        }
    }
    
    @Deprecated
    public CTTrackChange[] getCustomXmlMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.CUSTOMXMLMOVETORANGESTART$44, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlMoveToRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTSmartTagRunImpl.CUSTOMXMLMOVETORANGESTART$44, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.CUSTOMXMLMOVETORANGESTART$44);
        }
    }
    
    public void setCustomXmlMoveToRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.CUSTOMXMLMOVETORANGESTART$44);
    }
    
    public void setCustomXmlMoveToRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTSmartTagRunImpl.CUSTOMXMLMOVETORANGESTART$44, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTSmartTagRunImpl.CUSTOMXMLMOVETORANGESTART$44, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlMoveToRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTSmartTagRunImpl.CUSTOMXMLMOVETORANGESTART$44);
        }
    }
    
    public void removeCustomXmlMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.CUSTOMXMLMOVETORANGESTART$44, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlMoveToRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveToRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTSmartTagRunImpl.this.getCustomXmlMoveToRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlMoveToRangeEndArray = CTSmartTagRunImpl.this.getCustomXmlMoveToRangeEndArray(n);
                    CTSmartTagRunImpl.this.setCustomXmlMoveToRangeEndArray(n, ctMarkup);
                    return customXmlMoveToRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTSmartTagRunImpl.this.insertNewCustomXmlMoveToRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlMoveToRangeEndArray = CTSmartTagRunImpl.this.getCustomXmlMoveToRangeEndArray(n);
                    CTSmartTagRunImpl.this.removeCustomXmlMoveToRangeEnd(n);
                    return customXmlMoveToRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfCustomXmlMoveToRangeEndArray();
                }
            }
            return new CustomXmlMoveToRangeEndList();
        }
    }
    
    @Deprecated
    public CTMarkup[] getCustomXmlMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.CUSTOMXMLMOVETORANGEEND$46, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlMoveToRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTSmartTagRunImpl.CUSTOMXMLMOVETORANGEEND$46, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.CUSTOMXMLMOVETORANGEEND$46);
        }
    }
    
    public void setCustomXmlMoveToRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.CUSTOMXMLMOVETORANGEEND$46);
    }
    
    public void setCustomXmlMoveToRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTSmartTagRunImpl.CUSTOMXMLMOVETORANGEEND$46, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTSmartTagRunImpl.CUSTOMXMLMOVETORANGEEND$46, n);
        }
    }
    
    public CTMarkup addNewCustomXmlMoveToRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTSmartTagRunImpl.CUSTOMXMLMOVETORANGEEND$46);
        }
    }
    
    public void removeCustomXmlMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.CUSTOMXMLMOVETORANGEEND$46, n);
        }
    }
    
    public List<CTRunTrackChange> getInsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class InsList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTSmartTagRunImpl.this.getInsArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange insArray = CTSmartTagRunImpl.this.getInsArray(n);
                    CTSmartTagRunImpl.this.setInsArray(n, ctRunTrackChange);
                    return insArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTSmartTagRunImpl.this.insertNewIns(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange insArray = CTSmartTagRunImpl.this.getInsArray(n);
                    CTSmartTagRunImpl.this.removeIns(n);
                    return insArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfInsArray();
                }
            }
            return new InsList();
        }
    }
    
    @Deprecated
    public CTRunTrackChange[] getInsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.INS$48, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getInsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTSmartTagRunImpl.INS$48, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfInsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.INS$48);
        }
    }
    
    public void setInsArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.INS$48);
    }
    
    public void setInsArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTSmartTagRunImpl.INS$48, n, (short)2);
    }
    
    public CTRunTrackChange insertNewIns(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTSmartTagRunImpl.INS$48, n);
        }
    }
    
    public CTRunTrackChange addNewIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTSmartTagRunImpl.INS$48);
        }
    }
    
    public void removeIns(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.INS$48, n);
        }
    }
    
    public List<CTRunTrackChange> getDelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DelList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTSmartTagRunImpl.this.getDelArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange delArray = CTSmartTagRunImpl.this.getDelArray(n);
                    CTSmartTagRunImpl.this.setDelArray(n, ctRunTrackChange);
                    return delArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTSmartTagRunImpl.this.insertNewDel(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange delArray = CTSmartTagRunImpl.this.getDelArray(n);
                    CTSmartTagRunImpl.this.removeDel(n);
                    return delArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfDelArray();
                }
            }
            return new DelList();
        }
    }
    
    @Deprecated
    public CTRunTrackChange[] getDelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.DEL$50, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getDelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTSmartTagRunImpl.DEL$50, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfDelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.DEL$50);
        }
    }
    
    public void setDelArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.DEL$50);
    }
    
    public void setDelArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTSmartTagRunImpl.DEL$50, n, (short)2);
    }
    
    public CTRunTrackChange insertNewDel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTSmartTagRunImpl.DEL$50, n);
        }
    }
    
    public CTRunTrackChange addNewDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTSmartTagRunImpl.DEL$50);
        }
    }
    
    public void removeDel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.DEL$50, n);
        }
    }
    
    public List<CTRunTrackChange> getMoveFromList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTSmartTagRunImpl.this.getMoveFromArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange moveFromArray = CTSmartTagRunImpl.this.getMoveFromArray(n);
                    CTSmartTagRunImpl.this.setMoveFromArray(n, ctRunTrackChange);
                    return moveFromArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTSmartTagRunImpl.this.insertNewMoveFrom(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange moveFromArray = CTSmartTagRunImpl.this.getMoveFromArray(n);
                    CTSmartTagRunImpl.this.removeMoveFrom(n);
                    return moveFromArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfMoveFromArray();
                }
            }
            return new MoveFromList();
        }
    }
    
    @Deprecated
    public CTRunTrackChange[] getMoveFromArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.MOVEFROM$52, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getMoveFromArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTSmartTagRunImpl.MOVEFROM$52, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfMoveFromArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.MOVEFROM$52);
        }
    }
    
    public void setMoveFromArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.MOVEFROM$52);
    }
    
    public void setMoveFromArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTSmartTagRunImpl.MOVEFROM$52, n, (short)2);
    }
    
    public CTRunTrackChange insertNewMoveFrom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTSmartTagRunImpl.MOVEFROM$52, n);
        }
    }
    
    public CTRunTrackChange addNewMoveFrom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTSmartTagRunImpl.MOVEFROM$52);
        }
    }
    
    public void removeMoveFrom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.MOVEFROM$52, n);
        }
    }
    
    public List<CTRunTrackChange> getMoveToList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTSmartTagRunImpl.this.getMoveToArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange moveToArray = CTSmartTagRunImpl.this.getMoveToArray(n);
                    CTSmartTagRunImpl.this.setMoveToArray(n, ctRunTrackChange);
                    return moveToArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTSmartTagRunImpl.this.insertNewMoveTo(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange moveToArray = CTSmartTagRunImpl.this.getMoveToArray(n);
                    CTSmartTagRunImpl.this.removeMoveTo(n);
                    return moveToArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfMoveToArray();
                }
            }
            return new MoveToList();
        }
    }
    
    @Deprecated
    public CTRunTrackChange[] getMoveToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.MOVETO$54, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getMoveToArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTSmartTagRunImpl.MOVETO$54, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfMoveToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.MOVETO$54);
        }
    }
    
    public void setMoveToArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.MOVETO$54);
    }
    
    public void setMoveToArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTSmartTagRunImpl.MOVETO$54, n, (short)2);
    }
    
    public CTRunTrackChange insertNewMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTSmartTagRunImpl.MOVETO$54, n);
        }
    }
    
    public CTRunTrackChange addNewMoveTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTSmartTagRunImpl.MOVETO$54);
        }
    }
    
    public void removeMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.MOVETO$54, n);
        }
    }
    
    public List<CTOMathPara> getOMathParaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OMathParaList extends AbstractList<CTOMathPara>
            {
                @Override
                public CTOMathPara get(final int n) {
                    return CTSmartTagRunImpl.this.getOMathParaArray(n);
                }
                
                @Override
                public CTOMathPara set(final int n, final CTOMathPara ctoMathPara) {
                    final CTOMathPara oMathParaArray = CTSmartTagRunImpl.this.getOMathParaArray(n);
                    CTSmartTagRunImpl.this.setOMathParaArray(n, ctoMathPara);
                    return oMathParaArray;
                }
                
                @Override
                public void add(final int n, final CTOMathPara ctoMathPara) {
                    CTSmartTagRunImpl.this.insertNewOMathPara(n).set((XmlObject)ctoMathPara);
                }
                
                @Override
                public CTOMathPara remove(final int n) {
                    final CTOMathPara oMathParaArray = CTSmartTagRunImpl.this.getOMathParaArray(n);
                    CTSmartTagRunImpl.this.removeOMathPara(n);
                    return oMathParaArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfOMathParaArray();
                }
            }
            return new OMathParaList();
        }
    }
    
    @Deprecated
    public CTOMathPara[] getOMathParaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.OMATHPARA$56, (List)list);
            final CTOMathPara[] array = new CTOMathPara[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOMathPara getOMathParaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOMathPara ctoMathPara = (CTOMathPara)this.get_store().find_element_user(CTSmartTagRunImpl.OMATHPARA$56, n);
            if (ctoMathPara == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctoMathPara;
        }
    }
    
    public int sizeOfOMathParaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.OMATHPARA$56);
        }
    }
    
    public void setOMathParaArray(final CTOMathPara[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.OMATHPARA$56);
    }
    
    public void setOMathParaArray(final int n, final CTOMathPara ctoMathPara) {
        this.generatedSetterHelperImpl((XmlObject)ctoMathPara, CTSmartTagRunImpl.OMATHPARA$56, n, (short)2);
    }
    
    public CTOMathPara insertNewOMathPara(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMathPara)this.get_store().insert_element_user(CTSmartTagRunImpl.OMATHPARA$56, n);
        }
    }
    
    public CTOMathPara addNewOMathPara() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMathPara)this.get_store().add_element_user(CTSmartTagRunImpl.OMATHPARA$56);
        }
    }
    
    public void removeOMathPara(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.OMATHPARA$56, n);
        }
    }
    
    public List<CTOMath> getOMathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OMathList extends AbstractList<CTOMath>
            {
                @Override
                public CTOMath get(final int n) {
                    return CTSmartTagRunImpl.this.getOMathArray(n);
                }
                
                @Override
                public CTOMath set(final int n, final CTOMath ctoMath) {
                    final CTOMath oMathArray = CTSmartTagRunImpl.this.getOMathArray(n);
                    CTSmartTagRunImpl.this.setOMathArray(n, ctoMath);
                    return oMathArray;
                }
                
                @Override
                public void add(final int n, final CTOMath ctoMath) {
                    CTSmartTagRunImpl.this.insertNewOMath(n).set((XmlObject)ctoMath);
                }
                
                @Override
                public CTOMath remove(final int n) {
                    final CTOMath oMathArray = CTSmartTagRunImpl.this.getOMathArray(n);
                    CTSmartTagRunImpl.this.removeOMath(n);
                    return oMathArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfOMathArray();
                }
            }
            return new OMathList();
        }
    }
    
    @Deprecated
    public CTOMath[] getOMathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.OMATH$58, (List)list);
            final CTOMath[] array = new CTOMath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOMath getOMathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOMath ctoMath = (CTOMath)this.get_store().find_element_user(CTSmartTagRunImpl.OMATH$58, n);
            if (ctoMath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctoMath;
        }
    }
    
    public int sizeOfOMathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.OMATH$58);
        }
    }
    
    public void setOMathArray(final CTOMath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.OMATH$58);
    }
    
    public void setOMathArray(final int n, final CTOMath ctoMath) {
        this.generatedSetterHelperImpl((XmlObject)ctoMath, CTSmartTagRunImpl.OMATH$58, n, (short)2);
    }
    
    public CTOMath insertNewOMath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMath)this.get_store().insert_element_user(CTSmartTagRunImpl.OMATH$58, n);
        }
    }
    
    public CTOMath addNewOMath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMath)this.get_store().add_element_user(CTSmartTagRunImpl.OMATH$58);
        }
    }
    
    public void removeOMath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.OMATH$58, n);
        }
    }
    
    public List<CTSimpleField> getFldSimpleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FldSimpleList extends AbstractList<CTSimpleField>
            {
                @Override
                public CTSimpleField get(final int n) {
                    return CTSmartTagRunImpl.this.getFldSimpleArray(n);
                }
                
                @Override
                public CTSimpleField set(final int n, final CTSimpleField ctSimpleField) {
                    final CTSimpleField fldSimpleArray = CTSmartTagRunImpl.this.getFldSimpleArray(n);
                    CTSmartTagRunImpl.this.setFldSimpleArray(n, ctSimpleField);
                    return fldSimpleArray;
                }
                
                @Override
                public void add(final int n, final CTSimpleField ctSimpleField) {
                    CTSmartTagRunImpl.this.insertNewFldSimple(n).set((XmlObject)ctSimpleField);
                }
                
                @Override
                public CTSimpleField remove(final int n) {
                    final CTSimpleField fldSimpleArray = CTSmartTagRunImpl.this.getFldSimpleArray(n);
                    CTSmartTagRunImpl.this.removeFldSimple(n);
                    return fldSimpleArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfFldSimpleArray();
                }
            }
            return new FldSimpleList();
        }
    }
    
    @Deprecated
    public CTSimpleField[] getFldSimpleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.FLDSIMPLE$60, (List)list);
            final CTSimpleField[] array = new CTSimpleField[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSimpleField getFldSimpleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSimpleField ctSimpleField = (CTSimpleField)this.get_store().find_element_user(CTSmartTagRunImpl.FLDSIMPLE$60, n);
            if (ctSimpleField == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSimpleField;
        }
    }
    
    public int sizeOfFldSimpleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.FLDSIMPLE$60);
        }
    }
    
    public void setFldSimpleArray(final CTSimpleField[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.FLDSIMPLE$60);
    }
    
    public void setFldSimpleArray(final int n, final CTSimpleField ctSimpleField) {
        this.generatedSetterHelperImpl((XmlObject)ctSimpleField, CTSmartTagRunImpl.FLDSIMPLE$60, n, (short)2);
    }
    
    public CTSimpleField insertNewFldSimple(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSimpleField)this.get_store().insert_element_user(CTSmartTagRunImpl.FLDSIMPLE$60, n);
        }
    }
    
    public CTSimpleField addNewFldSimple() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSimpleField)this.get_store().add_element_user(CTSmartTagRunImpl.FLDSIMPLE$60);
        }
    }
    
    public void removeFldSimple(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.FLDSIMPLE$60, n);
        }
    }
    
    public List<CTHyperlink> getHyperlinkList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HyperlinkList extends AbstractList<CTHyperlink>
            {
                @Override
                public CTHyperlink get(final int n) {
                    return CTSmartTagRunImpl.this.getHyperlinkArray(n);
                }
                
                @Override
                public CTHyperlink set(final int n, final CTHyperlink ctHyperlink) {
                    final CTHyperlink hyperlinkArray = CTSmartTagRunImpl.this.getHyperlinkArray(n);
                    CTSmartTagRunImpl.this.setHyperlinkArray(n, ctHyperlink);
                    return hyperlinkArray;
                }
                
                @Override
                public void add(final int n, final CTHyperlink ctHyperlink) {
                    CTSmartTagRunImpl.this.insertNewHyperlink(n).set((XmlObject)ctHyperlink);
                }
                
                @Override
                public CTHyperlink remove(final int n) {
                    final CTHyperlink hyperlinkArray = CTSmartTagRunImpl.this.getHyperlinkArray(n);
                    CTSmartTagRunImpl.this.removeHyperlink(n);
                    return hyperlinkArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfHyperlinkArray();
                }
            }
            return new HyperlinkList();
        }
    }
    
    @Deprecated
    public CTHyperlink[] getHyperlinkArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.HYPERLINK$62, (List)list);
            final CTHyperlink[] array = new CTHyperlink[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTHyperlink getHyperlinkArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHyperlink ctHyperlink = (CTHyperlink)this.get_store().find_element_user(CTSmartTagRunImpl.HYPERLINK$62, n);
            if (ctHyperlink == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctHyperlink;
        }
    }
    
    public int sizeOfHyperlinkArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.HYPERLINK$62);
        }
    }
    
    public void setHyperlinkArray(final CTHyperlink[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.HYPERLINK$62);
    }
    
    public void setHyperlinkArray(final int n, final CTHyperlink ctHyperlink) {
        this.generatedSetterHelperImpl((XmlObject)ctHyperlink, CTSmartTagRunImpl.HYPERLINK$62, n, (short)2);
    }
    
    public CTHyperlink insertNewHyperlink(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHyperlink)this.get_store().insert_element_user(CTSmartTagRunImpl.HYPERLINK$62, n);
        }
    }
    
    public CTHyperlink addNewHyperlink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHyperlink)this.get_store().add_element_user(CTSmartTagRunImpl.HYPERLINK$62);
        }
    }
    
    public void removeHyperlink(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.HYPERLINK$62, n);
        }
    }
    
    public List<CTRel> getSubDocList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SubDocList extends AbstractList<CTRel>
            {
                @Override
                public CTRel get(final int n) {
                    return CTSmartTagRunImpl.this.getSubDocArray(n);
                }
                
                @Override
                public CTRel set(final int n, final CTRel ctRel) {
                    final CTRel subDocArray = CTSmartTagRunImpl.this.getSubDocArray(n);
                    CTSmartTagRunImpl.this.setSubDocArray(n, ctRel);
                    return subDocArray;
                }
                
                @Override
                public void add(final int n, final CTRel ctRel) {
                    CTSmartTagRunImpl.this.insertNewSubDoc(n).set((XmlObject)ctRel);
                }
                
                @Override
                public CTRel remove(final int n) {
                    final CTRel subDocArray = CTSmartTagRunImpl.this.getSubDocArray(n);
                    CTSmartTagRunImpl.this.removeSubDoc(n);
                    return subDocArray;
                }
                
                @Override
                public int size() {
                    return CTSmartTagRunImpl.this.sizeOfSubDocArray();
                }
            }
            return new SubDocList();
        }
    }
    
    @Deprecated
    public CTRel[] getSubDocArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSmartTagRunImpl.SUBDOC$64, (List)list);
            final CTRel[] array = new CTRel[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRel getSubDocArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRel ctRel = (CTRel)this.get_store().find_element_user(CTSmartTagRunImpl.SUBDOC$64, n);
            if (ctRel == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRel;
        }
    }
    
    public int sizeOfSubDocArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSmartTagRunImpl.SUBDOC$64);
        }
    }
    
    public void setSubDocArray(final CTRel[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSmartTagRunImpl.SUBDOC$64);
    }
    
    public void setSubDocArray(final int n, final CTRel ctRel) {
        this.generatedSetterHelperImpl((XmlObject)ctRel, CTSmartTagRunImpl.SUBDOC$64, n, (short)2);
    }
    
    public CTRel insertNewSubDoc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRel)this.get_store().insert_element_user(CTSmartTagRunImpl.SUBDOC$64, n);
        }
    }
    
    public CTRel addNewSubDoc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRel)this.get_store().add_element_user(CTSmartTagRunImpl.SUBDOC$64);
        }
    }
    
    public void removeSubDoc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSmartTagRunImpl.SUBDOC$64, n);
        }
    }
    
    public String getUri() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSmartTagRunImpl.URI$66);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetUri() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTSmartTagRunImpl.URI$66);
        }
    }
    
    public boolean isSetUri() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSmartTagRunImpl.URI$66) != null;
        }
    }
    
    public void setUri(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSmartTagRunImpl.URI$66);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSmartTagRunImpl.URI$66);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetUri(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTSmartTagRunImpl.URI$66);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTSmartTagRunImpl.URI$66);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public void unsetUri() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSmartTagRunImpl.URI$66);
        }
    }
    
    public String getElement() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSmartTagRunImpl.ELEMENT$68);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetElement() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTSmartTagRunImpl.ELEMENT$68);
        }
    }
    
    public void setElement(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSmartTagRunImpl.ELEMENT$68);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSmartTagRunImpl.ELEMENT$68);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetElement(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTSmartTagRunImpl.ELEMENT$68);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTSmartTagRunImpl.ELEMENT$68);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    static {
        SMARTTAGPR$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "smartTagPr");
        CUSTOMXML$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXml");
        SMARTTAG$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "smartTag");
        SDT$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sdt");
        R$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "r");
        PROOFERR$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "proofErr");
        PERMSTART$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "permStart");
        PERMEND$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "permEnd");
        BOOKMARKSTART$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookmarkStart");
        BOOKMARKEND$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookmarkEnd");
        MOVEFROMRANGESTART$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFromRangeStart");
        MOVEFROMRANGEEND$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFromRangeEnd");
        MOVETORANGESTART$24 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveToRangeStart");
        MOVETORANGEEND$26 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveToRangeEnd");
        COMMENTRANGESTART$28 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "commentRangeStart");
        COMMENTRANGEEND$30 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "commentRangeEnd");
        CUSTOMXMLINSRANGESTART$32 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlInsRangeStart");
        CUSTOMXMLINSRANGEEND$34 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlInsRangeEnd");
        CUSTOMXMLDELRANGESTART$36 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlDelRangeStart");
        CUSTOMXMLDELRANGEEND$38 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlDelRangeEnd");
        CUSTOMXMLMOVEFROMRANGESTART$40 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveFromRangeStart");
        CUSTOMXMLMOVEFROMRANGEEND$42 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveFromRangeEnd");
        CUSTOMXMLMOVETORANGESTART$44 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveToRangeStart");
        CUSTOMXMLMOVETORANGEEND$46 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveToRangeEnd");
        INS$48 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ins");
        DEL$50 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "del");
        MOVEFROM$52 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFrom");
        MOVETO$54 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveTo");
        OMATHPARA$56 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "oMathPara");
        OMATH$58 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "oMath");
        FLDSIMPLE$60 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "fldSimple");
        HYPERLINK$62 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hyperlink");
        SUBDOC$64 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "subDoc");
        URI$66 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "uri");
        ELEMENT$68 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "element");
    }
}
