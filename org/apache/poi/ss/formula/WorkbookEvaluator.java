package org.apache.poi.ss.formula;

import java.util.Collections;
import org.apache.poi.ss.formula.atp.AnalysisToolPak;
import java.util.TreeSet;
import java.util.Collection;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.util.CellRangeAddressBase;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.eval.FunctionNameEval;
import org.apache.poi.ss.formula.eval.ExternalNameEval;
import org.apache.poi.ss.formula.ptg.ExpPtg;
import org.apache.poi.ss.formula.ptg.UnknownPtg;
import org.apache.poi.ss.formula.ptg.ArrayPtg;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.formula.ptg.RefPtg;
import org.apache.poi.ss.formula.ptg.Area3DPxg;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.formula.ptg.Ref3DPxg;
import org.apache.poi.ss.formula.ptg.Ref3DPtg;
import org.apache.poi.ss.formula.ptg.DeletedRef3DPtg;
import org.apache.poi.ss.formula.ptg.DeletedArea3DPtg;
import org.apache.poi.ss.formula.ptg.RefErrorPtg;
import org.apache.poi.ss.formula.ptg.AreaErrPtg;
import org.apache.poi.ss.formula.ptg.MissingArgPtg;
import org.apache.poi.ss.formula.ptg.ErrPtg;
import org.apache.poi.ss.formula.ptg.BoolPtg;
import org.apache.poi.ss.formula.ptg.StringPtg;
import org.apache.poi.ss.formula.ptg.NumberPtg;
import org.apache.poi.ss.formula.ptg.IntPtg;
import org.apache.poi.ss.formula.ptg.NameXPxg;
import org.apache.poi.ss.formula.ptg.NameXPtg;
import org.apache.poi.ss.formula.ptg.NamePtg;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.formula.functions.ArrayMode;
import org.apache.poi.ss.formula.eval.FunctionEval;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.ptg.OperationPtg;
import org.apache.poi.ss.formula.eval.RefListEval;
import org.apache.poi.ss.formula.ptg.UnionPtg;
import org.apache.poi.ss.formula.ptg.MemErrPtg;
import org.apache.poi.ss.formula.ptg.MemAreaPtg;
import org.apache.poi.ss.formula.ptg.MemFuncPtg;
import org.apache.poi.ss.formula.ptg.ControlPtg;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.functions.IfFunc;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.functions.Choose;
import org.apache.poi.ss.formula.ptg.FuncVarPtg;
import org.apache.poi.ss.formula.ptg.AttrPtg;
import java.util.Stack;
import java.util.Arrays;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.formula.eval.ValueEval;
import java.util.IdentityHashMap;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.formula.udf.AggregatingUDFFinder;
import java.util.Map;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Internal;

@Internal
public final class WorkbookEvaluator
{
    private static final POILogger LOG;
    private final EvaluationWorkbook _workbook;
    private EvaluationCache _cache;
    private int _workbookIx;
    private final IEvaluationListener _evaluationListener;
    private final Map<EvaluationSheet, Integer> _sheetIndexesBySheet;
    private final Map<String, Integer> _sheetIndexesByName;
    private CollaboratingWorkbooksEnvironment _collaboratingWorkbookEnvironment;
    private final IStabilityClassifier _stabilityClassifier;
    private final AggregatingUDFFinder _udfFinder;
    private boolean _ignoreMissingWorkbooks;
    private boolean dbgEvaluationOutputForNextEval;
    private final POILogger EVAL_LOG;
    private int dbgEvaluationOutputIndent;
    
    public WorkbookEvaluator(final EvaluationWorkbook workbook, final IStabilityClassifier stabilityClassifier, final UDFFinder udfFinder) {
        this(workbook, null, stabilityClassifier, udfFinder);
    }
    
