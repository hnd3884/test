package javax.swing;

import javax.accessibility.AccessibleTable;
import javax.accessibility.AccessibleRelationSet;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleValue;
import javax.accessibility.AccessibleEditableText;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleComponent;
import java.awt.IllegalComponentStateException;
import java.util.Locale;
import javax.swing.event.ListSelectionEvent;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleRole;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.ComboPopup;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleAction;
import java.io.Serializable;
import java.beans.PropertyChangeEvent;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.event.KeyEvent;
import javax.swing.event.ListDataEvent;
import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.ItemListener;
import java.beans.Transient;
import java.awt.Component;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.util.Vector;
import java.beans.PropertyChangeListener;
import javax.accessibility.Accessible;
import java.awt.event.ActionListener;
import javax.swing.event.ListDataListener;
import java.awt.ItemSelectable;

public class JComboBox<E> extends JComponent implements ItemSelectable, ListDataListener, ActionListener, Accessible
{
    private static final String uiClassID = "ComboBoxUI";
    protected ComboBoxModel<E> dataModel;
    protected ListCellRenderer<? super E> renderer;
    protected ComboBoxEditor editor;
    protected int maximumRowCount;
    protected boolean isEditable;
    protected KeySelectionManager keySelectionManager;
    protected String actionCommand;
    protected boolean lightWeightPopupEnabled;
    protected Object selectedItemReminder;
    private E prototypeDisplayValue;
    private boolean firingActionEvent;
    private boolean selectingItem;
    private Action action;
    private PropertyChangeListener actionPropertyChangeListener;
    
    public JComboBox(final ComboBoxModel<E> model) {
        this.maximumRowCount = 8;
        this.isEditable = false;
        this.keySelectionManager = null;
        this.actionCommand = "comboBoxChanged";
        this.lightWeightPopupEnabled = JPopupMenu.getDefaultLightWeightPopupEnabled();
        this.selectedItemReminder = null;
        this.firingActionEvent = false;
        this.selectingItem = false;
        this.setModel(model);
        this.init();
    }
    
    public JComboBox(final E[] array) {
        this.maximumRowCount = 8;
        this.isEditable = false;
        this.keySelectionManager = null;
        this.actionCommand = "comboBoxChanged";
        this.lightWeightPopupEnabled = JPopupMenu.getDefaultLightWeightPopupEnabled();
        this.selectedItemReminder = null;
        this.firingActionEvent = false;
        this.selectingItem = false;
        this.setModel(new DefaultComboBoxModel<E>(array));
        this.init();
    }
    
    public JComboBox(final Vector<E> vector) {
        this.maximumRowCount = 8;
        this.isEditable = false;
        this.keySelectionManager = null;
        this.actionCommand = "comboBoxChanged";
        this.lightWeightPopupEnabled = JPopupMenu.getDefaultLightWeightPopupEnabled();
        this.selectedItemReminder = null;
        this.firingActionEvent = false;
        this.selectingItem = false;
        this.setModel(new DefaultComboBoxModel<E>(vector));
        this.init();
    }
    
    public JComboBox() {
        this.maximumRowCount = 8;
        this.isEditable = false;
        this.keySelectionManager = null;
        this.actionCommand = "comboBoxChanged";
        this.lightWeightPopupEnabled = JPopupMenu.getDefaultLightWeightPopupEnabled();
        this.selectedItemReminder = null;
        this.firingActionEvent = false;
        this.selectingItem = false;
        this.setModel(new DefaultComboBoxModel<E>());
        this.init();
    }
    
    private void init() {
        this.installAncestorListener();
        this.setUIProperty("opaque", true);
        this.updateUI();
    }
    
