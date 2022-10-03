package sun.security.tools.policytool;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class PrincipalTypeMenuListener implements ItemListener
{
    private ToolDialog td;
    
    PrincipalTypeMenuListener(final ToolDialog td) {
        this.td = td;
    }
    
    @Override
    public void itemStateChanged(final ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == 2) {
            return;
        }
        final JComboBox comboBox = (JComboBox)this.td.getComponent(1);
        final JTextField textField = (JTextField)this.td.getComponent(2);
        final JTextField textField2 = (JTextField)this.td.getComponent(4);
        comboBox.getAccessibleContext().setAccessibleName(PolicyTool.splitToWords((String)itemEvent.getItem()));
        if (((String)itemEvent.getItem()).equals(ToolDialog.PRIN_TYPE)) {
            if (textField.getText() != null && textField.getText().length() > 0) {
                comboBox.setSelectedItem(ToolDialog.getPrin(textField.getText(), true).CLASS);
            }
            return;
        }
        if (textField.getText().indexOf((String)itemEvent.getItem()) == -1) {
            textField2.setText("");
        }
        final Prin prin = ToolDialog.getPrin((String)itemEvent.getItem(), false);
        if (prin != null) {
            textField.setText(prin.FULL_CLASS);
        }
    }
}
