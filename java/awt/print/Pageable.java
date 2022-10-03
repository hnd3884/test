package java.awt.print;

public interface Pageable
{
    public static final int UNKNOWN_NUMBER_OF_PAGES = -1;
    
    int getNumberOfPages();
    
    PageFormat getPageFormat(final int p0) throws IndexOutOfBoundsException;
    
    Printable getPrintable(final int p0) throws IndexOutOfBoundsException;
}
