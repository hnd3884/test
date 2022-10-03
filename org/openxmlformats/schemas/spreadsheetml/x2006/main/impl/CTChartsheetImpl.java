package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPublishItems;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetBackgroundPicture;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLegacyDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCsPageSetup;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageMargins;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomChartsheetViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetProtection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetViews;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetPr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheet;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTChartsheetImpl extends XmlComplexContentImpl implements CTChartsheet
{
    private static final long serialVersionUID = 1L;
    private static final QName SHEETPR$0;
    private static final QName SHEETVIEWS$2;
    private static final QName SHEETPROTECTION$4;
    private static final QName CUSTOMSHEETVIEWS$6;
    private static final QName PAGEMARGINS$8;
    private static final QName PAGESETUP$10;
    private static final QName HEADERFOOTER$12;
    private static final QName DRAWING$14;
    private static final QName LEGACYDRAWING$16;
    private static final QName LEGACYDRAWINGHF$18;
    private static final QName PICTURE$20;
    private static final QName WEBPUBLISHITEMS$22;
    private static final QName EXTLST$24;
    
    public CTChartsheetImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTChartsheetPr getSheetPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTChartsheetPr ctChartsheetPr = (CTChartsheetPr)this.get_store().find_element_user(CTChartsheetImpl.SHEETPR$0, 0);
            if (ctChartsheetPr == null) {
                return null;
            }
            return ctChartsheetPr;
        }
    }
    
    public boolean isSetSheetPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartsheetImpl.SHEETPR$0) != 0;
        }
    }
    
    public void setSheetPr(final CTChartsheetPr ctChartsheetPr) {
        this.generatedSetterHelperImpl((XmlObject)ctChartsheetPr, CTChartsheetImpl.SHEETPR$0, 0, (short)1);
    }
    
    public CTChartsheetPr addNewSheetPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTChartsheetPr)this.get_store().add_element_user(CTChartsheetImpl.SHEETPR$0);
        }
    }
    
    public void unsetSheetPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartsheetImpl.SHEETPR$0, 0);
        }
    }
    
    public CTChartsheetViews getSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTChartsheetViews ctChartsheetViews = (CTChartsheetViews)this.get_store().find_element_user(CTChartsheetImpl.SHEETVIEWS$2, 0);
            if (ctChartsheetViews == null) {
                return null;
            }
            return ctChartsheetViews;
        }
    }
    
    public void setSheetViews(final CTChartsheetViews ctChartsheetViews) {
        this.generatedSetterHelperImpl((XmlObject)ctChartsheetViews, CTChartsheetImpl.SHEETVIEWS$2, 0, (short)1);
    }
    
    public CTChartsheetViews addNewSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTChartsheetViews)this.get_store().add_element_user(CTChartsheetImpl.SHEETVIEWS$2);
        }
    }
    
    public CTChartsheetProtection getSheetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTChartsheetProtection ctChartsheetProtection = (CTChartsheetProtection)this.get_store().find_element_user(CTChartsheetImpl.SHEETPROTECTION$4, 0);
            if (ctChartsheetProtection == null) {
                return null;
            }
            return ctChartsheetProtection;
        }
    }
    
    public boolean isSetSheetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartsheetImpl.SHEETPROTECTION$4) != 0;
        }
    }
    
    public void setSheetProtection(final CTChartsheetProtection ctChartsheetProtection) {
        this.generatedSetterHelperImpl((XmlObject)ctChartsheetProtection, CTChartsheetImpl.SHEETPROTECTION$4, 0, (short)1);
    }
    
    public CTChartsheetProtection addNewSheetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTChartsheetProtection)this.get_store().add_element_user(CTChartsheetImpl.SHEETPROTECTION$4);
        }
    }
    
    public void unsetSheetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartsheetImpl.SHEETPROTECTION$4, 0);
        }
    }
    
    public CTCustomChartsheetViews getCustomSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomChartsheetViews ctCustomChartsheetViews = (CTCustomChartsheetViews)this.get_store().find_element_user(CTChartsheetImpl.CUSTOMSHEETVIEWS$6, 0);
            if (ctCustomChartsheetViews == null) {
                return null;
            }
            return ctCustomChartsheetViews;
        }
    }
    
    public boolean isSetCustomSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartsheetImpl.CUSTOMSHEETVIEWS$6) != 0;
        }
    }
    
    public void setCustomSheetViews(final CTCustomChartsheetViews ctCustomChartsheetViews) {
        this.generatedSetterHelperImpl((XmlObject)ctCustomChartsheetViews, CTChartsheetImpl.CUSTOMSHEETVIEWS$6, 0, (short)1);
    }
    
    public CTCustomChartsheetViews addNewCustomSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomChartsheetViews)this.get_store().add_element_user(CTChartsheetImpl.CUSTOMSHEETVIEWS$6);
        }
    }
    
    public void unsetCustomSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartsheetImpl.CUSTOMSHEETVIEWS$6, 0);
        }
    }
    
    public CTPageMargins getPageMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPageMargins ctPageMargins = (CTPageMargins)this.get_store().find_element_user(CTChartsheetImpl.PAGEMARGINS$8, 0);
            if (ctPageMargins == null) {
                return null;
            }
            return ctPageMargins;
        }
    }
    
    public boolean isSetPageMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartsheetImpl.PAGEMARGINS$8) != 0;
        }
    }
    
    public void setPageMargins(final CTPageMargins ctPageMargins) {
        this.generatedSetterHelperImpl((XmlObject)ctPageMargins, CTChartsheetImpl.PAGEMARGINS$8, 0, (short)1);
    }
    
    public CTPageMargins addNewPageMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageMargins)this.get_store().add_element_user(CTChartsheetImpl.PAGEMARGINS$8);
        }
    }
    
    public void unsetPageMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartsheetImpl.PAGEMARGINS$8, 0);
        }
    }
    
    public CTCsPageSetup getPageSetup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCsPageSetup ctCsPageSetup = (CTCsPageSetup)this.get_store().find_element_user(CTChartsheetImpl.PAGESETUP$10, 0);
            if (ctCsPageSetup == null) {
                return null;
            }
            return ctCsPageSetup;
        }
    }
    
    public boolean isSetPageSetup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartsheetImpl.PAGESETUP$10) != 0;
        }
    }
    
    public void setPageSetup(final CTCsPageSetup ctCsPageSetup) {
        this.generatedSetterHelperImpl((XmlObject)ctCsPageSetup, CTChartsheetImpl.PAGESETUP$10, 0, (short)1);
    }
    
    public CTCsPageSetup addNewPageSetup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCsPageSetup)this.get_store().add_element_user(CTChartsheetImpl.PAGESETUP$10);
        }
    }
    
    public void unsetPageSetup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartsheetImpl.PAGESETUP$10, 0);
        }
    }
    
    public CTHeaderFooter getHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHeaderFooter ctHeaderFooter = (CTHeaderFooter)this.get_store().find_element_user(CTChartsheetImpl.HEADERFOOTER$12, 0);
            if (ctHeaderFooter == null) {
                return null;
            }
            return ctHeaderFooter;
        }
    }
    
    public boolean isSetHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartsheetImpl.HEADERFOOTER$12) != 0;
        }
    }
    
    public void setHeaderFooter(final CTHeaderFooter ctHeaderFooter) {
        this.generatedSetterHelperImpl((XmlObject)ctHeaderFooter, CTChartsheetImpl.HEADERFOOTER$12, 0, (short)1);
    }
    
    public CTHeaderFooter addNewHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHeaderFooter)this.get_store().add_element_user(CTChartsheetImpl.HEADERFOOTER$12);
        }
    }
    
    public void unsetHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartsheetImpl.HEADERFOOTER$12, 0);
        }
    }
    
    public CTDrawing getDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDrawing ctDrawing = (CTDrawing)this.get_store().find_element_user(CTChartsheetImpl.DRAWING$14, 0);
            if (ctDrawing == null) {
                return null;
            }
            return ctDrawing;
        }
    }
    
    public void setDrawing(final CTDrawing ctDrawing) {
        this.generatedSetterHelperImpl((XmlObject)ctDrawing, CTChartsheetImpl.DRAWING$14, 0, (short)1);
    }
    
    public CTDrawing addNewDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDrawing)this.get_store().add_element_user(CTChartsheetImpl.DRAWING$14);
        }
    }
    
    public CTLegacyDrawing getLegacyDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLegacyDrawing ctLegacyDrawing = (CTLegacyDrawing)this.get_store().find_element_user(CTChartsheetImpl.LEGACYDRAWING$16, 0);
            if (ctLegacyDrawing == null) {
                return null;
            }
            return ctLegacyDrawing;
        }
    }
    
    public boolean isSetLegacyDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartsheetImpl.LEGACYDRAWING$16) != 0;
        }
    }
    
    public void setLegacyDrawing(final CTLegacyDrawing ctLegacyDrawing) {
        this.generatedSetterHelperImpl((XmlObject)ctLegacyDrawing, CTChartsheetImpl.LEGACYDRAWING$16, 0, (short)1);
    }
    
    public CTLegacyDrawing addNewLegacyDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLegacyDrawing)this.get_store().add_element_user(CTChartsheetImpl.LEGACYDRAWING$16);
        }
    }
    
    public void unsetLegacyDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartsheetImpl.LEGACYDRAWING$16, 0);
        }
    }
    
    public CTLegacyDrawing getLegacyDrawingHF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLegacyDrawing ctLegacyDrawing = (CTLegacyDrawing)this.get_store().find_element_user(CTChartsheetImpl.LEGACYDRAWINGHF$18, 0);
            if (ctLegacyDrawing == null) {
                return null;
            }
            return ctLegacyDrawing;
        }
    }
    
    public boolean isSetLegacyDrawingHF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartsheetImpl.LEGACYDRAWINGHF$18) != 0;
        }
    }
    
    public void setLegacyDrawingHF(final CTLegacyDrawing ctLegacyDrawing) {
        this.generatedSetterHelperImpl((XmlObject)ctLegacyDrawing, CTChartsheetImpl.LEGACYDRAWINGHF$18, 0, (short)1);
    }
    
    public CTLegacyDrawing addNewLegacyDrawingHF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLegacyDrawing)this.get_store().add_element_user(CTChartsheetImpl.LEGACYDRAWINGHF$18);
        }
    }
    
    public void unsetLegacyDrawingHF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartsheetImpl.LEGACYDRAWINGHF$18, 0);
        }
    }
    
    public CTSheetBackgroundPicture getPicture() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSheetBackgroundPicture ctSheetBackgroundPicture = (CTSheetBackgroundPicture)this.get_store().find_element_user(CTChartsheetImpl.PICTURE$20, 0);
            if (ctSheetBackgroundPicture == null) {
                return null;
            }
            return ctSheetBackgroundPicture;
        }
    }
    
    public boolean isSetPicture() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartsheetImpl.PICTURE$20) != 0;
        }
    }
    
    public void setPicture(final CTSheetBackgroundPicture ctSheetBackgroundPicture) {
        this.generatedSetterHelperImpl((XmlObject)ctSheetBackgroundPicture, CTChartsheetImpl.PICTURE$20, 0, (short)1);
    }
    
    public CTSheetBackgroundPicture addNewPicture() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheetBackgroundPicture)this.get_store().add_element_user(CTChartsheetImpl.PICTURE$20);
        }
    }
    
    public void unsetPicture() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartsheetImpl.PICTURE$20, 0);
        }
    }
    
    public CTWebPublishItems getWebPublishItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWebPublishItems ctWebPublishItems = (CTWebPublishItems)this.get_store().find_element_user(CTChartsheetImpl.WEBPUBLISHITEMS$22, 0);
            if (ctWebPublishItems == null) {
                return null;
            }
            return ctWebPublishItems;
        }
    }
    
    public boolean isSetWebPublishItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartsheetImpl.WEBPUBLISHITEMS$22) != 0;
        }
    }
    
    public void setWebPublishItems(final CTWebPublishItems ctWebPublishItems) {
        this.generatedSetterHelperImpl((XmlObject)ctWebPublishItems, CTChartsheetImpl.WEBPUBLISHITEMS$22, 0, (short)1);
    }
    
    public CTWebPublishItems addNewWebPublishItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWebPublishItems)this.get_store().add_element_user(CTChartsheetImpl.WEBPUBLISHITEMS$22);
        }
    }
    
    public void unsetWebPublishItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartsheetImpl.WEBPUBLISHITEMS$22, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTChartsheetImpl.EXTLST$24, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartsheetImpl.EXTLST$24) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTChartsheetImpl.EXTLST$24, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTChartsheetImpl.EXTLST$24);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartsheetImpl.EXTLST$24, 0);
        }
    }
    
    static {
        SHEETPR$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetPr");
        SHEETVIEWS$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetViews");
        SHEETPROTECTION$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetProtection");
        CUSTOMSHEETVIEWS$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "customSheetViews");
        PAGEMARGINS$8 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pageMargins");
        PAGESETUP$10 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pageSetup");
        HEADERFOOTER$12 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "headerFooter");
        DRAWING$14 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "drawing");
        LEGACYDRAWING$16 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "legacyDrawing");
        LEGACYDRAWINGHF$18 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "legacyDrawingHF");
        PICTURE$20 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "picture");
        WEBPUBLISHITEMS$22 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "webPublishItems");
        EXTLST$24 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
    }
}
