package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.IntegerSyntax;

public final class PagesPerMinuteColor extends IntegerSyntax implements PrintServiceAttribute
{
    static final long serialVersionUID = 1684993151687470944L;
    
    public PagesPerMinuteColor(final int n) {
        super(n, 0, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof PagesPerMinuteColor;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PagesPerMinuteColor.class;
    }
    
    @Override
    public final String getName() {
        return "pages-per-minute-color";
    }
}
