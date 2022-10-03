package jcifs.smb;

import jcifs.Config;
import jcifs.util.Hexdump;
import java.security.NoSuchAlgorithmException;
import java.io.PrintStream;
import java.security.MessageDigest;
import jcifs.util.LogStream;

public class SigningDigest
{
    private static final int LM_COMPATIBILITY;
    static LogStream log;
    private MessageDigest digest;
    private byte[] macSigningKey;
    private int updates;
    private int signSequence;
    
    public SigningDigest(final SmbTransport transport, final NtlmPasswordAuthentication auth) throws SmbException {
        try {
            this.digest = MessageDigest.getInstance("MD5");
        }
        catch (final NoSuchAlgorithmException ex) {
            if (LogStream.level > 0) {
                ex.printStackTrace(SigningDigest.log);
            }
            throw new SmbException("MD5", ex);
        }
        try {
            switch (SigningDigest.LM_COMPATIBILITY) {
                case 0:
                case 1:
                case 2: {
                    this.macSigningKey = new byte[40];
                    auth.getUserSessionKey(transport.server.encryptionKey, this.macSigningKey, 0);
                    System.arraycopy(auth.getUnicodeHash(transport.server.encryptionKey), 0, this.macSigningKey, 16, 24);
                    break;
                }
                case 3:
                case 4:
                case 5: {
                    this.macSigningKey = new byte[16];
                    auth.getUserSessionKey(transport.server.encryptionKey, this.macSigningKey, 0);
                    break;
                }
                default: {
                    this.macSigningKey = new byte[40];
                    auth.getUserSessionKey(transport.server.encryptionKey, this.macSigningKey, 0);
                    System.arraycopy(auth.getUnicodeHash(transport.server.encryptionKey), 0, this.macSigningKey, 16, 24);
                    break;
                }
            }
        }
        catch (final Exception ex2) {
            throw new SmbException("", ex2);
        }
        if (LogStream.level >= 4) {
            SigningDigest.log.println("LM_COMPATIBILITY=" + SigningDigest.LM_COMPATIBILITY);
            Hexdump.hexdump(SigningDigest.log, this.macSigningKey, 0, this.macSigningKey.length);
        }
    }
    
    public void update(final byte[] input, final int offset, final int len) {
        if (LogStream.level >= 4) {
            SigningDigest.log.println("update: " + this.updates + " " + offset + ":" + len);
            Hexdump.hexdump(SigningDigest.log, input, offset, Math.min(len, 256));
            SigningDigest.log.flush();
        }
        if (len == 0) {
            return;
        }
        this.digest.update(input, offset, len);
        ++this.updates;
    }
    
    public byte[] digest() {
        final byte[] b = this.digest.digest();
        if (LogStream.level >= 4) {
            SigningDigest.log.println("digest: ");
            Hexdump.hexdump(SigningDigest.log, b, 0, b.length);
            SigningDigest.log.flush();
        }
        this.updates = 0;
        return b;
    }
    
    void sign(final byte[] data, final int offset, final int length, final ServerMessageBlock request, final ServerMessageBlock response) {
        request.signSeq = this.signSequence;
        if (response != null) {
            response.signSeq = this.signSequence + 1;
            response.verifyFailed = false;
        }
        try {
            this.update(this.macSigningKey, 0, this.macSigningKey.length);
            final int index = offset + 14;
            for (int i = 0; i < 8; ++i) {
                data[index + i] = 0;
            }
            ServerMessageBlock.writeInt4(this.signSequence, data, index);
            this.update(data, offset, length);
            System.arraycopy(this.digest(), 0, data, index, 8);
        }
        catch (final Exception ex) {
            if (LogStream.level > 0) {
                ex.printStackTrace(SigningDigest.log);
            }
        }
        finally {
            this.signSequence += 2;
        }
    }
    
    boolean verify(final byte[] data, final int offset, final ServerMessageBlock response) {
        this.update(this.macSigningKey, 0, this.macSigningKey.length);
        int index = offset;
        this.update(data, index, 14);
        index += 14;
        final byte[] sequence = new byte[8];
        ServerMessageBlock.writeInt4(response.signSeq, sequence, 0);
        this.update(sequence, 0, sequence.length);
        index += 8;
        if (response.command == 46) {
            final SmbComReadAndXResponse raxr = (SmbComReadAndXResponse)response;
            final int length = response.length - raxr.dataLength;
            this.update(data, index, length - 14 - 8);
            this.update(raxr.b, raxr.off, raxr.dataLength);
        }
        else {
            this.update(data, index, response.length - 14 - 8);
        }
        final byte[] signature = this.digest();
        for (int i = 0; i < 8; ++i) {
            if (signature[i] != data[offset + 14 + i]) {
                if (LogStream.level >= 2) {
                    SigningDigest.log.println("signature verification failure");
                    Hexdump.hexdump(SigningDigest.log, signature, 0, 8);
                    Hexdump.hexdump(SigningDigest.log, data, offset + 14, 8);
                }
                return response.verifyFailed = true;
            }
        }
        return response.verifyFailed = false;
    }
    
    public SigningDigest(final byte[] macSigningKey) throws SmbException {
        try {
            this.digest = MessageDigest.getInstance("MD5");
        }
        catch (final NoSuchAlgorithmException ex) {
            if (LogStream.level > 2) {
                ex.printStackTrace(SigningDigest.log);
            }
            throw new SmbException("MD5", ex);
        }
        this.macSigningKey = macSigningKey;
    }
    
    static {
        LM_COMPATIBILITY = Config.getInt("jcifs.smb.lmCompatibility", 0);
        SigningDigest.log = LogStream.getInstance();
    }
}
