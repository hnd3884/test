package org.bouncycastle.crypto.tls;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SignatureAndHashAlgorithm
{
    protected short hash;
    protected short signature;
    
    public SignatureAndHashAlgorithm(final short hash, final short signature) {
        if (!TlsUtils.isValidUint8(hash)) {
            throw new IllegalArgumentException("'hash' should be a uint8");
        }
        if (!TlsUtils.isValidUint8(signature)) {
            throw new IllegalArgumentException("'signature' should be a uint8");
        }
        if (signature == 0) {
            throw new IllegalArgumentException("'signature' MUST NOT be \"anonymous\"");
        }
        this.hash = hash;
        this.signature = signature;
    }
    
    public short getHash() {
        return this.hash;
    }
    
    public short getSignature() {
        return this.signature;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof SignatureAndHashAlgorithm)) {
            return false;
        }
        final SignatureAndHashAlgorithm signatureAndHashAlgorithm = (SignatureAndHashAlgorithm)o;
        return signatureAndHashAlgorithm.getHash() == this.getHash() && signatureAndHashAlgorithm.getSignature() == this.getSignature();
    }
    
    @Override
    public int hashCode() {
        return this.getHash() << 16 | this.getSignature();
    }
    
    public void encode(final OutputStream outputStream) throws IOException {
        TlsUtils.writeUint8(this.getHash(), outputStream);
        TlsUtils.writeUint8(this.getSignature(), outputStream);
    }
    
    public static SignatureAndHashAlgorithm parse(final InputStream inputStream) throws IOException {
        return new SignatureAndHashAlgorithm(TlsUtils.readUint8(inputStream), TlsUtils.readUint8(inputStream));
    }
}
