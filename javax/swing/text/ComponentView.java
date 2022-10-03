package javax.swing.text;

import java.beans.PropertyChangeEvent;
import java.awt.KeyboardFocusManager;
import java.awt.AWTKeyStroke;
import java.util.Set;
import java.awt.LayoutManager;
import java.beans.PropertyChangeListener;
import java.awt.Container;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Graphics;
import java.awt.Component;

public class ComponentView extends View
{
    private Component createdC;
    private Invalidator c;
    
    public ComponentView(final Element element) {
        super(element);
    }
    
    protected Component createComponent() {
        return StyleConstants.getComponent(this.getElement().getAttributes());
    }
    
    public final Component getComponent() {
        return this.createdC;
    }
    
    @Override
    public void paint(final Graphics graphics, final Shape shape) {
        if (this.c != null) {
            final Rectangle rectangle = (Rectangle)((shape instanceof Rectangle) ? shape : shape.getBounds());
            this.c.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
    }
    
    @Override
    public float getPreferredSpan(final int n) {
        if (n != 0 && n != 1) {
            throw new IllegalArgumentException("Invalid axis: " + n);
        }
        if (this.c == null) {
            return 0.0f;
        }
        final Dimension preferredSize = this.c.getPreferredSize();
        if (n == 0) {
            return (float)preferredSize.width;
        }
        return (float)preferredSize.height;
    }
    
    @Override
    public float getMinimumSpan(final int n) {
        if (n != 0 && n != 1) {
            throw new IllegalArgumentException("Invalid axis: " + n);
        }
        if (this.c == null) {
            return 0.0f;
        }
        final Dimension minimumSize = this.c.getMinimumSize();
        if (n == 0) {
            return (float)minimumSize.width;
        }
        return (float)minimumSize.height;
    }
    
    @Override
    public float getMaximumSpan(final int n) {
        if (n != 0 && n != 1) {
            throw new IllegalArgumentException("Invalid axis: " + n);
        }
        if (this.c == null) {
            return 0.0f;
        }
        final Dimension maximumSize = this.c.getMaximumSize();
        if (n == 0) {
            return (float)maximumSize.width;
        }
        return (float)maximumSize.height;
    }
    
    @Override
    public float getAlignment(final int n) {
        if (this.c != null) {
            switch (n) {
                case 0: {
                    return this.c.getAlignmentX();
                }
                case 1: {
                    return this.c.getAlignmentY();
                }
            }
        }
        return super.getAlignment(n);
    }
    
    @Override
    public void setParent(final View parent) {
        super.setParent(parent);
        if (SwingUtilities.isEventDispatchThread()) {
            this.setComponentParent();
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    final Document document = ComponentView.this.getDocument();
                    try {
                        if (document instanceof AbstractDocument) {
                            ((AbstractDocument)document).readLock();
                        }
                        ComponentView.this.setComponentParent();
                        final Container container = ComponentView.this.getContainer();
                        if (container != null) {
                            ComponentView.this.preferenceChanged(null, true, true);
                            container.repaint();
                        }
                    }
                    finally {
                        if (document instanceof AbstractDocument) {
                            ((AbstractDocument)document).readUnlock();
                        }
                    }
                }
            });
        }
    }
    
    void setComponentParent() {
        if (this.getParent() != null) {
            final Container container = this.getContainer();
            if (container != null) {
                if (this.c == null) {
                    final Component component = this.createComponent();
                    if (component != null) {
                        this.createdC = component;
                        this.c = new Invalidator(component);
                    }
                }
                if (this.c != null && this.c.getParent() == null) {
                    container.add(this.c, this);
                    container.addPropertyChangeListener("enabled", this.c);
                }
            }
        }
        else if (this.c != null) {
            final Container parent = this.c.getParent();
            if (parent != null) {
                parent.remove(this.c);
                parent.removePropertyChangeListener("enabled", this.c);
            }
        }
    }
    
    @Override
    public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
        final int startOffset = this.getStartOffset();
        final int endOffset = this.getEndOffset();
        if (n >= startOffset && n <= endOffset) {
            final Rectangle bounds = shape.getBounds();
            if (n == endOffset) {
                final Rectangle rectangle = bounds;
                rectangle.x += bounds.width;
            }
            bounds.width = 0;
            return bounds;
        }
        throw new BadLocationException(n + " not in range " + startOffset + "," + endOffset, n);
    }
    
    @Override
    public int viewToModel(final float n, final float n2, final Shape shape, final Position.Bias[] array) {
        final Rectangle rectangle = (Rectangle)shape;
        if (n < rectangle.x + rectangle.width / 2) {
            array[0] = Position.Bias.Forward;
            return this.getStartOffset();
        }
        array[0] = Position.Bias.Backward;
        return this.getEndOffset();
    }
    
    class Invalidator extends Container implements PropertyChangeListener
    {
        Dimension min;
        Dimension pref;
        Dimension max;
        float yalign;
        float xalign;
        
        Invalidator(final Component component) {
            this.setLayout(null);
            this.add(component);
            this.cacheChildSizes();
        }
        
        @Override
        public void invalidate() {
            super.invalidate();
            if (this.getParent() != null) {
                ComponentView.this.preferenceChanged(null, true, true);
            }
        }
        
        @Override
        public void doLayout() {
            this.cacheChildSizes();
        }
        
        @Override
        public void setBounds(final int n, final int n2, final int n3, final int n4) {
            super.setBounds(n, n2, n3, n4);
            if (this.getComponentCount() > 0) {
                this.getComponent(0).setSize(n3, n4);
            }
            this.cacheChildSizes();
        }
        
        public void validateIfNecessary() {
            if (!this.isValid()) {
                this.validate();
            }
        }
        
        private void cacheChildSizes() {
            if (this.getComponentCount() > 0) {
                final Component component = this.getComponent(0);
                this.min = component.getMinimumSize();
                this.pref = component.getPreferredSize();
                this.max = component.getMaximumSize();
                this.yalign = component.getAlignmentY();
                this.xalign = component.getAlignmentX();
            }
            else {
                final Dimension min = new Dimension(0, 0);
                this.max = min;
                this.pref = min;
                this.min = min;
            }
        }
        
        @Override
        public void setVisible(final boolean b) {
            super.setVisible(b);
            if (this.getComponentCount() > 0) {
                this.getComponent(0).setVisible(b);
            }
        }
        
        @Override
        public boolean isShowing() {
            return true;
        }
        
        @Override
        public Dimension getMinimumSize() {
            this.validateIfNecessary();
            return this.min;
        }
        
        @Override
        public Dimension getPreferredSize() {
            this.validateIfNecessary();
            return this.pref;
        }
        
        @Override
        public Dimension getMaximumSize() {
            this.validateIfNecessary();
            return this.max;
        }
        
        @Override
        public float getAlignmentX() {
            this.validateIfNecessary();
            return this.xalign;
        }
        
        @Override
        public float getAlignmentY() {
            this.validateIfNecessary();
            return this.yalign;
        }
        
        @Override
        public Set<AWTKeyStroke> getFocusTraversalKeys(final int n) {
            return KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalKeys(n);
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final Boolean b = (Boolean)propertyChangeEvent.getNewValue();
            if (this.getComponentCount() > 0) {
                this.getComponent(0).setEnabled(b);
            }
        }
    }
}
