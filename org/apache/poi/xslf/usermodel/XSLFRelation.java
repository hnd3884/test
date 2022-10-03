package org.apache.poi.xslf.usermodel;

import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ooxml.POIXMLRelation;

public final class XSLFRelation extends POIXMLRelation
{
    static final String NS_DRAWINGML = "http://schemas.openxmlformats.org/drawingml/2006/main";
    private static final Map<String, XSLFRelation> _table;
    public static final XSLFRelation MAIN;
    public static final XSLFRelation MACRO;
    public static final XSLFRelation MACRO_TEMPLATE;
    public static final XSLFRelation PRESENTATIONML;
    public static final XSLFRelation PRESENTATIONML_TEMPLATE;
    public static final XSLFRelation PRESENTATION_MACRO;
    public static final XSLFRelation THEME_MANAGER;
    public static final XSLFRelation NOTES;
    public static final XSLFRelation SLIDE;
    public static final XSLFRelation SLIDE_LAYOUT;
    public static final XSLFRelation SLIDE_MASTER;
    public static final XSLFRelation NOTES_MASTER;
    public static final XSLFRelation COMMENTS;
    public static final XSLFRelation COMMENT_AUTHORS;
    public static final XSLFRelation HYPERLINK;
    public static final XSLFRelation THEME;
    public static final XSLFRelation VML_DRAWING;
    public static final XSLFRelation WORKBOOK;
    public static final XSLFRelation CHART;
    public static final XSLFRelation IMAGE_EMF;
    public static final XSLFRelation IMAGE_WMF;
    public static final XSLFRelation IMAGE_PICT;
    public static final XSLFRelation IMAGE_JPEG;
    public static final XSLFRelation IMAGE_PNG;
    public static final XSLFRelation IMAGE_DIB;
    public static final XSLFRelation IMAGE_GIF;
    public static final XSLFRelation IMAGE_TIFF;
    public static final XSLFRelation IMAGE_EPS;
    public static final XSLFRelation IMAGE_BMP;
    public static final XSLFRelation IMAGE_WPG;
    public static final XSLFRelation IMAGE_WDP;
    public static final XSLFRelation IMAGE_SVG;
    public static final XSLFRelation IMAGES;
    public static final XSLFRelation TABLE_STYLES;
    public static final XSLFRelation OLE_OBJECT;
    public static final XSLFRelation FONT;
    
    private XSLFRelation(final String type) {
        this(type, null, null, null, null);
    }
    
    private XSLFRelation(final String type, final String rel, final String defaultName) {
        this(type, rel, defaultName, null, null);
    }
    
    private XSLFRelation(final String type, final String rel, final String defaultName, final NoArgConstructor noArgConstructor, final PackagePartConstructor packagePartConstructor) {
        super(type, rel, defaultName, noArgConstructor, packagePartConstructor, null);
        XSLFRelation._table.put(rel, this);
    }
    
    public static XSLFRelation getInstance(final String rel) {
        return XSLFRelation._table.get(rel);
    }
    
