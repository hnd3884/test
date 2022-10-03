package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;

public class JobStateReason extends EnumSyntax implements Attribute
{
    private static final long serialVersionUID = -8765894420449009168L;
    public static final JobStateReason JOB_INCOMING;
    public static final JobStateReason JOB_DATA_INSUFFICIENT;
    public static final JobStateReason DOCUMENT_ACCESS_ERROR;
    public static final JobStateReason SUBMISSION_INTERRUPTED;
    public static final JobStateReason JOB_OUTGOING;
    public static final JobStateReason JOB_HOLD_UNTIL_SPECIFIED;
    public static final JobStateReason RESOURCES_ARE_NOT_READY;
    public static final JobStateReason PRINTER_STOPPED_PARTLY;
    public static final JobStateReason PRINTER_STOPPED;
    public static final JobStateReason JOB_INTERPRETING;
    public static final JobStateReason JOB_QUEUED;
    public static final JobStateReason JOB_TRANSFORMING;
    public static final JobStateReason JOB_QUEUED_FOR_MARKER;
    public static final JobStateReason JOB_PRINTING;
    public static final JobStateReason JOB_CANCELED_BY_USER;
    public static final JobStateReason JOB_CANCELED_BY_OPERATOR;
    public static final JobStateReason JOB_CANCELED_AT_DEVICE;
    public static final JobStateReason ABORTED_BY_SYSTEM;
    public static final JobStateReason UNSUPPORTED_COMPRESSION;
    public static final JobStateReason COMPRESSION_ERROR;
    public static final JobStateReason UNSUPPORTED_DOCUMENT_FORMAT;
    public static final JobStateReason DOCUMENT_FORMAT_ERROR;
    public static final JobStateReason PROCESSING_TO_STOP_POINT;
    public static final JobStateReason SERVICE_OFF_LINE;
    public static final JobStateReason JOB_COMPLETED_SUCCESSFULLY;
    public static final JobStateReason JOB_COMPLETED_WITH_WARNINGS;
    public static final JobStateReason JOB_COMPLETED_WITH_ERRORS;
    public static final JobStateReason JOB_RESTARTABLE;
    public static final JobStateReason QUEUED_IN_DEVICE;
    private static final String[] myStringTable;
    private static final JobStateReason[] myEnumValueTable;
    
    protected JobStateReason(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return JobStateReason.myStringTable.clone();
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return JobStateReason.myEnumValueTable.clone();
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobStateReason.class;
    }
    
    @Override
    public final String getName() {
        return "job-state-reason";
    }
    
    static {
        JOB_INCOMING = new JobStateReason(0);
        JOB_DATA_INSUFFICIENT = new JobStateReason(1);
        DOCUMENT_ACCESS_ERROR = new JobStateReason(2);
        SUBMISSION_INTERRUPTED = new JobStateReason(3);
        JOB_OUTGOING = new JobStateReason(4);
        JOB_HOLD_UNTIL_SPECIFIED = new JobStateReason(5);
        RESOURCES_ARE_NOT_READY = new JobStateReason(6);
        PRINTER_STOPPED_PARTLY = new JobStateReason(7);
        PRINTER_STOPPED = new JobStateReason(8);
        JOB_INTERPRETING = new JobStateReason(9);
        JOB_QUEUED = new JobStateReason(10);
        JOB_TRANSFORMING = new JobStateReason(11);
        JOB_QUEUED_FOR_MARKER = new JobStateReason(12);
        JOB_PRINTING = new JobStateReason(13);
        JOB_CANCELED_BY_USER = new JobStateReason(14);
        JOB_CANCELED_BY_OPERATOR = new JobStateReason(15);
        JOB_CANCELED_AT_DEVICE = new JobStateReason(16);
        ABORTED_BY_SYSTEM = new JobStateReason(17);
        UNSUPPORTED_COMPRESSION = new JobStateReason(18);
        COMPRESSION_ERROR = new JobStateReason(19);
        UNSUPPORTED_DOCUMENT_FORMAT = new JobStateReason(20);
        DOCUMENT_FORMAT_ERROR = new JobStateReason(21);
        PROCESSING_TO_STOP_POINT = new JobStateReason(22);
        SERVICE_OFF_LINE = new JobStateReason(23);
        JOB_COMPLETED_SUCCESSFULLY = new JobStateReason(24);
        JOB_COMPLETED_WITH_WARNINGS = new JobStateReason(25);
        JOB_COMPLETED_WITH_ERRORS = new JobStateReason(26);
        JOB_RESTARTABLE = new JobStateReason(27);
        QUEUED_IN_DEVICE = new JobStateReason(28);
        myStringTable = new String[] { "job-incoming", "job-data-insufficient", "document-access-error", "submission-interrupted", "job-outgoing", "job-hold-until-specified", "resources-are-not-ready", "printer-stopped-partly", "printer-stopped", "job-interpreting", "job-queued", "job-transforming", "job-queued-for-marker", "job-printing", "job-canceled-by-user", "job-canceled-by-operator", "job-canceled-at-device", "aborted-by-system", "unsupported-compression", "compression-error", "unsupported-document-format", "document-format-error", "processing-to-stop-point", "service-off-line", "job-completed-successfully", "job-completed-with-warnings", "job-completed-with-errors", "job-restartable", "queued-in-device" };
        myEnumValueTable = new JobStateReason[] { JobStateReason.JOB_INCOMING, JobStateReason.JOB_DATA_INSUFFICIENT, JobStateReason.DOCUMENT_ACCESS_ERROR, JobStateReason.SUBMISSION_INTERRUPTED, JobStateReason.JOB_OUTGOING, JobStateReason.JOB_HOLD_UNTIL_SPECIFIED, JobStateReason.RESOURCES_ARE_NOT_READY, JobStateReason.PRINTER_STOPPED_PARTLY, JobStateReason.PRINTER_STOPPED, JobStateReason.JOB_INTERPRETING, JobStateReason.JOB_QUEUED, JobStateReason.JOB_TRANSFORMING, JobStateReason.JOB_QUEUED_FOR_MARKER, JobStateReason.JOB_PRINTING, JobStateReason.JOB_CANCELED_BY_USER, JobStateReason.JOB_CANCELED_BY_OPERATOR, JobStateReason.JOB_CANCELED_AT_DEVICE, JobStateReason.ABORTED_BY_SYSTEM, JobStateReason.UNSUPPORTED_COMPRESSION, JobStateReason.COMPRESSION_ERROR, JobStateReason.UNSUPPORTED_DOCUMENT_FORMAT, JobStateReason.DOCUMENT_FORMAT_ERROR, JobStateReason.PROCESSING_TO_STOP_POINT, JobStateReason.SERVICE_OFF_LINE, JobStateReason.JOB_COMPLETED_SUCCESSFULLY, JobStateReason.JOB_COMPLETED_WITH_WARNINGS, JobStateReason.JOB_COMPLETED_WITH_ERRORS, JobStateReason.JOB_RESTARTABLE, JobStateReason.QUEUED_IN_DEVICE };
    }
}
