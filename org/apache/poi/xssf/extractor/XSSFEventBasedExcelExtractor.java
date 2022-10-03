package org.apache.poi.xssf.extractor;

import org.apache.poi.xssf.usermodel.XSSFComment;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.util.POILogFactory;
import java.util.Iterator;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import org.apache.poi.xssf.usermodel.XSSFShape;
import java.util.List;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.util.XMLHelper;
import org.xml.sax.InputSource;
import org.apache.poi.ss.usermodel.DataFormatter;
import java.io.InputStream;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.Comments;
import org.apache.poi.xssf.model.Styles;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.ooxml.POIXMLDocument;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import java.util.Locale;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.extractor.ExcelExtractor;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;

public class XSSFEventBasedExcelExtractor extends POIXMLTextExtractor implements ExcelExtractor
{
    private static final POILogger LOGGER;
    protected OPCPackage container;
    protected POIXMLProperties properties;
    protected Locale locale;
    protected boolean includeTextBoxes;
    protected boolean includeSheetNames;
    protected boolean includeCellComments;
    protected boolean includeHeadersFooters;
    protected boolean formulasNotResults;
    protected boolean concatenatePhoneticRuns;
    
    public XSSFEventBasedExcelExtractor(final String path) throws XmlException, OpenXML4JException, IOException {
        this(OPCPackage.open(path));
    }
    
