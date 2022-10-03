package javax.swing.text;

import java.awt.Color;
import java.awt.Component;
import javax.swing.UIManager;
import java.awt.event.ActionEvent;
import javax.swing.event.CaretEvent;
import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import java.beans.PropertyChangeListener;
import javax.swing.event.CaretListener;
import javax.swing.JEditorPane;
import javax.swing.Action;

public class StyledEditorKit extends DefaultEditorKit
{
    private static final ViewFactory defaultFactory;
    Element currentRun;
    Element currentParagraph;
    MutableAttributeSet inputAttributes;
    private AttributeTracker inputAttributeUpdater;
    private static final Action[] defaultActions;
    
    public StyledEditorKit() {
        this.createInputAttributeUpdated();
        this.createInputAttributes();
    }
    
    public MutableAttributeSet getInputAttributes() {
        return this.inputAttributes;
    }
    
    public Element getCharacterAttributeRun() {
        return this.currentRun;
    }
    
    @Override
    public Action[] getActions() {
        return TextAction.augmentList(super.getActions(), StyledEditorKit.defaultActions);
    }
    
    @Override
    public Document createDefaultDocument() {
        return new DefaultStyledDocument();
    }
    
    @Override
    public void install(final JEditorPane editorPane) {
        editorPane.addCaretListener(this.inputAttributeUpdater);
        editorPane.addPropertyChangeListener(this.inputAttributeUpdater);
        final Caret caret = editorPane.getCaret();
        if (caret != null) {
            this.inputAttributeUpdater.updateInputAttributes(caret.getDot(), caret.getMark(), editorPane);
        }
    }
    
    @Override
    public void deinstall(final JEditorPane editorPane) {
        editorPane.removeCaretListener(this.inputAttributeUpdater);
        editorPane.removePropertyChangeListener(this.inputAttributeUpdater);
        this.currentRun = null;
        this.currentParagraph = null;
    }
    
    @Override
    public ViewFactory getViewFactory() {
        return StyledEditorKit.defaultFactory;
    }
    
    @Override
    public Object clone() {
        final StyledEditorKit styledEditorKit3;
        final StyledEditorKit styledEditorKit2;
        final StyledEditorKit styledEditorKit = styledEditorKit2 = (styledEditorKit3 = (StyledEditorKit)super.clone());
        final Element element = null;
        styledEditorKit2.currentParagraph = element;
        styledEditorKit3.currentRun = element;
        styledEditorKit.createInputAttributeUpdated();
        styledEditorKit.createInputAttributes();
        return styledEditorKit;
    }
    
    private void createInputAttributes() {
        this.inputAttributes = new SimpleAttributeSet() {
            @Override
            public AttributeSet getResolveParent() {
                return (StyledEditorKit.this.currentParagraph != null) ? StyledEditorKit.this.currentParagraph.getAttributes() : null;
            }
            
            @Override
            public Object clone() {
                return new SimpleAttributeSet(this);
            }
        };
    }
    
    private void createInputAttributeUpdated() {
        this.inputAttributeUpdater = new AttributeTracker();
    }
    
    protected void createInputAttributes(final Element element, final MutableAttributeSet set) {
        if (element.getAttributes().getAttributeCount() > 0 || element.getEndOffset() - element.getStartOffset() > 1 || element.getEndOffset() < element.getDocument().getLength()) {
            set.removeAttributes(set);
            set.addAttributes(element.getAttributes());
            set.removeAttribute(StyleConstants.ComponentAttribute);
            set.removeAttribute(StyleConstants.IconAttribute);
            set.removeAttribute("$ename");
            set.removeAttribute(StyleConstants.ComposedTextAttribute);
        }
    }
    
    static {
        defaultFactory = new StyledViewFactory();
        defaultActions = new Action[] { new FontFamilyAction("font-family-SansSerif", "SansSerif"), new FontFamilyAction("font-family-Monospaced", "Monospaced"), new FontFamilyAction("font-family-Serif", "Serif"), new FontSizeAction("font-size-8", 8), new FontSizeAction("font-size-10", 10), new FontSizeAction("font-size-12", 12), new FontSizeAction("font-size-14", 14), new FontSizeAction("font-size-16", 16), new FontSizeAction("font-size-18", 18), new FontSizeAction("font-size-24", 24), new FontSizeAction("font-size-36", 36), new FontSizeAction("font-size-48", 48), new AlignmentAction("left-justify", 0), new AlignmentAction("center-justify", 1), new AlignmentAction("right-justify", 2), new BoldAction(), new ItalicAction(), new StyledInsertBreakAction(), new UnderlineAction() };
    }
    
