package java.awt;

import java.io.ObjectStreamException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.StringTokenizer;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import sun.awt.AppContext;
import java.util.Map;
import java.io.Serializable;

public class AWTKeyStroke implements Serializable
{
    static final long serialVersionUID = -6430539691155161871L;
    private static Map<String, Integer> modifierKeywords;
    private static VKCollection vks;
    private static Object APP_CONTEXT_CACHE_KEY;
    private static AWTKeyStroke APP_CONTEXT_KEYSTROKE_KEY;
    private char keyChar;
    private int keyCode;
    private int modifiers;
    private boolean onKeyRelease;
    
    private static Class<AWTKeyStroke> getAWTKeyStrokeClass() {
        Class<AWTKeyStroke> clazz = (Class<AWTKeyStroke>)AppContext.getAppContext().get(AWTKeyStroke.class);
        if (clazz == null) {
            clazz = AWTKeyStroke.class;
            AppContext.getAppContext().put(AWTKeyStroke.class, AWTKeyStroke.class);
        }
        return clazz;
    }
    
    protected AWTKeyStroke() {
        this.keyChar = '\uffff';
        this.keyCode = 0;
    }
    
    protected AWTKeyStroke(final char keyChar, final int keyCode, final int modifiers, final boolean onKeyRelease) {
        this.keyChar = '\uffff';
        this.keyCode = 0;
        this.keyChar = keyChar;
        this.keyCode = keyCode;
        this.modifiers = modifiers;
        this.onKeyRelease = onKeyRelease;
    }
    
