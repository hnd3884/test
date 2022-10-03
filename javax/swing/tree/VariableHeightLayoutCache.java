package javax.swing.tree;

import java.util.NoSuchElementException;
import sun.swing.SwingUtilities2;
import javax.swing.event.TreeModelEvent;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Hashtable;
import java.awt.Rectangle;
import java.util.Vector;

public class VariableHeightLayoutCache extends AbstractLayoutCache
{
    private Vector<Object> visibleNodes;
    private boolean updateNodeSizes;
    private TreeStateNode root;
    private Rectangle boundsBuffer;
    private Hashtable<TreePath, TreeStateNode> treePathMapping;
    private Stack<Stack<TreePath>> tempStacks;
    
    public VariableHeightLayoutCache() {
        this.tempStacks = new Stack<Stack<TreePath>>();
        this.visibleNodes = new Vector<Object>();
        this.boundsBuffer = new Rectangle();
        this.treePathMapping = new Hashtable<TreePath, TreeStateNode>();
    }
    
    @Override
    public void setModel(final TreeModel model) {
        super.setModel(model);
        this.rebuild(false);
    }
    
    @Override
    public void setRootVisible(final boolean rootVisible) {
        if (this.isRootVisible() != rootVisible && this.root != null) {
            if (rootVisible) {
                this.root.updatePreferredSize(0);
                this.visibleNodes.insertElementAt(this.root, 0);
            }
            else if (this.visibleNodes.size() > 0) {
                this.visibleNodes.removeElementAt(0);
                if (this.treeSelectionModel != null) {
                    this.treeSelectionModel.removeSelectionPath(this.root.getTreePath());
                }
            }
            if (this.treeSelectionModel != null) {
                this.treeSelectionModel.resetRowSelection();
            }
            if (this.getRowCount() > 0) {
                this.getNode(0).setYOrigin(0);
            }
            this.updateYLocationsFrom(0);
            this.visibleNodesChanged();
        }
        super.setRootVisible(rootVisible);
    }
    
    @Override
    public void setRowHeight(final int rowHeight) {
        if (rowHeight != this.getRowHeight()) {
            super.setRowHeight(rowHeight);
            this.invalidateSizes();
            this.visibleNodesChanged();
        }
    }
    
    @Override
    public void setNodeDimensions(final NodeDimensions nodeDimensions) {
        super.setNodeDimensions(nodeDimensions);
        this.invalidateSizes();
        this.visibleNodesChanged();
    }
    
    @Override
    public void setExpandedState(final TreePath treePath, final boolean b) {
        if (treePath != null) {
            if (b) {
                this.ensurePathIsExpanded(treePath, true);
            }
            else {
                final TreeStateNode nodeForPath = this.getNodeForPath(treePath, false, true);
                if (nodeForPath != null) {
                    nodeForPath.makeVisible();
                    nodeForPath.collapse();
                }
            }
        }
    }
    
    @Override
    public boolean getExpandedState(final TreePath treePath) {
        final TreeStateNode nodeForPath = this.getNodeForPath(treePath, true, false);
        return nodeForPath != null && (nodeForPath.isVisible() && nodeForPath.isExpanded());
    }
    
    @Override
    public Rectangle getBounds(final TreePath treePath, final Rectangle rectangle) {
        final TreeStateNode nodeForPath = this.getNodeForPath(treePath, true, false);
        if (nodeForPath != null) {
            if (this.updateNodeSizes) {
                this.updateNodeSizes(false);
            }
            return nodeForPath.getNodeBounds(rectangle);
        }
        return null;
    }
    
    @Override
    public TreePath getPathForRow(final int n) {
        if (n >= 0 && n < this.getRowCount()) {
            return this.getNode(n).getTreePath();
        }
        return null;
    }
    
    @Override
    public int getRowForPath(final TreePath treePath) {
        if (treePath == null) {
            return -1;
        }
        final TreeStateNode nodeForPath = this.getNodeForPath(treePath, true, false);
        if (nodeForPath != null) {
            return nodeForPath.getRow();
        }
        return -1;
    }
    
    @Override
    public int getRowCount() {
        return this.visibleNodes.size();
    }
    
    @Override
    public void invalidatePathBounds(final TreePath treePath) {
        final TreeStateNode nodeForPath = this.getNodeForPath(treePath, true, false);
        if (nodeForPath != null) {
            nodeForPath.markSizeInvalid();
            if (nodeForPath.isVisible()) {
                this.updateYLocationsFrom(nodeForPath.getRow());
            }
        }
    }
    
    @Override
    public int getPreferredHeight() {
        final int rowCount = this.getRowCount();
        if (rowCount > 0) {
            final TreeStateNode node = this.getNode(rowCount - 1);
            return node.getYOrigin() + node.getPreferredHeight();
        }
        return 0;
    }
    
