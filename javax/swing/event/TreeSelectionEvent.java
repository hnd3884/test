package javax.swing.event;

import javax.swing.tree.TreePath;
import java.util.EventObject;

public class TreeSelectionEvent extends EventObject
{
    protected TreePath[] paths;
    protected boolean[] areNew;
    protected TreePath oldLeadSelectionPath;
    protected TreePath newLeadSelectionPath;
    
    public TreeSelectionEvent(final Object o, final TreePath[] paths, final boolean[] areNew, final TreePath oldLeadSelectionPath, final TreePath newLeadSelectionPath) {
        super(o);
        this.paths = paths;
        this.areNew = areNew;
        this.oldLeadSelectionPath = oldLeadSelectionPath;
        this.newLeadSelectionPath = newLeadSelectionPath;
    }
    
    public TreeSelectionEvent(final Object o, final TreePath treePath, final boolean b, final TreePath oldLeadSelectionPath, final TreePath newLeadSelectionPath) {
        super(o);
        (this.paths = new TreePath[1])[0] = treePath;
        (this.areNew = new boolean[1])[0] = b;
        this.oldLeadSelectionPath = oldLeadSelectionPath;
        this.newLeadSelectionPath = newLeadSelectionPath;
    }
    
    public TreePath[] getPaths() {
        final int length = this.paths.length;
        final TreePath[] array = new TreePath[length];
        System.arraycopy(this.paths, 0, array, 0, length);
        return array;
    }
    
    public TreePath getPath() {
        return this.paths[0];
    }
    
    public boolean isAddedPath() {
        return this.areNew[0];
    }
    
    public boolean isAddedPath(final TreePath treePath) {
        for (int i = this.paths.length - 1; i >= 0; --i) {
            if (this.paths[i].equals(treePath)) {
                return this.areNew[i];
            }
        }
        throw new IllegalArgumentException("path is not a path identified by the TreeSelectionEvent");
    }
    
    public boolean isAddedPath(final int n) {
        if (this.paths == null || n < 0 || n >= this.paths.length) {
            throw new IllegalArgumentException("index is beyond range of added paths identified by TreeSelectionEvent");
        }
        return this.areNew[n];
    }
    
    public TreePath getOldLeadSelectionPath() {
        return this.oldLeadSelectionPath;
    }
    
    public TreePath getNewLeadSelectionPath() {
        return this.newLeadSelectionPath;
    }
    
    public Object cloneWithSource(final Object o) {
        return new TreeSelectionEvent(o, this.paths, this.areNew, this.oldLeadSelectionPath, this.newLeadSelectionPath);
    }
}
