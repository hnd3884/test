package com.sun.security.sasl.digest;

import javax.security.sasl.AuthorizeCallback;
import java.security.NoSuchAlgorithmException;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.sasl.RealmCallback;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.security.sasl.SaslException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.ArrayList;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import java.util.List;
import javax.security.sasl.SaslServer;

final class DigestMD5Server extends DigestMD5Base implements SaslServer
{
    private static final String MY_CLASS_NAME;
    private static final String UTF8_DIRECTIVE = "charset=utf-8,";
    private static final String ALGORITHM_DIRECTIVE = "algorithm=md5-sess";
    private static final int NONCE_COUNT_VALUE = 1;
    private static final String UTF8_PROPERTY = "com.sun.security.sasl.digest.utf8";
    private static final String REALM_PROPERTY = "com.sun.security.sasl.digest.realm";
    private static final String[] DIRECTIVE_KEY;
    private static final int USERNAME = 0;
    private static final int REALM = 1;
    private static final int NONCE = 2;
    private static final int CNONCE = 3;
    private static final int NONCE_COUNT = 4;
    private static final int QOP = 5;
    private static final int DIGEST_URI = 6;
    private static final int RESPONSE = 7;
    private static final int MAXBUF = 8;
    private static final int CHARSET = 9;
    private static final int CIPHER = 10;
    private static final int AUTHZID = 11;
    private static final int AUTH_PARAM = 12;
    private String specifiedQops;
    private byte[] myCiphers;
    private List<String> serverRealms;
    
    DigestMD5Server(final String s, final String s2, final Map<String, ?> map, final CallbackHandler callbackHandler) throws SaslException {
        super(map, DigestMD5Server.MY_CLASS_NAME, 1, s + "/" + ((s2 == null) ? "*" : s2), callbackHandler);
        this.serverRealms = new ArrayList<String>();
        this.useUTF8 = true;
        if (map != null) {
            this.specifiedQops = (String)map.get("javax.security.sasl.qop");
            if ("false".equals(map.get("com.sun.security.sasl.digest.utf8"))) {
                this.useUTF8 = false;
                DigestMD5Server.logger.log(Level.FINE, "DIGEST80:Server supports ISO-Latin-1");
            }
            final String s3 = (String)map.get("com.sun.security.sasl.digest.realm");
            if (s3 != null) {
                final StringTokenizer stringTokenizer = new StringTokenizer(s3, ", \t\n");
                for (int countTokens = stringTokenizer.countTokens(), i = 0; i < countTokens; ++i) {
                    final String nextToken = stringTokenizer.nextToken();
                    DigestMD5Server.logger.log(Level.FINE, "DIGEST81:Server supports realm {0}", nextToken);
                    this.serverRealms.add(nextToken);
                }
            }
        }
        this.encoding = (this.useUTF8 ? "UTF8" : "8859_1");
        if (this.serverRealms.isEmpty()) {
            if (s2 == null) {
                throw new SaslException("A realm must be provided in props or serverName");
            }
            this.serverRealms.add(s2);
        }
    }
    
