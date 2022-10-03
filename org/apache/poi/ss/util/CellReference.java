package org.apache.poi.ss.util;

import java.util.Objects;
import org.apache.poi.ss.formula.SheetNameFormatter;
import java.util.regex.Matcher;
import org.apache.poi.ss.SpreadsheetVersion;
import java.util.Locale;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.util.StringUtil;
import java.util.regex.Pattern;

public class CellReference
{
    private static final char ABSOLUTE_REFERENCE_MARKER = '$';
    private static final char SHEET_NAME_DELIMITER = '!';
    private static final char SPECIAL_NAME_DELIMITER = '\'';
    private static final Pattern CELL_REF_PATTERN;
    private static final Pattern STRICTLY_CELL_REF_PATTERN;
    private static final Pattern COLUMN_REF_PATTERN;
    private static final Pattern ROW_REF_PATTERN;
    private static final Pattern NAMED_RANGE_NAME_PATTERN;
    private final String _sheetName;
    private final int _rowIndex;
    private final int _colIndex;
    private final boolean _isRowAbs;
    private final boolean _isColAbs;
    
    public CellReference(final String cellRef) {
        if (StringUtil.endsWithIgnoreCase(cellRef, "#REF!")) {
            throw new IllegalArgumentException("Cell reference invalid: " + cellRef);
        }
        final CellRefParts parts = separateRefParts(cellRef);
        this._sheetName = parts.sheetName;
        String colRef = parts.colRef;
        this._isColAbs = (colRef.length() > 0 && colRef.charAt(0) == '$');
        if (this._isColAbs) {
            colRef = colRef.substring(1);
        }
        if (colRef.length() == 0) {
            this._colIndex = -1;
        }
        else {
            this._colIndex = convertColStringToIndex(colRef);
        }
        String rowRef = parts.rowRef;
        this._isRowAbs = (rowRef.length() > 0 && rowRef.charAt(0) == '$');
        if (this._isRowAbs) {
            rowRef = rowRef.substring(1);
        }
        if (rowRef.length() == 0) {
            this._rowIndex = -1;
        }
        else {
            this._rowIndex = Integer.parseInt(rowRef) - 1;
        }
    }
    
    public CellReference(final int pRow, final int pCol) {
        this(pRow, pCol, false, false);
    }
    
    public CellReference(final int pRow, final short pCol) {
        this(pRow, pCol & 0xFFFF, false, false);
    }
    
    public CellReference(final Cell cell) {
        this(cell.getSheet().getSheetName(), cell.getRowIndex(), cell.getColumnIndex(), false, false);
    }
    
    public CellReference(final int pRow, final int pCol, final boolean pAbsRow, final boolean pAbsCol) {
        this(null, pRow, pCol, pAbsRow, pAbsCol);
    }
    
    public CellReference(final String pSheetName, final int pRow, final int pCol, final boolean pAbsRow, final boolean pAbsCol) {
        if (pRow < -1) {
            throw new IllegalArgumentException("row index may not be negative, but had " + pRow);
        }
        if (pCol < -1) {
            throw new IllegalArgumentException("column index may not be negative, but had " + pCol);
        }
        this._sheetName = pSheetName;
        this._rowIndex = pRow;
        this._colIndex = pCol;
        this._isRowAbs = pAbsRow;
        this._isColAbs = pAbsCol;
    }
    
    public int getRow() {
        return this._rowIndex;
    }
    
    public short getCol() {
        return (short)this._colIndex;
    }
    
    public boolean isRowAbsolute() {
        return this._isRowAbs;
    }
    
    public boolean isColAbsolute() {
        return this._isColAbs;
    }
    
    public String getSheetName() {
        return this._sheetName;
    }
    
    public static boolean isPartAbsolute(final String part) {
        return part.charAt(0) == '$';
    }
    
    public static int convertColStringToIndex(final String ref) {
        int retval = 0;
        final char[] refs = ref.toUpperCase(Locale.ROOT).toCharArray();
        for (int k = 0; k < refs.length; ++k) {
            final char thechar = refs[k];
            if (thechar == '$') {
                if (k != 0) {
                    throw new IllegalArgumentException("Bad col ref format '" + ref + "'");
                }
            }
            else {
                retval = retval * 26 + (thechar - 'A' + 1);
            }
        }
        return retval - 1;
    }
    
