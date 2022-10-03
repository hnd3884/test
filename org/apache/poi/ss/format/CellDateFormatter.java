package org.apache.poi.ss.format;

import java.util.regex.Matcher;
import org.apache.poi.util.StringUtil;
import java.util.Formatter;
import java.util.Date;
import java.text.AttributedCharacterIterator;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.poi.util.LocaleUtil;
import java.util.Calendar;
import java.text.DateFormat;

public class CellDateFormatter extends CellFormatter
{
    private boolean amPmUpper;
    private boolean showM;
    private boolean showAmPm;
    private final DateFormat dateFmt;
    private String sFmt;
    private final Calendar EXCEL_EPOCH_CAL;
    private static CellDateFormatter SIMPLE_DATE;
    
    public CellDateFormatter(final String format) {
        this(LocaleUtil.getUserLocale(), format);
    }
    
    public CellDateFormatter(final Locale locale, final String format) {
        super(format);
        this.EXCEL_EPOCH_CAL = LocaleUtil.getLocaleCalendar(1904, 0, 1);
        final DatePartHandler partHandler = new DatePartHandler();
        final StringBuffer descBuf = CellFormatPart.parseFormat(format, CellFormatType.DATE, partHandler);
        partHandler.finish(descBuf);
        final String ptrn = descBuf.toString().replaceAll("((y)(?!y))(?<!yy)", "yy");
        (this.dateFmt = new SimpleDateFormat(ptrn, locale)).setTimeZone(LocaleUtil.getUserTimeZone());
    }
    
    @Override
    public synchronized void formatValue(final StringBuffer toAppendTo, Object value) {
        if (value == null) {
            value = 0.0;
        }
        if (value instanceof Number) {
            final Number num = (Number)value;
            final long v = num.longValue();
            if (v == 0L) {
                value = this.EXCEL_EPOCH_CAL.getTime();
            }
            else {
                final Calendar c = (Calendar)this.EXCEL_EPOCH_CAL.clone();
                c.add(13, (int)(v / 1000L));
                c.add(14, (int)(v % 1000L));
                value = c.getTime();
            }
        }
        final AttributedCharacterIterator it = this.dateFmt.formatToCharacterIterator(value);
        boolean doneAm = false;
        boolean doneMillis = false;
        for (char ch = it.first(); ch != '\uffff'; ch = it.next()) {
            if (it.getAttribute(DateFormat.Field.MILLISECOND) != null) {
                if (!doneMillis) {
                    final Date dateObj = (Date)value;
                    final int pos = toAppendTo.length();
                    try (final Formatter formatter = new Formatter(toAppendTo, Locale.ROOT)) {
                        final long msecs = dateObj.getTime() % 1000L;
                        formatter.format(this.locale, this.sFmt, msecs / 1000.0);
                    }
                    toAppendTo.delete(pos, pos + 2);
                    doneMillis = true;
                }
            }
            else if (it.getAttribute(DateFormat.Field.AM_PM) != null) {
                if (!doneAm) {
                    if (this.showAmPm) {
                        if (this.amPmUpper) {
                            toAppendTo.append(StringUtil.toUpperCase(ch));
                            if (this.showM) {
                                toAppendTo.append('M');
                            }
                        }
                        else {
                            toAppendTo.append(StringUtil.toLowerCase(ch));
                            if (this.showM) {
                                toAppendTo.append('m');
                            }
                        }
                    }
                    doneAm = true;
                }
            }
            else {
                toAppendTo.append(ch);
            }
        }
    }
    
    @Override
    public void simpleValue(final StringBuffer toAppendTo, final Object value) {
        synchronized (CellDateFormatter.class) {
            if (CellDateFormatter.SIMPLE_DATE == null || !CellDateFormatter.SIMPLE_DATE.EXCEL_EPOCH_CAL.equals(this.EXCEL_EPOCH_CAL)) {
                CellDateFormatter.SIMPLE_DATE = new CellDateFormatter("mm/d/y");
            }
        }
        CellDateFormatter.SIMPLE_DATE.formatValue(toAppendTo, value);
    }
    
    private class DatePartHandler implements CellFormatPart.PartHandler
    {
        private int mStart;
        private int mLen;
        private int hStart;
        private int hLen;
        
        private DatePartHandler() {
            this.mStart = -1;
            this.hStart = -1;
        }
        
        @Override
        public String handlePart(final Matcher m, String part, final CellFormatType type, final StringBuffer desc) {
            final int pos = desc.length();
            final char firstCh = part.charAt(0);
            switch (firstCh) {
                case 'S':
                case 's': {
                    if (this.mStart >= 0) {
                        for (int i = 0; i < this.mLen; ++i) {
                            desc.setCharAt(this.mStart + i, 'm');
                        }
                        this.mStart = -1;
                    }
                    return part.toLowerCase(Locale.ROOT);
                }
                case 'H':
                case 'h': {
                    this.mStart = -1;
                    this.hStart = pos;
                    this.hLen = part.length();
                    return part.toLowerCase(Locale.ROOT);
                }
                case 'D':
                case 'd': {
                    this.mStart = -1;
                    if (part.length() <= 2) {
                        return part.toLowerCase(Locale.ROOT);
                    }
                    return part.toLowerCase(Locale.ROOT).replace('d', 'E');
                }
                case 'M':
                case 'm': {
                    this.mStart = pos;
                    this.mLen = part.length();
                    if (this.hStart >= 0) {
                        return part.toLowerCase(Locale.ROOT);
                    }
                    return part.toUpperCase(Locale.ROOT);
                }
                case 'Y':
                case 'y': {
                    this.mStart = -1;
                    if (part.length() == 3) {
                        part = "yyyy";
                    }
                    return part.toLowerCase(Locale.ROOT);
                }
                case '0': {
                    this.mStart = -1;
                    final int sLen = part.length();
                    CellDateFormatter.this.sFmt = "%0" + (sLen + 2) + "." + sLen + "f";
                    return part.replace('0', 'S');
                }
                case 'A':
                case 'P':
                case 'a':
                case 'p': {
                    if (part.length() > 1) {
                        this.mStart = -1;
                        CellDateFormatter.this.showAmPm = true;
                        CellDateFormatter.this.showM = StringUtil.toLowerCase(part.charAt(1)).equals("m");
                        CellDateFormatter.this.amPmUpper = (CellDateFormatter.this.showM || StringUtil.isUpperCase(part.charAt(0)));
                        return "a";
                    }
                    break;
                }
            }
            return null;
        }
        
        public void finish(final StringBuffer toAppendTo) {
            if (this.hStart >= 0 && !CellDateFormatter.this.showAmPm) {
                for (int i = 0; i < this.hLen; ++i) {
                    toAppendTo.setCharAt(this.hStart + i, 'H');
                }
            }
        }
    }
}
