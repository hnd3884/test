package java.awt;

import javax.accessibility.AccessibleRole;
import java.awt.event.AdjustmentEvent;
import java.io.Serializable;
import javax.accessibility.AccessibleContext;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import sun.awt.ScrollPaneWheelScroller;
import java.awt.event.MouseWheelEvent;
import java.beans.Transient;
import java.awt.peer.ScrollPanePeer;
import sun.awt.SunToolkit;
import java.beans.ConstructorProperties;
import java.awt.event.AdjustmentListener;
import javax.accessibility.Accessible;

public class ScrollPane extends Container implements Accessible
{
    public static final int SCROLLBARS_AS_NEEDED = 0;
    public static final int SCROLLBARS_ALWAYS = 1;
    public static final int SCROLLBARS_NEVER = 2;
    private int scrollbarDisplayPolicy;
    private ScrollPaneAdjustable vAdjustable;
    private ScrollPaneAdjustable hAdjustable;
    private static final String base = "scrollpane";
    private static int nameCounter;
    private static final boolean defaultWheelScroll = true;
    private boolean wheelScrollingEnabled;
    private static final long serialVersionUID = 7956609840827222915L;
    
    private static native void initIDs();
    
    public ScrollPane() throws HeadlessException {
        this(0);
    }
    
    @ConstructorProperties({ "scrollbarDisplayPolicy" })
    public ScrollPane(final int scrollbarDisplayPolicy) throws HeadlessException {
        this.wheelScrollingEnabled = true;
        GraphicsEnvironment.checkHeadless();
        this.layoutMgr = null;
        this.width = 100;
        this.height = 100;
        switch (scrollbarDisplayPolicy) {
            case 0:
            case 1:
            case 2: {
                this.scrollbarDisplayPolicy = scrollbarDisplayPolicy;
                this.vAdjustable = new ScrollPaneAdjustable(this, new PeerFixer(this), 1);
                this.hAdjustable = new ScrollPaneAdjustable(this, new PeerFixer(this), 0);
                this.setWheelScrollingEnabled(true);
                return;
            }
            default: {
                throw new IllegalArgumentException("illegal scrollbar display policy");
            }
        }
    }
    
    @Override
    String constructComponentName() {
        synchronized (ScrollPane.class) {
            return "scrollpane" + ScrollPane.nameCounter++;
        }
    }
    
    private void addToPanel(final Component component, final Object o, final int n) {
        final Panel panel = new Panel();
        panel.setLayout(new BorderLayout());
        panel.add(component);
        super.addImpl(panel, o, n);
        this.validate();
    }
    
    @Override
    protected final void addImpl(final Component component, final Object o, final int n) {
        synchronized (this.getTreeLock()) {
            if (this.getComponentCount() > 0) {
                this.remove(0);
            }
            if (n > 0) {
                throw new IllegalArgumentException("position greater than 0");
            }
            if (!SunToolkit.isLightweightOrUnknown(component)) {
                super.addImpl(component, o, n);
            }
            else {
                this.addToPanel(component, o, n);
            }
        }
    }
    
    public int getScrollbarDisplayPolicy() {
        return this.scrollbarDisplayPolicy;
    }
    
    public Dimension getViewportSize() {
        final Insets insets = this.getInsets();
        return new Dimension(this.width - insets.right - insets.left, this.height - insets.top - insets.bottom);
    }
    
    public int getHScrollbarHeight() {
        int hScrollbarHeight = 0;
        if (this.scrollbarDisplayPolicy != 2) {
            final ScrollPanePeer scrollPanePeer = (ScrollPanePeer)this.peer;
            if (scrollPanePeer != null) {
                hScrollbarHeight = scrollPanePeer.getHScrollbarHeight();
            }
        }
        return hScrollbarHeight;
    }
    
