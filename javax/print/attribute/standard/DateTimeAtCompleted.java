package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.util.Date;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.DateTimeSyntax;

public final class DateTimeAtCompleted extends DateTimeSyntax implements PrintJobAttribute
{
    private static final long serialVersionUID = 6497399708058490000L;
    
    public DateTimeAtCompleted(final Date date) {
        super(date);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof DateTimeAtCompleted;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return DateTimeAtCompleted.class;
    }
    
    @Override
    public final String getName() {
        return "date-time-at-completed";
    }
}
