package sun.security.tools.policytool;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class ToolWindowListener implements WindowListener
{
    private PolicyTool tool;
    private ToolWindow tw;
    
    ToolWindowListener(final PolicyTool tool, final ToolWindow tw) {
        this.tool = tool;
        this.tw = tw;
    }
    
    @Override
    public void windowOpened(final WindowEvent windowEvent) {
    }
    
    @Override
    public void windowClosing(final WindowEvent windowEvent) {
        new ToolDialog(PolicyTool.getMessage("Save.Changes"), this.tool, this.tw, true).displayUserSave(1);
    }
    
    @Override
    public void windowClosed(final WindowEvent windowEvent) {
        System.exit(0);
    }
    
    @Override
    public void windowIconified(final WindowEvent windowEvent) {
    }
    
    @Override
    public void windowDeiconified(final WindowEvent windowEvent) {
    }
    
    @Override
    public void windowActivated(final WindowEvent windowEvent) {
    }
    
    @Override
    public void windowDeactivated(final WindowEvent windowEvent) {
    }
}
