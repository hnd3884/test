package com.lowagie.text.pdf;

import java.util.Collection;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import com.lowagie.text.Font;
import java.awt.Color;
import com.lowagie.text.Chunk;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import java.util.ArrayList;

public class TextField extends BaseField
{
    private String defaultText;
    private String[] choices;
    private String[] choiceExports;
    private ArrayList choiceSelections;
    private int topFirst;
    private float extraMarginLeft;
    private float extraMarginTop;
    private ArrayList substitutionFonts;
    private BaseFont extensionFont;
    
    public TextField(final PdfWriter writer, final Rectangle box, final String fieldName) {
        super(writer, box, fieldName);
        this.choiceSelections = new ArrayList();
    }
    
    private static boolean checkRTL(final String text) {
        if (text == null || text.length() == 0) {
            return false;
        }
        final char[] cc = text.toCharArray();
        for (int k = 0; k < cc.length; ++k) {
            final int c = cc[k];
            if (c >= 1424 && c < 1920) {
                return true;
            }
        }
        return false;
    }
    
    private static void changeFontSize(final Phrase p, final float size) {
        for (int k = 0; k < p.size(); ++k) {
            p.get(k).getFont().setSize(size);
        }
    }
    
    private Phrase composePhrase(final String text, final BaseFont ufont, final Color color, final float fontSize) {
        Phrase phrase = null;
        if (this.extensionFont == null && (this.substitutionFonts == null || this.substitutionFonts.isEmpty())) {
            phrase = new Phrase(new Chunk(text, new Font(ufont, fontSize, 0, color)));
        }
        else {
            final FontSelector fs = new FontSelector();
            fs.addFont(new Font(ufont, fontSize, 0, color));
            if (this.extensionFont != null) {
                fs.addFont(new Font(this.extensionFont, fontSize, 0, color));
            }
            if (this.substitutionFonts != null) {
                for (int k = 0; k < this.substitutionFonts.size(); ++k) {
                    fs.addFont(new Font(this.substitutionFonts.get(k), fontSize, 0, color));
                }
            }
            phrase = fs.process(text);
        }
        return phrase;
    }
    
