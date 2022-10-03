package com.lowagie.text.pdf;

import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Deflater;
import java.io.OutputStream;
import java.io.InputStream;

public class PdfEFStream extends PdfStream
{
    public PdfEFStream(final InputStream in, final PdfWriter writer) {
        super(in, writer);
    }
    
    public PdfEFStream(final byte[] fileStore) {
        super(fileStore);
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
        if (crypto != null && crypto.isEmbeddedFilesOnly()) {
            final PdfArray filter2 = new PdfArray();
            final PdfArray decodeparms = new PdfArray();
            final PdfDictionary crypt = new PdfDictionary();
            crypt.put(PdfName.NAME, PdfName.STDCF);
            filter2.add(PdfName.CRYPT);
            decodeparms.add(crypt);
            if (this.compressed) {
                filter2.add(PdfName.FLATEDECODE);
                decodeparms.add(new PdfNull());
            }
            this.put(PdfName.FILTER, filter2);
            this.put(PdfName.DECODEPARMS, decodeparms);
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
        os.write(PdfEFStream.STARTSTREAM);
        if (this.inputStream != null) {
            this.rawLength = 0;
            DeflaterOutputStream def = null;
            final OutputStreamCounter osc = new OutputStreamCounter(os);
            OutputStreamEncryption ose = null;
            OutputStream fout = osc;
            if (crypto != null) {
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
        else if (crypto == null) {
            if (this.streamBytes != null) {
                this.streamBytes.writeTo(os);
            }
            else {
                os.write(this.bytes);
            }
        }
        else {
            byte[] b;
            if (this.streamBytes != null) {
                b = crypto.encryptByteArray(this.streamBytes.toByteArray());
            }
            else {
                b = crypto.encryptByteArray(this.bytes);
            }
            os.write(b);
        }
        os.write(PdfEFStream.ENDSTREAM);
    }
}
