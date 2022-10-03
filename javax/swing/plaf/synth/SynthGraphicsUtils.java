package javax.swing.plaf.synth;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonModel;
import java.awt.Component;
import sun.swing.MenuItemLayoutHelper;
import javax.swing.JMenuItem;
import java.awt.Color;
import java.awt.Shape;
import javax.swing.JComponent;
import javax.swing.text.View;
import java.awt.Dimension;
import sun.swing.SwingUtilities2;
import java.awt.Font;
import javax.swing.SwingUtilities;
import sun.swing.plaf.synth.SynthIcon;
import javax.swing.Icon;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

public class SynthGraphicsUtils
{
    private Rectangle paintIconR;
    private Rectangle paintTextR;
    private Rectangle paintViewR;
    private Insets paintInsets;
    private Rectangle iconR;
    private Rectangle textR;
    private Rectangle viewR;
    private Insets viewSizingInsets;
    
    public SynthGraphicsUtils() {
        this.paintIconR = new Rectangle();
        this.paintTextR = new Rectangle();
        this.paintViewR = new Rectangle();
        this.paintInsets = new Insets(0, 0, 0, 0);
        this.iconR = new Rectangle();
        this.textR = new Rectangle();
        this.viewR = new Rectangle();
        this.viewSizingInsets = new Insets(0, 0, 0, 0);
    }
    
    public void drawLine(final SynthContext synthContext, final Object o, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        graphics.drawLine(n, n2, n3, n4);
    }
    
    public void drawLine(final SynthContext synthContext, final Object o, final Graphics graphics, int n, int n2, final int n3, final int n4, final Object o2) {
        if ("dashed".equals(o2)) {
            if (n == n3) {
                int i;
                for (n2 = (i = n2 + n2 % 2); i <= n4; i += 2) {
                    graphics.drawLine(n, i, n3, i);
                }
            }
            else if (n2 == n4) {
                int j;
                for (n = (j = n + n % 2); j <= n3; j += 2) {
                    graphics.drawLine(j, n2, j, n4);
                }
            }
        }
        else {
            this.drawLine(synthContext, o, graphics, n, n2, n3, n4);
        }
    }
    
    public String layoutText(final SynthContext synthContext, final FontMetrics fontMetrics, final String s, final Icon icon, final int n, final int n2, final int n3, final int n4, final Rectangle rectangle, final Rectangle rectangle2, final Rectangle rectangle3, final int n5) {
        if (icon instanceof SynthIcon) {
            final SynthIconWrapper value = SynthIconWrapper.get((SynthIcon)icon, synthContext);
            final String layoutCompoundLabel = SwingUtilities.layoutCompoundLabel(synthContext.getComponent(), fontMetrics, s, value, n2, n, n4, n3, rectangle, rectangle2, rectangle3, n5);
            SynthIconWrapper.release(value);
            return layoutCompoundLabel;
        }
        return SwingUtilities.layoutCompoundLabel(synthContext.getComponent(), fontMetrics, s, icon, n2, n, n4, n3, rectangle, rectangle2, rectangle3, n5);
    }
    
    public int computeStringWidth(final SynthContext synthContext, final Font font, final FontMetrics fontMetrics, final String s) {
        return SwingUtilities2.stringWidth(synthContext.getComponent(), fontMetrics, s);
    }
    
    public Dimension getMinimumSize(final SynthContext synthContext, final Font font, final String s, final Icon icon, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final JComponent component = synthContext.getComponent();
        final Dimension preferredSize = this.getPreferredSize(synthContext, font, s, icon, n, n2, n3, n4, n5, n6);
        final View view = (View)component.getClientProperty("html");
        if (view != null) {
            final Dimension dimension = preferredSize;
            dimension.width -= (int)(view.getPreferredSpan(0) - view.getMinimumSpan(0));
        }
        return preferredSize;
    }
    
    public Dimension getMaximumSize(final SynthContext synthContext, final Font font, final String s, final Icon icon, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final JComponent component = synthContext.getComponent();
        final Dimension preferredSize = this.getPreferredSize(synthContext, font, s, icon, n, n2, n3, n4, n5, n6);
        final View view = (View)component.getClientProperty("html");
        if (view != null) {
            final Dimension dimension = preferredSize;
            dimension.width += (int)(view.getMaximumSpan(0) - view.getPreferredSpan(0));
        }
        return preferredSize;
    }
    
