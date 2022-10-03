package com.lowagie.text.pdf;

import java.util.Iterator;
import com.lowagie.text.Rectangle;
import java.util.ArrayList;

public class PdfFormField extends PdfAnnotation
{
    public static final int FF_READ_ONLY = 1;
    public static final int FF_REQUIRED = 2;
    public static final int FF_NO_EXPORT = 4;
    public static final int FF_NO_TOGGLE_TO_OFF = 16384;
    public static final int FF_RADIO = 32768;
    public static final int FF_PUSHBUTTON = 65536;
    public static final int FF_MULTILINE = 4096;
    public static final int FF_PASSWORD = 8192;
    public static final int FF_COMBO = 131072;
    public static final int FF_EDIT = 262144;
    public static final int FF_FILESELECT = 1048576;
    public static final int FF_MULTISELECT = 2097152;
    public static final int FF_DONOTSPELLCHECK = 4194304;
    public static final int FF_DONOTSCROLL = 8388608;
    public static final int FF_COMB = 16777216;
    public static final int FF_RADIOSINUNISON = 33554432;
    public static final int Q_LEFT = 0;
    public static final int Q_CENTER = 1;
    public static final int Q_RIGHT = 2;
    public static final int MK_NO_ICON = 0;
    public static final int MK_NO_CAPTION = 1;
    public static final int MK_CAPTION_BELOW = 2;
    public static final int MK_CAPTION_ABOVE = 3;
    public static final int MK_CAPTION_RIGHT = 4;
    public static final int MK_CAPTION_LEFT = 5;
    public static final int MK_CAPTION_OVERLAID = 6;
    public static final PdfName IF_SCALE_ALWAYS;
    public static final PdfName IF_SCALE_BIGGER;
    public static final PdfName IF_SCALE_SMALLER;
    public static final PdfName IF_SCALE_NEVER;
    public static final PdfName IF_SCALE_ANAMORPHIC;
    public static final PdfName IF_SCALE_PROPORTIONAL;
    public static final boolean MULTILINE = true;
    public static final boolean SINGLELINE = false;
    public static final boolean PLAINTEXT = false;
    public static final boolean PASSWORD = true;
    static PdfName[] mergeTarget;
    protected PdfFormField parent;
    protected ArrayList kids;
    
    public PdfFormField(final PdfWriter writer, final float llx, final float lly, final float urx, final float ury, final PdfAction action) {
        super(writer, llx, lly, urx, ury, action);
        this.put(PdfName.TYPE, PdfName.ANNOT);
        this.put(PdfName.SUBTYPE, PdfName.WIDGET);
        this.annotation = true;
    }
    
    protected PdfFormField(final PdfWriter writer) {
        super(writer, null);
        this.form = true;
        this.annotation = false;
    }
    
    public void setWidget(final Rectangle rect, final PdfName highlight) {
        this.put(PdfName.TYPE, PdfName.ANNOT);
        this.put(PdfName.SUBTYPE, PdfName.WIDGET);
        this.put(PdfName.RECT, new PdfRectangle(rect));
        this.annotation = true;
        if (highlight != null && !highlight.equals(PdfFormField.HIGHLIGHT_INVERT)) {
            this.put(PdfName.H, highlight);
        }
    }
    
    public static PdfFormField createEmpty(final PdfWriter writer) {
        final PdfFormField field = new PdfFormField(writer);
        return field;
    }
    
    public void setButton(final int flags) {
        this.put(PdfName.FT, PdfName.BTN);
        if (flags != 0) {
            this.put(PdfName.FF, new PdfNumber(flags));
        }
    }
    
    protected static PdfFormField createButton(final PdfWriter writer, final int flags) {
        final PdfFormField field = new PdfFormField(writer);
        field.setButton(flags);
        return field;
    }
    
    public static PdfFormField createPushButton(final PdfWriter writer) {
        return createButton(writer, 65536);
    }
    
    public static PdfFormField createCheckBox(final PdfWriter writer) {
        return createButton(writer, 0);
    }
    
    public static PdfFormField createRadioButton(final PdfWriter writer, final boolean noToggleToOff) {
        return createButton(writer, 32768 + (noToggleToOff ? 16384 : 0));
    }
    
    public static PdfFormField createTextField(final PdfWriter writer, final boolean multiline, final boolean password, final int maxLen) {
        final PdfFormField field = new PdfFormField(writer);
        field.put(PdfName.FT, PdfName.TX);
        int flags = multiline ? 4096 : 0;
        flags += (password ? 8192 : 0);
        field.put(PdfName.FF, new PdfNumber(flags));
        if (maxLen > 0) {
            field.put(PdfName.MAXLEN, new PdfNumber(maxLen));
        }
        return field;
    }
    
    protected static PdfFormField createChoice(final PdfWriter writer, final int flags, final PdfArray options, final int topIndex) {
        final PdfFormField field = new PdfFormField(writer);
        field.put(PdfName.FT, PdfName.CH);
        field.put(PdfName.FF, new PdfNumber(flags));
        field.put(PdfName.OPT, options);
        if (topIndex > 0) {
            field.put(PdfName.TI, new PdfNumber(topIndex));
        }
        return field;
    }
    
    public static PdfFormField createList(final PdfWriter writer, final String[] options, final int topIndex) {
        return createChoice(writer, 0, processOptions(options), topIndex);
    }
    
    public static PdfFormField createList(final PdfWriter writer, final String[][] options, final int topIndex) {
        return createChoice(writer, 0, processOptions(options), topIndex);
    }
    
    public static PdfFormField createCombo(final PdfWriter writer, final boolean edit, final String[] options, final int topIndex) {
        return createChoice(writer, 131072 + (edit ? 262144 : 0), processOptions(options), topIndex);
    }
    
