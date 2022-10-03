package org.bouncycastle.crypto.params;

public class ElGamalKeyParameters extends AsymmetricKeyParameter
{
    private ElGamalParameters params;
    
    protected ElGamalKeyParameters(final boolean b, final ElGamalParameters params) {
        super(b);
        this.params = params;
    }
    
    public ElGamalParameters getParameters() {
        return this.params;
    }
    
    @Override
    public int hashCode() {
        return (this.params != null) ? this.params.hashCode() : 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ElGamalKeyParameters)) {
            return false;
        }
        final ElGamalKeyParameters elGamalKeyParameters = (ElGamalKeyParameters)o;
        if (this.params == null) {
            return elGamalKeyParameters.getParameters() == null;
        }
        return this.params.equals(elGamalKeyParameters.getParameters());
    }
}
