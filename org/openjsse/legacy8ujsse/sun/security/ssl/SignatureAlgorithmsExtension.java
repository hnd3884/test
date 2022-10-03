package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.Iterator;
import java.io.IOException;
import javax.net.ssl.SSLProtocolException;
import java.util.ArrayList;
import java.util.Collection;

final class SignatureAlgorithmsExtension extends HelloExtension
{
    private Collection<SignatureAndHashAlgorithm> algorithms;
    private int algorithmsLen;
    
    SignatureAlgorithmsExtension(final Collection<SignatureAndHashAlgorithm> signAlgs) {
        super(ExtensionType.EXT_SIGNATURE_ALGORITHMS);
        this.algorithms = new ArrayList<SignatureAndHashAlgorithm>(signAlgs);
        this.algorithmsLen = SignatureAndHashAlgorithm.sizeInRecord() * this.algorithms.size();
    }
    
    SignatureAlgorithmsExtension(final HandshakeInStream s, final int len) throws IOException {
        super(ExtensionType.EXT_SIGNATURE_ALGORITHMS);
        this.algorithmsLen = s.getInt16();
        if (this.algorithmsLen == 0 || this.algorithmsLen + 2 != len) {
            throw new SSLProtocolException("Invalid " + this.type + " extension");
        }
        this.algorithms = new ArrayList<SignatureAndHashAlgorithm>();
        int remains = this.algorithmsLen;
        int sequence = 0;
        while (remains > 1) {
            final int hash = s.getInt8();
            final int signature = s.getInt8();
            final SignatureAndHashAlgorithm algorithm = SignatureAndHashAlgorithm.valueOf(hash, signature, ++sequence);
            this.algorithms.add(algorithm);
            remains -= 2;
        }
        if (remains != 0) {
            throw new SSLProtocolException("Invalid server_name extension");
        }
    }
    
    Collection<SignatureAndHashAlgorithm> getSignAlgorithms() {
        return this.algorithms;
    }
    
    @Override
    int length() {
        return 6 + this.algorithmsLen;
    }
    
    @Override
    void send(final HandshakeOutStream s) throws IOException {
        s.putInt16(this.type.id);
        s.putInt16(this.algorithmsLen + 2);
        s.putInt16(this.algorithmsLen);
        for (final SignatureAndHashAlgorithm algorithm : this.algorithms) {
            s.putInt8(algorithm.getHashValue());
            s.putInt8(algorithm.getSignatureValue());
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        boolean opened = false;
        for (final SignatureAndHashAlgorithm signAlg : this.algorithms) {
            if (opened) {
                buffer.append(", " + signAlg.getAlgorithmName());
            }
            else {
                buffer.append(signAlg.getAlgorithmName());
                opened = true;
            }
        }
        return "Extension " + this.type + ", signature_algorithms: " + (Object)buffer;
    }
}
