package sun.nio.ch;

import java.nio.channels.Selector;
import java.util.Iterator;
import java.net.SocketException;
import java.nio.channels.IllegalSelectorException;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.util.Collections;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashSet;
import java.nio.channels.SelectionKey;
import java.util.Set;
import java.nio.channels.spi.AbstractSelector;

public abstract class SelectorImpl extends AbstractSelector
{
    protected Set<SelectionKey> selectedKeys;
    protected HashSet<SelectionKey> keys;
    private Set<SelectionKey> publicKeys;
    private Set<SelectionKey> publicSelectedKeys;
    
    protected SelectorImpl(final SelectorProvider selectorProvider) {
        super(selectorProvider);
        this.keys = new HashSet<SelectionKey>();
        this.selectedKeys = new HashSet<SelectionKey>();
        if (Util.atBugLevel("1.4")) {
            this.publicKeys = this.keys;
            this.publicSelectedKeys = this.selectedKeys;
        }
        else {
            this.publicKeys = Collections.unmodifiableSet((Set<? extends SelectionKey>)this.keys);
            this.publicSelectedKeys = Util.ungrowableSet(this.selectedKeys);
        }
    }
    
    @Override
    public Set<SelectionKey> keys() {
        if (!this.isOpen() && !Util.atBugLevel("1.4")) {
            throw new ClosedSelectorException();
        }
        return this.publicKeys;
    }
    
    @Override
    public Set<SelectionKey> selectedKeys() {
        if (!this.isOpen() && !Util.atBugLevel("1.4")) {
            throw new ClosedSelectorException();
        }
        return this.publicSelectedKeys;
    }
    
    protected abstract int doSelect(final long p0) throws IOException;
    
    private int lockAndDoSelect(final long n) throws IOException {
        synchronized (this) {
            if (!this.isOpen()) {
                throw new ClosedSelectorException();
            }
            synchronized (this.publicKeys) {
                synchronized (this.publicSelectedKeys) {
                    return this.doSelect(n);
                }
            }
        }
    }
    
    @Override
    public int select(final long n) throws IOException {
        if (n < 0L) {
            throw new IllegalArgumentException("Negative timeout");
        }
        return this.lockAndDoSelect((n == 0L) ? -1L : n);
    }
    
    @Override
    public int select() throws IOException {
        return this.select(0L);
    }
    
    @Override
    public int selectNow() throws IOException {
        return this.lockAndDoSelect(0L);
    }
    
    public void implCloseSelector() throws IOException {
        this.wakeup();
        synchronized (this) {
            synchronized (this.publicKeys) {
                synchronized (this.publicSelectedKeys) {
                    this.implClose();
                }
            }
        }
    }
    
    protected abstract void implClose() throws IOException;
    
    public void putEventOps(final SelectionKeyImpl selectionKeyImpl, final int n) {
    }
    
    @Override
    protected final SelectionKey register(final AbstractSelectableChannel abstractSelectableChannel, final int n, final Object o) {
        if (!(abstractSelectableChannel instanceof SelChImpl)) {
            throw new IllegalSelectorException();
        }
        final SelectionKeyImpl selectionKeyImpl = new SelectionKeyImpl((SelChImpl)abstractSelectableChannel, this);
        selectionKeyImpl.attach(o);
        synchronized (this.publicKeys) {
            this.implRegister(selectionKeyImpl);
        }
        selectionKeyImpl.interestOps(n);
        return selectionKeyImpl;
    }
    
    protected abstract void implRegister(final SelectionKeyImpl p0);
    
    void processDeregisterQueue() throws IOException {
        final Set<SelectionKey> cancelledKeys = this.cancelledKeys();
        synchronized (cancelledKeys) {
            if (!cancelledKeys.isEmpty()) {
                final Iterator iterator = cancelledKeys.iterator();
                while (iterator.hasNext()) {
                    final SelectionKeyImpl selectionKeyImpl = (SelectionKeyImpl)iterator.next();
                    try {
                        this.implDereg(selectionKeyImpl);
                    }
                    catch (final SocketException ex) {
                        throw new IOException("Error deregistering key", ex);
                    }
                    finally {
                        iterator.remove();
                    }
                }
            }
        }
    }
    
    protected abstract void implDereg(final SelectionKeyImpl p0) throws IOException;
    
    @Override
    public abstract Selector wakeup();
}
