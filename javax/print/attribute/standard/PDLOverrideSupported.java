package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.EnumSyntax;

public class PDLOverrideSupported extends EnumSyntax implements PrintServiceAttribute
{
    private static final long serialVersionUID = -4393264467928463934L;
    public static final PDLOverrideSupported NOT_ATTEMPTED;
    public static final PDLOverrideSupported ATTEMPTED;
    private static final String[] myStringTable;
    private static final PDLOverrideSupported[] myEnumValueTable;
    
    protected PDLOverrideSupported(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return PDLOverrideSupported.myStringTable.clone();
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return PDLOverrideSupported.myEnumValueTable.clone();
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PDLOverrideSupported.class;
    }
    
    @Override
    public final String getName() {
        return "pdl-override-supported";
    }
    
    static {
        NOT_ATTEMPTED = new PDLOverrideSupported(0);
        ATTEMPTED = new PDLOverrideSupported(1);
        myStringTable = new String[] { "not-attempted", "attempted" };
        myEnumValueTable = new PDLOverrideSupported[] { PDLOverrideSupported.NOT_ATTEMPTED, PDLOverrideSupported.ATTEMPTED };
    }
}
