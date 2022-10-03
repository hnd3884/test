package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.ResolutionSyntax;

public final class PrinterResolution extends ResolutionSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = 13090306561090558L;
    
    public PrinterResolution(final int n, final int n2, final int n3) {
        super(n, n2, n3);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof PrinterResolution;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PrinterResolution.class;
    }
    
    @Override
    public final String getName() {
        return "printer-resolution";
    }
}
