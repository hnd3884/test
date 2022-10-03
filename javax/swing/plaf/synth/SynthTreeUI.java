package javax.swing.plaf.synth;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.tree.DefaultTreeCellEditor;
import java.beans.PropertyChangeEvent;
import sun.swing.plaf.synth.SynthIcon;
import java.awt.Container;
import java.awt.Color;
import javax.swing.tree.TreeModel;
import java.util.Enumeration;
import java.awt.Insets;
import javax.swing.tree.TreePath;
import java.awt.Rectangle;
import java.awt.Graphics;
import javax.swing.TransferHandler;
import javax.swing.plaf.UIResource;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import java.awt.Component;
import javax.swing.LookAndFeel;
import javax.swing.JTree;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.Icon;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicTreeUI;

public class SynthTreeUI extends BasicTreeUI implements PropertyChangeListener, SynthUI
{
    private SynthStyle style;
    private SynthStyle cellStyle;
    private SynthContext paintContext;
    private boolean drawHorizontalLines;
    private boolean drawVerticalLines;
    private Object linesStyle;
    private int padding;
    private boolean useTreeColors;
    private Icon expandedIconWrapper;
    
    public SynthTreeUI() {
        this.expandedIconWrapper = new ExpandedIconWrapper();
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthTreeUI();
    }
    
    @Override
    public Icon getExpandedIcon() {
        return this.expandedIconWrapper;
    }
    
    @Override
    protected void installDefaults() {
        this.updateStyle(this.tree);
    }
    
