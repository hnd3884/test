package javax.swing.text.html;

import javax.swing.JToggleButton;
import javax.swing.text.PlainDocument;
import javax.swing.DefaultButtonModel;
import java.net.MalformedURLException;
import sun.swing.SwingUtilities2;
import java.awt.font.TextAttribute;
import java.util.Stack;
import java.util.Vector;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.IOException;
import javax.swing.text.ElementIterator;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;
import javax.swing.event.DocumentEvent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleContext;
import javax.swing.text.AbstractDocument;
import javax.swing.text.GapContent;
import javax.swing.text.AttributeSet;
import java.net.URL;
import javax.swing.ButtonGroup;
import java.util.HashMap;
import javax.swing.text.DefaultStyledDocument;

public class HTMLDocument extends DefaultStyledDocument
{
    private boolean frameDocument;
    private boolean preservesUnknownTags;
    private HashMap<String, ButtonGroup> radioButtonGroupsMap;
    static final String TokenThreshold = "token threshold";
    private static final int MaxThreshold = 10000;
    private static final int StepThreshold = 5;
    public static final String AdditionalComments = "AdditionalComments";
    static final String StyleType = "StyleType";
    URL base;
    boolean hasBaseTag;
    private String baseTarget;
    private HTMLEditorKit.Parser parser;
    private static AttributeSet contentAttributeSet;
    static String MAP_PROPERTY;
    private static char[] NEWLINE;
    private boolean insertInBody;
    private static final String I18NProperty = "i18n";
    
    public HTMLDocument() {
        this(new GapContent(4096), new StyleSheet());
    }
    
    public HTMLDocument(final StyleSheet styleSheet) {
        this(new GapContent(4096), styleSheet);
    }
    
    public HTMLDocument(final Content content, final StyleSheet styleSheet) {
        super(content, styleSheet);
        this.frameDocument = false;
        this.preservesUnknownTags = true;
        this.hasBaseTag = false;
        this.baseTarget = null;
        this.insertInBody = false;
    }
    
    public HTMLEditorKit.ParserCallback getReader(final int n) {
        final Object property = this.getProperty("stream");
        if (property instanceof URL) {
            this.setBase((URL)property);
        }
        return new HTMLReader(n);
    }
    
    public HTMLEditorKit.ParserCallback getReader(final int n, final int n2, final int n3, final HTML.Tag tag) {
        return this.getReader(n, n2, n3, tag, true);
    }
    
    HTMLEditorKit.ParserCallback getReader(final int n, final int n2, final int n3, final HTML.Tag tag, final boolean b) {
        final Object property = this.getProperty("stream");
        if (property instanceof URL) {
            this.setBase((URL)property);
        }
        return new HTMLReader(n, n2, n3, tag, b, false, true);
    }
    
    public URL getBase() {
        return this.base;
    }
    
    public void setBase(final URL url) {
        this.base = url;
        this.getStyleSheet().setBase(url);
    }
    
    @Override
    protected void insert(final int n, final ElementSpec[] array) throws BadLocationException {
        super.insert(n, array);
    }
    
    @Override
    protected void insertUpdate(final DefaultDocumentEvent defaultDocumentEvent, AttributeSet contentAttributeSet) {
        if (contentAttributeSet == null) {
            contentAttributeSet = HTMLDocument.contentAttributeSet;
        }
        else if (contentAttributeSet.isDefined(StyleConstants.ComposedTextAttribute)) {
            ((MutableAttributeSet)contentAttributeSet).addAttributes(HTMLDocument.contentAttributeSet);
        }
        if (contentAttributeSet.isDefined("CR")) {
            ((MutableAttributeSet)contentAttributeSet).removeAttribute("CR");
        }
        super.insertUpdate(defaultDocumentEvent, contentAttributeSet);
    }
    
    @Override
    protected void create(final ElementSpec[] array) {
        super.create(array);
    }
    
    @Override
    public void setParagraphAttributes(int startOffset, int max, final AttributeSet set, final boolean b) {
        try {
            this.writeLock();
            final int min = Math.min(startOffset + max, this.getLength());
            startOffset = this.getParagraphElement(startOffset).getStartOffset();
            max = Math.max(0, this.getParagraphElement(min).getEndOffset() - startOffset);
            final DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(startOffset, max, DocumentEvent.EventType.CHANGE);
            final AttributeSet copyAttributes = set.copyAttributes();
            for (int endOffset = Integer.MAX_VALUE, i = startOffset; i <= min; i = endOffset) {
                final Element paragraphElement = this.getParagraphElement(i);
                if (endOffset == paragraphElement.getEndOffset()) {
                    ++endOffset;
                }
                else {
                    endOffset = paragraphElement.getEndOffset();
                }
                final MutableAttributeSet set2 = (MutableAttributeSet)paragraphElement.getAttributes();
                defaultDocumentEvent.addEdit(new AttributeUndoableEdit(paragraphElement, copyAttributes, b));
                if (b) {
                    set2.removeAttributes(set2);
                }
                set2.addAttributes(set);
            }
            defaultDocumentEvent.end();
            this.fireChangedUpdate(defaultDocumentEvent);
            this.fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
        }
        finally {
            this.writeUnlock();
        }
    }
    
    public StyleSheet getStyleSheet() {
        return (StyleSheet)this.getAttributeContext();
    }
    
    public Iterator getIterator(final HTML.Tag tag) {
        if (tag.isBlock()) {
            return null;
        }
        return new LeafIterator(tag, this);
    }
    
    @Override
    protected Element createLeafElement(final Element element, final AttributeSet set, final int n, final int n2) {
        return new RunElement(element, set, n, n2);
    }
    
    @Override
    protected Element createBranchElement(final Element element, final AttributeSet set) {
        return new BlockElement(element, set);
    }
    
    @Override
    protected AbstractElement createDefaultRoot() {
        this.writeLock();
        final SimpleAttributeSet set = new SimpleAttributeSet();
        set.addAttribute(StyleConstants.NameAttribute, HTML.Tag.HTML);
        final BlockElement blockElement = new BlockElement(null, set.copyAttributes());
        set.removeAttributes(set);
        set.addAttribute(StyleConstants.NameAttribute, HTML.Tag.BODY);
        final BlockElement blockElement2 = new BlockElement(blockElement, set.copyAttributes());
        set.removeAttributes(set);
        set.addAttribute(StyleConstants.NameAttribute, HTML.Tag.P);
        this.getStyleSheet().addCSSAttributeFromHTML(set, CSS.Attribute.MARGIN_TOP, "0");
        final BlockElement blockElement3 = new BlockElement(blockElement2, set.copyAttributes());
        set.removeAttributes(set);
        set.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
        final Element[] array = { new RunElement(blockElement3, set, 0, 1) };
        blockElement3.replace(0, 0, array);
        array[0] = blockElement3;
        blockElement2.replace(0, 0, array);
        array[0] = blockElement2;
        blockElement.replace(0, 0, array);
        this.writeUnlock();
        return blockElement;
    }
    
    public void setTokenThreshold(final int n) {
        this.putProperty("token threshold", new Integer(n));
    }
    
    public int getTokenThreshold() {
        final Integer n = (Integer)this.getProperty("token threshold");
        if (n != null) {
            return n;
        }
        return Integer.MAX_VALUE;
    }
    
    public void setPreservesUnknownTags(final boolean preservesUnknownTags) {
        this.preservesUnknownTags = preservesUnknownTags;
    }
    
    public boolean getPreservesUnknownTags() {
        return this.preservesUnknownTags;
    }
    
    public void processHTMLFrameHyperlinkEvent(final HTMLFrameHyperlinkEvent htmlFrameHyperlinkEvent) {
        final String target = htmlFrameHyperlinkEvent.getTarget();
        final Element sourceElement = htmlFrameHyperlinkEvent.getSourceElement();
        final String string = htmlFrameHyperlinkEvent.getURL().toString();
        if (target.equals("_self")) {
            this.updateFrame(sourceElement, string);
        }
        else if (target.equals("_parent")) {
            this.updateFrameSet(sourceElement.getParentElement(), string);
        }
        else {
            final Element frame = this.findFrame(target);
            if (frame != null) {
                this.updateFrame(frame, string);
            }
        }
    }
    
    private Element findFrame(final String s) {
        Element next;
        while ((next = new ElementIterator(this).next()) != null) {
            final AttributeSet attributes = next.getAttributes();
            if (matchNameAttribute(attributes, HTML.Tag.FRAME)) {
                final String s2 = (String)attributes.getAttribute(HTML.Attribute.NAME);
                if (s2 != null && s2.equals(s)) {
                    break;
                }
                continue;
            }
        }
        return next;
    }
    
    static boolean matchNameAttribute(final AttributeSet set, final HTML.Tag tag) {
        final Object attribute = set.getAttribute(StyleConstants.NameAttribute);
        return attribute instanceof HTML.Tag && attribute == tag;
    }
    
    private void updateFrameSet(final Element element, final String s) {
        try {
            element.getStartOffset();
            Math.min(this.getLength(), element.getEndOffset());
            String string = "<frame";
            if (s != null) {
                string = string + " src=\"" + s + "\"";
            }
            final String string2 = string + ">";
            this.installParserIfNecessary();
            this.setOuterHTML(element, string2);
        }
        catch (final BadLocationException ex) {}
        catch (final IOException ex2) {}
    }
    
    private void updateFrame(final Element element, final String s) {
        try {
            this.writeLock();
            final DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(element.getStartOffset(), 1, DocumentEvent.EventType.CHANGE);
            final AttributeSet copyAttributes = element.getAttributes().copyAttributes();
            final MutableAttributeSet set = (MutableAttributeSet)element.getAttributes();
            defaultDocumentEvent.addEdit(new AttributeUndoableEdit(element, copyAttributes, false));
            set.removeAttribute(HTML.Attribute.SRC);
            set.addAttribute(HTML.Attribute.SRC, s);
            defaultDocumentEvent.end();
            this.fireChangedUpdate(defaultDocumentEvent);
            this.fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
        }
        finally {
            this.writeUnlock();
        }
    }
    
