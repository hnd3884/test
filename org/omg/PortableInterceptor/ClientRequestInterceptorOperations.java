package org.omg.PortableInterceptor;

public interface ClientRequestInterceptorOperations extends InterceptorOperations
{
    void send_request(final ClientRequestInfo p0) throws ForwardRequest;
    
    void send_poll(final ClientRequestInfo p0);
    
    void receive_reply(final ClientRequestInfo p0);
    
    void receive_exception(final ClientRequestInfo p0) throws ForwardRequest;
    
    void receive_other(final ClientRequestInfo p0) throws ForwardRequest;
}
