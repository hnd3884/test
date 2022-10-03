package org.glassfish.jersey.server.model;

public abstract class AbstractResourceModelVisitor implements ResourceModelVisitor
{
    @Override
    public void visitResource(final Resource resource) {
    }
    
    @Override
    public void visitChildResource(final Resource resource) {
    }
    
    @Override
    public void visitResourceMethod(final ResourceMethod method) {
    }
    
    @Override
    public void visitInvocable(final Invocable invocable) {
    }
    
    @Override
    public void visitMethodHandler(final MethodHandler methodHandler) {
    }
    
    @Override
    public void visitResourceHandlerConstructor(final HandlerConstructor constructor) {
    }
    
    @Override
    public void visitResourceModel(final ResourceModel resourceModel) {
    }
    
    @Override
    public void visitRuntimeResource(final RuntimeResource runtimeResource) {
    }
}
