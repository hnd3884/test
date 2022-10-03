package org.bouncycastle.jcajce.provider.symmetric.util;

import java.util.Iterator;
import java.util.Hashtable;
import java.util.Map;
import org.bouncycastle.crypto.params.SkeinParameters;
import org.bouncycastle.jcajce.spec.SkeinParameterSpec;
import org.bouncycastle.crypto.params.RC2Parameters;
import javax.crypto.spec.RC2ParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.macs.HMac;
import javax.crypto.interfaces.PBEKey;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.SecretKey;
import org.bouncycastle.jcajce.PKCS12Key;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import org.bouncycastle.crypto.Mac;
import javax.crypto.MacSpi;

public class BaseMac extends MacSpi implements PBE
{
    private static final Class gcmSpecClass;
    private Mac macEngine;
    private int scheme;
    private int pbeHash;
    private int keySize;
    
    protected BaseMac(final Mac macEngine) {
        this.scheme = 2;
        this.pbeHash = 1;
        this.keySize = 160;
        this.macEngine = macEngine;
    }
    
    protected BaseMac(final Mac macEngine, final int scheme, final int pbeHash, final int keySize) {
        this.scheme = 2;
        this.pbeHash = 1;
        this.keySize = 160;
        this.macEngine = macEngine;
        this.scheme = scheme;
        this.pbeHash = pbeHash;
        this.keySize = keySize;
    }
    
