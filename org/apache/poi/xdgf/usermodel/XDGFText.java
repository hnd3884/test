package org.apache.poi.xdgf.usermodel;

import java.awt.geom.AffineTransform;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import com.microsoft.schemas.office.visio.x2012.main.impl.TextTypeImpl;
import org.apache.poi.util.Internal;
import com.microsoft.schemas.office.visio.x2012.main.TextType;

public class XDGFText
{
    TextType _text;
    XDGFShape _parent;
    
    public XDGFText(final TextType text, final XDGFShape parent) {
        this._text = text;
        this._parent = parent;
    }
    
    @Internal
    TextType getXmlObject() {
        return this._text;
    }
    
    public String getTextContent() {
        return ((TextTypeImpl)this._text).getStringValue();
    }
    
    public Rectangle2D.Double getTextBounds() {
        final double txtPinX = this._parent.getTxtPinX();
        final double txtPinY = this._parent.getTxtPinY();
        final double txtLocPinX = this._parent.getTxtLocPinX();
        final double txtLocPinY = this._parent.getTxtLocPinY();
        final double txtWidth = this._parent.getTxtWidth();
        final double txtHeight = this._parent.getTxtHeight();
        final double x = txtPinX - txtLocPinX;
        final double y = txtPinY - txtLocPinY;
        return new Rectangle2D.Double(x, y, txtWidth, txtHeight);
    }
    
    public Path2D.Double getBoundsAsPath() {
        final Rectangle2D.Double rect = this.getTextBounds();
        final double w = rect.getWidth();
        final double h = rect.getHeight();
        final Path2D.Double bounds = new Path2D.Double();
        bounds.moveTo(0.0, 0.0);
        bounds.lineTo(w, 0.0);
        bounds.lineTo(w, h);
        bounds.lineTo(0.0, h);
        bounds.lineTo(0.0, 0.0);
        return bounds;
    }
    
    public Point2D.Double getTextCenter() {
        return new Point2D.Double(this._parent.getTxtLocPinX(), this._parent.getTxtLocPinY());
    }
    
    public void draw(final Graphics2D graphics) {
        final String textContent = this.getTextContent();
        if (textContent.length() == 0) {
            return;
        }
        final Rectangle2D.Double bounds = this.getTextBounds();
        final String[] lines = textContent.trim().split("\n");
        final FontRenderContext frc = graphics.getFontRenderContext();
        final Font font = graphics.getFont();
        final AffineTransform oldTr = graphics.getTransform();
        final Boolean flipX = this._parent.getFlipX();
        final Boolean flipY = this._parent.getFlipY();
        if (flipY == null || !this._parent.getFlipY()) {
            graphics.translate(bounds.x, bounds.y);
            graphics.scale(1.0, -1.0);
            graphics.translate(0.0, -bounds.height + graphics.getFontMetrics().getMaxCharBounds(graphics).getHeight());
        }
        if (flipX != null && this._parent.getFlipX()) {
            graphics.scale(-1.0, 1.0);
            graphics.translate(-bounds.width, 0.0);
        }
        final Double txtAngle = this._parent.getTxtAngle();
        if (txtAngle != null && Math.abs(txtAngle) > 0.01) {
            graphics.rotate(txtAngle);
        }
        float nextY = 0.0f;
        for (final String line : lines) {
            if (line.length() != 0) {
                final TextLayout layout = new TextLayout(line, font, frc);
                if (layout.isLeftToRight()) {
                    layout.draw(graphics, 0.0f, nextY);
                }
                else {
                    layout.draw(graphics, (float)(bounds.width - layout.getAdvance()), nextY);
                }
                nextY += layout.getAscent() + layout.getDescent() + layout.getLeading();
            }
        }
        graphics.setTransform(oldTr);
    }
}
