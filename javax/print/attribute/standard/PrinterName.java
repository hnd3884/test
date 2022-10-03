package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.util.Locale;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.TextSyntax;

public final class PrinterName extends TextSyntax implements PrintServiceAttribute
{
    private static final long serialVersionUID = 299740639137803127L;
    
    public PrinterName(final String s, final Locale locale) {
        super(s, locale);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof PrinterName;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PrinterName.class;
    }
    
    @Override
    public final String getName() {
        return "printer-name";
    }
}