    public int getMaximumCharHeight(final SynthContext synthContext) {
        final FontMetrics fontMetrics = synthContext.getComponent().getFontMetrics(synthContext.getStyle().getFont(synthContext));
        return fontMetrics.getAscent() + fontMetrics.getDescent();
    }
    
    public Dimension getPreferredSize(final SynthContext synthContext, final Font font, final String s, final Icon icon, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final JComponent component = synthContext.getComponent();
        final Insets insets = component.getInsets(this.viewSizingInsets);
        final int x = insets.left + insets.right;
        final int y = insets.top + insets.bottom;
        if (icon == null && (s == null || font == null)) {
            return new Dimension(x, y);
        }
        if (s == null || (icon != null && font == null)) {
            return new Dimension(SynthIcon.getIconWidth(icon, synthContext) + x, SynthIcon.getIconHeight(icon, synthContext) + y);
        }
        final FontMetrics fontMetrics = component.getFontMetrics(font);
        final Rectangle iconR = this.iconR;
        final Rectangle iconR2 = this.iconR;
        final Rectangle iconR3 = this.iconR;
        final Rectangle iconR4 = this.iconR;
        final int n7 = 0;
        iconR4.height = n7;
        iconR3.width = n7;
        iconR2.y = n7;
        iconR.x = n7;
        final Rectangle textR = this.textR;
        final Rectangle textR2 = this.textR;
        final Rectangle textR3 = this.textR;
        final Rectangle textR4 = this.textR;
        final int n8 = 0;
        textR4.height = n8;
        textR3.width = n8;
        textR2.y = n8;
        textR.x = n8;
        this.viewR.x = x;
        this.viewR.y = y;
        final Rectangle viewR = this.viewR;
        final Rectangle viewR2 = this.viewR;
        final int n9 = 32767;
        viewR2.height = n9;
        viewR.width = n9;
        this.layoutText(synthContext, fontMetrics, s, icon, n, n2, n3, n4, this.viewR, this.iconR, this.textR, n5);
        final Dimension dimension2;
        final Dimension dimension = dimension2 = new Dimension(Math.max(this.iconR.x + this.iconR.width, this.textR.x + this.textR.width) - Math.min(this.iconR.x, this.textR.x), Math.max(this.iconR.y + this.iconR.height, this.textR.y + this.textR.height) - Math.min(this.iconR.y, this.textR.y));
        dimension2.width += x;
        final Dimension dimension3 = dimension;
        dimension3.height += y;
        return dimension;
    }
    
    public void paintText(final SynthContext synthContext, final Graphics graphics, final String s, final Rectangle rectangle, final int n) {
        this.paintText(synthContext, graphics, s, rectangle.x, rectangle.y, n);
    }
    
    public void paintText(final SynthContext synthContext, final Graphics graphics, final String s, final int n, int n2, final int n3) {
        if (s != null) {
            final JComponent component = synthContext.getComponent();
            n2 += SwingUtilities2.getFontMetrics(component, graphics).getAscent();
            SwingUtilities2.drawStringUnderlineCharAt(component, graphics, s, n3, n, n2);
        }
    }
    
