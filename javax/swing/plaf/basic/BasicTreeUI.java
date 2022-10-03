package javax.swing.plaf.basic;

import sun.swing.UIAction;
import javax.swing.KeyStroke;
import javax.swing.text.Position;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.awt.datatransfer.Transferable;
import java.util.Comparator;
import java.awt.AWTEvent;
import javax.swing.event.MouseInputListener;
import javax.swing.AbstractAction;
import java.beans.PropertyChangeEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeModelEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import javax.swing.event.TreeExpansionEvent;
import java.awt.event.InputEvent;
import javax.swing.JTextField;
import java.awt.Point;
import sun.swing.SwingUtilities2;
import sun.awt.AWTAccessor;
import java.util.EventObject;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import java.awt.Container;
import java.util.Enumeration;
import java.awt.Graphics;
import javax.swing.UIDefaults;
import javax.swing.ActionMap;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.VariableHeightLayoutCache;
import javax.swing.tree.FixedHeightLayoutCache;
import sun.swing.DefaultLookup;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseMotionListener;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.Action;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import java.awt.event.MouseEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.CellEditorListener;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.awt.Component;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Hashtable;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.CellRendererPane;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.JTree;
import java.awt.Dimension;
import java.awt.Color;
import javax.swing.Icon;
import javax.swing.plaf.TreeUI;

