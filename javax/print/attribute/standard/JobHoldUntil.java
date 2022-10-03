package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.util.Date;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.DateTimeSyntax;

public final class JobHoldUntil extends DateTimeSyntax implements PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = -1664471048860415024L;
    
    public JobHoldUntil(final Date date) {
        super(date);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof JobHoldUntil;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobHoldUntil.class;
    }
    
    @Override
    public final String getName() {
        return "job-hold-until";
    }
}
