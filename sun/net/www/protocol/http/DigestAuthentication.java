package sun.net.www.protocol.http;

import java.util.StringTokenizer;
import java.util.Random;
import java.io.Serializable;
import java.security.AccessController;
import sun.net.NetProperties;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.net.ProtocolException;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import sun.net.www.HeaderParser;
import java.net.PasswordAuthentication;
import java.net.URL;

class DigestAuthentication extends AuthenticationInfo
{
    private static final long serialVersionUID = 100L;
    private String authMethod;
    private static final String compatPropName = "http.auth.digest.quoteParameters";
    private static final boolean delimCompatFlag;
    Parameters params;
    private static final char[] charArray;
    private static final String[] zeroPad;
    
    public DigestAuthentication(final boolean b, final URL url, final String s, final String authMethod, final PasswordAuthentication pw, final Parameters params) {
        super(b ? 'p' : 's', AuthScheme.DIGEST, url, s);
        this.authMethod = authMethod;
        this.pw = pw;
        this.params = params;
    }
    
    public DigestAuthentication(final boolean b, final String s, final int n, final String s2, final String authMethod, final PasswordAuthentication pw, final Parameters params) {
        super(b ? 'p' : 's', AuthScheme.DIGEST, s, n, s2);
        this.authMethod = authMethod;
        this.pw = pw;
        this.params = params;
    }
    
    @Override
    public boolean supportsPreemptiveAuthorization() {
        return true;
    }
    
    @Override
    public String getHeaderValue(final URL url, final String s) {
        return this.getHeaderValueImpl(url.getFile(), s);
    }
    
    String getHeaderValue(final String s, final String s2) {
        return this.getHeaderValueImpl(s, s2);
    }
    
    @Override
    public boolean isAuthorizationStale(final String s) {
        final HeaderParser headerParser = new HeaderParser(s);
        final String value = headerParser.findValue("stale");
        if (value == null || !value.equals("true")) {
            return false;
        }
        final String value2 = headerParser.findValue("nonce");
        if (value2 == null || "".equals(value2)) {
            return false;
        }
        this.params.setNonce(value2);
        return true;
    }
    
    @Override
    public boolean setHeaders(final HttpURLConnection httpURLConnection, final HeaderParser headerParser, final String s) {
        this.params.setNonce(headerParser.findValue("nonce"));
        this.params.setOpaque(headerParser.findValue("opaque"));
        this.params.setQop(headerParser.findValue("qop"));
        String s2 = "";
        String s3;
        if (this.type == 'p' && httpURLConnection.tunnelState() == HttpURLConnection.TunnelState.SETUP) {
            s2 = HttpURLConnection.connectRequestURI(httpURLConnection.getURL());
            s3 = HttpURLConnection.HTTP_CONNECT;
        }
        else {
            try {
                s2 = httpURLConnection.getRequestURI();
            }
            catch (final IOException ex) {}
            s3 = httpURLConnection.getMethod();
        }
        if (this.params.nonce == null || this.authMethod == null || this.pw == null || this.realm == null) {
            return false;
        }
        if (this.authMethod.length() >= 1) {
            this.authMethod = Character.toUpperCase(this.authMethod.charAt(0)) + this.authMethod.substring(1).toLowerCase();
        }
        String value = headerParser.findValue("algorithm");
        if (value == null || "".equals(value)) {
            value = "MD5";
        }
        this.params.setAlgorithm(value);
        if (this.params.authQop()) {
            this.params.setNewCnonce();
        }
        final String headerValueImpl = this.getHeaderValueImpl(s2, s3);
        if (headerValueImpl != null) {
            httpURLConnection.setAuthenticationProperty(this.getHeaderName(), headerValueImpl);
            return true;
        }
        return false;
    }
    
