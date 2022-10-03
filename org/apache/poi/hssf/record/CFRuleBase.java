package org.apache.poi.hssf.record;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.hssf.record.cf.PatternFormatting;
import org.apache.poi.hssf.record.cf.BorderFormatting;
import org.apache.poi.hssf.record.cf.FontFormatting;
import org.apache.poi.util.BitField;
import org.apache.poi.util.POILogger;

public abstract class CFRuleBase extends StandardRecord
{
    public static final byte CONDITION_TYPE_CELL_VALUE_IS = 1;
    public static final byte CONDITION_TYPE_FORMULA = 2;
    public static final byte CONDITION_TYPE_COLOR_SCALE = 3;
    public static final byte CONDITION_TYPE_DATA_BAR = 4;
    public static final byte CONDITION_TYPE_FILTER = 5;
    public static final byte CONDITION_TYPE_ICON_SET = 6;
    public static final int TEMPLATE_CELL_VALUE = 0;
    public static final int TEMPLATE_FORMULA = 1;
    public static final int TEMPLATE_COLOR_SCALE_FORMATTING = 2;
    public static final int TEMPLATE_DATA_BAR_FORMATTING = 3;
    public static final int TEMPLATE_ICON_SET_FORMATTING = 4;
    public static final int TEMPLATE_FILTER = 5;
    public static final int TEMPLATE_UNIQUE_VALUES = 7;
    public static final int TEMPLATE_CONTAINS_TEXT = 8;
    public static final int TEMPLATE_CONTAINS_BLANKS = 9;
    public static final int TEMPLATE_CONTAINS_NO_BLANKS = 10;
    public static final int TEMPLATE_CONTAINS_ERRORS = 11;
    public static final int TEMPLATE_CONTAINS_NO_ERRORS = 12;
    public static final int TEMPLATE_TODAY = 15;
    public static final int TEMPLATE_TOMORROW = 16;
    public static final int TEMPLATE_YESTERDAY = 17;
    public static final int TEMPLATE_LAST_7_DAYS = 18;
    public static final int TEMPLATE_LAST_MONTH = 19;
    public static final int TEMPLATE_NEXT_MONTH = 20;
    public static final int TEMPLATE_THIS_WEEK = 21;
    public static final int TEMPLATE_NEXT_WEEK = 22;
    public static final int TEMPLATE_LAST_WEEK = 23;
    public static final int TEMPLATE_THIS_MONTH = 24;
    public static final int TEMPLATE_ABOVE_AVERAGE = 25;
    public static final int TEMPLATE_BELOW_AVERAGE = 26;
    public static final int TEMPLATE_DUPLICATE_VALUES = 27;
    public static final int TEMPLATE_ABOVE_OR_EQUAL_TO_AVERAGE = 29;
    public static final int TEMPLATE_BELOW_OR_EQUAL_TO_AVERAGE = 30;
    protected static final POILogger logger;
    static final BitField modificationBits;
    static final BitField alignHor;
    static final BitField alignVer;
    static final BitField alignWrap;
    static final BitField alignRot;
    static final BitField alignJustLast;
    static final BitField alignIndent;
    static final BitField alignShrin;
    static final BitField mergeCell;
    static final BitField protLocked;
    static final BitField protHidden;
    static final BitField bordLeft;
    static final BitField bordRight;
    static final BitField bordTop;
    static final BitField bordBot;
    static final BitField bordTlBr;
    static final BitField bordBlTr;
    static final BitField pattStyle;
    static final BitField pattCol;
    static final BitField pattBgCol;
    static final BitField notUsed2;
    static final BitField undocumented;
    static final BitField fmtBlockBits;
    static final BitField font;
    static final BitField align;
    static final BitField bord;
    static final BitField patt;
    static final BitField prot;
    static final BitField alignTextDir;
    private byte condition_type;
    private byte comparison_operator;
    protected int formatting_options;
    protected short formatting_not_used;
    protected FontFormatting _fontFormatting;
    protected BorderFormatting _borderFormatting;
    protected PatternFormatting _patternFormatting;
    private Formula formula1;
    private Formula formula2;
    
