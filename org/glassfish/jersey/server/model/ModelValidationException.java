package org.glassfish.jersey.server.model;

import java.util.List;

public class ModelValidationException extends RuntimeException
{
    private static final long serialVersionUID = 4076015716487596210L;
    private final List<ResourceModelIssue> issues;
    
    public ModelValidationException(final String message, final List<ResourceModelIssue> issues) {
        super(message);
        this.issues = issues;
    }
    
    public List<ResourceModelIssue> getIssues() {
        return this.issues;
    }
    
    @Override
    public String getMessage() {
        final String message = super.getMessage();
        return ((message == null) ? "" : (message + '\n')) + this.issues.toString();
    }
}
