package org.bouncycastle.cms;

import java.io.IOException;
import org.bouncycastle.asn1.cms.AuthEnvelopedData;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.cms.ContentInfo;

class CMSAuthEnvelopedData
{
    RecipientInformationStore recipientInfoStore;
    ContentInfo contentInfo;
    private OriginatorInfo originator;
    private AlgorithmIdentifier authEncAlg;
    private ASN1Set authAttrs;
    private byte[] mac;
    private ASN1Set unauthAttrs;
    
    public CMSAuthEnvelopedData(final byte[] array) throws CMSException {
        this(CMSUtils.readContentInfo(array));
    }
    
    public CMSAuthEnvelopedData(final InputStream inputStream) throws CMSException {
        this(CMSUtils.readContentInfo(inputStream));
    }
    
    public CMSAuthEnvelopedData(final ContentInfo contentInfo) throws CMSException {
        this.contentInfo = contentInfo;
        final AuthEnvelopedData instance = AuthEnvelopedData.getInstance((Object)contentInfo.getContent());
        this.originator = instance.getOriginatorInfo();
        final ASN1Set recipientInfos = instance.getRecipientInfos();
        this.authEncAlg = instance.getAuthEncryptedContentInfo().getContentEncryptionAlgorithm();
        this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.authEncAlg, new CMSSecureReadable() {
            public InputStream getInputStream() throws IOException, CMSException {
                return null;
            }
        });
        this.authAttrs = instance.getAuthAttrs();
        this.mac = instance.getMac().getOctets();
        this.unauthAttrs = instance.getUnauthAttrs();
    }
}
