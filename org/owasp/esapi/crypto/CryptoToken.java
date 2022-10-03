package org.owasp.esapi.crypto;

import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.io.IOException;
import org.owasp.esapi.Encryptor;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.Date;
import java.util.regex.Matcher;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.errors.EncodingException;
import org.owasp.esapi.errors.EncryptionException;
import org.owasp.esapi.ESAPI;
import java.util.regex.Pattern;
import javax.crypto.SecretKey;
import java.util.TreeMap;
import org.owasp.esapi.Logger;

public class CryptoToken
{
    public static final String ANONYMOUS_USER = "<anonymous>";
    private static final long DEFAULT_EXP_TIME = 300000L;
    private static final String DELIM = ";";
    private static final char DELIM_CHAR = ';';
    private static final char QUOTE_CHAR = '\\';
    private static final String ATTR_NAME_REGEX = "[A-Za-z0-9_.-]+";
    private static final String USERNAME_REGEX = "[a-z][a-z0-9_.@-]*";
    private static Logger logger;
    private String username;
    private long expirationTime;
    private TreeMap<String, String> attributes;
    private transient SecretKey secretKey;
    private Pattern attrNameRegex;
    private Pattern userNameRegex;
    
    public CryptoToken() {
        this.username = "<anonymous>";
        this.expirationTime = 0L;
        this.attributes = new TreeMap<String, String>();
        this.secretKey = null;
        this.attrNameRegex = Pattern.compile("[A-Za-z0-9_.-]+");
        this.userNameRegex = Pattern.compile("[a-z][a-z0-9_.@-]*");
        this.secretKey = this.getDefaultSecretKey(ESAPI.securityConfiguration().getEncryptionAlgorithm());
        final long now = System.currentTimeMillis();
        this.expirationTime = now + 300000L;
    }
    
    public CryptoToken(final SecretKey skey) {
        this.username = "<anonymous>";
        this.expirationTime = 0L;
        this.attributes = new TreeMap<String, String>();
        this.secretKey = null;
        this.attrNameRegex = Pattern.compile("[A-Za-z0-9_.-]+");
        this.userNameRegex = Pattern.compile("[a-z][a-z0-9_.@-]*");
        assert skey != null : "SecretKey may not be null.";
        this.secretKey = skey;
        final long now = System.currentTimeMillis();
        this.expirationTime = now + 300000L;
    }
    
    public CryptoToken(final String token) throws EncryptionException {
        this.username = "<anonymous>";
        this.expirationTime = 0L;
        this.attributes = new TreeMap<String, String>();
        this.secretKey = null;
        this.attrNameRegex = Pattern.compile("[A-Za-z0-9_.-]+");
        this.userNameRegex = Pattern.compile("[a-z][a-z0-9_.@-]*");
        this.secretKey = this.getDefaultSecretKey(ESAPI.securityConfiguration().getEncryptionAlgorithm());
        try {
            this.decryptToken(this.secretKey, token);
        }
        catch (final EncodingException e) {
            throw new EncryptionException("Decryption of token failed. Token improperly encoded or encrypted with different key.", "Can't decrypt token because not correctly encoded or encrypted with different key.", e);
        }
        assert this.username != null : "Programming error: Decrypted token found username null.";
        assert this.expirationTime > 0L : "Programming error: Decrypted token found expirationTime <= 0.";
    }
    
    public CryptoToken(final SecretKey skey, final String token) throws EncryptionException {
        this.username = "<anonymous>";
        this.expirationTime = 0L;
        this.attributes = new TreeMap<String, String>();
        this.secretKey = null;
        this.attrNameRegex = Pattern.compile("[A-Za-z0-9_.-]+");
        this.userNameRegex = Pattern.compile("[a-z][a-z0-9_.@-]*");
        assert skey != null : "SecretKey may not be null.";
        assert token != null : "Token may not be null";
        this.secretKey = skey;
        try {
            this.decryptToken(this.secretKey, token);
        }
        catch (final EncodingException e) {
            throw new EncryptionException("Decryption of token failed. Token improperly encoded.", "Can't decrypt token because not correctly encoded.", e);
        }
        assert this.username != null : "Programming error: Decrypted token found username null.";
        assert this.expirationTime > 0L : "Programming error: Decrypted token found expirationTime <= 0.";
    }
    
    public String getUserAccountName() {
        return (this.username != null) ? this.username : "<anonymous>";
    }
    
    public void setUserAccountName(final String userAccountName) throws ValidationException {
        assert userAccountName != null : "User account name may not be null.";
        final String userAcct = userAccountName.toLowerCase();
        final Matcher userNameChecker = this.userNameRegex.matcher(userAcct);
        if (userNameChecker.matches()) {
            this.username = userAcct;
            return;
        }
        throw new ValidationException("Invalid user account name encountered.", "User account name " + userAccountName + " does not match regex " + "[a-z][a-z0-9_.@-]*" + " after conversion to lowercase.");
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > this.expirationTime;
    }
    
