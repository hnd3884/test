package javax.swing;

import java.awt.event.ActionEvent;
import java.awt.Color;
import java.io.Serializable;
import java.awt.event.ActionListener;

class ColorTracker implements ActionListener, Serializable
{
    JColorChooser chooser;
    Color color;
    
    public ColorTracker(final JColorChooser chooser) {
        this.chooser = chooser;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        this.color = this.chooser.getColor();
    }
    
    public Color getColor() {
        return this.color;
    }
}
