package org.apache.poi.ss.formula;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.poi.ss.usermodel.Sheet;
import java.util.HashMap;
import org.apache.poi.ss.util.CellReference;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Workbook;

public class ConditionalFormattingEvaluator
{
    private final WorkbookEvaluator workbookEvaluator;
    private final Workbook workbook;
    private final Map<String, List<EvaluationConditionalFormatRule>> formats;
    private final Map<CellReference, List<EvaluationConditionalFormatRule>> values;
    
    public ConditionalFormattingEvaluator(final Workbook wb, final WorkbookEvaluatorProvider provider) {
        this.formats = new HashMap<String, List<EvaluationConditionalFormatRule>>();
        this.values = new HashMap<CellReference, List<EvaluationConditionalFormatRule>>();
        this.workbook = wb;
        this.workbookEvaluator = provider._getWorkbookEvaluator();
    }
    
    protected WorkbookEvaluator getWorkbookEvaluator() {
        return this.workbookEvaluator;
    }
    
    public void clearAllCachedFormats() {
        this.formats.clear();
    }
    
    public void clearAllCachedValues() {
        this.values.clear();
    }
    
    protected List<EvaluationConditionalFormatRule> getRules(final Sheet sheet) {
        final String sheetName = sheet.getSheetName();
        List<EvaluationConditionalFormatRule> rules = this.formats.get(sheetName);
        if (rules == null) {
            if (this.formats.containsKey(sheetName)) {
                return Collections.emptyList();
            }
            final SheetConditionalFormatting scf = sheet.getSheetConditionalFormatting();
            final int count = scf.getNumConditionalFormattings();
            rules = new ArrayList<EvaluationConditionalFormatRule>(count);
            this.formats.put(sheetName, rules);
            for (int i = 0; i < count; ++i) {
                final ConditionalFormatting f = scf.getConditionalFormattingAt(i);
                final CellRangeAddress[] regions = f.getFormattingRanges();
                for (int r = 0; r < f.getNumberOfRules(); ++r) {
                    final ConditionalFormattingRule rule = f.getRule(r);
                    rules.add(new EvaluationConditionalFormatRule(this.workbookEvaluator, sheet, f, i, rule, r, regions));
                }
            }
            Collections.sort(rules);
        }
        return Collections.unmodifiableList((List<? extends EvaluationConditionalFormatRule>)rules);
    }
    
    public List<EvaluationConditionalFormatRule> getConditionalFormattingForCell(final CellReference cellRef) {
        List<EvaluationConditionalFormatRule> rules = this.values.get(cellRef);
        if (rules == null) {
            rules = new ArrayList<EvaluationConditionalFormatRule>();
            Sheet sheet;
            if (cellRef.getSheetName() != null) {
                sheet = this.workbook.getSheet(cellRef.getSheetName());
            }
            else {
                sheet = this.workbook.getSheetAt(this.workbook.getActiveSheetIndex());
            }
            boolean stopIfTrue = false;
            for (final EvaluationConditionalFormatRule rule : this.getRules(sheet)) {
                if (stopIfTrue) {
                    continue;
                }
                if (!rule.matches(cellRef)) {
                    continue;
                }
                rules.add(rule);
                stopIfTrue = rule.getRule().getStopIfTrue();
            }
            Collections.sort(rules);
            this.values.put(cellRef, rules);
        }
        return Collections.unmodifiableList((List<? extends EvaluationConditionalFormatRule>)rules);
    }
    
    public List<EvaluationConditionalFormatRule> getConditionalFormattingForCell(final Cell cell) {
        return this.getConditionalFormattingForCell(getRef(cell));
    }
    
    public static CellReference getRef(final Cell cell) {
        return new CellReference(cell.getSheet().getSheetName(), cell.getRowIndex(), cell.getColumnIndex(), false, false);
    }
    
    public List<EvaluationConditionalFormatRule> getFormatRulesForSheet(final String sheetName) {
        return this.getFormatRulesForSheet(this.workbook.getSheet(sheetName));
    }
    
    public List<EvaluationConditionalFormatRule> getFormatRulesForSheet(final Sheet sheet) {
        return this.getRules(sheet);
    }
    
    public List<Cell> getMatchingCells(final Sheet sheet, final int conditionalFormattingIndex, final int ruleIndex) {
        for (final EvaluationConditionalFormatRule rule : this.getRules(sheet)) {
            if (rule.getSheet().equals(sheet) && rule.getFormattingIndex() == conditionalFormattingIndex && rule.getRuleIndex() == ruleIndex) {
                return this.getMatchingCells(rule);
            }
        }
        return Collections.emptyList();
    }
    
    public List<Cell> getMatchingCells(final EvaluationConditionalFormatRule rule) {
        final List<Cell> cells = new ArrayList<Cell>();
        final Sheet sheet = rule.getSheet();
        for (final CellRangeAddress region : rule.getRegions()) {
            for (int r = region.getFirstRow(); r <= region.getLastRow(); ++r) {
                final Row row = sheet.getRow(r);
                if (row != null) {
                    for (int c = region.getFirstColumn(); c <= region.getLastColumn(); ++c) {
                        final Cell cell = row.getCell(c);
                        if (cell != null) {
                            final List<EvaluationConditionalFormatRule> cellRules = this.getConditionalFormattingForCell(cell);
                            if (cellRules.contains(rule)) {
                                cells.add(cell);
                            }
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableList((List<? extends Cell>)cells);
    }
}
