package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.EnumSyntax;

public class JobState extends EnumSyntax implements PrintJobAttribute
{
    private static final long serialVersionUID = 400465010094018920L;
    public static final JobState UNKNOWN;
    public static final JobState PENDING;
    public static final JobState PENDING_HELD;
    public static final JobState PROCESSING;
    public static final JobState PROCESSING_STOPPED;
    public static final JobState CANCELED;
    public static final JobState ABORTED;
    public static final JobState COMPLETED;
    private static final String[] myStringTable;
    private static final JobState[] myEnumValueTable;
    
    protected JobState(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return JobState.myStringTable;
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return JobState.myEnumValueTable;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobState.class;
    }
    
    @Override
    public final String getName() {
        return "job-state";
    }
    
    static {
        UNKNOWN = new JobState(0);
        PENDING = new JobState(3);
        PENDING_HELD = new JobState(4);
        PROCESSING = new JobState(5);
        PROCESSING_STOPPED = new JobState(6);
        CANCELED = new JobState(7);
        ABORTED = new JobState(8);
        COMPLETED = new JobState(9);
        myStringTable = new String[] { "unknown", null, null, "pending", "pending-held", "processing", "processing-stopped", "canceled", "aborted", "completed" };
        myEnumValueTable = new JobState[] { JobState.UNKNOWN, null, null, JobState.PENDING, JobState.PENDING_HELD, JobState.PROCESSING, JobState.PROCESSING_STOPPED, JobState.CANCELED, JobState.ABORTED, JobState.COMPLETED };
    }
}
