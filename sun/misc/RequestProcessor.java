package sun.misc;

public class RequestProcessor implements Runnable
{
    private static Queue<Request> requestQueue;
    private static Thread dispatcher;
    
    public static void postRequest(final Request request) {
        lazyInitialize();
        RequestProcessor.requestQueue.enqueue(request);
    }
    
    @Override
    public void run() {
        lazyInitialize();
    Label_0003_Outer:
        while (true) {
            while (true) {
                try {
                    while (true) {
                        final Request request = RequestProcessor.requestQueue.dequeue();
                        try {
                            request.execute();
                        }
                        catch (final Throwable t) {}
                    }
                }
                catch (final InterruptedException ex) {
                    continue Label_0003_Outer;
                }
                continue;
            }
        }
    }
    
    public static synchronized void startProcessing() {
        if (RequestProcessor.dispatcher == null) {
            (RequestProcessor.dispatcher = new Thread(new RequestProcessor(), "Request Processor")).setPriority(7);
            RequestProcessor.dispatcher.start();
        }
    }
    
    private static synchronized void lazyInitialize() {
        if (RequestProcessor.requestQueue == null) {
            RequestProcessor.requestQueue = new Queue<Request>();
        }
    }
}
