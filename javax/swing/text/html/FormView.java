package javax.swing.text.html;

import java.io.File;
import javax.swing.JFileChooser;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.BitSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.ElementIterator;
import java.net.URLEncoder;
import java.awt.Point;
import javax.swing.SwingUtilities;
import java.io.IOException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.JEditorPane;
import java.awt.event.ActionEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataListener;
import java.awt.event.ItemListener;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;
import javax.swing.AbstractListModel;
import javax.swing.DefaultButtonModel;
import javax.accessibility.Accessible;
import javax.swing.Box;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.JCheckBox;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.net.URL;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.JComponent;
import javax.swing.text.AttributeSet;
import javax.swing.JTextArea;
import javax.swing.text.Document;
import javax.swing.JComboBox;
import javax.swing.ComboBoxModel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.text.StyleConstants;
import java.awt.Component;
import javax.swing.text.Element;
import java.awt.event.ActionListener;
import javax.swing.text.ComponentView;

public class FormView extends ComponentView implements ActionListener
{
    @Deprecated
    public static final String SUBMIT;
    @Deprecated
    public static final String RESET;
    static final String PostDataProperty = "javax.swing.JEditorPane.postdata";
    private short maxIsPreferred;
    
    public FormView(final Element element) {
        super(element);
    }
    
    @Override
    protected Component createComponent() {
        final AttributeSet attributes = this.getElement().getAttributes();
        final HTML.Tag tag = (HTML.Tag)attributes.getAttribute(StyleConstants.NameAttribute);
        JComponent inputComponent = null;
        final Object attribute = attributes.getAttribute(StyleConstants.ModelAttribute);
        this.removeStaleListenerForModel(attribute);
        if (tag == HTML.Tag.INPUT) {
            inputComponent = this.createInputComponent(attributes, attribute);
        }
        else if (tag == HTML.Tag.SELECT) {
            if (attribute instanceof OptionListModel) {
                final JList list = new JList((ListModel<Object>)attribute);
                list.setVisibleRowCount(HTML.getIntegerAttributeValue(attributes, HTML.Attribute.SIZE, 1));
                list.setSelectionModel((ListSelectionModel)attribute);
                inputComponent = new JScrollPane(list);
            }
            else {
                inputComponent = new JComboBox<Object>((ComboBoxModel<Object>)attribute);
                this.maxIsPreferred = 3;
            }
        }
        else if (tag == HTML.Tag.TEXTAREA) {
            final JTextArea textArea = new JTextArea((Document)attribute);
            textArea.setRows(HTML.getIntegerAttributeValue(attributes, HTML.Attribute.ROWS, 1));
            final int integerAttributeValue = HTML.getIntegerAttributeValue(attributes, HTML.Attribute.COLS, 20);
            this.maxIsPreferred = 3;
            textArea.setColumns(integerAttributeValue);
            inputComponent = new JScrollPane(textArea, 22, 32);
        }
        if (inputComponent != null) {
            inputComponent.setAlignmentY(1.0f);
        }
        return inputComponent;
    }
    
