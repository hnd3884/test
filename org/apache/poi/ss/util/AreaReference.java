package org.apache.poi.ss.util;

import org.apache.poi.util.StringUtil;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import org.apache.poi.ss.SpreadsheetVersion;

public class AreaReference
{
    private static final char SHEET_NAME_DELIMITER = '!';
    private static final char CELL_DELIMITER = ':';
    private static final char SPECIAL_NAME_DELIMITER = '\'';
    private static final SpreadsheetVersion DEFAULT_SPREADSHEET_VERSION;
    private final CellReference _firstCell;
    private final CellReference _lastCell;
    private final boolean _isSingleCell;
    private final SpreadsheetVersion _version;
    
    public AreaReference(final String reference, final SpreadsheetVersion version) {
        this._version = ((null != version) ? version : AreaReference.DEFAULT_SPREADSHEET_VERSION);
        if (!isContiguous(reference)) {
            throw new IllegalArgumentException("References passed to the AreaReference must be contiguous, use generateContiguous(ref) if you have non-contiguous references");
        }
        final String[] parts = separateAreaRefs(reference);
        final String part0 = parts[0];
        if (parts.length == 1) {
            this._firstCell = new CellReference(part0);
            this._lastCell = this._firstCell;
            this._isSingleCell = true;
            return;
        }
        if (parts.length != 2) {
            throw new IllegalArgumentException("Bad area ref '" + reference + "'");
        }
        final String part2 = parts[1];
        if (isPlainColumn(part0)) {
            if (!isPlainColumn(part2)) {
                throw new RuntimeException("Bad area ref '" + reference + "'");
            }
            final boolean firstIsAbs = CellReference.isPartAbsolute(part0);
            final boolean lastIsAbs = CellReference.isPartAbsolute(part2);
            final int col0 = CellReference.convertColStringToIndex(part0);
            final int col2 = CellReference.convertColStringToIndex(part2);
            this._firstCell = new CellReference(0, col0, true, firstIsAbs);
            this._lastCell = new CellReference(65535, col2, true, lastIsAbs);
            this._isSingleCell = false;
        }
        else {
            this._firstCell = new CellReference(part0);
            this._lastCell = new CellReference(part2);
            this._isSingleCell = part0.equals(part2);
        }
    }
    
    public AreaReference(final CellReference topLeft, final CellReference botRight, final SpreadsheetVersion version) {
        this._version = ((null != version) ? version : AreaReference.DEFAULT_SPREADSHEET_VERSION);
        final boolean swapRows = topLeft.getRow() > botRight.getRow();
        final boolean swapCols = topLeft.getCol() > botRight.getCol();
        if (swapRows || swapCols) {
            int firstRow;
            boolean firstRowAbs;
            int lastRow;
            boolean lastRowAbs;
            if (swapRows) {
                firstRow = botRight.getRow();
                firstRowAbs = botRight.isRowAbsolute();
                lastRow = topLeft.getRow();
                lastRowAbs = topLeft.isRowAbsolute();
            }
            else {
                firstRow = topLeft.getRow();
                firstRowAbs = topLeft.isRowAbsolute();
                lastRow = botRight.getRow();
                lastRowAbs = botRight.isRowAbsolute();
            }
            String firstSheet;
            int firstColumn;
            boolean firstColAbs;
            String lastSheet;
            int lastColumn;
            boolean lastColAbs;
            if (swapCols) {
                firstSheet = botRight.getSheetName();
                firstColumn = botRight.getCol();
                firstColAbs = botRight.isColAbsolute();
                lastSheet = topLeft.getSheetName();
                lastColumn = topLeft.getCol();
                lastColAbs = topLeft.isColAbsolute();
            }
            else {
                firstSheet = topLeft.getSheetName();
                firstColumn = topLeft.getCol();
                firstColAbs = topLeft.isColAbsolute();
                lastSheet = botRight.getSheetName();
                lastColumn = botRight.getCol();
                lastColAbs = botRight.isColAbsolute();
            }
            this._firstCell = new CellReference(firstSheet, firstRow, firstColumn, firstRowAbs, firstColAbs);
            this._lastCell = new CellReference(lastSheet, lastRow, lastColumn, lastRowAbs, lastColAbs);
        }
        else {
            this._firstCell = topLeft;
            this._lastCell = botRight;
        }
        this._isSingleCell = false;
    }
    
