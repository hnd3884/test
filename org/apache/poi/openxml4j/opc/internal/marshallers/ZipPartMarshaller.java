package org.apache.poi.openxml4j.opc.internal.marshallers;

import org.apache.poi.util.POILogFactory;
import java.util.Iterator;
import java.net.URI;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.apache.poi.openxml4j.opc.StreamHelper;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.w3c.dom.Node;
import org.apache.poi.ooxml.util.DocumentHelper;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagePartName;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import java.io.IOException;
import org.apache.poi.util.IOUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.poi.openxml4j.opc.internal.ZipHelper;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import java.io.OutputStream;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.POILogger;
import org.apache.poi.openxml4j.opc.internal.PartMarshaller;

public final class ZipPartMarshaller implements PartMarshaller
{
    private static final POILogger logger;
    
    @Override
    public boolean marshall(final PackagePart part, final OutputStream os) throws OpenXML4JException {
        if (!(os instanceof ZipArchiveOutputStream)) {
            ZipPartMarshaller.logger.log(7, new Object[] { "Unexpected class " + os.getClass().getName() });
            throw new OpenXML4JException("ZipOutputStream expected !");
        }
        if (part.getSize() == 0L && part.getPartName().getName().equals(XSSFRelation.SHARED_STRINGS.getDefaultFileName())) {
            return true;
        }
        final ZipArchiveOutputStream zos = (ZipArchiveOutputStream)os;
        final ZipArchiveEntry partEntry = new ZipArchiveEntry(ZipHelper.getZipItemNameFromOPCName(part.getPartName().getURI().getPath()));
        try {
            zos.putArchiveEntry((ArchiveEntry)partEntry);
            try (final InputStream ins = part.getInputStream()) {
                IOUtils.copy(ins, (OutputStream)zos);
            }
            finally {
                zos.closeArchiveEntry();
            }
        }
        catch (final IOException ioe) {
            ZipPartMarshaller.logger.log(7, new Object[] { "Cannot write: " + part.getPartName() + ": in ZIP", ioe });
            return false;
        }
        if (part.hasRelationships()) {
            final PackagePartName relationshipPartName = PackagingURIHelper.getRelationshipPartName(part.getPartName());
            marshallRelationshipPart(part.getRelationships(), relationshipPartName, zos);
        }
        return true;
    }
    
    public static boolean marshallRelationshipPart(final PackageRelationshipCollection rels, final PackagePartName relPartName, final ZipArchiveOutputStream zos) {
        final Document xmlOutDoc = DocumentHelper.createDocument();
        final Element root = xmlOutDoc.createElementNS("http://schemas.openxmlformats.org/package/2006/relationships", "Relationships");
        xmlOutDoc.appendChild(root);
        final URI sourcePartURI = PackagingURIHelper.getSourcePartUriFromRelationshipPartUri(relPartName.getURI());
        for (final PackageRelationship rel : rels) {
            final Element relElem = xmlOutDoc.createElementNS("http://schemas.openxmlformats.org/package/2006/relationships", "Relationship");
            root.appendChild(relElem);
            relElem.setAttribute("Id", rel.getId());
            relElem.setAttribute("Type", rel.getRelationshipType());
            final URI uri = rel.getTargetURI();
            String targetValue;
            if (rel.getTargetMode() == TargetMode.EXTERNAL) {
                targetValue = uri.toString();
                relElem.setAttribute("TargetMode", "External");
            }
            else {
                final URI targetURI = rel.getTargetURI();
                targetValue = PackagingURIHelper.relativizeURI(sourcePartURI, targetURI, true).toString();
            }
            relElem.setAttribute("Target", targetValue);
        }
        xmlOutDoc.normalize();
        final ZipArchiveEntry ctEntry = new ZipArchiveEntry(ZipHelper.getZipURIFromOPCName(relPartName.getURI().toASCIIString()).getPath());
        try {
            zos.putArchiveEntry((ArchiveEntry)ctEntry);
            try {
                return StreamHelper.saveXmlInStream(xmlOutDoc, (OutputStream)zos);
            }
            finally {
                zos.closeArchiveEntry();
            }
        }
        catch (final IOException e) {
            ZipPartMarshaller.logger.log(7, new Object[] { "Cannot create zip entry " + relPartName, e });
            return false;
        }
    }
    
    static {
        logger = POILogFactory.getLogger((Class)ZipPartMarshaller.class);
    }
}
