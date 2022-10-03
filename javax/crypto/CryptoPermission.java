package javax.crypto;

import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import java.security.PermissionCollection;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Permission;

class CryptoPermission extends Permission
{
    private static final long serialVersionUID = 8987399626114087514L;
    private String alg;
    private int maxKeySize;
    private String exemptionMechanism;
    private AlgorithmParameterSpec algParamSpec;
    private boolean checkParam;
    static final String ALG_NAME_WILDCARD = "*";
    
    CryptoPermission(final String alg) {
        super(null);
        this.maxKeySize = Integer.MAX_VALUE;
        this.exemptionMechanism = null;
        this.algParamSpec = null;
        this.checkParam = false;
        this.alg = alg;
    }
    
    CryptoPermission(final String alg, final int maxKeySize) {
        super(null);
        this.maxKeySize = Integer.MAX_VALUE;
        this.exemptionMechanism = null;
        this.algParamSpec = null;
        this.checkParam = false;
        this.alg = alg;
        this.maxKeySize = maxKeySize;
    }
    
    CryptoPermission(final String alg, final int maxKeySize, final AlgorithmParameterSpec algParamSpec) {
        super(null);
        this.maxKeySize = Integer.MAX_VALUE;
        this.exemptionMechanism = null;
        this.algParamSpec = null;
        this.checkParam = false;
        this.alg = alg;
        this.maxKeySize = maxKeySize;
        this.checkParam = true;
        this.algParamSpec = algParamSpec;
    }
    
    CryptoPermission(final String alg, final String exemptionMechanism) {
        super(null);
        this.maxKeySize = Integer.MAX_VALUE;
        this.exemptionMechanism = null;
        this.algParamSpec = null;
        this.checkParam = false;
        this.alg = alg;
        this.exemptionMechanism = exemptionMechanism;
    }
    
    CryptoPermission(final String alg, final int maxKeySize, final String exemptionMechanism) {
        super(null);
        this.maxKeySize = Integer.MAX_VALUE;
        this.exemptionMechanism = null;
        this.algParamSpec = null;
        this.checkParam = false;
        this.alg = alg;
        this.exemptionMechanism = exemptionMechanism;
        this.maxKeySize = maxKeySize;
    }
    
    CryptoPermission(final String alg, final int maxKeySize, final AlgorithmParameterSpec algParamSpec, final String exemptionMechanism) {
        super(null);
        this.maxKeySize = Integer.MAX_VALUE;
        this.exemptionMechanism = null;
        this.algParamSpec = null;
        this.checkParam = false;
        this.alg = alg;
        this.exemptionMechanism = exemptionMechanism;
        this.maxKeySize = maxKeySize;
        this.checkParam = true;
        this.algParamSpec = algParamSpec;
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof CryptoPermission)) {
            return false;
        }
        final CryptoPermission cryptoPermission = (CryptoPermission)permission;
        if (!this.alg.equalsIgnoreCase(cryptoPermission.alg) && !this.alg.equalsIgnoreCase("*")) {
            return false;
        }
        if (cryptoPermission.maxKeySize <= this.maxKeySize) {
            if (!this.impliesParameterSpec(cryptoPermission.checkParam, cryptoPermission.algParamSpec)) {
                return false;
            }
            if (this.impliesExemptionMechanism(cryptoPermission.exemptionMechanism)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CryptoPermission)) {
            return false;
        }
        final CryptoPermission cryptoPermission = (CryptoPermission)o;
        return this.alg.equalsIgnoreCase(cryptoPermission.alg) && this.maxKeySize == cryptoPermission.maxKeySize && this.checkParam == cryptoPermission.checkParam && this.equalObjects(this.exemptionMechanism, cryptoPermission.exemptionMechanism) && this.equalObjects(this.algParamSpec, cryptoPermission.algParamSpec);
    }
    
    @Override
    public int hashCode() {
        int n = this.alg.hashCode() ^ this.maxKeySize;
        if (this.exemptionMechanism != null) {
            n ^= this.exemptionMechanism.hashCode();
        }
        if (this.checkParam) {
            n ^= 0x64;
        }
        if (this.algParamSpec != null) {
            n ^= this.algParamSpec.hashCode();
        }
        return n;
    }
    
    @Override
    public String getActions() {
        return null;
    }
    
    @Override
    public PermissionCollection newPermissionCollection() {
        return new CryptoPermissionCollection();
    }
    
    final String getAlgorithm() {
        return this.alg;
    }
    
    final String getExemptionMechanism() {
        return this.exemptionMechanism;
    }
    
    final int getMaxKeySize() {
        return this.maxKeySize;
    }
    
    final boolean getCheckParam() {
        return this.checkParam;
    }
    
    final AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return this.algParamSpec;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(100);
        sb.append("(CryptoPermission " + this.alg + " " + this.maxKeySize);
        if (this.algParamSpec != null) {
            if (this.algParamSpec instanceof RC2ParameterSpec) {
                sb.append(" , effective " + ((RC2ParameterSpec)this.algParamSpec).getEffectiveKeyBits());
            }
            else if (this.algParamSpec instanceof RC5ParameterSpec) {
                sb.append(" , rounds " + ((RC5ParameterSpec)this.algParamSpec).getRounds());
            }
        }
        if (this.exemptionMechanism != null) {
            sb.append(" " + this.exemptionMechanism);
        }
        sb.append(")");
        return sb.toString();
    }
    
    private boolean impliesExemptionMechanism(final String s) {
        return this.exemptionMechanism == null || (s != null && this.exemptionMechanism.equals(s));
    }
    
    private boolean impliesParameterSpec(final boolean b, final AlgorithmParameterSpec algorithmParameterSpec) {
        if (this.checkParam && b) {
            return algorithmParameterSpec == null || (this.algParamSpec != null && this.algParamSpec.getClass() == algorithmParameterSpec.getClass() && ((algorithmParameterSpec instanceof RC2ParameterSpec && ((RC2ParameterSpec)algorithmParameterSpec).getEffectiveKeyBits() <= ((RC2ParameterSpec)this.algParamSpec).getEffectiveKeyBits()) || (algorithmParameterSpec instanceof RC5ParameterSpec && ((RC5ParameterSpec)algorithmParameterSpec).getRounds() <= ((RC5ParameterSpec)this.algParamSpec).getRounds()) || (algorithmParameterSpec instanceof PBEParameterSpec && ((PBEParameterSpec)algorithmParameterSpec).getIterationCount() <= ((PBEParameterSpec)this.algParamSpec).getIterationCount()) || this.algParamSpec.equals(algorithmParameterSpec)));
        }
        return !this.checkParam;
    }
    
    private boolean equalObjects(final Object o, final Object o2) {
        if (o == null) {
            return o2 == null;
        }
        return o.equals(o2);
    }
}
