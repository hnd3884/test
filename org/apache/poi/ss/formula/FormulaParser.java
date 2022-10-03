package org.apache.poi.ss.formula;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.formula.ptg.SubtractPtg;
import org.apache.poi.ss.formula.ptg.AddPtg;
import org.apache.poi.ss.formula.ptg.ConcatPtg;
import org.apache.poi.ss.formula.ptg.LessThanPtg;
import org.apache.poi.ss.formula.ptg.NotEqualPtg;
import org.apache.poi.ss.formula.ptg.LessEqualPtg;
import org.apache.poi.ss.formula.ptg.GreaterThanPtg;
import org.apache.poi.ss.formula.ptg.GreaterEqualPtg;
import org.apache.poi.ss.formula.ptg.EqualPtg;
import org.apache.poi.ss.formula.ptg.IntersectionPtg;
import org.apache.poi.ss.formula.ptg.UnionPtg;
import org.apache.poi.ss.formula.ptg.DividePtg;
import org.apache.poi.ss.formula.ptg.MultiplyPtg;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.formula.constant.ErrorConstant;
import org.apache.poi.ss.formula.ptg.ArrayPtg;
import org.apache.poi.ss.formula.ptg.UnaryMinusPtg;
import org.apache.poi.ss.formula.ptg.UnaryPlusPtg;
import org.apache.poi.ss.formula.ptg.IntPtg;
import org.apache.poi.ss.formula.ptg.NumberPtg;
import org.apache.poi.ss.formula.ptg.PercentPtg;
import org.apache.poi.ss.formula.ptg.PowerPtg;
import java.util.List;
import org.apache.poi.ss.formula.ptg.MissingArgPtg;
import java.util.ArrayList;
import org.apache.poi.ss.formula.function.FunctionMetadata;
import org.apache.poi.ss.formula.ptg.FuncPtg;
import org.apache.poi.ss.formula.ptg.AttrPtg;
import org.apache.poi.ss.formula.ptg.FuncVarPtg;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.formula.ptg.NameXPxg;
import org.apache.poi.ss.formula.function.FunctionMetadataRegistry;
import java.util.Locale;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.formula.ptg.RefPtg;
import org.apache.poi.ss.formula.ptg.BoolPtg;
import org.apache.poi.ss.formula.ptg.StringPtg;
import org.apache.poi.ss.usermodel.Table;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.ptg.ErrPtg;
import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;
import org.apache.poi.ss.formula.ptg.OperandPtg;
import org.apache.poi.ss.formula.ptg.ParenthesisPtg;
import org.apache.poi.ss.formula.ptg.OperationPtg;
import org.apache.poi.ss.formula.ptg.NameXPtg;
import org.apache.poi.ss.formula.ptg.NamePtg;
import org.apache.poi.ss.formula.ptg.AbstractFunctionPtg;
import org.apache.poi.ss.formula.ptg.MemAreaPtg;
import org.apache.poi.ss.formula.ptg.MemFuncPtg;
import org.apache.poi.ss.formula.ptg.RangePtg;
import org.apache.poi.ss.formula.ptg.Area3DPxg;
import org.apache.poi.ss.formula.ptg.Ptg;
import java.util.regex.Pattern;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Internal;

@Internal
public final class FormulaParser
{
    private static final POILogger log;
    private final String _formulaString;
    private final int _formulaLength;
    private int _pointer;
    private ParseNode _rootNode;
    private static final char TAB = '\t';
    private static final char CR = '\r';
    private static final char LF = '\n';
    private int look;
    private boolean _inIntersection;
    private final FormulaParsingWorkbook _book;
    private final SpreadsheetVersion _ssVersion;
    private final int _sheetIndex;
    private final int _rowIndex;
    private static final String specHeaders = "Headers";
    private static final String specAll = "All";
    private static final String specData = "Data";
    private static final String specTotals = "Totals";
    private static final String specThisRow = "This Row";
    private static final Pattern CELL_REF_PATTERN;
    
    private FormulaParser(final String formula, final FormulaParsingWorkbook book, final int sheetIndex, final int rowIndex) {
        this._formulaString = formula;
        this._pointer = 0;
        this._book = book;
        this._ssVersion = ((book == null) ? SpreadsheetVersion.EXCEL97 : book.getSpreadsheetVersion());
        this._formulaLength = this._formulaString.length();
        this._sheetIndex = sheetIndex;
        this._rowIndex = rowIndex;
    }
    
    public static Ptg[] parse(final String formula, final FormulaParsingWorkbook workbook, final FormulaType formulaType, final int sheetIndex, final int rowIndex) {
        final FormulaParser fp = new FormulaParser(formula, workbook, sheetIndex, rowIndex);
        fp.parse();
        return fp.getRPNPtg(formulaType);
    }
    
    public static Ptg[] parse(final String formula, final FormulaParsingWorkbook workbook, final FormulaType formulaType, final int sheetIndex) {
        return parse(formula, workbook, formulaType, sheetIndex, -1);
    }
    
    public static Area3DPxg parseStructuredReference(final String tableText, final FormulaParsingWorkbook workbook, final int rowIndex) {
        final int sheetIndex = -1;
        final Ptg[] arr = parse(tableText, workbook, FormulaType.CELL, -1, rowIndex);
        if (arr.length != 1 || !(arr[0] instanceof Area3DPxg)) {
            throw new IllegalStateException("Illegal structured reference, had length: " + arr.length);
        }
        return (Area3DPxg)arr[0];
    }
    
    private void GetChar() {
        if (IsWhite(this.look)) {
            if (this.look == 32) {
                this._inIntersection = true;
            }
        }
        else {
            this._inIntersection = false;
        }
        if (this._pointer > this._formulaLength) {
            throw new RuntimeException("Parsed past the end of the formula, pos: " + this._pointer + ", length: " + this._formulaLength + ", formula: " + this._formulaString);
        }
        if (this._pointer < this._formulaLength) {
            this.look = this._formulaString.codePointAt(this._pointer);
        }
        else {
            this.look = 0;
            this._inIntersection = false;
        }
        this._pointer += Character.charCount(this.look);
    }
    
    private void resetPointer(final int ptr) {
        this._pointer = ptr;
        if (this._pointer <= this._formulaLength) {
            this.look = this._formulaString.codePointAt(this._pointer - Character.charCount(this.look));
        }
        else {
            this.look = 0;
        }
    }
    
