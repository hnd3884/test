package javax.swing.plaf.basic;

import sun.swing.SwingUtilities2;
import java.awt.KeyboardFocusManager;
import java.awt.FocusTraversalPolicy;
import sun.swing.UIAction;
import java.awt.LayoutManager2;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.beans.PropertyChangeEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Canvas;
import javax.swing.ActionMap;
import java.awt.LayoutManager;
import sun.swing.DefaultLookup;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import java.awt.AWTKeyStroke;
import java.util.HashSet;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.LookAndFeel;
import javax.swing.Action;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.Component;
import javax.swing.KeyStroke;
import java.util.Set;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import javax.swing.JSplitPane;
import javax.swing.plaf.SplitPaneUI;

public class BasicSplitPaneUI extends SplitPaneUI
{
    protected static final String NON_CONTINUOUS_DIVIDER = "nonContinuousDivider";
    protected static int KEYBOARD_DIVIDER_MOVE_OFFSET;
    protected JSplitPane splitPane;
    protected BasicHorizontalLayoutManager layoutManager;
    protected BasicSplitPaneDivider divider;
    protected PropertyChangeListener propertyChangeListener;
    protected FocusListener focusListener;
    private Handler handler;
    private Set<KeyStroke> managingFocusForwardTraversalKeys;
    private Set<KeyStroke> managingFocusBackwardTraversalKeys;
    protected int dividerSize;
    protected Component nonContinuousLayoutDivider;
    protected boolean draggingHW;
    protected int beginDragDividerLocation;
    @Deprecated
    protected KeyStroke upKey;
    @Deprecated
    protected KeyStroke downKey;
    @Deprecated
    protected KeyStroke leftKey;
    @Deprecated
    protected KeyStroke rightKey;
    @Deprecated
    protected KeyStroke homeKey;
    @Deprecated
    protected KeyStroke endKey;
    @Deprecated
    protected KeyStroke dividerResizeToggleKey;
    @Deprecated
    protected ActionListener keyboardUpLeftListener;
    @Deprecated
    protected ActionListener keyboardDownRightListener;
    @Deprecated
    protected ActionListener keyboardHomeListener;
    @Deprecated
    protected ActionListener keyboardEndListener;
    @Deprecated
    protected ActionListener keyboardResizeToggleListener;
    private int orientation;
    private int lastDragLocation;
    private boolean continuousLayout;
    private boolean dividerKeyboardResize;
    private boolean dividerLocationIsSet;
    private Color dividerDraggingColor;
    private boolean rememberPaneSizes;
    private boolean keepHidden;
    boolean painted;
    boolean ignoreDividerLocationChange;
    
