package org.jfree.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import javax.swing.text.Document;
import java.awt.Insets;
import java.util.ResourceBundle;
import javax.swing.JTextField;
import javax.swing.JPanel;

public class InsetsChooserPanel extends JPanel
{
    private JTextField topValueEditor;
    private JTextField leftValueEditor;
    private JTextField bottomValueEditor;
    private JTextField rightValueEditor;
    protected static ResourceBundle localizationResources;
    
    static {
        InsetsChooserPanel.localizationResources = ResourceBundle.getBundle("org.jfree.ui.LocalizationBundle");
    }
    
    public InsetsChooserPanel() {
        this(new Insets(0, 0, 0, 0));
    }
    
    public InsetsChooserPanel(Insets current) {
        current = ((current == null) ? new Insets(0, 0, 0, 0) : current);
        this.topValueEditor = new JTextField(new IntegerDocument(), String.valueOf(current.top), 0);
        this.leftValueEditor = new JTextField(new IntegerDocument(), String.valueOf(current.left), 0);
        this.bottomValueEditor = new JTextField(new IntegerDocument(), String.valueOf(current.bottom), 0);
        this.rightValueEditor = new JTextField(new IntegerDocument(), String.valueOf(current.right), 0);
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder(InsetsChooserPanel.localizationResources.getString("Insets")));
        panel.add(new JLabel(InsetsChooserPanel.localizationResources.getString("Top")), new GridBagConstraints(1, 0, 3, 1, 0.0, 0.0, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(" "), new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 12, 0, 12), 8, 0));
        panel.add(this.topValueEditor, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(" "), new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 12, 0, 11), 8, 0));
        panel.add(new JLabel(InsetsChooserPanel.localizationResources.getString("Left")), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 4, 0, 4), 0, 0));
        panel.add(this.leftValueEditor, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(" "), new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, 10, 0, new Insets(0, 12, 0, 12), 8, 0));
        panel.add(this.rightValueEditor, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(InsetsChooserPanel.localizationResources.getString("Right")), new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0, 10, 0, new Insets(0, 4, 0, 4), 0, 0));
        panel.add(this.bottomValueEditor, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(InsetsChooserPanel.localizationResources.getString("Bottom")), new GridBagConstraints(1, 4, 3, 1, 0.0, 0.0, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        this.setLayout(new BorderLayout());
        this.add(panel, "Center");
    }
    
    public Insets getInsetsValue() {
        return new Insets(Math.abs(this.stringToInt(this.topValueEditor.getText())), Math.abs(this.stringToInt(this.leftValueEditor.getText())), Math.abs(this.stringToInt(this.bottomValueEditor.getText())), Math.abs(this.stringToInt(this.rightValueEditor.getText())));
    }
    
    public void removeNotify() {
        super.removeNotify();
        this.removeAll();
    }
    
    protected int stringToInt(String value) {
        value = value.trim();
        if (value.length() == 0) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        }
        catch (final NumberFormatException ex) {
            return 0;
        }
    }
}
