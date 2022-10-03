package org.apache.coyote.ajp;

import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

@Deprecated
public class AjpProtocol extends AjpNioProtocol
{
    private static final Log log;
    private static final StringManager sm;
    
    public AjpProtocol() {
        AjpProtocol.log.warn((Object)AjpProtocol.sm.getString("ajpprotocol.noBio"));
    }
    
    static {
        log = LogFactory.getLog((Class)AjpProtocol.class);
        sm = StringManager.getManager((Class)AjpProtocol.class);
    }
}
