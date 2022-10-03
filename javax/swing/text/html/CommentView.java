package javax.swing.text.html;

import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import java.awt.Font;
import javax.swing.text.Document;
import java.awt.Container;
import javax.swing.event.DocumentListener;
import javax.swing.text.StyledDocument;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;
import java.awt.Component;
import javax.swing.text.Element;
import javax.swing.border.Border;

class CommentView extends HiddenTagView
{
    static final Border CBorder;
    static final int commentPadding = 3;
    static final int commentPaddingD = 9;
    
    CommentView(final Element element) {
        super(element);
    }
    
    @Override
    protected Component createComponent() {
        final Container container = this.getContainer();
        if (container != null && !((JTextComponent)container).isEditable()) {
            return null;
        }
        final JTextArea textArea = new JTextArea(this.getRepresentedText());
        final Document document = this.getDocument();
        Font font;
        if (document instanceof StyledDocument) {
            font = ((StyledDocument)document).getFont(this.getAttributes());
            textArea.setFont(font);
        }
        else {
            font = textArea.getFont();
        }
        this.updateYAlign(font);
        textArea.setBorder(CommentView.CBorder);
        textArea.getDocument().addDocumentListener(this);
        textArea.setFocusable(this.isVisible());
        return textArea;
    }
    
    @Override
    void resetBorder() {
    }
    
    @Override
    void _updateModelFromText() {
        final JTextComponent textComponent = this.getTextComponent();
        final Document document = this.getDocument();
        if (textComponent != null && document != null) {
            final String text = textComponent.getText();
            final SimpleAttributeSet set = new SimpleAttributeSet();
            this.isSettingAttributes = true;
            try {
                set.addAttribute(HTML.Attribute.COMMENT, text);
                ((StyledDocument)document).setCharacterAttributes(this.getStartOffset(), this.getEndOffset() - this.getStartOffset(), set, false);
            }
            finally {
                this.isSettingAttributes = false;
            }
        }
    }
    
    @Override
    JTextComponent getTextComponent() {
        return (JTextComponent)this.getComponent();
    }
    
    @Override
    String getRepresentedText() {
        final AttributeSet attributes = this.getElement().getAttributes();
        if (attributes != null) {
            final Object attribute = attributes.getAttribute(HTML.Attribute.COMMENT);
            if (attribute instanceof String) {
                return (String)attribute;
            }
        }
        return "";
    }
    
    static {
        CBorder = new CommentBorder();
    }
    
    static class CommentBorder extends LineBorder
    {
        CommentBorder() {
            super(Color.black, 1);
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            super.paintBorder(component, graphics, n + 3, n2, n3 - 9, n4);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            final Insets borderInsets;
            final Insets insets2 = borderInsets = super.getBorderInsets(component, insets);
            borderInsets.left += 3;
            final Insets insets3 = insets2;
            insets3.right += 3;
            return insets2;
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
