package org.tukaani.xz;

import java.io.InputStream;

class LZMA2Decoder extends LZMA2Coder implements FilterDecoder
{
    private int dictSize;
    
    LZMA2Decoder(final byte[] array) throws UnsupportedOptionsException {
        if (array.length != 1 || (array[0] & 0xFF) > 37) {
            throw new UnsupportedOptionsException("Unsupported LZMA2 properties");
        }
        this.dictSize = (0x2 | (array[0] & 0x1));
        this.dictSize <<= (array[0] >>> 1) + 11;
    }
    
    @Override
    public int getMemoryUsage() {
        return LZMA2InputStream.getMemoryUsage(this.dictSize);
    }
    
    @Override
    public InputStream getInputStream(final InputStream inputStream, final ArrayCache arrayCache) {
        return new LZMA2InputStream(inputStream, this.dictSize, null, arrayCache);
    }
}
