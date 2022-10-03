package org.openjsse.com.sun.net.ssl;

import java.security.BasicPermission;

@Deprecated
public final class SSLPermission extends BasicPermission
{
    private static final long serialVersionUID = -2583684302506167542L;
    
    public SSLPermission(final String name) {
        super(name);
    }
    
    public SSLPermission(final String name, final String actions) {
        super(name, actions);
    }
}
