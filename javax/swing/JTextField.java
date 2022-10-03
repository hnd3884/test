package javax.swing;

import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.event.ChangeEvent;
import java.io.Serializable;
import java.beans.PropertyChangeEvent;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.Rectangle;
import javax.swing.text.TextAction;
import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.text.PlainDocument;
import java.awt.Component;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import java.beans.PropertyChangeListener;
import javax.swing.text.JTextComponent;

public class JTextField extends JTextComponent implements SwingConstants
{
    private Action action;
    private PropertyChangeListener actionPropertyChangeListener;
    public static final String notifyAction = "notify-field-accept";
    private BoundedRangeModel visibility;
    private int horizontalAlignment;
    private int columns;
    private int columnWidth;
    private String command;
    private static final Action[] defaultActions;
    private static final String uiClassID = "TextFieldUI";
    
    public JTextField() {
        this(null, null, 0);
    }
    
    public JTextField(final String s) {
        this(null, s, 0);
    }
    
    public JTextField(final int n) {
        this(null, null, n);
    }
    
    public JTextField(final String s, final int n) {
        this(null, s, n);
    }
    
    public JTextField(Document defaultModel, final String text, final int columns) {
        this.horizontalAlignment = 10;
        if (columns < 0) {
            throw new IllegalArgumentException("columns less than zero.");
        }
        (this.visibility = new DefaultBoundedRangeModel()).addChangeListener(new ScrollRepainter());
        this.columns = columns;
        if (defaultModel == null) {
            defaultModel = this.createDefaultModel();
        }
        this.setDocument(defaultModel);
        if (text != null) {
            this.setText(text);
        }
    }
    
    @Override
    public String getUIClassID() {
        return "TextFieldUI";
    }
    
    @Override
    public void setDocument(final Document document) {
        if (document != null) {
            document.putProperty("filterNewlines", Boolean.TRUE);
        }
        super.setDocument(document);
    }
    