    public int getVScrollbarWidth() {
        int vScrollbarWidth = 0;
        if (this.scrollbarDisplayPolicy != 2) {
            final ScrollPanePeer scrollPanePeer = (ScrollPanePeer)this.peer;
            if (scrollPanePeer != null) {
                vScrollbarWidth = scrollPanePeer.getVScrollbarWidth();
            }
        }
        return vScrollbarWidth;
    }
    
    public Adjustable getVAdjustable() {
        return this.vAdjustable;
    }
    
    public Adjustable getHAdjustable() {
        return this.hAdjustable;
    }
    
    public void setScrollPosition(final int value, final int value2) {
        synchronized (this.getTreeLock()) {
            if (this.getComponentCount() == 0) {
                throw new NullPointerException("child is null");
            }
            this.hAdjustable.setValue(value);
            this.vAdjustable.setValue(value2);
        }
    }
    
    public void setScrollPosition(final Point point) {
        this.setScrollPosition(point.x, point.y);
    }
    
    @Transient
    public Point getScrollPosition() {
        synchronized (this.getTreeLock()) {
            if (this.getComponentCount() == 0) {
                throw new NullPointerException("child is null");
            }
            return new Point(this.hAdjustable.getValue(), this.vAdjustable.getValue());
        }
    }
    
    @Override
    public final void setLayout(final LayoutManager layoutManager) {
        throw new AWTError("ScrollPane controls layout");
    }
    
    @Override
    public void doLayout() {
        this.layout();
    }
    
    Dimension calculateChildSize() {
        final Dimension size = this.getSize();
        final Insets insets = this.getInsets();
        int width = size.width - insets.left * 2;
        int height = size.height - insets.top * 2;
        final Dimension dimension = new Dimension(this.getComponent(0).getPreferredSize());
        boolean b;
        boolean b2;
        if (this.scrollbarDisplayPolicy == 0) {
            b = (dimension.height > height);
            b2 = (dimension.width > width);
        }
        else if (this.scrollbarDisplayPolicy == 1) {
            b2 = (b = true);
        }
        else {
            b2 = (b = false);
        }
        final int vScrollbarWidth = this.getVScrollbarWidth();
        final int hScrollbarHeight = this.getHScrollbarHeight();
        if (b) {
            width -= vScrollbarWidth;
        }
        if (b2) {
            height -= hScrollbarHeight;
        }
        if (dimension.width < width) {
            dimension.width = width;
        }
        if (dimension.height < height) {
            dimension.height = height;
        }
        return dimension;
    }
    
    @Deprecated
    @Override
    public void layout() {
        if (this.getComponentCount() == 0) {
            return;
        }
        final Component component = this.getComponent(0);
        final Point scrollPosition = this.getScrollPosition();
        final Dimension calculateChildSize = this.calculateChildSize();
        this.getViewportSize();
        component.reshape(-scrollPosition.x, -scrollPosition.y, calculateChildSize.width, calculateChildSize.height);
        final ScrollPanePeer scrollPanePeer = (ScrollPanePeer)this.peer;
        if (scrollPanePeer != null) {
            scrollPanePeer.childResized(calculateChildSize.width, calculateChildSize.height);
        }
        final Dimension viewportSize = this.getViewportSize();
        this.hAdjustable.setSpan(0, calculateChildSize.width, viewportSize.width);
        this.vAdjustable.setSpan(0, calculateChildSize.height, viewportSize.height);
    }
    
    @Override
    public void printComponents(final Graphics graphics) {
        if (this.getComponentCount() == 0) {
            return;
        }
        final Component component = this.getComponent(0);
        final Point location = component.getLocation();
        final Dimension viewportSize = this.getViewportSize();
        final Insets insets = this.getInsets();
        final Graphics create = graphics.create();
        try {
            create.clipRect(insets.left, insets.top, viewportSize.width, viewportSize.height);
            create.translate(location.x, location.y);
            component.printAll(create);
        }
        finally {
            create.dispose();
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            int value = 0;
            int value2 = 0;
            if (this.getComponentCount() > 0) {
                value = this.vAdjustable.getValue();
                value2 = this.hAdjustable.getValue();
                this.vAdjustable.setValue(0);
                this.hAdjustable.setValue(0);
            }
            if (this.peer == null) {
                this.peer = this.getToolkit().createScrollPane(this);
            }
            super.addNotify();
            if (this.getComponentCount() > 0) {
                this.vAdjustable.setValue(value);
                this.hAdjustable.setValue(value2);
            }
        }
    }
    
