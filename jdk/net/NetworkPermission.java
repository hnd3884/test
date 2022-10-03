package jdk.net;

import jdk.Exported;
import java.security.BasicPermission;

@Exported
public final class NetworkPermission extends BasicPermission
{
    private static final long serialVersionUID = -2012939586906722291L;
    
    public NetworkPermission(final String s) {
        super(s);
    }
    
    public NetworkPermission(final String s, final String s2) {
        super(s, s2);
    }
}
