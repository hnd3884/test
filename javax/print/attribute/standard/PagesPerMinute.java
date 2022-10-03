package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.IntegerSyntax;

public final class PagesPerMinute extends IntegerSyntax implements PrintServiceAttribute
{
    private static final long serialVersionUID = -6366403993072862015L;
    
    public PagesPerMinute(final int n) {
        super(n, 0, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof PagesPerMinute;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PagesPerMinute.class;
    }
    
    @Override
    public final String getName() {
        return "pages-per-minute";
    }
}
