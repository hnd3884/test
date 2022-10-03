package org.jscep.transport.response;

public interface ScepResponseHandler<T>
{
    T getResponse(final byte[] p0, final String p1) throws ContentException;
}
