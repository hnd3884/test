package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObjects;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLegacyDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageSetup;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageMargins;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPrintOptions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomSheetViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetProtection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetFormatPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetViews;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetPr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDialogsheet;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDialogsheetImpl extends XmlComplexContentImpl implements CTDialogsheet
{
    private static final long serialVersionUID = 1L;
    private static final QName SHEETPR$0;
    private static final QName SHEETVIEWS$2;
    private static final QName SHEETFORMATPR$4;
    private static final QName SHEETPROTECTION$6;
    private static final QName CUSTOMSHEETVIEWS$8;
    private static final QName PRINTOPTIONS$10;
    private static final QName PAGEMARGINS$12;
    private static final QName PAGESETUP$14;
    private static final QName HEADERFOOTER$16;
    private static final QName DRAWING$18;
    private static final QName LEGACYDRAWING$20;
    private static final QName LEGACYDRAWINGHF$22;
    private static final QName OLEOBJECTS$24;
    private static final QName EXTLST$26;
    
    public CTDialogsheetImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTSheetPr getSheetPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSheetPr ctSheetPr = (CTSheetPr)this.get_store().find_element_user(CTDialogsheetImpl.SHEETPR$0, 0);
            if (ctSheetPr == null) {
                return null;
            }
            return ctSheetPr;
        }
    }
    
    public boolean isSetSheetPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDialogsheetImpl.SHEETPR$0) != 0;
        }
    }
    
    public void setSheetPr(final CTSheetPr ctSheetPr) {
        this.generatedSetterHelperImpl((XmlObject)ctSheetPr, CTDialogsheetImpl.SHEETPR$0, 0, (short)1);
    }
    
    public CTSheetPr addNewSheetPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheetPr)this.get_store().add_element_user(CTDialogsheetImpl.SHEETPR$0);
        }
    }
    
    public void unsetSheetPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDialogsheetImpl.SHEETPR$0, 0);
        }
    }
    
    public CTSheetViews getSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSheetViews ctSheetViews = (CTSheetViews)this.get_store().find_element_user(CTDialogsheetImpl.SHEETVIEWS$2, 0);
            if (ctSheetViews == null) {
                return null;
            }
            return ctSheetViews;
        }
    }
    
    public boolean isSetSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDialogsheetImpl.SHEETVIEWS$2) != 0;
        }
    }
    
    public void setSheetViews(final CTSheetViews ctSheetViews) {
        this.generatedSetterHelperImpl((XmlObject)ctSheetViews, CTDialogsheetImpl.SHEETVIEWS$2, 0, (short)1);
    }
    
    public CTSheetViews addNewSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheetViews)this.get_store().add_element_user(CTDialogsheetImpl.SHEETVIEWS$2);
        }
    }
    
    public void unsetSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDialogsheetImpl.SHEETVIEWS$2, 0);
        }
    }
    
    public CTSheetFormatPr getSheetFormatPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSheetFormatPr ctSheetFormatPr = (CTSheetFormatPr)this.get_store().find_element_user(CTDialogsheetImpl.SHEETFORMATPR$4, 0);
            if (ctSheetFormatPr == null) {
                return null;
            }
            return ctSheetFormatPr;
        }
    }
    
    public boolean isSetSheetFormatPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDialogsheetImpl.SHEETFORMATPR$4) != 0;
        }
    }
    
    public void setSheetFormatPr(final CTSheetFormatPr ctSheetFormatPr) {
        this.generatedSetterHelperImpl((XmlObject)ctSheetFormatPr, CTDialogsheetImpl.SHEETFORMATPR$4, 0, (short)1);
    }
    
    public CTSheetFormatPr addNewSheetFormatPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheetFormatPr)this.get_store().add_element_user(CTDialogsheetImpl.SHEETFORMATPR$4);
        }
    }
    
    public void unsetSheetFormatPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDialogsheetImpl.SHEETFORMATPR$4, 0);
        }
    }
    
    public CTSheetProtection getSheetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSheetProtection ctSheetProtection = (CTSheetProtection)this.get_store().find_element_user(CTDialogsheetImpl.SHEETPROTECTION$6, 0);
            if (ctSheetProtection == null) {
                return null;
            }
            return ctSheetProtection;
        }
    }
    
    public boolean isSetSheetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDialogsheetImpl.SHEETPROTECTION$6) != 0;
        }
    }
    
    public void setSheetProtection(final CTSheetProtection ctSheetProtection) {
        this.generatedSetterHelperImpl((XmlObject)ctSheetProtection, CTDialogsheetImpl.SHEETPROTECTION$6, 0, (short)1);
    }
    
    public CTSheetProtection addNewSheetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheetProtection)this.get_store().add_element_user(CTDialogsheetImpl.SHEETPROTECTION$6);
        }
    }
    
    public void unsetSheetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDialogsheetImpl.SHEETPROTECTION$6, 0);
        }
    }
    
    public CTCustomSheetViews getCustomSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomSheetViews ctCustomSheetViews = (CTCustomSheetViews)this.get_store().find_element_user(CTDialogsheetImpl.CUSTOMSHEETVIEWS$8, 0);
            if (ctCustomSheetViews == null) {
                return null;
            }
            return ctCustomSheetViews;
        }
    }
    
    public boolean isSetCustomSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDialogsheetImpl.CUSTOMSHEETVIEWS$8) != 0;
        }
    }
    
    public void setCustomSheetViews(final CTCustomSheetViews ctCustomSheetViews) {
        this.generatedSetterHelperImpl((XmlObject)ctCustomSheetViews, CTDialogsheetImpl.CUSTOMSHEETVIEWS$8, 0, (short)1);
    }
    
    public CTCustomSheetViews addNewCustomSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomSheetViews)this.get_store().add_element_user(CTDialogsheetImpl.CUSTOMSHEETVIEWS$8);
        }
    }
    
    public void unsetCustomSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDialogsheetImpl.CUSTOMSHEETVIEWS$8, 0);
        }
    }
    
    public CTPrintOptions getPrintOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPrintOptions ctPrintOptions = (CTPrintOptions)this.get_store().find_element_user(CTDialogsheetImpl.PRINTOPTIONS$10, 0);
            if (ctPrintOptions == null) {
                return null;
            }
            return ctPrintOptions;
        }
    }
    
    public boolean isSetPrintOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDialogsheetImpl.PRINTOPTIONS$10) != 0;
        }
    }
    
    public void setPrintOptions(final CTPrintOptions ctPrintOptions) {
        this.generatedSetterHelperImpl((XmlObject)ctPrintOptions, CTDialogsheetImpl.PRINTOPTIONS$10, 0, (short)1);
    }
    
    public CTPrintOptions addNewPrintOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPrintOptions)this.get_store().add_element_user(CTDialogsheetImpl.PRINTOPTIONS$10);
        }
    }
    
    public void unsetPrintOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDialogsheetImpl.PRINTOPTIONS$10, 0);
        }
    }
    
    public CTPageMargins getPageMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPageMargins ctPageMargins = (CTPageMargins)this.get_store().find_element_user(CTDialogsheetImpl.PAGEMARGINS$12, 0);
            if (ctPageMargins == null) {
                return null;
            }
            return ctPageMargins;
        }
    }
    
    public boolean isSetPageMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDialogsheetImpl.PAGEMARGINS$12) != 0;
        }
    }
    
    public void setPageMargins(final CTPageMargins ctPageMargins) {
        this.generatedSetterHelperImpl((XmlObject)ctPageMargins, CTDialogsheetImpl.PAGEMARGINS$12, 0, (short)1);
    }
    
    public CTPageMargins addNewPageMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageMargins)this.get_store().add_element_user(CTDialogsheetImpl.PAGEMARGINS$12);
        }
    }
    
    public void unsetPageMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDialogsheetImpl.PAGEMARGINS$12, 0);
        }
    }
    
    public CTPageSetup getPageSetup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPageSetup ctPageSetup = (CTPageSetup)this.get_store().find_element_user(CTDialogsheetImpl.PAGESETUP$14, 0);
            if (ctPageSetup == null) {
                return null;
            }
            return ctPageSetup;
        }
    }
    
    public boolean isSetPageSetup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDialogsheetImpl.PAGESETUP$14) != 0;
        }
    }
    
    public void setPageSetup(final CTPageSetup ctPageSetup) {
        this.generatedSetterHelperImpl((XmlObject)ctPageSetup, CTDialogsheetImpl.PAGESETUP$14, 0, (short)1);
    }
    
    public CTPageSetup addNewPageSetup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageSetup)this.get_store().add_element_user(CTDialogsheetImpl.PAGESETUP$14);
        }
    }
    
    public void unsetPageSetup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDialogsheetImpl.PAGESETUP$14, 0);
        }
    }
    
    public CTHeaderFooter getHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHeaderFooter ctHeaderFooter = (CTHeaderFooter)this.get_store().find_element_user(CTDialogsheetImpl.HEADERFOOTER$16, 0);
            if (ctHeaderFooter == null) {
                return null;
            }
            return ctHeaderFooter;
        }
    }
    
    public boolean isSetHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDialogsheetImpl.HEADERFOOTER$16) != 0;
        }
    }
    
    public void setHeaderFooter(final CTHeaderFooter ctHeaderFooter) {
        this.generatedSetterHelperImpl((XmlObject)ctHeaderFooter, CTDialogsheetImpl.HEADERFOOTER$16, 0, (short)1);
    }
    
    public CTHeaderFooter addNewHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHeaderFooter)this.get_store().add_element_user(CTDialogsheetImpl.HEADERFOOTER$16);
        }
    }
    
    public void unsetHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDialogsheetImpl.HEADERFOOTER$16, 0);
        }
    }
    
    public CTDrawing getDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDrawing ctDrawing = (CTDrawing)this.get_store().find_element_user(CTDialogsheetImpl.DRAWING$18, 0);
            if (ctDrawing == null) {
                return null;
            }
            return ctDrawing;
        }
    }
    
    public boolean isSetDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDialogsheetImpl.DRAWING$18) != 0;
        }
    }
    
    public void setDrawing(final CTDrawing ctDrawing) {
        this.generatedSetterHelperImpl((XmlObject)ctDrawing, CTDialogsheetImpl.DRAWING$18, 0, (short)1);
    }
    
    public CTDrawing addNewDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDrawing)this.get_store().add_element_user(CTDialogsheetImpl.DRAWING$18);
        }
    }
    
    public void unsetDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDialogsheetImpl.DRAWING$18, 0);
        }
    }
    
    public CTLegacyDrawing getLegacyDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLegacyDrawing ctLegacyDrawing = (CTLegacyDrawing)this.get_store().find_element_user(CTDialogsheetImpl.LEGACYDRAWING$20, 0);
            if (ctLegacyDrawing == null) {
                return null;
            }
            return ctLegacyDrawing;
        }
    }
    
    public boolean isSetLegacyDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDialogsheetImpl.LEGACYDRAWING$20) != 0;
        }
    }
    
    public void setLegacyDrawing(final CTLegacyDrawing ctLegacyDrawing) {
        this.generatedSetterHelperImpl((XmlObject)ctLegacyDrawing, CTDialogsheetImpl.LEGACYDRAWING$20, 0, (short)1);
    }
    
    public CTLegacyDrawing addNewLegacyDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLegacyDrawing)this.get_store().add_element_user(CTDialogsheetImpl.LEGACYDRAWING$20);
        }
    }
    
    public void unsetLegacyDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDialogsheetImpl.LEGACYDRAWING$20, 0);
        }
    }
    
    public CTLegacyDrawing getLegacyDrawingHF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLegacyDrawing ctLegacyDrawing = (CTLegacyDrawing)this.get_store().find_element_user(CTDialogsheetImpl.LEGACYDRAWINGHF$22, 0);
            if (ctLegacyDrawing == null) {
                return null;
            }
            return ctLegacyDrawing;
        }
    }
    
    public boolean isSetLegacyDrawingHF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDialogsheetImpl.LEGACYDRAWINGHF$22) != 0;
        }
    }
    
    public void setLegacyDrawingHF(final CTLegacyDrawing ctLegacyDrawing) {
        this.generatedSetterHelperImpl((XmlObject)ctLegacyDrawing, CTDialogsheetImpl.LEGACYDRAWINGHF$22, 0, (short)1);
    }
    
    public CTLegacyDrawing addNewLegacyDrawingHF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLegacyDrawing)this.get_store().add_element_user(CTDialogsheetImpl.LEGACYDRAWINGHF$22);
        }
    }
    
    public void unsetLegacyDrawingHF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDialogsheetImpl.LEGACYDRAWINGHF$22, 0);
        }
    }
    
    public CTOleObjects getOleObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOleObjects ctOleObjects = (CTOleObjects)this.get_store().find_element_user(CTDialogsheetImpl.OLEOBJECTS$24, 0);
            if (ctOleObjects == null) {
                return null;
            }
            return ctOleObjects;
        }
    }
    
    public boolean isSetOleObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDialogsheetImpl.OLEOBJECTS$24) != 0;
        }
    }
    
    public void setOleObjects(final CTOleObjects ctOleObjects) {
        this.generatedSetterHelperImpl((XmlObject)ctOleObjects, CTDialogsheetImpl.OLEOBJECTS$24, 0, (short)1);
    }
    
    public CTOleObjects addNewOleObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOleObjects)this.get_store().add_element_user(CTDialogsheetImpl.OLEOBJECTS$24);
        }
    }
    
    public void unsetOleObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDialogsheetImpl.OLEOBJECTS$24, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTDialogsheetImpl.EXTLST$26, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDialogsheetImpl.EXTLST$26) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTDialogsheetImpl.EXTLST$26, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTDialogsheetImpl.EXTLST$26);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDialogsheetImpl.EXTLST$26, 0);
        }
    }
    
    static {
        SHEETPR$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetPr");
        SHEETVIEWS$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetViews");
        SHEETFORMATPR$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetFormatPr");
        SHEETPROTECTION$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetProtection");
        CUSTOMSHEETVIEWS$8 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "customSheetViews");
        PRINTOPTIONS$10 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "printOptions");
        PAGEMARGINS$12 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pageMargins");
        PAGESETUP$14 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pageSetup");
        HEADERFOOTER$16 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "headerFooter");
        DRAWING$18 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "drawing");
        LEGACYDRAWING$20 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "legacyDrawing");
        LEGACYDRAWINGHF$22 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "legacyDrawingHF");
        OLEOBJECTS$24 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "oleObjects");
        EXTLST$26 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
    }
}
