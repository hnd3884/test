package javax.smartcardio;

public class CardNotPresentException extends CardException
{
    private static final long serialVersionUID = 1346879911706545215L;
    
    public CardNotPresentException(final String s) {
        super(s);
    }
    
    public CardNotPresentException(final Throwable t) {
        super(t);
    }
    
    public CardNotPresentException(final String s, final Throwable t) {
        super(s, t);
    }
}
