package org.apache.poi.xdgf.usermodel.shape;

import java.awt.geom.AffineTransform;
import org.apache.poi.xdgf.usermodel.XDGFShape;

public class ShapeTextVisitor extends ShapeVisitor
{
    protected StringBuilder text;
    
    public ShapeTextVisitor() {
        this.text = new StringBuilder();
    }
    
    @Override
    protected ShapeVisitorAcceptor getAcceptor() {
        return new TextAcceptor();
    }
    
    @Override
    public void visit(final XDGFShape shape, final AffineTransform globalTransform, final int level) {
        this.text.append(shape.getText().getTextContent().trim());
        this.text.append('\n');
    }
    
    public String getText() {
        return this.text.toString();
    }
    
    public static class TextAcceptor implements ShapeVisitorAcceptor
    {
        @Override
        public boolean accept(final XDGFShape shape) {
            return shape.hasText();
        }
    }
}
