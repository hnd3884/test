package com.lowagie.text.pdf;

import com.lowagie.text.pdf.draw.DrawInterface;
import com.lowagie.text.ListItem;
import java.util.Stack;
import com.lowagie.text.List;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.SimpleTable;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Image;
import com.lowagie.text.Element;
import java.util.Iterator;
import com.lowagie.text.Chunk;
import java.util.Collection;
import com.lowagie.text.Phrase;
import java.util.LinkedList;
import java.util.ArrayList;

public class ColumnText
{
    public static final int AR_NOVOWEL = 1;
    public static final int AR_COMPOSEDTASHKEEL = 4;
    public static final int AR_LIG = 8;
    public static final int DIGITS_EN2AN = 32;
    public static final int DIGITS_AN2EN = 64;
    public static final int DIGITS_EN2AN_INIT_LR = 96;
    public static final int DIGITS_EN2AN_INIT_AL = 128;
    public static final int DIGIT_TYPE_AN = 0;
    public static final int DIGIT_TYPE_AN_EXTENDED = 256;
    protected int runDirection;
    public static final float GLOBAL_SPACE_CHAR_RATIO = 0.0f;
    public static final int START_COLUMN = 0;
    public static final int NO_MORE_TEXT = 1;
    public static final int NO_MORE_COLUMN = 2;
    protected static final int LINE_STATUS_OK = 0;
    protected static final int LINE_STATUS_OFFLIMITS = 1;
    protected static final int LINE_STATUS_NOLINE = 2;
    protected float maxY;
    protected float minY;
    protected float leftX;
    protected float rightX;
    protected int alignment;
    protected ArrayList leftWall;
    protected ArrayList rightWall;
    protected BidiLine bidiLine;
    protected float yLine;
    protected float currentLeading;
    protected float fixedLeading;
    protected float multipliedLeading;
    protected PdfContentByte canvas;
    protected PdfContentByte[] canvases;
    protected int lineStatus;
    protected float indent;
    protected float followingIndent;
    protected float rightIndent;
    protected float extraParagraphSpace;
    protected float rectangularWidth;
    protected boolean rectangularMode;
    private float spaceCharRatio;
    private boolean lastWasNewline;
    private int linesWritten;
    private float firstLineY;
    private boolean firstLineYDone;
    private int arabicOptions;
    protected float descender;
    protected boolean composite;
    protected ColumnText compositeColumn;
    protected LinkedList compositeElements;
    protected int listIdx;
    private boolean splittedRow;
    protected Phrase waitPhrase;
    private boolean useAscender;
    private float filledWidth;
    private boolean adjustFirstLine;
    
    public ColumnText(final PdfContentByte canvas) {
        this.runDirection = 0;
        this.alignment = 0;
        this.currentLeading = 16.0f;
        this.fixedLeading = 16.0f;
        this.multipliedLeading = 0.0f;
        this.indent = 0.0f;
        this.followingIndent = 0.0f;
        this.rightIndent = 0.0f;
        this.extraParagraphSpace = 0.0f;
        this.rectangularWidth = -1.0f;
        this.rectangularMode = false;
        this.spaceCharRatio = 0.0f;
        this.lastWasNewline = true;
        this.firstLineYDone = false;
        this.arabicOptions = 0;
        this.composite = false;
        this.listIdx = 0;
        this.useAscender = false;
        this.adjustFirstLine = true;
        this.canvas = canvas;
    }
    
    public static ColumnText duplicate(final ColumnText org) {
        final ColumnText ct = new ColumnText(null);
        ct.setACopy(org);
        return ct;
    }
    
    public ColumnText setACopy(final ColumnText org) {
        this.setSimpleVars(org);
        if (org.bidiLine != null) {
            this.bidiLine = new BidiLine(org.bidiLine);
        }
        return this;
    }
    
