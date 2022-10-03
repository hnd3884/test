package javax.servlet;

public interface AsyncContext
{
    public static final String ASYNC_REQUEST_URI = "javax.servlet.async.request_uri";
    public static final String ASYNC_CONTEXT_PATH = "javax.servlet.async.context_path";
    public static final String ASYNC_PATH_INFO = "javax.servlet.async.path_info";
    public static final String ASYNC_SERVLET_PATH = "javax.servlet.async.servlet_path";
    public static final String ASYNC_QUERY_STRING = "javax.servlet.async.query_string";
    
    ServletRequest getRequest();
    
    ServletResponse getResponse();
    
    boolean hasOriginalRequestAndResponse();
    
    void dispatch();
    
    void dispatch(final String p0);
    
    void dispatch(final ServletContext p0, final String p1);
    
    void complete();
    
    void start(final Runnable p0);
    
    void addListener(final AsyncListener p0);
    
    void addListener(final AsyncListener p0, final ServletRequest p1, final ServletResponse p2);
    
     <T extends AsyncListener> T createListener(final Class<T> p0) throws ServletException;
    
    void setTimeout(final long p0);
    
    long getTimeout();
}
