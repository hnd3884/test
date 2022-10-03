package javax.swing.event;

import java.util.EventListener;

public interface TreeModelListener extends EventListener
{
    void treeNodesChanged(final TreeModelEvent p0);
    
    void treeNodesInserted(final TreeModelEvent p0);
    
    void treeNodesRemoved(final TreeModelEvent p0);
    
    void treeStructureChanged(final TreeModelEvent p0);
}
