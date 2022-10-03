package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.ErrorInformation;

public class ErrorInformationImpl implements ErrorInformation
{
    private final ErrorType errorType;
    private final Descriptor descriptor;
    private final Injectee injectee;
    private final MultiException exception;
    
    ErrorInformationImpl(final ErrorType errorType, final Descriptor descriptor, final Injectee injectee, final MultiException exception) {
        this.errorType = errorType;
        this.descriptor = descriptor;
        this.injectee = injectee;
        this.exception = exception;
    }
    
    public ErrorType getErrorType() {
        return this.errorType;
    }
    
    public Descriptor getDescriptor() {
        return this.descriptor;
    }
    
    public Injectee getInjectee() {
        return this.injectee;
    }
    
    public MultiException getAssociatedException() {
        return this.exception;
    }
    
    @Override
    public String toString() {
        return "ErrorInformation(" + this.errorType + "," + this.descriptor + "," + this.injectee + "," + this.exception + "," + System.identityHashCode(this) + ")";
    }
}
