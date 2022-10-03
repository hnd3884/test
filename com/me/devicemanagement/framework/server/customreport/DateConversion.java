package com.me.devicemanagement.framework.server.customreport;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DateConversion implements ColumnInputCustomizer
{
    static String className;
    static Logger out;
    
    @Override
    public Object customize(final Object value) {
        long dateInMillis = 0L;
        try {
            final String val = (String)value;
            DateConversion.out.log(Level.INFO, "Date value in string " + val + " Leng " + val.length());
            final DateFormat format1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            final Date date = format1.parse(val.trim());
            dateInMillis = date.getTime();
            DateConversion.out.log(Level.INFO, "Date in milli seconds " + dateInMillis);
        }
        catch (final Exception e) {
            DateConversion.out.log(Level.WARNING, "Exception while Date Conversion", e);
        }
        return dateInMillis;
    }
    
    static {
        DateConversion.className = DateConversion.class.getName();
        DateConversion.out = Logger.getLogger(DateConversion.className);
    }
}