    WorkbookEvaluator(final EvaluationWorkbook workbook, final IEvaluationListener evaluationListener, final IStabilityClassifier stabilityClassifier, final UDFFinder udfFinder) {
        this.EVAL_LOG = POILogFactory.getLogger("POI.FormulaEval");
        this.dbgEvaluationOutputIndent = -1;
        this._workbook = workbook;
        this._evaluationListener = evaluationListener;
        this._cache = new EvaluationCache(evaluationListener);
        this._sheetIndexesBySheet = new IdentityHashMap<EvaluationSheet, Integer>();
        this._sheetIndexesByName = new IdentityHashMap<String, Integer>();
        this._collaboratingWorkbookEnvironment = CollaboratingWorkbooksEnvironment.EMPTY;
        this._workbookIx = 0;
        this._stabilityClassifier = stabilityClassifier;
        final AggregatingUDFFinder defaultToolkit = (workbook == null) ? null : ((AggregatingUDFFinder)workbook.getUDFFinder());
        if (defaultToolkit != null && udfFinder != null) {
            defaultToolkit.add(udfFinder);
        }
        this._udfFinder = defaultToolkit;
    }
    
    String getSheetName(final int sheetIndex) {
        return this._workbook.getSheetName(sheetIndex);
    }
    
    EvaluationSheet getSheet(final int sheetIndex) {
        return this._workbook.getSheet(sheetIndex);
    }
    
    EvaluationWorkbook getWorkbook() {
        return this._workbook;
    }
    
    EvaluationName getName(final String name, final int sheetIndex) {
        return this._workbook.getName(name, sheetIndex);
    }
    
    private static boolean isDebugLogEnabled() {
        return WorkbookEvaluator.LOG.check(1);
    }
    
    private static boolean isInfoLogEnabled() {
        return WorkbookEvaluator.LOG.check(3);
    }
    
    private static void logDebug(final String s) {
        if (isDebugLogEnabled()) {
            WorkbookEvaluator.LOG.log(1, s);
        }
    }
    
    private static void logInfo(final String s) {
        if (isInfoLogEnabled()) {
            WorkbookEvaluator.LOG.log(3, s);
        }
    }
    
    void attachToEnvironment(final CollaboratingWorkbooksEnvironment collaboratingWorkbooksEnvironment, final EvaluationCache cache, final int workbookIx) {
        this._collaboratingWorkbookEnvironment = collaboratingWorkbooksEnvironment;
        this._cache = cache;
        this._workbookIx = workbookIx;
    }
    
    CollaboratingWorkbooksEnvironment getEnvironment() {
        return this._collaboratingWorkbookEnvironment;
    }
    
    void detachFromEnvironment() {
        this._collaboratingWorkbookEnvironment = CollaboratingWorkbooksEnvironment.EMPTY;
        this._cache = new EvaluationCache(this._evaluationListener);
        this._workbookIx = 0;
    }
    
    WorkbookEvaluator getOtherWorkbookEvaluator(final String workbookName) throws CollaboratingWorkbooksEnvironment.WorkbookNotFoundException {
        return this._collaboratingWorkbookEnvironment.getWorkbookEvaluator(workbookName);
    }
    
    IEvaluationListener getEvaluationListener() {
        return this._evaluationListener;
    }
    
    public void clearAllCachedResultValues() {
        this._cache.clear();
        this._sheetIndexesBySheet.clear();
        this._workbook.clearAllCachedResultValues();
    }
    
    public void notifyUpdateCell(final EvaluationCell cell) {
        final int sheetIndex = this.getSheetIndex(cell.getSheet());
        this._cache.notifyUpdateCell(this._workbookIx, sheetIndex, cell);
    }
    
    public void notifyDeleteCell(final EvaluationCell cell) {
        final int sheetIndex = this.getSheetIndex(cell.getSheet());
        this._cache.notifyDeleteCell(this._workbookIx, sheetIndex, cell);
    }
    
    private int getSheetIndex(final EvaluationSheet sheet) {
        Integer result = this._sheetIndexesBySheet.get(sheet);
        if (result == null) {
            final int sheetIndex = this._workbook.getSheetIndex(sheet);
            if (sheetIndex < 0) {
                throw new RuntimeException("Specified sheet from a different book");
            }
            result = sheetIndex;
            this._sheetIndexesBySheet.put(sheet, result);
        }
        return result;
    }
    
    public ValueEval evaluate(final EvaluationCell srcCell) {
        final int sheetIndex = this.getSheetIndex(srcCell.getSheet());
        return this.evaluateAny(srcCell, sheetIndex, srcCell.getRowIndex(), srcCell.getColumnIndex(), new EvaluationTracker(this._cache));
    }
    
