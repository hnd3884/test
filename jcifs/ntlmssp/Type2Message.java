package jcifs.ntlmssp;

import java.net.UnknownHostException;
import jcifs.netbios.NbtAddress;
import jcifs.Config;
import java.io.IOException;

public class Type2Message extends NtlmMessage
{
    private static final int DEFAULT_FLAGS;
    private static final String DEFAULT_DOMAIN;
    private static final byte[] DEFAULT_TARGET_INFORMATION;
    private byte[] challenge;
    private String target;
    private byte[] context;
    private byte[] targetInformation;
    
    public Type2Message() {
        this(getDefaultFlags(), null, null);
    }
    
    public Type2Message(final Type1Message type1) {
        this(type1, null, null);
    }
    
    public Type2Message(final Type1Message type1, final byte[] challenge, final String target) {
        this(getDefaultFlags(type1), challenge, (type1 != null && target == null && type1.getFlag(4)) ? getDefaultDomain() : target);
    }
    
    public Type2Message(final int flags, final byte[] challenge, final String target) {
        this.setFlags(flags);
        this.setChallenge(challenge);
        this.setTarget(target);
        if (target != null) {
            this.setTargetInformation(getDefaultTargetInformation());
        }
    }
    
    public Type2Message(final byte[] material) throws IOException {
        this.parse(material);
    }
    
    public byte[] getChallenge() {
        return this.challenge;
    }
    
    public void setChallenge(final byte[] challenge) {
        this.challenge = challenge;
    }
    
    public String getTarget() {
        return this.target;
    }
    
    public void setTarget(final String target) {
        this.target = target;
    }
    
    public byte[] getTargetInformation() {
        return this.targetInformation;
    }
    
    public void setTargetInformation(final byte[] targetInformation) {
        this.targetInformation = targetInformation;
    }
    
    public byte[] getContext() {
        return this.context;
    }
    
    public void setContext(final byte[] context) {
        this.context = context;
    }
    