    @Override
    public int getPreferredWidth(final Rectangle rectangle) {
        if (this.updateNodeSizes) {
            this.updateNodeSizes(false);
        }
        return this.getMaxNodeWidth();
    }
    
    @Override
    public TreePath getPathClosestTo(final int n, final int n2) {
        if (this.getRowCount() == 0) {
            return null;
        }
        if (this.updateNodeSizes) {
            this.updateNodeSizes(false);
        }
        return this.getNode(this.getRowContainingYLocation(n2)).getTreePath();
    }
    
    @Override
    public Enumeration<TreePath> getVisiblePathsFrom(final TreePath treePath) {
        final TreeStateNode nodeForPath = this.getNodeForPath(treePath, true, false);
        if (nodeForPath != null) {
            return new VisibleTreeStateNodeEnumeration(nodeForPath);
        }
        return null;
    }
    
    @Override
    public int getVisibleChildCount(final TreePath treePath) {
        final TreeStateNode nodeForPath = this.getNodeForPath(treePath, true, false);
        return (nodeForPath != null) ? nodeForPath.getVisibleChildCount() : 0;
    }
    
    @Override
    public void invalidateSizes() {
        if (this.root != null) {
            this.root.deepMarkSizeInvalid();
        }
        if (!this.isFixedRowHeight() && this.visibleNodes.size() > 0) {
            this.updateNodeSizes(true);
        }
    }
    
    @Override
    public boolean isExpanded(final TreePath treePath) {
        if (treePath != null) {
            final TreeStateNode nodeForPath = this.getNodeForPath(treePath, true, false);
            return nodeForPath != null && nodeForPath.isExpanded();
        }
        return false;
    }
    
    @Override
    public void treeNodesChanged(final TreeModelEvent treeModelEvent) {
        if (treeModelEvent != null) {
            final int[] childIndices = treeModelEvent.getChildIndices();
            final TreeStateNode nodeForPath = this.getNodeForPath(SwingUtilities2.getTreePath(treeModelEvent, this.getModel()), false, false);
            if (nodeForPath != null) {
                final Object value = nodeForPath.getValue();
                nodeForPath.updatePreferredSize();
                if (nodeForPath.hasBeenExpanded() && childIndices != null) {
                    for (int i = 0; i < childIndices.length; ++i) {
                        final TreeStateNode treeStateNode = (TreeStateNode)nodeForPath.getChildAt(childIndices[i]);
                        treeStateNode.setUserObject(this.treeModel.getChild(value, childIndices[i]));
                        treeStateNode.updatePreferredSize();
                    }
                }
                else if (nodeForPath == this.root) {
                    nodeForPath.updatePreferredSize();
                }
                if (!this.isFixedRowHeight()) {
                    final int row = nodeForPath.getRow();
                    if (row != -1) {
                        this.updateYLocationsFrom(row);
                    }
                }
                this.visibleNodesChanged();
            }
        }
    }
    
    @Override
    public void treeNodesInserted(final TreeModelEvent treeModelEvent) {
        if (treeModelEvent != null) {
            final int[] childIndices = treeModelEvent.getChildIndices();
            final TreeStateNode nodeForPath = this.getNodeForPath(SwingUtilities2.getTreePath(treeModelEvent, this.getModel()), false, false);
            if (nodeForPath != null && childIndices != null && childIndices.length > 0) {
                if (nodeForPath.hasBeenExpanded()) {
                    final int childCount = nodeForPath.getChildCount();
                    nodeForPath.getValue();
                    final boolean b = (nodeForPath == this.root && !this.rootVisible) || (nodeForPath.getRow() != -1 && nodeForPath.isExpanded());
                    for (int i = 0; i < childIndices.length; ++i) {
                        this.createNodeAt(nodeForPath, childIndices[i]);
                    }
                    if (childCount == 0) {
                        nodeForPath.updatePreferredSize();
                    }
                    if (this.treeSelectionModel != null) {
                        this.treeSelectionModel.resetRowSelection();
                    }
                    if (!this.isFixedRowHeight() && (b || (childCount == 0 && nodeForPath.isVisible()))) {
                        if (nodeForPath == this.root) {
                            this.updateYLocationsFrom(0);
                        }
                        else {
                            this.updateYLocationsFrom(nodeForPath.getRow());
                        }
                        this.visibleNodesChanged();
                    }
                    else if (b) {
                        this.visibleNodesChanged();
                    }
                }
                else if (this.treeModel.getChildCount(nodeForPath.getValue()) - childIndices.length == 0) {
                    nodeForPath.updatePreferredSize();
                    if (!this.isFixedRowHeight() && nodeForPath.isVisible()) {
                        this.updateYLocationsFrom(nodeForPath.getRow());
                    }
                }
            }
        }
    }
    
