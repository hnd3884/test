package javax.swing.tree;

class PathPlaceHolder
{
    protected boolean isNew;
    protected TreePath path;
    
    PathPlaceHolder(final TreePath path, final boolean isNew) {
        this.path = path;
        this.isNew = isNew;
    }
}
