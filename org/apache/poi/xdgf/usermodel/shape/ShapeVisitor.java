package org.apache.poi.xdgf.usermodel.shape;

import java.awt.geom.AffineTransform;
import org.apache.poi.xdgf.usermodel.XDGFShape;

public abstract class ShapeVisitor
{
    protected ShapeVisitorAcceptor _acceptor;
    
    public ShapeVisitor() {
        this._acceptor = this.getAcceptor();
    }
    
    protected ShapeVisitorAcceptor getAcceptor() {
        return new ShapeVisitorAcceptor() {
            @Override
            public boolean accept(final XDGFShape shape) {
                return !shape.isDeleted();
            }
        };
    }
    
    public void setAcceptor(final ShapeVisitorAcceptor acceptor) {
        this._acceptor = acceptor;
    }
    
    public boolean accept(final XDGFShape shape) {
        return this._acceptor.accept(shape);
    }
    
    public abstract void visit(final XDGFShape p0, final AffineTransform p1, final int p2);
}