    private RuntimeException expected(final String s) {
        String msg;
        if (this.look == 61 && this._formulaString.substring(0, this._pointer - 1).trim().length() < 1) {
            msg = "The specified formula '" + this._formulaString + "' starts with an equals sign which is not allowed.";
        }
        else {
            msg = new StringBuilder("Parse error near char ").append(this._pointer - 1).append(" '").appendCodePoint(this.look).append("'").append(" in specified formula '").append(this._formulaString).append("'. Expected ").append(s).toString();
        }
        return new FormulaParseException(msg);
    }
    
    private static boolean IsAlpha(final int c) {
        return Character.isLetter(c) || c == 36 || c == 95;
    }
    
    private static boolean IsDigit(final int c) {
        return Character.isDigit(c);
    }
    
    private static boolean IsWhite(final int c) {
        return c == 32 || c == 9 || c == 13 || c == 10;
    }
    
    private void SkipWhite() {
        while (IsWhite(this.look)) {
            this.GetChar();
        }
    }
    
    private void Match(final int x) {
        if (this.look != x) {
            throw this.expected(new StringBuilder().append("'").appendCodePoint(x).append("'").toString());
        }
        this.GetChar();
    }
    
    private String GetNum() {
        final StringBuilder value = new StringBuilder();
        while (IsDigit(this.look)) {
            value.appendCodePoint(this.look);
            this.GetChar();
        }
        return (value.length() == 0) ? null : value.toString();
    }
    
    private ParseNode parseRangeExpression() {
        ParseNode result = this.parseRangeable();
        boolean hasRange = false;
        while (this.look == 58) {
            final int pos = this._pointer;
            this.GetChar();
            final ParseNode nextPart = this.parseRangeable();
            checkValidRangeOperand("LHS", pos, result);
            checkValidRangeOperand("RHS", pos, nextPart);
            final ParseNode[] children = { result, nextPart };
            result = new ParseNode(RangePtg.instance, children);
            hasRange = true;
        }
        if (hasRange) {
            return augmentWithMemPtg(result);
        }
        return result;
    }
    
    private static ParseNode augmentWithMemPtg(final ParseNode root) {
        Ptg memPtg;
        if (needsMemFunc(root)) {
            memPtg = new MemFuncPtg(root.getEncodedSize());
        }
        else {
            memPtg = new MemAreaPtg(root.getEncodedSize());
        }
        return new ParseNode(memPtg, root);
    }
    
    private static boolean needsMemFunc(final ParseNode root) {
        final Ptg token = root.getToken();
        if (token instanceof AbstractFunctionPtg) {
            return true;
        }
        if (token instanceof ExternSheetReferenceToken) {
            return true;
        }
        if (token instanceof NamePtg || token instanceof NameXPtg) {
            return true;
        }
        if (token instanceof OperationPtg || token instanceof ParenthesisPtg) {
            for (final ParseNode child : root.getChildren()) {
                if (needsMemFunc(child)) {
                    return true;
                }
            }
            return false;
        }
        return token instanceof OperandPtg && false;
    }
    
    private static void checkValidRangeOperand(final String sideName, final int currentParsePosition, final ParseNode pn) {
        if (!isValidRangeOperand(pn)) {
            throw new FormulaParseException("The " + sideName + " of the range operator ':' at position " + currentParsePosition + " is not a proper reference.");
        }
    }
    
    private static boolean isValidRangeOperand(final ParseNode a) {
        final Ptg tkn = a.getToken();
        if (tkn instanceof OperandPtg) {
            return true;
        }
        if (tkn instanceof AbstractFunctionPtg) {
            final AbstractFunctionPtg afp = (AbstractFunctionPtg)tkn;
            final byte returnClass = afp.getDefaultOperandClass();
            return 0 == returnClass;
        }
        if (tkn instanceof ValueOperatorPtg) {
            return false;
        }
        if (tkn instanceof OperationPtg) {
            return true;
        }
        if (tkn instanceof ParenthesisPtg) {
            return isValidRangeOperand(a.getChildren()[0]);
        }
        return tkn == ErrPtg.REF_INVALID;
    }
    
    private ParseNode parseRangeable() {
        this.SkipWhite();
        int savePointer = this._pointer;
        final SheetIdentifier sheetIden = this.parseSheetName();
        if (sheetIden == null) {
            this.resetPointer(savePointer);
        }
        else {
            this.SkipWhite();
            savePointer = this._pointer;
        }
        final SimpleRangePart part1 = this.parseSimpleRangePart();
        if (part1 == null) {
            if (sheetIden == null) {
                return this.parseNonRange(savePointer);
            }
            if (this.look == 35) {
                return new ParseNode(ErrPtg.valueOf(this.parseErrorLiteral()));
            }
            final String name = this.parseAsName();
            if (name.length() == 0) {
                throw new FormulaParseException("Cell reference or Named Range expected after sheet name at index " + this._pointer + ".");
            }
            final Ptg nameXPtg = this._book.getNameXPtg(name, sheetIden);
            if (nameXPtg == null) {
                throw new FormulaParseException("Specified name '" + name + "' for sheet " + sheetIden.asFormulaString() + " not found");
            }
            return new ParseNode(nameXPtg);
        }
        else {
            final boolean whiteAfterPart1 = IsWhite(this.look);
            if (whiteAfterPart1) {
                this.SkipWhite();
            }
            if (this.look == 58) {
                final int colonPos = this._pointer;
                this.GetChar();
                this.SkipWhite();
                SimpleRangePart part2 = this.parseSimpleRangePart();
                if (part2 != null && !part1.isCompatibleForArea(part2)) {
                    part2 = null;
                }
                if (part2 == null) {
                    this.resetPointer(colonPos);
                    if (!part1.isCell()) {
                        String prefix = "";
                        if (sheetIden != null) {
                            prefix = "'" + sheetIden.getSheetIdentifier().getName() + '!';
                        }
                        throw new FormulaParseException(prefix + part1.getRep() + "' is not a proper reference.");
                    }
                }
                return this.createAreaRefParseNode(sheetIden, part1, part2);
            }
            if (this.look == 46) {
                this.GetChar();
                int dotCount = 1;
                while (this.look == 46) {
                    ++dotCount;
                    this.GetChar();
                }
                final boolean whiteBeforePart2 = IsWhite(this.look);
                this.SkipWhite();
                final SimpleRangePart part3 = this.parseSimpleRangePart();
                final String part1And2 = this._formulaString.substring(savePointer - 1, this._pointer - 1);
                if (part3 == null) {
                    if (sheetIden != null) {
                        throw new FormulaParseException("Complete area reference expected after sheet name at index " + this._pointer + ".");
                    }
                    return this.parseNonRange(savePointer);
                }
                else if (whiteAfterPart1 || whiteBeforePart2) {
                    if (part1.isRowOrColumn() || part3.isRowOrColumn()) {
                        throw new FormulaParseException("Dotted range (full row or column) expression '" + part1And2 + "' must not contain whitespace.");
                    }
                    return this.createAreaRefParseNode(sheetIden, part1, part3);
                }
                else {
                    if (dotCount == 1 && part1.isRow() && part3.isRow()) {
                        return this.parseNonRange(savePointer);
                    }
                    if ((part1.isRowOrColumn() || part3.isRowOrColumn()) && dotCount != 2) {
                        throw new FormulaParseException("Dotted range (full row or column) expression '" + part1And2 + "' must have exactly 2 dots.");
                    }
                    return this.createAreaRefParseNode(sheetIden, part1, part3);
                }
            }
            else {
                if (part1.isCell() && this.isValidCellReference(part1.getRep())) {
                    return this.createAreaRefParseNode(sheetIden, part1, null);
                }
                if (sheetIden != null) {
                    throw new FormulaParseException("Second part of cell reference expected after sheet name at index " + this._pointer + ".");
                }
                return this.parseNonRange(savePointer);
            }
        }
    }
    
