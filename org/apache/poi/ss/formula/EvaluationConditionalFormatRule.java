package org.apache.poi.ss.formula;

import org.apache.poi.ss.usermodel.ConditionFilterType;
import java.util.ArrayList;
import org.apache.poi.ss.formula.functions.AggregateFunction;
import java.util.LinkedHashSet;
import org.apache.poi.ss.usermodel.ConditionFilterData;
import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import java.util.List;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.util.CellRangeAddressBase;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;
import java.util.Objects;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.apache.poi.util.LocaleUtil;
import java.util.HashMap;
import java.text.DecimalFormat;
import org.apache.poi.ss.usermodel.ExcelNumberFormat;
import org.apache.poi.ss.usermodel.ConditionType;
import java.util.Set;
import java.util.Map;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.Sheet;

public class EvaluationConditionalFormatRule implements Comparable<EvaluationConditionalFormatRule>
{
    private final WorkbookEvaluator workbookEvaluator;
    private final Sheet sheet;
    private final ConditionalFormatting formatting;
    private final ConditionalFormattingRule rule;
    private final CellRangeAddress[] regions;
    private CellRangeAddress topLeftRegion;
    private final Map<CellRangeAddress, Set<ValueAndFormat>> meaningfulRegionValues;
    private final int priority;
    private final int formattingIndex;
    private final int ruleIndex;
    private final String formula1;
    private final String formula2;
    private final String text;
    private final String lowerText;
    private final OperatorEnum operator;
    private final ConditionType type;
    private final ExcelNumberFormat numberFormat;
    private final DecimalFormat decimalTextFormat;
    
