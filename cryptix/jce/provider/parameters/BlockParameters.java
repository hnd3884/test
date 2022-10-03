package cryptix.jce.provider.parameters;

import java.io.IOException;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.AlgorithmParametersSpi;

public final class BlockParameters extends AlgorithmParametersSpi
{
    private String algorithm;
    private byte[] iv;
    
    protected final void engineInit(final AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
        if (paramSpec instanceof IvParameterSpec) {
            this.iv = ((IvParameterSpec)paramSpec).getIV();
            return;
        }
        throw new InvalidParameterSpecException("Wrong ParameterSpec");
    }
    
    protected final void engineInit(final byte[] params) throws IOException {
        this.iv = params;
    }
    
    protected final void engineInit(final byte[] params, final String format) throws IOException {
        throw new RuntimeException("Method init(byte[] params, String format) not implemented");
    }
    
    protected final AlgorithmParameterSpec engineGetParameterSpec(final Class paramSpec) throws InvalidParameterSpecException {
        try {
            if (!Class.forName("javax.crypto.spec.IvParameterSpec").isAssignableFrom(paramSpec)) {
                throw new InvalidParameterSpecException("Class is not IvParameterSpec assignable");
            }
        }
        catch (final ClassNotFoundException e) {
            throw new InvalidParameterSpecException("Class is not IvParameterSpec");
        }
        return new IvParameterSpec(this.iv);
    }
    
    protected final byte[] engineGetEncoded() throws IOException {
        return this.iv;
    }
    
    protected final byte[] engineGetEncoded(final String format) throws IOException {
        throw new RuntimeException("Method getEncoded(String format) not implemented");
    }
    
    protected final String engineToString() {
        return "iv:[" + this.hexDump(this.iv) + "]";
    }
    
    private final String hexDump(final byte[] b) {
        final char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        final char[] buf = new char[b.length * 2];
        int i;
        for (int j = i = 0; i < b.length; ++i) {
            final int k = b[i];
            buf[j++] = hex[k >>> 4 & 0xF];
            buf[j++] = hex[k & 0xF];
        }
        return new String(buf);
    }
    
    public BlockParameters() {
        this.iv = null;
    }
}
