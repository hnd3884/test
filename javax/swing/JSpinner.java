package javax.swing;

import java.awt.Rectangle;
import java.awt.Point;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleEditableText;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleValue;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;
import javax.swing.text.DocumentFilter;
import java.awt.ComponentOrientation;
import java.text.DecimalFormat;
import java.text.spi.NumberFormatProvider;
import java.text.NumberFormat;
import javax.swing.text.NumberFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.text.SimpleDateFormat;
import sun.util.locale.provider.LocaleResources;
import java.util.Calendar;
import java.util.spi.LocaleServiceProvider;
import sun.util.locale.provider.LocaleProviderAdapter;
import java.text.spi.DateFormatProvider;
import java.util.Locale;
import java.text.DateFormat;
import javax.swing.text.DateFormatter;
import java.awt.Insets;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.awt.Container;
import java.awt.Component;
import java.awt.LayoutManager;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SpinnerUI;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.accessibility.Accessible;

public class JSpinner extends JComponent implements Accessible
{
    private static final String uiClassID = "SpinnerUI";
    private static final Action DISABLED_ACTION;
    private SpinnerModel model;
    private JComponent editor;
    private ChangeListener modelListener;
    private transient ChangeEvent changeEvent;
    private boolean editorExplicitlySet;
    
    public JSpinner(final SpinnerModel model) {
        this.editorExplicitlySet = false;
        if (model == null) {
            throw new NullPointerException("model cannot be null");
        }
        this.model = model;
        this.editor = this.createEditor(model);
        this.setUIProperty("opaque", true);
        this.updateUI();
    }
    
    public JSpinner() {
        this(new SpinnerNumberModel());
    }
    
    public SpinnerUI getUI() {
        return (SpinnerUI)this.ui;
    }
    
    public void setUI(final SpinnerUI ui) {
        super.setUI(ui);
    }
    
    @Override
    public String getUIClassID() {
        return "SpinnerUI";
    }
    
    @Override
    public void updateUI() {
        this.setUI((SpinnerUI)UIManager.getUI(this));
        this.invalidate();
    }
    
    protected JComponent createEditor(final SpinnerModel spinnerModel) {
        if (spinnerModel instanceof SpinnerDateModel) {
            return new DateEditor(this);
        }
        if (spinnerModel instanceof SpinnerListModel) {
            return new ListEditor(this);
        }
        if (spinnerModel instanceof SpinnerNumberModel) {
            return new NumberEditor(this);
        }
        return new DefaultEditor(this);
    }
    
    public void setModel(final SpinnerModel model) {
        if (model == null) {
            throw new IllegalArgumentException("null model");
        }
        if (!model.equals(this.model)) {
            final SpinnerModel model2 = this.model;
            this.model = model;
            if (this.modelListener != null) {
                model2.removeChangeListener(this.modelListener);
                this.model.addChangeListener(this.modelListener);
            }
            this.firePropertyChange("model", model2, model);
            if (!this.editorExplicitlySet) {
                this.setEditor(this.createEditor(model));
                this.editorExplicitlySet = false;
            }
            this.repaint();
            this.revalidate();
        }
    }
    
    public SpinnerModel getModel() {
        return this.model;
    }
    
    public Object getValue() {
        return this.getModel().getValue();
    }
    
    public void setValue(final Object value) {
        this.getModel().setValue(value);
    }
    
    public Object getNextValue() {
        return this.getModel().getNextValue();
    }
    
    public void addChangeListener(final ChangeListener changeListener) {
        if (this.modelListener == null) {
            this.modelListener = new ModelListener();
            this.getModel().addChangeListener(this.modelListener);
        }
        this.listenerList.add(ChangeListener.class, changeListener);
    }
    
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
    
    public Object getPreviousValue() {
        return this.getModel().getPreviousValue();
    }
    
