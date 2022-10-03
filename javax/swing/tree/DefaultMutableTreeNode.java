package javax.swing.tree;

import java.util.EmptyStackException;
import java.util.Stack;
import java.util.Collections;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.NoSuchElementException;
import java.beans.Transient;
import java.util.Vector;
import java.util.Enumeration;
import java.io.Serializable;

public class DefaultMutableTreeNode implements Cloneable, MutableTreeNode, Serializable
{
    private static final long serialVersionUID = -4298474751201349152L;
    public static final Enumeration<TreeNode> EMPTY_ENUMERATION;
    protected MutableTreeNode parent;
    protected Vector children;
    protected transient Object userObject;
    protected boolean allowsChildren;
    
    public DefaultMutableTreeNode() {
        this(null);
    }
    
    public DefaultMutableTreeNode(final Object o) {
        this(o, true);
    }
    
    public DefaultMutableTreeNode(final Object userObject, final boolean allowsChildren) {
        this.parent = null;
        this.allowsChildren = allowsChildren;
        this.userObject = userObject;
    }
    
    @Override
    public void insert(final MutableTreeNode mutableTreeNode, final int n) {
        if (!this.allowsChildren) {
            throw new IllegalStateException("node does not allow children");
        }
        if (mutableTreeNode == null) {
            throw new IllegalArgumentException("new child is null");
        }
        if (this.isNodeAncestor(mutableTreeNode)) {
            throw new IllegalArgumentException("new child is an ancestor");
        }
        final MutableTreeNode mutableTreeNode2 = (MutableTreeNode)mutableTreeNode.getParent();
        if (mutableTreeNode2 != null) {
            mutableTreeNode2.remove(mutableTreeNode);
        }
        mutableTreeNode.setParent(this);
        if (this.children == null) {
            this.children = new Vector();
        }
        this.children.insertElementAt(mutableTreeNode, n);
    }
    
    @Override
    public void remove(final int n) {
        final MutableTreeNode mutableTreeNode = (MutableTreeNode)this.getChildAt(n);
        this.children.removeElementAt(n);
        mutableTreeNode.setParent(null);
    }
    
    @Transient
    @Override
    public void setParent(final MutableTreeNode parent) {
        this.parent = parent;
    }
    
    @Override
    public TreeNode getParent() {
        return this.parent;
    }
    
