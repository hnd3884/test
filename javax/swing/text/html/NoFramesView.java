package javax.swing.text.html;

import javax.swing.text.View;
import java.awt.Container;
import javax.swing.text.JTextComponent;
import java.awt.Shape;
import java.awt.Graphics;
import javax.swing.text.Element;

class NoFramesView extends BlockView
{
    boolean visible;
    
    public NoFramesView(final Element element, final int n) {
        super(element, n);
        this.visible = false;
    }
    
    @Override
    public void paint(final Graphics graphics, final Shape shape) {
        final Container container = this.getContainer();
        if (container != null && this.visible != ((JTextComponent)container).isEditable()) {
            this.visible = ((JTextComponent)container).isEditable();
        }
        if (!this.isVisible()) {
            return;
        }
        super.paint(graphics, shape);
    }
    
    @Override
    public void setParent(final View parent) {
        if (parent != null) {
            final Container container = parent.getContainer();
            if (container != null) {
                this.visible = ((JTextComponent)container).isEditable();
            }
        }
        super.setParent(parent);
    }
    
    @Override
    public boolean isVisible() {
        return this.visible;
    }
    
    @Override
    protected void layout(final int n, final int n2) {
        if (!this.isVisible()) {
            return;
        }
        super.layout(n, n2);
    }
    
    @Override
    public float getPreferredSpan(final int n) {
        if (!this.visible) {
            return 0.0f;
        }
        return super.getPreferredSpan(n);
    }
    
    @Override
    public float getMinimumSpan(final int n) {
        if (!this.visible) {
            return 0.0f;
        }
        return super.getMinimumSpan(n);
    }
    
    @Override
    public float getMaximumSpan(final int n) {
        if (!this.visible) {
            return 0.0f;
        }
        return super.getMaximumSpan(n);
    }
}
