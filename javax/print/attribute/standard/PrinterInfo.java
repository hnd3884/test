package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.util.Locale;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.TextSyntax;

public final class PrinterInfo extends TextSyntax implements PrintServiceAttribute
{
    private static final long serialVersionUID = 7765280618777599727L;
    
    public PrinterInfo(final String s, final Locale locale) {
        super(s, locale);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof PrinterInfo;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PrinterInfo.class;
    }
    
    @Override
    public final String getName() {
        return "printer-info";
    }
}
