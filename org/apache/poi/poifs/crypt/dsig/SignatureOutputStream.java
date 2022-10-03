package org.apache.poi.poifs.crypt.dsig;

import java.io.IOException;
import java.security.SignatureException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.PrivateKey;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import java.security.Signature;

class SignatureOutputStream extends DigestOutputStream
{
    Signature signature;
    
    SignatureOutputStream(final HashAlgorithm algo, final PrivateKey key) {
        super(algo, key);
    }
    
    @Override
    public void init() throws GeneralSecurityException {
        final String provider = DigestOutputStream.isMSCapi(this.key) ? "SunMSCAPI" : "SunRsaSign";
        if (Security.getProvider(provider) != null) {
            this.signature = Signature.getInstance(this.algo.ecmaString + "withRSA", provider);
        }
        else {
            this.signature = Signature.getInstance(this.algo.ecmaString + "withRSA");
        }
        this.signature.initSign(this.key);
    }
    
    @Override
    public byte[] sign() throws SignatureException {
        return this.signature.sign();
    }
    
    @Override
    public void write(final int b) throws IOException {
        try {
            this.signature.update((byte)b);
        }
        catch (final SignatureException e) {
            throw new IOException(e);
        }
    }
    
    @Override
    public void write(final byte[] data, final int off, final int len) throws IOException {
        try {
            this.signature.update(data, off, len);
        }
        catch (final SignatureException e) {
            throw new IOException(e);
        }
    }
}
