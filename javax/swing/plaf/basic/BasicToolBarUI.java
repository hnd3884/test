package javax.swing.plaf.basic;

import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.awt.event.MouseEvent;
import java.awt.event.FocusEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JToggleButton;
import java.awt.Dialog;
import java.awt.Frame;
import javax.swing.JDialog;
import java.awt.Window;
import javax.swing.JRootPane;
import java.awt.GraphicsConfiguration;
import javax.swing.UIDefaults;
import javax.swing.border.CompoundBorder;
import javax.swing.ActionMap;
import javax.swing.Action;
import sun.swing.DefaultLookup;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import java.awt.Point;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.util.Hashtable;
import javax.swing.AbstractButton;
import java.util.HashMap;
import javax.swing.border.Border;
import java.awt.event.FocusListener;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeListener;
import javax.swing.event.MouseInputListener;
import java.awt.Color;
import java.awt.Container;
import javax.swing.RootPaneContainer;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.plaf.ToolBarUI;

public class BasicToolBarUI extends ToolBarUI implements SwingConstants
{
    protected JToolBar toolBar;
    private boolean floating;
    private int floatingX;
    private int floatingY;
    private JFrame floatingFrame;
    private RootPaneContainer floatingToolBar;
    protected DragWindow dragWindow;
    private Container dockingSource;
    private int dockingSensitivity;
    protected int focusedCompIndex;
    protected Color dockingColor;
    protected Color floatingColor;
    protected Color dockingBorderColor;
    protected Color floatingBorderColor;
    protected MouseInputListener dockingListener;
    protected PropertyChangeListener propertyListener;
    protected ContainerListener toolBarContListener;
    protected FocusListener toolBarFocusListener;
    private Handler handler;
    protected String constraintBeforeFloating;
    private static String IS_ROLLOVER;
    private static Border rolloverBorder;
    private static Border nonRolloverBorder;
    private static Border nonRolloverToggleBorder;
    private boolean rolloverBorders;
    private HashMap<AbstractButton, Border> borderTable;
    private Hashtable<AbstractButton, Boolean> rolloverTable;
    @Deprecated
    protected KeyStroke upKey;
    @Deprecated
    protected KeyStroke downKey;
    @Deprecated
    protected KeyStroke leftKey;
    @Deprecated
    protected KeyStroke rightKey;
    private static String FOCUSED_COMP_INDEX;
    
    public BasicToolBarUI() {
        this.dockingSensitivity = 0;
        this.focusedCompIndex = -1;
        this.dockingColor = null;
        this.floatingColor = null;
        this.dockingBorderColor = null;
        this.floatingBorderColor = null;
        this.constraintBeforeFloating = "North";
        this.rolloverBorders = false;
        this.borderTable = new HashMap<AbstractButton, Border>();
        this.rolloverTable = new Hashtable<AbstractButton, Boolean>();
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicToolBarUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.toolBar = (JToolBar)component;
        this.installDefaults();
        this.installComponents();
        this.installListeners();
        this.installKeyboardActions();
        this.dockingSensitivity = 0;
        this.floating = false;
        final int n = 0;
        this.floatingY = n;
        this.floatingX = n;
        this.floatingToolBar = null;
        this.setOrientation(this.toolBar.getOrientation());
        LookAndFeel.installProperty(component, "opaque", Boolean.TRUE);
        if (component.getClientProperty(BasicToolBarUI.FOCUSED_COMP_INDEX) != null) {
            this.focusedCompIndex = (int)component.getClientProperty(BasicToolBarUI.FOCUSED_COMP_INDEX);
        }
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallDefaults();
        this.uninstallComponents();
        this.uninstallListeners();
        this.uninstallKeyboardActions();
        if (this.isFloating()) {
            this.setFloating(false, null);
        }
        this.floatingToolBar = null;
        this.dragWindow = null;
        this.dockingSource = null;
        component.putClientProperty(BasicToolBarUI.FOCUSED_COMP_INDEX, this.focusedCompIndex);
    }
    
