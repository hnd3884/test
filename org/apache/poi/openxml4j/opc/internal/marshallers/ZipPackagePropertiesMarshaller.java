package org.apache.poi.openxml4j.opc.internal.marshallers;

import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.StreamHelper;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.poi.openxml4j.opc.internal.ZipHelper;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import java.io.OutputStream;
import org.apache.poi.openxml4j.opc.PackagePart;

public final class ZipPackagePropertiesMarshaller extends PackagePropertiesMarshaller
{
    @Override
    public boolean marshall(final PackagePart part, final OutputStream out) throws OpenXML4JException {
        if (!(out instanceof ZipArchiveOutputStream)) {
            throw new IllegalArgumentException("ZipOutputStream expected!");
        }
        final ZipArchiveOutputStream zos = (ZipArchiveOutputStream)out;
        final ZipArchiveEntry ctEntry = new ZipArchiveEntry(ZipHelper.getZipItemNameFromOPCName(part.getPartName().getURI().toString()));
        try {
            zos.putArchiveEntry((ArchiveEntry)ctEntry);
            try {
                super.marshall(part, out);
                return StreamHelper.saveXmlInStream(this.xmlDoc, out);
            }
            finally {
                zos.closeArchiveEntry();
            }
        }
        catch (final IOException e) {
            throw new OpenXML4JException(e.getLocalizedMessage(), e);
        }
    }
}
