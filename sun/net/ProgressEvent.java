package sun.net;

import java.net.URL;
import java.util.EventObject;

public class ProgressEvent extends EventObject
{
    private URL url;
    private String contentType;
    private String method;
    private long progress;
    private long expected;
    private ProgressSource.State state;
    
    public ProgressEvent(final ProgressSource progressSource, final URL url, final String method, final String contentType, final ProgressSource.State state, final long progress, final long expected) {
        super(progressSource);
        this.url = url;
        this.method = method;
        this.contentType = contentType;
        this.progress = progress;
        this.expected = expected;
        this.state = state;
    }
    
    public URL getURL() {
        return this.url;
    }
    
    public String getMethod() {
        return this.method;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public long getProgress() {
        return this.progress;
    }
    
    public long getExpected() {
        return this.expected;
    }
    
    public ProgressSource.State getState() {
        return this.state;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[url=" + this.url + ", method=" + this.method + ", state=" + this.state + ", content-type=" + this.contentType + ", progress=" + this.progress + ", expected=" + this.expected + "]";
    }
}