    private JComponent createInputComponent(final AttributeSet set, final Object o) {
        Accessible accessible = null;
        final String s = (String)set.getAttribute(HTML.Attribute.TYPE);
        if (s.equals("submit") || s.equals("reset")) {
            String s2 = (String)set.getAttribute(HTML.Attribute.VALUE);
            if (s2 == null) {
                if (s.equals("submit")) {
                    s2 = UIManager.getString("FormView.submitButtonText");
                }
                else {
                    s2 = UIManager.getString("FormView.resetButtonText");
                }
            }
            final JButton button = new JButton(s2);
            if (o != null) {
                button.setModel((ButtonModel)o);
                button.addActionListener(this);
            }
            accessible = button;
            this.maxIsPreferred = 3;
        }
        else if (s.equals("image")) {
            final String s3 = (String)set.getAttribute(HTML.Attribute.SRC);
            JButton button2;
            try {
                button2 = new JButton(new ImageIcon(new URL(((HTMLDocument)this.getElement().getDocument()).getBase(), s3)));
            }
            catch (final MalformedURLException ex) {
                button2 = new JButton(s3);
            }
            if (o != null) {
                button2.setModel((ButtonModel)o);
                button2.addMouseListener(new MouseEventListener());
            }
            accessible = button2;
            this.maxIsPreferred = 3;
        }
        else if (s.equals("checkbox")) {
            accessible = new JCheckBox();
            if (o != null) {
                ((JCheckBox)accessible).setModel((ButtonModel)o);
            }
            this.maxIsPreferred = 3;
        }
        else if (s.equals("radio")) {
            accessible = new JRadioButton();
            if (o != null) {
                ((JRadioButton)accessible).setModel((ButtonModel)o);
            }
            this.maxIsPreferred = 3;
        }
        else if (s.equals("text")) {
            final int integerAttributeValue = HTML.getIntegerAttributeValue(set, HTML.Attribute.SIZE, -1);
            JTextField textField;
            if (integerAttributeValue > 0) {
                textField = new JTextField();
                textField.setColumns(integerAttributeValue);
            }
            else {
                textField = new JTextField();
                textField.setColumns(20);
            }
            accessible = textField;
            if (o != null) {
                textField.setDocument((Document)o);
            }
            textField.addActionListener(this);
            this.maxIsPreferred = 3;
        }
        else if (s.equals("password")) {
            final JPasswordField passwordField = (JPasswordField)(accessible = new JPasswordField());
            if (o != null) {
                passwordField.setDocument((Document)o);
            }
            final int integerAttributeValue2 = HTML.getIntegerAttributeValue(set, HTML.Attribute.SIZE, -1);
            passwordField.setColumns((integerAttributeValue2 > 0) ? integerAttributeValue2 : 20);
            passwordField.addActionListener(this);
            this.maxIsPreferred = 3;
        }
        else if (s.equals("file")) {
            final JTextField textField2 = new JTextField();
            if (o != null) {
                textField2.setDocument((Document)o);
            }
            final int integerAttributeValue3 = HTML.getIntegerAttributeValue(set, HTML.Attribute.SIZE, -1);
            textField2.setColumns((integerAttributeValue3 > 0) ? integerAttributeValue3 : 20);
            final JButton button3 = new JButton(UIManager.getString("FormView.browseFileButtonText"));
            final Box horizontalBox = Box.createHorizontalBox();
            horizontalBox.add(textField2);
            horizontalBox.add(Box.createHorizontalStrut(5));
            horizontalBox.add(button3);
            button3.addActionListener(new BrowseFileAction(set, (Document)o));
            accessible = horizontalBox;
            this.maxIsPreferred = 3;
        }
        return (JComponent)accessible;
    }
    
    private void removeStaleListenerForModel(final Object o) {
        if (o instanceof DefaultButtonModel) {
            final DefaultButtonModel defaultButtonModel = (DefaultButtonModel)o;
            final String s = "javax.swing.AbstractButton$Handler";
            for (final ActionListener actionListener : defaultButtonModel.getActionListeners()) {
                if (s.equals(actionListener.getClass().getName())) {
                    defaultButtonModel.removeActionListener(actionListener);
                }
            }
            for (final ChangeListener changeListener : defaultButtonModel.getChangeListeners()) {
                if (s.equals(changeListener.getClass().getName())) {
                    defaultButtonModel.removeChangeListener(changeListener);
                }
            }
            for (final ItemListener itemListener : defaultButtonModel.getItemListeners()) {
                if (s.equals(itemListener.getClass().getName())) {
                    defaultButtonModel.removeItemListener(itemListener);
                }
            }
        }
        else if (o instanceof AbstractListModel) {
            final AbstractListModel abstractListModel = (AbstractListModel)o;
            final String s2 = "javax.swing.plaf.basic.BasicListUI$Handler";
            final String s3 = "javax.swing.plaf.basic.BasicComboBoxUI$Handler";
            for (final ListDataListener listDataListener : abstractListModel.getListDataListeners()) {
                if (s2.equals(listDataListener.getClass().getName()) || s3.equals(listDataListener.getClass().getName())) {
                    abstractListModel.removeListDataListener(listDataListener);
                }
            }
        }
        else if (o instanceof AbstractDocument) {
            final String s4 = "javax.swing.plaf.basic.BasicTextUI$UpdateHandler";
            final String s5 = "javax.swing.text.DefaultCaret$Handler";
            final AbstractDocument abstractDocument = (AbstractDocument)o;
            for (final DocumentListener documentListener : abstractDocument.getDocumentListeners()) {
                if (s4.equals(documentListener.getClass().getName()) || s5.equals(documentListener.getClass().getName())) {
                    abstractDocument.removeDocumentListener(documentListener);
                }
            }
        }
    }
    
