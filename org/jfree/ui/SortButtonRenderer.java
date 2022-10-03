package org.jfree.ui;

import javax.swing.JComponent;
import javax.swing.table.JTableHeader;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.border.Border;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.table.TableCellRenderer;

public class SortButtonRenderer implements TableCellRenderer
{
    public static final int NONE = 0;
    public static final int DOWN = 1;
    public static final int UP = 2;
    private int pressedColumn;
    private JButton normalButton;
    private JButton ascendingButton;
    private JButton descendingButton;
    private boolean useLabels;
    private JLabel normalLabel;
    private JLabel ascendingLabel;
    private JLabel descendingLabel;
    
    public SortButtonRenderer() {
        this.pressedColumn = -1;
        this.pressedColumn = -1;
        this.useLabels = UIManager.getLookAndFeel().getID().equals("Aqua");
        final Border border = UIManager.getBorder("TableHeader.cellBorder");
        if (this.useLabels) {
            (this.normalLabel = new JLabel()).setHorizontalAlignment(10);
            (this.ascendingLabel = new JLabel()).setHorizontalAlignment(10);
            this.ascendingLabel.setHorizontalTextPosition(2);
            this.ascendingLabel.setIcon(new BevelArrowIcon(1, false, false));
            (this.descendingLabel = new JLabel()).setHorizontalAlignment(10);
            this.descendingLabel.setHorizontalTextPosition(2);
            this.descendingLabel.setIcon(new BevelArrowIcon(0, false, false));
            this.normalLabel.setBorder(border);
            this.ascendingLabel.setBorder(border);
            this.descendingLabel.setBorder(border);
        }
        else {
            (this.normalButton = new JButton()).setMargin(new Insets(0, 0, 0, 0));
            this.normalButton.setHorizontalAlignment(10);
            (this.ascendingButton = new JButton()).setMargin(new Insets(0, 0, 0, 0));
            this.ascendingButton.setHorizontalAlignment(10);
            this.ascendingButton.setHorizontalTextPosition(2);
            this.ascendingButton.setIcon(new BevelArrowIcon(1, false, false));
            this.ascendingButton.setPressedIcon(new BevelArrowIcon(1, false, true));
            (this.descendingButton = new JButton()).setMargin(new Insets(0, 0, 0, 0));
            this.descendingButton.setHorizontalAlignment(10);
            this.descendingButton.setHorizontalTextPosition(2);
            this.descendingButton.setIcon(new BevelArrowIcon(0, false, false));
            this.descendingButton.setPressedIcon(new BevelArrowIcon(0, false, true));
            this.normalButton.setBorder(border);
            this.ascendingButton.setBorder(border);
            this.descendingButton.setBorder(border);
        }
    }
    
    private JButton getRendererButton(final boolean isSorting, final boolean isAscending) {
        if (!isSorting) {
            return this.normalButton;
        }
        if (isAscending) {
            return this.ascendingButton;
        }
        return this.descendingButton;
    }
    
    private JLabel getRendererLabel(final boolean isSorting, final boolean isAscending) {
        if (!isSorting) {
            return this.normalLabel;
        }
        if (isAscending) {
            return this.ascendingLabel;
        }
        return this.descendingLabel;
    }
    
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        if (table == null) {
            throw new NullPointerException("Table must not be null.");
        }
        final SortableTableModel model = (SortableTableModel)table.getModel();
        final int cc = table.convertColumnIndexToModel(column);
        final boolean isSorting = model.getSortingColumn() == cc;
        final boolean isAscending = model.isAscending();
        final JTableHeader header = table.getTableHeader();
        final boolean isPressed = cc == this.pressedColumn;
        JComponent component;
        if (this.useLabels) {
            final JLabel label = this.getRendererLabel(isSorting, isAscending);
            label.setText((value == null) ? "" : value.toString());
            component = label;
        }
        else {
            final JButton button = this.getRendererButton(isSorting, isAscending);
            button.setText((value == null) ? "" : value.toString());
            button.getModel().setPressed(isPressed);
            button.getModel().setArmed(isPressed);
            component = button;
        }
        if (header != null) {
            component.setForeground(header.getForeground());
            component.setBackground(header.getBackground());
            component.setFont(header.getFont());
        }
        return component;
    }
    
    public void setPressedColumn(final int column) {
        this.pressedColumn = column;
    }
}
