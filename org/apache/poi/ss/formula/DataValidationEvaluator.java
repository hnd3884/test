package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.SheetUtil;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import java.util.Collections;
import org.apache.poi.ss.formula.eval.StringEval;
import java.util.ArrayList;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.util.CellRangeAddressBase;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import java.util.Iterator;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.usermodel.Sheet;
import java.util.HashMap;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.DataValidation;
import java.util.List;
import java.util.Map;

public class DataValidationEvaluator
{
    private final Map<String, List<? extends DataValidation>> validations;
    private final Workbook workbook;
    private final WorkbookEvaluator workbookEvaluator;
    
    public DataValidationEvaluator(final Workbook wb, final WorkbookEvaluatorProvider provider) {
        this.validations = new HashMap<String, List<? extends DataValidation>>();
        this.workbook = wb;
        this.workbookEvaluator = provider._getWorkbookEvaluator();
    }
    
    protected WorkbookEvaluator getWorkbookEvaluator() {
        return this.workbookEvaluator;
    }
    
    public void clearAllCachedValues() {
        this.validations.clear();
    }
    
    private List<? extends DataValidation> getValidations(final Sheet sheet) {
        List<? extends DataValidation> dvs = this.validations.get(sheet.getSheetName());
        if (dvs == null && !this.validations.containsKey(sheet.getSheetName())) {
            dvs = sheet.getDataValidations();
            this.validations.put(sheet.getSheetName(), dvs);
        }
        return dvs;
    }
    
    public DataValidation getValidationForCell(final CellReference cell) {
        final DataValidationContext vc = this.getValidationContextForCell(cell);
        return (vc == null) ? null : vc.getValidation();
    }
    
    public DataValidationContext getValidationContextForCell(final CellReference cell) {
        final Sheet sheet = this.workbook.getSheet(cell.getSheetName());
        if (sheet == null) {
            return null;
        }
        final List<? extends DataValidation> dataValidations = this.getValidations(sheet);
        if (dataValidations == null) {
            return null;
        }
        for (final DataValidation dv : dataValidations) {
            final CellRangeAddressList regions = dv.getRegions();
            if (regions == null) {
                return null;
            }
            for (final CellRangeAddressBase range : regions.getCellRangeAddresses()) {
                if (range.isInRange(cell)) {
                    return new DataValidationContext(dv, this, range, cell);
                }
            }
        }
        return null;
    }
    
    public List<ValueEval> getValidationValuesForCell(final CellReference cell) {
        final DataValidationContext context = this.getValidationContextForCell(cell);
        if (context == null) {
            return null;
        }
        return getValidationValuesForConstraint(context);
    }
    
    protected static List<ValueEval> getValidationValuesForConstraint(final DataValidationContext context) {
        final DataValidationConstraint val = context.getValidation().getValidationConstraint();
        if (val.getValidationType() != 3) {
            return null;
        }
        final String formula = val.getFormula1();
        final List<ValueEval> values = new ArrayList<ValueEval>();
        if (val.getExplicitListValues() != null && val.getExplicitListValues().length > 0) {
            for (final String s : val.getExplicitListValues()) {
                if (s != null) {
                    values.add(new StringEval(s));
                }
            }
        }
        else if (formula != null) {
            final ValueEval eval = context.getEvaluator().getWorkbookEvaluator().evaluateList(formula, context.getTarget(), context.getRegion());
            if (eval instanceof TwoDEval) {
                final TwoDEval twod = (TwoDEval)eval;
                for (int i = 0; i < twod.getHeight(); ++i) {
                    final ValueEval cellValue = twod.getValue(i, 0);
                    values.add(cellValue);
                }
            }
        }
        return Collections.unmodifiableList((List<? extends ValueEval>)values);
    }
    
    public boolean isValidCell(final CellReference cellRef) {
        final DataValidationContext context = this.getValidationContextForCell(cellRef);
        if (context == null) {
            return true;
        }
        final Cell cell = SheetUtil.getCell(this.workbook.getSheet(cellRef.getSheetName()), cellRef.getRow(), cellRef.getCol());
        if (cell == null || isType(cell, CellType.BLANK) || (isType(cell, CellType.STRING) && (cell.getStringCellValue() == null || cell.getStringCellValue().isEmpty()))) {
            return context.getValidation().getEmptyCellAllowed();
        }
        return ValidationEnum.isValid(cell, context);
    }
    
