package org.bouncycastle.cms;

import java.io.OutputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import java.io.File;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class CMSProcessableFile implements CMSTypedData, CMSReadable
{
    private static final int DEFAULT_BUF_SIZE = 32768;
    private final ASN1ObjectIdentifier type;
    private final File file;
    private final byte[] buf;
    
    public CMSProcessableFile(final File file) {
        this(file, 32768);
    }
    
    public CMSProcessableFile(final File file, final int n) {
        this(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), file, n);
    }
    
    public CMSProcessableFile(final ASN1ObjectIdentifier type, final File file, final int n) {
        this.type = type;
        this.file = file;
        this.buf = new byte[n];
    }
    
    public InputStream getInputStream() throws IOException, CMSException {
        return new BufferedInputStream(new FileInputStream(this.file), 32768);
    }
    
    public void write(final OutputStream outputStream) throws IOException, CMSException {
        final FileInputStream fileInputStream = new FileInputStream(this.file);
        int read;
        while ((read = fileInputStream.read(this.buf, 0, this.buf.length)) > 0) {
            outputStream.write(this.buf, 0, read);
        }
        fileInputStream.close();
    }
    
    public Object getContent() {
        return this.file;
    }
    
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }
}
