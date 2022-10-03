package javax.swing;

import java.util.EventListener;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeListener;
import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.EventQueue;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.ChangeEvent;
import java.io.Serializable;

public class DefaultButtonModel implements ButtonModel, Serializable
{
    protected int stateMask;
    protected String actionCommand;
    protected ButtonGroup group;
    protected int mnemonic;
    protected transient ChangeEvent changeEvent;
    protected EventListenerList listenerList;
    private boolean menuItem;
    public static final int ARMED = 1;
    public static final int SELECTED = 2;
    public static final int PRESSED = 4;
    public static final int ENABLED = 8;
    public static final int ROLLOVER = 16;
    
    public DefaultButtonModel() {
        this.stateMask = 0;
        this.actionCommand = null;
        this.group = null;
        this.mnemonic = 0;
        this.changeEvent = null;
        this.listenerList = new EventListenerList();
        this.menuItem = false;
        this.stateMask = 0;
        this.setEnabled(true);
    }
    
    @Override
    public void setActionCommand(final String actionCommand) {
        this.actionCommand = actionCommand;
    }
    
    @Override
    public String getActionCommand() {
        return this.actionCommand;
    }
    
    @Override
    public boolean isArmed() {
        return (this.stateMask & 0x1) != 0x0;
    }
    
    @Override
    public boolean isSelected() {
        return (this.stateMask & 0x2) != 0x0;
    }
    
    @Override
    public boolean isEnabled() {
        return (this.stateMask & 0x8) != 0x0;
    }
    
    @Override
    public boolean isPressed() {
        return (this.stateMask & 0x4) != 0x0;
    }
    
    @Override
    public boolean isRollover() {
        return (this.stateMask & 0x10) != 0x0;
    }
    
    @Override
    public void setArmed(final boolean b) {
        if (this.isMenuItem() && UIManager.getBoolean("MenuItem.disabledAreNavigable")) {
            if (this.isArmed() == b) {
                return;
            }
        }
        else if (this.isArmed() == b || !this.isEnabled()) {
            return;
        }
        if (b) {
            this.stateMask |= 0x1;
        }
        else {
            this.stateMask &= 0xFFFFFFFE;
        }
        this.fireStateChanged();
    }
    
    @Override
    public void setEnabled(final boolean b) {
        if (this.isEnabled() == b) {
            return;
        }
        if (b) {
            this.stateMask |= 0x8;
        }
        else {
            this.stateMask &= 0xFFFFFFF7;
            this.stateMask &= 0xFFFFFFFE;
            this.stateMask &= 0xFFFFFFFB;
        }
        this.fireStateChanged();
    }
    
    @Override
    public void setSelected(final boolean b) {
        if (this.isSelected() == b) {
            return;
        }
        if (b) {
            this.stateMask |= 0x2;
        }
        else {
            this.stateMask &= 0xFFFFFFFD;
        }
        this.fireItemStateChanged(new ItemEvent(this, 701, this, b ? 1 : 2));
        this.fireStateChanged();
    }
    
    @Override
    public void setPressed(final boolean b) {
        if (this.isPressed() == b || !this.isEnabled()) {
            return;
        }
        if (b) {
            this.stateMask |= 0x4;
        }
        else {
            this.stateMask &= 0xFFFFFFFB;
        }
        if (!this.isPressed() && this.isArmed()) {
            int n = 0;
            final AWTEvent currentEvent = EventQueue.getCurrentEvent();
            if (currentEvent instanceof InputEvent) {
                n = ((InputEvent)currentEvent).getModifiers();
            }
            else if (currentEvent instanceof ActionEvent) {
                n = ((ActionEvent)currentEvent).getModifiers();
            }
            this.fireActionPerformed(new ActionEvent(this, 1001, this.getActionCommand(), EventQueue.getMostRecentEventTime(), n));
        }
        this.fireStateChanged();
    }
    
    @Override
    public void setRollover(final boolean b) {
        if (this.isRollover() == b || !this.isEnabled()) {
            return;
        }
        if (b) {
            this.stateMask |= 0x10;
        }
        else {
            this.stateMask &= 0xFFFFFFEF;
        }
        this.fireStateChanged();
    }
    
    @Override
    public void setMnemonic(final int mnemonic) {
        this.mnemonic = mnemonic;
        this.fireStateChanged();
    }
    
    @Override
    public int getMnemonic() {
        return this.mnemonic;
    }
    
    @Override
    public void addChangeListener(final ChangeListener changeListener) {
        this.listenerList.add(ChangeListener.class, changeListener);
    }
    
    @Override
    public void removeChangeListener(final ChangeListener changeListener) {
        this.listenerList.remove(ChangeListener.class, changeListener);
    }
    
    public ChangeListener[] getChangeListeners() {
        return this.listenerList.getListeners(ChangeListener.class);
    }
    
    protected void fireStateChanged() {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ChangeListener.class) {
                if (this.changeEvent == null) {
                    this.changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listenerList[i + 1]).stateChanged(this.changeEvent);
            }
        }
    }
    
    @Override
    public void addActionListener(final ActionListener actionListener) {
        this.listenerList.add(ActionListener.class, actionListener);
    }
    
    @Override
    public void removeActionListener(final ActionListener actionListener) {
        this.listenerList.remove(ActionListener.class, actionListener);
    }
    
    public ActionListener[] getActionListeners() {
        return this.listenerList.getListeners(ActionListener.class);
    }
    
    protected void fireActionPerformed(final ActionEvent actionEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ActionListener.class) {
                ((ActionListener)listenerList[i + 1]).actionPerformed(actionEvent);
            }
        }
    }
    
    @Override
    public void addItemListener(final ItemListener itemListener) {
        this.listenerList.add(ItemListener.class, itemListener);
    }
    
    @Override
    public void removeItemListener(final ItemListener itemListener) {
        this.listenerList.remove(ItemListener.class, itemListener);
    }
    
    public ItemListener[] getItemListeners() {
        return this.listenerList.getListeners(ItemListener.class);
    }
    
    protected void fireItemStateChanged(final ItemEvent itemEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ItemListener.class) {
                ((ItemListener)listenerList[i + 1]).itemStateChanged(itemEvent);
            }
        }
    }
    
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        return this.listenerList.getListeners(clazz);
    }
    
    @Override
    public Object[] getSelectedObjects() {
        return null;
    }
    
    @Override
    public void setGroup(final ButtonGroup group) {
        this.group = group;
    }
    
    public ButtonGroup getGroup() {
        return this.group;
    }
    
    boolean isMenuItem() {
        return this.menuItem;
    }
    
    void setMenuItem(final boolean menuItem) {
        this.menuItem = menuItem;
    }
}
