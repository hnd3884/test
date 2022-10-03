package javax.swing.event;

import javax.swing.tree.TreePath;
import java.util.EventObject;

public class TreeExpansionEvent extends EventObject
{
    protected TreePath path;
    
    public TreeExpansionEvent(final Object o, final TreePath path) {
        super(o);
        this.path = path;
    }
    
    public TreePath getPath() {
        return this.path;
    }
}
