package com.sun.security.sasl.digest;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;
import com.sun.security.sasl.util.AbstractSaslImpl;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmChoiceCallback;
import javax.security.auth.callback.Callback;
import javax.security.sasl.RealmCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.io.UnsupportedEncodingException;
import javax.security.sasl.SaslException;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import javax.security.sasl.SaslClient;

final class DigestMD5Client extends DigestMD5Base implements SaslClient
{
    private static final String MY_CLASS_NAME;
    private static final String CIPHER_PROPERTY = "com.sun.security.sasl.digest.cipher";
    private static final String[] DIRECTIVE_KEY;
    private static final int REALM = 0;
    private static final int QOP = 1;
    private static final int ALGORITHM = 2;
    private static final int NONCE = 3;
    private static final int MAXBUF = 4;
    private static final int CHARSET = 5;
    private static final int CIPHER = 6;
    private static final int RESPONSE_AUTH = 7;
    private static final int STALE = 8;
    private int nonceCount;
    private String specifiedCipher;
    private byte[] cnonce;
    private String username;
    private char[] passwd;
    private byte[] authzidBytes;
    
    DigestMD5Client(final String authzid, final String s, final String s2, final Map<String, ?> map, final CallbackHandler callbackHandler) throws SaslException {
        super(map, DigestMD5Client.MY_CLASS_NAME, 2, s + "/" + s2, callbackHandler);
        if (authzid != null) {
            this.authzid = authzid;
            try {
                this.authzidBytes = authzid.getBytes("UTF8");
            }
            catch (final UnsupportedEncodingException ex) {
                throw new SaslException("DIGEST-MD5: Error encoding authzid value into UTF-8", ex);
            }
        }
        if (map != null) {
            this.specifiedCipher = (String)map.get("com.sun.security.sasl.digest.cipher");
            DigestMD5Client.logger.log(Level.FINE, "DIGEST60:Explicitly specified cipher: {0}", this.specifiedCipher);
        }
    }
    
    @Override
    public boolean hasInitialResponse() {
        return false;
    }
    
    @Override
    public byte[] evaluateChallenge(final byte[] array) throws SaslException {
        if (array.length > 2048) {
            throw new SaslException("DIGEST-MD5: Invalid digest-challenge length. Got:  " + array.length + " Expected < " + 2048);
        }
        switch (this.step) {
            case 2: {
                final ArrayList list = new ArrayList(3);
                final byte[][] directives = DigestMD5Base.parseDirectives(array, DigestMD5Client.DIRECTIVE_KEY, list, 0);
                try {
                    this.processChallenge(directives, list);
                    this.checkQopSupport(directives[1], directives[6]);
                    ++this.step;
                    return this.generateClientResponse(directives[5]);
                }
                catch (final SaslException ex) {
                    this.step = 0;
                    this.clearPassword();
                    throw ex;
                }
                catch (final IOException ex2) {
                    this.step = 0;
                    this.clearPassword();
                    throw new SaslException("DIGEST-MD5: Error generating digest response-value", ex2);
                }
            }
            case 3: {
                try {
                    this.validateResponseValue(DigestMD5Base.parseDirectives(array, DigestMD5Client.DIRECTIVE_KEY, null, 0)[7]);
                    if (this.integrity && this.privacy) {
                        this.secCtx = new DigestPrivacy(true);
                    }
                    else if (this.integrity) {
                        this.secCtx = new DigestIntegrity(true);
                    }
                    return null;
                }
                finally {
                    this.clearPassword();
                    this.step = 0;
                    this.completed = true;
                }
                break;
            }
        }
        throw new SaslException("DIGEST-MD5: Client at illegal state");
    }
    