    public byte[] toByteArray() {
        try {
            final String targetName = this.getTarget();
            final byte[] challenge = this.getChallenge();
            byte[] context = this.getContext();
            final byte[] targetInformation = this.getTargetInformation();
            int flags = this.getFlags();
            byte[] target = new byte[0];
            if ((flags & 0x70000) != 0x0) {
                if (targetName != null && targetName.length() != 0) {
                    target = (((flags & 0x1) != 0x0) ? targetName.getBytes("UnicodeLittleUnmarked") : targetName.toUpperCase().getBytes(NtlmMessage.getOEMEncoding()));
                }
                else {
                    flags &= 0xFFF8FFFF;
                }
            }
            if (targetInformation != null) {
                flags ^= 0x800000;
                if (context == null) {
                    context = new byte[8];
                }
            }
            int data = 32;
            if (context != null) {
                data += 8;
            }
            if (targetInformation != null) {
                data += 8;
            }
            final byte[] type2 = new byte[data + target.length + ((targetInformation != null) ? targetInformation.length : 0)];
            System.arraycopy(Type2Message.NTLMSSP_SIGNATURE, 0, type2, 0, 8);
            NtlmMessage.writeULong(type2, 8, 2);
            NtlmMessage.writeSecurityBuffer(type2, 12, data, target);
            NtlmMessage.writeULong(type2, 20, flags);
            System.arraycopy((challenge != null) ? challenge : new byte[8], 0, type2, 24, 8);
            if (context != null) {
                System.arraycopy(context, 0, type2, 32, 8);
            }
            if (targetInformation != null) {
                NtlmMessage.writeSecurityBuffer(type2, 40, data + target.length, targetInformation);
            }
            return type2;
        }
        catch (final IOException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
    
    public String toString() {
        final String target = this.getTarget();
        final byte[] challenge = this.getChallenge();
        final byte[] context = this.getContext();
        final byte[] targetInformation = this.getTargetInformation();
        final int flags = this.getFlags();
        final StringBuffer buffer = new StringBuffer();
        if (target != null) {
            buffer.append("target: ").append(target);
        }
        if (challenge != null) {
            if (buffer.length() > 0) {
                buffer.append("; ");
            }
            buffer.append("challenge: ");
            buffer.append("0x");
            for (int i = 0; i < challenge.length; ++i) {
                buffer.append(Integer.toHexString(challenge[i] >> 4 & 0xF));
                buffer.append(Integer.toHexString(challenge[i] & 0xF));
            }
        }
        if (context != null) {
            if (buffer.length() > 0) {
                buffer.append("; ");
            }
            buffer.append("context: ");
            buffer.append("0x");
            for (int i = 0; i < context.length; ++i) {
                buffer.append(Integer.toHexString(context[i] >> 4 & 0xF));
                buffer.append(Integer.toHexString(context[i] & 0xF));
            }
        }
        if (targetInformation != null) {
            if (buffer.length() > 0) {
                buffer.append("; ");
            }
            buffer.append("targetInformation: ");
            buffer.append("0x");
            for (int i = 0; i < targetInformation.length; ++i) {
                buffer.append(Integer.toHexString(targetInformation[i] >> 4 & 0xF));
                buffer.append(Integer.toHexString(targetInformation[i] & 0xF));
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
        return Type2Message.DEFAULT_FLAGS;
    }
    
    public static int getDefaultFlags(final Type1Message type1) {
        if (type1 == null) {
            return Type2Message.DEFAULT_FLAGS;
        }
        int flags = 512;
        final int type1Flags = type1.getFlags();
        flags |= (((type1Flags & 0x1) != 0x0) ? 1 : 2);
        if ((type1Flags & 0x4) != 0x0) {
            final String domain = getDefaultDomain();
            if (domain != null) {
                flags |= 0x10004;
            }
        }
        return flags;
    }
    
    public static String getDefaultDomain() {
        return Type2Message.DEFAULT_DOMAIN;
    }
    
    public static byte[] getDefaultTargetInformation() {
        return Type2Message.DEFAULT_TARGET_INFORMATION;
    }
    
    private void parse(final byte[] material) throws IOException {
        for (int i = 0; i < 8; ++i) {
            if (material[i] != Type2Message.NTLMSSP_SIGNATURE[i]) {
                throw new IOException("Not an NTLMSSP message.");
            }
        }
        if (NtlmMessage.readULong(material, 8) != 2) {
            throw new IOException("Not a Type 2 message.");
        }
        final int flags = NtlmMessage.readULong(material, 20);
        this.setFlags(flags);
        String target = null;
        byte[] bytes = NtlmMessage.readSecurityBuffer(material, 12);
        if (bytes.length != 0) {
            target = new String(bytes, ((flags & 0x1) != 0x0) ? "UnicodeLittleUnmarked" : NtlmMessage.getOEMEncoding());
        }
        this.setTarget(target);
        for (int j = 24; j < 32; ++j) {
            if (material[j] != 0) {
                final byte[] challenge = new byte[8];
                System.arraycopy(material, 24, challenge, 0, 8);
                this.setChallenge(challenge);
                break;
            }
        }
        final int offset = NtlmMessage.readULong(material, 16);
        if (offset == 32 || material.length == 32) {
            return;
        }
        for (int k = 32; k < 40; ++k) {
            if (material[k] != 0) {
                final byte[] context = new byte[8];
                System.arraycopy(material, 32, context, 0, 8);
                this.setContext(context);
                break;
            }
        }
        if (offset == 40 || material.length == 40) {
            return;
        }
        bytes = NtlmMessage.readSecurityBuffer(material, 40);
        if (bytes.length != 0) {
            this.setTargetInformation(bytes);
        }
    }
    
    static {
        DEFAULT_FLAGS = (0x200 | (Config.getBoolean("jcifs.smb.client.useUnicode", true) ? 1 : 2));
        DEFAULT_DOMAIN = Config.getProperty("jcifs.smb.client.domain", null);
        byte[] domain = new byte[0];
        if (Type2Message.DEFAULT_DOMAIN != null) {
            try {
                domain = Type2Message.DEFAULT_DOMAIN.getBytes("UnicodeLittleUnmarked");
            }
            catch (final IOException ex) {}
        }
        final int domainLength = domain.length;
        byte[] server = new byte[0];
        try {
            final String host = NbtAddress.getLocalHost().getHostName();
            if (host != null) {
                try {
                    server = host.getBytes("UnicodeLittleUnmarked");
                }
                catch (final IOException ex2) {}
            }
        }
        catch (final UnknownHostException ex3) {}
        final int serverLength = server.length;
        final byte[] targetInfo = new byte[((domainLength > 0) ? (domainLength + 4) : 0) + ((serverLength > 0) ? (serverLength + 4) : 0) + 4];
        int offset = 0;
        if (domainLength > 0) {
            NtlmMessage.writeUShort(targetInfo, offset, 2);
            offset += 2;
            NtlmMessage.writeUShort(targetInfo, offset, domainLength);
            offset += 2;
            System.arraycopy(domain, 0, targetInfo, offset, domainLength);
            offset += domainLength;
        }
        if (serverLength > 0) {
            NtlmMessage.writeUShort(targetInfo, offset, 1);
            offset += 2;
            NtlmMessage.writeUShort(targetInfo, offset, serverLength);
            offset += 2;
            System.arraycopy(server, 0, targetInfo, offset, serverLength);
        }
        DEFAULT_TARGET_INFORMATION = targetInfo;
    }
}
