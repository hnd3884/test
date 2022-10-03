package org.apache.commons.httpclient.auth;

import org.apache.commons.logging.LogFactory;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.httpclient.HttpClientError;
import java.util.List;
import org.apache.commons.httpclient.NameValuePair;
import java.util.ArrayList;
import org.apache.commons.httpclient.util.EncodingUtil;
import java.security.MessageDigest;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.Credentials;
import java.util.StringTokenizer;
import org.apache.commons.httpclient.util.ParameterFormatter;
import org.apache.commons.logging.Log;

public class DigestScheme extends RFC2617Scheme
{
    private static final Log LOG;
    private static final char[] HEXADECIMAL;
    private boolean complete;
    private static final String NC = "00000001";
    private static final int QOP_MISSING = 0;
    private static final int QOP_AUTH_INT = 1;
    private static final int QOP_AUTH = 2;
    private int qopVariant;
    private String cnonce;
    private final ParameterFormatter formatter;
    
    public DigestScheme() {
        this.qopVariant = 0;
        this.complete = false;
        this.formatter = new ParameterFormatter();
    }
    
    public String getID() {
        String id = this.getRealm();
        final String nonce = this.getParameter("nonce");
        if (nonce != null) {
            id = id + "-" + nonce;
        }
        return id;
    }
    
    public DigestScheme(final String challenge) throws MalformedChallengeException {
        this();
        this.processChallenge(challenge);
    }
    
    public void processChallenge(final String challenge) throws MalformedChallengeException {
        super.processChallenge(challenge);
        if (this.getParameter("realm") == null) {
            throw new MalformedChallengeException("missing realm in challange");
        }
        if (this.getParameter("nonce") == null) {
            throw new MalformedChallengeException("missing nonce in challange");
        }
        boolean unsupportedQop = false;
        final String qop = this.getParameter("qop");
        if (qop != null) {
            final StringTokenizer tok = new StringTokenizer(qop, ",");
            while (tok.hasMoreTokens()) {
                final String variant = tok.nextToken().trim();
                if (variant.equals("auth")) {
                    this.qopVariant = 2;
                    break;
                }
                if (variant.equals("auth-int")) {
                    this.qopVariant = 1;
                }
                else {
                    unsupportedQop = true;
                    DigestScheme.LOG.warn((Object)("Unsupported qop detected: " + variant));
                }
            }
        }
        if (unsupportedQop && this.qopVariant == 0) {
            throw new MalformedChallengeException("None of the qop methods is supported");
        }
        this.cnonce = createCnonce();
        this.complete = true;
    }
    
    public boolean isComplete() {
        final String s = this.getParameter("stale");
        return !"true".equalsIgnoreCase(s) && this.complete;
    }
    
    public String getSchemeName() {
        return "digest";
    }
    
    public boolean isConnectionBased() {
        return false;
    }
    
    public String authenticate(final Credentials credentials, final String method, final String uri) throws AuthenticationException {
        DigestScheme.LOG.trace((Object)"enter DigestScheme.authenticate(Credentials, String, String)");
        UsernamePasswordCredentials usernamepassword = null;
        try {
            usernamepassword = (UsernamePasswordCredentials)credentials;
        }
        catch (final ClassCastException e) {
            throw new InvalidCredentialsException("Credentials cannot be used for digest authentication: " + credentials.getClass().getName());
        }
        this.getParameters().put("methodname", method);
        this.getParameters().put("uri", uri);
        final String digest = this.createDigest(usernamepassword.getUserName(), usernamepassword.getPassword());
        return "Digest " + this.createDigestHeader(usernamepassword.getUserName(), digest);
    }
    