    public void setExpiration(final int intervalSecs) throws IllegalArgumentException {
        final int intervalMillis = intervalSecs * 1000;
        if (intervalMillis <= 0) {
            throw new IllegalArgumentException("intervalSecs argument, converted to millisecs, must be > 0.");
        }
        final long now = System.currentTimeMillis();
        preAdd(now, intervalMillis);
        this.expirationTime = now + intervalMillis;
    }
    
    public void setExpiration(final Date expirationDate) throws IllegalArgumentException {
        if (expirationDate == null) {
            throw new IllegalArgumentException("expirationDate may not be null.");
        }
        final long curTime = System.currentTimeMillis();
        final long expTime = expirationDate.getTime();
        if (expTime <= curTime) {
            throw new IllegalArgumentException("Expiration date must be after current date/time.");
        }
        this.expirationTime = expTime;
    }
    
    public long getExpiration() {
        assert this.expirationTime > 0L : "Programming error: Expiration time <= 0";
        return this.expirationTime;
    }
    
    public Date getExpirationDate() {
        return new Date(this.getExpiration());
    }
    
    public void setAttribute(final String name, final String value) throws ValidationException {
        if (name == null || name.length() == 0) {
            throw new ValidationException("Null or empty attribute NAME encountered", "Attribute NAMES may not be null or empty string.");
        }
        if (value == null) {
            throw new ValidationException("Null attribute VALUE encountered for attr name " + name, "Attribute VALUE may not be null; attr name: " + name);
        }
        final Matcher attrNameChecker = this.attrNameRegex.matcher(name);
        if (attrNameChecker.matches()) {
            this.attributes.put(name, value);
            return;
        }
        throw new ValidationException("Invalid attribute name encountered.", "Attribute name " + name + " does not match regex " + "[A-Za-z0-9_.-]+");
    }
    
