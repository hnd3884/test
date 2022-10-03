package com.lowagie.text.pdf;

import com.lowagie.text.pdf.collection.PdfCollectionItem;
import java.io.InputStream;
import com.lowagie.text.error_messages.MessageLocalization;
import java.net.URL;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

public class PdfFileSpecification extends PdfDictionary
{
    protected PdfWriter writer;
    protected PdfIndirectReference ref;
    
    public PdfFileSpecification() {
        super(PdfName.FILESPEC);
    }
    
    public static PdfFileSpecification url(final PdfWriter writer, final String url) {
        final PdfFileSpecification fs = new PdfFileSpecification();
        fs.writer = writer;
        fs.put(PdfName.FS, PdfName.URL);
        fs.put(PdfName.F, new PdfString(url));
        return fs;
    }
    
    public static PdfFileSpecification fileEmbedded(final PdfWriter writer, final String filePath, final String fileDisplay, final byte[] fileStore) throws IOException {
        return fileEmbedded(writer, filePath, fileDisplay, fileStore, 9);
    }
    
    public static PdfFileSpecification fileEmbedded(final PdfWriter writer, final String filePath, final String fileDisplay, final byte[] fileStore, final int compressionLevel) throws IOException {
        return fileEmbedded(writer, filePath, fileDisplay, fileStore, null, null, compressionLevel);
    }
    
    public static PdfFileSpecification fileEmbedded(final PdfWriter writer, final String filePath, final String fileDisplay, final byte[] fileStore, final boolean compress) throws IOException {
        return fileEmbedded(writer, filePath, fileDisplay, fileStore, null, null, compress ? 9 : 0);
    }
    
    public static PdfFileSpecification fileEmbedded(final PdfWriter writer, final String filePath, final String fileDisplay, final byte[] fileStore, final boolean compress, final String mimeType, final PdfDictionary fileParameter) throws IOException {
        return fileEmbedded(writer, filePath, fileDisplay, fileStore, mimeType, fileParameter, compress ? 9 : 0);
    }
    
    public static PdfFileSpecification fileEmbedded(final PdfWriter writer, final String filePath, final String fileDisplay, final byte[] fileStore, final String mimeType, final PdfDictionary fileParameter, final int compressionLevel) throws IOException {
        final PdfFileSpecification fs = new PdfFileSpecification();
        fs.writer = writer;
        fs.put(PdfName.F, new PdfString(fileDisplay));
        fs.setUnicodeFileName(fileDisplay, false);
        InputStream in = null;
        PdfIndirectReference refFileLength = null;
        PdfIndirectReference ref;
        try {
            PdfEFStream stream;
            if (fileStore == null) {
                refFileLength = writer.getPdfIndirectReference();
                final File file = new File(filePath);
                if (file.canRead()) {
                    in = new FileInputStream(filePath);
                }
                else if (filePath.startsWith("file:/") || filePath.startsWith("http://") || filePath.startsWith("https://") || filePath.startsWith("jar:")) {
                    in = new URL(filePath).openStream();
                }
                else {
                    in = BaseFont.getResourceStream(filePath);
                    if (in == null) {
                        throw new IOException(MessageLocalization.getComposedMessage("1.not.found.as.file.or.resource", filePath));
                    }
                }
                stream = new PdfEFStream(in, writer);
            }
            else {
                stream = new PdfEFStream(fileStore);
            }
            stream.put(PdfName.TYPE, PdfName.EMBEDDEDFILE);
            stream.flateCompress(compressionLevel);
            final PdfDictionary param = new PdfDictionary();
            if (fileParameter != null) {
                param.merge(fileParameter);
            }
            if (fileStore != null) {
                param.put(PdfName.SIZE, new PdfNumber(stream.getRawLength()));
                stream.put(PdfName.PARAMS, param);
            }
            else {
                stream.put(PdfName.PARAMS, refFileLength);
            }
            if (mimeType != null) {
                stream.put(PdfName.SUBTYPE, new PdfName(mimeType));
            }
            ref = writer.addToBody(stream).getIndirectReference();
            if (fileStore == null) {
                stream.writeLength();
                param.put(PdfName.SIZE, new PdfNumber(stream.getRawLength()));
                writer.addToBody(param, refFileLength);
            }
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (final Exception ex) {}
            }
        }
        final PdfDictionary f = new PdfDictionary();
        f.put(PdfName.F, ref);
        f.put(PdfName.UF, ref);
        fs.put(PdfName.EF, f);
        return fs;
    }
    
    public static PdfFileSpecification fileExtern(final PdfWriter writer, final String filePath) {
        final PdfFileSpecification fs = new PdfFileSpecification();
        fs.writer = writer;
        fs.put(PdfName.F, new PdfString(filePath));
        fs.setUnicodeFileName(filePath, false);
        return fs;
    }
    
    public PdfIndirectReference getReference() throws IOException {
        if (this.ref != null) {
            return this.ref;
        }
        return this.ref = this.writer.addToBody(this).getIndirectReference();
    }
    
    public void setMultiByteFileName(final byte[] fileName) {
        this.put(PdfName.F, new PdfString(fileName).setHexWriting(true));
    }
    
    public void setUnicodeFileName(final String filename, final boolean unicode) {
        this.put(PdfName.UF, new PdfString(filename, unicode ? "UnicodeBig" : "PDF"));
    }
    
    public void setVolatile(final boolean volatile_file) {
        this.put(PdfName.V, new PdfBoolean(volatile_file));
    }
    
    public void addDescription(final String description, final boolean unicode) {
        this.put(PdfName.DESC, new PdfString(description, unicode ? "UnicodeBig" : "PDF"));
    }
    
    public void addCollectionItem(final PdfCollectionItem ci) {
        this.put(PdfName.CI, ci);
    }
}
