package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

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
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRubyContent;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRubyContentImpl extends XmlComplexContentImpl implements CTRubyContent
{
    private static final long serialVersionUID = 1L;
    private static final QName R$0;
    private static final QName PROOFERR$2;
    private static final QName PERMSTART$4;
    private static final QName PERMEND$6;
    private static final QName BOOKMARKSTART$8;
    private static final QName BOOKMARKEND$10;
    private static final QName MOVEFROMRANGESTART$12;
    private static final QName MOVEFROMRANGEEND$14;
    private static final QName MOVETORANGESTART$16;
    private static final QName MOVETORANGEEND$18;
    private static final QName COMMENTRANGESTART$20;
    private static final QName COMMENTRANGEEND$22;
    private static final QName CUSTOMXMLINSRANGESTART$24;
    private static final QName CUSTOMXMLINSRANGEEND$26;
    private static final QName CUSTOMXMLDELRANGESTART$28;
    private static final QName CUSTOMXMLDELRANGEEND$30;
    private static final QName CUSTOMXMLMOVEFROMRANGESTART$32;
    private static final QName CUSTOMXMLMOVEFROMRANGEEND$34;
    private static final QName CUSTOMXMLMOVETORANGESTART$36;
    private static final QName CUSTOMXMLMOVETORANGEEND$38;
    private static final QName INS$40;
    private static final QName DEL$42;
    private static final QName MOVEFROM$44;
    private static final QName MOVETO$46;
    private static final QName OMATHPARA$48;
    private static final QName OMATH$50;
    
    public CTRubyContentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTR> getRList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RList extends AbstractList<CTR>
            {
                @Override
                public CTR get(final int n) {
                    return CTRubyContentImpl.this.getRArray(n);
                }
                
                @Override
                public CTR set(final int n, final CTR ctr) {
                    final CTR rArray = CTRubyContentImpl.this.getRArray(n);
                    CTRubyContentImpl.this.setRArray(n, ctr);
                    return rArray;
                }
                
                @Override
                public void add(final int n, final CTR ctr) {
                    CTRubyContentImpl.this.insertNewR(n).set((XmlObject)ctr);
                }
                
                @Override
                public CTR remove(final int n) {
                    final CTR rArray = CTRubyContentImpl.this.getRArray(n);
                    CTRubyContentImpl.this.removeR(n);
                    return rArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfRArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.R$0, (List)list);
            final CTR[] array = new CTR[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTR getRArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTR ctr = (CTR)this.get_store().find_element_user(CTRubyContentImpl.R$0, n);
            if (ctr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctr;
        }
    }
    
    public int sizeOfRArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.R$0);
        }
    }
    
    public void setRArray(final CTR[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.R$0);
    }
    
    public void setRArray(final int n, final CTR ctr) {
        this.generatedSetterHelperImpl((XmlObject)ctr, CTRubyContentImpl.R$0, n, (short)2);
    }
    
    public CTR insertNewR(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTR)this.get_store().insert_element_user(CTRubyContentImpl.R$0, n);
        }
    }
    
    public CTR addNewR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTR)this.get_store().add_element_user(CTRubyContentImpl.R$0);
        }
    }
    
    public void removeR(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.R$0, n);
        }
    }
    
    public List<CTProofErr> getProofErrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ProofErrList extends AbstractList<CTProofErr>
            {
                @Override
                public CTProofErr get(final int n) {
                    return CTRubyContentImpl.this.getProofErrArray(n);
                }
                
                @Override
                public CTProofErr set(final int n, final CTProofErr ctProofErr) {
                    final CTProofErr proofErrArray = CTRubyContentImpl.this.getProofErrArray(n);
                    CTRubyContentImpl.this.setProofErrArray(n, ctProofErr);
                    return proofErrArray;
                }
                
                @Override
                public void add(final int n, final CTProofErr ctProofErr) {
                    CTRubyContentImpl.this.insertNewProofErr(n).set((XmlObject)ctProofErr);
                }
                
                @Override
                public CTProofErr remove(final int n) {
                    final CTProofErr proofErrArray = CTRubyContentImpl.this.getProofErrArray(n);
                    CTRubyContentImpl.this.removeProofErr(n);
                    return proofErrArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfProofErrArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.PROOFERR$2, (List)list);
            final CTProofErr[] array = new CTProofErr[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTProofErr getProofErrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTProofErr ctProofErr = (CTProofErr)this.get_store().find_element_user(CTRubyContentImpl.PROOFERR$2, n);
            if (ctProofErr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctProofErr;
        }
    }
    
    public int sizeOfProofErrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.PROOFERR$2);
        }
    }
    
    public void setProofErrArray(final CTProofErr[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.PROOFERR$2);
    }
    
    public void setProofErrArray(final int n, final CTProofErr ctProofErr) {
        this.generatedSetterHelperImpl((XmlObject)ctProofErr, CTRubyContentImpl.PROOFERR$2, n, (short)2);
    }
    
    public CTProofErr insertNewProofErr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProofErr)this.get_store().insert_element_user(CTRubyContentImpl.PROOFERR$2, n);
        }
    }
    
    public CTProofErr addNewProofErr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProofErr)this.get_store().add_element_user(CTRubyContentImpl.PROOFERR$2);
        }
    }
    
    public void removeProofErr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.PROOFERR$2, n);
        }
    }
    
    public List<CTPermStart> getPermStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PermStartList extends AbstractList<CTPermStart>
            {
                @Override
                public CTPermStart get(final int n) {
                    return CTRubyContentImpl.this.getPermStartArray(n);
                }
                
                @Override
                public CTPermStart set(final int n, final CTPermStart ctPermStart) {
                    final CTPermStart permStartArray = CTRubyContentImpl.this.getPermStartArray(n);
                    CTRubyContentImpl.this.setPermStartArray(n, ctPermStart);
                    return permStartArray;
                }
                
                @Override
                public void add(final int n, final CTPermStart ctPermStart) {
                    CTRubyContentImpl.this.insertNewPermStart(n).set((XmlObject)ctPermStart);
                }
                
                @Override
                public CTPermStart remove(final int n) {
                    final CTPermStart permStartArray = CTRubyContentImpl.this.getPermStartArray(n);
                    CTRubyContentImpl.this.removePermStart(n);
                    return permStartArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfPermStartArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.PERMSTART$4, (List)list);
            final CTPermStart[] array = new CTPermStart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPermStart getPermStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPermStart ctPermStart = (CTPermStart)this.get_store().find_element_user(CTRubyContentImpl.PERMSTART$4, n);
            if (ctPermStart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPermStart;
        }
    }
    
    public int sizeOfPermStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.PERMSTART$4);
        }
    }
    
    public void setPermStartArray(final CTPermStart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.PERMSTART$4);
    }
    
    public void setPermStartArray(final int n, final CTPermStart ctPermStart) {
        this.generatedSetterHelperImpl((XmlObject)ctPermStart, CTRubyContentImpl.PERMSTART$4, n, (short)2);
    }
    
    public CTPermStart insertNewPermStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPermStart)this.get_store().insert_element_user(CTRubyContentImpl.PERMSTART$4, n);
        }
    }
    
    public CTPermStart addNewPermStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPermStart)this.get_store().add_element_user(CTRubyContentImpl.PERMSTART$4);
        }
    }
    
    public void removePermStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.PERMSTART$4, n);
        }
    }
    
    public List<CTPerm> getPermEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PermEndList extends AbstractList<CTPerm>
            {
                @Override
                public CTPerm get(final int n) {
                    return CTRubyContentImpl.this.getPermEndArray(n);
                }
                
                @Override
                public CTPerm set(final int n, final CTPerm ctPerm) {
                    final CTPerm permEndArray = CTRubyContentImpl.this.getPermEndArray(n);
                    CTRubyContentImpl.this.setPermEndArray(n, ctPerm);
                    return permEndArray;
                }
                
                @Override
                public void add(final int n, final CTPerm ctPerm) {
                    CTRubyContentImpl.this.insertNewPermEnd(n).set((XmlObject)ctPerm);
                }
                
                @Override
                public CTPerm remove(final int n) {
                    final CTPerm permEndArray = CTRubyContentImpl.this.getPermEndArray(n);
                    CTRubyContentImpl.this.removePermEnd(n);
                    return permEndArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfPermEndArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.PERMEND$6, (List)list);
            final CTPerm[] array = new CTPerm[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPerm getPermEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPerm ctPerm = (CTPerm)this.get_store().find_element_user(CTRubyContentImpl.PERMEND$6, n);
            if (ctPerm == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPerm;
        }
    }
    
    public int sizeOfPermEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.PERMEND$6);
        }
    }
    
    public void setPermEndArray(final CTPerm[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.PERMEND$6);
    }
    
    public void setPermEndArray(final int n, final CTPerm ctPerm) {
        this.generatedSetterHelperImpl((XmlObject)ctPerm, CTRubyContentImpl.PERMEND$6, n, (short)2);
    }
    
    public CTPerm insertNewPermEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerm)this.get_store().insert_element_user(CTRubyContentImpl.PERMEND$6, n);
        }
    }
    
    public CTPerm addNewPermEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerm)this.get_store().add_element_user(CTRubyContentImpl.PERMEND$6);
        }
    }
    
    public void removePermEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.PERMEND$6, n);
        }
    }
    
    public List<CTBookmark> getBookmarkStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BookmarkStartList extends AbstractList<CTBookmark>
            {
                @Override
                public CTBookmark get(final int n) {
                    return CTRubyContentImpl.this.getBookmarkStartArray(n);
                }
                
                @Override
                public CTBookmark set(final int n, final CTBookmark ctBookmark) {
                    final CTBookmark bookmarkStartArray = CTRubyContentImpl.this.getBookmarkStartArray(n);
                    CTRubyContentImpl.this.setBookmarkStartArray(n, ctBookmark);
                    return bookmarkStartArray;
                }
                
                @Override
                public void add(final int n, final CTBookmark ctBookmark) {
                    CTRubyContentImpl.this.insertNewBookmarkStart(n).set((XmlObject)ctBookmark);
                }
                
                @Override
                public CTBookmark remove(final int n) {
                    final CTBookmark bookmarkStartArray = CTRubyContentImpl.this.getBookmarkStartArray(n);
                    CTRubyContentImpl.this.removeBookmarkStart(n);
                    return bookmarkStartArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfBookmarkStartArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.BOOKMARKSTART$8, (List)list);
            final CTBookmark[] array = new CTBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBookmark getBookmarkStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBookmark ctBookmark = (CTBookmark)this.get_store().find_element_user(CTRubyContentImpl.BOOKMARKSTART$8, n);
            if (ctBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBookmark;
        }
    }
    
    public int sizeOfBookmarkStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.BOOKMARKSTART$8);
        }
    }
    
    public void setBookmarkStartArray(final CTBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.BOOKMARKSTART$8);
    }
    
    public void setBookmarkStartArray(final int n, final CTBookmark ctBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctBookmark, CTRubyContentImpl.BOOKMARKSTART$8, n, (short)2);
    }
    
    public CTBookmark insertNewBookmarkStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookmark)this.get_store().insert_element_user(CTRubyContentImpl.BOOKMARKSTART$8, n);
        }
    }
    
    public CTBookmark addNewBookmarkStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookmark)this.get_store().add_element_user(CTRubyContentImpl.BOOKMARKSTART$8);
        }
    }
    
    public void removeBookmarkStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.BOOKMARKSTART$8, n);
        }
    }
    
    public List<CTMarkupRange> getBookmarkEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BookmarkEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTRubyContentImpl.this.getBookmarkEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange bookmarkEndArray = CTRubyContentImpl.this.getBookmarkEndArray(n);
                    CTRubyContentImpl.this.setBookmarkEndArray(n, ctMarkupRange);
                    return bookmarkEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTRubyContentImpl.this.insertNewBookmarkEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange bookmarkEndArray = CTRubyContentImpl.this.getBookmarkEndArray(n);
                    CTRubyContentImpl.this.removeBookmarkEnd(n);
                    return bookmarkEndArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfBookmarkEndArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.BOOKMARKEND$10, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getBookmarkEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTRubyContentImpl.BOOKMARKEND$10, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfBookmarkEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.BOOKMARKEND$10);
        }
    }
    
    public void setBookmarkEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.BOOKMARKEND$10);
    }
    
    public void setBookmarkEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTRubyContentImpl.BOOKMARKEND$10, n, (short)2);
    }
    
    public CTMarkupRange insertNewBookmarkEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTRubyContentImpl.BOOKMARKEND$10, n);
        }
    }
    
    public CTMarkupRange addNewBookmarkEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTRubyContentImpl.BOOKMARKEND$10);
        }
    }
    
    public void removeBookmarkEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.BOOKMARKEND$10, n);
        }
    }
    
    public List<CTMoveBookmark> getMoveFromRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromRangeStartList extends AbstractList<CTMoveBookmark>
            {
                @Override
                public CTMoveBookmark get(final int n) {
                    return CTRubyContentImpl.this.getMoveFromRangeStartArray(n);
                }
                
                @Override
                public CTMoveBookmark set(final int n, final CTMoveBookmark ctMoveBookmark) {
                    final CTMoveBookmark moveFromRangeStartArray = CTRubyContentImpl.this.getMoveFromRangeStartArray(n);
                    CTRubyContentImpl.this.setMoveFromRangeStartArray(n, ctMoveBookmark);
                    return moveFromRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMoveBookmark ctMoveBookmark) {
                    CTRubyContentImpl.this.insertNewMoveFromRangeStart(n).set((XmlObject)ctMoveBookmark);
                }
                
                @Override
                public CTMoveBookmark remove(final int n) {
                    final CTMoveBookmark moveFromRangeStartArray = CTRubyContentImpl.this.getMoveFromRangeStartArray(n);
                    CTRubyContentImpl.this.removeMoveFromRangeStart(n);
                    return moveFromRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfMoveFromRangeStartArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.MOVEFROMRANGESTART$12, (List)list);
            final CTMoveBookmark[] array = new CTMoveBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMoveBookmark getMoveFromRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMoveBookmark ctMoveBookmark = (CTMoveBookmark)this.get_store().find_element_user(CTRubyContentImpl.MOVEFROMRANGESTART$12, n);
            if (ctMoveBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMoveBookmark;
        }
    }
    
    public int sizeOfMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.MOVEFROMRANGESTART$12);
        }
    }
    
    public void setMoveFromRangeStartArray(final CTMoveBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.MOVEFROMRANGESTART$12);
    }
    
    public void setMoveFromRangeStartArray(final int n, final CTMoveBookmark ctMoveBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctMoveBookmark, CTRubyContentImpl.MOVEFROMRANGESTART$12, n, (short)2);
    }
    
    public CTMoveBookmark insertNewMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().insert_element_user(CTRubyContentImpl.MOVEFROMRANGESTART$12, n);
        }
    }
    
    public CTMoveBookmark addNewMoveFromRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().add_element_user(CTRubyContentImpl.MOVEFROMRANGESTART$12);
        }
    }
    
    public void removeMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.MOVEFROMRANGESTART$12, n);
        }
    }
    
    public List<CTMarkupRange> getMoveFromRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTRubyContentImpl.this.getMoveFromRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange moveFromRangeEndArray = CTRubyContentImpl.this.getMoveFromRangeEndArray(n);
                    CTRubyContentImpl.this.setMoveFromRangeEndArray(n, ctMarkupRange);
                    return moveFromRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTRubyContentImpl.this.insertNewMoveFromRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange moveFromRangeEndArray = CTRubyContentImpl.this.getMoveFromRangeEndArray(n);
                    CTRubyContentImpl.this.removeMoveFromRangeEnd(n);
                    return moveFromRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfMoveFromRangeEndArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.MOVEFROMRANGEEND$14, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getMoveFromRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTRubyContentImpl.MOVEFROMRANGEEND$14, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.MOVEFROMRANGEEND$14);
        }
    }
    
    public void setMoveFromRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.MOVEFROMRANGEEND$14);
    }
    
    public void setMoveFromRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTRubyContentImpl.MOVEFROMRANGEEND$14, n, (short)2);
    }
    
    public CTMarkupRange insertNewMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTRubyContentImpl.MOVEFROMRANGEEND$14, n);
        }
    }
    
    public CTMarkupRange addNewMoveFromRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTRubyContentImpl.MOVEFROMRANGEEND$14);
        }
    }
    
    public void removeMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.MOVEFROMRANGEEND$14, n);
        }
    }
    
    public List<CTMoveBookmark> getMoveToRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToRangeStartList extends AbstractList<CTMoveBookmark>
            {
                @Override
                public CTMoveBookmark get(final int n) {
                    return CTRubyContentImpl.this.getMoveToRangeStartArray(n);
                }
                
                @Override
                public CTMoveBookmark set(final int n, final CTMoveBookmark ctMoveBookmark) {
                    final CTMoveBookmark moveToRangeStartArray = CTRubyContentImpl.this.getMoveToRangeStartArray(n);
                    CTRubyContentImpl.this.setMoveToRangeStartArray(n, ctMoveBookmark);
                    return moveToRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMoveBookmark ctMoveBookmark) {
                    CTRubyContentImpl.this.insertNewMoveToRangeStart(n).set((XmlObject)ctMoveBookmark);
                }
                
                @Override
                public CTMoveBookmark remove(final int n) {
                    final CTMoveBookmark moveToRangeStartArray = CTRubyContentImpl.this.getMoveToRangeStartArray(n);
                    CTRubyContentImpl.this.removeMoveToRangeStart(n);
                    return moveToRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfMoveToRangeStartArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.MOVETORANGESTART$16, (List)list);
            final CTMoveBookmark[] array = new CTMoveBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMoveBookmark getMoveToRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMoveBookmark ctMoveBookmark = (CTMoveBookmark)this.get_store().find_element_user(CTRubyContentImpl.MOVETORANGESTART$16, n);
            if (ctMoveBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMoveBookmark;
        }
    }
    
    public int sizeOfMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.MOVETORANGESTART$16);
        }
    }
    
    public void setMoveToRangeStartArray(final CTMoveBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.MOVETORANGESTART$16);
    }
    
    public void setMoveToRangeStartArray(final int n, final CTMoveBookmark ctMoveBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctMoveBookmark, CTRubyContentImpl.MOVETORANGESTART$16, n, (short)2);
    }
    
    public CTMoveBookmark insertNewMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().insert_element_user(CTRubyContentImpl.MOVETORANGESTART$16, n);
        }
    }
    
    public CTMoveBookmark addNewMoveToRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().add_element_user(CTRubyContentImpl.MOVETORANGESTART$16);
        }
    }
    
    public void removeMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.MOVETORANGESTART$16, n);
        }
    }
    
    public List<CTMarkupRange> getMoveToRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTRubyContentImpl.this.getMoveToRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange moveToRangeEndArray = CTRubyContentImpl.this.getMoveToRangeEndArray(n);
                    CTRubyContentImpl.this.setMoveToRangeEndArray(n, ctMarkupRange);
                    return moveToRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTRubyContentImpl.this.insertNewMoveToRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange moveToRangeEndArray = CTRubyContentImpl.this.getMoveToRangeEndArray(n);
                    CTRubyContentImpl.this.removeMoveToRangeEnd(n);
                    return moveToRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfMoveToRangeEndArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.MOVETORANGEEND$18, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getMoveToRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTRubyContentImpl.MOVETORANGEEND$18, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.MOVETORANGEEND$18);
        }
    }
    
    public void setMoveToRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.MOVETORANGEEND$18);
    }
    
    public void setMoveToRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTRubyContentImpl.MOVETORANGEEND$18, n, (short)2);
    }
    
    public CTMarkupRange insertNewMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTRubyContentImpl.MOVETORANGEEND$18, n);
        }
    }
    
    public CTMarkupRange addNewMoveToRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTRubyContentImpl.MOVETORANGEEND$18);
        }
    }
    
    public void removeMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.MOVETORANGEEND$18, n);
        }
    }
    
    public List<CTMarkupRange> getCommentRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentRangeStartList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTRubyContentImpl.this.getCommentRangeStartArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange commentRangeStartArray = CTRubyContentImpl.this.getCommentRangeStartArray(n);
                    CTRubyContentImpl.this.setCommentRangeStartArray(n, ctMarkupRange);
                    return commentRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTRubyContentImpl.this.insertNewCommentRangeStart(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange commentRangeStartArray = CTRubyContentImpl.this.getCommentRangeStartArray(n);
                    CTRubyContentImpl.this.removeCommentRangeStart(n);
                    return commentRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfCommentRangeStartArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.COMMENTRANGESTART$20, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getCommentRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTRubyContentImpl.COMMENTRANGESTART$20, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfCommentRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.COMMENTRANGESTART$20);
        }
    }
    
    public void setCommentRangeStartArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.COMMENTRANGESTART$20);
    }
    
    public void setCommentRangeStartArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTRubyContentImpl.COMMENTRANGESTART$20, n, (short)2);
    }
    
    public CTMarkupRange insertNewCommentRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTRubyContentImpl.COMMENTRANGESTART$20, n);
        }
    }
    
    public CTMarkupRange addNewCommentRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTRubyContentImpl.COMMENTRANGESTART$20);
        }
    }
    
    public void removeCommentRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.COMMENTRANGESTART$20, n);
        }
    }
    
    public List<CTMarkupRange> getCommentRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTRubyContentImpl.this.getCommentRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange commentRangeEndArray = CTRubyContentImpl.this.getCommentRangeEndArray(n);
                    CTRubyContentImpl.this.setCommentRangeEndArray(n, ctMarkupRange);
                    return commentRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTRubyContentImpl.this.insertNewCommentRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange commentRangeEndArray = CTRubyContentImpl.this.getCommentRangeEndArray(n);
                    CTRubyContentImpl.this.removeCommentRangeEnd(n);
                    return commentRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfCommentRangeEndArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.COMMENTRANGEEND$22, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getCommentRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTRubyContentImpl.COMMENTRANGEEND$22, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfCommentRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.COMMENTRANGEEND$22);
        }
    }
    
    public void setCommentRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.COMMENTRANGEEND$22);
    }
    
    public void setCommentRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTRubyContentImpl.COMMENTRANGEEND$22, n, (short)2);
    }
    
    public CTMarkupRange insertNewCommentRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTRubyContentImpl.COMMENTRANGEEND$22, n);
        }
    }
    
    public CTMarkupRange addNewCommentRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTRubyContentImpl.COMMENTRANGEEND$22);
        }
    }
    
    public void removeCommentRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.COMMENTRANGEEND$22, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlInsRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlInsRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTRubyContentImpl.this.getCustomXmlInsRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlInsRangeStartArray = CTRubyContentImpl.this.getCustomXmlInsRangeStartArray(n);
                    CTRubyContentImpl.this.setCustomXmlInsRangeStartArray(n, ctTrackChange);
                    return customXmlInsRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTRubyContentImpl.this.insertNewCustomXmlInsRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlInsRangeStartArray = CTRubyContentImpl.this.getCustomXmlInsRangeStartArray(n);
                    CTRubyContentImpl.this.removeCustomXmlInsRangeStart(n);
                    return customXmlInsRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfCustomXmlInsRangeStartArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.CUSTOMXMLINSRANGESTART$24, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlInsRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTRubyContentImpl.CUSTOMXMLINSRANGESTART$24, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlInsRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.CUSTOMXMLINSRANGESTART$24);
        }
    }
    
    public void setCustomXmlInsRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.CUSTOMXMLINSRANGESTART$24);
    }
    
    public void setCustomXmlInsRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTRubyContentImpl.CUSTOMXMLINSRANGESTART$24, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlInsRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTRubyContentImpl.CUSTOMXMLINSRANGESTART$24, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlInsRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTRubyContentImpl.CUSTOMXMLINSRANGESTART$24);
        }
    }
    
    public void removeCustomXmlInsRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.CUSTOMXMLINSRANGESTART$24, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlInsRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlInsRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTRubyContentImpl.this.getCustomXmlInsRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlInsRangeEndArray = CTRubyContentImpl.this.getCustomXmlInsRangeEndArray(n);
                    CTRubyContentImpl.this.setCustomXmlInsRangeEndArray(n, ctMarkup);
                    return customXmlInsRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTRubyContentImpl.this.insertNewCustomXmlInsRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlInsRangeEndArray = CTRubyContentImpl.this.getCustomXmlInsRangeEndArray(n);
                    CTRubyContentImpl.this.removeCustomXmlInsRangeEnd(n);
                    return customXmlInsRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfCustomXmlInsRangeEndArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.CUSTOMXMLINSRANGEEND$26, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlInsRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTRubyContentImpl.CUSTOMXMLINSRANGEEND$26, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlInsRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.CUSTOMXMLINSRANGEEND$26);
        }
    }
    
    public void setCustomXmlInsRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.CUSTOMXMLINSRANGEEND$26);
    }
    
    public void setCustomXmlInsRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTRubyContentImpl.CUSTOMXMLINSRANGEEND$26, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlInsRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTRubyContentImpl.CUSTOMXMLINSRANGEEND$26, n);
        }
    }
    
    public CTMarkup addNewCustomXmlInsRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTRubyContentImpl.CUSTOMXMLINSRANGEEND$26);
        }
    }
    
    public void removeCustomXmlInsRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.CUSTOMXMLINSRANGEEND$26, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlDelRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlDelRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTRubyContentImpl.this.getCustomXmlDelRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlDelRangeStartArray = CTRubyContentImpl.this.getCustomXmlDelRangeStartArray(n);
                    CTRubyContentImpl.this.setCustomXmlDelRangeStartArray(n, ctTrackChange);
                    return customXmlDelRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTRubyContentImpl.this.insertNewCustomXmlDelRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlDelRangeStartArray = CTRubyContentImpl.this.getCustomXmlDelRangeStartArray(n);
                    CTRubyContentImpl.this.removeCustomXmlDelRangeStart(n);
                    return customXmlDelRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfCustomXmlDelRangeStartArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.CUSTOMXMLDELRANGESTART$28, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlDelRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTRubyContentImpl.CUSTOMXMLDELRANGESTART$28, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlDelRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.CUSTOMXMLDELRANGESTART$28);
        }
    }
    
    public void setCustomXmlDelRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.CUSTOMXMLDELRANGESTART$28);
    }
    
    public void setCustomXmlDelRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTRubyContentImpl.CUSTOMXMLDELRANGESTART$28, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlDelRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTRubyContentImpl.CUSTOMXMLDELRANGESTART$28, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlDelRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTRubyContentImpl.CUSTOMXMLDELRANGESTART$28);
        }
    }
    
    public void removeCustomXmlDelRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.CUSTOMXMLDELRANGESTART$28, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlDelRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlDelRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTRubyContentImpl.this.getCustomXmlDelRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlDelRangeEndArray = CTRubyContentImpl.this.getCustomXmlDelRangeEndArray(n);
                    CTRubyContentImpl.this.setCustomXmlDelRangeEndArray(n, ctMarkup);
                    return customXmlDelRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTRubyContentImpl.this.insertNewCustomXmlDelRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlDelRangeEndArray = CTRubyContentImpl.this.getCustomXmlDelRangeEndArray(n);
                    CTRubyContentImpl.this.removeCustomXmlDelRangeEnd(n);
                    return customXmlDelRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfCustomXmlDelRangeEndArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.CUSTOMXMLDELRANGEEND$30, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlDelRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTRubyContentImpl.CUSTOMXMLDELRANGEEND$30, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlDelRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.CUSTOMXMLDELRANGEEND$30);
        }
    }
    
    public void setCustomXmlDelRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.CUSTOMXMLDELRANGEEND$30);
    }
    
    public void setCustomXmlDelRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTRubyContentImpl.CUSTOMXMLDELRANGEEND$30, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlDelRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTRubyContentImpl.CUSTOMXMLDELRANGEEND$30, n);
        }
    }
    
    public CTMarkup addNewCustomXmlDelRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTRubyContentImpl.CUSTOMXMLDELRANGEEND$30);
        }
    }
    
    public void removeCustomXmlDelRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.CUSTOMXMLDELRANGEEND$30, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlMoveFromRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveFromRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTRubyContentImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlMoveFromRangeStartArray = CTRubyContentImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                    CTRubyContentImpl.this.setCustomXmlMoveFromRangeStartArray(n, ctTrackChange);
                    return customXmlMoveFromRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTRubyContentImpl.this.insertNewCustomXmlMoveFromRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlMoveFromRangeStartArray = CTRubyContentImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                    CTRubyContentImpl.this.removeCustomXmlMoveFromRangeStart(n);
                    return customXmlMoveFromRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfCustomXmlMoveFromRangeStartArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.CUSTOMXMLMOVEFROMRANGESTART$32, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlMoveFromRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTRubyContentImpl.CUSTOMXMLMOVEFROMRANGESTART$32, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.CUSTOMXMLMOVEFROMRANGESTART$32);
        }
    }
    
    public void setCustomXmlMoveFromRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.CUSTOMXMLMOVEFROMRANGESTART$32);
    }
    
    public void setCustomXmlMoveFromRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTRubyContentImpl.CUSTOMXMLMOVEFROMRANGESTART$32, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTRubyContentImpl.CUSTOMXMLMOVEFROMRANGESTART$32, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlMoveFromRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTRubyContentImpl.CUSTOMXMLMOVEFROMRANGESTART$32);
        }
    }
    
    public void removeCustomXmlMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.CUSTOMXMLMOVEFROMRANGESTART$32, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlMoveFromRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveFromRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTRubyContentImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlMoveFromRangeEndArray = CTRubyContentImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                    CTRubyContentImpl.this.setCustomXmlMoveFromRangeEndArray(n, ctMarkup);
                    return customXmlMoveFromRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTRubyContentImpl.this.insertNewCustomXmlMoveFromRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlMoveFromRangeEndArray = CTRubyContentImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                    CTRubyContentImpl.this.removeCustomXmlMoveFromRangeEnd(n);
                    return customXmlMoveFromRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfCustomXmlMoveFromRangeEndArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.CUSTOMXMLMOVEFROMRANGEEND$34, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlMoveFromRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTRubyContentImpl.CUSTOMXMLMOVEFROMRANGEEND$34, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.CUSTOMXMLMOVEFROMRANGEEND$34);
        }
    }
    
    public void setCustomXmlMoveFromRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.CUSTOMXMLMOVEFROMRANGEEND$34);
    }
    
    public void setCustomXmlMoveFromRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTRubyContentImpl.CUSTOMXMLMOVEFROMRANGEEND$34, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTRubyContentImpl.CUSTOMXMLMOVEFROMRANGEEND$34, n);
        }
    }
    
    public CTMarkup addNewCustomXmlMoveFromRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTRubyContentImpl.CUSTOMXMLMOVEFROMRANGEEND$34);
        }
    }
    
    public void removeCustomXmlMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.CUSTOMXMLMOVEFROMRANGEEND$34, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlMoveToRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveToRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTRubyContentImpl.this.getCustomXmlMoveToRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlMoveToRangeStartArray = CTRubyContentImpl.this.getCustomXmlMoveToRangeStartArray(n);
                    CTRubyContentImpl.this.setCustomXmlMoveToRangeStartArray(n, ctTrackChange);
                    return customXmlMoveToRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTRubyContentImpl.this.insertNewCustomXmlMoveToRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlMoveToRangeStartArray = CTRubyContentImpl.this.getCustomXmlMoveToRangeStartArray(n);
                    CTRubyContentImpl.this.removeCustomXmlMoveToRangeStart(n);
                    return customXmlMoveToRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfCustomXmlMoveToRangeStartArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.CUSTOMXMLMOVETORANGESTART$36, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlMoveToRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTRubyContentImpl.CUSTOMXMLMOVETORANGESTART$36, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.CUSTOMXMLMOVETORANGESTART$36);
        }
    }
    
    public void setCustomXmlMoveToRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.CUSTOMXMLMOVETORANGESTART$36);
    }
    
    public void setCustomXmlMoveToRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTRubyContentImpl.CUSTOMXMLMOVETORANGESTART$36, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTRubyContentImpl.CUSTOMXMLMOVETORANGESTART$36, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlMoveToRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTRubyContentImpl.CUSTOMXMLMOVETORANGESTART$36);
        }
    }
    
    public void removeCustomXmlMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.CUSTOMXMLMOVETORANGESTART$36, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlMoveToRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveToRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTRubyContentImpl.this.getCustomXmlMoveToRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlMoveToRangeEndArray = CTRubyContentImpl.this.getCustomXmlMoveToRangeEndArray(n);
                    CTRubyContentImpl.this.setCustomXmlMoveToRangeEndArray(n, ctMarkup);
                    return customXmlMoveToRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTRubyContentImpl.this.insertNewCustomXmlMoveToRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlMoveToRangeEndArray = CTRubyContentImpl.this.getCustomXmlMoveToRangeEndArray(n);
                    CTRubyContentImpl.this.removeCustomXmlMoveToRangeEnd(n);
                    return customXmlMoveToRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfCustomXmlMoveToRangeEndArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.CUSTOMXMLMOVETORANGEEND$38, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlMoveToRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTRubyContentImpl.CUSTOMXMLMOVETORANGEEND$38, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.CUSTOMXMLMOVETORANGEEND$38);
        }
    }
    
    public void setCustomXmlMoveToRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.CUSTOMXMLMOVETORANGEEND$38);
    }
    
    public void setCustomXmlMoveToRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTRubyContentImpl.CUSTOMXMLMOVETORANGEEND$38, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTRubyContentImpl.CUSTOMXMLMOVETORANGEEND$38, n);
        }
    }
    
    public CTMarkup addNewCustomXmlMoveToRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTRubyContentImpl.CUSTOMXMLMOVETORANGEEND$38);
        }
    }
    
    public void removeCustomXmlMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.CUSTOMXMLMOVETORANGEEND$38, n);
        }
    }
    
    public List<CTRunTrackChange> getInsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class InsList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTRubyContentImpl.this.getInsArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange insArray = CTRubyContentImpl.this.getInsArray(n);
                    CTRubyContentImpl.this.setInsArray(n, ctRunTrackChange);
                    return insArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTRubyContentImpl.this.insertNewIns(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange insArray = CTRubyContentImpl.this.getInsArray(n);
                    CTRubyContentImpl.this.removeIns(n);
                    return insArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfInsArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.INS$40, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getInsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTRubyContentImpl.INS$40, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfInsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.INS$40);
        }
    }
    
    public void setInsArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.INS$40);
    }
    
    public void setInsArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTRubyContentImpl.INS$40, n, (short)2);
    }
    
    public CTRunTrackChange insertNewIns(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTRubyContentImpl.INS$40, n);
        }
    }
    
    public CTRunTrackChange addNewIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTRubyContentImpl.INS$40);
        }
    }
    
    public void removeIns(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.INS$40, n);
        }
    }
    
    public List<CTRunTrackChange> getDelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DelList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTRubyContentImpl.this.getDelArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange delArray = CTRubyContentImpl.this.getDelArray(n);
                    CTRubyContentImpl.this.setDelArray(n, ctRunTrackChange);
                    return delArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTRubyContentImpl.this.insertNewDel(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange delArray = CTRubyContentImpl.this.getDelArray(n);
                    CTRubyContentImpl.this.removeDel(n);
                    return delArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfDelArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.DEL$42, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getDelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTRubyContentImpl.DEL$42, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfDelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.DEL$42);
        }
    }
    
    public void setDelArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.DEL$42);
    }
    
    public void setDelArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTRubyContentImpl.DEL$42, n, (short)2);
    }
    
    public CTRunTrackChange insertNewDel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTRubyContentImpl.DEL$42, n);
        }
    }
    
    public CTRunTrackChange addNewDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTRubyContentImpl.DEL$42);
        }
    }
    
    public void removeDel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.DEL$42, n);
        }
    }
    
    public List<CTRunTrackChange> getMoveFromList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTRubyContentImpl.this.getMoveFromArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange moveFromArray = CTRubyContentImpl.this.getMoveFromArray(n);
                    CTRubyContentImpl.this.setMoveFromArray(n, ctRunTrackChange);
                    return moveFromArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTRubyContentImpl.this.insertNewMoveFrom(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange moveFromArray = CTRubyContentImpl.this.getMoveFromArray(n);
                    CTRubyContentImpl.this.removeMoveFrom(n);
                    return moveFromArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfMoveFromArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.MOVEFROM$44, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getMoveFromArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTRubyContentImpl.MOVEFROM$44, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfMoveFromArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.MOVEFROM$44);
        }
    }
    
    public void setMoveFromArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.MOVEFROM$44);
    }
    
    public void setMoveFromArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTRubyContentImpl.MOVEFROM$44, n, (short)2);
    }
    
    public CTRunTrackChange insertNewMoveFrom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTRubyContentImpl.MOVEFROM$44, n);
        }
    }
    
    public CTRunTrackChange addNewMoveFrom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTRubyContentImpl.MOVEFROM$44);
        }
    }
    
    public void removeMoveFrom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.MOVEFROM$44, n);
        }
    }
    
    public List<CTRunTrackChange> getMoveToList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTRubyContentImpl.this.getMoveToArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange moveToArray = CTRubyContentImpl.this.getMoveToArray(n);
                    CTRubyContentImpl.this.setMoveToArray(n, ctRunTrackChange);
                    return moveToArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTRubyContentImpl.this.insertNewMoveTo(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange moveToArray = CTRubyContentImpl.this.getMoveToArray(n);
                    CTRubyContentImpl.this.removeMoveTo(n);
                    return moveToArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfMoveToArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.MOVETO$46, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getMoveToArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTRubyContentImpl.MOVETO$46, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfMoveToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.MOVETO$46);
        }
    }
    
    public void setMoveToArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.MOVETO$46);
    }
    
    public void setMoveToArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTRubyContentImpl.MOVETO$46, n, (short)2);
    }
    
    public CTRunTrackChange insertNewMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTRubyContentImpl.MOVETO$46, n);
        }
    }
    
    public CTRunTrackChange addNewMoveTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTRubyContentImpl.MOVETO$46);
        }
    }
    
    public void removeMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.MOVETO$46, n);
        }
    }
    
    public List<CTOMathPara> getOMathParaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OMathParaList extends AbstractList<CTOMathPara>
            {
                @Override
                public CTOMathPara get(final int n) {
                    return CTRubyContentImpl.this.getOMathParaArray(n);
                }
                
                @Override
                public CTOMathPara set(final int n, final CTOMathPara ctoMathPara) {
                    final CTOMathPara oMathParaArray = CTRubyContentImpl.this.getOMathParaArray(n);
                    CTRubyContentImpl.this.setOMathParaArray(n, ctoMathPara);
                    return oMathParaArray;
                }
                
                @Override
                public void add(final int n, final CTOMathPara ctoMathPara) {
                    CTRubyContentImpl.this.insertNewOMathPara(n).set((XmlObject)ctoMathPara);
                }
                
                @Override
                public CTOMathPara remove(final int n) {
                    final CTOMathPara oMathParaArray = CTRubyContentImpl.this.getOMathParaArray(n);
                    CTRubyContentImpl.this.removeOMathPara(n);
                    return oMathParaArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfOMathParaArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.OMATHPARA$48, (List)list);
            final CTOMathPara[] array = new CTOMathPara[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOMathPara getOMathParaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOMathPara ctoMathPara = (CTOMathPara)this.get_store().find_element_user(CTRubyContentImpl.OMATHPARA$48, n);
            if (ctoMathPara == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctoMathPara;
        }
    }
    
    public int sizeOfOMathParaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.OMATHPARA$48);
        }
    }
    
    public void setOMathParaArray(final CTOMathPara[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.OMATHPARA$48);
    }
    
    public void setOMathParaArray(final int n, final CTOMathPara ctoMathPara) {
        this.generatedSetterHelperImpl((XmlObject)ctoMathPara, CTRubyContentImpl.OMATHPARA$48, n, (short)2);
    }
    
    public CTOMathPara insertNewOMathPara(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMathPara)this.get_store().insert_element_user(CTRubyContentImpl.OMATHPARA$48, n);
        }
    }
    
    public CTOMathPara addNewOMathPara() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMathPara)this.get_store().add_element_user(CTRubyContentImpl.OMATHPARA$48);
        }
    }
    
    public void removeOMathPara(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.OMATHPARA$48, n);
        }
    }
    
    public List<CTOMath> getOMathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OMathList extends AbstractList<CTOMath>
            {
                @Override
                public CTOMath get(final int n) {
                    return CTRubyContentImpl.this.getOMathArray(n);
                }
                
                @Override
                public CTOMath set(final int n, final CTOMath ctoMath) {
                    final CTOMath oMathArray = CTRubyContentImpl.this.getOMathArray(n);
                    CTRubyContentImpl.this.setOMathArray(n, ctoMath);
                    return oMathArray;
                }
                
                @Override
                public void add(final int n, final CTOMath ctoMath) {
                    CTRubyContentImpl.this.insertNewOMath(n).set((XmlObject)ctoMath);
                }
                
                @Override
                public CTOMath remove(final int n) {
                    final CTOMath oMathArray = CTRubyContentImpl.this.getOMathArray(n);
                    CTRubyContentImpl.this.removeOMath(n);
                    return oMathArray;
                }
                
                @Override
                public int size() {
                    return CTRubyContentImpl.this.sizeOfOMathArray();
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
            this.get_store().find_all_element_users(CTRubyContentImpl.OMATH$50, (List)list);
            final CTOMath[] array = new CTOMath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOMath getOMathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOMath ctoMath = (CTOMath)this.get_store().find_element_user(CTRubyContentImpl.OMATH$50, n);
            if (ctoMath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctoMath;
        }
    }
    
    public int sizeOfOMathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRubyContentImpl.OMATH$50);
        }
    }
    
    public void setOMathArray(final CTOMath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRubyContentImpl.OMATH$50);
    }
    
    public void setOMathArray(final int n, final CTOMath ctoMath) {
        this.generatedSetterHelperImpl((XmlObject)ctoMath, CTRubyContentImpl.OMATH$50, n, (short)2);
    }
    
    public CTOMath insertNewOMath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMath)this.get_store().insert_element_user(CTRubyContentImpl.OMATH$50, n);
        }
    }
    
    public CTOMath addNewOMath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMath)this.get_store().add_element_user(CTRubyContentImpl.OMATH$50);
        }
    }
    
    public void removeOMath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRubyContentImpl.OMATH$50, n);
        }
    }
    
    static {
        R$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "r");
        PROOFERR$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "proofErr");
        PERMSTART$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "permStart");
        PERMEND$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "permEnd");
        BOOKMARKSTART$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookmarkStart");
        BOOKMARKEND$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookmarkEnd");
        MOVEFROMRANGESTART$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFromRangeStart");
        MOVEFROMRANGEEND$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFromRangeEnd");
        MOVETORANGESTART$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveToRangeStart");
        MOVETORANGEEND$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveToRangeEnd");
        COMMENTRANGESTART$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "commentRangeStart");
        COMMENTRANGEEND$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "commentRangeEnd");
        CUSTOMXMLINSRANGESTART$24 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlInsRangeStart");
        CUSTOMXMLINSRANGEEND$26 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlInsRangeEnd");
        CUSTOMXMLDELRANGESTART$28 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlDelRangeStart");
        CUSTOMXMLDELRANGEEND$30 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlDelRangeEnd");
        CUSTOMXMLMOVEFROMRANGESTART$32 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveFromRangeStart");
        CUSTOMXMLMOVEFROMRANGEEND$34 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveFromRangeEnd");
        CUSTOMXMLMOVETORANGESTART$36 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveToRangeStart");
        CUSTOMXMLMOVETORANGEEND$38 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveToRangeEnd");
        INS$40 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ins");
        DEL$42 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "del");
        MOVEFROM$44 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFrom");
        MOVETO$46 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveTo");
        OMATHPARA$48 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "oMathPara");
        OMATH$50 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "oMath");
    }
}
