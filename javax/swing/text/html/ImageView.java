package javax.swing.text.html;

import javax.swing.text.GlyphView;
import javax.swing.text.Segment;
import javax.swing.text.Document;
import javax.swing.text.AbstractDocument;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import java.awt.Toolkit;
import java.util.Dictionary;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.swing.text.Highlighter;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.JTextComponent;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.text.ViewFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.text.StyledDocument;
import java.awt.Shape;
import javax.swing.GrayFilter;
import javax.swing.UIManager;
import javax.swing.Icon;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.text.Element;
import java.awt.image.ImageObserver;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Container;
import java.awt.Image;
import javax.swing.text.AttributeSet;
import javax.swing.text.View;

public class ImageView extends View
{
    private static boolean sIsInc;
    private static int sIncRate;
    private static final String PENDING_IMAGE = "html.pendingImage";
    private static final String MISSING_IMAGE = "html.missingImage";
    private static final String IMAGE_CACHE_PROPERTY = "imageCache";
    private static final int DEFAULT_WIDTH = 38;
    private static final int DEFAULT_HEIGHT = 38;
    private static final int DEFAULT_BORDER = 2;
    private static final int LOADING_FLAG = 1;
    private static final int LINK_FLAG = 2;
    private static final int WIDTH_FLAG = 4;
    private static final int HEIGHT_FLAG = 8;
    private static final int RELOAD_FLAG = 16;
    private static final int RELOAD_IMAGE_FLAG = 32;
    private static final int SYNC_LOAD_FLAG = 64;
    private AttributeSet attr;
    private Image image;
    private Image disabledImage;
    private int width;
    private int height;
    private int state;
    private Container container;
    private Rectangle fBounds;
    private Color borderColor;
    private short borderSize;
    private short leftInset;
    private short rightInset;
    private short topInset;
    private short bottomInset;
    private ImageObserver imageObserver;
    private View altView;
    private float vAlign;
    
    public ImageView(final Element element) {
        super(element);
        this.fBounds = new Rectangle();
        this.imageObserver = new ImageHandler();
        this.state = 48;
    }
    
    public String getAltText() {
        return (String)this.getElement().getAttributes().getAttribute(HTML.Attribute.ALT);
    }
    
    public URL getImageURL() {
        final String s = (String)this.getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
        if (s == null) {
            return null;
        }
        final URL base = ((HTMLDocument)this.getDocument()).getBase();
        try {
            return new URL(base, s);
        }
        catch (final MalformedURLException ex) {
            return null;
        }
    }
    
    public Icon getNoImageIcon() {
        return (Icon)UIManager.getLookAndFeelDefaults().get("html.missingImage");
    }
    
    public Icon getLoadingImageIcon() {
        return (Icon)UIManager.getLookAndFeelDefaults().get("html.pendingImage");
    }
    
    public Image getImage() {
        this.sync();
        return this.image;
    }
    
    private Image getImage(final boolean b) {
        Image image = this.getImage();
        if (!b) {
            if (this.disabledImage == null) {
                this.disabledImage = GrayFilter.createDisabledImage(image);
            }
            image = this.disabledImage;
        }
        return image;
    }
    
    public void setLoadsSynchronously(final boolean b) {
        synchronized (this) {
            if (b) {
                this.state |= 0x40;
            }
            else {
                this.state = ((this.state | 0x40) ^ 0x40);
            }
        }
    }
    
    public boolean getLoadsSynchronously() {
        return (this.state & 0x40) != 0x0;
    }
    
    protected StyleSheet getStyleSheet() {
        return ((HTMLDocument)this.getDocument()).getStyleSheet();
    }
    
    @Override
    public AttributeSet getAttributes() {
        this.sync();
        return this.attr;
    }
    
    @Override
    public String getToolTipText(final float n, final float n2, final Shape shape) {
        return this.getAltText();
    }
    
