package org.apache.poi.xssf.streaming;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.StringCodepointsIterable;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.ss.usermodel.FormulaError;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType;
import org.apache.poi.ss.util.CellReference;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import org.apache.poi.util.TempFile;
import java.io.IOException;
import org.apache.poi.xssf.model.SharedStringsTable;
import java.io.Writer;
import java.io.File;
import org.apache.poi.util.POILogger;
import java.io.Closeable;

public class SheetDataWriter implements Closeable
{
    private static final POILogger logger;
    private final File _fd;
    private final Writer _out;
    private int _rownum;
    private int _numberOfFlushedRows;
    private int _lowestIndexOfFlushedRows;
    private int _numberOfCellsOfLastFlushedRow;
    private int _numberLastFlushedRow;
    private SharedStringsTable _sharedStringSource;
    
    public SheetDataWriter() throws IOException {
        this._numberLastFlushedRow = -1;
        this._fd = this.createTempFile();
        this._out = this.createWriter(this._fd);
    }
    
    public SheetDataWriter(final SharedStringsTable sharedStringsTable) throws IOException {
        this();
        this._sharedStringSource = sharedStringsTable;
    }
    
    public File createTempFile() throws IOException {
        return TempFile.createTempFile("poi-sxssf-sheet", ".xml");
    }
    
    public Writer createWriter(final File fd) throws IOException {
        final FileOutputStream fos = new FileOutputStream(fd);
        OutputStream decorated;
        try {
            decorated = this.decorateOutputStream(fos);
        }
        catch (final IOException e) {
            fos.close();
            throw e;
        }
        return new BufferedWriter(new OutputStreamWriter(decorated, StandardCharsets.UTF_8));
    }
    
    protected OutputStream decorateOutputStream(final FileOutputStream fos) throws IOException {
        return fos;
    }
    
    @Override
    public void close() throws IOException {
        this._out.flush();
        this._out.close();
    }
    
    protected File getTempFile() {
        return this._fd;
    }
    
    public InputStream getWorksheetXMLInputStream() throws IOException {
        final File fd = this.getTempFile();
        final FileInputStream fis = new FileInputStream(fd);
        try {
            return this.decorateInputStream(fis);
        }
        catch (final IOException e) {
            fis.close();
            throw e;
        }
    }
    
    protected InputStream decorateInputStream(final FileInputStream fis) throws IOException {
        return fis;
    }
    
    public int getNumberOfFlushedRows() {
        return this._numberOfFlushedRows;
    }
    
    public int getNumberOfCellsOfLastFlushedRow() {
        return this._numberOfCellsOfLastFlushedRow;
    }
    
    public int getLowestIndexOfFlushedRows() {
        return this._lowestIndexOfFlushedRows;
    }
    
    public int getLastFlushedRow() {
        return this._numberLastFlushedRow;
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (!this._fd.delete()) {
            SheetDataWriter.logger.log(7, new Object[] { "Can't delete temporary encryption file: " + this._fd });
        }
        super.finalize();
    }
    
    public void writeRow(final int rownum, final SXSSFRow row) throws IOException {
        if (this._numberOfFlushedRows == 0) {
            this._lowestIndexOfFlushedRows = rownum;
        }
        this._numberLastFlushedRow = Math.max(rownum, this._numberLastFlushedRow);
        this._numberOfCellsOfLastFlushedRow = row.getLastCellNum();
        ++this._numberOfFlushedRows;
        this.beginRow(rownum, row);
        final Iterator<Cell> cells = row.allCellsIterator();
        int columnIndex = 0;
        while (cells.hasNext()) {
            this.writeCell(columnIndex++, cells.next());
        }
        this.endRow();
    }
    
    void beginRow(final int rownum, final SXSSFRow row) throws IOException {
        this._out.write("<row");
        this.writeAttribute("r", Integer.toString(rownum + 1));
        if (row.hasCustomHeight()) {
            this.writeAttribute("customHeight", "true");
            this.writeAttribute("ht", Float.toString(row.getHeightInPoints()));
        }
        if (row.getZeroHeight()) {
            this.writeAttribute("hidden", "true");
        }
        if (row.isFormatted()) {
            this.writeAttribute("s", Integer.toString(row.getRowStyleIndex()));
            this.writeAttribute("customFormat", "1");
        }
        if (row.getOutlineLevel() != 0) {
            this.writeAttribute("outlineLevel", Integer.toString(row.getOutlineLevel()));
        }
        if (row.getHidden() != null) {
            this.writeAttribute("hidden", ((boolean)row.getHidden()) ? "1" : "0");
        }
        if (row.getCollapsed() != null) {
            this.writeAttribute("collapsed", ((boolean)row.getCollapsed()) ? "1" : "0");
        }
        this._out.write(">\n");
        this._rownum = rownum;
    }
    
    void endRow() throws IOException {
        this._out.write("</row>\n");
    }
    
