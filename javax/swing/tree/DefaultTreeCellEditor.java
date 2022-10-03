package javax.swing.tree;

import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Dimension;
import javax.swing.plaf.FontUIResource;
import javax.swing.border.Border;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;
import java.io.ObjectOutputStream;
import javax.swing.JTextField;
import java.awt.Rectangle;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.DefaultCellEditor;
import javax.swing.event.CellEditorListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.UIManager;
import java.awt.Font;
import javax.swing.Icon;
import java.awt.Color;
import javax.swing.Timer;
import javax.swing.JTree;
import java.awt.Component;
import java.awt.Container;
import javax.swing.event.TreeSelectionListener;
import java.awt.event.ActionListener;

public class DefaultTreeCellEditor implements ActionListener, TreeCellEditor, TreeSelectionListener
{
    protected TreeCellEditor realEditor;
    protected DefaultTreeCellRenderer renderer;
    protected Container editingContainer;
    protected transient Component editingComponent;
    protected boolean canEdit;
    protected transient int offset;
    protected transient JTree tree;
    protected transient TreePath lastPath;
    protected transient Timer timer;
    protected transient int lastRow;
    protected Color borderSelectionColor;
    protected transient Icon editingIcon;
    protected Font font;
    
    public DefaultTreeCellEditor(final JTree tree, final DefaultTreeCellRenderer defaultTreeCellRenderer) {
        this(tree, defaultTreeCellRenderer, null);
    }
    
    public DefaultTreeCellEditor(final JTree tree, final DefaultTreeCellRenderer renderer, final TreeCellEditor realEditor) {
        this.renderer = renderer;
        this.realEditor = realEditor;
        if (this.realEditor == null) {
            this.realEditor = this.createTreeCellEditor();
        }
        this.editingContainer = this.createContainer();
        this.setTree(tree);
        this.setBorderSelectionColor(UIManager.getColor("Tree.editorBorderSelectionColor"));
    }
    
    public void setBorderSelectionColor(final Color borderSelectionColor) {
        this.borderSelectionColor = borderSelectionColor;
    }
    
    public Color getBorderSelectionColor() {
        return this.borderSelectionColor;
    }
    
    public void setFont(final Font font) {
        this.font = font;
    }
    
    public Font getFont() {
        return this.font;
    }
    
    @Override
    public Component getTreeCellEditorComponent(final JTree tree, final Object o, final boolean b, final boolean b2, final boolean b3, final int lastRow) {
        this.setTree(tree);
        this.determineOffset(tree, o, b, b2, b3, this.lastRow = lastRow);
        if (this.editingComponent != null) {
            this.editingContainer.remove(this.editingComponent);
        }
        this.editingComponent = this.realEditor.getTreeCellEditorComponent(tree, o, b, b2, b3, lastRow);
        final TreePath pathForRow = tree.getPathForRow(lastRow);
        this.canEdit = (this.lastPath != null && pathForRow != null && this.lastPath.equals(pathForRow));
        Font font = this.getFont();
        if (font == null) {
            if (this.renderer != null) {
                font = this.renderer.getFont();
            }
            if (font == null) {
                font = tree.getFont();
            }
        }
        this.editingContainer.setFont(font);
        this.prepareForEditing();
        return this.editingContainer;
    }
    
    @Override
    public Object getCellEditorValue() {
        return this.realEditor.getCellEditorValue();
    }
    
    @Override
    public boolean isCellEditable(final EventObject eventObject) {
        boolean b = false;
        boolean b2 = false;
        if (eventObject != null && eventObject.getSource() instanceof JTree) {
            this.setTree((JTree)eventObject.getSource());
            if (eventObject instanceof MouseEvent) {
                final TreePath pathForLocation = this.tree.getPathForLocation(((MouseEvent)eventObject).getX(), ((MouseEvent)eventObject).getY());
                b2 = (this.lastPath != null && pathForLocation != null && this.lastPath.equals(pathForLocation));
                if (pathForLocation != null) {
                    this.lastRow = this.tree.getRowForPath(pathForLocation);
                    final Object lastPathComponent = pathForLocation.getLastPathComponent();
                    this.determineOffset(this.tree, lastPathComponent, this.tree.isRowSelected(this.lastRow), this.tree.isExpanded(pathForLocation), this.tree.getModel().isLeaf(lastPathComponent), this.lastRow);
                }
            }
        }
        if (!this.realEditor.isCellEditable(eventObject)) {
            return false;
        }
        if (this.canEditImmediately(eventObject)) {
            b = true;
        }
        else if (b2 && this.shouldStartEditingTimer(eventObject)) {
            this.startEditingTimer();
        }
        else if (this.timer != null && this.timer.isRunning()) {
            this.timer.stop();
        }
        if (b) {
            this.prepareForEditing();
        }
        return b;
    }
    
