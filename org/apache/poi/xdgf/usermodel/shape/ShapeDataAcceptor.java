package org.apache.poi.xdgf.usermodel.shape;

import org.apache.poi.xdgf.usermodel.XDGFShape;

public class ShapeDataAcceptor implements ShapeVisitorAcceptor
{
    @Override
    public boolean accept(final XDGFShape shape) {
        return !shape.isDeleted() && ((shape.hasText() && shape.getTextAsString().length() != 0) || shape.isShape1D() || (!shape.hasMaster() && !shape.hasMasterShape()) || (shape.hasMaster() && !shape.hasMasterShape()) || (shape.hasMasterShape() && shape.getMasterShape().isTopmost()));
    }
}