    @Override
    public boolean isValidateRoot() {
        return !(SwingUtilities.getUnwrappedParent(this) instanceof JViewport);
    }
    
    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }
    
    public void setHorizontalAlignment(final int horizontalAlignment) {
        if (horizontalAlignment == this.horizontalAlignment) {
            return;
        }
        final int horizontalAlignment2 = this.horizontalAlignment;
        if (horizontalAlignment == 2 || horizontalAlignment == 0 || horizontalAlignment == 4 || horizontalAlignment == 10 || horizontalAlignment == 11) {
            this.firePropertyChange("horizontalAlignment", horizontalAlignment2, this.horizontalAlignment = horizontalAlignment);
            this.invalidate();
            this.repaint();
            return;
        }
        throw new IllegalArgumentException("horizontalAlignment");
    }
    
    protected Document createDefaultModel() {
        return new PlainDocument();
    }
    
    public int getColumns() {
        return this.columns;
    }
    
    public void setColumns(final int columns) {
        final int columns2 = this.columns;
        if (columns < 0) {
            throw new IllegalArgumentException("columns less than zero.");
        }
        if (columns != columns2) {
            this.columns = columns;
            this.invalidate();
        }
    }
    
    protected int getColumnWidth() {
        if (this.columnWidth == 0) {
            this.columnWidth = this.getFontMetrics(this.getFont()).charWidth('m');
        }
        return this.columnWidth;
    }
    
    @Override
    public Dimension getPreferredSize() {
        final Dimension preferredSize = super.getPreferredSize();
        if (this.columns != 0) {
            final Insets insets = this.getInsets();
            preferredSize.width = this.columns * this.getColumnWidth() + insets.left + insets.right;
        }
        return preferredSize;
    }
    
    @Override
    public void setFont(final Font font) {
        super.setFont(font);
        this.columnWidth = 0;
    }
    
    public synchronized void addActionListener(final ActionListener actionListener) {
        this.listenerList.add(ActionListener.class, actionListener);
    }
    
    public synchronized void removeActionListener(final ActionListener actionListener) {
        if (actionListener != null && this.getAction() == actionListener) {
            this.setAction(null);
        }
        else {
            this.listenerList.remove(ActionListener.class, actionListener);
        }
    }
    
    public synchronized ActionListener[] getActionListeners() {
        return this.listenerList.getListeners(ActionListener.class);
    }
    
    protected void fireActionPerformed() {
        final Object[] listenerList = this.listenerList.getListenerList();
        int n = 0;
        final AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof InputEvent) {
            n = ((InputEvent)currentEvent).getModifiers();
        }
        else if (currentEvent instanceof ActionEvent) {
            n = ((ActionEvent)currentEvent).getModifiers();
        }
        final ActionEvent actionEvent = new ActionEvent(this, 1001, (this.command != null) ? this.command : this.getText(), EventQueue.getMostRecentEventTime(), n);
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ActionListener.class) {
                ((ActionListener)listenerList[i + 1]).actionPerformed(actionEvent);
            }
        }
    }
    
    public void setActionCommand(final String command) {
        this.command = command;
    }
    
    public void setAction(final Action action) {
        final Action action2 = this.getAction();
        if (this.action == null || !this.action.equals(action)) {
            this.action = action;
            if (action2 != null) {
                this.removeActionListener(action2);
                action2.removePropertyChangeListener(this.actionPropertyChangeListener);
                this.actionPropertyChangeListener = null;
            }
            this.configurePropertiesFromAction(this.action);
            if (this.action != null) {
                if (!this.isListener(ActionListener.class, this.action)) {
                    this.addActionListener(this.action);
                }
                this.actionPropertyChangeListener = this.createActionPropertyChangeListener(this.action);
                this.action.addPropertyChangeListener(this.actionPropertyChangeListener);
            }
            this.firePropertyChange("action", action2, this.action);
        }
    }
    
    private boolean isListener(final Class clazz, final ActionListener actionListener) {
        boolean b = false;
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == clazz && listenerList[i + 1] == actionListener) {
                b = true;
            }
        }
        return b;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    protected void configurePropertiesFromAction(final Action actionCommandFromAction) {
        AbstractAction.setEnabledFromAction(this, actionCommandFromAction);
        AbstractAction.setToolTipTextFromAction(this, actionCommandFromAction);
        this.setActionCommandFromAction(actionCommandFromAction);
    }
    
    protected void actionPropertyChanged(final Action actionCommandFromAction, final String s) {
        if (s == "ActionCommandKey") {
            this.setActionCommandFromAction(actionCommandFromAction);
        }
        else if (s == "enabled") {
            AbstractAction.setEnabledFromAction(this, actionCommandFromAction);
        }
        else if (s == "ShortDescription") {
            AbstractAction.setToolTipTextFromAction(this, actionCommandFromAction);
        }
    }
    
    private void setActionCommandFromAction(final Action action) {
        this.setActionCommand((action == null) ? null : ((String)action.getValue("ActionCommandKey")));
    }
    
    protected PropertyChangeListener createActionPropertyChangeListener(final Action action) {
        return new TextFieldActionPropertyChangeListener(this, action);
    }
    
    @Override
    public Action[] getActions() {
        return TextAction.augmentList(super.getActions(), JTextField.defaultActions);
    }
    
    public void postActionEvent() {
        this.fireActionPerformed();
    }
    
    public BoundedRangeModel getHorizontalVisibility() {
        return this.visibility;
    }
    
    public int getScrollOffset() {
        return this.visibility.getValue();
    }
    
    public void setScrollOffset(final int value) {
        this.visibility.setValue(value);
    }
    
    @Override
    public void scrollRectToVisible(final Rectangle rectangle) {
        final int value = rectangle.x + this.visibility.getValue() - this.getInsets().left;
        final int n = value + rectangle.width;
        if (value < this.visibility.getValue()) {
            this.visibility.setValue(value);
        }
        else if (n > this.visibility.getValue() + this.visibility.getExtent()) {
            this.visibility.setValue(n - this.visibility.getExtent());
        }
    }
    
    boolean hasActionListener() {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ActionListener.class) {
                return true;
            }
        }
        return false;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("TextFieldUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        String s;
        if (this.horizontalAlignment == 2) {
            s = "LEFT";
        }
        else if (this.horizontalAlignment == 0) {
            s = "CENTER";
        }
        else if (this.horizontalAlignment == 4) {
            s = "RIGHT";
        }
        else if (this.horizontalAlignment == 10) {
            s = "LEADING";
        }
        else if (this.horizontalAlignment == 11) {
            s = "TRAILING";
        }
        else {
            s = "";
        }
        return super.paramString() + ",columns=" + this.columns + ",columnWidth=" + this.columnWidth + ",command=" + ((this.command != null) ? this.command : "") + ",horizontalAlignment=" + s;
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJTextField();
        }
        return this.accessibleContext;
    }
    
    static {
        defaultActions = new Action[] { new NotifyAction() };
    }
    
    private static class TextFieldActionPropertyChangeListener extends ActionPropertyChangeListener<JTextField>
    {
        TextFieldActionPropertyChangeListener(final JTextField textField, final Action action) {
            super(textField, action);
        }
        
        @Override
        protected void actionPropertyChanged(final JTextField textField, final Action action, final PropertyChangeEvent propertyChangeEvent) {
            if (AbstractAction.shouldReconfigure(propertyChangeEvent)) {
                textField.configurePropertiesFromAction(action);
            }
            else {
                textField.actionPropertyChanged(action, propertyChangeEvent.getPropertyName());
            }
        }
    }
    
    static class NotifyAction extends TextAction
    {
        NotifyAction() {
            super("notify-field-accept");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent focusedComponent = this.getFocusedComponent();
            if (focusedComponent instanceof JTextField) {
                ((JTextField)focusedComponent).postActionEvent();
            }
        }
        
        @Override
        public boolean isEnabled() {
            final JTextComponent focusedComponent = this.getFocusedComponent();
            return focusedComponent instanceof JTextField && ((JTextField)focusedComponent).hasActionListener();
        }
    }
    
    class ScrollRepainter implements ChangeListener, Serializable
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            JTextField.this.repaint();
        }
    }
    
    protected class AccessibleJTextField extends AccessibleJTextComponent
    {
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            accessibleStateSet.add(AccessibleState.SINGLE_LINE);
            return accessibleStateSet;
        }
    }
}
