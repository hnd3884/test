package javax.smartcardio;

import java.util.Iterator;
import java.util.List;

public abstract class CardTerminals
{
    protected CardTerminals() {
    }
    
    public List<CardTerminal> list() throws CardException {
        return this.list(State.ALL);
    }
    
    public abstract List<CardTerminal> list(final State p0) throws CardException;
    
    public CardTerminal getTerminal(final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        try {
            for (final CardTerminal cardTerminal : this.list()) {
                if (cardTerminal.getName().equals(s)) {
                    return cardTerminal;
                }
            }
            return null;
        }
        catch (final CardException ex) {
            return null;
        }
    }
    
    public void waitForChange() throws CardException {
        this.waitForChange(0L);
    }
    
    public abstract boolean waitForChange(final long p0) throws CardException;
    
    public enum State
    {
        ALL, 
        CARD_PRESENT, 
        CARD_ABSENT, 
        CARD_INSERTION, 
        CARD_REMOVAL;
    }
}
