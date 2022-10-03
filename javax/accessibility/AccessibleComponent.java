package javax.accessibility;

import java.awt.event.FocusListener;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Cursor;
import java.awt.Color;

public interface AccessibleComponent
{
    Color getBackground();
    
    void setBackground(final Color p0);
    
    Color getForeground();
    
    void setForeground(final Color p0);
    
    Cursor getCursor();
    
    void setCursor(final Cursor p0);
    
    Font getFont();
    
    void setFont(final Font p0);
    
    FontMetrics getFontMetrics(final Font p0);
    
    boolean isEnabled();
    
    void setEnabled(final boolean p0);
    
    boolean isVisible();
    
    void setVisible(final boolean p0);
    
    boolean isShowing();
    
    boolean contains(final Point p0);
    
    Point getLocationOnScreen();
    
    Point getLocation();
    
    void setLocation(final Point p0);
    
    Rectangle getBounds();
    
    void setBounds(final Rectangle p0);
    
    Dimension getSize();
    
    void setSize(final Dimension p0);
    
    Accessible getAccessibleAt(final Point p0);
    
    boolean isFocusTraversable();
    
    void requestFocus();
    
    void addFocusListener(final FocusListener p0);
    
    void removeFocusListener(final FocusListener p0);
}
