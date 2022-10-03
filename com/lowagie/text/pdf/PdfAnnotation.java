package com.lowagie.text.pdf;

import java.util.Map;
import com.lowagie.text.error_messages.MessageLocalization;
import java.awt.Color;
import java.io.IOException;
import com.lowagie.text.Rectangle;
import java.util.HashMap;

public class PdfAnnotation extends PdfDictionary
{
    public static final PdfName HIGHLIGHT_NONE;
    public static final PdfName HIGHLIGHT_INVERT;
    public static final PdfName HIGHLIGHT_OUTLINE;
    public static final PdfName HIGHLIGHT_PUSH;
    public static final PdfName HIGHLIGHT_TOGGLE;
    public static final int FLAGS_INVISIBLE = 1;
    public static final int FLAGS_HIDDEN = 2;
    public static final int FLAGS_PRINT = 4;
    public static final int FLAGS_NOZOOM = 8;
    public static final int FLAGS_NOROTATE = 16;
    public static final int FLAGS_NOVIEW = 32;
    public static final int FLAGS_READONLY = 64;
    public static final int FLAGS_LOCKED = 128;
    public static final int FLAGS_TOGGLENOVIEW = 256;
    public static final PdfName APPEARANCE_NORMAL;
    public static final PdfName APPEARANCE_ROLLOVER;
    public static final PdfName APPEARANCE_DOWN;
    public static final PdfName AA_ENTER;
    public static final PdfName AA_EXIT;
    public static final PdfName AA_DOWN;
    public static final PdfName AA_UP;
    public static final PdfName AA_FOCUS;
    public static final PdfName AA_BLUR;
    public static final PdfName AA_JS_KEY;
    public static final PdfName AA_JS_FORMAT;
    public static final PdfName AA_JS_CHANGE;
    public static final PdfName AA_JS_OTHER_CHANGE;
    public static final int MARKUP_HIGHLIGHT = 0;
    public static final int MARKUP_UNDERLINE = 1;
    public static final int MARKUP_STRIKEOUT = 2;
    public static final int MARKUP_SQUIGGLY = 3;
    protected PdfWriter writer;
    protected PdfIndirectReference reference;
    protected HashMap templates;
    protected boolean form;
    protected boolean annotation;
    protected boolean used;
    private int placeInPage;
    
    public PdfAnnotation(final PdfWriter writer, final Rectangle rect) {
        this.form = false;
        this.annotation = true;
        this.used = false;
        this.placeInPage = -1;
        this.writer = writer;
        if (rect != null) {
            this.put(PdfName.RECT, new PdfRectangle(rect));
        }
    }
    
    public PdfAnnotation(final PdfWriter writer, final float llx, final float lly, final float urx, final float ury, final PdfString title, final PdfString content) {
        this.form = false;
        this.annotation = true;
        this.used = false;
        this.placeInPage = -1;
        this.writer = writer;
        this.put(PdfName.SUBTYPE, PdfName.TEXT);
        this.put(PdfName.T, title);
        this.put(PdfName.RECT, new PdfRectangle(llx, lly, urx, ury));
        this.put(PdfName.CONTENTS, content);
    }
    
    public PdfAnnotation(final PdfWriter writer, final float llx, final float lly, final float urx, final float ury, final PdfAction action) {
        this.form = false;
        this.annotation = true;
        this.used = false;
        this.placeInPage = -1;
        this.writer = writer;
        this.put(PdfName.SUBTYPE, PdfName.LINK);
        this.put(PdfName.RECT, new PdfRectangle(llx, lly, urx, ury));
        this.put(PdfName.A, action);
        this.put(PdfName.BORDER, new PdfBorderArray(0.0f, 0.0f, 0.0f));
        this.put(PdfName.C, new PdfColor(0, 0, 255));
    }
    
