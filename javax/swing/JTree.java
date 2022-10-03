package javax.swing;

import java.awt.event.FocusListener;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Cursor;
import java.awt.Color;
import javax.accessibility.AccessibleValue;
import javax.accessibility.AccessibleText;
import java.util.Locale;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleRole;
import java.beans.PropertyChangeEvent;
import javax.accessibility.AccessibleSelection;
import java.beans.PropertyChangeListener;
import javax.swing.tree.RowMapper;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.accessibility.AccessibleContext;
import javax.swing.event.TreeModelEvent;
import java.awt.Container;
import java.awt.Dimension;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import javax.swing.text.Position;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.ExpandVetoException;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.event.TreeSelectionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.awt.Component;
import sun.awt.AWTAccessor;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import sun.swing.SwingUtilities2;
import java.awt.Point;
import java.awt.HeadlessException;
import java.awt.GraphicsEnvironment;
import javax.accessibility.AccessibleState;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TreeUI;
import java.beans.ConstructorProperties;
import javax.swing.tree.DefaultTreeSelectionModel;
import java.awt.LayoutManager;
import java.util.Vector;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeExpansionListener;
import java.util.Stack;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import java.util.Hashtable;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.accessibility.Accessible;

public class JTree extends JComponent implements Scrollable, Accessible
{
    private static final String uiClassID = "TreeUI";
    protected transient TreeModel treeModel;
    protected transient TreeSelectionModel selectionModel;
    protected boolean rootVisible;
    protected transient TreeCellRenderer cellRenderer;
    protected int rowHeight;
    private boolean rowHeightSet;
    private transient Hashtable<TreePath, Boolean> expandedState;
    protected boolean showsRootHandles;
    private boolean showsRootHandlesSet;
    protected transient TreeSelectionRedirector selectionRedirector;
    protected transient TreeCellEditor cellEditor;
    protected boolean editable;
    protected boolean largeModel;
    protected int visibleRowCount;
    protected boolean invokesStopCellEditing;
    protected boolean scrollsOnExpand;
    private boolean scrollsOnExpandSet;
    protected int toggleClickCount;
    protected transient TreeModelListener treeModelListener;
    private transient Stack<Stack<TreePath>> expandedStack;
    private TreePath leadPath;
    private TreePath anchorPath;
    private boolean expandsSelectedPaths;
    private boolean settingUI;
    private boolean dragEnabled;
    private DropMode dropMode;
    private transient DropLocation dropLocation;
    private int expandRow;
    private TreeTimer dropTimer;
    private transient TreeExpansionListener uiTreeExpansionListener;
    private static int TEMP_STACK_SIZE;
    public static final String CELL_RENDERER_PROPERTY = "cellRenderer";
    public static final String TREE_MODEL_PROPERTY = "model";
    public static final String ROOT_VISIBLE_PROPERTY = "rootVisible";
    public static final String SHOWS_ROOT_HANDLES_PROPERTY = "showsRootHandles";
    public static final String ROW_HEIGHT_PROPERTY = "rowHeight";
    public static final String CELL_EDITOR_PROPERTY = "cellEditor";
    public static final String EDITABLE_PROPERTY = "editable";
    public static final String LARGE_MODEL_PROPERTY = "largeModel";
    public static final String SELECTION_MODEL_PROPERTY = "selectionModel";
    public static final String VISIBLE_ROW_COUNT_PROPERTY = "visibleRowCount";
    public static final String INVOKES_STOP_CELL_EDITING_PROPERTY = "invokesStopCellEditing";
    public static final String SCROLLS_ON_EXPAND_PROPERTY = "scrollsOnExpand";
    public static final String TOGGLE_CLICK_COUNT_PROPERTY = "toggleClickCount";
    public static final String LEAD_SELECTION_PATH_PROPERTY = "leadSelectionPath";
    public static final String ANCHOR_SELECTION_PATH_PROPERTY = "anchorSelectionPath";
    public static final String EXPANDS_SELECTED_PATHS_PROPERTY = "expandsSelectedPaths";
    
    protected static TreeModel getDefaultTreeModel() {
        final DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode("JTree");
        final DefaultMutableTreeNode defaultMutableTreeNode2 = new DefaultMutableTreeNode("colors");
        defaultMutableTreeNode.add(defaultMutableTreeNode2);
        defaultMutableTreeNode2.add(new DefaultMutableTreeNode("blue"));
        defaultMutableTreeNode2.add(new DefaultMutableTreeNode("violet"));
        defaultMutableTreeNode2.add(new DefaultMutableTreeNode("red"));
        defaultMutableTreeNode2.add(new DefaultMutableTreeNode("yellow"));
        final DefaultMutableTreeNode defaultMutableTreeNode3 = new DefaultMutableTreeNode("sports");
        defaultMutableTreeNode.add(defaultMutableTreeNode3);
        defaultMutableTreeNode3.add(new DefaultMutableTreeNode("basketball"));
        defaultMutableTreeNode3.add(new DefaultMutableTreeNode("soccer"));
        defaultMutableTreeNode3.add(new DefaultMutableTreeNode("football"));
        defaultMutableTreeNode3.add(new DefaultMutableTreeNode("hockey"));
        final DefaultMutableTreeNode defaultMutableTreeNode4 = new DefaultMutableTreeNode("food");
        defaultMutableTreeNode.add(defaultMutableTreeNode4);
        defaultMutableTreeNode4.add(new DefaultMutableTreeNode("hot dogs"));
        defaultMutableTreeNode4.add(new DefaultMutableTreeNode("pizza"));
        defaultMutableTreeNode4.add(new DefaultMutableTreeNode("ravioli"));
        defaultMutableTreeNode4.add(new DefaultMutableTreeNode("bananas"));
        return new DefaultTreeModel(defaultMutableTreeNode);
    }
    
    protected static TreeModel createTreeModel(final Object o) {
        DefaultMutableTreeNode defaultMutableTreeNode;
        if (o instanceof Object[] || o instanceof Hashtable || o instanceof Vector) {
            defaultMutableTreeNode = new DefaultMutableTreeNode("root");
            DynamicUtilTreeNode.createChildren(defaultMutableTreeNode, o);
        }
        else {
            defaultMutableTreeNode = new DynamicUtilTreeNode("root", o);
        }
        return new DefaultTreeModel(defaultMutableTreeNode, false);
    }
    
    public JTree() {
        this(getDefaultTreeModel());
    }
    
    public JTree(final Object[] array) {
        this(createTreeModel(array));
        this.setRootVisible(false);
        this.setShowsRootHandles(true);
        this.expandRoot();
    }
    
    public JTree(final Vector<?> vector) {
        this(createTreeModel(vector));
        this.setRootVisible(false);
        this.setShowsRootHandles(true);
        this.expandRoot();
    }
    
    public JTree(final Hashtable<?, ?> hashtable) {
        this(createTreeModel(hashtable));
        this.setRootVisible(false);
        this.setShowsRootHandles(true);
        this.expandRoot();
    }
    
    public JTree(final TreeNode treeNode) {
        this(treeNode, false);
    }
    
    public JTree(final TreeNode treeNode, final boolean b) {
        this(new DefaultTreeModel(treeNode, b));
    }
    
    @ConstructorProperties({ "model" })
    public JTree(final TreeModel model) {
        this.rowHeightSet = false;
        this.showsRootHandlesSet = false;
        this.scrollsOnExpandSet = false;
        this.dropMode = DropMode.USE_SELECTION;
        this.expandRow = -1;
        this.expandedStack = new Stack<Stack<TreePath>>();
        this.toggleClickCount = 2;
        this.expandedState = new Hashtable<TreePath, Boolean>();
        this.setLayout(null);
        this.rowHeight = 16;
        this.visibleRowCount = 20;
        this.rootVisible = true;
        this.selectionModel = new DefaultTreeSelectionModel();
        this.cellRenderer = null;
        this.setOpaque(this.scrollsOnExpand = true);
        this.expandsSelectedPaths = true;
        this.updateUI();
        this.setModel(model);
    }
    
    public TreeUI getUI() {
        return (TreeUI)this.ui;
    }
    
    public void setUI(final TreeUI ui) {
        if (this.ui != ui) {
            this.settingUI = true;
            this.uiTreeExpansionListener = null;
            try {
                super.setUI(ui);
            }
            finally {
                this.settingUI = false;
            }
        }
    }
    
    @Override
    public void updateUI() {
        this.setUI((TreeUI)UIManager.getUI(this));
        SwingUtilities.updateRendererOrEditorUI(this.getCellRenderer());
        SwingUtilities.updateRendererOrEditorUI(this.getCellEditor());
    }
    
    @Override
    public String getUIClassID() {
        return "TreeUI";
    }
    
    public TreeCellRenderer getCellRenderer() {
        return this.cellRenderer;
    }
    
    public void setCellRenderer(final TreeCellRenderer cellRenderer) {
        this.firePropertyChange("cellRenderer", this.cellRenderer, this.cellRenderer = cellRenderer);
        this.invalidate();
    }
    
