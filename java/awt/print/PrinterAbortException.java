package java.awt.print;

public class PrinterAbortException extends PrinterException
{
    public PrinterAbortException() {
    }
    
    public PrinterAbortException(final String s) {
        super(s);
    }
}
