package javax.swing;

import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.NavigationFilter;
import javax.swing.text.DocumentFilter;
import java.io.Serializable;
import javax.swing.text.DefaultFormatter;
import java.text.DecimalFormat;
import java.util.Date;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;
import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import javax.swing.plaf.UIResource;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.text.Document;
import javax.swing.text.TextAction;
import java.awt.im.InputContext;
import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import java.text.AttributedCharacterIterator;
import java.awt.event.InputMethodEvent;
import java.awt.Component;
import java.text.ParseException;
import javax.swing.text.DefaultFormatterFactory;
import java.text.Format;
import javax.swing.event.DocumentListener;

public class JFormattedTextField extends JTextField
{
    private static final String uiClassID = "FormattedTextFieldUI";
    private static final Action[] defaultActions;
    public static final int COMMIT = 0;
    public static final int COMMIT_OR_REVERT = 1;
    public static final int REVERT = 2;
    public static final int PERSIST = 3;
    private AbstractFormatterFactory factory;
    private AbstractFormatter format;
    private Object value;
    private boolean editValid;
    private int focusLostBehavior;
    private boolean edited;
    private DocumentListener documentListener;
    private Object mask;
    private ActionMap textFormatterActionMap;
    private boolean composedTextExists;
    private FocusLostHandler focusLostHandler;
    
    public JFormattedTextField() {
        this.composedTextExists = false;
        this.enableEvents(4L);
        this.setFocusLostBehavior(1);
    }
    
    public JFormattedTextField(final Object value) {
        this();
        this.setValue(value);
    }
    
    public JFormattedTextField(final Format format) {
        this();
        this.setFormatterFactory(this.getDefaultFormatterFactory(format));
    }
    
    public JFormattedTextField(final AbstractFormatter abstractFormatter) {
        this(new DefaultFormatterFactory(abstractFormatter));
    }
    
    public JFormattedTextField(final AbstractFormatterFactory formatterFactory) {
        this();
        this.setFormatterFactory(formatterFactory);
    }
    
    public JFormattedTextField(final AbstractFormatterFactory formatterFactory, final Object o) {
        this(o);
        this.setFormatterFactory(formatterFactory);
    }
    
    public void setFocusLostBehavior(final int focusLostBehavior) {
        if (focusLostBehavior != 0 && focusLostBehavior != 1 && focusLostBehavior != 3 && focusLostBehavior != 2) {
            throw new IllegalArgumentException("setFocusLostBehavior must be one of: JFormattedTextField.COMMIT, JFormattedTextField.COMMIT_OR_REVERT, JFormattedTextField.PERSIST or JFormattedTextField.REVERT");
        }
        this.focusLostBehavior = focusLostBehavior;
    }
    
    public int getFocusLostBehavior() {
        return this.focusLostBehavior;
    }
    
    public void setFormatterFactory(final AbstractFormatterFactory factory) {
        this.firePropertyChange("formatterFactory", this.factory, this.factory = factory);
        this.setValue(this.getValue(), true, false);
    }
    
    public AbstractFormatterFactory getFormatterFactory() {
        return this.factory;
    }
    
    protected void setFormatter(final AbstractFormatter format) {
        final AbstractFormatter format2 = this.format;
        if (format2 != null) {
            format2.uninstall();
        }
        this.setEditValid(true);
        if ((this.format = format) != null) {
            format.install(this);
        }
        this.setEdited(false);
        this.firePropertyChange("textFormatter", format2, format);
    }
    
    public AbstractFormatter getFormatter() {
        return this.format;
    }
    
