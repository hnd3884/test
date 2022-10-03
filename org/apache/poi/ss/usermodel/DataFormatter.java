package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.format.CellFormatResult;
import java.text.ParsePosition;
import java.text.FieldPosition;
import org.apache.poi.util.POILogFactory;
import java.util.Observable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.math.BigDecimal;
import org.apache.poi.ss.util.NumberToTextConverter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.formula.ConditionalFormattingEvaluator;
import java.util.HashMap;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.POILogger;
import java.util.Locale;
import java.util.Map;
import java.text.Format;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.util.regex.Pattern;
import java.util.Observer;

public class DataFormatter implements Observer
{
    private static final String defaultFractionWholePartFormat = "#";
    private static final String defaultFractionFractionPartFormat = "#/##";
    private static final Pattern numPattern;
    private static final Pattern daysAsText;
    private static final Pattern amPmPattern;
    private static final Pattern rangeConditionalPattern;
    private static final Pattern localePatternGroup;
    private static final Pattern colorPattern;
    private static final Pattern fractionPattern;
    private static final Pattern fractionStripper;
    private static final Pattern alternateGrouping;
    private static final String invalidDateTimeString;
    private DecimalFormatSymbols decimalSymbols;
    private DateFormatSymbols dateSymbols;
    private DateFormat defaultDateformat;
    private Format generalNumberFormat;
    private Format defaultNumFormat;
    private final Map<String, Format> formats;
    private final boolean emulateCSV;
    private Locale locale;
    private boolean localeIsAdapting;
    private final LocaleChangeObservable localeChangedObservable;
    private static POILogger logger;
    
    public DataFormatter() {
        this(false);
    }
    
    public DataFormatter(final boolean emulateCSV) {
        this(LocaleUtil.getUserLocale(), true, emulateCSV);
    }
    
    public DataFormatter(final Locale locale) {
        this(locale, false);
    }
    
    public DataFormatter(final Locale locale, final boolean emulateCSV) {
        this(locale, false, emulateCSV);
    }
    
    public DataFormatter(final Locale locale, final boolean localeIsAdapting, final boolean emulateCSV) {
        this.formats = new HashMap<String, Format>();
        this.localeChangedObservable = new LocaleChangeObservable();
        this.localeIsAdapting = true;
        this.localeChangedObservable.addObserver(this);
        this.localeChangedObservable.checkForLocaleChange(locale);
        this.localeIsAdapting = localeIsAdapting;
        this.emulateCSV = emulateCSV;
    }
    
    private Format getFormat(final Cell cell, final ConditionalFormattingEvaluator cfEvaluator) {
        if (cell == null) {
            return null;
        }
        final ExcelNumberFormat numFmt = ExcelNumberFormat.from(cell, cfEvaluator);
        if (numFmt == null) {
            return null;
        }
        final int formatIndex = numFmt.getIdx();
        final String formatStr = numFmt.getFormat();
        if (formatStr == null || formatStr.trim().length() == 0) {
            return null;
        }
        return this.getFormat(cell.getNumericCellValue(), formatIndex, formatStr, this.isDate1904(cell));
    }
    
    private boolean isDate1904(final Cell cell) {
        return cell != null && cell.getSheet().getWorkbook() instanceof Date1904Support && ((Date1904Support)cell.getSheet().getWorkbook()).isDate1904();
    }
    
    private Format getFormat(final double cellValue, final int formatIndex, final String formatStrIn, final boolean use1904Windowing) {
        this.localeChangedObservable.checkForLocaleChange();
        String formatStr = formatStrIn;
        Label_0164: {
            if (formatStr.contains(";")) {
                if (formatStr.indexOf(59) == formatStr.lastIndexOf(59)) {
                    if (!DataFormatter.rangeConditionalPattern.matcher(formatStr).matches()) {
                        break Label_0164;
                    }
                }
                try {
                    final CellFormat cfmt = CellFormat.getInstance(this.locale, formatStr);
                    Object cellValueO = cellValue;
                    if (DateUtil.isADateFormat(formatIndex, formatStr) && (double)cellValueO != 0.0) {
                        cellValueO = DateUtil.getJavaDate(cellValue, use1904Windowing);
                    }
                    return new CellFormatResultWrapper(cfmt.apply(cellValueO));
                }
                catch (final Exception e) {
                    DataFormatter.logger.log(5, "Formatting failed for format " + formatStr + ", falling back", e);
                }
            }
        }
        if (this.emulateCSV && cellValue == 0.0 && formatStr.contains("#") && !formatStr.contains("0")) {
            formatStr = formatStr.replaceAll("#", "");
        }
        Format format = this.formats.get(formatStr);
        if (format != null) {
            return format;
        }
        if ("General".equalsIgnoreCase(formatStr) || "@".equals(formatStr)) {
            return this.generalNumberFormat;
        }
        format = this.createFormat(cellValue, formatIndex, formatStr);
        this.formats.put(formatStr, format);
        return format;
    }
    