    boolean isFrameDocument() {
        return this.frameDocument;
    }
    
    void setFrameDocumentState(final boolean frameDocument) {
        this.frameDocument = frameDocument;
    }
    
    void addMap(final Map map) {
        final String name = map.getName();
        if (name != null) {
            Object property = this.getProperty(HTMLDocument.MAP_PROPERTY);
            if (property == null) {
                property = new Hashtable<String, Map>(11);
                this.putProperty(HTMLDocument.MAP_PROPERTY, property);
            }
            if (property instanceof Hashtable) {
                ((Hashtable<String, Map>)property).put("#" + name, map);
            }
        }
    }
    
    void removeMap(final Map map) {
        final String name = map.getName();
        if (name != null) {
            final Object property = this.getProperty(HTMLDocument.MAP_PROPERTY);
            if (property instanceof Hashtable) {
                ((Hashtable)property).remove("#" + name);
            }
        }
    }
    
    Map getMap(final String s) {
        if (s != null) {
            final Object property = this.getProperty(HTMLDocument.MAP_PROPERTY);
            if (property != null && property instanceof Hashtable) {
                return ((Hashtable<K, Map>)property).get(s);
            }
        }
        return null;
    }
    
    Enumeration getMaps() {
        final Object property = this.getProperty(HTMLDocument.MAP_PROPERTY);
        if (property instanceof Hashtable) {
            return ((Hashtable)property).elements();
        }
        return null;
    }
    
    void setDefaultStyleSheetType(final String s) {
        this.putProperty("StyleType", s);
    }
    
    String getDefaultStyleSheetType() {
        final String s = (String)this.getProperty("StyleType");
        if (s == null) {
            return "text/css";
        }
        return s;
    }
    
    public void setParser(final HTMLEditorKit.Parser parser) {
        this.parser = parser;
        this.putProperty("__PARSER__", null);
    }
    
    public HTMLEditorKit.Parser getParser() {
        final Object property = this.getProperty("__PARSER__");
        if (property instanceof HTMLEditorKit.Parser) {
            return (HTMLEditorKit.Parser)property;
        }
        return this.parser;
    }
    
    public void setInnerHTML(final Element element, final String s) throws BadLocationException, IOException {
        this.verifyParser();
        if (element != null && element.isLeaf()) {
            throw new IllegalArgumentException("Can not set inner HTML of a leaf");
        }
        if (element != null && s != null) {
            final int elementCount = element.getElementCount();
            element.getStartOffset();
            this.insertHTML(element, element.getStartOffset(), s, true);
            if (element.getElementCount() > elementCount) {
                this.removeElements(element, element.getElementCount() - elementCount, elementCount);
            }
        }
    }
    
    public void setOuterHTML(final Element element, final String s) throws BadLocationException, IOException {
        this.verifyParser();
        if (element != null && element.getParentElement() != null && s != null) {
            final int startOffset = element.getStartOffset();
            final int endOffset = element.getEndOffset();
            final int length = this.getLength();
            boolean b = !element.isLeaf();
            if (!b && (endOffset > length || this.getText(endOffset - 1, 1).charAt(0) == HTMLDocument.NEWLINE[0])) {
                b = true;
            }
            final Element parentElement = element.getParentElement();
            final int elementCount = parentElement.getElementCount();
            this.insertHTML(parentElement, startOffset, s, b);
            final int length2 = this.getLength();
            if (elementCount != parentElement.getElementCount()) {
                this.removeElements(parentElement, parentElement.getElementIndex(startOffset + length2 - length), 1);
            }
        }
    }
    
    public void insertAfterStart(final Element element, final String s) throws BadLocationException, IOException {
        this.verifyParser();
        if (element == null || s == null) {
            return;
        }
        if (element.isLeaf()) {
            throw new IllegalArgumentException("Can not insert HTML after start of a leaf");
        }
        this.insertHTML(element, element.getStartOffset(), s, false);
    }
    
    public void insertBeforeEnd(final Element element, final String s) throws BadLocationException, IOException {
        this.verifyParser();
        if (element != null && element.isLeaf()) {
            throw new IllegalArgumentException("Can not set inner HTML before end of leaf");
        }
        if (element != null) {
            int endOffset = element.getEndOffset();
            if (element.getElement(element.getElementIndex(endOffset - 1)).isLeaf() && this.getText(endOffset - 1, 1).charAt(0) == HTMLDocument.NEWLINE[0]) {
                --endOffset;
            }
            this.insertHTML(element, endOffset, s, false);
        }
    }
    
    public void insertBeforeStart(final Element element, final String s) throws BadLocationException, IOException {
        this.verifyParser();
        if (element != null) {
            final Element parentElement = element.getParentElement();
            if (parentElement != null) {
                this.insertHTML(parentElement, element.getStartOffset(), s, false);
            }
        }
    }
    
    public void insertAfterEnd(final Element element, final String s) throws BadLocationException, IOException {
        this.verifyParser();
        if (element != null) {
            final Element parentElement = element.getParentElement();
            if (parentElement != null) {
                if (HTML.Tag.BODY.name.equals(parentElement.getName())) {
                    this.insertInBody = true;
                }
                int endOffset = element.getEndOffset();
                if (endOffset > this.getLength() + 1) {
                    --endOffset;
                }
                else if (element.isLeaf() && this.getText(endOffset - 1, 1).charAt(0) == HTMLDocument.NEWLINE[0]) {
                    --endOffset;
                }
                this.insertHTML(parentElement, endOffset, s, false);
                if (this.insertInBody) {
                    this.insertInBody = false;
                }
            }
        }
    }
    
    public Element getElement(final String s) {
        if (s == null) {
            return null;
        }
        return this.getElement(this.getDefaultRootElement(), HTML.Attribute.ID, s, true);
    }
    
    public Element getElement(final Element element, final Object o, final Object o2) {
        return this.getElement(element, o, o2, true);
    }
    
    private Element getElement(final Element element, final Object o, final Object o2, final boolean b) {
        final AttributeSet attributes = element.getAttributes();
        if (attributes != null && attributes.isDefined(o) && o2.equals(attributes.getAttribute(o))) {
            return element;
        }
        if (!element.isLeaf()) {
            for (int i = 0; i < element.getElementCount(); ++i) {
                final Element element2 = this.getElement(element.getElement(i), o, o2, b);
                if (element2 != null) {
                    return element2;
                }
            }
        }
        else if (b && attributes != null) {
            final Enumeration<?> attributeNames = attributes.getAttributeNames();
            if (attributeNames != null) {
                while (attributeNames.hasMoreElements()) {
                    final Object nextElement = attributeNames.nextElement();
                    if (nextElement instanceof HTML.Tag && attributes.getAttribute(nextElement) instanceof AttributeSet) {
                        final AttributeSet set = (AttributeSet)attributes.getAttribute(nextElement);
                        if (set.isDefined(o) && o2.equals(set.getAttribute(o))) {
                            return element;
                        }
                        continue;
                    }
                }
            }
        }
        return null;
    }
    
    private void verifyParser() {
        if (this.getParser() == null) {
            throw new IllegalStateException("No HTMLEditorKit.Parser");
        }
    }
    
    private void installParserIfNecessary() {
        if (this.getParser() == null) {
            this.setParser(new HTMLEditorKit().getParser());
        }
    }
    
    private void insertHTML(final Element element, final int n, final String s, final boolean b) throws BadLocationException, IOException {
        if (element != null && s != null) {
            final HTMLEditorKit.Parser parser = this.getParser();
            if (parser != null) {
                final int max = Math.max(0, n - 1);
                Element element2 = this.getCharacterElement(max);
                Element parentElement = element;
                int n2 = 0;
                int n3 = 0;
                if (element.getStartOffset() > max) {
                    while (parentElement != null && parentElement.getStartOffset() > max) {
                        parentElement = parentElement.getParentElement();
                        ++n3;
                    }
                    if (parentElement == null) {
                        throw new BadLocationException("No common parent", n);
                    }
                }
                while (element2 != null && element2 != parentElement) {
                    ++n2;
                    element2 = element2.getParentElement();
                }
                if (element2 != null) {
                    final HTMLReader htmlReader = new HTMLReader(n, n2 - 1, n3, null, false, true, b);
                    parser.parse(new StringReader(s), htmlReader, true);
                    htmlReader.flush();
                }
            }
        }
    }
    
    private void removeElements(final Element element, final int n, final int n2) throws BadLocationException {
        this.writeLock();
        try {
            final int startOffset = element.getElement(n).getStartOffset();
            final int endOffset = element.getElement(n + n2 - 1).getEndOffset();
            if (endOffset > this.getLength()) {
                this.removeElementsAtEnd(element, n, n2, startOffset, endOffset);
            }
            else {
                this.removeElements(element, n, n2, startOffset, endOffset);
            }
        }
        finally {
            this.writeUnlock();
        }
    }
    
    private void removeElementsAtEnd(final Element element, int n, int n2, final int n3, final int n4) throws BadLocationException {
        final boolean leaf = element.getElement(n - 1).isLeaf();
        final DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(n3 - 1, n4 - n3 + 1, DocumentEvent.EventType.REMOVE);
        if (leaf) {
            final Element characterElement = this.getCharacterElement(this.getLength());
            --n;
            if (characterElement.getParentElement() != element) {
                this.replace(defaultDocumentEvent, element, n, ++n2, n3, n4, true, true);
            }
            else {
                this.replace(defaultDocumentEvent, element, n, n2, n3, n4, true, false);
            }
        }
        else {
            Element element2;
            for (element2 = element.getElement(n - 1); !element2.isLeaf(); element2 = element2.getElement(element2.getElementCount() - 1)) {}
            final Element parentElement = element2.getParentElement();
            this.replace(defaultDocumentEvent, element, n, n2, n3, n4, false, false);
            this.replace(defaultDocumentEvent, parentElement, parentElement.getElementCount() - 1, 1, n3, n4, true, true);
        }
        this.postRemoveUpdate(defaultDocumentEvent);
        defaultDocumentEvent.end();
        this.fireRemoveUpdate(defaultDocumentEvent);
        this.fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
    }
    
