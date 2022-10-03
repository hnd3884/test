package org.apache.naming;

import javax.naming.RefAddr;
import javax.naming.StringRefAddr;

public class LookupRef extends AbstractRef
{
    private static final long serialVersionUID = 1L;
    public static final String LOOKUP_NAME = "lookup-name";
    
    public LookupRef(final String resourceType, final String lookupName) {
        this(resourceType, (String)null, null, lookupName);
    }
    
    public LookupRef(final String resourceType, final String factory, final String factoryLocation, final String lookupName) {
        super(resourceType, factory, factoryLocation);
        if (lookupName != null && !lookupName.equals("")) {
            final RefAddr ref = new StringRefAddr("lookup-name", lookupName);
            this.add(ref);
        }
    }
    
    @Override
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.LookupFactory";
    }
}
