package javax.swing;

import java.io.InvalidObjectException;
import java.util.Iterator;
import java.util.Vector;
import java.io.ObjectInputValidation;
import java.awt.event.FocusEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.Cursor;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleKeyBinding;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleRole;
import javax.accessibility.Accessible;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import java.awt.event.FocusListener;
import javax.accessibility.AccessibleExtendedComponent;
import java.awt.event.ActionEvent;
import sun.awt.CausedFocusEvent;
import javax.accessibility.AccessibleContext;
import java.io.ObjectOutputStream;
import java.io.IOException;
import javax.swing.table.JTableHeader;
import java.util.ArrayList;
import sun.awt.SunToolkit;
import java.beans.PropertyChangeListener;
import java.util.EventListener;
import javax.swing.event.AncestorListener;
import java.beans.VetoableChangeListener;
import java.beans.PropertyVetoException;
import java.awt.peer.LightweightPeer;
import java.awt.AWTKeyStroke;
import javax.accessibility.AccessibleState;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;
import java.awt.Color;
import java.util.Enumeration;
import java.awt.event.ActionListener;
import javax.swing.border.AbstractBorder;
import java.awt.Insets;
import java.beans.Transient;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.FocusTraversalPolicy;
import sun.swing.UIClientPropertyKey;
import sun.swing.SwingUtilities2;
import java.applet.Applet;
import java.awt.Window;
import java.util.HashSet;
import java.awt.Graphics;
import sun.awt.RequestFocusController;
import java.awt.Rectangle;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.Component;
import javax.swing.border.Border;
import java.beans.VetoableChangeSupport;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.ComponentUI;
import java.util.Set;
import java.io.ObjectInputStream;
import java.util.Hashtable;
import java.io.Serializable;
import java.awt.Container;

public abstract class JComponent extends Container implements Serializable, TransferHandler.HasGetTransferHandler
{
    private static final String uiClassID = "ComponentUI";
    private static final Hashtable<ObjectInputStream, ReadObjectCallback> readObjectCallbacks;
    private static Set<KeyStroke> managingFocusForwardTraversalKeys;
    private static Set<KeyStroke> managingFocusBackwardTraversalKeys;
    private static final int NOT_OBSCURED = 0;
    private static final int PARTIALLY_OBSCURED = 1;
    private static final int COMPLETELY_OBSCURED = 2;
    static boolean DEBUG_GRAPHICS_LOADED;
    private static final Object INPUT_VERIFIER_SOURCE_KEY;
    private boolean isAlignmentXSet;
    private float alignmentX;
    private boolean isAlignmentYSet;
    private float alignmentY;
    protected transient ComponentUI ui;
    protected EventListenerList listenerList;
    private transient ArrayTable clientProperties;
    private VetoableChangeSupport vetoableChangeSupport;
    private boolean autoscrolls;
    private Border border;
    private int flags;
    private InputVerifier inputVerifier;
    private boolean verifyInputWhenFocusTarget;
    transient Component paintingChild;
    public static final int WHEN_FOCUSED = 0;
    public static final int WHEN_ANCESTOR_OF_FOCUSED_COMPONENT = 1;
    public static final int WHEN_IN_FOCUSED_WINDOW = 2;
    public static final int UNDEFINED_CONDITION = -1;
    private static final String KEYBOARD_BINDINGS_KEY = "_KeyboardBindings";
    private static final String WHEN_IN_FOCUSED_WINDOW_BINDINGS = "_WhenInFocusedWindow";
    public static final String TOOL_TIP_TEXT_KEY = "ToolTipText";
    private static final String NEXT_FOCUS = "nextFocus";
    private JPopupMenu popupMenu;
    private static final int IS_DOUBLE_BUFFERED = 0;
    private static final int ANCESTOR_USING_BUFFER = 1;
    private static final int IS_PAINTING_TILE = 2;
    private static final int IS_OPAQUE = 3;
    private static final int KEY_EVENTS_ENABLED = 4;
    private static final int FOCUS_INPUTMAP_CREATED = 5;
    private static final int ANCESTOR_INPUTMAP_CREATED = 6;
    private static final int WIF_INPUTMAP_CREATED = 7;
    private static final int ACTIONMAP_CREATED = 8;
    private static final int CREATED_DOUBLE_BUFFER = 9;
    private static final int IS_PRINTING = 11;
    private static final int IS_PRINTING_ALL = 12;
    private static final int IS_REPAINTING = 13;
    private static final int WRITE_OBJ_COUNTER_FIRST = 14;
    private static final int RESERVED_1 = 15;
    private static final int RESERVED_2 = 16;
    private static final int RESERVED_3 = 17;
    private static final int RESERVED_4 = 18;
    private static final int RESERVED_5 = 19;
    private static final int RESERVED_6 = 20;
    private static final int WRITE_OBJ_COUNTER_LAST = 21;
    private static final int REQUEST_FOCUS_DISABLED = 22;
    private static final int INHERITS_POPUP_MENU = 23;
    private static final int OPAQUE_SET = 24;
    private static final int AUTOSCROLLS_SET = 25;
    private static final int FOCUS_TRAVERSAL_KEYS_FORWARD_SET = 26;
    private static final int FOCUS_TRAVERSAL_KEYS_BACKWARD_SET = 27;
    private transient AtomicBoolean revalidateRunnableScheduled;
    private static List<Rectangle> tempRectangles;
    private InputMap focusInputMap;
    private InputMap ancestorInputMap;
    private ComponentInputMap windowInputMap;
    private ActionMap actionMap;
    private static final String defaultLocale = "JComponent.defaultLocale";
    private static Component componentObtainingGraphicsFrom;
    private static Object componentObtainingGraphicsFromLock;
    private transient Object aaTextInfo;
    static final RequestFocusController focusController;
    
    static Graphics safelyGetGraphics(final Component component) {
        return safelyGetGraphics(component, SwingUtilities.getRoot(component));
    }
    
    static Graphics safelyGetGraphics(final Component component, final Component componentObtainingGraphicsFrom) {
        synchronized (JComponent.componentObtainingGraphicsFromLock) {
            JComponent.componentObtainingGraphicsFrom = componentObtainingGraphicsFrom;
            final Graphics graphics = component.getGraphics();
            JComponent.componentObtainingGraphicsFrom = null;
            return graphics;
        }
    }
    
    static void getGraphicsInvoked(final Component component) {
        if (!isComponentObtainingGraphicsFrom(component)) {
            final JRootPane rootPane = ((RootPaneContainer)component).getRootPane();
            if (rootPane != null) {
                rootPane.disableTrueDoubleBuffering();
            }
        }
    }
    
    private static boolean isComponentObtainingGraphicsFrom(final Component component) {
        synchronized (JComponent.componentObtainingGraphicsFromLock) {
            return JComponent.componentObtainingGraphicsFrom == component;
        }
    }
    
    static Set<KeyStroke> getManagingFocusForwardTraversalKeys() {
        synchronized (JComponent.class) {
            if (JComponent.managingFocusForwardTraversalKeys == null) {
                (JComponent.managingFocusForwardTraversalKeys = new HashSet<KeyStroke>(1)).add(KeyStroke.getKeyStroke(9, 2));
            }
        }
        return JComponent.managingFocusForwardTraversalKeys;
    }
    
    static Set<KeyStroke> getManagingFocusBackwardTraversalKeys() {
        synchronized (JComponent.class) {
            if (JComponent.managingFocusBackwardTraversalKeys == null) {
                (JComponent.managingFocusBackwardTraversalKeys = new HashSet<KeyStroke>(1)).add(KeyStroke.getKeyStroke(9, 3));
            }
        }
        return JComponent.managingFocusBackwardTraversalKeys;
    }
    
    private static Rectangle fetchRectangle() {
        synchronized (JComponent.tempRectangles) {
            final int size = JComponent.tempRectangles.size();
            Rectangle rectangle;
            if (size > 0) {
                rectangle = JComponent.tempRectangles.remove(size - 1);
            }
            else {
                rectangle = new Rectangle(0, 0, 0, 0);
            }
            return rectangle;
        }
    }
    
    private static void recycleRectangle(final Rectangle rectangle) {
        synchronized (JComponent.tempRectangles) {
            JComponent.tempRectangles.add(rectangle);
        }
    }
    
    public void setInheritsPopupMenu(final boolean b) {
        final boolean flag = this.getFlag(23);
        this.setFlag(23, b);
        this.firePropertyChange("inheritsPopupMenu", flag, b);
    }
    
    public boolean getInheritsPopupMenu() {
        return this.getFlag(23);
    }
    
    public void setComponentPopupMenu(final JPopupMenu popupMenu) {
        if (popupMenu != null) {
            this.enableEvents(16L);
        }
        this.firePropertyChange("componentPopupMenu", this.popupMenu, this.popupMenu = popupMenu);
    }
    
    public JPopupMenu getComponentPopupMenu() {
        if (!this.getInheritsPopupMenu()) {
            return this.popupMenu;
        }
        if (this.popupMenu == null) {
            for (Container container = this.getParent(); container != null; container = container.getParent()) {
                if (container instanceof JComponent) {
                    return ((JComponent)container).getComponentPopupMenu();
                }
                if (container instanceof Window) {
                    break;
                }
                if (container instanceof Applet) {
                    break;
                }
            }
            return null;
        }
        return this.popupMenu;
    }
    
    public JComponent() {
        this.listenerList = new EventListenerList();
        this.inputVerifier = null;
        this.verifyInputWhenFocusTarget = true;
        this.revalidateRunnableScheduled = new AtomicBoolean(false);
        this.enableEvents(8L);
        if (this.isManagingFocus()) {
            LookAndFeel.installProperty(this, "focusTraversalKeysForward", getManagingFocusForwardTraversalKeys());
            LookAndFeel.installProperty(this, "focusTraversalKeysBackward", getManagingFocusBackwardTraversalKeys());
        }
        super.setLocale(getDefaultLocale());
    }
    
    public void updateUI() {
    }
    
