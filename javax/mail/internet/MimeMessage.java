package javax.mail.internet;

import java.io.ObjectStreamException;
import javax.mail.Header;
import java.util.Enumeration;
import com.sun.mail.util.LineOutputStream;
import java.util.List;
import java.util.ArrayList;
import javax.mail.Part;
import javax.mail.Multipart;
import com.sun.mail.util.MessageRemovedIOException;
import javax.mail.MessageRemovedException;
import com.sun.mail.util.FolderClosedIOException;
import javax.mail.FolderClosedException;
import com.sun.mail.util.MimeUtil;
import java.text.ParseException;
import java.util.Date;
import java.io.UnsupportedEncodingException;
import javax.mail.Address;
import com.sun.mail.util.ASCIIUtility;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Properties;
import com.sun.mail.util.PropUtil;
import javax.mail.Folder;
import java.io.IOException;
import javax.mail.util.SharedByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Flags;
import java.io.InputStream;
import javax.activation.DataHandler;
import javax.mail.Message;

public class MimeMessage extends Message implements MimePart
{
    protected DataHandler dh;
    protected byte[] content;
    protected InputStream contentStream;
    protected InternetHeaders headers;
    protected Flags flags;
    protected boolean modified;
    protected boolean saved;
    protected Object cachedContent;
    private static final MailDateFormat mailDateFormat;
    private boolean strict;
    private boolean allowutf8;
    private static final Flags answeredFlag;
    
    public MimeMessage(final Session session) {
        super(session);
        this.modified = false;
        this.saved = false;
        this.strict = true;
        this.allowutf8 = false;
        this.modified = true;
        this.headers = new InternetHeaders();
        this.flags = new Flags();
        this.initStrict();
    }
    
    public MimeMessage(final Session session, final InputStream is) throws MessagingException {
        super(session);
        this.modified = false;
        this.saved = false;
        this.strict = true;
        this.allowutf8 = false;
        this.flags = new Flags();
        this.initStrict();
        this.parse(is);
        this.saved = true;
    }
    
    public MimeMessage(final MimeMessage source) throws MessagingException {
        super(source.session);
        this.modified = false;
        this.saved = false;
        this.strict = true;
        this.allowutf8 = false;
        this.flags = source.getFlags();
        if (this.flags == null) {
            this.flags = new Flags();
        }
        final int size = source.getSize();
        ByteArrayOutputStream bos;
        if (size > 0) {
            bos = new ByteArrayOutputStream(size);
        }
        else {
            bos = new ByteArrayOutputStream();
        }
        try {
            this.strict = source.strict;
            source.writeTo(bos);
            bos.close();
            final SharedByteArrayInputStream bis = new SharedByteArrayInputStream(bos.toByteArray());
            this.parse(bis);
            bis.close();
            this.saved = true;
        }
        catch (final IOException ex) {
            throw new MessagingException("IOException while copying message", ex);
        }
    }
    
    protected MimeMessage(final Folder folder, final int msgnum) {
        super(folder, msgnum);
        this.modified = false;
        this.saved = false;
        this.strict = true;
        this.allowutf8 = false;
        this.flags = new Flags();
        this.saved = true;
        this.initStrict();
    }
    
    protected MimeMessage(final Folder folder, final InputStream is, final int msgnum) throws MessagingException {
        this(folder, msgnum);
        this.initStrict();
        this.parse(is);
    }
    
    protected MimeMessage(final Folder folder, final InternetHeaders headers, final byte[] content, final int msgnum) throws MessagingException {
        this(folder, msgnum);
        this.headers = headers;
        this.content = content;
        this.initStrict();
    }
    
    private void initStrict() {
        if (this.session != null) {
            final Properties props = this.session.getProperties();
            this.strict = PropUtil.getBooleanProperty(props, "mail.mime.address.strict", true);
            this.allowutf8 = PropUtil.getBooleanProperty(props, "mail.mime.allowutf8", false);
        }
    }
    
