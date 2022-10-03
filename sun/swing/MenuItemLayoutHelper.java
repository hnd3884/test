package sun.swing;

import java.util.HashMap;
import java.util.Map;
import java.awt.Dimension;
import java.awt.Container;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;
import javax.swing.text.View;
import javax.swing.Icon;
import java.awt.FontMetrics;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JMenuItem;

public class MenuItemLayoutHelper
{
    public static final StringUIClientPropertyKey MAX_ARROW_WIDTH;
    public static final StringUIClientPropertyKey MAX_CHECK_WIDTH;
    public static final StringUIClientPropertyKey MAX_ICON_WIDTH;
    public static final StringUIClientPropertyKey MAX_TEXT_WIDTH;
    public static final StringUIClientPropertyKey MAX_ACC_WIDTH;
    public static final StringUIClientPropertyKey MAX_LABEL_WIDTH;
    private JMenuItem mi;
    private JComponent miParent;
    private Font font;
    private Font accFont;
    private FontMetrics fm;
    private FontMetrics accFm;
    private Icon icon;
    private Icon checkIcon;
    private Icon arrowIcon;
    private String text;
    private String accText;
    private boolean isColumnLayout;
    private boolean useCheckAndArrow;
    private boolean isLeftToRight;
    private boolean isTopLevelMenu;
    private View htmlView;
    private int verticalAlignment;
    private int horizontalAlignment;
    private int verticalTextPosition;
    private int horizontalTextPosition;
    private int gap;
    private int leadingGap;
    private int afterCheckIconGap;
    private int minTextOffset;
    private int leftTextExtraWidth;
    private Rectangle viewRect;
    private RectSize iconSize;
    private RectSize textSize;
    private RectSize accSize;
    private RectSize checkSize;
    private RectSize arrowSize;
    private RectSize labelSize;
    
    protected MenuItemLayoutHelper() {
    }
    
    public MenuItemLayoutHelper(final JMenuItem menuItem, final Icon icon, final Icon icon2, final Rectangle rectangle, final int n, final String s, final boolean b, final Font font, final Font font2, final boolean b2, final String s2) {
        this.reset(menuItem, icon, icon2, rectangle, n, s, b, font, font2, b2, s2);
    }
    
    protected void reset(final JMenuItem mi, final Icon checkIcon, final Icon arrowIcon, final Rectangle viewRect, final int gap, final String s, final boolean isLeftToRight, final Font font, final Font accFont, final boolean useCheckAndArrow, final String s2) {
        this.mi = mi;
        this.miParent = getMenuItemParent(mi);
        this.accText = this.getAccText(s);
        this.verticalAlignment = mi.getVerticalAlignment();
        this.horizontalAlignment = mi.getHorizontalAlignment();
        this.verticalTextPosition = mi.getVerticalTextPosition();
        this.horizontalTextPosition = mi.getHorizontalTextPosition();
        this.useCheckAndArrow = useCheckAndArrow;
        this.font = font;
        this.accFont = accFont;
        this.fm = mi.getFontMetrics(font);
        this.accFm = mi.getFontMetrics(accFont);
        this.isLeftToRight = isLeftToRight;
        this.isColumnLayout = isColumnLayout(isLeftToRight, this.horizontalAlignment, this.horizontalTextPosition, this.verticalTextPosition);
        this.isTopLevelMenu = (this.miParent == null);
        this.checkIcon = checkIcon;
        this.icon = this.getIcon(s2);
        this.arrowIcon = arrowIcon;
        this.text = mi.getText();
        this.gap = gap;
        this.afterCheckIconGap = this.getAfterCheckIconGap(s2);
        this.minTextOffset = this.getMinTextOffset(s2);
        this.htmlView = (View)mi.getClientProperty("html");
        this.viewRect = viewRect;
        this.iconSize = new RectSize();
        this.textSize = new RectSize();
        this.accSize = new RectSize();
        this.checkSize = new RectSize();
        this.arrowSize = new RectSize();
        this.labelSize = new RectSize();
        this.calcExtraWidths();
        this.calcWidthsAndHeights();
        this.setOriginalWidths();
        this.calcMaxWidths();
        this.leadingGap = this.getLeadingGap(s2);
        this.calcMaxTextOffset(viewRect);
    }
    
    private void calcExtraWidths() {
        this.leftTextExtraWidth = this.getLeftExtraWidth(this.text);
    }
    
    private int getLeftExtraWidth(final String s) {
        final int leftSideBearing = SwingUtilities2.getLeftSideBearing(this.mi, this.fm, s);
        if (leftSideBearing < 0) {
            return -leftSideBearing;
        }
        return 0;
    }
    
    private void setOriginalWidths() {
        this.iconSize.origWidth = this.iconSize.width;
        this.textSize.origWidth = this.textSize.width;
        this.accSize.origWidth = this.accSize.width;
        this.checkSize.origWidth = this.checkSize.width;
        this.arrowSize.origWidth = this.arrowSize.width;
    }
    
