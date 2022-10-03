package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.util.Locale;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.TextSyntax;

public final class OutputDeviceAssigned extends TextSyntax implements PrintJobAttribute
{
    private static final long serialVersionUID = 5486733778854271081L;
    
    public OutputDeviceAssigned(final String s, final Locale locale) {
        super(s, locale);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof OutputDeviceAssigned;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return OutputDeviceAssigned.class;
    }
    
    @Override
    public final String getName() {
        return "output-device-assigned";
    }
}
