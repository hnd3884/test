package javax.smartcardio;

public abstract class CardTerminal
{
    protected CardTerminal() {
    }
    
    public abstract String getName();
    
    public abstract Card connect(final String p0) throws CardException;
    
    public abstract boolean isCardPresent() throws CardException;
    
    public abstract boolean waitForCardPresent(final long p0) throws CardException;
    
    public abstract boolean waitForCardAbsent(final long p0) throws CardException;
}
