package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;

public final class Chromaticity extends EnumSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = 4660543931355214012L;
    public static final Chromaticity MONOCHROME;
    public static final Chromaticity COLOR;
    private static final String[] myStringTable;
    private static final Chromaticity[] myEnumValueTable;
    
    protected Chromaticity(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return Chromaticity.myStringTable;
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return Chromaticity.myEnumValueTable;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return Chromaticity.class;
    }
    
    @Override
    public final String getName() {
        return "chromaticity";
    }
    
    static {
        MONOCHROME = new Chromaticity(0);
        COLOR = new Chromaticity(1);
        myStringTable = new String[] { "monochrome", "color" };
        myEnumValueTable = new Chromaticity[] { Chromaticity.MONOCHROME, Chromaticity.COLOR };
    }
}