    public void paintText(final SynthContext synthContext, final Graphics graphics, final String s, final Icon icon, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        if (icon == null && s == null) {
            return;
        }
        final JComponent component = synthContext.getComponent();
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(component, graphics);
        final Insets paintingInsets = SynthLookAndFeel.getPaintingInsets(synthContext, this.paintInsets);
        this.paintViewR.x = paintingInsets.left;
        this.paintViewR.y = paintingInsets.top;
        this.paintViewR.width = component.getWidth() - (paintingInsets.left + paintingInsets.right);
        this.paintViewR.height = component.getHeight() - (paintingInsets.top + paintingInsets.bottom);
        final Rectangle paintIconR = this.paintIconR;
        final Rectangle paintIconR2 = this.paintIconR;
        final Rectangle paintIconR3 = this.paintIconR;
        final Rectangle paintIconR4 = this.paintIconR;
        final int n8 = 0;
        paintIconR4.height = n8;
        paintIconR3.width = n8;
        paintIconR2.y = n8;
        paintIconR.x = n8;
        final Rectangle paintTextR = this.paintTextR;
        final Rectangle paintTextR2 = this.paintTextR;
        final Rectangle paintTextR3 = this.paintTextR;
        final Rectangle paintTextR4 = this.paintTextR;
        final int n9 = 0;
        paintTextR4.height = n9;
        paintTextR3.width = n9;
        paintTextR2.y = n9;
        paintTextR.x = n9;
        final String layoutText = this.layoutText(synthContext, fontMetrics, s, icon, n, n2, n3, n4, this.paintViewR, this.paintIconR, this.paintTextR, n5);
        if (icon != null) {
            final Color color = graphics.getColor();
            if (synthContext.getStyle().getBoolean(synthContext, "TableHeader.alignSorterArrow", false) && "TableHeader.renderer".equals(component.getName())) {
                this.paintIconR.x = this.paintViewR.width - this.paintIconR.width;
            }
            else {
                final Rectangle paintIconR5 = this.paintIconR;
                paintIconR5.x += n7;
            }
            final Rectangle paintIconR6 = this.paintIconR;
            paintIconR6.y += n7;
            SynthIcon.paintIcon(icon, synthContext, graphics, this.paintIconR.x, this.paintIconR.y, this.paintIconR.width, this.paintIconR.height);
            graphics.setColor(color);
        }
        if (s != null) {
            final View view = (View)component.getClientProperty("html");
            if (view != null) {
                view.paint(graphics, this.paintTextR);
            }
            else {
                final Rectangle paintTextR5 = this.paintTextR;
                paintTextR5.x += n7;
                final Rectangle paintTextR6 = this.paintTextR;
                paintTextR6.y += n7;
                this.paintText(synthContext, graphics, layoutText, this.paintTextR, n6);
            }
        }
    }
    
    static Dimension getPreferredMenuItemSize(final SynthContext synthContext, final SynthContext synthContext2, final JComponent component, final Icon icon, final Icon icon2, final int n, final String s, final boolean b, final String s2) {
        final JMenuItem menuItem = (JMenuItem)component;
        final SynthMenuItemLayoutHelper synthMenuItemLayoutHelper = new SynthMenuItemLayoutHelper(synthContext, synthContext2, menuItem, icon, icon2, MenuItemLayoutHelper.createMaxRect(), n, s, SynthLookAndFeel.isLeftToRight(menuItem), b, s2);
        final Dimension dimension = new Dimension();
        final int gap = synthMenuItemLayoutHelper.getGap();
        dimension.width = 0;
        MenuItemLayoutHelper.addMaxWidth(synthMenuItemLayoutHelper.getCheckSize(), gap, dimension);
        MenuItemLayoutHelper.addMaxWidth(synthMenuItemLayoutHelper.getLabelSize(), gap, dimension);
        MenuItemLayoutHelper.addWidth(synthMenuItemLayoutHelper.getMaxAccOrArrowWidth(), 5 * gap, dimension);
        final Dimension dimension2 = dimension;
        dimension2.width -= gap;
        dimension.height = MenuItemLayoutHelper.max(synthMenuItemLayoutHelper.getCheckSize().getHeight(), synthMenuItemLayoutHelper.getLabelSize().getHeight(), synthMenuItemLayoutHelper.getAccSize().getHeight(), synthMenuItemLayoutHelper.getArrowSize().getHeight());
        final Insets insets = synthMenuItemLayoutHelper.getMenuItem().getInsets();
        if (insets != null) {
            final Dimension dimension3 = dimension;
            dimension3.width += insets.left + insets.right;
            final Dimension dimension4 = dimension;
            dimension4.height += insets.top + insets.bottom;
        }
        if (dimension.width % 2 == 0) {
            final Dimension dimension5 = dimension;
            ++dimension5.width;
        }
        if (dimension.height % 2 == 0) {
            final Dimension dimension6 = dimension;
            ++dimension6.height;
        }
        return dimension;
    }
    
