package sun.security.krb5.internal.ccache;

import sun.security.krb5.KrbException;
import java.io.IOException;
import java.io.File;
import sun.security.krb5.PrincipalName;

public abstract class MemoryCredentialsCache extends CredentialsCache
{
    private static CredentialsCache getCCacheInstance(final PrincipalName principalName) {
        return null;
    }
    
    private static CredentialsCache getCCacheInstance(final PrincipalName principalName, final File file) {
        return null;
    }
    
    public abstract boolean exists(final String p0);
    
    @Override
    public abstract void update(final Credentials p0);
    
    @Override
    public abstract void save() throws IOException, KrbException;
    
    @Override
    public abstract Credentials[] getCredsList();
    
    @Override
    public abstract Credentials getCreds(final PrincipalName p0);
    
    @Override
    public abstract PrincipalName getPrimaryPrincipal();
}
