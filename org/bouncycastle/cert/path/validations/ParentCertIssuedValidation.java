package org.bouncycastle.cert.path.validations;

import org.bouncycastle.util.Memoable;
import org.bouncycastle.asn1.ASN1Null;
import java.io.IOException;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509ContentVerifierProviderBuilder;
import org.bouncycastle.cert.path.CertPathValidation;

public class ParentCertIssuedValidation implements CertPathValidation
{
    private X509ContentVerifierProviderBuilder contentVerifierProvider;
    private X500Name workingIssuerName;
    private SubjectPublicKeyInfo workingPublicKey;
    private AlgorithmIdentifier workingAlgId;
    
    public ParentCertIssuedValidation(final X509ContentVerifierProviderBuilder contentVerifierProvider) {
        this.contentVerifierProvider = contentVerifierProvider;
    }
    
    public void validate(final CertPathValidationContext certPathValidationContext, final X509CertificateHolder x509CertificateHolder) throws CertPathValidationException {
        if (this.workingIssuerName != null && !this.workingIssuerName.equals((Object)x509CertificateHolder.getIssuer())) {
            throw new CertPathValidationException("Certificate issue does not match parent");
        }
        if (this.workingPublicKey != null) {
            try {
                SubjectPublicKeyInfo workingPublicKey;
                if (this.workingPublicKey.getAlgorithm().equals((Object)this.workingAlgId)) {
                    workingPublicKey = this.workingPublicKey;
                }
                else {
                    workingPublicKey = new SubjectPublicKeyInfo(this.workingAlgId, (ASN1Encodable)this.workingPublicKey.parsePublicKey());
                }
                if (!x509CertificateHolder.isSignatureValid(this.contentVerifierProvider.build(workingPublicKey))) {
                    throw new CertPathValidationException("Certificate signature not for public key in parent");
                }
            }
            catch (final OperatorCreationException ex) {
                throw new CertPathValidationException("Unable to create verifier: " + ex.getMessage(), ex);
            }
            catch (final CertException ex2) {
                throw new CertPathValidationException("Unable to validate signature: " + ex2.getMessage(), ex2);
            }
            catch (final IOException ex3) {
                throw new CertPathValidationException("Unable to build public key: " + ex3.getMessage(), ex3);
            }
        }
        this.workingIssuerName = x509CertificateHolder.getSubject();
        this.workingPublicKey = x509CertificateHolder.getSubjectPublicKeyInfo();
        if (this.workingAlgId != null) {
            if (this.workingPublicKey.getAlgorithm().getAlgorithm().equals((Object)this.workingAlgId.getAlgorithm())) {
                if (!this.isNull(this.workingPublicKey.getAlgorithm().getParameters())) {
                    this.workingAlgId = this.workingPublicKey.getAlgorithm();
                }
            }
            else {
                this.workingAlgId = this.workingPublicKey.getAlgorithm();
            }
        }
        else {
            this.workingAlgId = this.workingPublicKey.getAlgorithm();
        }
    }
    
    private boolean isNull(final ASN1Encodable asn1Encodable) {
        return asn1Encodable == null || asn1Encodable instanceof ASN1Null;
    }
    
    public Memoable copy() {
        final ParentCertIssuedValidation parentCertIssuedValidation = new ParentCertIssuedValidation(this.contentVerifierProvider);
        parentCertIssuedValidation.workingAlgId = this.workingAlgId;
        parentCertIssuedValidation.workingIssuerName = this.workingIssuerName;
        parentCertIssuedValidation.workingPublicKey = this.workingPublicKey;
        return (Memoable)parentCertIssuedValidation;
    }
    
    public void reset(final Memoable memoable) {
        final ParentCertIssuedValidation parentCertIssuedValidation = (ParentCertIssuedValidation)memoable;
        this.contentVerifierProvider = parentCertIssuedValidation.contentVerifierProvider;
        this.workingAlgId = parentCertIssuedValidation.workingAlgId;
        this.workingIssuerName = parentCertIssuedValidation.workingIssuerName;
        this.workingPublicKey = parentCertIssuedValidation.workingPublicKey;
    }
}
