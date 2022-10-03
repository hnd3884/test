package com.sun.net.ssl;

import java.security.BasicPermission;

@Deprecated
public final class SSLPermission extends BasicPermission
{
    private static final long serialVersionUID = -2583684302506167542L;
    
    public SSLPermission(final String s) {
        super(s);
    }
    
    public SSLPermission(final String s, final String s2) {
        super(s, s2);
    }
}
