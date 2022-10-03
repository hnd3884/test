package sun.security.acl;

import java.security.Principal;

public class WorldGroupImpl extends GroupImpl
{
    public WorldGroupImpl(final String s) {
        super(s);
    }
    
    @Override
    public boolean isMember(final Principal principal) {
        return true;
    }
}