    public void setEditable(final boolean editable) {
        final boolean editable2 = this.editable;
        this.firePropertyChange("editable", editable2, this.editable = editable);
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleState", editable2 ? AccessibleState.EDITABLE : null, editable ? AccessibleState.EDITABLE : null);
        }
    }
    
    public boolean isEditable() {
        return this.editable;
    }
    
    public void setCellEditor(final TreeCellEditor cellEditor) {
        this.firePropertyChange("cellEditor", this.cellEditor, this.cellEditor = cellEditor);
        this.invalidate();
    }
    
    public TreeCellEditor getCellEditor() {
        return this.cellEditor;
    }
    
    public TreeModel getModel() {
        return this.treeModel;
    }
    
    public void setModel(final TreeModel treeModel) {
        this.clearSelection();
        final TreeModel treeModel2 = this.treeModel;
        if (this.treeModel != null && this.treeModelListener != null) {
            this.treeModel.removeTreeModelListener(this.treeModelListener);
        }
        if (this.accessibleContext != null) {
            if (this.treeModel != null) {
                this.treeModel.removeTreeModelListener((TreeModelListener)this.accessibleContext);
            }
            if (treeModel != null) {
                treeModel.addTreeModelListener((TreeModelListener)this.accessibleContext);
            }
        }
        this.treeModel = treeModel;
        this.clearToggledPaths();
        if (this.treeModel != null) {
            if (this.treeModelListener == null) {
                this.treeModelListener = this.createTreeModelListener();
            }
            if (this.treeModelListener != null) {
                this.treeModel.addTreeModelListener(this.treeModelListener);
            }
            final Object root = this.treeModel.getRoot();
            if (root != null && !this.treeModel.isLeaf(root)) {
                this.expandedState.put(new TreePath(root), Boolean.TRUE);
            }
        }
        this.firePropertyChange("model", treeModel2, this.treeModel);
        this.invalidate();
    }
    
    public boolean isRootVisible() {
        return this.rootVisible;
    }
    
    public void setRootVisible(final boolean rootVisible) {
        this.firePropertyChange("rootVisible", this.rootVisible, this.rootVisible = rootVisible);
        if (this.accessibleContext != null) {
            ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
        }
    }
    
    public void setShowsRootHandles(final boolean showsRootHandles) {
        final boolean showsRootHandles2 = this.showsRootHandles;
        this.getModel();
        this.showsRootHandles = showsRootHandles;
        this.showsRootHandlesSet = true;
        this.firePropertyChange("showsRootHandles", showsRootHandles2, this.showsRootHandles);
        if (this.accessibleContext != null) {
            ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
        }
        this.invalidate();
    }
    
    public boolean getShowsRootHandles() {
        return this.showsRootHandles;
    }
    
    public void setRowHeight(final int rowHeight) {
        final int rowHeight2 = this.rowHeight;
        this.rowHeight = rowHeight;
        this.rowHeightSet = true;
        this.firePropertyChange("rowHeight", rowHeight2, this.rowHeight);
        this.invalidate();
    }
    
    public int getRowHeight() {
        return this.rowHeight;
    }
    
    public boolean isFixedRowHeight() {
        return this.rowHeight > 0;
    }
    
    public void setLargeModel(final boolean largeModel) {
        this.firePropertyChange("largeModel", this.largeModel, this.largeModel = largeModel);
    }
    
    public boolean isLargeModel() {
        return this.largeModel;
    }
    
    public void setInvokesStopCellEditing(final boolean invokesStopCellEditing) {
        this.firePropertyChange("invokesStopCellEditing", this.invokesStopCellEditing, this.invokesStopCellEditing = invokesStopCellEditing);
    }
    
    public boolean getInvokesStopCellEditing() {
        return this.invokesStopCellEditing;
    }
    
    public void setScrollsOnExpand(final boolean scrollsOnExpand) {
        final boolean scrollsOnExpand2 = this.scrollsOnExpand;
        this.scrollsOnExpand = scrollsOnExpand;
        this.scrollsOnExpandSet = true;
        this.firePropertyChange("scrollsOnExpand", scrollsOnExpand2, scrollsOnExpand);
    }
    
    public boolean getScrollsOnExpand() {
        return this.scrollsOnExpand;
    }
    
    public void setToggleClickCount(final int toggleClickCount) {
        this.firePropertyChange("toggleClickCount", this.toggleClickCount, this.toggleClickCount = toggleClickCount);
    }
    
    public int getToggleClickCount() {
        return this.toggleClickCount;
    }
    
    public void setExpandsSelectedPaths(final boolean expandsSelectedPaths) {
        this.firePropertyChange("expandsSelectedPaths", this.expandsSelectedPaths, this.expandsSelectedPaths = expandsSelectedPaths);
    }
    
    public boolean getExpandsSelectedPaths() {
        return this.expandsSelectedPaths;
    }
    
    public void setDragEnabled(final boolean dragEnabled) {
        if (dragEnabled && GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        this.dragEnabled = dragEnabled;
    }
    
    public boolean getDragEnabled() {
        return this.dragEnabled;
    }
    
    public final void setDropMode(final DropMode dropMode) {
        if (dropMode != null) {
            switch (dropMode) {
                case USE_SELECTION:
                case ON:
                case INSERT:
                case ON_OR_INSERT: {
                    this.dropMode = dropMode;
                    return;
                }
            }
        }
        throw new IllegalArgumentException(dropMode + ": Unsupported drop mode for tree");
    }
    
    public final DropMode getDropMode() {
        return this.dropMode;
    }
    
    @Override
    DropLocation dropLocationForPoint(final Point point) {
        DropLocation dropLocation = null;
        final int closestRowForLocation = this.getClosestRowForLocation(point.x, point.y);
        final Rectangle rowBounds = this.getRowBounds(closestRowForLocation);
        final TreeModel model = this.getModel();
        final Object o = (model == null) ? null : model.getRoot();
        final TreePath treePath = (o == null) ? null : new TreePath(o);
        final boolean b = closestRowForLocation == -1 || point.y < rowBounds.y || point.y >= rowBounds.y + rowBounds.height;
        switch (this.dropMode) {
            case USE_SELECTION:
            case ON: {
                if (b) {
                    dropLocation = new DropLocation(point, (TreePath)null, -1);
                    break;
                }
                dropLocation = new DropLocation(point, this.getPathForRow(closestRowForLocation), -1);
                break;
            }
            case INSERT:
            case ON_OR_INSERT: {
                if (closestRowForLocation == -1) {
                    if (o != null && !model.isLeaf(o) && this.isExpanded(treePath)) {
                        dropLocation = new DropLocation(point, treePath, 0);
                        break;
                    }
                    dropLocation = new DropLocation(point, (TreePath)null, -1);
                    break;
                }
                else {
                    final boolean b2 = this.dropMode == DropMode.ON_OR_INSERT || !model.isLeaf(this.getPathForRow(closestRowForLocation).getLastPathComponent());
                    final SwingUtilities2.Section liesInVertical = SwingUtilities2.liesInVertical(rowBounds, point, b2);
                    TreePath treePath2;
                    TreePath treePath3;
                    if (liesInVertical == SwingUtilities2.Section.LEADING) {
                        treePath2 = this.getPathForRow(closestRowForLocation);
                        treePath3 = treePath2.getParentPath();
                    }
                    else if (liesInVertical == SwingUtilities2.Section.TRAILING) {
                        final int n = closestRowForLocation + 1;
                        if (n >= this.getRowCount()) {
                            if (model.isLeaf(o) || !this.isExpanded(treePath)) {
                                dropLocation = new DropLocation(point, (TreePath)null, -1);
                                break;
                            }
                            dropLocation = new DropLocation(point, treePath, model.getChildCount(o));
                            break;
                        }
                        else {
                            treePath2 = this.getPathForRow(n);
                            treePath3 = treePath2.getParentPath();
                        }
                    }
                    else {
                        assert b2;
                        dropLocation = new DropLocation(point, this.getPathForRow(closestRowForLocation), -1);
                        break;
                    }
                    if (treePath3 != null) {
                        dropLocation = new DropLocation(point, treePath3, model.getIndexOfChild(treePath3.getLastPathComponent(), treePath2.getLastPathComponent()));
                        break;
                    }
                    if (b2 || !model.isLeaf(o)) {
                        dropLocation = new DropLocation(point, treePath, -1);
                        break;
                    }
                    dropLocation = new DropLocation(point, (TreePath)null, -1);
                    break;
                }
                break;
            }
            default: {
                assert false : "Unexpected drop mode";
                break;
            }
        }
        if (b || closestRowForLocation != this.expandRow) {
            this.cancelDropTimer();
        }
        if (!b && closestRowForLocation != this.expandRow && this.isCollapsed(closestRowForLocation)) {
            this.expandRow = closestRowForLocation;
            this.startDropTimer();
        }
        return dropLocation;
    }
    
    @Override
    Object setDropLocation(final TransferHandler.DropLocation dropLocation, final Object o, final boolean b) {
        Object o2 = null;
        final DropLocation dropLocation2 = (DropLocation)dropLocation;
        if (this.dropMode == DropMode.USE_SELECTION) {
            if (dropLocation2 == null) {
                if (!b && o != null) {
                    this.setSelectionPaths(((TreePath[][])o)[0]);
                    this.setAnchorSelectionPath(((TreePath[][])o)[1][0]);
                    this.setLeadSelectionPath(((TreePath[][])o)[1][1]);
                }
            }
            else {
                if (this.dropLocation == null) {
                    TreePath[] selectionPaths = this.getSelectionPaths();
                    if (selectionPaths == null) {
                        selectionPaths = new TreePath[0];
                    }
                    o2 = new TreePath[][] { selectionPaths, { this.getAnchorSelectionPath(), this.getLeadSelectionPath() } };
                }
                else {
                    o2 = o;
                }
                this.setSelectionPath(dropLocation2.getPath());
            }
        }
        this.firePropertyChange("dropLocation", this.dropLocation, this.dropLocation = dropLocation2);
        return o2;
    }
    
    @Override
    void dndDone() {
        this.cancelDropTimer();
        this.dropTimer = null;
    }
    
    public final DropLocation getDropLocation() {
        return this.dropLocation;
    }
    
    private void startDropTimer() {
        if (this.dropTimer == null) {
            this.dropTimer = new TreeTimer();
        }
        this.dropTimer.start();
    }
    
    private void cancelDropTimer() {
        if (this.dropTimer != null && this.dropTimer.isRunning()) {
            this.expandRow = -1;
            this.dropTimer.stop();
        }
    }
    
    public boolean isPathEditable(final TreePath treePath) {
        return this.isEditable();
    }
    
    @Override
    public String getToolTipText(final MouseEvent mouseEvent) {
        String s = null;
        if (mouseEvent != null) {
            final Point point = mouseEvent.getPoint();
            final int rowForLocation = this.getRowForLocation(point.x, point.y);
            final TreeCellRenderer cellRenderer = this.getCellRenderer();
            if (rowForLocation != -1 && cellRenderer != null) {
                final TreePath pathForRow = this.getPathForRow(rowForLocation);
                final Object lastPathComponent = pathForRow.getLastPathComponent();
                final Component treeCellRendererComponent = cellRenderer.getTreeCellRendererComponent(this, lastPathComponent, this.isRowSelected(rowForLocation), this.isExpanded(rowForLocation), this.getModel().isLeaf(lastPathComponent), rowForLocation, true);
                if (treeCellRendererComponent instanceof JComponent) {
                    final Rectangle pathBounds = this.getPathBounds(pathForRow);
                    point.translate(-pathBounds.x, -pathBounds.y);
                    final MouseEvent mouseEvent2 = new MouseEvent(treeCellRendererComponent, mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers(), point.x, point.y, mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), 0);
                    final AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
                    mouseEventAccessor.setCausedByTouchEvent(mouseEvent2, mouseEventAccessor.isCausedByTouchEvent(mouseEvent));
                    s = ((JComponent)treeCellRendererComponent).getToolTipText(mouseEvent2);
                }
            }
        }
        if (s == null) {
            s = this.getToolTipText();
        }
        return s;
    }
    
    public String convertValueToText(final Object o, final boolean b, final boolean b2, final boolean b3, final int n, final boolean b4) {
        if (o != null) {
            final String string = o.toString();
            if (string != null) {
                return string;
            }
        }
        return "";
    }
    
    public int getRowCount() {
        final TreeUI ui = this.getUI();
        if (ui != null) {
            return ui.getRowCount(this);
        }
        return 0;
    }
    
    public void setSelectionPath(final TreePath selectionPath) {
        this.getSelectionModel().setSelectionPath(selectionPath);
    }
    
    public void setSelectionPaths(final TreePath[] selectionPaths) {
        this.getSelectionModel().setSelectionPaths(selectionPaths);
    }
    
    public void setLeadSelectionPath(final TreePath leadPath) {
        final TreePath leadPath2 = this.leadPath;
        this.firePropertyChange("leadSelectionPath", leadPath2, this.leadPath = leadPath);
        if (this.accessibleContext != null) {
            ((AccessibleJTree)this.accessibleContext).fireActiveDescendantPropertyChange(leadPath2, leadPath);
        }
    }
    
    public void setAnchorSelectionPath(final TreePath anchorPath) {
        this.firePropertyChange("anchorSelectionPath", this.anchorPath, this.anchorPath = anchorPath);
    }
    
    public void setSelectionRow(final int n) {
        this.setSelectionRows(new int[] { n });
    }
    
    public void setSelectionRows(final int[] array) {
        final TreeUI ui = this.getUI();
        if (ui != null && array != null) {
            final int length = array.length;
            final TreePath[] selectionPaths = new TreePath[length];
            for (int i = 0; i < length; ++i) {
                selectionPaths[i] = ui.getPathForRow(this, array[i]);
            }
            this.setSelectionPaths(selectionPaths);
        }
    }
    
    public void addSelectionPath(final TreePath treePath) {
        this.getSelectionModel().addSelectionPath(treePath);
    }
    
    public void addSelectionPaths(final TreePath[] array) {
        this.getSelectionModel().addSelectionPaths(array);
    }
    
    public void addSelectionRow(final int n) {
        this.addSelectionRows(new int[] { n });
    }
    
    public void addSelectionRows(final int[] array) {
        final TreeUI ui = this.getUI();
        if (ui != null && array != null) {
            final int length = array.length;
            final TreePath[] array2 = new TreePath[length];
            for (int i = 0; i < length; ++i) {
                array2[i] = ui.getPathForRow(this, array[i]);
            }
            this.addSelectionPaths(array2);
        }
    }
    
    public Object getLastSelectedPathComponent() {
        final TreePath selectionPath = this.getSelectionModel().getSelectionPath();
        if (selectionPath != null) {
            return selectionPath.getLastPathComponent();
        }
        return null;
    }
    
    public TreePath getLeadSelectionPath() {
        return this.leadPath;
    }
    
    public TreePath getAnchorSelectionPath() {
        return this.anchorPath;
    }
    
    public TreePath getSelectionPath() {
        return this.getSelectionModel().getSelectionPath();
    }
    
    public TreePath[] getSelectionPaths() {
        final TreePath[] selectionPaths = this.getSelectionModel().getSelectionPaths();
        return (TreePath[])((selectionPaths != null && selectionPaths.length > 0) ? selectionPaths : null);
    }
    
    public int[] getSelectionRows() {
        return this.getSelectionModel().getSelectionRows();
    }
    
    public int getSelectionCount() {
        return this.selectionModel.getSelectionCount();
    }
    
    public int getMinSelectionRow() {
        return this.getSelectionModel().getMinSelectionRow();
    }
    
    public int getMaxSelectionRow() {
        return this.getSelectionModel().getMaxSelectionRow();
    }
    
    public int getLeadSelectionRow() {
        final TreePath leadSelectionPath = this.getLeadSelectionPath();
        if (leadSelectionPath != null) {
            return this.getRowForPath(leadSelectionPath);
        }
        return -1;
    }
    
    public boolean isPathSelected(final TreePath treePath) {
        return this.getSelectionModel().isPathSelected(treePath);
    }
    
    public boolean isRowSelected(final int n) {
        return this.getSelectionModel().isRowSelected(n);
    }
    
    public Enumeration<TreePath> getExpandedDescendants(final TreePath treePath) {
        if (!this.isExpanded(treePath)) {
            return null;
        }
        final Enumeration<TreePath> keys = this.expandedState.keys();
        Vector<TreePath> vector = null;
        if (keys != null) {
            while (keys.hasMoreElements()) {
                final TreePath treePath2 = keys.nextElement();
                final Boolean value = this.expandedState.get(treePath2);
                if (treePath2 != treePath && value != null && value && treePath.isDescendant(treePath2) && this.isVisible(treePath2)) {
                    if (vector == null) {
                        vector = new Vector<TreePath>();
                    }
                    vector.addElement(treePath2);
                }
            }
        }
        if (vector == null) {
            return Collections.enumeration((Collection<TreePath>)Collections.emptySet());
        }
        return vector.elements();
    }
    
    public boolean hasBeenExpanded(final TreePath treePath) {
        return treePath != null && this.expandedState.get(treePath) != null;
    }
    
    public boolean isExpanded(TreePath parentPath) {
        if (parentPath == null) {
            return false;
        }
        do {
            final Boolean value = this.expandedState.get(parentPath);
            if (value == null || !value) {
                return false;
            }
        } while ((parentPath = parentPath.getParentPath()) != null);
        return true;
    }
    
    public boolean isExpanded(final int n) {
        final TreeUI ui = this.getUI();
        if (ui != null) {
            final TreePath pathForRow = ui.getPathForRow(this, n);
            if (pathForRow != null) {
                final Boolean b = this.expandedState.get(pathForRow);
                return b != null && b;
            }
        }
        return false;
    }
    
    public boolean isCollapsed(final TreePath treePath) {
        return !this.isExpanded(treePath);
    }
    
    public boolean isCollapsed(final int n) {
        return !this.isExpanded(n);
    }
    
    public void makeVisible(final TreePath treePath) {
        if (treePath != null) {
            final TreePath parentPath = treePath.getParentPath();
            if (parentPath != null) {
                this.expandPath(parentPath);
            }
        }
    }
    
    public boolean isVisible(final TreePath treePath) {
        if (treePath != null) {
            final TreePath parentPath = treePath.getParentPath();
            return parentPath == null || this.isExpanded(parentPath);
        }
        return false;
    }
    
    public Rectangle getPathBounds(final TreePath treePath) {
        final TreeUI ui = this.getUI();
        if (ui != null) {
            return ui.getPathBounds(this, treePath);
        }
        return null;
    }
    
    public Rectangle getRowBounds(final int n) {
        return this.getPathBounds(this.getPathForRow(n));
    }
    
    public void scrollPathToVisible(final TreePath treePath) {
        if (treePath != null) {
            this.makeVisible(treePath);
            final Rectangle pathBounds = this.getPathBounds(treePath);
            if (pathBounds != null) {
                this.scrollRectToVisible(pathBounds);
                if (this.accessibleContext != null) {
                    ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
                }
            }
        }
    }
    
    public void scrollRowToVisible(final int n) {
        this.scrollPathToVisible(this.getPathForRow(n));
    }
    
    public TreePath getPathForRow(final int n) {
        final TreeUI ui = this.getUI();
        if (ui != null) {
            return ui.getPathForRow(this, n);
        }
        return null;
    }
    
    public int getRowForPath(final TreePath treePath) {
        final TreeUI ui = this.getUI();
        if (ui != null) {
            return ui.getRowForPath(this, treePath);
        }
        return -1;
    }
    
    public void expandPath(final TreePath treePath) {
        final TreeModel model = this.getModel();
        if (treePath != null && model != null && !model.isLeaf(treePath.getLastPathComponent())) {
            this.setExpandedState(treePath, true);
        }
    }
    
    public void expandRow(final int n) {
        this.expandPath(this.getPathForRow(n));
    }
    
    public void collapsePath(final TreePath treePath) {
        this.setExpandedState(treePath, false);
    }
    
    public void collapseRow(final int n) {
        this.collapsePath(this.getPathForRow(n));
    }
    
    public TreePath getPathForLocation(final int n, final int n2) {
        final TreePath closestPathForLocation = this.getClosestPathForLocation(n, n2);
        if (closestPathForLocation != null) {
            final Rectangle pathBounds = this.getPathBounds(closestPathForLocation);
            if (pathBounds != null && n >= pathBounds.x && n < pathBounds.x + pathBounds.width && n2 >= pathBounds.y && n2 < pathBounds.y + pathBounds.height) {
                return closestPathForLocation;
            }
        }
        return null;
    }
    
    public int getRowForLocation(final int n, final int n2) {
        return this.getRowForPath(this.getPathForLocation(n, n2));
    }
    
    public TreePath getClosestPathForLocation(final int n, final int n2) {
        final TreeUI ui = this.getUI();
        if (ui != null) {
            return ui.getClosestPathForLocation(this, n, n2);
        }
        return null;
    }
    
    public int getClosestRowForLocation(final int n, final int n2) {
        return this.getRowForPath(this.getClosestPathForLocation(n, n2));
    }
    
    public boolean isEditing() {
        final TreeUI ui = this.getUI();
        return ui != null && ui.isEditing(this);
    }
    
    public boolean stopEditing() {
        final TreeUI ui = this.getUI();
        return ui != null && ui.stopEditing(this);
    }
    
    public void cancelEditing() {
        final TreeUI ui = this.getUI();
        if (ui != null) {
            ui.cancelEditing(this);
        }
    }
    
    public void startEditingAtPath(final TreePath treePath) {
        final TreeUI ui = this.getUI();
        if (ui != null) {
            ui.startEditingAtPath(this, treePath);
        }
    }
    
    public TreePath getEditingPath() {
        final TreeUI ui = this.getUI();
        if (ui != null) {
            return ui.getEditingPath(this);
        }
        return null;
    }
    
    public void setSelectionModel(TreeSelectionModel sharedInstance) {
        if (sharedInstance == null) {
            sharedInstance = EmptySelectionModel.sharedInstance();
        }
        final TreeSelectionModel selectionModel = this.selectionModel;
        if (this.selectionModel != null && this.selectionRedirector != null) {
            this.selectionModel.removeTreeSelectionListener(this.selectionRedirector);
        }
        if (this.accessibleContext != null) {
            this.selectionModel.removeTreeSelectionListener((TreeSelectionListener)this.accessibleContext);
            sharedInstance.addTreeSelectionListener((TreeSelectionListener)this.accessibleContext);
        }
        this.selectionModel = sharedInstance;
        if (this.selectionRedirector != null) {
            this.selectionModel.addTreeSelectionListener(this.selectionRedirector);
        }
        this.firePropertyChange("selectionModel", selectionModel, this.selectionModel);
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleSelection", false, true);
        }
    }
    
    public TreeSelectionModel getSelectionModel() {
        return this.selectionModel;
    }
    
    protected TreePath[] getPathBetweenRows(int min, int min2) {
        final TreeUI ui = this.getUI();
        if (ui != null) {
            final int rowCount = this.getRowCount();
            if (rowCount > 0 && (min >= 0 || min2 >= 0) && (min < rowCount || min2 < rowCount)) {
                min = Math.min(rowCount - 1, Math.max(min, 0));
                min2 = Math.min(rowCount - 1, Math.max(min2, 0));
                final int min3 = Math.min(min, min2);
                final int max = Math.max(min, min2);
                final TreePath[] array = new TreePath[max - min3 + 1];
                for (int i = min3; i <= max; ++i) {
                    array[i - min3] = ui.getPathForRow(this, i);
                }
                return array;
            }
        }
        return new TreePath[0];
    }
    
    public void setSelectionInterval(final int n, final int n2) {
        this.getSelectionModel().setSelectionPaths(this.getPathBetweenRows(n, n2));
    }
    
    public void addSelectionInterval(final int n, final int n2) {
        final TreePath[] pathBetweenRows = this.getPathBetweenRows(n, n2);
        if (pathBetweenRows != null && pathBetweenRows.length > 0) {
            this.getSelectionModel().addSelectionPaths(pathBetweenRows);
        }
    }
    
    public void removeSelectionInterval(final int n, final int n2) {
        final TreePath[] pathBetweenRows = this.getPathBetweenRows(n, n2);
        if (pathBetweenRows != null && pathBetweenRows.length > 0) {
            this.getSelectionModel().removeSelectionPaths(pathBetweenRows);
        }
    }
    
    public void removeSelectionPath(final TreePath treePath) {
        this.getSelectionModel().removeSelectionPath(treePath);
    }
    
    public void removeSelectionPaths(final TreePath[] array) {
        this.getSelectionModel().removeSelectionPaths(array);
    }
    
    public void removeSelectionRow(final int n) {
        this.removeSelectionRows(new int[] { n });
    }
    
    public void removeSelectionRows(final int[] array) {
        final TreeUI ui = this.getUI();
        if (ui != null && array != null) {
            final int length = array.length;
            final TreePath[] array2 = new TreePath[length];
            for (int i = 0; i < length; ++i) {
                array2[i] = ui.getPathForRow(this, array[i]);
            }
            this.removeSelectionPaths(array2);
        }
    }
    
    public void clearSelection() {
        this.getSelectionModel().clearSelection();
    }
    
    public boolean isSelectionEmpty() {
        return this.getSelectionModel().isSelectionEmpty();
    }
    
    public void addTreeExpansionListener(final TreeExpansionListener uiTreeExpansionListener) {
        if (this.settingUI) {
            this.uiTreeExpansionListener = uiTreeExpansionListener;
        }
        this.listenerList.add(TreeExpansionListener.class, uiTreeExpansionListener);
    }
    
    public void removeTreeExpansionListener(final TreeExpansionListener treeExpansionListener) {
        this.listenerList.remove(TreeExpansionListener.class, treeExpansionListener);
        if (this.uiTreeExpansionListener == treeExpansionListener) {
            this.uiTreeExpansionListener = null;
        }
    }
    
    public TreeExpansionListener[] getTreeExpansionListeners() {
        return this.listenerList.getListeners(TreeExpansionListener.class);
    }
    
    public void addTreeWillExpandListener(final TreeWillExpandListener treeWillExpandListener) {
        this.listenerList.add(TreeWillExpandListener.class, treeWillExpandListener);
    }
    
    public void removeTreeWillExpandListener(final TreeWillExpandListener treeWillExpandListener) {
        this.listenerList.remove(TreeWillExpandListener.class, treeWillExpandListener);
    }
    
    public TreeWillExpandListener[] getTreeWillExpandListeners() {
        return this.listenerList.getListeners(TreeWillExpandListener.class);
    }
    
    public void fireTreeExpanded(final TreePath treePath) {
        final Object[] listenerList = this.listenerList.getListenerList();
        TreeExpansionEvent treeExpansionEvent = null;
        if (this.uiTreeExpansionListener != null) {
            treeExpansionEvent = new TreeExpansionEvent(this, treePath);
            this.uiTreeExpansionListener.treeExpanded(treeExpansionEvent);
        }
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TreeExpansionListener.class && listenerList[i + 1] != this.uiTreeExpansionListener) {
                if (treeExpansionEvent == null) {
                    treeExpansionEvent = new TreeExpansionEvent(this, treePath);
                }
                ((TreeExpansionListener)listenerList[i + 1]).treeExpanded(treeExpansionEvent);
            }
        }
    }
    
    public void fireTreeCollapsed(final TreePath treePath) {
        final Object[] listenerList = this.listenerList.getListenerList();
        TreeExpansionEvent treeExpansionEvent = null;
        if (this.uiTreeExpansionListener != null) {
            treeExpansionEvent = new TreeExpansionEvent(this, treePath);
            this.uiTreeExpansionListener.treeCollapsed(treeExpansionEvent);
        }
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TreeExpansionListener.class && listenerList[i + 1] != this.uiTreeExpansionListener) {
                if (treeExpansionEvent == null) {
                    treeExpansionEvent = new TreeExpansionEvent(this, treePath);
                }
                ((TreeExpansionListener)listenerList[i + 1]).treeCollapsed(treeExpansionEvent);
            }
        }
    }
    
    public void fireTreeWillExpand(final TreePath treePath) throws ExpandVetoException {
        final Object[] listenerList = this.listenerList.getListenerList();
        TreeExpansionEvent treeExpansionEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TreeWillExpandListener.class) {
                if (treeExpansionEvent == null) {
                    treeExpansionEvent = new TreeExpansionEvent(this, treePath);
                }
                ((TreeWillExpandListener)listenerList[i + 1]).treeWillExpand(treeExpansionEvent);
            }
        }
    }
    
    public void fireTreeWillCollapse(final TreePath treePath) throws ExpandVetoException {
        final Object[] listenerList = this.listenerList.getListenerList();
        TreeExpansionEvent treeExpansionEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TreeWillExpandListener.class) {
                if (treeExpansionEvent == null) {
                    treeExpansionEvent = new TreeExpansionEvent(this, treePath);
                }
                ((TreeWillExpandListener)listenerList[i + 1]).treeWillCollapse(treeExpansionEvent);
            }
        }
    }
    
    public void addTreeSelectionListener(final TreeSelectionListener treeSelectionListener) {
        this.listenerList.add(TreeSelectionListener.class, treeSelectionListener);
        if (this.listenerList.getListenerCount(TreeSelectionListener.class) != 0 && this.selectionRedirector == null) {
            this.selectionRedirector = new TreeSelectionRedirector();
            this.selectionModel.addTreeSelectionListener(this.selectionRedirector);
        }
    }
    
    public void removeTreeSelectionListener(final TreeSelectionListener treeSelectionListener) {
        this.listenerList.remove(TreeSelectionListener.class, treeSelectionListener);
        if (this.listenerList.getListenerCount(TreeSelectionListener.class) == 0 && this.selectionRedirector != null) {
            this.selectionModel.removeTreeSelectionListener(this.selectionRedirector);
            this.selectionRedirector = null;
        }
    }
    
    public TreeSelectionListener[] getTreeSelectionListeners() {
        return this.listenerList.getListeners(TreeSelectionListener.class);
    }
    
    protected void fireValueChanged(final TreeSelectionEvent treeSelectionEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TreeSelectionListener.class) {
                ((TreeSelectionListener)listenerList[i + 1]).valueChanged(treeSelectionEvent);
            }
        }
    }
    
    public void treeDidChange() {
        this.revalidate();
        this.repaint();
    }
    
    public void setVisibleRowCount(final int visibleRowCount) {
        this.firePropertyChange("visibleRowCount", this.visibleRowCount, this.visibleRowCount = visibleRowCount);
        this.invalidate();
        if (this.accessibleContext != null) {
            ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
        }
    }
    
    public int getVisibleRowCount() {
        return this.visibleRowCount;
    }
    
    private void expandRoot() {
        final TreeModel model = this.getModel();
        if (model != null && model.getRoot() != null) {
            this.expandPath(new TreePath(model.getRoot()));
        }
    }
    
    public TreePath getNextMatch(String upperCase, final int n, final Position.Bias bias) {
        final int rowCount = this.getRowCount();
        if (upperCase == null) {
            throw new IllegalArgumentException();
        }
        if (n < 0 || n >= rowCount) {
            throw new IllegalArgumentException();
        }
        upperCase = upperCase.toUpperCase();
        final int n2 = (bias == Position.Bias.Forward) ? 1 : -1;
        int i = n;
        do {
            final TreePath pathForRow = this.getPathForRow(i);
            if (this.convertValueToText(pathForRow.getLastPathComponent(), this.isRowSelected(i), this.isExpanded(i), true, i, false).toUpperCase().startsWith(upperCase)) {
                return pathForRow;
            }
            i = (i + n2 + rowCount) % rowCount;
        } while (i != n);
        return null;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final Vector vector = new Vector();
        objectOutputStream.defaultWriteObject();
        if (this.cellRenderer != null && this.cellRenderer instanceof Serializable) {
            vector.addElement("cellRenderer");
            vector.addElement(this.cellRenderer);
        }
        if (this.cellEditor != null && this.cellEditor instanceof Serializable) {
            vector.addElement("cellEditor");
            vector.addElement(this.cellEditor);
        }
        if (this.treeModel != null && this.treeModel instanceof Serializable) {
            vector.addElement("treeModel");
            vector.addElement(this.treeModel);
        }
        if (this.selectionModel != null && this.selectionModel instanceof Serializable) {
            vector.addElement("selectionModel");
            vector.addElement(this.selectionModel);
        }
        final Object archivableExpandedState = this.getArchivableExpandedState();
        if (archivableExpandedState != null) {
            vector.addElement("expandedState");
            vector.addElement(archivableExpandedState);
        }
        objectOutputStream.writeObject(vector);
        if (this.getUIClassID().equals("TreeUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.expandedState = new Hashtable<TreePath, Boolean>();
        this.expandedStack = new Stack<Stack<TreePath>>();
        final Vector vector = (Vector)objectInputStream.readObject();
        int n = 0;
        final int size = vector.size();
        if (n < size && vector.elementAt(n).equals("cellRenderer")) {
            this.cellRenderer = (TreeCellRenderer)vector.elementAt(++n);
            ++n;
        }
        if (n < size && vector.elementAt(n).equals("cellEditor")) {
            this.cellEditor = (TreeCellEditor)vector.elementAt(++n);
            ++n;
        }
        if (n < size && vector.elementAt(n).equals("treeModel")) {
            this.treeModel = (TreeModel)vector.elementAt(++n);
            ++n;
        }
        if (n < size && vector.elementAt(n).equals("selectionModel")) {
            this.selectionModel = (TreeSelectionModel)vector.elementAt(++n);
            ++n;
        }
        if (n < size && vector.elementAt(n).equals("expandedState")) {
            this.unarchiveExpandedState(vector.elementAt(++n));
            ++n;
        }
        if (this.listenerList.getListenerCount(TreeSelectionListener.class) != 0) {
            this.selectionRedirector = new TreeSelectionRedirector();
            this.selectionModel.addTreeSelectionListener(this.selectionRedirector);
        }
        if (this.treeModel != null) {
            this.treeModelListener = this.createTreeModelListener();
            if (this.treeModelListener != null) {
                this.treeModel.addTreeModelListener(this.treeModelListener);
            }
        }
    }
    
    private Object getArchivableExpandedState() {
        if (this.getModel() != null) {
            final Enumeration<TreePath> keys = this.expandedState.keys();
            if (keys != null) {
                final Vector vector = new Vector();
                while (keys.hasMoreElements()) {
                    final TreePath treePath = keys.nextElement();
                    int[] modelIndexsForPath;
                    try {
                        modelIndexsForPath = this.getModelIndexsForPath(treePath);
                    }
                    catch (final Error error) {
                        modelIndexsForPath = null;
                    }
                    if (modelIndexsForPath != null) {
                        vector.addElement(modelIndexsForPath);
                        vector.addElement(this.expandedState.get(treePath));
                    }
                }
                return vector;
            }
        }
        return null;
    }
    
    private void unarchiveExpandedState(final Object o) {
        if (o instanceof Vector) {
            final Vector vector = (Vector)o;
            for (int i = vector.size() - 1; i >= 0; --i) {
                final Boolean b = vector.elementAt(i--);
                try {
                    final TreePath pathForIndexs = this.getPathForIndexs(vector.elementAt(i));
                    if (pathForIndexs != null) {
                        this.expandedState.put(pathForIndexs, b);
                    }
                }
                catch (final Error error) {}
            }
        }
    }
    
    private int[] getModelIndexsForPath(final TreePath treePath) {
        if (treePath != null) {
            final TreeModel model = this.getModel();
            final int pathCount = treePath.getPathCount();
            final int[] array = new int[pathCount - 1];
            Object o = model.getRoot();
            for (int i = 1; i < pathCount; ++i) {
                array[i - 1] = model.getIndexOfChild(o, treePath.getPathComponent(i));
                o = treePath.getPathComponent(i);
                if (array[i - 1] < 0) {
                    return null;
                }
            }
            return array;
        }
        return null;
    }
    
    private TreePath getPathForIndexs(final int[] array) {
        if (array == null) {
            return null;
        }
        final TreeModel model = this.getModel();
        if (model == null) {
            return null;
        }
        final int length = array.length;
        Object o = model.getRoot();
        if (o == null) {
            return null;
        }
        TreePath pathByAddingChild = new TreePath(o);
        for (int i = 0; i < length; ++i) {
            o = model.getChild(o, array[i]);
            if (o == null) {
                return null;
            }
            pathByAddingChild = pathByAddingChild.pathByAddingChild(o);
        }
        return pathByAddingChild;
    }
    
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        final int width = this.getPreferredSize().width;
        final int visibleRowCount = this.getVisibleRowCount();
        int n = -1;
        if (this.isFixedRowHeight()) {
            n = visibleRowCount * this.getRowHeight();
        }
        else {
            final TreeUI ui = this.getUI();
            if (ui != null && visibleRowCount > 0) {
                final int rowCount = ui.getRowCount(this);
                if (rowCount >= visibleRowCount) {
                    final Rectangle rowBounds = this.getRowBounds(visibleRowCount - 1);
                    if (rowBounds != null) {
                        n = rowBounds.y + rowBounds.height;
                    }
                }
                else if (rowCount > 0) {
                    final Rectangle rowBounds2 = this.getRowBounds(0);
                    if (rowBounds2 != null) {
                        n = rowBounds2.height * visibleRowCount;
                    }
                }
            }
            if (n == -1) {
                n = 16 * visibleRowCount;
            }
        }
        return new Dimension(width, n);
    }
    
    @Override
    public int getScrollableUnitIncrement(final Rectangle rectangle, final int n, final int n2) {
        if (n == 1) {
            final int closestRowForLocation = this.getClosestRowForLocation(0, rectangle.y);
            if (closestRowForLocation != -1) {
                final Rectangle rowBounds = this.getRowBounds(closestRowForLocation);
                if (rowBounds.y != rectangle.y) {
                    if (n2 < 0) {
                        return Math.max(0, rectangle.y - rowBounds.y);
                    }
                    return rowBounds.y + rowBounds.height - rectangle.y;
                }
                else {
                    if (n2 >= 0) {
                        return rowBounds.height;
                    }
                    if (closestRowForLocation != 0) {
                        return this.getRowBounds(closestRowForLocation - 1).height;
                    }
                }
            }
            return 0;
        }
        return 4;
    }
    
    @Override
    public int getScrollableBlockIncrement(final Rectangle rectangle, final int n, final int n2) {
        return (n == 1) ? rectangle.height : rectangle.width;
    }
    
    @Override
    public boolean getScrollableTracksViewportWidth() {
        final Container unwrappedParent = SwingUtilities.getUnwrappedParent(this);
        return unwrappedParent instanceof JViewport && unwrappedParent.getWidth() > this.getPreferredSize().width;
    }
    
    @Override
    public boolean getScrollableTracksViewportHeight() {
        final Container unwrappedParent = SwingUtilities.getUnwrappedParent(this);
        return unwrappedParent instanceof JViewport && unwrappedParent.getHeight() > this.getPreferredSize().height;
    }
    
    protected void setExpandedState(final TreePath treePath, final boolean b) {
        if (treePath != null) {
            TreePath treePath2 = treePath.getParentPath();
            Stack stack;
            if (this.expandedStack.size() == 0) {
                stack = new Stack();
            }
            else {
                stack = this.expandedStack.pop();
            }
            try {
                while (treePath2 != null) {
                    if (this.isExpanded(treePath2)) {
                        treePath2 = null;
                    }
                    else {
                        stack.push(treePath2);
                        treePath2 = treePath2.getParentPath();
                    }
                }
                for (int i = stack.size() - 1; i >= 0; --i) {
                    final TreePath treePath3 = stack.pop();
                    if (!this.isExpanded(treePath3)) {
                        try {
                            this.fireTreeWillExpand(treePath3);
                        }
                        catch (final ExpandVetoException ex) {
                            return;
                        }
                        this.expandedState.put(treePath3, Boolean.TRUE);
                        this.fireTreeExpanded(treePath3);
                        if (this.accessibleContext != null) {
                            ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
                        }
                    }
                }
            }
            finally {
                if (this.expandedStack.size() < JTree.TEMP_STACK_SIZE) {
                    stack.removeAllElements();
                    this.expandedStack.push(stack);
                }
            }
            if (!b) {
                final Boolean value = this.expandedState.get(treePath);
                if (value != null && value) {
                    try {
                        this.fireTreeWillCollapse(treePath);
                    }
                    catch (final ExpandVetoException ex2) {
                        return;
                    }
                    this.expandedState.put(treePath, Boolean.FALSE);
                    this.fireTreeCollapsed(treePath);
                    if (this.removeDescendantSelectedPaths(treePath, false) && !this.isPathSelected(treePath)) {
                        this.addSelectionPath(treePath);
                    }
                    if (this.accessibleContext != null) {
                        ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
                    }
                }
            }
            else {
                final Boolean value2 = this.expandedState.get(treePath);
                if (value2 != null) {
                    if (value2) {
                        return;
                    }
                }
                try {
                    this.fireTreeWillExpand(treePath);
                }
                catch (final ExpandVetoException ex3) {
                    return;
                }
                this.expandedState.put(treePath, Boolean.TRUE);
                this.fireTreeExpanded(treePath);
                if (this.accessibleContext != null) {
                    ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange();
                }
            }
        }
    }
    
    protected Enumeration<TreePath> getDescendantToggledPaths(final TreePath treePath) {
        if (treePath == null) {
            return null;
        }
        final Vector vector = new Vector();
        final Enumeration<TreePath> keys = this.expandedState.keys();
        while (keys.hasMoreElements()) {
            final TreePath treePath2 = keys.nextElement();
            if (treePath.isDescendant(treePath2)) {
                vector.addElement(treePath2);
            }
        }
        return vector.elements();
    }
    
    protected void removeDescendantToggledPaths(final Enumeration<TreePath> enumeration) {
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                final Enumeration<TreePath> descendantToggledPaths = this.getDescendantToggledPaths(enumeration.nextElement());
                if (descendantToggledPaths != null) {
                    while (descendantToggledPaths.hasMoreElements()) {
                        this.expandedState.remove(descendantToggledPaths.nextElement());
                    }
                }
            }
        }
    }
    
    protected void clearToggledPaths() {
        this.expandedState.clear();
    }
    
    protected TreeModelListener createTreeModelListener() {
        return new TreeModelHandler();
    }
    
    protected boolean removeDescendantSelectedPaths(final TreePath treePath, final boolean b) {
        final TreePath[] descendantSelectedPaths = this.getDescendantSelectedPaths(treePath, b);
        if (descendantSelectedPaths != null) {
            this.getSelectionModel().removeSelectionPaths(descendantSelectedPaths);
            return true;
        }
        return false;
    }
    
    private TreePath[] getDescendantSelectedPaths(final TreePath treePath, final boolean b) {
        final TreeSelectionModel selectionModel = this.getSelectionModel();
        TreePath[] array = (TreePath[])((selectionModel != null) ? selectionModel.getSelectionPaths() : null);
        if (array != null) {
            boolean b2 = false;
            for (int i = array.length - 1; i >= 0; --i) {
                if (array[i] != null && treePath.isDescendant(array[i]) && (!treePath.equals(array[i]) || b)) {
                    b2 = true;
                }
                else {
                    array[i] = null;
                }
            }
            if (!b2) {
                array = null;
            }
            return array;
        }
        return null;
    }
    
    void removeDescendantSelectedPaths(final TreeModelEvent treeModelEvent) {
        final TreePath treePath = SwingUtilities2.getTreePath(treeModelEvent, this.getModel());
        final Object[] children = treeModelEvent.getChildren();
        if (this.getSelectionModel() != null && treePath != null && children != null && children.length > 0) {
            for (int i = children.length - 1; i >= 0; --i) {
                this.removeDescendantSelectedPaths(treePath.pathByAddingChild(children[i]), true);
            }
        }
    }
    
    @Override
    void setUIProperty(final String s, final Object o) {
        if (s == "rowHeight") {
            if (!this.rowHeightSet) {
                this.setRowHeight(((Number)o).intValue());
                this.rowHeightSet = false;
            }
        }
        else if (s == "scrollsOnExpand") {
            if (!this.scrollsOnExpandSet) {
                this.setScrollsOnExpand((boolean)o);
                this.scrollsOnExpandSet = false;
            }
        }
        else if (s == "showsRootHandles") {
            if (!this.showsRootHandlesSet) {
                this.setShowsRootHandles((boolean)o);
                this.showsRootHandlesSet = false;
            }
        }
        else {
            super.setUIProperty(s, o);
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",editable=" + (this.editable ? "true" : "false") + ",invokesStopCellEditing=" + (this.invokesStopCellEditing ? "true" : "false") + ",largeModel=" + (this.largeModel ? "true" : "false") + ",rootVisible=" + (this.rootVisible ? "true" : "false") + ",rowHeight=" + this.rowHeight + ",scrollsOnExpand=" + (this.scrollsOnExpand ? "true" : "false") + ",showsRootHandles=" + (this.showsRootHandles ? "true" : "false") + ",toggleClickCount=" + this.toggleClickCount + ",visibleRowCount=" + this.visibleRowCount;
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJTree();
        }
        return this.accessibleContext;
    }
    
    static {
        JTree.TEMP_STACK_SIZE = 11;
    }
    
    public static final class DropLocation extends TransferHandler.DropLocation
    {
        private final TreePath path;
        private final int index;
        
        private DropLocation(final Point point, final TreePath path, final int index) {
            super(point);
            this.path = path;
            this.index = index;
        }
        
        public int getChildIndex() {
            return this.index;
        }
        
        public TreePath getPath() {
            return this.path;
        }
        
        @Override
        public String toString() {
            return this.getClass().getName() + "[dropPoint=" + this.getDropPoint() + ",path=" + this.path + ",childIndex=" + this.index + "]";
        }
    }
    
    private class TreeTimer extends Timer
    {
        public TreeTimer() {
            super(2000, null);
            this.setRepeats(false);
        }
        
        public void fireActionPerformed(final ActionEvent actionEvent) {
            JTree.this.expandRow(JTree.this.expandRow);
        }
    }
    
    protected static class EmptySelectionModel extends DefaultTreeSelectionModel
    {
        protected static final EmptySelectionModel sharedInstance;
        
        public static EmptySelectionModel sharedInstance() {
            return EmptySelectionModel.sharedInstance;
        }
        
        @Override
        public void setSelectionPaths(final TreePath[] array) {
        }
        
        @Override
        public void addSelectionPaths(final TreePath[] array) {
        }
        
        @Override
        public void removeSelectionPaths(final TreePath[] array) {
        }
        
        @Override
        public void setSelectionMode(final int n) {
        }
        
        @Override
        public void setRowMapper(final RowMapper rowMapper) {
        }
        
        @Override
        public void addTreeSelectionListener(final TreeSelectionListener treeSelectionListener) {
        }
        
        @Override
        public void removeTreeSelectionListener(final TreeSelectionListener treeSelectionListener) {
        }
        
        @Override
        public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        }
        
        @Override
        public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        }
        
        static {
            sharedInstance = new EmptySelectionModel();
        }
    }
    
    protected class TreeSelectionRedirector implements Serializable, TreeSelectionListener
    {
        @Override
        public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {
            JTree.this.fireValueChanged((TreeSelectionEvent)treeSelectionEvent.cloneWithSource(JTree.this));
        }
    }
    
    protected class TreeModelHandler implements TreeModelListener
    {
        @Override
        public void treeNodesChanged(final TreeModelEvent treeModelEvent) {
        }
        
        @Override
        public void treeNodesInserted(final TreeModelEvent treeModelEvent) {
        }
        
        @Override
        public void treeStructureChanged(final TreeModelEvent treeModelEvent) {
            if (treeModelEvent == null) {
                return;
            }
            final TreePath treePath = SwingUtilities2.getTreePath(treeModelEvent, JTree.this.getModel());
            if (treePath == null) {
                return;
            }
            if (treePath.getPathCount() == 1) {
                JTree.this.clearToggledPaths();
                final Object root = JTree.this.treeModel.getRoot();
                if (root != null && !JTree.this.treeModel.isLeaf(root)) {
                    JTree.this.expandedState.put(treePath, Boolean.TRUE);
                }
            }
            else if (JTree.this.expandedState.get(treePath) != null) {
                final Vector vector = new Vector(1);
                final boolean expanded = JTree.this.isExpanded(treePath);
                vector.addElement(treePath);
                JTree.this.removeDescendantToggledPaths(vector.elements());
                if (expanded) {
                    final TreeModel model = JTree.this.getModel();
                    if (model == null || model.isLeaf(treePath.getLastPathComponent())) {
                        JTree.this.collapsePath(treePath);
                    }
                    else {
                        JTree.this.expandedState.put(treePath, Boolean.TRUE);
                    }
                }
            }
            JTree.this.removeDescendantSelectedPaths(treePath, false);
        }
        
        @Override
        public void treeNodesRemoved(final TreeModelEvent treeModelEvent) {
            if (treeModelEvent == null) {
                return;
            }
            final TreePath treePath = SwingUtilities2.getTreePath(treeModelEvent, JTree.this.getModel());
            final Object[] children = treeModelEvent.getChildren();
            if (children == null) {
                return;
            }
            final Vector vector = new Vector<TreePath>(Math.max(1, children.length));
            for (int i = children.length - 1; i >= 0; --i) {
                final TreePath pathByAddingChild = treePath.pathByAddingChild(children[i]);
                if (JTree.this.expandedState.get(pathByAddingChild) != null) {
                    vector.addElement(pathByAddingChild);
                }
            }
            if (vector.size() > 0) {
                JTree.this.removeDescendantToggledPaths(vector.elements());
            }
            final TreeModel model = JTree.this.getModel();
            if (model == null || model.isLeaf(treePath.getLastPathComponent())) {
                JTree.this.expandedState.remove(treePath);
            }
            JTree.this.removeDescendantSelectedPaths(treeModelEvent);
        }
    }
    
    public static class DynamicUtilTreeNode extends DefaultMutableTreeNode
    {
        protected boolean hasChildren;
        protected Object childValue;
        protected boolean loadedChildren;
        
        public static void createChildren(final DefaultMutableTreeNode defaultMutableTreeNode, final Object o) {
            if (o instanceof Vector) {
                final Vector vector = (Vector)o;
                for (int i = 0; i < vector.size(); ++i) {
                    defaultMutableTreeNode.add(new DynamicUtilTreeNode(vector.elementAt(i), vector.elementAt(i)));
                }
            }
            else if (o instanceof Hashtable) {
                final Hashtable hashtable = (Hashtable)o;
                final Enumeration keys = hashtable.keys();
                while (keys.hasMoreElements()) {
                    final Object nextElement = keys.nextElement();
                    defaultMutableTreeNode.add(new DynamicUtilTreeNode(nextElement, hashtable.get(nextElement)));
                }
            }
            else if (o instanceof Object[]) {
                final Object[] array = (Object[])o;
                for (int j = 0; j < array.length; ++j) {
                    defaultMutableTreeNode.add(new DynamicUtilTreeNode(array[j], array[j]));
                }
            }
        }
        
        public DynamicUtilTreeNode(final Object o, final Object childValue) {
            super(o);
            this.loadedChildren = false;
            this.childValue = childValue;
            if (childValue != null) {
                if (childValue instanceof Vector) {
                    this.setAllowsChildren(true);
                }
                else if (childValue instanceof Hashtable) {
                    this.setAllowsChildren(true);
                }
                else if (childValue instanceof Object[]) {
                    this.setAllowsChildren(true);
                }
                else {
                    this.setAllowsChildren(false);
                }
            }
            else {
                this.setAllowsChildren(false);
            }
        }
        
        @Override
        public boolean isLeaf() {
            return !this.getAllowsChildren();
        }
        
        @Override
        public int getChildCount() {
            if (!this.loadedChildren) {
                this.loadChildren();
            }
            return super.getChildCount();
        }
        
        protected void loadChildren() {
            this.loadedChildren = true;
            createChildren(this, this.childValue);
        }
        
        @Override
        public TreeNode getChildAt(final int n) {
            if (!this.loadedChildren) {
                this.loadChildren();
            }
            return super.getChildAt(n);
        }
        
        @Override
        public Enumeration children() {
            if (!this.loadedChildren) {
                this.loadChildren();
            }
            return super.children();
        }
    }
    
    protected class AccessibleJTree extends AccessibleJComponent implements AccessibleSelection, TreeSelectionListener, TreeModelListener, TreeExpansionListener
    {
        TreePath leadSelectionPath;
        Accessible leadSelectionAccessible;
        
        public AccessibleJTree() {
            final TreeModel model = JTree.this.getModel();
            if (model != null) {
                model.addTreeModelListener(this);
            }
            JTree.this.addTreeExpansionListener(this);
            JTree.this.addTreeSelectionListener(this);
            this.leadSelectionPath = JTree.this.getLeadSelectionPath();
            this.leadSelectionAccessible = ((this.leadSelectionPath != null) ? new AccessibleJTreeNode(JTree.this, this.leadSelectionPath, JTree.this) : null);
        }
        
        @Override
        public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {
            this.firePropertyChange("AccessibleSelection", false, true);
        }
        
        public void fireVisibleDataPropertyChange() {
            this.firePropertyChange("AccessibleVisibleData", false, true);
        }
        
        @Override
        public void treeNodesChanged(final TreeModelEvent treeModelEvent) {
            this.fireVisibleDataPropertyChange();
        }
        
        @Override
        public void treeNodesInserted(final TreeModelEvent treeModelEvent) {
            this.fireVisibleDataPropertyChange();
        }
        
        @Override
        public void treeNodesRemoved(final TreeModelEvent treeModelEvent) {
            this.fireVisibleDataPropertyChange();
        }
        
        @Override
        public void treeStructureChanged(final TreeModelEvent treeModelEvent) {
            this.fireVisibleDataPropertyChange();
        }
        
        @Override
        public void treeCollapsed(final TreeExpansionEvent treeExpansionEvent) {
            this.fireVisibleDataPropertyChange();
            final TreePath path = treeExpansionEvent.getPath();
            if (path != null) {
                this.firePropertyChange("AccessibleState", null, new PropertyChangeEvent(new AccessibleJTreeNode(JTree.this, path, null), "AccessibleState", AccessibleState.EXPANDED, AccessibleState.COLLAPSED));
            }
        }
        
        @Override
        public void treeExpanded(final TreeExpansionEvent treeExpansionEvent) {
            this.fireVisibleDataPropertyChange();
            final TreePath path = treeExpansionEvent.getPath();
            if (path != null) {
                this.firePropertyChange("AccessibleState", null, new PropertyChangeEvent(new AccessibleJTreeNode(JTree.this, path, null), "AccessibleState", AccessibleState.COLLAPSED, AccessibleState.EXPANDED));
            }
        }
        
        void fireActiveDescendantPropertyChange(final TreePath treePath, final TreePath treePath2) {
            if (treePath != treePath2) {
                this.firePropertyChange("AccessibleActiveDescendant", (treePath != null) ? new AccessibleJTreeNode(JTree.this, treePath, null) : null, (treePath2 != null) ? new AccessibleJTreeNode(JTree.this, treePath2, null) : null);
            }
        }
        
        private AccessibleContext getCurrentAccessibleContext() {
            final Component currentComponent = this.getCurrentComponent();
            if (currentComponent instanceof Accessible) {
                return currentComponent.getAccessibleContext();
            }
            return null;
        }
        
        private Component getCurrentComponent() {
            final TreeModel model = JTree.this.getModel();
            if (model == null) {
                return null;
            }
            final Object root = model.getRoot();
            if (root == null) {
                return null;
            }
            final TreePath treePath = new TreePath(root);
            if (JTree.this.isVisible(treePath)) {
                final TreeCellRenderer cellRenderer = JTree.this.getCellRenderer();
                final TreeUI ui = JTree.this.getUI();
                if (ui != null) {
                    final int rowForPath = ui.getRowForPath(JTree.this, treePath);
                    final int leadSelectionRow = JTree.this.getLeadSelectionRow();
                    return cellRenderer.getTreeCellRendererComponent(JTree.this, root, JTree.this.isPathSelected(treePath), JTree.this.isExpanded(treePath), model.isLeaf(root), rowForPath, JTree.this.isFocusOwner() && leadSelectionRow == rowForPath);
                }
            }
            return null;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TREE;
        }
        
        @Override
        public Accessible getAccessibleAt(final Point point) {
            final TreePath closestPathForLocation = JTree.this.getClosestPathForLocation(point.x, point.y);
            if (closestPathForLocation != null) {
                return new AccessibleJTreeNode(JTree.this, closestPathForLocation, null);
            }
            return null;
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            final TreeModel model = JTree.this.getModel();
            if (model == null) {
                return 0;
            }
            if (JTree.this.isRootVisible()) {
                return 1;
            }
            final Object root = model.getRoot();
            if (root == null) {
                return 0;
            }
            return model.getChildCount(root);
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            final TreeModel model = JTree.this.getModel();
            if (model == null) {
                return null;
            }
            final Object root = model.getRoot();
            if (root == null) {
                return null;
            }
            if (JTree.this.isRootVisible()) {
                if (n != 0) {
                    return null;
                }
                final Object[] array = { root };
                if (array[0] == null) {
                    return null;
                }
                return new AccessibleJTreeNode(JTree.this, new TreePath(array), JTree.this);
            }
            else {
                final int childCount = model.getChildCount(root);
                if (n < 0 || n >= childCount) {
                    return null;
                }
                final Object child = model.getChild(root, n);
                if (child == null) {
                    return null;
                }
                return new AccessibleJTreeNode(JTree.this, new TreePath(new Object[] { root, child }), JTree.this);
            }
        }
        
        @Override
        public int getAccessibleIndexInParent() {
            return super.getAccessibleIndexInParent();
        }
        
        @Override
        public AccessibleSelection getAccessibleSelection() {
            return this;
        }
        
        @Override
        public int getAccessibleSelectionCount() {
            final Object[] array = { JTree.this.treeModel.getRoot() };
            if (array[0] == null) {
                return 0;
            }
            if (JTree.this.isPathSelected(new TreePath(array))) {
                return 1;
            }
            return 0;
        }
        
        @Override
        public Accessible getAccessibleSelection(final int n) {
            if (n == 0) {
                final Object[] array = { JTree.this.treeModel.getRoot() };
                if (array[0] == null) {
                    return null;
                }
                final TreePath treePath = new TreePath(array);
                if (JTree.this.isPathSelected(treePath)) {
                    return new AccessibleJTreeNode(JTree.this, treePath, JTree.this);
                }
            }
            return null;
        }
        
        @Override
        public boolean isAccessibleChildSelected(final int n) {
            if (n == 0) {
                final Object[] array = { JTree.this.treeModel.getRoot() };
                return array[0] != null && JTree.this.isPathSelected(new TreePath(array));
            }
            return false;
        }
        
        @Override
        public void addAccessibleSelection(final int n) {
            final TreeModel model = JTree.this.getModel();
            if (model != null && n == 0) {
                final Object[] array = { model.getRoot() };
                if (array[0] == null) {
                    return;
                }
                JTree.this.addSelectionPath(new TreePath(array));
            }
        }
        
        @Override
        public void removeAccessibleSelection(final int n) {
            final TreeModel model = JTree.this.getModel();
            if (model != null && n == 0) {
                final Object[] array = { model.getRoot() };
                if (array[0] == null) {
                    return;
                }
                JTree.this.removeSelectionPath(new TreePath(array));
            }
        }
        
        @Override
        public void clearAccessibleSelection() {
            for (int accessibleChildrenCount = this.getAccessibleChildrenCount(), i = 0; i < accessibleChildrenCount; ++i) {
                this.removeAccessibleSelection(i);
            }
        }
        
        @Override
        public void selectAllAccessibleSelection() {
            final TreeModel model = JTree.this.getModel();
            if (model != null) {
                final Object[] array = { model.getRoot() };
                if (array[0] == null) {
                    return;
                }
                JTree.this.addSelectionPath(new TreePath(array));
            }
        }
        
        protected class AccessibleJTreeNode extends AccessibleContext implements Accessible, AccessibleComponent, AccessibleSelection, AccessibleAction
        {
            private JTree tree;
            private TreeModel treeModel;
            private Object obj;
            private TreePath path;
            private Accessible accessibleParent;
            private int index;
            private boolean isLeaf;
            
            public AccessibleJTreeNode(final JTree tree, final TreePath path, final Accessible accessibleParent) {
                this.tree = null;
                this.treeModel = null;
                this.obj = null;
                this.path = null;
                this.accessibleParent = null;
                this.index = 0;
                this.isLeaf = false;
                this.tree = tree;
                this.path = path;
                this.accessibleParent = accessibleParent;
                this.treeModel = tree.getModel();
                this.obj = path.getLastPathComponent();
                if (this.treeModel != null) {
                    this.isLeaf = this.treeModel.isLeaf(this.obj);
                }
            }
            
            private TreePath getChildTreePath(final int n) {
                if (n < 0 || n >= this.getAccessibleChildrenCount()) {
                    return null;
                }
                final Object child = this.treeModel.getChild(this.obj, n);
                final Object[] path = this.path.getPath();
                final Object[] array = new Object[path.length + 1];
                System.arraycopy(path, 0, array, 0, path.length);
                array[array.length - 1] = child;
                return new TreePath(array);
            }
            
            @Override
            public AccessibleContext getAccessibleContext() {
                return this;
            }
            
            private AccessibleContext getCurrentAccessibleContext() {
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent instanceof Accessible) {
                    return currentComponent.getAccessibleContext();
                }
                return null;
            }
            
            private Component getCurrentComponent() {
                if (this.tree.isVisible(this.path)) {
                    final TreeCellRenderer cellRenderer = this.tree.getCellRenderer();
                    if (cellRenderer == null) {
                        return null;
                    }
                    final TreeUI ui = this.tree.getUI();
                    if (ui != null) {
                        return cellRenderer.getTreeCellRendererComponent(this.tree, this.obj, this.tree.isPathSelected(this.path), this.tree.isExpanded(this.path), this.isLeaf, ui.getRowForPath(JTree.this, this.path), false);
                    }
                }
                return null;
            }
            
            @Override
            public String getAccessibleName() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    final String accessibleName = currentAccessibleContext.getAccessibleName();
                    if (accessibleName != null && accessibleName != "") {
                        return currentAccessibleContext.getAccessibleName();
                    }
                    return null;
                }
                else {
                    if (this.accessibleName != null && this.accessibleName != "") {
                        return this.accessibleName;
                    }
                    return (String)JTree.this.getClientProperty("AccessibleName");
                }
            }
            
            @Override
            public void setAccessibleName(final String s) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    currentAccessibleContext.setAccessibleName(s);
                }
                else {
                    super.setAccessibleName(s);
                }
            }
            
            @Override
            public String getAccessibleDescription() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    return currentAccessibleContext.getAccessibleDescription();
                }
                return super.getAccessibleDescription();
            }
            
            @Override
            public void setAccessibleDescription(final String s) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    currentAccessibleContext.setAccessibleDescription(s);
                }
                else {
                    super.setAccessibleDescription(s);
                }
            }
            
            @Override
            public AccessibleRole getAccessibleRole() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    return currentAccessibleContext.getAccessibleRole();
                }
                return AccessibleRole.UNKNOWN;
            }
            
            @Override
            public AccessibleStateSet getAccessibleStateSet() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                AccessibleStateSet accessibleStateSet;
                if (currentAccessibleContext != null) {
                    accessibleStateSet = currentAccessibleContext.getAccessibleStateSet();
                }
                else {
                    accessibleStateSet = new AccessibleStateSet();
                }
                if (this.isShowing()) {
                    accessibleStateSet.add(AccessibleState.SHOWING);
                }
                else if (accessibleStateSet.contains(AccessibleState.SHOWING)) {
                    accessibleStateSet.remove(AccessibleState.SHOWING);
                }
                if (this.isVisible()) {
                    accessibleStateSet.add(AccessibleState.VISIBLE);
                }
                else if (accessibleStateSet.contains(AccessibleState.VISIBLE)) {
                    accessibleStateSet.remove(AccessibleState.VISIBLE);
                }
                if (this.tree.isPathSelected(this.path)) {
                    accessibleStateSet.add(AccessibleState.SELECTED);
                }
                if (this.path == JTree.this.getLeadSelectionPath()) {
                    accessibleStateSet.add(AccessibleState.ACTIVE);
                }
                if (!this.isLeaf) {
                    accessibleStateSet.add(AccessibleState.EXPANDABLE);
                }
                if (this.tree.isExpanded(this.path)) {
                    accessibleStateSet.add(AccessibleState.EXPANDED);
                }
                else {
                    accessibleStateSet.add(AccessibleState.COLLAPSED);
                }
                if (this.tree.isEditable()) {
                    accessibleStateSet.add(AccessibleState.EDITABLE);
                }
                return accessibleStateSet;
            }
            
            @Override
            public Accessible getAccessibleParent() {
                if (this.accessibleParent == null) {
                    final Object[] path = this.path.getPath();
                    if (path.length > 1) {
                        final Object o = path[path.length - 2];
                        if (this.treeModel != null) {
                            this.index = this.treeModel.getIndexOfChild(o, this.obj);
                        }
                        final Object[] array = new Object[path.length - 1];
                        System.arraycopy(path, 0, array, 0, path.length - 1);
                        this.setAccessibleParent(this.accessibleParent = new AccessibleJTreeNode(this.tree, new TreePath(array), null));
                    }
                    else if (this.treeModel != null) {
                        this.accessibleParent = this.tree;
                        this.index = 0;
                        this.setAccessibleParent(this.accessibleParent);
                    }
                }
                return this.accessibleParent;
            }
            
            @Override
            public int getAccessibleIndexInParent() {
                if (this.accessibleParent == null) {
                    this.getAccessibleParent();
                }
                final Object[] path = this.path.getPath();
                if (path.length > 1) {
                    final Object o = path[path.length - 2];
                    if (this.treeModel != null) {
                        this.index = this.treeModel.getIndexOfChild(o, this.obj);
                    }
                }
                return this.index;
            }
            
            @Override
            public int getAccessibleChildrenCount() {
                return this.treeModel.getChildCount(this.obj);
            }
            
            @Override
            public Accessible getAccessibleChild(final int n) {
                if (n < 0 || n >= this.getAccessibleChildrenCount()) {
                    return null;
                }
                final Object child = this.treeModel.getChild(this.obj, n);
                final Object[] path = this.path.getPath();
                final Object[] array = new Object[path.length + 1];
                System.arraycopy(path, 0, array, 0, path.length);
                array[array.length - 1] = child;
                return new AccessibleJTreeNode(JTree.this, new TreePath(array), this);
            }
            
            @Override
            public Locale getLocale() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    return currentAccessibleContext.getLocale();
                }
                return this.tree.getLocale();
            }
            
            @Override
            public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    currentAccessibleContext.addPropertyChangeListener(propertyChangeListener);
                }
                else {
                    super.addPropertyChangeListener(propertyChangeListener);
                }
            }
            
            @Override
            public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    currentAccessibleContext.removePropertyChangeListener(propertyChangeListener);
                }
                else {
                    super.removePropertyChangeListener(propertyChangeListener);
                }
            }
            
            @Override
            public AccessibleAction getAccessibleAction() {
                return this;
            }
            
            @Override
            public AccessibleComponent getAccessibleComponent() {
                return this;
            }
            
            @Override
            public AccessibleSelection getAccessibleSelection() {
                if (this.getCurrentAccessibleContext() != null && this.isLeaf) {
                    return this.getCurrentAccessibleContext().getAccessibleSelection();
                }
                return this;
            }
            
            @Override
            public AccessibleText getAccessibleText() {
                if (this.getCurrentAccessibleContext() != null) {
                    return this.getCurrentAccessibleContext().getAccessibleText();
                }
                return null;
            }
            
            @Override
            public AccessibleValue getAccessibleValue() {
                if (this.getCurrentAccessibleContext() != null) {
                    return this.getCurrentAccessibleContext().getAccessibleValue();
                }
                return null;
            }
            
            @Override
            public Color getBackground() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getBackground();
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getBackground();
                }
                return null;
            }
            
            @Override
            public void setBackground(final Color color) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setBackground(color);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setBackground(color);
                    }
                }
            }
            
            @Override
            public Color getForeground() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getForeground();
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getForeground();
                }
                return null;
            }
            
            @Override
            public void setForeground(final Color color) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setForeground(color);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setForeground(color);
                    }
                }
            }
            
            @Override
            public Cursor getCursor() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getCursor();
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getCursor();
                }
                final Accessible accessibleParent = this.getAccessibleParent();
                if (accessibleParent instanceof AccessibleComponent) {
                    return ((AccessibleComponent)accessibleParent).getCursor();
                }
                return null;
            }
            
            @Override
            public void setCursor(final Cursor cursor) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setCursor(cursor);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setCursor(cursor);
                    }
                }
            }
            
            @Override
            public Font getFont() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getFont();
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getFont();
                }
                return null;
            }
            
            @Override
            public void setFont(final Font font) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setFont(font);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setFont(font);
                    }
                }
            }
            
            @Override
            public FontMetrics getFontMetrics(final Font font) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getFontMetrics(font);
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getFontMetrics(font);
                }
                return null;
            }
            
            @Override
            public boolean isEnabled() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).isEnabled();
                }
                final Component currentComponent = this.getCurrentComponent();
                return currentComponent != null && currentComponent.isEnabled();
            }
            
            @Override
            public void setEnabled(final boolean b) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setEnabled(b);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setEnabled(b);
                    }
                }
            }
            
            @Override
            public boolean isVisible() {
                final Rectangle pathBounds = this.tree.getPathBounds(this.path);
                final Rectangle visibleRect = this.tree.getVisibleRect();
                return pathBounds != null && visibleRect != null && visibleRect.intersects(pathBounds);
            }
            
            @Override
            public void setVisible(final boolean b) {
            }
            
            @Override
            public boolean isShowing() {
                return this.tree.isShowing() && this.isVisible();
            }
            
            @Override
            public boolean contains(final Point point) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getBounds().contains(point);
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getBounds().contains(point);
                }
                return this.getBounds().contains(point);
            }
            
            @Override
            public Point getLocationOnScreen() {
                if (this.tree == null) {
                    return null;
                }
                final Point locationOnScreen = this.tree.getLocationOnScreen();
                final Rectangle pathBounds = this.tree.getPathBounds(this.path);
                if (locationOnScreen != null && pathBounds != null) {
                    final Point point = new Point(pathBounds.x, pathBounds.y);
                    point.translate(locationOnScreen.x, locationOnScreen.y);
                    return point;
                }
                return null;
            }
            
            protected Point getLocationInJTree() {
                final Rectangle pathBounds = this.tree.getPathBounds(this.path);
                if (pathBounds != null) {
                    return pathBounds.getLocation();
                }
                return null;
            }
            
            @Override
            public Point getLocation() {
                final Rectangle bounds = this.getBounds();
                if (bounds != null) {
                    return bounds.getLocation();
                }
                return null;
            }
            
            @Override
            public void setLocation(final Point point) {
            }
            
            @Override
            public Rectangle getBounds() {
                final Rectangle pathBounds = this.tree.getPathBounds(this.path);
                final Accessible accessibleParent = this.getAccessibleParent();
                if (accessibleParent != null && accessibleParent instanceof AccessibleJTreeNode) {
                    final Point locationInJTree = ((AccessibleJTreeNode)accessibleParent).getLocationInJTree();
                    if (locationInJTree == null || pathBounds == null) {
                        return null;
                    }
                    pathBounds.translate(-locationInJTree.x, -locationInJTree.y);
                }
                return pathBounds;
            }
            
            @Override
            public void setBounds(final Rectangle rectangle) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setBounds(rectangle);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setBounds(rectangle);
                    }
                }
            }
            
            @Override
            public Dimension getSize() {
                return this.getBounds().getSize();
            }
            
            @Override
            public void setSize(final Dimension dimension) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setSize(dimension);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setSize(dimension);
                    }
                }
            }
            
            @Override
            public Accessible getAccessibleAt(final Point point) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getAccessibleAt(point);
                }
                return null;
            }
            
            @Override
            public boolean isFocusTraversable() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).isFocusTraversable();
                }
                final Component currentComponent = this.getCurrentComponent();
                return currentComponent != null && currentComponent.isFocusTraversable();
            }
            
            @Override
            public void requestFocus() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).requestFocus();
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.requestFocus();
                    }
                }
            }
            
            @Override
            public void addFocusListener(final FocusListener focusListener) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).addFocusListener(focusListener);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.addFocusListener(focusListener);
                    }
                }
            }
            
            @Override
            public void removeFocusListener(final FocusListener focusListener) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).removeFocusListener(focusListener);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.removeFocusListener(focusListener);
                    }
                }
            }
            
            @Override
            public int getAccessibleSelectionCount() {
                int n = 0;
                for (int accessibleChildrenCount = this.getAccessibleChildrenCount(), i = 0; i < accessibleChildrenCount; ++i) {
                    if (this.tree.isPathSelected(this.getChildTreePath(i))) {
                        ++n;
                    }
                }
                return n;
            }
            
            @Override
            public Accessible getAccessibleSelection(final int n) {
                final int accessibleChildrenCount = this.getAccessibleChildrenCount();
                if (n < 0 || n >= accessibleChildrenCount) {
                    return null;
                }
                for (int n2 = 0, n3 = 0; n3 < accessibleChildrenCount && n >= n2; ++n3) {
                    final TreePath childTreePath = this.getChildTreePath(n3);
                    if (this.tree.isPathSelected(childTreePath)) {
                        if (n2 == n) {
                            return new AccessibleJTreeNode(this.tree, childTreePath, this);
                        }
                        ++n2;
                    }
                }
                return null;
            }
            
            @Override
            public boolean isAccessibleChildSelected(final int n) {
                final int accessibleChildrenCount = this.getAccessibleChildrenCount();
                return n >= 0 && n < accessibleChildrenCount && this.tree.isPathSelected(this.getChildTreePath(n));
            }
            
            @Override
            public void addAccessibleSelection(final int n) {
                if (JTree.this.getModel() != null && n >= 0 && n < this.getAccessibleChildrenCount()) {
                    JTree.this.addSelectionPath(this.getChildTreePath(n));
                }
            }
            
            @Override
            public void removeAccessibleSelection(final int n) {
                if (JTree.this.getModel() != null && n >= 0 && n < this.getAccessibleChildrenCount()) {
                    JTree.this.removeSelectionPath(this.getChildTreePath(n));
                }
            }
            
            @Override
            public void clearAccessibleSelection() {
                for (int accessibleChildrenCount = this.getAccessibleChildrenCount(), i = 0; i < accessibleChildrenCount; ++i) {
                    this.removeAccessibleSelection(i);
                }
            }
            
            @Override
            public void selectAllAccessibleSelection() {
                if (JTree.this.getModel() != null) {
                    for (int accessibleChildrenCount = this.getAccessibleChildrenCount(), i = 0; i < accessibleChildrenCount; ++i) {
                        JTree.this.addSelectionPath(this.getChildTreePath(i));
                    }
                }
            }
            
            @Override
            public int getAccessibleActionCount() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    final AccessibleAction accessibleAction = currentAccessibleContext.getAccessibleAction();
                    if (accessibleAction != null) {
                        return accessibleAction.getAccessibleActionCount() + (this.isLeaf ? 0 : 1);
                    }
                }
                return this.isLeaf ? 0 : 1;
            }
            
            @Override
            public String getAccessibleActionDescription(final int n) {
                if (n < 0 || n >= this.getAccessibleActionCount()) {
                    return null;
                }
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (n == 0) {
                    return AccessibleAction.TOGGLE_EXPAND;
                }
                if (currentAccessibleContext != null) {
                    final AccessibleAction accessibleAction = currentAccessibleContext.getAccessibleAction();
                    if (accessibleAction != null) {
                        return accessibleAction.getAccessibleActionDescription(n - 1);
                    }
                }
                return null;
            }
            
            @Override
            public boolean doAccessibleAction(final int n) {
                if (n < 0 || n >= this.getAccessibleActionCount()) {
                    return false;
                }
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (n == 0) {
                    if (JTree.this.isExpanded(this.path)) {
                        JTree.this.collapsePath(this.path);
                    }
                    else {
                        JTree.this.expandPath(this.path);
                    }
                    return true;
                }
                if (currentAccessibleContext != null) {
                    final AccessibleAction accessibleAction = currentAccessibleContext.getAccessibleAction();
                    if (accessibleAction != null) {
                        return accessibleAction.doAccessibleAction(n - 1);
                    }
                }
                return false;
            }
        }
    }
}
