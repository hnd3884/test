package javax.swing.plaf.basic;

import java.awt.event.WindowEvent;
import javax.swing.event.InternalFrameEvent;
import java.awt.event.ComponentEvent;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import javax.swing.RootPaneContainer;
import java.beans.PropertyVetoException;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;
import java.beans.PropertyChangeEvent;
import javax.swing.DefaultDesktopManager;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Cursor;
import javax.swing.SwingUtilities;
import sun.swing.DefaultLookup;
import javax.swing.InputMap;
import java.awt.event.WindowFocusListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import javax.swing.ActionMap;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import java.awt.Container;
import javax.swing.Icon;
import java.awt.Color;
import javax.swing.plaf.UIResource;
import java.awt.IllegalComponentStateException;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.KeyStroke;
import java.awt.Rectangle;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.MouseInputListener;
import java.awt.event.ComponentListener;
import java.awt.LayoutManager;
import java.beans.PropertyChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.JInternalFrame;
import javax.swing.plaf.InternalFrameUI;

public class BasicInternalFrameUI extends InternalFrameUI
{
    protected JInternalFrame frame;
    private Handler handler;
    protected MouseInputAdapter borderListener;
    protected PropertyChangeListener propertyChangeListener;
    protected LayoutManager internalFrameLayout;
    protected ComponentListener componentListener;
    protected MouseInputListener glassPaneDispatcher;
    private InternalFrameListener internalFrameListener;
    protected JComponent northPane;
    protected JComponent southPane;
    protected JComponent westPane;
    protected JComponent eastPane;
    protected BasicInternalFrameTitlePane titlePane;
    private static DesktopManager sharedDesktopManager;
    private boolean componentListenerAdded;
    private Rectangle parentBounds;
    private boolean dragging;
    private boolean resizing;
    @Deprecated
    protected KeyStroke openMenuKey;
    private boolean keyBindingRegistered;
    private boolean keyBindingActive;
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicInternalFrameUI((JInternalFrame)component);
    }
    
    public BasicInternalFrameUI(final JInternalFrame internalFrame) {
        this.componentListenerAdded = false;
        this.dragging = false;
        this.resizing = false;
        this.keyBindingRegistered = false;
        this.keyBindingActive = false;
        final LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        if (lookAndFeel instanceof BasicLookAndFeel) {
            ((BasicLookAndFeel)lookAndFeel).installAWTEventListener();
        }
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.frame = (JInternalFrame)component;
        this.installDefaults();
        this.installListeners();
        this.installComponents();
        this.installKeyboardActions();
        LookAndFeel.installProperty(this.frame, "opaque", Boolean.TRUE);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        if (component != this.frame) {
            throw new IllegalComponentStateException(this + " was asked to deinstall() " + component + " when it only knows about " + this.frame + ".");
        }
        this.uninstallKeyboardActions();
        this.uninstallComponents();
        this.uninstallListeners();
        this.uninstallDefaults();
        this.updateFrameCursor();
        this.handler = null;
        this.frame = null;
    }
    
    protected void installDefaults() {
        final Icon frameIcon = this.frame.getFrameIcon();
        if (frameIcon == null || frameIcon instanceof UIResource) {
            this.frame.setFrameIcon(UIManager.getIcon("InternalFrame.icon"));
        }
        final Container contentPane = this.frame.getContentPane();
        if (contentPane != null && contentPane.getBackground() instanceof UIResource) {
            contentPane.setBackground(null);
        }
        this.frame.setLayout(this.internalFrameLayout = this.createLayoutManager());
        this.frame.setBackground(UIManager.getLookAndFeelDefaults().getColor("control"));
        LookAndFeel.installBorder(this.frame, "InternalFrame.border");
    }
    
    protected void installKeyboardActions() {
        this.createInternalFrameListener();
        if (this.internalFrameListener != null) {
            this.frame.addInternalFrameListener(this.internalFrameListener);
        }
        LazyActionMap.installLazyActionMap(this.frame, BasicInternalFrameUI.class, "InternalFrame.actionMap");
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new UIAction("showSystemMenu") {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                final JInternalFrame internalFrame = (JInternalFrame)actionEvent.getSource();
                if (internalFrame.getUI() instanceof BasicInternalFrameUI) {
                    final JComponent northPane = ((BasicInternalFrameUI)internalFrame.getUI()).getNorthPane();
                    if (northPane instanceof BasicInternalFrameTitlePane) {
                        ((BasicInternalFrameTitlePane)northPane).showSystemMenu();
                    }
                }
            }
            
            @Override
            public boolean isEnabled(final Object o) {
                if (o instanceof JInternalFrame) {
                    final JInternalFrame internalFrame = (JInternalFrame)o;
                    if (internalFrame.getUI() instanceof BasicInternalFrameUI) {
                        return ((BasicInternalFrameUI)internalFrame.getUI()).isKeyBindingActive();
                    }
                }
                return false;
            }
        });
        BasicLookAndFeel.installAudioActionMap(lazyActionMap);
    }
    
    protected void installComponents() {
        this.setNorthPane(this.createNorthPane(this.frame));
        this.setSouthPane(this.createSouthPane(this.frame));
        this.setEastPane(this.createEastPane(this.frame));
        this.setWestPane(this.createWestPane(this.frame));
    }
    
    protected void installListeners() {
        this.borderListener = this.createBorderListener(this.frame);
        this.propertyChangeListener = this.createPropertyChangeListener();
        this.frame.addPropertyChangeListener(this.propertyChangeListener);
        this.installMouseHandlers(this.frame);
        this.glassPaneDispatcher = this.createGlassPaneDispatcher();
        if (this.glassPaneDispatcher != null) {
            this.frame.getGlassPane().addMouseListener(this.glassPaneDispatcher);
            this.frame.getGlassPane().addMouseMotionListener(this.glassPaneDispatcher);
        }
        this.componentListener = this.createComponentListener();
        if (this.frame.getParent() != null) {
            this.parentBounds = this.frame.getParent().getBounds();
        }
        if (this.frame.getParent() != null && !this.componentListenerAdded) {
            this.frame.getParent().addComponentListener(this.componentListener);
            this.componentListenerAdded = true;
        }
    }
    
    private WindowFocusListener getWindowFocusListener() {
        return this.getHandler();
    }
    
    private void cancelResize() {
        if (this.resizing && this.borderListener instanceof BorderListener) {
            ((BorderListener)this.borderListener).finishMouseReleased();
        }
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    InputMap getInputMap(final int n) {
        if (n == 2) {
            return this.createInputMap(n);
        }
        return null;
    }
    
    InputMap createInputMap(final int n) {
        if (n == 2) {
            final Object[] array = (Object[])DefaultLookup.get(this.frame, this, "InternalFrame.windowBindings");
            if (array != null) {
                return LookAndFeel.makeComponentInputMap(this.frame, array);
            }
        }
        return null;
    }
    
    protected void uninstallDefaults() {
        if (this.frame.getFrameIcon() instanceof UIResource) {
            this.frame.setFrameIcon(null);
        }
        this.internalFrameLayout = null;
        this.frame.setLayout(null);
        LookAndFeel.uninstallBorder(this.frame);
    }
    
    protected void uninstallComponents() {
        this.setNorthPane(null);
        this.setSouthPane(null);
        this.setEastPane(null);
        this.setWestPane(null);
        if (this.titlePane != null) {
            this.titlePane.uninstallDefaults();
        }
        this.titlePane = null;
    }
    
    protected void uninstallListeners() {
        if (this.frame.getParent() != null && this.componentListenerAdded) {
            this.frame.getParent().removeComponentListener(this.componentListener);
            this.componentListenerAdded = false;
        }
        this.componentListener = null;
        if (this.glassPaneDispatcher != null) {
            this.frame.getGlassPane().removeMouseListener(this.glassPaneDispatcher);
            this.frame.getGlassPane().removeMouseMotionListener(this.glassPaneDispatcher);
            this.glassPaneDispatcher = null;
        }
        this.deinstallMouseHandlers(this.frame);
        this.frame.removePropertyChangeListener(this.propertyChangeListener);
        this.propertyChangeListener = null;
        this.borderListener = null;
    }
    
    protected void uninstallKeyboardActions() {
        if (this.internalFrameListener != null) {
            this.frame.removeInternalFrameListener(this.internalFrameListener);
        }
        this.internalFrameListener = null;
        SwingUtilities.replaceUIInputMap(this.frame, 2, null);
        SwingUtilities.replaceUIActionMap(this.frame, null);
    }
    
    void updateFrameCursor() {
        if (this.resizing) {
            return;
        }
        Cursor cursor = this.frame.getLastCursor();
        if (cursor == null) {
            cursor = Cursor.getPredefinedCursor(0);
        }
        this.frame.setCursor(cursor);
    }
    
    protected LayoutManager createLayoutManager() {
        return this.getHandler();
    }
    
    protected PropertyChangeListener createPropertyChangeListener() {
        return this.getHandler();
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        if (this.frame == component) {
            return this.frame.getLayout().preferredLayoutSize(component);
        }
        return new Dimension(100, 100);
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        if (this.frame == component) {
            return this.frame.getLayout().minimumLayoutSize(component);
        }
        return new Dimension(0, 0);
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    protected void replacePane(final JComponent component, final JComponent component2) {
        if (component != null) {
            this.deinstallMouseHandlers(component);
            this.frame.remove(component);
        }
        if (component2 != null) {
            this.frame.add(component2);
            this.installMouseHandlers(component2);
        }
    }
    
    protected void deinstallMouseHandlers(final JComponent component) {
        component.removeMouseListener(this.borderListener);
        component.removeMouseMotionListener(this.borderListener);
    }
    
    protected void installMouseHandlers(final JComponent component) {
        component.addMouseListener(this.borderListener);
        component.addMouseMotionListener(this.borderListener);
    }
    
    protected JComponent createNorthPane(final JInternalFrame internalFrame) {
        return this.titlePane = new BasicInternalFrameTitlePane(internalFrame);
    }
    
    protected JComponent createSouthPane(final JInternalFrame internalFrame) {
        return null;
    }
    
    protected JComponent createWestPane(final JInternalFrame internalFrame) {
        return null;
    }
    
    protected JComponent createEastPane(final JInternalFrame internalFrame) {
        return null;
    }
    
    protected MouseInputAdapter createBorderListener(final JInternalFrame internalFrame) {
        return new BorderListener();
    }
    
    protected void createInternalFrameListener() {
        this.internalFrameListener = this.getHandler();
    }
    
    protected final boolean isKeyBindingRegistered() {
        return this.keyBindingRegistered;
    }
    
    protected final void setKeyBindingRegistered(final boolean keyBindingRegistered) {
        this.keyBindingRegistered = keyBindingRegistered;
    }
    
    public final boolean isKeyBindingActive() {
        return this.keyBindingActive;
    }
    
    protected final void setKeyBindingActive(final boolean keyBindingActive) {
        this.keyBindingActive = keyBindingActive;
    }
    
    protected void setupMenuOpenKey() {
        SwingUtilities.replaceUIInputMap(this.frame, 2, this.getInputMap(2));
    }
    
    protected void setupMenuCloseKey() {
    }
    
    public JComponent getNorthPane() {
        return this.northPane;
    }
    
    public void setNorthPane(final JComponent northPane) {
        if (this.northPane != null && this.northPane instanceof BasicInternalFrameTitlePane) {
            ((BasicInternalFrameTitlePane)this.northPane).uninstallListeners();
        }
        this.replacePane(this.northPane, northPane);
        this.northPane = northPane;
        if (northPane instanceof BasicInternalFrameTitlePane) {
            this.titlePane = (BasicInternalFrameTitlePane)northPane;
        }
    }
    
    public JComponent getSouthPane() {
        return this.southPane;
    }
    
    public void setSouthPane(final JComponent southPane) {
        this.southPane = southPane;
    }
    
    public JComponent getWestPane() {
        return this.westPane;
    }
    
    public void setWestPane(final JComponent westPane) {
        this.westPane = westPane;
    }
    
    public JComponent getEastPane() {
        return this.eastPane;
    }
    
    public void setEastPane(final JComponent eastPane) {
        this.eastPane = eastPane;
    }
    
    protected DesktopManager getDesktopManager() {
        if (this.frame.getDesktopPane() != null && this.frame.getDesktopPane().getDesktopManager() != null) {
            return this.frame.getDesktopPane().getDesktopManager();
        }
        if (BasicInternalFrameUI.sharedDesktopManager == null) {
            BasicInternalFrameUI.sharedDesktopManager = this.createDesktopManager();
        }
        return BasicInternalFrameUI.sharedDesktopManager;
    }
    
    protected DesktopManager createDesktopManager() {
        return new DefaultDesktopManager();
    }
    
    protected void closeFrame(final JInternalFrame internalFrame) {
        BasicLookAndFeel.playSound(this.frame, "InternalFrame.closeSound");
        this.getDesktopManager().closeFrame(internalFrame);
    }
    
    protected void maximizeFrame(final JInternalFrame internalFrame) {
        BasicLookAndFeel.playSound(this.frame, "InternalFrame.maximizeSound");
        this.getDesktopManager().maximizeFrame(internalFrame);
    }
    
    protected void minimizeFrame(final JInternalFrame internalFrame) {
        if (!internalFrame.isIcon()) {
            BasicLookAndFeel.playSound(this.frame, "InternalFrame.restoreDownSound");
        }
        this.getDesktopManager().minimizeFrame(internalFrame);
    }
    
    protected void iconifyFrame(final JInternalFrame internalFrame) {
        BasicLookAndFeel.playSound(this.frame, "InternalFrame.minimizeSound");
        this.getDesktopManager().iconifyFrame(internalFrame);
    }
    
    protected void deiconifyFrame(final JInternalFrame internalFrame) {
        if (!internalFrame.isMaximum()) {
            BasicLookAndFeel.playSound(this.frame, "InternalFrame.restoreUpSound");
        }
        this.getDesktopManager().deiconifyFrame(internalFrame);
    }
    
    protected void activateFrame(final JInternalFrame internalFrame) {
        this.getDesktopManager().activateFrame(internalFrame);
    }
    
    protected void deactivateFrame(final JInternalFrame internalFrame) {
        this.getDesktopManager().deactivateFrame(internalFrame);
    }
    
    protected ComponentListener createComponentListener() {
        return this.getHandler();
    }
    
    protected MouseInputListener createGlassPaneDispatcher() {
        return null;
    }
    
    public class InternalFramePropertyChangeListener implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            BasicInternalFrameUI.this.getHandler().propertyChange(propertyChangeEvent);
        }
    }
    
    public class InternalFrameLayout implements LayoutManager
    {
        @Override
        public void addLayoutComponent(final String s, final Component component) {
            BasicInternalFrameUI.this.getHandler().addLayoutComponent(s, component);
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
            BasicInternalFrameUI.this.getHandler().removeLayoutComponent(component);
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            return BasicInternalFrameUI.this.getHandler().preferredLayoutSize(container);
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            return BasicInternalFrameUI.this.getHandler().minimumLayoutSize(container);
        }
        
        @Override
        public void layoutContainer(final Container container) {
            BasicInternalFrameUI.this.getHandler().layoutContainer(container);
        }
    }
    
    protected class BorderListener extends MouseInputAdapter implements SwingConstants
    {
        int _x;
        int _y;
        int __x;
        int __y;
        Rectangle startingBounds;
        int resizeDir;
        protected final int RESIZE_NONE = 0;
        private boolean discardRelease;
        int resizeCornerSize;
        
        protected BorderListener() {
            this.discardRelease = false;
            this.resizeCornerSize = 16;
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            if (mouseEvent.getClickCount() > 1 && mouseEvent.getSource() == BasicInternalFrameUI.this.getNorthPane()) {
                if (BasicInternalFrameUI.this.frame.isIconifiable() && BasicInternalFrameUI.this.frame.isIcon()) {
                    try {
                        BasicInternalFrameUI.this.frame.setIcon(false);
                    }
                    catch (final PropertyVetoException ex) {}
                }
                else if (BasicInternalFrameUI.this.frame.isMaximizable()) {
                    if (!BasicInternalFrameUI.this.frame.isMaximum()) {
                        try {
                            BasicInternalFrameUI.this.frame.setMaximum(true);
                        }
                        catch (final PropertyVetoException ex2) {}
                    }
                    else {
                        try {
                            BasicInternalFrameUI.this.frame.setMaximum(false);
                        }
                        catch (final PropertyVetoException ex3) {}
                    }
                }
            }
        }
        
        void finishMouseReleased() {
            if (this.discardRelease) {
                this.discardRelease = false;
                return;
            }
            if (this.resizeDir == 0) {
                BasicInternalFrameUI.this.getDesktopManager().endDraggingFrame(BasicInternalFrameUI.this.frame);
                BasicInternalFrameUI.this.dragging = false;
            }
            else {
                final Window windowAncestor = SwingUtilities.getWindowAncestor(BasicInternalFrameUI.this.frame);
                if (windowAncestor != null) {
                    windowAncestor.removeWindowFocusListener(BasicInternalFrameUI.this.getWindowFocusListener());
                }
                final Container topLevelAncestor = BasicInternalFrameUI.this.frame.getTopLevelAncestor();
                if (topLevelAncestor instanceof RootPaneContainer) {
                    final Component glassPane = ((RootPaneContainer)topLevelAncestor).getGlassPane();
                    glassPane.setCursor(Cursor.getPredefinedCursor(0));
                    glassPane.setVisible(false);
                }
                BasicInternalFrameUI.this.getDesktopManager().endResizingFrame(BasicInternalFrameUI.this.frame);
                BasicInternalFrameUI.this.resizing = false;
                BasicInternalFrameUI.this.updateFrameCursor();
            }
            this._x = 0;
            this._y = 0;
            this.__x = 0;
            this.__y = 0;
            this.startingBounds = null;
            this.resizeDir = 0;
            this.discardRelease = true;
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            this.finishMouseReleased();
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            final Point convertPoint = SwingUtilities.convertPoint((Component)mouseEvent.getSource(), mouseEvent.getX(), mouseEvent.getY(), null);
            this.__x = mouseEvent.getX();
            this.__y = mouseEvent.getY();
            this._x = convertPoint.x;
            this._y = convertPoint.y;
            this.startingBounds = BasicInternalFrameUI.this.frame.getBounds();
            this.resizeDir = 0;
            this.discardRelease = false;
            try {
                BasicInternalFrameUI.this.frame.setSelected(true);
            }
            catch (final PropertyVetoException ex) {}
            final Insets insets = BasicInternalFrameUI.this.frame.getInsets();
            final Point point = new Point(this.__x, this.__y);
            if (mouseEvent.getSource() == BasicInternalFrameUI.this.getNorthPane()) {
                final Point location = BasicInternalFrameUI.this.getNorthPane().getLocation();
                final Point point2 = point;
                point2.x += location.x;
                final Point point3 = point;
                point3.y += location.y;
            }
            if (mouseEvent.getSource() == BasicInternalFrameUI.this.getNorthPane() && point.x > insets.left && point.y > insets.top && point.x < BasicInternalFrameUI.this.frame.getWidth() - insets.right) {
                BasicInternalFrameUI.this.getDesktopManager().beginDraggingFrame(BasicInternalFrameUI.this.frame);
                BasicInternalFrameUI.this.dragging = true;
                return;
            }
            if (!BasicInternalFrameUI.this.frame.isResizable()) {
                return;
            }
            if (mouseEvent.getSource() == BasicInternalFrameUI.this.frame || mouseEvent.getSource() == BasicInternalFrameUI.this.getNorthPane()) {
                if (point.x <= insets.left) {
                    if (point.y < this.resizeCornerSize + insets.top) {
                        this.resizeDir = 8;
                    }
                    else if (point.y > BasicInternalFrameUI.this.frame.getHeight() - this.resizeCornerSize - insets.bottom) {
                        this.resizeDir = 6;
                    }
                    else {
                        this.resizeDir = 7;
                    }
                }
                else if (point.x >= BasicInternalFrameUI.this.frame.getWidth() - insets.right) {
                    if (point.y < this.resizeCornerSize + insets.top) {
                        this.resizeDir = 2;
                    }
                    else if (point.y > BasicInternalFrameUI.this.frame.getHeight() - this.resizeCornerSize - insets.bottom) {
                        this.resizeDir = 4;
                    }
                    else {
                        this.resizeDir = 3;
                    }
                }
                else if (point.y <= insets.top) {
                    if (point.x < this.resizeCornerSize + insets.left) {
                        this.resizeDir = 8;
                    }
                    else if (point.x > BasicInternalFrameUI.this.frame.getWidth() - this.resizeCornerSize - insets.right) {
                        this.resizeDir = 2;
                    }
                    else {
                        this.resizeDir = 1;
                    }
                }
                else {
                    if (point.y < BasicInternalFrameUI.this.frame.getHeight() - insets.bottom) {
                        this.discardRelease = true;
                        return;
                    }
                    if (point.x < this.resizeCornerSize + insets.left) {
                        this.resizeDir = 6;
                    }
                    else if (point.x > BasicInternalFrameUI.this.frame.getWidth() - this.resizeCornerSize - insets.right) {
                        this.resizeDir = 4;
                    }
                    else {
                        this.resizeDir = 5;
                    }
                }
                Cursor cursor = Cursor.getPredefinedCursor(0);
                switch (this.resizeDir) {
                    case 5: {
                        cursor = Cursor.getPredefinedCursor(9);
                        break;
                    }
                    case 1: {
                        cursor = Cursor.getPredefinedCursor(8);
                        break;
                    }
                    case 7: {
                        cursor = Cursor.getPredefinedCursor(10);
                        break;
                    }
                    case 3: {
                        cursor = Cursor.getPredefinedCursor(11);
                        break;
                    }
                    case 4: {
                        cursor = Cursor.getPredefinedCursor(5);
                        break;
                    }
                    case 6: {
                        cursor = Cursor.getPredefinedCursor(4);
                        break;
                    }
                    case 8: {
                        cursor = Cursor.getPredefinedCursor(6);
                        break;
                    }
                    case 2: {
                        cursor = Cursor.getPredefinedCursor(7);
                        break;
                    }
                }
                final Container topLevelAncestor = BasicInternalFrameUI.this.frame.getTopLevelAncestor();
                if (topLevelAncestor instanceof RootPaneContainer) {
                    final Component glassPane = ((RootPaneContainer)topLevelAncestor).getGlassPane();
                    glassPane.setVisible(true);
                    glassPane.setCursor(cursor);
                }
                BasicInternalFrameUI.this.getDesktopManager().beginResizingFrame(BasicInternalFrameUI.this.frame, this.resizeDir);
                BasicInternalFrameUI.this.resizing = true;
                final Window windowAncestor = SwingUtilities.getWindowAncestor(BasicInternalFrameUI.this.frame);
                if (windowAncestor != null) {
                    windowAncestor.addWindowFocusListener(BasicInternalFrameUI.this.getWindowFocusListener());
                }
            }
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            if (this.startingBounds == null) {
                return;
            }
            final Point convertPoint = SwingUtilities.convertPoint((Component)mouseEvent.getSource(), mouseEvent.getX(), mouseEvent.getY(), null);
            int n = this._x - convertPoint.x;
            int n2 = this._y - convertPoint.y;
            final Dimension minimumSize = BasicInternalFrameUI.this.frame.getMinimumSize();
            final Dimension maximumSize = BasicInternalFrameUI.this.frame.getMaximumSize();
            final Insets insets = BasicInternalFrameUI.this.frame.getInsets();
            if (BasicInternalFrameUI.this.dragging) {
                if (BasicInternalFrameUI.this.frame.isMaximum() || (mouseEvent.getModifiers() & 0x10) != 0x10) {
                    return;
                }
                final Dimension size = BasicInternalFrameUI.this.frame.getParent().getSize();
                final int width = size.width;
                final int height = size.height;
                int n3 = this.startingBounds.x - n;
                int n4 = this.startingBounds.y - n2;
                if (n3 + insets.left <= -this.__x) {
                    n3 = -this.__x - insets.left + 1;
                }
                if (n4 + insets.top <= -this.__y) {
                    n4 = -this.__y - insets.top + 1;
                }
                if (n3 + this.__x + insets.right >= width) {
                    n3 = width - this.__x - insets.right - 1;
                }
                if (n4 + this.__y + insets.bottom >= height) {
                    n4 = height - this.__y - insets.bottom - 1;
                }
                BasicInternalFrameUI.this.getDesktopManager().dragFrame(BasicInternalFrameUI.this.frame, n3, n4);
            }
            else {
                if (!BasicInternalFrameUI.this.frame.isResizable()) {
                    return;
                }
                int n5 = BasicInternalFrameUI.this.frame.getX();
                int n6 = BasicInternalFrameUI.this.frame.getY();
                BasicInternalFrameUI.this.frame.getWidth();
                BasicInternalFrameUI.this.frame.getHeight();
                BasicInternalFrameUI.this.parentBounds = BasicInternalFrameUI.this.frame.getParent().getBounds();
                int n7 = 0;
                int n8 = 0;
                switch (this.resizeDir) {
                    case 0: {
                        return;
                    }
                    case 1: {
                        if (this.startingBounds.height + n2 < minimumSize.height) {
                            n2 = -(this.startingBounds.height - minimumSize.height);
                        }
                        else if (this.startingBounds.height + n2 > maximumSize.height) {
                            n2 = maximumSize.height - this.startingBounds.height;
                        }
                        if (this.startingBounds.y - n2 < 0) {
                            n2 = this.startingBounds.y;
                        }
                        n5 = this.startingBounds.x;
                        n6 = this.startingBounds.y - n2;
                        n7 = this.startingBounds.width;
                        n8 = this.startingBounds.height + n2;
                        break;
                    }
                    case 2: {
                        if (this.startingBounds.height + n2 < minimumSize.height) {
                            n2 = -(this.startingBounds.height - minimumSize.height);
                        }
                        else if (this.startingBounds.height + n2 > maximumSize.height) {
                            n2 = maximumSize.height - this.startingBounds.height;
                        }
                        if (this.startingBounds.y - n2 < 0) {
                            n2 = this.startingBounds.y;
                        }
                        if (this.startingBounds.width - n < minimumSize.width) {
                            n = this.startingBounds.width - minimumSize.width;
                        }
                        else if (this.startingBounds.width - n > maximumSize.width) {
                            n = -(maximumSize.width - this.startingBounds.width);
                        }
                        if (this.startingBounds.x + this.startingBounds.width - n > BasicInternalFrameUI.this.parentBounds.width) {
                            n = this.startingBounds.x + this.startingBounds.width - BasicInternalFrameUI.this.parentBounds.width;
                        }
                        n5 = this.startingBounds.x;
                        n6 = this.startingBounds.y - n2;
                        n7 = this.startingBounds.width - n;
                        n8 = this.startingBounds.height + n2;
                        break;
                    }
                    case 3: {
                        if (this.startingBounds.width - n < minimumSize.width) {
                            n = this.startingBounds.width - minimumSize.width;
                        }
                        else if (this.startingBounds.width - n > maximumSize.width) {
                            n = -(maximumSize.width - this.startingBounds.width);
                        }
                        if (this.startingBounds.x + this.startingBounds.width - n > BasicInternalFrameUI.this.parentBounds.width) {
                            n = this.startingBounds.x + this.startingBounds.width - BasicInternalFrameUI.this.parentBounds.width;
                        }
                        n7 = this.startingBounds.width - n;
                        n8 = this.startingBounds.height;
                        break;
                    }
                    case 4: {
                        if (this.startingBounds.width - n < minimumSize.width) {
                            n = this.startingBounds.width - minimumSize.width;
                        }
                        else if (this.startingBounds.width - n > maximumSize.width) {
                            n = -(maximumSize.width - this.startingBounds.width);
                        }
                        if (this.startingBounds.x + this.startingBounds.width - n > BasicInternalFrameUI.this.parentBounds.width) {
                            n = this.startingBounds.x + this.startingBounds.width - BasicInternalFrameUI.this.parentBounds.width;
                        }
                        if (this.startingBounds.height - n2 < minimumSize.height) {
                            n2 = this.startingBounds.height - minimumSize.height;
                        }
                        else if (this.startingBounds.height - n2 > maximumSize.height) {
                            n2 = -(maximumSize.height - this.startingBounds.height);
                        }
                        if (this.startingBounds.y + this.startingBounds.height - n2 > BasicInternalFrameUI.this.parentBounds.height) {
                            n2 = this.startingBounds.y + this.startingBounds.height - BasicInternalFrameUI.this.parentBounds.height;
                        }
                        n7 = this.startingBounds.width - n;
                        n8 = this.startingBounds.height - n2;
                        break;
                    }
                    case 5: {
                        if (this.startingBounds.height - n2 < minimumSize.height) {
                            n2 = this.startingBounds.height - minimumSize.height;
                        }
                        else if (this.startingBounds.height - n2 > maximumSize.height) {
                            n2 = -(maximumSize.height - this.startingBounds.height);
                        }
                        if (this.startingBounds.y + this.startingBounds.height - n2 > BasicInternalFrameUI.this.parentBounds.height) {
                            n2 = this.startingBounds.y + this.startingBounds.height - BasicInternalFrameUI.this.parentBounds.height;
                        }
                        n7 = this.startingBounds.width;
                        n8 = this.startingBounds.height - n2;
                        break;
                    }
                    case 6: {
                        if (this.startingBounds.height - n2 < minimumSize.height) {
                            n2 = this.startingBounds.height - minimumSize.height;
                        }
                        else if (this.startingBounds.height - n2 > maximumSize.height) {
                            n2 = -(maximumSize.height - this.startingBounds.height);
                        }
                        if (this.startingBounds.y + this.startingBounds.height - n2 > BasicInternalFrameUI.this.parentBounds.height) {
                            n2 = this.startingBounds.y + this.startingBounds.height - BasicInternalFrameUI.this.parentBounds.height;
                        }
                        if (this.startingBounds.width + n < minimumSize.width) {
                            n = -(this.startingBounds.width - minimumSize.width);
                        }
                        else if (this.startingBounds.width + n > maximumSize.width) {
                            n = maximumSize.width - this.startingBounds.width;
                        }
                        if (this.startingBounds.x - n < 0) {
                            n = this.startingBounds.x;
                        }
                        n5 = this.startingBounds.x - n;
                        n6 = this.startingBounds.y;
                        n7 = this.startingBounds.width + n;
                        n8 = this.startingBounds.height - n2;
                        break;
                    }
                    case 7: {
                        if (this.startingBounds.width + n < minimumSize.width) {
                            n = -(this.startingBounds.width - minimumSize.width);
                        }
                        else if (this.startingBounds.width + n > maximumSize.width) {
                            n = maximumSize.width - this.startingBounds.width;
                        }
                        if (this.startingBounds.x - n < 0) {
                            n = this.startingBounds.x;
                        }
                        n5 = this.startingBounds.x - n;
                        n6 = this.startingBounds.y;
                        n7 = this.startingBounds.width + n;
                        n8 = this.startingBounds.height;
                        break;
                    }
                    case 8: {
                        if (this.startingBounds.width + n < minimumSize.width) {
                            n = -(this.startingBounds.width - minimumSize.width);
                        }
                        else if (this.startingBounds.width + n > maximumSize.width) {
                            n = maximumSize.width - this.startingBounds.width;
                        }
                        if (this.startingBounds.x - n < 0) {
                            n = this.startingBounds.x;
                        }
                        if (this.startingBounds.height + n2 < minimumSize.height) {
                            n2 = -(this.startingBounds.height - minimumSize.height);
                        }
                        else if (this.startingBounds.height + n2 > maximumSize.height) {
                            n2 = maximumSize.height - this.startingBounds.height;
                        }
                        if (this.startingBounds.y - n2 < 0) {
                            n2 = this.startingBounds.y;
                        }
                        n5 = this.startingBounds.x - n;
                        n6 = this.startingBounds.y - n2;
                        n7 = this.startingBounds.width + n;
                        n8 = this.startingBounds.height + n2;
                        break;
                    }
                    default: {
                        return;
                    }
                }
                BasicInternalFrameUI.this.getDesktopManager().resizeFrame(BasicInternalFrameUI.this.frame, n5, n6, n7, n8);
            }
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            if (!BasicInternalFrameUI.this.frame.isResizable()) {
                return;
            }
            if (mouseEvent.getSource() == BasicInternalFrameUI.this.frame || mouseEvent.getSource() == BasicInternalFrameUI.this.getNorthPane()) {
                final Insets insets = BasicInternalFrameUI.this.frame.getInsets();
                final Point point = new Point(mouseEvent.getX(), mouseEvent.getY());
                if (mouseEvent.getSource() == BasicInternalFrameUI.this.getNorthPane()) {
                    final Point location = BasicInternalFrameUI.this.getNorthPane().getLocation();
                    final Point point2 = point;
                    point2.x += location.x;
                    final Point point3 = point;
                    point3.y += location.y;
                }
                if (point.x <= insets.left) {
                    if (point.y < this.resizeCornerSize + insets.top) {
                        BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(6));
                    }
                    else if (point.y > BasicInternalFrameUI.this.frame.getHeight() - this.resizeCornerSize - insets.bottom) {
                        BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(4));
                    }
                    else {
                        BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(10));
                    }
                }
                else if (point.x >= BasicInternalFrameUI.this.frame.getWidth() - insets.right) {
                    if (mouseEvent.getY() < this.resizeCornerSize + insets.top) {
                        BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(7));
                    }
                    else if (point.y > BasicInternalFrameUI.this.frame.getHeight() - this.resizeCornerSize - insets.bottom) {
                        BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(5));
                    }
                    else {
                        BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(11));
                    }
                }
                else if (point.y <= insets.top) {
                    if (point.x < this.resizeCornerSize + insets.left) {
                        BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(6));
                    }
                    else if (point.x > BasicInternalFrameUI.this.frame.getWidth() - this.resizeCornerSize - insets.right) {
                        BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(7));
                    }
                    else {
                        BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(8));
                    }
                }
                else if (point.y >= BasicInternalFrameUI.this.frame.getHeight() - insets.bottom) {
                    if (point.x < this.resizeCornerSize + insets.left) {
                        BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(4));
                    }
                    else if (point.x > BasicInternalFrameUI.this.frame.getWidth() - this.resizeCornerSize - insets.right) {
                        BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(5));
                    }
                    else {
                        BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(9));
                    }
                }
                else {
                    BasicInternalFrameUI.this.updateFrameCursor();
                }
                return;
            }
            BasicInternalFrameUI.this.updateFrameCursor();
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            BasicInternalFrameUI.this.updateFrameCursor();
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            BasicInternalFrameUI.this.updateFrameCursor();
        }
    }
    
    protected class ComponentHandler implements ComponentListener
    {
        @Override
        public void componentResized(final ComponentEvent componentEvent) {
            BasicInternalFrameUI.this.getHandler().componentResized(componentEvent);
        }
        
        @Override
        public void componentMoved(final ComponentEvent componentEvent) {
            BasicInternalFrameUI.this.getHandler().componentMoved(componentEvent);
        }
        
        @Override
        public void componentShown(final ComponentEvent componentEvent) {
            BasicInternalFrameUI.this.getHandler().componentShown(componentEvent);
        }
        
        @Override
        public void componentHidden(final ComponentEvent componentEvent) {
            BasicInternalFrameUI.this.getHandler().componentHidden(componentEvent);
        }
    }
    
    protected class GlassPaneDispatcher implements MouseInputListener
    {
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            BasicInternalFrameUI.this.getHandler().mousePressed(mouseEvent);
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            BasicInternalFrameUI.this.getHandler().mouseEntered(mouseEvent);
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            BasicInternalFrameUI.this.getHandler().mouseMoved(mouseEvent);
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            BasicInternalFrameUI.this.getHandler().mouseExited(mouseEvent);
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            BasicInternalFrameUI.this.getHandler().mouseClicked(mouseEvent);
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            BasicInternalFrameUI.this.getHandler().mouseReleased(mouseEvent);
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            BasicInternalFrameUI.this.getHandler().mouseDragged(mouseEvent);
        }
    }
    
    protected class BasicInternalFrameListener implements InternalFrameListener
    {
        @Override
        public void internalFrameClosing(final InternalFrameEvent internalFrameEvent) {
            BasicInternalFrameUI.this.getHandler().internalFrameClosing(internalFrameEvent);
        }
        
        @Override
        public void internalFrameClosed(final InternalFrameEvent internalFrameEvent) {
            BasicInternalFrameUI.this.getHandler().internalFrameClosed(internalFrameEvent);
        }
        
        @Override
        public void internalFrameOpened(final InternalFrameEvent internalFrameEvent) {
            BasicInternalFrameUI.this.getHandler().internalFrameOpened(internalFrameEvent);
        }
        
        @Override
        public void internalFrameIconified(final InternalFrameEvent internalFrameEvent) {
            BasicInternalFrameUI.this.getHandler().internalFrameIconified(internalFrameEvent);
        }
        
        @Override
        public void internalFrameDeiconified(final InternalFrameEvent internalFrameEvent) {
            BasicInternalFrameUI.this.getHandler().internalFrameDeiconified(internalFrameEvent);
        }
        
        @Override
        public void internalFrameActivated(final InternalFrameEvent internalFrameEvent) {
            BasicInternalFrameUI.this.getHandler().internalFrameActivated(internalFrameEvent);
        }
        
        @Override
        public void internalFrameDeactivated(final InternalFrameEvent internalFrameEvent) {
            BasicInternalFrameUI.this.getHandler().internalFrameDeactivated(internalFrameEvent);
        }
    }
    
    private class Handler implements ComponentListener, InternalFrameListener, LayoutManager, MouseInputListener, PropertyChangeListener, WindowFocusListener, SwingConstants
    {
        @Override
        public void windowGainedFocus(final WindowEvent windowEvent) {
        }
        
        @Override
        public void windowLostFocus(final WindowEvent windowEvent) {
            BasicInternalFrameUI.this.cancelResize();
        }
        
        @Override
        public void componentResized(final ComponentEvent componentEvent) {
            final Rectangle bounds = ((Component)componentEvent.getSource()).getBounds();
            Component desktopIcon = null;
            if (BasicInternalFrameUI.this.frame != null) {
                desktopIcon = BasicInternalFrameUI.this.frame.getDesktopIcon();
                if (BasicInternalFrameUI.this.frame.isMaximum()) {
                    BasicInternalFrameUI.this.frame.setBounds(0, 0, bounds.width, bounds.height);
                }
            }
            if (desktopIcon != null) {
                final Rectangle bounds2 = desktopIcon.getBounds();
                desktopIcon.setBounds(bounds2.x, bounds2.y + (bounds.height - BasicInternalFrameUI.this.parentBounds.height), bounds2.width, bounds2.height);
            }
            if (!BasicInternalFrameUI.this.parentBounds.equals(bounds)) {
                BasicInternalFrameUI.this.parentBounds = bounds;
            }
            if (BasicInternalFrameUI.this.frame != null) {
                BasicInternalFrameUI.this.frame.validate();
            }
        }
        
        @Override
        public void componentMoved(final ComponentEvent componentEvent) {
        }
        
        @Override
        public void componentShown(final ComponentEvent componentEvent) {
        }
        
        @Override
        public void componentHidden(final ComponentEvent componentEvent) {
        }
        
        @Override
        public void internalFrameClosed(final InternalFrameEvent internalFrameEvent) {
            BasicInternalFrameUI.this.frame.removeInternalFrameListener(BasicInternalFrameUI.this.getHandler());
        }
        
        @Override
        public void internalFrameActivated(final InternalFrameEvent internalFrameEvent) {
            if (!BasicInternalFrameUI.this.isKeyBindingRegistered()) {
                BasicInternalFrameUI.this.setKeyBindingRegistered(true);
                BasicInternalFrameUI.this.setupMenuOpenKey();
                BasicInternalFrameUI.this.setupMenuCloseKey();
            }
            if (BasicInternalFrameUI.this.isKeyBindingRegistered()) {
                BasicInternalFrameUI.this.setKeyBindingActive(true);
            }
        }
        
        @Override
        public void internalFrameDeactivated(final InternalFrameEvent internalFrameEvent) {
            BasicInternalFrameUI.this.setKeyBindingActive(false);
        }
        
        @Override
        public void internalFrameClosing(final InternalFrameEvent internalFrameEvent) {
        }
        
        @Override
        public void internalFrameOpened(final InternalFrameEvent internalFrameEvent) {
        }
        
        @Override
        public void internalFrameIconified(final InternalFrameEvent internalFrameEvent) {
        }
        
        @Override
        public void internalFrameDeiconified(final InternalFrameEvent internalFrameEvent) {
        }
        
        @Override
        public void addLayoutComponent(final String s, final Component component) {
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            final Insets insets = BasicInternalFrameUI.this.frame.getInsets();
            final Dimension dimension2;
            final Dimension dimension = dimension2 = new Dimension(BasicInternalFrameUI.this.frame.getRootPane().getPreferredSize());
            dimension2.width += insets.left + insets.right;
            final Dimension dimension3 = dimension;
            dimension3.height += insets.top + insets.bottom;
            if (BasicInternalFrameUI.this.getNorthPane() != null) {
                final Dimension preferredSize = BasicInternalFrameUI.this.getNorthPane().getPreferredSize();
                dimension.width = Math.max(preferredSize.width, dimension.width);
                final Dimension dimension4 = dimension;
                dimension4.height += preferredSize.height;
            }
            if (BasicInternalFrameUI.this.getSouthPane() != null) {
                final Dimension preferredSize2 = BasicInternalFrameUI.this.getSouthPane().getPreferredSize();
                dimension.width = Math.max(preferredSize2.width, dimension.width);
                final Dimension dimension5 = dimension;
                dimension5.height += preferredSize2.height;
            }
            if (BasicInternalFrameUI.this.getEastPane() != null) {
                final Dimension preferredSize3 = BasicInternalFrameUI.this.getEastPane().getPreferredSize();
                final Dimension dimension6 = dimension;
                dimension6.width += preferredSize3.width;
                dimension.height = Math.max(preferredSize3.height, dimension.height);
            }
            if (BasicInternalFrameUI.this.getWestPane() != null) {
                final Dimension preferredSize4 = BasicInternalFrameUI.this.getWestPane().getPreferredSize();
                final Dimension dimension7 = dimension;
                dimension7.width += preferredSize4.width;
                dimension.height = Math.max(preferredSize4.height, dimension.height);
            }
            return dimension;
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            Dimension dimension = new Dimension();
            if (BasicInternalFrameUI.this.getNorthPane() != null && BasicInternalFrameUI.this.getNorthPane() instanceof BasicInternalFrameTitlePane) {
                dimension = new Dimension(BasicInternalFrameUI.this.getNorthPane().getMinimumSize());
            }
            final Insets insets = BasicInternalFrameUI.this.frame.getInsets();
            final Dimension dimension2 = dimension;
            dimension2.width += insets.left + insets.right;
            final Dimension dimension3 = dimension;
            dimension3.height += insets.top + insets.bottom;
            return dimension;
        }
        
        @Override
        public void layoutContainer(final Container container) {
            final Insets insets = BasicInternalFrameUI.this.frame.getInsets();
            int left = insets.left;
            int top = insets.top;
            int n = BasicInternalFrameUI.this.frame.getWidth() - insets.left - insets.right;
            int n2 = BasicInternalFrameUI.this.frame.getHeight() - insets.top - insets.bottom;
            if (BasicInternalFrameUI.this.getNorthPane() != null) {
                final Dimension preferredSize = BasicInternalFrameUI.this.getNorthPane().getPreferredSize();
                if (DefaultLookup.getBoolean(BasicInternalFrameUI.this.frame, BasicInternalFrameUI.this, "InternalFrame.layoutTitlePaneAtOrigin", false)) {
                    top = 0;
                    n2 += insets.top;
                    BasicInternalFrameUI.this.getNorthPane().setBounds(0, 0, BasicInternalFrameUI.this.frame.getWidth(), preferredSize.height);
                }
                else {
                    BasicInternalFrameUI.this.getNorthPane().setBounds(left, top, n, preferredSize.height);
                }
                top += preferredSize.height;
                n2 -= preferredSize.height;
            }
            if (BasicInternalFrameUI.this.getSouthPane() != null) {
                final Dimension preferredSize2 = BasicInternalFrameUI.this.getSouthPane().getPreferredSize();
                BasicInternalFrameUI.this.getSouthPane().setBounds(left, BasicInternalFrameUI.this.frame.getHeight() - insets.bottom - preferredSize2.height, n, preferredSize2.height);
                n2 -= preferredSize2.height;
            }
            if (BasicInternalFrameUI.this.getWestPane() != null) {
                final Dimension preferredSize3 = BasicInternalFrameUI.this.getWestPane().getPreferredSize();
                BasicInternalFrameUI.this.getWestPane().setBounds(left, top, preferredSize3.width, n2);
                n -= preferredSize3.width;
                left += preferredSize3.width;
            }
            if (BasicInternalFrameUI.this.getEastPane() != null) {
                final Dimension preferredSize4 = BasicInternalFrameUI.this.getEastPane().getPreferredSize();
                BasicInternalFrameUI.this.getEastPane().setBounds(n - preferredSize4.width, top, preferredSize4.width, n2);
                n -= preferredSize4.width;
            }
            if (BasicInternalFrameUI.this.frame.getRootPane() != null) {
                BasicInternalFrameUI.this.frame.getRootPane().setBounds(left, top, n, n2);
            }
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            final JInternalFrame internalFrame = (JInternalFrame)propertyChangeEvent.getSource();
            final Object newValue = propertyChangeEvent.getNewValue();
            final Object oldValue = propertyChangeEvent.getOldValue();
            if ("closed" == propertyName) {
                if (newValue == Boolean.TRUE) {
                    BasicInternalFrameUI.this.cancelResize();
                    if (BasicInternalFrameUI.this.frame.getParent() != null && BasicInternalFrameUI.this.componentListenerAdded) {
                        BasicInternalFrameUI.this.frame.getParent().removeComponentListener(BasicInternalFrameUI.this.componentListener);
                    }
                    BasicInternalFrameUI.this.closeFrame(internalFrame);
                }
            }
            else if ("maximum" == propertyName) {
                if (newValue == Boolean.TRUE) {
                    BasicInternalFrameUI.this.maximizeFrame(internalFrame);
                }
                else {
                    BasicInternalFrameUI.this.minimizeFrame(internalFrame);
                }
            }
            else if ("icon" == propertyName) {
                if (newValue == Boolean.TRUE) {
                    BasicInternalFrameUI.this.iconifyFrame(internalFrame);
                }
                else {
                    BasicInternalFrameUI.this.deiconifyFrame(internalFrame);
                }
            }
            else if ("selected" == propertyName) {
                if (newValue == Boolean.TRUE && oldValue == Boolean.FALSE) {
                    BasicInternalFrameUI.this.activateFrame(internalFrame);
                }
                else if (newValue == Boolean.FALSE && oldValue == Boolean.TRUE) {
                    BasicInternalFrameUI.this.deactivateFrame(internalFrame);
                }
            }
            else if (propertyName == "ancestor") {
                if (newValue == null) {
                    BasicInternalFrameUI.this.cancelResize();
                }
                if (BasicInternalFrameUI.this.frame.getParent() != null) {
                    BasicInternalFrameUI.this.parentBounds = internalFrame.getParent().getBounds();
                }
                else {
                    BasicInternalFrameUI.this.parentBounds = null;
                }
                if (BasicInternalFrameUI.this.frame.getParent() != null && !BasicInternalFrameUI.this.componentListenerAdded) {
                    internalFrame.getParent().addComponentListener(BasicInternalFrameUI.this.componentListener);
                    BasicInternalFrameUI.this.componentListenerAdded = true;
                }
            }
            else if ("title" == propertyName || propertyName == "closable" || propertyName == "iconable" || propertyName == "maximizable") {
                final Dimension minimumSize = BasicInternalFrameUI.this.frame.getMinimumSize();
                final Dimension size = BasicInternalFrameUI.this.frame.getSize();
                if (minimumSize.width > size.width) {
                    BasicInternalFrameUI.this.frame.setSize(minimumSize.width, size.height);
                }
            }
        }
    }
}
