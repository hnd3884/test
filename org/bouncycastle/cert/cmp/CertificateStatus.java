package org.bouncycastle.cert.cmp;

import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.cert.X509CertificateHolder;
import java.math.BigInteger;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.cmp.CertStatus;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

public class CertificateStatus
{
    private DigestAlgorithmIdentifierFinder digestAlgFinder;
    private CertStatus certStatus;
    
    CertificateStatus(final DigestAlgorithmIdentifierFinder digestAlgFinder, final CertStatus certStatus) {
        this.digestAlgFinder = digestAlgFinder;
        this.certStatus = certStatus;
    }
    
    public PKIStatusInfo getStatusInfo() {
        return this.certStatus.getStatusInfo();
    }
    
    public BigInteger getCertRequestID() {
        return this.certStatus.getCertReqId().getValue();
    }
    
    public boolean isVerified(final X509CertificateHolder x509CertificateHolder, final DigestCalculatorProvider digestCalculatorProvider) throws CMPException {
        final AlgorithmIdentifier find = this.digestAlgFinder.find(x509CertificateHolder.toASN1Structure().getSignatureAlgorithm());
        if (find == null) {
            throw new CMPException("cannot find algorithm for digest from signature");
        }
        DigestCalculator value;
        try {
            value = digestCalculatorProvider.get(find);
        }
        catch (final OperatorCreationException ex) {
            throw new CMPException("unable to create digester: " + ex.getMessage(), ex);
        }
        CMPUtil.derEncodeToStream((ASN1Encodable)x509CertificateHolder.toASN1Structure(), value.getOutputStream());
        return Arrays.areEqual(this.certStatus.getCertHash().getOctets(), value.getDigest());
    }
}
