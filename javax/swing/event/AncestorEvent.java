package javax.swing.event;

import javax.swing.JComponent;
import java.awt.Container;
import java.awt.AWTEvent;

public class AncestorEvent extends AWTEvent
{
    public static final int ANCESTOR_ADDED = 1;
    public static final int ANCESTOR_REMOVED = 2;
    public static final int ANCESTOR_MOVED = 3;
    Container ancestor;
    Container ancestorParent;
    
    public AncestorEvent(final JComponent component, final int n, final Container ancestor, final Container ancestorParent) {
        super(component, n);
        this.ancestor = ancestor;
        this.ancestorParent = ancestorParent;
    }
    
    public Container getAncestor() {
        return this.ancestor;
    }
    
    public Container getAncestorParent() {
        return this.ancestorParent;
    }
    
    public JComponent getComponent() {
        return (JComponent)this.getSource();
    }
}
