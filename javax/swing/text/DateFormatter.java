package javax.swing.text;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Calendar;
import java.text.Format;
import java.text.DateFormat;

public class DateFormatter extends InternationalFormatter
{
    public DateFormatter() {
        this(DateFormat.getDateInstance());
    }
    
    public DateFormatter(final DateFormat format) {
        super(format);
        this.setFormat(format);
    }
    
    public void setFormat(final DateFormat format) {
        super.setFormat(format);
    }
    
    private Calendar getCalendar() {
        final Format format = this.getFormat();
        if (format instanceof DateFormat) {
            return ((DateFormat)format).getCalendar();
        }
        return Calendar.getInstance();
    }
    
    @Override
    boolean getSupportsIncrement() {
        return true;
    }
    
    @Override
    Object getAdjustField(final int n, final Map map) {
        for (final Object next : map.keySet()) {
            if (next instanceof DateFormat.Field && (next == DateFormat.Field.HOUR1 || ((DateFormat.Field)next).getCalendarField() != -1)) {
                return next;
            }
        }
        return null;
    }
    
    @Override
    Object adjustValue(final Object o, final Map map, Object hour0, final int n) throws BadLocationException, ParseException {
        if (hour0 != null) {
            if (hour0 == DateFormat.Field.HOUR1) {
                hour0 = DateFormat.Field.HOUR0;
            }
            final int calendarField = ((DateFormat.Field)hour0).getCalendarField();
            final Calendar calendar = this.getCalendar();
            if (calendar != null) {
                calendar.setTime((Date)o);
                calendar.get(calendarField);
                Date time;
                try {
                    calendar.add(calendarField, n);
                    time = calendar.getTime();
                }
                catch (final Throwable t) {
                    time = null;
                }
                return time;
            }
        }
        return null;
    }
}
