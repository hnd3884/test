package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.util.Locale;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.TextSyntax;

public final class PrinterLocation extends TextSyntax implements PrintServiceAttribute
{
    private static final long serialVersionUID = -1598610039865566337L;
    
    public PrinterLocation(final String s, final Locale locale) {
        super(s, locale);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof PrinterLocation;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PrinterLocation.class;
    }
    
    @Override
    public final String getName() {
        return "printer-location";
    }
}
