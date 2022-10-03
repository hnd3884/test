package javax.print.event;

public abstract class PrintJobAdapter implements PrintJobListener
{
    @Override
    public void printDataTransferCompleted(final PrintJobEvent printJobEvent) {
    }
    
    @Override
    public void printJobCompleted(final PrintJobEvent printJobEvent) {
    }
    
    @Override
    public void printJobFailed(final PrintJobEvent printJobEvent) {
    }
    
    @Override
    public void printJobCanceled(final PrintJobEvent printJobEvent) {
    }
    
    @Override
    public void printJobNoMoreEvents(final PrintJobEvent printJobEvent) {
    }
    
    @Override
    public void printJobRequiresAttention(final PrintJobEvent printJobEvent) {
    }
}
