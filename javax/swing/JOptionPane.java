package javax.swing;

import javax.accessibility.AccessibleRole;
import java.lang.reflect.AccessibleObject;
import javax.accessibility.AccessibleContext;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;
import java.io.ObjectOutputStream;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.OptionPaneUI;
import java.awt.Point;
import java.awt.Dimension;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameAdapter;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.lang.reflect.Method;
import java.awt.KeyboardFocusManager;
import java.awt.Container;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.HeadlessException;
import java.awt.Component;
import javax.accessibility.Accessible;

public class JOptionPane extends JComponent implements Accessible
{
    private static final String uiClassID = "OptionPaneUI";
    public static final Object UNINITIALIZED_VALUE;
    public static final int DEFAULT_OPTION = -1;
    public static final int YES_NO_OPTION = 0;
    public static final int YES_NO_CANCEL_OPTION = 1;
    public static final int OK_CANCEL_OPTION = 2;
    public static final int YES_OPTION = 0;
    public static final int NO_OPTION = 1;
    public static final int CANCEL_OPTION = 2;
    public static final int OK_OPTION = 0;
    public static final int CLOSED_OPTION = -1;
    public static final int ERROR_MESSAGE = 0;
    public static final int INFORMATION_MESSAGE = 1;
    public static final int WARNING_MESSAGE = 2;
    public static final int QUESTION_MESSAGE = 3;
    public static final int PLAIN_MESSAGE = -1;
    public static final String ICON_PROPERTY = "icon";
    public static final String MESSAGE_PROPERTY = "message";
    public static final String VALUE_PROPERTY = "value";
    public static final String OPTIONS_PROPERTY = "options";
    public static final String INITIAL_VALUE_PROPERTY = "initialValue";
    public static final String MESSAGE_TYPE_PROPERTY = "messageType";
    public static final String OPTION_TYPE_PROPERTY = "optionType";
    public static final String SELECTION_VALUES_PROPERTY = "selectionValues";
    public static final String INITIAL_SELECTION_VALUE_PROPERTY = "initialSelectionValue";
    public static final String INPUT_VALUE_PROPERTY = "inputValue";
    public static final String WANTS_INPUT_PROPERTY = "wantsInput";
    protected transient Icon icon;
    protected transient Object message;
    protected transient Object[] options;
    protected transient Object initialValue;
    protected int messageType;
    protected int optionType;
    protected transient Object value;
    protected transient Object[] selectionValues;
    protected transient Object inputValue;
    protected transient Object initialSelectionValue;
    protected boolean wantsInput;
    private static final Object sharedFrameKey;
    
    public static String showInputDialog(final Object o) throws HeadlessException {
        return showInputDialog(null, o);
    }
    
    public static String showInputDialog(final Object o, final Object o2) {
        return showInputDialog(null, o, o2);
    }
    
    public static String showInputDialog(final Component component, final Object o) throws HeadlessException {
        return showInputDialog(component, o, UIManager.getString("OptionPane.inputDialogTitle", component), 3);
    }
    
    public static String showInputDialog(final Component component, final Object o, final Object o2) {
        return (String)showInputDialog(component, o, UIManager.getString("OptionPane.inputDialogTitle", component), 3, null, null, o2);
    }
    
    public static String showInputDialog(final Component component, final Object o, final String s, final int n) throws HeadlessException {
        return (String)showInputDialog(component, o, s, n, null, null, null);
    }
    
    public static Object showInputDialog(final Component component, final Object o, final String s, final int n, final Icon icon, final Object[] selectionValues, final Object initialSelectionValue) throws HeadlessException {
        final JOptionPane optionPane = new JOptionPane(o, n, 2, icon, null, null);
        optionPane.setWantsInput(true);
        optionPane.setSelectionValues(selectionValues);
        optionPane.setInitialSelectionValue(initialSelectionValue);
        optionPane.setComponentOrientation(((component == null) ? getRootFrame() : component).getComponentOrientation());
        final JDialog dialog = optionPane.createDialog(component, s, styleFromMessageType(n));
        optionPane.selectInitialValue();
        dialog.show();
        dialog.dispose();
        final Object inputValue = optionPane.getInputValue();
        if (inputValue == JOptionPane.UNINITIALIZED_VALUE) {
            return null;
        }
        return inputValue;
    }
    
