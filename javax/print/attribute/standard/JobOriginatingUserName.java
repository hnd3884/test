package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.util.Locale;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.TextSyntax;

public final class JobOriginatingUserName extends TextSyntax implements PrintJobAttribute
{
    private static final long serialVersionUID = -8052537926362933477L;
    
    public JobOriginatingUserName(final String s, final Locale locale) {
        super(s, locale);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof JobOriginatingUserName;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobOriginatingUserName.class;
    }
    
    @Override
    public final String getName() {
        return "job-originating-user-name";
    }
}
