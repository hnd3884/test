package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STString;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRel;
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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSmartTagRun;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCustomXmlRun;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTHyperlinkImpl extends XmlComplexContentImpl implements CTHyperlink
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
    private static final QName FLDSIMPLE$58;
    private static final QName HYPERLINK$60;
    private static final QName SUBDOC$62;
    private static final QName TGTFRAME$64;
    private static final QName TOOLTIP$66;
    private static final QName DOCLOCATION$68;
    private static final QName HISTORY$70;
    private static final QName ANCHOR$72;
    private static final QName ID$74;
    
    public CTHyperlinkImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTCustomXmlRun> getCustomXmlList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlList extends AbstractList<CTCustomXmlRun>
            {
                @Override
                public CTCustomXmlRun get(final int n) {
                    return CTHyperlinkImpl.this.getCustomXmlArray(n);
                }
                
                @Override
                public CTCustomXmlRun set(final int n, final CTCustomXmlRun ctCustomXmlRun) {
                    final CTCustomXmlRun customXmlArray = CTHyperlinkImpl.this.getCustomXmlArray(n);
                    CTHyperlinkImpl.this.setCustomXmlArray(n, ctCustomXmlRun);
                    return customXmlArray;
                }
                
                @Override
                public void add(final int n, final CTCustomXmlRun ctCustomXmlRun) {
                    CTHyperlinkImpl.this.insertNewCustomXml(n).set((XmlObject)ctCustomXmlRun);
                }
                
                @Override
                public CTCustomXmlRun remove(final int n) {
                    final CTCustomXmlRun customXmlArray = CTHyperlinkImpl.this.getCustomXmlArray(n);
                    CTHyperlinkImpl.this.removeCustomXml(n);
                    return customXmlArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfCustomXmlArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.CUSTOMXML$0, (List)list);
            final CTCustomXmlRun[] array = new CTCustomXmlRun[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCustomXmlRun getCustomXmlArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomXmlRun ctCustomXmlRun = (CTCustomXmlRun)this.get_store().find_element_user(CTHyperlinkImpl.CUSTOMXML$0, n);
            if (ctCustomXmlRun == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCustomXmlRun;
        }
    }
    
    public int sizeOfCustomXmlArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.CUSTOMXML$0);
        }
    }
    
    public void setCustomXmlArray(final CTCustomXmlRun[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.CUSTOMXML$0);
    }
    
    public void setCustomXmlArray(final int n, final CTCustomXmlRun ctCustomXmlRun) {
        this.generatedSetterHelperImpl((XmlObject)ctCustomXmlRun, CTHyperlinkImpl.CUSTOMXML$0, n, (short)2);
    }
    
    public CTCustomXmlRun insertNewCustomXml(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomXmlRun)this.get_store().insert_element_user(CTHyperlinkImpl.CUSTOMXML$0, n);
        }
    }
    
    public CTCustomXmlRun addNewCustomXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomXmlRun)this.get_store().add_element_user(CTHyperlinkImpl.CUSTOMXML$0);
        }
    }
    
    public void removeCustomXml(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.CUSTOMXML$0, n);
        }
    }
    
    public List<CTSmartTagRun> getSmartTagList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SmartTagList extends AbstractList<CTSmartTagRun>
            {
                @Override
                public CTSmartTagRun get(final int n) {
                    return CTHyperlinkImpl.this.getSmartTagArray(n);
                }
                
                @Override
                public CTSmartTagRun set(final int n, final CTSmartTagRun ctSmartTagRun) {
                    final CTSmartTagRun smartTagArray = CTHyperlinkImpl.this.getSmartTagArray(n);
                    CTHyperlinkImpl.this.setSmartTagArray(n, ctSmartTagRun);
                    return smartTagArray;
                }
                
                @Override
                public void add(final int n, final CTSmartTagRun ctSmartTagRun) {
                    CTHyperlinkImpl.this.insertNewSmartTag(n).set((XmlObject)ctSmartTagRun);
                }
                
                @Override
                public CTSmartTagRun remove(final int n) {
                    final CTSmartTagRun smartTagArray = CTHyperlinkImpl.this.getSmartTagArray(n);
                    CTHyperlinkImpl.this.removeSmartTag(n);
                    return smartTagArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfSmartTagArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.SMARTTAG$2, (List)list);
            final CTSmartTagRun[] array = new CTSmartTagRun[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSmartTagRun getSmartTagArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSmartTagRun ctSmartTagRun = (CTSmartTagRun)this.get_store().find_element_user(CTHyperlinkImpl.SMARTTAG$2, n);
            if (ctSmartTagRun == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSmartTagRun;
        }
    }
    
    public int sizeOfSmartTagArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.SMARTTAG$2);
        }
    }
    
    public void setSmartTagArray(final CTSmartTagRun[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.SMARTTAG$2);
    }
    
    public void setSmartTagArray(final int n, final CTSmartTagRun ctSmartTagRun) {
        this.generatedSetterHelperImpl((XmlObject)ctSmartTagRun, CTHyperlinkImpl.SMARTTAG$2, n, (short)2);
    }
    
    public CTSmartTagRun insertNewSmartTag(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSmartTagRun)this.get_store().insert_element_user(CTHyperlinkImpl.SMARTTAG$2, n);
        }
    }
    
    public CTSmartTagRun addNewSmartTag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSmartTagRun)this.get_store().add_element_user(CTHyperlinkImpl.SMARTTAG$2);
        }
    }
    
    public void removeSmartTag(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.SMARTTAG$2, n);
        }
    }
    
    public List<CTSdtRun> getSdtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SdtList extends AbstractList<CTSdtRun>
            {
                @Override
                public CTSdtRun get(final int n) {
                    return CTHyperlinkImpl.this.getSdtArray(n);
                }
                
                @Override
                public CTSdtRun set(final int n, final CTSdtRun ctSdtRun) {
                    final CTSdtRun sdtArray = CTHyperlinkImpl.this.getSdtArray(n);
                    CTHyperlinkImpl.this.setSdtArray(n, ctSdtRun);
                    return sdtArray;
                }
                
                @Override
                public void add(final int n, final CTSdtRun ctSdtRun) {
                    CTHyperlinkImpl.this.insertNewSdt(n).set((XmlObject)ctSdtRun);
                }
                
                @Override
                public CTSdtRun remove(final int n) {
                    final CTSdtRun sdtArray = CTHyperlinkImpl.this.getSdtArray(n);
                    CTHyperlinkImpl.this.removeSdt(n);
                    return sdtArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfSdtArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.SDT$4, (List)list);
            final CTSdtRun[] array = new CTSdtRun[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSdtRun getSdtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtRun ctSdtRun = (CTSdtRun)this.get_store().find_element_user(CTHyperlinkImpl.SDT$4, n);
            if (ctSdtRun == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSdtRun;
        }
    }
    
    public int sizeOfSdtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.SDT$4);
        }
    }
    
    public void setSdtArray(final CTSdtRun[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.SDT$4);
    }
    
    public void setSdtArray(final int n, final CTSdtRun ctSdtRun) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtRun, CTHyperlinkImpl.SDT$4, n, (short)2);
    }
    
    public CTSdtRun insertNewSdt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtRun)this.get_store().insert_element_user(CTHyperlinkImpl.SDT$4, n);
        }
    }
    
    public CTSdtRun addNewSdt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtRun)this.get_store().add_element_user(CTHyperlinkImpl.SDT$4);
        }
    }
    
    public void removeSdt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.SDT$4, n);
        }
    }
    
    public List<CTR> getRList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RList extends AbstractList<CTR>
            {
                @Override
                public CTR get(final int n) {
                    return CTHyperlinkImpl.this.getRArray(n);
                }
                
                @Override
                public CTR set(final int n, final CTR ctr) {
                    final CTR rArray = CTHyperlinkImpl.this.getRArray(n);
                    CTHyperlinkImpl.this.setRArray(n, ctr);
                    return rArray;
                }
                
                @Override
                public void add(final int n, final CTR ctr) {
                    CTHyperlinkImpl.this.insertNewR(n).set((XmlObject)ctr);
                }
                
                @Override
                public CTR remove(final int n) {
                    final CTR rArray = CTHyperlinkImpl.this.getRArray(n);
                    CTHyperlinkImpl.this.removeR(n);
                    return rArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfRArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.R$6, (List)list);
            final CTR[] array = new CTR[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTR getRArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTR ctr = (CTR)this.get_store().find_element_user(CTHyperlinkImpl.R$6, n);
            if (ctr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctr;
        }
    }
    
    public int sizeOfRArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.R$6);
        }
    }
    
    public void setRArray(final CTR[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.R$6);
    }
    
    public void setRArray(final int n, final CTR ctr) {
        this.generatedSetterHelperImpl((XmlObject)ctr, CTHyperlinkImpl.R$6, n, (short)2);
    }
    
    public CTR insertNewR(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTR)this.get_store().insert_element_user(CTHyperlinkImpl.R$6, n);
        }
    }
    
    public CTR addNewR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTR)this.get_store().add_element_user(CTHyperlinkImpl.R$6);
        }
    }
    
    public void removeR(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.R$6, n);
        }
    }
    
    public List<CTProofErr> getProofErrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ProofErrList extends AbstractList<CTProofErr>
            {
                @Override
                public CTProofErr get(final int n) {
                    return CTHyperlinkImpl.this.getProofErrArray(n);
                }
                
                @Override
                public CTProofErr set(final int n, final CTProofErr ctProofErr) {
                    final CTProofErr proofErrArray = CTHyperlinkImpl.this.getProofErrArray(n);
                    CTHyperlinkImpl.this.setProofErrArray(n, ctProofErr);
                    return proofErrArray;
                }
                
                @Override
                public void add(final int n, final CTProofErr ctProofErr) {
                    CTHyperlinkImpl.this.insertNewProofErr(n).set((XmlObject)ctProofErr);
                }
                
                @Override
                public CTProofErr remove(final int n) {
                    final CTProofErr proofErrArray = CTHyperlinkImpl.this.getProofErrArray(n);
                    CTHyperlinkImpl.this.removeProofErr(n);
                    return proofErrArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfProofErrArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.PROOFERR$8, (List)list);
            final CTProofErr[] array = new CTProofErr[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTProofErr getProofErrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTProofErr ctProofErr = (CTProofErr)this.get_store().find_element_user(CTHyperlinkImpl.PROOFERR$8, n);
            if (ctProofErr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctProofErr;
        }
    }
    
    public int sizeOfProofErrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.PROOFERR$8);
        }
    }
    
    public void setProofErrArray(final CTProofErr[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.PROOFERR$8);
    }
    
    public void setProofErrArray(final int n, final CTProofErr ctProofErr) {
        this.generatedSetterHelperImpl((XmlObject)ctProofErr, CTHyperlinkImpl.PROOFERR$8, n, (short)2);
    }
    
    public CTProofErr insertNewProofErr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProofErr)this.get_store().insert_element_user(CTHyperlinkImpl.PROOFERR$8, n);
        }
    }
    
    public CTProofErr addNewProofErr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProofErr)this.get_store().add_element_user(CTHyperlinkImpl.PROOFERR$8);
        }
    }
    
    public void removeProofErr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.PROOFERR$8, n);
        }
    }
    
    public List<CTPermStart> getPermStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PermStartList extends AbstractList<CTPermStart>
            {
                @Override
                public CTPermStart get(final int n) {
                    return CTHyperlinkImpl.this.getPermStartArray(n);
                }
                
                @Override
                public CTPermStart set(final int n, final CTPermStart ctPermStart) {
                    final CTPermStart permStartArray = CTHyperlinkImpl.this.getPermStartArray(n);
                    CTHyperlinkImpl.this.setPermStartArray(n, ctPermStart);
                    return permStartArray;
                }
                
                @Override
                public void add(final int n, final CTPermStart ctPermStart) {
                    CTHyperlinkImpl.this.insertNewPermStart(n).set((XmlObject)ctPermStart);
                }
                
                @Override
                public CTPermStart remove(final int n) {
                    final CTPermStart permStartArray = CTHyperlinkImpl.this.getPermStartArray(n);
                    CTHyperlinkImpl.this.removePermStart(n);
                    return permStartArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfPermStartArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.PERMSTART$10, (List)list);
            final CTPermStart[] array = new CTPermStart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPermStart getPermStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPermStart ctPermStart = (CTPermStart)this.get_store().find_element_user(CTHyperlinkImpl.PERMSTART$10, n);
            if (ctPermStart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPermStart;
        }
    }
    
    public int sizeOfPermStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.PERMSTART$10);
        }
    }
    
    public void setPermStartArray(final CTPermStart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.PERMSTART$10);
    }
    
    public void setPermStartArray(final int n, final CTPermStart ctPermStart) {
        this.generatedSetterHelperImpl((XmlObject)ctPermStart, CTHyperlinkImpl.PERMSTART$10, n, (short)2);
    }
    
    public CTPermStart insertNewPermStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPermStart)this.get_store().insert_element_user(CTHyperlinkImpl.PERMSTART$10, n);
        }
    }
    
    public CTPermStart addNewPermStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPermStart)this.get_store().add_element_user(CTHyperlinkImpl.PERMSTART$10);
        }
    }
    
    public void removePermStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.PERMSTART$10, n);
        }
    }
    
    public List<CTPerm> getPermEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PermEndList extends AbstractList<CTPerm>
            {
                @Override
                public CTPerm get(final int n) {
                    return CTHyperlinkImpl.this.getPermEndArray(n);
                }
                
                @Override
                public CTPerm set(final int n, final CTPerm ctPerm) {
                    final CTPerm permEndArray = CTHyperlinkImpl.this.getPermEndArray(n);
                    CTHyperlinkImpl.this.setPermEndArray(n, ctPerm);
                    return permEndArray;
                }
                
                @Override
                public void add(final int n, final CTPerm ctPerm) {
                    CTHyperlinkImpl.this.insertNewPermEnd(n).set((XmlObject)ctPerm);
                }
                
                @Override
                public CTPerm remove(final int n) {
                    final CTPerm permEndArray = CTHyperlinkImpl.this.getPermEndArray(n);
                    CTHyperlinkImpl.this.removePermEnd(n);
                    return permEndArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfPermEndArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.PERMEND$12, (List)list);
            final CTPerm[] array = new CTPerm[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPerm getPermEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPerm ctPerm = (CTPerm)this.get_store().find_element_user(CTHyperlinkImpl.PERMEND$12, n);
            if (ctPerm == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPerm;
        }
    }
    
    public int sizeOfPermEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.PERMEND$12);
        }
    }
    
    public void setPermEndArray(final CTPerm[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.PERMEND$12);
    }
    
    public void setPermEndArray(final int n, final CTPerm ctPerm) {
        this.generatedSetterHelperImpl((XmlObject)ctPerm, CTHyperlinkImpl.PERMEND$12, n, (short)2);
    }
    
    public CTPerm insertNewPermEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerm)this.get_store().insert_element_user(CTHyperlinkImpl.PERMEND$12, n);
        }
    }
    
    public CTPerm addNewPermEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerm)this.get_store().add_element_user(CTHyperlinkImpl.PERMEND$12);
        }
    }
    
    public void removePermEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.PERMEND$12, n);
        }
    }
    
    public List<CTBookmark> getBookmarkStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BookmarkStartList extends AbstractList<CTBookmark>
            {
                @Override
                public CTBookmark get(final int n) {
                    return CTHyperlinkImpl.this.getBookmarkStartArray(n);
                }
                
                @Override
                public CTBookmark set(final int n, final CTBookmark ctBookmark) {
                    final CTBookmark bookmarkStartArray = CTHyperlinkImpl.this.getBookmarkStartArray(n);
                    CTHyperlinkImpl.this.setBookmarkStartArray(n, ctBookmark);
                    return bookmarkStartArray;
                }
                
                @Override
                public void add(final int n, final CTBookmark ctBookmark) {
                    CTHyperlinkImpl.this.insertNewBookmarkStart(n).set((XmlObject)ctBookmark);
                }
                
                @Override
                public CTBookmark remove(final int n) {
                    final CTBookmark bookmarkStartArray = CTHyperlinkImpl.this.getBookmarkStartArray(n);
                    CTHyperlinkImpl.this.removeBookmarkStart(n);
                    return bookmarkStartArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfBookmarkStartArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.BOOKMARKSTART$14, (List)list);
            final CTBookmark[] array = new CTBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBookmark getBookmarkStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBookmark ctBookmark = (CTBookmark)this.get_store().find_element_user(CTHyperlinkImpl.BOOKMARKSTART$14, n);
            if (ctBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBookmark;
        }
    }
    
    public int sizeOfBookmarkStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.BOOKMARKSTART$14);
        }
    }
    
    public void setBookmarkStartArray(final CTBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.BOOKMARKSTART$14);
    }
    
    public void setBookmarkStartArray(final int n, final CTBookmark ctBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctBookmark, CTHyperlinkImpl.BOOKMARKSTART$14, n, (short)2);
    }
    
    public CTBookmark insertNewBookmarkStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookmark)this.get_store().insert_element_user(CTHyperlinkImpl.BOOKMARKSTART$14, n);
        }
    }
    
    public CTBookmark addNewBookmarkStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookmark)this.get_store().add_element_user(CTHyperlinkImpl.BOOKMARKSTART$14);
        }
    }
    
    public void removeBookmarkStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.BOOKMARKSTART$14, n);
        }
    }
    
    public List<CTMarkupRange> getBookmarkEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BookmarkEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTHyperlinkImpl.this.getBookmarkEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange bookmarkEndArray = CTHyperlinkImpl.this.getBookmarkEndArray(n);
                    CTHyperlinkImpl.this.setBookmarkEndArray(n, ctMarkupRange);
                    return bookmarkEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTHyperlinkImpl.this.insertNewBookmarkEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange bookmarkEndArray = CTHyperlinkImpl.this.getBookmarkEndArray(n);
                    CTHyperlinkImpl.this.removeBookmarkEnd(n);
                    return bookmarkEndArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfBookmarkEndArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.BOOKMARKEND$16, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getBookmarkEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTHyperlinkImpl.BOOKMARKEND$16, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfBookmarkEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.BOOKMARKEND$16);
        }
    }
    
    public void setBookmarkEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.BOOKMARKEND$16);
    }
    
    public void setBookmarkEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTHyperlinkImpl.BOOKMARKEND$16, n, (short)2);
    }
    
    public CTMarkupRange insertNewBookmarkEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTHyperlinkImpl.BOOKMARKEND$16, n);
        }
    }
    
    public CTMarkupRange addNewBookmarkEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTHyperlinkImpl.BOOKMARKEND$16);
        }
    }
    
    public void removeBookmarkEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.BOOKMARKEND$16, n);
        }
    }
    
    public List<CTMoveBookmark> getMoveFromRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromRangeStartList extends AbstractList<CTMoveBookmark>
            {
                @Override
                public CTMoveBookmark get(final int n) {
                    return CTHyperlinkImpl.this.getMoveFromRangeStartArray(n);
                }
                
                @Override
                public CTMoveBookmark set(final int n, final CTMoveBookmark ctMoveBookmark) {
                    final CTMoveBookmark moveFromRangeStartArray = CTHyperlinkImpl.this.getMoveFromRangeStartArray(n);
                    CTHyperlinkImpl.this.setMoveFromRangeStartArray(n, ctMoveBookmark);
                    return moveFromRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMoveBookmark ctMoveBookmark) {
                    CTHyperlinkImpl.this.insertNewMoveFromRangeStart(n).set((XmlObject)ctMoveBookmark);
                }
                
                @Override
                public CTMoveBookmark remove(final int n) {
                    final CTMoveBookmark moveFromRangeStartArray = CTHyperlinkImpl.this.getMoveFromRangeStartArray(n);
                    CTHyperlinkImpl.this.removeMoveFromRangeStart(n);
                    return moveFromRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfMoveFromRangeStartArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.MOVEFROMRANGESTART$18, (List)list);
            final CTMoveBookmark[] array = new CTMoveBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMoveBookmark getMoveFromRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMoveBookmark ctMoveBookmark = (CTMoveBookmark)this.get_store().find_element_user(CTHyperlinkImpl.MOVEFROMRANGESTART$18, n);
            if (ctMoveBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMoveBookmark;
        }
    }
    
    public int sizeOfMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.MOVEFROMRANGESTART$18);
        }
    }
    
    public void setMoveFromRangeStartArray(final CTMoveBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.MOVEFROMRANGESTART$18);
    }
    
    public void setMoveFromRangeStartArray(final int n, final CTMoveBookmark ctMoveBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctMoveBookmark, CTHyperlinkImpl.MOVEFROMRANGESTART$18, n, (short)2);
    }
    
    public CTMoveBookmark insertNewMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().insert_element_user(CTHyperlinkImpl.MOVEFROMRANGESTART$18, n);
        }
    }
    
    public CTMoveBookmark addNewMoveFromRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().add_element_user(CTHyperlinkImpl.MOVEFROMRANGESTART$18);
        }
    }
    
    public void removeMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.MOVEFROMRANGESTART$18, n);
        }
    }
    
    public List<CTMarkupRange> getMoveFromRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTHyperlinkImpl.this.getMoveFromRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange moveFromRangeEndArray = CTHyperlinkImpl.this.getMoveFromRangeEndArray(n);
                    CTHyperlinkImpl.this.setMoveFromRangeEndArray(n, ctMarkupRange);
                    return moveFromRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTHyperlinkImpl.this.insertNewMoveFromRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange moveFromRangeEndArray = CTHyperlinkImpl.this.getMoveFromRangeEndArray(n);
                    CTHyperlinkImpl.this.removeMoveFromRangeEnd(n);
                    return moveFromRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfMoveFromRangeEndArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.MOVEFROMRANGEEND$20, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getMoveFromRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTHyperlinkImpl.MOVEFROMRANGEEND$20, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.MOVEFROMRANGEEND$20);
        }
    }
    
    public void setMoveFromRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.MOVEFROMRANGEEND$20);
    }
    
    public void setMoveFromRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTHyperlinkImpl.MOVEFROMRANGEEND$20, n, (short)2);
    }
    
    public CTMarkupRange insertNewMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTHyperlinkImpl.MOVEFROMRANGEEND$20, n);
        }
    }
    
    public CTMarkupRange addNewMoveFromRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTHyperlinkImpl.MOVEFROMRANGEEND$20);
        }
    }
    
    public void removeMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.MOVEFROMRANGEEND$20, n);
        }
    }
    
    public List<CTMoveBookmark> getMoveToRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToRangeStartList extends AbstractList<CTMoveBookmark>
            {
                @Override
                public CTMoveBookmark get(final int n) {
                    return CTHyperlinkImpl.this.getMoveToRangeStartArray(n);
                }
                
                @Override
                public CTMoveBookmark set(final int n, final CTMoveBookmark ctMoveBookmark) {
                    final CTMoveBookmark moveToRangeStartArray = CTHyperlinkImpl.this.getMoveToRangeStartArray(n);
                    CTHyperlinkImpl.this.setMoveToRangeStartArray(n, ctMoveBookmark);
                    return moveToRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMoveBookmark ctMoveBookmark) {
                    CTHyperlinkImpl.this.insertNewMoveToRangeStart(n).set((XmlObject)ctMoveBookmark);
                }
                
                @Override
                public CTMoveBookmark remove(final int n) {
                    final CTMoveBookmark moveToRangeStartArray = CTHyperlinkImpl.this.getMoveToRangeStartArray(n);
                    CTHyperlinkImpl.this.removeMoveToRangeStart(n);
                    return moveToRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfMoveToRangeStartArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.MOVETORANGESTART$22, (List)list);
            final CTMoveBookmark[] array = new CTMoveBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMoveBookmark getMoveToRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMoveBookmark ctMoveBookmark = (CTMoveBookmark)this.get_store().find_element_user(CTHyperlinkImpl.MOVETORANGESTART$22, n);
            if (ctMoveBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMoveBookmark;
        }
    }
    
    public int sizeOfMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.MOVETORANGESTART$22);
        }
    }
    
    public void setMoveToRangeStartArray(final CTMoveBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.MOVETORANGESTART$22);
    }
    
    public void setMoveToRangeStartArray(final int n, final CTMoveBookmark ctMoveBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctMoveBookmark, CTHyperlinkImpl.MOVETORANGESTART$22, n, (short)2);
    }
    
    public CTMoveBookmark insertNewMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().insert_element_user(CTHyperlinkImpl.MOVETORANGESTART$22, n);
        }
    }
    
    public CTMoveBookmark addNewMoveToRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().add_element_user(CTHyperlinkImpl.MOVETORANGESTART$22);
        }
    }
    
    public void removeMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.MOVETORANGESTART$22, n);
        }
    }
    
    public List<CTMarkupRange> getMoveToRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTHyperlinkImpl.this.getMoveToRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange moveToRangeEndArray = CTHyperlinkImpl.this.getMoveToRangeEndArray(n);
                    CTHyperlinkImpl.this.setMoveToRangeEndArray(n, ctMarkupRange);
                    return moveToRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTHyperlinkImpl.this.insertNewMoveToRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange moveToRangeEndArray = CTHyperlinkImpl.this.getMoveToRangeEndArray(n);
                    CTHyperlinkImpl.this.removeMoveToRangeEnd(n);
                    return moveToRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfMoveToRangeEndArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.MOVETORANGEEND$24, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getMoveToRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTHyperlinkImpl.MOVETORANGEEND$24, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.MOVETORANGEEND$24);
        }
    }
    
    public void setMoveToRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.MOVETORANGEEND$24);
    }
    
    public void setMoveToRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTHyperlinkImpl.MOVETORANGEEND$24, n, (short)2);
    }
    
    public CTMarkupRange insertNewMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTHyperlinkImpl.MOVETORANGEEND$24, n);
        }
    }
    
    public CTMarkupRange addNewMoveToRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTHyperlinkImpl.MOVETORANGEEND$24);
        }
    }
    
    public void removeMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.MOVETORANGEEND$24, n);
        }
    }
    
    public List<CTMarkupRange> getCommentRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentRangeStartList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTHyperlinkImpl.this.getCommentRangeStartArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange commentRangeStartArray = CTHyperlinkImpl.this.getCommentRangeStartArray(n);
                    CTHyperlinkImpl.this.setCommentRangeStartArray(n, ctMarkupRange);
                    return commentRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTHyperlinkImpl.this.insertNewCommentRangeStart(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange commentRangeStartArray = CTHyperlinkImpl.this.getCommentRangeStartArray(n);
                    CTHyperlinkImpl.this.removeCommentRangeStart(n);
                    return commentRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfCommentRangeStartArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.COMMENTRANGESTART$26, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getCommentRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTHyperlinkImpl.COMMENTRANGESTART$26, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfCommentRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.COMMENTRANGESTART$26);
        }
    }
    
    public void setCommentRangeStartArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.COMMENTRANGESTART$26);
    }
    
    public void setCommentRangeStartArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTHyperlinkImpl.COMMENTRANGESTART$26, n, (short)2);
    }
    
    public CTMarkupRange insertNewCommentRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTHyperlinkImpl.COMMENTRANGESTART$26, n);
        }
    }
    
    public CTMarkupRange addNewCommentRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTHyperlinkImpl.COMMENTRANGESTART$26);
        }
    }
    
    public void removeCommentRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.COMMENTRANGESTART$26, n);
        }
    }
    
    public List<CTMarkupRange> getCommentRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTHyperlinkImpl.this.getCommentRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange commentRangeEndArray = CTHyperlinkImpl.this.getCommentRangeEndArray(n);
                    CTHyperlinkImpl.this.setCommentRangeEndArray(n, ctMarkupRange);
                    return commentRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTHyperlinkImpl.this.insertNewCommentRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange commentRangeEndArray = CTHyperlinkImpl.this.getCommentRangeEndArray(n);
                    CTHyperlinkImpl.this.removeCommentRangeEnd(n);
                    return commentRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfCommentRangeEndArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.COMMENTRANGEEND$28, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getCommentRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTHyperlinkImpl.COMMENTRANGEEND$28, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfCommentRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.COMMENTRANGEEND$28);
        }
    }
    
    public void setCommentRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.COMMENTRANGEEND$28);
    }
    
    public void setCommentRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTHyperlinkImpl.COMMENTRANGEEND$28, n, (short)2);
    }
    
    public CTMarkupRange insertNewCommentRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTHyperlinkImpl.COMMENTRANGEEND$28, n);
        }
    }
    
    public CTMarkupRange addNewCommentRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTHyperlinkImpl.COMMENTRANGEEND$28);
        }
    }
    
    public void removeCommentRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.COMMENTRANGEEND$28, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlInsRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlInsRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTHyperlinkImpl.this.getCustomXmlInsRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlInsRangeStartArray = CTHyperlinkImpl.this.getCustomXmlInsRangeStartArray(n);
                    CTHyperlinkImpl.this.setCustomXmlInsRangeStartArray(n, ctTrackChange);
                    return customXmlInsRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTHyperlinkImpl.this.insertNewCustomXmlInsRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlInsRangeStartArray = CTHyperlinkImpl.this.getCustomXmlInsRangeStartArray(n);
                    CTHyperlinkImpl.this.removeCustomXmlInsRangeStart(n);
                    return customXmlInsRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfCustomXmlInsRangeStartArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.CUSTOMXMLINSRANGESTART$30, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlInsRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTHyperlinkImpl.CUSTOMXMLINSRANGESTART$30, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlInsRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.CUSTOMXMLINSRANGESTART$30);
        }
    }
    
    public void setCustomXmlInsRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.CUSTOMXMLINSRANGESTART$30);
    }
    
    public void setCustomXmlInsRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTHyperlinkImpl.CUSTOMXMLINSRANGESTART$30, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlInsRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTHyperlinkImpl.CUSTOMXMLINSRANGESTART$30, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlInsRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTHyperlinkImpl.CUSTOMXMLINSRANGESTART$30);
        }
    }
    
    public void removeCustomXmlInsRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.CUSTOMXMLINSRANGESTART$30, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlInsRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlInsRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTHyperlinkImpl.this.getCustomXmlInsRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlInsRangeEndArray = CTHyperlinkImpl.this.getCustomXmlInsRangeEndArray(n);
                    CTHyperlinkImpl.this.setCustomXmlInsRangeEndArray(n, ctMarkup);
                    return customXmlInsRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTHyperlinkImpl.this.insertNewCustomXmlInsRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlInsRangeEndArray = CTHyperlinkImpl.this.getCustomXmlInsRangeEndArray(n);
                    CTHyperlinkImpl.this.removeCustomXmlInsRangeEnd(n);
                    return customXmlInsRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfCustomXmlInsRangeEndArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.CUSTOMXMLINSRANGEEND$32, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlInsRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTHyperlinkImpl.CUSTOMXMLINSRANGEEND$32, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlInsRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.CUSTOMXMLINSRANGEEND$32);
        }
    }
    
    public void setCustomXmlInsRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.CUSTOMXMLINSRANGEEND$32);
    }
    
    public void setCustomXmlInsRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTHyperlinkImpl.CUSTOMXMLINSRANGEEND$32, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlInsRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTHyperlinkImpl.CUSTOMXMLINSRANGEEND$32, n);
        }
    }
    
    public CTMarkup addNewCustomXmlInsRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTHyperlinkImpl.CUSTOMXMLINSRANGEEND$32);
        }
    }
    
    public void removeCustomXmlInsRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.CUSTOMXMLINSRANGEEND$32, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlDelRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlDelRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTHyperlinkImpl.this.getCustomXmlDelRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlDelRangeStartArray = CTHyperlinkImpl.this.getCustomXmlDelRangeStartArray(n);
                    CTHyperlinkImpl.this.setCustomXmlDelRangeStartArray(n, ctTrackChange);
                    return customXmlDelRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTHyperlinkImpl.this.insertNewCustomXmlDelRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlDelRangeStartArray = CTHyperlinkImpl.this.getCustomXmlDelRangeStartArray(n);
                    CTHyperlinkImpl.this.removeCustomXmlDelRangeStart(n);
                    return customXmlDelRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfCustomXmlDelRangeStartArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.CUSTOMXMLDELRANGESTART$34, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlDelRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTHyperlinkImpl.CUSTOMXMLDELRANGESTART$34, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlDelRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.CUSTOMXMLDELRANGESTART$34);
        }
    }
    
    public void setCustomXmlDelRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.CUSTOMXMLDELRANGESTART$34);
    }
    
    public void setCustomXmlDelRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTHyperlinkImpl.CUSTOMXMLDELRANGESTART$34, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlDelRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTHyperlinkImpl.CUSTOMXMLDELRANGESTART$34, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlDelRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTHyperlinkImpl.CUSTOMXMLDELRANGESTART$34);
        }
    }
    
    public void removeCustomXmlDelRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.CUSTOMXMLDELRANGESTART$34, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlDelRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlDelRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTHyperlinkImpl.this.getCustomXmlDelRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlDelRangeEndArray = CTHyperlinkImpl.this.getCustomXmlDelRangeEndArray(n);
                    CTHyperlinkImpl.this.setCustomXmlDelRangeEndArray(n, ctMarkup);
                    return customXmlDelRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTHyperlinkImpl.this.insertNewCustomXmlDelRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlDelRangeEndArray = CTHyperlinkImpl.this.getCustomXmlDelRangeEndArray(n);
                    CTHyperlinkImpl.this.removeCustomXmlDelRangeEnd(n);
                    return customXmlDelRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfCustomXmlDelRangeEndArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.CUSTOMXMLDELRANGEEND$36, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlDelRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTHyperlinkImpl.CUSTOMXMLDELRANGEEND$36, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlDelRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.CUSTOMXMLDELRANGEEND$36);
        }
    }
    
    public void setCustomXmlDelRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.CUSTOMXMLDELRANGEEND$36);
    }
    
    public void setCustomXmlDelRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTHyperlinkImpl.CUSTOMXMLDELRANGEEND$36, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlDelRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTHyperlinkImpl.CUSTOMXMLDELRANGEEND$36, n);
        }
    }
    
    public CTMarkup addNewCustomXmlDelRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTHyperlinkImpl.CUSTOMXMLDELRANGEEND$36);
        }
    }
    
    public void removeCustomXmlDelRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.CUSTOMXMLDELRANGEEND$36, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlMoveFromRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveFromRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTHyperlinkImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlMoveFromRangeStartArray = CTHyperlinkImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                    CTHyperlinkImpl.this.setCustomXmlMoveFromRangeStartArray(n, ctTrackChange);
                    return customXmlMoveFromRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTHyperlinkImpl.this.insertNewCustomXmlMoveFromRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlMoveFromRangeStartArray = CTHyperlinkImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                    CTHyperlinkImpl.this.removeCustomXmlMoveFromRangeStart(n);
                    return customXmlMoveFromRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfCustomXmlMoveFromRangeStartArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.CUSTOMXMLMOVEFROMRANGESTART$38, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlMoveFromRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTHyperlinkImpl.CUSTOMXMLMOVEFROMRANGESTART$38, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.CUSTOMXMLMOVEFROMRANGESTART$38);
        }
    }
    
    public void setCustomXmlMoveFromRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.CUSTOMXMLMOVEFROMRANGESTART$38);
    }
    
    public void setCustomXmlMoveFromRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTHyperlinkImpl.CUSTOMXMLMOVEFROMRANGESTART$38, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTHyperlinkImpl.CUSTOMXMLMOVEFROMRANGESTART$38, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlMoveFromRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTHyperlinkImpl.CUSTOMXMLMOVEFROMRANGESTART$38);
        }
    }
    
    public void removeCustomXmlMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.CUSTOMXMLMOVEFROMRANGESTART$38, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlMoveFromRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveFromRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTHyperlinkImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlMoveFromRangeEndArray = CTHyperlinkImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                    CTHyperlinkImpl.this.setCustomXmlMoveFromRangeEndArray(n, ctMarkup);
                    return customXmlMoveFromRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTHyperlinkImpl.this.insertNewCustomXmlMoveFromRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlMoveFromRangeEndArray = CTHyperlinkImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                    CTHyperlinkImpl.this.removeCustomXmlMoveFromRangeEnd(n);
                    return customXmlMoveFromRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfCustomXmlMoveFromRangeEndArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.CUSTOMXMLMOVEFROMRANGEEND$40, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlMoveFromRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTHyperlinkImpl.CUSTOMXMLMOVEFROMRANGEEND$40, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.CUSTOMXMLMOVEFROMRANGEEND$40);
        }
    }
    
    public void setCustomXmlMoveFromRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.CUSTOMXMLMOVEFROMRANGEEND$40);
    }
    
    public void setCustomXmlMoveFromRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTHyperlinkImpl.CUSTOMXMLMOVEFROMRANGEEND$40, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTHyperlinkImpl.CUSTOMXMLMOVEFROMRANGEEND$40, n);
        }
    }
    
    public CTMarkup addNewCustomXmlMoveFromRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTHyperlinkImpl.CUSTOMXMLMOVEFROMRANGEEND$40);
        }
    }
    
    public void removeCustomXmlMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.CUSTOMXMLMOVEFROMRANGEEND$40, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlMoveToRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveToRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTHyperlinkImpl.this.getCustomXmlMoveToRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlMoveToRangeStartArray = CTHyperlinkImpl.this.getCustomXmlMoveToRangeStartArray(n);
                    CTHyperlinkImpl.this.setCustomXmlMoveToRangeStartArray(n, ctTrackChange);
                    return customXmlMoveToRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTHyperlinkImpl.this.insertNewCustomXmlMoveToRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlMoveToRangeStartArray = CTHyperlinkImpl.this.getCustomXmlMoveToRangeStartArray(n);
                    CTHyperlinkImpl.this.removeCustomXmlMoveToRangeStart(n);
                    return customXmlMoveToRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfCustomXmlMoveToRangeStartArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.CUSTOMXMLMOVETORANGESTART$42, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlMoveToRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTHyperlinkImpl.CUSTOMXMLMOVETORANGESTART$42, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.CUSTOMXMLMOVETORANGESTART$42);
        }
    }
    
    public void setCustomXmlMoveToRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.CUSTOMXMLMOVETORANGESTART$42);
    }
    
    public void setCustomXmlMoveToRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTHyperlinkImpl.CUSTOMXMLMOVETORANGESTART$42, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTHyperlinkImpl.CUSTOMXMLMOVETORANGESTART$42, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlMoveToRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTHyperlinkImpl.CUSTOMXMLMOVETORANGESTART$42);
        }
    }
    
    public void removeCustomXmlMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.CUSTOMXMLMOVETORANGESTART$42, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlMoveToRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveToRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTHyperlinkImpl.this.getCustomXmlMoveToRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlMoveToRangeEndArray = CTHyperlinkImpl.this.getCustomXmlMoveToRangeEndArray(n);
                    CTHyperlinkImpl.this.setCustomXmlMoveToRangeEndArray(n, ctMarkup);
                    return customXmlMoveToRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTHyperlinkImpl.this.insertNewCustomXmlMoveToRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlMoveToRangeEndArray = CTHyperlinkImpl.this.getCustomXmlMoveToRangeEndArray(n);
                    CTHyperlinkImpl.this.removeCustomXmlMoveToRangeEnd(n);
                    return customXmlMoveToRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfCustomXmlMoveToRangeEndArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.CUSTOMXMLMOVETORANGEEND$44, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlMoveToRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTHyperlinkImpl.CUSTOMXMLMOVETORANGEEND$44, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.CUSTOMXMLMOVETORANGEEND$44);
        }
    }
    
    public void setCustomXmlMoveToRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.CUSTOMXMLMOVETORANGEEND$44);
    }
    
    public void setCustomXmlMoveToRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTHyperlinkImpl.CUSTOMXMLMOVETORANGEEND$44, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTHyperlinkImpl.CUSTOMXMLMOVETORANGEEND$44, n);
        }
    }
    
    public CTMarkup addNewCustomXmlMoveToRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTHyperlinkImpl.CUSTOMXMLMOVETORANGEEND$44);
        }
    }
    
    public void removeCustomXmlMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.CUSTOMXMLMOVETORANGEEND$44, n);
        }
    }
    
    public List<CTRunTrackChange> getInsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class InsList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTHyperlinkImpl.this.getInsArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange insArray = CTHyperlinkImpl.this.getInsArray(n);
                    CTHyperlinkImpl.this.setInsArray(n, ctRunTrackChange);
                    return insArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTHyperlinkImpl.this.insertNewIns(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange insArray = CTHyperlinkImpl.this.getInsArray(n);
                    CTHyperlinkImpl.this.removeIns(n);
                    return insArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfInsArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.INS$46, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getInsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTHyperlinkImpl.INS$46, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfInsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.INS$46);
        }
    }
    
    public void setInsArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.INS$46);
    }
    
    public void setInsArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTHyperlinkImpl.INS$46, n, (short)2);
    }
    
    public CTRunTrackChange insertNewIns(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTHyperlinkImpl.INS$46, n);
        }
    }
    
    public CTRunTrackChange addNewIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTHyperlinkImpl.INS$46);
        }
    }
    
    public void removeIns(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.INS$46, n);
        }
    }
    
    public List<CTRunTrackChange> getDelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DelList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTHyperlinkImpl.this.getDelArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange delArray = CTHyperlinkImpl.this.getDelArray(n);
                    CTHyperlinkImpl.this.setDelArray(n, ctRunTrackChange);
                    return delArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTHyperlinkImpl.this.insertNewDel(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange delArray = CTHyperlinkImpl.this.getDelArray(n);
                    CTHyperlinkImpl.this.removeDel(n);
                    return delArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfDelArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.DEL$48, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getDelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTHyperlinkImpl.DEL$48, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfDelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.DEL$48);
        }
    }
    
    public void setDelArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.DEL$48);
    }
    
    public void setDelArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTHyperlinkImpl.DEL$48, n, (short)2);
    }
    
    public CTRunTrackChange insertNewDel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTHyperlinkImpl.DEL$48, n);
        }
    }
    
    public CTRunTrackChange addNewDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTHyperlinkImpl.DEL$48);
        }
    }
    
    public void removeDel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.DEL$48, n);
        }
    }
    
    public List<CTRunTrackChange> getMoveFromList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTHyperlinkImpl.this.getMoveFromArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange moveFromArray = CTHyperlinkImpl.this.getMoveFromArray(n);
                    CTHyperlinkImpl.this.setMoveFromArray(n, ctRunTrackChange);
                    return moveFromArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTHyperlinkImpl.this.insertNewMoveFrom(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange moveFromArray = CTHyperlinkImpl.this.getMoveFromArray(n);
                    CTHyperlinkImpl.this.removeMoveFrom(n);
                    return moveFromArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfMoveFromArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.MOVEFROM$50, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getMoveFromArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTHyperlinkImpl.MOVEFROM$50, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfMoveFromArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.MOVEFROM$50);
        }
    }
    
    public void setMoveFromArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.MOVEFROM$50);
    }
    
    public void setMoveFromArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTHyperlinkImpl.MOVEFROM$50, n, (short)2);
    }
    
    public CTRunTrackChange insertNewMoveFrom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTHyperlinkImpl.MOVEFROM$50, n);
        }
    }
    
    public CTRunTrackChange addNewMoveFrom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTHyperlinkImpl.MOVEFROM$50);
        }
    }
    
    public void removeMoveFrom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.MOVEFROM$50, n);
        }
    }
    
    public List<CTRunTrackChange> getMoveToList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTHyperlinkImpl.this.getMoveToArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange moveToArray = CTHyperlinkImpl.this.getMoveToArray(n);
                    CTHyperlinkImpl.this.setMoveToArray(n, ctRunTrackChange);
                    return moveToArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTHyperlinkImpl.this.insertNewMoveTo(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange moveToArray = CTHyperlinkImpl.this.getMoveToArray(n);
                    CTHyperlinkImpl.this.removeMoveTo(n);
                    return moveToArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfMoveToArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.MOVETO$52, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getMoveToArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTHyperlinkImpl.MOVETO$52, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfMoveToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.MOVETO$52);
        }
    }
    
    public void setMoveToArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.MOVETO$52);
    }
    
    public void setMoveToArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTHyperlinkImpl.MOVETO$52, n, (short)2);
    }
    
    public CTRunTrackChange insertNewMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTHyperlinkImpl.MOVETO$52, n);
        }
    }
    
    public CTRunTrackChange addNewMoveTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTHyperlinkImpl.MOVETO$52);
        }
    }
    
    public void removeMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.MOVETO$52, n);
        }
    }
    
    public List<CTOMathPara> getOMathParaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OMathParaList extends AbstractList<CTOMathPara>
            {
                @Override
                public CTOMathPara get(final int n) {
                    return CTHyperlinkImpl.this.getOMathParaArray(n);
                }
                
                @Override
                public CTOMathPara set(final int n, final CTOMathPara ctoMathPara) {
                    final CTOMathPara oMathParaArray = CTHyperlinkImpl.this.getOMathParaArray(n);
                    CTHyperlinkImpl.this.setOMathParaArray(n, ctoMathPara);
                    return oMathParaArray;
                }
                
                @Override
                public void add(final int n, final CTOMathPara ctoMathPara) {
                    CTHyperlinkImpl.this.insertNewOMathPara(n).set((XmlObject)ctoMathPara);
                }
                
                @Override
                public CTOMathPara remove(final int n) {
                    final CTOMathPara oMathParaArray = CTHyperlinkImpl.this.getOMathParaArray(n);
                    CTHyperlinkImpl.this.removeOMathPara(n);
                    return oMathParaArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfOMathParaArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.OMATHPARA$54, (List)list);
            final CTOMathPara[] array = new CTOMathPara[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOMathPara getOMathParaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOMathPara ctoMathPara = (CTOMathPara)this.get_store().find_element_user(CTHyperlinkImpl.OMATHPARA$54, n);
            if (ctoMathPara == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctoMathPara;
        }
    }
    
    public int sizeOfOMathParaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.OMATHPARA$54);
        }
    }
    
    public void setOMathParaArray(final CTOMathPara[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.OMATHPARA$54);
    }
    
    public void setOMathParaArray(final int n, final CTOMathPara ctoMathPara) {
        this.generatedSetterHelperImpl((XmlObject)ctoMathPara, CTHyperlinkImpl.OMATHPARA$54, n, (short)2);
    }
    
    public CTOMathPara insertNewOMathPara(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMathPara)this.get_store().insert_element_user(CTHyperlinkImpl.OMATHPARA$54, n);
        }
    }
    
    public CTOMathPara addNewOMathPara() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMathPara)this.get_store().add_element_user(CTHyperlinkImpl.OMATHPARA$54);
        }
    }
    
    public void removeOMathPara(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.OMATHPARA$54, n);
        }
    }
    
    public List<CTOMath> getOMathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OMathList extends AbstractList<CTOMath>
            {
                @Override
                public CTOMath get(final int n) {
                    return CTHyperlinkImpl.this.getOMathArray(n);
                }
                
                @Override
                public CTOMath set(final int n, final CTOMath ctoMath) {
                    final CTOMath oMathArray = CTHyperlinkImpl.this.getOMathArray(n);
                    CTHyperlinkImpl.this.setOMathArray(n, ctoMath);
                    return oMathArray;
                }
                
                @Override
                public void add(final int n, final CTOMath ctoMath) {
                    CTHyperlinkImpl.this.insertNewOMath(n).set((XmlObject)ctoMath);
                }
                
                @Override
                public CTOMath remove(final int n) {
                    final CTOMath oMathArray = CTHyperlinkImpl.this.getOMathArray(n);
                    CTHyperlinkImpl.this.removeOMath(n);
                    return oMathArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfOMathArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.OMATH$56, (List)list);
            final CTOMath[] array = new CTOMath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOMath getOMathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOMath ctoMath = (CTOMath)this.get_store().find_element_user(CTHyperlinkImpl.OMATH$56, n);
            if (ctoMath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctoMath;
        }
    }
    
    public int sizeOfOMathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.OMATH$56);
        }
    }
    
    public void setOMathArray(final CTOMath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.OMATH$56);
    }
    
    public void setOMathArray(final int n, final CTOMath ctoMath) {
        this.generatedSetterHelperImpl((XmlObject)ctoMath, CTHyperlinkImpl.OMATH$56, n, (short)2);
    }
    
    public CTOMath insertNewOMath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMath)this.get_store().insert_element_user(CTHyperlinkImpl.OMATH$56, n);
        }
    }
    
    public CTOMath addNewOMath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMath)this.get_store().add_element_user(CTHyperlinkImpl.OMATH$56);
        }
    }
    
    public void removeOMath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.OMATH$56, n);
        }
    }
    
    public List<CTSimpleField> getFldSimpleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FldSimpleList extends AbstractList<CTSimpleField>
            {
                @Override
                public CTSimpleField get(final int n) {
                    return CTHyperlinkImpl.this.getFldSimpleArray(n);
                }
                
                @Override
                public CTSimpleField set(final int n, final CTSimpleField ctSimpleField) {
                    final CTSimpleField fldSimpleArray = CTHyperlinkImpl.this.getFldSimpleArray(n);
                    CTHyperlinkImpl.this.setFldSimpleArray(n, ctSimpleField);
                    return fldSimpleArray;
                }
                
                @Override
                public void add(final int n, final CTSimpleField ctSimpleField) {
                    CTHyperlinkImpl.this.insertNewFldSimple(n).set((XmlObject)ctSimpleField);
                }
                
                @Override
                public CTSimpleField remove(final int n) {
                    final CTSimpleField fldSimpleArray = CTHyperlinkImpl.this.getFldSimpleArray(n);
                    CTHyperlinkImpl.this.removeFldSimple(n);
                    return fldSimpleArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfFldSimpleArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.FLDSIMPLE$58, (List)list);
            final CTSimpleField[] array = new CTSimpleField[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSimpleField getFldSimpleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSimpleField ctSimpleField = (CTSimpleField)this.get_store().find_element_user(CTHyperlinkImpl.FLDSIMPLE$58, n);
            if (ctSimpleField == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSimpleField;
        }
    }
    
    public int sizeOfFldSimpleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.FLDSIMPLE$58);
        }
    }
    
    public void setFldSimpleArray(final CTSimpleField[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.FLDSIMPLE$58);
    }
    
    public void setFldSimpleArray(final int n, final CTSimpleField ctSimpleField) {
        this.generatedSetterHelperImpl((XmlObject)ctSimpleField, CTHyperlinkImpl.FLDSIMPLE$58, n, (short)2);
    }
    
    public CTSimpleField insertNewFldSimple(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSimpleField)this.get_store().insert_element_user(CTHyperlinkImpl.FLDSIMPLE$58, n);
        }
    }
    
    public CTSimpleField addNewFldSimple() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSimpleField)this.get_store().add_element_user(CTHyperlinkImpl.FLDSIMPLE$58);
        }
    }
    
    public void removeFldSimple(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.FLDSIMPLE$58, n);
        }
    }
    
    public List<CTHyperlink> getHyperlinkList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HyperlinkList extends AbstractList<CTHyperlink>
            {
                @Override
                public CTHyperlink get(final int n) {
                    return CTHyperlinkImpl.this.getHyperlinkArray(n);
                }
                
                @Override
                public CTHyperlink set(final int n, final CTHyperlink ctHyperlink) {
                    final CTHyperlink hyperlinkArray = CTHyperlinkImpl.this.getHyperlinkArray(n);
                    CTHyperlinkImpl.this.setHyperlinkArray(n, ctHyperlink);
                    return hyperlinkArray;
                }
                
                @Override
                public void add(final int n, final CTHyperlink ctHyperlink) {
                    CTHyperlinkImpl.this.insertNewHyperlink(n).set((XmlObject)ctHyperlink);
                }
                
                @Override
                public CTHyperlink remove(final int n) {
                    final CTHyperlink hyperlinkArray = CTHyperlinkImpl.this.getHyperlinkArray(n);
                    CTHyperlinkImpl.this.removeHyperlink(n);
                    return hyperlinkArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfHyperlinkArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.HYPERLINK$60, (List)list);
            final CTHyperlink[] array = new CTHyperlink[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTHyperlink getHyperlinkArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHyperlink ctHyperlink = (CTHyperlink)this.get_store().find_element_user(CTHyperlinkImpl.HYPERLINK$60, n);
            if (ctHyperlink == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctHyperlink;
        }
    }
    
    public int sizeOfHyperlinkArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.HYPERLINK$60);
        }
    }
    
    public void setHyperlinkArray(final CTHyperlink[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.HYPERLINK$60);
    }
    
    public void setHyperlinkArray(final int n, final CTHyperlink ctHyperlink) {
        this.generatedSetterHelperImpl((XmlObject)ctHyperlink, CTHyperlinkImpl.HYPERLINK$60, n, (short)2);
    }
    
    public CTHyperlink insertNewHyperlink(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHyperlink)this.get_store().insert_element_user(CTHyperlinkImpl.HYPERLINK$60, n);
        }
    }
    
    public CTHyperlink addNewHyperlink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHyperlink)this.get_store().add_element_user(CTHyperlinkImpl.HYPERLINK$60);
        }
    }
    
    public void removeHyperlink(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.HYPERLINK$60, n);
        }
    }
    
    public List<CTRel> getSubDocList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SubDocList extends AbstractList<CTRel>
            {
                @Override
                public CTRel get(final int n) {
                    return CTHyperlinkImpl.this.getSubDocArray(n);
                }
                
                @Override
                public CTRel set(final int n, final CTRel ctRel) {
                    final CTRel subDocArray = CTHyperlinkImpl.this.getSubDocArray(n);
                    CTHyperlinkImpl.this.setSubDocArray(n, ctRel);
                    return subDocArray;
                }
                
                @Override
                public void add(final int n, final CTRel ctRel) {
                    CTHyperlinkImpl.this.insertNewSubDoc(n).set((XmlObject)ctRel);
                }
                
                @Override
                public CTRel remove(final int n) {
                    final CTRel subDocArray = CTHyperlinkImpl.this.getSubDocArray(n);
                    CTHyperlinkImpl.this.removeSubDoc(n);
                    return subDocArray;
                }
                
                @Override
                public int size() {
                    return CTHyperlinkImpl.this.sizeOfSubDocArray();
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
            this.get_store().find_all_element_users(CTHyperlinkImpl.SUBDOC$62, (List)list);
            final CTRel[] array = new CTRel[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRel getSubDocArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRel ctRel = (CTRel)this.get_store().find_element_user(CTHyperlinkImpl.SUBDOC$62, n);
            if (ctRel == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRel;
        }
    }
    
    public int sizeOfSubDocArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.SUBDOC$62);
        }
    }
    
    public void setSubDocArray(final CTRel[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHyperlinkImpl.SUBDOC$62);
    }
    
    public void setSubDocArray(final int n, final CTRel ctRel) {
        this.generatedSetterHelperImpl((XmlObject)ctRel, CTHyperlinkImpl.SUBDOC$62, n, (short)2);
    }
    
    public CTRel insertNewSubDoc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRel)this.get_store().insert_element_user(CTHyperlinkImpl.SUBDOC$62, n);
        }
    }
    
    public CTRel addNewSubDoc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRel)this.get_store().add_element_user(CTHyperlinkImpl.SUBDOC$62);
        }
    }
    
    public void removeSubDoc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.SUBDOC$62, n);
        }
    }
    
    public String getTgtFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.TGTFRAME$64);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetTgtFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTHyperlinkImpl.TGTFRAME$64);
        }
    }
    
    public boolean isSetTgtFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.TGTFRAME$64) != null;
        }
    }
    
    public void setTgtFrame(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.TGTFRAME$64);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.TGTFRAME$64);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTgtFrame(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTHyperlinkImpl.TGTFRAME$64);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTHyperlinkImpl.TGTFRAME$64);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public void unsetTgtFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.TGTFRAME$64);
        }
    }
    
    public String getTooltip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.TOOLTIP$66);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetTooltip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTHyperlinkImpl.TOOLTIP$66);
        }
    }
    
    public boolean isSetTooltip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.TOOLTIP$66) != null;
        }
    }
    
    public void setTooltip(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.TOOLTIP$66);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.TOOLTIP$66);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTooltip(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTHyperlinkImpl.TOOLTIP$66);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTHyperlinkImpl.TOOLTIP$66);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public void unsetTooltip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.TOOLTIP$66);
        }
    }
    
    public String getDocLocation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.DOCLOCATION$68);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetDocLocation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTHyperlinkImpl.DOCLOCATION$68);
        }
    }
    
    public boolean isSetDocLocation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.DOCLOCATION$68) != null;
        }
    }
    
    public void setDocLocation(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.DOCLOCATION$68);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.DOCLOCATION$68);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetDocLocation(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTHyperlinkImpl.DOCLOCATION$68);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTHyperlinkImpl.DOCLOCATION$68);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public void unsetDocLocation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.DOCLOCATION$68);
        }
    }
    
    public STOnOff.Enum getHistory() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.HISTORY$70);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetHistory() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTHyperlinkImpl.HISTORY$70);
        }
    }
    
    public boolean isSetHistory() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.HISTORY$70) != null;
        }
    }
    
    public void setHistory(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.HISTORY$70);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.HISTORY$70);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHistory(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTHyperlinkImpl.HISTORY$70);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTHyperlinkImpl.HISTORY$70);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetHistory() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.HISTORY$70);
        }
    }
    
    public String getAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.ANCHOR$72);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTHyperlinkImpl.ANCHOR$72);
        }
    }
    
    public boolean isSetAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.ANCHOR$72) != null;
        }
    }
    
    public void setAnchor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.ANCHOR$72);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.ANCHOR$72);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetAnchor(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTHyperlinkImpl.ANCHOR$72);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTHyperlinkImpl.ANCHOR$72);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public void unsetAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.ANCHOR$72);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.ID$74);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRelationshipId)this.get_store().find_attribute_user(CTHyperlinkImpl.ID$74);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.ID$74) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.ID$74);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.ID$74);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTHyperlinkImpl.ID$74);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTHyperlinkImpl.ID$74);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.ID$74);
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
        FLDSIMPLE$58 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "fldSimple");
        HYPERLINK$60 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hyperlink");
        SUBDOC$62 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "subDoc");
        TGTFRAME$64 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tgtFrame");
        TOOLTIP$66 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tooltip");
        DOCLOCATION$68 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "docLocation");
        HISTORY$70 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "history");
        ANCHOR$72 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "anchor");
        ID$74 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
    }
}