    public Format createFormat(final Cell cell) {
        final int formatIndex = cell.getCellStyle().getDataFormat();
        final String formatStr = cell.getCellStyle().getDataFormatString();
        return this.createFormat(cell.getNumericCellValue(), formatIndex, formatStr);
    }
    
    private Format createFormat(final double cellValue, final int formatIndex, final String sFormat) {
        this.localeChangedObservable.checkForLocaleChange();
        String formatStr = sFormat;
        for (Matcher colourM = DataFormatter.colorPattern.matcher(formatStr); colourM.find(); colourM = DataFormatter.colorPattern.matcher(formatStr)) {
            final String colour = colourM.group();
            final int at = formatStr.indexOf(colour);
            if (at == -1) {
                break;
            }
            final String nFormatStr = formatStr.substring(0, at) + formatStr.substring(at + colour.length());
            if (nFormatStr.equals(formatStr)) {
                break;
            }
            formatStr = nFormatStr;
        }
        for (Matcher m = DataFormatter.localePatternGroup.matcher(formatStr); m.find(); m = DataFormatter.localePatternGroup.matcher(formatStr)) {
            final String match = m.group();
            String symbol = match.substring(match.indexOf(36) + 1, match.indexOf(45));
            if (symbol.indexOf(36) > -1) {
                symbol = symbol.substring(0, symbol.indexOf(36)) + '\\' + symbol.substring(symbol.indexOf(36));
            }
            formatStr = m.replaceAll(symbol);
        }
        if (formatStr == null || formatStr.trim().length() == 0) {
            return this.getDefaultFormat(cellValue);
        }
        if ("General".equalsIgnoreCase(formatStr) || "@".equals(formatStr)) {
            return this.generalNumberFormat;
        }
        if (DateUtil.isADateFormat(formatIndex, formatStr) && DateUtil.isValidExcelDate(cellValue)) {
            return this.createDateFormat(formatStr, cellValue);
        }
        if (formatStr.contains("#/") || formatStr.contains("?/")) {
            final String[] split;
            final String[] chunks = split = formatStr.split(";");
            for (final String chunk1 : split) {
                String chunk2 = chunk1.replaceAll("\\?", "#");
                final Matcher matcher = DataFormatter.fractionStripper.matcher(chunk2);
                chunk2 = matcher.replaceAll(" ");
                chunk2 = chunk2.replaceAll(" +", " ");
                final Matcher fractionMatcher = DataFormatter.fractionPattern.matcher(chunk2);
                if (fractionMatcher.find()) {
                    final String wholePart = (fractionMatcher.group(1) == null) ? "" : "#";
                    return new FractionFormat(wholePart, fractionMatcher.group(3));
                }
            }
            return new FractionFormat("#", "#/##");
        }
        if (DataFormatter.numPattern.matcher(formatStr).find()) {
            return this.createNumberFormat(formatStr, cellValue);
        }
        if (this.emulateCSV) {
            return new ConstantStringFormat(this.cleanFormatForNumber(formatStr));
        }
        return null;
    }
    
