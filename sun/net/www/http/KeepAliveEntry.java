package sun.net.www.http;

class KeepAliveEntry
{
    HttpClient hc;
    long idleStartTime;
    
    KeepAliveEntry(final HttpClient hc, final long idleStartTime) {
        this.hc = hc;
        this.idleStartTime = idleStartTime;
    }
}
