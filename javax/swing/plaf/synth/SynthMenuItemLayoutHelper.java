package javax.swing.plaf.synth;

import sun.swing.plaf.synth.SynthIcon;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import sun.swing.StringUIClientPropertyKey;
import sun.swing.MenuItemLayoutHelper;

class SynthMenuItemLayoutHelper extends MenuItemLayoutHelper
{
    public static final StringUIClientPropertyKey MAX_ACC_OR_ARROW_WIDTH;
    public static final ColumnAlignment LTR_ALIGNMENT_1;
    public static final ColumnAlignment LTR_ALIGNMENT_2;
    public static final ColumnAlignment RTL_ALIGNMENT_1;
    public static final ColumnAlignment RTL_ALIGNMENT_2;
    private SynthContext context;
    private SynthContext accContext;
    private SynthStyle style;
    private SynthStyle accStyle;
    private SynthGraphicsUtils gu;
    private SynthGraphicsUtils accGu;
    private boolean alignAcceleratorText;
    private int maxAccOrArrowWidth;
    
    public SynthMenuItemLayoutHelper(final SynthContext context, final SynthContext accContext, final JMenuItem menuItem, final Icon icon, final Icon icon2, final Rectangle rectangle, final int n, final String s, final boolean b, final boolean b2, final String s2) {
        this.context = context;
        this.accContext = accContext;
        this.style = context.getStyle();
        this.accStyle = accContext.getStyle();
        this.gu = this.style.getGraphicsUtils(context);
        this.accGu = this.accStyle.getGraphicsUtils(accContext);
        this.alignAcceleratorText = this.getAlignAcceleratorText(s2);
        this.reset(menuItem, icon, icon2, rectangle, n, s, b, this.style.getFont(context), this.accStyle.getFont(accContext), b2, s2);
        this.setLeadingGap(0);
    }
    
    private boolean getAlignAcceleratorText(final String s) {
        return this.style.getBoolean(this.context, s + ".alignAcceleratorText", true);
    }
    
    @Override
    protected void calcWidthsAndHeights() {
        if (this.getIcon() != null) {
            this.getIconSize().setWidth(SynthIcon.getIconWidth(this.getIcon(), this.context));
            this.getIconSize().setHeight(SynthIcon.getIconHeight(this.getIcon(), this.context));
        }
        if (!this.getAccText().equals("")) {
            this.getAccSize().setWidth(this.accGu.computeStringWidth(this.getAccContext(), this.getAccFontMetrics().getFont(), this.getAccFontMetrics(), this.getAccText()));
            this.getAccSize().setHeight(this.getAccFontMetrics().getHeight());
        }
        if (this.getText() == null) {
            this.setText("");
        }
        else if (!this.getText().equals("")) {
            if (this.getHtmlView() != null) {
                this.getTextSize().setWidth((int)this.getHtmlView().getPreferredSpan(0));
                this.getTextSize().setHeight((int)this.getHtmlView().getPreferredSpan(1));
            }
            else {
                this.getTextSize().setWidth(this.gu.computeStringWidth(this.context, this.getFontMetrics().getFont(), this.getFontMetrics(), this.getText()));
                this.getTextSize().setHeight(this.getFontMetrics().getHeight());
            }
        }
        if (this.useCheckAndArrow()) {
            if (this.getCheckIcon() != null) {
                this.getCheckSize().setWidth(SynthIcon.getIconWidth(this.getCheckIcon(), this.context));
                this.getCheckSize().setHeight(SynthIcon.getIconHeight(this.getCheckIcon(), this.context));
            }
            if (this.getArrowIcon() != null) {
                this.getArrowSize().setWidth(SynthIcon.getIconWidth(this.getArrowIcon(), this.context));
                this.getArrowSize().setHeight(SynthIcon.getIconHeight(this.getArrowIcon(), this.context));
            }
        }
        if (this.isColumnLayout()) {
            this.getLabelSize().setWidth(this.getIconSize().getWidth() + this.getTextSize().getWidth() + this.getGap());
            this.getLabelSize().setHeight(MenuItemLayoutHelper.max(this.getCheckSize().getHeight(), this.getIconSize().getHeight(), this.getTextSize().getHeight(), this.getAccSize().getHeight(), this.getArrowSize().getHeight()));
        }
        else {
            final Rectangle rectangle = new Rectangle();
            final Rectangle rectangle2 = new Rectangle();
            this.gu.layoutText(this.context, this.getFontMetrics(), this.getText(), this.getIcon(), this.getHorizontalAlignment(), this.getVerticalAlignment(), this.getHorizontalTextPosition(), this.getVerticalTextPosition(), this.getViewRect(), rectangle2, rectangle, this.getGap());
            final Rectangle rectangle3 = rectangle;
            rectangle3.width += this.getLeftTextExtraWidth();
            final Rectangle union = rectangle2.union(rectangle);
            this.getLabelSize().setHeight(union.height);
            this.getLabelSize().setWidth(union.width);
        }
    }
    
