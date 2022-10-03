package java.awt;

import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;
import javax.accessibility.AccessibleContext;
import java.io.OptionalDataException;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.awt.event.KeyEvent;
import java.util.List;
import java.awt.peer.FramePeer;
import sun.awt.SunToolkit;
import java.util.Vector;

public class Frame extends Window implements MenuContainer
{
    @Deprecated
    public static final int DEFAULT_CURSOR = 0;
    @Deprecated
    public static final int CROSSHAIR_CURSOR = 1;
    @Deprecated
    public static final int TEXT_CURSOR = 2;
    @Deprecated
    public static final int WAIT_CURSOR = 3;
    @Deprecated
    public static final int SW_RESIZE_CURSOR = 4;
    @Deprecated
    public static final int SE_RESIZE_CURSOR = 5;
    @Deprecated
    public static final int NW_RESIZE_CURSOR = 6;
    @Deprecated
    public static final int NE_RESIZE_CURSOR = 7;
    @Deprecated
    public static final int N_RESIZE_CURSOR = 8;
    @Deprecated
    public static final int S_RESIZE_CURSOR = 9;
    @Deprecated
    public static final int W_RESIZE_CURSOR = 10;
    @Deprecated
    public static final int E_RESIZE_CURSOR = 11;
    @Deprecated
    public static final int HAND_CURSOR = 12;
    @Deprecated
    public static final int MOVE_CURSOR = 13;
    public static final int NORMAL = 0;
    public static final int ICONIFIED = 1;
    public static final int MAXIMIZED_HORIZ = 2;
    public static final int MAXIMIZED_VERT = 4;
    public static final int MAXIMIZED_BOTH = 6;
    Rectangle maximizedBounds;
    String title;
    MenuBar menuBar;
    boolean resizable;
    boolean undecorated;
    boolean mbManagement;
    private int state;
    Vector<Window> ownedWindows;
    private static final String base = "frame";
    private static int nameCounter;
    private static final long serialVersionUID = 2673458971256075116L;
    private int frameSerializedDataVersion;
    
    public Frame() throws HeadlessException {
        this("");
    }
    
    public Frame(final GraphicsConfiguration graphicsConfiguration) {
        this("", graphicsConfiguration);
    }
    
    public Frame(final String s) throws HeadlessException {
        this.title = "Untitled";
        this.resizable = true;
        this.undecorated = false;
        this.mbManagement = false;
        this.state = 0;
        this.frameSerializedDataVersion = 1;
        this.init(s, null);
    }
    
    public Frame(final String s, final GraphicsConfiguration graphicsConfiguration) {
        super(graphicsConfiguration);
        this.title = "Untitled";
        this.resizable = true;
        this.undecorated = false;
        this.mbManagement = false;
        this.state = 0;
        this.frameSerializedDataVersion = 1;
        this.init(s, graphicsConfiguration);
    }
    
    private void init(final String title, final GraphicsConfiguration graphicsConfiguration) {
        this.title = title;
        SunToolkit.checkAndSetPolicy(this);
    }
    