    int getSheetIndex(final String sheetName) {
        Integer result = this._sheetIndexesByName.get(sheetName);
        if (result == null) {
            final int sheetIndex = this._workbook.getSheetIndex(sheetName);
            if (sheetIndex < 0) {
                return -1;
            }
            result = sheetIndex;
            this._sheetIndexesByName.put(sheetName, result);
        }
        return result;
    }
    
    int getSheetIndexByExternIndex(final int externSheetIndex) {
        return this._workbook.convertFromExternSheetIndex(externSheetIndex);
    }
    
    private ValueEval evaluateAny(final EvaluationCell srcCell, final int sheetIndex, final int rowIndex, final int columnIndex, final EvaluationTracker tracker) {
        final boolean shouldCellDependencyBeRecorded = this._stabilityClassifier == null || !this._stabilityClassifier.isCellFinal(sheetIndex, rowIndex, columnIndex);
        if (srcCell == null || srcCell.getCellType() != CellType.FORMULA) {
            final ValueEval result = getValueFromNonFormulaCell(srcCell);
            if (shouldCellDependencyBeRecorded) {
                tracker.acceptPlainValueDependency(this._workbook, this._workbookIx, sheetIndex, rowIndex, columnIndex, result);
            }
            return result;
        }
        final FormulaCellCacheEntry cce = this._cache.getOrCreateFormulaCellEntry(srcCell);
        if (shouldCellDependencyBeRecorded || cce.isInputSensitive()) {
            tracker.acceptFormulaDependency(cce);
        }
        final IEvaluationListener evalListener = this._evaluationListener;
        if (cce.getValue() != null) {
            if (evalListener != null) {
                evalListener.onCacheHit(sheetIndex, rowIndex, columnIndex, cce.getValue());
            }
            return cce.getValue();
        }
        if (!tracker.startEvaluate(cce)) {
            return ErrorEval.CIRCULAR_REF_ERROR;
        }
        ValueEval result2 = null;
        Label_0486: {
            try {
                final Ptg[] ptgs = this._workbook.getFormulaTokens(srcCell);
                final OperationEvaluationContext ec = new OperationEvaluationContext(this, this._workbook, sheetIndex, rowIndex, columnIndex, tracker);
                if (evalListener == null) {
                    result2 = this.evaluateFormula(ec, ptgs);
                }
                else {
                    evalListener.onStartEvaluate(srcCell, cce);
                    result2 = this.evaluateFormula(ec, ptgs);
                    evalListener.onEndEvaluate(cce, result2);
                }
                tracker.updateCacheResult(result2);
            }
            catch (final NotImplementedException e) {
                throw this.addExceptionInfo(e, sheetIndex, rowIndex, columnIndex);
            }
            catch (final RuntimeException re) {
                if (re.getCause() instanceof CollaboratingWorkbooksEnvironment.WorkbookNotFoundException && this._ignoreMissingWorkbooks) {
                    logInfo(re.getCause().getMessage() + " - Continuing with cached value!");
                    switch (srcCell.getCachedFormulaResultType()) {
                        case NUMERIC: {
                            result2 = new NumberEval(srcCell.getNumericCellValue());
                            break;
                        }
                        case STRING: {
                            result2 = new StringEval(srcCell.getStringCellValue());
                            break;
                        }
                        case BLANK: {
                            result2 = BlankEval.instance;
                            break;
                        }
                        case BOOLEAN: {
                            result2 = BoolEval.valueOf(srcCell.getBooleanCellValue());
                            break;
                        }
                        case ERROR: {
                            result2 = ErrorEval.valueOf(srcCell.getErrorCellValue());
                            break;
                        }
                        default: {
                            throw new RuntimeException("Unexpected cell type '" + srcCell.getCellType() + "' found!");
                        }
                    }
                    break Label_0486;
                }
                throw re;
            }
            finally {
                tracker.endEvaluate(cce);
            }
        }
        if (isDebugLogEnabled()) {
            final String sheetName = this.getSheetName(sheetIndex);
            final CellReference cr = new CellReference(rowIndex, columnIndex);
            logDebug("Evaluated " + sheetName + "!" + cr.formatAsString() + " to " + result2);
        }
        return result2;
    }
    
