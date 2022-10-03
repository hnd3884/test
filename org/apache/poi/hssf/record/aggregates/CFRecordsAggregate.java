package org.apache.poi.hssf.record.aggregates;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.helpers.BaseRowColShifter;
import org.apache.poi.ss.formula.FormulaShifter;
import java.util.Iterator;
import org.apache.poi.hssf.record.CFRule12Record;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.CFHeader12Record;
import org.apache.poi.hssf.record.CFHeaderRecord;
import org.apache.poi.hssf.record.CFRuleRecord;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.RecordFormatException;
import java.util.function.Consumer;
import java.util.ArrayList;
import org.apache.poi.hssf.record.CFRuleBase;
import java.util.List;
import org.apache.poi.hssf.record.CFHeaderBase;
import org.apache.poi.util.POILogger;

public final class CFRecordsAggregate extends RecordAggregate
{
    private static final int MAX_97_2003_CONDTIONAL_FORMAT_RULES = 3;
    private static final POILogger logger;
    private final CFHeaderBase header;
    private final List<CFRuleBase> rules;
    
    public CFRecordsAggregate(final CFRecordsAggregate other) {
        this.rules = new ArrayList<CFRuleBase>();
        this.header = other.header.copy();
        other.rules.stream().map(t -> t.copy()).forEach(this.rules::add);
    }
    
    private CFRecordsAggregate(final CFHeaderBase pHeader, final CFRuleBase[] pRules) {
        this.rules = new ArrayList<CFRuleBase>();
        if (pHeader == null) {
            throw new IllegalArgumentException("header must not be null");
        }
        if (pRules == null) {
            throw new IllegalArgumentException("rules must not be null");
        }
        if (pRules.length > 3) {
            CFRecordsAggregate.logger.log(5, "Excel versions before 2007 require that No more than 3 rules may be specified, " + pRules.length + " were found, this file will cause problems with old Excel versions");
        }
        if (pRules.length != pHeader.getNumberOfConditionalFormats()) {
            throw new RecordFormatException("Mismatch number of rules");
        }
        this.header = pHeader;
        for (final CFRuleBase pRule : pRules) {
            this.checkRuleType(pRule);
            this.rules.add(pRule);
        }
    }
    
    public CFRecordsAggregate(final CellRangeAddress[] regions, final CFRuleBase[] rules) {
        this(createHeader(regions, rules), rules);
    }
    
    private static CFHeaderBase createHeader(final CellRangeAddress[] regions, final CFRuleBase[] rules) {
        CFHeaderBase header;
        if (rules.length == 0 || rules[0] instanceof CFRuleRecord) {
            header = new CFHeaderRecord(regions, rules.length);
        }
        else {
            header = new CFHeader12Record(regions, rules.length);
        }
        header.setNeedRecalculation(true);
        return header;
    }
    
    public static CFRecordsAggregate createCFAggregate(final RecordStream rs) {
        final Record rec = rs.getNext();
        if (rec.getSid() != 432 && rec.getSid() != 2169) {
            throw new IllegalStateException("next record sid was " + rec.getSid() + " instead of " + 432 + " or " + 2169 + " as expected");
        }
        final CFHeaderBase header = (CFHeaderBase)rec;
        final int nRules = header.getNumberOfConditionalFormats();
        final CFRuleBase[] rules = new CFRuleBase[nRules];
        for (int i = 0; i < rules.length; ++i) {
            rules[i] = (CFRuleBase)rs.getNext();
        }
        return new CFRecordsAggregate(header, rules);
    }
    
    public CFRecordsAggregate cloneCFAggregate() {
        return new CFRecordsAggregate(this);
    }
    
    public CFHeaderBase getHeader() {
        return this.header;
    }
    
    private void checkRuleIndex(final int idx) {
        if (idx < 0 || idx >= this.rules.size()) {
            throw new IllegalArgumentException("Bad rule record index (" + idx + ") nRules=" + this.rules.size());
        }
    }
    
