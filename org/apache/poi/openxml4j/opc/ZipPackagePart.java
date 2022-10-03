package org.apache.poi.openxml4j.opc;

import org.apache.poi.util.NotImplemented;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.internal.marshallers.ZipPartMarshaller;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.internal.ContentType;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

public class ZipPackagePart extends PackagePart
{
    private ZipArchiveEntry zipEntry;
    
    public ZipPackagePart(final OPCPackage container, final ZipArchiveEntry zipEntry, final PackagePartName partName, final String contentType) throws InvalidFormatException {
        this(container, zipEntry, partName, contentType, true);
    }
    
    ZipPackagePart(final OPCPackage container, final ZipArchiveEntry zipEntry, final PackagePartName partName, final String contentType, final boolean loadRelationships) throws InvalidFormatException {
        super(container, partName, new ContentType(contentType), loadRelationships);
        this.zipEntry = zipEntry;
    }
    
    public ZipArchiveEntry getZipArchive() {
        return this.zipEntry;
    }
    
    @Override
    protected InputStream getInputStreamImpl() throws IOException {
        return ((ZipPackage)this._container).getZipArchive().getInputStream(this.zipEntry);
    }
    
    @Override
    protected OutputStream getOutputStreamImpl() {
        return null;
    }
    
    @Override
    public long getSize() {
        return this.zipEntry.getSize();
    }
    
    @Override
    public boolean save(final OutputStream os) throws OpenXML4JException {
        return new ZipPartMarshaller().marshall(this, os);
    }
    
    @NotImplemented
    @Override
    public boolean load(final InputStream ios) {
        throw new InvalidOperationException("Method not implemented !");
    }
    
    @NotImplemented
    @Override
    public void close() {
        throw new InvalidOperationException("Method not implemented !");
    }
    
    @NotImplemented
    @Override
    public void flush() {
        throw new InvalidOperationException("Method not implemented !");
    }
}
