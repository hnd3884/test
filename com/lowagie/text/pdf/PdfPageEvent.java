package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Document;

public interface PdfPageEvent
{
    void onOpenDocument(final PdfWriter p0, final Document p1);
    
    void onStartPage(final PdfWriter p0, final Document p1);
    
    void onEndPage(final PdfWriter p0, final Document p1);
    
    void onCloseDocument(final PdfWriter p0, final Document p1);
    
    void onParagraph(final PdfWriter p0, final Document p1, final float p2);
    
    void onParagraphEnd(final PdfWriter p0, final Document p1, final float p2);
    
    void onChapter(final PdfWriter p0, final Document p1, final float p2, final Paragraph p3);
    
    void onChapterEnd(final PdfWriter p0, final Document p1, final float p2);
    
    void onSection(final PdfWriter p0, final Document p1, final float p2, final int p3, final Paragraph p4);
    
    void onSectionEnd(final PdfWriter p0, final Document p1, final float p2);
    
    void onGenericTag(final PdfWriter p0, final Document p1, final Rectangle p2, final String p3);
}
