package javax.swing.tree;

import javax.swing.event.TreeExpansionEvent;

public class ExpandVetoException extends Exception
{
    protected TreeExpansionEvent event;
    
    public ExpandVetoException(final TreeExpansionEvent treeExpansionEvent) {
        this(treeExpansionEvent, null);
    }
    
    public ExpandVetoException(final TreeExpansionEvent event, final String s) {
        super(s);
        this.event = event;
    }
}
