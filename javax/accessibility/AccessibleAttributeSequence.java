package javax.accessibility;

import javax.swing.text.AttributeSet;

public class AccessibleAttributeSequence
{
    public int startIndex;
    public int endIndex;
    public AttributeSet attributes;
    
    public AccessibleAttributeSequence(final int startIndex, final int endIndex, final AttributeSet attributes) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.attributes = attributes;
    }
}