    @Override
    String constructComponentName() {
        synchronized (Frame.class) {
            return "frame" + Frame.nameCounter++;
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                this.peer = this.getToolkit().createFrame(this);
            }
            final FramePeer framePeer = (FramePeer)this.peer;
            final MenuBar menuBar = this.menuBar;
            if (menuBar != null) {
                this.mbManagement = true;
                menuBar.addNotify();
                framePeer.setMenuBar(menuBar);
            }
            framePeer.setMaximizedBounds(this.maximizedBounds);
            super.addNotify();
        }
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String s) {
        final String title = this.title;
        if (s == null) {
            s = "";
        }
        synchronized (this) {
            this.title = s;
            final FramePeer framePeer = (FramePeer)this.peer;
            if (framePeer != null) {
                framePeer.setTitle(s);
            }
        }
        this.firePropertyChange("title", title, s);
    }
    
    public Image getIconImage() {
        final List<Image> icons = this.icons;
        if (icons != null && icons.size() > 0) {
            return (Image)icons.get(0);
        }
        return null;
    }
    
    @Override
    public void setIconImage(final Image iconImage) {
        super.setIconImage(iconImage);
    }
    
    public MenuBar getMenuBar() {
        return this.menuBar;
    }
    
    public void setMenuBar(final MenuBar menuBar) {
        synchronized (this.getTreeLock()) {
            if (this.menuBar == menuBar) {
                return;
            }
            if (menuBar != null && menuBar.parent != null) {
                menuBar.parent.remove(menuBar);
            }
            if (this.menuBar != null) {
                this.remove(this.menuBar);
            }
            this.menuBar = menuBar;
            if (this.menuBar != null) {
                this.menuBar.parent = this;
                final FramePeer framePeer = (FramePeer)this.peer;
                if (framePeer != null) {
                    this.mbManagement = true;
                    this.menuBar.addNotify();
                    this.invalidateIfValid();
                    framePeer.setMenuBar(this.menuBar);
                }
            }
        }
    }
    
    public boolean isResizable() {
        return this.resizable;
    }
    
    public void setResizable(final boolean b) {
        final boolean resizable = this.resizable;
        boolean b2 = false;
        synchronized (this) {
            this.resizable = b;
            final FramePeer framePeer = (FramePeer)this.peer;
            if (framePeer != null) {
                framePeer.setResizable(b);
                b2 = true;
            }
        }
        if (b2) {
            this.invalidateIfValid();
        }
        this.firePropertyChange("resizable", resizable, b);
    }
    
    public synchronized void setState(final int n) {
        final int extendedState = this.getExtendedState();
        if (n == 1 && (extendedState & 0x1) == 0x0) {
            this.setExtendedState(extendedState | 0x1);
        }
        else if (n == 0 && (extendedState & 0x1) != 0x0) {
            this.setExtendedState(extendedState & 0xFFFFFFFE);
        }
    }
    
    public void setExtendedState(final int n) {
        if (!this.isFrameStateSupported(n)) {
            return;
        }
        synchronized (this.getObjectLock()) {
            this.state = n;
        }
        final FramePeer framePeer = (FramePeer)this.peer;
        if (framePeer != null) {
            framePeer.setState(n);
        }
    }
    
    private boolean isFrameStateSupported(int n) {
        if (this.getToolkit().isFrameStateSupported(n)) {
            return true;
        }
        if ((n & 0x1) != 0x0 && !this.getToolkit().isFrameStateSupported(1)) {
            return false;
        }
        n &= 0xFFFFFFFE;
        return this.getToolkit().isFrameStateSupported(n);
    }
    
    public synchronized int getState() {
        return ((this.getExtendedState() & 0x1) != 0x0) ? 1 : 0;
    }
    
    public int getExtendedState() {
        synchronized (this.getObjectLock()) {
            return this.state;
        }
    }
    
    public void setMaximizedBounds(final Rectangle rectangle) {
        synchronized (this.getObjectLock()) {
            this.maximizedBounds = rectangle;
        }
        final FramePeer framePeer = (FramePeer)this.peer;
        if (framePeer != null) {
            framePeer.setMaximizedBounds(rectangle);
        }
    }
    
    public Rectangle getMaximizedBounds() {
        synchronized (this.getObjectLock()) {
            return this.maximizedBounds;
        }
    }
    
    public void setUndecorated(final boolean undecorated) {
        synchronized (this.getTreeLock()) {
            if (this.isDisplayable()) {
                throw new IllegalComponentStateException("The frame is displayable.");
            }
            if (!undecorated) {
                if (this.getOpacity() < 1.0f) {
                    throw new IllegalComponentStateException("The frame is not opaque");
                }
                if (this.getShape() != null) {
                    throw new IllegalComponentStateException("The frame does not have a default shape");
                }
                final Color background = this.getBackground();
                if (background != null && background.getAlpha() < 255) {
                    throw new IllegalComponentStateException("The frame background color is not opaque");
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
                throw new IllegalComponentStateException("The frame is decorated");
            }
            super.setOpacity(opacity);
        }
    }
    
    @Override
    public void setShape(final Shape shape) {
        synchronized (this.getTreeLock()) {
            if (shape != null && !this.isUndecorated()) {
                throw new IllegalComponentStateException("The frame is decorated");
            }
            super.setShape(shape);
        }
    }
    
    @Override
    public void setBackground(final Color background) {
        synchronized (this.getTreeLock()) {
            if (background != null && background.getAlpha() < 255 && !this.isUndecorated()) {
                throw new IllegalComponentStateException("The frame is decorated");
            }
            super.setBackground(background);
        }
    }
    
    @Override
    public void remove(final MenuComponent menuComponent) {
        if (menuComponent == null) {
            return;
        }
        synchronized (this.getTreeLock()) {
            if (menuComponent == this.menuBar) {
                this.menuBar = null;
                final FramePeer framePeer = (FramePeer)this.peer;
                if (framePeer != null) {
                    this.mbManagement = true;
                    this.invalidateIfValid();
                    framePeer.setMenuBar(null);
                    menuComponent.removeNotify();
                }
                menuComponent.parent = null;
            }
            else {
                super.remove(menuComponent);
            }
        }
    }
    
    @Override
    public void removeNotify() {
        synchronized (this.getTreeLock()) {
            final FramePeer framePeer = (FramePeer)this.peer;
            if (framePeer != null) {
                this.getState();
                if (this.menuBar != null) {
                    this.mbManagement = true;
                    framePeer.setMenuBar(null);
                    this.menuBar.removeNotify();
                }
            }
            super.removeNotify();
        }
    }
    
    @Override
    void postProcessKeyEvent(final KeyEvent keyEvent) {
        if (this.menuBar != null && this.menuBar.handleShortcut(keyEvent)) {
            keyEvent.consume();
            return;
        }
        super.postProcessKeyEvent(keyEvent);
    }
    
    @Override
    protected String paramString() {
        String s = super.paramString();
        if (this.title != null) {
            s = s + ",title=" + this.title;
        }
        if (this.resizable) {
            s += ",resizable";
        }
        final int extendedState = this.getExtendedState();
        if (extendedState == 0) {
            s += ",normal";
        }
        else {
            if ((extendedState & 0x1) != 0x0) {
                s += ",iconified";
            }
            if ((extendedState & 0x6) == 0x6) {
                s += ",maximized";
            }
            else if ((extendedState & 0x2) != 0x0) {
                s += ",maximized_horiz";
            }
            else if ((extendedState & 0x4) != 0x0) {
                s += ",maximized_vert";
            }
        }
        return s;
    }
    
    @Deprecated
    public void setCursor(final int n) {
        if (n < 0 || n > 13) {
            throw new IllegalArgumentException("illegal cursor type");
        }
        this.setCursor(Cursor.getPredefinedCursor(n));
    }
    
    @Deprecated
    public int getCursorType() {
        return this.getCursor().getType();
    }
    
    public static Frame[] getFrames() {
        final Window[] windows = Window.getWindows();
        int n = 0;
        final Window[] array = windows;
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i] instanceof Frame) {
                ++n;
            }
        }
        final Frame[] array2 = new Frame[n];
        int n2 = 0;
        for (final Window window : windows) {
            if (window instanceof Frame) {
                array2[n2++] = (Frame)window;
            }
        }
        return array2;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.icons != null && this.icons.size() > 0) {
            final Image image = this.icons.get(0);
            if (image instanceof Serializable) {
                objectOutputStream.writeObject(image);
                return;
            }
        }
        objectOutputStream.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        objectInputStream.defaultReadObject();
        try {
            final Image image = (Image)objectInputStream.readObject();
            if (this.icons == null) {
                (this.icons = new ArrayList<Image>()).add(image);
            }
        }
        catch (final OptionalDataException ex) {
            if (!ex.eof) {
                throw ex;
            }
        }
        if (this.menuBar != null) {
            this.menuBar.parent = this;
        }
        if (this.ownedWindows != null) {
            for (int i = 0; i < this.ownedWindows.size(); ++i) {
                this.connectOwnedWindow(this.ownedWindows.elementAt(i));
            }
            this.ownedWindows = null;
        }
    }
    
    private static native void initIDs();
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleAWTFrame();
        }
        return this.accessibleContext;
    }
    
    static {
        Frame.nameCounter = 0;
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        AWTAccessor.setFrameAccessor(new AWTAccessor.FrameAccessor() {
            @Override
            public void setExtendedState(final Frame frame, final int n) {
                synchronized (frame.getObjectLock()) {
                    frame.state = n;
                }
            }
            
            @Override
            public int getExtendedState(final Frame frame) {
                synchronized (frame.getObjectLock()) {
                    return frame.state;
                }
            }
            
            @Override
            public Rectangle getMaximizedBounds(final Frame frame) {
                synchronized (frame.getObjectLock()) {
                    return frame.maximizedBounds;
                }
            }
        });
    }
    
    protected class AccessibleAWTFrame extends AccessibleAWTWindow
    {
        private static final long serialVersionUID = -6172960752956030250L;
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.FRAME;
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (Frame.this.getFocusOwner() != null) {
                accessibleStateSet.add(AccessibleState.ACTIVE);
            }
            if (Frame.this.isResizable()) {
                accessibleStateSet.add(AccessibleState.RESIZABLE);
            }
            return accessibleStateSet;
        }
    }
}
