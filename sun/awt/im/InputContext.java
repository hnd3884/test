package sun.awt.im;

import java.util.prefs.BackingStoreException;
import java.security.AccessController;
import java.util.prefs.Preferences;
import java.security.PrivilegedAction;
import java.awt.event.WindowEvent;
import java.awt.event.ComponentEvent;
import java.awt.Frame;
import java.text.MessageFormat;
import java.awt.Toolkit;
import sun.awt.SunToolkit;
import java.util.Iterator;
import java.awt.EventQueue;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.FocusEvent;
import java.awt.event.InputMethodEvent;
import java.awt.AWTEvent;
import java.util.Locale;
import java.awt.AWTKeyStroke;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.Component;
import java.util.HashMap;
import java.awt.im.spi.InputMethod;
import sun.util.logging.PlatformLogger;
import java.awt.event.WindowListener;
import java.awt.event.ComponentListener;

public class InputContext extends java.awt.im.InputContext implements ComponentListener, WindowListener
{
    private static final PlatformLogger log;
    private InputMethodLocator inputMethodLocator;
    private InputMethod inputMethod;
    private boolean inputMethodCreationFailed;
    private HashMap<InputMethodLocator, InputMethod> usedInputMethods;
    private Component currentClientComponent;
    private Component awtFocussedComponent;
    private boolean isInputMethodActive;
    private Character.Subset[] characterSubsets;
    private boolean compositionAreaHidden;
    private static InputContext inputMethodWindowContext;
    private static InputMethod previousInputMethod;
    private boolean clientWindowNotificationEnabled;
    private Window clientWindowListened;
    private Rectangle clientWindowLocation;
    private HashMap<InputMethod, Boolean> perInputMethodState;
    private static AWTKeyStroke inputMethodSelectionKey;
    private static boolean inputMethodSelectionKeyInitialized;
    private static final String inputMethodSelectionKeyPath = "/java/awt/im/selectionKey";
    private static final String inputMethodSelectionKeyCodeName = "keyCode";
    private static final String inputMethodSelectionKeyModifiersName = "modifiers";
    
    protected InputContext() {
        this.characterSubsets = null;
        this.compositionAreaHidden = false;
        this.clientWindowNotificationEnabled = false;
        this.clientWindowLocation = null;
        final InputMethodManager instance = InputMethodManager.getInstance();
        synchronized (InputContext.class) {
            if (!InputContext.inputMethodSelectionKeyInitialized) {
                InputContext.inputMethodSelectionKeyInitialized = true;
                if (instance.hasMultipleInputMethods()) {
                    this.initializeInputMethodSelectionKey();
                }
            }
        }
        this.selectInputMethod(instance.getDefaultKeyboardLocale());
    }
    
    @Override
    public synchronized boolean selectInputMethod(final Locale locale) {
        if (locale == null) {
            throw new NullPointerException();
        }
        if (this.inputMethod != null) {
            if (this.inputMethod.setLocale(locale)) {
                return true;
            }
        }
        else if (this.inputMethodLocator != null && this.inputMethodLocator.isLocaleAvailable(locale)) {
            this.inputMethodLocator = this.inputMethodLocator.deriveLocator(locale);
            return true;
        }
        final InputMethodLocator inputMethod = InputMethodManager.getInstance().findInputMethod(locale);
        if (inputMethod != null) {
            this.changeInputMethod(inputMethod);
            return true;
        }
        if (this.inputMethod == null && this.inputMethodLocator != null) {
            this.inputMethod = this.getInputMethod();
            if (this.inputMethod != null) {
                return this.inputMethod.setLocale(locale);
            }
        }
        return false;
    }
    
    @Override
    public Locale getLocale() {
        if (this.inputMethod != null) {
            return this.inputMethod.getLocale();
        }
        if (this.inputMethodLocator != null) {
            return this.inputMethodLocator.getLocale();
        }
        return null;
    }
    
    @Override
    public void setCharacterSubsets(final Character.Subset[] characterSubsets) {
        if (characterSubsets == null) {
            this.characterSubsets = null;
        }
        else {
            System.arraycopy(characterSubsets, 0, this.characterSubsets = new Character.Subset[characterSubsets.length], 0, this.characterSubsets.length);
        }
        if (this.inputMethod != null) {
            this.inputMethod.setCharacterSubsets(characterSubsets);
        }
    }
    
