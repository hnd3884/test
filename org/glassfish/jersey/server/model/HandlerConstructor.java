package org.glassfish.jersey.server.model;

import java.util.Iterator;
import java.util.List;
import java.lang.reflect.Constructor;

public final class HandlerConstructor implements Parameterized, ResourceModelComponent
{
    private final Constructor<?> constructor;
    private final List<Parameter> parameters;
    
    HandlerConstructor(final Constructor<?> constructor, final List<Parameter> parameters) {
        this.constructor = constructor;
        this.parameters = parameters;
    }
    
    public Constructor<?> getConstructor() {
        return this.constructor;
    }
    
    @Override
    public List<Parameter> getParameters() {
        return this.parameters;
    }
    
    @Override
    public boolean requiresEntity() {
        for (final Parameter p : this.getParameters()) {
            if (Parameter.Source.ENTITY == p.getSource()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void accept(final ResourceModelVisitor visitor) {
        visitor.visitResourceHandlerConstructor(this);
    }
    
    @Override
    public List<ResourceModelComponent> getComponents() {
        return null;
    }
}
