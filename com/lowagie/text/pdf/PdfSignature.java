package com.lowagie.text.pdf;

public class PdfSignature extends PdfDictionary
{
    public PdfSignature(final PdfName filter, final PdfName subFilter) {
        super(PdfName.SIG);
        this.put(PdfName.FILTER, filter);
        this.put(PdfName.SUBFILTER, subFilter);
    }
    
    public void setByteRange(final int[] range) {
        final PdfArray array = new PdfArray();
        for (int k = 0; k < range.length; ++k) {
            array.add(new PdfNumber(range[k]));
        }
        this.put(PdfName.BYTERANGE, array);
    }
    
    public void setContents(final byte[] contents) {
        this.put(PdfName.CONTENTS, new PdfString(contents).setHexWriting(true));
    }
    
    public void setCert(final byte[] cert) {
        this.put(PdfName.CERT, new PdfString(cert));
    }
    
    public void setName(final String name) {
        this.put(PdfName.NAME, new PdfString(name, "UnicodeBig"));
    }
    
    public void setDate(final PdfDate date) {
        this.put(PdfName.M, date);
    }
    
    public void setLocation(final String name) {
        this.put(PdfName.LOCATION, new PdfString(name, "UnicodeBig"));
    }
    
    public void setReason(final String name) {
        this.put(PdfName.REASON, new PdfString(name, "UnicodeBig"));
    }
    
    public void setContact(final String name) {
        this.put(PdfName.CONTACTINFO, new PdfString(name, "UnicodeBig"));
    }
}
