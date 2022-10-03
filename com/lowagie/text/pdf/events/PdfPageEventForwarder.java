package com.lowagie.text.pdf.events;

import com.lowagie.text.Rectangle;
import com.lowagie.text.Paragraph;
import java.util.Iterator;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import java.util.ArrayList;
import com.lowagie.text.pdf.PdfPageEvent;

public class PdfPageEventForwarder implements PdfPageEvent
{
    protected ArrayList events;
    
    public PdfPageEventForwarder() {
        this.events = new ArrayList();
    }
    
    public void addPageEvent(final PdfPageEvent event) {
        this.events.add(event);
    }
    
    @Override
    public void onOpenDocument(final PdfWriter writer, final Document document) {
        for (final PdfPageEvent event : this.events) {
            event.onOpenDocument(writer, document);
        }
    }
    
    @Override
    public void onStartPage(final PdfWriter writer, final Document document) {
        for (final PdfPageEvent event : this.events) {
            event.onStartPage(writer, document);
        }
    }
    
    @Override
    public void onEndPage(final PdfWriter writer, final Document document) {
        for (final PdfPageEvent event : this.events) {
            event.onEndPage(writer, document);
        }
    }
    
    @Override
    public void onCloseDocument(final PdfWriter writer, final Document document) {
        for (final PdfPageEvent event : this.events) {
            event.onCloseDocument(writer, document);
        }
    }
    
    @Override
    public void onParagraph(final PdfWriter writer, final Document document, final float paragraphPosition) {
        for (final PdfPageEvent event : this.events) {
            event.onParagraph(writer, document, paragraphPosition);
        }
    }
    
    @Override
    public void onParagraphEnd(final PdfWriter writer, final Document document, final float paragraphPosition) {
        for (final PdfPageEvent event : this.events) {
            event.onParagraphEnd(writer, document, paragraphPosition);
        }
    }
    
    @Override
    public void onChapter(final PdfWriter writer, final Document document, final float paragraphPosition, final Paragraph title) {
        for (final PdfPageEvent event : this.events) {
            event.onChapter(writer, document, paragraphPosition, title);
        }
    }
    
    @Override
    public void onChapterEnd(final PdfWriter writer, final Document document, final float position) {
        for (final PdfPageEvent event : this.events) {
            event.onChapterEnd(writer, document, position);
        }
    }
    
    @Override
    public void onSection(final PdfWriter writer, final Document document, final float paragraphPosition, final int depth, final Paragraph title) {
        for (final PdfPageEvent event : this.events) {
            event.onSection(writer, document, paragraphPosition, depth, title);
        }
    }
    
    @Override
    public void onSectionEnd(final PdfWriter writer, final Document document, final float position) {
        for (final PdfPageEvent event : this.events) {
            event.onSectionEnd(writer, document, position);
        }
    }
    
    @Override
    public void onGenericTag(final PdfWriter writer, final Document document, final Rectangle rect, final String text) {
        for (final PdfPageEvent event : this.events) {
            event.onGenericTag(writer, document, rect, text);
        }
    }
}
