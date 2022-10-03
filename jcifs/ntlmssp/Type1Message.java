package jcifs.ntlmssp;

import java.net.UnknownHostException;
import jcifs.netbios.NbtAddress;
import jcifs.Config;
import java.io.IOException;

public class Type1Message extends NtlmMessage
{
    private static final int DEFAULT_FLAGS;
    private static final String DEFAULT_DOMAIN;
    private static final String DEFAULT_WORKSTATION;
    private String suppliedDomain;
    private String suppliedWorkstation;
    
    public Type1Message() {
        this(getDefaultFlags(), getDefaultDomain(), getDefaultWorkstation());
    }
    
    public Type1Message(final int flags, final String suppliedDomain, final String suppliedWorkstation) {
        this.setFlags(flags);
        this.setSuppliedDomain(suppliedDomain);
        this.setSuppliedWorkstation(suppliedWorkstation);
    }
    
    public Type1Message(final byte[] material) throws IOException {
        this.parse(material);
    }
    
    public String getSuppliedDomain() {
        return this.suppliedDomain;
    }
    
    public void setSuppliedDomain(final String suppliedDomain) {
        this.suppliedDomain = suppliedDomain;
    }
    
    public String getSuppliedWorkstation() {
        return this.suppliedWorkstation;
    }
    
    public void setSuppliedWorkstation(final String suppliedWorkstation) {
        this.suppliedWorkstation = suppliedWorkstation;
    }
    
    public byte[] toByteArray() {
        try {
            final String suppliedDomain = this.getSuppliedDomain();
            final String suppliedWorkstation = this.getSuppliedWorkstation();
            int flags = this.getFlags();
            boolean hostInfo = false;
            byte[] domain = new byte[0];
            if (suppliedDomain != null && suppliedDomain.length() != 0) {
                hostInfo = true;
                flags |= 0x1000;
                domain = suppliedDomain.toUpperCase().getBytes(NtlmMessage.getOEMEncoding());
            }
            else {
                flags &= 0xFFFFEFFF;
            }
            byte[] workstation = new byte[0];
            if (suppliedWorkstation != null && suppliedWorkstation.length() != 0) {
                hostInfo = true;
                flags |= 0x2000;
                workstation = suppliedWorkstation.toUpperCase().getBytes(NtlmMessage.getOEMEncoding());
            }
            else {
                flags &= 0xFFFFDFFF;
            }
            final byte[] type1 = new byte[hostInfo ? (32 + domain.length + workstation.length) : 16];
            System.arraycopy(Type1Message.NTLMSSP_SIGNATURE, 0, type1, 0, 8);
            NtlmMessage.writeULong(type1, 8, 1);
            NtlmMessage.writeULong(type1, 12, flags);
            if (hostInfo) {
                NtlmMessage.writeSecurityBuffer(type1, 16, 32, domain);
                NtlmMessage.writeSecurityBuffer(type1, 24, 32 + domain.length, workstation);
            }
            return type1;
        }
        catch (final IOException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
    
    public String toString() {
        final String suppliedDomain = this.getSuppliedDomain();
        final String suppliedWorkstation = this.getSuppliedWorkstation();
        final int flags = this.getFlags();
        final StringBuffer buffer = new StringBuffer();
        if (suppliedDomain != null) {
            buffer.append("suppliedDomain: ").append(suppliedDomain);
        }
        if (suppliedWorkstation != null) {
            if (buffer.length() > 0) {
                buffer.append("; ");
            }
            buffer.append("suppliedWorkstation: ").append(suppliedWorkstation);
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
        return Type1Message.DEFAULT_FLAGS;
    }
    
    public static String getDefaultDomain() {
        return Type1Message.DEFAULT_DOMAIN;
    }
    
    public static String getDefaultWorkstation() {
        return Type1Message.DEFAULT_WORKSTATION;
    }
    
    private void parse(final byte[] material) throws IOException {
        for (int i = 0; i < 8; ++i) {
            if (material[i] != Type1Message.NTLMSSP_SIGNATURE[i]) {
                throw new IOException("Not an NTLMSSP message.");
            }
        }
        if (NtlmMessage.readULong(material, 8) != 1) {
            throw new IOException("Not a Type 1 message.");
        }
        final int flags = NtlmMessage.readULong(material, 12);
        String suppliedDomain = null;
        if ((flags & 0x1000) != 0x0) {
            final byte[] domain = NtlmMessage.readSecurityBuffer(material, 16);
            suppliedDomain = new String(domain, NtlmMessage.getOEMEncoding());
        }
        String suppliedWorkstation = null;
        if ((flags & 0x2000) != 0x0) {
            final byte[] workstation = NtlmMessage.readSecurityBuffer(material, 24);
            suppliedWorkstation = new String(workstation, NtlmMessage.getOEMEncoding());
        }
        this.setFlags(flags);
        this.setSuppliedDomain(suppliedDomain);
        this.setSuppliedWorkstation(suppliedWorkstation);
    }
    
    static {
        DEFAULT_FLAGS = (0x200 | (Config.getBoolean("jcifs.smb.client.useUnicode", true) ? 1 : 2));
        DEFAULT_DOMAIN = Config.getProperty("jcifs.smb.client.domain", null);
        String defaultWorkstation = null;
        try {
            defaultWorkstation = NbtAddress.getLocalHost().getHostName();
        }
        catch (final UnknownHostException ex) {}
        DEFAULT_WORKSTATION = defaultWorkstation;
    }
}
