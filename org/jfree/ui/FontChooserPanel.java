package org.jfree.ui;

import javax.swing.ListModel;
import java.awt.GridLayout;
import java.awt.Component;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Font;
import java.util.ResourceBundle;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;

public class FontChooserPanel extends JPanel
{
    public static final String[] SIZES;
    private JList fontlist;
    private JList sizelist;
    private JCheckBox bold;
    private JCheckBox italic;
    protected static ResourceBundle localizationResources;
    
    static {
        SIZES = new String[] { "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "28", "36", "48", "72" };
        FontChooserPanel.localizationResources = ResourceBundle.getBundle("org.jfree.ui.LocalizationBundle");
    }
    
    public FontChooserPanel(final Font font) {
        final GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final String[] fonts = g.getAvailableFontFamilyNames();
        this.setLayout(new BorderLayout());
        final JPanel right = new JPanel(new BorderLayout());
        final JPanel fontPanel = new JPanel(new BorderLayout());
        fontPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), FontChooserPanel.localizationResources.getString("Font")));
        this.fontlist = new JList((E[])fonts);
        final JScrollPane fontpane = new JScrollPane(this.fontlist);
        fontpane.setBorder(BorderFactory.createEtchedBorder());
        fontPanel.add(fontpane);
        this.add(fontPanel);
        final JPanel sizePanel = new JPanel(new BorderLayout());
        sizePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), FontChooserPanel.localizationResources.getString("Size")));
        this.sizelist = new JList((E[])FontChooserPanel.SIZES);
        final JScrollPane sizepane = new JScrollPane(this.sizelist);
        sizepane.setBorder(BorderFactory.createEtchedBorder());
        sizePanel.add(sizepane);
        final JPanel attributes = new JPanel(new GridLayout(1, 2));
        this.bold = new JCheckBox(FontChooserPanel.localizationResources.getString("Bold"));
        this.italic = new JCheckBox(FontChooserPanel.localizationResources.getString("Italic"));
        attributes.add(this.bold);
        attributes.add(this.italic);
        attributes.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), FontChooserPanel.localizationResources.getString("Attributes")));
        right.add(sizePanel, "Center");
        right.add(attributes, "South");
        this.add(right, "East");
        this.setSelectedFont(font);
    }
    
    public Font getSelectedFont() {
        return new Font(this.getSelectedName(), this.getSelectedStyle(), this.getSelectedSize());
    }
    
    public String getSelectedName() {
        return this.fontlist.getSelectedValue();
    }
    
    public int getSelectedSize() {
        final String selected = this.sizelist.getSelectedValue();
        if (selected != null) {
            return Integer.parseInt(selected);
        }
        return 10;
    }
    
    public int getSelectedStyle() {
        if (this.bold.isSelected() && this.italic.isSelected()) {
            return 3;
        }
        if (this.bold.isSelected()) {
            return 1;
        }
        if (this.italic.isSelected()) {
            return 2;
        }
        return 0;
    }
    
    public void setSelectedFont(final Font font) {
        if (font == null) {
            throw new NullPointerException();
        }
        this.bold.setSelected(font.isBold());
        this.italic.setSelected(font.isItalic());
        final String fontName = font.getName();
        ListModel model = this.fontlist.getModel();
        this.fontlist.clearSelection();
        for (int i = 0; i < model.getSize(); ++i) {
            if (fontName.equals(model.getElementAt(i))) {
                this.fontlist.setSelectedIndex(i);
                break;
            }
        }
        final String fontSize = String.valueOf(font.getSize());
        model = this.sizelist.getModel();
        this.sizelist.clearSelection();
        for (int j = 0; j < model.getSize(); ++j) {
            if (fontSize.equals(model.getElementAt(j))) {
                this.sizelist.setSelectedIndex(j);
                break;
            }
        }
    }
}
