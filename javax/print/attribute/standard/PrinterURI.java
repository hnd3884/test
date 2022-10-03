package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.net.URI;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.URISyntax;

public final class PrinterURI extends URISyntax implements PrintServiceAttribute
{
    private static final long serialVersionUID = 7923912792485606497L;
    
    public PrinterURI(final URI uri) {
        super(uri);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof PrinterURI;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PrinterURI.class;
    }
    
    @Override
    public final String getName() {
        return "printer-uri";
    }
}
