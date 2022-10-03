package org.apache.poi.xssf.usermodel;

import java.util.HashMap;
import org.apache.poi.ss.usermodel.BorderFormatting;
import org.apache.poi.ss.usermodel.FontFormatting;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.DataBarFormatting;
import org.apache.poi.ss.usermodel.ColorScaleFormatting;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STConditionalFormattingOperator;
import org.apache.poi.ss.usermodel.ConditionFilterData;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTNumFmt;
import org.apache.poi.ss.usermodel.ExcelNumberFormat;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColorScale;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIconSet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STIconSetType;
import org.apache.poi.ss.usermodel.IconMultiStateFormatting;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfvo;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataBar;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCfvoType;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.apache.poi.xssf.model.StylesTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxf;
import org.apache.poi.ss.usermodel.ConditionFilterType;
import org.apache.poi.ss.usermodel.ConditionType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCfType;
import java.util.Map;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfRule;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;

public class XSSFConditionalFormattingRule implements ConditionalFormattingRule
{
    private final CTCfRule _cfRule;
    private XSSFSheet _sh;
    private static Map<STCfType.Enum, ConditionType> typeLookup;
    private static Map<STCfType.Enum, ConditionFilterType> filterTypeLookup;
    
    XSSFConditionalFormattingRule(final XSSFSheet sh) {
        this._cfRule = CTCfRule.Factory.newInstance();
        this._sh = sh;
    }
    
    XSSFConditionalFormattingRule(final XSSFSheet sh, final CTCfRule cfRule) {
        this._cfRule = cfRule;
        this._sh = sh;
    }
    
    CTCfRule getCTCfRule() {
        return this._cfRule;
    }
    
    CTDxf getDxf(final boolean create) {
        final StylesTable styles = this._sh.getWorkbook().getStylesSource();
        CTDxf dxf = null;
        if (styles._getDXfsSize() > 0 && this._cfRule.isSetDxfId()) {
            final int dxfId = (int)this._cfRule.getDxfId();
            dxf = styles.getDxfAt(dxfId);
        }
        if (create && dxf == null) {
            dxf = CTDxf.Factory.newInstance();
            final int dxfId = styles.putDxf(dxf);
            this._cfRule.setDxfId((long)(dxfId - 1));
        }
        return dxf;
    }
    
    public int getPriority() {
        final int priority = this._cfRule.getPriority();
        return (priority >= 1) ? priority : 0;
    }
    
    public boolean getStopIfTrue() {
        return this._cfRule.getStopIfTrue();
    }
    
    public XSSFBorderFormatting createBorderFormatting() {
        final CTDxf dxf = this.getDxf(true);
        CTBorder border;
        if (!dxf.isSetBorder()) {
            border = dxf.addNewBorder();
        }
        else {
            border = dxf.getBorder();
        }
        return new XSSFBorderFormatting(border, this._sh.getWorkbook().getStylesSource().getIndexedColors());
    }
    
    public XSSFBorderFormatting getBorderFormatting() {
        final CTDxf dxf = this.getDxf(false);
        if (dxf == null || !dxf.isSetBorder()) {
            return null;
        }
        return new XSSFBorderFormatting(dxf.getBorder(), this._sh.getWorkbook().getStylesSource().getIndexedColors());
    }
    
    public XSSFFontFormatting createFontFormatting() {
        final CTDxf dxf = this.getDxf(true);
        CTFont font;
        if (!dxf.isSetFont()) {
            font = dxf.addNewFont();
        }
        else {
            font = dxf.getFont();
        }
        return new XSSFFontFormatting(font, this._sh.getWorkbook().getStylesSource().getIndexedColors());
    }
    
    public XSSFFontFormatting getFontFormatting() {
        final CTDxf dxf = this.getDxf(false);
        if (dxf == null || !dxf.isSetFont()) {
            return null;
        }
        return new XSSFFontFormatting(dxf.getFont(), this._sh.getWorkbook().getStylesSource().getIndexedColors());
    }
    
    public XSSFPatternFormatting createPatternFormatting() {
        final CTDxf dxf = this.getDxf(true);
        CTFill fill;
        if (!dxf.isSetFill()) {
            fill = dxf.addNewFill();
        }
        else {
            fill = dxf.getFill();
        }
        return new XSSFPatternFormatting(fill, this._sh.getWorkbook().getStylesSource().getIndexedColors());
    }
    
    public XSSFPatternFormatting getPatternFormatting() {
        final CTDxf dxf = this.getDxf(false);
        if (dxf == null || !dxf.isSetFill()) {
            return null;
        }
        return new XSSFPatternFormatting(dxf.getFill(), this._sh.getWorkbook().getStylesSource().getIndexedColors());
    }
    
