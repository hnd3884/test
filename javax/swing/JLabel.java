package javax.swing;

import javax.accessibility.AccessibleKeyBinding;
import java.awt.Insets;
import javax.swing.text.Element;
import javax.swing.text.AttributeSet;
import java.text.BreakIterator;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.BadLocationException;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.text.Position;
import java.awt.geom.Rectangle2D;
import java.awt.Point;
import javax.swing.text.View;
import javax.accessibility.AccessibleRelation;
import javax.accessibility.AccessibleRelationSet;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleExtendedComponent;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.beans.Transient;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LabelUI;
import java.awt.Component;
import javax.accessibility.Accessible;

public class JLabel extends JComponent implements SwingConstants, Accessible
{
    private static final String uiClassID = "LabelUI";
    private int mnemonic;
    private int mnemonicIndex;
    private String text;
    private Icon defaultIcon;
    private Icon disabledIcon;
    private boolean disabledIconSet;
    private int verticalAlignment;
    private int horizontalAlignment;
    private int verticalTextPosition;
    private int horizontalTextPosition;
    private int iconTextGap;
    protected Component labelFor;
    static final String LABELED_BY_PROPERTY = "labeledBy";
    
    public JLabel(final String text, final Icon icon, final int horizontalAlignment) {
        this.mnemonic = 0;
        this.mnemonicIndex = -1;
        this.text = "";
        this.defaultIcon = null;
        this.disabledIcon = null;
        this.disabledIconSet = false;
        this.verticalAlignment = 0;
        this.horizontalAlignment = 10;
        this.verticalTextPosition = 0;
        this.horizontalTextPosition = 11;
        this.iconTextGap = 4;
        this.labelFor = null;
        this.setText(text);
        this.setIcon(icon);
        this.setHorizontalAlignment(horizontalAlignment);
        this.updateUI();
        this.setAlignmentX(0.0f);
    }
    
    public JLabel(final String s, final int n) {
        this(s, null, n);
    }
    
    public JLabel(final String s) {
        this(s, null, 10);
    }
    
    public JLabel(final Icon icon, final int n) {
        this(null, icon, n);
    }
    
    public JLabel(final Icon icon) {
        this(null, icon, 0);
    }
    
    public JLabel() {
        this("", null, 10);
    }
    
    public LabelUI getUI() {
        return (LabelUI)this.ui;
    }
    
    public void setUI(final LabelUI ui) {
        super.setUI(ui);
        if (!this.disabledIconSet && this.disabledIcon != null) {
            this.setDisabledIcon(null);
        }
    }
    
