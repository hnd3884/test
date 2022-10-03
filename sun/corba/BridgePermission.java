package sun.corba;

import java.security.BasicPermission;

public final class BridgePermission extends BasicPermission
{
    public BridgePermission(final String s) {
        super(s);
    }
    
    public BridgePermission(final String s, final String s2) {
        super(s, s2);
    }
}
