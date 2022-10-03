package sun.net.www.http;

class KeepAliveCleanerEntry
{
    KeepAliveStream kas;
    HttpClient hc;
    
    public KeepAliveCleanerEntry(final KeepAliveStream kas, final HttpClient hc) {
        this.kas = kas;
        this.hc = hc;
    }
    
    protected KeepAliveStream getKeepAliveStream() {
        return this.kas;
    }
    
    protected HttpClient getHttpClient() {
        return this.hc;
    }
    
    protected void setQueuedForCleanup() {
        this.kas.queuedForCleanup = true;
    }
    
    protected boolean getQueuedForCleanup() {
        return this.kas.queuedForCleanup;
    }
}
