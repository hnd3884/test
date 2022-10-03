package sun.security.provider;

import java.security.Permission;
import java.security.ProtectionDomain;
import java.security.PermissionCollection;
import java.security.CodeSource;
import java.net.MalformedURLException;
import java.security.URIParameter;
import java.security.Policy;
import java.security.PolicySpi;

public final class PolicySpiFile extends PolicySpi
{
    private PolicyFile pf;
    
    public PolicySpiFile(final Policy.Parameters parameters) {
        if (parameters == null) {
            this.pf = new PolicyFile();
        }
        else {
            if (!(parameters instanceof URIParameter)) {
                throw new IllegalArgumentException("Unrecognized policy parameter: " + parameters);
            }
            final URIParameter uriParameter = (URIParameter)parameters;
            try {
                this.pf = new PolicyFile(uriParameter.getURI().toURL());
            }
            catch (final MalformedURLException ex) {
                throw new IllegalArgumentException("Invalid URIParameter", ex);
            }
        }
    }
    
    @Override
    protected PermissionCollection engineGetPermissions(final CodeSource codeSource) {
        return this.pf.getPermissions(codeSource);
    }
    
    @Override
    protected PermissionCollection engineGetPermissions(final ProtectionDomain protectionDomain) {
        return this.pf.getPermissions(protectionDomain);
    }
    
    @Override
    protected boolean engineImplies(final ProtectionDomain protectionDomain, final Permission permission) {
        return this.pf.implies(protectionDomain, permission);
    }
    
    @Override
    protected void engineRefresh() {
        this.pf.refresh();
    }
}
