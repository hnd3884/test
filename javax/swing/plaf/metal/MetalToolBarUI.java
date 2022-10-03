package javax.swing.plaf.metal;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.UIManager;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.UIResource;
import javax.swing.JCheckBox;
import javax.swing.JToggleButton;
import javax.swing.plaf.ComponentUI;
import java.awt.Point;
import javax.swing.JToolBar;
import javax.swing.JRootPane;
import java.awt.Component;
import javax.swing.SwingUtilities;
import javax.swing.JMenuBar;
import javax.swing.border.Border;
import java.beans.PropertyChangeListener;
import java.awt.event.ContainerListener;
import javax.swing.JComponent;
import java.lang.ref.WeakReference;
import java.util.List;
import javax.swing.plaf.basic.BasicToolBarUI;

public class MetalToolBarUI extends BasicToolBarUI
{
    private static List<WeakReference<JComponent>> components;
    protected ContainerListener contListener;
    protected PropertyChangeListener rolloverListener;
    private static Border nonRolloverBorder;
    private JMenuBar lastMenuBar;
    
    static synchronized void register(final JComponent component) {
        if (component == null) {
            throw new NullPointerException("JComponent must be non-null");
        }
        MetalToolBarUI.components.add(new WeakReference<JComponent>(component));
    }
    
    static synchronized void unregister(final JComponent component) {
        for (int i = MetalToolBarUI.components.size() - 1; i >= 0; --i) {
            final JComponent component2 = MetalToolBarUI.components.get(i).get();
            if (component2 == component || component2 == null) {
                MetalToolBarUI.components.remove(i);
            }
        }
    }
    
    static synchronized Object findRegisteredComponentOfType(final JComponent component, final Class clazz) {
        final JRootPane rootPane = SwingUtilities.getRootPane(component);
        if (rootPane != null) {
            for (int i = MetalToolBarUI.components.size() - 1; i >= 0; --i) {
                final Object value = MetalToolBarUI.components.get(i).get();
                if (value == null) {
                    MetalToolBarUI.components.remove(i);
                }
                else if (clazz.isInstance(value) && SwingUtilities.getRootPane((Component)value) == rootPane) {
                    return value;
                }
            }
        }
        return null;
    }
    
    static boolean doesMenuBarBorderToolBar(final JMenuBar menuBar) {
        final JToolBar toolBar = (JToolBar)findRegisteredComponentOfType(menuBar, JToolBar.class);
        if (toolBar != null && toolBar.getOrientation() == 0) {
            final JRootPane rootPane = SwingUtilities.getRootPane(menuBar);
            final Point convertPoint = SwingUtilities.convertPoint(menuBar, new Point(0, 0), rootPane);
            final int x = convertPoint.x;
            final int y = convertPoint.y;
            final Point point = convertPoint;
            final Point point2 = convertPoint;
            final int n = 0;
            point2.y = n;
            point.x = n;
            final Point convertPoint2 = SwingUtilities.convertPoint(toolBar, convertPoint, rootPane);
            return convertPoint2.x == x && y + menuBar.getHeight() == convertPoint2.y && menuBar.getWidth() == toolBar.getWidth();
        }
        return false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new MetalToolBarUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        register(component);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        super.uninstallUI(component);
        MetalToolBarUI.nonRolloverBorder = null;
        unregister(component);
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.contListener = this.createContainerListener();
        if (this.contListener != null) {
            this.toolBar.addContainerListener(this.contListener);
        }
        this.rolloverListener = this.createRolloverListener();
        if (this.rolloverListener != null) {
            this.toolBar.addPropertyChangeListener(this.rolloverListener);
        }
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        if (this.contListener != null) {
            this.toolBar.removeContainerListener(this.contListener);
        }
        this.rolloverListener = this.createRolloverListener();
        if (this.rolloverListener != null) {
            this.toolBar.removePropertyChangeListener(this.rolloverListener);
        }
    }
    
    @Override
    protected Border createRolloverBorder() {
        return super.createRolloverBorder();
    }
    
    @Override
    protected Border createNonRolloverBorder() {
        return super.createNonRolloverBorder();
    }
    
    private Border createNonRolloverToggleBorder() {
        return this.createNonRolloverBorder();
    }
    
    @Override
    protected void setBorderToNonRollover(final Component component) {
        if (component instanceof JToggleButton && !(component instanceof JCheckBox)) {
            final JToggleButton toggleButton = (JToggleButton)component;
            final Border border = toggleButton.getBorder();
            super.setBorderToNonRollover(component);
            if (border instanceof UIResource) {
                if (MetalToolBarUI.nonRolloverBorder == null) {
                    MetalToolBarUI.nonRolloverBorder = this.createNonRolloverToggleBorder();
                }
                toggleButton.setBorder(MetalToolBarUI.nonRolloverBorder);
            }
        }
        else {
            super.setBorderToNonRollover(component);
        }
    }
    
