package javax.swing;

import javax.swing.event.MenuKeyEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.awt.Frame;
import java.awt.Container;
import java.util.Objects;
import javax.swing.event.MenuKeyListener;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.KeyboardFocusManager;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.FocusListener;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseAdapter;

public class ToolTipManager extends MouseAdapter implements MouseMotionListener
{
    Timer enterTimer;
    Timer exitTimer;
    Timer insideTimer;
    String toolTipText;
    Point preferredLocation;
    JComponent insideComponent;
    MouseEvent mouseEvent;
    boolean showImmediately;
    private static final Object TOOL_TIP_MANAGER_KEY;
    transient Popup tipWindow;
    private Window window;
    JToolTip tip;
    private Rectangle popupRect;
    private Rectangle popupFrameRect;
    boolean enabled;
    private boolean tipShowing;
    private FocusListener focusChangeListener;
    private MouseMotionListener moveBeforeEnterListener;
    private KeyListener accessibilityKeyListener;
    private KeyStroke postTip;
    private KeyStroke hideTip;
    protected boolean lightWeightPopupEnabled;
    protected boolean heavyWeightPopupEnabled;
    
    ToolTipManager() {
        this.popupRect = null;
        this.popupFrameRect = null;
        this.enabled = true;
        this.tipShowing = false;
        this.focusChangeListener = null;
        this.moveBeforeEnterListener = null;
        this.accessibilityKeyListener = null;
        this.lightWeightPopupEnabled = true;
        this.heavyWeightPopupEnabled = false;
        (this.enterTimer = new Timer(750, new insideTimerAction())).setRepeats(false);
        (this.exitTimer = new Timer(500, new outsideTimerAction())).setRepeats(false);
        (this.insideTimer = new Timer(4000, new stillInsideTimerAction())).setRepeats(false);
        this.moveBeforeEnterListener = new MoveBeforeEnterListener();
        this.accessibilityKeyListener = new AccessibilityKeyListener();
        this.postTip = KeyStroke.getKeyStroke(112, 2);
        this.hideTip = KeyStroke.getKeyStroke(27, 0);
    }
    