    public static String removeCRLF(final String text) {
        if (text.indexOf(10) >= 0 || text.indexOf(13) >= 0) {
            final char[] p = text.toCharArray();
            final StringBuffer sb = new StringBuffer(p.length);
            for (int k = 0; k < p.length; ++k) {
                final char c = p[k];
                if (c == '\n') {
                    sb.append(' ');
                }
                else if (c == '\r') {
                    sb.append(' ');
                    if (k < p.length - 1 && p[k + 1] == '\n') {
                        ++k;
                    }
                }
                else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return text;
    }
    
    public static String obfuscatePassword(final String text) {
        final char[] pchar = new char[text.length()];
        for (int i = 0; i < text.length(); ++i) {
            pchar[i] = '*';
        }
        return new String(pchar);
    }
    
    public PdfAppearance getAppearance() throws IOException, DocumentException {
        final PdfAppearance app = this.getBorderAppearance();
        app.beginVariableText();
        if (this.text == null || this.text.length() == 0) {
            app.endVariableText();
            return app;
        }
        final boolean borderExtra = this.borderStyle == 2 || this.borderStyle == 3;
        float h = this.box.getHeight() - this.borderWidth * 2.0f - this.extraMarginTop;
        float bw2 = this.borderWidth;
        if (borderExtra) {
            h -= this.borderWidth * 2.0f;
            bw2 *= 2.0f;
        }
        final float offsetX = Math.max(bw2, 1.0f);
        final float offX = Math.min(bw2, offsetX);
        app.saveState();
        app.rectangle(offX, offX, this.box.getWidth() - 2.0f * offX, this.box.getHeight() - 2.0f * offX);
        app.clip();
        app.newPath();
        String ptext;
        if ((this.options & 0x2000) != 0x0) {
            ptext = obfuscatePassword(this.text);
        }
        else if ((this.options & 0x1000) == 0x0) {
            ptext = removeCRLF(this.text);
        }
        else {
            ptext = this.text;
        }
        final BaseFont ufont = this.getRealFont();
        final Color fcolor = (this.textColor == null) ? GrayColor.GRAYBLACK : this.textColor;
        final int rtl = checkRTL(ptext) ? 2 : 1;
        float usize = this.fontSize;
        final Phrase phrase = this.composePhrase(ptext, ufont, fcolor, usize);
        if ((this.options & 0x1000) != 0x0) {
            final float width = this.box.getWidth() - 4.0f * offsetX - this.extraMarginLeft;
            final float factor = ufont.getFontDescriptor(8, 1.0f) - ufont.getFontDescriptor(6, 1.0f);
            final ColumnText ct = new ColumnText(null);
            if (usize == 0.0f) {
                usize = h / factor;
                if (usize > 4.0f) {
                    if (usize > 12.0f) {
                        usize = 12.0f;
                    }
                    final float step = Math.max((usize - 4.0f) / 10.0f, 0.2f);
                    ct.setSimpleColumn(0.0f, -h, width, 0.0f);
                    ct.setAlignment(this.alignment);
                    ct.setRunDirection(rtl);
                    while (usize > 4.0f) {
                        ct.setYLine(0.0f);
                        changeFontSize(phrase, usize);
                        ct.setText(phrase);
                        ct.setLeading(factor * usize);
                        final int status = ct.go(true);
                        if ((status & 0x2) == 0x0) {
                            break;
                        }
                        usize -= step;
                    }
                }
                if (usize < 4.0f) {
                    usize = 4.0f;
                }
            }
            changeFontSize(phrase, usize);
            ct.setCanvas(app);
            final float leading = usize * factor;
            final float offsetY = offsetX + h - ufont.getFontDescriptor(8, usize);
            ct.setSimpleColumn(this.extraMarginLeft + 2.0f * offsetX, -20000.0f, this.box.getWidth() - 2.0f * offsetX, offsetY + leading);
            ct.setLeading(leading);
            ct.setAlignment(this.alignment);
            ct.setRunDirection(rtl);
            ct.setText(phrase);
            ct.go();
        }
        else {
            if (usize == 0.0f) {
                final float maxCalculatedSize = h / (ufont.getFontDescriptor(7, 1.0f) - ufont.getFontDescriptor(6, 1.0f));
                changeFontSize(phrase, 1.0f);
                final float wd = ColumnText.getWidth(phrase, rtl, 0);
                if (wd == 0.0f) {
                    usize = maxCalculatedSize;
                }
                else {
                    usize = Math.min(maxCalculatedSize, (this.box.getWidth() - this.extraMarginLeft - 4.0f * offsetX) / wd);
                }
                if (usize < 4.0f) {
                    usize = 4.0f;
                }
            }
            changeFontSize(phrase, usize);
            float offsetY2 = offX + (this.box.getHeight() - 2.0f * offX - ufont.getFontDescriptor(1, usize)) / 2.0f;
            if (offsetY2 < offX) {
                offsetY2 = offX;
            }
            if (offsetY2 - offX < -ufont.getFontDescriptor(3, usize)) {
                final float ny = -ufont.getFontDescriptor(3, usize) + offX;
                final float dy = this.box.getHeight() - offX - ufont.getFontDescriptor(1, usize);
                offsetY2 = Math.min(ny, Math.max(offsetY2, dy));
            }
            if ((this.options & 0x1000000) != 0x0 && this.maxCharacterLength > 0) {
                final int textLen = Math.min(this.maxCharacterLength, ptext.length());
                int position = 0;
                if (this.alignment == 2) {
                    position = this.maxCharacterLength - textLen;
                }
                else if (this.alignment == 1) {
                    position = (this.maxCharacterLength - textLen) / 2;
                }
                final float step = (this.box.getWidth() - this.extraMarginLeft) / this.maxCharacterLength;
                float start = step / 2.0f + position * step;
                if (this.textColor == null) {
                    app.setGrayFill(0.0f);
                }
                else {
                    app.setColorFill(this.textColor);
                }
                app.beginText();
                for (int k = 0; k < phrase.size(); ++k) {
                    final Chunk ck = phrase.get(k);
                    final BaseFont bf = ck.getFont().getBaseFont();
                    app.setFontAndSize(bf, usize);
                    final StringBuffer sb = ck.append("");
                    for (int j = 0; j < sb.length(); ++j) {
                        final String c = sb.substring(j, j + 1);
                        final float wd2 = bf.getWidthPoint(c, usize);
                        app.setTextMatrix(this.extraMarginLeft + start - wd2 / 2.0f, offsetY2 - this.extraMarginTop);
                        app.showText(c);
                        start += step;
                    }
                }
                app.endText();
            }
            else {
                float x = 0.0f;
                switch (this.alignment) {
                    case 2: {
                        x = this.extraMarginLeft + this.box.getWidth() - 2.0f * offsetX;
                        break;
                    }
                    case 1: {
                        x = this.extraMarginLeft + this.box.getWidth() / 2.0f;
                        break;
                    }
                    default: {
                        x = this.extraMarginLeft + 2.0f * offsetX;
                        break;
                    }
                }
                ColumnText.showTextAligned(app, this.alignment, phrase, x, offsetY2 - this.extraMarginTop, 0.0f, rtl, 0);
            }
        }
        app.restoreState();
        app.endVariableText();
        return app;
    }
    
    PdfAppearance getListAppearance() throws IOException, DocumentException {
        final PdfAppearance app = this.getBorderAppearance();
        if (this.choices == null || this.choices.length == 0) {
            return app;
        }
        app.beginVariableText();
        final int topChoice = this.getTopChoice();
        final BaseFont ufont = this.getRealFont();
        float usize = this.fontSize;
        if (usize == 0.0f) {
            usize = 12.0f;
        }
        final boolean borderExtra = this.borderStyle == 2 || this.borderStyle == 3;
        float h = this.box.getHeight() - this.borderWidth * 2.0f;
        float offsetX = this.borderWidth;
        if (borderExtra) {
            h -= this.borderWidth * 2.0f;
            offsetX *= 2.0f;
        }
        final float leading = ufont.getFontDescriptor(8, usize) - ufont.getFontDescriptor(6, usize);
        final int maxFit = (int)(h / leading) + 1;
        int first = 0;
        int last = 0;
        first = topChoice;
        last = first + maxFit;
        if (last > this.choices.length) {
            last = this.choices.length;
        }
        this.topFirst = first;
        app.saveState();
        app.rectangle(offsetX, offsetX, this.box.getWidth() - 2.0f * offsetX, this.box.getHeight() - 2.0f * offsetX);
        app.clip();
        app.newPath();
        final Color fcolor = (this.textColor == null) ? GrayColor.GRAYBLACK : this.textColor;
        app.setColorFill(new Color(10, 36, 106));
        for (int curVal = 0; curVal < this.choiceSelections.size(); ++curVal) {
            final int curChoice = this.choiceSelections.get(curVal);
            if (curChoice >= first && curChoice <= last) {
                app.rectangle(offsetX, offsetX + h - (curChoice - first + 1) * leading, this.box.getWidth() - 2.0f * offsetX, leading);
                app.fill();
            }
        }
        final float xp = offsetX * 2.0f;
        float yp = offsetX + h - ufont.getFontDescriptor(8, usize);
        for (int idx = first; idx < last; ++idx, yp -= leading) {
            String ptext = this.choices[idx];
            final int rtl = checkRTL(ptext) ? 2 : 1;
            ptext = removeCRLF(ptext);
            final Color textCol = this.choiceSelections.contains(new Integer(idx)) ? GrayColor.GRAYWHITE : fcolor;
            final Phrase phrase = this.composePhrase(ptext, ufont, textCol, usize);
            ColumnText.showTextAligned(app, 0, phrase, xp, yp, 0.0f, rtl, 0);
        }
        app.restoreState();
        app.endVariableText();
        return app;
    }
    
    public PdfFormField getTextField() throws IOException, DocumentException {
        if (this.maxCharacterLength <= 0) {
            this.options &= 0xFEFFFFFF;
        }
        if ((this.options & 0x1000000) != 0x0) {
            this.options &= 0xFFFFEFFF;
        }
        final PdfFormField field = PdfFormField.createTextField(this.writer, false, false, this.maxCharacterLength);
        field.setWidget(this.box, PdfAnnotation.HIGHLIGHT_INVERT);
        switch (this.alignment) {
            case 1: {
                field.setQuadding(1);
                break;
            }
            case 2: {
                field.setQuadding(2);
                break;
            }
        }
        if (this.rotation != 0) {
            field.setMKRotation(this.rotation);
        }
        if (this.fieldName != null) {
            field.setFieldName(this.fieldName);
            if (!"".equals(this.text)) {
                field.setValueAsString(this.text);
            }
            if (this.defaultText != null) {
                field.setDefaultValueAsString(this.defaultText);
            }
            if ((this.options & 0x1) != 0x0) {
                field.setFieldFlags(1);
            }
            if ((this.options & 0x2) != 0x0) {
                field.setFieldFlags(2);
            }
            if ((this.options & 0x1000) != 0x0) {
                field.setFieldFlags(4096);
            }
            if ((this.options & 0x800000) != 0x0) {
                field.setFieldFlags(8388608);
            }
            if ((this.options & 0x2000) != 0x0) {
                field.setFieldFlags(8192);
            }
            if ((this.options & 0x100000) != 0x0) {
                field.setFieldFlags(1048576);
            }
            if ((this.options & 0x400000) != 0x0) {
                field.setFieldFlags(4194304);
            }
            if ((this.options & 0x1000000) != 0x0) {
                field.setFieldFlags(16777216);
            }
        }
        field.setBorderStyle(new PdfBorderDictionary(this.borderWidth, this.borderStyle, new PdfDashPattern(3.0f)));
        final PdfAppearance tp = this.getAppearance();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
        final PdfAppearance da = (PdfAppearance)tp.getDuplicate();
        da.setFontAndSize(this.getRealFont(), this.fontSize);
        if (this.textColor == null) {
            da.setGrayFill(0.0f);
        }
        else {
            da.setColorFill(this.textColor);
        }
        field.setDefaultAppearanceString(da);
        if (this.borderColor != null) {
            field.setMKBorderColor(this.borderColor);
        }
        if (this.backgroundColor != null) {
            field.setMKBackgroundColor(this.backgroundColor);
        }
        switch (this.visibility) {
            case 1: {
                field.setFlags(6);
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                field.setFlags(36);
                break;
            }
            default: {
                field.setFlags(4);
                break;
            }
        }
        return field;
    }
    
    public PdfFormField getComboField() throws IOException, DocumentException {
        return this.getChoiceField(false);
    }
    
    public PdfFormField getListField() throws IOException, DocumentException {
        return this.getChoiceField(true);
    }
    
    private int getTopChoice() {
        if (this.choiceSelections == null || this.choiceSelections.size() == 0) {
            return 0;
        }
        final Integer firstValue = this.choiceSelections.get(0);
        if (firstValue == null) {
            return 0;
        }
        int topChoice = 0;
        if (this.choices != null) {
            topChoice = firstValue;
            topChoice = Math.min(topChoice, this.choices.length);
            topChoice = Math.max(0, topChoice);
        }
        return topChoice;
    }
    
    protected PdfFormField getChoiceField(final boolean isList) throws IOException, DocumentException {
        this.options &= 0xFEFFEFFF;
        String[] uchoices = this.choices;
        if (uchoices == null) {
            uchoices = new String[0];
        }
        final int topChoice = this.getTopChoice();
        if (this.text == null) {
            this.text = "";
        }
        if (topChoice >= 0) {
            this.text = uchoices[topChoice];
        }
        PdfFormField field = null;
        String[][] mix = null;
        if (this.choiceExports == null) {
            if (isList) {
                field = PdfFormField.createList(this.writer, uchoices, topChoice);
            }
            else {
                field = PdfFormField.createCombo(this.writer, (this.options & 0x40000) != 0x0, uchoices, topChoice);
            }
        }
        else {
            mix = new String[uchoices.length][2];
            for (int k = 0; k < mix.length; ++k) {
                mix[k][0] = (mix[k][1] = uchoices[k]);
            }
            for (int top = Math.min(uchoices.length, this.choiceExports.length), i = 0; i < top; ++i) {
                if (this.choiceExports[i] != null) {
                    mix[i][0] = this.choiceExports[i];
                }
            }
            if (isList) {
                field = PdfFormField.createList(this.writer, mix, topChoice);
            }
            else {
                field = PdfFormField.createCombo(this.writer, (this.options & 0x40000) != 0x0, mix, topChoice);
            }
        }
        field.setWidget(this.box, PdfAnnotation.HIGHLIGHT_INVERT);
        if (this.rotation != 0) {
            field.setMKRotation(this.rotation);
        }
        if (this.fieldName != null) {
            field.setFieldName(this.fieldName);
            if (uchoices.length > 0) {
                if (mix != null) {
                    if (this.choiceSelections.size() < 2) {
                        field.setValueAsString(mix[topChoice][0]);
                        field.setDefaultValueAsString(mix[topChoice][0]);
                    }
                    else {
                        this.writeMultipleValues(field, mix);
                    }
                }
                else if (this.choiceSelections.size() < 2) {
                    field.setValueAsString(this.text);
                    field.setDefaultValueAsString(this.text);
                }
                else {
                    this.writeMultipleValues(field, null);
                }
            }
            if ((this.options & 0x1) != 0x0) {
                field.setFieldFlags(1);
            }
            if ((this.options & 0x2) != 0x0) {
                field.setFieldFlags(2);
            }
            if ((this.options & 0x400000) != 0x0) {
                field.setFieldFlags(4194304);
            }
            if ((this.options & 0x200000) != 0x0) {
                field.setFieldFlags(2097152);
            }
        }
        field.setBorderStyle(new PdfBorderDictionary(this.borderWidth, this.borderStyle, new PdfDashPattern(3.0f)));
        PdfAppearance tp;
        if (isList) {
            tp = this.getListAppearance();
            if (this.topFirst > 0) {
                field.put(PdfName.TI, new PdfNumber(this.topFirst));
            }
        }
        else {
            tp = this.getAppearance();
        }
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
        final PdfAppearance da = (PdfAppearance)tp.getDuplicate();
        da.setFontAndSize(this.getRealFont(), this.fontSize);
        if (this.textColor == null) {
            da.setGrayFill(0.0f);
        }
        else {
            da.setColorFill(this.textColor);
        }
        field.setDefaultAppearanceString(da);
        if (this.borderColor != null) {
            field.setMKBorderColor(this.borderColor);
        }
        if (this.backgroundColor != null) {
            field.setMKBackgroundColor(this.backgroundColor);
        }
        switch (this.visibility) {
            case 1: {
                field.setFlags(6);
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                field.setFlags(36);
                break;
            }
            default: {
                field.setFlags(4);
                break;
            }
        }
        return field;
    }
    
    private void writeMultipleValues(final PdfFormField field, final String[][] mix) {
        final PdfArray indexes = new PdfArray();
        final PdfArray values = new PdfArray();
        for (int i = 0; i < this.choiceSelections.size(); ++i) {
            final int idx = this.choiceSelections.get(i);
            indexes.add(new PdfNumber(idx));
            if (mix != null) {
                values.add(new PdfString(mix[idx][0]));
            }
            else if (this.choices != null) {
                values.add(new PdfString(this.choices[idx]));
            }
        }
        field.put(PdfName.V, values);
        field.put(PdfName.I, indexes);
    }
    
    public String getDefaultText() {
        return this.defaultText;
    }
    
    public void setDefaultText(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String[] getChoices() {
        return this.choices;
    }
    
    public void setChoices(final String[] choices) {
        this.choices = choices;
    }
    
    public String[] getChoiceExports() {
        return this.choiceExports;
    }
    
    public void setChoiceExports(final String[] choiceExports) {
        this.choiceExports = choiceExports;
    }
    
    public int getChoiceSelection() {
        return this.getTopChoice();
    }
    
    public ArrayList gteChoiceSelections() {
        return this.choiceSelections;
    }
    
    public void setChoiceSelection(final int choiceSelection) {
        (this.choiceSelections = new ArrayList()).add(new Integer(choiceSelection));
    }
    
    public void addChoiceSelection(final int selection) {
        if ((this.options & 0x200000) != 0x0) {
            this.choiceSelections.add(new Integer(selection));
        }
    }
    
    public void setChoiceSelections(final ArrayList selections) {
        if (selections != null) {
            this.choiceSelections = new ArrayList(selections);
            if (this.choiceSelections.size() > 1 && (this.options & 0x200000) == 0x0) {
                while (this.choiceSelections.size() > 1) {
                    this.choiceSelections.remove(1);
                }
            }
        }
        else {
            this.choiceSelections.clear();
        }
    }
    
    int getTopFirst() {
        return this.topFirst;
    }
    
    public void setExtraMargin(final float extraMarginLeft, final float extraMarginTop) {
        this.extraMarginLeft = extraMarginLeft;
        this.extraMarginTop = extraMarginTop;
    }
    
    public ArrayList getSubstitutionFonts() {
        return this.substitutionFonts;
    }
    
    public void setSubstitutionFonts(final ArrayList substitutionFonts) {
        this.substitutionFonts = substitutionFonts;
    }
    
    public BaseFont getExtensionFont() {
        return this.extensionFont;
    }
    
    public void setExtensionFont(final BaseFont extensionFont) {
        this.extensionFont = extensionFont;
    }
}
