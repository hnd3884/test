package sun.security.jgss;

import sun.net.www.protocol.http.HttpCallerInfo;

public class HttpCaller extends GSSCaller
{
    private final HttpCallerInfo hci;
    
    public HttpCaller(final HttpCallerInfo hci) {
        super("HTTP_CLIENT");
        this.hci = hci;
    }
    
    public HttpCallerInfo info() {
        return this.hci;
    }
}