    public XSSFDataBarFormatting createDataBarFormatting(final XSSFColor color) {
        if (this._cfRule.isSetDataBar() && this._cfRule.getType() == STCfType.DATA_BAR) {
            return this.getDataBarFormatting();
        }
        this._cfRule.setType(STCfType.DATA_BAR);
        CTDataBar bar = null;
        if (this._cfRule.isSetDataBar()) {
            bar = this._cfRule.getDataBar();
        }
        else {
            bar = this._cfRule.addNewDataBar();
        }
        bar.setColor(color.getCTColor());
        final CTCfvo min = bar.addNewCfvo();
        min.setType(STCfvoType.Enum.forString(ConditionalFormattingThreshold.RangeType.MIN.name));
        final CTCfvo max = bar.addNewCfvo();
        max.setType(STCfvoType.Enum.forString(ConditionalFormattingThreshold.RangeType.MAX.name));
        return new XSSFDataBarFormatting(bar, this._sh.getWorkbook().getStylesSource().getIndexedColors());
    }
    
    public XSSFDataBarFormatting getDataBarFormatting() {
        if (this._cfRule.isSetDataBar()) {
            final CTDataBar bar = this._cfRule.getDataBar();
            return new XSSFDataBarFormatting(bar, this._sh.getWorkbook().getStylesSource().getIndexedColors());
        }
        return null;
    }
    
    public XSSFIconMultiStateFormatting createMultiStateFormatting(final IconMultiStateFormatting.IconSet iconSet) {
        if (this._cfRule.isSetIconSet() && this._cfRule.getType() == STCfType.ICON_SET) {
            return this.getMultiStateFormatting();
        }
        this._cfRule.setType(STCfType.ICON_SET);
        CTIconSet icons = null;
        if (this._cfRule.isSetIconSet()) {
            icons = this._cfRule.getIconSet();
        }
        else {
            icons = this._cfRule.addNewIconSet();
        }
        if (iconSet.name != null) {
            final STIconSetType.Enum xIconSet = STIconSetType.Enum.forString(iconSet.name);
            icons.setIconSet(xIconSet);
        }
        final int jump = 100 / iconSet.num;
        final STCfvoType.Enum type = STCfvoType.Enum.forString(ConditionalFormattingThreshold.RangeType.PERCENT.name);
        for (int i = 0; i < iconSet.num; ++i) {
            final CTCfvo cfvo = icons.addNewCfvo();
            cfvo.setType(type);
            cfvo.setVal(Integer.toString(i * jump));
        }
        return new XSSFIconMultiStateFormatting(icons);
    }
    
    public XSSFIconMultiStateFormatting getMultiStateFormatting() {
        if (this._cfRule.isSetIconSet()) {
            final CTIconSet icons = this._cfRule.getIconSet();
            return new XSSFIconMultiStateFormatting(icons);
        }
        return null;
    }
    
    public XSSFColorScaleFormatting createColorScaleFormatting() {
        if (this._cfRule.isSetColorScale() && this._cfRule.getType() == STCfType.COLOR_SCALE) {
            return this.getColorScaleFormatting();
        }
        this._cfRule.setType(STCfType.COLOR_SCALE);
        CTColorScale scale = null;
        if (this._cfRule.isSetColorScale()) {
            scale = this._cfRule.getColorScale();
        }
        else {
            scale = this._cfRule.addNewColorScale();
        }
        if (scale.sizeOfCfvoArray() == 0) {
            CTCfvo cfvo = scale.addNewCfvo();
            cfvo.setType(STCfvoType.Enum.forString(ConditionalFormattingThreshold.RangeType.MIN.name));
            cfvo = scale.addNewCfvo();
            cfvo.setType(STCfvoType.Enum.forString(ConditionalFormattingThreshold.RangeType.PERCENTILE.name));
            cfvo.setVal("50");
            cfvo = scale.addNewCfvo();
            cfvo.setType(STCfvoType.Enum.forString(ConditionalFormattingThreshold.RangeType.MAX.name));
            for (int i = 0; i < 3; ++i) {
                scale.addNewColor();
            }
        }
        return new XSSFColorScaleFormatting(scale, this._sh.getWorkbook().getStylesSource().getIndexedColors());
    }
    
    public XSSFColorScaleFormatting getColorScaleFormatting() {
        if (this._cfRule.isSetColorScale()) {
            final CTColorScale scale = this._cfRule.getColorScale();
            return new XSSFColorScaleFormatting(scale, this._sh.getWorkbook().getStylesSource().getIndexedColors());
        }
        return null;
    }
    
    public ExcelNumberFormat getNumberFormat() {
        final CTDxf dxf = this.getDxf(false);
        if (dxf == null || !dxf.isSetNumFmt()) {
            return null;
        }
        final CTNumFmt numFmt = dxf.getNumFmt();
        return new ExcelNumberFormat((int)numFmt.getNumFmtId(), numFmt.getFormatCode());
    }
    
    public ConditionType getConditionType() {
        return XSSFConditionalFormattingRule.typeLookup.get(this._cfRule.getType());
    }
    
