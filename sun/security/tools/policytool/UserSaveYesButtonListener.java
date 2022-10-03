package sun.security.tools.policytool;

import java.awt.Window;
import java.text.MessageFormat;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class UserSaveYesButtonListener implements ActionListener
{
    private ToolDialog us;
    private PolicyTool tool;
    private ToolWindow tw;
    private int select;
    
    UserSaveYesButtonListener(final ToolDialog us, final PolicyTool tool, final ToolWindow tw, final int select) {
        this.us = us;
        this.tool = tool;
        this.tw = tw;
        this.select = select;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        this.us.setVisible(false);
        this.us.dispose();
        try {
            final String text = ((JTextField)this.tw.getComponent(1)).getText();
            if (text == null || text.equals("")) {
                this.us.displaySaveAsDialog(this.select);
            }
            else {
                this.tool.savePolicy(text);
                this.tw.displayStatusDialog(null, new MessageFormat(PolicyTool.getMessage("Policy.successfully.written.to.filename")).format(new Object[] { text }));
                this.us.userSaveContinue(this.tool, this.tw, this.us, this.select);
            }
        }
        catch (final Exception ex) {
            this.tw.displayErrorDialog(null, ex);
        }
    }
}
