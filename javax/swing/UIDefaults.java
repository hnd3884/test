package javax.swing;

import javax.swing.plaf.ColorUIResource;
import java.lang.reflect.Constructor;
import sun.swing.SwingUtilities2;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.beans.PropertyChangeListener;
import sun.reflect.misc.MethodUtil;
import java.lang.reflect.Method;
import sun.reflect.misc.ReflectUtil;
import javax.swing.plaf.ComponentUI;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import sun.util.CoreResourceBundleControl;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.Vector;
import javax.swing.event.SwingPropertyChangeSupport;
import java.util.Hashtable;

public class UIDefaults extends Hashtable<Object, Object>
{
    private static final Object PENDING;
    private SwingPropertyChangeSupport changeSupport;
    private Vector<String> resourceBundles;
    private Locale defaultLocale;
    private Map<Locale, Map<String, Object>> resourceCache;
    
    public UIDefaults() {
        this(700, 0.75f);
    }
    
    public UIDefaults(final int n, final float n2) {
        super(n, n2);
        this.defaultLocale = Locale.getDefault();
        this.resourceCache = new HashMap<Locale, Map<String, Object>>();
    }
    
    public UIDefaults(final Object[] array) {
        super(array.length / 2);
        this.defaultLocale = Locale.getDefault();
        for (int i = 0; i < array.length; i += 2) {
            super.put(array[i], array[i + 1]);
        }
    }
    
    @Override
    public Object get(final Object o) {
        final Object fromHashtable = this.getFromHashtable(o);
        return (fromHashtable != null) ? fromHashtable : this.getFromResourceBundle(o, null);
    }
    
    private Object getFromHashtable(final Object o) {
        Object o2 = super.get(o);
        if (o2 != UIDefaults.PENDING && !(o2 instanceof ActiveValue) && !(o2 instanceof LazyValue)) {
            return o2;
        }
        synchronized (this) {
            o2 = super.get(o);
            if (o2 == UIDefaults.PENDING) {
                do {
                    try {
                        this.wait();
                    }
                    catch (final InterruptedException ex) {}
                    o2 = super.get(o);
                } while (o2 == UIDefaults.PENDING);
                return o2;
            }
            if (o2 instanceof LazyValue) {
                super.put(o, UIDefaults.PENDING);
            }
            else if (!(o2 instanceof ActiveValue)) {
                return o2;
            }
        }
        if (o2 instanceof LazyValue) {
            try {
                o2 = ((LazyValue)o2).createValue(this);
            }
            finally {
                synchronized (this) {
                    if (o2 == null) {
                        super.remove(o);
                    }
                    else {
                        super.put(o, (LazyValue)o2);
                    }
                    this.notifyAll();
                }
            }
        }
        else {
            o2 = ((ActiveValue)o2).createValue(this);
        }
        return o2;
    }
    
    public Object get(final Object o, final Locale locale) {
        final Object fromHashtable = this.getFromHashtable(o);
        return (fromHashtable != null) ? fromHashtable : this.getFromResourceBundle(o, locale);
    }
    
    private Object getFromResourceBundle(final Object o, Locale defaultLocale) {
        if (this.resourceBundles == null || this.resourceBundles.isEmpty() || !(o instanceof String)) {
            return null;
        }
        if (defaultLocale == null) {
            if (this.defaultLocale == null) {
                return null;
            }
            defaultLocale = this.defaultLocale;
        }
        synchronized (this) {
            return this.getResourceCache(defaultLocale).get(o);
        }
    }
    
    private Map<String, Object> getResourceCache(final Locale locale) {
        Map map = this.resourceCache.get(locale);
        if (map == null) {
            map = new TextAndMnemonicHashMap();
            for (int i = this.resourceBundles.size() - 1; i >= 0; --i) {
                final String s = this.resourceBundles.get(i);
                try {
                    final CoreResourceBundleControl rbControlInstance = CoreResourceBundleControl.getRBControlInstance(s);
                    ResourceBundle resourceBundle;
                    if (rbControlInstance != null) {
                        resourceBundle = ResourceBundle.getBundle(s, locale, rbControlInstance);
                    }
                    else {
                        resourceBundle = ResourceBundle.getBundle(s, locale, ClassLoader.getSystemClassLoader());
                    }
                    final Enumeration<String> keys = resourceBundle.getKeys();
                    while (keys.hasMoreElements()) {
                        final String s2 = keys.nextElement();
                        if (map.get(s2) == null) {
                            map.put(s2, resourceBundle.getObject(s2));
                        }
                    }
                }
                catch (final MissingResourceException ex) {}
            }
            this.resourceCache.put(locale, map);
        }
        return map;
    }
    
