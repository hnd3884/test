package org.jscep.transport;

import org.jscep.transport.response.ScepResponseHandler;
import org.jscep.transport.request.Request;

public interface Transport
{
     <T> T sendRequest(final Request p0, final ScepResponseHandler<T> p1) throws TransportException;
}
