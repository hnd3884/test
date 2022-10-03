package org.jfree.ui.action;

import org.jfree.util.Log;
import java.beans.PropertyChangeEvent;
import javax.swing.KeyStroke;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.Action;
import javax.swing.JMenuItem;

public class ActionMenuItem extends JMenuItem
{
    private Action action;
    private ActionEnablePropertyChangeHandler propertyChangeHandler;
    
    public ActionMenuItem() {
    }
    
    public ActionMenuItem(final String text) {
        super(text);
    }
    
    public ActionMenuItem(final String text, final int i) {
        super(text, i);
    }
    
    public ActionMenuItem(final String text, final Icon icon) {
        super(text, icon);
    }
    
    public ActionMenuItem(final Action action) {
        this.setAction(action);
    }
    
    public ActionMenuItem(final Icon icon) {
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
            this.setAccelerator(null);
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
            else {
                this.setMnemonic(0);
            }
            o = newAction.getValue("AcceleratorKey");
            if (o instanceof KeyStroke) {
                this.setAccelerator((KeyStroke)o);
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
                    ActionMenuItem.this.setEnabled(ActionMenuItem.this.getAction().isEnabled());
                }
                else if (event.getPropertyName().equals("SmallIcon")) {
                    ActionMenuItem.this.setIcon((Icon)ActionMenuItem.this.getAction().getValue("SmallIcon"));
                }
                else if (event.getPropertyName().equals("Name")) {
                    ActionMenuItem.this.setText((String)ActionMenuItem.this.getAction().getValue("Name"));
                }
                else if (event.getPropertyName().equals("ShortDescription")) {
                    ActionMenuItem.this.setToolTipText((String)ActionMenuItem.this.getAction().getValue("ShortDescription"));
                }
                final Action ac = ActionMenuItem.this.getAction();
                if (event.getPropertyName().equals("AcceleratorKey")) {
                    ActionMenuItem.this.setAccelerator((KeyStroke)ac.getValue("AcceleratorKey"));
                }
                else if (event.getPropertyName().equals("MnemonicKey")) {
                    final Object o = ac.getValue("MnemonicKey");
                    if (o != null) {
                        if (o instanceof Character) {
                            final Character c = (Character)o;
                            ActionMenuItem.this.setMnemonic(c);
                        }
                        else if (o instanceof Integer) {
                            final Integer c2 = (Integer)o;
                            ActionMenuItem.this.setMnemonic(c2);
                        }
                    }
                    else {
                        ActionMenuItem.this.setMnemonic(0);
                    }
                }
            }
            catch (final Exception e) {
                Log.warn("Error on PropertyChange in ActionButton: ", e);
            }
        }
    }
}