    public void addAttributes(final Map<String, String> attrs) throws ValidationException {
        assert attrs != null : "Attribute map may not be null.";
        final Set<Map.Entry<String, String>> keyValueSet = attrs.entrySet();
        for (final Map.Entry<String, String> entry : keyValueSet) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            this.setAttribute(key, value);
        }
    }
    
    public String getAttribute(final String name) {
        return this.attributes.get(name);
    }
    
    public Map<String, String> getAttributes() {
        return (Map)this.attributes.clone();
    }
    
    public void clearAttributes() {
        this.attributes.clear();
    }
    
    public String getToken(final SecretKey skey) throws EncryptionException {
        return this.createEncryptedToken(skey);
    }
    
    public String updateToken(final int additionalSecs) throws EncryptionException, ValidationException {
        if (additionalSecs < 0) {
            throw new IllegalArgumentException("additionalSecs argument must be >= 0.");
        }
        final long curExpTime = this.getExpiration();
        preAdd(curExpTime, additionalSecs * 1000);
        this.expirationTime = curExpTime + additionalSecs * 1000;
        if (this.isExpired()) {
            this.expirationTime = curExpTime;
            throw new ValidationException("Token timed out.", "Cryptographic token not increased to sufficient value to prevent timeout.");
        }
        return this.getToken();
    }
    
    public String getToken() throws EncryptionException {
        return this.createEncryptedToken(this.secretKey);
    }
    
    private String createEncryptedToken(final SecretKey skey) throws EncryptionException {
        final StringBuilder sb = new StringBuilder(this.getUserAccountName() + ";");
        sb.append(this.getExpiration()).append(";");
        sb.append(this.getQuotedAttributes());
        final Encryptor encryptor = ESAPI.encryptor();
        final CipherText ct = encryptor.encrypt(skey, new PlainText(sb.toString()));
        final String b64 = ESAPI.encoder().encodeForBase64(ct.asPortableSerializedByteArray(), false);
        return b64;
    }
    
    private String getQuotedAttributes() {
        final StringBuilder sb = new StringBuilder();
        final Set<Map.Entry<String, String>> keyValueSet = this.attributes.entrySet();
        for (final Map.Entry<String, String> entry : keyValueSet) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            CryptoToken.logger.debug(Logger.EVENT_UNSPECIFIED, "   " + key + " -> <not shown>");
            sb.append(key + "=" + quoteAttributeValue(value) + ";");
        }
        return sb.toString();
    }
    
    private static String quoteAttributeValue(final String value) {
        assert value != null : "Program error: Value should not be null.";
        final StringBuilder sb = new StringBuilder();
        final char[] charArray = value.toCharArray();
        for (int i = 0; i < charArray.length; ++i) {
            final char c = charArray[i];
            if (c == '\\' || c == '=' || c == ';') {
                sb.append('\\').append(c);
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    private static String parseQuotedValue(final String quotedValue) {
        final StringBuilder sb = new StringBuilder();
        final char[] charArray = quotedValue.toCharArray();
        for (int i = 0; i < charArray.length; ++i) {
            final char c = charArray[i];
            if (c == '\\') {
                ++i;
                sb.append(charArray[i]);
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    private void decryptToken(final SecretKey skey, final String b64token) throws EncryptionException, EncodingException {
        byte[] token = null;
        try {
            token = ESAPI.encoder().decodeFromBase64(b64token);
        }
        catch (final IOException e) {
            throw new EncodingException("Invalid base64 encoding.", "Invalid base64 encoding. Encrypted token was: " + b64token);
        }
        final CipherText ct = CipherText.fromPortableSerializedBytes(token);
        final Encryptor encryptor = ESAPI.encryptor();
        final PlainText pt = encryptor.decrypt(skey, ct);
        final String str = pt.toString();
        assert str.endsWith(";") : "Programming error: Expecting decrypted token to end with delim char, ;";
        final char[] charArray = str.toCharArray();
        int prevPos = -1;
        int fieldNo = 0;
        final ArrayList<Object> fields = new ArrayList<Object>();
        for (int lastPos = charArray.length, curPos = 0; curPos < lastPos; ++curPos) {
            boolean quoted = false;
            char curChar = charArray[curPos];
            if (curChar == '\\') {
                ++curPos;
                if (curChar != lastPos) {
                    curChar = charArray[curPos + 1];
                    quoted = true;
                }
                else {
                    curChar = ';';
                }
            }
            if (curChar == ';' && !quoted) {
                final String record = str.substring(prevPos + 1, curPos);
                fields.add(record);
                ++fieldNo;
                prevPos = curPos;
            }
        }
        final Object[] objArray = fields.toArray();
        assert fieldNo == objArray.length : "Program error: Mismatch of delimited field count.";
        CryptoToken.logger.debug(Logger.EVENT_UNSPECIFIED, "Found " + objArray.length + " fields.");
        assert objArray.length >= 2 : "Missing mandatory fields from decrypted token (username &/or expiration time).";
        this.username = ((String)objArray[0]).toLowerCase();
        final String expTime = (String)objArray[1];
        this.expirationTime = Long.parseLong(expTime);
        for (int i = 2; i < objArray.length; ++i) {
            final String nvpair = (String)objArray[i];
            final int equalsAt = nvpair.indexOf("=");
            if (equalsAt == -1) {
                throw new EncryptionException("Invalid attribute encountered in decrypted token.", "Malformed attribute name/value pair (" + nvpair + ") found in decrypted token.");
            }
            final String name = nvpair.substring(0, equalsAt);
            final String quotedValue = nvpair.substring(equalsAt + 1);
            final String value = parseQuotedValue(quotedValue);
            CryptoToken.logger.debug(Logger.EVENT_UNSPECIFIED, "Attribute[" + i + "]: name=" + name + ", value=<not shown>");
            final Matcher attrNameChecker = this.attrNameRegex.matcher(name);
            if (!attrNameChecker.matches()) {
                throw new EncryptionException("Invalid attribute name encountered in decrypted token.", "Invalid attribute name encountered in decrypted token; attribute name " + name + " does not match regex " + "[A-Za-z0-9_.-]+");
            }
            this.attributes.put(name, value);
            this.attributes.put(name, value);
        }
    }
    
    private SecretKey getDefaultSecretKey(final String encryptAlgorithm) {
        assert encryptAlgorithm != null : "Encryption algorithm cannot be null";
        final byte[] skey = ESAPI.securityConfiguration().getMasterKey();
        assert skey != null : "Can't obtain master key, Encryptor.MasterKey";
        assert skey.length >= 7 : "Encryptor.MasterKey must be at least 7 bytes. Length is: " + skey.length + " bytes.";
        return new SecretKeySpec(skey, encryptAlgorithm);
    }
    
    static final void preAdd(final long leftLongValue, final int rightIntValue) throws ArithmeticException {
        if (rightIntValue > 0 && leftLongValue + rightIntValue < leftLongValue) {
            throw new ArithmeticException("Arithmetic overflow for addition.");
        }
        if (rightIntValue < 0 && leftLongValue + rightIntValue > leftLongValue) {
            throw new ArithmeticException("Arithmetic underflow for addition.");
        }
    }
    
    static {
        CryptoToken.logger = ESAPI.getLogger("CryptoToken");
    }
}
