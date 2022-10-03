package sun.java2d.pipe;

public class RegionSpanIterator implements SpanIterator
{
    RegionIterator ri;
    int lox;
    int loy;
    int hix;
    int hiy;
    int curloy;
    int curhiy;
    boolean done;
    boolean isrect;
    
    public RegionSpanIterator(final Region region) {
        this.done = false;
        final int[] array = new int[4];
        region.getBounds(array);
        this.lox = array[0];
        this.loy = array[1];
        this.hix = array[2];
        this.hiy = array[3];
        this.isrect = region.isRectangular();
        this.ri = region.getIterator();
    }
    
    @Override
    public void getPathBox(final int[] array) {
        array[0] = this.lox;
        array[1] = this.loy;
        array[2] = this.hix;
        array[3] = this.hiy;
    }
    
    @Override
    public void intersectClipBox(final int lox, final int loy, final int hix, final int hiy) {
        if (lox > this.lox) {
            this.lox = lox;
        }
        if (loy > this.loy) {
            this.loy = loy;
        }
        if (hix < this.hix) {
            this.hix = hix;
        }
        if (hiy < this.hiy) {
            this.hiy = hiy;
        }
        this.done = (this.lox >= this.hix || this.loy >= this.hiy);
    }
    
    @Override
    public boolean nextSpan(final int[] array) {
        if (this.done) {
            return false;
        }
        if (this.isrect) {
            this.getPathBox(array);
            return this.done = true;
        }
        int curloy = this.curloy;
        int curhiy = this.curhiy;
        while (true) {
            if (!this.ri.nextXBand(array)) {
                if (!this.ri.nextYRange(array)) {
                    this.done = true;
                    return false;
                }
                curloy = array[1];
                curhiy = array[3];
                if (curloy < this.loy) {
                    curloy = this.loy;
                }
                if (curhiy > this.hiy) {
                    curhiy = this.hiy;
                }
                if (curloy >= this.hiy) {
                    this.done = true;
                    return false;
                }
                continue;
            }
            else {
                int lox = array[0];
                int hix = array[2];
                if (lox < this.lox) {
                    lox = this.lox;
                }
                if (hix > this.hix) {
                    hix = this.hix;
                }
                if (lox < hix && curloy < curhiy) {
                    array[0] = lox;
                    array[1] = (this.curloy = curloy);
                    array[2] = hix;
                    array[3] = (this.curhiy = curhiy);
                    return true;
                }
                continue;
            }
        }
    }
    
    @Override
    public void skipDownTo(final int loy) {
        this.loy = loy;
    }
    
    @Override
    public long getNativeIterator() {
        return 0L;
    }
}