    @Override
    public void treeNodesRemoved(final TreeModelEvent treeModelEvent) {
        if (treeModelEvent != null) {
            final int[] childIndices = treeModelEvent.getChildIndices();
            final TreeStateNode nodeForPath = this.getNodeForPath(SwingUtilities2.getTreePath(treeModelEvent, this.getModel()), false, false);
            if (nodeForPath != null && childIndices != null && childIndices.length > 0) {
                if (nodeForPath.hasBeenExpanded()) {
                    final boolean b = (nodeForPath == this.root && !this.rootVisible) || (nodeForPath.getRow() != -1 && nodeForPath.isExpanded());
                    for (int i = childIndices.length - 1; i >= 0; --i) {
                        final TreeStateNode treeStateNode = (TreeStateNode)nodeForPath.getChildAt(childIndices[i]);
                        if (treeStateNode.isExpanded()) {
                            treeStateNode.collapse(false);
                        }
                        if (b) {
                            final int row = treeStateNode.getRow();
                            if (row != -1) {
                                this.visibleNodes.removeElementAt(row);
                            }
                        }
                        nodeForPath.remove(childIndices[i]);
                    }
                    if (nodeForPath.getChildCount() == 0) {
                        nodeForPath.updatePreferredSize();
                        if (nodeForPath.isExpanded() && nodeForPath.isLeaf()) {
                            nodeForPath.collapse(false);
                        }
                    }
                    if (this.treeSelectionModel != null) {
                        this.treeSelectionModel.resetRowSelection();
                    }
                    if (!this.isFixedRowHeight() && (b || (nodeForPath.getChildCount() == 0 && nodeForPath.isVisible()))) {
                        if (nodeForPath == this.root) {
                            if (this.getRowCount() > 0) {
                                this.getNode(0).setYOrigin(0);
                            }
                            this.updateYLocationsFrom(0);
                        }
                        else {
                            this.updateYLocationsFrom(nodeForPath.getRow());
                        }
                        this.visibleNodesChanged();
                    }
                    else if (b) {
                        this.visibleNodesChanged();
                    }
                }
                else if (this.treeModel.getChildCount(nodeForPath.getValue()) == 0) {
                    nodeForPath.updatePreferredSize();
                    if (!this.isFixedRowHeight() && nodeForPath.isVisible()) {
                        this.updateYLocationsFrom(nodeForPath.getRow());
                    }
                }
            }
        }
    }
    
    @Override
    public void treeStructureChanged(final TreeModelEvent treeModelEvent) {
        if (treeModelEvent != null) {
            final TreePath treePath = SwingUtilities2.getTreePath(treeModelEvent, this.getModel());
            final TreeStateNode nodeForPath = this.getNodeForPath(treePath, false, false);
            if (nodeForPath == this.root || (nodeForPath == null && ((treePath == null && this.treeModel != null && this.treeModel.getRoot() == null) || (treePath != null && treePath.getPathCount() == 1)))) {
                this.rebuild(true);
            }
            else if (nodeForPath != null) {
                final boolean expanded = nodeForPath.isExpanded();
                final boolean b = nodeForPath.getRow() != -1;
                final TreeStateNode treeStateNode = (TreeStateNode)nodeForPath.getParent();
                final int index = treeStateNode.getIndex(nodeForPath);
                if (b && expanded) {
                    nodeForPath.collapse(false);
                }
                if (b) {
                    this.visibleNodes.removeElement(nodeForPath);
                }
                nodeForPath.removeFromParent();
                this.createNodeAt(treeStateNode, index);
                final TreeStateNode treeStateNode2 = (TreeStateNode)treeStateNode.getChildAt(index);
                if (b && expanded) {
                    treeStateNode2.expand(false);
                }
                final int row = treeStateNode2.getRow();
                if (!this.isFixedRowHeight() && b) {
                    if (row == 0) {
                        this.updateYLocationsFrom(row);
                    }
                    else {
                        this.updateYLocationsFrom(row - 1);
                    }
                    this.visibleNodesChanged();
                }
                else if (b) {
                    this.visibleNodesChanged();
                }
            }
        }
    }
    
    private void visibleNodesChanged() {
    }
    
    private void addMapping(final TreeStateNode treeStateNode) {
        this.treePathMapping.put(treeStateNode.getTreePath(), treeStateNode);
    }
    
    private void removeMapping(final TreeStateNode treeStateNode) {
        this.treePathMapping.remove(treeStateNode.getTreePath());
    }
    
    private TreeStateNode getMapping(final TreePath treePath) {
        return this.treePathMapping.get(treePath);
    }
    
