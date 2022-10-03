package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;

public abstract class Media extends EnumSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = -2823970704630722439L;
    
    protected Media(final int n) {
        super(n);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof Media && o.getClass() == this.getClass() && ((Media)o).getValue() == this.getValue();
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return Media.class;
    }
    
    @Override
    public final String getName() {
        return "media";
    }
}