    public void setEnabled(final boolean enabled) {
        if (!(this.enabled = enabled)) {
            this.hideTipWindow();
        }
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setLightWeightPopupEnabled(final boolean lightWeightPopupEnabled) {
        this.lightWeightPopupEnabled = lightWeightPopupEnabled;
    }
    
    public boolean isLightWeightPopupEnabled() {
        return this.lightWeightPopupEnabled;
    }
    
    public void setInitialDelay(final int initialDelay) {
        this.enterTimer.setInitialDelay(initialDelay);
    }
    
    public int getInitialDelay() {
        return this.enterTimer.getInitialDelay();
    }
    
    public void setDismissDelay(final int initialDelay) {
        this.insideTimer.setInitialDelay(initialDelay);
    }
    
    public int getDismissDelay() {
        return this.insideTimer.getInitialDelay();
    }
    
    public void setReshowDelay(final int initialDelay) {
        this.exitTimer.setInitialDelay(initialDelay);
    }
    
    public int getReshowDelay() {
        return this.exitTimer.getInitialDelay();
    }
    
    private GraphicsConfiguration getDrawingGC(final Point point) {
        final GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        for (int length = screenDevices.length, i = 0; i < length; ++i) {
            for (final GraphicsConfiguration graphicsConfiguration : screenDevices[i].getConfigurations()) {
                if (graphicsConfiguration.getBounds().contains(point)) {
                    return graphicsConfiguration;
                }
            }
        }
        return null;
    }
    
    void showTipWindow() {
        if (this.insideComponent == null || !this.insideComponent.isShowing()) {
            return;
        }
        if ("activeApplication".equals(UIManager.getString("ToolTipManager.enableToolTipMode")) && KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow() == null) {
            return;
        }
        if (this.enabled) {
            final Point locationOnScreen = this.insideComponent.getLocationOnScreen();
            Point point;
            if (this.preferredLocation != null) {
                point = new Point(locationOnScreen.x + this.preferredLocation.x, locationOnScreen.y + this.preferredLocation.y);
            }
            else {
                point = this.mouseEvent.getLocationOnScreen();
            }
            GraphicsConfiguration graphicsConfiguration = this.getDrawingGC(point);
            if (graphicsConfiguration == null) {
                point = this.mouseEvent.getLocationOnScreen();
                graphicsConfiguration = this.getDrawingGC(point);
                if (graphicsConfiguration == null) {
                    graphicsConfiguration = this.insideComponent.getGraphicsConfiguration();
                }
            }
            final Rectangle bounds = graphicsConfiguration.getBounds();
            final Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration);
            final Rectangle rectangle = bounds;
            rectangle.x += screenInsets.left;
            final Rectangle rectangle2 = bounds;
            rectangle2.y += screenInsets.top;
            final Rectangle rectangle3 = bounds;
            rectangle3.width -= screenInsets.left + screenInsets.right;
            final Rectangle rectangle4 = bounds;
            rectangle4.height -= screenInsets.top + screenInsets.bottom;
            final boolean leftToRight = SwingUtilities.isLeftToRight(this.insideComponent);
            this.hideTipWindow();
            (this.tip = this.insideComponent.createToolTip()).setTipText(this.toolTipText);
            final Dimension preferredSize = this.tip.getPreferredSize();
            Point point2;
            if (this.preferredLocation != null) {
                point2 = point;
                if (!leftToRight) {
                    final Point point3 = point2;
                    point3.x -= preferredSize.width;
                }
            }
            else {
                point2 = new Point(locationOnScreen.x + this.mouseEvent.getX(), locationOnScreen.y + this.mouseEvent.getY() + 20);
                if (!leftToRight && point2.x - preferredSize.width >= 0) {
                    final Point point4 = point2;
                    point4.x -= preferredSize.width;
                }
            }
            if (this.popupRect == null) {
                this.popupRect = new Rectangle();
            }
            this.popupRect.setBounds(point2.x, point2.y, preferredSize.width, preferredSize.height);
            if (point2.x < bounds.x) {
                point2.x = bounds.x;
            }
            else if (point2.x - bounds.x + preferredSize.width > bounds.width) {
                point2.x = bounds.x + Math.max(0, bounds.width - preferredSize.width);
            }
            if (point2.y < bounds.y) {
                point2.y = bounds.y;
            }
            else if (point2.y - bounds.y + preferredSize.height > bounds.height) {
                point2.y = bounds.y + Math.max(0, bounds.height - preferredSize.height);
            }
            final PopupFactory sharedInstance = PopupFactory.getSharedInstance();
            if (this.lightWeightPopupEnabled) {
                final int popupFitHeight = this.getPopupFitHeight(this.popupRect, this.insideComponent);
                if (this.getPopupFitWidth(this.popupRect, this.insideComponent) > 0 || popupFitHeight > 0) {
                    sharedInstance.setPopupType(1);
                }
                else {
                    sharedInstance.setPopupType(0);
                }
            }
            else {
                sharedInstance.setPopupType(1);
            }
            this.tipWindow = sharedInstance.getPopup(this.insideComponent, this.tip, point2.x, point2.y);
            sharedInstance.setPopupType(0);
            this.tipWindow.show();
            final Window windowForComponent = SwingUtilities.windowForComponent(this.insideComponent);
            this.window = SwingUtilities.windowForComponent(this.tip);
            if (this.window != null && this.window != windowForComponent) {
                this.window.addMouseListener(this);
            }
            else {
                this.window = null;
            }
            this.insideTimer.start();
            this.tipShowing = true;
        }
    }
    