    private Rectangle getBounds(final int n, final Rectangle rectangle) {
        if (this.updateNodeSizes) {
            this.updateNodeSizes(false);
        }
        if (n >= 0 && n < this.getRowCount()) {
            return this.getNode(n).getNodeBounds(rectangle);
        }
        return null;
    }
    
    private void rebuild(final boolean b) {
        this.treePathMapping.clear();
        final Object root;
        if (this.treeModel != null && (root = this.treeModel.getRoot()) != null) {
            this.root = this.createNodeForValue(root);
            this.root.path = new TreePath(root);
            this.addMapping(this.root);
            this.root.updatePreferredSize(0);
            this.visibleNodes.removeAllElements();
            if (this.isRootVisible()) {
                this.visibleNodes.addElement(this.root);
            }
            if (!this.root.isExpanded()) {
                this.root.expand();
            }
            else {
                final Enumeration children = this.root.children();
                while (children.hasMoreElements()) {
                    this.visibleNodes.addElement(children.nextElement());
                }
                if (!this.isFixedRowHeight()) {
                    this.updateYLocationsFrom(0);
                }
            }
        }
        else {
            this.visibleNodes.removeAllElements();
            this.root = null;
        }
        if (b && this.treeSelectionModel != null) {
            this.treeSelectionModel.clearSelection();
        }
        this.visibleNodesChanged();
    }
    
    private TreeStateNode createNodeAt(final TreeStateNode treeStateNode, final int n) {
        final TreeStateNode nodeForValue = this.createNodeForValue(this.treeModel.getChild(treeStateNode.getValue(), n));
        treeStateNode.insert(nodeForValue, n);
        nodeForValue.updatePreferredSize(-1);
        final boolean b = treeStateNode == this.root;
        if (nodeForValue != null && treeStateNode.isExpanded() && (treeStateNode.getRow() != -1 || b)) {
            int n2;
            if (n == 0) {
                if (b && !this.isRootVisible()) {
                    n2 = 0;
                }
                else {
                    n2 = treeStateNode.getRow() + 1;
                }
            }
            else if (n == treeStateNode.getChildCount()) {
                n2 = treeStateNode.getLastVisibleNode().getRow() + 1;
            }
            else {
                n2 = ((TreeStateNode)treeStateNode.getChildAt(n - 1)).getLastVisibleNode().getRow() + 1;
            }
            this.visibleNodes.insertElementAt(nodeForValue, n2);
        }
        return nodeForValue;
    }
    
    private TreeStateNode getNodeForPath(TreePath treePath, final boolean b, final boolean b2) {
        if (treePath != null) {
            final TreeStateNode mapping = this.getMapping(treePath);
            if (mapping != null) {
                if (b && !mapping.isVisible()) {
                    return null;
                }
                return mapping;
            }
            else {
                Stack stack;
                if (this.tempStacks.size() == 0) {
                    stack = new Stack();
                }
                else {
                    stack = this.tempStacks.pop();
                }
                try {
                    stack.push(treePath);
                    TreeStateNode mapping2;
                    int indexOfChild;
                    for (treePath = treePath.getParentPath(); treePath != null; treePath = treePath.getParentPath()) {
                        mapping2 = this.getMapping(treePath);
                        if (mapping2 != null) {
                            while (mapping2 != null && stack.size() > 0) {
                                treePath = (TreePath)stack.pop();
                                mapping2.getLoadedChildren(b2);
                                indexOfChild = this.treeModel.getIndexOfChild(mapping2.getUserObject(), treePath.getLastPathComponent());
                                if (indexOfChild == -1 || indexOfChild >= mapping2.getChildCount() || (b && !mapping2.isVisible())) {
                                    mapping2 = null;
                                }
                                else {
                                    mapping2 = (TreeStateNode)mapping2.getChildAt(indexOfChild);
                                }
                            }
                            return mapping2;
                        }
                        stack.push(treePath);
                    }
                }
                finally {
                    stack.removeAllElements();
                    this.tempStacks.push(stack);
                }
            }
        }
        return null;
    }
    
    private void updateYLocationsFrom(final int n) {
        if (n >= 0 && n < this.getRowCount()) {
            final TreeStateNode node = this.getNode(n);
            int yOrigin = node.getYOrigin() + node.getPreferredHeight();
            for (int i = n + 1; i < this.visibleNodes.size(); ++i) {
                final TreeStateNode treeStateNode = this.visibleNodes.elementAt(i);
                treeStateNode.setYOrigin(yOrigin);
                yOrigin += treeStateNode.getPreferredHeight();
            }
        }
    }
    
