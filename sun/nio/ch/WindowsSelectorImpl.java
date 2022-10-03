package sun.nio.ch;

import java.util.HashMap;
import java.nio.channels.Selector;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.spi.AbstractSelectionKey;
import java.util.Iterator;
import java.nio.channels.ClosedSelectorException;
import java.io.IOException;
import java.util.ArrayList;
import java.nio.channels.spi.SelectorProvider;
import java.nio.channels.Pipe;
import java.util.List;
import sun.misc.Unsafe;

final class WindowsSelectorImpl extends SelectorImpl
{
    private static final Unsafe unsafe;
    private static int addressSize;
    private final int INIT_CAP = 8;
    private static final int MAX_SELECTABLE_FDS = 1024;
    private static final long SIZEOF_FD_SET;
    private SelectionKeyImpl[] channelArray;
    private PollArrayWrapper pollWrapper;
    private int totalChannels;
    private int threadsCount;
    private final List<SelectThread> threads;
    private final Pipe wakeupPipe;
    private final int wakeupSourceFd;
    private final int wakeupSinkFd;
    private Object closeLock;
    private final FdMap fdMap;
    private final SubSelector subSelector;
    private long timeout;
    private final Object interruptLock;
    private volatile boolean interruptTriggered;
    private final StartLock startLock;
    private final FinishLock finishLock;
    private long updateCount;
    
    private static int dependsArch(final int n, final int n2) {
        return (WindowsSelectorImpl.addressSize == 4) ? n : n2;
    }
    
    WindowsSelectorImpl(final SelectorProvider selectorProvider) throws IOException {
        super(selectorProvider);
        this.channelArray = new SelectionKeyImpl[8];
        this.totalChannels = 1;
        this.threadsCount = 0;
        this.threads = new ArrayList<SelectThread>();
        this.closeLock = new Object();
        this.fdMap = new FdMap();
        this.subSelector = new SubSelector();
        this.interruptLock = new Object();
        this.interruptTriggered = false;
        this.startLock = new StartLock();
        this.finishLock = new FinishLock();
        this.updateCount = 0L;
        this.pollWrapper = new PollArrayWrapper(8);
        this.wakeupPipe = Pipe.open();
        this.wakeupSourceFd = ((SelChImpl)this.wakeupPipe.source()).getFDVal();
        final SinkChannelImpl sinkChannelImpl = (SinkChannelImpl)this.wakeupPipe.sink();
        sinkChannelImpl.sc.socket().setTcpNoDelay(true);
        this.wakeupSinkFd = sinkChannelImpl.getFDVal();
        this.pollWrapper.addWakeupSocket(this.wakeupSourceFd, 0);
    }
    
    @Override
    protected int doSelect(final long timeout) throws IOException {
        if (this.channelArray == null) {
            throw new ClosedSelectorException();
        }
        this.timeout = timeout;
        this.processDeregisterQueue();
        if (this.interruptTriggered) {
            this.resetWakeupSocket();
            return 0;
        }
        this.adjustThreadsCount();
        this.finishLock.reset();
        this.startLock.startThreads();
        try {
            this.begin();
            try {
                this.subSelector.poll();
            }
            catch (final IOException ex) {
                this.finishLock.setException(ex);
            }
            if (this.threads.size() > 0) {
                this.finishLock.waitForHelperThreads();
            }
        }
        finally {
            this.end();
        }
        this.finishLock.checkForException();
        this.processDeregisterQueue();
        final int updateSelectedKeys = this.updateSelectedKeys();
        this.resetWakeupSocket();
        return updateSelectedKeys;
    }
    
    private void adjustThreadsCount() {
        if (this.threadsCount > this.threads.size()) {
            for (int i = this.threads.size(); i < this.threadsCount; ++i) {
                final SelectThread selectThread = new SelectThread(i);
                this.threads.add(selectThread);
                selectThread.setDaemon(true);
                selectThread.start();
            }
        }
        else if (this.threadsCount < this.threads.size()) {
            for (int j = this.threads.size() - 1; j >= this.threadsCount; --j) {
                this.threads.remove(j).makeZombie();
            }
        }
    }
    
    private void setWakeupSocket() {
        this.setWakeupSocket0(this.wakeupSinkFd);
    }
    
    private native void setWakeupSocket0(final int p0);
    
    private void resetWakeupSocket() {
        synchronized (this.interruptLock) {
            if (!this.interruptTriggered) {
                return;
            }
            this.resetWakeupSocket0(this.wakeupSourceFd);
            this.interruptTriggered = false;
        }
    }
    
