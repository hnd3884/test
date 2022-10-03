package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.util.Locale;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.TextSyntax;

public final class PrinterMessageFromOperator extends TextSyntax implements PrintServiceAttribute
{
    static final long serialVersionUID = -4486871203218629318L;
    
    public PrinterMessageFromOperator(final String s, final Locale locale) {
        super(s, locale);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof PrinterMessageFromOperator;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PrinterMessageFromOperator.class;
    }
    
    @Override
    public final String getName() {
        return "printer-message-from-operator";
    }
}
