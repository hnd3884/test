package org.apache.poi.xdgf.usermodel.shape;

import java.awt.Font;
import org.apache.poi.xdgf.usermodel.XDGFText;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.AffineTransform;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import java.awt.Graphics2D;

public class ShapeRenderer extends ShapeVisitor
{
    protected Graphics2D _graphics;
    
    public ShapeRenderer() {
        this._graphics = null;
    }
    
    public ShapeRenderer(final Graphics2D g) {
        this._graphics = g;
    }
    
    public void setGraphics(final Graphics2D g) {
        this._graphics = g;
    }
    
    @Override
    public void visit(final XDGFShape shape, final AffineTransform globalTransform, final int level) {
        final AffineTransform savedTr = this._graphics.getTransform();
        this._graphics.transform(globalTransform);
        this.drawPath(shape);
        this.drawText(shape);
        this._graphics.setTransform(savedTr);
    }
    
    protected Path2D drawPath(final XDGFShape shape) {
        final Path2D.Double path = shape.getPath();
        if (path != null) {
            this._graphics.setColor(shape.getLineColor());
            this._graphics.setStroke(shape.getStroke());
            this._graphics.draw(path);
        }
        return path;
    }
    
    protected void drawText(final XDGFShape shape) {
        final XDGFText text = shape.getText();
        if (text != null) {
            if (text.getTextContent().equals("Header")) {
                text.getTextBounds();
            }
            final Font oldFont = this._graphics.getFont();
            this._graphics.setFont(oldFont.deriveFont(shape.getFontSize().floatValue()));
            this._graphics.setColor(shape.getFontColor());
            text.draw(this._graphics);
            this._graphics.setFont(oldFont);
        }
    }
}
