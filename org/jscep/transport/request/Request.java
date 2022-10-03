package org.jscep.transport.request;

public abstract class Request
{
    private final Operation operation;
    
    public Request(final Operation operation) {
        this.operation = operation;
    }
    
    public final Operation getOperation() {
        return this.operation;
    }
    
    public abstract String getMessage();
}
