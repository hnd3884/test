package org.bouncycastle.jce.provider;

import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.generators.PKCS5S1ParametersGenerator;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;

public interface BrokenPBE
{
    public static final int MD5 = 0;
    public static final int SHA1 = 1;
    public static final int RIPEMD160 = 2;
    public static final int PKCS5S1 = 0;
    public static final int PKCS5S2 = 1;
    public static final int PKCS12 = 2;
    public static final int OLD_PKCS12 = 3;
    
    public static class Util
    {
        private static void setOddParity(final byte[] array) {
            for (int i = 0; i < array.length; ++i) {
                final byte b = array[i];
                array[i] = (byte)((b & 0xFE) | (b >> 1 ^ b >> 2 ^ b >> 3 ^ b >> 4 ^ b >> 5 ^ b >> 6 ^ b >> 7 ^ 0x1));
            }
        }
        
        private static PBEParametersGenerator makePBEGenerator(final int n, final int n2) {
            PBEParametersGenerator pbeParametersGenerator = null;
            if (n == 0) {
                switch (n2) {
                    case 0: {
                        pbeParametersGenerator = new PKCS5S1ParametersGenerator(new MD5Digest());
                        break;
                    }
                    case 1: {
                        pbeParametersGenerator = new PKCS5S1ParametersGenerator(new SHA1Digest());
                        break;
                    }
                    default: {
                        throw new IllegalStateException("PKCS5 scheme 1 only supports only MD5 and SHA1.");
                    }
                }
            }
            else if (n == 1) {
                pbeParametersGenerator = new PKCS5S2ParametersGenerator();
            }
            else if (n == 3) {
                switch (n2) {
                    case 0: {
                        pbeParametersGenerator = new OldPKCS12ParametersGenerator(new MD5Digest());
                        break;
                    }
                    case 1: {
                        pbeParametersGenerator = new OldPKCS12ParametersGenerator(new SHA1Digest());
                        break;
                    }
                    case 2: {
                        pbeParametersGenerator = new OldPKCS12ParametersGenerator(new RIPEMD160Digest());
                        break;
                    }
                    default: {
                        throw new IllegalStateException("unknown digest scheme for PBE encryption.");
                    }
                }
            }
            else {
                switch (n2) {
                    case 0: {
                        pbeParametersGenerator = new PKCS12ParametersGenerator(new MD5Digest());
                        break;
                    }
                    case 1: {
                        pbeParametersGenerator = new PKCS12ParametersGenerator(new SHA1Digest());
                        break;
                    }
                    case 2: {
                        pbeParametersGenerator = new PKCS12ParametersGenerator(new RIPEMD160Digest());
                        break;
                    }
                    default: {
                        throw new IllegalStateException("unknown digest scheme for PBE encryption.");
                    }
                }
            }
            return pbeParametersGenerator;
        }
        
        static CipherParameters makePBEParameters(final BCPBEKey bcpbeKey, final AlgorithmParameterSpec algorithmParameterSpec, final int n, final int n2, final String s, final int n3, final int n4) {
            if (algorithmParameterSpec == null || !(algorithmParameterSpec instanceof PBEParameterSpec)) {
                throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
            }
            final PBEParameterSpec pbeParameterSpec = (PBEParameterSpec)algorithmParameterSpec;
            final PBEParametersGenerator pbeGenerator = makePBEGenerator(n, n2);
            final byte[] encoded = bcpbeKey.getEncoded();
            pbeGenerator.init(encoded, pbeParameterSpec.getSalt(), pbeParameterSpec.getIterationCount());
            CipherParameters cipherParameters;
            if (n4 != 0) {
                cipherParameters = pbeGenerator.generateDerivedParameters(n3, n4);
            }
            else {
                cipherParameters = pbeGenerator.generateDerivedParameters(n3);
            }
            if (s.startsWith("DES")) {
                if (cipherParameters instanceof ParametersWithIV) {
                    setOddParity(((KeyParameter)((ParametersWithIV)cipherParameters).getParameters()).getKey());
                }
                else {
                    setOddParity(((KeyParameter)cipherParameters).getKey());
                }
            }
            for (int i = 0; i != encoded.length; ++i) {
                encoded[i] = 0;
            }
            return cipherParameters;
        }
        
        static CipherParameters makePBEMacParameters(final BCPBEKey bcpbeKey, final AlgorithmParameterSpec algorithmParameterSpec, final int n, final int n2, final int n3) {
            if (algorithmParameterSpec == null || !(algorithmParameterSpec instanceof PBEParameterSpec)) {
                throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
            }
            final PBEParameterSpec pbeParameterSpec = (PBEParameterSpec)algorithmParameterSpec;
            final PBEParametersGenerator pbeGenerator = makePBEGenerator(n, n2);
            final byte[] encoded = bcpbeKey.getEncoded();
            pbeGenerator.init(encoded, pbeParameterSpec.getSalt(), pbeParameterSpec.getIterationCount());
            final CipherParameters generateDerivedMacParameters = pbeGenerator.generateDerivedMacParameters(n3);
            for (int i = 0; i != encoded.length; ++i) {
                encoded[i] = 0;
            }
            return generateDerivedMacParameters;
        }
    }
}
