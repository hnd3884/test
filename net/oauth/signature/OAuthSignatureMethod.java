package net.oauth.signature;

import java.util.concurrent.ConcurrentHashMap;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Collections;
import java.net.URI;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthProblemException;
import java.net.URISyntaxException;
import java.io.IOException;
import net.oauth.OAuthException;
import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import java.util.Map;

public abstract class OAuthSignatureMethod
{
    public static final String _ACCESSOR = "-Accessor";
    private String consumerSecret;
    private String tokenSecret;
    private static final String BASE64_ENCODING = "ISO-8859-1";
    private static final Base64 BASE64;
    private static final Map<String, Class> NAME_TO_CLASS;
    
    public void sign(final OAuthMessage message) throws OAuthException, IOException, URISyntaxException {
        message.addParameter(new OAuth.Parameter("oauth_signature", this.getSignature(message)));
    }
    
    public void validate(final OAuthMessage message) throws IOException, OAuthException, URISyntaxException {
        message.requireParameters("oauth_signature");
        final String signature = message.getSignature();
        final String baseString = getBaseString(message);
        if (!this.isValid(signature, baseString)) {
            final OAuthProblemException problem = new OAuthProblemException("signature_invalid");
            problem.setParameter("oauth_signature", signature);
            problem.setParameter("oauth_signature_base_string", baseString);
            problem.setParameter("oauth_signature_method", message.getSignatureMethod());
            throw problem;
        }
    }
    
    protected String getSignature(final OAuthMessage message) throws OAuthException, IOException, URISyntaxException {
        final String baseString = getBaseString(message);
        final String signature = this.getSignature(baseString);
        return signature;
    }
    
    protected void initialize(final String name, final OAuthAccessor accessor) throws OAuthException {
        String secret = accessor.consumer.consumerSecret;
        if (name.endsWith("-Accessor")) {
            final String key = "oauth_accessor_secret";
            Object accessorSecret = accessor.getProperty("oauth_accessor_secret");
            if (accessorSecret == null) {
                accessorSecret = accessor.consumer.getProperty("oauth_accessor_secret");
            }
            if (accessorSecret != null) {
                secret = accessorSecret.toString();
            }
        }
        if (secret == null) {
            secret = "";
        }
        this.setConsumerSecret(secret);
    }
    
    protected abstract String getSignature(final String p0) throws OAuthException;
    
    protected abstract boolean isValid(final String p0, final String p1) throws OAuthException;
    
    protected String getConsumerSecret() {
        return this.consumerSecret;
    }
    
    protected void setConsumerSecret(final String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }
    
    public String getTokenSecret() {
        return this.tokenSecret;
    }
    
    public void setTokenSecret(final String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }
    
    public static String getBaseString(final OAuthMessage message) throws IOException, URISyntaxException {
        String url = message.URL;
        final int q = url.indexOf(63);
        List<Map.Entry<String, String>> parameters;
        if (q < 0) {
            parameters = message.getParameters();
        }
        else {
            parameters = new ArrayList<Map.Entry<String, String>>();
            parameters.addAll(OAuth.decodeForm(message.URL.substring(q + 1)));
            parameters.addAll(message.getParameters());
            url = url.substring(0, q);
        }
        return OAuth.percentEncode(message.method.toUpperCase()) + '&' + OAuth.percentEncode(normalizeUrl(url)) + '&' + OAuth.percentEncode(normalizeParameters(parameters));
    }
    
    protected static String normalizeUrl(final String url) throws URISyntaxException {
        final URI uri = new URI(url);
        final String scheme = uri.getScheme().toLowerCase();
        String authority = uri.getAuthority().toLowerCase();
        final boolean dropPort = (scheme.equals("http") && uri.getPort() == 80) || (scheme.equals("https") && uri.getPort() == 443);
        if (dropPort) {
            final int index = authority.lastIndexOf(":");
            if (index >= 0) {
                authority = authority.substring(0, index);
            }
        }
        String path = uri.getRawPath();
        if (path == null || path.length() <= 0) {
            path = "/";
        }
        return scheme + "://" + authority + path;
    }
    
    protected static String normalizeParameters(final Collection<? extends Map.Entry> parameters) throws IOException {
        if (parameters == null) {
            return "";
        }
        final List<ComparableParameter> p = new ArrayList<ComparableParameter>(parameters.size());
        for (final Map.Entry parameter : parameters) {
            if (!"oauth_signature".equals(parameter.getKey())) {
                p.add(new ComparableParameter(parameter));
            }
        }
        Collections.sort(p);
        return OAuth.formEncode(getParameters(p));
    }
    
