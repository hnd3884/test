package org.glassfish.jersey.client;

import javax.ws.rs.ProcessingException;

class AbortException extends ProcessingException
{
    private final transient ClientResponse abortResponse;
    
    AbortException(final ClientResponse abortResponse) {
        super("Request processing has been aborted");
        this.abortResponse = abortResponse;
    }
    
    public ClientResponse getAbortResponse() {
        return this.abortResponse;
    }
}