    public EvaluationConditionalFormatRule(final WorkbookEvaluator workbookEvaluator, final Sheet sheet, final ConditionalFormatting formatting, final int formattingIndex, final ConditionalFormattingRule rule, final int ruleIndex, final CellRangeAddress[] regions) {
        this.meaningfulRegionValues = new HashMap<CellRangeAddress, Set<ValueAndFormat>>();
        this.workbookEvaluator = workbookEvaluator;
        this.sheet = sheet;
        this.formatting = formatting;
        this.rule = rule;
        this.formattingIndex = formattingIndex;
        this.ruleIndex = ruleIndex;
        this.priority = rule.getPriority();
        this.regions = regions;
        for (final CellRangeAddress region : regions) {
            if (this.topLeftRegion == null) {
                this.topLeftRegion = region;
            }
            else if (region.getFirstColumn() < this.topLeftRegion.getFirstColumn() || region.getFirstRow() < this.topLeftRegion.getFirstRow()) {
                this.topLeftRegion = region;
            }
        }
        this.formula1 = rule.getFormula1();
        this.formula2 = rule.getFormula2();
        this.text = rule.getText();
        this.lowerText = ((this.text == null) ? null : this.text.toLowerCase(LocaleUtil.getUserLocale()));
        this.numberFormat = rule.getNumberFormat();
        this.operator = OperatorEnum.values()[rule.getComparisonOperation()];
        this.type = rule.getConditionType();
        (this.decimalTextFormat = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH))).setMaximumFractionDigits(340);
    }
    
    public Sheet getSheet() {
        return this.sheet;
    }
    
    public ConditionalFormatting getFormatting() {
        return this.formatting;
    }
    
    public int getFormattingIndex() {
        return this.formattingIndex;
    }
    
    public ExcelNumberFormat getNumberFormat() {
        return this.numberFormat;
    }
    
    public ConditionalFormattingRule getRule() {
        return this.rule;
    }
    
    public int getRuleIndex() {
        return this.ruleIndex;
    }
    
    public CellRangeAddress[] getRegions() {
        return this.regions;
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public String getFormula1() {
        return this.formula1;
    }
    
    public String getFormula2() {
        return this.formula2;
    }
    
    public String getText() {
        return this.text;
    }
    
    public OperatorEnum getOperator() {
        return this.operator;
    }
    
    public ConditionType getType() {
        return this.type;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        }
        final EvaluationConditionalFormatRule r = (EvaluationConditionalFormatRule)obj;
        return this.getSheet().getSheetName().equalsIgnoreCase(r.getSheet().getSheetName()) && this.getFormattingIndex() == r.getFormattingIndex() && this.getRuleIndex() == r.getRuleIndex();
    }
    
    @Override
    public int compareTo(final EvaluationConditionalFormatRule o) {
        int cmp = this.getSheet().getSheetName().compareToIgnoreCase(o.getSheet().getSheetName());
        if (cmp != 0) {
            return cmp;
        }
        final int x = this.getPriority();
        final int y = o.getPriority();
        cmp = Integer.compare(x, y);
        if (cmp != 0) {
            return cmp;
        }
        cmp = Integer.compare(this.getFormattingIndex(), o.getFormattingIndex());
        if (cmp != 0) {
            return cmp;
        }
        return Integer.compare(this.getRuleIndex(), o.getRuleIndex());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.sheet.getSheetName(), this.formattingIndex, this.ruleIndex);
    }
    
    boolean matches(final CellReference ref) {
        CellRangeAddress region = null;
        for (final CellRangeAddress r : this.regions) {
            if (r.isInRange(ref)) {
                region = r;
                break;
            }
        }
        if (region == null) {
            return false;
        }
        final ConditionType ruleType = this.getRule().getConditionType();
        if (ruleType.equals(ConditionType.COLOR_SCALE) || ruleType.equals(ConditionType.DATA_BAR) || ruleType.equals(ConditionType.ICON_SET)) {
            return true;
        }
        Cell cell = null;
        final Row row = this.sheet.getRow(ref.getRow());
        if (row != null) {
            cell = row.getCell(ref.getCol());
        }
        if (ruleType.equals(ConditionType.CELL_VALUE_IS)) {
            return cell != null && this.checkValue(cell, this.topLeftRegion);
        }
        if (ruleType.equals(ConditionType.FORMULA)) {
            return this.checkFormula(ref, this.topLeftRegion);
        }
        return ruleType.equals(ConditionType.FILTER) && this.checkFilter(cell, ref, this.topLeftRegion);
    }
    
    private boolean checkValue(final Cell cell, final CellRangeAddress region) {
        if (cell == null || DataValidationEvaluator.isType(cell, CellType.BLANK) || DataValidationEvaluator.isType(cell, CellType.ERROR) || (DataValidationEvaluator.isType(cell, CellType.STRING) && (cell.getStringCellValue() == null || cell.getStringCellValue().isEmpty()))) {
            return false;
        }
        final ValueEval eval = this.unwrapEval(this.workbookEvaluator.evaluate(this.rule.getFormula1(), ConditionalFormattingEvaluator.getRef(cell), region));
        final String f2 = this.rule.getFormula2();
        ValueEval eval2 = BlankEval.instance;
        if (f2 != null && f2.length() > 0) {
            eval2 = this.unwrapEval(this.workbookEvaluator.evaluate(f2, ConditionalFormattingEvaluator.getRef(cell), region));
        }
        if (DataValidationEvaluator.isType(cell, CellType.BOOLEAN) && (eval == BlankEval.instance || eval instanceof BoolEval) && (eval2 == BlankEval.instance || eval2 instanceof BoolEval)) {
            return this.operator.isValid(cell.getBooleanCellValue(), (eval == BlankEval.instance) ? null : Boolean.valueOf(((BoolEval)eval).getBooleanValue()), (eval2 == BlankEval.instance) ? null : Boolean.valueOf(((BoolEval)eval2).getBooleanValue()));
        }
        if (DataValidationEvaluator.isType(cell, CellType.NUMERIC) && (eval == BlankEval.instance || eval instanceof NumberEval) && (eval2 == BlankEval.instance || eval2 instanceof NumberEval)) {
            return this.operator.isValid(cell.getNumericCellValue(), (eval == BlankEval.instance) ? null : Double.valueOf(((NumberEval)eval).getNumberValue()), (eval2 == BlankEval.instance) ? null : Double.valueOf(((NumberEval)eval2).getNumberValue()));
        }
        if (DataValidationEvaluator.isType(cell, CellType.STRING) && (eval == BlankEval.instance || eval instanceof StringEval) && (eval2 == BlankEval.instance || eval2 instanceof StringEval)) {
            return this.operator.isValid(cell.getStringCellValue(), (eval == BlankEval.instance) ? null : ((StringEval)eval).getStringValue(), (eval2 == BlankEval.instance) ? null : ((StringEval)eval2).getStringValue());
        }
        return this.operator.isValidForIncompatibleTypes();
    }
    
    private ValueEval unwrapEval(final ValueEval eval) {
        ValueEval comp;
        RefEval ref;
        for (comp = eval; comp instanceof RefEval; comp = ref.getInnerValueEval(ref.getFirstSheetIndex())) {
            ref = (RefEval)comp;
        }
        return comp;
    }
    
    private boolean checkFormula(final CellReference ref, final CellRangeAddress region) {
        final ValueEval comp = this.unwrapEval(this.workbookEvaluator.evaluate(this.rule.getFormula1(), ref, region));
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
    
    private boolean checkFilter(final Cell cell, final CellReference ref, final CellRangeAddress region) {
        final ConditionFilterType filterType = this.rule.getConditionFilterType();
        if (filterType == null) {
            return false;
        }
        final ValueAndFormat cv = this.getCellValue(cell);
        switch (filterType) {
            case FILTER: {
                return false;
            }
            case TOP_10: {
                return cv.isNumber() && this.getMeaningfulValues(region, false, new ValueFunction() {
                    @Override
                    public Set<ValueAndFormat> evaluate(final List<ValueAndFormat> allValues) {
                        final ConditionFilterData conf = EvaluationConditionalFormatRule.this.rule.getFilterConfiguration();
                        if (!conf.getBottom()) {
                            allValues.sort(Collections.reverseOrder());
                        }
                        else {
                            Collections.sort(allValues);
                        }
                        int limit = Math.toIntExact(conf.getRank());
                        if (conf.getPercent()) {
                            limit = allValues.size() * limit / 100;
                        }
                        if (allValues.size() <= limit) {
                            return new HashSet<ValueAndFormat>(allValues);
                        }
                        return new HashSet<ValueAndFormat>(allValues.subList(0, limit));
                    }
                }).contains(cv);
            }
            case UNIQUE_VALUES: {
                return this.getMeaningfulValues(region, true, new ValueFunction() {
                    @Override
                    public Set<ValueAndFormat> evaluate(final List<ValueAndFormat> allValues) {
                        Collections.sort(allValues);
                        final Set<ValueAndFormat> unique = new HashSet<ValueAndFormat>();
                        for (int i = 0; i < allValues.size(); ++i) {
                            final ValueAndFormat v = allValues.get(i);
                            if ((i < allValues.size() - 1 && v.equals(allValues.get(i + 1))) || (i > 0 && i == allValues.size() - 1 && v.equals(allValues.get(i - 1)))) {
                                ++i;
                            }
                            else {
                                unique.add(v);
                            }
                        }
                        return unique;
                    }
                }).contains(cv);
            }
            case DUPLICATE_VALUES: {
                return this.getMeaningfulValues(region, true, new ValueFunction() {
                    @Override
                    public Set<ValueAndFormat> evaluate(final List<ValueAndFormat> allValues) {
                        Collections.sort(allValues);
                        final Set<ValueAndFormat> dup = new HashSet<ValueAndFormat>();
                        for (int i = 0; i < allValues.size(); ++i) {
                            final ValueAndFormat v = allValues.get(i);
                            if ((i < allValues.size() - 1 && v.equals(allValues.get(i + 1))) || (i > 0 && i == allValues.size() - 1 && v.equals(allValues.get(i - 1)))) {
                                dup.add(v);
                                ++i;
                            }
                        }
                        return dup;
                    }
                }).contains(cv);
            }
            case ABOVE_AVERAGE: {
                final ConditionFilterData conf = this.rule.getFilterConfiguration();
                final List<ValueAndFormat> values = new ArrayList<ValueAndFormat>(this.getMeaningfulValues(region, false, new ValueFunction() {
                    @Override
                    public Set<ValueAndFormat> evaluate(final List<ValueAndFormat> allValues) {
                        double total = 0.0;
                        final ValueEval[] pop = new ValueEval[allValues.size()];
                        for (int i = 0; i < allValues.size(); ++i) {
                            final ValueAndFormat v = allValues.get(i);
                            total += v.value;
                            pop[i] = new NumberEval(v.value);
                        }
                        final Set<ValueAndFormat> avgSet = new LinkedHashSet<ValueAndFormat>(1);
                        avgSet.add(new ValueAndFormat((allValues.size() == 0) ? 0.0 : (total / allValues.size()), null, EvaluationConditionalFormatRule.this.decimalTextFormat));
                        final double stdDev = (allValues.size() <= 1) ? 0.0 : ((NumberEval)AggregateFunction.STDEV.evaluate(pop, 0, 0)).getNumberValue();
                        avgSet.add(new ValueAndFormat(stdDev, null, EvaluationConditionalFormatRule.this.decimalTextFormat));
                        return avgSet;
                    }
                }));
                final Double val = cv.isNumber() ? cv.getValue() : null;
                if (val == null) {
                    return false;
                }
                final double avg = values.get(0).value;
                final double stdDev = values.get(1).value;
                final Double comp = (conf.getStdDev() > 0) ? (avg + (conf.getAboveAverage() ? 1 : -1) * stdDev * conf.getStdDev()) : avg;
                OperatorEnum op;
                if (conf.getAboveAverage()) {
                    if (conf.getEqualAverage()) {
                        op = OperatorEnum.GREATER_OR_EQUAL;
                    }
                    else {
                        op = OperatorEnum.GREATER_THAN;
                    }
                }
                else if (conf.getEqualAverage()) {
                    op = OperatorEnum.LESS_OR_EQUAL;
                }
                else {
                    op = OperatorEnum.LESS_THAN;
                }
                return op.isValid(val, comp, null);
            }
            case CONTAINS_TEXT: {
                return this.text != null && cv.toString().toLowerCase(LocaleUtil.getUserLocale()).contains(this.lowerText);
            }
            case NOT_CONTAINS_TEXT: {
                return this.text == null || !cv.toString().toLowerCase(LocaleUtil.getUserLocale()).contains(this.lowerText);
            }
            case BEGINS_WITH: {
                return cv.toString().toLowerCase(LocaleUtil.getUserLocale()).startsWith(this.lowerText);
            }
            case ENDS_WITH: {
                return cv.toString().toLowerCase(LocaleUtil.getUserLocale()).endsWith(this.lowerText);
            }
            case CONTAINS_BLANKS: {
                try {
                    final String v = cv.getString();
                    return v == null || v.trim().length() == 0;
                }
                catch (final Exception e) {
                    return false;
                }
            }
            case NOT_CONTAINS_BLANKS: {
                try {
                    final String v = cv.getString();
                    return v != null && v.trim().length() > 0;
                }
                catch (final Exception e) {
                    return true;
                }
            }
            case CONTAINS_ERRORS: {
                return cell != null && DataValidationEvaluator.isType(cell, CellType.ERROR);
            }
            case NOT_CONTAINS_ERRORS: {
                return cell == null || !DataValidationEvaluator.isType(cell, CellType.ERROR);
            }
            case TIME_PERIOD: {
                return this.checkFormula(ref, region);
            }
            default: {
                return false;
            }
        }
    }
    
    private Set<ValueAndFormat> getMeaningfulValues(final CellRangeAddress region, final boolean withText, final ValueFunction func) {
        Set<ValueAndFormat> values = this.meaningfulRegionValues.get(region);
        if (values != null) {
            return values;
        }
        final List<ValueAndFormat> allValues = new ArrayList<ValueAndFormat>((region.getLastColumn() - region.getFirstColumn() + 1) * (region.getLastRow() - region.getFirstRow() + 1));
        for (int r = region.getFirstRow(); r <= region.getLastRow(); ++r) {
            final Row row = this.sheet.getRow(r);
            if (row != null) {
                for (int c = region.getFirstColumn(); c <= region.getLastColumn(); ++c) {
                    final Cell cell = row.getCell(c);
                    final ValueAndFormat cv = this.getCellValue(cell);
                    if (withText || cv.isNumber()) {
                        allValues.add(cv);
                    }
                }
            }
        }
        values = func.evaluate(allValues);
        this.meaningfulRegionValues.put(region, values);
        return values;
    }
    
    private ValueAndFormat getCellValue(final Cell cell) {
        if (cell != null) {
            final String format = cell.getCellStyle().getDataFormatString();
            CellType type = cell.getCellType();
            if (type == CellType.FORMULA) {
                type = cell.getCachedFormulaResultType();
            }
            switch (type) {
                case NUMERIC: {
                    return new ValueAndFormat(cell.getNumericCellValue(), format, this.decimalTextFormat);
                }
                case STRING:
                case BOOLEAN: {
                    return new ValueAndFormat(cell.getStringCellValue(), format);
                }
            }
        }
        return new ValueAndFormat("", "");
    }
    
    public enum OperatorEnum
    {
        NO_COMPARISON {
            @Override
            public <C extends Comparable<C>> boolean isValid(final C cellValue, final C v1, final C v2) {
                return false;
            }
        }, 
        BETWEEN {
            @Override
            public <C extends Comparable<C>> boolean isValid(final C cellValue, final C v1, final C v2) {
                if (v1 != null) {
                    return cellValue.compareTo(v1) >= 0 && cellValue.compareTo(v2) <= 0;
                }
                if (cellValue instanceof Number) {
                    final double n1 = 0.0;
                    final double n2 = (v2 == null) ? 0.0 : ((Number)v2).doubleValue();
                    return Double.compare(((Number)cellValue).doubleValue(), n1) >= 0 && Double.compare(((Number)cellValue).doubleValue(), n2) <= 0;
                }
                if (cellValue instanceof String) {
                    final String n3 = "";
                    final String n4 = (String)((v2 == null) ? "" : v2);
                    return ((String)cellValue).compareToIgnoreCase(n3) >= 0 && ((String)cellValue).compareToIgnoreCase(n4) <= 0;
                }
                return cellValue instanceof Boolean && false;
            }
        }, 
        NOT_BETWEEN {
            @Override
            public <C extends Comparable<C>> boolean isValid(final C cellValue, final C v1, final C v2) {
                if (v1 != null) {
                    return cellValue.compareTo(v1) < 0 || cellValue.compareTo(v2) > 0;
                }
                if (cellValue instanceof Number) {
                    final double n1 = 0.0;
                    final double n2 = (v2 == null) ? 0.0 : ((Number)v2).doubleValue();
                    return Double.compare(((Number)cellValue).doubleValue(), n1) < 0 || Double.compare(((Number)cellValue).doubleValue(), n2) > 0;
                }
                if (cellValue instanceof String) {
                    final String n3 = "";
                    final String n4 = (String)((v2 == null) ? "" : v2);
                    return ((String)cellValue).compareToIgnoreCase(n3) < 0 || ((String)cellValue).compareToIgnoreCase(n4) > 0;
                }
                return cellValue instanceof Boolean;
            }
            
            @Override
            public boolean isValidForIncompatibleTypes() {
                return true;
            }
        }, 
        EQUAL {
            @Override
            public <C extends Comparable<C>> boolean isValid(final C cellValue, final C v1, final C v2) {
                if (v1 == null) {
                    if (cellValue instanceof Number) {
                        return Double.compare(((Number)cellValue).doubleValue(), 0.0) == 0;
                    }
                    return !(cellValue instanceof String) && cellValue instanceof Boolean && false;
                }
                else {
                    if (cellValue.getClass() == String.class) {
                        return cellValue.toString().compareToIgnoreCase(v1.toString()) == 0;
                    }
                    return cellValue.compareTo(v1) == 0;
                }
            }
        }, 
        NOT_EQUAL {
            @Override
            public <C extends Comparable<C>> boolean isValid(final C cellValue, final C v1, final C v2) {
                if (v1 == null) {
                    return true;
                }
                if (cellValue.getClass() == String.class) {
                    return cellValue.toString().compareToIgnoreCase(v1.toString()) == 0;
                }
                return cellValue.compareTo(v1) != 0;
            }
            
            @Override
            public boolean isValidForIncompatibleTypes() {
                return true;
            }
        }, 
        GREATER_THAN {
            @Override
            public <C extends Comparable<C>> boolean isValid(final C cellValue, final C v1, final C v2) {
                if (v1 != null) {
                    return cellValue.compareTo(v1) > 0;
                }
                if (cellValue instanceof Number) {
                    return Double.compare(((Number)cellValue).doubleValue(), 0.0) > 0;
                }
                return cellValue instanceof String || cellValue instanceof Boolean;
            }
        }, 
        LESS_THAN {
            @Override
            public <C extends Comparable<C>> boolean isValid(final C cellValue, final C v1, final C v2) {
                if (v1 != null) {
                    return cellValue.compareTo(v1) < 0;
                }
                if (cellValue instanceof Number) {
                    return Double.compare(((Number)cellValue).doubleValue(), 0.0) < 0;
                }
                return !(cellValue instanceof String) && cellValue instanceof Boolean && false;
            }
        }, 
        GREATER_OR_EQUAL {
            @Override
            public <C extends Comparable<C>> boolean isValid(final C cellValue, final C v1, final C v2) {
                if (v1 != null) {
                    return cellValue.compareTo(v1) >= 0;
                }
                if (cellValue instanceof Number) {
                    return Double.compare(((Number)cellValue).doubleValue(), 0.0) >= 0;
                }
                return cellValue instanceof String || cellValue instanceof Boolean;
            }
        }, 
        LESS_OR_EQUAL {
            @Override
            public <C extends Comparable<C>> boolean isValid(final C cellValue, final C v1, final C v2) {
                if (v1 != null) {
                    return cellValue.compareTo(v1) <= 0;
                }
                if (cellValue instanceof Number) {
                    return Double.compare(((Number)cellValue).doubleValue(), 0.0) <= 0;
                }
                return !(cellValue instanceof String) && cellValue instanceof Boolean && false;
            }
        };
        
        public abstract <C extends Comparable<C>> boolean isValid(final C p0, final C p1, final C p2);
        
        public boolean isValidForIncompatibleTypes() {
            return false;
        }
    }
    
    protected static class ValueAndFormat implements Comparable<ValueAndFormat>
    {
        private final Double value;
        private final String string;
        private final String format;
        private final DecimalFormat decimalTextFormat;
        
        public ValueAndFormat(final Double value, final String format, final DecimalFormat df) {
            this.value = value;
            this.format = format;
            this.string = null;
            this.decimalTextFormat = df;
        }
        
        public ValueAndFormat(final String value, final String format) {
            this.value = null;
            this.format = format;
            this.string = value;
            this.decimalTextFormat = null;
        }
        
        public boolean isNumber() {
            return this.value != null;
        }
        
        public Double getValue() {
            return this.value;
        }
        
        public String getString() {
            return this.string;
        }
        
        @Override
        public String toString() {
            if (this.isNumber()) {
                return this.decimalTextFormat.format((double)this.getValue());
            }
            return this.getString();
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof ValueAndFormat)) {
                return false;
            }
            final ValueAndFormat o = (ValueAndFormat)obj;
            return Objects.equals(this.value, o.value) && Objects.equals(this.format, o.format) && Objects.equals(this.string, o.string);
        }
        
        @Override
        public int compareTo(final ValueAndFormat o) {
            if (this.value == null && o.value != null) {
                return 1;
            }
            if (o.value == null && this.value != null) {
                return -1;
            }
            final int cmp = (this.value == null) ? 0 : this.value.compareTo(o.value);
            if (cmp != 0) {
                return cmp;
            }
            if (this.string == null && o.string != null) {
                return 1;
            }
            if (o.string == null && this.string != null) {
                return -1;
            }
            return (this.string == null) ? 0 : this.string.compareTo(o.string);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.string, this.value, this.format);
        }
    }
    
    protected interface ValueFunction
    {
        Set<ValueAndFormat> evaluate(final List<ValueAndFormat> p0);
    }
}
