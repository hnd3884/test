package com.sun.mail.imap.protocol;

import com.sun.mail.iap.LiteralException;
import com.sun.mail.imap.Rights;
import com.sun.mail.imap.ACL;
import com.sun.mail.iap.ParsingException;
import javax.mail.Quota;
import com.sun.mail.imap.SortTerm;
import com.sun.mail.iap.CommandFailedException;
import javax.mail.search.SearchException;
import javax.mail.search.SearchTerm;
import com.sun.mail.imap.CopyUID;
import com.sun.mail.imap.AppendUID;
import com.sun.mail.iap.Literal;
import java.util.Date;
import javax.mail.Flags;
import java.util.HashSet;
import com.sun.mail.imap.Utility;
import com.sun.mail.imap.ResyncData;
import com.sun.mail.iap.BadCommandException;
import java.lang.reflect.Constructor;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.auth.Ntlm;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import com.sun.mail.util.BASE64EncoderStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import com.sun.mail.iap.ConnectionException;
import java.util.logging.Level;
import java.util.Locale;
import java.util.ArrayList;
import com.sun.mail.iap.Response;
import com.sun.mail.iap.Argument;
import java.util.HashMap;
import java.io.PrintStream;
import java.io.InputStream;
import com.sun.mail.iap.ProtocolException;
import java.io.IOException;
import javax.mail.internet.MimeUtility;
import com.sun.mail.util.PropUtil;
import com.sun.mail.util.MailLogger;
import java.util.Properties;
import com.sun.mail.iap.ByteArray;
import java.util.Set;
import java.util.List;
import java.util.Map;
import com.sun.mail.iap.Protocol;

public class IMAPProtocol extends Protocol
{
    private boolean connected;
    private boolean rev1;
    private boolean referralException;
    private boolean noauthdebug;
    private boolean authenticated;
    private Map<String, String> capabilities;
    private List<String> authmechs;
    private boolean utf8;
    protected SearchSequence searchSequence;
    protected String[] searchCharsets;
    protected Set<String> enabled;
    private String name;
    private SaslAuthenticator saslAuthenticator;
    private String proxyAuthUser;
    private ByteArray ba;
    private static final byte[] CRLF;
    private static final FetchItem[] fetchItems;
    private volatile String idleTag;
    private static final byte[] DONE;
    
    public IMAPProtocol(final String name, final String host, final int port, final Properties props, final boolean isSSL, final MailLogger logger) throws IOException, ProtocolException {
        super(host, port, props, "mail." + name, isSSL, logger);
        this.connected = false;
        this.rev1 = false;
        this.noauthdebug = true;
        try {
            this.name = name;
            this.noauthdebug = !PropUtil.getBooleanProperty(props, "mail.debug.auth", false);
            this.referralException = PropUtil.getBooleanProperty(props, this.prefix + ".referralexception", false);
            if (this.capabilities == null) {
                this.capability();
            }
            if (this.hasCapability("IMAP4rev1")) {
                this.rev1 = true;
            }
            (this.searchCharsets = new String[2])[0] = "UTF-8";
            this.searchCharsets[1] = MimeUtility.mimeCharset(MimeUtility.getDefaultJavaCharset());
            this.connected = true;
        }
        finally {
            if (!this.connected) {
                this.disconnect();
            }
        }
    }
    
    public IMAPProtocol(final InputStream in, final PrintStream out, final Properties props, final boolean debug) throws IOException {
        super(in, out, props, debug);
        this.connected = false;
        this.rev1 = false;
        this.noauthdebug = true;
        this.name = "imap";
        this.noauthdebug = !PropUtil.getBooleanProperty(props, "mail.debug.auth", false);
        if (this.capabilities == null) {
            this.capabilities = new HashMap<String, String>();
        }
        (this.searchCharsets = new String[2])[0] = "UTF-8";
        this.searchCharsets[1] = MimeUtility.mimeCharset(MimeUtility.getDefaultJavaCharset());
        this.connected = true;
    }
    
    public FetchItem[] getFetchItems() {
        return IMAPProtocol.fetchItems;
    }
    
