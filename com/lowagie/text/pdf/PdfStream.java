package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.ExceptionConverter;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Deflater;
import com.lowagie.text.Document;
import java.io.IOException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

public class PdfStream extends PdfDictionary
{
    public static final int DEFAULT_COMPRESSION = -1;
    public static final int NO_COMPRESSION = 0;
    public static final int BEST_SPEED = 1;
    public static final int BEST_COMPRESSION = 9;
    protected boolean compressed;
    protected int compressionLevel;
    protected ByteArrayOutputStream streamBytes;
    protected InputStream inputStream;
    protected PdfIndirectReference ref;
    protected int inputStreamLength;
    protected PdfWriter writer;
    protected int rawLength;
    static final byte[] STARTSTREAM;
    static final byte[] ENDSTREAM;
    static final int SIZESTREAM;
    
    public PdfStream(final byte[] bytes) {
        this.compressed = false;
        this.compressionLevel = 0;
        this.streamBytes = null;
        this.inputStreamLength = -1;
        this.type = 7;
        this.bytes = bytes;
        this.rawLength = bytes.length;
        this.put(PdfName.LENGTH, new PdfNumber(bytes.length));
    }
    
    public PdfStream(final InputStream inputStream, final PdfWriter writer) {
        this.compressed = false;
        this.compressionLevel = 0;
        this.streamBytes = null;
        this.inputStreamLength = -1;
        this.type = 7;
        this.inputStream = inputStream;
        this.writer = writer;
        this.ref = writer.getPdfIndirectReference();
        this.put(PdfName.LENGTH, this.ref);
    }
    
    protected PdfStream() {
        this.compressed = false;
        this.compressionLevel = 0;
        this.streamBytes = null;
        this.inputStreamLength = -1;
        this.type = 7;
    }
    
