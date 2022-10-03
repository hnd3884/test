package org.apache.poi.xssf.extractor;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.ss.usermodel.Comment;
import java.util.Iterator;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.HeaderFooter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ooxml.POIXMLDocument;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.Locale;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.ss.extractor.ExcelExtractor;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;

public class XSSFExcelExtractor extends POIXMLTextExtractor implements ExcelExtractor
{
    public static final XSSFRelation[] SUPPORTED_TYPES;
    private Locale locale;
    private XSSFWorkbook workbook;
    private boolean includeSheetNames;
    private boolean formulasNotResults;
    private boolean includeCellComments;
    private boolean includeHeadersFooters;
    private boolean includeTextBoxes;
    
    public XSSFExcelExtractor(final OPCPackage container) throws XmlException, OpenXML4JException, IOException {
        this(new XSSFWorkbook(container));
    }
    
    public XSSFExcelExtractor(final XSSFWorkbook workbook) {
        super(workbook);
        this.includeSheetNames = true;
        this.includeHeadersFooters = true;
        this.includeTextBoxes = true;
        this.workbook = workbook;
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("  XSSFExcelExtractor <filename.xlsx>");
            System.exit(1);
        }
        try (final OPCPackage pkg = OPCPackage.create(args[0]);
             final POIXMLTextExtractor extractor = new XSSFExcelExtractor(pkg)) {
            System.out.println(extractor.getText());
        }
    }
    
    public void setIncludeSheetNames(final boolean includeSheetNames) {
        this.includeSheetNames = includeSheetNames;
    }
    
    public void setFormulasNotResults(final boolean formulasNotResults) {
        this.formulasNotResults = formulasNotResults;
    }
    
    public void setIncludeCellComments(final boolean includeCellComments) {
        this.includeCellComments = includeCellComments;
    }
    
    public void setIncludeHeadersFooters(final boolean includeHeadersFooters) {
        this.includeHeadersFooters = includeHeadersFooters;
    }
    
    public void setIncludeTextBoxes(final boolean includeTextBoxes) {
        this.includeTextBoxes = includeTextBoxes;
    }
    
    public void setLocale(final Locale locale) {
        this.locale = locale;
    }
    
    public String getText() {
        DataFormatter formatter;
        if (this.locale == null) {
            formatter = new DataFormatter();
        }
        else {
            formatter = new DataFormatter(this.locale);
        }
        final StringBuilder text = new StringBuilder(64);
        for (final Sheet sh : this.workbook) {
            final XSSFSheet sheet = (XSSFSheet)sh;
            if (this.includeSheetNames) {
                text.append(sheet.getSheetName()).append("\n");
            }
            if (this.includeHeadersFooters) {
                text.append(this.extractHeaderFooter((HeaderFooter)sheet.getFirstHeader()));
                text.append(this.extractHeaderFooter((HeaderFooter)sheet.getOddHeader()));
                text.append(this.extractHeaderFooter((HeaderFooter)sheet.getEvenHeader()));
            }
            for (final Object rawR : sheet) {
                final Row row = (Row)rawR;
                final Iterator<Cell> ri = row.cellIterator();
                while (ri.hasNext()) {
                    final Cell cell = ri.next();
                    if (cell.getCellType() == CellType.FORMULA) {
                        if (this.formulasNotResults) {
                            final String contents = cell.getCellFormula();
                            this.checkMaxTextSize(text, contents);
                            text.append(contents);
                        }
                        else if (cell.getCachedFormulaResultType() == CellType.STRING) {
                            this.handleStringCell(text, cell);
                        }
                        else {
                            this.handleNonStringCell(text, cell, formatter);
                        }
                    }
                    else if (cell.getCellType() == CellType.STRING) {
                        this.handleStringCell(text, cell);
                    }
                    else {
                        this.handleNonStringCell(text, cell, formatter);
                    }
                    final Comment comment = cell.getCellComment();
                    if (this.includeCellComments && comment != null) {
                        final String commentText = comment.getString().getString().replace('\n', ' ');
                        this.checkMaxTextSize(text, commentText);
                        text.append(" Comment by ").append(comment.getAuthor()).append(": ").append(commentText);
                    }
                    if (ri.hasNext()) {
                        text.append("\t");
                    }
                }
                text.append("\n");
            }
            if (this.includeTextBoxes) {
                final XSSFDrawing drawing = sheet.getDrawingPatriarch();
                if (drawing != null) {
                    for (final XSSFShape shape : drawing.getShapes()) {
                        if (shape instanceof XSSFSimpleShape) {
                            final String boxText = ((XSSFSimpleShape)shape).getText();
                            if (boxText.length() <= 0) {
                                continue;
                            }
                            text.append(boxText);
                            text.append('\n');
                        }
                    }
                }
            }
            if (this.includeHeadersFooters) {
                text.append(this.extractHeaderFooter((HeaderFooter)sheet.getFirstFooter()));
                text.append(this.extractHeaderFooter((HeaderFooter)sheet.getOddFooter()));
                text.append(this.extractHeaderFooter((HeaderFooter)sheet.getEvenFooter()));
            }
        }
        return text.toString();
    }
    
    private void handleStringCell(final StringBuilder text, final Cell cell) {
        final String contents = cell.getRichStringCellValue().getString();
        this.checkMaxTextSize(text, contents);
        text.append(contents);
    }
    
    private void handleNonStringCell(final StringBuilder text, final Cell cell, final DataFormatter formatter) {
        CellType type = cell.getCellType();
        if (type == CellType.FORMULA) {
            type = cell.getCachedFormulaResultType();
        }
        if (type == CellType.NUMERIC) {
            final CellStyle cs = cell.getCellStyle();
            if (cs != null && cs.getDataFormatString() != null) {
                final String contents = formatter.formatRawCellContents(cell.getNumericCellValue(), (int)cs.getDataFormat(), cs.getDataFormatString());
                this.checkMaxTextSize(text, contents);
                text.append(contents);
                return;
            }
        }
        final String contents2 = ((XSSFCell)cell).getRawValue();
        if (contents2 != null) {
            this.checkMaxTextSize(text, contents2);
            text.append(contents2);
        }
    }
    
    private String extractHeaderFooter(final HeaderFooter hf) {
        return org.apache.poi.hssf.extractor.ExcelExtractor._extractHeaderFooter(hf);
    }
    
    static {
        SUPPORTED_TYPES = new XSSFRelation[] { XSSFRelation.WORKBOOK, XSSFRelation.MACRO_TEMPLATE_WORKBOOK, XSSFRelation.MACRO_ADDIN_WORKBOOK, XSSFRelation.TEMPLATE_WORKBOOK, XSSFRelation.MACROS_WORKBOOK };
    }
}
