package javax.swing;

import java.util.Enumeration;
import java.util.Vector;
import java.io.Serializable;

public class ButtonGroup implements Serializable
{
    protected Vector<AbstractButton> buttons;
    ButtonModel selection;
    
    public ButtonGroup() {
        this.buttons = new Vector<AbstractButton>();
        this.selection = null;
    }
    
    public void add(final AbstractButton abstractButton) {
        if (abstractButton == null) {
            return;
        }
        this.buttons.addElement(abstractButton);
        if (abstractButton.isSelected()) {
            if (this.selection == null) {
                this.selection = abstractButton.getModel();
            }
            else {
                abstractButton.setSelected(false);
            }
        }
        abstractButton.getModel().setGroup(this);
    }
    
    public void remove(final AbstractButton abstractButton) {
        if (abstractButton == null) {
            return;
        }
        this.buttons.removeElement(abstractButton);
        if (abstractButton.getModel() == this.selection) {
            this.selection = null;
        }
        abstractButton.getModel().setGroup(null);
    }
    
    public void clearSelection() {
        if (this.selection != null) {
            final ButtonModel selection = this.selection;
            this.selection = null;
            selection.setSelected(false);
        }
    }
    
    public Enumeration<AbstractButton> getElements() {
        return this.buttons.elements();
    }
    
    public ButtonModel getSelection() {
        return this.selection;
    }
    
    public void setSelected(final ButtonModel selection, final boolean b) {
        if (b && selection != null && selection != this.selection) {
            final ButtonModel selection2 = this.selection;
            this.selection = selection;
            if (selection2 != null) {
                selection2.setSelected(false);
            }
            selection.setSelected(true);
        }
    }
    
    public boolean isSelected(final ButtonModel buttonModel) {
        return buttonModel == this.selection;
    }
    
    public int getButtonCount() {
        if (this.buttons == null) {
            return 0;
        }
        return this.buttons.size();
    }
}
