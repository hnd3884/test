package javax.swing.text.html;

import java.awt.Insets;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import javax.swing.text.AbstractDocument;
import javax.swing.SwingUtilities;
import java.awt.FontMetrics;
import java.awt.Container;
import java.awt.Toolkit;
import javax.swing.text.ViewFactory;
import java.awt.Shape;
import javax.swing.event.DocumentEvent;
import java.awt.Font;
import javax.swing.text.Document;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.text.StyledDocument;
import javax.swing.JTextField;
import java.awt.Component;
import javax.swing.text.Element;
import javax.swing.border.Border;
import java.awt.Color;
import javax.swing.event.DocumentListener;

class HiddenTagView extends EditableView implements DocumentListener
{
    float yAlign;
    boolean isSettingAttributes;
    static final int circleR = 3;
    static final int circleD = 6;
    static final int tagSize = 6;
    static final int padding = 3;
    static final Color UnknownTagBorderColor;
    static final Border StartBorder;
    static final Border EndBorder;
    
    HiddenTagView(final Element element) {
        super(element);
        this.yAlign = 1.0f;
    }
    
    @Override
    protected Component createComponent() {
        final JTextField textField = new JTextField(this.getElement().getName());
        final Document document = this.getDocument();
        Font font;
        if (document instanceof StyledDocument) {
            font = ((StyledDocument)document).getFont(this.getAttributes());
            textField.setFont(font);
        }
        else {
            font = textField.getFont();
        }
        textField.getDocument().addDocumentListener(this);
        this.updateYAlign(font);
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(null);
        if (this.isEndTag()) {
            panel.setBorder(HiddenTagView.EndBorder);
        }
        else {
            panel.setBorder(HiddenTagView.StartBorder);
        }
        panel.add(textField);
        return panel;
    }
    
    @Override
    public float getAlignment(final int n) {
        if (n == 1) {
            return this.yAlign;
        }
        return 0.5f;
    }
    
    @Override
    public float getMinimumSpan(final int n) {
        if (n == 0 && this.isVisible()) {
            return Math.max(30.0f, super.getPreferredSpan(n));
        }
        return super.getMinimumSpan(n);
    }
    
    @Override
    public float getPreferredSpan(final int n) {
        if (n == 0 && this.isVisible()) {
            return Math.max(30.0f, super.getPreferredSpan(n));
        }
        return super.getPreferredSpan(n);
    }
    
    @Override
    public float getMaximumSpan(final int n) {
        if (n == 0 && this.isVisible()) {
            return Math.max(30.0f, super.getMaximumSpan(n));
        }
        return super.getMaximumSpan(n);
    }
    
    @Override
    public void insertUpdate(final DocumentEvent documentEvent) {
        this.updateModelFromText();
    }
    
    @Override
    public void removeUpdate(final DocumentEvent documentEvent) {
        this.updateModelFromText();
    }
    
    @Override
    public void changedUpdate(final DocumentEvent documentEvent) {
        this.updateModelFromText();
    }
    
