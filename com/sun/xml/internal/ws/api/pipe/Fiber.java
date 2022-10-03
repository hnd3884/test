package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import javax.xml.ws.WebServiceException;
import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import javax.xml.ws.Holder;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.istack.internal.NotNull;
import java.util.logging.Level;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Collection;
import java.util.ArrayList;
import com.sun.xml.internal.ws.api.Component;
import java.util.Set;
import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.List;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import com.sun.xml.internal.ws.api.Cancelable;

public final class Fiber implements Runnable, Cancelable, ComponentRegistry
{
    private final List<Listener> _listeners;
    private Tube[] conts;
    private int contsSize;
    private Tube next;
    private Packet packet;
    private Throwable throwable;
    public final Engine owner;
    private volatile int suspendedCount;
    private volatile boolean isInsideSuspendCallbacks;
    private boolean synchronous;
    private boolean interrupted;
    private final int id;
    private List<FiberContextSwitchInterceptor> interceptors;
    @Nullable
    private ClassLoader contextClassLoader;
    @Nullable
    private CompletionCallback completionCallback;
    private boolean isDeliverThrowableInPacket;
    private Thread currentThread;
    private final ReentrantLock lock;
    private final Condition condition;
    private volatile boolean isCanceled;
    private boolean started;
    private boolean startedSync;
    private static final PlaceholderTube PLACEHOLDER;
    private static final ThreadLocal<Fiber> CURRENT_FIBER;
    private static final AtomicInteger iotaGen;
    private static final Logger LOGGER;
    private static final ReentrantLock serializedExecutionLock;
    public static volatile boolean serializeExecution;
    private final Set<Component> components;
    
    @Deprecated
    public void addListener(final Listener listener) {
        synchronized (this._listeners) {
            if (!this._listeners.contains(listener)) {
                this._listeners.add(listener);
            }
        }
    }
    
    @Deprecated
    public void removeListener(final Listener listener) {
        synchronized (this._listeners) {
            this._listeners.remove(listener);
        }
    }
    
    List<Listener> getCurrentListeners() {
        synchronized (this._listeners) {
            return new ArrayList<Listener>(this._listeners);
        }
    }
    
    private void clearListeners() {
        synchronized (this._listeners) {
            this._listeners.clear();
        }
    }
    
    public void setDeliverThrowableInPacket(final boolean isDeliverThrowableInPacket) {
        this.isDeliverThrowableInPacket = isDeliverThrowableInPacket;
    }
    
    Fiber(final Engine engine) {
        this._listeners = new ArrayList<Listener>();
        this.conts = new Tube[16];
        this.suspendedCount = 0;
        this.isInsideSuspendCallbacks = false;
        this.isDeliverThrowableInPacket = false;
        this.lock = new ReentrantLock();
        this.condition = this.lock.newCondition();
        this.components = new CopyOnWriteArraySet<Component>();
        this.owner = engine;
        this.id = Fiber.iotaGen.incrementAndGet();
        if (isTraceEnabled()) {
            Fiber.LOGGER.log(Level.FINE, "{0} created", this.getName());
        }
        this.contextClassLoader = Thread.currentThread().getContextClassLoader();
    }
    
    public void start(@NotNull final Tube tubeline, @NotNull final Packet request, @Nullable final CompletionCallback completionCallback) {
        this.start(tubeline, request, completionCallback, false);
    }
    
    private void dumpFiberContext(final String desc) {
        if (isTraceEnabled()) {
            String action = null;
            String msgId = null;
            if (this.packet != null) {
                for (final SOAPVersion sv : SOAPVersion.values()) {
                    for (final AddressingVersion av : AddressingVersion.values()) {
                        action = ((this.packet.getMessage() != null) ? AddressingUtils.getAction(this.packet.getMessage().getHeaders(), av, sv) : null);
                        msgId = ((this.packet.getMessage() != null) ? AddressingUtils.getMessageID(this.packet.getMessage().getHeaders(), av, sv) : null);
                        if (action != null) {
                            break;
                        }
                        if (msgId != null) {
                            break;
                        }
                    }
                    if (action != null) {
                        break;
                    }
                    if (msgId != null) {
                        break;
                    }
                }
            }
            String actionAndMsgDesc;
            if (action == null && msgId == null) {
                actionAndMsgDesc = "NO ACTION or MSG ID";
            }
            else {
                actionAndMsgDesc = "'" + action + "' and msgId '" + msgId + "'";
            }
            String tubeDesc;
            if (this.next != null) {
                tubeDesc = this.next.toString() + ".processRequest()";
            }
            else {
                tubeDesc = this.peekCont() + ".processResponse()";
            }
            Fiber.LOGGER.log(Level.FINE, "{0} {1} with {2} and ''current'' tube {3} from thread {4} with Packet: {5}", new Object[] { this.getName(), desc, actionAndMsgDesc, tubeDesc, Thread.currentThread().getName(), (this.packet != null) ? this.packet.toShortString() : null });
        }
    }
    
