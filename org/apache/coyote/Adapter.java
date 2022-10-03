package org.apache.coyote;

import org.apache.tomcat.util.net.SocketEvent;

public interface Adapter
{
    void service(final Request p0, final Response p1) throws Exception;
    
    boolean prepare(final Request p0, final Response p1) throws Exception;
    
    boolean asyncDispatch(final Request p0, final Response p1, final SocketEvent p2) throws Exception;
    
    void log(final Request p0, final Response p1, final long p2);
    
    void checkRecycled(final Request p0, final Response p1);
    
    String getDomain();
}