    private ParseNode parseStructuredReference(final String tableName) {
        if (!this._ssVersion.equals(SpreadsheetVersion.EXCEL2007)) {
            throw new FormulaParseException("Structured references work only on XSSF (Excel 2007+)!");
        }
        final Table tbl = this._book.getTable(tableName);
        if (tbl == null) {
            throw new FormulaParseException("Illegal table name: '" + tableName + "'");
        }
        final String sheetName = tbl.getSheetName();
        final int startCol = tbl.getStartColIndex();
        final int endCol = tbl.getEndColIndex();
        final int startRow = tbl.getStartRowIndex();
        final int endRow = tbl.getEndRowIndex();
        int savePtr0 = this._pointer;
        this.GetChar();
        boolean isTotalsSpec = false;
        boolean isThisRowSpec = false;
        boolean isDataSpec = false;
        boolean isHeadersSpec = false;
        boolean isAllSpec = false;
        int nSpecQuantifiers = 0;
        while (true) {
            final int savePtr2 = this._pointer;
            final String specName = this.parseAsSpecialQuantifier();
            if (specName == null) {
                this.resetPointer(savePtr2);
                break;
            }
            final String s = specName;
            switch (s) {
                case "All": {
                    isAllSpec = true;
                    break;
                }
                case "Data": {
                    isDataSpec = true;
                    break;
                }
                case "Headers": {
                    isHeadersSpec = true;
                    break;
                }
                case "This Row": {
                    isThisRowSpec = true;
                    break;
                }
                case "Totals": {
                    isTotalsSpec = true;
                    break;
                }
                default: {
                    throw new FormulaParseException("Unknown special quantifier " + specName);
                }
            }
            ++nSpecQuantifiers;
            if (this.look != 44) {
                break;
            }
            this.GetChar();
        }
        boolean isThisRow = false;
        this.SkipWhite();
        if (this.look == 64) {
            isThisRow = true;
            this.GetChar();
        }
        String endColumnName = null;
        int nColQuantifiers = 0;
        final int savePtr3 = this._pointer;
        String startColumnName = this.parseAsColumnQuantifier();
        if (startColumnName == null) {
            this.resetPointer(savePtr3);
        }
        else {
            ++nColQuantifiers;
            if (this.look == 44) {
                throw new FormulaParseException("The formula " + this._formulaString + " is illegal: you should not use ',' with column quantifiers");
            }
            if (this.look == 58) {
                this.GetChar();
                endColumnName = this.parseAsColumnQuantifier();
                ++nColQuantifiers;
                if (endColumnName == null) {
                    throw new FormulaParseException("The formula " + this._formulaString + " is illegal: the string after ':' must be column quantifier");
                }
            }
        }
        if (nColQuantifiers == 0 && nSpecQuantifiers == 0) {
            this.resetPointer(savePtr0);
            savePtr0 = this._pointer;
            startColumnName = this.parseAsColumnQuantifier();
            if (startColumnName != null) {
                ++nColQuantifiers;
            }
            else {
                this.resetPointer(savePtr0);
                final String name = this.parseAsSpecialQuantifier();
                if (name == null) {
                    throw new FormulaParseException("The formula " + this._formulaString + " is illegal");
                }
                final String s2 = name;
                switch (s2) {
                    case "All": {
                        isAllSpec = true;
                        break;
                    }
                    case "Data": {
                        isDataSpec = true;
                        break;
                    }
                    case "Headers": {
                        isHeadersSpec = true;
                        break;
                    }
                    case "This Row": {
                        isThisRowSpec = true;
                        break;
                    }
                    case "Totals": {
                        isTotalsSpec = true;
                        break;
                    }
                    default: {
                        throw new FormulaParseException("Unknown special quantifier " + name);
                    }
                }
                ++nSpecQuantifiers;
            }
        }
        else {
            this.Match(93);
        }
        if (isTotalsSpec && tbl.getTotalsRowCount() == 0) {
            return new ParseNode(ErrPtg.REF_INVALID);
        }
        if ((!isThisRow && !isThisRowSpec) || (this._rowIndex >= startRow && endRow >= this._rowIndex)) {
            int actualStartRow = startRow;
            int actualEndRow = endRow;
            int actualStartCol = startCol;
            int actualEndCol = endCol;
            if (nSpecQuantifiers > 0) {
                if (nSpecQuantifiers != 1 || !isAllSpec) {
                    if (isDataSpec && isHeadersSpec) {
                        if (tbl.getTotalsRowCount() > 0) {
                            actualEndRow = endRow - 1;
                        }
                    }
                    else if (isDataSpec && isTotalsSpec) {
                        actualStartRow = startRow + 1;
                    }
                    else if (nSpecQuantifiers == 1 && isDataSpec) {
                        actualStartRow = startRow + 1;
                        if (tbl.getTotalsRowCount() > 0) {
                            actualEndRow = endRow - 1;
                        }
                    }
                    else if (nSpecQuantifiers == 1 && isHeadersSpec) {
                        actualEndRow = actualStartRow;
                    }
                    else if (nSpecQuantifiers == 1 && isTotalsSpec) {
                        actualStartRow = actualEndRow;
                    }
                    else {
                        if ((nSpecQuantifiers != 1 || !isThisRowSpec) && !isThisRow) {
                            throw new FormulaParseException("The formula " + this._formulaString + " is illegal");
                        }
                        actualStartRow = this._rowIndex;
                        actualEndRow = this._rowIndex;
                    }
                }
            }
            else if (isThisRow) {
                actualStartRow = this._rowIndex;
                actualEndRow = this._rowIndex;
            }
            else {
                ++actualStartRow;
                if (tbl.getTotalsRowCount() > 0) {
                    --actualEndRow;
                }
            }
            if (nColQuantifiers == 2) {
                if (startColumnName == null || endColumnName == null) {
                    throw new IllegalStateException("Fatal error");
                }
                final int startIdx = tbl.findColumnIndex(startColumnName);
                final int endIdx = tbl.findColumnIndex(endColumnName);
                if (startIdx == -1 || endIdx == -1) {
                    throw new FormulaParseException("One of the columns " + startColumnName + ", " + endColumnName + " doesn't exist in table " + tbl.getName());
                }
                actualStartCol = startCol + startIdx;
                actualEndCol = startCol + endIdx;
            }
            else if (nColQuantifiers == 1 && !isThisRow) {
                if (startColumnName == null) {
                    throw new IllegalStateException("Fatal error");
                }
                final int idx = tbl.findColumnIndex(startColumnName);
                if (idx == -1) {
                    throw new FormulaParseException("The column " + startColumnName + " doesn't exist in table " + tbl.getName());
                }
                actualStartCol = (actualEndCol = startCol + idx);
            }
            final CellReference topLeft = new CellReference(actualStartRow, actualStartCol);
            final CellReference bottomRight = new CellReference(actualEndRow, actualEndCol);
            final SheetIdentifier sheetIden = new SheetIdentifier(null, new NameIdentifier(sheetName, true));
            final Ptg ptg = this._book.get3DReferencePtg(new AreaReference(topLeft, bottomRight, this._ssVersion), sheetIden);
            return new ParseNode(ptg);
        }
        if (this._rowIndex >= 0) {
            return new ParseNode(ErrPtg.VALUE_INVALID);
        }
        throw new FormulaParseException("Formula contained [#This Row] or [@] structured reference but this row < 0. Row index must be specified for row-referencing structured references.");
    }
    
