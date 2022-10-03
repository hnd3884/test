package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLongHexNumber;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPrChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRel;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextDirection;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColumns;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLineNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPaperSource;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEdnProps;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnProps;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtrRef;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSectPrImpl extends XmlComplexContentImpl implements CTSectPr
{
    private static final long serialVersionUID = 1L;
    private static final QName HEADERREFERENCE$0;
    private static final QName FOOTERREFERENCE$2;
    private static final QName FOOTNOTEPR$4;
    private static final QName ENDNOTEPR$6;
    private static final QName TYPE$8;
    private static final QName PGSZ$10;
    private static final QName PGMAR$12;
    private static final QName PAPERSRC$14;
    private static final QName PGBORDERS$16;
    private static final QName LNNUMTYPE$18;
    private static final QName PGNUMTYPE$20;
    private static final QName COLS$22;
    private static final QName FORMPROT$24;
    private static final QName VALIGN$26;
    private static final QName NOENDNOTE$28;
    private static final QName TITLEPG$30;
    private static final QName TEXTDIRECTION$32;
    private static final QName BIDI$34;
    private static final QName RTLGUTTER$36;
    private static final QName DOCGRID$38;
    private static final QName PRINTERSETTINGS$40;
    private static final QName SECTPRCHANGE$42;
    private static final QName RSIDRPR$44;
    private static final QName RSIDDEL$46;
    private static final QName RSIDR$48;
    private static final QName RSIDSECT$50;
    
    public CTSectPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTHdrFtrRef> getHeaderReferenceList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HeaderReferenceList extends AbstractList<CTHdrFtrRef>
            {
                @Override
                public CTHdrFtrRef get(final int n) {
                    return CTSectPrImpl.this.getHeaderReferenceArray(n);
                }
                
                @Override
                public CTHdrFtrRef set(final int n, final CTHdrFtrRef ctHdrFtrRef) {
                    final CTHdrFtrRef headerReferenceArray = CTSectPrImpl.this.getHeaderReferenceArray(n);
                    CTSectPrImpl.this.setHeaderReferenceArray(n, ctHdrFtrRef);
                    return headerReferenceArray;
                }
                
                @Override
                public void add(final int n, final CTHdrFtrRef ctHdrFtrRef) {
                    CTSectPrImpl.this.insertNewHeaderReference(n).set((XmlObject)ctHdrFtrRef);
                }
                
                @Override
                public CTHdrFtrRef remove(final int n) {
                    final CTHdrFtrRef headerReferenceArray = CTSectPrImpl.this.getHeaderReferenceArray(n);
                    CTSectPrImpl.this.removeHeaderReference(n);
                    return headerReferenceArray;
                }
                
                @Override
                public int size() {
                    return CTSectPrImpl.this.sizeOfHeaderReferenceArray();
                }
            }
            return new HeaderReferenceList();
        }
    }
    
    @Deprecated
    public CTHdrFtrRef[] getHeaderReferenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSectPrImpl.HEADERREFERENCE$0, (List)list);
            final CTHdrFtrRef[] array = new CTHdrFtrRef[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTHdrFtrRef getHeaderReferenceArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHdrFtrRef ctHdrFtrRef = (CTHdrFtrRef)this.get_store().find_element_user(CTSectPrImpl.HEADERREFERENCE$0, n);
            if (ctHdrFtrRef == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctHdrFtrRef;
        }
    }
    
    public int sizeOfHeaderReferenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.HEADERREFERENCE$0);
        }
    }
    
    public void setHeaderReferenceArray(final CTHdrFtrRef[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSectPrImpl.HEADERREFERENCE$0);
    }
    
    public void setHeaderReferenceArray(final int n, final CTHdrFtrRef ctHdrFtrRef) {
        this.generatedSetterHelperImpl((XmlObject)ctHdrFtrRef, CTSectPrImpl.HEADERREFERENCE$0, n, (short)2);
    }
    
    public CTHdrFtrRef insertNewHeaderReference(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHdrFtrRef)this.get_store().insert_element_user(CTSectPrImpl.HEADERREFERENCE$0, n);
        }
    }
    
    public CTHdrFtrRef addNewHeaderReference() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHdrFtrRef)this.get_store().add_element_user(CTSectPrImpl.HEADERREFERENCE$0);
        }
    }
    
    public void removeHeaderReference(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.HEADERREFERENCE$0, n);
        }
    }
    
    public List<CTHdrFtrRef> getFooterReferenceList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FooterReferenceList extends AbstractList<CTHdrFtrRef>
            {
                @Override
                public CTHdrFtrRef get(final int n) {
                    return CTSectPrImpl.this.getFooterReferenceArray(n);
                }
                
                @Override
                public CTHdrFtrRef set(final int n, final CTHdrFtrRef ctHdrFtrRef) {
                    final CTHdrFtrRef footerReferenceArray = CTSectPrImpl.this.getFooterReferenceArray(n);
                    CTSectPrImpl.this.setFooterReferenceArray(n, ctHdrFtrRef);
                    return footerReferenceArray;
                }
                
                @Override
                public void add(final int n, final CTHdrFtrRef ctHdrFtrRef) {
                    CTSectPrImpl.this.insertNewFooterReference(n).set((XmlObject)ctHdrFtrRef);
                }
                
                @Override
                public CTHdrFtrRef remove(final int n) {
                    final CTHdrFtrRef footerReferenceArray = CTSectPrImpl.this.getFooterReferenceArray(n);
                    CTSectPrImpl.this.removeFooterReference(n);
                    return footerReferenceArray;
                }
                
                @Override
                public int size() {
                    return CTSectPrImpl.this.sizeOfFooterReferenceArray();
                }
            }
            return new FooterReferenceList();
        }
    }
    
    @Deprecated
    public CTHdrFtrRef[] getFooterReferenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSectPrImpl.FOOTERREFERENCE$2, (List)list);
            final CTHdrFtrRef[] array = new CTHdrFtrRef[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTHdrFtrRef getFooterReferenceArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHdrFtrRef ctHdrFtrRef = (CTHdrFtrRef)this.get_store().find_element_user(CTSectPrImpl.FOOTERREFERENCE$2, n);
            if (ctHdrFtrRef == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctHdrFtrRef;
        }
    }
    
    public int sizeOfFooterReferenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.FOOTERREFERENCE$2);
        }
    }
    
    public void setFooterReferenceArray(final CTHdrFtrRef[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSectPrImpl.FOOTERREFERENCE$2);
    }
    
    public void setFooterReferenceArray(final int n, final CTHdrFtrRef ctHdrFtrRef) {
        this.generatedSetterHelperImpl((XmlObject)ctHdrFtrRef, CTSectPrImpl.FOOTERREFERENCE$2, n, (short)2);
    }
    
    public CTHdrFtrRef insertNewFooterReference(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHdrFtrRef)this.get_store().insert_element_user(CTSectPrImpl.FOOTERREFERENCE$2, n);
        }
    }
    
    public CTHdrFtrRef addNewFooterReference() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHdrFtrRef)this.get_store().add_element_user(CTSectPrImpl.FOOTERREFERENCE$2);
        }
    }
    
    public void removeFooterReference(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.FOOTERREFERENCE$2, n);
        }
    }
    
    public CTFtnProps getFootnotePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFtnProps ctFtnProps = (CTFtnProps)this.get_store().find_element_user(CTSectPrImpl.FOOTNOTEPR$4, 0);
            if (ctFtnProps == null) {
                return null;
            }
            return ctFtnProps;
        }
    }
    
    public boolean isSetFootnotePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.FOOTNOTEPR$4) != 0;
        }
    }
    
    public void setFootnotePr(final CTFtnProps ctFtnProps) {
        this.generatedSetterHelperImpl((XmlObject)ctFtnProps, CTSectPrImpl.FOOTNOTEPR$4, 0, (short)1);
    }
    
    public CTFtnProps addNewFootnotePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFtnProps)this.get_store().add_element_user(CTSectPrImpl.FOOTNOTEPR$4);
        }
    }
    
    public void unsetFootnotePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.FOOTNOTEPR$4, 0);
        }
    }
    
    public CTEdnProps getEndnotePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEdnProps ctEdnProps = (CTEdnProps)this.get_store().find_element_user(CTSectPrImpl.ENDNOTEPR$6, 0);
            if (ctEdnProps == null) {
                return null;
            }
            return ctEdnProps;
        }
    }
    
    public boolean isSetEndnotePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.ENDNOTEPR$6) != 0;
        }
    }
    
    public void setEndnotePr(final CTEdnProps ctEdnProps) {
        this.generatedSetterHelperImpl((XmlObject)ctEdnProps, CTSectPrImpl.ENDNOTEPR$6, 0, (short)1);
    }
    
    public CTEdnProps addNewEndnotePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEdnProps)this.get_store().add_element_user(CTSectPrImpl.ENDNOTEPR$6);
        }
    }
    
    public void unsetEndnotePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.ENDNOTEPR$6, 0);
        }
    }
    
    public CTSectType getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSectType ctSectType = (CTSectType)this.get_store().find_element_user(CTSectPrImpl.TYPE$8, 0);
            if (ctSectType == null) {
                return null;
            }
            return ctSectType;
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.TYPE$8) != 0;
        }
    }
    
    public void setType(final CTSectType ctSectType) {
        this.generatedSetterHelperImpl((XmlObject)ctSectType, CTSectPrImpl.TYPE$8, 0, (short)1);
    }
    
    public CTSectType addNewType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSectType)this.get_store().add_element_user(CTSectPrImpl.TYPE$8);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.TYPE$8, 0);
        }
    }
    
    public CTPageSz getPgSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPageSz ctPageSz = (CTPageSz)this.get_store().find_element_user(CTSectPrImpl.PGSZ$10, 0);
            if (ctPageSz == null) {
                return null;
            }
            return ctPageSz;
        }
    }
    
    public boolean isSetPgSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.PGSZ$10) != 0;
        }
    }
    
    public void setPgSz(final CTPageSz ctPageSz) {
        this.generatedSetterHelperImpl((XmlObject)ctPageSz, CTSectPrImpl.PGSZ$10, 0, (short)1);
    }
    
    public CTPageSz addNewPgSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageSz)this.get_store().add_element_user(CTSectPrImpl.PGSZ$10);
        }
    }
    
    public void unsetPgSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.PGSZ$10, 0);
        }
    }
    
    public CTPageMar getPgMar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPageMar ctPageMar = (CTPageMar)this.get_store().find_element_user(CTSectPrImpl.PGMAR$12, 0);
            if (ctPageMar == null) {
                return null;
            }
            return ctPageMar;
        }
    }
    
    public boolean isSetPgMar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.PGMAR$12) != 0;
        }
    }
    
    public void setPgMar(final CTPageMar ctPageMar) {
        this.generatedSetterHelperImpl((XmlObject)ctPageMar, CTSectPrImpl.PGMAR$12, 0, (short)1);
    }
    
    public CTPageMar addNewPgMar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageMar)this.get_store().add_element_user(CTSectPrImpl.PGMAR$12);
        }
    }
    
    public void unsetPgMar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.PGMAR$12, 0);
        }
    }
    
    public CTPaperSource getPaperSrc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPaperSource ctPaperSource = (CTPaperSource)this.get_store().find_element_user(CTSectPrImpl.PAPERSRC$14, 0);
            if (ctPaperSource == null) {
                return null;
            }
            return ctPaperSource;
        }
    }
    
    public boolean isSetPaperSrc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.PAPERSRC$14) != 0;
        }
    }
    
    public void setPaperSrc(final CTPaperSource ctPaperSource) {
        this.generatedSetterHelperImpl((XmlObject)ctPaperSource, CTSectPrImpl.PAPERSRC$14, 0, (short)1);
    }
    
    public CTPaperSource addNewPaperSrc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPaperSource)this.get_store().add_element_user(CTSectPrImpl.PAPERSRC$14);
        }
    }
    
    public void unsetPaperSrc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.PAPERSRC$14, 0);
        }
    }
    
    public CTPageBorders getPgBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPageBorders ctPageBorders = (CTPageBorders)this.get_store().find_element_user(CTSectPrImpl.PGBORDERS$16, 0);
            if (ctPageBorders == null) {
                return null;
            }
            return ctPageBorders;
        }
    }
    
    public boolean isSetPgBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.PGBORDERS$16) != 0;
        }
    }
    
    public void setPgBorders(final CTPageBorders ctPageBorders) {
        this.generatedSetterHelperImpl((XmlObject)ctPageBorders, CTSectPrImpl.PGBORDERS$16, 0, (short)1);
    }
    
    public CTPageBorders addNewPgBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageBorders)this.get_store().add_element_user(CTSectPrImpl.PGBORDERS$16);
        }
    }
    
    public void unsetPgBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.PGBORDERS$16, 0);
        }
    }
    
    public CTLineNumber getLnNumType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineNumber ctLineNumber = (CTLineNumber)this.get_store().find_element_user(CTSectPrImpl.LNNUMTYPE$18, 0);
            if (ctLineNumber == null) {
                return null;
            }
            return ctLineNumber;
        }
    }
    
    public boolean isSetLnNumType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.LNNUMTYPE$18) != 0;
        }
    }
    
    public void setLnNumType(final CTLineNumber ctLineNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctLineNumber, CTSectPrImpl.LNNUMTYPE$18, 0, (short)1);
    }
    
    public CTLineNumber addNewLnNumType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineNumber)this.get_store().add_element_user(CTSectPrImpl.LNNUMTYPE$18);
        }
    }
    
    public void unsetLnNumType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.LNNUMTYPE$18, 0);
        }
    }
    
    public CTPageNumber getPgNumType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPageNumber ctPageNumber = (CTPageNumber)this.get_store().find_element_user(CTSectPrImpl.PGNUMTYPE$20, 0);
            if (ctPageNumber == null) {
                return null;
            }
            return ctPageNumber;
        }
    }
    
    public boolean isSetPgNumType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.PGNUMTYPE$20) != 0;
        }
    }
    
    public void setPgNumType(final CTPageNumber ctPageNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctPageNumber, CTSectPrImpl.PGNUMTYPE$20, 0, (short)1);
    }
    
    public CTPageNumber addNewPgNumType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageNumber)this.get_store().add_element_user(CTSectPrImpl.PGNUMTYPE$20);
        }
    }
    
    public void unsetPgNumType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.PGNUMTYPE$20, 0);
        }
    }
    
    public CTColumns getCols() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColumns ctColumns = (CTColumns)this.get_store().find_element_user(CTSectPrImpl.COLS$22, 0);
            if (ctColumns == null) {
                return null;
            }
            return ctColumns;
        }
    }
    
    public boolean isSetCols() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.COLS$22) != 0;
        }
    }
    
    public void setCols(final CTColumns ctColumns) {
        this.generatedSetterHelperImpl((XmlObject)ctColumns, CTSectPrImpl.COLS$22, 0, (short)1);
    }
    
    public CTColumns addNewCols() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColumns)this.get_store().add_element_user(CTSectPrImpl.COLS$22);
        }
    }
    
    public void unsetCols() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.COLS$22, 0);
        }
    }
    
    public CTOnOff getFormProt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSectPrImpl.FORMPROT$24, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetFormProt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.FORMPROT$24) != 0;
        }
    }
    
    public void setFormProt(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSectPrImpl.FORMPROT$24, 0, (short)1);
    }
    
    public CTOnOff addNewFormProt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSectPrImpl.FORMPROT$24);
        }
    }
    
    public void unsetFormProt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.FORMPROT$24, 0);
        }
    }
    
    public CTVerticalJc getVAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTVerticalJc ctVerticalJc = (CTVerticalJc)this.get_store().find_element_user(CTSectPrImpl.VALIGN$26, 0);
            if (ctVerticalJc == null) {
                return null;
            }
            return ctVerticalJc;
        }
    }
    
    public boolean isSetVAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.VALIGN$26) != 0;
        }
    }
    
    public void setVAlign(final CTVerticalJc ctVerticalJc) {
        this.generatedSetterHelperImpl((XmlObject)ctVerticalJc, CTSectPrImpl.VALIGN$26, 0, (short)1);
    }
    
    public CTVerticalJc addNewVAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVerticalJc)this.get_store().add_element_user(CTSectPrImpl.VALIGN$26);
        }
    }
    
    public void unsetVAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.VALIGN$26, 0);
        }
    }
    
    public CTOnOff getNoEndnote() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSectPrImpl.NOENDNOTE$28, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetNoEndnote() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.NOENDNOTE$28) != 0;
        }
    }
    
    public void setNoEndnote(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSectPrImpl.NOENDNOTE$28, 0, (short)1);
    }
    
    public CTOnOff addNewNoEndnote() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSectPrImpl.NOENDNOTE$28);
        }
    }
    
    public void unsetNoEndnote() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.NOENDNOTE$28, 0);
        }
    }
    
    public CTOnOff getTitlePg() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSectPrImpl.TITLEPG$30, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetTitlePg() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.TITLEPG$30) != 0;
        }
    }
    
    public void setTitlePg(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSectPrImpl.TITLEPG$30, 0, (short)1);
    }
    
    public CTOnOff addNewTitlePg() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSectPrImpl.TITLEPG$30);
        }
    }
    
    public void unsetTitlePg() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.TITLEPG$30, 0);
        }
    }
    
    public CTTextDirection getTextDirection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextDirection ctTextDirection = (CTTextDirection)this.get_store().find_element_user(CTSectPrImpl.TEXTDIRECTION$32, 0);
            if (ctTextDirection == null) {
                return null;
            }
            return ctTextDirection;
        }
    }
    
    public boolean isSetTextDirection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.TEXTDIRECTION$32) != 0;
        }
    }
    
    public void setTextDirection(final CTTextDirection ctTextDirection) {
        this.generatedSetterHelperImpl((XmlObject)ctTextDirection, CTSectPrImpl.TEXTDIRECTION$32, 0, (short)1);
    }
    
    public CTTextDirection addNewTextDirection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextDirection)this.get_store().add_element_user(CTSectPrImpl.TEXTDIRECTION$32);
        }
    }
    
    public void unsetTextDirection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.TEXTDIRECTION$32, 0);
        }
    }
    
    public CTOnOff getBidi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSectPrImpl.BIDI$34, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetBidi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.BIDI$34) != 0;
        }
    }
    
    public void setBidi(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSectPrImpl.BIDI$34, 0, (short)1);
    }
    
    public CTOnOff addNewBidi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSectPrImpl.BIDI$34);
        }
    }
    
    public void unsetBidi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.BIDI$34, 0);
        }
    }
    
    public CTOnOff getRtlGutter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSectPrImpl.RTLGUTTER$36, 0);
            if (ctOnOff == null) {
                return null;
            }
            return ctOnOff;
        }
    }
    
    public boolean isSetRtlGutter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.RTLGUTTER$36) != 0;
        }
    }
    
    public void setRtlGutter(final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSectPrImpl.RTLGUTTER$36, 0, (short)1);
    }
    
    public CTOnOff addNewRtlGutter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSectPrImpl.RTLGUTTER$36);
        }
    }
    
    public void unsetRtlGutter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.RTLGUTTER$36, 0);
        }
    }
    
    public CTDocGrid getDocGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDocGrid ctDocGrid = (CTDocGrid)this.get_store().find_element_user(CTSectPrImpl.DOCGRID$38, 0);
            if (ctDocGrid == null) {
                return null;
            }
            return ctDocGrid;
        }
    }
    
    public boolean isSetDocGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.DOCGRID$38) != 0;
        }
    }
    
    public void setDocGrid(final CTDocGrid ctDocGrid) {
        this.generatedSetterHelperImpl((XmlObject)ctDocGrid, CTSectPrImpl.DOCGRID$38, 0, (short)1);
    }
    
    public CTDocGrid addNewDocGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDocGrid)this.get_store().add_element_user(CTSectPrImpl.DOCGRID$38);
        }
    }
    
    public void unsetDocGrid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.DOCGRID$38, 0);
        }
    }
    
    public CTRel getPrinterSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRel ctRel = (CTRel)this.get_store().find_element_user(CTSectPrImpl.PRINTERSETTINGS$40, 0);
            if (ctRel == null) {
                return null;
            }
            return ctRel;
        }
    }
    
    public boolean isSetPrinterSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.PRINTERSETTINGS$40) != 0;
        }
    }
    
    public void setPrinterSettings(final CTRel ctRel) {
        this.generatedSetterHelperImpl((XmlObject)ctRel, CTSectPrImpl.PRINTERSETTINGS$40, 0, (short)1);
    }
    
    public CTRel addNewPrinterSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRel)this.get_store().add_element_user(CTSectPrImpl.PRINTERSETTINGS$40);
        }
    }
    
    public void unsetPrinterSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.PRINTERSETTINGS$40, 0);
        }
    }
    
    public CTSectPrChange getSectPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSectPrChange ctSectPrChange = (CTSectPrChange)this.get_store().find_element_user(CTSectPrImpl.SECTPRCHANGE$42, 0);
            if (ctSectPrChange == null) {
                return null;
            }
            return ctSectPrChange;
        }
    }
    
    public boolean isSetSectPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSectPrImpl.SECTPRCHANGE$42) != 0;
        }
    }
    
    public void setSectPrChange(final CTSectPrChange ctSectPrChange) {
        this.generatedSetterHelperImpl((XmlObject)ctSectPrChange, CTSectPrImpl.SECTPRCHANGE$42, 0, (short)1);
    }
    
    public CTSectPrChange addNewSectPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSectPrChange)this.get_store().add_element_user(CTSectPrImpl.SECTPRCHANGE$42);
        }
    }
    
    public void unsetSectPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSectPrImpl.SECTPRCHANGE$42, 0);
        }
    }
    
    public byte[] getRsidRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSectPrImpl.RSIDRPR$44);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STLongHexNumber xgetRsidRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLongHexNumber)this.get_store().find_attribute_user(CTSectPrImpl.RSIDRPR$44);
        }
    }
    
    public boolean isSetRsidRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSectPrImpl.RSIDRPR$44) != null;
        }
    }
    
    public void setRsidRPr(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSectPrImpl.RSIDRPR$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSectPrImpl.RSIDRPR$44);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetRsidRPr(final STLongHexNumber stLongHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLongHexNumber stLongHexNumber2 = (STLongHexNumber)this.get_store().find_attribute_user(CTSectPrImpl.RSIDRPR$44);
            if (stLongHexNumber2 == null) {
                stLongHexNumber2 = (STLongHexNumber)this.get_store().add_attribute_user(CTSectPrImpl.RSIDRPR$44);
            }
            stLongHexNumber2.set((XmlObject)stLongHexNumber);
        }
    }
    
    public void unsetRsidRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSectPrImpl.RSIDRPR$44);
        }
    }
    
    public byte[] getRsidDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSectPrImpl.RSIDDEL$46);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STLongHexNumber xgetRsidDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLongHexNumber)this.get_store().find_attribute_user(CTSectPrImpl.RSIDDEL$46);
        }
    }
    
    public boolean isSetRsidDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSectPrImpl.RSIDDEL$46) != null;
        }
    }
    
    public void setRsidDel(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSectPrImpl.RSIDDEL$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSectPrImpl.RSIDDEL$46);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetRsidDel(final STLongHexNumber stLongHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLongHexNumber stLongHexNumber2 = (STLongHexNumber)this.get_store().find_attribute_user(CTSectPrImpl.RSIDDEL$46);
            if (stLongHexNumber2 == null) {
                stLongHexNumber2 = (STLongHexNumber)this.get_store().add_attribute_user(CTSectPrImpl.RSIDDEL$46);
            }
            stLongHexNumber2.set((XmlObject)stLongHexNumber);
        }
    }
    
    public void unsetRsidDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSectPrImpl.RSIDDEL$46);
        }
    }
    
    public byte[] getRsidR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSectPrImpl.RSIDR$48);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STLongHexNumber xgetRsidR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLongHexNumber)this.get_store().find_attribute_user(CTSectPrImpl.RSIDR$48);
        }
    }
    
    public boolean isSetRsidR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSectPrImpl.RSIDR$48) != null;
        }
    }
    
    public void setRsidR(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSectPrImpl.RSIDR$48);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSectPrImpl.RSIDR$48);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetRsidR(final STLongHexNumber stLongHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLongHexNumber stLongHexNumber2 = (STLongHexNumber)this.get_store().find_attribute_user(CTSectPrImpl.RSIDR$48);
            if (stLongHexNumber2 == null) {
                stLongHexNumber2 = (STLongHexNumber)this.get_store().add_attribute_user(CTSectPrImpl.RSIDR$48);
            }
            stLongHexNumber2.set((XmlObject)stLongHexNumber);
        }
    }
    
    public void unsetRsidR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSectPrImpl.RSIDR$48);
        }
    }
    
    public byte[] getRsidSect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSectPrImpl.RSIDSECT$50);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STLongHexNumber xgetRsidSect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLongHexNumber)this.get_store().find_attribute_user(CTSectPrImpl.RSIDSECT$50);
        }
    }
    
    public boolean isSetRsidSect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSectPrImpl.RSIDSECT$50) != null;
        }
    }
    
    public void setRsidSect(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSectPrImpl.RSIDSECT$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSectPrImpl.RSIDSECT$50);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetRsidSect(final STLongHexNumber stLongHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLongHexNumber stLongHexNumber2 = (STLongHexNumber)this.get_store().find_attribute_user(CTSectPrImpl.RSIDSECT$50);
            if (stLongHexNumber2 == null) {
                stLongHexNumber2 = (STLongHexNumber)this.get_store().add_attribute_user(CTSectPrImpl.RSIDSECT$50);
            }
            stLongHexNumber2.set((XmlObject)stLongHexNumber);
        }
    }
    
    public void unsetRsidSect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSectPrImpl.RSIDSECT$50);
        }
    }
    
    static {
        HEADERREFERENCE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "headerReference");
        FOOTERREFERENCE$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "footerReference");
        FOOTNOTEPR$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "footnotePr");
        ENDNOTEPR$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "endnotePr");
        TYPE$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "type");
        PGSZ$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pgSz");
        PGMAR$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pgMar");
        PAPERSRC$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "paperSrc");
        PGBORDERS$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pgBorders");
        LNNUMTYPE$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lnNumType");
        PGNUMTYPE$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pgNumType");
        COLS$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cols");
        FORMPROT$24 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "formProt");
        VALIGN$26 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "vAlign");
        NOENDNOTE$28 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "noEndnote");
        TITLEPG$30 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "titlePg");
        TEXTDIRECTION$32 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "textDirection");
        BIDI$34 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bidi");
        RTLGUTTER$36 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rtlGutter");
        DOCGRID$38 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "docGrid");
        PRINTERSETTINGS$40 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "printerSettings");
        SECTPRCHANGE$42 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sectPrChange");
        RSIDRPR$44 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsidRPr");
        RSIDDEL$46 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsidDel");
        RSIDR$48 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsidR");
        RSIDSECT$50 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsidSect");
    }
}
