package org.apache.poi.xssf.usermodel;

import org.apache.poi.xssf.model.ExternalLinksTable;
import org.apache.poi.xssf.model.CalculationChain;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.model.SingleXmlCells;
import org.apache.poi.xssf.model.MapInfo;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.model.SharedStringsTable;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ooxml.POIXMLRelation;

public final class XSSFRelation extends POIXMLRelation
{
    private static final Map<String, XSSFRelation> _table;
    public static final XSSFRelation WORKBOOK;
    public static final XSSFRelation MACROS_WORKBOOK;
    public static final XSSFRelation TEMPLATE_WORKBOOK;
    public static final XSSFRelation MACRO_TEMPLATE_WORKBOOK;
    public static final XSSFRelation MACRO_ADDIN_WORKBOOK;
    public static final XSSFRelation XLSB_BINARY_WORKBOOK;
    public static final XSSFRelation WORKSHEET;
    public static final XSSFRelation CHARTSHEET;
    public static final XSSFRelation SHARED_STRINGS;
    public static final XSSFRelation STYLES;
    public static final XSSFRelation DRAWINGS;
    public static final XSSFRelation VML_DRAWINGS;
    public static final XSSFRelation CHART;
    public static final XSSFRelation CUSTOM_XML_MAPPINGS;
    public static final XSSFRelation SINGLE_XML_CELLS;
    public static final XSSFRelation TABLE;
    public static final XSSFRelation IMAGES;
    public static final XSSFRelation IMAGE_EMF;
    public static final XSSFRelation IMAGE_WMF;
    public static final XSSFRelation IMAGE_PICT;
    public static final XSSFRelation IMAGE_JPEG;
    public static final XSSFRelation IMAGE_PNG;
    public static final XSSFRelation IMAGE_DIB;
    public static final XSSFRelation IMAGE_GIF;
    public static final XSSFRelation IMAGE_TIFF;
    public static final XSSFRelation IMAGE_EPS;
    public static final XSSFRelation IMAGE_BMP;
    public static final XSSFRelation IMAGE_WPG;
    public static final XSSFRelation SHEET_COMMENTS;
    public static final XSSFRelation SHEET_HYPERLINKS;
    public static final XSSFRelation OLEEMBEDDINGS;
    public static final XSSFRelation PACKEMBEDDINGS;
    public static final XSSFRelation VBA_MACROS;
    public static final XSSFRelation ACTIVEX_CONTROLS;
    public static final XSSFRelation ACTIVEX_BINS;
    public static final XSSFRelation MACRO_SHEET_BIN;
    public static final XSSFRelation INTL_MACRO_SHEET_BIN;
    public static final XSSFRelation DIALOG_SHEET_BIN;
    public static final XSSFRelation THEME;
    public static final XSSFRelation CALC_CHAIN;
    public static final XSSFRelation EXTERNAL_LINKS;
    public static final XSSFRelation PRINTER_SETTINGS;
    public static final XSSFRelation PIVOT_TABLE;
    public static final XSSFRelation PIVOT_CACHE_DEFINITION;
    public static final XSSFRelation PIVOT_CACHE_RECORDS;
    public static final XSSFRelation CTRL_PROP_RECORDS;
    public static final XSSFRelation CUSTOM_PROPERTIES;
    public static final String NS_SPREADSHEETML = "http://schemas.openxmlformats.org/spreadsheetml/2006/main";
    public static final String NS_DRAWINGML = "http://schemas.openxmlformats.org/drawingml/2006/main";
    public static final String NS_CHART = "http://schemas.openxmlformats.org/drawingml/2006/chart";
    
    private XSSFRelation(final String type, final String rel, final String defaultName) {
        this(type, rel, defaultName, null, null);
    }
    
    private XSSFRelation(final String type, final String rel, final String defaultName, final NoArgConstructor noArgConstructor, final PackagePartConstructor packagePartConstructor) {
        super(type, rel, defaultName, noArgConstructor, packagePartConstructor, null);
        XSSFRelation._table.put(rel, this);
    }
    
