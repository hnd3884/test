package javax.swing.text.html;

import java.awt.Color;
import javax.swing.text.ViewFactory;
import java.awt.Shape;
import javax.swing.event.DocumentEvent;
import javax.swing.text.View;
import javax.swing.text.Element;
import javax.swing.text.AttributeSet;
import javax.swing.text.LabelView;

public class InlineView extends LabelView
{
    private boolean nowrap;
    private AttributeSet attr;
    
    public InlineView(final Element element) {
        super(element);
        this.attr = this.getStyleSheet().getViewAttributes(this);
    }
    
    @Override
    public void insertUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        super.insertUpdate(documentEvent, shape, viewFactory);
    }
    
    @Override
    public void removeUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        super.removeUpdate(documentEvent, shape, viewFactory);
    }
    
    @Override
    public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        super.changedUpdate(documentEvent, shape, viewFactory);
        this.attr = this.getStyleSheet().getViewAttributes(this);
        this.preferenceChanged(null, true, true);
    }
    
    @Override
    public AttributeSet getAttributes() {
        return this.attr;
    }
    
    @Override
    public int getBreakWeight(final int n, final float n2, final float n3) {
        if (this.nowrap) {
            return 0;
        }
        return super.getBreakWeight(n, n2, n3);
    }
    
    @Override
    public View breakView(final int n, final int n2, final float n3, final float n4) {
        return super.breakView(n, n2, n3, n4);
    }
    
    @Override
    protected void setPropertiesFromAttributes() {
        super.setPropertiesFromAttributes();
        final AttributeSet attributes = this.getAttributes();
        final Object attribute = attributes.getAttribute(CSS.Attribute.TEXT_DECORATION);
        this.setUnderline(attribute != null && attribute.toString().indexOf("underline") >= 0);
        this.setStrikeThrough(attribute != null && attribute.toString().indexOf("line-through") >= 0);
        final Object attribute2 = attributes.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
        this.setSuperscript(attribute2 != null && attribute2.toString().indexOf("sup") >= 0);
        this.setSubscript(attribute2 != null && attribute2.toString().indexOf("sub") >= 0);
        final Object attribute3 = attributes.getAttribute(CSS.Attribute.WHITE_SPACE);
        if (attribute3 != null && attribute3.equals("nowrap")) {
            this.nowrap = true;
        }
        else {
            this.nowrap = false;
        }
        final Color background = ((HTMLDocument)this.getDocument()).getBackground(attributes);
        if (background != null) {
            this.setBackground(background);
        }
    }
    
    protected StyleSheet getStyleSheet() {
        return ((HTMLDocument)this.getDocument()).getStyleSheet();
    }
}
