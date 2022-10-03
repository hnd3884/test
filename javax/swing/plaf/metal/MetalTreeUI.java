package javax.swing.plaf.metal;

import java.beans.PropertyChangeEvent;
import javax.swing.tree.TreePath;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import java.awt.Color;
import javax.swing.plaf.basic.BasicTreeUI;

public class MetalTreeUI extends BasicTreeUI
{
    private static Color lineColor;
    private static final String LINE_STYLE = "JTree.lineStyle";
    private static final String LEG_LINE_STYLE_STRING = "Angled";
    private static final String HORIZ_STYLE_STRING = "Horizontal";
    private static final String NO_STYLE_STRING = "None";
    private static final int LEG_LINE_STYLE = 2;
    private static final int HORIZ_LINE_STYLE = 1;
    private static final int NO_LINE_STYLE = 0;
    private int lineStyle;
    private PropertyChangeListener lineStyleListener;
    
    public static ComponentUI createUI(final JComponent component) {
        return new MetalTreeUI();
    }
    
    public MetalTreeUI() {
        this.lineStyle = 2;
        this.lineStyleListener = new LineListener();
    }
    
    @Override
    protected int getHorizontalLegBuffer() {
        return 3;
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        MetalTreeUI.lineColor = UIManager.getColor("Tree.line");
        this.decodeLineStyle(component.getClientProperty("JTree.lineStyle"));
        component.addPropertyChangeListener(this.lineStyleListener);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        component.removePropertyChangeListener(this.lineStyleListener);
        super.uninstallUI(component);
    }
    
    protected void decodeLineStyle(final Object o) {
        if (o == null || o.equals("Angled")) {
            this.lineStyle = 2;
        }
        else if (o.equals("None")) {
            this.lineStyle = 0;
        }
        else if (o.equals("Horizontal")) {
            this.lineStyle = 1;
        }
    }
    
    protected boolean isLocationInExpandControl(final int n, final int n2, final int n3, final int n4) {
        if (this.tree != null && !this.isLeaf(n)) {
            int n5;
            if (this.getExpandedIcon() != null) {
                n5 = this.getExpandedIcon().getIconWidth() + 6;
            }
            else {
                n5 = 8;
            }
            final Insets insets = this.tree.getInsets();
            final int n6 = ((insets != null) ? insets.left : 0) + ((n2 + this.depthOffset - 1) * this.totalChildIndent + this.getLeftChildIndent() - n5 / 2);
            final int n7 = n6 + n5;
            return n3 >= n6 && n3 <= n7;
        }
        return false;
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        super.paint(graphics, component);
        if (this.lineStyle == 1 && !this.largeModel) {
            this.paintHorizontalSeparators(graphics, component);
        }
    }
    
    protected void paintHorizontalSeparators(final Graphics graphics, final JComponent component) {
        graphics.setColor(MetalTreeUI.lineColor);
        final Rectangle clipBounds = graphics.getClipBounds();
        final int rowForPath = this.getRowForPath(this.tree, this.getClosestPathForLocation(this.tree, 0, clipBounds.y));
        final int rowForPath2 = this.getRowForPath(this.tree, this.getClosestPathForLocation(this.tree, 0, clipBounds.y + clipBounds.height - 1));
        if (rowForPath <= -1 || rowForPath2 <= -1) {
            return;
        }
        for (int i = rowForPath; i <= rowForPath2; ++i) {
            final TreePath pathForRow = this.getPathForRow(this.tree, i);
            if (pathForRow != null && pathForRow.getPathCount() == 2) {
                final Rectangle pathBounds = this.getPathBounds(this.tree, this.getPathForRow(this.tree, i));
                if (pathBounds != null) {
                    graphics.drawLine(clipBounds.x, pathBounds.y, clipBounds.x + clipBounds.width, pathBounds.y);
                }
            }
        }
    }
    
    @Override
    protected void paintVerticalPartOfLeg(final Graphics graphics, final Rectangle rectangle, final Insets insets, final TreePath treePath) {
        if (this.lineStyle == 2) {
            super.paintVerticalPartOfLeg(graphics, rectangle, insets, treePath);
        }
    }
    
    @Override
    protected void paintHorizontalPartOfLeg(final Graphics graphics, final Rectangle rectangle, final Insets insets, final Rectangle rectangle2, final TreePath treePath, final int n, final boolean b, final boolean b2, final boolean b3) {
        if (this.lineStyle == 2) {
            super.paintHorizontalPartOfLeg(graphics, rectangle, insets, rectangle2, treePath, n, b, b2, b3);
        }
    }
    
    class LineListener implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals("JTree.lineStyle")) {
                MetalTreeUI.this.decodeLineStyle(propertyChangeEvent.getNewValue());
            }
        }
    }
}
