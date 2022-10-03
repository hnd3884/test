package com.lowagie.text.pdf.events;

import java.util.Iterator;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import java.util.ArrayList;
import com.lowagie.text.pdf.PdfPTableEvent;

public class PdfPTableEventForwarder implements PdfPTableEvent
{
    protected ArrayList events;
    
    public PdfPTableEventForwarder() {
        this.events = new ArrayList();
    }
    
    public void addTableEvent(final PdfPTableEvent event) {
        this.events.add(event);
    }
    
    @Override
    public void tableLayout(final PdfPTable table, final float[][] widths, final float[] heights, final int headerRows, final int rowStart, final PdfContentByte[] canvases) {
        for (final PdfPTableEvent event : this.events) {
            event.tableLayout(table, widths, heights, headerRows, rowStart, canvases);
        }
    }
}
