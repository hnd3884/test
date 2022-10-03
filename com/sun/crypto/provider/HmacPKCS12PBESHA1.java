package com.sun.crypto.provider;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.util.Arrays;
import java.security.InvalidKeyException;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.PBEKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public final class HmacPKCS12PBESHA1 extends HmacCore
{
    public HmacPKCS12PBESHA1() throws NoSuchAlgorithmException {
        super("SHA1", 64);
    }
    
    @Override
    protected void engineInit(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        byte[] array = null;
        int n = 0;
        char[] password;
        if (key instanceof PBEKey) {
            final Object o = key;
            password = ((PBEKey)o).getPassword();
            array = ((PBEKey)o).getSalt();
            n = ((PBEKey)o).getIterationCount();
        }
        else {
            if (!(key instanceof SecretKey)) {
                throw new InvalidKeyException("SecretKey of PBE type required");
            }
            final Object o;
            if (!key.getAlgorithm().regionMatches(true, 0, "PBE", 0, 3) || (o = key.getEncoded()) == null) {
                throw new InvalidKeyException("Missing password");
            }
            password = new char[((PBEKey)o).length];
            for (int i = 0; i < password.length; ++i) {
                password[i] = (char)(o[i] & 0x7F);
            }
            Arrays.fill((byte[])o, (byte)0);
        }
        Object o;
        try {
            if (algorithmParameterSpec == null) {
                if (array == null || n == 0) {
                    throw new InvalidAlgorithmParameterException("PBEParameterSpec required for salt and iteration count");
                }
            }
            else {
                if (!(algorithmParameterSpec instanceof PBEParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("PBEParameterSpec type required");
                }
                final PBEParameterSpec pbeParameterSpec = (PBEParameterSpec)algorithmParameterSpec;
                if (array != null) {
                    if (!Arrays.equals(array, pbeParameterSpec.getSalt())) {
                        throw new InvalidAlgorithmParameterException("Inconsistent value of salt between key and params");
                    }
                }
                else {
                    array = pbeParameterSpec.getSalt();
                }
                if (n != 0) {
                    if (n != pbeParameterSpec.getIterationCount()) {
                        throw new InvalidAlgorithmParameterException("Different iteration count between key and params");
                    }
                }
                else {
                    n = pbeParameterSpec.getIterationCount();
                }
            }
            if (array.length < 8) {
                throw new InvalidAlgorithmParameterException("Salt must be at least 8 bytes long");
            }
            if (n <= 0) {
                throw new InvalidAlgorithmParameterException("IterationCount must be a positive number");
            }
            o = PKCS12PBECipherCore.derive(password, array, n, this.engineGetMacLength(), 3);
        }
        finally {
            Arrays.fill(password, '\0');
        }
        super.engineInit(new SecretKeySpec((byte[])o, "HmacSHA1"), null);
    }
}
