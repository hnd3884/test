package javax.swing.border;

import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Component;
import java.beans.ConstructorProperties;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;

public class TitledBorder extends AbstractBorder
{
    protected String title;
    protected Border border;
    protected int titlePosition;
    protected int titleJustification;
    protected Font titleFont;
    protected Color titleColor;
    private final JLabel label;
    public static final int DEFAULT_POSITION = 0;
    public static final int ABOVE_TOP = 1;
    public static final int TOP = 2;
    public static final int BELOW_TOP = 3;
    public static final int ABOVE_BOTTOM = 4;
    public static final int BOTTOM = 5;
    public static final int BELOW_BOTTOM = 6;
    public static final int DEFAULT_JUSTIFICATION = 0;
    public static final int LEFT = 1;
    public static final int CENTER = 2;
    public static final int RIGHT = 3;
    public static final int LEADING = 4;
    public static final int TRAILING = 5;
    protected static final int EDGE_SPACING = 2;
    protected static final int TEXT_SPACING = 2;
    protected static final int TEXT_INSET_H = 5;
    
    public TitledBorder(final String s) {
        this(null, s, 4, 0, null, null);
    }
    
    public TitledBorder(final Border border) {
        this(border, "", 4, 0, null, null);
    }
    
    public TitledBorder(final Border border, final String s) {
        this(border, s, 4, 0, null, null);
    }
    
    public TitledBorder(final Border border, final String s, final int n, final int n2) {
        this(border, s, n, n2, null, null);
    }
    
    public TitledBorder(final Border border, final String s, final int n, final int n2, final Font font) {
        this(border, s, n, n2, font, null);
    }
    
    @ConstructorProperties({ "border", "title", "titleJustification", "titlePosition", "titleFont", "titleColor" })
    public TitledBorder(final Border border, final String title, final int titleJustification, final int titlePosition, final Font titleFont, final Color titleColor) {
        this.title = title;
        this.border = border;
        this.titleFont = titleFont;
        this.titleColor = titleColor;
        this.setTitleJustification(titleJustification);
        this.setTitlePosition(titlePosition);
        (this.label = new JLabel()).setOpaque(false);
        this.label.putClientProperty("html", null);
    }
    
