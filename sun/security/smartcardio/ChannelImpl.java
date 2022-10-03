package sun.security.smartcardio;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.nio.ReadOnlyBufferException;
import java.nio.ByteBuffer;
import javax.smartcardio.CardException;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;

final class ChannelImpl extends CardChannel
{
    private final CardImpl card;
    private final int channel;
    private volatile boolean isClosed;
    private static final boolean t0GetResponse;
    private static final boolean t1GetResponse;
    private static final boolean t1StripLe;
    private static final int RESPONSE_ITERATIONS = 256;
    private static final byte[] B0;
    
    ChannelImpl(final CardImpl card, final int channel) {
        this.card = card;
        this.channel = channel;
    }
    
    void checkClosed() {
        this.card.checkState();
        if (this.isClosed) {
            throw new IllegalStateException("Logical channel has been closed");
        }
    }
    
    @Override
    public Card getCard() {
        return this.card;
    }
    
    @Override
    public int getChannelNumber() {
        this.checkClosed();
        return this.channel;
    }
    
    private static void checkManageChannel(final byte[] array) {
        if (array.length < 4) {
            throw new IllegalArgumentException("Command APDU must be at least 4 bytes long");
        }
        if (array[0] >= 0 && array[1] == 112) {
            throw new IllegalArgumentException("Manage channel command not allowed, use openLogicalChannel()");
        }
    }
    
    @Override
    public ResponseAPDU transmit(final CommandAPDU commandAPDU) throws CardException {
        this.checkClosed();
        this.card.checkExclusive();
        return new ResponseAPDU(this.doTransmit(commandAPDU.getBytes()));
    }
    
    @Override
    public int transmit(final ByteBuffer byteBuffer, final ByteBuffer byteBuffer2) throws CardException {
        this.checkClosed();
        this.card.checkExclusive();
        if (byteBuffer == null || byteBuffer2 == null) {
            throw new NullPointerException();
        }
        if (byteBuffer2.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        if (byteBuffer == byteBuffer2) {
            throw new IllegalArgumentException("command and response must not be the same object");
        }
        if (byteBuffer2.remaining() < 258) {
            throw new IllegalArgumentException("Insufficient space in response buffer");
        }
        final byte[] array = new byte[byteBuffer.remaining()];
        byteBuffer.get(array);
        final byte[] doTransmit = this.doTransmit(array);
        byteBuffer2.put(doTransmit);
        return doTransmit.length;
    }
    
    private static boolean getBooleanProperty(final String s, final boolean b) {
        final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction(s));
        if (s2 == null) {
            return b;
        }
        if (s2.equalsIgnoreCase("true")) {
            return true;
        }
        if (s2.equalsIgnoreCase("false")) {
            return false;
        }
        throw new IllegalArgumentException(s + " must be either 'true' or 'false'");
    }
    
    private byte[] concat(final byte[] array, final byte[] array2, final int n) {
        final int length = array.length;
        if (length == 0 && n == array2.length) {
            return array2;
        }
        final byte[] array3 = new byte[length + n];
        System.arraycopy(array, 0, array3, 0, length);
        System.arraycopy(array2, 0, array3, length, n);
        return array3;
    }
    
    private byte[] doTransmit(final byte[] channel) throws CardException {
        try {
            checkManageChannel(channel);
            this.setChannel(channel);
            int length = channel.length;
            final boolean b = this.card.protocol == 1;
            final boolean b2 = this.card.protocol == 2;
            if (b && length >= 7 && channel[4] == 0) {
                throw new CardException("Extended length forms not supported for T=0");
            }
            if ((b || (b2 && ChannelImpl.t1StripLe)) && length >= 7) {
                final int n = channel[4] & 0xFF;
                if (n != 0) {
                    if (length == n + 6) {
                        --length;
                    }
                }
                else if (length == ((channel[5] & 0xFF) << 8 | (channel[6] & 0xFF)) + 9) {
                    length -= 2;
                }
            }
            final boolean b3 = (b && ChannelImpl.t0GetResponse) || (b2 && ChannelImpl.t1GetResponse);
            int n2 = 0;
            byte[] array = ChannelImpl.B0;
            while (++n2 <= 256) {
                final byte[] sCardTransmit = PCSC.SCardTransmit(this.card.cardId, this.card.protocol, channel, 0, length);
                final int length2 = sCardTransmit.length;
                if (b3 && length2 >= 2) {
                    if (length2 == 2 && sCardTransmit[0] == 108) {
                        channel[length - 1] = sCardTransmit[1];
                        continue;
                    }
                    if (sCardTransmit[length2 - 2] == 97) {
                        if (length2 > 2) {
                            array = this.concat(array, sCardTransmit, length2 - 2);
                        }
                        channel[1] = -64;
                        channel[3] = (channel[2] = 0);
                        channel[4] = sCardTransmit[length2 - 1];
                        length = 5;
                        continue;
                    }
                }
                return this.concat(array, sCardTransmit, length2);
            }
            throw new CardException("Number of response iterations exceeded maximum 256");
        }
        catch (final PCSCException ex) {
            this.card.handleError(ex);
            throw new CardException(ex);
        }
    }
    
    private static int getSW(final byte[] array) throws CardException {
        if (array.length < 2) {
            throw new CardException("Invalid response length: " + array.length);
        }
        return (array[array.length - 2] & 0xFF) << 8 | (array[array.length - 1] & 0xFF);
    }
    
    private static boolean isOK(final byte[] array) throws CardException {
        return array.length == 2 && getSW(array) == 36864;
    }
    
    private void setChannel(final byte[] array) {
        final byte b = array[0];
        if (b < 0) {
            return;
        }
        if ((b & 0xE0) == 0x20) {
            return;
        }
        if (this.channel <= 3) {
            final int n = 0;
            array[n] &= (byte)188;
            final int n2 = 0;
            array[n2] |= (byte)this.channel;
        }
        else {
            if (this.channel > 19) {
                throw new RuntimeException("Unsupported channel number: " + this.channel);
            }
            final int n3 = 0;
            array[n3] &= (byte)176;
            final int n4 = 0;
            array[n4] |= 0x40;
            final int n5 = 0;
            array[n5] |= (byte)(this.channel - 4);
        }
    }
    
    @Override
    public void close() throws CardException {
        if (this.getChannelNumber() == 0) {
            throw new IllegalStateException("Cannot close basic logical channel");
        }
        if (this.isClosed) {
            return;
        }
        this.card.checkExclusive();
        try {
            final byte[] channel = { 0, 112, -128, 0 };
            channel[3] = (byte)this.getChannelNumber();
            this.setChannel(channel);
            final byte[] sCardTransmit = PCSC.SCardTransmit(this.card.cardId, this.card.protocol, channel, 0, channel.length);
            if (!isOK(sCardTransmit)) {
                throw new CardException("close() failed: " + PCSC.toString(sCardTransmit));
            }
        }
        catch (final PCSCException ex) {
            this.card.handleError(ex);
            throw new CardException("Could not close channel", ex);
        }
        finally {
            this.isClosed = true;
        }
    }
    
    @Override
    public String toString() {
        return "PC/SC channel " + this.channel;
    }
    
    static {
        t0GetResponse = getBooleanProperty("sun.security.smartcardio.t0GetResponse", true);
        t1GetResponse = getBooleanProperty("sun.security.smartcardio.t1GetResponse", true);
        t1StripLe = getBooleanProperty("sun.security.smartcardio.t1StripLe", false);
        B0 = new byte[0];
    }
}
