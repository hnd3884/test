package javax.swing.colorchooser;

import javax.swing.UIManager;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.Icon;
import java.beans.PropertyChangeEvent;
import javax.swing.JColorChooser;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

public abstract class AbstractColorChooserPanel extends JPanel
{
    private final PropertyChangeListener enabledListener;
    private JColorChooser chooser;
    
    public AbstractColorChooserPanel() {
        this.enabledListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                final Object newValue = propertyChangeEvent.getNewValue();
                if (newValue instanceof Boolean) {
                    AbstractColorChooserPanel.this.setEnabled((boolean)newValue);
                }
            }
        };
    }
    
    public abstract void updateChooser();
    
    protected abstract void buildChooser();
    
    public abstract String getDisplayName();
    
    public int getMnemonic() {
        return 0;
    }
    
    public int getDisplayedMnemonicIndex() {
        return -1;
    }
    
    public abstract Icon getSmallDisplayIcon();
    
    public abstract Icon getLargeDisplayIcon();
    
    public void installChooserPanel(final JColorChooser chooser) {
        if (this.chooser != null) {
            throw new RuntimeException("This chooser panel is already installed");
        }
        (this.chooser = chooser).addPropertyChangeListener("enabled", this.enabledListener);
        this.setEnabled(this.chooser.isEnabled());
        this.buildChooser();
        this.updateChooser();
    }
    
    public void uninstallChooserPanel(final JColorChooser colorChooser) {
        this.chooser.removePropertyChangeListener("enabled", this.enabledListener);
        this.chooser = null;
    }
    
    public ColorSelectionModel getColorSelectionModel() {
        return (this.chooser != null) ? this.chooser.getSelectionModel() : null;
    }
    
    protected Color getColorFromModel() {
        final ColorSelectionModel colorSelectionModel = this.getColorSelectionModel();
        return (colorSelectionModel != null) ? colorSelectionModel.getSelectedColor() : null;
    }
    
    void setSelectedColor(final Color selectedColor) {
        final ColorSelectionModel colorSelectionModel = this.getColorSelectionModel();
        if (colorSelectionModel != null) {
            colorSelectionModel.setSelectedColor(selectedColor);
        }
    }
    
    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
    }
    
    int getInt(final Object o, final int n) {
        final Object value = UIManager.get(o, this.getLocale());
        if (value instanceof Integer) {
            return (int)value;
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String)value);
            }
            catch (final NumberFormatException ex) {}
        }
        return n;
    }
}
