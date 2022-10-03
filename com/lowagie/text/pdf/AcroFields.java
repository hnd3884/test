package com.lowagie.text.pdf;

import java.io.InputStream;
import java.util.Comparator;
import java.util.Collections;
import java.util.Collection;
import com.lowagie.text.Image;
import org.apache.commons.codec.binary.Base64;
import java.util.List;
import org.w3c.dom.Node;
import com.lowagie.text.Rectangle;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.awt.Color;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.Iterator;
import com.lowagie.text.ExceptionConverter;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class AcroFields
{
    PdfReader reader;
    PdfWriter writer;
    HashMap fields;
    private int topFirst;
    private HashMap sigNames;
    private boolean append;
    public static final int DA_FONT = 0;
    public static final int DA_SIZE = 1;
    public static final int DA_COLOR = 2;
    private HashMap extensionFonts;
    private XfaForm xfa;
    public static final int FIELD_TYPE_NONE = 0;
    public static final int FIELD_TYPE_PUSHBUTTON = 1;
    public static final int FIELD_TYPE_CHECKBOX = 2;
    public static final int FIELD_TYPE_RADIOBUTTON = 3;
    public static final int FIELD_TYPE_TEXT = 4;
    public static final int FIELD_TYPE_LIST = 5;
    public static final int FIELD_TYPE_COMBO = 6;
    public static final int FIELD_TYPE_SIGNATURE = 7;
    private boolean lastWasString;
    private boolean generateAppearances;
    private HashMap localFonts;
    private float extraMarginLeft;
    private float extraMarginTop;
    private ArrayList substitutionFonts;
    private static final HashMap stdFieldFontNames;
    private int totalRevisions;
    private Map fieldCache;
    private static final PdfName[] buttonRemove;
    
    AcroFields(final PdfReader reader, final PdfWriter writer) {
        this.extensionFonts = new HashMap();
        this.generateAppearances = true;
        this.localFonts = new HashMap();
        this.reader = reader;
        this.writer = writer;
        try {
            this.xfa = new XfaForm(reader);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
        if (writer instanceof PdfStamperImp) {
            this.append = ((PdfStamperImp)writer).isAppend();
        }
        this.fill();
    }
    
    void fill() {
        this.fields = new HashMap();
        final PdfDictionary top = (PdfDictionary)PdfReader.getPdfObjectRelease(this.reader.getCatalog().get(PdfName.ACROFORM));
        if (top == null) {
            return;
        }
        final PdfArray arrfds = (PdfArray)PdfReader.getPdfObjectRelease(top.get(PdfName.FIELDS));
        if (arrfds == null || arrfds.size() == 0) {
            return;
        }
        for (int k = 1; k <= this.reader.getNumberOfPages(); ++k) {
            final PdfDictionary page = this.reader.getPageNRelease(k);
            final PdfArray annots = (PdfArray)PdfReader.getPdfObjectRelease(page.get(PdfName.ANNOTS), page);
            if (annots != null) {
                for (int j = 0; j < annots.size(); ++j) {
                    PdfDictionary annot = annots.getAsDict(j);
                    if (annot == null) {
                        PdfReader.releaseLastXrefPartial(annots.getAsIndirectObject(j));
                    }
                    else if (!PdfName.WIDGET.equals(annot.getAsName(PdfName.SUBTYPE))) {
                        PdfReader.releaseLastXrefPartial(annots.getAsIndirectObject(j));
                    }
                    else {
                        final PdfDictionary widget = annot;
                        final PdfDictionary dic = new PdfDictionary();
                        dic.putAll(annot);
                        String name = "";
                        PdfDictionary value = null;
                        PdfObject lastV = null;
                        while (annot != null) {
                            dic.mergeDifferent(annot);
                            final PdfString t = annot.getAsString(PdfName.T);
                            if (t != null) {
                                name = t.toUnicodeString() + "." + name;
                            }
                            if (lastV == null && annot.get(PdfName.V) != null) {
                                lastV = PdfReader.getPdfObjectRelease(annot.get(PdfName.V));
                            }
                            if (value == null && t != null) {
                                value = annot;
                                if (annot.get(PdfName.V) == null && lastV != null) {
                                    value.put(PdfName.V, lastV);
                                }
                            }
                            annot = annot.getAsDict(PdfName.PARENT);
                        }
                        if (name.length() > 0) {
                            name = name.substring(0, name.length() - 1);
                        }
                        Item item = this.fields.get(name);
                        if (item == null) {
                            item = new Item();
                            this.fields.put(name, item);
                        }
                        if (value == null) {
                            item.addValue(widget);
                        }
                        else {
                            item.addValue(value);
                        }
                        item.addWidget(widget);
                        item.addWidgetRef(annots.getAsIndirectObject(j));
                        if (top != null) {
                            dic.mergeDifferent(top);
                        }
                        item.addMerged(dic);
                        item.addPage(k);
                        item.addTabOrder(j);
                    }
                }
            }
        }
        final PdfNumber sigFlags = top.getAsNumber(PdfName.SIGFLAGS);
        if (sigFlags == null || (sigFlags.intValue() & 0x1) != 0x1) {
            return;
        }
        for (int i = 0; i < arrfds.size(); ++i) {
            final PdfDictionary annot2 = arrfds.getAsDict(i);
            if (annot2 == null) {
                PdfReader.releaseLastXrefPartial(arrfds.getAsIndirectObject(i));
            }
            else if (!PdfName.WIDGET.equals(annot2.getAsName(PdfName.SUBTYPE))) {
                PdfReader.releaseLastXrefPartial(arrfds.getAsIndirectObject(i));
            }
            else {
                final PdfArray kids = (PdfArray)PdfReader.getPdfObjectRelease(annot2.get(PdfName.KIDS));
                if (kids == null) {
                    final PdfDictionary dic2 = new PdfDictionary();
                    dic2.putAll(annot2);
                    final PdfString t2 = annot2.getAsString(PdfName.T);
                    if (t2 != null) {
                        final String name2 = t2.toUnicodeString();
                        if (!this.fields.containsKey(name2)) {
                            final Item item2 = new Item();
                            this.fields.put(name2, item2);
                            item2.addValue(dic2);
                            item2.addWidget(dic2);
                            item2.addWidgetRef(arrfds.getAsIndirectObject(i));
                            item2.addMerged(dic2);
                            item2.addPage(-1);
                            item2.addTabOrder(-1);
                        }
                    }
                }
            }
        }
    }
    
    public String[] getAppearanceStates(final String fieldName) {
        final Item fd = this.fields.get(fieldName);
        if (fd == null) {
            return null;
        }
        final HashMap names = new HashMap();
        final PdfDictionary vals = fd.getValue(0);
        final PdfString stringOpt = vals.getAsString(PdfName.OPT);
        if (stringOpt != null) {
            names.put(stringOpt.toUnicodeString(), null);
        }
        else {
            final PdfArray arrayOpt = vals.getAsArray(PdfName.OPT);
            if (arrayOpt != null) {
                for (int k = 0; k < arrayOpt.size(); ++k) {
                    final PdfString valStr = arrayOpt.getAsString(k);
                    if (valStr != null) {
                        names.put(valStr.toUnicodeString(), null);
                    }
                }
            }
        }
        for (int i = 0; i < fd.size(); ++i) {
            PdfDictionary dic = fd.getWidget(i);
            dic = dic.getAsDict(PdfName.AP);
            if (dic != null) {
                dic = dic.getAsDict(PdfName.N);
                if (dic != null) {
                    final Iterator it = dic.getKeys().iterator();
                    while (it.hasNext()) {
                        final String name = PdfName.decodeName(it.next().toString());
                        names.put(name, null);
                    }
                }
            }
        }
        final String[] out = new String[names.size()];
        return (String[])names.keySet().toArray(out);
    }
    
    private String[] getListOption(final String fieldName, final int idx) {
        final Item fd = this.getFieldItem(fieldName);
        if (fd == null) {
            return null;
        }
        final PdfArray ar = fd.getMerged(0).getAsArray(PdfName.OPT);
        if (ar == null) {
            return null;
        }
        final String[] ret = new String[ar.size()];
        for (int k = 0; k < ar.size(); ++k) {
            PdfObject obj = ar.getDirectObject(k);
            try {
                if (obj.isArray()) {
                    obj = ((PdfArray)obj).getDirectObject(idx);
                }
                if (obj.isString()) {
                    ret[k] = ((PdfString)obj).toUnicodeString();
                }
                else {
                    ret[k] = obj.toString();
                }
            }
            catch (final Exception e) {
                ret[k] = "";
            }
        }
        return ret;
    }
    
    public String[] getListOptionExport(final String fieldName) {
        return this.getListOption(fieldName, 0);
    }
    
    public String[] getListOptionDisplay(final String fieldName) {
        return this.getListOption(fieldName, 1);
    }
    
    public boolean setListOption(final String fieldName, final String[] exportValues, final String[] displayValues) {
        if (exportValues == null && displayValues == null) {
            return false;
        }
        if (exportValues != null && displayValues != null && exportValues.length != displayValues.length) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.export.and.the.display.array.must.have.the.same.size"));
        }
        final int ftype = this.getFieldType(fieldName);
        if (ftype != 6 && ftype != 5) {
            return false;
        }
        final Item fd = this.fields.get(fieldName);
        String[] sing = null;
        if (exportValues == null && displayValues != null) {
            sing = displayValues;
        }
        else if (exportValues != null && displayValues == null) {
            sing = exportValues;
        }
        final PdfArray opt = new PdfArray();
        if (sing != null) {
            for (int k = 0; k < sing.length; ++k) {
                opt.add(new PdfString(sing[k], "UnicodeBig"));
            }
        }
        else {
            for (int k = 0; k < exportValues.length; ++k) {
                final PdfArray a = new PdfArray();
                a.add(new PdfString(exportValues[k], "UnicodeBig"));
                a.add(new PdfString(displayValues[k], "UnicodeBig"));
                opt.add(a);
            }
        }
        fd.writeToAll(PdfName.OPT, opt, 5);
        return true;
    }
    
    public int getFieldType(final String fieldName) {
        final Item fd = this.getFieldItem(fieldName);
        if (fd == null) {
            return 0;
        }
        final PdfDictionary merged = fd.getMerged(0);
        final PdfName type = merged.getAsName(PdfName.FT);
        if (type == null) {
            return 0;
        }
        int ff = 0;
        final PdfNumber ffo = merged.getAsNumber(PdfName.FF);
        if (ffo != null) {
            ff = ffo.intValue();
        }
        if (PdfName.BTN.equals(type)) {
            if ((ff & 0x10000) != 0x0) {
                return 1;
            }
            if ((ff & 0x8000) != 0x0) {
                return 3;
            }
            return 2;
        }
        else {
            if (PdfName.TX.equals(type)) {
                return 4;
            }
            if (PdfName.CH.equals(type)) {
                if ((ff & 0x20000) != 0x0) {
                    return 6;
                }
                return 5;
            }
            else {
                if (PdfName.SIG.equals(type)) {
                    return 7;
                }
                return 0;
            }
        }
    }
    
    public void exportAsFdf(final FdfWriter writer) {
        for (final Map.Entry entry : this.fields.entrySet()) {
            final Item item = entry.getValue();
            final String name = entry.getKey();
            final PdfObject v = item.getMerged(0).get(PdfName.V);
            if (v == null) {
                continue;
            }
            final String value = this.getField(name);
            if (this.lastWasString) {
                writer.setFieldAsString(name, value);
            }
            else {
                writer.setFieldAsName(name, value);
            }
        }
    }
    
    public boolean renameField(final String oldName, String newName) {
        final int idx1 = oldName.lastIndexOf(46) + 1;
        final int idx2 = newName.lastIndexOf(46) + 1;
        if (idx1 != idx2) {
            return false;
        }
        if (!oldName.substring(0, idx1).equals(newName.substring(0, idx2))) {
            return false;
        }
        if (this.fields.containsKey(newName)) {
            return false;
        }
        final Item item = this.fields.get(oldName);
        if (item == null) {
            return false;
        }
        newName = newName.substring(idx2);
        final PdfString ss = new PdfString(newName, "UnicodeBig");
        item.writeToAll(PdfName.T, ss, 5);
        item.markUsed(this, 4);
        this.fields.remove(oldName);
        this.fields.put(newName, item);
        return true;
    }
    
    public static Object[] splitDAelements(final String da) {
        try {
            final PRTokeniser tk = new PRTokeniser(PdfEncodings.convertToBytes(da, null));
            final ArrayList stack = new ArrayList();
            final Object[] ret = new Object[3];
            while (tk.nextToken()) {
                if (tk.getTokenType() == 4) {
                    continue;
                }
                if (tk.getTokenType() == 10) {
                    final String operator = tk.getStringValue();
                    if (operator.equals("Tf")) {
                        if (stack.size() >= 2) {
                            ret[0] = stack.get(stack.size() - 2);
                            ret[1] = new Float(stack.get(stack.size() - 1));
                        }
                    }
                    else if (operator.equals("g")) {
                        if (stack.size() >= 1) {
                            final float gray = new Float(stack.get(stack.size() - 1));
                            if (gray != 0.0f) {
                                ret[2] = new GrayColor(gray);
                            }
                        }
                    }
                    else if (operator.equals("rg")) {
                        if (stack.size() >= 3) {
                            final float red = new Float(stack.get(stack.size() - 3));
                            final float green = new Float(stack.get(stack.size() - 2));
                            final float blue = new Float(stack.get(stack.size() - 1));
                            ret[2] = new Color(red, green, blue);
                        }
                    }
                    else if (operator.equals("k") && stack.size() >= 4) {
                        final float cyan = new Float(stack.get(stack.size() - 4));
                        final float magenta = new Float(stack.get(stack.size() - 3));
                        final float yellow = new Float(stack.get(stack.size() - 2));
                        final float black = new Float(stack.get(stack.size() - 1));
                        ret[2] = new CMYKColor(cyan, magenta, yellow, black);
                    }
                    stack.clear();
                }
                else {
                    stack.add(tk.getStringValue());
                }
            }
            return ret;
        }
        catch (final IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }
    
    public void decodeGenericDictionary(final PdfDictionary merged, final BaseField tx) throws IOException, DocumentException {
        int flags = 0;
        final PdfString da = merged.getAsString(PdfName.DA);
        if (da != null) {
            final Object[] dab = splitDAelements(da.toUnicodeString());
            if (dab[1] != null) {
                tx.setFontSize((float)dab[1]);
            }
            if (dab[2] != null) {
                tx.setTextColor((Color)dab[2]);
            }
            if (dab[0] != null) {
                PdfDictionary font = merged.getAsDict(PdfName.DR);
                if (font != null) {
                    font = font.getAsDict(PdfName.FONT);
                    if (font != null) {
                        final PdfObject po = font.get(new PdfName((String)dab[0]));
                        if (po != null && po.type() == 10) {
                            final PRIndirectReference por = (PRIndirectReference)po;
                            final BaseFont bp = new DocumentFont((PRIndirectReference)po);
                            tx.setFont(bp);
                            final Integer porkey = new Integer(por.getNumber());
                            BaseFont porf = this.extensionFonts.get(porkey);
                            if (porf == null && !this.extensionFonts.containsKey(porkey)) {
                                final PdfDictionary fo = (PdfDictionary)PdfReader.getPdfObject(po);
                                final PdfDictionary fd = fo.getAsDict(PdfName.FONTDESCRIPTOR);
                                if (fd != null) {
                                    PRStream prs = (PRStream)PdfReader.getPdfObject(fd.get(PdfName.FONTFILE2));
                                    if (prs == null) {
                                        prs = (PRStream)PdfReader.getPdfObject(fd.get(PdfName.FONTFILE3));
                                    }
                                    if (prs == null) {
                                        this.extensionFonts.put(porkey, null);
                                    }
                                    else {
                                        try {
                                            porf = BaseFont.createFont("font.ttf", "Identity-H", true, false, PdfReader.getStreamBytes(prs), null);
                                        }
                                        catch (final Exception ex) {}
                                        this.extensionFonts.put(porkey, porf);
                                    }
                                }
                            }
                            if (tx instanceof TextField) {
                                ((TextField)tx).setExtensionFont(porf);
                            }
                        }
                        else {
                            BaseFont bf = this.localFonts.get(dab[0]);
                            if (bf == null) {
                                final String[] fn = AcroFields.stdFieldFontNames.get(dab[0]);
                                if (fn != null) {
                                    try {
                                        String enc = "winansi";
                                        if (fn.length > 1) {
                                            enc = fn[1];
                                        }
                                        bf = BaseFont.createFont(fn[0], enc, false);
                                        tx.setFont(bf);
                                    }
                                    catch (final Exception ex2) {}
                                }
                            }
                            else {
                                tx.setFont(bf);
                            }
                        }
                    }
                }
            }
        }
        final PdfDictionary mk = merged.getAsDict(PdfName.MK);
        if (mk != null) {
            PdfArray ar = mk.getAsArray(PdfName.BC);
            final Color border = this.getMKColor(ar);
            tx.setBorderColor(border);
            if (border != null) {
                tx.setBorderWidth(1.0f);
            }
            ar = mk.getAsArray(PdfName.BG);
            tx.setBackgroundColor(this.getMKColor(ar));
            final PdfNumber rotation = mk.getAsNumber(PdfName.R);
            if (rotation != null) {
                tx.setRotation(rotation.intValue());
            }
        }
        PdfNumber nfl = merged.getAsNumber(PdfName.F);
        flags = 0;
        tx.setVisibility(2);
        if (nfl != null) {
            flags = nfl.intValue();
            if ((flags & 0x4) != 0x0 && (flags & 0x2) != 0x0) {
                tx.setVisibility(1);
            }
            else if ((flags & 0x4) != 0x0 && (flags & 0x20) != 0x0) {
                tx.setVisibility(3);
            }
            else if ((flags & 0x4) != 0x0) {
                tx.setVisibility(0);
            }
        }
        nfl = merged.getAsNumber(PdfName.FF);
        flags = 0;
        if (nfl != null) {
            flags = nfl.intValue();
        }
        tx.setOptions(flags);
        if ((flags & 0x1000000) != 0x0) {
            final PdfNumber maxLen = merged.getAsNumber(PdfName.MAXLEN);
            int len = 0;
            if (maxLen != null) {
                len = maxLen.intValue();
            }
            tx.setMaxCharacterLength(len);
        }
        nfl = merged.getAsNumber(PdfName.Q);
        if (nfl != null) {
            if (nfl.intValue() == 1) {
                tx.setAlignment(1);
            }
            else if (nfl.intValue() == 2) {
                tx.setAlignment(2);
            }
        }
        final PdfDictionary bs = merged.getAsDict(PdfName.BS);
        if (bs != null) {
            final PdfNumber w = bs.getAsNumber(PdfName.W);
            if (w != null) {
                tx.setBorderWidth(w.floatValue());
            }
            final PdfName s = bs.getAsName(PdfName.S);
            if (PdfName.D.equals(s)) {
                tx.setBorderStyle(1);
            }
            else if (PdfName.B.equals(s)) {
                tx.setBorderStyle(2);
            }
            else if (PdfName.I.equals(s)) {
                tx.setBorderStyle(3);
            }
            else if (PdfName.U.equals(s)) {
                tx.setBorderStyle(4);
            }
        }
        else {
            final PdfArray bd = merged.getAsArray(PdfName.BORDER);
            if (bd != null) {
                if (bd.size() >= 3) {
                    tx.setBorderWidth(bd.getAsNumber(2).floatValue());
                }
                if (bd.size() >= 4) {
                    tx.setBorderStyle(1);
                }
            }
        }
    }
    
    PdfAppearance getAppearance(final PdfDictionary merged, final String[] values, final String fieldName) throws IOException, DocumentException {
        this.topFirst = 0;
        String text = (values.length > 0) ? values[0] : null;
        TextField tx = null;
        if (this.fieldCache == null || !this.fieldCache.containsKey(fieldName)) {
            tx = new TextField(this.writer, null, null);
            tx.setExtraMargin(this.extraMarginLeft, this.extraMarginTop);
            tx.setBorderWidth(0.0f);
            tx.setSubstitutionFonts(this.substitutionFonts);
            this.decodeGenericDictionary(merged, tx);
            final PdfArray rect = merged.getAsArray(PdfName.RECT);
            Rectangle box = PdfReader.getNormalizedRectangle(rect);
            if (tx.getRotation() == 90 || tx.getRotation() == 270) {
                box = box.rotate();
            }
            tx.setBox(box);
            if (this.fieldCache != null) {
                this.fieldCache.put(fieldName, tx);
            }
        }
        else {
            tx = this.fieldCache.get(fieldName);
            tx.setWriter(this.writer);
        }
        final PdfName fieldType = merged.getAsName(PdfName.FT);
        if (PdfName.TX.equals(fieldType)) {
            if (values.length > 0 && values[0] != null) {
                tx.setText(values[0]);
            }
            return tx.getAppearance();
        }
        if (!PdfName.CH.equals(fieldType)) {
            throw new DocumentException(MessageLocalization.getComposedMessage("an.appearance.was.requested.without.a.variable.text.field"));
        }
        final PdfArray opt = merged.getAsArray(PdfName.OPT);
        int flags = 0;
        final PdfNumber nfl = merged.getAsNumber(PdfName.FF);
        if (nfl != null) {
            flags = nfl.intValue();
        }
        if ((flags & 0x20000) != 0x0 && opt == null) {
            tx.setText(text);
            return tx.getAppearance();
        }
        if (opt != null) {
            final String[] choices = new String[opt.size()];
            final String[] choicesExp = new String[opt.size()];
            for (int k = 0; k < opt.size(); ++k) {
                final PdfObject obj = opt.getPdfObject(k);
                if (obj.isString()) {
                    choices[k] = (choicesExp[k] = ((PdfString)obj).toUnicodeString());
                }
                else {
                    final PdfArray a = (PdfArray)obj;
                    choicesExp[k] = a.getAsString(0).toUnicodeString();
                    choices[k] = a.getAsString(1).toUnicodeString();
                }
            }
            if ((flags & 0x20000) != 0x0) {
                for (int k = 0; k < choices.length; ++k) {
                    if (text.equals(choicesExp[k])) {
                        text = choices[k];
                        break;
                    }
                }
                tx.setText(text);
                return tx.getAppearance();
            }
            final ArrayList indexes = new ArrayList();
            for (int i = 0; i < choicesExp.length; ++i) {
                for (int j = 0; j < values.length; ++j) {
                    final String val = values[j];
                    if (val != null && val.equals(choicesExp[i])) {
                        indexes.add(new Integer(i));
                        break;
                    }
                }
            }
            tx.setChoices(choices);
            tx.setChoiceExports(choicesExp);
            tx.setChoiceSelections(indexes);
        }
        final PdfAppearance app = tx.getListAppearance();
        this.topFirst = tx.getTopFirst();
        return app;
    }
    
    PdfAppearance getAppearance(final PdfDictionary merged, final String text, final String fieldName) throws IOException, DocumentException {
        final String[] valueArr = { text };
        return this.getAppearance(merged, valueArr, fieldName);
    }
    
    Color getMKColor(final PdfArray ar) {
        if (ar == null) {
            return null;
        }
        switch (ar.size()) {
            case 1: {
                return new GrayColor(ar.getAsNumber(0).floatValue());
            }
            case 3: {
                return new Color(ExtendedColor.normalize(ar.getAsNumber(0).floatValue()), ExtendedColor.normalize(ar.getAsNumber(1).floatValue()), ExtendedColor.normalize(ar.getAsNumber(2).floatValue()));
            }
            case 4: {
                return new CMYKColor(ar.getAsNumber(0).floatValue(), ar.getAsNumber(1).floatValue(), ar.getAsNumber(2).floatValue(), ar.getAsNumber(3).floatValue());
            }
            default: {
                return null;
            }
        }
    }
    
    public String getField(String name) {
        if (this.xfa.isXfaPresent()) {
            name = this.xfa.findFieldName(name, this);
            if (name == null) {
                return null;
            }
            name = XfaForm.Xml2Som.getShortName(name);
            return XfaForm.getNodeText(this.xfa.findDatasetsNode(name));
        }
        else {
            final Item item = this.fields.get(name);
            if (item == null) {
                return null;
            }
            this.lastWasString = false;
            final PdfDictionary mergedDict = item.getMerged(0);
            final PdfObject v = PdfReader.getPdfObject(mergedDict.get(PdfName.V));
            if (v == null) {
                return "";
            }
            if (v instanceof PRStream) {
                try {
                    final byte[] valBytes = PdfReader.getStreamBytes((PRStream)v);
                    return new String(valBytes);
                }
                catch (final IOException e) {
                    throw new ExceptionConverter(e);
                }
            }
            final PdfName type = mergedDict.getAsName(PdfName.FT);
            if (PdfName.BTN.equals(type)) {
                final PdfNumber ff = mergedDict.getAsNumber(PdfName.FF);
                int flags = 0;
                if (ff != null) {
                    flags = ff.intValue();
                }
                if ((flags & 0x10000) != 0x0) {
                    return "";
                }
                String value = "";
                if (v instanceof PdfName) {
                    value = PdfName.decodeName(v.toString());
                }
                else if (v instanceof PdfString) {
                    value = ((PdfString)v).toUnicodeString();
                }
                final PdfArray opts = item.getValue(0).getAsArray(PdfName.OPT);
                if (opts != null) {
                    int idx = 0;
                    try {
                        idx = Integer.parseInt(value);
                        final PdfString ps = opts.getAsString(idx);
                        value = ps.toUnicodeString();
                        this.lastWasString = true;
                    }
                    catch (final Exception ex) {}
                }
                return value;
            }
            else {
                if (v instanceof PdfString) {
                    this.lastWasString = true;
                    return ((PdfString)v).toUnicodeString();
                }
                if (v instanceof PdfName) {
                    return PdfName.decodeName(v.toString());
                }
                return "";
            }
        }
    }
    
    public String[] getListSelection(final String name) {
        final String s = this.getField(name);
        String[] ret;
        if (s == null) {
            ret = new String[0];
        }
        else {
            ret = new String[] { s };
        }
        final Item item = this.fields.get(name);
        if (item == null) {
            return ret;
        }
        final PdfArray values = item.getMerged(0).getAsArray(PdfName.I);
        if (values == null) {
            return ret;
        }
        ret = new String[values.size()];
        final String[] options = this.getListOptionExport(name);
        int idx = 0;
        final Iterator i = values.listIterator();
        while (i.hasNext()) {
            final PdfNumber n = i.next();
            ret[idx++] = options[n.intValue()];
        }
        return ret;
    }
    
    public boolean setFieldProperty(final String field, final String name, final Object value, final int[] inst) {
        if (this.writer == null) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("this.acrofields.instance.is.read.only"));
        }
        try {
            final Item item = this.fields.get(field);
            if (item == null) {
                return false;
            }
            final InstHit hit = new InstHit(inst);
            if (name.equalsIgnoreCase("textfont")) {
                for (int k = 0; k < item.size(); ++k) {
                    if (hit.isHit(k)) {
                        final PdfDictionary merged = item.getMerged(k);
                        final PdfString da = merged.getAsString(PdfName.DA);
                        PdfDictionary dr = merged.getAsDict(PdfName.DR);
                        if (da != null && dr != null) {
                            final Object[] dao = splitDAelements(da.toUnicodeString());
                            final PdfAppearance cb = new PdfAppearance();
                            if (dao[0] != null) {
                                final BaseFont bf = (BaseFont)value;
                                PdfName psn = PdfAppearance.stdFieldFontNames.get(bf.getPostscriptFontName());
                                if (psn == null) {
                                    psn = new PdfName(bf.getPostscriptFontName());
                                }
                                PdfDictionary fonts = dr.getAsDict(PdfName.FONT);
                                if (fonts == null) {
                                    fonts = new PdfDictionary();
                                    dr.put(PdfName.FONT, fonts);
                                }
                                final PdfIndirectReference fref = (PdfIndirectReference)fonts.get(psn);
                                final PdfDictionary top = this.reader.getCatalog().getAsDict(PdfName.ACROFORM);
                                this.markUsed(top);
                                dr = top.getAsDict(PdfName.DR);
                                if (dr == null) {
                                    dr = new PdfDictionary();
                                    top.put(PdfName.DR, dr);
                                }
                                this.markUsed(dr);
                                PdfDictionary fontsTop = dr.getAsDict(PdfName.FONT);
                                if (fontsTop == null) {
                                    fontsTop = new PdfDictionary();
                                    dr.put(PdfName.FONT, fontsTop);
                                }
                                this.markUsed(fontsTop);
                                final PdfIndirectReference frefTop = (PdfIndirectReference)fontsTop.get(psn);
                                if (frefTop != null) {
                                    if (fref == null) {
                                        fonts.put(psn, frefTop);
                                    }
                                }
                                else if (fref == null) {
                                    FontDetails fd;
                                    if (bf.getFontType() == 4) {
                                        fd = new FontDetails(null, ((DocumentFont)bf).getIndirectReference(), bf);
                                    }
                                    else {
                                        bf.setSubset(false);
                                        fd = this.writer.addSimple(bf);
                                        this.localFonts.put(psn.toString().substring(1), bf);
                                    }
                                    fontsTop.put(psn, fd.getIndirectReference());
                                    fonts.put(psn, fd.getIndirectReference());
                                }
                                final ByteBuffer buf = cb.getInternalBuffer();
                                buf.append(psn.getBytes()).append(' ').append((float)dao[1]).append(" Tf ");
                                if (dao[2] != null) {
                                    cb.setColorFill((Color)dao[2]);
                                }
                                final PdfString s = new PdfString(cb.toString());
                                item.getMerged(k).put(PdfName.DA, s);
                                item.getWidget(k).put(PdfName.DA, s);
                                this.markUsed(item.getWidget(k));
                            }
                        }
                    }
                }
            }
            else if (name.equalsIgnoreCase("textcolor")) {
                for (int k = 0; k < item.size(); ++k) {
                    if (hit.isHit(k)) {
                        final PdfDictionary merged = item.getMerged(k);
                        final PdfString da = merged.getAsString(PdfName.DA);
                        if (da != null) {
                            final Object[] dao2 = splitDAelements(da.toUnicodeString());
                            final PdfAppearance cb2 = new PdfAppearance();
                            if (dao2[0] != null) {
                                final ByteBuffer buf2 = cb2.getInternalBuffer();
                                buf2.append(new PdfName((String)dao2[0]).getBytes()).append(' ').append((float)dao2[1]).append(" Tf ");
                                cb2.setColorFill((Color)value);
                                final PdfString s2 = new PdfString(cb2.toString());
                                item.getMerged(k).put(PdfName.DA, s2);
                                item.getWidget(k).put(PdfName.DA, s2);
                                this.markUsed(item.getWidget(k));
                            }
                        }
                    }
                }
            }
            else if (name.equalsIgnoreCase("textsize")) {
                for (int k = 0; k < item.size(); ++k) {
                    if (hit.isHit(k)) {
                        final PdfDictionary merged = item.getMerged(k);
                        final PdfString da = merged.getAsString(PdfName.DA);
                        if (da != null) {
                            final Object[] dao2 = splitDAelements(da.toUnicodeString());
                            final PdfAppearance cb2 = new PdfAppearance();
                            if (dao2[0] != null) {
                                final ByteBuffer buf2 = cb2.getInternalBuffer();
                                buf2.append(new PdfName((String)dao2[0]).getBytes()).append(' ').append((float)value).append(" Tf ");
                                if (dao2[2] != null) {
                                    cb2.setColorFill((Color)dao2[2]);
                                }
                                final PdfString s2 = new PdfString(cb2.toString());
                                item.getMerged(k).put(PdfName.DA, s2);
                                item.getWidget(k).put(PdfName.DA, s2);
                                this.markUsed(item.getWidget(k));
                            }
                        }
                    }
                }
            }
            else {
                if (!name.equalsIgnoreCase("bgcolor") && !name.equalsIgnoreCase("bordercolor")) {
                    return false;
                }
                final PdfName dname = name.equalsIgnoreCase("bgcolor") ? PdfName.BG : PdfName.BC;
                for (int i = 0; i < item.size(); ++i) {
                    if (hit.isHit(i)) {
                        final PdfDictionary merged = item.getMerged(i);
                        PdfDictionary mk = merged.getAsDict(PdfName.MK);
                        if (mk == null) {
                            if (value == null) {
                                return true;
                            }
                            mk = new PdfDictionary();
                            item.getMerged(i).put(PdfName.MK, mk);
                            item.getWidget(i).put(PdfName.MK, mk);
                            this.markUsed(item.getWidget(i));
                        }
                        else {
                            this.markUsed(mk);
                        }
                        if (value == null) {
                            mk.remove(dname);
                        }
                        else {
                            mk.put(dname, PdfAnnotation.getMKColor((Color)value));
                        }
                    }
                }
            }
            return true;
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public boolean setFieldProperty(final String field, final String name, final int value, final int[] inst) {
        if (this.writer == null) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("this.acrofields.instance.is.read.only"));
        }
        final Item item = this.fields.get(field);
        if (item == null) {
            return false;
        }
        final InstHit hit = new InstHit(inst);
        if (name.equalsIgnoreCase("flags")) {
            final PdfNumber num = new PdfNumber(value);
            for (int k = 0; k < item.size(); ++k) {
                if (hit.isHit(k)) {
                    item.getMerged(k).put(PdfName.F, num);
                    item.getWidget(k).put(PdfName.F, num);
                    this.markUsed(item.getWidget(k));
                }
            }
        }
        else if (name.equalsIgnoreCase("setflags")) {
            for (int i = 0; i < item.size(); ++i) {
                if (hit.isHit(i)) {
                    PdfNumber num2 = item.getWidget(i).getAsNumber(PdfName.F);
                    int val = 0;
                    if (num2 != null) {
                        val = num2.intValue();
                    }
                    num2 = new PdfNumber(val | value);
                    item.getMerged(i).put(PdfName.F, num2);
                    item.getWidget(i).put(PdfName.F, num2);
                    this.markUsed(item.getWidget(i));
                }
            }
        }
        else if (name.equalsIgnoreCase("clrflags")) {
            for (int i = 0; i < item.size(); ++i) {
                if (hit.isHit(i)) {
                    final PdfDictionary widget = item.getWidget(i);
                    PdfNumber num3 = widget.getAsNumber(PdfName.F);
                    int val2 = 0;
                    if (num3 != null) {
                        val2 = num3.intValue();
                    }
                    num3 = new PdfNumber(val2 & ~value);
                    item.getMerged(i).put(PdfName.F, num3);
                    widget.put(PdfName.F, num3);
                    this.markUsed(widget);
                }
            }
        }
        else if (name.equalsIgnoreCase("fflags")) {
            final PdfNumber num = new PdfNumber(value);
            for (int k = 0; k < item.size(); ++k) {
                if (hit.isHit(k)) {
                    item.getMerged(k).put(PdfName.FF, num);
                    item.getValue(k).put(PdfName.FF, num);
                    this.markUsed(item.getValue(k));
                }
            }
        }
        else if (name.equalsIgnoreCase("setfflags")) {
            for (int i = 0; i < item.size(); ++i) {
                if (hit.isHit(i)) {
                    final PdfDictionary valDict = item.getValue(i);
                    PdfNumber num3 = valDict.getAsNumber(PdfName.FF);
                    int val2 = 0;
                    if (num3 != null) {
                        val2 = num3.intValue();
                    }
                    num3 = new PdfNumber(val2 | value);
                    item.getMerged(i).put(PdfName.FF, num3);
                    valDict.put(PdfName.FF, num3);
                    this.markUsed(valDict);
                }
            }
        }
        else {
            if (!name.equalsIgnoreCase("clrfflags")) {
                return false;
            }
            for (int i = 0; i < item.size(); ++i) {
                if (hit.isHit(i)) {
                    final PdfDictionary valDict = item.getValue(i);
                    PdfNumber num3 = valDict.getAsNumber(PdfName.FF);
                    int val2 = 0;
                    if (num3 != null) {
                        val2 = num3.intValue();
                    }
                    num3 = new PdfNumber(val2 & ~value);
                    item.getMerged(i).put(PdfName.FF, num3);
                    valDict.put(PdfName.FF, num3);
                    this.markUsed(valDict);
                }
            }
        }
        return true;
    }
    
    public void mergeXfaData(final Node n) throws IOException, DocumentException {
        final XfaForm.Xml2SomDatasets data = new XfaForm.Xml2SomDatasets(n);
        for (final String name : data.getOrder()) {
            final String text = XfaForm.getNodeText(data.getName2Node().get(name));
            this.setField(name, text);
        }
    }
    
    public void setFields(final FdfReader fdf) throws IOException, DocumentException {
        final HashMap fd = fdf.getFields();
        for (final String f : fd.keySet()) {
            final String v = fdf.getFieldValue(f);
            if (v != null) {
                this.setField(f, v);
            }
        }
    }
    
    public void setFields(final FieldReader fieldReader) throws IOException, DocumentException {
        final HashMap fd = fieldReader.getFields();
        for (final String f : fd.keySet()) {
            final String v = fieldReader.getFieldValue(f);
            if (v != null) {
                this.setField(f, v);
            }
            final List l = fieldReader.getListValues(f);
            if (l != null) {
                this.setListSelection(v, l.toArray(new String[l.size()]));
            }
        }
    }
    
    public boolean regenerateField(final String name) throws IOException, DocumentException {
        final String value = this.getField(name);
        return this.setField(name, value, value);
    }
    
    public boolean setField(final String name, final String value) throws IOException, DocumentException {
        return this.setField(name, value, null);
    }
    
    public boolean setField(String name, String value, String display) throws IOException, DocumentException {
        if (this.writer == null) {
            throw new DocumentException(MessageLocalization.getComposedMessage("this.acrofields.instance.is.read.only"));
        }
        if (this.xfa.isXfaPresent()) {
            name = this.xfa.findFieldName(name, this);
            if (name == null) {
                return false;
            }
            final String shortName = XfaForm.Xml2Som.getShortName(name);
            Node xn = this.xfa.findDatasetsNode(shortName);
            if (xn == null) {
                xn = this.xfa.getDatasetsSom().insertNode(this.xfa.getDatasetsNode(), shortName);
            }
            this.xfa.setNodeText(xn, value);
        }
        final Item item = this.fields.get(name);
        if (item == null) {
            return false;
        }
        PdfDictionary merged = item.getMerged(0);
        final PdfName type = merged.getAsName(PdfName.FT);
        if (PdfName.TX.equals(type)) {
            final PdfNumber maxLen = merged.getAsNumber(PdfName.MAXLEN);
            int len = 0;
            if (maxLen != null) {
                len = maxLen.intValue();
            }
            if (len > 0) {
                value = value.substring(0, Math.min(len, value.length()));
            }
        }
        if (display == null) {
            display = value;
        }
        if (PdfName.TX.equals(type) || PdfName.CH.equals(type)) {
            final PdfString v = new PdfString(value, "UnicodeBig");
            for (int idx = 0; idx < item.size(); ++idx) {
                final PdfDictionary valueDic = item.getValue(idx);
                valueDic.put(PdfName.V, v);
                valueDic.remove(PdfName.I);
                this.markUsed(valueDic);
                merged = item.getMerged(idx);
                merged.remove(PdfName.I);
                merged.put(PdfName.V, v);
                final PdfDictionary widget = item.getWidget(idx);
                if (this.generateAppearances) {
                    final PdfAppearance app = this.getAppearance(merged, display, name);
                    if (PdfName.CH.equals(type)) {
                        final PdfNumber n = new PdfNumber(this.topFirst);
                        widget.put(PdfName.TI, n);
                        merged.put(PdfName.TI, n);
                    }
                    PdfDictionary appDic = widget.getAsDict(PdfName.AP);
                    if (appDic == null) {
                        appDic = new PdfDictionary();
                        widget.put(PdfName.AP, appDic);
                        merged.put(PdfName.AP, appDic);
                    }
                    appDic.put(PdfName.N, app.getIndirectReference());
                    this.writer.releaseTemplate(app);
                }
                else {
                    widget.remove(PdfName.AP);
                    merged.remove(PdfName.AP);
                }
                this.markUsed(widget);
            }
            return true;
        }
        if (!PdfName.BTN.equals(type)) {
            return false;
        }
        final PdfNumber ff = item.getMerged(0).getAsNumber(PdfName.FF);
        int flags = 0;
        if (ff != null) {
            flags = ff.intValue();
        }
        if ((flags & 0x10000) != 0x0) {
            Image img;
            try {
                img = Image.getInstance(Base64.decodeBase64(value));
            }
            catch (final Exception e) {
                return false;
            }
            final PushbuttonField pb = this.getNewPushbuttonFromField(name);
            pb.setImage(img);
            this.replacePushbuttonField(name, pb.getField());
            return true;
        }
        final PdfName v2 = new PdfName(value);
        final ArrayList lopt = new ArrayList();
        final PdfArray opts = item.getValue(0).getAsArray(PdfName.OPT);
        if (opts != null) {
            for (int k = 0; k < opts.size(); ++k) {
                final PdfString valStr = opts.getAsString(k);
                if (valStr != null) {
                    lopt.add(valStr.toUnicodeString());
                }
                else {
                    lopt.add(null);
                }
            }
        }
        final int vidx = lopt.indexOf(value);
        PdfName vt;
        if (vidx >= 0) {
            vt = new PdfName(String.valueOf(vidx));
        }
        else {
            vt = v2;
        }
        for (int idx2 = 0; idx2 < item.size(); ++idx2) {
            merged = item.getMerged(idx2);
            final PdfDictionary widget2 = item.getWidget(idx2);
            final PdfDictionary valDict = item.getValue(idx2);
            this.markUsed(item.getValue(idx2));
            valDict.put(PdfName.V, vt);
            merged.put(PdfName.V, vt);
            this.markUsed(widget2);
            if (this.isInAP(widget2, vt)) {
                merged.put(PdfName.AS, vt);
                widget2.put(PdfName.AS, vt);
            }
            else {
                merged.put(PdfName.AS, PdfName.Off);
                widget2.put(PdfName.AS, PdfName.Off);
            }
        }
        return true;
    }
    
    public boolean setListSelection(final String name, final String[] value) throws IOException, DocumentException {
        final Item item = this.getFieldItem(name);
        if (item == null) {
            return false;
        }
        final PdfDictionary merged = item.getMerged(0);
        final PdfName type = merged.getAsName(PdfName.FT);
        if (!PdfName.CH.equals(type)) {
            return false;
        }
        final String[] options = this.getListOptionExport(name);
        final PdfArray array = new PdfArray();
        for (int i = 0; i < value.length; ++i) {
            for (int j = 0; j < options.length; ++j) {
                if (options[j].equals(value[i])) {
                    array.add(new PdfNumber(j));
                    break;
                }
            }
        }
        item.writeToAll(PdfName.I, array, 5);
        final PdfArray vals = new PdfArray();
        for (int k = 0; k < value.length; ++k) {
            vals.add(new PdfString(value[k]));
        }
        item.writeToAll(PdfName.V, vals, 5);
        final PdfAppearance app = this.getAppearance(merged, value, name);
        final PdfDictionary apDic = new PdfDictionary();
        apDic.put(PdfName.N, app.getIndirectReference());
        item.writeToAll(PdfName.AP, apDic, 3);
        this.writer.releaseTemplate(app);
        item.markUsed(this, 6);
        return true;
    }
    
    boolean isInAP(final PdfDictionary dic, final PdfName check) {
        final PdfDictionary appDic = dic.getAsDict(PdfName.AP);
        if (appDic == null) {
            return false;
        }
        final PdfDictionary NDic = appDic.getAsDict(PdfName.N);
        return NDic != null && NDic.get(check) != null;
    }
    
    public HashMap getFields() {
        return this.fields;
    }
    
    public Item getFieldItem(String name) {
        if (this.xfa.isXfaPresent()) {
            name = this.xfa.findFieldName(name, this);
            if (name == null) {
                return null;
            }
        }
        return this.fields.get(name);
    }
    
    public String getTranslatedFieldName(String name) {
        if (this.xfa.isXfaPresent()) {
            final String namex = this.xfa.findFieldName(name, this);
            if (namex != null) {
                name = namex;
            }
        }
        return name;
    }
    
    public float[] getFieldPositions(final String name) {
        final Item item = this.getFieldItem(name);
        if (item == null) {
            return null;
        }
        final float[] ret = new float[item.size() * 5];
        int ptr = 0;
        for (int k = 0; k < item.size(); ++k) {
            try {
                final PdfDictionary wd = item.getWidget(k);
                final PdfArray rect = wd.getAsArray(PdfName.RECT);
                if (rect != null) {
                    Rectangle r = PdfReader.getNormalizedRectangle(rect);
                    final int page = item.getPage(k);
                    final int rotation = this.reader.getPageRotation(page);
                    ret[ptr++] = (float)page;
                    if (rotation != 0) {
                        final Rectangle pageSize = this.reader.getPageSize(page);
                        switch (rotation) {
                            case 270: {
                                r = new Rectangle(pageSize.getTop() - r.getBottom(), r.getLeft(), pageSize.getTop() - r.getTop(), r.getRight());
                                break;
                            }
                            case 180: {
                                r = new Rectangle(pageSize.getRight() - r.getLeft(), pageSize.getTop() - r.getBottom(), pageSize.getRight() - r.getRight(), pageSize.getTop() - r.getTop());
                                break;
                            }
                            case 90: {
                                r = new Rectangle(r.getBottom(), pageSize.getRight() - r.getLeft(), r.getTop(), pageSize.getRight() - r.getRight());
                                break;
                            }
                        }
                        r.normalize();
                    }
                    ret[ptr++] = r.getLeft();
                    ret[ptr++] = r.getBottom();
                    ret[ptr++] = r.getRight();
                    ret[ptr++] = r.getTop();
                }
            }
            catch (final Exception ex) {}
        }
        if (ptr < ret.length) {
            final float[] ret2 = new float[ptr];
            System.arraycopy(ret, 0, ret2, 0, ptr);
            return ret2;
        }
        return ret;
    }
    
    private int removeRefFromArray(final PdfArray array, final PdfObject refo) {
        if (refo == null || !refo.isIndirect()) {
            return array.size();
        }
        final PdfIndirectReference ref = (PdfIndirectReference)refo;
        for (int j = 0; j < array.size(); ++j) {
            final PdfObject obj = array.getPdfObject(j);
            if (obj.isIndirect()) {
                if (((PdfIndirectReference)obj).getNumber() == ref.getNumber()) {
                    array.remove(j--);
                }
            }
        }
        return array.size();
    }
    
    public boolean removeFieldsFromPage(final int page) {
        if (page < 1) {
            return false;
        }
        final String[] names = new String[this.fields.size()];
        this.fields.keySet().toArray(names);
        boolean found = false;
        for (int k = 0; k < names.length; ++k) {
            final boolean fr = this.removeField(names[k], page);
            found = (found || fr);
        }
        return found;
    }
    
    public boolean removeField(final String name, final int page) {
        final Item item = this.getFieldItem(name);
        if (item == null) {
            return false;
        }
        final PdfDictionary acroForm = (PdfDictionary)PdfReader.getPdfObject(this.reader.getCatalog().get(PdfName.ACROFORM), this.reader.getCatalog());
        if (acroForm == null) {
            return false;
        }
        final PdfArray arrayf = acroForm.getAsArray(PdfName.FIELDS);
        if (arrayf == null) {
            return false;
        }
        for (int k = 0; k < item.size(); ++k) {
            final int pageV = item.getPage(k);
            if (page == -1 || page == pageV) {
                PdfIndirectReference ref = item.getWidgetRef(k);
                PdfDictionary wd = item.getWidget(k);
                final PdfDictionary pageDic = this.reader.getPageN(pageV);
                final PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
                if (annots != null) {
                    if (this.removeRefFromArray(annots, ref) == 0) {
                        pageDic.remove(PdfName.ANNOTS);
                        this.markUsed(pageDic);
                    }
                    else {
                        this.markUsed(annots);
                    }
                }
                PdfReader.killIndirect(ref);
                PdfIndirectReference kid = ref;
                while ((ref = wd.getAsIndirectObject(PdfName.PARENT)) != null) {
                    wd = wd.getAsDict(PdfName.PARENT);
                    final PdfArray kids = wd.getAsArray(PdfName.KIDS);
                    if (this.removeRefFromArray(kids, kid) != 0) {
                        break;
                    }
                    kid = ref;
                    PdfReader.killIndirect(ref);
                }
                if (ref == null) {
                    this.removeRefFromArray(arrayf, kid);
                    this.markUsed(arrayf);
                }
                if (page != -1) {
                    item.remove(k);
                    --k;
                }
            }
        }
        if (page == -1 || item.size() == 0) {
            this.fields.remove(name);
        }
        return true;
    }
    
    public boolean removeField(final String name) {
        return this.removeField(name, -1);
    }
    
    public boolean isGenerateAppearances() {
        return this.generateAppearances;
    }
    
    public void setGenerateAppearances(final boolean generateAppearances) {
        this.generateAppearances = generateAppearances;
        final PdfDictionary top = this.reader.getCatalog().getAsDict(PdfName.ACROFORM);
        if (generateAppearances) {
            top.remove(PdfName.NEEDAPPEARANCES);
        }
        else {
            top.put(PdfName.NEEDAPPEARANCES, PdfBoolean.PDFTRUE);
        }
    }
    
    public ArrayList getSignatureNames() {
        if (this.sigNames != null) {
            return new ArrayList(this.sigNames.keySet());
        }
        this.sigNames = new HashMap();
        final ArrayList sorter = new ArrayList();
        for (final Map.Entry entry : this.fields.entrySet()) {
            final Item item = entry.getValue();
            final PdfDictionary merged = item.getMerged(0);
            if (!PdfName.SIG.equals(merged.get(PdfName.FT))) {
                continue;
            }
            final PdfDictionary v = merged.getAsDict(PdfName.V);
            if (v == null) {
                continue;
            }
            final PdfString contents = v.getAsString(PdfName.CONTENTS);
            if (contents == null) {
                continue;
            }
            final PdfArray ro = v.getAsArray(PdfName.BYTERANGE);
            if (ro == null) {
                continue;
            }
            final int rangeSize = ro.size();
            if (rangeSize < 2) {
                continue;
            }
            final int length = ro.getAsNumber(rangeSize - 1).intValue() + ro.getAsNumber(rangeSize - 2).intValue();
            sorter.add(new Object[] { entry.getKey(), { length, 0 } });
        }
        Collections.sort((List<Object>)sorter, new SorterComparator());
        if (!sorter.isEmpty()) {
            if (((int[])((Object[])sorter.get(sorter.size() - 1))[1])[0] == this.reader.getFileLength()) {
                this.totalRevisions = sorter.size();
            }
            else {
                this.totalRevisions = sorter.size() + 1;
            }
            for (int k = 0; k < sorter.size(); ++k) {
                final Object[] objs = sorter.get(k);
                final String name = (String)objs[0];
                final int[] p = (int[])objs[1];
                p[1] = k + 1;
                this.sigNames.put(name, p);
            }
        }
        return new ArrayList(this.sigNames.keySet());
    }
    
    public ArrayList getBlankSignatureNames() {
        this.getSignatureNames();
        final ArrayList sigs = new ArrayList();
        for (final Map.Entry entry : this.fields.entrySet()) {
            final Item item = entry.getValue();
            final PdfDictionary merged = item.getMerged(0);
            if (!PdfName.SIG.equals(merged.getAsName(PdfName.FT))) {
                continue;
            }
            if (this.sigNames.containsKey(entry.getKey())) {
                continue;
            }
            sigs.add(entry.getKey());
        }
        return sigs;
    }
    
    public PdfDictionary getSignatureDictionary(String name) {
        this.getSignatureNames();
        name = this.getTranslatedFieldName(name);
        if (!this.sigNames.containsKey(name)) {
            return null;
        }
        final Item item = this.fields.get(name);
        final PdfDictionary merged = item.getMerged(0);
        return merged.getAsDict(PdfName.V);
    }
    
    public boolean signatureCoversWholeDocument(String name) {
        this.getSignatureNames();
        name = this.getTranslatedFieldName(name);
        return this.sigNames.containsKey(name) && ((int[])this.sigNames.get(name))[0] == this.reader.getFileLength();
    }
    
    public PdfPKCS7 verifySignature(final String name) {
        return this.verifySignature(name, null);
    }
    
    public PdfPKCS7 verifySignature(final String name, final String provider) {
        final PdfDictionary v = this.getSignatureDictionary(name);
        if (v == null) {
            return null;
        }
        try {
            final PdfName sub = v.getAsName(PdfName.SUBFILTER);
            final PdfString contents = v.getAsString(PdfName.CONTENTS);
            PdfPKCS7 pk = null;
            if (sub.equals(PdfName.ADBE_X509_RSA_SHA1)) {
                final PdfString cert = v.getAsString(PdfName.CERT);
                pk = new PdfPKCS7(contents.getOriginalBytes(), cert.getBytes(), provider);
            }
            else {
                pk = new PdfPKCS7(contents.getOriginalBytes(), provider);
            }
            this.updateByteRange(pk, v);
            PdfString str = v.getAsString(PdfName.M);
            if (str != null) {
                pk.setSignDate(PdfDate.decode(str.toString()));
            }
            final PdfObject obj = PdfReader.getPdfObject(v.get(PdfName.NAME));
            if (obj != null) {
                if (obj.isString()) {
                    pk.setSignName(((PdfString)obj).toUnicodeString());
                }
                else if (obj.isName()) {
                    pk.setSignName(PdfName.decodeName(obj.toString()));
                }
            }
            str = v.getAsString(PdfName.REASON);
            if (str != null) {
                pk.setReason(str.toUnicodeString());
            }
            str = v.getAsString(PdfName.LOCATION);
            if (str != null) {
                pk.setLocation(str.toUnicodeString());
            }
            return pk;
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    private void updateByteRange(final PdfPKCS7 pkcs7, final PdfDictionary v) {
        final PdfArray b = v.getAsArray(PdfName.BYTERANGE);
        final RandomAccessFileOrArray rf = this.reader.getSafeFile();
        try {
            rf.reOpen();
            final byte[] buf = new byte[8192];
            for (int k = 0; k < b.size(); ++k) {
                final int start = b.getAsNumber(k).intValue();
                int length = b.getAsNumber(++k).intValue();
                rf.seek(start);
                while (length > 0) {
                    final int rd = rf.read(buf, 0, Math.min(length, buf.length));
                    if (rd <= 0) {
                        break;
                    }
                    length -= rd;
                    pkcs7.update(buf, 0, rd);
                }
            }
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
        finally {
            try {
                rf.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    private void markUsed(final PdfObject obj) {
        if (!this.append) {
            return;
        }
        ((PdfStamperImp)this.writer).markUsed(obj);
    }
    
    public int getTotalRevisions() {
        this.getSignatureNames();
        return this.totalRevisions;
    }
    
    public int getRevision(String field) {
        this.getSignatureNames();
        field = this.getTranslatedFieldName(field);
        if (!this.sigNames.containsKey(field)) {
            return 0;
        }
        return ((int[])this.sigNames.get(field))[1];
    }
    
    public InputStream extractRevision(String field) throws IOException {
        this.getSignatureNames();
        field = this.getTranslatedFieldName(field);
        if (!this.sigNames.containsKey(field)) {
            return null;
        }
        final int length = ((int[])this.sigNames.get(field))[0];
        final RandomAccessFileOrArray raf = this.reader.getSafeFile();
        raf.reOpen();
        raf.seek(0);
        return new RevisionStream(raf, length);
    }
    
    public Map getFieldCache() {
        return this.fieldCache;
    }
    
    public void setFieldCache(final Map fieldCache) {
        this.fieldCache = fieldCache;
    }
    
    public void setExtraMargin(final float extraMarginLeft, final float extraMarginTop) {
        this.extraMarginLeft = extraMarginLeft;
        this.extraMarginTop = extraMarginTop;
    }
    
    public void addSubstitutionFont(final BaseFont font) {
        if (this.substitutionFonts == null) {
            this.substitutionFonts = new ArrayList();
        }
        this.substitutionFonts.add(font);
    }
    
    public ArrayList getSubstitutionFonts() {
        return this.substitutionFonts;
    }
    
    public void setSubstitutionFonts(final ArrayList substitutionFonts) {
        this.substitutionFonts = substitutionFonts;
    }
    
    public XfaForm getXfa() {
        return this.xfa;
    }
    
    public PushbuttonField getNewPushbuttonFromField(final String field) {
        return this.getNewPushbuttonFromField(field, 0);
    }
    
    public PushbuttonField getNewPushbuttonFromField(final String field, final int order) {
        try {
            if (this.getFieldType(field) != 1) {
                return null;
            }
            final Item item = this.getFieldItem(field);
            if (order >= item.size()) {
                return null;
            }
            final int posi = order * 5;
            final float[] pos = this.getFieldPositions(field);
            final Rectangle box = new Rectangle(pos[posi + 1], pos[posi + 2], pos[posi + 3], pos[posi + 4]);
            final PushbuttonField newButton = new PushbuttonField(this.writer, box, null);
            final PdfDictionary dic = item.getMerged(order);
            this.decodeGenericDictionary(dic, newButton);
            final PdfDictionary mk = dic.getAsDict(PdfName.MK);
            if (mk != null) {
                final PdfString text = mk.getAsString(PdfName.CA);
                if (text != null) {
                    newButton.setText(text.toUnicodeString());
                }
                final PdfNumber tp = mk.getAsNumber(PdfName.TP);
                if (tp != null) {
                    newButton.setLayout(tp.intValue() + 1);
                }
                final PdfDictionary ifit = mk.getAsDict(PdfName.IF);
                if (ifit != null) {
                    PdfName sw = ifit.getAsName(PdfName.SW);
                    if (sw != null) {
                        int scale = 1;
                        if (sw.equals(PdfName.B)) {
                            scale = 3;
                        }
                        else if (sw.equals(PdfName.S)) {
                            scale = 4;
                        }
                        else if (sw.equals(PdfName.N)) {
                            scale = 2;
                        }
                        newButton.setScaleIcon(scale);
                    }
                    sw = ifit.getAsName(PdfName.S);
                    if (sw != null && sw.equals(PdfName.A)) {
                        newButton.setProportionalIcon(false);
                    }
                    final PdfArray aj = ifit.getAsArray(PdfName.A);
                    if (aj != null && aj.size() == 2) {
                        final float left = aj.getAsNumber(0).floatValue();
                        final float bottom = aj.getAsNumber(1).floatValue();
                        newButton.setIconHorizontalAdjustment(left);
                        newButton.setIconVerticalAdjustment(bottom);
                    }
                    final PdfBoolean fb = ifit.getAsBoolean(PdfName.FB);
                    if (fb != null && fb.booleanValue()) {
                        newButton.setIconFitToBounds(true);
                    }
                }
                final PdfObject i = mk.get(PdfName.I);
                if (i != null && i.isIndirect()) {
                    newButton.setIconReference((PRIndirectReference)i);
                }
            }
            return newButton;
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public boolean replacePushbuttonField(final String field, final PdfFormField button) {
        return this.replacePushbuttonField(field, button, 0);
    }
    
    public boolean replacePushbuttonField(final String field, final PdfFormField button, final int order) {
        if (this.getFieldType(field) != 1) {
            return false;
        }
        final Item item = this.getFieldItem(field);
        if (order >= item.size()) {
            return false;
        }
        final PdfDictionary merged = item.getMerged(order);
        final PdfDictionary values = item.getValue(order);
        final PdfDictionary widgets = item.getWidget(order);
        for (int k = 0; k < AcroFields.buttonRemove.length; ++k) {
            merged.remove(AcroFields.buttonRemove[k]);
            values.remove(AcroFields.buttonRemove[k]);
            widgets.remove(AcroFields.buttonRemove[k]);
        }
        for (final PdfName key : button.getKeys()) {
            if (!key.equals(PdfName.T)) {
                if (key.equals(PdfName.RECT)) {
                    continue;
                }
                if (key.equals(PdfName.FF)) {
                    values.put(key, button.get(key));
                }
                else {
                    widgets.put(key, button.get(key));
                }
                merged.put(key, button.get(key));
            }
        }
        return true;
    }
    
    static {
        (stdFieldFontNames = new HashMap()).put("CoBO", new String[] { "Courier-BoldOblique" });
        AcroFields.stdFieldFontNames.put("CoBo", new String[] { "Courier-Bold" });
        AcroFields.stdFieldFontNames.put("CoOb", new String[] { "Courier-Oblique" });
        AcroFields.stdFieldFontNames.put("Cour", new String[] { "Courier" });
        AcroFields.stdFieldFontNames.put("HeBO", new String[] { "Helvetica-BoldOblique" });
        AcroFields.stdFieldFontNames.put("HeBo", new String[] { "Helvetica-Bold" });
        AcroFields.stdFieldFontNames.put("HeOb", new String[] { "Helvetica-Oblique" });
        AcroFields.stdFieldFontNames.put("Helv", new String[] { "Helvetica" });
        AcroFields.stdFieldFontNames.put("Symb", new String[] { "Symbol" });
        AcroFields.stdFieldFontNames.put("TiBI", new String[] { "Times-BoldItalic" });
        AcroFields.stdFieldFontNames.put("TiBo", new String[] { "Times-Bold" });
        AcroFields.stdFieldFontNames.put("TiIt", new String[] { "Times-Italic" });
        AcroFields.stdFieldFontNames.put("TiRo", new String[] { "Times-Roman" });
        AcroFields.stdFieldFontNames.put("ZaDb", new String[] { "ZapfDingbats" });
        AcroFields.stdFieldFontNames.put("HySm", new String[] { "HYSMyeongJo-Medium", "UniKS-UCS2-H" });
        AcroFields.stdFieldFontNames.put("HyGo", new String[] { "HYGoThic-Medium", "UniKS-UCS2-H" });
        AcroFields.stdFieldFontNames.put("KaGo", new String[] { "HeiseiKakuGo-W5", "UniKS-UCS2-H" });
        AcroFields.stdFieldFontNames.put("KaMi", new String[] { "HeiseiMin-W3", "UniJIS-UCS2-H" });
        AcroFields.stdFieldFontNames.put("MHei", new String[] { "MHei-Medium", "UniCNS-UCS2-H" });
        AcroFields.stdFieldFontNames.put("MSun", new String[] { "MSung-Light", "UniCNS-UCS2-H" });
        AcroFields.stdFieldFontNames.put("STSo", new String[] { "STSong-Light", "UniGB-UCS2-H" });
        buttonRemove = new PdfName[] { PdfName.MK, PdfName.F, PdfName.FF, PdfName.Q, PdfName.BS, PdfName.BORDER };
    }
    
    public static class Item
    {
        public static final int WRITE_MERGED = 1;
        public static final int WRITE_WIDGET = 2;
        public static final int WRITE_VALUE = 4;
        @Deprecated
        public ArrayList values;
        @Deprecated
        public ArrayList widgets;
        @Deprecated
        public ArrayList widget_refs;
        @Deprecated
        public ArrayList merged;
        @Deprecated
        public ArrayList page;
        @Deprecated
        public ArrayList tabOrder;
        
        public Item() {
            this.values = new ArrayList();
            this.widgets = new ArrayList();
            this.widget_refs = new ArrayList();
            this.merged = new ArrayList();
            this.page = new ArrayList();
            this.tabOrder = new ArrayList();
        }
        
        public void writeToAll(final PdfName key, final PdfObject value, final int writeFlags) {
            PdfDictionary curDict = null;
            if ((writeFlags & 0x1) != 0x0) {
                for (int i = 0; i < this.merged.size(); ++i) {
                    curDict = this.getMerged(i);
                    curDict.put(key, value);
                }
            }
            if ((writeFlags & 0x2) != 0x0) {
                for (int i = 0; i < this.widgets.size(); ++i) {
                    curDict = this.getWidget(i);
                    curDict.put(key, value);
                }
            }
            if ((writeFlags & 0x4) != 0x0) {
                for (int i = 0; i < this.values.size(); ++i) {
                    curDict = this.getValue(i);
                    curDict.put(key, value);
                }
            }
        }
        
        public void markUsed(final AcroFields parentFields, final int writeFlags) {
            if ((writeFlags & 0x4) != 0x0) {
                for (int i = 0; i < this.size(); ++i) {
                    parentFields.markUsed(this.getValue(i));
                }
            }
            if ((writeFlags & 0x2) != 0x0) {
                for (int i = 0; i < this.size(); ++i) {
                    parentFields.markUsed(this.getWidget(i));
                }
            }
        }
        
        public int size() {
            return this.values.size();
        }
        
        void remove(final int killIdx) {
            this.values.remove(killIdx);
            this.widgets.remove(killIdx);
            this.widget_refs.remove(killIdx);
            this.merged.remove(killIdx);
            this.page.remove(killIdx);
            this.tabOrder.remove(killIdx);
        }
        
        public PdfDictionary getValue(final int idx) {
            return this.values.get(idx);
        }
        
        void addValue(final PdfDictionary value) {
            this.values.add(value);
        }
        
        public PdfDictionary getWidget(final int idx) {
            return this.widgets.get(idx);
        }
        
        void addWidget(final PdfDictionary widget) {
            this.widgets.add(widget);
        }
        
        public PdfIndirectReference getWidgetRef(final int idx) {
            return this.widget_refs.get(idx);
        }
        
        void addWidgetRef(final PdfIndirectReference widgRef) {
            this.widget_refs.add(widgRef);
        }
        
        public PdfDictionary getMerged(final int idx) {
            return this.merged.get(idx);
        }
        
        void addMerged(final PdfDictionary mergeDict) {
            this.merged.add(mergeDict);
        }
        
        public Integer getPage(final int idx) {
            return this.page.get(idx);
        }
        
        void addPage(final int pg) {
            this.page.add(new Integer(pg));
        }
        
        void forcePage(final int idx, final int pg) {
            this.page.set(idx, new Integer(pg));
        }
        
        public Integer getTabOrder(final int idx) {
            return this.tabOrder.get(idx);
        }
        
        void addTabOrder(final int order) {
            this.tabOrder.add(new Integer(order));
        }
    }
    
    private static class InstHit
    {
        IntHashtable hits;
        
        public InstHit(final int[] inst) {
            if (inst == null) {
                return;
            }
            this.hits = new IntHashtable();
            for (int k = 0; k < inst.length; ++k) {
                this.hits.put(inst[k], 1);
            }
        }
        
        public boolean isHit(final int n) {
            return this.hits == null || this.hits.containsKey(n);
        }
    }
    
    private static class RevisionStream extends InputStream
    {
        private byte[] b;
        private RandomAccessFileOrArray raf;
        private int length;
        private int rangePosition;
        private boolean closed;
        
        private RevisionStream(final RandomAccessFileOrArray raf, final int length) {
            this.b = new byte[1];
            this.rangePosition = 0;
            this.raf = raf;
            this.length = length;
        }
        
        @Override
        public int read() throws IOException {
            final int n = this.read(this.b);
            if (n != 1) {
                return -1;
            }
            return this.b[0] & 0xFF;
        }
        
        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            }
            if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }
            if (this.rangePosition >= this.length) {
                this.close();
                return -1;
            }
            final int elen = Math.min(len, this.length - this.rangePosition);
            this.raf.readFully(b, off, elen);
            this.rangePosition += elen;
            return elen;
        }
        
        @Override
        public void close() throws IOException {
            if (!this.closed) {
                this.raf.close();
                this.closed = true;
            }
        }
    }
    
    private static class SorterComparator implements Comparator
    {
        @Override
        public int compare(final Object o1, final Object o2) {
            final int n1 = ((int[])((Object[])o1)[1])[0];
            final int n2 = ((int[])((Object[])o2)[1])[0];
            return n1 - n2;
        }
    }
}
