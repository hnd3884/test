package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAltChunk;
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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCustomXmlBlock;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTHdrFtrImpl extends XmlComplexContentImpl implements CTHdrFtr
{
    private static final long serialVersionUID = 1L;
    private static final QName CUSTOMXML$0;
    private static final QName SDT$2;
    private static final QName P$4;
    private static final QName TBL$6;
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
    private static final QName ALTCHUNK$58;
    
    public CTHdrFtrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTCustomXmlBlock> getCustomXmlList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlList extends AbstractList<CTCustomXmlBlock>
            {
                @Override
                public CTCustomXmlBlock get(final int n) {
                    return CTHdrFtrImpl.this.getCustomXmlArray(n);
                }
                
                @Override
                public CTCustomXmlBlock set(final int n, final CTCustomXmlBlock ctCustomXmlBlock) {
                    final CTCustomXmlBlock customXmlArray = CTHdrFtrImpl.this.getCustomXmlArray(n);
                    CTHdrFtrImpl.this.setCustomXmlArray(n, ctCustomXmlBlock);
                    return customXmlArray;
                }
                
                @Override
                public void add(final int n, final CTCustomXmlBlock ctCustomXmlBlock) {
                    CTHdrFtrImpl.this.insertNewCustomXml(n).set((XmlObject)ctCustomXmlBlock);
                }
                
                @Override
                public CTCustomXmlBlock remove(final int n) {
                    final CTCustomXmlBlock customXmlArray = CTHdrFtrImpl.this.getCustomXmlArray(n);
                    CTHdrFtrImpl.this.removeCustomXml(n);
                    return customXmlArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfCustomXmlArray();
                }
            }
            return new CustomXmlList();
        }
    }
    
    @Deprecated
    public CTCustomXmlBlock[] getCustomXmlArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTHdrFtrImpl.CUSTOMXML$0, (List)list);
            final CTCustomXmlBlock[] array = new CTCustomXmlBlock[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCustomXmlBlock getCustomXmlArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomXmlBlock ctCustomXmlBlock = (CTCustomXmlBlock)this.get_store().find_element_user(CTHdrFtrImpl.CUSTOMXML$0, n);
            if (ctCustomXmlBlock == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCustomXmlBlock;
        }
    }
    
    public int sizeOfCustomXmlArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.CUSTOMXML$0);
        }
    }
    
    public void setCustomXmlArray(final CTCustomXmlBlock[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.CUSTOMXML$0);
    }
    
    public void setCustomXmlArray(final int n, final CTCustomXmlBlock ctCustomXmlBlock) {
        this.generatedSetterHelperImpl((XmlObject)ctCustomXmlBlock, CTHdrFtrImpl.CUSTOMXML$0, n, (short)2);
    }
    
    public CTCustomXmlBlock insertNewCustomXml(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomXmlBlock)this.get_store().insert_element_user(CTHdrFtrImpl.CUSTOMXML$0, n);
        }
    }
    
    public CTCustomXmlBlock addNewCustomXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomXmlBlock)this.get_store().add_element_user(CTHdrFtrImpl.CUSTOMXML$0);
        }
    }
    
    public void removeCustomXml(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.CUSTOMXML$0, n);
        }
    }
    
    public List<CTSdtBlock> getSdtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SdtList extends AbstractList<CTSdtBlock>
            {
                @Override
                public CTSdtBlock get(final int n) {
                    return CTHdrFtrImpl.this.getSdtArray(n);
                }
                
                @Override
                public CTSdtBlock set(final int n, final CTSdtBlock ctSdtBlock) {
                    final CTSdtBlock sdtArray = CTHdrFtrImpl.this.getSdtArray(n);
                    CTHdrFtrImpl.this.setSdtArray(n, ctSdtBlock);
                    return sdtArray;
                }
                
                @Override
                public void add(final int n, final CTSdtBlock ctSdtBlock) {
                    CTHdrFtrImpl.this.insertNewSdt(n).set((XmlObject)ctSdtBlock);
                }
                
                @Override
                public CTSdtBlock remove(final int n) {
                    final CTSdtBlock sdtArray = CTHdrFtrImpl.this.getSdtArray(n);
                    CTHdrFtrImpl.this.removeSdt(n);
                    return sdtArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfSdtArray();
                }
            }
            return new SdtList();
        }
    }
    
    @Deprecated
    public CTSdtBlock[] getSdtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTHdrFtrImpl.SDT$2, (List)list);
            final CTSdtBlock[] array = new CTSdtBlock[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSdtBlock getSdtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtBlock ctSdtBlock = (CTSdtBlock)this.get_store().find_element_user(CTHdrFtrImpl.SDT$2, n);
            if (ctSdtBlock == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSdtBlock;
        }
    }
    
    public int sizeOfSdtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.SDT$2);
        }
    }
    
    public void setSdtArray(final CTSdtBlock[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.SDT$2);
    }
    
    public void setSdtArray(final int n, final CTSdtBlock ctSdtBlock) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtBlock, CTHdrFtrImpl.SDT$2, n, (short)2);
    }
    
    public CTSdtBlock insertNewSdt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtBlock)this.get_store().insert_element_user(CTHdrFtrImpl.SDT$2, n);
        }
    }
    
    public CTSdtBlock addNewSdt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtBlock)this.get_store().add_element_user(CTHdrFtrImpl.SDT$2);
        }
    }
    
    public void removeSdt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.SDT$2, n);
        }
    }
    
    public List<CTP> getPList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PList extends AbstractList<CTP>
            {
                @Override
                public CTP get(final int n) {
                    return CTHdrFtrImpl.this.getPArray(n);
                }
                
                @Override
                public CTP set(final int n, final CTP ctp) {
                    final CTP pArray = CTHdrFtrImpl.this.getPArray(n);
                    CTHdrFtrImpl.this.setPArray(n, ctp);
                    return pArray;
                }
                
                @Override
                public void add(final int n, final CTP ctp) {
                    CTHdrFtrImpl.this.insertNewP(n).set((XmlObject)ctp);
                }
                
                @Override
                public CTP remove(final int n) {
                    final CTP pArray = CTHdrFtrImpl.this.getPArray(n);
                    CTHdrFtrImpl.this.removeP(n);
                    return pArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfPArray();
                }
            }
            return new PList();
        }
    }
    
    @Deprecated
    public CTP[] getPArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTHdrFtrImpl.P$4, (List)list);
            final CTP[] array = new CTP[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTP getPArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTP ctp = (CTP)this.get_store().find_element_user(CTHdrFtrImpl.P$4, n);
            if (ctp == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctp;
        }
    }
    
    public int sizeOfPArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.P$4);
        }
    }
    
    public void setPArray(final CTP[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.P$4);
    }
    
    public void setPArray(final int n, final CTP ctp) {
        this.generatedSetterHelperImpl((XmlObject)ctp, CTHdrFtrImpl.P$4, n, (short)2);
    }
    
    public CTP insertNewP(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTP)this.get_store().insert_element_user(CTHdrFtrImpl.P$4, n);
        }
    }
    
    public CTP addNewP() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTP)this.get_store().add_element_user(CTHdrFtrImpl.P$4);
        }
    }
    
    public void removeP(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.P$4, n);
        }
    }
    
    public List<CTTbl> getTblList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TblList extends AbstractList<CTTbl>
            {
                @Override
                public CTTbl get(final int n) {
                    return CTHdrFtrImpl.this.getTblArray(n);
                }
                
                @Override
                public CTTbl set(final int n, final CTTbl ctTbl) {
                    final CTTbl tblArray = CTHdrFtrImpl.this.getTblArray(n);
                    CTHdrFtrImpl.this.setTblArray(n, ctTbl);
                    return tblArray;
                }
                
                @Override
                public void add(final int n, final CTTbl ctTbl) {
                    CTHdrFtrImpl.this.insertNewTbl(n).set((XmlObject)ctTbl);
                }
                
                @Override
                public CTTbl remove(final int n) {
                    final CTTbl tblArray = CTHdrFtrImpl.this.getTblArray(n);
                    CTHdrFtrImpl.this.removeTbl(n);
                    return tblArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfTblArray();
                }
            }
            return new TblList();
        }
    }
    
    @Deprecated
    public CTTbl[] getTblArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTHdrFtrImpl.TBL$6, (List)list);
            final CTTbl[] array = new CTTbl[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTbl getTblArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTbl ctTbl = (CTTbl)this.get_store().find_element_user(CTHdrFtrImpl.TBL$6, n);
            if (ctTbl == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTbl;
        }
    }
    
    public int sizeOfTblArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.TBL$6);
        }
    }
    
    public void setTblArray(final CTTbl[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.TBL$6);
    }
    
    public void setTblArray(final int n, final CTTbl ctTbl) {
        this.generatedSetterHelperImpl((XmlObject)ctTbl, CTHdrFtrImpl.TBL$6, n, (short)2);
    }
    
    public CTTbl insertNewTbl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTbl)this.get_store().insert_element_user(CTHdrFtrImpl.TBL$6, n);
        }
    }
    
    public CTTbl addNewTbl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTbl)this.get_store().add_element_user(CTHdrFtrImpl.TBL$6);
        }
    }
    
    public void removeTbl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.TBL$6, n);
        }
    }
    
    public List<CTProofErr> getProofErrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ProofErrList extends AbstractList<CTProofErr>
            {
                @Override
                public CTProofErr get(final int n) {
                    return CTHdrFtrImpl.this.getProofErrArray(n);
                }
                
                @Override
                public CTProofErr set(final int n, final CTProofErr ctProofErr) {
                    final CTProofErr proofErrArray = CTHdrFtrImpl.this.getProofErrArray(n);
                    CTHdrFtrImpl.this.setProofErrArray(n, ctProofErr);
                    return proofErrArray;
                }
                
                @Override
                public void add(final int n, final CTProofErr ctProofErr) {
                    CTHdrFtrImpl.this.insertNewProofErr(n).set((XmlObject)ctProofErr);
                }
                
                @Override
                public CTProofErr remove(final int n) {
                    final CTProofErr proofErrArray = CTHdrFtrImpl.this.getProofErrArray(n);
                    CTHdrFtrImpl.this.removeProofErr(n);
                    return proofErrArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfProofErrArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.PROOFERR$8, (List)list);
            final CTProofErr[] array = new CTProofErr[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTProofErr getProofErrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTProofErr ctProofErr = (CTProofErr)this.get_store().find_element_user(CTHdrFtrImpl.PROOFERR$8, n);
            if (ctProofErr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctProofErr;
        }
    }
    
    public int sizeOfProofErrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.PROOFERR$8);
        }
    }
    
    public void setProofErrArray(final CTProofErr[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.PROOFERR$8);
    }
    
    public void setProofErrArray(final int n, final CTProofErr ctProofErr) {
        this.generatedSetterHelperImpl((XmlObject)ctProofErr, CTHdrFtrImpl.PROOFERR$8, n, (short)2);
    }
    
    public CTProofErr insertNewProofErr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProofErr)this.get_store().insert_element_user(CTHdrFtrImpl.PROOFERR$8, n);
        }
    }
    
    public CTProofErr addNewProofErr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProofErr)this.get_store().add_element_user(CTHdrFtrImpl.PROOFERR$8);
        }
    }
    
    public void removeProofErr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.PROOFERR$8, n);
        }
    }
    
    public List<CTPermStart> getPermStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PermStartList extends AbstractList<CTPermStart>
            {
                @Override
                public CTPermStart get(final int n) {
                    return CTHdrFtrImpl.this.getPermStartArray(n);
                }
                
                @Override
                public CTPermStart set(final int n, final CTPermStart ctPermStart) {
                    final CTPermStart permStartArray = CTHdrFtrImpl.this.getPermStartArray(n);
                    CTHdrFtrImpl.this.setPermStartArray(n, ctPermStart);
                    return permStartArray;
                }
                
                @Override
                public void add(final int n, final CTPermStart ctPermStart) {
                    CTHdrFtrImpl.this.insertNewPermStart(n).set((XmlObject)ctPermStart);
                }
                
                @Override
                public CTPermStart remove(final int n) {
                    final CTPermStart permStartArray = CTHdrFtrImpl.this.getPermStartArray(n);
                    CTHdrFtrImpl.this.removePermStart(n);
                    return permStartArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfPermStartArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.PERMSTART$10, (List)list);
            final CTPermStart[] array = new CTPermStart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPermStart getPermStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPermStart ctPermStart = (CTPermStart)this.get_store().find_element_user(CTHdrFtrImpl.PERMSTART$10, n);
            if (ctPermStart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPermStart;
        }
    }
    
    public int sizeOfPermStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.PERMSTART$10);
        }
    }
    
    public void setPermStartArray(final CTPermStart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.PERMSTART$10);
    }
    
    public void setPermStartArray(final int n, final CTPermStart ctPermStart) {
        this.generatedSetterHelperImpl((XmlObject)ctPermStart, CTHdrFtrImpl.PERMSTART$10, n, (short)2);
    }
    
    public CTPermStart insertNewPermStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPermStart)this.get_store().insert_element_user(CTHdrFtrImpl.PERMSTART$10, n);
        }
    }
    
    public CTPermStart addNewPermStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPermStart)this.get_store().add_element_user(CTHdrFtrImpl.PERMSTART$10);
        }
    }
    
    public void removePermStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.PERMSTART$10, n);
        }
    }
    
    public List<CTPerm> getPermEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PermEndList extends AbstractList<CTPerm>
            {
                @Override
                public CTPerm get(final int n) {
                    return CTHdrFtrImpl.this.getPermEndArray(n);
                }
                
                @Override
                public CTPerm set(final int n, final CTPerm ctPerm) {
                    final CTPerm permEndArray = CTHdrFtrImpl.this.getPermEndArray(n);
                    CTHdrFtrImpl.this.setPermEndArray(n, ctPerm);
                    return permEndArray;
                }
                
                @Override
                public void add(final int n, final CTPerm ctPerm) {
                    CTHdrFtrImpl.this.insertNewPermEnd(n).set((XmlObject)ctPerm);
                }
                
                @Override
                public CTPerm remove(final int n) {
                    final CTPerm permEndArray = CTHdrFtrImpl.this.getPermEndArray(n);
                    CTHdrFtrImpl.this.removePermEnd(n);
                    return permEndArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfPermEndArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.PERMEND$12, (List)list);
            final CTPerm[] array = new CTPerm[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPerm getPermEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPerm ctPerm = (CTPerm)this.get_store().find_element_user(CTHdrFtrImpl.PERMEND$12, n);
            if (ctPerm == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPerm;
        }
    }
    
    public int sizeOfPermEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.PERMEND$12);
        }
    }
    
    public void setPermEndArray(final CTPerm[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.PERMEND$12);
    }
    
    public void setPermEndArray(final int n, final CTPerm ctPerm) {
        this.generatedSetterHelperImpl((XmlObject)ctPerm, CTHdrFtrImpl.PERMEND$12, n, (short)2);
    }
    
    public CTPerm insertNewPermEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerm)this.get_store().insert_element_user(CTHdrFtrImpl.PERMEND$12, n);
        }
    }
    
    public CTPerm addNewPermEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerm)this.get_store().add_element_user(CTHdrFtrImpl.PERMEND$12);
        }
    }
    
    public void removePermEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.PERMEND$12, n);
        }
    }
    
    public List<CTBookmark> getBookmarkStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BookmarkStartList extends AbstractList<CTBookmark>
            {
                @Override
                public CTBookmark get(final int n) {
                    return CTHdrFtrImpl.this.getBookmarkStartArray(n);
                }
                
                @Override
                public CTBookmark set(final int n, final CTBookmark ctBookmark) {
                    final CTBookmark bookmarkStartArray = CTHdrFtrImpl.this.getBookmarkStartArray(n);
                    CTHdrFtrImpl.this.setBookmarkStartArray(n, ctBookmark);
                    return bookmarkStartArray;
                }
                
                @Override
                public void add(final int n, final CTBookmark ctBookmark) {
                    CTHdrFtrImpl.this.insertNewBookmarkStart(n).set((XmlObject)ctBookmark);
                }
                
                @Override
                public CTBookmark remove(final int n) {
                    final CTBookmark bookmarkStartArray = CTHdrFtrImpl.this.getBookmarkStartArray(n);
                    CTHdrFtrImpl.this.removeBookmarkStart(n);
                    return bookmarkStartArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfBookmarkStartArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.BOOKMARKSTART$14, (List)list);
            final CTBookmark[] array = new CTBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBookmark getBookmarkStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBookmark ctBookmark = (CTBookmark)this.get_store().find_element_user(CTHdrFtrImpl.BOOKMARKSTART$14, n);
            if (ctBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBookmark;
        }
    }
    
    public int sizeOfBookmarkStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.BOOKMARKSTART$14);
        }
    }
    
    public void setBookmarkStartArray(final CTBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.BOOKMARKSTART$14);
    }
    
    public void setBookmarkStartArray(final int n, final CTBookmark ctBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctBookmark, CTHdrFtrImpl.BOOKMARKSTART$14, n, (short)2);
    }
    
    public CTBookmark insertNewBookmarkStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookmark)this.get_store().insert_element_user(CTHdrFtrImpl.BOOKMARKSTART$14, n);
        }
    }
    
    public CTBookmark addNewBookmarkStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookmark)this.get_store().add_element_user(CTHdrFtrImpl.BOOKMARKSTART$14);
        }
    }
    
    public void removeBookmarkStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.BOOKMARKSTART$14, n);
        }
    }
    
    public List<CTMarkupRange> getBookmarkEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BookmarkEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTHdrFtrImpl.this.getBookmarkEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange bookmarkEndArray = CTHdrFtrImpl.this.getBookmarkEndArray(n);
                    CTHdrFtrImpl.this.setBookmarkEndArray(n, ctMarkupRange);
                    return bookmarkEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTHdrFtrImpl.this.insertNewBookmarkEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange bookmarkEndArray = CTHdrFtrImpl.this.getBookmarkEndArray(n);
                    CTHdrFtrImpl.this.removeBookmarkEnd(n);
                    return bookmarkEndArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfBookmarkEndArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.BOOKMARKEND$16, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getBookmarkEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTHdrFtrImpl.BOOKMARKEND$16, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfBookmarkEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.BOOKMARKEND$16);
        }
    }
    
    public void setBookmarkEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.BOOKMARKEND$16);
    }
    
    public void setBookmarkEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTHdrFtrImpl.BOOKMARKEND$16, n, (short)2);
    }
    
    public CTMarkupRange insertNewBookmarkEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTHdrFtrImpl.BOOKMARKEND$16, n);
        }
    }
    
    public CTMarkupRange addNewBookmarkEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTHdrFtrImpl.BOOKMARKEND$16);
        }
    }
    
    public void removeBookmarkEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.BOOKMARKEND$16, n);
        }
    }
    
    public List<CTMoveBookmark> getMoveFromRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromRangeStartList extends AbstractList<CTMoveBookmark>
            {
                @Override
                public CTMoveBookmark get(final int n) {
                    return CTHdrFtrImpl.this.getMoveFromRangeStartArray(n);
                }
                
                @Override
                public CTMoveBookmark set(final int n, final CTMoveBookmark ctMoveBookmark) {
                    final CTMoveBookmark moveFromRangeStartArray = CTHdrFtrImpl.this.getMoveFromRangeStartArray(n);
                    CTHdrFtrImpl.this.setMoveFromRangeStartArray(n, ctMoveBookmark);
                    return moveFromRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMoveBookmark ctMoveBookmark) {
                    CTHdrFtrImpl.this.insertNewMoveFromRangeStart(n).set((XmlObject)ctMoveBookmark);
                }
                
                @Override
                public CTMoveBookmark remove(final int n) {
                    final CTMoveBookmark moveFromRangeStartArray = CTHdrFtrImpl.this.getMoveFromRangeStartArray(n);
                    CTHdrFtrImpl.this.removeMoveFromRangeStart(n);
                    return moveFromRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfMoveFromRangeStartArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.MOVEFROMRANGESTART$18, (List)list);
            final CTMoveBookmark[] array = new CTMoveBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMoveBookmark getMoveFromRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMoveBookmark ctMoveBookmark = (CTMoveBookmark)this.get_store().find_element_user(CTHdrFtrImpl.MOVEFROMRANGESTART$18, n);
            if (ctMoveBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMoveBookmark;
        }
    }
    
    public int sizeOfMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.MOVEFROMRANGESTART$18);
        }
    }
    
    public void setMoveFromRangeStartArray(final CTMoveBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.MOVEFROMRANGESTART$18);
    }
    
    public void setMoveFromRangeStartArray(final int n, final CTMoveBookmark ctMoveBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctMoveBookmark, CTHdrFtrImpl.MOVEFROMRANGESTART$18, n, (short)2);
    }
    
    public CTMoveBookmark insertNewMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().insert_element_user(CTHdrFtrImpl.MOVEFROMRANGESTART$18, n);
        }
    }
    
    public CTMoveBookmark addNewMoveFromRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().add_element_user(CTHdrFtrImpl.MOVEFROMRANGESTART$18);
        }
    }
    
    public void removeMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.MOVEFROMRANGESTART$18, n);
        }
    }
    
    public List<CTMarkupRange> getMoveFromRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTHdrFtrImpl.this.getMoveFromRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange moveFromRangeEndArray = CTHdrFtrImpl.this.getMoveFromRangeEndArray(n);
                    CTHdrFtrImpl.this.setMoveFromRangeEndArray(n, ctMarkupRange);
                    return moveFromRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTHdrFtrImpl.this.insertNewMoveFromRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange moveFromRangeEndArray = CTHdrFtrImpl.this.getMoveFromRangeEndArray(n);
                    CTHdrFtrImpl.this.removeMoveFromRangeEnd(n);
                    return moveFromRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfMoveFromRangeEndArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.MOVEFROMRANGEEND$20, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getMoveFromRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTHdrFtrImpl.MOVEFROMRANGEEND$20, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.MOVEFROMRANGEEND$20);
        }
    }
    
    public void setMoveFromRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.MOVEFROMRANGEEND$20);
    }
    
    public void setMoveFromRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTHdrFtrImpl.MOVEFROMRANGEEND$20, n, (short)2);
    }
    
    public CTMarkupRange insertNewMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTHdrFtrImpl.MOVEFROMRANGEEND$20, n);
        }
    }
    
    public CTMarkupRange addNewMoveFromRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTHdrFtrImpl.MOVEFROMRANGEEND$20);
        }
    }
    
    public void removeMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.MOVEFROMRANGEEND$20, n);
        }
    }
    
    public List<CTMoveBookmark> getMoveToRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToRangeStartList extends AbstractList<CTMoveBookmark>
            {
                @Override
                public CTMoveBookmark get(final int n) {
                    return CTHdrFtrImpl.this.getMoveToRangeStartArray(n);
                }
                
                @Override
                public CTMoveBookmark set(final int n, final CTMoveBookmark ctMoveBookmark) {
                    final CTMoveBookmark moveToRangeStartArray = CTHdrFtrImpl.this.getMoveToRangeStartArray(n);
                    CTHdrFtrImpl.this.setMoveToRangeStartArray(n, ctMoveBookmark);
                    return moveToRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMoveBookmark ctMoveBookmark) {
                    CTHdrFtrImpl.this.insertNewMoveToRangeStart(n).set((XmlObject)ctMoveBookmark);
                }
                
                @Override
                public CTMoveBookmark remove(final int n) {
                    final CTMoveBookmark moveToRangeStartArray = CTHdrFtrImpl.this.getMoveToRangeStartArray(n);
                    CTHdrFtrImpl.this.removeMoveToRangeStart(n);
                    return moveToRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfMoveToRangeStartArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.MOVETORANGESTART$22, (List)list);
            final CTMoveBookmark[] array = new CTMoveBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMoveBookmark getMoveToRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMoveBookmark ctMoveBookmark = (CTMoveBookmark)this.get_store().find_element_user(CTHdrFtrImpl.MOVETORANGESTART$22, n);
            if (ctMoveBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMoveBookmark;
        }
    }
    
    public int sizeOfMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.MOVETORANGESTART$22);
        }
    }
    
    public void setMoveToRangeStartArray(final CTMoveBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.MOVETORANGESTART$22);
    }
    
    public void setMoveToRangeStartArray(final int n, final CTMoveBookmark ctMoveBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctMoveBookmark, CTHdrFtrImpl.MOVETORANGESTART$22, n, (short)2);
    }
    
    public CTMoveBookmark insertNewMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().insert_element_user(CTHdrFtrImpl.MOVETORANGESTART$22, n);
        }
    }
    
    public CTMoveBookmark addNewMoveToRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().add_element_user(CTHdrFtrImpl.MOVETORANGESTART$22);
        }
    }
    
    public void removeMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.MOVETORANGESTART$22, n);
        }
    }
    
    public List<CTMarkupRange> getMoveToRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTHdrFtrImpl.this.getMoveToRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange moveToRangeEndArray = CTHdrFtrImpl.this.getMoveToRangeEndArray(n);
                    CTHdrFtrImpl.this.setMoveToRangeEndArray(n, ctMarkupRange);
                    return moveToRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTHdrFtrImpl.this.insertNewMoveToRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange moveToRangeEndArray = CTHdrFtrImpl.this.getMoveToRangeEndArray(n);
                    CTHdrFtrImpl.this.removeMoveToRangeEnd(n);
                    return moveToRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfMoveToRangeEndArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.MOVETORANGEEND$24, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getMoveToRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTHdrFtrImpl.MOVETORANGEEND$24, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.MOVETORANGEEND$24);
        }
    }
    
    public void setMoveToRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.MOVETORANGEEND$24);
    }
    
    public void setMoveToRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTHdrFtrImpl.MOVETORANGEEND$24, n, (short)2);
    }
    
    public CTMarkupRange insertNewMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTHdrFtrImpl.MOVETORANGEEND$24, n);
        }
    }
    
    public CTMarkupRange addNewMoveToRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTHdrFtrImpl.MOVETORANGEEND$24);
        }
    }
    
    public void removeMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.MOVETORANGEEND$24, n);
        }
    }
    
    public List<CTMarkupRange> getCommentRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentRangeStartList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTHdrFtrImpl.this.getCommentRangeStartArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange commentRangeStartArray = CTHdrFtrImpl.this.getCommentRangeStartArray(n);
                    CTHdrFtrImpl.this.setCommentRangeStartArray(n, ctMarkupRange);
                    return commentRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTHdrFtrImpl.this.insertNewCommentRangeStart(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange commentRangeStartArray = CTHdrFtrImpl.this.getCommentRangeStartArray(n);
                    CTHdrFtrImpl.this.removeCommentRangeStart(n);
                    return commentRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfCommentRangeStartArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.COMMENTRANGESTART$26, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getCommentRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTHdrFtrImpl.COMMENTRANGESTART$26, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfCommentRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.COMMENTRANGESTART$26);
        }
    }
    
    public void setCommentRangeStartArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.COMMENTRANGESTART$26);
    }
    
    public void setCommentRangeStartArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTHdrFtrImpl.COMMENTRANGESTART$26, n, (short)2);
    }
    
    public CTMarkupRange insertNewCommentRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTHdrFtrImpl.COMMENTRANGESTART$26, n);
        }
    }
    
    public CTMarkupRange addNewCommentRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTHdrFtrImpl.COMMENTRANGESTART$26);
        }
    }
    
    public void removeCommentRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.COMMENTRANGESTART$26, n);
        }
    }
    
    public List<CTMarkupRange> getCommentRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTHdrFtrImpl.this.getCommentRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange commentRangeEndArray = CTHdrFtrImpl.this.getCommentRangeEndArray(n);
                    CTHdrFtrImpl.this.setCommentRangeEndArray(n, ctMarkupRange);
                    return commentRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTHdrFtrImpl.this.insertNewCommentRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange commentRangeEndArray = CTHdrFtrImpl.this.getCommentRangeEndArray(n);
                    CTHdrFtrImpl.this.removeCommentRangeEnd(n);
                    return commentRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfCommentRangeEndArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.COMMENTRANGEEND$28, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getCommentRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTHdrFtrImpl.COMMENTRANGEEND$28, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfCommentRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.COMMENTRANGEEND$28);
        }
    }
    
    public void setCommentRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.COMMENTRANGEEND$28);
    }
    
    public void setCommentRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTHdrFtrImpl.COMMENTRANGEEND$28, n, (short)2);
    }
    
    public CTMarkupRange insertNewCommentRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTHdrFtrImpl.COMMENTRANGEEND$28, n);
        }
    }
    
    public CTMarkupRange addNewCommentRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTHdrFtrImpl.COMMENTRANGEEND$28);
        }
    }
    
    public void removeCommentRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.COMMENTRANGEEND$28, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlInsRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlInsRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTHdrFtrImpl.this.getCustomXmlInsRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlInsRangeStartArray = CTHdrFtrImpl.this.getCustomXmlInsRangeStartArray(n);
                    CTHdrFtrImpl.this.setCustomXmlInsRangeStartArray(n, ctTrackChange);
                    return customXmlInsRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTHdrFtrImpl.this.insertNewCustomXmlInsRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlInsRangeStartArray = CTHdrFtrImpl.this.getCustomXmlInsRangeStartArray(n);
                    CTHdrFtrImpl.this.removeCustomXmlInsRangeStart(n);
                    return customXmlInsRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfCustomXmlInsRangeStartArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.CUSTOMXMLINSRANGESTART$30, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlInsRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTHdrFtrImpl.CUSTOMXMLINSRANGESTART$30, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlInsRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.CUSTOMXMLINSRANGESTART$30);
        }
    }
    
    public void setCustomXmlInsRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.CUSTOMXMLINSRANGESTART$30);
    }
    
    public void setCustomXmlInsRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTHdrFtrImpl.CUSTOMXMLINSRANGESTART$30, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlInsRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTHdrFtrImpl.CUSTOMXMLINSRANGESTART$30, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlInsRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTHdrFtrImpl.CUSTOMXMLINSRANGESTART$30);
        }
    }
    
    public void removeCustomXmlInsRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.CUSTOMXMLINSRANGESTART$30, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlInsRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlInsRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTHdrFtrImpl.this.getCustomXmlInsRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlInsRangeEndArray = CTHdrFtrImpl.this.getCustomXmlInsRangeEndArray(n);
                    CTHdrFtrImpl.this.setCustomXmlInsRangeEndArray(n, ctMarkup);
                    return customXmlInsRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTHdrFtrImpl.this.insertNewCustomXmlInsRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlInsRangeEndArray = CTHdrFtrImpl.this.getCustomXmlInsRangeEndArray(n);
                    CTHdrFtrImpl.this.removeCustomXmlInsRangeEnd(n);
                    return customXmlInsRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfCustomXmlInsRangeEndArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.CUSTOMXMLINSRANGEEND$32, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlInsRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTHdrFtrImpl.CUSTOMXMLINSRANGEEND$32, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlInsRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.CUSTOMXMLINSRANGEEND$32);
        }
    }
    
    public void setCustomXmlInsRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.CUSTOMXMLINSRANGEEND$32);
    }
    
    public void setCustomXmlInsRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTHdrFtrImpl.CUSTOMXMLINSRANGEEND$32, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlInsRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTHdrFtrImpl.CUSTOMXMLINSRANGEEND$32, n);
        }
    }
    
    public CTMarkup addNewCustomXmlInsRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTHdrFtrImpl.CUSTOMXMLINSRANGEEND$32);
        }
    }
    
    public void removeCustomXmlInsRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.CUSTOMXMLINSRANGEEND$32, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlDelRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlDelRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTHdrFtrImpl.this.getCustomXmlDelRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlDelRangeStartArray = CTHdrFtrImpl.this.getCustomXmlDelRangeStartArray(n);
                    CTHdrFtrImpl.this.setCustomXmlDelRangeStartArray(n, ctTrackChange);
                    return customXmlDelRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTHdrFtrImpl.this.insertNewCustomXmlDelRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlDelRangeStartArray = CTHdrFtrImpl.this.getCustomXmlDelRangeStartArray(n);
                    CTHdrFtrImpl.this.removeCustomXmlDelRangeStart(n);
                    return customXmlDelRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfCustomXmlDelRangeStartArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.CUSTOMXMLDELRANGESTART$34, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlDelRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTHdrFtrImpl.CUSTOMXMLDELRANGESTART$34, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlDelRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.CUSTOMXMLDELRANGESTART$34);
        }
    }
    
    public void setCustomXmlDelRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.CUSTOMXMLDELRANGESTART$34);
    }
    
    public void setCustomXmlDelRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTHdrFtrImpl.CUSTOMXMLDELRANGESTART$34, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlDelRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTHdrFtrImpl.CUSTOMXMLDELRANGESTART$34, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlDelRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTHdrFtrImpl.CUSTOMXMLDELRANGESTART$34);
        }
    }
    
    public void removeCustomXmlDelRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.CUSTOMXMLDELRANGESTART$34, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlDelRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlDelRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTHdrFtrImpl.this.getCustomXmlDelRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlDelRangeEndArray = CTHdrFtrImpl.this.getCustomXmlDelRangeEndArray(n);
                    CTHdrFtrImpl.this.setCustomXmlDelRangeEndArray(n, ctMarkup);
                    return customXmlDelRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTHdrFtrImpl.this.insertNewCustomXmlDelRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlDelRangeEndArray = CTHdrFtrImpl.this.getCustomXmlDelRangeEndArray(n);
                    CTHdrFtrImpl.this.removeCustomXmlDelRangeEnd(n);
                    return customXmlDelRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfCustomXmlDelRangeEndArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.CUSTOMXMLDELRANGEEND$36, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlDelRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTHdrFtrImpl.CUSTOMXMLDELRANGEEND$36, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlDelRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.CUSTOMXMLDELRANGEEND$36);
        }
    }
    
    public void setCustomXmlDelRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.CUSTOMXMLDELRANGEEND$36);
    }
    
    public void setCustomXmlDelRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTHdrFtrImpl.CUSTOMXMLDELRANGEEND$36, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlDelRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTHdrFtrImpl.CUSTOMXMLDELRANGEEND$36, n);
        }
    }
    
    public CTMarkup addNewCustomXmlDelRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTHdrFtrImpl.CUSTOMXMLDELRANGEEND$36);
        }
    }
    
    public void removeCustomXmlDelRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.CUSTOMXMLDELRANGEEND$36, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlMoveFromRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveFromRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTHdrFtrImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlMoveFromRangeStartArray = CTHdrFtrImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                    CTHdrFtrImpl.this.setCustomXmlMoveFromRangeStartArray(n, ctTrackChange);
                    return customXmlMoveFromRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTHdrFtrImpl.this.insertNewCustomXmlMoveFromRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlMoveFromRangeStartArray = CTHdrFtrImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                    CTHdrFtrImpl.this.removeCustomXmlMoveFromRangeStart(n);
                    return customXmlMoveFromRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfCustomXmlMoveFromRangeStartArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.CUSTOMXMLMOVEFROMRANGESTART$38, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlMoveFromRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTHdrFtrImpl.CUSTOMXMLMOVEFROMRANGESTART$38, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.CUSTOMXMLMOVEFROMRANGESTART$38);
        }
    }
    
    public void setCustomXmlMoveFromRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.CUSTOMXMLMOVEFROMRANGESTART$38);
    }
    
    public void setCustomXmlMoveFromRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTHdrFtrImpl.CUSTOMXMLMOVEFROMRANGESTART$38, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTHdrFtrImpl.CUSTOMXMLMOVEFROMRANGESTART$38, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlMoveFromRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTHdrFtrImpl.CUSTOMXMLMOVEFROMRANGESTART$38);
        }
    }
    
    public void removeCustomXmlMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.CUSTOMXMLMOVEFROMRANGESTART$38, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlMoveFromRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveFromRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTHdrFtrImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlMoveFromRangeEndArray = CTHdrFtrImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                    CTHdrFtrImpl.this.setCustomXmlMoveFromRangeEndArray(n, ctMarkup);
                    return customXmlMoveFromRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTHdrFtrImpl.this.insertNewCustomXmlMoveFromRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlMoveFromRangeEndArray = CTHdrFtrImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                    CTHdrFtrImpl.this.removeCustomXmlMoveFromRangeEnd(n);
                    return customXmlMoveFromRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfCustomXmlMoveFromRangeEndArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.CUSTOMXMLMOVEFROMRANGEEND$40, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlMoveFromRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTHdrFtrImpl.CUSTOMXMLMOVEFROMRANGEEND$40, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.CUSTOMXMLMOVEFROMRANGEEND$40);
        }
    }
    
    public void setCustomXmlMoveFromRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.CUSTOMXMLMOVEFROMRANGEEND$40);
    }
    
    public void setCustomXmlMoveFromRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTHdrFtrImpl.CUSTOMXMLMOVEFROMRANGEEND$40, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTHdrFtrImpl.CUSTOMXMLMOVEFROMRANGEEND$40, n);
        }
    }
    
    public CTMarkup addNewCustomXmlMoveFromRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTHdrFtrImpl.CUSTOMXMLMOVEFROMRANGEEND$40);
        }
    }
    
    public void removeCustomXmlMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.CUSTOMXMLMOVEFROMRANGEEND$40, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlMoveToRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveToRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTHdrFtrImpl.this.getCustomXmlMoveToRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlMoveToRangeStartArray = CTHdrFtrImpl.this.getCustomXmlMoveToRangeStartArray(n);
                    CTHdrFtrImpl.this.setCustomXmlMoveToRangeStartArray(n, ctTrackChange);
                    return customXmlMoveToRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTHdrFtrImpl.this.insertNewCustomXmlMoveToRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlMoveToRangeStartArray = CTHdrFtrImpl.this.getCustomXmlMoveToRangeStartArray(n);
                    CTHdrFtrImpl.this.removeCustomXmlMoveToRangeStart(n);
                    return customXmlMoveToRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfCustomXmlMoveToRangeStartArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.CUSTOMXMLMOVETORANGESTART$42, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlMoveToRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTHdrFtrImpl.CUSTOMXMLMOVETORANGESTART$42, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.CUSTOMXMLMOVETORANGESTART$42);
        }
    }
    
    public void setCustomXmlMoveToRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.CUSTOMXMLMOVETORANGESTART$42);
    }
    
    public void setCustomXmlMoveToRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTHdrFtrImpl.CUSTOMXMLMOVETORANGESTART$42, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTHdrFtrImpl.CUSTOMXMLMOVETORANGESTART$42, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlMoveToRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTHdrFtrImpl.CUSTOMXMLMOVETORANGESTART$42);
        }
    }
    
    public void removeCustomXmlMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.CUSTOMXMLMOVETORANGESTART$42, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlMoveToRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveToRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTHdrFtrImpl.this.getCustomXmlMoveToRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlMoveToRangeEndArray = CTHdrFtrImpl.this.getCustomXmlMoveToRangeEndArray(n);
                    CTHdrFtrImpl.this.setCustomXmlMoveToRangeEndArray(n, ctMarkup);
                    return customXmlMoveToRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTHdrFtrImpl.this.insertNewCustomXmlMoveToRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlMoveToRangeEndArray = CTHdrFtrImpl.this.getCustomXmlMoveToRangeEndArray(n);
                    CTHdrFtrImpl.this.removeCustomXmlMoveToRangeEnd(n);
                    return customXmlMoveToRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfCustomXmlMoveToRangeEndArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.CUSTOMXMLMOVETORANGEEND$44, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlMoveToRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTHdrFtrImpl.CUSTOMXMLMOVETORANGEEND$44, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.CUSTOMXMLMOVETORANGEEND$44);
        }
    }
    
    public void setCustomXmlMoveToRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.CUSTOMXMLMOVETORANGEEND$44);
    }
    
    public void setCustomXmlMoveToRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTHdrFtrImpl.CUSTOMXMLMOVETORANGEEND$44, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTHdrFtrImpl.CUSTOMXMLMOVETORANGEEND$44, n);
        }
    }
    
    public CTMarkup addNewCustomXmlMoveToRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTHdrFtrImpl.CUSTOMXMLMOVETORANGEEND$44);
        }
    }
    
    public void removeCustomXmlMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.CUSTOMXMLMOVETORANGEEND$44, n);
        }
    }
    
    public List<CTRunTrackChange> getInsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class InsList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTHdrFtrImpl.this.getInsArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange insArray = CTHdrFtrImpl.this.getInsArray(n);
                    CTHdrFtrImpl.this.setInsArray(n, ctRunTrackChange);
                    return insArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTHdrFtrImpl.this.insertNewIns(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange insArray = CTHdrFtrImpl.this.getInsArray(n);
                    CTHdrFtrImpl.this.removeIns(n);
                    return insArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfInsArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.INS$46, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getInsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTHdrFtrImpl.INS$46, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfInsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.INS$46);
        }
    }
    
    public void setInsArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.INS$46);
    }
    
    public void setInsArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTHdrFtrImpl.INS$46, n, (short)2);
    }
    
    public CTRunTrackChange insertNewIns(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTHdrFtrImpl.INS$46, n);
        }
    }
    
    public CTRunTrackChange addNewIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTHdrFtrImpl.INS$46);
        }
    }
    
    public void removeIns(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.INS$46, n);
        }
    }
    
    public List<CTRunTrackChange> getDelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DelList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTHdrFtrImpl.this.getDelArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange delArray = CTHdrFtrImpl.this.getDelArray(n);
                    CTHdrFtrImpl.this.setDelArray(n, ctRunTrackChange);
                    return delArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTHdrFtrImpl.this.insertNewDel(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange delArray = CTHdrFtrImpl.this.getDelArray(n);
                    CTHdrFtrImpl.this.removeDel(n);
                    return delArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfDelArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.DEL$48, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getDelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTHdrFtrImpl.DEL$48, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfDelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.DEL$48);
        }
    }
    
    public void setDelArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.DEL$48);
    }
    
    public void setDelArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTHdrFtrImpl.DEL$48, n, (short)2);
    }
    
    public CTRunTrackChange insertNewDel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTHdrFtrImpl.DEL$48, n);
        }
    }
    
    public CTRunTrackChange addNewDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTHdrFtrImpl.DEL$48);
        }
    }
    
    public void removeDel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.DEL$48, n);
        }
    }
    
    public List<CTRunTrackChange> getMoveFromList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTHdrFtrImpl.this.getMoveFromArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange moveFromArray = CTHdrFtrImpl.this.getMoveFromArray(n);
                    CTHdrFtrImpl.this.setMoveFromArray(n, ctRunTrackChange);
                    return moveFromArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTHdrFtrImpl.this.insertNewMoveFrom(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange moveFromArray = CTHdrFtrImpl.this.getMoveFromArray(n);
                    CTHdrFtrImpl.this.removeMoveFrom(n);
                    return moveFromArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfMoveFromArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.MOVEFROM$50, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getMoveFromArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTHdrFtrImpl.MOVEFROM$50, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfMoveFromArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.MOVEFROM$50);
        }
    }
    
    public void setMoveFromArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.MOVEFROM$50);
    }
    
    public void setMoveFromArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTHdrFtrImpl.MOVEFROM$50, n, (short)2);
    }
    
    public CTRunTrackChange insertNewMoveFrom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTHdrFtrImpl.MOVEFROM$50, n);
        }
    }
    
    public CTRunTrackChange addNewMoveFrom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTHdrFtrImpl.MOVEFROM$50);
        }
    }
    
    public void removeMoveFrom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.MOVEFROM$50, n);
        }
    }
    
    public List<CTRunTrackChange> getMoveToList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTHdrFtrImpl.this.getMoveToArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange moveToArray = CTHdrFtrImpl.this.getMoveToArray(n);
                    CTHdrFtrImpl.this.setMoveToArray(n, ctRunTrackChange);
                    return moveToArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTHdrFtrImpl.this.insertNewMoveTo(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange moveToArray = CTHdrFtrImpl.this.getMoveToArray(n);
                    CTHdrFtrImpl.this.removeMoveTo(n);
                    return moveToArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfMoveToArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.MOVETO$52, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getMoveToArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTHdrFtrImpl.MOVETO$52, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfMoveToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.MOVETO$52);
        }
    }
    
    public void setMoveToArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.MOVETO$52);
    }
    
    public void setMoveToArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTHdrFtrImpl.MOVETO$52, n, (short)2);
    }
    
    public CTRunTrackChange insertNewMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTHdrFtrImpl.MOVETO$52, n);
        }
    }
    
    public CTRunTrackChange addNewMoveTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTHdrFtrImpl.MOVETO$52);
        }
    }
    
    public void removeMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.MOVETO$52, n);
        }
    }
    
    public List<CTOMathPara> getOMathParaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OMathParaList extends AbstractList<CTOMathPara>
            {
                @Override
                public CTOMathPara get(final int n) {
                    return CTHdrFtrImpl.this.getOMathParaArray(n);
                }
                
                @Override
                public CTOMathPara set(final int n, final CTOMathPara ctoMathPara) {
                    final CTOMathPara oMathParaArray = CTHdrFtrImpl.this.getOMathParaArray(n);
                    CTHdrFtrImpl.this.setOMathParaArray(n, ctoMathPara);
                    return oMathParaArray;
                }
                
                @Override
                public void add(final int n, final CTOMathPara ctoMathPara) {
                    CTHdrFtrImpl.this.insertNewOMathPara(n).set((XmlObject)ctoMathPara);
                }
                
                @Override
                public CTOMathPara remove(final int n) {
                    final CTOMathPara oMathParaArray = CTHdrFtrImpl.this.getOMathParaArray(n);
                    CTHdrFtrImpl.this.removeOMathPara(n);
                    return oMathParaArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfOMathParaArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.OMATHPARA$54, (List)list);
            final CTOMathPara[] array = new CTOMathPara[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOMathPara getOMathParaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOMathPara ctoMathPara = (CTOMathPara)this.get_store().find_element_user(CTHdrFtrImpl.OMATHPARA$54, n);
            if (ctoMathPara == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctoMathPara;
        }
    }
    
    public int sizeOfOMathParaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.OMATHPARA$54);
        }
    }
    
    public void setOMathParaArray(final CTOMathPara[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.OMATHPARA$54);
    }
    
    public void setOMathParaArray(final int n, final CTOMathPara ctoMathPara) {
        this.generatedSetterHelperImpl((XmlObject)ctoMathPara, CTHdrFtrImpl.OMATHPARA$54, n, (short)2);
    }
    
    public CTOMathPara insertNewOMathPara(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMathPara)this.get_store().insert_element_user(CTHdrFtrImpl.OMATHPARA$54, n);
        }
    }
    
    public CTOMathPara addNewOMathPara() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMathPara)this.get_store().add_element_user(CTHdrFtrImpl.OMATHPARA$54);
        }
    }
    
    public void removeOMathPara(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.OMATHPARA$54, n);
        }
    }
    
    public List<CTOMath> getOMathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OMathList extends AbstractList<CTOMath>
            {
                @Override
                public CTOMath get(final int n) {
                    return CTHdrFtrImpl.this.getOMathArray(n);
                }
                
                @Override
                public CTOMath set(final int n, final CTOMath ctoMath) {
                    final CTOMath oMathArray = CTHdrFtrImpl.this.getOMathArray(n);
                    CTHdrFtrImpl.this.setOMathArray(n, ctoMath);
                    return oMathArray;
                }
                
                @Override
                public void add(final int n, final CTOMath ctoMath) {
                    CTHdrFtrImpl.this.insertNewOMath(n).set((XmlObject)ctoMath);
                }
                
                @Override
                public CTOMath remove(final int n) {
                    final CTOMath oMathArray = CTHdrFtrImpl.this.getOMathArray(n);
                    CTHdrFtrImpl.this.removeOMath(n);
                    return oMathArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfOMathArray();
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
            this.get_store().find_all_element_users(CTHdrFtrImpl.OMATH$56, (List)list);
            final CTOMath[] array = new CTOMath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOMath getOMathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOMath ctoMath = (CTOMath)this.get_store().find_element_user(CTHdrFtrImpl.OMATH$56, n);
            if (ctoMath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctoMath;
        }
    }
    
    public int sizeOfOMathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.OMATH$56);
        }
    }
    
    public void setOMathArray(final CTOMath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.OMATH$56);
    }
    
    public void setOMathArray(final int n, final CTOMath ctoMath) {
        this.generatedSetterHelperImpl((XmlObject)ctoMath, CTHdrFtrImpl.OMATH$56, n, (short)2);
    }
    
    public CTOMath insertNewOMath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMath)this.get_store().insert_element_user(CTHdrFtrImpl.OMATH$56, n);
        }
    }
    
    public CTOMath addNewOMath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMath)this.get_store().add_element_user(CTHdrFtrImpl.OMATH$56);
        }
    }
    
    public void removeOMath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.OMATH$56, n);
        }
    }
    
    public List<CTAltChunk> getAltChunkList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AltChunkList extends AbstractList<CTAltChunk>
            {
                @Override
                public CTAltChunk get(final int n) {
                    return CTHdrFtrImpl.this.getAltChunkArray(n);
                }
                
                @Override
                public CTAltChunk set(final int n, final CTAltChunk ctAltChunk) {
                    final CTAltChunk altChunkArray = CTHdrFtrImpl.this.getAltChunkArray(n);
                    CTHdrFtrImpl.this.setAltChunkArray(n, ctAltChunk);
                    return altChunkArray;
                }
                
                @Override
                public void add(final int n, final CTAltChunk ctAltChunk) {
                    CTHdrFtrImpl.this.insertNewAltChunk(n).set((XmlObject)ctAltChunk);
                }
                
                @Override
                public CTAltChunk remove(final int n) {
                    final CTAltChunk altChunkArray = CTHdrFtrImpl.this.getAltChunkArray(n);
                    CTHdrFtrImpl.this.removeAltChunk(n);
                    return altChunkArray;
                }
                
                @Override
                public int size() {
                    return CTHdrFtrImpl.this.sizeOfAltChunkArray();
                }
            }
            return new AltChunkList();
        }
    }
    
    @Deprecated
    public CTAltChunk[] getAltChunkArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTHdrFtrImpl.ALTCHUNK$58, (List)list);
            final CTAltChunk[] array = new CTAltChunk[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAltChunk getAltChunkArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAltChunk ctAltChunk = (CTAltChunk)this.get_store().find_element_user(CTHdrFtrImpl.ALTCHUNK$58, n);
            if (ctAltChunk == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAltChunk;
        }
    }
    
    public int sizeOfAltChunkArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHdrFtrImpl.ALTCHUNK$58);
        }
    }
    
    public void setAltChunkArray(final CTAltChunk[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHdrFtrImpl.ALTCHUNK$58);
    }
    
    public void setAltChunkArray(final int n, final CTAltChunk ctAltChunk) {
        this.generatedSetterHelperImpl((XmlObject)ctAltChunk, CTHdrFtrImpl.ALTCHUNK$58, n, (short)2);
    }
    
    public CTAltChunk insertNewAltChunk(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAltChunk)this.get_store().insert_element_user(CTHdrFtrImpl.ALTCHUNK$58, n);
        }
    }
    
    public CTAltChunk addNewAltChunk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAltChunk)this.get_store().add_element_user(CTHdrFtrImpl.ALTCHUNK$58);
        }
    }
    
    public void removeAltChunk(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHdrFtrImpl.ALTCHUNK$58, n);
        }
    }
    
    static {
        CUSTOMXML$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXml");
        SDT$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sdt");
        P$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "p");
        TBL$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tbl");
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
        ALTCHUNK$58 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "altChunk");
    }
}
