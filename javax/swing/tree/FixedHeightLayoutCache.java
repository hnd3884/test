package javax.swing.tree;

import java.util.NoSuchElementException;
import sun.swing.SwingUtilities2;
import javax.swing.event.TreeModelEvent;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Hashtable;
import java.awt.Rectangle;

public class FixedHeightLayoutCache extends AbstractLayoutCache
{
    private FHTreeStateNode root;
    private int rowCount;
    private Rectangle boundsBuffer;
    private Hashtable<TreePath, FHTreeStateNode> treePathMapping;
    private SearchInfo info;
    private Stack<Stack<TreePath>> tempStacks;
    
    public FixedHeightLayoutCache() {
        this.tempStacks = new Stack<Stack<TreePath>>();
        this.boundsBuffer = new Rectangle();
        this.treePathMapping = new Hashtable<TreePath, FHTreeStateNode>();
        this.info = new SearchInfo();
        this.setRowHeight(1);
    }
    
    @Override
    public void setModel(final TreeModel model) {
        super.setModel(model);
        this.rebuild(false);
    }
    
    @Override
    public void setRootVisible(final boolean rootVisible) {
        if (this.isRootVisible() != rootVisible) {
            super.setRootVisible(rootVisible);
            if (this.root != null) {
                if (rootVisible) {
                    ++this.rowCount;
                    this.root.adjustRowBy(1);
                }
                else {
                    --this.rowCount;
                    this.root.adjustRowBy(-1);
                }
                this.visibleNodesChanged();
            }
        }
    }
    
    @Override
    public void setRowHeight(final int rowHeight) {
        if (rowHeight <= 0) {
            throw new IllegalArgumentException("FixedHeightLayoutCache only supports row heights greater than 0");
        }
        if (this.getRowHeight() != rowHeight) {
            super.setRowHeight(rowHeight);
            this.visibleNodesChanged();
        }
    }
    
    @Override
    public int getRowCount() {
        return this.rowCount;
    }
    
    @Override
    public void invalidatePathBounds(final TreePath treePath) {
    }
    
    @Override
    public void invalidateSizes() {
        this.visibleNodesChanged();
    }
    
    @Override
    public boolean isExpanded(final TreePath treePath) {
        if (treePath != null) {
            final FHTreeStateNode nodeForPath = this.getNodeForPath(treePath, true, false);
            return nodeForPath != null && nodeForPath.isExpanded();
        }
        return false;
    }
    
    @Override
    public Rectangle getBounds(final TreePath treePath, final Rectangle rectangle) {
        if (treePath == null) {
            return null;
        }
        final FHTreeStateNode nodeForPath = this.getNodeForPath(treePath, true, false);
        if (nodeForPath != null) {
            return this.getBounds(nodeForPath, -1, rectangle);
        }
        final TreePath parentPath = treePath.getParentPath();
        final FHTreeStateNode nodeForPath2 = this.getNodeForPath(parentPath, true, false);
        if (nodeForPath2 != null && nodeForPath2.isExpanded()) {
            final int indexOfChild = this.treeModel.getIndexOfChild(parentPath.getLastPathComponent(), treePath.getLastPathComponent());
            if (indexOfChild != -1) {
                return this.getBounds(nodeForPath2, indexOfChild, rectangle);
            }
        }
        return null;
    }
    
    @Override
    public TreePath getPathForRow(final int n) {
        if (n >= 0 && n < this.getRowCount() && this.root.getPathForRow(n, this.getRowCount(), this.info)) {
            return this.info.getPath();
        }
        return null;
    }
    
    @Override
    public int getRowForPath(final TreePath treePath) {
        if (treePath == null || this.root == null) {
            return -1;
        }
        final FHTreeStateNode nodeForPath = this.getNodeForPath(treePath, true, false);
        if (nodeForPath != null) {
            return nodeForPath.getRow();
        }
        final TreePath parentPath = treePath.getParentPath();
        final FHTreeStateNode nodeForPath2 = this.getNodeForPath(parentPath, true, false);
        if (nodeForPath2 != null && nodeForPath2.isExpanded()) {
            return nodeForPath2.getRowToModelIndex(this.treeModel.getIndexOfChild(parentPath.getLastPathComponent(), treePath.getLastPathComponent()));
        }
        return -1;
    }
    
    @Override
    public TreePath getPathClosestTo(final int n, final int n2) {
        if (this.getRowCount() == 0) {
            return null;
        }
        return this.getPathForRow(this.getRowContainingYLocation(n2));
    }
    
