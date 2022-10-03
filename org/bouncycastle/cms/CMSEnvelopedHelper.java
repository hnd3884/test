package org.bouncycastle.cms;

import java.io.FilterInputStream;
import org.bouncycastle.operator.DigestCalculator;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.bouncycastle.asn1.cms.KEKRecipientInfo;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import java.util.Collection;
import java.util.List;
import org.bouncycastle.asn1.cms.RecipientInfo;
import java.util.ArrayList;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Set;

class CMSEnvelopedHelper
{
    static RecipientInformationStore buildRecipientInformationStore(final ASN1Set set, final AlgorithmIdentifier algorithmIdentifier, final CMSSecureReadable cmsSecureReadable) {
        return buildRecipientInformationStore(set, algorithmIdentifier, cmsSecureReadable, null);
    }
    
    static RecipientInformationStore buildRecipientInformationStore(final ASN1Set set, final AlgorithmIdentifier algorithmIdentifier, final CMSSecureReadable cmsSecureReadable, final AuthAttributesProvider authAttributesProvider) {
        final ArrayList list = new ArrayList();
        for (int i = 0; i != set.size(); ++i) {
            readRecipientInfo(list, RecipientInfo.getInstance((Object)set.getObjectAt(i)), algorithmIdentifier, cmsSecureReadable, authAttributesProvider);
        }
        return new RecipientInformationStore(list);
    }
    
    private static void readRecipientInfo(final List list, final RecipientInfo recipientInfo, final AlgorithmIdentifier algorithmIdentifier, final CMSSecureReadable cmsSecureReadable, final AuthAttributesProvider authAttributesProvider) {
        final ASN1Encodable info = recipientInfo.getInfo();
        if (info instanceof KeyTransRecipientInfo) {
            list.add(new KeyTransRecipientInformation((KeyTransRecipientInfo)info, algorithmIdentifier, cmsSecureReadable, authAttributesProvider));
        }
        else if (info instanceof KEKRecipientInfo) {
            list.add(new KEKRecipientInformation((KEKRecipientInfo)info, algorithmIdentifier, cmsSecureReadable, authAttributesProvider));
        }
        else if (info instanceof KeyAgreeRecipientInfo) {
            KeyAgreeRecipientInformation.readRecipientInfo(list, (KeyAgreeRecipientInfo)info, algorithmIdentifier, cmsSecureReadable, authAttributesProvider);
        }
        else if (info instanceof PasswordRecipientInfo) {
            list.add(new PasswordRecipientInformation((PasswordRecipientInfo)info, algorithmIdentifier, cmsSecureReadable, authAttributesProvider));
        }
    }
    
    static class CMSAuthenticatedSecureReadable implements CMSSecureReadable
    {
        private AlgorithmIdentifier algorithm;
        private CMSReadable readable;
        
        CMSAuthenticatedSecureReadable(final AlgorithmIdentifier algorithm, final CMSReadable readable) {
            this.algorithm = algorithm;
            this.readable = readable;
        }
        
        public InputStream getInputStream() throws IOException, CMSException {
            return this.readable.getInputStream();
        }
    }
    
    static class CMSDigestAuthenticatedSecureReadable implements CMSSecureReadable
    {
        private DigestCalculator digestCalculator;
        private CMSReadable readable;
        
        public CMSDigestAuthenticatedSecureReadable(final DigestCalculator digestCalculator, final CMSReadable readable) {
            this.digestCalculator = digestCalculator;
            this.readable = readable;
        }
        
        public InputStream getInputStream() throws IOException, CMSException {
            return new FilterInputStream(this.readable.getInputStream()) {
                @Override
                public int read() throws IOException {
                    final int read = this.in.read();
                    if (read >= 0) {
                        CMSDigestAuthenticatedSecureReadable.this.digestCalculator.getOutputStream().write(read);
                    }
                    return read;
                }
                
                @Override
                public int read(final byte[] array, final int n, final int n2) throws IOException {
                    final int read = this.in.read(array, n, n2);
                    if (read >= 0) {
                        CMSDigestAuthenticatedSecureReadable.this.digestCalculator.getOutputStream().write(array, n, read);
                    }
                    return read;
                }
            };
        }
        
        public byte[] getDigest() {
            return this.digestCalculator.getDigest();
        }
    }
    
    static class CMSEnvelopedSecureReadable implements CMSSecureReadable
    {
        private AlgorithmIdentifier algorithm;
        private CMSReadable readable;
        
        CMSEnvelopedSecureReadable(final AlgorithmIdentifier algorithm, final CMSReadable readable) {
            this.algorithm = algorithm;
            this.readable = readable;
        }
        
        public InputStream getInputStream() throws IOException, CMSException {
            return this.readable.getInputStream();
        }
    }
}