    protected void parse(InputStream is) throws MessagingException {
        if (!(is instanceof ByteArrayInputStream) && !(is instanceof BufferedInputStream) && !(is instanceof SharedInputStream)) {
            is = new BufferedInputStream(is);
        }
        this.headers = this.createInternetHeaders(is);
        if (is instanceof SharedInputStream) {
            final SharedInputStream sis = (SharedInputStream)is;
            this.contentStream = sis.newStream(sis.getPosition(), -1L);
        }
        else {
            try {
                this.content = ASCIIUtility.getBytes(is);
            }
            catch (final IOException ioex) {
                throw new MessagingException("IOException", ioex);
            }
        }
        this.modified = false;
    }
    
    @Override
    public Address[] getFrom() throws MessagingException {
        Address[] a = this.getAddressHeader("From");
        if (a == null) {
            a = this.getAddressHeader("Sender");
        }
        return a;
    }
    
    @Override
    public void setFrom(final Address address) throws MessagingException {
        if (address == null) {
            this.removeHeader("From");
        }
        else {
            this.setHeader("From", MimeUtility.fold(6, address.toString()));
        }
    }
    
    public void setFrom(final String address) throws MessagingException {
        if (address == null) {
            this.removeHeader("From");
        }
        else {
            this.setAddressHeader("From", InternetAddress.parse(address));
        }
    }
    
    @Override
    public void setFrom() throws MessagingException {
        InternetAddress me = null;
        try {
            me = InternetAddress._getLocalAddress(this.session);
        }
        catch (final Exception ex) {
            throw new MessagingException("No From address", ex);
        }
        if (me != null) {
            this.setFrom(me);
            return;
        }
        throw new MessagingException("No From address");
    }
    
    @Override
    public void addFrom(final Address[] addresses) throws MessagingException {
        this.addAddressHeader("From", addresses);
    }
    
    public Address getSender() throws MessagingException {
        final Address[] a = this.getAddressHeader("Sender");
        if (a == null || a.length == 0) {
            return null;
        }
        return a[0];
    }
    
    public void setSender(final Address address) throws MessagingException {
        if (address == null) {
            this.removeHeader("Sender");
        }
        else {
            this.setHeader("Sender", MimeUtility.fold(8, address.toString()));
        }
    }
    
    @Override
    public Address[] getRecipients(final Message.RecipientType type) throws MessagingException {
        if (type == RecipientType.NEWSGROUPS) {
            final String s = this.getHeader("Newsgroups", ",");
            return (Address[])((s == null) ? null : NewsAddress.parse(s));
        }
        return this.getAddressHeader(this.getHeaderName(type));
    }
    
    @Override
    public Address[] getAllRecipients() throws MessagingException {
        final Address[] all = super.getAllRecipients();
        final Address[] ng = this.getRecipients(RecipientType.NEWSGROUPS);
        if (ng == null) {
            return all;
        }
        if (all == null) {
            return ng;
        }
        final Address[] addresses = new Address[all.length + ng.length];
        System.arraycopy(all, 0, addresses, 0, all.length);
        System.arraycopy(ng, 0, addresses, all.length, ng.length);
        return addresses;
    }
    
    @Override
    public void setRecipients(final Message.RecipientType type, final Address[] addresses) throws MessagingException {
        if (type == RecipientType.NEWSGROUPS) {
            if (addresses == null || addresses.length == 0) {
                this.removeHeader("Newsgroups");
            }
            else {
                this.setHeader("Newsgroups", NewsAddress.toString(addresses));
            }
        }
        else {
            this.setAddressHeader(this.getHeaderName(type), addresses);
        }
    }
    
