package sun.awt.dnd;

import java.util.Arrays;
import sun.awt.datatransfer.ToolkitThreadBlockedHandler;
import java.util.HashSet;
import sun.awt.AppContext;
import java.awt.AWTEvent;
import sun.awt.SunToolkit;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.Point;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.Component;
import java.io.InputStream;
import java.io.IOException;
import java.awt.datatransfer.UnsupportedFlavorException;
import sun.security.util.SecurityConstants;
import sun.awt.datatransfer.DataTransferer;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.InvalidDnDOperationException;
import sun.util.logging.PlatformLogger;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTarget;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.peer.DropTargetContextPeer;

public abstract class SunDropTargetContextPeer implements DropTargetContextPeer, Transferable
{
    public static final boolean DISPATCH_SYNC = true;
    private DropTarget currentDT;
    private DropTargetContext currentDTC;
    private long[] currentT;
    private int currentA;
    private int currentSA;
    private int currentDA;
    private int previousDA;
    private long nativeDragContext;
    private Transferable local;
    private boolean dragRejected;
    protected int dropStatus;
    protected boolean dropComplete;
    boolean dropInProcess;
    protected static final Object _globalLock;
    private static final PlatformLogger dndLog;
    protected static Transferable currentJVMLocalSourceTransferable;
    protected static final int STATUS_NONE = 0;
    protected static final int STATUS_WAIT = 1;
    protected static final int STATUS_ACCEPT = 2;
    protected static final int STATUS_REJECT = -1;
    
    public static void setCurrentJVMLocalSourceTransferable(final Transferable currentJVMLocalSourceTransferable) throws InvalidDnDOperationException {
        synchronized (SunDropTargetContextPeer._globalLock) {
            if (currentJVMLocalSourceTransferable != null && SunDropTargetContextPeer.currentJVMLocalSourceTransferable != null) {
                throw new InvalidDnDOperationException();
            }
            SunDropTargetContextPeer.currentJVMLocalSourceTransferable = currentJVMLocalSourceTransferable;
        }
    }
    
    private static Transferable getJVMLocalSourceTransferable() {
        return SunDropTargetContextPeer.currentJVMLocalSourceTransferable;
    }
    
    public SunDropTargetContextPeer() {
        this.dragRejected = false;
        this.dropStatus = 0;
        this.dropComplete = false;
        this.dropInProcess = false;
    }
    
    @Override
    public DropTarget getDropTarget() {
        return this.currentDT;
    }
    
    @Override
    public synchronized void setTargetActions(final int n) {
        this.currentA = (n & 0x40000003);
    }
    
    @Override
    public int getTargetActions() {
        return this.currentA;
    }
    
    @Override
    public Transferable getTransferable() {
        return this;
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        final Transferable local = this.local;
        if (local != null) {
            return local.getTransferDataFlavors();
        }
        return DataTransferer.getInstance().getFlavorsForFormatsAsArray(this.currentT, DataTransferer.adaptFlavorMap(this.currentDT.getFlavorMap()));
    }
    
    @Override
    public boolean isDataFlavorSupported(final DataFlavor dataFlavor) {
        final Transferable local = this.local;
        if (local != null) {
            return local.isDataFlavorSupported(dataFlavor);
        }
        return DataTransferer.getInstance().getFlavorsForFormats(this.currentT, DataTransferer.adaptFlavorMap(this.currentDT.getFlavorMap())).containsKey(dataFlavor);
    }
    
