package org.bouncycastle.eac.operator.jcajce;

import org.bouncycastle.operator.OperatorStreamException;
import java.io.IOException;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.security.Signature;
import java.security.SignatureException;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.eac.operator.EACSignatureVerifier;
import java.security.PublicKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.security.Provider;

public class JcaEACSignatureVerifierBuilder
{
    private EACHelper helper;
    
    public JcaEACSignatureVerifierBuilder() {
        this.helper = new DefaultEACHelper();
    }
    
    public JcaEACSignatureVerifierBuilder setProvider(final String s) {
        this.helper = new NamedEACHelper(s);
        return this;
    }
    
    public JcaEACSignatureVerifierBuilder setProvider(final Provider provider) {
        this.helper = new ProviderEACHelper(provider);
        return this;
    }
    
    public EACSignatureVerifier build(final ASN1ObjectIdentifier asn1ObjectIdentifier, final PublicKey publicKey) throws OperatorCreationException {
        Signature signature;
        try {
            signature = this.helper.getSignature(asn1ObjectIdentifier);
            signature.initVerify(publicKey);
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
        return new EACSignatureVerifier() {
            final /* synthetic */ SignatureOutputStream val$sigStream = new SignatureOutputStream(signature);
            
            public ASN1ObjectIdentifier getUsageIdentifier() {
                return asn1ObjectIdentifier;
            }
            
            public OutputStream getOutputStream() {
                return this.val$sigStream;
            }
            
            public boolean verify(final byte[] array) {
                try {
                    if (asn1ObjectIdentifier.on(EACObjectIdentifiers.id_TA_ECDSA)) {
                        try {
                            return this.val$sigStream.verify(derEncode(array));
                        }
                        catch (final Exception ex) {
                            return false;
                        }
                    }
                    return this.val$sigStream.verify(array);
                }
                catch (final SignatureException ex2) {
                    throw new RuntimeOperatorException("exception obtaining signature: " + ex2.getMessage(), ex2);
                }
            }
        };
    }
    
    private static byte[] derEncode(final byte[] array) throws IOException {
        final int n = array.length / 2;
        final byte[] array2 = new byte[n];
        final byte[] array3 = new byte[n];
        System.arraycopy(array, 0, array2, 0, n);
        System.arraycopy(array, n, array3, 0, n);
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add((ASN1Encodable)new ASN1Integer(new BigInteger(1, array2)));
        asn1EncodableVector.add((ASN1Encodable)new ASN1Integer(new BigInteger(1, array3)));
        return new DERSequence(asn1EncodableVector).getEncoded();
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
        
        boolean verify(final byte[] array) throws SignatureException {
            return this.sig.verify(array);
        }
    }
}
