package javax.swing.text;

import java.awt.Font;
import java.awt.Color;

public interface StyledDocument extends Document
{
    Style addStyle(final String p0, final Style p1);
    
    void removeStyle(final String p0);
    
    Style getStyle(final String p0);
    
    void setCharacterAttributes(final int p0, final int p1, final AttributeSet p2, final boolean p3);
    
    void setParagraphAttributes(final int p0, final int p1, final AttributeSet p2, final boolean p3);
    
    void setLogicalStyle(final int p0, final Style p1);
    
    Style getLogicalStyle(final int p0);
    
    Element getParagraphElement(final int p0);
    
    Element getCharacterElement(final int p0);
    
    Color getForeground(final AttributeSet p0);
    
    Color getBackground(final AttributeSet p0);
    
    Font getFont(final AttributeSet p0);
}
