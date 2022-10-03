package com.sun.xml.internal.messaging.saaj.soap;

import javax.activation.CommandInfo;
import javax.activation.MailcapCommandMap;
import javax.activation.CommandMap;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.InternetHeaders;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
import javax.xml.soap.MimeHeader;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePartDataSource;
import java.io.InputStream;
import javax.activation.DataSource;
import javax.xml.soap.SOAPException;
import java.io.OutputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.util.logging.Level;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import com.sun.xml.internal.org.jvnet.mimepull.Header;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import javax.activation.DataHandler;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import javax.xml.soap.MimeHeaders;
import java.util.logging.Logger;
import javax.xml.soap.AttachmentPart;

public class AttachmentPartImpl extends AttachmentPart
{
    protected static final Logger log;
    private final MimeHeaders headers;
    private MimeBodyPart rawContent;
    private DataHandler dataHandler;
    private MIMEPart mimePart;
    
    public AttachmentPartImpl() {
        this.rawContent = null;
        this.dataHandler = null;
        this.mimePart = null;
        this.headers = new MimeHeaders();
        initializeJavaActivationHandlers();
    }
    
    public AttachmentPartImpl(final MIMEPart part) {
        this.rawContent = null;
        this.dataHandler = null;
        this.mimePart = null;
        this.headers = new MimeHeaders();
        this.mimePart = part;
        final List<? extends Header> hdrs = part.getAllHeaders();
        for (final Header hd : hdrs) {
            this.headers.addHeader(hd.getName(), hd.getValue());
        }
    }
    
    @Override
    public int getSize() throws SOAPException {
        if (this.mimePart != null) {
            try {
                return this.mimePart.read().available();
            }
            catch (final IOException e) {
                return -1;
            }
        }
        if (this.rawContent == null && this.dataHandler == null) {
            return 0;
        }
        if (this.rawContent != null) {
            try {
                return this.rawContent.getSize();
            }
            catch (final Exception ex) {
                AttachmentPartImpl.log.log(Level.SEVERE, "SAAJ0573.soap.attachment.getrawbytes.ioexception", new String[] { ex.getLocalizedMessage() });
                throw new SOAPExceptionImpl("Raw InputStream Error: " + ex);
            }
        }
        final ByteOutputStream bout = new ByteOutputStream();
        try {
            this.dataHandler.writeTo(bout);
        }
        catch (final IOException ex2) {
            AttachmentPartImpl.log.log(Level.SEVERE, "SAAJ0501.soap.data.handler.err", new String[] { ex2.getLocalizedMessage() });
            throw new SOAPExceptionImpl("Data handler error: " + ex2);
        }
        return bout.size();
    }
    
    @Override
    public void clearContent() {
        if (this.mimePart != null) {
            this.mimePart.close();
            this.mimePart = null;
        }
        this.dataHandler = null;
        this.rawContent = null;
    }
    
    @Override
    public Object getContent() throws SOAPException {
        try {
            if (this.mimePart != null) {
                return this.mimePart.read();
            }
            if (this.dataHandler != null) {
                return this.getDataHandler().getContent();
            }
            if (this.rawContent != null) {
                return this.rawContent.getContent();
            }
            AttachmentPartImpl.log.severe("SAAJ0572.soap.no.content.for.attachment");
            throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
        }
        catch (final Exception ex) {
            AttachmentPartImpl.log.log(Level.SEVERE, "SAAJ0575.soap.attachment.getcontent.exception", ex);
            throw new SOAPExceptionImpl(ex.getLocalizedMessage());
        }
    }
    
    @Override
    public void setContent(final Object object, final String contentType) throws IllegalArgumentException {
        if (this.mimePart != null) {
            this.mimePart.close();
            this.mimePart = null;
        }
        final DataHandler dh = new DataHandler(object, contentType);
        this.setDataHandler(dh);
    }
    
    @Override
    public DataHandler getDataHandler() throws SOAPException {
        if (this.mimePart != null) {
            return new DataHandler(new DataSource() {
                @Override
                public InputStream getInputStream() throws IOException {
                    return AttachmentPartImpl.this.mimePart.read();
                }
                
                @Override
                public OutputStream getOutputStream() throws IOException {
                    throw new UnsupportedOperationException("getOutputStream cannot be supported : You have enabled LazyAttachments Option");
                }
                
                @Override
                public String getContentType() {
                    return AttachmentPartImpl.this.mimePart.getContentType();
                }
                
                @Override
                public String getName() {
                    return "MIMEPart Wrapper DataSource";
                }
            });
        }
        if (this.dataHandler != null) {
            return this.dataHandler;
        }
        if (this.rawContent != null) {
            return new DataHandler(new MimePartDataSource(this.rawContent));
        }
        AttachmentPartImpl.log.severe("SAAJ0502.soap.no.handler.for.attachment");
        throw new SOAPExceptionImpl("No data handler associated with this attachment");
    }
    