    @Override
    public float getMaximumSpan(final int n) {
        switch (n) {
            case 0: {
                if ((this.maxIsPreferred & 0x1) == 0x1) {
                    super.getMaximumSpan(n);
                    return this.getPreferredSpan(n);
                }
                return super.getMaximumSpan(n);
            }
            case 1: {
                if ((this.maxIsPreferred & 0x2) == 0x2) {
                    super.getMaximumSpan(n);
                    return this.getPreferredSpan(n);
                }
                return super.getMaximumSpan(n);
            }
            default: {
                return super.getMaximumSpan(n);
            }
        }
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        final Element element = this.getElement();
        final StringBuilder sb = new StringBuilder();
        final HTMLDocument htmlDocument = (HTMLDocument)this.getDocument();
        final String s = (String)element.getAttributes().getAttribute(HTML.Attribute.TYPE);
        if (s.equals("submit")) {
            this.getFormData(sb);
            this.submitData(sb.toString());
        }
        else if (s.equals("reset")) {
            this.resetForm();
        }
        else if (s.equals("text") || s.equals("password")) {
            if (this.isLastTextOrPasswordField()) {
                this.getFormData(sb);
                this.submitData(sb.toString());
            }
            else {
                this.getComponent().transferFocus();
            }
        }
    }
    