    private void replace(final DefaultDocumentEvent defaultDocumentEvent, final Element element, final int n, final int n2, final int n3, final int n4, final boolean b, final boolean b2) throws BadLocationException {
        final AttributeSet attributes = element.getElement(n).getAttributes();
        final Element[] array = new Element[n2];
        for (int i = 0; i < n2; ++i) {
            array[i] = element.getElement(i + n);
        }
        if (b) {
            final UndoableEdit remove = this.getContent().remove(n3 - 1, n4 - n3);
            if (remove != null) {
                defaultDocumentEvent.addEdit(remove);
            }
        }
        Element[] array2;
        if (b2) {
            array2 = new Element[] { this.createLeafElement(element, attributes, n3 - 1, n3) };
        }
        else {
            array2 = new Element[0];
        }
        defaultDocumentEvent.addEdit(new ElementEdit(element, n, array, array2));
        ((BranchElement)element).replace(n, array.length, array2);
    }
    
    private void removeElements(final Element element, final int n, final int n2, final int n3, final int n4) throws BadLocationException {
        final Element[] array = new Element[n2];
        final Element[] array2 = new Element[0];
        for (int i = 0; i < n2; ++i) {
            array[i] = element.getElement(i + n);
        }
        final DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(n3, n4 - n3, DocumentEvent.EventType.REMOVE);
        ((BranchElement)element).replace(n, array.length, array2);
        defaultDocumentEvent.addEdit(new ElementEdit(element, n, array, array2));
        final UndoableEdit remove = this.getContent().remove(n3, n4 - n3);
        if (remove != null) {
            defaultDocumentEvent.addEdit(remove);
        }
        this.postRemoveUpdate(defaultDocumentEvent);
        defaultDocumentEvent.end();
        this.fireRemoveUpdate(defaultDocumentEvent);
        if (remove != null) {
            this.fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
        }
    }
    
    void obtainLock() {
        this.writeLock();
    }
    
    void releaseLock() {
        this.writeUnlock();
    }
    
    @Override
    protected void fireChangedUpdate(final DocumentEvent documentEvent) {
        super.fireChangedUpdate(documentEvent);
    }
    
    @Override
    protected void fireUndoableEditUpdate(final UndoableEditEvent undoableEditEvent) {
        super.fireUndoableEditUpdate(undoableEditEvent);
    }
    
    boolean hasBaseTag() {
        return this.hasBaseTag;
    }
    
    String getBaseTarget() {
        return this.baseTarget;
    }
    
    static {
        HTMLDocument.MAP_PROPERTY = "__MAP__";
        HTMLDocument.contentAttributeSet = new SimpleAttributeSet();
        ((MutableAttributeSet)HTMLDocument.contentAttributeSet).addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
        (HTMLDocument.NEWLINE = new char[1])[0] = '\n';
    }
    
    public abstract static class Iterator
    {
        public abstract AttributeSet getAttributes();
        
        public abstract int getStartOffset();
        
        public abstract int getEndOffset();
        
        public abstract void next();
        
        public abstract boolean isValid();
        
        public abstract HTML.Tag getTag();
    }
    
    static class LeafIterator extends Iterator
    {
        private int endOffset;
        private HTML.Tag tag;
        private ElementIterator pos;
        
        LeafIterator(final HTML.Tag tag, final Document document) {
            this.tag = tag;
            this.pos = new ElementIterator(document);
            this.endOffset = 0;
            this.next();
        }
        
        @Override
        public AttributeSet getAttributes() {
            final Element current = this.pos.current();
            if (current != null) {
                AttributeSet attributes = (AttributeSet)current.getAttributes().getAttribute(this.tag);
                if (attributes == null) {
                    attributes = current.getAttributes();
                }
                return attributes;
            }
            return null;
        }
        
        @Override
        public int getStartOffset() {
            final Element current = this.pos.current();
            if (current != null) {
                return current.getStartOffset();
            }
            return -1;
        }
        
        @Override
        public int getEndOffset() {
            return this.endOffset;
        }
        
        @Override
        public void next() {
            this.nextLeaf(this.pos);
            while (this.isValid()) {
                if (this.pos.current().getStartOffset() >= this.endOffset) {
                    final AttributeSet attributes = this.pos.current().getAttributes();
                    if (attributes.isDefined(this.tag) || attributes.getAttribute(StyleConstants.NameAttribute) == this.tag) {
                        this.setEndOffset();
                        break;
                    }
                }
                this.nextLeaf(this.pos);
            }
        }
        
        @Override
        public HTML.Tag getTag() {
            return this.tag;
        }
        
        @Override
        public boolean isValid() {
            return this.pos.current() != null;
        }
        
        void nextLeaf(final ElementIterator elementIterator) {
            elementIterator.next();
            while (elementIterator.current() != null && !elementIterator.current().isLeaf()) {
                elementIterator.next();
            }
        }
        
        void setEndOffset() {
            final AttributeSet attributes = this.getAttributes();
            this.endOffset = this.pos.current().getEndOffset();
            final ElementIterator elementIterator = (ElementIterator)this.pos.clone();
            this.nextLeaf(elementIterator);
            while (elementIterator.current() != null) {
                final Element current = elementIterator.current();
                final AttributeSet set = (AttributeSet)current.getAttributes().getAttribute(this.tag);
                if (set == null) {
                    break;
                }
                if (!set.equals(attributes)) {
                    break;
                }
                this.endOffset = current.getEndOffset();
                this.nextLeaf(elementIterator);
            }
        }
    }
    
    public class HTMLReader extends HTMLEditorKit.ParserCallback
    {
        private boolean receivedEndHTML;
        private int flushCount;
        private boolean insertAfterImplied;
        private boolean wantsTrailingNewline;
        int threshold;
        int offset;
        boolean inParagraph;
        boolean impliedP;
        boolean inPre;
        boolean inTextArea;
        TextAreaDocument textAreaDocument;
        boolean inTitle;
        boolean lastWasNewline;
        boolean emptyAnchor;
        boolean midInsert;
        boolean inBody;
        HTML.Tag insertTag;
        boolean insertInsertTag;
        boolean foundInsertTag;
        int insertTagDepthDelta;
        int popDepth;
        int pushDepth;
        Map lastMap;
        boolean inStyle;
        String defaultStyle;
        Vector<Object> styles;
        boolean inHead;
        boolean isStyleCSS;
        boolean emptyDocument;
        AttributeSet styleAttributes;
        Option option;
        protected Vector<ElementSpec> parseBuffer;
        protected MutableAttributeSet charAttr;
        Stack<AttributeSet> charAttrStack;
        Hashtable<HTML.Tag, TagAction> tagMap;
        int inBlock;
        private HTML.Tag nextTagAfterPImplied;
        
        public HTMLReader(final HTMLDocument htmlDocument, final int n) {
            this(htmlDocument, n, 0, 0, null);
        }
        
        public HTMLReader(final HTMLDocument htmlDocument, final int n, final int n2, final int n3, final HTML.Tag tag) {
            this(htmlDocument, n, n2, n3, tag, true, false, true);
        }
        
