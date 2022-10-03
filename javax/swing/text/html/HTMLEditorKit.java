package javax.swing.text.html;

import javax.accessibility.AccessibleAction;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.text.DefaultHighlighter;
import java.awt.Color;
import javax.swing.text.Highlighter;
import javax.swing.text.ElementIterator;
import javax.swing.event.CaretEvent;
import java.awt.event.ActionEvent;
import javax.swing.text.EditorKit;
import javax.swing.text.AbstractDocument;
import java.awt.event.ComponentEvent;
import javax.swing.JComponent;
import java.awt.Container;
import java.lang.ref.WeakReference;
import javax.swing.SizeRequirements;
import javax.swing.JViewport;
import java.lang.ref.Reference;
import java.awt.event.ComponentListener;
import javax.swing.text.IconView;
import javax.swing.text.ComponentView;
import javax.swing.text.BoxView;
import javax.swing.text.LabelView;
import java.awt.Shape;
import javax.swing.text.View;
import java.awt.event.InputEvent;
import java.net.MalformedURLException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.plaf.TextUI;
import java.awt.Rectangle;
import java.awt.Point;
import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import javax.swing.text.Position;
import java.io.Serializable;
import java.awt.event.MouseAdapter;
import javax.swing.text.JTextComponent;
import java.util.Enumeration;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.TextAction;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.InputStream;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import sun.awt.AppContext;
import javax.swing.event.CaretListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import javax.swing.text.StyledDocument;
import java.io.Writer;
import java.io.StringReader;
import javax.swing.text.BadLocationException;
import java.io.Reader;
import java.io.IOException;
import javax.swing.text.Document;
import javax.swing.Action;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.ViewFactory;
import java.awt.Cursor;
import javax.accessibility.AccessibleContext;
import javax.swing.JEditorPane;
import javax.accessibility.Accessible;
import javax.swing.text.StyledEditorKit;

public class HTMLEditorKit extends StyledEditorKit implements Accessible
{
    private JEditorPane theEditor;
    public static final String DEFAULT_CSS = "default.css";
    private AccessibleContext accessibleContext;
    private static final Cursor MoveCursor;
    private static final Cursor DefaultCursor;
    private static final ViewFactory defaultFactory;
    MutableAttributeSet input;
    private static final Object DEFAULT_STYLES_KEY;
    private LinkController linkHandler;
    private static Parser defaultParser;
    private Cursor defaultCursor;
    private Cursor linkCursor;
    private boolean isAutoFormSubmission;
    public static final String BOLD_ACTION = "html-bold-action";
    public static final String ITALIC_ACTION = "html-italic-action";
    public static final String PARA_INDENT_LEFT = "html-para-indent-left";
    public static final String PARA_INDENT_RIGHT = "html-para-indent-right";
    public static final String FONT_CHANGE_BIGGER = "html-font-bigger";
    public static final String FONT_CHANGE_SMALLER = "html-font-smaller";
    public static final String COLOR_ACTION = "html-color-action";
    public static final String LOGICAL_STYLE_ACTION = "html-logical-style-action";
    public static final String IMG_ALIGN_TOP = "html-image-align-top";
    public static final String IMG_ALIGN_MIDDLE = "html-image-align-middle";
    public static final String IMG_ALIGN_BOTTOM = "html-image-align-bottom";
    public static final String IMG_BORDER = "html-image-border";
    private static final String INSERT_TABLE_HTML = "<table border=1><tr><td></td></tr></table>";
    private static final String INSERT_UL_HTML = "<ul><li></li></ul>";
    private static final String INSERT_OL_HTML = "<ol><li></li></ol>";
    private static final String INSERT_HR_HTML = "<hr>";
    private static final String INSERT_PRE_HTML = "<pre></pre>";
    private static final NavigateLinkAction nextLinkAction;
    private static final NavigateLinkAction previousLinkAction;
    private static final ActivateLinkAction activateLinkAction;
    private static final Action[] defaultActions;
    private boolean foundLink;
    private int prevHypertextOffset;
    private Object linkNavigationTag;
    
    public HTMLEditorKit() {
        this.linkHandler = new LinkController();
        this.defaultCursor = HTMLEditorKit.DefaultCursor;
        this.linkCursor = HTMLEditorKit.MoveCursor;
        this.isAutoFormSubmission = true;
        this.foundLink = false;
        this.prevHypertextOffset = -1;
    }
    
    @Override
    public String getContentType() {
        return "text/html";
    }
    
    @Override
    public ViewFactory getViewFactory() {
        return HTMLEditorKit.defaultFactory;
    }
    
    @Override
    public Document createDefaultDocument() {
        final StyleSheet styleSheet = this.getStyleSheet();
        final StyleSheet styleSheet2 = new StyleSheet();
        styleSheet2.addStyleSheet(styleSheet);
        final HTMLDocument htmlDocument = new HTMLDocument(styleSheet2);
        htmlDocument.setParser(this.getParser());
        htmlDocument.setAsynchronousLoadPriority(4);
        htmlDocument.setTokenThreshold(100);
        return htmlDocument;
    }
    
    private Parser ensureParser(final HTMLDocument htmlDocument) throws IOException {
        Parser parser = htmlDocument.getParser();
        if (parser == null) {
            parser = this.getParser();
        }
        if (parser == null) {
            throw new IOException("Can't load parser");
        }
        return parser;
    }
    
    @Override
    public void read(final Reader reader, final Document document, final int n) throws IOException, BadLocationException {
        if (document instanceof HTMLDocument) {
            final HTMLDocument htmlDocument = (HTMLDocument)document;
            if (n > document.getLength()) {
                throw new BadLocationException("Invalid location", n);
            }
            final Parser ensureParser = this.ensureParser(htmlDocument);
            final ParserCallback reader2 = htmlDocument.getReader(n);
            final Boolean b = (Boolean)document.getProperty("IgnoreCharsetDirective");
            ensureParser.parse(reader, reader2, b != null && b);
            reader2.flush();
        }
        else {
            super.read(reader, document, n);
        }
    }
    
    public void insertHTML(final HTMLDocument htmlDocument, final int n, final String s, final int n2, final int n3, final HTML.Tag tag) throws BadLocationException, IOException {
        if (n > htmlDocument.getLength()) {
            throw new BadLocationException("Invalid location", n);
        }
        final Parser ensureParser = this.ensureParser(htmlDocument);
        final ParserCallback reader = htmlDocument.getReader(n, n2, n3, tag);
        final Boolean b = (Boolean)htmlDocument.getProperty("IgnoreCharsetDirective");
        ensureParser.parse(new StringReader(s), reader, b != null && b);
        reader.flush();
    }
    