    protected void setSimpleVars(final ColumnText org) {
        this.maxY = org.maxY;
        this.minY = org.minY;
        this.alignment = org.alignment;
        this.leftWall = null;
        if (org.leftWall != null) {
            this.leftWall = new ArrayList(org.leftWall);
        }
        this.rightWall = null;
        if (org.rightWall != null) {
            this.rightWall = new ArrayList(org.rightWall);
        }
        this.yLine = org.yLine;
        this.currentLeading = org.currentLeading;
        this.fixedLeading = org.fixedLeading;
        this.multipliedLeading = org.multipliedLeading;
        this.canvas = org.canvas;
        this.canvases = org.canvases;
        this.lineStatus = org.lineStatus;
        this.indent = org.indent;
        this.followingIndent = org.followingIndent;
        this.rightIndent = org.rightIndent;
        this.extraParagraphSpace = org.extraParagraphSpace;
        this.rectangularWidth = org.rectangularWidth;
        this.rectangularMode = org.rectangularMode;
        this.spaceCharRatio = org.spaceCharRatio;
        this.lastWasNewline = org.lastWasNewline;
        this.linesWritten = org.linesWritten;
        this.arabicOptions = org.arabicOptions;
        this.runDirection = org.runDirection;
        this.descender = org.descender;
        this.composite = org.composite;
        this.splittedRow = org.splittedRow;
        if (org.composite) {
            this.compositeElements = new LinkedList(org.compositeElements);
            if (this.splittedRow) {
                final PdfPTable table = this.compositeElements.getFirst();
                this.compositeElements.set(0, new PdfPTable(table));
            }
            if (org.compositeColumn != null) {
                this.compositeColumn = duplicate(org.compositeColumn);
            }
        }
        this.listIdx = org.listIdx;
        this.firstLineY = org.firstLineY;
        this.leftX = org.leftX;
        this.rightX = org.rightX;
        this.firstLineYDone = org.firstLineYDone;
        this.waitPhrase = org.waitPhrase;
        this.useAscender = org.useAscender;
        this.filledWidth = org.filledWidth;
        this.adjustFirstLine = org.adjustFirstLine;
    }
    
    private void addWaitingPhrase() {
        if (this.bidiLine == null && this.waitPhrase != null) {
            this.bidiLine = new BidiLine();
            final Iterator j = this.waitPhrase.getChunks().iterator();
            while (j.hasNext()) {
                this.bidiLine.addChunk(new PdfChunk(j.next(), null));
            }
            this.waitPhrase = null;
        }
    }
    
    public void addText(final Phrase phrase) {
        if (phrase == null || this.composite) {
            return;
        }
        this.addWaitingPhrase();
        if (this.bidiLine == null) {
            this.waitPhrase = phrase;
            return;
        }
        final Iterator j = phrase.getChunks().iterator();
        while (j.hasNext()) {
            this.bidiLine.addChunk(new PdfChunk(j.next(), null));
        }
    }
    
    public void setText(final Phrase phrase) {
        this.bidiLine = null;
        this.composite = false;
        this.compositeColumn = null;
        this.compositeElements = null;
        this.listIdx = 0;
        this.splittedRow = false;
        this.waitPhrase = phrase;
    }
    
    public void addText(final Chunk chunk) {
        if (chunk == null || this.composite) {
            return;
        }
        this.addText(new Phrase(chunk));
    }
    
