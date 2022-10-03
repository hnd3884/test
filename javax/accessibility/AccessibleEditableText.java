package javax.accessibility;

import javax.swing.text.AttributeSet;

public interface AccessibleEditableText extends AccessibleText
{
    void setTextContents(final String p0);
    
    void insertTextAtIndex(final int p0, final String p1);
    
    String getTextRange(final int p0, final int p1);
    
    void delete(final int p0, final int p1);
    
    void cut(final int p0, final int p1);
    
    void paste(final int p0);
    
    void replaceText(final int p0, final int p1, final String p2);
    
    void selectText(final int p0, final int p1);
    
    void setAttributes(final int p0, final int p1, final AttributeSet p2);
}
