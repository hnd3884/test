package org.bouncycastle.jcajce.spec;

import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import java.util.HashMap;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import java.util.Map;
import java.security.spec.AlgorithmParameterSpec;

public class GOST28147ParameterSpec implements AlgorithmParameterSpec
{
    private byte[] iv;
    private byte[] sBox;
    private static Map oidMappings;
    
    public GOST28147ParameterSpec(final byte[] array) {
        this.iv = null;
        this.sBox = null;
        System.arraycopy(array, 0, this.sBox = new byte[array.length], 0, array.length);
    }
    
    public GOST28147ParameterSpec(final byte[] array, final byte[] array2) {
        this(array);
        System.arraycopy(array2, 0, this.iv = new byte[array2.length], 0, array2.length);
    }
    
    public GOST28147ParameterSpec(final String s) {
        this.iv = null;
        this.sBox = null;
        this.sBox = GOST28147Engine.getSBox(s);
    }
    
    public GOST28147ParameterSpec(final String s, final byte[] array) {
        this(s);
        System.arraycopy(array, 0, this.iv = new byte[array.length], 0, array.length);
    }
    
    public GOST28147ParameterSpec(final ASN1ObjectIdentifier asn1ObjectIdentifier, final byte[] array) {
        this(getName(asn1ObjectIdentifier));
        this.iv = Arrays.clone(array);
    }
    
    @Deprecated
    public byte[] getSbox() {
        return Arrays.clone(this.sBox);
    }
    
    public byte[] getSBox() {
        return Arrays.clone(this.sBox);
    }
    
    public byte[] getIV() {
        return Arrays.clone(this.iv);
    }
    
    private static String getName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final String s = GOST28147ParameterSpec.oidMappings.get(asn1ObjectIdentifier);
        if (s == null) {
            throw new IllegalArgumentException("unknown OID: " + asn1ObjectIdentifier);
        }
        return s;
    }
    
    static {
        (GOST28147ParameterSpec.oidMappings = new HashMap()).put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, "E-A");
        GOST28147ParameterSpec.oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_B_ParamSet, "E-B");
        GOST28147ParameterSpec.oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_C_ParamSet, "E-C");
        GOST28147ParameterSpec.oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_D_ParamSet, "E-D");
    }
}