    @Override
    public Object put(final Object o, final Object o2) {
        final Object o3 = (o2 == null) ? super.remove(o) : super.put(o, o2);
        if (o instanceof String) {
            this.firePropertyChange((String)o, o3, o2);
        }
        return o3;
    }
    
    public void putDefaults(final Object[] array) {
        for (int i = 0; i < array.length; i += 2) {
            final Object o = array[i + 1];
            if (o == null) {
                super.remove(array[i]);
            }
            else {
                super.put(array[i], o);
            }
        }
        this.firePropertyChange("UIDefaults", null, null);
    }
    
    public Font getFont(final Object o) {
        final Object value = this.get(o);
        return (value instanceof Font) ? ((Font)value) : null;
    }
    
    public Font getFont(final Object o, final Locale locale) {
        final Object value = this.get(o, locale);
        return (value instanceof Font) ? ((Font)value) : null;
    }
    
    public Color getColor(final Object o) {
        final Object value = this.get(o);
        return (value instanceof Color) ? ((Color)value) : null;
    }
    
    public Color getColor(final Object o, final Locale locale) {
        final Object value = this.get(o, locale);
        return (value instanceof Color) ? ((Color)value) : null;
    }
    
    public Icon getIcon(final Object o) {
        final Object value = this.get(o);
        return (value instanceof Icon) ? ((Icon)value) : null;
    }
    
    public Icon getIcon(final Object o, final Locale locale) {
        final Object value = this.get(o, locale);
        return (value instanceof Icon) ? ((Icon)value) : null;
    }
    
    public Border getBorder(final Object o) {
        final Object value = this.get(o);
        return (value instanceof Border) ? ((Border)value) : null;
    }
    
    public Border getBorder(final Object o, final Locale locale) {
        final Object value = this.get(o, locale);
        return (value instanceof Border) ? ((Border)value) : null;
    }
    
    public String getString(final Object o) {
        final Object value = this.get(o);
        return (value instanceof String) ? ((String)value) : null;
    }
    
    public String getString(final Object o, final Locale locale) {
        final Object value = this.get(o, locale);
        return (value instanceof String) ? ((String)value) : null;
    }
    
    public int getInt(final Object o) {
        final Object value = this.get(o);
        return (int)((value instanceof Integer) ? value : 0);
    }
    
    public int getInt(final Object o, final Locale locale) {
        final Object value = this.get(o, locale);
        return (int)((value instanceof Integer) ? value : 0);
    }
    
    public boolean getBoolean(final Object o) {
        final Object value = this.get(o);
        return value instanceof Boolean && (boolean)value;
    }
    
    public boolean getBoolean(final Object o, final Locale locale) {
        final Object value = this.get(o, locale);
        return value instanceof Boolean && (boolean)value;
    }
    
    public Insets getInsets(final Object o) {
        final Object value = this.get(o);
        return (value instanceof Insets) ? ((Insets)value) : null;
    }
    
    public Insets getInsets(final Object o, final Locale locale) {
        final Object value = this.get(o, locale);
        return (value instanceof Insets) ? ((Insets)value) : null;
    }
    
    public Dimension getDimension(final Object o) {
        final Object value = this.get(o);
        return (value instanceof Dimension) ? ((Dimension)value) : null;
    }
    
    public Dimension getDimension(final Object o, final Locale locale) {
        final Object value = this.get(o, locale);
        return (value instanceof Dimension) ? ((Dimension)value) : null;
    }
    
    public Class<? extends ComponentUI> getUIClass(final String s, final ClassLoader classLoader) {
        try {
            final String s2 = (String)this.get(s);
            if (s2 != null) {
                ReflectUtil.checkPackageAccess(s2);
                Class<?> clazz = (Class<?>)this.get(s2);
                if (clazz == null) {
                    if (classLoader == null) {
                        clazz = SwingUtilities.loadSystemClass(s2);
                    }
                    else {
                        clazz = classLoader.loadClass(s2);
                    }
                    if (clazz != null) {
                        this.put(s2, clazz);
                    }
                }
                return (Class<? extends ComponentUI>)clazz;
            }
        }
        catch (final ClassNotFoundException ex) {
            return null;
        }
        catch (final ClassCastException ex2) {
            return null;
        }
        return null;
    }
    
    public Class<? extends ComponentUI> getUIClass(final String s) {
        return this.getUIClass(s, null);
    }
    