    private Format createDateFormat(final String pFormatStr, final double cellValue) {
        String formatStr = pFormatStr;
        formatStr = formatStr.replaceAll("\\\\-", "-");
        formatStr = formatStr.replaceAll("\\\\,", ",");
        formatStr = formatStr.replaceAll("\\\\\\.", ".");
        formatStr = formatStr.replaceAll("\\\\ ", " ");
        formatStr = formatStr.replaceAll("\\\\/", "/");
        formatStr = formatStr.replaceAll(";@", "");
        formatStr = formatStr.replaceAll("\"/\"", "/");
        formatStr = formatStr.replace("\"\"", "'");
        formatStr = formatStr.replaceAll("\\\\T", "'T'");
        boolean hasAmPm = false;
        for (Matcher amPmMatcher = DataFormatter.amPmPattern.matcher(formatStr); amPmMatcher.find(); amPmMatcher = DataFormatter.amPmPattern.matcher(formatStr)) {
            formatStr = amPmMatcher.replaceAll("@");
            hasAmPm = true;
        }
        formatStr = formatStr.replaceAll("@", "a");
        final Matcher dateMatcher = DataFormatter.daysAsText.matcher(formatStr);
        if (dateMatcher.find()) {
            final String match = dateMatcher.group(0).toUpperCase(Locale.ROOT).replaceAll("D", "E");
            formatStr = dateMatcher.replaceAll(match);
        }
        final StringBuilder sb = new StringBuilder();
        final char[] chars = formatStr.toCharArray();
        boolean mIsMonth = true;
        final List<Integer> ms = new ArrayList<Integer>();
        boolean isElapsed = false;
        for (int j = 0; j < chars.length; ++j) {
            char c = chars[j];
            if (c == '\'') {
                sb.append(c);
                ++j;
                while (j < chars.length) {
                    c = chars[j];
                    sb.append(c);
                    if (c == '\'') {
                        break;
                    }
                    ++j;
                }
            }
            else if (c == '[' && !isElapsed) {
                isElapsed = true;
                mIsMonth = false;
                sb.append(c);
            }
            else if (c == ']' && isElapsed) {
                isElapsed = false;
                sb.append(c);
            }
            else if (isElapsed) {
                if (c == 'h' || c == 'H') {
                    sb.append('H');
                }
                else if (c == 'm' || c == 'M') {
                    sb.append('m');
                }
                else if (c == 's' || c == 'S') {
                    sb.append('s');
                }
                else {
                    sb.append(c);
                }
            }
            else if (c == 'h' || c == 'H') {
                mIsMonth = false;
                if (hasAmPm) {
                    sb.append('h');
                }
                else {
                    sb.append('H');
                }
            }
            else if (c == 'm' || c == 'M') {
                if (mIsMonth) {
                    sb.append('M');
                    ms.add(sb.length() - 1);
                }
                else {
                    sb.append('m');
                }
            }
            else if (c == 's' || c == 'S') {
                sb.append('s');
                for (final int index : ms) {
                    if (sb.charAt(index) == 'M') {
                        sb.replace(index, index + 1, "m");
                    }
                }
                mIsMonth = true;
                ms.clear();
            }
            else if (Character.isLetter(c)) {
                mIsMonth = true;
                ms.clear();
                if (c == 'y' || c == 'Y') {
                    sb.append('y');
                }
                else if (c == 'd' || c == 'D') {
                    sb.append('d');
                }
                else {
                    sb.append(c);
                }
            }
            else {
                if (Character.isWhitespace(c)) {
                    ms.clear();
                }
                sb.append(c);
            }
        }
        formatStr = sb.toString();
        try {
            return new ExcelStyleDateFormatter(formatStr, this.dateSymbols);
        }
        catch (final IllegalArgumentException iae) {
            DataFormatter.logger.log(1, "Formatting failed for format " + formatStr + ", falling back", iae);
            return this.getDefaultFormat(cellValue);
        }
    }
    
