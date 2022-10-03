package com.lowagie.text.pdf.events;

import java.util.Iterator;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import java.util.ArrayList;
import com.lowagie.text.pdf.PdfPCellEvent;

public class PdfPCellEventForwarder implements PdfPCellEvent
{
    protected ArrayList events;
    
    public PdfPCellEventForwarder() {
        this.events = new ArrayList();
    }
    
    public void addCellEvent(final PdfPCellEvent event) {
        this.events.add(event);
    }
    
    @Override
    public void cellLayout(final PdfPCell cell, final Rectangle position, final PdfContentByte[] canvases) {
        for (final PdfPCellEvent event : this.events) {
            event.cellLayout(cell, position, canvases);
        }
    }
}
