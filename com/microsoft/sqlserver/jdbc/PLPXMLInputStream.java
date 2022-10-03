package com.microsoft.sqlserver.jdbc;

import java.io.IOException;
import java.io.ByteArrayInputStream;

final class PLPXMLInputStream extends PLPInputStream
{
    private static final byte[] xmlBOM;
    private final ByteArrayInputStream bomStream;
    
    static final PLPXMLInputStream makeXMLStream(final TDSReader tdsReader, final InputStreamGetterArgs getterArgs, final ServerDTVImpl dtv) throws SQLServerException {
        final long payloadLength = tdsReader.readLong();
        if (-1L == payloadLength) {
            return null;
        }
        final PLPXMLInputStream is = new PLPXMLInputStream(tdsReader, payloadLength, getterArgs, dtv);
        is.setLoggingInfo(getterArgs.logContext);
        return is;
    }
    
    PLPXMLInputStream(final TDSReader tdsReader, final long statedPayloadLength, final InputStreamGetterArgs getterArgs, final ServerDTVImpl dtv) throws SQLServerException {
        super(tdsReader, statedPayloadLength, getterArgs.isAdaptive, getterArgs.isStreaming, dtv);
        this.bomStream = new ByteArrayInputStream(PLPXMLInputStream.xmlBOM);
    }
    
    @Override
    int readBytes(final byte[] b, final int offset, final int maxBytes) throws IOException {
        assert offset >= 0;
        assert maxBytes >= 0;
        if (0 == maxBytes) {
            return 0;
        }
        int bytesRead = 0;
        int xmlBytesRead = 0;
        if (null == b) {
            int bomBytesSkipped;
            while (bytesRead < maxBytes && 0 != (bomBytesSkipped = (int)this.bomStream.skip(maxBytes - (long)bytesRead))) {
                bytesRead += bomBytesSkipped;
            }
        }
        else {
            int bomBytesRead;
            while (bytesRead < maxBytes && -1 != (bomBytesRead = this.bomStream.read(b, offset + bytesRead, maxBytes - bytesRead))) {
                bytesRead += bomBytesRead;
            }
        }
        while (bytesRead < maxBytes && -1 != (xmlBytesRead = super.readBytes(b, offset + bytesRead, maxBytes - bytesRead))) {
            bytesRead += xmlBytesRead;
        }
        if (bytesRead > 0) {
            return bytesRead;
        }
        assert -1 == xmlBytesRead;
        return -1;
    }
    
    @Override
    public void mark(final int readLimit) {
        this.bomStream.mark(PLPXMLInputStream.xmlBOM.length);
        super.mark(readLimit);
    }
    
    @Override
    public void reset() throws IOException {
        this.bomStream.reset();
        super.reset();
    }
    
    @Override
    byte[] getBytes() throws SQLServerException {
        final byte[] bom = new byte[2];
        byte[] bytesToReturn = null;
        try {
            final int bytesread = this.bomStream.read(bom);
            final byte[] valueWithoutBOM = super.getBytes();
            if (bytesread > 0) {
                assert 2 == bytesread;
                final byte[] valueWithBOM = new byte[valueWithoutBOM.length + bytesread];
                System.arraycopy(bom, 0, valueWithBOM, 0, bytesread);
                System.arraycopy(valueWithoutBOM, 0, valueWithBOM, bytesread, valueWithoutBOM.length);
                bytesToReturn = valueWithBOM;
            }
            else {
                bytesToReturn = valueWithoutBOM;
            }
        }
        catch (final IOException e) {
            SQLServerException.makeFromDriverError(null, null, e.getMessage(), null, true);
        }
        return bytesToReturn;
    }
    
    static {
        xmlBOM = new byte[] { -1, -2 };
    }
}