    private void processChallenge(final byte[][] array, final List<byte[]> list) throws SaslException, UnsupportedEncodingException {
        if (array[5] != null) {
            if (!"utf-8".equals(new String(array[5], this.encoding))) {
                throw new SaslException("DIGEST-MD5: digest-challenge format violation. Unrecognised charset value: " + new String(array[5]));
            }
            this.encoding = "UTF8";
            this.useUTF8 = true;
        }
        if (array[2] == null) {
            throw new SaslException("DIGEST-MD5: Digest-challenge format violation: algorithm directive missing");
        }
        if (!"md5-sess".equals(new String(array[2], this.encoding))) {
            throw new SaslException("DIGEST-MD5: Digest-challenge format violation. Invalid value for 'algorithm' directive: " + array[2]);
        }
        if (array[3] == null) {
            throw new SaslException("DIGEST-MD5: Digest-challenge format violation: nonce directive missing");
        }
        this.nonce = array[3];
        try {
            String[] array2 = null;
            if (array[0] != null) {
                if (list == null || list.size() <= 1) {
                    this.negotiatedRealm = new String(array[0], this.encoding);
                }
                else {
                    array2 = new String[list.size()];
                    for (int i = 0; i < array2.length; ++i) {
                        array2[i] = new String((byte[])list.get(i), this.encoding);
                    }
                }
            }
            final NameCallback nameCallback = (this.authzid == null) ? new NameCallback("DIGEST-MD5 authentication ID: ") : new NameCallback("DIGEST-MD5 authentication ID: ", this.authzid);
            final PasswordCallback passwordCallback = new PasswordCallback("DIGEST-MD5 password: ", false);
            if (array2 == null) {
                final RealmCallback realmCallback = (this.negotiatedRealm == null) ? new RealmCallback("DIGEST-MD5 realm: ") : new RealmCallback("DIGEST-MD5 realm: ", this.negotiatedRealm);
                this.cbh.handle(new Callback[] { realmCallback, nameCallback, passwordCallback });
                this.negotiatedRealm = realmCallback.getText();
                if (this.negotiatedRealm == null) {
                    this.negotiatedRealm = "";
                }
            }
            else {
                final RealmChoiceCallback realmChoiceCallback = new RealmChoiceCallback("DIGEST-MD5 realm: ", array2, 0, false);
                this.cbh.handle(new Callback[] { realmChoiceCallback, nameCallback, passwordCallback });
                final int[] selectedIndexes = realmChoiceCallback.getSelectedIndexes();
                if (selectedIndexes == null || selectedIndexes[0] < 0 || selectedIndexes[0] >= array2.length) {
                    throw new SaslException("DIGEST-MD5: Invalid realm chosen");
                }
                this.negotiatedRealm = array2[selectedIndexes[0]];
            }
            this.passwd = passwordCallback.getPassword();
            passwordCallback.clearPassword();
            this.username = nameCallback.getName();
        }
        catch (final SaslException ex) {
            throw ex;
        }
        catch (final UnsupportedCallbackException ex2) {
            throw new SaslException("DIGEST-MD5: Cannot perform callback to acquire realm, authentication ID or password", ex2);
        }
        catch (final IOException ex3) {
            throw new SaslException("DIGEST-MD5: Error acquiring realm, authentication ID or password", ex3);
        }
        if (this.username == null || this.passwd == null) {
            throw new SaslException("DIGEST-MD5: authentication ID and password must be specified");
        }
        final int n = (array[4] == null) ? 65536 : Integer.parseInt(new String(array[4], this.encoding));
        this.sendMaxBufSize = ((this.sendMaxBufSize == 0) ? n : Math.min(this.sendMaxBufSize, n));
    }
    