    private static BitField bf(final int i) {
        return BitFieldFactory.getInstance(i);
    }
    
    protected CFRuleBase(final byte conditionType, final byte comparisonOperation) {
        this.setConditionType(conditionType);
        this.setComparisonOperation(comparisonOperation);
        this.formula1 = Formula.create(Ptg.EMPTY_PTG_ARRAY);
        this.formula2 = Formula.create(Ptg.EMPTY_PTG_ARRAY);
    }
    
    protected CFRuleBase(final byte conditionType, final byte comparisonOperation, final Ptg[] formula1, final Ptg[] formula2) {
        this(conditionType, comparisonOperation);
        this.formula1 = Formula.create(formula1);
        this.formula2 = Formula.create(formula2);
    }
    
    protected CFRuleBase() {
    }
    
    protected CFRuleBase(final CFRuleBase other) {
        super(other);
        this.setConditionType(other.getConditionType());
        this.setComparisonOperation(other.getComparisonOperation());
        this.formatting_options = other.formatting_options;
        this.formatting_not_used = other.formatting_not_used;
        this._fontFormatting = (other.containsFontFormattingBlock() ? other.getFontFormatting().copy() : null);
        this._borderFormatting = (other.containsBorderFormattingBlock() ? other.getBorderFormatting().copy() : null);
        this._patternFormatting = (other.containsPatternFormattingBlock() ? other.getPatternFormatting().copy() : null);
        this.formula1 = other.getFormula1().copy();
        this.formula2 = other.getFormula2().copy();
    }
    
    protected int readFormatOptions(final RecordInputStream in) {
        this.formatting_options = in.readInt();
        this.formatting_not_used = in.readShort();
        int len = 6;
        if (this.containsFontFormattingBlock()) {
            this._fontFormatting = new FontFormatting(in);
            len += this._fontFormatting.getDataLength();
        }
        if (this.containsBorderFormattingBlock()) {
            this._borderFormatting = new BorderFormatting(in);
            len += this._borderFormatting.getDataLength();
        }
        if (this.containsPatternFormattingBlock()) {
            this._patternFormatting = new PatternFormatting(in);
            len += this._patternFormatting.getDataLength();
        }
        return len;
    }
    
    public byte getConditionType() {
        return this.condition_type;
    }
    
    protected void setConditionType(final byte condition_type) {
        if (this instanceof CFRuleRecord && condition_type != 1 && condition_type != 2) {
            throw new IllegalArgumentException("CFRuleRecord only accepts Value-Is and Formula types");
        }
        this.condition_type = condition_type;
    }
    
    public void setComparisonOperation(final byte operation) {
        if (operation < 0 || operation > 8) {
            throw new IllegalArgumentException("Valid operators are only in the range 0 to 8");
        }
        this.comparison_operator = operation;
    }
    
    public byte getComparisonOperation() {
        return this.comparison_operator;
    }
    
    public boolean containsFontFormattingBlock() {
        return this.getOptionFlag(CFRuleBase.font);
    }
    
    public void setFontFormatting(final FontFormatting fontFormatting) {
        this._fontFormatting = fontFormatting;
        this.setOptionFlag(fontFormatting != null, CFRuleBase.font);
    }
    
    public FontFormatting getFontFormatting() {
        if (this.containsFontFormattingBlock()) {
            return this._fontFormatting;
        }
        return null;
    }
    
    public boolean containsAlignFormattingBlock() {
        return this.getOptionFlag(CFRuleBase.align);
    }
    
    public void setAlignFormattingUnchanged() {
        this.setOptionFlag(false, CFRuleBase.align);
    }
    
    public boolean containsBorderFormattingBlock() {
        return this.getOptionFlag(CFRuleBase.bord);
    }
    