    public static PdfFormField createCombo(final PdfWriter writer, final boolean edit, final String[][] options, final int topIndex) {
        return createChoice(writer, 131072 + (edit ? 262144 : 0), processOptions(options), topIndex);
    }
    
    protected static PdfArray processOptions(final String[] options) {
        final PdfArray array = new PdfArray();
        for (int k = 0; k < options.length; ++k) {
            array.add(new PdfString(options[k], "UnicodeBig"));
        }
        return array;
    }
    
    protected static PdfArray processOptions(final String[][] options) {
        final PdfArray array = new PdfArray();
        for (int k = 0; k < options.length; ++k) {
            final String[] subOption = options[k];
            final PdfArray ar2 = new PdfArray(new PdfString(subOption[0], "UnicodeBig"));
            ar2.add(new PdfString(subOption[1], "UnicodeBig"));
            array.add(ar2);
        }
        return array;
    }
    
    public static PdfFormField createSignature(final PdfWriter writer) {
        final PdfFormField field = new PdfFormField(writer);
        field.put(PdfName.FT, PdfName.SIG);
        return field;
    }
    
    public PdfFormField getParent() {
        return this.parent;
    }
    
    public void addKid(final PdfFormField field) {
        field.parent = this;
        if (this.kids == null) {
            this.kids = new ArrayList();
        }
        this.kids.add(field);
    }
    
    public ArrayList getKids() {
        return this.kids;
    }
    
    public int setFieldFlags(final int flags) {
        final PdfNumber obj = (PdfNumber)this.get(PdfName.FF);
        int old;
        if (obj == null) {
            old = 0;
        }
        else {
            old = obj.intValue();
        }
        final int v = old | flags;
        this.put(PdfName.FF, new PdfNumber(v));
        return old;
    }
    
    public void setValueAsString(final String s) {
        this.put(PdfName.V, new PdfString(s, "UnicodeBig"));
    }
    
    public void setValueAsName(final String s) {
        this.put(PdfName.V, new PdfName(s));
    }
    
    public void setValue(final PdfSignature sig) {
        this.put(PdfName.V, sig);
    }
    
    public void setDefaultValueAsString(final String s) {
        this.put(PdfName.DV, new PdfString(s, "UnicodeBig"));
    }
    
    public void setDefaultValueAsName(final String s) {
        this.put(PdfName.DV, new PdfName(s));
    }
    
    public void setFieldName(final String s) {
        if (s != null) {
            this.put(PdfName.T, new PdfString(s, "UnicodeBig"));
        }
    }
    
    public void setUserName(final String s) {
        this.put(PdfName.TU, new PdfString(s, "UnicodeBig"));
    }
    
    public void setMappingName(final String s) {
        this.put(PdfName.TM, new PdfString(s, "UnicodeBig"));
    }
    
    public void setQuadding(final int v) {
        this.put(PdfName.Q, new PdfNumber(v));
    }
    
    static void mergeResources(final PdfDictionary result, final PdfDictionary source, final PdfStamperImp writer) {
        PdfDictionary dic = null;
        PdfDictionary res = null;
        PdfName target = null;
        for (int k = 0; k < PdfFormField.mergeTarget.length; ++k) {
            target = PdfFormField.mergeTarget[k];
            final PdfDictionary pdfDict = source.getAsDict(target);
            if ((dic = pdfDict) != null) {
                if ((res = (PdfDictionary)PdfReader.getPdfObject(result.get(target), result)) == null) {
                    res = new PdfDictionary();
                }
                res.mergeDifferent(dic);
                result.put(target, res);
                if (writer != null) {
                    writer.markUsed(res);
                }
            }
        }
    }
    
    static void mergeResources(final PdfDictionary result, final PdfDictionary source) {
        mergeResources(result, source, null);
    }
    
    @Override
    public void setUsed() {
        this.used = true;
        if (this.parent != null) {
            this.put(PdfName.PARENT, this.parent.getIndirectReference());
        }
        if (this.kids != null) {
            final PdfArray array = new PdfArray();
            for (int k = 0; k < this.kids.size(); ++k) {
                array.add(this.kids.get(k).getIndirectReference());
            }
            this.put(PdfName.KIDS, array);
        }
        if (this.templates == null) {
            return;
        }
        final PdfDictionary dic = new PdfDictionary();
        for (final PdfTemplate template : this.templates.keySet()) {
            mergeResources(dic, (PdfDictionary)template.getResources());
        }
        this.put(PdfName.DR, dic);
    }
    
    public static PdfAnnotation shallowDuplicate(final PdfAnnotation annot) {
        PdfAnnotation dup;
        if (annot.isForm()) {
            dup = new PdfFormField(annot.writer);
            final PdfFormField dupField = (PdfFormField)dup;
            final PdfFormField srcField = (PdfFormField)annot;
            dupField.parent = srcField.parent;
            dupField.kids = srcField.kids;
        }
        else {
            dup = new PdfAnnotation(annot.writer, null);
        }
        dup.merge(annot);
        dup.form = annot.form;
        dup.annotation = annot.annotation;
        dup.templates = annot.templates;
        return dup;
    }
    
    static {
        IF_SCALE_ALWAYS = PdfName.A;
        IF_SCALE_BIGGER = PdfName.B;
        IF_SCALE_SMALLER = PdfName.S;
        IF_SCALE_NEVER = PdfName.N;
        IF_SCALE_ANAMORPHIC = PdfName.A;
        IF_SCALE_PROPORTIONAL = PdfName.P;
        PdfFormField.mergeTarget = new PdfName[] { PdfName.FONT, PdfName.XOBJECT, PdfName.COLORSPACE, PdfName.PATTERN };
    }
}
