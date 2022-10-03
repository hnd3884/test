package org.glassfish.jersey.server.model;

import org.glassfish.jersey.server.model.internal.ModelErrors;
import org.glassfish.jersey.internal.Errors;
import java.util.Iterator;
import org.glassfish.jersey.Severity;
import java.util.ArrayList;
import java.util.LinkedList;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import java.util.Collection;
import java.util.List;

public final class ComponentModelValidator
{
    private final List<ResourceModelIssue> issueList;
    private final List<ResourceModelVisitor> validators;
    
    public ComponentModelValidator(final Collection<ValueParamProvider> valueParamProviders, final MessageBodyWorkers msgBodyWorkers) {
        this.issueList = new LinkedList<ResourceModelIssue>();
        (this.validators = new ArrayList<ResourceModelVisitor>()).add(new ResourceValidator());
        this.validators.add(new RuntimeResourceModelValidator(msgBodyWorkers));
        this.validators.add(new ResourceMethodValidator(valueParamProviders));
        this.validators.add(new InvocableValidator());
    }
    
    public List<ResourceModelIssue> getIssueList() {
        return this.issueList;
    }
    
    public boolean fatalIssuesFound() {
        for (final ResourceModelIssue issue : this.getIssueList()) {
            if (issue.getSeverity() == Severity.FATAL) {
                return true;
            }
        }
        return false;
    }
    
    public void cleanIssueList() {
        this.issueList.clear();
    }
    
    public void validate(final ResourceModelComponent component) {
        Errors.process((Runnable)new Runnable() {
            @Override
            public void run() {
                Errors.mark();
                ComponentModelValidator.this.validateWithErrors(component);
                ComponentModelValidator.this.issueList.addAll(ModelErrors.getErrorsAsResourceModelIssues(true));
                Errors.unmark();
            }
        });
    }
    
    private void validateWithErrors(final ResourceModelComponent component) {
        for (final ResourceModelVisitor validator : this.validators) {
            component.accept(validator);
        }
        final List<? extends ResourceModelComponent> componentList = component.getComponents();
        if (null != componentList) {
            for (final ResourceModelComponent subComponent : componentList) {
                this.validateWithErrors(subComponent);
            }
        }
    }
}
