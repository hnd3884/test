package org.bouncycastle.jcajce.provider.symmetric.util;

import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.generators.PKCS5S1ParametersGenerator;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;

public interface PBE
{
    public static final int MD5 = 0;
    public static final int SHA1 = 1;
    public static final int RIPEMD160 = 2;
    public static final int TIGER = 3;
    public static final int SHA256 = 4;
    public static final int MD2 = 5;
    public static final int GOST3411 = 6;
    public static final int SHA224 = 7;
    public static final int SHA384 = 8;
    public static final int SHA512 = 9;
    public static final int SHA3_224 = 10;
    public static final int SHA3_256 = 11;
    public static final int SHA3_384 = 12;
    public static final int SHA3_512 = 13;
    public static final int PKCS5S1 = 0;
    public static final int PKCS5S2 = 1;
    public static final int PKCS12 = 2;
    public static final int OPENSSL = 3;
    public static final int PKCS5S1_UTF8 = 4;
    public static final int PKCS5S2_UTF8 = 5;
    
    public static class Util
    {
        private static PBEParametersGenerator makePBEGenerator(final int n, final int n2) {
            PBEParametersGenerator pbeParametersGenerator = null;
            if (n == 0 || n == 4) {
                switch (n2) {
                    case 5: {
                        pbeParametersGenerator = new PKCS5S1ParametersGenerator(new MD2Digest());
                        break;
                    }
                    case 0: {
                        pbeParametersGenerator = new PKCS5S1ParametersGenerator(DigestFactory.createMD5());
                        break;
                    }
                    case 1: {
                        pbeParametersGenerator = new PKCS5S1ParametersGenerator(DigestFactory.createSHA1());
                        break;
                    }
                    default: {
                        throw new IllegalStateException("PKCS5 scheme 1 only supports MD2, MD5 and SHA1.");
                    }
                }
            }
            else if (n == 1 || n == 5) {
                switch (n2) {
                    case 5: {
                        pbeParametersGenerator = new PKCS5S2ParametersGenerator(new MD2Digest());
                        break;
                    }
                    case 0: {
                        pbeParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createMD5());
                        break;
                    }
                    case 1: {
                        pbeParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA1());
                        break;
                    }
                    case 2: {
                        pbeParametersGenerator = new PKCS5S2ParametersGenerator(new RIPEMD160Digest());
                        break;
                    }
                    case 3: {
                        pbeParametersGenerator = new PKCS5S2ParametersGenerator(new TigerDigest());
                        break;
                    }
                    case 4: {
                        pbeParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA256());
                        break;
                    }
                    case 6: {
                        pbeParametersGenerator = new PKCS5S2ParametersGenerator(new GOST3411Digest());
                        break;
                    }
                    case 7: {
                        pbeParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA224());
                        break;
                    }
                    case 8: {
                        pbeParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA384());
                        break;
                    }
                    case 9: {
                        pbeParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA512());
                        break;
                    }
                    case 10: {
                        pbeParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_224());
                        break;
                    }
                    case 11: {
                        pbeParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_256());
                        break;
                    }
                    case 12: {
                        pbeParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_384());
                        break;
                    }
                    case 13: {
                        pbeParametersGenerator = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_512());
                        break;
                    }
                    default: {
                        throw new IllegalStateException("unknown digest scheme for PBE PKCS5S2 encryption.");
                    }
                }
            }
            else if (n == 2) {
                switch (n2) {
                    case 5: {
                        pbeParametersGenerator = new PKCS12ParametersGenerator(new MD2Digest());
                        break;
                    }
                    case 0: {
                        pbeParametersGenerator = new PKCS12ParametersGenerator(DigestFactory.createMD5());
                        break;
                    }
                    case 1: {
                        pbeParametersGenerator = new PKCS12ParametersGenerator(DigestFactory.createSHA1());
                        break;
                    }
                    case 2: {
                        pbeParametersGenerator = new PKCS12ParametersGenerator(new RIPEMD160Digest());
                        break;
                    }
                    case 3: {
                        pbeParametersGenerator = new PKCS12ParametersGenerator(new TigerDigest());
                        break;
                    }
                    case 4: {
                        pbeParametersGenerator = new PKCS12ParametersGenerator(DigestFactory.createSHA256());
                        break;
                    }
                    case 6: {
                        pbeParametersGenerator = new PKCS12ParametersGenerator(new GOST3411Digest());
                        break;
                    }
                    case 7: {
                        pbeParametersGenerator = new PKCS12ParametersGenerator(DigestFactory.createSHA224());
                        break;
                    }
                    case 8: {
                        pbeParametersGenerator = new PKCS12ParametersGenerator(DigestFactory.createSHA384());
                        break;
                    }
                    case 9: {
                        pbeParametersGenerator = new PKCS12ParametersGenerator(DigestFactory.createSHA512());
                        break;
                    }
                    default: {
                        throw new IllegalStateException("unknown digest scheme for PBE encryption.");
                    }
                }
            }
            else {
                pbeParametersGenerator = new OpenSSLPBEParametersGenerator();
            }
            return pbeParametersGenerator;
        }
        
        public static CipherParameters makePBEParameters(final byte[] array, final int n, final int n2, final int n3, final int n4, final AlgorithmParameterSpec algorithmParameterSpec, final String s) throws InvalidAlgorithmParameterException {
            if (algorithmParameterSpec == null || !(algorithmParameterSpec instanceof PBEParameterSpec)) {
                throw new InvalidAlgorithmParameterException("Need a PBEParameter spec with a PBE key.");
            }
            final PBEParameterSpec pbeParameterSpec = (PBEParameterSpec)algorithmParameterSpec;
            final PBEParametersGenerator pbeGenerator = makePBEGenerator(n, n2);
            pbeGenerator.init(array, pbeParameterSpec.getSalt(), pbeParameterSpec.getIterationCount());
            CipherParameters cipherParameters;
            if (n4 != 0) {
                cipherParameters = pbeGenerator.generateDerivedParameters(n3, n4);
            }
            else {
                cipherParameters = pbeGenerator.generateDerivedParameters(n3);
            }
            if (s.startsWith("DES")) {
                if (cipherParameters instanceof ParametersWithIV) {
                    DESParameters.setOddParity(((KeyParameter)((ParametersWithIV)cipherParameters).getParameters()).getKey());
                }
                else {
                    DESParameters.setOddParity(((KeyParameter)cipherParameters).getKey());
                }
            }
            return cipherParameters;
        }
        
        public static CipherParameters makePBEParameters(final BCPBEKey bcpbeKey, final AlgorithmParameterSpec algorithmParameterSpec, final String s) {
            if (algorithmParameterSpec == null || !(algorithmParameterSpec instanceof PBEParameterSpec)) {
                throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
            }
            final PBEParameterSpec pbeParameterSpec = (PBEParameterSpec)algorithmParameterSpec;
            final PBEParametersGenerator pbeGenerator = makePBEGenerator(bcpbeKey.getType(), bcpbeKey.getDigest());
            byte[] encoded = bcpbeKey.getEncoded();
            if (bcpbeKey.shouldTryWrongPKCS12()) {
                encoded = new byte[2];
            }
            pbeGenerator.init(encoded, pbeParameterSpec.getSalt(), pbeParameterSpec.getIterationCount());
            CipherParameters cipherParameters;
            if (bcpbeKey.getIvSize() != 0) {
                cipherParameters = pbeGenerator.generateDerivedParameters(bcpbeKey.getKeySize(), bcpbeKey.getIvSize());
            }
            else {
                cipherParameters = pbeGenerator.generateDerivedParameters(bcpbeKey.getKeySize());
            }
            if (s.startsWith("DES")) {
                if (cipherParameters instanceof ParametersWithIV) {
                    DESParameters.setOddParity(((KeyParameter)((ParametersWithIV)cipherParameters).getParameters()).getKey());
                }
                else {
                    DESParameters.setOddParity(((KeyParameter)cipherParameters).getKey());
                }
            }
            return cipherParameters;
        }
        
        public static CipherParameters makePBEMacParameters(final BCPBEKey bcpbeKey, final AlgorithmParameterSpec algorithmParameterSpec) {
            if (algorithmParameterSpec == null || !(algorithmParameterSpec instanceof PBEParameterSpec)) {
                throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
            }
            final PBEParameterSpec pbeParameterSpec = (PBEParameterSpec)algorithmParameterSpec;
            final PBEParametersGenerator pbeGenerator = makePBEGenerator(bcpbeKey.getType(), bcpbeKey.getDigest());
            pbeGenerator.init(bcpbeKey.getEncoded(), pbeParameterSpec.getSalt(), pbeParameterSpec.getIterationCount());
            return pbeGenerator.generateDerivedMacParameters(bcpbeKey.getKeySize());
        }
        
        public static CipherParameters makePBEMacParameters(final PBEKeySpec pbeKeySpec, final int n, final int n2, final int n3) {
            final PBEParametersGenerator pbeGenerator = makePBEGenerator(n, n2);
            final byte[] convertPassword = convertPassword(n, pbeKeySpec);
            pbeGenerator.init(convertPassword, pbeKeySpec.getSalt(), pbeKeySpec.getIterationCount());
            final CipherParameters generateDerivedMacParameters = pbeGenerator.generateDerivedMacParameters(n3);
            for (int i = 0; i != convertPassword.length; ++i) {
                convertPassword[i] = 0;
            }
            return generateDerivedMacParameters;
        }
        
        public static CipherParameters makePBEParameters(final PBEKeySpec pbeKeySpec, final int n, final int n2, final int n3, final int n4) {
            final PBEParametersGenerator pbeGenerator = makePBEGenerator(n, n2);
            final byte[] convertPassword = convertPassword(n, pbeKeySpec);
            pbeGenerator.init(convertPassword, pbeKeySpec.getSalt(), pbeKeySpec.getIterationCount());
            CipherParameters cipherParameters;
            if (n4 != 0) {
                cipherParameters = pbeGenerator.generateDerivedParameters(n3, n4);
            }
            else {
                cipherParameters = pbeGenerator.generateDerivedParameters(n3);
            }
            for (int i = 0; i != convertPassword.length; ++i) {
                convertPassword[i] = 0;
            }
            return cipherParameters;
        }
        
        public static CipherParameters makePBEMacParameters(final SecretKey secretKey, final int n, final int n2, final int n3, final PBEParameterSpec pbeParameterSpec) {
            final PBEParametersGenerator pbeGenerator = makePBEGenerator(n, n2);
            final byte[] encoded = secretKey.getEncoded();
            pbeGenerator.init(secretKey.getEncoded(), pbeParameterSpec.getSalt(), pbeParameterSpec.getIterationCount());
            final CipherParameters generateDerivedMacParameters = pbeGenerator.generateDerivedMacParameters(n3);
            for (int i = 0; i != encoded.length; ++i) {
                encoded[i] = 0;
            }
            return generateDerivedMacParameters;
        }
        
        private static byte[] convertPassword(final int n, final PBEKeySpec pbeKeySpec) {
            byte[] array;
            if (n == 2) {
                array = PBEParametersGenerator.PKCS12PasswordToBytes(pbeKeySpec.getPassword());
            }
            else if (n == 5 || n == 4) {
                array = PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(pbeKeySpec.getPassword());
            }
            else {
                array = PBEParametersGenerator.PKCS5PasswordToBytes(pbeKeySpec.getPassword());
            }
            return array;
        }
    }
}