    private void updateNodeSizes(final boolean b) {
        this.updateNodeSizes = false;
        int yOrigin;
        for (int i = yOrigin = 0; i < this.visibleNodes.size(); ++i) {
            final TreeStateNode treeStateNode = this.visibleNodes.elementAt(i);
            treeStateNode.setYOrigin(yOrigin);
            if (b || !treeStateNode.hasValidSize()) {
                treeStateNode.updatePreferredSize(i);
            }
            yOrigin += treeStateNode.getPreferredHeight();
        }
    }
    
    private int getRowContainingYLocation(final int n) {
        if (this.isFixedRowHeight()) {
            if (this.getRowCount() == 0) {
                return -1;
            }
            return Math.max(0, Math.min(this.getRowCount() - 1, n / this.getRowHeight()));
        }
        else {
            int rowCount;
            if ((rowCount = this.getRowCount()) <= 0) {
                return -1;
            }
            int n2;
            int i = n2 = 0;
            while (i < rowCount) {
                n2 = (rowCount - i) / 2 + i;
                final TreeStateNode treeStateNode = this.visibleNodes.elementAt(n2);
                final int yOrigin = treeStateNode.getYOrigin();
                final int n3 = yOrigin + treeStateNode.getPreferredHeight();
                if (n < yOrigin) {
                    rowCount = n2 - 1;
                }
                else {
                    if (n < n3) {
                        break;
                    }
                    i = n2 + 1;
                }
            }
            if (i == rowCount) {
                n2 = i;
                if (n2 >= this.getRowCount()) {
                    n2 = this.getRowCount() - 1;
                }
            }
            return n2;
        }
    }
    
    private void ensurePathIsExpanded(TreePath parentPath, boolean b) {
        if (parentPath != null) {
            if (this.treeModel.isLeaf(parentPath.getLastPathComponent())) {
                parentPath = parentPath.getParentPath();
                b = true;
            }
            if (parentPath != null) {
                final TreeStateNode nodeForPath = this.getNodeForPath(parentPath, false, true);
                if (nodeForPath != null) {
                    nodeForPath.makeVisible();
                    if (b) {
                        nodeForPath.expand();
                    }
                }
            }
        }
    }
    
    private TreeStateNode getNode(final int n) {
        return this.visibleNodes.elementAt(n);
    }
    
    private int getMaxNodeWidth() {
        int n = 0;
        for (int i = this.getRowCount() - 1; i >= 0; --i) {
            final TreeStateNode node = this.getNode(i);
            final int n2 = node.getPreferredWidth() + node.getXOrigin();
            if (n2 > n) {
                n = n2;
            }
        }
        return n;
    }
    
    private TreeStateNode createNodeForValue(final Object o) {
        return new TreeStateNode(o);
    }
    
    private class TreeStateNode extends DefaultMutableTreeNode
    {
        protected int preferredWidth;
        protected int preferredHeight;
        protected int xOrigin;
        protected int yOrigin;
        protected boolean expanded;
        protected boolean hasBeenExpanded;
        protected TreePath path;
        
        public TreeStateNode(final Object o) {
            super(o);
        }
        
        @Override
        public void setParent(final MutableTreeNode parent) {
            super.setParent(parent);
            if (parent != null) {
                this.path = ((TreeStateNode)parent).getTreePath().pathByAddingChild(this.getUserObject());
                VariableHeightLayoutCache.this.addMapping(this);
            }
        }
        
        @Override
        public void remove(final int n) {
            ((TreeStateNode)this.getChildAt(n)).removeFromMapping();
            super.remove(n);
        }
        
        @Override
        public void setUserObject(final Object userObject) {
            super.setUserObject(userObject);
            if (this.path != null) {
                final TreeStateNode treeStateNode = (TreeStateNode)this.getParent();
                if (treeStateNode != null) {
                    this.resetChildrenPaths(treeStateNode.getTreePath());
                }
                else {
                    this.resetChildrenPaths(null);
                }
            }
        }
        
        @Override
        public Enumeration children() {
            if (!this.isExpanded()) {
                return DefaultMutableTreeNode.EMPTY_ENUMERATION;
            }
            return super.children();
        }
        
        @Override
        public boolean isLeaf() {
            return VariableHeightLayoutCache.this.getModel().isLeaf(this.getValue());
        }
        
        public Rectangle getNodeBounds(Rectangle rectangle) {
            if (rectangle == null) {
                rectangle = new Rectangle(this.getXOrigin(), this.getYOrigin(), this.getPreferredWidth(), this.getPreferredHeight());
            }
            else {
                rectangle.x = this.getXOrigin();
                rectangle.y = this.getYOrigin();
                rectangle.width = this.getPreferredWidth();
                rectangle.height = this.getPreferredHeight();
            }
            return rectangle;
        }
        
        public int getXOrigin() {
            if (!this.hasValidSize()) {
                this.updatePreferredSize(this.getRow());
            }
            return this.xOrigin;
        }
        
