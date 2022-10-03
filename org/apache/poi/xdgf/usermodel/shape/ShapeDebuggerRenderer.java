package org.apache.poi.xdgf.usermodel.shape;

import java.awt.Font;
import java.awt.geom.Path2D;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import java.awt.Graphics2D;

public class ShapeDebuggerRenderer extends ShapeRenderer
{
    ShapeVisitorAcceptor _debugAcceptor;
    
    public ShapeDebuggerRenderer() {
    }
    
    public ShapeDebuggerRenderer(final Graphics2D g) {
        super(g);
    }
    
    public void setDebugAcceptor(final ShapeVisitorAcceptor acceptor) {
        this._debugAcceptor = acceptor;
    }
    
    @Override
    protected Path2D drawPath(final XDGFShape shape) {
        final Path2D path = super.drawPath(shape);
        if (this._debugAcceptor == null || this._debugAcceptor.accept(shape)) {
            final Font f = this._graphics.getFont();
            this._graphics.scale(1.0, -1.0);
            this._graphics.setFont(f.deriveFont(0.05f));
            String shapeId = "" + shape.getID();
            float shapeOffset = -0.1f;
            if (shape.hasMasterShape()) {
                shapeId = shapeId + " MS:" + shape.getMasterShape().getID();
                shapeOffset -= 0.15f;
            }
            this._graphics.drawString(shapeId, shapeOffset, 0.0f);
            this._graphics.setFont(f);
            this._graphics.scale(1.0, -1.0);
        }
        return path;
    }
}
