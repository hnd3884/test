package sun.security.tools.policytool;

import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

class PolicyListListener extends MouseAdapter implements ActionListener
{
    private PolicyTool tool;
    private ToolWindow tw;
    
    PolicyListListener(final PolicyTool tool, final ToolWindow tw) {
        this.tool = tool;
        this.tw = tw;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        new ToolDialog(PolicyTool.getMessage("Policy.Entry"), this.tool, this.tw, true).displayPolicyEntryDialog(true);
    }
    
    @Override
    public void mouseClicked(final MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            this.actionPerformed(null);
        }
    }
}
