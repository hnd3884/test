package org.bouncycastle.crypto.tls;

public class UseSRTPData
{
    protected int[] protectionProfiles;
    protected byte[] mki;
    
    public UseSRTPData(final int[] protectionProfiles, byte[] empty_BYTES) {
        if (protectionProfiles == null || protectionProfiles.length < 1 || protectionProfiles.length >= 32768) {
            throw new IllegalArgumentException("'protectionProfiles' must have length from 1 to (2^15 - 1)");
        }
        if (empty_BYTES == null) {
            empty_BYTES = TlsUtils.EMPTY_BYTES;
        }
        else if (empty_BYTES.length > 255) {
            throw new IllegalArgumentException("'mki' cannot be longer than 255 bytes");
        }
        this.protectionProfiles = protectionProfiles;
        this.mki = empty_BYTES;
    }
    
    public int[] getProtectionProfiles() {
        return this.protectionProfiles;
    }
    
    public byte[] getMki() {
        return this.mki;
    }
}
