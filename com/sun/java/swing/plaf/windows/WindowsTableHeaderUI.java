package com.sun.java.swing.plaf.windows;

import javax.swing.table.TableColumn;
import java.awt.Dimension;
import sun.swing.SwingUtilities2;
import java.awt.Graphics;
import javax.swing.border.Border;
import javax.swing.SortOrder;
import java.awt.Insets;
import javax.swing.border.EmptyBorder;
import javax.swing.UIManager;
import javax.swing.Icon;
import java.awt.Component;
import javax.swing.JTable;
import sun.swing.table.DefaultTableCellHeaderRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.table.TableCellRenderer;
import javax.swing.plaf.basic.BasicTableHeaderUI;

public class WindowsTableHeaderUI extends BasicTableHeaderUI
{
    private TableCellRenderer originalHeaderRenderer;
    
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsTableHeaderUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        if (XPStyle.getXP() != null) {
            this.originalHeaderRenderer = this.header.getDefaultRenderer();
            if (this.originalHeaderRenderer instanceof UIResource) {
                this.header.setDefaultRenderer(new XPDefaultRenderer());
            }
        }
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        if (this.header.getDefaultRenderer() instanceof XPDefaultRenderer) {
            this.header.setDefaultRenderer(this.originalHeaderRenderer);
        }
        super.uninstallUI(component);
    }
    
    @Override
    protected void rolloverColumnUpdated(final int n, final int n2) {
        if (XPStyle.getXP() != null) {
            this.header.repaint(this.header.getHeaderRect(n));
            this.header.repaint(this.header.getHeaderRect(n2));
        }
    }
    
    private class XPDefaultRenderer extends DefaultTableCellHeaderRenderer
    {
        XPStyle.Skin skin;
        boolean isSelected;
        boolean hasFocus;
        boolean hasRollover;
        int column;
        
        XPDefaultRenderer() {
            this.setHorizontalAlignment(10);
        }
        
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object o, final boolean isSelected, final boolean hasFocus, final int n, final int column) {
            super.getTableCellRendererComponent(table, o, isSelected, hasFocus, n, column);
            this.isSelected = isSelected;
            this.hasFocus = hasFocus;
            this.column = column;
            this.hasRollover = (column == BasicTableHeaderUI.this.getRolloverColumn());
            if (this.skin == null) {
                final XPStyle xp = XPStyle.getXP();
                this.skin = ((xp != null) ? xp.getSkin(WindowsTableHeaderUI.this.header, TMSchema.Part.HP_HEADERITEM) : null);
            }
            final Insets insets = (this.skin != null) ? this.skin.getContentMargin() : null;
            int top = 0;
            int left = 0;
            int bottom = 0;
            int right = 0;
            if (insets != null) {
                top = insets.top;
                left = insets.left;
                bottom = insets.bottom;
                right = insets.right;
            }
            left += 5;
            bottom += 4;
            right += 5;
            final Icon icon;
            Border border;
            if (WindowsLookAndFeel.isOnVista() && ((icon = this.getIcon()) instanceof UIResource || icon == null)) {
                ++top;
                this.setIcon(null);
                Icon icon2 = null;
                final SortOrder columnSortOrder = DefaultTableCellHeaderRenderer.getColumnSortOrder(table, column);
                if (columnSortOrder != null) {
                    switch (columnSortOrder) {
                        case ASCENDING: {
                            icon2 = UIManager.getIcon("Table.ascendingSortIcon");
                            break;
                        }
                        case DESCENDING: {
                            icon2 = UIManager.getIcon("Table.descendingSortIcon");
                            break;
                        }
                    }
                }
                if (icon2 != null) {
                    border = new IconBorder(icon2, top, left, icon2.getIconHeight(), right);
                }
                else {
                    final Icon icon3 = UIManager.getIcon("Table.ascendingSortIcon");
                    final int n2 = (icon3 != null) ? icon3.getIconHeight() : 0;
                    if (n2 != 0) {
                        bottom = n2;
                    }
                    border = new EmptyBorder(n2 + top, left, bottom, right);
                }
            }
            else {
                top += 3;
                border = new EmptyBorder(top, left, bottom, right);
            }
            this.setBorder(border);
            return this;
        }
        
        @Override
        public void paint(final Graphics graphics) {
            final Dimension size = this.getSize();
            TMSchema.State state = TMSchema.State.NORMAL;
            final TableColumn draggedColumn = WindowsTableHeaderUI.this.header.getDraggedColumn();
            if (draggedColumn != null && this.column == SwingUtilities2.convertColumnIndexToView(WindowsTableHeaderUI.this.header.getColumnModel(), draggedColumn.getModelIndex())) {
                state = TMSchema.State.PRESSED;
            }
            else if (this.isSelected || this.hasFocus || this.hasRollover) {
                state = TMSchema.State.HOT;
            }
            Label_0205: {
                if (WindowsLookAndFeel.isOnVista()) {
                    final SortOrder columnSortOrder = DefaultTableCellHeaderRenderer.getColumnSortOrder(WindowsTableHeaderUI.this.header.getTable(), this.column);
                    if (columnSortOrder != null) {
                        switch (columnSortOrder) {
                            case ASCENDING:
                            case DESCENDING: {
                                switch (state) {
                                    case NORMAL: {
                                        state = TMSchema.State.SORTEDNORMAL;
                                        break Label_0205;
                                    }
                                    case PRESSED: {
                                        state = TMSchema.State.SORTEDPRESSED;
                                        break Label_0205;
                                    }
                                    case HOT: {
                                        state = TMSchema.State.SORTEDHOT;
                                        break Label_0205;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
            this.skin.paintSkin(graphics, 0, 0, size.width - 1, size.height - 1, state);
            super.paint(graphics);
        }
    }
    
    private static class IconBorder implements Border, UIResource
    {
        private final Icon icon;
        private final int top;
        private final int left;
        private final int bottom;
        private final int right;
        
        public IconBorder(final Icon icon, final int top, final int left, final int bottom, final int right) {
            this.icon = icon;
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
        }
        
        @Override
        public Insets getBorderInsets(final Component component) {
            return new Insets(this.icon.getIconHeight() + this.top, this.left, this.bottom, this.right);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            this.icon.paintIcon(component, graphics, n + this.left + (n3 - this.left - this.right - this.icon.getIconWidth()) / 2, n2 + this.top);
        }
    }
}
