package java.awt.print;

import java.util.Vector;

public class Book implements Pageable
{
    private Vector mPages;
    
    public Book() {
        this.mPages = new Vector();
    }
    
    @Override
    public int getNumberOfPages() {
        return this.mPages.size();
    }
    
    @Override
    public PageFormat getPageFormat(final int n) throws IndexOutOfBoundsException {
        return this.getPage(n).getPageFormat();
    }
    
    @Override
    public Printable getPrintable(final int n) throws IndexOutOfBoundsException {
        return this.getPage(n).getPrintable();
    }
    
    public void setPage(final int n, final Printable printable, final PageFormat pageFormat) throws IndexOutOfBoundsException {
        if (printable == null) {
            throw new NullPointerException("painter is null");
        }
        if (pageFormat == null) {
            throw new NullPointerException("page is null");
        }
        this.mPages.setElementAt(new BookPage(printable, pageFormat), n);
    }
    
    public void append(final Printable printable, final PageFormat pageFormat) {
        this.mPages.addElement(new BookPage(printable, pageFormat));
    }
    
    public void append(final Printable printable, final PageFormat pageFormat, final int n) {
        final BookPage bookPage = new BookPage(printable, pageFormat);
        final int size = this.mPages.size();
        final int size2 = size + n;
        this.mPages.setSize(size2);
        for (int i = size; i < size2; ++i) {
            this.mPages.setElementAt(bookPage, i);
        }
    }
    
    private BookPage getPage(final int n) throws ArrayIndexOutOfBoundsException {
        return this.mPages.elementAt(n);
    }
    
    private class BookPage
    {
        private PageFormat mFormat;
        private Printable mPainter;
        
        BookPage(final Printable mPainter, final PageFormat mFormat) {
            if (mPainter == null || mFormat == null) {
                throw new NullPointerException();
            }
            this.mFormat = mFormat;
            this.mPainter = mPainter;
        }
        
        Printable getPrintable() {
            return this.mPainter;
        }
        
        PageFormat getPageFormat() {
            return this.mFormat;
        }
    }
}
