package javax.swing;

import javax.swing.plaf.UIResource;
import java.awt.Rectangle;
import javax.swing.border.Border;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Component;
import java.io.Serializable;
import java.awt.LayoutManager;

public class ScrollPaneLayout implements LayoutManager, ScrollPaneConstants, Serializable
{
    protected JViewport viewport;
    protected JScrollBar vsb;
    protected JScrollBar hsb;
    protected JViewport rowHead;
    protected JViewport colHead;
    protected Component lowerLeft;
    protected Component lowerRight;
    protected Component upperLeft;
    protected Component upperRight;
    protected int vsbPolicy;
    protected int hsbPolicy;
    
    public ScrollPaneLayout() {
        this.vsbPolicy = 20;
        this.hsbPolicy = 30;
    }
    
    public void syncWithScrollPane(final JScrollPane scrollPane) {
        this.viewport = scrollPane.getViewport();
        this.vsb = scrollPane.getVerticalScrollBar();
        this.hsb = scrollPane.getHorizontalScrollBar();
        this.rowHead = scrollPane.getRowHeader();
        this.colHead = scrollPane.getColumnHeader();
        this.lowerLeft = scrollPane.getCorner("LOWER_LEFT_CORNER");
        this.lowerRight = scrollPane.getCorner("LOWER_RIGHT_CORNER");
        this.upperLeft = scrollPane.getCorner("UPPER_LEFT_CORNER");
        this.upperRight = scrollPane.getCorner("UPPER_RIGHT_CORNER");
        this.vsbPolicy = scrollPane.getVerticalScrollBarPolicy();
        this.hsbPolicy = scrollPane.getHorizontalScrollBarPolicy();
    }
    
    protected Component addSingletonComponent(final Component component, final Component component2) {
        if (component != null && component != component2) {
            component.getParent().remove(component);
        }
        return component2;
    }
    
    @Override
    public void addLayoutComponent(final String s, final Component component) {
        if (s.equals("VIEWPORT")) {
            this.viewport = (JViewport)this.addSingletonComponent(this.viewport, component);
        }
        else if (s.equals("VERTICAL_SCROLLBAR")) {
            this.vsb = (JScrollBar)this.addSingletonComponent(this.vsb, component);
        }
        else if (s.equals("HORIZONTAL_SCROLLBAR")) {
            this.hsb = (JScrollBar)this.addSingletonComponent(this.hsb, component);
        }
        else if (s.equals("ROW_HEADER")) {
            this.rowHead = (JViewport)this.addSingletonComponent(this.rowHead, component);
        }
        else if (s.equals("COLUMN_HEADER")) {
            this.colHead = (JViewport)this.addSingletonComponent(this.colHead, component);
        }
        else if (s.equals("LOWER_LEFT_CORNER")) {
            this.lowerLeft = this.addSingletonComponent(this.lowerLeft, component);
        }
        else if (s.equals("LOWER_RIGHT_CORNER")) {
            this.lowerRight = this.addSingletonComponent(this.lowerRight, component);
        }
        else if (s.equals("UPPER_LEFT_CORNER")) {
            this.upperLeft = this.addSingletonComponent(this.upperLeft, component);
        }
        else {
            if (!s.equals("UPPER_RIGHT_CORNER")) {
                throw new IllegalArgumentException("invalid layout key " + s);
            }
            this.upperRight = this.addSingletonComponent(this.upperRight, component);
        }
    }
    
    @Override
    public void removeLayoutComponent(final Component component) {
        if (component == this.viewport) {
            this.viewport = null;
        }
        else if (component == this.vsb) {
            this.vsb = null;
        }
        else if (component == this.hsb) {
            this.hsb = null;
        }
        else if (component == this.rowHead) {
            this.rowHead = null;
        }
        else if (component == this.colHead) {
            this.colHead = null;
        }
        else if (component == this.lowerLeft) {
            this.lowerLeft = null;
        }
        else if (component == this.lowerRight) {
            this.lowerRight = null;
        }
        else if (component == this.upperLeft) {
            this.upperLeft = null;
        }
        else if (component == this.upperRight) {
            this.upperRight = null;
        }
    }
    
