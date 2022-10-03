package com.sun.security.jgss;

import org.ietf.jgss.GSSException;
import jdk.Exported;
import org.ietf.jgss.GSSContext;

@Exported
public interface ExtendedGSSContext extends GSSContext
{
    Object inquireSecContext(final InquireType p0) throws GSSException;
    
    void requestDelegPolicy(final boolean p0) throws GSSException;
    
    boolean getDelegPolicyState();
}
