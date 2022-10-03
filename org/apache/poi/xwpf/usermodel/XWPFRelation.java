package org.apache.poi.xwpf.usermodel;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ooxml.POIXMLRelation;

public final class XWPFRelation extends POIXMLRelation
{
    private static final Map<String, XWPFRelation> _table;
    public static final XWPFRelation DOCUMENT;
    public static final XWPFRelation TEMPLATE;
    public static final XWPFRelation MACRO_DOCUMENT;
    public static final XWPFRelation MACRO_TEMPLATE_DOCUMENT;
    public static final XWPFRelation GLOSSARY_DOCUMENT;
    public static final XWPFRelation NUMBERING;
    public static final XWPFRelation FONT_TABLE;
    public static final XWPFRelation SETTINGS;
    public static final XWPFRelation STYLES;
    public static final XWPFRelation WEB_SETTINGS;
    public static final XWPFRelation HEADER;
    public static final XWPFRelation FOOTER;
    public static final XWPFRelation THEME;
    public static final XWPFRelation WORKBOOK;
    public static final XWPFRelation CHART;
    public static final XWPFRelation HYPERLINK;
    public static final XWPFRelation COMMENT;
    public static final XWPFRelation FOOTNOTE;
    public static final XWPFRelation ENDNOTE;
    public static final XWPFRelation IMAGE_EMF;
    public static final XWPFRelation IMAGE_WMF;
    public static final XWPFRelation IMAGE_PICT;
    public static final XWPFRelation IMAGE_JPEG;
    public static final XWPFRelation IMAGE_PNG;
    public static final XWPFRelation IMAGE_DIB;
    public static final XWPFRelation IMAGE_GIF;
    public static final XWPFRelation IMAGE_TIFF;
    public static final XWPFRelation IMAGE_EPS;
    public static final XWPFRelation IMAGE_BMP;
    public static final XWPFRelation IMAGE_WPG;
    public static final XWPFRelation IMAGES;
    
    private XWPFRelation(final String type, final String rel, final String defaultName) {
        super(type, rel, defaultName);
        XWPFRelation._table.put(rel, this);
    }
    
    private XWPFRelation(final String type, final String rel, final String defaultName, final NoArgConstructor noArgConstructor, final PackagePartConstructor packagePartConstructor) {
        super(type, rel, defaultName, noArgConstructor, packagePartConstructor, null);
        XWPFRelation._table.put(rel, this);
    }
    
    private XWPFRelation(final String type, final String rel, final String defaultName, final NoArgConstructor noArgConstructor, final ParentPartConstructor parentPartConstructor) {
        super(type, rel, defaultName, noArgConstructor, null, parentPartConstructor);
        XWPFRelation._table.put(rel, this);
    }
    
    public static XWPFRelation getInstance(final String rel) {
        return XWPFRelation._table.get(rel);
    }
    
