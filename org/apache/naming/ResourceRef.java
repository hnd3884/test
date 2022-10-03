package org.apache.naming;

import javax.naming.RefAddr;
import javax.naming.StringRefAddr;

public class ResourceRef extends AbstractRef
{
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.ResourceFactory";
    public static final String DESCRIPTION = "description";
    public static final String SCOPE = "scope";
    public static final String AUTH = "auth";
    public static final String SINGLETON = "singleton";
    
    public ResourceRef(final String resourceClass, final String description, final String scope, final String auth, final boolean singleton) {
        this(resourceClass, description, scope, auth, singleton, null, null);
    }
    
    public ResourceRef(final String resourceClass, final String description, final String scope, final String auth, final boolean singleton, final String factory, final String factoryLocation) {
        super(resourceClass, factory, factoryLocation);
        StringRefAddr refAddr = null;
        if (description != null) {
            refAddr = new StringRefAddr("description", description);
            this.add(refAddr);
        }
        if (scope != null) {
            refAddr = new StringRefAddr("scope", scope);
            this.add(refAddr);
        }
        if (auth != null) {
            refAddr = new StringRefAddr("auth", auth);
            this.add(refAddr);
        }
        refAddr = new StringRefAddr("singleton", Boolean.toString(singleton));
        this.add(refAddr);
    }
    
    @Override
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.ResourceFactory";
    }
}