    @Override
    public Object getTransferData(final DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException, InvalidDnDOperationException {
        final SecurityManager securityManager = System.getSecurityManager();
        try {
            if (!this.dropInProcess && securityManager != null) {
                securityManager.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
            }
        }
        catch (final Exception ex) {
            final Thread currentThread = Thread.currentThread();
            currentThread.getUncaughtExceptionHandler().uncaughtException(currentThread, ex);
            return null;
        }
        final Transferable local = this.local;
        if (local != null) {
            return local.getTransferData(dataFlavor);
        }
        if (this.dropStatus != 2 || this.dropComplete) {
            throw new InvalidDnDOperationException("No drop current");
        }
        final Long n = DataTransferer.getInstance().getFlavorsForFormats(this.currentT, DataTransferer.adaptFlavorMap(this.currentDT.getFlavorMap())).get(dataFlavor);
        if (n == null) {
            throw new UnsupportedFlavorException(dataFlavor);
        }
        if (dataFlavor.isRepresentationClassRemote() && this.currentDA != 1073741824) {
            throw new InvalidDnDOperationException("only ACTION_LINK is permissable for transfer of java.rmi.Remote objects");
        }
        final long longValue = n;
        final Object nativeData = this.getNativeData(longValue);
        if (nativeData instanceof byte[]) {
            try {
                return DataTransferer.getInstance().translateBytes((byte[])nativeData, dataFlavor, longValue, this);
            }
            catch (final IOException ex2) {
                throw new InvalidDnDOperationException(ex2.getMessage());
            }
        }
        if (nativeData instanceof InputStream) {
            try {
                return DataTransferer.getInstance().translateStream((InputStream)nativeData, dataFlavor, longValue, this);
            }
            catch (final IOException ex3) {
                throw new InvalidDnDOperationException(ex3.getMessage());
            }
        }
        throw new IOException("no native data was transfered");
    }
    
    protected abstract Object getNativeData(final long p0) throws IOException;
    
    @Override
    public boolean isTransferableJVMLocal() {
        return this.local != null || getJVMLocalSourceTransferable() != null;
    }
    
    private int handleEnterMessage(final Component component, final int n, final int n2, final int n3, final int n4, final long[] array, final long n5) {
        return this.postDropTargetEvent(component, n, n2, n3, n4, array, n5, 504, true);
    }
    
    protected void processEnterMessage(final SunDropTargetEvent sunDropTargetEvent) {
        final Component component = (Component)sunDropTargetEvent.getSource();
        final DropTarget dropTarget = component.getDropTarget();
        final Point point = sunDropTargetEvent.getPoint();
        this.local = getJVMLocalSourceTransferable();
        if (this.currentDTC != null) {
            this.currentDTC.removeNotify();
            this.currentDTC = null;
        }
        if (component.isShowing() && dropTarget != null && dropTarget.isActive()) {
            this.currentDT = dropTarget;
            (this.currentDTC = this.currentDT.getDropTargetContext()).addNotify(this);
            this.currentA = dropTarget.getDefaultActions();
            try {
                dropTarget.dragEnter(new DropTargetDragEvent(this.currentDTC, point, this.currentDA, this.currentSA));
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                this.currentDA = 0;
            }
        }
        else {
            this.currentDT = null;
            this.currentDTC = null;
            this.currentDA = 0;
            this.currentSA = 0;
            this.currentA = 0;
        }
    }
    
    private void handleExitMessage(final Component component, final long n) {
        this.postDropTargetEvent(component, 0, 0, 0, 0, null, n, 505, true);
    }
    
    protected void processExitMessage(final SunDropTargetEvent sunDropTargetEvent) {
        final DropTarget dropTarget = ((Component)sunDropTargetEvent.getSource()).getDropTarget();
        if (dropTarget == null) {
            this.currentDT = null;
            this.currentT = null;
            if (this.currentDTC != null) {
                this.currentDTC.removeNotify();
            }
            this.currentDTC = null;
            return;
        }
        if (dropTarget != this.currentDT) {
            if (this.currentDTC != null) {
                this.currentDTC.removeNotify();
            }
            this.currentDT = dropTarget;
            (this.currentDTC = dropTarget.getDropTargetContext()).addNotify(this);
        }
        final DropTargetContext currentDTC = this.currentDTC;
        if (dropTarget.isActive()) {
            try {
                dropTarget.dragExit(new DropTargetEvent(currentDTC));
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
            finally {
                this.currentA = 0;
                this.currentSA = 0;
                this.currentDA = 0;
                this.currentDT = null;
                this.currentT = null;
                this.currentDTC.removeNotify();
                this.currentDTC = null;
                this.local = null;
                this.dragRejected = false;
            }
        }
    }
    
    private int handleMotionMessage(final Component component, final int n, final int n2, final int n3, final int n4, final long[] array, final long n5) {
        return this.postDropTargetEvent(component, n, n2, n3, n4, array, n5, 506, true);
    }
    
    protected void processMotionMessage(final SunDropTargetEvent sunDropTargetEvent, final boolean b) {
        final Component component = (Component)sunDropTargetEvent.getSource();
        final Point point = sunDropTargetEvent.getPoint();
        sunDropTargetEvent.getID();
        final DropTarget dropTarget = component.getDropTarget();
        if (component.isShowing() && dropTarget != null && dropTarget.isActive()) {
            if (this.currentDT != dropTarget) {
                if (this.currentDTC != null) {
                    this.currentDTC.removeNotify();
                }
                this.currentDT = dropTarget;
                this.currentDTC = null;
            }
            final DropTargetContext dropTargetContext = this.currentDT.getDropTargetContext();
            if (dropTargetContext != this.currentDTC) {
                if (this.currentDTC != null) {
                    this.currentDTC.removeNotify();
                }
                (this.currentDTC = dropTargetContext).addNotify(this);
            }
            this.currentA = this.currentDT.getDefaultActions();
            try {
                final DropTargetDragEvent dropTargetDragEvent = new DropTargetDragEvent(dropTargetContext, point, this.currentDA, this.currentSA);
                final DropTarget dropTarget2 = dropTarget;
                if (b) {
                    dropTarget2.dropActionChanged(dropTargetDragEvent);
                }
                else {
                    dropTarget2.dragOver(dropTargetDragEvent);
                }
                if (this.dragRejected) {
                    this.currentDA = 0;
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                this.currentDA = 0;
            }
        }
        else {
            this.currentDA = 0;
        }
    }
    
    private void handleDropMessage(final Component component, final int n, final int n2, final int n3, final int n4, final long[] array, final long n5) {
        this.postDropTargetEvent(component, n, n2, n3, n4, array, n5, 502, false);
    }
    
    protected void processDropMessage(final SunDropTargetEvent sunDropTargetEvent) {
        final Component component = (Component)sunDropTargetEvent.getSource();
        final Point point = sunDropTargetEvent.getPoint();
        final DropTarget dropTarget = component.getDropTarget();
        this.dropStatus = 1;
        this.dropComplete = false;
        if (component.isShowing() && dropTarget != null && dropTarget.isActive()) {
            final DropTargetContext dropTargetContext = dropTarget.getDropTargetContext();
            this.currentDT = dropTarget;
            if (this.currentDTC != null) {
                this.currentDTC.removeNotify();
            }
            (this.currentDTC = dropTargetContext).addNotify(this);
            this.currentA = dropTarget.getDefaultActions();
            synchronized (SunDropTargetContextPeer._globalLock) {
                final Transferable jvmLocalSourceTransferable = getJVMLocalSourceTransferable();
                this.local = jvmLocalSourceTransferable;
                if (jvmLocalSourceTransferable != null) {
                    setCurrentJVMLocalSourceTransferable(null);
                }
            }
            this.dropInProcess = true;
            try {
                dropTarget.drop(new DropTargetDropEvent(dropTargetContext, point, this.currentDA, this.currentSA, this.local != null));
            }
            finally {
                if (this.dropStatus == 1) {
                    this.rejectDrop();
                }
                else if (!this.dropComplete) {
                    this.dropComplete(false);
                }
                this.dropInProcess = false;
            }
        }
        else {
            this.rejectDrop();
        }
    }
    
    protected int postDropTargetEvent(final Component component, final int n, final int n2, final int n3, final int n4, final long[] array, final long n5, final int n6, final boolean b) {
        final AppContext targetToAppContext = SunToolkit.targetToAppContext(component);
        final EventDispatcher eventDispatcher = new EventDispatcher(this, n3, n4, array, n5, b);
        final SunDropTargetEvent sunDropTargetEvent = new SunDropTargetEvent(component, n6, n, n2, eventDispatcher);
        if (b) {
            DataTransferer.getInstance().getToolkitThreadBlockedHandler().lock();
        }
        SunToolkit.postEvent(targetToAppContext, sunDropTargetEvent);
        this.eventPosted(sunDropTargetEvent);
        if (b) {
            while (!eventDispatcher.isDone()) {
                DataTransferer.getInstance().getToolkitThreadBlockedHandler().enter();
            }
            DataTransferer.getInstance().getToolkitThreadBlockedHandler().unlock();
            return eventDispatcher.getReturnValue();
        }
        return 0;
    }
    
    @Override
    public synchronized void acceptDrag(final int n) {
        if (this.currentDT == null) {
            throw new InvalidDnDOperationException("No Drag pending");
        }
        this.currentDA = this.mapOperation(n);
        if (this.currentDA != 0) {
            this.dragRejected = false;
        }
    }
    
    @Override
    public synchronized void rejectDrag() {
        if (this.currentDT == null) {
            throw new InvalidDnDOperationException("No Drag pending");
        }
        this.currentDA = 0;
        this.dragRejected = true;
    }
    
    @Override
    public synchronized void acceptDrop(final int n) {
        if (n == 0) {
            throw new IllegalArgumentException("invalid acceptDrop() action");
        }
        if (this.dropStatus == 1 || this.dropStatus == 2) {
            final int mapOperation = this.mapOperation(n & this.currentSA);
            this.currentA = mapOperation;
            this.currentDA = mapOperation;
            this.dropStatus = 2;
            this.dropComplete = false;
            return;
        }
        throw new InvalidDnDOperationException("invalid acceptDrop()");
    }
    
    @Override
    public synchronized void rejectDrop() {
        if (this.dropStatus != 1) {
            throw new InvalidDnDOperationException("invalid rejectDrop()");
        }
        this.dropStatus = -1;
        this.currentDA = 0;
        this.dropComplete(false);
    }
    
    private int mapOperation(final int n) {
        final int[] array = { 2, 1, 1073741824 };
        int n2 = 0;
        for (int i = 0; i < array.length; ++i) {
            if ((n & array[i]) == array[i]) {
                n2 = array[i];
                break;
            }
        }
        return n2;
    }
    
    @Override
    public synchronized void dropComplete(final boolean b) {
        if (this.dropStatus == 0) {
            throw new InvalidDnDOperationException("No Drop pending");
        }
        if (this.currentDTC != null) {
            this.currentDTC.removeNotify();
        }
        this.currentDT = null;
        this.currentDTC = null;
        this.currentT = null;
        this.currentA = 0;
        synchronized (SunDropTargetContextPeer._globalLock) {
            SunDropTargetContextPeer.currentJVMLocalSourceTransferable = null;
        }
        this.dropStatus = 0;
        this.dropComplete = true;
        try {
            this.doDropDone(b, this.currentDA, this.local != null);
        }
        finally {
            this.currentDA = 0;
            this.nativeDragContext = 0L;
        }
    }
    
    protected abstract void doDropDone(final boolean p0, final int p1, final boolean p2);
    
    protected synchronized long getNativeDragContext() {
        return this.nativeDragContext;
    }
    
    protected void eventPosted(final SunDropTargetEvent sunDropTargetEvent) {
    }
    
    protected void eventProcessed(final SunDropTargetEvent sunDropTargetEvent, final int n, final boolean b) {
    }
    
    static {
        _globalLock = new Object();
        dndLog = PlatformLogger.getLogger("sun.awt.dnd.SunDropTargetContextPeer");
        SunDropTargetContextPeer.currentJVMLocalSourceTransferable = null;
    }
    
    protected static class EventDispatcher
    {
        private final SunDropTargetContextPeer peer;
        private final int dropAction;
        private final int actions;
        private final long[] formats;
        private long nativeCtxt;
        private final boolean dispatchType;
        private boolean dispatcherDone;
        private int returnValue;
        private final HashSet eventSet;
        static final ToolkitThreadBlockedHandler handler;
        
        EventDispatcher(final SunDropTargetContextPeer peer, final int dropAction, final int actions, final long[] array, final long nativeCtxt, final boolean dispatchType) {
            this.dispatcherDone = false;
            this.returnValue = 0;
            this.eventSet = new HashSet(3);
            this.peer = peer;
            this.nativeCtxt = nativeCtxt;
            this.dropAction = dropAction;
            this.actions = actions;
            this.formats = (long[])((null == array) ? null : Arrays.copyOf(array, array.length));
            this.dispatchType = dispatchType;
        }
        
        void dispatchEvent(final SunDropTargetEvent sunDropTargetEvent) {
            switch (sunDropTargetEvent.getID()) {
                case 504: {
                    this.dispatchEnterEvent(sunDropTargetEvent);
                    break;
                }
                case 506: {
                    this.dispatchMotionEvent(sunDropTargetEvent);
                    break;
                }
                case 505: {
                    this.dispatchExitEvent(sunDropTargetEvent);
                    break;
                }
                case 502: {
                    this.dispatchDropEvent(sunDropTargetEvent);
                    break;
                }
                default: {
                    throw new InvalidDnDOperationException();
                }
            }
        }
        
        private void dispatchEnterEvent(final SunDropTargetEvent sunDropTargetEvent) {
            synchronized (this.peer) {
                this.peer.previousDA = this.dropAction;
                this.peer.nativeDragContext = this.nativeCtxt;
                this.peer.currentT = this.formats;
                this.peer.currentSA = this.actions;
                this.peer.currentDA = this.dropAction;
                this.peer.dropStatus = 2;
                this.peer.dropComplete = false;
                try {
                    this.peer.processEnterMessage(sunDropTargetEvent);
                }
                finally {
                    this.peer.dropStatus = 0;
                }
                this.setReturnValue(this.peer.currentDA);
            }
        }
        
        private void dispatchMotionEvent(final SunDropTargetEvent sunDropTargetEvent) {
            synchronized (this.peer) {
                final boolean b = this.peer.previousDA != this.dropAction;
                this.peer.previousDA = this.dropAction;
                this.peer.nativeDragContext = this.nativeCtxt;
                this.peer.currentT = this.formats;
                this.peer.currentSA = this.actions;
                this.peer.currentDA = this.dropAction;
                this.peer.dropStatus = 2;
                this.peer.dropComplete = false;
                try {
                    this.peer.processMotionMessage(sunDropTargetEvent, b);
                }
                finally {
                    this.peer.dropStatus = 0;
                }
                this.setReturnValue(this.peer.currentDA);
            }
        }
        
        private void dispatchExitEvent(final SunDropTargetEvent sunDropTargetEvent) {
            synchronized (this.peer) {
                this.peer.nativeDragContext = this.nativeCtxt;
                this.peer.processExitMessage(sunDropTargetEvent);
            }
        }
        
        private void dispatchDropEvent(final SunDropTargetEvent sunDropTargetEvent) {
            synchronized (this.peer) {
                this.peer.nativeDragContext = this.nativeCtxt;
                this.peer.currentT = this.formats;
                this.peer.currentSA = this.actions;
                this.peer.currentDA = this.dropAction;
                this.peer.processDropMessage(sunDropTargetEvent);
            }
        }
        
        void setReturnValue(final int returnValue) {
            this.returnValue = returnValue;
        }
        
        int getReturnValue() {
            return this.returnValue;
        }
        
        boolean isDone() {
            return this.eventSet.isEmpty();
        }
        
        void registerEvent(final SunDropTargetEvent sunDropTargetEvent) {
            EventDispatcher.handler.lock();
            if (!this.eventSet.add(sunDropTargetEvent) && SunDropTargetContextPeer.dndLog.isLoggable(PlatformLogger.Level.FINE)) {
                SunDropTargetContextPeer.dndLog.fine("Event is already registered: " + sunDropTargetEvent);
            }
            EventDispatcher.handler.unlock();
        }
        
        void unregisterEvent(final SunDropTargetEvent sunDropTargetEvent) {
            EventDispatcher.handler.lock();
            try {
                if (!this.eventSet.remove(sunDropTargetEvent)) {
                    return;
                }
                if (this.eventSet.isEmpty()) {
                    if (!this.dispatcherDone && this.dispatchType) {
                        EventDispatcher.handler.exit();
                    }
                    this.dispatcherDone = true;
                }
            }
            finally {
                EventDispatcher.handler.unlock();
            }
            try {
                this.peer.eventProcessed(sunDropTargetEvent, this.returnValue, this.dispatcherDone);
            }
            finally {
                if (this.dispatcherDone) {
                    this.nativeCtxt = 0L;
                    this.peer.nativeDragContext = 0L;
                }
            }
        }
        
        public void unregisterAllEvents() {
            Object[] array = null;
            EventDispatcher.handler.lock();
            try {
                array = this.eventSet.toArray();
            }
            finally {
                EventDispatcher.handler.unlock();
            }
            if (array != null) {
                for (int i = 0; i < array.length; ++i) {
                    this.unregisterEvent((SunDropTargetEvent)array[i]);
                }
            }
        }
        
        static {
            handler = DataTransferer.getInstance().getToolkitThreadBlockedHandler();
        }
    }
}
