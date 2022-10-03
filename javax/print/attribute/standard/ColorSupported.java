package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.EnumSyntax;

public final class ColorSupported extends EnumSyntax implements PrintServiceAttribute
{
    private static final long serialVersionUID = -2700555589688535545L;
    public static final ColorSupported NOT_SUPPORTED;
    public static final ColorSupported SUPPORTED;
    private static final String[] myStringTable;
    private static final ColorSupported[] myEnumValueTable;
    
    protected ColorSupported(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return ColorSupported.myStringTable;
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return ColorSupported.myEnumValueTable;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return ColorSupported.class;
    }
    
    @Override
    public final String getName() {
        return "color-supported";
    }
    
    static {
        NOT_SUPPORTED = new ColorSupported(0);
        SUPPORTED = new ColorSupported(1);
        myStringTable = new String[] { "not-supported", "supported" };
        myEnumValueTable = new ColorSupported[] { ColorSupported.NOT_SUPPORTED, ColorSupported.SUPPORTED };
    }
}
