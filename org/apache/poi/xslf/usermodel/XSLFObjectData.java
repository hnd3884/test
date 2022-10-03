package org.apache.poi.xslf.usermodel;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.sl.usermodel.ObjectData;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public final class XSLFObjectData extends POIXMLDocumentPart implements ObjectData
{
    XSLFObjectData() {
    }
    
    public XSLFObjectData(final PackagePart part) {
        super(part);
    }
    
    public InputStream getInputStream() throws IOException {
        return this.getPackagePart().getInputStream();
    }
    
    public OutputStream getOutputStream() {
        final PackagePart pp = this.getPackagePart();
        pp.clear();
        return pp.getOutputStream();
    }
    
    @Override
    protected void prepareForCommit() {
    }
    
    public void setData(final byte[] data) throws IOException {
        try (final OutputStream os = this.getPackagePart().getOutputStream()) {
            os.write(data);
        }
    }
    
    public String getOLE2ClassName() {
        return null;
    }
    
    public String getFileName() {
        return null;
    }
}
