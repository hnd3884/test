package javax.swing.text;

import java.util.Enumeration;
import java.io.IOException;
import java.io.Writer;

public abstract class AbstractWriter
{
    private ElementIterator it;
    private Writer out;
    private int indentLevel;
    private int indentSpace;
    private Document doc;
    private int maxLineLength;
    private int currLength;
    private int startOffset;
    private int endOffset;
    private int offsetIndent;
    private String lineSeparator;
    private boolean canWrapLines;
    private boolean isLineEmpty;
    private char[] indentChars;
    private char[] tempChars;
    private char[] newlineChars;
    private Segment segment;
    protected static final char NEWLINE = '\n';
    
    protected AbstractWriter(final Writer writer, final Document document) {
        this(writer, document, 0, document.getLength());
    }
    
    protected AbstractWriter(final Writer out, final Document doc, final int startOffset, final int n) {
        this.indentLevel = 0;
        this.indentSpace = 2;
        this.doc = null;
        this.maxLineLength = 100;
        this.currLength = 0;
        this.startOffset = 0;
        this.endOffset = 0;
        this.offsetIndent = 0;
        this.doc = doc;
        this.it = new ElementIterator(doc.getDefaultRootElement());
        this.out = out;
        this.startOffset = startOffset;
        this.endOffset = startOffset + n;
        final Object property = doc.getProperty("__EndOfLine__");
        if (property instanceof String) {
            this.setLineSeparator((String)property);
        }
        else {
            String property2 = null;
            try {
                property2 = System.getProperty("line.separator");
            }
            catch (final SecurityException ex) {}
            if (property2 == null) {
                property2 = "\n";
            }
            this.setLineSeparator(property2);
        }
        this.canWrapLines = true;
    }
    
    protected AbstractWriter(final Writer writer, final Element element) {
        this(writer, element, 0, element.getEndOffset());
    }
    
    protected AbstractWriter(final Writer out, final Element element, final int startOffset, final int n) {
        this.indentLevel = 0;
        this.indentSpace = 2;
        this.doc = null;
        this.maxLineLength = 100;
        this.currLength = 0;
        this.startOffset = 0;
        this.endOffset = 0;
        this.offsetIndent = 0;
        this.doc = element.getDocument();
        this.it = new ElementIterator(element);
        this.out = out;
        this.startOffset = startOffset;
        this.endOffset = startOffset + n;
        this.canWrapLines = true;
    }
    
    public int getStartOffset() {
        return this.startOffset;
    }
    
    public int getEndOffset() {
        return this.endOffset;
    }
    
    protected ElementIterator getElementIterator() {
        return this.it;
    }
    
    protected Writer getWriter() {
        return this.out;
    }
    
    protected Document getDocument() {
        return this.doc;
    }
    
    protected boolean inRange(final Element element) {
        final int startOffset = this.getStartOffset();
        final int endOffset = this.getEndOffset();
        return (element.getStartOffset() >= startOffset && element.getStartOffset() < endOffset) || (startOffset >= element.getStartOffset() && startOffset < element.getEndOffset());
    }
    
    protected abstract void write() throws IOException, BadLocationException;
    
