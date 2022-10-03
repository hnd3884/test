package org.bouncycastle.crypto.tls;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DigitallySigned
{
    protected SignatureAndHashAlgorithm algorithm;
    protected byte[] signature;
    
    public DigitallySigned(final SignatureAndHashAlgorithm algorithm, final byte[] signature) {
        if (signature == null) {
            throw new IllegalArgumentException("'signature' cannot be null");
        }
        this.algorithm = algorithm;
        this.signature = signature;
    }
    
    public SignatureAndHashAlgorithm getAlgorithm() {
        return this.algorithm;
    }
    
    public byte[] getSignature() {
        return this.signature;
    }
    
    public void encode(final OutputStream outputStream) throws IOException {
        if (this.algorithm != null) {
            this.algorithm.encode(outputStream);
        }
        TlsUtils.writeOpaque16(this.signature, outputStream);
    }
    
    public static DigitallySigned parse(final TlsContext tlsContext, final InputStream inputStream) throws IOException {
        SignatureAndHashAlgorithm parse = null;
        if (TlsUtils.isTLSv12(tlsContext)) {
            parse = SignatureAndHashAlgorithm.parse(inputStream);
        }
        return new DigitallySigned(parse, TlsUtils.readOpaque16(inputStream));
    }
}