    public static NameType classifyCellReference(final String str, final SpreadsheetVersion ssVersion) {
        final int len = str.length();
        if (len < 1) {
            throw new IllegalArgumentException("Empty string not allowed");
        }
        final char firstChar = str.charAt(0);
        switch (firstChar) {
            case '$':
            case '.':
            case '_': {
                break;
            }
            default: {
                if (!Character.isLetter(firstChar) && !Character.isDigit(firstChar)) {
                    throw new IllegalArgumentException("Invalid first char (" + firstChar + ") of cell reference or named range.  Letter expected");
                }
                break;
            }
        }
        if (!Character.isDigit(str.charAt(len - 1))) {
            return validateNamedRangeName(str, ssVersion);
        }
        final Matcher cellRefPatternMatcher = CellReference.STRICTLY_CELL_REF_PATTERN.matcher(str);
        if (!cellRefPatternMatcher.matches()) {
            return validateNamedRangeName(str, ssVersion);
        }
        final String lettersGroup = cellRefPatternMatcher.group(1);
        final String digitsGroup = cellRefPatternMatcher.group(2);
        if (cellReferenceIsWithinRange(lettersGroup, digitsGroup, ssVersion)) {
            return NameType.CELL;
        }
        if (str.indexOf(36) >= 0) {
            return NameType.BAD_CELL_OR_NAMED_RANGE;
        }
        return NameType.NAMED_RANGE;
    }
    
    private static NameType validateNamedRangeName(final String str, final SpreadsheetVersion ssVersion) {
        final Matcher colMatcher = CellReference.COLUMN_REF_PATTERN.matcher(str);
        if (colMatcher.matches()) {
            final String colStr = colMatcher.group(1);
            if (isColumnWithinRange(colStr, ssVersion)) {
                return NameType.COLUMN;
            }
        }
        final Matcher rowMatcher = CellReference.ROW_REF_PATTERN.matcher(str);
        if (rowMatcher.matches()) {
            final String rowStr = rowMatcher.group(1);
            if (isRowWithinRange(rowStr, ssVersion)) {
                return NameType.ROW;
            }
        }
        if (!CellReference.NAMED_RANGE_NAME_PATTERN.matcher(str).matches()) {
            return NameType.BAD_CELL_OR_NAMED_RANGE;
        }
        return NameType.NAMED_RANGE;
    }
    
    public static boolean cellReferenceIsWithinRange(final String colStr, final String rowStr, final SpreadsheetVersion ssVersion) {
        return isColumnWithinRange(colStr, ssVersion) && isRowWithinRange(rowStr, ssVersion);
    }
    
    public static boolean isColumnWithinRange(final String colStr, final SpreadsheetVersion ssVersion) {
        final String lastCol = ssVersion.getLastColumnName();
        final int lastColLength = lastCol.length();
        final int numberOfLetters = colStr.length();
        return numberOfLetters <= lastColLength && (numberOfLetters != lastColLength || colStr.toUpperCase(Locale.ROOT).compareTo(lastCol) <= 0);
    }
    
    public static boolean isRowWithinRange(final String rowStr, final SpreadsheetVersion ssVersion) {
        final long rowNum = Long.parseLong(rowStr) - 1L;
        return rowNum <= 2147483647L && isRowWithinRange(Math.toIntExact(rowNum), ssVersion);
    }
    
    public static boolean isRowWithinRange(final int rowNum, final SpreadsheetVersion ssVersion) {
        return 0 <= rowNum && rowNum <= ssVersion.getLastRowIndex();
    }
    
