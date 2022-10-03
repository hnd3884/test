package sun.security.smartcardio;

import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;
import java.security.AccessController;
import javax.smartcardio.CardException;
import javax.smartcardio.CardChannel;
import java.security.Permission;
import javax.smartcardio.CardPermission;
import javax.smartcardio.ATR;
import javax.smartcardio.Card;

final class CardImpl extends Card
{
    private final TerminalImpl terminal;
    final long cardId;
    private final ATR atr;
    final int protocol;
    private final ChannelImpl basicChannel;
    private volatile State state;
    private volatile Thread exclusiveThread;
    private static final boolean isWindows;
    private static byte[] commandOpenChannel;
    private static final boolean invertReset;
    
    CardImpl(final TerminalImpl terminal, final String s) throws PCSCException {
        this.terminal = terminal;
        int n = 2;
        int n2;
        if (s.equals("*")) {
            n2 = 3;
        }
        else if (s.equalsIgnoreCase("T=0")) {
            n2 = 1;
        }
        else if (s.equalsIgnoreCase("T=1")) {
            n2 = 2;
        }
        else {
            if (!s.equalsIgnoreCase("direct")) {
                throw new IllegalArgumentException("Unsupported protocol " + s);
            }
            n2 = (CardImpl.isWindows ? 0 : 65536);
            n = 3;
        }
        this.cardId = PCSC.SCardConnect(terminal.contextId, terminal.name, n, n2);
        final byte[] array = new byte[2];
        this.atr = new ATR(PCSC.SCardStatus(this.cardId, array));
        this.protocol = (array[1] & 0xFF);
        this.basicChannel = new ChannelImpl(this, 0);
        this.state = State.OK;
    }
    
    void checkState() {
        final State state = this.state;
        if (state == State.DISCONNECTED) {
            throw new IllegalStateException("Card has been disconnected");
        }
        if (state == State.REMOVED) {
            throw new IllegalStateException("Card has been removed");
        }
    }
    
    boolean isValid() {
        if (this.state != State.OK) {
            return false;
        }
        try {
            PCSC.SCardStatus(this.cardId, new byte[2]);
            return true;
        }
        catch (final PCSCException ex) {
            this.state = State.REMOVED;
            return false;
        }
    }
    
    private void checkSecurity(final String s) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new CardPermission(this.terminal.name, s));
        }
    }
    
    void handleError(final PCSCException ex) {
        if (ex.code == -2146434967) {
            this.state = State.REMOVED;
        }
    }
    
    @Override
    public ATR getATR() {
        return this.atr;
    }
    
    @Override
    public String getProtocol() {
        switch (this.protocol) {
            case 1: {
                return "T=0";
            }
            case 2: {
                return "T=1";
            }
            default: {
                return "Unknown protocol " + this.protocol;
            }
        }
    }
    
    @Override
    public CardChannel getBasicChannel() {
        this.checkSecurity("getBasicChannel");
        this.checkState();
        return this.basicChannel;
    }
    
    private static int getSW(final byte[] array) {
        if (array.length < 2) {
            return -1;
        }
        return (array[array.length - 2] & 0xFF) << 8 | (array[array.length - 1] & 0xFF);
    }
    
    @Override
    public CardChannel openLogicalChannel() throws CardException {
        this.checkSecurity("openLogicalChannel");
        this.checkState();
        this.checkExclusive();
        try {
            final byte[] sCardTransmit = PCSC.SCardTransmit(this.cardId, this.protocol, CardImpl.commandOpenChannel, 0, CardImpl.commandOpenChannel.length);
            if (sCardTransmit.length != 3 || getSW(sCardTransmit) != 36864) {
                throw new CardException("openLogicalChannel() failed, card response: " + PCSC.toString(sCardTransmit));
            }
            return new ChannelImpl(this, sCardTransmit[0]);
        }
        catch (final PCSCException ex) {
            this.handleError(ex);
            throw new CardException("openLogicalChannel() failed", ex);
        }
    }
    
    void checkExclusive() throws CardException {
        final Thread exclusiveThread = this.exclusiveThread;
        if (exclusiveThread == null) {
            return;
        }
        if (exclusiveThread != Thread.currentThread()) {
            throw new CardException("Exclusive access established by another Thread");
        }
    }
    
    @Override
    public synchronized void beginExclusive() throws CardException {
        this.checkSecurity("exclusive");
        this.checkState();
        if (this.exclusiveThread != null) {
            throw new CardException("Exclusive access has already been assigned to Thread " + this.exclusiveThread.getName());
        }
        try {
            PCSC.SCardBeginTransaction(this.cardId);
        }
        catch (final PCSCException ex) {
            this.handleError(ex);
            throw new CardException("beginExclusive() failed", ex);
        }
        this.exclusiveThread = Thread.currentThread();
    }
    
    @Override
    public synchronized void endExclusive() throws CardException {
        this.checkState();
        if (this.exclusiveThread != Thread.currentThread()) {
            throw new IllegalStateException("Exclusive access not assigned to current Thread");
        }
        try {
            PCSC.SCardEndTransaction(this.cardId, 0);
        }
        catch (final PCSCException ex) {
            this.handleError(ex);
            throw new CardException("endExclusive() failed", ex);
        }
        finally {
            this.exclusiveThread = null;
        }
    }
    
    @Override
    public byte[] transmitControlCommand(final int n, final byte[] array) throws CardException {
        this.checkSecurity("transmitControl");
        this.checkState();
        this.checkExclusive();
        if (array == null) {
            throw new NullPointerException();
        }
        try {
            return PCSC.SCardControl(this.cardId, n, array);
        }
        catch (final PCSCException ex) {
            this.handleError(ex);
            throw new CardException("transmitControlCommand() failed", ex);
        }
    }
    
    @Override
    public void disconnect(boolean b) throws CardException {
        if (b) {
            this.checkSecurity("reset");
        }
        if (this.state != State.OK) {
            return;
        }
        this.checkExclusive();
        if (CardImpl.invertReset) {
            b = !b;
        }
        try {
            PCSC.SCardDisconnect(this.cardId, b ? 1 : 0);
        }
        catch (final PCSCException ex) {
            throw new CardException("disconnect() failed", ex);
        }
        finally {
            this.state = State.DISCONNECTED;
            this.exclusiveThread = null;
        }
    }
    
    @Override
    public String toString() {
        return "PC/SC card in " + this.terminal.name + ", protocol " + this.getProtocol() + ", state " + this.state;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (this.state == State.OK) {
                this.state = State.DISCONNECTED;
                PCSC.SCardDisconnect(this.cardId, 0);
            }
        }
        finally {
            super.finalize();
        }
    }
    
    static {
        isWindows = AccessController.doPrivileged(() -> System.getProperty("os.name")).startsWith("Windows");
        CardImpl.commandOpenChannel = new byte[] { 0, 112, 0, 0, 1 };
        invertReset = Boolean.parseBoolean(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.security.smartcardio.invertCardReset", "false")));
    }
    
    private enum State
    {
        OK, 
        REMOVED, 
        DISCONNECTED;
    }
}
