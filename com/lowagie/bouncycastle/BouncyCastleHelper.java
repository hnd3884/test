package com.lowagie.bouncycastle;

import org.bouncycastle.cms.Recipient;
import java.util.Collection;
import java.util.Iterator;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import java.security.PrivateKey;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.CMSEnvelopedData;
import java.security.Key;
import com.lowagie.text.pdf.PdfObject;
import java.util.List;
import com.lowagie.text.pdf.PdfArray;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import com.lowagie.text.ExceptionConverter;
import org.bouncycastle.cert.X509CertificateHolder;
import java.security.cert.Certificate;

public class BouncyCastleHelper
{
    public static void checkCertificateEncodingOrThrowException(final Certificate certificate) {
        try {
            final X509CertificateHolder x509CertificateHolder = new X509CertificateHolder(certificate.getEncoded());
        }
        catch (final CertificateEncodingException | IOException f) {
            throw new ExceptionConverter(f);
        }
    }
    
    public static byte[] getEnvelopedData(final PdfArray recipients, final List<PdfObject> strings, final Certificate certificate, final Key certificateKey, final String certificateKeyProvider) {
        byte[] envelopedData = null;
        for (final PdfObject recipient : recipients.getElements()) {
            strings.remove(recipient);
            try {
                final CMSEnvelopedData data = new CMSEnvelopedData(recipient.getBytes());
                final Collection<RecipientInformation> recipientInformations = data.getRecipientInfos().getRecipients();
                for (final RecipientInformation recipientInfo : recipientInformations) {
                    if (recipientInfo.getRID().match((Object)certificate)) {
                        final Recipient rec = (Recipient)new JceKeyTransEnvelopedRecipient((PrivateKey)certificateKey).setProvider(certificateKeyProvider);
                        envelopedData = recipientInfo.getContent(rec);
                        break;
                    }
                }
            }
            catch (final Exception f) {
                throw new ExceptionConverter(f);
            }
        }
        return envelopedData;
    }
}
