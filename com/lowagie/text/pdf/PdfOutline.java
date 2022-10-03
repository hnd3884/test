package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import com.lowagie.text.Chunk;
import com.lowagie.text.Paragraph;
import java.util.ArrayList;
import java.awt.Color;
import java.util.List;

public class PdfOutline extends PdfDictionary
{
    private PdfIndirectReference reference;
    private int count;
    private PdfOutline parent;
    private PdfDestination destination;
    private PdfAction action;
    protected List<PdfOutline> kids;
    protected PdfWriter writer;
    private String tag;
    private boolean open;
    private Color color;
    private int style;
    
    PdfOutline(final PdfWriter writer) {
        super(PdfOutline.OUTLINES);
        this.count = 0;
        this.kids = new ArrayList<PdfOutline>();
        this.style = 0;
        this.open = true;
        this.parent = null;
        this.writer = writer;
    }
    
    public PdfOutline(final PdfOutline parent, final PdfAction action, final String title) {
        this(parent, action, title, true);
    }
    
    public PdfOutline(final PdfOutline parent, final PdfAction action, final String title, final boolean open) {
        this.count = 0;
        this.kids = new ArrayList<PdfOutline>();
        this.style = 0;
        this.action = action;
        this.initOutline(parent, title, open);
    }
    
    public PdfOutline(final PdfOutline parent, final PdfDestination destination, final String title) {
        this(parent, destination, title, true);
    }
    
    public PdfOutline(final PdfOutline parent, final PdfDestination destination, final String title, final boolean open) {
        this.count = 0;
        this.kids = new ArrayList<PdfOutline>();
        this.style = 0;
        this.destination = destination;
        this.initOutline(parent, title, open);
    }
    
    public PdfOutline(final PdfOutline parent, final PdfAction action, final PdfString title) {
        this(parent, action, title, true);
    }
    
    public PdfOutline(final PdfOutline parent, final PdfAction action, final PdfString title, final boolean open) {
        this(parent, action, title.toString(), open);
    }
    
    public PdfOutline(final PdfOutline parent, final PdfDestination destination, final PdfString title) {
        this(parent, destination, title, true);
    }
    
    public PdfOutline(final PdfOutline parent, final PdfDestination destination, final PdfString title, final boolean open) {
        this(parent, destination, title.toString(), true);
    }
    
    public PdfOutline(final PdfOutline parent, final PdfAction action, final Paragraph title) {
        this(parent, action, title, true);
    }
    
    public PdfOutline(final PdfOutline parent, final PdfAction action, final Paragraph title, final boolean open) {
        this.count = 0;
        this.kids = new ArrayList<PdfOutline>();
        this.style = 0;
        final StringBuilder buf = new StringBuilder();
        for (final Object o : title.getChunks()) {
            final Chunk chunk = (Chunk)o;
            buf.append(chunk.getContent());
        }
        this.action = action;
        this.initOutline(parent, buf.toString(), open);
    }
    
    public PdfOutline(final PdfOutline parent, final PdfDestination destination, final Paragraph title) {
        this(parent, destination, title, true);
    }
    
    public PdfOutline(final PdfOutline parent, final PdfDestination destination, final Paragraph title, final boolean open) {
        this.count = 0;
        this.kids = new ArrayList<PdfOutline>();
        this.style = 0;
        final StringBuilder buf = new StringBuilder();
        for (final Object o : title.getChunks()) {
            final Chunk chunk = (Chunk)o;
            buf.append(chunk.getContent());
        }
        this.destination = destination;
        this.initOutline(parent, buf.toString(), open);
    }
    
    void initOutline(final PdfOutline parent, final String title, final boolean open) {
        this.open = open;
        this.parent = parent;
        this.writer = parent.writer;
        this.put(PdfName.TITLE, new PdfString(title, "UnicodeBig"));
        parent.addKid(this);
        if (this.destination != null && !this.destination.hasPage()) {
            this.setDestinationPage(this.writer.getCurrentPage());
        }
    }
    
    public void setIndirectReference(final PdfIndirectReference reference) {
        this.reference = reference;
    }
    
    public PdfIndirectReference indirectReference() {
        return this.reference;
    }
    
    public PdfOutline parent() {
        return this.parent;
    }
    
    public boolean setDestinationPage(final PdfIndirectReference pageReference) {
        return this.destination != null && this.destination.addPage(pageReference);
    }
    
    public PdfDestination getPdfDestination() {
        return this.destination;
    }
    
    int getCount() {
        return this.count;
    }
    
    void setCount(final int count) {
        this.count = count;
    }
    
    public int level() {
        if (this.parent == null) {
            return 0;
        }
        return this.parent.level() + 1;
    }
    
    @Override
    public void toPdf(final PdfWriter writer, final OutputStream os) throws IOException {
        if (this.color != null && !this.color.equals(Color.black)) {
            this.put(PdfName.C, new PdfArray(new float[] { this.color.getRed() / 255.0f, this.color.getGreen() / 255.0f, this.color.getBlue() / 255.0f }));
        }
        int flag = 0;
        if ((this.style & 0x1) != 0x0) {
            flag |= 0x2;
        }
        if ((this.style & 0x2) != 0x0) {
            flag |= 0x1;
        }
        if (flag != 0) {
            this.put(PdfName.F, new PdfNumber(flag));
        }
        if (this.parent != null) {
            this.put(PdfName.PARENT, this.parent.indirectReference());
        }
        if (this.destination != null && this.destination.hasPage()) {
            this.put(PdfName.DEST, this.destination);
        }
        if (this.action != null) {
            this.put(PdfName.A, this.action);
        }
        if (this.count != 0) {
            this.put(PdfName.COUNT, new PdfNumber(this.count));
        }
        super.toPdf(writer, os);
    }
    
    public void addKid(final PdfOutline outline) {
        this.kids.add(outline);
    }
    
    public List<PdfOutline> getKids() {
        return this.kids;
    }
    
    public void setKids(final List<PdfOutline> kids) {
        this.kids = kids;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public void setTag(final String tag) {
        this.tag = tag;
    }
    
    public String getTitle() {
        final PdfString title = (PdfString)this.get(PdfName.TITLE);
        return title.toString();
    }
    
    public void setTitle(final String title) {
        this.put(PdfName.TITLE, new PdfString(title, "UnicodeBig"));
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    public void setOpen(final boolean open) {
        this.open = open;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void setColor(final Color color) {
        this.color = color;
    }
    
    public int getStyle() {
        return this.style;
    }
    
    public void setStyle(final int style) {
        this.style = style;
    }
}
