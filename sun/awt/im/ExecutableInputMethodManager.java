package sun.awt.im;

import java.security.PrivilegedAction;
import java.util.prefs.BackingStoreException;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.security.PrivilegedExceptionAction;
import java.awt.Dialog;
import java.awt.Frame;
import sun.awt.AppContext;
import java.awt.AWTEvent;
import sun.awt.SunToolkit;
import java.awt.event.InvocationEvent;
import java.lang.reflect.InvocationTargetException;
import java.awt.EventQueue;
import java.awt.im.spi.InputMethodDescriptor;
import java.awt.AWTException;
import java.util.Locale;
import sun.awt.InputMethodSupport;
import java.awt.Toolkit;
import java.util.prefs.Preferences;
import java.util.Hashtable;
import java.awt.Component;
import java.util.Vector;

class ExecutableInputMethodManager extends InputMethodManager implements Runnable
{
    private InputContext currentInputContext;
    private String triggerMenuString;
    private InputMethodPopupMenu selectionMenu;
    private static String selectInputMethodMenuTitle;
    private InputMethodLocator hostAdapterLocator;
    private int javaInputMethodCount;
    private Vector<InputMethodLocator> javaInputMethodLocatorList;
    private Component requestComponent;
    private InputContext requestInputContext;
    private static final String preferredIMNode = "/sun/awt/im/preferredInputMethod";
    private static final String descriptorKey = "descriptor";
    private Hashtable<String, InputMethodLocator> preferredLocatorCache;
    private Preferences userRoot;
    
    ExecutableInputMethodManager() {
        this.preferredLocatorCache = new Hashtable<String, InputMethodLocator>();
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        try {
            if (defaultToolkit instanceof InputMethodSupport) {
                final InputMethodDescriptor inputMethodAdapterDescriptor = ((InputMethodSupport)defaultToolkit).getInputMethodAdapterDescriptor();
                if (inputMethodAdapterDescriptor != null) {
                    this.hostAdapterLocator = new InputMethodLocator(inputMethodAdapterDescriptor, null, null);
                }
            }
        }
        catch (final AWTException ex) {}
        this.javaInputMethodLocatorList = new Vector<InputMethodLocator>();
        this.initializeInputMethodLocatorList();
    }
    
    synchronized void initialize() {
        ExecutableInputMethodManager.selectInputMethodMenuTitle = Toolkit.getProperty("AWT.InputMethodSelectionMenu", "Select Input Method");
        this.triggerMenuString = ExecutableInputMethodManager.selectInputMethodMenuTitle;
    }
    
