package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class StatusOKButtonListener implements ActionListener
{
    private ToolDialog sd;
    
    StatusOKButtonListener(final ToolDialog sd) {
        this.sd = sd;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        this.sd.setVisible(false);
        this.sd.dispose();
    }
}
