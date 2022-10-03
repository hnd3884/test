package org.glassfish.jersey.server.model;

public interface ResourceModelVisitor
{
    void visitResource(final Resource p0);
    
    void visitChildResource(final Resource p0);
    
    void visitResourceMethod(final ResourceMethod p0);
    
    void visitInvocable(final Invocable p0);
    
    void visitMethodHandler(final MethodHandler p0);
    
    void visitResourceHandlerConstructor(final HandlerConstructor p0);
    
    void visitResourceModel(final ResourceModel p0);
    
    void visitRuntimeResource(final RuntimeResource p0);
}
