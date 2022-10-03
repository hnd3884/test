package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRunTrackChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPerm;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPermStart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTProofErr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCustomXmlRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkup;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMoveBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTblImpl extends XmlComplexContentImpl implements CTTbl
{
    private static final long serialVersionUID = 1L;
    private static final QName BOOKMARKSTART$0;
    private static final QName BOOKMARKEND$2;
    private static final QName MOVEFROMRANGESTART$4;
    private static final QName MOVEFROMRANGEEND$6;
    private static final QName MOVETORANGESTART$8;
    private static final QName MOVETORANGEEND$10;
    private static final QName COMMENTRANGESTART$12;
    private static final QName COMMENTRANGEEND$14;
    private static final QName CUSTOMXMLINSRANGESTART$16;
    private static final QName CUSTOMXMLINSRANGEEND$18;
    private static final QName CUSTOMXMLDELRANGESTART$20;
    private static final QName CUSTOMXMLDELRANGEEND$22;
    private static final QName CUSTOMXMLMOVEFROMRANGESTART$24;
    private static final QName CUSTOMXMLMOVEFROMRANGEEND$26;
    private static final QName CUSTOMXMLMOVETORANGESTART$28;
    private static final QName CUSTOMXMLMOVETORANGEEND$30;
    private static final QName TBLPR$32;
    private static final QName TBLGRID$34;
    private static final QName TR$36;
    private static final QName CUSTOMXML$38;
    private static final QName SDT$40;
    private static final QName PROOFERR$42;
    private static final QName PERMSTART$44;
    private static final QName PERMEND$46;
    private static final QName INS$48;
    private static final QName DEL$50;
    private static final QName MOVEFROM$52;
    private static final QName MOVETO$54;
    private static final QName OMATHPARA$56;
    private static final QName OMATH$58;
    
    public CTTblImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTBookmark> getBookmarkStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BookmarkStartList extends AbstractList<CTBookmark>
            {
                @Override
                public CTBookmark get(final int n) {
                    return CTTblImpl.this.getBookmarkStartArray(n);
                }
                
                @Override
                public CTBookmark set(final int n, final CTBookmark ctBookmark) {
                    final CTBookmark bookmarkStartArray = CTTblImpl.this.getBookmarkStartArray(n);
                    CTTblImpl.this.setBookmarkStartArray(n, ctBookmark);
                    return bookmarkStartArray;
                }
                
                @Override
                public void add(final int n, final CTBookmark ctBookmark) {
                    CTTblImpl.this.insertNewBookmarkStart(n).set((XmlObject)ctBookmark);
                }
                
                @Override
                public CTBookmark remove(final int n) {
                    final CTBookmark bookmarkStartArray = CTTblImpl.this.getBookmarkStartArray(n);
                    CTTblImpl.this.removeBookmarkStart(n);
                    return bookmarkStartArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfBookmarkStartArray();
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
            this.get_store().find_all_element_users(CTTblImpl.BOOKMARKSTART$0, (List)list);
            final CTBookmark[] array = new CTBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBookmark getBookmarkStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBookmark ctBookmark = (CTBookmark)this.get_store().find_element_user(CTTblImpl.BOOKMARKSTART$0, n);
            if (ctBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBookmark;
        }
    }
    
    public int sizeOfBookmarkStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.BOOKMARKSTART$0);
        }
    }
    
    public void setBookmarkStartArray(final CTBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.BOOKMARKSTART$0);
    }
    
    public void setBookmarkStartArray(final int n, final CTBookmark ctBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctBookmark, CTTblImpl.BOOKMARKSTART$0, n, (short)2);
    }
    
    public CTBookmark insertNewBookmarkStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookmark)this.get_store().insert_element_user(CTTblImpl.BOOKMARKSTART$0, n);
        }
    }
    
    public CTBookmark addNewBookmarkStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookmark)this.get_store().add_element_user(CTTblImpl.BOOKMARKSTART$0);
        }
    }
    
    public void removeBookmarkStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.BOOKMARKSTART$0, n);
        }
    }
    
    public List<CTMarkupRange> getBookmarkEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BookmarkEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTTblImpl.this.getBookmarkEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange bookmarkEndArray = CTTblImpl.this.getBookmarkEndArray(n);
                    CTTblImpl.this.setBookmarkEndArray(n, ctMarkupRange);
                    return bookmarkEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTTblImpl.this.insertNewBookmarkEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange bookmarkEndArray = CTTblImpl.this.getBookmarkEndArray(n);
                    CTTblImpl.this.removeBookmarkEnd(n);
                    return bookmarkEndArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfBookmarkEndArray();
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
            this.get_store().find_all_element_users(CTTblImpl.BOOKMARKEND$2, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getBookmarkEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTTblImpl.BOOKMARKEND$2, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfBookmarkEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.BOOKMARKEND$2);
        }
    }
    
    public void setBookmarkEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.BOOKMARKEND$2);
    }
    
    public void setBookmarkEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTTblImpl.BOOKMARKEND$2, n, (short)2);
    }
    
    public CTMarkupRange insertNewBookmarkEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTTblImpl.BOOKMARKEND$2, n);
        }
    }
    
    public CTMarkupRange addNewBookmarkEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTTblImpl.BOOKMARKEND$2);
        }
    }
    
    public void removeBookmarkEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.BOOKMARKEND$2, n);
        }
    }
    
    public List<CTMoveBookmark> getMoveFromRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromRangeStartList extends AbstractList<CTMoveBookmark>
            {
                @Override
                public CTMoveBookmark get(final int n) {
                    return CTTblImpl.this.getMoveFromRangeStartArray(n);
                }
                
                @Override
                public CTMoveBookmark set(final int n, final CTMoveBookmark ctMoveBookmark) {
                    final CTMoveBookmark moveFromRangeStartArray = CTTblImpl.this.getMoveFromRangeStartArray(n);
                    CTTblImpl.this.setMoveFromRangeStartArray(n, ctMoveBookmark);
                    return moveFromRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMoveBookmark ctMoveBookmark) {
                    CTTblImpl.this.insertNewMoveFromRangeStart(n).set((XmlObject)ctMoveBookmark);
                }
                
                @Override
                public CTMoveBookmark remove(final int n) {
                    final CTMoveBookmark moveFromRangeStartArray = CTTblImpl.this.getMoveFromRangeStartArray(n);
                    CTTblImpl.this.removeMoveFromRangeStart(n);
                    return moveFromRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfMoveFromRangeStartArray();
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
            this.get_store().find_all_element_users(CTTblImpl.MOVEFROMRANGESTART$4, (List)list);
            final CTMoveBookmark[] array = new CTMoveBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMoveBookmark getMoveFromRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMoveBookmark ctMoveBookmark = (CTMoveBookmark)this.get_store().find_element_user(CTTblImpl.MOVEFROMRANGESTART$4, n);
            if (ctMoveBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMoveBookmark;
        }
    }
    
    public int sizeOfMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.MOVEFROMRANGESTART$4);
        }
    }
    
    public void setMoveFromRangeStartArray(final CTMoveBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.MOVEFROMRANGESTART$4);
    }
    
    public void setMoveFromRangeStartArray(final int n, final CTMoveBookmark ctMoveBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctMoveBookmark, CTTblImpl.MOVEFROMRANGESTART$4, n, (short)2);
    }
    
    public CTMoveBookmark insertNewMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().insert_element_user(CTTblImpl.MOVEFROMRANGESTART$4, n);
        }
    }
    
    public CTMoveBookmark addNewMoveFromRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().add_element_user(CTTblImpl.MOVEFROMRANGESTART$4);
        }
    }
    
    public void removeMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.MOVEFROMRANGESTART$4, n);
        }
    }
    
    public List<CTMarkupRange> getMoveFromRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTTblImpl.this.getMoveFromRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange moveFromRangeEndArray = CTTblImpl.this.getMoveFromRangeEndArray(n);
                    CTTblImpl.this.setMoveFromRangeEndArray(n, ctMarkupRange);
                    return moveFromRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTTblImpl.this.insertNewMoveFromRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange moveFromRangeEndArray = CTTblImpl.this.getMoveFromRangeEndArray(n);
                    CTTblImpl.this.removeMoveFromRangeEnd(n);
                    return moveFromRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfMoveFromRangeEndArray();
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
            this.get_store().find_all_element_users(CTTblImpl.MOVEFROMRANGEEND$6, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getMoveFromRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTTblImpl.MOVEFROMRANGEEND$6, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.MOVEFROMRANGEEND$6);
        }
    }
    
    public void setMoveFromRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.MOVEFROMRANGEEND$6);
    }
    
    public void setMoveFromRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTTblImpl.MOVEFROMRANGEEND$6, n, (short)2);
    }
    
    public CTMarkupRange insertNewMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTTblImpl.MOVEFROMRANGEEND$6, n);
        }
    }
    
    public CTMarkupRange addNewMoveFromRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTTblImpl.MOVEFROMRANGEEND$6);
        }
    }
    
    public void removeMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.MOVEFROMRANGEEND$6, n);
        }
    }
    
    public List<CTMoveBookmark> getMoveToRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToRangeStartList extends AbstractList<CTMoveBookmark>
            {
                @Override
                public CTMoveBookmark get(final int n) {
                    return CTTblImpl.this.getMoveToRangeStartArray(n);
                }
                
                @Override
                public CTMoveBookmark set(final int n, final CTMoveBookmark ctMoveBookmark) {
                    final CTMoveBookmark moveToRangeStartArray = CTTblImpl.this.getMoveToRangeStartArray(n);
                    CTTblImpl.this.setMoveToRangeStartArray(n, ctMoveBookmark);
                    return moveToRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMoveBookmark ctMoveBookmark) {
                    CTTblImpl.this.insertNewMoveToRangeStart(n).set((XmlObject)ctMoveBookmark);
                }
                
                @Override
                public CTMoveBookmark remove(final int n) {
                    final CTMoveBookmark moveToRangeStartArray = CTTblImpl.this.getMoveToRangeStartArray(n);
                    CTTblImpl.this.removeMoveToRangeStart(n);
                    return moveToRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfMoveToRangeStartArray();
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
            this.get_store().find_all_element_users(CTTblImpl.MOVETORANGESTART$8, (List)list);
            final CTMoveBookmark[] array = new CTMoveBookmark[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMoveBookmark getMoveToRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMoveBookmark ctMoveBookmark = (CTMoveBookmark)this.get_store().find_element_user(CTTblImpl.MOVETORANGESTART$8, n);
            if (ctMoveBookmark == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMoveBookmark;
        }
    }
    
    public int sizeOfMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.MOVETORANGESTART$8);
        }
    }
    
    public void setMoveToRangeStartArray(final CTMoveBookmark[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.MOVETORANGESTART$8);
    }
    
    public void setMoveToRangeStartArray(final int n, final CTMoveBookmark ctMoveBookmark) {
        this.generatedSetterHelperImpl((XmlObject)ctMoveBookmark, CTTblImpl.MOVETORANGESTART$8, n, (short)2);
    }
    
    public CTMoveBookmark insertNewMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().insert_element_user(CTTblImpl.MOVETORANGESTART$8, n);
        }
    }
    
    public CTMoveBookmark addNewMoveToRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMoveBookmark)this.get_store().add_element_user(CTTblImpl.MOVETORANGESTART$8);
        }
    }
    
    public void removeMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.MOVETORANGESTART$8, n);
        }
    }
    
    public List<CTMarkupRange> getMoveToRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTTblImpl.this.getMoveToRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange moveToRangeEndArray = CTTblImpl.this.getMoveToRangeEndArray(n);
                    CTTblImpl.this.setMoveToRangeEndArray(n, ctMarkupRange);
                    return moveToRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTTblImpl.this.insertNewMoveToRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange moveToRangeEndArray = CTTblImpl.this.getMoveToRangeEndArray(n);
                    CTTblImpl.this.removeMoveToRangeEnd(n);
                    return moveToRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfMoveToRangeEndArray();
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
            this.get_store().find_all_element_users(CTTblImpl.MOVETORANGEEND$10, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getMoveToRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTTblImpl.MOVETORANGEEND$10, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.MOVETORANGEEND$10);
        }
    }
    
    public void setMoveToRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.MOVETORANGEEND$10);
    }
    
    public void setMoveToRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTTblImpl.MOVETORANGEEND$10, n, (short)2);
    }
    
    public CTMarkupRange insertNewMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTTblImpl.MOVETORANGEEND$10, n);
        }
    }
    
    public CTMarkupRange addNewMoveToRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTTblImpl.MOVETORANGEEND$10);
        }
    }
    
    public void removeMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.MOVETORANGEEND$10, n);
        }
    }
    
    public List<CTMarkupRange> getCommentRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentRangeStartList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTTblImpl.this.getCommentRangeStartArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange commentRangeStartArray = CTTblImpl.this.getCommentRangeStartArray(n);
                    CTTblImpl.this.setCommentRangeStartArray(n, ctMarkupRange);
                    return commentRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTTblImpl.this.insertNewCommentRangeStart(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange commentRangeStartArray = CTTblImpl.this.getCommentRangeStartArray(n);
                    CTTblImpl.this.removeCommentRangeStart(n);
                    return commentRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfCommentRangeStartArray();
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
            this.get_store().find_all_element_users(CTTblImpl.COMMENTRANGESTART$12, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getCommentRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTTblImpl.COMMENTRANGESTART$12, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfCommentRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.COMMENTRANGESTART$12);
        }
    }
    
    public void setCommentRangeStartArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.COMMENTRANGESTART$12);
    }
    
    public void setCommentRangeStartArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTTblImpl.COMMENTRANGESTART$12, n, (short)2);
    }
    
    public CTMarkupRange insertNewCommentRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTTblImpl.COMMENTRANGESTART$12, n);
        }
    }
    
    public CTMarkupRange addNewCommentRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTTblImpl.COMMENTRANGESTART$12);
        }
    }
    
    public void removeCommentRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.COMMENTRANGESTART$12, n);
        }
    }
    
    public List<CTMarkupRange> getCommentRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentRangeEndList extends AbstractList<CTMarkupRange>
            {
                @Override
                public CTMarkupRange get(final int n) {
                    return CTTblImpl.this.getCommentRangeEndArray(n);
                }
                
                @Override
                public CTMarkupRange set(final int n, final CTMarkupRange ctMarkupRange) {
                    final CTMarkupRange commentRangeEndArray = CTTblImpl.this.getCommentRangeEndArray(n);
                    CTTblImpl.this.setCommentRangeEndArray(n, ctMarkupRange);
                    return commentRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkupRange ctMarkupRange) {
                    CTTblImpl.this.insertNewCommentRangeEnd(n).set((XmlObject)ctMarkupRange);
                }
                
                @Override
                public CTMarkupRange remove(final int n) {
                    final CTMarkupRange commentRangeEndArray = CTTblImpl.this.getCommentRangeEndArray(n);
                    CTTblImpl.this.removeCommentRangeEnd(n);
                    return commentRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfCommentRangeEndArray();
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
            this.get_store().find_all_element_users(CTTblImpl.COMMENTRANGEEND$14, (List)list);
            final CTMarkupRange[] array = new CTMarkupRange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkupRange getCommentRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkupRange ctMarkupRange = (CTMarkupRange)this.get_store().find_element_user(CTTblImpl.COMMENTRANGEEND$14, n);
            if (ctMarkupRange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkupRange;
        }
    }
    
    public int sizeOfCommentRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.COMMENTRANGEEND$14);
        }
    }
    
    public void setCommentRangeEndArray(final CTMarkupRange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.COMMENTRANGEEND$14);
    }
    
    public void setCommentRangeEndArray(final int n, final CTMarkupRange ctMarkupRange) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkupRange, CTTblImpl.COMMENTRANGEEND$14, n, (short)2);
    }
    
    public CTMarkupRange insertNewCommentRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().insert_element_user(CTTblImpl.COMMENTRANGEEND$14, n);
        }
    }
    
    public CTMarkupRange addNewCommentRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkupRange)this.get_store().add_element_user(CTTblImpl.COMMENTRANGEEND$14);
        }
    }
    
    public void removeCommentRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.COMMENTRANGEEND$14, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlInsRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlInsRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTTblImpl.this.getCustomXmlInsRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlInsRangeStartArray = CTTblImpl.this.getCustomXmlInsRangeStartArray(n);
                    CTTblImpl.this.setCustomXmlInsRangeStartArray(n, ctTrackChange);
                    return customXmlInsRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTTblImpl.this.insertNewCustomXmlInsRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlInsRangeStartArray = CTTblImpl.this.getCustomXmlInsRangeStartArray(n);
                    CTTblImpl.this.removeCustomXmlInsRangeStart(n);
                    return customXmlInsRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfCustomXmlInsRangeStartArray();
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
            this.get_store().find_all_element_users(CTTblImpl.CUSTOMXMLINSRANGESTART$16, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlInsRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTTblImpl.CUSTOMXMLINSRANGESTART$16, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlInsRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.CUSTOMXMLINSRANGESTART$16);
        }
    }
    
    public void setCustomXmlInsRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.CUSTOMXMLINSRANGESTART$16);
    }
    
    public void setCustomXmlInsRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTTblImpl.CUSTOMXMLINSRANGESTART$16, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlInsRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTTblImpl.CUSTOMXMLINSRANGESTART$16, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlInsRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTTblImpl.CUSTOMXMLINSRANGESTART$16);
        }
    }
    
    public void removeCustomXmlInsRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.CUSTOMXMLINSRANGESTART$16, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlInsRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlInsRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTTblImpl.this.getCustomXmlInsRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlInsRangeEndArray = CTTblImpl.this.getCustomXmlInsRangeEndArray(n);
                    CTTblImpl.this.setCustomXmlInsRangeEndArray(n, ctMarkup);
                    return customXmlInsRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTTblImpl.this.insertNewCustomXmlInsRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlInsRangeEndArray = CTTblImpl.this.getCustomXmlInsRangeEndArray(n);
                    CTTblImpl.this.removeCustomXmlInsRangeEnd(n);
                    return customXmlInsRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfCustomXmlInsRangeEndArray();
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
            this.get_store().find_all_element_users(CTTblImpl.CUSTOMXMLINSRANGEEND$18, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlInsRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTTblImpl.CUSTOMXMLINSRANGEEND$18, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlInsRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.CUSTOMXMLINSRANGEEND$18);
        }
    }
    
    public void setCustomXmlInsRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.CUSTOMXMLINSRANGEEND$18);
    }
    
    public void setCustomXmlInsRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTTblImpl.CUSTOMXMLINSRANGEEND$18, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlInsRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTTblImpl.CUSTOMXMLINSRANGEEND$18, n);
        }
    }
    
    public CTMarkup addNewCustomXmlInsRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTTblImpl.CUSTOMXMLINSRANGEEND$18);
        }
    }
    
    public void removeCustomXmlInsRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.CUSTOMXMLINSRANGEEND$18, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlDelRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlDelRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTTblImpl.this.getCustomXmlDelRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlDelRangeStartArray = CTTblImpl.this.getCustomXmlDelRangeStartArray(n);
                    CTTblImpl.this.setCustomXmlDelRangeStartArray(n, ctTrackChange);
                    return customXmlDelRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTTblImpl.this.insertNewCustomXmlDelRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlDelRangeStartArray = CTTblImpl.this.getCustomXmlDelRangeStartArray(n);
                    CTTblImpl.this.removeCustomXmlDelRangeStart(n);
                    return customXmlDelRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfCustomXmlDelRangeStartArray();
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
            this.get_store().find_all_element_users(CTTblImpl.CUSTOMXMLDELRANGESTART$20, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlDelRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTTblImpl.CUSTOMXMLDELRANGESTART$20, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlDelRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.CUSTOMXMLDELRANGESTART$20);
        }
    }
    
    public void setCustomXmlDelRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.CUSTOMXMLDELRANGESTART$20);
    }
    
    public void setCustomXmlDelRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTTblImpl.CUSTOMXMLDELRANGESTART$20, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlDelRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTTblImpl.CUSTOMXMLDELRANGESTART$20, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlDelRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTTblImpl.CUSTOMXMLDELRANGESTART$20);
        }
    }
    
    public void removeCustomXmlDelRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.CUSTOMXMLDELRANGESTART$20, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlDelRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlDelRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTTblImpl.this.getCustomXmlDelRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlDelRangeEndArray = CTTblImpl.this.getCustomXmlDelRangeEndArray(n);
                    CTTblImpl.this.setCustomXmlDelRangeEndArray(n, ctMarkup);
                    return customXmlDelRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTTblImpl.this.insertNewCustomXmlDelRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlDelRangeEndArray = CTTblImpl.this.getCustomXmlDelRangeEndArray(n);
                    CTTblImpl.this.removeCustomXmlDelRangeEnd(n);
                    return customXmlDelRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfCustomXmlDelRangeEndArray();
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
            this.get_store().find_all_element_users(CTTblImpl.CUSTOMXMLDELRANGEEND$22, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlDelRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTTblImpl.CUSTOMXMLDELRANGEEND$22, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlDelRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.CUSTOMXMLDELRANGEEND$22);
        }
    }
    
    public void setCustomXmlDelRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.CUSTOMXMLDELRANGEEND$22);
    }
    
    public void setCustomXmlDelRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTTblImpl.CUSTOMXMLDELRANGEEND$22, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlDelRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTTblImpl.CUSTOMXMLDELRANGEEND$22, n);
        }
    }
    
    public CTMarkup addNewCustomXmlDelRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTTblImpl.CUSTOMXMLDELRANGEEND$22);
        }
    }
    
    public void removeCustomXmlDelRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.CUSTOMXMLDELRANGEEND$22, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlMoveFromRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveFromRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTTblImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlMoveFromRangeStartArray = CTTblImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                    CTTblImpl.this.setCustomXmlMoveFromRangeStartArray(n, ctTrackChange);
                    return customXmlMoveFromRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTTblImpl.this.insertNewCustomXmlMoveFromRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlMoveFromRangeStartArray = CTTblImpl.this.getCustomXmlMoveFromRangeStartArray(n);
                    CTTblImpl.this.removeCustomXmlMoveFromRangeStart(n);
                    return customXmlMoveFromRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfCustomXmlMoveFromRangeStartArray();
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
            this.get_store().find_all_element_users(CTTblImpl.CUSTOMXMLMOVEFROMRANGESTART$24, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlMoveFromRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTTblImpl.CUSTOMXMLMOVEFROMRANGESTART$24, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlMoveFromRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.CUSTOMXMLMOVEFROMRANGESTART$24);
        }
    }
    
    public void setCustomXmlMoveFromRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.CUSTOMXMLMOVEFROMRANGESTART$24);
    }
    
    public void setCustomXmlMoveFromRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTTblImpl.CUSTOMXMLMOVEFROMRANGESTART$24, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTTblImpl.CUSTOMXMLMOVEFROMRANGESTART$24, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlMoveFromRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTTblImpl.CUSTOMXMLMOVEFROMRANGESTART$24);
        }
    }
    
    public void removeCustomXmlMoveFromRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.CUSTOMXMLMOVEFROMRANGESTART$24, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlMoveFromRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveFromRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTTblImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlMoveFromRangeEndArray = CTTblImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                    CTTblImpl.this.setCustomXmlMoveFromRangeEndArray(n, ctMarkup);
                    return customXmlMoveFromRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTTblImpl.this.insertNewCustomXmlMoveFromRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlMoveFromRangeEndArray = CTTblImpl.this.getCustomXmlMoveFromRangeEndArray(n);
                    CTTblImpl.this.removeCustomXmlMoveFromRangeEnd(n);
                    return customXmlMoveFromRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfCustomXmlMoveFromRangeEndArray();
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
            this.get_store().find_all_element_users(CTTblImpl.CUSTOMXMLMOVEFROMRANGEEND$26, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlMoveFromRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTTblImpl.CUSTOMXMLMOVEFROMRANGEEND$26, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlMoveFromRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.CUSTOMXMLMOVEFROMRANGEEND$26);
        }
    }
    
    public void setCustomXmlMoveFromRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.CUSTOMXMLMOVEFROMRANGEEND$26);
    }
    
    public void setCustomXmlMoveFromRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTTblImpl.CUSTOMXMLMOVEFROMRANGEEND$26, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTTblImpl.CUSTOMXMLMOVEFROMRANGEEND$26, n);
        }
    }
    
    public CTMarkup addNewCustomXmlMoveFromRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTTblImpl.CUSTOMXMLMOVEFROMRANGEEND$26);
        }
    }
    
    public void removeCustomXmlMoveFromRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.CUSTOMXMLMOVEFROMRANGEEND$26, n);
        }
    }
    
    public List<CTTrackChange> getCustomXmlMoveToRangeStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveToRangeStartList extends AbstractList<CTTrackChange>
            {
                @Override
                public CTTrackChange get(final int n) {
                    return CTTblImpl.this.getCustomXmlMoveToRangeStartArray(n);
                }
                
                @Override
                public CTTrackChange set(final int n, final CTTrackChange ctTrackChange) {
                    final CTTrackChange customXmlMoveToRangeStartArray = CTTblImpl.this.getCustomXmlMoveToRangeStartArray(n);
                    CTTblImpl.this.setCustomXmlMoveToRangeStartArray(n, ctTrackChange);
                    return customXmlMoveToRangeStartArray;
                }
                
                @Override
                public void add(final int n, final CTTrackChange ctTrackChange) {
                    CTTblImpl.this.insertNewCustomXmlMoveToRangeStart(n).set((XmlObject)ctTrackChange);
                }
                
                @Override
                public CTTrackChange remove(final int n) {
                    final CTTrackChange customXmlMoveToRangeStartArray = CTTblImpl.this.getCustomXmlMoveToRangeStartArray(n);
                    CTTblImpl.this.removeCustomXmlMoveToRangeStart(n);
                    return customXmlMoveToRangeStartArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfCustomXmlMoveToRangeStartArray();
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
            this.get_store().find_all_element_users(CTTblImpl.CUSTOMXMLMOVETORANGESTART$28, (List)list);
            final CTTrackChange[] array = new CTTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrackChange getCustomXmlMoveToRangeStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTTblImpl.CUSTOMXMLMOVETORANGESTART$28, n);
            if (ctTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrackChange;
        }
    }
    
    public int sizeOfCustomXmlMoveToRangeStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.CUSTOMXMLMOVETORANGESTART$28);
        }
    }
    
    public void setCustomXmlMoveToRangeStartArray(final CTTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.CUSTOMXMLMOVETORANGESTART$28);
    }
    
    public void setCustomXmlMoveToRangeStartArray(final int n, final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTTblImpl.CUSTOMXMLMOVETORANGESTART$28, n, (short)2);
    }
    
    public CTTrackChange insertNewCustomXmlMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().insert_element_user(CTTblImpl.CUSTOMXMLMOVETORANGESTART$28, n);
        }
    }
    
    public CTTrackChange addNewCustomXmlMoveToRangeStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTTblImpl.CUSTOMXMLMOVETORANGESTART$28);
        }
    }
    
    public void removeCustomXmlMoveToRangeStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.CUSTOMXMLMOVETORANGESTART$28, n);
        }
    }
    
    public List<CTMarkup> getCustomXmlMoveToRangeEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlMoveToRangeEndList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTTblImpl.this.getCustomXmlMoveToRangeEndArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup customXmlMoveToRangeEndArray = CTTblImpl.this.getCustomXmlMoveToRangeEndArray(n);
                    CTTblImpl.this.setCustomXmlMoveToRangeEndArray(n, ctMarkup);
                    return customXmlMoveToRangeEndArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTTblImpl.this.insertNewCustomXmlMoveToRangeEnd(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup customXmlMoveToRangeEndArray = CTTblImpl.this.getCustomXmlMoveToRangeEndArray(n);
                    CTTblImpl.this.removeCustomXmlMoveToRangeEnd(n);
                    return customXmlMoveToRangeEndArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfCustomXmlMoveToRangeEndArray();
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
            this.get_store().find_all_element_users(CTTblImpl.CUSTOMXMLMOVETORANGEEND$30, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCustomXmlMoveToRangeEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTTblImpl.CUSTOMXMLMOVETORANGEEND$30, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCustomXmlMoveToRangeEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.CUSTOMXMLMOVETORANGEEND$30);
        }
    }
    
    public void setCustomXmlMoveToRangeEndArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.CUSTOMXMLMOVETORANGEEND$30);
    }
    
    public void setCustomXmlMoveToRangeEndArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTTblImpl.CUSTOMXMLMOVETORANGEEND$30, n, (short)2);
    }
    
    public CTMarkup insertNewCustomXmlMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTTblImpl.CUSTOMXMLMOVETORANGEEND$30, n);
        }
    }
    
    public CTMarkup addNewCustomXmlMoveToRangeEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTTblImpl.CUSTOMXMLMOVETORANGEEND$30);
        }
    }
    
    public void removeCustomXmlMoveToRangeEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.CUSTOMXMLMOVETORANGEEND$30, n);
        }
    }
    
    public CTTblPr getTblPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblPr ctTblPr = (CTTblPr)this.get_store().find_element_user(CTTblImpl.TBLPR$32, 0);
            if (ctTblPr == null) {
                return null;
            }
            return ctTblPr;
        }
    }
    
    public void setTblPr(final CTTblPr ctTblPr) {
        this.generatedSetterHelperImpl((XmlObject)ctTblPr, CTTblImpl.TBLPR$32, 0, (short)1);
    }
    
    public CTTblPr addNewTblPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblPr)this.get_store().add_element_user(CTTblImpl.TBLPR$32);
        }
    }
    
    public CTTblGrid getTblGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblGrid ctTblGrid = (CTTblGrid)this.get_store().find_element_user(CTTblImpl.TBLGRID$34, 0);
            if (ctTblGrid == null) {
                return null;
            }
            return ctTblGrid;
        }
    }
    
    public void setTblGrid(final CTTblGrid ctTblGrid) {
        this.generatedSetterHelperImpl((XmlObject)ctTblGrid, CTTblImpl.TBLGRID$34, 0, (short)1);
    }
    
    public CTTblGrid addNewTblGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblGrid)this.get_store().add_element_user(CTTblImpl.TBLGRID$34);
        }
    }
    
    public List<CTRow> getTrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TrList extends AbstractList<CTRow>
            {
                @Override
                public CTRow get(final int n) {
                    return CTTblImpl.this.getTrArray(n);
                }
                
                @Override
                public CTRow set(final int n, final CTRow ctRow) {
                    final CTRow trArray = CTTblImpl.this.getTrArray(n);
                    CTTblImpl.this.setTrArray(n, ctRow);
                    return trArray;
                }
                
                @Override
                public void add(final int n, final CTRow ctRow) {
                    CTTblImpl.this.insertNewTr(n).set((XmlObject)ctRow);
                }
                
                @Override
                public CTRow remove(final int n) {
                    final CTRow trArray = CTTblImpl.this.getTrArray(n);
                    CTTblImpl.this.removeTr(n);
                    return trArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfTrArray();
                }
            }
            return new TrList();
        }
    }
    
    @Deprecated
    public CTRow[] getTrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTblImpl.TR$36, (List)list);
            final CTRow[] array = new CTRow[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRow getTrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRow ctRow = (CTRow)this.get_store().find_element_user(CTTblImpl.TR$36, n);
            if (ctRow == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRow;
        }
    }
    
    public int sizeOfTrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.TR$36);
        }
    }
    
    public void setTrArray(final CTRow[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.TR$36);
    }
    
    public void setTrArray(final int n, final CTRow ctRow) {
        this.generatedSetterHelperImpl((XmlObject)ctRow, CTTblImpl.TR$36, n, (short)2);
    }
    
    public CTRow insertNewTr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRow)this.get_store().insert_element_user(CTTblImpl.TR$36, n);
        }
    }
    
    public CTRow addNewTr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRow)this.get_store().add_element_user(CTTblImpl.TR$36);
        }
    }
    
    public void removeTr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.TR$36, n);
        }
    }
    
    public List<CTCustomXmlRow> getCustomXmlList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CustomXmlList extends AbstractList<CTCustomXmlRow>
            {
                @Override
                public CTCustomXmlRow get(final int n) {
                    return CTTblImpl.this.getCustomXmlArray(n);
                }
                
                @Override
                public CTCustomXmlRow set(final int n, final CTCustomXmlRow ctCustomXmlRow) {
                    final CTCustomXmlRow customXmlArray = CTTblImpl.this.getCustomXmlArray(n);
                    CTTblImpl.this.setCustomXmlArray(n, ctCustomXmlRow);
                    return customXmlArray;
                }
                
                @Override
                public void add(final int n, final CTCustomXmlRow ctCustomXmlRow) {
                    CTTblImpl.this.insertNewCustomXml(n).set((XmlObject)ctCustomXmlRow);
                }
                
                @Override
                public CTCustomXmlRow remove(final int n) {
                    final CTCustomXmlRow customXmlArray = CTTblImpl.this.getCustomXmlArray(n);
                    CTTblImpl.this.removeCustomXml(n);
                    return customXmlArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfCustomXmlArray();
                }
            }
            return new CustomXmlList();
        }
    }
    
    @Deprecated
    public CTCustomXmlRow[] getCustomXmlArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTblImpl.CUSTOMXML$38, (List)list);
            final CTCustomXmlRow[] array = new CTCustomXmlRow[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCustomXmlRow getCustomXmlArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomXmlRow ctCustomXmlRow = (CTCustomXmlRow)this.get_store().find_element_user(CTTblImpl.CUSTOMXML$38, n);
            if (ctCustomXmlRow == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCustomXmlRow;
        }
    }
    
    public int sizeOfCustomXmlArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.CUSTOMXML$38);
        }
    }
    
    public void setCustomXmlArray(final CTCustomXmlRow[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.CUSTOMXML$38);
    }
    
    public void setCustomXmlArray(final int n, final CTCustomXmlRow ctCustomXmlRow) {
        this.generatedSetterHelperImpl((XmlObject)ctCustomXmlRow, CTTblImpl.CUSTOMXML$38, n, (short)2);
    }
    
    public CTCustomXmlRow insertNewCustomXml(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomXmlRow)this.get_store().insert_element_user(CTTblImpl.CUSTOMXML$38, n);
        }
    }
    
    public CTCustomXmlRow addNewCustomXml() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomXmlRow)this.get_store().add_element_user(CTTblImpl.CUSTOMXML$38);
        }
    }
    
    public void removeCustomXml(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.CUSTOMXML$38, n);
        }
    }
    
    public List<CTSdtRow> getSdtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SdtList extends AbstractList<CTSdtRow>
            {
                @Override
                public CTSdtRow get(final int n) {
                    return CTTblImpl.this.getSdtArray(n);
                }
                
                @Override
                public CTSdtRow set(final int n, final CTSdtRow ctSdtRow) {
                    final CTSdtRow sdtArray = CTTblImpl.this.getSdtArray(n);
                    CTTblImpl.this.setSdtArray(n, ctSdtRow);
                    return sdtArray;
                }
                
                @Override
                public void add(final int n, final CTSdtRow ctSdtRow) {
                    CTTblImpl.this.insertNewSdt(n).set((XmlObject)ctSdtRow);
                }
                
                @Override
                public CTSdtRow remove(final int n) {
                    final CTSdtRow sdtArray = CTTblImpl.this.getSdtArray(n);
                    CTTblImpl.this.removeSdt(n);
                    return sdtArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfSdtArray();
                }
            }
            return new SdtList();
        }
    }
    
    @Deprecated
    public CTSdtRow[] getSdtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTblImpl.SDT$40, (List)list);
            final CTSdtRow[] array = new CTSdtRow[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSdtRow getSdtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtRow ctSdtRow = (CTSdtRow)this.get_store().find_element_user(CTTblImpl.SDT$40, n);
            if (ctSdtRow == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSdtRow;
        }
    }
    
    public int sizeOfSdtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.SDT$40);
        }
    }
    
    public void setSdtArray(final CTSdtRow[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.SDT$40);
    }
    
    public void setSdtArray(final int n, final CTSdtRow ctSdtRow) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtRow, CTTblImpl.SDT$40, n, (short)2);
    }
    
    public CTSdtRow insertNewSdt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtRow)this.get_store().insert_element_user(CTTblImpl.SDT$40, n);
        }
    }
    
    public CTSdtRow addNewSdt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtRow)this.get_store().add_element_user(CTTblImpl.SDT$40);
        }
    }
    
    public void removeSdt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.SDT$40, n);
        }
    }
    
    public List<CTProofErr> getProofErrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ProofErrList extends AbstractList<CTProofErr>
            {
                @Override
                public CTProofErr get(final int n) {
                    return CTTblImpl.this.getProofErrArray(n);
                }
                
                @Override
                public CTProofErr set(final int n, final CTProofErr ctProofErr) {
                    final CTProofErr proofErrArray = CTTblImpl.this.getProofErrArray(n);
                    CTTblImpl.this.setProofErrArray(n, ctProofErr);
                    return proofErrArray;
                }
                
                @Override
                public void add(final int n, final CTProofErr ctProofErr) {
                    CTTblImpl.this.insertNewProofErr(n).set((XmlObject)ctProofErr);
                }
                
                @Override
                public CTProofErr remove(final int n) {
                    final CTProofErr proofErrArray = CTTblImpl.this.getProofErrArray(n);
                    CTTblImpl.this.removeProofErr(n);
                    return proofErrArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfProofErrArray();
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
            this.get_store().find_all_element_users(CTTblImpl.PROOFERR$42, (List)list);
            final CTProofErr[] array = new CTProofErr[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTProofErr getProofErrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTProofErr ctProofErr = (CTProofErr)this.get_store().find_element_user(CTTblImpl.PROOFERR$42, n);
            if (ctProofErr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctProofErr;
        }
    }
    
    public int sizeOfProofErrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.PROOFERR$42);
        }
    }
    
    public void setProofErrArray(final CTProofErr[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.PROOFERR$42);
    }
    
    public void setProofErrArray(final int n, final CTProofErr ctProofErr) {
        this.generatedSetterHelperImpl((XmlObject)ctProofErr, CTTblImpl.PROOFERR$42, n, (short)2);
    }
    
    public CTProofErr insertNewProofErr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProofErr)this.get_store().insert_element_user(CTTblImpl.PROOFERR$42, n);
        }
    }
    
    public CTProofErr addNewProofErr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProofErr)this.get_store().add_element_user(CTTblImpl.PROOFERR$42);
        }
    }
    
    public void removeProofErr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.PROOFERR$42, n);
        }
    }
    
    public List<CTPermStart> getPermStartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PermStartList extends AbstractList<CTPermStart>
            {
                @Override
                public CTPermStart get(final int n) {
                    return CTTblImpl.this.getPermStartArray(n);
                }
                
                @Override
                public CTPermStart set(final int n, final CTPermStart ctPermStart) {
                    final CTPermStart permStartArray = CTTblImpl.this.getPermStartArray(n);
                    CTTblImpl.this.setPermStartArray(n, ctPermStart);
                    return permStartArray;
                }
                
                @Override
                public void add(final int n, final CTPermStart ctPermStart) {
                    CTTblImpl.this.insertNewPermStart(n).set((XmlObject)ctPermStart);
                }
                
                @Override
                public CTPermStart remove(final int n) {
                    final CTPermStart permStartArray = CTTblImpl.this.getPermStartArray(n);
                    CTTblImpl.this.removePermStart(n);
                    return permStartArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfPermStartArray();
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
            this.get_store().find_all_element_users(CTTblImpl.PERMSTART$44, (List)list);
            final CTPermStart[] array = new CTPermStart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPermStart getPermStartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPermStart ctPermStart = (CTPermStart)this.get_store().find_element_user(CTTblImpl.PERMSTART$44, n);
            if (ctPermStart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPermStart;
        }
    }
    
    public int sizeOfPermStartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.PERMSTART$44);
        }
    }
    
    public void setPermStartArray(final CTPermStart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.PERMSTART$44);
    }
    
    public void setPermStartArray(final int n, final CTPermStart ctPermStart) {
        this.generatedSetterHelperImpl((XmlObject)ctPermStart, CTTblImpl.PERMSTART$44, n, (short)2);
    }
    
    public CTPermStart insertNewPermStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPermStart)this.get_store().insert_element_user(CTTblImpl.PERMSTART$44, n);
        }
    }
    
    public CTPermStart addNewPermStart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPermStart)this.get_store().add_element_user(CTTblImpl.PERMSTART$44);
        }
    }
    
    public void removePermStart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.PERMSTART$44, n);
        }
    }
    
    public List<CTPerm> getPermEndList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PermEndList extends AbstractList<CTPerm>
            {
                @Override
                public CTPerm get(final int n) {
                    return CTTblImpl.this.getPermEndArray(n);
                }
                
                @Override
                public CTPerm set(final int n, final CTPerm ctPerm) {
                    final CTPerm permEndArray = CTTblImpl.this.getPermEndArray(n);
                    CTTblImpl.this.setPermEndArray(n, ctPerm);
                    return permEndArray;
                }
                
                @Override
                public void add(final int n, final CTPerm ctPerm) {
                    CTTblImpl.this.insertNewPermEnd(n).set((XmlObject)ctPerm);
                }
                
                @Override
                public CTPerm remove(final int n) {
                    final CTPerm permEndArray = CTTblImpl.this.getPermEndArray(n);
                    CTTblImpl.this.removePermEnd(n);
                    return permEndArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfPermEndArray();
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
            this.get_store().find_all_element_users(CTTblImpl.PERMEND$46, (List)list);
            final CTPerm[] array = new CTPerm[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPerm getPermEndArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPerm ctPerm = (CTPerm)this.get_store().find_element_user(CTTblImpl.PERMEND$46, n);
            if (ctPerm == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPerm;
        }
    }
    
    public int sizeOfPermEndArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.PERMEND$46);
        }
    }
    
    public void setPermEndArray(final CTPerm[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.PERMEND$46);
    }
    
    public void setPermEndArray(final int n, final CTPerm ctPerm) {
        this.generatedSetterHelperImpl((XmlObject)ctPerm, CTTblImpl.PERMEND$46, n, (short)2);
    }
    
    public CTPerm insertNewPermEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerm)this.get_store().insert_element_user(CTTblImpl.PERMEND$46, n);
        }
    }
    
    public CTPerm addNewPermEnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerm)this.get_store().add_element_user(CTTblImpl.PERMEND$46);
        }
    }
    
    public void removePermEnd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.PERMEND$46, n);
        }
    }
    
    public List<CTRunTrackChange> getInsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class InsList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTTblImpl.this.getInsArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange insArray = CTTblImpl.this.getInsArray(n);
                    CTTblImpl.this.setInsArray(n, ctRunTrackChange);
                    return insArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTTblImpl.this.insertNewIns(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange insArray = CTTblImpl.this.getInsArray(n);
                    CTTblImpl.this.removeIns(n);
                    return insArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfInsArray();
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
            this.get_store().find_all_element_users(CTTblImpl.INS$48, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getInsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTTblImpl.INS$48, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfInsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.INS$48);
        }
    }
    
    public void setInsArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.INS$48);
    }
    
    public void setInsArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTTblImpl.INS$48, n, (short)2);
    }
    
    public CTRunTrackChange insertNewIns(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTTblImpl.INS$48, n);
        }
    }
    
    public CTRunTrackChange addNewIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTTblImpl.INS$48);
        }
    }
    
    public void removeIns(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.INS$48, n);
        }
    }
    
    public List<CTRunTrackChange> getDelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DelList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTTblImpl.this.getDelArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange delArray = CTTblImpl.this.getDelArray(n);
                    CTTblImpl.this.setDelArray(n, ctRunTrackChange);
                    return delArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTTblImpl.this.insertNewDel(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange delArray = CTTblImpl.this.getDelArray(n);
                    CTTblImpl.this.removeDel(n);
                    return delArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfDelArray();
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
            this.get_store().find_all_element_users(CTTblImpl.DEL$50, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getDelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTTblImpl.DEL$50, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfDelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.DEL$50);
        }
    }
    
    public void setDelArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.DEL$50);
    }
    
    public void setDelArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTTblImpl.DEL$50, n, (short)2);
    }
    
    public CTRunTrackChange insertNewDel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTTblImpl.DEL$50, n);
        }
    }
    
    public CTRunTrackChange addNewDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTTblImpl.DEL$50);
        }
    }
    
    public void removeDel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.DEL$50, n);
        }
    }
    
    public List<CTRunTrackChange> getMoveFromList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveFromList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTTblImpl.this.getMoveFromArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange moveFromArray = CTTblImpl.this.getMoveFromArray(n);
                    CTTblImpl.this.setMoveFromArray(n, ctRunTrackChange);
                    return moveFromArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTTblImpl.this.insertNewMoveFrom(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange moveFromArray = CTTblImpl.this.getMoveFromArray(n);
                    CTTblImpl.this.removeMoveFrom(n);
                    return moveFromArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfMoveFromArray();
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
            this.get_store().find_all_element_users(CTTblImpl.MOVEFROM$52, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getMoveFromArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTTblImpl.MOVEFROM$52, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfMoveFromArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.MOVEFROM$52);
        }
    }
    
    public void setMoveFromArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.MOVEFROM$52);
    }
    
    public void setMoveFromArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTTblImpl.MOVEFROM$52, n, (short)2);
    }
    
    public CTRunTrackChange insertNewMoveFrom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTTblImpl.MOVEFROM$52, n);
        }
    }
    
    public CTRunTrackChange addNewMoveFrom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTTblImpl.MOVEFROM$52);
        }
    }
    
    public void removeMoveFrom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.MOVEFROM$52, n);
        }
    }
    
    public List<CTRunTrackChange> getMoveToList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToList extends AbstractList<CTRunTrackChange>
            {
                @Override
                public CTRunTrackChange get(final int n) {
                    return CTTblImpl.this.getMoveToArray(n);
                }
                
                @Override
                public CTRunTrackChange set(final int n, final CTRunTrackChange ctRunTrackChange) {
                    final CTRunTrackChange moveToArray = CTTblImpl.this.getMoveToArray(n);
                    CTTblImpl.this.setMoveToArray(n, ctRunTrackChange);
                    return moveToArray;
                }
                
                @Override
                public void add(final int n, final CTRunTrackChange ctRunTrackChange) {
                    CTTblImpl.this.insertNewMoveTo(n).set((XmlObject)ctRunTrackChange);
                }
                
                @Override
                public CTRunTrackChange remove(final int n) {
                    final CTRunTrackChange moveToArray = CTTblImpl.this.getMoveToArray(n);
                    CTTblImpl.this.removeMoveTo(n);
                    return moveToArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfMoveToArray();
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
            this.get_store().find_all_element_users(CTTblImpl.MOVETO$54, (List)list);
            final CTRunTrackChange[] array = new CTRunTrackChange[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRunTrackChange getMoveToArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRunTrackChange ctRunTrackChange = (CTRunTrackChange)this.get_store().find_element_user(CTTblImpl.MOVETO$54, n);
            if (ctRunTrackChange == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRunTrackChange;
        }
    }
    
    public int sizeOfMoveToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.MOVETO$54);
        }
    }
    
    public void setMoveToArray(final CTRunTrackChange[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.MOVETO$54);
    }
    
    public void setMoveToArray(final int n, final CTRunTrackChange ctRunTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctRunTrackChange, CTTblImpl.MOVETO$54, n, (short)2);
    }
    
    public CTRunTrackChange insertNewMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().insert_element_user(CTTblImpl.MOVETO$54, n);
        }
    }
    
    public CTRunTrackChange addNewMoveTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRunTrackChange)this.get_store().add_element_user(CTTblImpl.MOVETO$54);
        }
    }
    
    public void removeMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.MOVETO$54, n);
        }
    }
    
    public List<CTOMathPara> getOMathParaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OMathParaList extends AbstractList<CTOMathPara>
            {
                @Override
                public CTOMathPara get(final int n) {
                    return CTTblImpl.this.getOMathParaArray(n);
                }
                
                @Override
                public CTOMathPara set(final int n, final CTOMathPara ctoMathPara) {
                    final CTOMathPara oMathParaArray = CTTblImpl.this.getOMathParaArray(n);
                    CTTblImpl.this.setOMathParaArray(n, ctoMathPara);
                    return oMathParaArray;
                }
                
                @Override
                public void add(final int n, final CTOMathPara ctoMathPara) {
                    CTTblImpl.this.insertNewOMathPara(n).set((XmlObject)ctoMathPara);
                }
                
                @Override
                public CTOMathPara remove(final int n) {
                    final CTOMathPara oMathParaArray = CTTblImpl.this.getOMathParaArray(n);
                    CTTblImpl.this.removeOMathPara(n);
                    return oMathParaArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfOMathParaArray();
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
            this.get_store().find_all_element_users(CTTblImpl.OMATHPARA$56, (List)list);
            final CTOMathPara[] array = new CTOMathPara[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOMathPara getOMathParaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOMathPara ctoMathPara = (CTOMathPara)this.get_store().find_element_user(CTTblImpl.OMATHPARA$56, n);
            if (ctoMathPara == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctoMathPara;
        }
    }
    
    public int sizeOfOMathParaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.OMATHPARA$56);
        }
    }
    
    public void setOMathParaArray(final CTOMathPara[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.OMATHPARA$56);
    }
    
    public void setOMathParaArray(final int n, final CTOMathPara ctoMathPara) {
        this.generatedSetterHelperImpl((XmlObject)ctoMathPara, CTTblImpl.OMATHPARA$56, n, (short)2);
    }
    
    public CTOMathPara insertNewOMathPara(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMathPara)this.get_store().insert_element_user(CTTblImpl.OMATHPARA$56, n);
        }
    }
    
    public CTOMathPara addNewOMathPara() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMathPara)this.get_store().add_element_user(CTTblImpl.OMATHPARA$56);
        }
    }
    
    public void removeOMathPara(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.OMATHPARA$56, n);
        }
    }
    
    public List<CTOMath> getOMathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OMathList extends AbstractList<CTOMath>
            {
                @Override
                public CTOMath get(final int n) {
                    return CTTblImpl.this.getOMathArray(n);
                }
                
                @Override
                public CTOMath set(final int n, final CTOMath ctoMath) {
                    final CTOMath oMathArray = CTTblImpl.this.getOMathArray(n);
                    CTTblImpl.this.setOMathArray(n, ctoMath);
                    return oMathArray;
                }
                
                @Override
                public void add(final int n, final CTOMath ctoMath) {
                    CTTblImpl.this.insertNewOMath(n).set((XmlObject)ctoMath);
                }
                
                @Override
                public CTOMath remove(final int n) {
                    final CTOMath oMathArray = CTTblImpl.this.getOMathArray(n);
                    CTTblImpl.this.removeOMath(n);
                    return oMathArray;
                }
                
                @Override
                public int size() {
                    return CTTblImpl.this.sizeOfOMathArray();
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
            this.get_store().find_all_element_users(CTTblImpl.OMATH$58, (List)list);
            final CTOMath[] array = new CTOMath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOMath getOMathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOMath ctoMath = (CTOMath)this.get_store().find_element_user(CTTblImpl.OMATH$58, n);
            if (ctoMath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctoMath;
        }
    }
    
    public int sizeOfOMathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblImpl.OMATH$58);
        }
    }
    
    public void setOMathArray(final CTOMath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTblImpl.OMATH$58);
    }
    
    public void setOMathArray(final int n, final CTOMath ctoMath) {
        this.generatedSetterHelperImpl((XmlObject)ctoMath, CTTblImpl.OMATH$58, n, (short)2);
    }
    
    public CTOMath insertNewOMath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMath)this.get_store().insert_element_user(CTTblImpl.OMATH$58, n);
        }
    }
    
    public CTOMath addNewOMath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOMath)this.get_store().add_element_user(CTTblImpl.OMATH$58);
        }
    }
    
    public void removeOMath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblImpl.OMATH$58, n);
        }
    }
    
    static {
        BOOKMARKSTART$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookmarkStart");
        BOOKMARKEND$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookmarkEnd");
        MOVEFROMRANGESTART$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFromRangeStart");
        MOVEFROMRANGEEND$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFromRangeEnd");
        MOVETORANGESTART$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveToRangeStart");
        MOVETORANGEEND$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveToRangeEnd");
        COMMENTRANGESTART$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "commentRangeStart");
        COMMENTRANGEEND$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "commentRangeEnd");
        CUSTOMXMLINSRANGESTART$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlInsRangeStart");
        CUSTOMXMLINSRANGEEND$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlInsRangeEnd");
        CUSTOMXMLDELRANGESTART$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlDelRangeStart");
        CUSTOMXMLDELRANGEEND$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlDelRangeEnd");
        CUSTOMXMLMOVEFROMRANGESTART$24 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveFromRangeStart");
        CUSTOMXMLMOVEFROMRANGEEND$26 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveFromRangeEnd");
        CUSTOMXMLMOVETORANGESTART$28 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveToRangeStart");
        CUSTOMXMLMOVETORANGEEND$30 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXmlMoveToRangeEnd");
        TBLPR$32 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblPr");
        TBLGRID$34 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblGrid");
        TR$36 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tr");
        CUSTOMXML$38 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customXml");
        SDT$40 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sdt");
        PROOFERR$42 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "proofErr");
        PERMSTART$44 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "permStart");
        PERMEND$46 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "permEnd");
        INS$48 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ins");
        DEL$50 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "del");
        MOVEFROM$52 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveFrom");
        MOVETO$54 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "moveTo");
        OMATHPARA$56 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "oMathPara");
        OMATH$58 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "oMath");
    }
}
