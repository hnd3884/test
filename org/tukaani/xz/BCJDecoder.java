package org.tukaani.xz;

import org.tukaani.xz.simple.SimpleFilter;
import org.tukaani.xz.simple.SPARC;
import org.tukaani.xz.simple.ARMThumb;
import org.tukaani.xz.simple.ARM;
import org.tukaani.xz.simple.IA64;
import org.tukaani.xz.simple.PowerPC;
import org.tukaani.xz.simple.X86;
import java.io.InputStream;

class BCJDecoder extends BCJCoder implements FilterDecoder
{
    private final long filterID;
    private final int startOffset;
    
    BCJDecoder(final long filterID, final byte[] array) throws UnsupportedOptionsException {
        assert BCJCoder.isBCJFilterID(filterID);
        this.filterID = filterID;
        if (array.length == 0) {
            this.startOffset = 0;
        }
        else {
            if (array.length != 4) {
                throw new UnsupportedOptionsException("Unsupported BCJ filter properties");
            }
            int startOffset = 0;
            for (int i = 0; i < 4; ++i) {
                startOffset |= (array[i] & 0xFF) << i * 8;
            }
            this.startOffset = startOffset;
        }
    }
    
    @Override
    public int getMemoryUsage() {
        return SimpleInputStream.getMemoryUsage();
    }
    
    @Override
    public InputStream getInputStream(final InputStream inputStream, final ArrayCache arrayCache) {
        SimpleFilter simpleFilter = null;
        if (this.filterID == 4L) {
            simpleFilter = new X86(false, this.startOffset);
        }
        else if (this.filterID == 5L) {
            simpleFilter = new PowerPC(false, this.startOffset);
        }
        else if (this.filterID == 6L) {
            simpleFilter = new IA64(false, this.startOffset);
        }
        else if (this.filterID == 7L) {
            simpleFilter = new ARM(false, this.startOffset);
        }
        else if (this.filterID == 8L) {
            simpleFilter = new ARMThumb(false, this.startOffset);
        }
        else if (this.filterID == 9L) {
            simpleFilter = new SPARC(false, this.startOffset);
        }
        else {
            assert false;
        }
        return new SimpleInputStream(inputStream, simpleFilter);
    }
}
