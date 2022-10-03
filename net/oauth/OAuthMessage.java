package net.oauth;

import java.util.regex.Matcher;
import java.io.Reader;
import java.io.InputStreamReader;
import net.oauth.signature.OAuthSignatureMethod;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.HashMap;
import java.util.Collections;
import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;
import java.io.InputStream;
import java.util.Map;
import java.util.List;

public class OAuthMessage
{
    public String method;
    public String URL;
    private final List<Map.Entry<String, String>> parameters;
    private Map<String, String> parameterMap;
    private boolean parametersAreComplete;
    private final List<Map.Entry<String, String>> headers;
    private final InputStream bodyAsStream;
    public static final String AUTH_SCHEME = "OAuth";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    private static final Pattern AUTHORIZATION;
    private static final Pattern NVP;
    
    public OAuthMessage(final String method, final String URL, final Collection<? extends Map.Entry> parameters) {
        this(method, URL, parameters, null);
    }
    
    public OAuthMessage(final String method, final String URL, final Collection<? extends Map.Entry> parameters, final InputStream bodyAsStream) {
        this.parametersAreComplete = false;
        this.headers = new ArrayList<Map.Entry<String, String>>();
        this.method = method;
        this.URL = URL;
        this.bodyAsStream = bodyAsStream;
        if (parameters == null) {
            this.parameters = new ArrayList<Map.Entry<String, String>>();
        }
        else {
            this.parameters = new ArrayList<Map.Entry<String, String>>(parameters.size());
            for (final Map.Entry p : parameters) {
                this.parameters.add(new OAuth.Parameter(toString(p.getKey()), toString(p.getValue())));
            }
        }
    }
    
    @Override
    public String toString() {
        return "OAuthMessage(" + this.method + ", " + this.URL + ", " + this.parameters + ")";
    }
    
    private void beforeGetParameter() throws IOException {
        if (!this.parametersAreComplete) {
            this.completeParameters();
            this.parametersAreComplete = true;
        }
    }
    
    protected void completeParameters() throws IOException {
    }
    
    public List<Map.Entry<String, String>> getParameters() throws IOException {
        this.beforeGetParameter();
        return Collections.unmodifiableList((List<? extends Map.Entry<String, String>>)this.parameters);
    }
    
    public void addParameter(final String key, final String value) {
        this.addParameter(new OAuth.Parameter(key, value));
    }
    
    public void addParameter(final Map.Entry<String, String> parameter) {
        this.parameters.add(parameter);
        this.parameterMap = null;
    }
    
    public void addParameters(final Collection<? extends Map.Entry<String, String>> parameters) {
        this.parameters.addAll(parameters);
        this.parameterMap = null;
    }
    
    public String getParameter(final String name) throws IOException {
        return this.getParameterMap().get(name);
    }
    
    public String getConsumerKey() throws IOException {
        return this.getParameter("oauth_consumer_key");
    }
    
    public String getToken() throws IOException {
        return this.getParameter("oauth_token");
    }
    
    public String getSignatureMethod() throws IOException {
        return this.getParameter("oauth_signature_method");
    }
    
    public String getSignature() throws IOException {
        return this.getParameter("oauth_signature");
    }
    
    protected Map<String, String> getParameterMap() throws IOException {
        this.beforeGetParameter();
        if (this.parameterMap == null) {
            this.parameterMap = OAuth.newMap(this.parameters);
        }
        return this.parameterMap;
    }
    
    public String getBodyType() {
        return this.getHeader("Content-Type");
    }
    
    public String getBodyEncoding() {
        return "ISO-8859-1";
    }
    
    public final String getHeader(final String name) {
        String value = null;
        for (final Map.Entry<String, String> header : this.getHeaders()) {
            if (name.equalsIgnoreCase(header.getKey())) {
                value = header.getValue();
            }
        }
        return value;
    }
    
    public final List<Map.Entry<String, String>> getHeaders() {
        return this.headers;
    }
    
    public final String readBodyAsString() throws IOException {
        final InputStream body = this.getBodyAsStream();
        return readAll(body, this.getBodyEncoding());
    }
    
    public InputStream getBodyAsStream() throws IOException {
        return this.bodyAsStream;
    }
    
    public Map<String, Object> getDump() throws IOException {
        final Map<String, Object> into = new HashMap<String, Object>();
        this.dump(into);
        return into;
    }
    