    public static boolean isType(final Cell cell, final CellType type) {
        final CellType cellType = cell.getCellType();
        return cellType == type || (cellType == CellType.FORMULA && cell.getCachedFormulaResultType() == type);
    }
    
    public enum ValidationEnum
    {
        ANY {
            @Override
            public boolean isValidValue(final Cell cell, final DataValidationContext context) {
                return true;
            }
        }, 
        INTEGER {
            @Override
            public boolean isValidValue(final Cell cell, final DataValidationContext context) {
                if (super.isValidValue(cell, context)) {
                    final double value = cell.getNumericCellValue();
                    return Double.compare(value, (int)value) == 0;
                }
                return false;
            }
        }, 
        DECIMAL, 
        LIST {
            @Override
            public boolean isValidValue(final Cell cell, final DataValidationContext context) {
                final List<ValueEval> valueList = DataValidationEvaluator.getValidationValuesForConstraint(context);
                if (valueList == null) {
                    return true;
                }
                for (final ValueEval listVal : valueList) {
                    final ValueEval comp = (listVal instanceof RefEval) ? ((RefEval)listVal).getInnerValueEval(context.getSheetIndex()) : listVal;
                    if (comp instanceof BlankEval) {
                        return true;
                    }
                    if (comp instanceof ErrorEval) {
                        continue;
                    }
                    if (comp instanceof BoolEval) {
                        if (DataValidationEvaluator.isType(cell, CellType.BOOLEAN) && ((BoolEval)comp).getBooleanValue() == cell.getBooleanCellValue()) {
                            return true;
                        }
                        continue;
                    }
                    else if (comp instanceof NumberEval) {
                        if (DataValidationEvaluator.isType(cell, CellType.NUMERIC) && ((NumberEval)comp).getNumberValue() == cell.getNumericCellValue()) {
                            return true;
                        }
                        continue;
                    }
                    else {
                        if (!(comp instanceof StringEval)) {
                            continue;
                        }
                        if (DataValidationEvaluator.isType(cell, CellType.STRING) && ((StringEval)comp).getStringValue().equalsIgnoreCase(cell.getStringCellValue())) {
                            return true;
                        }
                        continue;
                    }
                }
                return false;
            }
        }, 
        DATE, 
        TIME, 
        TEXT_LENGTH {
            @Override
            public boolean isValidValue(final Cell cell, final DataValidationContext context) {
                if (!DataValidationEvaluator.isType(cell, CellType.STRING)) {
                    return false;
                }
                final String v = cell.getStringCellValue();
                return this.isValidNumericValue((double)v.length(), context);
            }
        }, 
        FORMULA {
            @Override
            public boolean isValidValue(final Cell cell, final DataValidationContext context) {
                ValueEval comp = context.getEvaluator().getWorkbookEvaluator().evaluate(context.getFormula1(), context.getTarget(), context.getRegion());
                if (comp instanceof RefEval) {
                    comp = ((RefEval)comp).getInnerValueEval(((RefEval)comp).getFirstSheetIndex());
                }
                if (comp instanceof BlankEval) {
                    return true;
                }
                if (comp instanceof ErrorEval) {
                    return false;
                }
                if (comp instanceof BoolEval) {
                    return ((BoolEval)comp).getBooleanValue();
                }
                return comp instanceof NumberEval && ((NumberEval)comp).getNumberValue() != 0.0;
            }
        };
        
        public boolean isValidValue(final Cell cell, final DataValidationContext context) {
            return this.isValidNumericCell(cell, context);
        }
        
        protected boolean isValidNumericCell(final Cell cell, final DataValidationContext context) {
            if (!DataValidationEvaluator.isType(cell, CellType.NUMERIC)) {
                return false;
            }
            final Double value = cell.getNumericCellValue();
            return this.isValidNumericValue(value, context);
        }
        
        protected boolean isValidNumericValue(final Double value, final DataValidationContext context) {
            try {
                final Double t1 = this.evalOrConstant(context.getFormula1(), context);
                if (t1 == null) {
                    return true;
                }
                Double t2 = null;
                if (context.getOperator() == 0 || context.getOperator() == 1) {
                    t2 = this.evalOrConstant(context.getFormula2(), context);
                    if (t2 == null) {
                        return true;
                    }
                }
                return OperatorEnum.values()[context.getOperator()].isValid(value, t1, t2);
            }
            catch (final NumberFormatException e) {
                return false;
            }
        }
        
