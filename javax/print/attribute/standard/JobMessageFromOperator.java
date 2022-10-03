package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.util.Locale;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.TextSyntax;

public final class JobMessageFromOperator extends TextSyntax implements PrintJobAttribute
{
    private static final long serialVersionUID = -4620751846003142047L;
    
    public JobMessageFromOperator(final String s, final Locale locale) {
        super(s, locale);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof JobMessageFromOperator;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobMessageFromOperator.class;
    }
    
    @Override
    public final String getName() {
        return "job-message-from-operator";
    }
}
