package org.openjsse.com.sun.crypto.provider;

import org.openjsse.sun.security.util.HexDumpEncoder;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.AlgorithmParametersSpi;

public final class ChaCha20Poly1305Parameters extends AlgorithmParametersSpi
{
    private static final String DEFAULT_FMT = "ASN.1";
    private byte[] nonce;
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
        if (!(paramSpec instanceof IvParameterSpec)) {
            throw new InvalidParameterSpecException("Inappropriate parameter specification");
        }
        final IvParameterSpec ivps = (IvParameterSpec)paramSpec;
        this.nonce = ivps.getIV();
        if (this.nonce.length != 12) {
            throw new InvalidParameterSpecException("ChaCha20-Poly1305 nonce must be 12 bytes in length");
        }
    }
    
    @Override
    protected void engineInit(final byte[] encoded) throws IOException {
        final DerValue val = new DerValue(encoded);
        this.nonce = val.getOctetString();
        if (this.nonce.length != 12) {
            throw new IOException("ChaCha20-Poly1305 nonce must be 12 bytes in length");
        }
    }
    
    @Override
    protected void engineInit(final byte[] encoded, final String decodingMethod) throws IOException {
        if (decodingMethod == null || decodingMethod.equalsIgnoreCase("ASN.1")) {
            this.engineInit(encoded);
            return;
        }
        throw new IOException("Unsupported parameter format: " + decodingMethod);
    }
    
    @Override
    protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(final Class<T> paramSpec) throws InvalidParameterSpecException {
        if (IvParameterSpec.class.isAssignableFrom(paramSpec)) {
            return paramSpec.cast(new IvParameterSpec(this.nonce));
        }
        throw new InvalidParameterSpecException("Inappropriate parameter specification");
    }
    
    @Override
    protected byte[] engineGetEncoded() throws IOException {
        final DerOutputStream out = new DerOutputStream();
        out.write((byte)4, this.nonce);
        return out.toByteArray();
    }
    
    @Override
    protected byte[] engineGetEncoded(final String encodingMethod) throws IOException {
        if (encodingMethod == null || encodingMethod.equalsIgnoreCase("ASN.1")) {
            return this.engineGetEncoded();
        }
        throw new IOException("Unsupported encoding format: " + encodingMethod);
    }
    
    @Override
    protected String engineToString() {
        final String LINE_SEP = System.lineSeparator();
        final HexDumpEncoder encoder = new HexDumpEncoder();
        final StringBuilder sb = new StringBuilder(LINE_SEP + "nonce:" + LINE_SEP + "[" + encoder.encodeBuffer(this.nonce) + "]");
        return sb.toString();
    }
}
