package javax.swing.text.html;

import java.awt.Color;
import javax.swing.text.StyleContext;
import javax.swing.text.ElementIterator;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Style;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import java.util.Enumeration;
import javax.swing.text.StyleConstants;
import javax.swing.text.BadLocationException;
import java.io.IOException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import java.io.Writer;
import java.util.Hashtable;
import javax.swing.text.AttributeSet;
import javax.swing.text.AbstractWriter;

public class MinimalHTMLWriter extends AbstractWriter
{
    private static final int BOLD = 1;
    private static final int ITALIC = 2;
    private static final int UNDERLINE = 4;
    private static final CSS css;
    private int fontMask;
    int startOffset;
    int endOffset;
    private AttributeSet fontAttributes;
    private Hashtable<String, String> styleNameMapping;
    
    public MinimalHTMLWriter(final Writer writer, final StyledDocument styledDocument) {
        super(writer, styledDocument);
        this.fontMask = 0;
        this.startOffset = 0;
        this.endOffset = 0;
    }
    
    public MinimalHTMLWriter(final Writer writer, final StyledDocument styledDocument, final int n, final int n2) {
        super(writer, styledDocument, n, n2);
        this.fontMask = 0;
        this.startOffset = 0;
        this.endOffset = 0;
    }
    
    public void write() throws IOException, BadLocationException {
        this.styleNameMapping = new Hashtable<String, String>();
        this.writeStartTag("<html>");
        this.writeHeader();
        this.writeBody();
        this.writeEndTag("</html>");
    }
    
