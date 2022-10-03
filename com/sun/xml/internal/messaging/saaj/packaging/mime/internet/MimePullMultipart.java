package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import javax.xml.soap.AttachmentPart;
import java.util.Iterator;
import java.util.List;
import com.sun.xml.internal.messaging.saaj.soap.AttachmentPartImpl;
import java.io.IOException;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEConfig;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import javax.activation.DataSource;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage;
import java.io.InputStream;

public class MimePullMultipart extends MimeMultipart
{
    private InputStream in;
    private String boundary;
    private MIMEMessage mm;
    private DataSource dataSource;
    private ContentType contType;
    private String startParam;
    private MIMEPart soapPart;
    
    public MimePullMultipart(final DataSource ds, final ContentType ct) throws MessagingException {
        this.in = null;
        this.boundary = null;
        this.mm = null;
        this.dataSource = null;
        this.contType = null;
        this.startParam = null;
        this.soapPart = null;
        this.parsed = false;
        if (ct == null) {
            this.contType = new ContentType(ds.getContentType());
        }
        else {
            this.contType = ct;
        }
        this.dataSource = ds;
        this.boundary = this.contType.getParameter("boundary");
    }
    
    public MIMEPart readAndReturnSOAPPart() throws MessagingException {
        if (this.soapPart != null) {
            throw new MessagingException("Inputstream from datasource was already consumed");
        }
        this.readSOAPPart();
        return this.soapPart;
    }
    
    protected void readSOAPPart() throws MessagingException {
        try {
            if (this.soapPart != null) {
                return;
            }
            this.in = this.dataSource.getInputStream();
            final MIMEConfig config = new MIMEConfig();
            this.mm = new MIMEMessage(this.in, this.boundary, config);
            String st = this.contType.getParameter("start");
            if (this.startParam == null) {
                this.soapPart = this.mm.getPart(0);
            }
            else {
                if (st != null && st.length() > 2 && st.charAt(0) == '<' && st.charAt(st.length() - 1) == '>') {
                    st = st.substring(1, st.length() - 1);
                }
                this.startParam = st;
                this.soapPart = this.mm.getPart(this.startParam);
            }
        }
        catch (final IOException ex) {
            throw new MessagingException("No inputstream from datasource", ex);
        }
    }
    
    public void parseAll() throws MessagingException {
        if (this.parsed) {
            return;
        }
        if (this.soapPart == null) {
            this.readSOAPPart();
        }
        final List<MIMEPart> prts = this.mm.getAttachments();
        for (final MIMEPart part : prts) {
            if (part != this.soapPart) {
                final AttachmentPart attach = new AttachmentPartImpl(part);
                this.addBodyPart(new MimeBodyPart(part));
            }
        }
        this.parsed = true;
    }
    
    @Override
    protected void parse() throws MessagingException {
        this.parseAll();
    }
}
