package javax.swing;

import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public class JTextArea extends JTextComponent
{
    private static final String uiClassID = "TextAreaUI";
    private int rows;
    private int columns;
    private int columnWidth;
    private int rowHeight;
    private boolean wrap;
    private boolean word;
    
    public JTextArea() {
        this(null, null, 0, 0);
    }
    
    public JTextArea(final String s) {
        this(null, s, 0, 0);
    }
    
    public JTextArea(final int n, final int n2) {
        this(null, null, n, n2);
    }
    
    public JTextArea(final String s, final int n, final int n2) {
        this(null, s, n, n2);
    }
    
    public JTextArea(final Document document) {
        this(document, null, 0, 0);
    }
    
    public JTextArea(Document defaultModel, final String text, final int rows, final int columns) {
        this.rows = rows;
        this.columns = columns;
        if (defaultModel == null) {
            defaultModel = this.createDefaultModel();
        }
        this.setDocument(defaultModel);
        if (text != null) {
            this.setText(text);
            this.select(0, 0);
        }
        if (rows < 0) {
            throw new IllegalArgumentException("rows: " + rows);
        }
        if (columns < 0) {
            throw new IllegalArgumentException("columns: " + columns);
        }
        LookAndFeel.installProperty(this, "focusTraversalKeysForward", JComponent.getManagingFocusForwardTraversalKeys());
        LookAndFeel.installProperty(this, "focusTraversalKeysBackward", JComponent.getManagingFocusBackwardTraversalKeys());
    }
    
    @Override
    public String getUIClassID() {
        return "TextAreaUI";
    }
    
    protected Document createDefaultModel() {
        return new PlainDocument();
    }
    
    public void setTabSize(final int n) {
        final Document document = this.getDocument();
        if (document != null) {
            final int tabSize = this.getTabSize();
            document.putProperty("tabSize", n);
            this.firePropertyChange("tabSize", tabSize, n);
        }
    }
    
    public int getTabSize() {
        int intValue = 8;
        final Document document = this.getDocument();
        if (document != null) {
            final Integer n = (Integer)document.getProperty("tabSize");
            if (n != null) {
                intValue = n;
            }
        }
        return intValue;
    }
    
    public void setLineWrap(final boolean wrap) {
        this.firePropertyChange("lineWrap", this.wrap, this.wrap = wrap);
    }
    
    public boolean getLineWrap() {
        return this.wrap;
    }
    
    public void setWrapStyleWord(final boolean word) {
        this.firePropertyChange("wrapStyleWord", this.word, this.word = word);
    }
    
    public boolean getWrapStyleWord() {
        return this.word;
    }
    
    public int getLineOfOffset(final int n) throws BadLocationException {
        final Document document = this.getDocument();
        if (n < 0) {
            throw new BadLocationException("Can't translate offset to line", -1);
        }
        if (n > document.getLength()) {
            throw new BadLocationException("Can't translate offset to line", document.getLength() + 1);
        }
        return this.getDocument().getDefaultRootElement().getElementIndex(n);
    }
    
    public int getLineCount() {
        return this.getDocument().getDefaultRootElement().getElementCount();
    }
    
    public int getLineStartOffset(final int n) throws BadLocationException {
        final int lineCount = this.getLineCount();
        if (n < 0) {
            throw new BadLocationException("Negative line", -1);
        }
        if (n >= lineCount) {
            throw new BadLocationException("No such line", this.getDocument().getLength() + 1);
        }
        return this.getDocument().getDefaultRootElement().getElement(n).getStartOffset();
    }
    
    public int getLineEndOffset(final int n) throws BadLocationException {
        final int lineCount = this.getLineCount();
        if (n < 0) {
            throw new BadLocationException("Negative line", -1);
        }
        if (n >= lineCount) {
            throw new BadLocationException("No such line", this.getDocument().getLength() + 1);
        }
        final int endOffset = this.getDocument().getDefaultRootElement().getElement(n).getEndOffset();
        return (n == lineCount - 1) ? (endOffset - 1) : endOffset;
    }
    
    public void insert(final String s, final int n) {
        final Document document = this.getDocument();
        if (document != null) {
            try {
                document.insertString(n, s, null);
            }
            catch (final BadLocationException ex) {
                throw new IllegalArgumentException(ex.getMessage());
            }
        }
    }
    
    public void append(final String s) {
        final Document document = this.getDocument();
        if (document != null) {
            try {
                document.insertString(document.getLength(), s, null);
            }
            catch (final BadLocationException ex) {}
        }
    }
    
    public void replaceRange(final String s, final int n, final int n2) {
        if (n2 < n) {
            throw new IllegalArgumentException("end before start");
        }
        final Document document = this.getDocument();
        if (document != null) {
            try {
                if (document instanceof AbstractDocument) {
                    ((AbstractDocument)document).replace(n, n2 - n, s, null);
                }
                else {
                    document.remove(n, n2 - n);
                    document.insertString(n, s, null);
                }
            }
            catch (final BadLocationException ex) {
                throw new IllegalArgumentException(ex.getMessage());
            }
        }
    }
    
    public int getRows() {
        return this.rows;
    }
    
    public void setRows(final int rows) {
        final int rows2 = this.rows;
        if (rows < 0) {
            throw new IllegalArgumentException("rows less than zero.");
        }
        if (rows != rows2) {
            this.rows = rows;
            this.invalidate();
        }
    }
    
    protected int getRowHeight() {
        if (this.rowHeight == 0) {
            this.rowHeight = this.getFontMetrics(this.getFont()).getHeight();
        }
        return this.rowHeight;
    }
    
    public int getColumns() {
        return this.columns;
    }
    
    public void setColumns(final int columns) {
        final int columns2 = this.columns;
        if (columns < 0) {
            throw new IllegalArgumentException("columns less than zero.");
        }
        if (columns != columns2) {
            this.columns = columns;
            this.invalidate();
        }
    }
    
    protected int getColumnWidth() {
        if (this.columnWidth == 0) {
            this.columnWidth = this.getFontMetrics(this.getFont()).charWidth('m');
        }
        return this.columnWidth;
    }
    
    @Override
    public Dimension getPreferredSize() {
        final Dimension preferredSize = super.getPreferredSize();
        final Dimension dimension = (preferredSize == null) ? new Dimension(400, 400) : preferredSize;
        final Insets insets = this.getInsets();
        if (this.columns != 0) {
            dimension.width = Math.max(dimension.width, this.columns * this.getColumnWidth() + insets.left + insets.right);
        }
        if (this.rows != 0) {
            dimension.height = Math.max(dimension.height, this.rows * this.getRowHeight() + insets.top + insets.bottom);
        }
        return dimension;
    }
    
    @Override
    public void setFont(final Font font) {
        super.setFont(font);
        this.rowHeight = 0;
        this.columnWidth = 0;
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",colums=" + this.columns + ",columWidth=" + this.columnWidth + ",rows=" + this.rows + ",rowHeight=" + this.rowHeight + ",word=" + (this.word ? "true" : "false") + ",wrap=" + (this.wrap ? "true" : "false");
    }
    
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return this.wrap || super.getScrollableTracksViewportWidth();
    }
    
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        final Dimension preferredScrollableViewportSize = super.getPreferredScrollableViewportSize();
        final Dimension dimension = (preferredScrollableViewportSize == null) ? new Dimension(400, 400) : preferredScrollableViewportSize;
        final Insets insets = this.getInsets();
        dimension.width = ((this.columns == 0) ? dimension.width : (this.columns * this.getColumnWidth() + insets.left + insets.right));
        dimension.height = ((this.rows == 0) ? dimension.height : (this.rows * this.getRowHeight() + insets.top + insets.bottom));
        return dimension;
    }
    
    @Override
    public int getScrollableUnitIncrement(final Rectangle rectangle, final int n, final int n2) {
        switch (n) {
            case 1: {
                return this.getRowHeight();
            }
            case 0: {
                return this.getColumnWidth();
            }
            default: {
                throw new IllegalArgumentException("Invalid orientation: " + n);
            }
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("TextAreaUI")) {
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
            this.accessibleContext = new AccessibleJTextArea();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleJTextArea extends AccessibleJTextComponent
    {
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            accessibleStateSet.add(AccessibleState.MULTI_LINE);
            return accessibleStateSet;
        }
    }
}