    private void checkQopSupport(final byte[] array, final byte[] array2) throws IOException {
        String s;
        if (array == null) {
            s = "auth";
        }
        else {
            s = new String(array, this.encoding);
        }
        switch (AbstractSaslImpl.findPreferredMask(AbstractSaslImpl.combineMasks(AbstractSaslImpl.parseQop(s, new String[3], true)), this.qop)) {
            case 0: {
                throw new SaslException("DIGEST-MD5: No common protection layer between client and server");
            }
            case 1: {
                this.negotiatedQop = "auth";
                break;
            }
            case 2: {
                this.negotiatedQop = "auth-int";
                this.integrity = true;
                this.rawSendSize = this.sendMaxBufSize - 16;
                break;
            }
            case 4: {
                this.negotiatedQop = "auth-conf";
                final boolean b = true;
                this.integrity = b;
                this.privacy = b;
                this.rawSendSize = this.sendMaxBufSize - 26;
                this.checkStrengthSupport(array2);
                break;
            }
        }
        if (DigestMD5Client.logger.isLoggable(Level.FINE)) {
            DigestMD5Client.logger.log(Level.FINE, "DIGEST61:Raw send size: {0}", new Integer(this.rawSendSize));
        }
    }
    
    private void checkStrengthSupport(final byte[] array) throws IOException {
        if (array == null) {
            throw new SaslException("DIGEST-MD5: server did not specify cipher to use for 'auth-conf'");
        }
        final String s = new String(array, this.encoding);
        final StringTokenizer stringTokenizer = new StringTokenizer(s, ", \t\n");
        final int countTokens = stringTokenizer.countTokens();
        final byte[] array2 = { 0, 0, 0, 0, 0 };
        final String[] array3 = new String[array2.length];
        for (int i = 0; i < countTokens; ++i) {
            final String nextToken = stringTokenizer.nextToken();
            for (int j = 0; j < DigestMD5Client.CIPHER_TOKENS.length; ++j) {
                if (nextToken.equals(DigestMD5Client.CIPHER_TOKENS[j])) {
                    final byte[] array4 = array2;
                    final int n = j;
                    array4[n] |= DigestMD5Client.CIPHER_MASKS[j];
                    array3[j] = nextToken;
                    DigestMD5Client.logger.log(Level.FINE, "DIGEST62:Server supports {0}", nextToken);
                }
            }
        }
        final byte[] platformCiphers = DigestMD5Base.getPlatformCiphers();
        byte b = 0;
        for (int k = 0; k < array2.length; ++k) {
            final byte[] array5 = array2;
            final int n2 = k;
            array5[n2] &= platformCiphers[k];
            b |= array2[k];
        }
        if (b == 0) {
            throw new SaslException("DIGEST-MD5: Client supports none of these cipher suites: " + s);
        }
        this.negotiatedCipher = this.findCipherAndStrength(array2, array3);
        if (this.negotiatedCipher == null) {
            throw new SaslException("DIGEST-MD5: Unable to negotiate a strength level for 'auth-conf'");
        }
        DigestMD5Client.logger.log(Level.FINE, "DIGEST63:Cipher suite: {0}", this.negotiatedCipher);
    }
    
    private String findCipherAndStrength(final byte[] array, final String[] array2) {
        for (int i = 0; i < this.strength.length; ++i) {
            final byte b;
            if ((b = this.strength[i]) != 0) {
                for (int j = 0; j < array.length; ++j) {
                    if (b == array[j] && (this.specifiedCipher == null || this.specifiedCipher.equals(array2[j]))) {
                        switch (b) {
                            case 4: {
                                this.negotiatedStrength = "high";
                                break;
                            }
                            case 2: {
                                this.negotiatedStrength = "medium";
                                break;
                            }
                            case 1: {
                                this.negotiatedStrength = "low";
                                break;
                            }
                        }
                        return array2[j];
                    }
                }
            }
        }
        return null;
    }
    
