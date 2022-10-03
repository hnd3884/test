package javax.swing.plaf.nimbus;

import java.awt.Container;
import javax.swing.JComboBox;
import javax.swing.JComponent;

class ComboBoxArrowButtonEditableState extends State
{
    ComboBoxArrowButtonEditableState() {
        super("Editable");
    }
    
    @Override
    protected boolean isInState(final JComponent component) {
        final Container parent = component.getParent();
        return parent instanceof JComboBox && ((JComboBox)parent).isEditable();
    }
}
