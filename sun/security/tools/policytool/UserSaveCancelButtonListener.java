package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class UserSaveCancelButtonListener implements ActionListener
{
    private ToolDialog us;
    
    UserSaveCancelButtonListener(final ToolDialog us) {
        this.us = us;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        this.us.setVisible(false);
        this.us.dispose();
    }
}