    private NotImplementedException addExceptionInfo(final NotImplementedException inner, final int sheetIndex, final int rowIndex, final int columnIndex) {
        try {
            final String sheetName = this._workbook.getSheetName(sheetIndex);
            final CellReference cr = new CellReference(sheetName, rowIndex, columnIndex, false, false);
            final String msg = "Error evaluating cell " + cr.formatAsString();
            return new NotImplementedException(msg, inner);
        }
        catch (final Exception e) {
            WorkbookEvaluator.LOG.log(7, "Can't add exception info", e);
            return inner;
        }
    }
    
    static ValueEval getValueFromNonFormulaCell(final EvaluationCell cell) {
        if (cell == null) {
            return BlankEval.instance;
        }
        final CellType cellType = cell.getCellType();
        switch (cellType) {
            case NUMERIC: {
                return new NumberEval(cell.getNumericCellValue());
            }
            case STRING: {
                return new StringEval(cell.getStringCellValue());
            }
            case BOOLEAN: {
                return BoolEval.valueOf(cell.getBooleanCellValue());
            }
            case BLANK: {
                return BlankEval.instance;
            }
            case ERROR: {
                return ErrorEval.valueOf(cell.getErrorCellValue());
            }
            default: {
                throw new RuntimeException("Unexpected cell type (" + cellType + ")");
            }
        }
    }
    
