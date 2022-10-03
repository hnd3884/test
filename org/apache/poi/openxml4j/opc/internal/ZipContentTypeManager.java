package org.apache.poi.openxml4j.opc.internal;

import org.apache.poi.util.POILogFactory;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.StreamHelper;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import java.io.OutputStream;
import org.w3c.dom.Document;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import java.io.InputStream;
import org.apache.poi.util.POILogger;

public class ZipContentTypeManager extends ContentTypeManager
{
    private static final POILogger logger;
    
    public ZipContentTypeManager(final InputStream in, final OPCPackage pkg) throws InvalidFormatException {
        super(in, pkg);
    }
    
    @Override
    public boolean saveImpl(final Document content, final OutputStream out) {
        final ZipArchiveOutputStream zos = (out instanceof ZipArchiveOutputStream) ? out : new ZipArchiveOutputStream(out);
        final ZipArchiveEntry partEntry = new ZipArchiveEntry("[Content_Types].xml");
        try {
            zos.putArchiveEntry((ArchiveEntry)partEntry);
            try {
                return StreamHelper.saveXmlInStream(content, (OutputStream)zos);
            }
            finally {
                zos.closeArchiveEntry();
            }
        }
        catch (final IOException ioe) {
            ZipContentTypeManager.logger.log(7, new Object[] { "Cannot write: [Content_Types].xml in Zip !", ioe });
            return false;
        }
    }
    
    static {
        logger = POILogFactory.getLogger((Class)ZipContentTypeManager.class);
    }
}