    public static XSSFRelation getInstance(final String rel) {
        return XSSFRelation._table.get(rel);
    }
    
    static {
        _table = new HashMap<String, XSSFRelation>();
        WORKBOOK = new XSSFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/workbook", "/xl/workbook.xml");
        MACROS_WORKBOOK = new XSSFRelation("application/vnd.ms-excel.sheet.macroEnabled.main+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument", "/xl/workbook.xml");
        TEMPLATE_WORKBOOK = new XSSFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.template.main+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument", "/xl/workbook.xml");
        MACRO_TEMPLATE_WORKBOOK = new XSSFRelation("application/vnd.ms-excel.template.macroEnabled.main+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument", "/xl/workbook.xml");
        MACRO_ADDIN_WORKBOOK = new XSSFRelation("application/vnd.ms-excel.addin.macroEnabled.main+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument", "/xl/workbook.xml");
        XLSB_BINARY_WORKBOOK = new XSSFRelation("application/vnd.ms-excel.sheet.binary.macroEnabled.main", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument", "/xl/workbook.bin");
        WORKSHEET = new XSSFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet", "/xl/worksheets/sheet#.xml", XSSFSheet::new, XSSFSheet::new);
        CHARTSHEET = new XSSFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.chartsheet+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/chartsheet", "/xl/chartsheets/sheet#.xml", null, XSSFChartSheet::new);
        SHARED_STRINGS = new XSSFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/sharedStrings", "/xl/sharedStrings.xml", SharedStringsTable::new, SharedStringsTable::new);
        STYLES = new XSSFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles", "/xl/styles.xml", StylesTable::new, StylesTable::new);
        DRAWINGS = new XSSFRelation("application/vnd.openxmlformats-officedocument.drawing+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/drawing", "/xl/drawings/drawing#.xml", XSSFDrawing::new, XSSFDrawing::new);
        VML_DRAWINGS = new XSSFRelation("application/vnd.openxmlformats-officedocument.vmlDrawing", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/vmlDrawing", "/xl/drawings/vmlDrawing#.vml", XSSFVMLDrawing::new, XSSFVMLDrawing::new);
        CHART = new XSSFRelation("application/vnd.openxmlformats-officedocument.drawingml.chart+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/chart", "/xl/charts/chart#.xml", XSSFChart::new, XSSFChart::new);
        CUSTOM_XML_MAPPINGS = new XSSFRelation("application/xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/xmlMaps", "/xl/xmlMaps.xml", MapInfo::new, MapInfo::new);
        SINGLE_XML_CELLS = new XSSFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.tableSingleCells+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/tableSingleCells", "/xl/tables/tableSingleCells#.xml", SingleXmlCells::new, SingleXmlCells::new);
        TABLE = new XSSFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.table+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/table", "/xl/tables/table#.xml", XSSFTable::new, XSSFTable::new);
        IMAGES = new XSSFRelation(null, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", null, XSSFPictureData::new, XSSFPictureData::new);
        IMAGE_EMF = new XSSFRelation("image/x-emf", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/xl/media/image#.emf", XSSFPictureData::new, XSSFPictureData::new);
        IMAGE_WMF = new XSSFRelation("image/x-wmf", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/xl/media/image#.wmf", XSSFPictureData::new, XSSFPictureData::new);
        IMAGE_PICT = new XSSFRelation("image/pict", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/xl/media/image#.pict", XSSFPictureData::new, XSSFPictureData::new);
        IMAGE_JPEG = new XSSFRelation("image/jpeg", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/xl/media/image#.jpeg", XSSFPictureData::new, XSSFPictureData::new);
        IMAGE_PNG = new XSSFRelation("image/png", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/xl/media/image#.png", XSSFPictureData::new, XSSFPictureData::new);
        IMAGE_DIB = new XSSFRelation("image/dib", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/xl/media/image#.dib", XSSFPictureData::new, XSSFPictureData::new);
        IMAGE_GIF = new XSSFRelation("image/gif", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/xl/media/image#.gif", XSSFPictureData::new, XSSFPictureData::new);
        IMAGE_TIFF = new XSSFRelation("image/tiff", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/xl/media/image#.tiff", XSSFPictureData::new, XSSFPictureData::new);
        IMAGE_EPS = new XSSFRelation("image/x-eps", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/xl/media/image#.eps", XSSFPictureData::new, XSSFPictureData::new);
        IMAGE_BMP = new XSSFRelation("image/x-ms-bmp", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/xl/media/image#.bmp", XSSFPictureData::new, XSSFPictureData::new);
        IMAGE_WPG = new XSSFRelation("image/x-wpg", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/xl/media/image#.wpg", XSSFPictureData::new, XSSFPictureData::new);
        SHEET_COMMENTS = new XSSFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.comments+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/comments", "/xl/comments#.xml", CommentsTable::new, CommentsTable::new);
        SHEET_HYPERLINKS = new XSSFRelation(null, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink", null);
        OLEEMBEDDINGS = new XSSFRelation(null, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/oleObject", null);
        PACKEMBEDDINGS = new XSSFRelation(null, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/package", null);
        VBA_MACROS = new XSSFRelation("application/vnd.ms-office.vbaProject", "http://schemas.microsoft.com/office/2006/relationships/vbaProject", "/xl/vbaProject.bin", XSSFVBAPart::new, XSSFVBAPart::new);
        ACTIVEX_CONTROLS = new XSSFRelation("application/vnd.ms-office.activeX+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/control", "/xl/activeX/activeX#.xml");
        ACTIVEX_BINS = new XSSFRelation("application/vnd.ms-office.activeX", "http://schemas.microsoft.com/office/2006/relationships/activeXControlBinary", "/xl/activeX/activeX#.bin");
        MACRO_SHEET_BIN = new XSSFRelation(null, "http://schemas.microsoft.com/office/2006/relationships/xlMacrosheet", "/xl/macroSheets/sheet#.bin");
        INTL_MACRO_SHEET_BIN = new XSSFRelation(null, "http://schemas.microsoft.com/office/2006/relationships/xlIntlMacrosheet", "/xl/macroSheets/sheet#.bin");
        DIALOG_SHEET_BIN = new XSSFRelation(null, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/dialogsheet", "/xl/dialogSheets/sheet#.bin");
        THEME = new XSSFRelation("application/vnd.openxmlformats-officedocument.theme+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/theme", "/xl/theme/theme#.xml", ThemesTable::new, ThemesTable::new);
        CALC_CHAIN = new XSSFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.calcChain+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/calcChain", "/xl/calcChain.xml", CalculationChain::new, CalculationChain::new);
        EXTERNAL_LINKS = new XSSFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.externalLink+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/externalLink", "/xl/externalLinks/externalLink#.xmll", ExternalLinksTable::new, ExternalLinksTable::new);
        PRINTER_SETTINGS = new XSSFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.printerSettings", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/printerSettings", "/xl/printerSettings/printerSettings#.bin");
        PIVOT_TABLE = new XSSFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.pivotTable+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/pivotTable", "/xl/pivotTables/pivotTable#.xml", XSSFPivotTable::new, XSSFPivotTable::new);
        PIVOT_CACHE_DEFINITION = new XSSFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.pivotCacheDefinition+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/pivotCacheDefinition", "/xl/pivotCache/pivotCacheDefinition#.xml", XSSFPivotCacheDefinition::new, XSSFPivotCacheDefinition::new);
        PIVOT_CACHE_RECORDS = new XSSFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.pivotCacheRecords+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/pivotCacheRecords", "/xl/pivotCache/pivotCacheRecords#.xml", XSSFPivotCacheRecords::new, XSSFPivotCacheRecords::new);
        CTRL_PROP_RECORDS = new XSSFRelation(null, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/ctrlProp", "/xl/ctrlProps/ctrlProp#.xml");
        CUSTOM_PROPERTIES = new XSSFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.customProperty", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/customProperty", "/xl/customProperty#.bin");
    }
}
