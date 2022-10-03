package java.awt.event;

import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.awt.Toolkit;
import java.awt.IllegalComponentStateException;
import java.awt.Component;
import java.awt.Point;

public class MouseEvent extends InputEvent
{
    public static final int MOUSE_FIRST = 500;
    public static final int MOUSE_LAST = 507;
    public static final int MOUSE_CLICKED = 500;
    public static final int MOUSE_PRESSED = 501;
    public static final int MOUSE_RELEASED = 502;
    public static final int MOUSE_MOVED = 503;
    public static final int MOUSE_ENTERED = 504;
    public static final int MOUSE_EXITED = 505;
    public static final int MOUSE_DRAGGED = 506;
    public static final int MOUSE_WHEEL = 507;
    public static final int NOBUTTON = 0;
    public static final int BUTTON1 = 1;
    public static final int BUTTON2 = 2;
    public static final int BUTTON3 = 3;
    int x;
    int y;
    private int xAbs;
    private int yAbs;
    int clickCount;
    private boolean causedByTouchEvent;
    int button;
    boolean popupTrigger;
    private static final long serialVersionUID = -991214153494842848L;
    private static int cachedNumberOfButtons;
    private transient boolean shouldExcludeButtonFromExtModifiers;
    
    private static native void initIDs();
    
    public Point getLocationOnScreen() {
        return new Point(this.xAbs, this.yAbs);
    }
    
    public int getXOnScreen() {
        return this.xAbs;
    }
    
    public int getYOnScreen() {
        return this.yAbs;
    }
    
    public MouseEvent(final Component component, final int n, final long n2, final int n3, final int n4, final int n5, final int n6, final boolean b, final int n7) {
        this(component, n, n2, n3, n4, n5, 0, 0, n6, b, n7);
        final Point point = new Point(0, 0);
        try {
            final Point locationOnScreen = component.getLocationOnScreen();
            this.xAbs = locationOnScreen.x + n4;
            this.yAbs = locationOnScreen.y + n5;
        }
        catch (final IllegalComponentStateException ex) {
            this.xAbs = 0;
            this.yAbs = 0;
        }
    }
    
    public MouseEvent(final Component component, final int n, final long n2, final int n3, final int n4, final int n5, final int n6, final boolean b) {
        this(component, n, n2, n3, n4, n5, n6, b, 0);
    }
    
    @Override
    public int getModifiersEx() {
        int modifiers = this.modifiers;
        if (this.shouldExcludeButtonFromExtModifiers) {
            modifiers &= ~InputEvent.getMaskForButton(this.getButton());
        }
        return modifiers & 0xFFFFFFC0;
    }
    