    protected static void registerSubclass(final Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("subclass cannot be null");
        }
        synchronized (AWTKeyStroke.class) {
            final Class clazz2 = (Class)AppContext.getAppContext().get(AWTKeyStroke.class);
            if (clazz2 != null && clazz2.equals(clazz)) {
                return;
            }
        }
        if (!AWTKeyStroke.class.isAssignableFrom(clazz)) {
            throw new ClassCastException("subclass is not derived from AWTKeyStroke");
        }
        final Constructor ctor = getCtor(clazz);
        final String s = "subclass could not be instantiated";
        if (ctor == null) {
            throw new IllegalArgumentException(s);
        }
        try {
            if (ctor.newInstance((Object[])null) == null) {
                throw new IllegalArgumentException(s);
            }
        }
        catch (final NoSuchMethodError noSuchMethodError) {
            throw new IllegalArgumentException(s);
        }
        catch (final ExceptionInInitializerError exceptionInInitializerError) {
            throw new IllegalArgumentException(s);
        }
        catch (final InstantiationException ex) {
            throw new IllegalArgumentException(s);
        }
        catch (final IllegalAccessException ex2) {
            throw new IllegalArgumentException(s);
        }
        catch (final InvocationTargetException ex3) {
            throw new IllegalArgumentException(s);
        }
        synchronized (AWTKeyStroke.class) {
            AppContext.getAppContext().put(AWTKeyStroke.class, clazz);
            AppContext.getAppContext().remove(AWTKeyStroke.APP_CONTEXT_CACHE_KEY);
            AppContext.getAppContext().remove(AWTKeyStroke.APP_CONTEXT_KEYSTROKE_KEY);
        }
    }
    
    private static Constructor getCtor(final Class clazz) {
        return AccessController.doPrivileged((PrivilegedAction<Constructor>)new PrivilegedAction<Constructor>() {
            @Override
            public Constructor run() {
                try {
                    final Constructor declaredConstructor = clazz.getDeclaredConstructor((Class[])null);
                    if (declaredConstructor != null) {
                        declaredConstructor.setAccessible(true);
                    }
                    return declaredConstructor;
                }
                catch (final SecurityException ex) {}
                catch (final NoSuchMethodException ex2) {}
                return null;
            }
        });
    }
    
    private static synchronized AWTKeyStroke getCachedStroke(final char keyChar, final int keyCode, final int n, final boolean onKeyRelease) {
        Map map = (Map)AppContext.getAppContext().get(AWTKeyStroke.APP_CONTEXT_CACHE_KEY);
        AWTKeyStroke awtKeyStroke = (AWTKeyStroke)AppContext.getAppContext().get(AWTKeyStroke.APP_CONTEXT_KEYSTROKE_KEY);
        if (map == null) {
            map = new HashMap();
            AppContext.getAppContext().put(AWTKeyStroke.APP_CONTEXT_CACHE_KEY, map);
        }
        if (awtKeyStroke == null) {
            try {
                awtKeyStroke = getCtor(getAWTKeyStrokeClass()).newInstance((Object[])null);
                AppContext.getAppContext().put(AWTKeyStroke.APP_CONTEXT_KEYSTROKE_KEY, awtKeyStroke);
            }
            catch (final InstantiationException ex) {
                assert false;
            }
            catch (final IllegalAccessException ex2) {
                assert false;
            }
            catch (final InvocationTargetException ex3) {
                assert false;
            }
        }
        awtKeyStroke.keyChar = keyChar;
        awtKeyStroke.keyCode = keyCode;
        awtKeyStroke.modifiers = mapNewModifiers(mapOldModifiers(n));
        awtKeyStroke.onKeyRelease = onKeyRelease;
        AWTKeyStroke awtKeyStroke2 = map.get(awtKeyStroke);
        if (awtKeyStroke2 == null) {
            awtKeyStroke2 = awtKeyStroke;
            map.put(awtKeyStroke2, awtKeyStroke2);
            AppContext.getAppContext().remove(AWTKeyStroke.APP_CONTEXT_KEYSTROKE_KEY);
        }
        return awtKeyStroke2;
    }
    
    public static AWTKeyStroke getAWTKeyStroke(final char c) {
        return getCachedStroke(c, 0, 0, false);
    }
    
    public static AWTKeyStroke getAWTKeyStroke(final Character c, final int n) {
        if (c == null) {
            throw new IllegalArgumentException("keyChar cannot be null");
        }
        return getCachedStroke(c, 0, n, false);
    }
    
    public static AWTKeyStroke getAWTKeyStroke(final int n, final int n2, final boolean b) {
        return getCachedStroke('\uffff', n, n2, b);
    }
    
    public static AWTKeyStroke getAWTKeyStroke(final int n, final int n2) {
        return getCachedStroke('\uffff', n, n2, false);
    }
    
    public static AWTKeyStroke getAWTKeyStrokeForEvent(final KeyEvent keyEvent) {
        final int id = keyEvent.getID();
        switch (id) {
            case 401:
            case 402: {
                return getCachedStroke('\uffff', keyEvent.getKeyCode(), keyEvent.getModifiers(), id == 402);
            }
            case 400: {
                return getCachedStroke(keyEvent.getKeyChar(), 0, keyEvent.getModifiers(), false);
            }
            default: {
                return null;
            }
        }
    }
    
    public static AWTKeyStroke getAWTKeyStroke(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("String cannot be null");
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(s, " ");
        int n = 0;
        boolean b = false;
        int n2 = 0;
        int n3 = 0;
        synchronized (AWTKeyStroke.class) {
            if (AWTKeyStroke.modifierKeywords == null) {
                final HashMap hashMap = new HashMap(8, 1.0f);
                hashMap.put("shift", 65);
                hashMap.put("control", 130);
                hashMap.put("ctrl", 130);
                hashMap.put("meta", 260);
                hashMap.put("alt", 520);
                hashMap.put("altGraph", 8224);
                hashMap.put("button1", 1024);
                hashMap.put("button2", 2048);
                hashMap.put("button3", 4096);
                AWTKeyStroke.modifierKeywords = Collections.synchronizedMap((Map<String, Integer>)hashMap);
            }
        }
        final int countTokens = stringTokenizer.countTokens();
        int i = 1;
        while (i <= countTokens) {
            final String nextToken = stringTokenizer.nextToken();
            if (n2 != 0) {
                if (nextToken.length() != 1 || i != countTokens) {
                    throw new IllegalArgumentException("String formatted incorrectly");
                }
                return getCachedStroke(nextToken.charAt(0), 0, n, false);
            }
            else if (n3 != 0 || b || i == countTokens) {
                if (i != countTokens) {
                    throw new IllegalArgumentException("String formatted incorrectly");
                }
                return getCachedStroke('\uffff', getVKValue("VK_" + nextToken), n, b);
            }
            else {
                if (nextToken.equals("released")) {
                    b = true;
                }
                else if (nextToken.equals("pressed")) {
                    n3 = 1;
                }
                else if (nextToken.equals("typed")) {
                    n2 = 1;
                }
                else {
                    final Integer n4 = AWTKeyStroke.modifierKeywords.get(nextToken);
                    if (n4 == null) {
                        throw new IllegalArgumentException("String formatted incorrectly");
                    }
                    n |= n4;
                }
                ++i;
            }
        }
        throw new IllegalArgumentException("String formatted incorrectly");
    }
    
    private static VKCollection getVKCollection() {
        if (AWTKeyStroke.vks == null) {
            AWTKeyStroke.vks = new VKCollection();
        }
        return AWTKeyStroke.vks;
    }
    
    private static int getVKValue(final String s) {
        final VKCollection vkCollection = getVKCollection();
        Integer n = vkCollection.findCode(s);
        if (n == null) {
            int int1;
            try {
                int1 = KeyEvent.class.getField(s).getInt(KeyEvent.class);
            }
            catch (final NoSuchFieldException ex) {
                throw new IllegalArgumentException("String formatted incorrectly");
            }
            catch (final IllegalAccessException ex2) {
                throw new IllegalArgumentException("String formatted incorrectly");
            }
            n = int1;
            vkCollection.put(s, n);
        }
        return n;
    }
    
    public final char getKeyChar() {
        return this.keyChar;
    }
    
    public final int getKeyCode() {
        return this.keyCode;
    }
    
    public final int getModifiers() {
        return this.modifiers;
    }
    
    public final boolean isOnKeyRelease() {
        return this.onKeyRelease;
    }
    
    public final int getKeyEventType() {
        if (this.keyCode == 0) {
            return 400;
        }
        return this.onKeyRelease ? 402 : 401;
    }
    
    @Override
    public int hashCode() {
        return (this.keyChar + '\u0001') * (2 * (this.keyCode + 1)) * (this.modifiers + 1) + (this.onKeyRelease ? 1 : 2);
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (o instanceof AWTKeyStroke) {
            final AWTKeyStroke awtKeyStroke = (AWTKeyStroke)o;
            return awtKeyStroke.keyChar == this.keyChar && awtKeyStroke.keyCode == this.keyCode && awtKeyStroke.onKeyRelease == this.onKeyRelease && awtKeyStroke.modifiers == this.modifiers;
        }
        return false;
    }
    
    @Override
    public String toString() {
        if (this.keyCode == 0) {
            return getModifiersText(this.modifiers) + "typed " + this.keyChar;
        }
        return getModifiersText(this.modifiers) + (this.onKeyRelease ? "released" : "pressed") + " " + getVKText(this.keyCode);
    }
    
    static String getModifiersText(final int n) {
        final StringBuilder sb = new StringBuilder();
        if ((n & 0x40) != 0x0) {
            sb.append("shift ");
        }
        if ((n & 0x80) != 0x0) {
            sb.append("ctrl ");
        }
        if ((n & 0x100) != 0x0) {
            sb.append("meta ");
        }
        if ((n & 0x200) != 0x0) {
            sb.append("alt ");
        }
        if ((n & 0x2000) != 0x0) {
            sb.append("altGraph ");
        }
        if ((n & 0x400) != 0x0) {
            sb.append("button1 ");
        }
        if ((n & 0x800) != 0x0) {
            sb.append("button2 ");
        }
        if ((n & 0x1000) != 0x0) {
            sb.append("button3 ");
        }
        return sb.toString();
    }
    
    static String getVKText(final int n) {
        final VKCollection vkCollection = getVKCollection();
        final Integer value = n;
        final String name = vkCollection.findName(value);
        if (name != null) {
            return name.substring(3);
        }
        final int n2 = 25;
        final Field[] declaredFields = KeyEvent.class.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; ++i) {
            try {
                if (declaredFields[i].getModifiers() == n2 && declaredFields[i].getType() == Integer.TYPE && declaredFields[i].getName().startsWith("VK_") && declaredFields[i].getInt(KeyEvent.class) == n) {
                    final String name2 = declaredFields[i].getName();
                    vkCollection.put(name2, value);
                    return name2.substring(3);
                }
            }
            catch (final IllegalAccessException ex) {
                assert false;
            }
        }
        return "UNKNOWN";
    }
    
    protected Object readResolve() throws ObjectStreamException {
        synchronized (AWTKeyStroke.class) {
            if (this.getClass().equals(getAWTKeyStrokeClass())) {
                return getCachedStroke(this.keyChar, this.keyCode, this.modifiers, this.onKeyRelease);
            }
        }
        return this;
    }
    
    private static int mapOldModifiers(int n) {
        if ((n & 0x1) != 0x0) {
            n |= 0x40;
        }
        if ((n & 0x8) != 0x0) {
            n |= 0x200;
        }
        if ((n & 0x20) != 0x0) {
            n |= 0x2000;
        }
        if ((n & 0x2) != 0x0) {
            n |= 0x80;
        }
        if ((n & 0x4) != 0x0) {
            n |= 0x100;
        }
        n &= 0x3FC0;
        return n;
    }
    
    private static int mapNewModifiers(int n) {
        if ((n & 0x40) != 0x0) {
            n |= 0x1;
        }
        if ((n & 0x200) != 0x0) {
            n |= 0x8;
        }
        if ((n & 0x2000) != 0x0) {
            n |= 0x20;
        }
        if ((n & 0x80) != 0x0) {
            n |= 0x2;
        }
        if ((n & 0x100) != 0x0) {
            n |= 0x4;
        }
        return n;
    }
    
    static {
        AWTKeyStroke.APP_CONTEXT_CACHE_KEY = new Object();
        AWTKeyStroke.APP_CONTEXT_KEYSTROKE_KEY = new AWTKeyStroke();
        Toolkit.loadLibraries();
    }
}
