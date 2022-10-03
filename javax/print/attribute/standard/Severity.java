package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;

public final class Severity extends EnumSyntax implements Attribute
{
    private static final long serialVersionUID = 8781881462717925380L;
    public static final Severity REPORT;
    public static final Severity WARNING;
    public static final Severity ERROR;
    private static final String[] myStringTable;
    private static final Severity[] myEnumValueTable;
    
    protected Severity(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return Severity.myStringTable;
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return Severity.myEnumValueTable;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return Severity.class;
    }
    
    @Override
    public final String getName() {
        return "severity";
    }
    
    static {
        REPORT = new Severity(0);
        WARNING = new Severity(1);
        ERROR = new Severity(2);
        myStringTable = new String[] { "report", "warning", "error" };
        myEnumValueTable = new Severity[] { Severity.REPORT, Severity.WARNING, Severity.ERROR };
    }
}
