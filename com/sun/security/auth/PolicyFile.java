package com.sun.security.auth;

import java.security.PermissionCollection;
import java.security.CodeSource;
import javax.security.auth.Subject;
import sun.security.provider.AuthPolicyFile;
import jdk.Exported;
import javax.security.auth.Policy;

@Exported(false)
@Deprecated
public class PolicyFile extends Policy
{
    private final AuthPolicyFile apf;
    
    public PolicyFile() {
        this.apf = new AuthPolicyFile();
    }
    
    @Override
    public void refresh() {
        this.apf.refresh();
    }
    
    @Override
    public PermissionCollection getPermissions(final Subject subject, final CodeSource codeSource) {
        return this.apf.getPermissions(subject, codeSource);
    }
}