    private String cleanFormatForNumber(final String formatStr) {
        final StringBuilder sb = new StringBuilder(formatStr);
        if (this.emulateCSV) {
            for (int i = 0; i < sb.length(); ++i) {
                final char c = sb.charAt(i);
                if (c == '_' || c == '*' || c == '?') {
                    if (i <= 0 || sb.charAt(i - 1) != '\\') {
                        if (c == '?') {
                            sb.setCharAt(i, ' ');
                        }
                        else if (i < sb.length() - 1) {
                            if (c == '_') {
                                sb.setCharAt(i + 1, ' ');
                            }
                            else {
                                sb.deleteCharAt(i + 1);
                            }
                            sb.deleteCharAt(i);
                            --i;
                        }
                    }
                }
            }
        }
        else {
            for (int i = 0; i < sb.length(); ++i) {
                final char c = sb.charAt(i);
                if (c == '_' || c == '*') {
                    if (i <= 0 || sb.charAt(i - 1) != '\\') {
                        if (i < sb.length() - 1) {
                            sb.deleteCharAt(i + 1);
                        }
                        sb.deleteCharAt(i);
                        --i;
                    }
                }
            }
        }
        for (int i = 0; i < sb.length(); ++i) {
            final char c = sb.charAt(i);
            if (c == '\\' || c == '\"') {
                sb.deleteCharAt(i);
                --i;
            }
            else if (c == '+' && i > 0 && sb.charAt(i - 1) == 'E') {
                sb.deleteCharAt(i);
                --i;
            }
        }
        return sb.toString();
    }
    
    private Format createNumberFormat(final String formatStr, final double cellValue) {
        String format = this.cleanFormatForNumber(formatStr);
        DecimalFormatSymbols symbols = this.decimalSymbols;
        final Matcher agm = DataFormatter.alternateGrouping.matcher(format);
        if (agm.find()) {
            final char grouping = agm.group(2).charAt(0);
            if (grouping != ',') {
                symbols = DecimalFormatSymbols.getInstance(this.locale);
                symbols.setGroupingSeparator(grouping);
                final String oldPart = agm.group(1);
                final String newPart = oldPart.replace(grouping, ',');
                format = format.replace(oldPart, newPart);
            }
        }
        try {
            return new InternalDecimalFormatWithScale(format, symbols);
        }
        catch (final IllegalArgumentException iae) {
            DataFormatter.logger.log(1, "Formatting failed for format " + formatStr + ", falling back", iae);
            return this.getDefaultFormat(cellValue);
        }
    }
    
    public Format getDefaultFormat(final Cell cell) {
        return this.getDefaultFormat(cell.getNumericCellValue());
    }
    
    private Format getDefaultFormat(final double cellValue) {
        this.localeChangedObservable.checkForLocaleChange();
        if (this.defaultNumFormat != null) {
            return this.defaultNumFormat;
        }
        return this.generalNumberFormat;
    }
    
    private String performDateFormatting(final Date d, final Format dateFormat) {
        final Format df = (dateFormat != null) ? dateFormat : this.defaultDateformat;
        synchronized (df) {
            return df.format(d);
        }
    }
    
    private String getFormattedDateString(final Cell cell, final ConditionalFormattingEvaluator cfEvaluator) {
        if (cell == null) {
            return null;
        }
        Format dateFormat = this.getFormat(cell, cfEvaluator);
        if (dateFormat == null) {
            if (this.defaultDateformat == null) {
                final DateFormatSymbols sym = DateFormatSymbols.getInstance(LocaleUtil.getUserLocale());
                final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", sym);
                sdf.setTimeZone(LocaleUtil.getUserTimeZone());
                dateFormat = sdf;
            }
            else {
                dateFormat = this.defaultNumFormat;
            }
        }
        synchronized (dateFormat) {
            if (dateFormat instanceof ExcelStyleDateFormatter) {
                ((ExcelStyleDateFormatter)dateFormat).setDateToBeFormatted(cell.getNumericCellValue());
            }
            final Date d = cell.getDateCellValue();
            return this.performDateFormatting(d, dateFormat);
        }
    }
    
    private String getFormattedNumberString(final Cell cell, final ConditionalFormattingEvaluator cfEvaluator) {
        if (cell == null) {
            return null;
        }
        final Format numberFormat = this.getFormat(cell, cfEvaluator);
        final double d = cell.getNumericCellValue();
        if (numberFormat == null) {
            return String.valueOf(d);
        }
        final String formatted = numberFormat.format(d);
        return formatted.replaceFirst("E(\\d)", "E+$1");
    }
    
    public String formatRawCellContents(final double value, final int formatIndex, final String formatString) {
        return this.formatRawCellContents(value, formatIndex, formatString, false);
    }
    