    public void addElement(Element element) {
        if (element == null) {
            return;
        }
        if (element instanceof Image) {
            final Image img = (Image)element;
            final PdfPTable t = new PdfPTable(1);
            final float w = img.getWidthPercentage();
            if (w == 0.0f) {
                t.setTotalWidth(img.getScaledWidth());
                t.setLockedWidth(true);
            }
            else {
                t.setWidthPercentage(w);
            }
            t.setSpacingAfter(img.getSpacingAfter());
            t.setSpacingBefore(img.getSpacingBefore());
            switch (img.getAlignment()) {
                case 0: {
                    t.setHorizontalAlignment(0);
                    break;
                }
                case 2: {
                    t.setHorizontalAlignment(2);
                    break;
                }
                default: {
                    t.setHorizontalAlignment(1);
                    break;
                }
            }
            final PdfPCell c = new PdfPCell(img, true);
            c.setPadding(0.0f);
            c.setBorder(img.getBorder());
            c.setBorderColor(img.getBorderColor());
            c.setBorderWidth(img.getBorderWidth());
            c.setBackgroundColor(img.getBackgroundColor());
            t.addCell(c);
            element = t;
        }
        if (element.type() == 10) {
            element = new Paragraph((Chunk)element);
        }
        else if (element.type() == 11) {
            element = new Paragraph((Phrase)element);
        }
        Label_0328: {
            if (element instanceof SimpleTable) {
                try {
                    element = ((SimpleTable)element).createPdfPTable();
                    break Label_0328;
                }
                catch (final DocumentException e) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("element.not.allowed"));
                }
            }
            if (element.type() != 12 && element.type() != 14 && element.type() != 23 && element.type() != 55) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("element.not.allowed"));
            }
        }
        if (!this.composite) {
            this.composite = true;
            this.compositeElements = new LinkedList();
            this.bidiLine = null;
            this.waitPhrase = null;
        }
        this.compositeElements.add(element);
    }
    
    protected ArrayList convertColumn(final float[] cLine) {
        if (cLine.length < 4) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("no.valid.column.line.found"));
        }
        final ArrayList cc = new ArrayList();
        for (int k = 0; k < cLine.length - 2; k += 2) {
            final float x1 = cLine[k];
            final float y1 = cLine[k + 1];
            final float x2 = cLine[k + 2];
            final float y2 = cLine[k + 3];
            if (y1 != y2) {
                final float a = (x1 - x2) / (y1 - y2);
                final float b = x1 - a * y1;
                final float[] r = { Math.min(y1, y2), Math.max(y1, y2), a, b };
                cc.add(r);
                this.maxY = Math.max(this.maxY, r[1]);
                this.minY = Math.min(this.minY, r[0]);
            }
        }
        if (cc.isEmpty()) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("no.valid.column.line.found"));
        }
        return cc;
    }
    
    protected float findLimitsPoint(final ArrayList wall) {
        this.lineStatus = 0;
        if (this.yLine < this.minY || this.yLine > this.maxY) {
            this.lineStatus = 1;
            return 0.0f;
        }
        for (int k = 0; k < wall.size(); ++k) {
            final float[] r = wall.get(k);
            if (this.yLine >= r[0] && this.yLine <= r[1]) {
                return r[2] * this.yLine + r[3];
            }
        }
        this.lineStatus = 2;
        return 0.0f;
    }
    
    protected float[] findLimitsOneLine() {
        final float x1 = this.findLimitsPoint(this.leftWall);
        if (this.lineStatus == 1 || this.lineStatus == 2) {
            return null;
        }
        final float x2 = this.findLimitsPoint(this.rightWall);
        if (this.lineStatus == 2) {
            return null;
        }
        return new float[] { x1, x2 };
    }
    
    protected float[] findLimitsTwoLines() {
        boolean repeat = false;
        while (!repeat || this.currentLeading != 0.0f) {
            repeat = true;
            final float[] x1 = this.findLimitsOneLine();
            if (this.lineStatus == 1) {
                return null;
            }
            this.yLine -= this.currentLeading;
            if (this.lineStatus == 2) {
                continue;
            }
            final float[] x2 = this.findLimitsOneLine();
            if (this.lineStatus == 1) {
                return null;
            }
            if (this.lineStatus == 2) {
                this.yLine -= this.currentLeading;
            }
            else {
                if (x1[0] >= x2[1]) {
                    continue;
                }
                if (x2[0] >= x1[1]) {
                    continue;
                }
                return new float[] { x1[0], x1[1], x2[0], x2[1] };
            }
        }
        return null;
    }
    
    public void setColumns(final float[] leftLine, final float[] rightLine) {
        this.maxY = -1.0E21f;
        this.minY = 1.0E21f;
        this.setYLine(Math.max(leftLine[1], leftLine[leftLine.length - 1]));
        this.rightWall = this.convertColumn(rightLine);
        this.leftWall = this.convertColumn(leftLine);
        this.rectangularWidth = -1.0f;
        this.rectangularMode = false;
    }
    
    public void setSimpleColumn(final Phrase phrase, final float llx, final float lly, final float urx, final float ury, final float leading, final int alignment) {
        this.addText(phrase);
        this.setSimpleColumn(llx, lly, urx, ury, leading, alignment);
    }
    
    public void setSimpleColumn(final float llx, final float lly, final float urx, final float ury, final float leading, final int alignment) {
        this.setLeading(leading);
        this.alignment = alignment;
        this.setSimpleColumn(llx, lly, urx, ury);
    }
    
    public void setSimpleColumn(final float llx, final float lly, final float urx, final float ury) {
        this.leftX = Math.min(llx, urx);
        this.maxY = Math.max(lly, ury);
        this.minY = Math.min(lly, ury);
        this.rightX = Math.max(llx, urx);
        this.yLine = this.maxY;
        this.rectangularWidth = this.rightX - this.leftX;
        if (this.rectangularWidth < 0.0f) {
            this.rectangularWidth = 0.0f;
        }
        this.rectangularMode = true;
    }
    
    public void setLeading(final float leading) {
        this.fixedLeading = leading;
        this.multipliedLeading = 0.0f;
    }
    
    public void setLeading(final float fixedLeading, final float multipliedLeading) {
        this.fixedLeading = fixedLeading;
        this.multipliedLeading = multipliedLeading;
    }
    
    public float getLeading() {
        return this.fixedLeading;
    }
    
    public float getMultipliedLeading() {
        return this.multipliedLeading;
    }
    
    public void setYLine(final float yLine) {
        this.yLine = yLine;
    }
    
    public float getYLine() {
        return this.yLine;
    }
    
    public void setAlignment(final int alignment) {
        this.alignment = alignment;
    }
    
    public int getAlignment() {
        return this.alignment;
    }
    
    public void setIndent(final float indent) {
        this.indent = indent;
        this.lastWasNewline = true;
    }
    
    public float getIndent() {
        return this.indent;
    }
    
    public void setFollowingIndent(final float indent) {
        this.followingIndent = indent;
        this.lastWasNewline = true;
    }
    
    public float getFollowingIndent() {
        return this.followingIndent;
    }
    
    public void setRightIndent(final float indent) {
        this.rightIndent = indent;
        this.lastWasNewline = true;
    }
    
    public float getRightIndent() {
        return this.rightIndent;
    }
    
    public int go() throws DocumentException {
        return this.go(false);
    }
    
    public int go(final boolean simulate) throws DocumentException {
        if (this.composite) {
            return this.goComposite(simulate);
        }
        this.addWaitingPhrase();
        if (this.bidiLine == null) {
            return 1;
        }
        this.descender = 0.0f;
        this.linesWritten = 0;
        boolean dirty = false;
        float ratio = this.spaceCharRatio;
        final Object[] currentValues = new Object[2];
        PdfFont currentFont = null;
        final Float lastBaseFactor = new Float(0.0f);
        currentValues[1] = lastBaseFactor;
        PdfDocument pdf = null;
        PdfContentByte graphics = null;
        PdfContentByte text = null;
        this.firstLineY = Float.NaN;
        int localRunDirection = 1;
        if (this.runDirection != 0) {
            localRunDirection = this.runDirection;
        }
        if (this.canvas != null) {
            graphics = this.canvas;
            pdf = this.canvas.getPdfDocument();
            text = this.canvas.getDuplicate();
        }
        else if (!simulate) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("columntext.go.with.simulate.eq.eq.false.and.text.eq.eq.null"));
        }
        if (!simulate) {
            if (ratio == 0.0f) {
                ratio = text.getPdfWriter().getSpaceCharRatio();
            }
            else if (ratio < 0.001f) {
                ratio = 0.001f;
            }
        }
        float firstIndent = 0.0f;
        int status = 0;
        while (true) {
            firstIndent = (this.lastWasNewline ? this.indent : this.followingIndent);
            PdfLine line;
            float x1;
            if (this.rectangularMode) {
                if (this.rectangularWidth <= firstIndent + this.rightIndent) {
                    status = 2;
                    if (this.bidiLine.isEmpty()) {
                        status |= 0x1;
                        break;
                    }
                    break;
                }
                else {
                    if (this.bidiLine.isEmpty()) {
                        status = 1;
                        break;
                    }
                    line = this.bidiLine.processLine(this.leftX, this.rectangularWidth - firstIndent - this.rightIndent, this.alignment, localRunDirection, this.arabicOptions);
                    if (line == null) {
                        status = 1;
                        break;
                    }
                    final float[] maxSize = line.getMaxSize();
                    if (this.isUseAscender() && Float.isNaN(this.firstLineY)) {
                        this.currentLeading = line.getAscender();
                    }
                    else {
                        this.currentLeading = Math.max(this.fixedLeading + maxSize[0] * this.multipliedLeading, maxSize[1]);
                    }
                    if (this.yLine > this.maxY || this.yLine - this.currentLeading < this.minY) {
                        status = 2;
                        this.bidiLine.restore();
                        break;
                    }
                    this.yLine -= this.currentLeading;
                    if (!simulate && !dirty) {
                        text.beginText();
                        dirty = true;
                    }
                    if (Float.isNaN(this.firstLineY)) {
                        this.firstLineY = this.yLine;
                    }
                    this.updateFilledWidth(this.rectangularWidth - line.widthLeft());
                    x1 = this.leftX;
                }
            }
            else {
                final float yTemp = this.yLine;
                final float[] xx = this.findLimitsTwoLines();
                if (xx == null) {
                    status = 2;
                    if (this.bidiLine.isEmpty()) {
                        status |= 0x1;
                    }
                    this.yLine = yTemp;
                    break;
                }
                if (this.bidiLine.isEmpty()) {
                    status = 1;
                    this.yLine = yTemp;
                    break;
                }
                x1 = Math.max(xx[0], xx[2]);
                final float x2 = Math.min(xx[1], xx[3]);
                if (x2 - x1 <= firstIndent + this.rightIndent) {
                    continue;
                }
                if (!simulate && !dirty) {
                    text.beginText();
                    dirty = true;
                }
                line = this.bidiLine.processLine(x1, x2 - x1 - firstIndent - this.rightIndent, this.alignment, localRunDirection, this.arabicOptions);
                if (line == null) {
                    status = 1;
                    this.yLine = yTemp;
                    break;
                }
            }
            if (!simulate) {
                currentValues[0] = currentFont;
                text.setTextMatrix(x1 + (line.isRTL() ? this.rightIndent : firstIndent) + line.indentLeft(), this.yLine);
                pdf.writeLineToContent(line, text, graphics, currentValues, ratio);
                currentFont = (PdfFont)currentValues[0];
            }
            this.lastWasNewline = line.isNewlineSplit();
            this.yLine -= (line.isNewlineSplit() ? this.extraParagraphSpace : 0.0f);
            ++this.linesWritten;
            this.descender = line.getDescender();
        }
        if (dirty) {
            text.endText();
            this.canvas.add(text);
        }
        return status;
    }
    
    public float getExtraParagraphSpace() {
        return this.extraParagraphSpace;
    }
    
    public void setExtraParagraphSpace(final float extraParagraphSpace) {
        this.extraParagraphSpace = extraParagraphSpace;
    }
    
    public void clearChunks() {
        if (this.bidiLine != null) {
            this.bidiLine.clearChunks();
        }
    }
    
    public float getSpaceCharRatio() {
        return this.spaceCharRatio;
    }
    
    public void setSpaceCharRatio(final float spaceCharRatio) {
        this.spaceCharRatio = spaceCharRatio;
    }
    
    public void setRunDirection(final int runDirection) {
        if (runDirection < 0 || runDirection > 3) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.run.direction.1", runDirection));
        }
        this.runDirection = runDirection;
    }
    
    public int getRunDirection() {
        return this.runDirection;
    }
    
    public int getLinesWritten() {
        return this.linesWritten;
    }
    
    public int getArabicOptions() {
        return this.arabicOptions;
    }
    
    public void setArabicOptions(final int arabicOptions) {
        this.arabicOptions = arabicOptions;
    }
    
    public float getDescender() {
        return this.descender;
    }
    
    public static float getWidth(final Phrase phrase, final int runDirection, final int arabicOptions) {
        final ColumnText ct = new ColumnText(null);
        ct.addText(phrase);
        ct.addWaitingPhrase();
        final PdfLine line = ct.bidiLine.processLine(0.0f, 20000.0f, 0, runDirection, arabicOptions);
        if (line == null) {
            return 0.0f;
        }
        return 20000.0f - line.widthLeft();
    }
    
    public static float getWidth(final Phrase phrase) {
        return getWidth(phrase, 1, 0);
    }
    
    public static void showTextAligned(final PdfContentByte canvas, int alignment, final Phrase phrase, final float x, final float y, final float rotation, final int runDirection, final int arabicOptions) {
        if (alignment != 0 && alignment != 1 && alignment != 2) {
            alignment = 0;
        }
        canvas.saveState();
        final ColumnText ct = new ColumnText(canvas);
        float lly = -1.0f;
        float ury = 2.0f;
        float llx = 0.0f;
        float urx = 0.0f;
        switch (alignment) {
            case 0: {
                llx = 0.0f;
                urx = 20000.0f;
                break;
            }
            case 2: {
                llx = -20000.0f;
                urx = 0.0f;
                break;
            }
            default: {
                llx = -20000.0f;
                urx = 20000.0f;
                break;
            }
        }
        if (rotation == 0.0f) {
            llx += x;
            lly += y;
            urx += x;
            ury += y;
        }
        else {
            final double alpha = rotation * 3.141592653589793 / 180.0;
            final float cos = (float)Math.cos(alpha);
            final float sin = (float)Math.sin(alpha);
            canvas.concatCTM(cos, sin, -sin, cos, x, y);
        }
        ct.setSimpleColumn(phrase, llx, lly, urx, ury, 2.0f, alignment);
        if (runDirection == 3) {
            if (alignment == 0) {
                alignment = 2;
            }
            else if (alignment == 2) {
                alignment = 0;
            }
        }
        ct.setAlignment(alignment);
        ct.setArabicOptions(arabicOptions);
        ct.setRunDirection(runDirection);
        try {
            ct.go();
        }
        catch (final DocumentException e) {
            throw new ExceptionConverter(e);
        }
        canvas.restoreState();
    }
    
    public static void showTextAligned(final PdfContentByte canvas, final int alignment, final Phrase phrase, final float x, final float y, final float rotation) {
        showTextAligned(canvas, alignment, phrase, x, y, rotation, 1, 0);
    }
    
    protected int goComposite(final boolean simulate) throws DocumentException {
        if (!this.rectangularMode) {
            throw new DocumentException(MessageLocalization.getComposedMessage("irregular.columns.are.not.supported.in.composite.mode"));
        }
        this.linesWritten = 0;
        this.descender = 0.0f;
        boolean firstPass = this.adjustFirstLine;
    Label_0035:
        while (!this.compositeElements.isEmpty()) {
            final Element element = this.compositeElements.getFirst();
            if (element.type() == 12) {
                final Paragraph para = (Paragraph)element;
                int status = 0;
                for (int keep = 0; keep < 2; ++keep) {
                    final float lastY = this.yLine;
                    boolean createHere = false;
                    if (this.compositeColumn == null) {
                        (this.compositeColumn = new ColumnText(this.canvas)).setUseAscender(firstPass && this.useAscender);
                        this.compositeColumn.setAlignment(para.getAlignment());
                        this.compositeColumn.setIndent(para.getIndentationLeft() + para.getFirstLineIndent());
                        this.compositeColumn.setExtraParagraphSpace(para.getExtraParagraphSpace());
                        this.compositeColumn.setFollowingIndent(para.getIndentationLeft());
                        this.compositeColumn.setRightIndent(para.getIndentationRight());
                        this.compositeColumn.setLeading(para.getLeading(), para.getMultipliedLeading());
                        this.compositeColumn.setRunDirection(this.runDirection);
                        this.compositeColumn.setArabicOptions(this.arabicOptions);
                        this.compositeColumn.setSpaceCharRatio(this.spaceCharRatio);
                        this.compositeColumn.addText(para);
                        if (!firstPass) {
                            this.yLine -= para.getSpacingBefore();
                        }
                        createHere = true;
                    }
                    this.compositeColumn.leftX = this.leftX;
                    this.compositeColumn.rightX = this.rightX;
                    this.compositeColumn.yLine = this.yLine;
                    this.compositeColumn.rectangularWidth = this.rectangularWidth;
                    this.compositeColumn.rectangularMode = this.rectangularMode;
                    this.compositeColumn.minY = this.minY;
                    this.compositeColumn.maxY = this.maxY;
                    final boolean keepCandidate = para.getKeepTogether() && createHere && !firstPass;
                    status = this.compositeColumn.go(simulate || (keepCandidate && keep == 0));
                    this.updateFilledWidth(this.compositeColumn.filledWidth);
                    if ((status & 0x1) == 0x0 && keepCandidate) {
                        this.compositeColumn = null;
                        this.yLine = lastY;
                        return 2;
                    }
                    if (simulate) {
                        break;
                    }
                    if (!keepCandidate) {
                        break;
                    }
                    if (keep == 0) {
                        this.compositeColumn = null;
                        this.yLine = lastY;
                    }
                }
                firstPass = false;
                this.yLine = this.compositeColumn.yLine;
                this.linesWritten += this.compositeColumn.linesWritten;
                this.descender = this.compositeColumn.descender;
                if ((status & 0x1) != 0x0) {
                    this.compositeColumn = null;
                    this.compositeElements.removeFirst();
                    this.yLine -= para.getSpacingAfter();
                }
                if ((status & 0x2) != 0x0) {
                    return 2;
                }
                continue;
            }
            else if (element.type() == 14) {
                List list = (List)element;
                ArrayList items = list.getItems();
                ListItem item = null;
                float listIndentation = list.getIndentationLeft();
                int count = 0;
                final Stack stack = new Stack();
                for (int k = 0; k < items.size(); ++k) {
                    final Object obj = items.get(k);
                    if (obj instanceof ListItem) {
                        if (count == this.listIdx) {
                            item = (ListItem)obj;
                            break;
                        }
                        ++count;
                    }
                    else if (obj instanceof List) {
                        stack.push(new Object[] { list, new Integer(k), new Float(listIndentation) });
                        list = (List)obj;
                        items = list.getItems();
                        listIndentation += list.getIndentationLeft();
                        k = -1;
                        continue;
                    }
                    if (k == items.size() - 1 && !stack.isEmpty()) {
                        final Object[] objs = stack.pop();
                        list = (List)objs[0];
                        items = list.getItems();
                        k = (int)objs[1];
                        listIndentation = (float)objs[2];
                    }
                }
                int status2 = 0;
                for (int keep2 = 0; keep2 < 2; ++keep2) {
                    final float lastY2 = this.yLine;
                    boolean createHere2 = false;
                    if (this.compositeColumn == null) {
                        if (item == null) {
                            this.listIdx = 0;
                            this.compositeElements.removeFirst();
                            continue Label_0035;
                        }
                        (this.compositeColumn = new ColumnText(this.canvas)).setUseAscender(firstPass && this.useAscender);
                        this.compositeColumn.setAlignment(item.getAlignment());
                        this.compositeColumn.setIndent(item.getIndentationLeft() + listIndentation + item.getFirstLineIndent());
                        this.compositeColumn.setExtraParagraphSpace(item.getExtraParagraphSpace());
                        this.compositeColumn.setFollowingIndent(this.compositeColumn.getIndent());
                        this.compositeColumn.setRightIndent(item.getIndentationRight() + list.getIndentationRight());
                        this.compositeColumn.setLeading(item.getLeading(), item.getMultipliedLeading());
                        this.compositeColumn.setRunDirection(this.runDirection);
                        this.compositeColumn.setArabicOptions(this.arabicOptions);
                        this.compositeColumn.setSpaceCharRatio(this.spaceCharRatio);
                        this.compositeColumn.addText(item);
                        if (!firstPass) {
                            this.yLine -= item.getSpacingBefore();
                        }
                        createHere2 = true;
                    }
                    this.compositeColumn.leftX = this.leftX;
                    this.compositeColumn.rightX = this.rightX;
                    this.compositeColumn.yLine = this.yLine;
                    this.compositeColumn.rectangularWidth = this.rectangularWidth;
                    this.compositeColumn.rectangularMode = this.rectangularMode;
                    this.compositeColumn.minY = this.minY;
                    this.compositeColumn.maxY = this.maxY;
                    final boolean keepCandidate2 = item.getKeepTogether() && createHere2 && !firstPass;
                    status2 = this.compositeColumn.go(simulate || (keepCandidate2 && keep2 == 0));
                    this.updateFilledWidth(this.compositeColumn.filledWidth);
                    if ((status2 & 0x1) == 0x0 && keepCandidate2) {
                        this.compositeColumn = null;
                        this.yLine = lastY2;
                        return 2;
                    }
                    if (simulate) {
                        break;
                    }
                    if (!keepCandidate2) {
                        break;
                    }
                    if (keep2 == 0) {
                        this.compositeColumn = null;
                        this.yLine = lastY2;
                    }
                }
                firstPass = false;
                this.yLine = this.compositeColumn.yLine;
                this.linesWritten += this.compositeColumn.linesWritten;
                this.descender = this.compositeColumn.descender;
                if (!Float.isNaN(this.compositeColumn.firstLineY) && !this.compositeColumn.firstLineYDone) {
                    if (!simulate) {
                        showTextAligned(this.canvas, 0, new Phrase(item.getListSymbol()), this.compositeColumn.leftX + listIndentation, this.compositeColumn.firstLineY, 0.0f);
                    }
                    this.compositeColumn.firstLineYDone = true;
                }
                if ((status2 & 0x1) != 0x0) {
                    this.compositeColumn = null;
                    ++this.listIdx;
                    this.yLine -= item.getSpacingAfter();
                }
                if ((status2 & 0x2) != 0x0) {
                    return 2;
                }
                continue;
            }
            else if (element.type() == 23) {
                if (this.yLine < this.minY || this.yLine > this.maxY) {
                    return 2;
                }
                PdfPTable table = (PdfPTable)element;
                if (table.size() <= table.getHeaderRows()) {
                    this.compositeElements.removeFirst();
                }
                else {
                    float yTemp = this.yLine;
                    if (!firstPass && this.listIdx == 0) {
                        yTemp -= table.spacingBefore();
                    }
                    final float yLineWrite = yTemp;
                    if (yTemp < this.minY || yTemp > this.maxY) {
                        return 2;
                    }
                    this.currentLeading = 0.0f;
                    float x1 = this.leftX;
                    float tableWidth;
                    if (table.isLockedWidth()) {
                        tableWidth = table.getTotalWidth();
                        this.updateFilledWidth(tableWidth);
                    }
                    else {
                        tableWidth = this.rectangularWidth * table.getWidthPercentage() / 100.0f;
                        table.setTotalWidth(tableWidth);
                    }
                    final int headerRows = table.getHeaderRows();
                    int footerRows = table.getFooterRows();
                    if (footerRows > headerRows) {
                        footerRows = headerRows;
                    }
                    final int realHeaderRows = headerRows - footerRows;
                    final float headerHeight = table.getHeaderHeight();
                    final float footerHeight = table.getFooterHeight();
                    final boolean skipHeader = !firstPass && table.isSkipFirstHeader() && this.listIdx <= headerRows;
                    if (!skipHeader) {
                        yTemp -= headerHeight;
                        if (yTemp < this.minY || yTemp > this.maxY) {
                            if (firstPass) {
                                this.compositeElements.removeFirst();
                                continue;
                            }
                            return 2;
                        }
                    }
                    if (this.listIdx < headerRows) {
                        this.listIdx = headerRows;
                    }
                    if (!table.isComplete()) {
                        yTemp -= footerHeight;
                    }
                    int i;
                    for (i = this.listIdx; i < table.size(); ++i) {
                        final float rowHeight = table.getRowHeight(i);
                        if (yTemp - rowHeight < this.minY) {
                            break;
                        }
                        yTemp -= rowHeight;
                    }
                    if (!table.isComplete()) {
                        yTemp += footerHeight;
                    }
                    if (i < table.size()) {
                        if (table.isSplitRows() && (!table.isSplitLate() || (i == this.listIdx && firstPass))) {
                            if (!this.splittedRow) {
                                this.splittedRow = true;
                                table = new PdfPTable(table);
                                this.compositeElements.set(0, table);
                                final ArrayList rows = table.getRows();
                                for (int j = headerRows; j < this.listIdx; ++j) {
                                    rows.set(j, null);
                                }
                            }
                            final float h = yTemp - this.minY;
                            final PdfPRow newRow = table.getRow(i).splitRow(table, i, h);
                            if (newRow == null) {
                                if (i == this.listIdx) {
                                    return 2;
                                }
                            }
                            else {
                                yTemp = this.minY;
                                table.getRows().add(++i, newRow);
                            }
                        }
                        else {
                            if (!table.isSplitRows() && i == this.listIdx && firstPass) {
                                this.compositeElements.removeFirst();
                                this.splittedRow = false;
                                continue;
                            }
                            if (i == this.listIdx && !firstPass && (!table.isSplitRows() || table.isSplitLate()) && (table.getFooterRows() == 0 || table.isComplete())) {
                                return 2;
                            }
                        }
                    }
                    firstPass = false;
                    if (!simulate) {
                        switch (table.getHorizontalAlignment()) {
                            case 0: {
                                break;
                            }
                            case 2: {
                                x1 += this.rectangularWidth - tableWidth;
                                break;
                            }
                            default: {
                                x1 += (this.rectangularWidth - tableWidth) / 2.0f;
                                break;
                            }
                        }
                        final PdfPTable nt = PdfPTable.shallowCopy(table);
                        final ArrayList sub = nt.getRows();
                        if (!skipHeader && realHeaderRows > 0) {
                            sub.addAll(table.getRows(0, realHeaderRows));
                        }
                        else {
                            nt.setHeaderRows(footerRows);
                        }
                        sub.addAll(table.getRows(this.listIdx, i));
                        boolean showFooter = !table.isSkipLastFooter();
                        boolean newPageFollows = false;
                        if (i < table.size()) {
                            nt.setComplete(true);
                            showFooter = true;
                            newPageFollows = true;
                        }
                        for (int l = 0; l < footerRows && nt.isComplete() && showFooter; ++l) {
                            sub.add(table.getRow(l + realHeaderRows));
                        }
                        float rowHeight2 = 0.0f;
                        int index = sub.size() - 1;
                        if (showFooter) {
                            index -= footerRows;
                        }
                        final PdfPRow last = sub.get(index);
                        if (table.isExtendLastRow(newPageFollows)) {
                            rowHeight2 = last.getMaxHeights();
                            last.setMaxHeights(yTemp - this.minY + rowHeight2);
                            yTemp = this.minY;
                        }
                        if (this.canvases != null) {
                            nt.writeSelectedRows(0, -1, x1, yLineWrite, this.canvases);
                        }
                        else {
                            nt.writeSelectedRows(0, -1, x1, yLineWrite, this.canvas);
                        }
                        if (table.isExtendLastRow(newPageFollows)) {
                            last.setMaxHeights(rowHeight2);
                        }
                    }
                    else if (table.isExtendLastRow() && this.minY > -1.07374182E9f) {
                        yTemp = this.minY;
                    }
                    this.yLine = yTemp;
                    if (!skipHeader && !table.isComplete()) {
                        this.yLine += footerHeight;
                    }
                    if (i < table.size()) {
                        if (this.splittedRow) {
                            final ArrayList rows = table.getRows();
                            for (int j = this.listIdx; j < i; ++j) {
                                rows.set(j, null);
                            }
                        }
                        this.listIdx = i;
                        return 2;
                    }
                    this.yLine -= table.spacingAfter();
                    this.compositeElements.removeFirst();
                    this.splittedRow = false;
                    this.listIdx = 0;
                }
            }
            else if (element.type() == 55) {
                if (!simulate) {
                    final DrawInterface zh = (DrawInterface)element;
                    zh.draw(this.canvas, this.leftX, this.minY, this.rightX, this.maxY, this.yLine);
                }
                this.compositeElements.removeFirst();
            }
            else {
                this.compositeElements.removeFirst();
            }
        }
        return 1;
    }
    
    public PdfContentByte getCanvas() {
        return this.canvas;
    }
    
    public void setCanvas(final PdfContentByte canvas) {
        this.canvas = canvas;
        this.canvases = null;
        if (this.compositeColumn != null) {
            this.compositeColumn.setCanvas(canvas);
        }
    }
    
    public void setCanvases(final PdfContentByte[] canvases) {
        this.canvases = canvases;
        this.canvas = canvases[3];
        if (this.compositeColumn != null) {
            this.compositeColumn.setCanvases(canvases);
        }
    }
    
    public PdfContentByte[] getCanvases() {
        return this.canvases;
    }
    
    public boolean zeroHeightElement() {
        return this.composite && !this.compositeElements.isEmpty() && this.compositeElements.getFirst().type() == 55;
    }
    
    public boolean isUseAscender() {
        return this.useAscender;
    }
    
    public void setUseAscender(final boolean useAscender) {
        this.useAscender = useAscender;
    }
    
    public static boolean hasMoreText(final int status) {
        return (status & 0x1) == 0x0;
    }
    
    public float getFilledWidth() {
        return this.filledWidth;
    }
    
    public void setFilledWidth(final float filledWidth) {
        this.filledWidth = filledWidth;
    }
    
    public void updateFilledWidth(final float w) {
        if (w > this.filledWidth) {
            this.filledWidth = w;
        }
    }
    
    public boolean isAdjustFirstLine() {
        return this.adjustFirstLine;
    }
    
    public void setAdjustFirstLine(final boolean adjustFirstLine) {
        this.adjustFirstLine = adjustFirstLine;
    }
}