    protected void setPropertiesFromAttributes() {
        this.attr = this.getStyleSheet().getViewAttributes(this);
        this.borderSize = (short)this.getIntAttr(HTML.Attribute.BORDER, this.isLink() ? 2 : 0);
        final short n = (short)(this.getIntAttr(HTML.Attribute.HSPACE, 0) + this.borderSize);
        this.rightInset = n;
        this.leftInset = n;
        final short n2 = (short)(this.getIntAttr(HTML.Attribute.VSPACE, 0) + this.borderSize);
        this.bottomInset = n2;
        this.topInset = n2;
        this.borderColor = ((StyledDocument)this.getDocument()).getForeground(this.getAttributes());
        final AttributeSet attributes = this.getElement().getAttributes();
        final Object attribute = attributes.getAttribute(HTML.Attribute.ALIGN);
        this.vAlign = 1.0f;
        if (attribute != null) {
            final String string = attribute.toString();
            if ("top".equals(string)) {
                this.vAlign = 0.0f;
            }
            else if ("middle".equals(string)) {
                this.vAlign = 0.5f;
            }
        }
        final AttributeSet set = (AttributeSet)attributes.getAttribute(HTML.Tag.A);
        if (set != null && set.isDefined(HTML.Attribute.HREF)) {
            synchronized (this) {
                this.state |= 0x2;
            }
        }
        else {
            synchronized (this) {
                this.state = ((this.state | 0x2) ^ 0x2);
            }
        }
    }
    
    @Override
    public void setParent(final View parent) {
        final View parent2 = this.getParent();
        super.setParent(parent);
        this.container = ((parent != null) ? this.getContainer() : null);
        if (parent2 != parent) {
            synchronized (this) {
                this.state |= 0x10;
            }
        }
    }
    
