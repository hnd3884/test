package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.net.URI;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.URISyntax;

public final class PrinterMoreInfoManufacturer extends URISyntax implements PrintServiceAttribute
{
    private static final long serialVersionUID = 3323271346485076608L;
    
    public PrinterMoreInfoManufacturer(final URI uri) {
        super(uri);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof PrinterMoreInfoManufacturer;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PrinterMoreInfoManufacturer.class;
    }
    
    @Override
    public final String getName() {
        return "printer-more-info-manufacturer";
    }
}
