package javax.swing;

import java.awt.Graphics;
import java.awt.Shape;
import javax.swing.text.GlyphView;
import javax.swing.text.CompositeView;
import javax.swing.text.ParagraphView;
import javax.swing.text.BoxView;
import javax.swing.text.WrappedPlainView;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.DefaultEditorKit;
import java.util.Vector;
import java.net.MalformedURLException;
import javax.accessibility.AccessibleHyperlink;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.accessibility.AccessibleHypertext;
import javax.accessibility.AccessibleComponent;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import java.util.HashMap;
import javax.accessibility.AccessibleContext;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.io.StringWriter;
import java.io.StringReader;
import javax.swing.plaf.TextUI;
import java.awt.Dimension;
import java.util.Iterator;
import javax.swing.text.Caret;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import java.io.DataOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.BadLocationException;
import java.io.BufferedInputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.AbstractDocument;
import java.util.Enumeration;
import java.io.InputStream;
import javax.swing.text.Document;
import java.awt.Rectangle;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.io.IOException;
import java.awt.FocusTraversalPolicy;
import java.awt.Component;
import java.awt.Container;
import java.util.Map;
import java.util.Hashtable;
import javax.swing.text.EditorKit;
import java.net.URL;
import javax.swing.text.JTextComponent;

public class JEditorPane extends JTextComponent
{
    private SwingWorker<URL, Object> pageLoader;
    private EditorKit kit;
    private boolean isUserSetEditorKit;
    private Hashtable<String, Object> pageProperties;
    static final String PostDataProperty = "javax.swing.JEditorPane.postdata";
    private Hashtable<String, EditorKit> typeHandlers;
    private static final Object kitRegistryKey;
    private static final Object kitTypeRegistryKey;
    private static final Object kitLoaderRegistryKey;
    private static final String uiClassID = "EditorPaneUI";
    public static final String W3C_LENGTH_UNITS = "JEditorPane.w3cLengthUnits";
    public static final String HONOR_DISPLAY_PROPERTIES = "JEditorPane.honorDisplayProperties";
    static final Map<String, String> defaultEditorKitMap;
    