    private byte[] generateClientResponse(final byte[] array) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (this.useUTF8) {
            byteArrayOutputStream.write("charset=".getBytes(this.encoding));
            byteArrayOutputStream.write(array);
            byteArrayOutputStream.write(44);
        }
        byteArrayOutputStream.write(("username=\"" + DigestMD5Base.quotedStringValue(this.username) + "\",").getBytes(this.encoding));
        if (this.negotiatedRealm.length() > 0) {
            byteArrayOutputStream.write(("realm=\"" + DigestMD5Base.quotedStringValue(this.negotiatedRealm) + "\",").getBytes(this.encoding));
        }
        byteArrayOutputStream.write("nonce=\"".getBytes(this.encoding));
        DigestMD5Base.writeQuotedStringValue(byteArrayOutputStream, this.nonce);
        byteArrayOutputStream.write(34);
        byteArrayOutputStream.write(44);
        this.nonceCount = getNonceCount(this.nonce);
        byteArrayOutputStream.write(("nc=" + DigestMD5Base.nonceCountToHex(this.nonceCount) + ",").getBytes(this.encoding));
        this.cnonce = DigestMD5Base.generateNonce();
        byteArrayOutputStream.write("cnonce=\"".getBytes(this.encoding));
        DigestMD5Base.writeQuotedStringValue(byteArrayOutputStream, this.cnonce);
        byteArrayOutputStream.write("\",".getBytes(this.encoding));
        byteArrayOutputStream.write(("digest-uri=\"" + this.digestUri + "\",").getBytes(this.encoding));
        byteArrayOutputStream.write("maxbuf=".getBytes(this.encoding));
        byteArrayOutputStream.write(String.valueOf(this.recvMaxBufSize).getBytes(this.encoding));
        byteArrayOutputStream.write(44);
        try {
            byteArrayOutputStream.write("response=".getBytes(this.encoding));
            byteArrayOutputStream.write(this.generateResponseValue("AUTHENTICATE", this.digestUri, this.negotiatedQop, this.username, this.negotiatedRealm, this.passwd, this.nonce, this.cnonce, this.nonceCount, this.authzidBytes));
            byteArrayOutputStream.write(44);
        }
        catch (final Exception ex) {
            throw new SaslException("DIGEST-MD5: Error generating response value", ex);
        }
        byteArrayOutputStream.write(("qop=" + this.negotiatedQop).getBytes(this.encoding));
        if (this.negotiatedCipher != null) {
            byteArrayOutputStream.write((",cipher=\"" + this.negotiatedCipher + "\"").getBytes(this.encoding));
        }
        if (this.authzidBytes != null) {
            byteArrayOutputStream.write(",authzid=\"".getBytes(this.encoding));
            DigestMD5Base.writeQuotedStringValue(byteArrayOutputStream, this.authzidBytes);
            byteArrayOutputStream.write("\"".getBytes(this.encoding));
        }
        if (byteArrayOutputStream.size() > 4096) {
            throw new SaslException("DIGEST-MD5: digest-response size too large. Length: " + byteArrayOutputStream.size());
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    private void validateResponseValue(final byte[] array) throws SaslException {
        if (array == null) {
            throw new SaslException("DIGEST-MD5: Authenication failed. Expecting 'rspauth' authentication success message");
        }
        try {
            if (!Arrays.equals(this.generateResponseValue("", this.digestUri, this.negotiatedQop, this.username, this.negotiatedRealm, this.passwd, this.nonce, this.cnonce, this.nonceCount, this.authzidBytes), array)) {
                throw new SaslException("Server's rspauth value does not match what client expects");
            }
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new SaslException("Problem generating response value for verification", ex);
        }
        catch (final IOException ex2) {
            throw new SaslException("Problem generating response value for verification", ex2);
        }
    }
    
    private static int getNonceCount(final byte[] array) {
        return 1;
    }
    
    private void clearPassword() {
        if (this.passwd != null) {
            for (int i = 0; i < this.passwd.length; ++i) {
                this.passwd[i] = '\0';
            }
            this.passwd = null;
        }
    }
    
    static {
        MY_CLASS_NAME = DigestMD5Client.class.getName();
        DIRECTIVE_KEY = new String[] { "realm", "qop", "algorithm", "nonce", "maxbuf", "charset", "cipher", "rspauth", "stale" };
    }
}
