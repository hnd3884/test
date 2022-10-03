package sun.util.locale;

public class LocaleSyntaxException extends Exception
{
    private static final long serialVersionUID = 1L;
    private int index;
    
    public LocaleSyntaxException(final String s) {
        this(s, 0);
    }
    
    public LocaleSyntaxException(final String s, final int index) {
        super(s);
        this.index = -1;
        this.index = index;
    }
    
    public int getErrorIndex() {
        return this.index;
    }
}
