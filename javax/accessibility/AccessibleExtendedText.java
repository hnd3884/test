package javax.accessibility;

import java.awt.Rectangle;

public interface AccessibleExtendedText
{
    public static final int LINE = 4;
    public static final int ATTRIBUTE_RUN = 5;
    
    String getTextRange(final int p0, final int p1);
    
    AccessibleTextSequence getTextSequenceAt(final int p0, final int p1);
    
    AccessibleTextSequence getTextSequenceAfter(final int p0, final int p1);
    
    AccessibleTextSequence getTextSequenceBefore(final int p0, final int p1);
    
    Rectangle getTextBounds(final int p0, final int p1);
}
