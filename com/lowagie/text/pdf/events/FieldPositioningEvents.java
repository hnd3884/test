package com.lowagie.text.pdf.events;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import com.lowagie.text.pdf.TextField;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfFormField;
import java.util.HashMap;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPageEventHelper;

public class FieldPositioningEvents extends PdfPageEventHelper implements PdfPCellEvent
{
    protected HashMap genericChunkFields;
    protected PdfFormField cellField;
    protected PdfWriter fieldWriter;
    protected PdfFormField parent;
    public float padding;
    
    public FieldPositioningEvents() {
        this.genericChunkFields = new HashMap();
        this.cellField = null;
        this.fieldWriter = null;
        this.parent = null;
    }
    
    public void addField(final String text, final PdfFormField field) {
        this.genericChunkFields.put(text, field);
    }
    
    public FieldPositioningEvents(final PdfWriter writer, final PdfFormField field) {
        this.genericChunkFields = new HashMap();
        this.cellField = null;
        this.fieldWriter = null;
        this.parent = null;
        this.cellField = field;
        this.fieldWriter = writer;
    }
    
    public FieldPositioningEvents(final PdfFormField parent, final PdfFormField field) {
        this.genericChunkFields = new HashMap();
        this.cellField = null;
        this.fieldWriter = null;
        this.parent = null;
        this.cellField = field;
        this.parent = parent;
    }
    
    public FieldPositioningEvents(final PdfWriter writer, final String text) throws IOException, DocumentException {
        this.genericChunkFields = new HashMap();
        this.cellField = null;
        this.fieldWriter = null;
        this.parent = null;
        this.fieldWriter = writer;
        final TextField tf = new TextField(writer, new Rectangle(0.0f, 0.0f), text);
        tf.setFontSize(14.0f);
        this.cellField = tf.getTextField();
    }
    
    public FieldPositioningEvents(final PdfWriter writer, final PdfFormField parent, final String text) throws IOException, DocumentException {
        this.genericChunkFields = new HashMap();
        this.cellField = null;
        this.fieldWriter = null;
        this.parent = null;
        this.parent = parent;
        final TextField tf = new TextField(writer, new Rectangle(0.0f, 0.0f), text);
        tf.setFontSize(14.0f);
        this.cellField = tf.getTextField();
    }
    
    public void setPadding(final float padding) {
        this.padding = padding;
    }
    
    public void setParent(final PdfFormField parent) {
        this.parent = parent;
    }
    
    @Override
    public void onGenericTag(final PdfWriter writer, final Document document, final Rectangle rect, final String text) {
        rect.setBottom(rect.getBottom() - 3.0f);
        PdfFormField field = this.genericChunkFields.get(text);
        if (field == null) {
            final TextField tf = new TextField(writer, new Rectangle(rect.getLeft(this.padding), rect.getBottom(this.padding), rect.getRight(this.padding), rect.getTop(this.padding)), text);
            tf.setFontSize(14.0f);
            try {
                field = tf.getTextField();
            }
            catch (final Exception e) {
                throw new ExceptionConverter(e);
            }
        }
        else {
            field.put(PdfName.RECT, new PdfRectangle(rect.getLeft(this.padding), rect.getBottom(this.padding), rect.getRight(this.padding), rect.getTop(this.padding)));
        }
        if (this.parent == null) {
            writer.addAnnotation(field);
        }
        else {
            this.parent.addKid(field);
        }
    }
    
    @Override
    public void cellLayout(final PdfPCell cell, final Rectangle rect, final PdfContentByte[] canvases) {
        if (this.cellField == null || (this.fieldWriter == null && this.parent == null)) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("you.have.used.the.wrong.constructor.for.this.fieldpositioningevents.class"));
        }
        this.cellField.put(PdfName.RECT, new PdfRectangle(rect.getLeft(this.padding), rect.getBottom(this.padding), rect.getRight(this.padding), rect.getTop(this.padding)));
        if (this.parent == null) {
            this.fieldWriter.addAnnotation(this.cellField);
        }
        else {
            this.parent.addKid(this.cellField);
        }
    }
}
