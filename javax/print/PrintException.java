package javax.print;

public class PrintException extends Exception
{
    public PrintException() {
    }
    
    public PrintException(final String s) {
        super(s);
    }
    
    public PrintException(final Exception ex) {
        super(ex);
    }
    
    public PrintException(final String s, final Exception ex) {
        super(s, ex);
    }
}