    public void setBorderFormatting(final BorderFormatting borderFormatting) {
        this._borderFormatting = borderFormatting;
        this.setOptionFlag(borderFormatting != null, CFRuleBase.bord);
    }
    
    public BorderFormatting getBorderFormatting() {
        if (this.containsBorderFormattingBlock()) {
            return this._borderFormatting;
        }
        return null;
    }
    
    public boolean containsPatternFormattingBlock() {
        return this.getOptionFlag(CFRuleBase.patt);
    }
    
    public void setPatternFormatting(final PatternFormatting patternFormatting) {
        this._patternFormatting = patternFormatting;
        this.setOptionFlag(patternFormatting != null, CFRuleBase.patt);
    }
    
    public PatternFormatting getPatternFormatting() {
        if (this.containsPatternFormattingBlock()) {
            return this._patternFormatting;
        }
        return null;
    }
    
    public boolean containsProtectionFormattingBlock() {
        return this.getOptionFlag(CFRuleBase.prot);
    }
    
    public void setProtectionFormattingUnchanged() {
        this.setOptionFlag(false, CFRuleBase.prot);
    }
    
    public int getOptions() {
        return this.formatting_options;
    }
    
    private boolean isModified(final BitField field) {
        return !field.isSet(this.formatting_options);
    }
    
    private void setModified(final boolean modified, final BitField field) {
        this.formatting_options = field.setBoolean(this.formatting_options, !modified);
    }
    
    public boolean isLeftBorderModified() {
        return this.isModified(CFRuleBase.bordLeft);
    }
    
    public void setLeftBorderModified(final boolean modified) {
        this.setModified(modified, CFRuleBase.bordLeft);
    }
    
    public boolean isRightBorderModified() {
        return this.isModified(CFRuleBase.bordRight);
    }
    
    public void setRightBorderModified(final boolean modified) {
        this.setModified(modified, CFRuleBase.bordRight);
    }
    
    public boolean isTopBorderModified() {
        return this.isModified(CFRuleBase.bordTop);
    }
    
    public void setTopBorderModified(final boolean modified) {
        this.setModified(modified, CFRuleBase.bordTop);
    }
    
    public boolean isBottomBorderModified() {
        return this.isModified(CFRuleBase.bordBot);
    }
    
    public void setBottomBorderModified(final boolean modified) {
        this.setModified(modified, CFRuleBase.bordBot);
    }
    
    public boolean isTopLeftBottomRightBorderModified() {
        return this.isModified(CFRuleBase.bordTlBr);
    }
    
    public void setTopLeftBottomRightBorderModified(final boolean modified) {
        this.setModified(modified, CFRuleBase.bordTlBr);
    }
    
    public boolean isBottomLeftTopRightBorderModified() {
        return this.isModified(CFRuleBase.bordBlTr);
    }
    
    public void setBottomLeftTopRightBorderModified(final boolean modified) {
        this.setModified(modified, CFRuleBase.bordBlTr);
    }
    
    public boolean isPatternStyleModified() {
        return this.isModified(CFRuleBase.pattStyle);
    }
    
    public void setPatternStyleModified(final boolean modified) {
        this.setModified(modified, CFRuleBase.pattStyle);
    }
    
    public boolean isPatternColorModified() {
        return this.isModified(CFRuleBase.pattCol);
    }
    
    public void setPatternColorModified(final boolean modified) {
        this.setModified(modified, CFRuleBase.pattCol);
    }
    
    public boolean isPatternBackgroundColorModified() {
        return this.isModified(CFRuleBase.pattBgCol);
    }
    
    public void setPatternBackgroundColorModified(final boolean modified) {
        this.setModified(modified, CFRuleBase.pattBgCol);
    }
    
    private boolean getOptionFlag(final BitField field) {
        return field.isSet(this.formatting_options);
    }
    
