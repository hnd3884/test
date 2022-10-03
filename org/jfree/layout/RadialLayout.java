package org.jfree.layout;

import java.awt.Checkbox;
import java.awt.Panel;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Container;
import java.awt.Component;
import java.io.Serializable;
import java.awt.LayoutManager;

public class RadialLayout implements LayoutManager, Serializable
{
    private static final long serialVersionUID = -7582156799248315534L;
    private int minWidth;
    private int minHeight;
    private int maxCompWidth;
    private int maxCompHeight;
    private int preferredWidth;
    private int preferredHeight;
    private boolean sizeUnknown;
    
    public RadialLayout() {
        this.minWidth = 0;
        this.minHeight = 0;
        this.maxCompWidth = 0;
        this.maxCompHeight = 0;
        this.preferredWidth = 0;
        this.preferredHeight = 0;
        this.sizeUnknown = true;
    }
    
    public void addLayoutComponent(final Component comp) {
    }
    
    public void addLayoutComponent(final String name, final Component comp) {
    }
    
    public void layoutContainer(final Container parent) {
        final Insets insets = parent.getInsets();
        final int maxWidth = parent.getSize().width - (insets.left + insets.right);
        final int maxHeight = parent.getSize().height - (insets.top + insets.bottom);
        final int nComps = parent.getComponentCount();
        int x = 0;
        int y = 0;
        if (this.sizeUnknown) {
            this.setSizes(parent);
        }
        if (nComps < 2) {
            final Component c = parent.getComponent(0);
            if (c.isVisible()) {
                final Dimension d = c.getPreferredSize();
                c.setBounds(x, y, d.width, d.height);
            }
        }
        else {
            double radialCurrent = Math.toRadians(90.0);
            final double radialIncrement = 6.283185307179586 / nComps;
            final int midX = maxWidth / 2;
            final int midY = maxHeight / 2;
            final int a = midX - this.maxCompWidth;
            final int b = midY - this.maxCompHeight;
            for (int i = 0; i < nComps; ++i) {
                final Component c2 = parent.getComponent(i);
                if (c2.isVisible()) {
                    final Dimension d2 = c2.getPreferredSize();
                    x = (int)(midX - a * Math.cos(radialCurrent) - d2.getWidth() / 2.0 + insets.left);
                    y = (int)(midY - b * Math.sin(radialCurrent) - d2.getHeight() / 2.0 + insets.top);
                    c2.setBounds(x, y, d2.width, d2.height);
                }
                radialCurrent += radialIncrement;
            }
        }
    }
    
    public static void main(final String[] args) throws Exception {
        final Frame frame = new Frame();
        final Panel panel = new Panel();
        panel.setLayout(new RadialLayout());
        panel.add(new Checkbox("One"));
        panel.add(new Checkbox("Two"));
        panel.add(new Checkbox("Three"));
        panel.add(new Checkbox("Four"));
        panel.add(new Checkbox("Five"));
        panel.add(new Checkbox("One"));
        panel.add(new Checkbox("Two"));
        panel.add(new Checkbox("Three"));
        panel.add(new Checkbox("Four"));
        panel.add(new Checkbox("Five"));
        frame.add(panel);
        frame.setSize(300, 500);
        frame.setVisible(true);
    }
    
    public Dimension minimumLayoutSize(final Container parent) {
        final Dimension dim = new Dimension(0, 0);
        final Insets insets = parent.getInsets();
        dim.width = this.minWidth + insets.left + insets.right;
        dim.height = this.minHeight + insets.top + insets.bottom;
        this.sizeUnknown = false;
        return dim;
    }
    
    public Dimension preferredLayoutSize(final Container parent) {
        final Dimension dim = new Dimension(0, 0);
        this.setSizes(parent);
        final Insets insets = parent.getInsets();
        dim.width = this.preferredWidth + insets.left + insets.right;
        dim.height = this.preferredHeight + insets.top + insets.bottom;
        this.sizeUnknown = false;
        return dim;
    }
    
    public void removeLayoutComponent(final Component comp) {
    }
    
    public void removeLayoutComponent(final String name, final Component comp) {
    }
    
    private void setSizes(final Container parent) {
        final int nComps = parent.getComponentCount();
        this.preferredWidth = 0;
        this.preferredHeight = 0;
        this.minWidth = 0;
        this.minHeight = 0;
        for (int i = 0; i < nComps; ++i) {
            final Component c = parent.getComponent(i);
            if (c.isVisible()) {
                final Dimension d = c.getPreferredSize();
                if (this.maxCompWidth < d.width) {
                    this.maxCompWidth = d.width;
                }
                if (this.maxCompHeight < d.height) {
                    this.maxCompHeight = d.height;
                }
                this.preferredWidth += d.width;
                this.preferredHeight += d.height;
            }
        }
        this.preferredWidth /= 2;
        this.preferredHeight /= 2;
        this.minWidth = this.preferredWidth;
        this.minHeight = this.preferredHeight;
    }
    
    public String toString() {
        return this.getClass().getName();
    }
}
