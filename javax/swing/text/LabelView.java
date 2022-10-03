package javax.swing.text;

import java.awt.Shape;
import javax.swing.event.DocumentEvent;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Font;

public class LabelView extends GlyphView implements TabableView
{
    private Font font;
    private Color fg;
    private Color bg;
    private boolean underline;
    private boolean strike;
    private boolean superscript;
    private boolean subscript;
    
    public LabelView(final Element element) {
        super(element);
    }
    
    final void sync() {
        if (this.font == null) {
            this.setPropertiesFromAttributes();
        }
    }
    
    protected void setUnderline(final boolean underline) {
        this.underline = underline;
    }
    
    protected void setStrikeThrough(final boolean strike) {
        this.strike = strike;
    }
    
    protected void setSuperscript(final boolean superscript) {
        this.superscript = superscript;
    }
    
    protected void setSubscript(final boolean subscript) {
        this.subscript = subscript;
    }
    
    protected void setBackground(final Color bg) {
        this.bg = bg;
    }
    
    protected void setPropertiesFromAttributes() {
        final AttributeSet attributes = this.getAttributes();
        if (attributes != null) {
            final Document document = this.getDocument();
            if (!(document instanceof StyledDocument)) {
                throw new StateInvariantError("LabelView needs StyledDocument");
            }
            final StyledDocument styledDocument = (StyledDocument)document;
            this.font = styledDocument.getFont(attributes);
            this.fg = styledDocument.getForeground(attributes);
            if (attributes.isDefined(StyleConstants.Background)) {
                this.bg = styledDocument.getBackground(attributes);
            }
            else {
                this.bg = null;
            }
            this.setUnderline(StyleConstants.isUnderline(attributes));
            this.setStrikeThrough(StyleConstants.isStrikeThrough(attributes));
            this.setSuperscript(StyleConstants.isSuperscript(attributes));
            this.setSubscript(StyleConstants.isSubscript(attributes));
        }
    }
    
    @Deprecated
    protected FontMetrics getFontMetrics() {
        this.sync();
        final Container container = this.getContainer();
        return (container != null) ? container.getFontMetrics(this.font) : Toolkit.getDefaultToolkit().getFontMetrics(this.font);
    }
    
    @Override
    public Color getBackground() {
        this.sync();
        return this.bg;
    }
    
    @Override
    public Color getForeground() {
        this.sync();
        return this.fg;
    }
    
    @Override
    public Font getFont() {
        this.sync();
        return this.font;
    }
    
    @Override
    public boolean isUnderline() {
        this.sync();
        return this.underline;
    }
    
    @Override
    public boolean isStrikeThrough() {
        this.sync();
        return this.strike;
    }
    
    @Override
    public boolean isSubscript() {
        this.sync();
        return this.subscript;
    }
    
    @Override
    public boolean isSuperscript() {
        this.sync();
        return this.superscript;
    }
    
    @Override
    public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.font = null;
        super.changedUpdate(documentEvent, shape, viewFactory);
    }
}