    @Internal
    ValueEval evaluateFormula(final OperationEvaluationContext ec, final Ptg[] ptgs) {
        String dbgIndentStr = "";
        if (this.dbgEvaluationOutputForNextEval) {
            this.dbgEvaluationOutputIndent = 1;
            this.dbgEvaluationOutputForNextEval = false;
        }
        if (this.dbgEvaluationOutputIndent > 0) {
            dbgIndentStr = "                                                                                                    ";
            dbgIndentStr = dbgIndentStr.substring(0, Math.min(dbgIndentStr.length(), this.dbgEvaluationOutputIndent * 2));
            this.EVAL_LOG.log(5, dbgIndentStr + "- evaluateFormula('" + ec.getRefEvaluatorForCurrentSheet().getSheetNameRange() + "'/" + new CellReference(ec.getRowIndex(), ec.getColumnIndex()).formatAsString() + "): " + Arrays.toString(ptgs).replaceAll("\\Qorg.apache.poi.ss.formula.ptg.\\E", ""));
            ++this.dbgEvaluationOutputIndent;
        }
        final EvaluationSheet evalSheet = ec.getWorkbook().getSheet(ec.getSheetIndex());
        final EvaluationCell evalCell = evalSheet.getCell(ec.getRowIndex(), ec.getColumnIndex());
        final Stack<ValueEval> stack = new Stack<ValueEval>();
        for (int i = 0, iSize = ptgs.length; i < iSize; ++i) {
            Ptg ptg = ptgs[i];
            if (this.dbgEvaluationOutputIndent > 0) {
                this.EVAL_LOG.log(3, dbgIndentStr + "  * ptg " + i + ": " + ptg + ", stack: " + stack);
            }
            if (ptg instanceof AttrPtg) {
                AttrPtg attrPtg = (AttrPtg)ptg;
                if (attrPtg.isSum()) {
                    ptg = FuncVarPtg.SUM;
                }
                if (attrPtg.isOptimizedChoose()) {
                    final ValueEval arg0 = stack.pop();
                    final int[] jumpTable = attrPtg.getJumpTable();
                    final int nChoices = jumpTable.length;
                    int dist;
                    try {
                        final int switchIndex = Choose.evaluateFirstArg(arg0, ec.getRowIndex(), ec.getColumnIndex());
                        if (switchIndex < 1 || switchIndex > nChoices) {
                            stack.push(ErrorEval.VALUE_INVALID);
                            dist = attrPtg.getChooseFuncOffset() + 4;
                        }
                        else {
                            dist = jumpTable[switchIndex - 1];
                        }
                    }
                    catch (final EvaluationException e) {
                        stack.push(e.getErrorEval());
                        dist = attrPtg.getChooseFuncOffset() + 4;
                    }
                    dist -= nChoices * 2 + 2;
                    i += countTokensToBeSkipped(ptgs, i, dist);
                    continue;
                }
                if (attrPtg.isOptimizedIf()) {
                    if (!evalCell.isPartOfArrayFormulaGroup()) {
                        final ValueEval arg0 = stack.pop();
                        boolean evaluatedPredicate;
                        try {
                            evaluatedPredicate = IfFunc.evaluateFirstArg(arg0, ec.getRowIndex(), ec.getColumnIndex());
                        }
                        catch (final EvaluationException e2) {
                            stack.push(e2.getErrorEval());
                            int dist2 = attrPtg.getData();
                            i += countTokensToBeSkipped(ptgs, i, dist2);
                            attrPtg = (AttrPtg)ptgs[i];
                            dist2 = attrPtg.getData() + 1;
                            i += countTokensToBeSkipped(ptgs, i, dist2);
                            continue;
                        }
                        if (!evaluatedPredicate) {
                            final int dist = attrPtg.getData();
                            i += countTokensToBeSkipped(ptgs, i, dist);
                            final Ptg nextPtg = ptgs[i + 1];
                            if (ptgs[i] instanceof AttrPtg && nextPtg instanceof FuncVarPtg && ((FuncVarPtg)nextPtg).getFunctionIndex() == 1) {
                                stack.push(arg0);
                                stack.push(BoolEval.FALSE);
                            }
                        }
                    }
                    continue;
                }
                else if (attrPtg.isSkip() && !evalCell.isPartOfArrayFormulaGroup()) {
                    final int dist3 = attrPtg.getData() + 1;
                    i += countTokensToBeSkipped(ptgs, i, dist3);
                    if (stack.peek() == MissingArgEval.instance) {
                        stack.pop();
                        stack.push(BlankEval.instance);
                    }
                    continue;
                }
            }
            if (!(ptg instanceof ControlPtg)) {
                if (!(ptg instanceof MemFuncPtg)) {
                    if (!(ptg instanceof MemAreaPtg)) {
                        if (!(ptg instanceof MemErrPtg)) {
                            if (ptg instanceof UnionPtg) {
                                final ValueEval v2 = stack.pop();
                                final ValueEval v3 = stack.pop();
                                stack.push(new RefListEval(v3, v2));
                            }
                            else {
                                ValueEval opResult;
                                if (ptg instanceof OperationPtg) {
                                    final OperationPtg optg = (OperationPtg)ptg;
                                    final int numops = optg.getNumberOfOperands();
                                    final ValueEval[] ops = new ValueEval[numops];
                                    boolean areaArg = false;
                                    for (int j = numops - 1; j >= 0; --j) {
                                        final ValueEval p = stack.pop();
                                        ops[j] = p;
                                        if (p instanceof AreaEval) {
                                            areaArg = true;
                                        }
                                    }
                                    boolean arrayMode = false;
                                    if (areaArg) {
                                        for (int ii = i; ii < iSize; ++ii) {
                                            if (ptgs[ii] instanceof FuncVarPtg) {
                                                final FuncVarPtg f = (FuncVarPtg)ptgs[ii];
                                                try {
                                                    final Function func = FunctionEval.getBasicFunction(f.getFunctionIndex());
                                                    if (func != null && func instanceof ArrayMode) {
                                                        arrayMode = true;
                                                    }
                                                }
                                                catch (final NotImplementedException ex) {}
                                                break;
                                            }
                                        }
                                    }
                                    ec.setArrayMode(arrayMode);
                                    opResult = OperationEvaluatorFactory.evaluate(optg, ops, ec);
                                    ec.setArrayMode(false);
                                }
                                else {
                                    opResult = this.getEvalForPtg(ptg, ec);
                                }
                                if (opResult == null) {
                                    throw new RuntimeException("Evaluation result must not be null");
                                }
                                stack.push(opResult);
                                if (this.dbgEvaluationOutputIndent > 0) {
                                    this.EVAL_LOG.log(3, dbgIndentStr + "    = " + opResult);
                                }
                            }
                        }
                    }
                }
            }
        }
        final ValueEval value = stack.pop();
        if (!stack.isEmpty()) {
            throw new IllegalStateException("evaluation stack not empty");
        }
        ValueEval result;
        if (ec.isSingleValue()) {
            result = dereferenceResult(value, ec);
        }
        else {
            result = value;
        }
        if (this.dbgEvaluationOutputIndent > 0) {
            this.EVAL_LOG.log(3, dbgIndentStr + "finshed eval of " + new CellReference(ec.getRowIndex(), ec.getColumnIndex()).formatAsString() + ": " + result);
            --this.dbgEvaluationOutputIndent;
            if (this.dbgEvaluationOutputIndent == 1) {
                this.dbgEvaluationOutputIndent = -1;
            }
        }
        return result;
    }
    
