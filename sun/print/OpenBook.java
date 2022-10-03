package sun.print;

import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;

class OpenBook implements Pageable
{
    private PageFormat mFormat;
    private Printable mPainter;
    
    OpenBook(final PageFormat mFormat, final Printable mPainter) {
        this.mFormat = mFormat;
        this.mPainter = mPainter;
    }
    
    @Override
    public int getNumberOfPages() {
        return -1;
    }
    
    @Override
    public PageFormat getPageFormat(final int n) {
        return this.mFormat;
    }
    
    @Override
    public Printable getPrintable(final int n) throws IndexOutOfBoundsException {
        return this.mPainter;
    }
}
