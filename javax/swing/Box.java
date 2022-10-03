package javax.swing;

import javax.accessibility.AccessibleRole;
import java.beans.ConstructorProperties;
import javax.accessibility.AccessibleContext;
import java.awt.Graphics;
import java.awt.AWTError;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Container;
import javax.accessibility.Accessible;

public class Box extends JComponent implements Accessible
{
    public Box(final int n) {
        super.setLayout(new BoxLayout(this, n));
    }
    
    public static Box createHorizontalBox() {
        return new Box(0);
    }
    
    public static Box createVerticalBox() {
        return new Box(1);
    }
    
    public static Component createRigidArea(final Dimension dimension) {
        return new Filler(dimension, dimension, dimension);
    }
    
    public static Component createHorizontalStrut(final int n) {
        return new Filler(new Dimension(n, 0), new Dimension(n, 0), new Dimension(n, 32767));
    }
    
    public static Component createVerticalStrut(final int n) {
        return new Filler(new Dimension(0, n), new Dimension(0, n), new Dimension(32767, n));
    }
    
    public static Component createGlue() {
        return new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 32767));
    }
    
    public static Component createHorizontalGlue() {
        return new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
    }
    
    public static Component createVerticalGlue() {
        return new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
    }
    
    @Override
    public void setLayout(final LayoutManager layoutManager) {
        throw new AWTError("Illegal request");
    }
    
    @Override
    protected void paintComponent(final Graphics graphics) {
        if (this.ui != null) {
            super.paintComponent(graphics);
        }
        else if (this.isOpaque()) {
            graphics.setColor(this.getBackground());
            graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleBox();
        }
        return this.accessibleContext;
    }
    
    public static class Filler extends JComponent implements Accessible
    {
        @ConstructorProperties({ "minimumSize", "preferredSize", "maximumSize" })
        public Filler(final Dimension minimumSize, final Dimension preferredSize, final Dimension maximumSize) {
            this.setMinimumSize(minimumSize);
            this.setPreferredSize(preferredSize);
            this.setMaximumSize(maximumSize);
        }
        
        public void changeShape(final Dimension minimumSize, final Dimension preferredSize, final Dimension maximumSize) {
            this.setMinimumSize(minimumSize);
            this.setPreferredSize(preferredSize);
            this.setMaximumSize(maximumSize);
            this.revalidate();
        }
        
        @Override
        protected void paintComponent(final Graphics graphics) {
            if (this.ui != null) {
                super.paintComponent(graphics);
            }
            else if (this.isOpaque()) {
                graphics.setColor(this.getBackground());
                graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
        }
        
        @Override
        public AccessibleContext getAccessibleContext() {
            if (this.accessibleContext == null) {
                this.accessibleContext = new AccessibleBoxFiller();
            }
            return this.accessibleContext;
        }
        
        protected class AccessibleBoxFiller extends AccessibleAWTComponent
        {
            @Override
            public AccessibleRole getAccessibleRole() {
                return AccessibleRole.FILLER;
            }
        }
    }
    
    protected class AccessibleBox extends AccessibleAWTContainer
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.FILLER;
        }
    }
}
