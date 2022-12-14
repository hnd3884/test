package org.glassfish.jersey.client.authentication;

import java.security.MessageDigest;
import org.glassfish.jersey.message.MessageUtils;
import org.glassfish.jersey.uri.UriComponent;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.ClientResponseContext;
import java.io.IOException;
import javax.ws.rs.client.ClientRequestContext;
import java.security.NoSuchAlgorithmException;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.net.URI;
import java.util.Map;
import java.security.SecureRandom;
import java.util.regex.Pattern;

final class DigestAuthenticator
{
    private static final char[] HEX_ARRAY;
    private static final Pattern KEY_VALUE_PAIR_PATTERN;
    private static final int CLIENT_NONCE_BYTE_COUNT = 4;
    private final SecureRandom randomGenerator;
    private final HttpAuthenticationFilter.Credentials credentials;
    private final Map<URI, DigestScheme> digestCache;
    
    DigestAuthenticator(final HttpAuthenticationFilter.Credentials credentials, final int limit) {
        this.credentials = credentials;
        this.digestCache = Collections.synchronizedMap((Map<URI, DigestScheme>)new LinkedHashMap<URI, DigestScheme>(limit) {
            private static final long serialVersionUID = 2546245625L;
            
            @Override
            protected boolean removeEldestEntry(final Map.Entry eldest) {
                return this.size() > limit;
            }
        });
        try {
            this.randomGenerator = SecureRandom.getInstance("SHA1PRNG");
        }
        catch (final NoSuchAlgorithmException e) {
            throw new RequestAuthenticationException(LocalizationMessages.ERROR_DIGEST_FILTER_GENERATOR(), e);
        }
    }
    
    boolean filterRequest(final ClientRequestContext request) throws IOException {
        final DigestScheme digestScheme = this.digestCache.get(request.getUri());
        if (digestScheme != null) {
            final HttpAuthenticationFilter.Credentials cred = HttpAuthenticationFilter.getCredentials(request, this.credentials, HttpAuthenticationFilter.Type.DIGEST);
            if (cred != null) {
                request.getHeaders().add((Object)"Authorization", (Object)this.createNextAuthToken(digestScheme, request, cred));
                return true;
            }
        }
        return false;
    }
    
    public boolean filterResponse(final ClientRequestContext request, final ClientResponseContext response) throws IOException {
        if (Response.Status.fromStatusCode(response.getStatus()) != Response.Status.UNAUTHORIZED) {
            return true;
        }
        final DigestScheme digestScheme = this.parseAuthHeaders((List<?>)response.getHeaders().get((Object)"WWW-Authenticate"));
        if (digestScheme == null) {
            return false;
        }
        final HttpAuthenticationFilter.Credentials cred = HttpAuthenticationFilter.getCredentials(request, this.credentials, HttpAuthenticationFilter.Type.DIGEST);
        if (cred == null) {
            throw new ResponseAuthenticationException(null, LocalizationMessages.AUTHENTICATION_CREDENTIALS_MISSING_DIGEST());
        }
        final boolean success = HttpAuthenticationFilter.repeatRequest(request, response, this.createNextAuthToken(digestScheme, request, cred));
        if (success) {
            this.digestCache.put(request.getUri(), digestScheme);
        }
        else {
            this.digestCache.remove(request.getUri());
        }
        return success;
    }
    
    private DigestScheme parseAuthHeaders(final List<?> headers) throws IOException {
        if (headers == null) {
            return null;
        }
        for (final Object lineObject : headers) {
            if (!(lineObject instanceof String)) {
                continue;
            }
            final String line = (String)lineObject;
            final String[] parts = line.trim().split("\\s+", 2);
            if (parts.length != 2) {
                continue;
            }
            if (!"digest".equals(parts[0].toLowerCase())) {
                continue;
            }
            String realm = null;
            String nonce = null;
            String opaque = null;
            QOP qop = QOP.UNSPECIFIED;
            Algorithm algorithm = Algorithm.UNSPECIFIED;
            boolean stale = false;
            final Matcher match = DigestAuthenticator.KEY_VALUE_PAIR_PATTERN.matcher(parts[1]);
            while (match.find()) {
                final int nbGroups = match.groupCount();
                if (nbGroups != 4) {
                    continue;
                }
                final String key = match.group(1);
                final String valNoQuotes = match.group(3);
                final String valQuotes = match.group(4);
                final String val = (valNoQuotes == null) ? valQuotes : valNoQuotes;
                if ("qop".equals(key)) {
                    qop = QOP.parse(val);
                }
                else if ("realm".equals(key)) {
                    realm = val;
                }
                else if ("nonce".equals(key)) {
                    nonce = val;
                }
                else if ("opaque".equals(key)) {
                    opaque = val;
                }
                else if ("stale".equals(key)) {
                    stale = Boolean.parseBoolean(val);
                }
                else {
                    if (!"algorithm".equals(key)) {
                        continue;
                    }
                    algorithm = Algorithm.parse(val);
                }
            }
            return new DigestScheme(realm, nonce, opaque, qop, algorithm, stale);
        }
        return null;
    }
    
