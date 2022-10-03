package org.apache.poi.ss.usermodel;

import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.formula.EvaluationConditionalFormatRule;
import org.apache.poi.ss.formula.ConditionalFormattingEvaluator;

public class ExcelNumberFormat
{
    private final int idx;
    private final String format;
    
    public static ExcelNumberFormat from(final CellStyle style) {
        if (style == null) {
            return null;
        }
        return new ExcelNumberFormat(style.getDataFormat(), style.getDataFormatString());
    }
    
    public static ExcelNumberFormat from(final Cell cell, final ConditionalFormattingEvaluator cfEvaluator) {
        if (cell == null) {
            return null;
        }
        ExcelNumberFormat nf = null;
        if (cfEvaluator != null) {
            final List<EvaluationConditionalFormatRule> rules = cfEvaluator.getConditionalFormattingForCell(cell);
            for (final EvaluationConditionalFormatRule rule : rules) {
                nf = rule.getNumberFormat();
                if (nf != null) {
                    break;
                }
            }
        }
        if (nf == null) {
            final CellStyle style = cell.getCellStyle();
            nf = from(style);
        }
        return nf;
    }
    
    public ExcelNumberFormat(final int idx, final String format) {
        this.idx = idx;
        this.format = format;
    }
    
    public int getIdx() {
        return this.idx;
    }
    
    public String getFormat() {
        return this.format;
    }
}