    private static boolean isPlainColumn(final String refPart) {
        for (int i = refPart.length() - 1; i >= 0; --i) {
            final int ch = refPart.charAt(i);
            if (ch != 36 || i != 0) {
                if (ch < 65 || ch > 90) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean isContiguous(final String reference) {
        return splitAreaReferences(reference).length == 1;
    }
    
    public static AreaReference getWholeRow(SpreadsheetVersion version, final String start, final String end) {
        if (null == version) {
            version = AreaReference.DEFAULT_SPREADSHEET_VERSION;
        }
        return new AreaReference("$A" + start + ":$" + version.getLastColumnName() + end, version);
    }
    
    public static AreaReference getWholeColumn(SpreadsheetVersion version, final String start, final String end) {
        if (null == version) {
            version = AreaReference.DEFAULT_SPREADSHEET_VERSION;
        }
        return new AreaReference(start + "$1:" + end + "$" + version.getMaxRows(), version);
    }
    
    public static boolean isWholeColumnReference(SpreadsheetVersion version, final CellReference topLeft, final CellReference botRight) {
        if (null == version) {
            version = AreaReference.DEFAULT_SPREADSHEET_VERSION;
        }
        return topLeft.getRow() == 0 && topLeft.isRowAbsolute() && botRight.getRow() == version.getLastRowIndex() && botRight.isRowAbsolute();
    }
    
    public static AreaReference[] generateContiguous(SpreadsheetVersion version, final String reference) {
        if (null == version) {
            version = AreaReference.DEFAULT_SPREADSHEET_VERSION;
        }
        final List<AreaReference> refs = new ArrayList<AreaReference>();
        final String[] splitAreaReferences;
        final String[] splitReferences = splitAreaReferences = splitAreaReferences(reference);
        for (final String ref : splitAreaReferences) {
            refs.add(new AreaReference(ref, version));
        }
        return refs.toArray(new AreaReference[0]);
    }
    
    private static String[] separateAreaRefs(final String reference) {
        final int len = reference.length();
        int delimiterPos = -1;
        boolean insideDelimitedName = false;
        for (int i = 0; i < len; ++i) {
            switch (reference.charAt(i)) {
                case ':': {
                    if (insideDelimitedName) {
                        break;
                    }
                    if (delimiterPos >= 0) {
                        throw new IllegalArgumentException("More than one cell delimiter ':' appears in area reference '" + reference + "'");
                    }
                    delimiterPos = i;
                    break;
                }
                case '\'': {
                    if (!insideDelimitedName) {
                        insideDelimitedName = true;
                        break;
                    }
                    if (i >= len - 1) {
                        throw new IllegalArgumentException("Area reference '" + reference + "' ends with special name delimiter '" + '\'' + "'");
                    }
                    if (reference.charAt(i + 1) == '\'') {
                        ++i;
                        break;
                    }
                    insideDelimitedName = false;
                    break;
                }
            }
        }
        if (delimiterPos < 0) {
            return new String[] { reference };
        }
        final String partA = reference.substring(0, delimiterPos);
        final String partB = reference.substring(delimiterPos + 1);
        if (partB.indexOf(33) >= 0) {
            throw new RuntimeException("Unexpected ! in second cell reference of '" + reference + "'");
        }
        final int plingPos = partA.lastIndexOf(33);
        if (plingPos < 0) {
            return new String[] { partA, partB };
        }
        final String sheetName = partA.substring(0, plingPos + 1);
        return new String[] { partA, sheetName + partB };
    }
    
    private static String[] splitAreaReferences(final String reference) {
        final List<String> results = new ArrayList<String>();
        String currentSegment = "";
        final StringTokenizer st = new StringTokenizer(reference, ",");
        while (st.hasMoreTokens()) {
            if (currentSegment.length() > 0) {
                currentSegment += ",";
            }
            currentSegment += st.nextToken();
            final int numSingleQuotes = StringUtil.countMatches(currentSegment, '\'');
            if (numSingleQuotes == 0 || numSingleQuotes == 2) {
                results.add(currentSegment);
                currentSegment = "";
            }
        }
        if (currentSegment.length() > 0) {
            results.add(currentSegment);
        }
        return results.toArray(new String[0]);
    }
    
    public boolean isWholeColumnReference() {
        return isWholeColumnReference(this._version, this._firstCell, this._lastCell);
    }
    
    public boolean isSingleCell() {
        return this._isSingleCell;
    }
    
    public CellReference getFirstCell() {
        return this._firstCell;
    }
    
    public CellReference getLastCell() {
        return this._lastCell;
    }
    
    public CellReference[] getAllReferencedCells() {
        if (this._isSingleCell) {
            return new CellReference[] { this._firstCell };
        }
        final int minRow = Math.min(this._firstCell.getRow(), this._lastCell.getRow());
        final int maxRow = Math.max(this._firstCell.getRow(), this._lastCell.getRow());
        final int minCol = Math.min(this._firstCell.getCol(), this._lastCell.getCol());
        final int maxCol = Math.max(this._firstCell.getCol(), this._lastCell.getCol());
        final String sheetName = this._firstCell.getSheetName();
        final List<CellReference> refs = new ArrayList<CellReference>();
        for (int row = minRow; row <= maxRow; ++row) {
            for (int col = minCol; col <= maxCol; ++col) {
                final CellReference ref = new CellReference(sheetName, row, col, this._firstCell.isRowAbsolute(), this._firstCell.isColAbsolute());
                refs.add(ref);
            }
        }
        return refs.toArray(new CellReference[0]);
    }
    
    public String formatAsString() {
        if (this.isWholeColumnReference()) {
            return CellReference.convertNumToColString(this._firstCell.getCol()) + ":" + CellReference.convertNumToColString(this._lastCell.getCol());
        }
        final StringBuilder sb = new StringBuilder(32);
        sb.append(this._firstCell.formatAsString());
        if (!this._isSingleCell) {
            sb.append(':');
            if (this._lastCell.getSheetName() == null) {
                sb.append(this._lastCell.formatAsString());
            }
            else {
                this._lastCell.appendCellReference(sb);
            }
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(64);
        sb.append(this.getClass().getName()).append(" [");
        try {
            sb.append(this.formatAsString());
        }
        catch (final Exception e) {
            sb.append(e);
        }
        sb.append(']');
        return sb.toString();
    }
    
    static {
        DEFAULT_SPREADSHEET_VERSION = SpreadsheetVersion.EXCEL97;
    }
}
