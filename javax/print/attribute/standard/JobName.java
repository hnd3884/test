package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.util.Locale;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.TextSyntax;

public final class JobName extends TextSyntax implements PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = 4660359192078689545L;
    
    public JobName(final String s, final Locale locale) {
        super(s, locale);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof JobName;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobName.class;
    }
    
    @Override
    public final String getName() {
        return "job-name";
    }
}
