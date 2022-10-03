package javax.swing;

import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;
import java.awt.Toolkit;
import java.awt.Dimension;
import sun.java2d.SunGraphics2D;
import java.awt.Color;
import java.awt.Window;
import com.sun.awt.AWTUtilities;
import java.awt.Container;
import java.beans.PropertyVetoException;
import java.awt.Component;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;

public class DefaultDesktopManager implements DesktopManager, Serializable
{
    static final String HAS_BEEN_ICONIFIED_PROPERTY = "wasIconOnce";
    static final int DEFAULT_DRAG_MODE = 0;
    static final int OUTLINE_DRAG_MODE = 1;
    static final int FASTER_DRAG_MODE = 2;
    int dragMode;
    private transient Rectangle currentBounds;
    private transient Graphics desktopGraphics;
    private transient Rectangle desktopBounds;
    private transient Rectangle[] floatingItems;
    private transient boolean didDrag;
    private transient Point currentLoc;
    
    public DefaultDesktopManager() {
        this.dragMode = 0;
        this.currentBounds = null;
        this.desktopGraphics = null;
        this.desktopBounds = null;
        this.floatingItems = new Rectangle[0];
        this.currentLoc = null;
    }
    
    @Override
    public void openFrame(final JInternalFrame internalFrame) {
        if (internalFrame.getDesktopIcon().getParent() != null) {
            internalFrame.getDesktopIcon().getParent().add(internalFrame);
            this.removeIconFor(internalFrame);
        }
    }
    
    @Override
    public void closeFrame(final JInternalFrame internalFrame) {
        final JDesktopPane desktopPane = internalFrame.getDesktopPane();
        if (desktopPane == null) {
            return;
        }
        final boolean selected = internalFrame.isSelected();
        final Container parent = internalFrame.getParent();
        JInternalFrame nextFrame = null;
        if (selected) {
            nextFrame = desktopPane.getNextFrame(internalFrame);
            try {
                internalFrame.setSelected(false);
            }
            catch (final PropertyVetoException ex) {}
        }
        if (parent != null) {
            parent.remove(internalFrame);
            parent.repaint(internalFrame.getX(), internalFrame.getY(), internalFrame.getWidth(), internalFrame.getHeight());
        }
        this.removeIconFor(internalFrame);
        if (internalFrame.getNormalBounds() != null) {
            internalFrame.setNormalBounds(null);
        }
        if (this.wasIcon(internalFrame)) {
            this.setWasIcon(internalFrame, null);
        }
        if (nextFrame != null) {
            try {
                nextFrame.setSelected(true);
            }
            catch (final PropertyVetoException ex2) {}
        }
        else if (selected && desktopPane.getComponentCount() == 0) {
            desktopPane.requestFocus();
        }
    }
    
    @Override
    public void maximizeFrame(final JInternalFrame internalFrame) {
        if (internalFrame.isIcon()) {
            try {
                internalFrame.setIcon(false);
            }
            catch (final PropertyVetoException ex) {}
        }
        else {
            internalFrame.setNormalBounds(internalFrame.getBounds());
            final Rectangle bounds = internalFrame.getParent().getBounds();
            this.setBoundsForFrame(internalFrame, 0, 0, bounds.width, bounds.height);
        }
        try {
            internalFrame.setSelected(true);
        }
        catch (final PropertyVetoException ex2) {}
    }
    
    @Override
    public void minimizeFrame(final JInternalFrame internalFrame) {
        if (internalFrame.isIcon()) {
            this.iconifyFrame(internalFrame);
            return;
        }
        if (internalFrame.getNormalBounds() != null) {
            final Rectangle normalBounds = internalFrame.getNormalBounds();
            internalFrame.setNormalBounds(null);
            try {
                internalFrame.setSelected(true);
            }
            catch (final PropertyVetoException ex) {}
            this.setBoundsForFrame(internalFrame, normalBounds.x, normalBounds.y, normalBounds.width, normalBounds.height);
        }
    }
    