    public void setRecipients(final Message.RecipientType type, final String addresses) throws MessagingException {
        if (type == RecipientType.NEWSGROUPS) {
            if (addresses == null || addresses.length() == 0) {
                this.removeHeader("Newsgroups");
            }
            else {
                this.setHeader("Newsgroups", addresses);
            }
        }
        else {
            this.setAddressHeader(this.getHeaderName(type), (Address[])((addresses == null) ? null : InternetAddress.parse(addresses)));
        }
    }
    
    @Override
    public void addRecipients(final Message.RecipientType type, final Address[] addresses) throws MessagingException {
        if (type == RecipientType.NEWSGROUPS) {
            final String s = NewsAddress.toString(addresses);
            if (s != null) {
                this.addHeader("Newsgroups", s);
            }
        }
        else {
            this.addAddressHeader(this.getHeaderName(type), addresses);
        }
    }
    
    public void addRecipients(final Message.RecipientType type, final String addresses) throws MessagingException {
        if (type == RecipientType.NEWSGROUPS) {
            if (addresses != null && addresses.length() != 0) {
                this.addHeader("Newsgroups", addresses);
            }
        }
        else {
            this.addAddressHeader(this.getHeaderName(type), InternetAddress.parse(addresses));
        }
    }
    
    @Override
    public Address[] getReplyTo() throws MessagingException {
        Address[] a = this.getAddressHeader("Reply-To");
        if (a == null || a.length == 0) {
            a = this.getFrom();
        }
        return a;
    }
    
    @Override
    public void setReplyTo(final Address[] addresses) throws MessagingException {
        this.setAddressHeader("Reply-To", addresses);
    }
    
    private Address[] getAddressHeader(final String name) throws MessagingException {
        final String s = this.getHeader(name, ",");
        return (Address[])((s == null) ? null : InternetAddress.parseHeader(s, this.strict));
    }
    
    private void setAddressHeader(final String name, final Address[] addresses) throws MessagingException {
        String s;
        if (this.allowutf8) {
            s = InternetAddress.toUnicodeString(addresses, name.length() + 2);
        }
        else {
            s = InternetAddress.toString(addresses, name.length() + 2);
        }
        if (s == null) {
            this.removeHeader(name);
        }
        else {
            this.setHeader(name, s);
        }
    }
    
    private void addAddressHeader(final String name, final Address[] addresses) throws MessagingException {
        if (addresses == null || addresses.length == 0) {
            return;
        }
        final Address[] a = this.getAddressHeader(name);
        Address[] anew;
        if (a == null || a.length == 0) {
            anew = addresses;
        }
        else {
            anew = new Address[a.length + addresses.length];
            System.arraycopy(a, 0, anew, 0, a.length);
            System.arraycopy(addresses, 0, anew, a.length, addresses.length);
        }
        String s;
        if (this.allowutf8) {
            s = InternetAddress.toUnicodeString(anew, name.length() + 2);
        }
        else {
            s = InternetAddress.toString(anew, name.length() + 2);
        }
        if (s == null) {
            return;
        }
        this.setHeader(name, s);
    }
    
    @Override
    public String getSubject() throws MessagingException {
        final String rawvalue = this.getHeader("Subject", null);
        if (rawvalue == null) {
            return null;
        }
        try {
            return MimeUtility.decodeText(MimeUtility.unfold(rawvalue));
        }
        catch (final UnsupportedEncodingException ex) {
            return rawvalue;
        }
    }
    
    @Override
    public void setSubject(final String subject) throws MessagingException {
        this.setSubject(subject, null);
    }
    
    public void setSubject(final String subject, final String charset) throws MessagingException {
        if (subject == null) {
            this.removeHeader("Subject");
        }
        else {
            try {
                this.setHeader("Subject", MimeUtility.fold(9, MimeUtility.encodeText(subject, charset, null)));
            }
            catch (final UnsupportedEncodingException uex) {
                throw new MessagingException("Encoding error", uex);
            }
        }
    }
    
