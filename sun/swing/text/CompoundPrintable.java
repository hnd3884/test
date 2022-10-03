package sun.swing.text;

import java.awt.print.PrinterException;
import java.awt.print.PageFormat;
import java.awt.Graphics;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class CompoundPrintable implements CountingPrintable
{
    private final Queue<CountingPrintable> printables;
    private int offset;
    
    public CompoundPrintable(final List<CountingPrintable> list) {
        this.offset = 0;
        this.printables = new LinkedList<CountingPrintable>(list);
    }
    
    @Override
    public int print(final Graphics graphics, final PageFormat pageFormat, final int n) throws PrinterException {
        int print = 1;
        while (this.printables.peek() != null) {
            print = this.printables.peek().print(graphics, pageFormat, n - this.offset);
            if (print == 0) {
                break;
            }
            this.offset += this.printables.poll().getNumberOfPages();
        }
        return print;
    }
    
    @Override
    public int getNumberOfPages() {
        return this.offset;
    }
}
