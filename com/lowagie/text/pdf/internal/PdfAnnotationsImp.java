package com.lowagie.text.pdf.internal;

import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfAction;
import java.net.URL;
import com.lowagie.text.Annotation;
import java.util.HashMap;
import java.io.IOException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfWriter;
import java.util.ArrayList;
import com.lowagie.text.pdf.PdfAcroForm;

public class PdfAnnotationsImp
{
    protected PdfAcroForm acroForm;
    protected ArrayList annotations;
    protected ArrayList delayedAnnotations;
    
    public PdfAnnotationsImp(final PdfWriter writer) {
        this.delayedAnnotations = new ArrayList();
        this.acroForm = new PdfAcroForm(writer);
    }
    
    public boolean hasValidAcroForm() {
        return this.acroForm.isValid();
    }
    
    public PdfAcroForm getAcroForm() {
        return this.acroForm;
    }
    
    public void setSigFlags(final int f) {
        this.acroForm.setSigFlags(f);
    }
    
    public void addCalculationOrder(final PdfFormField formField) {
        this.acroForm.addCalculationOrder(formField);
    }
    
    public void addAnnotation(final PdfAnnotation annot) {
        if (annot.isForm()) {
            final PdfFormField field = (PdfFormField)annot;
            if (field.getParent() == null) {
                this.addFormFieldRaw(field);
            }
        }
        else {
            this.annotations.add(annot);
        }
    }
    
    public void addPlainAnnotation(final PdfAnnotation annot) {
        this.annotations.add(annot);
    }
    
    void addFormFieldRaw(final PdfFormField field) {
        this.annotations.add(field);
        final ArrayList kids = field.getKids();
        if (kids != null) {
            for (int k = 0; k < kids.size(); ++k) {
                this.addFormFieldRaw(kids.get(k));
            }
        }
    }
    
    public boolean hasUnusedAnnotations() {
        return !this.annotations.isEmpty();
    }
    
    public void resetAnnotations() {
        this.annotations = this.delayedAnnotations;
        this.delayedAnnotations = new ArrayList();
    }
    
    public PdfArray rotateAnnotations(final PdfWriter writer, final Rectangle pageSize) {
        final PdfArray array = new PdfArray();
        final int rotation = pageSize.getRotation() % 360;
        final int currentPage = writer.getCurrentPageNumber();
        for (int k = 0; k < this.annotations.size(); ++k) {
            final PdfAnnotation dic = this.annotations.get(k);
            final int page = dic.getPlaceInPage();
            if (page > currentPage) {
                this.delayedAnnotations.add(dic);
            }
            else {
                if (dic.isForm()) {
                    if (!dic.isUsed()) {
                        final HashMap templates = dic.getTemplates();
                        if (templates != null) {
                            this.acroForm.addFieldTemplates(templates);
                        }
                    }
                    final PdfFormField field = (PdfFormField)dic;
                    if (field.getParent() == null) {
                        this.acroForm.addDocumentField(field.getIndirectReference());
                    }
                }
                if (dic.isAnnotation()) {
                    array.add(dic.getIndirectReference());
                    if (!dic.isUsed()) {
                        final PdfRectangle rect = (PdfRectangle)dic.get(PdfName.RECT);
                        if (rect != null) {
                            switch (rotation) {
                                case 90: {
                                    dic.put(PdfName.RECT, new PdfRectangle(pageSize.getTop() - rect.bottom(), rect.left(), pageSize.getTop() - rect.top(), rect.right()));
                                    break;
                                }
                                case 180: {
                                    dic.put(PdfName.RECT, new PdfRectangle(pageSize.getRight() - rect.left(), pageSize.getTop() - rect.bottom(), pageSize.getRight() - rect.right(), pageSize.getTop() - rect.top()));
                                    break;
                                }
                                case 270: {
                                    dic.put(PdfName.RECT, new PdfRectangle(rect.bottom(), pageSize.getRight() - rect.left(), rect.top(), pageSize.getRight() - rect.right()));
                                    break;
                                }
                            }
                        }
                    }
                }
                if (!dic.isUsed()) {
                    dic.setUsed();
                    try {
                        writer.addToBody(dic, dic.getIndirectReference());
                    }
                    catch (final IOException e) {
                        throw new ExceptionConverter(e);
                    }
                }
            }
        }
        return array;
    }
    
    public static PdfAnnotation convertAnnotation(final PdfWriter writer, final Annotation annot, final Rectangle defaultRect) throws IOException {
        switch (annot.annotationType()) {
            case 1: {
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction(annot.attributes().get("url")));
            }
            case 2: {
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction(annot.attributes().get("file")));
            }
            case 3: {
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction(annot.attributes().get("file"), annot.attributes().get("destination")));
            }
            case 7: {
                final boolean[] sparams = annot.attributes().get("parameters");
                final String fname = annot.attributes().get("file");
                final String mimetype = annot.attributes().get("mime");
                PdfFileSpecification fs;
                if (sparams[0]) {
                    fs = PdfFileSpecification.fileEmbedded(writer, fname, fname, null);
                }
                else {
                    fs = PdfFileSpecification.fileExtern(writer, fname);
                }
                final PdfAnnotation ann = PdfAnnotation.createScreen(writer, new Rectangle(annot.llx(), annot.lly(), annot.urx(), annot.ury()), fname, fs, mimetype, sparams[1]);
                return ann;
            }
            case 4: {
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction(annot.attributes().get("file"), annot.attributes().get("page")));
            }
            case 5: {
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction(annot.attributes().get("named")));
            }
            case 6: {
                return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction(annot.attributes().get("application"), annot.attributes().get("parameters"), annot.attributes().get("operation"), annot.attributes().get("defaultdir")));
            }
            default: {
                return new PdfAnnotation(writer, defaultRect.getLeft(), defaultRect.getBottom(), defaultRect.getRight(), defaultRect.getTop(), new PdfString(annot.title(), "UnicodeBig"), new PdfString(annot.content(), "UnicodeBig"));
            }
        }
    }
}
