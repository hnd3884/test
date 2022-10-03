package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.EnumSyntax;

public final class Fidelity extends EnumSyntax implements PrintJobAttribute, PrintRequestAttribute
{
    private static final long serialVersionUID = 6320827847329172308L;
    public static final Fidelity FIDELITY_TRUE;
    public static final Fidelity FIDELITY_FALSE;
    private static final String[] myStringTable;
    private static final Fidelity[] myEnumValueTable;
    
    protected Fidelity(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return Fidelity.myStringTable;
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return Fidelity.myEnumValueTable;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return Fidelity.class;
    }
    
    @Override
    public final String getName() {
        return "ipp-attribute-fidelity";
    }
    
    static {
        FIDELITY_TRUE = new Fidelity(0);
        FIDELITY_FALSE = new Fidelity(1);
        myStringTable = new String[] { "true", "false" };
        myEnumValueTable = new Fidelity[] { Fidelity.FIDELITY_TRUE, Fidelity.FIDELITY_FALSE };
    }
}
