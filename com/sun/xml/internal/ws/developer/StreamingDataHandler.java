package com.sun.xml.internal.ws.developer;

import javax.activation.DataSource;
import java.net.URL;

public abstract class StreamingDataHandler extends com.sun.xml.internal.org.jvnet.staxex.StreamingDataHandler
{
    private String hrefCid;
    
    public StreamingDataHandler(final Object o, final String s) {
        super(o, s);
    }
    
    public StreamingDataHandler(final URL url) {
        super(url);
    }
    
    public StreamingDataHandler(final DataSource dataSource) {
        super(dataSource);
    }
    
    public String getHrefCid() {
        return this.hrefCid;
    }
    
    public void setHrefCid(final String cid) {
        this.hrefCid = cid;
    }
}