    @Override
    public void iconifyFrame(final JInternalFrame internalFrame) {
        final Container parent = internalFrame.getParent();
        final JDesktopPane desktopPane = internalFrame.getDesktopPane();
        final boolean selected = internalFrame.isSelected();
        final JInternalFrame.JDesktopIcon desktopIcon = internalFrame.getDesktopIcon();
        if (!this.wasIcon(internalFrame)) {
            final Rectangle boundsForIcon = this.getBoundsForIconOf(internalFrame);
            desktopIcon.setBounds(boundsForIcon.x, boundsForIcon.y, boundsForIcon.width, boundsForIcon.height);
            desktopIcon.revalidate();
            this.setWasIcon(internalFrame, Boolean.TRUE);
        }
        if (parent == null || desktopPane == null) {
            return;
        }
        if (parent instanceof JLayeredPane) {
            final JLayeredPane layeredPane = (JLayeredPane)parent;
            JLayeredPane.putLayer(desktopIcon, JLayeredPane.getLayer(internalFrame));
        }
        if (!internalFrame.isMaximum()) {
            internalFrame.setNormalBounds(internalFrame.getBounds());
        }
        desktopPane.setComponentOrderCheckingEnabled(false);
        parent.remove(internalFrame);
        parent.add(desktopIcon);
        desktopPane.setComponentOrderCheckingEnabled(true);
        parent.repaint(internalFrame.getX(), internalFrame.getY(), internalFrame.getWidth(), internalFrame.getHeight());
        if (selected && desktopPane.selectFrame(true) == null) {
            internalFrame.restoreSubcomponentFocus();
        }
    }
    
    @Override
    public void deiconifyFrame(final JInternalFrame internalFrame) {
        final Container parent = internalFrame.getDesktopIcon().getParent();
        final JDesktopPane desktopPane = internalFrame.getDesktopPane();
        if (parent != null && desktopPane != null) {
            parent.add(internalFrame);
            if (internalFrame.isMaximum()) {
                final Rectangle bounds = parent.getBounds();
                if (internalFrame.getWidth() != bounds.width || internalFrame.getHeight() != bounds.height) {
                    this.setBoundsForFrame(internalFrame, 0, 0, bounds.width, bounds.height);
                }
            }
            this.removeIconFor(internalFrame);
            if (internalFrame.isSelected()) {
                internalFrame.moveToFront();
                internalFrame.restoreSubcomponentFocus();
            }
            else {
                try {
                    internalFrame.setSelected(true);
                }
                catch (final PropertyVetoException ex) {}
            }
        }
    }
    
    @Override
    public void activateFrame(final JInternalFrame internalFrame) {
        final Container parent = internalFrame.getParent();
        final JDesktopPane desktopPane = internalFrame.getDesktopPane();
        final JInternalFrame internalFrame2 = (desktopPane == null) ? null : desktopPane.getSelectedFrame();
        if (parent == null && internalFrame.getDesktopIcon().getParent() == null) {
            return;
        }
        if (internalFrame2 == null) {
            if (desktopPane != null) {
                desktopPane.setSelectedFrame(internalFrame);
            }
        }
        else if (internalFrame2 != internalFrame) {
            if (internalFrame2.isSelected()) {
                try {
                    internalFrame2.setSelected(false);
                }
                catch (final PropertyVetoException ex) {}
            }
            if (desktopPane != null) {
                desktopPane.setSelectedFrame(internalFrame);
            }
        }
        internalFrame.moveToFront();
    }
    
    @Override
    public void deactivateFrame(final JInternalFrame internalFrame) {
        final JDesktopPane desktopPane = internalFrame.getDesktopPane();
        if (((desktopPane == null) ? null : desktopPane.getSelectedFrame()) == internalFrame) {
            desktopPane.setSelectedFrame(null);
        }
    }
    
    @Override
    public void beginDraggingFrame(final JComponent component) {
        this.setupDragMode(component);
        if (this.dragMode == 2) {
            final Container parent = component.getParent();
            this.floatingItems = this.findFloatingItems(component);
            this.currentBounds = component.getBounds();
            if (parent instanceof JComponent) {
                this.desktopBounds = ((JComponent)parent).getVisibleRect();
            }
            else {
                this.desktopBounds = parent.getBounds();
                final Rectangle desktopBounds = this.desktopBounds;
                final Rectangle desktopBounds2 = this.desktopBounds;
                final int n = 0;
                desktopBounds2.y = n;
                desktopBounds.x = n;
            }
            this.desktopGraphics = JComponent.safelyGetGraphics(parent);
            ((JInternalFrame)component).isDragging = true;
            this.didDrag = false;
        }
    }
    
