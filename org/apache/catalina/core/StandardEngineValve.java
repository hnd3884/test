package org.apache.catalina.core;

import javax.servlet.ServletException;
import java.io.IOException;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.Request;
import org.apache.catalina.valves.ValveBase;

final class StandardEngineValve extends ValveBase
{
    public StandardEngineValve() {
        super(true);
    }
    
    @Override
    public final void invoke(final Request request, final Response response) throws IOException, ServletException {
        final Host host = request.getHost();
        if (host == null) {
            if (!response.isError()) {
                response.sendError(404);
            }
            return;
        }
        if (request.isAsyncSupported()) {
            request.setAsyncSupported(host.getPipeline().isAsyncSupported());
        }
        host.getPipeline().getFirst().invoke(request, response);
    }
}
