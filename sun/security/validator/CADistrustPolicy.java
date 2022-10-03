package sun.security.validator;

import sun.security.util.Debug;
import java.security.AccessController;
import java.security.Security;
import java.security.PrivilegedAction;
import java.security.cert.X509Certificate;
import java.util.EnumSet;

enum CADistrustPolicy
{
    SYMANTEC_TLS {
        @Override
        void checkDistrust(final String s, final X509Certificate[] array) throws ValidatorException {
            if (!s.equals("tls server")) {
                return;
            }
            SymantecTLSPolicy.checkDistrust(array);
        }
    };
    
    static final EnumSet<CADistrustPolicy> POLICIES;
    
    abstract void checkDistrust(final String p0, final X509Certificate[] p1) throws ValidatorException;
    
    private static EnumSet<CADistrustPolicy> parseProperty() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return Security.getProperty("jdk.security.caDistrustPolicies");
            }
        });
        final EnumSet<CADistrustPolicy> none = EnumSet.noneOf(CADistrustPolicy.class);
        if (s == null || s.isEmpty()) {
            return none;
        }
        final String[] split = s.split(",");
        for (int length = split.length, i = 0; i < length; ++i) {
            final String trim = split[i].trim();
            try {
                none.add(Enum.valueOf(CADistrustPolicy.class, trim));
            }
            catch (final IllegalArgumentException ex) {
                final Debug instance = Debug.getInstance("certpath");
                if (instance != null) {
                    instance.println("Unknown value for the jdk.security.caDistrustPolicies property: " + trim);
                }
            }
        }
        return none;
    }
    
    static {
        POLICIES = parseProperty();
    }
}