    public void start(@NotNull final Tube tubeline, @NotNull final Packet request, @Nullable final CompletionCallback completionCallback, final boolean forceSync) {
        this.next = tubeline;
        this.packet = request;
        this.completionCallback = completionCallback;
        if (forceSync) {
            this.startedSync = true;
            this.dumpFiberContext("starting (sync)");
            this.run();
        }
        else {
            this.started = true;
            this.dumpFiberContext("starting (async)");
            this.owner.addRunnable(this);
        }
    }
    
    public void resume(@NotNull final Packet resumePacket) {
        this.resume(resumePacket, false);
    }
    
    public void resume(@NotNull final Packet resumePacket, final boolean forceSync) {
        this.resume(resumePacket, forceSync, null);
    }
    
    public void resume(@NotNull final Packet resumePacket, final boolean forceSync, final CompletionCallback callback) {
        this.lock.lock();
        try {
            if (callback != null) {
                this.setCompletionCallback(callback);
            }
            if (isTraceEnabled()) {
                Fiber.LOGGER.log(Level.FINE, "{0} resuming. Will have suspendedCount={1}", new Object[] { this.getName(), this.suspendedCount - 1 });
            }
            this.packet = resumePacket;
            if (--this.suspendedCount == 0) {
                if (!this.isInsideSuspendCallbacks) {
                    final List<Listener> listeners = this.getCurrentListeners();
                    for (final Listener listener : listeners) {
                        try {
                            listener.fiberResumed(this);
                        }
                        catch (final Throwable e) {
                            if (!isTraceEnabled()) {
                                continue;
                            }
                            Fiber.LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[] { listener, e.getMessage() });
                        }
                    }
                    if (this.synchronous) {
                        this.condition.signalAll();
                    }
                    else if (forceSync || this.startedSync) {
                        this.run();
                    }
                    else {
                        this.dumpFiberContext("resuming (async)");
                        this.owner.addRunnable(this);
                    }
                }
            }
            else if (isTraceEnabled()) {
                Fiber.LOGGER.log(Level.FINE, "{0} taking no action on resume because suspendedCount != 0: {1}", new Object[] { this.getName(), this.suspendedCount });
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void resumeAndReturn(@NotNull final Packet resumePacket, final boolean forceSync) {
        if (isTraceEnabled()) {
            Fiber.LOGGER.log(Level.FINE, "{0} resumed with Return Packet", this.getName());
        }
        this.next = null;
        this.resume(resumePacket, forceSync);
    }
    
    public void resume(@NotNull final Throwable throwable) {
        this.resume(throwable, this.packet, false);
    }
    
    public void resume(@NotNull final Throwable throwable, @NotNull final Packet packet) {
        this.resume(throwable, packet, false);
    }
    
    public void resume(@NotNull final Throwable error, final boolean forceSync) {
        this.resume(error, this.packet, forceSync);
    }
    
    public void resume(@NotNull final Throwable error, @NotNull final Packet packet, final boolean forceSync) {
        if (isTraceEnabled()) {
            Fiber.LOGGER.log(Level.FINE, "{0} resumed with Return Throwable", this.getName());
        }
        this.next = null;
        this.throwable = error;
        this.resume(packet, forceSync);
    }
    
    @Override
    public void cancel(final boolean mayInterrupt) {
        this.isCanceled = true;
        if (mayInterrupt) {
            synchronized (this) {
                if (this.currentThread != null) {
                    this.currentThread.interrupt();
                }
            }
        }
    }
    
    private boolean suspend(final Holder<Boolean> isRequireUnlock, final Runnable onExitRunnable) {
        if (isTraceEnabled()) {
            Fiber.LOGGER.log(Level.FINE, "{0} suspending. Will have suspendedCount={1}", new Object[] { this.getName(), this.suspendedCount + 1 });
            if (this.suspendedCount > 0) {
                Fiber.LOGGER.log(Level.FINE, "WARNING - {0} suspended more than resumed. Will require more than one resume to actually resume this fiber.", this.getName());
            }
        }
        final List<Listener> listeners = this.getCurrentListeners();
        if (++this.suspendedCount == 1) {
            this.isInsideSuspendCallbacks = true;
            try {
                for (final Listener listener : listeners) {
                    try {
                        listener.fiberSuspended(this);
                    }
                    catch (final Throwable e) {
                        if (!isTraceEnabled()) {
                            continue;
                        }
                        Fiber.LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[] { listener, e.getMessage() });
                    }
                }
            }
            finally {
                this.isInsideSuspendCallbacks = false;
            }
        }
        if (this.suspendedCount <= 0) {
            for (final Listener listener : listeners) {
                try {
                    listener.fiberResumed(this);
                }
                catch (final Throwable e) {
                    if (!isTraceEnabled()) {
                        continue;
                    }
                    Fiber.LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[] { listener, e.getMessage() });
                }
            }
        }
        else if (onExitRunnable != null) {
            if (!this.synchronous) {
                synchronized (this) {
                    this.currentThread = null;
                }
                this.lock.unlock();
                assert !this.lock.isHeldByCurrentThread();
                isRequireUnlock.value = Boolean.FALSE;
                try {
                    onExitRunnable.run();
                }
                catch (final Throwable t) {
                    throw new OnExitRunnableException(t);
                }
                return true;
            }
            else {
                if (isTraceEnabled()) {
                    Fiber.LOGGER.fine("onExitRunnable used with synchronous Fiber execution -- not exiting current thread");
                }
                onExitRunnable.run();
            }
        }
        return false;
    }
    
