package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;

public final class OrientationRequested extends EnumSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = -4447437289862822276L;
    public static final OrientationRequested PORTRAIT;
    public static final OrientationRequested LANDSCAPE;
    public static final OrientationRequested REVERSE_LANDSCAPE;
    public static final OrientationRequested REVERSE_PORTRAIT;
    private static final String[] myStringTable;
    private static final OrientationRequested[] myEnumValueTable;
    
    protected OrientationRequested(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return OrientationRequested.myStringTable;
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return OrientationRequested.myEnumValueTable;
    }
    
    @Override
    protected int getOffset() {
        return 3;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return OrientationRequested.class;
    }
    
    @Override
    public final String getName() {
        return "orientation-requested";
    }
    
    static {
        PORTRAIT = new OrientationRequested(3);
        LANDSCAPE = new OrientationRequested(4);
        REVERSE_LANDSCAPE = new OrientationRequested(5);
        REVERSE_PORTRAIT = new OrientationRequested(6);
        myStringTable = new String[] { "portrait", "landscape", "reverse-landscape", "reverse-portrait" };
        myEnumValueTable = new OrientationRequested[] { OrientationRequested.PORTRAIT, OrientationRequested.LANDSCAPE, OrientationRequested.REVERSE_LANDSCAPE, OrientationRequested.REVERSE_PORTRAIT };
    }
}
