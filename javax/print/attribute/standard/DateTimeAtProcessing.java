package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.util.Date;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.DateTimeSyntax;

public final class DateTimeAtProcessing extends DateTimeSyntax implements PrintJobAttribute
{
    private static final long serialVersionUID = -3710068197278263244L;
    
    public DateTimeAtProcessing(final Date date) {
        super(date);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof DateTimeAtProcessing;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return DateTimeAtProcessing.class;
    }
    
    @Override
    public final String getName() {
        return "date-time-at-processing";
    }
}