    private static int countTokensToBeSkipped(final Ptg[] ptgs, final int startIndex, final int distInBytes) {
        int remBytes = distInBytes;
        int index = startIndex;
        while (remBytes != 0) {
            ++index;
            remBytes -= ptgs[index].getSize();
            if (remBytes < 0) {
                throw new RuntimeException("Bad skip distance (wrong token size calculation).");
            }
            if (index >= ptgs.length) {
                throw new RuntimeException("Skip distance too far (ran out of formula tokens).");
            }
        }
        return index - startIndex;
    }
    
    private static ValueEval dereferenceResult(final ValueEval evaluationResult, final OperationEvaluationContext ec) {
        if (ec == null) {
            throw new IllegalArgumentException("OperationEvaluationContext ec is null");
        }
        if (ec.getWorkbook() == null) {
            throw new IllegalArgumentException("OperationEvaluationContext ec.getWorkbook() is null");
        }
        final EvaluationSheet evalSheet = ec.getWorkbook().getSheet(ec.getSheetIndex());
        final EvaluationCell evalCell = evalSheet.getCell(ec.getRowIndex(), ec.getColumnIndex());
        ValueEval value;
        if (evalCell != null && evalCell.isPartOfArrayFormulaGroup() && evaluationResult instanceof AreaEval) {
            value = OperandResolver.getElementFromArray((AreaEval)evaluationResult, evalCell);
        }
        else {
            value = dereferenceResult(evaluationResult, ec.getRowIndex(), ec.getColumnIndex());
        }
        return value;
    }
    
    public static ValueEval dereferenceResult(final ValueEval evaluationResult, final int srcRowNum, final int srcColNum) {
        ValueEval value;
        try {
            value = OperandResolver.getSingleValue(evaluationResult, srcRowNum, srcColNum);
        }
        catch (final EvaluationException e) {
            return e.getErrorEval();
        }
        if (value == BlankEval.instance) {
            return NumberEval.ZERO;
        }
        return value;
    }
    
    private ValueEval getEvalForPtg(final Ptg ptg, final OperationEvaluationContext ec) {
        if (ptg instanceof NamePtg) {
            final NamePtg namePtg = (NamePtg)ptg;
            final EvaluationName nameRecord = this._workbook.getName(namePtg);
            return this.getEvalForNameRecord(nameRecord, ec);
        }
        if (ptg instanceof NameXPtg) {
            return this.processNameEval(ec.getNameXEval((NameXPtg)ptg), ec);
        }
        if (ptg instanceof NameXPxg) {
            return this.processNameEval(ec.getNameXEval((NameXPxg)ptg), ec);
        }
        if (ptg instanceof IntPtg) {
            return new NumberEval(((IntPtg)ptg).getValue());
        }
        if (ptg instanceof NumberPtg) {
            return new NumberEval(((NumberPtg)ptg).getValue());
        }
        if (ptg instanceof StringPtg) {
            return new StringEval(((StringPtg)ptg).getValue());
        }
        if (ptg instanceof BoolPtg) {
            return BoolEval.valueOf(((BoolPtg)ptg).getValue());
        }
        if (ptg instanceof ErrPtg) {
            return ErrorEval.valueOf(((ErrPtg)ptg).getErrorCode());
        }
        if (ptg instanceof MissingArgPtg) {
            return MissingArgEval.instance;
        }
        if (ptg instanceof AreaErrPtg || ptg instanceof RefErrorPtg || ptg instanceof DeletedArea3DPtg || ptg instanceof DeletedRef3DPtg) {
            return ErrorEval.REF_INVALID;
        }
        if (ptg instanceof Ref3DPtg) {
            return ec.getRef3DEval((Ref3DPtg)ptg);
        }
        if (ptg instanceof Ref3DPxg) {
            return ec.getRef3DEval((Ref3DPxg)ptg);
        }
        if (ptg instanceof Area3DPtg) {
            return ec.getArea3DEval((Area3DPtg)ptg);
        }
        if (ptg instanceof Area3DPxg) {
            return ec.getArea3DEval((Area3DPxg)ptg);
        }
        if (ptg instanceof RefPtg) {
            final RefPtg rptg = (RefPtg)ptg;
            return ec.getRefEval(rptg.getRow(), rptg.getColumn());
        }
        if (ptg instanceof AreaPtg) {
            final AreaPtg aptg = (AreaPtg)ptg;
            return ec.getAreaEval(aptg.getFirstRow(), aptg.getFirstColumn(), aptg.getLastRow(), aptg.getLastColumn());
        }
        if (ptg instanceof ArrayPtg) {
            final ArrayPtg aptg2 = (ArrayPtg)ptg;
            return ec.getAreaValueEval(0, 0, aptg2.getRowCount() - 1, aptg2.getColumnCount() - 1, aptg2.getTokenArrayValues());
        }
        if (ptg instanceof UnknownPtg) {
            throw new RuntimeException("UnknownPtg not allowed");
        }
        if (ptg instanceof ExpPtg) {
            throw new RuntimeException("ExpPtg currently not supported");
        }
        throw new RuntimeException("Unexpected ptg class (" + ptg.getClass().getName() + ")");
    }
    