    private void updateStyle(final JTree tree) {
        final SynthContext context = this.getContext(tree, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style) {
            this.setExpandedIcon(this.style.getIcon(context, "Tree.expandedIcon"));
            this.setCollapsedIcon(this.style.getIcon(context, "Tree.collapsedIcon"));
            this.setLeftChildIndent(this.style.getInt(context, "Tree.leftChildIndent", 0));
            this.setRightChildIndent(this.style.getInt(context, "Tree.rightChildIndent", 0));
            this.drawHorizontalLines = this.style.getBoolean(context, "Tree.drawHorizontalLines", true);
            this.drawVerticalLines = this.style.getBoolean(context, "Tree.drawVerticalLines", true);
            this.linesStyle = this.style.get(context, "Tree.linesStyle");
            final Object value = this.style.get(context, "Tree.rowHeight");
            if (value != null) {
                LookAndFeel.installProperty(tree, "rowHeight", value);
            }
            final Object value2 = this.style.get(context, "Tree.scrollsOnExpand");
            LookAndFeel.installProperty(tree, "scrollsOnExpand", (value2 != null) ? value2 : Boolean.TRUE);
            this.padding = this.style.getInt(context, "Tree.padding", 0);
            this.largeModel = (tree.isLargeModel() && tree.getRowHeight() > 0);
            this.useTreeColors = this.style.getBoolean(context, "Tree.rendererUseTreeColors", true);
            LookAndFeel.installProperty(tree, "showsRootHandles", this.style.getBoolean(context, "Tree.showsRootHandles", Boolean.TRUE));
            if (style != null) {
                this.uninstallKeyboardActions();
                this.installKeyboardActions();
            }
        }
        context.dispose();
        final SynthContext context2 = this.getContext(tree, Region.TREE_CELL, 1);
        this.cellStyle = SynthLookAndFeel.updateStyle(context2, this);
        context2.dispose();
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.tree.addPropertyChangeListener(this);
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, SynthLookAndFeel.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    private SynthContext getContext(final JComponent component, final Region region) {
        return this.getContext(component, region, this.getComponentState(component, region));
    }
    
    private SynthContext getContext(final JComponent component, final Region region, final int n) {
        return SynthContext.getContext(component, region, this.cellStyle, n);
    }
    
    private int getComponentState(final JComponent component, final Region region) {
        return 513;
    }
    
    @Override
    protected TreeCellEditor createDefaultCellEditor() {
        final TreeCellRenderer cellRenderer = this.tree.getCellRenderer();
        SynthTreeCellEditor synthTreeCellEditor;
        if (cellRenderer != null && cellRenderer instanceof DefaultTreeCellRenderer) {
            synthTreeCellEditor = new SynthTreeCellEditor(this.tree, (DefaultTreeCellRenderer)cellRenderer);
        }
        else {
            synthTreeCellEditor = new SynthTreeCellEditor(this.tree, null);
        }
        return synthTreeCellEditor;
    }
    
    @Override
    protected TreeCellRenderer createDefaultCellRenderer() {
        return new SynthTreeCellRenderer();
    }
    
    @Override
    protected void uninstallDefaults() {
        final SynthContext context = this.getContext(this.tree, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
        final SynthContext context2 = this.getContext(this.tree, Region.TREE_CELL, 1);
        this.cellStyle.uninstallDefaults(context2);
        context2.dispose();
        this.cellStyle = null;
        if (this.tree.getTransferHandler() instanceof UIResource) {
            this.tree.setTransferHandler(null);
        }
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.tree.removePropertyChangeListener(this);
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintTreeBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
        this.paint(context, graphics);
        context.dispose();
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintTreeBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        this.paint(context, graphics);
        context.dispose();
    }
    
    protected void paint(final SynthContext paintContext, final Graphics graphics) {
        this.paintContext = paintContext;
        this.updateLeadSelectionRow();
        final Rectangle clipBounds = graphics.getClipBounds();
        final Insets insets = this.tree.getInsets();
        final TreePath closestPathForLocation = this.getClosestPathForLocation(this.tree, 0, clipBounds.y);
        final Enumeration<TreePath> visiblePaths = this.treeState.getVisiblePathsFrom(closestPathForLocation);
        int rowForPath = this.treeState.getRowForPath(closestPathForLocation);
        final int n = clipBounds.y + clipBounds.height;
        final TreeModel model = this.tree.getModel();
        final SynthContext context = this.getContext(this.tree, Region.TREE_CELL);
        this.drawingCache.clear();
        this.setHashColor(paintContext.getStyle().getColor(paintContext, ColorType.FOREGROUND));
        if (visiblePaths != null) {
            int n2 = 0;
            final Rectangle rectangle = new Rectangle(0, 0, this.tree.getWidth(), 0);
            final TreeCellRenderer cellRenderer = this.tree.getCellRenderer();
            final DefaultTreeCellRenderer defaultTreeCellRenderer = (cellRenderer instanceof DefaultTreeCellRenderer) ? ((DefaultTreeCellRenderer)cellRenderer) : null;
            this.configureRenderer(context);
            while (n2 == 0 && visiblePaths.hasMoreElements()) {
                final TreePath treePath = visiblePaths.nextElement();
                final Rectangle pathBounds = this.getPathBounds(this.tree, treePath);
                if (treePath != null && pathBounds != null) {
                    final boolean leaf = model.isLeaf(treePath.getLastPathComponent());
                    boolean expandedState;
                    boolean hasBeenExpanded;
                    if (leaf) {
                        hasBeenExpanded = (expandedState = false);
                    }
                    else {
                        expandedState = this.treeState.getExpandedState(treePath);
                        hasBeenExpanded = this.tree.hasBeenExpanded(treePath);
                    }
                    rectangle.y = pathBounds.y;
                    rectangle.height = pathBounds.height;
                    this.paintRow(cellRenderer, defaultTreeCellRenderer, paintContext, context, graphics, clipBounds, insets, pathBounds, rectangle, treePath, rowForPath, expandedState, hasBeenExpanded, leaf);
                    if (pathBounds.y + pathBounds.height >= n) {
                        n2 = 1;
                    }
                }
                else {
                    n2 = 1;
                }
                ++rowForPath;
            }
            final boolean rootVisible = this.tree.isRootVisible();
            for (TreePath treePath2 = closestPathForLocation.getParentPath(); treePath2 != null; treePath2 = treePath2.getParentPath()) {
                this.paintVerticalPartOfLeg(graphics, clipBounds, insets, treePath2);
                this.drawingCache.put(treePath2, Boolean.TRUE);
            }
            int n3 = 0;
            final Enumeration<TreePath> visiblePaths2 = this.treeState.getVisiblePathsFrom(closestPathForLocation);
            while (n3 == 0 && visiblePaths2.hasMoreElements()) {
                final TreePath treePath3 = visiblePaths2.nextElement();
                final Rectangle pathBounds2 = this.getPathBounds(this.tree, treePath3);
                if (treePath3 != null && pathBounds2 != null) {
                    final boolean leaf2 = model.isLeaf(treePath3.getLastPathComponent());
                    boolean expandedState2;
                    boolean hasBeenExpanded2;
                    if (leaf2) {
                        hasBeenExpanded2 = (expandedState2 = false);
                    }
                    else {
                        expandedState2 = this.treeState.getExpandedState(treePath3);
                        hasBeenExpanded2 = this.tree.hasBeenExpanded(treePath3);
                    }
                    final TreePath parentPath = treePath3.getParentPath();
                    if (parentPath != null) {
                        if (this.drawingCache.get(parentPath) == null) {
                            this.paintVerticalPartOfLeg(graphics, clipBounds, insets, parentPath);
                            this.drawingCache.put(parentPath, Boolean.TRUE);
                        }
                        this.paintHorizontalPartOfLeg(graphics, clipBounds, insets, pathBounds2, treePath3, rowForPath, expandedState2, hasBeenExpanded2, leaf2);
                    }
                    else if (rootVisible && rowForPath == 0) {
                        this.paintHorizontalPartOfLeg(graphics, clipBounds, insets, pathBounds2, treePath3, rowForPath, expandedState2, hasBeenExpanded2, leaf2);
                    }
                    if (this.shouldPaintExpandControl(treePath3, rowForPath, expandedState2, hasBeenExpanded2, leaf2)) {
                        this.paintExpandControl(graphics, clipBounds, insets, pathBounds2, treePath3, rowForPath, expandedState2, hasBeenExpanded2, leaf2);
                    }
                    if (pathBounds2.y + pathBounds2.height >= n) {
                        n3 = 1;
                    }
                }
                else {
                    n3 = 1;
                }
                ++rowForPath;
            }
        }
        context.dispose();
        this.paintDropLine(graphics);
        this.rendererPane.removeAll();
        this.paintContext = null;
    }
    
    private void configureRenderer(final SynthContext synthContext) {
        final TreeCellRenderer cellRenderer = this.tree.getCellRenderer();
        if (cellRenderer instanceof DefaultTreeCellRenderer) {
            final DefaultTreeCellRenderer defaultTreeCellRenderer = (DefaultTreeCellRenderer)cellRenderer;
            final SynthStyle style = synthContext.getStyle();
            synthContext.setComponentState(513);
            final Color textSelectionColor = defaultTreeCellRenderer.getTextSelectionColor();
            if (textSelectionColor == null || textSelectionColor instanceof UIResource) {
                defaultTreeCellRenderer.setTextSelectionColor(style.getColor(synthContext, ColorType.TEXT_FOREGROUND));
            }
            final Color backgroundSelectionColor = defaultTreeCellRenderer.getBackgroundSelectionColor();
            if (backgroundSelectionColor == null || backgroundSelectionColor instanceof UIResource) {
                defaultTreeCellRenderer.setBackgroundSelectionColor(style.getColor(synthContext, ColorType.TEXT_BACKGROUND));
            }
            synthContext.setComponentState(1);
            final Color textNonSelectionColor = defaultTreeCellRenderer.getTextNonSelectionColor();
            if (textNonSelectionColor == null || textNonSelectionColor instanceof UIResource) {
                defaultTreeCellRenderer.setTextNonSelectionColor(style.getColorForState(synthContext, ColorType.TEXT_FOREGROUND));
            }
            final Color backgroundNonSelectionColor = defaultTreeCellRenderer.getBackgroundNonSelectionColor();
            if (backgroundNonSelectionColor == null || backgroundNonSelectionColor instanceof UIResource) {
                defaultTreeCellRenderer.setBackgroundNonSelectionColor(style.getColorForState(synthContext, ColorType.TEXT_BACKGROUND));
            }
        }
    }
    
    @Override
    protected void paintHorizontalPartOfLeg(final Graphics graphics, final Rectangle rectangle, final Insets insets, final Rectangle rectangle2, final TreePath treePath, final int n, final boolean b, final boolean b2, final boolean b3) {
        if (this.drawHorizontalLines) {
            super.paintHorizontalPartOfLeg(graphics, rectangle, insets, rectangle2, treePath, n, b, b2, b3);
        }
    }
    
    @Override
    protected void paintHorizontalLine(final Graphics graphics, final JComponent component, final int n, final int n2, final int n3) {
        this.paintContext.getStyle().getGraphicsUtils(this.paintContext).drawLine(this.paintContext, "Tree.horizontalLine", graphics, n2, n, n3, n, this.linesStyle);
    }
    
    @Override
    protected void paintVerticalPartOfLeg(final Graphics graphics, final Rectangle rectangle, final Insets insets, final TreePath treePath) {
        if (this.drawVerticalLines) {
            super.paintVerticalPartOfLeg(graphics, rectangle, insets, treePath);
        }
    }
    
    @Override
    protected void paintVerticalLine(final Graphics graphics, final JComponent component, final int n, final int n2, final int n3) {
        this.paintContext.getStyle().getGraphicsUtils(this.paintContext).drawLine(this.paintContext, "Tree.verticalLine", graphics, n, n2, n, n3, this.linesStyle);
    }
    
    private void paintRow(final TreeCellRenderer treeCellRenderer, final DefaultTreeCellRenderer defaultTreeCellRenderer, final SynthContext synthContext, final SynthContext synthContext2, final Graphics graphics, final Rectangle rectangle, final Insets insets, final Rectangle rectangle2, final Rectangle rectangle3, final TreePath treePath, final int n, final boolean b, final boolean b2, final boolean b3) {
        final boolean rowSelected = this.tree.isRowSelected(n);
        final JTree.DropLocation dropLocation = this.tree.getDropLocation();
        final boolean b4 = dropLocation != null && dropLocation.getChildIndex() == -1 && treePath == dropLocation.getPath();
        int componentState = 1;
        if (rowSelected || b4) {
            componentState |= 0x200;
        }
        if (this.tree.isFocusOwner() && n == this.getLeadSelectionRow()) {
            componentState |= 0x100;
        }
        synthContext2.setComponentState(componentState);
        if (defaultTreeCellRenderer != null && defaultTreeCellRenderer.getBorderSelectionColor() instanceof UIResource) {
            defaultTreeCellRenderer.setBorderSelectionColor(this.style.getColor(synthContext2, ColorType.FOCUS));
        }
        SynthLookAndFeel.updateSubregion(synthContext2, graphics, rectangle3);
        synthContext2.getPainter().paintTreeCellBackground(synthContext2, graphics, rectangle3.x, rectangle3.y, rectangle3.width, rectangle3.height);
        synthContext2.getPainter().paintTreeCellBorder(synthContext2, graphics, rectangle3.x, rectangle3.y, rectangle3.width, rectangle3.height);
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
        this.rendererPane.paintComponent(graphics, treeCellRenderer.getTreeCellRendererComponent(this.tree, treePath.getLastPathComponent(), rowSelected, b, b3, n, leadSelectionRow == n), this.tree, rectangle2.x, rectangle2.y, rectangle2.width, rectangle2.height, true);
    }
    
    private int findCenteredX(final int n, final int n2) {
        return this.tree.getComponentOrientation().isLeftToRight() ? (n - (int)Math.ceil(n2 / 2.0)) : (n - (int)Math.floor(n2 / 2.0));
    }
    
    @Override
    protected void paintExpandControl(final Graphics graphics, final Rectangle rectangle, final Insets insets, final Rectangle rectangle2, final TreePath treePath, final int n, final boolean b, final boolean b2, final boolean b3) {
        final boolean pathSelected = this.tree.getSelectionModel().isPathSelected(treePath);
        final int componentState = this.paintContext.getComponentState();
        if (pathSelected) {
            this.paintContext.setComponentState(componentState | 0x200);
        }
        super.paintExpandControl(graphics, rectangle, insets, rectangle2, treePath, n, b, b2, b3);
        this.paintContext.setComponentState(componentState);
    }
    
    @Override
    protected void drawCentered(final Component component, final Graphics graphics, final Icon icon, final int n, final int n2) {
        final int iconWidth = SynthIcon.getIconWidth(icon, this.paintContext);
        final int iconHeight = SynthIcon.getIconHeight(icon, this.paintContext);
        SynthIcon.paintIcon(icon, this.paintContext, graphics, this.findCenteredX(n, iconWidth), n2 - iconHeight / 2, iconWidth, iconHeight);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JTree)propertyChangeEvent.getSource());
        }
        if ("dropLocation" == propertyChangeEvent.getPropertyName()) {
            this.repaintDropLocation((JTree.DropLocation)propertyChangeEvent.getOldValue());
            this.repaintDropLocation(this.tree.getDropLocation());
        }
    }
    
    @Override
    protected void paintDropLine(final Graphics graphics) {
        final JTree.DropLocation dropLocation = this.tree.getDropLocation();
        if (!this.isDropLine(dropLocation)) {
            return;
        }
        final Color color = (Color)this.style.get(this.paintContext, "Tree.dropLineColor");
        if (color != null) {
            graphics.setColor(color);
            final Rectangle dropLineRect = this.getDropLineRect(dropLocation);
            graphics.fillRect(dropLineRect.x, dropLineRect.y, dropLineRect.width, dropLineRect.height);
        }
    }
    
    private void repaintDropLocation(final JTree.DropLocation dropLocation) {
        if (dropLocation == null) {
            return;
        }
        Rectangle rectangle;
        if (this.isDropLine(dropLocation)) {
            rectangle = this.getDropLineRect(dropLocation);
        }
        else {
            rectangle = this.tree.getPathBounds(dropLocation.getPath());
            if (rectangle != null) {
                rectangle.x = 0;
                rectangle.width = this.tree.getWidth();
            }
        }
        if (rectangle != null) {
            this.tree.repaint(rectangle);
        }
    }
    
    @Override
    protected int getRowX(final int n, final int n2) {
        return super.getRowX(n, n2) + this.padding;
    }
    
    private class SynthTreeCellRenderer extends DefaultTreeCellRenderer implements UIResource
    {
        SynthTreeCellRenderer() {
        }
        
        @Override
        public String getName() {
            return "Tree.cellRenderer";
        }
        
        @Override
        public Component getTreeCellRendererComponent(final JTree tree, final Object o, final boolean b, final boolean b2, final boolean b3, final int n, final boolean b4) {
            if (!SynthTreeUI.this.useTreeColors && (b || b4)) {
                SynthLookAndFeel.setSelectedUI((ComponentUI)SynthLookAndFeel.getUIOfType(this.getUI(), SynthLabelUI.class), b, b4, tree.isEnabled(), false);
            }
            else {
                SynthLookAndFeel.resetSelectedUI();
            }
            return super.getTreeCellRendererComponent(tree, o, b, b2, b3, n, b4);
        }
        
        @Override
        public void paint(final Graphics graphics) {
            this.paintComponent(graphics);
            if (this.hasFocus) {
                final SynthContext access$300 = SynthTreeUI.this.getContext(SynthTreeUI.this.tree, Region.TREE_CELL);
                if (access$300.getStyle() == null) {
                    assert false : "SynthTreeCellRenderer is being used outside of UI that created it";
                    return;
                }
                else {
                    int n = 0;
                    final Icon icon = this.getIcon();
                    if (icon != null && this.getText() != null) {
                        n = icon.getIconWidth() + Math.max(0, this.getIconTextGap() - 1);
                    }
                    if (this.selected) {
                        access$300.setComponentState(513);
                    }
                    else {
                        access$300.setComponentState(1);
                    }
                    if (this.getComponentOrientation().isLeftToRight()) {
                        access$300.getPainter().paintTreeCellFocus(access$300, graphics, n, 0, this.getWidth() - n, this.getHeight());
                    }
                    else {
                        access$300.getPainter().paintTreeCellFocus(access$300, graphics, 0, 0, this.getWidth() - n, this.getHeight());
                    }
                    access$300.dispose();
                }
            }
            SynthLookAndFeel.resetSelectedUI();
        }
    }
    
    private static class SynthTreeCellEditor extends DefaultTreeCellEditor
    {
        public SynthTreeCellEditor(final JTree tree, final DefaultTreeCellRenderer defaultTreeCellRenderer) {
            super(tree, defaultTreeCellRenderer);
            this.setBorderSelectionColor(null);
        }
        
        @Override
        protected TreeCellEditor createTreeCellEditor() {
            final DefaultCellEditor defaultCellEditor = new DefaultCellEditor(new JTextField() {
                @Override
                public String getName() {
                    return "Tree.cellEditor";
                }
            });
            defaultCellEditor.setClickCountToStart(1);
            return defaultCellEditor;
        }
    }
    
    private class ExpandedIconWrapper extends SynthIcon
    {
        @Override
        public void paintIcon(SynthContext context, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (context == null) {
                context = SynthTreeUI.this.getContext(SynthTreeUI.this.tree);
                SynthIcon.paintIcon(SynthTreeUI.this.expandedIcon, context, graphics, n, n2, n3, n4);
                context.dispose();
            }
            else {
                SynthIcon.paintIcon(SynthTreeUI.this.expandedIcon, context, graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public int getIconWidth(SynthContext context) {
            int n;
            if (context == null) {
                context = SynthTreeUI.this.getContext(SynthTreeUI.this.tree);
                n = SynthIcon.getIconWidth(SynthTreeUI.this.expandedIcon, context);
                context.dispose();
            }
            else {
                n = SynthIcon.getIconWidth(SynthTreeUI.this.expandedIcon, context);
            }
            return n;
        }
        
        @Override
        public int getIconHeight(SynthContext context) {
            int n;
            if (context == null) {
                context = SynthTreeUI.this.getContext(SynthTreeUI.this.tree);
                n = SynthIcon.getIconHeight(SynthTreeUI.this.expandedIcon, context);
                context.dispose();
            }
            else {
                n = SynthIcon.getIconHeight(SynthTreeUI.this.expandedIcon, context);
            }
            return n;
        }
    }
}
