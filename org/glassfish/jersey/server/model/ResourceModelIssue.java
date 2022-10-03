package org.glassfish.jersey.server.model;

import org.glassfish.jersey.Severity;

public final class ResourceModelIssue
{
    private final Object source;
    private final String message;
    private final Severity severity;
    
    public ResourceModelIssue(final Object source, final String message) {
        this(source, message, Severity.WARNING);
    }
    
    public ResourceModelIssue(final Object source, final String message, final Severity severity) {
        this.source = source;
        this.message = message;
        this.severity = severity;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public Severity getSeverity() {
        return this.severity;
    }
    
    public Object getSource() {
        return this.source;
    }
    
    @Override
    public String toString() {
        return "[" + this.severity + "] " + this.message + "; source='" + this.source + '\'';
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ResourceModelIssue that = (ResourceModelIssue)o;
        Label_0062: {
            if (this.message != null) {
                if (this.message.equals(that.message)) {
                    break Label_0062;
                }
            }
            else if (that.message == null) {
                break Label_0062;
            }
            return false;
        }
        if (this.severity != that.severity) {
            return false;
        }
        if (this.source != null) {
            if (this.source.equals(that.source)) {
                return true;
            }
        }
        else if (that.source == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = (this.source != null) ? this.source.hashCode() : 0;
        result = 31 * result + ((this.message != null) ? this.message.hashCode() : 0);
        result = 31 * result + ((this.severity != null) ? this.severity.hashCode() : 0);
        return result;
    }
}