    public ConditionFilterType getConditionFilterType() {
        return XSSFConditionalFormattingRule.filterTypeLookup.get(this._cfRule.getType());
    }
    
    public ConditionFilterData getFilterConfiguration() {
        return (ConditionFilterData)new XSSFConditionFilterData(this._cfRule);
    }
    
    public byte getComparisonOperation() {
        final STConditionalFormattingOperator.Enum op = this._cfRule.getOperator();
        if (op == null) {
            return 0;
        }
        switch (op.intValue()) {
            case 1: {
                return 6;
            }
            case 2: {
                return 8;
            }
            case 6: {
                return 5;
            }
            case 5: {
                return 7;
            }
            case 3: {
                return 3;
            }
            case 4: {
                return 4;
            }
            case 7: {
                return 1;
            }
            case 8: {
                return 2;
            }
            default: {
                return 0;
            }
        }
    }
    
    public String getFormula1() {
        return (this._cfRule.sizeOfFormulaArray() > 0) ? this._cfRule.getFormulaArray(0) : null;
    }
    
    public String getFormula2() {
        return (this._cfRule.sizeOfFormulaArray() == 2) ? this._cfRule.getFormulaArray(1) : null;
    }
    
    public String getText() {
        return this._cfRule.getText();
    }
    
    public int getStripeSize() {
        return 0;
    }
    
    static {
        XSSFConditionalFormattingRule.typeLookup = new HashMap<STCfType.Enum, ConditionType>();
        XSSFConditionalFormattingRule.filterTypeLookup = new HashMap<STCfType.Enum, ConditionFilterType>();
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.CELL_IS, ConditionType.CELL_VALUE_IS);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.EXPRESSION, ConditionType.FORMULA);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.COLOR_SCALE, ConditionType.COLOR_SCALE);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.DATA_BAR, ConditionType.DATA_BAR);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.ICON_SET, ConditionType.ICON_SET);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.TOP_10, ConditionType.FILTER);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.UNIQUE_VALUES, ConditionType.FILTER);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.DUPLICATE_VALUES, ConditionType.FILTER);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.CONTAINS_TEXT, ConditionType.FILTER);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.NOT_CONTAINS_TEXT, ConditionType.FILTER);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.BEGINS_WITH, ConditionType.FILTER);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.ENDS_WITH, ConditionType.FILTER);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.CONTAINS_BLANKS, ConditionType.FILTER);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.NOT_CONTAINS_BLANKS, ConditionType.FILTER);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.CONTAINS_ERRORS, ConditionType.FILTER);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.NOT_CONTAINS_ERRORS, ConditionType.FILTER);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.TIME_PERIOD, ConditionType.FILTER);
        XSSFConditionalFormattingRule.typeLookup.put(STCfType.ABOVE_AVERAGE, ConditionType.FILTER);
        XSSFConditionalFormattingRule.filterTypeLookup.put(STCfType.TOP_10, ConditionFilterType.TOP_10);
        XSSFConditionalFormattingRule.filterTypeLookup.put(STCfType.UNIQUE_VALUES, ConditionFilterType.UNIQUE_VALUES);
        XSSFConditionalFormattingRule.filterTypeLookup.put(STCfType.DUPLICATE_VALUES, ConditionFilterType.DUPLICATE_VALUES);
        XSSFConditionalFormattingRule.filterTypeLookup.put(STCfType.CONTAINS_TEXT, ConditionFilterType.CONTAINS_TEXT);
        XSSFConditionalFormattingRule.filterTypeLookup.put(STCfType.NOT_CONTAINS_TEXT, ConditionFilterType.NOT_CONTAINS_TEXT);
        XSSFConditionalFormattingRule.filterTypeLookup.put(STCfType.BEGINS_WITH, ConditionFilterType.BEGINS_WITH);
        XSSFConditionalFormattingRule.filterTypeLookup.put(STCfType.ENDS_WITH, ConditionFilterType.ENDS_WITH);
        XSSFConditionalFormattingRule.filterTypeLookup.put(STCfType.CONTAINS_BLANKS, ConditionFilterType.CONTAINS_BLANKS);
        XSSFConditionalFormattingRule.filterTypeLookup.put(STCfType.NOT_CONTAINS_BLANKS, ConditionFilterType.NOT_CONTAINS_BLANKS);
        XSSFConditionalFormattingRule.filterTypeLookup.put(STCfType.CONTAINS_ERRORS, ConditionFilterType.CONTAINS_ERRORS);
        XSSFConditionalFormattingRule.filterTypeLookup.put(STCfType.NOT_CONTAINS_ERRORS, ConditionFilterType.NOT_CONTAINS_ERRORS);
        XSSFConditionalFormattingRule.filterTypeLookup.put(STCfType.TIME_PERIOD, ConditionFilterType.TIME_PERIOD);
        XSSFConditionalFormattingRule.filterTypeLookup.put(STCfType.ABOVE_AVERAGE, ConditionFilterType.ABOVE_AVERAGE);
    }
}
