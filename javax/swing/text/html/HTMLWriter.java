package javax.swing.text.html;

import javax.swing.text.Style;
import java.util.Enumeration;
import javax.swing.text.BadLocationException;
import java.io.IOException;
import javax.swing.text.ElementIterator;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Document;
import java.io.Writer;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Segment;
import java.util.Vector;
import javax.swing.text.Element;
import java.util.Stack;
import javax.swing.text.AbstractWriter;

public class HTMLWriter extends AbstractWriter
{
    private Stack<Element> blockElementStack;
    private boolean inContent;
    private boolean inPre;
    private int preEndOffset;
    private boolean inTextArea;
    private boolean newlineOutputed;
    private boolean completeDoc;
    private Vector<HTML.Tag> tags;
    private Vector<Object> tagValues;
    private Segment segment;
    private Vector<HTML.Tag> tagsToRemove;
    private boolean wroteHead;
    private boolean replaceEntities;
    private char[] tempChars;
    private boolean indentNext;
    private boolean writeCSS;
    private MutableAttributeSet convAttr;
    private MutableAttributeSet oConvAttr;
    private boolean indented;
    
    public HTMLWriter(final Writer writer, final HTMLDocument htmlDocument) {
        this(writer, htmlDocument, 0, htmlDocument.getLength());
    }
    
    public HTMLWriter(final Writer writer, final HTMLDocument htmlDocument, final int n, final int n2) {
        super(writer, htmlDocument, n, n2);
        this.blockElementStack = new Stack<Element>();
        this.inContent = false;
        this.inPre = false;
        this.inTextArea = false;
        this.newlineOutputed = false;
        this.tags = new Vector<HTML.Tag>(10);
        this.tagValues = new Vector<Object>(10);
        this.tagsToRemove = new Vector<HTML.Tag>(10);
        this.indentNext = false;
        this.writeCSS = false;
        this.convAttr = new SimpleAttributeSet();
        this.oConvAttr = new SimpleAttributeSet();
        this.indented = false;
        this.completeDoc = (n == 0 && n2 == htmlDocument.getLength());
        this.setLineLength(80);
    }
    
    public void write() throws IOException, BadLocationException {
        final ElementIterator elementIterator = this.getElementIterator();
        Element element = null;
        this.wroteHead = false;
        this.setCurrentLineLength(0);
        this.setCanWrapLines(this.replaceEntities = false);
        if (this.segment == null) {
            this.segment = new Segment();
        }
        this.inPre = false;
        boolean b = false;
        Element next;
        while ((next = elementIterator.next()) != null) {
            if (!this.inRange(next)) {
                if (!this.completeDoc || next.getAttributes().getAttribute(StyleConstants.NameAttribute) != HTML.Tag.BODY) {
                    continue;
                }
                b = true;
            }
            if (element != null) {
                if (this.indentNeedsIncrementing(element, next)) {
                    this.incrIndent();
                }
                else if (element.getParentElement() != next.getParentElement()) {
                    for (Element element2 = this.blockElementStack.peek(); element2 != next.getParentElement(); element2 = this.blockElementStack.peek()) {
                        this.blockElementStack.pop();
                        if (!this.synthesizedElement(element2)) {
                            final AttributeSet attributes = element2.getAttributes();
                            if (!this.matchNameAttribute(attributes, HTML.Tag.PRE) && !this.isFormElementWithContent(attributes)) {
                                this.decrIndent();
                            }
                            this.endTag(element2);
                        }
                    }
                }
                else if (element.getParentElement() == next.getParentElement()) {
                    final Element element3 = this.blockElementStack.peek();
                    if (element3 == element) {
                        this.blockElementStack.pop();
                        this.endTag(element3);
                    }
                }
            }
            if (!next.isLeaf() || this.isFormElementWithContent(next.getAttributes())) {
                this.blockElementStack.push(next);
                this.startTag(next);
            }
            else {
                this.emptyTag(next);
            }
            element = next;
        }
        this.closeOutUnwantedEmbeddedTags(null);
        if (b) {
            this.blockElementStack.pop();
            this.endTag(element);
        }
        while (!this.blockElementStack.empty()) {
            final Element element4 = this.blockElementStack.pop();
            if (!this.synthesizedElement(element4)) {
                final AttributeSet attributes2 = element4.getAttributes();
                if (!this.matchNameAttribute(attributes2, HTML.Tag.PRE) && !this.isFormElementWithContent(attributes2)) {
                    this.decrIndent();
                }
                this.endTag(element4);
            }
        }
        if (this.completeDoc) {
            this.writeAdditionalComments();
        }
        this.segment.array = null;
    }
    