    public void writeCell(final int columnIndex, final Cell cell) throws IOException {
        if (cell == null) {
            return;
        }
        final String ref = new CellReference(this._rownum, columnIndex).formatAsString();
        this._out.write("<c");
        this.writeAttribute("r", ref);
        final CellStyle cellStyle = cell.getCellStyle();
        if (cellStyle.getIndex() != 0) {
            this.writeAttribute("s", Integer.toString(cellStyle.getIndex() & 0xFFFF));
        }
        final CellType cellType = cell.getCellType();
        switch (cellType) {
            case BLANK: {
                this._out.write(62);
                break;
            }
            case FORMULA: {
                switch (cell.getCachedFormulaResultType()) {
                    case NUMERIC: {
                        this.writeAttribute("t", "n");
                        break;
                    }
                    case STRING: {
                        this.writeAttribute("t", STCellType.STR.toString());
                        break;
                    }
                    case BOOLEAN: {
                        this.writeAttribute("t", "b");
                        break;
                    }
                    case ERROR: {
                        this.writeAttribute("t", "e");
                        break;
                    }
                }
                this._out.write("><f>");
                this.outputQuotedString(cell.getCellFormula());
                this._out.write("</f>");
                switch (cell.getCachedFormulaResultType()) {
                    case NUMERIC: {
                        final double nval = cell.getNumericCellValue();
                        if (!Double.isNaN(nval)) {
                            this._out.write("<v>");
                            this._out.write(Double.toString(nval));
                            this._out.write("</v>");
                            break;
                        }
                        break;
                    }
                    case STRING: {
                        final String value = cell.getStringCellValue();
                        if (value != null && !value.isEmpty()) {
                            this._out.write("<v>");
                            this._out.write(value);
                            this._out.write("</v>");
                            break;
                        }
                        break;
                    }
                    case BOOLEAN: {
                        this._out.write("><v>");
                        this._out.write(cell.getBooleanCellValue() ? "1" : "0");
                        this._out.write("</v>");
                        break;
                    }
                    case ERROR: {
                        final FormulaError error = FormulaError.forInt(cell.getErrorCellValue());
                        this._out.write("><v>");
                        this._out.write(error.getString());
                        this._out.write("</v>");
                        break;
                    }
                }
                break;
            }
            case STRING: {
                if (this._sharedStringSource != null) {
                    final XSSFRichTextString rt = new XSSFRichTextString(cell.getStringCellValue());
                    final int sRef = this._sharedStringSource.addSharedStringItem((RichTextString)rt);
                    this.writeAttribute("t", STCellType.S.toString());
                    this._out.write("><v>");
                    this._out.write(String.valueOf(sRef));
                    this._out.write("</v>");
                    break;
                }
                this.writeAttribute("t", "inlineStr");
                this._out.write("><is><t");
                if (this.hasLeadingTrailingSpaces(cell.getStringCellValue())) {
                    this.writeAttribute("xml:space", "preserve");
                }
                this._out.write(">");
                this.outputQuotedString(cell.getStringCellValue());
                this._out.write("</t></is>");
                break;
            }
            case NUMERIC: {
                this.writeAttribute("t", "n");
                this._out.write("><v>");
                this._out.write(Double.toString(cell.getNumericCellValue()));
                this._out.write("</v>");
                break;
            }
            case BOOLEAN: {
                this.writeAttribute("t", "b");
                this._out.write("><v>");
                this._out.write(cell.getBooleanCellValue() ? "1" : "0");
                this._out.write("</v>");
                break;
            }
            case ERROR: {
                final FormulaError error2 = FormulaError.forInt(cell.getErrorCellValue());
                this.writeAttribute("t", "e");
                this._out.write("><v>");
                this._out.write(error2.getString());
                this._out.write("</v>");
                break;
            }
            default: {
                throw new IllegalStateException("Invalid cell type: " + cellType);
            }
        }
        this._out.write("</c>");
    }
    
    private void writeAttribute(final String name, final String value) throws IOException {
        this._out.write(32);
        this._out.write(name);
        this._out.write("=\"");
        this._out.write(value);
        this._out.write(34);
    }
    
    boolean hasLeadingTrailingSpaces(final String str) {
        if (str != null && str.length() > 0) {
            final char firstChar = str.charAt(0);
            final char lastChar = str.charAt(str.length() - 1);
            return Character.isWhitespace(firstChar) || Character.isWhitespace(lastChar);
        }
        return false;
    }
    
    protected void outputQuotedString(final String s) throws IOException {
        if (s == null || s.length() == 0) {
            return;
        }
        for (final String s2 : new StringCodepointsIterable(s)) {
            final String codepoint = s2;
            switch (s2) {
                case "<": {
                    this._out.write("&lt;");
                    continue;
                }
                case ">": {
                    this._out.write("&gt;");
                    continue;
                }
                case "&": {
                    this._out.write("&amp;");
                    continue;
                }
                case "\"": {
                    this._out.write("&quot;");
                    continue;
                }
                case "\n": {
                    this._out.write("&#xa;");
                    continue;
                }
                case "\r": {
                    this._out.write("&#xd;");
                    continue;
                }
                case "\t": {
                    this._out.write("&#x9;");
                    continue;
                }
                case " ": {
                    this._out.write("&#xa0;");
                    continue;
                }
                default: {
                    if (codepoint.length() == 1) {
                        final char c = codepoint.charAt(0);
                        if (replaceWithQuestionMark(c)) {
                            this._out.write(63);
                        }
                        else {
                            this._out.write(c);
                        }
                        continue;
                    }
                    this._out.write(codepoint);
                    continue;
                }
            }
        }
    }
    
    static boolean replaceWithQuestionMark(final char c) {
        return c < ' ' || ('\ufffe' <= c && c <= '\uffff');
    }
    
    boolean dispose() throws IOException {
        boolean ret;
        try {
            this._out.close();
        }
        finally {
            ret = this._fd.delete();
        }
        return ret;
    }
    
    static {
        logger = POILogFactory.getLogger((Class)SheetDataWriter.class);
    }
}
