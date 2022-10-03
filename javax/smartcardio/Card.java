package javax.smartcardio;

public abstract class Card
{
    protected Card() {
    }
    
    public abstract ATR getATR();
    
    public abstract String getProtocol();
    
    public abstract CardChannel getBasicChannel();
    
    public abstract CardChannel openLogicalChannel() throws CardException;
    
    public abstract void beginExclusive() throws CardException;
    
    public abstract void endExclusive() throws CardException;
    
    public abstract byte[] transmitControlCommand(final int p0, final byte[] p1) throws CardException;
    
    public abstract void disconnect(final boolean p0) throws CardException;
}
