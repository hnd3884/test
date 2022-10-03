package org.apache.catalina.authenticator;

import org.apache.tomcat.util.codec.binary.Base64;
import java.io.IOException;
import java.security.Principal;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.Request;
import java.nio.charset.StandardCharsets;
import org.apache.juli.logging.LogFactory;
import java.nio.charset.Charset;
import org.apache.juli.logging.Log;

public class BasicAuthenticator extends AuthenticatorBase
{
    private final Log log;
    private Charset charset;
    private String charsetString;
    private boolean trimCredentials;
    
    public BasicAuthenticator() {
        this.log = LogFactory.getLog((Class)BasicAuthenticator.class);
        this.charset = StandardCharsets.ISO_8859_1;
        this.charsetString = null;
        this.trimCredentials = true;
    }
    
    public String getCharset() {
        return this.charsetString;
    }
    
    public void setCharset(final String charsetString) {
        if (charsetString == null || charsetString.isEmpty()) {
            this.charset = StandardCharsets.ISO_8859_1;
        }
        else {
            if (!"UTF-8".equalsIgnoreCase(charsetString)) {
                throw new IllegalArgumentException(BasicAuthenticator.sm.getString("basicAuthenticator.invalidCharset"));
            }
            this.charset = StandardCharsets.UTF_8;
        }
        this.charsetString = charsetString;
    }
    
    public boolean getTrimCredentials() {
        return this.trimCredentials;
    }
    
    public void setTrimCredentials(final boolean trimCredentials) {
        this.trimCredentials = trimCredentials;
    }
    
    @Override
    protected boolean doAuthenticate(final Request request, final HttpServletResponse response) throws IOException {
        if (this.checkForCachedAuthentication(request, response, true)) {
            return true;
        }
        final MessageBytes authorization = request.getCoyoteRequest().getMimeHeaders().getValue("authorization");
        if (authorization != null) {
            authorization.toBytes();
            final ByteChunk authorizationBC = authorization.getByteChunk();
            BasicCredentials credentials = null;
            try {
                credentials = new BasicCredentials(authorizationBC, this.charset, this.getTrimCredentials());
                final String username = credentials.getUsername();
                final String password = credentials.getPassword();
                final Principal principal = this.context.getRealm().authenticate(username, password);
                if (principal != null) {
                    this.register(request, response, principal, "BASIC", username, password);
                    return true;
                }
            }
            catch (final IllegalArgumentException iae) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("Invalid Authorization" + iae.getMessage()));
                }
            }
        }
        final StringBuilder value = new StringBuilder(16);
        value.append("Basic realm=\"");
        value.append(AuthenticatorBase.getRealmName(this.context));
        value.append('\"');
        if (this.charsetString != null && !this.charsetString.isEmpty()) {
            value.append(", charset=");
            value.append(this.charsetString);
        }
        response.setHeader("WWW-Authenticate", value.toString());
        response.sendError(401);
        return false;
    }
    
    @Override
    protected String getAuthMethod() {
        return "BASIC";
    }
    
    @Override
    protected boolean isPreemptiveAuthPossible(final Request request) {
        final MessageBytes authorizationHeader = request.getCoyoteRequest().getMimeHeaders().getValue("authorization");
        return authorizationHeader != null && authorizationHeader.startsWithIgnoreCase("basic ", 0);
    }
    
    public static class BasicCredentials
    {
        private static final String METHOD = "basic ";
        private final Charset charset;
        private final boolean trimCredentials;
        private final ByteChunk authorization;
        private final int initialOffset;
        private int base64blobOffset;
        private int base64blobLength;
        private String username;
        private String password;
        
        @Deprecated
        public BasicCredentials(final ByteChunk input, final Charset charset) throws IllegalArgumentException {
            this(input, charset, true);
        }
        
        public BasicCredentials(final ByteChunk input, final Charset charset, final boolean trimCredentials) throws IllegalArgumentException {
            this.username = null;
            this.password = null;
            this.authorization = input;
            this.initialOffset = input.getOffset();
            this.charset = charset;
            this.trimCredentials = trimCredentials;
            this.parseMethod();
            final byte[] decoded = this.parseBase64();
            this.parseCredentials(decoded);
        }
        
        public String getUsername() {
            return this.username;
        }
        
        public String getPassword() {
            return this.password;
        }
        
        private void parseMethod() throws IllegalArgumentException {
            if (this.authorization.startsWithIgnoreCase("basic ", 0)) {
                this.base64blobOffset = this.initialOffset + "basic ".length();
                this.base64blobLength = this.authorization.getLength() - "basic ".length();
                return;
            }
            throw new IllegalArgumentException("Authorization header method is not \"Basic\"");
        }
        
        private byte[] parseBase64() throws IllegalArgumentException {
            final byte[] decoded = Base64.decodeBase64(this.authorization.getBuffer(), this.base64blobOffset, this.base64blobLength);
            this.authorization.setOffset(this.initialOffset);
            if (decoded == null) {
                throw new IllegalArgumentException("Basic Authorization credentials are not Base64");
            }
            return decoded;
        }
        
        private void parseCredentials(final byte[] decoded) throws IllegalArgumentException {
            int colon = -1;
            for (int i = 0; i < decoded.length; ++i) {
                if (decoded[i] == 58) {
                    colon = i;
                    break;
                }
            }
            if (colon < 0) {
                this.username = new String(decoded, this.charset);
            }
            else {
                this.username = new String(decoded, 0, colon, this.charset);
                this.password = new String(decoded, colon + 1, decoded.length - colon - 1, this.charset);
                if (this.password.length() > 1 && this.trimCredentials) {
                    this.password = this.password.trim();
                }
            }
            if (this.username.length() > 1 && this.trimCredentials) {
                this.username = this.username.trim();
            }
        }
    }
}
