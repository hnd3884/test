package org.bouncycastle.mail.smime;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;
import java.io.IOException;
import org.bouncycastle.cms.CMSException;
import java.io.OutputStream;
import java.security.AccessController;
import javax.activation.CommandMap;
import java.security.PrivilegedAction;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import org.bouncycastle.operator.OutputEncryptor;
import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.cms.RecipientInfoGenerator;
import java.util.ArrayList;
import javax.activation.MailcapCommandMap;
import java.util.List;

public class SMIMEEnvelopedGenerator extends SMIMEGenerator
{
    public static final String DES_EDE3_CBC;
    public static final String RC2_CBC;
    public static final String IDEA_CBC = "1.3.6.1.4.1.188.7.1.1.2";
    public static final String CAST5_CBC = "1.2.840.113533.7.66.10";
    public static final String AES128_CBC;
    public static final String AES192_CBC;
    public static final String AES256_CBC;
    public static final String CAMELLIA128_CBC;
    public static final String CAMELLIA192_CBC;
    public static final String CAMELLIA256_CBC;
    public static final String SEED_CBC;
    public static final String DES_EDE3_WRAP;
    public static final String AES128_WRAP;
    public static final String AES256_WRAP;
    public static final String CAMELLIA128_WRAP;
    public static final String CAMELLIA192_WRAP;
    public static final String CAMELLIA256_WRAP;
    public static final String SEED_WRAP;
    public static final String ECDH_SHA1KDF;
    private static final String ENCRYPTED_CONTENT_TYPE = "application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data";
    private EnvelopedGenerator fact;
    private List recipients;
    
    private static MailcapCommandMap addCommands(final MailcapCommandMap mailcapCommandMap) {
        mailcapCommandMap.addMailcap("application/pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature");
        mailcapCommandMap.addMailcap("application/pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_mime");
        mailcapCommandMap.addMailcap("application/x-pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_signature");
        mailcapCommandMap.addMailcap("application/x-pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_mime");
        mailcapCommandMap.addMailcap("multipart/signed;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.multipart_signed");
        return mailcapCommandMap;
    }
    
    public SMIMEEnvelopedGenerator() {
        this.recipients = new ArrayList();
        this.fact = new EnvelopedGenerator();
    }
    
    public void addRecipientInfoGenerator(final RecipientInfoGenerator recipientInfoGenerator) throws IllegalArgumentException {
        this.fact.addRecipientInfoGenerator(recipientInfoGenerator);
    }
    
    public void setBerEncodeRecipients(final boolean berEncodeRecipients) {
        this.fact.setBEREncodeRecipients(berEncodeRecipients);
    }
    
    private MimeBodyPart make(final MimeBodyPart mimeBodyPart, final OutputEncryptor outputEncryptor) throws SMIMEException {
        try {
            final MimeBodyPart mimeBodyPart2 = new MimeBodyPart();
            mimeBodyPart2.setContent((Object)new ContentEncryptor(mimeBodyPart, outputEncryptor), "application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data");
            mimeBodyPart2.addHeader("Content-Type", "application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data");
            mimeBodyPart2.addHeader("Content-Disposition", "attachment; filename=\"smime.p7m\"");
            mimeBodyPart2.addHeader("Content-Description", "S/MIME Encrypted Message");
            mimeBodyPart2.addHeader("Content-Transfer-Encoding", this.encoding);
            return mimeBodyPart2;
        }
        catch (final MessagingException ex) {
            throw new SMIMEException("exception putting multi-part together.", (Exception)ex);
        }
    }
    
    public MimeBodyPart generate(final MimeBodyPart mimeBodyPart, final OutputEncryptor outputEncryptor) throws SMIMEException {
        return this.make(this.makeContentBodyPart(mimeBodyPart), outputEncryptor);
    }
    
    public MimeBodyPart generate(final MimeMessage mimeMessage, final OutputEncryptor outputEncryptor) throws SMIMEException {
        try {
            mimeMessage.saveChanges();
        }
        catch (final MessagingException ex) {
            throw new SMIMEException("unable to save message", (Exception)ex);
        }
        return this.make(this.makeContentBodyPart(mimeMessage), outputEncryptor);
    }
    
