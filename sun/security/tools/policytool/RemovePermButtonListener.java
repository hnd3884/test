package sun.security.tools.policytool;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class RemovePermButtonListener implements ActionListener
{
    private PolicyTool tool;
    private ToolWindow tw;
    private ToolDialog td;
    private boolean edit;
    
    RemovePermButtonListener(final PolicyTool tool, final ToolWindow tw, final ToolDialog td, final boolean edit) {
        this.tool = tool;
        this.tw = tw;
        this.td = td;
        this.edit = edit;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        final TaggedList list = (TaggedList)this.td.getComponent(8);
        final int selectedIndex = list.getSelectedIndex();
        if (selectedIndex < 0) {
            this.tw.displayErrorDialog(this.td, new Exception(PolicyTool.getMessage("No.permission.selected")));
            return;
        }
        list.removeTaggedItem(selectedIndex);
    }
}
