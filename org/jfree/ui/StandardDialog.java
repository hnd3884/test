package org.jfree.ui;

import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.Frame;
import java.awt.Dialog;
import java.util.ResourceBundle;
import java.awt.event.ActionListener;
import javax.swing.JDialog;

public class StandardDialog extends JDialog implements ActionListener
{
    private boolean cancelled;
    protected static final ResourceBundle localizationResources;
    
    static {
        localizationResources = ResourceBundle.getBundle("org.jfree.ui.LocalizationBundle");
    }
    
    public StandardDialog(final Dialog owner, final String title, final boolean modal) {
        super(owner, title, modal);
        this.cancelled = false;
    }
    
    public StandardDialog(final Frame owner, final String title, final boolean modal) {
        super(owner, title, modal);
        this.cancelled = false;
    }
    
    public void actionPerformed(final ActionEvent event) {
        final String command = event.getActionCommand();
        if (!command.equals("helpButton")) {
            if (command.equals("okButton")) {
                this.setVisible(this.cancelled = false);
            }
            else if (command.equals("cancelButton")) {
                this.cancelled = true;
                this.setVisible(false);
            }
        }
    }
    
    protected JPanel createButtonPanel() {
        final L1R2ButtonPanel buttons = new L1R2ButtonPanel(StandardDialog.localizationResources.getString("Help"), StandardDialog.localizationResources.getString("OK"), StandardDialog.localizationResources.getString("Cancel"));
        final JButton helpButton = buttons.getLeftButton();
        helpButton.setActionCommand("helpButton");
        helpButton.addActionListener(this);
        final JButton okButton = buttons.getRightButton1();
        okButton.setActionCommand("okButton");
        okButton.addActionListener(this);
        final JButton cancelButton = buttons.getRightButton2();
        cancelButton.setActionCommand("cancelButton");
        cancelButton.addActionListener(this);
        buttons.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        return buttons;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
}
