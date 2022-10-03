package com.sun.xml.internal.ws.policy.spi;

import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.PolicyException;

public final class AssertionCreationException extends PolicyException
{
    private final AssertionData assertionData;
    
    public AssertionCreationException(final AssertionData assertionData, final String message) {
        super(message);
        this.assertionData = assertionData;
    }
    
    public AssertionCreationException(final AssertionData assertionData, final String message, final Throwable cause) {
        super(message, cause);
        this.assertionData = assertionData;
    }
    
    public AssertionCreationException(final AssertionData assertionData, final Throwable cause) {
        super(cause);
        this.assertionData = assertionData;
    }
    
    public AssertionData getAssertionData() {
        return this.assertionData;
    }
}