    protected ContainerListener createContainerListener() {
        return null;
    }
    
    protected PropertyChangeListener createRolloverListener() {
        return null;
    }
    
    @Override
    protected MouseInputListener createDockingListener() {
        return new MetalDockingListener(this.toolBar);
    }
    
    protected void setDragOffset(final Point offset) {
        if (!GraphicsEnvironment.isHeadless()) {
            if (this.dragWindow == null) {
                this.dragWindow = this.createDragWindow(this.toolBar);
            }
            this.dragWindow.setOffset(offset);
        }
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        if (graphics == null) {
            throw new NullPointerException("graphics must be non-null");
        }
        if (component.isOpaque() && component.getBackground() instanceof UIResource && ((JToolBar)component).getOrientation() == 0 && UIManager.get("MenuBar.gradient") != null) {
            final JRootPane rootPane = SwingUtilities.getRootPane(component);
            final JMenuBar lastMenuBar = (JMenuBar)findRegisteredComponentOfType(component, JMenuBar.class);
            if (lastMenuBar != null && lastMenuBar.isOpaque() && lastMenuBar.getBackground() instanceof UIResource) {
                final Point convertPoint = SwingUtilities.convertPoint(component, new Point(0, 0), rootPane);
                final int x = convertPoint.x;
                final int y = convertPoint.y;
                final Point point = convertPoint;
                final Point point2 = convertPoint;
                final int n = 0;
                point2.y = n;
                point.x = n;
                final Point convertPoint2 = SwingUtilities.convertPoint(lastMenuBar, convertPoint, rootPane);
                if (convertPoint2.x == x && y == convertPoint2.y + lastMenuBar.getHeight() && lastMenuBar.getWidth() == component.getWidth() && MetalUtils.drawGradient(component, graphics, "MenuBar.gradient", 0, -lastMenuBar.getHeight(), component.getWidth(), component.getHeight() + lastMenuBar.getHeight(), true)) {
                    this.setLastMenuBar(lastMenuBar);
                    this.paint(graphics, component);
                    return;
                }
            }
            if (MetalUtils.drawGradient(component, graphics, "MenuBar.gradient", 0, 0, component.getWidth(), component.getHeight(), true)) {
                this.setLastMenuBar(null);
                this.paint(graphics, component);
                return;
            }
        }
        this.setLastMenuBar(null);
        super.update(graphics, component);
    }
    
    private void setLastMenuBar(final JMenuBar lastMenuBar) {
        if (MetalLookAndFeel.usingOcean() && this.lastMenuBar != lastMenuBar) {
            if (this.lastMenuBar != null) {
                this.lastMenuBar.repaint();
            }
            if (lastMenuBar != null) {
                lastMenuBar.repaint();
            }
            this.lastMenuBar = lastMenuBar;
        }
    }
    
    static {
        MetalToolBarUI.components = new ArrayList<WeakReference<JComponent>>();
    }
    
    protected class MetalContainerListener extends ToolBarContListener
    {
    }
    
    protected class MetalRolloverListener extends PropertyListener
    {
    }
    
    protected class MetalDockingListener extends DockingListener
    {
        private boolean pressedInBumps;
        
        public MetalDockingListener(final JToolBar toolBar) {
            super(toolBar);
            this.pressedInBumps = false;
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            super.mousePressed(mouseEvent);
            if (!this.toolBar.isEnabled()) {
                return;
            }
            this.pressedInBumps = false;
            final Rectangle rectangle = new Rectangle();
            if (this.toolBar.getOrientation() == 0) {
                rectangle.setBounds(MetalUtils.isLeftToRight(this.toolBar) ? 0 : (this.toolBar.getSize().width - 14), 0, 14, this.toolBar.getSize().height);
            }
            else {
                rectangle.setBounds(0, 0, this.toolBar.getSize().width, 14);
            }
            if (rectangle.contains(mouseEvent.getPoint())) {
                this.pressedInBumps = true;
                final Point point = mouseEvent.getPoint();
                if (!MetalUtils.isLeftToRight(this.toolBar)) {
                    final Point point2 = point;
                    point2.x -= this.toolBar.getSize().width - this.toolBar.getPreferredSize().width;
                }
                MetalToolBarUI.this.setDragOffset(point);
            }
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            if (this.pressedInBumps) {
                super.mouseDragged(mouseEvent);
            }
        }
    }
}
