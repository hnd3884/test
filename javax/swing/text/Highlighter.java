package javax.swing.text;

import java.awt.Shape;
import java.awt.Graphics;

public interface Highlighter
{
    void install(final JTextComponent p0);
    
    void deinstall(final JTextComponent p0);
    
    void paint(final Graphics p0);
    
    Object addHighlight(final int p0, final int p1, final HighlightPainter p2) throws BadLocationException;
    
    void removeHighlight(final Object p0);
    
    void removeAllHighlights();
    
    void changeHighlight(final Object p0, final int p1, final int p2) throws BadLocationException;
    
    Highlight[] getHighlights();
    
    public interface Highlight
    {
        int getStartOffset();
        
        int getEndOffset();
        
        HighlightPainter getPainter();
    }
    
    public interface HighlightPainter
    {
        void paint(final Graphics p0, final int p1, final int p2, final Shape p3, final JTextComponent p4);
    }
}