    private String getAccText(final String s) {
        String s2 = "";
        final KeyStroke accelerator = this.mi.getAccelerator();
        if (accelerator != null) {
            final int modifiers = accelerator.getModifiers();
            if (modifiers > 0) {
                s2 = KeyEvent.getKeyModifiersText(modifiers) + s;
            }
            final int keyCode = accelerator.getKeyCode();
            if (keyCode != 0) {
                s2 += KeyEvent.getKeyText(keyCode);
            }
            else {
                s2 += accelerator.getKeyChar();
            }
        }
        return s2;
    }
    
    private Icon getIcon(final String s) {
        Icon icon = null;
        final MenuItemCheckIconFactory menuItemCheckIconFactory = (MenuItemCheckIconFactory)UIManager.get(s + ".checkIconFactory");
        if (!this.isColumnLayout || !this.useCheckAndArrow || menuItemCheckIconFactory == null || !menuItemCheckIconFactory.isCompatible(this.checkIcon, s)) {
            icon = this.mi.getIcon();
        }
        return icon;
    }
    
    private int getMinTextOffset(final String s) {
        int intValue = 0;
        final Object value = UIManager.get(s + ".minimumTextOffset");
        if (value instanceof Integer) {
            intValue = (int)value;
        }
        return intValue;
    }
    
    private int getAfterCheckIconGap(final String s) {
        int n = this.gap;
        final Object value = UIManager.get(s + ".afterCheckIconGap");
        if (value instanceof Integer) {
            n = (int)value;
        }
        return n;
    }
    
    private int getLeadingGap(final String s) {
        if (this.checkSize.getMaxWidth() > 0) {
            return this.getCheckOffset(s);
        }
        return this.gap;
    }
    
    private int getCheckOffset(final String s) {
        int n = this.gap;
        final Object value = UIManager.get(s + ".checkIconOffset");
        if (value instanceof Integer) {
            n = (int)value;
        }
        return n;
    }
    
    protected void calcWidthsAndHeights() {
        if (this.icon != null) {
            this.iconSize.width = this.icon.getIconWidth();
            this.iconSize.height = this.icon.getIconHeight();
        }
        if (!this.accText.equals("")) {
            this.accSize.width = SwingUtilities2.stringWidth(this.mi, this.accFm, this.accText);
            this.accSize.height = this.accFm.getHeight();
        }
        if (this.text == null) {
            this.text = "";
        }
        else if (!this.text.equals("")) {
            if (this.htmlView != null) {
                this.textSize.width = (int)this.htmlView.getPreferredSpan(0);
                this.textSize.height = (int)this.htmlView.getPreferredSpan(1);
            }
            else {
                this.textSize.width = SwingUtilities2.stringWidth(this.mi, this.fm, this.text);
                this.textSize.height = this.fm.getHeight();
            }
        }
        if (this.useCheckAndArrow) {
            if (this.checkIcon != null) {
                this.checkSize.width = this.checkIcon.getIconWidth();
                this.checkSize.height = this.checkIcon.getIconHeight();
            }
            if (this.arrowIcon != null) {
                this.arrowSize.width = this.arrowIcon.getIconWidth();
                this.arrowSize.height = this.arrowIcon.getIconHeight();
            }
        }
        if (this.isColumnLayout) {
            this.labelSize.width = this.iconSize.width + this.textSize.width + this.gap;
            this.labelSize.height = max(this.checkSize.height, this.iconSize.height, this.textSize.height, this.accSize.height, this.arrowSize.height);
        }
        else {
            final Rectangle rectangle = new Rectangle();
            final Rectangle rectangle2 = new Rectangle();
            SwingUtilities.layoutCompoundLabel(this.mi, this.fm, this.text, this.icon, this.verticalAlignment, this.horizontalAlignment, this.verticalTextPosition, this.horizontalTextPosition, this.viewRect, rectangle2, rectangle, this.gap);
            final Rectangle rectangle3 = rectangle;
            rectangle3.width += this.leftTextExtraWidth;
            final Rectangle union = rectangle2.union(rectangle);
            this.labelSize.height = union.height;
            this.labelSize.width = union.width;
        }
    }
    
