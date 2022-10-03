package javax.swing.tree;

import java.awt.Rectangle;
import java.awt.Dimension;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.UIManager;
import javax.swing.JComponent;
import sun.swing.DefaultLookup;
import javax.swing.plaf.UIResource;
import javax.swing.Icon;
import java.awt.Color;
import javax.swing.JTree;
import javax.swing.JLabel;

public class DefaultTreeCellRenderer extends JLabel implements TreeCellRenderer
{
    private JTree tree;
    protected boolean selected;
    protected boolean hasFocus;
    private boolean drawsFocusBorderAroundIcon;
    private boolean drawDashedFocusIndicator;
    private Color treeBGColor;
    private Color focusBGColor;
    protected transient Icon closedIcon;
    protected transient Icon leafIcon;
    protected transient Icon openIcon;
    protected Color textSelectionColor;
    protected Color textNonSelectionColor;
    protected Color backgroundSelectionColor;
    protected Color backgroundNonSelectionColor;
    protected Color borderSelectionColor;
    private boolean isDropCell;
    private boolean fillBackground;
    private boolean inited;
    
    public DefaultTreeCellRenderer() {
        this.inited = true;
    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        if (!this.inited || this.getLeafIcon() instanceof UIResource) {
            this.setLeafIcon(DefaultLookup.getIcon(this, this.ui, "Tree.leafIcon"));
        }
        if (!this.inited || this.getClosedIcon() instanceof UIResource) {
            this.setClosedIcon(DefaultLookup.getIcon(this, this.ui, "Tree.closedIcon"));
        }
        if (!this.inited || this.getOpenIcon() instanceof UIManager) {
            this.setOpenIcon(DefaultLookup.getIcon(this, this.ui, "Tree.openIcon"));
        }
        if (!this.inited || this.getTextSelectionColor() instanceof UIResource) {
            this.setTextSelectionColor(DefaultLookup.getColor(this, this.ui, "Tree.selectionForeground"));
        }
        if (!this.inited || this.getTextNonSelectionColor() instanceof UIResource) {
            this.setTextNonSelectionColor(DefaultLookup.getColor(this, this.ui, "Tree.textForeground"));
        }
        if (!this.inited || this.getBackgroundSelectionColor() instanceof UIResource) {
            this.setBackgroundSelectionColor(DefaultLookup.getColor(this, this.ui, "Tree.selectionBackground"));
        }
        if (!this.inited || this.getBackgroundNonSelectionColor() instanceof UIResource) {
            this.setBackgroundNonSelectionColor(DefaultLookup.getColor(this, this.ui, "Tree.textBackground"));
        }
        if (!this.inited || this.getBorderSelectionColor() instanceof UIResource) {
            this.setBorderSelectionColor(DefaultLookup.getColor(this, this.ui, "Tree.selectionBorderColor"));
        }
        this.drawsFocusBorderAroundIcon = DefaultLookup.getBoolean(this, this.ui, "Tree.drawsFocusBorderAroundIcon", false);
        this.drawDashedFocusIndicator = DefaultLookup.getBoolean(this, this.ui, "Tree.drawDashedFocusIndicator", false);
        this.fillBackground = DefaultLookup.getBoolean(this, this.ui, "Tree.rendererFillBackground", true);
        final Insets insets = DefaultLookup.getInsets(this, this.ui, "Tree.rendererMargins");
        if (insets != null) {
            this.setBorder(new EmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
        }
        this.setName("Tree.cellRenderer");
    }
    
    public Icon getDefaultOpenIcon() {
        return DefaultLookup.getIcon(this, this.ui, "Tree.openIcon");
    }
    
    public Icon getDefaultClosedIcon() {
        return DefaultLookup.getIcon(this, this.ui, "Tree.closedIcon");
    }
    
    public Icon getDefaultLeafIcon() {
        return DefaultLookup.getIcon(this, this.ui, "Tree.leafIcon");
    }
    
    public void setOpenIcon(final Icon openIcon) {
        this.openIcon = openIcon;
    }
    
    public Icon getOpenIcon() {
        return this.openIcon;
    }
    
    public void setClosedIcon(final Icon closedIcon) {
        this.closedIcon = closedIcon;
    }
    
    public Icon getClosedIcon() {
        return this.closedIcon;
    }
    
    public void setLeafIcon(final Icon leafIcon) {
        this.leafIcon = leafIcon;
    }
    
    public Icon getLeafIcon() {
        return this.leafIcon;
    }
    
    public void setTextSelectionColor(final Color textSelectionColor) {
        this.textSelectionColor = textSelectionColor;
    }
    
    public Color getTextSelectionColor() {
        return this.textSelectionColor;
    }
    
    public void setTextNonSelectionColor(final Color textNonSelectionColor) {
        this.textNonSelectionColor = textNonSelectionColor;
    }
    
    public Color getTextNonSelectionColor() {
        return this.textNonSelectionColor;
    }
    
    public void setBackgroundSelectionColor(final Color backgroundSelectionColor) {
        this.backgroundSelectionColor = backgroundSelectionColor;
    }
    
    public Color getBackgroundSelectionColor() {
        return this.backgroundSelectionColor;
    }
    
    public void setBackgroundNonSelectionColor(final Color backgroundNonSelectionColor) {
        this.backgroundNonSelectionColor = backgroundNonSelectionColor;
    }
    
    public Color getBackgroundNonSelectionColor() {
        return this.backgroundNonSelectionColor;
    }
    
    public void setBorderSelectionColor(final Color borderSelectionColor) {
        this.borderSelectionColor = borderSelectionColor;
    }
    
    public Color getBorderSelectionColor() {
        return this.borderSelectionColor;
    }
    
    @Override
    public void setFont(Font font) {
        if (font instanceof FontUIResource) {
            font = null;
        }
        super.setFont(font);
    }
    
    @Override
    public Font getFont() {
        Font font = super.getFont();
        if (font == null && this.tree != null) {
            font = this.tree.getFont();
        }
        return font;
    }
    
    @Override
    public void setBackground(Color background) {
        if (background instanceof ColorUIResource) {
            background = null;
        }
        super.setBackground(background);
    }
    
    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object o, final boolean selected, final boolean b, final boolean b2, final int n, final boolean hasFocus) {
        final String convertValueToText = tree.convertValueToText(o, selected, b, b2, n, hasFocus);
        this.tree = tree;
        this.hasFocus = hasFocus;
        this.setText(convertValueToText);
        this.isDropCell = false;
        final JTree.DropLocation dropLocation = tree.getDropLocation();
        Color foreground;
        if (dropLocation != null && dropLocation.getChildIndex() == -1 && tree.getRowForPath(dropLocation.getPath()) == n) {
            final Color color = DefaultLookup.getColor(this, this.ui, "Tree.dropCellForeground");
            if (color != null) {
                foreground = color;
            }
            else {
                foreground = this.getTextSelectionColor();
            }
            this.isDropCell = true;
        }
        else if (selected) {
            foreground = this.getTextSelectionColor();
        }
        else {
            foreground = this.getTextNonSelectionColor();
        }
        this.setForeground(foreground);
        Icon icon;
        if (b2) {
            icon = this.getLeafIcon();
        }
        else if (b) {
            icon = this.getOpenIcon();
        }
        else {
            icon = this.getClosedIcon();
        }
        if (!tree.isEnabled()) {
            this.setEnabled(false);
            final Icon disabledIcon = UIManager.getLookAndFeel().getDisabledIcon(tree, icon);
            if (disabledIcon != null) {
                icon = disabledIcon;
            }
            this.setDisabledIcon(icon);
        }
        else {
            this.setEnabled(true);
            this.setIcon(icon);
        }
        this.setComponentOrientation(tree.getComponentOrientation());
        this.selected = selected;
        return this;
    }
    