    private native void resetWakeupSocket0(final int p0);
    
    private native boolean discardUrgentData(final int p0);
    
    private int updateSelectedKeys() {
        ++this.updateCount;
        int n = 0 + this.subSelector.processSelectedKeys(this.updateCount);
        final Iterator<SelectThread> iterator = this.threads.iterator();
        while (iterator.hasNext()) {
            n += iterator.next().subSelector.processSelectedKeys(this.updateCount);
        }
        return n;
    }
    
    @Override
    protected void implClose() throws IOException {
        synchronized (this.closeLock) {
            if (this.channelArray != null && this.pollWrapper != null) {
                synchronized (this.interruptLock) {
                    this.interruptTriggered = true;
                }
                this.wakeupPipe.sink().close();
                this.wakeupPipe.source().close();
                for (int i = 1; i < this.totalChannels; ++i) {
                    if (i % 1024 != 0) {
                        this.deregister(this.channelArray[i]);
                        final SelectableChannel channel = this.channelArray[i].channel();
                        if (!channel.isOpen() && !channel.isRegistered()) {
                            ((SelChImpl)channel).kill();
                        }
                    }
                }
                this.pollWrapper.free();
                this.pollWrapper = null;
                this.selectedKeys = null;
                this.channelArray = null;
                final Iterator<SelectThread> iterator = this.threads.iterator();
                while (iterator.hasNext()) {
                    iterator.next().makeZombie();
                }
                this.startLock.startThreads();
                this.subSelector.freeFDSetBuffer();
            }
        }
    }
    
    @Override
    protected void implRegister(final SelectionKeyImpl selectionKeyImpl) {
        synchronized (this.closeLock) {
            if (this.pollWrapper == null) {
                throw new ClosedSelectorException();
            }
            this.growIfNeeded();
            (this.channelArray[this.totalChannels] = selectionKeyImpl).setIndex(this.totalChannels);
            this.fdMap.put(selectionKeyImpl);
            this.keys.add(selectionKeyImpl);
            this.pollWrapper.addEntry(this.totalChannels, selectionKeyImpl);
            ++this.totalChannels;
        }
    }
    
    private void growIfNeeded() {
        if (this.channelArray.length == this.totalChannels) {
            final int n = this.totalChannels * 2;
            final SelectionKeyImpl[] channelArray = new SelectionKeyImpl[n];
            System.arraycopy(this.channelArray, 1, channelArray, 1, this.totalChannels - 1);
            this.channelArray = channelArray;
            this.pollWrapper.grow(n);
        }
        if (this.totalChannels % 1024 == 0) {
            this.pollWrapper.addWakeupSocket(this.wakeupSourceFd, this.totalChannels);
            ++this.totalChannels;
            ++this.threadsCount;
        }
    }
    
    @Override
    protected void implDereg(final SelectionKeyImpl selectionKeyImpl) throws IOException {
        final int index = selectionKeyImpl.getIndex();
        assert index >= 0;
        synchronized (this.closeLock) {
            if (index != this.totalChannels - 1) {
                (this.channelArray[index] = this.channelArray[this.totalChannels - 1]).setIndex(index);
                this.pollWrapper.replaceEntry(this.pollWrapper, this.totalChannels - 1, this.pollWrapper, index);
            }
            selectionKeyImpl.setIndex(-1);
        }
        this.channelArray[this.totalChannels - 1] = null;
        --this.totalChannels;
        if (this.totalChannels != 1 && this.totalChannels % 1024 == 1) {
            --this.totalChannels;
            --this.threadsCount;
        }
        this.fdMap.remove(selectionKeyImpl);
        this.keys.remove(selectionKeyImpl);
        this.selectedKeys.remove(selectionKeyImpl);
        this.deregister(selectionKeyImpl);
        final SelectableChannel channel = selectionKeyImpl.channel();
        if (!channel.isOpen() && !channel.isRegistered()) {
            ((SelChImpl)channel).kill();
        }
    }
    
    @Override
    public void putEventOps(final SelectionKeyImpl selectionKeyImpl, final int n) {
        synchronized (this.closeLock) {
            if (this.pollWrapper == null) {
                throw new ClosedSelectorException();
            }
            final int index = selectionKeyImpl.getIndex();
            if (index == -1) {
                throw new CancelledKeyException();
            }
            this.pollWrapper.putEventOps(index, n);
        }
    }
    