    @Override
    public Date getSentDate() throws MessagingException {
        final String s = this.getHeader("Date", null);
        if (s != null) {
            try {
                synchronized (MimeMessage.mailDateFormat) {
                    return MimeMessage.mailDateFormat.parse(s);
                }
            }
            catch (final ParseException pex) {
                return null;
            }
        }
        return null;
    }
    
    @Override
    public void setSentDate(final Date d) throws MessagingException {
        if (d == null) {
            this.removeHeader("Date");
        }
        else {
            synchronized (MimeMessage.mailDateFormat) {
                this.setHeader("Date", MimeMessage.mailDateFormat.format(d));
            }
        }
    }
    
    @Override
    public Date getReceivedDate() throws MessagingException {
        return null;
    }
    
    @Override
    public int getSize() throws MessagingException {
        if (this.content != null) {
            return this.content.length;
        }
        if (this.contentStream != null) {
            try {
                final int size = this.contentStream.available();
                if (size > 0) {
                    return size;
                }
            }
            catch (final IOException ex) {}
        }
        return -1;
    }
    
    @Override
    public int getLineCount() throws MessagingException {
        return -1;
    }
    
    @Override
    public String getContentType() throws MessagingException {
        String s = this.getHeader("Content-Type", null);
        s = MimeUtil.cleanContentType(this, s);
        if (s == null) {
            return "text/plain";
        }
        return s;
    }
    
    @Override
    public boolean isMimeType(final String mimeType) throws MessagingException {
        return MimeBodyPart.isMimeType(this, mimeType);
    }
    
    @Override
    public String getDisposition() throws MessagingException {
        return MimeBodyPart.getDisposition(this);
    }
    
    @Override
    public void setDisposition(final String disposition) throws MessagingException {
        MimeBodyPart.setDisposition(this, disposition);
    }
    
    @Override
    public String getEncoding() throws MessagingException {
        return MimeBodyPart.getEncoding(this);
    }
    
    @Override
    public String getContentID() throws MessagingException {
        return this.getHeader("Content-Id", null);
    }
    
    public void setContentID(final String cid) throws MessagingException {
        if (cid == null) {
            this.removeHeader("Content-ID");
        }
        else {
            this.setHeader("Content-ID", cid);
        }
    }
    
    @Override
    public String getContentMD5() throws MessagingException {
        return this.getHeader("Content-MD5", null);
    }
    
    @Override
    public void setContentMD5(final String md5) throws MessagingException {
        this.setHeader("Content-MD5", md5);
    }
    
    @Override
    public String getDescription() throws MessagingException {
        return MimeBodyPart.getDescription(this);
    }
    
    @Override
    public void setDescription(final String description) throws MessagingException {
        this.setDescription(description, null);
    }
    
    public void setDescription(final String description, final String charset) throws MessagingException {
        MimeBodyPart.setDescription(this, description, charset);
    }
    
    @Override
    public String[] getContentLanguage() throws MessagingException {
        return MimeBodyPart.getContentLanguage(this);
    }
    
    @Override
    public void setContentLanguage(final String[] languages) throws MessagingException {
        MimeBodyPart.setContentLanguage(this, languages);
    }
    
    public String getMessageID() throws MessagingException {
        return this.getHeader("Message-ID", null);
    }
    
    @Override
    public String getFileName() throws MessagingException {
        return MimeBodyPart.getFileName(this);
    }
    
    @Override
    public void setFileName(final String filename) throws MessagingException {
        MimeBodyPart.setFileName(this, filename);
    }
    
    private String getHeaderName(final Message.RecipientType type) throws MessagingException {
        String headerName;
        if (type == Message.RecipientType.TO) {
            headerName = "To";
        }
        else if (type == Message.RecipientType.CC) {
            headerName = "Cc";
        }
        else if (type == Message.RecipientType.BCC) {
            headerName = "Bcc";
        }
        else {
            if (type != RecipientType.NEWSGROUPS) {
                throw new MessagingException("Invalid Recipient Type");
            }
            headerName = "Newsgroups";
        }
        return headerName;
    }
    
