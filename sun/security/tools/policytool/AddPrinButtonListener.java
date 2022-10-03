package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class AddPrinButtonListener implements ActionListener
{
    private PolicyTool tool;
    private ToolWindow tw;
    private ToolDialog td;
    private boolean editPolicyEntry;
    
    AddPrinButtonListener(final PolicyTool tool, final ToolWindow tw, final ToolDialog td, final boolean editPolicyEntry) {
        this.tool = tool;
        this.tw = tw;
        this.td = td;
        this.editPolicyEntry = editPolicyEntry;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        this.td.displayPrincipalDialog(this.editPolicyEntry, false);
    }
}