    @Override
    public boolean shouldSelectCell(final EventObject eventObject) {
        return this.realEditor.shouldSelectCell(eventObject);
    }
    
    @Override
    public boolean stopCellEditing() {
        if (this.realEditor.stopCellEditing()) {
            this.cleanupAfterEditing();
            return true;
        }
        return false;
    }
    
    @Override
    public void cancelCellEditing() {
        this.realEditor.cancelCellEditing();
        this.cleanupAfterEditing();
    }
    
    @Override
    public void addCellEditorListener(final CellEditorListener cellEditorListener) {
        this.realEditor.addCellEditorListener(cellEditorListener);
    }
    
    @Override
    public void removeCellEditorListener(final CellEditorListener cellEditorListener) {
        this.realEditor.removeCellEditorListener(cellEditorListener);
    }
    
    public CellEditorListener[] getCellEditorListeners() {
        return ((DefaultCellEditor)this.realEditor).getCellEditorListeners();
    }
    
    @Override
    public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {
        if (this.tree != null) {
            if (this.tree.getSelectionCount() == 1) {
                this.lastPath = this.tree.getSelectionPath();
            }
            else {
                this.lastPath = null;
            }
        }
        if (this.timer != null) {
            this.timer.stop();
        }
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        if (this.tree != null && this.lastPath != null) {
            this.tree.startEditingAtPath(this.lastPath);
        }
    }
    
    protected void setTree(final JTree tree) {
        if (this.tree != tree) {
            if (this.tree != null) {
                this.tree.removeTreeSelectionListener(this);
            }
            this.tree = tree;
            if (this.tree != null) {
                this.tree.addTreeSelectionListener(this);
            }
            if (this.timer != null) {
                this.timer.stop();
            }
        }
    }
    
    protected boolean shouldStartEditingTimer(final EventObject eventObject) {
        if (eventObject instanceof MouseEvent && SwingUtilities.isLeftMouseButton((MouseEvent)eventObject)) {
            final MouseEvent mouseEvent = (MouseEvent)eventObject;
            return mouseEvent.getClickCount() == 1 && this.inHitRegion(mouseEvent.getX(), mouseEvent.getY());
        }
        return false;
    }
    
    protected void startEditingTimer() {
        if (this.timer == null) {
            (this.timer = new Timer(1200, this)).setRepeats(false);
        }
        this.timer.start();
    }
    
    protected boolean canEditImmediately(final EventObject eventObject) {
        if (eventObject instanceof MouseEvent && SwingUtilities.isLeftMouseButton((MouseEvent)eventObject)) {
            final MouseEvent mouseEvent = (MouseEvent)eventObject;
            return mouseEvent.getClickCount() > 2 && this.inHitRegion(mouseEvent.getX(), mouseEvent.getY());
        }
        return eventObject == null;
    }
    
    protected boolean inHitRegion(final int n, final int n2) {
        if (this.lastRow != -1 && this.tree != null) {
            final Rectangle rowBounds = this.tree.getRowBounds(this.lastRow);
            if (this.tree.getComponentOrientation().isLeftToRight()) {
                if (rowBounds != null && n <= rowBounds.x + this.offset && this.offset < rowBounds.width - 5) {
                    return false;
                }
            }
            else if (rowBounds != null && (n >= rowBounds.x + rowBounds.width - this.offset + 5 || n <= rowBounds.x + 5) && this.offset < rowBounds.width - 5) {
                return false;
            }
        }
        return true;
    }
    
    protected void determineOffset(final JTree tree, final Object o, final boolean b, final boolean b2, final boolean b3, final int n) {
        if (this.renderer != null) {
            if (b3) {
                this.editingIcon = this.renderer.getLeafIcon();
            }
            else if (b2) {
                this.editingIcon = this.renderer.getOpenIcon();
            }
            else {
                this.editingIcon = this.renderer.getClosedIcon();
            }
            if (this.editingIcon != null) {
                this.offset = this.renderer.getIconTextGap() + this.editingIcon.getIconWidth();
            }
            else {
                this.offset = this.renderer.getIconTextGap();
            }
        }
        else {
            this.editingIcon = null;
            this.offset = 0;
        }
    }
    
    protected void prepareForEditing() {
        if (this.editingComponent != null) {
            this.editingContainer.add(this.editingComponent);
        }
    }
    
    protected Container createContainer() {
        return new EditorContainer();
    }
    
    protected TreeCellEditor createTreeCellEditor() {
        final DefaultCellEditor defaultCellEditor = new DefaultCellEditor(new DefaultTextField(UIManager.getBorder("Tree.editorBorder"))) {
            @Override
            public boolean shouldSelectCell(final EventObject eventObject) {
                return super.shouldSelectCell(eventObject);
            }
        };
        defaultCellEditor.setClickCountToStart(1);
        return defaultCellEditor;
    }
    
