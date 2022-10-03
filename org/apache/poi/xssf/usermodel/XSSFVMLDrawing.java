package org.apache.poi.xssf.usermodel;

import java.util.Iterator;
import com.microsoft.schemas.office.excel.CTClientData;
import com.microsoft.schemas.vml.CTShadow;
import java.math.BigInteger;
import com.microsoft.schemas.office.excel.STObjectType;
import com.microsoft.schemas.office.office.STInsetMode;
import com.microsoft.schemas.vml.CTPath;
import com.microsoft.schemas.office.office.CTIdMap;
import com.microsoft.schemas.office.office.STConnectType;
import com.microsoft.schemas.vml.STTrueFalse;
import com.microsoft.schemas.vml.STStrokeJoinStyle;
import com.microsoft.schemas.vml.STExt;
import org.apache.xmlbeans.XmlCursor;
import java.io.OutputStream;
import java.util.regex.Matcher;
import org.w3c.dom.Document;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import com.microsoft.schemas.vml.CTShape;
import com.microsoft.schemas.vml.CTShapetype;
import com.microsoft.schemas.office.office.CTShapeLayout;
import org.w3c.dom.Node;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.xml.sax.SAXException;
import org.apache.poi.ooxml.util.DocumentHelper;
import org.apache.poi.util.ReplacingInputStream;
import java.io.InputStream;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.List;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public final class XSSFVMLDrawing extends POIXMLDocumentPart
{
    private static final QName QNAME_SHAPE_LAYOUT;
    private static final QName QNAME_SHAPE_TYPE;
    private static final QName QNAME_SHAPE;
    private static final String COMMENT_SHAPE_TYPE_ID = "_x0000_t202";
    private static final Pattern ptrn_shapeId;
    private List<QName> _qnames;
    private List<XmlObject> _items;
    private String _shapeTypeId;
    private int _shapeId;
    
    protected XSSFVMLDrawing() {
        this._qnames = new ArrayList<QName>();
        this._items = new ArrayList<XmlObject>();
        this._shapeId = 1024;
        this.newDrawing();
    }
    
    protected XSSFVMLDrawing(final PackagePart part) throws IOException, XmlException {
        super(part);
        this._qnames = new ArrayList<QName>();
        this._items = new ArrayList<XmlObject>();
        this._shapeId = 1024;
        this.read(this.getPackagePart().getInputStream());
    }
    
    protected void read(final InputStream is) throws IOException, XmlException {
        Document doc;
        try {
            doc = DocumentHelper.readDocument((InputStream)new ReplacingInputStream(is, "<br>", "<br/>"));
        }
        catch (final SAXException e) {
            throw new XmlException(e.getMessage(), (Throwable)e);
        }
        final XmlObject root = XmlObject.Factory.parse((Node)doc, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        this._qnames = new ArrayList<QName>();
        this._items = new ArrayList<XmlObject>();
        for (final XmlObject obj : root.selectPath("$this/xml/*")) {
            final Node nd = obj.getDomNode();
            final QName qname = new QName(nd.getNamespaceURI(), nd.getLocalName());
            if (qname.equals(XSSFVMLDrawing.QNAME_SHAPE_LAYOUT)) {
                this._items.add((XmlObject)CTShapeLayout.Factory.parse(obj.xmlText(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS));
            }
            else if (qname.equals(XSSFVMLDrawing.QNAME_SHAPE_TYPE)) {
                final CTShapetype st = CTShapetype.Factory.parse(obj.xmlText(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                this._items.add((XmlObject)st);
                this._shapeTypeId = st.getId();
            }
            else if (qname.equals(XSSFVMLDrawing.QNAME_SHAPE)) {
                final CTShape shape = CTShape.Factory.parse(obj.xmlText(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                final String id = shape.getId();
                if (id != null) {
                    final Matcher m = XSSFVMLDrawing.ptrn_shapeId.matcher(id);
                    if (m.find()) {
                        this._shapeId = Math.max(this._shapeId, Integer.parseInt(m.group(1)));
                    }
                }
                this._items.add((XmlObject)shape);
            }
            else {
                Document doc2;
                try {
                    final InputSource is2 = new InputSource(new StringReader(obj.xmlText()));
                    doc2 = DocumentHelper.readDocument(is2);
                }
                catch (final SAXException e2) {
                    throw new XmlException(e2.getMessage(), (Throwable)e2);
                }
                this._items.add(XmlObject.Factory.parse((Node)doc2, POIXMLTypeLoader.DEFAULT_XML_OPTIONS));
            }
            this._qnames.add(qname);
        }
    }
    
    protected List<XmlObject> getItems() {
        return this._items;
    }
    
    protected void write(final OutputStream out) throws IOException {
        final XmlObject rootObject = XmlObject.Factory.newInstance();
        final XmlCursor rootCursor = rootObject.newCursor();
        rootCursor.toNextToken();
        rootCursor.beginElement("xml");
        for (int i = 0; i < this._items.size(); ++i) {
            final XmlCursor xc = this._items.get(i).newCursor();
            rootCursor.beginElement((QName)this._qnames.get(i));
            while (xc.toNextToken() == XmlCursor.TokenType.ATTR) {
                final Node anode = xc.getDomNode();
                rootCursor.insertAttributeWithValue(anode.getLocalName(), anode.getNamespaceURI(), anode.getNodeValue());
            }
            xc.toStartDoc();
            xc.copyXmlContents(rootCursor);
            rootCursor.toNextToken();
            xc.dispose();
        }
        rootCursor.dispose();
        rootObject.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }
    
    @Override
    protected void commit() throws IOException {
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.write(out);
        out.close();
    }
    
    private void newDrawing() {
        final CTShapeLayout layout = CTShapeLayout.Factory.newInstance();
        layout.setExt(STExt.EDIT);
        final CTIdMap idmap = layout.addNewIdmap();
        idmap.setExt(STExt.EDIT);
        idmap.setData("1");
        this._items.add((XmlObject)layout);
        this._qnames.add(XSSFVMLDrawing.QNAME_SHAPE_LAYOUT);
        final CTShapetype shapetype = CTShapetype.Factory.newInstance();
        shapetype.setId(this._shapeTypeId = "_x0000_t202");
        shapetype.setCoordsize("21600,21600");
        shapetype.setSpt(202.0f);
        shapetype.setPath2("m,l,21600r21600,l21600,xe");
        shapetype.addNewStroke().setJoinstyle(STStrokeJoinStyle.MITER);
        final CTPath path = shapetype.addNewPath();
        path.setGradientshapeok(STTrueFalse.T);
        path.setConnecttype(STConnectType.RECT);
        this._items.add((XmlObject)shapetype);
        this._qnames.add(XSSFVMLDrawing.QNAME_SHAPE_TYPE);
    }
    
    protected CTShape newCommentShape() {
        final CTShape shape = CTShape.Factory.newInstance();
        shape.setId("_x0000_s" + ++this._shapeId);
        shape.setType("#" + this._shapeTypeId);
        shape.setStyle("position:absolute; visibility:hidden");
        shape.setFillcolor("#ffffe1");
        shape.setInsetmode(STInsetMode.AUTO);
        shape.addNewFill().setColor("#ffffe1");
        final CTShadow shadow = shape.addNewShadow();
        shadow.setOn(STTrueFalse.T);
        shadow.setColor("black");
        shadow.setObscured(STTrueFalse.T);
        shape.addNewPath().setConnecttype(STConnectType.NONE);
        shape.addNewTextbox().setStyle("mso-direction-alt:auto");
        final CTClientData cldata = shape.addNewClientData();
        cldata.setObjectType(STObjectType.NOTE);
        cldata.addNewMoveWithCells();
        cldata.addNewSizeWithCells();
        cldata.addNewAnchor().setStringValue("1, 15, 0, 2, 3, 15, 3, 16");
        cldata.addNewAutoFill().setStringValue("False");
        cldata.addNewRow().setBigIntegerValue(new BigInteger("0"));
        cldata.addNewColumn().setBigIntegerValue(new BigInteger("0"));
        this._items.add((XmlObject)shape);
        this._qnames.add(XSSFVMLDrawing.QNAME_SHAPE);
        return shape;
    }
    
    public CTShape findCommentShape(final int row, final int col) {
        for (final XmlObject itm : this._items) {
            if (itm instanceof CTShape) {
                final CTShape sh = (CTShape)itm;
                if (sh.sizeOfClientDataArray() <= 0) {
                    continue;
                }
                final CTClientData cldata = sh.getClientDataArray(0);
                if (cldata.getObjectType() != STObjectType.NOTE) {
                    continue;
                }
                final int crow = cldata.getRowArray(0).intValue();
                final int ccol = cldata.getColumnArray(0).intValue();
                if (crow == row && ccol == col) {
                    return sh;
                }
                continue;
            }
        }
        return null;
    }
    
    protected boolean removeCommentShape(final int row, final int col) {
        final CTShape shape = this.findCommentShape(row, col);
        return shape != null && this._items.remove(shape);
    }
    
    static {
        QNAME_SHAPE_LAYOUT = new QName("urn:schemas-microsoft-com:office:office", "shapelayout");
        QNAME_SHAPE_TYPE = new QName("urn:schemas-microsoft-com:vml", "shapetype");
        QNAME_SHAPE = new QName("urn:schemas-microsoft-com:vml", "shape");
        ptrn_shapeId = Pattern.compile("_x0000_s(\\d+)");
    }
}