    class AttributeTracker implements CaretListener, PropertyChangeListener, Serializable
    {
        void updateInputAttributes(final int n, final int n2, final JTextComponent textComponent) {
            final Document document = textComponent.getDocument();
            if (!(document instanceof StyledDocument)) {
                return;
            }
            final int min = Math.min(n, n2);
            final StyledDocument styledDocument = (StyledDocument)document;
            StyledEditorKit.this.currentParagraph = styledDocument.getParagraphElement(min);
            Element currentRun;
            if (StyledEditorKit.this.currentParagraph.getStartOffset() == min || n != n2) {
                currentRun = styledDocument.getCharacterElement(min);
            }
            else {
                currentRun = styledDocument.getCharacterElement(Math.max(min - 1, 0));
            }
            if (currentRun != StyledEditorKit.this.currentRun) {
                StyledEditorKit.this.currentRun = currentRun;
                StyledEditorKit.this.createInputAttributes(StyledEditorKit.this.currentRun, StyledEditorKit.this.getInputAttributes());
            }
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final Object newValue = propertyChangeEvent.getNewValue();
            final Object source = propertyChangeEvent.getSource();
            if (source instanceof JTextComponent && newValue instanceof Document) {
                this.updateInputAttributes(0, 0, (JTextComponent)source);
            }
        }
        
        @Override
        public void caretUpdate(final CaretEvent caretEvent) {
            this.updateInputAttributes(caretEvent.getDot(), caretEvent.getMark(), (JTextComponent)caretEvent.getSource());
        }
    }
    
    static class StyledViewFactory implements ViewFactory
    {
        @Override
        public View create(final Element element) {
            final String name = element.getName();
            if (name != null) {
                if (name.equals("content")) {
                    return new LabelView(element);
                }
                if (name.equals("paragraph")) {
                    return new ParagraphView(element);
                }
                if (name.equals("section")) {
                    return new BoxView(element, 1);
                }
                if (name.equals("component")) {
                    return new ComponentView(element);
                }
                if (name.equals("icon")) {
                    return new IconView(element);
                }
            }
            return new LabelView(element);
        }
    }
    
    public abstract static class StyledTextAction extends TextAction
    {
        public StyledTextAction(final String s) {
            super(s);
        }
        
        protected final JEditorPane getEditor(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent instanceof JEditorPane) {
                return (JEditorPane)textComponent;
            }
            return null;
        }
        
        protected final StyledDocument getStyledDocument(final JEditorPane editorPane) {
            final Document document = editorPane.getDocument();
            if (document instanceof StyledDocument) {
                return (StyledDocument)document;
            }
            throw new IllegalArgumentException("document must be StyledDocument");
        }
        
        protected final StyledEditorKit getStyledEditorKit(final JEditorPane editorPane) {
            final EditorKit editorKit = editorPane.getEditorKit();
            if (editorKit instanceof StyledEditorKit) {
                return (StyledEditorKit)editorKit;
            }
            throw new IllegalArgumentException("EditorKit must be StyledEditorKit");
        }
        
        protected final void setCharacterAttributes(final JEditorPane editorPane, final AttributeSet set, final boolean b) {
            final int selectionStart = editorPane.getSelectionStart();
            final int selectionEnd = editorPane.getSelectionEnd();
            if (selectionStart != selectionEnd) {
                this.getStyledDocument(editorPane).setCharacterAttributes(selectionStart, selectionEnd - selectionStart, set, b);
            }
            final MutableAttributeSet inputAttributes = this.getStyledEditorKit(editorPane).getInputAttributes();
            if (b) {
                inputAttributes.removeAttributes(inputAttributes);
            }
            inputAttributes.addAttributes(set);
        }
        
