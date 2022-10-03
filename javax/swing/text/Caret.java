package javax.swing.text;

import java.awt.Point;
import javax.swing.event.ChangeListener;
import java.awt.Graphics;

public interface Caret
{
    void install(final JTextComponent p0);
    
    void deinstall(final JTextComponent p0);
    
    void paint(final Graphics p0);
    
    void addChangeListener(final ChangeListener p0);
    
    void removeChangeListener(final ChangeListener p0);
    
    boolean isVisible();
    
    void setVisible(final boolean p0);
    
    boolean isSelectionVisible();
    
    void setSelectionVisible(final boolean p0);
    
    void setMagicCaretPosition(final Point p0);
    
    Point getMagicCaretPosition();
    
    void setBlinkRate(final int p0);
    
    int getBlinkRate();
    
    int getDot();
    
    int getMark();
    
    void setDot(final int p0);
    
    void moveDot(final int p0);
}
