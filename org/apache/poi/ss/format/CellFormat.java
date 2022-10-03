package org.apache.poi.ss.format;

import org.apache.poi.util.Removal;
import org.apache.poi.ss.usermodel.CellType;
import javax.swing.JLabel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.WeakHashMap;
import org.apache.poi.util.LocaleUtil;
import java.awt.Color;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Locale;

public class CellFormat
{
    private final Locale locale;
    private final String format;
    private final CellFormatPart posNumFmt;
    private final CellFormatPart zeroNumFmt;
    private final CellFormatPart negNumFmt;
    private final CellFormatPart textFmt;
    private final int formatPartCount;
    private static final Pattern ONE_PART;
    private static final String INVALID_VALUE_FOR_FORMAT = "###############################################################################################################################################################################################################################################################";
    private static String QUOTE;
    private static final Map<Locale, Map<String, CellFormat>> formatCache;
    
    private static CellFormat createGeneralFormat(final Locale locale) {
        return new CellFormat(locale, "General") {
            @Override
            public CellFormatResult apply(final Object value) {
                final String text = new CellGeneralFormatter(locale).format(value);
                return new CellFormatResult(true, text, null);
            }
        };
    }
    
    public static CellFormat getInstance(final String format) {
        return getInstance(LocaleUtil.getUserLocale(), format);
    }
    
    public static synchronized CellFormat getInstance(final Locale locale, final String format) {
        final Map<String, CellFormat> formatMap = CellFormat.formatCache.computeIfAbsent(locale, k -> new WeakHashMap());
        CellFormat fmt = formatMap.get(format);
        if (fmt == null) {
            if (format.equals("General") || format.equals("@")) {
                fmt = createGeneralFormat(locale);
            }
            else {
                fmt = new CellFormat(locale, format);
            }
            formatMap.put(format, fmt);
        }
        return fmt;
    }
    
    private CellFormat(final Locale locale, final String format) {
        this.locale = locale;
        this.format = format;
        final CellFormatPart defaultTextFormat = new CellFormatPart(locale, "@");
        final Matcher m = CellFormat.ONE_PART.matcher(format);
        final List<CellFormatPart> parts = new ArrayList<CellFormatPart>();
        while (m.find()) {
            try {
                String valueDesc = m.group();
                if (valueDesc.endsWith(";")) {
                    valueDesc = valueDesc.substring(0, valueDesc.length() - 1);
                }
                parts.add(new CellFormatPart(locale, valueDesc));
            }
            catch (final RuntimeException e) {
                CellFormatter.logger.log(Level.WARNING, "Invalid format: " + CellFormatter.quote(m.group()), e);
                parts.add(null);
            }
        }
        switch (this.formatPartCount = parts.size()) {
            case 1: {
                this.posNumFmt = parts.get(0);
                this.negNumFmt = null;
                this.zeroNumFmt = null;
                this.textFmt = defaultTextFormat;
                break;
            }
            case 2: {
                this.posNumFmt = parts.get(0);
                this.negNumFmt = parts.get(1);
                this.zeroNumFmt = null;
                this.textFmt = defaultTextFormat;
                break;
            }
            case 3: {
                this.posNumFmt = parts.get(0);
                this.negNumFmt = parts.get(1);
                this.zeroNumFmt = parts.get(2);
                this.textFmt = defaultTextFormat;
                break;
            }
            default: {
                this.posNumFmt = parts.get(0);
                this.negNumFmt = parts.get(1);
                this.zeroNumFmt = parts.get(2);
                this.textFmt = parts.get(3);
                break;
            }
        }
    }
    
    public CellFormatResult apply(final Object value) {
        if (value instanceof Number) {
            final Number num = (Number)value;
            final double val = num.doubleValue();
            if (val < 0.0 && ((this.formatPartCount == 2 && !this.posNumFmt.hasCondition() && !this.negNumFmt.hasCondition()) || (this.formatPartCount == 3 && !this.negNumFmt.hasCondition()) || (this.formatPartCount == 4 && !this.negNumFmt.hasCondition()))) {
                return this.negNumFmt.apply(-val);
            }
            return this.getApplicableFormatPart(val).apply(val);
        }
        else {
            if (!(value instanceof Date)) {
                return this.textFmt.apply(value);
            }
            final Double numericValue = DateUtil.getExcelDate((Date)value);
            if (DateUtil.isValidExcelDate(numericValue)) {
                return this.getApplicableFormatPart(numericValue).apply(value);
            }
            throw new IllegalArgumentException("value " + numericValue + " of date " + value + " is not a valid Excel date");
        }
    }
    
