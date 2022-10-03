package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.EnumSyntax;

public class JobSheets extends EnumSyntax implements PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = -4735258056132519759L;
    public static final JobSheets NONE;
    public static final JobSheets STANDARD;
    private static final String[] myStringTable;
    private static final JobSheets[] myEnumValueTable;
    
    protected JobSheets(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return JobSheets.myStringTable.clone();
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return JobSheets.myEnumValueTable.clone();
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobSheets.class;
    }
    
    @Override
    public final String getName() {
        return "job-sheets";
    }
    
    static {
        NONE = new JobSheets(0);
        STANDARD = new JobSheets(1);
        myStringTable = new String[] { "none", "standard" };
        myEnumValueTable = new JobSheets[] { JobSheets.NONE, JobSheets.STANDARD };
    }
}
