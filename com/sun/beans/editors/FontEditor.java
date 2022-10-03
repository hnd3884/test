package com.sun.beans.editors;

import java.beans.PropertyChangeListener;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Event;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.LayoutManager;
import java.beans.PropertyChangeSupport;
import java.awt.Choice;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.Font;
import java.beans.PropertyEditor;
import java.awt.Panel;

public class FontEditor extends Panel implements PropertyEditor
{
    private static final long serialVersionUID = 6732704486002715933L;
    private Font font;
    private Toolkit toolkit;
    private String sampleText;
    private Label sample;
    private Choice familyChoser;
    private Choice styleChoser;
    private Choice sizeChoser;
    private String[] fonts;
    private String[] styleNames;
    private int[] styles;
    private int[] pointSizes;
    private PropertyChangeSupport support;
    
    public FontEditor() {
        this.sampleText = "Abcde...";
        this.styleNames = new String[] { "plain", "bold", "italic" };
        this.styles = new int[] { 0, 1, 2 };
        this.pointSizes = new int[] { 3, 5, 8, 10, 12, 14, 18, 24, 36, 48 };
        this.support = new PropertyChangeSupport(this);
        this.setLayout(null);
        this.toolkit = Toolkit.getDefaultToolkit();
        this.fonts = this.toolkit.getFontList();
        this.familyChoser = new Choice();
        for (int i = 0; i < this.fonts.length; ++i) {
            this.familyChoser.addItem(this.fonts[i]);
        }
        this.add(this.familyChoser);
        this.familyChoser.reshape(20, 5, 100, 30);
        this.styleChoser = new Choice();
        for (int j = 0; j < this.styleNames.length; ++j) {
            this.styleChoser.addItem(this.styleNames[j]);
        }
        this.add(this.styleChoser);
        this.styleChoser.reshape(145, 5, 70, 30);
        this.sizeChoser = new Choice();
        for (int k = 0; k < this.pointSizes.length; ++k) {
            this.sizeChoser.addItem("" + this.pointSizes[k]);
        }
        this.add(this.sizeChoser);
        this.sizeChoser.reshape(220, 5, 70, 30);
        this.resize(300, 40);
    }
    
    @Override
    public Dimension preferredSize() {
        return new Dimension(300, 40);
    }
    
    @Override
    public void setValue(final Object o) {
        this.font = (Font)o;
        if (this.font == null) {
            return;
        }
        this.changeFont(this.font);
        for (int i = 0; i < this.fonts.length; ++i) {
            if (this.fonts[i].equals(this.font.getFamily())) {
                this.familyChoser.select(i);
                break;
            }
        }
        for (int j = 0; j < this.styleNames.length; ++j) {
            if (this.font.getStyle() == this.styles[j]) {
                this.styleChoser.select(j);
                break;
            }
        }
        for (int k = 0; k < this.pointSizes.length; ++k) {
            if (this.font.getSize() <= this.pointSizes[k]) {
                this.sizeChoser.select(k);
                break;
            }
        }
    }
    
    private void changeFont(final Font font) {
        this.font = font;
        if (this.sample != null) {
            this.remove(this.sample);
        }
        (this.sample = new Label(this.sampleText)).setFont(this.font);
        this.add(this.sample);
        final Container parent = this.getParent();
        if (parent != null) {
            parent.invalidate();
            parent.layout();
        }
        this.invalidate();
        this.layout();
        this.repaint();
        this.support.firePropertyChange("", null, null);
    }
    
    @Override
    public Object getValue() {
        return this.font;
    }
    
    @Override
    public String getJavaInitializationString() {
        if (this.font == null) {
            return "null";
        }
        return "new java.awt.Font(\"" + this.font.getName() + "\", " + this.font.getStyle() + ", " + this.font.getSize() + ")";
    }
    
    @Override
    public boolean action(final Event event, final Object o) {
        final String selectedItem = this.familyChoser.getSelectedItem();
        final int n = this.styles[this.styleChoser.getSelectedIndex()];
        final int n2 = this.pointSizes[this.sizeChoser.getSelectedIndex()];
        try {
            this.changeFont(new Font(selectedItem, n, n2));
        }
        catch (final Exception ex) {
            System.err.println("Couldn't create font " + selectedItem + "-" + this.styleNames[n] + "-" + n2);
        }
        return false;
    }
    
    @Override
    public boolean isPaintable() {
        return true;
    }
    
    @Override
    public void paintValue(final Graphics graphics, final Rectangle rectangle) {
        final Font font = graphics.getFont();
        graphics.setFont(this.font);
        graphics.drawString(this.sampleText, 0, rectangle.height - (rectangle.height - graphics.getFontMetrics().getAscent()) / 2);
        graphics.setFont(font);
    }
    
    @Override
    public String getAsText() {
        if (this.font == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(this.font.getName());
        sb.append(' ');
        final boolean bold = this.font.isBold();
        if (bold) {
            sb.append("BOLD");
        }
        final boolean italic = this.font.isItalic();
        if (italic) {
            sb.append("ITALIC");
        }
        if (bold || italic) {
            sb.append(' ');
        }
        sb.append(this.font.getSize());
        return sb.toString();
    }
    
    @Override
    public void setAsText(final String s) throws IllegalArgumentException {
        this.setValue((s == null) ? null : Font.decode(s));
    }
    
    @Override
    public String[] getTags() {
        return null;
    }
    
    @Override
    public Component getCustomEditor() {
        return this;
    }
    
    @Override
    public boolean supportsCustomEditor() {
        return true;
    }
    
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        this.support.addPropertyChangeListener(propertyChangeListener);
    }
    
    @Override
    public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        this.support.removePropertyChangeListener(propertyChangeListener);
    }
}