    private String getHeaderValueImpl(final String s, final String s2) {
        final char[] password = this.pw.getPassword();
        final boolean authQop = this.params.authQop();
        final String opaque = this.params.getOpaque();
        final String cnonce = this.params.getCnonce();
        final String nonce = this.params.getNonce();
        final String algorithm = this.params.getAlgorithm();
        this.params.incrementNC();
        final int ncCount = this.params.getNCCount();
        String s3 = null;
        if (ncCount != -1) {
            s3 = Integer.toHexString(ncCount).toLowerCase();
            final int length = s3.length();
            if (length < 8) {
                s3 = DigestAuthentication.zeroPad[length] + s3;
            }
        }
        String computeDigest;
        try {
            computeDigest = this.computeDigest(true, this.pw.getUserName(), password, this.realm, s2, s, nonce, cnonce, s3);
        }
        catch (final NoSuchAlgorithmException ex) {
            return null;
        }
        String string = "\"";
        if (authQop) {
            string = "\", nc=" + s3;
        }
        String s4;
        String s5;
        if (DigestAuthentication.delimCompatFlag) {
            s4 = ", algorithm=\"" + algorithm + "\"";
            s5 = ", qop=\"auth\"";
        }
        else {
            s4 = ", algorithm=" + algorithm;
            s5 = ", qop=auth";
        }
        String s6 = this.authMethod + " username=\"" + this.pw.getUserName() + "\", realm=\"" + this.realm + "\", nonce=\"" + nonce + string + ", uri=\"" + s + "\", response=\"" + computeDigest + "\"" + s4;
        if (opaque != null) {
            s6 = s6 + ", opaque=\"" + opaque + "\"";
        }
        if (cnonce != null) {
            s6 = s6 + ", cnonce=\"" + cnonce + "\"";
        }
        if (authQop) {
            s6 += s5;
        }
        return s6;
    }
    
    public void checkResponse(final String s, final String s2, final URL url) throws IOException {
        this.checkResponse(s, s2, url.getFile());
    }
    
    public void checkResponse(final String s, final String s2, final String s3) throws IOException {
        final char[] password = this.pw.getPassword();
        final String userName = this.pw.getUserName();
        this.params.authQop();
        this.params.getOpaque();
        final String access$100 = this.params.cnonce;
        final String nonce = this.params.getNonce();
        this.params.getAlgorithm();
        final int ncCount = this.params.getNCCount();
        String s4 = null;
        if (s == null) {
            throw new ProtocolException("No authentication information in response");
        }
        if (ncCount != -1) {
            s4 = Integer.toHexString(ncCount).toUpperCase();
            final int length = s4.length();
            if (length < 8) {
                s4 = DigestAuthentication.zeroPad[length] + s4;
            }
        }
        try {
            final String computeDigest = this.computeDigest(false, userName, password, this.realm, s2, s3, nonce, access$100, s4);
            final HeaderParser headerParser = new HeaderParser(s);
            final String value = headerParser.findValue("rspauth");
            if (value == null) {
                throw new ProtocolException("No digest in response");
            }
            if (!value.equals(computeDigest)) {
                throw new ProtocolException("Response digest invalid");
            }
            final String value2 = headerParser.findValue("nextnonce");
            if (value2 != null && !"".equals(value2)) {
                this.params.setNonce(value2);
            }
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new ProtocolException("Unsupported algorithm in response");
        }
    }
    
    private String computeDigest(final boolean b, final String s, final char[] array, final String s2, final String s3, final String s4, final String s5, final String s6, final String s7) throws NoSuchAlgorithmException {
        final String algorithm = this.params.getAlgorithm();
        final boolean equalsIgnoreCase = algorithm.equalsIgnoreCase("MD5-sess");
        final MessageDigest instance = MessageDigest.getInstance(equalsIgnoreCase ? "MD5" : algorithm);
        String cachedHA1;
        if (equalsIgnoreCase) {
            if ((cachedHA1 = this.params.getCachedHA1()) == null) {
                cachedHA1 = this.encode(this.encode(s + ":" + s2 + ":", array, instance) + ":" + s5 + ":" + s6, null, instance);
                this.params.setCachedHA1(cachedHA1);
            }
        }
        else {
            cachedHA1 = this.encode(s + ":" + s2 + ":", array, instance);
        }
        String s8;
        if (b) {
            s8 = s3 + ":" + s4;
        }
        else {
            s8 = ":" + s4;
        }
        final String encode = this.encode(s8, null, instance);
        String s9;
        if (this.params.authQop()) {
            s9 = cachedHA1 + ":" + s5 + ":" + s7 + ":" + s6 + ":auth:" + encode;
        }
        else {
            s9 = cachedHA1 + ":" + s5 + ":" + encode;
        }
        return this.encode(s9, null, instance);
    }
    