        protected final void setParagraphAttributes(final JEditorPane editorPane, final AttributeSet set, final boolean b) {
            final int selectionStart = editorPane.getSelectionStart();
            this.getStyledDocument(editorPane).setParagraphAttributes(selectionStart, editorPane.getSelectionEnd() - selectionStart, set, b);
        }
    }
    
    public static class FontFamilyAction extends StyledTextAction
    {
        private String family;
        
        public FontFamilyAction(final String s, final String family) {
            super(s);
            this.family = family;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JEditorPane editor = this.getEditor(actionEvent);
            if (editor != null) {
                String family = this.family;
                if (actionEvent != null && actionEvent.getSource() == editor) {
                    final String actionCommand = actionEvent.getActionCommand();
                    if (actionCommand != null) {
                        family = actionCommand;
                    }
                }
                if (family != null) {
                    final SimpleAttributeSet set = new SimpleAttributeSet();
                    StyleConstants.setFontFamily(set, family);
                    this.setCharacterAttributes(editor, set, false);
                }
                else {
                    UIManager.getLookAndFeel().provideErrorFeedback(editor);
                }
            }
        }
    }
    
    public static class FontSizeAction extends StyledTextAction
    {
        private int size;
        
        public FontSizeAction(final String s, final int size) {
            super(s);
            this.size = size;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JEditorPane editor = this.getEditor(actionEvent);
            if (editor != null) {
                int n = this.size;
                if (actionEvent != null && actionEvent.getSource() == editor) {
                    final String actionCommand = actionEvent.getActionCommand();
                    try {
                        n = Integer.parseInt(actionCommand, 10);
                    }
                    catch (final NumberFormatException ex) {}
                }
                if (n != 0) {
                    final SimpleAttributeSet set = new SimpleAttributeSet();
                    StyleConstants.setFontSize(set, n);
                    this.setCharacterAttributes(editor, set, false);
                }
                else {
                    UIManager.getLookAndFeel().provideErrorFeedback(editor);
                }
            }
        }
    }
    
    public static class ForegroundAction extends StyledTextAction
    {
        private Color fg;
        
        public ForegroundAction(final String s, final Color fg) {
            super(s);
            this.fg = fg;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JEditorPane editor = this.getEditor(actionEvent);
            if (editor != null) {
                Color color = this.fg;
                if (actionEvent != null && actionEvent.getSource() == editor) {
                    final String actionCommand = actionEvent.getActionCommand();
                    try {
                        color = Color.decode(actionCommand);
                    }
                    catch (final NumberFormatException ex) {}
                }
                if (color != null) {
                    final SimpleAttributeSet set = new SimpleAttributeSet();
                    StyleConstants.setForeground(set, color);
                    this.setCharacterAttributes(editor, set, false);
                }
                else {
                    UIManager.getLookAndFeel().provideErrorFeedback(editor);
                }
            }
        }
    }
    
    public static class AlignmentAction extends StyledTextAction
    {
        private int a;
        
        public AlignmentAction(final String s, final int a) {
            super(s);
            this.a = a;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JEditorPane editor = this.getEditor(actionEvent);
            if (editor != null) {
                int n = this.a;
                if (actionEvent != null && actionEvent.getSource() == editor) {
                    final String actionCommand = actionEvent.getActionCommand();
                    try {
                        n = Integer.parseInt(actionCommand, 10);
                    }
                    catch (final NumberFormatException ex) {}
                }
                final SimpleAttributeSet set = new SimpleAttributeSet();
                StyleConstants.setAlignment(set, n);
                this.setParagraphAttributes(editor, set, false);
            }
        }
    }
    
    public static class BoldAction extends StyledTextAction
    {
        public BoldAction() {
            super("font-bold");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JEditorPane editor = this.getEditor(actionEvent);
            if (editor != null) {
                final boolean b = !StyleConstants.isBold(this.getStyledEditorKit(editor).getInputAttributes());
                final SimpleAttributeSet set = new SimpleAttributeSet();
                StyleConstants.setBold(set, b);
                this.setCharacterAttributes(editor, set, false);
            }
        }
    }
    
    public static class ItalicAction extends StyledTextAction
    {
        public ItalicAction() {
            super("font-italic");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JEditorPane editor = this.getEditor(actionEvent);
            if (editor != null) {
                final boolean b = !StyleConstants.isItalic(this.getStyledEditorKit(editor).getInputAttributes());
                final SimpleAttributeSet set = new SimpleAttributeSet();
                StyleConstants.setItalic(set, b);
                this.setCharacterAttributes(editor, set, false);
            }
        }
    }
    
    public static class UnderlineAction extends StyledTextAction
    {
        public UnderlineAction() {
            super("font-underline");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JEditorPane editor = this.getEditor(actionEvent);
            if (editor != null) {
                final boolean b = !StyleConstants.isUnderline(this.getStyledEditorKit(editor).getInputAttributes());
                final SimpleAttributeSet set = new SimpleAttributeSet();
                StyleConstants.setUnderline(set, b);
                this.setCharacterAttributes(editor, set, false);
            }
        }
    }
    
    static class StyledInsertBreakAction extends StyledTextAction
    {
        private SimpleAttributeSet tempSet;
        
        StyledInsertBreakAction() {
            super("insert-break");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JEditorPane editor = this.getEditor(actionEvent);
            if (editor != null) {
                if (!editor.isEditable() || !editor.isEnabled()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(editor);
                    return;
                }
                final StyledEditorKit styledEditorKit = this.getStyledEditorKit(editor);
                if (this.tempSet != null) {
                    this.tempSet.removeAttributes(this.tempSet);
                }
                else {
                    this.tempSet = new SimpleAttributeSet();
                }
                this.tempSet.addAttributes(styledEditorKit.getInputAttributes());
                editor.replaceSelection("\n");
                final MutableAttributeSet inputAttributes = styledEditorKit.getInputAttributes();
                inputAttributes.removeAttributes(inputAttributes);
                inputAttributes.addAttributes(this.tempSet);
                this.tempSet.removeAttributes(this.tempSet);
            }
            else {
                final JTextComponent textComponent = this.getTextComponent(actionEvent);
                if (textComponent != null) {
                    if (!textComponent.isEditable() || !textComponent.isEnabled()) {
                        UIManager.getLookAndFeel().provideErrorFeedback(editor);
                        return;
                    }
                    textComponent.replaceSelection("\n");
                }
            }
        }
    }
}