    protected void calcMaxWidths() {
        this.calcMaxWidth(this.checkSize, MenuItemLayoutHelper.MAX_CHECK_WIDTH);
        this.calcMaxWidth(this.arrowSize, MenuItemLayoutHelper.MAX_ARROW_WIDTH);
        this.calcMaxWidth(this.accSize, MenuItemLayoutHelper.MAX_ACC_WIDTH);
        if (this.isColumnLayout) {
            this.calcMaxWidth(this.iconSize, MenuItemLayoutHelper.MAX_ICON_WIDTH);
            this.calcMaxWidth(this.textSize, MenuItemLayoutHelper.MAX_TEXT_WIDTH);
            int gap = this.gap;
            if (this.iconSize.getMaxWidth() == 0 || this.textSize.getMaxWidth() == 0) {
                gap = 0;
            }
            this.labelSize.maxWidth = this.calcMaxValue(MenuItemLayoutHelper.MAX_LABEL_WIDTH, this.iconSize.maxWidth + this.textSize.maxWidth + gap);
        }
        else {
            this.iconSize.maxWidth = this.getParentIntProperty(MenuItemLayoutHelper.MAX_ICON_WIDTH);
            this.calcMaxWidth(this.labelSize, MenuItemLayoutHelper.MAX_LABEL_WIDTH);
            int n = this.labelSize.maxWidth - this.iconSize.maxWidth;
            if (this.iconSize.maxWidth > 0) {
                n -= this.gap;
            }
            this.textSize.maxWidth = this.calcMaxValue(MenuItemLayoutHelper.MAX_TEXT_WIDTH, n);
        }
    }
    
    protected void calcMaxWidth(final RectSize rectSize, final Object o) {
        rectSize.maxWidth = this.calcMaxValue(o, rectSize.width);
    }
    
    protected int calcMaxValue(final Object o, final int n) {
        final int parentIntProperty = this.getParentIntProperty(o);
        if (n > parentIntProperty) {
            if (this.miParent != null) {
                this.miParent.putClientProperty(o, n);
            }
            return n;
        }
        return parentIntProperty;
    }
    
    protected int getParentIntProperty(final Object o) {
        Object o2 = null;
        if (this.miParent != null) {
            o2 = this.miParent.getClientProperty(o);
        }
        if (o2 == null || !(o2 instanceof Integer)) {
            o2 = 0;
        }
        return (int)o2;
    }
    
    public static boolean isColumnLayout(final boolean b, final JMenuItem menuItem) {
        assert menuItem != null;
        return isColumnLayout(b, menuItem.getHorizontalAlignment(), menuItem.getHorizontalTextPosition(), menuItem.getVerticalTextPosition());
    }
    
    public static boolean isColumnLayout(final boolean b, final int n, final int n2, final int n3) {
        if (n3 != 0) {
            return false;
        }
        if (b) {
            if (n != 10 && n != 2) {
                return false;
            }
            if (n2 != 11 && n2 != 4) {
                return false;
            }
        }
        else {
            if (n != 10 && n != 4) {
                return false;
            }
            if (n2 != 11 && n2 != 2) {
                return false;
            }
        }
        return true;
    }
    
    private void calcMaxTextOffset(final Rectangle rectangle) {
        if (!this.isColumnLayout || !this.isLeftToRight) {
            return;
        }
        int minTextOffset = rectangle.x + this.leadingGap + this.checkSize.maxWidth + this.afterCheckIconGap + this.iconSize.maxWidth + this.gap;
        if (this.checkSize.maxWidth == 0) {
            minTextOffset -= this.afterCheckIconGap;
        }
        if (this.iconSize.maxWidth == 0) {
            minTextOffset -= this.gap;
        }
        if (minTextOffset < this.minTextOffset) {
            minTextOffset = this.minTextOffset;
        }
        this.calcMaxValue(SwingUtilities2.BASICMENUITEMUI_MAX_TEXT_OFFSET, minTextOffset);
    }
    
    public LayoutResult layoutMenuItem() {
        final LayoutResult layoutResult = this.createLayoutResult();
        this.prepareForLayout(layoutResult);
        if (this.isColumnLayout()) {
            if (this.isLeftToRight()) {
                this.doLTRColumnLayout(layoutResult, this.getLTRColumnAlignment());
            }
            else {
                this.doRTLColumnLayout(layoutResult, this.getRTLColumnAlignment());
            }
        }
        else if (this.isLeftToRight()) {
            this.doLTRComplexLayout(layoutResult, this.getLTRColumnAlignment());
        }
        else {
            this.doRTLComplexLayout(layoutResult, this.getRTLColumnAlignment());
        }
        this.alignAccCheckAndArrowVertically(layoutResult);
        return layoutResult;
    }
    
    private LayoutResult createLayoutResult() {
        return new LayoutResult(new Rectangle(this.iconSize.width, this.iconSize.height), new Rectangle(this.textSize.width, this.textSize.height), new Rectangle(this.accSize.width, this.accSize.height), new Rectangle(this.checkSize.width, this.checkSize.height), new Rectangle(this.arrowSize.width, this.arrowSize.height), new Rectangle(this.labelSize.width, this.labelSize.height));
    }
    
    public ColumnAlignment getLTRColumnAlignment() {
        return ColumnAlignment.LEFT_ALIGNMENT;
    }
    
    public ColumnAlignment getRTLColumnAlignment() {
        return ColumnAlignment.RIGHT_ALIGNMENT;
    }
    