    @Override
    public void run() {
        while (!this.hasMultipleInputMethods()) {
            try {
                synchronized (this) {
                    this.wait();
                }
            }
            catch (final InterruptedException ex) {}
        }
        while (true) {
            this.waitForChangeRequest();
            this.initializeInputMethodLocatorList();
            try {
                if (this.requestComponent != null) {
                    this.showInputMethodMenuOnRequesterEDT(this.requestComponent);
                }
                else {
                    EventQueue.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            ExecutableInputMethodManager.this.showInputMethodMenu();
                        }
                    });
                }
            }
            catch (final InterruptedException ex2) {}
            catch (final InvocationTargetException ex3) {}
        }
    }
    
    private void showInputMethodMenuOnRequesterEDT(final Component component) throws InterruptedException, InvocationTargetException {
        if (component == null) {
            return;
        }
        class AWTInvocationLock
        {
        }
        final AWTInvocationLock awtInvocationLock = new AWTInvocationLock();
        final InvocationEvent invocationEvent = new InvocationEvent(component, new Runnable() {
            @Override
            public void run() {
                ExecutableInputMethodManager.this.showInputMethodMenu();
            }
        }, awtInvocationLock, true);
        final AppContext targetToAppContext = SunToolkit.targetToAppContext(component);
        synchronized (awtInvocationLock) {
            SunToolkit.postEvent(targetToAppContext, invocationEvent);
            while (!invocationEvent.isDispatched()) {
                awtInvocationLock.wait();
            }
        }
        final Throwable throwable = invocationEvent.getThrowable();
        if (throwable != null) {
            throw new InvocationTargetException(throwable);
        }
    }
    
    @Override
    void setInputContext(final InputContext currentInputContext) {
        if (this.currentInputContext == null || currentInputContext != null) {}
        this.currentInputContext = currentInputContext;
    }
    
    @Override
    public synchronized void notifyChangeRequest(final Component requestComponent) {
        if (!(requestComponent instanceof Frame) && !(requestComponent instanceof Dialog)) {
            return;
        }
        if (this.requestComponent != null) {
            return;
        }
        this.requestComponent = requestComponent;
        this.notify();
    }
    
    @Override
    public synchronized void notifyChangeRequestByHotKey(Component parent) {
        while (!(parent instanceof Frame) && !(parent instanceof Dialog)) {
            if (parent == null) {
                return;
            }
            parent = parent.getParent();
        }
        this.notifyChangeRequest(parent);
    }
    
    @Override
    public String getTriggerMenuString() {
        return this.triggerMenuString;
    }
    
    @Override
    boolean hasMultipleInputMethods() {
        return (this.hostAdapterLocator != null && this.javaInputMethodCount > 0) || this.javaInputMethodCount > 1;
    }
    
    private synchronized void waitForChangeRequest() {
        try {
            while (this.requestComponent == null) {
                this.wait();
            }
        }
        catch (final InterruptedException ex) {}
    }
    
    private void initializeInputMethodLocatorList() {
        synchronized (this.javaInputMethodLocatorList) {
            this.javaInputMethodLocatorList.clear();
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                    @Override
                    public Object run() {
                        for (final InputMethodDescriptor inputMethodDescriptor : ServiceLoader.loadInstalled(InputMethodDescriptor.class)) {
                            ExecutableInputMethodManager.this.javaInputMethodLocatorList.add(new InputMethodLocator(inputMethodDescriptor, inputMethodDescriptor.getClass().getClassLoader(), null));
                        }
                        return null;
                    }
                });
            }
            catch (final PrivilegedActionException ex) {
                ex.printStackTrace();
            }
            this.javaInputMethodCount = this.javaInputMethodLocatorList.size();
        }
        if (this.hasMultipleInputMethods()) {
            if (this.userRoot == null) {
                this.userRoot = this.getUserRoot();
            }
        }
        else {
            this.triggerMenuString = null;
        }
    }
    
    private void showInputMethodMenu() {
        if (!this.hasMultipleInputMethods()) {
            this.requestComponent = null;
            return;
        }
        (this.selectionMenu = InputMethodPopupMenu.getInstance(this.requestComponent, ExecutableInputMethodManager.selectInputMethodMenuTitle)).removeAll();
        final String currentSelection = this.getCurrentSelection();
        if (this.hostAdapterLocator != null) {
            this.selectionMenu.addOneInputMethodToMenu(this.hostAdapterLocator, currentSelection);
            this.selectionMenu.addSeparator();
        }
        for (int i = 0; i < this.javaInputMethodLocatorList.size(); ++i) {
            this.selectionMenu.addOneInputMethodToMenu(this.javaInputMethodLocatorList.get(i), currentSelection);
        }
        synchronized (this) {
            this.selectionMenu.addToComponent(this.requestComponent);
            this.requestInputContext = this.currentInputContext;
            this.selectionMenu.show(this.requestComponent, 60, 80);
            this.requestComponent = null;
        }
    }
    
    private String getCurrentSelection() {
        final InputContext currentInputContext = this.currentInputContext;
        if (currentInputContext != null) {
            final InputMethodLocator inputMethodLocator = currentInputContext.getInputMethodLocator();
            if (inputMethodLocator != null) {
                return inputMethodLocator.getActionCommandString();
            }
        }
        return null;
    }
    
    synchronized void changeInputMethod(final String s) {
        InputMethodLocator inputMethodLocator = null;
        String substring = s;
        String substring2 = null;
        final int index = s.indexOf(10);
        if (index != -1) {
            substring2 = s.substring(index + 1);
            substring = s.substring(0, index);
        }
        if (this.hostAdapterLocator.getActionCommandString().equals(substring)) {
            inputMethodLocator = this.hostAdapterLocator;
        }
        else {
            for (int i = 0; i < this.javaInputMethodLocatorList.size(); ++i) {
                final InputMethodLocator inputMethodLocator2 = this.javaInputMethodLocatorList.get(i);
                if (inputMethodLocator2.getActionCommandString().equals(substring)) {
                    inputMethodLocator = inputMethodLocator2;
                    break;
                }
            }
        }
        if (inputMethodLocator != null && substring2 != null) {
            String s2 = "";
            String substring3 = "";
            final int index2 = substring2.indexOf(95);
            String substring4;
            if (index2 == -1) {
                substring4 = substring2;
            }
            else {
                substring4 = substring2.substring(0, index2);
                final int n = index2 + 1;
                final int index3 = substring2.indexOf(95, n);
                if (index3 == -1) {
                    s2 = substring2.substring(n);
                }
                else {
                    s2 = substring2.substring(n, index3);
                    substring3 = substring2.substring(index3 + 1);
                }
            }
            inputMethodLocator = inputMethodLocator.deriveLocator(new Locale(substring4, s2, substring3));
        }
        if (inputMethodLocator == null) {
            return;
        }
        if (this.requestInputContext != null) {
            this.requestInputContext.changeInputMethod(inputMethodLocator);
            this.requestInputContext = null;
            this.putPreferredInputMethod(inputMethodLocator);
        }
    }
    
    @Override
    InputMethodLocator findInputMethod(final Locale locale) {
        final InputMethodLocator preferredInputMethod = this.getPreferredInputMethod(locale);
        if (preferredInputMethod != null) {
            return preferredInputMethod;
        }
        if (this.hostAdapterLocator != null && this.hostAdapterLocator.isLocaleAvailable(locale)) {
            return this.hostAdapterLocator.deriveLocator(locale);
        }
        this.initializeInputMethodLocatorList();
        for (int i = 0; i < this.javaInputMethodLocatorList.size(); ++i) {
            final InputMethodLocator inputMethodLocator = this.javaInputMethodLocatorList.get(i);
            if (inputMethodLocator.isLocaleAvailable(locale)) {
                return inputMethodLocator.deriveLocator(locale);
            }
        }
        return null;
    }
    
    @Override
    Locale getDefaultKeyboardLocale() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (defaultToolkit instanceof InputMethodSupport) {
            return ((InputMethodSupport)defaultToolkit).getDefaultKeyboardLocale();
        }
        return Locale.getDefault();
    }
    
    private synchronized InputMethodLocator getPreferredInputMethod(final Locale locale) {
        if (!this.hasMultipleInputMethods()) {
            return null;
        }
        InputMethodLocator inputMethodLocator = this.preferredLocatorCache.get(locale.toString().intern());
        if (inputMethodLocator != null) {
            return inputMethodLocator;
        }
        final String preferredInputMethodNode = this.findPreferredInputMethodNode(locale);
        final String preferredInputMethod = this.readPreferredInputMethod(preferredInputMethodNode);
        if (preferredInputMethod != null) {
            if (this.hostAdapterLocator != null && this.hostAdapterLocator.getDescriptor().getClass().getName().equals(preferredInputMethod)) {
                final Locale advertisedLocale = this.getAdvertisedLocale(this.hostAdapterLocator, locale);
                if (advertisedLocale != null) {
                    inputMethodLocator = this.hostAdapterLocator.deriveLocator(advertisedLocale);
                    this.preferredLocatorCache.put(locale.toString().intern(), inputMethodLocator);
                }
                return inputMethodLocator;
            }
            for (int i = 0; i < this.javaInputMethodLocatorList.size(); ++i) {
                final InputMethodLocator inputMethodLocator2 = this.javaInputMethodLocatorList.get(i);
                if (inputMethodLocator2.getDescriptor().getClass().getName().equals(preferredInputMethod)) {
                    final Locale advertisedLocale2 = this.getAdvertisedLocale(inputMethodLocator2, locale);
                    if (advertisedLocale2 != null) {
                        inputMethodLocator = inputMethodLocator2.deriveLocator(advertisedLocale2);
                        this.preferredLocatorCache.put(locale.toString().intern(), inputMethodLocator);
                    }
                    return inputMethodLocator;
                }
            }
            this.writePreferredInputMethod(preferredInputMethodNode, null);
        }
        return null;
    }
    
    private String findPreferredInputMethodNode(final Locale locale) {
        if (this.userRoot == null) {
            return null;
        }
        for (String s = "/sun/awt/im/preferredInputMethod/" + this.createLocalePath(locale); !s.equals("/sun/awt/im/preferredInputMethod"); s = s.substring(0, s.lastIndexOf(47))) {
            try {
                if (this.userRoot.nodeExists(s) && this.readPreferredInputMethod(s) != null) {
                    return s;
                }
            }
            catch (final BackingStoreException ex) {}
        }
        return null;
    }
    
    private String readPreferredInputMethod(final String s) {
        if (this.userRoot == null || s == null) {
            return null;
        }
        return this.userRoot.node(s).get("descriptor", null);
    }
    
    private synchronized void putPreferredInputMethod(final InputMethodLocator inputMethodLocator) {
        final InputMethodDescriptor descriptor = inputMethodLocator.getDescriptor();
        Locale locale = inputMethodLocator.getLocale();
        if (locale == null) {
            try {
                final Locale[] availableLocales = descriptor.getAvailableLocales();
                if (availableLocales.length != 1) {
                    return;
                }
                locale = availableLocales[0];
            }
            catch (final AWTException ex) {
                return;
            }
        }
        if (locale.equals(Locale.JAPAN)) {
            locale = Locale.JAPANESE;
        }
        if (locale.equals(Locale.KOREA)) {
            locale = Locale.KOREAN;
        }
        if (locale.equals(new Locale("th", "TH"))) {
            locale = new Locale("th");
        }
        this.writePreferredInputMethod("/sun/awt/im/preferredInputMethod/" + this.createLocalePath(locale), descriptor.getClass().getName());
        this.preferredLocatorCache.put(locale.toString().intern(), inputMethodLocator.deriveLocator(locale));
    }
    
    private String createLocalePath(final Locale locale) {
        final String language = locale.getLanguage();
        final String country = locale.getCountry();
        final String variant = locale.getVariant();
        String s;
        if (!variant.equals("")) {
            s = "_" + language + "/_" + country + "/_" + variant;
        }
        else if (!country.equals("")) {
            s = "_" + language + "/_" + country;
        }
        else {
            s = "_" + language;
        }
        return s;
    }
    
    private void writePreferredInputMethod(final String s, final String s2) {
        if (this.userRoot != null) {
            final Preferences node = this.userRoot.node(s);
            if (s2 != null) {
                node.put("descriptor", s2);
            }
            else {
                node.remove("descriptor");
            }
        }
    }
    
    private Preferences getUserRoot() {
        return AccessController.doPrivileged((PrivilegedAction<Preferences>)new PrivilegedAction<Preferences>() {
            @Override
            public Preferences run() {
                return Preferences.userRoot();
            }
        });
    }
    
    private Locale getAdvertisedLocale(final InputMethodLocator inputMethodLocator, final Locale locale) {
        Locale locale2 = null;
        if (inputMethodLocator.isLocaleAvailable(locale)) {
            locale2 = locale;
        }
        else if (locale.getLanguage().equals("ja")) {
            if (inputMethodLocator.isLocaleAvailable(Locale.JAPAN)) {
                locale2 = Locale.JAPAN;
            }
            else if (inputMethodLocator.isLocaleAvailable(Locale.JAPANESE)) {
                locale2 = Locale.JAPANESE;
            }
        }
        else if (locale.getLanguage().equals("ko")) {
            if (inputMethodLocator.isLocaleAvailable(Locale.KOREA)) {
                locale2 = Locale.KOREA;
            }
            else if (inputMethodLocator.isLocaleAvailable(Locale.KOREAN)) {
                locale2 = Locale.KOREAN;
            }
        }
        else if (locale.getLanguage().equals("th")) {
            if (inputMethodLocator.isLocaleAvailable(new Locale("th", "TH"))) {
                locale2 = new Locale("th", "TH");
            }
            else if (inputMethodLocator.isLocaleAvailable(new Locale("th"))) {
                locale2 = new Locale("th");
            }
        }
        return locale2;
    }
}