    private void cleanupAfterEditing() {
        if (this.editingComponent != null) {
            this.editingContainer.remove(this.editingComponent);
        }
        this.editingComponent = null;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final Vector vector = new Vector();
        objectOutputStream.defaultWriteObject();
        if (this.realEditor != null && this.realEditor instanceof Serializable) {
            vector.addElement("realEditor");
            vector.addElement(this.realEditor);
        }
        objectOutputStream.writeObject(vector);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final Vector vector = (Vector)objectInputStream.readObject();
        int n = 0;
        if (n < vector.size() && vector.elementAt(n).equals("realEditor")) {
            this.realEditor = (TreeCellEditor)vector.elementAt(++n);
            ++n;
        }
    }
    
    public class DefaultTextField extends JTextField
    {
        protected Border border;
        
        public DefaultTextField(final Border border) {
            this.setBorder(border);
        }
        
        @Override
        public void setBorder(final Border border) {
            super.setBorder(border);
            this.border = border;
        }
        
        @Override
        public Border getBorder() {
            return this.border;
        }
        
        @Override
        public Font getFont() {
            Font font = super.getFont();
            if (font instanceof FontUIResource) {
                final Container parent = this.getParent();
                if (parent != null && parent.getFont() != null) {
                    font = parent.getFont();
                }
            }
            return font;
        }
        
        @Override
        public Dimension getPreferredSize() {
            final Dimension preferredSize = super.getPreferredSize();
            if (DefaultTreeCellEditor.this.renderer != null && DefaultTreeCellEditor.this.getFont() == null) {
                preferredSize.height = DefaultTreeCellEditor.this.renderer.getPreferredSize().height;
            }
            return preferredSize;
        }
    }
    
    public class EditorContainer extends Container
    {
        public EditorContainer() {
            this.setLayout(null);
        }
        
        public void EditorContainer() {
            this.setLayout(null);
        }
        
        @Override
        public void paint(final Graphics graphics) {
            final int width = this.getWidth();
            final int height = this.getHeight();
            if (DefaultTreeCellEditor.this.editingIcon != null) {
                final int calculateIconY = this.calculateIconY(DefaultTreeCellEditor.this.editingIcon);
                if (this.getComponentOrientation().isLeftToRight()) {
                    DefaultTreeCellEditor.this.editingIcon.paintIcon(this, graphics, 0, calculateIconY);
                }
                else {
                    DefaultTreeCellEditor.this.editingIcon.paintIcon(this, graphics, width - DefaultTreeCellEditor.this.editingIcon.getIconWidth(), calculateIconY);
                }
            }
            final Color borderSelectionColor = DefaultTreeCellEditor.this.getBorderSelectionColor();
            if (borderSelectionColor != null) {
                graphics.setColor(borderSelectionColor);
                graphics.drawRect(0, 0, width - 1, height - 1);
            }
            super.paint(graphics);
        }
        
        @Override
        public void doLayout() {
            if (DefaultTreeCellEditor.this.editingComponent != null) {
                final int width = this.getWidth();
                final int height = this.getHeight();
                if (this.getComponentOrientation().isLeftToRight()) {
                    DefaultTreeCellEditor.this.editingComponent.setBounds(DefaultTreeCellEditor.this.offset, 0, width - DefaultTreeCellEditor.this.offset, height);
                }
                else {
                    DefaultTreeCellEditor.this.editingComponent.setBounds(0, 0, width - DefaultTreeCellEditor.this.offset, height);
                }
            }
        }
        
        private int calculateIconY(final Icon icon) {
            final int iconHeight = icon.getIconHeight();
            final int height = DefaultTreeCellEditor.this.editingComponent.getFontMetrics(DefaultTreeCellEditor.this.editingComponent.getFont()).getHeight();
            final int n = iconHeight / 2 - height / 2;
            final int min = Math.min(0, n);
            return this.getHeight() / 2 - (min + (Math.max(iconHeight, n + height) - min) / 2);
        }
        
        @Override
        public Dimension getPreferredSize() {
            if (DefaultTreeCellEditor.this.editingComponent != null) {
                final Dimension preferredSize;
                final Dimension dimension = preferredSize = DefaultTreeCellEditor.this.editingComponent.getPreferredSize();
                preferredSize.width += DefaultTreeCellEditor.this.offset + 5;
                final Dimension dimension2 = (DefaultTreeCellEditor.this.renderer != null) ? DefaultTreeCellEditor.this.renderer.getPreferredSize() : null;
                if (dimension2 != null) {
                    dimension.height = Math.max(dimension.height, dimension2.height);
                }
                if (DefaultTreeCellEditor.this.editingIcon != null) {
                    dimension.height = Math.max(dimension.height, DefaultTreeCellEditor.this.editingIcon.getIconHeight());
                }
                dimension.width = Math.max(dimension.width, 100);
                return dimension;
            }
            return new Dimension(0, 0);
        }
    }
}
