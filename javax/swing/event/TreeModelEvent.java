package javax.swing.event;

import javax.swing.tree.TreePath;
import java.util.EventObject;

public class TreeModelEvent extends EventObject
{
    protected TreePath path;
    protected int[] childIndices;
    protected Object[] children;
    
    public TreeModelEvent(final Object o, final Object[] array, final int[] array2, final Object[] array3) {
        this(o, (array == null) ? null : new TreePath(array), array2, array3);
    }
    
    public TreeModelEvent(final Object o, final TreePath path, final int[] childIndices, final Object[] children) {
        super(o);
        this.path = path;
        this.childIndices = childIndices;
        this.children = children;
    }
    
    public TreeModelEvent(final Object o, final Object[] array) {
        this(o, (array == null) ? null : new TreePath(array));
    }
    
    public TreeModelEvent(final Object o, final TreePath path) {
        super(o);
        this.path = path;
        this.childIndices = new int[0];
    }
    
    public TreePath getTreePath() {
        return this.path;
    }
    
    public Object[] getPath() {
        if (this.path != null) {
            return this.path.getPath();
        }
        return null;
    }
    
    public Object[] getChildren() {
        if (this.children != null) {
            final int length = this.children.length;
            final Object[] array = new Object[length];
            System.arraycopy(this.children, 0, array, 0, length);
            return array;
        }
        return null;
    }
    
    public int[] getChildIndices() {
        if (this.childIndices != null) {
            final int length = this.childIndices.length;
            final int[] array = new int[length];
            System.arraycopy(this.childIndices, 0, array, 0, length);
            return array;
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getName() + " " + Integer.toString(this.hashCode()));
        if (this.path != null) {
            sb.append(" path " + this.path);
        }
        if (this.childIndices != null) {
            sb.append(" indices [ ");
            for (int i = 0; i < this.childIndices.length; ++i) {
                sb.append(Integer.toString(this.childIndices[i]) + " ");
            }
            sb.append("]");
        }
        if (this.children != null) {
            sb.append(" children [ ");
            for (int j = 0; j < this.children.length; ++j) {
                sb.append(this.children[j] + " ");
            }
            sb.append("]");
        }
        return sb.toString();
    }
}
