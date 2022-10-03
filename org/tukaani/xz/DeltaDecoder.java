package org.tukaani.xz;

import java.io.InputStream;

class DeltaDecoder extends DeltaCoder implements FilterDecoder
{
    private final int distance;
    
    DeltaDecoder(final byte[] array) throws UnsupportedOptionsException {
        if (array.length != 1) {
            throw new UnsupportedOptionsException("Unsupported Delta filter properties");
        }
        this.distance = (array[0] & 0xFF) + 1;
    }
    
    @Override
    public int getMemoryUsage() {
        return 1;
    }
    
    @Override
    public InputStream getInputStream(final InputStream inputStream, final ArrayCache arrayCache) {
        return new DeltaInputStream(inputStream, this.distance);
    }
}
