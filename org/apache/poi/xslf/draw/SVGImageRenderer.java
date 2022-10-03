package org.apache.poi.xslf.draw;

import org.apache.poi.sl.usermodel.PictureData;
import java.awt.Shape;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import java.awt.geom.AffineTransform;
import org.apache.poi.sl.draw.Drawable;
import java.awt.Insets;
import java.lang.ref.WeakReference;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.w3c.dom.Document;
import java.io.InputStream;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.poi.sl.draw.ImageRenderer;

public class SVGImageRenderer implements ImageRenderer
{
    private final GVTBuilder builder;
    private final BridgeContext context;
    private final SAXSVGDocumentFactory svgFact;
    private GraphicsNode svgRoot;
    private double alpha;
    
    public SVGImageRenderer() {
        this.builder = new GVTBuilder();
        this.alpha = 1.0;
        final String parser = XMLResourceDescriptor.getXMLParserClassName();
        this.svgFact = new SAXSVGDocumentFactory(parser);
        final UserAgent agent = (UserAgent)new UserAgentAdapter();
        final DocumentLoader loader = new DocumentLoader(agent);
        (this.context = new BridgeContext(agent, loader)).setDynamic(true);
    }
    
    public void loadImage(final InputStream data, final String contentType) throws IOException {
        final Document document = this.svgFact.createDocument("", data);
        this.svgRoot = this.builder.build(this.context, document);
    }
    
    public void loadImage(final byte[] data, final String contentType) throws IOException {
        this.loadImage(new ByteArrayInputStream(data), contentType);
    }
    
    public Rectangle2D getBounds() {
        return this.svgRoot.getPrimitiveBounds();
    }
    
    public void setAlpha(final double alpha) {
        this.alpha = alpha;
    }
    
    public BufferedImage getImage() {
        return this.getImage(this.getDimension());
    }
    
    public BufferedImage getImage(final Dimension2D dim) {
        final BufferedImage bi = new BufferedImage((int)dim.getWidth(), (int)dim.getHeight(), 2);
        final Graphics2D g2d = (Graphics2D)bi.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE, new WeakReference(bi));
        final Dimension2D dimSVG = this.getDimension();
        final double scaleX = dim.getWidth() / dimSVG.getWidth();
        final double scaleY = dim.getHeight() / dimSVG.getHeight();
        g2d.scale(scaleX, scaleY);
        this.svgRoot.paint(g2d);
        g2d.dispose();
        return bi;
    }
    
    public boolean drawImage(final Graphics2D graphics, final Rectangle2D anchor) {
        return this.drawImage(graphics, anchor, null);
    }
    
    public boolean drawImage(final Graphics2D graphics, final Rectangle2D anchor, final Insets clip) {
        graphics.setRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE, graphics.getRenderingHint((RenderingHints.Key)Drawable.BUFFERED_IMAGE));
        final Dimension2D bounds = this.getDimension();
        final AffineTransform at = new AffineTransform();
        at.translate(anchor.getX(), anchor.getY());
        at.scale(anchor.getWidth() / bounds.getWidth(), anchor.getHeight() / bounds.getHeight());
        this.svgRoot.setTransform(at);
        if (clip == null) {
            this.svgRoot.setClip((ClipRable)null);
        }
        else {
            final Rectangle2D clippedRect = new Rectangle2D.Double(anchor.getX() + clip.left, anchor.getY() + clip.top, anchor.getWidth() - (clip.left + clip.right), anchor.getHeight() - (clip.top + clip.bottom));
            this.svgRoot.setClip((ClipRable)new ClipRable8Bit((Filter)null, (Shape)clippedRect));
        }
        this.svgRoot.paint(graphics);
        return true;
    }
    
    public boolean canRender(final String contentType) {
        return PictureData.PictureType.SVG.contentType.equalsIgnoreCase(contentType);
    }
    
    public Rectangle2D getNativeBounds() {
        return this.svgRoot.getPrimitiveBounds();
    }
}