    protected void prepareForLayout(final LayoutResult layoutResult) {
        layoutResult.checkRect.width = this.checkSize.maxWidth;
        layoutResult.accRect.width = this.accSize.maxWidth;
        layoutResult.arrowRect.width = this.arrowSize.maxWidth;
    }
    
    private void alignAccCheckAndArrowVertically(final LayoutResult layoutResult) {
        layoutResult.accRect.y = (int)(layoutResult.labelRect.y + layoutResult.labelRect.height / 2.0f - layoutResult.accRect.height / 2.0f);
        this.fixVerticalAlignment(layoutResult, layoutResult.accRect);
        if (this.useCheckAndArrow) {
            layoutResult.arrowRect.y = (int)(layoutResult.labelRect.y + layoutResult.labelRect.height / 2.0f - layoutResult.arrowRect.height / 2.0f);
            layoutResult.checkRect.y = (int)(layoutResult.labelRect.y + layoutResult.labelRect.height / 2.0f - layoutResult.checkRect.height / 2.0f);
            this.fixVerticalAlignment(layoutResult, layoutResult.arrowRect);
            this.fixVerticalAlignment(layoutResult, layoutResult.checkRect);
        }
    }
    
    private void fixVerticalAlignment(final LayoutResult layoutResult, final Rectangle rectangle) {
        int n = 0;
        if (rectangle.y < this.viewRect.y) {
            n = this.viewRect.y - rectangle.y;
        }
        else if (rectangle.y + rectangle.height > this.viewRect.y + this.viewRect.height) {
            n = this.viewRect.y + this.viewRect.height - rectangle.y - rectangle.height;
        }
        if (n != 0) {
            final Rectangle access$400 = layoutResult.checkRect;
            access$400.y += n;
            final Rectangle access$401 = layoutResult.iconRect;
            access$401.y += n;
            final Rectangle access$402 = layoutResult.textRect;
            access$402.y += n;
            final Rectangle access$403 = layoutResult.accRect;
            access$403.y += n;
            final Rectangle access$404 = layoutResult.arrowRect;
            access$404.y += n;
            final Rectangle access$405 = layoutResult.labelRect;
            access$405.y += n;
        }
    }
    
    private void doLTRColumnLayout(final LayoutResult layoutResult, final ColumnAlignment columnAlignment) {
        layoutResult.iconRect.width = this.iconSize.maxWidth;
        layoutResult.textRect.width = this.textSize.maxWidth;
        this.calcXPositionsLTR(this.viewRect.x, this.leadingGap, this.gap, layoutResult.checkRect, layoutResult.iconRect, layoutResult.textRect);
        if (layoutResult.checkRect.width > 0) {
            final Rectangle access$800 = layoutResult.iconRect;
            access$800.x += this.afterCheckIconGap - this.gap;
            final Rectangle access$801 = layoutResult.textRect;
            access$801.x += this.afterCheckIconGap - this.gap;
        }
        this.calcXPositionsRTL(this.viewRect.x + this.viewRect.width, this.leadingGap, this.gap, layoutResult.arrowRect, layoutResult.accRect);
        final int n = layoutResult.textRect.x - this.viewRect.x;
        if (!this.isTopLevelMenu && n < this.minTextOffset) {
            final Rectangle access$802 = layoutResult.textRect;
            access$802.x += this.minTextOffset - n;
        }
        this.alignRects(layoutResult, columnAlignment);
        this.calcTextAndIconYPositions(layoutResult);
        layoutResult.setLabelRect(layoutResult.textRect.union(layoutResult.iconRect));
    }
    
    private void doLTRComplexLayout(final LayoutResult layoutResult, final ColumnAlignment columnAlignment) {
        layoutResult.labelRect.width = this.labelSize.maxWidth;
        this.calcXPositionsLTR(this.viewRect.x, this.leadingGap, this.gap, layoutResult.checkRect, layoutResult.labelRect);
        if (layoutResult.checkRect.width > 0) {
            final Rectangle access$700 = layoutResult.labelRect;
            access$700.x += this.afterCheckIconGap - this.gap;
        }
        this.calcXPositionsRTL(this.viewRect.x + this.viewRect.width, this.leadingGap, this.gap, layoutResult.arrowRect, layoutResult.accRect);
        final int n = layoutResult.labelRect.x - this.viewRect.x;
        if (!this.isTopLevelMenu && n < this.minTextOffset) {
            final Rectangle access$701 = layoutResult.labelRect;
            access$701.x += this.minTextOffset - n;
        }
        this.alignRects(layoutResult, columnAlignment);
        this.calcLabelYPosition(layoutResult);
        this.layoutIconAndTextInLabelRect(layoutResult);
    }
    