    public synchronized void addInterceptor(@NotNull final FiberContextSwitchInterceptor interceptor) {
        if (this.interceptors == null) {
            this.interceptors = new ArrayList<FiberContextSwitchInterceptor>();
        }
        else {
            final List<FiberContextSwitchInterceptor> l = new ArrayList<FiberContextSwitchInterceptor>();
            l.addAll(this.interceptors);
            this.interceptors = l;
        }
        this.interceptors.add(interceptor);
    }
    
    public synchronized boolean removeInterceptor(@NotNull final FiberContextSwitchInterceptor interceptor) {
        if (this.interceptors != null) {
            final boolean result = this.interceptors.remove(interceptor);
            if (this.interceptors.isEmpty()) {
                this.interceptors = null;
            }
            else {
                final List<FiberContextSwitchInterceptor> l = new ArrayList<FiberContextSwitchInterceptor>();
                l.addAll(this.interceptors);
                this.interceptors = l;
            }
            return result;
        }
        return false;
    }
    
    @Nullable
    public ClassLoader getContextClassLoader() {
        return this.contextClassLoader;
    }
    
    public ClassLoader setContextClassLoader(@Nullable final ClassLoader contextClassLoader) {
        final ClassLoader r = this.contextClassLoader;
        this.contextClassLoader = contextClassLoader;
        return r;
    }
    
    @Deprecated
    @Override
    public void run() {
        final Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
        try {
            assert !this.synchronous;
            if (!this.doRun()) {
                if (this.startedSync && this.suspendedCount == 0 && (this.next != null || this.contsSize > 0)) {
                    this.startedSync = false;
                    this.dumpFiberContext("restarting (async) after startSync");
                    this.owner.addRunnable(this);
                }
                else {
                    this.completionCheck();
                }
            }
        }
        finally {
            ContainerResolver.getDefault().exitContainer(old);
        }
    }
    