    static void applyInsets(final Rectangle rectangle, final Insets insets, final boolean b) {
        if (insets != null) {
            rectangle.x += (b ? insets.left : insets.right);
            rectangle.y += insets.top;
            rectangle.width -= (b ? insets.right : insets.left) + rectangle.x;
            rectangle.height -= insets.bottom + rectangle.y;
        }
    }
    
    static void paint(final SynthContext synthContext, final SynthContext synthContext2, final Graphics graphics, final Icon icon, final Icon icon2, final String s, final int n, final String s2) {
        final JMenuItem menuItem = (JMenuItem)synthContext.getComponent();
        graphics.setFont(synthContext.getStyle().getFont(synthContext));
        final Rectangle rectangle = new Rectangle(0, 0, menuItem.getWidth(), menuItem.getHeight());
        final boolean leftToRight = SynthLookAndFeel.isLeftToRight(menuItem);
        applyInsets(rectangle, menuItem.getInsets(), leftToRight);
        final SynthMenuItemLayoutHelper synthMenuItemLayoutHelper = new SynthMenuItemLayoutHelper(synthContext, synthContext2, menuItem, icon, icon2, rectangle, n, s, leftToRight, MenuItemLayoutHelper.useCheckAndArrow(menuItem), s2);
        paintMenuItem(graphics, synthMenuItemLayoutHelper, synthMenuItemLayoutHelper.layoutMenuItem());
    }
    
    static void paintMenuItem(final Graphics graphics, final SynthMenuItemLayoutHelper synthMenuItemLayoutHelper, final MenuItemLayoutHelper.LayoutResult layoutResult) {
        final Font font = graphics.getFont();
        final Color color = graphics.getColor();
        paintCheckIcon(graphics, synthMenuItemLayoutHelper, layoutResult);
        paintIcon(graphics, synthMenuItemLayoutHelper, layoutResult);
        paintText(graphics, synthMenuItemLayoutHelper, layoutResult);
        paintAccText(graphics, synthMenuItemLayoutHelper, layoutResult);
        paintArrowIcon(graphics, synthMenuItemLayoutHelper, layoutResult);
        graphics.setColor(color);
        graphics.setFont(font);
    }
    
    static void paintBackground(final Graphics graphics, final SynthMenuItemLayoutHelper synthMenuItemLayoutHelper) {
        paintBackground(synthMenuItemLayoutHelper.getContext(), graphics, synthMenuItemLayoutHelper.getMenuItem());
    }
    
    static void paintBackground(final SynthContext synthContext, final Graphics graphics, final JComponent component) {
        synthContext.getPainter().paintMenuItemBackground(synthContext, graphics, 0, 0, component.getWidth(), component.getHeight());
    }
    
    static void paintIcon(final Graphics graphics, final SynthMenuItemLayoutHelper synthMenuItemLayoutHelper, final MenuItemLayoutHelper.LayoutResult layoutResult) {
        if (synthMenuItemLayoutHelper.getIcon() != null) {
            final JMenuItem menuItem = synthMenuItemLayoutHelper.getMenuItem();
            final ButtonModel model = menuItem.getModel();
            Icon icon;
            if (!model.isEnabled()) {
                icon = menuItem.getDisabledIcon();
            }
            else if (model.isPressed() && model.isArmed()) {
                icon = menuItem.getPressedIcon();
                if (icon == null) {
                    icon = menuItem.getIcon();
                }
            }
            else {
                icon = menuItem.getIcon();
            }
            if (icon != null) {
                final Rectangle iconRect = layoutResult.getIconRect();
                SynthIcon.paintIcon(icon, synthMenuItemLayoutHelper.getContext(), graphics, iconRect.x, iconRect.y, iconRect.width, iconRect.height);
            }
        }
    }
    
    static void paintCheckIcon(final Graphics graphics, final SynthMenuItemLayoutHelper synthMenuItemLayoutHelper, final MenuItemLayoutHelper.LayoutResult layoutResult) {
        if (synthMenuItemLayoutHelper.getCheckIcon() != null) {
            final Rectangle checkRect = layoutResult.getCheckRect();
            SynthIcon.paintIcon(synthMenuItemLayoutHelper.getCheckIcon(), synthMenuItemLayoutHelper.getContext(), graphics, checkRect.x, checkRect.y, checkRect.width, checkRect.height);
        }
    }
    
