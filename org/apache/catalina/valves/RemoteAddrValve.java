package org.apache.catalina.valves;

import org.apache.juli.logging.LogFactory;
import javax.servlet.ServletException;
import java.io.IOException;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;

public final class RemoteAddrValve extends RequestFilterValve
{
    private static final Log log;
    
    @Override
    public void invoke(final Request request, final Response response) throws IOException, ServletException {
        String property;
        if (this.getUsePeerAddress()) {
            property = request.getPeerAddr();
        }
        else {
            property = request.getRequest().getRemoteAddr();
        }
        if (this.getAddConnectorPort()) {
            property = property + ";" + request.getConnector().getPort();
        }
        this.process(property, request, response);
    }
    
    @Override
    protected Log getLog() {
        return RemoteAddrValve.log;
    }
    
    static {
        log = LogFactory.getLog((Class)RemoteAddrValve.class);
    }
}
