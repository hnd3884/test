package org.apache.commons.httpclient.auth;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.logging.Log;

public class NTLMScheme implements AuthScheme
{
    private static final Log LOG;
    private String ntlmchallenge;
    private static final int UNINITIATED = 0;
    private static final int INITIATED = 1;
    private static final int TYPE1_MSG_GENERATED = 2;
    private static final int TYPE2_MSG_RECEIVED = 3;
    private static final int TYPE3_MSG_GENERATED = 4;
    private static final int FAILED = Integer.MAX_VALUE;
    private int state;
    
    public NTLMScheme() {
        this.ntlmchallenge = null;
        this.state = 0;
    }
    
    public NTLMScheme(final String challenge) throws MalformedChallengeException {
        this.ntlmchallenge = null;
        this.processChallenge(challenge);
    }
    
    public void processChallenge(final String challenge) throws MalformedChallengeException {
        String s = AuthChallengeParser.extractScheme(challenge);
        if (!s.equalsIgnoreCase(this.getSchemeName())) {
            throw new MalformedChallengeException("Invalid NTLM challenge: " + challenge);
        }
        final int i = challenge.indexOf(32);
        if (i != -1) {
            s = challenge.substring(i, challenge.length());
            this.ntlmchallenge = s.trim();
            this.state = 3;
        }
        else {
            this.ntlmchallenge = "";
            if (this.state == 0) {
                this.state = 1;
            }
            else {
                this.state = Integer.MAX_VALUE;
            }
        }
    }
    
    public boolean isComplete() {
        return this.state == 4 || this.state == Integer.MAX_VALUE;
    }
    
    public String getSchemeName() {
        return "ntlm";
    }
    
    public String getRealm() {
        return null;
    }
    
    public String getID() {
        return this.ntlmchallenge;
    }
    
    public String getParameter(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Parameter name may not be null");
        }
        return null;
    }
    
    public boolean isConnectionBased() {
        return true;
    }
    
    public static String authenticate(final NTCredentials credentials, final String challenge) throws AuthenticationException {
        NTLMScheme.LOG.trace((Object)"enter NTLMScheme.authenticate(NTCredentials, String)");
        if (credentials == null) {
            throw new IllegalArgumentException("Credentials may not be null");
        }
        final NTLM ntlm = new NTLM();
        final String s = ntlm.getResponseFor(challenge, credentials.getUserName(), credentials.getPassword(), credentials.getHost(), credentials.getDomain());
        return "NTLM " + s;
    }
    
    public static String authenticate(final NTCredentials credentials, final String challenge, final String charset) throws AuthenticationException {
        NTLMScheme.LOG.trace((Object)"enter NTLMScheme.authenticate(NTCredentials, String)");
        if (credentials == null) {
            throw new IllegalArgumentException("Credentials may not be null");
        }
        final NTLM ntlm = new NTLM();
        ntlm.setCredentialCharset(charset);
        final String s = ntlm.getResponseFor(challenge, credentials.getUserName(), credentials.getPassword(), credentials.getHost(), credentials.getDomain());
        return "NTLM " + s;
    }
    
    public String authenticate(final Credentials credentials, final String method, final String uri) throws AuthenticationException {
        NTLMScheme.LOG.trace((Object)"enter NTLMScheme.authenticate(Credentials, String, String)");
        NTCredentials ntcredentials = null;
        try {
            ntcredentials = (NTCredentials)credentials;
        }
        catch (final ClassCastException e) {
            throw new InvalidCredentialsException("Credentials cannot be used for NTLM authentication: " + credentials.getClass().getName());
        }
        return authenticate(ntcredentials, this.ntlmchallenge);
    }
    
    public String authenticate(final Credentials credentials, final HttpMethod method) throws AuthenticationException {
        NTLMScheme.LOG.trace((Object)"enter NTLMScheme.authenticate(Credentials, HttpMethod)");
        if (this.state == 0) {
            throw new IllegalStateException("NTLM authentication process has not been initiated");
        }
        NTCredentials ntcredentials = null;
        try {
            ntcredentials = (NTCredentials)credentials;
        }
        catch (final ClassCastException e) {
            throw new InvalidCredentialsException("Credentials cannot be used for NTLM authentication: " + credentials.getClass().getName());
        }
        final NTLM ntlm = new NTLM();
        ntlm.setCredentialCharset(method.getParams().getCredentialCharset());
        String response = null;
        if (this.state == 1 || this.state == Integer.MAX_VALUE) {
            response = ntlm.getType1Message(ntcredentials.getHost(), ntcredentials.getDomain());
            this.state = 2;
        }
        else {
            response = ntlm.getType3Message(ntcredentials.getUserName(), ntcredentials.getPassword(), ntcredentials.getHost(), ntcredentials.getDomain(), ntlm.parseType2Message(this.ntlmchallenge));
            this.state = 4;
        }
        return "NTLM " + response;
    }
    
    static {
        LOG = LogFactory.getLog(NTLMScheme.class);
    }
}
