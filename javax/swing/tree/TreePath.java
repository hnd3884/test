package javax.swing.tree;

import java.beans.ConstructorProperties;
import java.io.Serializable;

public class TreePath implements Serializable
{
    private TreePath parentPath;
    private Object lastPathComponent;
    
    @ConstructorProperties({ "path" })
    public TreePath(final Object[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("path in TreePath must be non null and not empty.");
        }
        this.lastPathComponent = array[array.length - 1];
        if (this.lastPathComponent == null) {
            throw new IllegalArgumentException("Last path component must be non-null");
        }
        if (array.length > 1) {
            this.parentPath = new TreePath(array, array.length - 1);
        }
    }
    
    public TreePath(final Object lastPathComponent) {
        if (lastPathComponent == null) {
            throw new IllegalArgumentException("path in TreePath must be non null.");
        }
        this.lastPathComponent = lastPathComponent;
        this.parentPath = null;
    }
    
    protected TreePath(final TreePath parentPath, final Object lastPathComponent) {
        if (lastPathComponent == null) {
            throw new IllegalArgumentException("path in TreePath must be non null.");
        }
        this.parentPath = parentPath;
        this.lastPathComponent = lastPathComponent;
    }
    
    protected TreePath(final Object[] array, final int n) {
        this.lastPathComponent = array[n - 1];
        if (this.lastPathComponent == null) {
            throw new IllegalArgumentException("Path elements must be non-null");
        }
        if (n > 1) {
            this.parentPath = new TreePath(array, n - 1);
        }
    }
    
    protected TreePath() {
    }
    
    public Object[] getPath() {
        int pathCount = this.getPathCount();
        final Object[] array = new Object[pathCount--];
        for (TreePath parentPath = this; parentPath != null; parentPath = parentPath.getParentPath()) {
            array[pathCount--] = parentPath.getLastPathComponent();
        }
        return array;
    }
    
    public Object getLastPathComponent() {
        return this.lastPathComponent;
    }
    
    public int getPathCount() {
        int n = 0;
        for (TreePath parentPath = this; parentPath != null; parentPath = parentPath.getParentPath()) {
            ++n;
        }
        return n;
    }
    
    public Object getPathComponent(final int n) {
        final int pathCount = this.getPathCount();
        if (n < 0 || n >= pathCount) {
            throw new IllegalArgumentException("Index " + n + " is out of the specified range");
        }
        TreePath parentPath = this;
        for (int i = pathCount - 1; i != n; --i) {
            parentPath = parentPath.getParentPath();
        }
        return parentPath.getLastPathComponent();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TreePath)) {
            return false;
        }
        TreePath parentPath = (TreePath)o;
        if (this.getPathCount() != parentPath.getPathCount()) {
            return false;
        }
        for (TreePath parentPath2 = this; parentPath2 != null; parentPath2 = parentPath2.getParentPath()) {
            if (!parentPath2.getLastPathComponent().equals(parentPath.getLastPathComponent())) {
                return false;
            }
            parentPath = parentPath.getParentPath();
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return this.getLastPathComponent().hashCode();
    }
    
    public boolean isDescendant(TreePath parentPath) {
        if (parentPath == this) {
            return true;
        }
        if (parentPath == null) {
            return false;
        }
        final int pathCount = this.getPathCount();
        int pathCount2 = parentPath.getPathCount();
        if (pathCount2 < pathCount) {
            return false;
        }
        while (pathCount2-- > pathCount) {
            parentPath = parentPath.getParentPath();
        }
        return this.equals(parentPath);
    }
    
    public TreePath pathByAddingChild(final Object o) {
        if (o == null) {
            throw new NullPointerException("Null child not allowed");
        }
        return new TreePath(this, o);
    }
    
    public TreePath getParentPath() {
        return this.parentPath;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("[");
        for (int i = 0; i < this.getPathCount(); ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.getPathComponent(i));
        }
        sb.append("]");
        return sb.toString();
    }
}
