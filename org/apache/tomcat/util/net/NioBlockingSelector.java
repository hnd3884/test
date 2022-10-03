package org.apache.tomcat.util.net;

import java.nio.channels.ClosedChannelException;
import java.util.concurrent.CountDownLatch;
import java.util.Iterator;
import org.apache.tomcat.util.ExceptionUtils;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SocketChannel;
import org.apache.tomcat.util.collections.SynchronizedQueue;
import org.apache.juli.logging.LogFactory;
import java.nio.channels.SelectionKey;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import org.apache.tomcat.util.collections.SynchronizedStack;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.juli.logging.Log;

public class NioBlockingSelector
{
    private static final Log log;
    private static AtomicInteger threadCounter;
    private final SynchronizedStack<KeyReference> keyReferenceStack;
    protected Selector sharedSelector;
    protected BlockPoller poller;
    
    public NioBlockingSelector() {
        this.keyReferenceStack = (SynchronizedStack<KeyReference>)new SynchronizedStack();
    }
    
    public void open(final Selector selector) {
        this.sharedSelector = selector;
        this.poller = new BlockPoller();
        this.poller.selector = this.sharedSelector;
        this.poller.setDaemon(true);
        this.poller.setName("NioBlockingSelector.BlockPoller-" + NioBlockingSelector.threadCounter.getAndIncrement());
        this.poller.start();
    }
    
    public void close() {
        if (this.poller != null) {
            this.poller.disable();
            this.poller.interrupt();
            this.poller = null;
        }
    }
    