    @Override
    public void paint(final Graphics graphics) {
        Color color;
        if (this.isDropCell) {
            color = DefaultLookup.getColor(this, this.ui, "Tree.dropCellBackground");
            if (color == null) {
                color = this.getBackgroundSelectionColor();
            }
        }
        else if (this.selected) {
            color = this.getBackgroundSelectionColor();
        }
        else {
            color = this.getBackgroundNonSelectionColor();
            if (color == null) {
                color = this.getBackground();
            }
        }
        int n = -1;
        if (color != null && this.fillBackground) {
            n = this.getLabelStart();
            graphics.setColor(color);
            if (this.getComponentOrientation().isLeftToRight()) {
                graphics.fillRect(n, 0, this.getWidth() - n, this.getHeight());
            }
            else {
                graphics.fillRect(0, 0, this.getWidth() - n, this.getHeight());
            }
        }
        if (this.hasFocus) {
            if (this.drawsFocusBorderAroundIcon) {
                n = 0;
            }
            else if (n == -1) {
                n = this.getLabelStart();
            }
            if (this.getComponentOrientation().isLeftToRight()) {
                this.paintFocus(graphics, n, 0, this.getWidth() - n, this.getHeight(), color);
            }
            else {
                this.paintFocus(graphics, 0, 0, this.getWidth() - n, this.getHeight(), color);
            }
        }
        super.paint(graphics);
    }
    