    public static void showMessageDialog(final Component component, final Object o) throws HeadlessException {
        showMessageDialog(component, o, UIManager.getString("OptionPane.messageDialogTitle", component), 1);
    }
    
    public static void showMessageDialog(final Component component, final Object o, final String s, final int n) throws HeadlessException {
        showMessageDialog(component, o, s, n, null);
    }
    
    public static void showMessageDialog(final Component component, final Object o, final String s, final int n, final Icon icon) throws HeadlessException {
        showOptionDialog(component, o, s, -1, n, icon, null, null);
    }
    
    public static int showConfirmDialog(final Component component, final Object o) throws HeadlessException {
        return showConfirmDialog(component, o, UIManager.getString("OptionPane.titleText"), 1);
    }
    
    public static int showConfirmDialog(final Component component, final Object o, final String s, final int n) throws HeadlessException {
        return showConfirmDialog(component, o, s, n, 3);
    }
    
    public static int showConfirmDialog(final Component component, final Object o, final String s, final int n, final int n2) throws HeadlessException {
        return showConfirmDialog(component, o, s, n, n2, null);
    }
    
    public static int showConfirmDialog(final Component component, final Object o, final String s, final int n, final int n2, final Icon icon) throws HeadlessException {
        return showOptionDialog(component, o, s, n, n2, icon, null, null);
    }
    
