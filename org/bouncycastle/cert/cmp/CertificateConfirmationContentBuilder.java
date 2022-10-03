package org.bouncycastle.cert.cmp;

import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.cmp.CertConfirmContent;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CertStatus;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.operator.DigestCalculatorProvider;
import java.math.BigInteger;
import org.bouncycastle.cert.X509CertificateHolder;
import java.util.ArrayList;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import java.util.List;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

public class CertificateConfirmationContentBuilder
{
    private DigestAlgorithmIdentifierFinder digestAlgFinder;
    private List acceptedCerts;
    private List acceptedReqIds;
    
    public CertificateConfirmationContentBuilder() {
        this(new DefaultDigestAlgorithmIdentifierFinder());
    }
    
    public CertificateConfirmationContentBuilder(final DigestAlgorithmIdentifierFinder digestAlgFinder) {
        this.acceptedCerts = new ArrayList();
        this.acceptedReqIds = new ArrayList();
        this.digestAlgFinder = digestAlgFinder;
    }
    
    public CertificateConfirmationContentBuilder addAcceptedCertificate(final X509CertificateHolder x509CertificateHolder, final BigInteger bigInteger) {
        this.acceptedCerts.add(x509CertificateHolder);
        this.acceptedReqIds.add(bigInteger);
        return this;
    }
    
    public CertificateConfirmationContent build(final DigestCalculatorProvider digestCalculatorProvider) throws CMPException {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != this.acceptedCerts.size(); ++i) {
            final X509CertificateHolder x509CertificateHolder = this.acceptedCerts.get(i);
            final BigInteger bigInteger = this.acceptedReqIds.get(i);
            final AlgorithmIdentifier find = this.digestAlgFinder.find(x509CertificateHolder.toASN1Structure().getSignatureAlgorithm());
            if (find == null) {
                throw new CMPException("cannot find algorithm for digest from signature");
            }
            DigestCalculator value;
            try {
                value = digestCalculatorProvider.get(find);
            }
            catch (final OperatorCreationException ex) {
                throw new CMPException("unable to create digest: " + ex.getMessage(), ex);
            }
            CMPUtil.derEncodeToStream((ASN1Encodable)x509CertificateHolder.toASN1Structure(), value.getOutputStream());
            asn1EncodableVector.add((ASN1Encodable)new CertStatus(value.getDigest(), bigInteger));
        }
        return new CertificateConfirmationContent(CertConfirmContent.getInstance((Object)new DERSequence(asn1EncodableVector)), this.digestAlgFinder);
    }
}
