package javax.swing.text;

public class BadLocationException extends Exception
{
    private int offs;
    
    public BadLocationException(final String s, final int offs) {
        super(s);
        this.offs = offs;
    }
    
    public int offsetRequested() {
        return this.offs;
    }
}