    protected void getUIError(final String s) {
        System.err.println("UIDefaults.getUI() failed: " + s);
        try {
            throw new Error();
        }
        catch (final Throwable t) {
            t.printStackTrace();
        }
    }
    
    public ComponentUI getUI(final JComponent component) {
        final Object value = this.get("ClassLoader");
        final Class<? extends ComponentUI> uiClass = this.getUIClass(component.getUIClassID(), (value != null) ? ((ClassLoader)value) : component.getClass().getClassLoader());
        Object invoke = null;
        if (uiClass == null) {
            this.getUIError("no ComponentUI class for: " + component);
        }
        else {
            try {
                Method method = (Method)this.get(uiClass);
                if (method == null) {
                    method = uiClass.getMethod("createUI", JComponent.class);
                    this.put(uiClass, method);
                }
                invoke = MethodUtil.invoke(method, null, new Object[] { component });
            }
            catch (final NoSuchMethodException ex) {
                this.getUIError("static createUI() method not found in " + uiClass);
            }
            catch (final Exception ex2) {
                this.getUIError("createUI() failed for " + component + " " + ex2);
            }
        }
        return (ComponentUI)invoke;
    }
    
    public synchronized void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (this.changeSupport == null) {
            this.changeSupport = new SwingPropertyChangeSupport(this);
        }
        this.changeSupport.addPropertyChangeListener(propertyChangeListener);
    }
    
    public synchronized void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (this.changeSupport != null) {
            this.changeSupport.removePropertyChangeListener(propertyChangeListener);
        }
    }
    
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        if (this.changeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return this.changeSupport.getPropertyChangeListeners();
    }
    
    protected void firePropertyChange(final String s, final Object o, final Object o2) {
        if (this.changeSupport != null) {
            this.changeSupport.firePropertyChange(s, o, o2);
        }
    }
    
    public synchronized void addResourceBundle(final String s) {
        if (s == null) {
            return;
        }
        if (this.resourceBundles == null) {
            this.resourceBundles = new Vector<String>(5);
        }
        if (!this.resourceBundles.contains(s)) {
            this.resourceBundles.add(s);
            this.resourceCache.clear();
        }
    }
    
    public synchronized void removeResourceBundle(final String s) {
        if (this.resourceBundles != null) {
            this.resourceBundles.remove(s);
        }
        this.resourceCache.clear();
    }
    
    public void setDefaultLocale(final Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }
    
    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }
    
    static {
        PENDING = new Object();
    }
    
    public static class ProxyLazyValue implements LazyValue
    {
        private AccessControlContext acc;
        private String className;
        private String methodName;
        private Object[] args;
        
        public ProxyLazyValue(final String s) {
            this(s, (String)null);
        }
        
        public ProxyLazyValue(final String s, final String s2) {
            this(s, s2, null);
        }
        
        public ProxyLazyValue(final String s, final Object[] array) {
            this(s, null, array);
        }
        
        public ProxyLazyValue(final String className, final String methodName, final Object[] array) {
            this.acc = AccessController.getContext();
            this.className = className;
            this.methodName = methodName;
            if (array != null) {
                this.args = array.clone();
            }
        }
        
        @Override
        public Object createValue(final UIDefaults uiDefaults) {
            if (this.acc == null && System.getSecurityManager() != null) {
                throw new SecurityException("null AccessControlContext");
            }
            return AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    try {
                        Object o;
                        if (uiDefaults == null || !((o = uiDefaults.get("ClassLoader")) instanceof ClassLoader)) {
                            o = Thread.currentThread().getContextClassLoader();
                            if (o == null) {
                                o = ClassLoader.getSystemClassLoader();
                            }
                        }
                        ReflectUtil.checkPackageAccess(ProxyLazyValue.this.className);
                        final Class<?> forName = Class.forName(ProxyLazyValue.this.className, true, (ClassLoader)o);
                        SwingUtilities2.checkAccess(forName.getModifiers());
                        if (ProxyLazyValue.this.methodName != null) {
                            return MethodUtil.invoke(forName.getMethod(ProxyLazyValue.this.methodName, (Class[])ProxyLazyValue.this.getClassArray(ProxyLazyValue.this.args)), forName, ProxyLazyValue.this.args);
                        }
                        final Constructor constructor = forName.getConstructor((Class[])ProxyLazyValue.this.getClassArray(ProxyLazyValue.this.args));
                        SwingUtilities2.checkAccess(constructor.getModifiers());
                        return constructor.newInstance(ProxyLazyValue.this.args);
                    }
                    catch (final Exception ex) {
                        return null;
                    }
                }
            }, this.acc);
        }
        
        private Class[] getClassArray(final Object[] array) {
            Class[] array2 = null;
            if (array != null) {
                array2 = new Class[array.length];
                for (int i = 0; i < array.length; ++i) {
                    if (array[i] instanceof Integer) {
                        array2[i] = Integer.TYPE;
                    }
                    else if (array[i] instanceof Boolean) {
                        array2[i] = Boolean.TYPE;
                    }
                    else if (array[i] instanceof ColorUIResource) {
                        array2[i] = Color.class;
                    }
                    else {
                        array2[i] = array[i].getClass();
                    }
                }
            }
            return array2;
        }
        
        private String printArgs(final Object[] array) {
            String concat = "{";
            String s;
            if (array != null) {
                for (int i = 0; i < array.length - 1; ++i) {
                    concat = concat.concat(array[i] + ",");
                }
                s = concat.concat(array[array.length - 1] + "}");
            }
            else {
                s = concat.concat("}");
            }
            return s;
        }
    }
    
    public static class LazyInputMap implements LazyValue
    {
        private Object[] bindings;
        
        public LazyInputMap(final Object[] bindings) {
            this.bindings = bindings;
        }
        
        @Override
        public Object createValue(final UIDefaults uiDefaults) {
            if (this.bindings != null) {
                return LookAndFeel.makeInputMap(this.bindings);
            }
            return null;
        }
    }
    
    private static class TextAndMnemonicHashMap extends HashMap<String, Object>
    {
        static final String AND_MNEMONIC = "AndMnemonic";
        static final String TITLE_SUFFIX = ".titleAndMnemonic";
        static final String TEXT_SUFFIX = ".textAndMnemonic";
        
        @Override
        public Object get(final Object o) {
            final Object value = super.get(o);
            if (value == null) {
                boolean b = false;
                final String string = o.toString();
                String s = null;
                if (string.endsWith("AndMnemonic")) {
                    return null;
                }
                if (string.endsWith(".mnemonic")) {
                    s = this.composeKey(string, 9, ".textAndMnemonic");
                }
                else if (string.endsWith("NameMnemonic")) {
                    s = this.composeKey(string, 12, ".textAndMnemonic");
                }
                else if (string.endsWith("Mnemonic")) {
                    s = this.composeKey(string, 8, ".textAndMnemonic");
                    b = true;
                }
                if (s != null) {
                    Object o2 = super.get(s);
                    if (o2 == null && b) {
                        o2 = super.get(this.composeKey(string, 8, ".titleAndMnemonic"));
                    }
                    return (o2 == null) ? null : this.getMnemonicFromProperty(o2.toString());
                }
                if (string.endsWith("NameText")) {
                    s = this.composeKey(string, 8, ".textAndMnemonic");
                }
                else if (string.endsWith(".nameText")) {
                    s = this.composeKey(string, 9, ".textAndMnemonic");
                }
                else if (string.endsWith("Text")) {
                    s = this.composeKey(string, 4, ".textAndMnemonic");
                }
                else if (string.endsWith("Title")) {
                    s = this.composeKey(string, 5, ".titleAndMnemonic");
                }
                if (s != null) {
                    final Object value2 = super.get(s);
                    return (value2 == null) ? null : this.getTextFromProperty(value2.toString());
                }
                if (string.endsWith("DisplayedMnemonicIndex")) {
                    Object o3 = super.get(this.composeKey(string, 22, ".textAndMnemonic"));
                    if (o3 == null) {
                        o3 = super.get(this.composeKey(string, 22, ".titleAndMnemonic"));
                    }
                    return (o3 == null) ? null : this.getIndexFromProperty(o3.toString());
                }
            }
            return value;
        }
        
        String composeKey(final String s, final int n, final String s2) {
            return s.substring(0, s.length() - n) + s2;
        }
        
        String getTextFromProperty(final String s) {
            return s.replace("&", "");
        }
        
        String getMnemonicFromProperty(final String s) {
            final int index = s.indexOf(38);
            if (0 <= index && index < s.length() - 1) {
                return Integer.toString(Character.toUpperCase(s.charAt(index + 1)));
            }
            return null;
        }
        
        String getIndexFromProperty(final String s) {
            final int index = s.indexOf(38);
            return (index == -1) ? null : Integer.toString(index);
        }
    }
    
    public interface LazyValue
    {
        Object createValue(final UIDefaults p0);
    }
    
    public interface ActiveValue
    {
        Object createValue(final UIDefaults p0);
    }
}
