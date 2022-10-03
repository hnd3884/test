package com.sun.mail.util.logging;

import java.util.Hashtable;
import javax.mail.PasswordAuthentication;
import javax.mail.MessageContext;
import javax.mail.Service;
import java.security.AccessController;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.util.ResourceBundle;
import java.util.Date;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.Address;
import java.net.UnknownHostException;
import java.net.InetAddress;
import javax.mail.Multipart;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.Transport;
import java.util.logging.SimpleFormatter;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import javax.mail.internet.ContentType;
import javax.activation.DataSource;
import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimeUtility;
import javax.mail.MessagingException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.Charset;
import java.io.UnsupportedEncodingException;
import javax.mail.Message;
import java.util.logging.ErrorManager;
import javax.activation.FileTypeMap;
import java.util.logging.Level;
import java.util.Comparator;
import java.util.logging.LogRecord;
import javax.mail.Session;
import javax.mail.Authenticator;
import java.util.Properties;
import java.security.PrivilegedAction;
import java.util.logging.Formatter;
import java.util.logging.Filter;
import java.util.logging.Handler;

public class MailHandler extends Handler
{
    private static final Filter[] EMPTY_FILTERS;
    private static final Formatter[] EMPTY_FORMATTERS;
    private static final int MIN_HEADER_SIZE = 1024;
    private static final int offValue;
    private static final PrivilegedAction<Object> MAILHANDLER_LOADER;
    private static final ThreadLocal<Integer> MUTEX;
    private static final Integer MUTEX_PUBLISH;
    private static final Integer MUTEX_REPORT;
    private static final Integer MUTEX_LINKAGE;
    private volatile boolean sealed;
    private boolean isWriting;
    private Properties mailProps;
    private Authenticator auth;
    private Session session;
    private int[] matched;
    private LogRecord[] data;
    private int size;
    private int capacity;
    private Comparator<? super LogRecord> comparator;
    private Formatter subjectFormatter;
    private Level pushLevel;
    private Filter pushFilter;
    private volatile Filter filter;
    private volatile Level logLevel;
    private volatile Filter[] attachmentFilters;
    private String encoding;
    private Formatter formatter;
    private Formatter[] attachmentFormatters;
    private Formatter[] attachmentNames;
    private FileTypeMap contentTypes;
    private volatile ErrorManager errorManager;
    
    public MailHandler() {
        this.logLevel = Level.ALL;
        this.errorManager = this.defaultErrorManager();
        this.init(null);
        this.sealed = true;
        this.checkAccess();
    }
    
    public MailHandler(final int capacity) {
        this.logLevel = Level.ALL;
        this.errorManager = this.defaultErrorManager();
        this.init(null);
        this.sealed = true;
        this.setCapacity0(capacity);
    }
    
    public MailHandler(final Properties props) {
        this.logLevel = Level.ALL;
        this.errorManager = this.defaultErrorManager();
        if (props == null) {
            throw new NullPointerException();
        }
        this.init(props);
        this.sealed = true;
        this.setMailProperties0(props);
    }
    
    @Override
    public boolean isLoggable(final LogRecord record) {
        final int levelValue = this.getLevel().intValue();
        if (record.getLevel().intValue() < levelValue || levelValue == MailHandler.offValue) {
            return false;
        }
        final Filter body = this.getFilter();
        if (body == null || body.isLoggable(record)) {
            this.setMatchedPart(-1);
            return true;
        }
        return this.isAttachmentLoggable(record);
    }
    
    @Override
    public void publish(final LogRecord record) {
        if (this.tryMutex()) {
            try {
                if (this.isLoggable(record)) {
                    record.getSourceMethodName();
                    this.publish0(record);
                }
            }
            catch (final LinkageError JDK8152515) {
                this.reportLinkageError(JDK8152515, 1);
            }
            finally {
                this.releaseMutex();
            }
        }
        else {
            this.reportUnPublishedError(record);
        }
    }
    
    private void publish0(final LogRecord record) {
        boolean priority;
        Message msg;
        synchronized (this) {
            if (this.size == this.data.length && this.size < this.capacity) {
                this.grow();
            }
            if (this.size < this.data.length) {
                this.matched[this.size] = this.getMatchedPart();
                this.data[this.size] = record;
                ++this.size;
                priority = this.isPushable(record);
                if (priority || this.size >= this.capacity) {
                    msg = this.writeLogRecords(1);
                }
                else {
                    msg = null;
                }
            }
            else {
                priority = false;
                msg = null;
            }
        }
        if (msg != null) {
            this.send(msg, priority, 1);
        }
    }
    
    private void reportUnPublishedError(final LogRecord record) {
        final Integer idx = MailHandler.MUTEX.get();
        if (idx == null || idx > MailHandler.MUTEX_REPORT) {
            MailHandler.MUTEX.set(MailHandler.MUTEX_REPORT);
            try {
                String msg;
                if (record != null) {
                    final Formatter f = createSimpleFormatter();
                    msg = "Log record " + record.getSequenceNumber() + " was not published. " + this.head(f) + this.format(f, record) + this.tail(f, "");
                }
                else {
                    msg = null;
                }
                final Exception e = new IllegalStateException("Recursive publish detected by thread " + Thread.currentThread());
                this.reportError(msg, e, 1);
            }
            finally {
                if (idx != null) {
                    MailHandler.MUTEX.set(idx);
                }
                else {
                    MailHandler.MUTEX.remove();
                }
            }
        }
    }
    
    private boolean tryMutex() {
        if (MailHandler.MUTEX.get() == null) {
            MailHandler.MUTEX.set(MailHandler.MUTEX_PUBLISH);
            return true;
        }
        return false;
    }
    
    private void releaseMutex() {
        MailHandler.MUTEX.remove();
    }
    
    private int getMatchedPart() {
        Integer idx = MailHandler.MUTEX.get();
        if (idx == null || idx >= this.readOnlyAttachmentFilters().length) {
            idx = MailHandler.MUTEX_PUBLISH;
        }
        return idx;
    }
    
    private void setMatchedPart(final int index) {
        if (MailHandler.MUTEX_PUBLISH.equals(MailHandler.MUTEX.get())) {
            MailHandler.MUTEX.set(index);
        }
    }
    
    private void clearMatches(final int index) {
        assert Thread.holdsLock(this);
        for (int r = 0; r < this.size; ++r) {
            if (this.matched[r] >= index) {
                this.matched[r] = MailHandler.MUTEX_PUBLISH;
            }
        }
    }
    
    public void postConstruct() {
    }
    
    public void preDestroy() {
        this.push(false, 3);
    }
    
    public void push() {
        this.push(true, 2);
    }
    
    @Override
    public void flush() {
        this.push(false, 2);
    }
    
    @Override
    public void close() {
        try {
            this.checkAccess();
            Message msg = null;
            synchronized (this) {
                try {
                    msg = this.writeLogRecords(3);
                }
                finally {
                    this.logLevel = Level.OFF;
                    if (this.capacity > 0) {
                        this.capacity = -this.capacity;
                    }
                    if (this.size == 0 && this.data.length != 1) {
                        this.data = new LogRecord[1];
                        this.matched = new int[this.data.length];
                    }
                }
            }
            if (msg != null) {
                this.send(msg, false, 3);
            }
        }
        catch (final LinkageError JDK8152515) {
            this.reportLinkageError(JDK8152515, 3);
        }
    }
    
    @Override
    public void setLevel(final Level newLevel) {
        if (newLevel == null) {
            throw new NullPointerException();
        }
        this.checkAccess();
        synchronized (this) {
            if (this.capacity > 0) {
                this.logLevel = newLevel;
            }
        }
    }
    
    @Override
    public Level getLevel() {
        return this.logLevel;
    }
    
    @Override
    public ErrorManager getErrorManager() {
        this.checkAccess();
        return this.errorManager;
    }
    
    @Override
    public void setErrorManager(final ErrorManager em) {
        this.checkAccess();
        this.setErrorManager0(em);
    }
    
    private void setErrorManager0(final ErrorManager em) {
        if (em == null) {
            throw new NullPointerException();
        }
        try {
            synchronized (this) {
                super.setErrorManager(this.errorManager = em);
            }
        }
        catch (final RuntimeException | LinkageError runtimeException | LinkageError) {}
    }
    
    @Override
    public Filter getFilter() {
        return this.filter;
    }
    
    @Override
    public void setFilter(final Filter newFilter) {
        this.checkAccess();
        synchronized (this) {
            if (newFilter != this.filter) {
                this.clearMatches(-1);
            }
            this.filter = newFilter;
        }
    }
    
    @Override
    public synchronized String getEncoding() {
        return this.encoding;
    }
    
    @Override
    public void setEncoding(final String encoding) throws UnsupportedEncodingException {
        this.checkAccess();
        this.setEncoding0(encoding);
    }
    
    private void setEncoding0(final String e) throws UnsupportedEncodingException {
        if (e != null) {
            try {
                if (!Charset.isSupported(e)) {
                    throw new UnsupportedEncodingException(e);
                }
            }
            catch (final IllegalCharsetNameException icne) {
                throw new UnsupportedEncodingException(e);
            }
        }
        synchronized (this) {
            this.encoding = e;
        }
    }
    
    @Override
    public synchronized Formatter getFormatter() {
        return this.formatter;
    }
    
    @Override
    public synchronized void setFormatter(final Formatter newFormatter) throws SecurityException {
        this.checkAccess();
        if (newFormatter == null) {
            throw new NullPointerException();
        }
        this.formatter = newFormatter;
    }
    
    public final synchronized Level getPushLevel() {
        return this.pushLevel;
    }
    
    public final synchronized void setPushLevel(final Level level) {
        this.checkAccess();
        if (level == null) {
            throw new NullPointerException();
        }
        if (this.isWriting) {
            throw new IllegalStateException();
        }
        this.pushLevel = level;
    }
    
    public final synchronized Filter getPushFilter() {
        return this.pushFilter;
    }
    
    public final synchronized void setPushFilter(final Filter filter) {
        this.checkAccess();
        if (this.isWriting) {
            throw new IllegalStateException();
        }
        this.pushFilter = filter;
    }
    
    public final synchronized Comparator<? super LogRecord> getComparator() {
        return this.comparator;
    }
    
    public final synchronized void setComparator(final Comparator<? super LogRecord> c) {
        this.checkAccess();
        if (this.isWriting) {
            throw new IllegalStateException();
        }
        this.comparator = c;
    }
    
    public final synchronized int getCapacity() {
        assert this.capacity != Integer.MIN_VALUE && this.capacity != 0 : this.capacity;
        return Math.abs(this.capacity);
    }
    
    public final synchronized Authenticator getAuthenticator() {
        this.checkAccess();
        return this.auth;
    }
    
    public final void setAuthenticator(final Authenticator auth) {
        this.setAuthenticator0(auth);
    }
    
    public final void setAuthenticator(final char... password) {
        if (password == null) {
            this.setAuthenticator0(null);
        }
        else {
            this.setAuthenticator0(DefaultAuthenticator.of(new String(password)));
        }
    }
    
    private void setAuthenticator0(final Authenticator auth) {
        this.checkAccess();
        final Session settings;
        synchronized (this) {
            if (this.isWriting) {
                throw new IllegalStateException();
            }
            this.auth = auth;
            settings = this.updateSession();
        }
        this.verifySettings(settings);
    }
    
    public final void setMailProperties(final Properties props) {
        this.setMailProperties0(props);
    }
    
    private void setMailProperties0(Properties props) {
        this.checkAccess();
        props = (Properties)props.clone();
        final Session settings;
        synchronized (this) {
            if (this.isWriting) {
                throw new IllegalStateException();
            }
            this.mailProps = props;
            settings = this.updateSession();
        }
        this.verifySettings(settings);
    }
    
    public final Properties getMailProperties() {
        this.checkAccess();
        final Properties props;
        synchronized (this) {
            props = this.mailProps;
        }
        return (Properties)props.clone();
    }
    
    public final Filter[] getAttachmentFilters() {
        return this.readOnlyAttachmentFilters().clone();
    }
    
    public final void setAttachmentFilters(Filter... filters) {
        this.checkAccess();
        if (filters.length == 0) {
            filters = emptyFilterArray();
        }
        else {
            filters = Arrays.copyOf(filters, filters.length, (Class<? extends Filter[]>)Filter[].class);
        }
        synchronized (this) {
            if (this.attachmentFormatters.length != filters.length) {
                throw attachmentMismatch(this.attachmentFormatters.length, filters.length);
            }
            if (this.isWriting) {
                throw new IllegalStateException();
            }
            if (this.size != 0) {
                for (int i = 0; i < filters.length; ++i) {
                    if (filters[i] != this.attachmentFilters[i]) {
                        this.clearMatches(i);
                        break;
                    }
                }
            }
            this.attachmentFilters = filters;
        }
    }
    
    public final Formatter[] getAttachmentFormatters() {
        final Formatter[] formatters;
        synchronized (this) {
            formatters = this.attachmentFormatters;
        }
        return formatters.clone();
    }
    
    public final void setAttachmentFormatters(Formatter... formatters) {
        this.checkAccess();
        if (formatters.length == 0) {
            formatters = emptyFormatterArray();
        }
        else {
            formatters = Arrays.copyOf(formatters, formatters.length, (Class<? extends Formatter[]>)Formatter[].class);
            for (int i = 0; i < formatters.length; ++i) {
                if (formatters[i] == null) {
                    throw new NullPointerException(atIndexMsg(i));
                }
            }
        }
        synchronized (this) {
            if (this.isWriting) {
                throw new IllegalStateException();
            }
            this.attachmentFormatters = formatters;
            this.alignAttachmentFilters();
            this.alignAttachmentNames();
        }
    }
    
    public final Formatter[] getAttachmentNames() {
        final Formatter[] formatters;
        synchronized (this) {
            formatters = this.attachmentNames;
        }
        return formatters.clone();
    }
    
