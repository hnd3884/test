package org.apache.poi.xdgf.usermodel;

import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.xdgf.exceptions.XDGFException;
import org.apache.poi.xdgf.usermodel.shape.exceptions.StopVisiting;
import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.Collections;
import org.apache.poi.xdgf.usermodel.shape.ShapeVisitor;
import org.apache.poi.xdgf.usermodel.shape.ShapeRenderer;
import java.awt.Graphics2D;
import java.util.Iterator;
import com.microsoft.schemas.office.visio.x2012.main.ConnectType;
import com.microsoft.schemas.office.visio.x2012.main.ShapeSheetType;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.util.Internal;
import java.util.HashMap;
import java.util.ArrayList;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.Map;
import java.util.List;
import com.microsoft.schemas.office.visio.x2012.main.PageContentsType;
import org.apache.poi.xdgf.xml.XDGFXMLDocumentPart;

public class XDGFBaseContents extends XDGFXMLDocumentPart
{
    protected PageContentsType _pageContents;
    protected List<XDGFShape> _toplevelShapes;
    protected Map<Long, XDGFShape> _shapes;
    protected List<XDGFConnection> _connections;
    
    public XDGFBaseContents(final PackagePart part) {
        super(part);
        this._toplevelShapes = new ArrayList<XDGFShape>();
        this._shapes = new HashMap<Long, XDGFShape>();
        this._connections = new ArrayList<XDGFConnection>();
    }
    
    @Internal
    public PageContentsType getXmlObject() {
        return this._pageContents;
    }
    
    @Override
    protected void onDocumentRead() {
        if (this._pageContents.isSetShapes()) {
            for (final ShapeSheetType shapeSheet : this._pageContents.getShapes().getShapeArray()) {
                final XDGFShape shape = new XDGFShape(shapeSheet, this, this._document);
                this._toplevelShapes.add(shape);
                this.addToShapeIndex(shape);
            }
        }
        if (this._pageContents.isSetConnects()) {
            for (final ConnectType connect : this._pageContents.getConnects().getConnectArray()) {
                final XDGFShape from = this._shapes.get(connect.getFromSheet());
                final XDGFShape to = this._shapes.get(connect.getToSheet());
                if (from == null) {
                    throw new POIXMLException(this + "; Connect; Invalid from id: " + connect.getFromSheet());
                }
                if (to == null) {
                    throw new POIXMLException(this + "; Connect; Invalid to id: " + connect.getToSheet());
                }
                this._connections.add(new XDGFConnection(connect, from, to));
            }
        }
    }
    
    protected void addToShapeIndex(final XDGFShape shape) {
        this._shapes.put(shape.getID(), shape);
        final List<XDGFShape> shapes = shape.getShapes();
        if (shapes == null) {
            return;
        }
        for (final XDGFShape subshape : shapes) {
            this.addToShapeIndex(subshape);
        }
    }
    
    public void draw(final Graphics2D graphics) {
        this.visitShapes(new ShapeRenderer(graphics));
    }
    
    public XDGFShape getShapeById(final long id) {
        return this._shapes.get(id);
    }
    
    public Map<Long, XDGFShape> getShapesMap() {
        return Collections.unmodifiableMap((Map<? extends Long, ? extends XDGFShape>)this._shapes);
    }
    
    public Collection<XDGFShape> getShapes() {
        return this._shapes.values();
    }
    
    public List<XDGFShape> getTopLevelShapes() {
        return Collections.unmodifiableList((List<? extends XDGFShape>)this._toplevelShapes);
    }
    
    public List<XDGFConnection> getConnections() {
        return Collections.unmodifiableList((List<? extends XDGFConnection>)this._connections);
    }
    
    @Override
    public String toString() {
        return this.getPackagePart().getPartName().toString();
    }
    
    public void visitShapes(final ShapeVisitor visitor) {
        try {
            for (final XDGFShape shape : this._toplevelShapes) {
                shape.visitShapes(visitor, new AffineTransform(), 0);
            }
        }
        catch (final StopVisiting stopVisiting) {}
        catch (final POIXMLException e) {
            throw XDGFException.wrap(this, e);
        }
    }
}