    @Override
    public Selector wakeup() {
        synchronized (this.interruptLock) {
            if (!this.interruptTriggered) {
                this.setWakeupSocket();
                this.interruptTriggered = true;
            }
        }
        return this;
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
        WindowsSelectorImpl.addressSize = WindowsSelectorImpl.unsafe.addressSize();
        SIZEOF_FD_SET = dependsArch(4100, 8200);
        IOUtil.load();
    }
    
    private static final class FdMap extends HashMap<Integer, MapEntry>
    {
        static final long serialVersionUID = 0L;
        
        private MapEntry get(final int n) {
            return ((HashMap<K, MapEntry>)this).get(new Integer(n));
        }
        
        private MapEntry put(final SelectionKeyImpl selectionKeyImpl) {
            return this.put(new Integer(selectionKeyImpl.channel.getFDVal()), new MapEntry(selectionKeyImpl));
        }
        
        private MapEntry remove(final SelectionKeyImpl selectionKeyImpl) {
            final Integer n = new Integer(selectionKeyImpl.channel.getFDVal());
            final MapEntry mapEntry = ((HashMap<K, MapEntry>)this).get(n);
            if (mapEntry != null && mapEntry.ski.channel == selectionKeyImpl.channel) {
                return this.remove(n);
            }
            return null;
        }
    }
    
    private static final class MapEntry
    {
        SelectionKeyImpl ski;
        long updateCount;
        long clearedCount;
        
        MapEntry(final SelectionKeyImpl ski) {
            this.updateCount = 0L;
            this.clearedCount = 0L;
            this.ski = ski;
        }
    }
    
    private final class StartLock
    {
        private long runsCounter;
        
        private synchronized void startThreads() {
            ++this.runsCounter;
            this.notifyAll();
        }
        
        private synchronized boolean waitForStart(final SelectThread selectThread) {
            while (this.runsCounter == selectThread.lastRun) {
                try {
                    WindowsSelectorImpl.this.startLock.wait();
                }
                catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            if (selectThread.isZombie()) {
                return true;
            }
            selectThread.lastRun = this.runsCounter;
            return false;
        }
    }
    
    private final class FinishLock
    {
        private int threadsToFinish;
        IOException exception;
        
        private FinishLock() {
            this.exception = null;
        }
        
        private void reset() {
            this.threadsToFinish = WindowsSelectorImpl.this.threads.size();
        }
        
        private synchronized void threadFinished() {
            if (this.threadsToFinish == WindowsSelectorImpl.this.threads.size()) {
                WindowsSelectorImpl.this.wakeup();
            }
            --this.threadsToFinish;
            if (this.threadsToFinish == 0) {
                this.notify();
            }
        }
        
