package org.apache.poi.ooxml;

import org.apache.poi.util.POILogFactory;
import java.util.Iterator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.POILogger;

public abstract class POIXMLFactory
{
    private static final POILogger LOGGER;
    
    public POIXMLDocumentPart createDocumentPart(final POIXMLDocumentPart parent, final PackagePart part) {
        final PackageRelationship rel = this.getPackageRelationship(parent, part);
        final String relType = rel.getRelationshipType();
        final POIXMLRelation descriptor = this.getDescriptor(relType);
        try {
            if (descriptor != null && !"http://schemas.openxmlformats.org/officeDocument/2006/relationships/package".equals(relType)) {
                final POIXMLRelation.ParentPartConstructor parentPartConstructor = descriptor.getParentPartConstructor();
                if (parentPartConstructor != null) {
                    return parentPartConstructor.init(parent, part);
                }
                final POIXMLRelation.PackagePartConstructor packagePartConstructor = descriptor.getPackagePartConstructor();
                if (packagePartConstructor != null) {
                    return packagePartConstructor.init(part);
                }
            }
            POIXMLFactory.LOGGER.log(1, new Object[] { "using default POIXMLDocumentPart for " + rel.getRelationshipType() });
            return new POIXMLDocumentPart(parent, part);
        }
        catch (final IOException | XmlException e) {
            throw new POIXMLException(e.getMessage(), e);
        }
    }
    
    protected abstract POIXMLRelation getDescriptor(final String p0);
    
    public POIXMLDocumentPart newDocumentPart(final POIXMLRelation descriptor) {
        if (descriptor == null || descriptor.getNoArgConstructor() == null) {
            throw new POIXMLException("can't initialize POIXMLDocumentPart");
        }
        return descriptor.getNoArgConstructor().init();
    }
    
    protected PackageRelationship getPackageRelationship(final POIXMLDocumentPart parent, final PackagePart part) {
        try {
            final String partName = part.getPartName().getName();
            for (final PackageRelationship pr : parent.getPackagePart().getRelationships()) {
                final String packName = pr.getTargetURI().toASCIIString();
                if (packName.equalsIgnoreCase(partName)) {
                    return pr;
                }
            }
        }
        catch (final InvalidFormatException e) {
            throw new POIXMLException("error while determining package relations", e);
        }
        throw new POIXMLException("package part isn't a child of the parent document.");
    }
    
    static {
        LOGGER = POILogFactory.getLogger((Class)POIXMLFactory.class);
    }
}