    protected void installDefaults() {
        LookAndFeel.installBorder(this.toolBar, "ToolBar.border");
        LookAndFeel.installColorsAndFont(this.toolBar, "ToolBar.background", "ToolBar.foreground", "ToolBar.font");
        if (this.dockingColor == null || this.dockingColor instanceof UIResource) {
            this.dockingColor = UIManager.getColor("ToolBar.dockingBackground");
        }
        if (this.floatingColor == null || this.floatingColor instanceof UIResource) {
            this.floatingColor = UIManager.getColor("ToolBar.floatingBackground");
        }
        if (this.dockingBorderColor == null || this.dockingBorderColor instanceof UIResource) {
            this.dockingBorderColor = UIManager.getColor("ToolBar.dockingForeground");
        }
        if (this.floatingBorderColor == null || this.floatingBorderColor instanceof UIResource) {
            this.floatingBorderColor = UIManager.getColor("ToolBar.floatingForeground");
        }
        Object o = this.toolBar.getClientProperty(BasicToolBarUI.IS_ROLLOVER);
        if (o == null) {
            o = UIManager.get("ToolBar.isRollover");
        }
        if (o != null) {
            this.rolloverBorders = (boolean)o;
        }
        if (BasicToolBarUI.rolloverBorder == null) {
            BasicToolBarUI.rolloverBorder = this.createRolloverBorder();
        }
        if (BasicToolBarUI.nonRolloverBorder == null) {
            BasicToolBarUI.nonRolloverBorder = this.createNonRolloverBorder();
        }
        if (BasicToolBarUI.nonRolloverToggleBorder == null) {
            BasicToolBarUI.nonRolloverToggleBorder = this.createNonRolloverToggleBorder();
        }
        this.setRolloverBorders(this.isRolloverBorders());
    }
    
    protected void uninstallDefaults() {
        LookAndFeel.uninstallBorder(this.toolBar);
        this.dockingColor = null;
        this.floatingColor = null;
        this.dockingBorderColor = null;
        this.floatingBorderColor = null;
        this.installNormalBorders(this.toolBar);
        BasicToolBarUI.rolloverBorder = null;
        BasicToolBarUI.nonRolloverBorder = null;
        BasicToolBarUI.nonRolloverToggleBorder = null;
    }
    
    protected void installComponents() {
    }
    
    protected void uninstallComponents() {
    }
    
    protected void installListeners() {
        this.dockingListener = this.createDockingListener();
        if (this.dockingListener != null) {
            this.toolBar.addMouseMotionListener(this.dockingListener);
            this.toolBar.addMouseListener(this.dockingListener);
        }
        this.propertyListener = this.createPropertyListener();
        if (this.propertyListener != null) {
            this.toolBar.addPropertyChangeListener(this.propertyListener);
        }
        this.toolBarContListener = this.createToolBarContListener();
        if (this.toolBarContListener != null) {
            this.toolBar.addContainerListener(this.toolBarContListener);
        }
        this.toolBarFocusListener = this.createToolBarFocusListener();
        if (this.toolBarFocusListener != null) {
            final Component[] components = this.toolBar.getComponents();
            for (int length = components.length, i = 0; i < length; ++i) {
                components[i].addFocusListener(this.toolBarFocusListener);
            }
        }
    }
    
    protected void uninstallListeners() {
        if (this.dockingListener != null) {
            this.toolBar.removeMouseMotionListener(this.dockingListener);
            this.toolBar.removeMouseListener(this.dockingListener);
            this.dockingListener = null;
        }
        if (this.propertyListener != null) {
            this.toolBar.removePropertyChangeListener(this.propertyListener);
            this.propertyListener = null;
        }
        if (this.toolBarContListener != null) {
            this.toolBar.removeContainerListener(this.toolBarContListener);
            this.toolBarContListener = null;
        }
        if (this.toolBarFocusListener != null) {
            final Component[] components = this.toolBar.getComponents();
            for (int length = components.length, i = 0; i < length; ++i) {
                components[i].removeFocusListener(this.toolBarFocusListener);
            }
            this.toolBarFocusListener = null;
        }
        this.handler = null;
    }
    
    protected void installKeyboardActions() {
        SwingUtilities.replaceUIInputMap(this.toolBar, 1, this.getInputMap(1));
        LazyActionMap.installLazyActionMap(this.toolBar, BasicToolBarUI.class, "ToolBar.actionMap");
    }
    