    @Override
    public byte[] evaluateResponse(final byte[] array) throws SaslException {
        if (array.length > 4096) {
            throw new SaslException("DIGEST-MD5: Invalid digest response length. Got:  " + array.length + " Expected < " + 4096);
        }
        Label_0180: {
            switch (this.step) {
                case 1: {
                    if (array.length != 0) {
                        throw new SaslException("DIGEST-MD5 must not have an initial response");
                    }
                    String string = null;
                    if ((this.allQop & 0x4) != 0x0) {
                        this.myCiphers = DigestMD5Base.getPlatformCiphers();
                        final StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < DigestMD5Server.CIPHER_TOKENS.length; ++i) {
                            if (this.myCiphers[i] != 0) {
                                if (sb.length() > 0) {
                                    sb.append(',');
                                }
                                sb.append(DigestMD5Server.CIPHER_TOKENS[i]);
                            }
                        }
                        string = sb.toString();
                    }
                    break Label_0180;
                }
                case 3: {
                    byte[] validateClientResponse = null;
                    Label_0229: {
                        break Label_0229;
                        try {
                            final String string;
                            final byte[] generateChallenge = this.generateChallenge(this.serverRealms, this.specifiedQops, string);
                            this.step = 3;
                            return generateChallenge;
                        }
                        catch (final UnsupportedEncodingException ex) {
                            throw new SaslException("DIGEST-MD5: Error encoding challenge", ex);
                        }
                        catch (final IOException ex2) {
                            throw new SaslException("DIGEST-MD5: Error generating challenge", ex2);
                        }
                        try {
                            validateClientResponse = this.validateClientResponse(DigestMD5Base.parseDirectives(array, DigestMD5Server.DIRECTIVE_KEY, null, 1));
                        }
                        catch (final SaslException ex3) {
                            throw ex3;
                        }
                        catch (final UnsupportedEncodingException ex4) {
                            throw new SaslException("DIGEST-MD5: Error validating client response", ex4);
                        }
                        finally {
                            this.step = 0;
                        }
                    }
                    this.completed = true;
                    if (this.integrity && this.privacy) {
                        this.secCtx = new DigestPrivacy(false);
                    }
                    else if (this.integrity) {
                        this.secCtx = new DigestIntegrity(false);
                    }
                    return validateClientResponse;
                }
                default: {
                    throw new SaslException("DIGEST-MD5: Server at illegal state");
                }
            }
        }
    }
    
    private byte[] generateChallenge(final List<String> list, final String s, final String s2) throws UnsupportedEncodingException, IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int n = 0; list != null && n < list.size(); ++n) {
            byteArrayOutputStream.write("realm=\"".getBytes(this.encoding));
            DigestMD5Base.writeQuotedStringValue(byteArrayOutputStream, ((String)list.get(n)).getBytes(this.encoding));
            byteArrayOutputStream.write(34);
            byteArrayOutputStream.write(44);
        }
        byteArrayOutputStream.write("nonce=\"".getBytes(this.encoding));
        DigestMD5Base.writeQuotedStringValue(byteArrayOutputStream, this.nonce = DigestMD5Base.generateNonce());
        byteArrayOutputStream.write(34);
        byteArrayOutputStream.write(44);
        if (s != null) {
            byteArrayOutputStream.write("qop=\"".getBytes(this.encoding));
            DigestMD5Base.writeQuotedStringValue(byteArrayOutputStream, s.getBytes(this.encoding));
            byteArrayOutputStream.write(34);
            byteArrayOutputStream.write(44);
        }
        if (this.recvMaxBufSize != 65536) {
            byteArrayOutputStream.write(("maxbuf=\"" + this.recvMaxBufSize + "\",").getBytes(this.encoding));
        }
        if (this.useUTF8) {
            byteArrayOutputStream.write("charset=utf-8,".getBytes(this.encoding));
        }
        if (s2 != null) {
            byteArrayOutputStream.write("cipher=\"".getBytes(this.encoding));
            DigestMD5Base.writeQuotedStringValue(byteArrayOutputStream, s2.getBytes(this.encoding));
            byteArrayOutputStream.write(34);
            byteArrayOutputStream.write(44);
        }
        byteArrayOutputStream.write("algorithm=md5-sess".getBytes(this.encoding));
        return byteArrayOutputStream.toByteArray();
    }
    
    private byte[] validateClientResponse(final byte[][] array) throws SaslException, UnsupportedEncodingException {
        if (array[9] != null && (!this.useUTF8 || !"utf-8".equals(new String(array[9], this.encoding)))) {
            throw new SaslException("DIGEST-MD5: digest response format violation. Incompatible charset value: " + new String(array[9]));
        }
        final int n = (array[8] == null) ? 65536 : Integer.parseInt(new String(array[8], this.encoding));
        this.sendMaxBufSize = ((this.sendMaxBufSize == 0) ? n : Math.min(this.sendMaxBufSize, n));
        if (array[0] == null) {
            throw new SaslException("DIGEST-MD5: digest response format violation. Missing username.");
        }
        final String s = new String(array[0], this.encoding);
        DigestMD5Server.logger.log(Level.FINE, "DIGEST82:Username: {0}", s);
        this.negotiatedRealm = ((array[1] != null) ? new String(array[1], this.encoding) : "");
        DigestMD5Server.logger.log(Level.FINE, "DIGEST83:Client negotiated realm: {0}", this.negotiatedRealm);
        if (!this.serverRealms.contains(this.negotiatedRealm)) {
            throw new SaslException("DIGEST-MD5: digest response format violation. Nonexistent realm: " + this.negotiatedRealm);
        }
        if (array[2] == null) {
            throw new SaslException("DIGEST-MD5: digest response format violation. Missing nonce.");
        }
        if (!Arrays.equals(array[2], this.nonce)) {
            throw new SaslException("DIGEST-MD5: digest response format violation. Mismatched nonce.");
        }
        if (array[3] == null) {
            throw new SaslException("DIGEST-MD5: digest response format violation. Missing cnonce.");
        }
        final byte[] array2 = array[3];
        if (array[4] != null && 1 != Integer.parseInt(new String(array[4], this.encoding), 16)) {
            throw new SaslException("DIGEST-MD5: digest response format violation. Nonce count does not match: " + new String(array[4]));
        }
        this.negotiatedQop = ((array[5] != null) ? new String(array[5], this.encoding) : "auth");
        DigestMD5Server.logger.log(Level.FINE, "DIGEST84:Client negotiated qop: {0}", this.negotiatedQop);
        final String negotiatedQop = this.negotiatedQop;
        byte b = 0;
        switch (negotiatedQop) {
            case "auth": {
                b = 1;
                break;
            }
            case "auth-int": {
                b = 2;
                this.integrity = true;
                this.rawSendSize = this.sendMaxBufSize - 16;
                break;
            }
            case "auth-conf": {
                b = 4;
                final boolean b2 = true;
                this.privacy = b2;
                this.integrity = b2;
                this.rawSendSize = this.sendMaxBufSize - 26;
                break;
            }
            default: {
                throw new SaslException("DIGEST-MD5: digest response format violation. Invalid QOP: " + this.negotiatedQop);
            }
        }
        if ((b & this.allQop) == 0x0) {
            throw new SaslException("DIGEST-MD5: server does not support  qop: " + this.negotiatedQop);
        }
        if (this.privacy) {
            this.negotiatedCipher = ((array[10] != null) ? new String(array[10], this.encoding) : null);
            if (this.negotiatedCipher == null) {
                throw new SaslException("DIGEST-MD5: digest response format violation. No cipher specified.");
            }
            int n3 = -1;
            DigestMD5Server.logger.log(Level.FINE, "DIGEST85:Client negotiated cipher: {0}", this.negotiatedCipher);
            for (int i = 0; i < DigestMD5Server.CIPHER_TOKENS.length; ++i) {
                if (this.negotiatedCipher.equals(DigestMD5Server.CIPHER_TOKENS[i]) && this.myCiphers[i] != 0) {
                    n3 = i;
                    break;
                }
            }
            if (n3 == -1) {
                throw new SaslException("DIGEST-MD5: server does not support cipher: " + this.negotiatedCipher);
            }
            if ((DigestMD5Server.CIPHER_MASKS[n3] & 0x4) != 0x0) {
                this.negotiatedStrength = "high";
            }
            else if ((DigestMD5Server.CIPHER_MASKS[n3] & 0x2) != 0x0) {
                this.negotiatedStrength = "medium";
            }
            else {
                this.negotiatedStrength = "low";
            }
            DigestMD5Server.logger.log(Level.FINE, "DIGEST86:Negotiated strength: {0}", this.negotiatedStrength);
        }
        final String digestUri = (array[6] != null) ? new String(array[6], this.encoding) : null;
        if (digestUri != null) {
            DigestMD5Server.logger.log(Level.FINE, "DIGEST87:digest URI: {0}", digestUri);
        }
        if (!uriMatches(this.digestUri, digestUri)) {
            throw new SaslException("DIGEST-MD5: digest response format violation. Mismatched URI: " + digestUri + "; expecting: " + this.digestUri);
        }
        this.digestUri = digestUri;
        final byte[] array3 = array[7];
        if (array3 == null) {
            throw new SaslException("DIGEST-MD5: digest response format  violation. Missing response.");
        }
        final byte[] array4;
        final String s2 = ((array4 = array[11]) != null) ? new String(array4, this.encoding) : s;
        if (array4 != null) {
            DigestMD5Server.logger.log(Level.FINE, "DIGEST88:Authzid: {0}", new String(array4));
        }
        char[] password;
        try {
            final RealmCallback realmCallback = new RealmCallback("DIGEST-MD5 realm: ", this.negotiatedRealm);
            final NameCallback nameCallback = new NameCallback("DIGEST-MD5 authentication ID: ", s);
            final PasswordCallback passwordCallback = new PasswordCallback("DIGEST-MD5 password: ", false);
            this.cbh.handle(new Callback[] { realmCallback, nameCallback, passwordCallback });
            password = passwordCallback.getPassword();
            passwordCallback.clearPassword();
        }
        catch (final UnsupportedCallbackException ex) {
            throw new SaslException("DIGEST-MD5: Cannot perform callback to acquire password", ex);
        }
        catch (final IOException ex2) {
            throw new SaslException("DIGEST-MD5: IO error acquiring password", ex2);
        }
        if (password == null) {
            throw new SaslException("DIGEST-MD5: cannot acquire password for " + s + " in realm : " + this.negotiatedRealm);
        }
        try {
            byte[] generateResponseValue;
            try {
                generateResponseValue = this.generateResponseValue("AUTHENTICATE", this.digestUri, this.negotiatedQop, s, this.negotiatedRealm, password, this.nonce, array2, 1, array4);
            }
            catch (final NoSuchAlgorithmException ex3) {
                throw new SaslException("DIGEST-MD5: problem duplicating client response", ex3);
            }
            catch (final IOException ex4) {
                throw new SaslException("DIGEST-MD5: problem duplicating client response", ex4);
            }
            if (!Arrays.equals(array3, generateResponseValue)) {
                throw new SaslException("DIGEST-MD5: digest response format violation. Mismatched response.");
            }
            try {
                final AuthorizeCallback authorizeCallback = new AuthorizeCallback(s, s2);
                this.cbh.handle(new Callback[] { authorizeCallback });
                if (!authorizeCallback.isAuthorized()) {
                    throw new SaslException("DIGEST-MD5: " + s + " is not authorized to act as " + s2);
                }
                this.authzid = authorizeCallback.getAuthorizedID();
            }
            catch (final SaslException ex5) {
                throw ex5;
            }
            catch (final UnsupportedCallbackException ex6) {
                throw new SaslException("DIGEST-MD5: Cannot perform callback to check authzid", ex6);
            }
            catch (final IOException ex7) {
                throw new SaslException("DIGEST-MD5: IO error checking authzid", ex7);
            }
            return this.generateResponseAuth(s, password, array2, 1, array4);
        }
        finally {
            for (int j = 0; j < password.length; ++j) {
                password[j] = '\0';
            }
        }
    }
    
    private static boolean uriMatches(final String s, final String s2) {
        if (s.equalsIgnoreCase(s2)) {
            return true;
        }
        if (s.endsWith("/*")) {
            final int n = s.length() - 1;
            return s.substring(0, n).equalsIgnoreCase(s2.substring(0, n));
        }
        return false;
    }
    
    private byte[] generateResponseAuth(final String s, final char[] array, final byte[] array2, final int n, final byte[] array3) throws SaslException {
        try {
            final byte[] generateResponseValue = this.generateResponseValue("", this.digestUri, this.negotiatedQop, s, this.negotiatedRealm, array, this.nonce, array2, n, array3);
            final byte[] array4 = new byte[generateResponseValue.length + 8];
            System.arraycopy("rspauth=".getBytes(this.encoding), 0, array4, 0, 8);
            System.arraycopy(generateResponseValue, 0, array4, 8, generateResponseValue.length);
            return array4;
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new SaslException("DIGEST-MD5: problem generating response", ex);
        }
        catch (final IOException ex2) {
            throw new SaslException("DIGEST-MD5: problem generating response", ex2);
        }
    }
    
    @Override
    public String getAuthorizationID() {
        if (this.completed) {
            return this.authzid;
        }
        throw new IllegalStateException("DIGEST-MD5 server negotiation not complete");
    }
    
    static {
        MY_CLASS_NAME = DigestMD5Server.class.getName();
        DIRECTIVE_KEY = new String[] { "username", "realm", "nonce", "cnonce", "nonce-count", "qop", "digest-uri", "response", "maxbuf", "charset", "cipher", "authzid", "auth-param" };
    }
}
