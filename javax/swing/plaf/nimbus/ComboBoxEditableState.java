package javax.swing.plaf.nimbus;

import javax.swing.JComboBox;
import javax.swing.JComponent;

class ComboBoxEditableState extends State
{
    ComboBoxEditableState() {
        super("Editable");
    }
    
    @Override
    protected boolean isInState(final JComponent component) {
        return component instanceof JComboBox && ((JComboBox)component).isEditable();
    }
}
