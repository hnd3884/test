package com.sun.security.sasl.util;

import java.util.Map;

public final class PolicyUtils
{
    public static final int NOPLAINTEXT = 1;
    public static final int NOACTIVE = 2;
    public static final int NODICTIONARY = 4;
    public static final int FORWARD_SECRECY = 8;
    public static final int NOANONYMOUS = 16;
    public static final int PASS_CREDENTIALS = 512;
    
    private PolicyUtils() {
    }
    
    public static boolean checkPolicy(final int n, final Map<String, ?> map) {
        return map == null || ((!"true".equalsIgnoreCase((String)map.get("javax.security.sasl.policy.noplaintext")) || (n & 0x1) != 0x0) && (!"true".equalsIgnoreCase((String)map.get("javax.security.sasl.policy.noactive")) || (n & 0x2) != 0x0) && (!"true".equalsIgnoreCase((String)map.get("javax.security.sasl.policy.nodictionary")) || (n & 0x4) != 0x0) && (!"true".equalsIgnoreCase((String)map.get("javax.security.sasl.policy.noanonymous")) || (n & 0x10) != 0x0) && (!"true".equalsIgnoreCase((String)map.get("javax.security.sasl.policy.forward")) || (n & 0x8) != 0x0) && (!"true".equalsIgnoreCase((String)map.get("javax.security.sasl.policy.credentials")) || (n & 0x200) != 0x0));
    }
    
    public static String[] filterMechs(final String[] array, final int[] array2, final Map<String, ?> map) {
        if (map == null) {
            return array.clone();
        }
        final boolean[] array3 = new boolean[array.length];
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            final boolean[] array4 = array3;
            final int n2 = i;
            final boolean checkPolicy = checkPolicy(array2[i], map);
            array4[n2] = checkPolicy;
            if (checkPolicy) {
                ++n;
            }
        }
        final String[] array5 = new String[n];
        int j = 0;
        int n3 = 0;
        while (j < array.length) {
            if (array3[j]) {
                array5[n3++] = array[j];
            }
            ++j;
        }
        return array5;
    }
}
