package javax.swing.plaf.basic;

import javax.swing.AbstractAction;
import java.beans.PropertyVetoException;
import java.awt.FocusTraversalPolicy;
import java.awt.Container;
import java.awt.Point;
import java.awt.Insets;
import javax.swing.SortingFocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.Component;
import javax.swing.JInternalFrame;
import sun.swing.UIAction;
import javax.swing.DefaultDesktopManager;
import java.beans.PropertyChangeEvent;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ActionMap;
import javax.swing.Action;
import sun.swing.DefaultLookup;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.DesktopManager;
import javax.swing.JDesktopPane;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.DesktopPaneUI;

public class BasicDesktopPaneUI extends DesktopPaneUI
{
    private static final Actions SHARED_ACTION;
    private Handler handler;
    private PropertyChangeListener pcl;
    protected JDesktopPane desktop;
    protected DesktopManager desktopManager;
    @Deprecated
    protected KeyStroke minimizeKey;
    @Deprecated
    protected KeyStroke maximizeKey;
    @Deprecated
    protected KeyStroke closeKey;
    @Deprecated
    protected KeyStroke navigateKey;
    @Deprecated
    protected KeyStroke navigateKey2;
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicDesktopPaneUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.desktop = (JDesktopPane)component;
        this.installDefaults();
        this.installDesktopManager();
        this.installListeners();
        this.installKeyboardActions();
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallKeyboardActions();
        this.uninstallListeners();
        this.uninstallDesktopManager();
        this.uninstallDefaults();
        this.desktop = null;
        this.handler = null;
    }
    
    protected void installDefaults() {
        if (this.desktop.getBackground() == null || this.desktop.getBackground() instanceof UIResource) {
            this.desktop.setBackground(UIManager.getColor("Desktop.background"));
        }
        LookAndFeel.installProperty(this.desktop, "opaque", Boolean.TRUE);
    }
    
    protected void uninstallDefaults() {
    }
    
    protected void installListeners() {
        this.pcl = this.createPropertyChangeListener();
        this.desktop.addPropertyChangeListener(this.pcl);
    }
    
    protected void uninstallListeners() {
        this.desktop.removePropertyChangeListener(this.pcl);
        this.pcl = null;
    }
    
    protected void installDesktopManager() {
        this.desktopManager = this.desktop.getDesktopManager();
        if (this.desktopManager == null) {
            this.desktopManager = new BasicDesktopManager();
            this.desktop.setDesktopManager(this.desktopManager);
        }
    }
    
    protected void uninstallDesktopManager() {
        if (this.desktop.getDesktopManager() instanceof UIResource) {
            this.desktop.setDesktopManager(null);
        }
        this.desktopManager = null;
    }
    
    protected void installKeyboardActions() {
        final InputMap inputMap = this.getInputMap(2);
        if (inputMap != null) {
            SwingUtilities.replaceUIInputMap(this.desktop, 2, inputMap);
        }
        final InputMap inputMap2 = this.getInputMap(1);
        if (inputMap2 != null) {
            SwingUtilities.replaceUIInputMap(this.desktop, 1, inputMap2);
        }
        LazyActionMap.installLazyActionMap(this.desktop, BasicDesktopPaneUI.class, "DesktopPane.actionMap");
        this.registerKeyboardActions();
    }
    
    protected void registerKeyboardActions() {
    }
    
    protected void unregisterKeyboardActions() {
    }
    
    InputMap getInputMap(final int n) {
        if (n == 2) {
            return this.createInputMap(n);
        }
        if (n == 1) {
            return (InputMap)DefaultLookup.get(this.desktop, this, "Desktop.ancestorInputMap");
        }
        return null;
    }
    
    InputMap createInputMap(final int n) {
        if (n == 2) {
            final Object[] array = (Object[])DefaultLookup.get(this.desktop, this, "Desktop.windowBindings");
            if (array != null) {
                return LookAndFeel.makeComponentInputMap(this.desktop, array);
            }
        }
        return null;
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions(Actions.RESTORE));
        lazyActionMap.put(new Actions(Actions.CLOSE));
        lazyActionMap.put(new Actions(Actions.MOVE));
        lazyActionMap.put(new Actions(Actions.RESIZE));
        lazyActionMap.put(new Actions(Actions.LEFT));
        lazyActionMap.put(new Actions(Actions.SHRINK_LEFT));
        lazyActionMap.put(new Actions(Actions.RIGHT));
        lazyActionMap.put(new Actions(Actions.SHRINK_RIGHT));
        lazyActionMap.put(new Actions(Actions.UP));
        lazyActionMap.put(new Actions(Actions.SHRINK_UP));
        lazyActionMap.put(new Actions(Actions.DOWN));
        lazyActionMap.put(new Actions(Actions.SHRINK_DOWN));
        lazyActionMap.put(new Actions(Actions.ESCAPE));
        lazyActionMap.put(new Actions(Actions.MINIMIZE));
        lazyActionMap.put(new Actions(Actions.MAXIMIZE));
        lazyActionMap.put(new Actions(Actions.NEXT_FRAME));
        lazyActionMap.put(new Actions(Actions.PREVIOUS_FRAME));
        lazyActionMap.put(new Actions(Actions.NAVIGATE_NEXT));
        lazyActionMap.put(new Actions(Actions.NAVIGATE_PREVIOUS));
    }
    
    protected void uninstallKeyboardActions() {
        this.unregisterKeyboardActions();
        SwingUtilities.replaceUIInputMap(this.desktop, 2, null);
        SwingUtilities.replaceUIInputMap(this.desktop, 1, null);
        SwingUtilities.replaceUIActionMap(this.desktop, null);
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        return null;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        return new Dimension(0, 0);
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
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
    
    static {
        SHARED_ACTION = new Actions();
    }
    
    private class Handler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if ("desktopManager" == propertyChangeEvent.getPropertyName()) {
                BasicDesktopPaneUI.this.installDesktopManager();
            }
        }
    }
    
    private class BasicDesktopManager extends DefaultDesktopManager implements UIResource
    {
    }
    
    private static class Actions extends UIAction
    {
        private static String CLOSE;
        private static String ESCAPE;
        private static String MAXIMIZE;
        private static String MINIMIZE;
        private static String MOVE;
        private static String RESIZE;
        private static String RESTORE;
        private static String LEFT;
        private static String RIGHT;
        private static String UP;
        private static String DOWN;
        private static String SHRINK_LEFT;
        private static String SHRINK_RIGHT;
        private static String SHRINK_UP;
        private static String SHRINK_DOWN;
        private static String NEXT_FRAME;
        private static String PREVIOUS_FRAME;
        private static String NAVIGATE_NEXT;
        private static String NAVIGATE_PREVIOUS;
        private final int MOVE_RESIZE_INCREMENT = 10;
        private static boolean moving;
        private static boolean resizing;
        private static JInternalFrame sourceFrame;
        private static Component focusOwner;
        
        Actions() {
            super(null);
        }
        
        Actions(final String s) {
            super(s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JDesktopPane desktopPane = (JDesktopPane)actionEvent.getSource();
            final String name = this.getName();
            if (Actions.CLOSE == name || Actions.MAXIMIZE == name || Actions.MINIMIZE == name || Actions.RESTORE == name) {
                this.setState(desktopPane, name);
            }
            else if (Actions.ESCAPE == name) {
                if (Actions.sourceFrame == desktopPane.getSelectedFrame() && Actions.focusOwner != null) {
                    Actions.focusOwner.requestFocus();
                }
                Actions.moving = false;
                Actions.resizing = false;
                Actions.sourceFrame = null;
                Actions.focusOwner = null;
            }
            else if (Actions.MOVE == name || Actions.RESIZE == name) {
                Actions.sourceFrame = desktopPane.getSelectedFrame();
                if (Actions.sourceFrame == null) {
                    return;
                }
                Actions.moving = (name == Actions.MOVE);
                Actions.resizing = (name == Actions.RESIZE);
                Actions.focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                if (!SwingUtilities.isDescendingFrom(Actions.focusOwner, Actions.sourceFrame)) {
                    Actions.focusOwner = null;
                }
                Actions.sourceFrame.requestFocus();
            }
            else if (Actions.LEFT == name || Actions.RIGHT == name || Actions.UP == name || Actions.DOWN == name || Actions.SHRINK_RIGHT == name || Actions.SHRINK_LEFT == name || Actions.SHRINK_UP == name || Actions.SHRINK_DOWN == name) {
                final JInternalFrame selectedFrame = desktopPane.getSelectedFrame();
                if (Actions.sourceFrame == null || selectedFrame != Actions.sourceFrame || KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() != Actions.sourceFrame) {
                    return;
                }
                final Insets insets = UIManager.getInsets("Desktop.minOnScreenInsets");
                final Dimension size = selectedFrame.getSize();
                final Dimension minimumSize = selectedFrame.getMinimumSize();
                final int width = desktopPane.getWidth();
                final int height = desktopPane.getHeight();
                final Point location = selectedFrame.getLocation();
                if (Actions.LEFT == name) {
                    if (Actions.moving) {
                        selectedFrame.setLocation((location.x + size.width - 10 < insets.right) ? (-size.width + insets.right) : (location.x - 10), location.y);
                    }
                    else if (Actions.resizing) {
                        selectedFrame.setLocation(location.x - 10, location.y);
                        selectedFrame.setSize(size.width + 10, size.height);
                    }
                }
                else if (Actions.RIGHT == name) {
                    if (Actions.moving) {
                        selectedFrame.setLocation((location.x + 10 > width - insets.left) ? (width - insets.left) : (location.x + 10), location.y);
                    }
                    else if (Actions.resizing) {
                        selectedFrame.setSize(size.width + 10, size.height);
                    }
                }
                else if (Actions.UP == name) {
                    if (Actions.moving) {
                        selectedFrame.setLocation(location.x, (location.y + size.height - 10 < insets.bottom) ? (-size.height + insets.bottom) : (location.y - 10));
                    }
                    else if (Actions.resizing) {
                        selectedFrame.setLocation(location.x, location.y - 10);
                        selectedFrame.setSize(size.width, size.height + 10);
                    }
                }
                else if (Actions.DOWN == name) {
                    if (Actions.moving) {
                        selectedFrame.setLocation(location.x, (location.y + 10 > height - insets.top) ? (height - insets.top) : (location.y + 10));
                    }
                    else if (Actions.resizing) {
                        selectedFrame.setSize(size.width, size.height + 10);
                    }
                }
                else if (Actions.SHRINK_LEFT == name && Actions.resizing) {
                    int n;
                    if (minimumSize.width < size.width - 10) {
                        n = 10;
                    }
                    else {
                        n = size.width - minimumSize.width;
                    }
                    if (location.x + size.width - n < insets.left) {
                        n = location.x + size.width - insets.left;
                    }
                    selectedFrame.setSize(size.width - n, size.height);
                }
                else if (Actions.SHRINK_RIGHT == name && Actions.resizing) {
                    int n2;
                    if (minimumSize.width < size.width - 10) {
                        n2 = 10;
                    }
                    else {
                        n2 = size.width - minimumSize.width;
                    }
                    if (location.x + n2 > width - insets.right) {
                        n2 = width - insets.right - location.x;
                    }
                    selectedFrame.setLocation(location.x + n2, location.y);
                    selectedFrame.setSize(size.width - n2, size.height);
                }
                else if (Actions.SHRINK_UP == name && Actions.resizing) {
                    int n3;
                    if (minimumSize.height < size.height - 10) {
                        n3 = 10;
                    }
                    else {
                        n3 = size.height - minimumSize.height;
                    }
                    if (location.y + size.height - n3 < insets.bottom) {
                        n3 = location.y + size.height - insets.bottom;
                    }
                    selectedFrame.setSize(size.width, size.height - n3);
                }
                else if (Actions.SHRINK_DOWN == name && Actions.resizing) {
                    int n4;
                    if (minimumSize.height < size.height - 10) {
                        n4 = 10;
                    }
                    else {
                        n4 = size.height - minimumSize.height;
                    }
                    if (location.y + n4 > height - insets.top) {
                        n4 = height - insets.top - location.y;
                    }
                    selectedFrame.setLocation(location.x, location.y + n4);
                    selectedFrame.setSize(size.width, size.height - n4);
                }
            }
            else if (Actions.NEXT_FRAME == name || Actions.PREVIOUS_FRAME == name) {
                desktopPane.selectFrame(name == Actions.NEXT_FRAME);
            }
            else if (Actions.NAVIGATE_NEXT == name || Actions.NAVIGATE_PREVIOUS == name) {
                boolean b = true;
                if (Actions.NAVIGATE_PREVIOUS == name) {
                    b = false;
                }
                final Container focusCycleRootAncestor = desktopPane.getFocusCycleRootAncestor();
                if (focusCycleRootAncestor != null) {
                    final FocusTraversalPolicy focusTraversalPolicy = focusCycleRootAncestor.getFocusTraversalPolicy();
                    if (focusTraversalPolicy != null && focusTraversalPolicy instanceof SortingFocusTraversalPolicy) {
                        final SortingFocusTraversalPolicy sortingFocusTraversalPolicy = (SortingFocusTraversalPolicy)focusTraversalPolicy;
                        final boolean implicitDownCycleTraversal = sortingFocusTraversalPolicy.getImplicitDownCycleTraversal();
                        try {
                            sortingFocusTraversalPolicy.setImplicitDownCycleTraversal(false);
                            if (b) {
                                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(desktopPane);
                            }
                            else {
                                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent(desktopPane);
                            }
                        }
                        finally {
                            sortingFocusTraversalPolicy.setImplicitDownCycleTraversal(implicitDownCycleTraversal);
                        }
                    }
                }
            }
        }
        
        private void setState(final JDesktopPane desktopPane, final String s) {
            if (s == Actions.CLOSE) {
                final JInternalFrame selectedFrame = desktopPane.getSelectedFrame();
                if (selectedFrame == null) {
                    return;
                }
                selectedFrame.doDefaultCloseAction();
            }
            else if (s == Actions.MAXIMIZE) {
                final JInternalFrame selectedFrame2 = desktopPane.getSelectedFrame();
                if (selectedFrame2 == null) {
                    return;
                }
                if (!selectedFrame2.isMaximum()) {
                    if (selectedFrame2.isIcon()) {
                        try {
                            selectedFrame2.setIcon(false);
                            selectedFrame2.setMaximum(true);
                        }
                        catch (final PropertyVetoException ex) {}
                    }
                    else {
                        try {
                            selectedFrame2.setMaximum(true);
                        }
                        catch (final PropertyVetoException ex2) {}
                    }
                }
            }
            else if (s == Actions.MINIMIZE) {
                final JInternalFrame selectedFrame3 = desktopPane.getSelectedFrame();
                if (selectedFrame3 == null) {
                    return;
                }
                if (!selectedFrame3.isIcon()) {
                    try {
                        selectedFrame3.setIcon(true);
                    }
                    catch (final PropertyVetoException ex3) {}
                }
            }
            else if (s == Actions.RESTORE) {
                final JInternalFrame selectedFrame4 = desktopPane.getSelectedFrame();
                if (selectedFrame4 == null) {
                    return;
                }
                try {
                    if (selectedFrame4.isIcon()) {
                        selectedFrame4.setIcon(false);
                    }
                    else if (selectedFrame4.isMaximum()) {
                        selectedFrame4.setMaximum(false);
                    }
                    selectedFrame4.setSelected(true);
                }
                catch (final PropertyVetoException ex4) {}
            }
        }
        
        @Override
        public boolean isEnabled(final Object o) {
            if (!(o instanceof JDesktopPane)) {
                return false;
            }
            final JDesktopPane desktopPane = (JDesktopPane)o;
            final String name = this.getName();
            if (name == Actions.NEXT_FRAME || name == Actions.PREVIOUS_FRAME) {
                return true;
            }
            final JInternalFrame selectedFrame = desktopPane.getSelectedFrame();
            if (selectedFrame == null) {
                return false;
            }
            if (name == Actions.CLOSE) {
                return selectedFrame.isClosable();
            }
            if (name == Actions.MINIMIZE) {
                return selectedFrame.isIconifiable();
            }
            return name != Actions.MAXIMIZE || selectedFrame.isMaximizable();
        }
        
        static {
            Actions.CLOSE = "close";
            Actions.ESCAPE = "escape";
            Actions.MAXIMIZE = "maximize";
            Actions.MINIMIZE = "minimize";
            Actions.MOVE = "move";
            Actions.RESIZE = "resize";
            Actions.RESTORE = "restore";
            Actions.LEFT = "left";
            Actions.RIGHT = "right";
            Actions.UP = "up";
            Actions.DOWN = "down";
            Actions.SHRINK_LEFT = "shrinkLeft";
            Actions.SHRINK_RIGHT = "shrinkRight";
            Actions.SHRINK_UP = "shrinkUp";
            Actions.SHRINK_DOWN = "shrinkDown";
            Actions.NEXT_FRAME = "selectNextFrame";
            Actions.PREVIOUS_FRAME = "selectPreviousFrame";
            Actions.NAVIGATE_NEXT = "navigateNext";
            Actions.NAVIGATE_PREVIOUS = "navigatePrevious";
            Actions.moving = false;
            Actions.resizing = false;
            Actions.sourceFrame = null;
            Actions.focusOwner = null;
        }
    }
    
    protected class OpenAction extends AbstractAction
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            BasicDesktopPaneUI.SHARED_ACTION.setState((JDesktopPane)actionEvent.getSource(), Actions.RESTORE);
        }
        
        @Override
        public boolean isEnabled() {
            return true;
        }
    }
    
    protected class CloseAction extends AbstractAction
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            BasicDesktopPaneUI.SHARED_ACTION.setState((JDesktopPane)actionEvent.getSource(), Actions.CLOSE);
        }
        
        @Override
        public boolean isEnabled() {
            final JInternalFrame selectedFrame = BasicDesktopPaneUI.this.desktop.getSelectedFrame();
            return selectedFrame != null && selectedFrame.isClosable();
        }
    }
    
    protected class MinimizeAction extends AbstractAction
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            BasicDesktopPaneUI.SHARED_ACTION.setState((JDesktopPane)actionEvent.getSource(), Actions.MINIMIZE);
        }
        
        @Override
        public boolean isEnabled() {
            final JInternalFrame selectedFrame = BasicDesktopPaneUI.this.desktop.getSelectedFrame();
            return selectedFrame != null && selectedFrame.isIconifiable();
        }
    }
    
    protected class MaximizeAction extends AbstractAction
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            BasicDesktopPaneUI.SHARED_ACTION.setState((JDesktopPane)actionEvent.getSource(), Actions.MAXIMIZE);
        }
        
        @Override
        public boolean isEnabled() {
            final JInternalFrame selectedFrame = BasicDesktopPaneUI.this.desktop.getSelectedFrame();
            return selectedFrame != null && selectedFrame.isMaximizable();
        }
    }
    
    protected class NavigateAction extends AbstractAction
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            ((JDesktopPane)actionEvent.getSource()).selectFrame(true);
        }
        
        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
