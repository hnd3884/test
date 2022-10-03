package org.tanukisoftware.wrapper.security;

import java.security.BasicPermission;

public class WrapperPermission extends BasicPermission
{
    private static final long serialVersionUID = -4947853086614625658L;
    
    public WrapperPermission(final String name) {
        super(name);
    }
    
    public WrapperPermission(final String name, final String actions) {
        super(name, actions);
    }
}
