package org.apache.tomcat.util.http;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.text.SimpleDateFormat;
import java.util.Queue;
import java.util.TimeZone;
import java.util.Locale;

public class ConcurrentDateFormat
{
    private final String format;
    private final Locale locale;
    private final TimeZone timezone;
    private final Queue<SimpleDateFormat> queue;
    
    public ConcurrentDateFormat(final String format, final Locale locale, final TimeZone timezone) {
        this.queue = new ConcurrentLinkedQueue<SimpleDateFormat>();
        this.format = format;
        this.locale = locale;
        this.timezone = timezone;
        final SimpleDateFormat initial = this.createInstance();
        this.queue.add(initial);
    }
    
    public String format(final Date date) {
        SimpleDateFormat sdf = this.queue.poll();
        if (sdf == null) {
            sdf = this.createInstance();
        }
        final String result = sdf.format(date);
        this.queue.add(sdf);
        return result;
    }
    
    public Date parse(final String source) throws ParseException {
        SimpleDateFormat sdf = this.queue.poll();
        if (sdf == null) {
            sdf = this.createInstance();
        }
        final Date result = sdf.parse(source);
        sdf.setTimeZone(this.timezone);
        this.queue.add(sdf);
        return result;
    }
    
    private SimpleDateFormat createInstance() {
        final SimpleDateFormat sdf = new SimpleDateFormat(this.format, this.locale);
        sdf.setTimeZone(this.timezone);
        return sdf;
    }
}
