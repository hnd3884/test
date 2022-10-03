package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Document;

public class PdfPageEventHelper implements PdfPageEvent
{
    @Override
    public void onOpenDocument(final PdfWriter writer, final Document document) {
    }
    
    @Override
    public void onStartPage(final PdfWriter writer, final Document document) {
    }
    
    @Override
    public void onEndPage(final PdfWriter writer, final Document document) {
    }
    
    @Override
    public void onCloseDocument(final PdfWriter writer, final Document document) {
    }
    
    @Override
    public void onParagraph(final PdfWriter writer, final Document document, final float paragraphPosition) {
    }
    
    @Override
    public void onParagraphEnd(final PdfWriter writer, final Document document, final float paragraphPosition) {
    }
    
    @Override
    public void onChapter(final PdfWriter writer, final Document document, final float paragraphPosition, final Paragraph title) {
    }
    
    @Override
    public void onChapterEnd(final PdfWriter writer, final Document document, final float position) {
    }
    
    @Override
    public void onSection(final PdfWriter writer, final Document document, final float paragraphPosition, final int depth, final Paragraph title) {
    }
    
    @Override
    public void onSectionEnd(final PdfWriter writer, final Document document, final float position) {
    }
    
    @Override
    public void onGenericTag(final PdfWriter writer, final Document document, final Rectangle rect, final String text) {
    }
}
