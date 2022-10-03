package org.glassfish.jersey.internal.inject;

public class ClassBinding<T> extends Binding<T, ClassBinding<T>>
{
    private final Class<T> service;
    
    ClassBinding(final Class<T> service) {
        this.asType(this.service = service);
    }
    
    public Class<T> getService() {
        return this.service;
    }
}