    public String paramString() {
        String s = null;
        switch (this.scrollbarDisplayPolicy) {
            case 0: {
                s = "as-needed";
                break;
            }
            case 1: {
                s = "always";
                break;
            }
            case 2: {
                s = "never";
                break;
            }
            default: {
                s = "invalid display policy";
                break;
            }
        }
        final Point point = (this.getComponentCount() > 0) ? this.getScrollPosition() : new Point(0, 0);
        final Insets insets = this.getInsets();
        return super.paramString() + ",ScrollPosition=(" + point.x + "," + point.y + "),Insets=(" + insets.top + "," + insets.left + "," + insets.bottom + "," + insets.right + "),ScrollbarDisplayPolicy=" + s + ",wheelScrollingEnabled=" + this.isWheelScrollingEnabled();
    }
    
    @Override
    void autoProcessMouseWheel(final MouseWheelEvent mouseWheelEvent) {
        this.processMouseWheelEvent(mouseWheelEvent);
    }
    
    @Override
    protected void processMouseWheelEvent(final MouseWheelEvent mouseWheelEvent) {
        if (this.isWheelScrollingEnabled()) {
            ScrollPaneWheelScroller.handleWheelScrolling(this, mouseWheelEvent);
            mouseWheelEvent.consume();
        }
        super.processMouseWheelEvent(mouseWheelEvent);
    }
    
    protected boolean eventTypeEnabled(final int n) {
        return (n == 507 && this.isWheelScrollingEnabled()) || super.eventTypeEnabled(n);
    }
    
    public void setWheelScrollingEnabled(final boolean wheelScrollingEnabled) {
        this.wheelScrollingEnabled = wheelScrollingEnabled;
    }
    
    public boolean isWheelScrollingEnabled() {
        return this.wheelScrollingEnabled;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        GraphicsEnvironment.checkHeadless();
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        this.scrollbarDisplayPolicy = fields.get("scrollbarDisplayPolicy", 0);
        this.hAdjustable = (ScrollPaneAdjustable)fields.get("hAdjustable", null);
        this.vAdjustable = (ScrollPaneAdjustable)fields.get("vAdjustable", null);
        this.wheelScrollingEnabled = fields.get("wheelScrollingEnabled", true);
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleAWTScrollPane();
        }
        return this.accessibleContext;
    }
    
    static {
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        ScrollPane.nameCounter = 0;
    }
    
    class PeerFixer implements AdjustmentListener, Serializable
    {
        private static final long serialVersionUID = 1043664721353696630L;
        private ScrollPane scroller;
        
        PeerFixer(final ScrollPane scroller) {
            this.scroller = scroller;
        }
        
        @Override
        public void adjustmentValueChanged(final AdjustmentEvent adjustmentEvent) {
            final Adjustable adjustable = adjustmentEvent.getAdjustable();
            final int value = adjustmentEvent.getValue();
            final ScrollPanePeer scrollPanePeer = (ScrollPanePeer)this.scroller.peer;
            if (scrollPanePeer != null) {
                scrollPanePeer.setValue(adjustable, value);
            }
            final Component component = this.scroller.getComponent(0);
            switch (adjustable.getOrientation()) {
                case 1: {
                    component.move(component.getLocation().x, -value);
                    break;
                }
                case 0: {
                    component.move(-value, component.getLocation().y);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Illegal adjustable orientation");
                }
            }
        }
    }
    
    protected class AccessibleAWTScrollPane extends AccessibleAWTContainer
    {
        private static final long serialVersionUID = 6100703663886637L;
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SCROLL_PANE;
        }
    }
}
