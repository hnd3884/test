package org.apache.tomcat.websocket;

import org.apache.tomcat.util.security.MD5Encoder;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.security.SecureRandom;

public class DigestAuthenticator extends Authenticator
{
    public static final String schemeName = "digest";
    private static final Object cnonceGeneratorLock;
    private static volatile SecureRandom cnonceGenerator;
    private int nonceCount;
    private long cNonce;
    
    public DigestAuthenticator() {
        this.nonceCount = 0;
    }
    
    @Override
    public String getAuthorization(final String requestUri, final String WWWAuthenticate, final Map<String, Object> userProperties) throws AuthenticationException {
        final String userName = userProperties.get("org.apache.tomcat.websocket.WS_AUTHENTICATION_USER_NAME");
        final String password = userProperties.get("org.apache.tomcat.websocket.WS_AUTHENTICATION_PASSWORD");
        if (userName == null || password == null) {
            throw new AuthenticationException("Failed to perform Digest authentication due to  missing user/password");
        }
        final Map<String, String> wwwAuthenticate = this.parseWWWAuthenticateHeader(WWWAuthenticate);
        final String realm = wwwAuthenticate.get("realm");
        final String nonce = wwwAuthenticate.get("nonce");
        final String messageQop = wwwAuthenticate.get("qop");
        final String algorithm = (wwwAuthenticate.get("algorithm") == null) ? "MD5" : wwwAuthenticate.get("algorithm");
        final String opaque = wwwAuthenticate.get("opaque");
        final StringBuilder challenge = new StringBuilder();
        if (!messageQop.isEmpty()) {
            if (DigestAuthenticator.cnonceGenerator == null) {
                synchronized (DigestAuthenticator.cnonceGeneratorLock) {
                    if (DigestAuthenticator.cnonceGenerator == null) {
                        DigestAuthenticator.cnonceGenerator = new SecureRandom();
                    }
                }
            }
            this.cNonce = DigestAuthenticator.cnonceGenerator.nextLong();
            ++this.nonceCount;
        }
        challenge.append("Digest ");
        challenge.append("username =\"" + userName + "\",");
        challenge.append("realm=\"" + realm + "\",");
        challenge.append("nonce=\"" + nonce + "\",");
        challenge.append("uri=\"" + requestUri + "\",");
        try {
            challenge.append("response=\"" + this.calculateRequestDigest(requestUri, userName, password, realm, nonce, messageQop, algorithm) + "\",");
        }
        catch (final NoSuchAlgorithmException e) {
            throw new AuthenticationException("Unable to generate request digest " + e.getMessage());
        }
        challenge.append("algorithm=" + algorithm + ",");
        challenge.append("opaque=\"" + opaque + "\",");
        if (!messageQop.isEmpty()) {
            challenge.append("qop=\"" + messageQop + "\"");
            challenge.append(",cnonce=\"" + this.cNonce + "\",");
            challenge.append("nc=" + String.format("%08X", this.nonceCount));
        }
        return challenge.toString();
    }
    
    private String calculateRequestDigest(final String requestUri, final String userName, final String password, final String realm, final String nonce, final String qop, final String algorithm) throws NoSuchAlgorithmException {
        final StringBuilder preDigest = new StringBuilder();
        String A1;
        if (algorithm.equalsIgnoreCase("MD5")) {
            A1 = userName + ":" + realm + ":" + password;
        }
        else {
            A1 = this.encodeMD5(userName + ":" + realm + ":" + password) + ":" + nonce + ":" + this.cNonce;
        }
        final String A2 = "GET:" + requestUri;
        preDigest.append(this.encodeMD5(A1));
        preDigest.append(':');
        preDigest.append(nonce);
        if (qop.toLowerCase().contains("auth")) {
            preDigest.append(':');
            preDigest.append(String.format("%08X", this.nonceCount));
            preDigest.append(':');
            preDigest.append(String.valueOf(this.cNonce));
            preDigest.append(':');
            preDigest.append(qop);
        }
        preDigest.append(':');
        preDigest.append(this.encodeMD5(A2));
        return this.encodeMD5(preDigest.toString());
    }
    
    private String encodeMD5(final String value) throws NoSuchAlgorithmException {
        final byte[] bytesOfMessage = value.getBytes(StandardCharsets.ISO_8859_1);
        final MessageDigest md = MessageDigest.getInstance("MD5");
        final byte[] thedigest = md.digest(bytesOfMessage);
        return MD5Encoder.encode(thedigest);
    }
    
    @Override
    public String getSchemeName() {
        return "digest";
    }
    
    static {
        cnonceGeneratorLock = new Object();
    }
}