        public int getYOrigin() {
            if (!VariableHeightLayoutCache.this.isFixedRowHeight()) {
                return this.yOrigin;
            }
            final int row = this.getRow();
            if (row == -1) {
                return -1;
            }
            return VariableHeightLayoutCache.this.getRowHeight() * row;
        }
        
        public int getPreferredHeight() {
            if (VariableHeightLayoutCache.this.isFixedRowHeight()) {
                return VariableHeightLayoutCache.this.getRowHeight();
            }
            if (!this.hasValidSize()) {
                this.updatePreferredSize(this.getRow());
            }
            return this.preferredHeight;
        }
        
        public int getPreferredWidth() {
            if (!this.hasValidSize()) {
                this.updatePreferredSize(this.getRow());
            }
            return this.preferredWidth;
        }
        
        public boolean hasValidSize() {
            return this.preferredHeight != 0;
        }
        
        public int getRow() {
            return VariableHeightLayoutCache.this.visibleNodes.indexOf(this);
        }
        
        public boolean hasBeenExpanded() {
            return this.hasBeenExpanded;
        }
        
        public boolean isExpanded() {
            return this.expanded;
        }
        
        public TreeStateNode getLastVisibleNode() {
            TreeStateNode treeStateNode;
            for (treeStateNode = this; treeStateNode.isExpanded() && treeStateNode.getChildCount() > 0; treeStateNode = (TreeStateNode)treeStateNode.getLastChild()) {}
            return treeStateNode;
        }
        
        public boolean isVisible() {
            if (this == VariableHeightLayoutCache.this.root) {
                return true;
            }
            final TreeStateNode treeStateNode = (TreeStateNode)this.getParent();
            return treeStateNode != null && treeStateNode.isExpanded() && treeStateNode.isVisible();
        }
        
        public int getModelChildCount() {
            if (this.hasBeenExpanded) {
                return super.getChildCount();
            }
            return VariableHeightLayoutCache.this.getModel().getChildCount(this.getValue());
        }
        
        public int getVisibleChildCount() {
            int n = 0;
            if (this.isExpanded()) {
                final int childCount = this.getChildCount();
                n += childCount;
                for (int i = 0; i < childCount; ++i) {
                    n += ((TreeStateNode)this.getChildAt(i)).getVisibleChildCount();
                }
            }
            return n;
        }
        
        public void toggleExpanded() {
            if (this.isExpanded()) {
                this.collapse();
            }
            else {
                this.expand();
            }
        }
        
        public void makeVisible() {
            final TreeStateNode treeStateNode = (TreeStateNode)this.getParent();
            if (treeStateNode != null) {
                treeStateNode.expandParentAndReceiver();
            }
        }
        
        public void expand() {
            this.expand(true);
        }
        
        public void collapse() {
            this.collapse(true);
        }
        
        public Object getValue() {
            return this.getUserObject();
        }
        
        public TreePath getTreePath() {
            return this.path;
        }
        
        protected void resetChildrenPaths(final TreePath treePath) {
            VariableHeightLayoutCache.this.removeMapping(this);
            if (treePath == null) {
                this.path = new TreePath(this.getUserObject());
            }
            else {
                this.path = treePath.pathByAddingChild(this.getUserObject());
            }
            VariableHeightLayoutCache.this.addMapping(this);
            for (int i = this.getChildCount() - 1; i >= 0; --i) {
                ((TreeStateNode)this.getChildAt(i)).resetChildrenPaths(this.path);
            }
        }
        
        protected void setYOrigin(final int yOrigin) {
            this.yOrigin = yOrigin;
        }
        
        protected void shiftYOriginBy(final int n) {
            this.yOrigin += n;
        }
        
        protected void updatePreferredSize() {
            this.updatePreferredSize(this.getRow());
        }
        
        protected void updatePreferredSize(final int n) {
            final Rectangle nodeDimensions = VariableHeightLayoutCache.this.getNodeDimensions(this.getUserObject(), n, this.getLevel(), this.isExpanded(), VariableHeightLayoutCache.this.boundsBuffer);
            if (nodeDimensions == null) {
                this.xOrigin = 0;
                final int n2 = 0;
                this.preferredHeight = n2;
                this.preferredWidth = n2;
                VariableHeightLayoutCache.this.updateNodeSizes = true;
            }
            else if (nodeDimensions.height == 0) {
                this.xOrigin = 0;
                final int n3 = 0;
                this.preferredHeight = n3;
                this.preferredWidth = n3;
                VariableHeightLayoutCache.this.updateNodeSizes = true;
            }
            else {
                this.xOrigin = nodeDimensions.x;
                this.preferredWidth = nodeDimensions.width;
                if (VariableHeightLayoutCache.this.isFixedRowHeight()) {
                    this.preferredHeight = VariableHeightLayoutCache.this.getRowHeight();
                }
                else {
                    this.preferredHeight = nodeDimensions.height;
                }
            }
        }
        