    static {
        _table = new HashMap<String, XWPFRelation>();
        DOCUMENT = new XWPFRelation("application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument", "/word/document.xml");
        TEMPLATE = new XWPFRelation("application/vnd.openxmlformats-officedocument.wordprocessingml.template.main+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument", "/word/document.xml");
        MACRO_DOCUMENT = new XWPFRelation("application/vnd.ms-word.document.macroEnabled.main+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument", "/word/document.xml");
        MACRO_TEMPLATE_DOCUMENT = new XWPFRelation("application/vnd.ms-word.template.macroEnabledTemplate.main+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument", "/word/document.xml");
        GLOSSARY_DOCUMENT = new XWPFRelation("application/vnd.openxmlformats-officedocument.wordprocessingml.document.glossary+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/glossaryDocument", "/word/glossary/document.xml");
        NUMBERING = new XWPFRelation("application/vnd.openxmlformats-officedocument.wordprocessingml.numbering+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/numbering", "/word/numbering.xml", XWPFNumbering::new, XWPFNumbering::new);
        FONT_TABLE = new XWPFRelation("application/vnd.openxmlformats-officedocument.wordprocessingml.fontTable+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/fontTable", "/word/fontTable.xml");
        SETTINGS = new XWPFRelation("application/vnd.openxmlformats-officedocument.wordprocessingml.settings+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/settings", "/word/settings.xml", XWPFSettings::new, XWPFSettings::new);
        STYLES = new XWPFRelation("application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles", "/word/styles.xml", XWPFStyles::new, XWPFStyles::new);
        WEB_SETTINGS = new XWPFRelation("application/vnd.openxmlformats-officedocument.wordprocessingml.webSettings+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/webSettings", "/word/webSettings.xml");
        HEADER = new XWPFRelation("application/vnd.openxmlformats-officedocument.wordprocessingml.header+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/header", "/word/header#.xml", XWPFHeader::new, XWPFHeader::new);
        FOOTER = new XWPFRelation("application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/footer", "/word/footer#.xml", XWPFFooter::new, XWPFFooter::new);
        THEME = new XWPFRelation("application/vnd.openxmlformats-officedocument.theme+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/theme", "/word/theme/theme#.xml");
        WORKBOOK = new XWPFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/package", "/word/embeddings/Microsoft_Excel_Worksheet#.xlsx", XSSFWorkbook::new, XSSFWorkbook::new);
        CHART = new XWPFRelation("application/vnd.openxmlformats-officedocument.drawingml.chart+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/chart", "/word/charts/chart#.xml", XWPFChart::new, XWPFChart::new);
        HYPERLINK = new XWPFRelation(null, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink", null);
        COMMENT = new XWPFRelation(null, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/comments", null);
        FOOTNOTE = new XWPFRelation("application/vnd.openxmlformats-officedocument.wordprocessingml.footnotes+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/footnotes", "/word/footnotes.xml", XWPFFootnotes::new, XWPFFootnotes::new);
        ENDNOTE = new XWPFRelation("application/vnd.openxmlformats-officedocument.wordprocessingml.endnotes+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/endnotes", "/word/endnotes.xml", XWPFEndnotes::new, XWPFEndnotes::new);
        IMAGE_EMF = new XWPFRelation("image/x-emf", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/word/media/image#.emf", XWPFPictureData::new, XWPFPictureData::new);
        IMAGE_WMF = new XWPFRelation("image/x-wmf", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/word/media/image#.wmf", XWPFPictureData::new, XWPFPictureData::new);
        IMAGE_PICT = new XWPFRelation("image/pict", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/word/media/image#.pict", XWPFPictureData::new, XWPFPictureData::new);
        IMAGE_JPEG = new XWPFRelation("image/jpeg", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/word/media/image#.jpeg", XWPFPictureData::new, XWPFPictureData::new);
        IMAGE_PNG = new XWPFRelation("image/png", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/word/media/image#.png", XWPFPictureData::new, XWPFPictureData::new);
        IMAGE_DIB = new XWPFRelation("image/dib", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/word/media/image#.dib", XWPFPictureData::new, XWPFPictureData::new);
        IMAGE_GIF = new XWPFRelation("image/gif", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/word/media/image#.gif", XWPFPictureData::new, XWPFPictureData::new);
        IMAGE_TIFF = new XWPFRelation("image/tiff", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/word/media/image#.tiff", XWPFPictureData::new, XWPFPictureData::new);
        IMAGE_EPS = new XWPFRelation("image/x-eps", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/word/media/image#.eps", XWPFPictureData::new, XWPFPictureData::new);
        IMAGE_BMP = new XWPFRelation("image/x-ms-bmp", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/word/media/image#.bmp", XWPFPictureData::new, XWPFPictureData::new);
        IMAGE_WPG = new XWPFRelation("image/x-wpg", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/word/media/image#.wpg", XWPFPictureData::new, XWPFPictureData::new);
        IMAGES = new XWPFRelation(null, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", null, XWPFPictureData::new, XWPFPictureData::new);
    }
}