    @Override
    public int getVisibleChildCount(final TreePath treePath) {
        final FHTreeStateNode nodeForPath = this.getNodeForPath(treePath, true, false);
        if (nodeForPath == null) {
            return 0;
        }
        return nodeForPath.getTotalChildCount();
    }
    
    @Override
    public Enumeration<TreePath> getVisiblePathsFrom(final TreePath treePath) {
        if (treePath == null) {
            return null;
        }
        final FHTreeStateNode nodeForPath = this.getNodeForPath(treePath, true, false);
        if (nodeForPath != null) {
            return new VisibleFHTreeStateNodeEnumeration(nodeForPath);
        }
        final TreePath parentPath = treePath.getParentPath();
        final FHTreeStateNode nodeForPath2 = this.getNodeForPath(parentPath, true, false);
        if (nodeForPath2 != null && nodeForPath2.isExpanded()) {
            return new VisibleFHTreeStateNodeEnumeration(nodeForPath2, this.treeModel.getIndexOfChild(parentPath.getLastPathComponent(), treePath.getLastPathComponent()));
        }
        return null;
    }
    
    @Override
    public void setExpandedState(final TreePath treePath, final boolean b) {
        if (b) {
            this.ensurePathIsExpanded(treePath, true);
        }
        else if (treePath != null) {
            final TreePath parentPath = treePath.getParentPath();
            if (parentPath != null) {
                final FHTreeStateNode nodeForPath = this.getNodeForPath(parentPath, false, true);
                if (nodeForPath != null) {
                    nodeForPath.makeVisible();
                }
            }
            final FHTreeStateNode nodeForPath2 = this.getNodeForPath(treePath, true, false);
            if (nodeForPath2 != null) {
                nodeForPath2.collapse(true);
            }
        }
    }
    
    @Override
    public boolean getExpandedState(final TreePath treePath) {
        final FHTreeStateNode nodeForPath = this.getNodeForPath(treePath, true, false);
        return nodeForPath != null && (nodeForPath.isVisible() && nodeForPath.isExpanded());
    }
    
    @Override
    public void treeNodesChanged(final TreeModelEvent treeModelEvent) {
        if (treeModelEvent != null) {
            final FHTreeStateNode nodeForPath = this.getNodeForPath(SwingUtilities2.getTreePath(treeModelEvent, this.getModel()), false, false);
            final int[] childIndices = treeModelEvent.getChildIndices();
            if (nodeForPath != null) {
                final int length;
                if (childIndices != null && (length = childIndices.length) > 0) {
                    final Object userObject = nodeForPath.getUserObject();
                    for (int i = 0; i < length; ++i) {
                        final FHTreeStateNode childAtModelIndex = nodeForPath.getChildAtModelIndex(childIndices[i]);
                        if (childAtModelIndex != null) {
                            childAtModelIndex.setUserObject(this.treeModel.getChild(userObject, childIndices[i]));
                        }
                    }
                    if (nodeForPath.isVisible() && nodeForPath.isExpanded()) {
                        this.visibleNodesChanged();
                    }
                }
                else if (nodeForPath == this.root && nodeForPath.isVisible() && nodeForPath.isExpanded()) {
                    this.visibleNodesChanged();
                }
            }
        }
    }
    
    @Override
    public void treeNodesInserted(final TreeModelEvent treeModelEvent) {
        if (treeModelEvent != null) {
            final FHTreeStateNode nodeForPath = this.getNodeForPath(SwingUtilities2.getTreePath(treeModelEvent, this.getModel()), false, false);
            final int[] childIndices = treeModelEvent.getChildIndices();
            final int length;
            if (nodeForPath != null && childIndices != null && (length = childIndices.length) > 0) {
                final boolean b = nodeForPath.isVisible() && nodeForPath.isExpanded();
                for (int i = 0; i < length; ++i) {
                    nodeForPath.childInsertedAtModelIndex(childIndices[i], b);
                }
                if (b && this.treeSelectionModel != null) {
                    this.treeSelectionModel.resetRowSelection();
                }
                if (nodeForPath.isVisible()) {
                    this.visibleNodesChanged();
                }
            }
        }
    }
    
