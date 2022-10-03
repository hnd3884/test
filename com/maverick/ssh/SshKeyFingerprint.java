package com.maverick.ssh;

import com.maverick.ssh.components.ComponentManager;
import com.maverick.ssh.components.Digest;

public class SshKeyFingerprint
{
    public static final String MD5_FINGERPRINT = "MD5";
    public static final String SHA1_FINGERPRINT = "SHA-1";
    public static final String SHA256_FINGERPRINT = "SHA-256";
    private static String c;
    static char[] b;
    
    public static String getFingerprint(final byte[] array) throws SshException {
        return getFingerprint(array, SshKeyFingerprint.c);
    }
    
    public static void setDefaultHashAlgorithm(final String c) {
        SshKeyFingerprint.c = c;
    }
    
    public static String getFingerprint(final byte[] array, final String s) throws SshException {
        final Digest digest = (Digest)ComponentManager.getInstance().supportedDigests().getInstance(s);
        digest.putBytes(array);
        final byte[] doFinal = digest.doFinal();
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < doFinal.length; ++i) {
            final int n = doFinal[i] & 0xFF;
            if (i > 0) {
                sb.append(':');
            }
            sb.append(SshKeyFingerprint.b[n >>> 4 & 0xF]);
            sb.append(SshKeyFingerprint.b[n & 0xF]);
        }
        return sb.toString();
    }
    
    static {
        SshKeyFingerprint.c = "MD5";
        SshKeyFingerprint.b = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
