package org.jcp.xml.dsig.internal.dom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.net.URISyntaxException;
import java.util.Locale;
import java.security.AccessController;
import java.security.Security;
import java.util.Map;
import java.net.URI;
import java.util.Set;

public final class Policy
{
    private static Set<URI> disallowedAlgs;
    private static int maxTrans;
    private static int maxRefs;
    private static Set<String> disallowedRefUriSchemes;
    private static Map<String, Integer> minKeyMap;
    private static boolean noDuplicateIds;
    private static boolean noRMLoops;
    
    private Policy() {
    }
    
    private static void initialize() {
        final String s = AccessController.doPrivileged(() -> Security.getProperty("jdk.xml.dsig.secureValidationPolicy"));
        if (s == null || s.isEmpty()) {
            return;
        }
        for (final String s2 : s.split(",")) {
            final String[] split2 = s2.split("\\s");
            final String s3 = split2[0];
            switch (s3) {
                case "disallowAlg": {
                    if (split2.length != 2) {
                        error(s2);
                    }
                    Policy.disallowedAlgs.add(URI.create(split2[1]));
                    break;
                }
                case "maxTransforms": {
                    if (split2.length != 2) {
                        error(s2);
                    }
                    Policy.maxTrans = Integer.parseUnsignedInt(split2[1]);
                    break;
                }
                case "maxReferences": {
                    if (split2.length != 2) {
                        error(s2);
                    }
                    Policy.maxRefs = Integer.parseUnsignedInt(split2[1]);
                    break;
                }
                case "disallowReferenceUriSchemes": {
                    if (split2.length == 1) {
                        error(s2);
                    }
                    for (int j = 1; j < split2.length; ++j) {
                        Policy.disallowedRefUriSchemes.add(split2[j].toLowerCase(Locale.ROOT));
                    }
                    break;
                }
                case "minKeySize": {
                    if (split2.length != 3) {
                        error(s2);
                    }
                    Policy.minKeyMap.put(split2[1], Integer.parseUnsignedInt(split2[2]));
                    break;
                }
                case "noDuplicateIds": {
                    if (split2.length != 1) {
                        error(s2);
                    }
                    Policy.noDuplicateIds = true;
                    break;
                }
                case "noRetrievalMethodLoops": {
                    if (split2.length != 1) {
                        error(s2);
                    }
                    Policy.noRMLoops = true;
                    break;
                }
                default: {
                    error(s2);
                    break;
                }
            }
        }
    }
    
    public static boolean restrictAlg(final String s) {
        try {
            return Policy.disallowedAlgs.contains(new URI(s));
        }
        catch (final URISyntaxException ex) {
            return false;
        }
    }
    
    public static boolean restrictNumTransforms(final int n) {
        return n > Policy.maxTrans;
    }
    
    public static boolean restrictNumReferences(final int n) {
        return n > Policy.maxRefs;
    }
    
    public static boolean restrictReferenceUriScheme(final String s) {
        if (s != null) {
            final String scheme = URI.create(s).getScheme();
            if (scheme != null) {
                return Policy.disallowedRefUriSchemes.contains(scheme.toLowerCase(Locale.ROOT));
            }
        }
        return false;
    }
    
    public static boolean restrictKey(final String s, final int n) {
        return n < Policy.minKeyMap.getOrDefault(s, 0);
    }
    
    public static boolean restrictDuplicateIds() {
        return Policy.noDuplicateIds;
    }
    
    public static boolean restrictRetrievalMethodLoops() {
        return Policy.noRMLoops;
    }
    
    public static Set<URI> disabledAlgs() {
        return Collections.unmodifiableSet((Set<? extends URI>)Policy.disallowedAlgs);
    }
    
    public static int maxTransforms() {
        return Policy.maxTrans;
    }
    
    public static int maxReferences() {
        return Policy.maxRefs;
    }
    
    public static Set<String> disabledReferenceUriSchemes() {
        return Collections.unmodifiableSet((Set<? extends String>)Policy.disallowedRefUriSchemes);
    }
    
    public static int minKeySize(final String s) {
        return Policy.minKeyMap.getOrDefault(s, 0);
    }
    
    private static void error(final String s) {
        throw new IllegalArgumentException("Invalid jdk.xml.dsig.secureValidationPolicy entry: " + s);
    }
    
    static {
        Policy.disallowedAlgs = new HashSet<URI>();
        Policy.maxTrans = Integer.MAX_VALUE;
        Policy.maxRefs = Integer.MAX_VALUE;
        Policy.disallowedRefUriSchemes = new HashSet<String>();
        Policy.minKeyMap = new HashMap<String, Integer>();
        Policy.noDuplicateIds = false;
        Policy.noRMLoops = false;
        try {
            initialize();
        }
        catch (final Exception ex) {
            throw new SecurityException("Cannot initialize the secure validation policy", ex);
        }
    }
}
