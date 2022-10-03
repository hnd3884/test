package com.adventnet.renderers;

import java.awt.Component;
import javax.swing.JTable;
import java.awt.Color;
import javax.swing.table.DefaultTableCellRenderer;

public class RowStripingRenderer extends DefaultTableCellRenderer
{
    private Color color1;
    private Color color2;
    
    public RowStripingRenderer(final Color color1, final Color color2) {
        this.color1 = color1;
        this.color2 = color2;
    }
    
    public void setStripColor1(final Color color1) {
        this.color1 = color1;
    }
    
    public Color getStripColor1() {
        return this.color1;
    }
    
    public Color getStripColor2() {
        return this.color2;
    }
    
    public void setStripColor2(final Color color2) {
        this.color2 = color2;
    }
    
    public Component getTableCellRendererComponent(final JTable table, final Object o, final boolean b, final boolean b2, final int n, final int n2) {
        final Component tableCellRendererComponent = super.getTableCellRendererComponent(table, o, b, b2, n, n2);
        if (table.isCellSelected(n, n2)) {
            return tableCellRendererComponent;
        }
        if (n % 2 == 0) {
            tableCellRendererComponent.setBackground(this.color2);
            return tableCellRendererComponent;
        }
        tableCellRendererComponent.setBackground(this.color1);
        return tableCellRendererComponent;
    }
}