    private void setOptionFlag(final boolean flag, final BitField field) {
        this.formatting_options = field.setBoolean(this.formatting_options, flag);
    }
    
    protected int getFormattingBlockSize() {
        return 6 + (this.containsFontFormattingBlock() ? this._fontFormatting.getRawRecord().length : 0) + (this.containsBorderFormattingBlock() ? 8 : 0) + (this.containsPatternFormattingBlock() ? 4 : 0);
    }
    
    protected void serializeFormattingBlock(final LittleEndianOutput out) {
        out.writeInt(this.formatting_options);
        out.writeShort(this.formatting_not_used);
        if (this.containsFontFormattingBlock()) {
            final byte[] fontFormattingRawRecord = this._fontFormatting.getRawRecord();
            out.write(fontFormattingRawRecord);
        }
        if (this.containsBorderFormattingBlock()) {
            this._borderFormatting.serialize(out);
        }
        if (this.containsPatternFormattingBlock()) {
            this._patternFormatting.serialize(out);
        }
    }
    
    public Ptg[] getParsedExpression1() {
        return this.formula1.getTokens();
    }
    
    public void setParsedExpression1(final Ptg[] ptgs) {
        this.formula1 = Formula.create(ptgs);
    }
    
    protected Formula getFormula1() {
        return this.formula1;
    }
    
    protected void setFormula1(final Formula formula1) {
        this.formula1 = formula1;
    }
    
    public Ptg[] getParsedExpression2() {
        return Formula.getTokens(this.formula2);
    }
    
    public void setParsedExpression2(final Ptg[] ptgs) {
        this.formula2 = Formula.create(ptgs);
    }
    
    protected Formula getFormula2() {
        return this.formula2;
    }
    
    protected void setFormula2(final Formula formula2) {
        this.formula2 = formula2;
    }
    
    protected static int getFormulaSize(final Formula formula) {
        return formula.getEncodedTokenSize();
    }
    
    public static Ptg[] parseFormula(final String formula, final HSSFSheet sheet) {
        if (formula == null) {
            return null;
        }
        final int sheetIndex = sheet.getWorkbook().getSheetIndex(sheet);
        return HSSFFormulaParser.parse(formula, sheet.getWorkbook(), FormulaType.CELL, sheetIndex);
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public abstract CFRuleBase clone();
    
    @Override
    public abstract CFRuleBase copy();
    
    static {
        logger = POILogFactory.getLogger(CFRuleBase.class);
        modificationBits = bf(4194303);
        alignHor = bf(1);
        alignVer = bf(2);
        alignWrap = bf(4);
        alignRot = bf(8);
        alignJustLast = bf(16);
        alignIndent = bf(32);
        alignShrin = bf(64);
        mergeCell = bf(128);
        protLocked = bf(256);
        protHidden = bf(512);
        bordLeft = bf(1024);
        bordRight = bf(2048);
        bordTop = bf(4096);
        bordBot = bf(8192);
        bordTlBr = bf(16384);
        bordBlTr = bf(32768);
        pattStyle = bf(65536);
        pattCol = bf(131072);
        pattBgCol = bf(262144);
        notUsed2 = bf(3670016);
        undocumented = bf(62914560);
        fmtBlockBits = bf(2080374784);
        font = bf(67108864);
        align = bf(134217728);
        bord = bf(268435456);
        patt = bf(536870912);
        prot = bf(1073741824);
        alignTextDir = bf(Integer.MIN_VALUE);
    }
    
    public interface ComparisonOperator
    {
        public static final byte NO_COMPARISON = 0;
        public static final byte BETWEEN = 1;
        public static final byte NOT_BETWEEN = 2;
        public static final byte EQUAL = 3;
        public static final byte NOT_EQUAL = 4;
        public static final byte GT = 5;
        public static final byte LT = 6;
        public static final byte GE = 7;
        public static final byte LE = 8;
        public static final byte max_operator = 8;
    }
}