    private void doRTLColumnLayout(final LayoutResult layoutResult, final ColumnAlignment columnAlignment) {
        layoutResult.iconRect.width = this.iconSize.maxWidth;
        layoutResult.textRect.width = this.textSize.maxWidth;
        this.calcXPositionsRTL(this.viewRect.x + this.viewRect.width, this.leadingGap, this.gap, layoutResult.checkRect, layoutResult.iconRect, layoutResult.textRect);
        if (layoutResult.checkRect.width > 0) {
            final Rectangle access$800 = layoutResult.iconRect;
            access$800.x -= this.afterCheckIconGap - this.gap;
            final Rectangle access$801 = layoutResult.textRect;
            access$801.x -= this.afterCheckIconGap - this.gap;
        }
        this.calcXPositionsLTR(this.viewRect.x, this.leadingGap, this.gap, layoutResult.arrowRect, layoutResult.accRect);
        final int n = this.viewRect.x + this.viewRect.width - (layoutResult.textRect.x + layoutResult.textRect.width);
        if (!this.isTopLevelMenu && n < this.minTextOffset) {
            final Rectangle access$802 = layoutResult.textRect;
            access$802.x -= this.minTextOffset - n;
        }
        this.alignRects(layoutResult, columnAlignment);
        this.calcTextAndIconYPositions(layoutResult);
        layoutResult.setLabelRect(layoutResult.textRect.union(layoutResult.iconRect));
    }
    
    private void doRTLComplexLayout(final LayoutResult layoutResult, final ColumnAlignment columnAlignment) {
        layoutResult.labelRect.width = this.labelSize.maxWidth;
        this.calcXPositionsRTL(this.viewRect.x + this.viewRect.width, this.leadingGap, this.gap, layoutResult.checkRect, layoutResult.labelRect);
        if (layoutResult.checkRect.width > 0) {
            final Rectangle access$700 = layoutResult.labelRect;
            access$700.x -= this.afterCheckIconGap - this.gap;
        }
        this.calcXPositionsLTR(this.viewRect.x, this.leadingGap, this.gap, layoutResult.arrowRect, layoutResult.accRect);
        final int n = this.viewRect.x + this.viewRect.width - (layoutResult.labelRect.x + layoutResult.labelRect.width);
        if (!this.isTopLevelMenu && n < this.minTextOffset) {
            final Rectangle access$701 = layoutResult.labelRect;
            access$701.x -= this.minTextOffset - n;
        }
        this.alignRects(layoutResult, columnAlignment);
        this.calcLabelYPosition(layoutResult);
        this.layoutIconAndTextInLabelRect(layoutResult);
    }
    
    private void alignRects(final LayoutResult layoutResult, final ColumnAlignment columnAlignment) {
        this.alignRect(layoutResult.checkRect, columnAlignment.getCheckAlignment(), this.checkSize.getOrigWidth());
        this.alignRect(layoutResult.iconRect, columnAlignment.getIconAlignment(), this.iconSize.getOrigWidth());
        this.alignRect(layoutResult.textRect, columnAlignment.getTextAlignment(), this.textSize.getOrigWidth());
        this.alignRect(layoutResult.accRect, columnAlignment.getAccAlignment(), this.accSize.getOrigWidth());
        this.alignRect(layoutResult.arrowRect, columnAlignment.getArrowAlignment(), this.arrowSize.getOrigWidth());
    }
    
    private void alignRect(final Rectangle rectangle, final int n, final int width) {
        if (n == 4) {
            rectangle.x = rectangle.x + rectangle.width - width;
        }
        rectangle.width = width;
    }
    
    protected void layoutIconAndTextInLabelRect(final LayoutResult layoutResult) {
        layoutResult.setTextRect(new Rectangle());
        layoutResult.setIconRect(new Rectangle());
        SwingUtilities.layoutCompoundLabel(this.mi, this.fm, this.text, this.icon, this.verticalAlignment, this.horizontalAlignment, this.verticalTextPosition, this.horizontalTextPosition, layoutResult.labelRect, layoutResult.iconRect, layoutResult.textRect, this.gap);
    }
    
    private void calcXPositionsLTR(final int n, final int n2, final int n3, final Rectangle... array) {
        int x = n + n2;
        for (final Rectangle rectangle : array) {
            rectangle.x = x;
            if (rectangle.width > 0) {
                x += rectangle.width + n3;
            }
        }
    }
    
    private void calcXPositionsRTL(final int n, final int n2, final int n3, final Rectangle... array) {
        int n4 = n - n2;
        for (final Rectangle rectangle : array) {
            rectangle.x = n4 - rectangle.width;
            if (rectangle.width > 0) {
                n4 -= rectangle.width + n3;
            }
        }
    }
    