    @Override
    protected void engineInit(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (key == null) {
            throw new InvalidKeyException("key is null");
        }
        CipherParameters cipherParameters;
        if (key instanceof PKCS12Key) {
            SecretKey secretKey;
            try {
                secretKey = (SecretKey)key;
            }
            catch (final Exception ex) {
                throw new InvalidKeyException("PKCS12 requires a SecretKey/PBEKey");
            }
            PBEParameterSpec pbeParameterSpec;
            try {
                pbeParameterSpec = (PBEParameterSpec)algorithmParameterSpec;
            }
            catch (final Exception ex2) {
                throw new InvalidAlgorithmParameterException("PKCS12 requires a PBEParameterSpec");
            }
            if (secretKey instanceof PBEKey && pbeParameterSpec == null) {
                pbeParameterSpec = new PBEParameterSpec(((PBEKey)secretKey).getSalt(), ((PBEKey)secretKey).getIterationCount());
            }
            int n = 1;
            int n2 = 160;
            if (this.macEngine.getAlgorithmName().startsWith("GOST")) {
                n = 6;
                n2 = 256;
            }
            else if (this.macEngine instanceof HMac && !this.macEngine.getAlgorithmName().startsWith("SHA-1")) {
                if (this.macEngine.getAlgorithmName().startsWith("SHA-224")) {
                    n = 7;
                    n2 = 224;
                }
                else if (this.macEngine.getAlgorithmName().startsWith("SHA-256")) {
                    n = 4;
                    n2 = 256;
                }
                else if (this.macEngine.getAlgorithmName().startsWith("SHA-384")) {
                    n = 8;
                    n2 = 384;
                }
                else if (this.macEngine.getAlgorithmName().startsWith("SHA-512")) {
                    n = 9;
                    n2 = 512;
                }
                else {
                    if (!this.macEngine.getAlgorithmName().startsWith("RIPEMD160")) {
                        throw new InvalidAlgorithmParameterException("no PKCS12 mapping for HMAC: " + this.macEngine.getAlgorithmName());
                    }
                    n = 2;
                    n2 = 160;
                }
            }
            cipherParameters = Util.makePBEMacParameters(secretKey, 2, n, n2, pbeParameterSpec);
        }
        else if (key instanceof BCPBEKey) {
            final BCPBEKey bcpbeKey = (BCPBEKey)key;
            if (bcpbeKey.getParam() != null) {
                cipherParameters = bcpbeKey.getParam();
            }
            else {
                if (!(algorithmParameterSpec instanceof PBEParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
                }
                cipherParameters = Util.makePBEMacParameters(bcpbeKey, algorithmParameterSpec);
            }
        }
        else {
            if (algorithmParameterSpec instanceof PBEParameterSpec) {
                throw new InvalidAlgorithmParameterException("inappropriate parameter type: " + algorithmParameterSpec.getClass().getName());
            }
            cipherParameters = new KeyParameter(key.getEncoded());
        }
        KeyParameter keyParameter;
        if (cipherParameters instanceof ParametersWithIV) {
            keyParameter = (KeyParameter)((ParametersWithIV)cipherParameters).getParameters();
        }
        else {
            keyParameter = (KeyParameter)cipherParameters;
        }
        Label_0821: {
            if (algorithmParameterSpec instanceof AEADParameterSpec) {
                final AEADParameterSpec aeadParameterSpec = (AEADParameterSpec)algorithmParameterSpec;
                cipherParameters = new AEADParameters(keyParameter, aeadParameterSpec.getMacSizeInBits(), aeadParameterSpec.getNonce(), aeadParameterSpec.getAssociatedData());
            }
            else if (algorithmParameterSpec instanceof IvParameterSpec) {
                cipherParameters = new ParametersWithIV(keyParameter, ((IvParameterSpec)algorithmParameterSpec).getIV());
            }
            else if (algorithmParameterSpec instanceof RC2ParameterSpec) {
                cipherParameters = new ParametersWithIV(new RC2Parameters(keyParameter.getKey(), ((RC2ParameterSpec)algorithmParameterSpec).getEffectiveKeyBits()), ((RC2ParameterSpec)algorithmParameterSpec).getIV());
            }
            else if (algorithmParameterSpec instanceof SkeinParameterSpec) {
                cipherParameters = new SkeinParameters.Builder(copyMap(((SkeinParameterSpec)algorithmParameterSpec).getParameters())).setKey(keyParameter.getKey()).build();
            }
            else if (algorithmParameterSpec == null) {
                cipherParameters = new KeyParameter(key.getEncoded());
            }
            else {
                if (BaseMac.gcmSpecClass != null && BaseMac.gcmSpecClass.isAssignableFrom(algorithmParameterSpec.getClass())) {
                    try {
                        cipherParameters = new AEADParameters(keyParameter, (int)BaseMac.gcmSpecClass.getDeclaredMethod("getTLen", (Class[])new Class[0]).invoke(algorithmParameterSpec, new Object[0]), (byte[])BaseMac.gcmSpecClass.getDeclaredMethod("getIV", (Class[])new Class[0]).invoke(algorithmParameterSpec, new Object[0]));
                        break Label_0821;
                    }
                    catch (final Exception ex3) {
                        throw new InvalidAlgorithmParameterException("Cannot process GCMParameterSpec.");
                    }
                }
                if (!(algorithmParameterSpec instanceof PBEParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("unknown parameter type: " + algorithmParameterSpec.getClass().getName());
                }
            }
            try {
                this.macEngine.init(cipherParameters);
            }
            catch (final Exception ex4) {
                throw new InvalidAlgorithmParameterException("cannot initialize MAC: " + ex4.getMessage());
            }
        }
    }
    
    @Override
    protected int engineGetMacLength() {
        return this.macEngine.getMacSize();
    }
    
    @Override
    protected void engineReset() {
        this.macEngine.reset();
    }
    
    @Override
    protected void engineUpdate(final byte b) {
        this.macEngine.update(b);
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) {
        this.macEngine.update(array, n, n2);
    }
    
    @Override
    protected byte[] engineDoFinal() {
        final byte[] array = new byte[this.engineGetMacLength()];
        this.macEngine.doFinal(array, 0);
        return array;
    }
    
    private static Hashtable copyMap(final Map map) {
        final Hashtable hashtable = new Hashtable();
        for (final Object next : map.keySet()) {
            hashtable.put(next, map.get(next));
        }
        return hashtable;
    }
    
    static {
        gcmSpecClass = ClassUtil.loadClass(BaseMac.class, "javax.crypto.spec.GCMParameterSpec");
    }
}
