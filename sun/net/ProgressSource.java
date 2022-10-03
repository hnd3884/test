package sun.net;

import java.net.URL;

public class ProgressSource
{
    private URL url;
    private String method;
    private String contentType;
    private long progress;
    private long lastProgress;
    private long expected;
    private State state;
    private boolean connected;
    private int threshold;
    private ProgressMonitor progressMonitor;
    
    public ProgressSource(final URL url, final String s) {
        this(url, s, -1L);
    }
    
    public ProgressSource(final URL url, final String method, final long expected) {
        this.progress = 0L;
        this.lastProgress = 0L;
        this.expected = -1L;
        this.connected = false;
        this.threshold = 8192;
        this.url = url;
        this.method = method;
        this.contentType = "content/unknown";
        this.progress = 0L;
        this.lastProgress = 0L;
        this.expected = expected;
        this.state = State.NEW;
        this.progressMonitor = ProgressMonitor.getDefault();
        this.threshold = this.progressMonitor.getProgressUpdateThreshold();
    }
    
    public boolean connected() {
        if (!this.connected) {
            this.connected = true;
            this.state = State.CONNECTED;
            return false;
        }
        return true;
    }
    
    public void close() {
        this.state = State.DELETE;
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
    
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }
    
    public long getProgress() {
        return this.progress;
    }
    
    public long getExpected() {
        return this.expected;
    }
    
    public State getState() {
        return this.state;
    }
    
    public void beginTracking() {
        this.progressMonitor.registerSource(this);
    }
    
    public void finishTracking() {
        this.progressMonitor.unregisterSource(this);
    }
    
    public void updateProgress(final long progress, final long expected) {
        this.lastProgress = this.progress;
        this.progress = progress;
        this.expected = expected;
        if (!this.connected()) {
            this.state = State.CONNECTED;
        }
        else {
            this.state = State.UPDATE;
        }
        if (this.lastProgress / this.threshold != this.progress / this.threshold) {
            this.progressMonitor.updateProgress(this);
        }
        if (this.expected != -1L && this.progress >= this.expected && this.progress != 0L) {
            this.close();
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[url=" + this.url + ", method=" + this.method + ", state=" + this.state + ", content-type=" + this.contentType + ", progress=" + this.progress + ", expected=" + this.expected + "]";
    }
    
    public enum State
    {
        NEW, 
        CONNECTED, 
        UPDATE, 
        DELETE;
    }
}