    static {
        _table = new HashMap<String, XSLFRelation>();
        MAIN = new XSLFRelation("application/vnd.openxmlformats-officedocument.presentationml.presentation.main+xml");
        MACRO = new XSLFRelation("application/vnd.ms-powerpoint.slideshow.macroEnabled.main+xml");
        MACRO_TEMPLATE = new XSLFRelation("application/vnd.ms-powerpoint.template.macroEnabled.main+xml");
        PRESENTATIONML = new XSLFRelation("application/vnd.openxmlformats-officedocument.presentationml.slideshow.main+xml");
        PRESENTATIONML_TEMPLATE = new XSLFRelation("application/vnd.openxmlformats-officedocument.presentationml.template.main+xml");
        PRESENTATION_MACRO = new XSLFRelation("application/vnd.ms-powerpoint.presentation.macroEnabled.main+xml");
        THEME_MANAGER = new XSLFRelation("application/vnd.openxmlformats-officedocument.themeManager+xml");
        NOTES = new XSLFRelation("application/vnd.openxmlformats-officedocument.presentationml.notesSlide+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/notesSlide", "/ppt/notesSlides/notesSlide#.xml", XSLFNotes::new, XSLFNotes::new);
        SLIDE = new XSLFRelation("application/vnd.openxmlformats-officedocument.presentationml.slide+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/slide", "/ppt/slides/slide#.xml", XSLFSlide::new, XSLFSlide::new);
        SLIDE_LAYOUT = new XSLFRelation("application/vnd.openxmlformats-officedocument.presentationml.slideLayout+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideLayout", "/ppt/slideLayouts/slideLayout#.xml", null, XSLFSlideLayout::new);
        SLIDE_MASTER = new XSLFRelation("application/vnd.openxmlformats-officedocument.presentationml.slideMaster+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideMaster", "/ppt/slideMasters/slideMaster#.xml", null, XSLFSlideMaster::new);
        NOTES_MASTER = new XSLFRelation("application/vnd.openxmlformats-officedocument.presentationml.notesMaster+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/notesMaster", "/ppt/notesMasters/notesMaster#.xml", XSLFNotesMaster::new, XSLFNotesMaster::new);
        COMMENTS = new XSLFRelation("application/vnd.openxmlformats-officedocument.presentationml.comments+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/comments", "/ppt/comments/comment#.xml", XSLFComments::new, XSLFComments::new);
        COMMENT_AUTHORS = new XSLFRelation("application/vnd.openxmlformats-officedocument.presentationml.commentAuthors+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/commentAuthors", "/ppt/commentAuthors.xml", XSLFCommentAuthors::new, XSLFCommentAuthors::new);
        HYPERLINK = new XSLFRelation(null, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink", null);
        THEME = new XSLFRelation("application/vnd.openxmlformats-officedocument.theme+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/theme", "/ppt/theme/theme#.xml", XSLFTheme::new, XSLFTheme::new);
        VML_DRAWING = new XSLFRelation("application/vnd.openxmlformats-officedocument.vmlDrawing", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/vmlDrawing", "/ppt/drawings/vmlDrawing#.vml");
        WORKBOOK = new XSLFRelation("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/package", "/ppt/embeddings/Microsoft_Excel_Worksheet#.xlsx", XSSFWorkbook::new, XSSFWorkbook::new);
        CHART = new XSLFRelation("application/vnd.openxmlformats-officedocument.drawingml.chart+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/chart", "/ppt/charts/chart#.xml", XSLFChart::new, XSLFChart::new);
        IMAGE_EMF = new XSLFRelation(PictureData.PictureType.EMF.contentType, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/ppt/media/image#.emf", XSLFPictureData::new, XSLFPictureData::new);
        IMAGE_WMF = new XSLFRelation(PictureData.PictureType.WMF.contentType, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/ppt/media/image#.wmf", XSLFPictureData::new, XSLFPictureData::new);
        IMAGE_PICT = new XSLFRelation(PictureData.PictureType.PICT.contentType, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/ppt/media/image#.pict", XSLFPictureData::new, XSLFPictureData::new);
        IMAGE_JPEG = new XSLFRelation(PictureData.PictureType.JPEG.contentType, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/ppt/media/image#.jpeg", XSLFPictureData::new, XSLFPictureData::new);
        IMAGE_PNG = new XSLFRelation(PictureData.PictureType.PNG.contentType, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/ppt/media/image#.png", XSLFPictureData::new, XSLFPictureData::new);
        IMAGE_DIB = new XSLFRelation(PictureData.PictureType.DIB.contentType, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/ppt/media/image#.dib", XSLFPictureData::new, XSLFPictureData::new);
        IMAGE_GIF = new XSLFRelation(PictureData.PictureType.GIF.contentType, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/ppt/media/image#.gif", XSLFPictureData::new, XSLFPictureData::new);
        IMAGE_TIFF = new XSLFRelation(PictureData.PictureType.TIFF.contentType, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/ppt/media/image#.tiff", XSLFPictureData::new, XSLFPictureData::new);
        IMAGE_EPS = new XSLFRelation(PictureData.PictureType.EPS.contentType, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/ppt/media/image#.eps", XSLFPictureData::new, XSLFPictureData::new);
        IMAGE_BMP = new XSLFRelation(PictureData.PictureType.BMP.contentType, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/ppt/media/image#.bmp", XSLFPictureData::new, XSLFPictureData::new);
        IMAGE_WPG = new XSLFRelation(PictureData.PictureType.WPG.contentType, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/ppt/media/image#.wpg", XSLFPictureData::new, XSLFPictureData::new);
        IMAGE_WDP = new XSLFRelation(PictureData.PictureType.WDP.contentType, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/ppt/media/image#.wdp", XSLFPictureData::new, XSLFPictureData::new);
        IMAGE_SVG = new XSLFRelation(PictureData.PictureType.SVG.contentType, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "/ppt/media/image#.svg", XSLFPictureData::new, XSLFPictureData::new);
        IMAGES = new XSLFRelation(null, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", null, XSLFPictureData::new, XSLFPictureData::new);
        TABLE_STYLES = new XSLFRelation("application/vnd.openxmlformats-officedocument.presentationml.tableStyles+xml", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/tableStyles", "/ppt/tableStyles.xml", XSLFTableStyles::new, XSLFTableStyles::new);
        OLE_OBJECT = new XSLFRelation("application/vnd.openxmlformats-officedocument.oleObject", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/oleObject", "/ppt/embeddings/oleObject#.bin", XSLFObjectData::new, XSLFObjectData::new);
        FONT = new XSLFRelation("application/x-fontdata", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/font", "/ppt/fonts/font#.fntdata", XSLFFontData::new, XSLFFontData::new);
    }
}
