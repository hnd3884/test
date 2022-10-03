package com.sun.beans.editors;

import java.beans.PropertyChangeListener;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Event;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.LayoutManager;
import java.beans.PropertyChangeSupport;
import java.awt.Choice;
import java.awt.TextField;
import java.awt.Canvas;
import java.awt.Color;
import java.beans.PropertyEditor;
import java.awt.Panel;

public class ColorEditor extends Panel implements PropertyEditor
{
    private static final long serialVersionUID = 1781257185164716054L;
    private String[] colorNames;
    private Color[] colors;
    private Canvas sample;
    private int sampleHeight;
    private int sampleWidth;
    private int hPad;
    private int ourWidth;
    private Color color;
    private TextField text;
    private Choice choser;
    private PropertyChangeSupport support;
    
    public ColorEditor() {
        this.colorNames = new String[] { " ", "white", "lightGray", "gray", "darkGray", "black", "red", "pink", "orange", "yellow", "green", "magenta", "cyan", "blue" };
        this.colors = new Color[] { null, Color.white, Color.lightGray, Color.gray, Color.darkGray, Color.black, Color.red, Color.pink, Color.orange, Color.yellow, Color.green, Color.magenta, Color.cyan, Color.blue };
        this.sampleHeight = 20;
        this.sampleWidth = 40;
        this.hPad = 5;
        this.support = new PropertyChangeSupport(this);
        this.setLayout(null);
        this.ourWidth = this.hPad;
        final Panel panel = new Panel();
        panel.setLayout(null);
        panel.setBackground(Color.black);
        panel.add(this.sample = new Canvas());
        this.sample.reshape(2, 2, this.sampleWidth, this.sampleHeight);
        this.add(panel);
        panel.reshape(this.ourWidth, 2, this.sampleWidth + 4, this.sampleHeight + 4);
        this.ourWidth += this.sampleWidth + 4 + this.hPad;
        this.add(this.text = new TextField("", 14));
        this.text.reshape(this.ourWidth, 0, 100, 30);
        this.ourWidth += 100 + this.hPad;
        this.choser = new Choice();
        for (int i = 0; i < this.colorNames.length; ++i) {
            this.choser.addItem(this.colorNames[i]);
        }
        this.add(this.choser);
        this.choser.reshape(this.ourWidth, 0, 100, 30);
        this.resize(this.ourWidth += 100 + this.hPad, 40);
    }
    
    @Override
    public void setValue(final Object o) {
        this.changeColor((Color)o);
    }
    
    @Override
    public Dimension preferredSize() {
        return new Dimension(this.ourWidth, 40);
    }
    
    @Override
    public boolean keyUp(final Event event, final int n) {
        if (event.target == this.text) {
            try {
                this.setAsText(this.text.getText());
            }
            catch (final IllegalArgumentException ex) {}
        }
        return false;
    }
    
    @Override
    public void setAsText(final String s) throws IllegalArgumentException {
        if (s == null) {
            this.changeColor(null);
            return;
        }
        final int index = s.indexOf(44);
        final int index2 = s.indexOf(44, index + 1);
        if (index < 0 || index2 < 0) {
            throw new IllegalArgumentException(s);
        }
        try {
            this.changeColor(new Color(Integer.parseInt(s.substring(0, index)), Integer.parseInt(s.substring(index + 1, index2)), Integer.parseInt(s.substring(index2 + 1))));
        }
        catch (final Exception ex) {
            throw new IllegalArgumentException(s);
        }
    }
    
    @Override
    public boolean action(final Event event, final Object o) {
        if (event.target == this.choser) {
            this.changeColor(this.colors[this.choser.getSelectedIndex()]);
        }
        return false;
    }
    
    @Override
    public String getJavaInitializationString() {
        return (this.color != null) ? ("new java.awt.Color(" + this.color.getRGB() + ",true)") : "null";
    }
    
    private void changeColor(final Color color) {
        if (color == null) {
            this.color = null;
            this.text.setText("");
            return;
        }
        this.color = color;
        this.text.setText("" + color.getRed() + "," + color.getGreen() + "," + color.getBlue());
        int n = 0;
        for (int i = 0; i < this.colorNames.length; ++i) {
            if (this.color.equals(this.colors[i])) {
                n = i;
            }
        }
        this.choser.select(n);
        this.sample.setBackground(this.color);
        this.sample.repaint();
        this.support.firePropertyChange("", null, null);
    }
    
    @Override
    public Object getValue() {
        return this.color;
    }
    
    @Override
    public boolean isPaintable() {
        return true;
    }
    
    @Override
    public void paintValue(final Graphics graphics, final Rectangle rectangle) {
        final Color color = graphics.getColor();
        graphics.setColor(Color.black);
        graphics.drawRect(rectangle.x, rectangle.y, rectangle.width - 3, rectangle.height - 3);
        graphics.setColor(this.color);
        graphics.fillRect(rectangle.x + 1, rectangle.y + 1, rectangle.width - 4, rectangle.height - 4);
        graphics.setColor(color);
    }
    
    @Override
    public String getAsText() {
        return (this.color != null) ? (this.color.getRed() + "," + this.color.getGreen() + "," + this.color.getBlue()) : null;
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