    public void writeLength() throws IOException {
        if (this.inputStream == null) {
            throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("writelength.can.only.be.called.in.a.contructed.pdfstream.inputstream.pdfwriter"));
        }
        if (this.inputStreamLength == -1) {
            throw new IOException(MessageLocalization.getComposedMessage("writelength.can.only.be.called.after.output.of.the.stream.body"));
        }
        this.writer.addToBody(new PdfNumber(this.inputStreamLength), this.ref, false);
    }
    
    public int getRawLength() {
        return this.rawLength;
    }
    
    public void flateCompress() {
        this.flateCompress(-1);
    }
    
    public void flateCompress(final int compressionLevel) {
        if (!Document.compress) {
            return;
        }
        if (this.compressed) {
            return;
        }
        this.compressionLevel = compressionLevel;
        if (this.inputStream != null) {
            this.compressed = true;
            return;
        }
        final PdfObject filter = PdfReader.getPdfObject(this.get(PdfName.FILTER));
        if (filter != null) {
            if (filter.isName()) {
                if (PdfName.FLATEDECODE.equals(filter)) {
                    return;
                }
            }
            else {
                if (!filter.isArray()) {
                    throw new RuntimeException(MessageLocalization.getComposedMessage("stream.could.not.be.compressed.filter.is.not.a.name.or.array"));
                }
                if (((PdfArray)filter).contains(PdfName.FLATEDECODE)) {
                    return;
                }
            }
        }
        try {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            final Deflater deflater = new Deflater(compressionLevel);
            final DeflaterOutputStream zip = new DeflaterOutputStream(stream, deflater);
            if (this.streamBytes != null) {
                this.streamBytes.writeTo(zip);
            }
            else {
                zip.write(this.bytes);
            }
            zip.close();
            deflater.end();
            this.streamBytes = stream;
            this.bytes = null;
            this.put(PdfName.LENGTH, new PdfNumber(this.streamBytes.size()));
            if (filter == null) {
                this.put(PdfName.FILTER, PdfName.FLATEDECODE);
            }
            else {
                final PdfArray filters = new PdfArray(filter);
                filters.add(PdfName.FLATEDECODE);
                this.put(PdfName.FILTER, filters);
            }
            this.compressed = true;
        }
        catch (final IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }
    
    protected void superToPdf(final PdfWriter writer, final OutputStream os) throws IOException {
        super.toPdf(writer, os);
    }
    
    @Override
    public void toPdf(final PdfWriter writer, final OutputStream os) throws IOException {
        if (this.inputStream != null && this.compressed) {
            this.put(PdfName.FILTER, PdfName.FLATEDECODE);
        }
        PdfEncryption crypto = null;
        if (writer != null) {
            crypto = writer.getEncryption();
        }
        if (crypto != null) {
            final PdfObject filter = this.get(PdfName.FILTER);
            if (filter != null) {
                if (PdfName.CRYPT.equals(filter)) {
                    crypto = null;
                }
                else if (filter.isArray()) {
                    final PdfArray a = (PdfArray)filter;
                    if (!a.isEmpty() && PdfName.CRYPT.equals(a.getPdfObject(0))) {
                        crypto = null;
                    }
                }
            }
        }
        final PdfObject nn = this.get(PdfName.LENGTH);
        if (crypto != null && nn != null && nn.isNumber()) {
            final int sz = ((PdfNumber)nn).intValue();
            this.put(PdfName.LENGTH, new PdfNumber(crypto.calculateStreamSize(sz)));
            this.superToPdf(writer, os);
            this.put(PdfName.LENGTH, nn);
        }
        else {
            this.superToPdf(writer, os);
        }
        os.write(PdfStream.STARTSTREAM);
        if (this.inputStream != null) {
            this.rawLength = 0;
            DeflaterOutputStream def = null;
            final OutputStreamCounter osc = new OutputStreamCounter(os);
            OutputStreamEncryption ose = null;
            OutputStream fout = osc;
            if (crypto != null && !crypto.isEmbeddedFilesOnly()) {
                ose = (OutputStreamEncryption)(fout = crypto.getEncryptionStream(fout));
            }
            Deflater deflater = null;
            if (this.compressed) {
                deflater = new Deflater(this.compressionLevel);
                def = (DeflaterOutputStream)(fout = new DeflaterOutputStream(fout, deflater, 32768));
            }
            final byte[] buf = new byte[4192];
            while (true) {
                final int n = this.inputStream.read(buf);
                if (n <= 0) {
                    break;
                }
                fout.write(buf, 0, n);
                this.rawLength += n;
            }
            if (def != null) {
                def.finish();
                deflater.end();
            }
            if (ose != null) {
                ose.finish();
            }
            this.inputStreamLength = osc.getCounter();
        }
        else if (crypto != null && !crypto.isEmbeddedFilesOnly()) {
            byte[] b;
            if (this.streamBytes != null) {
                b = crypto.encryptByteArray(this.streamBytes.toByteArray());
            }
            else {
                b = crypto.encryptByteArray(this.bytes);
            }
            os.write(b);
        }
        else if (this.streamBytes != null) {
            this.streamBytes.writeTo(os);
        }
        else {
            os.write(this.bytes);
        }
        os.write(PdfStream.ENDSTREAM);
    }
    
    public void writeContent(final OutputStream os) throws IOException {
        if (this.streamBytes != null) {
            this.streamBytes.writeTo(os);
        }
        else if (this.bytes != null) {
            os.write(this.bytes);
        }
    }
    
    @Override
    public String toString() {
        if (this.get(PdfName.TYPE) == null) {
            return "Stream";
        }
        return "Stream of type: " + this.get(PdfName.TYPE);
    }
    
    static {
        STARTSTREAM = DocWriter.getISOBytes("stream\n");
        ENDSTREAM = DocWriter.getISOBytes("\nendstream");
        SIZESTREAM = PdfStream.STARTSTREAM.length + PdfStream.ENDSTREAM.length;
    }
}
