package org.jfree.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.table.TableColumn;
import java.awt.Component;
import javax.swing.JScrollPane;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.table.TableModel;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Window;
import java.awt.Dialog;
import org.jfree.util.Log;
import org.jfree.util.LogContext;

public abstract class RefineryUtilities
{
    protected static final LogContext logger;
    static /* synthetic */ Class class$org$jfree$ui$RefineryUtilities;
    static /* synthetic */ Class class$java$lang$Number;
    
    static {
        logger = Log.createContext((RefineryUtilities.class$org$jfree$ui$RefineryUtilities != null) ? RefineryUtilities.class$org$jfree$ui$RefineryUtilities : (RefineryUtilities.class$org$jfree$ui$RefineryUtilities = class$("org.jfree.ui.RefineryUtilities")));
    }
    
    public static void centerDialogInParent(final Dialog dialog) {
        positionDialogRelativeToParent(dialog, 0.5, 0.5);
    }
    
    public static void centerFrameOnScreen(final Window frame) {
        positionFrameOnScreen(frame, 0.5, 0.5);
    }
    
    static /* synthetic */ Class class$(final String class$) {
        try {
            return Class.forName(class$);
        }
        catch (final ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }
    
    public static JButton createJButton(final String label, final Font font) {
        final JButton result = new JButton(label);
        result.setFont(font);
        return result;
    }
    
    public static JLabel createJLabel(final String text, final Font font) {
        final JLabel result = new JLabel(text);
        result.setFont(font);
        return result;
    }
    
    public static JLabel createJLabel(final String text, final Font font, final Color color) {
        final JLabel result = new JLabel(text);
        result.setFont(font);
        result.setForeground(color);
        return result;
    }
    
    public static JPanel createTablePanel(final TableModel model) {
        final JPanel panel = new JPanel(new BorderLayout());
        final JTable table = new JTable(model);
        for (int columnIndex = 0; columnIndex < model.getColumnCount(); ++columnIndex) {
            final TableColumn column = table.getColumnModel().getColumn(columnIndex);
            final Class c = model.getColumnClass(columnIndex);
            if (c.equals((RefineryUtilities.class$java$lang$Number != null) ? RefineryUtilities.class$java$lang$Number : (RefineryUtilities.class$java$lang$Number = class$("java.lang.Number")))) {
                column.setCellRenderer(new NumberCellRenderer());
            }
        }
        panel.add(new JScrollPane(table));
        return panel;
    }
    
    public static void positionDialogRelativeToParent(final Dialog dialog, final double horizontalPercent, final double verticalPercent) {
        final Dimension d = dialog.getSize();
        final Container parent = dialog.getParent();
        final Dimension p = parent.getSize();
        final int baseX = parent.getX() - d.width;
        final int baseY = parent.getY() - d.height;
        final int w = d.width + p.width;
        final int h = d.height + p.height;
        int x = baseX + (int)(horizontalPercent * w);
        int y = baseY + (int)(verticalPercent * h);
        final Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        x = Math.min(x, s.width - d.width);
        x = Math.max(x, 0);
        y = Math.min(y, s.height - d.height);
        y = Math.max(y, 0);
        dialog.setBounds(x, y, d.width, d.height);
    }
    
    public static void positionFrameOnScreen(final Window frame, final double horizontalPercent, final double verticalPercent) {
        final Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        final Dimension f = frame.getSize();
        final int w = Math.max(s.width - f.width, 0);
        final int h = Math.max(s.height - f.height, 0);
        final int x = (int)(horizontalPercent * w);
        final int y = (int)(verticalPercent * h);
        frame.setBounds(x, y, f.width, f.height);
    }
    
    public static void positionFrameRandomly(final Window frame) {
        positionFrameOnScreen(frame, Math.random(), Math.random());
    }
}
