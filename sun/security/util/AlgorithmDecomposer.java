package sun.security.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class AlgorithmDecomposer
{
    private static final Pattern transPattern;
    private static final Pattern pattern;
    
    private static Set<String> decomposeImpl(final String s) {
        final String[] split = AlgorithmDecomposer.transPattern.split(s);
        final HashSet set = new HashSet();
        for (final String s2 : split) {
            if (s2 != null) {
                if (s2.length() != 0) {
                    for (final String s3 : AlgorithmDecomposer.pattern.split(s2)) {
                        if (s3 != null) {
                            if (s3.length() != 0) {
                                set.add(s3);
                            }
                        }
                    }
                }
            }
        }
        return set;
    }
    
    public Set<String> decompose(final String s) {
        if (s == null || s.length() == 0) {
            return new HashSet<String>();
        }
        final Set<String> decomposeImpl = decomposeImpl(s);
        if (decomposeImpl.contains("SHA1") && !decomposeImpl.contains("SHA-1")) {
            decomposeImpl.add("SHA-1");
        }
        if (decomposeImpl.contains("SHA-1") && !decomposeImpl.contains("SHA1")) {
            decomposeImpl.add("SHA1");
        }
        if (decomposeImpl.contains("SHA224") && !decomposeImpl.contains("SHA-224")) {
            decomposeImpl.add("SHA-224");
        }
        if (decomposeImpl.contains("SHA-224") && !decomposeImpl.contains("SHA224")) {
            decomposeImpl.add("SHA224");
        }
        if (decomposeImpl.contains("SHA256") && !decomposeImpl.contains("SHA-256")) {
            decomposeImpl.add("SHA-256");
        }
        if (decomposeImpl.contains("SHA-256") && !decomposeImpl.contains("SHA256")) {
            decomposeImpl.add("SHA256");
        }
        if (decomposeImpl.contains("SHA384") && !decomposeImpl.contains("SHA-384")) {
            decomposeImpl.add("SHA-384");
        }
        if (decomposeImpl.contains("SHA-384") && !decomposeImpl.contains("SHA384")) {
            decomposeImpl.add("SHA384");
        }
        if (decomposeImpl.contains("SHA512") && !decomposeImpl.contains("SHA-512")) {
            decomposeImpl.add("SHA-512");
        }
        if (decomposeImpl.contains("SHA-512") && !decomposeImpl.contains("SHA512")) {
            decomposeImpl.add("SHA512");
        }
        return decomposeImpl;
    }
    
    public static Collection<String> getAliases(final String s) {
        String[] array;
        if (s.equalsIgnoreCase("DH") || s.equalsIgnoreCase("DiffieHellman")) {
            array = new String[] { "DH", "DiffieHellman" };
        }
        else {
            array = new String[] { s };
        }
        return Arrays.asList(array);
    }
    
    private static void hasLoop(final Set<String> set, final String s, final String s2) {
        if (set.contains(s)) {
            if (!set.contains(s2)) {
                set.add(s2);
            }
            set.remove(s);
        }
    }
    
    public static Set<String> decomposeOneHash(final String s) {
        if (s == null || s.length() == 0) {
            return new HashSet<String>();
        }
        final Set<String> decomposeImpl = decomposeImpl(s);
        hasLoop(decomposeImpl, "SHA-1", "SHA1");
        hasLoop(decomposeImpl, "SHA-224", "SHA224");
        hasLoop(decomposeImpl, "SHA-256", "SHA256");
        hasLoop(decomposeImpl, "SHA-384", "SHA384");
        hasLoop(decomposeImpl, "SHA-512", "SHA512");
        return decomposeImpl;
    }
    
    public static String hashName(final String s) {
        return s.replace("-", "");
    }
    
    static {
        transPattern = Pattern.compile("/");
        pattern = Pattern.compile("with|and", 2);
    }
}