    @Override
    public void write(final Writer writer, final Document document, final int n, final int n2) throws IOException, BadLocationException {
        if (document instanceof HTMLDocument) {
            new HTMLWriter(writer, (HTMLDocument)document, n, n2).write();
        }
        else if (document instanceof StyledDocument) {
            new MinimalHTMLWriter(writer, (StyledDocument)document, n, n2).write();
        }
        else {
            super.write(writer, document, n, n2);
        }
    }
    
    @Override
    public void install(final JEditorPane theEditor) {
        theEditor.addMouseListener(this.linkHandler);
        theEditor.addMouseMotionListener(this.linkHandler);
        theEditor.addCaretListener(HTMLEditorKit.nextLinkAction);
        super.install(theEditor);
        this.theEditor = theEditor;
    }
    
    @Override
    public void deinstall(final JEditorPane editorPane) {
        editorPane.removeMouseListener(this.linkHandler);
        editorPane.removeMouseMotionListener(this.linkHandler);
        editorPane.removeCaretListener(HTMLEditorKit.nextLinkAction);
        super.deinstall(editorPane);
        this.theEditor = null;
    }
    
    public void setStyleSheet(final StyleSheet styleSheet) {
        if (styleSheet == null) {
            AppContext.getAppContext().remove(HTMLEditorKit.DEFAULT_STYLES_KEY);
        }
        else {
            AppContext.getAppContext().put(HTMLEditorKit.DEFAULT_STYLES_KEY, styleSheet);
        }
    }
    