    public int write(final ByteBuffer buf, final NioChannel socket, final long writeTimeout) throws IOException {
        final SelectionKey key = socket.getIOChannel().keyFor(socket.getPoller().getSelector());
        if (key == null) {
            throw new IOException("Key no longer registered");
        }
        KeyReference reference = (KeyReference)this.keyReferenceStack.pop();
        if (reference == null) {
            reference = new KeyReference();
        }
        final NioEndpoint.NioSocketWrapper att = (NioEndpoint.NioSocketWrapper)key.attachment();
        if (att.previousIOException != null) {
            throw new IOException(att.previousIOException);
        }
        int written = 0;
        boolean timedout = false;
        int keycount = 1;
        long time = System.currentTimeMillis();
        try {
            while (!timedout && buf.hasRemaining()) {
                if (keycount > 0) {
                    final int cnt = socket.write(buf);
                    if (cnt == -1) {
                        throw new EOFException();
                    }
                    written += cnt;
                    if (cnt > 0) {
                        time = System.currentTimeMillis();
                        continue;
                    }
                }
                try {
                    if (att.getWriteLatch() == null || att.getWriteLatch().getCount() == 0L) {
                        att.startWriteLatch(1);
                    }
                    this.poller.add(att, 4, reference);
                    if (writeTimeout < 0L) {
                        att.awaitWriteLatch(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                    }
                    else {
                        att.awaitWriteLatch(writeTimeout, TimeUnit.MILLISECONDS);
                    }
                }
                catch (final InterruptedException ex) {}
                if (att.getWriteLatch() != null && att.getWriteLatch().getCount() > 0L) {
                    keycount = 0;
                }
                else {
                    keycount = 1;
                    att.resetWriteLatch();
                }
                if (writeTimeout > 0L && keycount == 0) {
                    timedout = (System.currentTimeMillis() - time >= writeTimeout);
                }
            }
            if (timedout) {
                throw att.previousIOException = new SocketTimeoutException();
            }
        }
        finally {
            this.poller.remove(att, 4);
            if (timedout && reference.key != null) {
                this.poller.cancelKey(reference.key);
            }
            reference.key = null;
            this.keyReferenceStack.push((Object)reference);
        }
        return written;
    }
    
    public int read(final ByteBuffer buf, final NioChannel socket, final long readTimeout) throws IOException {
        final SelectionKey key = socket.getIOChannel().keyFor(socket.getPoller().getSelector());
        if (key == null) {
            throw new IOException("Key no longer registered");
        }
        KeyReference reference = (KeyReference)this.keyReferenceStack.pop();
        if (reference == null) {
            reference = new KeyReference();
        }
        final NioEndpoint.NioSocketWrapper att = (NioEndpoint.NioSocketWrapper)key.attachment();
        int read = 0;
        boolean timedout = false;
        int keycount = 1;
        final long time = System.currentTimeMillis();
        try {
            while (!timedout) {
                if (keycount > 0) {
                    read = socket.read(buf);
                    if (read != 0) {
                        break;
                    }
                }
                try {
                    if (att.getReadLatch() == null || att.getReadLatch().getCount() == 0L) {
                        att.startReadLatch(1);
                    }
                    this.poller.add(att, 1, reference);
                    if (readTimeout < 0L) {
                        att.awaitReadLatch(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                    }
                    else {
                        att.awaitReadLatch(readTimeout, TimeUnit.MILLISECONDS);
                    }
                }
                catch (final InterruptedException ex) {}
                if (att.getReadLatch() != null && att.getReadLatch().getCount() > 0L) {
                    keycount = 0;
                }
                else {
                    keycount = 1;
                    att.resetReadLatch();
                }
                if (readTimeout >= 0L && keycount == 0) {
                    timedout = (System.currentTimeMillis() - time >= readTimeout);
                }
            }
            if (timedout) {
                throw new SocketTimeoutException();
            }
        }
        finally {
            this.poller.remove(att, 1);
            if (timedout && reference.key != null) {
                this.poller.cancelKey(reference.key);
            }
            reference.key = null;
            this.keyReferenceStack.push((Object)reference);
        }
        return read;
    }
    
    static {
        log = LogFactory.getLog((Class)NioBlockingSelector.class);
        NioBlockingSelector.threadCounter = new AtomicInteger(0);
    }
    
    protected static class BlockPoller extends Thread
    {
        protected volatile boolean run;
        protected Selector selector;
        protected final SynchronizedQueue<Runnable> events;
        protected final AtomicInteger wakeupCounter;
        
        protected BlockPoller() {
            this.run = true;
            this.selector = null;
            this.events = (SynchronizedQueue<Runnable>)new SynchronizedQueue();
            this.wakeupCounter = new AtomicInteger(0);
        }
        
        public void disable() {
            this.run = false;
            this.selector.wakeup();
        }
        
        public void cancelKey(final SelectionKey key) {
            final Runnable r = new RunnableCancel(key);
            this.events.offer((Object)r);
            this.wakeup();
        }
        
        public void wakeup() {
            if (this.wakeupCounter.addAndGet(1) == 0) {
                this.selector.wakeup();
            }
        }
        
        public void cancel(final SelectionKey sk, final NioEndpoint.NioSocketWrapper key, final int ops) {
            if (sk != null) {
                sk.cancel();
                sk.attach(null);
                if (0x4 == (ops & 0x4)) {
                    this.countDown(key.getWriteLatch());
                }
                if (0x1 == (ops & 0x1)) {
                    this.countDown(key.getReadLatch());
                }
            }
        }
        
        public void add(final NioEndpoint.NioSocketWrapper key, final int ops, final KeyReference ref) {
            if (key == null) {
                return;
            }
            final NioChannel nch = key.getSocket();
            final SocketChannel ch = nch.getIOChannel();
            if (ch == null) {
                return;
            }
            final Runnable r = new RunnableAdd(ch, key, ops, ref);
            this.events.offer((Object)r);
            this.wakeup();
        }
        
        public void remove(final NioEndpoint.NioSocketWrapper key, final int ops) {
            if (key == null) {
                return;
            }
            final NioChannel nch = key.getSocket();
            final SocketChannel ch = nch.getIOChannel();
            if (ch == null) {
                return;
            }
            final Runnable r = new RunnableRemove(ch, key, ops);
            this.events.offer((Object)r);
            this.wakeup();
        }
        
        public boolean events() {
            Runnable r = null;
            final int size = this.events.size();
            for (int i = 0; i < size && (r = (Runnable)this.events.poll()) != null; ++i) {
                r.run();
            }
            return size > 0;
        }
        
        @Override
        public void run() {
            while (this.run) {
                try {
                    this.events();
                    int keyCount = 0;
                    try {
                        if (this.wakeupCounter.getAndSet(-1) > 0) {
                            keyCount = this.selector.selectNow();
                        }
                        else {
                            keyCount = this.selector.select(1000L);
                        }
                        this.wakeupCounter.set(0);
                        if (!this.run) {
                            break;
                        }
                    }
                    catch (final NullPointerException x) {
                        if (this.selector == null) {
                            throw x;
                        }
                        if (!NioBlockingSelector.log.isDebugEnabled()) {
                            continue;
                        }
                        NioBlockingSelector.log.debug((Object)"Possibly encountered sun bug 5076772 on windows JDK 1.5", (Throwable)x);
                        continue;
                    }
                    catch (final CancelledKeyException x2) {
                        if (!NioBlockingSelector.log.isDebugEnabled()) {
                            continue;
                        }
                        NioBlockingSelector.log.debug((Object)"Possibly encountered sun bug 5076772 on windows JDK 1.5", (Throwable)x2);
                        continue;
                    }
                    catch (final Throwable x3) {
                        ExceptionUtils.handleThrowable(x3);
                        NioBlockingSelector.log.error((Object)"", x3);
                        continue;
                    }
                    final Iterator<SelectionKey> iterator = (keyCount > 0) ? this.selector.selectedKeys().iterator() : null;
                    while (this.run && iterator != null && iterator.hasNext()) {
                        final SelectionKey sk = iterator.next();
                        final NioEndpoint.NioSocketWrapper attachment = (NioEndpoint.NioSocketWrapper)sk.attachment();
                        try {
                            iterator.remove();
                            sk.interestOps(sk.interestOps() & ~sk.readyOps());
                            if (sk.isReadable()) {
                                this.countDown(attachment.getReadLatch());
                            }
                            if (!sk.isWritable()) {
                                continue;
                            }
                            this.countDown(attachment.getWriteLatch());
                        }
                        catch (final CancelledKeyException ckx) {
                            sk.cancel();
                            this.countDown(attachment.getReadLatch());
                            this.countDown(attachment.getWriteLatch());
                        }
                    }
                }
                catch (final Throwable t) {
                    NioBlockingSelector.log.error((Object)"", t);
                }
            }
            this.events.clear();
            if (this.selector.isOpen()) {
                try {
                    this.selector.selectNow();
                }
                catch (final Exception ignore) {
                    if (NioBlockingSelector.log.isDebugEnabled()) {
                        NioBlockingSelector.log.debug((Object)"", (Throwable)ignore);
                    }
                }
            }
            try {
                this.selector.close();
            }
            catch (final Exception ignore) {
                if (NioBlockingSelector.log.isDebugEnabled()) {
                    NioBlockingSelector.log.debug((Object)"", (Throwable)ignore);
                }
            }
        }
        
        public void countDown(final CountDownLatch latch) {
            if (latch == null) {
                return;
            }
            latch.countDown();
        }
        
        private class RunnableAdd implements Runnable
        {
            private final SocketChannel ch;
            private final NioEndpoint.NioSocketWrapper key;
            private final int ops;
            private final KeyReference ref;
            
            public RunnableAdd(final SocketChannel ch, final NioEndpoint.NioSocketWrapper key, final int ops, final KeyReference ref) {
                this.ch = ch;
                this.key = key;
                this.ops = ops;
                this.ref = ref;
            }
            
            @Override
            public void run() {
                SelectionKey sk = this.ch.keyFor(BlockPoller.this.selector);
                try {
                    if (sk == null) {
                        sk = this.ch.register(BlockPoller.this.selector, this.ops, this.key);
                        this.ref.key = sk;
                    }
                    else if (!sk.isValid()) {
                        BlockPoller.this.cancel(sk, this.key, this.ops);
                    }
                    else {
                        sk.interestOps(sk.interestOps() | this.ops);
                    }
                }
                catch (final CancelledKeyException cx) {
                    BlockPoller.this.cancel(sk, this.key, this.ops);
                }
                catch (final ClosedChannelException cx2) {
                    BlockPoller.this.cancel(null, this.key, this.ops);
                }
            }
        }
        
        private class RunnableRemove implements Runnable
        {
            private final SocketChannel ch;
            private final NioEndpoint.NioSocketWrapper key;
            private final int ops;
            
            public RunnableRemove(final SocketChannel ch, final NioEndpoint.NioSocketWrapper key, final int ops) {
                this.ch = ch;
                this.key = key;
                this.ops = ops;
            }
            
            @Override
            public void run() {
                final SelectionKey sk = this.ch.keyFor(BlockPoller.this.selector);
                try {
                    if (sk == null) {
                        if (0x4 == (this.ops & 0x4)) {
                            BlockPoller.this.countDown(this.key.getWriteLatch());
                        }
                        if (0x1 == (this.ops & 0x1)) {
                            BlockPoller.this.countDown(this.key.getReadLatch());
                        }
                    }
                    else if (sk.isValid()) {
                        sk.interestOps(sk.interestOps() & ~this.ops);
                        if (0x4 == (this.ops & 0x4)) {
                            BlockPoller.this.countDown(this.key.getWriteLatch());
                        }
                        if (0x1 == (this.ops & 0x1)) {
                            BlockPoller.this.countDown(this.key.getReadLatch());
                        }
                        if (sk.interestOps() == 0) {
                            sk.cancel();
                            sk.attach(null);
                        }
                    }
                    else {
                        sk.cancel();
                        sk.attach(null);
                    }
                }
                catch (final CancelledKeyException cx) {
                    if (sk != null) {
                        sk.cancel();
                        sk.attach(null);
                    }
                }
            }
        }
        
        public static class RunnableCancel implements Runnable
        {
            private final SelectionKey key;
            
            public RunnableCancel(final SelectionKey key) {
                this.key = key;
            }
            
            @Override
            public void run() {
                this.key.cancel();
            }
        }
    }
    
    public static class KeyReference
    {
        SelectionKey key;
        
        public KeyReference() {
            this.key = null;
        }
        
        public void finalize() {
            if (this.key != null && this.key.isValid()) {
                NioBlockingSelector.log.warn((Object)"Possible key leak, cancelling key in the finalizer.");
                try {
                    this.key.cancel();
                }
                catch (final Exception ex) {}
            }
        }
    }
}