    private void setupDragMode(final JComponent component) {
        final JDesktopPane desktopPane = this.getDesktopPane(component);
        final Container parent = component.getParent();
        this.dragMode = 0;
        if (desktopPane != null) {
            final String s = (String)desktopPane.getClientProperty("JDesktopPane.dragMode");
            final Window windowAncestor = SwingUtilities.getWindowAncestor(component);
            if (windowAncestor != null && !AWTUtilities.isWindowOpaque(windowAncestor)) {
                this.dragMode = 0;
            }
            else if (s != null && s.equals("outline")) {
                this.dragMode = 1;
            }
            else if (s != null && s.equals("faster") && component instanceof JInternalFrame && component.isOpaque() && (parent == null || parent.isOpaque())) {
                this.dragMode = 2;
            }
            else if (desktopPane.getDragMode() == 1) {
                this.dragMode = 1;
            }
            else if (desktopPane.getDragMode() == 0 && component instanceof JInternalFrame && component.isOpaque()) {
                this.dragMode = 2;
            }
            else {
                this.dragMode = 0;
            }
        }
    }
    
    @Override
    public void dragFrame(final JComponent component, final int n, final int n2) {
        if (this.dragMode == 1) {
            final JDesktopPane desktopPane = this.getDesktopPane(component);
            if (desktopPane != null) {
                final Graphics safelyGetGraphics = JComponent.safelyGetGraphics(desktopPane);
                safelyGetGraphics.setXORMode(Color.white);
                if (this.currentLoc != null) {
                    safelyGetGraphics.drawRect(this.currentLoc.x, this.currentLoc.y, component.getWidth() - 1, component.getHeight() - 1);
                }
                safelyGetGraphics.drawRect(n, n2, component.getWidth() - 1, component.getHeight() - 1);
                if (!((SunGraphics2D)safelyGetGraphics).getSurfaceData().isSurfaceLost()) {
                    this.currentLoc = new Point(n, n2);
                }
                safelyGetGraphics.dispose();
            }
        }
        else if (this.dragMode == 2) {
            this.dragFrameFaster(component, n, n2);
        }
        else {
            this.setBoundsForFrame(component, n, n2, component.getWidth(), component.getHeight());
        }
    }
    
    @Override
    public void endDraggingFrame(final JComponent component) {
        if (this.dragMode == 1 && this.currentLoc != null) {
            this.setBoundsForFrame(component, this.currentLoc.x, this.currentLoc.y, component.getWidth(), component.getHeight());
            this.currentLoc = null;
        }
        else if (this.dragMode == 2) {
            this.currentBounds = null;
            if (this.desktopGraphics != null) {
                this.desktopGraphics.dispose();
                this.desktopGraphics = null;
            }
            this.desktopBounds = null;
            ((JInternalFrame)component).isDragging = false;
        }
    }
    
    @Override
    public void beginResizingFrame(final JComponent component, final int n) {
        this.setupDragMode(component);
    }
    
    @Override
    public void resizeFrame(final JComponent component, final int n, final int n2, final int n3, final int n4) {
        if (this.dragMode == 0 || this.dragMode == 2) {
            this.setBoundsForFrame(component, n, n2, n3, n4);
        }
        else {
            final JDesktopPane desktopPane = this.getDesktopPane(component);
            if (desktopPane != null) {
                final Graphics safelyGetGraphics = JComponent.safelyGetGraphics(desktopPane);
                safelyGetGraphics.setXORMode(Color.white);
                if (this.currentBounds != null) {
                    safelyGetGraphics.drawRect(this.currentBounds.x, this.currentBounds.y, this.currentBounds.width - 1, this.currentBounds.height - 1);
                }
                safelyGetGraphics.drawRect(n, n2, n3 - 1, n4 - 1);
                if (!((SunGraphics2D)safelyGetGraphics).getSurfaceData().isSurfaceLost()) {
                    this.currentBounds = new Rectangle(n, n2, n3, n4);
                }
                safelyGetGraphics.setPaintMode();
                safelyGetGraphics.dispose();
            }
        }
    }
    
