package org.bouncycastle.eac.operator.jcajce;

import java.io.IOException;
import org.bouncycastle.operator.OperatorStreamException;
import java.math.BigInteger;
import java.util.Arrays;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import java.security.Signature;
import java.security.SignatureException;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.eac.operator.EACSigner;
import java.security.PrivateKey;
import java.security.Provider;
import java.util.Hashtable;

public class JcaEACSignerBuilder
{
    private static final Hashtable sigNames;
    private EACHelper helper;
    
    public JcaEACSignerBuilder() {
        this.helper = new DefaultEACHelper();
    }
    
    public JcaEACSignerBuilder setProvider(final String s) {
        this.helper = new NamedEACHelper(s);
        return this;
    }
    
    public JcaEACSignerBuilder setProvider(final Provider provider) {
        this.helper = new ProviderEACHelper(provider);
        return this;
    }
    
    public EACSigner build(final String s, final PrivateKey privateKey) throws OperatorCreationException {
        return this.build(JcaEACSignerBuilder.sigNames.get(s), privateKey);
    }
    
    public EACSigner build(final ASN1ObjectIdentifier asn1ObjectIdentifier, final PrivateKey privateKey) throws OperatorCreationException {
        Signature signature;
        try {
            signature = this.helper.getSignature(asn1ObjectIdentifier);
            signature.initSign(privateKey);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new OperatorCreationException("unable to find algorithm: " + ex.getMessage(), ex);
        }
        catch (final NoSuchProviderException ex2) {
            throw new OperatorCreationException("unable to find provider: " + ex2.getMessage(), ex2);
        }
        catch (final InvalidKeyException ex3) {
            throw new OperatorCreationException("invalid key: " + ex3.getMessage(), ex3);
        }
        return new EACSigner() {
            final /* synthetic */ SignatureOutputStream val$sigStream = new SignatureOutputStream(signature);
            
            public ASN1ObjectIdentifier getUsageIdentifier() {
                return asn1ObjectIdentifier;
            }
            
            public OutputStream getOutputStream() {
                return this.val$sigStream;
            }
            
            public byte[] getSignature() {
                try {
                    final byte[] signature = this.val$sigStream.getSignature();
                    if (asn1ObjectIdentifier.on(EACObjectIdentifiers.id_TA_ECDSA)) {
                        return reencode(signature);
                    }
                    return signature;
                }
                catch (final SignatureException ex) {
                    throw new RuntimeOperatorException("exception obtaining signature: " + ex.getMessage(), ex);
                }
            }
        };
    }
    
    public static int max(final int n, final int n2) {
        return (n > n2) ? n : n2;
    }
    
    private static byte[] reencode(final byte[] array) {
        final ASN1Sequence instance = ASN1Sequence.getInstance((Object)array);
        final BigInteger value = ASN1Integer.getInstance((Object)instance.getObjectAt(0)).getValue();
        final BigInteger value2 = ASN1Integer.getInstance((Object)instance.getObjectAt(1)).getValue();
        final byte[] byteArray = value.toByteArray();
        final byte[] byteArray2 = value2.toByteArray();
        final int unsignedIntLength = unsignedIntLength(byteArray);
        final int unsignedIntLength2 = unsignedIntLength(byteArray2);
        final int max = max(unsignedIntLength, unsignedIntLength2);
        final byte[] array2 = new byte[max * 2];
        Arrays.fill(array2, (byte)0);
        copyUnsignedInt(byteArray, array2, max - unsignedIntLength);
        copyUnsignedInt(byteArray2, array2, 2 * max - unsignedIntLength2);
        return array2;
    }
    
    private static int unsignedIntLength(final byte[] array) {
        int length = array.length;
        if (array[0] == 0) {
            --length;
        }
        return length;
    }
    
    private static void copyUnsignedInt(final byte[] array, final byte[] array2, final int n) {
        int length = array.length;
        int n2 = 0;
        if (array[0] == 0) {
            --length;
            n2 = 1;
        }
        System.arraycopy(array, n2, array2, n, length);
    }
    
    static {
        (sigNames = new Hashtable()).put("SHA1withRSA", EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_1);
        JcaEACSignerBuilder.sigNames.put("SHA256withRSA", EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_256);
        JcaEACSignerBuilder.sigNames.put("SHA1withRSAandMGF1", EACObjectIdentifiers.id_TA_RSA_PSS_SHA_1);
        JcaEACSignerBuilder.sigNames.put("SHA256withRSAandMGF1", EACObjectIdentifiers.id_TA_RSA_PSS_SHA_256);
        JcaEACSignerBuilder.sigNames.put("SHA512withRSA", EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_512);
        JcaEACSignerBuilder.sigNames.put("SHA512withRSAandMGF1", EACObjectIdentifiers.id_TA_RSA_PSS_SHA_512);
        JcaEACSignerBuilder.sigNames.put("SHA1withECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_1);
        JcaEACSignerBuilder.sigNames.put("SHA224withECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_224);
        JcaEACSignerBuilder.sigNames.put("SHA256withECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_256);
        JcaEACSignerBuilder.sigNames.put("SHA384withECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_384);
        JcaEACSignerBuilder.sigNames.put("SHA512withECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_512);
    }
    
    private class SignatureOutputStream extends OutputStream
    {
        private Signature sig;
        
        SignatureOutputStream(final Signature sig) {
            this.sig = sig;
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            try {
                this.sig.update(array, n, n2);
            }
            catch (final SignatureException ex) {
                throw new OperatorStreamException("exception in content signer: " + ex.getMessage(), ex);
            }
        }
        
        @Override
        public void write(final byte[] array) throws IOException {
            try {
                this.sig.update(array);
            }
            catch (final SignatureException ex) {
                throw new OperatorStreamException("exception in content signer: " + ex.getMessage(), ex);
            }
        }
        
        @Override
        public void write(final int n) throws IOException {
            try {
                this.sig.update((byte)n);
            }
            catch (final SignatureException ex) {
                throw new OperatorStreamException("exception in content signer: " + ex.getMessage(), ex);
            }
        }
        
        byte[] getSignature() throws SignatureException {
            return this.sig.sign();
        }
    }
}
