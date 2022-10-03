package org.openjsse.legacy8ujsse.sun.security.validator;

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
        void checkDistrust(final String variant, final X509Certificate[] chain) throws ValidatorException {
            if (!variant.equals("tls server")) {
                return;
            }
            SymantecTLSPolicy.checkDistrust(chain);
        }
    };
    
    static final EnumSet<CADistrustPolicy> POLICIES;
    
    abstract void checkDistrust(final String p0, final X509Certificate[] p1) throws ValidatorException;
    
    private static EnumSet<CADistrustPolicy> parseProperty() {
        final String property = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return Security.getProperty("jdk.security.caDistrustPolicies");
            }
        });
        final EnumSet<CADistrustPolicy> set = EnumSet.noneOf(CADistrustPolicy.class);
        if (property == null || property.isEmpty()) {
            return set;
        }
        final String[] split;
        final String[] policies = split = property.split(",");
        for (String policy : split) {
            policy = policy.trim();
            try {
                final CADistrustPolicy caPolicy = Enum.valueOf(CADistrustPolicy.class, policy);
                set.add(caPolicy);
            }
            catch (final IllegalArgumentException iae) {
                final Debug debug = Debug.getInstance("certpath");
                if (debug != null) {
                    debug.println("Unknown value for the jdk.security.caDistrustPolicies property: " + policy);
                }
            }
        }
        return set;
    }
    
    static {
        POLICIES = parseProperty();
    }
}
