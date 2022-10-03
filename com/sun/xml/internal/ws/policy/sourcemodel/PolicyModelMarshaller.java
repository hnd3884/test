package com.sun.xml.internal.ws.policy.sourcemodel;

import java.util.Collection;
import com.sun.xml.internal.ws.policy.PolicyException;

public abstract class PolicyModelMarshaller
{
    private static final PolicyModelMarshaller defaultXmlMarshaller;
    private static final PolicyModelMarshaller invisibleAssertionXmlMarshaller;
    
    PolicyModelMarshaller() {
    }
    
    public abstract void marshal(final PolicySourceModel p0, final Object p1) throws PolicyException;
    
    public abstract void marshal(final Collection<PolicySourceModel> p0, final Object p1) throws PolicyException;
    
    public static PolicyModelMarshaller getXmlMarshaller(final boolean marshallInvisible) {
        return marshallInvisible ? PolicyModelMarshaller.invisibleAssertionXmlMarshaller : PolicyModelMarshaller.defaultXmlMarshaller;
    }
    
    static {
        defaultXmlMarshaller = new XmlPolicyModelMarshaller(false);
        invisibleAssertionXmlMarshaller = new XmlPolicyModelMarshaller(true);
    }
}