    private String parseAsColumnQuantifier() {
        if (this.look != 91) {
            return null;
        }
        this.GetChar();
        if (this.look == 35) {
            return null;
        }
        if (this.look == 64) {
            this.GetChar();
        }
        final StringBuilder name = new StringBuilder();
        while (this.look != 93) {
            name.appendCodePoint(this.look);
            this.GetChar();
        }
        this.Match(93);
        return name.toString();
    }
    
    private String parseAsSpecialQuantifier() {
        if (this.look != 91) {
            return null;
        }
        this.GetChar();
        if (this.look != 35) {
            return null;
        }
        this.GetChar();
        String name = this.parseAsName();
        if (name.equals("This")) {
            name = name + ' ' + this.parseAsName();
        }
        this.Match(93);
        return name;
    }
    
    private ParseNode parseNonRange(final int savePointer) {
        this.resetPointer(savePointer);
        if (Character.isDigit(this.look)) {
            return new ParseNode(this.parseNumber());
        }
        if (this.look == 34) {
            return new ParseNode(new StringPtg(this.parseStringLiteral()));
        }
        final String name = this.parseAsName();
        if (this.look == 40) {
            return this.function(name);
        }
        if (this.look == 91) {
            return this.parseStructuredReference(name);
        }
        if (name.equalsIgnoreCase("TRUE") || name.equalsIgnoreCase("FALSE")) {
            return new ParseNode(BoolPtg.valueOf(name.equalsIgnoreCase("TRUE")));
        }
        if (this._book == null) {
            throw new IllegalStateException("Need book to evaluate name '" + name + "'");
        }
        final EvaluationName evalName = this._book.getName(name, this._sheetIndex);
        if (evalName == null) {
            throw new FormulaParseException("Specified named range '" + name + "' does not exist in the current workbook.");
        }
        if (evalName.isRange()) {
            return new ParseNode(evalName.createPtg());
        }
        throw new FormulaParseException("Specified name '" + name + "' is not a range as expected.");
    }
    
    private String parseAsName() {
        final StringBuilder sb = new StringBuilder();
        if (!Character.isLetter(this.look) && this.look != 95 && this.look != 92) {
            throw this.expected("number, string, defined name, or data table");
        }
        while (isValidDefinedNameChar(this.look)) {
            sb.appendCodePoint(this.look);
            this.GetChar();
        }
        this.SkipWhite();
        return sb.toString();
    }
    
