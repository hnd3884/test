package org.apache.poi.xslf.usermodel;

import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.OPCPackage;
import java.io.ByteArrayInputStream;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.util.Internal;

@Internal
public class XSLFMetroShape
{
    public static Shape<?, ?> parseShape(final byte[] metroBytes) throws InvalidFormatException, IOException, XmlException {
        final PackagePartName shapePN = PackagingURIHelper.createPartName("/drs/shapexml.xml");
        try (final OPCPackage pkg = OPCPackage.open(new ByteArrayInputStream(metroBytes))) {
            final PackagePart shapePart = pkg.getPart(shapePN);
            final CTGroupShape gs = CTGroupShape.Factory.parse(shapePart.getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            final XSLFGroupShape xgs = new XSLFGroupShape(gs, null);
            return (Shape<?, ?>)xgs.getShapes().get(0);
        }
    }
}
