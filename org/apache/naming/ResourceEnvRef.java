package org.apache.naming;

public class ResourceEnvRef extends AbstractRef
{
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.ResourceEnvFactory";
    
    public ResourceEnvRef(final String resourceType) {
        super(resourceType);
    }
    
    @Override
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.ResourceEnvFactory";
    }
}