    static void paintAccText(final Graphics graphics, final SynthMenuItemLayoutHelper synthMenuItemLayoutHelper, final MenuItemLayoutHelper.LayoutResult layoutResult) {
        final String accText = synthMenuItemLayoutHelper.getAccText();
        if (accText != null && !accText.equals("")) {
            graphics.setColor(synthMenuItemLayoutHelper.getAccStyle().getColor(synthMenuItemLayoutHelper.getAccContext(), ColorType.TEXT_FOREGROUND));
            graphics.setFont(synthMenuItemLayoutHelper.getAccStyle().getFont(synthMenuItemLayoutHelper.getAccContext()));
            synthMenuItemLayoutHelper.getAccGraphicsUtils().paintText(synthMenuItemLayoutHelper.getAccContext(), graphics, accText, layoutResult.getAccRect().x, layoutResult.getAccRect().y, -1);
        }
    }
    
    static void paintText(final Graphics graphics, final SynthMenuItemLayoutHelper synthMenuItemLayoutHelper, final MenuItemLayoutHelper.LayoutResult layoutResult) {
        if (!synthMenuItemLayoutHelper.getText().equals("")) {
            if (synthMenuItemLayoutHelper.getHtmlView() != null) {
                synthMenuItemLayoutHelper.getHtmlView().paint(graphics, layoutResult.getTextRect());
            }
            else {
                graphics.setColor(synthMenuItemLayoutHelper.getStyle().getColor(synthMenuItemLayoutHelper.getContext(), ColorType.TEXT_FOREGROUND));
                graphics.setFont(synthMenuItemLayoutHelper.getStyle().getFont(synthMenuItemLayoutHelper.getContext()));
                synthMenuItemLayoutHelper.getGraphicsUtils().paintText(synthMenuItemLayoutHelper.getContext(), graphics, synthMenuItemLayoutHelper.getText(), layoutResult.getTextRect().x, layoutResult.getTextRect().y, synthMenuItemLayoutHelper.getMenuItem().getDisplayedMnemonicIndex());
            }
        }
    }
    
    static void paintArrowIcon(final Graphics graphics, final SynthMenuItemLayoutHelper synthMenuItemLayoutHelper, final MenuItemLayoutHelper.LayoutResult layoutResult) {
        if (synthMenuItemLayoutHelper.getArrowIcon() != null) {
            final Rectangle arrowRect = layoutResult.getArrowRect();
            SynthIcon.paintIcon(synthMenuItemLayoutHelper.getArrowIcon(), synthMenuItemLayoutHelper.getContext(), graphics, arrowRect.x, arrowRect.y, arrowRect.width, arrowRect.height);
        }
    }
    
    private static class SynthIconWrapper implements Icon
    {
        private static final List<SynthIconWrapper> CACHE;
        private SynthIcon synthIcon;
        private SynthContext context;
        
        static SynthIconWrapper get(final SynthIcon synthIcon, final SynthContext synthContext) {
            synchronized (SynthIconWrapper.CACHE) {
                final int size = SynthIconWrapper.CACHE.size();
                if (size > 0) {
                    final SynthIconWrapper synthIconWrapper = SynthIconWrapper.CACHE.remove(size - 1);
                    synthIconWrapper.reset(synthIcon, synthContext);
                    return synthIconWrapper;
                }
            }
            return new SynthIconWrapper(synthIcon, synthContext);
        }
        
        static void release(final SynthIconWrapper synthIconWrapper) {
            synthIconWrapper.reset(null, null);
            synchronized (SynthIconWrapper.CACHE) {
                SynthIconWrapper.CACHE.add(synthIconWrapper);
            }
        }
        
        SynthIconWrapper(final SynthIcon synthIcon, final SynthContext synthContext) {
            this.reset(synthIcon, synthContext);
        }
        
        void reset(final SynthIcon synthIcon, final SynthContext context) {
            this.synthIcon = synthIcon;
            this.context = context;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
        }
        
        @Override
        public int getIconWidth() {
            return this.synthIcon.getIconWidth(this.context);
        }
        
        @Override
        public int getIconHeight() {
            return this.synthIcon.getIconHeight(this.context);
        }
        
        static {
            CACHE = new ArrayList<SynthIconWrapper>(1);
        }
    }
}
