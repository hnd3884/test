package org.apache.poi.xssf.extractor;

import org.apache.poi.util.POILogFactory;
import org.xml.sax.SAXException;
import org.apache.poi.xssf.binary.XSSFBHyperlinksTable;
import org.apache.poi.xssf.eventusermodel.XSSFBReader;
import org.apache.poi.xssf.binary.XSSFBSharedStringsTable;
import org.apache.poi.xssf.binary.XSSFBSheetHandler;
import org.apache.poi.ss.usermodel.DataFormatter;
import java.io.InputStream;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.binary.XSSFBCommentsTable;
import org.apache.poi.xssf.binary.XSSFBStylesTable;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.extractor.ExcelExtractor;

public class XSSFBEventBasedExcelExtractor extends XSSFEventBasedExcelExtractor implements ExcelExtractor
{
    private static final POILogger LOGGER;
    public static final XSSFRelation[] SUPPORTED_TYPES;
    private boolean handleHyperlinksInCells;
    
    public XSSFBEventBasedExcelExtractor(final String path) throws XmlException, OpenXML4JException, IOException {
        super(path);
    }
    
    public XSSFBEventBasedExcelExtractor(final OPCPackage container) throws XmlException, OpenXML4JException, IOException {
        super(container);
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("  XSSFBEventBasedExcelExtractor <filename.xlsb>");
            System.exit(1);
        }
        final POIXMLTextExtractor extractor = new XSSFBEventBasedExcelExtractor(args[0]);
        System.out.println(extractor.getText());
        extractor.close();
    }
    
    public void setHandleHyperlinksInCells(final boolean handleHyperlinksInCells) {
        this.handleHyperlinksInCells = handleHyperlinksInCells;
    }
    
    @Override
    public void setFormulasNotResults(final boolean formulasNotResults) {
        throw new IllegalArgumentException("Not currently supported");
    }
    
    public void processSheet(final XSSFSheetXMLHandler.SheetContentsHandler sheetContentsExtractor, final XSSFBStylesTable styles, final XSSFBCommentsTable comments, final SharedStrings strings, final InputStream sheetInputStream) throws IOException {
        DataFormatter formatter;
        if (this.getLocale() == null) {
            formatter = new DataFormatter();
        }
        else {
            formatter = new DataFormatter(this.getLocale());
        }
        final XSSFBSheetHandler xssfbSheetHandler = new XSSFBSheetHandler(sheetInputStream, styles, comments, strings, sheetContentsExtractor, formatter, this.getFormulasNotResults());
        xssfbSheetHandler.parse();
    }
    
    @Override
    public String getText() {
        try {
            final XSSFBSharedStringsTable strings = new XSSFBSharedStringsTable(this.getPackage());
            final XSSFBReader xssfbReader = new XSSFBReader(this.getPackage());
            final XSSFBStylesTable styles = xssfbReader.getXSSFBStylesTable();
            final XSSFBReader.SheetIterator iter = (XSSFBReader.SheetIterator)xssfbReader.getSheetsData();
            final StringBuilder text = new StringBuilder(64);
            final SheetTextExtractor sheetExtractor = new SheetTextExtractor();
            XSSFBHyperlinksTable hyperlinksTable = null;
            while (iter.hasNext()) {
                final InputStream stream = iter.next();
                if (this.getIncludeSheetNames()) {
                    text.append(iter.getSheetName());
                    text.append('\n');
                }
                if (this.handleHyperlinksInCells) {
                    hyperlinksTable = new XSSFBHyperlinksTable(iter.getSheetPart());
                }
                final XSSFBCommentsTable comments = this.getIncludeCellComments() ? iter.getXSSFBSheetComments() : null;
                this.processSheet(sheetExtractor, styles, comments, strings, stream);
                if (this.getIncludeHeadersFooters()) {
                    sheetExtractor.appendHeaderText(text);
                }
                sheetExtractor.appendCellText(text);
                if (this.getIncludeTextBoxes()) {
                    this.processShapes(iter.getShapes(), text);
                }
                if (this.getIncludeHeadersFooters()) {
                    sheetExtractor.appendFooterText(text);
                }
                sheetExtractor.reset();
                stream.close();
            }
            return text.toString();
        }
        catch (final IOException | OpenXML4JException | SAXException e) {
            XSSFBEventBasedExcelExtractor.LOGGER.log(5, new Object[] { e });
            return null;
        }
    }
    
    static {
        LOGGER = POILogFactory.getLogger((Class)XSSFBEventBasedExcelExtractor.class);
        SUPPORTED_TYPES = new XSSFRelation[] { XSSFRelation.XLSB_BINARY_WORKBOOK };
    }
}
