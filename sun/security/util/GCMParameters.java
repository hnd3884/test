package sun.security.util;

import sun.misc.HexDumpEncoder;
import java.io.IOException;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.GCMParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.AlgorithmParametersSpi;

public final class GCMParameters extends AlgorithmParametersSpi
{
    private byte[] iv;
    private int tLen;
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (!(algorithmParameterSpec instanceof GCMParameterSpec)) {
            throw new InvalidParameterSpecException("Inappropriate parameter specification");
        }
        final GCMParameterSpec gcmParameterSpec = (GCMParameterSpec)algorithmParameterSpec;
        this.tLen = gcmParameterSpec.getTLen() / 8;
        if (this.tLen < 12 || this.tLen > 16) {
            throw new InvalidParameterSpecException("GCM parameter parsing error: unsupported tag len: " + this.tLen);
        }
        this.iv = gcmParameterSpec.getIV();
    }
    
    @Override
    protected void engineInit(final byte[] array) throws IOException {
        final DerValue derValue = new DerValue(array);
        if (derValue.tag == 48) {
            final byte[] octetString = derValue.data.getOctetString();
            int integer;
            if (derValue.data.available() != 0) {
                integer = derValue.data.getInteger();
                if (integer < 12 || integer > 16) {
                    throw new IOException("GCM parameter parsing error: unsupported tag len: " + integer);
                }
                if (derValue.data.available() != 0) {
                    throw new IOException("GCM parameter parsing error: extra data");
                }
            }
            else {
                integer = 12;
            }
            this.iv = octetString.clone();
            this.tLen = integer;
            return;
        }
        throw new IOException("GCM parameter parsing error: no SEQ tag");
    }
    
    @Override
    protected void engineInit(final byte[] array, final String s) throws IOException {
        this.engineInit(array);
    }
    
    @Override
    protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(final Class<T> clazz) throws InvalidParameterSpecException {
        if (GCMParameterSpec.class.isAssignableFrom(clazz)) {
            return clazz.cast(new GCMParameterSpec(this.tLen * 8, this.iv));
        }
        throw new InvalidParameterSpecException("Inappropriate parameter specification");
    }
    
    @Override
    protected byte[] engineGetEncoded() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putOctetString(this.iv);
        if (this.tLen != 12) {
            derOutputStream2.putInteger(this.tLen);
        }
        derOutputStream.write((byte)48, derOutputStream2);
        return derOutputStream.toByteArray();
    }
    
    @Override
    protected byte[] engineGetEncoded(final String s) throws IOException {
        return this.engineGetEncoded();
    }
    
    @Override
    protected String engineToString() {
        final String lineSeparator = System.lineSeparator();
        final StringBuilder sb = new StringBuilder(lineSeparator + "    iv:" + lineSeparator + "[" + new HexDumpEncoder().encodeBuffer(this.iv) + "]");
        sb.append(lineSeparator + "tLen(bits):" + lineSeparator + this.tLen * 8 + lineSeparator);
        return sb.toString();
    }
}