    private ValueEval processNameEval(final ValueEval eval, final OperationEvaluationContext ec) {
        if (eval instanceof ExternalNameEval) {
            final EvaluationName name = ((ExternalNameEval)eval).getName();
            return this.getEvalForNameRecord(name, ec);
        }
        return eval;
    }
    
    private ValueEval getEvalForNameRecord(final EvaluationName nameRecord, final OperationEvaluationContext ec) {
        if (nameRecord.isFunctionName()) {
            return new FunctionNameEval(nameRecord.getNameText());
        }
        if (nameRecord.hasFormula()) {
            return this.evaluateNameFormula(nameRecord.getNameDefinition(), ec);
        }
        throw new RuntimeException("Don't know how to evaluate name '" + nameRecord.getNameText() + "'");
    }
    
    ValueEval evaluateNameFormula(final Ptg[] ptgs, final OperationEvaluationContext ec) {
        if (ptgs.length == 1 && !(ptgs[0] instanceof FuncVarPtg)) {
            return this.getEvalForPtg(ptgs[0], ec);
        }
        final OperationEvaluationContext anyValueContext = new OperationEvaluationContext(this, ec.getWorkbook(), ec.getSheetIndex(), ec.getRowIndex(), ec.getColumnIndex(), new EvaluationTracker(this._cache), false);
        return this.evaluateFormula(anyValueContext, ptgs);
    }
    
    ValueEval evaluateReference(final EvaluationSheet sheet, final int sheetIndex, final int rowIndex, final int columnIndex, final EvaluationTracker tracker) {
        final EvaluationCell cell = sheet.getCell(rowIndex, columnIndex);
        return this.evaluateAny(cell, sheetIndex, rowIndex, columnIndex, tracker);
    }
    
    public FreeRefFunction findUserDefinedFunction(final String functionName) {
        return this._udfFinder.findFunction(functionName);
    }
    
    public ValueEval evaluate(final String formula, final CellReference ref) {
        final String sheetName = (ref == null) ? null : ref.getSheetName();
        int sheetIndex;
        if (sheetName == null) {
            sheetIndex = -1;
        }
        else {
            sheetIndex = this.getWorkbook().getSheetIndex(sheetName);
        }
        final int rowIndex = (ref == null) ? -1 : ref.getRow();
        final short colIndex = (short)((ref == null) ? -1 : ref.getCol());
        final OperationEvaluationContext ec = new OperationEvaluationContext(this, this.getWorkbook(), sheetIndex, rowIndex, colIndex, new EvaluationTracker(this._cache));
        final Ptg[] ptgs = FormulaParser.parse(formula, (FormulaParsingWorkbook)this.getWorkbook(), FormulaType.CELL, sheetIndex, rowIndex);
        return this.evaluateNameFormula(ptgs, ec);
    }
    
