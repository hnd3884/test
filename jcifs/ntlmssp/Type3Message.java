package jcifs.ntlmssp;

import java.net.UnknownHostException;
import jcifs.netbios.NbtAddress;
import jcifs.Config;
import jcifs.smb.NtlmPasswordAuthentication;
import java.io.IOException;
import java.security.SecureRandom;

public class Type3Message extends NtlmMessage
{
    private static final int DEFAULT_FLAGS;
    private static final String DEFAULT_DOMAIN;
    private static final String DEFAULT_USER;
    private static final String DEFAULT_PASSWORD;
    private static final String DEFAULT_WORKSTATION;
    private static final int LM_COMPATIBILITY;
    private static final SecureRandom RANDOM;
    private byte[] lmResponse;
    private byte[] ntResponse;
    private String domain;
    private String user;
    private String workstation;
    private byte[] sessionKey;
    
    public Type3Message() {
        this.setFlags(getDefaultFlags());
        this.setDomain(getDefaultDomain());
        this.setUser(getDefaultUser());
        this.setWorkstation(getDefaultWorkstation());
    }
    
    public Type3Message(final Type2Message type2) {
        this.setFlags(getDefaultFlags(type2));
        this.setWorkstation(getDefaultWorkstation());
        final String domain = getDefaultDomain();
        this.setDomain(domain);
        final String user = getDefaultUser();
        this.setUser(user);
        final String password = getDefaultPassword();
        switch (Type3Message.LM_COMPATIBILITY) {
            case 0:
            case 1: {
                this.setLMResponse(getLMResponse(type2, password));
                this.setNTResponse(getNTResponse(type2, password));
                break;
            }
            case 2: {
                final byte[] nt = getNTResponse(type2, password);
                this.setLMResponse(nt);
                this.setNTResponse(nt);
                break;
            }
            case 3:
            case 4:
            case 5: {
                final byte[] clientChallenge = new byte[8];
                Type3Message.RANDOM.nextBytes(clientChallenge);
                this.setLMResponse(getLMv2Response(type2, domain, user, password, clientChallenge));
                break;
            }
            default: {
                this.setLMResponse(getLMResponse(type2, password));
                this.setNTResponse(getNTResponse(type2, password));
                break;
            }
        }
    }
    
    public Type3Message(final Type2Message type2, final String password, final String domain, final String user, final String workstation) {
        this.setFlags(getDefaultFlags(type2));
        this.setDomain(domain);
        this.setUser(user);
        this.setWorkstation(workstation);
        switch (Type3Message.LM_COMPATIBILITY) {
            case 0:
            case 1: {
                this.setLMResponse(getLMResponse(type2, password));
                this.setNTResponse(getNTResponse(type2, password));
                break;
            }
            case 2: {
                final byte[] nt = getNTResponse(type2, password);
                this.setLMResponse(nt);
                this.setNTResponse(nt);
                break;
            }
            case 3:
            case 4:
            case 5: {
                final byte[] clientChallenge = new byte[8];
                Type3Message.RANDOM.nextBytes(clientChallenge);
                this.setLMResponse(getLMv2Response(type2, domain, user, password, clientChallenge));
                break;
            }
            default: {
                this.setLMResponse(getLMResponse(type2, password));
                this.setNTResponse(getNTResponse(type2, password));
                break;
            }
        }
    }
    
    public Type3Message(final int flags, final byte[] lmResponse, final byte[] ntResponse, final String domain, final String user, final String workstation) {
        this.setFlags(flags);
        this.setLMResponse(lmResponse);
        this.setNTResponse(ntResponse);
        this.setDomain(domain);
        this.setUser(user);
        this.setWorkstation(workstation);
    }
    
    public Type3Message(final byte[] material) throws IOException {
        this.parse(material);
    }
    
    public byte[] getLMResponse() {
        return this.lmResponse;
    }
    
    public void setLMResponse(final byte[] lmResponse) {
        this.lmResponse = lmResponse;
    }
    
    public byte[] getNTResponse() {
        return this.ntResponse;
    }
    
    public void setNTResponse(final byte[] ntResponse) {
        this.ntResponse = ntResponse;
    }
    
    public String getDomain() {
        return this.domain;
    }
    
