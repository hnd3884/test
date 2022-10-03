package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLongHexNumber;
import org.apache.xmlbeans.SimpleValue;
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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCustomXmlCell;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import java.util.List;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPrEx;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRowImpl extends XmlComplexContentImpl implements CTRow
{
    private static final long serialVersionUID = 1L;
    private static final QName TBLPREX$0;
    private static final QName TRPR$2;
    private static final QName TC$4;
    private static final QName CUSTOMXML$6;
    private static final QName SDT$8;
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
    private static final QName RSIDRPR$60;
    private static final QName RSIDR$62;
    private static final QName RSIDDEL$64;
    private static final QName RSIDTR$66;
    
    public CTRowImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTblPrEx getTblPrEx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblPrEx ctTblPrEx = (CTTblPrEx)this.get_store().find_element_user(CTRowImpl.TBLPREX$0, 0);
            if (ctTblPrEx == null) {
                return null;
            }
            return ctTblPrEx;
        }
    }
    
    public boolean isSetTblPrEx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.TBLPREX$0) != 0;
        }
    }
    
    public void setTblPrEx(final CTTblPrEx ctTblPrEx) {
        this.generatedSetterHelperImpl((XmlObject)ctTblPrEx, CTRowImpl.TBLPREX$0, 0, (short)1);
    }
    
    public CTTblPrEx addNewTblPrEx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblPrEx)this.get_store().add_element_user(CTRowImpl.TBLPREX$0);
        }
    }
    
    public void unsetTblPrEx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.TBLPREX$0, 0);
        }
    }
    
    public CTTrPr getTrPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrPr ctTrPr = (CTTrPr)this.get_store().find_element_user(CTRowImpl.TRPR$2, 0);
            if (ctTrPr == null) {
                return null;
            }
            return ctTrPr;
        }
    }
    
    public boolean isSetTrPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.TRPR$2) != 0;
        }
    }
    
    public void setTrPr(final CTTrPr ctTrPr) {
        this.generatedSetterHelperImpl((XmlObject)ctTrPr, CTRowImpl.TRPR$2, 0, (short)1);
    }
    
    public CTTrPr addNewTrPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrPr)this.get_store().add_element_user(CTRowImpl.TRPR$2);
        }
    }
    
    public void unsetTrPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.TRPR$2, 0);
        }
    }
    
    public List<CTTc> getTcList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TcList extends AbstractList<CTTc>
            {
                @Override
                public CTTc get(final int n) {
                    return CTRowImpl.this.getTcArray(n);
                }
                
                @Override
                public CTTc set(final int n, final CTTc ctTc) {
                    final CTTc tcArray = CTRowImpl.this.getTcArray(n);
                    CTRowImpl.this.setTcArray(n, ctTc);
                    return tcArray;
                }
                
                @Override
                public void add(final int n, final CTTc ctTc) {
                    CTRowImpl.this.insertNewTc(n).set((XmlObject)ctTc);
                }
                
                @Override
                public CTTc remove(final int n) {
                    final CTTc tcArray = CTRowImpl.this.getTcArray(n);
                    CTRowImpl.this.removeTc(n);
                    return tcArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfTcArray();
                }
            }
            return new TcList();
        }
    }
    
    @Deprecated
    public CTTc[] getTcArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRowImpl.TC$4, (List)list);
            final CTTc[] array = new CTTc[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTc getTcArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTc ctTc = (CTTc)this.get_store().find_element_user(CTRowImpl.TC$4, n);
            if (ctTc == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTc;
        }
    }
    
    public int sizeOfTcArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.TC$4);
        }
    }
    
    public void setTcArray(final CTTc[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.TC$4);
    }
    
    public void setTcArray(final int n, final CTTc ctTc) {
        this.generatedSetterHelperImpl((XmlObject)ctTc, CTRowImpl.TC$4, n, (short)2);
    }
    
    public CTTc insertNewTc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTc)this.get_store().insert_element_user(CTRowImpl.TC$4, n);
        }
    }
    
    public CTTc addNewTc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTc)this.get_store().add_element_user(CTRowImpl.TC$4);
        }
    }
    
    public void removeTc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.TC$4, n);
        }
    }
    
    public List<CTCustomXmlCell> getCustomXmlList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlList extends AbstractList<CTCustomXmlCell>
            {
                @Override
                public CTCustomXmlCell get(final int n) {
                    return CTRowImpl.this.getCustomXmlArray(n);
                }
                
                @Override
                public CTCustomXmlCell set(final int n, final CTCustomXmlCell ctCustomXmlCell) {
                    final CTCustomXmlCell customXmlArray = CTRowImpl.this.getCustomXmlArray(n);
                    CTRowImpl.this.setCustomXmlArray(n, ctCustomXmlCell);
                    return customXmlArray;
                }
                
                @Override
                public void add(final int n, final CTCustomXmlCell ctCustomXmlCell) {
                    CTRowImpl.this.insertNewCustomXml(n).set((XmlObject)ctCustomXmlCell);
                }
                
                @Override
                public CTCustomXmlCell remove(final int n) {
                    final CTCustomXmlCell customXmlArray = CTRowImpl.this.getCustomXmlArray(n);
                    CTRowImpl.this.removeCustomXml(n);
                    return customXmlArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfCustomXmlArray();
                }
            }
            return new CustomXmlList();
        }
    }
    
    @Deprecated
    public CTCustomXmlCell[] getCustomXmlArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRowImpl.CUSTOMXML$6, (List)list);
            final CTCustomXmlCell[] array = new CTCustomXmlCell[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCustomXmlCell getCustomXmlArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomXmlCell ctCustomXmlCell = (CTCustomXmlCell)this.get_store().find_element_user(CTRowImpl.CUSTOMXML$6, n);
            if (ctCustomXmlCell == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCustomXmlCell;
        }
    }
    
    public int sizeOfCustomXmlArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.CUSTOMXML$6);
        }
    }
    
    public void setCustomXmlArray(final CTCustomXmlCell[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.CUSTOMXML$6);
    }
    
    public void setCustomXmlArray(final int n, final CTCustomXmlCell ctCustomXmlCell) {
        this.generatedSetterHelperImpl((XmlObject)ctCustomXmlCell, CTRowImpl.CUSTOMXML$6, n, (short)2);
    }
    
    public CTCustomXmlCell insertNewCustomXml(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomXmlCell)this.get_store().insert_element_user(CTRowImpl.CUSTOMXML$6, n);
        }
    }
    
    public CTCustomXmlCell addNewCustomXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomXmlCell)this.get_store().add_element_user(CTRowImpl.CUSTOMXML$6);
        }
    }
    
    public void removeCustomXml(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.CUSTOMXML$6, n);
        }
    }
    
    public List<CTSdtCell> getSdtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SdtList extends AbstractList<CTSdtCell>
            {
                @Override
                public CTSdtCell get(final int n) {
                    return CTRowImpl.this.getSdtArray(n);
                }
                
                @Override
                public CTSdtCell set(final int n, final CTSdtCell ctSdtCell) {
                    final CTSdtCell sdtArray = CTRowImpl.this.getSdtArray(n);
                    CTRowImpl.this.setSdtArray(n, ctSdtCell);
                    return sdtArray;
                }
                
                @Override
                public void add(final int n, final CTSdtCell ctSdtCell) {
                    CTRowImpl.this.insertNewSdt(n).set((XmlObject)ctSdtCell);
                }
                
                @Override
                public CTSdtCell remove(final int n) {
                    final CTSdtCell sdtArray = CTRowImpl.this.getSdtArray(n);
                    CTRowImpl.this.removeSdt(n);
                    return sdtArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfSdtArray();
                }
            }
            return new SdtList();
        }
    }
    
    @Deprecated
    public CTSdtCell[] getSdtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRowImpl.SDT$8, (List)list);
            final CTSdtCell[] array = new CTSdtCell[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSdtCell getSdtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtCell ctSdtCell = (CTSdtCell)this.get_store().find_element_user(CTRowImpl.SDT$8, n);
            if (ctSdtCell == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSdtCell;
        }
    }
    
    public int sizeOfSdtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.SDT$8);
        }
    }
    
    public void setSdtArray(final CTSdtCell[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.SDT$8);
    }
    
    public void setSdtArray(final int n, final CTSdtCell ctSdtCell) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtCell, CTRowImpl.SDT$8, n, (short)2);
    }
    
    public CTSdtCell insertNewSdt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtCell)this.get_store().insert_element_user(CTRowImpl.SDT$8, n);
        }
    }
    
    public CTSdtCell addNewSdt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtCell)this.get_store().add_element_user(CTRowImpl.SDT$8);
        }
    }
    
    public void removeSdt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.SDT$8, n);
        }
    }
    
    public List<CTProofErr> getProofErrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ProofErrList extends AbstractList<CTProofErr>
            {
                @Override
                public CTProofErr get(final int n) {
                    return CTRowImpl.this.getProofErrArray(n);
                }
                
                @Override
                public CTProofErr set(final int n, final CTProofErr ctProofErr) {
                    final CTProofErr proofErrArray = CTRowImpl.this.getProofErrArray(n);
                    CTRowImpl.this.setProofErrArray(n, ctProofErr);
                    return proofErrArray;
                }
                
                @Override
                public void add(final int n, final CTProofErr ctProofErr) {
                    CTRowImpl.this.insertNewProofErr(n).set((XmlObject)ctProofErr);
                }
                
                @Override
                public CTProofErr remove(final int n) {
                    final CTProofErr proofErrArray = CTRowImpl.this.getProofErrArray(n);
                    CTRowImpl.this.removeProofErr(n);
                    return proofErrArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfProofErrArray();
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
            this.get_store().find_all_element_users(CTRowImpl.PROOFERR$10, (List)list);
            final CTProofErr[] array = new CTProofErr[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTProofErr getProofErrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTProofErr ctProofErr = (CTProofErr)this.get_store().find_element_user(CTRowImpl.PROOFERR$10, n);
            if (ctProofErr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctProofErr;
        }
    }
    
    public int sizeOfProofErrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.PROOFERR$10);
        }
    }
    
    public void setProofErrArray(final CTProofErr[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.PROOFERR$10);
    }
    
    public void setProofErrArray(final int n, final CTProofErr ctProofErr) {
        this.generatedSetterHelperImpl((XmlObject)ctProofErr, CTRowImpl.PROOFERR$10, n, (short)2);
    }
    
    public CTProofErr insertNewProofErr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProofErr)this.get_store().insert_element_user(CTRowImpl.PROOFERR$10, n);
        }
    }
    
    public CTProofErr addNewProofErr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProofErr)this.get_store().add_element_user(CTRowImpl.PROOFERR$10);
        }
    }
    
    public void removeProofErr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.PROOFERR$10, n);
        }
    }
    
    public List<CTPermStart> getPermStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PermStartList extends AbstractList<CTPermStart>
            {
                @Override
                public CTPermStart get(final int n) {
                    return CTRowImpl.this.getPermStartArray(n);
                }
                
                @Override
                public CTPermStart set(final int n, final CTPermStart ctPermStart) {
                    final CTPermStart permStartArray = CTRowImpl.this.getPermStartArray(n);
                    CTRowImpl.this.setPermStartArray(n, ctPermStart);
                    return permStartArray;
                }
                
                @Override
                public void add(final int n, final CTPermStart ctPermStart) {
                    CTRowImpl.this.insertNewPermStart(n).set((XmlObject)ctPermStart);
                }
                
                @Override
                public CTPermStart remove(final int n) {
                    final CTPermStart permStartArray = CTRowImpl.this.getPermStartArray(n);
                    CTRowImpl.this.removePermStart(n);
                    return permStartArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfPermStartArray();
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
            this.get_store().find_all_element_users(CTRowImpl.PERMSTART$12, (List)list);
            final CTPermStart[] array = new CTPermStart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPermStart getPermStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPermStart ctPermStart = (CTPermStart)this.get_store().find_element_user(CTRowImpl.PERMSTART$12, n);
            if (ctPermStart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPermStart;
        }
    }
    
    public int sizeOfPermStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.PERMSTART$12);
        }
    }
    
    public void setPermStartArray(final CTPermStart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.PERMSTART$12);
    }
    
    public void setPermStartArray(final int n, final CTPermStart ctPermStart) {
        this.generatedSetterHelperImpl((XmlObject)ctPermStart, CTRowImpl.PERMSTART$12, n, (short)2);
    }
    
    public CTPermStart insertNewPermStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPermStart)this.get_store().insert_element_user(CTRowImpl.PERMSTART$12, n);
        }
    }
    
    public CTPermStart addNewPermStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPermStart)this.get_store().add_element_user(CTRowImpl.PERMSTART$12);
        }
    }
    
    public void removePermStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.PERMSTART$12, n);
        }
    }
    
    public List<CTPerm> getPermEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PermEndList extends AbstractList<CTPerm>
            {
                @Override
                public CTPerm get(final int n) {
                    return CTRowImpl.this.getPermEndArray(n);
                }
                
                @Override
                public CTPerm set(final int n, final CTPerm ctPerm) {
                    final CTPerm permEndArray = CTRowImpl.this.getPermEndArray(n);
                    CTRowImpl.this.setPermEndArray(n, ctPerm);
                    return permEndArray;
                }
                
                @Override
                public void add(final int n, final CTPerm ctPerm) {
                    CTRowImpl.this.insertNewPermEnd(n).set((XmlObject)ctPerm);
                }
                
                @Override
                public CTPerm remove(final int n) {
                    final CTPerm permEndArray = CTRowImpl.this.getPermEndArray(n);
                    CTRowImpl.this.removePermEnd(n);
                    return permEndArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfPermEndArray();
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
            this.get_store().find_all_element_users(CTRowImpl.PERMEND$14, (List)list);
            final CTPerm[] array = new CTPerm[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPerm getPermEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPerm ctPerm = (CTPerm)this.get_store().find_element_user(CTRowImpl.PERMEND$14, n);
            if (ctPerm == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPerm;
        }
    }
    
    public int sizeOfPermEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.PERMEND$14);
        }
    }
    
    public void setPermEndArray(final CTPerm[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.PERMEND$14);
    }
    
    public void setPermEndArray(final int n, final CTPerm ctPerm) {
        this.generatedSetterHelperImpl((XmlObject)ctPerm, CTRowImpl.PERMEND$14, n, (short)2);
    }
    
    public CTPerm insertNewPermEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerm)this.get_store().insert_element_user(CTRowImpl.PERMEND$14, n);
        }
    }
    
    public CTPerm addNewPermEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerm)this.get_store().add_element_user(CTRowImpl.PERMEND$14);
        }
    }
    
    public void removePermEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.PERMEND$14, n);
        }
    }
    
    public List<CTBookmark> getBookmarkStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BookmarkStartList extends AbstractList<CTBookmark>
            {
                @Override
                public CTBookmark get(final int n) {
                    return CTRowImpl.this.getBookmarkStartArray(n);
                }
                
                @Override
                public CTBookmark set(final int n, final CTBookmark ctBookmark) {
                    final CTBookmark bookmarkStartArray = CTRowImpl.this.getBookmarkStartArray(n);
                    CTRowImpl.this.setBookmarkStartArray(n, ctBookmark);
                    return bookmarkStartArray;
                }
                
                @Override
                public void add(final int n, final CTBookmark ctBookmark) {
                    CTRowImpl.this.insertNewBookmarkStart(n).set((XmlObject)ctBookmark);
                }
                
                @Override
                public CTBookmark remove(final int n) {
                    final CTBookmark bookmarkStartArray = CTRowImpl.this.getBookmarkStartArray(n);
                    CTRowImpl.this.removeBookmarkStart(n);
                    return bookmarkStartArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfBookmarkStartArray();
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
            this.get_store().find_all_element_users(CTRowImpl.BOOKMARKSTART$16, (List)list);
            final CTBookmark[] array = new CTBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBookmark getBookmarkStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBookmark ctBookmark = (CTBookmark)this.get_store().find_element_user(CTRowImpl.BOOKMARKSTART$16, n);
            if (ctBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBookmark;
        }
    }
    
    public int sizeOfBookmarkStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.BOOKMARKSTART$16);
        }
    }
    
    public void setBookmarkStartArray(final CTBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.BOOKMARKSTART$16);
    }
    
    public void setBookmarkStartArray(final int n, final CTBookmark ctBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctBookmark, CTRowImpl.BOOKMARKSTART$16, n, (short)2);
    }
    
    public CTBookmark insertNewBookmarkStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookmark)this.get_store().insert_element_user(CTRowImpl.BOOKMARKSTART$16, n);
        }
    }
    
    public CTBookmark addNewBookmarkStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookmark)this.get_store().add_element_user(CTRowImpl.BOOKMARKSTART$16);
        }
    }
    
    public void removeBookmarkStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.BOOKMARKSTART$16, n);
        }
    }
    
    public List<CTMarkupRange> getBookmarkEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BookmarkEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTRowImpl.this.getBookmarkEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange bookmarkEndArray = CTRowImpl.this.getBookmarkEndArray(n);
                    CTRowImpl.this.setBookmarkEndArray(n, ctMarkupRange);
                    return bookmarkEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTRowImpl.this.insertNewBookmarkEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange bookmarkEndArray = CTRowImpl.this.getBookmarkEndArray(n);
                    CTRowImpl.this.removeBookmarkEnd(n);
                    return bookmarkEndArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfBookmarkEndArray();
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
            this.get_store().find_all_element_users(CTRowImpl.BOOKMARKEND$18, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getBookmarkEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTRowImpl.BOOKMARKEND$18, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfBookmarkEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.BOOKMARKEND$18);
        }
    }
    
    public void setBookmarkEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.BOOKMARKEND$18);
    }
    
    public void setBookmarkEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTRowImpl.BOOKMARKEND$18, n, (short)2);
    }
    
    public CTMarkupRange insertNewBookmarkEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTRowImpl.BOOKMARKEND$18, n);
        }
    }
    
    public CTMarkupRange addNewBookmarkEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTRowImpl.BOOKMARKEND$18);
        }
    }
    
    public void removeBookmarkEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.BOOKMARKEND$18, n);
        }
    }
    
    public List<CTMoveBookmark> getMoveFromRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromRangeStartList extends AbstractList<CTMoveBookmark>
            {
                @Override
                public CTMoveBookmark get(final int n) {
                    return CTRowImpl.this.getMoveFromRangeStartArray(n);
                }
                
                @Override
                public CTMoveBookmark set(final int n, final CTMoveBookmark ctMoveBookmark) {
                    final CTMoveBookmark moveFromRangeStartArray = CTRowImpl.this.getMoveFromRangeStartArray(n);
                    CTRowImpl.this.setMoveFromRangeStartArray(n, ctMoveBookmark);
                    return moveFromRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMoveBookmark ctMoveBookmark) {
                    CTRowImpl.this.insertNewMoveFromRangeStart(n).set((XmlObject)ctMoveBookmark);
                }
                
                @Override
                public CTMoveBookmark remove(final int n) {
                    final CTMoveBookmark moveFromRangeStartArray = CTRowImpl.this.getMoveFromRangeStartArray(n);
                    CTRowImpl.this.removeMoveFromRangeStart(n);
                    return moveFromRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfMoveFromRangeStartArray();
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
            this.get_store().find_all_element_users(CTRowImpl.MOVEFROMRANGESTART$20, (List)list);
            final CTMoveBookmark[] array = new CTMoveBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMoveBookmark getMoveFromRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMoveBookmark ctMoveBookmark = (CTMoveBookmark)this.get_store().find_element_user(CTRowImpl.MOVEFROMRANGESTART$20, n);
            if (ctMoveBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMoveBookmark;
        }
    }
    
    public int sizeOfMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.MOVEFROMRANGESTART$20);
        }
    }
    
    public void setMoveFromRangeStartArray(final CTMoveBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.MOVEFROMRANGESTART$20);
    }
    
    public void setMoveFromRangeStartArray(final int n, final CTMoveBookmark ctMoveBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctMoveBookmark, CTRowImpl.MOVEFROMRANGESTART$20, n, (short)2);
    }
    
    public CTMoveBookmark insertNewMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().insert_element_user(CTRowImpl.MOVEFROMRANGESTART$20, n);
        }
    }
    
    public CTMoveBookmark addNewMoveFromRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().add_element_user(CTRowImpl.MOVEFROMRANGESTART$20);
        }
    }
    
    public void removeMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.MOVEFROMRANGESTART$20, n);
        }
    }
    
    public List<CTMarkupRange> getMoveFromRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTRowImpl.this.getMoveFromRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange moveFromRangeEndArray = CTRowImpl.this.getMoveFromRangeEndArray(n);
                    CTRowImpl.this.setMoveFromRangeEndArray(n, ctMarkupRange);
                    return moveFromRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTRowImpl.this.insertNewMoveFromRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange moveFromRangeEndArray = CTRowImpl.this.getMoveFromRangeEndArray(n);
                    CTRowImpl.this.removeMoveFromRangeEnd(n);
                    return moveFromRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfMoveFromRangeEndArray();
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
            this.get_store().find_all_element_users(CTRowImpl.MOVEFROMRANGEEND$22, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getMoveFromRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTRowImpl.MOVEFROMRANGEEND$22, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.MOVEFROMRANGEEND$22);
        }
    }
    
    public void setMoveFromRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.MOVEFROMRANGEEND$22);
    }
    
    public void setMoveFromRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTRowImpl.MOVEFROMRANGEEND$22, n, (short)2);
    }
    
    public CTMarkupRange insertNewMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTRowImpl.MOVEFROMRANGEEND$22, n);
        }
    }
    
    public CTMarkupRange addNewMoveFromRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTRowImpl.MOVEFROMRANGEEND$22);
        }
    }
    
    public void removeMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.MOVEFROMRANGEEND$22, n);
        }
    }
    
    public List<CTMoveBookmark> getMoveToRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToRangeStartList extends AbstractList<CTMoveBookmark>
            {
                @Override
                public CTMoveBookmark get(final int n) {
                    return CTRowImpl.this.getMoveToRangeStartArray(n);
                }
                
                @Override
                public CTMoveBookmark set(final int n, final CTMoveBookmark ctMoveBookmark) {
                    final CTMoveBookmark moveToRangeStartArray = CTRowImpl.this.getMoveToRangeStartArray(n);
                    CTRowImpl.this.setMoveToRangeStartArray(n, ctMoveBookmark);
                    return moveToRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMoveBookmark ctMoveBookmark) {
                    CTRowImpl.this.insertNewMoveToRangeStart(n).set((XmlObject)ctMoveBookmark);
                }
                
                @Override
                public CTMoveBookmark remove(final int n) {
                    final CTMoveBookmark moveToRangeStartArray = CTRowImpl.this.getMoveToRangeStartArray(n);
                    CTRowImpl.this.removeMoveToRangeStart(n);
                    return moveToRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfMoveToRangeStartArray();
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
            this.get_store().find_all_element_users(CTRowImpl.MOVETORANGESTART$24, (List)list);
            final CTMoveBookmark[] array = new CTMoveBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMoveBookmark getMoveToRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMoveBookmark ctMoveBookmark = (CTMoveBookmark)this.get_store().find_element_user(CTRowImpl.MOVETORANGESTART$24, n);
            if (ctMoveBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMoveBookmark;
        }
    }
    
    public int sizeOfMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.MOVETORANGESTART$24);
        }
    }
    
    public void setMoveToRangeStartArray(final CTMoveBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.MOVETORANGESTART$24);
    }
    
    public void setMoveToRangeStartArray(final int n, final CTMoveBookmark ctMoveBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctMoveBookmark, CTRowImpl.MOVETORANGESTART$24, n, (short)2);
    }
    
    public CTMoveBookmark insertNewMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().insert_element_user(CTRowImpl.MOVETORANGESTART$24, n);
        }
    }
    
    public CTMoveBookmark addNewMoveToRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().add_element_user(CTRowImpl.MOVETORANGESTART$24);
        }
    }
    
    public void removeMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.MOVETORANGESTART$24, n);
        }
    }
    
    public List<CTMarkupRange> getMoveToRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTRowImpl.this.getMoveToRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange moveToRangeEndArray = CTRowImpl.this.getMoveToRangeEndArray(n);
                    CTRowImpl.this.setMoveToRangeEndArray(n, ctMarkupRange);
                    return moveToRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTRowImpl.this.insertNewMoveToRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange moveToRangeEndArray = CTRowImpl.this.getMoveToRangeEndArray(n);
                    CTRowImpl.this.removeMoveToRangeEnd(n);
                    return moveToRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfMoveToRangeEndArray();
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
            this.get_store().find_all_element_users(CTRowImpl.MOVETORANGEEND$26, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getMoveToRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTRowImpl.MOVETORANGEEND$26, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.MOVETORANGEEND$26);
        }
    }
    
    public void setMoveToRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.MOVETORANGEEND$26);
    }
    
    public void setMoveToRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTRowImpl.MOVETORANGEEND$26, n, (short)2);
    }
    
    public CTMarkupRange insertNewMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTRowImpl.MOVETORANGEEND$26, n);
        }
    }
    
    public CTMarkupRange addNewMoveToRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTRowImpl.MOVETORANGEEND$26);
        }
    }
    
    public void removeMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.MOVETORANGEEND$26, n);
        }
    }
    
    public List<CTMarkupRange> getCommentRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentRangeStartList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTRowImpl.this.getCommentRangeStartArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange commentRangeStartArray = CTRowImpl.this.getCommentRangeStartArray(n);
                    CTRowImpl.this.setCommentRangeStartArray(n, ctMarkupRange);
                    return commentRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTRowImpl.this.insertNewCommentRangeStart(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange commentRangeStartArray = CTRowImpl.this.getCommentRangeStartArray(n);
                    CTRowImpl.this.removeCommentRangeStart(n);
                    return commentRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfCommentRangeStartArray();
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
            this.get_store().find_all_element_users(CTRowImpl.COMMENTRANGESTART$28, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getCommentRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTRowImpl.COMMENTRANGESTART$28, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfCommentRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.COMMENTRANGESTART$28);
        }
    }
    
    public void setCommentRangeStartArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.COMMENTRANGESTART$28);
    }
    
    public void setCommentRangeStartArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTRowImpl.COMMENTRANGESTART$28, n, (short)2);
    }
    
    public CTMarkupRange insertNewCommentRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTRowImpl.COMMENTRANGESTART$28, n);
        }
    }
    
    public CTMarkupRange addNewCommentRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTRowImpl.COMMENTRANGESTART$28);
        }
    }
    
    public void removeCommentRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.COMMENTRANGESTART$28, n);
        }
    }
    
    public List<CTMarkupRange> getCommentRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTRowImpl.this.getCommentRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange commentRangeEndArray = CTRowImpl.this.getCommentRangeEndArray(n);
                    CTRowImpl.this.setCommentRangeEndArray(n, ctMarkupRange);
                    return commentRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTRowImpl.this.insertNewCommentRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange commentRangeEndArray = CTRowImpl.this.getCommentRangeEndArray(n);
                    CTRowImpl.this.removeCommentRangeEnd(n);
                    return commentRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfCommentRangeEndArray();
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
            this.get_store().find_all_element_users(CTRowImpl.COMMENTRANGEEND$30, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getCommentRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTRowImpl.COMMENTRANGEEND$30, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfCommentRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.COMMENTRANGEEND$30);
        }
    }
    
    public void setCommentRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.COMMENTRANGEEND$30);
    }
    
    public void setCommentRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTRowImpl.COMMENTRANGEEND$30, n, (short)2);
    }
    
    public CTMarkupRange insertNewCommentRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTRowImpl.COMMENTRANGEEND$30, n);
        }
    }
    
    public CTMarkupRange addNewCommentRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTRowImpl.COMMENTRANGEEND$30);
        }
    }
    
    public void removeCommentRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.COMMENTRANGEEND$30, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlInsRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlInsRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTRowImpl.this.getCustomXmlInsRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlInsRangeStartArray = CTRowImpl.this.getCustomXmlInsRangeStartArray(n);
                    CTRowImpl.this.setCustomXmlInsRangeStartArray(n, ctTrackChange);
                    return customXmlInsRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTRowImpl.this.insertNewCustomXmlInsRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlInsRangeStartArray = CTRowImpl.this.getCustomXmlInsRangeStartArray(n);
                    CTRowImpl.this.removeCustomXmlInsRangeStart(n);
                    return customXmlInsRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfCustomXmlInsRangeStartArray();
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
            this.get_store().find_all_element_users(CTRowImpl.CUSTOMXMLINSRANGESTART$32, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlInsRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTRowImpl.CUSTOMXMLINSRANGESTART$32, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlInsRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.CUSTOMXMLINSRANGESTART$32);
        }
    }
    
    public void setCustomXmlInsRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.CUSTOMXMLINSRANGESTART$32);
    }
    
    public void setCustomXmlInsRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTRowImpl.CUSTOMXMLINSRANGESTART$32, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlInsRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTRowImpl.CUSTOMXMLINSRANGESTART$32, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlInsRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTRowImpl.CUSTOMXMLINSRANGESTART$32);
        }
    }
    
    public void removeCustomXmlInsRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.CUSTOMXMLINSRANGESTART$32, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlInsRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlInsRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTRowImpl.this.getCustomXmlInsRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlInsRangeEndArray = CTRowImpl.this.getCustomXmlInsRangeEndArray(n);
                    CTRowImpl.this.setCustomXmlInsRangeEndArray(n, ctMarkup);
                    return customXmlInsRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTRowImpl.this.insertNewCustomXmlInsRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlInsRangeEndArray = CTRowImpl.this.getCustomXmlInsRangeEndArray(n);
                    CTRowImpl.this.removeCustomXmlInsRangeEnd(n);
                    return customXmlInsRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfCustomXmlInsRangeEndArray();
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
            this.get_store().find_all_element_users(CTRowImpl.CUSTOMXMLINSRANGEEND$34, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlInsRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTRowImpl.CUSTOMXMLINSRANGEEND$34, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlInsRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.CUSTOMXMLINSRANGEEND$34);
        }
    }
    
    public void setCustomXmlInsRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.CUSTOMXMLINSRANGEEND$34);
    }
    
    public void setCustomXmlInsRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTRowImpl.CUSTOMXMLINSRANGEEND$34, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlInsRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTRowImpl.CUSTOMXMLINSRANGEEND$34, n);
        }
    }
    
    public CTMarkup addNewCustomXmlInsRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTRowImpl.CUSTOMXMLINSRANGEEND$34);
        }
    }
    
    public void removeCustomXmlInsRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.CUSTOMXMLINSRANGEEND$34, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlDelRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlDelRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTRowImpl.this.getCustomXmlDelRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlDelRangeStartArray = CTRowImpl.this.getCustomXmlDelRangeStartArray(n);
                    CTRowImpl.this.setCustomXmlDelRangeStartArray(n, ctTrackChange);
                    return customXmlDelRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTRowImpl.this.insertNewCustomXmlDelRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlDelRangeStartArray = CTRowImpl.this.getCustomXmlDelRangeStartArray(n);
                    CTRowImpl.this.removeCustomXmlDelRangeStart(n);
                    return customXmlDelRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfCustomXmlDelRangeStartArray();
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
            this.get_store().find_all_element_users(CTRowImpl.CUSTOMXMLDELRANGESTART$36, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlDelRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTRowImpl.CUSTOMXMLDELRANGESTART$36, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlDelRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.CUSTOMXMLDELRANGESTART$36);
        }
    }
    
    public void setCustomXmlDelRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.CUSTOMXMLDELRANGESTART$36);
    }
    
    public void setCustomXmlDelRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTRowImpl.CUSTOMXMLDELRANGESTART$36, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlDelRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTRowImpl.CUSTOMXMLDELRANGESTART$36, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlDelRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTRowImpl.CUSTOMXMLDELRANGESTART$36);
        }
    }
    
    public void removeCustomXmlDelRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.CUSTOMXMLDELRANGESTART$36, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlDelRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlDelRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTRowImpl.this.getCustomXmlDelRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlDelRangeEndArray = CTRowImpl.this.getCustomXmlDelRangeEndArray(n);
                    CTRowImpl.this.setCustomXmlDelRangeEndArray(n, ctMarkup);
                    return customXmlDelRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTRowImpl.this.insertNewCustomXmlDelRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlDelRangeEndArray = CTRowImpl.this.getCustomXmlDelRangeEndArray(n);
                    CTRowImpl.this.removeCustomXmlDelRangeEnd(n);
                    return customXmlDelRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfCustomXmlDelRangeEndArray();
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
            this.get_store().find_all_element_users(CTRowImpl.CUSTOMXMLDELRANGEEND$38, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlDelRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTRowImpl.CUSTOMXMLDELRANGEEND$38, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlDelRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.CUSTOMXMLDELRANGEEND$38);
        }
    }
    
    public void setCustomXmlDelRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.CUSTOMXMLDELRANGEEND$38);
    }
    
    public void setCustomXmlDelRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTRowImpl.CUSTOMXMLDELRANGEEND$38, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlDelRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTRowImpl.CUSTOMXMLDELRANGEEND$38, n);
        }
    }
    
    public CTMarkup addNewCustomXmlDelRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTRowImpl.CUSTOMXMLDELRANGEEND$38);
        }
    }
    
    public void removeCustomXmlDelRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.CUSTOMXMLDELRANGEEND$38, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlMoveFromRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveFromRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTRowImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlMoveFromRangeStartArray = CTRowImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                    CTRowImpl.this.setCustomXmlMoveFromRangeStartArray(n, ctTrackChange);
                    return customXmlMoveFromRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTRowImpl.this.insertNewCustomXmlMoveFromRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlMoveFromRangeStartArray = CTRowImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                    CTRowImpl.this.removeCustomXmlMoveFromRangeStart(n);
                    return customXmlMoveFromRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfCustomXmlMoveFromRangeStartArray();
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
            this.get_store().find_all_element_users(CTRowImpl.CUSTOMXMLMOVEFROMRANGESTART$40, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlMoveFromRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTRowImpl.CUSTOMXMLMOVEFROMRANGESTART$40, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.CUSTOMXMLMOVEFROMRANGESTART$40);
        }
    }
    
    public void setCustomXmlMoveFromRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.CUSTOMXMLMOVEFROMRANGESTART$40);
    }
    
    public void setCustomXmlMoveFromRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTRowImpl.CUSTOMXMLMOVEFROMRANGESTART$40, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTRowImpl.CUSTOMXMLMOVEFROMRANGESTART$40, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlMoveFromRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTRowImpl.CUSTOMXMLMOVEFROMRANGESTART$40);
        }
    }
    
    public void removeCustomXmlMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.CUSTOMXMLMOVEFROMRANGESTART$40, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlMoveFromRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveFromRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTRowImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlMoveFromRangeEndArray = CTRowImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                    CTRowImpl.this.setCustomXmlMoveFromRangeEndArray(n, ctMarkup);
                    return customXmlMoveFromRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTRowImpl.this.insertNewCustomXmlMoveFromRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlMoveFromRangeEndArray = CTRowImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                    CTRowImpl.this.removeCustomXmlMoveFromRangeEnd(n);
                    return customXmlMoveFromRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfCustomXmlMoveFromRangeEndArray();
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
            this.get_store().find_all_element_users(CTRowImpl.CUSTOMXMLMOVEFROMRANGEEND$42, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlMoveFromRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTRowImpl.CUSTOMXMLMOVEFROMRANGEEND$42, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.CUSTOMXMLMOVEFROMRANGEEND$42);
        }
    }
    
    public void setCustomXmlMoveFromRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.CUSTOMXMLMOVEFROMRANGEEND$42);
    }
    
    public void setCustomXmlMoveFromRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTRowImpl.CUSTOMXMLMOVEFROMRANGEEND$42, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTRowImpl.CUSTOMXMLMOVEFROMRANGEEND$42, n);
        }
    }
    
    public CTMarkup addNewCustomXmlMoveFromRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTRowImpl.CUSTOMXMLMOVEFROMRANGEEND$42);
        }
    }
    
    public void removeCustomXmlMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.CUSTOMXMLMOVEFROMRANGEEND$42, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlMoveToRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveToRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTRowImpl.this.getCustomXmlMoveToRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlMoveToRangeStartArray = CTRowImpl.this.getCustomXmlMoveToRangeStartArray(n);
                    CTRowImpl.this.setCustomXmlMoveToRangeStartArray(n, ctTrackChange);
                    return customXmlMoveToRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTRowImpl.this.insertNewCustomXmlMoveToRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlMoveToRangeStartArray = CTRowImpl.this.getCustomXmlMoveToRangeStartArray(n);
                    CTRowImpl.this.removeCustomXmlMoveToRangeStart(n);
                    return customXmlMoveToRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfCustomXmlMoveToRangeStartArray();
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
            this.get_store().find_all_element_users(CTRowImpl.CUSTOMXMLMOVETORANGESTART$44, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlMoveToRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTRowImpl.CUSTOMXMLMOVETORANGESTART$44, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.CUSTOMXMLMOVETORANGESTART$44);
        }
    }
    
    public void setCustomXmlMoveToRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.CUSTOMXMLMOVETORANGESTART$44);
    }
    
    public void setCustomXmlMoveToRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTRowImpl.CUSTOMXMLMOVETORANGESTART$44, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTRowImpl.CUSTOMXMLMOVETORANGESTART$44, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlMoveToRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTRowImpl.CUSTOMXMLMOVETORANGESTART$44);
        }
    }
    
    public void removeCustomXmlMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.CUSTOMXMLMOVETORANGESTART$44, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlMoveToRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveToRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTRowImpl.this.getCustomXmlMoveToRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlMoveToRangeEndArray = CTRowImpl.this.getCustomXmlMoveToRangeEndArray(n);
                    CTRowImpl.this.setCustomXmlMoveToRangeEndArray(n, ctMarkup);
                    return customXmlMoveToRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTRowImpl.this.insertNewCustomXmlMoveToRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlMoveToRangeEndArray = CTRowImpl.this.getCustomXmlMoveToRangeEndArray(n);
                    CTRowImpl.this.removeCustomXmlMoveToRangeEnd(n);
                    return customXmlMoveToRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfCustomXmlMoveToRangeEndArray();
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
            this.get_store().find_all_element_users(CTRowImpl.CUSTOMXMLMOVETORANGEEND$46, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlMoveToRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTRowImpl.CUSTOMXMLMOVETORANGEEND$46, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.CUSTOMXMLMOVETORANGEEND$46);
        }
    }
    
    public void setCustomXmlMoveToRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.CUSTOMXMLMOVETORANGEEND$46);
    }
    
    public void setCustomXmlMoveToRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTRowImpl.CUSTOMXMLMOVETORANGEEND$46, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTRowImpl.CUSTOMXMLMOVETORANGEEND$46, n);
        }
    }
    
    public CTMarkup addNewCustomXmlMoveToRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTRowImpl.CUSTOMXMLMOVETORANGEEND$46);
        }
    }
    
    public void removeCustomXmlMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.CUSTOMXMLMOVETORANGEEND$46, n);
        }
    }
    
    public List<CTRunTrackChange> getInsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class InsList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTRowImpl.this.getInsArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange insArray = CTRowImpl.this.getInsArray(n);
                    CTRowImpl.this.setInsArray(n, ctRunTrackChange);
                    return insArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTRowImpl.this.insertNewIns(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange insArray = CTRowImpl.this.getInsArray(n);
                    CTRowImpl.this.removeIns(n);
                    return insArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfInsArray();
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
            this.get_store().find_all_element_users(CTRowImpl.INS$48, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getInsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTRowImpl.INS$48, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfInsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.INS$48);
        }
    }
    
    public void setInsArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.INS$48);
    }
    
    public void setInsArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTRowImpl.INS$48, n, (short)2);
    }
    
    public CTRunTrackChange insertNewIns(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTRowImpl.INS$48, n);
        }
    }
    
    public CTRunTrackChange addNewIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTRowImpl.INS$48);
        }
    }
    
    public void removeIns(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.INS$48, n);
        }
    }
    
    public List<CTRunTrackChange> getDelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DelList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTRowImpl.this.getDelArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange delArray = CTRowImpl.this.getDelArray(n);
                    CTRowImpl.this.setDelArray(n, ctRunTrackChange);
                    return delArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTRowImpl.this.insertNewDel(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange delArray = CTRowImpl.this.getDelArray(n);
                    CTRowImpl.this.removeDel(n);
                    return delArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfDelArray();
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
            this.get_store().find_all_element_users(CTRowImpl.DEL$50, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getDelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTRowImpl.DEL$50, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfDelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.DEL$50);
        }
    }
    
    public void setDelArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.DEL$50);
    }
    
    public void setDelArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTRowImpl.DEL$50, n, (short)2);
    }
    
    public CTRunTrackChange insertNewDel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTRowImpl.DEL$50, n);
        }
    }
    
    public CTRunTrackChange addNewDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTRowImpl.DEL$50);
        }
    }
    
    public void removeDel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.DEL$50, n);
        }
    }
    
    public List<CTRunTrackChange> getMoveFromList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTRowImpl.this.getMoveFromArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange moveFromArray = CTRowImpl.this.getMoveFromArray(n);
                    CTRowImpl.this.setMoveFromArray(n, ctRunTrackChange);
                    return moveFromArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTRowImpl.this.insertNewMoveFrom(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange moveFromArray = CTRowImpl.this.getMoveFromArray(n);
                    CTRowImpl.this.removeMoveFrom(n);
                    return moveFromArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfMoveFromArray();
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
            this.get_store().find_all_element_users(CTRowImpl.MOVEFROM$52, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getMoveFromArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTRowImpl.MOVEFROM$52, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfMoveFromArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.MOVEFROM$52);
        }
    }
    
    public void setMoveFromArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.MOVEFROM$52);
    }
    
    public void setMoveFromArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTRowImpl.MOVEFROM$52, n, (short)2);
    }
    
    public CTRunTrackChange insertNewMoveFrom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTRowImpl.MOVEFROM$52, n);
        }
    }
    
    public CTRunTrackChange addNewMoveFrom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTRowImpl.MOVEFROM$52);
        }
    }
    
    public void removeMoveFrom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.MOVEFROM$52, n);
        }
    }
    
    public List<CTRunTrackChange> getMoveToList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTRowImpl.this.getMoveToArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange moveToArray = CTRowImpl.this.getMoveToArray(n);
                    CTRowImpl.this.setMoveToArray(n, ctRunTrackChange);
                    return moveToArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTRowImpl.this.insertNewMoveTo(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange moveToArray = CTRowImpl.this.getMoveToArray(n);
                    CTRowImpl.this.removeMoveTo(n);
                    return moveToArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfMoveToArray();
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
            this.get_store().find_all_element_users(CTRowImpl.MOVETO$54, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getMoveToArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTRowImpl.MOVETO$54, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfMoveToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.MOVETO$54);
        }
    }
    
    public void setMoveToArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.MOVETO$54);
    }
    
    public void setMoveToArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTRowImpl.MOVETO$54, n, (short)2);
    }
    
    public CTRunTrackChange insertNewMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTRowImpl.MOVETO$54, n);
        }
    }
    
    public CTRunTrackChange addNewMoveTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTRowImpl.MOVETO$54);
        }
    }
    
    public void removeMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.MOVETO$54, n);
        }
    }
    
    public List<CTOMathPara> getOMathParaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OMathParaList extends AbstractList<CTOMathPara>
            {
                @Override
                public CTOMathPara get(final int n) {
                    return CTRowImpl.this.getOMathParaArray(n);
                }
                
                @Override
                public CTOMathPara set(final int n, final CTOMathPara ctoMathPara) {
                    final CTOMathPara oMathParaArray = CTRowImpl.this.getOMathParaArray(n);
                    CTRowImpl.this.setOMathParaArray(n, ctoMathPara);
                    return oMathParaArray;
                }
                
                @Override
                public void add(final int n, final CTOMathPara ctoMathPara) {
                    CTRowImpl.this.insertNewOMathPara(n).set((XmlObject)ctoMathPara);
                }
                
                @Override
                public CTOMathPara remove(final int n) {
                    final CTOMathPara oMathParaArray = CTRowImpl.this.getOMathParaArray(n);
                    CTRowImpl.this.removeOMathPara(n);
                    return oMathParaArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfOMathParaArray();
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
            this.get_store().find_all_element_users(CTRowImpl.OMATHPARA$56, (List)list);
            final CTOMathPara[] array = new CTOMathPara[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOMathPara getOMathParaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOMathPara ctoMathPara = (CTOMathPara)this.get_store().find_element_user(CTRowImpl.OMATHPARA$56, n);
            if (ctoMathPara == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctoMathPara;
        }
    }
    
    public int sizeOfOMathParaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.OMATHPARA$56);
        }
    }
    
    public void setOMathParaArray(final CTOMathPara[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.OMATHPARA$56);
    }
    
    public void setOMathParaArray(final int n, final CTOMathPara ctoMathPara) {
        this.generatedSetterHelperImpl((XmlObject)ctoMathPara, CTRowImpl.OMATHPARA$56, n, (short)2);
    }
    
    public CTOMathPara insertNewOMathPara(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMathPara)this.get_store().insert_element_user(CTRowImpl.OMATHPARA$56, n);
        }
    }
    
    public CTOMathPara addNewOMathPara() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMathPara)this.get_store().add_element_user(CTRowImpl.OMATHPARA$56);
        }
    }
    
    public void removeOMathPara(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.OMATHPARA$56, n);
        }
    }
    
    public List<CTOMath> getOMathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OMathList extends AbstractList<CTOMath>
            {
                @Override
                public CTOMath get(final int n) {
                    return CTRowImpl.this.getOMathArray(n);
                }
                
                @Override
                public CTOMath set(final int n, final CTOMath ctoMath) {
                    final CTOMath oMathArray = CTRowImpl.this.getOMathArray(n);
                    CTRowImpl.this.setOMathArray(n, ctoMath);
                    return oMathArray;
                }
                
                @Override
                public void add(final int n, final CTOMath ctoMath) {
                    CTRowImpl.this.insertNewOMath(n).set((XmlObject)ctoMath);
                }
                
                @Override
                public CTOMath remove(final int n) {
                    final CTOMath oMathArray = CTRowImpl.this.getOMathArray(n);
                    CTRowImpl.this.removeOMath(n);
                    return oMathArray;
                }
                
                @Override
                public int size() {
                    return CTRowImpl.this.sizeOfOMathArray();
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
            this.get_store().find_all_element_users(CTRowImpl.OMATH$58, (List)list);
            final CTOMath[] array = new CTOMath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOMath getOMathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOMath ctoMath = (CTOMath)this.get_store().find_element_user(CTRowImpl.OMATH$58, n);
            if (ctoMath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctoMath;
        }
    }
    
    public int sizeOfOMathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRowImpl.OMATH$58);
        }
    }
    
    public void setOMathArray(final CTOMath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRowImpl.OMATH$58);
    }
    
    public void setOMathArray(final int n, final CTOMath ctoMath) {
        this.generatedSetterHelperImpl((XmlObject)ctoMath, CTRowImpl.OMATH$58, n, (short)2);
    }
    
    public CTOMath insertNewOMath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMath)this.get_store().insert_element_user(CTRowImpl.OMATH$58, n);
        }
    }
    
    public CTOMath addNewOMath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMath)this.get_store().add_element_user(CTRowImpl.OMATH$58);
        }
    }
    
    public void removeOMath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRowImpl.OMATH$58, n);
        }
    }
    
    public byte[] getRsidRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.RSIDRPR$60);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STLongHexNumber xgetRsidRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLongHexNumber)this.get_store().find_attribute_user(CTRowImpl.RSIDRPR$60);
        }
    }
    
    public boolean isSetRsidRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowImpl.RSIDRPR$60) != null;
        }
    }
    
    public void setRsidRPr(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.RSIDRPR$60);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowImpl.RSIDRPR$60);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetRsidRPr(final STLongHexNumber stLongHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLongHexNumber stLongHexNumber2 = (STLongHexNumber)this.get_store().find_attribute_user(CTRowImpl.RSIDRPR$60);
            if (stLongHexNumber2 == null) {
                stLongHexNumber2 = (STLongHexNumber)this.get_store().add_attribute_user(CTRowImpl.RSIDRPR$60);
            }
            stLongHexNumber2.set((XmlObject)stLongHexNumber);
        }
    }
    
    public void unsetRsidRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowImpl.RSIDRPR$60);
        }
    }
    
    public byte[] getRsidR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.RSIDR$62);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STLongHexNumber xgetRsidR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLongHexNumber)this.get_store().find_attribute_user(CTRowImpl.RSIDR$62);
        }
    }
    
    public boolean isSetRsidR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowImpl.RSIDR$62) != null;
        }
    }
    
    public void setRsidR(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.RSIDR$62);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowImpl.RSIDR$62);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetRsidR(final STLongHexNumber stLongHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLongHexNumber stLongHexNumber2 = (STLongHexNumber)this.get_store().find_attribute_user(CTRowImpl.RSIDR$62);
            if (stLongHexNumber2 == null) {
                stLongHexNumber2 = (STLongHexNumber)this.get_store().add_attribute_user(CTRowImpl.RSIDR$62);
            }
            stLongHexNumber2.set((XmlObject)stLongHexNumber);
        }
    }
    
    public void unsetRsidR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowImpl.RSIDR$62);
        }
    }
    
    public byte[] getRsidDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.RSIDDEL$64);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STLongHexNumber xgetRsidDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLongHexNumber)this.get_store().find_attribute_user(CTRowImpl.RSIDDEL$64);
        }
    }
    
    public boolean isSetRsidDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowImpl.RSIDDEL$64) != null;
        }
    }
    
    public void setRsidDel(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.RSIDDEL$64);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowImpl.RSIDDEL$64);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetRsidDel(final STLongHexNumber stLongHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLongHexNumber stLongHexNumber2 = (STLongHexNumber)this.get_store().find_attribute_user(CTRowImpl.RSIDDEL$64);
            if (stLongHexNumber2 == null) {
                stLongHexNumber2 = (STLongHexNumber)this.get_store().add_attribute_user(CTRowImpl.RSIDDEL$64);
            }
            stLongHexNumber2.set((XmlObject)stLongHexNumber);
        }
    }
    
    public void unsetRsidDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowImpl.RSIDDEL$64);
        }
    }
    
    public byte[] getRsidTr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.RSIDTR$66);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STLongHexNumber xgetRsidTr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLongHexNumber)this.get_store().find_attribute_user(CTRowImpl.RSIDTR$66);
        }
    }
    
    public boolean isSetRsidTr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRowImpl.RSIDTR$66) != null;
        }
    }
    
    public void setRsidTr(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRowImpl.RSIDTR$66);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRowImpl.RSIDTR$66);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetRsidTr(final STLongHexNumber stLongHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLongHexNumber stLongHexNumber2 = (STLongHexNumber)this.get_store().find_attribute_user(CTRowImpl.RSIDTR$66);
            if (stLongHexNumber2 == null) {
                stLongHexNumber2 = (STLongHexNumber)this.get_store().add_attribute_user(CTRowImpl.RSIDTR$66);
            }
            stLongHexNumber2.set((XmlObject)stLongHexNumber);
        }
    }
    
    public void unsetRsidTr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRowImpl.RSIDTR$66);
        }
    }
    
    static {
        TBLPREX$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblPrEx");
        TRPR$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "trPr");
        TC$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tc");
        CUSTOMXML$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXml");
        SDT$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sdt");
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
        RSIDRPR$60 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsidRPr");
        RSIDR$62 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsidR");
        RSIDDEL$64 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsidDel");
        RSIDTR$66 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsidTr");
    }
}
