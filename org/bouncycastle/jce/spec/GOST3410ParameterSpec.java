package org.bouncycastle.jce.spec;

import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST3410ParamSetParameters;
import org.bouncycastle.asn1.cryptopro.GOST3410NamedParameters;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jce.interfaces.GOST3410Params;
import java.security.spec.AlgorithmParameterSpec;

public class GOST3410ParameterSpec implements AlgorithmParameterSpec, GOST3410Params
{
    private GOST3410PublicKeyParameterSetSpec keyParameters;
    private String keyParamSetOID;
    private String digestParamSetOID;
    private String encryptionParamSetOID;
    
    public GOST3410ParameterSpec(String id, final String digestParamSetOID, final String encryptionParamSetOID) {
        GOST3410ParamSetParameters gost3410ParamSetParameters = null;
        try {
            gost3410ParamSetParameters = GOST3410NamedParameters.getByOID(new ASN1ObjectIdentifier(id));
        }
        catch (final IllegalArgumentException ex) {
            final ASN1ObjectIdentifier oid = GOST3410NamedParameters.getOID(id);
            if (oid != null) {
                id = oid.getId();
                gost3410ParamSetParameters = GOST3410NamedParameters.getByOID(oid);
            }
        }
        if (gost3410ParamSetParameters == null) {
            throw new IllegalArgumentException("no key parameter set for passed in name/OID.");
        }
        this.keyParameters = new GOST3410PublicKeyParameterSetSpec(gost3410ParamSetParameters.getP(), gost3410ParamSetParameters.getQ(), gost3410ParamSetParameters.getA());
        this.keyParamSetOID = id;
        this.digestParamSetOID = digestParamSetOID;
        this.encryptionParamSetOID = encryptionParamSetOID;
    }
    
    public GOST3410ParameterSpec(final String s, final String s2) {
        this(s, s2, null);
    }
    
    public GOST3410ParameterSpec(final String s) {
        this(s, CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet.getId(), null);
    }
    
    public GOST3410ParameterSpec(final GOST3410PublicKeyParameterSetSpec keyParameters) {
        this.keyParameters = keyParameters;
        this.digestParamSetOID = CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet.getId();
        this.encryptionParamSetOID = null;
    }
    
    public String getPublicKeyParamSetOID() {
        return this.keyParamSetOID;
    }
    
    public GOST3410PublicKeyParameterSetSpec getPublicKeyParameters() {
        return this.keyParameters;
    }
    
    public String getDigestParamSetOID() {
        return this.digestParamSetOID;
    }
    
    public String getEncryptionParamSetOID() {
        return this.encryptionParamSetOID;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof GOST3410ParameterSpec) {
            final GOST3410ParameterSpec gost3410ParameterSpec = (GOST3410ParameterSpec)o;
            return this.keyParameters.equals(gost3410ParameterSpec.keyParameters) && this.digestParamSetOID.equals(gost3410ParameterSpec.digestParamSetOID) && (this.encryptionParamSetOID == gost3410ParameterSpec.encryptionParamSetOID || (this.encryptionParamSetOID != null && this.encryptionParamSetOID.equals(gost3410ParameterSpec.encryptionParamSetOID)));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.keyParameters.hashCode() ^ this.digestParamSetOID.hashCode() ^ ((this.encryptionParamSetOID != null) ? this.encryptionParamSetOID.hashCode() : 0);
    }
    
    public static GOST3410ParameterSpec fromPublicKeyAlg(final GOST3410PublicKeyAlgParameters gost3410PublicKeyAlgParameters) {
        if (gost3410PublicKeyAlgParameters.getEncryptionParamSet() != null) {
            return new GOST3410ParameterSpec(gost3410PublicKeyAlgParameters.getPublicKeyParamSet().getId(), gost3410PublicKeyAlgParameters.getDigestParamSet().getId(), gost3410PublicKeyAlgParameters.getEncryptionParamSet().getId());
        }
        return new GOST3410ParameterSpec(gost3410PublicKeyAlgParameters.getPublicKeyParamSet().getId(), gost3410PublicKeyAlgParameters.getDigestParamSet().getId());
    }
}
