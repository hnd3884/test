package org.apache.http.impl.client;

import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class LaxRedirectStrategy extends DefaultRedirectStrategy
{
    public static final LaxRedirectStrategy INSTANCE;
    
    public LaxRedirectStrategy() {
        super(new String[] { "GET", "POST", "HEAD", "DELETE" });
    }
    
    static {
        INSTANCE = new LaxRedirectStrategy();
    }
}