    public StyleSheet getStyleSheet() {
        final AppContext appContext = AppContext.getAppContext();
        StyleSheet styleSheet = (StyleSheet)appContext.get(HTMLEditorKit.DEFAULT_STYLES_KEY);
        if (styleSheet == null) {
            styleSheet = new StyleSheet();
            appContext.put(HTMLEditorKit.DEFAULT_STYLES_KEY, styleSheet);
            try {
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getResourceAsStream("default.css"), "ISO-8859-1"));
                styleSheet.loadRules(bufferedReader, null);
                bufferedReader.close();
            }
            catch (final Throwable t) {}
        }
        return styleSheet;
    }
    
    static InputStream getResourceAsStream(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction<InputStream>() {
            @Override
            public InputStream run() {
                return HTMLEditorKit.class.getResourceAsStream(s);
            }
        });
    }
    
    @Override
    public Action[] getActions() {
        return TextAction.augmentList(super.getActions(), HTMLEditorKit.defaultActions);
    }
    
    @Override
    protected void createInputAttributes(final Element element, final MutableAttributeSet set) {
        set.removeAttributes(set);
        set.addAttributes(element.getAttributes());
        set.removeAttribute(StyleConstants.ComposedTextAttribute);
        final Object attribute = set.getAttribute(StyleConstants.NameAttribute);
        if (attribute instanceof HTML.Tag) {
            final HTML.Tag tag = (HTML.Tag)attribute;
            if (tag == HTML.Tag.IMG) {
                set.removeAttribute(HTML.Attribute.SRC);
                set.removeAttribute(HTML.Attribute.HEIGHT);
                set.removeAttribute(HTML.Attribute.WIDTH);
                set.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
            }
            else if (tag == HTML.Tag.HR || tag == HTML.Tag.BR) {
                set.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
            }
            else if (tag == HTML.Tag.COMMENT) {
                set.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
                set.removeAttribute(HTML.Attribute.COMMENT);
            }
            else if (tag == HTML.Tag.INPUT) {
                set.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
                set.removeAttribute(HTML.Tag.INPUT);
            }
            else if (tag instanceof HTML.UnknownTag) {
                set.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
                set.removeAttribute(HTML.Attribute.ENDTAG);
            }
        }
    }
    
    @Override
    public MutableAttributeSet getInputAttributes() {
        if (this.input == null) {
            this.input = this.getStyleSheet().addStyle(null, null);
        }
        return this.input;
    }
    
    public void setDefaultCursor(final Cursor defaultCursor) {
        this.defaultCursor = defaultCursor;
    }
    
    public Cursor getDefaultCursor() {
        return this.defaultCursor;
    }
    
    public void setLinkCursor(final Cursor linkCursor) {
        this.linkCursor = linkCursor;
    }
    
    public Cursor getLinkCursor() {
        return this.linkCursor;
    }
    
    public boolean isAutoFormSubmission() {
        return this.isAutoFormSubmission;
    }
    
    public void setAutoFormSubmission(final boolean isAutoFormSubmission) {
        this.isAutoFormSubmission = isAutoFormSubmission;
    }
    
    @Override
    public Object clone() {
        final HTMLEditorKit htmlEditorKit = (HTMLEditorKit)super.clone();
        if (htmlEditorKit != null) {
            htmlEditorKit.input = null;
            htmlEditorKit.linkHandler = new LinkController();
        }
        return htmlEditorKit;
    }
    
    protected Parser getParser() {
        if (HTMLEditorKit.defaultParser == null) {
            try {
                HTMLEditorKit.defaultParser = (Parser)Class.forName("javax.swing.text.html.parser.ParserDelegator").newInstance();
            }
            catch (final Throwable t) {}
        }
        return HTMLEditorKit.defaultParser;
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.theEditor == null) {
            return null;
        }
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleHTML(this.theEditor).getAccessibleContext();
        }
        return this.accessibleContext;
    }
    
    private static Object getAttrValue(final AttributeSet set, final HTML.Attribute attribute) {
        final Enumeration<?> attributeNames = set.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final Object nextElement = attributeNames.nextElement();
            final Object attribute2 = set.getAttribute(nextElement);
            if (attribute2 instanceof AttributeSet) {
                final Object attrValue = getAttrValue((AttributeSet)attribute2, attribute);
                if (attrValue != null) {
                    return attrValue;
                }
                continue;
            }
            else {
                if (nextElement == attribute) {
                    return attribute2;
                }
                continue;
            }
        }
        return null;
    }
    
    private static int getBodyElementStart(final JTextComponent textComponent) {
        final Element element = textComponent.getDocument().getRootElements()[0];
        for (int i = 0; i < element.getElementCount(); ++i) {
            final Element element2 = element.getElement(i);
            if ("body".equals(element2.getName())) {
                return element2.getStartOffset();
            }
        }
        return 0;
    }
    
    static {
        MoveCursor = Cursor.getPredefinedCursor(12);
        DefaultCursor = Cursor.getPredefinedCursor(0);
        defaultFactory = new HTMLFactory();
        DEFAULT_STYLES_KEY = new Object();
        HTMLEditorKit.defaultParser = null;
        nextLinkAction = new NavigateLinkAction("next-link-action");
        previousLinkAction = new NavigateLinkAction("previous-link-action");
        activateLinkAction = new ActivateLinkAction("activate-link-action");
        defaultActions = new Action[] { new InsertHTMLTextAction("InsertTable", "<table border=1><tr><td></td></tr></table>", HTML.Tag.BODY, HTML.Tag.TABLE), new InsertHTMLTextAction("InsertTableRow", "<table border=1><tr><td></td></tr></table>", HTML.Tag.TABLE, HTML.Tag.TR, HTML.Tag.BODY, HTML.Tag.TABLE), new InsertHTMLTextAction("InsertTableDataCell", "<table border=1><tr><td></td></tr></table>", HTML.Tag.TR, HTML.Tag.TD, HTML.Tag.BODY, HTML.Tag.TABLE), new InsertHTMLTextAction("InsertUnorderedList", "<ul><li></li></ul>", HTML.Tag.BODY, HTML.Tag.UL), new InsertHTMLTextAction("InsertUnorderedListItem", "<ul><li></li></ul>", HTML.Tag.UL, HTML.Tag.LI, HTML.Tag.BODY, HTML.Tag.UL), new InsertHTMLTextAction("InsertOrderedList", "<ol><li></li></ol>", HTML.Tag.BODY, HTML.Tag.OL), new InsertHTMLTextAction("InsertOrderedListItem", "<ol><li></li></ol>", HTML.Tag.OL, HTML.Tag.LI, HTML.Tag.BODY, HTML.Tag.OL), new InsertHRAction(), new InsertHTMLTextAction("InsertPre", "<pre></pre>", HTML.Tag.BODY, HTML.Tag.PRE), HTMLEditorKit.nextLinkAction, HTMLEditorKit.previousLinkAction, HTMLEditorKit.activateLinkAction, new BeginAction("caret-begin", false), new BeginAction("selection-begin", true) };
    }
    
    public static class LinkController extends MouseAdapter implements MouseMotionListener, Serializable
    {
        private Element curElem;
        private boolean curElemImage;
        private String href;
        private transient Position.Bias[] bias;
        private int curOffset;
        
        public LinkController() {
            this.curElem = null;
            this.curElemImage = false;
            this.href = null;
            this.bias = new Position.Bias[1];
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            final JEditorPane editorPane = (JEditorPane)mouseEvent.getSource();
            if (!editorPane.isEditable() && editorPane.isEnabled() && SwingUtilities.isLeftMouseButton(mouseEvent)) {
                final int viewToModel = editorPane.viewToModel(new Point(mouseEvent.getX(), mouseEvent.getY()));
                if (viewToModel >= 0) {
                    this.activateLink(viewToModel, editorPane, mouseEvent);
                }
            }
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            final JEditorPane editorPane = (JEditorPane)mouseEvent.getSource();
            if (!editorPane.isEnabled()) {
                return;
            }
            final HTMLEditorKit htmlEditorKit = (HTMLEditorKit)editorPane.getEditorKit();
            boolean b = true;
            Cursor cursor = htmlEditorKit.getDefaultCursor();
            if (!editorPane.isEditable()) {
                int viewToModel = editorPane.getUI().viewToModel(editorPane, new Point(mouseEvent.getX(), mouseEvent.getY()), this.bias);
                if (this.bias[0] == Position.Bias.Backward && viewToModel > 0) {
                    --viewToModel;
                }
                if (viewToModel >= 0 && editorPane.getDocument() instanceof HTMLDocument) {
                    final HTMLDocument htmlDocument = (HTMLDocument)editorPane.getDocument();
                    Element characterElement = htmlDocument.getCharacterElement(viewToModel);
                    if (!this.doesElementContainLocation(editorPane, characterElement, viewToModel, mouseEvent.getX(), mouseEvent.getY())) {
                        characterElement = null;
                    }
                    if (this.curElem != characterElement || this.curElemImage) {
                        final Element curElem = this.curElem;
                        this.curElem = characterElement;
                        String mapHREF = null;
                        this.curElemImage = false;
                        if (characterElement != null) {
                            final AttributeSet attributes = characterElement.getAttributes();
                            final AttributeSet set = (AttributeSet)attributes.getAttribute(HTML.Tag.A);
                            if (set == null) {
                                this.curElemImage = (attributes.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.IMG);
                                if (this.curElemImage) {
                                    mapHREF = this.getMapHREF(editorPane, htmlDocument, characterElement, attributes, viewToModel, mouseEvent.getX(), mouseEvent.getY());
                                }
                            }
                            else {
                                mapHREF = (String)set.getAttribute(HTML.Attribute.HREF);
                            }
                        }
                        if (mapHREF != this.href) {
                            this.fireEvents(editorPane, htmlDocument, mapHREF, curElem, mouseEvent);
                            if ((this.href = mapHREF) != null) {
                                cursor = htmlEditorKit.getLinkCursor();
                            }
                        }
                        else {
                            b = false;
                        }
                    }
                    else {
                        b = false;
                    }
                    this.curOffset = viewToModel;
                }
            }
            if (b && editorPane.getCursor() != cursor) {
                editorPane.setCursor(cursor);
            }
        }
        
        private String getMapHREF(final JEditorPane editorPane, final HTMLDocument htmlDocument, final Element element, final AttributeSet set, final int n, final int n2, final int n3) {
            final Object attribute = set.getAttribute(HTML.Attribute.USEMAP);
            if (attribute != null && attribute instanceof String) {
                final Map map = htmlDocument.getMap((String)attribute);
                if (map != null && n < htmlDocument.getLength()) {
                    final TextUI ui = editorPane.getUI();
                    Rectangle bounds;
                    try {
                        final Rectangle modelToView = ui.modelToView(editorPane, n, Position.Bias.Forward);
                        final Rectangle modelToView2 = ui.modelToView(editorPane, n + 1, Position.Bias.Backward);
                        bounds = modelToView.getBounds();
                        bounds.add((modelToView2 instanceof Rectangle) ? modelToView2 : modelToView2.getBounds());
                    }
                    catch (final BadLocationException ex) {
                        bounds = null;
                    }
                    if (bounds != null) {
                        final AttributeSet area = map.getArea(n2 - bounds.x, n3 - bounds.y, bounds.width, bounds.height);
                        if (area != null) {
                            return (String)area.getAttribute(HTML.Attribute.HREF);
                        }
                    }
                }
            }
            return null;
        }
        
        private boolean doesElementContainLocation(final JEditorPane editorPane, final Element element, final int n, final int n2, final int n3) {
            if (element != null && n > 0 && element.getStartOffset() == n) {
                try {
                    final TextUI ui = editorPane.getUI();
                    final Rectangle modelToView = ui.modelToView(editorPane, n, Position.Bias.Forward);
                    if (modelToView == null) {
                        return false;
                    }
                    final Rectangle rectangle = (modelToView instanceof Rectangle) ? modelToView : modelToView.getBounds();
                    final Rectangle modelToView2 = ui.modelToView(editorPane, element.getEndOffset(), Position.Bias.Backward);
                    if (modelToView2 != null) {
                        rectangle.add((modelToView2 instanceof Rectangle) ? modelToView2 : modelToView2.getBounds());
                    }
                    return rectangle.contains(n2, n3);
                }
                catch (final BadLocationException ex) {}
            }
            return true;
        }
        
        protected void activateLink(final int n, final JEditorPane editorPane) {
            this.activateLink(n, editorPane, null);
        }
        
        void activateLink(final int n, final JEditorPane editorPane, final MouseEvent mouseEvent) {
            final Document document = editorPane.getDocument();
            if (document instanceof HTMLDocument) {
                final HTMLDocument htmlDocument = (HTMLDocument)document;
                final Element characterElement = htmlDocument.getCharacterElement(n);
                final AttributeSet attributes = characterElement.getAttributes();
                final AttributeSet set = (AttributeSet)attributes.getAttribute(HTML.Tag.A);
                HyperlinkEvent hyperlinkEvent = null;
                int x = -1;
                int y = -1;
                if (mouseEvent != null) {
                    x = mouseEvent.getX();
                    y = mouseEvent.getY();
                }
                if (set == null) {
                    this.href = this.getMapHREF(editorPane, htmlDocument, characterElement, attributes, n, x, y);
                }
                else {
                    this.href = (String)set.getAttribute(HTML.Attribute.HREF);
                }
                if (this.href != null) {
                    hyperlinkEvent = this.createHyperlinkEvent(editorPane, htmlDocument, this.href, set, characterElement, mouseEvent);
                }
                if (hyperlinkEvent != null) {
                    editorPane.fireHyperlinkUpdate(hyperlinkEvent);
                }
            }
        }
        
        HyperlinkEvent createHyperlinkEvent(final JEditorPane editorPane, final HTMLDocument htmlDocument, final String s, final AttributeSet set, final Element element, final MouseEvent mouseEvent) {
            URL url;
            try {
                final URL base = htmlDocument.getBase();
                url = new URL(base, s);
                if (s != null && "file".equals(url.getProtocol()) && s.startsWith("#")) {
                    final String file = base.getFile();
                    final String file2 = url.getFile();
                    if (file != null && file2 != null && !file2.startsWith(file)) {
                        url = new URL(base, file + s);
                    }
                }
            }
            catch (final MalformedURLException ex) {
                url = null;
            }
            HyperlinkEvent hyperlinkEvent;
            if (!htmlDocument.isFrameDocument()) {
                hyperlinkEvent = new HyperlinkEvent(editorPane, HyperlinkEvent.EventType.ACTIVATED, url, s, element, mouseEvent);
            }
            else {
                String baseTarget = (set != null) ? ((String)set.getAttribute(HTML.Attribute.TARGET)) : null;
                if (baseTarget == null || baseTarget.equals("")) {
                    baseTarget = htmlDocument.getBaseTarget();
                }
                if (baseTarget == null || baseTarget.equals("")) {
                    baseTarget = "_self";
                }
                hyperlinkEvent = new HTMLFrameHyperlinkEvent(editorPane, HyperlinkEvent.EventType.ACTIVATED, url, s, element, mouseEvent, baseTarget);
            }
            return hyperlinkEvent;
        }
        
        void fireEvents(final JEditorPane editorPane, final HTMLDocument htmlDocument, final String s, final Element element, final MouseEvent mouseEvent) {
            if (this.href != null) {
                URL url;
                try {
                    url = new URL(htmlDocument.getBase(), this.href);
                }
                catch (final MalformedURLException ex) {
                    url = null;
                }
                editorPane.fireHyperlinkUpdate(new HyperlinkEvent(editorPane, HyperlinkEvent.EventType.EXITED, url, this.href, element, mouseEvent));
            }
            if (s != null) {
                URL url2;
                try {
                    url2 = new URL(htmlDocument.getBase(), s);
                }
                catch (final MalformedURLException ex2) {
                    url2 = null;
                }
                editorPane.fireHyperlinkUpdate(new HyperlinkEvent(editorPane, HyperlinkEvent.EventType.ENTERED, url2, s, this.curElem, mouseEvent));
            }
        }
    }
    
    public abstract static class Parser
    {
        public abstract void parse(final Reader p0, final ParserCallback p1, final boolean p2) throws IOException;
    }
    
    public static class ParserCallback
    {
        public static final Object IMPLIED;
        
        public void flush() throws BadLocationException {
        }
        
        public void handleText(final char[] array, final int n) {
        }
        
        public void handleComment(final char[] array, final int n) {
        }
        
        public void handleStartTag(final HTML.Tag tag, final MutableAttributeSet set, final int n) {
        }
        
        public void handleEndTag(final HTML.Tag tag, final int n) {
        }
        
        public void handleSimpleTag(final HTML.Tag tag, final MutableAttributeSet set, final int n) {
        }
        
        public void handleError(final String s, final int n) {
        }
        
        public void handleEndOfLineString(final String s) {
        }
        
        static {
            IMPLIED = "_implied_";
        }
    }
    
    public static class HTMLFactory implements ViewFactory
    {
        @Override
        public View create(final Element element) {
            final AttributeSet attributes = element.getAttributes();
            final Object attribute = attributes.getAttribute("$ename");
            final Object o = (attribute != null) ? null : attributes.getAttribute(StyleConstants.NameAttribute);
            if (o instanceof HTML.Tag) {
                final HTML.Tag tag = (HTML.Tag)o;
                if (tag == HTML.Tag.CONTENT) {
                    return new InlineView(element);
                }
                if (tag == HTML.Tag.IMPLIED) {
                    final String s = (String)element.getAttributes().getAttribute(CSS.Attribute.WHITE_SPACE);
                    if (s != null && s.equals("pre")) {
                        return new LineView(element);
                    }
                    return new ParagraphView(element);
                }
                else {
                    if (tag == HTML.Tag.P || tag == HTML.Tag.H1 || tag == HTML.Tag.H2 || tag == HTML.Tag.H3 || tag == HTML.Tag.H4 || tag == HTML.Tag.H5 || tag == HTML.Tag.H6 || tag == HTML.Tag.DT) {
                        return new ParagraphView(element);
                    }
                    if (tag == HTML.Tag.MENU || tag == HTML.Tag.DIR || tag == HTML.Tag.UL || tag == HTML.Tag.OL) {
                        return new ListView(element);
                    }
                    if (tag == HTML.Tag.BODY) {
                        return new BodyBlockView(element);
                    }
                    if (tag == HTML.Tag.HTML) {
                        return new BlockView(element, 1);
                    }
                    if (tag == HTML.Tag.LI || tag == HTML.Tag.CENTER || tag == HTML.Tag.DL || tag == HTML.Tag.DD || tag == HTML.Tag.DIV || tag == HTML.Tag.BLOCKQUOTE || tag == HTML.Tag.PRE || tag == HTML.Tag.FORM) {
                        return new BlockView(element, 1);
                    }
                    if (tag == HTML.Tag.NOFRAMES) {
                        return new NoFramesView(element, 1);
                    }
                    if (tag == HTML.Tag.IMG) {
                        return new ImageView(element);
                    }
                    if (tag == HTML.Tag.ISINDEX) {
                        return new IsindexView(element);
                    }
                    if (tag == HTML.Tag.HR) {
                        return new HRuleView(element);
                    }
                    if (tag == HTML.Tag.BR) {
                        return new BRView(element);
                    }
                    if (tag == HTML.Tag.TABLE) {
                        return new TableView(element);
                    }
                    if (tag == HTML.Tag.INPUT || tag == HTML.Tag.SELECT || tag == HTML.Tag.TEXTAREA) {
                        return new FormView(element);
                    }
                    if (tag == HTML.Tag.OBJECT) {
                        return new ObjectView(element);
                    }
                    if (tag == HTML.Tag.FRAMESET) {
                        if (element.getAttributes().isDefined(HTML.Attribute.ROWS)) {
                            return new FrameSetView(element, 1);
                        }
                        if (element.getAttributes().isDefined(HTML.Attribute.COLS)) {
                            return new FrameSetView(element, 0);
                        }
                        throw new RuntimeException("Can't build a" + tag + ", " + element + ":no ROWS or COLS defined.");
                    }
                    else {
                        if (tag == HTML.Tag.FRAME) {
                            return new FrameView(element);
                        }
                        if (tag instanceof HTML.UnknownTag) {
                            return new HiddenTagView(element);
                        }
                        if (tag == HTML.Tag.COMMENT) {
                            return new CommentView(element);
                        }
                        if (tag == HTML.Tag.HEAD) {
                            return new BlockView(element, 0) {
                                @Override
                                public float getPreferredSpan(final int n) {
                                    return 0.0f;
                                }
                                
                                @Override
                                public float getMinimumSpan(final int n) {
                                    return 0.0f;
                                }
                                
                                @Override
                                public float getMaximumSpan(final int n) {
                                    return 0.0f;
                                }
                                
                                @Override
                                protected void loadChildren(final ViewFactory viewFactory) {
                                }
                                
                                @Override
                                public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
                                    return shape;
                                }
                                
                                @Override
                                public int getNextVisualPositionFrom(final int n, final Position.Bias bias, final Shape shape, final int n2, final Position.Bias[] array) {
                                    return this.getElement().getEndOffset();
                                }
                            };
                        }
                        if (tag == HTML.Tag.TITLE || tag == HTML.Tag.META || tag == HTML.Tag.LINK || tag == HTML.Tag.STYLE || tag == HTML.Tag.SCRIPT || tag == HTML.Tag.AREA || tag == HTML.Tag.MAP || tag == HTML.Tag.PARAM || tag == HTML.Tag.APPLET) {
                            return new HiddenTagView(element);
                        }
                    }
                }
            }
            final String s2 = (String)((attribute != null) ? attribute : element.getName());
            if (s2 != null) {
                if (s2.equals("content")) {
                    return new LabelView(element);
                }
                if (s2.equals("paragraph")) {
                    return new ParagraphView(element);
                }
                if (s2.equals("section")) {
                    return new BoxView(element, 1);
                }
                if (s2.equals("component")) {
                    return new ComponentView(element);
                }
                if (s2.equals("icon")) {
                    return new IconView(element);
                }
            }
            return new LabelView(element);
        }
        
        static class BodyBlockView extends BlockView implements ComponentListener
        {
            private Reference<JViewport> cachedViewPort;
            private boolean isListening;
            private int viewVisibleWidth;
            private int componentVisibleWidth;
            
            public BodyBlockView(final Element element) {
                super(element, 1);
                this.cachedViewPort = null;
                this.isListening = false;
                this.viewVisibleWidth = Integer.MAX_VALUE;
                this.componentVisibleWidth = Integer.MAX_VALUE;
            }
            
            @Override
            protected SizeRequirements calculateMajorAxisRequirements(final int n, SizeRequirements calculateMajorAxisRequirements) {
                calculateMajorAxisRequirements = super.calculateMajorAxisRequirements(n, calculateMajorAxisRequirements);
                calculateMajorAxisRequirements.maximum = Integer.MAX_VALUE;
                return calculateMajorAxisRequirements;
            }
            
            @Override
            protected void layoutMinorAxis(int min, final int n, final int[] array, final int[] array2) {
                final Container container = this.getContainer();
                final Container parent;
                if (container != null && container instanceof JEditorPane && (parent = container.getParent()) != null && parent instanceof JViewport) {
                    final JViewport viewport = (JViewport)parent;
                    if (this.cachedViewPort != null) {
                        final JViewport viewport2 = this.cachedViewPort.get();
                        if (viewport2 != null) {
                            if (viewport2 != viewport) {
                                viewport2.removeComponentListener(this);
                            }
                        }
                        else {
                            this.cachedViewPort = null;
                        }
                    }
                    if (this.cachedViewPort == null) {
                        viewport.addComponentListener(this);
                        this.cachedViewPort = new WeakReference<JViewport>(viewport);
                    }
                    this.componentVisibleWidth = viewport.getExtentSize().width;
                    if (this.componentVisibleWidth > 0) {
                        this.viewVisibleWidth = this.componentVisibleWidth - container.getInsets().left - this.getLeftInset();
                        min = Math.min(min, this.viewVisibleWidth);
                    }
                }
                else if (this.cachedViewPort != null) {
                    final JViewport viewport3 = this.cachedViewPort.get();
                    if (viewport3 != null) {
                        viewport3.removeComponentListener(this);
                    }
                    this.cachedViewPort = null;
                }
                super.layoutMinorAxis(min, n, array, array2);
            }
            
            @Override
            public void setParent(final View parent) {
                if (parent == null && this.cachedViewPort != null) {
                    final JViewport value;
                    if ((value = this.cachedViewPort.get()) != null) {
                        value.removeComponentListener(this);
                    }
                    this.cachedViewPort = null;
                }
                super.setParent(parent);
            }
            
            @Override
            public void componentResized(final ComponentEvent componentEvent) {
                if (!(componentEvent.getSource() instanceof JViewport)) {
                    return;
                }
                if (this.componentVisibleWidth != ((JViewport)componentEvent.getSource()).getExtentSize().width && this.getDocument() instanceof AbstractDocument) {
                    final AbstractDocument abstractDocument = (AbstractDocument)this.getDocument();
                    abstractDocument.readLock();
                    try {
                        this.layoutChanged(0);
                        this.preferenceChanged(null, true, true);
                    }
                    finally {
                        abstractDocument.readUnlock();
                    }
                }
            }
            
            @Override
            public void componentHidden(final ComponentEvent componentEvent) {
            }
            
            @Override
            public void componentMoved(final ComponentEvent componentEvent) {
            }
            
            @Override
            public void componentShown(final ComponentEvent componentEvent) {
            }
        }
    }
    
    public abstract static class HTMLTextAction extends StyledTextAction
    {
        public HTMLTextAction(final String s) {
            super(s);
        }
        
        protected HTMLDocument getHTMLDocument(final JEditorPane editorPane) {
            final Document document = editorPane.getDocument();
            if (document instanceof HTMLDocument) {
                return (HTMLDocument)document;
            }
            throw new IllegalArgumentException("document must be HTMLDocument");
        }
        
        protected HTMLEditorKit getHTMLEditorKit(final JEditorPane editorPane) {
            final EditorKit editorKit = editorPane.getEditorKit();
            if (editorKit instanceof HTMLEditorKit) {
                return (HTMLEditorKit)editorKit;
            }
            throw new IllegalArgumentException("EditorKit must be HTMLEditorKit");
        }
        
        protected Element[] getElementsAt(final HTMLDocument htmlDocument, final int n) {
            return this.getElementsAt(htmlDocument.getDefaultRootElement(), n, 0);
        }
        
        private Element[] getElementsAt(final Element element, final int n, final int n2) {
            if (element.isLeaf()) {
                final Element[] array = new Element[n2 + 1];
                array[n2] = element;
                return array;
            }
            final Element[] elements = this.getElementsAt(element.getElement(element.getElementIndex(n)), n, n2 + 1);
            elements[n2] = element;
            return elements;
        }
        
        protected int elementCountToTag(final HTMLDocument htmlDocument, final int n, final HTML.Tag tag) {
            int n2;
            Element element;
            for (n2 = -1, element = htmlDocument.getCharacterElement(n); element != null && element.getAttributes().getAttribute(StyleConstants.NameAttribute) != tag; element = element.getParentElement(), ++n2) {}
            if (element == null) {
                return -1;
            }
            return n2;
        }
        
        protected Element findElementMatchingTag(final HTMLDocument htmlDocument, final int n, final HTML.Tag tag) {
            Element element = htmlDocument.getDefaultRootElement();
            Element element2 = null;
            while (element != null) {
                if (element.getAttributes().getAttribute(StyleConstants.NameAttribute) == tag) {
                    element2 = element;
                }
                element = element.getElement(element.getElementIndex(n));
            }
            return element2;
        }
    }
    
    public static class InsertHTMLTextAction extends HTMLTextAction
    {
        protected String html;
        protected HTML.Tag parentTag;
        protected HTML.Tag addTag;
        protected HTML.Tag alternateParentTag;
        protected HTML.Tag alternateAddTag;
        boolean adjustSelection;
        
        public InsertHTMLTextAction(final String s, final String s2, final HTML.Tag tag, final HTML.Tag tag2) {
            this(s, s2, tag, tag2, null, null);
        }
        
        public InsertHTMLTextAction(final String s, final String s2, final HTML.Tag tag, final HTML.Tag tag2, final HTML.Tag tag3, final HTML.Tag tag4) {
            this(s, s2, tag, tag2, tag3, tag4, true);
        }
        
        InsertHTMLTextAction(final String s, final String html, final HTML.Tag parentTag, final HTML.Tag addTag, final HTML.Tag alternateParentTag, final HTML.Tag alternateAddTag, final boolean adjustSelection) {
            super(s);
            this.html = html;
            this.parentTag = parentTag;
            this.addTag = addTag;
            this.alternateParentTag = alternateParentTag;
            this.alternateAddTag = alternateAddTag;
            this.adjustSelection = adjustSelection;
        }
        
        protected void insertHTML(final JEditorPane editorPane, final HTMLDocument htmlDocument, final int n, final String s, final int n2, final int n3, final HTML.Tag tag) {
            try {
                this.getHTMLEditorKit(editorPane).insertHTML(htmlDocument, n, s, n2, n3, tag);
            }
            catch (final IOException ex) {
                throw new RuntimeException("Unable to insert: " + ex);
            }
            catch (final BadLocationException ex2) {
                throw new RuntimeException("Unable to insert: " + ex2);
            }
        }
        
        protected void insertAtBoundary(final JEditorPane editorPane, final HTMLDocument htmlDocument, final int n, final Element element, final String s, final HTML.Tag tag, final HTML.Tag tag2) {
            this.insertAtBoundry(editorPane, htmlDocument, n, element, s, tag, tag2);
        }
        
        @Deprecated
        protected void insertAtBoundry(final JEditorPane editorPane, final HTMLDocument htmlDocument, int n, final Element element, final String s, final HTML.Tag tag, final HTML.Tag tag2) {
            final boolean b = n == 0;
            Element element3;
            if (n > 0 || element == null) {
                Element element2;
                for (element2 = htmlDocument.getDefaultRootElement(); element2 != null && element2.getStartOffset() != n && !element2.isLeaf(); element2 = element2.getElement(element2.getElementIndex(n))) {}
                element3 = ((element2 != null) ? element2.getParentElement() : null);
            }
            else {
                element3 = element;
            }
            if (element3 != null) {
                int n2 = 0;
                int n3 = 0;
                if (b && element != null) {
                    for (Element element4 = element3; element4 != null && !element4.isLeaf(); element4 = element4.getElement(element4.getElementIndex(n)), ++n2) {}
                }
                else {
                    Element element5 = element3;
                    --n;
                    while (element5 != null && !element5.isLeaf()) {
                        element5 = element5.getElement(element5.getElementIndex(n));
                        ++n2;
                    }
                    Element element6 = element3;
                    ++n;
                    while (element6 != null && element6 != element) {
                        element6 = element6.getElement(element6.getElementIndex(n));
                        ++n3;
                    }
                }
                this.insertHTML(editorPane, htmlDocument, n, s, Math.max(0, n2 - 1), n3, tag2);
            }
        }
        
        boolean insertIntoTag(final JEditorPane editorPane, final HTMLDocument htmlDocument, final int n, final HTML.Tag tag, final HTML.Tag tag2) {
            final Element elementMatchingTag = this.findElementMatchingTag(htmlDocument, n, tag);
            if (elementMatchingTag != null && elementMatchingTag.getStartOffset() == n) {
                this.insertAtBoundary(editorPane, htmlDocument, n, elementMatchingTag, this.html, tag, tag2);
                return true;
            }
            if (n > 0) {
                final int elementCountToTag = this.elementCountToTag(htmlDocument, n - 1, tag);
                if (elementCountToTag != -1) {
                    this.insertHTML(editorPane, htmlDocument, n, this.html, elementCountToTag, 0, tag2);
                    return true;
                }
            }
            return false;
        }
        
        void adjustSelection(final JEditorPane editorPane, final HTMLDocument htmlDocument, final int n, final int n2) {
            final int length = htmlDocument.getLength();
            if (length != n2 && n < length) {
                if (n > 0) {
                    String text;
                    try {
                        text = htmlDocument.getText(n - 1, 1);
                    }
                    catch (final BadLocationException ex) {
                        text = null;
                    }
                    if (text != null && text.length() > 0 && text.charAt(0) == '\n') {
                        editorPane.select(n, n);
                    }
                    else {
                        editorPane.select(n + 1, n + 1);
                    }
                }
                else {
                    editorPane.select(1, 1);
                }
            }
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JEditorPane editor = this.getEditor(actionEvent);
            if (editor != null) {
                final HTMLDocument htmlDocument = this.getHTMLDocument(editor);
                final int selectionStart = editor.getSelectionStart();
                final int length = htmlDocument.getLength();
                final boolean b = this.insertIntoTag(editor, htmlDocument, selectionStart, this.parentTag, this.addTag) || this.alternateParentTag == null || this.insertIntoTag(editor, htmlDocument, selectionStart, this.alternateParentTag, this.alternateAddTag);
                if (this.adjustSelection && b) {
                    this.adjustSelection(editor, htmlDocument, selectionStart, length);
                }
            }
        }
    }
    
    static class InsertHRAction extends InsertHTMLTextAction
    {
        InsertHRAction() {
            super("InsertHR", "<hr>", null, HTML.Tag.IMPLIED, null, null, false);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JEditorPane editor = this.getEditor(actionEvent);
            if (editor != null) {
                final Element paragraphElement = this.getHTMLDocument(editor).getParagraphElement(editor.getSelectionStart());
                if (paragraphElement.getParentElement() != null) {
                    this.parentTag = (HTML.Tag)paragraphElement.getParentElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
                    super.actionPerformed(actionEvent);
                }
            }
        }
    }
    
    static class NavigateLinkAction extends TextAction implements CaretListener
    {
        private static final FocusHighlightPainter focusPainter;
        private final boolean focusBack;
        
        public NavigateLinkAction(final String s) {
            super(s);
            this.focusBack = "previous-link-action".equals(s);
        }
        
        @Override
        public void caretUpdate(final CaretEvent caretEvent) {
            final Object source = caretEvent.getSource();
            if (source instanceof JTextComponent) {
                final JTextComponent textComponent = (JTextComponent)source;
                final HTMLEditorKit htmlEditorKit = this.getHTMLEditorKit(textComponent);
                if (htmlEditorKit != null && htmlEditorKit.foundLink) {
                    htmlEditorKit.foundLink = false;
                    textComponent.getAccessibleContext().firePropertyChange("AccessibleHypertextOffset", htmlEditorKit.prevHypertextOffset, caretEvent.getDot());
                }
            }
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent == null || textComponent.isEditable()) {
                return;
            }
            final Document document = textComponent.getDocument();
            final HTMLEditorKit htmlEditorKit = this.getHTMLEditorKit(textComponent);
            if (document == null || htmlEditorKit == null) {
                return;
            }
            final ElementIterator elementIterator = new ElementIterator(document);
            final int caretPosition = textComponent.getCaretPosition();
            int startOffset = -1;
            int endOffset = -1;
            Element next;
            while ((next = elementIterator.next()) != null) {
                final String name = next.getName();
                final Object access$200 = getAttrValue(next.getAttributes(), HTML.Attribute.HREF);
                if (!name.equals(HTML.Tag.OBJECT.toString()) && access$200 == null) {
                    continue;
                }
                final int startOffset2 = next.getStartOffset();
                if (this.focusBack) {
                    if (startOffset2 >= caretPosition && startOffset >= 0) {
                        htmlEditorKit.foundLink = true;
                        textComponent.setCaretPosition(startOffset);
                        this.moveCaretPosition(textComponent, htmlEditorKit, startOffset, endOffset);
                        htmlEditorKit.prevHypertextOffset = startOffset;
                        return;
                    }
                }
                else if (startOffset2 > caretPosition) {
                    htmlEditorKit.foundLink = true;
                    textComponent.setCaretPosition(startOffset2);
                    this.moveCaretPosition(textComponent, htmlEditorKit, startOffset2, next.getEndOffset());
                    htmlEditorKit.prevHypertextOffset = startOffset2;
                    return;
                }
                startOffset = next.getStartOffset();
                endOffset = next.getEndOffset();
            }
            if (this.focusBack && startOffset >= 0) {
                htmlEditorKit.foundLink = true;
                textComponent.setCaretPosition(startOffset);
                this.moveCaretPosition(textComponent, htmlEditorKit, startOffset, endOffset);
                htmlEditorKit.prevHypertextOffset = startOffset;
            }
        }
        
        private void moveCaretPosition(final JTextComponent textComponent, final HTMLEditorKit htmlEditorKit, final int n, final int n2) {
            final Highlighter highlighter = textComponent.getHighlighter();
            if (highlighter != null) {
                final int min = Math.min(n2, n);
                final int max = Math.max(n2, n);
                try {
                    if (htmlEditorKit.linkNavigationTag != null) {
                        highlighter.changeHighlight(htmlEditorKit.linkNavigationTag, min, max);
                    }
                    else {
                        htmlEditorKit.linkNavigationTag = highlighter.addHighlight(min, max, NavigateLinkAction.focusPainter);
                    }
                }
                catch (final BadLocationException ex) {}
            }
        }
        
        private HTMLEditorKit getHTMLEditorKit(final JTextComponent textComponent) {
            if (textComponent instanceof JEditorPane) {
                final EditorKit editorKit = ((JEditorPane)textComponent).getEditorKit();
                if (editorKit instanceof HTMLEditorKit) {
                    return (HTMLEditorKit)editorKit;
                }
            }
            return null;
        }
        
        static {
            focusPainter = new FocusHighlightPainter(null);
        }
        
        static class FocusHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter
        {
            FocusHighlightPainter(final Color color) {
                super(color);
            }
            
            @Override
            public Shape paintLayer(final Graphics graphics, final int n, final int n2, final Shape shape, final JTextComponent textComponent, final View view) {
                final Color color = this.getColor();
                if (color == null) {
                    graphics.setColor(textComponent.getSelectionColor());
                }
                else {
                    graphics.setColor(color);
                }
                if (n == view.getStartOffset() && n2 == view.getEndOffset()) {
                    Rectangle bounds;
                    if (shape instanceof Rectangle) {
                        bounds = (Rectangle)shape;
                    }
                    else {
                        bounds = shape.getBounds();
                    }
                    graphics.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height);
                    return bounds;
                }
                try {
                    final Shape modelToView = view.modelToView(n, Position.Bias.Forward, n2, Position.Bias.Backward, shape);
                    final Rectangle rectangle = (Rectangle)((modelToView instanceof Rectangle) ? modelToView : modelToView.getBounds());
                    graphics.drawRect(rectangle.x, rectangle.y, rectangle.width - 1, rectangle.height);
                    return rectangle;
                }
                catch (final BadLocationException ex) {
                    return null;
                }
            }
        }
    }
    
    static class ActivateLinkAction extends TextAction
    {
        public ActivateLinkAction(final String s) {
            super(s);
        }
        
        private void activateLink(final String s, final HTMLDocument htmlDocument, final JEditorPane editorPane, final int n) {
            try {
                final URL url = new URL((URL)htmlDocument.getProperty("stream"), s);
                editorPane.fireHyperlinkUpdate(new HyperlinkEvent(editorPane, HyperlinkEvent.EventType.ACTIVATED, url, url.toExternalForm(), htmlDocument.getCharacterElement(n)));
            }
            catch (final MalformedURLException ex) {}
        }
        
        private void doObjectAction(final JEditorPane editorPane, final Element element) {
            final View view = this.getView(editorPane, element);
            if (view != null && view instanceof ObjectView) {
                final Component component = ((ObjectView)view).getComponent();
                if (component != null && component instanceof Accessible) {
                    final AccessibleContext accessibleContext = component.getAccessibleContext();
                    if (accessibleContext != null) {
                        final AccessibleAction accessibleAction = accessibleContext.getAccessibleAction();
                        if (accessibleAction != null) {
                            accessibleAction.doAccessibleAction(0);
                        }
                    }
                }
            }
        }
        
        private View getRootView(final JEditorPane editorPane) {
            return editorPane.getUI().getRootView(editorPane);
        }
        
        private View getView(final JEditorPane editorPane, final Element element) {
            final Object lock = this.lock(editorPane);
            try {
                final View rootView = this.getRootView(editorPane);
                final int startOffset = element.getStartOffset();
                if (rootView != null) {
                    return this.getView(rootView, element, startOffset);
                }
                return null;
            }
            finally {
                this.unlock(lock);
            }
        }
        
        private View getView(final View view, final Element element, final int n) {
            if (view.getElement() == element) {
                return view;
            }
            final int viewIndex = view.getViewIndex(n, Position.Bias.Forward);
            if (viewIndex != -1 && viewIndex < view.getViewCount()) {
                return this.getView(view.getView(viewIndex), element, n);
            }
            return null;
        }
        
        private Object lock(final JEditorPane editorPane) {
            final Document document = editorPane.getDocument();
            if (document instanceof AbstractDocument) {
                ((AbstractDocument)document).readLock();
                return document;
            }
            return null;
        }
        
        private void unlock(final Object o) {
            if (o != null) {
                ((AbstractDocument)o).readUnlock();
            }
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            if (textComponent.isEditable() || !(textComponent instanceof JEditorPane)) {
                return;
            }
            final JEditorPane editorPane = (JEditorPane)textComponent;
            final Document document = editorPane.getDocument();
            if (document == null || !(document instanceof HTMLDocument)) {
                return;
            }
            final HTMLDocument htmlDocument = (HTMLDocument)document;
            final ElementIterator elementIterator = new ElementIterator(htmlDocument);
            final int caretPosition = editorPane.getCaretPosition();
            Element next;
            while ((next = elementIterator.next()) != null) {
                final String name = next.getName();
                final AttributeSet attributes = next.getAttributes();
                final Object access$200 = getAttrValue(attributes, HTML.Attribute.HREF);
                if (access$200 != null) {
                    if (caretPosition >= next.getStartOffset() && caretPosition <= next.getEndOffset()) {
                        this.activateLink((String)access$200, htmlDocument, editorPane, caretPosition);
                        return;
                    }
                    continue;
                }
                else {
                    if (name.equals(HTML.Tag.OBJECT.toString()) && getAttrValue(attributes, HTML.Attribute.CLASSID) != null && caretPosition >= next.getStartOffset() && caretPosition <= next.getEndOffset()) {
                        this.doObjectAction(editorPane, next);
                        return;
                    }
                    continue;
                }
            }
        }
    }
    
    static class BeginAction extends TextAction
    {
        private boolean select;
        
        BeginAction(final String s, final boolean select) {
            super(s);
            this.select = select;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTextComponent textComponent = this.getTextComponent(actionEvent);
            final int access$400 = getBodyElementStart(textComponent);
            if (textComponent != null) {
                if (this.select) {
                    textComponent.moveCaretPosition(access$400);
                }
                else {
                    textComponent.setCaretPosition(access$400);
                }
            }
        }
    }
}
