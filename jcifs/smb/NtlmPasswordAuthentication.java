package jcifs.smb;

import java.util.Arrays;
import jcifs.util.HMACT64;
import jcifs.util.MD4;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import jcifs.Config;
import jcifs.util.DES;
import jcifs.util.LogStream;
import java.util.Random;
import java.io.Serializable;
import java.security.Principal;

public final class NtlmPasswordAuthentication implements Principal, Serializable
{
    private static final int LM_COMPATIBILITY;
    private static final Random RANDOM;
    private static LogStream log;
    private static final byte[] S8;
    static String DEFAULT_DOMAIN;
    static String DEFAULT_USERNAME;
    static String DEFAULT_PASSWORD;
    static final String BLANK = "";
    static final NtlmPasswordAuthentication NULL;
    static final NtlmPasswordAuthentication GUEST;
    static final NtlmPasswordAuthentication DEFAULT;
    String domain;
    String username;
    String password;
    byte[] ansiHash;
    byte[] unicodeHash;
    boolean hashesExternal;
    byte[] clientChallenge;
    byte[] challenge;
    
    private static void E(final byte[] key, final byte[] data, final byte[] e) {
        final byte[] key2 = new byte[7];
        final byte[] e2 = new byte[8];
        for (int i = 0; i < key.length / 7; ++i) {
            System.arraycopy(key, i * 7, key2, 0, 7);
            final DES des = new DES(key2);
            des.encrypt(data, e2);
            System.arraycopy(e2, 0, e, i * 8, 8);
        }
    }
    
    static void initDefaults() {
        if (NtlmPasswordAuthentication.DEFAULT_DOMAIN != null) {
            return;
        }
        NtlmPasswordAuthentication.DEFAULT_DOMAIN = Config.getProperty("jcifs.smb.client.domain", "?");
        NtlmPasswordAuthentication.DEFAULT_USERNAME = Config.getProperty("jcifs.smb.client.username", "GUEST");
        NtlmPasswordAuthentication.DEFAULT_PASSWORD = Config.getProperty("jcifs.smb.client.password", "");
    }
    
    public static byte[] getPreNTLMResponse(final String password, final byte[] challenge) {
        final byte[] p14 = new byte[14];
        final byte[] p15 = new byte[21];
        final byte[] p16 = new byte[24];
        byte[] passwordBytes;
        try {
            passwordBytes = password.toUpperCase().getBytes(SmbConstants.OEM_ENCODING);
        }
        catch (final UnsupportedEncodingException uee) {
            throw new RuntimeException("Try setting jcifs.encoding=US-ASCII", uee);
        }
        int passwordLength = passwordBytes.length;
        if (passwordLength > 14) {
            passwordLength = 14;
        }
        System.arraycopy(passwordBytes, 0, p14, 0, passwordLength);
        E(p14, NtlmPasswordAuthentication.S8, p15);
        E(p15, challenge, p16);
        return p16;
    }
    
    public static byte[] getNTLMResponse(final String password, final byte[] challenge) {
        byte[] uni = null;
        final byte[] p21 = new byte[21];
        final byte[] p22 = new byte[24];
        try {
            uni = password.getBytes("UnicodeLittleUnmarked");
        }
        catch (final UnsupportedEncodingException uee) {
            final LogStream log = NtlmPasswordAuthentication.log;
            if (LogStream.level > 0) {
                uee.printStackTrace(NtlmPasswordAuthentication.log);
            }
        }
        final MD4 md4 = new MD4();
        md4.update(uni);
        try {
            md4.digest(p21, 0, 16);
        }
        catch (final Exception ex) {
            final LogStream log2 = NtlmPasswordAuthentication.log;
            if (LogStream.level > 0) {
                ex.printStackTrace(NtlmPasswordAuthentication.log);
            }
        }
        E(p21, challenge, p22);
        return p22;
    }
    
