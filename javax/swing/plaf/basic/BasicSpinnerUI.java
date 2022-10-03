package javax.swing.plaf.basic;

import javax.swing.event.ChangeEvent;
import java.awt.ComponentOrientation;
import java.beans.PropertyChangeEvent;
import javax.swing.ButtonModel;
import java.awt.event.FocusEvent;
import java.awt.KeyboardFocusManager;
import java.awt.event.MouseEvent;
import java.text.Format;
import javax.swing.text.InternationalFormatter;
import java.util.Map;
import java.text.AttributedCharacterIterator;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import javax.swing.SpinnerDateModel;
import java.awt.event.ActionEvent;
import java.awt.AWTEvent;
import javax.swing.Timer;
import javax.swing.AbstractAction;
import java.awt.Insets;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import javax.swing.SpinnerModel;
import java.awt.Container;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.UIResource;
import javax.swing.UIManager;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import java.awt.LayoutManager;
import javax.swing.LookAndFeel;
import javax.swing.JFormattedTextField;
import java.awt.event.FocusListener;
import javax.swing.event.ChangeListener;
import sun.swing.DefaultLookup;
import java.awt.Component;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import javax.swing.JSpinner;
import javax.swing.plaf.SpinnerUI;

public class BasicSpinnerUI extends SpinnerUI
{
    protected JSpinner spinner;
    private Handler handler;
    private static final ArrowButtonHandler nextButtonHandler;
    private static final ArrowButtonHandler previousButtonHandler;
    private PropertyChangeListener propertyChangeListener;
    private static final Dimension zeroSize;
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicSpinnerUI();
    }
    
    private void maybeAdd(final Component component, final String s) {
        if (component != null) {
            this.spinner.add(component, s);
        }
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.spinner = (JSpinner)component;
        this.installDefaults();
        this.installListeners();
        this.maybeAdd(this.createNextButton(), "Next");
        this.maybeAdd(this.createPreviousButton(), "Previous");
        this.maybeAdd(this.createEditor(), "Editor");
        this.updateEnabledState();
        this.installKeyboardActions();
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallDefaults();
        this.uninstallListeners();
        this.spinner = null;
        component.removeAll();
    }
    
    protected void installListeners() {
        this.propertyChangeListener = this.createPropertyChangeListener();
        this.spinner.addPropertyChangeListener(this.propertyChangeListener);
        if (DefaultLookup.getBoolean(this.spinner, this, "Spinner.disableOnBoundaryValues", false)) {
            this.spinner.addChangeListener(this.getHandler());
        }
        final JComponent editor = this.spinner.getEditor();
        if (editor != null && editor instanceof JSpinner.DefaultEditor) {
            final JFormattedTextField textField = ((JSpinner.DefaultEditor)editor).getTextField();
            if (textField != null) {
                textField.addFocusListener(BasicSpinnerUI.nextButtonHandler);
                textField.addFocusListener(BasicSpinnerUI.previousButtonHandler);
            }
        }
    }
    
    protected void uninstallListeners() {
        this.spinner.removePropertyChangeListener(this.propertyChangeListener);
        this.spinner.removeChangeListener(this.handler);
        final JComponent editor = this.spinner.getEditor();
        this.removeEditorBorderListener(editor);
        if (editor instanceof JSpinner.DefaultEditor) {
            final JFormattedTextField textField = ((JSpinner.DefaultEditor)editor).getTextField();
            if (textField != null) {
                textField.removeFocusListener(BasicSpinnerUI.nextButtonHandler);
                textField.removeFocusListener(BasicSpinnerUI.previousButtonHandler);
            }
        }
        this.propertyChangeListener = null;
        this.handler = null;
    }
    
    protected void installDefaults() {
        this.spinner.setLayout(this.createLayout());
        LookAndFeel.installBorder(this.spinner, "Spinner.border");
        LookAndFeel.installColorsAndFont(this.spinner, "Spinner.background", "Spinner.foreground", "Spinner.font");
        LookAndFeel.installProperty(this.spinner, "opaque", Boolean.TRUE);
    }
    
    protected void uninstallDefaults() {
        this.spinner.setLayout(null);
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    protected void installNextButtonListeners(final Component component) {
        this.installButtonListeners(component, BasicSpinnerUI.nextButtonHandler);
    }
    
    protected void installPreviousButtonListeners(final Component component) {
        this.installButtonListeners(component, BasicSpinnerUI.previousButtonHandler);
    }
    
    private void installButtonListeners(final Component component, final ArrowButtonHandler arrowButtonHandler) {
        if (component instanceof JButton) {
            ((JButton)component).addActionListener(arrowButtonHandler);
        }
        component.addMouseListener(arrowButtonHandler);
    }
    
    protected LayoutManager createLayout() {
        return this.getHandler();
    }
    
    protected PropertyChangeListener createPropertyChangeListener() {
        return this.getHandler();
    }
    
    protected Component createPreviousButton() {
        final Component arrowButton = this.createArrowButton(5);
        arrowButton.setName("Spinner.previousButton");
        this.installPreviousButtonListeners(arrowButton);
        return arrowButton;
    }
    
    protected Component createNextButton() {
        final Component arrowButton = this.createArrowButton(1);
        arrowButton.setName("Spinner.nextButton");
        this.installNextButtonListeners(arrowButton);
        return arrowButton;
    }
    
    private Component createArrowButton(final int n) {
        final BasicArrowButton basicArrowButton = new BasicArrowButton(n);
        final Border border = UIManager.getBorder("Spinner.arrowButtonBorder");
        if (border instanceof UIResource) {
            basicArrowButton.setBorder(new CompoundBorder(border, null));
        }
        else {
            basicArrowButton.setBorder(border);
        }
        basicArrowButton.setInheritsPopupMenu(true);
        return basicArrowButton;
    }
    
    protected JComponent createEditor() {
        final JComponent editor = this.spinner.getEditor();
        this.maybeRemoveEditorBorder(editor);
        this.installEditorBorderListener(editor);
        editor.setInheritsPopupMenu(true);
        this.updateEditorAlignment(editor);
        return editor;
    }
    
    protected void replaceEditor(final JComponent component, final JComponent component2) {
        this.spinner.remove(component);
        this.maybeRemoveEditorBorder(component2);
        this.installEditorBorderListener(component2);
        component2.setInheritsPopupMenu(true);
        this.spinner.add(component2, "Editor");
    }
    
    private void updateEditorAlignment(final JComponent component) {
        if (component instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor)component).getTextField().setHorizontalAlignment(UIManager.getInt("Spinner.editorAlignment"));
        }
    }
    
    private void maybeRemoveEditorBorder(JComponent component) {
        if (!UIManager.getBoolean("Spinner.editorBorderPainted")) {
            if (component instanceof JPanel && component.getBorder() == null && component.getComponentCount() > 0) {
                component = (JComponent)component.getComponent(0);
            }
            if (component != null && component.getBorder() instanceof UIResource) {
                component.setBorder(null);
            }
        }
    }
    
    private void installEditorBorderListener(JComponent component) {
        if (!UIManager.getBoolean("Spinner.editorBorderPainted")) {
            if (component instanceof JPanel && component.getBorder() == null && component.getComponentCount() > 0) {
                component = (JComponent)component.getComponent(0);
            }
            if (component != null && (component.getBorder() == null || component.getBorder() instanceof UIResource)) {
                component.addPropertyChangeListener(this.getHandler());
            }
        }
    }
    
    private void removeEditorBorderListener(JComponent component) {
        if (!UIManager.getBoolean("Spinner.editorBorderPainted")) {
            if (component instanceof JPanel && component.getComponentCount() > 0) {
                component = (JComponent)component.getComponent(0);
            }
            if (component != null) {
                component.removePropertyChangeListener(this.getHandler());
            }
        }
    }
    
    private void updateEnabledState() {
        this.updateEnabledState(this.spinner, this.spinner.isEnabled());
    }
    
    private void updateEnabledState(final Container container, final boolean b) {
        for (int i = container.getComponentCount() - 1; i >= 0; --i) {
            final Component component = container.getComponent(i);
            if (DefaultLookup.getBoolean(this.spinner, this, "Spinner.disableOnBoundaryValues", false)) {
                final SpinnerModel model = this.spinner.getModel();
                if (component.getName() == "Spinner.nextButton" && model.getNextValue() == null) {
                    component.setEnabled(false);
                }
                else if (component.getName() == "Spinner.previousButton" && model.getPreviousValue() == null) {
                    component.setEnabled(false);
                }
                else {
                    component.setEnabled(b);
                }
            }
            else {
                component.setEnabled(b);
            }
            if (component instanceof Container) {
                this.updateEnabledState((Container)component, b);
            }
        }
    }
    
    protected void installKeyboardActions() {
        SwingUtilities.replaceUIInputMap(this.spinner, 1, this.getInputMap(1));
        LazyActionMap.installLazyActionMap(this.spinner, BasicSpinnerUI.class, "Spinner.actionMap");
    }
    
    private InputMap getInputMap(final int n) {
        if (n == 1) {
            return (InputMap)DefaultLookup.get(this.spinner, this, "Spinner.ancestorInputMap");
        }
        return null;
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put("increment", BasicSpinnerUI.nextButtonHandler);
        lazyActionMap.put("decrement", BasicSpinnerUI.previousButtonHandler);
    }
    
    @Override
    public int getBaseline(final JComponent component, int n, int n2) {
        super.getBaseline(component, n, n2);
        final JComponent editor = this.spinner.getEditor();
        final Insets insets = this.spinner.getInsets();
        n = n - insets.left - insets.right;
        n2 = n2 - insets.top - insets.bottom;
        if (n >= 0 && n2 >= 0) {
            final int baseline = editor.getBaseline(n, n2);
            if (baseline >= 0) {
                return insets.top + baseline;
            }
        }
        return -1;
    }
    
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final JComponent component) {
        super.getBaselineResizeBehavior(component);
        return this.spinner.getEditor().getBaselineResizeBehavior();
    }
    
    static {
        nextButtonHandler = new ArrowButtonHandler("increment", true);
        previousButtonHandler = new ArrowButtonHandler("decrement", false);
        zeroSize = new Dimension(0, 0);
    }
    
    private static class ArrowButtonHandler extends AbstractAction implements FocusListener, MouseListener, UIResource
    {
        final Timer autoRepeatTimer;
        final boolean isNext;
        JSpinner spinner;
        JButton arrowButton;
        
        ArrowButtonHandler(final String s, final boolean isNext) {
            super(s);
            this.spinner = null;
            this.arrowButton = null;
            this.isNext = isNext;
            (this.autoRepeatTimer = new Timer(60, this)).setInitialDelay(300);
        }
        
        private JSpinner eventToSpinner(final AWTEvent awtEvent) {
            Object o;
            for (o = awtEvent.getSource(); o instanceof Component && !(o instanceof JSpinner); o = ((Component)o).getParent()) {}
            return (o instanceof JSpinner) ? ((JSpinner)o) : null;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            JSpinner spinner = this.spinner;
            if (!(actionEvent.getSource() instanceof Timer)) {
                spinner = this.eventToSpinner(actionEvent);
                if (actionEvent.getSource() instanceof JButton) {
                    this.arrowButton = (JButton)actionEvent.getSource();
                }
            }
            else if (this.arrowButton != null && !this.arrowButton.getModel().isPressed() && this.autoRepeatTimer.isRunning()) {
                this.autoRepeatTimer.stop();
                spinner = null;
                this.arrowButton = null;
            }
            if (spinner != null) {
                try {
                    final int calendarField = this.getCalendarField(spinner);
                    spinner.commitEdit();
                    if (calendarField != -1) {
                        ((SpinnerDateModel)spinner.getModel()).setCalendarField(calendarField);
                    }
                    final Object value = this.isNext ? spinner.getNextValue() : spinner.getPreviousValue();
                    if (value != null) {
                        spinner.setValue(value);
                        this.select(spinner);
                    }
                }
                catch (final IllegalArgumentException ex) {
                    UIManager.getLookAndFeel().provideErrorFeedback(spinner);
                }
                catch (final ParseException ex2) {
                    UIManager.getLookAndFeel().provideErrorFeedback(spinner);
                }
            }
        }
        
        private void select(final JSpinner spinner) {
            final JComponent editor = spinner.getEditor();
            if (editor instanceof JSpinner.DateEditor) {
                final JSpinner.DateEditor dateEditor = (JSpinner.DateEditor)editor;
                final JFormattedTextField textField = dateEditor.getTextField();
                final SimpleDateFormat format = dateEditor.getFormat();
                final Object value;
                if (format != null && (value = spinner.getValue()) != null) {
                    final DateFormat.Field ofCalendarField = DateFormat.Field.ofCalendarField(dateEditor.getModel().getCalendarField());
                    if (ofCalendarField != null) {
                        try {
                            final AttributedCharacterIterator formatToCharacterIterator = format.formatToCharacterIterator(value);
                            if (!this.select(textField, formatToCharacterIterator, ofCalendarField) && ofCalendarField == DateFormat.Field.HOUR0) {
                                this.select(textField, formatToCharacterIterator, DateFormat.Field.HOUR1);
                            }
                        }
                        catch (final IllegalArgumentException ex) {}
                    }
                }
            }
        }
        
        private boolean select(final JFormattedTextField formattedTextField, final AttributedCharacterIterator attributedCharacterIterator, final DateFormat.Field field) {
            final int length = formattedTextField.getDocument().getLength();
            attributedCharacterIterator.first();
            do {
                final Map<AttributedCharacterIterator.Attribute, Object> attributes = attributedCharacterIterator.getAttributes();
                if (attributes != null && attributes.containsKey(field)) {
                    final int runStart = attributedCharacterIterator.getRunStart(field);
                    final int runLimit = attributedCharacterIterator.getRunLimit(field);
                    if (runStart != -1 && runLimit != -1 && runStart <= length && runLimit <= length) {
                        formattedTextField.select(runStart, runLimit);
                    }
                    return true;
                }
            } while (attributedCharacterIterator.next() != '\uffff');
            return false;
        }
        
        private int getCalendarField(final JSpinner spinner) {
            final JComponent editor = spinner.getEditor();
            if (editor instanceof JSpinner.DateEditor) {
                final JFormattedTextField textField = ((JSpinner.DateEditor)editor).getTextField();
                final int selectionStart = textField.getSelectionStart();
                final JFormattedTextField.AbstractFormatter formatter = textField.getFormatter();
                if (formatter instanceof InternationalFormatter) {
                    final Format.Field[] fields = ((InternationalFormatter)formatter).getFields(selectionStart);
                    for (int i = 0; i < fields.length; ++i) {
                        if (fields[i] instanceof DateFormat.Field) {
                            int calendarField;
                            if (fields[i] == DateFormat.Field.HOUR1) {
                                calendarField = 10;
                            }
                            else {
                                calendarField = ((DateFormat.Field)fields[i]).getCalendarField();
                            }
                            if (calendarField != -1) {
                                return calendarField;
                            }
                        }
                    }
                }
            }
            return -1;
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (SwingUtilities.isLeftMouseButton(mouseEvent) && mouseEvent.getComponent().isEnabled()) {
                this.spinner = this.eventToSpinner(mouseEvent);
                this.autoRepeatTimer.start();
                this.focusSpinnerIfNecessary();
            }
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            this.autoRepeatTimer.stop();
            this.arrowButton = null;
            this.spinner = null;
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            if (this.spinner != null && !this.autoRepeatTimer.isRunning() && this.spinner == this.eventToSpinner(mouseEvent)) {
                this.autoRepeatTimer.start();
            }
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            if (this.autoRepeatTimer.isRunning()) {
                this.autoRepeatTimer.stop();
            }
        }
        
        private void focusSpinnerIfNecessary() {
            final Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (this.spinner.isRequestFocusEnabled() && (focusOwner == null || !SwingUtilities.isDescendingFrom(focusOwner, this.spinner))) {
                Container container = this.spinner;
                if (!container.isFocusCycleRoot()) {
                    container = container.getFocusCycleRootAncestor();
                }
                if (container != null) {
                    final Component componentAfter = container.getFocusTraversalPolicy().getComponentAfter(container, this.spinner);
                    if (componentAfter != null && SwingUtilities.isDescendingFrom(componentAfter, this.spinner)) {
                        componentAfter.requestFocus();
                    }
                }
            }
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            if (this.spinner == this.eventToSpinner(focusEvent)) {
                if (this.autoRepeatTimer.isRunning()) {
                    this.autoRepeatTimer.stop();
                }
                this.spinner = null;
                if (this.arrowButton != null) {
                    final ButtonModel model = this.arrowButton.getModel();
                    model.setPressed(false);
                    model.setArmed(false);
                    this.arrowButton = null;
                }
            }
        }
    }
    
    private static class Handler implements LayoutManager, PropertyChangeListener, ChangeListener
    {
        private Component nextButton;
        private Component previousButton;
        private Component editor;
        
        private Handler() {
            this.nextButton = null;
            this.previousButton = null;
            this.editor = null;
        }
        
        @Override
        public void addLayoutComponent(final String s, final Component editor) {
            if ("Next".equals(s)) {
                this.nextButton = editor;
            }
            else if ("Previous".equals(s)) {
                this.previousButton = editor;
            }
            else if ("Editor".equals(s)) {
                this.editor = editor;
            }
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
            if (component == this.nextButton) {
                this.nextButton = null;
            }
            else if (component == this.previousButton) {
                this.previousButton = null;
            }
            else if (component == this.editor) {
                this.editor = null;
            }
        }
        
        private Dimension preferredSize(final Component component) {
            return (component == null) ? BasicSpinnerUI.zeroSize : component.getPreferredSize();
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            final Dimension preferredSize = this.preferredSize(this.nextButton);
            final Dimension preferredSize2 = this.preferredSize(this.previousButton);
            final Dimension preferredSize3 = this.preferredSize(this.editor);
            preferredSize3.height = (preferredSize3.height + 1) / 2 * 2;
            final Dimension dimension2;
            final Dimension dimension = dimension2 = new Dimension(preferredSize3.width, preferredSize3.height);
            dimension2.width += Math.max(preferredSize.width, preferredSize2.width);
            final Insets insets = container.getInsets();
            final Dimension dimension3 = dimension;
            dimension3.width += insets.left + insets.right;
            final Dimension dimension4 = dimension;
            dimension4.height += insets.top + insets.bottom;
            return dimension;
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            return this.preferredLayoutSize(container);
        }
        
        private void setBounds(final Component component, final int n, final int n2, final int n3, final int n4) {
            if (component != null) {
                component.setBounds(n, n2, n3, n4);
            }
        }
        
        @Override
        public void layoutContainer(final Container container) {
            final int width = container.getWidth();
            final int height = container.getHeight();
            final Insets insets = container.getInsets();
            if (this.nextButton == null && this.previousButton == null) {
                this.setBounds(this.editor, insets.left, insets.top, width - insets.left - insets.right, height - insets.top - insets.bottom);
                return;
            }
            final int max = Math.max(this.preferredSize(this.nextButton).width, this.preferredSize(this.previousButton).width);
            final int n = height - (insets.top + insets.bottom);
            Insets insets2 = UIManager.getInsets("Spinner.arrowButtonInsets");
            if (insets2 == null) {
                insets2 = insets;
            }
            int left;
            int n2;
            int left2;
            if (container.getComponentOrientation().isLeftToRight()) {
                left = insets.left;
                n2 = width - insets.left - max - insets2.right;
                left2 = width - max - insets2.right;
            }
            else {
                left2 = insets2.left;
                left = left2 + max;
                n2 = width - insets2.left - max - insets.right;
            }
            final int top = insets2.top;
            final int n3 = height / 2 + height % 2 - top;
            final int n4 = insets2.top + n3;
            final int n5 = height - n4 - insets2.bottom;
            this.setBounds(this.editor, left, insets.top, n2, n);
            this.setBounds(this.nextButton, left2, top, max, n3);
            this.setBounds(this.previousButton, left2, n4, max, n5);
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyChangeEvent.getSource() instanceof JSpinner) {
                final JSpinner spinner = (JSpinner)propertyChangeEvent.getSource();
                final SpinnerUI ui = spinner.getUI();
                if (ui instanceof BasicSpinnerUI) {
                    final BasicSpinnerUI basicSpinnerUI = (BasicSpinnerUI)ui;
                    if ("editor".equals(propertyName)) {
                        final JComponent component = (JComponent)propertyChangeEvent.getOldValue();
                        final JComponent component2 = (JComponent)propertyChangeEvent.getNewValue();
                        basicSpinnerUI.replaceEditor(component, component2);
                        basicSpinnerUI.updateEnabledState();
                        if (component instanceof JSpinner.DefaultEditor) {
                            final JFormattedTextField textField = ((JSpinner.DefaultEditor)component).getTextField();
                            if (textField != null) {
                                textField.removeFocusListener(BasicSpinnerUI.nextButtonHandler);
                                textField.removeFocusListener(BasicSpinnerUI.previousButtonHandler);
                            }
                        }
                        if (component2 instanceof JSpinner.DefaultEditor) {
                            final JFormattedTextField textField2 = ((JSpinner.DefaultEditor)component2).getTextField();
                            if (textField2 != null) {
                                if (textField2.getFont() instanceof UIResource) {
                                    textField2.setFont(spinner.getFont());
                                }
                                textField2.addFocusListener(BasicSpinnerUI.nextButtonHandler);
                                textField2.addFocusListener(BasicSpinnerUI.previousButtonHandler);
                            }
                        }
                    }
                    else if ("enabled".equals(propertyName) || "model".equals(propertyName)) {
                        basicSpinnerUI.updateEnabledState();
                    }
                    else if ("font".equals(propertyName)) {
                        final JComponent editor = spinner.getEditor();
                        if (editor != null && editor instanceof JSpinner.DefaultEditor) {
                            final JFormattedTextField textField3 = ((JSpinner.DefaultEditor)editor).getTextField();
                            if (textField3 != null && textField3.getFont() instanceof UIResource) {
                                textField3.setFont(spinner.getFont());
                            }
                        }
                    }
                    else if ("ToolTipText".equals(propertyName)) {
                        this.updateToolTipTextForChildren(spinner);
                    }
                    else if ("componentOrientation".equals(propertyName)) {
                        final ComponentOrientation componentOrientation = (ComponentOrientation)propertyChangeEvent.getNewValue();
                        if (componentOrientation != propertyChangeEvent.getOldValue()) {
                            final JComponent editor2 = spinner.getEditor();
                            if (editor2 != null) {
                                editor2.applyComponentOrientation(componentOrientation);
                            }
                            spinner.revalidate();
                            spinner.repaint();
                        }
                    }
                }
            }
            else if (propertyChangeEvent.getSource() instanceof JComponent) {
                final JComponent component3 = (JComponent)propertyChangeEvent.getSource();
                if (component3.getParent() instanceof JPanel && component3.getParent().getParent() instanceof JSpinner && "border".equals(propertyName)) {
                    final SpinnerUI ui2 = ((JSpinner)component3.getParent().getParent()).getUI();
                    if (ui2 instanceof BasicSpinnerUI) {
                        ((BasicSpinnerUI)ui2).maybeRemoveEditorBorder(component3);
                    }
                }
            }
        }
        
        private void updateToolTipTextForChildren(final JComponent component) {
            final String toolTipText = component.getToolTipText();
            final Component[] components = component.getComponents();
            for (int i = 0; i < components.length; ++i) {
                if (components[i] instanceof JSpinner.DefaultEditor) {
                    final JFormattedTextField textField = ((JSpinner.DefaultEditor)components[i]).getTextField();
                    if (textField != null) {
                        textField.setToolTipText(toolTipText);
                    }
                }
                else if (components[i] instanceof JComponent) {
                    ((JComponent)components[i]).setToolTipText(component.getToolTipText());
                }
            }
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            if (changeEvent.getSource() instanceof JSpinner) {
                final JSpinner spinner = (JSpinner)changeEvent.getSource();
                final SpinnerUI ui = spinner.getUI();
                if (DefaultLookup.getBoolean(spinner, ui, "Spinner.disableOnBoundaryValues", false) && ui instanceof BasicSpinnerUI) {
                    ((BasicSpinnerUI)ui).updateEnabledState();
                }
            }
        }
    }
}
