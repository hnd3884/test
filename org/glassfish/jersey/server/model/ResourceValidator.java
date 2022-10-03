package org.glassfish.jersey.server.model;

import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.server.internal.LocalizationMessages;

class ResourceValidator extends AbstractResourceModelVisitor
{
    @Override
    public void visitResource(final Resource resource) {
        this.checkResource(resource);
    }
    
    private void checkResource(final Resource resource) {
        if (!resource.getResourceMethods().isEmpty() && resource.getResourceLocator() != null) {
            Errors.warning((Object)resource, LocalizationMessages.RESOURCE_CONTAINS_RES_METHODS_AND_LOCATOR(resource, resource.getPath()));
        }
        if (resource.getPath() != null && resource.getResourceMethods().isEmpty() && resource.getChildResources().isEmpty() && resource.getResourceLocator() == null) {
            Errors.warning((Object)resource, LocalizationMessages.RESOURCE_EMPTY(resource, resource.getPath()));
        }
    }
    
    @Override
    public void visitChildResource(final Resource resource) {
        this.checkResource(resource);
    }
}
