package org.glassfish.jersey.server.model.internal;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.server.model.ResourceModelIssue;
import java.util.List;

public class ModelErrors
{
    public static List<ResourceModelIssue> getErrorsAsResourceModelIssues() {
        return getErrorsAsResourceModelIssues(false);
    }
    
    public static List<ResourceModelIssue> getErrorsAsResourceModelIssues(final boolean afterMark) {
        return (List)Errors.getErrorMessages(afterMark).stream().map(input -> new ResourceModelIssue(input.getSource(), input.getMessage(), input.getSeverity())).collect(Collectors.toList());
    }
}
