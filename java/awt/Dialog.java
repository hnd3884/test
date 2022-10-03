package java.awt;

import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.security.AccessControlException;
import java.io.ObjectInputStream;
import sun.security.util.SecurityConstants;
import java.util.List;
import sun.awt.util.IdentityLinkedList;
import java.util.Collection;
import java.util.Iterator;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.awt.event.InvocationEvent;
import sun.awt.AppContext;
import java.awt.event.ComponentEvent;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicLong;
import java.awt.peer.DialogPeer;
import sun.awt.SunToolkit;
import sun.awt.util.IdentityArrayList;

public class Dialog extends Window
{
    boolean resizable;
    boolean undecorated;
    private transient boolean initialized;
    public static final ModalityType DEFAULT_MODALITY_TYPE;
    boolean modal;
    ModalityType modalityType;
    static transient IdentityArrayList<Dialog> modalDialogs;
    transient IdentityArrayList<Window> blockedWindows;
    String title;
    private transient ModalEventFilter modalFilter;
    private transient volatile SecondaryLoop secondaryLoop;
    transient volatile boolean isInHide;
    transient volatile boolean isInDispose;
    private static final String base = "dialog";
    private static int nameCounter;
    private static final long serialVersionUID = 5920926903803293709L;
    
    public Dialog(final Frame frame) {
        this(frame, "", false);
    }
    
    public Dialog(final Frame frame, final boolean b) {
        this(frame, "", b);
    }
    
    public Dialog(final Frame frame, final String s) {
        this(frame, s, false);
    }
    
    public Dialog(final Frame frame, final String s, final boolean b) {
        this(frame, s, b ? Dialog.DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
    }
    
    public Dialog(final Frame frame, final String s, final boolean b, final GraphicsConfiguration graphicsConfiguration) {
        this(frame, s, b ? Dialog.DEFAULT_MODALITY_TYPE : ModalityType.MODELESS, graphicsConfiguration);
    }
    
    public Dialog(final Dialog dialog) {
        this(dialog, "", false);
    }
    
    public Dialog(final Dialog dialog, final String s) {
        this(dialog, s, false);
    }
    
    public Dialog(final Dialog dialog, final String s, final boolean b) {
        this(dialog, s, b ? Dialog.DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
    }
    
    public Dialog(final Dialog dialog, final String s, final boolean b, final GraphicsConfiguration graphicsConfiguration) {
        this(dialog, s, b ? Dialog.DEFAULT_MODALITY_TYPE : ModalityType.MODELESS, graphicsConfiguration);
    }
    
    public Dialog(final Window window) {
        this(window, "", ModalityType.MODELESS);
    }
    
    public Dialog(final Window window, final String s) {
        this(window, s, ModalityType.MODELESS);
    }
    
    public Dialog(final Window window, final ModalityType modalityType) {
        this(window, "", modalityType);
    }
    
    public Dialog(final Window window, final String title, final ModalityType modalityType) {
        super(window);
        this.resizable = true;
        this.undecorated = false;
        this.initialized = false;
        this.blockedWindows = new IdentityArrayList<Window>();
        this.isInHide = false;
        this.isInDispose = false;
        if (window != null && !(window instanceof Frame) && !(window instanceof Dialog)) {
            throw new IllegalArgumentException("Wrong parent window");
        }
        this.title = title;
        this.setModalityType(modalityType);
        SunToolkit.checkAndSetPolicy(this);
        this.initialized = true;
    }
    
    public Dialog(final Window window, final String title, final ModalityType modalityType, final GraphicsConfiguration graphicsConfiguration) {
        super(window, graphicsConfiguration);
        this.resizable = true;
        this.undecorated = false;
        this.initialized = false;
        this.blockedWindows = new IdentityArrayList<Window>();
        this.isInHide = false;
        this.isInDispose = false;
        if (window != null && !(window instanceof Frame) && !(window instanceof Dialog)) {
            throw new IllegalArgumentException("wrong owner window");
        }
        this.title = title;
        this.setModalityType(modalityType);
        SunToolkit.checkAndSetPolicy(this);
        this.initialized = true;
    }
    
    @Override
    String constructComponentName() {
        synchronized (Dialog.class) {
            return "dialog" + Dialog.nameCounter++;
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.parent != null && this.parent.getPeer() == null) {
                this.parent.addNotify();
            }
            if (this.peer == null) {
                this.peer = this.getToolkit().createDialog(this);
            }
            super.addNotify();
        }
    }
    
    public boolean isModal() {
        return this.isModal_NoClientCode();
    }
    
    final boolean isModal_NoClientCode() {
        return this.modalityType != ModalityType.MODELESS;
    }
    
    public void setModal(final boolean modal) {
        this.modal = modal;
        this.setModalityType(modal ? Dialog.DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
    }
    
    public ModalityType getModalityType() {
        return this.modalityType;
    }
    
    public void setModalityType(ModalityType modalityType) {
        if (modalityType == null) {
            modalityType = ModalityType.MODELESS;
        }
        if (!Toolkit.getDefaultToolkit().isModalityTypeSupported(modalityType)) {
            modalityType = ModalityType.MODELESS;
        }
        if (this.modalityType == modalityType) {
            return;
        }
        this.checkModalityPermission(modalityType);
        this.modalityType = modalityType;
        this.modal = (this.modalityType != ModalityType.MODELESS);
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String s) {
        final String title = this.title;
        synchronized (this) {
            this.title = s;
            final DialogPeer dialogPeer = (DialogPeer)this.peer;
            if (dialogPeer != null) {
                dialogPeer.setTitle(s);
            }
        }
        this.firePropertyChange("title", title, s);
    }
    
    private boolean conditionalShow(final Component component, final AtomicLong atomicLong) {
        this.closeSplashScreen();
        boolean b;
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                this.addNotify();
            }
            this.validateUnconditionally();
            if (this.visible) {
                this.toFront();
                b = false;
            }
            else {
                b = (this.visible = true);
                if (!this.isModal()) {
                    checkShouldBeBlocked(this);
                }
                else {
                    Dialog.modalDialogs.add(this);
                    this.modalShow();
                }
                if (component != null && atomicLong != null && this.isFocusable() && this.isEnabled() && !this.isModalBlocked()) {
                    atomicLong.set(Toolkit.getEventQueue().getMostRecentKeyEventTime());
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().enqueueKeyEvents(atomicLong.get(), component);
                }
                this.mixOnShowing();
                this.peer.setVisible(true);
                if (this.isModalBlocked()) {
                    this.modalBlocker.toFront();
                }
                this.setLocationByPlatform(false);
                for (int i = 0; i < this.ownedWindowList.size(); ++i) {
                    final Window window = this.ownedWindowList.elementAt(i).get();
                    if (window != null && window.showWithParent) {
                        window.show();
                        window.showWithParent = false;
                    }
                }
                Window.updateChildFocusableWindowState(this);
                this.createHierarchyEvents(1400, this, this.parent, 4L, Toolkit.enabledOnToolkit(32768L));
                if (this.componentListener != null || (this.eventMask & 0x1L) != 0x0L || Toolkit.enabledOnToolkit(1L)) {
                    Toolkit.getEventQueue().postEvent(new ComponentEvent(this, 102));
                }
            }
        }
        if (b && (this.state & 0x1) == 0x0) {
            this.postWindowEvent(200);
            this.state |= 0x1;
        }
        return b;
    }
    