    @Override
    protected void calcMaxWidths() {
        this.calcMaxWidth(this.getCheckSize(), SynthMenuItemLayoutHelper.MAX_CHECK_WIDTH);
        this.maxAccOrArrowWidth = this.calcMaxValue(SynthMenuItemLayoutHelper.MAX_ACC_OR_ARROW_WIDTH, this.getArrowSize().getWidth());
        this.maxAccOrArrowWidth = this.calcMaxValue(SynthMenuItemLayoutHelper.MAX_ACC_OR_ARROW_WIDTH, this.getAccSize().getWidth());
        if (this.isColumnLayout()) {
            this.calcMaxWidth(this.getIconSize(), SynthMenuItemLayoutHelper.MAX_ICON_WIDTH);
            this.calcMaxWidth(this.getTextSize(), SynthMenuItemLayoutHelper.MAX_TEXT_WIDTH);
            int gap = this.getGap();
            if (this.getIconSize().getMaxWidth() == 0 || this.getTextSize().getMaxWidth() == 0) {
                gap = 0;
            }
            this.getLabelSize().setMaxWidth(this.calcMaxValue(SynthMenuItemLayoutHelper.MAX_LABEL_WIDTH, this.getIconSize().getMaxWidth() + this.getTextSize().getMaxWidth() + gap));
        }
        else {
            this.getIconSize().setMaxWidth(this.getParentIntProperty(SynthMenuItemLayoutHelper.MAX_ICON_WIDTH));
            this.calcMaxWidth(this.getLabelSize(), SynthMenuItemLayoutHelper.MAX_LABEL_WIDTH);
            int n = this.getLabelSize().getMaxWidth() - this.getIconSize().getMaxWidth();
            if (this.getIconSize().getMaxWidth() > 0) {
                n -= this.getGap();
            }
            this.getTextSize().setMaxWidth(this.calcMaxValue(SynthMenuItemLayoutHelper.MAX_TEXT_WIDTH, n));
        }
    }
    
    public SynthContext getContext() {
        return this.context;
    }
    
    public SynthContext getAccContext() {
        return this.accContext;
    }
    
    public SynthStyle getStyle() {
        return this.style;
    }
    
    public SynthStyle getAccStyle() {
        return this.accStyle;
    }
    
    public SynthGraphicsUtils getGraphicsUtils() {
        return this.gu;
    }
    
    public SynthGraphicsUtils getAccGraphicsUtils() {
        return this.accGu;
    }
    
    public boolean alignAcceleratorText() {
        return this.alignAcceleratorText;
    }
    
    public int getMaxAccOrArrowWidth() {
        return this.maxAccOrArrowWidth;
    }
    
    @Override
    protected void prepareForLayout(final LayoutResult layoutResult) {
        layoutResult.getCheckRect().width = this.getCheckSize().getMaxWidth();
        if (this.useCheckAndArrow() && !"".equals(this.getAccText())) {
            layoutResult.getAccRect().width = this.maxAccOrArrowWidth;
        }
        else {
            layoutResult.getArrowRect().width = this.maxAccOrArrowWidth;
        }
    }
    
    @Override
    public ColumnAlignment getLTRColumnAlignment() {
        if (this.alignAcceleratorText()) {
            return SynthMenuItemLayoutHelper.LTR_ALIGNMENT_2;
        }
        return SynthMenuItemLayoutHelper.LTR_ALIGNMENT_1;
    }
    
    @Override
    public ColumnAlignment getRTLColumnAlignment() {
        if (this.alignAcceleratorText()) {
            return SynthMenuItemLayoutHelper.RTL_ALIGNMENT_2;
        }
        return SynthMenuItemLayoutHelper.RTL_ALIGNMENT_1;
    }
    
    @Override
    protected void layoutIconAndTextInLabelRect(final LayoutResult layoutResult) {
        layoutResult.setTextRect(new Rectangle());
        layoutResult.setIconRect(new Rectangle());
        this.gu.layoutText(this.context, this.getFontMetrics(), this.getText(), this.getIcon(), this.getHorizontalAlignment(), this.getVerticalAlignment(), this.getHorizontalTextPosition(), this.getVerticalTextPosition(), layoutResult.getLabelRect(), layoutResult.getIconRect(), layoutResult.getTextRect(), this.getGap());
    }
    
    static {
        MAX_ACC_OR_ARROW_WIDTH = new StringUIClientPropertyKey("maxAccOrArrowWidth");
        LTR_ALIGNMENT_1 = new ColumnAlignment(2, 2, 2, 4, 4);
        LTR_ALIGNMENT_2 = new ColumnAlignment(2, 2, 2, 2, 4);
        RTL_ALIGNMENT_1 = new ColumnAlignment(4, 4, 4, 2, 2);
        RTL_ALIGNMENT_2 = new ColumnAlignment(4, 4, 4, 4, 2);
    }
}
