package org.jfree.ui.action;

import org.jfree.util.Log;
import java.beans.PropertyChangeEvent;
import javax.swing.KeyStroke;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.Action;
import javax.swing.JButton;

public class ActionButton extends JButton
{
    private Action action;
    private ActionEnablePropertyChangeHandler propertyChangeHandler;
    
    public ActionButton() {
    }
    
    public ActionButton(final String text) {
        super(text);
    }
    
    public ActionButton(final String text, final Icon icon) {
        super(text, icon);
    }
    
    public ActionButton(final Action action) {
        this.setAction(action);
    }
    
    public ActionButton(final Icon icon) {
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
            if (o instanceof KeyStroke) {
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
            if (o instanceof KeyStroke) {
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
        public ActionEnablePropertyChangeHandler() {
        }
        
        public void propertyChange(final PropertyChangeEvent event) {
            try {
                if (event.getPropertyName().equals("enabled")) {
                    ActionButton.this.setEnabled(ActionButton.this.getAction().isEnabled());
                }
                else if (event.getPropertyName().equals("SmallIcon")) {
                    ActionButton.this.setIcon((Icon)ActionButton.this.getAction().getValue("SmallIcon"));
                }
                else if (event.getPropertyName().equals("Name")) {
                    ActionButton.this.setText((String)ActionButton.this.getAction().getValue("Name"));
                }
                else if (event.getPropertyName().equals("ShortDescription")) {
                    ActionButton.this.setToolTipText((String)ActionButton.this.getAction().getValue("ShortDescription"));
                }
                final Action ac = ActionButton.this.getAction();
                if (event.getPropertyName().equals("AcceleratorKey")) {
                    final KeyStroke oldVal = (KeyStroke)event.getOldValue();
                    if (oldVal != null) {
                        ActionButton.this.unregisterKeyboardAction(oldVal);
                    }
                    final Object o = ac.getValue("AcceleratorKey");
                    if (o instanceof KeyStroke) {
                        final KeyStroke k = (KeyStroke)o;
                        ActionButton.this.registerKeyboardAction(ac, k, 2);
                    }
                }
                else if (event.getPropertyName().equals("MnemonicKey")) {
                    final Object o2 = ac.getValue("MnemonicKey");
                    if (o2 != null) {
                        if (o2 instanceof Character) {
                            final Character c = (Character)o2;
                            ActionButton.this.setMnemonic(c);
                        }
                        else if (o2 instanceof Integer) {
                            final Integer c2 = (Integer)o2;
                            ActionButton.this.setMnemonic(c2);
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
