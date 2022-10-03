package org.apache.poi.xssf.binary;

import org.apache.poi.util.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import com.zaxxer.sparsebits.SparseBitSet;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.Internal;

@Internal
public abstract class XSSFBParser
{
    private static final int MAX_RECORD_LENGTH = 1000000;
    private final LittleEndianInputStream is;
    private final SparseBitSet records;
    
    public XSSFBParser(final InputStream is) {
        this.is = new LittleEndianInputStream(is);
        this.records = null;
    }
    
    protected XSSFBParser(final InputStream is, final SparseBitSet bitSet) {
        this.is = new LittleEndianInputStream(is);
        this.records = bitSet;
    }
    
    public void parse() throws IOException {
        while (true) {
            final int bInt = this.is.read();
            if (bInt == -1) {
                break;
            }
            this.readNext((byte)bInt);
        }
    }
    
    private void readNext(byte b1) throws IOException {
        int recordId = 0;
        if ((b1 >> 7 & 0x1) == 0x1) {
            byte b2 = this.is.readByte();
            b1 &= (byte)(-129);
            b2 &= (byte)(-129);
            recordId = (b2 << 7) + b1;
        }
        else {
            recordId = b1;
        }
        long recordLength = 0L;
        int i = 0;
        byte b3;
        for (boolean halt = false; i < 4 && !halt; halt = ((b3 >> 7 & 0x1) == 0x0), b3 &= (byte)(-129), recordLength += b3 << i * 7, ++i) {
            b3 = this.is.readByte();
        }
        if (this.records == null || this.records.get(recordId)) {
            final byte[] buff = IOUtils.safelyAllocate(recordLength, 1000000);
            this.is.readFully(buff);
            this.handleRecord(recordId, buff);
        }
        else {
            final long length = IOUtils.skipFully((InputStream)this.is, recordLength);
            if (length != recordLength) {
                throw new XSSFBParseException("End of file reached before expected.\tTried to skip " + recordLength + ", but only skipped " + length);
            }
        }
    }
    
    public abstract void handleRecord(final int p0, final byte[] p1) throws XSSFBParseException;
}