    private void paintFocus(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final Color treeBGColor) {
        final Color borderSelectionColor = this.getBorderSelectionColor();
        if (borderSelectionColor != null && (this.selected || !this.drawDashedFocusIndicator)) {
            graphics.setColor(borderSelectionColor);
            graphics.drawRect(n, n2, n3 - 1, n4 - 1);
        }
        if (this.drawDashedFocusIndicator && treeBGColor != null) {
            if (this.treeBGColor != treeBGColor) {
                this.treeBGColor = treeBGColor;
                this.focusBGColor = new Color(~treeBGColor.getRGB());
            }
            graphics.setColor(this.focusBGColor);
            BasicGraphicsUtils.drawDashedRect(graphics, n, n2, n3, n4);
        }
    }
    
    private int getLabelStart() {
        final Icon icon = this.getIcon();
        if (icon != null && this.getText() != null) {
            return icon.getIconWidth() + Math.max(0, this.getIconTextGap() - 1);
        }
        return 0;
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension preferredSize = super.getPreferredSize();
        if (preferredSize != null) {
            preferredSize = new Dimension(preferredSize.width + 3, preferredSize.height);
        }
        return preferredSize;
    }
    
    @Override
    public void validate() {
    }
    
    @Override
    public void invalidate() {
    }
    
    @Override
    public void revalidate() {
    }
    
    @Override
    public void repaint(final long n, final int n2, final int n3, final int n4, final int n5) {
    }
    
    @Override
    public void repaint(final Rectangle rectangle) {
    }
    
    @Override
    public void repaint() {
    }
    
    @Override
    protected void firePropertyChange(final String s, final Object o, final Object o2) {
        if (s == "text" || ((s == "font" || s == "foreground") && o != o2 && this.getClientProperty("html") != null)) {
            super.firePropertyChange(s, o, o2);
        }
    }
    
    @Override
    public void firePropertyChange(final String s, final byte b, final byte b2) {
    }
    
    @Override
    public void firePropertyChange(final String s, final char c, final char c2) {
    }
    
    @Override
    public void firePropertyChange(final String s, final short n, final short n2) {
    }
    
    @Override
    public void firePropertyChange(final String s, final int n, final int n2) {
    }
    
    @Override
    public void firePropertyChange(final String s, final long n, final long n2) {
    }
    
    @Override
    public void firePropertyChange(final String s, final float n, final float n2) {
    }
    
    @Override
    public void firePropertyChange(final String s, final double n, final double n2) {
    }
    
    @Override
    public void firePropertyChange(final String s, final boolean b, final boolean b2) {
    }
}