    @Override
    public TreeNode getChildAt(final int n) {
        if (this.children == null) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }
        return this.children.elementAt(n);
    }
    
    @Override
    public int getChildCount() {
        if (this.children == null) {
            return 0;
        }
        return this.children.size();
    }
    
    @Override
    public int getIndex(final TreeNode treeNode) {
        if (treeNode == null) {
            throw new IllegalArgumentException("argument is null");
        }
        if (!this.isNodeChild(treeNode)) {
            return -1;
        }
        return this.children.indexOf(treeNode);
    }
    
    @Override
    public Enumeration children() {
        if (this.children == null) {
            return DefaultMutableTreeNode.EMPTY_ENUMERATION;
        }
        return this.children.elements();
    }
    
    public void setAllowsChildren(final boolean allowsChildren) {
        if (allowsChildren != this.allowsChildren && !(this.allowsChildren = allowsChildren)) {
            this.removeAllChildren();
        }
    }
    
    @Override
    public boolean getAllowsChildren() {
        return this.allowsChildren;
    }
    
    @Override
    public void setUserObject(final Object userObject) {
        this.userObject = userObject;
    }
    
    public Object getUserObject() {
        return this.userObject;
    }
    
    @Override
    public void removeFromParent() {
        final MutableTreeNode mutableTreeNode = (MutableTreeNode)this.getParent();
        if (mutableTreeNode != null) {
            mutableTreeNode.remove(this);
        }
    }
    
    @Override
    public void remove(final MutableTreeNode mutableTreeNode) {
        if (mutableTreeNode == null) {
            throw new IllegalArgumentException("argument is null");
        }
        if (!this.isNodeChild(mutableTreeNode)) {
            throw new IllegalArgumentException("argument is not a child");
        }
        this.remove(this.getIndex(mutableTreeNode));
    }
    
    public void removeAllChildren() {
        for (int i = this.getChildCount() - 1; i >= 0; --i) {
            this.remove(i);
        }
    }
    
    public void add(final MutableTreeNode mutableTreeNode) {
        if (mutableTreeNode != null && mutableTreeNode.getParent() == this) {
            this.insert(mutableTreeNode, this.getChildCount() - 1);
        }
        else {
            this.insert(mutableTreeNode, this.getChildCount());
        }
    }
    
    public boolean isNodeAncestor(final TreeNode treeNode) {
        if (treeNode == null) {
            return false;
        }
        TreeNode parent = this;
        while (parent != treeNode) {
            if ((parent = parent.getParent()) == null) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isNodeDescendant(final DefaultMutableTreeNode defaultMutableTreeNode) {
        return defaultMutableTreeNode != null && defaultMutableTreeNode.isNodeAncestor(this);
    }
    
    public TreeNode getSharedAncestor(final DefaultMutableTreeNode defaultMutableTreeNode) {
        if (defaultMutableTreeNode == this) {
            return this;
        }
        if (defaultMutableTreeNode == null) {
            return null;
        }
        final int level = this.getLevel();
        final int level2 = defaultMutableTreeNode.getLevel();
        int i;
        TreeNode treeNode;
        TreeNode parent;
        if (level2 > level) {
            i = level2 - level;
            treeNode = defaultMutableTreeNode;
            parent = this;
        }
        else {
            i = level - level2;
            treeNode = this;
            parent = defaultMutableTreeNode;
        }
        while (i > 0) {
            treeNode = treeNode.getParent();
            --i;
        }
        while (treeNode != parent) {
            treeNode = treeNode.getParent();
            parent = parent.getParent();
            if (treeNode == null) {
                if (treeNode != null || parent != null) {
                    throw new Error("nodes should be null");
                }
                return null;
            }
        }
        return treeNode;
    }
    
    public boolean isNodeRelated(final DefaultMutableTreeNode defaultMutableTreeNode) {
        return defaultMutableTreeNode != null && this.getRoot() == defaultMutableTreeNode.getRoot();
    }
    
    public int getDepth() {
        DefaultMutableTreeNode nextElement = null;
        final Enumeration breadthFirstEnumeration = this.breadthFirstEnumeration();
        while (breadthFirstEnumeration.hasMoreElements()) {
            nextElement = (DefaultMutableTreeNode)breadthFirstEnumeration.nextElement();
        }
        if (nextElement == null) {
            throw new Error("nodes should be null");
        }
        return nextElement.getLevel() - this.getLevel();
    }
    
    public int getLevel() {
        int n = 0;
        TreeNode parent = this;
        while ((parent = parent.getParent()) != null) {
            ++n;
        }
        return n;
    }
    
    public TreeNode[] getPath() {
        return this.getPathToRoot(this, 0);
    }
    
    protected TreeNode[] getPathToRoot(final TreeNode treeNode, int n) {
        TreeNode[] pathToRoot;
        if (treeNode == null) {
            if (n == 0) {
                return null;
            }
            pathToRoot = new TreeNode[n];
        }
        else {
            ++n;
            pathToRoot = this.getPathToRoot(treeNode.getParent(), n);
            pathToRoot[pathToRoot.length - n] = treeNode;
        }
        return pathToRoot;
    }
    
    public Object[] getUserObjectPath() {
        final TreeNode[] path = this.getPath();
        final Object[] array = new Object[path.length];
        for (int i = 0; i < path.length; ++i) {
            array[i] = ((DefaultMutableTreeNode)path[i]).getUserObject();
        }
        return array;
    }
    
    public TreeNode getRoot() {
        TreeNode parent = this;
        TreeNode treeNode;
        do {
            treeNode = parent;
            parent = parent.getParent();
        } while (parent != null);
        return treeNode;
    }
    
    public boolean isRoot() {
        return this.getParent() == null;
    }
    
    public DefaultMutableTreeNode getNextNode() {
        if (this.getChildCount() != 0) {
            return (DefaultMutableTreeNode)this.getChildAt(0);
        }
        final DefaultMutableTreeNode nextSibling = this.getNextSibling();
        if (nextSibling == null) {
            for (DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)this.getParent(); defaultMutableTreeNode != null; defaultMutableTreeNode = (DefaultMutableTreeNode)defaultMutableTreeNode.getParent()) {
                final DefaultMutableTreeNode nextSibling2 = defaultMutableTreeNode.getNextSibling();
                if (nextSibling2 != null) {
                    return nextSibling2;
                }
            }
            return null;
        }
        return nextSibling;
    }
    
    public DefaultMutableTreeNode getPreviousNode() {
        final DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)this.getParent();
        if (defaultMutableTreeNode == null) {
            return null;
        }
        final DefaultMutableTreeNode previousSibling = this.getPreviousSibling();
        if (previousSibling == null) {
            return defaultMutableTreeNode;
        }
        if (previousSibling.getChildCount() == 0) {
            return previousSibling;
        }
        return previousSibling.getLastLeaf();
    }
    
    public Enumeration preorderEnumeration() {
        return new PreorderEnumeration(this);
    }
    
    public Enumeration postorderEnumeration() {
        return new PostorderEnumeration(this);
    }
    
    public Enumeration breadthFirstEnumeration() {
        return new BreadthFirstEnumeration(this);
    }
    
    public Enumeration depthFirstEnumeration() {
        return this.postorderEnumeration();
    }
    
    public Enumeration pathFromAncestorEnumeration(final TreeNode treeNode) {
        return new PathBetweenNodesEnumeration(treeNode, this);
    }
    
    public boolean isNodeChild(final TreeNode treeNode) {
        return treeNode != null && this.getChildCount() != 0 && treeNode.getParent() == this;
    }
    
    public TreeNode getFirstChild() {
        if (this.getChildCount() == 0) {
            throw new NoSuchElementException("node has no children");
        }
        return this.getChildAt(0);
    }
    
    public TreeNode getLastChild() {
        if (this.getChildCount() == 0) {
            throw new NoSuchElementException("node has no children");
        }
        return this.getChildAt(this.getChildCount() - 1);
    }
    
    public TreeNode getChildAfter(final TreeNode treeNode) {
        if (treeNode == null) {
            throw new IllegalArgumentException("argument is null");
        }
        final int index = this.getIndex(treeNode);
        if (index == -1) {
            throw new IllegalArgumentException("node is not a child");
        }
        if (index < this.getChildCount() - 1) {
            return this.getChildAt(index + 1);
        }
        return null;
    }
    
    public TreeNode getChildBefore(final TreeNode treeNode) {
        if (treeNode == null) {
            throw new IllegalArgumentException("argument is null");
        }
        final int index = this.getIndex(treeNode);
        if (index == -1) {
            throw new IllegalArgumentException("argument is not a child");
        }
        if (index > 0) {
            return this.getChildAt(index - 1);
        }
        return null;
    }
    
    public boolean isNodeSibling(final TreeNode treeNode) {
        boolean b;
        if (treeNode == null) {
            b = false;
        }
        else if (treeNode == this) {
            b = true;
        }
        else {
            final TreeNode parent = this.getParent();
            b = (parent != null && parent == treeNode.getParent());
            if (b && !((DefaultMutableTreeNode)this.getParent()).isNodeChild(treeNode)) {
                throw new Error("sibling has different parent");
            }
        }
        return b;
    }
    
    public int getSiblingCount() {
        final TreeNode parent = this.getParent();
        if (parent == null) {
            return 1;
        }
        return parent.getChildCount();
    }
    
    public DefaultMutableTreeNode getNextSibling() {
        final DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)this.getParent();
        TreeNode treeNode;
        if (defaultMutableTreeNode == null) {
            treeNode = null;
        }
        else {
            treeNode = defaultMutableTreeNode.getChildAfter(this);
        }
        if (treeNode != null && !this.isNodeSibling(treeNode)) {
            throw new Error("child of parent is not a sibling");
        }
        return (DefaultMutableTreeNode)treeNode;
    }
    
    public DefaultMutableTreeNode getPreviousSibling() {
        final DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)this.getParent();
        TreeNode treeNode;
        if (defaultMutableTreeNode == null) {
            treeNode = null;
        }
        else {
            treeNode = defaultMutableTreeNode.getChildBefore(this);
        }
        if (treeNode != null && !this.isNodeSibling(treeNode)) {
            throw new Error("child of parent is not a sibling");
        }
        return (DefaultMutableTreeNode)treeNode;
    }
    
    @Override
    public boolean isLeaf() {
        return this.getChildCount() == 0;
    }
    
    public DefaultMutableTreeNode getFirstLeaf() {
        DefaultMutableTreeNode defaultMutableTreeNode;
        for (defaultMutableTreeNode = this; !defaultMutableTreeNode.isLeaf(); defaultMutableTreeNode = (DefaultMutableTreeNode)defaultMutableTreeNode.getFirstChild()) {}
        return defaultMutableTreeNode;
    }
    
    public DefaultMutableTreeNode getLastLeaf() {
        DefaultMutableTreeNode defaultMutableTreeNode;
        for (defaultMutableTreeNode = this; !defaultMutableTreeNode.isLeaf(); defaultMutableTreeNode = (DefaultMutableTreeNode)defaultMutableTreeNode.getLastChild()) {}
        return defaultMutableTreeNode;
    }
    
    public DefaultMutableTreeNode getNextLeaf() {
        final DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)this.getParent();
        if (defaultMutableTreeNode == null) {
            return null;
        }
        final DefaultMutableTreeNode nextSibling = this.getNextSibling();
        if (nextSibling != null) {
            return nextSibling.getFirstLeaf();
        }
        return defaultMutableTreeNode.getNextLeaf();
    }
    
    public DefaultMutableTreeNode getPreviousLeaf() {
        final DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)this.getParent();
        if (defaultMutableTreeNode == null) {
            return null;
        }
        final DefaultMutableTreeNode previousSibling = this.getPreviousSibling();
        if (previousSibling != null) {
            return previousSibling.getLastLeaf();
        }
        return defaultMutableTreeNode.getPreviousLeaf();
    }
    
    public int getLeafCount() {
        int n = 0;
        final Enumeration breadthFirstEnumeration = this.breadthFirstEnumeration();
        while (breadthFirstEnumeration.hasMoreElements()) {
            if (((TreeNode)breadthFirstEnumeration.nextElement()).isLeaf()) {
                ++n;
            }
        }
        if (n < 1) {
            throw new Error("tree has zero leaves");
        }
        return n;
    }
    
    @Override
    public String toString() {
        if (this.userObject == null) {
            return "";
        }
        return this.userObject.toString();
    }
    
    public Object clone() {
        DefaultMutableTreeNode defaultMutableTreeNode;
        try {
            defaultMutableTreeNode = (DefaultMutableTreeNode)super.clone();
            defaultMutableTreeNode.children = null;
            defaultMutableTreeNode.parent = null;
        }
        catch (final CloneNotSupportedException ex) {
            throw new Error(ex.toString());
        }
        return defaultMutableTreeNode;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        Object[] array;
        if (this.userObject != null && this.userObject instanceof Serializable) {
            array = new Object[] { "userObject", this.userObject };
        }
        else {
            array = new Object[0];
        }
        objectOutputStream.writeObject(array);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final Object[] array = (Object[])objectInputStream.readObject();
        if (array.length > 0 && array[0].equals("userObject")) {
            this.userObject = array[1];
        }
    }
    
    static {
        EMPTY_ENUMERATION = Collections.emptyEnumeration();
    }
    
    private final class PreorderEnumeration implements Enumeration<TreeNode>
    {
        private final Stack<Enumeration> stack;
        
        public PreorderEnumeration(final TreeNode treeNode) {
            this.stack = new Stack<Enumeration>();
            final Vector vector = new Vector(1);
            vector.addElement(treeNode);
            this.stack.push(vector.elements());
        }
        
        @Override
        public boolean hasMoreElements() {
            return !this.stack.empty() && this.stack.peek().hasMoreElements();
        }
        
        @Override
        public TreeNode nextElement() {
            final Enumeration enumeration = this.stack.peek();
            final TreeNode treeNode = (TreeNode)enumeration.nextElement();
            final Enumeration children = treeNode.children();
            if (!enumeration.hasMoreElements()) {
                this.stack.pop();
            }
            if (children.hasMoreElements()) {
                this.stack.push(children);
            }
            return treeNode;
        }
    }
    
    final class PostorderEnumeration implements Enumeration<TreeNode>
    {
        protected TreeNode root;
        protected Enumeration<TreeNode> children;
        protected Enumeration<TreeNode> subtree;
        
        public PostorderEnumeration(final TreeNode root) {
            this.root = root;
            this.children = this.root.children();
            this.subtree = DefaultMutableTreeNode.EMPTY_ENUMERATION;
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.root != null;
        }
        
        @Override
        public TreeNode nextElement() {
            TreeNode root;
            if (this.subtree.hasMoreElements()) {
                root = this.subtree.nextElement();
            }
            else if (this.children.hasMoreElements()) {
                this.subtree = new PostorderEnumeration(this.children.nextElement());
                root = this.subtree.nextElement();
            }
            else {
                root = this.root;
                this.root = null;
            }
            return root;
        }
    }
    
    final class BreadthFirstEnumeration implements Enumeration<TreeNode>
    {
        protected Queue queue;
        
        public BreadthFirstEnumeration(final TreeNode treeNode) {
            final Vector vector = new Vector(1);
            vector.addElement(treeNode);
            (this.queue = new Queue()).enqueue(vector.elements());
        }
        
        @Override
        public boolean hasMoreElements() {
            return !this.queue.isEmpty() && ((Enumeration)this.queue.firstObject()).hasMoreElements();
        }
        
        @Override
        public TreeNode nextElement() {
            final Enumeration enumeration = (Enumeration)this.queue.firstObject();
            final TreeNode treeNode = enumeration.nextElement();
            final Enumeration children = treeNode.children();
            if (!enumeration.hasMoreElements()) {
                this.queue.dequeue();
            }
            if (children.hasMoreElements()) {
                this.queue.enqueue(children);
            }
            return treeNode;
        }
        
        final class Queue
        {
            QNode head;
            QNode tail;
            
            public void enqueue(final Object o) {
                if (this.head == null) {
                    final QNode qNode = new QNode(o, null);
                    this.tail = qNode;
                    this.head = qNode;
                }
                else {
                    this.tail.next = new QNode(o, null);
                    this.tail = this.tail.next;
                }
            }
            
            public Object dequeue() {
                if (this.head == null) {
                    throw new NoSuchElementException("No more elements");
                }
                final Object object = this.head.object;
                final QNode head = this.head;
                this.head = this.head.next;
                if (this.head == null) {
                    this.tail = null;
                }
                else {
                    head.next = null;
                }
                return object;
            }
            
            public Object firstObject() {
                if (this.head == null) {
                    throw new NoSuchElementException("No more elements");
                }
                return this.head.object;
            }
            
            public boolean isEmpty() {
                return this.head == null;
            }
            
            final class QNode
            {
                public Object object;
                public QNode next;
                
                public QNode(final Object object, final QNode next) {
                    this.object = object;
                    this.next = next;
                }
            }
        }
    }
    
    final class PathBetweenNodesEnumeration implements Enumeration<TreeNode>
    {
        protected Stack<TreeNode> stack;
        
        public PathBetweenNodesEnumeration(final TreeNode treeNode, final TreeNode treeNode2) {
            if (treeNode == null || treeNode2 == null) {
                throw new IllegalArgumentException("argument is null");
            }
            (this.stack = new Stack<TreeNode>()).push(treeNode2);
            TreeNode parent = treeNode2;
            while (parent != treeNode) {
                parent = parent.getParent();
                if (parent == null && treeNode2 != treeNode) {
                    throw new IllegalArgumentException("node " + treeNode + " is not an ancestor of " + treeNode2);
                }
                this.stack.push(parent);
            }
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.stack.size() > 0;
        }
        
        @Override
        public TreeNode nextElement() {
            try {
                return this.stack.pop();
            }
            catch (final EmptyStackException ex) {
                throw new NoSuchElementException("No more elements");
            }
        }
    }
}
