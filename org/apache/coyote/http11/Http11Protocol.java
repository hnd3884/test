package org.apache.coyote.http11;

import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

@Deprecated
public class Http11Protocol extends Http11NioProtocol
{
    private static final Log log;
    private static final StringManager sm;
    
    public Http11Protocol() {
        Http11Protocol.log.warn((Object)Http11Protocol.sm.getString("http11protocol.noBio"));
    }
    
    static {
        log = LogFactory.getLog((Class)Http11Protocol.class);
        sm = StringManager.getManager((Class)Http11Protocol.class);
    }
}
