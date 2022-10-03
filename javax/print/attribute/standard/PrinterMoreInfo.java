package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.net.URI;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.URISyntax;

public final class PrinterMoreInfo extends URISyntax implements PrintServiceAttribute
{
    private static final long serialVersionUID = 4555850007675338574L;
    
    public PrinterMoreInfo(final URI uri) {
        super(uri);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof PrinterMoreInfo;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PrinterMoreInfo.class;
    }
    
    @Override
    public final String getName() {
        return "printer-more-info";
    }
}
