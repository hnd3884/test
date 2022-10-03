package org.omg.PortableInterceptor;

public interface ServerRequestInterceptorOperations extends InterceptorOperations
{
    void receive_request_service_contexts(final ServerRequestInfo p0) throws ForwardRequest;
    
    void receive_request(final ServerRequestInfo p0) throws ForwardRequest;
    
    void send_reply(final ServerRequestInfo p0);
    
    void send_exception(final ServerRequestInfo p0) throws ForwardRequest;
    
    void send_other(final ServerRequestInfo p0) throws ForwardRequest;
}
