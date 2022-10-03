package org.bouncycastle.mail.smime;

import java.io.IOException;
import org.bouncycastle.cms.CMSCompressedDataStreamGenerator;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.activation.MailcapCommandMap;
import javax.activation.CommandMap;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import org.bouncycastle.operator.OutputCompressor;
import javax.mail.internet.MimeBodyPart;

public class SMIMECompressedGenerator extends SMIMEGenerator
{
    public static final String ZLIB = "1.2.840.113549.1.9.16.3.8";
    private static final String COMPRESSED_CONTENT_TYPE = "application/pkcs7-mime; name=\"smime.p7z\"; smime-type=compressed-data";
    
    private MimeBodyPart make(final MimeBodyPart mimeBodyPart, final OutputCompressor outputCompressor) throws SMIMEException {
        try {
            final MimeBodyPart mimeBodyPart2 = new MimeBodyPart();
            mimeBodyPart2.setContent((Object)new ContentCompressor(mimeBodyPart, outputCompressor), "application/pkcs7-mime; name=\"smime.p7z\"; smime-type=compressed-data");
            mimeBodyPart2.addHeader("Content-Type", "application/pkcs7-mime; name=\"smime.p7z\"; smime-type=compressed-data");
            mimeBodyPart2.addHeader("Content-Disposition", "attachment; filename=\"smime.p7z\"");
            mimeBodyPart2.addHeader("Content-Description", "S/MIME Compressed Message");
            mimeBodyPart2.addHeader("Content-Transfer-Encoding", this.encoding);
            return mimeBodyPart2;
        }
        catch (final MessagingException ex) {
            throw new SMIMEException("exception putting multi-part together.", (Exception)ex);
        }
    }
    
    public MimeBodyPart generate(final MimeBodyPart mimeBodyPart, final OutputCompressor outputCompressor) throws SMIMEException {
        return this.make(this.makeContentBodyPart(mimeBodyPart), outputCompressor);
    }
    
    public MimeBodyPart generate(final MimeMessage mimeMessage, final OutputCompressor outputCompressor) throws SMIMEException {
        try {
            mimeMessage.saveChanges();
        }
        catch (final MessagingException ex) {
            throw new SMIMEException("unable to save message", (Exception)ex);
        }
        return this.make(this.makeContentBodyPart(mimeMessage), outputCompressor);
    }
    
    static {
        final CommandMap defaultCommandMap = CommandMap.getDefaultCommandMap();
        if (defaultCommandMap instanceof MailcapCommandMap) {
            final MailcapCommandMap mailcapCommandMap = (MailcapCommandMap)defaultCommandMap;
            mailcapCommandMap.addMailcap("application/pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_mime");
            mailcapCommandMap.addMailcap("application/x-pkcs7-mime;; x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_mime");
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                public Object run() {
                    CommandMap.setDefaultCommandMap(mailcapCommandMap);
                    return null;
                }
            });
        }
    }
    
    private class ContentCompressor implements SMIMEStreamingProcessor
    {
        private final MimeBodyPart content;
        private final OutputCompressor compressor;
        
        ContentCompressor(final MimeBodyPart content, final OutputCompressor compressor) {
            this.content = content;
            this.compressor = compressor;
        }
        
        public void write(final OutputStream outputStream) throws IOException {
            final OutputStream open = new CMSCompressedDataStreamGenerator().open(outputStream, this.compressor);
            try {
                this.content.writeTo(open);
                open.close();
            }
            catch (final MessagingException ex) {
                throw new IOException(ex.toString());
            }
        }
    }
}
