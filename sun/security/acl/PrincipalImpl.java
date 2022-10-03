package sun.security.acl;

import java.security.Principal;

public class PrincipalImpl implements Principal
{
    private String user;
    
    public PrincipalImpl(final String user) {
        this.user = user;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof PrincipalImpl && this.user.equals(((PrincipalImpl)o).toString());
    }
    
    @Override
    public String toString() {
        return this.user;
    }
    
    @Override
    public int hashCode() {
        return this.user.hashCode();
    }
    
    @Override
    public String getName() {
        return this.user;
    }
}