    public int getVerticalScrollBarPolicy() {
        return this.vsbPolicy;
    }
    
    public void setVerticalScrollBarPolicy(final int vsbPolicy) {
        switch (vsbPolicy) {
            case 20:
            case 21:
            case 22: {
                this.vsbPolicy = vsbPolicy;
                return;
            }
            default: {
                throw new IllegalArgumentException("invalid verticalScrollBarPolicy");
            }
        }
    }
    
    public int getHorizontalScrollBarPolicy() {
        return this.hsbPolicy;
    }
    
    public void setHorizontalScrollBarPolicy(final int hsbPolicy) {
        switch (hsbPolicy) {
            case 30:
            case 31:
            case 32: {
                this.hsbPolicy = hsbPolicy;
                return;
            }
            default: {
                throw new IllegalArgumentException("invalid horizontalScrollBarPolicy");
            }
        }
    }
    
    public JViewport getViewport() {
        return this.viewport;
    }
    
    public JScrollBar getHorizontalScrollBar() {
        return this.hsb;
    }
    
    public JScrollBar getVerticalScrollBar() {
        return this.vsb;
    }
    
    public JViewport getRowHeader() {
        return this.rowHead;
    }
    
    public JViewport getColumnHeader() {
        return this.colHead;
    }
    
    public Component getCorner(final String s) {
        if (s.equals("LOWER_LEFT_CORNER")) {
            return this.lowerLeft;
        }
        if (s.equals("LOWER_RIGHT_CORNER")) {
            return this.lowerRight;
        }
        if (s.equals("UPPER_LEFT_CORNER")) {
            return this.upperLeft;
        }
        if (s.equals("UPPER_RIGHT_CORNER")) {
            return this.upperRight;
        }
        return null;
    }
    
    @Override
    public Dimension preferredLayoutSize(final Container container) {
        final JScrollPane scrollPane = (JScrollPane)container;
        this.vsbPolicy = scrollPane.getVerticalScrollBarPolicy();
        this.hsbPolicy = scrollPane.getHorizontalScrollBarPolicy();
        final Insets insets = container.getInsets();
        int n = insets.left + insets.right;
        int n2 = insets.top + insets.bottom;
        Dimension preferredSize = null;
        Dimension preferredSize2 = null;
        Object view = null;
        if (this.viewport != null) {
            preferredSize = this.viewport.getPreferredSize();
            view = this.viewport.getView();
            if (view != null) {
                preferredSize2 = ((Component)view).getPreferredSize();
            }
            else {
                preferredSize2 = new Dimension(0, 0);
            }
        }
        if (preferredSize != null) {
            n += preferredSize.width;
            n2 += preferredSize.height;
        }
        final Border viewportBorder = scrollPane.getViewportBorder();
        if (viewportBorder != null) {
            final Insets borderInsets = viewportBorder.getBorderInsets(container);
            n += borderInsets.left + borderInsets.right;
            n2 += borderInsets.top + borderInsets.bottom;
        }
        if (this.rowHead != null && this.rowHead.isVisible()) {
            n += this.rowHead.getPreferredSize().width;
        }
        if (this.colHead != null && this.colHead.isVisible()) {
            n2 += this.colHead.getPreferredSize().height;
        }
        if (this.vsb != null && this.vsbPolicy != 21) {
            if (this.vsbPolicy == 22) {
                n += this.vsb.getPreferredSize().width;
            }
            else if (preferredSize2 != null && preferredSize != null) {
                int n3 = 1;
                if (view instanceof Scrollable) {
                    n3 = (((Scrollable)view).getScrollableTracksViewportHeight() ? 0 : 1);
                }
                if (n3 != 0 && preferredSize2.height > preferredSize.height) {
                    n += this.vsb.getPreferredSize().width;
                }
            }
        }
        if (this.hsb != null && this.hsbPolicy != 31) {
            if (this.hsbPolicy == 32) {
                n2 += this.hsb.getPreferredSize().height;
            }
            else if (preferredSize2 != null && preferredSize != null) {
                int n4 = 1;
                if (view instanceof Scrollable) {
                    n4 = (((Scrollable)view).getScrollableTracksViewportWidth() ? 0 : 1);
                }
                if (n4 != 0 && preferredSize2.width > preferredSize.width) {
                    n2 += this.hsb.getPreferredSize().height;
                }
            }
        }
        return new Dimension(n, n2);
    }
    
