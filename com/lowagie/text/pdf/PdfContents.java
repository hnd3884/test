package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Deflater;
import com.lowagie.text.Document;
import java.io.ByteArrayOutputStream;
import com.lowagie.text.Rectangle;

class PdfContents extends PdfStream
{
    static final byte[] SAVESTATE;
    static final byte[] RESTORESTATE;
    static final byte[] ROTATE90;
    static final byte[] ROTATE180;
    static final byte[] ROTATE270;
    static final byte[] ROTATEFINAL;
    
    PdfContents(final PdfContentByte under, final PdfContentByte content, final PdfContentByte text, final PdfContentByte secondContent, final Rectangle page) throws BadPdfFormatException {
        try {
            OutputStream out = null;
            Deflater deflater = null;
            this.streamBytes = new ByteArrayOutputStream();
            if (Document.compress) {
                this.compressed = true;
                this.compressionLevel = text.getPdfWriter().getCompressionLevel();
                deflater = new Deflater(this.compressionLevel);
                out = new DeflaterOutputStream(this.streamBytes, deflater);
            }
            else {
                out = this.streamBytes;
            }
            final int rotation = page.getRotation();
            switch (rotation) {
                case 90: {
                    out.write(PdfContents.ROTATE90);
                    out.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(page.getTop())));
                    out.write(32);
                    out.write(48);
                    out.write(PdfContents.ROTATEFINAL);
                    break;
                }
                case 180: {
                    out.write(PdfContents.ROTATE180);
                    out.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(page.getRight())));
                    out.write(32);
                    out.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(page.getTop())));
                    out.write(PdfContents.ROTATEFINAL);
                    break;
                }
                case 270: {
                    out.write(PdfContents.ROTATE270);
                    out.write(48);
                    out.write(32);
                    out.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(page.getRight())));
                    out.write(PdfContents.ROTATEFINAL);
                    break;
                }
            }
            if (under.size() > 0) {
                out.write(PdfContents.SAVESTATE);
                under.getInternalBuffer().writeTo(out);
                out.write(PdfContents.RESTORESTATE);
            }
            if (content.size() > 0) {
                out.write(PdfContents.SAVESTATE);
                content.getInternalBuffer().writeTo(out);
                out.write(PdfContents.RESTORESTATE);
            }
            if (text != null) {
                out.write(PdfContents.SAVESTATE);
                text.getInternalBuffer().writeTo(out);
                out.write(PdfContents.RESTORESTATE);
            }
            if (secondContent.size() > 0) {
                secondContent.getInternalBuffer().writeTo(out);
            }
            out.close();
            if (deflater != null) {
                deflater.end();
            }
        }
        catch (final Exception e) {
            throw new BadPdfFormatException(e.getMessage());
        }
        this.put(PdfName.LENGTH, new PdfNumber(this.streamBytes.size()));
        if (this.compressed) {
            this.put(PdfName.FILTER, PdfName.FLATEDECODE);
        }
    }
    
    static {
        SAVESTATE = DocWriter.getISOBytes("q\n");
        RESTORESTATE = DocWriter.getISOBytes("Q\n");
        ROTATE90 = DocWriter.getISOBytes("0 1 -1 0 ");
        ROTATE180 = DocWriter.getISOBytes("-1 0 0 -1 ");
        ROTATE270 = DocWriter.getISOBytes("0 -1 1 0 ");
        ROTATEFINAL = DocWriter.getISOBytes(" cm\n");
    }
}
