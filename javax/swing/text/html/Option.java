package javax.swing.text.html;

import javax.swing.text.AttributeSet;
import java.io.Serializable;

public class Option implements Serializable
{
    private boolean selected;
    private String label;
    private AttributeSet attr;
    
    public Option(final AttributeSet set) {
        this.attr = set.copyAttributes();
        this.selected = (set.getAttribute(HTML.Attribute.SELECTED) != null);
    }
    
    public void setLabel(final String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public AttributeSet getAttributes() {
        return this.attr;
    }
    
    @Override
    public String toString() {
        return this.label;
    }
    
    protected void setSelection(final boolean selected) {
        this.selected = selected;
    }
    
    public boolean isSelected() {
        return this.selected;
    }
    
    public String getValue() {
        String label = (String)this.attr.getAttribute(HTML.Attribute.VALUE);
        if (label == null) {
            label = this.label;
        }
        return label;
    }
}
