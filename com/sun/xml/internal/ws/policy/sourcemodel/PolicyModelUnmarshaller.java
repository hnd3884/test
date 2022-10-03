package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.PolicyException;

public abstract class PolicyModelUnmarshaller
{
    private static final PolicyModelUnmarshaller xmlUnmarshaller;
    
    PolicyModelUnmarshaller() {
    }
    
    public abstract PolicySourceModel unmarshalModel(final Object p0) throws PolicyException;
    
    public static PolicyModelUnmarshaller getXmlUnmarshaller() {
        return PolicyModelUnmarshaller.xmlUnmarshaller;
    }
    
    static {
        xmlUnmarshaller = new XmlPolicyModelUnmarshaller();
    }
}