    @Override
    public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        super.changedUpdate(documentEvent, shape, viewFactory);
        synchronized (this) {
            this.state |= 0x30;
        }
        this.preferenceChanged(null, true, true);
    }
    
    @Override
    public void paint(final Graphics graphics, final Shape shape) {
        this.sync();
        final Rectangle bounds = (Rectangle)((shape instanceof Rectangle) ? shape : shape.getBounds());
        final Rectangle clipBounds = graphics.getClipBounds();
        this.fBounds.setBounds(bounds);
        this.paintHighlights(graphics, shape);
        this.paintBorder(graphics, bounds);
        if (clipBounds != null) {
            graphics.clipRect(bounds.x + this.leftInset, bounds.y + this.topInset, bounds.width - this.leftInset - this.rightInset, bounds.height - this.topInset - this.bottomInset);
        }
        final Container container = this.getContainer();
        final Image image = this.getImage(container == null || container.isEnabled());
        if (image != null) {
            if (!this.hasPixels(image)) {
                final Icon loadingImageIcon = this.getLoadingImageIcon();
                if (loadingImageIcon != null) {
                    loadingImageIcon.paintIcon(container, graphics, bounds.x + this.leftInset, bounds.y + this.topInset);
                }
            }
            else {
                graphics.drawImage(image, bounds.x + this.leftInset, bounds.y + this.topInset, this.width, this.height, this.imageObserver);
            }
        }
        else {
            final Icon noImageIcon = this.getNoImageIcon();
            if (noImageIcon != null) {
                noImageIcon.paintIcon(container, graphics, bounds.x + this.leftInset, bounds.y + this.topInset);
            }
            final View altView = this.getAltView();
            if (altView != null && ((this.state & 0x4) == 0x0 || this.width > 38)) {
                altView.paint(graphics, new Rectangle(bounds.x + this.leftInset + 38, bounds.y + this.topInset, bounds.width - this.leftInset - this.rightInset - 38, bounds.height - this.topInset - this.bottomInset));
            }
        }
        if (clipBounds != null) {
            graphics.setClip(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        }
    }
    
    private void paintHighlights(final Graphics graphics, final Shape shape) {
        if (this.container instanceof JTextComponent) {
            final JTextComponent textComponent = (JTextComponent)this.container;
            final Highlighter highlighter = textComponent.getHighlighter();
            if (highlighter instanceof LayeredHighlighter) {
                ((LayeredHighlighter)highlighter).paintLayeredHighlights(graphics, this.getStartOffset(), this.getEndOffset(), shape, textComponent, this);
            }
        }
    }
    
    private void paintBorder(final Graphics graphics, final Rectangle rectangle) {
        final Color borderColor = this.borderColor;
        if ((this.borderSize > 0 || this.image == null) && borderColor != null) {
            final int n = this.leftInset - this.borderSize;
            final int n2 = this.topInset - this.borderSize;
            graphics.setColor(borderColor);
            for (short n3 = (short)((this.image == null) ? 1 : this.borderSize), n4 = 0; n4 < n3; ++n4) {
                graphics.drawRect(rectangle.x + n + n4, rectangle.y + n2 + n4, rectangle.width - n4 - n4 - n - n - 1, rectangle.height - n4 - n4 - n2 - n2 - 1);
            }
        }
    }
    
    @Override
    public float getPreferredSpan(final int n) {
        this.sync();
        if (n == 0 && (this.state & 0x4) == 0x4) {
            this.getPreferredSpanFromAltView(n);
            return (float)(this.width + this.leftInset + this.rightInset);
        }
        if (n == 1 && (this.state & 0x8) == 0x8) {
            this.getPreferredSpanFromAltView(n);
            return (float)(this.height + this.topInset + this.bottomInset);
        }
        if (this.getImage() != null) {
            switch (n) {
                case 0: {
                    return (float)(this.width + this.leftInset + this.rightInset);
                }
                case 1: {
                    return (float)(this.height + this.topInset + this.bottomInset);
                }
                default: {
                    throw new IllegalArgumentException("Invalid axis: " + n);
                }
            }
        }
        else {
            final View altView = this.getAltView();
            float preferredSpan = 0.0f;
            if (altView != null) {
                preferredSpan = altView.getPreferredSpan(n);
            }
            switch (n) {
                case 0: {
                    return preferredSpan + (this.width + this.leftInset + this.rightInset);
                }
                case 1: {
                    return preferredSpan + (this.height + this.topInset + this.bottomInset);
                }
                default: {
                    throw new IllegalArgumentException("Invalid axis: " + n);
                }
            }
        }
    }
    
    @Override
    public float getAlignment(final int n) {
        switch (n) {
            case 1: {
                return this.vAlign;
            }
            default: {
                return super.getAlignment(n);
            }
        }
    }
    
    @Override
    public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
        final int startOffset = this.getStartOffset();
        final int endOffset = this.getEndOffset();
        if (n >= startOffset && n <= endOffset) {
            final Rectangle bounds = shape.getBounds();
            if (n == endOffset) {
                final Rectangle rectangle = bounds;
                rectangle.x += bounds.width;
            }
            bounds.width = 0;
            return bounds;
        }
        return null;
    }
    
    @Override
    public int viewToModel(final float n, final float n2, final Shape shape, final Position.Bias[] array) {
        final Rectangle rectangle = (Rectangle)shape;
        if (n < rectangle.x + rectangle.width) {
            array[0] = Position.Bias.Forward;
            return this.getStartOffset();
        }
        array[0] = Position.Bias.Backward;
        return this.getEndOffset();
    }
    
    @Override
    public void setSize(final float n, final float n2) {
        this.sync();
        if (this.getImage() == null) {
            final View altView = this.getAltView();
            if (altView != null) {
                altView.setSize(Math.max(0.0f, n - (38 + this.leftInset + this.rightInset)), Math.max(0.0f, n2 - (this.topInset + this.bottomInset)));
            }
        }
    }
    
    private boolean isLink() {
        return (this.state & 0x2) == 0x2;
    }
    
    private boolean hasPixels(final Image image) {
        return image != null && image.getHeight(this.imageObserver) > 0 && image.getWidth(this.imageObserver) > 0;
    }
    
    private float getPreferredSpanFromAltView(final int n) {
        if (this.getImage() == null) {
            final View altView = this.getAltView();
            if (altView != null) {
                return altView.getPreferredSpan(n);
            }
        }
        return 0.0f;
    }
    
    private void repaint(final long n) {
        if (this.container != null && this.fBounds != null) {
            this.container.repaint(n, this.fBounds.x, this.fBounds.y, this.fBounds.width, this.fBounds.height);
        }
    }
    
    private int getIntAttr(final HTML.Attribute attribute, final int n) {
        final AttributeSet attributes = this.getElement().getAttributes();
        if (attributes.isDefined(attribute)) {
            final String s = (String)attributes.getAttribute(attribute);
            int max;
            if (s == null) {
                max = n;
            }
            else {
                try {
                    max = Math.max(0, Integer.parseInt(s));
                }
                catch (final NumberFormatException ex) {
                    max = n;
                }
            }
            return max;
        }
        return n;
    }
    
    private void sync() {
        if ((this.state & 0x20) != 0x0) {
            this.refreshImage();
        }
        if ((this.state & 0x10) != 0x0) {
            synchronized (this) {
                this.state = ((this.state | 0x10) ^ 0x10);
            }
            this.setPropertiesFromAttributes();
        }
    }
    
    private void refreshImage() {
        synchronized (this) {
            this.state = ((this.state | 0x1 | 0x20 | 0x4 | 0x8) ^ 0x2C);
            this.image = null;
            final int n = 0;
            this.height = n;
            this.width = n;
        }
        try {
            this.loadImage();
            this.updateImageSize();
        }
        finally {
            synchronized (this) {
                this.state = ((this.state | 0x1) ^ 0x1);
            }
        }
    }
    
    private void loadImage() {
        final URL imageURL = this.getImageURL();
        Image image = null;
        if (imageURL != null) {
            final Dictionary dictionary = (Dictionary)this.getDocument().getProperty("imageCache");
            if (dictionary != null) {
                image = (Image)dictionary.get(imageURL);
            }
            else {
                image = Toolkit.getDefaultToolkit().createImage(imageURL);
                if (image != null && this.getLoadsSynchronously()) {
                    new ImageIcon().setImage(image);
                }
            }
        }
        this.image = image;
    }
    
    private void updateImageSize() {
        int n = 0;
        final Image image = this.getImage();
        if (image != null) {
            this.getElement().getAttributes();
            int n2 = this.getIntAttr(HTML.Attribute.WIDTH, -1);
            if (n2 > 0) {
                n |= 0x4;
            }
            int n3 = this.getIntAttr(HTML.Attribute.HEIGHT, -1);
            if (n3 > 0) {
                n |= 0x8;
            }
            final Image image2;
            synchronized (this) {
                image2 = this.image;
            }
            if (n2 <= 0) {
                n2 = image2.getWidth(this.imageObserver);
                if (n2 <= 0) {
                    n2 = 38;
                }
            }
            if (n3 <= 0) {
                n3 = image2.getHeight(this.imageObserver);
                if (n3 <= 0) {
                    n3 = 38;
                }
            }
            if (this.getLoadsSynchronously()) {
                final Dimension adjustWidthHeight = this.adjustWidthHeight(n2, n3);
                n2 = adjustWidthHeight.width;
                n3 = adjustWidthHeight.height;
                n |= 0xC;
            }
            if ((n & 0xC) != 0x0) {
                Toolkit.getDefaultToolkit().prepareImage(image, n2, n3, this.imageObserver);
            }
            else {
                Toolkit.getDefaultToolkit().prepareImage(image, -1, -1, this.imageObserver);
            }
            boolean b = false;
            synchronized (this) {
                if (this.image != null) {
                    if ((n & 0x4) == 0x4 || this.width == 0) {
                        this.width = n2;
                    }
                    if ((n & 0x8) == 0x8 || this.height == 0) {
                        this.height = n3;
                    }
                }
                else {
                    b = true;
                    if ((n & 0x4) == 0x4) {
                        this.width = n2;
                    }
                    if ((n & 0x8) == 0x8) {
                        this.height = n3;
                    }
                }
                this.state |= n;
                this.state = ((this.state | 0x1) ^ 0x1);
            }
            if (b) {
                this.updateAltTextView();
            }
        }
        else {
            final int n4 = 38;
            this.height = n4;
            this.width = n4;
            this.updateAltTextView();
        }
    }
    
    private void updateAltTextView() {
        final String altText = this.getAltText();
        if (altText != null) {
            final ImageLabelView altView = new ImageLabelView(this.getElement(), altText);
            synchronized (this) {
                this.altView = altView;
            }
        }
    }
    
    private View getAltView() {
        final View altView;
        synchronized (this) {
            altView = this.altView;
        }
        if (altView != null && altView.getParent() == null) {
            altView.setParent(this.getParent());
        }
        return altView;
    }
    
    private void safePreferenceChanged() {
        if (SwingUtilities.isEventDispatchThread()) {
            final Document document = this.getDocument();
            if (document instanceof AbstractDocument) {
                ((AbstractDocument)document).readLock();
            }
            this.preferenceChanged(null, true, true);
            if (document instanceof AbstractDocument) {
                ((AbstractDocument)document).readUnlock();
            }
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ImageView.this.safePreferenceChanged();
                }
            });
        }
    }
    
    private Dimension adjustWidthHeight(int width, int height) {
        final Dimension dimension = new Dimension();
        final int intAttr = this.getIntAttr(HTML.Attribute.WIDTH, -1);
        final int intAttr2 = this.getIntAttr(HTML.Attribute.HEIGHT, -1);
        if (intAttr != -1 && intAttr2 != -1) {
            width = intAttr;
            height = intAttr2;
        }
        else if (intAttr != -1 ^ intAttr2 != -1) {
            if (intAttr <= 0) {
                width *= (int)(intAttr2 / (double)height);
                height = intAttr2;
            }
            if (intAttr2 <= 0) {
                height *= (int)(intAttr / (double)width);
                width = intAttr;
            }
        }
        dimension.width = width;
        dimension.height = height;
        return dimension;
    }
    
    static {
        ImageView.sIsInc = false;
        ImageView.sIncRate = 100;
    }
    
    private class ImageHandler implements ImageObserver
    {
        @Override
        public boolean imageUpdate(final Image image, final int n, final int n2, final int n3, int width, int height) {
            if ((image != ImageView.this.image && image != ImageView.this.disabledImage) || ImageView.this.image == null || ImageView.this.getParent() == null) {
                return false;
            }
            if ((n & 0xC0) != 0x0) {
                ImageView.this.repaint(0L);
                synchronized (ImageView.this) {
                    if (ImageView.this.image == image) {
                        ImageView.this.image = null;
                        if ((ImageView.this.state & 0x4) != 0x4) {
                            ImageView.this.width = 38;
                        }
                        if ((ImageView.this.state & 0x8) != 0x8) {
                            ImageView.this.height = 38;
                        }
                    }
                    else {
                        ImageView.this.disabledImage = null;
                    }
                    if ((ImageView.this.state & 0x1) == 0x1) {
                        return false;
                    }
                }
                ImageView.this.updateAltTextView();
                ImageView.this.safePreferenceChanged();
                return false;
            }
            if (ImageView.this.image == image) {
                short n4 = 0;
                if ((n & 0x2) != 0x0 && !ImageView.this.getElement().getAttributes().isDefined(HTML.Attribute.HEIGHT)) {
                    n4 |= 0x1;
                }
                if ((n & 0x1) != 0x0 && !ImageView.this.getElement().getAttributes().isDefined(HTML.Attribute.WIDTH)) {
                    n4 |= 0x2;
                }
                if ((n & 0x2) != 0x0 && (n & 0x1) != 0x0) {
                    final Dimension access$900 = ImageView.this.adjustWidthHeight(width, height);
                    width = access$900.width;
                    height = access$900.height;
                    n4 |= 0x3;
                }
                synchronized (ImageView.this) {
                    if ((n4 & 0x1) == 0x1 && (ImageView.this.state & 0x8) == 0x0) {
                        ImageView.this.height = height;
                    }
                    if ((n4 & 0x2) == 0x2 && (ImageView.this.state & 0x4) == 0x0) {
                        ImageView.this.width = width;
                    }
                    if ((ImageView.this.state & 0x1) == 0x1) {
                        return true;
                    }
                }
                if (n4 != 0) {
                    ImageView.this.safePreferenceChanged();
                    return true;
                }
            }
            if ((n & 0x30) != 0x0) {
                ImageView.this.repaint(0L);
            }
            else if ((n & 0x8) != 0x0 && ImageView.sIsInc) {
                ImageView.this.repaint(ImageView.sIncRate);
            }
            return (n & 0x20) == 0x0;
        }
    }
    
    private class ImageLabelView extends InlineView
    {
        private Segment segment;
        private Color fg;
        
        ImageLabelView(final Element element, final String s) {
            super(element);
            this.reset(s);
        }
        
        public void reset(final String s) {
            this.segment = new Segment(s.toCharArray(), 0, s.length());
        }
        
        @Override
        public void paint(final Graphics graphics, final Shape shape) {
            final GlyphPainter glyphPainter = this.getGlyphPainter();
            if (glyphPainter != null) {
                graphics.setColor(this.getForeground());
                glyphPainter.paint(this, graphics, shape, this.getStartOffset(), this.getEndOffset());
            }
        }
        
        @Override
        public Segment getText(final int offset, final int n) {
            if (offset < 0 || n > this.segment.array.length) {
                throw new RuntimeException("ImageLabelView: Stale view");
            }
            this.segment.offset = offset;
            this.segment.count = n - offset;
            return this.segment;
        }
        
        @Override
        public int getStartOffset() {
            return 0;
        }
        
        @Override
        public int getEndOffset() {
            return this.segment.array.length;
        }
        
        @Override
        public View breakView(final int n, final int n2, final float n3, final float n4) {
            return this;
        }
        
        @Override
        public Color getForeground() {
            final View parent;
            if (this.fg == null && (parent = this.getParent()) != null) {
                final Document document = this.getDocument();
                final AttributeSet attributes = parent.getAttributes();
                if (attributes != null && document instanceof StyledDocument) {
                    this.fg = ((StyledDocument)document).getForeground(attributes);
                }
            }
            return this.fg;
        }
    }
}
