package javax.swing.plaf.basic;

import java.awt.Container;
import javax.swing.text.Position;
import java.awt.Graphics;
import sun.swing.SwingUtilities2;
import javax.swing.text.html.ImageView;
import javax.swing.text.Element;
import java.awt.Color;
import java.awt.Font;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;
import javax.swing.text.ParagraphView;
import javax.swing.text.html.HTML;
import javax.swing.text.StyleConstants;
import java.awt.Shape;
import java.awt.Rectangle;
import javax.swing.text.Document;
import java.io.Reader;
import java.io.StringReader;
import javax.swing.text.html.HTMLDocument;
import java.net.URL;
import javax.swing.text.View;
import javax.swing.JComponent;
import javax.swing.text.ViewFactory;

public class BasicHTML
{
    private static final String htmlDisable = "html.disable";
    public static final String propertyKey = "html";
    public static final String documentBaseKey = "html.base";
    private static BasicEditorKit basicHTMLFactory;
    private static ViewFactory basicHTMLViewFactory;
    private static final String styleChanges = "p { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }body { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }";
    
    public static View createHTMLView(final JComponent component, final String s) {
        final BasicEditorKit factory = getFactory();
        final Document defaultDocument = factory.createDefaultDocument(component.getFont(), component.getForeground());
        final Object clientProperty = component.getClientProperty("html.base");
        if (clientProperty instanceof URL) {
            ((HTMLDocument)defaultDocument).setBase((URL)clientProperty);
        }
        final StringReader stringReader = new StringReader(s);
        try {
            factory.read(stringReader, defaultDocument, 0);
        }
        catch (final Throwable t) {}
        final ViewFactory viewFactory = factory.getViewFactory();
        return new Renderer(component, viewFactory, viewFactory.create(defaultDocument.getDefaultRootElement()));
    }
    
    public static int getHTMLBaseline(final View view, final int n, final int n2) {
        if (n < 0 || n2 < 0) {
            throw new IllegalArgumentException("Width and height must be >= 0");
        }
        if (view instanceof Renderer) {
            return getBaseline(view.getView(0), n, n2);
        }
        return -1;
    }
    
    static int getBaseline(final JComponent component, final int n, final int n2, final int n3, final int n4) {
        final View view = (View)component.getClientProperty("html");
        if (view == null) {
            return n + n2;
        }
        final int htmlBaseline = getHTMLBaseline(view, n3, n4);
        if (htmlBaseline < 0) {
            return htmlBaseline;
        }
        return n + htmlBaseline;
    }
    
    static int getBaseline(final View view, final int n, final int n2) {
        if (hasParagraph(view)) {
            view.setSize((float)n, (float)n2);
            return getBaseline(view, new Rectangle(0, 0, n, n2));
        }
        return -1;
    }
    
    private static int getBaseline(final View view, Shape childAllocation) {
        if (view.getViewCount() == 0) {
            return -1;
        }
        final AttributeSet attributes = view.getElement().getAttributes();
        Object attribute = null;
        if (attributes != null) {
            attribute = attributes.getAttribute(StyleConstants.NameAttribute);
        }
        int n = 0;
        if (attribute == HTML.Tag.HTML && view.getViewCount() > 1) {
            ++n;
        }
        childAllocation = view.getChildAllocation(n, childAllocation);
        if (childAllocation == null) {
            return -1;
        }
        final View view2 = view.getView(n);
        if (view instanceof ParagraphView) {
            Rectangle bounds;
            if (childAllocation instanceof Rectangle) {
                bounds = (Rectangle)childAllocation;
            }
            else {
                bounds = childAllocation.getBounds();
            }
            return bounds.y + (int)(bounds.height * view2.getAlignment(1));
        }
        return getBaseline(view2, childAllocation);
    }
    
    private static boolean hasParagraph(final View view) {
        if (view instanceof ParagraphView) {
            return true;
        }
        if (view.getViewCount() == 0) {
            return false;
        }
        final AttributeSet attributes = view.getElement().getAttributes();
        Object attribute = null;
        if (attributes != null) {
            attribute = attributes.getAttribute(StyleConstants.NameAttribute);
        }
        int n = 0;
        if (attribute == HTML.Tag.HTML && view.getViewCount() > 1) {
            n = 1;
        }
        return hasParagraph(view.getView(n));
    }
    
    public static boolean isHTMLString(final String s) {
        return s != null && s.length() >= 6 && s.charAt(0) == '<' && s.charAt(5) == '>' && s.substring(1, 5).equalsIgnoreCase("html");
    }
    
    public static void updateRenderer(final JComponent component, final String s) {
        View htmlView = null;
        final View view = (View)component.getClientProperty("html");
        if (component.getClientProperty("html.disable") != Boolean.TRUE && isHTMLString(s)) {
            htmlView = createHTMLView(component, s);
        }
        if (htmlView != view && view != null) {
            for (int i = 0; i < view.getViewCount(); ++i) {
                view.getView(i).setParent(null);
            }
        }
        component.putClientProperty("html", htmlView);
        final String s2 = (String)component.getClientProperty("AccessibleName");
        Object trim = null;
        if (s2 != null && view != null) {
            try {
                trim = view.getDocument().getText(0, view.getDocument().getLength()).trim();
            }
            catch (final BadLocationException ex) {}
        }
        if (s2 == null || s2.equals(trim)) {
            Object trim2 = null;
            if (htmlView != null) {
                try {
                    trim2 = htmlView.getDocument().getText(0, htmlView.getDocument().getLength()).trim();
                }
                catch (final BadLocationException ex2) {}
            }
            component.putClientProperty("AccessibleName", trim2);
        }
    }
    