    @Override
    public void updateUI() {
        this.setUI((LabelUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "LabelUI";
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(final String text) {
        String accessibleName = null;
        if (this.accessibleContext != null) {
            accessibleName = this.accessibleContext.getAccessibleName();
        }
        final String text2 = this.text;
        this.firePropertyChange("text", text2, this.text = text);
        this.setDisplayedMnemonicIndex(SwingUtilities.findDisplayedMnemonicIndex(text, this.getDisplayedMnemonic()));
        if (this.accessibleContext != null && this.accessibleContext.getAccessibleName() != accessibleName) {
            this.accessibleContext.firePropertyChange("AccessibleVisibleData", accessibleName, this.accessibleContext.getAccessibleName());
        }
        if (text == null || text2 == null || !text.equals(text2)) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public Icon getIcon() {
        return this.defaultIcon;
    }
    
    public void setIcon(final Icon defaultIcon) {
        final Icon defaultIcon2 = this.defaultIcon;
        this.defaultIcon = defaultIcon;
        if (this.defaultIcon != defaultIcon2 && !this.disabledIconSet) {
            this.disabledIcon = null;
        }
        this.firePropertyChange("icon", defaultIcon2, this.defaultIcon);
        if (this.accessibleContext != null && defaultIcon2 != this.defaultIcon) {
            this.accessibleContext.firePropertyChange("AccessibleVisibleData", defaultIcon2, this.defaultIcon);
        }
        if (this.defaultIcon != defaultIcon2) {
            if (this.defaultIcon == null || defaultIcon2 == null || this.defaultIcon.getIconWidth() != defaultIcon2.getIconWidth() || this.defaultIcon.getIconHeight() != defaultIcon2.getIconHeight()) {
                this.revalidate();
            }
            this.repaint();
        }
    }
    
    @Transient
    public Icon getDisabledIcon() {
        if (!this.disabledIconSet && this.disabledIcon == null && this.defaultIcon != null) {
            this.disabledIcon = UIManager.getLookAndFeel().getDisabledIcon(this, this.defaultIcon);
            if (this.disabledIcon != null) {
                this.firePropertyChange("disabledIcon", null, this.disabledIcon);
            }
        }
        return this.disabledIcon;
    }
    
    public void setDisabledIcon(final Icon disabledIcon) {
        final Icon disabledIcon2 = this.disabledIcon;
        this.disabledIcon = disabledIcon;
        this.disabledIconSet = (disabledIcon != null);
        this.firePropertyChange("disabledIcon", disabledIcon2, disabledIcon);
        if (disabledIcon != disabledIcon2) {
            if (disabledIcon == null || disabledIcon2 == null || disabledIcon.getIconWidth() != disabledIcon2.getIconWidth() || disabledIcon.getIconHeight() != disabledIcon2.getIconHeight()) {
                this.revalidate();
            }
            if (!this.isEnabled()) {
                this.repaint();
            }
        }
    }
    
    public void setDisplayedMnemonic(final int mnemonic) {
        final int mnemonic2 = this.mnemonic;
        this.firePropertyChange("displayedMnemonic", mnemonic2, this.mnemonic = mnemonic);
        this.setDisplayedMnemonicIndex(SwingUtilities.findDisplayedMnemonicIndex(this.getText(), this.mnemonic));
        if (mnemonic != mnemonic2) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public void setDisplayedMnemonic(final char c) {
        final int extendedKeyCodeForChar = KeyEvent.getExtendedKeyCodeForChar(c);
        if (extendedKeyCodeForChar != 0) {
            this.setDisplayedMnemonic(extendedKeyCodeForChar);
        }
    }
    
    public int getDisplayedMnemonic() {
        return this.mnemonic;
    }
    
    public void setDisplayedMnemonicIndex(final int mnemonicIndex) throws IllegalArgumentException {
        final int mnemonicIndex2 = this.mnemonicIndex;
        if (mnemonicIndex == -1) {
            this.mnemonicIndex = -1;
        }
        else {
            final String text = this.getText();
            final int n = (text == null) ? 0 : text.length();
            if (mnemonicIndex < -1 || mnemonicIndex >= n) {
                throw new IllegalArgumentException("index == " + mnemonicIndex);
            }
        }
        this.firePropertyChange("displayedMnemonicIndex", mnemonicIndex2, this.mnemonicIndex = mnemonicIndex);
        if (mnemonicIndex != mnemonicIndex2) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public int getDisplayedMnemonicIndex() {
        return this.mnemonicIndex;
    }
    
    protected int checkHorizontalKey(final int n, final String s) {
        if (n == 2 || n == 0 || n == 4 || n == 10 || n == 11) {
            return n;
        }
        throw new IllegalArgumentException(s);
    }
    
    protected int checkVerticalKey(final int n, final String s) {
        if (n == 1 || n == 0 || n == 3) {
            return n;
        }
        throw new IllegalArgumentException(s);
    }
    
    public int getIconTextGap() {
        return this.iconTextGap;
    }
    
    public void setIconTextGap(final int iconTextGap) {
        final int iconTextGap2 = this.iconTextGap;
        this.firePropertyChange("iconTextGap", iconTextGap2, this.iconTextGap = iconTextGap);
        if (iconTextGap != iconTextGap2) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }
    
    public void setVerticalAlignment(final int n) {
        if (n == this.verticalAlignment) {
            return;
        }
        this.firePropertyChange("verticalAlignment", this.verticalAlignment, this.verticalAlignment = this.checkVerticalKey(n, "verticalAlignment"));
        this.repaint();
    }
    
    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }
    
    public void setHorizontalAlignment(final int n) {
        if (n == this.horizontalAlignment) {
            return;
        }
        this.firePropertyChange("horizontalAlignment", this.horizontalAlignment, this.horizontalAlignment = this.checkHorizontalKey(n, "horizontalAlignment"));
        this.repaint();
    }
    
    public int getVerticalTextPosition() {
        return this.verticalTextPosition;
    }
    
    public void setVerticalTextPosition(final int n) {
        if (n == this.verticalTextPosition) {
            return;
        }
        this.firePropertyChange("verticalTextPosition", this.verticalTextPosition, this.verticalTextPosition = this.checkVerticalKey(n, "verticalTextPosition"));
        this.revalidate();
        this.repaint();
    }
    
    public int getHorizontalTextPosition() {
        return this.horizontalTextPosition;
    }
    
    public void setHorizontalTextPosition(final int n) {
        this.firePropertyChange("horizontalTextPosition", this.horizontalTextPosition, this.horizontalTextPosition = this.checkHorizontalKey(n, "horizontalTextPosition"));
        this.revalidate();
        this.repaint();
    }
    
    @Override
    public boolean imageUpdate(final Image image, final int n, final int n2, final int n3, final int n4, final int n5) {
        return this.isShowing() && (SwingUtilities.doesIconReferenceImage(this.getIcon(), image) || SwingUtilities.doesIconReferenceImage(this.disabledIcon, image)) && super.imageUpdate(image, n, n2, n3, n4, n5);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("LabelUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        final String s = (this.text != null) ? this.text : "";
        final String s2 = (this.defaultIcon != null && this.defaultIcon != this) ? this.defaultIcon.toString() : "";
        final String s3 = (this.disabledIcon != null && this.disabledIcon != this) ? this.disabledIcon.toString() : "";
        final String s4 = (this.labelFor != null) ? this.labelFor.toString() : "";
        String s5;
        if (this.verticalAlignment == 1) {
            s5 = "TOP";
        }
        else if (this.verticalAlignment == 0) {
            s5 = "CENTER";
        }
        else if (this.verticalAlignment == 3) {
            s5 = "BOTTOM";
        }
        else {
            s5 = "";
        }
        String s6;
        if (this.horizontalAlignment == 2) {
            s6 = "LEFT";
        }
        else if (this.horizontalAlignment == 0) {
            s6 = "CENTER";
        }
        else if (this.horizontalAlignment == 4) {
            s6 = "RIGHT";
        }
        else if (this.horizontalAlignment == 10) {
            s6 = "LEADING";
        }
        else if (this.horizontalAlignment == 11) {
            s6 = "TRAILING";
        }
        else {
            s6 = "";
        }
        String s7;
        if (this.verticalTextPosition == 1) {
            s7 = "TOP";
        }
        else if (this.verticalTextPosition == 0) {
            s7 = "CENTER";
        }
        else if (this.verticalTextPosition == 3) {
            s7 = "BOTTOM";
        }
        else {
            s7 = "";
        }
        String s8;
        if (this.horizontalTextPosition == 2) {
            s8 = "LEFT";
        }
        else if (this.horizontalTextPosition == 0) {
            s8 = "CENTER";
        }
        else if (this.horizontalTextPosition == 4) {
            s8 = "RIGHT";
        }
        else if (this.horizontalTextPosition == 10) {
            s8 = "LEADING";
        }
        else if (this.horizontalTextPosition == 11) {
            s8 = "TRAILING";
        }
        else {
            s8 = "";
        }
        return super.paramString() + ",defaultIcon=" + s2 + ",disabledIcon=" + s3 + ",horizontalAlignment=" + s6 + ",horizontalTextPosition=" + s8 + ",iconTextGap=" + this.iconTextGap + ",labelFor=" + s4 + ",text=" + s + ",verticalAlignment=" + s5 + ",verticalTextPosition=" + s7;
    }
    
    public Component getLabelFor() {
        return this.labelFor;
    }
    
    public void setLabelFor(final Component labelFor) {
        final Component labelFor2 = this.labelFor;
        this.firePropertyChange("labelFor", labelFor2, this.labelFor = labelFor);
        if (labelFor2 instanceof JComponent) {
            ((JComponent)labelFor2).putClientProperty("labeledBy", null);
        }
        if (labelFor instanceof JComponent) {
            ((JComponent)labelFor).putClientProperty("labeledBy", this);
        }
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJLabel();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleJLabel extends AccessibleJComponent implements AccessibleText, AccessibleExtendedComponent
    {
        @Override
        public String getAccessibleName() {
            String s = this.accessibleName;
            if (s == null) {
                s = (String)JLabel.this.getClientProperty("AccessibleName");
            }
            if (s == null) {
                s = JLabel.this.getText();
            }
            if (s == null) {
                s = super.getAccessibleName();
            }
            return s;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.LABEL;
        }
        
        @Override
        public AccessibleIcon[] getAccessibleIcon() {
            final Icon icon = JLabel.this.getIcon();
            if (icon instanceof Accessible) {
                final AccessibleContext accessibleContext = ((Accessible)icon).getAccessibleContext();
                if (accessibleContext != null && accessibleContext instanceof AccessibleIcon) {
                    return new AccessibleIcon[] { (AccessibleIcon)accessibleContext };
                }
            }
            return null;
        }
        
        @Override
        public AccessibleRelationSet getAccessibleRelationSet() {
            final AccessibleRelationSet accessibleRelationSet = super.getAccessibleRelationSet();
            if (!accessibleRelationSet.contains(AccessibleRelation.LABEL_FOR)) {
                final Component label = JLabel.this.getLabelFor();
                if (label != null) {
                    final AccessibleRelation accessibleRelation = new AccessibleRelation(AccessibleRelation.LABEL_FOR);
                    accessibleRelation.setTarget(label);
                    accessibleRelationSet.add(accessibleRelation);
                }
            }
            return accessibleRelationSet;
        }
        
        @Override
        public AccessibleText getAccessibleText() {
            if (JLabel.this.getClientProperty("html") != null) {
                return this;
            }
            return null;
        }
        
        @Override
        public int getIndexAtPoint(final Point point) {
            final View view = (View)JLabel.this.getClientProperty("html");
            if (view == null) {
                return -1;
            }
            final Rectangle textRectangle = this.getTextRectangle();
            if (textRectangle == null) {
                return -1;
            }
            return view.viewToModel((float)point.x, (float)point.y, new Rectangle2D.Float((float)textRectangle.x, (float)textRectangle.y, (float)textRectangle.width, (float)textRectangle.height), new Position.Bias[1]);
        }
        
        @Override
        public Rectangle getCharacterBounds(final int n) {
            final View view = (View)JLabel.this.getClientProperty("html");
            if (view != null) {
                final Rectangle textRectangle = this.getTextRectangle();
                if (textRectangle == null) {
                    return null;
                }
                final Rectangle2D.Float float1 = new Rectangle2D.Float((float)textRectangle.x, (float)textRectangle.y, (float)textRectangle.width, (float)textRectangle.height);
                try {
                    return view.modelToView(n, float1, Position.Bias.Forward).getBounds();
                }
                catch (final BadLocationException ex) {
                    return null;
                }
            }
            return null;
        }
        
        @Override
        public int getCharCount() {
            final View view = (View)JLabel.this.getClientProperty("html");
            if (view != null) {
                final Document document = view.getDocument();
                if (document instanceof StyledDocument) {
                    return ((StyledDocument)document).getLength();
                }
            }
            return JLabel.this.accessibleContext.getAccessibleName().length();
        }
        
        @Override
        public int getCaretPosition() {
            return -1;
        }
        
        @Override
        public String getAtIndex(final int n, final int n2) {
            if (n2 < 0 || n2 >= this.getCharCount()) {
                return null;
            }
            switch (n) {
                case 1: {
                    try {
                        return this.getText(n2, 1);
                    }
                    catch (final BadLocationException ex) {
                        return null;
                    }
                }
                case 2: {
                    try {
                        final String text = this.getText(0, this.getCharCount());
                        final BreakIterator wordInstance = BreakIterator.getWordInstance(this.getLocale());
                        wordInstance.setText(text);
                        return text.substring(wordInstance.previous(), wordInstance.following(n2));
                    }
                    catch (final BadLocationException ex2) {
                        return null;
                    }
                }
                case 3: {
                    try {
                        final String text2 = this.getText(0, this.getCharCount());
                        final BreakIterator sentenceInstance = BreakIterator.getSentenceInstance(this.getLocale());
                        sentenceInstance.setText(text2);
                        return text2.substring(sentenceInstance.previous(), sentenceInstance.following(n2));
                    }
                    catch (final BadLocationException ex3) {
                        return null;
                    }
                    break;
                }
            }
            return null;
        }
        
        @Override
        public String getAfterIndex(final int n, final int n2) {
            if (n2 < 0 || n2 >= this.getCharCount()) {
                return null;
            }
            switch (n) {
                case 1: {
                    if (n2 + 1 >= this.getCharCount()) {
                        return null;
                    }
                    try {
                        return this.getText(n2 + 1, 1);
                    }
                    catch (final BadLocationException ex) {
                        return null;
                    }
                }
                case 2: {
                    try {
                        final String text = this.getText(0, this.getCharCount());
                        final BreakIterator wordInstance = BreakIterator.getWordInstance(this.getLocale());
                        wordInstance.setText(text);
                        final int following = wordInstance.following(n2);
                        if (following == -1 || following >= text.length()) {
                            return null;
                        }
                        final int following2 = wordInstance.following(following);
                        if (following2 == -1 || following2 >= text.length()) {
                            return null;
                        }
                        return text.substring(following, following2);
                    }
                    catch (final BadLocationException ex2) {
                        return null;
                    }
                }
                case 3: {
                    try {
                        final String text2 = this.getText(0, this.getCharCount());
                        final BreakIterator sentenceInstance = BreakIterator.getSentenceInstance(this.getLocale());
                        sentenceInstance.setText(text2);
                        final int following3 = sentenceInstance.following(n2);
                        if (following3 == -1 || following3 > text2.length()) {
                            return null;
                        }
                        final int following4 = sentenceInstance.following(following3);
                        if (following4 == -1 || following4 > text2.length()) {
                            return null;
                        }
                        return text2.substring(following3, following4);
                    }
                    catch (final BadLocationException ex3) {
                        return null;
                    }
                    break;
                }
            }
            return null;
        }
        
        @Override
        public String getBeforeIndex(final int n, final int n2) {
            if (n2 < 0 || n2 > this.getCharCount() - 1) {
                return null;
            }
            switch (n) {
                case 1: {
                    if (n2 == 0) {
                        return null;
                    }
                    try {
                        return this.getText(n2 - 1, 1);
                    }
                    catch (final BadLocationException ex) {
                        return null;
                    }
                }
                case 2: {
                    try {
                        final String text = this.getText(0, this.getCharCount());
                        final BreakIterator wordInstance = BreakIterator.getWordInstance(this.getLocale());
                        wordInstance.setText(text);
                        wordInstance.following(n2);
                        final int previous = wordInstance.previous();
                        final int previous2 = wordInstance.previous();
                        if (previous2 == -1) {
                            return null;
                        }
                        return text.substring(previous2, previous);
                    }
                    catch (final BadLocationException ex2) {
                        return null;
                    }
                }
                case 3: {
                    try {
                        final String text2 = this.getText(0, this.getCharCount());
                        final BreakIterator sentenceInstance = BreakIterator.getSentenceInstance(this.getLocale());
                        sentenceInstance.setText(text2);
                        sentenceInstance.following(n2);
                        final int previous3 = sentenceInstance.previous();
                        final int previous4 = sentenceInstance.previous();
                        if (previous4 == -1) {
                            return null;
                        }
                        return text2.substring(previous4, previous3);
                    }
                    catch (final BadLocationException ex3) {
                        return null;
                    }
                    break;
                }
            }
            return null;
        }
        
        @Override
        public AttributeSet getCharacterAttribute(final int n) {
            final View view = (View)JLabel.this.getClientProperty("html");
            if (view != null) {
                final Document document = view.getDocument();
                if (document instanceof StyledDocument) {
                    final Element characterElement = ((StyledDocument)document).getCharacterElement(n);
                    if (characterElement != null) {
                        return characterElement.getAttributes();
                    }
                }
            }
            return null;
        }
        
        @Override
        public int getSelectionStart() {
            return -1;
        }
        
        @Override
        public int getSelectionEnd() {
            return -1;
        }
        
        @Override
        public String getSelectedText() {
            return null;
        }
        
        private String getText(final int n, final int n2) throws BadLocationException {
            final View view = (View)JLabel.this.getClientProperty("html");
            if (view != null) {
                final Document document = view.getDocument();
                if (document instanceof StyledDocument) {
                    return ((StyledDocument)document).getText(n, n2);
                }
            }
            return null;
        }
        
        private Rectangle getTextRectangle() {
            final String text = JLabel.this.getText();
            final Icon icon = JLabel.this.isEnabled() ? JLabel.this.getIcon() : JLabel.this.getDisabledIcon();
            if (icon == null && text == null) {
                return null;
            }
            final Rectangle rectangle = new Rectangle();
            final Rectangle rectangle2 = new Rectangle();
            final Rectangle rectangle3 = new Rectangle();
            final Insets insets = JLabel.this.getInsets(new Insets(0, 0, 0, 0));
            rectangle3.x = insets.left;
            rectangle3.y = insets.top;
            rectangle3.width = JLabel.this.getWidth() - (insets.left + insets.right);
            rectangle3.height = JLabel.this.getHeight() - (insets.top + insets.bottom);
            SwingUtilities.layoutCompoundLabel(JLabel.this, this.getFontMetrics(this.getFont()), text, icon, JLabel.this.getVerticalAlignment(), JLabel.this.getHorizontalAlignment(), JLabel.this.getVerticalTextPosition(), JLabel.this.getHorizontalTextPosition(), rectangle3, rectangle, rectangle2, JLabel.this.getIconTextGap());
            return rectangle2;
        }
        
        @Override
        AccessibleExtendedComponent getAccessibleExtendedComponent() {
            return this;
        }
        
        @Override
        public String getToolTipText() {
            return JLabel.this.getToolTipText();
        }
        
        @Override
        public String getTitledBorderText() {
            return super.getTitledBorderText();
        }
        
        @Override
        public AccessibleKeyBinding getAccessibleKeyBinding() {
            final int displayedMnemonic = JLabel.this.getDisplayedMnemonic();
            if (displayedMnemonic == 0) {
                return null;
            }
            return new LabelKeyBinding(displayedMnemonic);
        }
        
        class LabelKeyBinding implements AccessibleKeyBinding
        {
            int mnemonic;
            
            LabelKeyBinding(final int mnemonic) {
                this.mnemonic = mnemonic;
            }
            
            @Override
            public int getAccessibleKeyBindingCount() {
                return 1;
            }
            
            @Override
            public Object getAccessibleKeyBinding(final int n) {
                if (n != 0) {
                    throw new IllegalArgumentException();
                }
                return KeyStroke.getKeyStroke(this.mnemonic, 0);
            }
        }
    }
}
