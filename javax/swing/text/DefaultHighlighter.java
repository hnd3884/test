package javax.swing.text;

import javax.swing.SwingUtilities;
import java.awt.Color;
import javax.swing.plaf.TextUI;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Graphics;
import java.util.Vector;

public class DefaultHighlighter extends LayeredHighlighter
{
    private static final Highlighter.Highlight[] noHighlights;
    private Vector<HighlightInfo> highlights;
    private JTextComponent component;
    private boolean drawsLayeredHighlights;
    private SafeDamager safeDamager;
    public static final LayerPainter DefaultPainter;
    
    public DefaultHighlighter() {
        this.highlights = new Vector<HighlightInfo>();
        this.safeDamager = new SafeDamager();
        this.drawsLayeredHighlights = true;
    }
    
    @Override
    public void paint(final Graphics graphics) {
        for (int size = this.highlights.size(), i = 0; i < size; ++i) {
            if (!(this.highlights.elementAt(i) instanceof LayeredHighlightInfo)) {
                final Rectangle bounds = this.component.getBounds();
                final Insets insets = this.component.getInsets();
                bounds.x = insets.left;
                bounds.y = insets.top;
                final Rectangle rectangle = bounds;
                rectangle.width -= insets.left + insets.right;
                final Rectangle rectangle2 = bounds;
                rectangle2.height -= insets.top + insets.bottom;
                while (i < size) {
                    final HighlightInfo highlightInfo = this.highlights.elementAt(i);
                    if (!(highlightInfo instanceof LayeredHighlightInfo)) {
                        highlightInfo.getPainter().paint(graphics, highlightInfo.getStartOffset(), highlightInfo.getEndOffset(), bounds, this.component);
                    }
                    ++i;
                }
            }
        }
    }
    
    @Override
    public void install(final JTextComponent component) {
        this.component = component;
        this.removeAllHighlights();
    }
    
    @Override
    public void deinstall(final JTextComponent textComponent) {
        this.component = null;
    }
    
    @Override
    public Object addHighlight(final int n, final int n2, final Highlighter.HighlightPainter painter) throws BadLocationException {
        if (n < 0) {
            throw new BadLocationException("Invalid start offset", n);
        }
        if (n2 < n) {
            throw new BadLocationException("Invalid end offset", n2);
        }
        final Document document = this.component.getDocument();
        final HighlightInfo highlightInfo = (this.getDrawsLayeredHighlights() && painter instanceof LayerPainter) ? new LayeredHighlightInfo() : new HighlightInfo();
        highlightInfo.painter = painter;
        highlightInfo.p0 = document.createPosition(n);
        highlightInfo.p1 = document.createPosition(n2);
        this.highlights.addElement(highlightInfo);
        this.safeDamageRange(n, n2);
        return highlightInfo;
    }
    
    @Override
    public void removeHighlight(final Object o) {
        if (o instanceof LayeredHighlightInfo) {
            final LayeredHighlightInfo layeredHighlightInfo = (LayeredHighlightInfo)o;
            if (layeredHighlightInfo.width > 0 && layeredHighlightInfo.height > 0) {
                this.component.repaint(layeredHighlightInfo.x, layeredHighlightInfo.y, layeredHighlightInfo.width, layeredHighlightInfo.height);
            }
        }
        else {
            final HighlightInfo highlightInfo = (HighlightInfo)o;
            this.safeDamageRange(highlightInfo.p0, highlightInfo.p1);
        }
        this.highlights.removeElement(o);
    }
    
    @Override
    public void removeAllHighlights() {
        final TextUI ui = this.component.getUI();
        if (this.getDrawsLayeredHighlights()) {
            final int size = this.highlights.size();
            if (size != 0) {
                int min = 0;
                int min2 = 0;
                int max = 0;
                int max2 = 0;
                int n = -1;
                int n2 = -1;
                for (int i = 0; i < size; ++i) {
                    final HighlightInfo highlightInfo = this.highlights.elementAt(i);
                    if (highlightInfo instanceof LayeredHighlightInfo) {
                        final LayeredHighlightInfo layeredHighlightInfo = (LayeredHighlightInfo)highlightInfo;
                        min = Math.min(min, layeredHighlightInfo.x);
                        min2 = Math.min(min2, layeredHighlightInfo.y);
                        max = Math.max(max, layeredHighlightInfo.x + layeredHighlightInfo.width);
                        max2 = Math.max(max2, layeredHighlightInfo.y + layeredHighlightInfo.height);
                    }
                    else if (n == -1) {
                        n = highlightInfo.p0.getOffset();
                        n2 = highlightInfo.p1.getOffset();
                    }
                    else {
                        n = Math.min(n, highlightInfo.p0.getOffset());
                        n2 = Math.max(n2, highlightInfo.p1.getOffset());
                    }
                }
                if (min != max && min2 != max2) {
                    this.component.repaint(min, min2, max - min, max2 - min2);
                }
                if (n != -1) {
                    try {
                        this.safeDamageRange(n, n2);
                    }
                    catch (final BadLocationException ex) {}
                }
                this.highlights.removeAllElements();
            }
        }
        else if (ui != null) {
            final int size2 = this.highlights.size();
            if (size2 != 0) {
                int min3 = Integer.MAX_VALUE;
                int max3 = 0;
                for (int j = 0; j < size2; ++j) {
                    final HighlightInfo highlightInfo2 = this.highlights.elementAt(j);
                    min3 = Math.min(min3, highlightInfo2.p0.getOffset());
                    max3 = Math.max(max3, highlightInfo2.p1.getOffset());
                }
                try {
                    this.safeDamageRange(min3, max3);
                }
                catch (final BadLocationException ex2) {}
                this.highlights.removeAllElements();
            }
        }
    }
    