        private Double evalOrConstant(final String formula, final DataValidationContext context) throws NumberFormatException {
            if (formula == null || formula.trim().isEmpty()) {
                return null;
            }
            try {
                return Double.valueOf(formula);
            }
            catch (final NumberFormatException ex) {
                ValueEval eval = context.getEvaluator().getWorkbookEvaluator().evaluate(formula, context.getTarget(), context.getRegion());
                if (eval instanceof RefEval) {
                    eval = ((RefEval)eval).getInnerValueEval(((RefEval)eval).getFirstSheetIndex());
                }
                if (eval instanceof BlankEval) {
                    return null;
                }
                if (eval instanceof NumberEval) {
                    return ((NumberEval)eval).getNumberValue();
                }
                if (!(eval instanceof StringEval)) {
                    throw new NumberFormatException("Formula '" + formula + "' evaluates to something other than a number");
                }
                final String value = ((StringEval)eval).getStringValue();
                if (value == null || value.trim().isEmpty()) {
                    return null;
                }
                return Double.valueOf(value);
            }
        }
        
        public static boolean isValid(final Cell cell, final DataValidationContext context) {
            return values()[context.getValidation().getValidationConstraint().getValidationType()].isValidValue(cell, context);
        }
    }
    
    public enum OperatorEnum
    {
        BETWEEN {
            @Override
            public boolean isValid(final Double cellValue, final Double v1, final Double v2) {
                return cellValue.compareTo(v1) >= 0 && cellValue.compareTo(v2) <= 0;
            }
        }, 
        NOT_BETWEEN {
            @Override
            public boolean isValid(final Double cellValue, final Double v1, final Double v2) {
                return cellValue.compareTo(v1) < 0 || cellValue.compareTo(v2) > 0;
            }
        }, 
        EQUAL {
            @Override
            public boolean isValid(final Double cellValue, final Double v1, final Double v2) {
                return cellValue.compareTo(v1) == 0;
            }
        }, 
        NOT_EQUAL {
            @Override
            public boolean isValid(final Double cellValue, final Double v1, final Double v2) {
                return cellValue.compareTo(v1) != 0;
            }
        }, 
        GREATER_THAN {
            @Override
            public boolean isValid(final Double cellValue, final Double v1, final Double v2) {
                return cellValue.compareTo(v1) > 0;
            }
        }, 
        LESS_THAN {
            @Override
            public boolean isValid(final Double cellValue, final Double v1, final Double v2) {
                return cellValue.compareTo(v1) < 0;
            }
        }, 
        GREATER_OR_EQUAL {
            @Override
            public boolean isValid(final Double cellValue, final Double v1, final Double v2) {
                return cellValue.compareTo(v1) >= 0;
            }
        }, 
        LESS_OR_EQUAL {
            @Override
            public boolean isValid(final Double cellValue, final Double v1, final Double v2) {
                return cellValue.compareTo(v1) <= 0;
            }
        };
        
        public static final OperatorEnum IGNORED;
        
        public abstract boolean isValid(final Double p0, final Double p1, final Double p2);
        
        static {
            IGNORED = OperatorEnum.BETWEEN;
        }
    }
    
    public static class DataValidationContext
    {
        private final DataValidation dv;
        private final DataValidationEvaluator dve;
        private final CellRangeAddressBase region;
        private final CellReference target;
        
        public DataValidationContext(final DataValidation dv, final DataValidationEvaluator dve, final CellRangeAddressBase region, final CellReference target) {
            this.dv = dv;
            this.dve = dve;
            this.region = region;
            this.target = target;
        }
        
        public DataValidation getValidation() {
            return this.dv;
        }
        
        public DataValidationEvaluator getEvaluator() {
            return this.dve;
        }
        
        public CellRangeAddressBase getRegion() {
            return this.region;
        }
        
        public CellReference getTarget() {
            return this.target;
        }
        
        public int getOffsetColumns() {
            return this.target.getCol() - this.region.getFirstColumn();
        }
        
        public int getOffsetRows() {
            return this.target.getRow() - this.region.getFirstRow();
        }
        
        public int getSheetIndex() {
            return this.dve.getWorkbookEvaluator().getSheetIndex(this.target.getSheetName());
        }
        
        public String getFormula1() {
            return this.dv.getValidationConstraint().getFormula1();
        }
        
        public String getFormula2() {
            return this.dv.getValidationConstraint().getFormula2();
        }
        
        public int getOperator() {
            return this.dv.getValidationConstraint().getOperator();
        }
    }
}