        private synchronized void waitForHelperThreads() {
            if (this.threadsToFinish == WindowsSelectorImpl.this.threads.size()) {
                WindowsSelectorImpl.this.wakeup();
            }
            while (this.threadsToFinish != 0) {
                try {
                    WindowsSelectorImpl.this.finishLock.wait();
                }
                catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        private synchronized void setException(final IOException exception) {
            this.exception = exception;
        }
        
        private void checkForException() throws IOException {
            if (this.exception == null) {
                return;
            }
            final StringBuffer sb = new StringBuffer("An exception occurred during the execution of select(): \n");
            sb.append(this.exception);
            sb.append('\n');
            this.exception = null;
            throw new IOException(sb.toString());
        }
    }
    
    private final class SubSelector
    {
        private final int pollArrayIndex;
        private final int[] readFds;
        private final int[] writeFds;
        private final int[] exceptFds;
        private final long fdsBuffer;
        
        private SubSelector() {
            this.readFds = new int[1025];
            this.writeFds = new int[1025];
            this.exceptFds = new int[1025];
            this.fdsBuffer = WindowsSelectorImpl.unsafe.allocateMemory(WindowsSelectorImpl.SIZEOF_FD_SET * 6L);
            this.pollArrayIndex = 0;
        }
        
        private SubSelector(final int n) {
            this.readFds = new int[1025];
            this.writeFds = new int[1025];
            this.exceptFds = new int[1025];
            this.fdsBuffer = WindowsSelectorImpl.unsafe.allocateMemory(WindowsSelectorImpl.SIZEOF_FD_SET * 6L);
            this.pollArrayIndex = (n + 1) * 1024;
        }
        
        private int poll() throws IOException {
            return this.poll0(WindowsSelectorImpl.this.pollWrapper.pollArrayAddress, Math.min(WindowsSelectorImpl.this.totalChannels, 1024), this.readFds, this.writeFds, this.exceptFds, WindowsSelectorImpl.this.timeout, this.fdsBuffer);
        }
        
        private int poll(final int n) throws IOException {
            return this.poll0(WindowsSelectorImpl.this.pollWrapper.pollArrayAddress + this.pollArrayIndex * PollArrayWrapper.SIZE_POLLFD, Math.min(1024, WindowsSelectorImpl.this.totalChannels - (n + 1) * 1024), this.readFds, this.writeFds, this.exceptFds, WindowsSelectorImpl.this.timeout, this.fdsBuffer);
        }
        
        private native int poll0(final long p0, final int p1, final int[] p2, final int[] p3, final int[] p4, final long p5, final long p6);
        
        private int processSelectedKeys(final long n) {
            return 0 + this.processFDSet(n, this.readFds, Net.POLLIN, false) + this.processFDSet(n, this.writeFds, Net.POLLCONN | Net.POLLOUT, false) + this.processFDSet(n, this.exceptFds, Net.POLLIN | Net.POLLCONN | Net.POLLOUT, true);
        }
        
        private int processFDSet(final long n, final int[] array, final int n2, final boolean b) {
            int n3 = 0;
            for (int i = 1; i <= array[0]; ++i) {
                final int n4 = array[i];
                if (n4 == WindowsSelectorImpl.this.wakeupSourceFd) {
                    synchronized (WindowsSelectorImpl.this.interruptLock) {
                        WindowsSelectorImpl.this.interruptTriggered = true;
                    }
                }
                else {
                    final MapEntry access$2300 = WindowsSelectorImpl.this.fdMap.get(n4);
                    if (access$2300 != null) {
                        final SelectionKeyImpl ski = access$2300.ski;
                        if (!b || !(ski.channel() instanceof SocketChannelImpl) || !WindowsSelectorImpl.this.discardUrgentData(n4)) {
                            if (WindowsSelectorImpl.this.selectedKeys.contains(ski)) {
                                if (access$2300.clearedCount != n) {
                                    if (ski.channel.translateAndSetReadyOps(n2, ski) && access$2300.updateCount != n) {
                                        access$2300.updateCount = n;
                                        ++n3;
                                    }
                                }
                                else if (ski.channel.translateAndUpdateReadyOps(n2, ski) && access$2300.updateCount != n) {
                                    access$2300.updateCount = n;
                                    ++n3;
                                }
                                access$2300.clearedCount = n;
                            }
                            else {
                                if (access$2300.clearedCount != n) {
                                    ski.channel.translateAndSetReadyOps(n2, ski);
                                    if ((ski.nioReadyOps() & ski.nioInterestOps()) != 0x0) {
                                        WindowsSelectorImpl.this.selectedKeys.add(ski);
                                        access$2300.updateCount = n;
                                        ++n3;
                                    }
                                }
                                else {
                                    ski.channel.translateAndUpdateReadyOps(n2, ski);
                                    if ((ski.nioReadyOps() & ski.nioInterestOps()) != 0x0) {
                                        WindowsSelectorImpl.this.selectedKeys.add(ski);
                                        access$2300.updateCount = n;
                                        ++n3;
                                    }
                                }
                                access$2300.clearedCount = n;
                            }
                        }
                    }
                }
            }
            return n3;
        }
        
        private void freeFDSetBuffer() {
            WindowsSelectorImpl.unsafe.freeMemory(this.fdsBuffer);
        }
    }
    
    private final class SelectThread extends Thread
    {
        private final int index;
        final SubSelector subSelector;
        private long lastRun;
        private volatile boolean zombie;
        
        private SelectThread(final int index) {
            this.lastRun = 0L;
            this.index = index;
            this.subSelector = new SubSelector(index);
            this.lastRun = WindowsSelectorImpl.this.startLock.runsCounter;
        }
        
        void makeZombie() {
            this.zombie = true;
        }
        
        boolean isZombie() {
            return this.zombie;
        }
        
        @Override
        public void run() {
            while (!WindowsSelectorImpl.this.startLock.waitForStart(this)) {
                try {
                    this.subSelector.poll(this.index);
                }
                catch (final IOException ex) {
                    WindowsSelectorImpl.this.finishLock.setException(ex);
                }
                WindowsSelectorImpl.this.finishLock.threadFinished();
            }
            this.subSelector.freeFDSetBuffer();
        }
    }
}
