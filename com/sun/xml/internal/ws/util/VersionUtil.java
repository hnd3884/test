package com.sun.xml.internal.ws.util;

import java.util.StringTokenizer;

public final class VersionUtil
{
    public static final String JAXWS_VERSION_20 = "2.0";
    public static final String JAXWS_VERSION_DEFAULT = "2.0";
    
    public static boolean isVersion20(final String version) {
        return "2.0".equals(version);
    }
    
    public static boolean isValidVersion(final String version) {
        return isVersion20(version);
    }
    
    public static String getValidVersionString() {
        return "2.0";
    }
    
    public static int[] getCanonicalVersion(final String version) {
        final int[] canonicalVersion = { 1, 1, 0, 0 };
        final String DASH_DELIM = "_";
        final String DOT_DELIM = ".";
        final StringTokenizer tokenizer = new StringTokenizer(version, ".");
        String token = tokenizer.nextToken();
        canonicalVersion[0] = Integer.parseInt(token);
        token = tokenizer.nextToken();
        if (token.indexOf("_") == -1) {
            canonicalVersion[1] = Integer.parseInt(token);
        }
        else {
            final StringTokenizer subTokenizer = new StringTokenizer(token, "_");
            canonicalVersion[1] = Integer.parseInt(subTokenizer.nextToken());
            canonicalVersion[3] = Integer.parseInt(subTokenizer.nextToken());
        }
        if (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if (token.indexOf("_") == -1) {
                canonicalVersion[2] = Integer.parseInt(token);
                if (tokenizer.hasMoreTokens()) {
                    canonicalVersion[3] = Integer.parseInt(tokenizer.nextToken());
                }
            }
            else {
                final StringTokenizer subTokenizer = new StringTokenizer(token, "_");
                canonicalVersion[2] = Integer.parseInt(subTokenizer.nextToken());
                canonicalVersion[3] = Integer.parseInt(subTokenizer.nextToken());
            }
        }
        return canonicalVersion;
    }
    
    public static int compare(final String version1, final String version2) {
        final int[] canonicalVersion1 = getCanonicalVersion(version1);
        final int[] canonicalVersion2 = getCanonicalVersion(version2);
        if (canonicalVersion1[0] < canonicalVersion2[0]) {
            return -1;
        }
        if (canonicalVersion1[0] > canonicalVersion2[0]) {
            return 1;
        }
        if (canonicalVersion1[1] < canonicalVersion2[1]) {
            return -1;
        }
        if (canonicalVersion1[1] > canonicalVersion2[1]) {
            return 1;
        }
        if (canonicalVersion1[2] < canonicalVersion2[2]) {
            return -1;
        }
        if (canonicalVersion1[2] > canonicalVersion2[2]) {
            return 1;
        }
        if (canonicalVersion1[3] < canonicalVersion2[3]) {
            return -1;
        }
        if (canonicalVersion1[3] > canonicalVersion2[3]) {
            return 1;
        }
        return 0;
    }
}