    @Override
    public void changeHighlight(final Object o, final int n, final int n2) throws BadLocationException {
        if (n < 0) {
            throw new BadLocationException("Invalid beginning of the range", n);
        }
        if (n2 < n) {
            throw new BadLocationException("Invalid end of the range", n2);
        }
        final Document document = this.component.getDocument();
        if (o instanceof LayeredHighlightInfo) {
            final LayeredHighlightInfo layeredHighlightInfo = (LayeredHighlightInfo)o;
            if (layeredHighlightInfo.width > 0 && layeredHighlightInfo.height > 0) {
                this.component.repaint(layeredHighlightInfo.x, layeredHighlightInfo.y, layeredHighlightInfo.width, layeredHighlightInfo.height);
            }
            final LayeredHighlightInfo layeredHighlightInfo2 = layeredHighlightInfo;
            final LayeredHighlightInfo layeredHighlightInfo3 = layeredHighlightInfo;
            final int n3 = 0;
            layeredHighlightInfo3.height = n3;
            layeredHighlightInfo2.width = n3;
            layeredHighlightInfo.p0 = document.createPosition(n);
            layeredHighlightInfo.p1 = document.createPosition(n2);
            this.safeDamageRange(Math.min(n, n2), Math.max(n, n2));
        }
        else {
            final HighlightInfo highlightInfo = (HighlightInfo)o;
            final int offset = highlightInfo.p0.getOffset();
            final int offset2 = highlightInfo.p1.getOffset();
            if (n == offset) {
                this.safeDamageRange(Math.min(offset2, n2), Math.max(offset2, n2));
            }
            else if (n2 == offset2) {
                this.safeDamageRange(Math.min(n, offset), Math.max(n, offset));
            }
            else {
                this.safeDamageRange(offset, offset2);
                this.safeDamageRange(n, n2);
            }
            highlightInfo.p0 = document.createPosition(n);
            highlightInfo.p1 = document.createPosition(n2);
        }
    }
    
    @Override
    public Highlighter.Highlight[] getHighlights() {
        final int size = this.highlights.size();
        if (size == 0) {
            return DefaultHighlighter.noHighlights;
        }
        final Highlighter.Highlight[] array = new Highlighter.Highlight[size];
        this.highlights.copyInto(array);
        return array;
    }
    
    @Override
    public void paintLayeredHighlights(final Graphics graphics, final int n, final int n2, final Shape shape, final JTextComponent textComponent, final View view) {
        for (int i = this.highlights.size() - 1; i >= 0; --i) {
            final HighlightInfo highlightInfo = this.highlights.elementAt(i);
            if (highlightInfo instanceof LayeredHighlightInfo) {
                final LayeredHighlightInfo layeredHighlightInfo = (LayeredHighlightInfo)highlightInfo;
                final int startOffset = layeredHighlightInfo.getStartOffset();
                final int endOffset = layeredHighlightInfo.getEndOffset();
                if ((n < startOffset && n2 > startOffset) || (n >= startOffset && n < endOffset)) {
                    layeredHighlightInfo.paintLayeredHighlights(graphics, n, n2, shape, textComponent, view);
                }
            }
        }
    }
    
    private void safeDamageRange(final Position position, final Position position2) {
        this.safeDamager.damageRange(position, position2);
    }
    
    private void safeDamageRange(final int n, final int n2) throws BadLocationException {
        final Document document = this.component.getDocument();
        this.safeDamageRange(document.createPosition(n), document.createPosition(n2));
    }
    
    public void setDrawsLayeredHighlights(final boolean drawsLayeredHighlights) {
        this.drawsLayeredHighlights = drawsLayeredHighlights;
    }
    
    public boolean getDrawsLayeredHighlights() {
        return this.drawsLayeredHighlights;
    }
    
    static {
        noHighlights = new Highlighter.Highlight[0];
        DefaultPainter = new DefaultHighlightPainter(null);
    }
    
    public static class DefaultHighlightPainter extends LayerPainter
    {
        private Color color;
        
        public DefaultHighlightPainter(final Color color) {
            this.color = color;
        }
        
        public Color getColor() {
            return this.color;
        }
        
