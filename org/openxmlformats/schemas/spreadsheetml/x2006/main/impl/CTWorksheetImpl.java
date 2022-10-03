package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableParts;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPublishItems;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTControls;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObjects;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetBackgroundPicture;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLegacyDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSmartTags;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIgnoredErrors;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellWatches;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomProperties;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageBreak;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageSetup;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageMargins;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPrintOptions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHyperlinks;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidations;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConditionalFormatting;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPhoneticPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMergeCells;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomSheetViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataConsolidate;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSortState;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTAutoFilter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTScenarios;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTProtectedRanges;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetProtection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetCalcPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetData;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCols;
import java.util.List;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetFormatPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetDimension;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetPr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTWorksheetImpl extends XmlComplexContentImpl implements CTWorksheet
{
    private static final long serialVersionUID = 1L;
    private static final QName SHEETPR$0;
    private static final QName DIMENSION$2;
    private static final QName SHEETVIEWS$4;
    private static final QName SHEETFORMATPR$6;
    private static final QName COLS$8;
    private static final QName SHEETDATA$10;
    private static final QName SHEETCALCPR$12;
    private static final QName SHEETPROTECTION$14;
    private static final QName PROTECTEDRANGES$16;
    private static final QName SCENARIOS$18;
    private static final QName AUTOFILTER$20;
    private static final QName SORTSTATE$22;
    private static final QName DATACONSOLIDATE$24;
    private static final QName CUSTOMSHEETVIEWS$26;
    private static final QName MERGECELLS$28;
    private static final QName PHONETICPR$30;
    private static final QName CONDITIONALFORMATTING$32;
    private static final QName DATAVALIDATIONS$34;
    private static final QName HYPERLINKS$36;
    private static final QName PRINTOPTIONS$38;
    private static final QName PAGEMARGINS$40;
    private static final QName PAGESETUP$42;
    private static final QName HEADERFOOTER$44;
    private static final QName ROWBREAKS$46;
    private static final QName COLBREAKS$48;
    private static final QName CUSTOMPROPERTIES$50;
    private static final QName CELLWATCHES$52;
    private static final QName IGNOREDERRORS$54;
    private static final QName SMARTTAGS$56;
    private static final QName DRAWING$58;
    private static final QName LEGACYDRAWING$60;
    private static final QName LEGACYDRAWINGHF$62;
    private static final QName PICTURE$64;
    private static final QName OLEOBJECTS$66;
    private static final QName CONTROLS$68;
    private static final QName WEBPUBLISHITEMS$70;
    private static final QName TABLEPARTS$72;
    private static final QName EXTLST$74;
    
    public CTWorksheetImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTSheetPr getSheetPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSheetPr ctSheetPr = (CTSheetPr)this.get_store().find_element_user(CTWorksheetImpl.SHEETPR$0, 0);
            if (ctSheetPr == null) {
                return null;
            }
            return ctSheetPr;
        }
    }
    
    public boolean isSetSheetPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.SHEETPR$0) != 0;
        }
    }
    
    public void setSheetPr(final CTSheetPr ctSheetPr) {
        this.generatedSetterHelperImpl((XmlObject)ctSheetPr, CTWorksheetImpl.SHEETPR$0, 0, (short)1);
    }
    
    public CTSheetPr addNewSheetPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheetPr)this.get_store().add_element_user(CTWorksheetImpl.SHEETPR$0);
        }
    }
    
    public void unsetSheetPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.SHEETPR$0, 0);
        }
    }
    
    public CTSheetDimension getDimension() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSheetDimension ctSheetDimension = (CTSheetDimension)this.get_store().find_element_user(CTWorksheetImpl.DIMENSION$2, 0);
            if (ctSheetDimension == null) {
                return null;
            }
            return ctSheetDimension;
        }
    }
    
    public boolean isSetDimension() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.DIMENSION$2) != 0;
        }
    }
    
    public void setDimension(final CTSheetDimension ctSheetDimension) {
        this.generatedSetterHelperImpl((XmlObject)ctSheetDimension, CTWorksheetImpl.DIMENSION$2, 0, (short)1);
    }
    
    public CTSheetDimension addNewDimension() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheetDimension)this.get_store().add_element_user(CTWorksheetImpl.DIMENSION$2);
        }
    }
    
    public void unsetDimension() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.DIMENSION$2, 0);
        }
    }
    
    public CTSheetViews getSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSheetViews ctSheetViews = (CTSheetViews)this.get_store().find_element_user(CTWorksheetImpl.SHEETVIEWS$4, 0);
            if (ctSheetViews == null) {
                return null;
            }
            return ctSheetViews;
        }
    }
    
    public boolean isSetSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.SHEETVIEWS$4) != 0;
        }
    }
    
    public void setSheetViews(final CTSheetViews ctSheetViews) {
        this.generatedSetterHelperImpl((XmlObject)ctSheetViews, CTWorksheetImpl.SHEETVIEWS$4, 0, (short)1);
    }
    
    public CTSheetViews addNewSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheetViews)this.get_store().add_element_user(CTWorksheetImpl.SHEETVIEWS$4);
        }
    }
    
    public void unsetSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.SHEETVIEWS$4, 0);
        }
    }
    
    public CTSheetFormatPr getSheetFormatPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSheetFormatPr ctSheetFormatPr = (CTSheetFormatPr)this.get_store().find_element_user(CTWorksheetImpl.SHEETFORMATPR$6, 0);
            if (ctSheetFormatPr == null) {
                return null;
            }
            return ctSheetFormatPr;
        }
    }
    
    public boolean isSetSheetFormatPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.SHEETFORMATPR$6) != 0;
        }
    }
    
    public void setSheetFormatPr(final CTSheetFormatPr ctSheetFormatPr) {
        this.generatedSetterHelperImpl((XmlObject)ctSheetFormatPr, CTWorksheetImpl.SHEETFORMATPR$6, 0, (short)1);
    }
    
    public CTSheetFormatPr addNewSheetFormatPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheetFormatPr)this.get_store().add_element_user(CTWorksheetImpl.SHEETFORMATPR$6);
        }
    }
    
    public void unsetSheetFormatPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.SHEETFORMATPR$6, 0);
        }
    }
    
    public List<CTCols> getColsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ColsList extends AbstractList<CTCols>
            {
                @Override
                public CTCols get(final int n) {
                    return CTWorksheetImpl.this.getColsArray(n);
                }
                
                @Override
                public CTCols set(final int n, final CTCols ctCols) {
                    final CTCols colsArray = CTWorksheetImpl.this.getColsArray(n);
                    CTWorksheetImpl.this.setColsArray(n, ctCols);
                    return colsArray;
                }
                
                @Override
                public void add(final int n, final CTCols ctCols) {
                    CTWorksheetImpl.this.insertNewCols(n).set((XmlObject)ctCols);
                }
                
                @Override
                public CTCols remove(final int n) {
                    final CTCols colsArray = CTWorksheetImpl.this.getColsArray(n);
                    CTWorksheetImpl.this.removeCols(n);
                    return colsArray;
                }
                
                @Override
                public int size() {
                    return CTWorksheetImpl.this.sizeOfColsArray();
                }
            }
            return new ColsList();
        }
    }
    
    @Deprecated
    public CTCols[] getColsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTWorksheetImpl.COLS$8, (List)list);
            final CTCols[] array = new CTCols[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCols getColsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCols ctCols = (CTCols)this.get_store().find_element_user(CTWorksheetImpl.COLS$8, n);
            if (ctCols == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCols;
        }
    }
    
    public int sizeOfColsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.COLS$8);
        }
    }
    
    public void setColsArray(final CTCols[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTWorksheetImpl.COLS$8);
    }
    
    public void setColsArray(final int n, final CTCols ctCols) {
        this.generatedSetterHelperImpl((XmlObject)ctCols, CTWorksheetImpl.COLS$8, n, (short)2);
    }
    
    public CTCols insertNewCols(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCols)this.get_store().insert_element_user(CTWorksheetImpl.COLS$8, n);
        }
    }
    
    public CTCols addNewCols() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCols)this.get_store().add_element_user(CTWorksheetImpl.COLS$8);
        }
    }
    
    public void removeCols(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.COLS$8, n);
        }
    }
    
    public CTSheetData getSheetData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSheetData ctSheetData = (CTSheetData)this.get_store().find_element_user(CTWorksheetImpl.SHEETDATA$10, 0);
            if (ctSheetData == null) {
                return null;
            }
            return ctSheetData;
        }
    }
    
    public void setSheetData(final CTSheetData ctSheetData) {
        this.generatedSetterHelperImpl((XmlObject)ctSheetData, CTWorksheetImpl.SHEETDATA$10, 0, (short)1);
    }
    
    public CTSheetData addNewSheetData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheetData)this.get_store().add_element_user(CTWorksheetImpl.SHEETDATA$10);
        }
    }
    
    public CTSheetCalcPr getSheetCalcPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSheetCalcPr ctSheetCalcPr = (CTSheetCalcPr)this.get_store().find_element_user(CTWorksheetImpl.SHEETCALCPR$12, 0);
            if (ctSheetCalcPr == null) {
                return null;
            }
            return ctSheetCalcPr;
        }
    }
    
    public boolean isSetSheetCalcPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.SHEETCALCPR$12) != 0;
        }
    }
    
    public void setSheetCalcPr(final CTSheetCalcPr ctSheetCalcPr) {
        this.generatedSetterHelperImpl((XmlObject)ctSheetCalcPr, CTWorksheetImpl.SHEETCALCPR$12, 0, (short)1);
    }
    
    public CTSheetCalcPr addNewSheetCalcPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheetCalcPr)this.get_store().add_element_user(CTWorksheetImpl.SHEETCALCPR$12);
        }
    }
    
    public void unsetSheetCalcPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.SHEETCALCPR$12, 0);
        }
    }
    
    public CTSheetProtection getSheetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSheetProtection ctSheetProtection = (CTSheetProtection)this.get_store().find_element_user(CTWorksheetImpl.SHEETPROTECTION$14, 0);
            if (ctSheetProtection == null) {
                return null;
            }
            return ctSheetProtection;
        }
    }
    
    public boolean isSetSheetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.SHEETPROTECTION$14) != 0;
        }
    }
    
    public void setSheetProtection(final CTSheetProtection ctSheetProtection) {
        this.generatedSetterHelperImpl((XmlObject)ctSheetProtection, CTWorksheetImpl.SHEETPROTECTION$14, 0, (short)1);
    }
    
    public CTSheetProtection addNewSheetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheetProtection)this.get_store().add_element_user(CTWorksheetImpl.SHEETPROTECTION$14);
        }
    }
    
    public void unsetSheetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.SHEETPROTECTION$14, 0);
        }
    }
    
    public CTProtectedRanges getProtectedRanges() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTProtectedRanges ctProtectedRanges = (CTProtectedRanges)this.get_store().find_element_user(CTWorksheetImpl.PROTECTEDRANGES$16, 0);
            if (ctProtectedRanges == null) {
                return null;
            }
            return ctProtectedRanges;
        }
    }
    
    public boolean isSetProtectedRanges() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.PROTECTEDRANGES$16) != 0;
        }
    }
    
    public void setProtectedRanges(final CTProtectedRanges ctProtectedRanges) {
        this.generatedSetterHelperImpl((XmlObject)ctProtectedRanges, CTWorksheetImpl.PROTECTEDRANGES$16, 0, (short)1);
    }
    
    public CTProtectedRanges addNewProtectedRanges() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProtectedRanges)this.get_store().add_element_user(CTWorksheetImpl.PROTECTEDRANGES$16);
        }
    }
    
    public void unsetProtectedRanges() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.PROTECTEDRANGES$16, 0);
        }
    }
    
    public CTScenarios getScenarios() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTScenarios ctScenarios = (CTScenarios)this.get_store().find_element_user(CTWorksheetImpl.SCENARIOS$18, 0);
            if (ctScenarios == null) {
                return null;
            }
            return ctScenarios;
        }
    }
    
    public boolean isSetScenarios() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.SCENARIOS$18) != 0;
        }
    }
    
    public void setScenarios(final CTScenarios ctScenarios) {
        this.generatedSetterHelperImpl((XmlObject)ctScenarios, CTWorksheetImpl.SCENARIOS$18, 0, (short)1);
    }
    
    public CTScenarios addNewScenarios() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTScenarios)this.get_store().add_element_user(CTWorksheetImpl.SCENARIOS$18);
        }
    }
    
    public void unsetScenarios() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.SCENARIOS$18, 0);
        }
    }
    
    public CTAutoFilter getAutoFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAutoFilter ctAutoFilter = (CTAutoFilter)this.get_store().find_element_user(CTWorksheetImpl.AUTOFILTER$20, 0);
            if (ctAutoFilter == null) {
                return null;
            }
            return ctAutoFilter;
        }
    }
    
    public boolean isSetAutoFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.AUTOFILTER$20) != 0;
        }
    }
    
    public void setAutoFilter(final CTAutoFilter ctAutoFilter) {
        this.generatedSetterHelperImpl((XmlObject)ctAutoFilter, CTWorksheetImpl.AUTOFILTER$20, 0, (short)1);
    }
    
    public CTAutoFilter addNewAutoFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAutoFilter)this.get_store().add_element_user(CTWorksheetImpl.AUTOFILTER$20);
        }
    }
    
    public void unsetAutoFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.AUTOFILTER$20, 0);
        }
    }
    
    public CTSortState getSortState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSortState ctSortState = (CTSortState)this.get_store().find_element_user(CTWorksheetImpl.SORTSTATE$22, 0);
            if (ctSortState == null) {
                return null;
            }
            return ctSortState;
        }
    }
    
    public boolean isSetSortState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.SORTSTATE$22) != 0;
        }
    }
    
    public void setSortState(final CTSortState ctSortState) {
        this.generatedSetterHelperImpl((XmlObject)ctSortState, CTWorksheetImpl.SORTSTATE$22, 0, (short)1);
    }
    
    public CTSortState addNewSortState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSortState)this.get_store().add_element_user(CTWorksheetImpl.SORTSTATE$22);
        }
    }
    
    public void unsetSortState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.SORTSTATE$22, 0);
        }
    }
    
    public CTDataConsolidate getDataConsolidate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDataConsolidate ctDataConsolidate = (CTDataConsolidate)this.get_store().find_element_user(CTWorksheetImpl.DATACONSOLIDATE$24, 0);
            if (ctDataConsolidate == null) {
                return null;
            }
            return ctDataConsolidate;
        }
    }
    
    public boolean isSetDataConsolidate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.DATACONSOLIDATE$24) != 0;
        }
    }
    
    public void setDataConsolidate(final CTDataConsolidate ctDataConsolidate) {
        this.generatedSetterHelperImpl((XmlObject)ctDataConsolidate, CTWorksheetImpl.DATACONSOLIDATE$24, 0, (short)1);
    }
    
    public CTDataConsolidate addNewDataConsolidate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDataConsolidate)this.get_store().add_element_user(CTWorksheetImpl.DATACONSOLIDATE$24);
        }
    }
    
    public void unsetDataConsolidate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.DATACONSOLIDATE$24, 0);
        }
    }
    
    public CTCustomSheetViews getCustomSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomSheetViews ctCustomSheetViews = (CTCustomSheetViews)this.get_store().find_element_user(CTWorksheetImpl.CUSTOMSHEETVIEWS$26, 0);
            if (ctCustomSheetViews == null) {
                return null;
            }
            return ctCustomSheetViews;
        }
    }
    
    public boolean isSetCustomSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.CUSTOMSHEETVIEWS$26) != 0;
        }
    }
    
    public void setCustomSheetViews(final CTCustomSheetViews ctCustomSheetViews) {
        this.generatedSetterHelperImpl((XmlObject)ctCustomSheetViews, CTWorksheetImpl.CUSTOMSHEETVIEWS$26, 0, (short)1);
    }
    
    public CTCustomSheetViews addNewCustomSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomSheetViews)this.get_store().add_element_user(CTWorksheetImpl.CUSTOMSHEETVIEWS$26);
        }
    }
    
    public void unsetCustomSheetViews() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.CUSTOMSHEETVIEWS$26, 0);
        }
    }
    
    public CTMergeCells getMergeCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMergeCells ctMergeCells = (CTMergeCells)this.get_store().find_element_user(CTWorksheetImpl.MERGECELLS$28, 0);
            if (ctMergeCells == null) {
                return null;
            }
            return ctMergeCells;
        }
    }
    
    public boolean isSetMergeCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.MERGECELLS$28) != 0;
        }
    }
    
    public void setMergeCells(final CTMergeCells ctMergeCells) {
        this.generatedSetterHelperImpl((XmlObject)ctMergeCells, CTWorksheetImpl.MERGECELLS$28, 0, (short)1);
    }
    
    public CTMergeCells addNewMergeCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMergeCells)this.get_store().add_element_user(CTWorksheetImpl.MERGECELLS$28);
        }
    }
    
    public void unsetMergeCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.MERGECELLS$28, 0);
        }
    }
    
    public CTPhoneticPr getPhoneticPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPhoneticPr ctPhoneticPr = (CTPhoneticPr)this.get_store().find_element_user(CTWorksheetImpl.PHONETICPR$30, 0);
            if (ctPhoneticPr == null) {
                return null;
            }
            return ctPhoneticPr;
        }
    }
    
    public boolean isSetPhoneticPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.PHONETICPR$30) != 0;
        }
    }
    
    public void setPhoneticPr(final CTPhoneticPr ctPhoneticPr) {
        this.generatedSetterHelperImpl((XmlObject)ctPhoneticPr, CTWorksheetImpl.PHONETICPR$30, 0, (short)1);
    }
    
    public CTPhoneticPr addNewPhoneticPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPhoneticPr)this.get_store().add_element_user(CTWorksheetImpl.PHONETICPR$30);
        }
    }
    
    public void unsetPhoneticPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.PHONETICPR$30, 0);
        }
    }
    
    public List<CTConditionalFormatting> getConditionalFormattingList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ConditionalFormattingList extends AbstractList<CTConditionalFormatting>
            {
                @Override
                public CTConditionalFormatting get(final int n) {
                    return CTWorksheetImpl.this.getConditionalFormattingArray(n);
                }
                
                @Override
                public CTConditionalFormatting set(final int n, final CTConditionalFormatting ctConditionalFormatting) {
                    final CTConditionalFormatting conditionalFormattingArray = CTWorksheetImpl.this.getConditionalFormattingArray(n);
                    CTWorksheetImpl.this.setConditionalFormattingArray(n, ctConditionalFormatting);
                    return conditionalFormattingArray;
                }
                
                @Override
                public void add(final int n, final CTConditionalFormatting ctConditionalFormatting) {
                    CTWorksheetImpl.this.insertNewConditionalFormatting(n).set((XmlObject)ctConditionalFormatting);
                }
                
                @Override
                public CTConditionalFormatting remove(final int n) {
                    final CTConditionalFormatting conditionalFormattingArray = CTWorksheetImpl.this.getConditionalFormattingArray(n);
                    CTWorksheetImpl.this.removeConditionalFormatting(n);
                    return conditionalFormattingArray;
                }
                
                @Override
                public int size() {
                    return CTWorksheetImpl.this.sizeOfConditionalFormattingArray();
                }
            }
            return new ConditionalFormattingList();
        }
    }
    
    @Deprecated
    public CTConditionalFormatting[] getConditionalFormattingArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTWorksheetImpl.CONDITIONALFORMATTING$32, (List)list);
            final CTConditionalFormatting[] array = new CTConditionalFormatting[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTConditionalFormatting getConditionalFormattingArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTConditionalFormatting ctConditionalFormatting = (CTConditionalFormatting)this.get_store().find_element_user(CTWorksheetImpl.CONDITIONALFORMATTING$32, n);
            if (ctConditionalFormatting == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctConditionalFormatting;
        }
    }
    
    public int sizeOfConditionalFormattingArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.CONDITIONALFORMATTING$32);
        }
    }
    
    public void setConditionalFormattingArray(final CTConditionalFormatting[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTWorksheetImpl.CONDITIONALFORMATTING$32);
    }
    
    public void setConditionalFormattingArray(final int n, final CTConditionalFormatting ctConditionalFormatting) {
        this.generatedSetterHelperImpl((XmlObject)ctConditionalFormatting, CTWorksheetImpl.CONDITIONALFORMATTING$32, n, (short)2);
    }
    
    public CTConditionalFormatting insertNewConditionalFormatting(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTConditionalFormatting)this.get_store().insert_element_user(CTWorksheetImpl.CONDITIONALFORMATTING$32, n);
        }
    }
    
    public CTConditionalFormatting addNewConditionalFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTConditionalFormatting)this.get_store().add_element_user(CTWorksheetImpl.CONDITIONALFORMATTING$32);
        }
    }
    
    public void removeConditionalFormatting(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.CONDITIONALFORMATTING$32, n);
        }
    }
    
    public CTDataValidations getDataValidations() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDataValidations ctDataValidations = (CTDataValidations)this.get_store().find_element_user(CTWorksheetImpl.DATAVALIDATIONS$34, 0);
            if (ctDataValidations == null) {
                return null;
            }
            return ctDataValidations;
        }
    }
    
    public boolean isSetDataValidations() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.DATAVALIDATIONS$34) != 0;
        }
    }
    
    public void setDataValidations(final CTDataValidations ctDataValidations) {
        this.generatedSetterHelperImpl((XmlObject)ctDataValidations, CTWorksheetImpl.DATAVALIDATIONS$34, 0, (short)1);
    }
    
    public CTDataValidations addNewDataValidations() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDataValidations)this.get_store().add_element_user(CTWorksheetImpl.DATAVALIDATIONS$34);
        }
    }
    
    public void unsetDataValidations() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.DATAVALIDATIONS$34, 0);
        }
    }
    
    public CTHyperlinks getHyperlinks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHyperlinks ctHyperlinks = (CTHyperlinks)this.get_store().find_element_user(CTWorksheetImpl.HYPERLINKS$36, 0);
            if (ctHyperlinks == null) {
                return null;
            }
            return ctHyperlinks;
        }
    }
    
    public boolean isSetHyperlinks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.HYPERLINKS$36) != 0;
        }
    }
    
    public void setHyperlinks(final CTHyperlinks ctHyperlinks) {
        this.generatedSetterHelperImpl((XmlObject)ctHyperlinks, CTWorksheetImpl.HYPERLINKS$36, 0, (short)1);
    }
    
    public CTHyperlinks addNewHyperlinks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHyperlinks)this.get_store().add_element_user(CTWorksheetImpl.HYPERLINKS$36);
        }
    }
    
    public void unsetHyperlinks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.HYPERLINKS$36, 0);
        }
    }
    
    public CTPrintOptions getPrintOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPrintOptions ctPrintOptions = (CTPrintOptions)this.get_store().find_element_user(CTWorksheetImpl.PRINTOPTIONS$38, 0);
            if (ctPrintOptions == null) {
                return null;
            }
            return ctPrintOptions;
        }
    }
    
    public boolean isSetPrintOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.PRINTOPTIONS$38) != 0;
        }
    }
    
    public void setPrintOptions(final CTPrintOptions ctPrintOptions) {
        this.generatedSetterHelperImpl((XmlObject)ctPrintOptions, CTWorksheetImpl.PRINTOPTIONS$38, 0, (short)1);
    }
    
    public CTPrintOptions addNewPrintOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPrintOptions)this.get_store().add_element_user(CTWorksheetImpl.PRINTOPTIONS$38);
        }
    }
    
    public void unsetPrintOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.PRINTOPTIONS$38, 0);
        }
    }
    
    public CTPageMargins getPageMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPageMargins ctPageMargins = (CTPageMargins)this.get_store().find_element_user(CTWorksheetImpl.PAGEMARGINS$40, 0);
            if (ctPageMargins == null) {
                return null;
            }
            return ctPageMargins;
        }
    }
    
    public boolean isSetPageMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.PAGEMARGINS$40) != 0;
        }
    }
    
    public void setPageMargins(final CTPageMargins ctPageMargins) {
        this.generatedSetterHelperImpl((XmlObject)ctPageMargins, CTWorksheetImpl.PAGEMARGINS$40, 0, (short)1);
    }
    
    public CTPageMargins addNewPageMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageMargins)this.get_store().add_element_user(CTWorksheetImpl.PAGEMARGINS$40);
        }
    }
    
    public void unsetPageMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.PAGEMARGINS$40, 0);
        }
    }
    
    public CTPageSetup getPageSetup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPageSetup ctPageSetup = (CTPageSetup)this.get_store().find_element_user(CTWorksheetImpl.PAGESETUP$42, 0);
            if (ctPageSetup == null) {
                return null;
            }
            return ctPageSetup;
        }
    }
    
    public boolean isSetPageSetup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.PAGESETUP$42) != 0;
        }
    }
    
    public void setPageSetup(final CTPageSetup ctPageSetup) {
        this.generatedSetterHelperImpl((XmlObject)ctPageSetup, CTWorksheetImpl.PAGESETUP$42, 0, (short)1);
    }
    
    public CTPageSetup addNewPageSetup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageSetup)this.get_store().add_element_user(CTWorksheetImpl.PAGESETUP$42);
        }
    }
    
    public void unsetPageSetup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.PAGESETUP$42, 0);
        }
    }
    
    public CTHeaderFooter getHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHeaderFooter ctHeaderFooter = (CTHeaderFooter)this.get_store().find_element_user(CTWorksheetImpl.HEADERFOOTER$44, 0);
            if (ctHeaderFooter == null) {
                return null;
            }
            return ctHeaderFooter;
        }
    }
    
    public boolean isSetHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.HEADERFOOTER$44) != 0;
        }
    }
    
    public void setHeaderFooter(final CTHeaderFooter ctHeaderFooter) {
        this.generatedSetterHelperImpl((XmlObject)ctHeaderFooter, CTWorksheetImpl.HEADERFOOTER$44, 0, (short)1);
    }
    
    public CTHeaderFooter addNewHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHeaderFooter)this.get_store().add_element_user(CTWorksheetImpl.HEADERFOOTER$44);
        }
    }
    
    public void unsetHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.HEADERFOOTER$44, 0);
        }
    }
    
    public CTPageBreak getRowBreaks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPageBreak ctPageBreak = (CTPageBreak)this.get_store().find_element_user(CTWorksheetImpl.ROWBREAKS$46, 0);
            if (ctPageBreak == null) {
                return null;
            }
            return ctPageBreak;
        }
    }
    
    public boolean isSetRowBreaks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.ROWBREAKS$46) != 0;
        }
    }
    
    public void setRowBreaks(final CTPageBreak ctPageBreak) {
        this.generatedSetterHelperImpl((XmlObject)ctPageBreak, CTWorksheetImpl.ROWBREAKS$46, 0, (short)1);
    }
    
    public CTPageBreak addNewRowBreaks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageBreak)this.get_store().add_element_user(CTWorksheetImpl.ROWBREAKS$46);
        }
    }
    
    public void unsetRowBreaks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.ROWBREAKS$46, 0);
        }
    }
    
    public CTPageBreak getColBreaks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPageBreak ctPageBreak = (CTPageBreak)this.get_store().find_element_user(CTWorksheetImpl.COLBREAKS$48, 0);
            if (ctPageBreak == null) {
                return null;
            }
            return ctPageBreak;
        }
    }
    
    public boolean isSetColBreaks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.COLBREAKS$48) != 0;
        }
    }
    
    public void setColBreaks(final CTPageBreak ctPageBreak) {
        this.generatedSetterHelperImpl((XmlObject)ctPageBreak, CTWorksheetImpl.COLBREAKS$48, 0, (short)1);
    }
    
    public CTPageBreak addNewColBreaks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageBreak)this.get_store().add_element_user(CTWorksheetImpl.COLBREAKS$48);
        }
    }
    
    public void unsetColBreaks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.COLBREAKS$48, 0);
        }
    }
    
    public CTCustomProperties getCustomProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCustomProperties ctCustomProperties = (CTCustomProperties)this.get_store().find_element_user(CTWorksheetImpl.CUSTOMPROPERTIES$50, 0);
            if (ctCustomProperties == null) {
                return null;
            }
            return ctCustomProperties;
        }
    }
    
    public boolean isSetCustomProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.CUSTOMPROPERTIES$50) != 0;
        }
    }
    
    public void setCustomProperties(final CTCustomProperties ctCustomProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctCustomProperties, CTWorksheetImpl.CUSTOMPROPERTIES$50, 0, (short)1);
    }
    
    public CTCustomProperties addNewCustomProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCustomProperties)this.get_store().add_element_user(CTWorksheetImpl.CUSTOMPROPERTIES$50);
        }
    }
    
    public void unsetCustomProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.CUSTOMPROPERTIES$50, 0);
        }
    }
    
    public CTCellWatches getCellWatches() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCellWatches ctCellWatches = (CTCellWatches)this.get_store().find_element_user(CTWorksheetImpl.CELLWATCHES$52, 0);
            if (ctCellWatches == null) {
                return null;
            }
            return ctCellWatches;
        }
    }
    
    public boolean isSetCellWatches() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.CELLWATCHES$52) != 0;
        }
    }
    
    public void setCellWatches(final CTCellWatches ctCellWatches) {
        this.generatedSetterHelperImpl((XmlObject)ctCellWatches, CTWorksheetImpl.CELLWATCHES$52, 0, (short)1);
    }
    
    public CTCellWatches addNewCellWatches() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCellWatches)this.get_store().add_element_user(CTWorksheetImpl.CELLWATCHES$52);
        }
    }
    
    public void unsetCellWatches() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.CELLWATCHES$52, 0);
        }
    }
    
    public CTIgnoredErrors getIgnoredErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTIgnoredErrors ctIgnoredErrors = (CTIgnoredErrors)this.get_store().find_element_user(CTWorksheetImpl.IGNOREDERRORS$54, 0);
            if (ctIgnoredErrors == null) {
                return null;
            }
            return ctIgnoredErrors;
        }
    }
    
    public boolean isSetIgnoredErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.IGNOREDERRORS$54) != 0;
        }
    }
    
    public void setIgnoredErrors(final CTIgnoredErrors ctIgnoredErrors) {
        this.generatedSetterHelperImpl((XmlObject)ctIgnoredErrors, CTWorksheetImpl.IGNOREDERRORS$54, 0, (short)1);
    }
    
    public CTIgnoredErrors addNewIgnoredErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTIgnoredErrors)this.get_store().add_element_user(CTWorksheetImpl.IGNOREDERRORS$54);
        }
    }
    
    public void unsetIgnoredErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.IGNOREDERRORS$54, 0);
        }
    }
    
    public CTSmartTags getSmartTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSmartTags ctSmartTags = (CTSmartTags)this.get_store().find_element_user(CTWorksheetImpl.SMARTTAGS$56, 0);
            if (ctSmartTags == null) {
                return null;
            }
            return ctSmartTags;
        }
    }
    
    public boolean isSetSmartTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.SMARTTAGS$56) != 0;
        }
    }
    
    public void setSmartTags(final CTSmartTags ctSmartTags) {
        this.generatedSetterHelperImpl((XmlObject)ctSmartTags, CTWorksheetImpl.SMARTTAGS$56, 0, (short)1);
    }
    
    public CTSmartTags addNewSmartTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSmartTags)this.get_store().add_element_user(CTWorksheetImpl.SMARTTAGS$56);
        }
    }
    
    public void unsetSmartTags() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.SMARTTAGS$56, 0);
        }
    }
    
    public CTDrawing getDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDrawing ctDrawing = (CTDrawing)this.get_store().find_element_user(CTWorksheetImpl.DRAWING$58, 0);
            if (ctDrawing == null) {
                return null;
            }
            return ctDrawing;
        }
    }
    
    public boolean isSetDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.DRAWING$58) != 0;
        }
    }
    
    public void setDrawing(final CTDrawing ctDrawing) {
        this.generatedSetterHelperImpl((XmlObject)ctDrawing, CTWorksheetImpl.DRAWING$58, 0, (short)1);
    }
    
    public CTDrawing addNewDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDrawing)this.get_store().add_element_user(CTWorksheetImpl.DRAWING$58);
        }
    }
    
    public void unsetDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.DRAWING$58, 0);
        }
    }
    
    public CTLegacyDrawing getLegacyDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLegacyDrawing ctLegacyDrawing = (CTLegacyDrawing)this.get_store().find_element_user(CTWorksheetImpl.LEGACYDRAWING$60, 0);
            if (ctLegacyDrawing == null) {
                return null;
            }
            return ctLegacyDrawing;
        }
    }
    
    public boolean isSetLegacyDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.LEGACYDRAWING$60) != 0;
        }
    }
    
    public void setLegacyDrawing(final CTLegacyDrawing ctLegacyDrawing) {
        this.generatedSetterHelperImpl((XmlObject)ctLegacyDrawing, CTWorksheetImpl.LEGACYDRAWING$60, 0, (short)1);
    }
    
    public CTLegacyDrawing addNewLegacyDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLegacyDrawing)this.get_store().add_element_user(CTWorksheetImpl.LEGACYDRAWING$60);
        }
    }
    
    public void unsetLegacyDrawing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.LEGACYDRAWING$60, 0);
        }
    }
    
    public CTLegacyDrawing getLegacyDrawingHF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLegacyDrawing ctLegacyDrawing = (CTLegacyDrawing)this.get_store().find_element_user(CTWorksheetImpl.LEGACYDRAWINGHF$62, 0);
            if (ctLegacyDrawing == null) {
                return null;
            }
            return ctLegacyDrawing;
        }
    }
    
    public boolean isSetLegacyDrawingHF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.LEGACYDRAWINGHF$62) != 0;
        }
    }
    
    public void setLegacyDrawingHF(final CTLegacyDrawing ctLegacyDrawing) {
        this.generatedSetterHelperImpl((XmlObject)ctLegacyDrawing, CTWorksheetImpl.LEGACYDRAWINGHF$62, 0, (short)1);
    }
    
    public CTLegacyDrawing addNewLegacyDrawingHF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLegacyDrawing)this.get_store().add_element_user(CTWorksheetImpl.LEGACYDRAWINGHF$62);
        }
    }
    
    public void unsetLegacyDrawingHF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.LEGACYDRAWINGHF$62, 0);
        }
    }
    
    public CTSheetBackgroundPicture getPicture() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSheetBackgroundPicture ctSheetBackgroundPicture = (CTSheetBackgroundPicture)this.get_store().find_element_user(CTWorksheetImpl.PICTURE$64, 0);
            if (ctSheetBackgroundPicture == null) {
                return null;
            }
            return ctSheetBackgroundPicture;
        }
    }
    
    public boolean isSetPicture() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.PICTURE$64) != 0;
        }
    }
    
    public void setPicture(final CTSheetBackgroundPicture ctSheetBackgroundPicture) {
        this.generatedSetterHelperImpl((XmlObject)ctSheetBackgroundPicture, CTWorksheetImpl.PICTURE$64, 0, (short)1);
    }
    
    public CTSheetBackgroundPicture addNewPicture() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheetBackgroundPicture)this.get_store().add_element_user(CTWorksheetImpl.PICTURE$64);
        }
    }
    
    public void unsetPicture() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.PICTURE$64, 0);
        }
    }
    
    public CTOleObjects getOleObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOleObjects ctOleObjects = (CTOleObjects)this.get_store().find_element_user(CTWorksheetImpl.OLEOBJECTS$66, 0);
            if (ctOleObjects == null) {
                return null;
            }
            return ctOleObjects;
        }
    }
    
    public boolean isSetOleObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.OLEOBJECTS$66) != 0;
        }
    }
    
    public void setOleObjects(final CTOleObjects ctOleObjects) {
        this.generatedSetterHelperImpl((XmlObject)ctOleObjects, CTWorksheetImpl.OLEOBJECTS$66, 0, (short)1);
    }
    
    public CTOleObjects addNewOleObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOleObjects)this.get_store().add_element_user(CTWorksheetImpl.OLEOBJECTS$66);
        }
    }
    
    public void unsetOleObjects() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.OLEOBJECTS$66, 0);
        }
    }
    
    public CTControls getControls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTControls ctControls = (CTControls)this.get_store().find_element_user(CTWorksheetImpl.CONTROLS$68, 0);
            if (ctControls == null) {
                return null;
            }
            return ctControls;
        }
    }
    
    public boolean isSetControls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.CONTROLS$68) != 0;
        }
    }
    
    public void setControls(final CTControls ctControls) {
        this.generatedSetterHelperImpl((XmlObject)ctControls, CTWorksheetImpl.CONTROLS$68, 0, (short)1);
    }
    
    public CTControls addNewControls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTControls)this.get_store().add_element_user(CTWorksheetImpl.CONTROLS$68);
        }
    }
    
    public void unsetControls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.CONTROLS$68, 0);
        }
    }
    
    public CTWebPublishItems getWebPublishItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWebPublishItems ctWebPublishItems = (CTWebPublishItems)this.get_store().find_element_user(CTWorksheetImpl.WEBPUBLISHITEMS$70, 0);
            if (ctWebPublishItems == null) {
                return null;
            }
            return ctWebPublishItems;
        }
    }
    
    public boolean isSetWebPublishItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.WEBPUBLISHITEMS$70) != 0;
        }
    }
    
    public void setWebPublishItems(final CTWebPublishItems ctWebPublishItems) {
        this.generatedSetterHelperImpl((XmlObject)ctWebPublishItems, CTWorksheetImpl.WEBPUBLISHITEMS$70, 0, (short)1);
    }
    
    public CTWebPublishItems addNewWebPublishItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWebPublishItems)this.get_store().add_element_user(CTWorksheetImpl.WEBPUBLISHITEMS$70);
        }
    }
    
    public void unsetWebPublishItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.WEBPUBLISHITEMS$70, 0);
        }
    }
    
    public CTTableParts getTableParts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableParts ctTableParts = (CTTableParts)this.get_store().find_element_user(CTWorksheetImpl.TABLEPARTS$72, 0);
            if (ctTableParts == null) {
                return null;
            }
            return ctTableParts;
        }
    }
    
    public boolean isSetTableParts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.TABLEPARTS$72) != 0;
        }
    }
    
    public void setTableParts(final CTTableParts ctTableParts) {
        this.generatedSetterHelperImpl((XmlObject)ctTableParts, CTWorksheetImpl.TABLEPARTS$72, 0, (short)1);
    }
    
    public CTTableParts addNewTableParts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableParts)this.get_store().add_element_user(CTWorksheetImpl.TABLEPARTS$72);
        }
    }
    
    public void unsetTableParts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.TABLEPARTS$72, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTWorksheetImpl.EXTLST$74, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTWorksheetImpl.EXTLST$74) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTWorksheetImpl.EXTLST$74, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTWorksheetImpl.EXTLST$74);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTWorksheetImpl.EXTLST$74, 0);
        }
    }
    
    static {
        SHEETPR$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetPr");
        DIMENSION$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "dimension");
        SHEETVIEWS$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetViews");
        SHEETFORMATPR$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetFormatPr");
        COLS$8 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "cols");
        SHEETDATA$10 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetData");
        SHEETCALCPR$12 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetCalcPr");
        SHEETPROTECTION$14 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetProtection");
        PROTECTEDRANGES$16 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "protectedRanges");
        SCENARIOS$18 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "scenarios");
        AUTOFILTER$20 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "autoFilter");
        SORTSTATE$22 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sortState");
        DATACONSOLIDATE$24 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "dataConsolidate");
        CUSTOMSHEETVIEWS$26 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "customSheetViews");
        MERGECELLS$28 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "mergeCells");
        PHONETICPR$30 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "phoneticPr");
        CONDITIONALFORMATTING$32 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "conditionalFormatting");
        DATAVALIDATIONS$34 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "dataValidations");
        HYPERLINKS$36 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "hyperlinks");
        PRINTOPTIONS$38 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "printOptions");
        PAGEMARGINS$40 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pageMargins");
        PAGESETUP$42 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pageSetup");
        HEADERFOOTER$44 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "headerFooter");
        ROWBREAKS$46 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "rowBreaks");
        COLBREAKS$48 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "colBreaks");
        CUSTOMPROPERTIES$50 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "customProperties");
        CELLWATCHES$52 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "cellWatches");
        IGNOREDERRORS$54 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "ignoredErrors");
        SMARTTAGS$56 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "smartTags");
        DRAWING$58 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "drawing");
        LEGACYDRAWING$60 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "legacyDrawing");
        LEGACYDRAWINGHF$62 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "legacyDrawingHF");
        PICTURE$64 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "picture");
        OLEOBJECTS$66 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "oleObjects");
        CONTROLS$68 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "controls");
        WEBPUBLISHITEMS$70 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "webPublishItems");
        TABLEPARTS$72 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "tableParts");
        EXTLST$74 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
    }
}
