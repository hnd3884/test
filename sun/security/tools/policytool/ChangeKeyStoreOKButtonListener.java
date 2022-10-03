package sun.security.tools.policytool;

import java.awt.Window;
import java.text.MessageFormat;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ChangeKeyStoreOKButtonListener implements ActionListener
{
    private PolicyTool tool;
    private ToolWindow tw;
    private ToolDialog td;
    
    ChangeKeyStoreOKButtonListener(final PolicyTool tool, final ToolWindow tw, final ToolDialog td) {
        this.tool = tool;
        this.tw = tw;
        this.td = td;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        final String trim = ((JTextField)this.td.getComponent(1)).getText().trim();
        final String trim2 = ((JTextField)this.td.getComponent(3)).getText().trim();
        final String trim3 = ((JTextField)this.td.getComponent(5)).getText().trim();
        final String trim4 = ((JTextField)this.td.getComponent(7)).getText().trim();
        try {
            this.tool.openKeyStore((trim.length() == 0) ? null : trim, (trim2.length() == 0) ? null : trim2, (trim3.length() == 0) ? null : trim3, (trim4.length() == 0) ? null : trim4);
            this.tool.modified = true;
        }
        catch (final Exception ex) {
            this.tw.displayErrorDialog(this.td, new MessageFormat(PolicyTool.getMessage("Unable.to.open.KeyStore.ex.toString.")).format(new Object[] { ex.toString() }));
            return;
        }
        this.td.dispose();
    }
}
