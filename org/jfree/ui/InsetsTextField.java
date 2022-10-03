package org.jfree.ui;

import java.awt.Insets;
import java.util.ResourceBundle;
import javax.swing.JTextField;

public class InsetsTextField extends JTextField
{
    protected static ResourceBundle localizationResources;
    
    static {
        InsetsTextField.localizationResources = ResourceBundle.getBundle("org.jfree.ui.LocalizationBundle");
    }
    
    public InsetsTextField(final Insets insets) {
        this.setInsets(insets);
        this.setEnabled(false);
    }
    
    public String formatInsetsString(Insets insets) {
        insets = ((insets == null) ? new Insets(0, 0, 0, 0) : insets);
        return String.valueOf(InsetsTextField.localizationResources.getString("T")) + insets.top + ", " + InsetsTextField.localizationResources.getString("L") + insets.left + ", " + InsetsTextField.localizationResources.getString("B") + insets.bottom + ", " + InsetsTextField.localizationResources.getString("R") + insets.right;
    }
    
    public void setInsets(final Insets insets) {
        this.setText(this.formatInsetsString(insets));
    }
}
