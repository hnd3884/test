package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import java.util.Arrays;
import java.security.MessageDigest;
import java.io.IOException;
import com.lowagie.text.DocumentException;
import java.io.OutputStream;
import com.lowagie.text.Document;
import java.util.HashMap;

public class PdfSmartCopy extends PdfCopy
{
    private HashMap streamMap;
    
    public PdfSmartCopy(final Document document, final OutputStream os) throws DocumentException {
        super(document, os);
        this.streamMap = null;
        this.streamMap = new HashMap();
    }
    
    @Override
    protected PdfIndirectReference copyIndirect(final PRIndirectReference in) throws IOException, BadPdfFormatException {
        final PdfObject srcObj = PdfReader.getPdfObjectRelease(in);
        ByteStore streamKey = null;
        boolean validStream = false;
        if (srcObj.isStream()) {
            streamKey = new ByteStore((PRStream)srcObj);
            validStream = true;
            final PdfIndirectReference streamRef = this.streamMap.get(streamKey);
            if (streamRef != null) {
                return streamRef;
            }
        }
        final RefKey key = new RefKey(in);
        IndirectReferences iRef = this.indirects.get(key);
        PdfIndirectReference theRef;
        if (iRef != null) {
            theRef = iRef.getRef();
            if (iRef.getCopied()) {
                return theRef;
            }
        }
        else {
            theRef = this.body.getPdfIndirectReference();
            iRef = new IndirectReferences(theRef);
            this.indirects.put(key, iRef);
        }
        if (srcObj.isDictionary()) {
            final PdfObject type = PdfReader.getPdfObjectRelease(((PdfDictionary)srcObj).get(PdfName.TYPE));
            if (type != null && PdfName.PAGE.equals(type)) {
                return theRef;
            }
        }
        iRef.setCopied();
        if (validStream) {
            this.streamMap.put(streamKey, theRef);
        }
        final PdfObject obj = this.copyObject(srcObj);
        this.addToBody(obj, theRef);
        return theRef;
    }
    
    static class ByteStore
    {
        private byte[] b;
        private int hash;
        private MessageDigest md5;
        
        private void serObject(PdfObject obj, final int level, final ByteBuffer bb) throws IOException {
            if (level <= 0) {
                return;
            }
            if (obj == null) {
                bb.append("$Lnull");
                return;
            }
            obj = PdfReader.getPdfObject(obj);
            if (obj.isStream()) {
                bb.append("$B");
                this.serDic((PdfDictionary)obj, level - 1, bb);
                if (level > 0) {
                    this.md5.reset();
                    bb.append(this.md5.digest(PdfReader.getStreamBytesRaw((PRStream)obj)));
                }
            }
            else if (obj.isDictionary()) {
                this.serDic((PdfDictionary)obj, level - 1, bb);
            }
            else if (obj.isArray()) {
                this.serArray((PdfArray)obj, level - 1, bb);
            }
            else if (obj.isString()) {
                bb.append("$S").append(obj.toString());
            }
            else if (obj.isName()) {
                bb.append("$N").append(obj.toString());
            }
            else {
                bb.append("$L").append(obj.toString());
            }
        }
        
        private void serDic(final PdfDictionary dic, final int level, final ByteBuffer bb) throws IOException {
            bb.append("$D");
            if (level <= 0) {
                return;
            }
            final Object[] keys = dic.getKeys().toArray();
            Arrays.sort(keys);
            for (int k = 0; k < keys.length; ++k) {
                this.serObject((PdfObject)keys[k], level, bb);
                this.serObject(dic.get((PdfName)keys[k]), level, bb);
            }
        }
        
        private void serArray(final PdfArray array, final int level, final ByteBuffer bb) throws IOException {
            bb.append("$A");
            if (level <= 0) {
                return;
            }
            for (int k = 0; k < array.size(); ++k) {
                this.serObject(array.getPdfObject(k), level, bb);
            }
        }
        
        ByteStore(final PRStream str) throws IOException {
            try {
                this.md5 = MessageDigest.getInstance("MD5");
            }
            catch (final Exception e) {
                throw new ExceptionConverter(e);
            }
            final ByteBuffer bb = new ByteBuffer();
            final int level = 100;
            this.serObject(str, level, bb);
            this.b = bb.toByteArray();
            this.md5 = null;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof ByteStore && this.hashCode() == obj.hashCode() && Arrays.equals(this.b, ((ByteStore)obj).b);
        }
        
        @Override
        public int hashCode() {
            if (this.hash == 0) {
                for (int len = this.b.length, k = 0; k < len; ++k) {
                    this.hash = this.hash * 31 + (this.b[k] & 0xFF);
                }
            }
            return this.hash;
        }
    }
}