    @Override
    public void treeNodesRemoved(final TreeModelEvent treeModelEvent) {
        if (treeModelEvent != null) {
            final FHTreeStateNode nodeForPath = this.getNodeForPath(SwingUtilities2.getTreePath(treeModelEvent, this.getModel()), false, false);
            final int[] childIndices = treeModelEvent.getChildIndices();
            final int length;
            if (nodeForPath != null && childIndices != null && (length = childIndices.length) > 0) {
                treeModelEvent.getChildren();
                final boolean b = nodeForPath.isVisible() && nodeForPath.isExpanded();
                for (int i = length - 1; i >= 0; --i) {
                    nodeForPath.removeChildAtModelIndex(childIndices[i], b);
                }
                if (b) {
                    if (this.treeSelectionModel != null) {
                        this.treeSelectionModel.resetRowSelection();
                    }
                    if (this.treeModel.getChildCount(nodeForPath.getUserObject()) == 0 && nodeForPath.isLeaf()) {
                        nodeForPath.collapse(false);
                    }
                    this.visibleNodesChanged();
                }
                else if (nodeForPath.isVisible()) {
                    this.visibleNodesChanged();
                }
            }
        }
    }
    
    @Override
    public void treeStructureChanged(final TreeModelEvent treeModelEvent) {
        if (treeModelEvent != null) {
            final TreePath treePath = SwingUtilities2.getTreePath(treeModelEvent, this.getModel());
            final FHTreeStateNode nodeForPath = this.getNodeForPath(treePath, false, false);
            if (nodeForPath == this.root || (nodeForPath == null && ((treePath == null && this.treeModel != null && this.treeModel.getRoot() == null) || (treePath != null && treePath.getPathCount() <= 1)))) {
                this.rebuild(true);
            }
            else if (nodeForPath != null) {
                final FHTreeStateNode fhTreeStateNode = (FHTreeStateNode)nodeForPath.getParent();
                final boolean expanded = nodeForPath.isExpanded();
                final boolean visible = nodeForPath.isVisible();
                final int index = fhTreeStateNode.getIndex(nodeForPath);
                nodeForPath.collapse(false);
                fhTreeStateNode.remove(index);
                if (visible && expanded) {
                    fhTreeStateNode.resetChildrenRowsFrom(nodeForPath.getRow(), index, nodeForPath.getChildIndex());
                    this.getNodeForPath(treePath, false, true).expand();
                }
                if (this.treeSelectionModel != null && visible && expanded) {
                    this.treeSelectionModel.resetRowSelection();
                }
                if (visible) {
                    this.visibleNodesChanged();
                }
            }
        }
    }
    
    private void visibleNodesChanged() {
    }
    
    private Rectangle getBounds(final FHTreeStateNode fhTreeStateNode, final int n, Rectangle rectangle) {
        int n2;
        Object o;
        boolean expanded;
        int level;
        if (n == -1) {
            n2 = fhTreeStateNode.getRow();
            o = fhTreeStateNode.getUserObject();
            expanded = fhTreeStateNode.isExpanded();
            level = fhTreeStateNode.getLevel();
        }
        else {
            n2 = fhTreeStateNode.getRowToModelIndex(n);
            o = this.treeModel.getChild(fhTreeStateNode.getUserObject(), n);
            expanded = false;
            level = fhTreeStateNode.getLevel() + 1;
        }
        final Rectangle nodeDimensions = this.getNodeDimensions(o, n2, level, expanded, this.boundsBuffer);
        if (nodeDimensions == null) {
            return null;
        }
        if (rectangle == null) {
            rectangle = new Rectangle();
        }
        rectangle.x = nodeDimensions.x;
        rectangle.height = this.getRowHeight();
        rectangle.y = n2 * rectangle.height;
        rectangle.width = nodeDimensions.width;
        return rectangle;
    }
    
    private void adjustRowCountBy(final int n) {
        this.rowCount += n;
    }
    
    private void addMapping(final FHTreeStateNode fhTreeStateNode) {
        this.treePathMapping.put(fhTreeStateNode.getTreePath(), fhTreeStateNode);
    }
    
    private void removeMapping(final FHTreeStateNode fhTreeStateNode) {
        this.treePathMapping.remove(fhTreeStateNode.getTreePath());
    }
    
    private FHTreeStateNode getMapping(final TreePath treePath) {
        return this.treePathMapping.get(treePath);
    }
    
    private void rebuild(final boolean b) {
        this.treePathMapping.clear();
        final Object root;
        if (this.treeModel != null && (root = this.treeModel.getRoot()) != null) {
            this.root = this.createNodeForValue(root, 0);
            this.root.path = new TreePath(root);
            this.addMapping(this.root);
            if (this.isRootVisible()) {
                this.rowCount = 1;
                this.root.row = 0;
            }
            else {
                this.rowCount = 0;
                this.root.row = -1;
            }
            this.root.expand();
        }
        else {
            this.root = null;
            this.rowCount = 0;
        }
        if (b && this.treeSelectionModel != null) {
            this.treeSelectionModel.clearSelection();
        }
        this.visibleNodesChanged();
    }
    
