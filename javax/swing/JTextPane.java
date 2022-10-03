package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.Caret;
import javax.swing.text.BadLocationException;
import javax.swing.text.AbstractDocument;
import java.awt.Component;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.EditorKit;

public class JTextPane extends JEditorPane
{
    private static final String uiClassID = "TextPaneUI";
    
    public JTextPane() {
        final EditorKit defaultEditorKit = this.createDefaultEditorKit();
        final String contentType = defaultEditorKit.getContentType();
        if (contentType != null && JEditorPane.getEditorKitClassNameForContentType(contentType) == JTextPane.defaultEditorKitMap.get(contentType)) {
            this.setEditorKitForContentType(contentType, defaultEditorKit);
        }
        this.setEditorKit(defaultEditorKit);
    }
    
    public JTextPane(final StyledDocument styledDocument) {
        this();
        this.setStyledDocument(styledDocument);
    }
    
    @Override
    public String getUIClassID() {
        return "TextPaneUI";
    }
    
    @Override
    public void setDocument(final Document document) {
        if (document instanceof StyledDocument) {
            super.setDocument(document);
            return;
        }
        throw new IllegalArgumentException("Model must be StyledDocument");
    }
    
    public void setStyledDocument(final StyledDocument document) {
        super.setDocument(document);
    }
    
    public StyledDocument getStyledDocument() {
        return (StyledDocument)this.getDocument();
    }
    
    @Override
    public void replaceSelection(final String s) {
        this.replaceSelection(s, true);
    }
    
    private void replaceSelection(final String s, final boolean b) {
        if (b && !this.isEditable()) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }
        final StyledDocument styledDocument = this.getStyledDocument();
        if (styledDocument != null) {
            try {
                final Caret caret = this.getCaret();
                final boolean saveComposedText = this.saveComposedText(caret.getDot());
                final int min = Math.min(caret.getDot(), caret.getMark());
                final int max = Math.max(caret.getDot(), caret.getMark());
                final AttributeSet copyAttributes = this.getInputAttributes().copyAttributes();
                if (styledDocument instanceof AbstractDocument) {
                    ((AbstractDocument)styledDocument).replace(min, max - min, s, copyAttributes);
                }
                else {
                    if (min != max) {
                        styledDocument.remove(min, max - min);
                    }
                    if (s != null && s.length() > 0) {
                        styledDocument.insertString(min, s, copyAttributes);
                    }
                }
                if (saveComposedText) {
                    this.restoreComposedText();
                }
            }
            catch (final BadLocationException ex) {
                UIManager.getLookAndFeel().provideErrorFeedback(this);
            }
        }
    }
    
    public void insertComponent(final Component component) {
        final MutableAttributeSet inputAttributes = this.getInputAttributes();
        inputAttributes.removeAttributes(inputAttributes);
        StyleConstants.setComponent(inputAttributes, component);
        this.replaceSelection(" ", false);
        inputAttributes.removeAttributes(inputAttributes);
    }
    
    public void insertIcon(final Icon icon) {
        final MutableAttributeSet inputAttributes = this.getInputAttributes();
        inputAttributes.removeAttributes(inputAttributes);
        StyleConstants.setIcon(inputAttributes, icon);
        this.replaceSelection(" ", false);
        inputAttributes.removeAttributes(inputAttributes);
    }
    
    public Style addStyle(final String s, final Style style) {
        return this.getStyledDocument().addStyle(s, style);
    }
    
    public void removeStyle(final String s) {
        this.getStyledDocument().removeStyle(s);
    }
    
    public Style getStyle(final String s) {
        return this.getStyledDocument().getStyle(s);
    }
    
    public void setLogicalStyle(final Style style) {
        this.getStyledDocument().setLogicalStyle(this.getCaretPosition(), style);
    }
    
    public Style getLogicalStyle() {
        return this.getStyledDocument().getLogicalStyle(this.getCaretPosition());
    }
    
    public AttributeSet getCharacterAttributes() {
        final Element characterElement = this.getStyledDocument().getCharacterElement(this.getCaretPosition());
        if (characterElement != null) {
            return characterElement.getAttributes();
        }
        return null;
    }
    
    public void setCharacterAttributes(final AttributeSet set, final boolean b) {
        final int selectionStart = this.getSelectionStart();
        final int selectionEnd = this.getSelectionEnd();
        if (selectionStart != selectionEnd) {
            this.getStyledDocument().setCharacterAttributes(selectionStart, selectionEnd - selectionStart, set, b);
        }
        else {
            final MutableAttributeSet inputAttributes = this.getInputAttributes();
            if (b) {
                inputAttributes.removeAttributes(inputAttributes);
            }
            inputAttributes.addAttributes(set);
        }
    }
    
    public AttributeSet getParagraphAttributes() {
        final Element paragraphElement = this.getStyledDocument().getParagraphElement(this.getCaretPosition());
        if (paragraphElement != null) {
            return paragraphElement.getAttributes();
        }
        return null;
    }
    
    public void setParagraphAttributes(final AttributeSet set, final boolean b) {
        final int selectionStart = this.getSelectionStart();
        this.getStyledDocument().setParagraphAttributes(selectionStart, this.getSelectionEnd() - selectionStart, set, b);
    }
    
    public MutableAttributeSet getInputAttributes() {
        return this.getStyledEditorKit().getInputAttributes();
    }
    
    protected final StyledEditorKit getStyledEditorKit() {
        return (StyledEditorKit)this.getEditorKit();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("TextPaneUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected EditorKit createDefaultEditorKit() {
        return new StyledEditorKit();
    }
    
    @Override
    public final void setEditorKit(final EditorKit editorKit) {
        if (editorKit instanceof StyledEditorKit) {
            super.setEditorKit(editorKit);
            return;
        }
        throw new IllegalArgumentException("Must be StyledEditorKit");
    }
    
    @Override
    protected String paramString() {
        return super.paramString();
    }
}