        HTMLReader(final int offset, final int n, final int n2, final HTML.Tag insertTag, final boolean insertInsertTag, final boolean b, final boolean wantsTrailingNewline) {
            this.inParagraph = false;
            this.impliedP = false;
            this.inPre = false;
            this.inTextArea = false;
            this.textAreaDocument = null;
            this.inTitle = false;
            this.lastWasNewline = true;
            this.inStyle = false;
            this.inHead = false;
            this.parseBuffer = new Vector<ElementSpec>();
            this.charAttr = new TaggedAttributeSet();
            this.charAttrStack = new Stack<AttributeSet>();
            this.inBlock = 0;
            this.nextTagAfterPImplied = null;
            this.emptyDocument = (HTMLDocument.this.getLength() == 0);
            this.isStyleCSS = "text/css".equals(HTMLDocument.this.getDefaultStyleSheetType());
            this.offset = offset;
            this.threshold = HTMLDocument.this.getTokenThreshold();
            this.tagMap = new Hashtable<HTML.Tag, TagAction>(57);
            final TagAction tagAction = new TagAction();
            final BlockAction blockAction = new BlockAction();
            final ParagraphAction paragraphAction = new ParagraphAction();
            final CharacterAction characterAction = new CharacterAction();
            final SpecialAction specialAction = new SpecialAction();
            final FormAction formAction = new FormAction();
            final HiddenAction hiddenAction = new HiddenAction();
            final ConvertAction convertAction = new ConvertAction();
            this.tagMap.put(HTML.Tag.A, new AnchorAction());
            this.tagMap.put(HTML.Tag.ADDRESS, characterAction);
            this.tagMap.put(HTML.Tag.APPLET, hiddenAction);
            this.tagMap.put(HTML.Tag.AREA, new AreaAction());
            this.tagMap.put(HTML.Tag.B, convertAction);
            this.tagMap.put(HTML.Tag.BASE, new BaseAction());
            this.tagMap.put(HTML.Tag.BASEFONT, characterAction);
            this.tagMap.put(HTML.Tag.BIG, characterAction);
            this.tagMap.put(HTML.Tag.BLOCKQUOTE, blockAction);
            this.tagMap.put(HTML.Tag.BODY, blockAction);
            this.tagMap.put(HTML.Tag.BR, specialAction);
            this.tagMap.put(HTML.Tag.CAPTION, blockAction);
            this.tagMap.put(HTML.Tag.CENTER, blockAction);
            this.tagMap.put(HTML.Tag.CITE, characterAction);
            this.tagMap.put(HTML.Tag.CODE, characterAction);
            this.tagMap.put(HTML.Tag.DD, blockAction);
            this.tagMap.put(HTML.Tag.DFN, characterAction);
            this.tagMap.put(HTML.Tag.DIR, blockAction);
            this.tagMap.put(HTML.Tag.DIV, blockAction);
            this.tagMap.put(HTML.Tag.DL, blockAction);
            this.tagMap.put(HTML.Tag.DT, paragraphAction);
            this.tagMap.put(HTML.Tag.EM, characterAction);
            this.tagMap.put(HTML.Tag.FONT, convertAction);
            this.tagMap.put(HTML.Tag.FORM, new FormTagAction());
            this.tagMap.put(HTML.Tag.FRAME, specialAction);
            this.tagMap.put(HTML.Tag.FRAMESET, blockAction);
            this.tagMap.put(HTML.Tag.H1, paragraphAction);
            this.tagMap.put(HTML.Tag.H2, paragraphAction);
            this.tagMap.put(HTML.Tag.H3, paragraphAction);
            this.tagMap.put(HTML.Tag.H4, paragraphAction);
            this.tagMap.put(HTML.Tag.H5, paragraphAction);
            this.tagMap.put(HTML.Tag.H6, paragraphAction);
            this.tagMap.put(HTML.Tag.HEAD, new HeadAction());
            this.tagMap.put(HTML.Tag.HR, specialAction);
            this.tagMap.put(HTML.Tag.HTML, blockAction);
            this.tagMap.put(HTML.Tag.I, convertAction);
            this.tagMap.put(HTML.Tag.IMG, specialAction);
            this.tagMap.put(HTML.Tag.INPUT, formAction);
            this.tagMap.put(HTML.Tag.ISINDEX, new IsindexAction());
            this.tagMap.put(HTML.Tag.KBD, characterAction);
            this.tagMap.put(HTML.Tag.LI, blockAction);
            this.tagMap.put(HTML.Tag.LINK, new LinkAction());
            this.tagMap.put(HTML.Tag.MAP, new MapAction());
            this.tagMap.put(HTML.Tag.MENU, blockAction);
            this.tagMap.put(HTML.Tag.META, new MetaAction());
            this.tagMap.put(HTML.Tag.NOBR, characterAction);
            this.tagMap.put(HTML.Tag.NOFRAMES, blockAction);
            this.tagMap.put(HTML.Tag.OBJECT, specialAction);
            this.tagMap.put(HTML.Tag.OL, blockAction);
            this.tagMap.put(HTML.Tag.OPTION, formAction);
            this.tagMap.put(HTML.Tag.P, paragraphAction);
            this.tagMap.put(HTML.Tag.PARAM, new ObjectAction());
            this.tagMap.put(HTML.Tag.PRE, new PreAction());
            this.tagMap.put(HTML.Tag.SAMP, characterAction);
            this.tagMap.put(HTML.Tag.SCRIPT, hiddenAction);
            this.tagMap.put(HTML.Tag.SELECT, formAction);
            this.tagMap.put(HTML.Tag.SMALL, characterAction);
            this.tagMap.put(HTML.Tag.SPAN, characterAction);
            this.tagMap.put(HTML.Tag.STRIKE, convertAction);
            this.tagMap.put(HTML.Tag.S, characterAction);
            this.tagMap.put(HTML.Tag.STRONG, characterAction);
            this.tagMap.put(HTML.Tag.STYLE, new StyleAction());
            this.tagMap.put(HTML.Tag.SUB, convertAction);
            this.tagMap.put(HTML.Tag.SUP, convertAction);
            this.tagMap.put(HTML.Tag.TABLE, blockAction);
            this.tagMap.put(HTML.Tag.TD, blockAction);
            this.tagMap.put(HTML.Tag.TEXTAREA, formAction);
            this.tagMap.put(HTML.Tag.TH, blockAction);
            this.tagMap.put(HTML.Tag.TITLE, new TitleAction());
            this.tagMap.put(HTML.Tag.TR, blockAction);
            this.tagMap.put(HTML.Tag.TT, characterAction);
            this.tagMap.put(HTML.Tag.U, convertAction);
            this.tagMap.put(HTML.Tag.UL, blockAction);
            this.tagMap.put(HTML.Tag.VAR, characterAction);
            if (insertTag != null) {
                this.insertTag = insertTag;
                this.popDepth = n;
                this.pushDepth = n2;
                this.insertInsertTag = insertInsertTag;
                this.foundInsertTag = false;
            }
            else {
                this.foundInsertTag = true;
            }
            if (b) {
                this.popDepth = n;
                this.pushDepth = n2;
                this.insertAfterImplied = true;
                this.foundInsertTag = false;
                this.midInsert = false;
                this.insertInsertTag = true;
                this.wantsTrailingNewline = wantsTrailingNewline;
            }
            else {
                this.midInsert = (!this.emptyDocument && insertTag == null);
                if (this.midInsert) {
                    this.generateEndsSpecsForMidInsert();
                }
            }
            if (!this.emptyDocument && !this.midInsert) {
                Element element = HTMLDocument.this.getCharacterElement(Math.max(this.offset - 1, 0));
                for (int i = 0; i <= this.popDepth; ++i) {
                    element = element.getParentElement();
                }
                for (int j = 0; j < this.pushDepth; ++j) {
                    element = element.getElement(element.getElementIndex(this.offset));
                }
                final AttributeSet attributes = element.getAttributes();
                if (attributes != null) {
                    final HTML.Tag tag = (HTML.Tag)attributes.getAttribute(StyleConstants.NameAttribute);
                    if (tag != null) {
                        this.inParagraph = tag.isParagraph();
                    }
                }
            }
        }
        
        private void generateEndsSpecsForMidInsert() {
            int n = this.heightToElementWithName(HTML.Tag.BODY, Math.max(0, this.offset - 1));
            boolean b = false;
            if (n == -1 && this.offset > 0) {
                n = this.heightToElementWithName(HTML.Tag.BODY, this.offset);
                if (n != -1) {
                    n = this.depthTo(this.offset - 1) - 1;
                    b = true;
                }
            }
            if (n == -1) {
                throw new RuntimeException("Must insert new content into body element-");
            }
            if (n != -1) {
                try {
                    if (!b && this.offset > 0 && !HTMLDocument.this.getText(this.offset - 1, 1).equals("\n")) {
                        final SimpleAttributeSet set = new SimpleAttributeSet();
                        set.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
                        this.parseBuffer.addElement(new ElementSpec(set, (short)3, HTMLDocument.NEWLINE, 0, 1));
                    }
                }
                catch (final BadLocationException ex) {}
                while (n-- > 0) {
                    this.parseBuffer.addElement(new ElementSpec(null, (short)2));
                }
                if (b) {
                    final ElementSpec elementSpec = new ElementSpec(null, (short)1);
                    elementSpec.setDirection((short)5);
                    this.parseBuffer.addElement(elementSpec);
                }
            }
        }
        
        private int depthTo(final int n) {
            Element element = HTMLDocument.this.getDefaultRootElement();
            int n2 = 0;
            while (!element.isLeaf()) {
                ++n2;
                element = element.getElement(element.getElementIndex(n));
            }
            return n2;
        }
        
        private int heightToElementWithName(final Object o, final int n) {
            Element element = HTMLDocument.this.getCharacterElement(n).getParentElement();
            int n2 = 0;
            while (element != null && element.getAttributes().getAttribute(StyleConstants.NameAttribute) != o) {
                ++n2;
                element = element.getParentElement();
            }
            return (element == null) ? -1 : n2;
        }
        
