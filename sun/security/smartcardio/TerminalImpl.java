package sun.security.smartcardio;

import javax.smartcardio.CardNotPresentException;
import javax.smartcardio.CardException;
import java.security.Permission;
import javax.smartcardio.CardPermission;
import javax.smartcardio.Card;
import javax.smartcardio.CardTerminal;

final class TerminalImpl extends CardTerminal
{
    final long contextId;
    final String name;
    private CardImpl card;
    
    TerminalImpl(final long contextId, final String name) {
        this.contextId = contextId;
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public synchronized Card connect(final String s) throws CardException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new CardPermission(this.name, "connect"));
        }
        if (this.card != null) {
            if (this.card.isValid()) {
                final String protocol = this.card.getProtocol();
                if (s.equals("*") || s.equalsIgnoreCase(protocol)) {
                    return this.card;
                }
                throw new CardException("Cannot connect using " + s + ", connection already established using " + protocol);
            }
            else {
                this.card = null;
            }
        }
        try {
            return this.card = new CardImpl(this, s);
        }
        catch (final PCSCException ex) {
            if (ex.code == -2146434967 || ex.code == -2146435060) {
                throw new CardNotPresentException("No card present", ex);
            }
            throw new CardException("connect() failed", ex);
        }
    }
    
    @Override
    public boolean isCardPresent() throws CardException {
        try {
            return (PCSC.SCardGetStatusChange(this.contextId, 0L, new int[] { 0 }, new String[] { this.name })[0] & 0x20) != 0x0;
        }
        catch (final PCSCException ex) {
            throw new CardException("isCardPresent() failed", ex);
        }
    }
    
    private boolean waitForCard(final boolean b, long max) throws CardException {
        if (max < 0L) {
            throw new IllegalArgumentException("timeout must not be negative");
        }
        if (max == 0L) {
            max = -1L;
        }
        final int[] array = { 0 };
        final String[] array2 = { this.name };
        try {
            int[] array3 = PCSC.SCardGetStatusChange(this.contextId, 0L, array, array2);
            boolean b2 = (array3[0] & 0x20) != 0x0;
            if (b == b2) {
                return true;
            }
            final long n = System.currentTimeMillis() + max;
            while (b != b2 && max != 0L) {
                if (max != -1L) {
                    max = Math.max(n - System.currentTimeMillis(), 0L);
                }
                array3 = PCSC.SCardGetStatusChange(this.contextId, max, array3, array2);
                b2 = ((array3[0] & 0x20) != 0x0);
            }
            return b == b2;
        }
        catch (final PCSCException ex) {
            if (ex.code == -2146435062) {
                return false;
            }
            throw new CardException("waitForCard() failed", ex);
        }
    }
    
    @Override
    public boolean waitForCardPresent(final long n) throws CardException {
        return this.waitForCard(true, n);
    }
    
    @Override
    public boolean waitForCardAbsent(final long n) throws CardException {
        return this.waitForCard(false, n);
    }
    
    @Override
    public String toString() {
        return "PC/SC terminal " + this.name;
    }
}
