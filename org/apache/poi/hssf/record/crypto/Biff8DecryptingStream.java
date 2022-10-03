package org.apache.poi.hssf.record.crypto;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.SuppressForbidden;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.util.RecordFormatException;
import java.io.PushbackInputStream;
import org.apache.poi.util.IOUtils;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import java.io.InputStream;
import org.apache.poi.poifs.crypt.ChunkedCipherInputStream;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.hssf.record.BiffHeaderInput;

public final class Biff8DecryptingStream implements BiffHeaderInput, LittleEndianInput
{
    public static final int RC4_REKEYING_INTERVAL = 1024;
    private static final int MAX_RECORD_LENGTH = 100000;
    private ChunkedCipherInputStream ccis;
    private final byte[] buffer;
    private boolean shouldSkipEncryptionOnCurrentRecord;
    
    public Biff8DecryptingStream(final InputStream in, final int initialOffset, final EncryptionInfo info) throws RecordFormatException {
        this.buffer = new byte[8];
        try {
            final byte[] initialBuf = IOUtils.safelyAllocate(initialOffset, 100000);
            InputStream stream;
            if (initialOffset == 0) {
                stream = in;
            }
            else {
                stream = new PushbackInputStream(in, initialOffset);
                ((PushbackInputStream)stream).unread(initialBuf);
            }
            final Decryptor dec = info.getDecryptor();
            dec.setChunkSize(1024);
            this.ccis = (ChunkedCipherInputStream)dec.getDataStream(stream, Integer.MAX_VALUE, 0);
            if (initialOffset > 0) {
                this.ccis.readFully(initialBuf);
            }
        }
        catch (final Exception e) {
            throw new RecordFormatException(e);
        }
    }
    
    @SuppressForbidden("just delegating")
    @Override
    public int available() {
        return this.ccis.available();
    }
    
    @Override
    public int readRecordSID() {
        this.readPlain(this.buffer, 0, 2);
        final int sid = LittleEndian.getUShort(this.buffer, 0);
        this.shouldSkipEncryptionOnCurrentRecord = isNeverEncryptedRecord(sid);
        return sid;
    }
    
    @Override
    public int readDataSize() {
        this.readPlain(this.buffer, 0, 2);
        final int dataSize = LittleEndian.getUShort(this.buffer, 0);
        this.ccis.setNextRecordSize(dataSize);
        return dataSize;
    }
    
    @Override
    public double readDouble() {
        final long valueLongBits = this.readLong();
        final double result = Double.longBitsToDouble(valueLongBits);
        if (Double.isNaN(result)) {
            throw new RuntimeException("Did not expect to read NaN");
        }
        return result;
    }
    
    @Override
    public void readFully(final byte[] buf) {
        this.readFully(buf, 0, buf.length);
    }
    
    @Override
    public void readFully(final byte[] buf, final int off, final int len) {
        if (this.shouldSkipEncryptionOnCurrentRecord) {
            this.readPlain(buf, off, buf.length);
        }
        else {
            this.ccis.readFully(buf, off, len);
        }
    }
    
    @Override
    public int readUByte() {
        return this.readByte() & 0xFF;
    }
    
    @Override
    public byte readByte() {
        if (this.shouldSkipEncryptionOnCurrentRecord) {
            this.readPlain(this.buffer, 0, 1);
            return this.buffer[0];
        }
        return this.ccis.readByte();
    }
    
    @Override
    public int readUShort() {
        return this.readShort() & 0xFFFF;
    }
    
    @Override
    public short readShort() {
        if (this.shouldSkipEncryptionOnCurrentRecord) {
            this.readPlain(this.buffer, 0, 2);
            return LittleEndian.getShort(this.buffer);
        }
        return this.ccis.readShort();
    }
    
    @Override
    public int readInt() {
        if (this.shouldSkipEncryptionOnCurrentRecord) {
            this.readPlain(this.buffer, 0, 4);
            return LittleEndian.getInt(this.buffer);
        }
        return this.ccis.readInt();
    }
    
    @Override
    public long readLong() {
        if (this.shouldSkipEncryptionOnCurrentRecord) {
            this.readPlain(this.buffer, 0, 8);
            return LittleEndian.getLong(this.buffer);
        }
        return this.ccis.readLong();
    }
    
    public long getPosition() {
        return this.ccis.getPos();
    }
    
    public static boolean isNeverEncryptedRecord(final int sid) {
        switch (sid) {
            case 47:
            case 225:
            case 2057: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public void readPlain(final byte[] b, final int off, final int len) {
        this.ccis.readPlain(b, off, len);
    }
}
