package javax.smartcardio;

public class CardException extends Exception
{
    private static final long serialVersionUID = 7787607144922050628L;
    
    public CardException(final String s) {
        super(s);
    }
    
    public CardException(final Throwable t) {
        super(t);
    }
    
    public CardException(final String s, final Throwable t) {
        super(s, t);
    }
}
