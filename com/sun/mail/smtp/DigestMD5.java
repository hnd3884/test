package com.sun.mail.smtp;

import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.InputStreamReader;
import java.io.InputStream;
import com.sun.mail.util.BASE64DecoderStream;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import com.sun.mail.util.ASCIIUtility;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.util.logging.Level;
import java.security.SecureRandom;
import java.io.OutputStream;
import com.sun.mail.util.BASE64EncoderStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import com.sun.mail.util.MailLogger;

public class DigestMD5
{
    private MailLogger logger;
    private MessageDigest md5;
    private String uri;
    private String clientResponse;
    private static char[] digits;
    
    public DigestMD5(final MailLogger logger) {
        this.logger = logger.getLogger(this.getClass(), "DEBUG DIGEST-MD5");
        logger.config("DIGEST-MD5 Loaded");
    }
    
    public byte[] authClient(final String host, final String user, final String passwd, String realm, final String serverChallenge) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final OutputStream b64os = new BASE64EncoderStream(bos, Integer.MAX_VALUE);
        SecureRandom random;
        try {
            random = new SecureRandom();
            this.md5 = MessageDigest.getInstance("MD5");
        }
        catch (final NoSuchAlgorithmException ex) {
            this.logger.log(Level.FINE, "NoSuchAlgorithmException", ex);
            throw new IOException(ex.toString());
        }
        final StringBuilder result = new StringBuilder();
        this.uri = "smtp/" + host;
        final String nc = "00000001";
        final String qop = "auth";
        final byte[] bytes = new byte[32];
        this.logger.fine("Begin authentication ...");
        final Map<String, String> map = this.tokenize(serverChallenge);
        if (realm == null) {
            final String text = map.get("realm");
            realm = ((text != null) ? new StringTokenizer(text, ",").nextToken() : host);
        }
        final String nonce = map.get("nonce");
        final String charset = map.get("charset");
        final boolean utf8 = charset != null && charset.equalsIgnoreCase("utf-8");
        random.nextBytes(bytes);
        b64os.write(bytes);
        b64os.flush();
        final String cnonce = bos.toString("iso-8859-1");
        bos.reset();
        if (utf8) {
            final String up = user + ":" + realm + ":" + passwd;
            this.md5.update(this.md5.digest(up.getBytes(StandardCharsets.UTF_8)));
        }
        else {
            this.md5.update(this.md5.digest(ASCIIUtility.getBytes(user + ":" + realm + ":" + passwd)));
        }
        this.md5.update(ASCIIUtility.getBytes(":" + nonce + ":" + cnonce));
        this.clientResponse = toHex(this.md5.digest()) + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":";
        this.md5.update(ASCIIUtility.getBytes("AUTHENTICATE:" + this.uri));
        this.md5.update(ASCIIUtility.getBytes(this.clientResponse + toHex(this.md5.digest())));
        result.append("username=\"" + user + "\"");
        result.append(",realm=\"" + realm + "\"");
        result.append(",qop=" + qop);
        result.append(",nc=" + nc);
        result.append(",nonce=\"" + nonce + "\"");
        result.append(",cnonce=\"" + cnonce + "\"");
        result.append(",digest-uri=\"" + this.uri + "\"");
        if (utf8) {
            result.append(",charset=\"utf-8\"");
        }
        result.append(",response=" + toHex(this.md5.digest()));
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Response => " + result.toString());
        }
        b64os.write(ASCIIUtility.getBytes(result.toString()));
        b64os.flush();
        return bos.toByteArray();
    }
    
    public boolean authServer(final String serverResponse) throws IOException {
        final Map<String, String> map = this.tokenize(serverResponse);
        this.md5.update(ASCIIUtility.getBytes(":" + this.uri));
        this.md5.update(ASCIIUtility.getBytes(this.clientResponse + toHex(this.md5.digest())));
        final String text = toHex(this.md5.digest());
        if (!text.equals(map.get("rspauth"))) {
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("Expected => rspauth=" + text);
            }
            return false;
        }
        return true;
    }
    
    private Map<String, String> tokenize(final String serverResponse) throws IOException {
        final Map<String, String> map = new HashMap<String, String>();
        final byte[] bytes = serverResponse.getBytes("iso-8859-1");
        String key = null;
        final StreamTokenizer tokens = new StreamTokenizer(new InputStreamReader(new BASE64DecoderStream(new ByteArrayInputStream(bytes, 4, bytes.length - 4)), "iso-8859-1"));
        tokens.ordinaryChars(48, 57);
        tokens.wordChars(48, 57);
        int ttype;
        while ((ttype = tokens.nextToken()) != -1) {
            switch (ttype) {
                case -3: {
                    if (key == null) {
                        key = tokens.sval;
                        continue;
                    }
                }
                case 34: {
                    if (this.logger.isLoggable(Level.FINE)) {
                        this.logger.fine("Received => " + key + "='" + tokens.sval + "'");
                    }
                    if (map.containsKey(key)) {
                        map.put(key, map.get(key) + "," + tokens.sval);
                    }
                    else {
                        map.put(key, tokens.sval);
                    }
                    key = null;
                    continue;
                }
                default: {
                    continue;
                }
            }
        }
        return map;
    }
    
    private static String toHex(final byte[] bytes) {
        final char[] result = new char[bytes.length * 2];
        int index = 0;
        int i = 0;
        while (index < bytes.length) {
            final int temp = bytes[index] & 0xFF;
            result[i++] = DigestMD5.digits[temp >> 4];
            result[i++] = DigestMD5.digits[temp & 0xF];
            ++index;
        }
        return new String(result);
    }
    
    static {
        DigestMD5.digits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