    @Override
    public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        final Border border = this.getBorder();
        final String title = this.getTitle();
        if (title != null && !title.isEmpty()) {
            final int n5 = (border instanceof TitledBorder) ? 0 : 2;
            final JLabel label = this.getLabel(component);
            final Dimension preferredSize = label.getPreferredSize();
            final Insets borderInsets = getBorderInsets(border, component, new Insets(0, 0, 0, 0));
            final int n6 = n + n5;
            int n7 = n2 + n5;
            final int n8 = n3 - n5 - n5;
            int n9 = n4 - n5 - n5;
            int n10 = n2;
            final int height = preferredSize.height;
            final int position = this.getPosition();
            switch (position) {
                case 1: {
                    borderInsets.left = 0;
                    borderInsets.right = 0;
                    n7 += height - n5;
                    n9 -= height - n5;
                    break;
                }
                case 2: {
                    borderInsets.top = n5 + borderInsets.top / 2 - height / 2;
                    if (borderInsets.top < n5) {
                        n7 -= borderInsets.top;
                        n9 += borderInsets.top;
                        break;
                    }
                    n10 += borderInsets.top;
                    break;
                }
                case 3: {
                    n10 += borderInsets.top + n5;
                    break;
                }
                case 4: {
                    n10 += n4 - height - borderInsets.bottom - n5;
                    break;
                }
                case 5: {
                    n10 += n4 - height;
                    borderInsets.bottom = n5 + (borderInsets.bottom - height) / 2;
                    if (borderInsets.bottom < n5) {
                        n9 += borderInsets.bottom;
                        break;
                    }
                    n10 -= borderInsets.bottom;
                    break;
                }
                case 6: {
                    borderInsets.left = 0;
                    borderInsets.right = 0;
                    n10 += n4 - height;
                    n9 -= height - n5;
                    break;
                }
            }
            final Insets insets = borderInsets;
            insets.left += n5 + 5;
            final Insets insets2 = borderInsets;
            insets2.right += n5 + 5;
            int n11 = n;
            int width = n3 - borderInsets.left - borderInsets.right;
            if (width > preferredSize.width) {
                width = preferredSize.width;
            }
            switch (this.getJustification(component)) {
                case 1: {
                    n11 += borderInsets.left;
                    break;
                }
                case 3: {
                    n11 += n3 - borderInsets.right - width;
                    break;
                }
                case 2: {
                    n11 += (n3 - width) / 2;
                    break;
                }
            }
            if (border != null) {
                if (position != 2 && position != 5) {
                    border.paintBorder(component, graphics, n6, n7, n8, n9);
                }
                else {
                    final Graphics create = graphics.create();
                    if (create instanceof Graphics2D) {
                        final Graphics2D graphics2D = (Graphics2D)create;
                        final Path2D.Float float1 = new Path2D.Float();
                        float1.append(new Rectangle(n6, n7, n8, n10 - n7), false);
                        float1.append(new Rectangle(n6, n10, n11 - n6 - 2, height), false);
                        float1.append(new Rectangle(n11 + width + 2, n10, n6 - n11 + n8 - width - 2, height), false);
                        float1.append(new Rectangle(n6, n10 + height, n8, n7 - n10 + n9 - height), false);
                        graphics2D.clip(float1);
                    }
                    border.paintBorder(component, create, n6, n7, n8, n9);
                    create.dispose();
                }
            }
            graphics.translate(n11, n10);
            label.setSize(width, height);
            label.paint(graphics);
            graphics.translate(-n11, -n10);
        }
        else if (border != null) {
            border.paintBorder(component, graphics, n, n2, n3, n4);
        }
    }
    
    @Override
    public Insets getBorderInsets(final Component component, Insets borderInsets) {
        final Border border = this.getBorder();
        borderInsets = getBorderInsets(border, component, borderInsets);
        final String title = this.getTitle();
        if (title != null && !title.isEmpty()) {
            final int n = (border instanceof TitledBorder) ? 0 : 2;
            final Dimension preferredSize = this.getLabel(component).getPreferredSize();
            switch (this.getPosition()) {
                case 1: {
                    final Insets insets = borderInsets;
                    insets.top += preferredSize.height - n;
                    break;
                }
                case 2: {
                    if (borderInsets.top < preferredSize.height) {
                        borderInsets.top = preferredSize.height - n;
                        break;
                    }
                    break;
                }
                case 3: {
                    final Insets insets2 = borderInsets;
                    insets2.top += preferredSize.height;
                    break;
                }
                case 4: {
                    final Insets insets3 = borderInsets;
                    insets3.bottom += preferredSize.height;
                    break;
                }
                case 5: {
                    if (borderInsets.bottom < preferredSize.height) {
                        borderInsets.bottom = preferredSize.height - n;
                        break;
                    }
                    break;
                }
                case 6: {
                    final Insets insets4 = borderInsets;
                    insets4.bottom += preferredSize.height - n;
                    break;
                }
            }
            final Insets insets5 = borderInsets;
            insets5.top += n + 2;
            final Insets insets6 = borderInsets;
            insets6.left += n + 2;
            final Insets insets7 = borderInsets;
            insets7.right += n + 2;
            final Insets insets8 = borderInsets;
            insets8.bottom += n + 2;
        }
        return borderInsets;
    }
    
    @Override
    public boolean isBorderOpaque() {
        return false;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public Border getBorder() {
        return (this.border != null) ? this.border : UIManager.getBorder("TitledBorder.border");
    }
    
    public int getTitlePosition() {
        return this.titlePosition;
    }
    
    public int getTitleJustification() {
        return this.titleJustification;
    }
    
    public Font getTitleFont() {
        return (this.titleFont == null) ? UIManager.getFont("TitledBorder.font") : this.titleFont;
    }
    
    public Color getTitleColor() {
        return (this.titleColor == null) ? UIManager.getColor("TitledBorder.titleColor") : this.titleColor;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public void setBorder(final Border border) {
        this.border = border;
    }
    
    public void setTitlePosition(final int titlePosition) {
        switch (titlePosition) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6: {
                this.titlePosition = titlePosition;
                return;
            }
            default: {
                throw new IllegalArgumentException(titlePosition + " is not a valid title position.");
            }
        }
    }
    
    public void setTitleJustification(final int titleJustification) {
        switch (titleJustification) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5: {
                this.titleJustification = titleJustification;
                return;
            }
            default: {
                throw new IllegalArgumentException(titleJustification + " is not a valid title justification.");
            }
        }
    }
    
    public void setTitleFont(final Font titleFont) {
        this.titleFont = titleFont;
    }
    
    public void setTitleColor(final Color titleColor) {
        this.titleColor = titleColor;
    }
    
    public Dimension getMinimumSize(final Component component) {
        final Insets borderInsets = this.getBorderInsets(component);
        final Dimension dimension = new Dimension(borderInsets.right + borderInsets.left, borderInsets.top + borderInsets.bottom);
        final String title = this.getTitle();
        if (title != null && !title.isEmpty()) {
            final Dimension preferredSize = this.getLabel(component).getPreferredSize();
            final int position = this.getPosition();
            if (position != 1 && position != 6) {
                final Dimension dimension2 = dimension;
                dimension2.width += preferredSize.width;
            }
            else if (dimension.width < preferredSize.width) {
                final Dimension dimension3 = dimension;
                dimension3.width += preferredSize.width;
            }
        }
        return dimension;
    }
    
    @Override
    public int getBaseline(final Component component, final int n, final int n2) {
        if (component == null) {
            throw new NullPointerException("Must supply non-null component");
        }
        if (n < 0) {
            throw new IllegalArgumentException("Width must be >= 0");
        }
        if (n2 < 0) {
            throw new IllegalArgumentException("Height must be >= 0");
        }
        final Border border = this.getBorder();
        final String title = this.getTitle();
        if (title != null && !title.isEmpty()) {
            final int n3 = (border instanceof TitledBorder) ? 0 : 2;
            final JLabel label = this.getLabel(component);
            final Dimension preferredSize = label.getPreferredSize();
            final Insets borderInsets = getBorderInsets(border, component, new Insets(0, 0, 0, 0));
            final int baseline = label.getBaseline(preferredSize.width, preferredSize.height);
            switch (this.getPosition()) {
                case 1: {
                    return baseline;
                }
                case 2: {
                    borderInsets.top = n3 + (borderInsets.top - preferredSize.height) / 2;
                    return (borderInsets.top < n3) ? baseline : (baseline + borderInsets.top);
                }
                case 3: {
                    return baseline + borderInsets.top + n3;
                }
                case 4: {
                    return baseline + n2 - preferredSize.height - borderInsets.bottom - n3;
                }
                case 5: {
                    borderInsets.bottom = n3 + (borderInsets.bottom - preferredSize.height) / 2;
                    return (borderInsets.bottom < n3) ? (baseline + n2 - preferredSize.height) : (baseline + n2 - preferredSize.height + borderInsets.bottom);
                }
                case 6: {
                    return baseline + n2 - preferredSize.height;
                }
            }
        }
        return -1;
    }
    
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final Component component) {
        super.getBaselineResizeBehavior(component);
        switch (this.getPosition()) {
            case 1:
            case 2:
            case 3: {
                return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
            }
            case 4:
            case 5:
            case 6: {
                return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
            }
            default: {
                return Component.BaselineResizeBehavior.OTHER;
            }
        }
    }
    
    private int getPosition() {
        final int titlePosition = this.getTitlePosition();
        if (titlePosition != 0) {
            return titlePosition;
        }
        final Object value = UIManager.get("TitledBorder.position");
        if (value instanceof Integer) {
            final int intValue = (int)value;
            if (0 < intValue && intValue <= 6) {
                return intValue;
            }
        }
        else if (value instanceof String) {
            final String s = (String)value;
            if (s.equalsIgnoreCase("ABOVE_TOP")) {
                return 1;
            }
            if (s.equalsIgnoreCase("TOP")) {
                return 2;
            }
            if (s.equalsIgnoreCase("BELOW_TOP")) {
                return 3;
            }
            if (s.equalsIgnoreCase("ABOVE_BOTTOM")) {
                return 4;
            }
            if (s.equalsIgnoreCase("BOTTOM")) {
                return 5;
            }
            if (s.equalsIgnoreCase("BELOW_BOTTOM")) {
                return 6;
            }
        }
        return 2;
    }
    
    private int getJustification(final Component component) {
        final int titleJustification = this.getTitleJustification();
        if (titleJustification == 4 || titleJustification == 0) {
            return component.getComponentOrientation().isLeftToRight() ? 1 : 3;
        }
        if (titleJustification == 5) {
            return component.getComponentOrientation().isLeftToRight() ? 3 : 1;
        }
        return titleJustification;
    }
    
    protected Font getFont(final Component component) {
        final Font titleFont = this.getTitleFont();
        if (titleFont != null) {
            return titleFont;
        }
        if (component != null) {
            final Font font = component.getFont();
            if (font != null) {
                return font;
            }
        }
        return new Font("Dialog", 0, 12);
    }
    
    private Color getColor(final Component component) {
        final Color titleColor = this.getTitleColor();
        if (titleColor != null) {
            return titleColor;
        }
        return (component != null) ? component.getForeground() : null;
    }
    
    private JLabel getLabel(final Component component) {
        this.label.setText(this.getTitle());
        this.label.setFont(this.getFont(component));
        this.label.setForeground(this.getColor(component));
        this.label.setComponentOrientation(component.getComponentOrientation());
        this.label.setEnabled(component.isEnabled());
        return this.label;
    }
    
    private static Insets getBorderInsets(final Border border, final Component component, Insets borderInsets) {
        if (border == null) {
            borderInsets.set(0, 0, 0, 0);
        }
        else if (border instanceof AbstractBorder) {
            borderInsets = ((AbstractBorder)border).getBorderInsets(component, borderInsets);
        }
        else {
            final Insets borderInsets2 = border.getBorderInsets(component);
            borderInsets.set(borderInsets2.top, borderInsets2.left, borderInsets2.bottom, borderInsets2.right);
        }
        return borderInsets;
    }
}
