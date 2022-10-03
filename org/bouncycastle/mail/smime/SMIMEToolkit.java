package org.bouncycastle.mail.smime;

import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientId;
import javax.mail.Multipart;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.Store;
import java.util.Collection;
import org.bouncycastle.util.CollectionStore;
import java.util.ArrayList;
import org.bouncycastle.cms.SignerInfoGenerator;
import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.util.Selector;
import javax.mail.internet.MimeMessage;
import java.util.Iterator;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerId;
import java.io.IOException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.SignerInformationVerifier;
import javax.mail.internet.MimeMultipart;
import javax.mail.MessagingException;
import javax.mail.Part;
import org.bouncycastle.operator.DigestCalculatorProvider;

public class SMIMEToolkit
{
    private final DigestCalculatorProvider digestCalculatorProvider;
    
    public SMIMEToolkit(final DigestCalculatorProvider digestCalculatorProvider) {
        this.digestCalculatorProvider = digestCalculatorProvider;
    }
    
    public boolean isEncrypted(final Part part) throws MessagingException {
        return part.getHeader("Content-Type")[0].equals("application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data");
    }
    
    public boolean isSigned(final Part part) throws MessagingException {
        return part.getHeader("Content-Type")[0].startsWith("multipart/signed") || part.getHeader("Content-Type")[0].equals("application/pkcs7-mime; name=smime.p7m; smime-type=signed-data");
    }
    
    public boolean isSigned(final MimeMultipart mimeMultipart) throws MessagingException {
        return mimeMultipart.getBodyPart(1).getHeader("Content-Type")[0].equals("application/pkcs7-signature; name=smime.p7s; smime-type=signed-data");
    }
    
    public boolean isValidSignature(final Part part, final SignerInformationVerifier signerInformationVerifier) throws SMIMEException, MessagingException {
        try {
            SMIMESignedParser smimeSignedParser;
            if (part.isMimeType("multipart/signed")) {
                smimeSignedParser = new SMIMESignedParser(this.digestCalculatorProvider, (MimeMultipart)part.getContent());
            }
            else {
                smimeSignedParser = new SMIMESignedParser(this.digestCalculatorProvider, part);
            }
            return this.isAtLeastOneValidSigner(smimeSignedParser, signerInformationVerifier);
        }
        catch (final CMSException ex) {
            throw new SMIMEException("CMS processing failure: " + ex.getMessage(), (Exception)ex);
        }
        catch (final IOException ex2) {
            throw new SMIMEException("Parsing failure: " + ex2.getMessage(), ex2);
        }
    }
    