    public static boolean equals(final String x, final String y) {
        if (x == null) {
            return y == null;
        }
        if (y == null) {
            return false;
        }
        if (y.length() <= 0) {
            return x.length() <= 0;
        }
        final char[] a = x.toCharArray();
        final char[] b = y.toCharArray();
        char diff = (char)((a.length != b.length) ? 1 : 0);
        int j = 0;
        for (int i = 0; i < a.length; ++i) {
            diff |= (char)(a[i] ^ b[j]);
            j = (j + 1) % b.length;
        }
        return diff == '\0';
    }
    
    public static boolean equals(final byte[] a, final byte[] b) {
        if (a == null) {
            return b == null;
        }
        if (b == null) {
            return false;
        }
        if (b.length <= 0) {
            return a.length <= 0;
        }
        byte diff = (byte)((a.length != b.length) ? 1 : 0);
        int j = 0;
        for (int i = 0; i < a.length; ++i) {
            diff |= (byte)(a[i] ^ b[j]);
            j = (j + 1) % b.length;
        }
        return diff == 0;
    }
    
    public static byte[] decodeBase64(final String s) {
        byte[] b;
        try {
            b = s.getBytes("ISO-8859-1");
        }
        catch (final UnsupportedEncodingException e) {
            System.err.println(e + "");
            b = s.getBytes();
        }
        return OAuthSignatureMethod.BASE64.decode(b);
    }
    
    public static String base64Encode(final byte[] b) {
        final byte[] b2 = OAuthSignatureMethod.BASE64.encode(b);
        try {
            return new String(b2, "ISO-8859-1");
        }
        catch (final UnsupportedEncodingException e) {
            System.err.println(e + "");
            return new String(b2);
        }
    }
    
    public static OAuthSignatureMethod newSigner(final OAuthMessage message, final OAuthAccessor accessor) throws IOException, OAuthException {
        message.requireParameters("oauth_signature_method");
        final OAuthSignatureMethod signer = newMethod(message.getSignatureMethod(), accessor);
        signer.setTokenSecret(accessor.tokenSecret);
        return signer;
    }
    
    public static OAuthSignatureMethod newMethod(final String name, final OAuthAccessor accessor) throws OAuthException {
        try {
            final Class methodClass = OAuthSignatureMethod.NAME_TO_CLASS.get(name);
            if (methodClass != null) {
                final OAuthSignatureMethod method = methodClass.newInstance();
                method.initialize(name, accessor);
                return method;
            }
            final OAuthProblemException problem = new OAuthProblemException("signature_method_rejected");
            final String acceptable = OAuth.percentEncode(OAuthSignatureMethod.NAME_TO_CLASS.keySet());
            if (acceptable.length() > 0) {
                problem.setParameter("oauth_acceptable_signature_methods", acceptable.toString());
            }
            throw problem;
        }
        catch (final InstantiationException e) {
            throw new OAuthException(e);
        }
        catch (final IllegalAccessException e2) {
            throw new OAuthException(e2);
        }
    }
    
    public static void registerMethodClass(final String name, final Class clazz) {
        if (clazz == null) {
            unregisterMethod(name);
        }
        else {
            OAuthSignatureMethod.NAME_TO_CLASS.put(name, clazz);
        }
    }
    
    public static void unregisterMethod(final String name) {
        OAuthSignatureMethod.NAME_TO_CLASS.remove(name);
    }
    
    private static List<Map.Entry> getParameters(final Collection<ComparableParameter> parameters) {
        if (parameters == null) {
            return null;
        }
        final List<Map.Entry> list = new ArrayList<Map.Entry>(parameters.size());
        for (final ComparableParameter parameter : parameters) {
            list.add(parameter.value);
        }
        return list;
    }
    
    static {
        BASE64 = new Base64();
        NAME_TO_CLASS = new ConcurrentHashMap<String, Class>();
        registerMethodClass("HMAC-SHA1", HMAC_SHA1.class);
        registerMethodClass("PLAINTEXT", PLAINTEXT.class);
        registerMethodClass("RSA-SHA1", RSA_SHA1.class);
        registerMethodClass("HMAC-SHA1-Accessor", HMAC_SHA1.class);
        registerMethodClass("PLAINTEXT-Accessor", PLAINTEXT.class);
    }
    
    private static class ComparableParameter implements Comparable<ComparableParameter>
    {
        final Map.Entry value;
        private final String key;
        
        ComparableParameter(final Map.Entry value) {
            this.value = value;
            final String n = toString(value.getKey());
            final String v = toString(value.getValue());
            this.key = OAuth.percentEncode(n) + ' ' + OAuth.percentEncode(v);
        }
        
        private static String toString(final Object from) {
            return (from == null) ? null : from.toString();
        }
        
        public int compareTo(final ComparableParameter that) {
            return this.key.compareTo(that.key);
        }
        
        @Override
        public String toString() {
            return this.key;
        }
    }
}