    private int getRowContainingYLocation(final int n) {
        if (this.getRowCount() == 0) {
            return -1;
        }
        return Math.max(0, Math.min(this.getRowCount() - 1, n / this.getRowHeight()));
    }
    
    private boolean ensurePathIsExpanded(TreePath parentPath, boolean b) {
        if (parentPath != null) {
            if (this.treeModel.isLeaf(parentPath.getLastPathComponent())) {
                parentPath = parentPath.getParentPath();
                b = true;
            }
            if (parentPath != null) {
                final FHTreeStateNode nodeForPath = this.getNodeForPath(parentPath, false, true);
                if (nodeForPath != null) {
                    nodeForPath.makeVisible();
                    if (b) {
                        nodeForPath.expand();
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    private FHTreeStateNode createNodeForValue(final Object o, final int n) {
        return new FHTreeStateNode(o, n, -1);
    }
    
    private FHTreeStateNode getNodeForPath(TreePath treePath, final boolean b, final boolean b2) {
        if (treePath == null) {
            return null;
        }
        final FHTreeStateNode mapping = this.getMapping(treePath);
        if (mapping != null) {
            if (b && !mapping.isVisible()) {
                return null;
            }
            return mapping;
        }
        else {
            if (b) {
                return null;
            }
            Stack stack;
            if (this.tempStacks.size() == 0) {
                stack = new Stack();
            }
            else {
                stack = this.tempStacks.pop();
            }
            try {
                stack.push(treePath);
                FHTreeStateNode fhTreeStateNode;
                for (treePath = treePath.getParentPath(); treePath != null; treePath = treePath.getParentPath()) {
                    fhTreeStateNode = this.getMapping(treePath);
                    if (fhTreeStateNode != null) {
                        while (fhTreeStateNode != null && stack.size() > 0) {
                            treePath = (TreePath)stack.pop();
                            fhTreeStateNode = fhTreeStateNode.createChildFor(treePath.getLastPathComponent());
                        }
                        return fhTreeStateNode;
                    }
                    stack.push(treePath);
                }
            }
            finally {
                stack.removeAllElements();
                this.tempStacks.push(stack);
            }
            return null;
        }
    }
    
    private class FHTreeStateNode extends DefaultMutableTreeNode
    {
        protected boolean isExpanded;
        protected int childIndex;
        protected int childCount;
        protected int row;
        protected TreePath path;
        
        public FHTreeStateNode(final Object o, final int childIndex, final int row) {
            super(o);
            this.childIndex = childIndex;
            this.row = row;
        }
        
        @Override
        public void setParent(final MutableTreeNode parent) {
            super.setParent(parent);
            if (parent != null) {
                this.path = ((FHTreeStateNode)parent).getTreePath().pathByAddingChild(this.getUserObject());
                FixedHeightLayoutCache.this.addMapping(this);
            }
        }
        
        @Override
        public void remove(final int n) {
            ((FHTreeStateNode)this.getChildAt(n)).removeFromMapping();
            super.remove(n);
        }
        
        @Override
        public void setUserObject(final Object userObject) {
            super.setUserObject(userObject);
            if (this.path != null) {
                final FHTreeStateNode fhTreeStateNode = (FHTreeStateNode)this.getParent();
                if (fhTreeStateNode != null) {
                    this.resetChildrenPaths(fhTreeStateNode.getTreePath());
                }
                else {
                    this.resetChildrenPaths(null);
                }
            }
        }
        
        public int getChildIndex() {
            return this.childIndex;
        }
        
        public TreePath getTreePath() {
            return this.path;
        }
        
        public FHTreeStateNode getChildAtModelIndex(final int n) {
            for (int i = this.getChildCount() - 1; i >= 0; --i) {
                if (((FHTreeStateNode)this.getChildAt(i)).childIndex == n) {
                    return (FHTreeStateNode)this.getChildAt(i);
                }
            }
            return null;
        }
        
        public boolean isVisible() {
            final FHTreeStateNode fhTreeStateNode = (FHTreeStateNode)this.getParent();
            return fhTreeStateNode == null || (fhTreeStateNode.isExpanded() && fhTreeStateNode.isVisible());
        }
        
        public int getRow() {
            return this.row;
        }
        
        public int getRowToModelIndex(final int n) {
            final int n2 = this.getRow() + 1;
            int i = 0;
            while (i < this.getChildCount()) {
                final FHTreeStateNode fhTreeStateNode = (FHTreeStateNode)this.getChildAt(i);
                if (fhTreeStateNode.childIndex >= n) {
                    if (fhTreeStateNode.childIndex == n) {
                        return fhTreeStateNode.row;
                    }
                    if (i == 0) {
                        return this.getRow() + 1 + n;
                    }
                    return fhTreeStateNode.row - (fhTreeStateNode.childIndex - n);
                }
                else {
                    ++i;
                }
            }
            return this.getRow() + 1 + this.getTotalChildCount() - (this.childCount - n);
        }
        
        public int getTotalChildCount() {
            if (!this.isExpanded()) {
                return 0;
            }
            final FHTreeStateNode fhTreeStateNode = (FHTreeStateNode)this.getParent();
            final int index;
            if (fhTreeStateNode != null && (index = fhTreeStateNode.getIndex(this)) + 1 < fhTreeStateNode.getChildCount()) {
                final FHTreeStateNode fhTreeStateNode2 = (FHTreeStateNode)fhTreeStateNode.getChildAt(index + 1);
                return fhTreeStateNode2.row - this.row - (fhTreeStateNode2.childIndex - this.childIndex);
            }
            int childCount = this.childCount;
            for (int i = this.getChildCount() - 1; i >= 0; --i) {
                childCount += ((FHTreeStateNode)this.getChildAt(i)).getTotalChildCount();
            }
            return childCount;
        }
        
        public boolean isExpanded() {
            return this.isExpanded;
        }
        
        public int getVisibleLevel() {
            if (FixedHeightLayoutCache.this.isRootVisible()) {
                return this.getLevel();
            }
            return this.getLevel() - 1;
        }
        
        protected void resetChildrenPaths(final TreePath treePath) {
            FixedHeightLayoutCache.this.removeMapping(this);
            if (treePath == null) {
                this.path = new TreePath(this.getUserObject());
            }
            else {
                this.path = treePath.pathByAddingChild(this.getUserObject());
            }
            FixedHeightLayoutCache.this.addMapping(this);
            for (int i = this.getChildCount() - 1; i >= 0; --i) {
                ((FHTreeStateNode)this.getChildAt(i)).resetChildrenPaths(this.path);
            }
        }
        
        protected void removeFromMapping() {
            if (this.path != null) {
                FixedHeightLayoutCache.this.removeMapping(this);
                for (int i = this.getChildCount() - 1; i >= 0; --i) {
                    ((FHTreeStateNode)this.getChildAt(i)).removeFromMapping();
                }
            }
        }
        
        protected FHTreeStateNode createChildFor(final Object o) {
            final int indexOfChild = FixedHeightLayoutCache.this.treeModel.getIndexOfChild(this.getUserObject(), o);
            if (indexOfChild < 0) {
                return null;
            }
            final FHTreeStateNode access$300 = FixedHeightLayoutCache.this.createNodeForValue(o, indexOfChild);
            int rowToModelIndex;
            if (this.isVisible()) {
                rowToModelIndex = this.getRowToModelIndex(indexOfChild);
            }
            else {
                rowToModelIndex = -1;
            }
            access$300.row = rowToModelIndex;
            for (int i = 0; i < this.getChildCount(); ++i) {
                if (((FHTreeStateNode)this.getChildAt(i)).childIndex > indexOfChild) {
                    this.insert(access$300, i);
                    return access$300;
                }
            }
            this.add(access$300);
            return access$300;
        }
        
        protected void adjustRowBy(final int n) {
            this.row += n;
            if (this.isExpanded) {
                for (int i = this.getChildCount() - 1; i >= 0; --i) {
                    ((FHTreeStateNode)this.getChildAt(i)).adjustRowBy(n);
                }
            }
        }
        
        protected void adjustRowBy(final int n, final int n2) {
            if (this.isExpanded) {
                for (int i = this.getChildCount() - 1; i >= n2; --i) {
                    ((FHTreeStateNode)this.getChildAt(i)).adjustRowBy(n);
                }
            }
            final FHTreeStateNode fhTreeStateNode = (FHTreeStateNode)this.getParent();
            if (fhTreeStateNode != null) {
                fhTreeStateNode.adjustRowBy(n, fhTreeStateNode.getIndex(this) + 1);
            }
        }
        
        protected void didExpand() {
            final int setRowAndChildren = this.setRowAndChildren(this.row);
            final FHTreeStateNode fhTreeStateNode = (FHTreeStateNode)this.getParent();
            final int n = setRowAndChildren - this.row - 1;
            if (fhTreeStateNode != null) {
                fhTreeStateNode.adjustRowBy(n, fhTreeStateNode.getIndex(this) + 1);
            }
            FixedHeightLayoutCache.this.adjustRowCountBy(n);
        }
        
        protected int setRowAndChildren(final int row) {
            this.row = row;
            if (!this.isExpanded()) {
                return this.row + 1;
            }
            int setRowAndChildren = this.row + 1;
            int n = 0;
            for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                final FHTreeStateNode fhTreeStateNode = (FHTreeStateNode)this.getChildAt(i);
                setRowAndChildren += fhTreeStateNode.childIndex - n;
                n = fhTreeStateNode.childIndex + 1;
                if (fhTreeStateNode.isExpanded) {
                    setRowAndChildren = fhTreeStateNode.setRowAndChildren(setRowAndChildren);
                }
                else {
                    fhTreeStateNode.row = setRowAndChildren++;
                }
            }
            return setRowAndChildren + this.childCount - n;
        }
        
        protected void resetChildrenRowsFrom(final int n, final int n2, final int n3) {
            int setRowAndChildren = n;
            int n4 = n3;
            for (int childCount = this.getChildCount(), i = n2; i < childCount; ++i) {
                final FHTreeStateNode fhTreeStateNode = (FHTreeStateNode)this.getChildAt(i);
                setRowAndChildren += fhTreeStateNode.childIndex - n4;
                n4 = fhTreeStateNode.childIndex + 1;
                if (fhTreeStateNode.isExpanded) {
                    setRowAndChildren = fhTreeStateNode.setRowAndChildren(setRowAndChildren);
                }
                else {
                    fhTreeStateNode.row = setRowAndChildren++;
                }
            }
            final int n5 = setRowAndChildren + (this.childCount - n4);
            final FHTreeStateNode fhTreeStateNode2 = (FHTreeStateNode)this.getParent();
            if (fhTreeStateNode2 != null) {
                fhTreeStateNode2.resetChildrenRowsFrom(n5, fhTreeStateNode2.getIndex(this) + 1, this.childIndex + 1);
            }
            else {
                FixedHeightLayoutCache.this.rowCount = n5;
            }
        }
        
        protected void makeVisible() {
            final FHTreeStateNode fhTreeStateNode = (FHTreeStateNode)this.getParent();
            if (fhTreeStateNode != null) {
                fhTreeStateNode.expandParentAndReceiver();
            }
        }
        
        protected void expandParentAndReceiver() {
            final FHTreeStateNode fhTreeStateNode = (FHTreeStateNode)this.getParent();
            if (fhTreeStateNode != null) {
                fhTreeStateNode.expandParentAndReceiver();
            }
            this.expand();
        }
        
        protected void expand() {
            if (!this.isExpanded && !this.isLeaf()) {
                final boolean visible = this.isVisible();
                this.isExpanded = true;
                this.childCount = FixedHeightLayoutCache.this.treeModel.getChildCount(this.getUserObject());
                if (visible) {
                    this.didExpand();
                }
                if (visible && FixedHeightLayoutCache.this.treeSelectionModel != null) {
                    FixedHeightLayoutCache.this.treeSelectionModel.resetRowSelection();
                }
            }
        }
        
        protected void collapse(final boolean b) {
            if (this.isExpanded) {
                if (this.isVisible() && b) {
                    final int totalChildCount = this.getTotalChildCount();
                    this.isExpanded = false;
                    FixedHeightLayoutCache.this.adjustRowCountBy(-totalChildCount);
                    this.adjustRowBy(-totalChildCount, 0);
                }
                else {
                    this.isExpanded = false;
                }
                if (b && this.isVisible() && FixedHeightLayoutCache.this.treeSelectionModel != null) {
                    FixedHeightLayoutCache.this.treeSelectionModel.resetRowSelection();
                }
            }
        }
        
        @Override
        public boolean isLeaf() {
            final TreeModel model = FixedHeightLayoutCache.this.getModel();
            return model == null || model.isLeaf(this.getUserObject());
        }
        
        protected void addNode(final FHTreeStateNode fhTreeStateNode) {
            boolean b = false;
            final int childIndex = fhTreeStateNode.getChildIndex();
            for (int i = 0, childCount = this.getChildCount(); i < childCount; ++i) {
                if (((FHTreeStateNode)this.getChildAt(i)).getChildIndex() > childIndex) {
                    b = true;
                    this.insert(fhTreeStateNode, i);
                    i = childCount;
                }
            }
            if (!b) {
                this.add(fhTreeStateNode);
            }
        }
        
        protected void removeChildAtModelIndex(final int n, final boolean b) {
            final FHTreeStateNode childAtModelIndex = this.getChildAtModelIndex(n);
            if (childAtModelIndex != null) {
                final int row = childAtModelIndex.getRow();
                final int index = this.getIndex(childAtModelIndex);
                childAtModelIndex.collapse(false);
                this.remove(index);
                this.adjustChildIndexs(index, -1);
                --this.childCount;
                if (b) {
                    this.resetChildrenRowsFrom(row, index, n);
                }
            }
            else {
                final int childCount = this.getChildCount();
                for (int i = 0; i < childCount; ++i) {
                    if (((FHTreeStateNode)this.getChildAt(i)).childIndex >= n) {
                        if (b) {
                            this.adjustRowBy(-1, i);
                            FixedHeightLayoutCache.this.adjustRowCountBy(-1);
                        }
                        while (i < childCount) {
                            final FHTreeStateNode fhTreeStateNode = (FHTreeStateNode)this.getChildAt(i);
                            --fhTreeStateNode.childIndex;
                            ++i;
                        }
                        --this.childCount;
                        return;
                    }
                }
                if (b) {
                    this.adjustRowBy(-1, childCount);
                    FixedHeightLayoutCache.this.adjustRowCountBy(-1);
                }
                --this.childCount;
            }
        }
        
        protected void adjustChildIndexs(final int n, final int n2) {
            for (int i = n; i < this.getChildCount(); ++i) {
                final FHTreeStateNode fhTreeStateNode = (FHTreeStateNode)this.getChildAt(i);
                fhTreeStateNode.childIndex += n2;
            }
        }
        
        protected void childInsertedAtModelIndex(final int n, final boolean b) {
            final int childCount = this.getChildCount();
            for (int i = 0; i < childCount; ++i) {
                if (((FHTreeStateNode)this.getChildAt(i)).childIndex >= n) {
                    if (b) {
                        this.adjustRowBy(1, i);
                        FixedHeightLayoutCache.this.adjustRowCountBy(1);
                    }
                    while (i < childCount) {
                        final FHTreeStateNode fhTreeStateNode = (FHTreeStateNode)this.getChildAt(i);
                        ++fhTreeStateNode.childIndex;
                        ++i;
                    }
                    ++this.childCount;
                    return;
                }
            }
            if (b) {
                this.adjustRowBy(1, childCount);
                FixedHeightLayoutCache.this.adjustRowCountBy(1);
            }
            ++this.childCount;
        }
        
        protected boolean getPathForRow(final int n, final int n2, final SearchInfo searchInfo) {
            if (this.row == n) {
                searchInfo.node = this;
                searchInfo.isNodeParentNode = false;
                searchInfo.childIndex = this.childIndex;
                return true;
            }
            FHTreeStateNode fhTreeStateNode = null;
            int i = 0;
            while (i < this.getChildCount()) {
                final FHTreeStateNode fhTreeStateNode2 = (FHTreeStateNode)this.getChildAt(i);
                if (fhTreeStateNode2.row > n) {
                    if (i == 0) {
                        searchInfo.node = this;
                        searchInfo.isNodeParentNode = true;
                        searchInfo.childIndex = n - this.row - 1;
                        return true;
                    }
                    final int n3 = 1 + fhTreeStateNode2.row - (fhTreeStateNode2.childIndex - fhTreeStateNode.childIndex);
                    if (n < n3) {
                        return fhTreeStateNode.getPathForRow(n, n3, searchInfo);
                    }
                    searchInfo.node = this;
                    searchInfo.isNodeParentNode = true;
                    searchInfo.childIndex = n - n3 + fhTreeStateNode.childIndex + 1;
                    return true;
                }
                else {
                    fhTreeStateNode = fhTreeStateNode2;
                    ++i;
                }
            }
            if (fhTreeStateNode != null) {
                final int n4 = n2 - (this.childCount - fhTreeStateNode.childIndex) + 1;
                if (n < n4) {
                    return fhTreeStateNode.getPathForRow(n, n4, searchInfo);
                }
                searchInfo.node = this;
                searchInfo.isNodeParentNode = true;
                searchInfo.childIndex = n - n4 + fhTreeStateNode.childIndex + 1;
                return true;
            }
            else {
                final int childIndex = n - this.row - 1;
                if (childIndex >= this.childCount) {
                    return false;
                }
                searchInfo.node = this;
                searchInfo.isNodeParentNode = true;
                searchInfo.childIndex = childIndex;
                return true;
            }
        }
        
        protected int getCountTo(final int n) {
            int n2 = n + 1;
            for (int i = 0, childCount = this.getChildCount(); i < childCount; ++i) {
                final FHTreeStateNode fhTreeStateNode = (FHTreeStateNode)this.getChildAt(i);
                if (fhTreeStateNode.childIndex >= n) {
                    i = childCount;
                }
                else {
                    n2 += fhTreeStateNode.getTotalChildCount();
                }
            }
            if (this.parent != null) {
                return n2 + ((FHTreeStateNode)this.getParent()).getCountTo(this.childIndex);
            }
            if (!FixedHeightLayoutCache.this.isRootVisible()) {
                return n2 - 1;
            }
            return n2;
        }
        
        protected int getNumExpandedChildrenTo(final int n) {
            int n2 = n;
            for (int i = 0; i < this.getChildCount(); ++i) {
                final FHTreeStateNode fhTreeStateNode = (FHTreeStateNode)this.getChildAt(i);
                if (fhTreeStateNode.childIndex >= n) {
                    return n2;
                }
                n2 += fhTreeStateNode.getTotalChildCount();
            }
            return n2;
        }
        
        protected void didAdjustTree() {
        }
    }
    
    private class SearchInfo
    {
        protected FHTreeStateNode node;
        protected boolean isNodeParentNode;
        protected int childIndex;
        
        protected TreePath getPath() {
            if (this.node == null) {
                return null;
            }
            if (this.isNodeParentNode) {
                return this.node.getTreePath().pathByAddingChild(FixedHeightLayoutCache.this.treeModel.getChild(this.node.getUserObject(), this.childIndex));
            }
            return this.node.path;
        }
    }
    
    private class VisibleFHTreeStateNodeEnumeration implements Enumeration<TreePath>
    {
        protected FHTreeStateNode parent;
        protected int nextIndex;
        protected int childCount;
        
        protected VisibleFHTreeStateNodeEnumeration(final FixedHeightLayoutCache fixedHeightLayoutCache, final FHTreeStateNode fhTreeStateNode) {
            this(fixedHeightLayoutCache, fhTreeStateNode, -1);
        }
        
        protected VisibleFHTreeStateNodeEnumeration(final FHTreeStateNode parent, final int nextIndex) {
            this.parent = parent;
            this.nextIndex = nextIndex;
            this.childCount = FixedHeightLayoutCache.this.treeModel.getChildCount(this.parent.getUserObject());
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.parent != null;
        }
        
        @Override
        public TreePath nextElement() {
            if (!this.hasMoreElements()) {
                throw new NoSuchElementException("No more visible paths");
            }
            TreePath treePath;
            if (this.nextIndex == -1) {
                treePath = this.parent.getTreePath();
            }
            else {
                final FHTreeStateNode childAtModelIndex = this.parent.getChildAtModelIndex(this.nextIndex);
                if (childAtModelIndex == null) {
                    treePath = this.parent.getTreePath().pathByAddingChild(FixedHeightLayoutCache.this.treeModel.getChild(this.parent.getUserObject(), this.nextIndex));
                }
                else {
                    treePath = childAtModelIndex.getTreePath();
                }
            }
            this.updateNextObject();
            return treePath;
        }
        
        protected void updateNextObject() {
            if (!this.updateNextIndex()) {
                this.findNextValidParent();
            }
        }
        
        protected boolean findNextValidParent() {
            if (this.parent == FixedHeightLayoutCache.this.root) {
                this.parent = null;
                return false;
            }
            while (this.parent != null) {
                final FHTreeStateNode parent = (FHTreeStateNode)this.parent.getParent();
                if (parent != null) {
                    this.nextIndex = this.parent.childIndex;
                    this.parent = parent;
                    this.childCount = FixedHeightLayoutCache.this.treeModel.getChildCount(this.parent.getUserObject());
                    if (this.updateNextIndex()) {
                        return true;
                    }
                    continue;
                }
                else {
                    this.parent = null;
                }
            }
            return false;
        }
        
        protected boolean updateNextIndex() {
            if (this.nextIndex == -1 && !this.parent.isExpanded()) {
                return false;
            }
            if (this.childCount == 0) {
                return false;
            }
            if (++this.nextIndex >= this.childCount) {
                return false;
            }
            final FHTreeStateNode childAtModelIndex = this.parent.getChildAtModelIndex(this.nextIndex);
            if (childAtModelIndex != null && childAtModelIndex.isExpanded()) {
                this.parent = childAtModelIndex;
                this.nextIndex = -1;
                this.childCount = FixedHeightLayoutCache.this.treeModel.getChildCount(childAtModelIndex.getUserObject());
            }
            return true;
        }
    }
}
