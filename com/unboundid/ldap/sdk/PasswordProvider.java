package com.unboundid.ldap.sdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;
import java.io.Serializable;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class PasswordProvider implements Serializable
{
    private static final long serialVersionUID = -1582416755360005908L;
    
    public abstract byte[] getPasswordBytes() throws LDAPException;
}