    @Override
    public void setVisible(final boolean visible) {
        super.setVisible(visible);
    }
    
    @Deprecated
    @Override
    public void show() {
        if (!this.initialized) {
            throw new IllegalStateException("The dialog component has not been initialized properly");
        }
        this.beforeFirstShow = false;
        if (!this.isModal()) {
            this.conditionalShow(null, null);
        }
        else {
            final AppContext appContext = AppContext.getAppContext();
            final AtomicLong atomicLong = new AtomicLong();
            Component mostRecentFocusOwner = null;
            try {
                mostRecentFocusOwner = this.getMostRecentFocusOwner();
                if (this.conditionalShow(mostRecentFocusOwner, atomicLong)) {
                    this.modalFilter = ModalEventFilter.createFilterForDialog(this);
                    final Conditional conditional = new Conditional() {
                        @Override
                        public boolean evaluate() {
                            return Dialog.this.windowClosingException == null;
                        }
                    };
                    if (this.modalityType == ModalityType.TOOLKIT_MODAL) {
                        for (final AppContext appContext2 : AppContext.getAppContexts()) {
                            if (appContext2 == appContext) {
                                continue;
                            }
                            final EventQueue eventQueue = (EventQueue)appContext2.get(AppContext.EVENT_QUEUE_KEY);
                            eventQueue.postEvent(new InvocationEvent(this, new Runnable() {
                                @Override
                                public void run() {
                                }
                            }));
                            eventQueue.getDispatchThread().addEventFilter(this.modalFilter);
                        }
                    }
                    this.modalityPushed();
                    try {
                        this.secondaryLoop = AccessController.doPrivileged((PrivilegedAction<EventQueue>)new PrivilegedAction<EventQueue>() {
                            @Override
                            public EventQueue run() {
                                return Toolkit.getDefaultToolkit().getSystemEventQueue();
                            }
                        }).createSecondaryLoop(conditional, this.modalFilter, 0L);
                        if (!this.secondaryLoop.enter()) {
                            this.secondaryLoop = null;
                        }
                    }
                    finally {
                        this.modalityPopped();
                    }
                    if (this.modalityType == ModalityType.TOOLKIT_MODAL) {
                        for (final AppContext appContext3 : AppContext.getAppContexts()) {
                            if (appContext3 == appContext) {
                                continue;
                            }
                            ((EventQueue)appContext3.get(AppContext.EVENT_QUEUE_KEY)).getDispatchThread().removeEventFilter(this.modalFilter);
                        }
                    }
                    if (this.windowClosingException != null) {
                        this.windowClosingException.fillInStackTrace();
                        throw this.windowClosingException;
                    }
                }
            }
            finally {
                if (mostRecentFocusOwner != null) {
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().dequeueKeyEvents(atomicLong.get(), mostRecentFocusOwner);
                }
            }
        }
    }
    