    public void capability() throws ProtocolException {
        final Response[] r = this.command("CAPABILITY", null);
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            this.handleCapabilityResponse(r);
        }
        this.handleResult(response);
    }
    
    public void handleCapabilityResponse(final Response[] r) {
        boolean first = true;
        for (int i = 0, len = r.length; i < len; ++i) {
            if (r[i] instanceof IMAPResponse) {
                final IMAPResponse ir = (IMAPResponse)r[i];
                if (ir.keyEquals("CAPABILITY")) {
                    if (first) {
                        this.capabilities = new HashMap<String, String>(10);
                        this.authmechs = new ArrayList<String>(5);
                        first = false;
                    }
                    this.parseCapabilities(ir);
                }
            }
        }
    }
    
    protected void setCapabilities(final Response r) {
        byte b;
        while ((b = r.readByte()) > 0 && b != 91) {}
        if (b == 0) {
            return;
        }
        final String s = r.readAtom();
        if (!s.equalsIgnoreCase("CAPABILITY")) {
            return;
        }
        this.capabilities = new HashMap<String, String>(10);
        this.authmechs = new ArrayList<String>(5);
        this.parseCapabilities(r);
    }
    
    protected void parseCapabilities(final Response r) {
        String s;
        while ((s = r.readAtom()) != null) {
            if (s.length() == 0) {
                if (r.peekByte() == 93) {
                    break;
                }
                r.skipToken();
            }
            else {
                this.capabilities.put(s.toUpperCase(Locale.ENGLISH), s);
                if (!s.regionMatches(true, 0, "AUTH=", 0, 5)) {
                    continue;
                }
                this.authmechs.add(s.substring(5));
                if (!this.logger.isLoggable(Level.FINE)) {
                    continue;
                }
                this.logger.fine("AUTH: " + s.substring(5));
            }
        }
    }
    
    @Override
    protected void processGreeting(final Response r) throws ProtocolException {
        if (r.isBYE()) {
            this.checkReferral(r);
            throw new ConnectionException(this, r);
        }
        if (r.isOK()) {
            this.referralException = PropUtil.getBooleanProperty(this.props, this.prefix + ".referralexception", false);
            if (this.referralException) {
                this.checkReferral(r);
            }
            this.setCapabilities(r);
            return;
        }
        assert r instanceof IMAPResponse;
        final IMAPResponse ir = (IMAPResponse)r;
        if (ir.keyEquals("PREAUTH")) {
            this.authenticated = true;
            this.setCapabilities(r);
            return;
        }
        this.disconnect();
        throw new ConnectionException(this, r);
    }
    
    private void checkReferral(final Response r) throws IMAPReferralException {
        final String s = r.getRest();
        if (s.startsWith("[")) {
            final int i = s.indexOf(32);
            if (i > 0 && s.substring(1, i).equalsIgnoreCase("REFERRAL")) {
                final int j = s.indexOf(93);
                String url;
                String msg;
                if (j > 0) {
                    url = s.substring(i + 1, j);
                    msg = s.substring(j + 1).trim();
                }
                else {
                    url = s.substring(i + 1);
                    msg = "";
                }
                if (r.isBYE()) {
                    this.disconnect();
                }
                throw new IMAPReferralException(msg, url);
            }
        }
    }
    
    public boolean isAuthenticated() {
        return this.authenticated;
    }
    
    public boolean isREV1() {
        return this.rev1;
    }
    
    @Override
    protected boolean supportsNonSyncLiterals() {
        return this.hasCapability("LITERAL+");
    }
    
    @Override
    public Response readResponse() throws IOException, ProtocolException {
        IMAPResponse r = new IMAPResponse(this);
        if (r.keyEquals("FETCH")) {
            r = new FetchResponse(r, this.getFetchItems());
        }
        return r;
    }
    
    public boolean hasCapability(String c) {
        if (c.endsWith("*")) {
            c = c.substring(0, c.length() - 1).toUpperCase(Locale.ENGLISH);
            final Iterator<String> it = this.capabilities.keySet().iterator();
            while (it.hasNext()) {
                if (it.next().startsWith(c)) {
                    return true;
                }
            }
            return false;
        }
        return this.capabilities.containsKey(c.toUpperCase(Locale.ENGLISH));
    }
    
    public Map<String, String> getCapabilities() {
        return this.capabilities;
    }
    
    @Override
    public boolean supportsUtf8() {
        return this.utf8;
    }
    
    public void disconnect() {
        super.disconnect();
        this.authenticated = false;
    }
    
    public void noop() throws ProtocolException {
        this.logger.fine("IMAPProtocol noop");
        this.simpleCommand("NOOP", null);
    }
    
    public void logout() throws ProtocolException {
        try {
            final Response[] r = this.command("LOGOUT", null);
            this.authenticated = false;
            this.notifyResponseHandlers(r);
        }
        finally {
            this.disconnect();
        }
    }
    
    public void login(final String u, final String p) throws ProtocolException {
        final Argument args = new Argument();
        args.writeString(u);
        args.writeString(p);
        Response[] r = null;
        try {
            if (this.noauthdebug && this.isTracing()) {
                this.logger.fine("LOGIN command trace suppressed");
                this.suspendTracing();
            }
            r = this.command("LOGIN", args);
        }
        finally {
            this.resumeTracing();
        }
        this.handleCapabilityResponse(r);
        this.notifyResponseHandlers(r);
        if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("LOGIN command result: " + r[r.length - 1]);
        }
        this.handleLoginResult(r[r.length - 1]);
        this.setCapabilities(r[r.length - 1]);
        this.authenticated = true;
    }
    
    public synchronized void authlogin(final String u, final String p) throws ProtocolException {
        final List<Response> v = new ArrayList<Response>();
        String tag = null;
        Response r = null;
        boolean done = false;
        try {
            if (this.noauthdebug && this.isTracing()) {
                this.logger.fine("AUTHENTICATE LOGIN command trace suppressed");
                this.suspendTracing();
            }
            try {
                tag = this.writeCommand("AUTHENTICATE LOGIN", null);
            }
            catch (final Exception ex) {
                r = Response.byeResponse(ex);
                done = true;
            }
            final OutputStream os = this.getOutputStream();
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final OutputStream b64os = new BASE64EncoderStream(bos, Integer.MAX_VALUE);
            boolean first = true;
            while (!done) {
                try {
                    r = this.readResponse();
                    if (r.isContinuation()) {
                        String s;
                        if (first) {
                            s = u;
                            first = false;
                        }
                        else {
                            s = p;
                        }
                        b64os.write(s.getBytes(StandardCharsets.UTF_8));
                        b64os.flush();
                        bos.write(IMAPProtocol.CRLF);
                        os.write(bos.toByteArray());
                        os.flush();
                        bos.reset();
                    }
                    else if (r.isTagged() && r.getTag().equals(tag)) {
                        done = true;
                    }
                    else if (r.isBYE()) {
                        done = true;
                    }
                }
                catch (final Exception ioex) {
                    r = Response.byeResponse(ioex);
                    done = true;
                }
                v.add(r);
            }
        }
        finally {
            this.resumeTracing();
        }
        final Response[] responses = v.toArray(new Response[v.size()]);
        this.handleCapabilityResponse(responses);
        this.notifyResponseHandlers(responses);
        if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("AUTHENTICATE LOGIN command result: " + r);
        }
        this.handleLoginResult(r);
        this.setCapabilities(r);
        this.authenticated = true;
    }
    
    public synchronized void authplain(final String authzid, final String u, final String p) throws ProtocolException {
        final List<Response> v = new ArrayList<Response>();
        String tag = null;
        Response r = null;
        boolean done = false;
        try {
            if (this.noauthdebug && this.isTracing()) {
                this.logger.fine("AUTHENTICATE PLAIN command trace suppressed");
                this.suspendTracing();
            }
            try {
                tag = this.writeCommand("AUTHENTICATE PLAIN", null);
            }
            catch (final Exception ex) {
                r = Response.byeResponse(ex);
                done = true;
            }
            final OutputStream os = this.getOutputStream();
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final OutputStream b64os = new BASE64EncoderStream(bos, Integer.MAX_VALUE);
            while (!done) {
                try {
                    r = this.readResponse();
                    if (r.isContinuation()) {
                        final String nullByte = "\u0000";
                        final String s = ((authzid == null) ? "" : authzid) + "\u0000" + u + "\u0000" + p;
                        b64os.write(s.getBytes(StandardCharsets.UTF_8));
                        b64os.flush();
                        bos.write(IMAPProtocol.CRLF);
                        os.write(bos.toByteArray());
                        os.flush();
                        bos.reset();
                    }
                    else if (r.isTagged() && r.getTag().equals(tag)) {
                        done = true;
                    }
                    else if (r.isBYE()) {
                        done = true;
                    }
                }
                catch (final Exception ioex) {
                    r = Response.byeResponse(ioex);
                    done = true;
                }
                v.add(r);
            }
        }
        finally {
            this.resumeTracing();
        }
        final Response[] responses = v.toArray(new Response[v.size()]);
        this.handleCapabilityResponse(responses);
        this.notifyResponseHandlers(responses);
        if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("AUTHENTICATE PLAIN command result: " + r);
        }
        this.handleLoginResult(r);
        this.setCapabilities(r);
        this.authenticated = true;
    }
    
    public synchronized void authntlm(final String authzid, final String u, final String p) throws ProtocolException {
        final List<Response> v = new ArrayList<Response>();
        String tag = null;
        Response r = null;
        boolean done = false;
        final String type1Msg = null;
        final int flags = PropUtil.getIntProperty(this.props, "mail." + this.name + ".auth.ntlm.flags", 0);
        final String domain = this.props.getProperty("mail." + this.name + ".auth.ntlm.domain", "");
        final Ntlm ntlm = new Ntlm(domain, this.getLocalHost(), u, p, this.logger);
        try {
            if (this.noauthdebug && this.isTracing()) {
                this.logger.fine("AUTHENTICATE NTLM command trace suppressed");
                this.suspendTracing();
            }
            try {
                tag = this.writeCommand("AUTHENTICATE NTLM", null);
            }
            catch (final Exception ex) {
                r = Response.byeResponse(ex);
                done = true;
            }
            final OutputStream os = this.getOutputStream();
            boolean first = true;
            while (!done) {
                try {
                    r = this.readResponse();
                    if (r.isContinuation()) {
                        String s;
                        if (first) {
                            s = ntlm.generateType1Msg(flags);
                            first = false;
                        }
                        else {
                            s = ntlm.generateType3Msg(r.getRest());
                        }
                        os.write(s.getBytes(StandardCharsets.UTF_8));
                        os.write(IMAPProtocol.CRLF);
                        os.flush();
                    }
                    else if (r.isTagged() && r.getTag().equals(tag)) {
                        done = true;
                    }
                    else if (r.isBYE()) {
                        done = true;
                    }
                }
                catch (final Exception ioex) {
                    r = Response.byeResponse(ioex);
                    done = true;
                }
                v.add(r);
            }
        }
        finally {
            this.resumeTracing();
        }
        final Response[] responses = v.toArray(new Response[v.size()]);
        this.handleCapabilityResponse(responses);
        this.notifyResponseHandlers(responses);
        if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("AUTHENTICATE NTLM command result: " + r);
        }
        this.handleLoginResult(r);
        this.setCapabilities(r);
        this.authenticated = true;
    }
    
    public synchronized void authoauth2(final String u, final String p) throws ProtocolException {
        final List<Response> v = new ArrayList<Response>();
        String tag = null;
        Response r = null;
        boolean done = false;
        try {
            if (this.noauthdebug && this.isTracing()) {
                this.logger.fine("AUTHENTICATE XOAUTH2 command trace suppressed");
                this.suspendTracing();
            }
            try {
                final Argument args = new Argument();
                args.writeAtom("XOAUTH2");
                if (this.hasCapability("SASL-IR")) {
                    final String resp = "user=" + u + "\u0001auth=Bearer " + p + "\u0001\u0001";
                    final byte[] ba = BASE64EncoderStream.encode(resp.getBytes(StandardCharsets.UTF_8));
                    final String irs = ASCIIUtility.toString(ba, 0, ba.length);
                    args.writeAtom(irs);
                }
                tag = this.writeCommand("AUTHENTICATE", args);
            }
            catch (final Exception ex) {
                r = Response.byeResponse(ex);
                done = true;
            }
            final OutputStream os = this.getOutputStream();
            while (!done) {
                try {
                    r = this.readResponse();
                    if (r.isContinuation()) {
                        final String resp = "user=" + u + "\u0001auth=Bearer " + p + "\u0001\u0001";
                        final byte[] b = BASE64EncoderStream.encode(resp.getBytes(StandardCharsets.UTF_8));
                        os.write(b);
                        os.write(IMAPProtocol.CRLF);
                        os.flush();
                    }
                    else if (r.isTagged() && r.getTag().equals(tag)) {
                        done = true;
                    }
                    else if (r.isBYE()) {
                        done = true;
                    }
                }
                catch (final Exception ioex) {
                    r = Response.byeResponse(ioex);
                    done = true;
                }
                v.add(r);
            }
        }
        finally {
            this.resumeTracing();
        }
        final Response[] responses = v.toArray(new Response[v.size()]);
        this.handleCapabilityResponse(responses);
        this.notifyResponseHandlers(responses);
        if (this.noauthdebug && this.isTracing()) {
            this.logger.fine("AUTHENTICATE XOAUTH2 command result: " + r);
        }
        this.handleLoginResult(r);
        this.setCapabilities(r);
        this.authenticated = true;
    }
    
    public void sasllogin(final String[] allowed, final String realm, final String authzid, final String u, final String p) throws ProtocolException {
        final boolean useCanonicalHostName = PropUtil.getBooleanProperty(this.props, "mail." + this.name + ".sasl.usecanonicalhostname", false);
        String serviceHost;
        if (useCanonicalHostName) {
            serviceHost = this.getInetAddress().getCanonicalHostName();
        }
        else {
            serviceHost = this.host;
        }
        if (this.saslAuthenticator == null) {
            try {
                final Class<?> sac = Class.forName("com.sun.mail.imap.protocol.IMAPSaslAuthenticator");
                final Constructor<?> c = sac.getConstructor(IMAPProtocol.class, String.class, Properties.class, MailLogger.class, String.class);
                this.saslAuthenticator = (SaslAuthenticator)c.newInstance(this, this.name, this.props, this.logger, serviceHost);
            }
            catch (final Exception ex) {
                this.logger.log(Level.FINE, "Can't load SASL authenticator", ex);
                return;
            }
        }
        List<String> v;
        if (allowed != null && allowed.length > 0) {
            v = new ArrayList<String>(allowed.length);
            for (int i = 0; i < allowed.length; ++i) {
                if (this.authmechs.contains(allowed[i])) {
                    v.add(allowed[i]);
                }
            }
        }
        else {
            v = this.authmechs;
        }
        final String[] mechs = v.toArray(new String[v.size()]);
        try {
            if (this.noauthdebug && this.isTracing()) {
                this.logger.fine("SASL authentication command trace suppressed");
                this.suspendTracing();
            }
            if (this.saslAuthenticator.authenticate(mechs, realm, authzid, u, p)) {
                if (this.noauthdebug && this.isTracing()) {
                    this.logger.fine("SASL authentication succeeded");
                }
                this.authenticated = true;
            }
            else if (this.noauthdebug && this.isTracing()) {
                this.logger.fine("SASL authentication failed");
            }
        }
        finally {
            this.resumeTracing();
        }
    }
    
    OutputStream getIMAPOutputStream() {
        return this.getOutputStream();
    }
    
    protected void handleLoginResult(final Response r) throws ProtocolException {
        if (this.hasCapability("LOGIN-REFERRALS") && (!r.isOK() || this.referralException)) {
            this.checkReferral(r);
        }
        this.handleResult(r);
    }
    
    public void proxyauth(final String u) throws ProtocolException {
        final Argument args = new Argument();
        args.writeString(u);
        this.simpleCommand("PROXYAUTH", args);
        this.proxyAuthUser = u;
    }
    
    public String getProxyAuthUser() {
        return this.proxyAuthUser;
    }
    
    public void unauthenticate() throws ProtocolException {
        if (!this.hasCapability("X-UNAUTHENTICATE")) {
            throw new BadCommandException("UNAUTHENTICATE not supported");
        }
        this.simpleCommand("UNAUTHENTICATE", null);
        this.authenticated = false;
    }
    
    @Deprecated
    public void id(final String guid) throws ProtocolException {
        final Map<String, String> gmap = new HashMap<String, String>();
        gmap.put("GUID", guid);
        this.id(gmap);
    }
    
    public void startTLS() throws ProtocolException {
        try {
            super.startTLS("STARTTLS");
        }
        catch (final ProtocolException pex) {
            this.logger.log(Level.FINE, "STARTTLS ProtocolException", pex);
            throw pex;
        }
        catch (final Exception ex) {
            this.logger.log(Level.FINE, "STARTTLS Exception", ex);
            final Response[] r = { Response.byeResponse(ex) };
            this.notifyResponseHandlers(r);
            this.disconnect();
            throw new ProtocolException("STARTTLS failure", ex);
        }
    }
    
    public void compress() throws ProtocolException {
        try {
            super.startCompression("COMPRESS DEFLATE");
        }
        catch (final ProtocolException pex) {
            this.logger.log(Level.FINE, "COMPRESS ProtocolException", pex);
            throw pex;
        }
        catch (final Exception ex) {
            this.logger.log(Level.FINE, "COMPRESS Exception", ex);
            final Response[] r = { Response.byeResponse(ex) };
            this.notifyResponseHandlers(r);
            this.disconnect();
            throw new ProtocolException("COMPRESS failure", ex);
        }
    }
    
    protected void writeMailboxName(final Argument args, final String name) {
        if (this.utf8) {
            args.writeString(name, StandardCharsets.UTF_8);
        }
        else {
            args.writeString(BASE64MailboxEncoder.encode(name));
        }
    }
    
    public MailboxInfo select(final String mbox) throws ProtocolException {
        return this.select(mbox, null);
    }
    
    public MailboxInfo select(final String mbox, final ResyncData rd) throws ProtocolException {
        final Argument args = new Argument();
        this.writeMailboxName(args, mbox);
        if (rd != null) {
            if (rd == ResyncData.CONDSTORE) {
                if (!this.hasCapability("CONDSTORE")) {
                    throw new BadCommandException("CONDSTORE not supported");
                }
                args.writeArgument(new Argument().writeAtom("CONDSTORE"));
            }
            else {
                if (!this.hasCapability("QRESYNC")) {
                    throw new BadCommandException("QRESYNC not supported");
                }
                args.writeArgument(resyncArgs(rd));
            }
        }
        final Response[] r = this.command("SELECT", args);
        final MailboxInfo minfo = new MailboxInfo(r);
        this.notifyResponseHandlers(r);
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            if (response.toString().indexOf("READ-ONLY") != -1) {
                minfo.mode = 1;
            }
            else {
                minfo.mode = 2;
            }
        }
        this.handleResult(response);
        return minfo;
    }
    
    public MailboxInfo examine(final String mbox) throws ProtocolException {
        return this.examine(mbox, null);
    }
    
    public MailboxInfo examine(final String mbox, final ResyncData rd) throws ProtocolException {
        final Argument args = new Argument();
        this.writeMailboxName(args, mbox);
        if (rd != null) {
            if (rd == ResyncData.CONDSTORE) {
                if (!this.hasCapability("CONDSTORE")) {
                    throw new BadCommandException("CONDSTORE not supported");
                }
                args.writeArgument(new Argument().writeAtom("CONDSTORE"));
            }
            else {
                if (!this.hasCapability("QRESYNC")) {
                    throw new BadCommandException("QRESYNC not supported");
                }
                args.writeArgument(resyncArgs(rd));
            }
        }
        final Response[] r = this.command("EXAMINE", args);
        final MailboxInfo minfo = new MailboxInfo(r);
        minfo.mode = 1;
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
        return minfo;
    }
    
    private static Argument resyncArgs(final ResyncData rd) {
        final Argument cmd = new Argument();
        cmd.writeAtom("QRESYNC");
        final Argument args = new Argument();
        args.writeNumber(rd.getUIDValidity());
        args.writeNumber(rd.getModSeq());
        final UIDSet[] uids = Utility.getResyncUIDSet(rd);
        if (uids != null) {
            args.writeString(UIDSet.toString(uids));
        }
        cmd.writeArgument(args);
        return cmd;
    }
    
    public void enable(final String cap) throws ProtocolException {
        if (!this.hasCapability("ENABLE")) {
            throw new BadCommandException("ENABLE not supported");
        }
        final Argument args = new Argument();
        args.writeAtom(cap);
        this.simpleCommand("ENABLE", args);
        if (this.enabled == null) {
            this.enabled = new HashSet<String>();
        }
        this.enabled.add(cap.toUpperCase(Locale.ENGLISH));
        this.utf8 = this.isEnabled("UTF8=ACCEPT");
    }
    
    public boolean isEnabled(final String cap) {
        return this.enabled != null && this.enabled.contains(cap.toUpperCase(Locale.ENGLISH));
    }
    
    public void unselect() throws ProtocolException {
        if (!this.hasCapability("UNSELECT")) {
            throw new BadCommandException("UNSELECT not supported");
        }
        this.simpleCommand("UNSELECT", null);
    }
    
    public Status status(final String mbox, String[] items) throws ProtocolException {
        if (!this.isREV1() && !this.hasCapability("IMAP4SUNVERSION")) {
            throw new BadCommandException("STATUS not supported");
        }
        final Argument args = new Argument();
        this.writeMailboxName(args, mbox);
        final Argument itemArgs = new Argument();
        if (items == null) {
            items = Status.standardItems;
        }
        for (int i = 0, len = items.length; i < len; ++i) {
            itemArgs.writeAtom(items[i]);
        }
        args.writeArgument(itemArgs);
        final Response[] r = this.command("STATUS", args);
        Status status = null;
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            for (int j = 0, len2 = r.length; j < len2; ++j) {
                if (r[j] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[j];
                    if (ir.keyEquals("STATUS")) {
                        if (status == null) {
                            status = new Status(ir);
                        }
                        else {
                            Status.add(status, new Status(ir));
                        }
                        r[j] = null;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return status;
    }
    
    public void create(final String mbox) throws ProtocolException {
        final Argument args = new Argument();
        this.writeMailboxName(args, mbox);
        this.simpleCommand("CREATE", args);
    }
    
    public void delete(final String mbox) throws ProtocolException {
        final Argument args = new Argument();
        this.writeMailboxName(args, mbox);
        this.simpleCommand("DELETE", args);
    }
    
    public void rename(final String o, final String n) throws ProtocolException {
        final Argument args = new Argument();
        this.writeMailboxName(args, o);
        this.writeMailboxName(args, n);
        this.simpleCommand("RENAME", args);
    }
    
    public void subscribe(final String mbox) throws ProtocolException {
        final Argument args = new Argument();
        this.writeMailboxName(args, mbox);
        this.simpleCommand("SUBSCRIBE", args);
    }
    
    public void unsubscribe(final String mbox) throws ProtocolException {
        final Argument args = new Argument();
        this.writeMailboxName(args, mbox);
        this.simpleCommand("UNSUBSCRIBE", args);
    }
    
    public ListInfo[] list(final String ref, final String pattern) throws ProtocolException {
        return this.doList("LIST", ref, pattern);
    }
    
    public ListInfo[] lsub(final String ref, final String pattern) throws ProtocolException {
        return this.doList("LSUB", ref, pattern);
    }
    
    protected ListInfo[] doList(final String cmd, final String ref, final String pat) throws ProtocolException {
        final Argument args = new Argument();
        this.writeMailboxName(args, ref);
        this.writeMailboxName(args, pat);
        final Response[] r = this.command(cmd, args);
        ListInfo[] linfo = null;
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            final List<ListInfo> v = new ArrayList<ListInfo>(1);
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals(cmd)) {
                        v.add(new ListInfo(ir));
                        r[i] = null;
                    }
                }
            }
            if (v.size() > 0) {
                linfo = v.toArray(new ListInfo[v.size()]);
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return linfo;
    }
    
    public void append(final String mbox, final Flags f, final Date d, final Literal data) throws ProtocolException {
        this.appenduid(mbox, f, d, data, false);
    }
    
    public AppendUID appenduid(final String mbox, final Flags f, final Date d, final Literal data) throws ProtocolException {
        return this.appenduid(mbox, f, d, data, true);
    }
    
    public AppendUID appenduid(final String mbox, Flags f, final Date d, final Literal data, final boolean uid) throws ProtocolException {
        final Argument args = new Argument();
        this.writeMailboxName(args, mbox);
        if (f != null) {
            if (f.contains(Flags.Flag.RECENT)) {
                f = new Flags(f);
                f.remove(Flags.Flag.RECENT);
            }
            args.writeAtom(this.createFlagList(f));
        }
        if (d != null) {
            args.writeString(INTERNALDATE.format(d));
        }
        args.writeBytes(data);
        final Response[] r = this.command("APPEND", args);
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
        if (uid) {
            return this.getAppendUID(r[r.length - 1]);
        }
        return null;
    }
    
    private AppendUID getAppendUID(final Response r) {
        if (!r.isOK()) {
            return null;
        }
        byte b;
        while ((b = r.readByte()) > 0 && b != 91) {}
        if (b == 0) {
            return null;
        }
        final String s = r.readAtom();
        if (!s.equalsIgnoreCase("APPENDUID")) {
            return null;
        }
        final long uidvalidity = r.readLong();
        final long uid = r.readLong();
        return new AppendUID(uidvalidity, uid);
    }
    
    public void check() throws ProtocolException {
        this.simpleCommand("CHECK", null);
    }
    
    public void close() throws ProtocolException {
        this.simpleCommand("CLOSE", null);
    }
    
    public void expunge() throws ProtocolException {
        this.simpleCommand("EXPUNGE", null);
    }
    
    public void uidexpunge(final UIDSet[] set) throws ProtocolException {
        if (!this.hasCapability("UIDPLUS")) {
            throw new BadCommandException("UID EXPUNGE not supported");
        }
        this.simpleCommand("UID EXPUNGE " + UIDSet.toString(set), null);
    }
    
    public BODYSTRUCTURE fetchBodyStructure(final int msgno) throws ProtocolException {
        final Response[] r = this.fetch(msgno, "BODYSTRUCTURE");
        this.notifyResponseHandlers(r);
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            return FetchResponse.getItem(r, msgno, BODYSTRUCTURE.class);
        }
        if (response.isNO()) {
            return null;
        }
        this.handleResult(response);
        return null;
    }
    
    public BODY peekBody(final int msgno, final String section) throws ProtocolException {
        return this.fetchBody(msgno, section, true);
    }
    
    public BODY fetchBody(final int msgno, final String section) throws ProtocolException {
        return this.fetchBody(msgno, section, false);
    }
    
    protected BODY fetchBody(final int msgno, String section, final boolean peek) throws ProtocolException {
        if (section == null) {
            section = "";
        }
        final String body = (peek ? "BODY.PEEK[" : "BODY[") + section + "]";
        return this.fetchSectionBody(msgno, section, body);
    }
    
    public BODY peekBody(final int msgno, final String section, final int start, final int size) throws ProtocolException {
        return this.fetchBody(msgno, section, start, size, true, null);
    }
    
    public BODY fetchBody(final int msgno, final String section, final int start, final int size) throws ProtocolException {
        return this.fetchBody(msgno, section, start, size, false, null);
    }
    
    public BODY peekBody(final int msgno, final String section, final int start, final int size, final ByteArray ba) throws ProtocolException {
        return this.fetchBody(msgno, section, start, size, true, ba);
    }
    
    public BODY fetchBody(final int msgno, final String section, final int start, final int size, final ByteArray ba) throws ProtocolException {
        return this.fetchBody(msgno, section, start, size, false, ba);
    }
    
    protected BODY fetchBody(final int msgno, String section, final int start, final int size, final boolean peek, final ByteArray ba) throws ProtocolException {
        this.ba = ba;
        if (section == null) {
            section = "";
        }
        final String body = (peek ? "BODY.PEEK[" : "BODY[") + section + "]<" + String.valueOf(start) + "." + String.valueOf(size) + ">";
        return this.fetchSectionBody(msgno, section, body);
    }
    
    protected BODY fetchSectionBody(final int msgno, final String section, final String body) throws ProtocolException {
        final Response[] r = this.fetch(msgno, body);
        this.notifyResponseHandlers(r);
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            final List<BODY> bl = FetchResponse.getItems(r, msgno, BODY.class);
            if (bl.size() == 1) {
                return bl.get(0);
            }
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest("got " + bl.size() + " BODY responses for section " + section);
            }
            for (final BODY br : bl) {
                if (this.logger.isLoggable(Level.FINEST)) {
                    this.logger.finest("got BODY section " + br.getSection());
                }
                if (br.getSection().equalsIgnoreCase(section)) {
                    return br;
                }
            }
            return null;
        }
        else {
            if (response.isNO()) {
                return null;
            }
            this.handleResult(response);
            return null;
        }
    }
    
    @Override
    protected ByteArray getResponseBuffer() {
        final ByteArray ret = this.ba;
        this.ba = null;
        return ret;
    }
    
    public RFC822DATA fetchRFC822(final int msgno, final String what) throws ProtocolException {
        final Response[] r = this.fetch(msgno, (what == null) ? "RFC822" : ("RFC822." + what));
        this.notifyResponseHandlers(r);
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            return FetchResponse.getItem(r, msgno, RFC822DATA.class);
        }
        if (response.isNO()) {
            return null;
        }
        this.handleResult(response);
        return null;
    }
    
    public Flags fetchFlags(final int msgno) throws ProtocolException {
        Flags flags = null;
        final Response[] r = this.fetch(msgno, "FLAGS");
        for (int i = 0, len = r.length; i < len; ++i) {
            if (r[i] != null && r[i] instanceof FetchResponse) {
                if (((FetchResponse)r[i]).getNumber() == msgno) {
                    final FetchResponse fr = (FetchResponse)r[i];
                    if ((flags = fr.getItem(FLAGS.class)) != null) {
                        r[i] = null;
                        break;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
        return flags;
    }
    
    public UID fetchUID(final int msgno) throws ProtocolException {
        final Response[] r = this.fetch(msgno, "UID");
        this.notifyResponseHandlers(r);
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            return FetchResponse.getItem(r, msgno, UID.class);
        }
        if (response.isNO()) {
            return null;
        }
        this.handleResult(response);
        return null;
    }
    
    public MODSEQ fetchMODSEQ(final int msgno) throws ProtocolException {
        final Response[] r = this.fetch(msgno, "MODSEQ");
        this.notifyResponseHandlers(r);
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            return FetchResponse.getItem(r, msgno, MODSEQ.class);
        }
        if (response.isNO()) {
            return null;
        }
        this.handleResult(response);
        return null;
    }
    
    public void fetchSequenceNumber(final long uid) throws ProtocolException {
        final Response[] r = this.fetch(String.valueOf(uid), "UID", true);
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
    }
    
    public long[] fetchSequenceNumbers(final long start, final long end) throws ProtocolException {
        final Response[] r = this.fetch(String.valueOf(start) + ":" + ((end == -1L) ? "*" : String.valueOf(end)), "UID", true);
        final List<UID> v = new ArrayList<UID>();
        for (int i = 0, len = r.length; i < len; ++i) {
            if (r[i] != null) {
                if (r[i] instanceof FetchResponse) {
                    final FetchResponse fr = (FetchResponse)r[i];
                    final UID u;
                    if ((u = fr.getItem(UID.class)) != null) {
                        v.add(u);
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
        final long[] lv = new long[v.size()];
        for (int j = 0; j < v.size(); ++j) {
            lv[j] = v.get(j).uid;
        }
        return lv;
    }
    
    public void fetchSequenceNumbers(final long[] uids) throws ProtocolException {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < uids.length; ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(String.valueOf(uids[i]));
        }
        final Response[] r = this.fetch(sb.toString(), "UID", true);
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
    }
    
    public int[] uidfetchChangedSince(final long start, final long end, final long modseq) throws ProtocolException {
        final String msgSequence = String.valueOf(start) + ":" + ((end == -1L) ? "*" : String.valueOf(end));
        final Response[] r = this.command("UID FETCH " + msgSequence + " (FLAGS) (CHANGEDSINCE " + String.valueOf(modseq) + ")", null);
        final List<Integer> v = new ArrayList<Integer>();
        for (int i = 0, len = r.length; i < len; ++i) {
            if (r[i] != null) {
                if (r[i] instanceof FetchResponse) {
                    final FetchResponse fr = (FetchResponse)r[i];
                    v.add(fr.getNumber());
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
        final int vsize = v.size();
        final int[] matches = new int[vsize];
        for (int j = 0; j < vsize; ++j) {
            matches[j] = v.get(j);
        }
        return matches;
    }
    
    public Response[] fetch(final MessageSet[] msgsets, final String what) throws ProtocolException {
        return this.fetch(MessageSet.toString(msgsets), what, false);
    }
    
    public Response[] fetch(final int start, final int end, final String what) throws ProtocolException {
        return this.fetch(String.valueOf(start) + ":" + String.valueOf(end), what, false);
    }
    
    public Response[] fetch(final int msg, final String what) throws ProtocolException {
        return this.fetch(String.valueOf(msg), what, false);
    }
    
    private Response[] fetch(final String msgSequence, final String what, final boolean uid) throws ProtocolException {
        if (uid) {
            return this.command("UID FETCH " + msgSequence + " (" + what + ")", null);
        }
        return this.command("FETCH " + msgSequence + " (" + what + ")", null);
    }
    
    public void copy(final MessageSet[] msgsets, final String mbox) throws ProtocolException {
        this.copyuid(MessageSet.toString(msgsets), mbox, false);
    }
    
    public void copy(final int start, final int end, final String mbox) throws ProtocolException {
        this.copyuid(String.valueOf(start) + ":" + String.valueOf(end), mbox, false);
    }
    
    public CopyUID copyuid(final MessageSet[] msgsets, final String mbox) throws ProtocolException {
        return this.copyuid(MessageSet.toString(msgsets), mbox, true);
    }
    
    public CopyUID copyuid(final int start, final int end, final String mbox) throws ProtocolException {
        return this.copyuid(String.valueOf(start) + ":" + String.valueOf(end), mbox, true);
    }
    
    private CopyUID copyuid(final String msgSequence, final String mbox, final boolean uid) throws ProtocolException {
        if (uid && !this.hasCapability("UIDPLUS")) {
            throw new BadCommandException("UIDPLUS not supported");
        }
        final Argument args = new Argument();
        args.writeAtom(msgSequence);
        this.writeMailboxName(args, mbox);
        final Response[] r = this.command("COPY", args);
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
        if (uid) {
            return this.getCopyUID(r);
        }
        return null;
    }
    
    public void move(final MessageSet[] msgsets, final String mbox) throws ProtocolException {
        this.moveuid(MessageSet.toString(msgsets), mbox, false);
    }
    
    public void move(final int start, final int end, final String mbox) throws ProtocolException {
        this.moveuid(String.valueOf(start) + ":" + String.valueOf(end), mbox, false);
    }
    
    public CopyUID moveuid(final MessageSet[] msgsets, final String mbox) throws ProtocolException {
        return this.moveuid(MessageSet.toString(msgsets), mbox, true);
    }
    
    public CopyUID moveuid(final int start, final int end, final String mbox) throws ProtocolException {
        return this.moveuid(String.valueOf(start) + ":" + String.valueOf(end), mbox, true);
    }
    
    private CopyUID moveuid(final String msgSequence, final String mbox, final boolean uid) throws ProtocolException {
        if (!this.hasCapability("MOVE")) {
            throw new BadCommandException("MOVE not supported");
        }
        if (uid && !this.hasCapability("UIDPLUS")) {
            throw new BadCommandException("UIDPLUS not supported");
        }
        final Argument args = new Argument();
        args.writeAtom(msgSequence);
        this.writeMailboxName(args, mbox);
        final Response[] r = this.command("MOVE", args);
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
        if (uid) {
            return this.getCopyUID(r);
        }
        return null;
    }
    
    protected CopyUID getCopyUID(final Response[] rr) {
        for (int i = rr.length - 1; i >= 0; --i) {
            final Response r = rr[i];
            if (r != null) {
                if (r.isOK()) {
                    byte b;
                    while ((b = r.readByte()) > 0 && b != 91) {}
                    if (b != 0) {
                        final String s = r.readAtom();
                        if (s.equalsIgnoreCase("COPYUID")) {
                            final long uidvalidity = r.readLong();
                            final String src = r.readAtom();
                            final String dst = r.readAtom();
                            return new CopyUID(uidvalidity, UIDSet.parseUIDSets(src), UIDSet.parseUIDSets(dst));
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public void storeFlags(final MessageSet[] msgsets, final Flags flags, final boolean set) throws ProtocolException {
        this.storeFlags(MessageSet.toString(msgsets), flags, set);
    }
    
    public void storeFlags(final int start, final int end, final Flags flags, final boolean set) throws ProtocolException {
        this.storeFlags(String.valueOf(start) + ":" + String.valueOf(end), flags, set);
    }
    
    public void storeFlags(final int msg, final Flags flags, final boolean set) throws ProtocolException {
        this.storeFlags(String.valueOf(msg), flags, set);
    }
    
    private void storeFlags(final String msgset, final Flags flags, final boolean set) throws ProtocolException {
        Response[] r;
        if (set) {
            r = this.command("STORE " + msgset + " +FLAGS " + this.createFlagList(flags), null);
        }
        else {
            r = this.command("STORE " + msgset + " -FLAGS " + this.createFlagList(flags), null);
        }
        this.notifyResponseHandlers(r);
        this.handleResult(r[r.length - 1]);
    }
    
    protected String createFlagList(final Flags flags) {
        final StringBuilder sb = new StringBuilder("(");
        final Flags.Flag[] sf = flags.getSystemFlags();
        boolean first = true;
        for (int i = 0; i < sf.length; ++i) {
            final Flags.Flag f = sf[i];
            String s;
            if (f == Flags.Flag.ANSWERED) {
                s = "\\Answered";
            }
            else if (f == Flags.Flag.DELETED) {
                s = "\\Deleted";
            }
            else if (f == Flags.Flag.DRAFT) {
                s = "\\Draft";
            }
            else if (f == Flags.Flag.FLAGGED) {
                s = "\\Flagged";
            }
            else if (f == Flags.Flag.RECENT) {
                s = "\\Recent";
            }
            else {
                if (f != Flags.Flag.SEEN) {
                    continue;
                }
                s = "\\Seen";
            }
            if (first) {
                first = false;
            }
            else {
                sb.append(' ');
            }
            sb.append(s);
        }
        final String[] uf = flags.getUserFlags();
        for (int j = 0; j < uf.length; ++j) {
            if (first) {
                first = false;
            }
            else {
                sb.append(' ');
            }
            sb.append(uf[j]);
        }
        sb.append(")");
        return sb.toString();
    }
    
    public int[] search(final MessageSet[] msgsets, final SearchTerm term) throws ProtocolException, SearchException {
        return this.search(MessageSet.toString(msgsets), term);
    }
    
    public int[] search(final SearchTerm term) throws ProtocolException, SearchException {
        return this.search("ALL", term);
    }
    
    private int[] search(final String msgSequence, final SearchTerm term) throws ProtocolException, SearchException {
        Label_0023: {
            if (!this.supportsUtf8()) {
                if (!SearchSequence.isAscii(term)) {
                    break Label_0023;
                }
            }
            try {
                return this.issueSearch(msgSequence, term, null);
            }
            catch (final IOException ex) {}
        }
        for (int i = 0; i < this.searchCharsets.length; ++i) {
            if (this.searchCharsets[i] != null) {
                try {
                    return this.issueSearch(msgSequence, term, this.searchCharsets[i]);
                }
                catch (final CommandFailedException cfx) {
                    this.searchCharsets[i] = null;
                }
                catch (final IOException ioex) {}
                catch (final ProtocolException pex) {
                    throw pex;
                }
                catch (final SearchException sex) {
                    throw sex;
                }
            }
        }
        throw new SearchException("Search failed");
    }
    
    private int[] issueSearch(final String msgSequence, final SearchTerm term, final String charset) throws ProtocolException, SearchException, IOException {
        final Argument args = this.getSearchSequence().generateSequence(term, (charset == null) ? null : MimeUtility.javaCharset(charset));
        args.writeAtom(msgSequence);
        Response[] r;
        if (charset == null) {
            r = this.command("SEARCH", args);
        }
        else {
            r = this.command("SEARCH CHARSET " + charset, args);
        }
        final Response response = r[r.length - 1];
        int[] matches = null;
        if (response.isOK()) {
            final List<Integer> v = new ArrayList<Integer>();
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals("SEARCH")) {
                        int num;
                        while ((num = ir.readNumber()) != -1) {
                            v.add(num);
                        }
                        r[i] = null;
                    }
                }
            }
            final int vsize = v.size();
            matches = new int[vsize];
            for (int j = 0; j < vsize; ++j) {
                matches[j] = v.get(j);
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return matches;
    }
    
    protected SearchSequence getSearchSequence() {
        if (this.searchSequence == null) {
            this.searchSequence = new SearchSequence(this);
        }
        return this.searchSequence;
    }
    
    public int[] sort(final SortTerm[] term, final SearchTerm sterm) throws ProtocolException, SearchException {
        if (!this.hasCapability("SORT*")) {
            throw new BadCommandException("SORT not supported");
        }
        if (term == null || term.length == 0) {
            throw new BadCommandException("Must have at least one sort term");
        }
        final Argument args = new Argument();
        final Argument sargs = new Argument();
        for (int i = 0; i < term.length; ++i) {
            sargs.writeAtom(term[i].toString());
        }
        args.writeArgument(sargs);
        args.writeAtom("UTF-8");
        Label_0146: {
            if (sterm != null) {
                try {
                    args.append(this.getSearchSequence().generateSequence(sterm, "UTF-8"));
                    break Label_0146;
                }
                catch (final IOException ioex) {
                    throw new SearchException(ioex.toString());
                }
            }
            args.writeAtom("ALL");
        }
        final Response[] r = this.command("SORT", args);
        final Response response = r[r.length - 1];
        int[] matches = null;
        if (response.isOK()) {
            final List<Integer> v = new ArrayList<Integer>();
            for (int j = 0, len = r.length; j < len; ++j) {
                if (r[j] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[j];
                    if (ir.keyEquals("SORT")) {
                        int num;
                        while ((num = ir.readNumber()) != -1) {
                            v.add(num);
                        }
                        r[j] = null;
                    }
                }
            }
            final int vsize = v.size();
            matches = new int[vsize];
            for (int k = 0; k < vsize; ++k) {
                matches[k] = v.get(k);
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return matches;
    }
    
    public Namespaces namespace() throws ProtocolException {
        if (!this.hasCapability("NAMESPACE")) {
            throw new BadCommandException("NAMESPACE not supported");
        }
        final Response[] r = this.command("NAMESPACE", null);
        Namespaces namespace = null;
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals("NAMESPACE")) {
                        if (namespace == null) {
                            namespace = new Namespaces(ir);
                        }
                        r[i] = null;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return namespace;
    }
    
    public Quota[] getQuotaRoot(final String mbox) throws ProtocolException {
        if (!this.hasCapability("QUOTA")) {
            throw new BadCommandException("GETQUOTAROOT not supported");
        }
        final Argument args = new Argument();
        this.writeMailboxName(args, mbox);
        final Response[] r = this.command("GETQUOTAROOT", args);
        final Response response = r[r.length - 1];
        final Map<String, Quota> tab = new HashMap<String, Quota>();
        if (response.isOK()) {
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals("QUOTAROOT")) {
                        ir.readAtomString();
                        String root = null;
                        while ((root = ir.readAtomString()) != null && root.length() > 0) {
                            tab.put(root, new Quota(root));
                        }
                        r[i] = null;
                    }
                    else if (ir.keyEquals("QUOTA")) {
                        final Quota quota = this.parseQuota(ir);
                        final Quota q = tab.get(quota.quotaRoot);
                        if (q != null && q.resources != null) {
                            final int newl = q.resources.length + quota.resources.length;
                            final Quota.Resource[] newr = new Quota.Resource[newl];
                            System.arraycopy(q.resources, 0, newr, 0, q.resources.length);
                            System.arraycopy(quota.resources, 0, newr, q.resources.length, quota.resources.length);
                            quota.resources = newr;
                        }
                        tab.put(quota.quotaRoot, quota);
                        r[i] = null;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return tab.values().toArray(new Quota[tab.size()]);
    }
    
    public Quota[] getQuota(final String root) throws ProtocolException {
        if (!this.hasCapability("QUOTA")) {
            throw new BadCommandException("QUOTA not supported");
        }
        final Argument args = new Argument();
        args.writeString(root);
        final Response[] r = this.command("GETQUOTA", args);
        Quota quota = null;
        final List<Quota> v = new ArrayList<Quota>();
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals("QUOTA")) {
                        quota = this.parseQuota(ir);
                        v.add(quota);
                        r[i] = null;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return v.toArray(new Quota[v.size()]);
    }
    
    public void setQuota(final Quota quota) throws ProtocolException {
        if (!this.hasCapability("QUOTA")) {
            throw new BadCommandException("QUOTA not supported");
        }
        final Argument args = new Argument();
        args.writeString(quota.quotaRoot);
        final Argument qargs = new Argument();
        if (quota.resources != null) {
            for (int i = 0; i < quota.resources.length; ++i) {
                qargs.writeAtom(quota.resources[i].name);
                qargs.writeNumber(quota.resources[i].limit);
            }
        }
        args.writeArgument(qargs);
        final Response[] r = this.command("SETQUOTA", args);
        final Response response = r[r.length - 1];
        this.notifyResponseHandlers(r);
        this.handleResult(response);
    }
    
    private Quota parseQuota(final Response r) throws ParsingException {
        final String quotaRoot = r.readAtomString();
        final Quota q = new Quota(quotaRoot);
        r.skipSpaces();
        if (r.readByte() != 40) {
            throw new ParsingException("parse error in QUOTA");
        }
        final List<Quota.Resource> v = new ArrayList<Quota.Resource>();
        while (!r.isNextNonSpace(')')) {
            final String name = r.readAtom();
            if (name != null) {
                final long usage = r.readLong();
                final long limit = r.readLong();
                final Quota.Resource res = new Quota.Resource(name, usage, limit);
                v.add(res);
            }
        }
        q.resources = v.toArray(new Quota.Resource[v.size()]);
        return q;
    }
    
    public void setACL(final String mbox, final char modifier, final ACL acl) throws ProtocolException {
        if (!this.hasCapability("ACL")) {
            throw new BadCommandException("ACL not supported");
        }
        final Argument args = new Argument();
        this.writeMailboxName(args, mbox);
        args.writeString(acl.getName());
        String rights = acl.getRights().toString();
        if (modifier == '+' || modifier == '-') {
            rights = modifier + rights;
        }
        args.writeString(rights);
        final Response[] r = this.command("SETACL", args);
        final Response response = r[r.length - 1];
        this.notifyResponseHandlers(r);
        this.handleResult(response);
    }
    
    public void deleteACL(final String mbox, final String user) throws ProtocolException {
        if (!this.hasCapability("ACL")) {
            throw new BadCommandException("ACL not supported");
        }
        final Argument args = new Argument();
        this.writeMailboxName(args, mbox);
        args.writeString(user);
        final Response[] r = this.command("DELETEACL", args);
        final Response response = r[r.length - 1];
        this.notifyResponseHandlers(r);
        this.handleResult(response);
    }
    
    public ACL[] getACL(final String mbox) throws ProtocolException {
        if (!this.hasCapability("ACL")) {
            throw new BadCommandException("ACL not supported");
        }
        final Argument args = new Argument();
        this.writeMailboxName(args, mbox);
        final Response[] r = this.command("GETACL", args);
        final Response response = r[r.length - 1];
        final List<ACL> v = new ArrayList<ACL>();
        if (response.isOK()) {
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals("ACL")) {
                        ir.readAtomString();
                        String name = null;
                        while ((name = ir.readAtomString()) != null) {
                            final String rights = ir.readAtomString();
                            if (rights == null) {
                                break;
                            }
                            final ACL acl = new ACL(name, new Rights(rights));
                            v.add(acl);
                        }
                        r[i] = null;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return v.toArray(new ACL[v.size()]);
    }
    
    public Rights[] listRights(final String mbox, final String user) throws ProtocolException {
        if (!this.hasCapability("ACL")) {
            throw new BadCommandException("ACL not supported");
        }
        final Argument args = new Argument();
        this.writeMailboxName(args, mbox);
        args.writeString(user);
        final Response[] r = this.command("LISTRIGHTS", args);
        final Response response = r[r.length - 1];
        final List<Rights> v = new ArrayList<Rights>();
        if (response.isOK()) {
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals("LISTRIGHTS")) {
                        ir.readAtomString();
                        ir.readAtomString();
                        String rights;
                        while ((rights = ir.readAtomString()) != null) {
                            v.add(new Rights(rights));
                        }
                        r[i] = null;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return v.toArray(new Rights[v.size()]);
    }
    
    public Rights myRights(final String mbox) throws ProtocolException {
        if (!this.hasCapability("ACL")) {
            throw new BadCommandException("ACL not supported");
        }
        final Argument args = new Argument();
        this.writeMailboxName(args, mbox);
        final Response[] r = this.command("MYRIGHTS", args);
        final Response response = r[r.length - 1];
        Rights rights = null;
        if (response.isOK()) {
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals("MYRIGHTS")) {
                        ir.readAtomString();
                        final String rs = ir.readAtomString();
                        if (rights == null) {
                            rights = new Rights(rs);
                        }
                        r[i] = null;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return rights;
    }
    
    public synchronized void idleStart() throws ProtocolException {
        if (!this.hasCapability("IDLE")) {
            throw new BadCommandException("IDLE not supported");
        }
        final List<Response> v = new ArrayList<Response>();
        boolean done = false;
        Response r = null;
        try {
            this.idleTag = this.writeCommand("IDLE", null);
        }
        catch (final LiteralException lex) {
            v.add(lex.getResponse());
            done = true;
        }
        catch (final Exception ex) {
            v.add(Response.byeResponse(ex));
            done = true;
        }
        while (!done) {
            try {
                r = this.readResponse();
            }
            catch (final IOException ioex) {
                r = Response.byeResponse(ioex);
            }
            catch (final ProtocolException pex) {
                continue;
            }
            v.add(r);
            if (r.isContinuation() || r.isBYE()) {
                done = true;
            }
        }
        final Response[] responses = v.toArray(new Response[v.size()]);
        r = responses[responses.length - 1];
        this.notifyResponseHandlers(responses);
        if (!r.isContinuation()) {
            this.handleResult(r);
        }
    }
    
    public synchronized Response readIdleResponse() {
        if (this.idleTag == null) {
            return null;
        }
        Response r = null;
        try {
            r = this.readResponse();
        }
        catch (final IOException ioex) {
            r = Response.byeResponse(ioex);
        }
        catch (final ProtocolException pex) {
            r = Response.byeResponse(pex);
        }
        return r;
    }
    
    public boolean processIdleResponse(final Response r) throws ProtocolException {
        final Response[] responses = { r };
        boolean done = false;
        this.notifyResponseHandlers(responses);
        if (r.isBYE()) {
            done = true;
        }
        if (r.isTagged() && r.getTag().equals(this.idleTag)) {
            done = true;
        }
        if (done) {
            this.idleTag = null;
        }
        this.handleResult(r);
        return !done;
    }
    
    public void idleAbort() {
        final OutputStream os = this.getOutputStream();
        try {
            os.write(IMAPProtocol.DONE);
            os.flush();
        }
        catch (final Exception ex) {
            this.logger.log(Level.FINEST, "Exception aborting IDLE", ex);
        }
    }
    
    public Map<String, String> id(final Map<String, String> clientParams) throws ProtocolException {
        if (!this.hasCapability("ID")) {
            throw new BadCommandException("ID not supported");
        }
        final Response[] r = this.command("ID", ID.getArgumentList(clientParams));
        ID id = null;
        final Response response = r[r.length - 1];
        if (response.isOK()) {
            for (int i = 0, len = r.length; i < len; ++i) {
                if (r[i] instanceof IMAPResponse) {
                    final IMAPResponse ir = (IMAPResponse)r[i];
                    if (ir.keyEquals("ID")) {
                        if (id == null) {
                            id = new ID(ir);
                        }
                        r[i] = null;
                    }
                }
            }
        }
        this.notifyResponseHandlers(r);
        this.handleResult(response);
        return (id == null) ? null : id.getServerParams();
    }
    
    static {
        CRLF = new byte[] { 13, 10 };
        fetchItems = new FetchItem[0];
        DONE = new byte[] { 68, 79, 78, 69, 13, 10 };
    }
}
