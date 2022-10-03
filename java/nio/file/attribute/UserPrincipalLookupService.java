package java.nio.file.attribute;

import java.io.IOException;

public abstract class UserPrincipalLookupService
{
    protected UserPrincipalLookupService() {
    }
    
    public abstract UserPrincipal lookupPrincipalByName(final String p0) throws IOException;
    
    public abstract GroupPrincipal lookupPrincipalByGroupName(final String p0) throws IOException;
}
