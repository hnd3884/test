package org.jfree.ui.action;

import org.jfree.util.Log;
import java.beans.PropertyChangeEvent;
import javax.swing.KeyStroke;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.Action;
import javax.swing.JRadioButton;

public class ActionRadioButton extends JRadioButton
{
    private Action action;
    private ActionEnablePropertyChangeHandler propertyChangeHandler;
    
    public ActionRadioButton() {
    }
    
    public ActionRadioButton(final String text) {
        super(text);
    }
    
    public ActionRadioButton(final String text, final Icon icon) {
        super(text, icon);
    }
    
    public ActionRadioButton(final Action action) {
        this.setAction(action);
    }
    
    public ActionRadioButton(final Icon icon) {
        super(icon);
    }
    
    public Action getAction() {
        return this.action;
    }
    
    private ActionEnablePropertyChangeHandler getPropertyChangeHandler() {
        if (this.propertyChangeHandler == null) {
            this.propertyChangeHandler = new ActionEnablePropertyChangeHandler();
        }
        return this.propertyChangeHandler;
    }
    
    public void setAction(final Action newAction) {
        final Action oldAction = this.getAction();
        if (oldAction != null) {
            this.removeActionListener(oldAction);
            oldAction.removePropertyChangeListener(this.getPropertyChangeHandler());
            final Object o = oldAction.getValue("AcceleratorKey");
            if (o instanceof KeyStroke && o != null) {
                final KeyStroke k = (KeyStroke)o;
                this.unregisterKeyboardAction(k);
            }
        }
        this.action = newAction;
        if (this.action != null) {
            this.addActionListener(newAction);
            newAction.addPropertyChangeListener(this.getPropertyChangeHandler());
            this.setText((String)newAction.getValue("Name"));
            this.setToolTipText((String)newAction.getValue("ShortDescription"));
            this.setIcon((Icon)newAction.getValue("SmallIcon"));
            this.setEnabled(this.action.isEnabled());
            Object o = newAction.getValue("MnemonicKey");
            if (o != null) {
                if (o instanceof Character) {
                    final Character c = (Character)o;
                    this.setMnemonic(c);
                }
                else if (o instanceof Integer) {
                    final Integer c2 = (Integer)o;
                    this.setMnemonic(c2);
                }
            }
            o = newAction.getValue("AcceleratorKey");
            if (o instanceof KeyStroke && o != null) {
                final KeyStroke k = (KeyStroke)o;
                this.registerKeyboardAction(newAction, k, 2);
            }
        }
    }
    
    public void setEnabled(final boolean b) {
        super.setEnabled(b);
        if (this.getAction() != null) {
            this.getAction().setEnabled(b);
        }
    }
    
    private class ActionEnablePropertyChangeHandler implements PropertyChangeListener
    {
        ActionEnablePropertyChangeHandler() {
        }
        
        public void propertyChange(final PropertyChangeEvent event) {
            try {
                if (event.getPropertyName().equals("enabled")) {
                    ActionRadioButton.this.setEnabled(ActionRadioButton.this.getAction().isEnabled());
                }
                else if (event.getPropertyName().equals("SmallIcon")) {
                    ActionRadioButton.this.setIcon((Icon)ActionRadioButton.this.getAction().getValue("SmallIcon"));
                }
                else if (event.getPropertyName().equals("Name")) {
                    ActionRadioButton.this.setText((String)ActionRadioButton.this.getAction().getValue("Name"));
                }
                else if (event.getPropertyName().equals("ShortDescription")) {
                    ActionRadioButton.this.setToolTipText((String)ActionRadioButton.this.getAction().getValue("ShortDescription"));
                }
                final Action ac = ActionRadioButton.this.getAction();
                if (event.getPropertyName().equals("AcceleratorKey")) {
                    final KeyStroke oldVal = (KeyStroke)event.getOldValue();
                    if (oldVal != null) {
                        ActionRadioButton.this.unregisterKeyboardAction(oldVal);
                    }
                    final Object o = ac.getValue("AcceleratorKey");
                    if (o instanceof KeyStroke && o != null) {
                        final KeyStroke k = (KeyStroke)o;
                        ActionRadioButton.this.registerKeyboardAction(ac, k, 2);
                    }
                }
                else if (event.getPropertyName().equals("MnemonicKey")) {
                    final Object o2 = ac.getValue("MnemonicKey");
                    if (o2 != null) {
                        if (o2 instanceof Character) {
                            final Character c = (Character)o2;
                            ActionRadioButton.this.setMnemonic(c);
                        }
                        else if (o2 instanceof Integer) {
                            final Integer c2 = (Integer)o2;
                            ActionRadioButton.this.setMnemonic(c2);
                        }
                    }
                }
            }
            catch (final Exception e) {
                Log.warn("Error on PropertyChange in ActionButton: ", e);
            }
        }
    }
}