        private void adjustEndElement() {
            final int length = HTMLDocument.this.getLength();
            if (length == 0) {
                return;
            }
            HTMLDocument.this.obtainLock();
            try {
                final Element[] pathTo = this.getPathTo(length - 1);
                final int length2 = pathTo.length;
                if (length2 > 1 && pathTo[1].getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.BODY && pathTo[1].getEndOffset() == length) {
                    final String text = HTMLDocument.this.getText(length - 1, 1);
                    final Element[] array = new Element[0];
                    final Element[] array2 = { null };
                    final int elementIndex = pathTo[0].getElementIndex(length);
                    array2[0] = pathTo[0].getElement(elementIndex);
                    ((BranchElement)pathTo[0]).replace(elementIndex, 1, array);
                    final ElementEdit elementEdit = new ElementEdit(pathTo[0], elementIndex, array2, array);
                    final SimpleAttributeSet set = new SimpleAttributeSet();
                    set.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
                    set.addAttribute("CR", Boolean.TRUE);
                    final Element[] array3 = { HTMLDocument.this.createLeafElement(pathTo[length2 - 1], set, length, length + 1) };
                    final int elementCount = pathTo[length2 - 1].getElementCount();
                    ((BranchElement)pathTo[length2 - 1]).replace(elementCount, 0, array3);
                    final DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(length, 1, DocumentEvent.EventType.CHANGE);
                    defaultDocumentEvent.addEdit(new ElementEdit(pathTo[length2 - 1], elementCount, new Element[0], array3));
                    defaultDocumentEvent.addEdit(elementEdit);
                    defaultDocumentEvent.end();
                    HTMLDocument.this.fireChangedUpdate(defaultDocumentEvent);
                    HTMLDocument.this.fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
                    if (text.equals("\n")) {
                        final DefaultDocumentEvent defaultDocumentEvent2 = new DefaultDocumentEvent(length - 1, 1, DocumentEvent.EventType.REMOVE);
                        DefaultStyledDocument.this.removeUpdate(defaultDocumentEvent2);
                        final UndoableEdit remove = AbstractDocument.this.getContent().remove(length - 1, 1);
                        if (remove != null) {
                            defaultDocumentEvent2.addEdit(remove);
                        }
                        AbstractDocument.this.postRemoveUpdate(defaultDocumentEvent2);
                        defaultDocumentEvent2.end();
                        AbstractDocument.this.fireRemoveUpdate(defaultDocumentEvent2);
                        HTMLDocument.this.fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent2));
                    }
                }
            }
            catch (final BadLocationException ex) {}
            finally {
                HTMLDocument.this.releaseLock();
            }
        }
        
        private Element[] getPathTo(final int n) {
            final Stack stack = new Stack();
            for (Element element = HTMLDocument.this.getDefaultRootElement(); !element.isLeaf(); element = element.getElement(element.getElementIndex(n))) {
                stack.push(element);
            }
            final Element[] array = new Element[stack.size()];
            stack.copyInto(array);
            return array;
        }
        
        @Override
        public void flush() throws BadLocationException {
            if (this.emptyDocument && !this.insertAfterImplied) {
                if (HTMLDocument.this.getLength() > 0 || this.parseBuffer.size() > 0) {
                    this.flushBuffer(true);
                    this.adjustEndElement();
                }
            }
            else {
                this.flushBuffer(true);
            }
        }
        
        @Override
        public void handleText(final char[] array, final int n) {
            if (this.receivedEndHTML || (this.midInsert && !this.inBody)) {
                return;
            }
            if (HTMLDocument.this.getProperty("i18n").equals(Boolean.FALSE)) {
                final Object property = HTMLDocument.this.getProperty(TextAttribute.RUN_DIRECTION);
                if (property != null && property.equals(TextAttribute.RUN_DIRECTION_RTL)) {
                    HTMLDocument.this.putProperty("i18n", Boolean.TRUE);
                }
                else if (SwingUtilities2.isComplexLayout(array, 0, array.length)) {
                    HTMLDocument.this.putProperty("i18n", Boolean.TRUE);
                }
            }
            if (this.inTextArea) {
                this.textAreaContent(array);
            }
            else if (this.inPre) {
                this.preContent(array);
            }
            else if (this.inTitle) {
                HTMLDocument.this.putProperty("title", new String(array));
            }
            else if (this.option != null) {
                this.option.setLabel(new String(array));
            }
            else if (this.inStyle) {
                if (this.styles != null) {
                    this.styles.addElement(new String(array));
                }
            }
            else if (this.inBlock > 0) {
                if (!this.foundInsertTag && this.insertAfterImplied) {
                    this.foundInsertTag(false);
                    this.foundInsertTag = true;
                    final boolean b = !HTMLDocument.this.insertInBody;
                    this.impliedP = b;
                    this.inParagraph = b;
                }
                if (array.length >= 1) {
                    this.addContent(array, 0, array.length);
                }
            }
        }
        
        @Override
        public void handleStartTag(final HTML.Tag tag, final MutableAttributeSet set, final int n) {
            if (this.receivedEndHTML) {
                return;
            }
            if (this.midInsert && !this.inBody) {
                if (tag == HTML.Tag.BODY) {
                    this.inBody = true;
                    ++this.inBlock;
                }
                return;
            }
            if (!this.inBody && tag == HTML.Tag.BODY) {
                this.inBody = true;
            }
            if (this.isStyleCSS && set.isDefined(HTML.Attribute.STYLE)) {
                final String s = (String)set.getAttribute(HTML.Attribute.STYLE);
                set.removeAttribute(HTML.Attribute.STYLE);
                set.addAttributes(this.styleAttributes = HTMLDocument.this.getStyleSheet().getDeclaration(s));
            }
            else {
                this.styleAttributes = null;
            }
            final TagAction tagAction = this.tagMap.get(tag);
            if (tagAction != null) {
                tagAction.start(tag, set);
            }
        }
        
        @Override
        public void handleComment(final char[] array, final int n) {
            if (this.receivedEndHTML) {
                this.addExternalComment(new String(array));
                return;
            }
            if (this.inStyle) {
                if (this.styles != null) {
                    this.styles.addElement(new String(array));
                }
            }
            else if (HTMLDocument.this.getPreservesUnknownTags()) {
                if (this.inBlock == 0 && (this.foundInsertTag || this.insertTag != HTML.Tag.COMMENT)) {
                    this.addExternalComment(new String(array));
                    return;
                }
                final SimpleAttributeSet set = new SimpleAttributeSet();
                set.addAttribute(HTML.Attribute.COMMENT, new String(array));
                this.addSpecialElement(HTML.Tag.COMMENT, set);
            }
            final TagAction tagAction = this.tagMap.get(HTML.Tag.COMMENT);
            if (tagAction != null) {
                tagAction.start(HTML.Tag.COMMENT, new SimpleAttributeSet());
                tagAction.end(HTML.Tag.COMMENT);
            }
        }
        
        private void addExternalComment(final String s) {
            Object property = HTMLDocument.this.getProperty("AdditionalComments");
            if (property != null && !(property instanceof Vector)) {
                return;
            }
            if (property == null) {
                property = new Vector<String>();
                HTMLDocument.this.putProperty("AdditionalComments", property);
            }
            ((Vector<String>)property).addElement(s);
        }
        
        @Override
        public void handleEndTag(final HTML.Tag tag, final int n) {
            if (this.receivedEndHTML || (this.midInsert && !this.inBody)) {
                return;
            }
            if (tag == HTML.Tag.HTML) {
                this.receivedEndHTML = true;
            }
            if (tag == HTML.Tag.BODY) {
                this.inBody = false;
                if (this.midInsert) {
                    --this.inBlock;
                }
            }
            final TagAction tagAction = this.tagMap.get(tag);
            if (tagAction != null) {
                tagAction.end(tag);
            }
        }
        
        @Override
        public void handleSimpleTag(final HTML.Tag tag, final MutableAttributeSet set, final int n) {
            if (this.receivedEndHTML || (this.midInsert && !this.inBody)) {
                return;
            }
            if (this.isStyleCSS && set.isDefined(HTML.Attribute.STYLE)) {
                final String s = (String)set.getAttribute(HTML.Attribute.STYLE);
                set.removeAttribute(HTML.Attribute.STYLE);
                set.addAttributes(this.styleAttributes = HTMLDocument.this.getStyleSheet().getDeclaration(s));
            }
            else {
                this.styleAttributes = null;
            }
            final TagAction tagAction = this.tagMap.get(tag);
            if (tagAction != null) {
                tagAction.start(tag, set);
                tagAction.end(tag);
            }
            else if (HTMLDocument.this.getPreservesUnknownTags()) {
                this.addSpecialElement(tag, set);
            }
        }
        
        @Override
        public void handleEndOfLineString(final String s) {
            if (this.emptyDocument && s != null) {
                HTMLDocument.this.putProperty("__EndOfLine__", s);
            }
        }
        
        protected void registerTag(final HTML.Tag tag, final TagAction tagAction) {
            this.tagMap.put(tag, tagAction);
        }
        
        protected void pushCharacterStyle() {
            this.charAttrStack.push(this.charAttr.copyAttributes());
        }
        
        protected void popCharacterStyle() {
            if (!this.charAttrStack.empty()) {
                this.charAttr = this.charAttrStack.peek();
                this.charAttrStack.pop();
            }
        }
        
        protected void textAreaContent(final char[] array) {
            try {
                this.textAreaDocument.insertString(this.textAreaDocument.getLength(), new String(array), null);
            }
            catch (final BadLocationException ex) {}
        }
        
        protected void preContent(final char[] array) {
            int n = 0;
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == '\n') {
                    this.addContent(array, n, i - n + 1);
                    this.blockClose(HTML.Tag.IMPLIED);
                    final SimpleAttributeSet set = new SimpleAttributeSet();
                    set.addAttribute(CSS.Attribute.WHITE_SPACE, "pre");
                    this.blockOpen(HTML.Tag.IMPLIED, set);
                    n = i + 1;
                }
            }
            if (n < array.length) {
                this.addContent(array, n, array.length - n);
            }
        }
        
        protected void blockOpen(final HTML.Tag tag, final MutableAttributeSet set) {
            if (this.impliedP) {
                this.blockClose(HTML.Tag.IMPLIED);
            }
            ++this.inBlock;
            if (!this.canInsertTag(tag, set, true)) {
                return;
            }
            if (set.isDefined(HTMLReader.IMPLIED)) {
                set.removeAttribute(HTMLReader.IMPLIED);
            }
            this.lastWasNewline = false;
            set.addAttribute(StyleConstants.NameAttribute, tag);
            this.parseBuffer.addElement(new ElementSpec(set.copyAttributes(), (short)1));
        }
        
        protected void blockClose(final HTML.Tag tag) {
            --this.inBlock;
            if (!this.foundInsertTag) {
                return;
            }
            if (!this.lastWasNewline) {
                this.pushCharacterStyle();
                this.charAttr.addAttribute("CR", Boolean.TRUE);
                this.addContent(HTMLDocument.NEWLINE, 0, 1, true);
                this.popCharacterStyle();
                this.lastWasNewline = true;
            }
            if (this.impliedP) {
                this.impliedP = false;
                this.inParagraph = false;
                if (tag != HTML.Tag.IMPLIED) {
                    this.blockClose(HTML.Tag.IMPLIED);
                }
            }
            final ElementSpec elementSpec = (this.parseBuffer.size() > 0) ? this.parseBuffer.lastElement() : null;
            if (elementSpec != null && elementSpec.getType() == 1) {
                this.addContent(new char[] { ' ' }, 0, 1);
            }
            this.parseBuffer.addElement(new ElementSpec(null, (short)2));
        }
        
        protected void addContent(final char[] array, final int n, final int n2) {
            this.addContent(array, n, n2, true);
        }
        
        protected void addContent(final char[] array, final int n, final int n2, final boolean b) {
            if (!this.foundInsertTag) {
                return;
            }
            if (b && !this.inParagraph && !this.inPre) {
                this.blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
                this.inParagraph = true;
                this.impliedP = true;
            }
            this.emptyAnchor = false;
            this.charAttr.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
            this.parseBuffer.addElement(new ElementSpec(this.charAttr.copyAttributes(), (short)3, array, n, n2));
            if (this.parseBuffer.size() > this.threshold) {
                if (this.threshold <= 10000) {
                    this.threshold *= 5;
                }
                try {
                    this.flushBuffer(false);
                }
                catch (final BadLocationException ex) {}
            }
            if (n2 > 0) {
                this.lastWasNewline = (array[n + n2 - 1] == '\n');
            }
        }
        
        protected void addSpecialElement(final HTML.Tag nextTagAfterPImplied, final MutableAttributeSet set) {
            if (nextTagAfterPImplied != HTML.Tag.FRAME && !this.inParagraph && !this.inPre) {
                this.nextTagAfterPImplied = nextTagAfterPImplied;
                this.blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
                this.nextTagAfterPImplied = null;
                this.inParagraph = true;
                this.impliedP = true;
            }
            if (!this.canInsertTag(nextTagAfterPImplied, set, nextTagAfterPImplied.isBlock())) {
                return;
            }
            if (set.isDefined(HTMLReader.IMPLIED)) {
                set.removeAttribute(HTMLReader.IMPLIED);
            }
            this.emptyAnchor = false;
            set.addAttributes(this.charAttr);
            set.addAttribute(StyleConstants.NameAttribute, nextTagAfterPImplied);
            this.parseBuffer.addElement(new ElementSpec(set.copyAttributes(), (short)3, new char[] { ' ' }, 0, 1));
            if (nextTagAfterPImplied == HTML.Tag.FRAME) {
                this.lastWasNewline = true;
            }
        }
        
        void flushBuffer(final boolean b) throws BadLocationException {
            final int length = HTMLDocument.this.getLength();
            int n = this.parseBuffer.size();
            if (b && (this.insertTag != null || this.insertAfterImplied) && n > 0) {
                this.adjustEndSpecsForPartialInsert();
                n = this.parseBuffer.size();
            }
            final ElementSpec[] array = new ElementSpec[n];
            this.parseBuffer.copyInto(array);
            if (length == 0 && this.insertTag == null && !this.insertAfterImplied) {
                HTMLDocument.this.create(array);
            }
            else {
                HTMLDocument.this.insert(this.offset, array);
            }
            this.parseBuffer.removeAllElements();
            this.offset += HTMLDocument.this.getLength() - length;
            ++this.flushCount;
        }
        
        private void adjustEndSpecsForPartialInsert() {
            int i = this.parseBuffer.size();
            if (this.insertTagDepthDelta < 0) {
                for (int insertTagDepthDelta = this.insertTagDepthDelta; insertTagDepthDelta < 0 && i >= 0 && this.parseBuffer.elementAt(i - 1).getType() == 2; ++insertTagDepthDelta) {
                    this.parseBuffer.removeElementAt(--i);
                }
            }
            if (this.flushCount == 0 && (!this.insertAfterImplied || !this.wantsTrailingNewline)) {
                int n = 0;
                if (this.pushDepth > 0 && this.parseBuffer.elementAt(0).getType() == 3) {
                    ++n;
                }
                int n2 = n + (this.popDepth + this.pushDepth);
                int n3 = 0;
                final int n4 = n2;
                while (n2 < i && this.parseBuffer.elementAt(n2).getType() == 3) {
                    ++n2;
                    ++n3;
                }
                if (n3 > 1) {
                    while (n2 < i && this.parseBuffer.elementAt(n2).getType() == 2) {
                        ++n2;
                    }
                    if (n2 == i) {
                        final char[] array = this.parseBuffer.elementAt(n4 + n3 - 1).getArray();
                        if (array.length == 1 && array[0] == HTMLDocument.NEWLINE[0]) {
                            while (i > n4 + n3 - 1) {
                                this.parseBuffer.removeElementAt(--i);
                            }
                        }
                    }
                }
            }
            if (this.wantsTrailingNewline) {
                int j = this.parseBuffer.size() - 1;
                while (j >= 0) {
                    final ElementSpec elementSpec = this.parseBuffer.elementAt(j);
                    if (elementSpec.getType() == 3) {
                        if (elementSpec.getArray()[elementSpec.getLength() - 1] != '\n') {
                            final SimpleAttributeSet set = new SimpleAttributeSet();
                            set.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
                            this.parseBuffer.insertElementAt(new ElementSpec(set, (short)3, HTMLDocument.NEWLINE, 0, 1), j + 1);
                            break;
                        }
                        break;
                    }
                    else {
                        --j;
                    }
                }
            }
        }
        
        void addCSSRules(final String s) {
            HTMLDocument.this.getStyleSheet().addRule(s);
        }
        
        void linkCSSStyleSheet(final String s) {
            URL url;
            try {
                url = new URL(HTMLDocument.this.base, s);
            }
            catch (final MalformedURLException ex) {
                try {
                    url = new URL(s);
                }
                catch (final MalformedURLException ex2) {
                    url = null;
                }
            }
            if (url != null) {
                HTMLDocument.this.getStyleSheet().importStyleSheet(url);
            }
        }
        
        private boolean canInsertTag(final HTML.Tag tag, final AttributeSet set, final boolean b) {
            if (!this.foundInsertTag) {
                if (tag == HTML.Tag.IMPLIED && !this.inParagraph && !this.inPre && this.nextTagAfterPImplied != null) {
                    if (this.insertTag != null) {
                        if (!this.isInsertTag(this.nextTagAfterPImplied) || !this.insertInsertTag) {
                            return false;
                        }
                    }
                }
                else if ((this.insertTag != null && !this.isInsertTag(tag)) || (this.insertAfterImplied && (set == null || set.isDefined(HTMLReader.IMPLIED) || tag == HTML.Tag.IMPLIED))) {
                    return false;
                }
                this.foundInsertTag(b);
                if (!this.insertInsertTag) {
                    return false;
                }
            }
            return true;
        }
        
        private boolean isInsertTag(final HTML.Tag tag) {
            return this.insertTag == tag;
        }
        
        private void foundInsertTag(final boolean b) {
            this.foundInsertTag = true;
            Label_0236: {
                if (!this.insertAfterImplied) {
                    if (this.popDepth <= 0) {
                        if (this.pushDepth <= 0) {
                            break Label_0236;
                        }
                    }
                    try {
                        if (this.offset == 0 || !HTMLDocument.this.getText(this.offset - 1, 1).equals("\n")) {
                            SimpleAttributeSet set = null;
                            boolean b2 = true;
                            if (this.offset != 0) {
                                final AttributeSet attributes = HTMLDocument.this.getCharacterElement(this.offset - 1).getAttributes();
                                if (attributes.isDefined(StyleConstants.ComposedTextAttribute)) {
                                    b2 = false;
                                }
                                else {
                                    final Object attribute = attributes.getAttribute(StyleConstants.NameAttribute);
                                    if (attribute instanceof HTML.Tag) {
                                        final HTML.Tag tag = (HTML.Tag)attribute;
                                        if (tag == HTML.Tag.IMG || tag == HTML.Tag.HR || tag == HTML.Tag.COMMENT || tag instanceof HTML.UnknownTag) {
                                            b2 = false;
                                        }
                                    }
                                }
                            }
                            if (!b2) {
                                set = new SimpleAttributeSet();
                                set.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
                            }
                            final ElementSpec elementSpec = new ElementSpec(set, (short)3, HTMLDocument.NEWLINE, 0, HTMLDocument.NEWLINE.length);
                            if (b2) {
                                elementSpec.setDirection((short)4);
                            }
                            this.parseBuffer.addElement(elementSpec);
                        }
                    }
                    catch (final BadLocationException ex) {}
                }
            }
            for (int i = 0; i < this.popDepth; ++i) {
                this.parseBuffer.addElement(new ElementSpec(null, (short)2));
            }
            for (int j = 0; j < this.pushDepth; ++j) {
                final ElementSpec elementSpec2 = new ElementSpec(null, (short)1);
                elementSpec2.setDirection((short)5);
                this.parseBuffer.addElement(elementSpec2);
            }
            this.insertTagDepthDelta = this.depthTo(Math.max(0, this.offset - 1)) - this.popDepth + this.pushDepth - this.inBlock;
            if (b) {
                ++this.insertTagDepthDelta;
            }
            else {
                --this.insertTagDepthDelta;
                this.inParagraph = true;
                this.lastWasNewline = false;
            }
        }
        
        public class TagAction
        {
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
            }
            
            public void end(final HTML.Tag tag) {
            }
        }
        
        public class BlockAction extends TagAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                HTMLReader.this.blockOpen(tag, set);
            }
            
            @Override
            public void end(final HTML.Tag tag) {
                HTMLReader.this.blockClose(tag);
            }
        }
        
        private class FormTagAction extends BlockAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                super.start(tag, set);
                HTMLDocument.this.radioButtonGroupsMap = (HashMap<String, ButtonGroup>)new HashMap();
            }
            
            @Override
            public void end(final HTML.Tag tag) {
                super.end(tag);
                HTMLDocument.this.radioButtonGroupsMap = null;
            }
        }
        
        public class ParagraphAction extends BlockAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                super.start(tag, set);
                HTMLReader.this.inParagraph = true;
            }
            
            @Override
            public void end(final HTML.Tag tag) {
                super.end(tag);
                HTMLReader.this.inParagraph = false;
            }
        }
        
        public class SpecialAction extends TagAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                HTMLReader.this.addSpecialElement(tag, set);
            }
        }
        
        public class IsindexAction extends TagAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                HTMLReader.this.blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
                HTMLReader.this.addSpecialElement(tag, set);
                HTMLReader.this.blockClose(HTML.Tag.IMPLIED);
            }
        }
        
        public class HiddenAction extends TagAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                HTMLReader.this.addSpecialElement(tag, set);
            }
            
            @Override
            public void end(final HTML.Tag tag) {
                if (!this.isEmpty(tag)) {
                    final SimpleAttributeSet set = new SimpleAttributeSet();
                    set.addAttribute(HTML.Attribute.ENDTAG, "true");
                    HTMLReader.this.addSpecialElement(tag, set);
                }
            }
            
            boolean isEmpty(final HTML.Tag tag) {
                return tag != HTML.Tag.APPLET && tag != HTML.Tag.SCRIPT;
            }
        }
        
        class MetaAction extends HiddenAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                final Object attribute = set.getAttribute(HTML.Attribute.HTTPEQUIV);
                if (attribute != null) {
                    final String lowerCase = ((String)attribute).toLowerCase();
                    if (lowerCase.equals("content-style-type")) {
                        HTMLDocument.this.setDefaultStyleSheetType((String)set.getAttribute(HTML.Attribute.CONTENT));
                        HTMLReader.this.isStyleCSS = "text/css".equals(HTMLDocument.this.getDefaultStyleSheetType());
                    }
                    else if (lowerCase.equals("default-style")) {
                        HTMLReader.this.defaultStyle = (String)set.getAttribute(HTML.Attribute.CONTENT);
                    }
                }
                super.start(tag, set);
            }
            
            @Override
            boolean isEmpty(final HTML.Tag tag) {
                return true;
            }
        }
        
        class HeadAction extends BlockAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                HTMLReader.this.inHead = true;
                if ((HTMLReader.this.insertTag == null && !HTMLReader.this.insertAfterImplied) || HTMLReader.this.insertTag == HTML.Tag.HEAD || (HTMLReader.this.insertAfterImplied && (HTMLReader.this.foundInsertTag || !set.isDefined(HTMLEditorKit.ParserCallback.IMPLIED)))) {
                    super.start(tag, set);
                }
            }
            
            @Override
            public void end(final HTML.Tag tag) {
                final HTMLReader this$1 = HTMLReader.this;
                final HTMLReader this$2 = HTMLReader.this;
                final boolean b = false;
                this$2.inStyle = b;
                this$1.inHead = b;
                if (HTMLReader.this.styles != null) {
                    final boolean isStyleCSS = HTMLReader.this.isStyleCSS;
                    for (int i = 0, size = HTMLReader.this.styles.size(); i < size; ++i) {
                        if (HTMLReader.this.styles.elementAt(i) == HTML.Tag.LINK) {
                            this.handleLink((AttributeSet)HTMLReader.this.styles.elementAt(++i));
                        }
                        else {
                            final String s = HTMLReader.this.styles.elementAt(++i);
                            final boolean b2 = (s == null) ? isStyleCSS : s.equals("text/css");
                            while (++i < size && HTMLReader.this.styles.elementAt(i) instanceof String) {
                                if (b2) {
                                    HTMLReader.this.addCSSRules((String)HTMLReader.this.styles.elementAt(i));
                                }
                            }
                        }
                    }
                }
                if ((HTMLReader.this.insertTag == null && !HTMLReader.this.insertAfterImplied) || HTMLReader.this.insertTag == HTML.Tag.HEAD || (HTMLReader.this.insertAfterImplied && HTMLReader.this.foundInsertTag)) {
                    super.end(tag);
                }
            }
            
            boolean isEmpty(final HTML.Tag tag) {
                return false;
            }
            
            private void handleLink(final AttributeSet set) {
                String defaultStyleSheetType = (String)set.getAttribute(HTML.Attribute.TYPE);
                if (defaultStyleSheetType == null) {
                    defaultStyleSheetType = HTMLDocument.this.getDefaultStyleSheetType();
                }
                if (defaultStyleSheetType.equals("text/css")) {
                    final String s = (String)set.getAttribute(HTML.Attribute.REL);
                    final String s2 = (String)set.getAttribute(HTML.Attribute.TITLE);
                    final String s3 = (String)set.getAttribute(HTML.Attribute.MEDIA);
                    String lowerCase;
                    if (s3 == null) {
                        lowerCase = "all";
                    }
                    else {
                        lowerCase = s3.toLowerCase();
                    }
                    if (s != null) {
                        final String lowerCase2 = s.toLowerCase();
                        if ((lowerCase.indexOf("all") != -1 || lowerCase.indexOf("screen") != -1) && (lowerCase2.equals("stylesheet") || (lowerCase2.equals("alternate stylesheet") && s2.equals(HTMLReader.this.defaultStyle)))) {
                            HTMLReader.this.linkCSSStyleSheet((String)set.getAttribute(HTML.Attribute.HREF));
                        }
                    }
                }
            }
        }
        
        class LinkAction extends HiddenAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                final String s = (String)set.getAttribute(HTML.Attribute.REL);
                if (s != null) {
                    final String lowerCase = s.toLowerCase();
                    if (lowerCase.equals("stylesheet") || lowerCase.equals("alternate stylesheet")) {
                        if (HTMLReader.this.styles == null) {
                            HTMLReader.this.styles = new Vector<Object>(3);
                        }
                        HTMLReader.this.styles.addElement(tag);
                        HTMLReader.this.styles.addElement(set.copyAttributes());
                    }
                }
                super.start(tag, set);
            }
        }
        
        class MapAction extends TagAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                HTMLReader.this.lastMap = new Map((String)set.getAttribute(HTML.Attribute.NAME));
                HTMLDocument.this.addMap(HTMLReader.this.lastMap);
            }
            
            @Override
            public void end(final HTML.Tag tag) {
            }
        }
        
        class AreaAction extends TagAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                if (HTMLReader.this.lastMap != null) {
                    HTMLReader.this.lastMap.addArea(set.copyAttributes());
                }
            }
            
            @Override
            public void end(final HTML.Tag tag) {
            }
        }
        
        class StyleAction extends TagAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                if (HTMLReader.this.inHead) {
                    if (HTMLReader.this.styles == null) {
                        HTMLReader.this.styles = new Vector<Object>(3);
                    }
                    HTMLReader.this.styles.addElement(tag);
                    HTMLReader.this.styles.addElement(set.getAttribute(HTML.Attribute.TYPE));
                    HTMLReader.this.inStyle = true;
                }
            }
            
            @Override
            public void end(final HTML.Tag tag) {
                HTMLReader.this.inStyle = false;
            }
            
            boolean isEmpty(final HTML.Tag tag) {
                return false;
            }
        }
        
        public class PreAction extends BlockAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                HTMLReader.this.inPre = true;
                HTMLReader.this.blockOpen(tag, set);
                set.addAttribute(CSS.Attribute.WHITE_SPACE, "pre");
                HTMLReader.this.blockOpen(HTML.Tag.IMPLIED, set);
            }
            
            @Override
            public void end(final HTML.Tag tag) {
                HTMLReader.this.blockClose(HTML.Tag.IMPLIED);
                HTMLReader.this.inPre = false;
                HTMLReader.this.blockClose(tag);
            }
        }
        
        public class CharacterAction extends TagAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                HTMLReader.this.pushCharacterStyle();
                if (!HTMLReader.this.foundInsertTag) {
                    final boolean access$900 = HTMLReader.this.canInsertTag(tag, set, false);
                    if (HTMLReader.this.foundInsertTag && !HTMLReader.this.inParagraph) {
                        final HTMLReader this$1 = HTMLReader.this;
                        final HTMLReader this$2 = HTMLReader.this;
                        final boolean b = true;
                        this$2.impliedP = b;
                        this$1.inParagraph = b;
                    }
                    if (!access$900) {
                        return;
                    }
                }
                if (set.isDefined(HTMLEditorKit.ParserCallback.IMPLIED)) {
                    set.removeAttribute(HTMLEditorKit.ParserCallback.IMPLIED);
                }
                HTMLReader.this.charAttr.addAttribute(tag, set.copyAttributes());
                if (HTMLReader.this.styleAttributes != null) {
                    HTMLReader.this.charAttr.addAttributes(HTMLReader.this.styleAttributes);
                }
            }
            
            @Override
            public void end(final HTML.Tag tag) {
                HTMLReader.this.popCharacterStyle();
            }
        }
        
        class ConvertAction extends TagAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                HTMLReader.this.pushCharacterStyle();
                if (!HTMLReader.this.foundInsertTag) {
                    final boolean access$900 = HTMLReader.this.canInsertTag(tag, set, false);
                    if (HTMLReader.this.foundInsertTag && !HTMLReader.this.inParagraph) {
                        final HTMLReader this$1 = HTMLReader.this;
                        final HTMLReader this$2 = HTMLReader.this;
                        final boolean b = true;
                        this$2.impliedP = b;
                        this$1.inParagraph = b;
                    }
                    if (!access$900) {
                        return;
                    }
                }
                if (set.isDefined(HTMLEditorKit.ParserCallback.IMPLIED)) {
                    set.removeAttribute(HTMLEditorKit.ParserCallback.IMPLIED);
                }
                if (HTMLReader.this.styleAttributes != null) {
                    HTMLReader.this.charAttr.addAttributes(HTMLReader.this.styleAttributes);
                }
                HTMLReader.this.charAttr.addAttribute(tag, set.copyAttributes());
                final StyleSheet styleSheet = HTMLDocument.this.getStyleSheet();
                if (tag == HTML.Tag.B) {
                    styleSheet.addCSSAttribute(HTMLReader.this.charAttr, CSS.Attribute.FONT_WEIGHT, "bold");
                }
                else if (tag == HTML.Tag.I) {
                    styleSheet.addCSSAttribute(HTMLReader.this.charAttr, CSS.Attribute.FONT_STYLE, "italic");
                }
                else if (tag == HTML.Tag.U) {
                    final Object attribute = HTMLReader.this.charAttr.getAttribute(CSS.Attribute.TEXT_DECORATION);
                    final String s = "underline";
                    styleSheet.addCSSAttribute(HTMLReader.this.charAttr, CSS.Attribute.TEXT_DECORATION, (attribute != null) ? (s + "," + attribute.toString()) : s);
                }
                else if (tag == HTML.Tag.STRIKE) {
                    final Object attribute2 = HTMLReader.this.charAttr.getAttribute(CSS.Attribute.TEXT_DECORATION);
                    final String s2 = "line-through";
                    styleSheet.addCSSAttribute(HTMLReader.this.charAttr, CSS.Attribute.TEXT_DECORATION, (attribute2 != null) ? (s2 + "," + attribute2.toString()) : s2);
                }
                else if (tag == HTML.Tag.SUP) {
                    final Object attribute3 = HTMLReader.this.charAttr.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
                    final String s3 = "sup";
                    styleSheet.addCSSAttribute(HTMLReader.this.charAttr, CSS.Attribute.VERTICAL_ALIGN, (attribute3 != null) ? (s3 + "," + attribute3.toString()) : s3);
                }
                else if (tag == HTML.Tag.SUB) {
                    final Object attribute4 = HTMLReader.this.charAttr.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
                    final String s4 = "sub";
                    styleSheet.addCSSAttribute(HTMLReader.this.charAttr, CSS.Attribute.VERTICAL_ALIGN, (attribute4 != null) ? (s4 + "," + attribute4.toString()) : s4);
                }
                else if (tag == HTML.Tag.FONT) {
                    final String s5 = (String)set.getAttribute(HTML.Attribute.COLOR);
                    if (s5 != null) {
                        styleSheet.addCSSAttribute(HTMLReader.this.charAttr, CSS.Attribute.COLOR, s5);
                    }
                    final String s6 = (String)set.getAttribute(HTML.Attribute.FACE);
                    if (s6 != null) {
                        styleSheet.addCSSAttribute(HTMLReader.this.charAttr, CSS.Attribute.FONT_FAMILY, s6);
                    }
                    final String s7 = (String)set.getAttribute(HTML.Attribute.SIZE);
                    if (s7 != null) {
                        styleSheet.addCSSAttributeFromHTML(HTMLReader.this.charAttr, CSS.Attribute.FONT_SIZE, s7);
                    }
                }
            }
            
            @Override
            public void end(final HTML.Tag tag) {
                HTMLReader.this.popCharacterStyle();
            }
        }
        
        class AnchorAction extends CharacterAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                HTMLReader.this.emptyAnchor = true;
                super.start(tag, set);
            }
            
            @Override
            public void end(final HTML.Tag tag) {
                if (HTMLReader.this.emptyAnchor) {
                    HTMLReader.this.addContent(new char[] { '\n' }, 0, 1);
                }
                super.end(tag);
            }
        }
        
        class TitleAction extends HiddenAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                HTMLReader.this.inTitle = true;
                super.start(tag, set);
            }
            
            @Override
            public void end(final HTML.Tag tag) {
                HTMLReader.this.inTitle = false;
                super.end(tag);
            }
            
            @Override
            boolean isEmpty(final HTML.Tag tag) {
                return false;
            }
        }
        
        class BaseAction extends TagAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                final String s = (String)set.getAttribute(HTML.Attribute.HREF);
                if (s != null) {
                    try {
                        HTMLDocument.this.setBase(new URL(HTMLDocument.this.base, s));
                        HTMLDocument.this.hasBaseTag = true;
                    }
                    catch (final MalformedURLException ex) {}
                }
                HTMLDocument.this.baseTarget = (String)set.getAttribute(HTML.Attribute.TARGET);
            }
        }
        
        class ObjectAction extends SpecialAction
        {
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                if (tag == HTML.Tag.PARAM) {
                    this.addParameter(set);
                }
                else {
                    super.start(tag, set);
                }
            }
            
            @Override
            public void end(final HTML.Tag tag) {
                if (tag != HTML.Tag.PARAM) {
                    super.end(tag);
                }
            }
            
            void addParameter(final AttributeSet set) {
                final String s = (String)set.getAttribute(HTML.Attribute.NAME);
                final String s2 = (String)set.getAttribute(HTML.Attribute.VALUE);
                if (s != null && s2 != null) {
                    ((MutableAttributeSet)HTMLReader.this.parseBuffer.lastElement().getAttributes()).addAttribute(s, s2);
                }
            }
        }
        
        public class FormAction extends SpecialAction
        {
            Object selectModel;
            int optionCount;
            
            @Override
            public void start(final HTML.Tag tag, final MutableAttributeSet set) {
                if (tag == HTML.Tag.INPUT) {
                    String s = (String)set.getAttribute(HTML.Attribute.TYPE);
                    if (s == null) {
                        s = "text";
                        set.addAttribute(HTML.Attribute.TYPE, "text");
                    }
                    this.setModel(s, set);
                }
                else if (tag == HTML.Tag.TEXTAREA) {
                    HTMLReader.this.inTextArea = true;
                    HTMLReader.this.textAreaDocument = new TextAreaDocument();
                    set.addAttribute(StyleConstants.ModelAttribute, HTMLReader.this.textAreaDocument);
                }
                else if (tag == HTML.Tag.SELECT) {
                    final int integerAttributeValue = HTML.getIntegerAttributeValue(set, HTML.Attribute.SIZE, 1);
                    final boolean b = set.getAttribute(HTML.Attribute.MULTIPLE) != null;
                    if (integerAttributeValue > 1 || b) {
                        final OptionListModel selectModel = new OptionListModel();
                        if (b) {
                            selectModel.setSelectionMode(2);
                        }
                        this.selectModel = selectModel;
                    }
                    else {
                        this.selectModel = new OptionComboBoxModel();
                    }
                    set.addAttribute(StyleConstants.ModelAttribute, this.selectModel);
                }
                if (tag == HTML.Tag.OPTION) {
                    HTMLReader.this.option = new Option(set);
                    if (this.selectModel instanceof OptionListModel) {
                        final OptionListModel optionListModel = (OptionListModel)this.selectModel;
                        optionListModel.addElement(HTMLReader.this.option);
                        if (HTMLReader.this.option.isSelected()) {
                            optionListModel.addSelectionInterval(this.optionCount, this.optionCount);
                            optionListModel.setInitialSelection(this.optionCount);
                        }
                    }
                    else if (this.selectModel instanceof OptionComboBoxModel) {
                        final OptionComboBoxModel optionComboBoxModel = (OptionComboBoxModel)this.selectModel;
                        optionComboBoxModel.addElement(HTMLReader.this.option);
                        if (HTMLReader.this.option.isSelected()) {
                            optionComboBoxModel.setSelectedItem(HTMLReader.this.option);
                            optionComboBoxModel.setInitialSelection(HTMLReader.this.option);
                        }
                    }
                    ++this.optionCount;
                }
                else {
                    super.start(tag, set);
                }
            }
            
            @Override
            public void end(final HTML.Tag tag) {
                if (tag == HTML.Tag.OPTION) {
                    HTMLReader.this.option = null;
                }
                else {
                    if (tag == HTML.Tag.SELECT) {
                        this.selectModel = null;
                        this.optionCount = 0;
                    }
                    else if (tag == HTML.Tag.TEXTAREA) {
                        HTMLReader.this.inTextArea = false;
                        HTMLReader.this.textAreaDocument.storeInitialText();
                    }
                    super.end(tag);
                }
            }
            
            void setModel(final String s, final MutableAttributeSet set) {
                if (s.equals("submit") || s.equals("reset") || s.equals("image")) {
                    set.addAttribute(StyleConstants.ModelAttribute, new DefaultButtonModel());
                }
                else if (s.equals("text") || s.equals("password")) {
                    final int integerAttributeValue = HTML.getIntegerAttributeValue(set, HTML.Attribute.MAXLENGTH, -1);
                    PlainDocument plainDocument;
                    if (integerAttributeValue > 0) {
                        plainDocument = new FixedLengthDocument(integerAttributeValue);
                    }
                    else {
                        plainDocument = new PlainDocument();
                    }
                    final String s2 = (String)set.getAttribute(HTML.Attribute.VALUE);
                    try {
                        plainDocument.insertString(0, s2, null);
                    }
                    catch (final BadLocationException ex) {}
                    set.addAttribute(StyleConstants.ModelAttribute, plainDocument);
                }
                else if (s.equals("file")) {
                    set.addAttribute(StyleConstants.ModelAttribute, new PlainDocument());
                }
                else if (s.equals("checkbox") || s.equals("radio")) {
                    final JToggleButton.ToggleButtonModel toggleButtonModel = new JToggleButton.ToggleButtonModel();
                    if (s.equals("radio")) {
                        final String s3 = (String)set.getAttribute(HTML.Attribute.NAME);
                        if (HTMLDocument.this.radioButtonGroupsMap == null) {
                            HTMLDocument.this.radioButtonGroupsMap = (HashMap<String, ButtonGroup>)new HashMap();
                        }
                        ButtonGroup group = HTMLDocument.this.radioButtonGroupsMap.get(s3);
                        if (group == null) {
                            group = new ButtonGroup();
                            HTMLDocument.this.radioButtonGroupsMap.put(s3, group);
                        }
                        toggleButtonModel.setGroup(group);
                    }
                    toggleButtonModel.setSelected(set.getAttribute(HTML.Attribute.CHECKED) != null);
                    set.addAttribute(StyleConstants.ModelAttribute, toggleButtonModel);
                }
            }
        }
    }
    
    static class TaggedAttributeSet extends SimpleAttributeSet
    {
    }
    
    public class RunElement extends LeafElement
    {
        public RunElement(final Element element, final AttributeSet set, final int n, final int n2) {
            super(element, set, n, n2);
        }
        
        @Override
        public String getName() {
            final Object attribute = this.getAttribute(StyleConstants.NameAttribute);
            if (attribute != null) {
                return attribute.toString();
            }
            return super.getName();
        }
        
        @Override
        public AttributeSet getResolveParent() {
            return null;
        }
    }
    
    public class BlockElement extends BranchElement
    {
        public BlockElement(final Element element, final AttributeSet set) {
            super(element, set);
        }
        
        @Override
        public String getName() {
            final Object attribute = this.getAttribute(StyleConstants.NameAttribute);
            if (attribute != null) {
                return attribute.toString();
            }
            return super.getName();
        }
        
        @Override
        public AttributeSet getResolveParent() {
            return null;
        }
    }
    
    private static class FixedLengthDocument extends PlainDocument
    {
        private int maxLength;
        
        public FixedLengthDocument(final int maxLength) {
            this.maxLength = maxLength;
        }
        
        @Override
        public void insertString(final int n, final String s, final AttributeSet set) throws BadLocationException {
            if (s != null && s.length() + this.getLength() <= this.maxLength) {
                super.insertString(n, s, set);
            }
        }
    }
}