    public String authenticate(final Credentials credentials, final HttpMethod method) throws AuthenticationException {
        DigestScheme.LOG.trace((Object)"enter DigestScheme.authenticate(Credentials, HttpMethod)");
        UsernamePasswordCredentials usernamepassword = null;
        try {
            usernamepassword = (UsernamePasswordCredentials)credentials;
        }
        catch (final ClassCastException e) {
            throw new InvalidCredentialsException("Credentials cannot be used for digest authentication: " + credentials.getClass().getName());
        }
        this.getParameters().put("methodname", method.getName());
        final StringBuffer buffer = new StringBuffer(method.getPath());
        final String query = method.getQueryString();
        if (query != null) {
            if (query.indexOf("?") != 0) {
                buffer.append("?");
            }
            buffer.append(method.getQueryString());
        }
        this.getParameters().put("uri", buffer.toString());
        final String charset = this.getParameter("charset");
        if (charset == null) {
            this.getParameters().put("charset", method.getParams().getCredentialCharset());
        }
        final String digest = this.createDigest(usernamepassword.getUserName(), usernamepassword.getPassword());
        return "Digest " + this.createDigestHeader(usernamepassword.getUserName(), digest);
    }
    
    private String createDigest(final String uname, final String pwd) throws AuthenticationException {
        DigestScheme.LOG.trace((Object)"enter DigestScheme.createDigest(String, String, Map)");
        final String digAlg = "MD5";
        final String uri = this.getParameter("uri");
        final String realm = this.getParameter("realm");
        final String nonce = this.getParameter("nonce");
        final String qop = this.getParameter("qop");
        final String method = this.getParameter("methodname");
        String algorithm = this.getParameter("algorithm");
        if (algorithm == null) {
            algorithm = "MD5";
        }
        String charset = this.getParameter("charset");
        if (charset == null) {
            charset = "ISO-8859-1";
        }
        if (this.qopVariant == 1) {
            DigestScheme.LOG.warn((Object)"qop=auth-int is not supported");
            throw new AuthenticationException("Unsupported qop in HTTP Digest authentication");
        }
        MessageDigest md5Helper;
        try {
            md5Helper = MessageDigest.getInstance("MD5");
        }
        catch (final Exception e) {
            throw new AuthenticationException("Unsupported algorithm in HTTP Digest authentication: MD5");
        }
        final StringBuffer tmp = new StringBuffer(uname.length() + realm.length() + pwd.length() + 2);
        tmp.append(uname);
        tmp.append(':');
        tmp.append(realm);
        tmp.append(':');
        tmp.append(pwd);
        String a1 = tmp.toString();
        if (algorithm.equals("MD5-sess")) {
            final String tmp2 = encode(md5Helper.digest(EncodingUtil.getBytes(a1, charset)));
            final StringBuffer tmp3 = new StringBuffer(tmp2.length() + nonce.length() + this.cnonce.length() + 2);
            tmp3.append(tmp2);
            tmp3.append(':');
            tmp3.append(nonce);
            tmp3.append(':');
            tmp3.append(this.cnonce);
            a1 = tmp3.toString();
        }
        else if (!algorithm.equals("MD5")) {
            DigestScheme.LOG.warn((Object)("Unhandled algorithm " + algorithm + " requested"));
        }
        final String md5a1 = encode(md5Helper.digest(EncodingUtil.getBytes(a1, charset)));
        String a2 = null;
        if (this.qopVariant == 1) {
            DigestScheme.LOG.error((Object)"Unhandled qop auth-int");
        }
        else {
            a2 = method + ":" + uri;
        }
        final String md5a2 = encode(md5Helper.digest(EncodingUtil.getAsciiBytes(a2)));
        String serverDigestValue;
        if (this.qopVariant == 0) {
            DigestScheme.LOG.debug((Object)"Using null qop method");
            final StringBuffer tmp4 = new StringBuffer(md5a1.length() + nonce.length() + md5a2.length());
            tmp4.append(md5a1);
            tmp4.append(':');
            tmp4.append(nonce);
            tmp4.append(':');
            tmp4.append(md5a2);
            serverDigestValue = tmp4.toString();
        }
        else {
            if (DigestScheme.LOG.isDebugEnabled()) {
                DigestScheme.LOG.debug((Object)("Using qop method " + qop));
            }
            final String qopOption = this.getQopVariantString();
            final StringBuffer tmp5 = new StringBuffer(md5a1.length() + nonce.length() + "00000001".length() + this.cnonce.length() + qopOption.length() + md5a2.length() + 5);
            tmp5.append(md5a1);
            tmp5.append(':');
            tmp5.append(nonce);
            tmp5.append(':');
            tmp5.append("00000001");
            tmp5.append(':');
            tmp5.append(this.cnonce);
            tmp5.append(':');
            tmp5.append(qopOption);
            tmp5.append(':');
            tmp5.append(md5a2);
            serverDigestValue = tmp5.toString();
        }
        final String serverDigest = encode(md5Helper.digest(EncodingUtil.getAsciiBytes(serverDigestValue)));
        return serverDigest;
    }
    