    final void modalityPushed() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (defaultToolkit instanceof SunToolkit) {
            ((SunToolkit)defaultToolkit).notifyModalityPushed(this);
        }
    }
    
    final void modalityPopped() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (defaultToolkit instanceof SunToolkit) {
            ((SunToolkit)defaultToolkit).notifyModalityPopped(this);
        }
    }
    
    void interruptBlocking() {
        if (this.isModal()) {
            this.disposeImpl();
        }
        else if (this.windowClosingException != null) {
            this.windowClosingException.fillInStackTrace();
            this.windowClosingException.printStackTrace();
            this.windowClosingException = null;
        }
    }
    
    private void hideAndDisposePreHandler() {
        this.isInHide = true;
        synchronized (this.getTreeLock()) {
            if (this.secondaryLoop != null) {
                this.modalHide();
                if (this.modalFilter != null) {
                    this.modalFilter.disable();
                }
                Dialog.modalDialogs.remove(this);
            }
        }
    }
    
    private void hideAndDisposeHandler() {
        if (this.secondaryLoop != null) {
            this.secondaryLoop.exit();
            this.secondaryLoop = null;
        }
        this.isInHide = false;
    }
    
    @Deprecated
    @Override
    public void hide() {
        this.hideAndDisposePreHandler();
        super.hide();
        if (!this.isInDispose) {
            this.hideAndDisposeHandler();
        }
    }
    
    @Override
    void doDispose() {
        this.isInDispose = true;
        super.doDispose();
        this.hideAndDisposeHandler();
        this.isInDispose = false;
    }
    
    @Override
    public void toBack() {
        super.toBack();
        if (this.visible) {
            synchronized (this.getTreeLock()) {
                final Iterator<Object> iterator = this.blockedWindows.iterator();
                while (iterator.hasNext()) {
                    iterator.next().toBack_NoClientCode();
                }
            }
        }
    }
    
    public boolean isResizable() {
        return this.resizable;
    }
    
    public void setResizable(final boolean b) {
        boolean b2 = false;
        synchronized (this) {
            this.resizable = b;
            final DialogPeer dialogPeer = (DialogPeer)this.peer;
            if (dialogPeer != null) {
                dialogPeer.setResizable(b);
                b2 = true;
            }
        }
        if (b2) {
            this.invalidateIfValid();
        }
    }
    
    public void setUndecorated(final boolean undecorated) {
        synchronized (this.getTreeLock()) {
            if (this.isDisplayable()) {
                throw new IllegalComponentStateException("The dialog is displayable.");
            }
            if (!undecorated) {
                if (this.getOpacity() < 1.0f) {
                    throw new IllegalComponentStateException("The dialog is not opaque");
                }
                if (this.getShape() != null) {
                    throw new IllegalComponentStateException("The dialog does not have a default shape");
                }
                final Color background = this.getBackground();
                if (background != null && background.getAlpha() < 255) {
                    throw new IllegalComponentStateException("The dialog background color is not opaque");
                }
            }
            this.undecorated = undecorated;
        }
    }
    
    public boolean isUndecorated() {
        return this.undecorated;
    }
    
    @Override
    public void setOpacity(final float opacity) {
        synchronized (this.getTreeLock()) {
            if (opacity < 1.0f && !this.isUndecorated()) {
                throw new IllegalComponentStateException("The dialog is decorated");
            }
            super.setOpacity(opacity);
        }
    }
    
    @Override
    public void setShape(final Shape shape) {
        synchronized (this.getTreeLock()) {
            if (shape != null && !this.isUndecorated()) {
                throw new IllegalComponentStateException("The dialog is decorated");
            }
            super.setShape(shape);
        }
    }
    
    @Override
    public void setBackground(final Color background) {
        synchronized (this.getTreeLock()) {
            if (background != null && background.getAlpha() < 255 && !this.isUndecorated()) {
                throw new IllegalComponentStateException("The dialog is decorated");
            }
            super.setBackground(background);
        }
    }
    
    @Override
    protected String paramString() {
        String s = super.paramString() + "," + this.modalityType;
        if (this.title != null) {
            s = s + ",title=" + this.title;
        }
        return s;
    }
    
    private static native void initIDs();
    
    void modalShow() {
        final IdentityArrayList list = new IdentityArrayList();
        for (final Dialog dialog : Dialog.modalDialogs) {
            if (dialog.shouldBlock(this)) {
                Window owner_NoClientCode;
                for (owner_NoClientCode = dialog; owner_NoClientCode != null && owner_NoClientCode != this; owner_NoClientCode = owner_NoClientCode.getOwner_NoClientCode()) {}
                if (owner_NoClientCode != this && this.shouldBlock(dialog) && this.modalityType.compareTo(dialog.getModalityType()) >= 0) {
                    continue;
                }
                list.add(dialog);
            }
        }
        for (int i = 0; i < list.size(); ++i) {
            final Dialog dialog2 = list.get(i);
            if (dialog2.isModalBlocked()) {
                final Dialog modalBlocker = dialog2.getModalBlocker();
                if (!list.contains(modalBlocker)) {
                    list.add(i + 1, modalBlocker);
                }
            }
        }
        if (list.size() > 0) {
            ((Dialog)list.get(0)).blockWindow(this);
        }
        final IdentityArrayList list2 = new IdentityArrayList(list);
        for (int j = 0; j < list2.size(); ++j) {
            final Window[] ownedWindows_NoClientCode = ((Window)list2.get(j)).getOwnedWindows_NoClientCode();
            for (int length = ownedWindows_NoClientCode.length, k = 0; k < length; ++k) {
                list2.add((Object)ownedWindows_NoClientCode[k]);
            }
        }
        final IdentityLinkedList list3 = new IdentityLinkedList();
        for (final Window window : Window.getAllUnblockedWindows()) {
            if (this.shouldBlock(window) && !list2.contains(window)) {
                if (window instanceof Dialog && ((Dialog)window).isModal_NoClientCode()) {
                    final Dialog dialog3 = (Dialog)window;
                    if (dialog3.shouldBlock(this) && Dialog.modalDialogs.indexOf(dialog3) > Dialog.modalDialogs.indexOf(this)) {
                        continue;
                    }
                }
                list3.add(window);
            }
        }
        this.blockWindows(list3);
        if (!this.isModalBlocked()) {
            this.updateChildrenBlocking();
        }
    }
    
    void modalHide() {
        final IdentityArrayList list = new IdentityArrayList();
        final int size = this.blockedWindows.size();
        for (int i = 0; i < size; ++i) {
            final Window window = this.blockedWindows.get(0);
            list.add(window);
            this.unblockWindow(window);
        }
        for (int j = 0; j < size; ++j) {
            final Window window2 = list.get(j);
            if (window2 instanceof Dialog && ((Dialog)window2).isModal_NoClientCode()) {
                ((Dialog)window2).modalShow();
            }
            else {
                checkShouldBeBlocked(window2);
            }
        }
    }
    
    boolean shouldBlock(final Window window) {
        if (!this.isVisible_NoClientCode() || (!window.isVisible_NoClientCode() && !window.isInShow) || this.isInHide || window == this || !this.isModal_NoClientCode()) {
            return false;
        }
        if (window instanceof Dialog && ((Dialog)window).isInHide) {
            return false;
        }
        for (Dialog modalBlocker = this; modalBlocker != null; modalBlocker = modalBlocker.getModalBlocker()) {
            Container parent_NoClientCode;
            for (parent_NoClientCode = window; parent_NoClientCode != null && parent_NoClientCode != modalBlocker; parent_NoClientCode = parent_NoClientCode.getParent_NoClientCode()) {}
            if (parent_NoClientCode == modalBlocker) {
                return false;
            }
        }
        switch (this.modalityType) {
            case MODELESS: {
                return false;
            }
            case DOCUMENT_MODAL: {
                if (window.isModalExcluded(ModalExclusionType.APPLICATION_EXCLUDE)) {
                    Container parent_NoClientCode2;
                    for (parent_NoClientCode2 = this; parent_NoClientCode2 != null && parent_NoClientCode2 != window; parent_NoClientCode2 = parent_NoClientCode2.getParent_NoClientCode()) {}
                    return parent_NoClientCode2 == window;
                }
                return this.getDocumentRoot() == window.getDocumentRoot();
            }
            case APPLICATION_MODAL: {
                return !window.isModalExcluded(ModalExclusionType.APPLICATION_EXCLUDE) && this.appContext == window.appContext;
            }
            case TOOLKIT_MODAL: {
                return !window.isModalExcluded(ModalExclusionType.TOOLKIT_EXCLUDE);
            }
            default: {
                return false;
            }
        }
    }
    
    void blockWindow(final Window window) {
        if (!window.isModalBlocked()) {
            window.setModalBlocked(this, true, true);
            this.blockedWindows.add(window);
        }
    }
    
    void blockWindows(final List<Window> list) {
        final DialogPeer dialogPeer = (DialogPeer)this.peer;
        if (dialogPeer == null) {
            return;
        }
        final Iterator<Window> iterator = list.iterator();
        while (iterator.hasNext()) {
            final Window window = iterator.next();
            if (!window.isModalBlocked()) {
                window.setModalBlocked(this, true, false);
            }
            else {
                iterator.remove();
            }
        }
        dialogPeer.blockWindows(list);
        this.blockedWindows.addAll(list);
    }
    
    void unblockWindow(final Window window) {
        if (window.isModalBlocked() && this.blockedWindows.contains(window)) {
            this.blockedWindows.remove(window);
            window.setModalBlocked(this, false, true);
        }
    }
    
    static void checkShouldBeBlocked(final Window window) {
        synchronized (window.getTreeLock()) {
            for (int i = 0; i < Dialog.modalDialogs.size(); ++i) {
                final Dialog dialog = Dialog.modalDialogs.get(i);
                if (dialog.shouldBlock(window)) {
                    dialog.blockWindow(window);
                    break;
                }
            }
        }
    }
    
    private void checkModalityPermission(final ModalityType modalityType) {
        if (modalityType == ModalityType.TOOLKIT_MODAL) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkPermission(SecurityConstants.AWT.TOOLKIT_MODALITY_PERMISSION);
            }
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        GraphicsEnvironment.checkHeadless();
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        ModalityType default_MODALITY_TYPE = (ModalityType)fields.get("modalityType", null);
        try {
            this.checkModalityPermission(default_MODALITY_TYPE);
        }
        catch (final AccessControlException ex) {
            default_MODALITY_TYPE = Dialog.DEFAULT_MODALITY_TYPE;
        }
        if (default_MODALITY_TYPE == null) {
            this.setModal(this.modal = fields.get("modal", false));
        }
        else {
            this.modalityType = default_MODALITY_TYPE;
        }
        this.resizable = fields.get("resizable", true);
        this.undecorated = fields.get("undecorated", false);
        this.title = (String)fields.get("title", "");
        this.blockedWindows = new IdentityArrayList<Window>();
        SunToolkit.checkAndSetPolicy(this);
        this.initialized = true;
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleAWTDialog();
        }
        return this.accessibleContext;
    }
    
    static {
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        DEFAULT_MODALITY_TYPE = ModalityType.APPLICATION_MODAL;
        Dialog.modalDialogs = new IdentityArrayList<Dialog>();
        Dialog.nameCounter = 0;
    }
    
    public enum ModalityType
    {
        MODELESS, 
        DOCUMENT_MODAL, 
        APPLICATION_MODAL, 
        TOOLKIT_MODAL;
    }
    
    public enum ModalExclusionType
    {
        NO_EXCLUDE, 
        APPLICATION_EXCLUDE, 
        TOOLKIT_EXCLUDE;
    }
    
    protected class AccessibleAWTDialog extends AccessibleAWTWindow
    {
        private static final long serialVersionUID = 4837230331833941201L;
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.DIALOG;
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (Dialog.this.getFocusOwner() != null) {
                accessibleStateSet.add(AccessibleState.ACTIVE);
            }
            if (Dialog.this.isModal()) {
                accessibleStateSet.add(AccessibleState.MODAL);
            }
            if (Dialog.this.isResizable()) {
                accessibleStateSet.add(AccessibleState.RESIZABLE);
            }
            return accessibleStateSet;
        }
    }
}