    @Override
    public void endResizingFrame(final JComponent component) {
        if (this.dragMode == 1 && this.currentBounds != null) {
            this.setBoundsForFrame(component, this.currentBounds.x, this.currentBounds.y, this.currentBounds.width, this.currentBounds.height);
            this.currentBounds = null;
        }
    }
    
    @Override
    public void setBoundsForFrame(final JComponent component, final int n, final int n2, final int n3, final int n4) {
        component.setBounds(n, n2, n3, n4);
        component.revalidate();
    }
    
    protected void removeIconFor(final JInternalFrame internalFrame) {
        final JInternalFrame.JDesktopIcon desktopIcon = internalFrame.getDesktopIcon();
        final Container parent = desktopIcon.getParent();
        if (parent != null) {
            parent.remove(desktopIcon);
            parent.repaint(desktopIcon.getX(), desktopIcon.getY(), desktopIcon.getWidth(), desktopIcon.getHeight());
        }
    }
    
    protected Rectangle getBoundsForIconOf(final JInternalFrame internalFrame) {
        final JInternalFrame.JDesktopIcon desktopIcon = internalFrame.getDesktopIcon();
        final Dimension preferredSize = desktopIcon.getPreferredSize();
        Container container = internalFrame.getParent();
        if (container == null) {
            container = internalFrame.getDesktopIcon().getParent();
        }
        if (container == null) {
            return new Rectangle(0, 0, preferredSize.width, preferredSize.height);
        }
        final Rectangle bounds = container.getBounds();
        final Component[] components = container.getComponents();
        Rectangle rectangle = null;
        Component desktopIcon2 = null;
        int n = 0;
        int n2 = bounds.height - preferredSize.height;
        final int width = preferredSize.width;
        final int height = preferredSize.height;
        int i = 0;
        while (i == 0) {
            rectangle = new Rectangle(n, n2, width, height);
            i = 1;
            for (int j = 0; j < components.length; ++j) {
                if (components[j] instanceof JInternalFrame) {
                    desktopIcon2 = ((JInternalFrame)components[j]).getDesktopIcon();
                }
                else {
                    if (!(components[j] instanceof JInternalFrame.JDesktopIcon)) {
                        continue;
                    }
                    desktopIcon2 = components[j];
                }
                if (!desktopIcon2.equals(desktopIcon) && rectangle.intersects(desktopIcon2.getBounds())) {
                    i = 0;
                    break;
                }
            }
            if (desktopIcon2 == null) {
                return rectangle;
            }
            n += desktopIcon2.getBounds().width;
            if (n + width <= bounds.width) {
                continue;
            }
            n = 0;
            n2 -= height;
        }
        return rectangle;
    }
    
    protected void setPreviousBounds(final JInternalFrame internalFrame, final Rectangle normalBounds) {
        internalFrame.setNormalBounds(normalBounds);
    }
    
    protected Rectangle getPreviousBounds(final JInternalFrame internalFrame) {
        return internalFrame.getNormalBounds();
    }
    
    protected void setWasIcon(final JInternalFrame internalFrame, final Boolean b) {
        if (b != null) {
            internalFrame.putClientProperty("wasIconOnce", b);
        }
    }
    
    protected boolean wasIcon(final JInternalFrame internalFrame) {
        return internalFrame.getClientProperty("wasIconOnce") == Boolean.TRUE;
    }
    
    JDesktopPane getDesktopPane(final JComponent component) {
        JDesktopPane desktopPane = null;
        Container container = component.getParent();
        while (desktopPane == null) {
            if (container instanceof JDesktopPane) {
                desktopPane = (JDesktopPane)container;
            }
            else {
                if (container == null) {
                    break;
                }
                container = container.getParent();
            }
        }
        return desktopPane;
    }
    
