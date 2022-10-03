package org.apache.poi.openxml4j.opc.internal;

import java.io.IOException;
import org.apache.poi.util.IOUtils;
import java.io.ByteArrayOutputStream;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.internal.marshallers.ZipPartMarshaller;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;

public final class MemoryPackagePart extends PackagePart
{
    protected byte[] data;
    
    public MemoryPackagePart(final OPCPackage pack, final PackagePartName partName, final String contentType) throws InvalidFormatException {
        super(pack, partName, contentType);
    }
    
    public MemoryPackagePart(final OPCPackage pack, final PackagePartName partName, final String contentType, final boolean loadRelationships) throws InvalidFormatException {
        super(pack, partName, new ContentType(contentType), loadRelationships);
    }
    
    @Override
    protected InputStream getInputStreamImpl() {
        if (this.data == null) {
            this.data = new byte[0];
        }
        return new ByteArrayInputStream(this.data);
    }
    
    @Override
    protected OutputStream getOutputStreamImpl() {
        return new MemoryPackagePartOutputStream(this);
    }
    
    @Override
    public long getSize() {
        return (this.data == null) ? 0L : this.data.length;
    }
    
    @Override
    public void clear() {
        this.data = null;
    }
    
    @Override
    public boolean save(final OutputStream os) throws OpenXML4JException {
        return new ZipPartMarshaller().marshall(this, os);
    }
    
    @Override
    public boolean load(final InputStream ios) throws InvalidFormatException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            IOUtils.copy(ios, (OutputStream)baos);
        }
        catch (final IOException e) {
            throw new InvalidFormatException(e.getMessage());
        }
        this.data = baos.toByteArray();
        return true;
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void flush() {
    }
}