    private void calcTextAndIconYPositions(final LayoutResult layoutResult) {
        if (this.verticalAlignment == 1) {
            layoutResult.textRect.y = (int)(this.viewRect.y + layoutResult.labelRect.height / 2.0f - layoutResult.textRect.height / 2.0f);
            layoutResult.iconRect.y = (int)(this.viewRect.y + layoutResult.labelRect.height / 2.0f - layoutResult.iconRect.height / 2.0f);
        }
        else if (this.verticalAlignment == 0) {
            layoutResult.textRect.y = (int)(this.viewRect.y + this.viewRect.height / 2.0f - layoutResult.textRect.height / 2.0f);
            layoutResult.iconRect.y = (int)(this.viewRect.y + this.viewRect.height / 2.0f - layoutResult.iconRect.height / 2.0f);
        }
        else if (this.verticalAlignment == 3) {
            layoutResult.textRect.y = (int)(this.viewRect.y + this.viewRect.height - layoutResult.labelRect.height / 2.0f - layoutResult.textRect.height / 2.0f);
            layoutResult.iconRect.y = (int)(this.viewRect.y + this.viewRect.height - layoutResult.labelRect.height / 2.0f - layoutResult.iconRect.height / 2.0f);
        }
    }
    
    private void calcLabelYPosition(final LayoutResult layoutResult) {
        if (this.verticalAlignment == 1) {
            layoutResult.labelRect.y = this.viewRect.y;
        }
        else if (this.verticalAlignment == 0) {
            layoutResult.labelRect.y = (int)(this.viewRect.y + this.viewRect.height / 2.0f - layoutResult.labelRect.height / 2.0f);
        }
        else if (this.verticalAlignment == 3) {
            layoutResult.labelRect.y = this.viewRect.y + this.viewRect.height - layoutResult.labelRect.height;
        }
    }
    
    public static JComponent getMenuItemParent(final JMenuItem menuItem) {
        final Container parent = menuItem.getParent();
        if (parent instanceof JComponent && (!(menuItem instanceof JMenu) || !((JMenu)menuItem).isTopLevelMenu())) {
            return (JComponent)parent;
        }
        return null;
    }
    
    public static void clearUsedParentClientProperties(final JMenuItem menuItem) {
        clearUsedClientProperties(getMenuItemParent(menuItem));
    }
    
    public static void clearUsedClientProperties(final JComponent component) {
        if (component != null) {
            component.putClientProperty(MenuItemLayoutHelper.MAX_ARROW_WIDTH, null);
            component.putClientProperty(MenuItemLayoutHelper.MAX_CHECK_WIDTH, null);
            component.putClientProperty(MenuItemLayoutHelper.MAX_ACC_WIDTH, null);
            component.putClientProperty(MenuItemLayoutHelper.MAX_TEXT_WIDTH, null);
            component.putClientProperty(MenuItemLayoutHelper.MAX_ICON_WIDTH, null);
            component.putClientProperty(MenuItemLayoutHelper.MAX_LABEL_WIDTH, null);
            component.putClientProperty(SwingUtilities2.BASICMENUITEMUI_MAX_TEXT_OFFSET, null);
        }
    }
    
    public static int max(final int... array) {
        int n = Integer.MIN_VALUE;
        for (final int n2 : array) {
            if (n2 > n) {
                n = n2;
            }
        }
        return n;
    }
    
    public static Rectangle createMaxRect() {
        return new Rectangle(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    public static void addMaxWidth(final RectSize rectSize, final int n, final Dimension dimension) {
        if (rectSize.maxWidth > 0) {
            dimension.width += rectSize.maxWidth + n;
        }
    }
    
    public static void addWidth(final int n, final int n2, final Dimension dimension) {
        if (n > 0) {
            dimension.width += n + n2;
        }
    }
    
    public JMenuItem getMenuItem() {
        return this.mi;
    }
    
    public JComponent getMenuItemParent() {
        return this.miParent;
    }
    
    public Font getFont() {
        return this.font;
    }
    
    public Font getAccFont() {
        return this.accFont;
    }
    
    public FontMetrics getFontMetrics() {
        return this.fm;
    }
    
    public FontMetrics getAccFontMetrics() {
        return this.accFm;
    }
    
    public Icon getIcon() {
        return this.icon;
    }
    
    public Icon getCheckIcon() {
        return this.checkIcon;
    }
    
    public Icon getArrowIcon() {
        return this.arrowIcon;
    }
    
    public String getText() {
        return this.text;
    }
    
    public String getAccText() {
        return this.accText;
    }
    
    public boolean isColumnLayout() {
        return this.isColumnLayout;
    }
    
    public boolean useCheckAndArrow() {
        return this.useCheckAndArrow;
    }
    
    public boolean isLeftToRight() {
        return this.isLeftToRight;
    }
    
    public boolean isTopLevelMenu() {
        return this.isTopLevelMenu;
    }
    
    public View getHtmlView() {
        return this.htmlView;
    }
    
    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }
    
    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }
    
    public int getVerticalTextPosition() {
        return this.verticalTextPosition;
    }
    
    public int getHorizontalTextPosition() {
        return this.horizontalTextPosition;
    }
    
    public int getGap() {
        return this.gap;
    }
    
    public int getLeadingGap() {
        return this.leadingGap;
    }
    
