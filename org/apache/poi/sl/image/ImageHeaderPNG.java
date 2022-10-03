package org.apache.poi.sl.image;

import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.IOUtils;
import org.apache.poi.poifs.filesystem.FileMagic;
import java.io.ByteArrayInputStream;

public final class ImageHeaderPNG
{
    private static final int MAGIC_OFFSET = 16;
    private byte[] data;
    
    public ImageHeaderPNG(final byte[] data) {
        this.data = data;
    }
    
    public byte[] extractPNG() {
        try (final InputStream is = new ByteArrayInputStream(this.data)) {
            if (is.skip(16L) == 16L && FileMagic.valueOf(is) == FileMagic.PNG) {
                return IOUtils.toByteArray(is);
            }
        }
        catch (final IOException e) {
            throw new RecordFormatException("Unable to parse PNG header", e);
        }
        return this.data;
    }
}