    public void setEditor(final JComponent editor) {
        if (editor == null) {
            throw new IllegalArgumentException("null editor");
        }
        if (!editor.equals(this.editor)) {
            final JComponent editor2 = this.editor;
            this.editor = editor;
            if (editor2 instanceof DefaultEditor) {
                ((DefaultEditor)editor2).dismiss(this);
            }
            this.editorExplicitlySet = true;
            this.firePropertyChange("editor", editor2, editor);
            this.revalidate();
            this.repaint();
        }
    }
    
    public JComponent getEditor() {
        return this.editor;
    }
    
    public void commitEdit() throws ParseException {
        final JComponent editor = this.getEditor();
        if (editor instanceof DefaultEditor) {
            ((DefaultEditor)editor).commitEdit();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("SpinnerUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJSpinner();
        }
        return this.accessibleContext;
    }
    
    static {
        DISABLED_ACTION = new DisabledAction();
    }
    
    private class ModelListener implements ChangeListener, Serializable
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            JSpinner.this.fireStateChanged();
        }
    }
    
    public static class DefaultEditor extends JPanel implements ChangeListener, PropertyChangeListener, LayoutManager
    {
        public DefaultEditor(final JSpinner spinner) {
            super(null);
            final JFormattedTextField formattedTextField = new JFormattedTextField();
            formattedTextField.setName("Spinner.formattedTextField");
            formattedTextField.setValue(spinner.getValue());
            formattedTextField.addPropertyChangeListener(this);
            formattedTextField.setEditable(false);
            formattedTextField.setInheritsPopupMenu(true);
            final String toolTipText = spinner.getToolTipText();
            if (toolTipText != null) {
                formattedTextField.setToolTipText(toolTipText);
            }
            this.add(formattedTextField);
            this.setLayout(this);
            spinner.addChangeListener(this);
            final ActionMap actionMap = formattedTextField.getActionMap();
            if (actionMap != null) {
                actionMap.put("increment", JSpinner.DISABLED_ACTION);
                actionMap.put("decrement", JSpinner.DISABLED_ACTION);
            }
        }
        
        public void dismiss(final JSpinner spinner) {
            spinner.removeChangeListener(this);
        }
        
        public JSpinner getSpinner() {
            for (Container parent = this; parent != null; parent = parent.getParent()) {
                if (parent instanceof JSpinner) {
                    return (JSpinner)parent;
                }
            }
            return null;
        }
        
        public JFormattedTextField getTextField() {
            return (JFormattedTextField)this.getComponent(0);
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            this.getTextField().setValue(((JSpinner)changeEvent.getSource()).getValue());
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final JSpinner spinner = this.getSpinner();
            if (spinner == null) {
                return;
            }
            final Object source = propertyChangeEvent.getSource();
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (source instanceof JFormattedTextField && "value".equals(propertyName)) {
                final Object value = spinner.getValue();
                try {
                    spinner.setValue(this.getTextField().getValue());
                }
                catch (final IllegalArgumentException ex) {
                    try {
                        ((JFormattedTextField)source).setValue(value);
                    }
                    catch (final IllegalArgumentException ex2) {}
                }
            }
        }
        
        @Override
        public void addLayoutComponent(final String s, final Component component) {
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
        }
        
        private Dimension insetSize(final Container container) {
            final Insets insets = container.getInsets();
            return new Dimension(insets.left + insets.right, insets.top + insets.bottom);
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            final Dimension insetSize = this.insetSize(container);
            if (container.getComponentCount() > 0) {
                final Dimension preferredSize = this.getComponent(0).getPreferredSize();
                final Dimension dimension = insetSize;
                dimension.width += preferredSize.width;
                final Dimension dimension2 = insetSize;
                dimension2.height += preferredSize.height;
            }
            return insetSize;
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            final Dimension insetSize = this.insetSize(container);
            if (container.getComponentCount() > 0) {
                final Dimension minimumSize = this.getComponent(0).getMinimumSize();
                final Dimension dimension = insetSize;
                dimension.width += minimumSize.width;
                final Dimension dimension2 = insetSize;
                dimension2.height += minimumSize.height;
            }
            return insetSize;
        }
        
        @Override
        public void layoutContainer(final Container container) {
            if (container.getComponentCount() > 0) {
                final Insets insets = container.getInsets();
                this.getComponent(0).setBounds(insets.left, insets.top, container.getWidth() - (insets.left + insets.right), container.getHeight() - (insets.top + insets.bottom));
            }
        }
        
        public void commitEdit() throws ParseException {
            this.getTextField().commitEdit();
        }
        
        @Override
        public int getBaseline(int n, int n2) {
            super.getBaseline(n, n2);
            final Insets insets = this.getInsets();
            n = n - insets.left - insets.right;
            n2 = n2 - insets.top - insets.bottom;
            final int baseline = this.getComponent(0).getBaseline(n, n2);
            if (baseline >= 0) {
                return baseline + insets.top;
            }
            return -1;
        }
        
        @Override
        public BaselineResizeBehavior getBaselineResizeBehavior() {
            return this.getComponent(0).getBaselineResizeBehavior();
        }
    }
    
    private static class DateEditorFormatter extends DateFormatter
    {
        private final SpinnerDateModel model;
        
        DateEditorFormatter(final SpinnerDateModel model, final DateFormat dateFormat) {
            super(dateFormat);
            this.model = model;
        }
        
        @Override
        public void setMinimum(final Comparable start) {
            this.model.setStart(start);
        }
        
        @Override
        public Comparable getMinimum() {
            return this.model.getStart();
        }
        
        @Override
        public void setMaximum(final Comparable end) {
            this.model.setEnd(end);
        }
        
        @Override
        public Comparable getMaximum() {
            return this.model.getEnd();
        }
    }
    
    public static class DateEditor extends DefaultEditor
    {
        private static String getDefaultPattern(final Locale locale) {
            LocaleResources localeResources = LocaleProviderAdapter.getAdapter(DateFormatProvider.class, locale).getLocaleResources(locale);
            if (localeResources == null) {
                localeResources = LocaleProviderAdapter.forJRE().getLocaleResources(locale);
            }
            return localeResources.getDateTimePattern(3, 3, null);
        }
        
        public DateEditor(final JSpinner spinner) {
            this(spinner, getDefaultPattern(spinner.getLocale()));
        }
        
        public DateEditor(final JSpinner spinner, final String s) {
            this(spinner, new SimpleDateFormat(s, spinner.getLocale()));
        }
        
        private DateEditor(final JSpinner spinner, final DateFormat dateFormat) {
            super(spinner);
            if (!(spinner.getModel() instanceof SpinnerDateModel)) {
                throw new IllegalArgumentException("model not a SpinnerDateModel");
            }
            final SpinnerDateModel spinnerDateModel = (SpinnerDateModel)spinner.getModel();
            final DateEditorFormatter dateEditorFormatter = new DateEditorFormatter(spinnerDateModel, dateFormat);
            final DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(dateEditorFormatter);
            final JFormattedTextField textField = this.getTextField();
            textField.setEditable(true);
            textField.setFormatterFactory(formatterFactory);
            try {
                textField.setColumns(Math.max(dateEditorFormatter.valueToString(spinnerDateModel.getStart()).length(), dateEditorFormatter.valueToString(spinnerDateModel.getEnd()).length()));
            }
            catch (final ParseException ex) {}
        }
        
        public SimpleDateFormat getFormat() {
            return (SimpleDateFormat)((DateFormatter)this.getTextField().getFormatter()).getFormat();
        }
        
        public SpinnerDateModel getModel() {
            return (SpinnerDateModel)this.getSpinner().getModel();
        }
    }
    
    private static class NumberEditorFormatter extends NumberFormatter
    {
        private final SpinnerNumberModel model;
        
        NumberEditorFormatter(final SpinnerNumberModel model, final NumberFormat numberFormat) {
            super(numberFormat);
            this.model = model;
            this.setValueClass(model.getValue().getClass());
        }
        
        @Override
        public void setMinimum(final Comparable minimum) {
            this.model.setMinimum(minimum);
        }
        
        @Override
        public Comparable getMinimum() {
            return this.model.getMinimum();
        }
        
        @Override
        public void setMaximum(final Comparable maximum) {
            this.model.setMaximum(maximum);
        }
        
        @Override
        public Comparable getMaximum() {
            return this.model.getMaximum();
        }
    }
    
    public static class NumberEditor extends DefaultEditor
    {
        private static String getDefaultPattern(final Locale locale) {
            LocaleResources localeResources = LocaleProviderAdapter.getAdapter(NumberFormatProvider.class, locale).getLocaleResources(locale);
            if (localeResources == null) {
                localeResources = LocaleProviderAdapter.forJRE().getLocaleResources(locale);
            }
            return localeResources.getNumberPatterns()[0];
        }
        
        public NumberEditor(final JSpinner spinner) {
            this(spinner, getDefaultPattern(spinner.getLocale()));
        }
        
        public NumberEditor(final JSpinner spinner, final String s) {
            this(spinner, new DecimalFormat(s));
        }
        
        private NumberEditor(final JSpinner spinner, final DecimalFormat decimalFormat) {
            super(spinner);
            if (!(spinner.getModel() instanceof SpinnerNumberModel)) {
                throw new IllegalArgumentException("model not a SpinnerNumberModel");
            }
            final SpinnerNumberModel spinnerNumberModel = (SpinnerNumberModel)spinner.getModel();
            final NumberEditorFormatter numberEditorFormatter = new NumberEditorFormatter(spinnerNumberModel, decimalFormat);
            final DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(numberEditorFormatter);
            final JFormattedTextField textField = this.getTextField();
            textField.setEditable(true);
            textField.setFormatterFactory(formatterFactory);
            textField.setHorizontalAlignment(4);
            try {
                textField.setColumns(Math.max(numberEditorFormatter.valueToString(spinnerNumberModel.getMinimum()).length(), numberEditorFormatter.valueToString(spinnerNumberModel.getMaximum()).length()));
            }
            catch (final ParseException ex) {}
        }
        
        public DecimalFormat getFormat() {
            return (DecimalFormat)((NumberFormatter)this.getTextField().getFormatter()).getFormat();
        }
        
        public SpinnerNumberModel getModel() {
            return (SpinnerNumberModel)this.getSpinner().getModel();
        }
        
        @Override
        public void setComponentOrientation(final ComponentOrientation componentOrientation) {
            super.setComponentOrientation(componentOrientation);
            this.getTextField().setHorizontalAlignment(componentOrientation.isLeftToRight() ? 4 : 2);
        }
    }
    
    public static class ListEditor extends DefaultEditor
    {
        public ListEditor(final JSpinner spinner) {
            super(spinner);
            if (!(spinner.getModel() instanceof SpinnerListModel)) {
                throw new IllegalArgumentException("model not a SpinnerListModel");
            }
            this.getTextField().setEditable(true);
            this.getTextField().setFormatterFactory(new DefaultFormatterFactory(new ListFormatter()));
        }
        
        public SpinnerListModel getModel() {
            return (SpinnerListModel)this.getSpinner().getModel();
        }
        
        private class ListFormatter extends JFormattedTextField.AbstractFormatter
        {
            private DocumentFilter filter;
            
            @Override
            public String valueToString(final Object o) throws ParseException {
                if (o == null) {
                    return "";
                }
                return o.toString();
            }
            
            @Override
            public Object stringToValue(final String s) throws ParseException {
                return s;
            }
            
            @Override
            protected DocumentFilter getDocumentFilter() {
                if (this.filter == null) {
                    this.filter = new Filter();
                }
                return this.filter;
            }
            
            private class Filter extends DocumentFilter
            {
                @Override
                public void replace(final FilterBypass filterBypass, final int n, final int n2, final String s, final AttributeSet set) throws BadLocationException {
                    if (s != null && n + n2 == filterBypass.getDocument().getLength()) {
                        final Object nextMatch = ListEditor.this.getModel().findNextMatch(filterBypass.getDocument().getText(0, n) + s);
                        final String s2 = (nextMatch != null) ? nextMatch.toString() : null;
                        if (s2 != null) {
                            filterBypass.remove(0, n + n2);
                            filterBypass.insertString(0, s2, null);
                            ListFormatter.this.getFormattedTextField().select(n + s.length(), s2.length());
                            return;
                        }
                    }
                    super.replace(filterBypass, n, n2, s, set);
                }
                
                @Override
                public void insertString(final FilterBypass filterBypass, final int n, final String s, final AttributeSet set) throws BadLocationException {
                    this.replace(filterBypass, n, 0, s, set);
                }
            }
        }
    }
    
    private static class DisabledAction implements Action
    {
        @Override
        public Object getValue(final String s) {
            return null;
        }
        
        @Override
        public void putValue(final String s, final Object o) {
        }
        
        @Override
        public void setEnabled(final boolean b) {
        }
        
        @Override
        public boolean isEnabled() {
            return false;
        }
        
        @Override
        public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        }
        
        @Override
        public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
        }
    }
    
    protected class AccessibleJSpinner extends AccessibleJComponent implements AccessibleValue, AccessibleAction, AccessibleText, AccessibleEditableText, ChangeListener
    {
        private Object oldModelValue;
        
        protected AccessibleJSpinner() {
            this.oldModelValue = null;
            this.oldModelValue = JSpinner.this.model.getValue();
            JSpinner.this.addChangeListener(this);
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            if (changeEvent == null) {
                throw new NullPointerException();
            }
            final Object value = JSpinner.this.model.getValue();
            this.firePropertyChange("AccessibleValue", this.oldModelValue, value);
            this.firePropertyChange("AccessibleText", null, 0);
            this.oldModelValue = value;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SPIN_BOX;
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            if (JSpinner.this.editor.getAccessibleContext() != null) {
                return 1;
            }
            return 0;
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            if (n != 0) {
                return null;
            }
            if (JSpinner.this.editor.getAccessibleContext() != null) {
                return (Accessible)JSpinner.this.editor;
            }
            return null;
        }
        
        @Override
        public AccessibleAction getAccessibleAction() {
            return this;
        }
        
        @Override
        public AccessibleText getAccessibleText() {
            return this;
        }
        
        private AccessibleContext getEditorAccessibleContext() {
            if (JSpinner.this.editor instanceof DefaultEditor) {
                final JFormattedTextField textField = ((DefaultEditor)JSpinner.this.editor).getTextField();
                if (textField != null) {
                    return textField.getAccessibleContext();
                }
            }
            else if (JSpinner.this.editor instanceof Accessible) {
                return JSpinner.this.editor.getAccessibleContext();
            }
            return null;
        }
        
        private AccessibleText getEditorAccessibleText() {
            final AccessibleContext editorAccessibleContext = this.getEditorAccessibleContext();
            if (editorAccessibleContext != null) {
                return editorAccessibleContext.getAccessibleText();
            }
            return null;
        }
        
        private AccessibleEditableText getEditorAccessibleEditableText() {
            final AccessibleText editorAccessibleText = this.getEditorAccessibleText();
            if (editorAccessibleText instanceof AccessibleEditableText) {
                return (AccessibleEditableText)editorAccessibleText;
            }
            return null;
        }
        
        @Override
        public AccessibleValue getAccessibleValue() {
            return this;
        }
        
        @Override
        public Number getCurrentAccessibleValue() {
            final Object value = JSpinner.this.model.getValue();
            if (value instanceof Number) {
                return (Number)value;
            }
            return null;
        }
        
        @Override
        public boolean setCurrentAccessibleValue(final Number value) {
            try {
                JSpinner.this.model.setValue(value);
                return true;
            }
            catch (final IllegalArgumentException ex) {
                return false;
            }
        }
        
        @Override
        public Number getMinimumAccessibleValue() {
            if (JSpinner.this.model instanceof SpinnerNumberModel) {
                final Comparable minimum = ((SpinnerNumberModel)JSpinner.this.model).getMinimum();
                if (minimum instanceof Number) {
                    return (Number)minimum;
                }
            }
            return null;
        }
        
        @Override
        public Number getMaximumAccessibleValue() {
            if (JSpinner.this.model instanceof SpinnerNumberModel) {
                final Comparable maximum = ((SpinnerNumberModel)JSpinner.this.model).getMaximum();
                if (maximum instanceof Number) {
                    return (Number)maximum;
                }
            }
            return null;
        }
        
        @Override
        public int getAccessibleActionCount() {
            return 2;
        }
        
        @Override
        public String getAccessibleActionDescription(final int n) {
            if (n == 0) {
                return AccessibleAction.INCREMENT;
            }
            if (n == 1) {
                return AccessibleAction.DECREMENT;
            }
            return null;
        }
        
        @Override
        public boolean doAccessibleAction(final int n) {
            if (n < 0 || n > 1) {
                return false;
            }
            Object value;
            if (n == 0) {
                value = JSpinner.this.getNextValue();
            }
            else {
                value = JSpinner.this.getPreviousValue();
            }
            try {
                JSpinner.this.model.setValue(value);
                return true;
            }
            catch (final IllegalArgumentException ex) {
                return false;
            }
        }
        
        private boolean sameWindowAncestor(final Component component, final Component component2) {
            return component != null && component2 != null && SwingUtilities.getWindowAncestor(component) == SwingUtilities.getWindowAncestor(component2);
        }
        
        @Override
        public int getIndexAtPoint(final Point point) {
            final AccessibleText editorAccessibleText = this.getEditorAccessibleText();
            if (editorAccessibleText != null && this.sameWindowAncestor(JSpinner.this, JSpinner.this.editor)) {
                final Point convertPoint = SwingUtilities.convertPoint(JSpinner.this, point, JSpinner.this.editor);
                if (convertPoint != null) {
                    return editorAccessibleText.getIndexAtPoint(convertPoint);
                }
            }
            return -1;
        }
        
        @Override
        public Rectangle getCharacterBounds(final int n) {
            final AccessibleText editorAccessibleText = this.getEditorAccessibleText();
            if (editorAccessibleText != null) {
                final Rectangle characterBounds = editorAccessibleText.getCharacterBounds(n);
                if (characterBounds != null && this.sameWindowAncestor(JSpinner.this, JSpinner.this.editor)) {
                    return SwingUtilities.convertRectangle(JSpinner.this.editor, characterBounds, JSpinner.this);
                }
            }
            return null;
        }
        
        @Override
        public int getCharCount() {
            final AccessibleText editorAccessibleText = this.getEditorAccessibleText();
            if (editorAccessibleText != null) {
                return editorAccessibleText.getCharCount();
            }
            return -1;
        }
        
        @Override
        public int getCaretPosition() {
            final AccessibleText editorAccessibleText = this.getEditorAccessibleText();
            if (editorAccessibleText != null) {
                return editorAccessibleText.getCaretPosition();
            }
            return -1;
        }
        
        @Override
        public String getAtIndex(final int n, final int n2) {
            final AccessibleText editorAccessibleText = this.getEditorAccessibleText();
            if (editorAccessibleText != null) {
                return editorAccessibleText.getAtIndex(n, n2);
            }
            return null;
        }
        
        @Override
        public String getAfterIndex(final int n, final int n2) {
            final AccessibleText editorAccessibleText = this.getEditorAccessibleText();
            if (editorAccessibleText != null) {
                return editorAccessibleText.getAfterIndex(n, n2);
            }
            return null;
        }
        
        @Override
        public String getBeforeIndex(final int n, final int n2) {
            final AccessibleText editorAccessibleText = this.getEditorAccessibleText();
            if (editorAccessibleText != null) {
                return editorAccessibleText.getBeforeIndex(n, n2);
            }
            return null;
        }
        
        @Override
        public AttributeSet getCharacterAttribute(final int n) {
            final AccessibleText editorAccessibleText = this.getEditorAccessibleText();
            if (editorAccessibleText != null) {
                return editorAccessibleText.getCharacterAttribute(n);
            }
            return null;
        }
        
        @Override
        public int getSelectionStart() {
            final AccessibleText editorAccessibleText = this.getEditorAccessibleText();
            if (editorAccessibleText != null) {
                return editorAccessibleText.getSelectionStart();
            }
            return -1;
        }
        
        @Override
        public int getSelectionEnd() {
            final AccessibleText editorAccessibleText = this.getEditorAccessibleText();
            if (editorAccessibleText != null) {
                return editorAccessibleText.getSelectionEnd();
            }
            return -1;
        }
        
        @Override
        public String getSelectedText() {
            final AccessibleText editorAccessibleText = this.getEditorAccessibleText();
            if (editorAccessibleText != null) {
                return editorAccessibleText.getSelectedText();
            }
            return null;
        }
        
        @Override
        public void setTextContents(final String textContents) {
            final AccessibleEditableText editorAccessibleEditableText = this.getEditorAccessibleEditableText();
            if (editorAccessibleEditableText != null) {
                editorAccessibleEditableText.setTextContents(textContents);
            }
        }
        
        @Override
        public void insertTextAtIndex(final int n, final String s) {
            final AccessibleEditableText editorAccessibleEditableText = this.getEditorAccessibleEditableText();
            if (editorAccessibleEditableText != null) {
                editorAccessibleEditableText.insertTextAtIndex(n, s);
            }
        }
        
        @Override
        public String getTextRange(final int n, final int n2) {
            final AccessibleEditableText editorAccessibleEditableText = this.getEditorAccessibleEditableText();
            if (editorAccessibleEditableText != null) {
                return editorAccessibleEditableText.getTextRange(n, n2);
            }
            return null;
        }
        
        @Override
        public void delete(final int n, final int n2) {
            final AccessibleEditableText editorAccessibleEditableText = this.getEditorAccessibleEditableText();
            if (editorAccessibleEditableText != null) {
                editorAccessibleEditableText.delete(n, n2);
            }
        }
        
        @Override
        public void cut(final int n, final int n2) {
            final AccessibleEditableText editorAccessibleEditableText = this.getEditorAccessibleEditableText();
            if (editorAccessibleEditableText != null) {
                editorAccessibleEditableText.cut(n, n2);
            }
        }
        
        @Override
        public void paste(final int n) {
            final AccessibleEditableText editorAccessibleEditableText = this.getEditorAccessibleEditableText();
            if (editorAccessibleEditableText != null) {
                editorAccessibleEditableText.paste(n);
            }
        }
        
        @Override
        public void replaceText(final int n, final int n2, final String s) {
            final AccessibleEditableText editorAccessibleEditableText = this.getEditorAccessibleEditableText();
            if (editorAccessibleEditableText != null) {
                editorAccessibleEditableText.replaceText(n, n2, s);
            }
        }
        
        @Override
        public void selectText(final int n, final int n2) {
            final AccessibleEditableText editorAccessibleEditableText = this.getEditorAccessibleEditableText();
            if (editorAccessibleEditableText != null) {
                editorAccessibleEditableText.selectText(n, n2);
            }
        }
        
        @Override
        public void setAttributes(final int n, final int n2, final AttributeSet set) {
            final AccessibleEditableText editorAccessibleEditableText = this.getEditorAccessibleEditableText();
            if (editorAccessibleEditableText != null) {
                editorAccessibleEditableText.setAttributes(n, n2, set);
            }
        }
    }
}