    void hideTipWindow() {
        if (this.tipWindow != null) {
            if (this.window != null) {
                this.window.removeMouseListener(this);
                this.window = null;
            }
            this.tipWindow.hide();
            this.tipWindow = null;
            this.tipShowing = false;
            this.tip = null;
            this.insideTimer.stop();
        }
    }
    
    public static ToolTipManager sharedInstance() {
        final Object appContextGet = SwingUtilities.appContextGet(ToolTipManager.TOOL_TIP_MANAGER_KEY);
        if (appContextGet instanceof ToolTipManager) {
            return (ToolTipManager)appContextGet;
        }
        final ToolTipManager toolTipManager = new ToolTipManager();
        SwingUtilities.appContextPut(ToolTipManager.TOOL_TIP_MANAGER_KEY, toolTipManager);
        return toolTipManager;
    }
    
    public void registerComponent(final JComponent component) {
        component.removeMouseListener(this);
        component.addMouseListener(this);
        component.removeMouseMotionListener(this.moveBeforeEnterListener);
        component.addMouseMotionListener(this.moveBeforeEnterListener);
        if (component instanceof JMenuItem) {
            ((JMenuItem)component).removeMenuKeyListener((MenuKeyListener)this.accessibilityKeyListener);
            ((JMenuItem)component).addMenuKeyListener((MenuKeyListener)this.accessibilityKeyListener);
        }
        else {
            component.removeKeyListener(this.accessibilityKeyListener);
            component.addKeyListener(this.accessibilityKeyListener);
        }
    }
    
    public void unregisterComponent(final JComponent component) {
        component.removeMouseListener(this);
        component.removeMouseMotionListener(this.moveBeforeEnterListener);
        if (component instanceof JMenuItem) {
            ((JMenuItem)component).removeMenuKeyListener((MenuKeyListener)this.accessibilityKeyListener);
        }
        else {
            component.removeKeyListener(this.accessibilityKeyListener);
        }
    }
    
    @Override
    public void mouseEntered(final MouseEvent mouseEvent) {
        this.initiateToolTip(mouseEvent);
    }
    
    private void initiateToolTip(final MouseEvent mouseEvent) {
        if (mouseEvent.getSource() == this.window) {
            return;
        }
        final JComponent insideComponent = (JComponent)mouseEvent.getSource();
        insideComponent.removeMouseMotionListener(this.moveBeforeEnterListener);
        this.exitTimer.stop();
        final Point point = mouseEvent.getPoint();
        if (point.x < 0 || point.x >= insideComponent.getWidth() || point.y < 0 || point.y >= insideComponent.getHeight()) {
            return;
        }
        if (this.insideComponent != null) {
            this.enterTimer.stop();
        }
        insideComponent.removeMouseMotionListener(this);
        insideComponent.addMouseMotionListener(this);
        final boolean b = this.insideComponent == insideComponent;
        this.insideComponent = insideComponent;
        if (this.tipWindow != null) {
            this.mouseEvent = mouseEvent;
            if (this.showImmediately) {
                final String toolTipText = insideComponent.getToolTipText(mouseEvent);
                final Point toolTipLocation = insideComponent.getToolTipLocation(mouseEvent);
                final boolean b2 = (this.preferredLocation != null) ? this.preferredLocation.equals(toolTipLocation) : (toolTipLocation == null);
                if (!b || !Objects.equals(this.toolTipText, toolTipText) || !b2) {
                    this.toolTipText = toolTipText;
                    this.preferredLocation = toolTipLocation;
                    this.showTipWindow();
                }
            }
            else {
                this.enterTimer.start();
            }
        }
    }
    
