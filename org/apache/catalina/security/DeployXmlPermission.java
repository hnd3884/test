package org.apache.catalina.security;

import java.security.BasicPermission;

public class DeployXmlPermission extends BasicPermission
{
    private static final long serialVersionUID = 1L;
    
    public DeployXmlPermission(final String name) {
        super(name);
    }
    
    public DeployXmlPermission(final String name, final String actions) {
        super(name, actions);
    }
}