    @Override
    public void setDataHandler(final DataHandler dataHandler) throws IllegalArgumentException {
        if (this.mimePart != null) {
            this.mimePart.close();
            this.mimePart = null;
        }
        if (dataHandler == null) {
            AttachmentPartImpl.log.severe("SAAJ0503.soap.no.null.to.dataHandler");
            throw new IllegalArgumentException("Null dataHandler argument to setDataHandler");
        }
        this.dataHandler = dataHandler;
        this.rawContent = null;
        if (AttachmentPartImpl.log.isLoggable(Level.FINE)) {
            AttachmentPartImpl.log.log(Level.FINE, "SAAJ0580.soap.set.Content-Type", new String[] { dataHandler.getContentType() });
        }
        this.setMimeHeader("Content-Type", dataHandler.getContentType());
    }
    
    @Override
    public void removeAllMimeHeaders() {
        this.headers.removeAllHeaders();
    }
    
    @Override
    public void removeMimeHeader(final String header) {
        this.headers.removeHeader(header);
    }
    
    @Override
    public String[] getMimeHeader(final String name) {
        return this.headers.getHeader(name);
    }
    
    @Override
    public void setMimeHeader(final String name, final String value) {
        this.headers.setHeader(name, value);
    }
    
    @Override
    public void addMimeHeader(final String name, final String value) {
        this.headers.addHeader(name, value);
    }
    
    @Override
    public Iterator getAllMimeHeaders() {
        return this.headers.getAllHeaders();
    }
    
    @Override
    public Iterator getMatchingMimeHeaders(final String[] names) {
        return this.headers.getMatchingHeaders(names);
    }
    
    @Override
    public Iterator getNonMatchingMimeHeaders(final String[] names) {
        return this.headers.getNonMatchingHeaders(names);
    }
    