public class BasicTreeUI extends TreeUI
{
    private static final StringBuilder BASELINE_COMPONENT_KEY;
    private static final Actions SHARED_ACTION;
    protected transient Icon collapsedIcon;
    protected transient Icon expandedIcon;
    private Color hashColor;
    protected int leftChildIndent;
    protected int rightChildIndent;
    protected int totalChildIndent;
    protected Dimension preferredMinSize;
    protected int lastSelectedRow;
    protected JTree tree;
    protected transient TreeCellRenderer currentCellRenderer;
    protected boolean createdRenderer;
    protected transient TreeCellEditor cellEditor;
    protected boolean createdCellEditor;
    protected boolean stopEditingInCompleteEditing;
    protected CellRendererPane rendererPane;
    protected Dimension preferredSize;
    protected boolean validCachedPreferredSize;
    protected AbstractLayoutCache treeState;
    protected Hashtable<TreePath, Boolean> drawingCache;
    protected boolean largeModel;
    protected AbstractLayoutCache.NodeDimensions nodeDimensions;
    protected TreeModel treeModel;
    protected TreeSelectionModel treeSelectionModel;
    protected int depthOffset;
    protected Component editingComponent;
    protected TreePath editingPath;
    protected int editingRow;
    protected boolean editorHasDifferentSize;
    private int leadRow;
    private boolean ignoreLAChange;
    private boolean leftToRight;
    private PropertyChangeListener propertyChangeListener;
    private PropertyChangeListener selectionModelPropertyChangeListener;
    private MouseListener mouseListener;
    private FocusListener focusListener;
    private KeyListener keyListener;
    private ComponentListener componentListener;
    private CellEditorListener cellEditorListener;
    private TreeSelectionListener treeSelectionListener;
    private TreeModelListener treeModelListener;
    private TreeExpansionListener treeExpansionListener;
    private boolean paintLines;
    private boolean lineTypeDashed;
    private long timeFactor;
    private Handler handler;
    private MouseEvent releaseEvent;
    private static final TransferHandler defaultTransferHandler;
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicTreeUI();
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("selectPrevious"));
        lazyActionMap.put(new Actions("selectPreviousChangeLead"));
        lazyActionMap.put(new Actions("selectPreviousExtendSelection"));
        lazyActionMap.put(new Actions("selectNext"));
        lazyActionMap.put(new Actions("selectNextChangeLead"));
        lazyActionMap.put(new Actions("selectNextExtendSelection"));
        lazyActionMap.put(new Actions("selectChild"));
        lazyActionMap.put(new Actions("selectChildChangeLead"));
        lazyActionMap.put(new Actions("selectParent"));
        lazyActionMap.put(new Actions("selectParentChangeLead"));
        lazyActionMap.put(new Actions("scrollUpChangeSelection"));
        lazyActionMap.put(new Actions("scrollUpChangeLead"));
        lazyActionMap.put(new Actions("scrollUpExtendSelection"));
        lazyActionMap.put(new Actions("scrollDownChangeSelection"));
        lazyActionMap.put(new Actions("scrollDownExtendSelection"));
        lazyActionMap.put(new Actions("scrollDownChangeLead"));
        lazyActionMap.put(new Actions("selectFirst"));
        lazyActionMap.put(new Actions("selectFirstChangeLead"));
        lazyActionMap.put(new Actions("selectFirstExtendSelection"));
        lazyActionMap.put(new Actions("selectLast"));
        lazyActionMap.put(new Actions("selectLastChangeLead"));
        lazyActionMap.put(new Actions("selectLastExtendSelection"));
        lazyActionMap.put(new Actions("toggle"));
        lazyActionMap.put(new Actions("cancel"));
        lazyActionMap.put(new Actions("startEditing"));
        lazyActionMap.put(new Actions("selectAll"));
        lazyActionMap.put(new Actions("clearSelection"));
        lazyActionMap.put(new Actions("scrollLeft"));
        lazyActionMap.put(new Actions("scrollRight"));
        lazyActionMap.put(new Actions("scrollLeftExtendSelection"));
        lazyActionMap.put(new Actions("scrollRightExtendSelection"));
        lazyActionMap.put(new Actions("scrollRightChangeLead"));
        lazyActionMap.put(new Actions("scrollLeftChangeLead"));
        lazyActionMap.put(new Actions("expand"));
        lazyActionMap.put(new Actions("collapse"));
        lazyActionMap.put(new Actions("moveSelectionToParent"));
        lazyActionMap.put(new Actions("addToSelection"));
        lazyActionMap.put(new Actions("toggleAndAnchor"));
        lazyActionMap.put(new Actions("extendTo"));
        lazyActionMap.put(new Actions("moveSelectionTo"));
        lazyActionMap.put(TransferHandler.getCutAction());
        lazyActionMap.put(TransferHandler.getCopyAction());
        lazyActionMap.put(TransferHandler.getPasteAction());
    }
    
    public BasicTreeUI() {
        this.paintLines = true;
        this.timeFactor = 1000L;
    }
    
    protected Color getHashColor() {
        return this.hashColor;
    }
    
    protected void setHashColor(final Color hashColor) {
        this.hashColor = hashColor;
    }
    
    public void setLeftChildIndent(final int leftChildIndent) {
        this.leftChildIndent = leftChildIndent;
        this.totalChildIndent = this.leftChildIndent + this.rightChildIndent;
        if (this.treeState != null) {
            this.treeState.invalidateSizes();
        }
        this.updateSize();
    }
    
    public int getLeftChildIndent() {
        return this.leftChildIndent;
    }
    
    public void setRightChildIndent(final int rightChildIndent) {
        this.rightChildIndent = rightChildIndent;
        this.totalChildIndent = this.leftChildIndent + this.rightChildIndent;
        if (this.treeState != null) {
            this.treeState.invalidateSizes();
        }
        this.updateSize();
    }
    
    public int getRightChildIndent() {
        return this.rightChildIndent;
    }
    
    public void setExpandedIcon(final Icon expandedIcon) {
        this.expandedIcon = expandedIcon;
    }
    
    public Icon getExpandedIcon() {
        return this.expandedIcon;
    }
    
    public void setCollapsedIcon(final Icon collapsedIcon) {
        this.collapsedIcon = collapsedIcon;
    }
    
    public Icon getCollapsedIcon() {
        return this.collapsedIcon;
    }
    
    protected void setLargeModel(boolean largeModel) {
        if (this.getRowHeight() < 1) {
            largeModel = false;
        }
        if (this.largeModel != largeModel) {
            this.completeEditing();
            this.largeModel = largeModel;
            this.treeState = this.createLayoutCache();
            this.configureLayoutCache();
            this.updateLayoutCacheExpandedNodesIfNecessary();
            this.updateSize();
        }
    }
    
    protected boolean isLargeModel() {
        return this.largeModel;
    }
    
    protected void setRowHeight(final int rowHeight) {
        this.completeEditing();
        if (this.treeState != null) {
            this.setLargeModel(this.tree.isLargeModel());
            this.treeState.setRowHeight(rowHeight);
            this.updateSize();
        }
    }
    
    protected int getRowHeight() {
        return (this.tree == null) ? -1 : this.tree.getRowHeight();
    }
    
    protected void setCellRenderer(final TreeCellRenderer treeCellRenderer) {
        this.completeEditing();
        this.updateRenderer();
        if (this.treeState != null) {
            this.treeState.invalidateSizes();
            this.updateSize();
        }
    }
    
    protected TreeCellRenderer getCellRenderer() {
        return this.currentCellRenderer;
    }
    
    protected void setModel(final TreeModel treeModel) {
        this.completeEditing();
        if (this.treeModel != null && this.treeModelListener != null) {
            this.treeModel.removeTreeModelListener(this.treeModelListener);
        }
        this.treeModel = treeModel;
        if (this.treeModel != null && this.treeModelListener != null) {
            this.treeModel.addTreeModelListener(this.treeModelListener);
        }
        if (this.treeState != null) {
            this.treeState.setModel(treeModel);
            this.updateLayoutCacheExpandedNodesIfNecessary();
            this.updateSize();
        }
    }
    
    protected TreeModel getModel() {
        return this.treeModel;
    }
    
    protected void setRootVisible(final boolean rootVisible) {
        this.completeEditing();
        this.updateDepthOffset();
        if (this.treeState != null) {
            this.treeState.setRootVisible(rootVisible);
            this.treeState.invalidateSizes();
            this.updateSize();
        }
    }
    
    protected boolean isRootVisible() {
        return this.tree != null && this.tree.isRootVisible();
    }
    
    protected void setShowsRootHandles(final boolean b) {
        this.completeEditing();
        this.updateDepthOffset();
        if (this.treeState != null) {
            this.treeState.invalidateSizes();
            this.updateSize();
        }
    }
    
    protected boolean getShowsRootHandles() {
        return this.tree != null && this.tree.getShowsRootHandles();
    }
    
    protected void setCellEditor(final TreeCellEditor treeCellEditor) {
        this.updateCellEditor();
    }
    
    protected TreeCellEditor getCellEditor() {
        return (this.tree != null) ? this.tree.getCellEditor() : null;
    }
    
    protected void setEditable(final boolean b) {
        this.updateCellEditor();
    }
    
    protected boolean isEditable() {
        return this.tree != null && this.tree.isEditable();
    }
    
    protected void setSelectionModel(final TreeSelectionModel treeSelectionModel) {
        this.completeEditing();
        if (this.selectionModelPropertyChangeListener != null && this.treeSelectionModel != null) {
            this.treeSelectionModel.removePropertyChangeListener(this.selectionModelPropertyChangeListener);
        }
        if (this.treeSelectionListener != null && this.treeSelectionModel != null) {
            this.treeSelectionModel.removeTreeSelectionListener(this.treeSelectionListener);
        }
        this.treeSelectionModel = treeSelectionModel;
        if (this.treeSelectionModel != null) {
            if (this.selectionModelPropertyChangeListener != null) {
                this.treeSelectionModel.addPropertyChangeListener(this.selectionModelPropertyChangeListener);
            }
            if (this.treeSelectionListener != null) {
                this.treeSelectionModel.addTreeSelectionListener(this.treeSelectionListener);
            }
            if (this.treeState != null) {
                this.treeState.setSelectionModel(this.treeSelectionModel);
            }
        }
        else if (this.treeState != null) {
            this.treeState.setSelectionModel(null);
        }
        if (this.tree != null) {
            this.tree.repaint();
        }
    }
    
    protected TreeSelectionModel getSelectionModel() {
        return this.treeSelectionModel;
    }
    
    @Override
    public Rectangle getPathBounds(final JTree tree, final TreePath treePath) {
        if (tree != null && this.treeState != null) {
            return this.getPathBounds(treePath, tree.getInsets(), new Rectangle());
        }
        return null;
    }
    
    private Rectangle getPathBounds(final TreePath treePath, final Insets insets, Rectangle bounds) {
        bounds = this.treeState.getBounds(treePath, bounds);
        if (bounds != null) {
            if (this.leftToRight) {
                final Rectangle rectangle = bounds;
                rectangle.x += insets.left;
            }
            else {
                bounds.x = this.tree.getWidth() - (bounds.x + bounds.width) - insets.right;
            }
            final Rectangle rectangle2 = bounds;
            rectangle2.y += insets.top;
        }
        return bounds;
    }
    
    @Override
    public TreePath getPathForRow(final JTree tree, final int n) {
        return (this.treeState != null) ? this.treeState.getPathForRow(n) : null;
    }
    
    @Override
    public int getRowForPath(final JTree tree, final TreePath treePath) {
        return (this.treeState != null) ? this.treeState.getRowForPath(treePath) : -1;
    }
    
    @Override
    public int getRowCount(final JTree tree) {
        return (this.treeState != null) ? this.treeState.getRowCount() : 0;
    }
    
    @Override
    public TreePath getClosestPathForLocation(final JTree tree, final int n, int n2) {
        if (tree != null && this.treeState != null) {
            n2 -= tree.getInsets().top;
            return this.treeState.getPathClosestTo(n, n2);
        }
        return null;
    }
    
    @Override
    public boolean isEditing(final JTree tree) {
        return this.editingComponent != null;
    }
    
    @Override
    public boolean stopEditing(final JTree tree) {
        if (this.editingComponent != null && this.cellEditor.stopCellEditing()) {
            this.completeEditing(false, false, true);
            return true;
        }
        return false;
    }
    
    @Override
    public void cancelEditing(final JTree tree) {
        if (this.editingComponent != null) {
            this.completeEditing(false, true, false);
        }
    }
    
    @Override
    public void startEditingAtPath(final JTree tree, final TreePath treePath) {
        tree.scrollPathToVisible(treePath);
        if (treePath != null && tree.isVisible(treePath)) {
            this.startEditing(treePath, null);
        }
    }
    
    @Override
    public TreePath getEditingPath(final JTree tree) {
        return this.editingPath;
    }
    
    @Override
    public void installUI(final JComponent component) {
        if (component == null) {
            throw new NullPointerException("null component passed to BasicTreeUI.installUI()");
        }
        this.tree = (JTree)component;
        this.prepareForUIInstall();
        this.installDefaults();
        this.installKeyboardActions();
        this.installComponents();
        this.installListeners();
        this.completeUIInstall();
    }
    
    protected void prepareForUIInstall() {
        this.drawingCache = new Hashtable<TreePath, Boolean>(7);
        this.leftToRight = BasicGraphicsUtils.isLeftToRight(this.tree);
        this.stopEditingInCompleteEditing = true;
        this.lastSelectedRow = -1;
        this.leadRow = -1;
        this.preferredSize = new Dimension();
        this.largeModel = this.tree.isLargeModel();
        if (this.getRowHeight() <= 0) {
            this.largeModel = false;
        }
        this.setModel(this.tree.getModel());
    }
    
    protected void completeUIInstall() {
        this.setShowsRootHandles(this.tree.getShowsRootHandles());
        this.updateRenderer();
        this.updateDepthOffset();
        this.setSelectionModel(this.tree.getSelectionModel());
        this.treeState = this.createLayoutCache();
        this.configureLayoutCache();
        this.updateSize();
    }
    
    protected void installDefaults() {
        if (this.tree.getBackground() == null || this.tree.getBackground() instanceof UIResource) {
            this.tree.setBackground(UIManager.getColor("Tree.background"));
        }
        if (this.getHashColor() == null || this.getHashColor() instanceof UIResource) {
            this.setHashColor(UIManager.getColor("Tree.hash"));
        }
        if (this.tree.getFont() == null || this.tree.getFont() instanceof UIResource) {
            this.tree.setFont(UIManager.getFont("Tree.font"));
        }
        this.setExpandedIcon((Icon)UIManager.get("Tree.expandedIcon"));
        this.setCollapsedIcon((Icon)UIManager.get("Tree.collapsedIcon"));
        this.setLeftChildIndent((int)UIManager.get("Tree.leftChildIndent"));
        this.setRightChildIndent((int)UIManager.get("Tree.rightChildIndent"));
        LookAndFeel.installProperty(this.tree, "rowHeight", UIManager.get("Tree.rowHeight"));
        this.largeModel = (this.tree.isLargeModel() && this.tree.getRowHeight() > 0);
        final Object value = UIManager.get("Tree.scrollsOnExpand");
        if (value != null) {
            LookAndFeel.installProperty(this.tree, "scrollsOnExpand", value);
        }
        this.paintLines = UIManager.getBoolean("Tree.paintLines");
        this.lineTypeDashed = UIManager.getBoolean("Tree.lineTypeDashed");
        final Long n = (Long)UIManager.get("Tree.timeFactor");
        this.timeFactor = ((n != null) ? n : 1000L);
        final Object value2 = UIManager.get("Tree.showsRootHandles");
        if (value2 != null) {
            LookAndFeel.installProperty(this.tree, "showsRootHandles", value2);
        }
    }
    
    protected void installListeners() {
        final PropertyChangeListener propertyChangeListener = this.createPropertyChangeListener();
        this.propertyChangeListener = propertyChangeListener;
        if (propertyChangeListener != null) {
            this.tree.addPropertyChangeListener(this.propertyChangeListener);
        }
        if ((this.mouseListener = this.createMouseListener()) != null) {
            this.tree.addMouseListener(this.mouseListener);
            if (this.mouseListener instanceof MouseMotionListener) {
                this.tree.addMouseMotionListener((MouseMotionListener)this.mouseListener);
            }
        }
        if ((this.focusListener = this.createFocusListener()) != null) {
            this.tree.addFocusListener(this.focusListener);
        }
        if ((this.keyListener = this.createKeyListener()) != null) {
            this.tree.addKeyListener(this.keyListener);
        }
        if ((this.treeExpansionListener = this.createTreeExpansionListener()) != null) {
            this.tree.addTreeExpansionListener(this.treeExpansionListener);
        }
        final TreeModelListener treeModelListener = this.createTreeModelListener();
        this.treeModelListener = treeModelListener;
        if (treeModelListener != null && this.treeModel != null) {
            this.treeModel.addTreeModelListener(this.treeModelListener);
        }
        if ((this.selectionModelPropertyChangeListener = this.createSelectionModelPropertyChangeListener()) != null && this.treeSelectionModel != null) {
            this.treeSelectionModel.addPropertyChangeListener(this.selectionModelPropertyChangeListener);
        }
        if ((this.treeSelectionListener = this.createTreeSelectionListener()) != null && this.treeSelectionModel != null) {
            this.treeSelectionModel.addTreeSelectionListener(this.treeSelectionListener);
        }
        final TransferHandler transferHandler = this.tree.getTransferHandler();
        if (transferHandler == null || transferHandler instanceof UIResource) {
            this.tree.setTransferHandler(BasicTreeUI.defaultTransferHandler);
            if (this.tree.getDropTarget() instanceof UIResource) {
                this.tree.setDropTarget(null);
            }
        }
        LookAndFeel.installProperty(this.tree, "opaque", Boolean.TRUE);
    }
    
    protected void installKeyboardActions() {
        SwingUtilities.replaceUIInputMap(this.tree, 1, this.getInputMap(1));
        SwingUtilities.replaceUIInputMap(this.tree, 0, this.getInputMap(0));
        LazyActionMap.installLazyActionMap(this.tree, BasicTreeUI.class, "Tree.actionMap");
    }
    
    InputMap getInputMap(final int n) {
        if (n == 1) {
            return (InputMap)DefaultLookup.get(this.tree, this, "Tree.ancestorInputMap");
        }
        if (n != 0) {
            return null;
        }
        final InputMap parent = (InputMap)DefaultLookup.get(this.tree, this, "Tree.focusInputMap");
        final InputMap inputMap;
        if (this.tree.getComponentOrientation().isLeftToRight() || (inputMap = (InputMap)DefaultLookup.get(this.tree, this, "Tree.focusInputMap.RightToLeft")) == null) {
            return parent;
        }
        inputMap.setParent(parent);
        return inputMap;
    }
    
    protected void installComponents() {
        final CellRendererPane cellRendererPane = this.createCellRendererPane();
        this.rendererPane = cellRendererPane;
        if (cellRendererPane != null) {
            this.tree.add(this.rendererPane);
        }
    }
    
    protected AbstractLayoutCache.NodeDimensions createNodeDimensions() {
        return new NodeDimensionsHandler();
    }
    
    protected PropertyChangeListener createPropertyChangeListener() {
        return this.getHandler();
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    protected MouseListener createMouseListener() {
        return this.getHandler();
    }
    
    protected FocusListener createFocusListener() {
        return this.getHandler();
    }
    
    protected KeyListener createKeyListener() {
        return this.getHandler();
    }
    
    protected PropertyChangeListener createSelectionModelPropertyChangeListener() {
        return this.getHandler();
    }
    
    protected TreeSelectionListener createTreeSelectionListener() {
        return this.getHandler();
    }
    
    protected CellEditorListener createCellEditorListener() {
        return this.getHandler();
    }
    
    protected ComponentListener createComponentListener() {
        return new ComponentHandler();
    }
    
    protected TreeExpansionListener createTreeExpansionListener() {
        return this.getHandler();
    }
    
    protected AbstractLayoutCache createLayoutCache() {
        if (this.isLargeModel() && this.getRowHeight() > 0) {
            return new FixedHeightLayoutCache();
        }
        return new VariableHeightLayoutCache();
    }
    
    protected CellRendererPane createCellRendererPane() {
        return new CellRendererPane();
    }
    
    protected TreeCellEditor createDefaultCellEditor() {
        if (this.currentCellRenderer != null && this.currentCellRenderer instanceof DefaultTreeCellRenderer) {
            return new DefaultTreeCellEditor(this.tree, (DefaultTreeCellRenderer)this.currentCellRenderer);
        }
        return new DefaultTreeCellEditor(this.tree, null);
    }
    
    protected TreeCellRenderer createDefaultCellRenderer() {
        return new DefaultTreeCellRenderer();
    }
    
    protected TreeModelListener createTreeModelListener() {
        return this.getHandler();
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.completeEditing();
        this.prepareForUIUninstall();
        this.uninstallDefaults();
        this.uninstallListeners();
        this.uninstallKeyboardActions();
        this.uninstallComponents();
        this.completeUIUninstall();
    }
    
    protected void prepareForUIUninstall() {
    }
    
    protected void completeUIUninstall() {
        if (this.createdRenderer) {
            this.tree.setCellRenderer(null);
        }
        if (this.createdCellEditor) {
            this.tree.setCellEditor(null);
        }
        this.cellEditor = null;
        this.currentCellRenderer = null;
        this.rendererPane = null;
        this.componentListener = null;
        this.propertyChangeListener = null;
        this.mouseListener = null;
        this.focusListener = null;
        this.keyListener = null;
        this.setSelectionModel(null);
        this.treeState = null;
        this.drawingCache = null;
        this.selectionModelPropertyChangeListener = null;
        this.tree = null;
        this.treeModel = null;
        this.treeSelectionModel = null;
        this.treeSelectionListener = null;
        this.treeExpansionListener = null;
    }
    
    protected void uninstallDefaults() {
        if (this.tree.getTransferHandler() instanceof UIResource) {
            this.tree.setTransferHandler(null);
        }
    }
    
    protected void uninstallListeners() {
        if (this.componentListener != null) {
            this.tree.removeComponentListener(this.componentListener);
        }
        if (this.propertyChangeListener != null) {
            this.tree.removePropertyChangeListener(this.propertyChangeListener);
        }
        if (this.mouseListener != null) {
            this.tree.removeMouseListener(this.mouseListener);
            if (this.mouseListener instanceof MouseMotionListener) {
                this.tree.removeMouseMotionListener((MouseMotionListener)this.mouseListener);
            }
        }
        if (this.focusListener != null) {
            this.tree.removeFocusListener(this.focusListener);
        }
        if (this.keyListener != null) {
            this.tree.removeKeyListener(this.keyListener);
        }
        if (this.treeExpansionListener != null) {
            this.tree.removeTreeExpansionListener(this.treeExpansionListener);
        }
        if (this.treeModel != null && this.treeModelListener != null) {
            this.treeModel.removeTreeModelListener(this.treeModelListener);
        }
        if (this.selectionModelPropertyChangeListener != null && this.treeSelectionModel != null) {
            this.treeSelectionModel.removePropertyChangeListener(this.selectionModelPropertyChangeListener);
        }
        if (this.treeSelectionListener != null && this.treeSelectionModel != null) {
            this.treeSelectionModel.removeTreeSelectionListener(this.treeSelectionListener);
        }
        this.handler = null;
    }
    
    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIActionMap(this.tree, null);
        SwingUtilities.replaceUIInputMap(this.tree, 1, null);
        SwingUtilities.replaceUIInputMap(this.tree, 0, null);
    }
    
    protected void uninstallComponents() {
        if (this.rendererPane != null) {
            this.tree.remove(this.rendererPane);
        }
    }
    
    private void redoTheLayout() {
        if (this.treeState != null) {
            this.treeState.invalidateSizes();
        }
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, final int n2) {
        super.getBaseline(component, n, n2);
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        Component treeCellRendererComponent = (Component)lookAndFeelDefaults.get(BasicTreeUI.BASELINE_COMPONENT_KEY);
        if (treeCellRendererComponent == null) {
            treeCellRendererComponent = this.createDefaultCellRenderer().getTreeCellRendererComponent(this.tree, "a", false, false, false, -1, false);
            lookAndFeelDefaults.put(BasicTreeUI.BASELINE_COMPONENT_KEY, treeCellRendererComponent);
        }
        final int rowHeight = this.tree.getRowHeight();
        int n3;
        if (rowHeight > 0) {
            n3 = treeCellRendererComponent.getBaseline(Integer.MAX_VALUE, rowHeight);
        }
        else {
            final Dimension preferredSize = treeCellRendererComponent.getPreferredSize();
            n3 = treeCellRendererComponent.getBaseline(preferredSize.width, preferredSize.height);
        }
        return n3 + this.tree.getInsets().top;
    }
    
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final JComponent component) {
        super.getBaselineResizeBehavior(component);
        return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        if (this.tree != component) {
            throw new InternalError("incorrect component");
        }
        if (this.treeState == null) {
            return;
        }
        final Rectangle clipBounds = graphics.getClipBounds();
        final Insets insets = this.tree.getInsets();
        final TreePath closestPathForLocation = this.getClosestPathForLocation(this.tree, 0, clipBounds.y);
        final Enumeration<TreePath> visiblePaths = this.treeState.getVisiblePathsFrom(closestPathForLocation);
        int rowForPath = this.treeState.getRowForPath(closestPathForLocation);
        final int n = clipBounds.y + clipBounds.height;
        this.drawingCache.clear();
        if (closestPathForLocation != null && visiblePaths != null) {
            for (TreePath treePath = closestPathForLocation.getParentPath(); treePath != null; treePath = treePath.getParentPath()) {
                this.paintVerticalPartOfLeg(graphics, clipBounds, insets, treePath);
                this.drawingCache.put(treePath, Boolean.TRUE);
            }
            int n2 = 0;
            final Rectangle rectangle = new Rectangle();
            final boolean rootVisible = this.isRootVisible();
            while (n2 == 0 && visiblePaths.hasMoreElements()) {
                final TreePath treePath2 = visiblePaths.nextElement();
                if (treePath2 != null) {
                    final boolean leaf = this.treeModel.isLeaf(treePath2.getLastPathComponent());
                    boolean expandedState;
                    boolean hasBeenExpanded;
                    if (leaf) {
                        hasBeenExpanded = (expandedState = false);
                    }
                    else {
                        expandedState = this.treeState.getExpandedState(treePath2);
                        hasBeenExpanded = this.tree.hasBeenExpanded(treePath2);
                    }
                    final Rectangle pathBounds = this.getPathBounds(treePath2, insets, rectangle);
                    if (pathBounds == null) {
                        return;
                    }
                    final TreePath parentPath = treePath2.getParentPath();
                    if (parentPath != null) {
                        if (this.drawingCache.get(parentPath) == null) {
                            this.paintVerticalPartOfLeg(graphics, clipBounds, insets, parentPath);
                            this.drawingCache.put(parentPath, Boolean.TRUE);
                        }
                        this.paintHorizontalPartOfLeg(graphics, clipBounds, insets, pathBounds, treePath2, rowForPath, expandedState, hasBeenExpanded, leaf);
                    }
                    else if (rootVisible && rowForPath == 0) {
                        this.paintHorizontalPartOfLeg(graphics, clipBounds, insets, pathBounds, treePath2, rowForPath, expandedState, hasBeenExpanded, leaf);
                    }
                    if (this.shouldPaintExpandControl(treePath2, rowForPath, expandedState, hasBeenExpanded, leaf)) {
                        this.paintExpandControl(graphics, clipBounds, insets, pathBounds, treePath2, rowForPath, expandedState, hasBeenExpanded, leaf);
                    }
                    this.paintRow(graphics, clipBounds, insets, pathBounds, treePath2, rowForPath, expandedState, hasBeenExpanded, leaf);
                    if (pathBounds.y + pathBounds.height >= n) {
                        n2 = 1;
                    }
                }
                else {
                    n2 = 1;
                }
                ++rowForPath;
            }
        }
        this.paintDropLine(graphics);
        this.rendererPane.removeAll();
        this.drawingCache.clear();
    }
    
    protected boolean isDropLine(final JTree.DropLocation dropLocation) {
        return dropLocation != null && dropLocation.getPath() != null && dropLocation.getChildIndex() != -1;
    }
    
    protected void paintDropLine(final Graphics graphics) {
        final JTree.DropLocation dropLocation = this.tree.getDropLocation();
        if (!this.isDropLine(dropLocation)) {
            return;
        }
        final Color color = UIManager.getColor("Tree.dropLineColor");
        if (color != null) {
            graphics.setColor(color);
            final Rectangle dropLineRect = this.getDropLineRect(dropLocation);
            graphics.fillRect(dropLineRect.x, dropLineRect.y, dropLineRect.width, dropLineRect.height);
        }
    }
    
    protected Rectangle getDropLineRect(final JTree.DropLocation dropLocation) {
        final TreePath path = dropLocation.getPath();
        final int childIndex = dropLocation.getChildIndex();
        final boolean leftToRight = this.leftToRight;
        final Insets insets = this.tree.getInsets();
        Rectangle rectangle;
        if (this.tree.getRowCount() == 0) {
            rectangle = new Rectangle(insets.left, insets.top, this.tree.getWidth() - insets.left - insets.right, 0);
        }
        else {
            final TreeModel model = this.getModel();
            final Object root = model.getRoot();
            if (path.getLastPathComponent() == root && childIndex >= model.getChildCount(root)) {
                rectangle = this.tree.getRowBounds(this.tree.getRowCount() - 1);
                rectangle.y += rectangle.height;
                Rectangle rectangle2;
                if (!this.tree.isRootVisible()) {
                    rectangle2 = this.tree.getRowBounds(0);
                }
                else if (model.getChildCount(root) == 0) {
                    final Rectangle rowBounds;
                    rectangle2 = (rowBounds = this.tree.getRowBounds(0));
                    rowBounds.x += this.totalChildIndent;
                    final Rectangle rectangle3 = rectangle2;
                    rectangle3.width -= this.totalChildIndent + this.totalChildIndent;
                }
                else {
                    rectangle2 = this.tree.getPathBounds(path.pathByAddingChild(model.getChild(root, model.getChildCount(root) - 1)));
                }
                rectangle.x = rectangle2.x;
                rectangle.width = rectangle2.width;
            }
            else {
                rectangle = this.tree.getPathBounds(path.pathByAddingChild(model.getChild(path.getLastPathComponent(), childIndex)));
            }
        }
        if (rectangle.y != 0) {
            final Rectangle rectangle4 = rectangle;
            --rectangle4.y;
        }
        if (!leftToRight) {
            rectangle.x = rectangle.x + rectangle.width - 100;
        }
        rectangle.width = 100;
        rectangle.height = 2;
        return rectangle;
    }
    
    protected void paintHorizontalPartOfLeg(final Graphics graphics, final Rectangle rectangle, final Insets insets, final Rectangle rectangle2, final TreePath treePath, final int n, final boolean b, final boolean b2, final boolean b3) {
        if (!this.paintLines) {
            return;
        }
        final int n2 = treePath.getPathCount() - 1;
        if ((n2 == 0 || (n2 == 1 && !this.isRootVisible())) && !this.getShowsRootHandles()) {
            return;
        }
        final int x = rectangle.x;
        final int n3 = rectangle.x + rectangle.width;
        final int y = rectangle.y;
        final int n4 = rectangle.y + rectangle.height;
        final int n5 = rectangle2.y + rectangle2.height / 2;
        if (this.leftToRight) {
            final int n6 = rectangle2.x - this.getRightChildIndent();
            final int n7 = rectangle2.x - this.getHorizontalLegBuffer();
            if (n5 >= y && n5 < n4 && n7 >= x && n6 < n3 && n6 < n7) {
                graphics.setColor(this.getHashColor());
                this.paintHorizontalLine(graphics, this.tree, n5, n6, n7 - 1);
            }
        }
        else {
            final int n8 = rectangle2.x + rectangle2.width + this.getHorizontalLegBuffer();
            final int n9 = rectangle2.x + rectangle2.width + this.getRightChildIndent();
            if (n5 >= y && n5 < n4 && n9 >= x && n8 < n3 && n8 < n9) {
                graphics.setColor(this.getHashColor());
                this.paintHorizontalLine(graphics, this.tree, n5, n8, n9 - 1);
            }
        }
    }
    
    protected void paintVerticalPartOfLeg(final Graphics graphics, final Rectangle rectangle, final Insets insets, final TreePath treePath) {
        if (!this.paintLines) {
            return;
        }
        final int n = treePath.getPathCount() - 1;
        if (n == 0 && !this.getShowsRootHandles() && !this.isRootVisible()) {
            return;
        }
        final int rowX = this.getRowX(-1, n + 1);
        int n2;
        if (this.leftToRight) {
            n2 = rowX - this.getRightChildIndent() + insets.left;
        }
        else {
            n2 = this.tree.getWidth() - rowX - insets.right + this.getRightChildIndent() - 1;
        }
        final int x = rectangle.x;
        final int n3 = rectangle.x + (rectangle.width - 1);
        if (n2 >= x && n2 <= n3) {
            final int y = rectangle.y;
            final int n4 = rectangle.y + rectangle.height;
            final Rectangle pathBounds = this.getPathBounds(this.tree, treePath);
            final Rectangle pathBounds2 = this.getPathBounds(this.tree, this.getLastChildPath(treePath));
            if (pathBounds2 == null) {
                return;
            }
            int n5;
            if (pathBounds == null) {
                n5 = Math.max(insets.top + this.getVerticalLegBuffer(), y);
            }
            else {
                n5 = Math.max(pathBounds.y + pathBounds.height + this.getVerticalLegBuffer(), y);
            }
            if (n == 0 && !this.isRootVisible()) {
                final TreeModel model = this.getModel();
                if (model != null) {
                    final Object root = model.getRoot();
                    if (model.getChildCount(root) > 0) {
                        final Rectangle pathBounds3 = this.getPathBounds(this.tree, treePath.pathByAddingChild(model.getChild(root, 0)));
                        if (pathBounds3 != null) {
                            n5 = Math.max(insets.top + this.getVerticalLegBuffer(), pathBounds3.y + pathBounds3.height / 2);
                        }
                    }
                }
            }
            final int min = Math.min(pathBounds2.y + pathBounds2.height / 2, n4);
            if (n5 <= min) {
                graphics.setColor(this.getHashColor());
                this.paintVerticalLine(graphics, this.tree, n2, n5, min);
            }
        }
    }
    
    protected void paintExpandControl(final Graphics graphics, final Rectangle rectangle, final Insets insets, final Rectangle rectangle2, final TreePath treePath, final int n, final boolean b, final boolean b2, final boolean b3) {
        final Object lastPathComponent = treePath.getLastPathComponent();
        if (!b3 && (!b2 || this.treeModel.getChildCount(lastPathComponent) > 0)) {
            int n2;
            if (this.leftToRight) {
                n2 = rectangle2.x - this.getRightChildIndent() + 1;
            }
            else {
                n2 = rectangle2.x + rectangle2.width + this.getRightChildIndent() - 1;
            }
            final int n3 = rectangle2.y + rectangle2.height / 2;
            if (b) {
                final Icon expandedIcon = this.getExpandedIcon();
                if (expandedIcon != null) {
                    this.drawCentered(this.tree, graphics, expandedIcon, n2, n3);
                }
            }
            else {
                final Icon collapsedIcon = this.getCollapsedIcon();
                if (collapsedIcon != null) {
                    this.drawCentered(this.tree, graphics, collapsedIcon, n2, n3);
                }
            }
        }
    }
    
    protected void paintRow(final Graphics graphics, final Rectangle rectangle, final Insets insets, final Rectangle rectangle2, final TreePath treePath, final int n, final boolean b, final boolean b2, final boolean b3) {
        if (this.editingComponent != null && this.editingRow == n) {
            return;
        }
        int leadSelectionRow;
        if (this.tree.hasFocus()) {
            leadSelectionRow = this.getLeadSelectionRow();
        }
        else {
            leadSelectionRow = -1;
        }
        this.rendererPane.paintComponent(graphics, this.currentCellRenderer.getTreeCellRendererComponent(this.tree, treePath.getLastPathComponent(), this.tree.isRowSelected(n), b, b3, n, leadSelectionRow == n), this.tree, rectangle2.x, rectangle2.y, rectangle2.width, rectangle2.height, true);
    }
    
    protected boolean shouldPaintExpandControl(final TreePath treePath, final int n, final boolean b, final boolean b2, final boolean b3) {
        if (b3) {
            return false;
        }
        final int n2 = treePath.getPathCount() - 1;
        return (n2 != 0 && (n2 != 1 || this.isRootVisible())) || this.getShowsRootHandles();
    }
    
    protected void paintVerticalLine(final Graphics graphics, final JComponent component, final int n, final int n2, final int n3) {
        if (this.lineTypeDashed) {
            this.drawDashedVerticalLine(graphics, n, n2, n3);
        }
        else {
            graphics.drawLine(n, n2, n, n3);
        }
    }
    
    protected void paintHorizontalLine(final Graphics graphics, final JComponent component, final int n, final int n2, final int n3) {
        if (this.lineTypeDashed) {
            this.drawDashedHorizontalLine(graphics, n, n2, n3);
        }
        else {
            graphics.drawLine(n2, n, n3, n);
        }
    }
    
    protected int getVerticalLegBuffer() {
        return 0;
    }
    
    protected int getHorizontalLegBuffer() {
        return 0;
    }
    
    private int findCenteredX(final int n, final int n2) {
        return this.leftToRight ? (n - (int)Math.ceil(n2 / 2.0)) : (n - (int)Math.floor(n2 / 2.0));
    }
    
    protected void drawCentered(final Component component, final Graphics graphics, final Icon icon, final int n, final int n2) {
        icon.paintIcon(component, graphics, this.findCenteredX(n, icon.getIconWidth()), n2 - icon.getIconHeight() / 2);
    }
    
    protected void drawDashedHorizontalLine(final Graphics graphics, final int n, int n2, final int n3) {
        int i;
        for (n2 = (i = n2 + n2 % 2); i <= n3; i += 2) {
            graphics.drawLine(i, n, i, n);
        }
    }
    
    protected void drawDashedVerticalLine(final Graphics graphics, final int n, int n2, final int n3) {
        int i;
        for (n2 = (i = n2 + n2 % 2); i <= n3; i += 2) {
            graphics.drawLine(n, i, n, i);
        }
    }
    
    protected int getRowX(final int n, final int n2) {
        return this.totalChildIndent * (n2 + this.depthOffset);
    }
    
    protected void updateLayoutCacheExpandedNodes() {
        if (this.treeModel != null && this.treeModel.getRoot() != null) {
            this.updateExpandedDescendants(new TreePath(this.treeModel.getRoot()));
        }
    }
    
    private void updateLayoutCacheExpandedNodesIfNecessary() {
        if (this.treeModel != null && this.treeModel.getRoot() != null) {
            final TreePath treePath = new TreePath(this.treeModel.getRoot());
            if (this.tree.isExpanded(treePath)) {
                this.updateLayoutCacheExpandedNodes();
            }
            else {
                this.treeState.setExpandedState(treePath, false);
            }
        }
    }
    
    protected void updateExpandedDescendants(TreePath treePath) {
        this.completeEditing();
        if (this.treeState != null) {
            this.treeState.setExpandedState(treePath, true);
            final Enumeration<TreePath> expandedDescendants = this.tree.getExpandedDescendants(treePath);
            if (expandedDescendants != null) {
                while (expandedDescendants.hasMoreElements()) {
                    treePath = expandedDescendants.nextElement();
                    this.treeState.setExpandedState(treePath, true);
                }
            }
            this.updateLeadSelectionRow();
            this.updateSize();
        }
    }
    
    protected TreePath getLastChildPath(final TreePath treePath) {
        if (this.treeModel != null) {
            final int childCount = this.treeModel.getChildCount(treePath.getLastPathComponent());
            if (childCount > 0) {
                return treePath.pathByAddingChild(this.treeModel.getChild(treePath.getLastPathComponent(), childCount - 1));
            }
        }
        return null;
    }
    
    protected void updateDepthOffset() {
        if (this.isRootVisible()) {
            if (this.getShowsRootHandles()) {
                this.depthOffset = 1;
            }
            else {
                this.depthOffset = 0;
            }
        }
        else if (!this.getShowsRootHandles()) {
            this.depthOffset = -1;
        }
        else {
            this.depthOffset = 0;
        }
    }
    
    protected void updateCellEditor() {
        this.completeEditing();
        TreeCellEditor treeCellEditor;
        if (this.tree == null) {
            treeCellEditor = null;
        }
        else if (this.tree.isEditable()) {
            treeCellEditor = this.tree.getCellEditor();
            if (treeCellEditor == null) {
                treeCellEditor = this.createDefaultCellEditor();
                if (treeCellEditor != null) {
                    this.tree.setCellEditor(treeCellEditor);
                    this.createdCellEditor = true;
                }
            }
        }
        else {
            treeCellEditor = null;
        }
        if (treeCellEditor != this.cellEditor) {
            if (this.cellEditor != null && this.cellEditorListener != null) {
                this.cellEditor.removeCellEditorListener(this.cellEditorListener);
            }
            this.cellEditor = treeCellEditor;
            if (this.cellEditorListener == null) {
                this.cellEditorListener = this.createCellEditorListener();
            }
            if (treeCellEditor != null && this.cellEditorListener != null) {
                treeCellEditor.addCellEditorListener(this.cellEditorListener);
            }
            this.createdCellEditor = false;
        }
    }
    
    protected void updateRenderer() {
        if (this.tree != null) {
            final TreeCellRenderer cellRenderer = this.tree.getCellRenderer();
            if (cellRenderer == null) {
                this.tree.setCellRenderer(this.createDefaultCellRenderer());
                this.createdRenderer = true;
            }
            else {
                this.createdRenderer = false;
                this.currentCellRenderer = cellRenderer;
                if (this.createdCellEditor) {
                    this.tree.setCellEditor(null);
                }
            }
        }
        else {
            this.createdRenderer = false;
            this.currentCellRenderer = null;
        }
        this.updateCellEditor();
    }
    
    protected void configureLayoutCache() {
        if (this.treeState != null && this.tree != null) {
            if (this.nodeDimensions == null) {
                this.nodeDimensions = this.createNodeDimensions();
            }
            this.treeState.setNodeDimensions(this.nodeDimensions);
            this.treeState.setRootVisible(this.tree.isRootVisible());
            this.treeState.setRowHeight(this.tree.getRowHeight());
            this.treeState.setSelectionModel(this.getSelectionModel());
            if (this.treeState.getModel() != this.tree.getModel()) {
                this.treeState.setModel(this.tree.getModel());
            }
            this.updateLayoutCacheExpandedNodesIfNecessary();
            if (this.isLargeModel()) {
                if (this.componentListener == null) {
                    this.componentListener = this.createComponentListener();
                    if (this.componentListener != null) {
                        this.tree.addComponentListener(this.componentListener);
                    }
                }
            }
            else if (this.componentListener != null) {
                this.tree.removeComponentListener(this.componentListener);
                this.componentListener = null;
            }
        }
        else if (this.componentListener != null) {
            this.tree.removeComponentListener(this.componentListener);
            this.componentListener = null;
        }
    }
    
    protected void updateSize() {
        this.validCachedPreferredSize = false;
        this.tree.treeDidChange();
    }
    
    private void updateSize0() {
        this.validCachedPreferredSize = false;
        this.tree.revalidate();
    }
    
    protected void updateCachedPreferredSize() {
        if (this.treeState != null) {
            final Insets insets = this.tree.getInsets();
            if (this.isLargeModel()) {
                final Rectangle visibleRect = this.tree.getVisibleRect();
                if (visibleRect.x == 0 && visibleRect.y == 0 && visibleRect.width == 0 && visibleRect.height == 0 && this.tree.getVisibleRowCount() > 0) {
                    visibleRect.width = 1;
                    visibleRect.height = this.tree.getRowHeight() * this.tree.getVisibleRowCount();
                }
                else {
                    final Rectangle rectangle = visibleRect;
                    rectangle.x -= insets.left;
                    final Rectangle rectangle2 = visibleRect;
                    rectangle2.y -= insets.top;
                }
                final Container unwrappedParent = SwingUtilities.getUnwrappedParent(this.tree);
                if (unwrappedParent instanceof JViewport) {
                    final Container parent = unwrappedParent.getParent();
                    if (parent instanceof JScrollPane) {
                        final JScrollBar horizontalScrollBar = ((JScrollPane)parent).getHorizontalScrollBar();
                        if (horizontalScrollBar != null && horizontalScrollBar.isVisible()) {
                            final int height = horizontalScrollBar.getHeight();
                            final Rectangle rectangle3 = visibleRect;
                            rectangle3.y -= height;
                            final Rectangle rectangle4 = visibleRect;
                            rectangle4.height += height;
                        }
                    }
                }
                this.preferredSize.width = this.treeState.getPreferredWidth(visibleRect);
            }
            else {
                this.preferredSize.width = this.treeState.getPreferredWidth(null);
            }
            this.preferredSize.height = this.treeState.getPreferredHeight();
            final Dimension preferredSize = this.preferredSize;
            preferredSize.width += insets.left + insets.right;
            final Dimension preferredSize2 = this.preferredSize;
            preferredSize2.height += insets.top + insets.bottom;
        }
        this.validCachedPreferredSize = true;
    }
    
    protected void pathWasExpanded(final TreePath treePath) {
        if (this.tree != null) {
            this.tree.fireTreeExpanded(treePath);
        }
    }
    
    protected void pathWasCollapsed(final TreePath treePath) {
        if (this.tree != null) {
            this.tree.fireTreeCollapsed(treePath);
        }
    }
    
    protected void ensureRowsAreVisible(final int n, final int n2) {
        if (this.tree != null && n >= 0 && n2 < this.getRowCount(this.tree)) {
            final boolean boolean1 = DefaultLookup.getBoolean(this.tree, this, "Tree.scrollsHorizontallyAndVertically", false);
            if (n == n2) {
                final Rectangle pathBounds = this.getPathBounds(this.tree, this.getPathForRow(this.tree, n));
                if (pathBounds != null) {
                    if (!boolean1) {
                        pathBounds.x = this.tree.getVisibleRect().x;
                        pathBounds.width = 1;
                    }
                    this.tree.scrollRectToVisible(pathBounds);
                }
            }
            else {
                final Rectangle pathBounds2 = this.getPathBounds(this.tree, this.getPathForRow(this.tree, n));
                if (pathBounds2 != null) {
                    final Rectangle visibleRect = this.tree.getVisibleRect();
                    Rectangle pathBounds3 = pathBounds2;
                    final int y = pathBounds2.y;
                    final int n3 = y + visibleRect.height;
                    for (int i = n + 1; i <= n2; ++i) {
                        pathBounds3 = this.getPathBounds(this.tree, this.getPathForRow(this.tree, i));
                        if (pathBounds3 == null) {
                            return;
                        }
                        if (pathBounds3.y + pathBounds3.height > n3) {
                            i = n2;
                        }
                    }
                    this.tree.scrollRectToVisible(new Rectangle(visibleRect.x, y, 1, pathBounds3.y + pathBounds3.height - y));
                }
            }
        }
    }
    
    public void setPreferredMinSize(final Dimension preferredMinSize) {
        this.preferredMinSize = preferredMinSize;
    }
    
    public Dimension getPreferredMinSize() {
        if (this.preferredMinSize == null) {
            return null;
        }
        return new Dimension(this.preferredMinSize);
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        return this.getPreferredSize(component, true);
    }
    
    public Dimension getPreferredSize(final JComponent component, final boolean b) {
        final Dimension preferredMinSize = this.getPreferredMinSize();
        if (!this.validCachedPreferredSize) {
            this.updateCachedPreferredSize();
        }
        if (this.tree != null) {
            if (preferredMinSize != null) {
                return new Dimension(Math.max(preferredMinSize.width, this.preferredSize.width), Math.max(preferredMinSize.height, this.preferredSize.height));
            }
            return new Dimension(this.preferredSize.width, this.preferredSize.height);
        }
        else {
            if (preferredMinSize != null) {
                return preferredMinSize;
            }
            return new Dimension(0, 0);
        }
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        if (this.getPreferredMinSize() != null) {
            return this.getPreferredMinSize();
        }
        return new Dimension(0, 0);
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        if (this.tree != null) {
            return this.getPreferredSize(this.tree);
        }
        if (this.getPreferredMinSize() != null) {
            return this.getPreferredMinSize();
        }
        return new Dimension(0, 0);
    }
    
    protected void completeEditing() {
        if (this.tree.getInvokesStopCellEditing() && this.stopEditingInCompleteEditing && this.editingComponent != null) {
            this.cellEditor.stopCellEditing();
        }
        this.completeEditing(false, true, false);
    }
    
    protected void completeEditing(final boolean b, final boolean b2, final boolean b3) {
        if (this.stopEditingInCompleteEditing && this.editingComponent != null) {
            final Component editingComponent = this.editingComponent;
            final TreePath editingPath = this.editingPath;
            final TreeCellEditor cellEditor = this.cellEditor;
            final Object cellEditorValue = cellEditor.getCellEditorValue();
            final Rectangle pathBounds = this.getPathBounds(this.tree, this.editingPath);
            final boolean b4 = this.tree != null && (this.tree.hasFocus() || SwingUtilities.findFocusOwner(this.editingComponent) != null);
            this.editingComponent = null;
            this.editingPath = null;
            if (b) {
                cellEditor.stopCellEditing();
            }
            else if (b2) {
                cellEditor.cancelCellEditing();
            }
            this.tree.remove(editingComponent);
            if (this.editorHasDifferentSize) {
                this.treeState.invalidatePathBounds(editingPath);
                this.updateSize();
            }
            else if (pathBounds != null) {
                pathBounds.x = 0;
                pathBounds.width = this.tree.getSize().width;
                this.tree.repaint(pathBounds);
            }
            if (b4) {
                this.tree.requestFocus();
            }
            if (b3) {
                this.treeModel.valueForPathChanged(editingPath, cellEditorValue);
            }
        }
    }
    
    private boolean startEditingOnRelease(final TreePath treePath, final MouseEvent mouseEvent, final MouseEvent releaseEvent) {
        this.releaseEvent = releaseEvent;
        try {
            return this.startEditing(treePath, mouseEvent);
        }
        finally {
            this.releaseEvent = null;
        }
    }
    
    protected boolean startEditing(final TreePath editingPath, final MouseEvent mouseEvent) {
        if (this.isEditing(this.tree) && this.tree.getInvokesStopCellEditing() && !this.stopEditing(this.tree)) {
            return false;
        }
        this.completeEditing();
        if (this.cellEditor != null && this.tree.isPathEditable(editingPath)) {
            final int rowForPath = this.getRowForPath(this.tree, editingPath);
            if (this.cellEditor.isCellEditable(mouseEvent)) {
                this.editingComponent = this.cellEditor.getTreeCellEditorComponent(this.tree, editingPath.getLastPathComponent(), this.tree.isPathSelected(editingPath), this.tree.isExpanded(editingPath), this.treeModel.isLeaf(editingPath.getLastPathComponent()), rowForPath);
                Rectangle rectangle = this.getPathBounds(this.tree, editingPath);
                if (rectangle == null) {
                    return false;
                }
                this.editingRow = rowForPath;
                final Dimension preferredSize = this.editingComponent.getPreferredSize();
                if (preferredSize.height != rectangle.height && this.getRowHeight() > 0) {
                    preferredSize.height = this.getRowHeight();
                }
                if (preferredSize.width != rectangle.width || preferredSize.height != rectangle.height) {
                    this.editorHasDifferentSize = true;
                    this.treeState.invalidatePathBounds(editingPath);
                    this.updateSize();
                    rectangle = this.getPathBounds(this.tree, editingPath);
                    if (rectangle == null) {
                        return false;
                    }
                }
                else {
                    this.editorHasDifferentSize = false;
                }
                this.tree.add(this.editingComponent);
                this.editingComponent.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                this.editingPath = editingPath;
                AWTAccessor.getComponentAccessor().revalidateSynchronously(this.editingComponent);
                this.editingComponent.repaint();
                if (this.cellEditor.shouldSelectCell(mouseEvent)) {
                    this.stopEditingInCompleteEditing = false;
                    this.tree.setSelectionRow(rowForPath);
                    this.stopEditingInCompleteEditing = true;
                }
                final Component compositeRequestFocus = SwingUtilities2.compositeRequestFocus(this.editingComponent);
                boolean b = true;
                if (mouseEvent != null) {
                    final Point convertPoint = SwingUtilities.convertPoint(this.tree, new Point(mouseEvent.getX(), mouseEvent.getY()), this.editingComponent);
                    final Component deepestComponent = SwingUtilities.getDeepestComponentAt(this.editingComponent, convertPoint.x, convertPoint.y);
                    if (deepestComponent != null) {
                        final MouseInputHandler mouseInputHandler = new MouseInputHandler(this.tree, deepestComponent, mouseEvent, compositeRequestFocus);
                        if (this.releaseEvent != null) {
                            mouseInputHandler.mouseReleased(this.releaseEvent);
                        }
                        b = false;
                    }
                }
                if (b && compositeRequestFocus instanceof JTextField) {
                    ((JTextField)compositeRequestFocus).selectAll();
                }
                return true;
            }
            else {
                this.editingComponent = null;
            }
        }
        return false;
    }
    
    protected void checkForClickInExpandControl(final TreePath treePath, final int n, final int n2) {
        if (this.isLocationInExpandControl(treePath, n, n2)) {
            this.handleExpandControlClick(treePath, n, n2);
        }
    }
    
    protected boolean isLocationInExpandControl(final TreePath treePath, final int n, final int n2) {
        if (treePath != null && !this.treeModel.isLeaf(treePath.getLastPathComponent())) {
            final Insets insets = this.tree.getInsets();
            int iconWidth;
            if (this.getExpandedIcon() != null) {
                iconWidth = this.getExpandedIcon().getIconWidth();
            }
            else {
                iconWidth = 8;
            }
            final int rowX = this.getRowX(this.tree.getRowForPath(treePath), treePath.getPathCount() - 1);
            int n3;
            if (this.leftToRight) {
                n3 = rowX + insets.left - this.getRightChildIndent() + 1;
            }
            else {
                n3 = this.tree.getWidth() - rowX - insets.right + this.getRightChildIndent() - 1;
            }
            final int centeredX = this.findCenteredX(n3, iconWidth);
            return n >= centeredX && n < centeredX + iconWidth;
        }
        return false;
    }
    
    protected void handleExpandControlClick(final TreePath treePath, final int n, final int n2) {
        this.toggleExpandState(treePath);
    }
    
    protected void toggleExpandState(final TreePath treePath) {
        if (!this.tree.isExpanded(treePath)) {
            final int rowForPath = this.getRowForPath(this.tree, treePath);
            this.tree.expandPath(treePath);
            this.updateSize();
            if (rowForPath != -1) {
                if (this.tree.getScrollsOnExpand()) {
                    this.ensureRowsAreVisible(rowForPath, rowForPath + this.treeState.getVisibleChildCount(treePath));
                }
                else {
                    this.ensureRowsAreVisible(rowForPath, rowForPath);
                }
            }
        }
        else {
            this.tree.collapsePath(treePath);
            this.updateSize();
        }
    }
    
    protected boolean isToggleSelectionEvent(final MouseEvent mouseEvent) {
        return SwingUtilities.isLeftMouseButton(mouseEvent) && BasicGraphicsUtils.isMenuShortcutKeyDown(mouseEvent);
    }
    
    protected boolean isMultiSelectEvent(final MouseEvent mouseEvent) {
        return SwingUtilities.isLeftMouseButton(mouseEvent) && mouseEvent.isShiftDown();
    }
    
    protected boolean isToggleEvent(final MouseEvent mouseEvent) {
        if (!SwingUtilities.isLeftMouseButton(mouseEvent)) {
            return false;
        }
        final int toggleClickCount = this.tree.getToggleClickCount();
        return toggleClickCount > 0 && mouseEvent.getClickCount() % toggleClickCount == 0;
    }
    
    protected void selectPathForEvent(final TreePath selectionPath, final MouseEvent mouseEvent) {
        if (this.isMultiSelectEvent(mouseEvent)) {
            final TreePath anchorSelectionPath = this.getAnchorSelectionPath();
            final int n = (anchorSelectionPath == null) ? -1 : this.getRowForPath(this.tree, anchorSelectionPath);
            if (n == -1 || this.tree.getSelectionModel().getSelectionMode() == 1) {
                this.tree.setSelectionPath(selectionPath);
            }
            else {
                final int rowForPath = this.getRowForPath(this.tree, selectionPath);
                final TreePath anchorSelectionPath2 = anchorSelectionPath;
                if (this.isToggleSelectionEvent(mouseEvent)) {
                    if (this.tree.isRowSelected(n)) {
                        this.tree.addSelectionInterval(n, rowForPath);
                    }
                    else {
                        this.tree.removeSelectionInterval(n, rowForPath);
                        this.tree.addSelectionInterval(rowForPath, rowForPath);
                    }
                }
                else if (rowForPath < n) {
                    this.tree.setSelectionInterval(rowForPath, n);
                }
                else {
                    this.tree.setSelectionInterval(n, rowForPath);
                }
                this.lastSelectedRow = rowForPath;
                this.setAnchorSelectionPath(anchorSelectionPath2);
                this.setLeadSelectionPath(selectionPath);
            }
        }
        else if (this.isToggleSelectionEvent(mouseEvent)) {
            if (this.tree.isPathSelected(selectionPath)) {
                this.tree.removeSelectionPath(selectionPath);
            }
            else {
                this.tree.addSelectionPath(selectionPath);
            }
            this.lastSelectedRow = this.getRowForPath(this.tree, selectionPath);
            this.setAnchorSelectionPath(selectionPath);
            this.setLeadSelectionPath(selectionPath);
        }
        else if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
            this.tree.setSelectionPath(selectionPath);
            if (this.isToggleEvent(mouseEvent)) {
                this.toggleExpandState(selectionPath);
            }
        }
    }
    
    protected boolean isLeaf(final int n) {
        final TreePath pathForRow = this.getPathForRow(this.tree, n);
        return pathForRow == null || this.treeModel.isLeaf(pathForRow.getLastPathComponent());
    }
    
    private void setAnchorSelectionPath(final TreePath anchorSelectionPath) {
        this.ignoreLAChange = true;
        try {
            this.tree.setAnchorSelectionPath(anchorSelectionPath);
        }
        finally {
            this.ignoreLAChange = false;
        }
    }
    
    private TreePath getAnchorSelectionPath() {
        return this.tree.getAnchorSelectionPath();
    }
    
    private void setLeadSelectionPath(final TreePath treePath) {
        this.setLeadSelectionPath(treePath, false);
    }
    
    private void setLeadSelectionPath(final TreePath leadSelectionPath, final boolean b) {
        final Rectangle rectangle = b ? this.getPathBounds(this.tree, this.getLeadSelectionPath()) : null;
        this.ignoreLAChange = true;
        try {
            this.tree.setLeadSelectionPath(leadSelectionPath);
        }
        finally {
            this.ignoreLAChange = false;
        }
        this.leadRow = this.getRowForPath(this.tree, leadSelectionPath);
        if (b) {
            if (rectangle != null) {
                this.tree.repaint(this.getRepaintPathBounds(rectangle));
            }
            final Rectangle pathBounds = this.getPathBounds(this.tree, leadSelectionPath);
            if (pathBounds != null) {
                this.tree.repaint(this.getRepaintPathBounds(pathBounds));
            }
        }
    }
    
    private Rectangle getRepaintPathBounds(final Rectangle rectangle) {
        if (UIManager.getBoolean("Tree.repaintWholeRow")) {
            rectangle.x = 0;
            rectangle.width = this.tree.getWidth();
        }
        return rectangle;
    }
    
    private TreePath getLeadSelectionPath() {
        return this.tree.getLeadSelectionPath();
    }
    
    protected void updateLeadSelectionRow() {
        this.leadRow = this.getRowForPath(this.tree, this.getLeadSelectionPath());
    }
    
    protected int getLeadSelectionRow() {
        return this.leadRow;
    }
    
    private void extendSelection(final TreePath leadSelectionPath) {
        final TreePath anchorSelectionPath = this.getAnchorSelectionPath();
        final int n = (anchorSelectionPath == null) ? -1 : this.getRowForPath(this.tree, anchorSelectionPath);
        final int rowForPath = this.getRowForPath(this.tree, leadSelectionPath);
        if (n == -1) {
            this.tree.setSelectionRow(rowForPath);
        }
        else {
            if (n < rowForPath) {
                this.tree.setSelectionInterval(n, rowForPath);
            }
            else {
                this.tree.setSelectionInterval(rowForPath, n);
            }
            this.setAnchorSelectionPath(anchorSelectionPath);
            this.setLeadSelectionPath(leadSelectionPath);
        }
    }
    
    private void repaintPath(final TreePath treePath) {
        if (treePath != null) {
            final Rectangle pathBounds = this.getPathBounds(this.tree, treePath);
            if (pathBounds != null) {
                this.tree.repaint(pathBounds.x, pathBounds.y, pathBounds.width, pathBounds.height);
            }
        }
    }
    
    static {
        BASELINE_COMPONENT_KEY = new StringBuilder("Tree.baselineComponent");
        SHARED_ACTION = new Actions();
        defaultTransferHandler = new TreeTransferHandler();
    }
    
    public class TreeExpansionHandler implements TreeExpansionListener
    {
        @Override
        public void treeExpanded(final TreeExpansionEvent treeExpansionEvent) {
            BasicTreeUI.this.getHandler().treeExpanded(treeExpansionEvent);
        }
        
        @Override
        public void treeCollapsed(final TreeExpansionEvent treeExpansionEvent) {
            BasicTreeUI.this.getHandler().treeCollapsed(treeExpansionEvent);
        }
    }
    
    public class ComponentHandler extends ComponentAdapter implements ActionListener
    {
        protected Timer timer;
        protected JScrollBar scrollBar;
        
        @Override
        public void componentMoved(final ComponentEvent componentEvent) {
            if (this.timer == null) {
                final JScrollPane scrollPane = this.getScrollPane();
                if (scrollPane == null) {
                    BasicTreeUI.this.updateSize();
                }
                else {
                    this.scrollBar = scrollPane.getVerticalScrollBar();
                    if (this.scrollBar == null || !this.scrollBar.getValueIsAdjusting()) {
                        if ((this.scrollBar = scrollPane.getHorizontalScrollBar()) != null && this.scrollBar.getValueIsAdjusting()) {
                            this.startTimer();
                        }
                        else {
                            BasicTreeUI.this.updateSize();
                        }
                    }
                    else {
                        this.startTimer();
                    }
                }
            }
        }
        
        protected void startTimer() {
            if (this.timer == null) {
                (this.timer = new Timer(200, this)).setRepeats(true);
            }
            this.timer.start();
        }
        
        protected JScrollPane getScrollPane() {
            Container container;
            for (container = BasicTreeUI.this.tree.getParent(); container != null && !(container instanceof JScrollPane); container = container.getParent()) {}
            if (container instanceof JScrollPane) {
                return (JScrollPane)container;
            }
            return null;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (this.scrollBar == null || !this.scrollBar.getValueIsAdjusting()) {
                if (this.timer != null) {
                    this.timer.stop();
                }
                BasicTreeUI.this.updateSize();
                this.timer = null;
                this.scrollBar = null;
            }
        }
    }
    
    public class TreeModelHandler implements TreeModelListener
    {
        @Override
        public void treeNodesChanged(final TreeModelEvent treeModelEvent) {
            BasicTreeUI.this.getHandler().treeNodesChanged(treeModelEvent);
        }
        
        @Override
        public void treeNodesInserted(final TreeModelEvent treeModelEvent) {
            BasicTreeUI.this.getHandler().treeNodesInserted(treeModelEvent);
        }
        
        @Override
        public void treeNodesRemoved(final TreeModelEvent treeModelEvent) {
            BasicTreeUI.this.getHandler().treeNodesRemoved(treeModelEvent);
        }
        
        @Override
        public void treeStructureChanged(final TreeModelEvent treeModelEvent) {
            BasicTreeUI.this.getHandler().treeStructureChanged(treeModelEvent);
        }
    }
    
    public class TreeSelectionHandler implements TreeSelectionListener
    {
        @Override
        public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {
            BasicTreeUI.this.getHandler().valueChanged(treeSelectionEvent);
        }
    }
    
    public class CellEditorHandler implements CellEditorListener
    {
        @Override
        public void editingStopped(final ChangeEvent changeEvent) {
            BasicTreeUI.this.getHandler().editingStopped(changeEvent);
        }
        
        @Override
        public void editingCanceled(final ChangeEvent changeEvent) {
            BasicTreeUI.this.getHandler().editingCanceled(changeEvent);
        }
    }
    
    public class KeyHandler extends KeyAdapter
    {
        protected Action repeatKeyAction;
        protected boolean isKeyDown;
        
        @Override
        public void keyTyped(final KeyEvent keyEvent) {
            BasicTreeUI.this.getHandler().keyTyped(keyEvent);
        }
        
        @Override
        public void keyPressed(final KeyEvent keyEvent) {
            BasicTreeUI.this.getHandler().keyPressed(keyEvent);
        }
        
        @Override
        public void keyReleased(final KeyEvent keyEvent) {
            BasicTreeUI.this.getHandler().keyReleased(keyEvent);
        }
    }
    
    public class FocusHandler implements FocusListener
    {
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            BasicTreeUI.this.getHandler().focusGained(focusEvent);
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            BasicTreeUI.this.getHandler().focusLost(focusEvent);
        }
    }
    
    public class NodeDimensionsHandler extends AbstractLayoutCache.NodeDimensions
    {
        @Override
        public Rectangle getNodeDimensions(final Object o, final int n, final int n2, final boolean b, Rectangle rectangle) {
            if (BasicTreeUI.this.editingComponent != null && BasicTreeUI.this.editingRow == n) {
                final Dimension preferredSize = BasicTreeUI.this.editingComponent.getPreferredSize();
                final int rowHeight = BasicTreeUI.this.getRowHeight();
                if (rowHeight > 0 && rowHeight != preferredSize.height) {
                    preferredSize.height = rowHeight;
                }
                if (rectangle != null) {
                    rectangle.x = this.getRowX(n, n2);
                    rectangle.width = preferredSize.width;
                    rectangle.height = preferredSize.height;
                }
                else {
                    rectangle = new Rectangle(this.getRowX(n, n2), 0, preferredSize.width, preferredSize.height);
                }
                return rectangle;
            }
            if (BasicTreeUI.this.currentCellRenderer != null) {
                final Component treeCellRendererComponent = BasicTreeUI.this.currentCellRenderer.getTreeCellRendererComponent(BasicTreeUI.this.tree, o, BasicTreeUI.this.tree.isRowSelected(n), b, BasicTreeUI.this.treeModel.isLeaf(o), n, false);
                if (BasicTreeUI.this.tree != null) {
                    BasicTreeUI.this.rendererPane.add(treeCellRendererComponent);
                    treeCellRendererComponent.validate();
                }
                final Dimension preferredSize2 = treeCellRendererComponent.getPreferredSize();
                if (rectangle != null) {
                    rectangle.x = this.getRowX(n, n2);
                    rectangle.width = preferredSize2.width;
                    rectangle.height = preferredSize2.height;
                }
                else {
                    rectangle = new Rectangle(this.getRowX(n, n2), 0, preferredSize2.width, preferredSize2.height);
                }
                return rectangle;
            }
            return null;
        }
        
        protected int getRowX(final int n, final int n2) {
            return BasicTreeUI.this.getRowX(n, n2);
        }
    }
    
    public class MouseHandler extends MouseAdapter implements MouseMotionListener
    {
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            BasicTreeUI.this.getHandler().mousePressed(mouseEvent);
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            BasicTreeUI.this.getHandler().mouseDragged(mouseEvent);
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            BasicTreeUI.this.getHandler().mouseMoved(mouseEvent);
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            BasicTreeUI.this.getHandler().mouseReleased(mouseEvent);
        }
    }
    
    public class PropertyChangeHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            BasicTreeUI.this.getHandler().propertyChange(propertyChangeEvent);
        }
    }
    
    public class SelectionModelPropertyChangeHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            BasicTreeUI.this.getHandler().propertyChange(propertyChangeEvent);
        }
    }
    
    public class TreeTraverseAction extends AbstractAction
    {
        protected int direction;
        private boolean changeSelection;
        
        public TreeTraverseAction(final BasicTreeUI basicTreeUI, final int n, final String s) {
            this(basicTreeUI, n, s, true);
        }
        
        private TreeTraverseAction(final int direction, final String s, final boolean changeSelection) {
            this.direction = direction;
            this.changeSelection = changeSelection;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicTreeUI.this.tree != null) {
                BasicTreeUI.SHARED_ACTION.traverse(BasicTreeUI.this.tree, BasicTreeUI.this, this.direction, this.changeSelection);
            }
        }
        
        @Override
        public boolean isEnabled() {
            return BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled();
        }
    }
    
    public class TreePageAction extends AbstractAction
    {
        protected int direction;
        private boolean addToSelection;
        private boolean changeSelection;
        
        public TreePageAction(final BasicTreeUI basicTreeUI, final int n, final String s) {
            this(basicTreeUI, n, s, false, true);
        }
        
        private TreePageAction(final int direction, final String s, final boolean addToSelection, final boolean changeSelection) {
            this.direction = direction;
            this.addToSelection = addToSelection;
            this.changeSelection = changeSelection;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicTreeUI.this.tree != null) {
                BasicTreeUI.SHARED_ACTION.page(BasicTreeUI.this.tree, BasicTreeUI.this, this.direction, this.addToSelection, this.changeSelection);
            }
        }
        
        @Override
        public boolean isEnabled() {
            return BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled();
        }
    }
    
    public class TreeIncrementAction extends AbstractAction
    {
        protected int direction;
        private boolean addToSelection;
        private boolean changeSelection;
        
        public TreeIncrementAction(final BasicTreeUI basicTreeUI, final int n, final String s) {
            this(basicTreeUI, n, s, false, true);
        }
        
        private TreeIncrementAction(final int direction, final String s, final boolean addToSelection, final boolean changeSelection) {
            this.direction = direction;
            this.addToSelection = addToSelection;
            this.changeSelection = changeSelection;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicTreeUI.this.tree != null) {
                BasicTreeUI.SHARED_ACTION.increment(BasicTreeUI.this.tree, BasicTreeUI.this, this.direction, this.addToSelection, this.changeSelection);
            }
        }
        
        @Override
        public boolean isEnabled() {
            return BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled();
        }
    }
    
    public class TreeHomeAction extends AbstractAction
    {
        protected int direction;
        private boolean addToSelection;
        private boolean changeSelection;
        
        public TreeHomeAction(final BasicTreeUI basicTreeUI, final int n, final String s) {
            this(basicTreeUI, n, s, false, true);
        }
        
        private TreeHomeAction(final int direction, final String s, final boolean addToSelection, final boolean changeSelection) {
            this.direction = direction;
            this.changeSelection = changeSelection;
            this.addToSelection = addToSelection;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicTreeUI.this.tree != null) {
                BasicTreeUI.SHARED_ACTION.home(BasicTreeUI.this.tree, BasicTreeUI.this, this.direction, this.addToSelection, this.changeSelection);
            }
        }
        
        @Override
        public boolean isEnabled() {
            return BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled();
        }
    }
    
    public class TreeToggleAction extends AbstractAction
    {
        public TreeToggleAction(final String s) {
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicTreeUI.this.tree != null) {
                BasicTreeUI.SHARED_ACTION.toggle(BasicTreeUI.this.tree, BasicTreeUI.this);
            }
        }
        
        @Override
        public boolean isEnabled() {
            return BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled();
        }
    }
    
    public class TreeCancelEditingAction extends AbstractAction
    {
        public TreeCancelEditingAction(final String s) {
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicTreeUI.this.tree != null) {
                BasicTreeUI.SHARED_ACTION.cancelEditing(BasicTreeUI.this.tree, BasicTreeUI.this);
            }
        }
        
        @Override
        public boolean isEnabled() {
            return BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled() && BasicTreeUI.this.isEditing(BasicTreeUI.this.tree);
        }
    }
    
    public class MouseInputHandler implements MouseInputListener
    {
        protected Component source;
        protected Component destination;
        private Component focusComponent;
        private boolean dispatchedEvent;
        
        public MouseInputHandler(final BasicTreeUI basicTreeUI, final Component component, final Component component2, final MouseEvent mouseEvent) {
            this(basicTreeUI, component, component2, mouseEvent, null);
        }
        
        MouseInputHandler(final Component source, final Component destination, final MouseEvent mouseEvent, final Component focusComponent) {
            this.source = source;
            this.destination = destination;
            this.source.addMouseListener(this);
            this.source.addMouseMotionListener(this);
            SwingUtilities2.setSkipClickCount(destination, mouseEvent.getClickCount() - 1);
            destination.dispatchEvent(SwingUtilities.convertMouseEvent(source, mouseEvent, destination));
            this.focusComponent = focusComponent;
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            if (this.destination != null) {
                this.dispatchedEvent = true;
                this.destination.dispatchEvent(SwingUtilities.convertMouseEvent(this.source, mouseEvent, this.destination));
            }
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (this.destination != null) {
                this.destination.dispatchEvent(SwingUtilities.convertMouseEvent(this.source, mouseEvent, this.destination));
            }
            this.removeFromSource();
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            if (!SwingUtilities.isLeftMouseButton(mouseEvent)) {
                this.removeFromSource();
            }
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            if (!SwingUtilities.isLeftMouseButton(mouseEvent)) {
                this.removeFromSource();
            }
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            if (this.destination != null) {
                this.dispatchedEvent = true;
                this.destination.dispatchEvent(SwingUtilities.convertMouseEvent(this.source, mouseEvent, this.destination));
            }
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            this.removeFromSource();
        }
        
        protected void removeFromSource() {
            if (this.source != null) {
                this.source.removeMouseListener(this);
                this.source.removeMouseMotionListener(this);
                if (this.focusComponent != null && this.focusComponent == this.destination && !this.dispatchedEvent && this.focusComponent instanceof JTextField) {
                    ((JTextField)this.focusComponent).selectAll();
                }
            }
            final Component component = null;
            this.destination = component;
            this.source = component;
        }
    }
    
    static class TreeTransferHandler extends TransferHandler implements UIResource, Comparator<TreePath>
    {
        private JTree tree;
        
        @Override
        protected Transferable createTransferable(final JComponent component) {
            if (!(component instanceof JTree)) {
                return null;
            }
            this.tree = (JTree)component;
            final TreePath[] selectionPaths = this.tree.getSelectionPaths();
            if (selectionPaths == null || selectionPaths.length == 0) {
                return null;
            }
            final StringBuffer sb = new StringBuffer();
            final StringBuffer sb2 = new StringBuffer();
            sb2.append("<html>\n<body>\n<ul>\n");
            final TreeModel model = this.tree.getModel();
            for (final TreePath treePath : this.getDisplayOrderPaths(selectionPaths)) {
                final String displayString = this.getDisplayString(treePath, true, model.isLeaf(treePath.getLastPathComponent()));
                sb.append(displayString + "\n");
                sb2.append("  <li>" + displayString + "\n");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb2.append("</ul>\n</body>\n</html>");
            this.tree = null;
            return new BasicTransferable(sb.toString(), sb2.toString());
        }
        
        @Override
        public int compare(final TreePath treePath, final TreePath treePath2) {
            return this.tree.getRowForPath(treePath) - this.tree.getRowForPath(treePath2);
        }
        
        String getDisplayString(final TreePath treePath, final boolean b, final boolean b2) {
            final int rowForPath = this.tree.getRowForPath(treePath);
            return this.tree.convertValueToText(treePath.getLastPathComponent(), b, this.tree.isExpanded(rowForPath), b2, rowForPath, this.tree.getLeadSelectionRow() == rowForPath);
        }
        
        TreePath[] getDisplayOrderPaths(final TreePath[] array) {
            final ArrayList list = new ArrayList();
            for (int length = array.length, i = 0; i < length; ++i) {
                list.add(array[i]);
            }
            Collections.sort((List<Object>)list, (Comparator<? super Object>)this);
            final int size = list.size();
            final TreePath[] array2 = new TreePath[size];
            for (int j = 0; j < size; ++j) {
                array2[j] = (TreePath)list.get(j);
            }
            return array2;
        }
        
        @Override
        public int getSourceActions(final JComponent component) {
            return 1;
        }
    }
    
    private class Handler implements CellEditorListener, FocusListener, KeyListener, MouseListener, MouseMotionListener, PropertyChangeListener, TreeExpansionListener, TreeModelListener, TreeSelectionListener, DragRecognitionSupport.BeforeDrag
    {
        private String prefix;
        private String typedString;
        private long lastTime;
        private boolean dragPressDidSelection;
        private boolean dragStarted;
        private TreePath pressedPath;
        private MouseEvent pressedEvent;
        private boolean valueChangedOnPress;
        
        private Handler() {
            this.prefix = "";
            this.typedString = "";
            this.lastTime = 0L;
        }
        
        @Override
        public void keyTyped(final KeyEvent keyEvent) {
            if (BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.getRowCount() > 0 && BasicTreeUI.this.tree.hasFocus() && BasicTreeUI.this.tree.isEnabled()) {
                if (keyEvent.isAltDown() || BasicGraphicsUtils.isMenuShortcutKeyDown(keyEvent) || this.isNavigationKey(keyEvent)) {
                    return;
                }
                boolean b = true;
                final char keyChar = keyEvent.getKeyChar();
                final long when = keyEvent.getWhen();
                int leadSelectionRow = BasicTreeUI.this.tree.getLeadSelectionRow();
                if (when - this.lastTime < BasicTreeUI.this.timeFactor) {
                    this.typedString += keyChar;
                    if (this.prefix.length() == 1 && keyChar == this.prefix.charAt(0)) {
                        ++leadSelectionRow;
                    }
                    else {
                        this.prefix = this.typedString;
                    }
                }
                else {
                    ++leadSelectionRow;
                    this.typedString = "" + keyChar;
                    this.prefix = this.typedString;
                }
                this.lastTime = when;
                if (leadSelectionRow < 0 || leadSelectionRow >= BasicTreeUI.this.tree.getRowCount()) {
                    b = false;
                    leadSelectionRow = 0;
                }
                final TreePath nextMatch = BasicTreeUI.this.tree.getNextMatch(this.prefix, leadSelectionRow, Position.Bias.Forward);
                if (nextMatch != null) {
                    BasicTreeUI.this.tree.setSelectionPath(nextMatch);
                    final int rowForPath = BasicTreeUI.this.getRowForPath(BasicTreeUI.this.tree, nextMatch);
                    BasicTreeUI.this.ensureRowsAreVisible(rowForPath, rowForPath);
                }
                else if (b) {
                    final TreePath nextMatch2 = BasicTreeUI.this.tree.getNextMatch(this.prefix, 0, Position.Bias.Forward);
                    if (nextMatch2 != null) {
                        BasicTreeUI.this.tree.setSelectionPath(nextMatch2);
                        final int rowForPath2 = BasicTreeUI.this.getRowForPath(BasicTreeUI.this.tree, nextMatch2);
                        BasicTreeUI.this.ensureRowsAreVisible(rowForPath2, rowForPath2);
                    }
                }
            }
        }
        
        @Override
        public void keyPressed(final KeyEvent keyEvent) {
            if (BasicTreeUI.this.tree != null && this.isNavigationKey(keyEvent)) {
                this.prefix = "";
                this.typedString = "";
                this.lastTime = 0L;
            }
        }
        
        @Override
        public void keyReleased(final KeyEvent keyEvent) {
        }
        
        private boolean isNavigationKey(final KeyEvent keyEvent) {
            final InputMap inputMap = BasicTreeUI.this.tree.getInputMap(1);
            final KeyStroke keyStrokeForEvent = KeyStroke.getKeyStrokeForEvent(keyEvent);
            return inputMap != null && inputMap.get(keyStrokeForEvent) != null;
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getSource() == BasicTreeUI.this.treeSelectionModel) {
                BasicTreeUI.this.treeSelectionModel.resetRowSelection();
            }
            else if (propertyChangeEvent.getSource() == BasicTreeUI.this.tree) {
                final String propertyName = propertyChangeEvent.getPropertyName();
                if (propertyName == "leadSelectionPath") {
                    if (!BasicTreeUI.this.ignoreLAChange) {
                        BasicTreeUI.this.updateLeadSelectionRow();
                        BasicTreeUI.this.repaintPath((TreePath)propertyChangeEvent.getOldValue());
                        BasicTreeUI.this.repaintPath((TreePath)propertyChangeEvent.getNewValue());
                    }
                }
                else if (propertyName == "anchorSelectionPath" && !BasicTreeUI.this.ignoreLAChange) {
                    BasicTreeUI.this.repaintPath((TreePath)propertyChangeEvent.getOldValue());
                    BasicTreeUI.this.repaintPath((TreePath)propertyChangeEvent.getNewValue());
                }
                if (propertyName == "cellRenderer") {
                    BasicTreeUI.this.setCellRenderer((TreeCellRenderer)propertyChangeEvent.getNewValue());
                    BasicTreeUI.this.redoTheLayout();
                }
                else if (propertyName == "model") {
                    BasicTreeUI.this.setModel((TreeModel)propertyChangeEvent.getNewValue());
                }
                else if (propertyName == "rootVisible") {
                    BasicTreeUI.this.setRootVisible((boolean)propertyChangeEvent.getNewValue());
                }
                else if (propertyName == "showsRootHandles") {
                    BasicTreeUI.this.setShowsRootHandles((boolean)propertyChangeEvent.getNewValue());
                }
                else if (propertyName == "rowHeight") {
                    BasicTreeUI.this.setRowHeight((int)propertyChangeEvent.getNewValue());
                }
                else if (propertyName == "cellEditor") {
                    BasicTreeUI.this.setCellEditor((TreeCellEditor)propertyChangeEvent.getNewValue());
                }
                else if (propertyName == "editable") {
                    BasicTreeUI.this.setEditable((boolean)propertyChangeEvent.getNewValue());
                }
                else if (propertyName == "largeModel") {
                    BasicTreeUI.this.setLargeModel(BasicTreeUI.this.tree.isLargeModel());
                }
                else if (propertyName == "selectionModel") {
                    BasicTreeUI.this.setSelectionModel(BasicTreeUI.this.tree.getSelectionModel());
                }
                else if (propertyName == "font") {
                    BasicTreeUI.this.completeEditing();
                    if (BasicTreeUI.this.treeState != null) {
                        BasicTreeUI.this.treeState.invalidateSizes();
                    }
                    BasicTreeUI.this.updateSize();
                }
                else if (propertyName == "componentOrientation") {
                    if (BasicTreeUI.this.tree != null) {
                        BasicTreeUI.this.leftToRight = BasicGraphicsUtils.isLeftToRight(BasicTreeUI.this.tree);
                        BasicTreeUI.this.redoTheLayout();
                        BasicTreeUI.this.tree.treeDidChange();
                        SwingUtilities.replaceUIInputMap(BasicTreeUI.this.tree, 0, BasicTreeUI.this.getInputMap(0));
                    }
                }
                else if ("dropLocation" == propertyName) {
                    this.repaintDropLocation((JTree.DropLocation)propertyChangeEvent.getOldValue());
                    this.repaintDropLocation(BasicTreeUI.this.tree.getDropLocation());
                }
            }
        }
        
        private void repaintDropLocation(final JTree.DropLocation dropLocation) {
            if (dropLocation == null) {
                return;
            }
            Rectangle rectangle;
            if (BasicTreeUI.this.isDropLine(dropLocation)) {
                rectangle = BasicTreeUI.this.getDropLineRect(dropLocation);
            }
            else {
                rectangle = BasicTreeUI.this.tree.getPathBounds(dropLocation.getPath());
            }
            if (rectangle != null) {
                BasicTreeUI.this.tree.repaint(rectangle);
            }
        }
        
        private boolean isActualPath(final TreePath treePath, final int n, final int n2) {
            if (treePath == null) {
                return false;
            }
            final Rectangle pathBounds = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, treePath);
            return pathBounds != null && n2 <= pathBounds.y + pathBounds.height && n >= pathBounds.x && n <= pathBounds.x + pathBounds.width;
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (SwingUtilities2.shouldIgnore(mouseEvent, BasicTreeUI.this.tree)) {
                return;
            }
            if (BasicTreeUI.this.isEditing(BasicTreeUI.this.tree) && BasicTreeUI.this.tree.getInvokesStopCellEditing() && !BasicTreeUI.this.stopEditing(BasicTreeUI.this.tree)) {
                return;
            }
            BasicTreeUI.this.completeEditing();
            this.pressedPath = BasicTreeUI.this.getClosestPathForLocation(BasicTreeUI.this.tree, mouseEvent.getX(), mouseEvent.getY());
            if (BasicTreeUI.this.tree.getDragEnabled()) {
                this.mousePressedDND(mouseEvent);
            }
            else {
                SwingUtilities2.adjustFocus(BasicTreeUI.this.tree);
                this.handleSelection(mouseEvent);
            }
        }
        
        private void mousePressedDND(final MouseEvent pressedEvent) {
            this.pressedEvent = pressedEvent;
            boolean b = true;
            this.dragStarted = false;
            this.valueChangedOnPress = false;
            if (this.isActualPath(this.pressedPath, pressedEvent.getX(), pressedEvent.getY()) && DragRecognitionSupport.mousePressed(pressedEvent)) {
                this.dragPressDidSelection = false;
                if (BasicGraphicsUtils.isMenuShortcutKeyDown(pressedEvent)) {
                    return;
                }
                if (!pressedEvent.isShiftDown() && BasicTreeUI.this.tree.isPathSelected(this.pressedPath)) {
                    BasicTreeUI.this.setAnchorSelectionPath(this.pressedPath);
                    BasicTreeUI.this.setLeadSelectionPath(this.pressedPath, true);
                    return;
                }
                this.dragPressDidSelection = true;
                b = false;
            }
            if (b) {
                SwingUtilities2.adjustFocus(BasicTreeUI.this.tree);
            }
            this.handleSelection(pressedEvent);
        }
        
        void handleSelection(final MouseEvent mouseEvent) {
            if (this.pressedPath != null) {
                final Rectangle pathBounds = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, this.pressedPath);
                if (pathBounds == null || mouseEvent.getY() >= pathBounds.y + pathBounds.height) {
                    return;
                }
                if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
                    BasicTreeUI.this.checkForClickInExpandControl(this.pressedPath, mouseEvent.getX(), mouseEvent.getY());
                }
                final int x = mouseEvent.getX();
                if (x >= pathBounds.x && x < pathBounds.x + pathBounds.width && (BasicTreeUI.this.tree.getDragEnabled() || !BasicTreeUI.this.startEditing(this.pressedPath, mouseEvent))) {
                    BasicTreeUI.this.selectPathForEvent(this.pressedPath, mouseEvent);
                }
            }
        }
        
        @Override
        public void dragStarting(final MouseEvent mouseEvent) {
            this.dragStarted = true;
            if (BasicGraphicsUtils.isMenuShortcutKeyDown(mouseEvent)) {
                BasicTreeUI.this.tree.addSelectionPath(this.pressedPath);
                BasicTreeUI.this.setAnchorSelectionPath(this.pressedPath);
                BasicTreeUI.this.setLeadSelectionPath(this.pressedPath, true);
            }
            this.pressedEvent = null;
            this.pressedPath = null;
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            if (SwingUtilities2.shouldIgnore(mouseEvent, BasicTreeUI.this.tree)) {
                return;
            }
            if (BasicTreeUI.this.tree.getDragEnabled()) {
                DragRecognitionSupport.mouseDragged(mouseEvent, this);
            }
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (SwingUtilities2.shouldIgnore(mouseEvent, BasicTreeUI.this.tree)) {
                return;
            }
            if (BasicTreeUI.this.tree.getDragEnabled()) {
                this.mouseReleasedDND(mouseEvent);
            }
            this.pressedEvent = null;
            this.pressedPath = null;
        }
        
        private void mouseReleasedDND(final MouseEvent mouseEvent) {
            final MouseEvent mouseReleased = DragRecognitionSupport.mouseReleased(mouseEvent);
            if (mouseReleased != null) {
                SwingUtilities2.adjustFocus(BasicTreeUI.this.tree);
                if (!this.dragPressDidSelection) {
                    this.handleSelection(mouseReleased);
                }
            }
            if (!this.dragStarted && this.pressedPath != null && !this.valueChangedOnPress && this.isActualPath(this.pressedPath, this.pressedEvent.getX(), this.pressedEvent.getY())) {
                BasicTreeUI.this.startEditingOnRelease(this.pressedPath, this.pressedEvent, mouseEvent);
            }
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            if (BasicTreeUI.this.tree != null) {
                final Rectangle pathBounds = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, BasicTreeUI.this.tree.getLeadSelectionPath());
                if (pathBounds != null) {
                    BasicTreeUI.this.tree.repaint(BasicTreeUI.this.getRepaintPathBounds(pathBounds));
                }
                final Rectangle pathBounds2 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, BasicTreeUI.this.getLeadSelectionPath());
                if (pathBounds2 != null) {
                    BasicTreeUI.this.tree.repaint(BasicTreeUI.this.getRepaintPathBounds(pathBounds2));
                }
            }
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            this.focusGained(focusEvent);
        }
        
        @Override
        public void editingStopped(final ChangeEvent changeEvent) {
            BasicTreeUI.this.completeEditing(false, false, true);
        }
        
        @Override
        public void editingCanceled(final ChangeEvent changeEvent) {
            BasicTreeUI.this.completeEditing(false, false, false);
        }
        
        @Override
        public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {
            this.valueChangedOnPress = true;
            BasicTreeUI.this.completeEditing();
            if (BasicTreeUI.this.tree.getExpandsSelectedPaths() && BasicTreeUI.this.treeSelectionModel != null) {
                final TreePath[] selectionPaths = BasicTreeUI.this.treeSelectionModel.getSelectionPaths();
                if (selectionPaths != null) {
                    for (int i = selectionPaths.length - 1; i >= 0; --i) {
                        TreePath treePath = selectionPaths[i].getParentPath();
                        boolean b = true;
                        while (treePath != null) {
                            if (BasicTreeUI.this.treeModel.isLeaf(treePath.getLastPathComponent())) {
                                b = false;
                                treePath = null;
                            }
                            else {
                                treePath = treePath.getParentPath();
                            }
                        }
                        if (b) {
                            BasicTreeUI.this.tree.makeVisible(selectionPaths[i]);
                        }
                    }
                }
            }
            final TreePath access$1800 = BasicTreeUI.this.getLeadSelectionPath();
            BasicTreeUI.this.lastSelectedRow = BasicTreeUI.this.tree.getMinSelectionRow();
            final TreePath leadSelectionPath = BasicTreeUI.this.tree.getSelectionModel().getLeadSelectionPath();
            BasicTreeUI.this.setAnchorSelectionPath(leadSelectionPath);
            BasicTreeUI.this.setLeadSelectionPath(leadSelectionPath);
            final TreePath[] paths = treeSelectionEvent.getPaths();
            final Rectangle visibleRect = BasicTreeUI.this.tree.getVisibleRect();
            boolean b2 = true;
            final int width = BasicTreeUI.this.tree.getWidth();
            if (paths != null) {
                final int length = paths.length;
                if (length > 4) {
                    BasicTreeUI.this.tree.repaint();
                    b2 = false;
                }
                else {
                    for (int j = 0; j < length; ++j) {
                        final Rectangle pathBounds = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, paths[j]);
                        if (pathBounds != null && visibleRect.intersects(pathBounds)) {
                            BasicTreeUI.this.tree.repaint(0, pathBounds.y, width, pathBounds.height);
                        }
                    }
                }
            }
            if (b2) {
                final Rectangle pathBounds2 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, access$1800);
                if (pathBounds2 != null && visibleRect.intersects(pathBounds2)) {
                    BasicTreeUI.this.tree.repaint(0, pathBounds2.y, width, pathBounds2.height);
                }
                final Rectangle pathBounds3 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, leadSelectionPath);
                if (pathBounds3 != null && visibleRect.intersects(pathBounds3)) {
                    BasicTreeUI.this.tree.repaint(0, pathBounds3.y, width, pathBounds3.height);
                }
            }
        }
        
        @Override
        public void treeExpanded(final TreeExpansionEvent treeExpansionEvent) {
            if (treeExpansionEvent != null && BasicTreeUI.this.tree != null) {
                BasicTreeUI.this.updateExpandedDescendants(treeExpansionEvent.getPath());
            }
        }
        
        @Override
        public void treeCollapsed(final TreeExpansionEvent treeExpansionEvent) {
            if (treeExpansionEvent != null && BasicTreeUI.this.tree != null) {
                final TreePath path = treeExpansionEvent.getPath();
                BasicTreeUI.this.completeEditing();
                if (path != null && BasicTreeUI.this.tree.isVisible(path)) {
                    BasicTreeUI.this.treeState.setExpandedState(path, false);
                    BasicTreeUI.this.updateLeadSelectionRow();
                    BasicTreeUI.this.updateSize();
                }
            }
        }
        
        @Override
        public void treeNodesChanged(final TreeModelEvent treeModelEvent) {
            if (BasicTreeUI.this.treeState != null && treeModelEvent != null) {
                final TreePath treePath = SwingUtilities2.getTreePath(treeModelEvent, BasicTreeUI.this.getModel());
                final int[] childIndices = treeModelEvent.getChildIndices();
                if (childIndices == null || childIndices.length == 0) {
                    BasicTreeUI.this.treeState.treeNodesChanged(treeModelEvent);
                    BasicTreeUI.this.updateSize();
                }
                else if (BasicTreeUI.this.treeState.isExpanded(treePath)) {
                    int min = childIndices[0];
                    for (int i = childIndices.length - 1; i > 0; --i) {
                        min = Math.min(childIndices[i], min);
                    }
                    final TreePath pathByAddingChild = treePath.pathByAddingChild(BasicTreeUI.this.treeModel.getChild(treePath.getLastPathComponent(), min));
                    final Rectangle pathBounds = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, pathByAddingChild);
                    BasicTreeUI.this.treeState.treeNodesChanged(treeModelEvent);
                    BasicTreeUI.this.updateSize0();
                    final Rectangle pathBounds2 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, pathByAddingChild);
                    if (pathBounds == null || pathBounds2 == null) {
                        return;
                    }
                    if (childIndices.length == 1 && pathBounds2.height == pathBounds.height) {
                        BasicTreeUI.this.tree.repaint(0, pathBounds.y, BasicTreeUI.this.tree.getWidth(), pathBounds.height);
                    }
                    else {
                        BasicTreeUI.this.tree.repaint(0, pathBounds.y, BasicTreeUI.this.tree.getWidth(), BasicTreeUI.this.tree.getHeight() - pathBounds.y);
                    }
                }
                else {
                    BasicTreeUI.this.treeState.treeNodesChanged(treeModelEvent);
                }
            }
        }
        
        @Override
        public void treeNodesInserted(final TreeModelEvent treeModelEvent) {
            if (BasicTreeUI.this.treeState != null && treeModelEvent != null) {
                BasicTreeUI.this.treeState.treeNodesInserted(treeModelEvent);
                BasicTreeUI.this.updateLeadSelectionRow();
                final TreePath treePath = SwingUtilities2.getTreePath(treeModelEvent, BasicTreeUI.this.getModel());
                if (BasicTreeUI.this.treeState.isExpanded(treePath)) {
                    BasicTreeUI.this.updateSize();
                }
                else {
                    final int[] childIndices = treeModelEvent.getChildIndices();
                    final int childCount = BasicTreeUI.this.treeModel.getChildCount(treePath.getLastPathComponent());
                    if (childIndices != null && childCount - childIndices.length == 0) {
                        BasicTreeUI.this.updateSize();
                    }
                }
            }
        }
        
        @Override
        public void treeNodesRemoved(final TreeModelEvent treeModelEvent) {
            if (BasicTreeUI.this.treeState != null && treeModelEvent != null) {
                BasicTreeUI.this.treeState.treeNodesRemoved(treeModelEvent);
                BasicTreeUI.this.updateLeadSelectionRow();
                final TreePath treePath = SwingUtilities2.getTreePath(treeModelEvent, BasicTreeUI.this.getModel());
                if (BasicTreeUI.this.treeState.isExpanded(treePath) || BasicTreeUI.this.treeModel.getChildCount(treePath.getLastPathComponent()) == 0) {
                    BasicTreeUI.this.updateSize();
                }
            }
        }
        
        @Override
        public void treeStructureChanged(final TreeModelEvent treeModelEvent) {
            if (BasicTreeUI.this.treeState != null && treeModelEvent != null) {
                BasicTreeUI.this.treeState.treeStructureChanged(treeModelEvent);
                BasicTreeUI.this.updateLeadSelectionRow();
                TreePath treePath = SwingUtilities2.getTreePath(treeModelEvent, BasicTreeUI.this.getModel());
                if (treePath != null) {
                    treePath = treePath.getParentPath();
                }
                if (treePath == null || BasicTreeUI.this.treeState.isExpanded(treePath)) {
                    BasicTreeUI.this.updateSize();
                }
            }
        }
    }
    
    private static class Actions extends UIAction
    {
        private static final String SELECT_PREVIOUS = "selectPrevious";
        private static final String SELECT_PREVIOUS_CHANGE_LEAD = "selectPreviousChangeLead";
        private static final String SELECT_PREVIOUS_EXTEND_SELECTION = "selectPreviousExtendSelection";
        private static final String SELECT_NEXT = "selectNext";
        private static final String SELECT_NEXT_CHANGE_LEAD = "selectNextChangeLead";
        private static final String SELECT_NEXT_EXTEND_SELECTION = "selectNextExtendSelection";
        private static final String SELECT_CHILD = "selectChild";
        private static final String SELECT_CHILD_CHANGE_LEAD = "selectChildChangeLead";
        private static final String SELECT_PARENT = "selectParent";
        private static final String SELECT_PARENT_CHANGE_LEAD = "selectParentChangeLead";
        private static final String SCROLL_UP_CHANGE_SELECTION = "scrollUpChangeSelection";
        private static final String SCROLL_UP_CHANGE_LEAD = "scrollUpChangeLead";
        private static final String SCROLL_UP_EXTEND_SELECTION = "scrollUpExtendSelection";
        private static final String SCROLL_DOWN_CHANGE_SELECTION = "scrollDownChangeSelection";
        private static final String SCROLL_DOWN_EXTEND_SELECTION = "scrollDownExtendSelection";
        private static final String SCROLL_DOWN_CHANGE_LEAD = "scrollDownChangeLead";
        private static final String SELECT_FIRST = "selectFirst";
        private static final String SELECT_FIRST_CHANGE_LEAD = "selectFirstChangeLead";
        private static final String SELECT_FIRST_EXTEND_SELECTION = "selectFirstExtendSelection";
        private static final String SELECT_LAST = "selectLast";
        private static final String SELECT_LAST_CHANGE_LEAD = "selectLastChangeLead";
        private static final String SELECT_LAST_EXTEND_SELECTION = "selectLastExtendSelection";
        private static final String TOGGLE = "toggle";
        private static final String CANCEL_EDITING = "cancel";
        private static final String START_EDITING = "startEditing";
        private static final String SELECT_ALL = "selectAll";
        private static final String CLEAR_SELECTION = "clearSelection";
        private static final String SCROLL_LEFT = "scrollLeft";
        private static final String SCROLL_RIGHT = "scrollRight";
        private static final String SCROLL_LEFT_EXTEND_SELECTION = "scrollLeftExtendSelection";
        private static final String SCROLL_RIGHT_EXTEND_SELECTION = "scrollRightExtendSelection";
        private static final String SCROLL_RIGHT_CHANGE_LEAD = "scrollRightChangeLead";
        private static final String SCROLL_LEFT_CHANGE_LEAD = "scrollLeftChangeLead";
        private static final String EXPAND = "expand";
        private static final String COLLAPSE = "collapse";
        private static final String MOVE_SELECTION_TO_PARENT = "moveSelectionToParent";
        private static final String ADD_TO_SELECTION = "addToSelection";
        private static final String TOGGLE_AND_ANCHOR = "toggleAndAnchor";
        private static final String EXTEND_TO = "extendTo";
        private static final String MOVE_SELECTION_TO = "moveSelectionTo";
        
        Actions() {
            super(null);
        }
        
        Actions(final String s) {
            super(s);
        }
        
        @Override
        public boolean isEnabled(final Object o) {
            return !(o instanceof JTree) || this.getName() != "cancel" || ((JTree)o).isEditing();
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTree tree = (JTree)actionEvent.getSource();
            final BasicTreeUI basicTreeUI = (BasicTreeUI)BasicLookAndFeel.getUIOfType(tree.getUI(), BasicTreeUI.class);
            if (basicTreeUI == null) {
                return;
            }
            final String name = this.getName();
            if (name == "selectPrevious") {
                this.increment(tree, basicTreeUI, -1, false, true);
            }
            else if (name == "selectPreviousChangeLead") {
                this.increment(tree, basicTreeUI, -1, false, false);
            }
            else if (name == "selectPreviousExtendSelection") {
                this.increment(tree, basicTreeUI, -1, true, true);
            }
            else if (name == "selectNext") {
                this.increment(tree, basicTreeUI, 1, false, true);
            }
            else if (name == "selectNextChangeLead") {
                this.increment(tree, basicTreeUI, 1, false, false);
            }
            else if (name == "selectNextExtendSelection") {
                this.increment(tree, basicTreeUI, 1, true, true);
            }
            else if (name == "selectChild") {
                this.traverse(tree, basicTreeUI, 1, true);
            }
            else if (name == "selectChildChangeLead") {
                this.traverse(tree, basicTreeUI, 1, false);
            }
            else if (name == "selectParent") {
                this.traverse(tree, basicTreeUI, -1, true);
            }
            else if (name == "selectParentChangeLead") {
                this.traverse(tree, basicTreeUI, -1, false);
            }
            else if (name == "scrollUpChangeSelection") {
                this.page(tree, basicTreeUI, -1, false, true);
            }
            else if (name == "scrollUpChangeLead") {
                this.page(tree, basicTreeUI, -1, false, false);
            }
            else if (name == "scrollUpExtendSelection") {
                this.page(tree, basicTreeUI, -1, true, true);
            }
            else if (name == "scrollDownChangeSelection") {
                this.page(tree, basicTreeUI, 1, false, true);
            }
            else if (name == "scrollDownExtendSelection") {
                this.page(tree, basicTreeUI, 1, true, true);
            }
            else if (name == "scrollDownChangeLead") {
                this.page(tree, basicTreeUI, 1, false, false);
            }
            else if (name == "selectFirst") {
                this.home(tree, basicTreeUI, -1, false, true);
            }
            else if (name == "selectFirstChangeLead") {
                this.home(tree, basicTreeUI, -1, false, false);
            }
            else if (name == "selectFirstExtendSelection") {
                this.home(tree, basicTreeUI, -1, true, true);
            }
            else if (name == "selectLast") {
                this.home(tree, basicTreeUI, 1, false, true);
            }
            else if (name == "selectLastChangeLead") {
                this.home(tree, basicTreeUI, 1, false, false);
            }
            else if (name == "selectLastExtendSelection") {
                this.home(tree, basicTreeUI, 1, true, true);
            }
            else if (name == "toggle") {
                this.toggle(tree, basicTreeUI);
            }
            else if (name == "cancel") {
                this.cancelEditing(tree, basicTreeUI);
            }
            else if (name == "startEditing") {
                this.startEditing(tree, basicTreeUI);
            }
            else if (name == "selectAll") {
                this.selectAll(tree, basicTreeUI, true);
            }
            else if (name == "clearSelection") {
                this.selectAll(tree, basicTreeUI, false);
            }
            else if (name == "addToSelection") {
                if (basicTreeUI.getRowCount(tree) > 0) {
                    final int leadSelectionRow = basicTreeUI.getLeadSelectionRow();
                    if (!tree.isRowSelected(leadSelectionRow)) {
                        final TreePath access$2100 = basicTreeUI.getAnchorSelectionPath();
                        tree.addSelectionRow(leadSelectionRow);
                        basicTreeUI.setAnchorSelectionPath(access$2100);
                    }
                }
            }
            else if (name == "toggleAndAnchor") {
                if (basicTreeUI.getRowCount(tree) > 0) {
                    final int leadSelectionRow2 = basicTreeUI.getLeadSelectionRow();
                    final TreePath access$2101 = basicTreeUI.getLeadSelectionPath();
                    if (!tree.isRowSelected(leadSelectionRow2)) {
                        tree.addSelectionRow(leadSelectionRow2);
                    }
                    else {
                        tree.removeSelectionRow(leadSelectionRow2);
                        basicTreeUI.setLeadSelectionPath(access$2101);
                    }
                    basicTreeUI.setAnchorSelectionPath(access$2101);
                }
            }
            else if (name == "extendTo") {
                this.extendSelection(tree, basicTreeUI);
            }
            else if (name == "moveSelectionTo") {
                if (basicTreeUI.getRowCount(tree) > 0) {
                    final int leadSelectionRow3 = basicTreeUI.getLeadSelectionRow();
                    tree.setSelectionInterval(leadSelectionRow3, leadSelectionRow3);
                }
            }
            else if (name == "scrollLeft") {
                this.scroll(tree, basicTreeUI, 0, -10);
            }
            else if (name == "scrollRight") {
                this.scroll(tree, basicTreeUI, 0, 10);
            }
            else if (name == "scrollLeftExtendSelection") {
                this.scrollChangeSelection(tree, basicTreeUI, -1, true, true);
            }
            else if (name == "scrollRightExtendSelection") {
                this.scrollChangeSelection(tree, basicTreeUI, 1, true, true);
            }
            else if (name == "scrollRightChangeLead") {
                this.scrollChangeSelection(tree, basicTreeUI, 1, false, false);
            }
            else if (name == "scrollLeftChangeLead") {
                this.scrollChangeSelection(tree, basicTreeUI, -1, false, false);
            }
            else if (name == "expand") {
                this.expand(tree, basicTreeUI);
            }
            else if (name == "collapse") {
                this.collapse(tree, basicTreeUI);
            }
            else if (name == "moveSelectionToParent") {
                this.moveSelectionToParent(tree, basicTreeUI);
            }
        }
        
        private void scrollChangeSelection(final JTree tree, final BasicTreeUI basicTreeUI, final int n, final boolean b, final boolean b2) {
            if (basicTreeUI.getRowCount(tree) > 0 && basicTreeUI.treeSelectionModel != null) {
                final Rectangle visibleRect = tree.getVisibleRect();
                TreePath selectionPath;
                if (n == -1) {
                    selectionPath = basicTreeUI.getClosestPathForLocation(tree, visibleRect.x, visibleRect.y);
                    visibleRect.x = Math.max(0, visibleRect.x - visibleRect.width);
                }
                else {
                    visibleRect.x = Math.min(Math.max(0, tree.getWidth() - visibleRect.width), visibleRect.x + visibleRect.width);
                    selectionPath = basicTreeUI.getClosestPathForLocation(tree, visibleRect.x, visibleRect.y + visibleRect.height);
                }
                tree.scrollRectToVisible(visibleRect);
                if (b) {
                    basicTreeUI.extendSelection(selectionPath);
                }
                else if (b2) {
                    tree.setSelectionPath(selectionPath);
                }
                else {
                    basicTreeUI.setLeadSelectionPath(selectionPath, true);
                }
            }
        }
        
        private void scroll(final JTree tree, final BasicTreeUI basicTreeUI, final int n, final int n2) {
            final Rectangle visibleRect = tree.getVisibleRect();
            final Dimension size = tree.getSize();
            if (n == 0) {
                final Rectangle rectangle = visibleRect;
                rectangle.x += n2;
                visibleRect.x = Math.max(0, visibleRect.x);
                visibleRect.x = Math.min(Math.max(0, size.width - visibleRect.width), visibleRect.x);
            }
            else {
                final Rectangle rectangle2 = visibleRect;
                rectangle2.y += n2;
                visibleRect.y = Math.max(0, visibleRect.y);
                visibleRect.y = Math.min(Math.max(0, size.width - visibleRect.height), visibleRect.y);
            }
            tree.scrollRectToVisible(visibleRect);
        }
        
        private void extendSelection(final JTree tree, final BasicTreeUI basicTreeUI) {
            if (basicTreeUI.getRowCount(tree) > 0) {
                final int leadSelectionRow = basicTreeUI.getLeadSelectionRow();
                if (leadSelectionRow != -1) {
                    final TreePath access$1800 = basicTreeUI.getLeadSelectionPath();
                    final TreePath access$1801 = basicTreeUI.getAnchorSelectionPath();
                    int rowForPath = basicTreeUI.getRowForPath(tree, access$1801);
                    if (rowForPath == -1) {
                        rowForPath = 0;
                    }
                    tree.setSelectionInterval(rowForPath, leadSelectionRow);
                    basicTreeUI.setLeadSelectionPath(access$1800);
                    basicTreeUI.setAnchorSelectionPath(access$1801);
                }
            }
        }
        
        private void selectAll(final JTree tree, final BasicTreeUI basicTreeUI, final boolean b) {
            final int rowCount = basicTreeUI.getRowCount(tree);
            if (rowCount > 0) {
                if (b) {
                    if (tree.getSelectionModel().getSelectionMode() == 1) {
                        final int leadSelectionRow = basicTreeUI.getLeadSelectionRow();
                        if (leadSelectionRow != -1) {
                            tree.setSelectionRow(leadSelectionRow);
                        }
                        else if (tree.getMinSelectionRow() == -1) {
                            tree.setSelectionRow(0);
                            basicTreeUI.ensureRowsAreVisible(0, 0);
                        }
                        return;
                    }
                    TreePath access$1800 = basicTreeUI.getLeadSelectionPath();
                    final TreePath access$1801 = basicTreeUI.getAnchorSelectionPath();
                    if (access$1800 != null && !tree.isVisible(access$1800)) {
                        access$1800 = null;
                    }
                    tree.setSelectionInterval(0, rowCount - 1);
                    if (access$1800 != null) {
                        basicTreeUI.setLeadSelectionPath(access$1800);
                    }
                    if (access$1801 != null && tree.isVisible(access$1801)) {
                        basicTreeUI.setAnchorSelectionPath(access$1801);
                    }
                }
                else {
                    final TreePath access$1802 = basicTreeUI.getLeadSelectionPath();
                    final TreePath access$1803 = basicTreeUI.getAnchorSelectionPath();
                    tree.clearSelection();
                    basicTreeUI.setAnchorSelectionPath(access$1803);
                    basicTreeUI.setLeadSelectionPath(access$1802);
                }
            }
        }
        
        private void startEditing(final JTree tree, final BasicTreeUI basicTreeUI) {
            final TreePath access$1800 = basicTreeUI.getLeadSelectionPath();
            if (((access$1800 != null) ? basicTreeUI.getRowForPath(tree, access$1800) : -1) != -1) {
                tree.startEditingAtPath(access$1800);
            }
        }
        
        private void cancelEditing(final JTree tree, final BasicTreeUI basicTreeUI) {
            tree.cancelEditing();
        }
        
        private void toggle(final JTree tree, final BasicTreeUI basicTreeUI) {
            final int leadSelectionRow = basicTreeUI.getLeadSelectionRow();
            if (leadSelectionRow != -1 && !basicTreeUI.isLeaf(leadSelectionRow)) {
                final TreePath access$2100 = basicTreeUI.getAnchorSelectionPath();
                final TreePath access$2101 = basicTreeUI.getLeadSelectionPath();
                basicTreeUI.toggleExpandState(basicTreeUI.getPathForRow(tree, leadSelectionRow));
                basicTreeUI.setAnchorSelectionPath(access$2100);
                basicTreeUI.setLeadSelectionPath(access$2101);
            }
        }
        
        private void expand(final JTree tree, final BasicTreeUI basicTreeUI) {
            tree.expandRow(basicTreeUI.getLeadSelectionRow());
        }
        
        private void collapse(final JTree tree, final BasicTreeUI basicTreeUI) {
            tree.collapseRow(basicTreeUI.getLeadSelectionRow());
        }
        
        private void increment(final JTree tree, final BasicTreeUI basicTreeUI, final int n, final boolean b, boolean b2) {
            if (!b && !b2 && tree.getSelectionModel().getSelectionMode() != 4) {
                b2 = true;
            }
            final int rowCount;
            if (basicTreeUI.treeSelectionModel != null && (rowCount = tree.getRowCount()) > 0) {
                final int leadSelectionRow = basicTreeUI.getLeadSelectionRow();
                int min;
                if (leadSelectionRow == -1) {
                    if (n == 1) {
                        min = 0;
                    }
                    else {
                        min = rowCount - 1;
                    }
                }
                else {
                    min = Math.min(rowCount - 1, Math.max(0, leadSelectionRow + n));
                }
                if (b && basicTreeUI.treeSelectionModel.getSelectionMode() != 1) {
                    basicTreeUI.extendSelection(tree.getPathForRow(min));
                }
                else if (b2) {
                    tree.setSelectionInterval(min, min);
                }
                else {
                    basicTreeUI.setLeadSelectionPath(tree.getPathForRow(min), true);
                }
                basicTreeUI.ensureRowsAreVisible(min, min);
                basicTreeUI.lastSelectedRow = min;
            }
        }
        
        private void traverse(final JTree tree, final BasicTreeUI basicTreeUI, final int n, boolean b) {
            if (!b && tree.getSelectionModel().getSelectionMode() != 4) {
                b = true;
            }
            final int rowCount;
            if ((rowCount = tree.getRowCount()) > 0) {
                final int leadSelectionRow = basicTreeUI.getLeadSelectionRow();
                int n2;
                if (leadSelectionRow == -1) {
                    n2 = 0;
                }
                else if (n == 1) {
                    final TreePath pathForRow = basicTreeUI.getPathForRow(tree, leadSelectionRow);
                    final int childCount = tree.getModel().getChildCount(pathForRow.getLastPathComponent());
                    n2 = -1;
                    if (!basicTreeUI.isLeaf(leadSelectionRow)) {
                        if (!tree.isExpanded(leadSelectionRow)) {
                            basicTreeUI.toggleExpandState(pathForRow);
                        }
                        else if (childCount > 0) {
                            n2 = Math.min(leadSelectionRow + 1, rowCount - 1);
                        }
                    }
                }
                else if (!basicTreeUI.isLeaf(leadSelectionRow) && tree.isExpanded(leadSelectionRow)) {
                    basicTreeUI.toggleExpandState(basicTreeUI.getPathForRow(tree, leadSelectionRow));
                    n2 = -1;
                }
                else {
                    final TreePath pathForRow2 = basicTreeUI.getPathForRow(tree, leadSelectionRow);
                    if (pathForRow2 != null && pathForRow2.getPathCount() > 1) {
                        n2 = basicTreeUI.getRowForPath(tree, pathForRow2.getParentPath());
                    }
                    else {
                        n2 = -1;
                    }
                }
                if (n2 != -1) {
                    if (b) {
                        tree.setSelectionInterval(n2, n2);
                    }
                    else {
                        basicTreeUI.setLeadSelectionPath(basicTreeUI.getPathForRow(tree, n2), true);
                    }
                    basicTreeUI.ensureRowsAreVisible(n2, n2);
                }
            }
        }
        
        private void moveSelectionToParent(final JTree tree, final BasicTreeUI basicTreeUI) {
            final TreePath pathForRow = basicTreeUI.getPathForRow(tree, basicTreeUI.getLeadSelectionRow());
            if (pathForRow != null && pathForRow.getPathCount() > 1) {
                final int rowForPath = basicTreeUI.getRowForPath(tree, pathForRow.getParentPath());
                if (rowForPath != -1) {
                    tree.setSelectionInterval(rowForPath, rowForPath);
                    basicTreeUI.ensureRowsAreVisible(rowForPath, rowForPath);
                }
            }
        }
        
        private void page(final JTree tree, final BasicTreeUI basicTreeUI, final int n, final boolean b, boolean b2) {
            if (!b && !b2 && tree.getSelectionModel().getSelectionMode() != 4) {
                b2 = true;
            }
            final int rowCount;
            if ((rowCount = basicTreeUI.getRowCount(tree)) > 0 && basicTreeUI.treeSelectionModel != null) {
                final Dimension size = tree.getSize();
                final TreePath access$1800 = basicTreeUI.getLeadSelectionPath();
                final Rectangle visibleRect = tree.getVisibleRect();
                TreePath selectionPath;
                if (n == -1) {
                    selectionPath = basicTreeUI.getClosestPathForLocation(tree, visibleRect.x, visibleRect.y);
                    if (selectionPath.equals(access$1800)) {
                        visibleRect.y = Math.max(0, visibleRect.y - visibleRect.height);
                        selectionPath = tree.getClosestPathForLocation(visibleRect.x, visibleRect.y);
                    }
                }
                else {
                    visibleRect.y = Math.min(size.height, visibleRect.y + visibleRect.height - 1);
                    selectionPath = tree.getClosestPathForLocation(visibleRect.x, visibleRect.y);
                    if (selectionPath.equals(access$1800)) {
                        visibleRect.y = Math.min(size.height, visibleRect.y + visibleRect.height - 1);
                        selectionPath = tree.getClosestPathForLocation(visibleRect.x, visibleRect.y);
                    }
                }
                final Rectangle pathBounds = basicTreeUI.getPathBounds(tree, selectionPath);
                if (pathBounds != null) {
                    pathBounds.x = visibleRect.x;
                    pathBounds.width = visibleRect.width;
                    if (n == -1) {
                        pathBounds.height = visibleRect.height;
                    }
                    else {
                        final Rectangle rectangle = pathBounds;
                        rectangle.y -= visibleRect.height - pathBounds.height;
                        pathBounds.height = visibleRect.height;
                    }
                    if (b) {
                        basicTreeUI.extendSelection(selectionPath);
                    }
                    else if (b2) {
                        tree.setSelectionPath(selectionPath);
                    }
                    else {
                        basicTreeUI.setLeadSelectionPath(selectionPath, true);
                    }
                    tree.scrollRectToVisible(pathBounds);
                }
            }
        }
        
        private void home(final JTree tree, final BasicTreeUI basicTreeUI, final int n, final boolean b, boolean b2) {
            if (!b && !b2 && tree.getSelectionModel().getSelectionMode() != 4) {
                b2 = true;
            }
            final int rowCount = basicTreeUI.getRowCount(tree);
            if (rowCount > 0) {
                if (n == -1) {
                    basicTreeUI.ensureRowsAreVisible(0, 0);
                    if (b) {
                        final TreePath access$2100 = basicTreeUI.getAnchorSelectionPath();
                        final int n2 = (access$2100 == null) ? -1 : basicTreeUI.getRowForPath(tree, access$2100);
                        if (n2 == -1) {
                            tree.setSelectionInterval(0, 0);
                        }
                        else {
                            tree.setSelectionInterval(0, n2);
                            basicTreeUI.setAnchorSelectionPath(access$2100);
                            basicTreeUI.setLeadSelectionPath(basicTreeUI.getPathForRow(tree, 0));
                        }
                    }
                    else if (b2) {
                        tree.setSelectionInterval(0, 0);
                    }
                    else {
                        basicTreeUI.setLeadSelectionPath(basicTreeUI.getPathForRow(tree, 0), true);
                    }
                }
                else {
                    basicTreeUI.ensureRowsAreVisible(rowCount - 1, rowCount - 1);
                    if (b) {
                        final TreePath access$2101 = basicTreeUI.getAnchorSelectionPath();
                        final int n3 = (access$2101 == null) ? -1 : basicTreeUI.getRowForPath(tree, access$2101);
                        if (n3 == -1) {
                            tree.setSelectionInterval(rowCount - 1, rowCount - 1);
                        }
                        else {
                            tree.setSelectionInterval(n3, rowCount - 1);
                            basicTreeUI.setAnchorSelectionPath(access$2101);
                            basicTreeUI.setLeadSelectionPath(basicTreeUI.getPathForRow(tree, rowCount - 1));
                        }
                    }
                    else if (b2) {
                        tree.setSelectionInterval(rowCount - 1, rowCount - 1);
                    }
                    else {
                        basicTreeUI.setLeadSelectionPath(basicTreeUI.getPathForRow(tree, rowCount - 1), true);
                    }
                    if (basicTreeUI.isLargeModel()) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                basicTreeUI.ensureRowsAreVisible(rowCount - 1, rowCount - 1);
                            }
                        });
                    }
                }
            }
        }
    }
}
