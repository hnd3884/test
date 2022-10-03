package javax.smartcardio;

import java.nio.ByteBuffer;

public abstract class CardChannel
{
    protected CardChannel() {
    }
    
    public abstract Card getCard();
    
    public abstract int getChannelNumber();
    
    public abstract ResponseAPDU transmit(final CommandAPDU p0) throws CardException;
    
    public abstract int transmit(final ByteBuffer p0, final ByteBuffer p1) throws CardException;
    
    public abstract void close() throws CardException;
}