    protected String getText(final Element element) throws BadLocationException {
        return this.doc.getText(element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
    }
    
    protected void text(final Element element) throws BadLocationException, IOException {
        final int max = Math.max(this.getStartOffset(), element.getStartOffset());
        final int min = Math.min(this.getEndOffset(), element.getEndOffset());
        if (max < min) {
            if (this.segment == null) {
                this.segment = new Segment();
            }
            this.getDocument().getText(max, min - max, this.segment);
            if (this.segment.count > 0) {
                this.write(this.segment.array, this.segment.offset, this.segment.count);
            }
        }
    }
    
    protected void setLineLength(final int maxLineLength) {
        this.maxLineLength = maxLineLength;
    }
    
    protected int getLineLength() {
        return this.maxLineLength;
    }
    
    protected void setCurrentLineLength(final int currLength) {
        this.currLength = currLength;
        this.isLineEmpty = (this.currLength == 0);
    }
    
    protected int getCurrentLineLength() {
        return this.currLength;
    }
    
    protected boolean isLineEmpty() {
        return this.isLineEmpty;
    }
    
    protected void setCanWrapLines(final boolean canWrapLines) {
        this.canWrapLines = canWrapLines;
    }
    
    protected boolean getCanWrapLines() {
        return this.canWrapLines;
    }
    
    protected void setIndentSpace(final int indentSpace) {
        this.indentSpace = indentSpace;
    }
    
    protected int getIndentSpace() {
        return this.indentSpace;
    }
    
    public void setLineSeparator(final String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }
    
    public String getLineSeparator() {
        return this.lineSeparator;
    }
    
    protected void incrIndent() {
        if (this.offsetIndent > 0) {
            ++this.offsetIndent;
        }
        else if (++this.indentLevel * this.getIndentSpace() >= this.getLineLength()) {
            ++this.offsetIndent;
            --this.indentLevel;
        }
    }
    
    protected void decrIndent() {
        if (this.offsetIndent > 0) {
            --this.offsetIndent;
        }
        else {
            --this.indentLevel;
        }
    }
    
    protected int getIndentLevel() {
        return this.indentLevel;
    }
    
    protected void indent() throws IOException {
        final int n = this.getIndentLevel() * this.getIndentSpace();
        if (this.indentChars == null || n > this.indentChars.length) {
            this.indentChars = new char[n];
            for (int i = 0; i < n; ++i) {
                this.indentChars[i] = ' ';
            }
        }
        final int currentLineLength = this.getCurrentLineLength();
        final boolean lineEmpty = this.isLineEmpty();
        this.output(this.indentChars, 0, n);
        if (lineEmpty && currentLineLength == 0) {
            this.isLineEmpty = true;
        }
    }
    
    protected void write(final char c) throws IOException {
        if (this.tempChars == null) {
            this.tempChars = new char[128];
        }
        this.tempChars[0] = c;
        this.write(this.tempChars, 0, 1);
    }
    
    protected void write(final String s) throws IOException {
        if (s == null) {
            return;
        }
        final int length = s.length();
        if (this.tempChars == null || this.tempChars.length < length) {
            this.tempChars = new char[length];
        }
        s.getChars(0, length, this.tempChars, 0);
        this.write(this.tempChars, 0, length);
    }
    
    protected void writeLineSeparator() throws IOException {
        final String lineSeparator = this.getLineSeparator();
        final int length = lineSeparator.length();
        if (this.newlineChars == null || this.newlineChars.length < length) {
            this.newlineChars = new char[length];
        }
        lineSeparator.getChars(0, length, this.newlineChars, 0);
        this.output(this.newlineChars, 0, length);
        this.setCurrentLineLength(0);
    }
    
    protected void write(final char[] array, final int n, final int n2) throws IOException {
        if (!this.getCanWrapLines()) {
            int n3 = n;
            final int n4 = n + n2;
            for (int i = this.indexOf(array, '\n', n, n4); i != -1; i = this.indexOf(array, '\n', n3, n4)) {
                if (i > n3) {
                    this.output(array, n3, i - n3);
                }
                this.writeLineSeparator();
                n3 = i + 1;
            }
            if (n3 < n4) {
                this.output(array, n3, n4 - n3);
            }
        }
        else {
            int j = n;
            final int n5 = n + n2;
            this.getCurrentLineLength();
            final int lineLength = this.getLineLength();
            while (j < n5) {
                final int index = this.indexOf(array, '\n', j, n5);
                boolean b = false;
                boolean b2 = false;
                final int currentLineLength = this.getCurrentLineLength();
                if (index != -1 && currentLineLength + (index - j) < lineLength) {
                    if (index > j) {
                        this.output(array, j, index - j);
                    }
                    j = index + 1;
                    b2 = true;
                }
                else if (index == -1 && currentLineLength + (n5 - j) < lineLength) {
                    if (n5 > j) {
                        this.output(array, j, n5 - j);
                    }
                    j = n5;
                }
                else {
                    int n6 = -1;
                    final int min = Math.min(n5 - j, lineLength - currentLineLength - 1);
                    for (int k = 0; k < min; ++k) {
                        if (Character.isWhitespace(array[k + j])) {
                            n6 = k;
                        }
                    }
                    if (n6 != -1) {
                        final int n7 = n6 + (j + 1);
                        this.output(array, j, n7 - j);
                        j = n7;
                        b = true;
                    }
                    else {
                        for (int l = Math.max(0, min); l < n5 - j; ++l) {
                            if (Character.isWhitespace(array[l + j])) {
                                n6 = l;
                                break;
                            }
                        }
                        int n8;
                        if (n6 == -1) {
                            this.output(array, j, n5 - j);
                            n8 = n5;
                        }
                        else {
                            n8 = n6 + j;
                            if (array[n8] == '\n') {
                                this.output(array, j, n8++ - j);
                                b2 = true;
                            }
                            else {
                                this.output(array, j, ++n8 - j);
                                b = true;
                            }
                        }
                        j = n8;
                    }
                }
                if (b2 || b || j < n5) {
                    this.writeLineSeparator();
                    if (j >= n5 && b2) {
                        continue;
                    }
                    this.indent();
                }
            }
        }
    }
    
    protected void writeAttributes(final AttributeSet set) throws IOException {
        final Enumeration<?> attributeNames = set.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final Object nextElement = attributeNames.nextElement();
            this.write(" " + nextElement + "=" + set.getAttribute(nextElement));
        }
    }
    
    protected void output(final char[] array, final int n, final int n2) throws IOException {
        this.getWriter().write(array, n, n2);
        this.setCurrentLineLength(this.getCurrentLineLength() + n2);
    }
    
    private int indexOf(final char[] array, final char c, int i, final int n) {
        while (i < n) {
            if (array[i] == c) {
                return i;
            }
            ++i;
        }
        return -1;
    }
}
