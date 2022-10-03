package org.jfree.ui.about;

import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import java.awt.event.ActionEvent;
import java.awt.Container;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.BorderFactory;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.ResourceBundle;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

public class SystemPropertiesFrame extends JFrame implements ActionListener
{
    private static final String COPY_COMMAND = "COPY";
    private static final String CLOSE_COMMAND = "CLOSE";
    private SystemPropertiesPanel panel;
    
    public SystemPropertiesFrame(final boolean menu) {
        final String baseName = "org.jfree.ui.about.resources.AboutResources";
        final ResourceBundle resources = ResourceBundle.getBundle("org.jfree.ui.about.resources.AboutResources");
        final String title = resources.getString("system-frame.title");
        this.setTitle(title);
        this.setDefaultCloseOperation(2);
        if (menu) {
            this.setJMenuBar(this.createMenuBar(resources));
        }
        final JPanel content = new JPanel(new BorderLayout());
        (this.panel = new SystemPropertiesPanel()).setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        content.add(this.panel, "Center");
        final JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        final String label = resources.getString("system-frame.button.close");
        final Character mnemonic = (Character)resources.getObject("system-frame.button.close.mnemonic");
        final JButton closeButton = new JButton(label);
        closeButton.setMnemonic(mnemonic);
        closeButton.setActionCommand("CLOSE");
        closeButton.addActionListener(this);
        buttonPanel.add(closeButton, "East");
        content.add(buttonPanel, "South");
        this.setContentPane(content);
    }
    
    public void actionPerformed(final ActionEvent e) {
        final String command = e.getActionCommand();
        if (command.equals("CLOSE")) {
            this.dispose();
        }
        else if (command.equals("COPY")) {
            this.panel.copySystemPropertiesToClipboard();
        }
    }
    
    private JMenuBar createMenuBar(final ResourceBundle resources) {
        final JMenuBar menuBar = new JMenuBar();
        String label = resources.getString("system-frame.menu.file");
        Character mnemonic = (Character)resources.getObject("system-frame.menu.file.mnemonic");
        final JMenu fileMenu = new JMenu(label, true);
        fileMenu.setMnemonic(mnemonic);
        label = resources.getString("system-frame.menu.file.close");
        mnemonic = (Character)resources.getObject("system-frame.menu.file.close.mnemonic");
        final JMenuItem closeItem = new JMenuItem(label, mnemonic);
        closeItem.setActionCommand("CLOSE");
        closeItem.addActionListener(this);
        fileMenu.add(closeItem);
        label = resources.getString("system-frame.menu.edit");
        mnemonic = (Character)resources.getObject("system-frame.menu.edit.mnemonic");
        final JMenu editMenu = new JMenu(label);
        editMenu.setMnemonic(mnemonic);
        label = resources.getString("system-frame.menu.edit.copy");
        mnemonic = (Character)resources.getObject("system-frame.menu.edit.copy.mnemonic");
        final JMenuItem copyItem = new JMenuItem(label, mnemonic);
        copyItem.setActionCommand("COPY");
        copyItem.addActionListener(this);
        editMenu.add(copyItem);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        return menuBar;
    }
}
