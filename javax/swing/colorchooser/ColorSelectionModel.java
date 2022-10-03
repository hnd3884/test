package javax.swing.colorchooser;

import javax.swing.event.ChangeListener;
import java.awt.Color;

public interface ColorSelectionModel
{
    Color getSelectedColor();
    
    void setSelectedColor(final Color p0);
    
    void addChangeListener(final ChangeListener p0);
    
    void removeChangeListener(final ChangeListener p0);
}