    @Override
    public synchronized void reconvert() {
        final InputMethod inputMethod = this.getInputMethod();
        if (inputMethod == null) {
            throw new UnsupportedOperationException();
        }
        inputMethod.reconvert();
    }
    
    @Override
    public void dispatchEvent(final AWTEvent awtEvent) {
        if (awtEvent instanceof InputMethodEvent) {
            return;
        }
        if (awtEvent instanceof FocusEvent) {
            final Component oppositeComponent = ((FocusEvent)awtEvent).getOppositeComponent();
            if (oppositeComponent != null && getComponentWindow(oppositeComponent) instanceof InputMethodWindow && oppositeComponent.getInputContext() == this) {
                return;
            }
        }
        final InputMethod inputMethod = this.getInputMethod();
        switch (awtEvent.getID()) {
            case 1004: {
                this.focusGained((Component)awtEvent.getSource());
                return;
            }
            case 1005: {
                this.focusLost((Component)awtEvent.getSource(), ((FocusEvent)awtEvent).isTemporary());
                return;
            }
            case 401: {
                if (this.checkInputMethodSelectionKey((KeyEvent)awtEvent)) {
                    InputMethodManager.getInstance().notifyChangeRequestByHotKey((Component)awtEvent.getSource());
                    return;
                }
                break;
            }
        }
        if (inputMethod != null && awtEvent instanceof InputEvent) {
            inputMethod.dispatchEvent(awtEvent);
        }
    }
    
    private void focusGained(final Component awtFocussedComponent) {
        synchronized (awtFocussedComponent.getTreeLock()) {
            synchronized (this) {
                if (!"sun.awt.im.CompositionArea".equals(awtFocussedComponent.getClass().getName())) {
                    if (!(getComponentWindow(awtFocussedComponent) instanceof InputMethodWindow)) {
                        if (!awtFocussedComponent.isDisplayable()) {
                            return;
                        }
                        if (this.inputMethod != null && this.currentClientComponent != null && this.currentClientComponent != awtFocussedComponent) {
                            if (!this.isInputMethodActive) {
                                this.activateInputMethod(false);
                            }
                            this.endComposition();
                            this.deactivateInputMethod(false);
                        }
                        this.currentClientComponent = awtFocussedComponent;
                    }
                }
                this.awtFocussedComponent = awtFocussedComponent;
                if (this.inputMethod instanceof InputMethodAdapter) {
                    ((InputMethodAdapter)this.inputMethod).setAWTFocussedComponent(awtFocussedComponent);
                }
                if (!this.isInputMethodActive) {
                    this.activateInputMethod(true);
                }
                final InputMethodContext inputMethodContext = (InputMethodContext)this;
                if (!inputMethodContext.isCompositionAreaVisible()) {
                    if (awtFocussedComponent.getInputMethodRequests() != null && inputMethodContext.useBelowTheSpotInput()) {
                        inputMethodContext.setCompositionAreaUndecorated(true);
                    }
                    else {
                        inputMethodContext.setCompositionAreaUndecorated(false);
                    }
                }
                if (this.compositionAreaHidden) {
                    ((InputMethodContext)this).setCompositionAreaVisible(true);
                    this.compositionAreaHidden = false;
                }
            }
        }
    }
    
    private void activateInputMethod(final boolean b) {
        if (InputContext.inputMethodWindowContext != null && InputContext.inputMethodWindowContext != this && InputContext.inputMethodWindowContext.inputMethodLocator != null && !InputContext.inputMethodWindowContext.inputMethodLocator.sameInputMethod(this.inputMethodLocator) && InputContext.inputMethodWindowContext.inputMethod != null) {
            InputContext.inputMethodWindowContext.inputMethod.hideWindows();
        }
        InputContext.inputMethodWindowContext = this;
        if (this.inputMethod != null) {
            if (InputContext.previousInputMethod != this.inputMethod && InputContext.previousInputMethod instanceof InputMethodAdapter) {
                ((InputMethodAdapter)InputContext.previousInputMethod).stopListening();
            }
            InputContext.previousInputMethod = null;
            if (InputContext.log.isLoggable(PlatformLogger.Level.FINE)) {
                InputContext.log.fine("Current client component " + this.currentClientComponent);
            }
            if (this.inputMethod instanceof InputMethodAdapter) {
                ((InputMethodAdapter)this.inputMethod).setClientComponent(this.currentClientComponent);
            }
            this.inputMethod.activate();
            this.isInputMethodActive = true;
            if (this.perInputMethodState != null) {
                final Boolean b2 = this.perInputMethodState.remove(this.inputMethod);
                if (b2 != null) {
                    this.clientWindowNotificationEnabled = b2;
                }
            }
            if (this.clientWindowNotificationEnabled) {
                if (!this.addedClientWindowListeners()) {
                    this.addClientWindowListeners();
                }
                synchronized (this) {
                    if (this.clientWindowListened != null) {
                        this.notifyClientWindowChange(this.clientWindowListened);
                    }
                }
            }
            else if (this.addedClientWindowListeners()) {
                this.removeClientWindowListeners();
            }
        }
        InputMethodManager.getInstance().setInputContext(this);
        ((InputMethodContext)this).grabCompositionArea(b);
    }
    
