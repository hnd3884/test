package org.apache.poi.xssf.eventusermodel;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import java.util.LinkedList;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.xssf.model.CommentsTable;
import java.util.List;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.util.XMLHelper;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.util.POILogFactory;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import java.io.InputStream;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.util.ArrayList;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.POILogger;
import java.util.Set;

public class XSSFReader
{
    private static final Set<String> WORKSHEET_RELS;
    private static final POILogger LOGGER;
    protected OPCPackage pkg;
    protected PackagePart workbookPart;
    
    public XSSFReader(final OPCPackage pkg) throws IOException, OpenXML4JException {
        this.pkg = pkg;
        final PackageRelationship coreDocRelationship = this.pkg.getRelationshipsByType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument").getRelationship(0);
        if (coreDocRelationship != null) {
            this.workbookPart = this.pkg.getPart(coreDocRelationship);
            return;
        }
        if (this.pkg.getRelationshipsByType("http://purl.oclc.org/ooxml/officeDocument/relationships/officeDocument").getRelationship(0) != null) {
            throw new POIXMLException("Strict OOXML isn't currently supported, please see bug #57699");
        }
        throw new POIXMLException("OOXML file structure broken/invalid - no core document found!");
    }
    
    public SharedStringsTable getSharedStringsTable() throws IOException, InvalidFormatException {
        final ArrayList<PackagePart> parts = this.pkg.getPartsByContentType(XSSFRelation.SHARED_STRINGS.getContentType());
        return (parts.size() == 0) ? null : new SharedStringsTable(parts.get(0));
    }
    
    public StylesTable getStylesTable() throws IOException, InvalidFormatException {
        ArrayList<PackagePart> parts = this.pkg.getPartsByContentType(XSSFRelation.STYLES.getContentType());
        if (parts.size() == 0) {
            return null;
        }
        final StylesTable styles = new StylesTable(parts.get(0));
        parts = this.pkg.getPartsByContentType(XSSFRelation.THEME.getContentType());
        if (parts.size() != 0) {
            styles.setTheme(new ThemesTable(parts.get(0)));
        }
        return styles;
    }
    
    public InputStream getSharedStringsData() throws IOException, InvalidFormatException {
        return XSSFRelation.SHARED_STRINGS.getContents(this.workbookPart);
    }
    
    public InputStream getStylesData() throws IOException, InvalidFormatException {
        return XSSFRelation.STYLES.getContents(this.workbookPart);
    }
    
    public InputStream getThemesData() throws IOException, InvalidFormatException {
        return XSSFRelation.THEME.getContents(this.workbookPart);
    }
    
    public InputStream getWorkbookData() throws IOException, InvalidFormatException {
        return this.workbookPart.getInputStream();
    }
    
    public InputStream getSheet(final String relId) throws IOException, InvalidFormatException {
        final PackageRelationship rel = this.workbookPart.getRelationship(relId);
        if (rel == null) {
            throw new IllegalArgumentException("No Sheet found with r:id " + relId);
        }
        final PackagePartName relName = PackagingURIHelper.createPartName(rel.getTargetURI());
        final PackagePart sheet = this.pkg.getPart(relName);
        if (sheet == null) {
            throw new IllegalArgumentException("No data found for Sheet with r:id " + relId);
        }
        return sheet.getInputStream();
    }
    
    public Iterator<InputStream> getSheetsData() throws IOException, InvalidFormatException {
        return new SheetIterator(this.workbookPart);
    }
    
