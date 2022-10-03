package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.util.Locale;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.TextSyntax;

public final class PrinterMakeAndModel extends TextSyntax implements PrintServiceAttribute
{
    private static final long serialVersionUID = 4580461489499351411L;
    
    public PrinterMakeAndModel(final String s, final Locale locale) {
        super(s, locale);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof PrinterMakeAndModel;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PrinterMakeAndModel.class;
    }
    
    @Override
    public final String getName() {
        return "printer-make-and-model";
    }
}