    @Override
    public void mouseExited(final MouseEvent mouseEvent) {
        boolean b = true;
        if (this.insideComponent == null) {}
        if (this.window != null && mouseEvent.getSource() == this.window && this.insideComponent != null) {
            final Container topLevelAncestor = this.insideComponent.getTopLevelAncestor();
            if (topLevelAncestor != null) {
                final Point point = mouseEvent.getPoint();
                SwingUtilities.convertPointToScreen(point, this.window);
                final Point point2 = point;
                point2.x -= topLevelAncestor.getX();
                final Point point3 = point;
                point3.y -= topLevelAncestor.getY();
                final Point convertPoint = SwingUtilities.convertPoint(null, point, this.insideComponent);
                b = (convertPoint.x < 0 || convertPoint.x >= this.insideComponent.getWidth() || convertPoint.y < 0 || convertPoint.y >= this.insideComponent.getHeight());
            }
        }
        else if (mouseEvent.getSource() == this.insideComponent && this.tipWindow != null) {
            final Window windowAncestor = SwingUtilities.getWindowAncestor(this.insideComponent);
            if (windowAncestor != null) {
                final Point convertPoint2 = SwingUtilities.convertPoint(this.insideComponent, mouseEvent.getPoint(), windowAncestor);
                final Rectangle bounds = this.insideComponent.getTopLevelAncestor().getBounds();
                final Point point4 = convertPoint2;
                point4.x += bounds.x;
                final Point point5 = convertPoint2;
                point5.y += bounds.y;
                final Point point6 = new Point(0, 0);
                SwingUtilities.convertPointToScreen(point6, this.tip);
                bounds.x = point6.x;
                bounds.y = point6.y;
                bounds.width = this.tip.getWidth();
                bounds.height = this.tip.getHeight();
                b = (convertPoint2.x < bounds.x || convertPoint2.x >= bounds.x + bounds.width || convertPoint2.y < bounds.y || convertPoint2.y >= bounds.y + bounds.height);
            }
        }
        if (b) {
            this.enterTimer.stop();
            if (this.insideComponent != null) {
                this.insideComponent.removeMouseMotionListener(this);
            }
            this.insideComponent = null;
            this.toolTipText = null;
            this.mouseEvent = null;
            this.hideTipWindow();
            this.exitTimer.restart();
        }
    }
    
    @Override
    public void mousePressed(final MouseEvent mouseEvent) {
        this.hideTipWindow();
        this.enterTimer.stop();
        this.showImmediately = false;
        this.insideComponent = null;
        this.mouseEvent = null;
    }
    
