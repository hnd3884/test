package sun.java2d.pipe;

public class RegionClipSpanIterator implements SpanIterator
{
    Region rgn;
    SpanIterator spanIter;
    RegionIterator resetState;
    RegionIterator lwm;
    RegionIterator row;
    RegionIterator box;
    int spanlox;
    int spanhix;
    int spanloy;
    int spanhiy;
    int lwmloy;
    int lwmhiy;
    int rgnlox;
    int rgnloy;
    int rgnhix;
    int rgnhiy;
    int rgnbndslox;
    int rgnbndsloy;
    int rgnbndshix;
    int rgnbndshiy;
    int[] rgnbox;
    int[] spanbox;
    boolean doNextSpan;
    boolean doNextBox;
    boolean done;
    
    public RegionClipSpanIterator(final Region rgn, final SpanIterator spanIter) {
        this.rgnbox = new int[4];
        this.spanbox = new int[4];
        this.done = false;
        this.spanIter = spanIter;
        this.resetState = rgn.getIterator();
        this.lwm = this.resetState.createCopy();
        if (!this.lwm.nextYRange(this.rgnbox)) {
            this.done = true;
            return;
        }
        final int n = this.rgnbox[1];
        this.lwmloy = n;
        this.rgnloy = n;
        final int n2 = this.rgnbox[3];
        this.lwmhiy = n2;
        this.rgnhiy = n2;
        rgn.getBounds(this.rgnbox);
        this.rgnbndslox = this.rgnbox[0];
        this.rgnbndsloy = this.rgnbox[1];
        this.rgnbndshix = this.rgnbox[2];
        this.rgnbndshiy = this.rgnbox[3];
        if (this.rgnbndslox >= this.rgnbndshix || this.rgnbndsloy >= this.rgnbndshiy) {
            this.done = true;
            return;
        }
        this.rgn = rgn;
        this.row = this.lwm.createCopy();
        this.box = this.row.createCopy();
        this.doNextSpan = true;
        this.doNextBox = false;
    }
    
    @Override
    public void getPathBox(final int[] array) {
        final int[] array2 = new int[4];
        this.rgn.getBounds(array2);
        this.spanIter.getPathBox(array);
        if (array[0] < array2[0]) {
            array[0] = array2[0];
        }
        if (array[1] < array2[1]) {
            array[1] = array2[1];
        }
        if (array[2] > array2[2]) {
            array[2] = array2[2];
        }
        if (array[3] > array2[3]) {
            array[3] = array2[3];
        }
    }
    
    @Override
    public void intersectClipBox(final int n, final int n2, final int n3, final int n4) {
        this.spanIter.intersectClipBox(n, n2, n3, n4);
    }
    
    @Override
    public boolean nextSpan(final int[] array) {
        if (this.done) {
            return false;
        }
        int n = 0;
        while (true) {
            if (this.doNextSpan) {
                if (!this.spanIter.nextSpan(this.spanbox)) {
                    this.done = true;
                    return false;
                }
                this.spanlox = this.spanbox[0];
                if (this.spanlox >= this.rgnbndshix) {
                    continue;
                }
                this.spanloy = this.spanbox[1];
                if (this.spanloy >= this.rgnbndshiy) {
                    continue;
                }
                this.spanhix = this.spanbox[2];
                if (this.spanhix <= this.rgnbndslox) {
                    continue;
                }
                this.spanhiy = this.spanbox[3];
                if (this.spanhiy <= this.rgnbndsloy) {
                    continue;
                }
                if (this.lwmloy > this.spanloy) {
                    this.lwm.copyStateFrom(this.resetState);
                    this.lwm.nextYRange(this.rgnbox);
                    this.lwmloy = this.rgnbox[1];
                    this.lwmhiy = this.rgnbox[3];
                }
                while (this.lwmhiy <= this.spanloy && this.lwm.nextYRange(this.rgnbox)) {
                    this.lwmloy = this.rgnbox[1];
                    this.lwmhiy = this.rgnbox[3];
                }
                if (this.lwmhiy <= this.spanloy || this.lwmloy >= this.spanhiy) {
                    continue;
                }
                if (this.rgnloy != this.lwmloy) {
                    this.row.copyStateFrom(this.lwm);
                    this.rgnloy = this.lwmloy;
                    this.rgnhiy = this.lwmhiy;
                }
                this.box.copyStateFrom(this.row);
                this.doNextBox = true;
                this.doNextSpan = false;
            }
            else if (n != 0) {
                n = 0;
                final boolean nextYRange = this.row.nextYRange(this.rgnbox);
                if (nextYRange) {
                    this.rgnloy = this.rgnbox[1];
                    this.rgnhiy = this.rgnbox[3];
                }
                if (!nextYRange || this.rgnloy >= this.spanhiy) {
                    this.doNextSpan = true;
                }
                else {
                    this.box.copyStateFrom(this.row);
                    this.doNextBox = true;
                }
            }
            else if (this.doNextBox) {
                final boolean nextXBand = this.box.nextXBand(this.rgnbox);
                if (nextXBand) {
                    this.rgnlox = this.rgnbox[0];
                    this.rgnhix = this.rgnbox[2];
                }
                if (!nextXBand || this.rgnlox >= this.spanhix) {
                    this.doNextBox = false;
                    if (this.rgnhiy >= this.spanhiy) {
                        this.doNextSpan = true;
                    }
                    else {
                        n = 1;
                    }
                }
                else {
                    this.doNextBox = (this.rgnhix <= this.spanlox);
                }
            }
            else {
                this.doNextBox = true;
                int n2;
                if (this.spanlox > this.rgnlox) {
                    n2 = this.spanlox;
                }
                else {
                    n2 = this.rgnlox;
                }
                int n3;
                if (this.spanloy > this.rgnloy) {
                    n3 = this.spanloy;
                }
                else {
                    n3 = this.rgnloy;
                }
                int n4;
                if (this.spanhix < this.rgnhix) {
                    n4 = this.spanhix;
                }
                else {
                    n4 = this.rgnhix;
                }
                int n5;
                if (this.spanhiy < this.rgnhiy) {
                    n5 = this.spanhiy;
                }
                else {
                    n5 = this.rgnhiy;
                }
                if (n2 >= n4) {
                    continue;
                }
                if (n3 >= n5) {
                    continue;
                }
                array[0] = n2;
                array[1] = n3;
                array[2] = n4;
                array[3] = n5;
                return true;
            }
        }
    }
    
    @Override
    public void skipDownTo(final int n) {
        this.spanIter.skipDownTo(n);
    }
    
    @Override
    public long getNativeIterator() {
        return 0L;
    }
    
    @Override
    protected void finalize() {
    }
}
