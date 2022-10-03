package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class UserSaveNoButtonListener implements ActionListener
{
    private PolicyTool tool;
    private ToolWindow tw;
    private ToolDialog us;
    private int select;
    
    UserSaveNoButtonListener(final ToolDialog us, final PolicyTool tool, final ToolWindow tw, final int select) {
        this.us = us;
        this.tool = tool;
        this.tw = tw;
        this.select = select;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        this.us.setVisible(false);
        this.us.dispose();
        this.us.userSaveContinue(this.tool, this.tw, this.us, this.select);
    }
}