    public String formatRawCellContents(final double value, final int formatIndex, final String formatString, final boolean use1904Windowing) {
        this.localeChangedObservable.checkForLocaleChange();
        if (DateUtil.isADateFormat(formatIndex, formatString)) {
            if (DateUtil.isValidExcelDate(value)) {
                final Format dateFormat = this.getFormat(value, formatIndex, formatString, use1904Windowing);
                if (dateFormat instanceof ExcelStyleDateFormatter) {
                    ((ExcelStyleDateFormatter)dateFormat).setDateToBeFormatted(value);
                }
                final Date d = DateUtil.getJavaDate(value, use1904Windowing);
                return this.performDateFormatting(d, dateFormat);
            }
            if (this.emulateCSV) {
                return DataFormatter.invalidDateTimeString;
            }
        }
        final Format numberFormat = this.getFormat(value, formatIndex, formatString, use1904Windowing);
        if (numberFormat == null) {
            return String.valueOf(value);
        }
        final String textValue = NumberToTextConverter.toText(value);
        String result;
        if (textValue.indexOf(69) > -1) {
            result = numberFormat.format(value);
        }
        else {
            result = numberFormat.format(new BigDecimal(textValue));
        }
        if (result.indexOf(69) > -1 && !result.contains("E-")) {
            result = result.replaceFirst("E", "E+");
        }
        return result;
    }
    
    public String formatCellValue(final Cell cell) {
        return this.formatCellValue(cell, null);
    }
    
    public String formatCellValue(final Cell cell, final FormulaEvaluator evaluator) {
        return this.formatCellValue(cell, evaluator, null);
    }
    
    public String formatCellValue(final Cell cell, final FormulaEvaluator evaluator, final ConditionalFormattingEvaluator cfEvaluator) {
        this.localeChangedObservable.checkForLocaleChange();
        if (cell == null) {
            return "";
        }
        CellType cellType = cell.getCellType();
        if (cellType == CellType.FORMULA) {
            if (evaluator == null) {
                return cell.getCellFormula();
            }
            cellType = evaluator.evaluateFormulaCell(cell);
        }
        switch (cellType) {
            case NUMERIC: {
                if (DateUtil.isCellDateFormatted(cell, cfEvaluator)) {
                    return this.getFormattedDateString(cell, cfEvaluator);
                }
                return this.getFormattedNumberString(cell, cfEvaluator);
            }
            case STRING: {
                return cell.getRichStringCellValue().getString();
            }
            case BOOLEAN: {
                return cell.getBooleanCellValue() ? "TRUE" : "FALSE";
            }
            case BLANK: {
                return "";
            }
            case ERROR: {
                return FormulaError.forInt(cell.getErrorCellValue()).getString();
            }
            default: {
                throw new RuntimeException("Unexpected celltype (" + cellType + ")");
            }
        }
    }
    
    public void setDefaultNumberFormat(final Format format) {
        for (final Map.Entry<String, Format> entry : this.formats.entrySet()) {
            if (entry.getValue() == this.generalNumberFormat) {
                entry.setValue(format);
            }
        }
        this.defaultNumFormat = format;
    }
    
    public void addFormat(final String excelFormatStr, final Format format) {
        this.formats.put(excelFormatStr, format);
    }
    
    private static DecimalFormat createIntegerOnlyFormat(final String fmt) {
        final DecimalFormatSymbols dsf = DecimalFormatSymbols.getInstance(Locale.ROOT);
        final DecimalFormat result = new DecimalFormat(fmt, dsf);
        result.setParseIntegerOnly(true);
        return result;
    }
    
    public static void setExcelStyleRoundingMode(final DecimalFormat format) {
        setExcelStyleRoundingMode(format, RoundingMode.HALF_UP);
    }
    
    public static void setExcelStyleRoundingMode(final DecimalFormat format, final RoundingMode roundingMode) {
        format.setRoundingMode(roundingMode);
    }
    
    public Observable getLocaleChangedObservable() {
        return this.localeChangedObservable;
    }
    