    public XSSFEventBasedExcelExtractor(final OPCPackage container) throws XmlException, OpenXML4JException, IOException {
        super(null);
        this.includeTextBoxes = true;
        this.includeSheetNames = true;
        this.includeHeadersFooters = true;
        this.concatenatePhoneticRuns = true;
        this.container = container;
        this.properties = new POIXMLProperties(container);
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("  XSSFEventBasedExcelExtractor <filename.xlsx>");
            System.exit(1);
        }
        final POIXMLTextExtractor extractor = new XSSFEventBasedExcelExtractor(args[0]);
        System.out.println(extractor.getText());
        extractor.close();
    }
    
    public void setIncludeSheetNames(final boolean includeSheetNames) {
        this.includeSheetNames = includeSheetNames;
    }
    
    public boolean getIncludeSheetNames() {
        return this.includeSheetNames;
    }
    
    public void setFormulasNotResults(final boolean formulasNotResults) {
        this.formulasNotResults = formulasNotResults;
    }
    
    public boolean getFormulasNotResults() {
        return this.formulasNotResults;
    }
    
    public void setIncludeHeadersFooters(final boolean includeHeadersFooters) {
        this.includeHeadersFooters = includeHeadersFooters;
    }
    
    public boolean getIncludeHeadersFooters() {
        return this.includeHeadersFooters;
    }
    
    public void setIncludeTextBoxes(final boolean includeTextBoxes) {
        this.includeTextBoxes = includeTextBoxes;
    }
    
    public boolean getIncludeTextBoxes() {
        return this.includeTextBoxes;
    }
    
    public void setIncludeCellComments(final boolean includeCellComments) {
        this.includeCellComments = includeCellComments;
    }
    
    public boolean getIncludeCellComments() {
        return this.includeCellComments;
    }
    
    public void setConcatenatePhoneticRuns(final boolean concatenatePhoneticRuns) {
        this.concatenatePhoneticRuns = concatenatePhoneticRuns;
    }
    
    public void setLocale(final Locale locale) {
        this.locale = locale;
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    @Override
    public OPCPackage getPackage() {
        return this.container;
    }
    
    @Override
    public POIXMLProperties.CoreProperties getCoreProperties() {
        return this.properties.getCoreProperties();
    }
    
    @Override
    public POIXMLProperties.ExtendedProperties getExtendedProperties() {
        return this.properties.getExtendedProperties();
    }
    
    @Override
    public POIXMLProperties.CustomProperties getCustomProperties() {
        return this.properties.getCustomProperties();
    }
    
    public void processSheet(final XSSFSheetXMLHandler.SheetContentsHandler sheetContentsExtractor, final Styles styles, final Comments comments, final SharedStrings strings, final InputStream sheetInputStream) throws IOException, SAXException {
        DataFormatter formatter;
        if (this.locale == null) {
            formatter = new DataFormatter();
        }
        else {
            formatter = new DataFormatter(this.locale);
        }
        final InputSource sheetSource = new InputSource(sheetInputStream);
        try {
            final XMLReader sheetParser = XMLHelper.newXMLReader();
            final ContentHandler handler = new XSSFSheetXMLHandler(styles, comments, strings, sheetContentsExtractor, formatter, this.formulasNotResults);
            sheetParser.setContentHandler(handler);
            sheetParser.parse(sheetSource);
        }
        catch (final ParserConfigurationException e) {
            throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
        }
    }
    
    protected SharedStrings createSharedStringsTable(final XSSFReader xssfReader, final OPCPackage container) throws IOException, SAXException {
        return new ReadOnlySharedStringsTable(container, this.concatenatePhoneticRuns);
    }
    
    public String getText() {
        try {
            final XSSFReader xssfReader = new XSSFReader(this.container);
            final SharedStrings strings = this.createSharedStringsTable(xssfReader, this.container);
            final StylesTable styles = xssfReader.getStylesTable();
            final XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator)xssfReader.getSheetsData();
            final StringBuilder text = new StringBuilder(64);
            final SheetTextExtractor sheetExtractor = new SheetTextExtractor();
            while (iter.hasNext()) {
                final InputStream stream = iter.next();
                if (this.includeSheetNames) {
                    text.append(iter.getSheetName());
                    text.append('\n');
                }
                final Comments comments = this.includeCellComments ? iter.getSheetComments() : null;
                this.processSheet(sheetExtractor, styles, comments, strings, stream);
                if (this.includeHeadersFooters) {
                    sheetExtractor.appendHeaderText(text);
                }
                sheetExtractor.appendCellText(text);
                if (this.includeTextBoxes) {
                    this.processShapes(iter.getShapes(), text);
                }
                if (this.includeHeadersFooters) {
                    sheetExtractor.appendFooterText(text);
                }
                sheetExtractor.reset();
                stream.close();
            }
            return text.toString();
        }
        catch (final IOException | OpenXML4JException | SAXException e) {
            XSSFEventBasedExcelExtractor.LOGGER.log(5, new Object[] { e });
            return null;
        }
    }
    
    void processShapes(final List<XSSFShape> shapes, final StringBuilder text) {
        if (shapes == null) {
            return;
        }
        for (final XSSFShape shape : shapes) {
            if (shape instanceof XSSFSimpleShape) {
                final String sText = ((XSSFSimpleShape)shape).getText();
                if (sText == null || sText.length() <= 0) {
                    continue;
                }
                text.append(sText).append('\n');
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.container != null) {
            this.container.close();
            this.container = null;
        }
        super.close();
    }
    
    static {
        LOGGER = POILogFactory.getLogger((Class)XSSFEventBasedExcelExtractor.class);
    }
    
    protected class SheetTextExtractor implements XSSFSheetXMLHandler.SheetContentsHandler
    {
        private final StringBuilder output;
        private boolean firstCellOfRow;
        private final Map<String, String> headerFooterMap;
        
        protected SheetTextExtractor() {
            this.output = new StringBuilder(64);
            this.firstCellOfRow = true;
            this.headerFooterMap = (XSSFEventBasedExcelExtractor.this.includeHeadersFooters ? new HashMap<String, String>() : null);
        }
        
        @Override
        public void startRow(final int rowNum) {
            this.firstCellOfRow = true;
        }
        
        @Override
        public void endRow(final int rowNum) {
            this.output.append('\n');
        }
        
        @Override
        public void cell(final String cellRef, final String formattedValue, final XSSFComment comment) {
            if (this.firstCellOfRow) {
                this.firstCellOfRow = false;
            }
            else {
                this.output.append('\t');
            }
            if (formattedValue != null) {
                POIXMLTextExtractor.this.checkMaxTextSize(this.output, formattedValue);
                this.output.append(formattedValue);
            }
            if (XSSFEventBasedExcelExtractor.this.includeCellComments && comment != null) {
                final String commentText = comment.getString().getString().replace('\n', ' ');
                this.output.append((formattedValue != null) ? " Comment by " : "Comment by ");
                POIXMLTextExtractor.this.checkMaxTextSize(this.output, commentText);
                if (commentText.startsWith(comment.getAuthor() + ": ")) {
                    this.output.append(commentText);
                }
                else {
                    this.output.append(comment.getAuthor()).append(": ").append(commentText);
                }
            }
        }
        
        @Override
        public void headerFooter(final String text, final boolean isHeader, final String tagName) {
            if (this.headerFooterMap != null) {
                this.headerFooterMap.put(tagName, text);
            }
        }
        
        private void appendHeaderFooterText(final StringBuilder buffer, final String name) {
            String text = this.headerFooterMap.get(name);
            if (text != null && text.length() > 0) {
                text = this.handleHeaderFooterDelimiter(text, "&L");
                text = this.handleHeaderFooterDelimiter(text, "&C");
                text = this.handleHeaderFooterDelimiter(text, "&R");
                buffer.append(text).append('\n');
            }
        }
        
        private String handleHeaderFooterDelimiter(String text, final String delimiter) {
            final int index = text.indexOf(delimiter);
            if (index == 0) {
                text = text.substring(2);
            }
            else if (index > 0) {
                text = text.substring(0, index) + "\t" + text.substring(index + 2);
            }
            return text;
        }
        
        void appendHeaderText(final StringBuilder buffer) {
            this.appendHeaderFooterText(buffer, "firstHeader");
            this.appendHeaderFooterText(buffer, "oddHeader");
            this.appendHeaderFooterText(buffer, "evenHeader");
        }
        
        void appendFooterText(final StringBuilder buffer) {
            this.appendHeaderFooterText(buffer, "firstFooter");
            this.appendHeaderFooterText(buffer, "oddFooter");
            this.appendHeaderFooterText(buffer, "evenFooter");
        }
        
        void appendCellText(final StringBuilder buffer) {
            POIXMLTextExtractor.this.checkMaxTextSize(buffer, this.output.toString());
            buffer.append((CharSequence)this.output);
        }
        
        void reset() {
            this.output.setLength(0);
            this.firstCellOfRow = true;
            if (this.headerFooterMap != null) {
                this.headerFooterMap.clear();
            }
        }
    }
}
