package org.bouncycastle.jcajce.provider.symmetric.util;

import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import java.security.spec.KeySpec;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import javax.crypto.interfaces.PBEKey;

public class BCPBEKey implements PBEKey
{
    String algorithm;
    ASN1ObjectIdentifier oid;
    int type;
    int digest;
    int keySize;
    int ivSize;
    CipherParameters param;
    PBEKeySpec pbeKeySpec;
    boolean tryWrong;
    
    public BCPBEKey(final String algorithm, final ASN1ObjectIdentifier oid, final int type, final int digest, final int keySize, final int ivSize, final PBEKeySpec pbeKeySpec, final CipherParameters param) {
        this.tryWrong = false;
        this.algorithm = algorithm;
        this.oid = oid;
        this.type = type;
        this.digest = digest;
        this.keySize = keySize;
        this.ivSize = ivSize;
        this.pbeKeySpec = pbeKeySpec;
        this.param = param;
    }
    
    public BCPBEKey(final String algorithm, final KeySpec keySpec, final CipherParameters param) {
        this.tryWrong = false;
        this.algorithm = algorithm;
        this.param = param;
    }
    
    public String getAlgorithm() {
        return this.algorithm;
    }
    
    public String getFormat() {
        return "RAW";
    }
    
    public byte[] getEncoded() {
        if (this.param != null) {
            KeyParameter keyParameter;
            if (this.param instanceof ParametersWithIV) {
                keyParameter = (KeyParameter)((ParametersWithIV)this.param).getParameters();
            }
            else {
                keyParameter = (KeyParameter)this.param;
            }
            return keyParameter.getKey();
        }
        if (this.type == 2) {
            return PBEParametersGenerator.PKCS12PasswordToBytes(this.pbeKeySpec.getPassword());
        }
        if (this.type == 5) {
            return PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(this.pbeKeySpec.getPassword());
        }
        return PBEParametersGenerator.PKCS5PasswordToBytes(this.pbeKeySpec.getPassword());
    }
    
    int getType() {
        return this.type;
    }
    
    int getDigest() {
        return this.digest;
    }
    
    int getKeySize() {
        return this.keySize;
    }
    
    public int getIvSize() {
        return this.ivSize;
    }
    
    public CipherParameters getParam() {
        return this.param;
    }
    
    public char[] getPassword() {
        return this.pbeKeySpec.getPassword();
    }
    
    public byte[] getSalt() {
        return this.pbeKeySpec.getSalt();
    }
    
    public int getIterationCount() {
        return this.pbeKeySpec.getIterationCount();
    }
    
    public ASN1ObjectIdentifier getOID() {
        return this.oid;
    }
    
    public void setTryWrongPKCS12Zero(final boolean tryWrong) {
        this.tryWrong = tryWrong;
    }
    
    boolean shouldTryWrongPKCS12() {
        return this.tryWrong;
    }
}
