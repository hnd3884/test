package org.apache.poi.xssf.eventusermodel;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.SAXException;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.xml.sax.Attributes;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.poi.ss.util.CellAddress;
import java.util.Queue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.Comments;
import org.apache.poi.xssf.model.Styles;
import org.apache.poi.util.POILogger;
import org.xml.sax.helpers.DefaultHandler;

public class XSSFSheetXMLHandler extends DefaultHandler
{
    private static final POILogger logger;
    private Styles stylesTable;
    private Comments comments;
    private SharedStrings sharedStringsTable;
    private final SheetContentsHandler output;
    private boolean vIsOpen;
    private boolean fIsOpen;
    private boolean isIsOpen;
    private boolean hfIsOpen;
    private xssfDataType nextDataType;
    private short formatIndex;
    private String formatString;
    private final DataFormatter formatter;
    private int rowNum;
    private int nextRowNum;
    private String cellRef;
    private boolean formulasNotResults;
    private StringBuilder value;
    private StringBuilder formula;
    private StringBuilder headerFooter;
    private Queue<CellAddress> commentCellRefs;
    
    public XSSFSheetXMLHandler(final Styles styles, final Comments comments, final SharedStrings strings, final SheetContentsHandler sheetContentsHandler, final DataFormatter dataFormatter, final boolean formulasNotResults) {
        this.value = new StringBuilder(64);
        this.formula = new StringBuilder(64);
        this.headerFooter = new StringBuilder(64);
        this.stylesTable = styles;
        this.comments = comments;
        this.sharedStringsTable = strings;
        this.output = sheetContentsHandler;
        this.formulasNotResults = formulasNotResults;
        this.nextDataType = xssfDataType.NUMBER;
        this.formatter = dataFormatter;
        this.init(comments);
    }
    
    public XSSFSheetXMLHandler(final Styles styles, final SharedStrings strings, final SheetContentsHandler sheetContentsHandler, final DataFormatter dataFormatter, final boolean formulasNotResults) {
        this(styles, null, strings, sheetContentsHandler, dataFormatter, formulasNotResults);
    }
    
    public XSSFSheetXMLHandler(final Styles styles, final SharedStrings strings, final SheetContentsHandler sheetContentsHandler, final boolean formulasNotResults) {
        this(styles, strings, sheetContentsHandler, new DataFormatter(), formulasNotResults);
    }
    
    private void init(final Comments commentsTable) {
        if (commentsTable != null) {
            this.commentCellRefs = new LinkedList<CellAddress>();
            final Iterator<CellAddress> iter = commentsTable.getCellAddresses();
            while (iter.hasNext()) {
                this.commentCellRefs.add(iter.next());
            }
        }
    }
    