    @Override
    public Dimension minimumLayoutSize(final Container container) {
        final JScrollPane scrollPane = (JScrollPane)container;
        this.vsbPolicy = scrollPane.getVerticalScrollBarPolicy();
        this.hsbPolicy = scrollPane.getHorizontalScrollBarPolicy();
        final Insets insets = container.getInsets();
        int n = insets.left + insets.right;
        int n2 = insets.top + insets.bottom;
        if (this.viewport != null) {
            final Dimension minimumSize = this.viewport.getMinimumSize();
            n += minimumSize.width;
            n2 += minimumSize.height;
        }
        final Border viewportBorder = scrollPane.getViewportBorder();
        if (viewportBorder != null) {
            final Insets borderInsets = viewportBorder.getBorderInsets(container);
            n += borderInsets.left + borderInsets.right;
            n2 += borderInsets.top + borderInsets.bottom;
        }
        if (this.rowHead != null && this.rowHead.isVisible()) {
            final Dimension minimumSize2 = this.rowHead.getMinimumSize();
            n += minimumSize2.width;
            n2 = Math.max(n2, minimumSize2.height);
        }
        if (this.colHead != null && this.colHead.isVisible()) {
            final Dimension minimumSize3 = this.colHead.getMinimumSize();
            n = Math.max(n, minimumSize3.width);
            n2 += minimumSize3.height;
        }
        if (this.vsb != null && this.vsbPolicy != 21) {
            final Dimension minimumSize4 = this.vsb.getMinimumSize();
            n += minimumSize4.width;
            n2 = Math.max(n2, minimumSize4.height);
        }
        if (this.hsb != null && this.hsbPolicy != 31) {
            final Dimension minimumSize5 = this.hsb.getMinimumSize();
            n = Math.max(n, minimumSize5.width);
            n2 += minimumSize5.height;
        }
        return new Dimension(n, n2);
    }
    