    @Override
    public InputStream getInputStream() throws IOException, MessagingException {
        return this.getDataHandler().getInputStream();
    }
    
    protected InputStream getContentStream() throws MessagingException {
        if (this.contentStream != null) {
            return ((SharedInputStream)this.contentStream).newStream(0L, -1L);
        }
        if (this.content != null) {
            return new SharedByteArrayInputStream(this.content);
        }
        throw new MessagingException("No MimeMessage content");
    }
    
    public InputStream getRawInputStream() throws MessagingException {
        return this.getContentStream();
    }
    
    @Override
    public synchronized DataHandler getDataHandler() throws MessagingException {
        if (this.dh == null) {
            this.dh = new MimeBodyPart.MimePartDataHandler(this);
        }
        return this.dh;
    }
    
    @Override
    public Object getContent() throws IOException, MessagingException {
        if (this.cachedContent != null) {
            return this.cachedContent;
        }
        Object c;
        try {
            c = this.getDataHandler().getContent();
        }
        catch (final FolderClosedIOException fex) {
            throw new FolderClosedException(fex.getFolder(), fex.getMessage());
        }
        catch (final MessageRemovedIOException mex) {
            throw new MessageRemovedException(mex.getMessage());
        }
        if (MimeBodyPart.cacheMultipart && (c instanceof Multipart || c instanceof Message) && (this.content != null || this.contentStream != null)) {
            this.cachedContent = c;
            if (c instanceof MimeMultipart) {
                ((MimeMultipart)c).parse();
            }
        }
        return c;
    }
    
    @Override
    public synchronized void setDataHandler(final DataHandler dh) throws MessagingException {
        this.dh = dh;
        this.cachedContent = null;
        MimeBodyPart.invalidateContentHeaders(this);
    }
    
    @Override
    public void setContent(final Object o, final String type) throws MessagingException {
        if (o instanceof Multipart) {
            this.setContent((Multipart)o);
        }
        else {
            this.setDataHandler(new DataHandler(o, type));
        }
    }
    
    @Override
    public void setText(final String text) throws MessagingException {
        this.setText(text, null);
    }
    
    @Override
    public void setText(final String text, final String charset) throws MessagingException {
        MimeBodyPart.setText(this, text, charset, "plain");
    }
    
    @Override
    public void setText(final String text, final String charset, final String subtype) throws MessagingException {
        MimeBodyPart.setText(this, text, charset, subtype);
    }
    
    @Override
    public void setContent(final Multipart mp) throws MessagingException {
        this.setDataHandler(new DataHandler(mp, mp.getContentType()));
        mp.setParent(this);
    }
    
    @Override
    public Message reply(final boolean replyToAll) throws MessagingException {
        return this.reply(replyToAll, true);
    }
    
