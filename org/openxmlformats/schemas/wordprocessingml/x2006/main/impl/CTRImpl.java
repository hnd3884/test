package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLongHexNumber;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPTab;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkup;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdnRef;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRuby;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFldChar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPicture;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSym;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEmpty;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBr;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRImpl extends XmlComplexContentImpl implements CTR
{
    private static final long serialVersionUID = 1L;
    private static final QName RPR$0;
    private static final QName BR$2;
    private static final QName T$4;
    private static final QName DELTEXT$6;
    private static final QName INSTRTEXT$8;
    private static final QName DELINSTRTEXT$10;
    private static final QName NOBREAKHYPHEN$12;
    private static final QName SOFTHYPHEN$14;
    private static final QName DAYSHORT$16;
    private static final QName MONTHSHORT$18;
    private static final QName YEARSHORT$20;
    private static final QName DAYLONG$22;
    private static final QName MONTHLONG$24;
    private static final QName YEARLONG$26;
    private static final QName ANNOTATIONREF$28;
    private static final QName FOOTNOTEREF$30;
    private static final QName ENDNOTEREF$32;
    private static final QName SEPARATOR$34;
    private static final QName CONTINUATIONSEPARATOR$36;
    private static final QName SYM$38;
    private static final QName PGNUM$40;
    private static final QName CR$42;
    private static final QName TAB$44;
    private static final QName OBJECT$46;
    private static final QName PICT$48;
    private static final QName FLDCHAR$50;
    private static final QName RUBY$52;
    private static final QName FOOTNOTEREFERENCE$54;
    private static final QName ENDNOTEREFERENCE$56;
    private static final QName COMMENTREFERENCE$58;
    private static final QName DRAWING$60;
    private static final QName PTAB$62;
    private static final QName LASTRENDEREDPAGEBREAK$64;
    private static final QName RSIDRPR$66;
    private static final QName RSIDDEL$68;
    private static final QName RSIDR$70;
    
    public CTRImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTRPr getRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRPr ctrPr = (CTRPr)this.get_store().find_element_user(CTRImpl.RPR$0, 0);
            if (ctrPr == null) {
                return null;
            }
            return ctrPr;
        }
    }
    
    public boolean isSetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.RPR$0) != 0;
        }
    }
    
    public void setRPr(final CTRPr ctrPr) {
        this.generatedSetterHelperImpl((XmlObject)ctrPr, CTRImpl.RPR$0, 0, (short)1);
    }
    
    public CTRPr addNewRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRPr)this.get_store().add_element_user(CTRImpl.RPR$0);
        }
    }
    
    public void unsetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.RPR$0, 0);
        }
    }
    
    public List<CTBr> getBrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BrList extends AbstractList<CTBr>
            {
                @Override
                public CTBr get(final int n) {
                    return CTRImpl.this.getBrArray(n);
                }
                
                @Override
                public CTBr set(final int n, final CTBr ctBr) {
                    final CTBr brArray = CTRImpl.this.getBrArray(n);
                    CTRImpl.this.setBrArray(n, ctBr);
                    return brArray;
                }
                
                @Override
                public void add(final int n, final CTBr ctBr) {
                    CTRImpl.this.insertNewBr(n).set((XmlObject)ctBr);
                }
                
                @Override
                public CTBr remove(final int n) {
                    final CTBr brArray = CTRImpl.this.getBrArray(n);
                    CTRImpl.this.removeBr(n);
                    return brArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfBrArray();
                }
            }
            return new BrList();
        }
    }
    
    @Deprecated
    public CTBr[] getBrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.BR$2, (List)list);
            final CTBr[] array = new CTBr[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBr getBrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBr ctBr = (CTBr)this.get_store().find_element_user(CTRImpl.BR$2, n);
            if (ctBr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBr;
        }
    }
    
    public int sizeOfBrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.BR$2);
        }
    }
    
    public void setBrArray(final CTBr[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.BR$2);
    }
    
    public void setBrArray(final int n, final CTBr ctBr) {
        this.generatedSetterHelperImpl((XmlObject)ctBr, CTRImpl.BR$2, n, (short)2);
    }
    
    public CTBr insertNewBr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBr)this.get_store().insert_element_user(CTRImpl.BR$2, n);
        }
    }
    
    public CTBr addNewBr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBr)this.get_store().add_element_user(CTRImpl.BR$2);
        }
    }
    
    public void removeBr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.BR$2, n);
        }
    }
    
    public List<CTText> getTList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TList extends AbstractList<CTText>
            {
                @Override
                public CTText get(final int n) {
                    return CTRImpl.this.getTArray(n);
                }
                
                @Override
                public CTText set(final int n, final CTText ctText) {
                    final CTText tArray = CTRImpl.this.getTArray(n);
                    CTRImpl.this.setTArray(n, ctText);
                    return tArray;
                }
                
                @Override
                public void add(final int n, final CTText ctText) {
                    CTRImpl.this.insertNewT(n).set((XmlObject)ctText);
                }
                
                @Override
                public CTText remove(final int n) {
                    final CTText tArray = CTRImpl.this.getTArray(n);
                    CTRImpl.this.removeT(n);
                    return tArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfTArray();
                }
            }
            return new TList();
        }
    }
    
    @Deprecated
    public CTText[] getTArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.T$4, (List)list);
            final CTText[] array = new CTText[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTText getTArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTText ctText = (CTText)this.get_store().find_element_user(CTRImpl.T$4, n);
            if (ctText == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctText;
        }
    }
    
    public int sizeOfTArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.T$4);
        }
    }
    
    public void setTArray(final CTText[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.T$4);
    }
    
    public void setTArray(final int n, final CTText ctText) {
        this.generatedSetterHelperImpl((XmlObject)ctText, CTRImpl.T$4, n, (short)2);
    }
    
    public CTText insertNewT(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTText)this.get_store().insert_element_user(CTRImpl.T$4, n);
        }
    }
    
    public CTText addNewT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTText)this.get_store().add_element_user(CTRImpl.T$4);
        }
    }
    
    public void removeT(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.T$4, n);
        }
    }
    
    public List<CTText> getDelTextList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DelTextList extends AbstractList<CTText>
            {
                @Override
                public CTText get(final int n) {
                    return CTRImpl.this.getDelTextArray(n);
                }
                
                @Override
                public CTText set(final int n, final CTText ctText) {
                    final CTText delTextArray = CTRImpl.this.getDelTextArray(n);
                    CTRImpl.this.setDelTextArray(n, ctText);
                    return delTextArray;
                }
                
                @Override
                public void add(final int n, final CTText ctText) {
                    CTRImpl.this.insertNewDelText(n).set((XmlObject)ctText);
                }
                
                @Override
                public CTText remove(final int n) {
                    final CTText delTextArray = CTRImpl.this.getDelTextArray(n);
                    CTRImpl.this.removeDelText(n);
                    return delTextArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfDelTextArray();
                }
            }
            return new DelTextList();
        }
    }
    
    @Deprecated
    public CTText[] getDelTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.DELTEXT$6, (List)list);
            final CTText[] array = new CTText[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTText getDelTextArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTText ctText = (CTText)this.get_store().find_element_user(CTRImpl.DELTEXT$6, n);
            if (ctText == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctText;
        }
    }
    
    public int sizeOfDelTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.DELTEXT$6);
        }
    }
    
    public void setDelTextArray(final CTText[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.DELTEXT$6);
    }
    
    public void setDelTextArray(final int n, final CTText ctText) {
        this.generatedSetterHelperImpl((XmlObject)ctText, CTRImpl.DELTEXT$6, n, (short)2);
    }
    
    public CTText insertNewDelText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTText)this.get_store().insert_element_user(CTRImpl.DELTEXT$6, n);
        }
    }
    
    public CTText addNewDelText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTText)this.get_store().add_element_user(CTRImpl.DELTEXT$6);
        }
    }
    
    public void removeDelText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.DELTEXT$6, n);
        }
    }
    
    public List<CTText> getInstrTextList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class InstrTextList extends AbstractList<CTText>
            {
                @Override
                public CTText get(final int n) {
                    return CTRImpl.this.getInstrTextArray(n);
                }
                
                @Override
                public CTText set(final int n, final CTText ctText) {
                    final CTText instrTextArray = CTRImpl.this.getInstrTextArray(n);
                    CTRImpl.this.setInstrTextArray(n, ctText);
                    return instrTextArray;
                }
                
                @Override
                public void add(final int n, final CTText ctText) {
                    CTRImpl.this.insertNewInstrText(n).set((XmlObject)ctText);
                }
                
                @Override
                public CTText remove(final int n) {
                    final CTText instrTextArray = CTRImpl.this.getInstrTextArray(n);
                    CTRImpl.this.removeInstrText(n);
                    return instrTextArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfInstrTextArray();
                }
            }
            return new InstrTextList();
        }
    }
    
    @Deprecated
    public CTText[] getInstrTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.INSTRTEXT$8, (List)list);
            final CTText[] array = new CTText[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTText getInstrTextArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTText ctText = (CTText)this.get_store().find_element_user(CTRImpl.INSTRTEXT$8, n);
            if (ctText == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctText;
        }
    }
    
    public int sizeOfInstrTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.INSTRTEXT$8);
        }
    }
    
    public void setInstrTextArray(final CTText[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.INSTRTEXT$8);
    }
    
    public void setInstrTextArray(final int n, final CTText ctText) {
        this.generatedSetterHelperImpl((XmlObject)ctText, CTRImpl.INSTRTEXT$8, n, (short)2);
    }
    
    public CTText insertNewInstrText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTText)this.get_store().insert_element_user(CTRImpl.INSTRTEXT$8, n);
        }
    }
    
    public CTText addNewInstrText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTText)this.get_store().add_element_user(CTRImpl.INSTRTEXT$8);
        }
    }
    
    public void removeInstrText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.INSTRTEXT$8, n);
        }
    }
    
    public List<CTText> getDelInstrTextList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DelInstrTextList extends AbstractList<CTText>
            {
                @Override
                public CTText get(final int n) {
                    return CTRImpl.this.getDelInstrTextArray(n);
                }
                
                @Override
                public CTText set(final int n, final CTText ctText) {
                    final CTText delInstrTextArray = CTRImpl.this.getDelInstrTextArray(n);
                    CTRImpl.this.setDelInstrTextArray(n, ctText);
                    return delInstrTextArray;
                }
                
                @Override
                public void add(final int n, final CTText ctText) {
                    CTRImpl.this.insertNewDelInstrText(n).set((XmlObject)ctText);
                }
                
                @Override
                public CTText remove(final int n) {
                    final CTText delInstrTextArray = CTRImpl.this.getDelInstrTextArray(n);
                    CTRImpl.this.removeDelInstrText(n);
                    return delInstrTextArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfDelInstrTextArray();
                }
            }
            return new DelInstrTextList();
        }
    }
    
    @Deprecated
    public CTText[] getDelInstrTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.DELINSTRTEXT$10, (List)list);
            final CTText[] array = new CTText[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTText getDelInstrTextArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTText ctText = (CTText)this.get_store().find_element_user(CTRImpl.DELINSTRTEXT$10, n);
            if (ctText == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctText;
        }
    }
    
    public int sizeOfDelInstrTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.DELINSTRTEXT$10);
        }
    }
    
    public void setDelInstrTextArray(final CTText[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.DELINSTRTEXT$10);
    }
    
    public void setDelInstrTextArray(final int n, final CTText ctText) {
        this.generatedSetterHelperImpl((XmlObject)ctText, CTRImpl.DELINSTRTEXT$10, n, (short)2);
    }
    
    public CTText insertNewDelInstrText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTText)this.get_store().insert_element_user(CTRImpl.DELINSTRTEXT$10, n);
        }
    }
    
    public CTText addNewDelInstrText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTText)this.get_store().add_element_user(CTRImpl.DELINSTRTEXT$10);
        }
    }
    
    public void removeDelInstrText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.DELINSTRTEXT$10, n);
        }
    }
    
    public List<CTEmpty> getNoBreakHyphenList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class NoBreakHyphenList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getNoBreakHyphenArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty noBreakHyphenArray = CTRImpl.this.getNoBreakHyphenArray(n);
                    CTRImpl.this.setNoBreakHyphenArray(n, ctEmpty);
                    return noBreakHyphenArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewNoBreakHyphen(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty noBreakHyphenArray = CTRImpl.this.getNoBreakHyphenArray(n);
                    CTRImpl.this.removeNoBreakHyphen(n);
                    return noBreakHyphenArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfNoBreakHyphenArray();
                }
            }
            return new NoBreakHyphenList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getNoBreakHyphenArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.NOBREAKHYPHEN$12, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getNoBreakHyphenArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.NOBREAKHYPHEN$12, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfNoBreakHyphenArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.NOBREAKHYPHEN$12);
        }
    }
    
    public void setNoBreakHyphenArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.NOBREAKHYPHEN$12);
    }
    
    public void setNoBreakHyphenArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.NOBREAKHYPHEN$12, n, (short)2);
    }
    
    public CTEmpty insertNewNoBreakHyphen(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.NOBREAKHYPHEN$12, n);
        }
    }
    
    public CTEmpty addNewNoBreakHyphen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.NOBREAKHYPHEN$12);
        }
    }
    
    public void removeNoBreakHyphen(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.NOBREAKHYPHEN$12, n);
        }
    }
    
    public List<CTEmpty> getSoftHyphenList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SoftHyphenList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getSoftHyphenArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty softHyphenArray = CTRImpl.this.getSoftHyphenArray(n);
                    CTRImpl.this.setSoftHyphenArray(n, ctEmpty);
                    return softHyphenArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewSoftHyphen(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty softHyphenArray = CTRImpl.this.getSoftHyphenArray(n);
                    CTRImpl.this.removeSoftHyphen(n);
                    return softHyphenArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfSoftHyphenArray();
                }
            }
            return new SoftHyphenList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getSoftHyphenArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.SOFTHYPHEN$14, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getSoftHyphenArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.SOFTHYPHEN$14, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfSoftHyphenArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.SOFTHYPHEN$14);
        }
    }
    
    public void setSoftHyphenArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.SOFTHYPHEN$14);
    }
    
    public void setSoftHyphenArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.SOFTHYPHEN$14, n, (short)2);
    }
    
    public CTEmpty insertNewSoftHyphen(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.SOFTHYPHEN$14, n);
        }
    }
    
    public CTEmpty addNewSoftHyphen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.SOFTHYPHEN$14);
        }
    }
    
    public void removeSoftHyphen(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.SOFTHYPHEN$14, n);
        }
    }
    
    public List<CTEmpty> getDayShortList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DayShortList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getDayShortArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty dayShortArray = CTRImpl.this.getDayShortArray(n);
                    CTRImpl.this.setDayShortArray(n, ctEmpty);
                    return dayShortArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewDayShort(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty dayShortArray = CTRImpl.this.getDayShortArray(n);
                    CTRImpl.this.removeDayShort(n);
                    return dayShortArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfDayShortArray();
                }
            }
            return new DayShortList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getDayShortArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.DAYSHORT$16, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getDayShortArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.DAYSHORT$16, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfDayShortArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.DAYSHORT$16);
        }
    }
    
    public void setDayShortArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.DAYSHORT$16);
    }
    
    public void setDayShortArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.DAYSHORT$16, n, (short)2);
    }
    
    public CTEmpty insertNewDayShort(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.DAYSHORT$16, n);
        }
    }
    
    public CTEmpty addNewDayShort() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.DAYSHORT$16);
        }
    }
    
    public void removeDayShort(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.DAYSHORT$16, n);
        }
    }
    
    public List<CTEmpty> getMonthShortList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MonthShortList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getMonthShortArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty monthShortArray = CTRImpl.this.getMonthShortArray(n);
                    CTRImpl.this.setMonthShortArray(n, ctEmpty);
                    return monthShortArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewMonthShort(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty monthShortArray = CTRImpl.this.getMonthShortArray(n);
                    CTRImpl.this.removeMonthShort(n);
                    return monthShortArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfMonthShortArray();
                }
            }
            return new MonthShortList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getMonthShortArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.MONTHSHORT$18, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getMonthShortArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.MONTHSHORT$18, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfMonthShortArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.MONTHSHORT$18);
        }
    }
    
    public void setMonthShortArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.MONTHSHORT$18);
    }
    
    public void setMonthShortArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.MONTHSHORT$18, n, (short)2);
    }
    
    public CTEmpty insertNewMonthShort(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.MONTHSHORT$18, n);
        }
    }
    
    public CTEmpty addNewMonthShort() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.MONTHSHORT$18);
        }
    }
    
    public void removeMonthShort(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.MONTHSHORT$18, n);
        }
    }
    
    public List<CTEmpty> getYearShortList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class YearShortList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getYearShortArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty yearShortArray = CTRImpl.this.getYearShortArray(n);
                    CTRImpl.this.setYearShortArray(n, ctEmpty);
                    return yearShortArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewYearShort(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty yearShortArray = CTRImpl.this.getYearShortArray(n);
                    CTRImpl.this.removeYearShort(n);
                    return yearShortArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfYearShortArray();
                }
            }
            return new YearShortList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getYearShortArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.YEARSHORT$20, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getYearShortArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.YEARSHORT$20, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfYearShortArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.YEARSHORT$20);
        }
    }
    
    public void setYearShortArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.YEARSHORT$20);
    }
    
    public void setYearShortArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.YEARSHORT$20, n, (short)2);
    }
    
    public CTEmpty insertNewYearShort(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.YEARSHORT$20, n);
        }
    }
    
    public CTEmpty addNewYearShort() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.YEARSHORT$20);
        }
    }
    
    public void removeYearShort(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.YEARSHORT$20, n);
        }
    }
    
    public List<CTEmpty> getDayLongList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DayLongList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getDayLongArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty dayLongArray = CTRImpl.this.getDayLongArray(n);
                    CTRImpl.this.setDayLongArray(n, ctEmpty);
                    return dayLongArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewDayLong(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty dayLongArray = CTRImpl.this.getDayLongArray(n);
                    CTRImpl.this.removeDayLong(n);
                    return dayLongArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfDayLongArray();
                }
            }
            return new DayLongList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getDayLongArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.DAYLONG$22, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getDayLongArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.DAYLONG$22, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfDayLongArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.DAYLONG$22);
        }
    }
    
    public void setDayLongArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.DAYLONG$22);
    }
    
    public void setDayLongArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.DAYLONG$22, n, (short)2);
    }
    
    public CTEmpty insertNewDayLong(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.DAYLONG$22, n);
        }
    }
    
    public CTEmpty addNewDayLong() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.DAYLONG$22);
        }
    }
    
    public void removeDayLong(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.DAYLONG$22, n);
        }
    }
    
    public List<CTEmpty> getMonthLongList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MonthLongList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getMonthLongArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty monthLongArray = CTRImpl.this.getMonthLongArray(n);
                    CTRImpl.this.setMonthLongArray(n, ctEmpty);
                    return monthLongArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewMonthLong(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty monthLongArray = CTRImpl.this.getMonthLongArray(n);
                    CTRImpl.this.removeMonthLong(n);
                    return monthLongArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfMonthLongArray();
                }
            }
            return new MonthLongList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getMonthLongArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.MONTHLONG$24, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getMonthLongArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.MONTHLONG$24, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfMonthLongArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.MONTHLONG$24);
        }
    }
    
    public void setMonthLongArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.MONTHLONG$24);
    }
    
    public void setMonthLongArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.MONTHLONG$24, n, (short)2);
    }
    
    public CTEmpty insertNewMonthLong(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.MONTHLONG$24, n);
        }
    }
    
    public CTEmpty addNewMonthLong() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.MONTHLONG$24);
        }
    }
    
    public void removeMonthLong(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.MONTHLONG$24, n);
        }
    }
    
    public List<CTEmpty> getYearLongList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class YearLongList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getYearLongArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty yearLongArray = CTRImpl.this.getYearLongArray(n);
                    CTRImpl.this.setYearLongArray(n, ctEmpty);
                    return yearLongArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewYearLong(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty yearLongArray = CTRImpl.this.getYearLongArray(n);
                    CTRImpl.this.removeYearLong(n);
                    return yearLongArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfYearLongArray();
                }
            }
            return new YearLongList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getYearLongArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.YEARLONG$26, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getYearLongArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.YEARLONG$26, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfYearLongArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.YEARLONG$26);
        }
    }
    
    public void setYearLongArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.YEARLONG$26);
    }
    
    public void setYearLongArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.YEARLONG$26, n, (short)2);
    }
    
    public CTEmpty insertNewYearLong(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.YEARLONG$26, n);
        }
    }
    
    public CTEmpty addNewYearLong() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.YEARLONG$26);
        }
    }
    
    public void removeYearLong(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.YEARLONG$26, n);
        }
    }
    
    public List<CTEmpty> getAnnotationRefList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AnnotationRefList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getAnnotationRefArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty annotationRefArray = CTRImpl.this.getAnnotationRefArray(n);
                    CTRImpl.this.setAnnotationRefArray(n, ctEmpty);
                    return annotationRefArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewAnnotationRef(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty annotationRefArray = CTRImpl.this.getAnnotationRefArray(n);
                    CTRImpl.this.removeAnnotationRef(n);
                    return annotationRefArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfAnnotationRefArray();
                }
            }
            return new AnnotationRefList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getAnnotationRefArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.ANNOTATIONREF$28, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getAnnotationRefArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.ANNOTATIONREF$28, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfAnnotationRefArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.ANNOTATIONREF$28);
        }
    }
    
    public void setAnnotationRefArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.ANNOTATIONREF$28);
    }
    
    public void setAnnotationRefArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.ANNOTATIONREF$28, n, (short)2);
    }
    
    public CTEmpty insertNewAnnotationRef(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.ANNOTATIONREF$28, n);
        }
    }
    
    public CTEmpty addNewAnnotationRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.ANNOTATIONREF$28);
        }
    }
    
    public void removeAnnotationRef(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.ANNOTATIONREF$28, n);
        }
    }
    
    public List<CTEmpty> getFootnoteRefList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FootnoteRefList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getFootnoteRefArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty footnoteRefArray = CTRImpl.this.getFootnoteRefArray(n);
                    CTRImpl.this.setFootnoteRefArray(n, ctEmpty);
                    return footnoteRefArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewFootnoteRef(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty footnoteRefArray = CTRImpl.this.getFootnoteRefArray(n);
                    CTRImpl.this.removeFootnoteRef(n);
                    return footnoteRefArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfFootnoteRefArray();
                }
            }
            return new FootnoteRefList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getFootnoteRefArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.FOOTNOTEREF$30, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getFootnoteRefArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.FOOTNOTEREF$30, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfFootnoteRefArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.FOOTNOTEREF$30);
        }
    }
    
    public void setFootnoteRefArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.FOOTNOTEREF$30);
    }
    
    public void setFootnoteRefArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.FOOTNOTEREF$30, n, (short)2);
    }
    
    public CTEmpty insertNewFootnoteRef(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.FOOTNOTEREF$30, n);
        }
    }
    
    public CTEmpty addNewFootnoteRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.FOOTNOTEREF$30);
        }
    }
    
    public void removeFootnoteRef(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.FOOTNOTEREF$30, n);
        }
    }
    
    public List<CTEmpty> getEndnoteRefList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class EndnoteRefList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getEndnoteRefArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty endnoteRefArray = CTRImpl.this.getEndnoteRefArray(n);
                    CTRImpl.this.setEndnoteRefArray(n, ctEmpty);
                    return endnoteRefArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewEndnoteRef(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty endnoteRefArray = CTRImpl.this.getEndnoteRefArray(n);
                    CTRImpl.this.removeEndnoteRef(n);
                    return endnoteRefArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfEndnoteRefArray();
                }
            }
            return new EndnoteRefList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getEndnoteRefArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.ENDNOTEREF$32, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getEndnoteRefArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.ENDNOTEREF$32, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfEndnoteRefArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.ENDNOTEREF$32);
        }
    }
    
    public void setEndnoteRefArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.ENDNOTEREF$32);
    }
    
    public void setEndnoteRefArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.ENDNOTEREF$32, n, (short)2);
    }
    
    public CTEmpty insertNewEndnoteRef(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.ENDNOTEREF$32, n);
        }
    }
    
    public CTEmpty addNewEndnoteRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.ENDNOTEREF$32);
        }
    }
    
    public void removeEndnoteRef(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.ENDNOTEREF$32, n);
        }
    }
    
    public List<CTEmpty> getSeparatorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SeparatorList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getSeparatorArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty separatorArray = CTRImpl.this.getSeparatorArray(n);
                    CTRImpl.this.setSeparatorArray(n, ctEmpty);
                    return separatorArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewSeparator(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty separatorArray = CTRImpl.this.getSeparatorArray(n);
                    CTRImpl.this.removeSeparator(n);
                    return separatorArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfSeparatorArray();
                }
            }
            return new SeparatorList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getSeparatorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.SEPARATOR$34, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getSeparatorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.SEPARATOR$34, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfSeparatorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.SEPARATOR$34);
        }
    }
    
    public void setSeparatorArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.SEPARATOR$34);
    }
    
    public void setSeparatorArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.SEPARATOR$34, n, (short)2);
    }
    
    public CTEmpty insertNewSeparator(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.SEPARATOR$34, n);
        }
    }
    
    public CTEmpty addNewSeparator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.SEPARATOR$34);
        }
    }
    
    public void removeSeparator(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.SEPARATOR$34, n);
        }
    }
    
    public List<CTEmpty> getContinuationSeparatorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ContinuationSeparatorList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getContinuationSeparatorArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty continuationSeparatorArray = CTRImpl.this.getContinuationSeparatorArray(n);
                    CTRImpl.this.setContinuationSeparatorArray(n, ctEmpty);
                    return continuationSeparatorArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewContinuationSeparator(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty continuationSeparatorArray = CTRImpl.this.getContinuationSeparatorArray(n);
                    CTRImpl.this.removeContinuationSeparator(n);
                    return continuationSeparatorArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfContinuationSeparatorArray();
                }
            }
            return new ContinuationSeparatorList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getContinuationSeparatorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.CONTINUATIONSEPARATOR$36, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getContinuationSeparatorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.CONTINUATIONSEPARATOR$36, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfContinuationSeparatorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.CONTINUATIONSEPARATOR$36);
        }
    }
    
    public void setContinuationSeparatorArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.CONTINUATIONSEPARATOR$36);
    }
    
    public void setContinuationSeparatorArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.CONTINUATIONSEPARATOR$36, n, (short)2);
    }
    
    public CTEmpty insertNewContinuationSeparator(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.CONTINUATIONSEPARATOR$36, n);
        }
    }
    
    public CTEmpty addNewContinuationSeparator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.CONTINUATIONSEPARATOR$36);
        }
    }
    
    public void removeContinuationSeparator(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.CONTINUATIONSEPARATOR$36, n);
        }
    }
    
    public List<CTSym> getSymList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SymList extends AbstractList<CTSym>
            {
                @Override
                public CTSym get(final int n) {
                    return CTRImpl.this.getSymArray(n);
                }
                
                @Override
                public CTSym set(final int n, final CTSym ctSym) {
                    final CTSym symArray = CTRImpl.this.getSymArray(n);
                    CTRImpl.this.setSymArray(n, ctSym);
                    return symArray;
                }
                
                @Override
                public void add(final int n, final CTSym ctSym) {
                    CTRImpl.this.insertNewSym(n).set((XmlObject)ctSym);
                }
                
                @Override
                public CTSym remove(final int n) {
                    final CTSym symArray = CTRImpl.this.getSymArray(n);
                    CTRImpl.this.removeSym(n);
                    return symArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfSymArray();
                }
            }
            return new SymList();
        }
    }
    
    @Deprecated
    public CTSym[] getSymArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.SYM$38, (List)list);
            final CTSym[] array = new CTSym[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSym getSymArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSym ctSym = (CTSym)this.get_store().find_element_user(CTRImpl.SYM$38, n);
            if (ctSym == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSym;
        }
    }
    
    public int sizeOfSymArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.SYM$38);
        }
    }
    
    public void setSymArray(final CTSym[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.SYM$38);
    }
    
    public void setSymArray(final int n, final CTSym ctSym) {
        this.generatedSetterHelperImpl((XmlObject)ctSym, CTRImpl.SYM$38, n, (short)2);
    }
    
    public CTSym insertNewSym(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSym)this.get_store().insert_element_user(CTRImpl.SYM$38, n);
        }
    }
    
    public CTSym addNewSym() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSym)this.get_store().add_element_user(CTRImpl.SYM$38);
        }
    }
    
    public void removeSym(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.SYM$38, n);
        }
    }
    
    public List<CTEmpty> getPgNumList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PgNumList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getPgNumArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty pgNumArray = CTRImpl.this.getPgNumArray(n);
                    CTRImpl.this.setPgNumArray(n, ctEmpty);
                    return pgNumArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewPgNum(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty pgNumArray = CTRImpl.this.getPgNumArray(n);
                    CTRImpl.this.removePgNum(n);
                    return pgNumArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfPgNumArray();
                }
            }
            return new PgNumList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getPgNumArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.PGNUM$40, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getPgNumArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.PGNUM$40, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfPgNumArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.PGNUM$40);
        }
    }
    
    public void setPgNumArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.PGNUM$40);
    }
    
    public void setPgNumArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.PGNUM$40, n, (short)2);
    }
    
    public CTEmpty insertNewPgNum(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.PGNUM$40, n);
        }
    }
    
    public CTEmpty addNewPgNum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.PGNUM$40);
        }
    }
    
    public void removePgNum(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.PGNUM$40, n);
        }
    }
    
    public List<CTEmpty> getCrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CrList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getCrArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty crArray = CTRImpl.this.getCrArray(n);
                    CTRImpl.this.setCrArray(n, ctEmpty);
                    return crArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewCr(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty crArray = CTRImpl.this.getCrArray(n);
                    CTRImpl.this.removeCr(n);
                    return crArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfCrArray();
                }
            }
            return new CrList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getCrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.CR$42, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getCrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.CR$42, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfCrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.CR$42);
        }
    }
    
    public void setCrArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.CR$42);
    }
    
    public void setCrArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.CR$42, n, (short)2);
    }
    
    public CTEmpty insertNewCr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.CR$42, n);
        }
    }
    
    public CTEmpty addNewCr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.CR$42);
        }
    }
    
    public void removeCr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.CR$42, n);
        }
    }
    
    public List<CTEmpty> getTabList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TabList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getTabArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty tabArray = CTRImpl.this.getTabArray(n);
                    CTRImpl.this.setTabArray(n, ctEmpty);
                    return tabArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewTab(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty tabArray = CTRImpl.this.getTabArray(n);
                    CTRImpl.this.removeTab(n);
                    return tabArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfTabArray();
                }
            }
            return new TabList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getTabArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.TAB$44, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getTabArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.TAB$44, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfTabArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.TAB$44);
        }
    }
    
    public void setTabArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.TAB$44);
    }
    
    public void setTabArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.TAB$44, n, (short)2);
    }
    
    public CTEmpty insertNewTab(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.TAB$44, n);
        }
    }
    
    public CTEmpty addNewTab() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.TAB$44);
        }
    }
    
    public void removeTab(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.TAB$44, n);
        }
    }
    
    public List<CTObject> getObjectList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ObjectList extends AbstractList<CTObject>
            {
                @Override
                public CTObject get(final int n) {
                    return CTRImpl.this.getObjectArray(n);
                }
                
                @Override
                public CTObject set(final int n, final CTObject ctObject) {
                    final CTObject objectArray = CTRImpl.this.getObjectArray(n);
                    CTRImpl.this.setObjectArray(n, ctObject);
                    return objectArray;
                }
                
                @Override
                public void add(final int n, final CTObject ctObject) {
                    CTRImpl.this.insertNewObject(n).set((XmlObject)ctObject);
                }
                
                @Override
                public CTObject remove(final int n) {
                    final CTObject objectArray = CTRImpl.this.getObjectArray(n);
                    CTRImpl.this.removeObject(n);
                    return objectArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfObjectArray();
                }
            }
            return new ObjectList();
        }
    }
    
    @Deprecated
    public CTObject[] getObjectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.OBJECT$46, (List)list);
            final CTObject[] array = new CTObject[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTObject getObjectArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTObject ctObject = (CTObject)this.get_store().find_element_user(CTRImpl.OBJECT$46, n);
            if (ctObject == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctObject;
        }
    }
    
    public int sizeOfObjectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.OBJECT$46);
        }
    }
    
    public void setObjectArray(final CTObject[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.OBJECT$46);
    }
    
    public void setObjectArray(final int n, final CTObject ctObject) {
        this.generatedSetterHelperImpl((XmlObject)ctObject, CTRImpl.OBJECT$46, n, (short)2);
    }
    
    public CTObject insertNewObject(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTObject)this.get_store().insert_element_user(CTRImpl.OBJECT$46, n);
        }
    }
    
    public CTObject addNewObject() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTObject)this.get_store().add_element_user(CTRImpl.OBJECT$46);
        }
    }
    
    public void removeObject(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.OBJECT$46, n);
        }
    }
    
    public List<CTPicture> getPictList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PictList extends AbstractList<CTPicture>
            {
                @Override
                public CTPicture get(final int n) {
                    return CTRImpl.this.getPictArray(n);
                }
                
                @Override
                public CTPicture set(final int n, final CTPicture ctPicture) {
                    final CTPicture pictArray = CTRImpl.this.getPictArray(n);
                    CTRImpl.this.setPictArray(n, ctPicture);
                    return pictArray;
                }
                
                @Override
                public void add(final int n, final CTPicture ctPicture) {
                    CTRImpl.this.insertNewPict(n).set((XmlObject)ctPicture);
                }
                
                @Override
                public CTPicture remove(final int n) {
                    final CTPicture pictArray = CTRImpl.this.getPictArray(n);
                    CTRImpl.this.removePict(n);
                    return pictArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfPictArray();
                }
            }
            return new PictList();
        }
    }
    
    @Deprecated
    public CTPicture[] getPictArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.PICT$48, (List)list);
            final CTPicture[] array = new CTPicture[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPicture getPictArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPicture ctPicture = (CTPicture)this.get_store().find_element_user(CTRImpl.PICT$48, n);
            if (ctPicture == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPicture;
        }
    }
    
    public int sizeOfPictArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.PICT$48);
        }
    }
    
    public void setPictArray(final CTPicture[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.PICT$48);
    }
    
    public void setPictArray(final int n, final CTPicture ctPicture) {
        this.generatedSetterHelperImpl((XmlObject)ctPicture, CTRImpl.PICT$48, n, (short)2);
    }
    
    public CTPicture insertNewPict(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPicture)this.get_store().insert_element_user(CTRImpl.PICT$48, n);
        }
    }
    
    public CTPicture addNewPict() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPicture)this.get_store().add_element_user(CTRImpl.PICT$48);
        }
    }
    
    public void removePict(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.PICT$48, n);
        }
    }
    
    public List<CTFldChar> getFldCharList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FldCharList extends AbstractList<CTFldChar>
            {
                @Override
                public CTFldChar get(final int n) {
                    return CTRImpl.this.getFldCharArray(n);
                }
                
                @Override
                public CTFldChar set(final int n, final CTFldChar ctFldChar) {
                    final CTFldChar fldCharArray = CTRImpl.this.getFldCharArray(n);
                    CTRImpl.this.setFldCharArray(n, ctFldChar);
                    return fldCharArray;
                }
                
                @Override
                public void add(final int n, final CTFldChar ctFldChar) {
                    CTRImpl.this.insertNewFldChar(n).set((XmlObject)ctFldChar);
                }
                
                @Override
                public CTFldChar remove(final int n) {
                    final CTFldChar fldCharArray = CTRImpl.this.getFldCharArray(n);
                    CTRImpl.this.removeFldChar(n);
                    return fldCharArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfFldCharArray();
                }
            }
            return new FldCharList();
        }
    }
    
    @Deprecated
    public CTFldChar[] getFldCharArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.FLDCHAR$50, (List)list);
            final CTFldChar[] array = new CTFldChar[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFldChar getFldCharArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFldChar ctFldChar = (CTFldChar)this.get_store().find_element_user(CTRImpl.FLDCHAR$50, n);
            if (ctFldChar == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFldChar;
        }
    }
    
    public int sizeOfFldCharArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.FLDCHAR$50);
        }
    }
    
    public void setFldCharArray(final CTFldChar[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.FLDCHAR$50);
    }
    
    public void setFldCharArray(final int n, final CTFldChar ctFldChar) {
        this.generatedSetterHelperImpl((XmlObject)ctFldChar, CTRImpl.FLDCHAR$50, n, (short)2);
    }
    
    public CTFldChar insertNewFldChar(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFldChar)this.get_store().insert_element_user(CTRImpl.FLDCHAR$50, n);
        }
    }
    
    public CTFldChar addNewFldChar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFldChar)this.get_store().add_element_user(CTRImpl.FLDCHAR$50);
        }
    }
    
    public void removeFldChar(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.FLDCHAR$50, n);
        }
    }
    
    public List<CTRuby> getRubyList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RubyList extends AbstractList<CTRuby>
            {
                @Override
                public CTRuby get(final int n) {
                    return CTRImpl.this.getRubyArray(n);
                }
                
                @Override
                public CTRuby set(final int n, final CTRuby ctRuby) {
                    final CTRuby rubyArray = CTRImpl.this.getRubyArray(n);
                    CTRImpl.this.setRubyArray(n, ctRuby);
                    return rubyArray;
                }
                
                @Override
                public void add(final int n, final CTRuby ctRuby) {
                    CTRImpl.this.insertNewRuby(n).set((XmlObject)ctRuby);
                }
                
                @Override
                public CTRuby remove(final int n) {
                    final CTRuby rubyArray = CTRImpl.this.getRubyArray(n);
                    CTRImpl.this.removeRuby(n);
                    return rubyArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfRubyArray();
                }
            }
            return new RubyList();
        }
    }
    
    @Deprecated
    public CTRuby[] getRubyArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.RUBY$52, (List)list);
            final CTRuby[] array = new CTRuby[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRuby getRubyArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRuby ctRuby = (CTRuby)this.get_store().find_element_user(CTRImpl.RUBY$52, n);
            if (ctRuby == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRuby;
        }
    }
    
    public int sizeOfRubyArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.RUBY$52);
        }
    }
    
    public void setRubyArray(final CTRuby[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.RUBY$52);
    }
    
    public void setRubyArray(final int n, final CTRuby ctRuby) {
        this.generatedSetterHelperImpl((XmlObject)ctRuby, CTRImpl.RUBY$52, n, (short)2);
    }
    
    public CTRuby insertNewRuby(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRuby)this.get_store().insert_element_user(CTRImpl.RUBY$52, n);
        }
    }
    
    public CTRuby addNewRuby() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRuby)this.get_store().add_element_user(CTRImpl.RUBY$52);
        }
    }
    
    public void removeRuby(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.RUBY$52, n);
        }
    }
    
    public List<CTFtnEdnRef> getFootnoteReferenceList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FootnoteReferenceList extends AbstractList<CTFtnEdnRef>
            {
                @Override
                public CTFtnEdnRef get(final int n) {
                    return CTRImpl.this.getFootnoteReferenceArray(n);
                }
                
                @Override
                public CTFtnEdnRef set(final int n, final CTFtnEdnRef ctFtnEdnRef) {
                    final CTFtnEdnRef footnoteReferenceArray = CTRImpl.this.getFootnoteReferenceArray(n);
                    CTRImpl.this.setFootnoteReferenceArray(n, ctFtnEdnRef);
                    return footnoteReferenceArray;
                }
                
                @Override
                public void add(final int n, final CTFtnEdnRef ctFtnEdnRef) {
                    CTRImpl.this.insertNewFootnoteReference(n).set((XmlObject)ctFtnEdnRef);
                }
                
                @Override
                public CTFtnEdnRef remove(final int n) {
                    final CTFtnEdnRef footnoteReferenceArray = CTRImpl.this.getFootnoteReferenceArray(n);
                    CTRImpl.this.removeFootnoteReference(n);
                    return footnoteReferenceArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfFootnoteReferenceArray();
                }
            }
            return new FootnoteReferenceList();
        }
    }
    
    @Deprecated
    public CTFtnEdnRef[] getFootnoteReferenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.FOOTNOTEREFERENCE$54, (List)list);
            final CTFtnEdnRef[] array = new CTFtnEdnRef[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFtnEdnRef getFootnoteReferenceArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFtnEdnRef ctFtnEdnRef = (CTFtnEdnRef)this.get_store().find_element_user(CTRImpl.FOOTNOTEREFERENCE$54, n);
            if (ctFtnEdnRef == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFtnEdnRef;
        }
    }
    
    public int sizeOfFootnoteReferenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.FOOTNOTEREFERENCE$54);
        }
    }
    
    public void setFootnoteReferenceArray(final CTFtnEdnRef[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.FOOTNOTEREFERENCE$54);
    }
    
    public void setFootnoteReferenceArray(final int n, final CTFtnEdnRef ctFtnEdnRef) {
        this.generatedSetterHelperImpl((XmlObject)ctFtnEdnRef, CTRImpl.FOOTNOTEREFERENCE$54, n, (short)2);
    }
    
    public CTFtnEdnRef insertNewFootnoteReference(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFtnEdnRef)this.get_store().insert_element_user(CTRImpl.FOOTNOTEREFERENCE$54, n);
        }
    }
    
    public CTFtnEdnRef addNewFootnoteReference() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFtnEdnRef)this.get_store().add_element_user(CTRImpl.FOOTNOTEREFERENCE$54);
        }
    }
    
    public void removeFootnoteReference(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.FOOTNOTEREFERENCE$54, n);
        }
    }
    
    public List<CTFtnEdnRef> getEndnoteReferenceList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class EndnoteReferenceList extends AbstractList<CTFtnEdnRef>
            {
                @Override
                public CTFtnEdnRef get(final int n) {
                    return CTRImpl.this.getEndnoteReferenceArray(n);
                }
                
                @Override
                public CTFtnEdnRef set(final int n, final CTFtnEdnRef ctFtnEdnRef) {
                    final CTFtnEdnRef endnoteReferenceArray = CTRImpl.this.getEndnoteReferenceArray(n);
                    CTRImpl.this.setEndnoteReferenceArray(n, ctFtnEdnRef);
                    return endnoteReferenceArray;
                }
                
                @Override
                public void add(final int n, final CTFtnEdnRef ctFtnEdnRef) {
                    CTRImpl.this.insertNewEndnoteReference(n).set((XmlObject)ctFtnEdnRef);
                }
                
                @Override
                public CTFtnEdnRef remove(final int n) {
                    final CTFtnEdnRef endnoteReferenceArray = CTRImpl.this.getEndnoteReferenceArray(n);
                    CTRImpl.this.removeEndnoteReference(n);
                    return endnoteReferenceArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfEndnoteReferenceArray();
                }
            }
            return new EndnoteReferenceList();
        }
    }
    
    @Deprecated
    public CTFtnEdnRef[] getEndnoteReferenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.ENDNOTEREFERENCE$56, (List)list);
            final CTFtnEdnRef[] array = new CTFtnEdnRef[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFtnEdnRef getEndnoteReferenceArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFtnEdnRef ctFtnEdnRef = (CTFtnEdnRef)this.get_store().find_element_user(CTRImpl.ENDNOTEREFERENCE$56, n);
            if (ctFtnEdnRef == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFtnEdnRef;
        }
    }
    
    public int sizeOfEndnoteReferenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.ENDNOTEREFERENCE$56);
        }
    }
    
    public void setEndnoteReferenceArray(final CTFtnEdnRef[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.ENDNOTEREFERENCE$56);
    }
    
    public void setEndnoteReferenceArray(final int n, final CTFtnEdnRef ctFtnEdnRef) {
        this.generatedSetterHelperImpl((XmlObject)ctFtnEdnRef, CTRImpl.ENDNOTEREFERENCE$56, n, (short)2);
    }
    
    public CTFtnEdnRef insertNewEndnoteReference(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFtnEdnRef)this.get_store().insert_element_user(CTRImpl.ENDNOTEREFERENCE$56, n);
        }
    }
    
    public CTFtnEdnRef addNewEndnoteReference() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFtnEdnRef)this.get_store().add_element_user(CTRImpl.ENDNOTEREFERENCE$56);
        }
    }
    
    public void removeEndnoteReference(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.ENDNOTEREFERENCE$56, n);
        }
    }
    
    public List<CTMarkup> getCommentReferenceList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentReferenceList extends AbstractList<CTMarkup>
            {
                @Override
                public CTMarkup get(final int n) {
                    return CTRImpl.this.getCommentReferenceArray(n);
                }
                
                @Override
                public CTMarkup set(final int n, final CTMarkup ctMarkup) {
                    final CTMarkup commentReferenceArray = CTRImpl.this.getCommentReferenceArray(n);
                    CTRImpl.this.setCommentReferenceArray(n, ctMarkup);
                    return commentReferenceArray;
                }
                
                @Override
                public void add(final int n, final CTMarkup ctMarkup) {
                    CTRImpl.this.insertNewCommentReference(n).set((XmlObject)ctMarkup);
                }
                
                @Override
                public CTMarkup remove(final int n) {
                    final CTMarkup commentReferenceArray = CTRImpl.this.getCommentReferenceArray(n);
                    CTRImpl.this.removeCommentReference(n);
                    return commentReferenceArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfCommentReferenceArray();
                }
            }
            return new CommentReferenceList();
        }
    }
    
    @Deprecated
    public CTMarkup[] getCommentReferenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.COMMENTREFERENCE$58, (List)list);
            final CTMarkup[] array = new CTMarkup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMarkup getCommentReferenceArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkup ctMarkup = (CTMarkup)this.get_store().find_element_user(CTRImpl.COMMENTREFERENCE$58, n);
            if (ctMarkup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMarkup;
        }
    }
    
    public int sizeOfCommentReferenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.COMMENTREFERENCE$58);
        }
    }
    
    public void setCommentReferenceArray(final CTMarkup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.COMMENTREFERENCE$58);
    }
    
    public void setCommentReferenceArray(final int n, final CTMarkup ctMarkup) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkup, CTRImpl.COMMENTREFERENCE$58, n, (short)2);
    }
    
    public CTMarkup insertNewCommentReference(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().insert_element_user(CTRImpl.COMMENTREFERENCE$58, n);
        }
    }
    
    public CTMarkup addNewCommentReference() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkup)this.get_store().add_element_user(CTRImpl.COMMENTREFERENCE$58);
        }
    }
    
    public void removeCommentReference(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.COMMENTREFERENCE$58, n);
        }
    }
    
    public List<CTDrawing> getDrawingList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DrawingList extends AbstractList<CTDrawing>
            {
                @Override
                public CTDrawing get(final int n) {
                    return CTRImpl.this.getDrawingArray(n);
                }
                
                @Override
                public CTDrawing set(final int n, final CTDrawing ctDrawing) {
                    final CTDrawing drawingArray = CTRImpl.this.getDrawingArray(n);
                    CTRImpl.this.setDrawingArray(n, ctDrawing);
                    return drawingArray;
                }
                
                @Override
                public void add(final int n, final CTDrawing ctDrawing) {
                    CTRImpl.this.insertNewDrawing(n).set((XmlObject)ctDrawing);
                }
                
                @Override
                public CTDrawing remove(final int n) {
                    final CTDrawing drawingArray = CTRImpl.this.getDrawingArray(n);
                    CTRImpl.this.removeDrawing(n);
                    return drawingArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfDrawingArray();
                }
            }
            return new DrawingList();
        }
    }
    
    @Deprecated
    public CTDrawing[] getDrawingArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.DRAWING$60, (List)list);
            final CTDrawing[] array = new CTDrawing[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDrawing getDrawingArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDrawing ctDrawing = (CTDrawing)this.get_store().find_element_user(CTRImpl.DRAWING$60, n);
            if (ctDrawing == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctDrawing;
        }
    }
    
    public int sizeOfDrawingArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.DRAWING$60);
        }
    }
    
    public void setDrawingArray(final CTDrawing[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.DRAWING$60);
    }
    
    public void setDrawingArray(final int n, final CTDrawing ctDrawing) {
        this.generatedSetterHelperImpl((XmlObject)ctDrawing, CTRImpl.DRAWING$60, n, (short)2);
    }
    
    public CTDrawing insertNewDrawing(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDrawing)this.get_store().insert_element_user(CTRImpl.DRAWING$60, n);
        }
    }
    
    public CTDrawing addNewDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDrawing)this.get_store().add_element_user(CTRImpl.DRAWING$60);
        }
    }
    
    public void removeDrawing(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.DRAWING$60, n);
        }
    }
    
    public List<CTPTab> getPtabList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PtabList extends AbstractList<CTPTab>
            {
                @Override
                public CTPTab get(final int n) {
                    return CTRImpl.this.getPtabArray(n);
                }
                
                @Override
                public CTPTab set(final int n, final CTPTab ctpTab) {
                    final CTPTab ptabArray = CTRImpl.this.getPtabArray(n);
                    CTRImpl.this.setPtabArray(n, ctpTab);
                    return ptabArray;
                }
                
                @Override
                public void add(final int n, final CTPTab ctpTab) {
                    CTRImpl.this.insertNewPtab(n).set((XmlObject)ctpTab);
                }
                
                @Override
                public CTPTab remove(final int n) {
                    final CTPTab ptabArray = CTRImpl.this.getPtabArray(n);
                    CTRImpl.this.removePtab(n);
                    return ptabArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfPtabArray();
                }
            }
            return new PtabList();
        }
    }
    
    @Deprecated
    public CTPTab[] getPtabArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.PTAB$62, (List)list);
            final CTPTab[] array = new CTPTab[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPTab getPtabArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPTab ctpTab = (CTPTab)this.get_store().find_element_user(CTRImpl.PTAB$62, n);
            if (ctpTab == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctpTab;
        }
    }
    
    public int sizeOfPtabArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.PTAB$62);
        }
    }
    
    public void setPtabArray(final CTPTab[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.PTAB$62);
    }
    
    public void setPtabArray(final int n, final CTPTab ctpTab) {
        this.generatedSetterHelperImpl((XmlObject)ctpTab, CTRImpl.PTAB$62, n, (short)2);
    }
    
    public CTPTab insertNewPtab(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPTab)this.get_store().insert_element_user(CTRImpl.PTAB$62, n);
        }
    }
    
    public CTPTab addNewPtab() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPTab)this.get_store().add_element_user(CTRImpl.PTAB$62);
        }
    }
    
    public void removePtab(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.PTAB$62, n);
        }
    }
    
    public List<CTEmpty> getLastRenderedPageBreakList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LastRenderedPageBreakList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTRImpl.this.getLastRenderedPageBreakArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty lastRenderedPageBreakArray = CTRImpl.this.getLastRenderedPageBreakArray(n);
                    CTRImpl.this.setLastRenderedPageBreakArray(n, ctEmpty);
                    return lastRenderedPageBreakArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTRImpl.this.insertNewLastRenderedPageBreak(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty lastRenderedPageBreakArray = CTRImpl.this.getLastRenderedPageBreakArray(n);
                    CTRImpl.this.removeLastRenderedPageBreak(n);
                    return lastRenderedPageBreakArray;
                }
                
                @Override
                public int size() {
                    return CTRImpl.this.sizeOfLastRenderedPageBreakArray();
                }
            }
            return new LastRenderedPageBreakList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getLastRenderedPageBreakArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRImpl.LASTRENDEREDPAGEBREAK$64, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getLastRenderedPageBreakArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTRImpl.LASTRENDEREDPAGEBREAK$64, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfLastRenderedPageBreakArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRImpl.LASTRENDEREDPAGEBREAK$64);
        }
    }
    
    public void setLastRenderedPageBreakArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRImpl.LASTRENDEREDPAGEBREAK$64);
    }
    
    public void setLastRenderedPageBreakArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTRImpl.LASTRENDEREDPAGEBREAK$64, n, (short)2);
    }
    
    public CTEmpty insertNewLastRenderedPageBreak(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTRImpl.LASTRENDEREDPAGEBREAK$64, n);
        }
    }
    
    public CTEmpty addNewLastRenderedPageBreak() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTRImpl.LASTRENDEREDPAGEBREAK$64);
        }
    }
    
    public void removeLastRenderedPageBreak(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRImpl.LASTRENDEREDPAGEBREAK$64, n);
        }
    }
    
    public byte[] getRsidRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRImpl.RSIDRPR$66);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STLongHexNumber xgetRsidRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLongHexNumber)this.get_store().find_attribute_user(CTRImpl.RSIDRPR$66);
        }
    }
    
    public boolean isSetRsidRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRImpl.RSIDRPR$66) != null;
        }
    }
    
    public void setRsidRPr(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRImpl.RSIDRPR$66);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRImpl.RSIDRPR$66);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetRsidRPr(final STLongHexNumber stLongHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLongHexNumber stLongHexNumber2 = (STLongHexNumber)this.get_store().find_attribute_user(CTRImpl.RSIDRPR$66);
            if (stLongHexNumber2 == null) {
                stLongHexNumber2 = (STLongHexNumber)this.get_store().add_attribute_user(CTRImpl.RSIDRPR$66);
            }
            stLongHexNumber2.set((XmlObject)stLongHexNumber);
        }
    }
    
    public void unsetRsidRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRImpl.RSIDRPR$66);
        }
    }
    
    public byte[] getRsidDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRImpl.RSIDDEL$68);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STLongHexNumber xgetRsidDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLongHexNumber)this.get_store().find_attribute_user(CTRImpl.RSIDDEL$68);
        }
    }
    
    public boolean isSetRsidDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRImpl.RSIDDEL$68) != null;
        }
    }
    
    public void setRsidDel(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRImpl.RSIDDEL$68);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRImpl.RSIDDEL$68);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetRsidDel(final STLongHexNumber stLongHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLongHexNumber stLongHexNumber2 = (STLongHexNumber)this.get_store().find_attribute_user(CTRImpl.RSIDDEL$68);
            if (stLongHexNumber2 == null) {
                stLongHexNumber2 = (STLongHexNumber)this.get_store().add_attribute_user(CTRImpl.RSIDDEL$68);
            }
            stLongHexNumber2.set((XmlObject)stLongHexNumber);
        }
    }
    
    public void unsetRsidDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRImpl.RSIDDEL$68);
        }
    }
    
    public byte[] getRsidR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRImpl.RSIDR$70);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STLongHexNumber xgetRsidR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLongHexNumber)this.get_store().find_attribute_user(CTRImpl.RSIDR$70);
        }
    }
    
    public boolean isSetRsidR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRImpl.RSIDR$70) != null;
        }
    }
    
    public void setRsidR(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRImpl.RSIDR$70);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRImpl.RSIDR$70);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetRsidR(final STLongHexNumber stLongHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLongHexNumber stLongHexNumber2 = (STLongHexNumber)this.get_store().find_attribute_user(CTRImpl.RSIDR$70);
            if (stLongHexNumber2 == null) {
                stLongHexNumber2 = (STLongHexNumber)this.get_store().add_attribute_user(CTRImpl.RSIDR$70);
            }
            stLongHexNumber2.set((XmlObject)stLongHexNumber);
        }
    }
    
    public void unsetRsidR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRImpl.RSIDR$70);
        }
    }
    
    static {
        RPR$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rPr");
        BR$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "br");
        T$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "t");
        DELTEXT$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "delText");
        INSTRTEXT$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "instrText");
        DELINSTRTEXT$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "delInstrText");
        NOBREAKHYPHEN$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "noBreakHyphen");
        SOFTHYPHEN$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "softHyphen");
        DAYSHORT$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "dayShort");
        MONTHSHORT$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "monthShort");
        YEARSHORT$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "yearShort");
        DAYLONG$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "dayLong");
        MONTHLONG$24 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "monthLong");
        YEARLONG$26 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "yearLong");
        ANNOTATIONREF$28 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "annotationRef");
        FOOTNOTEREF$30 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "footnoteRef");
        ENDNOTEREF$32 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "endnoteRef");
        SEPARATOR$34 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "separator");
        CONTINUATIONSEPARATOR$36 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "continuationSeparator");
        SYM$38 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sym");
        PGNUM$40 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pgNum");
        CR$42 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cr");
        TAB$44 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tab");
        OBJECT$46 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "object");
        PICT$48 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pict");
        FLDCHAR$50 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "fldChar");
        RUBY$52 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ruby");
        FOOTNOTEREFERENCE$54 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "footnoteReference");
        ENDNOTEREFERENCE$56 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "endnoteReference");
        COMMENTREFERENCE$58 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "commentReference");
        DRAWING$60 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "drawing");
        PTAB$62 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ptab");
        LASTRENDEREDPAGEBREAK$64 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lastRenderedPageBreak");
        RSIDRPR$66 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsidRPr");
        RSIDDEL$68 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsidDel");
        RSIDR$70 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsidR");
    }
}
