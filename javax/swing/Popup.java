package javax.swing;

import java.awt.Frame;
import java.awt.Graphics;
import sun.awt.ModalExclude;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.Component;

public class Popup
{
    private Component component;
    
    protected Popup(final Component component, final Component component2, final int n, final int n2) {
        this();
        if (component2 == null) {
            throw new IllegalArgumentException("Contents must be non-null");
        }
        this.reset(component, component2, n, n2);
    }
    
    protected Popup() {
    }
    
    public void show() {
        final Component component = this.getComponent();
        if (component != null) {
            component.show();
        }
    }
    
    public void hide() {
        final Component component = this.getComponent();
        if (component instanceof JWindow) {
            component.hide();
            ((JWindow)component).getContentPane().removeAll();
        }
        this.dispose();
    }
    
    void dispose() {
        final Component component = this.getComponent();
        final Window windowAncestor = SwingUtilities.getWindowAncestor(component);
        if (component instanceof JWindow) {
            ((Window)component).dispose();
        }
        if (windowAncestor instanceof DefaultFrame) {
            windowAncestor.dispose();
        }
    }
    
    void reset(final Component component, final Component component2, final int n, final int n2) {
        if (this.getComponent() == null) {
            this.component = this.createComponent(component);
        }
        if (this.getComponent() instanceof JWindow) {
            final JWindow window = (JWindow)this.getComponent();
            window.setLocation(n, n2);
            window.getContentPane().add(component2, "Center");
            window.invalidate();
            window.validate();
            if (window.isVisible()) {
                this.pack();
            }
        }
    }
    
    void pack() {
        final Component component = this.getComponent();
        if (component instanceof Window) {
            ((Window)component).pack();
        }
    }
    
    private Window getParentWindow(final Component component) {
        Component windowAncestor = null;
        if (component instanceof Window) {
            windowAncestor = component;
        }
        else if (component != null) {
            windowAncestor = SwingUtilities.getWindowAncestor(component);
        }
        if (windowAncestor == null) {
            windowAncestor = new DefaultFrame();
        }
        return (Window)windowAncestor;
    }
    
    Component createComponent(final Component component) {
        if (GraphicsEnvironment.isHeadless()) {
            return null;
        }
        return new HeavyWeightWindow(this.getParentWindow(component));
    }
    
    Component getComponent() {
        return this.component;
    }
    
    static class HeavyWeightWindow extends JWindow implements ModalExclude
    {
        HeavyWeightWindow(final Window window) {
            super(window);
            this.setFocusableWindowState(false);
            this.setType(Type.POPUP);
            this.getRootPane().setUseTrueDoubleBuffering(false);
            try {
                this.setAlwaysOnTop(true);
            }
            catch (final SecurityException ex) {}
        }
        
        @Override
        public void update(final Graphics graphics) {
            this.paint(graphics);
        }
        
        @Override
        public void show() {
            this.pack();
            if (this.getWidth() > 0 && this.getHeight() > 0) {
                super.show();
            }
        }
    }
    
    static class DefaultFrame extends Frame
    {
    }
}