    public MouseEvent(final Component component, final int n, final long n2, final int n3, final int x, final int y, final int xAbs, final int yAbs, final int clickCount, final boolean popupTrigger, final int button) {
        super(component, n, n2, n3);
        this.popupTrigger = false;
        this.shouldExcludeButtonFromExtModifiers = false;
        this.x = x;
        this.y = y;
        this.xAbs = xAbs;
        this.yAbs = yAbs;
        this.clickCount = clickCount;
        this.popupTrigger = popupTrigger;
        if (button < 0) {
            throw new IllegalArgumentException("Invalid button value :" + button);
        }
        if (button > 3) {
            if (!Toolkit.getDefaultToolkit().areExtraMouseButtonsEnabled()) {
                throw new IllegalArgumentException("Extra mouse events are disabled " + button);
            }
            if (button > MouseEvent.cachedNumberOfButtons) {
                throw new IllegalArgumentException("Nonexistent button " + button);
            }
            if (this.getModifiersEx() != 0 && (n == 502 || n == 500)) {
                this.shouldExcludeButtonFromExtModifiers = true;
            }
        }
        this.button = button;
        if (this.getModifiers() != 0 && this.getModifiersEx() == 0) {
            this.setNewModifiers();
        }
        else if (this.getModifiers() == 0 && (this.getModifiersEx() != 0 || button != 0) && button <= 3) {
            this.setOldModifiers();
        }
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public Point getPoint() {
        final int x;
        final int y;
        synchronized (this) {
            x = this.x;
            y = this.y;
        }
        return new Point(x, y);
    }
    
    public synchronized void translatePoint(final int n, final int n2) {
        this.x += n;
        this.y += n2;
    }
    
    public int getClickCount() {
        return this.clickCount;
    }
    
    public int getButton() {
        return this.button;
    }
    
    public boolean isPopupTrigger() {
        return this.popupTrigger;
    }
    
    public static String getMouseModifiersText(final int n) {
        final StringBuilder sb = new StringBuilder();
        if ((n & 0x8) != 0x0) {
            sb.append(Toolkit.getProperty("AWT.alt", "Alt"));
            sb.append("+");
        }
        if ((n & 0x4) != 0x0) {
            sb.append(Toolkit.getProperty("AWT.meta", "Meta"));
            sb.append("+");
        }
        if ((n & 0x2) != 0x0) {
            sb.append(Toolkit.getProperty("AWT.control", "Ctrl"));
            sb.append("+");
        }
        if ((n & 0x1) != 0x0) {
            sb.append(Toolkit.getProperty("AWT.shift", "Shift"));
            sb.append("+");
        }
        if ((n & 0x20) != 0x0) {
            sb.append(Toolkit.getProperty("AWT.altGraph", "Alt Graph"));
            sb.append("+");
        }
        if ((n & 0x10) != 0x0) {
            sb.append(Toolkit.getProperty("AWT.button1", "Button1"));
            sb.append("+");
        }
        if ((n & 0x8) != 0x0) {
            sb.append(Toolkit.getProperty("AWT.button2", "Button2"));
            sb.append("+");
        }
        if ((n & 0x4) != 0x0) {
            sb.append(Toolkit.getProperty("AWT.button3", "Button3"));
            sb.append("+");
        }
        for (int i = 1; i <= MouseEvent.cachedNumberOfButtons; ++i) {
            if ((n & InputEvent.getMaskForButton(i)) != 0x0 && sb.indexOf(Toolkit.getProperty("AWT.button" + i, "Button" + i)) == -1) {
                sb.append(Toolkit.getProperty("AWT.button" + i, "Button" + i));
                sb.append("+");
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    @Override
    public String paramString() {
        final StringBuilder sb = new StringBuilder(80);
        switch (this.id) {
            case 501: {
                sb.append("MOUSE_PRESSED");
                break;
            }
            case 502: {
                sb.append("MOUSE_RELEASED");
                break;
            }
            case 500: {
                sb.append("MOUSE_CLICKED");
                break;
            }
            case 504: {
                sb.append("MOUSE_ENTERED");
                break;
            }
            case 505: {
                sb.append("MOUSE_EXITED");
                break;
            }
            case 503: {
                sb.append("MOUSE_MOVED");
                break;
            }
            case 506: {
                sb.append("MOUSE_DRAGGED");
                break;
            }
            case 507: {
                sb.append("MOUSE_WHEEL");
                break;
            }
            default: {
                sb.append("unknown type");
                break;
            }
        }
        sb.append(",(").append(this.x).append(",").append(this.y).append(")");
        sb.append(",absolute(").append(this.xAbs).append(",").append(this.yAbs).append(")");
        if (this.id != 506 && this.id != 503) {
            sb.append(",button=").append(this.getButton());
        }
        if (this.getModifiers() != 0) {
            sb.append(",modifiers=").append(getMouseModifiersText(this.modifiers));
        }
        if (this.getModifiersEx() != 0) {
            sb.append(",extModifiers=").append(InputEvent.getModifiersExText(this.getModifiersEx()));
        }
        sb.append(",clickCount=").append(this.clickCount);
        return sb.toString();
    }
    
    private void setNewModifiers() {
        if ((this.modifiers & 0x10) != 0x0) {
            this.modifiers |= 0x400;
        }
        if ((this.modifiers & 0x8) != 0x0) {
            this.modifiers |= 0x800;
        }
        if ((this.modifiers & 0x4) != 0x0) {
            this.modifiers |= 0x1000;
        }
        if (this.id == 501 || this.id == 502 || this.id == 500) {
            if ((this.modifiers & 0x10) != 0x0) {
                this.button = 1;
                this.modifiers &= 0xFFFFFFF3;
                if (this.id != 501) {
                    this.modifiers &= 0xFFFFFBFF;
                }
            }
            else if ((this.modifiers & 0x8) != 0x0) {
                this.button = 2;
                this.modifiers &= 0xFFFFFFEB;
                if (this.id != 501) {
                    this.modifiers &= 0xFFFFF7FF;
                }
            }
            else if ((this.modifiers & 0x4) != 0x0) {
                this.button = 3;
                this.modifiers &= 0xFFFFFFE7;
                if (this.id != 501) {
                    this.modifiers &= 0xFFFFEFFF;
                }
            }
        }
        if ((this.modifiers & 0x8) != 0x0) {
            this.modifiers |= 0x200;
        }
        if ((this.modifiers & 0x4) != 0x0) {
            this.modifiers |= 0x100;
        }
        if ((this.modifiers & 0x1) != 0x0) {
            this.modifiers |= 0x40;
        }
        if ((this.modifiers & 0x2) != 0x0) {
            this.modifiers |= 0x80;
        }
        if ((this.modifiers & 0x20) != 0x0) {
            this.modifiers |= 0x2000;
        }
    }
    
    private void setOldModifiers() {
        if (this.id == 501 || this.id == 502 || this.id == 500) {
            switch (this.button) {
                case 1: {
                    this.modifiers |= 0x10;
                    break;
                }
                case 2: {
                    this.modifiers |= 0x8;
                    break;
                }
                case 3: {
                    this.modifiers |= 0x4;
                    break;
                }
            }
        }
        else {
            if ((this.modifiers & 0x400) != 0x0) {
                this.modifiers |= 0x10;
            }
            if ((this.modifiers & 0x800) != 0x0) {
                this.modifiers |= 0x8;
            }
            if ((this.modifiers & 0x1000) != 0x0) {
                this.modifiers |= 0x4;
            }
        }
        if ((this.modifiers & 0x200) != 0x0) {
            this.modifiers |= 0x8;
        }
        if ((this.modifiers & 0x100) != 0x0) {
            this.modifiers |= 0x4;
        }
        if ((this.modifiers & 0x40) != 0x0) {
            this.modifiers |= 0x1;
        }
        if ((this.modifiers & 0x80) != 0x0) {
            this.modifiers |= 0x2;
        }
        if ((this.modifiers & 0x2000) != 0x0) {
            this.modifiers |= 0x20;
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.getModifiers() != 0 && this.getModifiersEx() == 0) {
            this.setNewModifiers();
        }
    }
    
    static {
        NativeLibLoader.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (defaultToolkit instanceof SunToolkit) {
            MouseEvent.cachedNumberOfButtons = ((SunToolkit)defaultToolkit).getNumberOfButtons();
        }
        else {
            MouseEvent.cachedNumberOfButtons = 3;
        }
        AWTAccessor.setMouseEventAccessor(new AWTAccessor.MouseEventAccessor() {
            @Override
            public boolean isCausedByTouchEvent(final MouseEvent mouseEvent) {
                return mouseEvent.causedByTouchEvent;
            }
            
            @Override
            public void setCausedByTouchEvent(final MouseEvent mouseEvent, final boolean b) {
                mouseEvent.causedByTouchEvent = b;
            }
        });
    }
}
