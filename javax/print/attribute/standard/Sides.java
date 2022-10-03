package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;

public final class Sides extends EnumSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = -6890309414893262822L;
    public static final Sides ONE_SIDED;
    public static final Sides TWO_SIDED_LONG_EDGE;
    public static final Sides TWO_SIDED_SHORT_EDGE;
    public static final Sides DUPLEX;
    public static final Sides TUMBLE;
    private static final String[] myStringTable;
    private static final Sides[] myEnumValueTable;
    
    protected Sides(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return Sides.myStringTable;
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return Sides.myEnumValueTable;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return Sides.class;
    }
    
    @Override
    public final String getName() {
        return "sides";
    }
    
    static {
        ONE_SIDED = new Sides(0);
        TWO_SIDED_LONG_EDGE = new Sides(1);
        TWO_SIDED_SHORT_EDGE = new Sides(2);
        DUPLEX = Sides.TWO_SIDED_LONG_EDGE;
        TUMBLE = Sides.TWO_SIDED_SHORT_EDGE;
        myStringTable = new String[] { "one-sided", "two-sided-long-edge", "two-sided-short-edge" };
        myEnumValueTable = new Sides[] { Sides.ONE_SIDED, Sides.TWO_SIDED_LONG_EDGE, Sides.TWO_SIDED_SHORT_EDGE };
    }
}
