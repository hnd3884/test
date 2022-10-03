package org.tukaani.xz.check;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public class SHA256 extends Check
{
    private final MessageDigest sha256;
    
    public SHA256() throws NoSuchAlgorithmException {
        this.size = 32;
        this.name = "SHA-256";
        this.sha256 = MessageDigest.getInstance("SHA-256");
    }
    
    @Override
    public void update(final byte[] array, final int n, final int n2) {
        this.sha256.update(array, n, n2);
    }
    
    @Override
    public byte[] finish() {
        final byte[] digest = this.sha256.digest();
        this.sha256.reset();
        return digest;
    }
}
