package javax.swing.colorchooser;

import javax.swing.UIManager;
import java.awt.Component;

class ColorModel
{
    private final String prefix;
    private final String[] labels;
    
    ColorModel(final String s, final String... labels) {
        this.prefix = "ColorChooser." + s;
        this.labels = labels;
    }
    
    ColorModel() {
        this("rgb", new String[] { "Red", "Green", "Blue", "Alpha" });
    }
    
    void setColor(final int n, final float[] array) {
        array[0] = normalize(n >> 16);
        array[1] = normalize(n >> 8);
        array[2] = normalize(n);
        array[3] = normalize(n >> 24);
    }
    
    int getColor(final float[] array) {
        return to8bit(array[2]) | to8bit(array[1]) << 8 | to8bit(array[0]) << 16 | to8bit(array[3]) << 24;
    }
    
    int getCount() {
        return this.labels.length;
    }
    
    int getMinimum(final int n) {
        return 0;
    }
    
    int getMaximum(final int n) {
        return 255;
    }
    
    float getDefault(final int n) {
        return 0.0f;
    }
    
    final String getLabel(final Component component, final int n) {
        return this.getText(component, this.labels[n]);
    }
    
    private static float normalize(final int n) {
        return (n & 0xFF) / 255.0f;
    }
    
    private static int to8bit(final float n) {
        return (int)(255.0f * n);
    }
    
    final String getText(final Component component, final String s) {
        return UIManager.getString(this.prefix + s + "Text", component.getLocale());
    }
    
    final int getInteger(final Component component, final String s) {
        final Object value = UIManager.get(this.prefix + s, component.getLocale());
        if (value instanceof Integer) {
            return (int)value;
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String)value);
            }
            catch (final NumberFormatException ex) {}
        }
        return -1;
    }
}