    static BasicEditorKit getFactory() {
        if (BasicHTML.basicHTMLFactory == null) {
            BasicHTML.basicHTMLViewFactory = new BasicHTMLViewFactory();
            BasicHTML.basicHTMLFactory = new BasicEditorKit();
        }
        return BasicHTML.basicHTMLFactory;
    }
    
    static class BasicEditorKit extends HTMLEditorKit
    {
        private static StyleSheet defaultStyles;
        
        @Override
        public StyleSheet getStyleSheet() {
            if (BasicEditorKit.defaultStyles == null) {
                BasicEditorKit.defaultStyles = new StyleSheet();
                final StringReader stringReader = new StringReader("p { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }body { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }");
                try {
                    BasicEditorKit.defaultStyles.loadRules(stringReader, null);
                }
                catch (final Throwable t) {}
                stringReader.close();
                BasicEditorKit.defaultStyles.addStyleSheet(super.getStyleSheet());
            }
            return BasicEditorKit.defaultStyles;
        }
        
        public Document createDefaultDocument(final Font font, final Color color) {
            final StyleSheet styleSheet = this.getStyleSheet();
            final StyleSheet styleSheet2 = new StyleSheet();
            styleSheet2.addStyleSheet(styleSheet);
            final BasicDocument basicDocument = new BasicDocument(styleSheet2, font, color);
            basicDocument.setAsynchronousLoadPriority(Integer.MAX_VALUE);
            basicDocument.setPreservesUnknownTags(false);
            return basicDocument;
        }
        
        @Override
        public ViewFactory getViewFactory() {
            return BasicHTML.basicHTMLViewFactory;
        }
    }
    
    static class BasicHTMLViewFactory extends HTMLEditorKit.HTMLFactory
    {
        @Override
        public View create(final Element element) {
            final View create = super.create(element);
            if (create instanceof ImageView) {
                ((ImageView)create).setLoadsSynchronously(true);
            }
            return create;
        }
    }
    
    static class BasicDocument extends HTMLDocument
    {
        BasicDocument(final StyleSheet styleSheet, final Font font, final Color color) {
            super(styleSheet);
            this.setPreservesUnknownTags(false);
            this.setFontAndColor(font, color);
        }
        
        private void setFontAndColor(final Font font, final Color color) {
            this.getStyleSheet().addRule(SwingUtilities2.displayPropertiesToCSS(font, color));
        }
    }
    
    static class Renderer extends View
    {
        private int width;
        private View view;
        private ViewFactory factory;
        private JComponent host;
        
        Renderer(final JComponent host, final ViewFactory factory, final View view) {
            super(null);
            this.host = host;
            this.factory = factory;
            (this.view = view).setParent(this);
            this.setSize(this.view.getPreferredSpan(0), this.view.getPreferredSpan(1));
        }
        
        @Override
        public AttributeSet getAttributes() {
            return null;
        }
        
        @Override
        public float getPreferredSpan(final int n) {
            if (n == 0) {
                return (float)this.width;
            }
            return this.view.getPreferredSpan(n);
        }
        
        @Override
        public float getMinimumSpan(final int n) {
            return this.view.getMinimumSpan(n);
        }
        
        @Override
        public float getMaximumSpan(final int n) {
            return 2.14748365E9f;
        }
        
        @Override
        public void preferenceChanged(final View view, final boolean b, final boolean b2) {
            this.host.revalidate();
            this.host.repaint();
        }
        
        @Override
        public float getAlignment(final int n) {
            return this.view.getAlignment(n);
        }
        
        @Override
        public void paint(final Graphics graphics, final Shape shape) {
            final Rectangle bounds = shape.getBounds();
            this.view.setSize((float)bounds.width, (float)bounds.height);
            this.view.paint(graphics, shape);
        }
        
        @Override
        public void setParent(final View view) {
            throw new Error("Can't set parent on root view");
        }
        
        @Override
        public int getViewCount() {
            return 1;
        }
        
        @Override
        public View getView(final int n) {
            return this.view;
        }
        
        @Override
        public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
            return this.view.modelToView(n, shape, bias);
        }
        
        @Override
        public Shape modelToView(final int n, final Position.Bias bias, final int n2, final Position.Bias bias2, final Shape shape) throws BadLocationException {
            return this.view.modelToView(n, bias, n2, bias2, shape);
        }
        
        @Override
        public int viewToModel(final float n, final float n2, final Shape shape, final Position.Bias[] array) {
            return this.view.viewToModel(n, n2, shape, array);
        }
        
        @Override
        public Document getDocument() {
            return this.view.getDocument();
        }
        
        @Override
        public int getStartOffset() {
            return this.view.getStartOffset();
        }
        
        @Override
        public int getEndOffset() {
            return this.view.getEndOffset();
        }
        
        @Override
        public Element getElement() {
            return this.view.getElement();
        }
        
        @Override
        public void setSize(final float n, final float n2) {
            this.width = (int)n;
            this.view.setSize(n, n2);
        }
        
        @Override
        public Container getContainer() {
            return this.host;
        }
        
        @Override
        public ViewFactory getViewFactory() {
            return this.factory;
        }
    }
}
