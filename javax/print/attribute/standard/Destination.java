package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.net.URI;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.URISyntax;

public final class Destination extends URISyntax implements PrintJobAttribute, PrintRequestAttribute
{
    private static final long serialVersionUID = 6776739171700415321L;
    
    public Destination(final URI uri) {
        super(uri);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof Destination;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return Destination.class;
    }
    
    @Override
    public final String getName() {
        return "spool-data-destination";
    }
}
