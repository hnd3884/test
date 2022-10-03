package sun.security.tools.policytool;

import java.io.FileNotFoundException;
import java.awt.Window;
import java.text.MessageFormat;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class FileMenuListener implements ActionListener
{
    private PolicyTool tool;
    private ToolWindow tw;
    
    FileMenuListener(final PolicyTool tool, final ToolWindow tw) {
        this.tool = tool;
        this.tw = tw;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        if (PolicyTool.collator.compare(actionEvent.getActionCommand(), "Exit") == 0) {
            new ToolDialog(PolicyTool.getMessage("Save.Changes"), this.tool, this.tw, true).displayUserSave(1);
        }
        else if (PolicyTool.collator.compare(actionEvent.getActionCommand(), "New") == 0) {
            new ToolDialog(PolicyTool.getMessage("Save.Changes"), this.tool, this.tw, true).displayUserSave(2);
        }
        else if (PolicyTool.collator.compare(actionEvent.getActionCommand(), "Open") == 0) {
            new ToolDialog(PolicyTool.getMessage("Save.Changes"), this.tool, this.tw, true).displayUserSave(3);
        }
        else if (PolicyTool.collator.compare(actionEvent.getActionCommand(), "Save") == 0) {
            final String text = ((JTextField)this.tw.getComponent(1)).getText();
            if (text == null || text.length() == 0) {
                new ToolDialog(PolicyTool.getMessage("Save.As"), this.tool, this.tw, true).displaySaveAsDialog(0);
            }
            else {
                try {
                    this.tool.savePolicy(text);
                    this.tw.displayStatusDialog(null, new MessageFormat(PolicyTool.getMessage("Policy.successfully.written.to.filename")).format(new Object[] { text }));
                }
                catch (final FileNotFoundException ex) {
                    if (text == null || text.equals("")) {
                        this.tw.displayErrorDialog(null, new FileNotFoundException(PolicyTool.getMessage("null.filename")));
                    }
                    else {
                        this.tw.displayErrorDialog(null, ex);
                    }
                }
                catch (final Exception ex2) {
                    this.tw.displayErrorDialog(null, ex2);
                }
            }
        }
        else if (PolicyTool.collator.compare(actionEvent.getActionCommand(), "Save.As") == 0) {
            new ToolDialog(PolicyTool.getMessage("Save.As"), this.tool, this.tw, true).displaySaveAsDialog(0);
        }
        else if (PolicyTool.collator.compare(actionEvent.getActionCommand(), "View.Warning.Log") == 0) {
            this.tw.displayWarningLog(null);
        }
    }
}
