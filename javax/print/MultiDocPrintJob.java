package javax.print;

import javax.print.attribute.PrintRequestAttributeSet;

public interface MultiDocPrintJob extends DocPrintJob
{
    void print(final MultiDoc p0, final PrintRequestAttributeSet p1) throws PrintException;
}
