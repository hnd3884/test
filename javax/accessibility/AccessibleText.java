package javax.accessibility;

import javax.swing.text.AttributeSet;
import java.awt.Rectangle;
import java.awt.Point;

public interface AccessibleText
{
    public static final int CHARACTER = 1;
    public static final int WORD = 2;
    public static final int SENTENCE = 3;
    
    int getIndexAtPoint(final Point p0);
    
    Rectangle getCharacterBounds(final int p0);
    
    int getCharCount();
    
    int getCaretPosition();
    
    String getAtIndex(final int p0, final int p1);
    
    String getAfterIndex(final int p0, final int p1);
    
    String getBeforeIndex(final int p0, final int p1);
    
    AttributeSet getCharacterAttribute(final int p0);
    
    int getSelectionStart();
    
    int getSelectionEnd();
    
    String getSelectedText();
}