    @Override
    public void layoutContainer(final Container container) {
        final JScrollPane scrollPane = (JScrollPane)container;
        this.vsbPolicy = scrollPane.getVerticalScrollBarPolicy();
        this.hsbPolicy = scrollPane.getHorizontalScrollBarPolicy();
        final Rectangle bounds;
        final Rectangle rectangle2;
        final Rectangle rectangle = rectangle2 = (bounds = scrollPane.getBounds());
        final int n = 0;
        rectangle2.y = n;
        bounds.x = n;
        final Insets insets = container.getInsets();
        rectangle.x = insets.left;
        rectangle.y = insets.top;
        final Rectangle rectangle3 = rectangle;
        rectangle3.width -= insets.left + insets.right;
        final Rectangle rectangle4 = rectangle;
        rectangle4.height -= insets.top + insets.bottom;
        final boolean leftToRight = SwingUtilities.isLeftToRight(scrollPane);
        final Rectangle bounds2 = new Rectangle(0, rectangle.y, 0, 0);
        if (this.colHead != null && this.colHead.isVisible()) {
            final int min = Math.min(rectangle.height, this.colHead.getPreferredSize().height);
            bounds2.height = min;
            final Rectangle rectangle5 = rectangle;
            rectangle5.y += min;
            final Rectangle rectangle6 = rectangle;
            rectangle6.height -= min;
        }
        final Rectangle bounds3 = new Rectangle(0, 0, 0, 0);
        if (this.rowHead != null && this.rowHead.isVisible()) {
            final int min2 = Math.min(rectangle.width, this.rowHead.getPreferredSize().width);
            bounds3.width = min2;
            final Rectangle rectangle7 = rectangle;
            rectangle7.width -= min2;
            if (leftToRight) {
                bounds3.x = rectangle.x;
                final Rectangle rectangle8 = rectangle;
                rectangle8.x += min2;
            }
            else {
                bounds3.x = rectangle.x + rectangle.width;
            }
        }
        final Border viewportBorder = scrollPane.getViewportBorder();
        Insets borderInsets;
        if (viewportBorder != null) {
            borderInsets = viewportBorder.getBorderInsets(container);
            final Rectangle rectangle9 = rectangle;
            rectangle9.x += borderInsets.left;
            final Rectangle rectangle10 = rectangle;
            rectangle10.y += borderInsets.top;
            final Rectangle rectangle11 = rectangle;
            rectangle11.width -= borderInsets.left + borderInsets.right;
            final Rectangle rectangle12 = rectangle;
            rectangle12.height -= borderInsets.top + borderInsets.bottom;
        }
        else {
            borderInsets = new Insets(0, 0, 0, 0);
        }
        final Component component = (this.viewport != null) ? this.viewport.getView() : null;
        final Dimension dimension = (component != null) ? component.getPreferredSize() : new Dimension(0, 0);
        Dimension viewCoordinates = (this.viewport != null) ? this.viewport.toViewCoordinates(rectangle.getSize()) : new Dimension(0, 0);
        boolean scrollableTracksViewportWidth = false;
        boolean scrollableTracksViewportHeight = false;
        final boolean b = rectangle.width < 0 || rectangle.height < 0;
        Scrollable scrollable;
        if (!b && component instanceof Scrollable) {
            scrollable = (Scrollable)component;
            scrollableTracksViewportWidth = scrollable.getScrollableTracksViewportWidth();
            scrollableTracksViewportHeight = scrollable.getScrollableTracksViewportHeight();
        }
        else {
            scrollable = null;
        }
        final Rectangle bounds4 = new Rectangle(0, rectangle.y - borderInsets.top, 0, 0);
        boolean b2 = !b && (this.vsbPolicy == 22 || (this.vsbPolicy != 21 && !scrollableTracksViewportHeight && dimension.height > viewCoordinates.height));
        if (this.vsb != null && b2) {
            this.adjustForVSB(true, rectangle, bounds4, borderInsets, leftToRight);
            viewCoordinates = this.viewport.toViewCoordinates(rectangle.getSize());
        }
        final Rectangle bounds5 = new Rectangle(rectangle.x - borderInsets.left, 0, 0, 0);
        boolean b3 = !b && (this.hsbPolicy == 32 || (this.hsbPolicy != 31 && !scrollableTracksViewportWidth && dimension.width > viewCoordinates.width));
        if (this.hsb != null && b3) {
            this.adjustForHSB(true, rectangle, bounds5, borderInsets);
            if (this.vsb != null && !b2 && this.vsbPolicy != 21) {
                b2 = (dimension.height > this.viewport.toViewCoordinates(rectangle.getSize()).height);
                if (b2) {
                    this.adjustForVSB(true, rectangle, bounds4, borderInsets, leftToRight);
                }
            }
        }
        if (this.viewport != null) {
            this.viewport.setBounds(rectangle);
            if (scrollable != null) {
                Dimension dimension2 = this.viewport.toViewCoordinates(rectangle.getSize());
                final boolean b4 = b3;
                final boolean b5 = b2;
                final boolean scrollableTracksViewportWidth2 = scrollable.getScrollableTracksViewportWidth();
                final boolean scrollableTracksViewportHeight2 = scrollable.getScrollableTracksViewportHeight();
                if (this.vsb != null && this.vsbPolicy == 20) {
                    final boolean b6 = !scrollableTracksViewportHeight2 && dimension.height > dimension2.height;
                    if (b6 != b2) {
                        b2 = b6;
                        this.adjustForVSB(b2, rectangle, bounds4, borderInsets, leftToRight);
                        dimension2 = this.viewport.toViewCoordinates(rectangle.getSize());
                    }
                }
                if (this.hsb != null && this.hsbPolicy == 30) {
                    final boolean b7 = !scrollableTracksViewportWidth2 && dimension.width > dimension2.width;
                    if (b7 != b3) {
                        b3 = b7;
                        this.adjustForHSB(b3, rectangle, bounds5, borderInsets);
                        if (this.vsb != null && !b2 && this.vsbPolicy != 21) {
                            b2 = (dimension.height > this.viewport.toViewCoordinates(rectangle.getSize()).height);
                            if (b2) {
                                this.adjustForVSB(true, rectangle, bounds4, borderInsets, leftToRight);
                            }
                        }
                    }
                }
                if (b4 != b3 || b5 != b2) {
                    this.viewport.setBounds(rectangle);
                }
            }
        }
        bounds4.height = rectangle.height + borderInsets.top + borderInsets.bottom;
        bounds5.width = rectangle.width + borderInsets.left + borderInsets.right;
        bounds3.height = rectangle.height + borderInsets.top + borderInsets.bottom;
        bounds3.y = rectangle.y - borderInsets.top;
        bounds2.width = rectangle.width + borderInsets.left + borderInsets.right;
        bounds2.x = rectangle.x - borderInsets.left;
        if (this.rowHead != null) {
            this.rowHead.setBounds(bounds3);
        }
        if (this.colHead != null) {
            this.colHead.setBounds(bounds2);
        }
        if (this.vsb != null) {
            if (b2) {
                if (this.colHead != null && UIManager.getBoolean("ScrollPane.fillUpperCorner") && ((leftToRight && this.upperRight == null) || (!leftToRight && this.upperLeft == null))) {
                    bounds4.y = bounds2.y;
                    final Rectangle rectangle13 = bounds4;
                    rectangle13.height += bounds2.height;
                }
                this.vsb.setVisible(true);
                this.vsb.setBounds(bounds4);
            }
            else {
                this.vsb.setVisible(false);
            }
        }
        if (this.hsb != null) {
            if (b3) {
                if (this.rowHead != null && UIManager.getBoolean("ScrollPane.fillLowerCorner") && ((leftToRight && this.lowerLeft == null) || (!leftToRight && this.lowerRight == null))) {
                    if (leftToRight) {
                        bounds5.x = bounds3.x;
                    }
                    final Rectangle rectangle14 = bounds5;
                    rectangle14.width += bounds3.width;
                }
                this.hsb.setVisible(true);
                this.hsb.setBounds(bounds5);
            }
            else {
                this.hsb.setVisible(false);
            }
        }
        if (this.lowerLeft != null) {
            this.lowerLeft.setBounds(leftToRight ? bounds3.x : bounds4.x, bounds5.y, leftToRight ? bounds3.width : bounds4.width, bounds5.height);
        }
        if (this.lowerRight != null) {
            this.lowerRight.setBounds(leftToRight ? bounds4.x : bounds3.x, bounds5.y, leftToRight ? bounds4.width : bounds3.width, bounds5.height);
        }
        if (this.upperLeft != null) {
            this.upperLeft.setBounds(leftToRight ? bounds3.x : bounds4.x, bounds2.y, leftToRight ? bounds3.width : bounds4.width, bounds2.height);
        }
        if (this.upperRight != null) {
            this.upperRight.setBounds(leftToRight ? bounds4.x : bounds3.x, bounds2.y, leftToRight ? bounds4.width : bounds3.width, bounds2.height);
        }
    }
    