    protected void setUI(final ComponentUI ui) {
        this.uninstallUIAndProperties();
        this.aaTextInfo = UIManager.getDefaults().get(SwingUtilities2.AA_TEXT_PROPERTY_KEY);
        final ComponentUI ui2 = this.ui;
        this.ui = ui;
        if (this.ui != null) {
            this.ui.installUI(this);
        }
        this.firePropertyChange("UI", ui2, ui);
        this.revalidate();
        this.repaint();
    }
    
    private void uninstallUIAndProperties() {
        if (this.ui != null) {
            this.ui.uninstallUI(this);
            if (this.clientProperties != null) {
                synchronized (this.clientProperties) {
                    final Object[] keys = this.clientProperties.getKeys(null);
                    if (keys != null) {
                        for (final Object o : keys) {
                            if (o instanceof UIClientPropertyKey) {
                                this.putClientProperty(o, null);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public String getUIClassID() {
        return "ComponentUI";
    }
    
    protected Graphics getComponentGraphics(final Graphics graphics) {
        Graphics graphics2 = graphics;
        if (this.ui != null && JComponent.DEBUG_GRAPHICS_LOADED && DebugGraphics.debugComponentCount() != 0 && this.shouldDebugGraphics() != 0 && !(graphics instanceof DebugGraphics)) {
            graphics2 = new DebugGraphics(graphics, this);
        }
        graphics2.setColor(this.getForeground());
        graphics2.setFont(this.getFont());
        return graphics2;
    }
    
    protected void paintComponent(final Graphics graphics) {
        if (this.ui != null) {
            final Graphics graphics2 = (graphics == null) ? null : graphics.create();
            try {
                this.ui.update(graphics2, this);
            }
            finally {
                graphics2.dispose();
            }
        }
    }
    
    protected void paintChildren(final Graphics graphics) {
        synchronized (this.getTreeLock()) {
            int i = this.getComponentCount() - 1;
            if (i < 0) {
                return;
            }
            if (this.paintingChild != null && this.paintingChild instanceof JComponent && this.paintingChild.isOpaque()) {
                while (i >= 0) {
                    if (this.getComponent(i) == this.paintingChild) {
                        break;
                    }
                    --i;
                }
            }
            final Rectangle fetchRectangle = fetchRectangle();
            final boolean b = !this.isOptimizedDrawingEnabled() && this.checkIfChildObscuredBySibling();
            Rectangle clipBounds = null;
            if (b) {
                clipBounds = graphics.getClipBounds();
                if (clipBounds == null) {
                    clipBounds = new Rectangle(0, 0, this.getWidth(), this.getHeight());
                }
            }
            final boolean flag = this.getFlag(11);
            final Window windowAncestor = SwingUtilities.getWindowAncestor(this);
            final boolean b2 = windowAncestor == null || windowAncestor.isOpaque();
            while (i >= 0) {
                final Component component = this.getComponent(i);
                Label_0645: {
                    if (component != null) {
                        final boolean b3 = component instanceof JComponent;
                        if ((!b2 || b3 || isLightweightComponent(component)) && component.isVisible()) {
                            final Rectangle bounds = component.getBounds(fetchRectangle);
                            if (graphics.hitClip(bounds.x, bounds.y, bounds.width, bounds.height)) {
                                if (b && i > 0) {
                                    final int x = bounds.x;
                                    final int y = bounds.y;
                                    final int width = bounds.width;
                                    final int height = bounds.height;
                                    SwingUtilities.computeIntersection(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height, bounds);
                                    if (this.getObscuredState(i, bounds.x, bounds.y, bounds.width, bounds.height) == 2) {
                                        break Label_0645;
                                    }
                                    bounds.x = x;
                                    bounds.y = y;
                                    bounds.width = width;
                                    bounds.height = height;
                                }
                                final Graphics create = graphics.create(bounds.x, bounds.y, bounds.width, bounds.height);
                                create.setColor(component.getForeground());
                                create.setFont(component.getFont());
                                boolean b4 = false;
                                try {
                                    if (b3) {
                                        if (this.getFlag(1)) {
                                            ((JComponent)component).setFlag(1, true);
                                            b4 = true;
                                        }
                                        if (this.getFlag(2)) {
                                            ((JComponent)component).setFlag(2, true);
                                            b4 = true;
                                        }
                                        if (!flag) {
                                            component.paint(create);
                                        }
                                        else if (!this.getFlag(12)) {
                                            component.print(create);
                                        }
                                        else {
                                            component.printAll(create);
                                        }
                                    }
                                    else if (!flag) {
                                        component.paint(create);
                                    }
                                    else if (!this.getFlag(12)) {
                                        component.print(create);
                                    }
                                    else {
                                        component.printAll(create);
                                    }
                                }
                                finally {
                                    create.dispose();
                                    if (b4) {
                                        ((JComponent)component).setFlag(1, false);
                                        ((JComponent)component).setFlag(2, false);
                                    }
                                }
                            }
                        }
                    }
                }
                --i;
            }
            recycleRectangle(fetchRectangle);
        }
    }
    
    protected void paintBorder(final Graphics graphics) {
        final Border border = this.getBorder();
        if (border != null) {
            border.paintBorder(this, graphics, 0, 0, this.getWidth(), this.getHeight());
        }
    }
    
    @Override
    public void update(final Graphics graphics) {
        this.paint(graphics);
    }
    
    @Override
    public void paint(final Graphics graphics) {
        boolean b = false;
        if (this.getWidth() <= 0 || this.getHeight() <= 0) {
            return;
        }
        final Graphics create = this.getComponentGraphics(graphics).create();
        try {
            final RepaintManager currentManager = RepaintManager.currentManager(this);
            final Rectangle clipBounds = create.getClipBounds();
            int x;
            int y;
            int n;
            int n2;
            if (clipBounds == null) {
                y = (x = 0);
                n = this.getWidth();
                n2 = this.getHeight();
            }
            else {
                x = clipBounds.x;
                y = clipBounds.y;
                n = clipBounds.width;
                n2 = clipBounds.height;
            }
            if (n > this.getWidth()) {
                n = this.getWidth();
            }
            if (n2 > this.getHeight()) {
                n2 = this.getHeight();
            }
            if (this.getParent() != null && !(this.getParent() instanceof JComponent)) {
                this.adjustPaintFlags();
                b = true;
            }
            final boolean flag = this.getFlag(11);
            if (!flag && currentManager.isDoubleBufferingEnabled() && !this.getFlag(1) && this.isDoubleBuffered() && (this.getFlag(13) || currentManager.isPainting())) {
                currentManager.beginPaint();
                try {
                    currentManager.paint(this, this, create, x, y, n, n2);
                }
                finally {
                    currentManager.endPaint();
                }
            }
            else {
                if (clipBounds == null) {
                    create.setClip(x, y, n, n2);
                }
                if (!this.rectangleIsObscured(x, y, n, n2)) {
                    if (!flag) {
                        this.paintComponent(create);
                        this.paintBorder(create);
                    }
                    else {
                        this.printComponent(create);
                        this.printBorder(create);
                    }
                }
                if (!flag) {
                    this.paintChildren(create);
                }
                else {
                    this.printChildren(create);
                }
            }
        }
        finally {
            create.dispose();
            if (b) {
                this.setFlag(1, false);
                this.setFlag(2, false);
                this.setFlag(11, false);
                this.setFlag(12, false);
            }
        }
    }
    
    void paintForceDoubleBuffered(final Graphics graphics) {
        final RepaintManager currentManager = RepaintManager.currentManager(this);
        final Rectangle clipBounds = graphics.getClipBounds();
        currentManager.beginPaint();
        this.setFlag(13, true);
        try {
            currentManager.paint(this, this, graphics, clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        }
        finally {
            currentManager.endPaint();
            this.setFlag(13, false);
        }
    }
    
    boolean isPainting() {
        for (Container parent = this; parent != null; parent = parent.getParent()) {
            if (parent instanceof JComponent && ((JComponent)parent).getFlag(1)) {
                return true;
            }
        }
        return false;
    }
    
    private void adjustPaintFlags() {
        Container container = this.getParent();
        while (container != null) {
            if (container instanceof JComponent) {
                final JComponent component = (JComponent)container;
                if (component.getFlag(1)) {
                    this.setFlag(1, true);
                }
                if (component.getFlag(2)) {
                    this.setFlag(2, true);
                }
                if (component.getFlag(11)) {
                    this.setFlag(11, true);
                }
                if (component.getFlag(12)) {
                    this.setFlag(12, true);
                    break;
                }
                break;
            }
            else {
                container = container.getParent();
            }
        }
    }
    
    @Override
    public void printAll(final Graphics graphics) {
        this.setFlag(12, true);
        try {
            this.print(graphics);
        }
        finally {
            this.setFlag(12, false);
        }
    }
    
    @Override
    public void print(final Graphics graphics) {
        this.setFlag(11, true);
        this.firePropertyChange("paintingForPrint", false, true);
        try {
            this.paint(graphics);
        }
        finally {
            this.setFlag(11, false);
            this.firePropertyChange("paintingForPrint", true, false);
        }
    }
    
    protected void printComponent(final Graphics graphics) {
        this.paintComponent(graphics);
    }
    
    protected void printChildren(final Graphics graphics) {
        this.paintChildren(graphics);
    }
    
    protected void printBorder(final Graphics graphics) {
        this.paintBorder(graphics);
    }
    
    public boolean isPaintingTile() {
        return this.getFlag(2);
    }
    
    public final boolean isPaintingForPrint() {
        return this.getFlag(11);
    }
    
    @Deprecated
    public boolean isManagingFocus() {
        return false;
    }
    
    private void registerNextFocusableComponent() {
        this.registerNextFocusableComponent(this.getNextFocusableComponent());
    }
    
    private void registerNextFocusableComponent(final Component component) {
        if (component == null) {
            return;
        }
        final Container container = this.isFocusCycleRoot() ? this : this.getFocusCycleRootAncestor();
        FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
        if (!(focusTraversalPolicy instanceof LegacyGlueFocusTraversalPolicy)) {
            focusTraversalPolicy = new LegacyGlueFocusTraversalPolicy(focusTraversalPolicy);
            container.setFocusTraversalPolicy(focusTraversalPolicy);
        }
        ((LegacyGlueFocusTraversalPolicy)focusTraversalPolicy).setNextFocusableComponent(this, component);
    }
    
    private void deregisterNextFocusableComponent() {
        final Component nextFocusableComponent = this.getNextFocusableComponent();
        if (nextFocusableComponent == null) {
            return;
        }
        final Container container = this.isFocusCycleRoot() ? this : this.getFocusCycleRootAncestor();
        if (container == null) {
            return;
        }
        final FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
        if (focusTraversalPolicy instanceof LegacyGlueFocusTraversalPolicy) {
            ((LegacyGlueFocusTraversalPolicy)focusTraversalPolicy).unsetNextFocusableComponent(this, nextFocusableComponent);
        }
    }
    
    @Deprecated
    public void setNextFocusableComponent(final Component component) {
        final boolean displayable = this.isDisplayable();
        if (displayable) {
            this.deregisterNextFocusableComponent();
        }
        this.putClientProperty("nextFocus", component);
        if (displayable) {
            this.registerNextFocusableComponent(component);
        }
    }
    
    @Deprecated
    public Component getNextFocusableComponent() {
        return (Component)this.getClientProperty("nextFocus");
    }
    
    public void setRequestFocusEnabled(final boolean b) {
        this.setFlag(22, !b);
    }
    
    public boolean isRequestFocusEnabled() {
        return !this.getFlag(22);
    }
    
    @Override
    public void requestFocus() {
        super.requestFocus();
    }
    
    public boolean requestFocus(final boolean b) {
        return super.requestFocus(b);
    }
    
    @Override
    public boolean requestFocusInWindow() {
        return super.requestFocusInWindow();
    }
    
    @Override
    protected boolean requestFocusInWindow(final boolean b) {
        return super.requestFocusInWindow(b);
    }
    
    public void grabFocus() {
        this.requestFocus();
    }
    
    public void setVerifyInputWhenFocusTarget(final boolean verifyInputWhenFocusTarget) {
        this.firePropertyChange("verifyInputWhenFocusTarget", this.verifyInputWhenFocusTarget, this.verifyInputWhenFocusTarget = verifyInputWhenFocusTarget);
    }
    
    public boolean getVerifyInputWhenFocusTarget() {
        return this.verifyInputWhenFocusTarget;
    }
    
    @Override
    public FontMetrics getFontMetrics(final Font font) {
        return SwingUtilities2.getFontMetrics(this, font);
    }
    
    @Override
    public void setPreferredSize(final Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
    }
    
    @Transient
    @Override
    public Dimension getPreferredSize() {
        if (this.isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        Dimension preferredSize = null;
        if (this.ui != null) {
            preferredSize = this.ui.getPreferredSize(this);
        }
        return (preferredSize != null) ? preferredSize : super.getPreferredSize();
    }
    
    @Override
    public void setMaximumSize(final Dimension maximumSize) {
        super.setMaximumSize(maximumSize);
    }
    
    @Transient
    @Override
    public Dimension getMaximumSize() {
        if (this.isMaximumSizeSet()) {
            return super.getMaximumSize();
        }
        Dimension maximumSize = null;
        if (this.ui != null) {
            maximumSize = this.ui.getMaximumSize(this);
        }
        return (maximumSize != null) ? maximumSize : super.getMaximumSize();
    }
    
    @Override
    public void setMinimumSize(final Dimension minimumSize) {
        super.setMinimumSize(minimumSize);
    }
    
    @Transient
    @Override
    public Dimension getMinimumSize() {
        if (this.isMinimumSizeSet()) {
            return super.getMinimumSize();
        }
        Dimension minimumSize = null;
        if (this.ui != null) {
            minimumSize = this.ui.getMinimumSize(this);
        }
        return (minimumSize != null) ? minimumSize : super.getMinimumSize();
    }
    
    @Override
    public boolean contains(final int n, final int n2) {
        return (this.ui != null) ? this.ui.contains(this, n, n2) : super.contains(n, n2);
    }
    
    public void setBorder(final Border border) {
        final Border border2 = this.border;
        this.firePropertyChange("border", border2, this.border = border);
        if (border != border2) {
            if (border == null || border2 == null || !border.getBorderInsets(this).equals(border2.getBorderInsets(this))) {
                this.revalidate();
            }
            this.repaint();
        }
    }
    
    public Border getBorder() {
        return this.border;
    }
    
    @Override
    public Insets getInsets() {
        if (this.border != null) {
            return this.border.getBorderInsets(this);
        }
        return super.getInsets();
    }
    
    public Insets getInsets(Insets insets) {
        if (insets == null) {
            insets = new Insets(0, 0, 0, 0);
        }
        if (this.border == null) {
            final Insets insets2 = insets;
            final Insets insets3 = insets;
            final Insets insets4 = insets;
            final Insets insets5 = insets;
            final int n = 0;
            insets5.bottom = n;
            insets4.right = n;
            insets3.top = n;
            insets2.left = n;
            return insets;
        }
        if (this.border instanceof AbstractBorder) {
            return ((AbstractBorder)this.border).getBorderInsets(this, insets);
        }
        return this.border.getBorderInsets(this);
    }
    
    @Override
    public float getAlignmentY() {
        if (this.isAlignmentYSet) {
            return this.alignmentY;
        }
        return super.getAlignmentY();
    }
    
    public void setAlignmentY(final float n) {
        this.alignmentY = ((n > 1.0f) ? 1.0f : ((n < 0.0f) ? 0.0f : n));
        this.isAlignmentYSet = true;
    }
    
    @Override
    public float getAlignmentX() {
        if (this.isAlignmentXSet) {
            return this.alignmentX;
        }
        return super.getAlignmentX();
    }
    
    public void setAlignmentX(final float n) {
        this.alignmentX = ((n > 1.0f) ? 1.0f : ((n < 0.0f) ? 0.0f : n));
        this.isAlignmentXSet = true;
    }
    
    public void setInputVerifier(final InputVerifier inputVerifier) {
        final InputVerifier inputVerifier2 = (InputVerifier)this.getClientProperty(ClientPropertyKey.JComponent_INPUT_VERIFIER);
        this.putClientProperty(ClientPropertyKey.JComponent_INPUT_VERIFIER, inputVerifier);
        this.firePropertyChange("inputVerifier", inputVerifier2, inputVerifier);
    }
    
    public InputVerifier getInputVerifier() {
        return (InputVerifier)this.getClientProperty(ClientPropertyKey.JComponent_INPUT_VERIFIER);
    }
    
    @Override
    public Graphics getGraphics() {
        if (JComponent.DEBUG_GRAPHICS_LOADED && this.shouldDebugGraphics() != 0) {
            return new DebugGraphics(super.getGraphics(), this);
        }
        return super.getGraphics();
    }
    
    public void setDebugGraphicsOptions(final int n) {
        DebugGraphics.setDebugOptions(this, n);
    }
    
    public int getDebugGraphicsOptions() {
        return DebugGraphics.getDebugOptions(this);
    }
    
    int shouldDebugGraphics() {
        return DebugGraphics.shouldComponentDebug(this);
    }
    
    public void registerKeyboardAction(final ActionListener actionListener, final String s, final KeyStroke keyStroke, final int n) {
        final InputMap inputMap = this.getInputMap(n, true);
        if (inputMap != null) {
            final ActionMap actionMap = this.getActionMap(true);
            final ActionStandin actionStandin = new ActionStandin(actionListener, s);
            inputMap.put(keyStroke, actionStandin);
            if (actionMap != null) {
                actionMap.put(actionStandin, actionStandin);
            }
        }
    }
    
    private void registerWithKeyboardManager(final boolean b) {
        final InputMap inputMap = this.getInputMap(2, false);
        Object o = this.getClientProperty("_WhenInFocusedWindow");
        KeyStroke[] allKeys;
        if (inputMap != null) {
            allKeys = inputMap.allKeys();
            if (allKeys != null) {
                for (int i = allKeys.length - 1; i >= 0; --i) {
                    if (!b || o == null || ((Hashtable)o).get(allKeys[i]) == null) {
                        this.registerWithKeyboardManager(allKeys[i]);
                    }
                    if (o != null) {
                        ((Hashtable)o).remove(allKeys[i]);
                    }
                }
            }
        }
        else {
            allKeys = null;
        }
        if (o != null && ((Hashtable)o).size() > 0) {
            final Enumeration keys = ((Hashtable)o).keys();
            while (keys.hasMoreElements()) {
                this.unregisterWithKeyboardManager((KeyStroke)keys.nextElement());
            }
            ((Hashtable)o).clear();
        }
        if (allKeys != null && allKeys.length > 0) {
            if (o == null) {
                o = new Hashtable(allKeys.length);
                this.putClientProperty("_WhenInFocusedWindow", o);
            }
            for (int j = allKeys.length - 1; j >= 0; --j) {
                ((Hashtable)o).put(allKeys[j], allKeys[j]);
            }
        }
        else {
            this.putClientProperty("_WhenInFocusedWindow", null);
        }
    }
    
    private void unregisterWithKeyboardManager() {
        final Hashtable hashtable = (Hashtable)this.getClientProperty("_WhenInFocusedWindow");
        if (hashtable != null && hashtable.size() > 0) {
            final Enumeration keys = hashtable.keys();
            while (keys.hasMoreElements()) {
                this.unregisterWithKeyboardManager((KeyStroke)keys.nextElement());
            }
        }
        this.putClientProperty("_WhenInFocusedWindow", null);
    }
    
    void componentInputMapChanged(final ComponentInputMap componentInputMap) {
        InputMap inputMap;
        for (inputMap = this.getInputMap(2, false); inputMap != componentInputMap && inputMap != null; inputMap = inputMap.getParent()) {}
        if (inputMap != null) {
            this.registerWithKeyboardManager(false);
        }
    }
    
    private void registerWithKeyboardManager(final KeyStroke keyStroke) {
        KeyboardManager.getCurrentManager().registerKeyStroke(keyStroke, this);
    }
    
    private void unregisterWithKeyboardManager(final KeyStroke keyStroke) {
        KeyboardManager.getCurrentManager().unregisterKeyStroke(keyStroke, this);
    }
    
    public void registerKeyboardAction(final ActionListener actionListener, final KeyStroke keyStroke, final int n) {
        this.registerKeyboardAction(actionListener, null, keyStroke, n);
    }
    
    public void unregisterKeyboardAction(final KeyStroke keyStroke) {
        final ActionMap actionMap = this.getActionMap(false);
        for (int i = 0; i < 3; ++i) {
            final InputMap inputMap = this.getInputMap(i, false);
            if (inputMap != null) {
                final Object value = inputMap.get(keyStroke);
                if (actionMap != null && value != null) {
                    actionMap.remove(value);
                }
                inputMap.remove(keyStroke);
            }
        }
    }
    
    public KeyStroke[] getRegisteredKeyStrokes() {
        final int[] array = new int[3];
        final KeyStroke[][] array2 = new KeyStroke[3][];
        for (int i = 0; i < 3; ++i) {
            final InputMap inputMap = this.getInputMap(i, false);
            array2[i] = (KeyStroke[])((inputMap != null) ? inputMap.allKeys() : null);
            array[i] = ((array2[i] != null) ? array2[i].length : 0);
        }
        final KeyStroke[] array3 = new KeyStroke[array[0] + array[1] + array[2]];
        int j = 0;
        int n = 0;
        while (j < 3) {
            if (array[j] > 0) {
                System.arraycopy(array2[j], 0, array3, n, array[j]);
                n += array[j];
            }
            ++j;
        }
        return array3;
    }
    
    public int getConditionForKeyStroke(final KeyStroke keyStroke) {
        for (int i = 0; i < 3; ++i) {
            final InputMap inputMap = this.getInputMap(i, false);
            if (inputMap != null && inputMap.get(keyStroke) != null) {
                return i;
            }
        }
        return -1;
    }
    
    public ActionListener getActionForKeyStroke(final KeyStroke keyStroke) {
        final ActionMap actionMap = this.getActionMap(false);
        if (actionMap == null) {
            return null;
        }
        for (int i = 0; i < 3; ++i) {
            final InputMap inputMap = this.getInputMap(i, false);
            if (inputMap != null) {
                final Object value = inputMap.get(keyStroke);
                if (value != null) {
                    final Action value2 = actionMap.get(value);
                    if (value2 instanceof ActionStandin) {
                        return ((ActionStandin)value2).actionListener;
                    }
                    return value2;
                }
            }
        }
        return null;
    }
    
    public void resetKeyboardActions() {
        for (int i = 0; i < 3; ++i) {
            final InputMap inputMap = this.getInputMap(i, false);
            if (inputMap != null) {
                inputMap.clear();
            }
        }
        final ActionMap actionMap = this.getActionMap(false);
        if (actionMap != null) {
            actionMap.clear();
        }
    }
    
    public final void setInputMap(final int n, final InputMap inputMap) {
        switch (n) {
            case 2: {
                if (inputMap != null && !(inputMap instanceof ComponentInputMap)) {
                    throw new IllegalArgumentException("WHEN_IN_FOCUSED_WINDOW InputMaps must be of type ComponentInputMap");
                }
                this.windowInputMap = (ComponentInputMap)inputMap;
                this.setFlag(7, true);
                this.registerWithKeyboardManager(false);
                break;
            }
            case 1: {
                this.ancestorInputMap = inputMap;
                this.setFlag(6, true);
                break;
            }
            case 0: {
                this.focusInputMap = inputMap;
                this.setFlag(5, true);
                break;
            }
            default: {
                throw new IllegalArgumentException("condition must be one of JComponent.WHEN_IN_FOCUSED_WINDOW, JComponent.WHEN_FOCUSED or JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT");
            }
        }
    }
    
    public final InputMap getInputMap(final int n) {
        return this.getInputMap(n, true);
    }
    
    public final InputMap getInputMap() {
        return this.getInputMap(0, true);
    }
    
    public final void setActionMap(final ActionMap actionMap) {
        this.actionMap = actionMap;
        this.setFlag(8, true);
    }
    
    public final ActionMap getActionMap() {
        return this.getActionMap(true);
    }
    
    final InputMap getInputMap(final int n, final boolean b) {
        switch (n) {
            case 0: {
                if (this.getFlag(5)) {
                    return this.focusInputMap;
                }
                if (b) {
                    final InputMap inputMap = new InputMap();
                    this.setInputMap(n, inputMap);
                    return inputMap;
                }
                break;
            }
            case 1: {
                if (this.getFlag(6)) {
                    return this.ancestorInputMap;
                }
                if (b) {
                    final InputMap inputMap2 = new InputMap();
                    this.setInputMap(n, inputMap2);
                    return inputMap2;
                }
                break;
            }
            case 2: {
                if (this.getFlag(7)) {
                    return this.windowInputMap;
                }
                if (b) {
                    final ComponentInputMap componentInputMap = new ComponentInputMap(this);
                    this.setInputMap(n, componentInputMap);
                    return componentInputMap;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("condition must be one of JComponent.WHEN_IN_FOCUSED_WINDOW, JComponent.WHEN_FOCUSED or JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT");
            }
        }
        return null;
    }
    
    final ActionMap getActionMap(final boolean b) {
        if (this.getFlag(8)) {
            return this.actionMap;
        }
        if (b) {
            final ActionMap actionMap = new ActionMap();
            this.setActionMap(actionMap);
            return actionMap;
        }
        return null;
    }
    
    @Override
    public int getBaseline(final int n, final int n2) {
        super.getBaseline(n, n2);
        if (this.ui != null) {
            return this.ui.getBaseline(this, n, n2);
        }
        return -1;
    }
    
    @Override
    public BaselineResizeBehavior getBaselineResizeBehavior() {
        if (this.ui != null) {
            return this.ui.getBaselineResizeBehavior(this);
        }
        return BaselineResizeBehavior.OTHER;
    }
    
    @Deprecated
    public boolean requestDefaultFocus() {
        final Container container = this.isFocusCycleRoot() ? this : this.getFocusCycleRootAncestor();
        if (container == null) {
            return false;
        }
        final Component defaultComponent = container.getFocusTraversalPolicy().getDefaultComponent(container);
        if (defaultComponent != null) {
            defaultComponent.requestFocus();
            return true;
        }
        return false;
    }
    
    @Override
    public void setVisible(final boolean visible) {
        if (visible != this.isVisible()) {
            super.setVisible(visible);
            if (visible) {
                final Container parent = this.getParent();
                if (parent != null) {
                    final Rectangle bounds = this.getBounds();
                    parent.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
                }
                this.revalidate();
            }
        }
    }
    
    @Override
    public void setEnabled(final boolean enabled) {
        final boolean enabled2 = this.isEnabled();
        super.setEnabled(enabled);
        this.firePropertyChange("enabled", enabled2, enabled);
        if (enabled != enabled2) {
            this.repaint();
        }
    }
    
    @Override
    public void setForeground(final Color foreground) {
        final Color foreground2 = this.getForeground();
        super.setForeground(foreground);
        if (foreground2 != null) {
            if (foreground2.equals(foreground)) {
                return;
            }
        }
        else if (foreground == null || foreground.equals(foreground2)) {
            return;
        }
        this.repaint();
    }
    
    @Override
    public void setBackground(final Color background) {
        final Color background2 = this.getBackground();
        super.setBackground(background);
        if (background2 != null) {
            if (background2.equals(background)) {
                return;
            }
        }
        else if (background == null || background.equals(background2)) {
            return;
        }
        this.repaint();
    }
    
    @Override
    public void setFont(final Font font) {
        final Font font2 = this.getFont();
        super.setFont(font);
        if (font != font2) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public static Locale getDefaultLocale() {
        Locale default1 = (Locale)SwingUtilities.appContextGet("JComponent.defaultLocale");
        if (default1 == null) {
            default1 = Locale.getDefault();
            setDefaultLocale(default1);
        }
        return default1;
    }
    
    public static void setDefaultLocale(final Locale locale) {
        SwingUtilities.appContextPut("JComponent.defaultLocale", locale);
    }
    
    protected void processComponentKeyEvent(final KeyEvent keyEvent) {
    }
    
    @Override
    protected void processKeyEvent(final KeyEvent keyEvent) {
        super.processKeyEvent(keyEvent);
        if (!keyEvent.isConsumed()) {
            this.processComponentKeyEvent(keyEvent);
        }
        final boolean shouldProcess = KeyboardState.shouldProcess(keyEvent);
        if (keyEvent.isConsumed()) {
            return;
        }
        if (shouldProcess && this.processKeyBindings(keyEvent, keyEvent.getID() == 401)) {
            keyEvent.consume();
        }
    }
    
    protected boolean processKeyBinding(final KeyStroke keyStroke, final KeyEvent keyEvent, final int n, final boolean b) {
        final InputMap inputMap = this.getInputMap(n, false);
        final ActionMap actionMap = this.getActionMap(false);
        if (inputMap != null && actionMap != null && this.isEnabled()) {
            final Object value = inputMap.get(keyStroke);
            final Action action = (value == null) ? null : actionMap.get(value);
            if (action != null) {
                return SwingUtilities.notifyAction(action, keyStroke, keyEvent, this, keyEvent.getModifiers());
            }
        }
        return false;
    }
    
    boolean processKeyBindings(final KeyEvent keyEvent, final boolean b) {
        if (!SwingUtilities.isValidKeyEventForKeyBindings(keyEvent)) {
            return false;
        }
        KeyStroke keyStroke = null;
        KeyStroke keyStroke2;
        if (keyEvent.getID() == 400) {
            keyStroke2 = KeyStroke.getKeyStroke(keyEvent.getKeyChar());
        }
        else {
            keyStroke2 = KeyStroke.getKeyStroke(keyEvent.getKeyCode(), keyEvent.getModifiers(), !b);
            if (keyEvent.getKeyCode() != keyEvent.getExtendedKeyCode()) {
                keyStroke = KeyStroke.getKeyStroke(keyEvent.getExtendedKeyCode(), keyEvent.getModifiers(), !b);
            }
        }
        if (keyStroke != null && this.processKeyBinding(keyStroke, keyEvent, 0, b)) {
            return true;
        }
        if (this.processKeyBinding(keyStroke2, keyEvent, 0, b)) {
            return true;
        }
        Container parent;
        for (parent = this; parent != null && !(parent instanceof Window) && !(parent instanceof Applet); parent = parent.getParent()) {
            if (parent instanceof JComponent) {
                if (keyStroke != null && ((JComponent)parent).processKeyBinding(keyStroke, keyEvent, 1, b)) {
                    return true;
                }
                if (((JComponent)parent).processKeyBinding(keyStroke2, keyEvent, 1, b)) {
                    return true;
                }
            }
            if (parent instanceof JInternalFrame && processKeyBindingsForAllComponents(keyEvent, parent, b)) {
                return true;
            }
        }
        return parent != null && processKeyBindingsForAllComponents(keyEvent, parent, b);
    }
    
    static boolean processKeyBindingsForAllComponents(final KeyEvent keyEvent, Container owner, final boolean b) {
        while (!KeyboardManager.getCurrentManager().fireKeyboardAction(keyEvent, b, owner)) {
            if (!(owner instanceof Popup.HeavyWeightWindow)) {
                return false;
            }
            owner = ((Window)owner).getOwner();
        }
        return true;
    }
    
    public void setToolTipText(final String s) {
        final String toolTipText = this.getToolTipText();
        this.putClientProperty("ToolTipText", s);
        final ToolTipManager sharedInstance = ToolTipManager.sharedInstance();
        if (s != null) {
            if (toolTipText == null) {
                sharedInstance.registerComponent(this);
            }
        }
        else {
            sharedInstance.unregisterComponent(this);
        }
    }
    
    public String getToolTipText() {
        return (String)this.getClientProperty("ToolTipText");
    }
    
    public String getToolTipText(final MouseEvent mouseEvent) {
        return this.getToolTipText();
    }
    
    public Point getToolTipLocation(final MouseEvent mouseEvent) {
        return null;
    }
    
    public Point getPopupLocation(final MouseEvent mouseEvent) {
        return null;
    }
    
    public JToolTip createToolTip() {
        final JToolTip toolTip = new JToolTip();
        toolTip.setComponent(this);
        return toolTip;
    }
    
    public void scrollRectToVisible(final Rectangle rectangle) {
        int x = this.getX();
        int y = this.getY();
        Container container;
        for (container = this.getParent(); container != null && !(container instanceof JComponent) && !(container instanceof CellRendererPane); container = container.getParent()) {
            final Rectangle bounds = container.getBounds();
            x += bounds.x;
            y += bounds.y;
        }
        if (container != null && !(container instanceof CellRendererPane)) {
            rectangle.x += x;
            rectangle.y += y;
            ((JComponent)container).scrollRectToVisible(rectangle);
            rectangle.x -= x;
            rectangle.y -= y;
        }
    }
    
    public void setAutoscrolls(final boolean autoscrolls) {
        this.setFlag(25, true);
        if (this.autoscrolls != autoscrolls) {
            this.autoscrolls = autoscrolls;
            if (autoscrolls) {
                this.enableEvents(16L);
                this.enableEvents(32L);
            }
            else {
                Autoscroller.stop(this);
            }
        }
    }
    
    public boolean getAutoscrolls() {
        return this.autoscrolls;
    }
    
    public void setTransferHandler(final TransferHandler transferHandler) {
        final TransferHandler transferHandler2 = (TransferHandler)this.getClientProperty(ClientPropertyKey.JComponent_TRANSFER_HANDLER);
        this.putClientProperty(ClientPropertyKey.JComponent_TRANSFER_HANDLER, transferHandler);
        SwingUtilities.installSwingDropTargetAsNecessary(this, transferHandler);
        this.firePropertyChange("transferHandler", transferHandler2, transferHandler);
    }
    
    @Override
    public TransferHandler getTransferHandler() {
        return (TransferHandler)this.getClientProperty(ClientPropertyKey.JComponent_TRANSFER_HANDLER);
    }
    
    TransferHandler.DropLocation dropLocationForPoint(final Point point) {
        return null;
    }
    
    Object setDropLocation(final TransferHandler.DropLocation dropLocation, final Object o, final boolean b) {
        return null;
    }
    
    void dndDone() {
    }
    
    @Override
    protected void processMouseEvent(final MouseEvent mouseEvent) {
        if (this.autoscrolls && mouseEvent.getID() == 502) {
            Autoscroller.stop(this);
        }
        super.processMouseEvent(mouseEvent);
    }
    
    @Override
    protected void processMouseMotionEvent(final MouseEvent mouseEvent) {
        int n = 1;
        if (this.autoscrolls && mouseEvent.getID() == 506) {
            n = (Autoscroller.isRunning(this) ? 0 : 1);
            Autoscroller.processMouseDragged(mouseEvent);
        }
        if (n != 0) {
            super.processMouseMotionEvent(mouseEvent);
        }
    }
    
    void superProcessMouseMotionEvent(final MouseEvent mouseEvent) {
        super.processMouseMotionEvent(mouseEvent);
    }
    
    void setCreatedDoubleBuffer(final boolean b) {
        this.setFlag(9, b);
    }
    
    boolean getCreatedDoubleBuffer() {
        return this.getFlag(9);
    }
    
    @Deprecated
    @Override
    public void enable() {
        if (!this.isEnabled()) {
            super.enable();
            if (this.accessibleContext != null) {
                this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.ENABLED);
            }
        }
    }
    
    @Deprecated
    @Override
    public void disable() {
        if (this.isEnabled()) {
            super.disable();
            if (this.accessibleContext != null) {
                this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.ENABLED, null);
            }
        }
    }
    
    private ArrayTable getClientProperties() {
        if (this.clientProperties == null) {
            this.clientProperties = new ArrayTable();
        }
        return this.clientProperties;
    }
    
    public final Object getClientProperty(final Object o) {
        if (o == SwingUtilities2.AA_TEXT_PROPERTY_KEY) {
            return this.aaTextInfo;
        }
        if (o == SwingUtilities2.COMPONENT_UI_PROPERTY_KEY) {
            return this.ui;
        }
        if (this.clientProperties == null) {
            return null;
        }
        synchronized (this.clientProperties) {
            return this.clientProperties.get(o);
        }
    }
    
    public final void putClientProperty(final Object o, final Object aaTextInfo) {
        if (o == SwingUtilities2.AA_TEXT_PROPERTY_KEY) {
            this.aaTextInfo = aaTextInfo;
            return;
        }
        if (aaTextInfo == null && this.clientProperties == null) {
            return;
        }
        final ArrayTable clientProperties = this.getClientProperties();
        final Object value;
        synchronized (clientProperties) {
            value = clientProperties.get(o);
            if (aaTextInfo != null) {
                clientProperties.put(o, aaTextInfo);
            }
            else {
                if (value == null) {
                    return;
                }
                clientProperties.remove(o);
            }
        }
        this.clientPropertyChanged(o, value, aaTextInfo);
        this.firePropertyChange(o.toString(), value, aaTextInfo);
    }
    
    void clientPropertyChanged(final Object o, final Object o2, final Object o3) {
    }
    
    void setUIProperty(final String s, final Object o) {
        if (s == "opaque") {
            if (!this.getFlag(24)) {
                this.setOpaque((boolean)o);
                this.setFlag(24, false);
            }
        }
        else if (s == "autoscrolls") {
            if (!this.getFlag(25)) {
                this.setAutoscrolls((boolean)o);
                this.setFlag(25, false);
            }
        }
        else if (s == "focusTraversalKeysForward") {
            if (!this.getFlag(26)) {
                super.setFocusTraversalKeys(0, (Set<? extends AWTKeyStroke>)o);
            }
        }
        else {
            if (s != "focusTraversalKeysBackward") {
                throw new IllegalArgumentException("property \"" + s + "\" cannot be set using this method");
            }
            if (!this.getFlag(27)) {
                super.setFocusTraversalKeys(1, (Set<? extends AWTKeyStroke>)o);
            }
        }
    }
    
    @Override
    public void setFocusTraversalKeys(final int n, final Set<? extends AWTKeyStroke> set) {
        if (n == 0) {
            this.setFlag(26, true);
        }
        else if (n == 1) {
            this.setFlag(27, true);
        }
        super.setFocusTraversalKeys(n, set);
    }
    
    public static boolean isLightweightComponent(final Component component) {
        return component.getPeer() instanceof LightweightPeer;
    }
    
    @Deprecated
    @Override
    public void reshape(final int n, final int n2, final int n3, final int n4) {
        super.reshape(n, n2, n3, n4);
    }
    
    @Override
    public Rectangle getBounds(final Rectangle rectangle) {
        if (rectangle == null) {
            return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
        rectangle.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        return rectangle;
    }
    
    @Override
    public Dimension getSize(final Dimension dimension) {
        if (dimension == null) {
            return new Dimension(this.getWidth(), this.getHeight());
        }
        dimension.setSize(this.getWidth(), this.getHeight());
        return dimension;
    }
    
    @Override
    public Point getLocation(final Point point) {
        if (point == null) {
            return new Point(this.getX(), this.getY());
        }
        point.setLocation(this.getX(), this.getY());
        return point;
    }
    
    @Override
    public int getX() {
        return super.getX();
    }
    
    @Override
    public int getY() {
        return super.getY();
    }
    
    @Override
    public int getWidth() {
        return super.getWidth();
    }
    
    @Override
    public int getHeight() {
        return super.getHeight();
    }
    
    @Override
    public boolean isOpaque() {
        return this.getFlag(3);
    }
    
    public void setOpaque(final boolean b) {
        final boolean flag = this.getFlag(3);
        this.setFlag(3, b);
        this.setFlag(24, true);
        this.firePropertyChange("opaque", flag, b);
    }
    
    boolean rectangleIsObscured(final int n, final int n2, final int n3, final int n4) {
        for (int componentCount = this.getComponentCount(), i = 0; i < componentCount; ++i) {
            final Component component = this.getComponent(i);
            final int x = component.getX();
            final int y = component.getY();
            final int width = component.getWidth();
            final int height = component.getHeight();
            if (n >= x && n + n3 <= x + width && n2 >= y && n2 + n4 <= y + height && component.isVisible()) {
                return component instanceof JComponent && component.isOpaque();
            }
        }
        return false;
    }
    
    static final void computeVisibleRect(final Component component, final Rectangle rectangle) {
        final Container parent = component.getParent();
        final Rectangle bounds = component.getBounds();
        if (parent == null || parent instanceof Window || parent instanceof Applet) {
            rectangle.setBounds(0, 0, bounds.width, bounds.height);
        }
        else {
            computeVisibleRect(parent, rectangle);
            rectangle.x -= bounds.x;
            rectangle.y -= bounds.y;
            SwingUtilities.computeIntersection(0, 0, bounds.width, bounds.height, rectangle);
        }
    }
    
    public void computeVisibleRect(final Rectangle rectangle) {
        computeVisibleRect(this, rectangle);
    }
    
    public Rectangle getVisibleRect() {
        final Rectangle rectangle = new Rectangle();
        this.computeVisibleRect(rectangle);
        return rectangle;
    }
    
    public void firePropertyChange(final String s, final boolean b, final boolean b2) {
        super.firePropertyChange(s, b, b2);
    }
    
    public void firePropertyChange(final String s, final int n, final int n2) {
        super.firePropertyChange(s, n, n2);
    }
    
    @Override
    public void firePropertyChange(final String s, final char c, final char c2) {
        super.firePropertyChange(s, c, c2);
    }
    
    protected void fireVetoableChange(final String s, final Object o, final Object o2) throws PropertyVetoException {
        if (this.vetoableChangeSupport == null) {
            return;
        }
        this.vetoableChangeSupport.fireVetoableChange(s, o, o2);
    }
    
    public synchronized void addVetoableChangeListener(final VetoableChangeListener vetoableChangeListener) {
        if (this.vetoableChangeSupport == null) {
            this.vetoableChangeSupport = new VetoableChangeSupport(this);
        }
        this.vetoableChangeSupport.addVetoableChangeListener(vetoableChangeListener);
    }
    
    public synchronized void removeVetoableChangeListener(final VetoableChangeListener vetoableChangeListener) {
        if (this.vetoableChangeSupport == null) {
            return;
        }
        this.vetoableChangeSupport.removeVetoableChangeListener(vetoableChangeListener);
    }
    
    public synchronized VetoableChangeListener[] getVetoableChangeListeners() {
        if (this.vetoableChangeSupport == null) {
            return new VetoableChangeListener[0];
        }
        return this.vetoableChangeSupport.getVetoableChangeListeners();
    }
    
    public Container getTopLevelAncestor() {
        for (Container parent = this; parent != null; parent = parent.getParent()) {
            if (parent instanceof Window || parent instanceof Applet) {
                return parent;
            }
        }
        return null;
    }
    
    private AncestorNotifier getAncestorNotifier() {
        return (AncestorNotifier)this.getClientProperty(ClientPropertyKey.JComponent_ANCESTOR_NOTIFIER);
    }
    
    public void addAncestorListener(final AncestorListener ancestorListener) {
        AncestorNotifier ancestorNotifier = this.getAncestorNotifier();
        if (ancestorNotifier == null) {
            ancestorNotifier = new AncestorNotifier(this);
            this.putClientProperty(ClientPropertyKey.JComponent_ANCESTOR_NOTIFIER, ancestorNotifier);
        }
        ancestorNotifier.addAncestorListener(ancestorListener);
    }
    
    public void removeAncestorListener(final AncestorListener ancestorListener) {
        final AncestorNotifier ancestorNotifier = this.getAncestorNotifier();
        if (ancestorNotifier == null) {
            return;
        }
        ancestorNotifier.removeAncestorListener(ancestorListener);
        if (ancestorNotifier.listenerList.getListenerList().length == 0) {
            ancestorNotifier.removeAllListeners();
            this.putClientProperty(ClientPropertyKey.JComponent_ANCESTOR_NOTIFIER, null);
        }
    }
    
    public AncestorListener[] getAncestorListeners() {
        final AncestorNotifier ancestorNotifier = this.getAncestorNotifier();
        if (ancestorNotifier == null) {
            return new AncestorListener[0];
        }
        return ancestorNotifier.getAncestorListeners();
    }
    
    @Override
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        EventListener[] listeners;
        if (clazz == AncestorListener.class) {
            listeners = this.getAncestorListeners();
        }
        else if (clazz == VetoableChangeListener.class) {
            listeners = this.getVetoableChangeListeners();
        }
        else if (clazz == PropertyChangeListener.class) {
            listeners = this.getPropertyChangeListeners();
        }
        else {
            listeners = this.listenerList.getListeners(clazz);
        }
        if (listeners.length == 0) {
            return super.getListeners(clazz);
        }
        return (T[])listeners;
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        this.firePropertyChange("ancestor", null, this.getParent());
        this.registerWithKeyboardManager(false);
        this.registerNextFocusableComponent();
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        this.firePropertyChange("ancestor", this.getParent(), null);
        this.unregisterWithKeyboardManager();
        this.deregisterNextFocusableComponent();
        if (this.getCreatedDoubleBuffer()) {
            RepaintManager.currentManager(this).resetDoubleBuffer();
            this.setCreatedDoubleBuffer(false);
        }
        if (this.autoscrolls) {
            Autoscroller.stop(this);
        }
    }
    
    @Override
    public void repaint(final long n, final int n2, final int n3, final int n4, final int n5) {
        RepaintManager.currentManager(SunToolkit.targetToAppContext(this)).addDirtyRegion(this, n2, n3, n4, n5);
    }
    
    public void repaint(final Rectangle rectangle) {
        this.repaint(0L, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    @Override
    public void revalidate() {
        if (this.getParent() == null) {
            return;
        }
        if (SunToolkit.isDispatchThreadForAppContext(this)) {
            this.invalidate();
            RepaintManager.currentManager(this).addInvalidComponent(this);
        }
        else {
            if (this.revalidateRunnableScheduled.getAndSet(true)) {
                return;
            }
            SunToolkit.executeOnEventHandlerThread(this, () -> {
                this.revalidateRunnableScheduled.set(false);
                this.revalidate();
            });
        }
    }
    
    @Override
    public boolean isValidateRoot() {
        return false;
    }
    
    public boolean isOptimizedDrawingEnabled() {
        return true;
    }
    
    protected boolean isPaintingOrigin() {
        return false;
    }
    
    public void paintImmediately(int n, int n2, final int n3, final int n4) {
        JComponent component = this;
        if (!this.isShowing()) {
            return;
        }
        final JComponent paintingOrigin = SwingUtilities.getPaintingOrigin(this);
        if (paintingOrigin != null) {
            final Rectangle convertRectangle = SwingUtilities.convertRectangle(component, new Rectangle(n, n2, n3, n4), paintingOrigin);
            paintingOrigin.paintImmediately(convertRectangle.x, convertRectangle.y, convertRectangle.width, convertRectangle.height);
            return;
        }
        while (!component.isOpaque()) {
            final Container parent = component.getParent();
            if (parent == null) {
                break;
            }
            n += component.getX();
            n2 += component.getY();
            component = (JComponent)parent;
            if (!(component instanceof JComponent)) {
                break;
            }
        }
        if (component instanceof JComponent) {
            component._paintImmediately(n, n2, n3, n4);
        }
        else {
            component.repaint(n, n2, n3, n4);
        }
    }
    
    public void paintImmediately(final Rectangle rectangle) {
        this.paintImmediately(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    boolean alwaysOnTop() {
        return false;
    }
    
    void setPaintingChild(final Component paintingChild) {
        this.paintingChild = paintingChild;
    }
    
    void _paintImmediately(final int x, final int y, final int width, final int height) {
        int n = 0;
        int n2 = 0;
        boolean b = false;
        JComponent component = null;
        JComponent component2 = this;
        final RepaintManager currentManager = RepaintManager.currentManager(this);
        final ArrayList list = new ArrayList(7);
        int n3 = -1;
        int n4 = 0;
        final int n5 = 0;
        final Rectangle fetchRectangle = fetchRectangle();
        fetchRectangle.x = x;
        fetchRectangle.y = y;
        fetchRectangle.width = width;
        fetchRectangle.height = height;
        final boolean b2 = this.alwaysOnTop() && this.isOpaque();
        if (b2) {
            SwingUtilities.computeIntersection(0, 0, this.getWidth(), this.getHeight(), fetchRectangle);
            if (fetchRectangle.width == 0) {
                recycleRectangle(fetchRectangle);
                return;
            }
        }
        Container parent = this;
        Component component3 = null;
        while (parent != null && !(parent instanceof Window) && !(parent instanceof Applet)) {
            final JComponent component4 = (parent instanceof JComponent) ? ((JComponent)parent) : null;
            list.add(parent);
            if (!b2 && component4 != null && !component4.isOptimizedDrawingEnabled()) {
                boolean b3;
                if (parent != this) {
                    if (component4.isPaintingOrigin()) {
                        b3 = true;
                    }
                    else {
                        Component[] components;
                        int n6;
                        for (components = parent.getComponents(), n6 = 0; n6 < components.length && components[n6] != component3; ++n6) {}
                        switch (component4.getObscuredState(n6, fetchRectangle.x, fetchRectangle.y, fetchRectangle.width, fetchRectangle.height)) {
                            case 0: {
                                b3 = false;
                                break;
                            }
                            case 2: {
                                recycleRectangle(fetchRectangle);
                                return;
                            }
                            default: {
                                b3 = true;
                                break;
                            }
                        }
                    }
                }
                else {
                    b3 = false;
                }
                if (b3) {
                    component2 = component4;
                    n3 = n4;
                    n2 = (n = 0);
                    b = false;
                }
            }
            ++n4;
            if (currentManager.isDoubleBufferingEnabled() && component4 != null && component4.isDoubleBuffered()) {
                b = true;
                component = component4;
            }
            if (!b2) {
                final int x2 = parent.getX();
                final int y2 = parent.getY();
                SwingUtilities.computeIntersection(n5, n5, parent.getWidth(), parent.getHeight(), fetchRectangle);
                final Rectangle rectangle = fetchRectangle;
                rectangle.x += x2;
                final Rectangle rectangle2 = fetchRectangle;
                rectangle2.y += y2;
                n += x2;
                n2 += y2;
            }
            component3 = parent;
            parent = parent.getParent();
        }
        if (parent == null || parent.getPeer() == null || fetchRectangle.width <= 0 || fetchRectangle.height <= 0) {
            recycleRectangle(fetchRectangle);
            return;
        }
        component2.setFlag(13, true);
        final Rectangle rectangle3 = fetchRectangle;
        rectangle3.x -= n;
        final Rectangle rectangle4 = fetchRectangle;
        rectangle4.y -= n2;
        if (component2 != this) {
            for (int i = n3; i > 0; --i) {
                final Component component5 = (Component)list.get(i);
                if (component5 instanceof JComponent) {
                    ((JComponent)component5).setPaintingChild((Component)list.get(i - 1));
                }
            }
        }
        try {
            final Graphics safelyGetGraphics;
            if ((safelyGetGraphics = safelyGetGraphics(component2, parent)) != null) {
                try {
                    if (b) {
                        final RepaintManager currentManager2 = RepaintManager.currentManager(component);
                        currentManager2.beginPaint();
                        try {
                            currentManager2.paint(component2, component, safelyGetGraphics, fetchRectangle.x, fetchRectangle.y, fetchRectangle.width, fetchRectangle.height);
                        }
                        finally {
                            currentManager2.endPaint();
                        }
                    }
                    else {
                        safelyGetGraphics.setClip(fetchRectangle.x, fetchRectangle.y, fetchRectangle.width, fetchRectangle.height);
                        component2.paint(safelyGetGraphics);
                    }
                }
                finally {
                    safelyGetGraphics.dispose();
                }
            }
        }
        finally {
            if (component2 != this) {
                for (int j = n3; j > 0; --j) {
                    final Component component6 = (Component)list.get(j);
                    if (component6 instanceof JComponent) {
                        ((JComponent)component6).setPaintingChild(null);
                    }
                }
            }
            component2.setFlag(13, false);
        }
        recycleRectangle(fetchRectangle);
    }
    
    void paintToOffscreen(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        try {
            this.setFlag(1, true);
            if (n2 + n4 < n6 || n + n3 < n5) {
                this.setFlag(2, true);
            }
            if (this.getFlag(13)) {
                this.paint(graphics);
            }
            else {
                if (!this.rectangleIsObscured(n, n2, n3, n4)) {
                    this.paintComponent(graphics);
                    this.paintBorder(graphics);
                }
                this.paintChildren(graphics);
            }
        }
        finally {
            this.setFlag(1, false);
            this.setFlag(2, false);
        }
    }
    
    private int getObscuredState(final int n, final int n2, final int n3, final int n4, final int n5) {
        int n6 = 0;
        final Rectangle fetchRectangle = fetchRectangle();
        for (int i = n - 1; i >= 0; --i) {
            final Component component = this.getComponent(i);
            if (component.isVisible()) {
                int opaque;
                if (component instanceof JComponent) {
                    opaque = (component.isOpaque() ? 1 : 0);
                    if (opaque == 0 && n6 == 1) {
                        continue;
                    }
                }
                else {
                    opaque = 1;
                }
                final Rectangle bounds = component.getBounds(fetchRectangle);
                if (opaque != 0 && n2 >= bounds.x && n2 + n4 <= bounds.x + bounds.width && n3 >= bounds.y && n3 + n5 <= bounds.y + bounds.height) {
                    recycleRectangle(fetchRectangle);
                    return 2;
                }
                if (n6 == 0 && n2 + n4 > bounds.x && n3 + n5 > bounds.y && n2 < bounds.x + bounds.width && n3 < bounds.y + bounds.height) {
                    n6 = 1;
                }
            }
        }
        recycleRectangle(fetchRectangle);
        return n6;
    }
    
    boolean checkIfChildObscuredBySibling() {
        return true;
    }
    
    private void setFlag(final int n, final boolean b) {
        if (b) {
            this.flags |= 1 << n;
        }
        else {
            this.flags &= ~(1 << n);
        }
    }
    
    private boolean getFlag(final int n) {
        final int n2 = 1 << n;
        return (this.flags & n2) == n2;
    }
    
    static void setWriteObjCounter(final JComponent component, final byte b) {
        component.flags = ((component.flags & 0xFFC03FFF) | b << 14);
    }
    
    static byte getWriteObjCounter(final JComponent component) {
        return (byte)(component.flags >> 14 & 0xFF);
    }
    
    public void setDoubleBuffered(final boolean b) {
        this.setFlag(0, b);
    }
    
    @Override
    public boolean isDoubleBuffered() {
        return this.getFlag(0);
    }
    
    public JRootPane getRootPane() {
        return SwingUtilities.getRootPane(this);
    }
    
    void compWriteObjectNotify() {
        final byte writeObjCounter = getWriteObjCounter(this);
        setWriteObjCounter(this, (byte)(writeObjCounter + 1));
        if (writeObjCounter != 0) {
            return;
        }
        this.uninstallUIAndProperties();
        if (this.getToolTipText() != null || this instanceof JTableHeader) {
            ToolTipManager.sharedInstance().unregisterComponent(this);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        ReadObjectCallback readObjectCallback = JComponent.readObjectCallbacks.get(objectInputStream);
        if (readObjectCallback == null) {
            try {
                JComponent.readObjectCallbacks.put(objectInputStream, readObjectCallback = new ReadObjectCallback(objectInputStream));
            }
            catch (final Exception ex) {
                throw new IOException(ex.toString());
            }
        }
        readObjectCallback.registerComponent(this);
        final int int1 = objectInputStream.readInt();
        if (int1 > 0) {
            this.clientProperties = new ArrayTable();
            for (int i = 0; i < int1; ++i) {
                this.clientProperties.put(objectInputStream.readObject(), objectInputStream.readObject());
            }
        }
        if (this.getToolTipText() != null) {
            ToolTipManager.sharedInstance().registerComponent(this);
        }
        setWriteObjCounter(this, (byte)0);
        this.revalidateRunnableScheduled = new AtomicBoolean(false);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("ComponentUI")) {
            final byte b = (byte)(getWriteObjCounter(this) - 1);
            setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
        ArrayTable.writeArrayTable(objectOutputStream, this.clientProperties);
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",alignmentX=" + this.alignmentX + ",alignmentY=" + this.alignmentY + ",border=" + ((this.border == null) ? "" : ((this.border == this) ? "this" : this.border.toString())) + ",flags=" + this.flags + ",maximumSize=" + (this.isMaximumSizeSet() ? this.getMaximumSize().toString() : "") + ",minimumSize=" + (this.isMinimumSizeSet() ? this.getMinimumSize().toString() : "") + ",preferredSize=" + (this.isPreferredSizeSet() ? this.getPreferredSize().toString() : "");
    }
    
    @Deprecated
    @Override
    public void hide() {
        final boolean showing = this.isShowing();
        super.hide();
        if (showing) {
            final Container parent = this.getParent();
            if (parent != null) {
                final Rectangle bounds = this.getBounds();
                parent.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
            }
            this.revalidate();
        }
    }
    
    static {
        readObjectCallbacks = new Hashtable<ObjectInputStream, ReadObjectCallback>(1);
        INPUT_VERIFIER_SOURCE_KEY = new StringBuilder("InputVerifierSourceKey");
        JComponent.tempRectangles = new ArrayList<Rectangle>(11);
        JComponent.componentObtainingGraphicsFromLock = new StringBuilder("componentObtainingGraphicsFrom");
        focusController = new RequestFocusController() {
            @Override
            public boolean acceptRequestFocus(final Component component, final Component component2, final boolean b, final boolean b2, final CausedFocusEvent.Cause cause) {
                if (component2 == null || !(component2 instanceof JComponent)) {
                    return true;
                }
                if (component == null || !(component instanceof JComponent)) {
                    return true;
                }
                if (!((JComponent)component2).getVerifyInputWhenFocusTarget()) {
                    return true;
                }
                final JComponent component3 = (JComponent)component;
                final InputVerifier inputVerifier = component3.getInputVerifier();
                if (inputVerifier == null) {
                    return true;
                }
                final Object appContextGet = SwingUtilities.appContextGet(JComponent.INPUT_VERIFIER_SOURCE_KEY);
                if (appContextGet == component3) {
                    return true;
                }
                SwingUtilities.appContextPut(JComponent.INPUT_VERIFIER_SOURCE_KEY, component3);
                try {
                    return inputVerifier.shouldYieldFocus(component3);
                }
                finally {
                    if (appContextGet != null) {
                        SwingUtilities.appContextPut(JComponent.INPUT_VERIFIER_SOURCE_KEY, appContextGet);
                    }
                    else {
                        SwingUtilities.appContextRemove(JComponent.INPUT_VERIFIER_SOURCE_KEY);
                    }
                }
            }
        };
    }
    
    final class ActionStandin implements Action
    {
        private final ActionListener actionListener;
        private final String command;
        private final Action action;
        
        ActionStandin(final ActionListener actionListener, final String command) {
            this.actionListener = actionListener;
            if (actionListener instanceof Action) {
                this.action = (Action)actionListener;
            }
            else {
                this.action = null;
            }
            this.command = command;
        }
        
        @Override
        public Object getValue(final String s) {
            if (s != null) {
                if (s.equals("ActionCommandKey")) {
                    return this.command;
                }
                if (this.action != null) {
                    return this.action.getValue(s);
                }
                if (s.equals("Name")) {
                    return "ActionStandin";
                }
            }
            return null;
        }
        
        @Override
        public boolean isEnabled() {
            return this.actionListener != null && (this.action == null || this.action.isEnabled());
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (this.actionListener != null) {
                this.actionListener.actionPerformed(actionEvent);
            }
        }
        
        @Override
        public void putValue(final String s, final Object o) {
        }
        
        @Override
        public void setEnabled(final boolean b) {
        }
        
        @Override
        public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        }
        
        @Override
        public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        }
    }
    
    static final class IntVector
    {
        int[] array;
        int count;
        int capacity;
        
        IntVector() {
            this.array = null;
            this.count = 0;
            this.capacity = 0;
        }
        
        int size() {
            return this.count;
        }
        
        int elementAt(final int n) {
            return this.array[n];
        }
        
        void addElement(final int n) {
            if (this.count == this.capacity) {
                this.capacity = (this.capacity + 2) * 2;
                final int[] array = new int[this.capacity];
                if (this.count > 0) {
                    System.arraycopy(this.array, 0, array, 0, this.count);
                }
                this.array = array;
            }
            this.array[this.count++] = n;
        }
        
        void setElementAt(final int n, final int n2) {
            this.array[n2] = n;
        }
    }
    
    static class KeyboardState implements Serializable
    {
        private static final Object keyCodesKey;
        
        static IntVector getKeyCodeArray() {
            IntVector intVector = (IntVector)SwingUtilities.appContextGet(KeyboardState.keyCodesKey);
            if (intVector == null) {
                intVector = new IntVector();
                SwingUtilities.appContextPut(KeyboardState.keyCodesKey, intVector);
            }
            return intVector;
        }
        
        static void registerKeyPressed(final int n) {
            final IntVector keyCodeArray = getKeyCodeArray();
            for (int size = keyCodeArray.size(), i = 0; i < size; ++i) {
                if (keyCodeArray.elementAt(i) == -1) {
                    keyCodeArray.setElementAt(n, i);
                    return;
                }
            }
            keyCodeArray.addElement(n);
        }
        
        static void registerKeyReleased(final int n) {
            final IntVector keyCodeArray = getKeyCodeArray();
            for (int size = keyCodeArray.size(), i = 0; i < size; ++i) {
                if (keyCodeArray.elementAt(i) == n) {
                    keyCodeArray.setElementAt(-1, i);
                    return;
                }
            }
        }
        
        static boolean keyIsPressed(final int n) {
            final IntVector keyCodeArray = getKeyCodeArray();
            for (int size = keyCodeArray.size(), i = 0; i < size; ++i) {
                if (keyCodeArray.elementAt(i) == n) {
                    return true;
                }
            }
            return false;
        }
        
        static boolean shouldProcess(final KeyEvent keyEvent) {
            switch (keyEvent.getID()) {
                case 401: {
                    if (!keyIsPressed(keyEvent.getKeyCode())) {
                        registerKeyPressed(keyEvent.getKeyCode());
                    }
                    return true;
                }
                case 402: {
                    if (keyIsPressed(keyEvent.getKeyCode()) || keyEvent.getKeyCode() == 154) {
                        registerKeyReleased(keyEvent.getKeyCode());
                        return true;
                    }
                    return false;
                }
                case 400: {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
        
        static {
            keyCodesKey = KeyboardState.class;
        }
    }
    
    public abstract class AccessibleJComponent extends AccessibleAWTContainer implements AccessibleExtendedComponent
    {
        private transient volatile int propertyListenersCount;
        @Deprecated
        protected FocusListener accessibleFocusHandler;
        
        protected AccessibleJComponent() {
            this.propertyListenersCount = 0;
            this.accessibleFocusHandler = null;
        }
        
        @Override
        public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
            super.addPropertyChangeListener(propertyChangeListener);
        }
        
        @Override
        public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
            super.removePropertyChangeListener(propertyChangeListener);
        }
        
        protected String getBorderTitle(final Border border) {
            if (border instanceof TitledBorder) {
                return ((TitledBorder)border).getTitle();
            }
            if (border instanceof CompoundBorder) {
                String s = this.getBorderTitle(((CompoundBorder)border).getInsideBorder());
                if (s == null) {
                    s = this.getBorderTitle(((CompoundBorder)border).getOutsideBorder());
                }
                return s;
            }
            return null;
        }
        
        @Override
        public String getAccessibleName() {
            String s = this.accessibleName;
            if (s == null) {
                s = (String)JComponent.this.getClientProperty("AccessibleName");
            }
            if (s == null) {
                s = this.getBorderTitle(JComponent.this.getBorder());
            }
            if (s == null) {
                final Object clientProperty = JComponent.this.getClientProperty("labeledBy");
                if (clientProperty instanceof Accessible) {
                    final AccessibleContext accessibleContext = ((Accessible)clientProperty).getAccessibleContext();
                    if (accessibleContext != null) {
                        s = accessibleContext.getAccessibleName();
                    }
                }
            }
            return s;
        }
        
        @Override
        public String getAccessibleDescription() {
            String s = this.accessibleDescription;
            if (s == null) {
                s = (String)JComponent.this.getClientProperty("AccessibleDescription");
            }
            if (s == null) {
                try {
                    s = this.getToolTipText();
                }
                catch (final Exception ex) {}
            }
            if (s == null) {
                final Object clientProperty = JComponent.this.getClientProperty("labeledBy");
                if (clientProperty instanceof Accessible) {
                    final AccessibleContext accessibleContext = ((Accessible)clientProperty).getAccessibleContext();
                    if (accessibleContext != null) {
                        s = accessibleContext.getAccessibleDescription();
                    }
                }
            }
            return s;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SWING_COMPONENT;
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (JComponent.this.isOpaque()) {
                accessibleStateSet.add(AccessibleState.OPAQUE);
            }
            return accessibleStateSet;
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            return super.getAccessibleChildrenCount();
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            return super.getAccessibleChild(n);
        }
        
        AccessibleExtendedComponent getAccessibleExtendedComponent() {
            return this;
        }
        
        @Override
        public String getToolTipText() {
            return JComponent.this.getToolTipText();
        }
        
        @Override
        public String getTitledBorderText() {
            final Border border = JComponent.this.getBorder();
            if (border instanceof TitledBorder) {
                return ((TitledBorder)border).getTitle();
            }
            return null;
        }
        
        @Override
        public AccessibleKeyBinding getAccessibleKeyBinding() {
            final Object clientProperty = JComponent.this.getClientProperty("labeledBy");
            if (clientProperty instanceof Accessible) {
                final AccessibleContext accessibleContext = ((Accessible)clientProperty).getAccessibleContext();
                if (accessibleContext != null) {
                    final AccessibleComponent accessibleComponent = accessibleContext.getAccessibleComponent();
                    if (!(accessibleComponent instanceof AccessibleExtendedComponent)) {
                        return null;
                    }
                    return ((AccessibleExtendedComponent)accessibleComponent).getAccessibleKeyBinding();
                }
            }
            return null;
        }
        
        protected class AccessibleContainerHandler implements ContainerListener
        {
            @Override
            public void componentAdded(final ContainerEvent containerEvent) {
                final Component child = containerEvent.getChild();
                if (child != null && child instanceof Accessible) {
                    AccessibleJComponent.this.firePropertyChange("AccessibleChild", null, child.getAccessibleContext());
                }
            }
            
            @Override
            public void componentRemoved(final ContainerEvent containerEvent) {
                final Component child = containerEvent.getChild();
                if (child != null && child instanceof Accessible) {
                    AccessibleJComponent.this.firePropertyChange("AccessibleChild", child.getAccessibleContext(), null);
                }
            }
        }
        
        protected class AccessibleFocusHandler implements FocusListener
        {
            @Override
            public void focusGained(final FocusEvent focusEvent) {
                if (JComponent.this.accessibleContext != null) {
                    JComponent.this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.FOCUSED);
                }
            }
            
            @Override
            public void focusLost(final FocusEvent focusEvent) {
                if (JComponent.this.accessibleContext != null) {
                    JComponent.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.FOCUSED, null);
                }
            }
        }
    }
    
    private class ReadObjectCallback implements ObjectInputValidation
    {
        private final Vector<JComponent> roots;
        private final ObjectInputStream inputStream;
        
        ReadObjectCallback(final ObjectInputStream inputStream) throws Exception {
            this.roots = new Vector<JComponent>(1);
            (this.inputStream = inputStream).registerValidation(this, 0);
        }
        
        @Override
        public void validateObject() throws InvalidObjectException {
            try {
                final Iterator<JComponent> iterator = this.roots.iterator();
                while (iterator.hasNext()) {
                    SwingUtilities.updateComponentTreeUI(iterator.next());
                }
            }
            finally {
                JComponent.readObjectCallbacks.remove(this.inputStream);
            }
        }
        
        private void registerComponent(final JComponent component) {
            for (final JComponent component2 : this.roots) {
                for (Container parent = component; parent != null; parent = parent.getParent()) {
                    if (parent == component2) {
                        return;
                    }
                }
            }
            for (int i = 0; i < this.roots.size(); ++i) {
                for (Container container = this.roots.elementAt(i).getParent(); container != null; container = container.getParent()) {
                    if (container == component) {
                        this.roots.removeElementAt(i--);
                        break;
                    }
                }
            }
            this.roots.addElement(component);
        }
    }
}
