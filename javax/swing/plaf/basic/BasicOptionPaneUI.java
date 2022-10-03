package javax.swing.plaf.basic;

import sun.swing.UIAction;
import java.awt.event.KeyEvent;
import java.awt.ComponentOrientation;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.awt.Color;
import javax.swing.JTextField;
import java.util.Locale;
import javax.swing.JRootPane;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import javax.swing.JButton;
import javax.accessibility.Accessible;
import java.awt.event.ActionListener;
import javax.swing.KeyStroke;
import java.awt.event.MouseListener;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.Box;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import javax.swing.border.Border;
import javax.swing.JPanel;
import sun.swing.DefaultLookup;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import javax.swing.BoxLayout;
import java.awt.Container;
import javax.swing.UIManager;
import javax.swing.LookAndFeel;
import java.awt.LayoutManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.ActionMap;
import javax.swing.Action;
import java.beans.PropertyChangeListener;
import java.awt.Component;
import javax.swing.JComponent;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.plaf.OptionPaneUI;

public class BasicOptionPaneUI extends OptionPaneUI
{
    public static final int MinimumWidth = 262;
    public static final int MinimumHeight = 90;
    private static String newline;
    protected JOptionPane optionPane;
    protected Dimension minimumSize;
    protected JComponent inputComponent;
    protected Component initialFocusComponent;
    protected boolean hasCustomComponents;
    protected PropertyChangeListener propertyChangeListener;
    private Handler handler;
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("close"));
        BasicLookAndFeel.installAudioActionMap(lazyActionMap);
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicOptionPaneUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.optionPane = (JOptionPane)component;
        this.installDefaults();
        this.optionPane.setLayout(this.createLayoutManager());
        this.installComponents();
        this.installListeners();
        this.installKeyboardActions();
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallComponents();
        this.optionPane.setLayout(null);
        this.uninstallKeyboardActions();
        this.uninstallListeners();
        this.uninstallDefaults();
        this.optionPane = null;
    }
    
    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(this.optionPane, "OptionPane.background", "OptionPane.foreground", "OptionPane.font");
        LookAndFeel.installBorder(this.optionPane, "OptionPane.border");
        this.minimumSize = UIManager.getDimension("OptionPane.minimumSize");
        LookAndFeel.installProperty(this.optionPane, "opaque", Boolean.TRUE);
    }
    
    protected void uninstallDefaults() {
        LookAndFeel.uninstallBorder(this.optionPane);
    }
    
    protected void installComponents() {
        this.optionPane.add(this.createMessageArea());
        final Container separator = this.createSeparator();
        if (separator != null) {
            this.optionPane.add(separator);
        }
        this.optionPane.add(this.createButtonArea());
        this.optionPane.applyComponentOrientation(this.optionPane.getComponentOrientation());
    }
    
    protected void uninstallComponents() {
        this.hasCustomComponents = false;
        this.inputComponent = null;
        this.initialFocusComponent = null;
        this.optionPane.removeAll();
    }
    
    protected LayoutManager createLayoutManager() {
        return new BoxLayout(this.optionPane, 1);
    }
    
    protected void installListeners() {
        final PropertyChangeListener propertyChangeListener = this.createPropertyChangeListener();
        this.propertyChangeListener = propertyChangeListener;
        if (propertyChangeListener != null) {
            this.optionPane.addPropertyChangeListener(this.propertyChangeListener);
        }
    }
    
    protected void uninstallListeners() {
        if (this.propertyChangeListener != null) {
            this.optionPane.removePropertyChangeListener(this.propertyChangeListener);
            this.propertyChangeListener = null;
        }
        this.handler = null;
    }
    
    protected PropertyChangeListener createPropertyChangeListener() {
        return this.getHandler();
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    protected void installKeyboardActions() {
        SwingUtilities.replaceUIInputMap(this.optionPane, 2, this.getInputMap(2));
        LazyActionMap.installLazyActionMap(this.optionPane, BasicOptionPaneUI.class, "OptionPane.actionMap");
    }
    
    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIInputMap(this.optionPane, 2, null);
        SwingUtilities.replaceUIActionMap(this.optionPane, null);
    }
    
    InputMap getInputMap(final int n) {
        if (n == 2) {
            final Object[] array = (Object[])DefaultLookup.get(this.optionPane, this, "OptionPane.windowBindings");
            if (array != null) {
                return LookAndFeel.makeComponentInputMap(this.optionPane, array);
            }
        }
        return null;
    }
    
    public Dimension getMinimumOptionPaneSize() {
        if (this.minimumSize == null) {
            return new Dimension(262, 90);
        }
        return new Dimension(this.minimumSize.width, this.minimumSize.height);
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        if (component != this.optionPane) {
            return null;
        }
        final Dimension minimumOptionPaneSize = this.getMinimumOptionPaneSize();
        final LayoutManager layout = component.getLayout();
        if (layout == null) {
            return minimumOptionPaneSize;
        }
        final Dimension preferredLayoutSize = layout.preferredLayoutSize(component);
        if (minimumOptionPaneSize != null) {
            return new Dimension(Math.max(preferredLayoutSize.width, minimumOptionPaneSize.width), Math.max(preferredLayoutSize.height, minimumOptionPaneSize.height));
        }
        return preferredLayoutSize;
    }
    
    protected Container createMessageArea() {
        final JPanel panel = new JPanel();
        final Border border = (Border)DefaultLookup.get(this.optionPane, this, "OptionPane.messageAreaBorder");
        if (border != null) {
            panel.setBorder(border);
        }
        panel.setLayout(new BorderLayout());
        final JPanel panel2 = new JPanel(new GridBagLayout());
        final JPanel panel3 = new JPanel(new BorderLayout());
        panel2.setName("OptionPane.body");
        panel3.setName("OptionPane.realBody");
        if (this.getIcon() != null) {
            final JPanel panel4 = new JPanel();
            panel4.setName("OptionPane.separator");
            panel4.setPreferredSize(new Dimension(15, 1));
            panel3.add(panel4, "Before");
        }
        panel3.add(panel2, "Center");
        final GridBagConstraints gridBagConstraints3;
        final GridBagConstraints gridBagConstraints2;
        final GridBagConstraints gridBagConstraints = gridBagConstraints2 = (gridBagConstraints3 = new GridBagConstraints());
        final int n = 0;
        gridBagConstraints2.gridy = n;
        gridBagConstraints3.gridx = n;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.anchor = DefaultLookup.getInt(this.optionPane, this, "OptionPane.messageAnchor", 10);
        gridBagConstraints.insets = new Insets(0, 0, 3, 0);
        this.addMessageComponents(panel2, gridBagConstraints, this.getMessage(), this.getMaxCharactersPerLineCount(), false);
        panel.add(panel3, "Center");
        this.addIcon(panel);
        return panel;
    }
    
    protected void addMessageComponents(final Container container, final GridBagConstraints gridBagConstraints, final Object o, final int n, final boolean b) {
        if (o == null) {
            return;
        }
        if (o instanceof Component) {
            if (o instanceof JScrollPane || o instanceof JPanel) {
                gridBagConstraints.fill = 1;
                gridBagConstraints.weighty = 1.0;
            }
            else {
                gridBagConstraints.fill = 2;
            }
            gridBagConstraints.weightx = 1.0;
            container.add((Component)o, gridBagConstraints);
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.weighty = 0.0;
            gridBagConstraints.fill = 0;
            ++gridBagConstraints.gridy;
            if (!b) {
                this.hasCustomComponents = true;
            }
        }
        else if (o instanceof Object[]) {
            final Object[] array = (Object[])o;
            for (int length = array.length, i = 0; i < length; ++i) {
                this.addMessageComponents(container, gridBagConstraints, array[i], n, false);
            }
        }
        else if (o instanceof Icon) {
            final JLabel label = new JLabel((Icon)o, 0);
            this.configureMessageLabel(label);
            this.addMessageComponents(container, gridBagConstraints, label, n, true);
        }
        else {
            final String string = o.toString();
            final int length2 = string.length();
            if (length2 <= 0) {
                return;
            }
            int length3 = 0;
            int n2;
            if ((n2 = string.indexOf(BasicOptionPaneUI.newline)) >= 0) {
                length3 = BasicOptionPaneUI.newline.length();
            }
            else if ((n2 = string.indexOf("\r\n")) >= 0) {
                length3 = 2;
            }
            else if ((n2 = string.indexOf(10)) >= 0) {
                length3 = 1;
            }
            if (n2 >= 0) {
                if (n2 == 0) {
                    final JPanel panel = new JPanel() {
                        @Override
                        public Dimension getPreferredSize() {
                            final Font font = this.getFont();
                            if (font != null) {
                                return new Dimension(1, font.getSize() + 2);
                            }
                            return new Dimension(0, 0);
                        }
                    };
                    panel.setName("OptionPane.break");
                    this.addMessageComponents(container, gridBagConstraints, panel, n, true);
                }
                else {
                    this.addMessageComponents(container, gridBagConstraints, string.substring(0, n2), n, false);
                }
                this.addMessageComponents(container, gridBagConstraints, string.substring(n2 + length3), n, false);
            }
            else if (length2 > n) {
                final Box verticalBox = Box.createVerticalBox();
                verticalBox.setName("OptionPane.verticalBox");
                this.burstStringInto(verticalBox, string, n);
                this.addMessageComponents(container, gridBagConstraints, verticalBox, n, true);
            }
            else {
                final JLabel label2 = new JLabel(string, 10);
                label2.setName("OptionPane.label");
                this.configureMessageLabel(label2);
                this.addMessageComponents(container, gridBagConstraints, label2, n, true);
            }
        }
    }
    
    protected Object getMessage() {
        this.inputComponent = null;
        if (this.optionPane == null) {
            return null;
        }
        if (this.optionPane.getWantsInput()) {
            final Object message = this.optionPane.getMessage();
            final Object[] selectionValues = this.optionPane.getSelectionValues();
            final Object initialSelectionValue = this.optionPane.getInitialSelectionValue();
            Accessible accessible;
            if (selectionValues != null) {
                if (selectionValues.length < 20) {
                    final JComboBox inputComponent = new JComboBox();
                    inputComponent.setName("OptionPane.comboBox");
                    for (int i = 0; i < selectionValues.length; ++i) {
                        inputComponent.addItem(selectionValues[i]);
                    }
                    if (initialSelectionValue != null) {
                        inputComponent.setSelectedItem(initialSelectionValue);
                    }
                    this.inputComponent = inputComponent;
                    accessible = inputComponent;
                }
                else {
                    final JList inputComponent2 = new JList(selectionValues);
                    final JScrollPane scrollPane = new JScrollPane(inputComponent2);
                    scrollPane.setName("OptionPane.scrollPane");
                    inputComponent2.setName("OptionPane.list");
                    inputComponent2.setVisibleRowCount(10);
                    inputComponent2.setSelectionMode(0);
                    if (initialSelectionValue != null) {
                        inputComponent2.setSelectedValue(initialSelectionValue, true);
                    }
                    inputComponent2.addMouseListener(this.getHandler());
                    accessible = scrollPane;
                    this.inputComponent = inputComponent2;
                }
            }
            else {
                final MultiplexingTextField multiplexingTextField = new MultiplexingTextField(20);
                multiplexingTextField.setName("OptionPane.textField");
                multiplexingTextField.setKeyStrokes(new KeyStroke[] { KeyStroke.getKeyStroke("ENTER") });
                if (initialSelectionValue != null) {
                    final String string = initialSelectionValue.toString();
                    multiplexingTextField.setText(string);
                    multiplexingTextField.setSelectionStart(0);
                    multiplexingTextField.setSelectionEnd(string.length());
                }
                multiplexingTextField.addActionListener(this.getHandler());
                final MultiplexingTextField inputComponent3 = multiplexingTextField;
                this.inputComponent = inputComponent3;
                accessible = inputComponent3;
            }
            Object[] array;
            if (message == null) {
                array = new Object[] { accessible };
            }
            else {
                array = new Object[] { message, accessible };
            }
            return array;
        }
        return this.optionPane.getMessage();
    }
    
    protected void addIcon(final Container container) {
        final Icon icon = this.getIcon();
        if (icon != null) {
            final JLabel label = new JLabel(icon);
            label.setName("OptionPane.iconLabel");
            label.setVerticalAlignment(1);
            container.add(label, "Before");
        }
    }
    
    protected Icon getIcon() {
        Icon iconForType = (this.optionPane == null) ? null : this.optionPane.getIcon();
        if (iconForType == null && this.optionPane != null) {
            iconForType = this.getIconForType(this.optionPane.getMessageType());
        }
        return iconForType;
    }
    
    protected Icon getIconForType(final int n) {
        if (n < 0 || n > 3) {
            return null;
        }
        String s = null;
        switch (n) {
            case 0: {
                s = "OptionPane.errorIcon";
                break;
            }
            case 1: {
                s = "OptionPane.informationIcon";
                break;
            }
            case 2: {
                s = "OptionPane.warningIcon";
                break;
            }
            case 3: {
                s = "OptionPane.questionIcon";
                break;
            }
        }
        if (s != null) {
            return (Icon)DefaultLookup.get(this.optionPane, this, s);
        }
        return null;
    }
    
    protected int getMaxCharactersPerLineCount() {
        return this.optionPane.getMaxCharactersPerLineCount();
    }
    
    protected void burstStringInto(final Container container, final String s, final int n) {
        final int length = s.length();
        if (length <= 0) {
            return;
        }
        if (length > n) {
            int n2 = s.lastIndexOf(32, n);
            if (n2 <= 0) {
                n2 = s.indexOf(32, n);
            }
            if (n2 > 0 && n2 < length) {
                this.burstStringInto(container, s.substring(0, n2), n);
                this.burstStringInto(container, s.substring(n2 + 1), n);
                return;
            }
        }
        final JLabel label = new JLabel(s, 2);
        label.setName("OptionPane.label");
        this.configureMessageLabel(label);
        container.add(label);
    }
    
    protected Container createSeparator() {
        return null;
    }
    
    protected Container createButtonArea() {
        final JPanel panel = new JPanel();
        final Border border = (Border)DefaultLookup.get(this.optionPane, this, "OptionPane.buttonAreaBorder");
        panel.setName("OptionPane.buttonArea");
        if (border != null) {
            panel.setBorder(border);
        }
        panel.setLayout(new ButtonAreaLayout(DefaultLookup.getBoolean(this.optionPane, this, "OptionPane.sameSizeButtons", true), DefaultLookup.getInt(this.optionPane, this, "OptionPane.buttonPadding", 6), DefaultLookup.getInt(this.optionPane, this, "OptionPane.buttonOrientation", 0), DefaultLookup.getBoolean(this.optionPane, this, "OptionPane.isYesLast", false)));
        this.addButtonComponents(panel, this.getButtons(), this.getInitialValueIndex());
        return panel;
    }
    
    protected void addButtonComponents(final Container container, final Object[] array, final int n) {
        if (array != null && array.length > 0) {
            final boolean sizeButtonsToSameWidth = this.getSizeButtonsToSameWidth();
            boolean b = true;
            final int length = array.length;
            JButton[] array2 = null;
            int max = 0;
            if (sizeButtonsToSameWidth) {
                array2 = new JButton[length];
            }
            for (int i = 0; i < length; ++i) {
                final Object o = array[i];
                Component initialFocusComponent;
                if (o instanceof Component) {
                    b = false;
                    initialFocusComponent = (Component)o;
                    container.add(initialFocusComponent);
                    this.hasCustomComponents = true;
                }
                else {
                    JButton button;
                    if (o instanceof ButtonFactory) {
                        button = ((ButtonFactory)o).createButton();
                    }
                    else if (o instanceof Icon) {
                        button = new JButton((Icon)o);
                    }
                    else {
                        button = new JButton(o.toString());
                    }
                    button.setName("OptionPane.button");
                    button.setMultiClickThreshhold(DefaultLookup.getInt(this.optionPane, this, "OptionPane.buttonClickThreshhold", 0));
                    this.configureButton(button);
                    container.add(button);
                    final ActionListener buttonActionListener = this.createButtonActionListener(i);
                    if (buttonActionListener != null) {
                        button.addActionListener(buttonActionListener);
                    }
                    initialFocusComponent = button;
                }
                if (sizeButtonsToSameWidth && b && initialFocusComponent instanceof JButton) {
                    array2[i] = (JButton)initialFocusComponent;
                    max = Math.max(max, initialFocusComponent.getMinimumSize().width);
                }
                if (i == n) {
                    this.initialFocusComponent = initialFocusComponent;
                    if (this.initialFocusComponent instanceof JButton) {
                        this.initialFocusComponent.addHierarchyListener(new HierarchyListener() {
                            @Override
                            public void hierarchyChanged(final HierarchyEvent hierarchyEvent) {
                                if ((hierarchyEvent.getChangeFlags() & 0x1L) != 0x0L) {
                                    final JButton defaultButton = (JButton)hierarchyEvent.getComponent();
                                    final JRootPane rootPane = SwingUtilities.getRootPane(defaultButton);
                                    if (rootPane != null) {
                                        rootPane.setDefaultButton(defaultButton);
                                    }
                                }
                            }
                        });
                    }
                }
            }
            ((ButtonAreaLayout)container.getLayout()).setSyncAllWidths(sizeButtonsToSameWidth && b);
            if (DefaultLookup.getBoolean(this.optionPane, this, "OptionPane.setButtonMargin", true) && sizeButtonsToSameWidth && b) {
                final int n2 = (length <= 2) ? 8 : 4;
                for (int j = 0; j < length; ++j) {
                    array2[j].setMargin(new Insets(2, n2, 2, n2));
                }
            }
        }
    }
    
    protected ActionListener createButtonActionListener(final int n) {
        return new ButtonActionListener(n);
    }
    
    protected Object[] getButtons() {
        if (this.optionPane == null) {
            return null;
        }
        final Object[] options = this.optionPane.getOptions();
        if (options == null) {
            final int optionType = this.optionPane.getOptionType();
            final Locale locale = this.optionPane.getLocale();
            final int int1 = DefaultLookup.getInt(this.optionPane, this, "OptionPane.buttonMinimumWidth", -1);
            ButtonFactory[] array;
            if (optionType == 0) {
                array = new ButtonFactory[] { new ButtonFactory(UIManager.getString("OptionPane.yesButtonText", locale), this.getMnemonic("OptionPane.yesButtonMnemonic", locale), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.yesIcon"), int1), new ButtonFactory(UIManager.getString("OptionPane.noButtonText", locale), this.getMnemonic("OptionPane.noButtonMnemonic", locale), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.noIcon"), int1) };
            }
            else if (optionType == 1) {
                array = new ButtonFactory[] { new ButtonFactory(UIManager.getString("OptionPane.yesButtonText", locale), this.getMnemonic("OptionPane.yesButtonMnemonic", locale), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.yesIcon"), int1), new ButtonFactory(UIManager.getString("OptionPane.noButtonText", locale), this.getMnemonic("OptionPane.noButtonMnemonic", locale), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.noIcon"), int1), new ButtonFactory(UIManager.getString("OptionPane.cancelButtonText", locale), this.getMnemonic("OptionPane.cancelButtonMnemonic", locale), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.cancelIcon"), int1) };
            }
            else if (optionType == 2) {
                array = new ButtonFactory[] { new ButtonFactory(UIManager.getString("OptionPane.okButtonText", locale), this.getMnemonic("OptionPane.okButtonMnemonic", locale), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.okIcon"), int1), new ButtonFactory(UIManager.getString("OptionPane.cancelButtonText", locale), this.getMnemonic("OptionPane.cancelButtonMnemonic", locale), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.cancelIcon"), int1) };
            }
            else {
                array = new ButtonFactory[] { new ButtonFactory(UIManager.getString("OptionPane.okButtonText", locale), this.getMnemonic("OptionPane.okButtonMnemonic", locale), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.okIcon"), int1) };
            }
            return array;
        }
        return options;
    }
    
    private int getMnemonic(final String s, final Locale locale) {
        final String s2 = (String)UIManager.get(s, locale);
        if (s2 == null) {
            return 0;
        }
        try {
            return Integer.parseInt(s2);
        }
        catch (final NumberFormatException ex) {
            return 0;
        }
    }
    
    protected boolean getSizeButtonsToSameWidth() {
        return true;
    }
    
    protected int getInitialValueIndex() {
        if (this.optionPane != null) {
            final Object initialValue = this.optionPane.getInitialValue();
            final Object[] options = this.optionPane.getOptions();
            if (options == null) {
                return 0;
            }
            if (initialValue != null) {
                for (int i = options.length - 1; i >= 0; --i) {
                    if (options[i].equals(initialValue)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    
    protected void resetInputValue() {
        if (this.inputComponent != null && this.inputComponent instanceof JTextField) {
            this.optionPane.setInputValue(((JTextField)this.inputComponent).getText());
        }
        else if (this.inputComponent != null && this.inputComponent instanceof JComboBox) {
            this.optionPane.setInputValue(((JComboBox)this.inputComponent).getSelectedItem());
        }
        else if (this.inputComponent != null) {
            this.optionPane.setInputValue(((JList)this.inputComponent).getSelectedValue());
        }
    }
    
    @Override
    public void selectInitialValue(final JOptionPane optionPane) {
        if (this.inputComponent != null) {
            this.inputComponent.requestFocus();
        }
        else {
            if (this.initialFocusComponent != null) {
                this.initialFocusComponent.requestFocus();
            }
            if (this.initialFocusComponent instanceof JButton) {
                final JRootPane rootPane = SwingUtilities.getRootPane(this.initialFocusComponent);
                if (rootPane != null) {
                    rootPane.setDefaultButton((JButton)this.initialFocusComponent);
                }
            }
        }
    }
    
    @Override
    public boolean containsCustomComponents(final JOptionPane optionPane) {
        return this.hasCustomComponents;
    }
    
    private void configureMessageLabel(final JLabel label) {
        final Color foreground = (Color)DefaultLookup.get(this.optionPane, this, "OptionPane.messageForeground");
        if (foreground != null) {
            label.setForeground(foreground);
        }
        final Font font = (Font)DefaultLookup.get(this.optionPane, this, "OptionPane.messageFont");
        if (font != null) {
            label.setFont(font);
        }
    }
    
    private void configureButton(final JButton button) {
        final Font font = (Font)DefaultLookup.get(this.optionPane, this, "OptionPane.buttonFont");
        if (font != null) {
            button.setFont(font);
        }
    }
    
    static {
        BasicOptionPaneUI.newline = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("line.separator"));
        if (BasicOptionPaneUI.newline == null) {
            BasicOptionPaneUI.newline = "\n";
        }
    }
    
    public static class ButtonAreaLayout implements LayoutManager
    {
        protected boolean syncAllWidths;
        protected int padding;
        protected boolean centersChildren;
        private int orientation;
        private boolean reverseButtons;
        private boolean useOrientation;
        
        public ButtonAreaLayout(final boolean syncAllWidths, final int padding) {
            this.syncAllWidths = syncAllWidths;
            this.padding = padding;
            this.centersChildren = true;
            this.useOrientation = false;
        }
        
        ButtonAreaLayout(final boolean b, final int n, final int orientation, final boolean reverseButtons) {
            this(b, n);
            this.useOrientation = true;
            this.orientation = orientation;
            this.reverseButtons = reverseButtons;
        }
        
        public void setSyncAllWidths(final boolean syncAllWidths) {
            this.syncAllWidths = syncAllWidths;
        }
        
        public boolean getSyncAllWidths() {
            return this.syncAllWidths;
        }
        
        public void setPadding(final int padding) {
            this.padding = padding;
        }
        
        public int getPadding() {
            return this.padding;
        }
        
        public void setCentersChildren(final boolean centersChildren) {
            this.centersChildren = centersChildren;
            this.useOrientation = false;
        }
        
        public boolean getCentersChildren() {
            return this.centersChildren;
        }
        
        private int getOrientation(final Container container) {
            if (!this.useOrientation) {
                return 0;
            }
            if (container.getComponentOrientation().isLeftToRight()) {
                return this.orientation;
            }
            switch (this.orientation) {
                case 2: {
                    return 4;
                }
                case 4: {
                    return 2;
                }
                case 0: {
                    return 0;
                }
                default: {
                    return 2;
                }
            }
        }
        
        @Override
        public void addLayoutComponent(final String s, final Component component) {
        }
        
        @Override
        public void layoutContainer(final Container container) {
            final Component[] components = container.getComponents();
            if (components != null && components.length > 0) {
                final int length = components.length;
                final Insets insets = container.getInsets();
                int max = 0;
                int max2 = 0;
                int n = 0;
                int n2 = 0;
                int n3 = 0;
                final boolean b = container.getComponentOrientation().isLeftToRight() ? this.reverseButtons : (!this.reverseButtons);
                for (int i = 0; i < length; ++i) {
                    final Dimension preferredSize = components[i].getPreferredSize();
                    max = Math.max(max, preferredSize.width);
                    max2 = Math.max(max2, preferredSize.height);
                    n += preferredSize.width;
                }
                if (this.getSyncAllWidths()) {
                    n = max * length;
                }
                final int n4 = n + (length - 1) * this.padding;
                switch (this.getOrientation(container)) {
                    case 2: {
                        n2 = insets.left;
                        break;
                    }
                    case 4: {
                        n2 = container.getWidth() - insets.right - n4;
                        break;
                    }
                    case 0: {
                        if (this.getCentersChildren() || length < 2) {
                            n2 = (container.getWidth() - n4) / 2;
                            break;
                        }
                        n2 = insets.left;
                        if (this.getSyncAllWidths()) {
                            n3 = (container.getWidth() - insets.left - insets.right - n4) / (length - 1) + max;
                            break;
                        }
                        n3 = (container.getWidth() - insets.left - insets.right - n4) / (length - 1);
                        break;
                    }
                }
                for (int j = 0; j < length; ++j) {
                    final int n5 = b ? (length - j - 1) : j;
                    final Dimension preferredSize2 = components[n5].getPreferredSize();
                    if (this.getSyncAllWidths()) {
                        components[n5].setBounds(n2, insets.top, max, max2);
                    }
                    else {
                        components[n5].setBounds(n2, insets.top, preferredSize2.width, preferredSize2.height);
                    }
                    if (n3 != 0) {
                        n2 += n3;
                    }
                    else {
                        n2 += components[n5].getWidth() + this.padding;
                    }
                }
            }
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            if (container != null) {
                final Component[] components = container.getComponents();
                if (components != null && components.length > 0) {
                    final int length = components.length;
                    int n = 0;
                    final Insets insets = container.getInsets();
                    final int n2 = insets.top + insets.bottom;
                    final int n3 = insets.left + insets.right;
                    if (this.syncAllWidths) {
                        int max = 0;
                        for (int i = 0; i < length; ++i) {
                            final Dimension preferredSize = components[i].getPreferredSize();
                            n = Math.max(n, preferredSize.height);
                            max = Math.max(max, preferredSize.width);
                        }
                        return new Dimension(n3 + max * length + (length - 1) * this.padding, n2 + n);
                    }
                    int n4 = 0;
                    for (int j = 0; j < length; ++j) {
                        final Dimension preferredSize2 = components[j].getPreferredSize();
                        n = Math.max(n, preferredSize2.height);
                        n4 += preferredSize2.width;
                    }
                    return new Dimension(n3 + (n4 + (length - 1) * this.padding), n2 + n);
                }
            }
            return new Dimension(0, 0);
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            return this.minimumLayoutSize(container);
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
        }
    }
    
    public class PropertyChangeHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            BasicOptionPaneUI.this.getHandler().propertyChange(propertyChangeEvent);
        }
    }
    
    public class ButtonActionListener implements ActionListener
    {
        protected int buttonIndex;
        
        public ButtonActionListener(final int buttonIndex) {
            this.buttonIndex = buttonIndex;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicOptionPaneUI.this.optionPane != null) {
                final int optionType = BasicOptionPaneUI.this.optionPane.getOptionType();
                final Object[] options = BasicOptionPaneUI.this.optionPane.getOptions();
                if (BasicOptionPaneUI.this.inputComponent != null && (options != null || optionType == -1 || ((optionType == 0 || optionType == 1 || optionType == 2) && this.buttonIndex == 0))) {
                    BasicOptionPaneUI.this.resetInputValue();
                }
                if (options == null) {
                    if (optionType == 2 && this.buttonIndex == 1) {
                        BasicOptionPaneUI.this.optionPane.setValue(2);
                    }
                    else {
                        BasicOptionPaneUI.this.optionPane.setValue(this.buttonIndex);
                    }
                }
                else {
                    BasicOptionPaneUI.this.optionPane.setValue(options[this.buttonIndex]);
                }
            }
        }
    }
    
    private class Handler implements ActionListener, MouseListener, PropertyChangeListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            BasicOptionPaneUI.this.optionPane.setInputValue(((JTextField)actionEvent.getSource()).getText());
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (mouseEvent.getClickCount() == 2) {
                final JList list = (JList)mouseEvent.getSource();
                BasicOptionPaneUI.this.optionPane.setInputValue(list.getModel().getElementAt(list.locationToIndex(mouseEvent.getPoint())));
                BasicOptionPaneUI.this.optionPane.setValue(0);
            }
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getSource() == BasicOptionPaneUI.this.optionPane) {
                if ("ancestor" == propertyChangeEvent.getPropertyName()) {
                    final JOptionPane optionPane = (JOptionPane)propertyChangeEvent.getSource();
                    final boolean b = propertyChangeEvent.getOldValue() == null;
                    switch (optionPane.getMessageType()) {
                        case -1: {
                            if (b) {
                                BasicLookAndFeel.playSound(BasicOptionPaneUI.this.optionPane, "OptionPane.informationSound");
                                break;
                            }
                            break;
                        }
                        case 3: {
                            if (b) {
                                BasicLookAndFeel.playSound(BasicOptionPaneUI.this.optionPane, "OptionPane.questionSound");
                                break;
                            }
                            break;
                        }
                        case 1: {
                            if (b) {
                                BasicLookAndFeel.playSound(BasicOptionPaneUI.this.optionPane, "OptionPane.informationSound");
                                break;
                            }
                            break;
                        }
                        case 2: {
                            if (b) {
                                BasicLookAndFeel.playSound(BasicOptionPaneUI.this.optionPane, "OptionPane.warningSound");
                                break;
                            }
                            break;
                        }
                        case 0: {
                            if (b) {
                                BasicLookAndFeel.playSound(BasicOptionPaneUI.this.optionPane, "OptionPane.errorSound");
                                break;
                            }
                            break;
                        }
                        default: {
                            System.err.println("Undefined JOptionPane type: " + optionPane.getMessageType());
                            break;
                        }
                    }
                }
                final String propertyName = propertyChangeEvent.getPropertyName();
                if (propertyName == "options" || propertyName == "initialValue" || propertyName == "icon" || propertyName == "messageType" || propertyName == "optionType" || propertyName == "message" || propertyName == "selectionValues" || propertyName == "initialSelectionValue" || propertyName == "wantsInput") {
                    BasicOptionPaneUI.this.uninstallComponents();
                    BasicOptionPaneUI.this.installComponents();
                    BasicOptionPaneUI.this.optionPane.validate();
                }
                else if (propertyName == "componentOrientation") {
                    final ComponentOrientation componentOrientation = (ComponentOrientation)propertyChangeEvent.getNewValue();
                    final JOptionPane optionPane2 = (JOptionPane)propertyChangeEvent.getSource();
                    if (componentOrientation != propertyChangeEvent.getOldValue()) {
                        optionPane2.applyComponentOrientation(componentOrientation);
                    }
                }
            }
        }
    }
    
    private static class MultiplexingTextField extends JTextField
    {
        private KeyStroke[] strokes;
        
        MultiplexingTextField(final int n) {
            super(n);
        }
        
        void setKeyStrokes(final KeyStroke[] strokes) {
            this.strokes = strokes;
        }
        
        @Override
        protected boolean processKeyBinding(final KeyStroke keyStroke, final KeyEvent keyEvent, final int n, final boolean b) {
            final boolean processKeyBinding = super.processKeyBinding(keyStroke, keyEvent, n, b);
            if (processKeyBinding && n != 2) {
                for (int i = this.strokes.length - 1; i >= 0; --i) {
                    if (this.strokes[i].equals(keyStroke)) {
                        return false;
                    }
                }
            }
            return processKeyBinding;
        }
    }
    
    private static class Actions extends UIAction
    {
        private static final String CLOSE = "close";
        
        Actions(final String s) {
            super(s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (this.getName() == "close") {
                ((JOptionPane)actionEvent.getSource()).setValue(-1);
            }
        }
    }
    
    private static class ButtonFactory
    {
        private String text;
        private int mnemonic;
        private Icon icon;
        private int minimumWidth;
        
        ButtonFactory(final String text, final int mnemonic, final Icon icon, final int minimumWidth) {
            this.minimumWidth = -1;
            this.text = text;
            this.mnemonic = mnemonic;
            this.icon = icon;
            this.minimumWidth = minimumWidth;
        }
        
        JButton createButton() {
            JButton button;
            if (this.minimumWidth > 0) {
                button = new ConstrainedButton(this.text, this.minimumWidth);
            }
            else {
                button = new JButton(this.text);
            }
            if (this.icon != null) {
                button.setIcon(this.icon);
            }
            if (this.mnemonic != 0) {
                button.setMnemonic(this.mnemonic);
            }
            return button;
        }
        
        private static class ConstrainedButton extends JButton
        {
            int minimumWidth;
            
            ConstrainedButton(final String s, final int minimumWidth) {
                super(s);
                this.minimumWidth = minimumWidth;
            }
            
            @Override
            public Dimension getMinimumSize() {
                final Dimension minimumSize = super.getMinimumSize();
                minimumSize.width = Math.max(minimumSize.width, this.minimumWidth);
                return minimumSize;
            }
            
            @Override
            public Dimension getPreferredSize() {
                final Dimension preferredSize = super.getPreferredSize();
                preferredSize.width = Math.max(preferredSize.width, this.minimumWidth);
                return preferredSize;
            }
        }
    }
}
