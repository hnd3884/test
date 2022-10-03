package java.awt.dnd;

import java.awt.Point;
import java.awt.Insets;

public interface Autoscroll
{
    Insets getAutoscrollInsets();
    
    void autoscroll(final Point p0);
}