    @Override
    public void mouseDragged(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mouseMoved(final MouseEvent mouseEvent) {
        if (this.tipShowing) {
            this.checkForTipChange(mouseEvent);
        }
        else if (this.showImmediately) {
            final JComponent insideComponent = (JComponent)mouseEvent.getSource();
            this.toolTipText = insideComponent.getToolTipText(mouseEvent);
            if (this.toolTipText != null) {
                this.preferredLocation = insideComponent.getToolTipLocation(mouseEvent);
                this.mouseEvent = mouseEvent;
                this.insideComponent = insideComponent;
                this.exitTimer.stop();
                this.showTipWindow();
            }
        }
        else {
            this.insideComponent = (JComponent)mouseEvent.getSource();
            this.mouseEvent = mouseEvent;
            this.toolTipText = null;
            this.enterTimer.restart();
        }
    }
    
    private void checkForTipChange(final MouseEvent mouseEvent) {
        final JComponent component = (JComponent)mouseEvent.getSource();
        final String toolTipText = component.getToolTipText(mouseEvent);
        final Point toolTipLocation = component.getToolTipLocation(mouseEvent);
        if (toolTipText != null || toolTipLocation != null) {
            this.mouseEvent = mouseEvent;
            if (((toolTipText != null && toolTipText.equals(this.toolTipText)) || toolTipText == null) && ((toolTipLocation != null && toolTipLocation.equals(this.preferredLocation)) || toolTipLocation == null)) {
                if (this.tipWindow != null) {
                    this.insideTimer.restart();
                }
                else {
                    this.enterTimer.restart();
                }
            }
            else {
                this.toolTipText = toolTipText;
                this.preferredLocation = toolTipLocation;
                if (this.showImmediately) {
                    this.hideTipWindow();
                    this.showTipWindow();
                    this.exitTimer.stop();
                }
                else {
                    this.enterTimer.restart();
                }
            }
        }
        else {
            this.toolTipText = null;
            this.preferredLocation = null;
            this.mouseEvent = null;
            this.insideComponent = null;
            this.hideTipWindow();
            this.enterTimer.stop();
            this.exitTimer.restart();
        }
    }
    
    static Frame frameForComponent(Component parent) {
        while (!(parent instanceof Frame)) {
            parent = parent.getParent();
        }
        return (Frame)parent;
    }
    
    private FocusListener createFocusChangeListener() {
        return new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent focusEvent) {
                ToolTipManager.this.hideTipWindow();
                ToolTipManager.this.insideComponent = null;
                ((JComponent)focusEvent.getSource()).removeFocusListener(ToolTipManager.this.focusChangeListener);
            }
        };
    }
    
    private int getPopupFitWidth(final Rectangle rectangle, final Component component) {
        if (component != null) {
            for (Container container = component.getParent(); container != null; container = container.getParent()) {
                if (container instanceof JFrame || container instanceof JDialog || container instanceof JWindow) {
                    return this.getWidthAdjust(container.getBounds(), rectangle);
                }
                if (container instanceof JApplet || container instanceof JInternalFrame) {
                    if (this.popupFrameRect == null) {
                        this.popupFrameRect = new Rectangle();
                    }
                    final Point locationOnScreen = container.getLocationOnScreen();
                    this.popupFrameRect.setBounds(locationOnScreen.x, locationOnScreen.y, container.getBounds().width, container.getBounds().height);
                    return this.getWidthAdjust(this.popupFrameRect, rectangle);
                }
            }
        }
        return 0;
    }
    
    private int getPopupFitHeight(final Rectangle rectangle, final Component component) {
        if (component != null) {
            for (Container container = component.getParent(); container != null; container = container.getParent()) {
                if (container instanceof JFrame || container instanceof JDialog || container instanceof JWindow) {
                    return this.getHeightAdjust(container.getBounds(), rectangle);
                }
                if (container instanceof JApplet || container instanceof JInternalFrame) {
                    if (this.popupFrameRect == null) {
                        this.popupFrameRect = new Rectangle();
                    }
                    final Point locationOnScreen = container.getLocationOnScreen();
                    this.popupFrameRect.setBounds(locationOnScreen.x, locationOnScreen.y, container.getBounds().width, container.getBounds().height);
                    return this.getHeightAdjust(this.popupFrameRect, rectangle);
                }
            }
        }
        return 0;
    }
    
    private int getHeightAdjust(final Rectangle rectangle, final Rectangle rectangle2) {
        if (rectangle2.y >= rectangle.y && rectangle2.y + rectangle2.height <= rectangle.y + rectangle.height) {
            return 0;
        }
        return rectangle2.y + rectangle2.height - (rectangle.y + rectangle.height) + 5;
    }
    
    private int getWidthAdjust(final Rectangle rectangle, final Rectangle rectangle2) {
        if (rectangle2.x >= rectangle.x && rectangle2.x + rectangle2.width <= rectangle.x + rectangle.width) {
            return 0;
        }
        return rectangle2.x + rectangle2.width - (rectangle.x + rectangle.width) + 5;
    }
    
    private void show(final JComponent insideComponent) {
        if (this.tipWindow != null) {
            this.hideTipWindow();
            this.insideComponent = null;
        }
        else {
            this.hideTipWindow();
            this.enterTimer.stop();
            this.exitTimer.stop();
            this.insideTimer.stop();
            this.insideComponent = insideComponent;
            if (this.insideComponent != null) {
                this.toolTipText = this.insideComponent.getToolTipText();
                this.preferredLocation = new Point(10, this.insideComponent.getHeight() + 10);
                this.showTipWindow();
                if (this.focusChangeListener == null) {
                    this.focusChangeListener = this.createFocusChangeListener();
                }
                this.insideComponent.addFocusListener(this.focusChangeListener);
            }
        }
    }
    
    private void hide(final JComponent component) {
        this.hideTipWindow();
        component.removeFocusListener(this.focusChangeListener);
        this.preferredLocation = null;
        this.insideComponent = null;
    }
    
    static {
        TOOL_TIP_MANAGER_KEY = new Object();
    }
    
    protected class insideTimerAction implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (ToolTipManager.this.insideComponent != null && ToolTipManager.this.insideComponent.isShowing()) {
                if (ToolTipManager.this.toolTipText == null && ToolTipManager.this.mouseEvent != null) {
                    ToolTipManager.this.toolTipText = ToolTipManager.this.insideComponent.getToolTipText(ToolTipManager.this.mouseEvent);
                    ToolTipManager.this.preferredLocation = ToolTipManager.this.insideComponent.getToolTipLocation(ToolTipManager.this.mouseEvent);
                }
                if (ToolTipManager.this.toolTipText != null) {
                    ToolTipManager.this.showImmediately = true;
                    ToolTipManager.this.showTipWindow();
                }
                else {
                    ToolTipManager.this.insideComponent = null;
                    ToolTipManager.this.toolTipText = null;
                    ToolTipManager.this.preferredLocation = null;
                    ToolTipManager.this.mouseEvent = null;
                    ToolTipManager.this.hideTipWindow();
                }
            }
        }
    }
    
    protected class outsideTimerAction implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            ToolTipManager.this.showImmediately = false;
        }
    }
    
    protected class stillInsideTimerAction implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            ToolTipManager.this.hideTipWindow();
            ToolTipManager.this.enterTimer.stop();
            ToolTipManager.this.showImmediately = false;
            ToolTipManager.this.insideComponent = null;
            ToolTipManager.this.mouseEvent = null;
        }
    }
    
    private class MoveBeforeEnterListener extends MouseMotionAdapter
    {
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            ToolTipManager.this.initiateToolTip(mouseEvent);
        }
    }
    
    private class AccessibilityKeyListener extends KeyAdapter implements MenuKeyListener
    {
        @Override
        public void keyPressed(final KeyEvent keyEvent) {
            if (!keyEvent.isConsumed()) {
                final JComponent component = (JComponent)keyEvent.getComponent();
                final KeyStroke keyStrokeForEvent = KeyStroke.getKeyStrokeForEvent(keyEvent);
                if (ToolTipManager.this.hideTip.equals(keyStrokeForEvent)) {
                    if (ToolTipManager.this.tipWindow != null) {
                        ToolTipManager.this.hide(component);
                        keyEvent.consume();
                    }
                }
                else if (ToolTipManager.this.postTip.equals(keyStrokeForEvent)) {
                    ToolTipManager.this.show(component);
                    keyEvent.consume();
                }
            }
        }
        
        @Override
        public void menuKeyTyped(final MenuKeyEvent menuKeyEvent) {
        }
        
        @Override
        public void menuKeyPressed(final MenuKeyEvent menuKeyEvent) {
            if (ToolTipManager.this.postTip.equals(KeyStroke.getKeyStrokeForEvent(menuKeyEvent))) {
                final MenuElement[] path = menuKeyEvent.getPath();
                final MenuElement menuElement = path[path.length - 1];
                final MenuElement[] selectedPath = menuKeyEvent.getMenuSelectionManager().getSelectedPath();
                if (menuElement.equals(selectedPath[selectedPath.length - 1])) {
                    ToolTipManager.this.show((JComponent)menuElement.getComponent());
                    menuKeyEvent.consume();
                }
            }
        }
        
        @Override
        public void menuKeyReleased(final MenuKeyEvent menuKeyEvent) {
        }
    }
}
