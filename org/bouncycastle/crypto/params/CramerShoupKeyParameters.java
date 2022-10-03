package org.bouncycastle.crypto.params;

public class CramerShoupKeyParameters extends AsymmetricKeyParameter
{
    private CramerShoupParameters params;
    
    protected CramerShoupKeyParameters(final boolean b, final CramerShoupParameters params) {
        super(b);
        this.params = params;
    }
    
    public CramerShoupParameters getParameters() {
        return this.params;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof CramerShoupKeyParameters)) {
            return false;
        }
        final CramerShoupKeyParameters cramerShoupKeyParameters = (CramerShoupKeyParameters)o;
        if (this.params == null) {
            return cramerShoupKeyParameters.getParameters() == null;
        }
        return this.params.equals(cramerShoupKeyParameters.getParameters());
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