    protected void dump(final Map<String, Object> into) throws IOException {
        into.put("URL", this.URL);
        if (this.parametersAreComplete) {
            try {
                into.putAll(this.getParameterMap());
            }
            catch (final Exception ex) {}
        }
    }
    
    public void requireParameters(final String... names) throws OAuthProblemException, IOException {
        final Set<String> present = this.getParameterMap().keySet();
        final List<String> absent = new ArrayList<String>();
        for (final String required : names) {
            if (!present.contains(required)) {
                absent.add(required);
            }
        }
        if (!absent.isEmpty()) {
            final OAuthProblemException problem = new OAuthProblemException("parameter_absent");
            problem.setParameter("oauth_parameters_absent", OAuth.percentEncode(absent));
            throw problem;
        }
    }
    
    public void addRequiredParameters(final OAuthAccessor accessor) throws OAuthException, IOException, URISyntaxException {
        final Map<String, String> pMap = OAuth.newMap(this.parameters);
        if (pMap.get("oauth_token") == null && accessor.accessToken != null) {
            this.addParameter("oauth_token", accessor.accessToken);
        }
        final OAuthConsumer consumer = accessor.consumer;
        if (pMap.get("oauth_consumer_key") == null) {
            this.addParameter("oauth_consumer_key", consumer.consumerKey);
        }
        String signatureMethod = pMap.get("oauth_signature_method");
        if (signatureMethod == null) {
            signatureMethod = (String)consumer.getProperty("oauth_signature_method");
            if (signatureMethod == null) {
                signatureMethod = "HMAC-SHA1";
            }
            this.addParameter("oauth_signature_method", signatureMethod);
        }
        if (pMap.get("oauth_timestamp") == null) {
            this.addParameter("oauth_timestamp", System.currentTimeMillis() / 1000L + "");
        }
        if (pMap.get("oauth_nonce") == null) {
            this.addParameter("oauth_nonce", System.nanoTime() + "");
        }
        if (pMap.get("oauth_version") == null) {
            this.addParameter("oauth_version", "1.0");
        }
        this.sign(accessor);
    }
    
    public void sign(final OAuthAccessor accessor) throws IOException, OAuthException, URISyntaxException {
        OAuthSignatureMethod.newSigner(this, accessor).sign(this);
    }
    
    public String getAuthorizationHeader(final String realm) throws IOException {
        final StringBuilder into = new StringBuilder();
        if (realm != null) {
            into.append(" realm=\"").append(OAuth.percentEncode(realm)).append('\"');
        }
        this.beforeGetParameter();
        if (this.parameters != null) {
            for (final Map.Entry parameter : this.parameters) {
                final String name = toString(parameter.getKey());
                if (name.startsWith("oauth_")) {
                    if (into.length() > 0) {
                        into.append(",");
                    }
                    into.append(" ");
                    into.append(OAuth.percentEncode(name)).append("=\"");
                    into.append(OAuth.percentEncode(toString(parameter.getValue()))).append('\"');
                }
            }
        }
        return "OAuth" + into.toString();
    }
    
    public static String readAll(final InputStream from, final String encoding) throws IOException {
        if (from == null) {
            return null;
        }
        try {
            final StringBuilder into = new StringBuilder();
            final Reader r = new InputStreamReader(from, encoding);
            final char[] s = new char[512];
            int n;
            while (0 < (n = r.read(s))) {
                into.append(s, 0, n);
            }
            return into.toString();
        }
        finally {
            from.close();
        }
    }
    
    public static List<OAuth.Parameter> decodeAuthorization(final String authorization) {
        final List<OAuth.Parameter> into = new ArrayList<OAuth.Parameter>();
        if (authorization != null) {
            Matcher m = OAuthMessage.AUTHORIZATION.matcher(authorization);
            if (m.matches() && "OAuth".equalsIgnoreCase(m.group(1))) {
                for (final String nvp : m.group(2).split("\\s*,\\s*")) {
                    m = OAuthMessage.NVP.matcher(nvp);
                    if (m.matches()) {
                        final String name = OAuth.decodePercent(m.group(1));
                        final String value = OAuth.decodePercent(m.group(2));
                        into.add(new OAuth.Parameter(name, value));
                    }
                }
            }
        }
        return into;
    }
    
    private static final String toString(final Object from) {
        return (from == null) ? null : from.toString();
    }
    
    static {
        AUTHORIZATION = Pattern.compile("\\s*(\\w*)\\s+(.*)");
        NVP = Pattern.compile("(\\S*)\\s*\\=\\s*\"([^\"]*)\"");
    }
}
