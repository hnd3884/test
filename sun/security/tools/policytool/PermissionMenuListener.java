package sun.security.tools.policytool;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class PermissionMenuListener implements ItemListener
{
    private ToolDialog td;
    
    PermissionMenuListener(final ToolDialog td) {
        this.td = td;
    }
    
    @Override
    public void itemStateChanged(final ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == 2) {
            return;
        }
        final JComboBox comboBox = (JComboBox)this.td.getComponent(1);
        final JComboBox comboBox2 = (JComboBox)this.td.getComponent(3);
        final JComboBox comboBox3 = (JComboBox)this.td.getComponent(5);
        final JTextField textField = (JTextField)this.td.getComponent(4);
        final JTextField textField2 = (JTextField)this.td.getComponent(6);
        final JTextField textField3 = (JTextField)this.td.getComponent(2);
        final JTextField textField4 = (JTextField)this.td.getComponent(8);
        comboBox.getAccessibleContext().setAccessibleName(PolicyTool.splitToWords((String)itemEvent.getItem()));
        if (PolicyTool.collator.compare((String)itemEvent.getItem(), ToolDialog.PERM) == 0) {
            if (textField3.getText() != null && textField3.getText().length() > 0) {
                final Perm perm = ToolDialog.getPerm(textField3.getText(), true);
                if (perm != null) {
                    comboBox.setSelectedItem(perm.CLASS);
                }
            }
            return;
        }
        if (textField3.getText().indexOf((String)itemEvent.getItem()) == -1) {
            textField.setText("");
            textField2.setText("");
            textField4.setText("");
        }
        final Perm perm2 = ToolDialog.getPerm((String)itemEvent.getItem(), false);
        if (perm2 == null) {
            textField3.setText("");
        }
        else {
            textField3.setText(perm2.FULL_CLASS);
        }
        this.td.setPermissionNames(perm2, comboBox2, textField);
        this.td.setPermissionActions(perm2, comboBox3, textField2);
    }
}