    static {
        WORKSHEET_RELS = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList(XSSFRelation.WORKSHEET.getRelation(), XSSFRelation.CHARTSHEET.getRelation())));
        LOGGER = POILogFactory.getLogger((Class)XSSFReader.class);
    }
    
    public static class SheetIterator implements Iterator<InputStream>
    {
        private final Map<String, PackagePart> sheetMap;
        XSSFSheetRef xssfSheetRef;
        final Iterator<XSSFSheetRef> sheetIterator;
        
        SheetIterator(final PackagePart wb) throws IOException {
            try {
                this.sheetMap = new HashMap<String, PackagePart>();
                final OPCPackage pkg = wb.getPackage();
                final Set<String> worksheetRels = this.getSheetRelationships();
                for (final PackageRelationship rel : wb.getRelationships()) {
                    final String relType = rel.getRelationshipType();
                    if (worksheetRels.contains(relType)) {
                        final PackagePartName relName = PackagingURIHelper.createPartName(rel.getTargetURI());
                        this.sheetMap.put(rel.getId(), pkg.getPart(relName));
                    }
                }
                this.sheetIterator = this.createSheetIteratorFromWB(wb);
            }
            catch (final InvalidFormatException e) {
                throw new POIXMLException(e);
            }
        }
        
        Iterator<XSSFSheetRef> createSheetIteratorFromWB(final PackagePart wb) throws IOException {
            final XMLSheetRefReader xmlSheetRefReader = new XMLSheetRefReader();
            XMLReader xmlReader;
            try {
                xmlReader = XMLHelper.newXMLReader();
            }
            catch (final ParserConfigurationException | SAXException e) {
                throw new POIXMLException(e);
            }
            xmlReader.setContentHandler(xmlSheetRefReader);
            try {
                xmlReader.parse(new InputSource(wb.getInputStream()));
            }
            catch (final SAXException e2) {
                throw new POIXMLException(e2);
            }
            final List<XSSFSheetRef> validSheets = new ArrayList<XSSFSheetRef>();
            for (final XSSFSheetRef xssfSheetRef : xmlSheetRefReader.getSheetRefs()) {
                final String sheetId = xssfSheetRef.getId();
                if (sheetId != null && sheetId.length() > 0) {
                    validSheets.add(xssfSheetRef);
                }
            }
            return validSheets.iterator();
        }
        
        Set<String> getSheetRelationships() {
            return XSSFReader.WORKSHEET_RELS;
        }
        
        @Override
        public boolean hasNext() {
            return this.sheetIterator.hasNext();
        }
        
        @Override
        public InputStream next() {
            this.xssfSheetRef = this.sheetIterator.next();
            final String sheetId = this.xssfSheetRef.getId();
            try {
                final PackagePart sheetPkg = this.sheetMap.get(sheetId);
                return sheetPkg.getInputStream();
            }
            catch (final IOException e) {
                throw new POIXMLException(e);
            }
        }
        
        public String getSheetName() {
            return this.xssfSheetRef.getName();
        }
        
        public CommentsTable getSheetComments() {
            final PackagePart sheetPkg = this.getSheetPart();
            try {
                final PackageRelationshipCollection commentsList = sheetPkg.getRelationshipsByType(XSSFRelation.SHEET_COMMENTS.getRelation());
                if (commentsList.size() > 0) {
                    final PackageRelationship comments = commentsList.getRelationship(0);
                    final PackagePartName commentsName = PackagingURIHelper.createPartName(comments.getTargetURI());
                    final PackagePart commentsPart = sheetPkg.getPackage().getPart(commentsName);
                    return new CommentsTable(commentsPart);
                }
            }
            catch (final InvalidFormatException | IOException e) {
                XSSFReader.LOGGER.log(5, new Object[] { e });
                return null;
            }
            return null;
        }
        
        public List<XSSFShape> getShapes() {
            final PackagePart sheetPkg = this.getSheetPart();
            final List<XSSFShape> shapes = new LinkedList<XSSFShape>();
            try {
                final PackageRelationshipCollection drawingsList = sheetPkg.getRelationshipsByType(XSSFRelation.DRAWINGS.getRelation());
                for (int i = 0; i < drawingsList.size(); ++i) {
                    final PackageRelationship drawings = drawingsList.getRelationship(i);
                    final PackagePartName drawingsName = PackagingURIHelper.createPartName(drawings.getTargetURI());
                    final PackagePart drawingsPart = sheetPkg.getPackage().getPart(drawingsName);
                    if (drawingsPart == null) {
                        XSSFReader.LOGGER.log(5, new Object[] { "Missing drawing: " + drawingsName + ". Skipping it." });
                    }
                    else {
                        final XSSFDrawing drawing = new XSSFDrawing(drawingsPart);
                        shapes.addAll(drawing.getShapes());
                    }
                }
            }
            catch (final XmlException | InvalidFormatException | IOException e) {
                XSSFReader.LOGGER.log(5, new Object[] { e });
                return null;
            }
            return shapes;
        }
        
        public PackagePart getSheetPart() {
            final String sheetId = this.xssfSheetRef.getId();
            return this.sheetMap.get(sheetId);
        }
        
        @Override
        public void remove() {
            throw new IllegalStateException("Not supported");
        }
    }
    
    protected static final class XSSFSheetRef
    {
        private final String id;
        private final String name;
        
        public XSSFSheetRef(final String id, final String name) {
            this.id = id;
            this.name = name;
        }
        
        public String getId() {
            return this.id;
        }
        
        public String getName() {
            return this.name;
        }
    }
    
    private static class XMLSheetRefReader extends DefaultHandler
    {
        private static final String SHEET = "sheet";
        private static final String ID = "id";
        private static final String NAME = "name";
        private final List<XSSFSheetRef> sheetRefs;
        
        private XMLSheetRefReader() {
            this.sheetRefs = new LinkedList<XSSFSheetRef>();
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attrs) throws SAXException {
            if (localName.equalsIgnoreCase("sheet")) {
                String name = null;
                String id = null;
                for (int i = 0; i < attrs.getLength(); ++i) {
                    final String attrName = attrs.getLocalName(i);
                    if (attrName.equalsIgnoreCase("name")) {
                        name = attrs.getValue(i);
                    }
                    else if (attrName.equalsIgnoreCase("id")) {
                        id = attrs.getValue(i);
                    }
                    if (name != null && id != null) {
                        this.sheetRefs.add(new XSSFSheetRef(id, name));
                        break;
                    }
                }
            }
        }
        
        List<XSSFSheetRef> getSheetRefs() {
            return Collections.unmodifiableList((List<? extends XSSFSheetRef>)this.sheetRefs);
        }
    }
}
