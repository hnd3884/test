package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.SchemaTypeLoader;
import java.lang.ref.SoftReference;
import org.apache.xmlbeans.XmlBeans;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTWorksheet extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTWorksheet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctworksheet530dtype");
    
    CTSheetPr getSheetPr();
    
    boolean isSetSheetPr();
    
    void setSheetPr(final CTSheetPr p0);
    
    CTSheetPr addNewSheetPr();
    
    void unsetSheetPr();
    
    CTSheetDimension getDimension();
    
    boolean isSetDimension();
    
    void setDimension(final CTSheetDimension p0);
    
    CTSheetDimension addNewDimension();
    
    void unsetDimension();
    
    CTSheetViews getSheetViews();
    
    boolean isSetSheetViews();
    
    void setSheetViews(final CTSheetViews p0);
    
    CTSheetViews addNewSheetViews();
    
    void unsetSheetViews();
    
    CTSheetFormatPr getSheetFormatPr();
    
    boolean isSetSheetFormatPr();
    
    void setSheetFormatPr(final CTSheetFormatPr p0);
    
    CTSheetFormatPr addNewSheetFormatPr();
    
    void unsetSheetFormatPr();
    
    List<CTCols> getColsList();
    
    @Deprecated
    CTCols[] getColsArray();
    
    CTCols getColsArray(final int p0);
    
    int sizeOfColsArray();
    
    void setColsArray(final CTCols[] p0);
    
    void setColsArray(final int p0, final CTCols p1);
    
    CTCols insertNewCols(final int p0);
    
    CTCols addNewCols();
    
    void removeCols(final int p0);
    
    CTSheetData getSheetData();
    
    void setSheetData(final CTSheetData p0);
    
    CTSheetData addNewSheetData();
    
    CTSheetCalcPr getSheetCalcPr();
    
    boolean isSetSheetCalcPr();
    
    void setSheetCalcPr(final CTSheetCalcPr p0);
    
    CTSheetCalcPr addNewSheetCalcPr();
    
    void unsetSheetCalcPr();
    
    CTSheetProtection getSheetProtection();
    
    boolean isSetSheetProtection();
    
    void setSheetProtection(final CTSheetProtection p0);
    
    CTSheetProtection addNewSheetProtection();
    
    void unsetSheetProtection();
    
    CTProtectedRanges getProtectedRanges();
    
    boolean isSetProtectedRanges();
    
    void setProtectedRanges(final CTProtectedRanges p0);
    
    CTProtectedRanges addNewProtectedRanges();
    
    void unsetProtectedRanges();
    
    CTScenarios getScenarios();
    
    boolean isSetScenarios();
    
    void setScenarios(final CTScenarios p0);
    
    CTScenarios addNewScenarios();
    
    void unsetScenarios();
    
    CTAutoFilter getAutoFilter();
    
    boolean isSetAutoFilter();
    
    void setAutoFilter(final CTAutoFilter p0);
    
    CTAutoFilter addNewAutoFilter();
    
    void unsetAutoFilter();
    
    CTSortState getSortState();
    
    boolean isSetSortState();
    
    void setSortState(final CTSortState p0);
    
    CTSortState addNewSortState();
    
    void unsetSortState();
    
    CTDataConsolidate getDataConsolidate();
    
    boolean isSetDataConsolidate();
    
    void setDataConsolidate(final CTDataConsolidate p0);
    
    CTDataConsolidate addNewDataConsolidate();
    
    void unsetDataConsolidate();
    
    CTCustomSheetViews getCustomSheetViews();
    
    boolean isSetCustomSheetViews();
    
    void setCustomSheetViews(final CTCustomSheetViews p0);
    
    CTCustomSheetViews addNewCustomSheetViews();
    
    void unsetCustomSheetViews();
    
    CTMergeCells getMergeCells();
    
    boolean isSetMergeCells();
    
    void setMergeCells(final CTMergeCells p0);
    
    CTMergeCells addNewMergeCells();
    
    void unsetMergeCells();
    
    CTPhoneticPr getPhoneticPr();
    
    boolean isSetPhoneticPr();
    
    void setPhoneticPr(final CTPhoneticPr p0);
    
    CTPhoneticPr addNewPhoneticPr();
    
    void unsetPhoneticPr();
    
    List<CTConditionalFormatting> getConditionalFormattingList();
    
    @Deprecated
    CTConditionalFormatting[] getConditionalFormattingArray();
    
    CTConditionalFormatting getConditionalFormattingArray(final int p0);
    
    int sizeOfConditionalFormattingArray();
    
    void setConditionalFormattingArray(final CTConditionalFormatting[] p0);
    
    void setConditionalFormattingArray(final int p0, final CTConditionalFormatting p1);
    
    CTConditionalFormatting insertNewConditionalFormatting(final int p0);
    
    CTConditionalFormatting addNewConditionalFormatting();
    
    void removeConditionalFormatting(final int p0);
    
    CTDataValidations getDataValidations();
    
    boolean isSetDataValidations();
    
    void setDataValidations(final CTDataValidations p0);
    
    CTDataValidations addNewDataValidations();
    
    void unsetDataValidations();
    
    CTHyperlinks getHyperlinks();
    
    boolean isSetHyperlinks();
    
    void setHyperlinks(final CTHyperlinks p0);
    
    CTHyperlinks addNewHyperlinks();
    
    void unsetHyperlinks();
    
    CTPrintOptions getPrintOptions();
    
    boolean isSetPrintOptions();
    
    void setPrintOptions(final CTPrintOptions p0);
    
    CTPrintOptions addNewPrintOptions();
    
    void unsetPrintOptions();
    
    CTPageMargins getPageMargins();
    
    boolean isSetPageMargins();
    
    void setPageMargins(final CTPageMargins p0);
    
    CTPageMargins addNewPageMargins();
    
    void unsetPageMargins();
    
    CTPageSetup getPageSetup();
    
    boolean isSetPageSetup();
    
    void setPageSetup(final CTPageSetup p0);
    
    CTPageSetup addNewPageSetup();
    
    void unsetPageSetup();
    
    CTHeaderFooter getHeaderFooter();
    
    boolean isSetHeaderFooter();
    
    void setHeaderFooter(final CTHeaderFooter p0);
    
    CTHeaderFooter addNewHeaderFooter();
    
    void unsetHeaderFooter();
    
    CTPageBreak getRowBreaks();
    
    boolean isSetRowBreaks();
    
    void setRowBreaks(final CTPageBreak p0);
    
    CTPageBreak addNewRowBreaks();
    
    void unsetRowBreaks();
    
    CTPageBreak getColBreaks();
    
    boolean isSetColBreaks();
    
    void setColBreaks(final CTPageBreak p0);
    
    CTPageBreak addNewColBreaks();
    
    void unsetColBreaks();
    
    CTCustomProperties getCustomProperties();
    
    boolean isSetCustomProperties();
    
    void setCustomProperties(final CTCustomProperties p0);
    
    CTCustomProperties addNewCustomProperties();
    
    void unsetCustomProperties();
    
    CTCellWatches getCellWatches();
    
    boolean isSetCellWatches();
    
    void setCellWatches(final CTCellWatches p0);
    
    CTCellWatches addNewCellWatches();
    
    void unsetCellWatches();
    
    CTIgnoredErrors getIgnoredErrors();
    
    boolean isSetIgnoredErrors();
    
    void setIgnoredErrors(final CTIgnoredErrors p0);
    
    CTIgnoredErrors addNewIgnoredErrors();
    
    void unsetIgnoredErrors();
    
    CTSmartTags getSmartTags();
    
    boolean isSetSmartTags();
    
    void setSmartTags(final CTSmartTags p0);
    
    CTSmartTags addNewSmartTags();
    
    void unsetSmartTags();
    
    CTDrawing getDrawing();
    
    boolean isSetDrawing();
    
    void setDrawing(final CTDrawing p0);
    
    CTDrawing addNewDrawing();
    
    void unsetDrawing();
    
    CTLegacyDrawing getLegacyDrawing();
    
    boolean isSetLegacyDrawing();
    
    void setLegacyDrawing(final CTLegacyDrawing p0);
    
    CTLegacyDrawing addNewLegacyDrawing();
    
    void unsetLegacyDrawing();
    
    CTLegacyDrawing getLegacyDrawingHF();
    
    boolean isSetLegacyDrawingHF();
    
    void setLegacyDrawingHF(final CTLegacyDrawing p0);
    
    CTLegacyDrawing addNewLegacyDrawingHF();
    
    void unsetLegacyDrawingHF();
    
    CTSheetBackgroundPicture getPicture();
    
    boolean isSetPicture();
    
    void setPicture(final CTSheetBackgroundPicture p0);
    
    CTSheetBackgroundPicture addNewPicture();
    
    void unsetPicture();
    
    CTOleObjects getOleObjects();
    
    boolean isSetOleObjects();
    
    void setOleObjects(final CTOleObjects p0);
    
    CTOleObjects addNewOleObjects();
    
    void unsetOleObjects();
    
    CTControls getControls();
    
    boolean isSetControls();
    
    void setControls(final CTControls p0);
    
    CTControls addNewControls();
    
    void unsetControls();
    
    CTWebPublishItems getWebPublishItems();
    
    boolean isSetWebPublishItems();
    
    void setWebPublishItems(final CTWebPublishItems p0);
    
    CTWebPublishItems addNewWebPublishItems();
    
    void unsetWebPublishItems();
    
    CTTableParts getTableParts();
    
    boolean isSetTableParts();
    
    void setTableParts(final CTTableParts p0);
    
    CTTableParts addNewTableParts();
    
    void unsetTableParts();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTWorksheet.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTWorksheet newInstance() {
            return (CTWorksheet)getTypeLoader().newInstance(CTWorksheet.type, (XmlOptions)null);
        }
        
        public static CTWorksheet newInstance(final XmlOptions xmlOptions) {
            return (CTWorksheet)getTypeLoader().newInstance(CTWorksheet.type, xmlOptions);
        }
        
        public static CTWorksheet parse(final String s) throws XmlException {
            return (CTWorksheet)getTypeLoader().parse(s, CTWorksheet.type, (XmlOptions)null);
        }
        
        public static CTWorksheet parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTWorksheet)getTypeLoader().parse(s, CTWorksheet.type, xmlOptions);
        }
        
        public static CTWorksheet parse(final File file) throws XmlException, IOException {
            return (CTWorksheet)getTypeLoader().parse(file, CTWorksheet.type, (XmlOptions)null);
        }
        
        public static CTWorksheet parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorksheet)getTypeLoader().parse(file, CTWorksheet.type, xmlOptions);
        }
        
        public static CTWorksheet parse(final URL url) throws XmlException, IOException {
            return (CTWorksheet)getTypeLoader().parse(url, CTWorksheet.type, (XmlOptions)null);
        }
        
        public static CTWorksheet parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorksheet)getTypeLoader().parse(url, CTWorksheet.type, xmlOptions);
        }
        
        public static CTWorksheet parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTWorksheet)getTypeLoader().parse(inputStream, CTWorksheet.type, (XmlOptions)null);
        }
        
        public static CTWorksheet parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorksheet)getTypeLoader().parse(inputStream, CTWorksheet.type, xmlOptions);
        }
        
        public static CTWorksheet parse(final Reader reader) throws XmlException, IOException {
            return (CTWorksheet)getTypeLoader().parse(reader, CTWorksheet.type, (XmlOptions)null);
        }
        
        public static CTWorksheet parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorksheet)getTypeLoader().parse(reader, CTWorksheet.type, xmlOptions);
        }
        
        public static CTWorksheet parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTWorksheet)getTypeLoader().parse(xmlStreamReader, CTWorksheet.type, (XmlOptions)null);
        }
        
        public static CTWorksheet parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTWorksheet)getTypeLoader().parse(xmlStreamReader, CTWorksheet.type, xmlOptions);
        }
        
        public static CTWorksheet parse(final Node node) throws XmlException {
            return (CTWorksheet)getTypeLoader().parse(node, CTWorksheet.type, (XmlOptions)null);
        }
        
        public static CTWorksheet parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTWorksheet)getTypeLoader().parse(node, CTWorksheet.type, xmlOptions);
        }
        
        @Deprecated
        public static CTWorksheet parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTWorksheet)getTypeLoader().parse(xmlInputStream, CTWorksheet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTWorksheet parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTWorksheet)getTypeLoader().parse(xmlInputStream, CTWorksheet.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTWorksheet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTWorksheet.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