    public int getAfterCheckIconGap() {
        return this.afterCheckIconGap;
    }
    
    public int getMinTextOffset() {
        return this.minTextOffset;
    }
    
    public Rectangle getViewRect() {
        return this.viewRect;
    }
    
    public RectSize getIconSize() {
        return this.iconSize;
    }
    
    public RectSize getTextSize() {
        return this.textSize;
    }
    
    public RectSize getAccSize() {
        return this.accSize;
    }
    
    public RectSize getCheckSize() {
        return this.checkSize;
    }
    
    public RectSize getArrowSize() {
        return this.arrowSize;
    }
    
    public RectSize getLabelSize() {
        return this.labelSize;
    }
    
    protected void setMenuItem(final JMenuItem mi) {
        this.mi = mi;
    }
    
    protected void setMenuItemParent(final JComponent miParent) {
        this.miParent = miParent;
    }
    
    protected void setFont(final Font font) {
        this.font = font;
    }
    
    protected void setAccFont(final Font accFont) {
        this.accFont = accFont;
    }
    
    protected void setFontMetrics(final FontMetrics fm) {
        this.fm = fm;
    }
    
    protected void setAccFontMetrics(final FontMetrics accFm) {
        this.accFm = accFm;
    }
    
    protected void setIcon(final Icon icon) {
        this.icon = icon;
    }
    
    protected void setCheckIcon(final Icon checkIcon) {
        this.checkIcon = checkIcon;
    }
    
    protected void setArrowIcon(final Icon arrowIcon) {
        this.arrowIcon = arrowIcon;
    }
    
    protected void setText(final String text) {
        this.text = text;
    }
    
    protected void setAccText(final String accText) {
        this.accText = accText;
    }
    
    protected void setColumnLayout(final boolean isColumnLayout) {
        this.isColumnLayout = isColumnLayout;
    }
    
    protected void setUseCheckAndArrow(final boolean useCheckAndArrow) {
        this.useCheckAndArrow = useCheckAndArrow;
    }
    
    protected void setLeftToRight(final boolean isLeftToRight) {
        this.isLeftToRight = isLeftToRight;
    }
    
    protected void setTopLevelMenu(final boolean isTopLevelMenu) {
        this.isTopLevelMenu = isTopLevelMenu;
    }
    
    protected void setHtmlView(final View htmlView) {
        this.htmlView = htmlView;
    }
    
    protected void setVerticalAlignment(final int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }
    
    protected void setHorizontalAlignment(final int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }
    
    protected void setVerticalTextPosition(final int verticalTextPosition) {
        this.verticalTextPosition = verticalTextPosition;
    }
    
    protected void setHorizontalTextPosition(final int horizontalTextPosition) {
        this.horizontalTextPosition = horizontalTextPosition;
    }
    
    protected void setGap(final int gap) {
        this.gap = gap;
    }
    
    protected void setLeadingGap(final int leadingGap) {
        this.leadingGap = leadingGap;
    }
    
    protected void setAfterCheckIconGap(final int afterCheckIconGap) {
        this.afterCheckIconGap = afterCheckIconGap;
    }
    
    protected void setMinTextOffset(final int minTextOffset) {
        this.minTextOffset = minTextOffset;
    }
    
    protected void setViewRect(final Rectangle viewRect) {
        this.viewRect = viewRect;
    }
    
    protected void setIconSize(final RectSize iconSize) {
        this.iconSize = iconSize;
    }
    
    protected void setTextSize(final RectSize textSize) {
        this.textSize = textSize;
    }
    
    protected void setAccSize(final RectSize accSize) {
        this.accSize = accSize;
    }
    
    protected void setCheckSize(final RectSize checkSize) {
        this.checkSize = checkSize;
    }
    
    protected void setArrowSize(final RectSize arrowSize) {
        this.arrowSize = arrowSize;
    }
    
    protected void setLabelSize(final RectSize labelSize) {
        this.labelSize = labelSize;
    }
    
    public int getLeftTextExtraWidth() {
        return this.leftTextExtraWidth;
    }
    
    public static boolean useCheckAndArrow(final JMenuItem menuItem) {
        boolean b = true;
        if (menuItem instanceof JMenu && ((JMenu)menuItem).isTopLevelMenu()) {
            b = false;
        }
        return b;
    }
    
    static {
        MAX_ARROW_WIDTH = new StringUIClientPropertyKey("maxArrowWidth");
        MAX_CHECK_WIDTH = new StringUIClientPropertyKey("maxCheckWidth");
        MAX_ICON_WIDTH = new StringUIClientPropertyKey("maxIconWidth");
        MAX_TEXT_WIDTH = new StringUIClientPropertyKey("maxTextWidth");
        MAX_ACC_WIDTH = new StringUIClientPropertyKey("maxAccWidth");
        MAX_LABEL_WIDTH = new StringUIClientPropertyKey("maxLabelWidth");
    }
    
