package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.UIManager;

class RecentSwatchPanel extends SwatchPanel
{
    @Override
    protected void initValues() {
        this.swatchSize = UIManager.getDimension("ColorChooser.swatchesRecentSwatchSize", this.getLocale());
        this.numSwatches = new Dimension(5, 7);
        this.gap = new Dimension(1, 1);
    }
    
    @Override
    protected void initColors() {
        final Color color = UIManager.getColor("ColorChooser.swatchesDefaultRecentColor", this.getLocale());
        final int n = this.numSwatches.width * this.numSwatches.height;
        this.colors = new Color[n];
        for (int i = 0; i < n; ++i) {
            this.colors[i] = color;
        }
    }
    
    public void setMostRecentColor(final Color color) {
        System.arraycopy(this.colors, 0, this.colors, 1, this.colors.length - 1);
        this.colors[0] = color;
        this.repaint();
    }
}