    public static byte[] getLMv2Response(final String domain, final String user, final String password, final byte[] challenge, final byte[] clientChallenge) {
        try {
            final byte[] hash = new byte[16];
            final byte[] response = new byte[24];
            final MD4 md4 = new MD4();
            md4.update(password.getBytes("UnicodeLittleUnmarked"));
            HMACT64 hmac = new HMACT64(md4.digest());
            hmac.update(user.toUpperCase().getBytes("UnicodeLittleUnmarked"));
            hmac.update(domain.toUpperCase().getBytes("UnicodeLittleUnmarked"));
            hmac = new HMACT64(hmac.digest());
            hmac.update(challenge);
            hmac.update(clientChallenge);
            hmac.digest(response, 0, 16);
            System.arraycopy(clientChallenge, 0, response, 16, 8);
            return response;
        }
        catch (final Exception ex) {
            final LogStream log = NtlmPasswordAuthentication.log;
            if (LogStream.level > 0) {
                ex.printStackTrace(NtlmPasswordAuthentication.log);
            }
            return null;
        }
    }
    
    public NtlmPasswordAuthentication(final String userInfo) {
        this.hashesExternal = false;
        this.clientChallenge = null;
        this.challenge = null;
        final String domain = null;
        this.password = domain;
        this.username = domain;
        this.domain = domain;
        if (userInfo != null) {
            final int end = userInfo.length();
            int i = 0;
            int u = 0;
            while (i < end) {
                final char c = userInfo.charAt(i);
                if (c == ';') {
                    this.domain = userInfo.substring(0, i);
                    u = i + 1;
                }
                else if (c == ':') {
                    this.password = userInfo.substring(i + 1);
                    break;
                }
                ++i;
            }
            this.username = userInfo.substring(u, i);
        }
        initDefaults();
        if (this.domain == null) {
            this.domain = NtlmPasswordAuthentication.DEFAULT_DOMAIN;
        }
        if (this.username == null) {
            this.username = NtlmPasswordAuthentication.DEFAULT_USERNAME;
        }
        if (this.password == null) {
            this.password = NtlmPasswordAuthentication.DEFAULT_PASSWORD;
        }
    }
    
    public NtlmPasswordAuthentication(final String domain, final String username, final String password) {
        this.hashesExternal = false;
        this.clientChallenge = null;
        this.challenge = null;
        this.domain = domain;
        this.username = username;
        this.password = password;
        initDefaults();
        if (domain == null) {
            this.domain = NtlmPasswordAuthentication.DEFAULT_DOMAIN;
        }
        if (username == null) {
            this.username = NtlmPasswordAuthentication.DEFAULT_USERNAME;
        }
        if (password == null) {
            this.password = NtlmPasswordAuthentication.DEFAULT_PASSWORD;
        }
    }
    
    public NtlmPasswordAuthentication(final String domain, final String username, final byte[] challenge, final byte[] ansiHash, final byte[] unicodeHash) {
        this.hashesExternal = false;
        this.clientChallenge = null;
        this.challenge = null;
        if (domain == null || username == null || ansiHash == null || unicodeHash == null) {
            throw new IllegalArgumentException("External credentials cannot be null");
        }
        this.domain = domain;
        this.username = username;
        this.password = null;
        this.challenge = challenge;
        this.ansiHash = ansiHash;
        this.unicodeHash = unicodeHash;
        this.hashesExternal = true;
    }
    
