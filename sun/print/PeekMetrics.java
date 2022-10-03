package sun.print;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Color;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.RenderedImage;
import java.awt.Image;
import java.awt.font.TextLayout;
import java.awt.Paint;
import java.awt.Graphics2D;

public class PeekMetrics
{
    private boolean mHasNonSolidColors;
    private boolean mHasCompositing;
    private boolean mHasText;
    private boolean mHasImages;
    
    public boolean hasNonSolidColors() {
        return this.mHasNonSolidColors;
    }
    
    public boolean hasCompositing() {
        return this.mHasCompositing;
    }
    
    public boolean hasText() {
        return this.mHasText;
    }
    
    public boolean hasImages() {
        return this.mHasImages;
    }
    
    public void fill(final Graphics2D graphics2D) {
        this.checkDrawingMode(graphics2D);
    }
    
    public void draw(final Graphics2D graphics2D) {
        this.checkDrawingMode(graphics2D);
    }
    
    public void clear(final Graphics2D graphics2D) {
        this.checkPaint(graphics2D.getBackground());
    }
    
    public void drawText(final Graphics2D graphics2D) {
        this.mHasText = true;
        this.checkDrawingMode(graphics2D);
    }
    
    public void drawText(final Graphics2D graphics2D, final TextLayout textLayout) {
        this.mHasText = true;
        this.checkDrawingMode(graphics2D);
    }
    
    public void drawImage(final Graphics2D graphics2D, final Image image) {
        this.mHasImages = true;
    }
    
    public void drawImage(final Graphics2D graphics2D, final RenderedImage renderedImage) {
        this.mHasImages = true;
    }
    
    public void drawImage(final Graphics2D graphics2D, final RenderableImage renderableImage) {
        this.mHasImages = true;
    }
    
    private void checkDrawingMode(final Graphics2D graphics2D) {
        this.checkPaint(graphics2D.getPaint());
        this.checkAlpha(graphics2D.getComposite());
    }
    
    private void checkPaint(final Paint paint) {
        if (paint instanceof Color) {
            if (((Color)paint).getAlpha() < 255) {
                this.mHasNonSolidColors = true;
            }
        }
        else {
            this.mHasNonSolidColors = true;
        }
    }
    
    private void checkAlpha(final Composite composite) {
        if (composite instanceof AlphaComposite) {
            final AlphaComposite alphaComposite = (AlphaComposite)composite;
            final float alpha = alphaComposite.getAlpha();
            final int rule = alphaComposite.getRule();
            if (alpha != 1.0 || (rule != 2 && rule != 3)) {
                this.mHasCompositing = true;
            }
        }
        else {
            this.mHasCompositing = true;
        }
    }
}