    @Override
    public void update(final Observable observable, final Object localeObj) {
        if (!(localeObj instanceof Locale)) {
            return;
        }
        final Locale newLocale = (Locale)localeObj;
        if (!this.localeIsAdapting || newLocale.equals(this.locale)) {
            return;
        }
        this.locale = newLocale;
        this.dateSymbols = DateFormatSymbols.getInstance(this.locale);
        this.decimalSymbols = DecimalFormatSymbols.getInstance(this.locale);
        this.generalNumberFormat = new ExcelGeneralNumberFormat(this.locale);
        (this.defaultDateformat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", this.dateSymbols)).setTimeZone(LocaleUtil.getUserTimeZone());
        this.formats.clear();
        final Format zipFormat = ZipPlusFourFormat.instance;
        this.addFormat("00000\\-0000", zipFormat);
        this.addFormat("00000-0000", zipFormat);
        final Format phoneFormat = PhoneFormat.instance;
        this.addFormat("[<=9999999]###\\-####;\\(###\\)\\ ###\\-####", phoneFormat);
        this.addFormat("[<=9999999]###-####;(###) ###-####", phoneFormat);
        this.addFormat("###\\-####;\\(###\\)\\ ###\\-####", phoneFormat);
        this.addFormat("###-####;(###) ###-####", phoneFormat);
        final Format ssnFormat = SSNFormat.instance;
        this.addFormat("000\\-00\\-0000", ssnFormat);
        this.addFormat("000-00-0000", ssnFormat);
    }
    
    static {
        numPattern = Pattern.compile("[0#]+");
        daysAsText = Pattern.compile("([d]{3,})", 2);
        amPmPattern = Pattern.compile("(([AP])[M/P]*)", 2);
        rangeConditionalPattern = Pattern.compile(".*\\[\\s*(>|>=|<|<=|=)\\s*[0-9]*\\.*[0-9].*");
        localePatternGroup = Pattern.compile("(\\[\\$[^-\\]]*-[0-9A-Z]+])");
        colorPattern = Pattern.compile("(\\[BLACK])|(\\[BLUE])|(\\[CYAN])|(\\[GREEN])|(\\[MAGENTA])|(\\[RED])|(\\[WHITE])|(\\[YELLOW])|(\\[COLOR\\s*\\d])|(\\[COLOR\\s*[0-5]\\d])", 2);
        fractionPattern = Pattern.compile("(?:([#\\d]+)\\s+)?(#+)\\s*/\\s*([#\\d]+)");
        fractionStripper = Pattern.compile("(\"[^\"]*\")|([^ ?#\\d/]+)");
        alternateGrouping = Pattern.compile("([#0]([^.#0])[#0]{3})");
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 255; ++i) {
            buf.append('#');
        }
        invalidDateTimeString = buf.toString();
        DataFormatter.logger = POILogFactory.getLogger(DataFormatter.class);
    }
    
    private class LocaleChangeObservable extends Observable
    {
        void checkForLocaleChange() {
            this.checkForLocaleChange(LocaleUtil.getUserLocale());
        }
        
        void checkForLocaleChange(final Locale newLocale) {
            if (!DataFormatter.this.localeIsAdapting) {
                return;
            }
            if (newLocale.equals(DataFormatter.this.locale)) {
                return;
            }
            super.setChanged();
            this.notifyObservers(newLocale);
        }
    }
    
    private static class InternalDecimalFormatWithScale extends Format
    {
        private static final Pattern endsWithCommas;
        private BigDecimal divider;
        private static final BigDecimal ONE_THOUSAND;
        private final DecimalFormat df;
        
        private static String trimTrailingCommas(final String s) {
            return s.replaceAll(",+$", "");
        }
        
        public InternalDecimalFormatWithScale(final String pattern, final DecimalFormatSymbols symbols) {
            DataFormatter.setExcelStyleRoundingMode(this.df = new DecimalFormat(trimTrailingCommas(pattern), symbols));
            final Matcher endsWithCommasMatcher = InternalDecimalFormatWithScale.endsWithCommas.matcher(pattern);
            if (endsWithCommasMatcher.find()) {
                final String commas = endsWithCommasMatcher.group(1);
                BigDecimal temp = BigDecimal.ONE;
                for (int i = 0; i < commas.length(); ++i) {
                    temp = temp.multiply(InternalDecimalFormatWithScale.ONE_THOUSAND);
                }
                this.divider = temp;
            }
            else {
                this.divider = null;
            }
        }
        
