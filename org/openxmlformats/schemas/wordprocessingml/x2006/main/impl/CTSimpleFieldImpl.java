package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STString;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRel;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSmartTagRun;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCustomXmlRun;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSimpleFieldImpl extends XmlComplexContentImpl implements CTSimpleField
{
    private static final long serialVersionUID = 1L;
    private static final QName FLDDATA$0;
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
    private static final QName INSTR$66;
    private static final QName FLDLOCK$68;
    private static final QName DIRTY$70;
    
    public CTSimpleFieldImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTText getFldData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTText ctText = (CTText)this.get_store().find_element_user(CTSimpleFieldImpl.FLDDATA$0, 0);
            if (ctText == null) {
                return null;
            }
            return ctText;
        }
    }
    
    public boolean isSetFldData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.FLDDATA$0) != 0;
        }
    }
    
    public void setFldData(final CTText ctText) {
        this.generatedSetterHelperImpl((XmlObject)ctText, CTSimpleFieldImpl.FLDDATA$0, 0, (short)1);
    }
    
    public CTText addNewFldData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTText)this.get_store().add_element_user(CTSimpleFieldImpl.FLDDATA$0);
        }
    }
    
    public void unsetFldData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.FLDDATA$0, 0);
        }
    }
    
    public List<CTCustomXmlRun> getCustomXmlList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlList extends AbstractList<CTCustomXmlRun>
            {
                @Override
                public CTCustomXmlRun get(final int n) {
                    return CTSimpleFieldImpl.this.getCustomXmlArray(n);
                }
                
                @Override
                public CTCustomXmlRun set(final int n, final CTCustomXmlRun ctCustomXmlRun) {
                    final CTCustomXmlRun customXmlArray = CTSimpleFieldImpl.this.getCustomXmlArray(n);
                    CTSimpleFieldImpl.this.setCustomXmlArray(n, ctCustomXmlRun);
                    return customXmlArray;
                }
                
                @Override
                public void add(final int n, final CTCustomXmlRun ctCustomXmlRun) {
                    CTSimpleFieldImpl.this.insertNewCustomXml(n).set((XmlObject)ctCustomXmlRun);
                }
                
                @Override
                public CTCustomXmlRun remove(final int n) {
                    final CTCustomXmlRun customXmlArray = CTSimpleFieldImpl.this.getCustomXmlArray(n);
                    CTSimpleFieldImpl.this.removeCustomXml(n);
                    return customXmlArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfCustomXmlArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.CUSTOMXML$2, (List)list);
            final CTCustomXmlRun[] array = new CTCustomXmlRun[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCustomXmlRun getCustomXmlArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomXmlRun ctCustomXmlRun = (CTCustomXmlRun)this.get_store().find_element_user(CTSimpleFieldImpl.CUSTOMXML$2, n);
            if (ctCustomXmlRun == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCustomXmlRun;
        }
    }
    
    public int sizeOfCustomXmlArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.CUSTOMXML$2);
        }
    }
    
    public void setCustomXmlArray(final CTCustomXmlRun[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.CUSTOMXML$2);
    }
    
    public void setCustomXmlArray(final int n, final CTCustomXmlRun ctCustomXmlRun) {
        this.generatedSetterHelperImpl((XmlObject)ctCustomXmlRun, CTSimpleFieldImpl.CUSTOMXML$2, n, (short)2);
    }
    
    public CTCustomXmlRun insertNewCustomXml(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomXmlRun)this.get_store().insert_element_user(CTSimpleFieldImpl.CUSTOMXML$2, n);
        }
    }
    
    public CTCustomXmlRun addNewCustomXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomXmlRun)this.get_store().add_element_user(CTSimpleFieldImpl.CUSTOMXML$2);
        }
    }
    
    public void removeCustomXml(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.CUSTOMXML$2, n);
        }
    }
    
    public List<CTSmartTagRun> getSmartTagList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SmartTagList extends AbstractList<CTSmartTagRun>
            {
                @Override
                public CTSmartTagRun get(final int n) {
                    return CTSimpleFieldImpl.this.getSmartTagArray(n);
                }
                
                @Override
                public CTSmartTagRun set(final int n, final CTSmartTagRun ctSmartTagRun) {
                    final CTSmartTagRun smartTagArray = CTSimpleFieldImpl.this.getSmartTagArray(n);
                    CTSimpleFieldImpl.this.setSmartTagArray(n, ctSmartTagRun);
                    return smartTagArray;
                }
                
                @Override
                public void add(final int n, final CTSmartTagRun ctSmartTagRun) {
                    CTSimpleFieldImpl.this.insertNewSmartTag(n).set((XmlObject)ctSmartTagRun);
                }
                
                @Override
                public CTSmartTagRun remove(final int n) {
                    final CTSmartTagRun smartTagArray = CTSimpleFieldImpl.this.getSmartTagArray(n);
                    CTSimpleFieldImpl.this.removeSmartTag(n);
                    return smartTagArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfSmartTagArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.SMARTTAG$4, (List)list);
            final CTSmartTagRun[] array = new CTSmartTagRun[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSmartTagRun getSmartTagArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSmartTagRun ctSmartTagRun = (CTSmartTagRun)this.get_store().find_element_user(CTSimpleFieldImpl.SMARTTAG$4, n);
            if (ctSmartTagRun == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSmartTagRun;
        }
    }
    
    public int sizeOfSmartTagArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.SMARTTAG$4);
        }
    }
    
    public void setSmartTagArray(final CTSmartTagRun[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.SMARTTAG$4);
    }
    
    public void setSmartTagArray(final int n, final CTSmartTagRun ctSmartTagRun) {
        this.generatedSetterHelperImpl((XmlObject)ctSmartTagRun, CTSimpleFieldImpl.SMARTTAG$4, n, (short)2);
    }
    
    public CTSmartTagRun insertNewSmartTag(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSmartTagRun)this.get_store().insert_element_user(CTSimpleFieldImpl.SMARTTAG$4, n);
        }
    }
    
    public CTSmartTagRun addNewSmartTag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSmartTagRun)this.get_store().add_element_user(CTSimpleFieldImpl.SMARTTAG$4);
        }
    }
    
    public void removeSmartTag(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.SMARTTAG$4, n);
        }
    }
    
    public List<CTSdtRun> getSdtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SdtList extends AbstractList<CTSdtRun>
            {
                @Override
                public CTSdtRun get(final int n) {
                    return CTSimpleFieldImpl.this.getSdtArray(n);
                }
                
                @Override
                public CTSdtRun set(final int n, final CTSdtRun ctSdtRun) {
                    final CTSdtRun sdtArray = CTSimpleFieldImpl.this.getSdtArray(n);
                    CTSimpleFieldImpl.this.setSdtArray(n, ctSdtRun);
                    return sdtArray;
                }
                
                @Override
                public void add(final int n, final CTSdtRun ctSdtRun) {
                    CTSimpleFieldImpl.this.insertNewSdt(n).set((XmlObject)ctSdtRun);
                }
                
                @Override
                public CTSdtRun remove(final int n) {
                    final CTSdtRun sdtArray = CTSimpleFieldImpl.this.getSdtArray(n);
                    CTSimpleFieldImpl.this.removeSdt(n);
                    return sdtArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfSdtArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.SDT$6, (List)list);
            final CTSdtRun[] array = new CTSdtRun[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSdtRun getSdtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtRun ctSdtRun = (CTSdtRun)this.get_store().find_element_user(CTSimpleFieldImpl.SDT$6, n);
            if (ctSdtRun == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSdtRun;
        }
    }
    
    public int sizeOfSdtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.SDT$6);
        }
    }
    
    public void setSdtArray(final CTSdtRun[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.SDT$6);
    }
    
    public void setSdtArray(final int n, final CTSdtRun ctSdtRun) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtRun, CTSimpleFieldImpl.SDT$6, n, (short)2);
    }
    
    public CTSdtRun insertNewSdt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtRun)this.get_store().insert_element_user(CTSimpleFieldImpl.SDT$6, n);
        }
    }
    
    public CTSdtRun addNewSdt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtRun)this.get_store().add_element_user(CTSimpleFieldImpl.SDT$6);
        }
    }
    
    public void removeSdt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.SDT$6, n);
        }
    }
    
    public List<CTR> getRList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RList extends AbstractList<CTR>
            {
                @Override
                public CTR get(final int n) {
                    return CTSimpleFieldImpl.this.getRArray(n);
                }
                
                @Override
                public CTR set(final int n, final CTR ctr) {
                    final CTR rArray = CTSimpleFieldImpl.this.getRArray(n);
                    CTSimpleFieldImpl.this.setRArray(n, ctr);
                    return rArray;
                }
                
                @Override
                public void add(final int n, final CTR ctr) {
                    CTSimpleFieldImpl.this.insertNewR(n).set((XmlObject)ctr);
                }
                
                @Override
                public CTR remove(final int n) {
                    final CTR rArray = CTSimpleFieldImpl.this.getRArray(n);
                    CTSimpleFieldImpl.this.removeR(n);
                    return rArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfRArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.R$8, (List)list);
            final CTR[] array = new CTR[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTR getRArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTR ctr = (CTR)this.get_store().find_element_user(CTSimpleFieldImpl.R$8, n);
            if (ctr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctr;
        }
    }
    
    public int sizeOfRArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.R$8);
        }
    }
    
    public void setRArray(final CTR[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.R$8);
    }
    
    public void setRArray(final int n, final CTR ctr) {
        this.generatedSetterHelperImpl((XmlObject)ctr, CTSimpleFieldImpl.R$8, n, (short)2);
    }
    
    public CTR insertNewR(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTR)this.get_store().insert_element_user(CTSimpleFieldImpl.R$8, n);
        }
    }
    
    public CTR addNewR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTR)this.get_store().add_element_user(CTSimpleFieldImpl.R$8);
        }
    }
    
    public void removeR(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.R$8, n);
        }
    }
    
    public List<CTProofErr> getProofErrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ProofErrList extends AbstractList<CTProofErr>
            {
                @Override
                public CTProofErr get(final int n) {
                    return CTSimpleFieldImpl.this.getProofErrArray(n);
                }
                
                @Override
                public CTProofErr set(final int n, final CTProofErr ctProofErr) {
                    final CTProofErr proofErrArray = CTSimpleFieldImpl.this.getProofErrArray(n);
                    CTSimpleFieldImpl.this.setProofErrArray(n, ctProofErr);
                    return proofErrArray;
                }
                
                @Override
                public void add(final int n, final CTProofErr ctProofErr) {
                    CTSimpleFieldImpl.this.insertNewProofErr(n).set((XmlObject)ctProofErr);
                }
                
                @Override
                public CTProofErr remove(final int n) {
                    final CTProofErr proofErrArray = CTSimpleFieldImpl.this.getProofErrArray(n);
                    CTSimpleFieldImpl.this.removeProofErr(n);
                    return proofErrArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfProofErrArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.PROOFERR$10, (List)list);
            final CTProofErr[] array = new CTProofErr[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTProofErr getProofErrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTProofErr ctProofErr = (CTProofErr)this.get_store().find_element_user(CTSimpleFieldImpl.PROOFERR$10, n);
            if (ctProofErr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctProofErr;
        }
    }
    
    public int sizeOfProofErrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.PROOFERR$10);
        }
    }
    
    public void setProofErrArray(final CTProofErr[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.PROOFERR$10);
    }
    
    public void setProofErrArray(final int n, final CTProofErr ctProofErr) {
        this.generatedSetterHelperImpl((XmlObject)ctProofErr, CTSimpleFieldImpl.PROOFERR$10, n, (short)2);
    }
    
    public CTProofErr insertNewProofErr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProofErr)this.get_store().insert_element_user(CTSimpleFieldImpl.PROOFERR$10, n);
        }
    }
    
    public CTProofErr addNewProofErr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProofErr)this.get_store().add_element_user(CTSimpleFieldImpl.PROOFERR$10);
        }
    }
    
    public void removeProofErr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.PROOFERR$10, n);
        }
    }
    
    public List<CTPermStart> getPermStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PermStartList extends AbstractList<CTPermStart>
            {
                @Override
                public CTPermStart get(final int n) {
                    return CTSimpleFieldImpl.this.getPermStartArray(n);
                }
                
                @Override
                public CTPermStart set(final int n, final CTPermStart ctPermStart) {
                    final CTPermStart permStartArray = CTSimpleFieldImpl.this.getPermStartArray(n);
                    CTSimpleFieldImpl.this.setPermStartArray(n, ctPermStart);
                    return permStartArray;
                }
                
                @Override
                public void add(final int n, final CTPermStart ctPermStart) {
                    CTSimpleFieldImpl.this.insertNewPermStart(n).set((XmlObject)ctPermStart);
                }
                
                @Override
                public CTPermStart remove(final int n) {
                    final CTPermStart permStartArray = CTSimpleFieldImpl.this.getPermStartArray(n);
                    CTSimpleFieldImpl.this.removePermStart(n);
                    return permStartArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfPermStartArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.PERMSTART$12, (List)list);
            final CTPermStart[] array = new CTPermStart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPermStart getPermStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPermStart ctPermStart = (CTPermStart)this.get_store().find_element_user(CTSimpleFieldImpl.PERMSTART$12, n);
            if (ctPermStart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPermStart;
        }
    }
    
    public int sizeOfPermStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.PERMSTART$12);
        }
    }
    
    public void setPermStartArray(final CTPermStart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.PERMSTART$12);
    }
    
    public void setPermStartArray(final int n, final CTPermStart ctPermStart) {
        this.generatedSetterHelperImpl((XmlObject)ctPermStart, CTSimpleFieldImpl.PERMSTART$12, n, (short)2);
    }
    
    public CTPermStart insertNewPermStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPermStart)this.get_store().insert_element_user(CTSimpleFieldImpl.PERMSTART$12, n);
        }
    }
    
    public CTPermStart addNewPermStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPermStart)this.get_store().add_element_user(CTSimpleFieldImpl.PERMSTART$12);
        }
    }
    
    public void removePermStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.PERMSTART$12, n);
        }
    }
    
    public List<CTPerm> getPermEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PermEndList extends AbstractList<CTPerm>
            {
                @Override
                public CTPerm get(final int n) {
                    return CTSimpleFieldImpl.this.getPermEndArray(n);
                }
                
                @Override
                public CTPerm set(final int n, final CTPerm ctPerm) {
                    final CTPerm permEndArray = CTSimpleFieldImpl.this.getPermEndArray(n);
                    CTSimpleFieldImpl.this.setPermEndArray(n, ctPerm);
                    return permEndArray;
                }
                
                @Override
                public void add(final int n, final CTPerm ctPerm) {
                    CTSimpleFieldImpl.this.insertNewPermEnd(n).set((XmlObject)ctPerm);
                }
                
                @Override
                public CTPerm remove(final int n) {
                    final CTPerm permEndArray = CTSimpleFieldImpl.this.getPermEndArray(n);
                    CTSimpleFieldImpl.this.removePermEnd(n);
                    return permEndArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfPermEndArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.PERMEND$14, (List)list);
            final CTPerm[] array = new CTPerm[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPerm getPermEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPerm ctPerm = (CTPerm)this.get_store().find_element_user(CTSimpleFieldImpl.PERMEND$14, n);
            if (ctPerm == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPerm;
        }
    }
    
    public int sizeOfPermEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.PERMEND$14);
        }
    }
    
    public void setPermEndArray(final CTPerm[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.PERMEND$14);
    }
    
    public void setPermEndArray(final int n, final CTPerm ctPerm) {
        this.generatedSetterHelperImpl((XmlObject)ctPerm, CTSimpleFieldImpl.PERMEND$14, n, (short)2);
    }
    
    public CTPerm insertNewPermEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerm)this.get_store().insert_element_user(CTSimpleFieldImpl.PERMEND$14, n);
        }
    }
    
    public CTPerm addNewPermEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerm)this.get_store().add_element_user(CTSimpleFieldImpl.PERMEND$14);
        }
    }
    
    public void removePermEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.PERMEND$14, n);
        }
    }
    
    public List<CTBookmark> getBookmarkStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BookmarkStartList extends AbstractList<CTBookmark>
            {
                @Override
                public CTBookmark get(final int n) {
                    return CTSimpleFieldImpl.this.getBookmarkStartArray(n);
                }
                
                @Override
                public CTBookmark set(final int n, final CTBookmark ctBookmark) {
                    final CTBookmark bookmarkStartArray = CTSimpleFieldImpl.this.getBookmarkStartArray(n);
                    CTSimpleFieldImpl.this.setBookmarkStartArray(n, ctBookmark);
                    return bookmarkStartArray;
                }
                
                @Override
                public void add(final int n, final CTBookmark ctBookmark) {
                    CTSimpleFieldImpl.this.insertNewBookmarkStart(n).set((XmlObject)ctBookmark);
                }
                
                @Override
                public CTBookmark remove(final int n) {
                    final CTBookmark bookmarkStartArray = CTSimpleFieldImpl.this.getBookmarkStartArray(n);
                    CTSimpleFieldImpl.this.removeBookmarkStart(n);
                    return bookmarkStartArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfBookmarkStartArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.BOOKMARKSTART$16, (List)list);
            final CTBookmark[] array = new CTBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBookmark getBookmarkStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBookmark ctBookmark = (CTBookmark)this.get_store().find_element_user(CTSimpleFieldImpl.BOOKMARKSTART$16, n);
            if (ctBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBookmark;
        }
    }
    
    public int sizeOfBookmarkStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.BOOKMARKSTART$16);
        }
    }
    
    public void setBookmarkStartArray(final CTBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.BOOKMARKSTART$16);
    }
    
    public void setBookmarkStartArray(final int n, final CTBookmark ctBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctBookmark, CTSimpleFieldImpl.BOOKMARKSTART$16, n, (short)2);
    }
    
    public CTBookmark insertNewBookmarkStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookmark)this.get_store().insert_element_user(CTSimpleFieldImpl.BOOKMARKSTART$16, n);
        }
    }
    
    public CTBookmark addNewBookmarkStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookmark)this.get_store().add_element_user(CTSimpleFieldImpl.BOOKMARKSTART$16);
        }
    }
    
    public void removeBookmarkStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.BOOKMARKSTART$16, n);
        }
    }
    
    public List<CTMarkupRange> getBookmarkEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BookmarkEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTSimpleFieldImpl.this.getBookmarkEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange bookmarkEndArray = CTSimpleFieldImpl.this.getBookmarkEndArray(n);
                    CTSimpleFieldImpl.this.setBookmarkEndArray(n, ctMarkupRange);
                    return bookmarkEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTSimpleFieldImpl.this.insertNewBookmarkEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange bookmarkEndArray = CTSimpleFieldImpl.this.getBookmarkEndArray(n);
                    CTSimpleFieldImpl.this.removeBookmarkEnd(n);
                    return bookmarkEndArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfBookmarkEndArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.BOOKMARKEND$18, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getBookmarkEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTSimpleFieldImpl.BOOKMARKEND$18, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfBookmarkEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.BOOKMARKEND$18);
        }
    }
    
    public void setBookmarkEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.BOOKMARKEND$18);
    }
    
    public void setBookmarkEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTSimpleFieldImpl.BOOKMARKEND$18, n, (short)2);
    }
    
    public CTMarkupRange insertNewBookmarkEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTSimpleFieldImpl.BOOKMARKEND$18, n);
        }
    }
    
    public CTMarkupRange addNewBookmarkEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTSimpleFieldImpl.BOOKMARKEND$18);
        }
    }
    
    public void removeBookmarkEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.BOOKMARKEND$18, n);
        }
    }
    
    public List<CTMoveBookmark> getMoveFromRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromRangeStartList extends AbstractList<CTMoveBookmark>
            {
                @Override
                public CTMoveBookmark get(final int n) {
                    return CTSimpleFieldImpl.this.getMoveFromRangeStartArray(n);
                }
                
                @Override
                public CTMoveBookmark set(final int n, final CTMoveBookmark ctMoveBookmark) {
                    final CTMoveBookmark moveFromRangeStartArray = CTSimpleFieldImpl.this.getMoveFromRangeStartArray(n);
                    CTSimpleFieldImpl.this.setMoveFromRangeStartArray(n, ctMoveBookmark);
                    return moveFromRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMoveBookmark ctMoveBookmark) {
                    CTSimpleFieldImpl.this.insertNewMoveFromRangeStart(n).set((XmlObject)ctMoveBookmark);
                }
                
                @Override
                public CTMoveBookmark remove(final int n) {
                    final CTMoveBookmark moveFromRangeStartArray = CTSimpleFieldImpl.this.getMoveFromRangeStartArray(n);
                    CTSimpleFieldImpl.this.removeMoveFromRangeStart(n);
                    return moveFromRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfMoveFromRangeStartArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.MOVEFROMRANGESTART$20, (List)list);
            final CTMoveBookmark[] array = new CTMoveBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMoveBookmark getMoveFromRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMoveBookmark ctMoveBookmark = (CTMoveBookmark)this.get_store().find_element_user(CTSimpleFieldImpl.MOVEFROMRANGESTART$20, n);
            if (ctMoveBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMoveBookmark;
        }
    }
    
    public int sizeOfMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.MOVEFROMRANGESTART$20);
        }
    }
    
    public void setMoveFromRangeStartArray(final CTMoveBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.MOVEFROMRANGESTART$20);
    }
    
    public void setMoveFromRangeStartArray(final int n, final CTMoveBookmark ctMoveBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctMoveBookmark, CTSimpleFieldImpl.MOVEFROMRANGESTART$20, n, (short)2);
    }
    
    public CTMoveBookmark insertNewMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().insert_element_user(CTSimpleFieldImpl.MOVEFROMRANGESTART$20, n);
        }
    }
    
    public CTMoveBookmark addNewMoveFromRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().add_element_user(CTSimpleFieldImpl.MOVEFROMRANGESTART$20);
        }
    }
    
    public void removeMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.MOVEFROMRANGESTART$20, n);
        }
    }
    
    public List<CTMarkupRange> getMoveFromRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTSimpleFieldImpl.this.getMoveFromRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange moveFromRangeEndArray = CTSimpleFieldImpl.this.getMoveFromRangeEndArray(n);
                    CTSimpleFieldImpl.this.setMoveFromRangeEndArray(n, ctMarkupRange);
                    return moveFromRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTSimpleFieldImpl.this.insertNewMoveFromRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange moveFromRangeEndArray = CTSimpleFieldImpl.this.getMoveFromRangeEndArray(n);
                    CTSimpleFieldImpl.this.removeMoveFromRangeEnd(n);
                    return moveFromRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfMoveFromRangeEndArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.MOVEFROMRANGEEND$22, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getMoveFromRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTSimpleFieldImpl.MOVEFROMRANGEEND$22, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.MOVEFROMRANGEEND$22);
        }
    }
    
    public void setMoveFromRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.MOVEFROMRANGEEND$22);
    }
    
    public void setMoveFromRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTSimpleFieldImpl.MOVEFROMRANGEEND$22, n, (short)2);
    }
    
    public CTMarkupRange insertNewMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTSimpleFieldImpl.MOVEFROMRANGEEND$22, n);
        }
    }
    
    public CTMarkupRange addNewMoveFromRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTSimpleFieldImpl.MOVEFROMRANGEEND$22);
        }
    }
    
    public void removeMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.MOVEFROMRANGEEND$22, n);
        }
    }
    
    public List<CTMoveBookmark> getMoveToRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToRangeStartList extends AbstractList<CTMoveBookmark>
            {
                @Override
                public CTMoveBookmark get(final int n) {
                    return CTSimpleFieldImpl.this.getMoveToRangeStartArray(n);
                }
                
                @Override
                public CTMoveBookmark set(final int n, final CTMoveBookmark ctMoveBookmark) {
                    final CTMoveBookmark moveToRangeStartArray = CTSimpleFieldImpl.this.getMoveToRangeStartArray(n);
                    CTSimpleFieldImpl.this.setMoveToRangeStartArray(n, ctMoveBookmark);
                    return moveToRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMoveBookmark ctMoveBookmark) {
                    CTSimpleFieldImpl.this.insertNewMoveToRangeStart(n).set((XmlObject)ctMoveBookmark);
                }
                
                @Override
                public CTMoveBookmark remove(final int n) {
                    final CTMoveBookmark moveToRangeStartArray = CTSimpleFieldImpl.this.getMoveToRangeStartArray(n);
                    CTSimpleFieldImpl.this.removeMoveToRangeStart(n);
                    return moveToRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfMoveToRangeStartArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.MOVETORANGESTART$24, (List)list);
            final CTMoveBookmark[] array = new CTMoveBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMoveBookmark getMoveToRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMoveBookmark ctMoveBookmark = (CTMoveBookmark)this.get_store().find_element_user(CTSimpleFieldImpl.MOVETORANGESTART$24, n);
            if (ctMoveBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMoveBookmark;
        }
    }
    
    public int sizeOfMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.MOVETORANGESTART$24);
        }
    }
    
    public void setMoveToRangeStartArray(final CTMoveBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.MOVETORANGESTART$24);
    }
    
    public void setMoveToRangeStartArray(final int n, final CTMoveBookmark ctMoveBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctMoveBookmark, CTSimpleFieldImpl.MOVETORANGESTART$24, n, (short)2);
    }
    
    public CTMoveBookmark insertNewMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().insert_element_user(CTSimpleFieldImpl.MOVETORANGESTART$24, n);
        }
    }
    
    public CTMoveBookmark addNewMoveToRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().add_element_user(CTSimpleFieldImpl.MOVETORANGESTART$24);
        }
    }
    
    public void removeMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.MOVETORANGESTART$24, n);
        }
    }
    
    public List<CTMarkupRange> getMoveToRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTSimpleFieldImpl.this.getMoveToRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange moveToRangeEndArray = CTSimpleFieldImpl.this.getMoveToRangeEndArray(n);
                    CTSimpleFieldImpl.this.setMoveToRangeEndArray(n, ctMarkupRange);
                    return moveToRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTSimpleFieldImpl.this.insertNewMoveToRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange moveToRangeEndArray = CTSimpleFieldImpl.this.getMoveToRangeEndArray(n);
                    CTSimpleFieldImpl.this.removeMoveToRangeEnd(n);
                    return moveToRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfMoveToRangeEndArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.MOVETORANGEEND$26, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getMoveToRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTSimpleFieldImpl.MOVETORANGEEND$26, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.MOVETORANGEEND$26);
        }
    }
    
    public void setMoveToRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.MOVETORANGEEND$26);
    }
    
    public void setMoveToRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTSimpleFieldImpl.MOVETORANGEEND$26, n, (short)2);
    }
    
    public CTMarkupRange insertNewMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTSimpleFieldImpl.MOVETORANGEEND$26, n);
        }
    }
    
    public CTMarkupRange addNewMoveToRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTSimpleFieldImpl.MOVETORANGEEND$26);
        }
    }
    
    public void removeMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.MOVETORANGEEND$26, n);
        }
    }
    
    public List<CTMarkupRange> getCommentRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentRangeStartList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTSimpleFieldImpl.this.getCommentRangeStartArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange commentRangeStartArray = CTSimpleFieldImpl.this.getCommentRangeStartArray(n);
                    CTSimpleFieldImpl.this.setCommentRangeStartArray(n, ctMarkupRange);
                    return commentRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTSimpleFieldImpl.this.insertNewCommentRangeStart(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange commentRangeStartArray = CTSimpleFieldImpl.this.getCommentRangeStartArray(n);
                    CTSimpleFieldImpl.this.removeCommentRangeStart(n);
                    return commentRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfCommentRangeStartArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.COMMENTRANGESTART$28, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getCommentRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTSimpleFieldImpl.COMMENTRANGESTART$28, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfCommentRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.COMMENTRANGESTART$28);
        }
    }
    
    public void setCommentRangeStartArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.COMMENTRANGESTART$28);
    }
    
    public void setCommentRangeStartArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTSimpleFieldImpl.COMMENTRANGESTART$28, n, (short)2);
    }
    
    public CTMarkupRange insertNewCommentRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTSimpleFieldImpl.COMMENTRANGESTART$28, n);
        }
    }
    
    public CTMarkupRange addNewCommentRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTSimpleFieldImpl.COMMENTRANGESTART$28);
        }
    }
    
    public void removeCommentRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.COMMENTRANGESTART$28, n);
        }
    }
    
    public List<CTMarkupRange> getCommentRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTSimpleFieldImpl.this.getCommentRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange commentRangeEndArray = CTSimpleFieldImpl.this.getCommentRangeEndArray(n);
                    CTSimpleFieldImpl.this.setCommentRangeEndArray(n, ctMarkupRange);
                    return commentRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTSimpleFieldImpl.this.insertNewCommentRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange commentRangeEndArray = CTSimpleFieldImpl.this.getCommentRangeEndArray(n);
                    CTSimpleFieldImpl.this.removeCommentRangeEnd(n);
                    return commentRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfCommentRangeEndArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.COMMENTRANGEEND$30, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getCommentRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTSimpleFieldImpl.COMMENTRANGEEND$30, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfCommentRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.COMMENTRANGEEND$30);
        }
    }
    
    public void setCommentRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.COMMENTRANGEEND$30);
    }
    
    public void setCommentRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTSimpleFieldImpl.COMMENTRANGEEND$30, n, (short)2);
    }
    
    public CTMarkupRange insertNewCommentRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTSimpleFieldImpl.COMMENTRANGEEND$30, n);
        }
    }
    
    public CTMarkupRange addNewCommentRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTSimpleFieldImpl.COMMENTRANGEEND$30);
        }
    }
    
    public void removeCommentRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.COMMENTRANGEEND$30, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlInsRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlInsRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTSimpleFieldImpl.this.getCustomXmlInsRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlInsRangeStartArray = CTSimpleFieldImpl.this.getCustomXmlInsRangeStartArray(n);
                    CTSimpleFieldImpl.this.setCustomXmlInsRangeStartArray(n, ctTrackChange);
                    return customXmlInsRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTSimpleFieldImpl.this.insertNewCustomXmlInsRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlInsRangeStartArray = CTSimpleFieldImpl.this.getCustomXmlInsRangeStartArray(n);
                    CTSimpleFieldImpl.this.removeCustomXmlInsRangeStart(n);
                    return customXmlInsRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfCustomXmlInsRangeStartArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.CUSTOMXMLINSRANGESTART$32, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlInsRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTSimpleFieldImpl.CUSTOMXMLINSRANGESTART$32, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlInsRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.CUSTOMXMLINSRANGESTART$32);
        }
    }
    
    public void setCustomXmlInsRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.CUSTOMXMLINSRANGESTART$32);
    }
    
    public void setCustomXmlInsRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTSimpleFieldImpl.CUSTOMXMLINSRANGESTART$32, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlInsRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTSimpleFieldImpl.CUSTOMXMLINSRANGESTART$32, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlInsRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTSimpleFieldImpl.CUSTOMXMLINSRANGESTART$32);
        }
    }
    
    public void removeCustomXmlInsRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.CUSTOMXMLINSRANGESTART$32, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlInsRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlInsRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTSimpleFieldImpl.this.getCustomXmlInsRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlInsRangeEndArray = CTSimpleFieldImpl.this.getCustomXmlInsRangeEndArray(n);
                    CTSimpleFieldImpl.this.setCustomXmlInsRangeEndArray(n, ctMarkup);
                    return customXmlInsRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTSimpleFieldImpl.this.insertNewCustomXmlInsRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlInsRangeEndArray = CTSimpleFieldImpl.this.getCustomXmlInsRangeEndArray(n);
                    CTSimpleFieldImpl.this.removeCustomXmlInsRangeEnd(n);
                    return customXmlInsRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfCustomXmlInsRangeEndArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.CUSTOMXMLINSRANGEEND$34, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlInsRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTSimpleFieldImpl.CUSTOMXMLINSRANGEEND$34, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlInsRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.CUSTOMXMLINSRANGEEND$34);
        }
    }
    
    public void setCustomXmlInsRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.CUSTOMXMLINSRANGEEND$34);
    }
    
    public void setCustomXmlInsRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTSimpleFieldImpl.CUSTOMXMLINSRANGEEND$34, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlInsRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTSimpleFieldImpl.CUSTOMXMLINSRANGEEND$34, n);
        }
    }
    
    public CTMarkup addNewCustomXmlInsRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTSimpleFieldImpl.CUSTOMXMLINSRANGEEND$34);
        }
    }
    
    public void removeCustomXmlInsRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.CUSTOMXMLINSRANGEEND$34, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlDelRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlDelRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTSimpleFieldImpl.this.getCustomXmlDelRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlDelRangeStartArray = CTSimpleFieldImpl.this.getCustomXmlDelRangeStartArray(n);
                    CTSimpleFieldImpl.this.setCustomXmlDelRangeStartArray(n, ctTrackChange);
                    return customXmlDelRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTSimpleFieldImpl.this.insertNewCustomXmlDelRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlDelRangeStartArray = CTSimpleFieldImpl.this.getCustomXmlDelRangeStartArray(n);
                    CTSimpleFieldImpl.this.removeCustomXmlDelRangeStart(n);
                    return customXmlDelRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfCustomXmlDelRangeStartArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.CUSTOMXMLDELRANGESTART$36, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlDelRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTSimpleFieldImpl.CUSTOMXMLDELRANGESTART$36, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlDelRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.CUSTOMXMLDELRANGESTART$36);
        }
    }
    
    public void setCustomXmlDelRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.CUSTOMXMLDELRANGESTART$36);
    }
    
    public void setCustomXmlDelRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTSimpleFieldImpl.CUSTOMXMLDELRANGESTART$36, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlDelRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTSimpleFieldImpl.CUSTOMXMLDELRANGESTART$36, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlDelRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTSimpleFieldImpl.CUSTOMXMLDELRANGESTART$36);
        }
    }
    
    public void removeCustomXmlDelRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.CUSTOMXMLDELRANGESTART$36, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlDelRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlDelRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTSimpleFieldImpl.this.getCustomXmlDelRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlDelRangeEndArray = CTSimpleFieldImpl.this.getCustomXmlDelRangeEndArray(n);
                    CTSimpleFieldImpl.this.setCustomXmlDelRangeEndArray(n, ctMarkup);
                    return customXmlDelRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTSimpleFieldImpl.this.insertNewCustomXmlDelRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlDelRangeEndArray = CTSimpleFieldImpl.this.getCustomXmlDelRangeEndArray(n);
                    CTSimpleFieldImpl.this.removeCustomXmlDelRangeEnd(n);
                    return customXmlDelRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfCustomXmlDelRangeEndArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.CUSTOMXMLDELRANGEEND$38, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlDelRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTSimpleFieldImpl.CUSTOMXMLDELRANGEEND$38, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlDelRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.CUSTOMXMLDELRANGEEND$38);
        }
    }
    
    public void setCustomXmlDelRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.CUSTOMXMLDELRANGEEND$38);
    }
    
    public void setCustomXmlDelRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTSimpleFieldImpl.CUSTOMXMLDELRANGEEND$38, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlDelRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTSimpleFieldImpl.CUSTOMXMLDELRANGEEND$38, n);
        }
    }
    
    public CTMarkup addNewCustomXmlDelRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTSimpleFieldImpl.CUSTOMXMLDELRANGEEND$38);
        }
    }
    
    public void removeCustomXmlDelRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.CUSTOMXMLDELRANGEEND$38, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlMoveFromRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveFromRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTSimpleFieldImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlMoveFromRangeStartArray = CTSimpleFieldImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                    CTSimpleFieldImpl.this.setCustomXmlMoveFromRangeStartArray(n, ctTrackChange);
                    return customXmlMoveFromRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTSimpleFieldImpl.this.insertNewCustomXmlMoveFromRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlMoveFromRangeStartArray = CTSimpleFieldImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                    CTSimpleFieldImpl.this.removeCustomXmlMoveFromRangeStart(n);
                    return customXmlMoveFromRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfCustomXmlMoveFromRangeStartArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.CUSTOMXMLMOVEFROMRANGESTART$40, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlMoveFromRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTSimpleFieldImpl.CUSTOMXMLMOVEFROMRANGESTART$40, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.CUSTOMXMLMOVEFROMRANGESTART$40);
        }
    }
    
    public void setCustomXmlMoveFromRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.CUSTOMXMLMOVEFROMRANGESTART$40);
    }
    
    public void setCustomXmlMoveFromRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTSimpleFieldImpl.CUSTOMXMLMOVEFROMRANGESTART$40, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTSimpleFieldImpl.CUSTOMXMLMOVEFROMRANGESTART$40, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlMoveFromRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTSimpleFieldImpl.CUSTOMXMLMOVEFROMRANGESTART$40);
        }
    }
    
    public void removeCustomXmlMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.CUSTOMXMLMOVEFROMRANGESTART$40, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlMoveFromRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveFromRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTSimpleFieldImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlMoveFromRangeEndArray = CTSimpleFieldImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                    CTSimpleFieldImpl.this.setCustomXmlMoveFromRangeEndArray(n, ctMarkup);
                    return customXmlMoveFromRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTSimpleFieldImpl.this.insertNewCustomXmlMoveFromRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlMoveFromRangeEndArray = CTSimpleFieldImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                    CTSimpleFieldImpl.this.removeCustomXmlMoveFromRangeEnd(n);
                    return customXmlMoveFromRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfCustomXmlMoveFromRangeEndArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.CUSTOMXMLMOVEFROMRANGEEND$42, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlMoveFromRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTSimpleFieldImpl.CUSTOMXMLMOVEFROMRANGEEND$42, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.CUSTOMXMLMOVEFROMRANGEEND$42);
        }
    }
    
    public void setCustomXmlMoveFromRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.CUSTOMXMLMOVEFROMRANGEEND$42);
    }
    
    public void setCustomXmlMoveFromRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTSimpleFieldImpl.CUSTOMXMLMOVEFROMRANGEEND$42, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTSimpleFieldImpl.CUSTOMXMLMOVEFROMRANGEEND$42, n);
        }
    }
    
    public CTMarkup addNewCustomXmlMoveFromRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTSimpleFieldImpl.CUSTOMXMLMOVEFROMRANGEEND$42);
        }
    }
    
    public void removeCustomXmlMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.CUSTOMXMLMOVEFROMRANGEEND$42, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlMoveToRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveToRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTSimpleFieldImpl.this.getCustomXmlMoveToRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlMoveToRangeStartArray = CTSimpleFieldImpl.this.getCustomXmlMoveToRangeStartArray(n);
                    CTSimpleFieldImpl.this.setCustomXmlMoveToRangeStartArray(n, ctTrackChange);
                    return customXmlMoveToRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTSimpleFieldImpl.this.insertNewCustomXmlMoveToRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlMoveToRangeStartArray = CTSimpleFieldImpl.this.getCustomXmlMoveToRangeStartArray(n);
                    CTSimpleFieldImpl.this.removeCustomXmlMoveToRangeStart(n);
                    return customXmlMoveToRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfCustomXmlMoveToRangeStartArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.CUSTOMXMLMOVETORANGESTART$44, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlMoveToRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTSimpleFieldImpl.CUSTOMXMLMOVETORANGESTART$44, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.CUSTOMXMLMOVETORANGESTART$44);
        }
    }
    
    public void setCustomXmlMoveToRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.CUSTOMXMLMOVETORANGESTART$44);
    }
    
    public void setCustomXmlMoveToRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTSimpleFieldImpl.CUSTOMXMLMOVETORANGESTART$44, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTSimpleFieldImpl.CUSTOMXMLMOVETORANGESTART$44, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlMoveToRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTSimpleFieldImpl.CUSTOMXMLMOVETORANGESTART$44);
        }
    }
    
    public void removeCustomXmlMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.CUSTOMXMLMOVETORANGESTART$44, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlMoveToRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveToRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTSimpleFieldImpl.this.getCustomXmlMoveToRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlMoveToRangeEndArray = CTSimpleFieldImpl.this.getCustomXmlMoveToRangeEndArray(n);
                    CTSimpleFieldImpl.this.setCustomXmlMoveToRangeEndArray(n, ctMarkup);
                    return customXmlMoveToRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTSimpleFieldImpl.this.insertNewCustomXmlMoveToRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlMoveToRangeEndArray = CTSimpleFieldImpl.this.getCustomXmlMoveToRangeEndArray(n);
                    CTSimpleFieldImpl.this.removeCustomXmlMoveToRangeEnd(n);
                    return customXmlMoveToRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfCustomXmlMoveToRangeEndArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.CUSTOMXMLMOVETORANGEEND$46, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlMoveToRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTSimpleFieldImpl.CUSTOMXMLMOVETORANGEEND$46, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.CUSTOMXMLMOVETORANGEEND$46);
        }
    }
    
    public void setCustomXmlMoveToRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.CUSTOMXMLMOVETORANGEEND$46);
    }
    
    public void setCustomXmlMoveToRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTSimpleFieldImpl.CUSTOMXMLMOVETORANGEEND$46, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTSimpleFieldImpl.CUSTOMXMLMOVETORANGEEND$46, n);
        }
    }
    
    public CTMarkup addNewCustomXmlMoveToRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTSimpleFieldImpl.CUSTOMXMLMOVETORANGEEND$46);
        }
    }
    
    public void removeCustomXmlMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.CUSTOMXMLMOVETORANGEEND$46, n);
        }
    }
    
    public List<CTRunTrackChange> getInsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class InsList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTSimpleFieldImpl.this.getInsArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange insArray = CTSimpleFieldImpl.this.getInsArray(n);
                    CTSimpleFieldImpl.this.setInsArray(n, ctRunTrackChange);
                    return insArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTSimpleFieldImpl.this.insertNewIns(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange insArray = CTSimpleFieldImpl.this.getInsArray(n);
                    CTSimpleFieldImpl.this.removeIns(n);
                    return insArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfInsArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.INS$48, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getInsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTSimpleFieldImpl.INS$48, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfInsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.INS$48);
        }
    }
    
    public void setInsArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.INS$48);
    }
    
    public void setInsArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTSimpleFieldImpl.INS$48, n, (short)2);
    }
    
    public CTRunTrackChange insertNewIns(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTSimpleFieldImpl.INS$48, n);
        }
    }
    
    public CTRunTrackChange addNewIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTSimpleFieldImpl.INS$48);
        }
    }
    
    public void removeIns(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.INS$48, n);
        }
    }
    
    public List<CTRunTrackChange> getDelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DelList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTSimpleFieldImpl.this.getDelArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange delArray = CTSimpleFieldImpl.this.getDelArray(n);
                    CTSimpleFieldImpl.this.setDelArray(n, ctRunTrackChange);
                    return delArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTSimpleFieldImpl.this.insertNewDel(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange delArray = CTSimpleFieldImpl.this.getDelArray(n);
                    CTSimpleFieldImpl.this.removeDel(n);
                    return delArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfDelArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.DEL$50, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getDelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTSimpleFieldImpl.DEL$50, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfDelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.DEL$50);
        }
    }
    
    public void setDelArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.DEL$50);
    }
    
    public void setDelArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTSimpleFieldImpl.DEL$50, n, (short)2);
    }
    
    public CTRunTrackChange insertNewDel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTSimpleFieldImpl.DEL$50, n);
        }
    }
    
    public CTRunTrackChange addNewDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTSimpleFieldImpl.DEL$50);
        }
    }
    
    public void removeDel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.DEL$50, n);
        }
    }
    
    public List<CTRunTrackChange> getMoveFromList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTSimpleFieldImpl.this.getMoveFromArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange moveFromArray = CTSimpleFieldImpl.this.getMoveFromArray(n);
                    CTSimpleFieldImpl.this.setMoveFromArray(n, ctRunTrackChange);
                    return moveFromArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTSimpleFieldImpl.this.insertNewMoveFrom(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange moveFromArray = CTSimpleFieldImpl.this.getMoveFromArray(n);
                    CTSimpleFieldImpl.this.removeMoveFrom(n);
                    return moveFromArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfMoveFromArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.MOVEFROM$52, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getMoveFromArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTSimpleFieldImpl.MOVEFROM$52, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfMoveFromArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.MOVEFROM$52);
        }
    }
    
    public void setMoveFromArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.MOVEFROM$52);
    }
    
    public void setMoveFromArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTSimpleFieldImpl.MOVEFROM$52, n, (short)2);
    }
    
    public CTRunTrackChange insertNewMoveFrom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTSimpleFieldImpl.MOVEFROM$52, n);
        }
    }
    
    public CTRunTrackChange addNewMoveFrom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTSimpleFieldImpl.MOVEFROM$52);
        }
    }
    
    public void removeMoveFrom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.MOVEFROM$52, n);
        }
    }
    
    public List<CTRunTrackChange> getMoveToList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTSimpleFieldImpl.this.getMoveToArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange moveToArray = CTSimpleFieldImpl.this.getMoveToArray(n);
                    CTSimpleFieldImpl.this.setMoveToArray(n, ctRunTrackChange);
                    return moveToArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTSimpleFieldImpl.this.insertNewMoveTo(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange moveToArray = CTSimpleFieldImpl.this.getMoveToArray(n);
                    CTSimpleFieldImpl.this.removeMoveTo(n);
                    return moveToArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfMoveToArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.MOVETO$54, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getMoveToArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTSimpleFieldImpl.MOVETO$54, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfMoveToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.MOVETO$54);
        }
    }
    
    public void setMoveToArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.MOVETO$54);
    }
    
    public void setMoveToArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTSimpleFieldImpl.MOVETO$54, n, (short)2);
    }
    
    public CTRunTrackChange insertNewMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTSimpleFieldImpl.MOVETO$54, n);
        }
    }
    
    public CTRunTrackChange addNewMoveTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTSimpleFieldImpl.MOVETO$54);
        }
    }
    
    public void removeMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.MOVETO$54, n);
        }
    }
    
    public List<CTOMathPara> getOMathParaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OMathParaList extends AbstractList<CTOMathPara>
            {
                @Override
                public CTOMathPara get(final int n) {
                    return CTSimpleFieldImpl.this.getOMathParaArray(n);
                }
                
                @Override
                public CTOMathPara set(final int n, final CTOMathPara ctoMathPara) {
                    final CTOMathPara oMathParaArray = CTSimpleFieldImpl.this.getOMathParaArray(n);
                    CTSimpleFieldImpl.this.setOMathParaArray(n, ctoMathPara);
                    return oMathParaArray;
                }
                
                @Override
                public void add(final int n, final CTOMathPara ctoMathPara) {
                    CTSimpleFieldImpl.this.insertNewOMathPara(n).set((XmlObject)ctoMathPara);
                }
                
                @Override
                public CTOMathPara remove(final int n) {
                    final CTOMathPara oMathParaArray = CTSimpleFieldImpl.this.getOMathParaArray(n);
                    CTSimpleFieldImpl.this.removeOMathPara(n);
                    return oMathParaArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfOMathParaArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.OMATHPARA$56, (List)list);
            final CTOMathPara[] array = new CTOMathPara[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOMathPara getOMathParaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOMathPara ctoMathPara = (CTOMathPara)this.get_store().find_element_user(CTSimpleFieldImpl.OMATHPARA$56, n);
            if (ctoMathPara == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctoMathPara;
        }
    }
    
    public int sizeOfOMathParaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.OMATHPARA$56);
        }
    }
    
    public void setOMathParaArray(final CTOMathPara[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.OMATHPARA$56);
    }
    
    public void setOMathParaArray(final int n, final CTOMathPara ctoMathPara) {
        this.generatedSetterHelperImpl((XmlObject)ctoMathPara, CTSimpleFieldImpl.OMATHPARA$56, n, (short)2);
    }
    
    public CTOMathPara insertNewOMathPara(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMathPara)this.get_store().insert_element_user(CTSimpleFieldImpl.OMATHPARA$56, n);
        }
    }
    
    public CTOMathPara addNewOMathPara() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMathPara)this.get_store().add_element_user(CTSimpleFieldImpl.OMATHPARA$56);
        }
    }
    
    public void removeOMathPara(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.OMATHPARA$56, n);
        }
    }
    
    public List<CTOMath> getOMathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OMathList extends AbstractList<CTOMath>
            {
                @Override
                public CTOMath get(final int n) {
                    return CTSimpleFieldImpl.this.getOMathArray(n);
                }
                
                @Override
                public CTOMath set(final int n, final CTOMath ctoMath) {
                    final CTOMath oMathArray = CTSimpleFieldImpl.this.getOMathArray(n);
                    CTSimpleFieldImpl.this.setOMathArray(n, ctoMath);
                    return oMathArray;
                }
                
                @Override
                public void add(final int n, final CTOMath ctoMath) {
                    CTSimpleFieldImpl.this.insertNewOMath(n).set((XmlObject)ctoMath);
                }
                
                @Override
                public CTOMath remove(final int n) {
                    final CTOMath oMathArray = CTSimpleFieldImpl.this.getOMathArray(n);
                    CTSimpleFieldImpl.this.removeOMath(n);
                    return oMathArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfOMathArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.OMATH$58, (List)list);
            final CTOMath[] array = new CTOMath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOMath getOMathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOMath ctoMath = (CTOMath)this.get_store().find_element_user(CTSimpleFieldImpl.OMATH$58, n);
            if (ctoMath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctoMath;
        }
    }
    
    public int sizeOfOMathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.OMATH$58);
        }
    }
    
    public void setOMathArray(final CTOMath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.OMATH$58);
    }
    
    public void setOMathArray(final int n, final CTOMath ctoMath) {
        this.generatedSetterHelperImpl((XmlObject)ctoMath, CTSimpleFieldImpl.OMATH$58, n, (short)2);
    }
    
    public CTOMath insertNewOMath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMath)this.get_store().insert_element_user(CTSimpleFieldImpl.OMATH$58, n);
        }
    }
    
    public CTOMath addNewOMath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMath)this.get_store().add_element_user(CTSimpleFieldImpl.OMATH$58);
        }
    }
    
    public void removeOMath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.OMATH$58, n);
        }
    }
    
    public List<CTSimpleField> getFldSimpleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FldSimpleList extends AbstractList<CTSimpleField>
            {
                @Override
                public CTSimpleField get(final int n) {
                    return CTSimpleFieldImpl.this.getFldSimpleArray(n);
                }
                
                @Override
                public CTSimpleField set(final int n, final CTSimpleField ctSimpleField) {
                    final CTSimpleField fldSimpleArray = CTSimpleFieldImpl.this.getFldSimpleArray(n);
                    CTSimpleFieldImpl.this.setFldSimpleArray(n, ctSimpleField);
                    return fldSimpleArray;
                }
                
                @Override
                public void add(final int n, final CTSimpleField ctSimpleField) {
                    CTSimpleFieldImpl.this.insertNewFldSimple(n).set((XmlObject)ctSimpleField);
                }
                
                @Override
                public CTSimpleField remove(final int n) {
                    final CTSimpleField fldSimpleArray = CTSimpleFieldImpl.this.getFldSimpleArray(n);
                    CTSimpleFieldImpl.this.removeFldSimple(n);
                    return fldSimpleArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfFldSimpleArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.FLDSIMPLE$60, (List)list);
            final CTSimpleField[] array = new CTSimpleField[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSimpleField getFldSimpleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSimpleField ctSimpleField = (CTSimpleField)this.get_store().find_element_user(CTSimpleFieldImpl.FLDSIMPLE$60, n);
            if (ctSimpleField == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSimpleField;
        }
    }
    
    public int sizeOfFldSimpleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.FLDSIMPLE$60);
        }
    }
    
    public void setFldSimpleArray(final CTSimpleField[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.FLDSIMPLE$60);
    }
    
    public void setFldSimpleArray(final int n, final CTSimpleField ctSimpleField) {
        this.generatedSetterHelperImpl((XmlObject)ctSimpleField, CTSimpleFieldImpl.FLDSIMPLE$60, n, (short)2);
    }
    
    public CTSimpleField insertNewFldSimple(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSimpleField)this.get_store().insert_element_user(CTSimpleFieldImpl.FLDSIMPLE$60, n);
        }
    }
    
    public CTSimpleField addNewFldSimple() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSimpleField)this.get_store().add_element_user(CTSimpleFieldImpl.FLDSIMPLE$60);
        }
    }
    
    public void removeFldSimple(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.FLDSIMPLE$60, n);
        }
    }
    
    public List<CTHyperlink> getHyperlinkList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HyperlinkList extends AbstractList<CTHyperlink>
            {
                @Override
                public CTHyperlink get(final int n) {
                    return CTSimpleFieldImpl.this.getHyperlinkArray(n);
                }
                
                @Override
                public CTHyperlink set(final int n, final CTHyperlink ctHyperlink) {
                    final CTHyperlink hyperlinkArray = CTSimpleFieldImpl.this.getHyperlinkArray(n);
                    CTSimpleFieldImpl.this.setHyperlinkArray(n, ctHyperlink);
                    return hyperlinkArray;
                }
                
                @Override
                public void add(final int n, final CTHyperlink ctHyperlink) {
                    CTSimpleFieldImpl.this.insertNewHyperlink(n).set((XmlObject)ctHyperlink);
                }
                
                @Override
                public CTHyperlink remove(final int n) {
                    final CTHyperlink hyperlinkArray = CTSimpleFieldImpl.this.getHyperlinkArray(n);
                    CTSimpleFieldImpl.this.removeHyperlink(n);
                    return hyperlinkArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfHyperlinkArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.HYPERLINK$62, (List)list);
            final CTHyperlink[] array = new CTHyperlink[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTHyperlink getHyperlinkArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHyperlink ctHyperlink = (CTHyperlink)this.get_store().find_element_user(CTSimpleFieldImpl.HYPERLINK$62, n);
            if (ctHyperlink == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctHyperlink;
        }
    }
    
    public int sizeOfHyperlinkArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.HYPERLINK$62);
        }
    }
    
    public void setHyperlinkArray(final CTHyperlink[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.HYPERLINK$62);
    }
    
    public void setHyperlinkArray(final int n, final CTHyperlink ctHyperlink) {
        this.generatedSetterHelperImpl((XmlObject)ctHyperlink, CTSimpleFieldImpl.HYPERLINK$62, n, (short)2);
    }
    
    public CTHyperlink insertNewHyperlink(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHyperlink)this.get_store().insert_element_user(CTSimpleFieldImpl.HYPERLINK$62, n);
        }
    }
    
    public CTHyperlink addNewHyperlink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHyperlink)this.get_store().add_element_user(CTSimpleFieldImpl.HYPERLINK$62);
        }
    }
    
    public void removeHyperlink(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.HYPERLINK$62, n);
        }
    }
    
    public List<CTRel> getSubDocList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SubDocList extends AbstractList<CTRel>
            {
                @Override
                public CTRel get(final int n) {
                    return CTSimpleFieldImpl.this.getSubDocArray(n);
                }
                
                @Override
                public CTRel set(final int n, final CTRel ctRel) {
                    final CTRel subDocArray = CTSimpleFieldImpl.this.getSubDocArray(n);
                    CTSimpleFieldImpl.this.setSubDocArray(n, ctRel);
                    return subDocArray;
                }
                
                @Override
                public void add(final int n, final CTRel ctRel) {
                    CTSimpleFieldImpl.this.insertNewSubDoc(n).set((XmlObject)ctRel);
                }
                
                @Override
                public CTRel remove(final int n) {
                    final CTRel subDocArray = CTSimpleFieldImpl.this.getSubDocArray(n);
                    CTSimpleFieldImpl.this.removeSubDoc(n);
                    return subDocArray;
                }
                
                @Override
                public int size() {
                    return CTSimpleFieldImpl.this.sizeOfSubDocArray();
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
            this.get_store().find_all_element_users(CTSimpleFieldImpl.SUBDOC$64, (List)list);
            final CTRel[] array = new CTRel[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRel getSubDocArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRel ctRel = (CTRel)this.get_store().find_element_user(CTSimpleFieldImpl.SUBDOC$64, n);
            if (ctRel == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRel;
        }
    }
    
    public int sizeOfSubDocArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSimpleFieldImpl.SUBDOC$64);
        }
    }
    
    public void setSubDocArray(final CTRel[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSimpleFieldImpl.SUBDOC$64);
    }
    
    public void setSubDocArray(final int n, final CTRel ctRel) {
        this.generatedSetterHelperImpl((XmlObject)ctRel, CTSimpleFieldImpl.SUBDOC$64, n, (short)2);
    }
    
    public CTRel insertNewSubDoc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRel)this.get_store().insert_element_user(CTSimpleFieldImpl.SUBDOC$64, n);
        }
    }
    
    public CTRel addNewSubDoc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRel)this.get_store().add_element_user(CTSimpleFieldImpl.SUBDOC$64);
        }
    }
    
    public void removeSubDoc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSimpleFieldImpl.SUBDOC$64, n);
        }
    }
    
    public String getInstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSimpleFieldImpl.INSTR$66);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetInstr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTSimpleFieldImpl.INSTR$66);
        }
    }
    
    public void setInstr(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSimpleFieldImpl.INSTR$66);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSimpleFieldImpl.INSTR$66);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetInstr(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTSimpleFieldImpl.INSTR$66);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTSimpleFieldImpl.INSTR$66);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public STOnOff.Enum getFldLock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSimpleFieldImpl.FLDLOCK$68);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetFldLock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTSimpleFieldImpl.FLDLOCK$68);
        }
    }
    
    public boolean isSetFldLock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSimpleFieldImpl.FLDLOCK$68) != null;
        }
    }
    
    public void setFldLock(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSimpleFieldImpl.FLDLOCK$68);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSimpleFieldImpl.FLDLOCK$68);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFldLock(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTSimpleFieldImpl.FLDLOCK$68);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTSimpleFieldImpl.FLDLOCK$68);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetFldLock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSimpleFieldImpl.FLDLOCK$68);
        }
    }
    
    public STOnOff.Enum getDirty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSimpleFieldImpl.DIRTY$70);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetDirty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTSimpleFieldImpl.DIRTY$70);
        }
    }
    
    public boolean isSetDirty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSimpleFieldImpl.DIRTY$70) != null;
        }
    }
    
    public void setDirty(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSimpleFieldImpl.DIRTY$70);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSimpleFieldImpl.DIRTY$70);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDirty(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTSimpleFieldImpl.DIRTY$70);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTSimpleFieldImpl.DIRTY$70);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetDirty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSimpleFieldImpl.DIRTY$70);
        }
    }
    
    static {
        FLDDATA$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "fldData");
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
        INSTR$66 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "instr");
        FLDLOCK$68 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "fldLock");
        DIRTY$70 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "dirty");
    }
}
