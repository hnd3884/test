package org.apache.poi.hpsf;

import org.apache.poi.util.POILogFactory;
import java.io.Closeable;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Internal;

@Internal
public class ClipboardData
{
    private static final int MAX_RECORD_LENGTH = 100000000;
    private static final POILogger LOG;
    private int _format;
    private byte[] _value;
    
    public void read(final LittleEndianByteArrayInputStream lei) {
        final int offset = lei.getReadIndex();
        final int size = lei.readInt();
        if (size < 4) {
            final String msg = "ClipboardData at offset " + offset + " size less than 4 bytes (doesn't even have format field!). Setting to format == 0 and hope for the best";
            ClipboardData.LOG.log(5, msg);
            this._format = 0;
            this._value = new byte[0];
            return;
        }
        this._format = lei.readInt();
        lei.readFully(this._value = IOUtils.safelyAllocate(size - 4, 100000000));
    }
    
    public byte[] getValue() {
        return this._value;
    }
    
    public byte[] toByteArray() {
        final byte[] result = new byte[8 + this._value.length];
        final LittleEndianByteArrayOutputStream bos = new LittleEndianByteArrayOutputStream(result, 0);
        try {
            bos.writeInt(4 + this._value.length);
            bos.writeInt(this._format);
            bos.write(this._value);
            return result;
        }
        finally {
            IOUtils.closeQuietly(bos);
        }
    }
    
    public void setValue(final byte[] value) {
        this._value = value.clone();
    }
    
    static {
        LOG = POILogFactory.getLogger(ClipboardData.class);
    }
}