    static {
        DES_EDE3_CBC = CMSEnvelopedDataGenerator.DES_EDE3_CBC;
        RC2_CBC = CMSEnvelopedDataGenerator.RC2_CBC;
        AES128_CBC = CMSEnvelopedDataGenerator.AES128_CBC;
        AES192_CBC = CMSEnvelopedDataGenerator.AES192_CBC;
        AES256_CBC = CMSEnvelopedDataGenerator.AES256_CBC;
        CAMELLIA128_CBC = CMSEnvelopedDataGenerator.CAMELLIA128_CBC;
        CAMELLIA192_CBC = CMSEnvelopedDataGenerator.CAMELLIA192_CBC;
        CAMELLIA256_CBC = CMSEnvelopedDataGenerator.CAMELLIA256_CBC;
        SEED_CBC = CMSEnvelopedDataGenerator.SEED_CBC;
        DES_EDE3_WRAP = CMSEnvelopedDataGenerator.DES_EDE3_WRAP;
        AES128_WRAP = CMSEnvelopedDataGenerator.AES128_WRAP;
        AES256_WRAP = CMSEnvelopedDataGenerator.AES256_WRAP;
        CAMELLIA128_WRAP = CMSEnvelopedDataGenerator.CAMELLIA128_WRAP;
        CAMELLIA192_WRAP = CMSEnvelopedDataGenerator.CAMELLIA192_WRAP;
        CAMELLIA256_WRAP = CMSEnvelopedDataGenerator.CAMELLIA256_WRAP;
        SEED_WRAP = CMSEnvelopedDataGenerator.SEED_WRAP;
        ECDH_SHA1KDF = CMSEnvelopedDataGenerator.ECDH_SHA1KDF;
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                final CommandMap defaultCommandMap = CommandMap.getDefaultCommandMap();
                if (defaultCommandMap instanceof MailcapCommandMap) {
                    CommandMap.setDefaultCommandMap(addCommands((MailcapCommandMap)defaultCommandMap));
                }
                return null;
            }
        });
    }
    
    private class ContentEncryptor implements SMIMEStreamingProcessor
    {
        private final MimeBodyPart _content;
        private OutputEncryptor _encryptor;
        private boolean _firstTime;
        
        ContentEncryptor(final MimeBodyPart content, final OutputEncryptor encryptor) {
            this._firstTime = true;
            this._content = content;
            this._encryptor = encryptor;
        }
        
        public void write(final OutputStream outputStream) throws IOException {
            try {
                OutputStream outputStream2;
                if (this._firstTime) {
                    outputStream2 = SMIMEEnvelopedGenerator.this.fact.open(outputStream, this._encryptor);
                    this._firstTime = false;
                }
                else {
                    outputStream2 = SMIMEEnvelopedGenerator.this.fact.regenerate(outputStream, this._encryptor);
                }
                final CommandMap defaultCommandMap = CommandMap.getDefaultCommandMap();
                if (defaultCommandMap instanceof MailcapCommandMap) {
                    this._content.getDataHandler().setCommandMap(addCommands((MailcapCommandMap)defaultCommandMap));
                }
                this._content.writeTo(outputStream2);
                outputStream2.close();
            }
            catch (final MessagingException ex) {
                throw new WrappingIOException(ex.toString(), (Throwable)ex);
            }
            catch (final CMSException ex2) {
                throw new WrappingIOException(ex2.toString(), (Throwable)ex2);
            }
        }
    }
    
    private class EnvelopedGenerator extends CMSEnvelopedDataStreamGenerator
    {
        private ASN1ObjectIdentifier dataType;
        private ASN1EncodableVector recipientInfos;
        
        protected OutputStream open(final ASN1ObjectIdentifier dataType, final OutputStream outputStream, final ASN1EncodableVector recipientInfos, final OutputEncryptor outputEncryptor) throws IOException {
            this.dataType = dataType;
            this.recipientInfos = recipientInfos;
            return super.open(dataType, outputStream, recipientInfos, outputEncryptor);
        }
        
        OutputStream regenerate(final OutputStream outputStream, final OutputEncryptor outputEncryptor) throws IOException {
            return super.open(this.dataType, outputStream, this.recipientInfos, outputEncryptor);
        }
    }
    
    private static class WrappingIOException extends IOException
    {
        private Throwable cause;
        
        WrappingIOException(final String s, final Throwable cause) {
            super(s);
            this.cause = cause;
        }
        
        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}