    protected void installAncestorListener() {
        this.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(final AncestorEvent ancestorEvent) {
                JComboBox.this.hidePopup();
            }
            
            @Override
            public void ancestorRemoved(final AncestorEvent ancestorEvent) {
                JComboBox.this.hidePopup();
            }
            
            @Override
            public void ancestorMoved(final AncestorEvent ancestorEvent) {
                if (ancestorEvent.getSource() != JComboBox.this) {
                    JComboBox.this.hidePopup();
                }
            }
        });
    }
    
    public void setUI(final ComboBoxUI ui) {
        super.setUI(ui);
    }
    
    @Override
    public void updateUI() {
        this.setUI((ComboBoxUI)UIManager.getUI(this));
        final ListCellRenderer<? super E> renderer = this.getRenderer();
        if (renderer instanceof Component) {
            SwingUtilities.updateComponentTreeUI((Component)renderer);
        }
    }
    
    @Override
    public String getUIClassID() {
        return "ComboBoxUI";
    }
    
    public ComboBoxUI getUI() {
        return (ComboBoxUI)this.ui;
    }
    
    public void setModel(final ComboBoxModel<E> dataModel) {
        final ComboBoxModel<E> dataModel2 = this.dataModel;
        if (dataModel2 != null) {
            dataModel2.removeListDataListener(this);
        }
        (this.dataModel = dataModel).addListDataListener(this);
        this.selectedItemReminder = this.dataModel.getSelectedItem();
        this.firePropertyChange("model", dataModel2, this.dataModel);
    }
    
    public ComboBoxModel<E> getModel() {
        return this.dataModel;
    }
    
    public void setLightWeightPopupEnabled(final boolean lightWeightPopupEnabled) {
        this.firePropertyChange("lightWeightPopupEnabled", this.lightWeightPopupEnabled, this.lightWeightPopupEnabled = lightWeightPopupEnabled);
    }
    
    public boolean isLightWeightPopupEnabled() {
        return this.lightWeightPopupEnabled;
    }
    
    public void setEditable(final boolean isEditable) {
        this.firePropertyChange("editable", this.isEditable, this.isEditable = isEditable);
    }
    
    public boolean isEditable() {
        return this.isEditable;
    }
    
    public void setMaximumRowCount(final int maximumRowCount) {
        this.firePropertyChange("maximumRowCount", this.maximumRowCount, this.maximumRowCount = maximumRowCount);
    }
    
    public int getMaximumRowCount() {
        return this.maximumRowCount;
    }
    
    public void setRenderer(final ListCellRenderer<? super E> renderer) {
        this.firePropertyChange("renderer", this.renderer, this.renderer = renderer);
        this.invalidate();
    }
    
    public ListCellRenderer<? super E> getRenderer() {
        return this.renderer;
    }
    
    public void setEditor(final ComboBoxEditor editor) {
        final ComboBoxEditor editor2 = this.editor;
        if (this.editor != null) {
            this.editor.removeActionListener(this);
        }
        this.editor = editor;
        if (this.editor != null) {
            this.editor.addActionListener(this);
        }
        this.firePropertyChange("editor", editor2, this.editor);
    }
    
    public ComboBoxEditor getEditor() {
        return this.editor;
    }
    
    public void setSelectedItem(final Object o) {
        final Object selectedItemReminder = this.selectedItemReminder;
        Object selectedItem = o;
        if (selectedItemReminder == null || !selectedItemReminder.equals(o)) {
            if (o != null && !this.isEditable()) {
                boolean b = false;
                for (int i = 0; i < this.dataModel.getSize(); ++i) {
                    final Object element = this.dataModel.getElementAt(i);
                    if (o.equals(element)) {
                        b = true;
                        selectedItem = element;
                        break;
                    }
                }
                if (!b) {
                    return;
                }
            }
            this.selectingItem = true;
            this.dataModel.setSelectedItem(selectedItem);
            this.selectingItem = false;
            if (this.selectedItemReminder != this.dataModel.getSelectedItem()) {
                this.selectedItemChanged();
            }
        }
        this.fireActionEvent();
    }
    
    public Object getSelectedItem() {
        return this.dataModel.getSelectedItem();
    }
    
    public void setSelectedIndex(final int n) {
        final int size = this.dataModel.getSize();
        if (n == -1) {
            this.setSelectedItem(null);
        }
        else {
            if (n < -1 || n >= size) {
                throw new IllegalArgumentException("setSelectedIndex: " + n + " out of bounds");
            }
            this.setSelectedItem(this.dataModel.getElementAt(n));
        }
    }
    
    @Transient
    public int getSelectedIndex() {
        final Object selectedItem = this.dataModel.getSelectedItem();
        for (int i = 0; i < this.dataModel.getSize(); ++i) {
            final Object element = this.dataModel.getElementAt(i);
            if (element != null && element.equals(selectedItem)) {
                return i;
            }
        }
        return -1;
    }
    
    public E getPrototypeDisplayValue() {
        return this.prototypeDisplayValue;
    }
    
    public void setPrototypeDisplayValue(final E prototypeDisplayValue) {
        this.firePropertyChange("prototypeDisplayValue", this.prototypeDisplayValue, this.prototypeDisplayValue = prototypeDisplayValue);
    }
    
    public void addItem(final E e) {
        this.checkMutableComboBoxModel();
        ((MutableComboBoxModel)this.dataModel).addElement(e);
    }
    
    public void insertItemAt(final E e, final int n) {
        this.checkMutableComboBoxModel();
        ((MutableComboBoxModel)this.dataModel).insertElementAt(e, n);
    }
    
    public void removeItem(final Object o) {
        this.checkMutableComboBoxModel();
        ((MutableComboBoxModel)this.dataModel).removeElement(o);
    }
    
    public void removeItemAt(final int n) {
        this.checkMutableComboBoxModel();
        ((MutableComboBoxModel)this.dataModel).removeElementAt(n);
    }
    
    public void removeAllItems() {
        this.checkMutableComboBoxModel();
        final MutableComboBoxModel mutableComboBoxModel = (MutableComboBoxModel)this.dataModel;
        final int size = mutableComboBoxModel.getSize();
        if (mutableComboBoxModel instanceof DefaultComboBoxModel) {
            ((DefaultComboBoxModel)mutableComboBoxModel).removeAllElements();
        }
        else {
            for (int i = 0; i < size; ++i) {
                mutableComboBoxModel.removeElement(mutableComboBoxModel.getElementAt(0));
            }
        }
        this.selectedItemReminder = null;
        if (this.isEditable()) {
            this.editor.setItem(null);
        }
    }
    
    void checkMutableComboBoxModel() {
        if (!(this.dataModel instanceof MutableComboBoxModel)) {
            throw new RuntimeException("Cannot use this method with a non-Mutable data model.");
        }
    }
    
    public void showPopup() {
        this.setPopupVisible(true);
    }
    
    public void hidePopup() {
        this.setPopupVisible(false);
    }
    
    public void setPopupVisible(final boolean b) {
        this.getUI().setPopupVisible(this, b);
    }
    
    public boolean isPopupVisible() {
        return this.getUI().isPopupVisible(this);
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
    
    public void addActionListener(final ActionListener actionListener) {
        this.listenerList.add(ActionListener.class, actionListener);
    }
    
    public void removeActionListener(final ActionListener actionListener) {
        if (actionListener != null && this.getAction() == actionListener) {
            this.setAction(null);
        }
        else {
            this.listenerList.remove(ActionListener.class, actionListener);
        }
    }
    
    public ActionListener[] getActionListeners() {
        return this.listenerList.getListeners(ActionListener.class);
    }
    
    public void addPopupMenuListener(final PopupMenuListener popupMenuListener) {
        this.listenerList.add(PopupMenuListener.class, popupMenuListener);
    }
    
    public void removePopupMenuListener(final PopupMenuListener popupMenuListener) {
        this.listenerList.remove(PopupMenuListener.class, popupMenuListener);
    }
    
    public PopupMenuListener[] getPopupMenuListeners() {
        return this.listenerList.getListeners(PopupMenuListener.class);
    }
    
    public void firePopupMenuWillBecomeVisible() {
        final Object[] listenerList = this.listenerList.getListenerList();
        PopupMenuEvent popupMenuEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == PopupMenuListener.class) {
                if (popupMenuEvent == null) {
                    popupMenuEvent = new PopupMenuEvent(this);
                }
                ((PopupMenuListener)listenerList[i + 1]).popupMenuWillBecomeVisible(popupMenuEvent);
            }
        }
    }
    
    public void firePopupMenuWillBecomeInvisible() {
        final Object[] listenerList = this.listenerList.getListenerList();
        PopupMenuEvent popupMenuEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == PopupMenuListener.class) {
                if (popupMenuEvent == null) {
                    popupMenuEvent = new PopupMenuEvent(this);
                }
                ((PopupMenuListener)listenerList[i + 1]).popupMenuWillBecomeInvisible(popupMenuEvent);
            }
        }
    }
    
    public void firePopupMenuCanceled() {
        final Object[] listenerList = this.listenerList.getListenerList();
        PopupMenuEvent popupMenuEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == PopupMenuListener.class) {
                if (popupMenuEvent == null) {
                    popupMenuEvent = new PopupMenuEvent(this);
                }
                ((PopupMenuListener)listenerList[i + 1]).popupMenuCanceled(popupMenuEvent);
            }
        }
    }
    
    public void setActionCommand(final String actionCommand) {
        this.actionCommand = actionCommand;
    }
    
    public String getActionCommand() {
        return this.actionCommand;
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
    
    protected PropertyChangeListener createActionPropertyChangeListener(final Action action) {
        return new ComboBoxActionPropertyChangeListener(this, action);
    }
    
    protected void actionPropertyChanged(final Action actionCommandFromAction, final String s) {
        if (s == "ActionCommandKey") {
            this.setActionCommandFromAction(actionCommandFromAction);
        }
        else if (s == "enabled") {
            AbstractAction.setEnabledFromAction(this, actionCommandFromAction);
        }
        else if ("ShortDescription" == s) {
            AbstractAction.setToolTipTextFromAction(this, actionCommandFromAction);
        }
    }
    
    private void setActionCommandFromAction(final Action action) {
        this.setActionCommand((action != null) ? ((String)action.getValue("ActionCommandKey")) : null);
    }
    
    protected void fireItemStateChanged(final ItemEvent itemEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ItemListener.class) {
                ((ItemListener)listenerList[i + 1]).itemStateChanged(itemEvent);
            }
        }
    }
    
    protected void fireActionEvent() {
        if (!this.firingActionEvent) {
            this.firingActionEvent = true;
            ActionEvent actionEvent = null;
            final Object[] listenerList = this.listenerList.getListenerList();
            final long mostRecentEventTime = EventQueue.getMostRecentEventTime();
            int n = 0;
            final AWTEvent currentEvent = EventQueue.getCurrentEvent();
            if (currentEvent instanceof InputEvent) {
                n = ((InputEvent)currentEvent).getModifiers();
            }
            else if (currentEvent instanceof ActionEvent) {
                n = ((ActionEvent)currentEvent).getModifiers();
            }
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == ActionListener.class) {
                    if (actionEvent == null) {
                        actionEvent = new ActionEvent(this, 1001, this.getActionCommand(), mostRecentEventTime, n);
                    }
                    ((ActionListener)listenerList[i + 1]).actionPerformed(actionEvent);
                }
            }
            this.firingActionEvent = false;
        }
    }
    
    protected void selectedItemChanged() {
        if (this.selectedItemReminder != null) {
            this.fireItemStateChanged(new ItemEvent(this, 701, this.selectedItemReminder, 2));
        }
        this.selectedItemReminder = this.dataModel.getSelectedItem();
        if (this.selectedItemReminder != null) {
            this.fireItemStateChanged(new ItemEvent(this, 701, this.selectedItemReminder, 1));
        }
    }
    
    @Override
    public Object[] getSelectedObjects() {
        final Object selectedItem = this.getSelectedItem();
        if (selectedItem == null) {
            return new Object[0];
        }
        return new Object[] { selectedItem };
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        final ComboBoxEditor editor = this.getEditor();
        if (editor != null && actionEvent != null && (editor == actionEvent.getSource() || editor.getEditorComponent() == actionEvent.getSource())) {
            this.setPopupVisible(false);
            this.getModel().setSelectedItem(editor.getItem());
            final String actionCommand = this.getActionCommand();
            this.setActionCommand("comboBoxEdited");
            this.fireActionEvent();
            this.setActionCommand(actionCommand);
        }
    }
    
    @Override
    public void contentsChanged(final ListDataEvent listDataEvent) {
        final Object selectedItemReminder = this.selectedItemReminder;
        final Object selectedItem = this.dataModel.getSelectedItem();
        if (selectedItemReminder == null || !selectedItemReminder.equals(selectedItem)) {
            this.selectedItemChanged();
            if (!this.selectingItem) {
                this.fireActionEvent();
            }
        }
    }
    
    @Override
    public void intervalAdded(final ListDataEvent listDataEvent) {
        if (this.selectedItemReminder != this.dataModel.getSelectedItem()) {
            this.selectedItemChanged();
        }
    }
    
    @Override
    public void intervalRemoved(final ListDataEvent listDataEvent) {
        this.contentsChanged(listDataEvent);
    }
    
    public boolean selectWithKeyChar(final char c) {
        if (this.keySelectionManager == null) {
            this.keySelectionManager = this.createDefaultKeySelectionManager();
        }
        final int selectionForKey = this.keySelectionManager.selectionForKey(c, this.getModel());
        if (selectionForKey != -1) {
            this.setSelectedIndex(selectionForKey);
            return true;
        }
        return false;
    }
    
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        this.firePropertyChange("enabled", !this.isEnabled(), this.isEnabled());
    }
    
    public void configureEditor(final ComboBoxEditor comboBoxEditor, final Object item) {
        comboBoxEditor.setItem(item);
    }
    
    public void processKeyEvent(final KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 9) {
            this.hidePopup();
        }
        super.processKeyEvent(keyEvent);
    }
    
    @Override
    protected boolean processKeyBinding(final KeyStroke keyStroke, final KeyEvent keyEvent, final int n, final boolean b) {
        if (super.processKeyBinding(keyStroke, keyEvent, n, b)) {
            return true;
        }
        if (!this.isEditable() || n != 0 || this.getEditor() == null || !Boolean.TRUE.equals(this.getClientProperty("JComboBox.isTableCellEditor"))) {
            return false;
        }
        final Component editorComponent = this.getEditor().getEditorComponent();
        return editorComponent instanceof JComponent && ((JComponent)editorComponent).processKeyBinding(keyStroke, keyEvent, 0, b);
    }
    
    public void setKeySelectionManager(final KeySelectionManager keySelectionManager) {
        this.keySelectionManager = keySelectionManager;
    }
    
    public KeySelectionManager getKeySelectionManager() {
        return this.keySelectionManager;
    }
    
    public int getItemCount() {
        return this.dataModel.getSize();
    }
    
    public E getItemAt(final int n) {
        return this.dataModel.getElementAt(n);
    }
    
    protected KeySelectionManager createDefaultKeySelectionManager() {
        return new DefaultKeySelectionManager();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("ComboBoxUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",isEditable=" + (this.isEditable ? "true" : "false") + ",lightWeightPopupEnabled=" + (this.lightWeightPopupEnabled ? "true" : "false") + ",maximumRowCount=" + this.maximumRowCount + ",selectedItemReminder=" + ((this.selectedItemReminder != null) ? this.selectedItemReminder.toString() : "");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJComboBox();
        }
        return this.accessibleContext;
    }
    
    private static class ComboBoxActionPropertyChangeListener extends ActionPropertyChangeListener<JComboBox<?>>
    {
        ComboBoxActionPropertyChangeListener(final JComboBox<?> comboBox, final Action action) {
            super(comboBox, action);
        }
        
        @Override
        protected void actionPropertyChanged(final JComboBox<?> comboBox, final Action action, final PropertyChangeEvent propertyChangeEvent) {
            if (AbstractAction.shouldReconfigure(propertyChangeEvent)) {
                comboBox.configurePropertiesFromAction(action);
            }
            else {
                comboBox.actionPropertyChanged(action, propertyChangeEvent.getPropertyName());
            }
        }
    }
    
    class DefaultKeySelectionManager implements KeySelectionManager, Serializable
    {
        @Override
        public int selectionForKey(final char c, final ComboBoxModel comboBoxModel) {
            int n = -1;
            final Object selectedItem = comboBoxModel.getSelectedItem();
            if (selectedItem != null) {
                for (int i = 0; i < comboBoxModel.getSize(); ++i) {
                    if (selectedItem == comboBoxModel.getElementAt(i)) {
                        n = i;
                        break;
                    }
                }
            }
            final char char1 = ("" + c).toLowerCase().charAt(0);
            for (int j = ++n; j < comboBoxModel.getSize(); ++j) {
                final Object element = comboBoxModel.getElementAt(j);
                if (element != null && element.toString() != null) {
                    final String lowerCase = element.toString().toLowerCase();
                    if (lowerCase.length() > 0 && lowerCase.charAt(0) == char1) {
                        return j;
                    }
                }
            }
            for (int k = 0; k < n; ++k) {
                final Object element2 = comboBoxModel.getElementAt(k);
                if (element2 != null && element2.toString() != null) {
                    final String lowerCase2 = element2.toString().toLowerCase();
                    if (lowerCase2.length() > 0 && lowerCase2.charAt(0) == char1) {
                        return k;
                    }
                }
            }
            return -1;
        }
    }
    
    protected class AccessibleJComboBox extends AccessibleJComponent implements AccessibleAction, AccessibleSelection
    {
        private JList popupList;
        private Accessible previousSelectedAccessible;
        private EditorAccessibleContext editorAccessibleContext;
        
        public AccessibleJComboBox() {
            this.previousSelectedAccessible = null;
            this.editorAccessibleContext = null;
            JComboBox.this.addPropertyChangeListener(new AccessibleJComboBoxPropertyChangeListener());
            this.setEditorNameAndDescription();
            final Accessible accessibleChild = JComboBox.this.getUI().getAccessibleChild(JComboBox.this, 0);
            if (accessibleChild instanceof ComboPopup) {
                (this.popupList = ((ComboPopup)accessibleChild).getList()).addListSelectionListener(new AccessibleJComboBoxListSelectionListener());
            }
            JComboBox.this.addPopupMenuListener(new AccessibleJComboBoxPopupMenuListener());
        }
        
        private void setEditorNameAndDescription() {
            final ComboBoxEditor editor = JComboBox.this.getEditor();
            if (editor != null) {
                final Component editorComponent = editor.getEditorComponent();
                if (editorComponent instanceof Accessible) {
                    final AccessibleContext accessibleContext = editorComponent.getAccessibleContext();
                    if (accessibleContext != null) {
                        accessibleContext.setAccessibleName(this.getAccessibleName());
                        accessibleContext.setAccessibleDescription(this.getAccessibleDescription());
                    }
                }
            }
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            if (JComboBox.this.ui != null) {
                return JComboBox.this.ui.getAccessibleChildrenCount(JComboBox.this);
            }
            return super.getAccessibleChildrenCount();
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            if (JComboBox.this.ui != null) {
                return JComboBox.this.ui.getAccessibleChild(JComboBox.this, n);
            }
            return super.getAccessibleChild(n);
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.COMBO_BOX;
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (accessibleStateSet == null) {
                accessibleStateSet = new AccessibleStateSet();
            }
            if (JComboBox.this.isPopupVisible()) {
                accessibleStateSet.add(AccessibleState.EXPANDED);
            }
            else {
                accessibleStateSet.add(AccessibleState.COLLAPSED);
            }
            return accessibleStateSet;
        }
        
        @Override
        public AccessibleAction getAccessibleAction() {
            return this;
        }
        
        @Override
        public String getAccessibleActionDescription(final int n) {
            if (n == 0) {
                return UIManager.getString("ComboBox.togglePopupText");
            }
            return null;
        }
        
        @Override
        public int getAccessibleActionCount() {
            return 1;
        }
        
        @Override
        public boolean doAccessibleAction(final int n) {
            if (n == 0) {
                JComboBox.this.setPopupVisible(!JComboBox.this.isPopupVisible());
                return true;
            }
            return false;
        }
        
        @Override
        public AccessibleSelection getAccessibleSelection() {
            return this;
        }
        
        @Override
        public int getAccessibleSelectionCount() {
            if (JComboBox.this.getSelectedItem() != null) {
                return 1;
            }
            return 0;
        }
        
        @Override
        public Accessible getAccessibleSelection(final int n) {
            final Accessible accessibleChild = JComboBox.this.getUI().getAccessibleChild(JComboBox.this, 0);
            if (accessibleChild != null && accessibleChild instanceof ComboPopup) {
                final AccessibleContext accessibleContext = ((ComboPopup)accessibleChild).getList().getAccessibleContext();
                if (accessibleContext != null) {
                    final AccessibleSelection accessibleSelection = accessibleContext.getAccessibleSelection();
                    if (accessibleSelection != null) {
                        return accessibleSelection.getAccessibleSelection(n);
                    }
                }
            }
            return null;
        }
        
        @Override
        public boolean isAccessibleChildSelected(final int n) {
            return JComboBox.this.getSelectedIndex() == n;
        }
        
        @Override
        public void addAccessibleSelection(final int selectedIndex) {
            this.clearAccessibleSelection();
            JComboBox.this.setSelectedIndex(selectedIndex);
        }
        
        @Override
        public void removeAccessibleSelection(final int n) {
            if (JComboBox.this.getSelectedIndex() == n) {
                this.clearAccessibleSelection();
            }
        }
        
        @Override
        public void clearAccessibleSelection() {
            JComboBox.this.setSelectedIndex(-1);
        }
        
        @Override
        public void selectAllAccessibleSelection() {
        }
        
        private class AccessibleJComboBoxPropertyChangeListener implements PropertyChangeListener
        {
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getPropertyName() == "editor") {
                    AccessibleJComboBox.this.setEditorNameAndDescription();
                }
            }
        }
        
        private class AccessibleJComboBoxPopupMenuListener implements PopupMenuListener
        {
            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent popupMenuEvent) {
                if (AccessibleJComboBox.this.popupList == null) {
                    return;
                }
                final int selectedIndex = AccessibleJComboBox.this.popupList.getSelectedIndex();
                if (selectedIndex < 0) {
                    return;
                }
                AccessibleJComboBox.this.previousSelectedAccessible = AccessibleJComboBox.this.popupList.getAccessibleContext().getAccessibleChild(selectedIndex);
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(final PopupMenuEvent popupMenuEvent) {
            }
            
            @Override
            public void popupMenuCanceled(final PopupMenuEvent popupMenuEvent) {
            }
        }
        
        private class AccessibleJComboBoxListSelectionListener implements ListSelectionListener
        {
            @Override
            public void valueChanged(final ListSelectionEvent listSelectionEvent) {
                if (AccessibleJComboBox.this.popupList == null) {
                    return;
                }
                final int selectedIndex = AccessibleJComboBox.this.popupList.getSelectedIndex();
                if (selectedIndex < 0) {
                    return;
                }
                final Accessible accessibleChild = AccessibleJComboBox.this.popupList.getAccessibleContext().getAccessibleChild(selectedIndex);
                if (accessibleChild == null) {
                    return;
                }
                if (AccessibleJComboBox.this.previousSelectedAccessible != null) {
                    AccessibleJComboBox.this.firePropertyChange("AccessibleState", null, new PropertyChangeEvent(AccessibleJComboBox.this.previousSelectedAccessible, "AccessibleState", AccessibleState.FOCUSED, null));
                }
                AccessibleJComboBox.this.firePropertyChange("AccessibleState", null, new PropertyChangeEvent(accessibleChild, "AccessibleState", null, AccessibleState.FOCUSED));
                AccessibleJComboBox.this.firePropertyChange("AccessibleActiveDescendant", AccessibleJComboBox.this.previousSelectedAccessible, accessibleChild);
                AccessibleJComboBox.this.previousSelectedAccessible = accessibleChild;
            }
        }
        
        private class AccessibleEditor implements Accessible
        {
            @Override
            public AccessibleContext getAccessibleContext() {
                if (AccessibleJComboBox.this.editorAccessibleContext == null) {
                    final Component editorComponent = JComboBox.this.getEditor().getEditorComponent();
                    if (editorComponent instanceof Accessible) {
                        AccessibleJComboBox.this.editorAccessibleContext = new EditorAccessibleContext((Accessible)editorComponent);
                    }
                }
                return AccessibleJComboBox.this.editorAccessibleContext;
            }
        }
        
        private class EditorAccessibleContext extends AccessibleContext
        {
            private AccessibleContext ac;
            
            private EditorAccessibleContext() {
            }
            
            EditorAccessibleContext(final Accessible accessible) {
                this.ac = accessible.getAccessibleContext();
            }
            
            @Override
            public String getAccessibleName() {
                return this.ac.getAccessibleName();
            }
            
            @Override
            public void setAccessibleName(final String accessibleName) {
                this.ac.setAccessibleName(accessibleName);
            }
            
            @Override
            public String getAccessibleDescription() {
                return this.ac.getAccessibleDescription();
            }
            
            @Override
            public void setAccessibleDescription(final String accessibleDescription) {
                this.ac.setAccessibleDescription(accessibleDescription);
            }
            
            @Override
            public AccessibleRole getAccessibleRole() {
                return this.ac.getAccessibleRole();
            }
            
            @Override
            public AccessibleStateSet getAccessibleStateSet() {
                return this.ac.getAccessibleStateSet();
            }
            
            @Override
            public Accessible getAccessibleParent() {
                return this.ac.getAccessibleParent();
            }
            
            @Override
            public void setAccessibleParent(final Accessible accessibleParent) {
                this.ac.setAccessibleParent(accessibleParent);
            }
            
            @Override
            public int getAccessibleIndexInParent() {
                return JComboBox.this.getSelectedIndex();
            }
            
            @Override
            public int getAccessibleChildrenCount() {
                return this.ac.getAccessibleChildrenCount();
            }
            
            @Override
            public Accessible getAccessibleChild(final int n) {
                return this.ac.getAccessibleChild(n);
            }
            
            @Override
            public Locale getLocale() throws IllegalComponentStateException {
                return this.ac.getLocale();
            }
            
            @Override
            public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
                this.ac.addPropertyChangeListener(propertyChangeListener);
            }
            
            @Override
            public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
                this.ac.removePropertyChangeListener(propertyChangeListener);
            }
            
            @Override
            public AccessibleAction getAccessibleAction() {
                return this.ac.getAccessibleAction();
            }
            
            @Override
            public AccessibleComponent getAccessibleComponent() {
                return this.ac.getAccessibleComponent();
            }
            
            @Override
            public AccessibleSelection getAccessibleSelection() {
                return this.ac.getAccessibleSelection();
            }
            
            @Override
            public AccessibleText getAccessibleText() {
                return this.ac.getAccessibleText();
            }
            
            @Override
            public AccessibleEditableText getAccessibleEditableText() {
                return this.ac.getAccessibleEditableText();
            }
            
            @Override
            public AccessibleValue getAccessibleValue() {
                return this.ac.getAccessibleValue();
            }
            
            @Override
            public AccessibleIcon[] getAccessibleIcon() {
                return this.ac.getAccessibleIcon();
            }
            
            @Override
            public AccessibleRelationSet getAccessibleRelationSet() {
                return this.ac.getAccessibleRelationSet();
            }
            
            @Override
            public AccessibleTable getAccessibleTable() {
                return this.ac.getAccessibleTable();
            }
            
            @Override
            public void firePropertyChange(final String s, final Object o, final Object o2) {
                this.ac.firePropertyChange(s, o, o2);
            }
        }
    }
    
    public interface KeySelectionManager
    {
        int selectionForKey(final char p0, final ComboBoxModel p1);
    }
}
