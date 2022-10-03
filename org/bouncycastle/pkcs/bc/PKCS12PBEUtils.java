package org.bouncycastle.pkcs.bc;

import org.bouncycastle.util.Integers;
import java.util.HashSet;
import java.util.HashMap;
import org.bouncycastle.crypto.params.DESedeParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.io.MacOutputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Set;
import java.util.Map;

class PKCS12PBEUtils
{
    private static Map keySizes;
    private static Set noIvAlgs;
    private static Set desAlgs;
    
    static int getKeySize(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return PKCS12PBEUtils.keySizes.get(asn1ObjectIdentifier);
    }
    
    static boolean hasNoIv(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return PKCS12PBEUtils.noIvAlgs.contains(asn1ObjectIdentifier);
    }
    
    static boolean isDesAlg(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return PKCS12PBEUtils.desAlgs.contains(asn1ObjectIdentifier);
    }
    
    static PaddedBufferedBlockCipher getEngine(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        Object o;
        if (asn1ObjectIdentifier.equals((Object)PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC) || asn1ObjectIdentifier.equals((Object)PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC)) {
            o = new DESedeEngine();
        }
        else {
            if (!asn1ObjectIdentifier.equals((Object)PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC) && !asn1ObjectIdentifier.equals((Object)PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC)) {
                throw new IllegalStateException("unknown algorithm");
            }
            o = new RC2Engine();
        }
        return new PaddedBufferedBlockCipher((BlockCipher)new CBCBlockCipher((BlockCipher)o), (BlockCipherPadding)new PKCS7Padding());
    }
    
    static MacCalculator createMacCalculator(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ExtendedDigest extendedDigest, final PKCS12PBEParams pkcs12PBEParams, final char[] array) {
        final PKCS12ParametersGenerator pkcs12ParametersGenerator = new PKCS12ParametersGenerator((Digest)extendedDigest);
        pkcs12ParametersGenerator.init(PKCS12ParametersGenerator.PKCS12PasswordToBytes(array), pkcs12PBEParams.getIV(), pkcs12PBEParams.getIterations().intValue());
        final KeyParameter keyParameter = (KeyParameter)pkcs12ParametersGenerator.generateDerivedMacParameters(extendedDigest.getDigestSize() * 8);
        final HMac hMac = new HMac((Digest)extendedDigest);
        hMac.init((CipherParameters)keyParameter);
        return new MacCalculator() {
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)pkcs12PBEParams);
            }
            
            public OutputStream getOutputStream() {
                return (OutputStream)new MacOutputStream((Mac)hMac);
            }
            
            public byte[] getMac() {
                final byte[] array = new byte[hMac.getMacSize()];
                hMac.doFinal(array, 0);
                return array;
            }
            
            public GenericKey getKey() {
                return new GenericKey(this.getAlgorithmIdentifier(), PKCS12ParametersGenerator.PKCS12PasswordToBytes(array));
            }
        };
    }
    
    static CipherParameters createCipherParameters(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ExtendedDigest extendedDigest, final int n, final PKCS12PBEParams pkcs12PBEParams, final char[] array) {
        final PKCS12ParametersGenerator pkcs12ParametersGenerator = new PKCS12ParametersGenerator((Digest)extendedDigest);
        pkcs12ParametersGenerator.init(PKCS12ParametersGenerator.PKCS12PasswordToBytes(array), pkcs12PBEParams.getIV(), pkcs12PBEParams.getIterations().intValue());
        CipherParameters cipherParameters;
        if (hasNoIv(asn1ObjectIdentifier)) {
            cipherParameters = pkcs12ParametersGenerator.generateDerivedParameters(getKeySize(asn1ObjectIdentifier));
        }
        else {
            cipherParameters = pkcs12ParametersGenerator.generateDerivedParameters(getKeySize(asn1ObjectIdentifier), n * 8);
            if (isDesAlg(asn1ObjectIdentifier)) {
                DESedeParameters.setOddParity(((KeyParameter)((ParametersWithIV)cipherParameters).getParameters()).getKey());
            }
        }
        return cipherParameters;
    }
    
    static {
        PKCS12PBEUtils.keySizes = new HashMap();
        PKCS12PBEUtils.noIvAlgs = new HashSet();
        PKCS12PBEUtils.desAlgs = new HashSet();
        PKCS12PBEUtils.keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4, Integers.valueOf(128));
        PKCS12PBEUtils.keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, Integers.valueOf(40));
        PKCS12PBEUtils.keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, Integers.valueOf(192));
        PKCS12PBEUtils.keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, Integers.valueOf(128));
        PKCS12PBEUtils.keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, Integers.valueOf(128));
        PKCS12PBEUtils.keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, Integers.valueOf(40));
        PKCS12PBEUtils.noIvAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4);
        PKCS12PBEUtils.noIvAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4);
        PKCS12PBEUtils.desAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC);
        PKCS12PBEUtils.desAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC);
    }
}
