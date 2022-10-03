package sun.security.tools.policytool;

import sun.security.provider.PolicyParser;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import java.awt.Window;
import java.text.MessageFormat;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class AddEntryDoneButtonListener implements ActionListener
{
    private PolicyTool tool;
    private ToolWindow tw;
    private ToolDialog td;
    private boolean edit;
    
    AddEntryDoneButtonListener(final PolicyTool tool, final ToolWindow tw, final ToolDialog td, final boolean edit) {
        this.tool = tool;
        this.tw = tw;
        this.td = td;
        this.edit = edit;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        try {
            final PolicyEntry policyEntryFromDialog = this.td.getPolicyEntryFromDialog();
            final PolicyParser.GrantEntry grantEntry = policyEntryFromDialog.getGrantEntry();
            if (grantEntry.signedBy != null) {
                final String[] signers = this.tool.parseSigners(grantEntry.signedBy);
                for (int i = 0; i < signers.length; ++i) {
                    if (this.tool.getPublicKeyAlias(signers[i]) == null) {
                        final MessageFormat messageFormat = new MessageFormat(PolicyTool.getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
                        final Object[] array = { signers[i] };
                        this.tool.warnings.addElement(messageFormat.format(array));
                        this.tw.displayStatusDialog(this.td, messageFormat.format(array));
                    }
                }
            }
            final JList list = (JList)this.tw.getComponent(3);
            if (this.edit) {
                final int selectedIndex = list.getSelectedIndex();
                this.tool.addEntry(policyEntryFromDialog, selectedIndex);
                final String headerToString = policyEntryFromDialog.headerToString();
                if (PolicyTool.collator.compare(headerToString, list.getModel().getElementAt(selectedIndex)) != 0) {
                    this.tool.modified = true;
                }
                ((DefaultListModel)list.getModel()).set(selectedIndex, headerToString);
            }
            else {
                this.tool.addEntry(policyEntryFromDialog, -1);
                ((DefaultListModel)list.getModel()).addElement(policyEntryFromDialog.headerToString());
                this.tool.modified = true;
            }
            this.td.setVisible(false);
            this.td.dispose();
        }
        catch (final Exception ex) {
            this.tw.displayErrorDialog(this.td, ex);
        }
    }
}