    public static int showOptionDialog(final Component component, final Object o, final String s, final int n, final int n2, final Icon icon, final Object[] array, final Object initialValue) throws HeadlessException {
        final JOptionPane optionPane = new JOptionPane(o, n2, n, icon, array, initialValue);
        optionPane.setInitialValue(initialValue);
        optionPane.setComponentOrientation(((component == null) ? getRootFrame() : component).getComponentOrientation());
        final JDialog dialog = optionPane.createDialog(component, s, styleFromMessageType(n2));
        optionPane.selectInitialValue();
        dialog.show();
        dialog.dispose();
        final Object value = optionPane.getValue();
        if (value == null) {
            return -1;
        }
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i].equals(value)) {
                    return i;
                }
            }
            return -1;
        }
        if (value instanceof Integer) {
            return (int)value;
        }
        return -1;
    }
    
    public JDialog createDialog(final Component component, final String s) throws HeadlessException {
        return this.createDialog(component, s, styleFromMessageType(this.getMessageType()));
    }
    
    public JDialog createDialog(final String s) throws HeadlessException {
        final int styleFromMessageType = styleFromMessageType(this.getMessageType());
        final JDialog dialog = new JDialog((Dialog)null, s, true);
        this.initDialog(dialog, styleFromMessageType, null);
        return dialog;
    }
    
    private JDialog createDialog(final Component component, final String s, final int n) throws HeadlessException {
        final Window windowForComponent = getWindowForComponent(component);
        JDialog dialog;
        if (windowForComponent instanceof Frame) {
            dialog = new JDialog((Frame)windowForComponent, s, true);
        }
        else {
            dialog = new JDialog((Dialog)windowForComponent, s, true);
        }
        if (windowForComponent instanceof SwingUtilities.SharedOwnerFrame) {
            dialog.addWindowListener(SwingUtilities.getSharedOwnerFrameShutdownListener());
        }
        this.initDialog(dialog, n, component);
        return dialog;
    }
    
    private void initDialog(final JDialog dialog, final int windowDecorationStyle, final Component locationRelativeTo) {
        dialog.setComponentOrientation(this.getComponentOrientation());
        final Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(this, "Center");
        dialog.setResizable(false);
        if (JDialog.isDefaultLookAndFeelDecorated() && UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            dialog.setUndecorated(true);
            this.getRootPane().setWindowDecorationStyle(windowDecorationStyle);
        }
        dialog.pack();
        dialog.setLocationRelativeTo(locationRelativeTo);
        final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                if (dialog.isVisible() && propertyChangeEvent.getSource() == JOptionPane.this && propertyChangeEvent.getPropertyName().equals("value") && propertyChangeEvent.getNewValue() != null && propertyChangeEvent.getNewValue() != JOptionPane.UNINITIALIZED_VALUE) {
                    dialog.setVisible(false);
                }
            }
        };
        final WindowAdapter windowAdapter = new WindowAdapter() {
            private boolean gotFocus = false;
            
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                JOptionPane.this.setValue(null);
            }
            
            @Override
            public void windowClosed(final WindowEvent windowEvent) {
                JOptionPane.this.removePropertyChangeListener(propertyChangeListener);
                dialog.getContentPane().removeAll();
            }
            
            @Override
            public void windowGainedFocus(final WindowEvent windowEvent) {
                if (!this.gotFocus) {
                    JOptionPane.this.selectInitialValue();
                    this.gotFocus = true;
                }
            }
        };
        dialog.addWindowListener(windowAdapter);
        dialog.addWindowFocusListener(windowAdapter);
        dialog.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(final ComponentEvent componentEvent) {
                JOptionPane.this.setValue(JOptionPane.UNINITIALIZED_VALUE);
            }
        });
        this.addPropertyChangeListener(propertyChangeListener);
    }
    
    public static void showInternalMessageDialog(final Component component, final Object o) {
        showInternalMessageDialog(component, o, UIManager.getString("OptionPane.messageDialogTitle", component), 1);
    }
    
    public static void showInternalMessageDialog(final Component component, final Object o, final String s, final int n) {
        showInternalMessageDialog(component, o, s, n, null);
    }
    
    public static void showInternalMessageDialog(final Component component, final Object o, final String s, final int n, final Icon icon) {
        showInternalOptionDialog(component, o, s, -1, n, icon, null, null);
    }
    
    public static int showInternalConfirmDialog(final Component component, final Object o) {
        return showInternalConfirmDialog(component, o, UIManager.getString("OptionPane.titleText"), 1);
    }
    
    public static int showInternalConfirmDialog(final Component component, final Object o, final String s, final int n) {
        return showInternalConfirmDialog(component, o, s, n, 3);
    }
    
    public static int showInternalConfirmDialog(final Component component, final Object o, final String s, final int n, final int n2) {
        return showInternalConfirmDialog(component, o, s, n, n2, null);
    }
    
    public static int showInternalConfirmDialog(final Component component, final Object o, final String s, final int n, final int n2, final Icon icon) {
        return showInternalOptionDialog(component, o, s, n, n2, icon, null, null);
    }
    
    public static int showInternalOptionDialog(final Component component, final Object o, final String s, final int n, final int n2, final Icon icon, final Object[] array, final Object initialValue) {
        final JOptionPane optionPane = new JOptionPane(o, n2, n, icon, array, initialValue);
        optionPane.putClientProperty(ClientPropertyKey.PopupFactory_FORCE_HEAVYWEIGHT_POPUP, Boolean.TRUE);
        final Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        optionPane.setInitialValue(initialValue);
        final JInternalFrame internalFrame = optionPane.createInternalFrame(component, s);
        optionPane.selectInitialValue();
        internalFrame.setVisible(true);
        if (internalFrame.isVisible() && !internalFrame.isShowing()) {
            for (Container container = internalFrame.getParent(); container != null; container = container.getParent()) {
                if (!container.isVisible()) {
                    container.setVisible(true);
                }
            }
        }
        try {
            final Method method = AccessController.doPrivileged((PrivilegedAction<Method>)new ModalPrivilegedAction(Container.class, "startLWModal"));
            if (method != null) {
                method.invoke(internalFrame, (Object[])null);
            }
        }
        catch (final IllegalAccessException ex) {}
        catch (final IllegalArgumentException ex2) {}
        catch (final InvocationTargetException ex3) {}
        if (component instanceof JInternalFrame) {
            try {
                ((JInternalFrame)component).setSelected(true);
            }
            catch (final PropertyVetoException ex4) {}
        }
        final Object value = optionPane.getValue();
        if (focusOwner != null && focusOwner.isShowing()) {
            focusOwner.requestFocus();
        }
        if (value == null) {
            return -1;
        }
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i].equals(value)) {
                    return i;
                }
            }
            return -1;
        }
        if (value instanceof Integer) {
            return (int)value;
        }
        return -1;
    }
    
    public static String showInternalInputDialog(final Component component, final Object o) {
        return showInternalInputDialog(component, o, UIManager.getString("OptionPane.inputDialogTitle", component), 3);
    }
    
    public static String showInternalInputDialog(final Component component, final Object o, final String s, final int n) {
        return (String)showInternalInputDialog(component, o, s, n, null, null, null);
    }
    
    public static Object showInternalInputDialog(final Component component, final Object o, final String s, final int n, final Icon icon, final Object[] selectionValues, final Object initialSelectionValue) {
        final JOptionPane optionPane = new JOptionPane(o, n, 2, icon, null, null);
        optionPane.putClientProperty(ClientPropertyKey.PopupFactory_FORCE_HEAVYWEIGHT_POPUP, Boolean.TRUE);
        final Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        optionPane.setWantsInput(true);
        optionPane.setSelectionValues(selectionValues);
        optionPane.setInitialSelectionValue(initialSelectionValue);
        final JInternalFrame internalFrame = optionPane.createInternalFrame(component, s);
        optionPane.selectInitialValue();
        internalFrame.setVisible(true);
        if (internalFrame.isVisible() && !internalFrame.isShowing()) {
            for (Container container = internalFrame.getParent(); container != null; container = container.getParent()) {
                if (!container.isVisible()) {
                    container.setVisible(true);
                }
            }
        }
        try {
            final Method method = AccessController.doPrivileged((PrivilegedAction<Method>)new ModalPrivilegedAction(Container.class, "startLWModal"));
            if (method != null) {
                method.invoke(internalFrame, (Object[])null);
            }
        }
        catch (final IllegalAccessException ex) {}
        catch (final IllegalArgumentException ex2) {}
        catch (final InvocationTargetException ex3) {}
        if (component instanceof JInternalFrame) {
            try {
                ((JInternalFrame)component).setSelected(true);
            }
            catch (final PropertyVetoException ex4) {}
        }
        if (focusOwner != null && focusOwner.isShowing()) {
            focusOwner.requestFocus();
        }
        final Object inputValue = optionPane.getInputValue();
        if (inputValue == JOptionPane.UNINITIALIZED_VALUE) {
            return null;
        }
        return inputValue;
    }
    
    public JInternalFrame createInternalFrame(final Component component, final String s) {
        Container container = getDesktopPaneForComponent(component);
        if (container == null && (component == null || (container = component.getParent()) == null)) {
            throw new RuntimeException("JOptionPane: parentComponent does not have a valid parent");
        }
        final JInternalFrame internalFrame = new JInternalFrame(s, false, true, false, false);
        internalFrame.putClientProperty("JInternalFrame.frameType", "optionDialog");
        internalFrame.putClientProperty("JInternalFrame.messageType", this.getMessageType());
        internalFrame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(final InternalFrameEvent internalFrameEvent) {
                if (JOptionPane.this.getValue() == JOptionPane.UNINITIALIZED_VALUE) {
                    JOptionPane.this.setValue(null);
                }
            }
        });
        this.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                if (internalFrame.isVisible() && propertyChangeEvent.getSource() == JOptionPane.this && propertyChangeEvent.getPropertyName().equals("value")) {
                    try {
                        final Method method = AccessController.doPrivileged((PrivilegedAction<Method>)new ModalPrivilegedAction(Container.class, "stopLWModal"));
                        if (method != null) {
                            method.invoke(internalFrame, (Object[])null);
                        }
                    }
                    catch (final IllegalAccessException ex) {}
                    catch (final IllegalArgumentException ex2) {}
                    catch (final InvocationTargetException ex3) {}
                    try {
                        internalFrame.setClosed(true);
                    }
                    catch (final PropertyVetoException ex4) {}
                    internalFrame.setVisible(false);
                }
            }
        });
        internalFrame.getContentPane().add(this, "Center");
        if (container instanceof JDesktopPane) {
            container.add(internalFrame, JLayeredPane.MODAL_LAYER);
        }
        else {
            container.add(internalFrame, "Center");
        }
        final Dimension preferredSize = internalFrame.getPreferredSize();
        final Dimension size = container.getSize();
        final Dimension size2 = component.getSize();
        internalFrame.setBounds((size.width - preferredSize.width) / 2, (size.height - preferredSize.height) / 2, preferredSize.width, preferredSize.height);
        final Point convertPoint = SwingUtilities.convertPoint(component, 0, 0, container);
        final int n = (size2.width - preferredSize.width) / 2 + convertPoint.x;
        final int n2 = (size2.height - preferredSize.height) / 2 + convertPoint.y;
        final int n3 = n + preferredSize.width - size.width;
        final int n4 = n2 + preferredSize.height - size.height;
        internalFrame.setBounds(Math.max((n3 > 0) ? (n - n3) : n, 0), Math.max((n4 > 0) ? (n2 - n4) : n2, 0), preferredSize.width, preferredSize.height);
        container.validate();
        try {
            internalFrame.setSelected(true);
        }
        catch (final PropertyVetoException ex) {}
        return internalFrame;
    }
    
    public static Frame getFrameForComponent(final Component component) throws HeadlessException {
        if (component == null) {
            return getRootFrame();
        }
        if (component instanceof Frame) {
            return (Frame)component;
        }
        return getFrameForComponent(component.getParent());
    }
    
    static Window getWindowForComponent(final Component component) throws HeadlessException {
        if (component == null) {
            return getRootFrame();
        }
        if (component instanceof Frame || component instanceof Dialog) {
            return (Window)component;
        }
        return getWindowForComponent(component.getParent());
    }
    
    public static JDesktopPane getDesktopPaneForComponent(final Component component) {
        if (component == null) {
            return null;
        }
        if (component instanceof JDesktopPane) {
            return (JDesktopPane)component;
        }
        return getDesktopPaneForComponent(component.getParent());
    }
    
    public static void setRootFrame(final Frame frame) {
        if (frame != null) {
            SwingUtilities.appContextPut(JOptionPane.sharedFrameKey, frame);
        }
        else {
            SwingUtilities.appContextRemove(JOptionPane.sharedFrameKey);
        }
    }
    
    public static Frame getRootFrame() throws HeadlessException {
        Frame sharedOwnerFrame = (Frame)SwingUtilities.appContextGet(JOptionPane.sharedFrameKey);
        if (sharedOwnerFrame == null) {
            sharedOwnerFrame = SwingUtilities.getSharedOwnerFrame();
            SwingUtilities.appContextPut(JOptionPane.sharedFrameKey, sharedOwnerFrame);
        }
        return sharedOwnerFrame;
    }
    
    public JOptionPane() {
        this("JOptionPane message");
    }
    
    public JOptionPane(final Object o) {
        this(o, -1);
    }
    
    public JOptionPane(final Object o, final int n) {
        this(o, n, -1);
    }
    
    public JOptionPane(final Object o, final int n, final int n2) {
        this(o, n, n2, null);
    }
    
    public JOptionPane(final Object o, final int n, final int n2, final Icon icon) {
        this(o, n, n2, icon, null);
    }
    
    public JOptionPane(final Object o, final int n, final int n2, final Icon icon, final Object[] array) {
        this(o, n, n2, icon, array, null);
    }
    
    public JOptionPane(final Object message, final int messageType, final int optionType, final Icon icon, final Object[] options, final Object initialValue) {
        this.message = message;
        this.options = options;
        this.initialValue = initialValue;
        this.icon = icon;
        this.setMessageType(messageType);
        this.setOptionType(optionType);
        this.value = JOptionPane.UNINITIALIZED_VALUE;
        this.inputValue = JOptionPane.UNINITIALIZED_VALUE;
        this.updateUI();
    }
    
    public void setUI(final OptionPaneUI ui) {
        if (this.ui != ui) {
            super.setUI(ui);
            this.invalidate();
        }
    }
    
    public OptionPaneUI getUI() {
        return (OptionPaneUI)this.ui;
    }
    
    @Override
    public void updateUI() {
        this.setUI((OptionPaneUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "OptionPaneUI";
    }
    
    public void setMessage(final Object message) {
        this.firePropertyChange("message", this.message, this.message = message);
    }
    
    public Object getMessage() {
        return this.message;
    }
    
    public void setIcon(final Icon icon) {
        this.firePropertyChange("icon", this.icon, this.icon = icon);
    }
    
    public Icon getIcon() {
        return this.icon;
    }
    
    public void setValue(final Object value) {
        this.firePropertyChange("value", this.value, this.value = value);
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public void setOptions(final Object[] options) {
        this.firePropertyChange("options", this.options, this.options = options);
    }
    
    public Object[] getOptions() {
        if (this.options != null) {
            final int length = this.options.length;
            final Object[] array = new Object[length];
            System.arraycopy(this.options, 0, array, 0, length);
            return array;
        }
        return this.options;
    }
    
    public void setInitialValue(final Object initialValue) {
        this.firePropertyChange("initialValue", this.initialValue, this.initialValue = initialValue);
    }
    
    public Object getInitialValue() {
        return this.initialValue;
    }
    
    public void setMessageType(final int messageType) {
        if (messageType != 0 && messageType != 1 && messageType != 2 && messageType != 3 && messageType != -1) {
            throw new RuntimeException("JOptionPane: type must be one of JOptionPane.ERROR_MESSAGE, JOptionPane.INFORMATION_MESSAGE, JOptionPane.WARNING_MESSAGE, JOptionPane.QUESTION_MESSAGE or JOptionPane.PLAIN_MESSAGE");
        }
        this.firePropertyChange("messageType", this.messageType, this.messageType = messageType);
    }
    
    public int getMessageType() {
        return this.messageType;
    }
    
    public void setOptionType(final int optionType) {
        if (optionType != -1 && optionType != 0 && optionType != 1 && optionType != 2) {
            throw new RuntimeException("JOptionPane: option type must be one of JOptionPane.DEFAULT_OPTION, JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_CANCEL_OPTION or JOptionPane.OK_CANCEL_OPTION");
        }
        this.firePropertyChange("optionType", this.optionType, this.optionType = optionType);
    }
    
    public int getOptionType() {
        return this.optionType;
    }
    
    public void setSelectionValues(final Object[] selectionValues) {
        this.firePropertyChange("selectionValues", this.selectionValues, this.selectionValues = selectionValues);
        if (this.selectionValues != null) {
            this.setWantsInput(true);
        }
    }
    
    public Object[] getSelectionValues() {
        return this.selectionValues;
    }
    
    public void setInitialSelectionValue(final Object initialSelectionValue) {
        this.firePropertyChange("initialSelectionValue", this.initialSelectionValue, this.initialSelectionValue = initialSelectionValue);
    }
    
    public Object getInitialSelectionValue() {
        return this.initialSelectionValue;
    }
    
    public void setInputValue(final Object inputValue) {
        this.firePropertyChange("inputValue", this.inputValue, this.inputValue = inputValue);
    }
    
    public Object getInputValue() {
        return this.inputValue;
    }
    
    public int getMaxCharactersPerLineCount() {
        return Integer.MAX_VALUE;
    }
    
    public void setWantsInput(final boolean wantsInput) {
        this.firePropertyChange("wantsInput", this.wantsInput, this.wantsInput = wantsInput);
    }
    
    public boolean getWantsInput() {
        return this.wantsInput;
    }
    
    public void selectInitialValue() {
        final OptionPaneUI ui = this.getUI();
        if (ui != null) {
            ui.selectInitialValue(this);
        }
    }
    
    private static int styleFromMessageType(final int n) {
        switch (n) {
            case 0: {
                return 4;
            }
            case 3: {
                return 7;
            }
            case 2: {
                return 8;
            }
            case 1: {
                return 3;
            }
            default: {
                return 2;
            }
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final Vector vector = new Vector();
        objectOutputStream.defaultWriteObject();
        if (this.icon != null && this.icon instanceof Serializable) {
            vector.addElement("icon");
            vector.addElement(this.icon);
        }
        if (this.message != null && this.message instanceof Serializable) {
            vector.addElement("message");
            vector.addElement(this.message);
        }
        if (this.options != null) {
            final Vector vector2 = new Vector();
            for (int i = 0; i < this.options.length; ++i) {
                if (this.options[i] instanceof Serializable) {
                    vector2.addElement(this.options[i]);
                }
            }
            if (vector2.size() > 0) {
                final Object[] array = new Object[vector2.size()];
                vector2.copyInto(array);
                vector.addElement("options");
                vector.addElement(array);
            }
        }
        if (this.initialValue != null && this.initialValue instanceof Serializable) {
            vector.addElement("initialValue");
            vector.addElement(this.initialValue);
        }
        if (this.value != null && this.value instanceof Serializable) {
            vector.addElement("value");
            vector.addElement(this.value);
        }
        if (this.selectionValues != null) {
            boolean b = true;
            for (int j = 0; j < this.selectionValues.length; ++j) {
                if (this.selectionValues[j] != null && !(this.selectionValues[j] instanceof Serializable)) {
                    b = false;
                    break;
                }
            }
            if (b) {
                vector.addElement("selectionValues");
                vector.addElement(this.selectionValues);
            }
        }
        if (this.inputValue != null && this.inputValue instanceof Serializable) {
            vector.addElement("inputValue");
            vector.addElement(this.inputValue);
        }
        if (this.initialSelectionValue != null && this.initialSelectionValue instanceof Serializable) {
            vector.addElement("initialSelectionValue");
            vector.addElement(this.initialSelectionValue);
        }
        objectOutputStream.writeObject(vector);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final Vector vector = (Vector)objectInputStream.readObject();
        int n = 0;
        final int size = vector.size();
        if (n < size && vector.elementAt(n).equals("icon")) {
            this.icon = (Icon)vector.elementAt(++n);
            ++n;
        }
        if (n < size && vector.elementAt(n).equals("message")) {
            this.message = vector.elementAt(++n);
            ++n;
        }
        if (n < size && vector.elementAt(n).equals("options")) {
            this.options = (Object[])vector.elementAt(++n);
            ++n;
        }
        if (n < size && vector.elementAt(n).equals("initialValue")) {
            this.initialValue = vector.elementAt(++n);
            ++n;
        }
        if (n < size && vector.elementAt(n).equals("value")) {
            this.value = vector.elementAt(++n);
            ++n;
        }
        if (n < size && vector.elementAt(n).equals("selectionValues")) {
            this.selectionValues = (Object[])vector.elementAt(++n);
            ++n;
        }
        if (n < size && vector.elementAt(n).equals("inputValue")) {
            this.inputValue = vector.elementAt(++n);
            ++n;
        }
        if (n < size && vector.elementAt(n).equals("initialSelectionValue")) {
            this.initialSelectionValue = vector.elementAt(++n);
            ++n;
        }
        if (this.getUIClassID().equals("OptionPaneUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        final String s = (this.icon != null) ? this.icon.toString() : "";
        final String s2 = (this.initialValue != null) ? this.initialValue.toString() : "";
        final String s3 = (this.message != null) ? this.message.toString() : "";
        String s4;
        if (this.messageType == 0) {
            s4 = "ERROR_MESSAGE";
        }
        else if (this.messageType == 1) {
            s4 = "INFORMATION_MESSAGE";
        }
        else if (this.messageType == 2) {
            s4 = "WARNING_MESSAGE";
        }
        else if (this.messageType == 3) {
            s4 = "QUESTION_MESSAGE";
        }
        else if (this.messageType == -1) {
            s4 = "PLAIN_MESSAGE";
        }
        else {
            s4 = "";
        }
        String s5;
        if (this.optionType == -1) {
            s5 = "DEFAULT_OPTION";
        }
        else if (this.optionType == 0) {
            s5 = "YES_NO_OPTION";
        }
        else if (this.optionType == 1) {
            s5 = "YES_NO_CANCEL_OPTION";
        }
        else if (this.optionType == 2) {
            s5 = "OK_CANCEL_OPTION";
        }
        else {
            s5 = "";
        }
        return super.paramString() + ",icon=" + s + ",initialValue=" + s2 + ",message=" + s3 + ",messageType=" + s4 + ",optionType=" + s5 + ",wantsInput=" + (this.wantsInput ? "true" : "false");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJOptionPane();
        }
        return this.accessibleContext;
    }
    
    static {
        UNINITIALIZED_VALUE = "uninitializedValue";
        sharedFrameKey = JOptionPane.class;
    }
    
    private static class ModalPrivilegedAction implements PrivilegedAction<Method>
    {
        private Class<?> clazz;
        private String methodName;
        
        public ModalPrivilegedAction(final Class<?> clazz, final String methodName) {
            this.clazz = clazz;
            this.methodName = methodName;
        }
        
        @Override
        public Method run() {
            AccessibleObject declaredMethod = null;
            try {
                declaredMethod = this.clazz.getDeclaredMethod(this.methodName, (Class<?>[])null);
            }
            catch (final NoSuchMethodException ex) {}
            if (declaredMethod != null) {
                declaredMethod.setAccessible(true);
            }
            return (Method)declaredMethod;
        }
    }
    
    protected class AccessibleJOptionPane extends AccessibleJComponent
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            switch (JOptionPane.this.messageType) {
                case 0:
                case 1:
                case 2: {
                    return AccessibleRole.ALERT;
                }
                default: {
                    return AccessibleRole.OPTION_PANE;
                }
            }
        }
    }
}