    static Window getComponentWindow(Component parent) {
        while (parent != null) {
            if (parent instanceof Window) {
                return (Window)parent;
            }
            parent = parent.getParent();
        }
        return null;
    }
    
    private void focusLost(final Component component, final boolean b) {
        synchronized (component.getTreeLock()) {
            synchronized (this) {
                if (this.isInputMethodActive) {
                    this.deactivateInputMethod(b);
                }
                this.awtFocussedComponent = null;
                if (this.inputMethod instanceof InputMethodAdapter) {
                    ((InputMethodAdapter)this.inputMethod).setAWTFocussedComponent(null);
                }
                final InputMethodContext inputMethodContext = (InputMethodContext)this;
                if (inputMethodContext.isCompositionAreaVisible()) {
                    inputMethodContext.setCompositionAreaVisible(false);
                    this.compositionAreaHidden = true;
                }
            }
        }
    }
    
    private boolean checkInputMethodSelectionKey(final KeyEvent keyEvent) {
        return InputContext.inputMethodSelectionKey != null && InputContext.inputMethodSelectionKey.equals(AWTKeyStroke.getAWTKeyStrokeForEvent(keyEvent));
    }
    
    private void deactivateInputMethod(final boolean b) {
        InputMethodManager.getInstance().setInputContext(null);
        if (this.inputMethod != null) {
            this.isInputMethodActive = false;
            this.inputMethod.deactivate(b);
            InputContext.previousInputMethod = this.inputMethod;
        }
    }
    
    synchronized void changeInputMethod(InputMethodLocator deriveLocator) {
        if (this.inputMethodLocator == null) {
            this.inputMethodLocator = deriveLocator;
            this.inputMethodCreationFailed = false;
            return;
        }
        if (this.inputMethodLocator.sameInputMethod(deriveLocator)) {
            final Locale locale = deriveLocator.getLocale();
            if (locale != null && this.inputMethodLocator.getLocale() != locale) {
                if (this.inputMethod != null) {
                    this.inputMethod.setLocale(locale);
                }
                this.inputMethodLocator = deriveLocator;
            }
            return;
        }
        Locale locale2 = this.inputMethodLocator.getLocale();
        final boolean isInputMethodActive = this.isInputMethodActive;
        boolean b = false;
        boolean compositionEnabled = false;
        if (this.inputMethod != null) {
            try {
                compositionEnabled = this.inputMethod.isCompositionEnabled();
                b = true;
            }
            catch (final UnsupportedOperationException ex) {}
            if (this.currentClientComponent != null) {
                if (!this.isInputMethodActive) {
                    this.activateInputMethod(false);
                }
                this.endComposition();
                this.deactivateInputMethod(false);
                if (this.inputMethod instanceof InputMethodAdapter) {
                    ((InputMethodAdapter)this.inputMethod).setClientComponent(null);
                }
            }
            locale2 = this.inputMethod.getLocale();
            if (this.usedInputMethods == null) {
                this.usedInputMethods = new HashMap<InputMethodLocator, InputMethod>(5);
            }
            if (this.perInputMethodState == null) {
                this.perInputMethodState = new HashMap<InputMethod, Boolean>(5);
            }
            this.usedInputMethods.put(this.inputMethodLocator.deriveLocator(null), this.inputMethod);
            this.perInputMethodState.put(this.inputMethod, this.clientWindowNotificationEnabled);
            this.enableClientWindowNotification(this.inputMethod, false);
            if (this == InputContext.inputMethodWindowContext) {
                this.inputMethod.hideWindows();
                InputContext.inputMethodWindowContext = null;
            }
            this.inputMethodLocator = null;
            this.inputMethod = null;
            this.inputMethodCreationFailed = false;
        }
        if (deriveLocator.getLocale() == null && locale2 != null && deriveLocator.isLocaleAvailable(locale2)) {
            deriveLocator = deriveLocator.deriveLocator(locale2);
        }
        this.inputMethodLocator = deriveLocator;
        this.inputMethodCreationFailed = false;
        if (isInputMethodActive) {
            this.inputMethod = this.getInputMethodInstance();
            if (this.inputMethod instanceof InputMethodAdapter) {
                ((InputMethodAdapter)this.inputMethod).setAWTFocussedComponent(this.awtFocussedComponent);
            }
            this.activateInputMethod(true);
        }
        if (b) {
            this.inputMethod = this.getInputMethod();
            if (this.inputMethod != null) {
                try {
                    this.inputMethod.setCompositionEnabled(compositionEnabled);
                }
                catch (final UnsupportedOperationException ex2) {}
            }
        }
    }
    