    public final void setAttachmentNames(final String... names) {
        this.checkAccess();
        Formatter[] formatters;
        if (names.length == 0) {
            formatters = emptyFormatterArray();
        }
        else {
            formatters = new Formatter[names.length];
        }
        for (int i = 0; i < names.length; ++i) {
            final String name = names[i];
            if (name == null) {
                throw new NullPointerException(atIndexMsg(i));
            }
            if (name.length() <= 0) {
                throw new IllegalArgumentException(atIndexMsg(i));
            }
            formatters[i] = TailNameFormatter.of(name);
        }
        synchronized (this) {
            if (this.attachmentFormatters.length != names.length) {
                throw attachmentMismatch(this.attachmentFormatters.length, names.length);
            }
            if (this.isWriting) {
                throw new IllegalStateException();
            }
            this.attachmentNames = formatters;
        }
    }
    
    public final void setAttachmentNames(Formatter... formatters) {
        this.checkAccess();
        if (formatters.length == 0) {
            formatters = emptyFormatterArray();
        }
        else {
            formatters = Arrays.copyOf(formatters, formatters.length, (Class<? extends Formatter[]>)Formatter[].class);
        }
        for (int i = 0; i < formatters.length; ++i) {
            if (formatters[i] == null) {
                throw new NullPointerException(atIndexMsg(i));
            }
        }
        synchronized (this) {
            if (this.attachmentFormatters.length != formatters.length) {
                throw attachmentMismatch(this.attachmentFormatters.length, formatters.length);
            }
            if (this.isWriting) {
                throw new IllegalStateException();
            }
            this.attachmentNames = formatters;
        }
    }
    
    public final synchronized Formatter getSubject() {
        return this.subjectFormatter;
    }
    
    public final void setSubject(final String subject) {
        if (subject != null) {
            this.setSubject(TailNameFormatter.of(subject));
            return;
        }
        this.checkAccess();
        throw new NullPointerException();
    }
    
    public final void setSubject(final Formatter format) {
        this.checkAccess();
        if (format == null) {
            throw new NullPointerException();
        }
        synchronized (this) {
            if (this.isWriting) {
                throw new IllegalStateException();
            }
            this.subjectFormatter = format;
        }
    }
    
    @Override
    protected void reportError(final String msg, final Exception ex, final int code) {
        try {
            if (msg != null) {
                this.errorManager.error(Level.SEVERE.getName().concat(": ").concat(msg), ex, code);
            }
            else {
                this.errorManager.error(null, ex, code);
            }
        }
        catch (final RuntimeException | LinkageError GLASSFISH_21258) {
            this.reportLinkageError(GLASSFISH_21258, code);
        }
    }
    
    private void checkAccess() {
        if (this.sealed) {
            LogManagerProperties.checkLogManagerAccess();
        }
    }
    
    final String contentTypeOf(CharSequence chunk) {
        if (!isEmpty(chunk)) {
            final int MAX_CHARS = 25;
            if (chunk.length() > 25) {
                chunk = chunk.subSequence(0, 25);
            }
            try {
                final String charset = this.getEncodingName();
                final byte[] b = chunk.toString().getBytes(charset);
                final ByteArrayInputStream in = new ByteArrayInputStream(b);
                assert in.markSupported() : in.getClass().getName();
                return URLConnection.guessContentTypeFromStream(in);
            }
            catch (final IOException IOE) {
                this.reportError(IOE.getMessage(), IOE, 5);
            }
        }
        return null;
    }
    
    final String contentTypeOf(final Formatter f) {
        assert Thread.holdsLock(this);
        if (f != null) {
            final String type = this.getContentType(f.getClass().getName());
            if (type != null) {
                return type;
            }
            for (Class<?> k = f.getClass(); k != Formatter.class; k = k.getSuperclass()) {
                String name;
                try {
                    name = k.getSimpleName();
                }
                catch (final InternalError JDK8057919) {
                    name = k.getName();
                }
                name = name.toLowerCase(Locale.ENGLISH);
                for (int idx = name.indexOf(36) + 1; (idx = name.indexOf("ml", idx)) > -1; idx += 2) {
                    if (idx > 0) {
                        if (name.charAt(idx - 1) == 'x') {
                            return "application/xml";
                        }
                        if (idx > 1 && name.charAt(idx - 2) == 'h' && name.charAt(idx - 1) == 't') {
                            return "text/html";
                        }
                    }
                }
            }
        }
        return null;
    }
    
    final boolean isMissingContent(final Message msg, Throwable t) {
        final Object ccl = this.getAndSetContextClassLoader(MailHandler.MAILHANDLER_LOADER);
        try {
            msg.writeTo(new ByteArrayOutputStream(1024));
        }
        catch (final RuntimeException RE) {
            throw RE;
        }
        catch (final Exception noContent) {
            final String txt = noContent.getMessage();
            if (!isEmpty(txt)) {
                int limit = 0;
                while (t != null) {
                    if (noContent.getClass() == t.getClass() && txt.equals(t.getMessage())) {
                        return true;
                    }
                    final Throwable cause = t.getCause();
                    if (cause == null && t instanceof MessagingException) {
                        t = ((MessagingException)t).getNextException();
                    }
                    else {
                        t = cause;
                    }
                    if (++limit == 65536) {
                        break;
                    }
                }
            }
        }
        finally {
            this.getAndSetContextClassLoader(ccl);
        }
        return false;
    }
    
    private void reportError(final Message msg, final Exception ex, final int code) {
        try {
            try {
                this.errorManager.error(this.toRawString(msg), ex, code);
            }
            catch (final RuntimeException re) {
                this.reportError(this.toMsgString(re), ex, code);
            }
            catch (final Exception e) {
                this.reportError(this.toMsgString(e), ex, code);
            }
        }
        catch (final LinkageError GLASSFISH_21258) {
            this.reportLinkageError(GLASSFISH_21258, code);
        }
    }
    
