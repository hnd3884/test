package HTTPClient;

public final class Request implements RoRequest, Cloneable
{
    private static final NVPair[] empty;
    private HTTPConnection connection;
    private String method;
    private String req_uri;
    private NVPair[] headers;
    private byte[] data;
    private HttpOutputStream stream;
    private boolean allow_ui;
    long delay_entity;
    int num_retries;
    boolean dont_pipeline;
    boolean aborted;
    boolean internal_subrequest;
    
    public Request(final HTTPConnection con, final String method, final String req_uri, final NVPair[] headers, final byte[] data, final HttpOutputStream stream, final boolean allow_ui) {
        this.dont_pipeline = false;
        this.aborted = false;
        this.internal_subrequest = false;
        this.connection = con;
        this.method = method;
        this.setRequestURI(req_uri);
        this.setHeaders(headers);
        this.data = data;
        this.stream = stream;
        this.allow_ui = allow_ui;
    }
    
    public HTTPConnection getConnection() {
        return this.connection;
    }
    
    public void setConnection(final HTTPConnection con) {
        this.connection = con;
    }
    
    public String getMethod() {
        return this.method;
    }
    
    public void setMethod(final String method) {
        this.method = method;
    }
    
    public String getRequestURI() {
        return this.req_uri;
    }
    
    public void setRequestURI(String req_uri) {
        if (req_uri != null && req_uri.trim().length() > 0) {
            req_uri = req_uri.trim();
            if (req_uri.charAt(0) != '/' && !req_uri.equals("*") && !this.method.equals("CONNECT") && !isAbsolute(req_uri)) {
                req_uri = "/" + req_uri;
            }
            this.req_uri = req_uri;
        }
        else {
            this.req_uri = "/";
        }
    }
    
    private static final boolean isAbsolute(final String uri) {
        char ch = '\0';
        for (int pos = 0, len = uri.length(); pos < len && (ch = uri.charAt(pos)) != ':' && ch != '/' && ch != '?' && ch != '#'; ++pos) {}
        return ch == ':';
    }
    
    public NVPair[] getHeaders() {
        return this.headers;
    }
    
    public void setHeaders(final NVPair[] headers) {
        if (headers != null) {
            this.headers = headers;
        }
        else {
            this.headers = Request.empty;
        }
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public void setData(final byte[] data) {
        this.data = data;
    }
    
    public HttpOutputStream getStream() {
        return this.stream;
    }
    
    public void setStream(final HttpOutputStream stream) {
        this.stream = stream;
    }
    
    public boolean allowUI() {
        return this.allow_ui;
    }
    
    public void setAllowUI(final boolean allow_ui) {
        this.allow_ui = allow_ui;
    }
    
    public Object clone() {
        Request cl;
        try {
            cl = (Request)super.clone();
        }
        catch (final CloneNotSupportedException cnse) {
            throw new InternalError(cnse.toString());
        }
        cl.headers = new NVPair[this.headers.length];
        System.arraycopy(this.headers, 0, cl.headers, 0, this.headers.length);
        return cl;
    }
    
    public void copyFrom(final Request other) {
        this.connection = other.connection;
        this.method = other.method;
        this.req_uri = other.req_uri;
        this.headers = other.headers;
        this.data = other.data;
        this.stream = other.stream;
        this.allow_ui = other.allow_ui;
        this.delay_entity = other.delay_entity;
        this.num_retries = other.num_retries;
        this.dont_pipeline = other.dont_pipeline;
        this.aborted = other.aborted;
        this.internal_subrequest = other.internal_subrequest;
    }
    
    public String toString() {
        return String.valueOf(this.getClass().getName()) + ": " + this.method + " " + this.req_uri;
    }
    
    static {
        empty = new NVPair[0];
    }
}
