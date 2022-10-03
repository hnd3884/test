package org.apache.lucene.queryparser.flexible.standard.config;

import java.text.ParsePosition;
import java.util.Date;
import java.text.FieldPosition;
import java.text.DateFormat;
import java.text.NumberFormat;

public class NumberDateFormat extends NumberFormat
{
    private static final long serialVersionUID = 964823936071308283L;
    private final DateFormat dateFormat;
    
    public NumberDateFormat(final DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    @Override
    public StringBuffer format(final double number, final StringBuffer toAppendTo, final FieldPosition pos) {
        return this.dateFormat.format(new Date((long)number), toAppendTo, pos);
    }
    
    @Override
    public StringBuffer format(final long number, final StringBuffer toAppendTo, final FieldPosition pos) {
        return this.dateFormat.format(new Date(number), toAppendTo, pos);
    }
    
    @Override
    public Number parse(final String source, final ParsePosition parsePosition) {
        final Date date = this.dateFormat.parse(source, parsePosition);
        return (date == null) ? null : Long.valueOf(date.getTime());
    }
    
    @Override
    public StringBuffer format(final Object number, final StringBuffer toAppendTo, final FieldPosition pos) {
        return this.dateFormat.format(number, toAppendTo, pos);
    }
}