        protected void markSizeInvalid() {
            this.preferredHeight = 0;
        }
        
        protected void deepMarkSizeInvalid() {
            this.markSizeInvalid();
            for (int i = this.getChildCount() - 1; i >= 0; --i) {
                ((TreeStateNode)this.getChildAt(i)).deepMarkSizeInvalid();
            }
        }
        
        protected Enumeration getLoadedChildren(final boolean b) {
            if (!b || this.hasBeenExpanded) {
                return super.children();
            }
            final Object value = this.getValue();
            final TreeModel model = VariableHeightLayoutCache.this.getModel();
            final int childCount = model.getChildCount(value);
            this.hasBeenExpanded = true;
            int row = this.getRow();
            if (row == -1) {
                for (int i = 0; i < childCount; ++i) {
                    final TreeStateNode access$600 = VariableHeightLayoutCache.this.createNodeForValue(model.getChild(value, i));
                    this.add(access$600);
                    access$600.updatePreferredSize(-1);
                }
            }
            else {
                ++row;
                for (int j = 0; j < childCount; ++j) {
                    final TreeStateNode access$601 = VariableHeightLayoutCache.this.createNodeForValue(model.getChild(value, j));
                    this.add(access$601);
                    access$601.updatePreferredSize(row++);
                }
            }
            return super.children();
        }
        
        protected void didAdjustTree() {
        }
        
        protected void expandParentAndReceiver() {
            final TreeStateNode treeStateNode = (TreeStateNode)this.getParent();
            if (treeStateNode != null) {
                treeStateNode.expandParentAndReceiver();
            }
            this.expand();
        }
        
        protected void expand(final boolean b) {
            if (!this.isExpanded() && !this.isLeaf()) {
                final boolean fixedRowHeight = VariableHeightLayoutCache.this.isFixedRowHeight();
                final int preferredHeight = this.getPreferredHeight();
                final int row = this.getRow();
                this.expanded = true;
                this.updatePreferredSize(row);
                if (!this.hasBeenExpanded) {
                    final Object value = this.getValue();
                    final TreeModel model = VariableHeightLayoutCache.this.getModel();
                    final int childCount = model.getChildCount(value);
                    this.hasBeenExpanded = true;
                    if (row == -1) {
                        for (int i = 0; i < childCount; ++i) {
                            final TreeStateNode access$600 = VariableHeightLayoutCache.this.createNodeForValue(model.getChild(value, i));
                            this.add(access$600);
                            access$600.updatePreferredSize(-1);
                        }
                    }
                    else {
                        final int n = row + 1;
                        for (int j = 0; j < childCount; ++j) {
                            final TreeStateNode access$601 = VariableHeightLayoutCache.this.createNodeForValue(model.getChild(value, j));
                            this.add(access$601);
                            access$601.updatePreferredSize(n);
                        }
                    }
                }
                int n2 = row;
                final Enumeration preorderEnumeration = this.preorderEnumeration();
                preorderEnumeration.nextElement();
                int yOrigin;
                if (fixedRowHeight) {
                    yOrigin = 0;
                }
                else if (this == VariableHeightLayoutCache.this.root && !VariableHeightLayoutCache.this.isRootVisible()) {
                    yOrigin = 0;
                }
                else {
                    yOrigin = this.getYOrigin() + this.getPreferredHeight();
                }
                if (!fixedRowHeight) {
                    while (preorderEnumeration.hasMoreElements()) {
                        final TreeStateNode treeStateNode = preorderEnumeration.nextElement();
                        if (!VariableHeightLayoutCache.this.updateNodeSizes && !treeStateNode.hasValidSize()) {
                            treeStateNode.updatePreferredSize(n2 + 1);
                        }
                        treeStateNode.setYOrigin(yOrigin);
                        yOrigin += treeStateNode.getPreferredHeight();
                        VariableHeightLayoutCache.this.visibleNodes.insertElementAt(treeStateNode, ++n2);
                    }
                }
                else {
                    while (preorderEnumeration.hasMoreElements()) {
                        VariableHeightLayoutCache.this.visibleNodes.insertElementAt(preorderEnumeration.nextElement(), ++n2);
                    }
                }
                if (b && (row != n2 || this.getPreferredHeight() != preferredHeight)) {
                    if (!fixedRowHeight && ++n2 < VariableHeightLayoutCache.this.getRowCount()) {
                        final int n3 = yOrigin - (this.getYOrigin() + this.getPreferredHeight()) + (this.getPreferredHeight() - preferredHeight);
                        for (int k = VariableHeightLayoutCache.this.visibleNodes.size() - 1; k >= n2; --k) {
                            ((TreeStateNode)VariableHeightLayoutCache.this.visibleNodes.elementAt(k)).shiftYOriginBy(n3);
                        }
                    }
                    this.didAdjustTree();
                    VariableHeightLayoutCache.this.visibleNodesChanged();
                }
                if (VariableHeightLayoutCache.this.treeSelectionModel != null) {
                    VariableHeightLayoutCache.this.treeSelectionModel.resetRowSelection();
                }
            }
        }
        