        private Object scaleInput(Object obj) {
            if (this.divider != null) {
                if (obj instanceof BigDecimal) {
                    obj = ((BigDecimal)obj).divide(this.divider, RoundingMode.HALF_UP);
                }
                else {
                    if (!(obj instanceof Double)) {
                        throw new UnsupportedOperationException();
                    }
                    obj = (double)obj / this.divider.doubleValue();
                }
            }
            return obj;
        }
        
        @Override
        public StringBuffer format(Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
            obj = this.scaleInput(obj);
            return this.df.format(obj, toAppendTo, pos);
        }
        
        @Override
        public Object parseObject(final String source, final ParsePosition pos) {
            throw new UnsupportedOperationException();
        }
        
        static {
            endsWithCommas = Pattern.compile("(,+)$");
            ONE_THOUSAND = new BigDecimal(1000);
        }
    }
    
    private static final class SSNFormat extends Format
    {
        public static final Format instance;
        private static final DecimalFormat df;
        
        public static String format(final Number num) {
            final String result = SSNFormat.df.format(num);
            return result.substring(0, 3) + '-' + result.substring(3, 5) + '-' + result.substring(5, 9);
        }
        
        @Override
        public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
            return toAppendTo.append(format((Number)obj));
        }
        
        @Override
        public Object parseObject(final String source, final ParsePosition pos) {
            return SSNFormat.df.parseObject(source, pos);
        }
        
        static {
            instance = new SSNFormat();
            df = createIntegerOnlyFormat("000000000");
        }
    }
    
    private static final class ZipPlusFourFormat extends Format
    {
        public static final Format instance;
        private static final DecimalFormat df;
        
        public static String format(final Number num) {
            final String result = ZipPlusFourFormat.df.format(num);
            return result.substring(0, 5) + '-' + result.substring(5, 9);
        }
        
        @Override
        public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
            return toAppendTo.append(format((Number)obj));
        }
        
        @Override
        public Object parseObject(final String source, final ParsePosition pos) {
            return ZipPlusFourFormat.df.parseObject(source, pos);
        }
        
        static {
            instance = new ZipPlusFourFormat();
            df = createIntegerOnlyFormat("000000000");
        }
    }
    
    private static final class PhoneFormat extends Format
    {
        public static final Format instance;
        private static final DecimalFormat df;
        
        public static String format(final Number num) {
            final String result = PhoneFormat.df.format(num);
            final StringBuilder sb = new StringBuilder();
            final int len = result.length();
            if (len <= 4) {
                return result;
            }
            final String seg3 = result.substring(len - 4, len);
            final String seg4 = result.substring(Math.max(0, len - 7), len - 4);
            final String seg5 = result.substring(Math.max(0, len - 10), Math.max(0, len - 7));
            if (seg5.trim().length() > 0) {
                sb.append('(').append(seg5).append(") ");
            }
            if (seg4.trim().length() > 0) {
                sb.append(seg4).append('-');
            }
            sb.append(seg3);
            return sb.toString();
        }
        
        @Override
        public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
            return toAppendTo.append(format((Number)obj));
        }
        
        @Override
        public Object parseObject(final String source, final ParsePosition pos) {
            return PhoneFormat.df.parseObject(source, pos);
        }
        
        static {
            instance = new PhoneFormat();
            df = createIntegerOnlyFormat("##########");
        }
    }
    
    private static final class ConstantStringFormat extends Format
    {
        private static final DecimalFormat df;
        private final String str;
        
        public ConstantStringFormat(final String s) {
            this.str = s;
        }
        
        @Override
        public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
            return toAppendTo.append(this.str);
        }
        
        @Override
        public Object parseObject(final String source, final ParsePosition pos) {
            return ConstantStringFormat.df.parseObject(source, pos);
        }
        
        static {
            df = createIntegerOnlyFormat("##########");
        }
    }
    
    private final class CellFormatResultWrapper extends Format
    {
        private final CellFormatResult result;
        
        private CellFormatResultWrapper(final CellFormatResult result) {
            this.result = result;
        }
        
        @Override
        public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
            if (DataFormatter.this.emulateCSV) {
                return toAppendTo.append(this.result.text);
            }
            return toAppendTo.append(this.result.text.trim());
        }
        
        @Override
        public Object parseObject(final String source, final ParsePosition pos) {
            return null;
        }
    }
}