    private boolean isTextTag(final String name) {
        return "v".equals(name) || "inlineStr".equals(name) || ("t".equals(name) && this.isIsOpen);
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if (uri != null && !uri.equals("http://schemas.openxmlformats.org/spreadsheetml/2006/main")) {
            return;
        }
        if (this.isTextTag(localName)) {
            this.vIsOpen = true;
            this.value.setLength(0);
        }
        else if ("is".equals(localName)) {
            this.isIsOpen = true;
        }
        else if ("f".equals(localName)) {
            this.formula.setLength(0);
            if (this.nextDataType == xssfDataType.NUMBER) {
                this.nextDataType = xssfDataType.FORMULA;
            }
            final String type = attributes.getValue("t");
            if (type != null && type.equals("shared")) {
                final String ref = attributes.getValue("ref");
                final String si = attributes.getValue("si");
                if (ref != null) {
                    this.fIsOpen = true;
                }
                else if (this.formulasNotResults) {
                    XSSFSheetXMLHandler.logger.log(5, new Object[] { "shared formulas not yet supported!" });
                }
            }
            else {
                this.fIsOpen = true;
            }
        }
        else if ("oddHeader".equals(localName) || "evenHeader".equals(localName) || "firstHeader".equals(localName) || "firstFooter".equals(localName) || "oddFooter".equals(localName) || "evenFooter".equals(localName)) {
            this.hfIsOpen = true;
            this.headerFooter.setLength(0);
        }
        else if ("row".equals(localName)) {
            final String rowNumStr = attributes.getValue("r");
            if (rowNumStr != null) {
                this.rowNum = Integer.parseInt(rowNumStr) - 1;
            }
            else {
                this.rowNum = this.nextRowNum;
            }
            this.output.startRow(this.rowNum);
        }
        else if ("c".equals(localName)) {
            this.nextDataType = xssfDataType.NUMBER;
            this.formatIndex = -1;
            this.formatString = null;
            this.cellRef = attributes.getValue("r");
            final String cellType = attributes.getValue("t");
            final String cellStyleStr = attributes.getValue("s");
            if ("b".equals(cellType)) {
                this.nextDataType = xssfDataType.BOOLEAN;
            }
            else if ("e".equals(cellType)) {
                this.nextDataType = xssfDataType.ERROR;
            }
            else if ("inlineStr".equals(cellType)) {
                this.nextDataType = xssfDataType.INLINE_STRING;
            }
            else if ("s".equals(cellType)) {
                this.nextDataType = xssfDataType.SST_STRING;
            }
            else if ("str".equals(cellType)) {
                this.nextDataType = xssfDataType.FORMULA;
            }
            else {
                XSSFCellStyle style = null;
                if (this.stylesTable != null) {
                    if (cellStyleStr != null) {
                        final int styleIndex = Integer.parseInt(cellStyleStr);
                        style = this.stylesTable.getStyleAt(styleIndex);
                    }
                    else if (this.stylesTable.getNumCellStyles() > 0) {
                        style = this.stylesTable.getStyleAt(0);
                    }
                }
                if (style != null) {
                    this.formatIndex = style.getDataFormat();
                    this.formatString = style.getDataFormatString();
                    if (this.formatString == null) {
                        this.formatString = BuiltinFormats.getBuiltinFormat((int)this.formatIndex);
                    }
                }
            }
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (uri != null && !uri.equals("http://schemas.openxmlformats.org/spreadsheetml/2006/main")) {
            return;
        }
        String thisStr = null;
        if (this.isTextTag(localName)) {
            this.vIsOpen = false;
            switch (this.nextDataType) {
                case BOOLEAN: {
                    final char first = this.value.charAt(0);
                    thisStr = ((first == '0') ? "FALSE" : "TRUE");
                    break;
                }
                case ERROR: {
                    thisStr = "ERROR:" + (Object)this.value;
                    break;
                }
                case FORMULA: {
                    if (this.formulasNotResults) {
                        thisStr = this.formula.toString();
                        break;
                    }
                    final String fv = this.value.toString();
                    if (this.formatString != null) {
                        try {
                            final double d = Double.parseDouble(fv);
                            thisStr = this.formatter.formatRawCellContents(d, (int)this.formatIndex, this.formatString);
                        }
                        catch (final NumberFormatException e) {
                            thisStr = fv;
                        }
                    }
                    else {
                        thisStr = fv;
                    }
                    break;
                }
                case INLINE_STRING: {
                    final XSSFRichTextString rtsi = new XSSFRichTextString(this.value.toString());
                    thisStr = rtsi.toString();
                    break;
                }
                case SST_STRING: {
                    final String sstIndex = this.value.toString();
                    try {
                        final int idx = Integer.parseInt(sstIndex);
                        final RichTextString rtss = this.sharedStringsTable.getItemAt(idx);
                        thisStr = rtss.toString();
                    }
                    catch (final NumberFormatException ex) {
                        XSSFSheetXMLHandler.logger.log(7, new Object[] { "Failed to parse SST index '" + sstIndex, ex });
                    }
                    break;
                }
                case NUMBER: {
                    final String n = this.value.toString();
                    if (this.formatString != null && n.length() > 0) {
                        thisStr = this.formatter.formatRawCellContents(Double.parseDouble(n), (int)this.formatIndex, this.formatString);
                        break;
                    }
                    thisStr = n;
                    break;
                }
                default: {
                    thisStr = "(TODO: Unexpected type: " + this.nextDataType + ")";
                    break;
                }
            }
            this.checkForEmptyCellComments(EmptyCellCommentsCheckType.CELL);
            final XSSFComment comment = (this.comments != null) ? this.comments.findCellComment(new CellAddress(this.cellRef)) : null;
            this.output.cell(this.cellRef, thisStr, comment);
        }
        else if ("f".equals(localName)) {
            this.fIsOpen = false;
        }
        else if ("is".equals(localName)) {
            this.isIsOpen = false;
        }
        else if ("row".equals(localName)) {
            this.checkForEmptyCellComments(EmptyCellCommentsCheckType.END_OF_ROW);
            this.output.endRow(this.rowNum);
            this.nextRowNum = this.rowNum + 1;
        }
        else if ("sheetData".equals(localName)) {
            this.checkForEmptyCellComments(EmptyCellCommentsCheckType.END_OF_SHEET_DATA);
            this.output.endSheet();
        }
        else if ("oddHeader".equals(localName) || "evenHeader".equals(localName) || "firstHeader".equals(localName)) {
            this.hfIsOpen = false;
            this.output.headerFooter(this.headerFooter.toString(), true, localName);
        }
        else if ("oddFooter".equals(localName) || "evenFooter".equals(localName) || "firstFooter".equals(localName)) {
            this.hfIsOpen = false;
            this.output.headerFooter(this.headerFooter.toString(), false, localName);
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.vIsOpen) {
            this.value.append(ch, start, length);
        }
        if (this.fIsOpen) {
            this.formula.append(ch, start, length);
        }
        if (this.hfIsOpen) {
            this.headerFooter.append(ch, start, length);
        }
    }
    
    private void checkForEmptyCellComments(final EmptyCellCommentsCheckType type) {
        if (this.commentCellRefs != null && !this.commentCellRefs.isEmpty()) {
            if (type == EmptyCellCommentsCheckType.END_OF_SHEET_DATA) {
                while (!this.commentCellRefs.isEmpty()) {
                    this.outputEmptyCellComment(this.commentCellRefs.remove());
                }
                return;
            }
            if (this.cellRef == null) {
                if (type == EmptyCellCommentsCheckType.END_OF_ROW) {
                    while (!this.commentCellRefs.isEmpty()) {
                        if (this.commentCellRefs.peek().getRow() != this.rowNum) {
                            return;
                        }
                        this.outputEmptyCellComment(this.commentCellRefs.remove());
                    }
                    return;
                }
                throw new IllegalStateException("Cell ref should be null only if there are only empty cells in the row; rowNum: " + this.rowNum);
            }
            else {
                CellAddress nextCommentCellRef;
                do {
                    final CellAddress cellRef = new CellAddress(this.cellRef);
                    final CellAddress peekCellRef = this.commentCellRefs.peek();
                    if (type == EmptyCellCommentsCheckType.CELL && cellRef.equals((Object)peekCellRef)) {
                        this.commentCellRefs.remove();
                        return;
                    }
                    final int comparison = peekCellRef.compareTo(cellRef);
                    if (comparison > 0 && type == EmptyCellCommentsCheckType.END_OF_ROW && peekCellRef.getRow() <= this.rowNum) {
                        nextCommentCellRef = this.commentCellRefs.remove();
                        this.outputEmptyCellComment(nextCommentCellRef);
                    }
                    else if (comparison < 0 && type == EmptyCellCommentsCheckType.CELL && peekCellRef.getRow() <= this.rowNum) {
                        nextCommentCellRef = this.commentCellRefs.remove();
                        this.outputEmptyCellComment(nextCommentCellRef);
                    }
                    else {
                        nextCommentCellRef = null;
                    }
                } while (nextCommentCellRef != null && !this.commentCellRefs.isEmpty());
            }
        }
    }
    
    private void outputEmptyCellComment(final CellAddress cellRef) {
        final XSSFComment comment = this.comments.findCellComment(cellRef);
        this.output.cell(cellRef.formatAsString(), null, comment);
    }
    
    static {
        logger = POILogFactory.getLogger((Class)XSSFSheetXMLHandler.class);
    }
    
    enum xssfDataType
    {
        BOOLEAN, 
        ERROR, 
        FORMULA, 
        INLINE_STRING, 
        SST_STRING, 
        NUMBER;
    }
    
    private enum EmptyCellCommentsCheckType
    {
        CELL, 
        END_OF_ROW, 
        END_OF_SHEET_DATA;
    }
    
    public interface SheetContentsHandler
    {
        void startRow(final int p0);
        
        void endRow(final int p0);
        
        void cell(final String p0, final String p1, final XSSFComment p2);
        
        default void headerFooter(final String text, final boolean isHeader, final String tagName) {
        }
        
        default void endSheet() {
        }
    }
}