    public void setDomain(final String domain) {
        this.domain = domain;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public void setUser(final String user) {
        this.user = user;
    }
    
    public String getWorkstation() {
        return this.workstation;
    }
    
    public void setWorkstation(final String workstation) {
        this.workstation = workstation;
    }
    
    public byte[] getSessionKey() {
        return this.sessionKey;
    }
    
    public void setSessionKey(final byte[] sessionKey) {
        this.sessionKey = sessionKey;
    }
    
    public byte[] toByteArray() {
        try {
            final int flags = this.getFlags();
            final boolean unicode = (flags & 0x1) != 0x0;
            final String oem = unicode ? null : NtlmMessage.getOEMEncoding();
            final String domainName = this.getDomain();
            byte[] domain = null;
            if (domainName != null && domainName.length() != 0) {
                domain = (unicode ? domainName.toUpperCase().getBytes("UnicodeLittleUnmarked") : domainName.toUpperCase().getBytes(oem));
            }
            final int domainLength = (domain != null) ? domain.length : 0;
            final String userName = this.getUser();
            byte[] user = null;
            if (userName != null && userName.length() != 0) {
                user = (unicode ? userName.getBytes("UnicodeLittleUnmarked") : userName.toUpperCase().getBytes(oem));
            }
            final int userLength = (user != null) ? user.length : 0;
            final String workstationName = this.getWorkstation();
            byte[] workstation = null;
            if (workstationName != null && workstationName.length() != 0) {
                workstation = (unicode ? workstationName.getBytes("UnicodeLittleUnmarked") : workstationName.toUpperCase().getBytes(oem));
            }
            final int workstationLength = (workstation != null) ? workstation.length : 0;
            final byte[] lmResponse = this.getLMResponse();
            final int lmLength = (lmResponse != null) ? lmResponse.length : 0;
            final byte[] ntResponse = this.getNTResponse();
            final int ntLength = (ntResponse != null) ? ntResponse.length : 0;
            final byte[] sessionKey = this.getSessionKey();
            final int keyLength = (sessionKey != null) ? sessionKey.length : 0;
            final byte[] type3 = new byte[64 + domainLength + userLength + workstationLength + lmLength + ntLength + keyLength];
            System.arraycopy(Type3Message.NTLMSSP_SIGNATURE, 0, type3, 0, 8);
            NtlmMessage.writeULong(type3, 8, 3);
            int offset = 64;
            NtlmMessage.writeSecurityBuffer(type3, 12, offset, lmResponse);
            offset += lmLength;
            NtlmMessage.writeSecurityBuffer(type3, 20, offset, ntResponse);
            offset += ntLength;
            NtlmMessage.writeSecurityBuffer(type3, 28, offset, domain);
            offset += domainLength;
            NtlmMessage.writeSecurityBuffer(type3, 36, offset, user);
            offset += userLength;
            NtlmMessage.writeSecurityBuffer(type3, 44, offset, workstation);
            offset += workstationLength;
            NtlmMessage.writeSecurityBuffer(type3, 52, offset, sessionKey);
            NtlmMessage.writeULong(type3, 60, flags);
            return type3;
        }
        catch (final IOException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
    
    public String toString() {
        final String user = this.getUser();
        final String domain = this.getDomain();
        final String workstation = this.getWorkstation();
        final byte[] lmResponse = this.getLMResponse();
        final byte[] ntResponse = this.getNTResponse();
        final byte[] sessionKey = this.getSessionKey();
        final int flags = this.getFlags();
        final StringBuffer buffer = new StringBuffer();
        if (domain != null) {
            buffer.append("domain: ").append(domain);
        }
        if (user != null) {
            if (buffer.length() > 0) {
                buffer.append("; ");
            }
            buffer.append("user: ").append(user);
        }
        if (workstation != null) {
            if (buffer.length() > 0) {
                buffer.append("; ");
            }
            buffer.append("workstation: ").append(workstation);
        }
        if (lmResponse != null) {
            if (buffer.length() > 0) {
                buffer.append("; ");
            }
            buffer.append("lmResponse: ");
            buffer.append("0x");
            for (int i = 0; i < lmResponse.length; ++i) {
                buffer.append(Integer.toHexString(lmResponse[i] >> 4 & 0xF));
                buffer.append(Integer.toHexString(lmResponse[i] & 0xF));
            }
        }
        if (ntResponse != null) {
            if (buffer.length() > 0) {
                buffer.append("; ");
            }
            buffer.append("ntResponse: ");
            buffer.append("0x");
            for (int i = 0; i < ntResponse.length; ++i) {
                buffer.append(Integer.toHexString(ntResponse[i] >> 4 & 0xF));
                buffer.append(Integer.toHexString(ntResponse[i] & 0xF));
            }
        }
        if (sessionKey != null) {
            if (buffer.length() > 0) {
                buffer.append("; ");
            }
            buffer.append("sessionKey: ");
            buffer.append("0x");
            for (int i = 0; i < sessionKey.length; ++i) {
                buffer.append(Integer.toHexString(sessionKey[i] >> 4 & 0xF));
                buffer.append(Integer.toHexString(sessionKey[i] & 0xF));
            }
        }
        if (flags != 0) {
            if (buffer.length() > 0) {
                buffer.append("; ");
            }
            buffer.append("flags: ");
            buffer.append("0x");
            buffer.append(Integer.toHexString(flags >> 28 & 0xF));
            buffer.append(Integer.toHexString(flags >> 24 & 0xF));
            buffer.append(Integer.toHexString(flags >> 20 & 0xF));
            buffer.append(Integer.toHexString(flags >> 16 & 0xF));
            buffer.append(Integer.toHexString(flags >> 12 & 0xF));
            buffer.append(Integer.toHexString(flags >> 8 & 0xF));
            buffer.append(Integer.toHexString(flags >> 4 & 0xF));
            buffer.append(Integer.toHexString(flags & 0xF));
        }
        return buffer.toString();
    }
    
    public static int getDefaultFlags() {
        return Type3Message.DEFAULT_FLAGS;
    }
    
    public static int getDefaultFlags(final Type2Message type2) {
        if (type2 == null) {
            return Type3Message.DEFAULT_FLAGS;
        }
        int flags = 512;
        flags |= (((type2.getFlags() & 0x1) != 0x0) ? 1 : 2);
        return flags;
    }
    
    public static byte[] getLMResponse(final Type2Message type2, final String password) {
        if (type2 == null || password == null) {
            return null;
        }
        return NtlmPasswordAuthentication.getPreNTLMResponse(password, type2.getChallenge());
    }
    
    public static byte[] getLMv2Response(final Type2Message type2, final String domain, final String user, final String password, final byte[] clientChallenge) {
        if (type2 == null || domain == null || user == null || password == null || clientChallenge == null) {
            return null;
        }
        return NtlmPasswordAuthentication.getLMv2Response(domain, user, password, type2.getChallenge(), clientChallenge);
    }
    
    public static byte[] getNTResponse(final Type2Message type2, final String password) {
        if (type2 == null || password == null) {
            return null;
        }
        return NtlmPasswordAuthentication.getNTLMResponse(password, type2.getChallenge());
    }
    
    public static String getDefaultDomain() {
        return Type3Message.DEFAULT_DOMAIN;
    }
    
    public static String getDefaultUser() {
        return Type3Message.DEFAULT_USER;
    }
    
    public static String getDefaultPassword() {
        return Type3Message.DEFAULT_PASSWORD;
    }
    
    public static String getDefaultWorkstation() {
        return Type3Message.DEFAULT_WORKSTATION;
    }
    
    private void parse(final byte[] material) throws IOException {
        for (int i = 0; i < 8; ++i) {
            if (material[i] != Type3Message.NTLMSSP_SIGNATURE[i]) {
                throw new IOException("Not an NTLMSSP message.");
            }
        }
        if (NtlmMessage.readULong(material, 8) != 3) {
            throw new IOException("Not a Type 3 message.");
        }
        final byte[] lmResponse = NtlmMessage.readSecurityBuffer(material, 12);
        final int lmResponseOffset = NtlmMessage.readULong(material, 16);
        final byte[] ntResponse = NtlmMessage.readSecurityBuffer(material, 20);
        final int ntResponseOffset = NtlmMessage.readULong(material, 24);
        final byte[] domain = NtlmMessage.readSecurityBuffer(material, 28);
        final int domainOffset = NtlmMessage.readULong(material, 32);
        final byte[] user = NtlmMessage.readSecurityBuffer(material, 36);
        final int userOffset = NtlmMessage.readULong(material, 40);
        final byte[] workstation = NtlmMessage.readSecurityBuffer(material, 44);
        final int workstationOffset = NtlmMessage.readULong(material, 48);
        int flags;
        String charset;
        if (lmResponseOffset == 52 || ntResponseOffset == 52 || domainOffset == 52 || userOffset == 52 || workstationOffset == 52) {
            flags = 514;
            charset = NtlmMessage.getOEMEncoding();
        }
        else {
            this.setSessionKey(NtlmMessage.readSecurityBuffer(material, 52));
            flags = NtlmMessage.readULong(material, 60);
            charset = (((flags & 0x1) != 0x0) ? "UnicodeLittleUnmarked" : NtlmMessage.getOEMEncoding());
        }
        this.setFlags(flags);
        this.setLMResponse(lmResponse);
        if (ntResponse.length == 24) {
            this.setNTResponse(ntResponse);
        }
        this.setDomain(new String(domain, charset));
        this.setUser(new String(user, charset));
        this.setWorkstation(new String(workstation, charset));
    }
    
    static {
        RANDOM = new SecureRandom();
        DEFAULT_FLAGS = (0x200 | (Config.getBoolean("jcifs.smb.client.useUnicode", true) ? 1 : 2));
        DEFAULT_DOMAIN = Config.getProperty("jcifs.smb.client.domain", null);
        DEFAULT_USER = Config.getProperty("jcifs.smb.client.username", null);
        DEFAULT_PASSWORD = Config.getProperty("jcifs.smb.client.password", null);
        String defaultWorkstation = null;
        try {
            defaultWorkstation = NbtAddress.getLocalHost().getHostName();
        }
        catch (final UnknownHostException ex) {}
        DEFAULT_WORKSTATION = defaultWorkstation;
        LM_COMPATIBILITY = Config.getInt("jcifs.smb.lmCompatibility", 0);
    }
}
