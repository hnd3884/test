package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.util.Date;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.DateTimeSyntax;

public final class DateTimeAtCreation extends DateTimeSyntax implements PrintJobAttribute
{
    private static final long serialVersionUID = -2923732231056647903L;
    
    public DateTimeAtCreation(final Date date) {
        super(date);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof DateTimeAtCreation;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return DateTimeAtCreation.class;
    }
    
    @Override
    public final String getName() {
        return "date-time-at-creation";
    }
}
