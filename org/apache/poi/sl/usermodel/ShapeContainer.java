package org.apache.poi.sl.usermodel;

import java.util.List;

public interface ShapeContainer<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> extends Iterable<S>
{
    List<S> getShapes();
    
    void addShape(final S p0);
    
    boolean removeShape(final S p0);
    
    AutoShape<S, P> createAutoShape();
    
    FreeformShape<S, P> createFreeform();
    
    TextBox<S, P> createTextBox();
    
    ConnectorShape<S, P> createConnector();
    
    GroupShape<S, P> createGroup();
    
    PictureShape<S, P> createPicture(final PictureData p0);
    
    TableShape<S, P> createTable(final int p0, final int p1);
    
    ObjectShape<?, ?> createOleShape(final PictureData p0);
}
