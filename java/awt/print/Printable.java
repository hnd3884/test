package java.awt.print;

import java.awt.Graphics;

public interface Printable
{
    public static final int PAGE_EXISTS = 0;
    public static final int NO_SUCH_PAGE = 1;
    
    int print(final Graphics p0, final PageFormat p1, final int p2) throws PrinterException;
}
