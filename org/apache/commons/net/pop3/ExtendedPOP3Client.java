package org.apache.commons.net.pop3;

import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;
import java.io.IOException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import org.apache.commons.net.util.Base64;
import java.security.NoSuchAlgorithmException;

public class ExtendedPOP3Client extends POP3SClient
{
    public ExtendedPOP3Client() throws NoSuchAlgorithmException {
    }
    
    public boolean auth(final AUTH_METHOD method, final String username, final String password) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        if (this.sendCommand(13, method.getAuthName()) != 2) {
            return false;
        }
        switch (method) {
            case PLAIN: {
                return this.sendCommand(new String(Base64.encodeBase64(("\u0000" + username + "\u0000" + password).getBytes(this.getCharset())), this.getCharset())) == 0;
            }
            case CRAM_MD5: {
                final byte[] serverChallenge = Base64.decodeBase64(this.getReplyString().substring(2).trim());
                final Mac hmac_md5 = Mac.getInstance("HmacMD5");
                hmac_md5.init(new SecretKeySpec(password.getBytes(this.getCharset()), "HmacMD5"));
                final byte[] hmacResult = this._convertToHexString(hmac_md5.doFinal(serverChallenge)).getBytes(this.getCharset());
                final byte[] usernameBytes = username.getBytes(this.getCharset());
                final byte[] toEncode = new byte[usernameBytes.length + 1 + hmacResult.length];
                System.arraycopy(usernameBytes, 0, toEncode, 0, usernameBytes.length);
                toEncode[usernameBytes.length] = 32;
                System.arraycopy(hmacResult, 0, toEncode, usernameBytes.length + 1, hmacResult.length);
                return this.sendCommand(Base64.encodeBase64StringUnChunked(toEncode)) == 0;
            }
            default: {
                return false;
            }
        }
    }
    
    private String _convertToHexString(final byte[] a) {
        final StringBuilder result = new StringBuilder(a.length * 2);
        for (final byte element : a) {
            if ((element & 0xFF) <= 15) {
                result.append("0");
            }
            result.append(Integer.toHexString(element & 0xFF));
        }
        return result.toString();
    }
    
    public enum AUTH_METHOD
    {
        PLAIN("PLAIN"), 
        CRAM_MD5("CRAM-MD5");
        
        private final String methodName;
        
        private AUTH_METHOD(final String methodName) {
            this.methodName = methodName;
        }
        
        public final String getAuthName() {
            return this.methodName;
        }
    }
}
