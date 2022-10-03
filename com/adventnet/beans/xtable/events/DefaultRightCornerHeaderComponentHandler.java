package com.adventnet.beans.xtable.events;

import com.adventnet.beans.xtable.XTableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.adventnet.beans.xtable.ToggleMenuItem;
import java.awt.Component;
import com.adventnet.beans.xtable.XTableHeader;
import com.adventnet.beans.xtable.XTable;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class DefaultRightCornerHeaderComponentHandler implements RightCornerHeaderComponentActionListener
{
    private JPopupMenu p;
    private JMenuItem mi;
    private RightCornerHeaderComponentActionEvent rccae;
    
    public DefaultRightCornerHeaderComponentHandler() {
        (this.p = new JPopupMenu()).setBorder(new BevelBorder(0));
    }
    
    public void rightCornerHeaderComponentInvoked(final RightCornerHeaderComponentActionEvent rightCornerHeaderComponentActionEvent) {
        final XTable xTable = (XTable)((XTableHeader)rightCornerHeaderComponentActionEvent.getSource()).getTable();
        if (!this.p.isVisible()) {
            this.constructPopup(xTable);
            this.p.show((Component)rightCornerHeaderComponentActionEvent.getSource(), rightCornerHeaderComponentActionEvent.getX() - this.p.getPreferredSize().width, rightCornerHeaderComponentActionEvent.getY());
        }
        else {
            this.p.setVisible(false);
        }
    }
    
    private void constructPopup(final XTable xTable) {
        (this.p = new JPopupMenu()).setBorder(new BevelBorder(0));
        for (int i = 0; i < xTable.getColumnCount(); ++i) {
            final XTableColumn column = xTable.getColumn(i);
            if (!column.isMandatoryColumn()) {
                final ToggleMenuItem toggleMenuItem = new ToggleMenuItem(column.getHeaderValue().toString(), !column.isHidden());
                toggleMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent actionEvent) {
                        if (!toggleMenuItem.getState()) {
                            xTable.hideColumn(toggleMenuItem.getLabel());
                        }
                        else {
                            xTable.showColumn(toggleMenuItem.getLabel());
                        }
                    }
                });
                this.p.add(toggleMenuItem);
            }
        }
    }
}
