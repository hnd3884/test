package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSEncryptedData;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.ASN1Sequence;

public class PKCS12SafeBagFactory
{
    private ASN1Sequence safeBagSeq;
    
    public PKCS12SafeBagFactory(final ContentInfo contentInfo) {
        if (contentInfo.getContentType().equals((Object)PKCSObjectIdentifiers.encryptedData)) {
            throw new IllegalArgumentException("encryptedData requires constructor with decryptor.");
        }
        this.safeBagSeq = ASN1Sequence.getInstance((Object)ASN1OctetString.getInstance((Object)contentInfo.getContent()).getOctets());
    }
    
    public PKCS12SafeBagFactory(final ContentInfo contentInfo, final InputDecryptorProvider inputDecryptorProvider) throws PKCSException {
        if (contentInfo.getContentType().equals((Object)PKCSObjectIdentifiers.encryptedData)) {
            final CMSEncryptedData cmsEncryptedData = new CMSEncryptedData(org.bouncycastle.asn1.cms.ContentInfo.getInstance((Object)contentInfo));
            try {
                this.safeBagSeq = ASN1Sequence.getInstance((Object)cmsEncryptedData.getContent(inputDecryptorProvider));
            }
            catch (final CMSException ex) {
                throw new PKCSException("unable to extract data: " + ex.getMessage(), ex);
            }
            return;
        }
        throw new IllegalArgumentException("encryptedData requires constructor with decryptor.");
    }
    
    public PKCS12SafeBag[] getSafeBags() {
        final PKCS12SafeBag[] array = new PKCS12SafeBag[this.safeBagSeq.size()];
        for (int i = 0; i != this.safeBagSeq.size(); ++i) {
            array[i] = new PKCS12SafeBag(SafeBag.getInstance((Object)this.safeBagSeq.getObjectAt(i)));
        }
        return array;
    }
}