    Component getClientComponent() {
        return this.currentClientComponent;
    }
    
    @Override
    public synchronized void removeNotify(final Component component) {
        if (component == null) {
            throw new NullPointerException();
        }
        if (this.inputMethod == null) {
            if (component == this.currentClientComponent) {
                this.currentClientComponent = null;
            }
            return;
        }
        if (component == this.awtFocussedComponent) {
            this.focusLost(component, false);
        }
        if (component == this.currentClientComponent) {
            if (this.isInputMethodActive) {
                this.deactivateInputMethod(false);
            }
            this.inputMethod.removeNotify();
            if (this.clientWindowNotificationEnabled && this.addedClientWindowListeners()) {
                this.removeClientWindowListeners();
            }
            this.currentClientComponent = null;
            if (this.inputMethod instanceof InputMethodAdapter) {
                ((InputMethodAdapter)this.inputMethod).setClientComponent(null);
            }
            if (EventQueue.isDispatchThread()) {
                ((InputMethodContext)this).releaseCompositionArea();
            }
            else {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ((InputMethodContext)InputContext.this).releaseCompositionArea();
                    }
                });
            }
        }
    }
    
    @Override
    public synchronized void dispose() {
        if (this.currentClientComponent != null) {
            throw new IllegalStateException("Can't dispose InputContext while it's active");
        }
        if (this.inputMethod != null) {
            if (this == InputContext.inputMethodWindowContext) {
                this.inputMethod.hideWindows();
                InputContext.inputMethodWindowContext = null;
            }
            if (this.inputMethod == InputContext.previousInputMethod) {
                InputContext.previousInputMethod = null;
            }
            if (this.clientWindowNotificationEnabled) {
                if (this.addedClientWindowListeners()) {
                    this.removeClientWindowListeners();
                }
                this.clientWindowNotificationEnabled = false;
            }
            this.inputMethod.dispose();
            if (this.clientWindowNotificationEnabled) {
                this.enableClientWindowNotification(this.inputMethod, false);
            }
            this.inputMethod = null;
        }
        this.inputMethodLocator = null;
        if (this.usedInputMethods != null && !this.usedInputMethods.isEmpty()) {
            final Iterator<InputMethod> iterator = this.usedInputMethods.values().iterator();
            this.usedInputMethods = null;
            while (iterator.hasNext()) {
                iterator.next().dispose();
            }
        }
        this.clientWindowNotificationEnabled = false;
        this.clientWindowListened = null;
        this.perInputMethodState = null;
    }
    
    @Override
    public synchronized Object getInputMethodControlObject() {
        final InputMethod inputMethod = this.getInputMethod();
        if (inputMethod != null) {
            return inputMethod.getControlObject();
        }
        return null;
    }
    
    @Override
    public void setCompositionEnabled(final boolean compositionEnabled) {
        final InputMethod inputMethod = this.getInputMethod();
        if (inputMethod == null) {
            throw new UnsupportedOperationException();
        }
        inputMethod.setCompositionEnabled(compositionEnabled);
    }
    
    @Override
    public boolean isCompositionEnabled() {
        final InputMethod inputMethod = this.getInputMethod();
        if (inputMethod == null) {
            throw new UnsupportedOperationException();
        }
        return inputMethod.isCompositionEnabled();
    }
    
    public String getInputMethodInfo() {
        final InputMethod inputMethod = this.getInputMethod();
        if (inputMethod == null) {
            throw new UnsupportedOperationException("Null input method");
        }
        String s = null;
        if (inputMethod instanceof InputMethodAdapter) {
            s = ((InputMethodAdapter)inputMethod).getNativeInputMethodInfo();
        }
        if (s == null && this.inputMethodLocator != null) {
            s = this.inputMethodLocator.getDescriptor().getInputMethodDisplayName(this.getLocale(), SunToolkit.getStartupLocale());
        }
        if (s != null && !s.equals("")) {
            return s;
        }
        return inputMethod.toString() + "-" + inputMethod.getLocale().toString();
    }
    
    public void disableNativeIM() {
        final InputMethod inputMethod = this.getInputMethod();
        if (inputMethod != null && inputMethod instanceof InputMethodAdapter) {
            ((InputMethodAdapter)inputMethod).stopListening();
        }
    }
    
    private synchronized InputMethod getInputMethod() {
        if (this.inputMethod != null) {
            return this.inputMethod;
        }
        if (this.inputMethodCreationFailed) {
            return null;
        }
        return this.inputMethod = this.getInputMethodInstance();
    }
    
    private InputMethod getInputMethodInstance() {
        final InputMethodLocator inputMethodLocator = this.inputMethodLocator;
        if (inputMethodLocator == null) {
            this.inputMethodCreationFailed = true;
            return null;
        }
        final Locale locale = inputMethodLocator.getLocale();
        InputMethod inputMethod = null;
        if (this.usedInputMethods != null) {
            inputMethod = this.usedInputMethods.remove(inputMethodLocator.deriveLocator(null));
            if (inputMethod != null) {
                if (locale != null) {
                    inputMethod.setLocale(locale);
                }
                inputMethod.setCharacterSubsets(this.characterSubsets);
                final Boolean b = this.perInputMethodState.remove(inputMethod);
                if (b != null) {
                    this.enableClientWindowNotification(inputMethod, b);
                }
                ((InputMethodContext)this).setInputMethodSupportsBelowTheSpot(!(inputMethod instanceof InputMethodAdapter) || ((InputMethodAdapter)inputMethod).supportsBelowTheSpot());
                return inputMethod;
            }
        }
        try {
            inputMethod = inputMethodLocator.getDescriptor().createInputMethod();
            if (locale != null) {
                inputMethod.setLocale(locale);
            }
            inputMethod.setInputMethodContext((java.awt.im.spi.InputMethodContext)this);
            inputMethod.setCharacterSubsets(this.characterSubsets);
        }
        catch (final Exception ex) {
            this.logCreationFailed(ex);
            this.inputMethodCreationFailed = true;
            if (inputMethod != null) {
                inputMethod = null;
            }
        }
        catch (final LinkageError linkageError) {
            this.logCreationFailed(linkageError);
            this.inputMethodCreationFailed = true;
        }
        ((InputMethodContext)this).setInputMethodSupportsBelowTheSpot(!(inputMethod instanceof InputMethodAdapter) || ((InputMethodAdapter)inputMethod).supportsBelowTheSpot());
        return inputMethod;
    }
    
    private void logCreationFailed(final Throwable t) {
        final PlatformLogger logger = PlatformLogger.getLogger("sun.awt.im");
        if (logger.isLoggable(PlatformLogger.Level.CONFIG)) {
            logger.config(new MessageFormat(Toolkit.getProperty("AWT.InputMethodCreationFailed", "Could not create {0}. Reason: {1}")).format(new Object[] { this.inputMethodLocator.getDescriptor().getInputMethodDisplayName(null, Locale.getDefault()), t.getLocalizedMessage() }));
        }
    }
    
    InputMethodLocator getInputMethodLocator() {
        if (this.inputMethod != null) {
            return this.inputMethodLocator.deriveLocator(this.inputMethod.getLocale());
        }
        return this.inputMethodLocator;
    }
    
    @Override
    public synchronized void endComposition() {
        if (this.inputMethod != null) {
            this.inputMethod.endComposition();
        }
    }
    
    synchronized void enableClientWindowNotification(final InputMethod inputMethod, final boolean clientWindowNotificationEnabled) {
        if (inputMethod != this.inputMethod) {
            if (this.perInputMethodState == null) {
                this.perInputMethodState = new HashMap<InputMethod, Boolean>(5);
            }
            this.perInputMethodState.put(inputMethod, clientWindowNotificationEnabled);
            return;
        }
        if (this.clientWindowNotificationEnabled != clientWindowNotificationEnabled) {
            this.clientWindowLocation = null;
            this.clientWindowNotificationEnabled = clientWindowNotificationEnabled;
        }
        if (this.clientWindowNotificationEnabled) {
            if (!this.addedClientWindowListeners()) {
                this.addClientWindowListeners();
            }
            if (this.clientWindowListened != null) {
                this.clientWindowLocation = null;
                this.notifyClientWindowChange(this.clientWindowListened);
            }
        }
        else if (this.addedClientWindowListeners()) {
            this.removeClientWindowListeners();
        }
    }
    
    private synchronized void notifyClientWindowChange(final Window window) {
        if (this.inputMethod == null) {
            return;
        }
        if (!window.isVisible() || (window instanceof Frame && ((Frame)window).getState() == 1)) {
            this.clientWindowLocation = null;
            this.inputMethod.notifyClientWindowChange(null);
            return;
        }
        final Rectangle bounds = window.getBounds();
        if (this.clientWindowLocation == null || !this.clientWindowLocation.equals(bounds)) {
            this.clientWindowLocation = bounds;
            this.inputMethod.notifyClientWindowChange(this.clientWindowLocation);
        }
    }
    
    private synchronized void addClientWindowListeners() {
        final Component clientComponent = this.getClientComponent();
        if (clientComponent == null) {
            return;
        }
        final Window componentWindow = getComponentWindow(clientComponent);
        if (componentWindow == null) {
            return;
        }
        componentWindow.addComponentListener(this);
        componentWindow.addWindowListener(this);
        this.clientWindowListened = componentWindow;
    }
    
    private synchronized void removeClientWindowListeners() {
        this.clientWindowListened.removeComponentListener(this);
        this.clientWindowListened.removeWindowListener(this);
        this.clientWindowListened = null;
    }
    
    private boolean addedClientWindowListeners() {
        return this.clientWindowListened != null;
    }
    
    @Override
    public void componentResized(final ComponentEvent componentEvent) {
        this.notifyClientWindowChange((Window)componentEvent.getComponent());
    }
    
    @Override
    public void componentMoved(final ComponentEvent componentEvent) {
        this.notifyClientWindowChange((Window)componentEvent.getComponent());
    }
    
    @Override
    public void componentShown(final ComponentEvent componentEvent) {
        this.notifyClientWindowChange((Window)componentEvent.getComponent());
    }
    
    @Override
    public void componentHidden(final ComponentEvent componentEvent) {
        this.notifyClientWindowChange((Window)componentEvent.getComponent());
    }
    
    @Override
    public void windowOpened(final WindowEvent windowEvent) {
    }
    
    @Override
    public void windowClosing(final WindowEvent windowEvent) {
    }
    
    @Override
    public void windowClosed(final WindowEvent windowEvent) {
    }
    
    @Override
    public void windowIconified(final WindowEvent windowEvent) {
        this.notifyClientWindowChange(windowEvent.getWindow());
    }
    
    @Override
    public void windowDeiconified(final WindowEvent windowEvent) {
        this.notifyClientWindowChange(windowEvent.getWindow());
    }
    
    @Override
    public void windowActivated(final WindowEvent windowEvent) {
    }
    
    @Override
    public void windowDeactivated(final WindowEvent windowEvent) {
    }
    
    private void initializeInputMethodSelectionKey() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                InputContext.inputMethodSelectionKey = InputContext.this.getInputMethodSelectionKeyStroke(Preferences.userRoot());
                if (InputContext.inputMethodSelectionKey == null) {
                    InputContext.inputMethodSelectionKey = InputContext.this.getInputMethodSelectionKeyStroke(Preferences.systemRoot());
                }
                return null;
            }
        });
    }
    
    private AWTKeyStroke getInputMethodSelectionKeyStroke(final Preferences preferences) {
        try {
            if (preferences.nodeExists("/java/awt/im/selectionKey")) {
                final Preferences node = preferences.node("/java/awt/im/selectionKey");
                final int int1 = node.getInt("keyCode", 0);
                if (int1 != 0) {
                    return AWTKeyStroke.getAWTKeyStroke(int1, node.getInt("modifiers", 0));
                }
            }
        }
        catch (final BackingStoreException ex) {}
        return null;
    }
    
    static {
        log = PlatformLogger.getLogger("sun.awt.im.InputContext");
        InputContext.previousInputMethod = null;
        InputContext.inputMethodSelectionKeyInitialized = false;
    }
}
