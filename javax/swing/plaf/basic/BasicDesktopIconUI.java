package javax.swing.plaf.basic;

import java.awt.Point;
import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import javax.swing.event.MouseInputAdapter;
import java.beans.PropertyVetoException;
import java.awt.Insets;
import javax.swing.border.Border;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.LookAndFeel;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.DesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JLayeredPane;
import javax.swing.DefaultDesktopManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.event.MouseInputListener;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.plaf.DesktopIconUI;

public class BasicDesktopIconUI extends DesktopIconUI
{
    protected JInternalFrame.JDesktopIcon desktopIcon;
    protected JInternalFrame frame;
    protected JComponent iconPane;
    MouseInputListener mouseInputListener;
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicDesktopIconUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.desktopIcon = (JInternalFrame.JDesktopIcon)component;
        this.frame = this.desktopIcon.getInternalFrame();
        this.installDefaults();
        this.installComponents();
        final JInternalFrame internalFrame = this.desktopIcon.getInternalFrame();
        if (internalFrame.isIcon() && internalFrame.getParent() == null) {
            final JDesktopPane desktopPane = this.desktopIcon.getDesktopPane();
            if (desktopPane != null) {
                final DesktopManager desktopManager = desktopPane.getDesktopManager();
                if (desktopManager instanceof DefaultDesktopManager) {
                    desktopManager.iconifyFrame(internalFrame);
                }
            }
        }
        this.installListeners();
        JLayeredPane.putLayer(this.desktopIcon, JLayeredPane.getLayer(this.frame));
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallDefaults();
        this.uninstallComponents();
        final JInternalFrame internalFrame = this.desktopIcon.getInternalFrame();
        if (internalFrame.isIcon()) {
            final JDesktopPane desktopPane = this.desktopIcon.getDesktopPane();
            if (desktopPane != null && desktopPane.getDesktopManager() instanceof DefaultDesktopManager) {
                internalFrame.putClientProperty("wasIconOnce", null);
                this.desktopIcon.setLocation(Integer.MIN_VALUE, 0);
            }
        }
        this.uninstallListeners();
        this.frame = null;
        this.desktopIcon = null;
    }
    
    protected void installComponents() {
        this.iconPane = new BasicInternalFrameTitlePane(this.frame);
        this.desktopIcon.setLayout(new BorderLayout());
        this.desktopIcon.add(this.iconPane, "Center");
    }
    
    protected void uninstallComponents() {
        this.desktopIcon.remove(this.iconPane);
        this.desktopIcon.setLayout(null);
        this.iconPane = null;
    }
    
    protected void installListeners() {
        this.mouseInputListener = this.createMouseInputListener();
        this.desktopIcon.addMouseMotionListener(this.mouseInputListener);
        this.desktopIcon.addMouseListener(this.mouseInputListener);
    }
    
    protected void uninstallListeners() {
        this.desktopIcon.removeMouseMotionListener(this.mouseInputListener);
        this.desktopIcon.removeMouseListener(this.mouseInputListener);
        this.mouseInputListener = null;
    }
    
    protected void installDefaults() {
        LookAndFeel.installBorder(this.desktopIcon, "DesktopIcon.border");
        LookAndFeel.installProperty(this.desktopIcon, "opaque", Boolean.TRUE);
    }
    
    protected void uninstallDefaults() {
        LookAndFeel.uninstallBorder(this.desktopIcon);
    }
    
    protected MouseInputListener createMouseInputListener() {
        return new MouseInputHandler();
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        return this.desktopIcon.getLayout().preferredLayoutSize(this.desktopIcon);
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        final Dimension dimension = new Dimension(this.iconPane.getMinimumSize());
        final Border border = this.frame.getBorder();
        if (border != null) {
            final Dimension dimension2 = dimension;
            dimension2.height += border.getBorderInsets(this.frame).bottom + border.getBorderInsets(this.frame).top;
        }
        return dimension;
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        return this.iconPane.getMaximumSize();
    }
    
    public Insets getInsets(final JComponent component) {
        final JInternalFrame internalFrame = this.desktopIcon.getInternalFrame();
        final Border border = internalFrame.getBorder();
        if (border != null) {
            return border.getBorderInsets(internalFrame);
        }
        return new Insets(0, 0, 0, 0);
    }
    
    public void deiconize() {
        try {
            this.frame.setIcon(false);
        }
        catch (final PropertyVetoException ex) {}
    }
    
    public class MouseInputHandler extends MouseInputAdapter
    {
        int _x;
        int _y;
        int __x;
        int __y;
        Rectangle startingBounds;
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            this._x = 0;
            this._y = 0;
            this.__x = 0;
            this.__y = 0;
            this.startingBounds = null;
            final JDesktopPane desktopPane;
            if ((desktopPane = BasicDesktopIconUI.this.desktopIcon.getDesktopPane()) != null) {
                desktopPane.getDesktopManager().endDraggingFrame(BasicDesktopIconUI.this.desktopIcon);
            }
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            final Point convertPoint = SwingUtilities.convertPoint((Component)mouseEvent.getSource(), mouseEvent.getX(), mouseEvent.getY(), null);
            this.__x = mouseEvent.getX();
            this.__y = mouseEvent.getY();
            this._x = convertPoint.x;
            this._y = convertPoint.y;
            this.startingBounds = BasicDesktopIconUI.this.desktopIcon.getBounds();
            final JDesktopPane desktopPane;
            if ((desktopPane = BasicDesktopIconUI.this.desktopIcon.getDesktopPane()) != null) {
                desktopPane.getDesktopManager().beginDraggingFrame(BasicDesktopIconUI.this.desktopIcon);
            }
            try {
                BasicDesktopIconUI.this.frame.setSelected(true);
            }
            catch (final PropertyVetoException ex) {}
            if (BasicDesktopIconUI.this.desktopIcon.getParent() instanceof JLayeredPane) {
                ((JLayeredPane)BasicDesktopIconUI.this.desktopIcon.getParent()).moveToFront(BasicDesktopIconUI.this.desktopIcon);
            }
            if (mouseEvent.getClickCount() > 1 && BasicDesktopIconUI.this.frame.isIconifiable() && BasicDesktopIconUI.this.frame.isIcon()) {
                BasicDesktopIconUI.this.deiconize();
            }
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            final Point convertPoint = SwingUtilities.convertPoint((Component)mouseEvent.getSource(), mouseEvent.getX(), mouseEvent.getY(), null);
            final Insets insets = BasicDesktopIconUI.this.desktopIcon.getInsets();
            final int width = ((JComponent)BasicDesktopIconUI.this.desktopIcon.getParent()).getWidth();
            final int height = ((JComponent)BasicDesktopIconUI.this.desktopIcon.getParent()).getHeight();
            if (this.startingBounds == null) {
                return;
            }
            int n = this.startingBounds.x - (this._x - convertPoint.x);
            int n2 = this.startingBounds.y - (this._y - convertPoint.y);
            if (n + insets.left <= -this.__x) {
                n = -this.__x - insets.left;
            }
            if (n2 + insets.top <= -this.__y) {
                n2 = -this.__y - insets.top;
            }
            if (n + this.__x + insets.right > width) {
                n = width - this.__x - insets.right;
            }
            if (n2 + this.__y + insets.bottom > height) {
                n2 = height - this.__y - insets.bottom;
            }
            final JDesktopPane desktopPane;
            if ((desktopPane = BasicDesktopIconUI.this.desktopIcon.getDesktopPane()) != null) {
                desktopPane.getDesktopManager().dragFrame(BasicDesktopIconUI.this.desktopIcon, n, n2);
            }
            else {
                this.moveAndRepaint(BasicDesktopIconUI.this.desktopIcon, n, n2, BasicDesktopIconUI.this.desktopIcon.getWidth(), BasicDesktopIconUI.this.desktopIcon.getHeight());
            }
        }
        
        public void moveAndRepaint(final JComponent component, final int n, final int n2, final int n3, final int n4) {
            final Rectangle bounds = component.getBounds();
            component.setBounds(n, n2, n3, n4);
            SwingUtilities.computeUnion(n, n2, n3, n4, bounds);
            component.getParent().repaint(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }
}
