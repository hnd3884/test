package org.bouncycastle.cert.cmp;

import org.bouncycastle.asn1.cmp.CertStatus;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.asn1.cmp.CertConfirmContent;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

public class CertificateConfirmationContent
{
    private DigestAlgorithmIdentifierFinder digestAlgFinder;
    private CertConfirmContent content;
    
    public CertificateConfirmationContent(final CertConfirmContent certConfirmContent) {
        this(certConfirmContent, new DefaultDigestAlgorithmIdentifierFinder());
    }
    
    public CertificateConfirmationContent(final CertConfirmContent content, final DigestAlgorithmIdentifierFinder digestAlgFinder) {
        this.digestAlgFinder = digestAlgFinder;
        this.content = content;
    }
    
    public CertConfirmContent toASN1Structure() {
        return this.content;
    }
    
    public CertificateStatus[] getStatusMessages() {
        final CertStatus[] certStatusArray = this.content.toCertStatusArray();
        final CertificateStatus[] array = new CertificateStatus[certStatusArray.length];
        for (int i = 0; i != array.length; ++i) {
            array[i] = new CertificateStatus(this.digestAlgFinder, certStatusArray[i]);
        }
        return array;
    }
}
