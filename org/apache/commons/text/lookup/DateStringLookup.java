package org.apache.commons.text.lookup;

import java.util.Date;
import org.apache.commons.lang3.time.FastDateFormat;

final class DateStringLookup extends AbstractStringLookup
{
    static final DateStringLookup INSTANCE;
    
    private DateStringLookup() {
    }
    
    private String formatDate(final long date, final String format) {
        FastDateFormat dateFormat = null;
        if (format != null) {
            try {
                dateFormat = FastDateFormat.getInstance(format);
            }
            catch (final Exception ex) {
                throw IllegalArgumentExceptions.format(ex, "Invalid date format: [%s]", format);
            }
        }
        if (dateFormat == null) {
            dateFormat = FastDateFormat.getInstance();
        }
        return dateFormat.format(new Date(date));
    }
    
    @Override
    public String lookup(final String key) {
        return this.formatDate(System.currentTimeMillis(), key);
    }
    
    static {
        INSTANCE = new DateStringLookup();
    }
}