    public static class LayoutResult
    {
        private Rectangle iconRect;
        private Rectangle textRect;
        private Rectangle accRect;
        private Rectangle checkRect;
        private Rectangle arrowRect;
        private Rectangle labelRect;
        
        public LayoutResult() {
            this.iconRect = new Rectangle();
            this.textRect = new Rectangle();
            this.accRect = new Rectangle();
            this.checkRect = new Rectangle();
            this.arrowRect = new Rectangle();
            this.labelRect = new Rectangle();
        }
        
        public LayoutResult(final Rectangle iconRect, final Rectangle textRect, final Rectangle accRect, final Rectangle checkRect, final Rectangle arrowRect, final Rectangle labelRect) {
            this.iconRect = iconRect;
            this.textRect = textRect;
            this.accRect = accRect;
            this.checkRect = checkRect;
            this.arrowRect = arrowRect;
            this.labelRect = labelRect;
        }
        
        public Rectangle getIconRect() {
            return this.iconRect;
        }
        
        public void setIconRect(final Rectangle iconRect) {
            this.iconRect = iconRect;
        }
        
        public Rectangle getTextRect() {
            return this.textRect;
        }
        
        public void setTextRect(final Rectangle textRect) {
            this.textRect = textRect;
        }
        
        public Rectangle getAccRect() {
            return this.accRect;
        }
        
        public void setAccRect(final Rectangle accRect) {
            this.accRect = accRect;
        }
        
        public Rectangle getCheckRect() {
            return this.checkRect;
        }
        
        public void setCheckRect(final Rectangle checkRect) {
            this.checkRect = checkRect;
        }
        
        public Rectangle getArrowRect() {
            return this.arrowRect;
        }
        
        public void setArrowRect(final Rectangle arrowRect) {
            this.arrowRect = arrowRect;
        }
        
        public Rectangle getLabelRect() {
            return this.labelRect;
        }
        
        public void setLabelRect(final Rectangle labelRect) {
            this.labelRect = labelRect;
        }
        
        public Map<String, Rectangle> getAllRects() {
            final HashMap hashMap = new HashMap();
            hashMap.put("checkRect", this.checkRect);
            hashMap.put("iconRect", this.iconRect);
            hashMap.put("textRect", this.textRect);
            hashMap.put("accRect", this.accRect);
            hashMap.put("arrowRect", this.arrowRect);
            hashMap.put("labelRect", this.labelRect);
            return hashMap;
        }
    }
    
    public static class ColumnAlignment
    {
        private int checkAlignment;
        private int iconAlignment;
        private int textAlignment;
        private int accAlignment;
        private int arrowAlignment;
        public static final ColumnAlignment LEFT_ALIGNMENT;
        public static final ColumnAlignment RIGHT_ALIGNMENT;
        
        public ColumnAlignment(final int checkAlignment, final int iconAlignment, final int textAlignment, final int accAlignment, final int arrowAlignment) {
            this.checkAlignment = checkAlignment;
            this.iconAlignment = iconAlignment;
            this.textAlignment = textAlignment;
            this.accAlignment = accAlignment;
            this.arrowAlignment = arrowAlignment;
        }
        
        public int getCheckAlignment() {
            return this.checkAlignment;
        }
        
        public int getIconAlignment() {
            return this.iconAlignment;
        }
        
        public int getTextAlignment() {
            return this.textAlignment;
        }
        
        public int getAccAlignment() {
            return this.accAlignment;
        }
        
        public int getArrowAlignment() {
            return this.arrowAlignment;
        }
        
        static {
            LEFT_ALIGNMENT = new ColumnAlignment(2, 2, 2, 2, 2);
            RIGHT_ALIGNMENT = new ColumnAlignment(4, 4, 4, 4, 4);
        }
    }
    
    public static class RectSize
    {
        private int width;
        private int height;
        private int origWidth;
        private int maxWidth;
        
        public RectSize() {
        }
        
        public RectSize(final int width, final int height, final int origWidth, final int maxWidth) {
            this.width = width;
            this.height = height;
            this.origWidth = origWidth;
            this.maxWidth = maxWidth;
        }
        
        public int getWidth() {
            return this.width;
        }
        
        public int getHeight() {
            return this.height;
        }
        
        public int getOrigWidth() {
            return this.origWidth;
        }
        
        public int getMaxWidth() {
            return this.maxWidth;
        }
        
        public void setWidth(final int width) {
            this.width = width;
        }
        
        public void setHeight(final int height) {
            this.height = height;
        }
        
        public void setOrigWidth(final int origWidth) {
            this.origWidth = origWidth;
        }
        
        public void setMaxWidth(final int maxWidth) {
            this.maxWidth = maxWidth;
        }
        
        @Override
        public String toString() {
            return "[w=" + this.width + ",h=" + this.height + ",ow=" + this.origWidth + ",mw=" + this.maxWidth + "]";
        }
    }
}
