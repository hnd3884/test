package com.me.ems.onpremise.summaryserver.common.authentication;

import java.util.Enumeration;
import java.util.UUID;
import java.util.Hashtable;

public class ProbeHSKeyGenerator
{
    private static ProbeHSKeyGenerator instance;
    private static Hashtable<String, Long> keyMap;
    private static String lastGeneratedKey;
    
    public static synchronized ProbeHSKeyGenerator getInstance() {
        if (ProbeHSKeyGenerator.instance == null) {
            ProbeHSKeyGenerator.instance = new ProbeHSKeyGenerator();
        }
        return ProbeHSKeyGenerator.instance;
    }
    
    public static Hashtable<String, Long> getKeyMap() {
        return ProbeHSKeyGenerator.keyMap;
    }
    
    public static String getLastGeneratedKey() {
        return ProbeHSKeyGenerator.lastGeneratedKey;
    }
    
    public static String generateKey() {
        final Long cT = System.currentTimeMillis();
        final String key = UUID.randomUUID().toString().toUpperCase();
        ProbeHSKeyGenerator.keyMap.put(key, cT);
        return ProbeHSKeyGenerator.lastGeneratedKey = key;
    }
    
    public static synchronized String getKey() {
        final String key = generateKey();
        final Enumeration<String> enumeration = ProbeHSKeyGenerator.keyMap.keys();
        while (enumeration.hasMoreElements()) {
            final String nextKey = enumeration.nextElement();
            final Long nextTime = ProbeHSKeyGenerator.keyMap.get(nextKey);
            final Long cT = System.currentTimeMillis();
            if (nextTime + 180000L < cT) {
                ProbeHSKeyGenerator.keyMap.remove(nextKey);
            }
        }
        return key;
    }
    
    public static boolean validateKey(final String key) {
        final Long ct = System.currentTimeMillis();
        if (!ProbeHSKeyGenerator.keyMap.containsKey(key)) {
            return false;
        }
        final Long genTime = ProbeHSKeyGenerator.keyMap.get(key);
        if (ct < genTime + 180000L) {
            return true;
        }
        ProbeHSKeyGenerator.keyMap.remove(key);
        return false;
    }
    
    static {
        ProbeHSKeyGenerator.instance = null;
        ProbeHSKeyGenerator.keyMap = new Hashtable<String, Long>();
        ProbeHSKeyGenerator.lastGeneratedKey = "";
    }
}