    @Override
    protected void writeAttributes(final AttributeSet set) throws IOException {
        this.convAttr.removeAttributes(this.convAttr);
        convertToHTML32(set, this.convAttr);
        final Enumeration<?> attributeNames = this.convAttr.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final Object nextElement = attributeNames.nextElement();
            if (!(nextElement instanceof HTML.Tag) && !(nextElement instanceof StyleConstants)) {
                if (nextElement == HTML.Attribute.ENDTAG) {
                    continue;
                }
                this.write(" " + nextElement + "=\"" + this.convAttr.getAttribute(nextElement) + "\"");
            }
        }
    }
    
    protected void emptyTag(final Element element) throws BadLocationException, IOException {
        if (!this.inContent && !this.inPre) {
            this.indentSmart();
        }
        final AttributeSet attributes = element.getAttributes();
        this.closeOutUnwantedEmbeddedTags(attributes);
        this.writeEmbeddedTags(attributes);
        if (this.matchNameAttribute(attributes, HTML.Tag.CONTENT)) {
            this.inContent = true;
            this.text(element);
        }
        else if (this.matchNameAttribute(attributes, HTML.Tag.COMMENT)) {
            this.comment(element);
        }
        else {
            final boolean blockTag = this.isBlockTag(element.getAttributes());
            if (this.inContent && blockTag) {
                this.writeLineSeparator();
                this.indentSmart();
            }
            final Object o = (attributes != null) ? attributes.getAttribute(StyleConstants.NameAttribute) : null;
            final Object o2 = (attributes != null) ? attributes.getAttribute(HTML.Attribute.ENDTAG) : null;
            boolean b = false;
            if (o != null && o2 != null && o2 instanceof String && o2.equals("true")) {
                b = true;
            }
            if (this.completeDoc && this.matchNameAttribute(attributes, HTML.Tag.HEAD)) {
                if (b) {
                    this.writeStyles(((HTMLDocument)this.getDocument()).getStyleSheet());
                }
                this.wroteHead = true;
            }
            this.write('<');
            if (b) {
                this.write('/');
            }
            this.write(element.getName());
            this.writeAttributes(attributes);
            this.write('>');
            if (this.matchNameAttribute(attributes, HTML.Tag.TITLE) && !b) {
                this.write((String)element.getDocument().getProperty("title"));
            }
            else if (!this.inContent || blockTag) {
                this.writeLineSeparator();
                if (blockTag && this.inContent) {
                    this.indentSmart();
                }
            }
        }
    }
    
    protected boolean isBlockTag(final AttributeSet set) {
        final Object attribute = set.getAttribute(StyleConstants.NameAttribute);
        return attribute instanceof HTML.Tag && ((HTML.Tag)attribute).isBlock();
    }
    
    protected void startTag(final Element element) throws IOException, BadLocationException {
        if (this.synthesizedElement(element)) {
            return;
        }
        final AttributeSet attributes = element.getAttributes();
        final Object attribute = attributes.getAttribute(StyleConstants.NameAttribute);
        HTML.Tag tag;
        if (attribute instanceof HTML.Tag) {
            tag = (HTML.Tag)attribute;
        }
        else {
            tag = null;
        }
        if (tag == HTML.Tag.PRE) {
            this.inPre = true;
            this.preEndOffset = element.getEndOffset();
        }
        this.closeOutUnwantedEmbeddedTags(attributes);
        if (this.inContent) {
            this.writeLineSeparator();
            this.inContent = false;
            this.newlineOutputed = false;
        }
        if (this.completeDoc && tag == HTML.Tag.BODY && !this.wroteHead) {
            this.wroteHead = true;
            this.indentSmart();
            this.write("<head>");
            this.writeLineSeparator();
            this.incrIndent();
            this.writeStyles(((HTMLDocument)this.getDocument()).getStyleSheet());
            this.decrIndent();
            this.writeLineSeparator();
            this.indentSmart();
            this.write("</head>");
            this.writeLineSeparator();
        }
        this.indentSmart();
        this.write('<');
        this.write(element.getName());
        this.writeAttributes(attributes);
        this.write('>');
        if (tag != HTML.Tag.PRE) {
            this.writeLineSeparator();
        }
        if (tag == HTML.Tag.TEXTAREA) {
            this.textAreaContent(element.getAttributes());
        }
        else if (tag == HTML.Tag.SELECT) {
            this.selectContent(element.getAttributes());
        }
        else if (this.completeDoc && tag == HTML.Tag.BODY) {
            this.writeMaps(((HTMLDocument)this.getDocument()).getMaps());
        }
        else if (tag == HTML.Tag.HEAD) {
            final HTMLDocument htmlDocument = (HTMLDocument)this.getDocument();
            this.wroteHead = true;
            this.incrIndent();
            this.writeStyles(htmlDocument.getStyleSheet());
            if (htmlDocument.hasBaseTag()) {
                this.indentSmart();
                this.write("<base href=\"" + htmlDocument.getBase() + "\">");
                this.writeLineSeparator();
            }
            this.decrIndent();
        }
    }
    
    protected void textAreaContent(final AttributeSet set) throws BadLocationException, IOException {
        final Document document = (Document)set.getAttribute(StyleConstants.ModelAttribute);
        if (document != null && document.getLength() > 0) {
            if (this.segment == null) {
                this.segment = new Segment();
            }
            document.getText(0, document.getLength(), this.segment);
            if (this.segment.count > 0) {
                this.inTextArea = true;
                this.incrIndent();
                this.indentSmart();
                this.setCanWrapLines(true);
                this.replaceEntities = true;
                this.write(this.segment.array, this.segment.offset, this.segment.count);
                this.setCanWrapLines(this.replaceEntities = false);
                this.writeLineSeparator();
                this.inTextArea = false;
                this.decrIndent();
            }
        }
    }
    
    @Override
    protected void text(final Element element) throws BadLocationException, IOException {
        final int max = Math.max(this.getStartOffset(), element.getStartOffset());
        final int min = Math.min(this.getEndOffset(), element.getEndOffset());
        if (max < min) {
            if (this.segment == null) {
                this.segment = new Segment();
            }
            this.getDocument().getText(max, min - max, this.segment);
            this.newlineOutputed = false;
            if (this.segment.count > 0) {
                if (this.segment.array[this.segment.offset + this.segment.count - 1] == '\n') {
                    this.newlineOutputed = true;
                }
                if (this.inPre && min == this.preEndOffset) {
                    if (this.segment.count <= 1) {
                        return;
                    }
                    final Segment segment = this.segment;
                    --segment.count;
                }
                this.replaceEntities = true;
                this.setCanWrapLines(!this.inPre);
                this.write(this.segment.array, this.segment.offset, this.segment.count);
                this.setCanWrapLines(false);
                this.replaceEntities = false;
            }
        }
    }
    
    protected void selectContent(final AttributeSet set) throws IOException {
        final Object attribute = set.getAttribute(StyleConstants.ModelAttribute);
        this.incrIndent();
        if (attribute instanceof OptionListModel) {
            final OptionListModel optionListModel = (OptionListModel)attribute;
            for (int size = optionListModel.getSize(), i = 0; i < size; ++i) {
                this.writeOption((Option)optionListModel.getElementAt(i));
            }
        }
        else if (attribute instanceof OptionComboBoxModel) {
            final OptionComboBoxModel optionComboBoxModel = (OptionComboBoxModel)attribute;
            for (int size2 = optionComboBoxModel.getSize(), j = 0; j < size2; ++j) {
                this.writeOption((Option)optionComboBoxModel.getElementAt(j));
            }
        }
        this.decrIndent();
    }
    
    protected void writeOption(final Option option) throws IOException {
        this.indentSmart();
        this.write('<');
        this.write("option");
        final Object attribute = option.getAttributes().getAttribute(HTML.Attribute.VALUE);
        if (attribute != null) {
            this.write(" value=" + attribute);
        }
        if (option.isSelected()) {
            this.write(" selected");
        }
        this.write('>');
        if (option.getLabel() != null) {
            this.write(option.getLabel());
        }
        this.writeLineSeparator();
    }
    
    protected void endTag(final Element element) throws IOException {
        if (this.synthesizedElement(element)) {
            return;
        }
        this.closeOutUnwantedEmbeddedTags(element.getAttributes());
        if (this.inContent) {
            if (!this.newlineOutputed && !this.inPre) {
                this.writeLineSeparator();
            }
            this.newlineOutputed = false;
            this.inContent = false;
        }
        if (!this.inPre) {
            this.indentSmart();
        }
        if (this.matchNameAttribute(element.getAttributes(), HTML.Tag.PRE)) {
            this.inPre = false;
        }
        this.write('<');
        this.write('/');
        this.write(element.getName());
        this.write('>');
        this.writeLineSeparator();
    }
    
    protected void comment(final Element element) throws BadLocationException, IOException {
        final AttributeSet attributes = element.getAttributes();
        if (this.matchNameAttribute(attributes, HTML.Tag.COMMENT)) {
            final Object attribute = attributes.getAttribute(HTML.Attribute.COMMENT);
            if (attribute instanceof String) {
                this.writeComment((String)attribute);
            }
            else {
                this.writeComment(null);
            }
        }
    }
    
    void writeComment(final String s) throws IOException {
        this.write("<!--");
        if (s != null) {
            this.write(s);
        }
        this.write("-->");
        this.writeLineSeparator();
        this.indentSmart();
    }
    
    void writeAdditionalComments() throws IOException {
        final Object property = this.getDocument().getProperty("AdditionalComments");
        if (property instanceof Vector) {
            final Vector vector = (Vector)property;
            for (int i = 0; i < vector.size(); ++i) {
                this.writeComment(vector.elementAt(i).toString());
            }
        }
    }
    
    protected boolean synthesizedElement(final Element element) {
        return this.matchNameAttribute(element.getAttributes(), HTML.Tag.IMPLIED);
    }
    
    protected boolean matchNameAttribute(final AttributeSet set, final HTML.Tag tag) {
        final Object attribute = set.getAttribute(StyleConstants.NameAttribute);
        return attribute instanceof HTML.Tag && attribute == tag;
    }
    
    protected void writeEmbeddedTags(AttributeSet convertToHTML) throws IOException {
        convertToHTML = this.convertToHTML(convertToHTML, this.oConvAttr);
        final Enumeration<?> attributeNames = convertToHTML.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final Object nextElement = attributeNames.nextElement();
            if (nextElement instanceof HTML.Tag) {
                final HTML.Tag tag = (HTML.Tag)nextElement;
                if (tag == HTML.Tag.FORM) {
                    continue;
                }
                if (this.tags.contains(tag)) {
                    continue;
                }
                this.write('<');
                this.write(tag.toString());
                final Object attribute = convertToHTML.getAttribute(tag);
                if (attribute != null && attribute instanceof AttributeSet) {
                    this.writeAttributes((AttributeSet)attribute);
                }
                this.write('>');
                this.tags.addElement(tag);
                this.tagValues.addElement(attribute);
            }
        }
    }
    
    private boolean noMatchForTagInAttributes(final AttributeSet set, final HTML.Tag tag, final Object o) {
        if (set != null && set.isDefined(tag)) {
            final Object attribute = set.getAttribute(tag);
            if (o == null) {
                if (attribute != null) {
                    return true;
                }
            }
            else if (attribute == null || !o.equals(attribute)) {
                return true;
            }
            return false;
        }
        return true;
    }
    
    protected void closeOutUnwantedEmbeddedTags(AttributeSet convertToHTML) throws IOException {
        this.tagsToRemove.removeAllElements();
        convertToHTML = this.convertToHTML(convertToHTML, null);
        int n = -1;
        final int size = this.tags.size();
        for (int i = size - 1; i >= 0; --i) {
            final HTML.Tag tag = this.tags.elementAt(i);
            final Object element = this.tagValues.elementAt(i);
            if (convertToHTML == null || this.noMatchForTagInAttributes(convertToHTML, tag, element)) {
                n = i;
                this.tagsToRemove.addElement(tag);
            }
        }
        if (n != -1) {
            final boolean b = size - n == this.tagsToRemove.size();
            for (int j = size - 1; j >= n; --j) {
                final HTML.Tag tag2 = this.tags.elementAt(j);
                if (b || this.tagsToRemove.contains(tag2)) {
                    this.tags.removeElementAt(j);
                    this.tagValues.removeElementAt(j);
                }
                this.write('<');
                this.write('/');
                this.write(tag2.toString());
                this.write('>');
            }
            for (int size2 = this.tags.size(), k = n; k < size2; ++k) {
                final HTML.Tag tag3 = this.tags.elementAt(k);
                this.write('<');
                this.write(tag3.toString());
                final Object element2 = this.tagValues.elementAt(k);
                if (element2 != null && element2 instanceof AttributeSet) {
                    this.writeAttributes((AttributeSet)element2);
                }
                this.write('>');
            }
        }
    }
    
    private boolean isFormElementWithContent(final AttributeSet set) {
        return this.matchNameAttribute(set, HTML.Tag.TEXTAREA) || this.matchNameAttribute(set, HTML.Tag.SELECT);
    }
    
    private boolean indentNeedsIncrementing(final Element element, final Element element2) {
        if (element2.getParentElement() == element && !this.inPre) {
            if (this.indentNext) {
                this.indentNext = false;
                return true;
            }
            if (this.synthesizedElement(element2)) {
                this.indentNext = true;
            }
            else if (!this.synthesizedElement(element)) {
                return true;
            }
        }
        return false;
    }
    
    void writeMaps(final Enumeration enumeration) throws IOException {
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                final Map map = enumeration.nextElement();
                final String name = map.getName();
                this.incrIndent();
                this.indentSmart();
                this.write("<map");
                if (name != null) {
                    this.write(" name=\"");
                    this.write(name);
                    this.write("\">");
                }
                else {
                    this.write('>');
                }
                this.writeLineSeparator();
                this.incrIndent();
                final AttributeSet[] areas = map.getAreas();
                if (areas != null) {
                    for (int i = 0; i < areas.length; ++i) {
                        this.indentSmart();
                        this.write("<area");
                        this.writeAttributes(areas[i]);
                        this.write("></area>");
                        this.writeLineSeparator();
                    }
                }
                this.decrIndent();
                this.indentSmart();
                this.write("</map>");
                this.writeLineSeparator();
                this.decrIndent();
            }
        }
    }
    
    void writeStyles(final StyleSheet styleSheet) throws IOException {
        if (styleSheet != null) {
            final Enumeration<?> styleNames = styleSheet.getStyleNames();
            if (styleNames != null) {
                boolean b = false;
                while (styleNames.hasMoreElements()) {
                    final String s = (String)styleNames.nextElement();
                    if (!"default".equals(s) && this.writeStyle(s, styleSheet.getStyle(s), b)) {
                        b = true;
                    }
                }
                if (b) {
                    this.writeStyleEndTag();
                }
            }
        }
    }
    
    boolean writeStyle(final String s, final Style style, boolean b) throws IOException {
        boolean b2 = false;
        final Enumeration<?> attributeNames = style.getAttributeNames();
        if (attributeNames != null) {
            while (attributeNames.hasMoreElements()) {
                final Object nextElement = attributeNames.nextElement();
                if (nextElement instanceof CSS.Attribute) {
                    final String string = style.getAttribute(nextElement).toString();
                    if (string == null) {
                        continue;
                    }
                    if (!b) {
                        this.writeStyleStartTag();
                        b = true;
                    }
                    if (!b2) {
                        b2 = true;
                        this.indentSmart();
                        this.write(s);
                        this.write(" {");
                    }
                    else {
                        this.write(";");
                    }
                    this.write(' ');
                    this.write(nextElement.toString());
                    this.write(": ");
                    this.write(string);
                }
            }
        }
        if (b2) {
            this.write(" }");
            this.writeLineSeparator();
        }
        return b2;
    }
    
    void writeStyleStartTag() throws IOException {
        this.indentSmart();
        this.write("<style type=\"text/css\">");
        this.incrIndent();
        this.writeLineSeparator();
        this.indentSmart();
        this.write("<!--");
        this.incrIndent();
        this.writeLineSeparator();
    }
    
    void writeStyleEndTag() throws IOException {
        this.decrIndent();
        this.indentSmart();
        this.write("-->");
        this.writeLineSeparator();
        this.decrIndent();
        this.indentSmart();
        this.write("</style>");
        this.writeLineSeparator();
        this.indentSmart();
    }
    
    AttributeSet convertToHTML(final AttributeSet set, MutableAttributeSet convAttr) {
        if (convAttr == null) {
            convAttr = this.convAttr;
        }
        convAttr.removeAttributes(convAttr);
        if (this.writeCSS) {
            convertToHTML40(set, convAttr);
        }
        else {
            convertToHTML32(set, convAttr);
        }
        return convAttr;
    }
    
    private static void convertToHTML32(final AttributeSet set, final MutableAttributeSet set2) {
        if (set == null) {
            return;
        }
        final Enumeration<?> attributeNames = set.getAttributeNames();
        String s = "";
        while (attributeNames.hasMoreElements()) {
            final Object nextElement = attributeNames.nextElement();
            if (nextElement instanceof CSS.Attribute) {
                if (nextElement == CSS.Attribute.FONT_FAMILY || nextElement == CSS.Attribute.FONT_SIZE || nextElement == CSS.Attribute.COLOR) {
                    createFontAttribute((CSS.Attribute)nextElement, set, set2);
                }
                else if (nextElement == CSS.Attribute.FONT_WEIGHT) {
                    final CSS.FontWeight fontWeight = (CSS.FontWeight)set.getAttribute(CSS.Attribute.FONT_WEIGHT);
                    if (fontWeight == null || fontWeight.getValue() <= 400) {
                        continue;
                    }
                    addAttribute(set2, HTML.Tag.B, SimpleAttributeSet.EMPTY);
                }
                else if (nextElement == CSS.Attribute.FONT_STYLE) {
                    if (set.getAttribute(nextElement).toString().indexOf("italic") < 0) {
                        continue;
                    }
                    addAttribute(set2, HTML.Tag.I, SimpleAttributeSet.EMPTY);
                }
                else if (nextElement == CSS.Attribute.TEXT_DECORATION) {
                    final String string = set.getAttribute(nextElement).toString();
                    if (string.indexOf("underline") >= 0) {
                        addAttribute(set2, HTML.Tag.U, SimpleAttributeSet.EMPTY);
                    }
                    if (string.indexOf("line-through") < 0) {
                        continue;
                    }
                    addAttribute(set2, HTML.Tag.STRIKE, SimpleAttributeSet.EMPTY);
                }
                else if (nextElement == CSS.Attribute.VERTICAL_ALIGN) {
                    final String string2 = set.getAttribute(nextElement).toString();
                    if (string2.indexOf("sup") >= 0) {
                        addAttribute(set2, HTML.Tag.SUP, SimpleAttributeSet.EMPTY);
                    }
                    if (string2.indexOf("sub") < 0) {
                        continue;
                    }
                    addAttribute(set2, HTML.Tag.SUB, SimpleAttributeSet.EMPTY);
                }
                else if (nextElement == CSS.Attribute.TEXT_ALIGN) {
                    addAttribute(set2, HTML.Attribute.ALIGN, set.getAttribute(nextElement).toString());
                }
                else {
                    if (s.length() > 0) {
                        s += "; ";
                    }
                    s = s + nextElement + ": " + set.getAttribute(nextElement);
                }
            }
            else {
                Object o = set.getAttribute(nextElement);
                if (o instanceof AttributeSet) {
                    o = ((AttributeSet)o).copyAttributes();
                }
                addAttribute(set2, nextElement, o);
            }
        }
        if (s.length() > 0) {
            set2.addAttribute(HTML.Attribute.STYLE, s);
        }
    }
    
    private static void addAttribute(final MutableAttributeSet set, final Object o, final Object o2) {
        final Object attribute = set.getAttribute(o);
        if (attribute == null || attribute == SimpleAttributeSet.EMPTY) {
            set.addAttribute(o, o2);
        }
        else if (attribute instanceof MutableAttributeSet && o2 instanceof AttributeSet) {
            ((MutableAttributeSet)attribute).addAttributes((AttributeSet)o2);
        }
    }
    
    private static void createFontAttribute(final CSS.Attribute attribute, final AttributeSet set, final MutableAttributeSet set2) {
        MutableAttributeSet set3 = (MutableAttributeSet)set2.getAttribute(HTML.Tag.FONT);
        if (set3 == null) {
            set3 = new SimpleAttributeSet();
            set2.addAttribute(HTML.Tag.FONT, set3);
        }
        final String string = set.getAttribute(attribute).toString();
        if (attribute == CSS.Attribute.FONT_FAMILY) {
            set3.addAttribute(HTML.Attribute.FACE, string);
        }
        else if (attribute == CSS.Attribute.FONT_SIZE) {
            set3.addAttribute(HTML.Attribute.SIZE, string);
        }
        else if (attribute == CSS.Attribute.COLOR) {
            set3.addAttribute(HTML.Attribute.COLOR, string);
        }
    }
    
    private static void convertToHTML40(final AttributeSet set, final MutableAttributeSet set2) {
        final Enumeration<?> attributeNames = set.getAttributeNames();
        String string = "";
        while (attributeNames.hasMoreElements()) {
            final Object nextElement = attributeNames.nextElement();
            if (nextElement instanceof CSS.Attribute) {
                string = string + " " + nextElement + "=" + set.getAttribute(nextElement) + ";";
            }
            else {
                set2.addAttribute(nextElement, set.getAttribute(nextElement));
            }
        }
        if (string.length() > 0) {
            set2.addAttribute(HTML.Attribute.STYLE, string);
        }
    }
    
    @Override
    protected void writeLineSeparator() throws IOException {
        final boolean replaceEntities = this.replaceEntities;
        this.replaceEntities = false;
        super.writeLineSeparator();
        this.replaceEntities = replaceEntities;
        this.indented = false;
    }
    
    @Override
    protected void output(final char[] array, final int n, int n2) throws IOException {
        if (!this.replaceEntities) {
            super.output(array, n, n2);
            return;
        }
        int n3 = n;
        n2 += n;
        for (int i = n; i < n2; ++i) {
            switch (array[i]) {
                case '<': {
                    if (i > n3) {
                        super.output(array, n3, i - n3);
                    }
                    n3 = i + 1;
                    this.output("&lt;");
                    break;
                }
                case '>': {
                    if (i > n3) {
                        super.output(array, n3, i - n3);
                    }
                    n3 = i + 1;
                    this.output("&gt;");
                    break;
                }
                case '&': {
                    if (i > n3) {
                        super.output(array, n3, i - n3);
                    }
                    n3 = i + 1;
                    this.output("&amp;");
                    break;
                }
                case '\"': {
                    if (i > n3) {
                        super.output(array, n3, i - n3);
                    }
                    n3 = i + 1;
                    this.output("&quot;");
                    break;
                }
                case '\t':
                case '\n':
                case '\r': {
                    break;
                }
                default: {
                    if (array[i] < ' ' || array[i] > '\u007f') {
                        if (i > n3) {
                            super.output(array, n3, i - n3);
                        }
                        n3 = i + 1;
                        this.output("&#");
                        this.output(String.valueOf((int)array[i]));
                        this.output(";");
                        break;
                    }
                    break;
                }
            }
        }
        if (n3 < n2) {
            super.output(array, n3, n2 - n3);
        }
    }
    
    private void output(final String s) throws IOException {
        final int length = s.length();
        if (this.tempChars == null || this.tempChars.length < length) {
            this.tempChars = new char[length];
        }
        s.getChars(0, length, this.tempChars, 0);
        super.output(this.tempChars, 0, length);
    }
    
    private void indentSmart() throws IOException {
        if (!this.indented) {
            this.indent();
            this.indented = true;
        }
    }
}
