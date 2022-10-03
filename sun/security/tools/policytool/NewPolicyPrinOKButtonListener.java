package sun.security.tools.policytool;

import sun.security.provider.PolicyParser;
import java.awt.Window;
import java.text.MessageFormat;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class NewPolicyPrinOKButtonListener implements ActionListener
{
    private PolicyTool tool;
    private ToolWindow tw;
    private ToolDialog listDialog;
    private ToolDialog infoDialog;
    private boolean edit;
    
    NewPolicyPrinOKButtonListener(final PolicyTool tool, final ToolWindow tw, final ToolDialog listDialog, final ToolDialog infoDialog, final boolean edit) {
        this.tool = tool;
        this.tw = tw;
        this.listDialog = listDialog;
        this.infoDialog = infoDialog;
        this.edit = edit;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        try {
            final PolicyParser.PrincipalEntry prinFromDialog = this.infoDialog.getPrinFromDialog();
            if (prinFromDialog != null) {
                try {
                    this.tool.verifyPrincipal(prinFromDialog.getPrincipalClass(), prinFromDialog.getPrincipalName());
                }
                catch (final ClassNotFoundException ex) {
                    final MessageFormat messageFormat = new MessageFormat(PolicyTool.getMessage("Warning.Class.not.found.class"));
                    final Object[] array = { prinFromDialog.getPrincipalClass() };
                    this.tool.warnings.addElement(messageFormat.format(array));
                    this.tw.displayStatusDialog(this.infoDialog, messageFormat.format(array));
                }
                final TaggedList list = (TaggedList)this.listDialog.getComponent(6);
                final String principalEntryToUserFriendlyString = ToolDialog.PrincipalEntryToUserFriendlyString(prinFromDialog);
                if (this.edit) {
                    list.replaceTaggedItem(principalEntryToUserFriendlyString, prinFromDialog, list.getSelectedIndex());
                }
                else {
                    list.addTaggedItem(principalEntryToUserFriendlyString, prinFromDialog);
                }
            }
            this.infoDialog.dispose();
        }
        catch (final Exception ex2) {
            this.tw.displayErrorDialog(this.infoDialog, ex2);
        }
    }
}
