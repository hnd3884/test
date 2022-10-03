package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfDictionary;

public class PdfTargetDictionary extends PdfDictionary
{
    public PdfTargetDictionary(final PdfTargetDictionary nested) {
        this.put(PdfName.R, PdfName.P);
        if (nested != null) {
            this.setAdditionalPath(nested);
        }
    }
    
    public PdfTargetDictionary(final boolean child) {
        if (child) {
            this.put(PdfName.R, PdfName.C);
        }
        else {
            this.put(PdfName.R, PdfName.P);
        }
    }
    
    public void setEmbeddedFileName(final String target) {
        this.put(PdfName.N, new PdfString(target, null));
    }
    
    public void setFileAttachmentPagename(final String name) {
        this.put(PdfName.P, new PdfString(name, null));
    }
    
    public void setFileAttachmentPage(final int page) {
        this.put(PdfName.P, new PdfNumber(page));
    }
    
    public void setFileAttachmentName(final String name) {
        this.put(PdfName.A, new PdfString(name, "UnicodeBig"));
    }
    
    public void setFileAttachmentIndex(final int annotation) {
        this.put(PdfName.A, new PdfNumber(annotation));
    }
    
    public void setAdditionalPath(final PdfTargetDictionary nested) {
        this.put(PdfName.T, nested);
    }
}
