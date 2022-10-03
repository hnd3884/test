package sun.security.ec;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.PutAllAction;
import java.util.HashMap;
import java.util.Map;
import java.security.Provider;

public final class SunEC extends Provider
{
    private static final long serialVersionUID = -2279741672933606418L;
    private static boolean useFullImplementation;
    
    public SunEC() {
        super("SunEC", 1.8, "Sun Elliptic Curve provider (EC, ECDSA, ECDH)");
        if (System.getSecurityManager() == null) {
            SunECEntries.putEntries(this, SunEC.useFullImplementation);
        }
        else {
            final HashMap hashMap = new HashMap();
            SunECEntries.putEntries(hashMap, SunEC.useFullImplementation);
            AccessController.doPrivileged((PrivilegedAction<Object>)new PutAllAction(this, hashMap));
        }
    }
    
    static {
        SunEC.useFullImplementation = true;
        try {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    System.loadLibrary("sunec");
                    return null;
                }
            });
        }
        catch (final UnsatisfiedLinkError unsatisfiedLinkError) {
            SunEC.useFullImplementation = false;
        }
    }
}