    public Message reply(final boolean replyToAll, final boolean setAnswered) throws MessagingException {
        final MimeMessage reply = this.createMimeMessage(this.session);
        String subject = this.getHeader("Subject", null);
        if (subject != null) {
            if (!subject.regionMatches(true, 0, "Re: ", 0, 4)) {
                subject = "Re: " + subject;
            }
            reply.setHeader("Subject", subject);
        }
        Address[] a = this.getReplyTo();
        reply.setRecipients(Message.RecipientType.TO, a);
        if (replyToAll) {
            final List<Address> v = new ArrayList<Address>();
            final InternetAddress me = InternetAddress.getLocalAddress(this.session);
            if (me != null) {
                v.add(me);
            }
            String alternates = null;
            if (this.session != null) {
                alternates = this.session.getProperty("mail.alternates");
            }
            if (alternates != null) {
                this.eliminateDuplicates(v, InternetAddress.parse(alternates, false));
            }
            final String replyallccStr = null;
            boolean replyallcc = false;
            if (this.session != null) {
                replyallcc = PropUtil.getBooleanProperty(this.session.getProperties(), "mail.replyallcc", false);
            }
            this.eliminateDuplicates(v, a);
            a = this.getRecipients(Message.RecipientType.TO);
            a = this.eliminateDuplicates(v, a);
            if (a != null && a.length > 0) {
                if (replyallcc) {
                    reply.addRecipients(Message.RecipientType.CC, a);
                }
                else {
                    reply.addRecipients(Message.RecipientType.TO, a);
                }
            }
            a = this.getRecipients(Message.RecipientType.CC);
            a = this.eliminateDuplicates(v, a);
            if (a != null && a.length > 0) {
                reply.addRecipients(Message.RecipientType.CC, a);
            }
            a = this.getRecipients(RecipientType.NEWSGROUPS);
            if (a != null && a.length > 0) {
                reply.setRecipients(RecipientType.NEWSGROUPS, a);
            }
        }
        final String msgId = this.getHeader("Message-Id", null);
        if (msgId != null) {
            reply.setHeader("In-Reply-To", msgId);
        }
        String refs = this.getHeader("References", " ");
        if (refs == null) {
            refs = this.getHeader("In-Reply-To", " ");
        }
        if (msgId != null) {
            if (refs != null) {
                refs = MimeUtility.unfold(refs) + " " + msgId;
            }
            else {
                refs = msgId;
            }
        }
        if (refs != null) {
            reply.setHeader("References", MimeUtility.fold(12, refs));
        }
        if (setAnswered) {
            try {
                this.setFlags(MimeMessage.answeredFlag, true);
            }
            catch (final MessagingException ex) {}
        }
        return reply;
    }
    
    private Address[] eliminateDuplicates(final List<Address> v, Address[] addrs) {
        if (addrs == null) {
            return null;
        }
        int gone = 0;
        for (int i = 0; i < addrs.length; ++i) {
            boolean found = false;
            for (int j = 0; j < v.size(); ++j) {
                if (v.get(j).equals(addrs[i])) {
                    found = true;
                    ++gone;
                    addrs[i] = null;
                    break;
                }
            }
            if (!found) {
                v.add(addrs[i]);
            }
        }
        if (gone != 0) {
            Address[] a;
            if (addrs instanceof InternetAddress[]) {
                a = new InternetAddress[addrs.length - gone];
            }
            else {
                a = new Address[addrs.length - gone];
            }
            int k = 0;
            int j = 0;
            while (k < addrs.length) {
                if (addrs[k] != null) {
                    a[j++] = addrs[k];
                }
                ++k;
            }
            addrs = a;
        }
        return addrs;
    }
    
    @Override
    public void writeTo(final OutputStream os) throws IOException, MessagingException {
        this.writeTo(os, null);
    }
    
    public void writeTo(final OutputStream os, final String[] ignoreList) throws IOException, MessagingException {
        if (!this.saved) {
            this.saveChanges();
        }
        if (this.modified) {
            MimeBodyPart.writeTo(this, os, ignoreList);
            return;
        }
        final Enumeration<String> hdrLines = this.getNonMatchingHeaderLines(ignoreList);
        final LineOutputStream los = new LineOutputStream(os, this.allowutf8);
        while (hdrLines.hasMoreElements()) {
            los.writeln(hdrLines.nextElement());
        }
        los.writeln();
        if (this.content == null) {
            InputStream is = null;
            byte[] buf = new byte[8192];
            try {
                is = this.getContentStream();
                int len;
                while ((len = is.read(buf)) > 0) {
                    os.write(buf, 0, len);
                }
            }
            finally {
                if (is != null) {
                    is.close();
                }
                buf = null;
            }
        }
        else {
            os.write(this.content);
        }
        os.flush();
    }
    
    @Override
    public String[] getHeader(final String name) throws MessagingException {
        return this.headers.getHeader(name);
    }
    
    @Override
    public String getHeader(final String name, final String delimiter) throws MessagingException {
        return this.headers.getHeader(name, delimiter);
    }
    