    @Override
    protected void writeAttributes(final AttributeSet set) throws IOException {
        final Enumeration<?> attributeNames = set.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final Object nextElement = attributeNames.nextElement();
            if (nextElement instanceof StyleConstants.ParagraphConstants || nextElement instanceof StyleConstants.CharacterConstants || nextElement instanceof StyleConstants.FontConstants || nextElement instanceof StyleConstants.ColorConstants) {
                this.indent();
                this.write(nextElement.toString());
                this.write(':');
                this.write(MinimalHTMLWriter.css.styleConstantsValueToCSSValue((StyleConstants)nextElement, set.getAttribute(nextElement)).toString());
                this.write(';');
                this.write('\n');
            }
        }
    }
    
    @Override
    protected void text(final Element element) throws IOException, BadLocationException {
        String s = this.getText(element);
        if (s.length() > 0 && s.charAt(s.length() - 1) == '\n') {
            s = s.substring(0, s.length() - 1);
        }
        if (s.length() > 0) {
            this.write(s);
        }
    }
    
    protected void writeStartTag(final String s) throws IOException {
        this.indent();
        this.write(s);
        this.write('\n');
        this.incrIndent();
    }
    
    protected void writeEndTag(final String s) throws IOException {
        this.decrIndent();
        this.indent();
        this.write(s);
        this.write('\n');
    }
    
    protected void writeHeader() throws IOException {
        this.writeStartTag("<head>");
        this.writeStartTag("<style>");
        this.writeStartTag("<!--");
        this.writeStyles();
        this.writeEndTag("-->");
        this.writeEndTag("</style>");
        this.writeEndTag("</head>");
    }
    
    protected void writeStyles() throws IOException {
        final DefaultStyledDocument defaultStyledDocument = (DefaultStyledDocument)this.getDocument();
        final Enumeration<?> styleNames = defaultStyledDocument.getStyleNames();
        while (styleNames.hasMoreElements()) {
            final Style style = defaultStyledDocument.getStyle((String)styleNames.nextElement());
            if (style.getAttributeCount() == 1 && style.isDefined(StyleConstants.NameAttribute)) {
                continue;
            }
            this.indent();
            this.write("p." + this.addStyleName(style.getName()));
            this.write(" {\n");
            this.incrIndent();
            this.writeAttributes(style);
            this.decrIndent();
            this.indent();
            this.write("}\n");
        }
    }
    
    protected void writeBody() throws IOException, BadLocationException {
        final ElementIterator elementIterator = this.getElementIterator();
        elementIterator.current();
        this.writeStartTag("<body>");
        int n = 0;
        Element next;
        while ((next = elementIterator.next()) != null) {
            if (!this.inRange(next)) {
                continue;
            }
            if (next instanceof AbstractDocument.BranchElement) {
                if (n != 0) {
                    this.writeEndParagraph();
                    n = 0;
                    this.fontMask = 0;
                }
                this.writeStartParagraph(next);
            }
            else if (this.isText(next)) {
                this.writeContent(next, n == 0);
                n = 1;
            }
            else {
                this.writeLeaf(next);
                n = 1;
            }
        }
        if (n != 0) {
            this.writeEndParagraph();
        }
        this.writeEndTag("</body>");
    }
    
    protected void writeEndParagraph() throws IOException {
        this.writeEndMask(this.fontMask);
        if (this.inFontTag()) {
            this.endSpanTag();
        }
        else {
            this.write('\n');
        }
        this.writeEndTag("</p>");
    }
    
    protected void writeStartParagraph(final Element element) throws IOException {
        final Object attribute = element.getAttributes().getAttribute(StyleConstants.ResolveAttribute);
        if (attribute instanceof StyleContext.NamedStyle) {
            this.writeStartTag("<p class=" + this.mapStyleName(((StyleContext.NamedStyle)attribute).getName()) + ">");
        }
        else {
            this.writeStartTag("<p>");
        }
    }
    
    protected void writeLeaf(final Element element) throws IOException {
        this.indent();
        if (element.getName() == "icon") {
            this.writeImage(element);
        }
        else if (element.getName() == "component") {
            this.writeComponent(element);
        }
    }
    
    protected void writeImage(final Element element) throws IOException {
    }
    
    protected void writeComponent(final Element element) throws IOException {
    }
    
    protected boolean isText(final Element element) {
        return element.getName() == "content";
    }
    
    protected void writeContent(final Element element, final boolean b) throws IOException, BadLocationException {
        final AttributeSet attributes = element.getAttributes();
        this.writeNonHTMLAttributes(attributes);
        if (b) {
            this.indent();
        }
        this.writeHTMLTags(attributes);
        this.text(element);
    }
    
    protected void writeHTMLTags(final AttributeSet fontMask) throws IOException {
        final int fontMask2 = this.fontMask;
        this.setFontMask(fontMask);
        int n = 0;
        int n2 = 0;
        if ((fontMask2 & 0x1) != 0x0) {
            if ((this.fontMask & 0x1) == 0x0) {
                n |= 0x1;
            }
        }
        else if ((this.fontMask & 0x1) != 0x0) {
            n2 |= 0x1;
        }
        if ((fontMask2 & 0x2) != 0x0) {
            if ((this.fontMask & 0x2) == 0x0) {
                n |= 0x2;
            }
        }
        else if ((this.fontMask & 0x2) != 0x0) {
            n2 |= 0x2;
        }
        if ((fontMask2 & 0x4) != 0x0) {
            if ((this.fontMask & 0x4) == 0x0) {
                n |= 0x4;
            }
        }
        else if ((this.fontMask & 0x4) != 0x0) {
            n2 |= 0x4;
        }
        this.writeEndMask(n);
        this.writeStartMask(n2);
    }
    
    private void setFontMask(final AttributeSet set) {
        if (StyleConstants.isBold(set)) {
            this.fontMask |= 0x1;
        }
        if (StyleConstants.isItalic(set)) {
            this.fontMask |= 0x2;
        }
        if (StyleConstants.isUnderline(set)) {
            this.fontMask |= 0x4;
        }
    }
    
    private void writeStartMask(final int n) throws IOException {
        if (n != 0) {
            if ((n & 0x4) != 0x0) {
                this.write("<u>");
            }
            if ((n & 0x2) != 0x0) {
                this.write("<i>");
            }
            if ((n & 0x1) != 0x0) {
                this.write("<b>");
            }
        }
    }
    
    private void writeEndMask(final int n) throws IOException {
        if (n != 0) {
            if ((n & 0x1) != 0x0) {
                this.write("</b>");
            }
            if ((n & 0x2) != 0x0) {
                this.write("</i>");
            }
            if ((n & 0x4) != 0x0) {
                this.write("</u>");
            }
        }
    }
    
    protected void writeNonHTMLAttributes(final AttributeSet fontAttributes) throws IOException {
        String s = "";
        final String s2 = "; ";
        if (this.inFontTag() && this.fontAttributes.isEqual(fontAttributes)) {
            return;
        }
        int n = 1;
        final Color color = (Color)fontAttributes.getAttribute(StyleConstants.Foreground);
        if (color != null) {
            s = s + "color: " + MinimalHTMLWriter.css.styleConstantsValueToCSSValue((StyleConstants)StyleConstants.Foreground, color);
            n = 0;
        }
        final Integer n2 = (Integer)fontAttributes.getAttribute(StyleConstants.FontSize);
        if (n2 != null) {
            if (n == 0) {
                s += s2;
            }
            s = s + "font-size: " + (int)n2 + "pt";
            n = 0;
        }
        final String s3 = (String)fontAttributes.getAttribute(StyleConstants.FontFamily);
        if (s3 != null) {
            if (n == 0) {
                s += s2;
            }
            s = s + "font-family: " + s3;
        }
        if (s.length() > 0) {
            if (this.fontMask != 0) {
                this.writeEndMask(this.fontMask);
                this.fontMask = 0;
            }
            this.startSpanTag(s);
            this.fontAttributes = fontAttributes;
        }
        else if (this.fontAttributes != null) {
            this.writeEndMask(this.fontMask);
            this.fontMask = 0;
            this.endSpanTag();
        }
    }
    
    protected boolean inFontTag() {
        return this.fontAttributes != null;
    }
    
    protected void endFontTag() throws IOException {
        this.write('\n');
        this.writeEndTag("</font>");
        this.fontAttributes = null;
    }
    
    protected void startFontTag(final String s) throws IOException {
        boolean b = false;
        if (this.inFontTag()) {
            this.endFontTag();
            b = true;
        }
        this.writeStartTag("<font style=\"" + s + "\">");
        if (b) {
            this.indent();
        }
    }
    
    private void startSpanTag(final String s) throws IOException {
        boolean b = false;
        if (this.inFontTag()) {
            this.endSpanTag();
            b = true;
        }
        this.writeStartTag("<span style=\"" + s + "\">");
        if (b) {
            this.indent();
        }
    }
    
    private void endSpanTag() throws IOException {
        this.write('\n');
        this.writeEndTag("</span>");
        this.fontAttributes = null;
    }
    
    private String addStyleName(final String s) {
        if (this.styleNameMapping == null) {
            return s;
        }
        StringBuilder sb = null;
        for (int i = s.length() - 1; i >= 0; --i) {
            if (!this.isValidCharacter(s.charAt(i))) {
                if (sb == null) {
                    sb = new StringBuilder(s);
                }
                sb.setCharAt(i, 'a');
            }
        }
        String string;
        for (string = ((sb != null) ? sb.toString() : s); this.styleNameMapping.get(string) != null; string += 'x') {}
        this.styleNameMapping.put(s, string);
        return string;
    }
    
    private String mapStyleName(final String s) {
        if (this.styleNameMapping == null) {
            return s;
        }
        final String s2 = this.styleNameMapping.get(s);
        return (s2 == null) ? s : s2;
    }
    
    private boolean isValidCharacter(final char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    
    static {
        css = new CSS();
    }
}
