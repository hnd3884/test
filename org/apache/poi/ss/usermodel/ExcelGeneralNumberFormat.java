package org.apache.poi.ss.usermodel;

import java.math.RoundingMode;
import java.text.ParsePosition;
import java.math.BigDecimal;
import java.text.FieldPosition;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.math.MathContext;
import java.text.Format;

public class ExcelGeneralNumberFormat extends Format
{
    private static final long serialVersionUID = 1L;
    private static final MathContext TO_10_SF;
    private final DecimalFormatSymbols decimalSymbols;
    private final DecimalFormat integerFormat;
    private final DecimalFormat decimalFormat;
    private final DecimalFormat scientificFormat;
    
    public ExcelGeneralNumberFormat(final Locale locale) {
        this.decimalSymbols = DecimalFormatSymbols.getInstance(locale);
        DataFormatter.setExcelStyleRoundingMode(this.scientificFormat = new DecimalFormat("0.#####E0", this.decimalSymbols));
        DataFormatter.setExcelStyleRoundingMode(this.integerFormat = new DecimalFormat("#", this.decimalSymbols));
        DataFormatter.setExcelStyleRoundingMode(this.decimalFormat = new DecimalFormat("#.##########", this.decimalSymbols));
    }
    
    @Override
    public StringBuffer format(final Object number, final StringBuffer toAppendTo, final FieldPosition pos) {
        if (!(number instanceof Number)) {
            return this.integerFormat.format(number, toAppendTo, pos);
        }
        final double value = ((Number)number).doubleValue();
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            return this.integerFormat.format(number, toAppendTo, pos);
        }
        final double abs = Math.abs(value);
        if (abs >= 1.0E11 || (abs <= 1.0E-10 && abs > 0.0)) {
            return this.scientificFormat.format(number, toAppendTo, pos);
        }
        if (Math.floor(value) == value || abs >= 1.0E10) {
            return this.integerFormat.format(number, toAppendTo, pos);
        }
        final double rounded = new BigDecimal(value).round(ExcelGeneralNumberFormat.TO_10_SF).doubleValue();
        return this.decimalFormat.format(rounded, toAppendTo, pos);
    }
    
    @Override
    public Object parseObject(final String source, final ParsePosition pos) {
        throw new UnsupportedOperationException();
    }
    
    static {
        TO_10_SF = new MathContext(10, RoundingMode.HALF_UP);
    }
}
