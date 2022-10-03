package sun.security.tools.policytool;

import sun.security.provider.PolicyParser;
import java.lang.reflect.InvocationTargetException;
import java.awt.Window;
import java.text.MessageFormat;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class NewPolicyPermOKButtonListener implements ActionListener
{
    private PolicyTool tool;
    private ToolWindow tw;
    private ToolDialog listDialog;
    private ToolDialog infoDialog;
    private boolean edit;
    
    NewPolicyPermOKButtonListener(final PolicyTool tool, final ToolWindow tw, final ToolDialog listDialog, final ToolDialog infoDialog, final boolean edit) {
        this.tool = tool;
        this.tw = tw;
        this.listDialog = listDialog;
        this.infoDialog = infoDialog;
        this.edit = edit;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        try {
            final PolicyParser.PermissionEntry permFromDialog = this.infoDialog.getPermFromDialog();
            try {
                this.tool.verifyPermission(permFromDialog.permission, permFromDialog.name, permFromDialog.action);
            }
            catch (final ClassNotFoundException ex) {
                final MessageFormat messageFormat = new MessageFormat(PolicyTool.getMessage("Warning.Class.not.found.class"));
                final Object[] array = { permFromDialog.permission };
                this.tool.warnings.addElement(messageFormat.format(array));
                this.tw.displayStatusDialog(this.infoDialog, messageFormat.format(array));
            }
            final TaggedList list = (TaggedList)this.listDialog.getComponent(8);
            final String permissionEntryToUserFriendlyString = ToolDialog.PermissionEntryToUserFriendlyString(permFromDialog);
            if (this.edit) {
                list.replaceTaggedItem(permissionEntryToUserFriendlyString, permFromDialog, list.getSelectedIndex());
            }
            else {
                list.addTaggedItem(permissionEntryToUserFriendlyString, permFromDialog);
            }
            this.infoDialog.dispose();
        }
        catch (final InvocationTargetException ex2) {
            this.tw.displayErrorDialog(this.infoDialog, ex2.getTargetException());
        }
        catch (final Exception ex3) {
            this.tw.displayErrorDialog(this.infoDialog, ex3);
        }
    }
}