    InputMap getInputMap(final int n) {
        if (n == 1) {
            return (InputMap)DefaultLookup.get(this.toolBar, this, "ToolBar.ancestorInputMap");
        }
        return null;
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("navigateRight"));
        lazyActionMap.put(new Actions("navigateLeft"));
        lazyActionMap.put(new Actions("navigateUp"));
        lazyActionMap.put(new Actions("navigateDown"));
    }
    
    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIActionMap(this.toolBar, null);
        SwingUtilities.replaceUIInputMap(this.toolBar, 1, null);
    }
    
    protected void navigateFocusedComp(final int n) {
        final int componentCount = this.toolBar.getComponentCount();
        switch (n) {
            case 3:
            case 5: {
                if (this.focusedCompIndex < 0) {
                    break;
                }
                if (this.focusedCompIndex >= componentCount) {
                    break;
                }
                int i = this.focusedCompIndex + 1;
                while (i != this.focusedCompIndex) {
                    if (i >= componentCount) {
                        i = 0;
                    }
                    final Component componentAtIndex = this.toolBar.getComponentAtIndex(i++);
                    if (componentAtIndex != null && componentAtIndex.isFocusTraversable() && componentAtIndex.isEnabled()) {
                        componentAtIndex.requestFocus();
                        break;
                    }
                }
                break;
            }
            case 1:
            case 7: {
                if (this.focusedCompIndex < 0) {
                    break;
                }
                if (this.focusedCompIndex >= componentCount) {
                    break;
                }
                int j = this.focusedCompIndex - 1;
                while (j != this.focusedCompIndex) {
                    if (j < 0) {
                        j = componentCount - 1;
                    }
                    final Component componentAtIndex2 = this.toolBar.getComponentAtIndex(j--);
                    if (componentAtIndex2 != null && componentAtIndex2.isFocusTraversable() && componentAtIndex2.isEnabled()) {
                        componentAtIndex2.requestFocus();
                        break;
                    }
                }
                break;
            }
        }
    }
    
    protected Border createRolloverBorder() {
        final Object value = UIManager.get("ToolBar.rolloverBorder");
        if (value != null) {
            return (Border)value;
        }
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        return new CompoundBorder(new BasicBorders.RolloverButtonBorder(lookAndFeelDefaults.getColor("controlShadow"), lookAndFeelDefaults.getColor("controlDkShadow"), lookAndFeelDefaults.getColor("controlHighlight"), lookAndFeelDefaults.getColor("controlLtHighlight")), new BasicBorders.RolloverMarginBorder());
    }
    
    protected Border createNonRolloverBorder() {
        final Object value = UIManager.get("ToolBar.nonrolloverBorder");
        if (value != null) {
            return (Border)value;
        }
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        return new CompoundBorder(new BasicBorders.ButtonBorder(lookAndFeelDefaults.getColor("Button.shadow"), lookAndFeelDefaults.getColor("Button.darkShadow"), lookAndFeelDefaults.getColor("Button.light"), lookAndFeelDefaults.getColor("Button.highlight")), new BasicBorders.RolloverMarginBorder());
    }
    
    private Border createNonRolloverToggleBorder() {
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        return new CompoundBorder(new BasicBorders.RadioButtonBorder(lookAndFeelDefaults.getColor("ToggleButton.shadow"), lookAndFeelDefaults.getColor("ToggleButton.darkShadow"), lookAndFeelDefaults.getColor("ToggleButton.light"), lookAndFeelDefaults.getColor("ToggleButton.highlight")), new BasicBorders.RolloverMarginBorder());
    }
    
    protected JFrame createFloatingFrame(final JToolBar toolBar) {
        final Window windowAncestor = SwingUtilities.getWindowAncestor(toolBar);
        final JFrame frame = new JFrame(toolBar.getName(), (windowAncestor != null) ? windowAncestor.getGraphicsConfiguration() : null) {
            @Override
            protected JRootPane createRootPane() {
                final JRootPane rootPane = new JRootPane() {
                    private boolean packing = false;
                    
                    @Override
                    public void validate() {
                        super.validate();
                        if (!this.packing) {
                            this.packing = true;
                            JFrame.this.pack();
                            this.packing = false;
                        }
                    }
                };
                rootPane.setOpaque(true);
                return rootPane;
            }
        };
        frame.getRootPane().setName("ToolBar.FloatingFrame");
        frame.setResizable(false);
        frame.addWindowListener(this.createFrameListener());
        return frame;
    }
    
    protected RootPaneContainer createFloatingWindow(final JToolBar toolBar) {
        final Window windowAncestor = SwingUtilities.getWindowAncestor(toolBar);
        class ToolBarDialog extends JDialog
        {
            public ToolBarDialog(final String s, final boolean b) {
                super(frame, s, b);
            }
            
            public ToolBarDialog(final String s, final boolean b) {
                super(dialog, s, b);
            }
            
            @Override
            protected JRootPane createRootPane() {
                final JRootPane rootPane = new JRootPane() {
                    private boolean packing = false;
                    
                    @Override
                    public void validate() {
                        super.validate();
                        if (!this.packing) {
                            this.packing = true;
                            ToolBarDialog.this.pack();
                            this.packing = false;
                        }
                    }
                };
                rootPane.setOpaque(true);
                return rootPane;
            }
        }
        ToolBarDialog toolBarDialog;
        if (windowAncestor instanceof Frame) {
            toolBarDialog = new ToolBarDialog(toolBar.getName(), false);
        }
        else if (windowAncestor instanceof Dialog) {
            toolBarDialog = new ToolBarDialog(toolBar.getName(), false);
        }
        else {
            toolBarDialog = new ToolBarDialog(toolBar.getName(), false);
        }
        toolBarDialog.getRootPane().setName("ToolBar.FloatingWindow");
        toolBarDialog.setTitle(toolBar.getName());
        toolBarDialog.setResizable(false);
        toolBarDialog.addWindowListener(this.createFrameListener());
        return toolBarDialog;
    }
    
    protected DragWindow createDragWindow(final JToolBar toolBar) {
        Window window = null;
        if (this.toolBar != null) {
            Container container;
            for (container = this.toolBar.getParent(); container != null && !(container instanceof Window); container = container.getParent()) {}
            if (container != null && container instanceof Window) {
                window = (Window)container;
            }
        }
        if (this.floatingToolBar == null) {
            this.floatingToolBar = this.createFloatingWindow(this.toolBar);
        }
        if (this.floatingToolBar instanceof Window) {
            window = (Window)this.floatingToolBar;
        }
        return new DragWindow(window);
    }
    
    public boolean isRolloverBorders() {
        return this.rolloverBorders;
    }
    
    public void setRolloverBorders(final boolean rolloverBorders) {
        this.rolloverBorders = rolloverBorders;
        if (this.rolloverBorders) {
            this.installRolloverBorders(this.toolBar);
        }
        else {
            this.installNonRolloverBorders(this.toolBar);
        }
    }
    
    protected void installRolloverBorders(final JComponent component) {
        for (final Component borderToRollover : component.getComponents()) {
            if (borderToRollover instanceof JComponent) {
                ((JComponent)borderToRollover).updateUI();
                this.setBorderToRollover(borderToRollover);
            }
        }
    }
    
    protected void installNonRolloverBorders(final JComponent component) {
        for (final Component borderToNonRollover : component.getComponents()) {
            if (borderToNonRollover instanceof JComponent) {
                ((JComponent)borderToNonRollover).updateUI();
                this.setBorderToNonRollover(borderToNonRollover);
            }
        }
    }
    
    protected void installNormalBorders(final JComponent component) {
        final Component[] components = component.getComponents();
        for (int length = components.length, i = 0; i < length; ++i) {
            this.setBorderToNormal(components[i]);
        }
    }
    
    protected void setBorderToRollover(final Component component) {
        if (component instanceof AbstractButton) {
            final AbstractButton abstractButton = (AbstractButton)component;
            final Border border = this.borderTable.get(abstractButton);
            if (border == null || border instanceof UIResource) {
                this.borderTable.put(abstractButton, abstractButton.getBorder());
            }
            if (abstractButton.getBorder() instanceof UIResource) {
                abstractButton.setBorder(this.getRolloverBorder(abstractButton));
            }
            this.rolloverTable.put(abstractButton, abstractButton.isRolloverEnabled() ? Boolean.TRUE : Boolean.FALSE);
            abstractButton.setRolloverEnabled(true);
        }
    }
    
    protected Border getRolloverBorder(final AbstractButton abstractButton) {
        return BasicToolBarUI.rolloverBorder;
    }
    
    protected void setBorderToNonRollover(final Component component) {
        if (component instanceof AbstractButton) {
            final AbstractButton abstractButton = (AbstractButton)component;
            final Border border = this.borderTable.get(abstractButton);
            if (border == null || border instanceof UIResource) {
                this.borderTable.put(abstractButton, abstractButton.getBorder());
            }
            if (abstractButton.getBorder() instanceof UIResource) {
                abstractButton.setBorder(this.getNonRolloverBorder(abstractButton));
            }
            this.rolloverTable.put(abstractButton, abstractButton.isRolloverEnabled() ? Boolean.TRUE : Boolean.FALSE);
            abstractButton.setRolloverEnabled(false);
        }
    }
    
    protected Border getNonRolloverBorder(final AbstractButton abstractButton) {
        if (abstractButton instanceof JToggleButton) {
            return BasicToolBarUI.nonRolloverToggleBorder;
        }
        return BasicToolBarUI.nonRolloverBorder;
    }
    
    protected void setBorderToNormal(final Component component) {
        if (component instanceof AbstractButton) {
            final AbstractButton abstractButton = (AbstractButton)component;
            abstractButton.setBorder(this.borderTable.remove(abstractButton));
            final Boolean b = this.rolloverTable.remove(abstractButton);
            if (b != null) {
                abstractButton.setRolloverEnabled(b);
            }
        }
    }
    
    public void setFloatingLocation(final int floatingX, final int floatingY) {
        this.floatingX = floatingX;
        this.floatingY = floatingY;
    }
    
    public boolean isFloating() {
        return this.floating;
    }
    
    public void setFloating(final boolean floating, final Point point) {
        if (this.toolBar.isFloatable()) {
            boolean visible = false;
            final Window windowAncestor = SwingUtilities.getWindowAncestor(this.toolBar);
            if (windowAncestor != null) {
                visible = windowAncestor.isVisible();
            }
            if (this.dragWindow != null) {
                this.dragWindow.setVisible(false);
            }
            this.floating = floating;
            if (this.floatingToolBar == null) {
                this.floatingToolBar = this.createFloatingWindow(this.toolBar);
            }
            if (floating) {
                if (this.dockingSource == null) {
                    (this.dockingSource = this.toolBar.getParent()).remove(this.toolBar);
                }
                this.constraintBeforeFloating = this.calculateConstraint();
                if (this.propertyListener != null) {
                    UIManager.addPropertyChangeListener(this.propertyListener);
                }
                this.floatingToolBar.getContentPane().add(this.toolBar, "Center");
                if (this.floatingToolBar instanceof Window) {
                    ((Window)this.floatingToolBar).pack();
                    ((Window)this.floatingToolBar).setLocation(this.floatingX, this.floatingY);
                    if (visible) {
                        ((Window)this.floatingToolBar).show();
                    }
                    else {
                        windowAncestor.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowOpened(final WindowEvent windowEvent) {
                                ((Window)BasicToolBarUI.this.floatingToolBar).show();
                            }
                        });
                    }
                }
            }
            else {
                if (this.floatingToolBar == null) {
                    this.floatingToolBar = this.createFloatingWindow(this.toolBar);
                }
                if (this.floatingToolBar instanceof Window) {
                    ((Window)this.floatingToolBar).setVisible(false);
                }
                this.floatingToolBar.getContentPane().remove(this.toolBar);
                String dockingConstraint = this.getDockingConstraint(this.dockingSource, point);
                if (dockingConstraint == null) {
                    dockingConstraint = "North";
                }
                this.setOrientation(this.mapConstraintToOrientation(dockingConstraint));
                if (this.dockingSource == null) {
                    this.dockingSource = this.toolBar.getParent();
                }
                if (this.propertyListener != null) {
                    UIManager.removePropertyChangeListener(this.propertyListener);
                }
                this.dockingSource.add(dockingConstraint, this.toolBar);
            }
            this.dockingSource.invalidate();
            final Container parent = this.dockingSource.getParent();
            if (parent != null) {
                parent.validate();
            }
            this.dockingSource.repaint();
        }
    }
    
    private int mapConstraintToOrientation(final String s) {
        int orientation = this.toolBar.getOrientation();
        if (s != null) {
            if (s.equals("East") || s.equals("West")) {
                orientation = 1;
            }
            else if (s.equals("North") || s.equals("South")) {
                orientation = 0;
            }
        }
        return orientation;
    }
    
    public void setOrientation(final int n) {
        this.toolBar.setOrientation(n);
        if (this.dragWindow != null) {
            this.dragWindow.setOrientation(n);
        }
    }
    
    public Color getDockingColor() {
        return this.dockingColor;
    }
    
    public void setDockingColor(final Color dockingColor) {
        this.dockingColor = dockingColor;
    }
    
    public Color getFloatingColor() {
        return this.floatingColor;
    }
    
    public void setFloatingColor(final Color floatingColor) {
        this.floatingColor = floatingColor;
    }
    
    private boolean isBlocked(final Component component, final Object o) {
        if (component instanceof Container) {
            final Container container = (Container)component;
            final LayoutManager layout = container.getLayout();
            if (layout instanceof BorderLayout) {
                final Component layoutComponent = ((BorderLayout)layout).getLayoutComponent(container, o);
                return layoutComponent != null && layoutComponent != this.toolBar;
            }
        }
        return false;
    }
    
    public boolean canDock(final Component component, final Point point) {
        return point != null && this.getDockingConstraint(component, point) != null;
    }
    
    private String calculateConstraint() {
        String s = null;
        final LayoutManager layout = this.dockingSource.getLayout();
        if (layout instanceof BorderLayout) {
            s = (String)((BorderLayout)layout).getConstraints(this.toolBar);
        }
        return (s != null) ? s : this.constraintBeforeFloating;
    }
    
    private String getDockingConstraint(final Component component, final Point point) {
        if (point == null) {
            return this.constraintBeforeFloating;
        }
        if (component.contains(point)) {
            this.dockingSensitivity = ((this.toolBar.getOrientation() == 0) ? this.toolBar.getSize().height : this.toolBar.getSize().width);
            if (point.y < this.dockingSensitivity && !this.isBlocked(component, "North")) {
                return "North";
            }
            if (point.x >= component.getWidth() - this.dockingSensitivity && !this.isBlocked(component, "East")) {
                return "East";
            }
            if (point.x < this.dockingSensitivity && !this.isBlocked(component, "West")) {
                return "West";
            }
            if (point.y >= component.getHeight() - this.dockingSensitivity && !this.isBlocked(component, "South")) {
                return "South";
            }
        }
        return null;
    }
    
    protected void dragTo(final Point point, final Point point2) {
        if (this.toolBar.isFloatable()) {
            try {
                if (this.dragWindow == null) {
                    this.dragWindow = this.createDragWindow(this.toolBar);
                }
                Point offset = this.dragWindow.getOffset();
                if (offset == null) {
                    final Dimension preferredSize = this.toolBar.getPreferredSize();
                    offset = new Point(preferredSize.width / 2, preferredSize.height / 2);
                    this.dragWindow.setOffset(offset);
                }
                final Point point3 = new Point(point2.x + point.x, point2.y + point.y);
                final Point point4 = new Point(point3.x - offset.x, point3.y - offset.y);
                if (this.dockingSource == null) {
                    this.dockingSource = this.toolBar.getParent();
                }
                this.constraintBeforeFloating = this.calculateConstraint();
                final Point locationOnScreen = this.dockingSource.getLocationOnScreen();
                final Point point5 = new Point(point3.x - locationOnScreen.x, point3.y - locationOnScreen.y);
                if (this.canDock(this.dockingSource, point5)) {
                    this.dragWindow.setBackground(this.getDockingColor());
                    this.dragWindow.setOrientation(this.mapConstraintToOrientation(this.getDockingConstraint(this.dockingSource, point5)));
                    this.dragWindow.setBorderColor(this.dockingBorderColor);
                }
                else {
                    this.dragWindow.setBackground(this.getFloatingColor());
                    this.dragWindow.setBorderColor(this.floatingBorderColor);
                    this.dragWindow.setOrientation(this.toolBar.getOrientation());
                }
                this.dragWindow.setLocation(point4.x, point4.y);
                if (!this.dragWindow.isVisible()) {
                    final Dimension preferredSize2 = this.toolBar.getPreferredSize();
                    this.dragWindow.setSize(preferredSize2.width, preferredSize2.height);
                    this.dragWindow.show();
                }
            }
            catch (final IllegalComponentStateException ex) {}
        }
    }
    
    protected void floatAt(final Point point, final Point point2) {
        if (this.toolBar.isFloatable()) {
            try {
                Point offset = this.dragWindow.getOffset();
                if (offset == null) {
                    offset = point;
                    this.dragWindow.setOffset(offset);
                }
                final Point point3 = new Point(point2.x + point.x, point2.y + point.y);
                this.setFloatingLocation(point3.x - offset.x, point3.y - offset.y);
                if (this.dockingSource != null) {
                    final Point locationOnScreen = this.dockingSource.getLocationOnScreen();
                    final Point point4 = new Point(point3.x - locationOnScreen.x, point3.y - locationOnScreen.y);
                    if (this.canDock(this.dockingSource, point4)) {
                        this.setFloating(false, point4);
                    }
                    else {
                        this.setFloating(true, null);
                    }
                }
                else {
                    this.setFloating(true, null);
                }
                this.dragWindow.setOffset(null);
            }
            catch (final IllegalComponentStateException ex) {}
        }
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    protected ContainerListener createToolBarContListener() {
        return this.getHandler();
    }
    
    protected FocusListener createToolBarFocusListener() {
        return this.getHandler();
    }
    
    protected PropertyChangeListener createPropertyListener() {
        return this.getHandler();
    }
    
    protected MouseInputListener createDockingListener() {
        this.getHandler().tb = this.toolBar;
        return this.getHandler();
    }
    
    protected WindowListener createFrameListener() {
        return new FrameListener();
    }
    
    protected void paintDragWindow(final Graphics graphics) {
        graphics.setColor(this.dragWindow.getBackground());
        final int width = this.dragWindow.getWidth();
        final int height = this.dragWindow.getHeight();
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(this.dragWindow.getBorderColor());
        graphics.drawRect(0, 0, width - 1, height - 1);
    }
    
    static {
        BasicToolBarUI.IS_ROLLOVER = "JToolBar.isRollover";
        BasicToolBarUI.FOCUSED_COMP_INDEX = "JToolBar.focusedCompIndex";
    }
    
    private static class Actions extends UIAction
    {
        private static final String NAVIGATE_RIGHT = "navigateRight";
        private static final String NAVIGATE_LEFT = "navigateLeft";
        private static final String NAVIGATE_UP = "navigateUp";
        private static final String NAVIGATE_DOWN = "navigateDown";
        
        public Actions(final String s) {
            super(s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final String name = this.getName();
            final BasicToolBarUI basicToolBarUI = (BasicToolBarUI)BasicLookAndFeel.getUIOfType(((JToolBar)actionEvent.getSource()).getUI(), BasicToolBarUI.class);
            if ("navigateRight" == name) {
                basicToolBarUI.navigateFocusedComp(3);
            }
            else if ("navigateLeft" == name) {
                basicToolBarUI.navigateFocusedComp(7);
            }
            else if ("navigateUp" == name) {
                basicToolBarUI.navigateFocusedComp(1);
            }
            else if ("navigateDown" == name) {
                basicToolBarUI.navigateFocusedComp(5);
            }
        }
    }
    
    private class Handler implements ContainerListener, FocusListener, MouseInputListener, PropertyChangeListener
    {
        JToolBar tb;
        boolean isDragging;
        Point origin;
        
        private Handler() {
            this.isDragging = false;
            this.origin = null;
        }
        
        @Override
        public void componentAdded(final ContainerEvent containerEvent) {
            final Component child = containerEvent.getChild();
            if (BasicToolBarUI.this.toolBarFocusListener != null) {
                child.addFocusListener(BasicToolBarUI.this.toolBarFocusListener);
            }
            if (BasicToolBarUI.this.isRolloverBorders()) {
                BasicToolBarUI.this.setBorderToRollover(child);
            }
            else {
                BasicToolBarUI.this.setBorderToNonRollover(child);
            }
        }
        
        @Override
        public void componentRemoved(final ContainerEvent containerEvent) {
            final Component child = containerEvent.getChild();
            if (BasicToolBarUI.this.toolBarFocusListener != null) {
                child.removeFocusListener(BasicToolBarUI.this.toolBarFocusListener);
            }
            BasicToolBarUI.this.setBorderToNormal(child);
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            BasicToolBarUI.this.focusedCompIndex = BasicToolBarUI.this.toolBar.getComponentIndex(focusEvent.getComponent());
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (!this.tb.isEnabled()) {
                return;
            }
            this.isDragging = false;
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (!this.tb.isEnabled()) {
                return;
            }
            if (this.isDragging) {
                final Point point = mouseEvent.getPoint();
                if (this.origin == null) {
                    this.origin = mouseEvent.getComponent().getLocationOnScreen();
                }
                BasicToolBarUI.this.floatAt(point, this.origin);
            }
            this.origin = null;
            this.isDragging = false;
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            if (!this.tb.isEnabled()) {
                return;
            }
            this.isDragging = true;
            final Point point = mouseEvent.getPoint();
            if (this.origin == null) {
                this.origin = mouseEvent.getComponent().getLocationOnScreen();
            }
            BasicToolBarUI.this.dragTo(point, this.origin);
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName == "lookAndFeel") {
                BasicToolBarUI.this.toolBar.updateUI();
            }
            else if (propertyName == "orientation") {
                final Component[] components = BasicToolBarUI.this.toolBar.getComponents();
                final int intValue = (int)propertyChangeEvent.getNewValue();
                for (int i = 0; i < components.length; ++i) {
                    if (components[i] instanceof JToolBar.Separator) {
                        final JToolBar.Separator separator = (JToolBar.Separator)components[i];
                        if (intValue == 0) {
                            separator.setOrientation(1);
                        }
                        else {
                            separator.setOrientation(0);
                        }
                        final Dimension separatorSize = separator.getSeparatorSize();
                        if (separatorSize != null && separatorSize.width != separatorSize.height) {
                            separator.setSeparatorSize(new Dimension(separatorSize.height, separatorSize.width));
                        }
                    }
                }
            }
            else if (propertyName == BasicToolBarUI.IS_ROLLOVER) {
                BasicToolBarUI.this.installNormalBorders(BasicToolBarUI.this.toolBar);
                BasicToolBarUI.this.setRolloverBorders((boolean)propertyChangeEvent.getNewValue());
            }
        }
    }
    
    protected class FrameListener extends WindowAdapter
    {
        @Override
        public void windowClosing(final WindowEvent windowEvent) {
            if (BasicToolBarUI.this.toolBar.isFloatable()) {
                if (BasicToolBarUI.this.dragWindow != null) {
                    BasicToolBarUI.this.dragWindow.setVisible(false);
                }
                BasicToolBarUI.this.floating = false;
                if (BasicToolBarUI.this.floatingToolBar == null) {
                    BasicToolBarUI.this.floatingToolBar = BasicToolBarUI.this.createFloatingWindow(BasicToolBarUI.this.toolBar);
                }
                if (BasicToolBarUI.this.floatingToolBar instanceof Window) {
                    ((Window)BasicToolBarUI.this.floatingToolBar).setVisible(false);
                }
                BasicToolBarUI.this.floatingToolBar.getContentPane().remove(BasicToolBarUI.this.toolBar);
                String constraintBeforeFloating = BasicToolBarUI.this.constraintBeforeFloating;
                if (BasicToolBarUI.this.toolBar.getOrientation() == 0) {
                    if (constraintBeforeFloating == "West" || constraintBeforeFloating == "East") {
                        constraintBeforeFloating = "North";
                    }
                }
                else if (constraintBeforeFloating == "North" || constraintBeforeFloating == "South") {
                    constraintBeforeFloating = "West";
                }
                if (BasicToolBarUI.this.dockingSource == null) {
                    BasicToolBarUI.this.dockingSource = BasicToolBarUI.this.toolBar.getParent();
                }
                if (BasicToolBarUI.this.propertyListener != null) {
                    UIManager.removePropertyChangeListener(BasicToolBarUI.this.propertyListener);
                }
                BasicToolBarUI.this.dockingSource.add(BasicToolBarUI.this.toolBar, constraintBeforeFloating);
                BasicToolBarUI.this.dockingSource.invalidate();
                final Container parent = BasicToolBarUI.this.dockingSource.getParent();
                if (parent != null) {
                    parent.validate();
                }
                BasicToolBarUI.this.dockingSource.repaint();
            }
        }
    }
    
    protected class ToolBarContListener implements ContainerListener
    {
        @Override
        public void componentAdded(final ContainerEvent containerEvent) {
            BasicToolBarUI.this.getHandler().componentAdded(containerEvent);
        }
        
        @Override
        public void componentRemoved(final ContainerEvent containerEvent) {
            BasicToolBarUI.this.getHandler().componentRemoved(containerEvent);
        }
    }
    
    protected class ToolBarFocusListener implements FocusListener
    {
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            BasicToolBarUI.this.getHandler().focusGained(focusEvent);
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            BasicToolBarUI.this.getHandler().focusLost(focusEvent);
        }
    }
    
    protected class PropertyListener implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            BasicToolBarUI.this.getHandler().propertyChange(propertyChangeEvent);
        }
    }
    
    public class DockingListener implements MouseInputListener
    {
        protected JToolBar toolBar;
        protected boolean isDragging;
        protected Point origin;
        
        public DockingListener(final JToolBar toolBar) {
            this.isDragging = false;
            this.origin = null;
            this.toolBar = toolBar;
            BasicToolBarUI.this.getHandler().tb = toolBar;
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            BasicToolBarUI.this.getHandler().mouseClicked(mouseEvent);
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            BasicToolBarUI.this.getHandler().tb = this.toolBar;
            BasicToolBarUI.this.getHandler().mousePressed(mouseEvent);
            this.isDragging = BasicToolBarUI.this.getHandler().isDragging;
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            BasicToolBarUI.this.getHandler().tb = this.toolBar;
            BasicToolBarUI.this.getHandler().isDragging = this.isDragging;
            BasicToolBarUI.this.getHandler().origin = this.origin;
            BasicToolBarUI.this.getHandler().mouseReleased(mouseEvent);
            this.isDragging = BasicToolBarUI.this.getHandler().isDragging;
            this.origin = BasicToolBarUI.this.getHandler().origin;
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            BasicToolBarUI.this.getHandler().mouseEntered(mouseEvent);
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            BasicToolBarUI.this.getHandler().mouseExited(mouseEvent);
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            BasicToolBarUI.this.getHandler().tb = this.toolBar;
            BasicToolBarUI.this.getHandler().origin = this.origin;
            BasicToolBarUI.this.getHandler().mouseDragged(mouseEvent);
            this.isDragging = BasicToolBarUI.this.getHandler().isDragging;
            this.origin = BasicToolBarUI.this.getHandler().origin;
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            BasicToolBarUI.this.getHandler().mouseMoved(mouseEvent);
        }
    }
    
    protected class DragWindow extends Window
    {
        Color borderColor;
        int orientation;
        Point offset;
        
        DragWindow(final Window window) {
            super(window);
            this.borderColor = Color.gray;
            this.orientation = BasicToolBarUI.this.toolBar.getOrientation();
        }
        
        public int getOrientation() {
            return this.orientation;
        }
        
        public void setOrientation(final int orientation) {
            if (this.isShowing()) {
                if (orientation == this.orientation) {
                    return;
                }
                this.orientation = orientation;
                final Dimension size = this.getSize();
                this.setSize(new Dimension(size.height, size.width));
                if (this.offset != null) {
                    if (BasicGraphicsUtils.isLeftToRight(BasicToolBarUI.this.toolBar)) {
                        this.setOffset(new Point(this.offset.y, this.offset.x));
                    }
                    else if (orientation == 0) {
                        this.setOffset(new Point(size.height - this.offset.y, this.offset.x));
                    }
                    else {
                        this.setOffset(new Point(this.offset.y, size.width - this.offset.x));
                    }
                }
                this.repaint();
            }
        }
        
        public Point getOffset() {
            return this.offset;
        }
        
        public void setOffset(final Point offset) {
            this.offset = offset;
        }
        
        public void setBorderColor(final Color borderColor) {
            if (this.borderColor == borderColor) {
                return;
            }
            this.borderColor = borderColor;
            this.repaint();
        }
        
        public Color getBorderColor() {
            return this.borderColor;
        }
        
        @Override
        public void paint(final Graphics graphics) {
            BasicToolBarUI.this.paintDragWindow(graphics);
            super.paint(graphics);
        }
        
        @Override
        public Insets getInsets() {
            return new Insets(1, 1, 1, 1);
        }
    }
}
