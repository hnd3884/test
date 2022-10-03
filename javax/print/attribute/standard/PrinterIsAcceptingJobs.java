package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.EnumSyntax;

public final class PrinterIsAcceptingJobs extends EnumSyntax implements PrintServiceAttribute
{
    private static final long serialVersionUID = -5052010680537678061L;
    public static final PrinterIsAcceptingJobs NOT_ACCEPTING_JOBS;
    public static final PrinterIsAcceptingJobs ACCEPTING_JOBS;
    private static final String[] myStringTable;
    private static final PrinterIsAcceptingJobs[] myEnumValueTable;
    
    protected PrinterIsAcceptingJobs(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return PrinterIsAcceptingJobs.myStringTable;
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return PrinterIsAcceptingJobs.myEnumValueTable;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PrinterIsAcceptingJobs.class;
    }
    
    @Override
    public final String getName() {
        return "printer-is-accepting-jobs";
    }
    
    static {
        NOT_ACCEPTING_JOBS = new PrinterIsAcceptingJobs(0);
        ACCEPTING_JOBS = new PrinterIsAcceptingJobs(1);
        myStringTable = new String[] { "not-accepting-jobs", "accepting-jobs" };
        myEnumValueTable = new PrinterIsAcceptingJobs[] { PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS, PrinterIsAcceptingJobs.ACCEPTING_JOBS };
    }
}
