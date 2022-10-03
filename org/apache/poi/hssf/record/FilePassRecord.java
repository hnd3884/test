package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.Removal;
import java.io.OutputStream;
import org.apache.poi.util.LittleEndianOutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIEncryptionVerifier;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIEncryptionHeader;
import org.apache.poi.poifs.crypt.binaryrc4.BinaryRC4EncryptionVerifier;
import org.apache.poi.poifs.crypt.binaryrc4.BinaryRC4EncryptionHeader;
import org.apache.poi.poifs.crypt.xor.XOREncryptionVerifier;
import org.apache.poi.poifs.crypt.xor.XOREncryptionHeader;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.LittleEndianOutput;
import java.io.IOException;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.EncryptionInfo;

public final class FilePassRecord extends StandardRecord
{
    public static final short sid = 47;
    private static final int ENCRYPTION_XOR = 0;
    private static final int ENCRYPTION_OTHER = 1;
    private final int encryptionType;
    private EncryptionInfo encryptionInfo;
    
    private FilePassRecord(final FilePassRecord other) {
        super(other);
        this.encryptionType = other.encryptionType;
        this.encryptionInfo = other.encryptionInfo.copy();
    }
    
    public FilePassRecord(final EncryptionMode encryptionMode) {
        this.encryptionType = ((encryptionMode != EncryptionMode.xor) ? 1 : 0);
        this.encryptionInfo = new EncryptionInfo(encryptionMode);
    }
    
    public FilePassRecord(final RecordInputStream in) {
        EncryptionMode preferredMode = null;
        switch (this.encryptionType = in.readUShort()) {
            case 0: {
                preferredMode = EncryptionMode.xor;
                break;
            }
            case 1: {
                preferredMode = EncryptionMode.cryptoAPI;
                break;
            }
            default: {
                throw new EncryptedDocumentException("invalid encryption type");
            }
        }
        try {
            this.encryptionInfo = new EncryptionInfo(in, preferredMode);
        }
        catch (final IOException e) {
            throw new EncryptedDocumentException(e);
        }
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.encryptionType);
        final byte[] data = new byte[1024];
        try (final LittleEndianByteArrayOutputStream bos = new LittleEndianByteArrayOutputStream(data, 0)) {
            switch (this.encryptionInfo.getEncryptionMode()) {
                case xor: {
                    ((XOREncryptionHeader)this.encryptionInfo.getHeader()).write(bos);
                    ((XOREncryptionVerifier)this.encryptionInfo.getVerifier()).write(bos);
                    break;
                }
                case binaryRC4: {
                    out.writeShort(this.encryptionInfo.getVersionMajor());
                    out.writeShort(this.encryptionInfo.getVersionMinor());
                    ((BinaryRC4EncryptionHeader)this.encryptionInfo.getHeader()).write(bos);
                    ((BinaryRC4EncryptionVerifier)this.encryptionInfo.getVerifier()).write(bos);
                    break;
                }
                case cryptoAPI: {
                    out.writeShort(this.encryptionInfo.getVersionMajor());
                    out.writeShort(this.encryptionInfo.getVersionMinor());
                    out.writeInt(this.encryptionInfo.getEncryptionFlags());
                    ((CryptoAPIEncryptionHeader)this.encryptionInfo.getHeader()).write(bos);
                    ((CryptoAPIEncryptionVerifier)this.encryptionInfo.getVerifier()).write(bos);
                    break;
                }
                default: {
                    throw new EncryptedDocumentException("not supported");
                }
            }
            out.write(data, 0, bos.getWriteIndex());
        }
        catch (final IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }
    
    @Override
    protected int getDataSize() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final LittleEndianOutputStream leos = new LittleEndianOutputStream(bos);
        this.serialize(leos);
        return bos.size();
    }
    
    public EncryptionInfo getEncryptionInfo() {
        return this.encryptionInfo;
    }
    
    @Override
    public short getSid() {
        return 47;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public FilePassRecord clone() {
        return this.copy();
    }
    
    @Override
    public FilePassRecord copy() {
        return new FilePassRecord(this);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[FILEPASS]\n");
        buffer.append("    .type = ").append(HexDump.shortToHex(this.encryptionType)).append('\n');
        final String prefix = "     ." + this.encryptionInfo.getEncryptionMode();
        buffer.append(prefix + ".info = ").append(HexDump.shortToHex(this.encryptionInfo.getVersionMajor())).append('\n');
        buffer.append(prefix + ".ver  = ").append(HexDump.shortToHex(this.encryptionInfo.getVersionMinor())).append('\n');
        buffer.append(prefix + ".salt = ").append(HexDump.toHex(this.encryptionInfo.getVerifier().getSalt())).append('\n');
        buffer.append(prefix + ".verifier = ").append(HexDump.toHex(this.encryptionInfo.getVerifier().getEncryptedVerifier())).append('\n');
        buffer.append(prefix + ".verifierHash = ").append(HexDump.toHex(this.encryptionInfo.getVerifier().getEncryptedVerifierHash())).append('\n');
        buffer.append("[/FILEPASS]\n");
        return buffer.toString();
    }
}
