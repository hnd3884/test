package com.sun.media.sound;

import java.util.StringTokenizer;
import java.security.AccessController;

final class Platform
{
    private static final String libNameMain = "jsound";
    private static final String libNameALSA = "jsoundalsa";
    private static final String libNameDSound = "jsoundds";
    public static final int LIB_MAIN = 1;
    public static final int LIB_ALSA = 2;
    public static final int LIB_DSOUND = 4;
    private static int loadedLibs;
    public static final int FEATURE_MIDIIO = 1;
    public static final int FEATURE_PORTS = 2;
    public static final int FEATURE_DIRECT_AUDIO = 3;
    private static boolean signed8;
    private static boolean bigEndian;
    
    private Platform() {
    }
    
    static void initialize() {
    }
    
    static boolean isBigEndian() {
        return Platform.bigEndian;
    }
    
    static boolean isSigned8() {
        return Platform.signed8;
    }
    
    private static void loadLibraries() {
        AccessController.doPrivileged(() -> {
            System.loadLibrary("jsound");
            return null;
        });
        Platform.loadedLibs |= 0x1;
        final StringTokenizer stringTokenizer = new StringTokenizer(nGetExtraLibraries());
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            try {
                AccessController.doPrivileged(() -> {
                    System.loadLibrary(s);
                    return null;
                });
                if (nextToken.equals("jsoundalsa")) {
                    Platform.loadedLibs |= 0x2;
                }
                else {
                    if (!nextToken.equals("jsoundds")) {
                        continue;
                    }
                    Platform.loadedLibs |= 0x4;
                }
            }
            catch (final Throwable t) {}
        }
    }
    
    static boolean isMidiIOEnabled() {
        return isFeatureLibLoaded(1);
    }
    
    static boolean isPortsEnabled() {
        return isFeatureLibLoaded(2);
    }
    
    static boolean isDirectAudioEnabled() {
        return isFeatureLibLoaded(3);
    }
    
    private static boolean isFeatureLibLoaded(final int n) {
        final int nGetLibraryForFeature = nGetLibraryForFeature(n);
        return nGetLibraryForFeature != 0 && (Platform.loadedLibs & nGetLibraryForFeature) == nGetLibraryForFeature;
    }
    
    private static native boolean nIsBigEndian();
    
    private static native boolean nIsSigned8();
    
    private static native String nGetExtraLibraries();
    
    private static native int nGetLibraryForFeature(final int p0);
    
    private static void readProperties() {
        Platform.bigEndian = nIsBigEndian();
        Platform.signed8 = nIsSigned8();
    }
    
    static {
        Platform.loadedLibs = 0;
        loadLibraries();
        readProperties();
    }
}
