package javax.swing.event;

import javax.swing.tree.ExpandVetoException;
import java.util.EventListener;

public interface TreeWillExpandListener extends EventListener
{
    void treeWillExpand(final TreeExpansionEvent p0) throws ExpandVetoException;
    
    void treeWillCollapse(final TreeExpansionEvent p0) throws ExpandVetoException;
}