    boolean hasAllHeaders(final MimeHeaders hdrs) {
        if (hdrs != null) {
            final Iterator i = hdrs.getAllHeaders();
            while (i.hasNext()) {
                final MimeHeader hdr = i.next();
                final String[] values = this.headers.getHeader(hdr.getName());
                boolean found = false;
                if (values != null) {
                    for (int j = 0; j < values.length; ++j) {
                        if (hdr.getValue().equalsIgnoreCase(values[j])) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }
        return true;
    }
    
    MimeBodyPart getMimePart() throws SOAPException {
        try {
            if (this.mimePart != null) {
                return new MimeBodyPart(this.mimePart);
            }
            if (this.rawContent != null) {
                copyMimeHeaders(this.headers, this.rawContent);
                return this.rawContent;
            }
            final MimeBodyPart envelope = new MimeBodyPart();
            envelope.setDataHandler(this.dataHandler);
            copyMimeHeaders(this.headers, envelope);
            return envelope;
        }
        catch (final Exception ex) {
            AttachmentPartImpl.log.severe("SAAJ0504.soap.cannot.externalize.attachment");
            throw new SOAPExceptionImpl("Unable to externalize attachment", ex);
        }
    }
    
    public static void copyMimeHeaders(final MimeHeaders headers, final MimeBodyPart mbp) throws SOAPException {
        final Iterator i = headers.getAllHeaders();
        while (i.hasNext()) {
            try {
                final MimeHeader mh = i.next();
                mbp.setHeader(mh.getName(), mh.getValue());
                continue;
            }
            catch (final Exception ex) {
                AttachmentPartImpl.log.severe("SAAJ0505.soap.cannot.copy.mime.hdr");
                throw new SOAPExceptionImpl("Unable to copy MIME header", ex);
            }
            break;
        }
    }
    
    public static void copyMimeHeaders(final MimeBodyPart mbp, final AttachmentPartImpl ap) throws SOAPException {
        try {
            final List hdr = mbp.getAllHeaders();
            for (int sz = hdr.size(), i = 0; i < sz; ++i) {
                final com.sun.xml.internal.messaging.saaj.packaging.mime.Header h = hdr.get(i);
                if (!h.getName().equalsIgnoreCase("Content-Type")) {
                    ap.addMimeHeader(h.getName(), h.getValue());
                }
            }
        }
        catch (final Exception ex) {
            AttachmentPartImpl.log.severe("SAAJ0506.soap.cannot.copy.mime.hdrs.into.attachment");
            throw new SOAPExceptionImpl("Unable to copy MIME headers into attachment", ex);
        }
    }
    
    @Override
    public void setBase64Content(final InputStream content, final String contentType) throws SOAPException {
        if (this.mimePart != null) {
            this.mimePart.close();
            this.mimePart = null;
        }
        this.dataHandler = null;
        InputStream decoded = null;
        try {
            decoded = MimeUtility.decode(content, "base64");
            final InternetHeaders hdrs = new InternetHeaders();
            hdrs.setHeader("Content-Type", contentType);
            final ByteOutputStream bos = new ByteOutputStream();
            bos.write(decoded);
            this.rawContent = new MimeBodyPart(hdrs, bos.getBytes(), bos.getCount());
            this.setMimeHeader("Content-Type", contentType);
        }
        catch (final Exception e) {
            AttachmentPartImpl.log.log(Level.SEVERE, "SAAJ0578.soap.attachment.setbase64content.exception", e);
            throw new SOAPExceptionImpl(e.getLocalizedMessage());
        }
        finally {
            try {
                decoded.close();
            }
            catch (final IOException ex) {
                throw new SOAPException(ex);
            }
        }
    }
    
    @Override
    public InputStream getBase64Content() throws SOAPException {
        InputStream stream = null;
        Label_0133: {
            if (this.mimePart == null) {
                if (this.rawContent != null) {
                    try {
                        stream = this.rawContent.getInputStream();
                        break Label_0133;
                    }
                    catch (final Exception e) {
                        AttachmentPartImpl.log.log(Level.SEVERE, "SAAJ0579.soap.attachment.getbase64content.exception", e);
                        throw new SOAPExceptionImpl(e.getLocalizedMessage());
                    }
                }
                if (this.dataHandler != null) {
                    try {
                        stream = this.dataHandler.getInputStream();
                        break Label_0133;
                    }
                    catch (final IOException e2) {
                        AttachmentPartImpl.log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
                        throw new SOAPExceptionImpl("DataHandler error" + e2);
                    }
                }
                AttachmentPartImpl.log.severe("SAAJ0572.soap.no.content.for.attachment");
                throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
            }
            stream = this.mimePart.read();
        }
        final int size = 1024;
        if (stream != null) {
            try {
                final ByteArrayOutputStream bos = new ByteArrayOutputStream(size);
                final OutputStream ret = MimeUtility.encode(bos, "base64");
                byte[] buf = new byte[size];
                int len;
                while ((len = stream.read(buf, 0, size)) != -1) {
                    ret.write(buf, 0, len);
                }
                ret.flush();
                buf = bos.toByteArray();
                return new ByteArrayInputStream(buf);
            }
            catch (final Exception e3) {
                AttachmentPartImpl.log.log(Level.SEVERE, "SAAJ0579.soap.attachment.getbase64content.exception", e3);
                throw new SOAPExceptionImpl(e3.getLocalizedMessage());
            }
            finally {
                try {
                    stream.close();
                }
                catch (final IOException ex) {}
            }
        }
        AttachmentPartImpl.log.log(Level.SEVERE, "SAAJ0572.soap.no.content.for.attachment");
        throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
    }
    
    @Override
    public void setRawContent(final InputStream content, final String contentType) throws SOAPException {
        if (this.mimePart != null) {
            this.mimePart.close();
            this.mimePart = null;
        }
        this.dataHandler = null;
        try {
            final InternetHeaders hdrs = new InternetHeaders();
            hdrs.setHeader("Content-Type", contentType);
            final ByteOutputStream bos = new ByteOutputStream();
            bos.write(content);
            this.rawContent = new MimeBodyPart(hdrs, bos.getBytes(), bos.getCount());
            this.setMimeHeader("Content-Type", contentType);
        }
        catch (final Exception e) {
            AttachmentPartImpl.log.log(Level.SEVERE, "SAAJ0576.soap.attachment.setrawcontent.exception", e);
            throw new SOAPExceptionImpl(e.getLocalizedMessage());
        }
        finally {
            try {
                content.close();
            }
            catch (final IOException ex) {
                throw new SOAPException(ex);
            }
        }
    }
    
    @Override
    public void setRawContentBytes(final byte[] content, final int off, final int len, final String contentType) throws SOAPException {
        if (this.mimePart != null) {
            this.mimePart.close();
            this.mimePart = null;
        }
        if (content == null) {
            throw new SOAPExceptionImpl("Null content passed to setRawContentBytes");
        }
        this.dataHandler = null;
        try {
            final InternetHeaders hdrs = new InternetHeaders();
            hdrs.setHeader("Content-Type", contentType);
            this.rawContent = new MimeBodyPart(hdrs, content, off, len);
            this.setMimeHeader("Content-Type", contentType);
        }
        catch (final Exception e) {
            AttachmentPartImpl.log.log(Level.SEVERE, "SAAJ0576.soap.attachment.setrawcontent.exception", e);
            throw new SOAPExceptionImpl(e.getLocalizedMessage());
        }
    }
    
    @Override
    public InputStream getRawContent() throws SOAPException {
        if (this.mimePart != null) {
            return this.mimePart.read();
        }
        if (this.rawContent != null) {
            try {
                return this.rawContent.getInputStream();
            }
            catch (final Exception e) {
                AttachmentPartImpl.log.log(Level.SEVERE, "SAAJ0577.soap.attachment.getrawcontent.exception", e);
                throw new SOAPExceptionImpl(e.getLocalizedMessage());
            }
        }
        if (this.dataHandler != null) {
            try {
                return this.dataHandler.getInputStream();
            }
            catch (final IOException e2) {
                AttachmentPartImpl.log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
                throw new SOAPExceptionImpl("DataHandler error" + e2);
            }
        }
        AttachmentPartImpl.log.severe("SAAJ0572.soap.no.content.for.attachment");
        throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
    }
    
    @Override
    public byte[] getRawContentBytes() throws SOAPException {
        if (this.mimePart != null) {
            try {
                final InputStream ret = this.mimePart.read();
                return ASCIIUtility.getBytes(ret);
            }
            catch (final IOException ex) {
                AttachmentPartImpl.log.log(Level.SEVERE, "SAAJ0577.soap.attachment.getrawcontent.exception", ex);
                throw new SOAPExceptionImpl(ex);
            }
        }
        if (this.rawContent != null) {
            try {
                final InputStream ret = this.rawContent.getInputStream();
                return ASCIIUtility.getBytes(ret);
            }
            catch (final Exception e) {
                AttachmentPartImpl.log.log(Level.SEVERE, "SAAJ0577.soap.attachment.getrawcontent.exception", e);
                throw new SOAPExceptionImpl(e);
            }
        }
        if (this.dataHandler != null) {
            try {
                final InputStream ret = this.dataHandler.getInputStream();
                return ASCIIUtility.getBytes(ret);
            }
            catch (final IOException e2) {
                AttachmentPartImpl.log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
                throw new SOAPExceptionImpl("DataHandler error" + e2);
            }
        }
        AttachmentPartImpl.log.severe("SAAJ0572.soap.no.content.for.attachment");
        throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    public MimeHeaders getMimeHeaders() {
        return this.headers;
    }
    
    public static void initializeJavaActivationHandlers() {
        try {
            final CommandMap map = CommandMap.getDefaultCommandMap();
            if (map instanceof MailcapCommandMap) {
                final MailcapCommandMap mailMap = (MailcapCommandMap)map;
                if (!cmdMapInitialized(mailMap)) {
                    mailMap.addMailcap("text/xml;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.XmlDataContentHandler");
                    mailMap.addMailcap("application/xml;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.XmlDataContentHandler");
                    mailMap.addMailcap("application/fastinfoset;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.FastInfosetDataContentHandler");
                    mailMap.addMailcap("image/*;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.ImageDataContentHandler");
                    mailMap.addMailcap("text/plain;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.StringDataContentHandler");
                }
            }
        }
        catch (final Throwable t) {}
    }
    
    private static boolean cmdMapInitialized(final MailcapCommandMap mailMap) {
        final CommandInfo[] commands = mailMap.getAllCommands("application/fastinfoset");
        if (commands == null || commands.length == 0) {
            return false;
        }
        final String saajClassName = "com.sun.xml.internal.ws.binding.FastInfosetDataContentHandler";
        for (final CommandInfo command : commands) {
            final String commandClass = command.getCommandClass();
            if (saajClassName.equals(commandClass)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
    }
}