    protected void submitData(final String s) {
        final Element formElement = this.getFormElement();
        final AttributeSet attributes = formElement.getAttributes();
        final HTMLDocument htmlDocument = (HTMLDocument)formElement.getDocument();
        final URL base = htmlDocument.getBase();
        String s2 = (String)attributes.getAttribute(HTML.Attribute.TARGET);
        if (s2 == null) {
            s2 = "_self";
        }
        String s3 = (String)attributes.getAttribute(HTML.Attribute.METHOD);
        if (s3 == null) {
            s3 = "GET";
        }
        final boolean equals = s3.toLowerCase().equals("post");
        if (equals) {
            this.storePostData(htmlDocument, s2, s);
        }
        final String s4 = (String)attributes.getAttribute(HTML.Attribute.ACTION);
        URL url;
        try {
            url = ((s4 == null) ? new URL(base.getProtocol(), base.getHost(), base.getPort(), base.getFile()) : new URL(base, s4));
            if (!equals) {
                url = new URL(url + "?" + s.toString());
            }
        }
        catch (final MalformedURLException ex) {
            url = null;
        }
        final JEditorPane editorPane = (JEditorPane)this.getContainer();
        final HTMLEditorKit htmlEditorKit = (HTMLEditorKit)editorPane.getEditorKit();
        FormSubmitEvent formSubmitEvent = null;
        if (!htmlEditorKit.isAutoFormSubmission() || htmlDocument.isFrameDocument()) {
            formSubmitEvent = new FormSubmitEvent(this, HyperlinkEvent.EventType.ACTIVATED, url, formElement, s2, equals ? FormSubmitEvent.MethodType.POST : FormSubmitEvent.MethodType.GET, s);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (formSubmitEvent != null) {
                    editorPane.fireHyperlinkUpdate(formSubmitEvent);
                }
                else {
                    try {
                        editorPane.setPage(url);
                    }
                    catch (final IOException ex) {
                        UIManager.getLookAndFeel().provideErrorFeedback(editorPane);
                    }
                }
            }
        });
    }
    
    private void storePostData(final HTMLDocument htmlDocument, final String s, final String s2) {
        Document document = htmlDocument;
        String string = "javax.swing.JEditorPane.postdata";
        if (htmlDocument.isFrameDocument()) {
            final JEditorPane outermostJEditorPane = ((FrameView.FrameEditorPane)this.getContainer()).getFrameView().getOutermostJEditorPane();
            if (outermostJEditorPane != null) {
                document = outermostJEditorPane.getDocument();
                string = string + "." + s;
            }
        }
        document.putProperty(string, s2);
    }
    
    protected void imageSubmit(final String s) {
        final StringBuilder sb = new StringBuilder();
        final HTMLDocument htmlDocument = (HTMLDocument)this.getElement().getDocument();
        this.getFormData(sb);
        if (sb.length() > 0) {
            sb.append('&');
        }
        sb.append(s);
        this.submitData(sb.toString());
    }
    
    private String getImageData(final Point point) {
        final String string = point.x + ":" + point.y;
        int index = string.indexOf(58);
        final String substring = string.substring(0, index);
        final String substring2 = string.substring(++index);
        final String s = (String)this.getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
        String s2;
        if (s == null || s.equals("")) {
            s2 = "x=" + substring + "&y=" + substring2;
        }
        else {
            final String encode = URLEncoder.encode(s);
            s2 = encode + ".x=" + substring + "&" + encode + ".y=" + substring2;
        }
        return s2;
    }
    
    private Element getFormElement() {
        for (Element element = this.getElement(); element != null; element = element.getParentElement()) {
            if (element.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.FORM) {
                return element;
            }
        }
        return null;
    }
    
    private void getFormData(final StringBuilder sb) {
        final Element formElement = this.getFormElement();
        if (formElement != null) {
            Element next;
            while ((next = new ElementIterator(formElement).next()) != null) {
                if (this.isControl(next)) {
                    final String s = (String)next.getAttributes().getAttribute(HTML.Attribute.TYPE);
                    if (s != null && s.equals("submit") && next != this.getElement()) {
                        continue;
                    }
                    if (s != null && s.equals("image")) {
                        continue;
                    }
                    this.loadElementDataIntoBuffer(next, sb);
                }
            }
        }
    }
    
    private void loadElementDataIntoBuffer(final Element element, final StringBuilder sb) {
        final AttributeSet attributes = element.getAttributes();
        final String s = (String)attributes.getAttribute(HTML.Attribute.NAME);
        if (s == null) {
            return;
        }
        String s2 = null;
        final HTML.Tag tag = (HTML.Tag)element.getAttributes().getAttribute(StyleConstants.NameAttribute);
        if (tag == HTML.Tag.INPUT) {
            s2 = this.getInputElementData(attributes);
        }
        else if (tag == HTML.Tag.TEXTAREA) {
            s2 = this.getTextAreaData(attributes);
        }
        else if (tag == HTML.Tag.SELECT) {
            this.loadSelectData(attributes, sb);
        }
        if (s != null && s2 != null) {
            this.appendBuffer(sb, s, s2);
        }
    }
    
    private String getInputElementData(final AttributeSet set) {
        final Object attribute = set.getAttribute(StyleConstants.ModelAttribute);
        final String s = (String)set.getAttribute(HTML.Attribute.TYPE);
        String text = null;
        if (s.equals("text") || s.equals("password")) {
            final Document document = (Document)attribute;
            try {
                text = document.getText(0, document.getLength());
            }
            catch (final BadLocationException ex) {
                text = null;
            }
        }
        else if (s.equals("submit") || s.equals("hidden")) {
            text = (String)set.getAttribute(HTML.Attribute.VALUE);
            if (text == null) {
                text = "";
            }
        }
        else if (s.equals("radio") || s.equals("checkbox")) {
            if (((ButtonModel)attribute).isSelected()) {
                text = (String)set.getAttribute(HTML.Attribute.VALUE);
                if (text == null) {
                    text = "on";
                }
            }
        }
        else if (s.equals("file")) {
            final Document document2 = (Document)attribute;
            String text2;
            try {
                text2 = document2.getText(0, document2.getLength());
            }
            catch (final BadLocationException ex2) {
                text2 = null;
            }
            if (text2 != null && text2.length() > 0) {
                text = text2;
            }
        }
        return text;
    }
    
    private String getTextAreaData(final AttributeSet set) {
        final Document document = (Document)set.getAttribute(StyleConstants.ModelAttribute);
        try {
            return document.getText(0, document.getLength());
        }
        catch (final BadLocationException ex) {
            return null;
        }
    }
    
    private void loadSelectData(final AttributeSet set, final StringBuilder sb) {
        final String s = (String)set.getAttribute(HTML.Attribute.NAME);
        if (s == null) {
            return;
        }
        final Object attribute = set.getAttribute(StyleConstants.ModelAttribute);
        if (attribute instanceof OptionListModel) {
            final OptionListModel optionListModel = (OptionListModel)attribute;
            for (int i = 0; i < optionListModel.getSize(); ++i) {
                if (optionListModel.isSelectedIndex(i)) {
                    this.appendBuffer(sb, s, ((Option)optionListModel.getElementAt(i)).getValue());
                }
            }
        }
        else if (attribute instanceof ComboBoxModel) {
            final Option option = (Option)((ComboBoxModel)attribute).getSelectedItem();
            if (option != null) {
                this.appendBuffer(sb, s, option.getValue());
            }
        }
    }
    
    private void appendBuffer(final StringBuilder sb, final String s, final String s2) {
        if (sb.length() > 0) {
            sb.append('&');
        }
        sb.append(URLEncoder.encode(s));
        sb.append('=');
        sb.append(URLEncoder.encode(s2));
    }
    
    private boolean isControl(final Element element) {
        return element.isLeaf();
    }
    
    boolean isLastTextOrPasswordField() {
        final Element formElement = this.getFormElement();
        final Element element = this.getElement();
        if (formElement != null) {
            final ElementIterator elementIterator = new ElementIterator(formElement);
            boolean b = false;
            Element next;
            while ((next = elementIterator.next()) != null) {
                if (next == element) {
                    b = true;
                }
                else {
                    if (!b || !this.isControl(next)) {
                        continue;
                    }
                    final AttributeSet attributes = next.getAttributes();
                    if (!HTMLDocument.matchNameAttribute(attributes, HTML.Tag.INPUT)) {
                        continue;
                    }
                    final String s = (String)attributes.getAttribute(HTML.Attribute.TYPE);
                    if ("text".equals(s) || "password".equals(s)) {
                        return false;
                    }
                    continue;
                }
            }
        }
        return true;
    }
    
    void resetForm() {
        final Element formElement = this.getFormElement();
        if (formElement != null) {
            Element next;
            while ((next = new ElementIterator(formElement).next()) != null) {
                if (this.isControl(next)) {
                    final AttributeSet attributes = next.getAttributes();
                    final Object attribute = attributes.getAttribute(StyleConstants.ModelAttribute);
                    if (attribute instanceof TextAreaDocument) {
                        ((TextAreaDocument)attribute).reset();
                    }
                    else if (attribute instanceof PlainDocument) {
                        try {
                            final PlainDocument plainDocument = (PlainDocument)attribute;
                            plainDocument.remove(0, plainDocument.getLength());
                            if (!HTMLDocument.matchNameAttribute(attributes, HTML.Tag.INPUT)) {
                                continue;
                            }
                            final String s = (String)attributes.getAttribute(HTML.Attribute.VALUE);
                            if (s == null) {
                                continue;
                            }
                            plainDocument.insertString(0, s, null);
                        }
                        catch (final BadLocationException ex) {}
                    }
                    else if (attribute instanceof OptionListModel) {
                        final OptionListModel optionListModel = (OptionListModel)attribute;
                        for (int size = optionListModel.getSize(), i = 0; i < size; ++i) {
                            optionListModel.removeIndexInterval(i, i);
                        }
                        final BitSet initialSelection = optionListModel.getInitialSelection();
                        for (int j = 0; j < initialSelection.size(); ++j) {
                            if (initialSelection.get(j)) {
                                optionListModel.addSelectionInterval(j, j);
                            }
                        }
                    }
                    else if (attribute instanceof OptionComboBoxModel) {
                        final OptionComboBoxModel optionComboBoxModel = (OptionComboBoxModel)attribute;
                        final Option initialSelection2 = optionComboBoxModel.getInitialSelection();
                        if (initialSelection2 == null) {
                            continue;
                        }
                        optionComboBoxModel.setSelectedItem(initialSelection2);
                    }
                    else {
                        if (!(attribute instanceof JToggleButton.ToggleButtonModel)) {
                            continue;
                        }
                        ((JToggleButton.ToggleButtonModel)attribute).setSelected(attributes.getAttribute(HTML.Attribute.CHECKED) != null);
                    }
                }
            }
        }
    }
    
    static {
        SUBMIT = new String("Submit Query");
        RESET = new String("Reset");
    }
    
    protected class MouseEventListener extends MouseAdapter
    {
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            FormView.this.imageSubmit(FormView.this.getImageData(mouseEvent.getPoint()));
        }
    }
    
    private class BrowseFileAction implements ActionListener
    {
        private AttributeSet attrs;
        private Document model;
        
        BrowseFileAction(final AttributeSet attrs, final Document model) {
            this.attrs = attrs;
            this.model = model;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(false);
            if (fileChooser.showOpenDialog(FormView.this.getContainer()) == 0) {
                final File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile != null) {
                    try {
                        if (this.model.getLength() > 0) {
                            this.model.remove(0, this.model.getLength());
                        }
                        this.model.insertString(0, selectedFile.getPath(), null);
                    }
                    catch (final BadLocationException ex) {}
                }
            }
        }
    }
}