        protected void collapse(final boolean b) {
            if (this.isExpanded()) {
                final Enumeration preorderEnumeration = this.preorderEnumeration();
                preorderEnumeration.nextElement();
                int n = 0;
                final boolean fixedRowHeight = VariableHeightLayoutCache.this.isFixedRowHeight();
                int n2;
                if (fixedRowHeight) {
                    n2 = 0;
                }
                else {
                    n2 = this.getPreferredHeight() + this.getYOrigin();
                }
                final int preferredHeight = this.getPreferredHeight();
                final int n3 = n2;
                final int row = this.getRow();
                if (!fixedRowHeight) {
                    while (preorderEnumeration.hasMoreElements()) {
                        final TreeStateNode treeStateNode = preorderEnumeration.nextElement();
                        if (treeStateNode.isVisible()) {
                            ++n;
                            n2 = treeStateNode.getYOrigin() + treeStateNode.getPreferredHeight();
                        }
                    }
                }
                else {
                    while (preorderEnumeration.hasMoreElements()) {
                        if (((TreeStateNode)preorderEnumeration.nextElement()).isVisible()) {
                            ++n;
                        }
                    }
                }
                for (int i = n + row; i > row; --i) {
                    VariableHeightLayoutCache.this.visibleNodes.removeElementAt(i);
                }
                this.expanded = false;
                if (row == -1) {
                    this.markSizeInvalid();
                }
                else if (b) {
                    this.updatePreferredSize(row);
                }
                if (row != -1 && b && (n > 0 || preferredHeight != this.getPreferredHeight())) {
                    final int n4 = n3 + (this.getPreferredHeight() - preferredHeight);
                    if (!fixedRowHeight && row + 1 < VariableHeightLayoutCache.this.getRowCount() && n4 != n2) {
                        final int n5 = n4 - n2;
                        for (int j = row + 1; j < VariableHeightLayoutCache.this.visibleNodes.size(); ++j) {
                            ((TreeStateNode)VariableHeightLayoutCache.this.visibleNodes.elementAt(j)).shiftYOriginBy(n5);
                        }
                    }
                    this.didAdjustTree();
                    VariableHeightLayoutCache.this.visibleNodesChanged();
                }
                if (VariableHeightLayoutCache.this.treeSelectionModel != null && n > 0 && row != -1) {
                    VariableHeightLayoutCache.this.treeSelectionModel.resetRowSelection();
                }
            }
        }
        
        protected void removeFromMapping() {
            if (this.path != null) {
                VariableHeightLayoutCache.this.removeMapping(this);
                for (int i = this.getChildCount() - 1; i >= 0; --i) {
                    ((TreeStateNode)this.getChildAt(i)).removeFromMapping();
                }
            }
        }
    }
    
    private class VisibleTreeStateNodeEnumeration implements Enumeration<TreePath>
    {
        protected TreeStateNode parent;
        protected int nextIndex;
        protected int childCount;
        
        protected VisibleTreeStateNodeEnumeration(final VariableHeightLayoutCache variableHeightLayoutCache, final TreeStateNode treeStateNode) {
            this(variableHeightLayoutCache, treeStateNode, -1);
        }
        
        protected VisibleTreeStateNodeEnumeration(final TreeStateNode parent, final int nextIndex) {
            this.parent = parent;
            this.nextIndex = nextIndex;
            this.childCount = this.parent.getChildCount();
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
                treePath = ((TreeStateNode)this.parent.getChildAt(this.nextIndex)).getTreePath();
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
            if (this.parent == VariableHeightLayoutCache.this.root) {
                this.parent = null;
                return false;
            }
            while (this.parent != null) {
                final TreeStateNode parent = (TreeStateNode)this.parent.getParent();
                if (parent != null) {
                    this.nextIndex = parent.getIndex(this.parent);
                    this.parent = parent;
                    this.childCount = this.parent.getChildCount();
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
            final TreeStateNode parent = (TreeStateNode)this.parent.getChildAt(this.nextIndex);
            if (parent != null && parent.isExpanded()) {
                this.parent = parent;
                this.nextIndex = -1;
                this.childCount = parent.getChildCount();
            }
            return true;
        }
    }
}
