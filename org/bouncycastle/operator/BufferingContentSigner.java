package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.io.BufferingOutputStream;
import java.io.OutputStream;

public class BufferingContentSigner implements ContentSigner
{
    private final ContentSigner contentSigner;
    private final OutputStream output;
    
    public BufferingContentSigner(final ContentSigner contentSigner) {
        this.contentSigner = contentSigner;
        this.output = (OutputStream)new BufferingOutputStream(contentSigner.getOutputStream());
    }
    
    public BufferingContentSigner(final ContentSigner contentSigner, final int n) {
        this.contentSigner = contentSigner;
        this.output = (OutputStream)new BufferingOutputStream(contentSigner.getOutputStream(), n);
    }
    
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.contentSigner.getAlgorithmIdentifier();
    }
    
    public OutputStream getOutputStream() {
        return this.output;
    }
    
    public byte[] getSignature() {
        return this.contentSigner.getSignature();
    }
}
