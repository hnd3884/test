package javax.print.event;

public interface PrintJobListener
{
    void printDataTransferCompleted(final PrintJobEvent p0);
    
    void printJobCompleted(final PrintJobEvent p0);
    
    void printJobFailed(final PrintJobEvent p0);
    
    void printJobCanceled(final PrintJobEvent p0);
    
    void printJobNoMoreEvents(final PrintJobEvent p0);
    
    void printJobRequiresAttention(final PrintJobEvent p0);
}
