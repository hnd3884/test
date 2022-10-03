package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ErrorOKButtonListener implements ActionListener
{
    private ToolDialog ed;
    
    ErrorOKButtonListener(final ToolDialog ed) {
        this.ed = ed;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        this.ed.setVisible(false);
        this.ed.dispose();
    }
}