    private CellFormatResult apply(final Date date, final double numericValue) {
        return this.getApplicableFormatPart(numericValue).apply(date);
    }
    
    public CellFormatResult apply(final Cell c) {
        switch (ultimateType(c)) {
            case BLANK: {
                return this.apply("");
            }
            case BOOLEAN: {
                return this.apply(c.getBooleanCellValue());
            }
            case NUMERIC: {
                final Double value = c.getNumericCellValue();
                if (this.getApplicableFormatPart(value).getCellFormatType() != CellFormatType.DATE) {
                    return this.apply(value);
                }
                if (DateUtil.isValidExcelDate(value)) {
                    return this.apply(c.getDateCellValue(), value);
                }
                return this.apply("###############################################################################################################################################################################################################################################################");
            }
            case STRING: {
                return this.apply(c.getStringCellValue());
            }
            default: {
                return this.apply("?");
            }
        }
    }
    
    public CellFormatResult apply(final JLabel label, final Object value) {
        final CellFormatResult result = this.apply(value);
        label.setText(result.text);
        if (result.textColor != null) {
            label.setForeground(result.textColor);
        }
        return result;
    }
    
    private CellFormatResult apply(final JLabel label, final Date date, final double numericValue) {
        final CellFormatResult result = this.apply(date, numericValue);
        label.setText(result.text);
        if (result.textColor != null) {
            label.setForeground(result.textColor);
        }
        return result;
    }
    
    public CellFormatResult apply(final JLabel label, final Cell c) {
        switch (ultimateType(c)) {
            case BLANK: {
                return this.apply(label, "");
            }
            case BOOLEAN: {
                return this.apply(label, c.getBooleanCellValue());
            }
            case NUMERIC: {
                final Double value = c.getNumericCellValue();
                if (this.getApplicableFormatPart(value).getCellFormatType() != CellFormatType.DATE) {
                    return this.apply(label, value);
                }
                if (DateUtil.isValidExcelDate(value)) {
                    return this.apply(label, c.getDateCellValue(), value);
                }
                return this.apply(label, "###############################################################################################################################################################################################################################################################");
            }
            case STRING: {
                return this.apply(label, c.getStringCellValue());
            }
            default: {
                return this.apply(label, "?");
            }
        }
    }
    
    private CellFormatPart getApplicableFormatPart(final Object value) {
        if (!(value instanceof Number)) {
            throw new IllegalArgumentException("value must be a Number");
        }
        final double val = ((Number)value).doubleValue();
        if (this.formatPartCount == 1) {
            if (!this.posNumFmt.hasCondition() || (this.posNumFmt.hasCondition() && this.posNumFmt.applies(val))) {
                return this.posNumFmt;
            }
            return new CellFormatPart(this.locale, "General");
        }
        else if (this.formatPartCount == 2) {
            if ((!this.posNumFmt.hasCondition() && val >= 0.0) || (this.posNumFmt.hasCondition() && this.posNumFmt.applies(val))) {
                return this.posNumFmt;
            }
            if (!this.negNumFmt.hasCondition() || (this.negNumFmt.hasCondition() && this.negNumFmt.applies(val))) {
                return this.negNumFmt;
            }
            return new CellFormatPart(CellFormat.QUOTE + "###############################################################################################################################################################################################################################################################" + CellFormat.QUOTE);
        }
        else {
            if ((!this.posNumFmt.hasCondition() && val > 0.0) || (this.posNumFmt.hasCondition() && this.posNumFmt.applies(val))) {
                return this.posNumFmt;
            }
            if ((!this.negNumFmt.hasCondition() && val < 0.0) || (this.negNumFmt.hasCondition() && this.negNumFmt.applies(val))) {
                return this.negNumFmt;
            }
            return this.zeroNumFmt;
        }
    }
    
    public static CellType ultimateType(final Cell cell) {
        final CellType type = cell.getCellType();
        if (type == CellType.FORMULA) {
            return cell.getCachedFormulaResultType();
        }
        return type;
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public static CellType ultimateTypeEnum(final Cell cell) {
        return ultimateType(cell);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CellFormat) {
            final CellFormat that = (CellFormat)obj;
            return this.format.equals(that.format);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.format.hashCode();
    }
    
    static {
        ONE_PART = Pattern.compile(CellFormatPart.FORMAT_PAT.pattern() + "(;|$)", 6);
        CellFormat.QUOTE = "\"";
        formatCache = new WeakHashMap<Locale, Map<String, CellFormat>>();
    }
}
