package org.bouncycastle.operator.jcajce;

import java.io.IOException;
import org.bouncycastle.operator.OperatorStreamException;
import java.io.OutputStream;
import java.security.SignatureException;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.operator.RawContentVerifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.PublicKey;
import java.security.Signature;
import java.security.GeneralSecurityException;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;

public class JcaContentVerifierProviderBuilder
{
    private OperatorHelper helper;
    
    public JcaContentVerifierProviderBuilder() {
        this.helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
    }
    
    public JcaContentVerifierProviderBuilder setProvider(final Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }
    
    public JcaContentVerifierProviderBuilder setProvider(final String s) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(s));
        return this;
    }
    
    public ContentVerifierProvider build(final X509CertificateHolder x509CertificateHolder) throws OperatorCreationException, CertificateException {
        return this.build(this.helper.convertCertificate(x509CertificateHolder));
    }
    
    public ContentVerifierProvider build(final X509Certificate x509Certificate) throws OperatorCreationException {
        JcaX509CertificateHolder jcaX509CertificateHolder;
        try {
            jcaX509CertificateHolder = new JcaX509CertificateHolder(x509Certificate);
        }
        catch (final CertificateEncodingException ex) {
            throw new OperatorCreationException("cannot process certificate: " + ex.getMessage(), ex);
        }
        return new ContentVerifierProvider() {
            private SignatureOutputStream stream;
            
            public boolean hasAssociatedCertificate() {
                return true;
            }
            
            public X509CertificateHolder getAssociatedCertificate() {
                return jcaX509CertificateHolder;
            }
            
            public ContentVerifier get(final AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                try {
                    final Signature signature = JcaContentVerifierProviderBuilder.this.helper.createSignature(algorithmIdentifier);
                    signature.initVerify(x509Certificate.getPublicKey());
                    this.stream = new SignatureOutputStream(signature);
                }
                catch (final GeneralSecurityException ex) {
                    throw new OperatorCreationException("exception on setup: " + ex, ex);
                }
                final Signature access$100 = JcaContentVerifierProviderBuilder.this.createRawSig(algorithmIdentifier, x509Certificate.getPublicKey());
                if (access$100 != null) {
                    return new RawSigVerifier(algorithmIdentifier, this.stream, access$100);
                }
                return new SigVerifier(algorithmIdentifier, this.stream);
            }
        };
    }
    
    public ContentVerifierProvider build(final PublicKey publicKey) throws OperatorCreationException {
        return new ContentVerifierProvider() {
            public boolean hasAssociatedCertificate() {
                return false;
            }
            
            public X509CertificateHolder getAssociatedCertificate() {
                return null;
            }
            
            public ContentVerifier get(final AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                final SignatureOutputStream access$200 = JcaContentVerifierProviderBuilder.this.createSignatureStream(algorithmIdentifier, publicKey);
                final Signature access$201 = JcaContentVerifierProviderBuilder.this.createRawSig(algorithmIdentifier, publicKey);
                if (access$201 != null) {
                    return new RawSigVerifier(algorithmIdentifier, access$200, access$201);
                }
                return new SigVerifier(algorithmIdentifier, access$200);
            }
        };
    }
    
    public ContentVerifierProvider build(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws OperatorCreationException {
        return this.build(this.helper.convertPublicKey(subjectPublicKeyInfo));
    }
    
    private SignatureOutputStream createSignatureStream(final AlgorithmIdentifier algorithmIdentifier, final PublicKey publicKey) throws OperatorCreationException {
        try {
            final Signature signature = this.helper.createSignature(algorithmIdentifier);
            signature.initVerify(publicKey);
            return new SignatureOutputStream(signature);
        }
        catch (final GeneralSecurityException ex) {
            throw new OperatorCreationException("exception on setup: " + ex, ex);
        }
    }
    
    private Signature createRawSig(final AlgorithmIdentifier algorithmIdentifier, final PublicKey publicKey) {
        Signature rawSignature;
        try {
            rawSignature = this.helper.createRawSignature(algorithmIdentifier);
            if (rawSignature != null) {
                rawSignature.initVerify(publicKey);
            }
        }
        catch (final Exception ex) {
            rawSignature = null;
        }
        return rawSignature;
    }
    
    private class RawSigVerifier extends SigVerifier implements RawContentVerifier
    {
        private Signature rawSignature;
        
        RawSigVerifier(final AlgorithmIdentifier algorithmIdentifier, final SignatureOutputStream signatureOutputStream, final Signature rawSignature) {
            super(algorithmIdentifier, signatureOutputStream);
            this.rawSignature = rawSignature;
        }
        
        @Override
        public boolean verify(final byte[] array) {
            try {
                return super.verify(array);
            }
            finally {
                try {
                    this.rawSignature.verify(array);
                }
                catch (final Exception ex) {}
            }
        }
        
        public boolean verify(final byte[] array, final byte[] array2) {
            try {
                this.rawSignature.update(array);
                return this.rawSignature.verify(array2);
            }
            catch (final SignatureException ex) {
                throw new RuntimeOperatorException("exception obtaining raw signature: " + ex.getMessage(), ex);
            }
            finally {
                try {
                    this.stream.verify(array2);
                }
                catch (final Exception ex2) {}
            }
        }
    }
    
    private class SigVerifier implements ContentVerifier
    {
        private AlgorithmIdentifier algorithm;
        protected SignatureOutputStream stream;
        
        SigVerifier(final AlgorithmIdentifier algorithm, final SignatureOutputStream stream) {
            this.algorithm = algorithm;
            this.stream = stream;
        }
        
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithm;
        }
        
        public OutputStream getOutputStream() {
            if (this.stream == null) {
                throw new IllegalStateException("verifier not initialised");
            }
            return this.stream;
        }
        
        public boolean verify(final byte[] array) {
            try {
                return this.stream.verify(array);
            }
            catch (final SignatureException ex) {
                throw new RuntimeOperatorException("exception obtaining signature: " + ex.getMessage(), ex);
            }
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
        
        boolean verify(final byte[] array) throws SignatureException {
            return this.sig.verify(array);
        }
    }
}
