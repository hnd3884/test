package com.sun.corba.se.impl.presentation.rmi;

import java.security.BasicPermission;

public final class DynamicAccessPermission extends BasicPermission
{
    public DynamicAccessPermission(final String s) {
        super(s);
    }
    
    public DynamicAccessPermission(final String s, final String s2) {
        super(s, s2);
    }
}
