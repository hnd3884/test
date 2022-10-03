package sun.security.tools.policytool;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class PermissionNameMenuListener implements ItemListener
{
    private ToolDialog td;
    
    PermissionNameMenuListener(final ToolDialog td) {
        this.td = td;
    }
    
    @Override
    public void itemStateChanged(final ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == 2) {
            return;
        }
        ((JComboBox)this.td.getComponent(3)).getAccessibleContext().setAccessibleName(PolicyTool.splitToWords((String)itemEvent.getItem()));
        if (((String)itemEvent.getItem()).indexOf(ToolDialog.PERM_NAME) != -1) {
            return;
        }
        ((JTextField)this.td.getComponent(4)).setText((String)itemEvent.getItem());
    }
}
