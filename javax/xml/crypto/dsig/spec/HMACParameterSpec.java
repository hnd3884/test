package javax.xml.crypto.dsig.spec;

public final class HMACParameterSpec implements SignatureMethodParameterSpec
{
    private int outputLength;
    
    public HMACParameterSpec(final int outputLength) {
        this.outputLength = outputLength;
    }
    
    public int getOutputLength() {
        return this.outputLength;
    }
}