        @Override
        public void paint(final Graphics graphics, final int n, final int n2, final Shape shape, final JTextComponent textComponent) {
            final Rectangle bounds = shape.getBounds();
            try {
                final TextUI ui = textComponent.getUI();
                final Rectangle modelToView = ui.modelToView(textComponent, n);
                final Rectangle modelToView2 = ui.modelToView(textComponent, n2);
                final Color color = this.getColor();
                if (color == null) {
                    graphics.setColor(textComponent.getSelectionColor());
                }
                else {
                    graphics.setColor(color);
                }
                if (modelToView.y == modelToView2.y) {
                    final Rectangle union = modelToView.union(modelToView2);
                    graphics.fillRect(union.x, union.y, union.width, union.height);
                }
                else {
                    graphics.fillRect(modelToView.x, modelToView.y, bounds.x + bounds.width - modelToView.x, modelToView.height);
                    if (modelToView.y + modelToView.height != modelToView2.y) {
                        graphics.fillRect(bounds.x, modelToView.y + modelToView.height, bounds.width, modelToView2.y - (modelToView.y + modelToView.height));
                    }
                    graphics.fillRect(bounds.x, modelToView2.y, modelToView2.x - bounds.x, modelToView2.height);
                }
            }
            catch (final BadLocationException ex) {}
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
            Rectangle bounds;
            if (n == view.getStartOffset() && n2 == view.getEndOffset()) {
                if (shape instanceof Rectangle) {
                    bounds = (Rectangle)shape;
                }
                else {
                    bounds = shape.getBounds();
                }
            }
            else {
                try {
                    final Shape modelToView = view.modelToView(n, Position.Bias.Forward, n2, Position.Bias.Backward, shape);
                    bounds = (Rectangle)((modelToView instanceof Rectangle) ? modelToView : modelToView.getBounds());
                }
                catch (final BadLocationException ex) {
                    bounds = null;
                }
            }
            if (bounds != null) {
                bounds.width = Math.max(bounds.width, 1);
                graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
            return bounds;
        }
    }
    
    class HighlightInfo implements Highlighter.Highlight
    {
        Position p0;
        Position p1;
        Highlighter.HighlightPainter painter;
        
        @Override
        public int getStartOffset() {
            return this.p0.getOffset();
        }
        
        @Override
        public int getEndOffset() {
            return this.p1.getOffset();
        }
        
        @Override
        public Highlighter.HighlightPainter getPainter() {
            return this.painter;
        }
    }
    
    class LayeredHighlightInfo extends HighlightInfo
    {
        int x;
        int y;
        int width;
        int height;
        
        void union(final Shape shape) {
            if (shape == null) {
                return;
            }
            Rectangle bounds;
            if (shape instanceof Rectangle) {
                bounds = (Rectangle)shape;
            }
            else {
                bounds = shape.getBounds();
            }
            if (this.width == 0 || this.height == 0) {
                this.x = bounds.x;
                this.y = bounds.y;
                this.width = bounds.width;
                this.height = bounds.height;
            }
            else {
                this.width = Math.max(this.x + this.width, bounds.x + bounds.width);
                this.height = Math.max(this.y + this.height, bounds.y + bounds.height);
                this.x = Math.min(this.x, bounds.x);
                this.width -= this.x;
                this.y = Math.min(this.y, bounds.y);
                this.height -= this.y;
            }
        }
        
        void paintLayeredHighlights(final Graphics graphics, int max, int min, final Shape shape, final JTextComponent textComponent, final View view) {
            final int startOffset = this.getStartOffset();
            final int endOffset = this.getEndOffset();
            max = Math.max(startOffset, max);
            min = Math.min(endOffset, min);
            this.union(((LayerPainter)this.painter).paintLayer(graphics, max, min, shape, textComponent, view));
        }
    }
    
    class SafeDamager implements Runnable
    {
        private Vector<Position> p0;
        private Vector<Position> p1;
        private Document lastDoc;
        
        SafeDamager() {
            this.p0 = new Vector<Position>(10);
            this.p1 = new Vector<Position>(10);
            this.lastDoc = null;
        }
        
        @Override
        public synchronized void run() {
            if (DefaultHighlighter.this.component != null) {
                final TextUI ui = DefaultHighlighter.this.component.getUI();
                if (ui != null && this.lastDoc == DefaultHighlighter.this.component.getDocument()) {
                    for (int size = this.p0.size(), i = 0; i < size; ++i) {
                        ui.damageRange(DefaultHighlighter.this.component, this.p0.get(i).getOffset(), this.p1.get(i).getOffset());
                    }
                }
            }
            this.p0.clear();
            this.p1.clear();
            this.lastDoc = null;
        }
        
        public synchronized void damageRange(final Position position, final Position position2) {
            if (DefaultHighlighter.this.component == null) {
                this.p0.clear();
                this.lastDoc = null;
                return;
            }
            final boolean empty = this.p0.isEmpty();
            final Document document = DefaultHighlighter.this.component.getDocument();
            if (document != this.lastDoc) {
                if (!this.p0.isEmpty()) {
                    this.p0.clear();
                    this.p1.clear();
                }
                this.lastDoc = document;
            }
            this.p0.add(position);
            this.p1.add(position2);
            if (empty) {
                SwingUtilities.invokeLater(this);
            }
        }
    }
}
