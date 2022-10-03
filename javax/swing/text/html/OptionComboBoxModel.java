package javax.swing.text.html;

import java.io.Serializable;
import javax.swing.DefaultComboBoxModel;

class OptionComboBoxModel<E> extends DefaultComboBoxModel<E> implements Serializable
{
    private Option selectedOption;
    
    OptionComboBoxModel() {
        this.selectedOption = null;
    }
    
    public void setInitialSelection(final Option selectedOption) {
        this.selectedOption = selectedOption;
    }
    
    public Option getInitialSelection() {
        return this.selectedOption;
    }
}
