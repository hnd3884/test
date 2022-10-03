package org.apache.poi.ss.format;

import org.apache.poi.util.LocaleUtil;
import java.util.logging.Logger;
import java.util.Locale;

public abstract class CellFormatter
{
    protected final String format;
    protected final Locale locale;
    static final Logger logger;
    
    public CellFormatter(final String format) {
        this(LocaleUtil.getUserLocale(), format);
    }
    
    public CellFormatter(final Locale locale, final String format) {
        this.locale = locale;
        this.format = format;
    }
    
    public abstract void formatValue(final StringBuffer p0, final Object p1);
    
    public abstract void simpleValue(final StringBuffer p0, final Object p1);
    
    public String format(final Object value) {
        final StringBuffer sb = new StringBuffer();
        this.formatValue(sb, value);
        return sb.toString();
    }
    
    public String simpleFormat(final Object value) {
        final StringBuffer sb = new StringBuffer();
        this.simpleValue(sb, value);
        return sb.toString();
    }
    
    static String quote(final String str) {
        return '\"' + str + '\"';
    }
    
    static {
        logger = Logger.getLogger(CellFormatter.class.getName());
    }
}
