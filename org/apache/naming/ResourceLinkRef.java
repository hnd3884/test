package org.apache.naming;

import javax.naming.RefAddr;
import javax.naming.StringRefAddr;

public class ResourceLinkRef extends AbstractRef
{
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.ResourceLinkFactory";
    public static final String GLOBALNAME = "globalName";
    
    public ResourceLinkRef(final String resourceClass, final String globalName, final String factory, final String factoryLocation) {
        super(resourceClass, factory, factoryLocation);
        StringRefAddr refAddr = null;
        if (globalName != null) {
            refAddr = new StringRefAddr("globalName", globalName);
            this.add(refAddr);
        }
    }
    
    @Override
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.ResourceLinkFactory";
    }
}