    private void dragFrameFaster(final JComponent component, final int x, final int y) {
        final Rectangle rectangle = new Rectangle(this.currentBounds.x, this.currentBounds.y, this.currentBounds.width, this.currentBounds.height);
        this.currentBounds.x = x;
        this.currentBounds.y = y;
        if (this.didDrag) {
            this.emergencyCleanup(component);
        }
        else {
            this.didDrag = true;
            ((JInternalFrame)component).danger = false;
        }
        final boolean floaterCollision = this.isFloaterCollision(rectangle, this.currentBounds);
        final JComponent component2 = (JComponent)component.getParent();
        final Rectangle intersection = rectangle.intersection(this.desktopBounds);
        final RepaintManager currentManager = RepaintManager.currentManager(component);
        currentManager.beginPaint();
        try {
            if (!floaterCollision) {
                currentManager.copyArea(component2, this.desktopGraphics, intersection.x, intersection.y, intersection.width, intersection.height, x - rectangle.x, y - rectangle.y, true);
            }
            component.setBounds(this.currentBounds);
            if (!floaterCollision) {
                final Rectangle currentBounds = this.currentBounds;
                currentManager.notifyRepaintPerformed(component2, currentBounds.x, currentBounds.y, currentBounds.width, currentBounds.height);
            }
            if (floaterCollision) {
                ((JInternalFrame)component).isDragging = false;
                component2.paintImmediately(this.currentBounds);
                ((JInternalFrame)component).isDragging = true;
            }
            currentManager.markCompletelyClean(component2);
            currentManager.markCompletelyClean(component);
            Rectangle[] computeDifference;
            if (rectangle.intersects(this.currentBounds)) {
                computeDifference = SwingUtilities.computeDifference(rectangle, this.currentBounds);
            }
            else {
                computeDifference = new Rectangle[] { rectangle };
            }
            for (int i = 0; i < computeDifference.length; ++i) {
                component2.paintImmediately(computeDifference[i]);
                final Rectangle rectangle2 = computeDifference[i];
                currentManager.notifyRepaintPerformed(component2, rectangle2.x, rectangle2.y, rectangle2.width, rectangle2.height);
            }
            if (!intersection.equals(rectangle)) {
                final Rectangle[] computeDifference2 = SwingUtilities.computeDifference(rectangle, this.desktopBounds);
                for (int j = 0; j < computeDifference2.length; ++j) {
                    final Rectangle rectangle3 = computeDifference2[j];
                    rectangle3.x += x - rectangle.x;
                    final Rectangle rectangle4 = computeDifference2[j];
                    rectangle4.y += y - rectangle.y;
                    ((JInternalFrame)component).isDragging = false;
                    component2.paintImmediately(computeDifference2[j]);
                    ((JInternalFrame)component).isDragging = true;
                    final Rectangle rectangle5 = computeDifference2[j];
                    currentManager.notifyRepaintPerformed(component2, rectangle5.x, rectangle5.y, rectangle5.width, rectangle5.height);
                }
            }
        }
        finally {
            currentManager.endPaint();
        }
        final Window windowAncestor = SwingUtilities.getWindowAncestor(component);
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (!windowAncestor.isOpaque() && defaultToolkit instanceof SunToolkit && ((SunToolkit)defaultToolkit).needUpdateWindow()) {
            AWTAccessor.getWindowAccessor().updateWindow(windowAncestor);
        }
    }
    
    private boolean isFloaterCollision(final Rectangle rectangle, final Rectangle rectangle2) {
        if (this.floatingItems.length == 0) {
            return false;
        }
        for (int i = 0; i < this.floatingItems.length; ++i) {
            if (rectangle.intersects(this.floatingItems[i])) {
                return true;
            }
            if (rectangle2.intersects(this.floatingItems[i])) {
                return true;
            }
        }
        return false;
    }
    
    private Rectangle[] findFloatingItems(final JComponent component) {
        Component[] components;
        int n;
        for (components = component.getParent().getComponents(), n = 0; n < components.length && components[n] != component; ++n) {}
        final Rectangle[] array = new Rectangle[n];
        for (int i = 0; i < array.length; ++i) {
            array[i] = components[i].getBounds();
        }
        return array;
    }
    
    private void emergencyCleanup(final JComponent component) {
        if (((JInternalFrame)component).danger) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ((JInternalFrame)component).isDragging = false;
                    component.paintImmediately(0, 0, component.getWidth(), component.getHeight());
                    ((JInternalFrame)component).isDragging = true;
                }
            });
            ((JInternalFrame)component).danger = false;
        }
    }
}