    @Override
    public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        if (!this.isSettingAttributes) {
            this.setTextFromModel();
        }
    }
    
    void updateYAlign(final Font font) {
        final Container container = this.getContainer();
        final FontMetrics fontMetrics = (container != null) ? container.getFontMetrics(font) : Toolkit.getDefaultToolkit().getFontMetrics(font);
        final float n = (float)fontMetrics.getHeight();
        final float n2 = (float)fontMetrics.getDescent();
        this.yAlign = ((n > 0.0f) ? ((n - n2) / n) : 0.0f);
    }
    
    void resetBorder() {
        final Component component = this.getComponent();
        if (component != null) {
            if (this.isEndTag()) {
                ((JPanel)component).setBorder(HiddenTagView.EndBorder);
            }
            else {
                ((JPanel)component).setBorder(HiddenTagView.StartBorder);
            }
        }
    }
    
    void setTextFromModel() {
        if (SwingUtilities.isEventDispatchThread()) {
            this._setTextFromModel();
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    HiddenTagView.this._setTextFromModel();
                }
            });
        }
    }
    
    void _setTextFromModel() {
        final Document document = this.getDocument();
        try {
            this.isSettingAttributes = true;
            if (document instanceof AbstractDocument) {
                ((AbstractDocument)document).readLock();
            }
            final JTextComponent textComponent = this.getTextComponent();
            if (textComponent != null) {
                textComponent.setText(this.getRepresentedText());
                this.resetBorder();
                final Container container = this.getContainer();
                if (container != null) {
                    this.preferenceChanged(this, true, true);
                    container.repaint();
                }
            }
        }
        finally {
            this.isSettingAttributes = false;
            if (document instanceof AbstractDocument) {
                ((AbstractDocument)document).readUnlock();
            }
        }
    }
    
    void updateModelFromText() {
        if (!this.isSettingAttributes) {
            if (SwingUtilities.isEventDispatchThread()) {
                this._updateModelFromText();
            }
            else {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        HiddenTagView.this._updateModelFromText();
                    }
                });
            }
        }
    }
    
    void _updateModelFromText() {
        final Document document = this.getDocument();
        if (this.getElement().getAttributes().getAttribute(StyleConstants.NameAttribute) instanceof HTML.UnknownTag && document instanceof StyledDocument) {
            final SimpleAttributeSet set = new SimpleAttributeSet();
            final JTextComponent textComponent = this.getTextComponent();
            if (textComponent != null) {
                final String text = textComponent.getText();
                this.isSettingAttributes = true;
                try {
                    set.addAttribute(StyleConstants.NameAttribute, new HTML.UnknownTag(text));
                    ((StyledDocument)document).setCharacterAttributes(this.getStartOffset(), this.getEndOffset() - this.getStartOffset(), set, false);
                }
                finally {
                    this.isSettingAttributes = false;
                }
            }
        }
    }
    
    JTextComponent getTextComponent() {
        final Component component = this.getComponent();
        return (component == null) ? null : ((JTextComponent)((Container)component).getComponent(0));
    }
    
    String getRepresentedText() {
        final String name = this.getElement().getName();
        return (name == null) ? "" : name;
    }
    
    boolean isEndTag() {
        final AttributeSet attributes = this.getElement().getAttributes();
        if (attributes != null) {
            final Object attribute = attributes.getAttribute(HTML.Attribute.ENDTAG);
            if (attribute != null && attribute instanceof String && ((String)attribute).equals("true")) {
                return true;
            }
        }
        return false;
    }
    
    static {
        UnknownTagBorderColor = Color.black;
        StartBorder = new StartTagBorder();
        EndBorder = new EndTagBorder();
    }
    
    static class StartTagBorder implements Border, Serializable
    {
        @Override
        public void paintBorder(final Component component, final Graphics graphics, int n, final int n2, int n3, final int n4) {
            graphics.setColor(HiddenTagView.UnknownTagBorderColor);
            n += 3;
            n3 -= 6;
            graphics.drawLine(n, n2 + 3, n, n2 + n4 - 3);
            graphics.drawArc(n, n2 + n4 - 6 - 1, 6, 6, 180, 90);
            graphics.drawArc(n, n2, 6, 6, 90, 90);
            graphics.drawLine(n + 3, n2, n + n3 - 6, n2);
            graphics.drawLine(n + 3, n2 + n4 - 1, n + n3 - 6, n2 + n4 - 1);
            graphics.drawLine(n + n3 - 6, n2, n + n3 - 1, n2 + n4 / 2);
            graphics.drawLine(n + n3 - 6, n2 + n4, n + n3 - 1, n2 + n4 / 2);
        }
        
        @Override
        public Insets getBorderInsets(final Component component) {
            return new Insets(2, 5, 2, 11);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
    
    static class EndTagBorder implements Border, Serializable
    {
        @Override
        public void paintBorder(final Component component, final Graphics graphics, int n, final int n2, int n3, final int n4) {
            graphics.setColor(HiddenTagView.UnknownTagBorderColor);
            n += 3;
            n3 -= 6;
            graphics.drawLine(n + n3 - 1, n2 + 3, n + n3 - 1, n2 + n4 - 3);
            graphics.drawArc(n + n3 - 6 - 1, n2 + n4 - 6 - 1, 6, 6, 270, 90);
            graphics.drawArc(n + n3 - 6 - 1, n2, 6, 6, 0, 90);
            graphics.drawLine(n + 6, n2, n + n3 - 3, n2);
            graphics.drawLine(n + 6, n2 + n4 - 1, n + n3 - 3, n2 + n4 - 1);
            graphics.drawLine(n + 6, n2, n, n2 + n4 / 2);
            graphics.drawLine(n + 6, n2 + n4, n, n2 + n4 / 2);
        }
        
        @Override
        public Insets getBorderInsets(final Component component) {
            return new Insets(2, 11, 2, 5);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
