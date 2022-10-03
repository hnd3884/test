package javax.swing.text.html;

import javax.swing.text.EditorKit;
import sun.swing.text.html.FrameEditorPaneTag;
import javax.swing.text.ViewFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.JViewport;
import java.awt.Dimension;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Graphics;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import java.awt.Container;
import javax.swing.text.Document;
import java.io.IOException;
import java.net.MalformedURLException;
import java.awt.Component;
import javax.swing.text.Element;
import java.net.URL;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.ComponentView;

class FrameView extends ComponentView implements HyperlinkListener
{
    JEditorPane htmlPane;
    JScrollPane scroller;
    boolean editable;
    float width;
    float height;
    URL src;
    private boolean createdComponent;
    
    public FrameView(final Element element) {
        super(element);
    }
    
    @Override
    protected Component createComponent() {
        final Element element = this.getElement();
        final String s = (String)element.getAttributes().getAttribute(HTML.Attribute.SRC);
        if (s != null && !s.equals("")) {
            try {
                this.src = new URL(((HTMLDocument)element.getDocument()).getBase(), s);
                (this.htmlPane = new FrameEditorPane()).addHyperlinkListener(this);
                final JEditorPane hostPane = this.getHostPane();
                boolean autoFormSubmission = true;
                if (hostPane != null) {
                    this.htmlPane.setEditable(hostPane.isEditable());
                    final String s2 = (String)hostPane.getClientProperty("charset");
                    if (s2 != null) {
                        this.htmlPane.putClientProperty("charset", s2);
                    }
                    final HTMLEditorKit htmlEditorKit = (HTMLEditorKit)hostPane.getEditorKit();
                    if (htmlEditorKit != null) {
                        autoFormSubmission = htmlEditorKit.isAutoFormSubmission();
                    }
                }
                this.htmlPane.setPage(this.src);
                final HTMLEditorKit htmlEditorKit2 = (HTMLEditorKit)this.htmlPane.getEditorKit();
                if (htmlEditorKit2 != null) {
                    htmlEditorKit2.setAutoFormSubmission(autoFormSubmission);
                }
                final Document document = this.htmlPane.getDocument();
                if (document instanceof HTMLDocument) {
                    ((HTMLDocument)document).setFrameDocumentState(true);
                }
                this.setMargin();
                this.createScrollPane();
                this.setBorder();
            }
            catch (final MalformedURLException ex) {
                ex.printStackTrace();
            }
            catch (final IOException ex2) {
                ex2.printStackTrace();
            }
        }
        this.createdComponent = true;
        return this.scroller;
    }
    
    JEditorPane getHostPane() {
        Container container;
        for (container = this.getContainer(); container != null && !(container instanceof JEditorPane); container = container.getParent()) {}
        return (JEditorPane)container;
    }
    
    @Override
    public void setParent(final View parent) {
        if (parent != null) {
            this.editable = ((JTextComponent)parent.getContainer()).isEditable();
        }
        super.setParent(parent);
    }
    
    @Override
    public void paint(final Graphics graphics, final Shape shape) {
        final Container container = this.getContainer();
        if (container != null && this.htmlPane != null && this.htmlPane.isEditable() != ((JTextComponent)container).isEditable()) {
            this.editable = ((JTextComponent)container).isEditable();
            this.htmlPane.setEditable(this.editable);
        }
        super.paint(graphics, shape);
    }
    
    private void setMargin() {
        final Insets margin = this.htmlPane.getMargin();
        boolean b = false;
        final AttributeSet attributes = this.getElement().getAttributes();
        final String s = (String)attributes.getAttribute(HTML.Attribute.MARGINWIDTH);
        Insets margin2;
        if (margin != null) {
            margin2 = new Insets(margin.top, margin.left, margin.right, margin.bottom);
        }
        else {
            margin2 = new Insets(0, 0, 0, 0);
        }
        if (s != null) {
            final int int1 = Integer.parseInt(s);
            if (int1 > 0) {
                margin2.left = int1;
                margin2.right = int1;
                b = true;
            }
        }
        final String s2 = (String)attributes.getAttribute(HTML.Attribute.MARGINHEIGHT);
        if (s2 != null) {
            final int int2 = Integer.parseInt(s2);
            if (int2 > 0) {
                margin2.top = int2;
                margin2.bottom = int2;
                b = true;
            }
        }
        if (b) {
            this.htmlPane.setMargin(margin2);
        }
    }
    
    private void setBorder() {
        final String s = (String)this.getElement().getAttributes().getAttribute(HTML.Attribute.FRAMEBORDER);
        if (s != null && (s.equals("no") || s.equals("0"))) {
            this.scroller.setBorder(null);
        }
    }
    
    private void createScrollPane() {
        String s = (String)this.getElement().getAttributes().getAttribute(HTML.Attribute.SCROLLING);
        if (s == null) {
            s = "auto";
        }
        if (!s.equals("no")) {
            if (s.equals("yes")) {
                this.scroller = new JScrollPane(22, 32);
            }
            else {
                this.scroller = new JScrollPane();
            }
        }
        else {
            this.scroller = new JScrollPane(21, 31);
        }
        final JViewport viewport = this.scroller.getViewport();
        viewport.add(this.htmlPane);
        viewport.setBackingStoreEnabled(true);
        this.scroller.setMinimumSize(new Dimension(5, 5));
        this.scroller.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }
    
