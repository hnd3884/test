package org.bouncycastle.mail.smime;

import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import javax.mail.MessagingException;
import java.io.InputStream;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.cms.CMSSignedData;

public class SMIMESigned extends CMSSignedData
{
    Object message;
    MimeBodyPart content;
    
    private static InputStream getInputStream(final Part part) throws MessagingException {
        try {
            if (part.isMimeType("multipart/signed")) {
                throw new MessagingException("attempt to create signed data object from multipart content - use MimeMultipart constructor.");
            }
            return part.getInputStream();
        }
        catch (final IOException ex) {
            throw new MessagingException("can't extract input stream: " + ex);
        }
    }
    
    public SMIMESigned(final MimeMultipart message) throws MessagingException, CMSException {
        super((CMSProcessable)new CMSProcessableBodyPartInbound(message.getBodyPart(0)), getInputStream((Part)message.getBodyPart(1)));
        this.message = message;
        this.content = (MimeBodyPart)message.getBodyPart(0);
    }
    
    public SMIMESigned(final MimeMultipart message, final String s) throws MessagingException, CMSException {
        super((CMSProcessable)new CMSProcessableBodyPartInbound(message.getBodyPart(0), s), getInputStream((Part)message.getBodyPart(1)));
        this.message = message;
        this.content = (MimeBodyPart)message.getBodyPart(0);
    }
    
    public SMIMESigned(final Part message) throws MessagingException, CMSException, SMIMEException {
        super(getInputStream(message));
        this.message = message;
        final CMSTypedData signedContent = this.getSignedContent();
        if (signedContent != null) {
            this.content = SMIMEUtil.toMimeBodyPart((byte[])((CMSProcessable)signedContent).getContent());
        }
    }
    
    public MimeBodyPart getContent() {
        return this.content;
    }
    
    public MimeMessage getContentAsMimeMessage(final Session session) throws MessagingException, IOException {
        final Object content = this.getSignedContent().getContent();
        byte[] byteArray;
        if (content instanceof byte[]) {
            byteArray = (byte[])content;
        }
        else {
            if (!(content instanceof MimePart)) {
                String name = "<null>";
                if (content != null) {
                    name = ((MimePart)content).getClass().getName();
                }
                throw new MessagingException("Could not transfrom content of type " + name + " into MimeMessage.");
            }
            final MimePart mimePart = (MimePart)content;
            ByteArrayOutputStream byteArrayOutputStream;
            if (mimePart.getSize() > 0) {
                byteArrayOutputStream = new ByteArrayOutputStream(mimePart.getSize());
            }
            else {
                byteArrayOutputStream = new ByteArrayOutputStream();
            }
            mimePart.writeTo((OutputStream)byteArrayOutputStream);
            byteArray = byteArrayOutputStream.toByteArray();
        }
        if (byteArray != null) {
            return new MimeMessage(session, (InputStream)new ByteArrayInputStream(byteArray));
        }
        return null;
    }
    
    public Object getContentWithSignature() {
        return this.message;
    }
    
    static {
        final MailcapCommandMap mailcapCommandMap = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
        mailcapCommandMap.addMailcap("application/pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature");
        mailcapCommandMap.addMailcap("application/pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_mime");
        mailcapCommandMap.addMailcap("application/x-pkcs7-signature;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_signature");
        mailcapCommandMap.addMailcap("application/x-pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_mime");
        mailcapCommandMap.addMailcap("multipart/signed;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.multipart_signed");
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                CommandMap.setDefaultCommandMap(mailcapCommandMap);
                return null;
            }
        });
    }
}
