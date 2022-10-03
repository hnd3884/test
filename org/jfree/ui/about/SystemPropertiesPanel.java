package org.jfree.ui.about;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.datatransfer.Clipboard;
import javax.swing.ListSelectionModel;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.KeyStroke;
import java.awt.Component;
import javax.swing.JScrollPane;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.util.ResourceBundle;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JPanel;

public class SystemPropertiesPanel extends JPanel
{
    private JTable table;
    private JPopupMenu copyPopupMenu;
    private JMenuItem copyMenuItem;
    private PopupListener copyPopupListener;
    
    public SystemPropertiesPanel() {
        final String baseName = "org.jfree.ui.about.resources.AboutResources";
        final ResourceBundle resources = ResourceBundle.getBundle("org.jfree.ui.about.resources.AboutResources");
        this.setLayout(new BorderLayout());
        this.table = SystemProperties.createSystemPropertiesTable();
        this.add(new JScrollPane(this.table));
        this.copyPopupMenu = new JPopupMenu();
        final String label = resources.getString("system-properties-panel.popup-menu.copy");
        final KeyStroke accelerator = (KeyStroke)resources.getObject("system-properties-panel.popup-menu.copy.accelerator");
        (this.copyMenuItem = new JMenuItem(label)).setAccelerator(accelerator);
        this.copyMenuItem.getAccessibleContext().setAccessibleDescription(label);
        this.copyMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                SystemPropertiesPanel.this.copySystemPropertiesToClipboard();
            }
        });
        this.copyPopupMenu.add(this.copyMenuItem);
        this.copyPopupListener = new PopupListener();
        this.table.addMouseListener(this.copyPopupListener);
    }
    
    public void copySystemPropertiesToClipboard() {
        final StringBuffer buffer = new StringBuffer();
        final ListSelectionModel selection = this.table.getSelectionModel();
        final int firstRow = selection.getMinSelectionIndex();
        final int lastRow = selection.getMaxSelectionIndex();
        if (firstRow != -1 && lastRow != -1) {
            for (int r = firstRow; r <= lastRow; ++r) {
                for (int c = 0; c < this.table.getColumnCount(); ++c) {
                    buffer.append(this.table.getValueAt(r, c));
                    if (c != 2) {
                        buffer.append("\t");
                    }
                }
                buffer.append("\n");
            }
        }
        final StringSelection ss = new StringSelection(buffer.toString());
        final Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        cb.setContents(ss, ss);
    }
    
    protected final JPopupMenu getCopyPopupMenu() {
        return this.copyPopupMenu;
    }
    
    protected final JTable getTable() {
        return this.table;
    }
    
    private class PopupListener extends MouseAdapter
    {
        public PopupListener() {
        }
        
        private void maybeShowPopup(final MouseEvent e) {
            if (e.isPopupTrigger()) {
                SystemPropertiesPanel.this.getCopyPopupMenu().show(SystemPropertiesPanel.this.getTable(), e.getX(), e.getY());
            }
        }
        
        public void mousePressed(final MouseEvent e) {
            this.maybeShowPopup(e);
        }
        
        public void mouseReleased(final MouseEvent e) {
            this.maybeShowPopup(e);
        }
    }
}