    private static boolean isValidDefinedNameChar(final int ch) {
        if (Character.isLetterOrDigit(ch)) {
            return true;
        }
        if (ch > 128) {
            return true;
        }
        switch (ch) {
            case 46:
            case 63:
            case 92:
            case 95: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private ParseNode createAreaRefParseNode(final SheetIdentifier sheetIden, final SimpleRangePart part1, final SimpleRangePart part2) throws FormulaParseException {
        Ptg ptg;
        if (part2 == null) {
            final CellReference cr = part1.getCellReference();
            if (sheetIden == null) {
                ptg = new RefPtg(cr);
            }
            else {
                ptg = this._book.get3DReferencePtg(cr, sheetIden);
            }
        }
        else {
            final AreaReference areaRef = this.createAreaRef(part1, part2);
            if (sheetIden == null) {
                ptg = new AreaPtg(areaRef);
            }
            else {
                ptg = this._book.get3DReferencePtg(areaRef, sheetIden);
            }
        }
        return new ParseNode(ptg);
    }
    
    private AreaReference createAreaRef(final SimpleRangePart part1, final SimpleRangePart part2) {
        if (!part1.isCompatibleForArea(part2)) {
            throw new FormulaParseException("has incompatible parts: '" + part1.getRep() + "' and '" + part2.getRep() + "'.");
        }
        if (part1.isRow()) {
            return AreaReference.getWholeRow(this._ssVersion, part1.getRep(), part2.getRep());
        }
        if (part1.isColumn()) {
            return AreaReference.getWholeColumn(this._ssVersion, part1.getRep(), part2.getRep());
        }
        return new AreaReference(part1.getCellReference(), part2.getCellReference(), this._ssVersion);
    }
    
    private SimpleRangePart parseSimpleRangePart() {
        int ptr = this._pointer - 1;
        boolean hasDigits = false;
        boolean hasLetters = false;
        while (ptr < this._formulaLength) {
            final char ch = this._formulaString.charAt(ptr);
            if (Character.isDigit(ch)) {
                hasDigits = true;
            }
            else if (Character.isLetter(ch)) {
                hasLetters = true;
            }
            else if (ch != '$' && ch != '_') {
                break;
            }
            ++ptr;
        }
        if (ptr <= this._pointer - 1) {
            return null;
        }
        final String rep = this._formulaString.substring(this._pointer - 1, ptr);
        if (!FormulaParser.CELL_REF_PATTERN.matcher(rep).matches()) {
            return null;
        }
        if (hasLetters && hasDigits) {
            if (!this.isValidCellReference(rep)) {
                return null;
            }
        }
        else if (hasLetters) {
            if (!CellReference.isColumnWithinRange(rep.replace("$", ""), this._ssVersion)) {
                return null;
            }
        }
        else {
            if (!hasDigits) {
                return null;
            }
            int i;
            try {
                i = Integer.parseInt(rep.replace("$", ""));
            }
            catch (final NumberFormatException e) {
                return null;
            }
            if (i < 1 || i > this._ssVersion.getMaxRows()) {
                return null;
            }
        }
        this.resetPointer(ptr + 1);
        return new SimpleRangePart(rep, hasLetters, hasDigits);
    }
    
    private String getBookName() {
        final StringBuilder sb = new StringBuilder();
        this.GetChar();
        while (this.look != 93) {
            sb.appendCodePoint(this.look);
            this.GetChar();
        }
        this.GetChar();
        return sb.toString();
    }
    
    private SheetIdentifier parseSheetName() {
        String bookName;
        if (this.look == 91) {
            bookName = this.getBookName();
        }
        else {
            bookName = null;
        }
        if (this.look == 39) {
            this.Match(39);
            if (this.look == 91) {
                bookName = this.getBookName();
            }
            final StringBuilder sb = new StringBuilder();
            for (boolean done = this.look == 39; !done; done = (this.look != 39)) {
                sb.appendCodePoint(this.look);
                this.GetChar();
                if (this.look == 39) {
                    this.Match(39);
                }
            }
            final NameIdentifier iden = new NameIdentifier(sb.toString(), true);
            this.SkipWhite();
            if (this.look == 33) {
                this.GetChar();
                return new SheetIdentifier(bookName, iden);
            }
            if (this.look == 58) {
                return this.parseSheetRange(bookName, iden);
            }
            return null;
        }
        else if (this.look == 95 || Character.isLetter(this.look)) {
            final StringBuilder sb = new StringBuilder();
            while (isUnquotedSheetNameChar(this.look)) {
                sb.appendCodePoint(this.look);
                this.GetChar();
            }
            final NameIdentifier iden2 = new NameIdentifier(sb.toString(), false);
            this.SkipWhite();
            if (this.look == 33) {
                this.GetChar();
                return new SheetIdentifier(bookName, iden2);
            }
            if (this.look == 58) {
                return this.parseSheetRange(bookName, iden2);
            }
            return null;
        }
        else {
            if (this.look == 33 && bookName != null) {
                this.GetChar();
                return new SheetIdentifier(bookName, null);
            }
            return null;
        }
    }
    
    private SheetIdentifier parseSheetRange(final String bookname, final NameIdentifier sheet1Name) {
        this.GetChar();
        final SheetIdentifier sheet2 = this.parseSheetName();
        if (sheet2 != null) {
            return new SheetRangeIdentifier(bookname, sheet1Name, sheet2.getSheetIdentifier());
        }
        return null;
    }
    
    private static boolean isUnquotedSheetNameChar(final int ch) {
        if (Character.isLetterOrDigit(ch)) {
            return true;
        }
        if (ch > 128) {
            return true;
        }
        switch (ch) {
            case 46:
            case 95: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean isValidCellReference(final String str) {
        boolean result = CellReference.classifyCellReference(str, this._ssVersion) == CellReference.NameType.CELL;
        if (result) {
            final boolean isFunc = FunctionMetadataRegistry.getFunctionByName(str.toUpperCase(Locale.ROOT)) != null;
            if (isFunc) {
                final int savePointer = this._pointer;
                this.resetPointer(this._pointer + str.length());
                this.SkipWhite();
                result = (this.look != 40);
                this.resetPointer(savePointer);
            }
        }
        return result;
    }
    
    private ParseNode function(final String name) {
        Ptg nameToken = null;
        if (!AbstractFunctionPtg.isBuiltInFunctionName(name)) {
            if (this._book == null) {
                throw new IllegalStateException("Need book to evaluate name '" + name + "'");
            }
            EvaluationName hName = this._book.getName(name, this._sheetIndex);
            if (hName != null) {
                if (!hName.isFunctionName()) {
                    throw new FormulaParseException("Attempt to use name '" + name + "' as a function, but defined name in workbook does not refer to a function");
                }
                nameToken = hName.createPtg();
            }
            else {
                nameToken = this._book.getNameXPtg(name, null);
                if (nameToken == null) {
                    if (FormulaParser.log.check(5)) {
                        FormulaParser.log.log(5, "FormulaParser.function: Name '" + name + "' is completely unknown in the current workbook.");
                    }
                    switch (this._book.getSpreadsheetVersion()) {
                        case EXCEL97: {
                            this.addName(name);
                            hName = this._book.getName(name, this._sheetIndex);
                            nameToken = hName.createPtg();
                            break;
                        }
                        case EXCEL2007: {
                            nameToken = new NameXPxg(name);
                            break;
                        }
                        default: {
                            throw new IllegalStateException("Unexpected spreadsheet version: " + this._book.getSpreadsheetVersion().name());
                        }
                    }
                }
            }
        }
        this.Match(40);
        final ParseNode[] args = this.Arguments();
        this.Match(41);
        return this.getFunction(name, nameToken, args);
    }
    
    private void addName(final String functionName) {
        final Name name = this._book.createName();
        name.setFunction(true);
        name.setNameName(functionName);
        name.setSheetIndex(this._sheetIndex);
    }
    
    private ParseNode getFunction(final String name, final Ptg namePtg, final ParseNode[] args) {
        final FunctionMetadata fm = FunctionMetadataRegistry.getFunctionByName(name.toUpperCase(Locale.ROOT));
        final int numArgs = args.length;
        if (fm == null) {
            if (namePtg == null) {
                throw new IllegalStateException("NamePtg must be supplied for external functions");
            }
            final ParseNode[] allArgs = new ParseNode[numArgs + 1];
            allArgs[0] = new ParseNode(namePtg);
            System.arraycopy(args, 0, allArgs, 1, numArgs);
            return new ParseNode(FuncVarPtg.create(name, numArgs + 1), allArgs);
        }
        else {
            if (namePtg != null) {
                throw new IllegalStateException("NamePtg no applicable to internal functions");
            }
            final boolean isVarArgs = !fm.hasFixedArgsLength();
            final int funcIx = fm.getIndex();
            if (funcIx == 4 && args.length == 1) {
                return new ParseNode(AttrPtg.getSumSingle(), args);
            }
            this.validateNumArgs(args.length, fm);
            AbstractFunctionPtg retval;
            if (isVarArgs) {
                retval = FuncVarPtg.create(name, numArgs);
            }
            else {
                retval = FuncPtg.create(funcIx);
            }
            return new ParseNode(retval, args);
        }
    }
    
    private void validateNumArgs(final int numArgs, final FunctionMetadata fm) {
        if (numArgs < fm.getMinParams()) {
            String msg = "Too few arguments to function '" + fm.getName() + "'. ";
            if (fm.hasFixedArgsLength()) {
                msg = msg + "Expected " + fm.getMinParams();
            }
            else {
                msg = msg + "At least " + fm.getMinParams() + " were expected";
            }
            msg = msg + " but got " + numArgs + ".";
            throw new FormulaParseException(msg);
        }
        int maxArgs;
        if (fm.hasUnlimitedVarags()) {
            if (this._book != null) {
                maxArgs = this._book.getSpreadsheetVersion().getMaxFunctionArgs();
            }
            else {
                maxArgs = fm.getMaxParams();
            }
        }
        else {
            maxArgs = fm.getMaxParams();
        }
        if (numArgs > maxArgs) {
            String msg2 = "Too many arguments to function '" + fm.getName() + "'. ";
            if (fm.hasFixedArgsLength()) {
                msg2 = msg2 + "Expected " + maxArgs;
            }
            else {
                msg2 = msg2 + "At most " + maxArgs + " were expected";
            }
            msg2 = msg2 + " but got " + numArgs + ".";
            throw new FormulaParseException(msg2);
        }
    }
    
    private static boolean isArgumentDelimiter(final int ch) {
        return ch == 44 || ch == 41;
    }
    
    private ParseNode[] Arguments() {
        final List<ParseNode> temp = new ArrayList<ParseNode>(2);
        this.SkipWhite();
        if (this.look == 41) {
            return ParseNode.EMPTY_ARRAY;
        }
        boolean missedPrevArg = true;
        while (true) {
            this.SkipWhite();
            if (isArgumentDelimiter(this.look)) {
                if (missedPrevArg) {
                    temp.add(new ParseNode(MissingArgPtg.instance));
                }
                if (this.look == 41) {
                    final ParseNode[] result = new ParseNode[temp.size()];
                    temp.toArray(result);
                    return result;
                }
                this.Match(44);
                missedPrevArg = true;
            }
            else {
                temp.add(this.intersectionExpression());
                missedPrevArg = false;
                this.SkipWhite();
                if (!isArgumentDelimiter(this.look)) {
                    throw this.expected("',' or ')'");
                }
                continue;
            }
        }
    }
    
    private ParseNode powerFactor() {
        ParseNode result = this.percentFactor();
        while (true) {
            this.SkipWhite();
            if (this.look != 94) {
                break;
            }
            this.Match(94);
            final ParseNode other = this.percentFactor();
            result = new ParseNode(PowerPtg.instance, result, other);
        }
        return result;
    }
    
    private ParseNode percentFactor() {
        ParseNode result = this.parseSimpleFactor();
        while (true) {
            this.SkipWhite();
            if (this.look != 37) {
                break;
            }
            this.Match(37);
            result = new ParseNode(PercentPtg.instance, result);
        }
        return result;
    }
    
    private ParseNode parseSimpleFactor() {
        this.SkipWhite();
        switch (this.look) {
            case 35: {
                return new ParseNode(ErrPtg.valueOf(this.parseErrorLiteral()));
            }
            case 45: {
                this.Match(45);
                return this.parseUnary(false);
            }
            case 43: {
                this.Match(43);
                return this.parseUnary(true);
            }
            case 40: {
                this.Match(40);
                final ParseNode inside = this.unionExpression();
                this.Match(41);
                return new ParseNode(ParenthesisPtg.instance, inside);
            }
            case 34: {
                return new ParseNode(new StringPtg(this.parseStringLiteral()));
            }
            case 123: {
                this.Match(123);
                final ParseNode arrayNode = this.parseArray();
                this.Match(125);
                return arrayNode;
            }
            default: {
                if (IsAlpha(this.look) || Character.isDigit(this.look) || this.look == 39 || this.look == 91 || this.look == 95 || this.look == 92) {
                    return this.parseRangeExpression();
                }
                if (this.look == 46) {
                    return new ParseNode(this.parseNumber());
                }
                throw this.expected("cell ref or constant literal");
            }
        }
    }
    
    private ParseNode parseUnary(final boolean isPlus) {
        final boolean numberFollows = IsDigit(this.look) || this.look == 46;
        final ParseNode factor = this.powerFactor();
        if (numberFollows) {
            Ptg token = factor.getToken();
            if (token instanceof NumberPtg) {
                if (isPlus) {
                    return factor;
                }
                token = new NumberPtg(-((NumberPtg)token).getValue());
                return new ParseNode(token);
            }
            else if (token instanceof IntPtg) {
                if (isPlus) {
                    return factor;
                }
                final int intVal = ((IntPtg)token).getValue();
                token = new NumberPtg(-intVal);
                return new ParseNode(token);
            }
        }
        return new ParseNode(isPlus ? UnaryPlusPtg.instance : UnaryMinusPtg.instance, factor);
    }
    
    private ParseNode parseArray() {
        final List<Object[]> rowsData = new ArrayList<Object[]>();
        while (true) {
            final Object[] singleRowData = this.parseArrayRow();
            rowsData.add(singleRowData);
            if (this.look == 125) {
                final int nRows = rowsData.size();
                final Object[][] values2d = new Object[nRows][];
                rowsData.toArray(values2d);
                final int nColumns = values2d[0].length;
                this.checkRowLengths(values2d, nColumns);
                return new ParseNode(new ArrayPtg(values2d));
            }
            if (this.look != 59) {
                throw this.expected("'}' or ';'");
            }
            this.Match(59);
        }
    }
    
    private void checkRowLengths(final Object[][] values2d, final int nColumns) {
        for (int i = 0; i < values2d.length; ++i) {
            final int rowLen = values2d[i].length;
            if (rowLen != nColumns) {
                throw new FormulaParseException("Array row " + i + " has length " + rowLen + " but row 0 has length " + nColumns);
            }
        }
    }
    
    private Object[] parseArrayRow() {
        final List<Object> temp = new ArrayList<Object>();
        while (true) {
            temp.add(this.parseArrayItem());
            this.SkipWhite();
            switch (this.look) {
                case 59:
                case 125: {
                    final Object[] result = new Object[temp.size()];
                    temp.toArray(result);
                    return result;
                }
                case 44: {
                    this.Match(44);
                    continue;
                }
                default: {
                    throw this.expected("'}' or ','");
                }
            }
        }
    }
    
    private Object parseArrayItem() {
        this.SkipWhite();
        switch (this.look) {
            case 34: {
                return this.parseStringLiteral();
            }
            case 35: {
                return ErrorConstant.valueOf(this.parseErrorLiteral());
            }
            case 70:
            case 84:
            case 102:
            case 116: {
                return this.parseBooleanLiteral();
            }
            case 45: {
                this.Match(45);
                this.SkipWhite();
                return convertArrayNumber(this.parseNumber(), false);
            }
            default: {
                return convertArrayNumber(this.parseNumber(), true);
            }
        }
    }
    
    private Boolean parseBooleanLiteral() {
        final String iden = this.parseUnquotedIdentifier();
        if ("TRUE".equalsIgnoreCase(iden)) {
            return Boolean.TRUE;
        }
        if ("FALSE".equalsIgnoreCase(iden)) {
            return Boolean.FALSE;
        }
        throw this.expected("'TRUE' or 'FALSE'");
    }
    
    private static Double convertArrayNumber(final Ptg ptg, final boolean isPositive) {
        double value;
        if (ptg instanceof IntPtg) {
            value = ((IntPtg)ptg).getValue();
        }
        else {
            if (!(ptg instanceof NumberPtg)) {
                throw new RuntimeException("Unexpected ptg (" + ptg.getClass().getName() + ")");
            }
            value = ((NumberPtg)ptg).getValue();
        }
        if (!isPositive) {
            value = -value;
        }
        return value;
    }
    
    private Ptg parseNumber() {
        String number2 = null;
        String exponent = null;
        final String number3 = this.GetNum();
        if (this.look == 46) {
            this.GetChar();
            number2 = this.GetNum();
        }
        if (this.look == 69) {
            this.GetChar();
            String sign = "";
            if (this.look == 43) {
                this.GetChar();
            }
            else if (this.look == 45) {
                this.GetChar();
                sign = "-";
            }
            final String number4 = this.GetNum();
            if (number4 == null) {
                throw this.expected("Integer");
            }
            exponent = sign + number4;
        }
        if (number3 == null && number2 == null) {
            throw this.expected("Integer");
        }
        return getNumberPtgFromString(number3, number2, exponent);
    }
    
    private int parseErrorLiteral() {
        this.Match(35);
        String part1 = this.parseUnquotedIdentifier();
        if (part1 == null) {
            throw this.expected("remainder of error constant literal");
        }
        part1 = part1.toUpperCase(Locale.ROOT);
        switch (part1.charAt(0)) {
            case 'V': {
                final FormulaError fe = FormulaError.VALUE;
                if (part1.equals(fe.name())) {
                    this.Match(33);
                    return fe.getCode();
                }
                throw this.expected(fe.getString());
            }
            case 'R': {
                final FormulaError fe = FormulaError.REF;
                if (part1.equals(fe.name())) {
                    this.Match(33);
                    return fe.getCode();
                }
                throw this.expected(fe.getString());
            }
            case 'D': {
                final FormulaError fe = FormulaError.DIV0;
                if (part1.equals("DIV")) {
                    this.Match(47);
                    this.Match(48);
                    this.Match(33);
                    return fe.getCode();
                }
                throw this.expected(fe.getString());
            }
            case 'N': {
                FormulaError fe = FormulaError.NAME;
                if (part1.equals(fe.name())) {
                    this.Match(63);
                    return fe.getCode();
                }
                fe = FormulaError.NUM;
                if (part1.equals(fe.name())) {
                    this.Match(33);
                    return fe.getCode();
                }
                fe = FormulaError.NULL;
                if (part1.equals(fe.name())) {
                    this.Match(33);
                    return fe.getCode();
                }
                fe = FormulaError.NA;
                if (!part1.equals("N")) {
                    throw this.expected("#NAME?, #NUM!, #NULL! or #N/A");
                }
                this.Match(47);
                if (this.look != 65 && this.look != 97) {
                    throw this.expected(fe.getString());
                }
                this.Match(this.look);
                return fe.getCode();
            }
            default: {
                throw this.expected("#VALUE!, #REF!, #DIV/0!, #NAME?, #NUM!, #NULL! or #N/A");
            }
        }
    }
    
    private String parseUnquotedIdentifier() {
        if (this.look == 39) {
            throw this.expected("unquoted identifier");
        }
        final StringBuilder sb = new StringBuilder();
        while (Character.isLetterOrDigit(this.look) || this.look == 46) {
            sb.appendCodePoint(this.look);
            this.GetChar();
        }
        if (sb.length() < 1) {
            return null;
        }
        return sb.toString();
    }
    
    private static Ptg getNumberPtgFromString(final String number1, final String number2, final String exponent) {
        final StringBuilder number3 = new StringBuilder();
        if (number2 != null) {
            if (number1 != null) {
                number3.append(number1);
            }
            number3.append('.');
            number3.append(number2);
            if (exponent != null) {
                number3.append('E');
                number3.append(exponent);
            }
            return new NumberPtg(number3.toString());
        }
        number3.append(number1);
        if (exponent != null) {
            number3.append('E');
            number3.append(exponent);
        }
        final String numberStr = number3.toString();
        int intVal;
        try {
            intVal = Integer.parseInt(numberStr);
        }
        catch (final NumberFormatException e) {
            return new NumberPtg(numberStr);
        }
        if (IntPtg.isInRange(intVal)) {
            return new IntPtg(intVal);
        }
        return new NumberPtg(numberStr);
    }
    
    private String parseStringLiteral() {
        this.Match(34);
        final StringBuilder token = new StringBuilder();
        while (true) {
            if (this.look == 34) {
                this.GetChar();
                if (this.look != 34) {
                    break;
                }
            }
            token.appendCodePoint(this.look);
            this.GetChar();
        }
        return token.toString();
    }
    
    private ParseNode Term() {
        ParseNode result = this.powerFactor();
        while (true) {
            this.SkipWhite();
            Ptg operator = null;
            switch (this.look) {
                case 42: {
                    this.Match(42);
                    operator = MultiplyPtg.instance;
                    break;
                }
                case 47: {
                    this.Match(47);
                    operator = DividePtg.instance;
                    break;
                }
                default: {
                    return result;
                }
            }
            final ParseNode other = this.powerFactor();
            result = new ParseNode(operator, result, other);
        }
    }
    
    private ParseNode unionExpression() {
        ParseNode result = this.intersectionExpression();
        boolean hasUnions = false;
        while (true) {
            this.SkipWhite();
            if (this.look != 44) {
                break;
            }
            this.GetChar();
            hasUnions = true;
            final ParseNode other = this.intersectionExpression();
            result = new ParseNode(UnionPtg.instance, result, other);
        }
        if (hasUnions) {
            return augmentWithMemPtg(result);
        }
        return result;
    }
    
    private ParseNode intersectionExpression() {
        ParseNode result = this.comparisonExpression();
        boolean hasIntersections = false;
        while (true) {
            this.SkipWhite();
            if (this._inIntersection) {
                final int savePointer = this._pointer;
                try {
                    final ParseNode other = this.comparisonExpression();
                    result = new ParseNode(IntersectionPtg.instance, result, other);
                    hasIntersections = true;
                    continue;
                }
                catch (final FormulaParseException e) {
                    this.resetPointer(savePointer);
                }
                break;
            }
            break;
        }
        if (hasIntersections) {
            return augmentWithMemPtg(result);
        }
        return result;
    }
    
    private ParseNode comparisonExpression() {
        ParseNode result = this.concatExpression();
        while (true) {
            this.SkipWhite();
            switch (this.look) {
                case 60:
                case 61:
                case 62: {
                    final Ptg comparisonToken = this.getComparisonToken();
                    final ParseNode other = this.concatExpression();
                    result = new ParseNode(comparisonToken, result, other);
                    continue;
                }
                default: {
                    return result;
                }
            }
        }
    }
    
    private Ptg getComparisonToken() {
        if (this.look == 61) {
            this.Match(this.look);
            return EqualPtg.instance;
        }
        final boolean isGreater = this.look == 62;
        this.Match(this.look);
        if (isGreater) {
            if (this.look == 61) {
                this.Match(61);
                return GreaterEqualPtg.instance;
            }
            return GreaterThanPtg.instance;
        }
        else {
            switch (this.look) {
                case 61: {
                    this.Match(61);
                    return LessEqualPtg.instance;
                }
                case 62: {
                    this.Match(62);
                    return NotEqualPtg.instance;
                }
                default: {
                    return LessThanPtg.instance;
                }
            }
        }
    }
    
    private ParseNode concatExpression() {
        ParseNode result = this.additiveExpression();
        while (true) {
            this.SkipWhite();
            if (this.look != 38) {
                break;
            }
            this.Match(38);
            final ParseNode other = this.additiveExpression();
            result = new ParseNode(ConcatPtg.instance, result, other);
        }
        return result;
    }
    
    private ParseNode additiveExpression() {
        ParseNode result = this.Term();
        while (true) {
            this.SkipWhite();
            Ptg operator = null;
            switch (this.look) {
                case 43: {
                    this.Match(43);
                    operator = AddPtg.instance;
                    break;
                }
                case 45: {
                    this.Match(45);
                    operator = SubtractPtg.instance;
                    break;
                }
                default: {
                    return result;
                }
            }
            final ParseNode other = this.Term();
            result = new ParseNode(operator, result, other);
        }
    }
    
    private void parse() {
        this._pointer = 0;
        this.GetChar();
        this._rootNode = this.unionExpression();
        if (this._pointer <= this._formulaLength) {
            final String msg = "Unused input [" + this._formulaString.substring(this._pointer - 1) + "] after attempting to parse the formula [" + this._formulaString + "]";
            throw new FormulaParseException(msg);
        }
    }
    
    private Ptg[] getRPNPtg(final FormulaType formulaType) {
        final OperandClassTransformer oct = new OperandClassTransformer(formulaType);
        oct.transformFormula(this._rootNode);
        return ParseNode.toTokenArray(this._rootNode);
    }
    
    static {
        log = POILogFactory.getLogger(FormulaParser.class);
        CELL_REF_PATTERN = Pattern.compile("(\\$?[A-Za-z]+)?(\\$?[0-9]+)?");
    }
    
    private static final class SimpleRangePart
    {
        private final Type _type;
        private final String _rep;
        
        public SimpleRangePart(final String rep, final boolean hasLetters, final boolean hasNumbers) {
            this._rep = rep;
            this._type = Type.get(hasLetters, hasNumbers);
        }
        
        public boolean isCell() {
            return this._type == Type.CELL;
        }
        
        public boolean isRowOrColumn() {
            return this._type != Type.CELL;
        }
        
        public CellReference getCellReference() {
            if (this._type != Type.CELL) {
                throw new IllegalStateException("Not applicable to this type");
            }
            return new CellReference(this._rep);
        }
        
        public boolean isColumn() {
            return this._type == Type.COLUMN;
        }
        
        public boolean isRow() {
            return this._type == Type.ROW;
        }
        
        public String getRep() {
            return this._rep;
        }
        
        public boolean isCompatibleForArea(final SimpleRangePart part2) {
            return this._type == part2._type;
        }
        
        @Override
        public String toString() {
            return this.getClass().getName() + " [" + this._rep + "]";
        }
        
        private enum Type
        {
            CELL, 
            ROW, 
            COLUMN;
            
            public static Type get(final boolean hasLetters, final boolean hasDigits) {
                if (hasLetters) {
                    return hasDigits ? Type.CELL : Type.COLUMN;
                }
                if (!hasDigits) {
                    throw new IllegalArgumentException("must have either letters or numbers");
                }
                return Type.ROW;
            }
        }
    }
}
