package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class CancelButtonListener implements ActionListener
{
    private ToolDialog td;
    
    CancelButtonListener(final ToolDialog td) {
        this.td = td;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        this.td.setVisible(false);
        this.td.dispose();
    }
}
