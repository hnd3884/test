package java.awt.event;

import sun.awt.AWTAccessor;
import java.awt.Toolkit;
import sun.security.util.SecurityConstants;
import java.awt.GraphicsEnvironment;
import java.awt.Component;
import java.util.Arrays;
import sun.util.logging.PlatformLogger;

public abstract class InputEvent extends ComponentEvent
{
    private static final PlatformLogger logger;
    public static final int SHIFT_MASK = 1;
    public static final int CTRL_MASK = 2;
    public static final int META_MASK = 4;
    public static final int ALT_MASK = 8;
    public static final int ALT_GRAPH_MASK = 32;
    public static final int BUTTON1_MASK = 16;
    public static final int BUTTON2_MASK = 8;
    public static final int BUTTON3_MASK = 4;
    public static final int SHIFT_DOWN_MASK = 64;
    public static final int CTRL_DOWN_MASK = 128;
    public static final int META_DOWN_MASK = 256;
    public static final int ALT_DOWN_MASK = 512;
    public static final int BUTTON1_DOWN_MASK = 1024;
    public static final int BUTTON2_DOWN_MASK = 2048;
    public static final int BUTTON3_DOWN_MASK = 4096;
    public static final int ALT_GRAPH_DOWN_MASK = 8192;
    private static final int[] BUTTON_DOWN_MASK;
    static final int FIRST_HIGH_BIT = Integer.MIN_VALUE;
    static final int JDK_1_3_MODIFIERS = 63;
    static final int HIGH_MODIFIERS = Integer.MIN_VALUE;
    long when;
    int modifiers;
    private transient boolean canAccessSystemClipboard;
    static final long serialVersionUID = -2482525981698309786L;
    
    private static int[] getButtonDownMasks() {
        return Arrays.copyOf(InputEvent.BUTTON_DOWN_MASK, InputEvent.BUTTON_DOWN_MASK.length);
    }
    
    public static int getMaskForButton(final int n) {
        if (n <= 0 || n > InputEvent.BUTTON_DOWN_MASK.length) {
            throw new IllegalArgumentException("button doesn't exist " + n);
        }
        return InputEvent.BUTTON_DOWN_MASK[n - 1];
    }
    
    private static native void initIDs();
    
    InputEvent(final Component component, final int n, final long when, final int modifiers) {
        super(component, n);
        this.when = when;
        this.modifiers = modifiers;
        this.canAccessSystemClipboard = this.canAccessSystemClipboard();
    }
    
    private boolean canAccessSystemClipboard() {
        boolean b = false;
        if (!GraphicsEnvironment.isHeadless()) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                try {
                    securityManager.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
                    b = true;
                }
                catch (final SecurityException ex) {
                    if (InputEvent.logger.isLoggable(PlatformLogger.Level.FINE)) {
                        InputEvent.logger.fine("InputEvent.canAccessSystemClipboard() got SecurityException ", ex);
                    }
                }
            }
            else {
                b = true;
            }
        }
        return b;
    }
    
    public boolean isShiftDown() {
        return (this.modifiers & 0x1) != 0x0;
    }
    
    public boolean isControlDown() {
        return (this.modifiers & 0x2) != 0x0;
    }
    
    public boolean isMetaDown() {
        return (this.modifiers & 0x4) != 0x0;
    }
    
    public boolean isAltDown() {
        return (this.modifiers & 0x8) != 0x0;
    }
    
    public boolean isAltGraphDown() {
        return (this.modifiers & 0x20) != 0x0;
    }
    
    public long getWhen() {
        return this.when;
    }
    
    public int getModifiers() {
        return this.modifiers & 0x8000003F;
    }
    
    public int getModifiersEx() {
        return this.modifiers & 0xFFFFFFC0;
    }
    
    public void consume() {
        this.consumed = true;
    }
    
    public boolean isConsumed() {
        return this.consumed;
    }
    
    public static String getModifiersExText(final int n) {
        final StringBuilder sb = new StringBuilder();
        if ((n & 0x100) != 0x0) {
            sb.append(Toolkit.getProperty("AWT.meta", "Meta"));
            sb.append("+");
        }
        if ((n & 0x80) != 0x0) {
            sb.append(Toolkit.getProperty("AWT.control", "Ctrl"));
            sb.append("+");
        }
        if ((n & 0x200) != 0x0) {
            sb.append(Toolkit.getProperty("AWT.alt", "Alt"));
            sb.append("+");
        }
        if ((n & 0x40) != 0x0) {
            sb.append(Toolkit.getProperty("AWT.shift", "Shift"));
            sb.append("+");
        }
        if ((n & 0x2000) != 0x0) {
            sb.append(Toolkit.getProperty("AWT.altGraph", "Alt Graph"));
            sb.append("+");
        }
        int n2 = 1;
        final int[] button_DOWN_MASK = InputEvent.BUTTON_DOWN_MASK;
        for (int length = button_DOWN_MASK.length, i = 0; i < length; ++i) {
            if ((n & button_DOWN_MASK[i]) != 0x0) {
                sb.append(Toolkit.getProperty("AWT.button" + n2, "Button" + n2));
                sb.append("+");
            }
            ++n2;
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    static {
        logger = PlatformLogger.getLogger("java.awt.event.InputEvent");
        BUTTON_DOWN_MASK = new int[] { 1024, 2048, 4096, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824 };
        NativeLibLoader.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        AWTAccessor.setInputEventAccessor(new AWTAccessor.InputEventAccessor() {
            @Override
            public int[] getButtonDownMasks() {
                return getButtonDownMasks();
            }
        });
    }
}
