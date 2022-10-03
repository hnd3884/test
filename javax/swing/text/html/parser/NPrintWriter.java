package javax.swing.text.html.parser;

import java.io.OutputStream;
import java.io.PrintWriter;

class NPrintWriter extends PrintWriter
{
    private int numLines;
    private int numPrinted;
    
    public NPrintWriter(final int numLines) {
        super(System.out);
        this.numLines = 5;
        this.numPrinted = 0;
        this.numLines = numLines;
    }
    
    @Override
    public void println(final char[] array) {
        if (this.numPrinted >= this.numLines) {
            return;
        }
        final char[] array2 = null;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == '\n') {
                ++this.numPrinted;
            }
            if (this.numPrinted == this.numLines) {
                System.arraycopy(array, 0, array2, 0, i);
            }
        }
        if (array2 != null) {
            super.print(array2);
        }
        if (this.numPrinted == this.numLines) {
            return;
        }
        super.println(array);
        ++this.numPrinted;
    }
}