    public BasicSplitPaneUI() {
        this.keepHidden = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicSplitPaneUI();
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("negativeIncrement"));
        lazyActionMap.put(new Actions("positiveIncrement"));
        lazyActionMap.put(new Actions("selectMin"));
        lazyActionMap.put(new Actions("selectMax"));
        lazyActionMap.put(new Actions("startResize"));
        lazyActionMap.put(new Actions("toggleFocus"));
        lazyActionMap.put(new Actions("focusOutForward"));
        lazyActionMap.put(new Actions("focusOutBackward"));
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.splitPane = (JSplitPane)component;
        this.dividerLocationIsSet = false;
        this.dividerKeyboardResize = false;
        this.keepHidden = false;
        this.installDefaults();
        this.installListeners();
        this.installKeyboardActions();
        this.setLastDragLocation(-1);
    }
    
    protected void installDefaults() {
        LookAndFeel.installBorder(this.splitPane, "SplitPane.border");
        LookAndFeel.installColors(this.splitPane, "SplitPane.background", "SplitPane.foreground");
        LookAndFeel.installProperty(this.splitPane, "opaque", Boolean.TRUE);
        if (this.divider == null) {
            this.divider = this.createDefaultDivider();
        }
        this.divider.setBasicSplitPaneUI(this);
        final Border border = this.divider.getBorder();
        if (border == null || !(border instanceof UIResource)) {
            this.divider.setBorder(UIManager.getBorder("SplitPaneDivider.border"));
        }
        this.dividerDraggingColor = UIManager.getColor("SplitPaneDivider.draggingColor");
        this.setOrientation(this.splitPane.getOrientation());
        final Integer n = (Integer)UIManager.get("SplitPane.dividerSize");
        LookAndFeel.installProperty(this.splitPane, "dividerSize", (n == null) ? 10 : n);
        this.divider.setDividerSize(this.splitPane.getDividerSize());
        this.dividerSize = this.divider.getDividerSize();
        this.splitPane.add(this.divider, "divider");
        this.setContinuousLayout(this.splitPane.isContinuousLayout());
        this.resetLayoutManager();
        if (this.nonContinuousLayoutDivider == null) {
            this.setNonContinuousLayoutDivider(this.createDefaultNonContinuousLayoutDivider(), true);
        }
        else {
            this.setNonContinuousLayoutDivider(this.nonContinuousLayoutDivider, true);
        }
        if (this.managingFocusForwardTraversalKeys == null) {
            (this.managingFocusForwardTraversalKeys = new HashSet<KeyStroke>()).add(KeyStroke.getKeyStroke(9, 0));
        }
        this.splitPane.setFocusTraversalKeys(0, this.managingFocusForwardTraversalKeys);
        if (this.managingFocusBackwardTraversalKeys == null) {
            (this.managingFocusBackwardTraversalKeys = new HashSet<KeyStroke>()).add(KeyStroke.getKeyStroke(9, 1));
        }
        this.splitPane.setFocusTraversalKeys(1, this.managingFocusBackwardTraversalKeys);
    }
    
    protected void installListeners() {
        final PropertyChangeListener propertyChangeListener = this.createPropertyChangeListener();
        this.propertyChangeListener = propertyChangeListener;
        if (propertyChangeListener != null) {
            this.splitPane.addPropertyChangeListener(this.propertyChangeListener);
        }
        if ((this.focusListener = this.createFocusListener()) != null) {
            this.splitPane.addFocusListener(this.focusListener);
        }
    }
    
    protected void installKeyboardActions() {
        SwingUtilities.replaceUIInputMap(this.splitPane, 1, this.getInputMap(1));
        LazyActionMap.installLazyActionMap(this.splitPane, BasicSplitPaneUI.class, "SplitPane.actionMap");
    }
    
    InputMap getInputMap(final int n) {
        if (n == 1) {
            return (InputMap)DefaultLookup.get(this.splitPane, this, "SplitPane.ancestorInputMap");
        }
        return null;
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallKeyboardActions();
        this.uninstallListeners();
        this.uninstallDefaults();
        this.dividerLocationIsSet = false;
        this.dividerKeyboardResize = false;
        this.splitPane = null;
    }
    
    protected void uninstallDefaults() {
        if (this.splitPane.getLayout() == this.layoutManager) {
            this.splitPane.setLayout(null);
        }
        if (this.nonContinuousLayoutDivider != null) {
            this.splitPane.remove(this.nonContinuousLayoutDivider);
        }
        LookAndFeel.uninstallBorder(this.splitPane);
        if (this.divider.getBorder() instanceof UIResource) {
            this.divider.setBorder(null);
        }
        this.splitPane.remove(this.divider);
        this.divider.setBasicSplitPaneUI(null);
        this.layoutManager = null;
        this.divider = null;
        this.setNonContinuousLayoutDivider(this.nonContinuousLayoutDivider = null);
        this.splitPane.setFocusTraversalKeys(0, null);
        this.splitPane.setFocusTraversalKeys(1, null);
    }
    
    protected void uninstallListeners() {
        if (this.propertyChangeListener != null) {
            this.splitPane.removePropertyChangeListener(this.propertyChangeListener);
            this.propertyChangeListener = null;
        }
        if (this.focusListener != null) {
            this.splitPane.removeFocusListener(this.focusListener);
            this.focusListener = null;
        }
        this.keyboardUpLeftListener = null;
        this.keyboardDownRightListener = null;
        this.keyboardHomeListener = null;
        this.keyboardEndListener = null;
        this.keyboardResizeToggleListener = null;
        this.handler = null;
    }
    
    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIActionMap(this.splitPane, null);
        SwingUtilities.replaceUIInputMap(this.splitPane, 1, null);
    }
    
    protected PropertyChangeListener createPropertyChangeListener() {
        return this.getHandler();
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    protected FocusListener createFocusListener() {
        return this.getHandler();
    }
    
    @Deprecated
    protected ActionListener createKeyboardUpLeftListener() {
        return new KeyboardUpLeftHandler();
    }
    
    @Deprecated
    protected ActionListener createKeyboardDownRightListener() {
        return new KeyboardDownRightHandler();
    }
    
    @Deprecated
    protected ActionListener createKeyboardHomeListener() {
        return new KeyboardHomeHandler();
    }
    
    @Deprecated
    protected ActionListener createKeyboardEndListener() {
        return new KeyboardEndHandler();
    }
    
    @Deprecated
    protected ActionListener createKeyboardResizeToggleListener() {
        return new KeyboardResizeToggleHandler();
    }
    
    public int getOrientation() {
        return this.orientation;
    }
    
    public void setOrientation(final int orientation) {
        this.orientation = orientation;
    }
    
    public boolean isContinuousLayout() {
        return this.continuousLayout;
    }
    
    public void setContinuousLayout(final boolean continuousLayout) {
        this.continuousLayout = continuousLayout;
    }
    
    public int getLastDragLocation() {
        return this.lastDragLocation;
    }
    
    public void setLastDragLocation(final int lastDragLocation) {
        this.lastDragLocation = lastDragLocation;
    }
    
    int getKeyboardMoveIncrement() {
        return 3;
    }
    
    public BasicSplitPaneDivider getDivider() {
        return this.divider;
    }
    
    protected Component createDefaultNonContinuousLayoutDivider() {
        return new Canvas() {
            @Override
            public void paint(final Graphics graphics) {
                if (!BasicSplitPaneUI.this.isContinuousLayout() && BasicSplitPaneUI.this.getLastDragLocation() != -1) {
                    final Dimension size = BasicSplitPaneUI.this.splitPane.getSize();
                    graphics.setColor(BasicSplitPaneUI.this.dividerDraggingColor);
                    if (BasicSplitPaneUI.this.orientation == 1) {
                        graphics.fillRect(0, 0, BasicSplitPaneUI.this.dividerSize - 1, size.height - 1);
                    }
                    else {
                        graphics.fillRect(0, 0, size.width - 1, BasicSplitPaneUI.this.dividerSize - 1);
                    }
                }
            }
        };
    }
    
    protected void setNonContinuousLayoutDivider(final Component component) {
        this.setNonContinuousLayoutDivider(component, true);
    }
    
    protected void setNonContinuousLayoutDivider(final Component nonContinuousLayoutDivider, final boolean rememberPaneSizes) {
        this.rememberPaneSizes = rememberPaneSizes;
        if (this.nonContinuousLayoutDivider != null && this.splitPane != null) {
            this.splitPane.remove(this.nonContinuousLayoutDivider);
        }
        this.nonContinuousLayoutDivider = nonContinuousLayoutDivider;
    }
    
    private void addHeavyweightDivider() {
        if (this.nonContinuousLayoutDivider != null && this.splitPane != null) {
            final Component leftComponent = this.splitPane.getLeftComponent();
            final Component rightComponent = this.splitPane.getRightComponent();
            final int dividerLocation = this.splitPane.getDividerLocation();
            if (leftComponent != null) {
                this.splitPane.setLeftComponent(null);
            }
            if (rightComponent != null) {
                this.splitPane.setRightComponent(null);
            }
            this.splitPane.remove(this.divider);
            this.splitPane.add(this.nonContinuousLayoutDivider, "nonContinuousDivider", this.splitPane.getComponentCount());
            this.splitPane.setLeftComponent(leftComponent);
            this.splitPane.setRightComponent(rightComponent);
            this.splitPane.add(this.divider, "divider");
            if (this.rememberPaneSizes) {
                this.splitPane.setDividerLocation(dividerLocation);
            }
        }
    }
    
    public Component getNonContinuousLayoutDivider() {
        return this.nonContinuousLayoutDivider;
    }
    
    public JSplitPane getSplitPane() {
        return this.splitPane;
    }
    
    public BasicSplitPaneDivider createDefaultDivider() {
        return new BasicSplitPaneDivider(this);
    }
    
    @Override
    public void resetToPreferredSizes(final JSplitPane splitPane) {
        if (this.splitPane != null) {
            this.layoutManager.resetToPreferredSizes();
            this.splitPane.revalidate();
            this.splitPane.repaint();
        }
    }
    
    @Override
    public void setDividerLocation(final JSplitPane splitPane, final int n) {
        if (!this.ignoreDividerLocationChange) {
            this.dividerLocationIsSet = true;
            this.splitPane.revalidate();
            this.splitPane.repaint();
            if (this.keepHidden) {
                final Insets insets = this.splitPane.getInsets();
                final int orientation = this.splitPane.getOrientation();
                if ((orientation == 0 && n != insets.top && n != this.splitPane.getHeight() - this.divider.getHeight() - insets.top) || (orientation == 1 && n != insets.left && n != this.splitPane.getWidth() - this.divider.getWidth() - insets.left)) {
                    this.setKeepHidden(false);
                }
            }
        }
        else {
            this.ignoreDividerLocationChange = false;
        }
    }
    
    @Override
    public int getDividerLocation(final JSplitPane splitPane) {
        if (this.orientation == 1) {
            return this.divider.getLocation().x;
        }
        return this.divider.getLocation().y;
    }
    
    @Override
    public int getMinimumDividerLocation(final JSplitPane splitPane) {
        int n = 0;
        final Component leftComponent = this.splitPane.getLeftComponent();
        if (leftComponent != null && leftComponent.isVisible()) {
            final Insets insets = this.splitPane.getInsets();
            final Dimension minimumSize = leftComponent.getMinimumSize();
            if (this.orientation == 1) {
                n = minimumSize.width;
            }
            else {
                n = minimumSize.height;
            }
            if (insets != null) {
                if (this.orientation == 1) {
                    n += insets.left;
                }
                else {
                    n += insets.top;
                }
            }
        }
        return n;
    }
    
    @Override
    public int getMaximumDividerLocation(final JSplitPane splitPane) {
        final Dimension size = this.splitPane.getSize();
        int n = 0;
        final Component rightComponent = this.splitPane.getRightComponent();
        if (rightComponent != null) {
            final Insets insets = this.splitPane.getInsets();
            Dimension minimumSize = new Dimension(0, 0);
            if (rightComponent.isVisible()) {
                minimumSize = rightComponent.getMinimumSize();
            }
            int n2;
            if (this.orientation == 1) {
                n2 = size.width - minimumSize.width;
            }
            else {
                n2 = size.height - minimumSize.height;
            }
            n = n2 - this.dividerSize;
            if (insets != null) {
                if (this.orientation == 1) {
                    n -= insets.right;
                }
                else {
                    n -= insets.top;
                }
            }
        }
        return Math.max(this.getMinimumDividerLocation(this.splitPane), n);
    }
    
    @Override
    public void finishedPaintingChildren(final JSplitPane splitPane, final Graphics graphics) {
        if (splitPane == this.splitPane && this.getLastDragLocation() != -1 && !this.isContinuousLayout() && !this.draggingHW) {
            final Dimension size = this.splitPane.getSize();
            graphics.setColor(this.dividerDraggingColor);
            if (this.orientation == 1) {
                graphics.fillRect(this.getLastDragLocation(), 0, this.dividerSize - 1, size.height - 1);
            }
            else {
                graphics.fillRect(0, this.lastDragLocation, size.width - 1, this.dividerSize - 1);
            }
        }
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        if (!this.painted && this.splitPane.getDividerLocation() < 0) {
            this.ignoreDividerLocationChange = true;
            this.splitPane.setDividerLocation(this.getDividerLocation(this.splitPane));
        }
        this.painted = true;
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        if (this.splitPane != null) {
            return this.layoutManager.preferredLayoutSize(this.splitPane);
        }
        return new Dimension(0, 0);
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        if (this.splitPane != null) {
            return this.layoutManager.minimumLayoutSize(this.splitPane);
        }
        return new Dimension(0, 0);
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        if (this.splitPane != null) {
            return this.layoutManager.maximumLayoutSize(this.splitPane);
        }
        return new Dimension(0, 0);
    }
    
    public Insets getInsets(final JComponent component) {
        return null;
    }
    
    protected void resetLayoutManager() {
        if (this.orientation == 1) {
            this.layoutManager = new BasicHorizontalLayoutManager(0);
        }
        else {
            this.layoutManager = new BasicHorizontalLayoutManager(1);
        }
        this.splitPane.setLayout(this.layoutManager);
        this.layoutManager.updateComponents();
        this.splitPane.revalidate();
        this.splitPane.repaint();
    }
    
    void setKeepHidden(final boolean keepHidden) {
        this.keepHidden = keepHidden;
    }
    
    private boolean getKeepHidden() {
        return this.keepHidden;
    }
    
    protected void startDragging() {
        final Component leftComponent = this.splitPane.getLeftComponent();
        final Component rightComponent = this.splitPane.getRightComponent();
        this.beginDragDividerLocation = this.getDividerLocation(this.splitPane);
        this.draggingHW = false;
        final ComponentPeer peer;
        if (leftComponent != null && (peer = leftComponent.getPeer()) != null && !(peer instanceof LightweightPeer)) {
            this.draggingHW = true;
        }
        else {
            final ComponentPeer peer2;
            if (rightComponent != null && (peer2 = rightComponent.getPeer()) != null && !(peer2 instanceof LightweightPeer)) {
                this.draggingHW = true;
            }
        }
        if (this.orientation == 1) {
            this.setLastDragLocation(this.divider.getBounds().x);
            this.dividerSize = this.divider.getSize().width;
            if (!this.isContinuousLayout() && this.draggingHW) {
                this.nonContinuousLayoutDivider.setBounds(this.getLastDragLocation(), 0, this.dividerSize, this.splitPane.getHeight());
                this.addHeavyweightDivider();
            }
        }
        else {
            this.setLastDragLocation(this.divider.getBounds().y);
            this.dividerSize = this.divider.getSize().height;
            if (!this.isContinuousLayout() && this.draggingHW) {
                this.nonContinuousLayoutDivider.setBounds(0, this.getLastDragLocation(), this.splitPane.getWidth(), this.dividerSize);
                this.addHeavyweightDivider();
            }
        }
    }
    
    protected void dragDividerTo(final int lastDragLocation) {
        if (this.getLastDragLocation() != lastDragLocation) {
            if (this.isContinuousLayout()) {
                this.splitPane.setDividerLocation(lastDragLocation);
                this.setLastDragLocation(lastDragLocation);
            }
            else {
                final int lastDragLocation2 = this.getLastDragLocation();
                this.setLastDragLocation(lastDragLocation);
                if (this.orientation == 1) {
                    if (this.draggingHW) {
                        this.nonContinuousLayoutDivider.setLocation(this.getLastDragLocation(), 0);
                    }
                    else {
                        final int height = this.splitPane.getHeight();
                        this.splitPane.repaint(lastDragLocation2, 0, this.dividerSize, height);
                        this.splitPane.repaint(lastDragLocation, 0, this.dividerSize, height);
                    }
                }
                else if (this.draggingHW) {
                    this.nonContinuousLayoutDivider.setLocation(0, this.getLastDragLocation());
                }
                else {
                    final int width = this.splitPane.getWidth();
                    this.splitPane.repaint(0, lastDragLocation2, width, this.dividerSize);
                    this.splitPane.repaint(0, lastDragLocation, width, this.dividerSize);
                }
            }
        }
    }
    
    protected void finishDraggingTo(final int dividerLocation) {
        this.dragDividerTo(dividerLocation);
        this.setLastDragLocation(-1);
        if (!this.isContinuousLayout()) {
            this.splitPane.getLeftComponent().getBounds();
            if (this.draggingHW) {
                if (this.orientation == 1) {
                    this.nonContinuousLayoutDivider.setLocation(-this.dividerSize, 0);
                }
                else {
                    this.nonContinuousLayoutDivider.setLocation(0, -this.dividerSize);
                }
                this.splitPane.remove(this.nonContinuousLayoutDivider);
            }
            this.splitPane.setDividerLocation(dividerLocation);
        }
    }
    
    @Deprecated
    protected int getDividerBorderSize() {
        return 1;
    }
    
    static {
        BasicSplitPaneUI.KEYBOARD_DIVIDER_MOVE_OFFSET = 3;
    }
    
    public class PropertyHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            BasicSplitPaneUI.this.getHandler().propertyChange(propertyChangeEvent);
        }
    }
    
    public class FocusHandler extends FocusAdapter
    {
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            BasicSplitPaneUI.this.getHandler().focusGained(focusEvent);
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            BasicSplitPaneUI.this.getHandler().focusLost(focusEvent);
        }
    }
    
    public class KeyboardUpLeftHandler implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicSplitPaneUI.this.dividerKeyboardResize) {
                BasicSplitPaneUI.this.splitPane.setDividerLocation(Math.max(0, BasicSplitPaneUI.this.getDividerLocation(BasicSplitPaneUI.this.splitPane) - BasicSplitPaneUI.this.getKeyboardMoveIncrement()));
            }
        }
    }
    
    public class KeyboardDownRightHandler implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicSplitPaneUI.this.dividerKeyboardResize) {
                BasicSplitPaneUI.this.splitPane.setDividerLocation(BasicSplitPaneUI.this.getDividerLocation(BasicSplitPaneUI.this.splitPane) + BasicSplitPaneUI.this.getKeyboardMoveIncrement());
            }
        }
    }
    
    public class KeyboardHomeHandler implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicSplitPaneUI.this.dividerKeyboardResize) {
                BasicSplitPaneUI.this.splitPane.setDividerLocation(0);
            }
        }
    }
    
    public class KeyboardEndHandler implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicSplitPaneUI.this.dividerKeyboardResize) {
                final Insets insets = BasicSplitPaneUI.this.splitPane.getInsets();
                final int n = (insets != null) ? insets.bottom : 0;
                final int n2 = (insets != null) ? insets.right : 0;
                if (BasicSplitPaneUI.this.orientation == 0) {
                    BasicSplitPaneUI.this.splitPane.setDividerLocation(BasicSplitPaneUI.this.splitPane.getHeight() - n);
                }
                else {
                    BasicSplitPaneUI.this.splitPane.setDividerLocation(BasicSplitPaneUI.this.splitPane.getWidth() - n2);
                }
            }
        }
    }
    
    public class KeyboardResizeToggleHandler implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (!BasicSplitPaneUI.this.dividerKeyboardResize) {
                BasicSplitPaneUI.this.splitPane.requestFocus();
            }
        }
    }
    
    public class BasicHorizontalLayoutManager implements LayoutManager2
    {
        protected int[] sizes;
        protected Component[] components;
        private int lastSplitPaneSize;
        private boolean doReset;
        private int axis;
        
        BasicHorizontalLayoutManager(final BasicSplitPaneUI basicSplitPaneUI) {
            this(basicSplitPaneUI, 0);
        }
        
        BasicHorizontalLayoutManager(final int axis) {
            this.axis = axis;
            this.components = new Component[3];
            final Component[] components = this.components;
            final int n = 0;
            final Component[] components2 = this.components;
            final int n2 = 1;
            final Component[] components3 = this.components;
            final int n3 = 2;
            final Component component = null;
            components3[n3] = component;
            components[n] = (components2[n2] = component);
            this.sizes = new int[3];
        }
        
        @Override
        public void layoutContainer(final Container container) {
            final Dimension size = container.getSize();
            if (size.height <= 0 || size.width <= 0) {
                this.lastSplitPaneSize = 0;
                return;
            }
            final int dividerLocation = BasicSplitPaneUI.this.splitPane.getDividerLocation();
            final Insets insets = BasicSplitPaneUI.this.splitPane.getInsets();
            final int availableSize = this.getAvailableSize(size, insets);
            this.getSizeForPrimaryAxis(size);
            BasicSplitPaneUI.this.getDividerLocation(BasicSplitPaneUI.this.splitPane);
            final int sizeForPrimaryAxis = this.getSizeForPrimaryAxis(insets, true);
            final Dimension dimension = (this.components[2] == null) ? null : this.components[2].getPreferredSize();
            if ((this.doReset && !BasicSplitPaneUI.this.dividerLocationIsSet) || dividerLocation < 0) {
                this.resetToPreferredSizes(availableSize);
            }
            else if (this.lastSplitPaneSize <= 0 || availableSize == this.lastSplitPaneSize || !BasicSplitPaneUI.this.painted || (dimension != null && this.getSizeForPrimaryAxis(dimension) != this.sizes[2])) {
                if (dimension != null) {
                    this.sizes[2] = this.getSizeForPrimaryAxis(dimension);
                }
                else {
                    this.sizes[2] = 0;
                }
                this.setDividerLocation(dividerLocation - sizeForPrimaryAxis, availableSize);
                BasicSplitPaneUI.this.dividerLocationIsSet = false;
            }
            else if (availableSize != this.lastSplitPaneSize) {
                this.distributeSpace(availableSize - this.lastSplitPaneSize, BasicSplitPaneUI.this.getKeepHidden());
            }
            this.doReset = false;
            BasicSplitPaneUI.this.dividerLocationIsSet = false;
            this.lastSplitPaneSize = availableSize;
            int initialLocation = this.getInitialLocation(insets);
            int i = 0;
            while (i < 3) {
                if (this.components[i] != null && this.components[i].isVisible()) {
                    this.setComponentToSize(this.components[i], this.sizes[i], initialLocation, insets, size);
                    initialLocation += this.sizes[i];
                }
                switch (i) {
                    case 0: {
                        i = 2;
                        continue;
                    }
                    case 2: {
                        i = 1;
                        continue;
                    }
                    case 1: {
                        i = 3;
                        continue;
                    }
                }
            }
            if (BasicSplitPaneUI.this.painted) {
                final int dividerLocation2 = BasicSplitPaneUI.this.getDividerLocation(BasicSplitPaneUI.this.splitPane);
                if (dividerLocation2 != dividerLocation - sizeForPrimaryAxis) {
                    final int lastDividerLocation = BasicSplitPaneUI.this.splitPane.getLastDividerLocation();
                    BasicSplitPaneUI.this.ignoreDividerLocationChange = true;
                    try {
                        BasicSplitPaneUI.this.splitPane.setDividerLocation(dividerLocation2);
                        BasicSplitPaneUI.this.splitPane.setLastDividerLocation(lastDividerLocation);
                    }
                    finally {
                        BasicSplitPaneUI.this.ignoreDividerLocationChange = false;
                    }
                }
            }
        }
        
        @Override
        public void addLayoutComponent(final String s, final Component component) {
            boolean b = true;
            if (s != null) {
                if (s.equals("divider")) {
                    this.components[2] = component;
                    this.sizes[2] = this.getSizeForPrimaryAxis(component.getPreferredSize());
                }
                else if (s.equals("left") || s.equals("top")) {
                    this.components[0] = component;
                    this.sizes[0] = 0;
                }
                else if (s.equals("right") || s.equals("bottom")) {
                    this.components[1] = component;
                    this.sizes[1] = 0;
                }
                else if (!s.equals("nonContinuousDivider")) {
                    b = false;
                }
            }
            else {
                b = false;
            }
            if (!b) {
                throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + s);
            }
            this.doReset = true;
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            int n = 0;
            int n2 = 0;
            final Insets insets = BasicSplitPaneUI.this.splitPane.getInsets();
            for (int i = 0; i < 3; ++i) {
                if (this.components[i] != null) {
                    final Dimension minimumSize = this.components[i].getMinimumSize();
                    final int sizeForSecondaryAxis = this.getSizeForSecondaryAxis(minimumSize);
                    n += this.getSizeForPrimaryAxis(minimumSize);
                    if (sizeForSecondaryAxis > n2) {
                        n2 = sizeForSecondaryAxis;
                    }
                }
            }
            if (insets != null) {
                n += this.getSizeForPrimaryAxis(insets, true) + this.getSizeForPrimaryAxis(insets, false);
                n2 += this.getSizeForSecondaryAxis(insets, true) + this.getSizeForSecondaryAxis(insets, false);
            }
            if (this.axis == 0) {
                return new Dimension(n, n2);
            }
            return new Dimension(n2, n);
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            int n = 0;
            int n2 = 0;
            final Insets insets = BasicSplitPaneUI.this.splitPane.getInsets();
            for (int i = 0; i < 3; ++i) {
                if (this.components[i] != null) {
                    final Dimension preferredSize = this.components[i].getPreferredSize();
                    final int sizeForSecondaryAxis = this.getSizeForSecondaryAxis(preferredSize);
                    n += this.getSizeForPrimaryAxis(preferredSize);
                    if (sizeForSecondaryAxis > n2) {
                        n2 = sizeForSecondaryAxis;
                    }
                }
            }
            if (insets != null) {
                n += this.getSizeForPrimaryAxis(insets, true) + this.getSizeForPrimaryAxis(insets, false);
                n2 += this.getSizeForSecondaryAxis(insets, true) + this.getSizeForSecondaryAxis(insets, false);
            }
            if (this.axis == 0) {
                return new Dimension(n, n2);
            }
            return new Dimension(n2, n);
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
            for (int i = 0; i < 3; ++i) {
                if (this.components[i] == component) {
                    this.components[i] = null;
                    this.sizes[i] = 0;
                    this.doReset = true;
                }
            }
        }
        
        @Override
        public void addLayoutComponent(final Component component, final Object o) {
            if (o == null || o instanceof String) {
                this.addLayoutComponent((String)o, component);
                return;
            }
            throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
        }
        
        @Override
        public float getLayoutAlignmentX(final Container container) {
            return 0.0f;
        }
        
        @Override
        public float getLayoutAlignmentY(final Container container) {
            return 0.0f;
        }
        
        @Override
        public void invalidateLayout(final Container container) {
        }
        
        @Override
        public Dimension maximumLayoutSize(final Container container) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        
        public void resetToPreferredSizes() {
            this.doReset = true;
        }
        
        protected void resetSizeAt(final int n) {
            this.sizes[n] = 0;
            this.doReset = true;
        }
        
        protected void setSizes(final int[] array) {
            System.arraycopy(array, 0, this.sizes, 0, 3);
        }
        
        protected int[] getSizes() {
            final int[] array = new int[3];
            System.arraycopy(this.sizes, 0, array, 0, 3);
            return array;
        }
        
        protected int getPreferredSizeOfComponent(final Component component) {
            return this.getSizeForPrimaryAxis(component.getPreferredSize());
        }
        
        int getMinimumSizeOfComponent(final Component component) {
            return this.getSizeForPrimaryAxis(component.getMinimumSize());
        }
        
        protected int getSizeOfComponent(final Component component) {
            return this.getSizeForPrimaryAxis(component.getSize());
        }
        
        protected int getAvailableSize(final Dimension dimension, final Insets insets) {
            if (insets == null) {
                return this.getSizeForPrimaryAxis(dimension);
            }
            return this.getSizeForPrimaryAxis(dimension) - (this.getSizeForPrimaryAxis(insets, true) + this.getSizeForPrimaryAxis(insets, false));
        }
        
        protected int getInitialLocation(final Insets insets) {
            if (insets != null) {
                return this.getSizeForPrimaryAxis(insets, true);
            }
            return 0;
        }
        
        protected void setComponentToSize(final Component component, final int n, final int n2, final Insets insets, final Dimension dimension) {
            if (insets != null) {
                if (this.axis == 0) {
                    component.setBounds(n2, insets.top, n, dimension.height - (insets.top + insets.bottom));
                }
                else {
                    component.setBounds(insets.left, n2, dimension.width - (insets.left + insets.right), n);
                }
            }
            else if (this.axis == 0) {
                component.setBounds(n2, 0, n, dimension.height);
            }
            else {
                component.setBounds(0, n2, dimension.width, n);
            }
        }
        
        int getSizeForPrimaryAxis(final Dimension dimension) {
            if (this.axis == 0) {
                return dimension.width;
            }
            return dimension.height;
        }
        
        int getSizeForSecondaryAxis(final Dimension dimension) {
            if (this.axis == 0) {
                return dimension.height;
            }
            return dimension.width;
        }
        
        int getSizeForPrimaryAxis(final Insets insets, final boolean b) {
            if (this.axis == 0) {
                if (b) {
                    return insets.left;
                }
                return insets.right;
            }
            else {
                if (b) {
                    return insets.top;
                }
                return insets.bottom;
            }
        }
        
        int getSizeForSecondaryAxis(final Insets insets, final boolean b) {
            if (this.axis == 0) {
                if (b) {
                    return insets.top;
                }
                return insets.bottom;
            }
            else {
                if (b) {
                    return insets.left;
                }
                return insets.right;
            }
        }
        
        protected void updateComponents() {
            final Component leftComponent = BasicSplitPaneUI.this.splitPane.getLeftComponent();
            if (this.components[0] != leftComponent) {
                if ((this.components[0] = leftComponent) == null) {
                    this.sizes[0] = 0;
                }
                else {
                    this.sizes[0] = -1;
                }
            }
            final Component rightComponent = BasicSplitPaneUI.this.splitPane.getRightComponent();
            if (this.components[1] != rightComponent) {
                if ((this.components[1] = rightComponent) == null) {
                    this.sizes[1] = 0;
                }
                else {
                    this.sizes[1] = -1;
                }
            }
            final Component[] components = BasicSplitPaneUI.this.splitPane.getComponents();
            final Component component = this.components[2];
            this.components[2] = null;
            int i = components.length - 1;
            while (i >= 0) {
                if (components[i] != this.components[0] && components[i] != this.components[1] && components[i] != BasicSplitPaneUI.this.nonContinuousLayoutDivider) {
                    if (component != components[i]) {
                        this.components[2] = components[i];
                        break;
                    }
                    this.components[2] = component;
                    break;
                }
                else {
                    --i;
                }
            }
            if (this.components[2] == null) {
                this.sizes[2] = 0;
            }
            else {
                this.sizes[2] = this.getSizeForPrimaryAxis(this.components[2].getPreferredSize());
            }
        }
        
        void setDividerLocation(int max, final int n) {
            final boolean b = this.components[0] != null && this.components[0].isVisible();
            final boolean b2 = this.components[1] != null && this.components[1].isVisible();
            final boolean b3 = this.components[2] != null && this.components[2].isVisible();
            int n2 = n;
            if (b3) {
                n2 -= this.sizes[2];
            }
            max = Math.max(0, Math.min(max, n2));
            if (b) {
                if (b2) {
                    this.sizes[0] = max;
                    this.sizes[1] = n2 - max;
                }
                else {
                    this.sizes[0] = n2;
                    this.sizes[1] = 0;
                }
            }
            else if (b2) {
                this.sizes[1] = n2;
                this.sizes[0] = 0;
            }
        }
        
        int[] getPreferredSizes() {
            final int[] array = new int[3];
            for (int i = 0; i < 3; ++i) {
                if (this.components[i] != null && this.components[i].isVisible()) {
                    array[i] = this.getPreferredSizeOfComponent(this.components[i]);
                }
                else {
                    array[i] = -1;
                }
            }
            return array;
        }
        
        int[] getMinimumSizes() {
            final int[] array = new int[3];
            for (int i = 0; i < 2; ++i) {
                if (this.components[i] != null && this.components[i].isVisible()) {
                    array[i] = this.getMinimumSizeOfComponent(this.components[i]);
                }
                else {
                    array[i] = -1;
                }
            }
            array[2] = ((this.components[2] != null) ? this.getMinimumSizeOfComponent(this.components[2]) : -1);
            return array;
        }
        
        void resetToPreferredSizes(final int n) {
            int[] sizes = this.getPreferredSizes();
            int n2 = 0;
            for (int i = 0; i < 3; ++i) {
                if (sizes[i] != -1) {
                    n2 += sizes[i];
                }
            }
            if (n2 > n) {
                sizes = this.getMinimumSizes();
                n2 = 0;
                for (int j = 0; j < 3; ++j) {
                    if (sizes[j] != -1) {
                        n2 += sizes[j];
                    }
                }
            }
            this.setSizes(sizes);
            this.distributeSpace(n - n2, false);
        }
        
        void distributeSpace(final int n, final boolean b) {
            int n2 = (this.components[0] != null && this.components[0].isVisible()) ? 1 : 0;
            int n3 = (this.components[1] != null && this.components[1].isVisible()) ? 1 : 0;
            if (b) {
                if (n2 != 0 && this.getSizeForPrimaryAxis(this.components[0].getSize()) == 0) {
                    n2 = 0;
                    if (n3 != 0 && this.getSizeForPrimaryAxis(this.components[1].getSize()) == 0) {
                        n2 = 1;
                    }
                }
                else if (n3 != 0 && this.getSizeForPrimaryAxis(this.components[1].getSize()) == 0) {
                    n3 = 0;
                }
            }
            if (n2 != 0 && n3 != 0) {
                final int n4 = (int)(BasicSplitPaneUI.this.splitPane.getResizeWeight() * n);
                final int n5 = n - n4;
                final int[] sizes = this.sizes;
                final int n6 = 0;
                sizes[n6] += n4;
                final int[] sizes2 = this.sizes;
                final int n7 = 1;
                sizes2[n7] += n5;
                final int minimumSizeOfComponent = this.getMinimumSizeOfComponent(this.components[0]);
                final int minimumSizeOfComponent2 = this.getMinimumSizeOfComponent(this.components[1]);
                final boolean b2 = this.sizes[0] >= minimumSizeOfComponent;
                final boolean b3 = this.sizes[1] >= minimumSizeOfComponent2;
                if (!b2 && !b3) {
                    if (this.sizes[0] < 0) {
                        final int[] sizes3 = this.sizes;
                        final int n8 = 1;
                        sizes3[n8] += this.sizes[0];
                        this.sizes[0] = 0;
                    }
                    else if (this.sizes[1] < 0) {
                        final int[] sizes4 = this.sizes;
                        final int n9 = 0;
                        sizes4[n9] += this.sizes[1];
                        this.sizes[1] = 0;
                    }
                }
                else if (!b2) {
                    if (this.sizes[1] - (minimumSizeOfComponent - this.sizes[0]) < minimumSizeOfComponent2) {
                        if (this.sizes[0] < 0) {
                            final int[] sizes5 = this.sizes;
                            final int n10 = 1;
                            sizes5[n10] += this.sizes[0];
                            this.sizes[0] = 0;
                        }
                    }
                    else {
                        final int[] sizes6 = this.sizes;
                        final int n11 = 1;
                        sizes6[n11] -= minimumSizeOfComponent - this.sizes[0];
                        this.sizes[0] = minimumSizeOfComponent;
                    }
                }
                else if (!b3) {
                    if (this.sizes[0] - (minimumSizeOfComponent2 - this.sizes[1]) < minimumSizeOfComponent) {
                        if (this.sizes[1] < 0) {
                            final int[] sizes7 = this.sizes;
                            final int n12 = 0;
                            sizes7[n12] += this.sizes[1];
                            this.sizes[1] = 0;
                        }
                    }
                    else {
                        final int[] sizes8 = this.sizes;
                        final int n13 = 0;
                        sizes8[n13] -= minimumSizeOfComponent2 - this.sizes[1];
                        this.sizes[1] = minimumSizeOfComponent2;
                    }
                }
                if (this.sizes[0] < 0) {
                    this.sizes[0] = 0;
                }
                if (this.sizes[1] < 0) {
                    this.sizes[1] = 0;
                }
            }
            else if (n2 != 0) {
                this.sizes[0] = Math.max(0, this.sizes[0] + n);
            }
            else if (n3 != 0) {
                this.sizes[1] = Math.max(0, this.sizes[1] + n);
            }
        }
    }
    
    public class BasicVerticalLayoutManager extends BasicHorizontalLayoutManager
    {
        public BasicVerticalLayoutManager() {
            super(1);
        }
    }
    
    private class Handler implements FocusListener, PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getSource() == BasicSplitPaneUI.this.splitPane) {
                final String propertyName = propertyChangeEvent.getPropertyName();
                if (propertyName == "orientation") {
                    BasicSplitPaneUI.this.orientation = BasicSplitPaneUI.this.splitPane.getOrientation();
                    BasicSplitPaneUI.this.resetLayoutManager();
                }
                else if (propertyName == "continuousLayout") {
                    BasicSplitPaneUI.this.setContinuousLayout(BasicSplitPaneUI.this.splitPane.isContinuousLayout());
                    if (!BasicSplitPaneUI.this.isContinuousLayout()) {
                        if (BasicSplitPaneUI.this.nonContinuousLayoutDivider == null) {
                            BasicSplitPaneUI.this.setNonContinuousLayoutDivider(BasicSplitPaneUI.this.createDefaultNonContinuousLayoutDivider(), true);
                        }
                        else if (BasicSplitPaneUI.this.nonContinuousLayoutDivider.getParent() == null) {
                            BasicSplitPaneUI.this.setNonContinuousLayoutDivider(BasicSplitPaneUI.this.nonContinuousLayoutDivider, true);
                        }
                    }
                }
                else if (propertyName == "dividerSize") {
                    BasicSplitPaneUI.this.divider.setDividerSize(BasicSplitPaneUI.this.splitPane.getDividerSize());
                    BasicSplitPaneUI.this.dividerSize = BasicSplitPaneUI.this.divider.getDividerSize();
                    BasicSplitPaneUI.this.splitPane.revalidate();
                    BasicSplitPaneUI.this.splitPane.repaint();
                }
            }
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            BasicSplitPaneUI.this.dividerKeyboardResize = true;
            BasicSplitPaneUI.this.splitPane.repaint();
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            BasicSplitPaneUI.this.dividerKeyboardResize = false;
            BasicSplitPaneUI.this.splitPane.repaint();
        }
    }
    
    private static class Actions extends UIAction
    {
        private static final String NEGATIVE_INCREMENT = "negativeIncrement";
        private static final String POSITIVE_INCREMENT = "positiveIncrement";
        private static final String SELECT_MIN = "selectMin";
        private static final String SELECT_MAX = "selectMax";
        private static final String START_RESIZE = "startResize";
        private static final String TOGGLE_FOCUS = "toggleFocus";
        private static final String FOCUS_OUT_FORWARD = "focusOutForward";
        private static final String FOCUS_OUT_BACKWARD = "focusOutBackward";
        
        Actions(final String s) {
            super(s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JSplitPane splitPane = (JSplitPane)actionEvent.getSource();
            final BasicSplitPaneUI basicSplitPaneUI = (BasicSplitPaneUI)BasicLookAndFeel.getUIOfType(splitPane.getUI(), BasicSplitPaneUI.class);
            if (basicSplitPaneUI == null) {
                return;
            }
            final String name = this.getName();
            if (name == "negativeIncrement") {
                if (basicSplitPaneUI.dividerKeyboardResize) {
                    splitPane.setDividerLocation(Math.max(0, basicSplitPaneUI.getDividerLocation(splitPane) - basicSplitPaneUI.getKeyboardMoveIncrement()));
                }
            }
            else if (name == "positiveIncrement") {
                if (basicSplitPaneUI.dividerKeyboardResize) {
                    splitPane.setDividerLocation(basicSplitPaneUI.getDividerLocation(splitPane) + basicSplitPaneUI.getKeyboardMoveIncrement());
                }
            }
            else if (name == "selectMin") {
                if (basicSplitPaneUI.dividerKeyboardResize) {
                    splitPane.setDividerLocation(0);
                }
            }
            else if (name == "selectMax") {
                if (basicSplitPaneUI.dividerKeyboardResize) {
                    final Insets insets = splitPane.getInsets();
                    final int n = (insets != null) ? insets.bottom : 0;
                    final int n2 = (insets != null) ? insets.right : 0;
                    if (basicSplitPaneUI.orientation == 0) {
                        splitPane.setDividerLocation(splitPane.getHeight() - n);
                    }
                    else {
                        splitPane.setDividerLocation(splitPane.getWidth() - n2);
                    }
                }
            }
            else if (name == "startResize") {
                if (!basicSplitPaneUI.dividerKeyboardResize) {
                    splitPane.requestFocus();
                }
                else {
                    final JSplitPane splitPane2 = (JSplitPane)SwingUtilities.getAncestorOfClass(JSplitPane.class, splitPane);
                    if (splitPane2 != null) {
                        splitPane2.requestFocus();
                    }
                }
            }
            else if (name == "toggleFocus") {
                this.toggleFocus(splitPane);
            }
            else if (name == "focusOutForward") {
                this.moveFocus(splitPane, 1);
            }
            else if (name == "focusOutBackward") {
                this.moveFocus(splitPane, -1);
            }
        }
        
        private void moveFocus(final JSplitPane splitPane, final int n) {
            final Container focusCycleRootAncestor = splitPane.getFocusCycleRootAncestor();
            final FocusTraversalPolicy focusTraversalPolicy = focusCycleRootAncestor.getFocusTraversalPolicy();
            Component component = (n > 0) ? focusTraversalPolicy.getComponentAfter(focusCycleRootAncestor, splitPane) : focusTraversalPolicy.getComponentBefore(focusCycleRootAncestor, splitPane);
            final HashSet set = new HashSet();
            if (splitPane.isAncestorOf(component)) {
                do {
                    set.add(component);
                    final Container focusCycleRootAncestor2 = component.getFocusCycleRootAncestor();
                    final FocusTraversalPolicy focusTraversalPolicy2 = focusCycleRootAncestor2.getFocusTraversalPolicy();
                    component = ((n > 0) ? focusTraversalPolicy2.getComponentAfter(focusCycleRootAncestor2, component) : focusTraversalPolicy2.getComponentBefore(focusCycleRootAncestor2, component));
                } while (splitPane.isAncestorOf(component) && !set.contains(component));
            }
            if (component != null && !splitPane.isAncestorOf(component)) {
                component.requestFocus();
            }
        }
        
        private void toggleFocus(final JSplitPane splitPane) {
            final Component leftComponent = splitPane.getLeftComponent();
            final Component rightComponent = splitPane.getRightComponent();
            final Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            final Component nextSide = this.getNextSide(splitPane, focusOwner);
            if (nextSide != null) {
                if (focusOwner != null && ((SwingUtilities.isDescendingFrom(focusOwner, leftComponent) && SwingUtilities.isDescendingFrom(nextSide, leftComponent)) || (SwingUtilities.isDescendingFrom(focusOwner, rightComponent) && SwingUtilities.isDescendingFrom(nextSide, rightComponent)))) {
                    return;
                }
                SwingUtilities2.compositeRequestFocus(nextSide);
            }
        }
        
        private Component getNextSide(final JSplitPane splitPane, final Component component) {
            final Component leftComponent = splitPane.getLeftComponent();
            final Component rightComponent = splitPane.getRightComponent();
            if (component != null && SwingUtilities.isDescendingFrom(component, leftComponent) && rightComponent != null) {
                final Component firstAvailableComponent = this.getFirstAvailableComponent(rightComponent);
                if (firstAvailableComponent != null) {
                    return firstAvailableComponent;
                }
            }
            final JSplitPane splitPane2 = (JSplitPane)SwingUtilities.getAncestorOfClass(JSplitPane.class, splitPane);
            Component component2;
            if (splitPane2 != null) {
                component2 = this.getNextSide(splitPane2, component);
            }
            else {
                component2 = this.getFirstAvailableComponent(leftComponent);
                if (component2 == null) {
                    component2 = this.getFirstAvailableComponent(rightComponent);
                }
            }
            return component2;
        }
        
        private Component getFirstAvailableComponent(Component firstAvailableComponent) {
            if (firstAvailableComponent != null && firstAvailableComponent instanceof JSplitPane) {
                final JSplitPane splitPane = (JSplitPane)firstAvailableComponent;
                final Component firstAvailableComponent2 = this.getFirstAvailableComponent(splitPane.getLeftComponent());
                if (firstAvailableComponent2 != null) {
                    firstAvailableComponent = firstAvailableComponent2;
                }
                else {
                    firstAvailableComponent = this.getFirstAvailableComponent(splitPane.getRightComponent());
                }
            }
            return firstAvailableComponent;
        }
    }
}