    @Override
    public void setHeader(final String name, final String value) throws MessagingException {
        this.headers.setHeader(name, value);
    }
    
    @Override
    public void addHeader(final String name, final String value) throws MessagingException {
        this.headers.addHeader(name, value);
    }
    
    @Override
    public void removeHeader(final String name) throws MessagingException {
        this.headers.removeHeader(name);
    }
    
    @Override
    public Enumeration<Header> getAllHeaders() throws MessagingException {
        return this.headers.getAllHeaders();
    }
    
    @Override
    public Enumeration<Header> getMatchingHeaders(final String[] names) throws MessagingException {
        return this.headers.getMatchingHeaders(names);
    }
    
    @Override
    public Enumeration<Header> getNonMatchingHeaders(final String[] names) throws MessagingException {
        return this.headers.getNonMatchingHeaders(names);
    }
    
    @Override
    public void addHeaderLine(final String line) throws MessagingException {
        this.headers.addHeaderLine(line);
    }
    
    @Override
    public Enumeration<String> getAllHeaderLines() throws MessagingException {
        return this.headers.getAllHeaderLines();
    }
    
    @Override
    public Enumeration<String> getMatchingHeaderLines(final String[] names) throws MessagingException {
        return this.headers.getMatchingHeaderLines(names);
    }
    
    @Override
    public Enumeration<String> getNonMatchingHeaderLines(final String[] names) throws MessagingException {
        return this.headers.getNonMatchingHeaderLines(names);
    }
    
    @Override
    public synchronized Flags getFlags() throws MessagingException {
        return (Flags)this.flags.clone();
    }
    
    @Override
    public synchronized boolean isSet(final Flags.Flag flag) throws MessagingException {
        return this.flags.contains(flag);
    }
    
    @Override
    public synchronized void setFlags(final Flags flag, final boolean set) throws MessagingException {
        if (set) {
            this.flags.add(flag);
        }
        else {
            this.flags.remove(flag);
        }
    }
    
    @Override
    public void saveChanges() throws MessagingException {
        this.modified = true;
        this.saved = true;
        this.updateHeaders();
    }
    
    protected void updateMessageID() throws MessagingException {
        this.setHeader("Message-ID", "<" + UniqueValue.getUniqueMessageIDValue(this.session) + ">");
    }
    
    protected synchronized void updateHeaders() throws MessagingException {
        MimeBodyPart.updateHeaders(this);
        this.setHeader("MIME-Version", "1.0");
        if (this.getHeader("Date") == null) {
            this.setSentDate(new Date());
        }
        this.updateMessageID();
        if (this.cachedContent != null) {
            this.dh = new DataHandler(this.cachedContent, this.getContentType());
            this.cachedContent = null;
            this.content = null;
            if (this.contentStream != null) {
                try {
                    this.contentStream.close();
                }
                catch (final IOException ex) {}
            }
            this.contentStream = null;
        }
    }
    
    protected InternetHeaders createInternetHeaders(final InputStream is) throws MessagingException {
        return new InternetHeaders(is, this.allowutf8);
    }
    
    protected MimeMessage createMimeMessage(final Session session) throws MessagingException {
        return new MimeMessage(session);
    }
    
    static {
        mailDateFormat = new MailDateFormat();
        answeredFlag = new Flags(Flags.Flag.ANSWERED);
    }
    
    public static class RecipientType extends Message.RecipientType
    {
        private static final long serialVersionUID = -5468290701714395543L;
        public static final RecipientType NEWSGROUPS;
        
        protected RecipientType(final String type) {
            super(type);
        }
        
        @Override
        protected Object readResolve() throws ObjectStreamException {
            if (this.type.equals("Newsgroups")) {
                return RecipientType.NEWSGROUPS;
            }
            return super.readResolve();
        }
        
        static {
            NEWSGROUPS = new RecipientType("Newsgroups");
        }
    }
}
