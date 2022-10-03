package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.util.Locale;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.TextSyntax;

public final class RequestingUserName extends TextSyntax implements PrintRequestAttribute
{
    private static final long serialVersionUID = -2683049894310331454L;
    
    public RequestingUserName(final String s, final Locale locale) {
        super(s, locale);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof RequestingUserName;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return RequestingUserName.class;
    }
    
    @Override
    public final String getName() {
        return "requesting-user-name";
    }
}