    JEditorPane getOutermostJEditorPane() {
        View view = this.getParent();
        View view2 = null;
        while (view != null) {
            if (view instanceof FrameSetView) {
                view2 = view;
            }
            view = view.getParent();
        }
        if (view2 != null) {
            return (JEditorPane)view2.getContainer();
        }
        return null;
    }
    
    private boolean inNestedFrameSet() {
        return this.getParent().getParent() instanceof FrameSetView;
    }
    
    @Override
    public void hyperlinkUpdate(final HyperlinkEvent hyperlinkEvent) {
        final JEditorPane outermostJEditorPane = this.getOutermostJEditorPane();
        if (outermostJEditorPane == null) {
            return;
        }
        if (!(hyperlinkEvent instanceof HTMLFrameHyperlinkEvent)) {
            outermostJEditorPane.fireHyperlinkUpdate(hyperlinkEvent);
            return;
        }
        final HTMLFrameHyperlinkEvent htmlFrameHyperlinkEvent = (HTMLFrameHyperlinkEvent)hyperlinkEvent;
        if (htmlFrameHyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            final String target;
            String s = target = htmlFrameHyperlinkEvent.getTarget();
            if (s.equals("_parent") && !this.inNestedFrameSet()) {
                s = "_top";
            }
            if (hyperlinkEvent instanceof FormSubmitEvent) {
                final HTMLEditorKit htmlEditorKit = (HTMLEditorKit)outermostJEditorPane.getEditorKit();
                if (htmlEditorKit != null && htmlEditorKit.isAutoFormSubmission()) {
                    if (s.equals("_top")) {
                        try {
                            this.movePostData(outermostJEditorPane, target);
                            outermostJEditorPane.setPage(htmlFrameHyperlinkEvent.getURL());
                        }
                        catch (final IOException ex) {}
                    }
                    else {
                        ((HTMLDocument)outermostJEditorPane.getDocument()).processHTMLFrameHyperlinkEvent(htmlFrameHyperlinkEvent);
                    }
                }
                else {
                    outermostJEditorPane.fireHyperlinkUpdate(hyperlinkEvent);
                }
                return;
            }
            if (s.equals("_top")) {
                try {
                    outermostJEditorPane.setPage(htmlFrameHyperlinkEvent.getURL());
                }
                catch (final IOException ex2) {}
            }
            if (!outermostJEditorPane.isEditable()) {
                outermostJEditorPane.fireHyperlinkUpdate(new HTMLFrameHyperlinkEvent(outermostJEditorPane, htmlFrameHyperlinkEvent.getEventType(), htmlFrameHyperlinkEvent.getURL(), htmlFrameHyperlinkEvent.getDescription(), this.getElement(), htmlFrameHyperlinkEvent.getInputEvent(), s));
            }
        }
    }
    
    @Override
    public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        final Element element = this.getElement();
        final AttributeSet attributes = element.getAttributes();
        final URL src = this.src;
        final String s = (String)attributes.getAttribute(HTML.Attribute.SRC);
        final URL base = ((HTMLDocument)element.getDocument()).getBase();
        try {
            if (!this.createdComponent) {
                return;
            }
            final Object movePostData = this.movePostData(this.htmlPane, null);
            this.src = new URL(base, s);
            if (src.equals(this.src) && this.src.getRef() == null && movePostData == null) {
                return;
            }
            this.htmlPane.setPage(this.src);
            final Document document = this.htmlPane.getDocument();
            if (document instanceof HTMLDocument) {
                ((HTMLDocument)document).setFrameDocumentState(true);
            }
        }
        catch (final MalformedURLException ex) {}
        catch (final IOException ex2) {}
    }
    
    private Object movePostData(final JEditorPane editorPane, String s) {
        Object property = null;
        final JEditorPane outermostJEditorPane = this.getOutermostJEditorPane();
        if (outermostJEditorPane != null) {
            if (s == null) {
                s = (String)this.getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
            }
            if (s != null) {
                final String string = "javax.swing.JEditorPane.postdata." + s;
                final Document document = outermostJEditorPane.getDocument();
                property = document.getProperty(string);
                if (property != null) {
                    editorPane.getDocument().putProperty("javax.swing.JEditorPane.postdata", property);
                    document.putProperty(string, null);
                }
            }
        }
        return property;
    }
    
    @Override
    public float getMinimumSpan(final int n) {
        return 5.0f;
    }
    
    @Override
    public float getMaximumSpan(final int n) {
        return 2.14748365E9f;
    }
    
    class FrameEditorPane extends JEditorPane implements FrameEditorPaneTag
    {
        @Override
        public EditorKit getEditorKitForContentType(final String s) {
            EditorKit editorKitForContentType = super.getEditorKitForContentType(s);
            final JEditorPane outermostJEditorPane;
            if ((outermostJEditorPane = FrameView.this.getOutermostJEditorPane()) != null) {
                final EditorKit editorKitForContentType2 = outermostJEditorPane.getEditorKitForContentType(s);
                if (!editorKitForContentType.getClass().equals(editorKitForContentType2.getClass())) {
                    editorKitForContentType = (EditorKit)editorKitForContentType2.clone();
                    this.setEditorKitForContentType(s, editorKitForContentType);
                }
            }
            return editorKitForContentType;
        }
        
        FrameView getFrameView() {
            return FrameView.this;
        }
    }
}
