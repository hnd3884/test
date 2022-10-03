package sun.security.tools.policytool;

import java.awt.Window;
import javax.swing.JList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MainWindowListener implements ActionListener
{
    private PolicyTool tool;
    private ToolWindow tw;
    
    MainWindowListener(final PolicyTool tool, final ToolWindow tw) {
        this.tool = tool;
        this.tw = tw;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        if (PolicyTool.collator.compare(actionEvent.getActionCommand(), "Add.Policy.Entry") == 0) {
            new ToolDialog(PolicyTool.getMessage("Policy.Entry"), this.tool, this.tw, true).displayPolicyEntryDialog(false);
        }
        else if (PolicyTool.collator.compare(actionEvent.getActionCommand(), "Remove.Policy.Entry") == 0) {
            if (((JList)this.tw.getComponent(3)).getSelectedIndex() < 0) {
                this.tw.displayErrorDialog(null, new Exception(PolicyTool.getMessage("No.Policy.Entry.selected")));
                return;
            }
            new ToolDialog(PolicyTool.getMessage("Remove.Policy.Entry"), this.tool, this.tw, true).displayConfirmRemovePolicyEntry();
        }
        else if (PolicyTool.collator.compare(actionEvent.getActionCommand(), "Edit.Policy.Entry") == 0) {
            if (((JList)this.tw.getComponent(3)).getSelectedIndex() < 0) {
                this.tw.displayErrorDialog(null, new Exception(PolicyTool.getMessage("No.Policy.Entry.selected")));
                return;
            }
            new ToolDialog(PolicyTool.getMessage("Policy.Entry"), this.tool, this.tw, true).displayPolicyEntryDialog(true);
        }
        else if (PolicyTool.collator.compare(actionEvent.getActionCommand(), "Edit") == 0) {
            new ToolDialog(PolicyTool.getMessage("KeyStore"), this.tool, this.tw, true).keyStoreDialog(0);
        }
    }
}