    private void checkRuleType(final CFRuleBase r) {
        if (this.header instanceof CFHeaderRecord && r instanceof CFRuleRecord) {
            return;
        }
        if (this.header instanceof CFHeader12Record && r instanceof CFRule12Record) {
            return;
        }
        throw new IllegalArgumentException("Header and Rule must both be CF or both be CF12, can't mix");
    }
    
    public CFRuleBase getRule(final int idx) {
        this.checkRuleIndex(idx);
        return this.rules.get(idx);
    }
    
    public void setRule(final int idx, final CFRuleBase r) {
        if (r == null) {
            throw new IllegalArgumentException("r must not be null");
        }
        this.checkRuleIndex(idx);
        this.checkRuleType(r);
        this.rules.set(idx, r);
    }
    
    public void addRule(final CFRuleBase r) {
        if (r == null) {
            throw new IllegalArgumentException("r must not be null");
        }
        if (this.rules.size() >= 3) {
            CFRecordsAggregate.logger.log(5, "Excel versions before 2007 cannot cope with any more than 3 - this file will cause problems with old Excel versions");
        }
        this.checkRuleType(r);
        this.rules.add(r);
        this.header.setNumberOfConditionalFormats(this.rules.size());
    }
    
    public int getNumberOfRules() {
        return this.rules.size();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        String type = "CF";
        if (this.header instanceof CFHeader12Record) {
            type = "CF12";
        }
        buffer.append("[").append(type).append("]\n");
        if (this.header != null) {
            buffer.append(this.header);
        }
        for (final CFRuleBase cfRule : this.rules) {
            buffer.append(cfRule);
        }
        buffer.append("[/").append(type).append("]\n");
        return buffer.toString();
    }
    
    @Override
    public void visitContainedRecords(final RecordVisitor rv) {
        rv.visitRecord(this.header);
        for (final CFRuleBase rule : this.rules) {
            rv.visitRecord(rule);
        }
    }
    
    public boolean updateFormulasAfterCellShift(final FormulaShifter shifter, final int currentExternSheetIx) {
        final CellRangeAddress[] cellRanges = this.header.getCellRanges();
        boolean changed = false;
        final List<CellRangeAddress> temp = new ArrayList<CellRangeAddress>();
        for (final CellRangeAddress craOld : cellRanges) {
            final CellRangeAddress craNew = BaseRowColShifter.shiftRange(shifter, craOld, currentExternSheetIx);
            if (craNew == null) {
                changed = true;
            }
            else {
                temp.add(craNew);
                if (craNew != craOld) {
                    changed = true;
                }
            }
        }
        if (changed) {
            final int nRanges = temp.size();
            if (nRanges == 0) {
                return false;
            }
            final CellRangeAddress[] newRanges = new CellRangeAddress[nRanges];
            temp.toArray(newRanges);
            this.header.setCellRanges(newRanges);
        }
        for (final CFRuleBase rule : this.rules) {
            Ptg[] ptgs = rule.getParsedExpression1();
            if (ptgs != null && shifter.adjustFormula(ptgs, currentExternSheetIx)) {
                rule.setParsedExpression1(ptgs);
            }
            ptgs = rule.getParsedExpression2();
            if (ptgs != null && shifter.adjustFormula(ptgs, currentExternSheetIx)) {
                rule.setParsedExpression2(ptgs);
            }
            if (rule instanceof CFRule12Record) {
                final CFRule12Record rule2 = (CFRule12Record)rule;
                ptgs = rule2.getParsedExpressionScale();
                if (ptgs == null || !shifter.adjustFormula(ptgs, currentExternSheetIx)) {
                    continue;
                }
                rule2.setParsedExpressionScale(ptgs);
            }
        }
        return true;
    }
    
    static {
        logger = POILogFactory.getLogger(CFRecordsAggregate.class);
    }
}