    private String createDigestHeader(final String uname, final String digest) throws AuthenticationException {
        DigestScheme.LOG.trace((Object)"enter DigestScheme.createDigestHeader(String, Map, String)");
        final String uri = this.getParameter("uri");
        final String realm = this.getParameter("realm");
        final String nonce = this.getParameter("nonce");
        final String opaque = this.getParameter("opaque");
        final String response = digest;
        final String algorithm = this.getParameter("algorithm");
        final List params = new ArrayList(20);
        params.add(new NameValuePair("username", uname));
        params.add(new NameValuePair("realm", realm));
        params.add(new NameValuePair("nonce", nonce));
        params.add(new NameValuePair("uri", uri));
        params.add(new NameValuePair("response", response));
        if (this.qopVariant != 0) {
            params.add(new NameValuePair("qop", this.getQopVariantString()));
            params.add(new NameValuePair("nc", "00000001"));
            params.add(new NameValuePair("cnonce", this.cnonce));
        }
        if (algorithm != null) {
            params.add(new NameValuePair("algorithm", algorithm));
        }
        if (opaque != null) {
            params.add(new NameValuePair("opaque", opaque));
        }
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < params.size(); ++i) {
            final NameValuePair param = params.get(i);
            if (i > 0) {
                buffer.append(", ");
            }
            final boolean noQuotes = "nc".equals(param.getName()) || "qop".equals(param.getName());
            this.formatter.setAlwaysUseQuotes(!noQuotes);
            this.formatter.format(buffer, param);
        }
        return buffer.toString();
    }
    
    private String getQopVariantString() {
        String qopOption;
        if (this.qopVariant == 1) {
            qopOption = "auth-int";
        }
        else {
            qopOption = "auth";
        }
        return qopOption;
    }
    
    private static String encode(final byte[] binaryData) {
        DigestScheme.LOG.trace((Object)"enter DigestScheme.encode(byte[])");
        if (binaryData.length != 16) {
            return null;
        }
        final char[] buffer = new char[32];
        for (int i = 0; i < 16; ++i) {
            final int low = binaryData[i] & 0xF;
            final int high = (binaryData[i] & 0xF0) >> 4;
            buffer[i * 2] = DigestScheme.HEXADECIMAL[high];
            buffer[i * 2 + 1] = DigestScheme.HEXADECIMAL[low];
        }
        return new String(buffer);
    }
    
    public static String createCnonce() {
        DigestScheme.LOG.trace((Object)"enter DigestScheme.createCnonce()");
        final String digAlg = "MD5";
        MessageDigest md5Helper;
        try {
            md5Helper = MessageDigest.getInstance("MD5");
        }
        catch (final NoSuchAlgorithmException e) {
            throw new HttpClientError("Unsupported algorithm in HTTP Digest authentication: MD5");
        }
        String cnonce = Long.toString(System.currentTimeMillis());
        cnonce = encode(md5Helper.digest(EncodingUtil.getAsciiBytes(cnonce)));
        return cnonce;
    }
    
    static {
        LOG = LogFactory.getLog(DigestScheme.class);
        HEXADECIMAL = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