    public static PdfAnnotation createScreen(final PdfWriter writer, final Rectangle rect, final String clipTitle, final PdfFileSpecification fs, final String mimeType, final boolean playOnDisplay) throws IOException {
        final PdfAnnotation ann = new PdfAnnotation(writer, rect);
        ann.put(PdfName.SUBTYPE, PdfName.SCREEN);
        ann.put(PdfName.F, new PdfNumber(4));
        ann.put(PdfName.TYPE, PdfName.ANNOT);
        ann.setPage();
        final PdfIndirectReference ref = ann.getIndirectReference();
        final PdfAction action = PdfAction.rendition(clipTitle, fs, mimeType, ref);
        final PdfIndirectReference actionRef = writer.addToBody(action).getIndirectReference();
        if (playOnDisplay) {
            final PdfDictionary aa = new PdfDictionary();
            aa.put(new PdfName("PV"), actionRef);
            ann.put(PdfName.AA, aa);
        }
        ann.put(PdfName.A, actionRef);
        return ann;
    }
    
    public PdfIndirectReference getIndirectReference() {
        if (this.reference == null) {
            this.reference = this.writer.getPdfIndirectReference();
        }
        return this.reference;
    }
    
    public static PdfAnnotation createText(final PdfWriter writer, final Rectangle rect, final String title, final String contents, final boolean open, final String icon) {
        final PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.TEXT);
        if (title != null) {
            annot.put(PdfName.T, new PdfString(title, "UnicodeBig"));
        }
        if (contents != null) {
            annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        }
        if (open) {
            annot.put(PdfName.OPEN, PdfBoolean.PDFTRUE);
        }
        if (icon != null) {
            annot.put(PdfName.NAME, new PdfName(icon));
        }
        return annot;
    }
    
    protected static PdfAnnotation createLink(final PdfWriter writer, final Rectangle rect, final PdfName highlight) {
        final PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.LINK);
        if (!highlight.equals(PdfAnnotation.HIGHLIGHT_INVERT)) {
            annot.put(PdfName.H, highlight);
        }
        return annot;
    }
    
    public static PdfAnnotation createLink(final PdfWriter writer, final Rectangle rect, final PdfName highlight, final PdfAction action) {
        final PdfAnnotation annot = createLink(writer, rect, highlight);
        annot.putEx(PdfName.A, action);
        return annot;
    }
    
    public static PdfAnnotation createLink(final PdfWriter writer, final Rectangle rect, final PdfName highlight, final String namedDestination) {
        final PdfAnnotation annot = createLink(writer, rect, highlight);
        annot.put(PdfName.DEST, new PdfString(namedDestination));
        return annot;
    }
    
    public static PdfAnnotation createLink(final PdfWriter writer, final Rectangle rect, final PdfName highlight, final int page, final PdfDestination dest) {
        final PdfAnnotation annot = createLink(writer, rect, highlight);
        final PdfIndirectReference ref = writer.getPageReference(page);
        dest.addPage(ref);
        annot.put(PdfName.DEST, dest);
        return annot;
    }
    
    public static PdfAnnotation createFreeText(final PdfWriter writer, final Rectangle rect, final String contents, final PdfContentByte defaultAppearance) {
        final PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.FREETEXT);
        annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        annot.setDefaultAppearanceString(defaultAppearance);
        return annot;
    }
    
    public static PdfAnnotation createLine(final PdfWriter writer, final Rectangle rect, final String contents, final float x1, final float y1, final float x2, final float y2) {
        final PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.LINE);
        annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        final PdfArray array = new PdfArray(new PdfNumber(x1));
        array.add(new PdfNumber(y1));
        array.add(new PdfNumber(x2));
        array.add(new PdfNumber(y2));
        annot.put(PdfName.L, array);
        return annot;
    }
    
    public static PdfAnnotation createSquareCircle(final PdfWriter writer, final Rectangle rect, final String contents, final boolean square) {
        final PdfAnnotation annot = new PdfAnnotation(writer, rect);
        if (square) {
            annot.put(PdfName.SUBTYPE, PdfName.SQUARE);
        }
        else {
            annot.put(PdfName.SUBTYPE, PdfName.CIRCLE);
        }
        annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        return annot;
    }
    
    public static PdfAnnotation createMarkup(final PdfWriter writer, final Rectangle rect, final String contents, final int type, final float[] quadPoints) {
        final PdfAnnotation annot = new PdfAnnotation(writer, rect);
        PdfName name = PdfName.HIGHLIGHT;
        switch (type) {
            case 1: {
                name = PdfName.UNDERLINE;
                break;
            }
            case 2: {
                name = PdfName.STRIKEOUT;
                break;
            }
            case 3: {
                name = PdfName.SQUIGGLY;
                break;
            }
        }
        annot.put(PdfName.SUBTYPE, name);
        annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        final PdfArray array = new PdfArray();
        for (int k = 0; k < quadPoints.length; ++k) {
            array.add(new PdfNumber(quadPoints[k]));
        }
        annot.put(PdfName.QUADPOINTS, array);
        return annot;
    }
    
    public static PdfAnnotation createStamp(final PdfWriter writer, final Rectangle rect, final String contents, final String name) {
        final PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.STAMP);
        annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        annot.put(PdfName.NAME, new PdfName(name));
        return annot;
    }
    
    public static PdfAnnotation createInk(final PdfWriter writer, final Rectangle rect, final String contents, final float[][] inkList) {
        final PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.INK);
        annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        final PdfArray outer = new PdfArray();
        for (int k = 0; k < inkList.length; ++k) {
            final PdfArray inner = new PdfArray();
            final float[] deep = inkList[k];
            for (int j = 0; j < deep.length; ++j) {
                inner.add(new PdfNumber(deep[j]));
            }
            outer.add(inner);
        }
        annot.put(PdfName.INKLIST, outer);
        return annot;
    }
    
    public static PdfAnnotation createFileAttachment(final PdfWriter writer, final Rectangle rect, final String contents, final byte[] fileStore, final String file, final String fileDisplay) throws IOException {
        return createFileAttachment(writer, rect, contents, PdfFileSpecification.fileEmbedded(writer, file, fileDisplay, fileStore));
    }
    
    public static PdfAnnotation createFileAttachment(final PdfWriter writer, final Rectangle rect, final String contents, final PdfFileSpecification fs) throws IOException {
        final PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.FILEATTACHMENT);
        if (contents != null) {
            annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        }
        annot.put(PdfName.FS, fs.getReference());
        return annot;
    }
    
    public static PdfAnnotation createPopup(final PdfWriter writer, final Rectangle rect, final String contents, final boolean open) {
        final PdfAnnotation annot = new PdfAnnotation(writer, rect);
        annot.put(PdfName.SUBTYPE, PdfName.POPUP);
        if (contents != null) {
            annot.put(PdfName.CONTENTS, new PdfString(contents, "UnicodeBig"));
        }
        if (open) {
            annot.put(PdfName.OPEN, PdfBoolean.PDFTRUE);
        }
        return annot;
    }
    
    public void setDefaultAppearanceString(final PdfContentByte cb) {
        final byte[] b = cb.getInternalBuffer().toByteArray();
        for (int len = b.length, k = 0; k < len; ++k) {
            if (b[k] == 10) {
                b[k] = 32;
            }
        }
        this.put(PdfName.DA, new PdfString(b));
    }
    
    public void setFlags(final int flags) {
        if (flags == 0) {
            this.remove(PdfName.F);
        }
        else {
            this.put(PdfName.F, new PdfNumber(flags));
        }
    }
    
    public void setBorder(final PdfBorderArray border) {
        this.put(PdfName.BORDER, border);
    }
    
    public void setBorderStyle(final PdfBorderDictionary border) {
        this.put(PdfName.BS, border);
    }
    
    public void setHighlighting(final PdfName highlight) {
        if (highlight.equals(PdfAnnotation.HIGHLIGHT_INVERT)) {
            this.remove(PdfName.H);
        }
        else {
            this.put(PdfName.H, highlight);
        }
    }
    
    public void setAppearance(final PdfName ap, final PdfTemplate template) {
        PdfDictionary dic = (PdfDictionary)this.get(PdfName.AP);
        if (dic == null) {
            dic = new PdfDictionary();
        }
        dic.put(ap, template.getIndirectReference());
        this.put(PdfName.AP, dic);
        if (!this.form) {
            return;
        }
        if (this.templates == null) {
            this.templates = new HashMap();
        }
        this.templates.put(template, null);
    }
    
    public void setAppearance(final PdfName ap, final String state, final PdfTemplate template) {
        PdfDictionary dicAp = (PdfDictionary)this.get(PdfName.AP);
        if (dicAp == null) {
            dicAp = new PdfDictionary();
        }
        final PdfObject obj = dicAp.get(ap);
        PdfDictionary dic;
        if (obj != null && obj.isDictionary()) {
            dic = (PdfDictionary)obj;
        }
        else {
            dic = new PdfDictionary();
        }
        dic.put(new PdfName(state), template.getIndirectReference());
        dicAp.put(ap, dic);
        this.put(PdfName.AP, dicAp);
        if (!this.form) {
            return;
        }
        if (this.templates == null) {
            this.templates = new HashMap();
        }
        this.templates.put(template, null);
    }
    
    public void setAppearanceState(final String state) {
        if (state == null) {
            this.remove(PdfName.AS);
            return;
        }
        this.put(PdfName.AS, new PdfName(state));
    }
    
    public void setColor(final Color color) {
        this.put(PdfName.C, new PdfColor(color));
    }
    
    public void setTitle(final String title) {
        if (title == null) {
            this.remove(PdfName.T);
            return;
        }
        this.put(PdfName.T, new PdfString(title, "UnicodeBig"));
    }
    
    public void setPopup(final PdfAnnotation popup) {
        this.put(PdfName.POPUP, popup.getIndirectReference());
        popup.put(PdfName.PARENT, this.getIndirectReference());
    }
    
    public void setAction(final PdfAction action) {
        this.put(PdfName.A, action);
    }
    
    public void setAdditionalActions(final PdfName key, final PdfAction action) {
        final PdfObject obj = this.get(PdfName.AA);
        PdfDictionary dic;
        if (obj != null && obj.isDictionary()) {
            dic = (PdfDictionary)obj;
        }
        else {
            dic = new PdfDictionary();
        }
        dic.put(key, action);
        this.put(PdfName.AA, dic);
    }
    
    public boolean isUsed() {
        return this.used;
    }
    
    public void setUsed() {
        this.used = true;
    }
    
    public HashMap getTemplates() {
        return this.templates;
    }
    
    public boolean isForm() {
        return this.form;
    }
    
    public boolean isAnnotation() {
        return this.annotation;
    }
    
    public void setPage(final int page) {
        this.put(PdfName.P, this.writer.getPageReference(page));
    }
    
    public void setPage() {
        this.put(PdfName.P, this.writer.getCurrentPage());
    }
    
    public int getPlaceInPage() {
        return this.placeInPage;
    }
    
    public void setPlaceInPage(final int placeInPage) {
        this.placeInPage = placeInPage;
    }
    
    public void setRotate(final int v) {
        this.put(PdfName.ROTATE, new PdfNumber(v));
    }
    
    PdfDictionary getMK() {
        PdfDictionary mk = (PdfDictionary)this.get(PdfName.MK);
        if (mk == null) {
            mk = new PdfDictionary();
            this.put(PdfName.MK, mk);
        }
        return mk;
    }
    
    public void setMKRotation(final int rotation) {
        this.getMK().put(PdfName.R, new PdfNumber(rotation));
    }
    
    public static PdfArray getMKColor(final Color color) {
        final PdfArray array = new PdfArray();
        final int type = ExtendedColor.getType(color);
        switch (type) {
            case 1: {
                array.add(new PdfNumber(((GrayColor)color).getGray()));
                break;
            }
            case 2: {
                final CMYKColor cmyk = (CMYKColor)color;
                array.add(new PdfNumber(cmyk.getCyan()));
                array.add(new PdfNumber(cmyk.getMagenta()));
                array.add(new PdfNumber(cmyk.getYellow()));
                array.add(new PdfNumber(cmyk.getBlack()));
                break;
            }
            case 3:
            case 4:
            case 5: {
                throw new RuntimeException(MessageLocalization.getComposedMessage("separations.patterns.and.shadings.are.not.allowed.in.mk.dictionary"));
            }
            default: {
                array.add(new PdfNumber(color.getRed() / 255.0f));
                array.add(new PdfNumber(color.getGreen() / 255.0f));
                array.add(new PdfNumber(color.getBlue() / 255.0f));
                break;
            }
        }
        return array;
    }
    
    public void setMKBorderColor(final Color color) {
        if (color == null) {
            this.getMK().remove(PdfName.BC);
        }
        else {
            this.getMK().put(PdfName.BC, getMKColor(color));
        }
    }
    
    public void setMKBackgroundColor(final Color color) {
        if (color == null) {
            this.getMK().remove(PdfName.BG);
        }
        else {
            this.getMK().put(PdfName.BG, getMKColor(color));
        }
    }
    
    public void setMKNormalCaption(final String caption) {
        this.getMK().put(PdfName.CA, new PdfString(caption, "UnicodeBig"));
    }
    
    public void setMKRolloverCaption(final String caption) {
        this.getMK().put(PdfName.RC, new PdfString(caption, "UnicodeBig"));
    }
    
    public void setMKAlternateCaption(final String caption) {
        this.getMK().put(PdfName.AC, new PdfString(caption, "UnicodeBig"));
    }
    
    public void setMKNormalIcon(final PdfTemplate template) {
        this.getMK().put(PdfName.I, template.getIndirectReference());
    }
    
    public void setMKRolloverIcon(final PdfTemplate template) {
        this.getMK().put(PdfName.RI, template.getIndirectReference());
    }
    
    public void setMKAlternateIcon(final PdfTemplate template) {
        this.getMK().put(PdfName.IX, template.getIndirectReference());
    }
    
    public void setMKIconFit(final PdfName scale, final PdfName scalingType, final float leftoverLeft, final float leftoverBottom, final boolean fitInBounds) {
        final PdfDictionary dic = new PdfDictionary();
        if (!scale.equals(PdfName.A)) {
            dic.put(PdfName.SW, scale);
        }
        if (!scalingType.equals(PdfName.P)) {
            dic.put(PdfName.S, scalingType);
        }
        if (leftoverLeft != 0.5f || leftoverBottom != 0.5f) {
            final PdfArray array = new PdfArray(new PdfNumber(leftoverLeft));
            array.add(new PdfNumber(leftoverBottom));
            dic.put(PdfName.A, array);
        }
        if (fitInBounds) {
            dic.put(PdfName.FB, PdfBoolean.PDFTRUE);
        }
        this.getMK().put(PdfName.IF, dic);
    }
    
    public void setMKTextPosition(final int tp) {
        this.getMK().put(PdfName.TP, new PdfNumber(tp));
    }
    
    public void setLayer(final PdfOCG layer) {
        this.put(PdfName.OC, layer.getRef());
    }
    
    public void setName(final String name) {
        this.put(PdfName.NM, new PdfString(name));
    }
    
    static {
        HIGHLIGHT_NONE = PdfName.N;
        HIGHLIGHT_INVERT = PdfName.I;
        HIGHLIGHT_OUTLINE = PdfName.O;
        HIGHLIGHT_PUSH = PdfName.P;
        HIGHLIGHT_TOGGLE = PdfName.T;
        APPEARANCE_NORMAL = PdfName.N;
        APPEARANCE_ROLLOVER = PdfName.R;
        APPEARANCE_DOWN = PdfName.D;
        AA_ENTER = PdfName.E;
        AA_EXIT = PdfName.X;
        AA_DOWN = PdfName.D;
        AA_UP = PdfName.U;
        AA_FOCUS = PdfName.FO;
        AA_BLUR = PdfName.BL;
        AA_JS_KEY = PdfName.K;
        AA_JS_FORMAT = PdfName.F;
        AA_JS_CHANGE = PdfName.V;
        AA_JS_OTHER_CHANGE = PdfName.C;
    }
    
    public static class PdfImportedLink
    {
        float llx;
        float lly;
        float urx;
        float ury;
        HashMap parameters;
        PdfArray destination;
        int newPage;
        
        PdfImportedLink(final PdfDictionary annotation) {
            this.parameters = new HashMap();
            this.destination = null;
            this.newPage = 0;
            this.parameters.putAll(annotation.hashMap);
            try {
                this.destination = this.parameters.remove(PdfName.DEST);
            }
            catch (final ClassCastException ex) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("you.have.to.consolidate.the.named.destinations.of.your.reader"));
            }
            if (this.destination != null) {
                this.destination = new PdfArray(this.destination);
            }
            final PdfArray rc = this.parameters.remove(PdfName.RECT);
            this.llx = rc.getAsNumber(0).floatValue();
            this.lly = rc.getAsNumber(1).floatValue();
            this.urx = rc.getAsNumber(2).floatValue();
            this.ury = rc.getAsNumber(3).floatValue();
        }
        
        public boolean isInternal() {
            return this.destination != null;
        }
        
        public int getDestinationPage() {
            if (!this.isInternal()) {
                return 0;
            }
            final PdfIndirectReference ref = this.destination.getAsIndirectObject(0);
            final PRIndirectReference pr = (PRIndirectReference)ref;
            final PdfReader r = pr.getReader();
            for (int i = 1; i <= r.getNumberOfPages(); ++i) {
                final PRIndirectReference pp = r.getPageOrigRef(i);
                if (pp.getGeneration() == pr.getGeneration() && pp.getNumber() == pr.getNumber()) {
                    return i;
                }
            }
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("page.not.found"));
        }
        
        public void setDestinationPage(final int newPage) {
            if (!this.isInternal()) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("cannot.change.destination.of.external.link"));
            }
            this.newPage = newPage;
        }
        
        public void transformDestination(final float a, final float b, final float c, final float d, final float e, final float f) {
            if (!this.isInternal()) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("cannot.change.destination.of.external.link"));
            }
            if (this.destination.getAsName(1).equals(PdfName.XYZ)) {
                final float x = this.destination.getAsNumber(2).floatValue();
                final float y = this.destination.getAsNumber(3).floatValue();
                final float xx = x * a + y * c + e;
                final float yy = x * b + y * d + f;
                this.destination.set(2, new PdfNumber(xx));
                this.destination.set(3, new PdfNumber(yy));
            }
        }
        
        public void transformRect(final float a, final float b, final float c, final float d, final float e, final float f) {
            float x = this.llx * a + this.lly * c + e;
            float y = this.llx * b + this.lly * d + f;
            this.llx = x;
            this.lly = y;
            x = this.urx * a + this.ury * c + e;
            y = this.urx * b + this.ury * d + f;
            this.urx = x;
            this.ury = y;
        }
        
        public PdfAnnotation createAnnotation(final PdfWriter writer) {
            final PdfAnnotation annotation = new PdfAnnotation(writer, new Rectangle(this.llx, this.lly, this.urx, this.ury));
            if (this.newPage != 0) {
                final PdfIndirectReference ref = writer.getPageReference(this.newPage);
                this.destination.set(0, ref);
            }
            if (this.destination != null) {
                annotation.put(PdfName.DEST, this.destination);
            }
            annotation.hashMap.putAll(this.parameters);
            return annotation;
        }
        
        @Override
        public String toString() {
            final StringBuffer buf = new StringBuffer("Imported link: location [");
            buf.append(this.llx);
            buf.append(' ');
            buf.append(this.lly);
            buf.append(' ');
            buf.append(this.urx);
            buf.append(' ');
            buf.append(this.ury);
            buf.append("] destination ");
            buf.append(this.destination);
            buf.append(" parameters ");
            buf.append(this.parameters);
            return buf.toString();
        }
    }
}
