package sun.security.tools.policytool;

import java.awt.event.MouseEvent;
import java.awt.Window;
import javax.swing.JList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

class EditPermButtonListener extends MouseAdapter implements ActionListener
{
    private PolicyTool tool;
    private ToolWindow tw;
    private ToolDialog td;
    private boolean editPolicyEntry;
    
    EditPermButtonListener(final PolicyTool tool, final ToolWindow tw, final ToolDialog td, final boolean editPolicyEntry) {
        this.tool = tool;
        this.tw = tw;
        this.td = td;
        this.editPolicyEntry = editPolicyEntry;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        if (((JList)this.td.getComponent(8)).getSelectedIndex() < 0) {
            this.tw.displayErrorDialog(this.td, new Exception(PolicyTool.getMessage("No.permission.selected")));
            return;
        }
        this.td.displayPermissionDialog(this.editPolicyEntry, true);
    }
    
    @Override
    public void mouseClicked(final MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            this.actionPerformed(null);
        }
    }
}