    private void reportLinkageError(final Throwable le, final int code) {
        if (le == null) {
            throw new NullPointerException(String.valueOf(code));
        }
        final Integer idx = MailHandler.MUTEX.get();
        if (idx == null || idx > MailHandler.MUTEX_LINKAGE) {
            MailHandler.MUTEX.set(MailHandler.MUTEX_LINKAGE);
            try {
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), le);
            }
            catch (final RuntimeException | LinkageError runtimeException | LinkageError) {}
            finally {
                if (idx != null) {
                    MailHandler.MUTEX.set(idx);
                }
                else {
                    MailHandler.MUTEX.remove();
                }
            }
        }
    }
    
    private String getContentType(final String name) {
        assert Thread.holdsLock(this);
        final String type = this.contentTypes.getContentType(name);
        if ("application/octet-stream".equalsIgnoreCase(type)) {
            return null;
        }
        return type;
    }
    
    private String getEncodingName() {
        String charset = this.getEncoding();
        if (charset == null) {
            charset = MimeUtility.getDefaultJavaCharset();
        }
        return charset;
    }
    
    private void setContent(final MimePart part, final CharSequence buf, String type) throws MessagingException {
        final String charset = this.getEncodingName();
        if (type != null && !"text/plain".equalsIgnoreCase(type)) {
            type = this.contentWithEncoding(type, charset);
            try {
                final DataSource source = new ByteArrayDataSource(buf.toString(), type);
                part.setDataHandler(new DataHandler(source));
            }
            catch (final IOException IOE) {
                this.reportError(IOE.getMessage(), IOE, 5);
                part.setText(buf.toString(), charset);
            }
        }
        else {
            part.setText(buf.toString(), MimeUtility.mimeCharset(charset));
        }
    }
    
    private String contentWithEncoding(String type, String encoding) {
        assert encoding != null;
        try {
            final ContentType ct = new ContentType(type);
            ct.setParameter("charset", MimeUtility.mimeCharset(encoding));
            encoding = ct.toString();
            if (!isEmpty(encoding)) {
                type = encoding;
            }
        }
        catch (final MessagingException ME) {
            this.reportError(type, ME, 5);
        }
        return type;
    }
    
    private synchronized void setCapacity0(final int newCapacity) {
        this.checkAccess();
        if (newCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
        if (this.isWriting) {
            throw new IllegalStateException();
        }
        if (this.capacity < 0) {
            this.capacity = -newCapacity;
        }
        else {
            this.capacity = newCapacity;
        }
    }
    
    private Filter[] readOnlyAttachmentFilters() {
        return this.attachmentFilters;
    }
    
    private static Formatter[] emptyFormatterArray() {
        return MailHandler.EMPTY_FORMATTERS;
    }
    
    private static Filter[] emptyFilterArray() {
        return MailHandler.EMPTY_FILTERS;
    }
    
    private boolean alignAttachmentNames() {
        assert Thread.holdsLock(this);
        boolean fixed = false;
        final int expect = this.attachmentFormatters.length;
        final int current = this.attachmentNames.length;
        if (current != expect) {
            this.attachmentNames = Arrays.copyOf(this.attachmentNames, expect, (Class<? extends Formatter[]>)Formatter[].class);
            fixed = (current != 0);
        }
        if (expect == 0) {
            this.attachmentNames = emptyFormatterArray();
            assert this.attachmentNames.length == 0;
        }
        else {
            for (int i = 0; i < expect; ++i) {
                if (this.attachmentNames[i] == null) {
                    this.attachmentNames[i] = TailNameFormatter.of(this.toString(this.attachmentFormatters[i]));
                }
            }
        }
        return fixed;
    }
    
    private boolean alignAttachmentFilters() {
        assert Thread.holdsLock(this);
        boolean fixed = false;
        final int expect = this.attachmentFormatters.length;
        final int current = this.attachmentFilters.length;
        if (current != expect) {
            this.attachmentFilters = Arrays.copyOf(this.attachmentFilters, expect, (Class<? extends Filter[]>)Filter[].class);
            this.clearMatches(current);
            fixed = (current != 0);
            final Filter body = this.filter;
            if (body != null) {
                for (int i = current; i < expect; ++i) {
                    this.attachmentFilters[i] = body;
                }
            }
        }
        if (expect == 0) {
            this.attachmentFilters = emptyFilterArray();
            assert this.attachmentFilters.length == 0;
        }
        return fixed;
    }
    
    private void reset() {
        assert Thread.holdsLock(this);
        if (this.size < this.data.length) {
            Arrays.fill(this.data, 0, this.size, null);
        }
        else {
            Arrays.fill(this.data, null);
        }
        this.size = 0;
    }
    
    private void grow() {
        assert Thread.holdsLock(this);
        final int len = this.data.length;
        int newCapacity = len + (len >> 1) + 1;
        if (newCapacity > this.capacity || newCapacity < len) {
            newCapacity = this.capacity;
        }
        assert len != this.capacity : len;
        this.data = Arrays.copyOf(this.data, newCapacity, (Class<? extends LogRecord[]>)LogRecord[].class);
        this.matched = Arrays.copyOf(this.matched, newCapacity);
    }
    
    private synchronized void init(final Properties props) {
        assert this.errorManager != null;
        final String p = this.getClass().getName();
        this.mailProps = new Properties();
        final Object ccl = this.getAndSetContextClassLoader(MailHandler.MAILHANDLER_LOADER);
        try {
            this.contentTypes = FileTypeMap.getDefaultFileTypeMap();
        }
        finally {
            this.getAndSetContextClassLoader(ccl);
        }
        this.initErrorManager(p);
        this.initLevel(p);
        this.initFilter(p);
        this.initCapacity(p);
        this.initAuthenticator(p);
        this.initEncoding(p);
        this.initFormatter(p);
        this.initComparator(p);
        this.initPushLevel(p);
        this.initPushFilter(p);
        this.initSubject(p);
        this.initAttachmentFormaters(p);
        this.initAttachmentFilters(p);
        this.initAttachmentNames(p);
        if (props == null && LogManagerProperties.fromLogManager(p.concat(".verify")) != null) {
            this.verifySettings(this.initSession());
        }
        this.intern();
    }
    
    private void intern() {
        assert Thread.holdsLock(this);
        try {
            final Map<Object, Object> seen = new HashMap<Object, Object>();
            try {
                this.intern(seen, this.errorManager);
            }
            catch (final SecurityException se) {
                this.reportError(se.getMessage(), se, 4);
            }
            try {
                Object canidate = this.filter;
                Object result = this.intern(seen, canidate);
                if (result != canidate && result instanceof Filter) {
                    this.filter = (Filter)result;
                }
                canidate = this.formatter;
                result = this.intern(seen, canidate);
                if (result != canidate && result instanceof Formatter) {
                    this.formatter = (Formatter)result;
                }
            }
            catch (final SecurityException se) {
                this.reportError(se.getMessage(), se, 4);
            }
            Object canidate = this.subjectFormatter;
            Object result = this.intern(seen, canidate);
            if (result != canidate && result instanceof Formatter) {
                this.subjectFormatter = (Formatter)result;
            }
            canidate = this.pushFilter;
            result = this.intern(seen, canidate);
            if (result != canidate && result instanceof Filter) {
                this.pushFilter = (Filter)result;
            }
            for (int i = 0; i < this.attachmentFormatters.length; ++i) {
                canidate = this.attachmentFormatters[i];
                result = this.intern(seen, canidate);
                if (result != canidate && result instanceof Formatter) {
                    this.attachmentFormatters[i] = (Formatter)result;
                }
                canidate = this.attachmentFilters[i];
                result = this.intern(seen, canidate);
                if (result != canidate && result instanceof Filter) {
                    this.attachmentFilters[i] = (Filter)result;
                }
                canidate = this.attachmentNames[i];
                result = this.intern(seen, canidate);
                if (result != canidate && result instanceof Formatter) {
                    this.attachmentNames[i] = (Formatter)result;
                }
            }
        }
        catch (final Exception skip) {
            this.reportError(skip.getMessage(), skip, 4);
        }
        catch (final LinkageError skip2) {
            this.reportError(skip2.getMessage(), new InvocationTargetException(skip2), 4);
        }
    }
    
    private Object intern(final Map<Object, Object> m, final Object o) throws Exception {
        if (o == null) {
            return null;
        }
        Object key;
        if (o.getClass().getName().equals(TailNameFormatter.class.getName())) {
            key = o;
        }
        else {
            key = o.getClass().getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        Object use;
        if (key.getClass() == o.getClass()) {
            Object found = m.get(key);
            if (found == null) {
                final boolean right = key.equals(o);
                final boolean left = o.equals(key);
                if (right && left) {
                    found = m.put(o, o);
                    if (found != null) {
                        this.reportNonDiscriminating(key, found);
                        found = m.remove(key);
                        if (found != o) {
                            this.reportNonDiscriminating(key, found);
                            m.clear();
                        }
                    }
                }
                else if (right != left) {
                    this.reportNonSymmetric(o, key);
                }
                use = o;
            }
            else if (o.getClass() == found.getClass()) {
                use = found;
            }
            else {
                this.reportNonDiscriminating(o, found);
                use = o;
            }
        }
        else {
            use = o;
        }
        return use;
    }
    
    private static Formatter createSimpleFormatter() {
        return Formatter.class.cast(new SimpleFormatter());
    }
    
    private static boolean isEmpty(final CharSequence s) {
        return s == null || s.length() == 0;
    }
    
    private static boolean hasValue(final String name) {
        return !isEmpty(name) && !"null".equalsIgnoreCase(name);
    }
    
    private void initAttachmentFilters(final String p) {
        assert Thread.holdsLock(this);
        assert this.attachmentFormatters != null;
        final String list = LogManagerProperties.fromLogManager(p.concat(".attachment.filters"));
        if (!isEmpty(list)) {
            final String[] names = list.split(",");
            final Filter[] a = new Filter[names.length];
            for (int i = 0; i < a.length; ++i) {
                names[i] = names[i].trim();
                if (!"null".equalsIgnoreCase(names[i])) {
                    try {
                        a[i] = LogManagerProperties.newFilter(names[i]);
                    }
                    catch (final SecurityException SE) {
                        throw SE;
                    }
                    catch (final Exception E) {
                        this.reportError(E.getMessage(), E, 4);
                    }
                }
            }
            this.attachmentFilters = a;
            if (this.alignAttachmentFilters()) {
                this.reportError("Attachment filters.", attachmentMismatch("Length mismatch."), 4);
            }
        }
        else {
            this.attachmentFilters = emptyFilterArray();
            this.alignAttachmentFilters();
        }
    }
    
    private void initAttachmentFormaters(final String p) {
        assert Thread.holdsLock(this);
        final String list = LogManagerProperties.fromLogManager(p.concat(".attachment.formatters"));
        if (!isEmpty(list)) {
            final String[] names = list.split(",");
            Formatter[] a;
            if (names.length == 0) {
                a = emptyFormatterArray();
            }
            else {
                a = new Formatter[names.length];
            }
            for (int i = 0; i < a.length; ++i) {
                names[i] = names[i].trim();
                if (!"null".equalsIgnoreCase(names[i])) {
                    try {
                        a[i] = LogManagerProperties.newFormatter(names[i]);
                        if (a[i] instanceof TailNameFormatter) {
                            final Exception CNFE = new ClassNotFoundException(a[i].toString());
                            this.reportError("Attachment formatter.", CNFE, 4);
                            a[i] = createSimpleFormatter();
                        }
                        continue;
                    }
                    catch (final SecurityException SE) {
                        throw SE;
                    }
                    catch (final Exception E) {
                        this.reportError(E.getMessage(), E, 4);
                        a[i] = createSimpleFormatter();
                        continue;
                    }
                }
                final Exception NPE = new NullPointerException(atIndexMsg(i));
                this.reportError("Attachment formatter.", NPE, 4);
                a[i] = createSimpleFormatter();
            }
            this.attachmentFormatters = a;
        }
        else {
            this.attachmentFormatters = emptyFormatterArray();
        }
    }
    
    private void initAttachmentNames(final String p) {
        assert Thread.holdsLock(this);
        assert this.attachmentFormatters != null;
        final String list = LogManagerProperties.fromLogManager(p.concat(".attachment.names"));
        if (!isEmpty(list)) {
            final String[] names = list.split(",");
            final Formatter[] a = new Formatter[names.length];
            for (int i = 0; i < a.length; ++i) {
                names[i] = names[i].trim();
                if (!"null".equalsIgnoreCase(names[i])) {
                    try {
                        try {
                            a[i] = LogManagerProperties.newFormatter(names[i]);
                        }
                        catch (final ClassNotFoundException | ClassCastException literal) {
                            a[i] = TailNameFormatter.of(names[i]);
                        }
                        continue;
                    }
                    catch (final SecurityException SE) {
                        throw SE;
                    }
                    catch (final Exception E) {
                        this.reportError(E.getMessage(), E, 4);
                        continue;
                    }
                }
                final Exception NPE = new NullPointerException(atIndexMsg(i));
                this.reportError("Attachment names.", NPE, 4);
            }
            this.attachmentNames = a;
            if (this.alignAttachmentNames()) {
                this.reportError("Attachment names.", attachmentMismatch("Length mismatch."), 4);
            }
        }
        else {
            this.attachmentNames = emptyFormatterArray();
            this.alignAttachmentNames();
        }
    }
    
    private void initAuthenticator(final String p) {
        assert Thread.holdsLock(this);
        final String name = LogManagerProperties.fromLogManager(p.concat(".authenticator"));
        if (name != null && !"null".equalsIgnoreCase(name)) {
            if (name.length() != 0) {
                try {
                    this.auth = LogManagerProperties.newObjectFrom(name, Authenticator.class);
                    return;
                }
                catch (final SecurityException SE) {
                    throw SE;
                }
                catch (final ClassNotFoundException | ClassCastException literalAuth) {
                    this.auth = DefaultAuthenticator.of(name);
                    return;
                }
                catch (final Exception E) {
                    this.reportError(E.getMessage(), E, 4);
                    return;
                }
            }
            this.auth = DefaultAuthenticator.of(name);
        }
    }
    
    private void initLevel(final String p) {
        assert Thread.holdsLock(this);
        try {
            final String val = LogManagerProperties.fromLogManager(p.concat(".level"));
            if (val != null) {
                this.logLevel = Level.parse(val);
            }
            else {
                this.logLevel = Level.WARNING;
            }
        }
        catch (final SecurityException SE) {
            throw SE;
        }
        catch (final RuntimeException RE) {
            this.reportError(RE.getMessage(), RE, 4);
            this.logLevel = Level.WARNING;
        }
    }
    
    private void initFilter(final String p) {
        assert Thread.holdsLock(this);
        try {
            final String name = LogManagerProperties.fromLogManager(p.concat(".filter"));
            if (hasValue(name)) {
                this.filter = LogManagerProperties.newFilter(name);
            }
        }
        catch (final SecurityException SE) {
            throw SE;
        }
        catch (final Exception E) {
            this.reportError(E.getMessage(), E, 4);
        }
    }
    
    private void initCapacity(final String p) {
        assert Thread.holdsLock(this);
        final int DEFAULT_CAPACITY = 1000;
        try {
            final String value = LogManagerProperties.fromLogManager(p.concat(".capacity"));
            if (value != null) {
                this.setCapacity0(Integer.parseInt(value));
            }
            else {
                this.setCapacity0(1000);
            }
        }
        catch (final SecurityException SE) {
            throw SE;
        }
        catch (final RuntimeException RE) {
            this.reportError(RE.getMessage(), RE, 4);
        }
        if (this.capacity <= 0) {
            this.capacity = 1000;
        }
        this.data = new LogRecord[1];
        this.matched = new int[this.data.length];
    }
    
    private void initEncoding(final String p) {
        assert Thread.holdsLock(this);
        try {
            final String e = LogManagerProperties.fromLogManager(p.concat(".encoding"));
            if (e != null) {
                this.setEncoding0(e);
            }
        }
        catch (final SecurityException SE) {
            throw SE;
        }
        catch (final UnsupportedEncodingException | RuntimeException UEE) {
            this.reportError(UEE.getMessage(), UEE, 4);
        }
    }
    
    private ErrorManager defaultErrorManager() {
        ErrorManager em;
        try {
            em = super.getErrorManager();
        }
        catch (final RuntimeException | LinkageError ignore) {
            em = null;
        }
        if (em == null) {
            em = new ErrorManager();
        }
        return em;
    }
    
    private void initErrorManager(final String p) {
        assert Thread.holdsLock(this);
        try {
            final String name = LogManagerProperties.fromLogManager(p.concat(".errorManager"));
            if (name != null) {
                this.setErrorManager0(LogManagerProperties.newErrorManager(name));
            }
        }
        catch (final SecurityException SE) {
            throw SE;
        }
        catch (final Exception E) {
            this.reportError(E.getMessage(), E, 4);
        }
    }
    
    private void initFormatter(final String p) {
        assert Thread.holdsLock(this);
        try {
            final String name = LogManagerProperties.fromLogManager(p.concat(".formatter"));
            if (hasValue(name)) {
                final Formatter f = LogManagerProperties.newFormatter(name);
                assert f != null;
                if (!(f instanceof TailNameFormatter)) {
                    this.formatter = f;
                }
                else {
                    this.formatter = createSimpleFormatter();
                }
            }
            else {
                this.formatter = createSimpleFormatter();
            }
        }
        catch (final SecurityException SE) {
            throw SE;
        }
        catch (final Exception E) {
            this.reportError(E.getMessage(), E, 4);
            this.formatter = createSimpleFormatter();
        }
    }
    
    private void initComparator(final String p) {
        assert Thread.holdsLock(this);
        try {
            final String name = LogManagerProperties.fromLogManager(p.concat(".comparator"));
            final String reverse = LogManagerProperties.fromLogManager(p.concat(".comparator.reverse"));
            if (hasValue(name)) {
                this.comparator = LogManagerProperties.newComparator(name);
                if (Boolean.parseBoolean(reverse)) {
                    assert this.comparator != null : "null";
                    this.comparator = LogManagerProperties.reverseOrder(this.comparator);
                }
            }
            else if (!isEmpty(reverse)) {
                throw new IllegalArgumentException("No comparator to reverse.");
            }
        }
        catch (final SecurityException SE) {
            throw SE;
        }
        catch (final Exception E) {
            this.reportError(E.getMessage(), E, 4);
        }
    }
    
    private void initPushLevel(final String p) {
        assert Thread.holdsLock(this);
        try {
            final String val = LogManagerProperties.fromLogManager(p.concat(".pushLevel"));
            if (val != null) {
                this.pushLevel = Level.parse(val);
            }
        }
        catch (final RuntimeException RE) {
            this.reportError(RE.getMessage(), RE, 4);
        }
        if (this.pushLevel == null) {
            this.pushLevel = Level.OFF;
        }
    }
    
    private void initPushFilter(final String p) {
        assert Thread.holdsLock(this);
        try {
            final String name = LogManagerProperties.fromLogManager(p.concat(".pushFilter"));
            if (hasValue(name)) {
                this.pushFilter = LogManagerProperties.newFilter(name);
            }
        }
        catch (final SecurityException SE) {
            throw SE;
        }
        catch (final Exception E) {
            this.reportError(E.getMessage(), E, 4);
        }
    }
    
    private void initSubject(final String p) {
        assert Thread.holdsLock(this);
        String name = LogManagerProperties.fromLogManager(p.concat(".subject"));
        if (name == null) {
            name = "com.sun.mail.util.logging.CollectorFormatter";
        }
        if (hasValue(name)) {
            try {
                this.subjectFormatter = LogManagerProperties.newFormatter(name);
                return;
            }
            catch (final SecurityException SE) {
                throw SE;
            }
            catch (final ClassNotFoundException | ClassCastException literalSubject) {
                this.subjectFormatter = TailNameFormatter.of(name);
                return;
            }
            catch (final Exception E) {
                this.subjectFormatter = TailNameFormatter.of(name);
                this.reportError(E.getMessage(), E, 4);
                return;
            }
        }
        this.subjectFormatter = TailNameFormatter.of(name);
    }
    
    private boolean isAttachmentLoggable(final LogRecord record) {
        final Filter[] filters = this.readOnlyAttachmentFilters();
        for (int i = 0; i < filters.length; ++i) {
            final Filter f = filters[i];
            if (f == null || f.isLoggable(record)) {
                this.setMatchedPart(i);
                return true;
            }
        }
        return false;
    }
    
    private boolean isPushable(final LogRecord record) {
        assert Thread.holdsLock(this);
        final int value = this.getPushLevel().intValue();
        if (value == MailHandler.offValue || record.getLevel().intValue() < value) {
            return false;
        }
        final Filter push = this.getPushFilter();
        if (push == null) {
            return true;
        }
        final int match = this.getMatchedPart();
        return (match == -1 && this.getFilter() == push) || (match >= 0 && this.attachmentFilters[match] == push) || push.isLoggable(record);
    }
    
    private void push(final boolean priority, final int code) {
        if (this.tryMutex()) {
            try {
                final Message msg = this.writeLogRecords(code);
                if (msg != null) {
                    this.send(msg, priority, code);
                }
            }
            catch (final LinkageError JDK8152515) {
                this.reportLinkageError(JDK8152515, code);
            }
            finally {
                this.releaseMutex();
            }
        }
        else {
            this.reportUnPublishedError(null);
        }
    }
    
    private void send(final Message msg, final boolean priority, final int code) {
        try {
            this.envelopeFor(msg, priority);
            final Object ccl = this.getAndSetContextClassLoader(MailHandler.MAILHANDLER_LOADER);
            try {
                Transport.send(msg);
            }
            finally {
                this.getAndSetContextClassLoader(ccl);
            }
        }
        catch (final RuntimeException re) {
            this.reportError(msg, re, code);
        }
        catch (final Exception e) {
            this.reportError(msg, e, code);
        }
    }
    
    private void sort() {
        assert Thread.holdsLock(this);
        if (this.comparator != null) {
            try {
                if (this.size != 1) {
                    Arrays.sort(this.data, 0, this.size, this.comparator);
                }
                else if (this.comparator.compare(this.data[0], this.data[0]) != 0) {
                    throw new IllegalArgumentException(this.comparator.getClass().getName());
                }
            }
            catch (final RuntimeException RE) {
                this.reportError(RE.getMessage(), RE, 5);
            }
        }
    }
    
    private Message writeLogRecords(final int code) {
        try {
            synchronized (this) {
                if (this.size > 0 && !this.isWriting) {
                    this.isWriting = true;
                    try {
                        return this.writeLogRecords0();
                    }
                    finally {
                        this.isWriting = false;
                        if (this.size > 0) {
                            this.reset();
                        }
                    }
                }
            }
        }
        catch (final RuntimeException re) {
            this.reportError(re.getMessage(), re, code);
        }
        catch (final Exception e) {
            this.reportError(e.getMessage(), e, code);
        }
        return null;
    }
    
    private Message writeLogRecords0() throws Exception {
        assert Thread.holdsLock(this);
        this.sort();
        if (this.session == null) {
            this.initSession();
        }
        final MimeMessage msg = new MimeMessage(this.session);
        final MimeBodyPart[] parts = new MimeBodyPart[this.attachmentFormatters.length];
        final StringBuilder[] buffers = new StringBuilder[parts.length];
        StringBuilder buf = null;
        MimePart body;
        if (parts.length == 0) {
            msg.setDescription(this.descriptionFrom(this.getFormatter(), this.getFilter(), this.subjectFormatter));
            body = msg;
        }
        else {
            msg.setDescription(this.descriptionFrom(this.comparator, this.pushLevel, this.pushFilter));
            body = this.createBodyPart();
        }
        this.appendSubject(msg, this.head(this.subjectFormatter));
        final Formatter bodyFormat = this.getFormatter();
        final Filter bodyFilter = this.getFilter();
        Locale lastLocale = null;
        for (int ix = 0; ix < this.size; ++ix) {
            boolean formatted = false;
            final int match = this.matched[ix];
            final LogRecord r = this.data[ix];
            this.data[ix] = null;
            final Locale locale = this.localeFor(r);
            this.appendSubject(msg, this.format(this.subjectFormatter, r));
            Filter lmf = null;
            if (bodyFilter == null || match == -1 || parts.length == 0 || (match < -1 && bodyFilter.isLoggable(r))) {
                lmf = bodyFilter;
                if (buf == null) {
                    buf = new StringBuilder();
                    buf.append(this.head(bodyFormat));
                }
                formatted = true;
                buf.append(this.format(bodyFormat, r));
                if (locale != null && !locale.equals(lastLocale)) {
                    this.appendContentLang(body, locale);
                }
            }
            for (int i = 0; i < parts.length; ++i) {
                final Filter af = this.attachmentFilters[i];
                if (af == null || lmf == af || match == i || (match < i && af.isLoggable(r))) {
                    if (lmf == null && af != null) {
                        lmf = af;
                    }
                    if (parts[i] == null) {
                        parts[i] = this.createBodyPart(i);
                        (buffers[i] = new StringBuilder()).append(this.head(this.attachmentFormatters[i]));
                        this.appendFileName(parts[i], this.head(this.attachmentNames[i]));
                    }
                    formatted = true;
                    this.appendFileName(parts[i], this.format(this.attachmentNames[i], r));
                    buffers[i].append(this.format(this.attachmentFormatters[i], r));
                    if (locale != null && !locale.equals(lastLocale)) {
                        this.appendContentLang(parts[i], locale);
                    }
                }
            }
            if (formatted) {
                if (body != msg && locale != null && !locale.equals(lastLocale)) {
                    this.appendContentLang(msg, locale);
                }
            }
            else {
                this.reportFilterError(r);
            }
            lastLocale = locale;
        }
        this.size = 0;
        for (int j = parts.length - 1; j >= 0; --j) {
            if (parts[j] != null) {
                this.appendFileName(parts[j], this.tail(this.attachmentNames[j], "err"));
                buffers[j].append(this.tail(this.attachmentFormatters[j], ""));
                if (buffers[j].length() > 0) {
                    String name = parts[j].getFileName();
                    if (isEmpty(name)) {
                        name = this.toString(this.attachmentFormatters[j]);
                        parts[j].setFileName(name);
                    }
                    this.setContent(parts[j], buffers[j], this.getContentType(name));
                }
                else {
                    this.setIncompleteCopy(msg);
                    parts[j] = null;
                }
                buffers[j] = null;
            }
        }
        if (buf != null) {
            buf.append(this.tail(bodyFormat, ""));
        }
        else {
            buf = new StringBuilder(0);
        }
        this.appendSubject(msg, this.tail(this.subjectFormatter, ""));
        final String contentType = this.contentTypeOf(buf);
        final String altType = this.contentTypeOf(bodyFormat);
        this.setContent(body, buf, (altType == null) ? contentType : altType);
        if (body != msg) {
            final MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart((BodyPart)body);
            for (int k = 0; k < parts.length; ++k) {
                if (parts[k] != null) {
                    multipart.addBodyPart(parts[k]);
                }
            }
            msg.setContent(multipart);
        }
        return msg;
    }
    
    private void verifySettings(final Session session) {
        try {
            if (session != null) {
                final Properties props = session.getProperties();
                final Object check = ((Hashtable<String, String>)props).put("verify", "");
                if (check instanceof String) {
                    final String value = (String)check;
                    if (hasValue(value)) {
                        this.verifySettings0(session, value);
                    }
                }
                else if (check != null) {
                    this.verifySettings0(session, check.getClass().toString());
                }
            }
        }
        catch (final LinkageError JDK8152515) {
            this.reportLinkageError(JDK8152515, 4);
        }
    }
    
    private void verifySettings0(final Session session, final String verify) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: ifne            22
        //     6: aload_2         /* verify */
        //     7: ifnonnull       22
        //    10: new             Ljava/lang/AssertionError;
        //    13: dup            
        //    14: aconst_null    
        //    15: checkcast       Ljava/lang/String;
        //    18: invokespecial   java/lang/AssertionError.<init>:(Ljava/lang/Object;)V
        //    21: athrow         
        //    22: ldc_w           "local"
        //    25: aload_2         /* verify */
        //    26: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    29: ifne            89
        //    32: ldc_w           "remote"
        //    35: aload_2         /* verify */
        //    36: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    39: ifne            89
        //    42: ldc_w           "limited"
        //    45: aload_2         /* verify */
        //    46: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    49: ifne            89
        //    52: ldc_w           "resolve"
        //    55: aload_2         /* verify */
        //    56: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    59: ifne            89
        //    62: ldc_w           "login"
        //    65: aload_2         /* verify */
        //    66: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    69: ifne            89
        //    72: aload_0         /* this */
        //    73: ldc_w           "Verify must be 'limited', local', 'resolve', 'login', or 'remote'."
        //    76: new             Ljava/lang/IllegalArgumentException;
        //    79: dup            
        //    80: aload_2         /* verify */
        //    81: invokespecial   java/lang/IllegalArgumentException.<init>:(Ljava/lang/String;)V
        //    84: iconst_4       
        //    85: invokevirtual   com/sun/mail/util/logging/MailHandler.reportError:(Ljava/lang/String;Ljava/lang/Exception;I)V
        //    88: return         
        //    89: new             Ljavax/mail/internet/MimeMessage;
        //    92: dup            
        //    93: aload_1         /* session */
        //    94: invokespecial   javax/mail/internet/MimeMessage.<init>:(Ljavax/mail/Session;)V
        //    97: astore_3        /* abort */
        //    98: ldc_w           "limited"
        //   101: aload_2         /* verify */
        //   102: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   105: ifne            185
        //   108: new             Ljava/lang/StringBuilder;
        //   111: dup            
        //   112: invokespecial   java/lang/StringBuilder.<init>:()V
        //   115: ldc_w           "Local address is "
        //   118: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   121: aload_1         /* session */
        //   122: invokestatic    javax/mail/internet/InternetAddress.getLocalAddress:(Ljavax/mail/Session;)Ljavax/mail/internet/InternetAddress;
        //   125: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   128: bipush          46
        //   130: invokevirtual   java/lang/StringBuilder.append:(C)Ljava/lang/StringBuilder;
        //   133: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   136: astore          msg
        //   138: aload_0         /* this */
        //   139: invokespecial   com/sun/mail/util/logging/MailHandler.getEncodingName:()Ljava/lang/String;
        //   142: invokestatic    java/nio/charset/Charset.forName:(Ljava/lang/String;)Ljava/nio/charset/Charset;
        //   145: pop            
        //   146: goto            190
        //   149: astore          RE
        //   151: new             Ljava/io/UnsupportedEncodingException;
        //   154: dup            
        //   155: aload           RE
        //   157: invokevirtual   java/lang/RuntimeException.toString:()Ljava/lang/String;
        //   160: invokespecial   java/io/UnsupportedEncodingException.<init>:(Ljava/lang/String;)V
        //   163: astore          UEE
        //   165: aload           UEE
        //   167: aload           RE
        //   169: invokevirtual   java/io/UnsupportedEncodingException.initCause:(Ljava/lang/Throwable;)Ljava/lang/Throwable;
        //   172: pop            
        //   173: aload_0         /* this */
        //   174: aload           msg
        //   176: aload           UEE
        //   178: iconst_5       
        //   179: invokevirtual   com/sun/mail/util/logging/MailHandler.reportError:(Ljava/lang/String;Ljava/lang/Exception;I)V
        //   182: goto            190
        //   185: ldc_w           "Skipping local address check."
        //   188: astore          msg
        //   190: aload_0         /* this */
        //   191: dup            
        //   192: astore          6
        //   194: monitorenter   
        //   195: aload_0         /* this */
        //   196: aload_3         /* abort */
        //   197: aload_0         /* this */
        //   198: aload_0         /* this */
        //   199: getfield        com/sun/mail/util/logging/MailHandler.subjectFormatter:Ljava/util/logging/Formatter;
        //   202: invokespecial   com/sun/mail/util/logging/MailHandler.head:(Ljava/util/logging/Formatter;)Ljava/lang/String;
        //   205: invokespecial   com/sun/mail/util/logging/MailHandler.appendSubject:(Ljavax/mail/Message;Ljava/lang/String;)V
        //   208: aload_0         /* this */
        //   209: aload_3         /* abort */
        //   210: aload_0         /* this */
        //   211: aload_0         /* this */
        //   212: getfield        com/sun/mail/util/logging/MailHandler.subjectFormatter:Ljava/util/logging/Formatter;
        //   215: ldc             ""
        //   217: invokespecial   com/sun/mail/util/logging/MailHandler.tail:(Ljava/util/logging/Formatter;Ljava/lang/String;)Ljava/lang/String;
        //   220: invokespecial   com/sun/mail/util/logging/MailHandler.appendSubject:(Ljavax/mail/Message;Ljava/lang/String;)V
        //   223: aload_0         /* this */
        //   224: getfield        com/sun/mail/util/logging/MailHandler.attachmentNames:[Ljava/util/logging/Formatter;
        //   227: arraylength    
        //   228: anewarray       Ljava/lang/String;
        //   231: astore          atn
        //   233: iconst_0       
        //   234: istore          i
        //   236: iload           i
        //   238: aload           atn
        //   240: arraylength    
        //   241: if_icmpge       324
        //   244: aload           atn
        //   246: iload           i
        //   248: aload_0         /* this */
        //   249: aload_0         /* this */
        //   250: getfield        com/sun/mail/util/logging/MailHandler.attachmentNames:[Ljava/util/logging/Formatter;
        //   253: iload           i
        //   255: aaload         
        //   256: invokespecial   com/sun/mail/util/logging/MailHandler.head:(Ljava/util/logging/Formatter;)Ljava/lang/String;
        //   259: aastore        
        //   260: aload           atn
        //   262: iload           i
        //   264: aaload         
        //   265: invokevirtual   java/lang/String.length:()I
        //   268: ifne            292
        //   271: aload           atn
        //   273: iload           i
        //   275: aload_0         /* this */
        //   276: aload_0         /* this */
        //   277: getfield        com/sun/mail/util/logging/MailHandler.attachmentNames:[Ljava/util/logging/Formatter;
        //   280: iload           i
        //   282: aaload         
        //   283: ldc             ""
        //   285: invokespecial   com/sun/mail/util/logging/MailHandler.tail:(Ljava/util/logging/Formatter;Ljava/lang/String;)Ljava/lang/String;
        //   288: aastore        
        //   289: goto            318
        //   292: aload           atn
        //   294: iload           i
        //   296: aload           atn
        //   298: iload           i
        //   300: aaload         
        //   301: aload_0         /* this */
        //   302: aload_0         /* this */
        //   303: getfield        com/sun/mail/util/logging/MailHandler.attachmentNames:[Ljava/util/logging/Formatter;
        //   306: iload           i
        //   308: aaload         
        //   309: ldc             ""
        //   311: invokespecial   com/sun/mail/util/logging/MailHandler.tail:(Ljava/util/logging/Formatter;Ljava/lang/String;)Ljava/lang/String;
        //   314: invokevirtual   java/lang/String.concat:(Ljava/lang/String;)Ljava/lang/String;
        //   317: aastore        
        //   318: iinc            i, 1
        //   321: goto            236
        //   324: aload           6
        //   326: monitorexit    
        //   327: goto            338
        //   330: astore          8
        //   332: aload           6
        //   334: monitorexit    
        //   335: aload           8
        //   337: athrow         
        //   338: aload_0         /* this */
        //   339: aload_3         /* abort */
        //   340: invokespecial   com/sun/mail/util/logging/MailHandler.setIncompleteCopy:(Ljavax/mail/Message;)V
        //   343: aload_0         /* this */
        //   344: aload_3         /* abort */
        //   345: iconst_1       
        //   346: invokespecial   com/sun/mail/util/logging/MailHandler.envelopeFor:(Ljavax/mail/Message;Z)V
        //   349: aload_0         /* this */
        //   350: aload_3         /* abort */
        //   351: aload           msg
        //   353: invokespecial   com/sun/mail/util/logging/MailHandler.saveChangesNoContent:(Ljavax/mail/Message;Ljava/lang/String;)V
        //   356: aload_3         /* abort */
        //   357: invokevirtual   javax/mail/internet/MimeMessage.getAllRecipients:()[Ljavax/mail/Address;
        //   360: astore          all
        //   362: aload           all
        //   364: ifnonnull       373
        //   367: iconst_0       
        //   368: anewarray       Ljavax/mail/internet/InternetAddress;
        //   371: astore          all
        //   373: aload           all
        //   375: arraylength    
        //   376: ifeq            384
        //   379: aload           all
        //   381: goto            388
        //   384: aload_3         /* abort */
        //   385: invokevirtual   javax/mail/internet/MimeMessage.getFrom:()[Ljavax/mail/Address;
        //   388: astore          any
        //   390: aload           any
        //   392: ifnull          422
        //   395: aload           any
        //   397: arraylength    
        //   398: ifeq            422
        //   401: aload_1         /* session */
        //   402: aload           any
        //   404: iconst_0       
        //   405: aaload         
        //   406: invokevirtual   javax/mail/Session.getTransport:(Ljavax/mail/Address;)Ljavax/mail/Transport;
        //   409: astore          t
        //   411: aload_1         /* session */
        //   412: ldc_w           "mail.transport.protocol"
        //   415: invokevirtual   javax/mail/Session.getProperty:(Ljava/lang/String;)Ljava/lang/String;
        //   418: pop            
        //   419: goto            446
        //   422: new             Ljavax/mail/MessagingException;
        //   425: dup            
        //   426: ldc_w           "No recipient or from address."
        //   429: invokespecial   javax/mail/MessagingException.<init>:(Ljava/lang/String;)V
        //   432: astore          me
        //   434: aload_0         /* this */
        //   435: aload           msg
        //   437: aload           me
        //   439: iconst_4       
        //   440: invokevirtual   com/sun/mail/util/logging/MailHandler.reportError:(Ljava/lang/String;Ljava/lang/Exception;I)V
        //   443: aload           me
        //   445: athrow         
        //   446: goto            498
        //   449: astore          protocol
        //   451: aload_0         /* this */
        //   452: getstatic       com/sun/mail/util/logging/MailHandler.MAILHANDLER_LOADER:Ljava/security/PrivilegedAction;
        //   455: invokespecial   com/sun/mail/util/logging/MailHandler.getAndSetContextClassLoader:(Ljava/lang/Object;)Ljava/lang/Object;
        //   458: astore          ccl
        //   460: aload_1         /* session */
        //   461: invokevirtual   javax/mail/Session.getTransport:()Ljavax/mail/Transport;
        //   464: astore          t
        //   466: aload_0         /* this */
        //   467: aload           ccl
        //   469: invokespecial   com/sun/mail/util/logging/MailHandler.getAndSetContextClassLoader:(Ljava/lang/Object;)Ljava/lang/Object;
        //   472: pop            
        //   473: goto            498
        //   476: astore          fail
        //   478: aload           protocol
        //   480: aload           fail
        //   482: invokestatic    com/sun/mail/util/logging/MailHandler.attach:(Ljavax/mail/MessagingException;Ljava/lang/Exception;)Ljavax/mail/MessagingException;
        //   485: athrow         
        //   486: astore          11
        //   488: aload_0         /* this */
        //   489: aload           ccl
        //   491: invokespecial   com/sun/mail/util/logging/MailHandler.getAndSetContextClassLoader:(Ljava/lang/Object;)Ljava/lang/Object;
        //   494: pop            
        //   495: aload           11
        //   497: athrow         
        //   498: aconst_null    
        //   499: astore          local
        //   501: ldc_w           "remote"
        //   504: aload_2         /* verify */
        //   505: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   508: ifne            521
        //   511: ldc_w           "login"
        //   514: aload_2         /* verify */
        //   515: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   518: ifeq            747
        //   521: aconst_null    
        //   522: astore          closed
        //   524: aload           t
        //   526: invokevirtual   javax/mail/Transport.connect:()V
        //   529: aload_0         /* this */
        //   530: aload           t
        //   532: invokespecial   com/sun/mail/util/logging/MailHandler.getLocalHost:(Ljavax/mail/Service;)Ljava/lang/String;
        //   535: astore          local
        //   537: ldc_w           "remote"
        //   540: aload_2         /* verify */
        //   541: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   544: ifeq            555
        //   547: aload           t
        //   549: aload_3         /* abort */
        //   550: aload           all
        //   552: invokevirtual   javax/mail/Transport.sendMessage:(Ljavax/mail/Message;[Ljavax/mail/Address;)V
        //   555: aload           t
        //   557: invokevirtual   javax/mail/Transport.close:()V
        //   560: goto            591
        //   563: astore          ME
        //   565: aload           ME
        //   567: astore          closed
        //   569: goto            591
        //   572: astore          12
        //   574: aload           t
        //   576: invokevirtual   javax/mail/Transport.close:()V
        //   579: goto            588
        //   582: astore          ME
        //   584: aload           ME
        //   586: astore          closed
        //   588: aload           12
        //   590: athrow         
        //   591: ldc_w           "remote"
        //   594: aload_2         /* verify */
        //   595: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   598: ifeq            611
        //   601: aload_0         /* this */
        //   602: aload_3         /* abort */
        //   603: aload_2         /* verify */
        //   604: aconst_null    
        //   605: invokespecial   com/sun/mail/util/logging/MailHandler.reportUnexpectedSend:(Ljavax/mail/internet/MimeMessage;Ljava/lang/String;Ljava/lang/Exception;)V
        //   608: goto            627
        //   611: aload           t
        //   613: invokevirtual   javax/mail/Transport.getURLName:()Ljavax/mail/URLName;
        //   616: invokevirtual   javax/mail/URLName.getProtocol:()Ljava/lang/String;
        //   619: astore          protocol
        //   621: aload_1         /* session */
        //   622: aload           protocol
        //   624: invokestatic    com/sun/mail/util/logging/MailHandler.verifyProperties:(Ljavax/mail/Session;Ljava/lang/String;)V
        //   627: goto            723
        //   630: astore          sfe
        //   632: aload           sfe
        //   634: invokevirtual   javax/mail/SendFailedException.getInvalidAddresses:()[Ljavax/mail/Address;
        //   637: astore          recip
        //   639: aload           recip
        //   641: ifnull          666
        //   644: aload           recip
        //   646: arraylength    
        //   647: ifeq            666
        //   650: aload_0         /* this */
        //   651: aload_3         /* abort */
        //   652: aload_2         /* verify */
        //   653: aload           sfe
        //   655: invokespecial   com/sun/mail/util/logging/MailHandler.setErrorContent:(Ljavax/mail/internet/MimeMessage;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   658: aload_0         /* this */
        //   659: aload_3         /* abort */
        //   660: aload           sfe
        //   662: iconst_4       
        //   663: invokespecial   com/sun/mail/util/logging/MailHandler.reportError:(Ljavax/mail/Message;Ljava/lang/Exception;I)V
        //   666: aload           sfe
        //   668: invokevirtual   javax/mail/SendFailedException.getValidSentAddresses:()[Ljavax/mail/Address;
        //   671: astore          recip
        //   673: aload           recip
        //   675: ifnull          692
        //   678: aload           recip
        //   680: arraylength    
        //   681: ifeq            692
        //   684: aload_0         /* this */
        //   685: aload_3         /* abort */
        //   686: aload_2         /* verify */
        //   687: aload           sfe
        //   689: invokespecial   com/sun/mail/util/logging/MailHandler.reportUnexpectedSend:(Ljavax/mail/internet/MimeMessage;Ljava/lang/String;Ljava/lang/Exception;)V
        //   692: goto            723
        //   695: astore          ME
        //   697: aload_0         /* this */
        //   698: aload_3         /* abort */
        //   699: aload           ME
        //   701: invokevirtual   com/sun/mail/util/logging/MailHandler.isMissingContent:(Ljavax/mail/Message;Ljava/lang/Throwable;)Z
        //   704: ifne            723
        //   707: aload_0         /* this */
        //   708: aload_3         /* abort */
        //   709: aload_2         /* verify */
        //   710: aload           ME
        //   712: invokespecial   com/sun/mail/util/logging/MailHandler.setErrorContent:(Ljavax/mail/internet/MimeMessage;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   715: aload_0         /* this */
        //   716: aload_3         /* abort */
        //   717: aload           ME
        //   719: iconst_4       
        //   720: invokespecial   com/sun/mail/util/logging/MailHandler.reportError:(Ljavax/mail/Message;Ljava/lang/Exception;I)V
        //   723: aload           closed
        //   725: ifnull          744
        //   728: aload_0         /* this */
        //   729: aload_3         /* abort */
        //   730: aload_2         /* verify */
        //   731: aload           closed
        //   733: invokespecial   com/sun/mail/util/logging/MailHandler.setErrorContent:(Ljavax/mail/internet/MimeMessage;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   736: aload_0         /* this */
        //   737: aload_3         /* abort */
        //   738: aload           closed
        //   740: iconst_3       
        //   741: invokespecial   com/sun/mail/util/logging/MailHandler.reportError:(Ljavax/mail/Message;Ljava/lang/Exception;I)V
        //   744: goto            1026
        //   747: aload           t
        //   749: invokevirtual   javax/mail/Transport.getURLName:()Ljavax/mail/URLName;
        //   752: invokevirtual   javax/mail/URLName.getProtocol:()Ljava/lang/String;
        //   755: astore          protocol
        //   757: aload_1         /* session */
        //   758: aload           protocol
        //   760: invokestatic    com/sun/mail/util/logging/MailHandler.verifyProperties:(Ljavax/mail/Session;Ljava/lang/String;)V
        //   763: aload_1         /* session */
        //   764: new             Ljava/lang/StringBuilder;
        //   767: dup            
        //   768: invokespecial   java/lang/StringBuilder.<init>:()V
        //   771: ldc_w           "mail."
        //   774: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   777: aload           protocol
        //   779: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   782: ldc_w           ".host"
        //   785: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   788: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   791: invokevirtual   javax/mail/Session.getProperty:(Ljava/lang/String;)Ljava/lang/String;
        //   794: astore          mailHost
        //   796: aload           mailHost
        //   798: invokestatic    com/sun/mail/util/logging/MailHandler.isEmpty:(Ljava/lang/CharSequence;)Z
        //   801: ifeq            816
        //   804: aload_1         /* session */
        //   805: ldc_w           "mail.host"
        //   808: invokevirtual   javax/mail/Session.getProperty:(Ljava/lang/String;)Ljava/lang/String;
        //   811: astore          mailHost
        //   813: goto            824
        //   816: aload_1         /* session */
        //   817: ldc_w           "mail.host"
        //   820: invokevirtual   javax/mail/Session.getProperty:(Ljava/lang/String;)Ljava/lang/String;
        //   823: pop            
        //   824: aload_1         /* session */
        //   825: new             Ljava/lang/StringBuilder;
        //   828: dup            
        //   829: invokespecial   java/lang/StringBuilder.<init>:()V
        //   832: ldc_w           "mail."
        //   835: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   838: aload           protocol
        //   840: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   843: ldc_w           ".localhost"
        //   846: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   849: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   852: invokevirtual   javax/mail/Session.getProperty:(Ljava/lang/String;)Ljava/lang/String;
        //   855: astore          local
        //   857: aload           local
        //   859: invokestatic    com/sun/mail/util/logging/MailHandler.isEmpty:(Ljava/lang/CharSequence;)Z
        //   862: ifeq            901
        //   865: aload_1         /* session */
        //   866: new             Ljava/lang/StringBuilder;
        //   869: dup            
        //   870: invokespecial   java/lang/StringBuilder.<init>:()V
        //   873: ldc_w           "mail."
        //   876: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   879: aload           protocol
        //   881: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   884: ldc_w           ".localaddress"
        //   887: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   890: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   893: invokevirtual   javax/mail/Session.getProperty:(Ljava/lang/String;)Ljava/lang/String;
        //   896: astore          local
        //   898: goto            933
        //   901: aload_1         /* session */
        //   902: new             Ljava/lang/StringBuilder;
        //   905: dup            
        //   906: invokespecial   java/lang/StringBuilder.<init>:()V
        //   909: ldc_w           "mail."
        //   912: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   915: aload           protocol
        //   917: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   920: ldc_w           ".localaddress"
        //   923: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   926: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   929: invokevirtual   javax/mail/Session.getProperty:(Ljava/lang/String;)Ljava/lang/String;
        //   932: pop            
        //   933: ldc_w           "resolve"
        //   936: aload_2         /* verify */
        //   937: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   940: ifeq            1026
        //   943: aload           t
        //   945: invokevirtual   javax/mail/Transport.getURLName:()Ljavax/mail/URLName;
        //   948: invokevirtual   javax/mail/URLName.getHost:()Ljava/lang/String;
        //   951: astore          transportHost
        //   953: aload           transportHost
        //   955: invokestatic    com/sun/mail/util/logging/MailHandler.isEmpty:(Ljava/lang/CharSequence;)Z
        //   958: ifne            986
        //   961: aload           transportHost
        //   963: invokestatic    com/sun/mail/util/logging/MailHandler.verifyHost:(Ljava/lang/String;)Ljava/net/InetAddress;
        //   966: pop            
        //   967: aload           transportHost
        //   969: aload           mailHost
        //   971: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   974: ifne            992
        //   977: aload           mailHost
        //   979: invokestatic    com/sun/mail/util/logging/MailHandler.verifyHost:(Ljava/lang/String;)Ljava/net/InetAddress;
        //   982: pop            
        //   983: goto            992
        //   986: aload           mailHost
        //   988: invokestatic    com/sun/mail/util/logging/MailHandler.verifyHost:(Ljava/lang/String;)Ljava/net/InetAddress;
        //   991: pop            
        //   992: goto            1026
        //   995: astore          IOE
        //   997: new             Ljavax/mail/MessagingException;
        //  1000: dup            
        //  1001: aload           msg
        //  1003: aload           IOE
        //  1005: invokespecial   javax/mail/MessagingException.<init>:(Ljava/lang/String;Ljava/lang/Exception;)V
        //  1008: astore          ME
        //  1010: aload_0         /* this */
        //  1011: aload_3         /* abort */
        //  1012: aload_2         /* verify */
        //  1013: aload           ME
        //  1015: invokespecial   com/sun/mail/util/logging/MailHandler.setErrorContent:(Ljavax/mail/internet/MimeMessage;Ljava/lang/String;Ljava/lang/Throwable;)V
        //  1018: aload_0         /* this */
        //  1019: aload_3         /* abort */
        //  1020: aload           ME
        //  1022: iconst_4       
        //  1023: invokespecial   com/sun/mail/util/logging/MailHandler.reportError:(Ljavax/mail/Message;Ljava/lang/Exception;I)V
        //  1026: ldc_w           "limited"
        //  1029: aload_2         /* verify */
        //  1030: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //  1033: ifne            1365
        //  1036: ldc_w           "remote"
        //  1039: aload_2         /* verify */
        //  1040: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //  1043: ifne            1064
        //  1046: ldc_w           "login"
        //  1049: aload_2         /* verify */
        //  1050: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //  1053: ifne            1064
        //  1056: aload_0         /* this */
        //  1057: aload           t
        //  1059: invokespecial   com/sun/mail/util/logging/MailHandler.getLocalHost:(Ljavax/mail/Service;)Ljava/lang/String;
        //  1062: astore          local
        //  1064: aload           local
        //  1066: invokestatic    com/sun/mail/util/logging/MailHandler.verifyHost:(Ljava/lang/String;)Ljava/net/InetAddress;
        //  1069: pop            
        //  1070: goto            1104
        //  1073: astore          IOE
        //  1075: new             Ljavax/mail/MessagingException;
        //  1078: dup            
        //  1079: aload           msg
        //  1081: aload           IOE
        //  1083: invokespecial   javax/mail/MessagingException.<init>:(Ljava/lang/String;Ljava/lang/Exception;)V
        //  1086: astore          ME
        //  1088: aload_0         /* this */
        //  1089: aload_3         /* abort */
        //  1090: aload_2         /* verify */
        //  1091: aload           ME
        //  1093: invokespecial   com/sun/mail/util/logging/MailHandler.setErrorContent:(Ljavax/mail/internet/MimeMessage;Ljava/lang/String;Ljava/lang/Throwable;)V
        //  1096: aload_0         /* this */
        //  1097: aload_3         /* abort */
        //  1098: aload           ME
        //  1100: iconst_4       
        //  1101: invokespecial   com/sun/mail/util/logging/MailHandler.reportError:(Ljavax/mail/Message;Ljava/lang/Exception;I)V
        //  1104: aload_0         /* this */
        //  1105: getstatic       com/sun/mail/util/logging/MailHandler.MAILHANDLER_LOADER:Ljava/security/PrivilegedAction;
        //  1108: invokespecial   com/sun/mail/util/logging/MailHandler.getAndSetContextClassLoader:(Ljava/lang/Object;)Ljava/lang/Object;
        //  1111: astore          ccl
        //  1113: new             Ljavax/mail/internet/MimeMultipart;
        //  1116: dup            
        //  1117: invokespecial   javax/mail/internet/MimeMultipart.<init>:()V
        //  1120: astore          multipart
        //  1122: aload           atn
        //  1124: arraylength    
        //  1125: anewarray       Ljavax/mail/internet/MimeBodyPart;
        //  1128: astore          ambp
        //  1130: aload_0         /* this */
        //  1131: dup            
        //  1132: astore          14
        //  1134: monitorenter   
        //  1135: aload_0         /* this */
        //  1136: aload_0         /* this */
        //  1137: invokevirtual   com/sun/mail/util/logging/MailHandler.getFormatter:()Ljava/util/logging/Formatter;
        //  1140: invokevirtual   com/sun/mail/util/logging/MailHandler.contentTypeOf:(Ljava/util/logging/Formatter;)Ljava/lang/String;
        //  1143: astore          bodyContentType
        //  1145: aload_0         /* this */
        //  1146: invokespecial   com/sun/mail/util/logging/MailHandler.createBodyPart:()Ljavax/mail/internet/MimeBodyPart;
        //  1149: astore          body
        //  1151: iconst_0       
        //  1152: istore          i
        //  1154: iload           i
        //  1156: aload           atn
        //  1158: arraylength    
        //  1159: if_icmpge       1206
        //  1162: aload           ambp
        //  1164: iload           i
        //  1166: aload_0         /* this */
        //  1167: iload           i
        //  1169: invokespecial   com/sun/mail/util/logging/MailHandler.createBodyPart:(I)Ljavax/mail/internet/MimeBodyPart;
        //  1172: aastore        
        //  1173: aload           ambp
        //  1175: iload           i
        //  1177: aaload         
        //  1178: aload           atn
        //  1180: iload           i
        //  1182: aaload         
        //  1183: invokevirtual   javax/mail/internet/MimeBodyPart.setFileName:(Ljava/lang/String;)V
        //  1186: aload           atn
        //  1188: iload           i
        //  1190: aload_0         /* this */
        //  1191: aload           atn
        //  1193: iload           i
        //  1195: aaload         
        //  1196: invokespecial   com/sun/mail/util/logging/MailHandler.getContentType:(Ljava/lang/String;)Ljava/lang/String;
        //  1199: aastore        
        //  1200: iinc            i, 1
        //  1203: goto            1154
        //  1206: aload           14
        //  1208: monitorexit    
        //  1209: goto            1220
        //  1212: astore          16
        //  1214: aload           14
        //  1216: monitorexit    
        //  1217: aload           16
        //  1219: athrow         
        //  1220: aload           body
        //  1222: aload_2         /* verify */
        //  1223: invokevirtual   javax/mail/internet/MimeBodyPart.setDescription:(Ljava/lang/String;)V
        //  1226: aload_0         /* this */
        //  1227: aload           body
        //  1229: ldc             ""
        //  1231: aload           bodyContentType
        //  1233: invokespecial   com/sun/mail/util/logging/MailHandler.setContent:(Ljavax/mail/internet/MimePart;Ljava/lang/CharSequence;Ljava/lang/String;)V
        //  1236: aload           multipart
        //  1238: aload           body
        //  1240: invokevirtual   javax/mail/internet/MimeMultipart.addBodyPart:(Ljavax/mail/BodyPart;)V
        //  1243: iconst_0       
        //  1244: istore          i
        //  1246: iload           i
        //  1248: aload           ambp
        //  1250: arraylength    
        //  1251: if_icmpge       1285
        //  1254: aload           ambp
        //  1256: iload           i
        //  1258: aaload         
        //  1259: aload_2         /* verify */
        //  1260: invokevirtual   javax/mail/internet/MimeBodyPart.setDescription:(Ljava/lang/String;)V
        //  1263: aload_0         /* this */
        //  1264: aload           ambp
        //  1266: iload           i
        //  1268: aaload         
        //  1269: ldc             ""
        //  1271: aload           atn
        //  1273: iload           i
        //  1275: aaload         
        //  1276: invokespecial   com/sun/mail/util/logging/MailHandler.setContent:(Ljavax/mail/internet/MimePart;Ljava/lang/CharSequence;Ljava/lang/String;)V
        //  1279: iinc            i, 1
        //  1282: goto            1246
        //  1285: aload_3         /* abort */
        //  1286: aload           multipart
        //  1288: invokevirtual   javax/mail/internet/MimeMessage.setContent:(Ljavax/mail/Multipart;)V
        //  1291: aload_3         /* abort */
        //  1292: invokevirtual   javax/mail/internet/MimeMessage.saveChanges:()V
        //  1295: aload_3         /* abort */
        //  1296: new             Ljava/io/ByteArrayOutputStream;
        //  1299: dup            
        //  1300: sipush          1024
        //  1303: invokespecial   java/io/ByteArrayOutputStream.<init>:(I)V
        //  1306: invokevirtual   javax/mail/internet/MimeMessage.writeTo:(Ljava/io/OutputStream;)V
        //  1309: aload_0         /* this */
        //  1310: aload           ccl
        //  1312: invokespecial   com/sun/mail/util/logging/MailHandler.getAndSetContextClassLoader:(Ljava/lang/Object;)Ljava/lang/Object;
        //  1315: pop            
        //  1316: goto            1331
        //  1319: astore          17
        //  1321: aload_0         /* this */
        //  1322: aload           ccl
        //  1324: invokespecial   com/sun/mail/util/logging/MailHandler.getAndSetContextClassLoader:(Ljava/lang/Object;)Ljava/lang/Object;
        //  1327: pop            
        //  1328: aload           17
        //  1330: athrow         
        //  1331: goto            1365
        //  1334: astore          IOE
        //  1336: new             Ljavax/mail/MessagingException;
        //  1339: dup            
        //  1340: aload           msg
        //  1342: aload           IOE
        //  1344: invokespecial   javax/mail/MessagingException.<init>:(Ljava/lang/String;Ljava/lang/Exception;)V
        //  1347: astore          ME
        //  1349: aload_0         /* this */
        //  1350: aload_3         /* abort */
        //  1351: aload_2         /* verify */
        //  1352: aload           ME
        //  1354: invokespecial   com/sun/mail/util/logging/MailHandler.setErrorContent:(Ljavax/mail/internet/MimeMessage;Ljava/lang/String;Ljava/lang/Throwable;)V
        //  1357: aload_0         /* this */
        //  1358: aload_3         /* abort */
        //  1359: aload           ME
        //  1361: iconst_5       
        //  1362: invokespecial   com/sun/mail/util/logging/MailHandler.reportError:(Ljavax/mail/Message;Ljava/lang/Exception;I)V
        //  1365: aload           all
        //  1367: arraylength    
        //  1368: ifeq            1379
        //  1371: aload           all
        //  1373: invokestatic    com/sun/mail/util/logging/MailHandler.verifyAddresses:([Ljavax/mail/Address;)V
        //  1376: goto            1390
        //  1379: new             Ljavax/mail/MessagingException;
        //  1382: dup            
        //  1383: ldc_w           "No recipient addresses."
        //  1386: invokespecial   javax/mail/MessagingException.<init>:(Ljava/lang/String;)V
        //  1389: athrow         
        //  1390: aload_3         /* abort */
        //  1391: invokevirtual   javax/mail/internet/MimeMessage.getFrom:()[Ljavax/mail/Address;
        //  1394: astore          from
        //  1396: aload_3         /* abort */
        //  1397: invokevirtual   javax/mail/internet/MimeMessage.getSender:()Ljavax/mail/Address;
        //  1400: astore          sender
        //  1402: aload           sender
        //  1404: instanceof      Ljavax/mail/internet/InternetAddress;
        //  1407: ifeq            1418
        //  1410: aload           sender
        //  1412: checkcast       Ljavax/mail/internet/InternetAddress;
        //  1415: invokevirtual   javax/mail/internet/InternetAddress.validate:()V
        //  1418: aload_3         /* abort */
        //  1419: ldc_w           "From"
        //  1422: ldc_w           ","
        //  1425: invokevirtual   javax/mail/internet/MimeMessage.getHeader:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //  1428: ifnull          1523
        //  1431: aload           from
        //  1433: arraylength    
        //  1434: ifeq            1523
        //  1437: aload           from
        //  1439: invokestatic    com/sun/mail/util/logging/MailHandler.verifyAddresses:([Ljavax/mail/Address;)V
        //  1442: iconst_0       
        //  1443: istore          i
        //  1445: iload           i
        //  1447: aload           from
        //  1449: arraylength    
        //  1450: if_icmpge       1520
        //  1453: aload           from
        //  1455: iload           i
        //  1457: aaload         
        //  1458: aload           sender
        //  1460: invokevirtual   javax/mail/Address.equals:(Ljava/lang/Object;)Z
        //  1463: ifeq            1514
        //  1466: new             Ljavax/mail/MessagingException;
        //  1469: dup            
        //  1470: new             Ljava/lang/StringBuilder;
        //  1473: dup            
        //  1474: invokespecial   java/lang/StringBuilder.<init>:()V
        //  1477: ldc_w           "Sender address '"
        //  1480: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //  1483: aload           sender
        //  1485: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //  1488: ldc_w           "' equals from address."
        //  1491: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //  1494: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //  1497: invokespecial   javax/mail/MessagingException.<init>:(Ljava/lang/String;)V
        //  1500: astore          ME
        //  1502: new             Ljavax/mail/MessagingException;
        //  1505: dup            
        //  1506: aload           msg
        //  1508: aload           ME
        //  1510: invokespecial   javax/mail/MessagingException.<init>:(Ljava/lang/String;Ljava/lang/Exception;)V
        //  1513: athrow         
        //  1514: iinc            i, 1
        //  1517: goto            1445
        //  1520: goto            1552
        //  1523: aload           sender
        //  1525: ifnonnull       1552
        //  1528: new             Ljavax/mail/MessagingException;
        //  1531: dup            
        //  1532: ldc_w           "No from or sender address."
        //  1535: invokespecial   javax/mail/MessagingException.<init>:(Ljava/lang/String;)V
        //  1538: astore          ME
        //  1540: new             Ljavax/mail/MessagingException;
        //  1543: dup            
        //  1544: aload           msg
        //  1546: aload           ME
        //  1548: invokespecial   javax/mail/MessagingException.<init>:(Ljava/lang/String;Ljava/lang/Exception;)V
        //  1551: athrow         
        //  1552: aload_3         /* abort */
        //  1553: invokevirtual   javax/mail/internet/MimeMessage.getReplyTo:()[Ljavax/mail/Address;
        //  1556: invokestatic    com/sun/mail/util/logging/MailHandler.verifyAddresses:([Ljavax/mail/Address;)V
        //  1559: goto            1601
        //  1562: astore          RE
        //  1564: aload_0         /* this */
        //  1565: aload_3         /* abort */
        //  1566: aload_2         /* verify */
        //  1567: aload           RE
        //  1569: invokespecial   com/sun/mail/util/logging/MailHandler.setErrorContent:(Ljavax/mail/internet/MimeMessage;Ljava/lang/String;Ljava/lang/Throwable;)V
        //  1572: aload_0         /* this */
        //  1573: aload_3         /* abort */
        //  1574: aload           RE
        //  1576: iconst_4       
        //  1577: invokespecial   com/sun/mail/util/logging/MailHandler.reportError:(Ljavax/mail/Message;Ljava/lang/Exception;I)V
        //  1580: goto            1601
        //  1583: astore          ME
        //  1585: aload_0         /* this */
        //  1586: aload_3         /* abort */
        //  1587: aload_2         /* verify */
        //  1588: aload           ME
        //  1590: invokespecial   com/sun/mail/util/logging/MailHandler.setErrorContent:(Ljavax/mail/internet/MimeMessage;Ljava/lang/String;Ljava/lang/Throwable;)V
        //  1593: aload_0         /* this */
        //  1594: aload_3         /* abort */
        //  1595: aload           ME
        //  1597: iconst_4       
        //  1598: invokespecial   com/sun/mail/util/logging/MailHandler.reportError:(Ljavax/mail/Message;Ljava/lang/Exception;I)V
        //  1601: return         
        //    StackMapTable: 00 44 16 FB 00 42 FF 00 3B 00 05 07 02 87 07 02 E9 07 02 A3 07 03 AE 07 02 A3 00 01 07 03 2E FA 00 23 FC 00 04 07 02 A3 FE 00 2D 07 03 75 07 02 9A 01 37 19 FA 00 05 FF 00 05 00 07 07 02 87 07 02 E9 07 02 A3 07 03 AE 07 02 A3 00 07 02 9A 00 01 07 02 95 FF 00 07 00 06 07 02 87 07 02 E9 07 02 A3 07 03 AE 07 02 A3 07 03 75 00 00 FC 00 22 07 03 D1 0A 43 07 03 D1 FD 00 21 00 07 03 D1 FF 00 17 00 08 07 02 87 07 02 E9 07 02 A3 07 03 AE 07 02 A3 07 03 75 07 03 D1 07 03 D2 00 00 FF 00 02 00 07 07 02 87 07 02 E9 07 02 A3 07 03 AE 07 02 A3 07 03 75 07 03 D1 00 01 07 03 46 FF 00 1A 00 0A 07 02 87 07 02 E9 07 02 A3 07 03 AE 07 02 A3 07 03 75 07 03 D1 00 07 03 46 07 02 9A 00 01 07 03 46 49 07 02 95 FF 00 0B 00 08 07 02 87 07 02 E9 07 02 A3 07 03 AE 07 02 A3 07 03 75 07 03 D1 07 03 D2 00 00 FC 00 16 07 02 A3 FC 00 21 07 03 46 47 07 03 46 48 07 02 95 FF 00 09 00 0D 07 02 87 07 02 E9 07 02 A3 07 03 AE 07 02 A3 07 03 75 07 03 D1 07 03 D2 07 02 A3 07 03 46 00 00 07 02 95 00 01 07 03 46 05 F8 00 02 13 0F 42 07 03 D3 FD 00 23 07 03 D3 07 03 D1 F9 00 19 42 07 03 46 1B FA 00 14 02 FD 00 44 07 02 A3 07 02 A3 07 FB 00 4C 1F FC 00 34 07 02 A3 FA 00 05 42 07 03 2F F9 00 1E 25 48 07 03 2F 1E FF 00 31 00 10 07 02 87 07 02 E9 07 02 A3 07 03 AE 07 02 A3 07 03 75 07 03 D1 07 03 D2 07 02 A3 07 02 9A 07 03 B3 07 03 AF 07 03 D4 07 02 A3 07 02 9A 01 00 00 FA 00 33 FF 00 05 00 0F 07 02 87 07 02 E9 07 02 A3 07 03 AE 07 02 A3 07 03 75 07 03 D1 07 03 D2 07 02 A3 07 02 9A 07 03 B3 07 03 AF 00 00 07 02 9A 00 01 07 02 95 FF 00 07 00 0E 07 02 87 07 02 E9 07 02 A3 07 03 AE 07 02 A3 07 03 75 07 03 D1 07 03 D2 07 02 A3 07 02 9A 07 03 B3 07 03 AF 07 03 D4 07 02 A3 00 00 FC 00 19 01 FA 00 26 FF 00 21 00 0A 07 02 87 07 02 E9 07 02 A3 07 03 AE 07 02 A3 07 03 75 07 03 D1 07 03 D2 07 02 A3 07 02 9A 00 01 07 02 95 FA 00 0B 42 07 03 19 1E 0D 0A FD 00 1B 07 03 D1 07 03 D5 FC 00 1A 01 FB 00 44 FA 00 05 02 1C FF 00 09 00 06 07 02 87 07 02 E9 07 02 A3 07 03 AE 07 02 A3 07 03 75 00 01 07 03 2E 54 07 03 2F 11
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  138    146    149    185    Ljava/lang/RuntimeException;
        //  195    327    330    338    Any
        //  330    335    330    338    Any
        //  373    446    449    498    Ljavax/mail/MessagingException;
        //  460    466    476    486    Ljavax/mail/MessagingException;
        //  460    466    486    498    Any
        //  476    488    486    498    Any
        //  555    560    563    572    Ljavax/mail/MessagingException;
        //  529    555    572    591    Any
        //  574    579    582    588    Ljavax/mail/MessagingException;
        //  572    574    572    591    Any
        //  529    627    630    695    Ljavax/mail/SendFailedException;
        //  529    627    695    723    Ljavax/mail/MessagingException;
        //  943    992    995    1026   Ljava/lang/RuntimeException;
        //  943    992    995    1026   Ljava/io/IOException;
        //  1036   1070   1073   1104   Ljava/lang/RuntimeException;
        //  1036   1070   1073   1104   Ljava/io/IOException;
        //  1135   1209   1212   1220   Any
        //  1212   1217   1212   1220   Any
        //  1113   1309   1319   1331   Any
        //  1319   1321   1319   1331   Any
        //  1104   1331   1334   1365   Ljava/io/IOException;
        //  356    1559   1562   1583   Ljava/lang/RuntimeException;
        //  356    1559   1583   1601   Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.assembler.ir.StackMappingVisitor.push(StackMappingVisitor.java:290)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.execute(StackMappingVisitor.java:837)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.visit(StackMappingVisitor.java:398)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2086)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void saveChangesNoContent(final Message abort, final String msg) {
        if (abort != null) {
            try {
                try {
                    abort.saveChanges();
                }
                catch (final NullPointerException xferEncoding) {
                    try {
                        final String cte = "Content-Transfer-Encoding";
                        if (abort.getHeader(cte) == null) {
                            abort.setHeader(cte, "base64");
                            abort.saveChanges();
                            return;
                        }
                        throw xferEncoding;
                    }
                    catch (final RuntimeException | MessagingException e) {
                        if (e != xferEncoding) {
                            e.addSuppressed(xferEncoding);
                        }
                        throw e;
                    }
                }
            }
            catch (final RuntimeException | MessagingException ME) {
                this.reportError(msg, ME, 5);
            }
        }
    }
    
    private static void verifyProperties(final Session session, final String protocol) {
        session.getProperty("mail.from");
        session.getProperty("mail." + protocol + ".from");
        session.getProperty("mail.dsn.ret");
        session.getProperty("mail." + protocol + ".dsn.ret");
        session.getProperty("mail.dsn.notify");
        session.getProperty("mail." + protocol + ".dsn.notify");
        session.getProperty("mail." + protocol + ".port");
        session.getProperty("mail.user");
        session.getProperty("mail." + protocol + ".user");
        session.getProperty("mail." + protocol + ".localport");
    }
    
    private static InetAddress verifyHost(final String host) throws IOException {
        InetAddress a;
        if (isEmpty(host)) {
            a = InetAddress.getLocalHost();
        }
        else {
            a = InetAddress.getByName(host);
        }
        if (a.getCanonicalHostName().length() == 0) {
            throw new UnknownHostException();
        }
        return a;
    }
    
    private static void verifyAddresses(final Address[] all) throws AddressException {
        if (all != null) {
            for (int i = 0; i < all.length; ++i) {
                final Address a = all[i];
                if (a instanceof InternetAddress) {
                    ((InternetAddress)a).validate();
                }
            }
        }
    }
    
    private void reportUnexpectedSend(final MimeMessage msg, final String verify, final Exception cause) {
        final MessagingException write = new MessagingException("An empty message was sent.", cause);
        this.setErrorContent(msg, verify, write);
        this.reportError(msg, write, 4);
    }
    
    private void setErrorContent(final MimeMessage msg, final String verify, final Throwable t) {
        try {
            final MimeBodyPart body;
            final String msgDesc;
            final String subjectType;
            synchronized (this) {
                body = this.createBodyPart();
                msgDesc = this.descriptionFrom(this.comparator, this.pushLevel, this.pushFilter);
                subjectType = this.getClassId(this.subjectFormatter);
            }
            body.setDescription("Formatted using " + ((t == null) ? Throwable.class.getName() : t.getClass().getName()) + ", filtered with " + verify + ", and named by " + subjectType + '.');
            this.setContent(body, this.toMsgString(t), "text/plain");
            final MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(body);
            msg.setContent(multipart);
            msg.setDescription(msgDesc);
            this.setAcceptLang(msg);
            msg.saveChanges();
        }
        catch (final MessagingException | RuntimeException ME) {
            this.reportError("Unable to create body.", ME, 4);
        }
    }
    
    private Session updateSession() {
        assert Thread.holdsLock(this);
        Session settings;
        if (this.mailProps.getProperty("verify") != null) {
            settings = this.initSession();
            assert settings == this.session : this.session;
        }
        else {
            this.session = null;
            settings = null;
        }
        return settings;
    }
    
    private Session initSession() {
        assert Thread.holdsLock(this);
        final String p = this.getClass().getName();
        final LogManagerProperties proxy = new LogManagerProperties(this.mailProps, p);
        return this.session = Session.getInstance(proxy, this.auth);
    }
    
    private void envelopeFor(final Message msg, final boolean priority) {
        this.setAcceptLang(msg);
        this.setFrom(msg);
        if (!this.setRecipient(msg, "mail.to", Message.RecipientType.TO)) {
            this.setDefaultRecipient(msg, Message.RecipientType.TO);
        }
        this.setRecipient(msg, "mail.cc", Message.RecipientType.CC);
        this.setRecipient(msg, "mail.bcc", Message.RecipientType.BCC);
        this.setReplyTo(msg);
        this.setSender(msg);
        this.setMailer(msg);
        this.setAutoSubmitted(msg);
        if (priority) {
            this.setPriority(msg);
        }
        try {
            msg.setSentDate(new Date());
        }
        catch (final MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private MimeBodyPart createBodyPart() throws MessagingException {
        assert Thread.holdsLock(this);
        final MimeBodyPart part = new MimeBodyPart();
        part.setDisposition("inline");
        part.setDescription(this.descriptionFrom(this.getFormatter(), this.getFilter(), this.subjectFormatter));
        this.setAcceptLang(part);
        return part;
    }
    
    private MimeBodyPart createBodyPart(final int index) throws MessagingException {
        assert Thread.holdsLock(this);
        final MimeBodyPart part = new MimeBodyPart();
        part.setDisposition("attachment");
        part.setDescription(this.descriptionFrom(this.attachmentFormatters[index], this.attachmentFilters[index], this.attachmentNames[index]));
        this.setAcceptLang(part);
        return part;
    }
    
    private String descriptionFrom(final Comparator<?> c, final Level l, final Filter f) {
        return "Sorted using " + ((c == null) ? "no comparator" : c.getClass().getName()) + ", pushed when " + l.getName() + ", and " + ((f == null) ? "no push filter" : f.getClass().getName()) + '.';
    }
    
    private String descriptionFrom(final Formatter f, final Filter filter, final Formatter name) {
        return "Formatted using " + this.getClassId(f) + ", filtered with " + ((filter == null) ? "no filter" : filter.getClass().getName()) + ", and named by " + this.getClassId(name) + '.';
    }
    
    private String getClassId(final Formatter f) {
        if (f instanceof TailNameFormatter) {
            return String.class.getName();
        }
        return f.getClass().getName();
    }
    
    private String toString(final Formatter f) {
        final String name = f.toString();
        if (!isEmpty(name)) {
            return name;
        }
        return this.getClassId(f);
    }
    
    private void appendFileName(final Part part, final String chunk) {
        if (chunk != null) {
            if (chunk.length() > 0) {
                this.appendFileName0(part, chunk);
            }
        }
        else {
            this.reportNullError(5);
        }
    }
    
    private void appendFileName0(final Part part, String chunk) {
        try {
            chunk = chunk.replaceAll("[\\x00-\\x1F\\x7F]+", "");
            final String old = part.getFileName();
            part.setFileName((old != null) ? old.concat(chunk) : chunk);
        }
        catch (final MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private void appendSubject(final Message msg, final String chunk) {
        if (chunk != null) {
            if (chunk.length() > 0) {
                this.appendSubject0(msg, chunk);
            }
        }
        else {
            this.reportNullError(5);
        }
    }
    
    private void appendSubject0(final Message msg, String chunk) {
        try {
            chunk = chunk.replaceAll("[\\x00-\\x1F\\x7F]+", "");
            final String charset = this.getEncodingName();
            final String old = msg.getSubject();
            assert msg instanceof MimeMessage : msg;
            ((MimeMessage)msg).setSubject((old != null) ? old.concat(chunk) : chunk, MimeUtility.mimeCharset(charset));
        }
        catch (final MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private Locale localeFor(final LogRecord r) {
        final ResourceBundle rb = r.getResourceBundle();
        Locale l;
        if (rb != null) {
            l = rb.getLocale();
            if (l == null || isEmpty(l.getLanguage())) {
                l = Locale.getDefault();
            }
        }
        else {
            l = null;
        }
        return l;
    }
    
    private void appendContentLang(final MimePart p, final Locale l) {
        try {
            String lang = LogManagerProperties.toLanguageTag(l);
            if (lang.length() != 0) {
                String header = p.getHeader("Content-Language", null);
                if (isEmpty(header)) {
                    p.setHeader("Content-Language", lang);
                }
                else if (!header.equalsIgnoreCase(lang)) {
                    lang = ",".concat(lang);
                    int idx = 0;
                    while ((idx = header.indexOf(lang, idx)) > -1) {
                        idx += lang.length();
                        if (idx == header.length() || header.charAt(idx) == ',') {
                            break;
                        }
                    }
                    if (idx < 0) {
                        int len = header.lastIndexOf("\r\n\t");
                        if (len < 0) {
                            len = 20 + header.length();
                        }
                        else {
                            len = header.length() - len + 8;
                        }
                        if (len + lang.length() > 76) {
                            header = header.concat("\r\n\t".concat(lang));
                        }
                        else {
                            header = header.concat(lang);
                        }
                        p.setHeader("Content-Language", header);
                    }
                }
            }
        }
        catch (final MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private void setAcceptLang(final Part p) {
        try {
            final String lang = LogManagerProperties.toLanguageTag(Locale.getDefault());
            if (lang.length() != 0) {
                p.setHeader("Accept-Language", lang);
            }
        }
        catch (final MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private void reportFilterError(final LogRecord record) {
        assert Thread.holdsLock(this);
        final Formatter f = createSimpleFormatter();
        final String msg = "Log record " + record.getSequenceNumber() + " was filtered from all message parts.  " + this.head(f) + this.format(f, record) + this.tail(f, "");
        final String txt = this.getFilter() + ", " + Arrays.asList(this.readOnlyAttachmentFilters());
        this.reportError(msg, new IllegalArgumentException(txt), 5);
    }
    
    private void reportNonSymmetric(final Object o, final Object found) {
        this.reportError("Non symmetric equals implementation.", new IllegalArgumentException(o.getClass().getName() + " is not equal to " + found.getClass().getName()), 4);
    }
    
    private void reportNonDiscriminating(final Object o, final Object found) {
        this.reportError("Non discriminating equals implementation.", new IllegalArgumentException(o.getClass().getName() + " should not be equal to " + found.getClass().getName()), 4);
    }
    
    private void reportNullError(final int code) {
        this.reportError("null", new NullPointerException(), code);
    }
    
    private String head(final Formatter f) {
        try {
            return f.getHead(this);
        }
        catch (final RuntimeException RE) {
            this.reportError(RE.getMessage(), RE, 5);
            return "";
        }
    }
    
    private String format(final Formatter f, final LogRecord r) {
        try {
            return f.format(r);
        }
        catch (final RuntimeException RE) {
            this.reportError(RE.getMessage(), RE, 5);
            return "";
        }
    }
    
    private String tail(final Formatter f, final String def) {
        try {
            return f.getTail(this);
        }
        catch (final RuntimeException RE) {
            this.reportError(RE.getMessage(), RE, 5);
            return def;
        }
    }
    
    private void setMailer(final Message msg) {
        try {
            final Class<?> mail = MailHandler.class;
            final Class<?> k = this.getClass();
            String value;
            if (k == mail) {
                value = mail.getName();
            }
            else {
                try {
                    value = MimeUtility.encodeText(k.getName());
                }
                catch (final UnsupportedEncodingException E) {
                    this.reportError(E.getMessage(), E, 5);
                    value = k.getName().replaceAll("[^\\x00-\\x7F]", "\u001a");
                }
                value = MimeUtility.fold(10, mail.getName() + " using the " + value + " extension.");
            }
            msg.setHeader("X-Mailer", value);
        }
        catch (final MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private void setPriority(final Message msg) {
        try {
            msg.setHeader("Importance", "High");
            msg.setHeader("Priority", "urgent");
            msg.setHeader("X-Priority", "2");
        }
        catch (final MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private void setIncompleteCopy(final Message msg) {
        try {
            msg.setHeader("Incomplete-Copy", "");
        }
        catch (final MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private void setAutoSubmitted(final Message msg) {
        if (this.allowRestrictedHeaders()) {
            try {
                msg.setHeader("auto-submitted", "auto-generated");
            }
            catch (final MessagingException ME) {
                this.reportError(ME.getMessage(), ME, 5);
            }
        }
    }
    
    private void setFrom(final Message msg) {
        final String from = this.getSession(msg).getProperty("mail.from");
        if (from != null) {
            try {
                final Address[] address = InternetAddress.parse(from, false);
                if (address.length > 0) {
                    if (address.length == 1) {
                        msg.setFrom(address[0]);
                    }
                    else {
                        msg.addFrom(address);
                    }
                }
            }
            catch (final MessagingException ME) {
                this.reportError(ME.getMessage(), ME, 5);
                this.setDefaultFrom(msg);
            }
        }
        else {
            this.setDefaultFrom(msg);
        }
    }
    
    private void setDefaultFrom(final Message msg) {
        try {
            msg.setFrom();
        }
        catch (final MessagingException ME) {
            this.reportError(ME.getMessage(), ME, 5);
        }
    }
    
    private void setDefaultRecipient(final Message msg, final Message.RecipientType type) {
        try {
            final Address a = InternetAddress.getLocalAddress(this.getSession(msg));
            if (a != null) {
                msg.setRecipient(type, a);
            }
            else {
                final MimeMessage m = new MimeMessage(this.getSession(msg));
                m.setFrom();
                final Address[] from = m.getFrom();
                if (from.length <= 0) {
                    throw new MessagingException("No local address.");
                }
                msg.setRecipients(type, from);
            }
        }
        catch (final MessagingException | RuntimeException ME) {
            this.reportError("Unable to compute a default recipient.", ME, 5);
        }
    }
    
    private void setReplyTo(final Message msg) {
        final String reply = this.getSession(msg).getProperty("mail.reply.to");
        if (!isEmpty(reply)) {
            try {
                final Address[] address = InternetAddress.parse(reply, false);
                if (address.length > 0) {
                    msg.setReplyTo(address);
                }
            }
            catch (final MessagingException ME) {
                this.reportError(ME.getMessage(), ME, 5);
            }
        }
    }
    
    private void setSender(final Message msg) {
        assert msg instanceof MimeMessage : msg;
        final String sender = this.getSession(msg).getProperty("mail.sender");
        if (!isEmpty(sender)) {
            try {
                final InternetAddress[] address = InternetAddress.parse(sender, false);
                if (address.length > 0) {
                    ((MimeMessage)msg).setSender(address[0]);
                    if (address.length > 1) {
                        this.reportError("Ignoring other senders.", this.tooManyAddresses(address, 1), 5);
                    }
                }
            }
            catch (final MessagingException ME) {
                this.reportError(ME.getMessage(), ME, 5);
            }
        }
    }
    
    private AddressException tooManyAddresses(final Address[] address, final int offset) {
        final Object l = Arrays.asList(address).subList(offset, address.length);
        return new AddressException(l.toString());
    }
    
    private boolean setRecipient(final Message msg, final String key, final Message.RecipientType type) {
        final String value = this.getSession(msg).getProperty(key);
        final boolean containsKey = value != null;
        if (!isEmpty(value)) {
            try {
                final Address[] address = InternetAddress.parse(value, false);
                if (address.length > 0) {
                    msg.setRecipients(type, address);
                }
            }
            catch (final MessagingException ME) {
                this.reportError(ME.getMessage(), ME, 5);
            }
        }
        return containsKey;
    }
    
    private String toRawString(final Message msg) throws MessagingException, IOException {
        if (msg != null) {
            final Object ccl = this.getAndSetContextClassLoader(MailHandler.MAILHANDLER_LOADER);
            try {
                final int nbytes = Math.max(msg.getSize() + 1024, 1024);
                final ByteArrayOutputStream out = new ByteArrayOutputStream(nbytes);
                msg.writeTo(out);
                return out.toString("UTF-8");
            }
            finally {
                this.getAndSetContextClassLoader(ccl);
            }
        }
        return null;
    }
    
    private String toMsgString(final Throwable t) {
        if (t == null) {
            return "null";
        }
        final String charset = this.getEncodingName();
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            try (final OutputStreamWriter ows = new OutputStreamWriter(out, charset);
                 final PrintWriter pw = new PrintWriter(ows)) {
                pw.println(t.getMessage());
                t.printStackTrace(pw);
                pw.flush();
            }
            return out.toString(charset);
        }
        catch (final RuntimeException unexpected) {
            return t.toString() + ' ' + unexpected.toString();
        }
        catch (final Exception badMimeCharset) {
            return t.toString() + ' ' + badMimeCharset.toString();
        }
    }
    
    private Object getAndSetContextClassLoader(final Object ccl) {
        if (ccl != GetAndSetContext.NOT_MODIFIED) {
            try {
                PrivilegedAction<?> pa;
                if (ccl instanceof PrivilegedAction) {
                    pa = (PrivilegedAction)ccl;
                }
                else {
                    pa = new GetAndSetContext(ccl);
                }
                return AccessController.doPrivileged(pa);
            }
            catch (final SecurityException ex) {}
        }
        return GetAndSetContext.NOT_MODIFIED;
    }
    
    private static RuntimeException attachmentMismatch(final String msg) {
        return new IndexOutOfBoundsException(msg);
    }
    
    private static RuntimeException attachmentMismatch(final int expected, final int found) {
        return attachmentMismatch("Attachments mismatched, expected " + expected + " but given " + found + '.');
    }
    
    private static MessagingException attach(final MessagingException required, final Exception optional) {
        if (optional != null && !required.setNextException(optional)) {
            if (optional instanceof MessagingException) {
                final MessagingException head = (MessagingException)optional;
                if (head.setNextException(required)) {
                    return head;
                }
            }
            if (optional != required) {
                required.addSuppressed(optional);
            }
        }
        return required;
    }
    
    private String getLocalHost(final Service s) {
        try {
            return LogManagerProperties.getLocalHost(s);
        }
        catch (final SecurityException | NoSuchMethodException | LinkageError securityException | NoSuchMethodException | LinkageError) {}
        catch (final Exception ex) {
            this.reportError(s.toString(), ex, 4);
        }
        return null;
    }
    
    private Session getSession(final Message msg) {
        if (msg == null) {
            throw new NullPointerException();
        }
        return new MessageContext(msg).getSession();
    }
    
    private boolean allowRestrictedHeaders() {
        return LogManagerProperties.hasLogManager();
    }
    
    private static String atIndexMsg(final int i) {
        return "At index: " + i + '.';
    }
    
    static {
        EMPTY_FILTERS = new Filter[0];
        EMPTY_FORMATTERS = new Formatter[0];
        offValue = Level.OFF.intValue();
        MAILHANDLER_LOADER = new GetAndSetContext(MailHandler.class);
        MUTEX = new ThreadLocal<Integer>();
        MUTEX_PUBLISH = -2;
        MUTEX_REPORT = -4;
        MUTEX_LINKAGE = -8;
    }
    
    private static final class DefaultAuthenticator extends Authenticator
    {
        private final String pass;
        
        static Authenticator of(final String pass) {
            return new DefaultAuthenticator(pass);
        }
        
        private DefaultAuthenticator(final String pass) {
            assert pass != null;
            this.pass = pass;
        }
        
        @Override
        protected final PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(this.getDefaultUserName(), this.pass);
        }
    }
    
    private static final class GetAndSetContext implements PrivilegedAction<Object>
    {
        public static final Object NOT_MODIFIED;
        private final Object source;
        
        GetAndSetContext(final Object source) {
            this.source = source;
        }
        
        @Override
        public final Object run() {
            final Thread current = Thread.currentThread();
            final ClassLoader ccl = current.getContextClassLoader();
            ClassLoader loader;
            if (this.source == null) {
                loader = null;
            }
            else if (this.source instanceof ClassLoader) {
                loader = (ClassLoader)this.source;
            }
            else if (this.source instanceof Class) {
                loader = ((Class)this.source).getClassLoader();
            }
            else if (this.source instanceof Thread) {
                loader = ((Thread)this.source).getContextClassLoader();
            }
            else {
                assert !(this.source instanceof Class) : this.source;
                loader = this.source.getClass().getClassLoader();
            }
            if (ccl != loader) {
                current.setContextClassLoader(loader);
                return ccl;
            }
            return GetAndSetContext.NOT_MODIFIED;
        }
        
        static {
            NOT_MODIFIED = GetAndSetContext.class;
        }
    }
    
    private static final class TailNameFormatter extends Formatter
    {
        private final String name;
        
        static Formatter of(final String name) {
            return new TailNameFormatter(name);
        }
        
        private TailNameFormatter(final String name) {
            assert name != null;
            this.name = name;
        }
        
        @Override
        public final String format(final LogRecord record) {
            return "";
        }
        
        @Override
        public final String getTail(final Handler h) {
            return this.name;
        }
        
        @Override
        public final boolean equals(final Object o) {
            return o instanceof TailNameFormatter && this.name.equals(((TailNameFormatter)o).name);
        }
        
        @Override
        public final int hashCode() {
            return this.getClass().hashCode() + this.name.hashCode();
        }
        
        @Override
        public final String toString() {
            return this.name;
        }
    }
}