    public ValueEval evaluate(final String formula, final CellReference target, final CellRangeAddressBase region) {
        return this.evaluate(formula, target, region, FormulaType.CELL);
    }
    
    public ValueEval evaluateList(final String formula, final CellReference target, final CellRangeAddressBase region) {
        return this.evaluate(formula, target, region, FormulaType.DATAVALIDATION_LIST);
    }
    
    private ValueEval evaluate(final String formula, final CellReference target, final CellRangeAddressBase region, final FormulaType formulaType) {
        final String sheetName = (target == null) ? null : target.getSheetName();
        if (sheetName == null) {
            throw new IllegalArgumentException("Sheet name is required");
        }
        final int sheetIndex = this.getWorkbook().getSheetIndex(sheetName);
        final Ptg[] ptgs = FormulaParser.parse(formula, (FormulaParsingWorkbook)this.getWorkbook(), formulaType, sheetIndex, target.getRow());
        this.adjustRegionRelativeReference(ptgs, target, region);
        final OperationEvaluationContext ec = new OperationEvaluationContext(this, this.getWorkbook(), sheetIndex, target.getRow(), target.getCol(), new EvaluationTracker(this._cache), formulaType.isSingleValue());
        return this.evaluateNameFormula(ptgs, ec);
    }
    
    protected boolean adjustRegionRelativeReference(final Ptg[] ptgs, final CellReference target, final CellRangeAddressBase region) {
        final int deltaRow = target.getRow() - region.getFirstRow();
        final int deltaColumn = target.getCol() - region.getFirstColumn();
        boolean shifted = false;
        for (final Ptg ptg : ptgs) {
            if (ptg instanceof RefPtgBase) {
                final RefPtgBase ref = (RefPtgBase)ptg;
                final SpreadsheetVersion version = this._workbook.getSpreadsheetVersion();
                if (ref.isRowRelative() && deltaRow > 0) {
                    final int rowIndex = ref.getRow() + deltaRow;
                    if (rowIndex > version.getMaxRows()) {
                        throw new IndexOutOfBoundsException(version.name() + " files can only have " + version.getMaxRows() + " rows, but row " + rowIndex + " was requested.");
                    }
                    ref.setRow(rowIndex);
                    shifted = true;
                }
                if (ref.isColRelative() && deltaColumn > 0) {
                    final int colIndex = ref.getColumn() + deltaColumn;
                    if (colIndex > version.getMaxColumns()) {
                        throw new IndexOutOfBoundsException(version.name() + " files can only have " + version.getMaxColumns() + " columns, but column " + colIndex + " was requested.");
                    }
                    ref.setColumn(colIndex);
                    shifted = true;
                }
            }
        }
        return shifted;
    }
    
    public void setIgnoreMissingWorkbooks(final boolean ignore) {
        this._ignoreMissingWorkbooks = ignore;
    }
    
    public boolean isIgnoreMissingWorkbooks() {
        return this._ignoreMissingWorkbooks;
    }
    
    public static Collection<String> getSupportedFunctionNames() {
        final Collection<String> lst = new TreeSet<String>();
        lst.addAll(FunctionEval.getSupportedFunctionNames());
        lst.addAll(AnalysisToolPak.getSupportedFunctionNames());
        return Collections.unmodifiableCollection((Collection<? extends String>)lst);
    }
    
    public static Collection<String> getNotSupportedFunctionNames() {
        final Collection<String> lst = new TreeSet<String>();
        lst.addAll(FunctionEval.getNotSupportedFunctionNames());
        lst.addAll(AnalysisToolPak.getNotSupportedFunctionNames());
        return Collections.unmodifiableCollection((Collection<? extends String>)lst);
    }
    
    public static void registerFunction(final String name, final FreeRefFunction func) {
        AnalysisToolPak.registerFunction(name, func);
    }
    
    public static void registerFunction(final String name, final Function func) {
        FunctionEval.registerFunction(name, func);
    }
    
    public void setDebugEvaluationOutputForNextEval(final boolean value) {
        this.dbgEvaluationOutputForNextEval = value;
    }
    
    public boolean isDebugEvaluationOutputForNextEval() {
        return this.dbgEvaluationOutputForNextEval;
    }
    
    static {
        LOG = POILogFactory.getLogger(WorkbookEvaluator.class);
    }
}