    public String getDomain() {
        return this.domain;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public String getName() {
        final boolean d = this.domain.length() > 0 && !this.domain.equals("?");
        return d ? (this.domain + "\\" + this.username) : this.username;
    }
    
    public byte[] getAnsiHash(final byte[] challenge) {
        if (this.hashesExternal) {
            return this.ansiHash;
        }
        switch (NtlmPasswordAuthentication.LM_COMPATIBILITY) {
            case 0:
            case 1: {
                return getPreNTLMResponse(this.password, challenge);
            }
            case 2: {
                return getNTLMResponse(this.password, challenge);
            }
            case 3:
            case 4:
            case 5: {
                if (this.clientChallenge == null) {
                    this.clientChallenge = new byte[8];
                    NtlmPasswordAuthentication.RANDOM.nextBytes(this.clientChallenge);
                }
                return getLMv2Response(this.domain, this.username, this.password, challenge, this.clientChallenge);
            }
            default: {
                return getPreNTLMResponse(this.password, challenge);
            }
        }
    }
    
    public byte[] getUnicodeHash(final byte[] challenge) {
        if (this.hashesExternal) {
            return this.unicodeHash;
        }
        switch (NtlmPasswordAuthentication.LM_COMPATIBILITY) {
            case 0:
            case 1:
            case 2: {
                return getNTLMResponse(this.password, challenge);
            }
            case 3:
            case 4:
            case 5: {
                return new byte[0];
            }
            default: {
                return getNTLMResponse(this.password, challenge);
            }
        }
    }
    
    public byte[] getUserSessionKey(final byte[] challenge) {
        if (this.hashesExternal) {
            return null;
        }
        final byte[] key = new byte[16];
        try {
            this.getUserSessionKey(challenge, key, 0);
        }
        catch (final Exception ex) {
            final LogStream log = NtlmPasswordAuthentication.log;
            if (LogStream.level > 0) {
                ex.printStackTrace(NtlmPasswordAuthentication.log);
            }
        }
        return key;
    }
    
    void getUserSessionKey(final byte[] challenge, final byte[] dest, final int offset) throws Exception {
        if (this.hashesExternal) {
            return;
        }
        final MD4 md4 = new MD4();
        md4.update(this.password.getBytes("UnicodeLittleUnmarked"));
        switch (NtlmPasswordAuthentication.LM_COMPATIBILITY) {
            case 0:
            case 1:
            case 2: {
                md4.update(md4.digest());
                md4.digest(dest, offset, 16);
                break;
            }
            case 3:
            case 4:
            case 5: {
                if (this.clientChallenge == null) {
                    this.clientChallenge = new byte[8];
                    NtlmPasswordAuthentication.RANDOM.nextBytes(this.clientChallenge);
                }
                HMACT64 hmac = new HMACT64(md4.digest());
                hmac.update(this.username.toUpperCase().getBytes("UnicodeLittleUnmarked"));
                hmac.update(this.domain.toUpperCase().getBytes("UnicodeLittleUnmarked"));
                final byte[] ntlmv2Hash = hmac.digest();
                hmac = new HMACT64(ntlmv2Hash);
                hmac.update(challenge);
                hmac.update(this.clientChallenge);
                final HMACT64 userKey = new HMACT64(ntlmv2Hash);
                userKey.update(hmac.digest());
                userKey.digest(dest, offset, 16);
                break;
            }
            default: {
                md4.update(md4.digest());
                md4.digest(dest, offset, 16);
                break;
            }
        }
    }
    
    public boolean equals(final Object obj) {
        if (obj instanceof NtlmPasswordAuthentication) {
            final NtlmPasswordAuthentication ntlm = (NtlmPasswordAuthentication)obj;
            if (ntlm.domain.toUpperCase().equals(this.domain.toUpperCase()) && ntlm.username.toUpperCase().equals(this.username.toUpperCase())) {
                if (this.hashesExternal && ntlm.hashesExternal) {
                    return Arrays.equals(this.ansiHash, ntlm.ansiHash) && Arrays.equals(this.unicodeHash, ntlm.unicodeHash);
                }
                if (!this.hashesExternal && this.password.equals(ntlm.password)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public int hashCode() {
        return this.getName().toUpperCase().hashCode();
    }
    
    public String toString() {
        return this.getName();
    }
    
    static {
        LM_COMPATIBILITY = Config.getInt("jcifs.smb.lmCompatibility", 0);
        RANDOM = new Random();
        NtlmPasswordAuthentication.log = LogStream.getInstance();
        S8 = new byte[] { 75, 71, 83, 33, 64, 35, 36, 37 };
        NULL = new NtlmPasswordAuthentication("", "", "");
        GUEST = new NtlmPasswordAuthentication("?", "GUEST", "");
        DEFAULT = new NtlmPasswordAuthentication(null);
    }
}
