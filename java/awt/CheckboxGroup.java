package java.awt;

import java.io.Serializable;

public class CheckboxGroup implements Serializable
{
    Checkbox selectedCheckbox;
    private static final long serialVersionUID = 3729780091441768983L;
    
    public CheckboxGroup() {
        this.selectedCheckbox = null;
    }
    
    public Checkbox getSelectedCheckbox() {
        return this.getCurrent();
    }
    
    @Deprecated
    public Checkbox getCurrent() {
        return this.selectedCheckbox;
    }
    
    public void setSelectedCheckbox(final Checkbox current) {
        this.setCurrent(current);
    }
    
    @Deprecated
    public synchronized void setCurrent(final Checkbox selectedCheckbox) {
        if (selectedCheckbox != null && selectedCheckbox.group != this) {
            return;
        }
        final Checkbox selectedCheckbox2 = this.selectedCheckbox;
        this.selectedCheckbox = selectedCheckbox;
        if (selectedCheckbox2 != null && selectedCheckbox2 != selectedCheckbox && selectedCheckbox2.group == this) {
            selectedCheckbox2.setState(false);
        }
        if (selectedCheckbox != null && selectedCheckbox2 != selectedCheckbox && !selectedCheckbox.getState()) {
            selectedCheckbox.setStateInternal(true);
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[selectedCheckbox=" + this.selectedCheckbox + "]";
    }
}
