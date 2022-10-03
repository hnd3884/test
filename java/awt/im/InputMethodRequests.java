package java.awt.im;

import java.text.AttributedCharacterIterator;
import java.awt.Rectangle;
import java.awt.font.TextHitInfo;

public interface InputMethodRequests
{
    Rectangle getTextLocation(final TextHitInfo p0);
    
    TextHitInfo getLocationOffset(final int p0, final int p1);
    
    int getInsertPositionOffset();
    
    AttributedCharacterIterator getCommittedText(final int p0, final int p1, final AttributedCharacterIterator.Attribute[] p2);
    
    int getCommittedTextLength();
    
    AttributedCharacterIterator cancelLatestCommittedText(final AttributedCharacterIterator.Attribute[] p0);
    
    AttributedCharacterIterator getSelectedText(final AttributedCharacterIterator.Attribute[] p0);
}