    private void adjustForVSB(final boolean b, final Rectangle rectangle, final Rectangle rectangle2, final Insets insets, final boolean b2) {
        final int width = rectangle2.width;
        if (b) {
            final int max = Math.max(0, Math.min(this.vsb.getPreferredSize().width, rectangle.width));
            rectangle.width -= max;
            rectangle2.width = max;
            if (b2) {
                rectangle2.x = rectangle.x + rectangle.width + insets.right;
            }
            else {
                rectangle2.x = rectangle.x - insets.left;
                rectangle.x += max;
            }
        }
        else {
            rectangle.width += width;
        }
    }
    
    private void adjustForHSB(final boolean b, final Rectangle rectangle, final Rectangle rectangle2, final Insets insets) {
        final int height = rectangle2.height;
        if (b) {
            final int max = Math.max(0, Math.min(rectangle.height, this.hsb.getPreferredSize().height));
            rectangle.height -= max;
            rectangle2.y = rectangle.y + rectangle.height + insets.bottom;
            rectangle2.height = max;
        }
        else {
            rectangle.height += height;
        }
    }
    
    @Deprecated
    public Rectangle getViewportBorderBounds(final JScrollPane scrollPane) {
        return scrollPane.getViewportBorderBounds();
    }
    
    public static class UIResource extends ScrollPaneLayout implements javax.swing.plaf.UIResource
    {
    }
}
