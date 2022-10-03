package javax.print;

import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobListener;
import javax.print.attribute.PrintJobAttributeSet;

public interface DocPrintJob
{
    PrintService getPrintService();
    
    PrintJobAttributeSet getAttributes();
    
    void addPrintJobListener(final PrintJobListener p0);
    
    void removePrintJobListener(final PrintJobListener p0);
    
    void addPrintJobAttributeListener(final PrintJobAttributeListener p0, final PrintJobAttributeSet p1);
    
    void removePrintJobAttributeListener(final PrintJobAttributeListener p0);
    
    void print(final Doc p0, final PrintRequestAttributeSet p1) throws PrintException;
}