    @NotNull
    public Packet runSync(@NotNull final Tube tubeline, @NotNull final Packet request) {
        this.lock.lock();
        try {
            final Tube[] oldCont = this.conts;
            final int oldContSize = this.contsSize;
            final boolean oldSynchronous = this.synchronous;
            final Tube oldNext = this.next;
            if (oldContSize > 0) {
                this.conts = new Tube[16];
                this.contsSize = 0;
            }
            try {
                this.synchronous = true;
                this.packet = request;
                this.next = tubeline;
                this.doRun();
                if (this.throwable != null) {
                    if (this.isDeliverThrowableInPacket) {
                        this.packet.addSatellite(new ThrowableContainerPropertySet(this.throwable));
                    }
                    else {
                        if (this.throwable instanceof RuntimeException) {
                            throw (RuntimeException)this.throwable;
                        }
                        if (this.throwable instanceof Error) {
                            throw (Error)this.throwable;
                        }
                        throw new AssertionError((Object)this.throwable);
                    }
                }
                return this.packet;
            }
            finally {
                this.conts = oldCont;
                this.contsSize = oldContSize;
                this.synchronous = oldSynchronous;
                this.next = oldNext;
                if (this.interrupted) {
                    Thread.currentThread().interrupt();
                    this.interrupted = false;
                }
                if (!this.started && !this.startedSync) {
                    this.completionCheck();
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private void completionCheck() {
        this.lock.lock();
        try {
            if (!this.isCanceled && this.contsSize == 0 && this.suspendedCount == 0) {
                if (isTraceEnabled()) {
                    Fiber.LOGGER.log(Level.FINE, "{0} completed", this.getName());
                }
                this.clearListeners();
                this.condition.signalAll();
                if (this.completionCallback != null) {
                    if (this.throwable != null) {
                        if (this.isDeliverThrowableInPacket) {
                            this.packet.addSatellite(new ThrowableContainerPropertySet(this.throwable));
                            this.completionCallback.onCompletion(this.packet);
                        }
                        else {
                            this.completionCallback.onCompletion(this.throwable);
                        }
                    }
                    else {
                        this.completionCallback.onCompletion(this.packet);
                    }
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private boolean doRun() {
        this.dumpFiberContext("running");
        if (Fiber.serializeExecution) {
            Fiber.serializedExecutionLock.lock();
            try {
                return this._doRun(this.next);
            }
            finally {
                Fiber.serializedExecutionLock.unlock();
            }
        }
        return this._doRun(this.next);
    }
    
    private boolean _doRun(Tube next) {
        final Holder<Boolean> isRequireUnlock = new Holder<Boolean>(Boolean.TRUE);
        this.lock.lock();
        try {
            Label_0090: {
                List<FiberContextSwitchInterceptor> ints;
                final ClassLoader old;
                synchronized (this) {
                    ints = this.interceptors;
                    this.currentThread = Thread.currentThread();
                    if (isTraceEnabled()) {
                        Fiber.LOGGER.log(Level.FINE, "Thread entering _doRun(): {0}", this.currentThread);
                    }
                    old = this.currentThread.getContextClassLoader();
                    this.currentThread.setContextClassLoader(this.contextClassLoader);
                    break Label_0090;
                }
                try {
                    boolean needsToReenter;
                    do {
                        if (ints == null) {
                            this.next = next;
                            if (this.__doRun(isRequireUnlock, null)) {
                                return true;
                            }
                        }
                        else {
                            next = new InterceptorHandler(isRequireUnlock, ints).invoke(next);
                            if (next == Fiber.PLACEHOLDER) {
                                return true;
                            }
                        }
                        synchronized (this) {
                            needsToReenter = (ints != this.interceptors);
                            if (!needsToReenter) {
                                continue;
                            }
                            ints = this.interceptors;
                        }
                    } while (needsToReenter);
                }
                catch (final OnExitRunnableException o) {
                    final Throwable t = o.target;
                    if (t instanceof WebServiceException) {
                        throw (WebServiceException)t;
                    }
                    throw new WebServiceException(t);
                }
                finally {
                    final Thread thread = Thread.currentThread();
                    thread.setContextClassLoader(old);
                    if (isTraceEnabled()) {
                        Fiber.LOGGER.log(Level.FINE, "Thread leaving _doRun(): {0}", thread);
                    }
                }
            }
            return false;
        }
        finally {
            if (isRequireUnlock.value) {
                synchronized (this) {
                    this.currentThread = null;
                }
                this.lock.unlock();
            }
        }
    }
    
    private boolean __doRun(final Holder<Boolean> isRequireUnlock, final List<FiberContextSwitchInterceptor> originalInterceptors) {
        assert this.lock.isHeldByCurrentThread();
        final Fiber old = Fiber.CURRENT_FIBER.get();
        Fiber.CURRENT_FIBER.set(this);
        final boolean traceEnabled = Fiber.LOGGER.isLoggable(Level.FINER);
        try {
            boolean abortResponse = false;
            while (this.isReady(originalInterceptors)) {
                if (this.isCanceled) {
                    this.next = null;
                    this.throwable = null;
                    this.contsSize = 0;
                    break;
                }
                try {
                    Tube last;
                    NextAction na;
                    if (this.throwable != null) {
                        if (this.contsSize == 0 || abortResponse) {
                            this.contsSize = 0;
                            return false;
                        }
                        last = this.popCont();
                        if (traceEnabled) {
                            Fiber.LOGGER.log(Level.FINER, "{0} {1}.processException({2})", new Object[] { this.getName(), last, this.throwable });
                        }
                        na = last.processException(this.throwable);
                    }
                    else if (this.next != null) {
                        if (traceEnabled) {
                            Fiber.LOGGER.log(Level.FINER, "{0} {1}.processRequest({2})", new Object[] { this.getName(), this.next, (this.packet != null) ? ("Packet@" + Integer.toHexString(this.packet.hashCode())) : "null" });
                        }
                        na = this.next.processRequest(this.packet);
                        last = this.next;
                    }
                    else {
                        if (this.contsSize == 0 || abortResponse) {
                            this.contsSize = 0;
                            return false;
                        }
                        last = this.popCont();
                        if (traceEnabled) {
                            Fiber.LOGGER.log(Level.FINER, "{0} {1}.processResponse({2})", new Object[] { this.getName(), last, (this.packet != null) ? ("Packet@" + Integer.toHexString(this.packet.hashCode())) : "null" });
                        }
                        na = last.processResponse(this.packet);
                    }
                    if (traceEnabled) {
                        Fiber.LOGGER.log(Level.FINER, "{0} {1} returned with {2}", new Object[] { this.getName(), last, na });
                    }
                    if (na.kind != 4) {
                        if (na.kind != 3 && na.kind != 5) {
                            this.packet = na.packet;
                        }
                        this.throwable = na.throwable;
                    }
                    switch (na.kind) {
                        case 0:
                        case 7: {
                            this.pushCont(last);
                        }
                        case 1: {
                            this.next = na.next;
                            if (na.kind == 7 && this.startedSync) {
                                return false;
                            }
                            break;
                        }
                        case 5:
                        case 6: {
                            abortResponse = true;
                            if (isTraceEnabled()) {
                                Fiber.LOGGER.log(Level.FINE, "Fiber {0} is aborting a response due to exception: {1}", new Object[] { this, na.throwable });
                            }
                        }
                        case 2:
                        case 3: {
                            this.next = null;
                            break;
                        }
                        case 4: {
                            if (this.next != null) {
                                this.pushCont(last);
                            }
                            this.next = na.next;
                            if (this.suspend(isRequireUnlock, na.onExitRunnable)) {
                                return true;
                            }
                            break;
                        }
                        default: {
                            throw new AssertionError();
                        }
                    }
                }
                catch (final RuntimeException t) {
                    if (traceEnabled) {
                        Fiber.LOGGER.log(Level.FINER, this.getName() + " Caught " + t + ". Start stack unwinding", t);
                    }
                    this.throwable = t;
                }
                catch (final Error t2) {
                    if (traceEnabled) {
                        Fiber.LOGGER.log(Level.FINER, this.getName() + " Caught " + t2 + ". Start stack unwinding", t2);
                    }
                    this.throwable = t2;
                }
                this.dumpFiberContext("After tube execution");
            }
        }
        finally {
            Fiber.CURRENT_FIBER.set(old);
        }
        return false;
    }
    
    private void pushCont(final Tube tube) {
        this.conts[this.contsSize++] = tube;
        final int len = this.conts.length;
        if (this.contsSize == len) {
            final Tube[] newBuf = new Tube[len * 2];
            System.arraycopy(this.conts, 0, newBuf, 0, len);
            this.conts = newBuf;
        }
    }
    
    private Tube popCont() {
        final Tube[] conts = this.conts;
        final int contsSize = this.contsSize - 1;
        this.contsSize = contsSize;
        return conts[contsSize];
    }
    
    private Tube peekCont() {
        final int index = this.contsSize - 1;
        if (index >= 0 && index < this.conts.length) {
            return this.conts[index];
        }
        return null;
    }
    
    public void resetCont(final Tube[] conts, final int contsSize) {
        this.conts = conts;
        this.contsSize = contsSize;
    }
    
    private boolean isReady(final List<FiberContextSwitchInterceptor> originalInterceptors) {
        if (this.synchronous) {
            while (this.suspendedCount == 1) {
                try {
                    if (isTraceEnabled()) {
                        Fiber.LOGGER.log(Level.FINE, "{0} is blocking thread {1}", new Object[] { this.getName(), Thread.currentThread().getName() });
                    }
                    this.condition.await();
                }
                catch (final InterruptedException e) {
                    this.interrupted = true;
                }
            }
            synchronized (this) {
                return this.interceptors == originalInterceptors;
            }
        }
        if (this.suspendedCount > 0) {
            return false;
        }
        synchronized (this) {
            return this.interceptors == originalInterceptors;
        }
    }
    
    private String getName() {
        return "engine-" + this.owner.id + "fiber-" + this.id;
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    @Nullable
    public Packet getPacket() {
        return this.packet;
    }
    
    public CompletionCallback getCompletionCallback() {
        return this.completionCallback;
    }
    
    public void setCompletionCallback(final CompletionCallback completionCallback) {
        this.completionCallback = completionCallback;
    }
    
    public static boolean isSynchronous() {
        return current().synchronous;
    }
    
    public boolean isStartedSync() {
        return this.startedSync;
    }
    
    @NotNull
    public static Fiber current() {
        final Fiber fiber = Fiber.CURRENT_FIBER.get();
        if (fiber == null) {
            throw new IllegalStateException("Can be only used from fibers");
        }
        return fiber;
    }
    
    public static Fiber getCurrentIfSet() {
        return Fiber.CURRENT_FIBER.get();
    }
    
    private static boolean isTraceEnabled() {
        return Fiber.LOGGER.isLoggable(Level.FINE);
    }
    
    @Override
    public <S> S getSPI(final Class<S> spiType) {
        for (final Component c : this.components) {
            final S spi = c.getSPI(spiType);
            if (spi != null) {
                return spi;
            }
        }
        return null;
    }
    
    @Override
    public Set<Component> getComponents() {
        return this.components;
    }
    
    static {
        PLACEHOLDER = new PlaceholderTube();
        CURRENT_FIBER = new ThreadLocal<Fiber>();
        iotaGen = new AtomicInteger();
        LOGGER = Logger.getLogger(Fiber.class.getName());
        serializedExecutionLock = new ReentrantLock();
        Fiber.serializeExecution = Boolean.getBoolean(Fiber.class.getName() + ".serialize");
    }
    
    private static final class OnExitRunnableException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
        Throwable target;
        
        public OnExitRunnableException(final Throwable target) {
            super((Throwable)null);
            this.target = target;
        }
    }
    
    private class InterceptorHandler implements FiberContextSwitchInterceptor.Work<Tube, Tube>
    {
        private final Holder<Boolean> isUnlockRequired;
        private final List<FiberContextSwitchInterceptor> ints;
        private int idx;
        
        public InterceptorHandler(final Holder<Boolean> isUnlockRequired, final List<FiberContextSwitchInterceptor> ints) {
            this.isUnlockRequired = isUnlockRequired;
            this.ints = ints;
        }
        
        Tube invoke(final Tube next) {
            this.idx = 0;
            return this.execute(next);
        }
        
        @Override
        public Tube execute(final Tube next) {
            if (this.idx != this.ints.size()) {
                final FiberContextSwitchInterceptor interceptor = this.ints.get(this.idx++);
                return interceptor.execute(Fiber.this, next, (FiberContextSwitchInterceptor.Work<Tube, Tube>)this);
            }
            Fiber.this.next = next;
            if (Fiber.this.__doRun(this.isUnlockRequired, this.ints)) {
                return Fiber.PLACEHOLDER;
            }
            return Fiber.this.next;
        }
    }
    
    private static class PlaceholderTube extends AbstractTubeImpl
    {
        @Override
        public NextAction processRequest(final Packet request) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public NextAction processResponse(final Packet response) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public NextAction processException(final Throwable t) {
            return this.doThrow(t);
        }
        
        @Override
        public void preDestroy() {
        }
        
        @Override
        public PlaceholderTube copy(final TubeCloner cloner) {
            throw new UnsupportedOperationException();
        }
    }
    
    public interface CompletionCallback
    {
        void onCompletion(@NotNull final Packet p0);
        
        void onCompletion(@NotNull final Throwable p0);
    }
    
    public interface Listener
    {
        void fiberSuspended(final Fiber p0);
        
        void fiberResumed(final Fiber p0);
    }
}
