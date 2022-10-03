package sun.security.tools.policytool;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class PermissionActionsMenuListener implements ItemListener
{
    private ToolDialog td;
    
    PermissionActionsMenuListener(final ToolDialog td) {
        this.td = td;
    }
    
    @Override
    public void itemStateChanged(final ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == 2) {
            return;
        }
        ((JComboBox)this.td.getComponent(5)).getAccessibleContext().setAccessibleName((String)itemEvent.getItem());
        if (((String)itemEvent.getItem()).indexOf(ToolDialog.PERM_ACTIONS) != -1) {
            return;
        }
        final JTextField textField = (JTextField)this.td.getComponent(6);
        if (textField.getText() == null || textField.getText().equals("")) {
            textField.setText((String)itemEvent.getItem());
        }
        else if (textField.getText().indexOf((String)itemEvent.getItem()) == -1) {
            textField.setText(textField.getText() + ", " + (String)itemEvent.getItem());
        }
    }
}