    public JEditorPane() {
        this.setFocusCycleRoot(true);
        this.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
            @Override
            public Component getComponentAfter(final Container container, final Component component) {
                if (container != JEditorPane.this || (!JEditorPane.this.isEditable() && JEditorPane.this.getComponentCount() > 0)) {
                    return super.getComponentAfter(container, component);
                }
                final Container focusCycleRootAncestor = JEditorPane.this.getFocusCycleRootAncestor();
                return (focusCycleRootAncestor != null) ? focusCycleRootAncestor.getFocusTraversalPolicy().getComponentAfter(focusCycleRootAncestor, JEditorPane.this) : null;
            }
            
            @Override
            public Component getComponentBefore(final Container container, final Component component) {
                if (container != JEditorPane.this || (!JEditorPane.this.isEditable() && JEditorPane.this.getComponentCount() > 0)) {
                    return super.getComponentBefore(container, component);
                }
                final Container focusCycleRootAncestor = JEditorPane.this.getFocusCycleRootAncestor();
                return (focusCycleRootAncestor != null) ? focusCycleRootAncestor.getFocusTraversalPolicy().getComponentBefore(focusCycleRootAncestor, JEditorPane.this) : null;
            }
            
            @Override
            public Component getDefaultComponent(final Container container) {
                return (container != JEditorPane.this || (!JEditorPane.this.isEditable() && JEditorPane.this.getComponentCount() > 0)) ? super.getDefaultComponent(container) : null;
            }
            
            @Override
            protected boolean accept(final Component component) {
                return component != JEditorPane.this && super.accept(component);
            }
        });
        LookAndFeel.installProperty(this, "focusTraversalKeysForward", JComponent.getManagingFocusForwardTraversalKeys());
        LookAndFeel.installProperty(this, "focusTraversalKeysBackward", JComponent.getManagingFocusBackwardTraversalKeys());
    }
    
    public JEditorPane(final URL page) throws IOException {
        this();
        this.setPage(page);
    }
    
    public JEditorPane(final String page) throws IOException {
        this();
        this.setPage(page);
    }
    
    public JEditorPane(final String contentType, final String text) {
        this();
        this.setContentType(contentType);
        this.setText(text);
    }
    
    public synchronized void addHyperlinkListener(final HyperlinkListener hyperlinkListener) {
        this.listenerList.add(HyperlinkListener.class, hyperlinkListener);
    }
    
    public synchronized void removeHyperlinkListener(final HyperlinkListener hyperlinkListener) {
        this.listenerList.remove(HyperlinkListener.class, hyperlinkListener);
    }
    
    public synchronized HyperlinkListener[] getHyperlinkListeners() {
        return this.listenerList.getListeners(HyperlinkListener.class);
    }
    
    public void fireHyperlinkUpdate(final HyperlinkEvent hyperlinkEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == HyperlinkListener.class) {
                ((HyperlinkListener)listenerList[i + 1]).hyperlinkUpdate(hyperlinkEvent);
            }
        }
    }
    
    public void setPage(final URL url) throws IOException {
        if (url == null) {
            throw new IOException("invalid url");
        }
        final URL page = this.getPage();
        if (!url.equals(page) && url.getRef() == null) {
            this.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
        }
        boolean b = false;
        final Object postData = this.getPostData();
        if (page == null || !page.sameFile(url) || postData != null) {
            if (this.getAsynchronousLoadPriority(this.getDocument()) >= 0) {
                if (this.pageLoader != null) {
                    this.pageLoader.cancel(true);
                }
                (this.pageLoader = new PageLoader(null, null, page, url)).execute();
                return;
            }
            final InputStream stream = this.getStream(url);
            if (this.kit != null) {
                final Document initializeModel = this.initializeModel(this.kit, url);
                if (this.getAsynchronousLoadPriority(initializeModel) >= 0) {
                    this.setDocument(initializeModel);
                    synchronized (this) {
                        (this.pageLoader = new PageLoader(initializeModel, stream, page, url)).execute();
                    }
                    return;
                }
                this.read(stream, initializeModel);
                this.setDocument(initializeModel);
                b = true;
            }
        }
        final String ref = url.getRef();
        if (ref != null) {
            if (!b) {
                this.scrollToReference(ref);
            }
            else {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JEditorPane.this.scrollToReference(ref);
                    }
                });
            }
            this.getDocument().putProperty("stream", url);
        }
        this.firePropertyChange("page", page, url);
    }
    
    private Document initializeModel(final EditorKit editorKit, final URL url) {
        final Document defaultDocument = editorKit.createDefaultDocument();
        if (this.pageProperties != null) {
            final Enumeration<String> keys = this.pageProperties.keys();
            while (keys.hasMoreElements()) {
                final String s = keys.nextElement();
                defaultDocument.putProperty(s, this.pageProperties.get(s));
            }
            this.pageProperties.clear();
        }
        if (defaultDocument.getProperty("stream") == null) {
            defaultDocument.putProperty("stream", url);
        }
        return defaultDocument;
    }
    
    private int getAsynchronousLoadPriority(final Document document) {
        return (document instanceof AbstractDocument) ? ((AbstractDocument)document).getAsynchronousLoadPriority() : -1;
    }
    
    public void read(final InputStream inputStream, final Object o) throws IOException {
        if (o instanceof HTMLDocument && this.kit instanceof HTMLEditorKit) {
            final HTMLDocument document = (HTMLDocument)o;
            this.setDocument(document);
            this.read(inputStream, document);
        }
        else {
            final String s = (String)this.getClientProperty("charset");
            super.read((s != null) ? new InputStreamReader(inputStream, s) : new InputStreamReader(inputStream), o);
        }
    }
    
    void read(InputStream inputStream, final Document document) throws IOException {
        if (!Boolean.TRUE.equals(document.getProperty("IgnoreCharsetDirective"))) {
            inputStream = new BufferedInputStream(inputStream, 10240);
            inputStream.mark(10240);
        }
        try {
            final String s = (String)this.getClientProperty("charset");
            this.kit.read((s != null) ? new InputStreamReader(inputStream, s) : new InputStreamReader(inputStream), document, 0);
        }
        catch (final BadLocationException ex) {
            throw new IOException(ex.getMessage());
        }
        catch (final ChangedCharSetException ex2) {
            final String charSetSpec = ex2.getCharSetSpec();
            if (ex2.keyEqualsCharSet()) {
                this.putClientProperty("charset", charSetSpec);
            }
            else {
                this.setCharsetFromContentTypeParameters(charSetSpec);
            }
            try {
                inputStream.reset();
            }
            catch (final IOException ex3) {
                inputStream.close();
                final URL url = (URL)document.getProperty("stream");
                if (url == null) {
                    throw ex2;
                }
                inputStream = url.openConnection().getInputStream();
            }
            try {
                document.remove(0, document.getLength());
            }
            catch (final BadLocationException ex4) {}
            document.putProperty("IgnoreCharsetDirective", true);
            this.read(inputStream, document);
        }
    }
    
    protected InputStream getStream(URL url) throws IOException {
        final URLConnection openConnection = url.openConnection();
        if (openConnection instanceof HttpURLConnection) {
            final HttpURLConnection httpURLConnection = (HttpURLConnection)openConnection;
            httpURLConnection.setInstanceFollowRedirects(false);
            final Object postData = this.getPostData();
            if (postData != null) {
                this.handlePostData(httpURLConnection, postData);
            }
            final int responseCode = httpURLConnection.getResponseCode();
            if (responseCode >= 300 && responseCode <= 399) {
                final String headerField = openConnection.getHeaderField("Location");
                if (headerField.startsWith("http", 0)) {
                    url = new URL(headerField);
                }
                else {
                    url = new URL(url, headerField);
                }
                return this.getStream(url);
            }
        }
        if (SwingUtilities.isEventDispatchThread()) {
            this.handleConnectionProperties(openConnection);
        }
        else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        JEditorPane.this.handleConnectionProperties(openConnection);
                    }
                });
            }
            catch (final InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            catch (final InvocationTargetException ex2) {
                throw new RuntimeException(ex2);
            }
        }
        return openConnection.getInputStream();
    }
    
    private void handleConnectionProperties(final URLConnection urlConnection) {
        if (this.pageProperties == null) {
            this.pageProperties = new Hashtable<String, Object>();
        }
        final String contentType = urlConnection.getContentType();
        if (contentType != null) {
            this.setContentType(contentType);
            this.pageProperties.put("content-type", contentType);
        }
        this.pageProperties.put("stream", urlConnection.getURL());
        final String contentEncoding = urlConnection.getContentEncoding();
        if (contentEncoding != null) {
            this.pageProperties.put("content-encoding", contentEncoding);
        }
    }
    
    private Object getPostData() {
        return this.getDocument().getProperty("javax.swing.JEditorPane.postdata");
    }
    
    private void handlePostData(final HttpURLConnection httpURLConnection, final Object o) throws IOException {
        httpURLConnection.setDoOutput(true);
        DataOutputStream dataOutputStream = null;
        try {
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes((String)o);
        }
        finally {
            if (dataOutputStream != null) {
                dataOutputStream.close();
            }
        }
    }
    
    public void scrollToReference(final String s) {
        final Document document = this.getDocument();
        if (document instanceof HTMLDocument) {
            final HTMLDocument.Iterator iterator = ((HTMLDocument)document).getIterator(HTML.Tag.A);
            while (iterator.isValid()) {
                final String s2 = (String)iterator.getAttributes().getAttribute(HTML.Attribute.NAME);
                if (s2 != null && s2.equals(s)) {
                    try {
                        final int startOffset = iterator.getStartOffset();
                        final Rectangle modelToView = this.modelToView(startOffset);
                        if (modelToView != null) {
                            modelToView.height = this.getVisibleRect().height;
                            this.scrollRectToVisible(modelToView);
                            this.setCaretPosition(startOffset);
                        }
                    }
                    catch (final BadLocationException ex) {
                        UIManager.getLookAndFeel().provideErrorFeedback(this);
                    }
                }
                iterator.next();
            }
        }
    }
    
    public URL getPage() {
        return (URL)this.getDocument().getProperty("stream");
    }
    
    public void setPage(final String s) throws IOException {
        if (s == null) {
            throw new IOException("invalid url");
        }
        this.setPage(new URL(s));
    }
    
    @Override
    public String getUIClassID() {
        return "EditorPaneUI";
    }
    
    protected EditorKit createDefaultEditorKit() {
        return new PlainEditorKit();
    }
    
    public EditorKit getEditorKit() {
        if (this.kit == null) {
            this.kit = this.createDefaultEditorKit();
            this.isUserSetEditorKit = false;
        }
        return this.kit;
    }
    
    public final String getContentType() {
        return (this.kit != null) ? this.kit.getContentType() : null;
    }
    
    public final void setContentType(String trim) {
        final int index = trim.indexOf(";");
        if (index > -1) {
            final String substring = trim.substring(index);
            trim = trim.substring(0, index).trim();
            if (trim.toLowerCase().startsWith("text/")) {
                this.setCharsetFromContentTypeParameters(substring);
            }
        }
        if (this.kit == null || !trim.equals(this.kit.getContentType()) || !this.isUserSetEditorKit) {
            final EditorKit editorKitForContentType = this.getEditorKitForContentType(trim);
            if (editorKitForContentType != null && editorKitForContentType != this.kit) {
                this.setEditorKit(editorKitForContentType);
                this.isUserSetEditorKit = false;
            }
        }
    }
    
    private void setCharsetFromContentTypeParameters(String substring) {
        try {
            final int index = substring.indexOf(59);
            if (index > -1 && index < substring.length() - 1) {
                substring = substring.substring(index + 1);
            }
            if (substring.length() > 0) {
                final String value = new HeaderParser(substring).findValue("charset");
                if (value != null) {
                    this.putClientProperty("charset", value);
                }
            }
        }
        catch (final IndexOutOfBoundsException ex) {}
        catch (final NullPointerException ex2) {}
        catch (final Exception ex3) {
            System.err.println("JEditorPane.getCharsetFromContentTypeParameters failed on: " + substring);
            ex3.printStackTrace();
        }
    }
    
    public void setEditorKit(final EditorKit kit) {
        final EditorKit kit2 = this.kit;
        this.isUserSetEditorKit = true;
        if (kit2 != null) {
            kit2.deinstall(this);
        }
        this.kit = kit;
        if (this.kit != null) {
            this.kit.install(this);
            this.setDocument(this.kit.createDefaultDocument());
        }
        this.firePropertyChange("editorKit", kit2, kit);
    }
    
    public EditorKit getEditorKitForContentType(final String s) {
        if (this.typeHandlers == null) {
            this.typeHandlers = new Hashtable<String, EditorKit>(3);
        }
        EditorKit editorKit = this.typeHandlers.get(s);
        if (editorKit == null) {
            editorKit = createEditorKitForContentType(s);
            if (editorKit != null) {
                this.setEditorKitForContentType(s, editorKit);
            }
        }
        if (editorKit == null) {
            editorKit = this.createDefaultEditorKit();
        }
        return editorKit;
    }
    
    public void setEditorKitForContentType(final String s, final EditorKit editorKit) {
        if (this.typeHandlers == null) {
            this.typeHandlers = new Hashtable<String, EditorKit>(3);
        }
        this.typeHandlers.put(s, editorKit);
    }
    
    @Override
    public void replaceSelection(final String s) {
        if (!this.isEditable()) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
            return;
        }
        final EditorKit editorKit = this.getEditorKit();
        if (editorKit instanceof StyledEditorKit) {
            try {
                final Document document = this.getDocument();
                final Caret caret = this.getCaret();
                final boolean saveComposedText = this.saveComposedText(caret.getDot());
                final int min = Math.min(caret.getDot(), caret.getMark());
                final int max = Math.max(caret.getDot(), caret.getMark());
                if (document instanceof AbstractDocument) {
                    ((AbstractDocument)document).replace(min, max - min, s, ((StyledEditorKit)editorKit).getInputAttributes());
                }
                else {
                    if (min != max) {
                        document.remove(min, max - min);
                    }
                    if (s != null && s.length() > 0) {
                        document.insertString(min, s, ((StyledEditorKit)editorKit).getInputAttributes());
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
        else {
            super.replaceSelection(s);
        }
    }
    
    public static EditorKit createEditorKitForContentType(final String s) {
        final Hashtable<String, EditorKit> kitRegisty = getKitRegisty();
        EditorKit editorKit = kitRegisty.get(s);
        if (editorKit == null) {
            final String s2 = getKitTypeRegistry().get(s);
            final ClassLoader classLoader = getKitLoaderRegistry().get(s);
            try {
                Class<?> clazz;
                if (classLoader != null) {
                    clazz = classLoader.loadClass(s2);
                }
                else {
                    clazz = Class.forName(s2, true, Thread.currentThread().getContextClassLoader());
                }
                editorKit = (EditorKit)clazz.newInstance();
                kitRegisty.put(s, editorKit);
            }
            catch (final Throwable t) {
                editorKit = null;
            }
        }
        if (editorKit != null) {
            return (EditorKit)editorKit.clone();
        }
        return null;
    }
    
    public static void registerEditorKitForContentType(final String s, final String s2) {
        registerEditorKitForContentType(s, s2, Thread.currentThread().getContextClassLoader());
    }
    
    public static void registerEditorKitForContentType(final String s, final String s2, final ClassLoader classLoader) {
        getKitTypeRegistry().put(s, s2);
        if (classLoader != null) {
            getKitLoaderRegistry().put(s, classLoader);
        }
        else {
            getKitLoaderRegistry().remove(s);
        }
        getKitRegisty().remove(s);
    }
    
    public static String getEditorKitClassNameForContentType(final String s) {
        return getKitTypeRegistry().get(s);
    }
    
    private static Hashtable<String, String> getKitTypeRegistry() {
        loadDefaultKitsIfNecessary();
        return (Hashtable)SwingUtilities.appContextGet(JEditorPane.kitTypeRegistryKey);
    }
    
    private static Hashtable<String, ClassLoader> getKitLoaderRegistry() {
        loadDefaultKitsIfNecessary();
        return (Hashtable)SwingUtilities.appContextGet(JEditorPane.kitLoaderRegistryKey);
    }
    
    private static Hashtable<String, EditorKit> getKitRegisty() {
        Hashtable hashtable = (Hashtable)SwingUtilities.appContextGet(JEditorPane.kitRegistryKey);
        if (hashtable == null) {
            hashtable = new Hashtable(3);
            SwingUtilities.appContextPut(JEditorPane.kitRegistryKey, hashtable);
        }
        return hashtable;
    }
    
    private static void loadDefaultKitsIfNecessary() {
        if (SwingUtilities.appContextGet(JEditorPane.kitTypeRegistryKey) == null) {
            synchronized (JEditorPane.defaultEditorKitMap) {
                if (JEditorPane.defaultEditorKitMap.size() == 0) {
                    JEditorPane.defaultEditorKitMap.put("text/plain", "javax.swing.JEditorPane$PlainEditorKit");
                    JEditorPane.defaultEditorKitMap.put("text/html", "javax.swing.text.html.HTMLEditorKit");
                    JEditorPane.defaultEditorKitMap.put("text/rtf", "javax.swing.text.rtf.RTFEditorKit");
                    JEditorPane.defaultEditorKitMap.put("application/rtf", "javax.swing.text.rtf.RTFEditorKit");
                }
            }
            SwingUtilities.appContextPut(JEditorPane.kitTypeRegistryKey, new Hashtable());
            SwingUtilities.appContextPut(JEditorPane.kitLoaderRegistryKey, new Hashtable());
            for (final String s : JEditorPane.defaultEditorKitMap.keySet()) {
                registerEditorKitForContentType(s, JEditorPane.defaultEditorKitMap.get(s));
            }
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension preferredSize = super.getPreferredSize();
        final Container unwrappedParent = SwingUtilities.getUnwrappedParent(this);
        if (unwrappedParent instanceof JViewport) {
            final JViewport viewport = (JViewport)unwrappedParent;
            final TextUI ui = this.getUI();
            int n = preferredSize.width;
            int n2 = preferredSize.height;
            if (!this.getScrollableTracksViewportWidth()) {
                final int width = viewport.getWidth();
                final Dimension minimumSize = ui.getMinimumSize(this);
                if (width != 0 && width < minimumSize.width) {
                    n = minimumSize.width;
                }
            }
            if (!this.getScrollableTracksViewportHeight()) {
                final int height = viewport.getHeight();
                final Dimension minimumSize2 = ui.getMinimumSize(this);
                if (height != 0 && height < minimumSize2.height) {
                    n2 = minimumSize2.height;
                }
            }
            if (n != preferredSize.width || n2 != preferredSize.height) {
                preferredSize = new Dimension(n, n2);
            }
        }
        return preferredSize;
    }
    
    @Override
    public void setText(final String s) {
        try {
            final Document document = this.getDocument();
            document.remove(0, document.getLength());
            if (s == null || s.equals("")) {
                return;
            }
            this.getEditorKit().read(new StringReader(s), document, 0);
        }
        catch (final IOException ex) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
        }
        catch (final BadLocationException ex2) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
        }
    }
    
    @Override
    public String getText() {
        String string;
        try {
            final StringWriter stringWriter = new StringWriter();
            this.write(stringWriter);
            string = stringWriter.toString();
        }
        catch (final IOException ex) {
            string = null;
        }
        return string;
    }
    
    @Override
    public boolean getScrollableTracksViewportWidth() {
        final Container unwrappedParent = SwingUtilities.getUnwrappedParent(this);
        if (unwrappedParent instanceof JViewport) {
            final JViewport viewport = (JViewport)unwrappedParent;
            final TextUI ui = this.getUI();
            final int width = viewport.getWidth();
            final Dimension minimumSize = ui.getMinimumSize(this);
            final Dimension maximumSize = ui.getMaximumSize(this);
            if (width >= minimumSize.width && width <= maximumSize.width) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean getScrollableTracksViewportHeight() {
        final Container unwrappedParent = SwingUtilities.getUnwrappedParent(this);
        if (unwrappedParent instanceof JViewport) {
            final JViewport viewport = (JViewport)unwrappedParent;
            final TextUI ui = this.getUI();
            final int height = viewport.getHeight();
            if (height >= ui.getMinimumSize(this).height && height <= ui.getMaximumSize(this).height) {
                return true;
            }
        }
        return false;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("EditorPaneUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",kit=" + ((this.kit != null) ? this.kit.toString() : "") + ",typeHandlers=" + ((this.typeHandlers != null) ? this.typeHandlers.toString() : "");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.getEditorKit() instanceof HTMLEditorKit) {
            if (this.accessibleContext == null || this.accessibleContext.getClass() != AccessibleJEditorPaneHTML.class) {
                this.accessibleContext = new AccessibleJEditorPaneHTML();
            }
        }
        else if (this.accessibleContext == null || this.accessibleContext.getClass() != AccessibleJEditorPane.class) {
            this.accessibleContext = new AccessibleJEditorPane();
        }
        return this.accessibleContext;
    }
    
    static {
        kitRegistryKey = new StringBuffer("JEditorPane.kitRegistry");
        kitTypeRegistryKey = new StringBuffer("JEditorPane.kitTypeRegistry");
        kitLoaderRegistryKey = new StringBuffer("JEditorPane.kitLoaderRegistry");
        defaultEditorKitMap = new HashMap<String, String>(0);
    }
    
    class PageLoader extends SwingWorker<URL, Object>
    {
        InputStream in;
        URL old;
        URL page;
        Document doc;
        
        PageLoader(final Document doc, final InputStream in, final URL old, final URL page) {
            this.in = in;
            this.old = old;
            this.page = page;
            this.doc = doc;
        }
        
        @Override
        protected URL doInBackground() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: istore_1       
            //     2: aload_0        
            //     3: getfield        javax/swing/JEditorPane$PageLoader.in:Ljava/io/InputStream;
            //     6: ifnonnull       80
            //     9: aload_0        
            //    10: aload_0        
            //    11: getfield        javax/swing/JEditorPane$PageLoader.this$0:Ljavax/swing/JEditorPane;
            //    14: aload_0        
            //    15: getfield        javax/swing/JEditorPane$PageLoader.page:Ljava/net/URL;
            //    18: invokevirtual   javax/swing/JEditorPane.getStream:(Ljava/net/URL;)Ljava/io/InputStream;
            //    21: putfield        javax/swing/JEditorPane$PageLoader.in:Ljava/io/InputStream;
            //    24: aload_0        
            //    25: getfield        javax/swing/JEditorPane$PageLoader.this$0:Ljavax/swing/JEditorPane;
            //    28: invokestatic    javax/swing/JEditorPane.access$000:(Ljavax/swing/JEditorPane;)Ljavax/swing/text/EditorKit;
            //    31: ifnonnull       80
            //    34: invokestatic    javax/swing/UIManager.getLookAndFeel:()Ljavax/swing/LookAndFeel;
            //    37: aload_0        
            //    38: getfield        javax/swing/JEditorPane$PageLoader.this$0:Ljavax/swing/JEditorPane;
            //    41: invokevirtual   javax/swing/LookAndFeel.provideErrorFeedback:(Ljava/awt/Component;)V
            //    44: aload_0        
            //    45: getfield        javax/swing/JEditorPane$PageLoader.old:Ljava/net/URL;
            //    48: astore_2       
            //    49: iload_1        
            //    50: ifeq            64
            //    53: new             Ljavax/swing/JEditorPane$PageLoader$3;
            //    56: dup            
            //    57: aload_0        
            //    58: invokespecial   javax/swing/JEditorPane$PageLoader$3.<init>:(Ljavax/swing/JEditorPane$PageLoader;)V
            //    61: invokestatic    javax/swing/SwingUtilities.invokeLater:(Ljava/lang/Runnable;)V
            //    64: iload_1        
            //    65: ifeq            75
            //    68: aload_0        
            //    69: getfield        javax/swing/JEditorPane$PageLoader.page:Ljava/net/URL;
            //    72: goto            79
            //    75: aload_0        
            //    76: getfield        javax/swing/JEditorPane$PageLoader.old:Ljava/net/URL;
            //    79: areturn        
            //    80: aload_0        
            //    81: getfield        javax/swing/JEditorPane$PageLoader.doc:Ljavax/swing/text/Document;
            //    84: ifnonnull       195
            //    87: new             Ljavax/swing/JEditorPane$PageLoader$1;
            //    90: dup            
            //    91: aload_0        
            //    92: invokespecial   javax/swing/JEditorPane$PageLoader$1.<init>:(Ljavax/swing/JEditorPane$PageLoader;)V
            //    95: invokestatic    javax/swing/SwingUtilities.invokeAndWait:(Ljava/lang/Runnable;)V
            //    98: goto            195
            //   101: astore_2       
            //   102: invokestatic    javax/swing/UIManager.getLookAndFeel:()Ljavax/swing/LookAndFeel;
            //   105: aload_0        
            //   106: getfield        javax/swing/JEditorPane$PageLoader.this$0:Ljavax/swing/JEditorPane;
            //   109: invokevirtual   javax/swing/LookAndFeel.provideErrorFeedback:(Ljava/awt/Component;)V
            //   112: aload_0        
            //   113: getfield        javax/swing/JEditorPane$PageLoader.old:Ljava/net/URL;
            //   116: astore_3       
            //   117: iload_1        
            //   118: ifeq            132
            //   121: new             Ljavax/swing/JEditorPane$PageLoader$3;
            //   124: dup            
            //   125: aload_0        
            //   126: invokespecial   javax/swing/JEditorPane$PageLoader$3.<init>:(Ljavax/swing/JEditorPane$PageLoader;)V
            //   129: invokestatic    javax/swing/SwingUtilities.invokeLater:(Ljava/lang/Runnable;)V
            //   132: iload_1        
            //   133: ifeq            143
            //   136: aload_0        
            //   137: getfield        javax/swing/JEditorPane$PageLoader.page:Ljava/net/URL;
            //   140: goto            147
            //   143: aload_0        
            //   144: getfield        javax/swing/JEditorPane$PageLoader.old:Ljava/net/URL;
            //   147: areturn        
            //   148: astore_2       
            //   149: invokestatic    javax/swing/UIManager.getLookAndFeel:()Ljavax/swing/LookAndFeel;
            //   152: aload_0        
            //   153: getfield        javax/swing/JEditorPane$PageLoader.this$0:Ljavax/swing/JEditorPane;
            //   156: invokevirtual   javax/swing/LookAndFeel.provideErrorFeedback:(Ljava/awt/Component;)V
            //   159: aload_0        
            //   160: getfield        javax/swing/JEditorPane$PageLoader.old:Ljava/net/URL;
            //   163: astore_3       
            //   164: iload_1        
            //   165: ifeq            179
            //   168: new             Ljavax/swing/JEditorPane$PageLoader$3;
            //   171: dup            
            //   172: aload_0        
            //   173: invokespecial   javax/swing/JEditorPane$PageLoader$3.<init>:(Ljavax/swing/JEditorPane$PageLoader;)V
            //   176: invokestatic    javax/swing/SwingUtilities.invokeLater:(Ljava/lang/Runnable;)V
            //   179: iload_1        
            //   180: ifeq            190
            //   183: aload_0        
            //   184: getfield        javax/swing/JEditorPane$PageLoader.page:Ljava/net/URL;
            //   187: goto            194
            //   190: aload_0        
            //   191: getfield        javax/swing/JEditorPane$PageLoader.old:Ljava/net/URL;
            //   194: areturn        
            //   195: aload_0        
            //   196: getfield        javax/swing/JEditorPane$PageLoader.this$0:Ljavax/swing/JEditorPane;
            //   199: aload_0        
            //   200: getfield        javax/swing/JEditorPane$PageLoader.in:Ljava/io/InputStream;
            //   203: aload_0        
            //   204: getfield        javax/swing/JEditorPane$PageLoader.doc:Ljavax/swing/text/Document;
            //   207: invokevirtual   javax/swing/JEditorPane.read:(Ljava/io/InputStream;Ljavax/swing/text/Document;)V
            //   210: aload_0        
            //   211: getfield        javax/swing/JEditorPane$PageLoader.doc:Ljavax/swing/text/Document;
            //   214: ldc             "stream"
            //   216: invokeinterface javax/swing/text/Document.getProperty:(Ljava/lang/Object;)Ljava/lang/Object;
            //   221: checkcast       Ljava/net/URL;
            //   224: astore_2       
            //   225: aload_2        
            //   226: invokevirtual   java/net/URL.getRef:()Ljava/lang/String;
            //   229: astore_3       
            //   230: aload_3        
            //   231: ifnull          249
            //   234: new             Ljavax/swing/JEditorPane$PageLoader$2;
            //   237: dup            
            //   238: aload_0        
            //   239: invokespecial   javax/swing/JEditorPane$PageLoader$2.<init>:(Ljavax/swing/JEditorPane$PageLoader;)V
            //   242: astore          4
            //   244: aload           4
            //   246: invokestatic    javax/swing/SwingUtilities.invokeLater:(Ljava/lang/Runnable;)V
            //   249: iconst_1       
            //   250: istore_1       
            //   251: iload_1        
            //   252: ifeq            266
            //   255: new             Ljavax/swing/JEditorPane$PageLoader$3;
            //   258: dup            
            //   259: aload_0        
            //   260: invokespecial   javax/swing/JEditorPane$PageLoader$3.<init>:(Ljavax/swing/JEditorPane$PageLoader;)V
            //   263: invokestatic    javax/swing/SwingUtilities.invokeLater:(Ljava/lang/Runnable;)V
            //   266: iload_1        
            //   267: ifeq            277
            //   270: aload_0        
            //   271: getfield        javax/swing/JEditorPane$PageLoader.page:Ljava/net/URL;
            //   274: goto            281
            //   277: aload_0        
            //   278: getfield        javax/swing/JEditorPane$PageLoader.old:Ljava/net/URL;
            //   281: areturn        
            //   282: astore_2       
            //   283: invokestatic    javax/swing/UIManager.getLookAndFeel:()Ljavax/swing/LookAndFeel;
            //   286: aload_0        
            //   287: getfield        javax/swing/JEditorPane$PageLoader.this$0:Ljavax/swing/JEditorPane;
            //   290: invokevirtual   javax/swing/LookAndFeel.provideErrorFeedback:(Ljava/awt/Component;)V
            //   293: iload_1        
            //   294: ifeq            308
            //   297: new             Ljavax/swing/JEditorPane$PageLoader$3;
            //   300: dup            
            //   301: aload_0        
            //   302: invokespecial   javax/swing/JEditorPane$PageLoader$3.<init>:(Ljavax/swing/JEditorPane$PageLoader;)V
            //   305: invokestatic    javax/swing/SwingUtilities.invokeLater:(Ljava/lang/Runnable;)V
            //   308: iload_1        
            //   309: ifeq            319
            //   312: aload_0        
            //   313: getfield        javax/swing/JEditorPane$PageLoader.page:Ljava/net/URL;
            //   316: goto            323
            //   319: aload_0        
            //   320: getfield        javax/swing/JEditorPane$PageLoader.old:Ljava/net/URL;
            //   323: areturn        
            //   324: astore          5
            //   326: iload_1        
            //   327: ifeq            341
            //   330: new             Ljavax/swing/JEditorPane$PageLoader$3;
            //   333: dup            
            //   334: aload_0        
            //   335: invokespecial   javax/swing/JEditorPane$PageLoader$3.<init>:(Ljavax/swing/JEditorPane$PageLoader;)V
            //   338: invokestatic    javax/swing/SwingUtilities.invokeLater:(Ljava/lang/Runnable;)V
            //   341: iload_1        
            //   342: ifeq            352
            //   345: aload_0        
            //   346: getfield        javax/swing/JEditorPane$PageLoader.page:Ljava/net/URL;
            //   349: goto            356
            //   352: aload_0        
            //   353: getfield        javax/swing/JEditorPane$PageLoader.old:Ljava/net/URL;
            //   356: areturn        
            //    StackMapTable: 00 19 FD 00 40 01 07 00 31 0A 43 07 00 31 FA 00 00 54 07 00 32 FD 00 1E 07 00 32 07 00 31 0A 43 07 00 31 FF 00 00 00 02 07 00 33 01 00 01 07 00 34 FD 00 1E 07 00 34 07 00 31 0A 43 07 00 31 F9 00 00 FD 00 35 07 00 31 07 00 35 F9 00 10 0A 43 07 00 31 40 07 00 36 19 0A 43 07 00 31 40 07 00 37 FF 00 10 00 06 07 00 33 01 00 00 00 07 00 37 00 00 0A 43 07 00 31
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                                         
            //  -----  -----  -----  -----  ---------------------------------------------
            //  87     98     101    148    Ljava/lang/reflect/InvocationTargetException;
            //  87     98     148    195    Ljava/lang/InterruptedException;
            //  2      49     282    324    Ljava/io/IOException;
            //  80     117    282    324    Ljava/io/IOException;
            //  148    164    282    324    Ljava/io/IOException;
            //  195    251    282    324    Ljava/io/IOException;
            //  2      49     324    357    Any
            //  80     117    324    357    Any
            //  148    164    324    357    Any
            //  195    251    324    357    Any
            //  282    293    324    357    Any
            //  324    326    324    357    Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Inconsistent stack size at #0281 (coming from #0274).
            //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2239)
            //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
    }
    
    protected class AccessibleJEditorPane extends AccessibleJTextComponent
    {
        @Override
        public String getAccessibleDescription() {
            String s = this.accessibleDescription;
            if (s == null) {
                s = (String)JEditorPane.this.getClientProperty("AccessibleDescription");
            }
            if (s == null) {
                s = JEditorPane.this.getContentType();
            }
            return s;
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            accessibleStateSet.add(AccessibleState.MULTI_LINE);
            return accessibleStateSet;
        }
    }
    
    protected class AccessibleJEditorPaneHTML extends AccessibleJEditorPane
    {
        private AccessibleContext accessibleContext;
        
        @Override
        public AccessibleText getAccessibleText() {
            return new JEditorPaneAccessibleHypertextSupport();
        }
        
        protected AccessibleJEditorPaneHTML() {
            this.accessibleContext = ((HTMLEditorKit)JEditorPane.this.getEditorKit()).getAccessibleContext();
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            if (this.accessibleContext != null) {
                return this.accessibleContext.getAccessibleChildrenCount();
            }
            return 0;
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            if (this.accessibleContext != null) {
                return this.accessibleContext.getAccessibleChild(n);
            }
            return null;
        }
        
        @Override
        public Accessible getAccessibleAt(final Point point) {
            if (this.accessibleContext != null && point != null) {
                try {
                    final AccessibleComponent accessibleComponent = this.accessibleContext.getAccessibleComponent();
                    if (accessibleComponent != null) {
                        return accessibleComponent.getAccessibleAt(point);
                    }
                    return null;
                }
                catch (final IllegalComponentStateException ex) {
                    return null;
                }
            }
            return null;
        }
    }
    
    protected class JEditorPaneAccessibleHypertextSupport extends AccessibleJEditorPane implements AccessibleHypertext
    {
        LinkVector hyperlinks;
        boolean linksValid;
        
        private void buildLinkTable() {
            this.hyperlinks.removeAllElements();
            final Document document = JEditorPane.this.getDocument();
            if (document != null) {
                Element next;
                while ((next = new ElementIterator(document).next()) != null) {
                    if (next.isLeaf()) {
                        final AttributeSet set = (AttributeSet)next.getAttributes().getAttribute(HTML.Tag.A);
                        if (((set != null) ? ((String)set.getAttribute(HTML.Attribute.HREF)) : null) == null) {
                            continue;
                        }
                        this.hyperlinks.addElement(new HTMLLink(next));
                    }
                }
            }
            this.linksValid = true;
        }
        
        public JEditorPaneAccessibleHypertextSupport() {
            this.linksValid = false;
            this.hyperlinks = new LinkVector();
            final Document document = JEditorPane.this.getDocument();
            if (document != null) {
                document.addDocumentListener(new DocumentListener() {
                    @Override
                    public void changedUpdate(final DocumentEvent documentEvent) {
                        JEditorPaneAccessibleHypertextSupport.this.linksValid = false;
                    }
                    
                    @Override
                    public void insertUpdate(final DocumentEvent documentEvent) {
                        JEditorPaneAccessibleHypertextSupport.this.linksValid = false;
                    }
                    
                    @Override
                    public void removeUpdate(final DocumentEvent documentEvent) {
                        JEditorPaneAccessibleHypertextSupport.this.linksValid = false;
                    }
                });
            }
        }
        
        @Override
        public int getLinkCount() {
            if (!this.linksValid) {
                this.buildLinkTable();
            }
            return this.hyperlinks.size();
        }
        
        @Override
        public int getLinkIndex(final int n) {
            if (!this.linksValid) {
                this.buildLinkTable();
            }
            Element element = null;
            final Document document = JEditorPane.this.getDocument();
            if (document != null) {
                for (element = document.getDefaultRootElement(); !element.isLeaf(); element = element.getElement(element.getElementIndex(n))) {}
            }
            return this.hyperlinks.baseElementIndex(element);
        }
        
        @Override
        public AccessibleHyperlink getLink(final int n) {
            if (!this.linksValid) {
                this.buildLinkTable();
            }
            if (n >= 0 && n < this.hyperlinks.size()) {
                return ((Vector<AccessibleHyperlink>)this.hyperlinks).elementAt(n);
            }
            return null;
        }
        
        public String getLinkText(final int n) {
            if (!this.linksValid) {
                this.buildLinkTable();
            }
            final Element element = ((Vector<Element>)this.hyperlinks).elementAt(n);
            if (element != null) {
                final Document document = JEditorPane.this.getDocument();
                if (document != null) {
                    try {
                        return document.getText(element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
                    }
                    catch (final BadLocationException ex) {
                        return null;
                    }
                }
            }
            return null;
        }
        
        public class HTMLLink extends AccessibleHyperlink
        {
            Element element;
            
            public HTMLLink(final Element element) {
                this.element = element;
            }
            
            @Override
            public boolean isValid() {
                return JEditorPaneAccessibleHypertextSupport.this.linksValid;
            }
            
            @Override
            public int getAccessibleActionCount() {
                return 1;
            }
            
            @Override
            public boolean doAccessibleAction(final int n) {
                if (n == 0 && this.isValid()) {
                    final URL url = (URL)this.getAccessibleActionObject(n);
                    if (url != null) {
                        JEditorPane.this.fireHyperlinkUpdate(new HyperlinkEvent(JEditorPane.this, HyperlinkEvent.EventType.ACTIVATED, url));
                        return true;
                    }
                }
                return false;
            }
            
            @Override
            public String getAccessibleActionDescription(final int n) {
                if (n == 0 && this.isValid()) {
                    final Document document = JEditorPane.this.getDocument();
                    if (document != null) {
                        try {
                            return document.getText(this.getStartIndex(), this.getEndIndex() - this.getStartIndex());
                        }
                        catch (final BadLocationException ex) {
                            return null;
                        }
                    }
                }
                return null;
            }
            
            @Override
            public Object getAccessibleActionObject(final int n) {
                if (n == 0 && this.isValid()) {
                    final AttributeSet set = (AttributeSet)this.element.getAttributes().getAttribute(HTML.Tag.A);
                    final String s = (set != null) ? ((String)set.getAttribute(HTML.Attribute.HREF)) : null;
                    if (s != null) {
                        Object o;
                        try {
                            o = new URL(JEditorPane.this.getPage(), s);
                        }
                        catch (final MalformedURLException ex) {
                            o = null;
                        }
                        return o;
                    }
                }
                return null;
            }
            
            @Override
            public Object getAccessibleActionAnchor(final int n) {
                return this.getAccessibleActionDescription(n);
            }
            
            @Override
            public int getStartIndex() {
                return this.element.getStartOffset();
            }
            
            @Override
            public int getEndIndex() {
                return this.element.getEndOffset();
            }
        }
        
        private class LinkVector extends Vector<HTMLLink>
        {
            public int baseElementIndex(final Element element) {
                for (int i = 0; i < this.elementCount; ++i) {
                    if (this.elementAt(i).element == element) {
                        return i;
                    }
                }
                return -1;
            }
        }
    }
    
    static class PlainEditorKit extends DefaultEditorKit implements ViewFactory
    {
        @Override
        public ViewFactory getViewFactory() {
            return this;
        }
        
        @Override
        public View create(final Element element) {
            final Object property = element.getDocument().getProperty("i18n");
            if (property != null && property.equals(Boolean.TRUE)) {
                return this.createI18N(element);
            }
            return new WrappedPlainView(element);
        }
        
        View createI18N(final Element element) {
            final String name = element.getName();
            if (name != null) {
                if (name.equals("content")) {
                    return new PlainParagraph(element);
                }
                if (name.equals("paragraph")) {
                    return new BoxView(element, 1);
                }
            }
            return null;
        }
        
        static class PlainParagraph extends ParagraphView
        {
            PlainParagraph(final Element element) {
                super(element);
                (this.layoutPool = new LogicalView(element)).setParent(this);
            }
            
            @Override
            protected void setPropertiesFromAttributes() {
                final Container container = this.getContainer();
                if (container != null && !container.getComponentOrientation().isLeftToRight()) {
                    this.setJustification(2);
                }
                else {
                    this.setJustification(0);
                }
            }
            
            @Override
            public int getFlowSpan(final int n) {
                final Container container = this.getContainer();
                if (container instanceof JTextArea && !((JTextArea)container).getLineWrap()) {
                    return Integer.MAX_VALUE;
                }
                return super.getFlowSpan(n);
            }
            
            @Override
            protected SizeRequirements calculateMinorAxisRequirements(final int n, final SizeRequirements sizeRequirements) {
                final SizeRequirements calculateMinorAxisRequirements = super.calculateMinorAxisRequirements(n, sizeRequirements);
                final Container container = this.getContainer();
                if (container instanceof JTextArea && !((JTextArea)container).getLineWrap()) {
                    calculateMinorAxisRequirements.minimum = calculateMinorAxisRequirements.preferred;
                }
                return calculateMinorAxisRequirements;
            }
            
            static class LogicalView extends CompositeView
            {
                LogicalView(final Element element) {
                    super(element);
                }
                
                @Override
                protected int getViewIndexAtPosition(final int n) {
                    final Element element = this.getElement();
                    if (element.getElementCount() > 0) {
                        return element.getElementIndex(n);
                    }
                    return 0;
                }
                
                @Override
                protected boolean updateChildren(final DocumentEvent.ElementChange elementChange, final DocumentEvent documentEvent, final ViewFactory viewFactory) {
                    return false;
                }
                
                @Override
                protected void loadChildren(final ViewFactory viewFactory) {
                    final Element element = this.getElement();
                    if (element.getElementCount() > 0) {
                        super.loadChildren(viewFactory);
                    }
                    else {
                        this.append(new GlyphView(element));
                    }
                }
                
                @Override
                public float getPreferredSpan(final int n) {
                    if (this.getViewCount() != 1) {
                        throw new Error("One child view is assumed.");
                    }
                    return this.getView(0).getPreferredSpan(n);
                }
                
                @Override
                protected void forwardUpdateToView(final View view, final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
                    view.setParent(this);
                    super.forwardUpdateToView(view, documentEvent, shape, viewFactory);
                }
                
                @Override
                public void paint(final Graphics graphics, final Shape shape) {
                }
                
                @Override
                protected boolean isBefore(final int n, final int n2, final Rectangle rectangle) {
                    return false;
                }
                
                @Override
                protected boolean isAfter(final int n, final int n2, final Rectangle rectangle) {
                    return false;
                }
                
                @Override
                protected View getViewAtPoint(final int n, final int n2, final Rectangle rectangle) {
                    return null;
                }
                
                @Override
                protected void childAllocation(final int n, final Rectangle rectangle) {
                }
            }
        }
    }
    
    static class HeaderParser
    {
        String raw;
        String[][] tab;
        
        public HeaderParser(final String raw) {
            this.raw = raw;
            this.tab = new String[10][2];
            this.parse();
        }
        
        private void parse() {
            if (this.raw != null) {
                this.raw = this.raw.trim();
                final char[] charArray = this.raw.toCharArray();
                int n = 0;
                int i = 0;
                int n2 = 0;
                int n3 = 1;
                int n4 = 0;
                final int length = charArray.length;
                while (i < length) {
                    final char c = charArray[i];
                    if (c == '=') {
                        this.tab[n2][0] = new String(charArray, n, i - n).toLowerCase();
                        n3 = 0;
                        n = ++i;
                    }
                    else if (c == '\"') {
                        if (n4 != 0) {
                            this.tab[n2++][1] = new String(charArray, n, i - n);
                            n4 = 0;
                            while (++i < length && (charArray[i] == ' ' || charArray[i] == ',')) {}
                            n3 = 1;
                            n = i;
                        }
                        else {
                            n4 = 1;
                            n = ++i;
                        }
                    }
                    else if (c == ' ' || c == ',') {
                        if (n4 != 0) {
                            ++i;
                        }
                        else {
                            if (n3 != 0) {
                                this.tab[n2++][0] = new String(charArray, n, i - n).toLowerCase();
                            }
                            else {
                                this.tab[n2++][1] = new String(charArray, n, i - n);
                            }
                            while (i < length && (charArray[i] == ' ' || charArray[i] == ',')) {
                                ++i;
                            }
                            n3 = 1;
                            n = i;
                        }
                    }
                    else {
                        ++i;
                    }
                }
                if (--i > n) {
                    if (n3 == 0) {
                        if (charArray[i] == '\"') {
                            this.tab[n2++][1] = new String(charArray, n, i - n);
                        }
                        else {
                            this.tab[n2++][1] = new String(charArray, n, i - n + 1);
                        }
                    }
                    else {
                        this.tab[n2][0] = new String(charArray, n, i - n + 1).toLowerCase();
                    }
                }
                else if (i == n) {
                    if (n3 == 0) {
                        if (charArray[i] == '\"') {
                            this.tab[n2++][1] = String.valueOf(charArray[i - 1]);
                        }
                        else {
                            this.tab[n2++][1] = String.valueOf(charArray[i]);
                        }
                    }
                    else {
                        this.tab[n2][0] = String.valueOf(charArray[i]).toLowerCase();
                    }
                }
            }
        }
        
        public String findKey(final int n) {
            if (n < 0 || n > 10) {
                return null;
            }
            return this.tab[n][0];
        }
        
        public String findValue(final int n) {
            if (n < 0 || n > 10) {
                return null;
            }
            return this.tab[n][1];
        }
        
        public String findValue(final String s) {
            return this.findValue(s, null);
        }
        
        public String findValue(String lowerCase, final String s) {
            if (lowerCase == null) {
                return s;
            }
            lowerCase = lowerCase.toLowerCase();
            for (int i = 0; i < 10; ++i) {
                if (this.tab[i][0] == null) {
                    return s;
                }
                if (lowerCase.equals(this.tab[i][0])) {
                    return this.tab[i][1];
                }
            }
            return s;
        }
        
        public int findInt(final String s, final int n) {
            try {
                return Integer.parseInt(this.findValue(s, String.valueOf(n)));
            }
            catch (final Throwable t) {
                return n;
            }
        }
    }
}
