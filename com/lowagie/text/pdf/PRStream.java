package com.lowagie.text.pdf;

import java.io.IOException;
import com.lowagie.text.ExceptionConverter;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Deflater;
import java.io.ByteArrayOutputStream;
import com.lowagie.text.Document;
import java.util.Map;

public class PRStream extends PdfStream
{
    protected PdfReader reader;
    protected int offset;
    protected int length;
    protected int objNum;
    protected int objGen;
    
    public PRStream(final PRStream stream, final PdfDictionary newDic) {
        this.objNum = 0;
        this.objGen = 0;
        this.reader = stream.reader;
        this.offset = stream.offset;
        this.length = stream.length;
        this.compressed = stream.compressed;
        this.compressionLevel = stream.compressionLevel;
        this.streamBytes = stream.streamBytes;
        this.bytes = stream.bytes;
        this.objNum = stream.objNum;
        this.objGen = stream.objGen;
        if (newDic != null) {
            this.putAll(newDic);
        }
        else {
            this.hashMap.putAll(stream.hashMap);
        }
    }
    
    public PRStream(final PRStream stream, final PdfDictionary newDic, final PdfReader reader) {
        this(stream, newDic);
        this.reader = reader;
    }
    
    public PRStream(final PdfReader reader, final int offset) {
        this.objNum = 0;
        this.objGen = 0;
        this.reader = reader;
        this.offset = offset;
    }
    
    public PRStream(final PdfReader reader, final byte[] conts) {
        this(reader, conts, -1);
    }
    
    public PRStream(final PdfReader reader, final byte[] conts, final int compressionLevel) {
        this.objNum = 0;
        this.objGen = 0;
        this.reader = reader;
        this.offset = -1;
        if (Document.compress) {
            try {
                final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                final Deflater deflater = new Deflater(compressionLevel);
                final DeflaterOutputStream zip = new DeflaterOutputStream(stream, deflater);
                zip.write(conts);
                zip.close();
                deflater.end();
                this.bytes = stream.toByteArray();
            }
            catch (final IOException ioe) {
                throw new ExceptionConverter(ioe);
            }
            this.put(PdfName.FILTER, PdfName.FLATEDECODE);
        }
        else {
            this.bytes = conts;
        }
        this.setLength(this.bytes.length);
    }
    
    public void setData(final byte[] data, final boolean compress) {
        this.setData(data, compress, -1);
    }
    
    public void setData(final byte[] data, final boolean compress, final int compressionLevel) {
        this.remove(PdfName.FILTER);
        this.offset = -1;
        if (Document.compress && compress) {
            try {
                final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                final Deflater deflater = new Deflater(compressionLevel);
                final DeflaterOutputStream zip = new DeflaterOutputStream(stream, deflater);
                zip.write(data);
                zip.close();
                deflater.end();
                this.bytes = stream.toByteArray();
                this.compressionLevel = compressionLevel;
            }
            catch (final IOException ioe) {
                throw new ExceptionConverter(ioe);
            }
            this.put(PdfName.FILTER, PdfName.FLATEDECODE);
        }
        else {
            this.bytes = data;
        }
        this.setLength(this.bytes.length);
    }
    
    public void setData(final byte[] data) {
        this.setData(data, true);
    }
    
    public void setLength(final int length) {
        this.length = length;
        this.put(PdfName.LENGTH, new PdfNumber(length));
    }
    
    public int getOffset() {
        return this.offset;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public PdfReader getReader() {
        return this.reader;
    }
    
    @Override
    public byte[] getBytes() {
        return this.bytes;
    }
    
    public void setObjNum(final int objNum, final int objGen) {
        this.objNum = objNum;
        this.objGen = objGen;
    }
    
    int getObjNum() {
        return this.objNum;
    }
    
    int getObjGen() {
        return this.objGen;
    }
    
    @Override
    public void toPdf(final PdfWriter writer, final OutputStream os) throws IOException {
        byte[] b = PdfReader.getStreamBytesRaw(this);
        PdfEncryption crypto = null;
        if (writer != null) {
            crypto = writer.getEncryption();
        }
        final PdfObject objLen = this.get(PdfName.LENGTH);
        int nn = b.length;
        if (crypto != null) {
            nn = crypto.calculateStreamSize(nn);
        }
        this.put(PdfName.LENGTH, new PdfNumber(nn));
        this.superToPdf(writer, os);
        this.put(PdfName.LENGTH, objLen);
        os.write(PRStream.STARTSTREAM);
        if (this.length > 0) {
            if (crypto != null && !crypto.isEmbeddedFilesOnly()) {
                b = crypto.encryptByteArray(b);
            }
            os.write(b);
        }
        os.write(PRStream.ENDSTREAM);
    }
}
