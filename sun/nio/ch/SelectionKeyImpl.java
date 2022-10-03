package sun.nio.ch;

import java.nio.channels.SelectionKey;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.Selector;
import java.nio.channels.SelectableChannel;
import java.nio.channels.spi.AbstractSelectionKey;

public class SelectionKeyImpl extends AbstractSelectionKey
{
    final SelChImpl channel;
    public final SelectorImpl selector;
    private int index;
    private volatile int interestOps;
    private int readyOps;
    
    SelectionKeyImpl(final SelChImpl channel, final SelectorImpl selector) {
        this.channel = channel;
        this.selector = selector;
    }
    
    @Override
    public SelectableChannel channel() {
        return (SelectableChannel)this.channel;
    }
    
    @Override
    public Selector selector() {
        return this.selector;
    }
    
    int getIndex() {
        return this.index;
    }
    
    void setIndex(final int index) {
        this.index = index;
    }
    
    private void ensureValid() {
        if (!this.isValid()) {
            throw new CancelledKeyException();
        }
    }
    
    @Override
    public int interestOps() {
        this.ensureValid();
        return this.interestOps;
    }
    
    @Override
    public SelectionKey interestOps(final int n) {
        this.ensureValid();
        return this.nioInterestOps(n);
    }
    
    @Override
    public int readyOps() {
        this.ensureValid();
        return this.readyOps;
    }
    
    public void nioReadyOps(final int readyOps) {
        this.readyOps = readyOps;
    }
    
    public int nioReadyOps() {
        return this.readyOps;
    }
    
    public SelectionKey nioInterestOps(final int interestOps) {
        if ((interestOps & ~this.channel().validOps()) != 0x0) {
            throw new IllegalArgumentException();
        }
        this.channel.translateAndSetInterestOps(interestOps, this);
        this.interestOps = interestOps;
        return this;
    }
    
    public int nioInterestOps() {
        return this.interestOps;
    }
}
