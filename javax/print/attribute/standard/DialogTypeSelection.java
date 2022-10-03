package javax.print.attribute.standard;

import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.EnumSyntax;

public final class DialogTypeSelection extends EnumSyntax implements PrintRequestAttribute
{
    private static final long serialVersionUID = 7518682952133256029L;
    public static final DialogTypeSelection NATIVE;
    public static final DialogTypeSelection COMMON;
    private static final String[] myStringTable;
    private static final DialogTypeSelection[] myEnumValueTable;
    
    protected DialogTypeSelection(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return DialogTypeSelection.myStringTable;
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return DialogTypeSelection.myEnumValueTable;
    }
    
    @Override
    public final Class getCategory() {
        return DialogTypeSelection.class;
    }
    
    @Override
    public final String getName() {
        return "dialog-type-selection";
    }
    
    static {
        NATIVE = new DialogTypeSelection(0);
        COMMON = new DialogTypeSelection(1);
        myStringTable = new String[] { "native", "common" };
        myEnumValueTable = new DialogTypeSelection[] { DialogTypeSelection.NATIVE, DialogTypeSelection.COMMON };
    }
}
