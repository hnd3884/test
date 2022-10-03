package org.glassfish.jersey.message.internal;

import javax.ws.rs.core.Response;

public final class Statuses
{
    public static Response.StatusType from(final int code) {
        final Response.StatusType result = (Response.StatusType)Response.Status.fromStatusCode(code);
        return (Response.StatusType)((result != null) ? result : new StatusImpl(code, ""));
    }
    
    public static Response.StatusType from(final int code, final String reason) {
        return (Response.StatusType)new StatusImpl(code, reason);
    }
    
    public static Response.StatusType from(final Response.StatusType status, final String reason) {
        return (Response.StatusType)new StatusImpl(status.getStatusCode(), reason);
    }
    
    private Statuses() {
        throw new AssertionError((Object)"Instantiation not allowed.");
    }
    
    private static final class StatusImpl implements Response.StatusType
    {
        private final int code;
        private final String reason;
        private final Response.Status.Family family;
        
        private StatusImpl(final int code, final String reason) {
            this.code = code;
            this.reason = reason;
            this.family = Response.Status.Family.familyOf(code);
        }
        
        public int getStatusCode() {
            return this.code;
        }
        
        public String getReasonPhrase() {
            return this.reason;
        }
        
        @Override
        public String toString() {
            return this.reason;
        }
        
        public Response.Status.Family getFamily() {
            return this.family;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Response.StatusType)) {
                return false;
            }
            final Response.StatusType status = (Response.StatusType)o;
            if (this.code != status.getStatusCode()) {
                return false;
            }
            if (this.family != status.getFamily()) {
                return false;
            }
            if (this.reason != null) {
                if (this.reason.equals(status.getReasonPhrase())) {
                    return true;
                }
            }
            else if (status.getReasonPhrase() == null) {
                return true;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int result = this.code;
            result = 31 * result + ((this.reason != null) ? this.reason.hashCode() : 0);
            result = 31 * result + this.family.hashCode();
            return result;
        }
    }
}
