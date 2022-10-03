package org.bouncycastle.crypto.params;

public class DHKeyParameters extends AsymmetricKeyParameter
{
    private DHParameters params;
    
    protected DHKeyParameters(final boolean b, final DHParameters params) {
        super(b);
        this.params = params;
    }
    
    public DHParameters getParameters() {
        return this.params;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DHKeyParameters)) {
            return false;
        }
        final DHKeyParameters dhKeyParameters = (DHKeyParameters)o;
        if (this.params == null) {
            return dhKeyParameters.getParameters() == null;
        }
        return this.params.equals(dhKeyParameters.getParameters());
    }
    
    @Override
    public int hashCode() {
        int n = this.isPrivate() ? 0 : 1;
        if (this.params != null) {
            n ^= this.params.hashCode();
        }
        return n;
    }
}