    public void setValue(final Object o) {
        if (o != null && this.getFormatterFactory() == null) {
            this.setFormatterFactory(this.getDefaultFormatterFactory(o));
        }
        this.setValue(o, true, true);
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public void commitEdit() throws ParseException {
        final AbstractFormatter formatter = this.getFormatter();
        if (formatter != null) {
            this.setValue(formatter.stringToValue(this.getText()), false, true);
        }
    }
    
    private void setEditValid(final boolean editValid) {
        if (editValid != this.editValid) {
            this.editValid = editValid;
            this.firePropertyChange("editValid", !editValid, (Object)editValid);
        }
    }
    
    public boolean isEditValid() {
        return this.editValid;
    }
    
    protected void invalidEdit() {
        UIManager.getLookAndFeel().provideErrorFeedback(this);
    }
    
    @Override
    protected void processInputMethodEvent(final InputMethodEvent inputMethodEvent) {
        final AttributedCharacterIterator text = inputMethodEvent.getText();
        final int committedCharacterCount = inputMethodEvent.getCommittedCharacterCount();
        if (text != null) {
            this.composedTextExists = (text.getEndIndex() - text.getBeginIndex() > committedCharacterCount);
        }
        else {
            this.composedTextExists = false;
        }
        super.processInputMethodEvent(inputMethodEvent);
    }
    
    @Override
    protected void processFocusEvent(final FocusEvent focusEvent) {
        super.processFocusEvent(focusEvent);
        if (focusEvent.isTemporary()) {
            return;
        }
        if (this.isEdited() && focusEvent.getID() == 1005) {
            final InputContext inputContext = this.getInputContext();
            if (this.focusLostHandler == null) {
                this.focusLostHandler = new FocusLostHandler();
            }
            if (inputContext != null && this.composedTextExists) {
                inputContext.endComposition();
                EventQueue.invokeLater(this.focusLostHandler);
            }
            else {
                this.focusLostHandler.run();
            }
        }
        else if (!this.isEdited()) {
            this.setValue(this.getValue(), true, true);
        }
    }
    
    @Override
    public Action[] getActions() {
        return TextAction.augmentList(super.getActions(), JFormattedTextField.defaultActions);
    }
    
    @Override
    public String getUIClassID() {
        return "FormattedTextFieldUI";
    }
    
    @Override
    public void setDocument(final Document document) {
        if (this.documentListener != null && this.getDocument() != null) {
            this.getDocument().removeDocumentListener(this.documentListener);
        }
        super.setDocument(document);
        if (this.documentListener == null) {
            this.documentListener = new DocumentHandler();
        }
        document.addDocumentListener(this.documentListener);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("FormattedTextFieldUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    private void setFormatterActions(final Action[] array) {
        if (array == null) {
            if (this.textFormatterActionMap != null) {
                this.textFormatterActionMap.clear();
            }
        }
        else {
            if (this.textFormatterActionMap == null) {
                ActionMap actionMap = this.getActionMap();
                this.textFormatterActionMap = new ActionMap();
                while (actionMap != null) {
                    final ActionMap parent = actionMap.getParent();
                    if (parent instanceof UIResource || parent == null) {
                        actionMap.setParent(this.textFormatterActionMap);
                        this.textFormatterActionMap.setParent(parent);
                        break;
                    }
                    actionMap = parent;
                }
            }
            for (int i = array.length - 1; i >= 0; --i) {
                final Object value = array[i].getValue("Name");
                if (value != null) {
                    this.textFormatterActionMap.put(value, array[i]);
                }
            }
        }
    }
    
    private void setValue(final Object value, final boolean b, final boolean b2) {
        final Object value2 = this.value;
        this.value = value;
        if (b) {
            final AbstractFormatterFactory formatterFactory = this.getFormatterFactory();
            AbstractFormatter formatter;
            if (formatterFactory != null) {
                formatter = formatterFactory.getFormatter(this);
            }
            else {
                formatter = null;
            }
            this.setFormatter(formatter);
        }
        else {
            this.setEditValid(true);
        }
        this.setEdited(false);
        if (b2) {
            this.firePropertyChange("value", value2, value);
        }
    }
    
    private void setEdited(final boolean edited) {
        this.edited = edited;
    }
    
    private boolean isEdited() {
        return this.edited;
    }
    
    private AbstractFormatterFactory getDefaultFormatterFactory(final Object o) {
        if (o instanceof DateFormat) {
            return new DefaultFormatterFactory(new DateFormatter((DateFormat)o));
        }
        if (o instanceof NumberFormat) {
            return new DefaultFormatterFactory(new NumberFormatter((NumberFormat)o));
        }
        if (o instanceof Format) {
            return new DefaultFormatterFactory(new InternationalFormatter((Format)o));
        }
        if (o instanceof Date) {
            return new DefaultFormatterFactory(new DateFormatter());
        }
        if (o instanceof Number) {
            final NumberFormatter numberFormatter = new NumberFormatter();
            numberFormatter.setValueClass(o.getClass());
            final NumberFormatter numberFormatter2 = new NumberFormatter(new DecimalFormat("#.#"));
            numberFormatter2.setValueClass(o.getClass());
            return new DefaultFormatterFactory(numberFormatter, numberFormatter, numberFormatter2);
        }
        return new DefaultFormatterFactory(new DefaultFormatter());
    }
    
    static {
        defaultActions = new Action[] { new CommitAction(), new CancelAction() };
    }
    
    private class FocusLostHandler implements Runnable, Serializable
    {
        @Override
        public void run() {
            final int focusLostBehavior = JFormattedTextField.this.getFocusLostBehavior();
            if (focusLostBehavior != 0) {
                if (focusLostBehavior != 1) {
                    if (focusLostBehavior == 2) {
                        JFormattedTextField.this.setValue(JFormattedTextField.this.getValue(), true, true);
                    }
                    return;
                }
            }
            try {
                JFormattedTextField.this.commitEdit();
                JFormattedTextField.this.setValue(JFormattedTextField.this.getValue(), true, true);
            }
            catch (final ParseException ex) {
                final int n = focusLostBehavior;
                final JFormattedTextField this$0 = JFormattedTextField.this;
                if (n == 1) {
                    JFormattedTextField.this.setValue(JFormattedTextField.this.getValue(), true, true);
                }
            }
        }
    }
    
    public abstract static class AbstractFormatterFactory
    {
        public abstract AbstractFormatter getFormatter(final JFormattedTextField p0);
    }
    
    public abstract static class AbstractFormatter implements Serializable
    {
        private JFormattedTextField ftf;
        
        public void install(final JFormattedTextField ftf) {
            if (this.ftf != null) {
                this.uninstall();
            }
            if ((this.ftf = ftf) != null) {
                try {
                    ftf.setText(this.valueToString(ftf.getValue()));
                }
                catch (final ParseException ex) {
                    ftf.setText("");
                    this.setEditValid(false);
                }
                this.installDocumentFilter(this.getDocumentFilter());
                ftf.setNavigationFilter(this.getNavigationFilter());
                ftf.setFormatterActions(this.getActions());
            }
        }
        
        public void uninstall() {
            if (this.ftf != null) {
                this.installDocumentFilter(null);
                this.ftf.setNavigationFilter(null);
                this.ftf.setFormatterActions(null);
            }
        }
        
        public abstract Object stringToValue(final String p0) throws ParseException;
        
        public abstract String valueToString(final Object p0) throws ParseException;
        
        protected JFormattedTextField getFormattedTextField() {
            return this.ftf;
        }
        
        protected void invalidEdit() {
            final JFormattedTextField formattedTextField = this.getFormattedTextField();
            if (formattedTextField != null) {
                formattedTextField.invalidEdit();
            }
        }
        
        protected void setEditValid(final boolean b) {
            final JFormattedTextField formattedTextField = this.getFormattedTextField();
            if (formattedTextField != null) {
                formattedTextField.setEditValid(b);
            }
        }
        
        protected Action[] getActions() {
            return null;
        }
        
        protected DocumentFilter getDocumentFilter() {
            return null;
        }
        
        protected NavigationFilter getNavigationFilter() {
            return null;
        }
        
        @Override
        protected Object clone() throws CloneNotSupportedException {
            final AbstractFormatter abstractFormatter = (AbstractFormatter)super.clone();
            abstractFormatter.ftf = null;
            return abstractFormatter;
        }
        
        private void installDocumentFilter(final DocumentFilter documentFilter) {
            final JFormattedTextField formattedTextField = this.getFormattedTextField();
            if (formattedTextField != null) {
                final Document document = formattedTextField.getDocument();
                if (document instanceof AbstractDocument) {
                    ((AbstractDocument)document).setDocumentFilter(documentFilter);
                }
                document.putProperty(DocumentFilter.class, null);
            }
        }
    }
    
    static class CommitAction extends NotifyAction
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent focusedComponent = this.getFocusedComponent();
            if (focusedComponent instanceof JFormattedTextField) {
                try {
                    ((JFormattedTextField)focusedComponent).commitEdit();
                }
                catch (final ParseException ex) {
                    ((JFormattedTextField)focusedComponent).invalidEdit();
                    return;
                }
            }
            super.actionPerformed(actionEvent);
        }
        
        @Override
        public boolean isEnabled() {
            final JTextComponent focusedComponent = this.getFocusedComponent();
            if (focusedComponent instanceof JFormattedTextField) {
                return ((JFormattedTextField)focusedComponent).isEdited();
            }
            return super.isEnabled();
        }
    }
    
    private static class CancelAction extends TextAction
    {
        public CancelAction() {
            super("reset-field-edit");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent focusedComponent = this.getFocusedComponent();
            if (focusedComponent instanceof JFormattedTextField) {
                final JFormattedTextField formattedTextField = (JFormattedTextField)focusedComponent;
                formattedTextField.setValue(formattedTextField.getValue());
            }
        }
        
        @Override
        public boolean isEnabled() {
            final JTextComponent focusedComponent = this.getFocusedComponent();
            if (focusedComponent instanceof JFormattedTextField) {
                return ((JFormattedTextField)focusedComponent).isEdited();
            }
            return super.isEnabled();
        }
    }
    
    private class DocumentHandler implements DocumentListener, Serializable
    {
        @Override
        public void insertUpdate(final DocumentEvent documentEvent) {
            JFormattedTextField.this.setEdited(true);
        }
        
        @Override
        public void removeUpdate(final DocumentEvent documentEvent) {
            JFormattedTextField.this.setEdited(true);
        }
        
        @Override
        public void changedUpdate(final DocumentEvent documentEvent) {
        }
    }
}