    private String createNextAuthToken(final DigestScheme ds, final ClientRequestContext requestContext, final HttpAuthenticationFilter.Credentials credentials) throws IOException {
        final StringBuilder sb = new StringBuilder(100);
        sb.append("Digest ");
        append(sb, "username", credentials.getUsername());
        append(sb, "realm", ds.getRealm());
        append(sb, "nonce", ds.getNonce());
        append(sb, "opaque", ds.getOpaque());
        append(sb, "algorithm", ds.getAlgorithm().toString(), false);
        append(sb, "qop", ds.getQop().toString(), false);
        final String uri = UriComponent.fullRelativeUri(requestContext.getUri());
        append(sb, "uri", uri);
        String ha1;
        if (ds.getAlgorithm() == Algorithm.MD5_SESS) {
            ha1 = md5(md5(credentials.getUsername(), ds.getRealm(), new String(credentials.getPassword(), MessageUtils.getCharset(requestContext.getMediaType()))));
        }
        else {
            ha1 = md5(credentials.getUsername(), ds.getRealm(), new String(credentials.getPassword(), MessageUtils.getCharset(requestContext.getMediaType())));
        }
        final String ha2 = md5(requestContext.getMethod(), uri);
        String response;
        if (ds.getQop() == QOP.UNSPECIFIED) {
            response = md5(ha1, ds.getNonce(), ha2);
        }
        else {
            final String cnonce = this.randomBytes(4);
            append(sb, "cnonce", cnonce);
            final String nc = String.format("%08x", ds.incrementCounter());
            append(sb, "nc", nc, false);
            response = md5(ha1, ds.getNonce(), nc, cnonce, ds.getQop().toString(), ha2);
        }
        append(sb, "response", response);
        return sb.toString();
    }
    
    private static void append(final StringBuilder sb, final String key, final String value, final boolean useQuote) {
        if (value == null) {
            return;
        }
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ') {
            sb.append(',');
        }
        sb.append(key);
        sb.append('=');
        if (useQuote) {
            sb.append('\"');
        }
        sb.append(value);
        if (useQuote) {
            sb.append('\"');
        }
    }
    
    private static void append(final StringBuilder sb, final String key, final String value) {
        append(sb, key, value, true);
    }
    
    private static String bytesToHex(final byte[] bytes) {
        final char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; ++j) {
            final int v = bytes[j] & 0xFF;
            hexChars[j * 2] = DigestAuthenticator.HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = DigestAuthenticator.HEX_ARRAY[v & 0xF];
        }
        return new String(hexChars);
    }
    
    private static String md5(final String... tokens) throws IOException {
        final StringBuilder sb = new StringBuilder(100);
        for (final String token : tokens) {
            if (sb.length() > 0) {
                sb.append(':');
            }
            sb.append(token);
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new IOException(ex.getMessage());
        }
        md.update(sb.toString().getBytes(HttpAuthenticationFilter.CHARACTER_SET), 0, sb.length());
        final byte[] md5hash = md.digest();
        return bytesToHex(md5hash);
    }
    
    private String randomBytes(final int nbBytes) {
        final byte[] bytes = new byte[nbBytes];
        this.randomGenerator.nextBytes(bytes);
        return bytesToHex(bytes);
    }
    
    static {
        HEX_ARRAY = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        KEY_VALUE_PAIR_PATTERN = Pattern.compile("(\\w+)\\s*=\\s*(\"([^\"]+)\"|(\\w+))\\s*,?\\s*");
    }
    
    private enum QOP
    {
        UNSPECIFIED((String)null), 
        AUTH("auth");
        
        private final String qop;
        
        private QOP(final String qop) {
            this.qop = qop;
        }
        
        @Override
        public String toString() {
            return this.qop;
        }
        
        public static QOP parse(final String val) {
            if (val == null || val.isEmpty()) {
                return QOP.UNSPECIFIED;
            }
            if (val.contains("auth")) {
                return QOP.AUTH;
            }
            throw new UnsupportedOperationException(LocalizationMessages.DIGEST_FILTER_QOP_UNSUPPORTED(val));
        }
    }
    
    enum Algorithm
    {
        UNSPECIFIED((String)null), 
        MD5("MD5"), 
        MD5_SESS("MD5-sess");
        
        private final String md;
        
        private Algorithm(final String md) {
            this.md = md;
        }
        
        @Override
        public String toString() {
            return this.md;
        }
        
        public static Algorithm parse(String val) {
            if (val == null || val.isEmpty()) {
                return Algorithm.UNSPECIFIED;
            }
            val = val.trim();
            if (val.contains(Algorithm.MD5_SESS.md) || val.contains(Algorithm.MD5_SESS.md.toLowerCase())) {
                return Algorithm.MD5_SESS;
            }
            return Algorithm.MD5;
        }
    }
    
    final class DigestScheme
    {
        private final String realm;
        private final String nonce;
        private final String opaque;
        private final Algorithm algorithm;
        private final QOP qop;
        private final boolean stale;
        private volatile int nc;
        
        DigestScheme(final String realm, final String nonce, final String opaque, final QOP qop, final Algorithm algorithm, final boolean stale) {
            this.realm = realm;
            this.nonce = nonce;
            this.opaque = opaque;
            this.qop = qop;
            this.algorithm = algorithm;
            this.stale = stale;
            this.nc = 0;
        }
        
        public int incrementCounter() {
            return ++this.nc;
        }
        
        public String getNonce() {
            return this.nonce;
        }
        
        public String getRealm() {
            return this.realm;
        }
        
        public String getOpaque() {
            return this.opaque;
        }
        
        public Algorithm getAlgorithm() {
            return this.algorithm;
        }
        
        public QOP getQop() {
            return this.qop;
        }
        
        public boolean isStale() {
            return this.stale;
        }
        
        public int getNc() {
            return this.nc;
        }
    }
}
