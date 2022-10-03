package org.apache.poi.xdgf.extractor;

import java.util.Iterator;
import org.apache.poi.xdgf.usermodel.shape.ShapeVisitor;
import org.apache.poi.xdgf.usermodel.XDGFPage;
import org.apache.poi.xdgf.usermodel.shape.ShapeTextVisitor;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.xdgf.usermodel.XmlVisioDocument;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;

public class XDGFVisioExtractor extends POIXMLTextExtractor
{
    protected final XmlVisioDocument document;
    
    public XDGFVisioExtractor(final XmlVisioDocument document) {
        super(document);
        this.document = document;
    }
    
    public XDGFVisioExtractor(final OPCPackage openPackage) throws IOException {
        this(new XmlVisioDocument(openPackage));
    }
    
    public String getText() {
        final ShapeTextVisitor visitor = new ShapeTextVisitor();
        for (final XDGFPage page : this.document.getPages()) {
            page.getContent().visitShapes(visitor);
        }
        return visitor.getText();
    }
    
    public static void main(final String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("  XDGFVisioExtractor <filename.vsdx>");
            System.exit(1);
        }
        final POIXMLTextExtractor extractor = new XDGFVisioExtractor(POIXMLDocument.openPackage(args[0]));
        System.out.println(extractor.getText());
        extractor.close();
    }
}