    private boolean isAtLeastOneValidSigner(final SMIMESignedParser smimeSignedParser, final SignerInformationVerifier signerInformationVerifier) throws CMSException {
        if (signerInformationVerifier.hasAssociatedCertificate()) {
            final X509CertificateHolder associatedCertificate = signerInformationVerifier.getAssociatedCertificate();
            final SignerInformation value = smimeSignedParser.getSignerInfos().get(new SignerId(associatedCertificate.getIssuer(), associatedCertificate.getSerialNumber()));
            if (value != null) {
                return value.verify(signerInformationVerifier);
            }
        }
        final Iterator iterator = smimeSignedParser.getSignerInfos().getSigners().iterator();
        while (iterator.hasNext()) {
            if (((SignerInformation)iterator.next()).verify(signerInformationVerifier)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isValidSignature(final MimeMultipart mimeMultipart, final SignerInformationVerifier signerInformationVerifier) throws SMIMEException, MessagingException {
        try {
            return this.isAtLeastOneValidSigner(new SMIMESignedParser(this.digestCalculatorProvider, mimeMultipart), signerInformationVerifier);
        }
        catch (final CMSException ex) {
            throw new SMIMEException("CMS processing failure: " + ex.getMessage(), (Exception)ex);
        }
    }
    
    public X509CertificateHolder extractCertificate(final Part part, final SignerInformation signerInformation) throws SMIMEException, MessagingException {
        try {
            SMIMESignedParser smimeSignedParser;
            if (part instanceof MimeMessage && part.isMimeType("multipart/signed")) {
                smimeSignedParser = new SMIMESignedParser(this.digestCalculatorProvider, (MimeMultipart)part.getContent());
            }
            else {
                smimeSignedParser = new SMIMESignedParser(this.digestCalculatorProvider, part);
            }
            final Iterator iterator = smimeSignedParser.getCertificates().getMatches((Selector)signerInformation.getSID()).iterator();
            if (iterator.hasNext()) {
                return (X509CertificateHolder)iterator.next();
            }
            return null;
        }
        catch (final CMSException ex) {
            throw new SMIMEException("CMS processing failure: " + ex.getMessage(), (Exception)ex);
        }
        catch (final IOException ex2) {
            throw new SMIMEException("Parsing failure: " + ex2.getMessage(), ex2);
        }
    }
    
    public X509CertificateHolder extractCertificate(final MimeMultipart mimeMultipart, final SignerInformation signerInformation) throws SMIMEException, MessagingException {
        try {
            final Iterator iterator = new SMIMESignedParser(this.digestCalculatorProvider, mimeMultipart).getCertificates().getMatches((Selector)signerInformation.getSID()).iterator();
            if (iterator.hasNext()) {
                return (X509CertificateHolder)iterator.next();
            }
            return null;
        }
        catch (final CMSException ex) {
            throw new SMIMEException("CMS processing failure: " + ex.getMessage(), (Exception)ex);
        }
    }
    
    public MimeMultipart sign(final MimeBodyPart mimeBodyPart, final SignerInfoGenerator signerInfoGenerator) throws SMIMEException {
        final SMIMESignedGenerator smimeSignedGenerator = new SMIMESignedGenerator();
        if (signerInfoGenerator.hasAssociatedCertificate()) {
            final ArrayList list = new ArrayList();
            list.add(signerInfoGenerator.getAssociatedCertificate());
            smimeSignedGenerator.addCertificates((Store)new CollectionStore((Collection)list));
        }
        smimeSignedGenerator.addSignerInfoGenerator(signerInfoGenerator);
        return smimeSignedGenerator.generate(mimeBodyPart);
    }
    
    public MimeBodyPart signEncapsulated(final MimeBodyPart mimeBodyPart, final SignerInfoGenerator signerInfoGenerator) throws SMIMEException {
        final SMIMESignedGenerator smimeSignedGenerator = new SMIMESignedGenerator();
        if (signerInfoGenerator.hasAssociatedCertificate()) {
            final ArrayList list = new ArrayList();
            list.add(signerInfoGenerator.getAssociatedCertificate());
            smimeSignedGenerator.addCertificates((Store)new CollectionStore((Collection)list));
        }
        smimeSignedGenerator.addSignerInfoGenerator(signerInfoGenerator);
        return smimeSignedGenerator.generateEncapsulated(mimeBodyPart);
    }
    
    public MimeBodyPart encrypt(final MimeBodyPart mimeBodyPart, final OutputEncryptor outputEncryptor, final RecipientInfoGenerator recipientInfoGenerator) throws SMIMEException {
        final SMIMEEnvelopedGenerator smimeEnvelopedGenerator = new SMIMEEnvelopedGenerator();
        smimeEnvelopedGenerator.addRecipientInfoGenerator(recipientInfoGenerator);
        return smimeEnvelopedGenerator.generate(mimeBodyPart, outputEncryptor);
    }
    
    public MimeBodyPart encrypt(final MimeMultipart content, final OutputEncryptor outputEncryptor, final RecipientInfoGenerator recipientInfoGenerator) throws SMIMEException, MessagingException {
        final SMIMEEnvelopedGenerator smimeEnvelopedGenerator = new SMIMEEnvelopedGenerator();
        smimeEnvelopedGenerator.addRecipientInfoGenerator(recipientInfoGenerator);
        final MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent((Multipart)content);
        return smimeEnvelopedGenerator.generate(mimeBodyPart, outputEncryptor);
    }
    
    public MimeBodyPart encrypt(final MimeMessage mimeMessage, final OutputEncryptor outputEncryptor, final RecipientInfoGenerator recipientInfoGenerator) throws SMIMEException {
        final SMIMEEnvelopedGenerator smimeEnvelopedGenerator = new SMIMEEnvelopedGenerator();
        smimeEnvelopedGenerator.addRecipientInfoGenerator(recipientInfoGenerator);
        return smimeEnvelopedGenerator.generate(mimeMessage, outputEncryptor);
    }
    
    public MimeBodyPart decrypt(final MimeBodyPart mimeBodyPart, final RecipientId recipientId, final Recipient recipient) throws SMIMEException, MessagingException {
        try {
            final RecipientInformation value = new SMIMEEnvelopedParser(mimeBodyPart).getRecipientInfos().get(recipientId);
            if (value == null) {
                return null;
            }
            return SMIMEUtil.toMimeBodyPart(value.getContent(recipient));
        }
        catch (final CMSException ex) {
            throw new SMIMEException("CMS processing failure: " + ex.getMessage(), (Exception)ex);
        }
        catch (final IOException ex2) {
            throw new SMIMEException("Parsing failure: " + ex2.getMessage(), ex2);
        }
    }
    
    public MimeBodyPart decrypt(final MimeMessage mimeMessage, final RecipientId recipientId, final Recipient recipient) throws SMIMEException, MessagingException {
        try {
            final RecipientInformation value = new SMIMEEnvelopedParser(mimeMessage).getRecipientInfos().get(recipientId);
            if (value == null) {
                return null;
            }
            return SMIMEUtil.toMimeBodyPart(value.getContent(recipient));
        }
        catch (final CMSException ex) {
            throw new SMIMEException("CMS processing failure: " + ex.getMessage(), (Exception)ex);
        }
        catch (final IOException ex2) {
            throw new SMIMEException("Parsing failure: " + ex2.getMessage(), ex2);
        }
    }
}
