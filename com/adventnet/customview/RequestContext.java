package com.adventnet.customview;

import java.io.Serializable;

public class RequestContext implements Serializable, Cloneable
{
    int sessionId;
    int requestId;
    
    public RequestContext(final int sessionId, final int requestId) {
        this.sessionId = sessionId;
        this.requestId = requestId;
    }
    
    public int getSessionId() {
        return this.sessionId;
    }
    
    public void setSessionId(final int v) {
        this.sessionId = v;
    }
    
    public int getRequestId() {
        return this.requestId;
    }
    
    public void setRequestId(final int v) {
        this.requestId = v;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RequestContext)) {
            return false;
        }
        final RequestContext toCheck = (RequestContext)obj;
        return this.requestId == toCheck.getRequestId() && this.sessionId == toCheck.sessionId;
    }
    
    @Override
    public int hashCode() {
        return (this.requestId + "#" + this.sessionId).hashCode();
    }
    
    public Object clone() {
        try {
            final RequestContext requestContext = (RequestContext)super.clone();
            requestContext.requestId = this.requestId;
            requestContext.sessionId = this.sessionId;
            return requestContext;
        }
        catch (final CloneNotSupportedException cnse) {
            final InternalError internalError = new InternalError("Could not clone RequestContext");
            internalError.initCause(cnse);
            throw internalError;
        }
    }
    
    @Override
    public String toString() {
        return "RequestContext[" + this.requestId + "," + super.hashCode() + "]";
    }
}