    private static CellRefParts separateRefParts(final String reference) {
        final int plingPos = reference.lastIndexOf(33);
        final String sheetName = parseSheetName(reference, plingPos);
        final String cell = reference.substring(plingPos + 1).toUpperCase(Locale.ROOT);
        final Matcher matcher = CellReference.CELL_REF_PATTERN.matcher(cell);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid CellReference: " + reference);
        }
        final String col = matcher.group(1);
        final String row = matcher.group(2);
        return new CellRefParts(sheetName, row, col);
    }
    
    private static String parseSheetName(final String reference, final int indexOfSheetNameDelimiter) {
        if (indexOfSheetNameDelimiter < 0) {
            return null;
        }
        final boolean isQuoted = reference.charAt(0) == '\'';
        if (!isQuoted) {
            if (!reference.contains(" ")) {
                return reference.substring(0, indexOfSheetNameDelimiter);
            }
            throw new IllegalArgumentException("Sheet names containing spaces must be quoted: (" + reference + ")");
        }
        else {
            final int lastQuotePos = indexOfSheetNameDelimiter - 1;
            if (reference.charAt(lastQuotePos) != '\'') {
                throw new IllegalArgumentException("Mismatched quotes: (" + reference + ")");
            }
            final StringBuilder sb = new StringBuilder(indexOfSheetNameDelimiter);
            for (int i = 1; i < lastQuotePos; ++i) {
                final char ch = reference.charAt(i);
                if (ch != '\'') {
                    sb.append(ch);
                }
                else {
                    if (i + 1 >= lastQuotePos || reference.charAt(i + 1) != '\'') {
                        throw new IllegalArgumentException("Bad sheet name quote escaping: (" + reference + ")");
                    }
                    ++i;
                    sb.append(ch);
                }
            }
            return sb.toString();
        }
    }
    
    public static String convertNumToColString(final int col) {
        final int excelColNum = col + 1;
        final StringBuilder colRef = new StringBuilder(2);
        int colRemain = excelColNum;
        while (colRemain > 0) {
            int thisPart = colRemain % 26;
            if (thisPart == 0) {
                thisPart = 26;
            }
            colRemain = (colRemain - thisPart) / 26;
            final char colChar = (char)(thisPart + 64);
            colRef.insert(0, colChar);
        }
        return colRef.toString();
    }
    
    public String formatAsString() {
        return this.formatAsString(true);
    }
    
    public String formatAsString(final boolean includeSheetName) {
        final StringBuilder sb = new StringBuilder(32);
        if (includeSheetName && this._sheetName != null) {
            SheetNameFormatter.appendFormat(sb, this._sheetName);
            sb.append('!');
        }
        this.appendCellReference(sb);
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + " [" + this.formatAsString() + "]";
    }
    
    public String[] getCellRefParts() {
        return new String[] { this._sheetName, Integer.toString(this._rowIndex + 1), convertNumToColString(this._colIndex) };
    }
    
    void appendCellReference(final StringBuilder sb) {
        if (this._colIndex != -1) {
            if (this._isColAbs) {
                sb.append('$');
            }
            sb.append(convertNumToColString(this._colIndex));
        }
        if (this._rowIndex != -1) {
            if (this._isRowAbs) {
                sb.append('$');
            }
            sb.append(this._rowIndex + 1);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CellReference)) {
            return false;
        }
        final CellReference cr = (CellReference)o;
        return this._rowIndex == cr._rowIndex && this._colIndex == cr._colIndex && this._isRowAbs == cr._isRowAbs && this._isColAbs == cr._isColAbs && ((this._sheetName != null) ? this._sheetName.equals(cr._sheetName) : (cr._sheetName == null));
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this._rowIndex, this._colIndex, this._isRowAbs, this._isColAbs, this._sheetName);
    }
    
    static {
        CELL_REF_PATTERN = Pattern.compile("(\\$?[A-Z]+)?(\\$?[0-9]+)?", 2);
        STRICTLY_CELL_REF_PATTERN = Pattern.compile("\\$?([A-Z]+)\\$?([0-9]+)", 2);
        COLUMN_REF_PATTERN = Pattern.compile("\\$?([A-Z]+)", 2);
        ROW_REF_PATTERN = Pattern.compile("\\$?([0-9]+)");
        NAMED_RANGE_NAME_PATTERN = Pattern.compile("[_A-Z][_.A-Z0-9]*", 2);
    }
    
    public enum NameType
    {
        CELL, 
        NAMED_RANGE, 
        COLUMN, 
        ROW, 
        BAD_CELL_OR_NAMED_RANGE;
    }
    
    private static final class CellRefParts
    {
        final String sheetName;
        final String rowRef;
        final String colRef;
        
        private CellRefParts(final String sheetName, final String rowRef, final String colRef) {
            this.sheetName = sheetName;
            this.rowRef = ((rowRef != null) ? rowRef : "");
            this.colRef = ((colRef != null) ? colRef : "");
        }
    }
}
