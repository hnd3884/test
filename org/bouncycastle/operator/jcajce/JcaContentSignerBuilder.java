package org.bouncycastle.operator.jcajce;

import java.io.IOException;
import org.bouncycastle.operator.OperatorStreamException;
import java.security.GeneralSecurityException;
import org.bouncycastle.operator.OperatorCreationException;
import java.security.SignatureException;
import org.bouncycastle.operator.RuntimeOperatorException;
import java.io.OutputStream;
import java.security.Signature;
import org.bouncycastle.operator.ContentSigner;
import java.security.PrivateKey;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.SecureRandom;

public class JcaContentSignerBuilder
{
    private OperatorHelper helper;
    private SecureRandom random;
    private String signatureAlgorithm;
    private AlgorithmIdentifier sigAlgId;
    
    public JcaContentSignerBuilder(final String signatureAlgorithm) {
        this.helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
        this.signatureAlgorithm = signatureAlgorithm;
        this.sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find(signatureAlgorithm);
    }
    
    public JcaContentSignerBuilder setProvider(final Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }
    
    public JcaContentSignerBuilder setProvider(final String s) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(s));
        return this;
    }
    
    public JcaContentSignerBuilder setSecureRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public ContentSigner build(final PrivateKey privateKey) throws OperatorCreationException {
        try {
            final Signature signature = this.helper.createSignature(this.sigAlgId);
            final AlgorithmIdentifier sigAlgId = this.sigAlgId;
            if (this.random != null) {
                signature.initSign(privateKey, this.random);
            }
            else {
                signature.initSign(privateKey);
            }
            return new ContentSigner() {
                private SignatureOutputStream stream = new SignatureOutputStream(signature);
                
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return sigAlgId;
                }
                
                public OutputStream getOutputStream() {
                    return this.stream;
                }
                
                public byte[] getSignature() {
                    try {
                        return this.stream.getSignature();
                    }
                    catch (final SignatureException ex) {
                        throw new RuntimeOperatorException("exception obtaining signature: " + ex.getMessage(), ex);
                    }
                }
            };
        }
        catch (final GeneralSecurityException ex) {
            throw new OperatorCreationException("cannot create signer: " + ex.getMessage(), ex);
        }
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