    private String encode(final String s, final char[] array, final MessageDigest messageDigest) {
        try {
            messageDigest.update(s.getBytes("ISO-8859-1"));
        }
        catch (final UnsupportedEncodingException ex) {
            assert false;
        }
        if (array != null) {
            final byte[] array2 = new byte[array.length];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = (byte)array[i];
            }
            messageDigest.update(array2);
            Arrays.fill(array2, (byte)0);
        }
        final byte[] digest = messageDigest.digest();
        final StringBuffer sb = new StringBuffer(digest.length * 2);
        for (int j = 0; j < digest.length; ++j) {
            sb.append(DigestAuthentication.charArray[digest[j] >>> 4 & 0xF]);
            sb.append(DigestAuthentication.charArray[digest[j] & 0xF]);
        }
        return sb.toString();
    }
    
    static {
        final Boolean b = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return NetProperties.getBoolean("http.auth.digest.quoteParameters");
            }
        });
        delimCompatFlag = (b != null && b);
        charArray = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        zeroPad = new String[] { "00000000", "0000000", "000000", "00000", "0000", "000", "00", "0" };
    }
    
    static class Parameters implements Serializable
    {
        private static final long serialVersionUID = -3584543755194526252L;
        private boolean serverQop;
        private String opaque;
        private String cnonce;
        private String nonce;
        private String algorithm;
        private int NCcount;
        private String cachedHA1;
        private boolean redoCachedHA1;
        private static final int cnonceRepeat = 5;
        private static final int cnoncelen = 40;
        private static Random random;
        int cnonce_count;
        
        Parameters() {
            this.NCcount = 0;
            this.redoCachedHA1 = true;
            this.cnonce_count = 0;
            this.serverQop = false;
            this.opaque = null;
            this.algorithm = null;
            this.cachedHA1 = null;
            this.nonce = null;
            this.setNewCnonce();
        }
        
        boolean authQop() {
            return this.serverQop;
        }
        
        synchronized void incrementNC() {
            ++this.NCcount;
        }
        
        synchronized int getNCCount() {
            return this.NCcount;
        }
        
        synchronized String getCnonce() {
            if (this.cnonce_count >= 5) {
                this.setNewCnonce();
            }
            ++this.cnonce_count;
            return this.cnonce;
        }
        
        synchronized void setNewCnonce() {
            final byte[] array = new byte[20];
            final char[] array2 = new char[40];
            Parameters.random.nextBytes(array);
            for (int i = 0; i < 20; ++i) {
                final int n = array[i] + 128;
                array2[i * 2] = (char)(65 + n / 16);
                array2[i * 2 + 1] = (char)(65 + n % 16);
            }
            this.cnonce = new String(array2, 0, 40);
            this.cnonce_count = 0;
            this.redoCachedHA1 = true;
        }
        
        synchronized void setQop(final String s) {
            if (s != null) {
                final StringTokenizer stringTokenizer = new StringTokenizer(s, " ");
                while (stringTokenizer.hasMoreTokens()) {
                    if (stringTokenizer.nextToken().equalsIgnoreCase("auth")) {
                        this.serverQop = true;
                        return;
                    }
                }
            }
            this.serverQop = false;
        }
        
        synchronized String getOpaque() {
            return this.opaque;
        }
        
        synchronized void setOpaque(final String opaque) {
            this.opaque = opaque;
        }
        
        synchronized String getNonce() {
            return this.nonce;
        }
        
        synchronized void setNonce(final String nonce) {
            if (!nonce.equals(this.nonce)) {
                this.nonce = nonce;
                this.NCcount = 0;
                this.redoCachedHA1 = true;
            }
        }
        
        synchronized String getCachedHA1() {
            if (this.redoCachedHA1) {
                return null;
            }
            return this.cachedHA1;
        }
        
        synchronized void setCachedHA1(final String cachedHA1) {
            this.cachedHA1 = cachedHA1;
            this.redoCachedHA1 = false;
        }
        
        synchronized String getAlgorithm() {
            return this.algorithm;
        }
        
        synchronized void setAlgorithm(final String algorithm) {
            this.algorithm = algorithm;
        }
        
        static {
            Parameters.random = new Random();
        }
    }
}
