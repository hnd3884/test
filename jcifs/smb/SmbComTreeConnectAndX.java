package jcifs.smb;

import jcifs.Config;
import jcifs.util.Hexdump;
import java.io.UnsupportedEncodingException;

class SmbComTreeConnectAndX extends AndXServerMessageBlock
{
    private static final boolean DISABLE_PLAIN_TEXT_PASSWORDS;
    private SmbSession session;
    private boolean disconnectTid;
    private String path;
    private String service;
    private byte[] password;
    private int passwordLength;
    private static byte[] batchLimits;
    
    SmbComTreeConnectAndX(final SmbSession session, final String path, final String service, final ServerMessageBlock andx) {
        super(andx);
        this.disconnectTid = false;
        this.session = session;
        this.path = path;
        this.service = service;
        this.command = 117;
    }
    
    int getBatchLimit(final byte command) {
        final int c = command & 0xFF;
        switch (c) {
            case 16: {
                return SmbComTreeConnectAndX.batchLimits[0];
            }
            case 0: {
                return SmbComTreeConnectAndX.batchLimits[2];
            }
            case 6: {
                return SmbComTreeConnectAndX.batchLimits[3];
            }
            case 1: {
                return SmbComTreeConnectAndX.batchLimits[4];
            }
            case 45: {
                return SmbComTreeConnectAndX.batchLimits[5];
            }
            case 7: {
                return SmbComTreeConnectAndX.batchLimits[6];
            }
            case 37: {
                return SmbComTreeConnectAndX.batchLimits[7];
            }
            case 8: {
                return SmbComTreeConnectAndX.batchLimits[8];
            }
            default: {
                return 0;
            }
        }
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, int dstIndex) {
        if (this.session.transport.server.security == 0 && (this.session.auth.hashesExternal || this.session.auth.password.length() > 0)) {
            if (this.session.transport.server.encryptedPasswords) {
                this.password = this.session.auth.getAnsiHash(this.session.transport.server.encryptionKey);
                this.passwordLength = this.password.length;
            }
            else {
                if (SmbComTreeConnectAndX.DISABLE_PLAIN_TEXT_PASSWORDS) {
                    throw new RuntimeException("Plain text passwords are disabled");
                }
                this.password = new byte[(this.session.auth.password.length() + 1) * 2];
                this.passwordLength = this.writeString(this.session.auth.password, this.password, 0);
            }
        }
        else {
            this.passwordLength = 1;
        }
        dst[dstIndex++] = (byte)(this.disconnectTid ? 1 : 0);
        dst[dstIndex++] = 0;
        ServerMessageBlock.writeInt2(this.passwordLength, dst, dstIndex);
        return 4;
    }
    
    int writeBytesWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        if (this.session.transport.server.security == 0 && (this.session.auth.hashesExternal || this.session.auth.password.length() > 0)) {
            System.arraycopy(this.password, 0, dst, dstIndex, this.passwordLength);
            dstIndex += this.passwordLength;
        }
        else {
            dst[dstIndex++] = 0;
        }
        dstIndex += this.writeString(this.path, dst, dstIndex);
        try {
            System.arraycopy(this.service.getBytes("ASCII"), 0, dst, dstIndex, this.service.length());
        }
        catch (final UnsupportedEncodingException uee) {
            return 0;
        }
        dstIndex += this.service.length();
        dst[dstIndex++] = 0;
        return dstIndex - start;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    public String toString() {
        final String result = new String("SmbComTreeConnectAndX[" + super.toString() + ",disconnectTid=" + this.disconnectTid + ",passwordLength=" + this.passwordLength + ",password=" + Hexdump.toHexString(this.password, this.passwordLength, 0) + ",path=" + this.path + ",service=" + this.service + "]");
        return result;
    }
    
    static {
        DISABLE_PLAIN_TEXT_PASSWORDS = Config.getBoolean("jcifs.smb.client.disablePlainTextPasswords", true);
        SmbComTreeConnectAndX.batchLimits = new byte[] { 1, 1, 1, 1, 1, 1, 1, 1, 0 };
        String s;
        if ((s = Config.getProperty("jcifs.smb.client.TreeConnectAndX.CheckDirectory")) != null) {
            SmbComTreeConnectAndX.batchLimits[0] = Byte.parseByte(s);
        }
        if ((s = Config.getProperty("jcifs.smb.client.TreeConnectAndX.CreateDirectory")) != null) {
            SmbComTreeConnectAndX.batchLimits[2] = Byte.parseByte(s);
        }
        if ((s = Config.getProperty("jcifs.smb.client.TreeConnectAndX.Delete")) != null) {
            SmbComTreeConnectAndX.batchLimits[3] = Byte.parseByte(s);
        }
        if ((s = Config.getProperty("jcifs.smb.client.TreeConnectAndX.DeleteDirectory")) != null) {
            SmbComTreeConnectAndX.batchLimits[4] = Byte.parseByte(s);
        }
        if ((s = Config.getProperty("jcifs.smb.client.TreeConnectAndX.OpenAndX")) != null) {
            SmbComTreeConnectAndX.batchLimits[5] = Byte.parseByte(s);
        }
        if ((s = Config.getProperty("jcifs.smb.client.TreeConnectAndX.Rename")) != null) {
            SmbComTreeConnectAndX.batchLimits[6] = Byte.parseByte(s);
        }
        if ((s = Config.getProperty("jcifs.smb.client.TreeConnectAndX.Transaction")) != null) {
            SmbComTreeConnectAndX.batchLimits[7] = Byte.parseByte(s);
        }
        if ((s = Config.getProperty("jcifs.smb.client.TreeConnectAndX.QueryInformation")) != null) {
            SmbComTreeConnectAndX.batchLimits[8] = Byte.parseByte(s);
        }
    }
}
