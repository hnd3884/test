package jcifs.smb;

import jcifs.Config;

class SmbComSessionSetupAndX extends AndXServerMessageBlock
{
    private static final int BATCH_LIMIT;
    private static final boolean DISABLE_PLAIN_TEXT_PASSWORDS;
    private byte[] accountPassword;
    private byte[] unicodePassword;
    private int passwordLength;
    private int unicodePasswordLength;
    private int sessionKey;
    private String accountName;
    private String primaryDomain;
    SmbSession session;
    NtlmPasswordAuthentication auth;
    
    SmbComSessionSetupAndX(final SmbSession session, final ServerMessageBlock andx) throws SmbException {
        super(andx);
        this.command = 115;
        this.session = session;
        this.auth = session.auth;
        if (this.auth.hashesExternal && this.auth.challenge != session.transport.server.encryptionKey) {
            throw new SmbAuthException(-1073741819);
        }
    }
    
    int getBatchLimit(final byte command) {
        return (command == 117) ? SmbComSessionSetupAndX.BATCH_LIMIT : 0;
    }
    
    int writeParameterWordsWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        if (this.session.transport.server.security == 1 && (this.auth.hashesExternal || this.auth.password.length() > 0)) {
            if (this.session.transport.server.encryptedPasswords) {
                this.accountPassword = this.auth.getAnsiHash(this.session.transport.server.encryptionKey);
                this.passwordLength = this.accountPassword.length;
                this.unicodePassword = this.auth.getUnicodeHash(this.session.transport.server.encryptionKey);
                this.unicodePasswordLength = this.unicodePassword.length;
                if (this.unicodePasswordLength == 0 && this.passwordLength == 0) {
                    throw new RuntimeException("Null setup prohibited.");
                }
            }
            else {
                if (SmbComSessionSetupAndX.DISABLE_PLAIN_TEXT_PASSWORDS) {
                    throw new RuntimeException("Plain text passwords are disabled");
                }
                if (this.useUnicode) {
                    final String password = this.auth.getPassword();
                    this.accountPassword = new byte[0];
                    this.passwordLength = 0;
                    this.unicodePassword = new byte[(password.length() + 1) * 2];
                    this.unicodePasswordLength = this.writeString(password, this.unicodePassword, 0);
                }
                else {
                    final String password = this.auth.getPassword();
                    this.accountPassword = new byte[(password.length() + 1) * 2];
                    this.passwordLength = this.writeString(password, this.accountPassword, 0);
                    this.unicodePassword = new byte[0];
                    this.unicodePasswordLength = 0;
                }
            }
        }
        else {
            final int n = 0;
            this.unicodePasswordLength = n;
            this.passwordLength = n;
        }
        this.sessionKey = this.session.transport.sessionKey;
        ServerMessageBlock.writeInt2(this.session.transport.snd_buf_size, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(this.session.transport.maxMpxCount, dst, dstIndex);
        dstIndex += 2;
        final SmbTransport transport = this.session.transport;
        ServerMessageBlock.writeInt2(1L, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt4(this.sessionKey, dst, dstIndex);
        dstIndex += 4;
        ServerMessageBlock.writeInt2(this.passwordLength, dst, dstIndex);
        dstIndex += 2;
        ServerMessageBlock.writeInt2(this.unicodePasswordLength, dst, dstIndex);
        dstIndex += 2;
        dst[dstIndex++] = 0;
        dst[dstIndex++] = 0;
        dst[dstIndex++] = 0;
        dst[dstIndex++] = 0;
        ServerMessageBlock.writeInt4(this.session.transport.capabilities, dst, dstIndex);
        dstIndex += 4;
        return dstIndex - start;
    }
    
    int writeBytesWireFormat(final byte[] dst, int dstIndex) {
        final int start = dstIndex;
        this.accountName = (this.useUnicode ? this.auth.username : this.auth.username.toUpperCase());
        this.primaryDomain = this.auth.domain.toUpperCase();
        if (this.session.transport.server.security == 1 && (this.auth.hashesExternal || this.auth.password.length() > 0)) {
            System.arraycopy(this.accountPassword, 0, dst, dstIndex, this.passwordLength);
            dstIndex += this.passwordLength;
            if (!this.session.transport.server.encryptedPasswords && this.useUnicode && (dstIndex - this.headerStart) % 2 != 0) {
                dst[dstIndex++] = 0;
            }
            System.arraycopy(this.unicodePassword, 0, dst, dstIndex, this.unicodePasswordLength);
            dstIndex += this.unicodePasswordLength;
        }
        dstIndex += this.writeString(this.accountName, dst, dstIndex);
        final int n;
        dstIndex = (n = dstIndex + this.writeString(this.primaryDomain, dst, dstIndex));
        final SmbTransport transport = this.session.transport;
        final int n2;
        dstIndex = (n2 = n + this.writeString(SmbConstants.NATIVE_OS, dst, dstIndex));
        final SmbTransport transport2 = this.session.transport;
        dstIndex = n2 + this.writeString(SmbConstants.NATIVE_LANMAN, dst, dstIndex);
        return dstIndex - start;
    }
    
    int readParameterWordsWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    int readBytesWireFormat(final byte[] buffer, final int bufferIndex) {
        return 0;
    }
    
    public String toString() {
        final StringBuffer append = new StringBuffer().append("SmbComSessionSetupAndX[").append(super.toString()).append(",snd_buf_size=").append(this.session.transport.snd_buf_size).append(",maxMpxCount=").append(this.session.transport.maxMpxCount).append(",VC_NUMBER=");
        final SmbTransport transport = this.session.transport;
        final StringBuffer append2 = append.append(1).append(",sessionKey=").append(this.sessionKey).append(",passwordLength=").append(this.passwordLength).append(",unicodePasswordLength=").append(this.unicodePasswordLength).append(",capabilities=").append(this.session.transport.capabilities).append(",accountName=").append(this.accountName).append(",primaryDomain=").append(this.primaryDomain).append(",NATIVE_OS=");
        final SmbTransport transport2 = this.session.transport;
        final StringBuffer append3 = append2.append(SmbConstants.NATIVE_OS).append(",NATIVE_LANMAN=");
        final SmbTransport transport3 = this.session.transport;
        final String result = new String(append3.append(SmbConstants.NATIVE_LANMAN).append("]").toString());
        return result;
    }
    
    static {
        BATCH_LIMIT = Config.getInt("jcifs.smb.client.SessionSetupAndX.TreeConnectAndX", 1);
        DISABLE_PLAIN_TEXT_PASSWORDS = Config.getBoolean("jcifs.smb.client.disablePlainTextPasswords", true);
    }
}
