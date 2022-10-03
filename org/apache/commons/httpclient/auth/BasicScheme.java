package org.apache.commons.httpclient.auth;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.logging.Log;

public class BasicScheme extends RFC2617Scheme
{
    private static final Log LOG;
    private boolean complete;
    
    public BasicScheme() {
        this.complete = false;
    }
    
    public BasicScheme(final String challenge) throws MalformedChallengeException {
        super(challenge);
        this.complete = true;
    }
    
    public String getSchemeName() {
        return "basic";
    }
    
    public void processChallenge(final String challenge) throws MalformedChallengeException {
        super.processChallenge(challenge);
        this.complete = true;
    }
    
    public boolean isComplete() {
        return this.complete;
    }
    
    public String authenticate(final Credentials credentials, final String method, final String uri) throws AuthenticationException {
        BasicScheme.LOG.trace((Object)"enter BasicScheme.authenticate(Credentials, String, String)");
        UsernamePasswordCredentials usernamepassword = null;
        try {
            usernamepassword = (UsernamePasswordCredentials)credentials;
        }
        catch (final ClassCastException e) {
            throw new InvalidCredentialsException("Credentials cannot be used for basic authentication: " + credentials.getClass().getName());
        }
        return authenticate(usernamepassword);
    }
    
    public boolean isConnectionBased() {
        return false;
    }
    
    public String authenticate(final Credentials credentials, final HttpMethod method) throws AuthenticationException {
        BasicScheme.LOG.trace((Object)"enter BasicScheme.authenticate(Credentials, HttpMethod)");
        if (method == null) {
            throw new IllegalArgumentException("Method may not be null");
        }
        UsernamePasswordCredentials usernamepassword = null;
        try {
            usernamepassword = (UsernamePasswordCredentials)credentials;
        }
        catch (final ClassCastException e) {
            throw new InvalidCredentialsException("Credentials cannot be used for basic authentication: " + credentials.getClass().getName());
        }
        return authenticate(usernamepassword, method.getParams().getCredentialCharset());
    }
    
    public static String authenticate(final UsernamePasswordCredentials credentials) {
        return authenticate(credentials, "ISO-8859-1");
    }
    
    public static String authenticate(final UsernamePasswordCredentials credentials, final String charset) {
        BasicScheme.LOG.trace((Object)"enter BasicScheme.authenticate(UsernamePasswordCredentials, String)");
        if (credentials == null) {
            throw new IllegalArgumentException("Credentials may not be null");
        }
        if (charset == null || charset.length() == 0) {
            throw new IllegalArgumentException("charset may not be null or empty");
        }
        final StringBuffer buffer = new StringBuffer();
        buffer.append(credentials.getUserName());
        buffer.append(":");
        buffer.append(credentials.getPassword());
        return "Basic " + EncodingUtil.getAsciiString(Base64.encodeBase64(EncodingUtil.getBytes(buffer.toString(), charset)));
    }
    
    static {
        LOG = LogFactory.getLog(BasicScheme.class);
    }
}
