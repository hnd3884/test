package sun.security.jgss.wrapper;

import org.ietf.jgss.Oid;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.util.Map;
import sun.security.action.PutAllAction;
import java.util.HashMap;
import java.security.Provider;

public final class SunNativeProvider extends Provider
{
    private static final long serialVersionUID = -238911724858694204L;
    private static final String NAME = "SunNativeGSS";
    private static final String INFO = "Sun Native GSS provider";
    private static final String MF_CLASS = "sun.security.jgss.wrapper.NativeGSSFactory";
    private static final String LIB_PROP = "sun.security.jgss.lib";
    private static final String DEBUG_PROP = "sun.security.nativegss.debug";
    private static HashMap<String, String> MECH_MAP;
    static final Provider INSTANCE;
    static boolean DEBUG;
    
    static void debug(final String s) {
        if (SunNativeProvider.DEBUG) {
            if (s == null) {
                throw new NullPointerException();
            }
            System.out.println("SunNativeGSS: " + s);
        }
    }
    
    public SunNativeProvider() {
        super("SunNativeGSS", 1.8, "Sun Native GSS provider");
        if (SunNativeProvider.MECH_MAP != null) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PutAllAction(this, SunNativeProvider.MECH_MAP));
        }
    }
    
    static {
        INSTANCE = new SunNativeProvider();
        SunNativeProvider.MECH_MAP = AccessController.doPrivileged((PrivilegedAction<HashMap<String, String>>)new PrivilegedAction<HashMap<String, String>>() {
            @Override
            public HashMap<String, String> run() {
                SunNativeProvider.DEBUG = Boolean.parseBoolean(System.getProperty("sun.security.nativegss.debug"));
                try {
                    System.loadLibrary("j2gss");
                }
                catch (final Error error) {
                    SunNativeProvider.debug("No j2gss library found!");
                    if (SunNativeProvider.DEBUG) {
                        error.printStackTrace();
                    }
                    return null;
                }
                String[] array = new String[0];
                final String property = System.getProperty("sun.security.jgss.lib");
                if (property == null || property.trim().equals("")) {
                    final String property2 = System.getProperty("os.name");
                    if (property2.startsWith("SunOS")) {
                        array = new String[] { "libgss.so" };
                    }
                    else if (property2.startsWith("Linux")) {
                        array = new String[] { "libgssapi.so", "libgssapi_krb5.so", "libgssapi_krb5.so.2" };
                    }
                    else if (property2.contains("OS X")) {
                        array = new String[] { "libgssapi_krb5.dylib", "/usr/lib/sasl2/libgssapiv2.2.so" };
                    }
                }
                else {
                    array = new String[] { property };
                }
                for (final String s : array) {
                    if (GSSLibStub.init(s, SunNativeProvider.DEBUG)) {
                        SunNativeProvider.debug("Loaded GSS library: " + s);
                        final Oid[] indicateMechs = GSSLibStub.indicateMechs();
                        final HashMap<String, String> hashMap = new HashMap<String, String>();
                        for (int j = 0; j < indicateMechs.length; ++j) {
                            SunNativeProvider.debug("Native MF for " + indicateMechs[j]);
                            hashMap.put("GssApiMechanism." + indicateMechs[j], "sun.security.jgss.wrapper.NativeGSSFactory");
                        }
                        return hashMap;
                    }
                }
                return null;
            }
        });
    }
}
