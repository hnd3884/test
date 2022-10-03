package com.lowagie.text.pdf;

import java.awt.Color;
import com.lowagie.text.ExceptionConverter;
import java.util.StringTokenizer;
import com.lowagie.text.Rectangle;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class PdfAcroForm extends PdfDictionary
{
    private PdfWriter writer;
    private HashMap fieldTemplates;
    private PdfArray documentFields;
    private PdfArray calculationOrder;
    private int sigFlags;
    
    public PdfAcroForm(final PdfWriter writer) {
        this.fieldTemplates = new HashMap();
        this.documentFields = new PdfArray();
        this.calculationOrder = new PdfArray();
        this.sigFlags = 0;
        this.writer = writer;
    }
    
    public void setNeedAppearances(final boolean value) {
        this.put(PdfName.NEEDAPPEARANCES, new PdfBoolean(value));
    }
    
    public void addFieldTemplates(final HashMap ft) {
        this.fieldTemplates.putAll(ft);
    }
    
    public void addDocumentField(final PdfIndirectReference ref) {
        this.documentFields.add(ref);
    }
    
    public boolean isValid() {
        if (this.documentFields.size() == 0) {
            return false;
        }
        this.put(PdfName.FIELDS, this.documentFields);
        if (this.sigFlags != 0) {
            this.put(PdfName.SIGFLAGS, new PdfNumber(this.sigFlags));
        }
        if (this.calculationOrder.size() > 0) {
            this.put(PdfName.CO, this.calculationOrder);
        }
        if (this.fieldTemplates.isEmpty()) {
            return true;
        }
        final PdfDictionary dic = new PdfDictionary();
        for (final PdfTemplate template : this.fieldTemplates.keySet()) {
            PdfFormField.mergeResources(dic, (PdfDictionary)template.getResources());
        }
        this.put(PdfName.DR, dic);
        this.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
        final PdfDictionary fonts = (PdfDictionary)dic.get(PdfName.FONT);
        if (fonts != null) {
            this.writer.eliminateFontSubset(fonts);
        }
        return true;
    }
    
    public void addCalculationOrder(final PdfFormField formField) {
        this.calculationOrder.add(formField.getIndirectReference());
    }
    
    public void setSigFlags(final int f) {
        this.sigFlags |= f;
    }
    
    public void addFormField(final PdfFormField formField) {
        this.writer.addAnnotation(formField);
    }
    
    public PdfFormField addHtmlPostButton(final String name, final String caption, final String value, final String url, final BaseFont font, final float fontSize, final float llx, final float lly, final float urx, final float ury) {
        final PdfAction action = PdfAction.createSubmitForm(url, null, 4);
        final PdfFormField button = new PdfFormField(this.writer, llx, lly, urx, ury, action);
        this.setButtonParams(button, 65536, name, value);
        this.drawButton(button, caption, font, fontSize, llx, lly, urx, ury);
        this.addFormField(button);
        return button;
    }
    
    public PdfFormField addResetButton(final String name, final String caption, final String value, final BaseFont font, final float fontSize, final float llx, final float lly, final float urx, final float ury) {
        final PdfAction action = PdfAction.createResetForm(null, 0);
        final PdfFormField button = new PdfFormField(this.writer, llx, lly, urx, ury, action);
        this.setButtonParams(button, 65536, name, value);
        this.drawButton(button, caption, font, fontSize, llx, lly, urx, ury);
        this.addFormField(button);
        return button;
    }
    
    public PdfFormField addMap(final String name, final String value, final String url, final PdfContentByte appearance, final float llx, final float lly, final float urx, final float ury) {
        final PdfAction action = PdfAction.createSubmitForm(url, null, 20);
        final PdfFormField button = new PdfFormField(this.writer, llx, lly, urx, ury, action);
        this.setButtonParams(button, 65536, name, null);
        final PdfAppearance pa = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        pa.add(appearance);
        button.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, pa);
        this.addFormField(button);
        return button;
    }
    
    public void setButtonParams(final PdfFormField button, final int characteristics, final String name, final String value) {
        button.setButton(characteristics);
        button.setFlags(4);
        button.setPage();
        button.setFieldName(name);
        if (value != null) {
            button.setValueAsString(value);
        }
    }
    
    public void drawButton(final PdfFormField button, final String caption, final BaseFont font, final float fontSize, final float llx, final float lly, final float urx, final float ury) {
        final PdfAppearance pa = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        pa.drawButton(0.0f, 0.0f, urx - llx, ury - lly, caption, font, fontSize);
        button.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, pa);
    }
    
    public PdfFormField addHiddenField(final String name, final String value) {
        final PdfFormField hidden = PdfFormField.createEmpty(this.writer);
        hidden.setFieldName(name);
        hidden.setValueAsName(value);
        this.addFormField(hidden);
        return hidden;
    }
    
    public PdfFormField addSingleLineTextField(final String name, final String text, final BaseFont font, final float fontSize, final float llx, final float lly, final float urx, final float ury) {
        final PdfFormField field = PdfFormField.createTextField(this.writer, false, false, 0);
        this.setTextFieldParams(field, text, name, llx, lly, urx, ury);
        this.drawSingleLineOfText(field, text, font, fontSize, llx, lly, urx, ury);
        this.addFormField(field);
        return field;
    }
    
    public PdfFormField addMultiLineTextField(final String name, final String text, final BaseFont font, final float fontSize, final float llx, final float lly, final float urx, final float ury) {
        final PdfFormField field = PdfFormField.createTextField(this.writer, true, false, 0);
        this.setTextFieldParams(field, text, name, llx, lly, urx, ury);
        this.drawMultiLineOfText(field, text, font, fontSize, llx, lly, urx, ury);
        this.addFormField(field);
        return field;
    }
    
    public PdfFormField addSingleLinePasswordField(final String name, final String text, final BaseFont font, final float fontSize, final float llx, final float lly, final float urx, final float ury) {
        final PdfFormField field = PdfFormField.createTextField(this.writer, false, true, 0);
        this.setTextFieldParams(field, text, name, llx, lly, urx, ury);
        this.drawSingleLineOfText(field, text, font, fontSize, llx, lly, urx, ury);
        this.addFormField(field);
        return field;
    }
    
    public void setTextFieldParams(final PdfFormField field, final String text, final String name, final float llx, final float lly, final float urx, final float ury) {
        field.setWidget(new Rectangle(llx, lly, urx, ury), PdfAnnotation.HIGHLIGHT_INVERT);
        field.setValueAsString(text);
        field.setDefaultValueAsString(text);
        field.setFieldName(name);
        field.setFlags(4);
        field.setPage();
    }
    
    public void drawSingleLineOfText(final PdfFormField field, final String text, final BaseFont font, final float fontSize, final float llx, final float lly, final float urx, final float ury) {
        final PdfAppearance tp = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        final PdfAppearance tp2 = (PdfAppearance)tp.getDuplicate();
        tp2.setFontAndSize(font, fontSize);
        tp2.resetRGBColorFill();
        field.setDefaultAppearanceString(tp2);
        tp.drawTextField(0.0f, 0.0f, urx - llx, ury - lly);
        tp.beginVariableText();
        tp.saveState();
        tp.rectangle(3.0f, 3.0f, urx - llx - 6.0f, ury - lly - 6.0f);
        tp.clip();
        tp.newPath();
        tp.beginText();
        tp.setFontAndSize(font, fontSize);
        tp.resetRGBColorFill();
        tp.setTextMatrix(4.0f, (ury - lly) / 2.0f - fontSize * 0.3f);
        tp.showText(text);
        tp.endText();
        tp.restoreState();
        tp.endVariableText();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
    }
    
    public void drawMultiLineOfText(final PdfFormField field, final String text, final BaseFont font, final float fontSize, final float llx, final float lly, final float urx, final float ury) {
        final PdfAppearance tp = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        final PdfAppearance tp2 = (PdfAppearance)tp.getDuplicate();
        tp2.setFontAndSize(font, fontSize);
        tp2.resetRGBColorFill();
        field.setDefaultAppearanceString(tp2);
        tp.drawTextField(0.0f, 0.0f, urx - llx, ury - lly);
        tp.beginVariableText();
        tp.saveState();
        tp.rectangle(3.0f, 3.0f, urx - llx - 6.0f, ury - lly - 6.0f);
        tp.clip();
        tp.newPath();
        tp.beginText();
        tp.setFontAndSize(font, fontSize);
        tp.resetRGBColorFill();
        tp.setTextMatrix(4.0f, 5.0f);
        final StringTokenizer tokenizer = new StringTokenizer(text, "\n");
        float yPos = ury - lly;
        while (tokenizer.hasMoreTokens()) {
            yPos -= fontSize * 1.2f;
            tp.showTextAligned(0, tokenizer.nextToken(), 3.0f, yPos, 0.0f);
        }
        tp.endText();
        tp.restoreState();
        tp.endVariableText();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
    }
    
    public PdfFormField addCheckBox(final String name, final String value, final boolean status, final float llx, final float lly, final float urx, final float ury) {
        final PdfFormField field = PdfFormField.createCheckBox(this.writer);
        this.setCheckBoxParams(field, name, value, status, llx, lly, urx, ury);
        this.drawCheckBoxAppearences(field, value, llx, lly, urx, ury);
        this.addFormField(field);
        return field;
    }
    
    public void setCheckBoxParams(final PdfFormField field, final String name, final String value, final boolean status, final float llx, final float lly, final float urx, final float ury) {
        field.setWidget(new Rectangle(llx, lly, urx, ury), PdfAnnotation.HIGHLIGHT_TOGGLE);
        field.setFieldName(name);
        if (status) {
            field.setValueAsName(value);
            field.setAppearanceState(value);
        }
        else {
            field.setValueAsName("Off");
            field.setAppearanceState("Off");
        }
        field.setFlags(4);
        field.setPage();
        field.setBorderStyle(new PdfBorderDictionary(1.0f, 0));
    }
    
    public void drawCheckBoxAppearences(final PdfFormField field, final String value, final float llx, final float lly, final float urx, final float ury) {
        BaseFont font = null;
        try {
            font = BaseFont.createFont("ZapfDingbats", "Cp1252", false);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
        final float size = ury - lly;
        final PdfAppearance tpOn = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        final PdfAppearance tp2 = (PdfAppearance)tpOn.getDuplicate();
        tp2.setFontAndSize(font, size);
        tp2.resetRGBColorFill();
        field.setDefaultAppearanceString(tp2);
        tpOn.drawTextField(0.0f, 0.0f, urx - llx, ury - lly);
        tpOn.saveState();
        tpOn.resetRGBColorFill();
        tpOn.beginText();
        tpOn.setFontAndSize(font, size);
        tpOn.showTextAligned(1, "4", (urx - llx) / 2.0f, (ury - lly) / 2.0f - size * 0.3f, 0.0f);
        tpOn.endText();
        tpOn.restoreState();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, value, tpOn);
        final PdfAppearance tpOff = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        tpOff.drawTextField(0.0f, 0.0f, urx - llx, ury - lly);
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", tpOff);
    }
    
    public PdfFormField getRadioGroup(final String name, final String defaultValue, final boolean noToggleToOff) {
        final PdfFormField radio = PdfFormField.createRadioButton(this.writer, noToggleToOff);
        radio.setFieldName(name);
        radio.setValueAsName(defaultValue);
        return radio;
    }
    
    public void addRadioGroup(final PdfFormField radiogroup) {
        this.addFormField(radiogroup);
    }
    
    public PdfFormField addRadioButton(final PdfFormField radiogroup, final String value, final float llx, final float lly, final float urx, final float ury) {
        final PdfFormField radio = PdfFormField.createEmpty(this.writer);
        radio.setWidget(new Rectangle(llx, lly, urx, ury), PdfAnnotation.HIGHLIGHT_TOGGLE);
        final String name = radiogroup.get(PdfName.V).toString().substring(1);
        if (name.equals(value)) {
            radio.setAppearanceState(value);
        }
        else {
            radio.setAppearanceState("Off");
        }
        this.drawRadioAppearences(radio, value, llx, lly, urx, ury);
        radiogroup.addKid(radio);
        return radio;
    }
    
    public void drawRadioAppearences(final PdfFormField field, final String value, final float llx, final float lly, final float urx, final float ury) {
        final PdfAppearance tpOn = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        tpOn.drawRadioField(0.0f, 0.0f, urx - llx, ury - lly, true);
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, value, tpOn);
        final PdfAppearance tpOff = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        tpOff.drawRadioField(0.0f, 0.0f, urx - llx, ury - lly, false);
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", tpOff);
    }
    
    public PdfFormField addSelectList(final String name, final String[] options, final String defaultValue, final BaseFont font, final float fontSize, final float llx, final float lly, final float urx, final float ury) {
        final PdfFormField choice = PdfFormField.createList(this.writer, options, 0);
        this.setChoiceParams(choice, name, defaultValue, llx, lly, urx, ury);
        final StringBuffer text = new StringBuffer();
        for (int i = 0; i < options.length; ++i) {
            text.append(options[i]).append('\n');
        }
        this.drawMultiLineOfText(choice, text.toString(), font, fontSize, llx, lly, urx, ury);
        this.addFormField(choice);
        return choice;
    }
    
    public PdfFormField addSelectList(final String name, final String[][] options, final String defaultValue, final BaseFont font, final float fontSize, final float llx, final float lly, final float urx, final float ury) {
        final PdfFormField choice = PdfFormField.createList(this.writer, options, 0);
        this.setChoiceParams(choice, name, defaultValue, llx, lly, urx, ury);
        final StringBuffer text = new StringBuffer();
        for (int i = 0; i < options.length; ++i) {
            text.append(options[i][1]).append('\n');
        }
        this.drawMultiLineOfText(choice, text.toString(), font, fontSize, llx, lly, urx, ury);
        this.addFormField(choice);
        return choice;
    }
    
    public PdfFormField addComboBox(final String name, final String[] options, String defaultValue, final boolean editable, final BaseFont font, final float fontSize, final float llx, final float lly, final float urx, final float ury) {
        final PdfFormField choice = PdfFormField.createCombo(this.writer, editable, options, 0);
        this.setChoiceParams(choice, name, defaultValue, llx, lly, urx, ury);
        if (defaultValue == null) {
            defaultValue = options[0];
        }
        this.drawSingleLineOfText(choice, defaultValue, font, fontSize, llx, lly, urx, ury);
        this.addFormField(choice);
        return choice;
    }
    
    public PdfFormField addComboBox(final String name, final String[][] options, final String defaultValue, final boolean editable, final BaseFont font, final float fontSize, final float llx, final float lly, final float urx, final float ury) {
        final PdfFormField choice = PdfFormField.createCombo(this.writer, editable, options, 0);
        this.setChoiceParams(choice, name, defaultValue, llx, lly, urx, ury);
        String value = null;
        for (int i = 0; i < options.length; ++i) {
            if (options[i][0].equals(defaultValue)) {
                value = options[i][1];
                break;
            }
        }
        if (value == null) {
            value = options[0][1];
        }
        this.drawSingleLineOfText(choice, value, font, fontSize, llx, lly, urx, ury);
        this.addFormField(choice);
        return choice;
    }
    
    public void setChoiceParams(final PdfFormField field, final String name, final String defaultValue, final float llx, final float lly, final float urx, final float ury) {
        field.setWidget(new Rectangle(llx, lly, urx, ury), PdfAnnotation.HIGHLIGHT_INVERT);
        if (defaultValue != null) {
            field.setValueAsString(defaultValue);
            field.setDefaultValueAsString(defaultValue);
        }
        field.setFieldName(name);
        field.setFlags(4);
        field.setPage();
        field.setBorderStyle(new PdfBorderDictionary(2.0f, 0));
    }
    
    public PdfFormField addSignature(final String name, final float llx, final float lly, final float urx, final float ury) {
        final PdfFormField signature = PdfFormField.createSignature(this.writer);
        this.setSignatureParams(signature, name, llx, lly, urx, ury);
        this.drawSignatureAppearences(signature, llx, lly, urx, ury);
        this.addFormField(signature);
        return signature;
    }
    
    public void setSignatureParams(final PdfFormField field, final String name, final float llx, final float lly, final float urx, final float ury) {
        field.setWidget(new Rectangle(llx, lly, urx, ury), PdfAnnotation.HIGHLIGHT_INVERT);
        field.setFieldName(name);
        field.setFlags(4);
        field.setPage();
        field.setMKBorderColor(Color.black);
        field.setMKBackgroundColor(Color.white);
    }
    
    public void drawSignatureAppearences(final PdfFormField field, final float llx, final float lly, final float urx, final float ury) {
        final PdfAppearance tp = PdfAppearance.createAppearance(this.writer, urx - llx, ury - lly);
        tp.setGrayFill(1.0f);
        tp.rectangle(0.0f, 0.0f, urx - llx, ury - lly);
        tp.fill();
        tp.setGrayStroke(0.0f);
        tp.setLineWidth(1.0f);
        tp.rectangle(0.5f, 0.5f, urx - llx - 0.5f, ury - lly - 0.5f);
        tp.closePathStroke();
        tp.saveState();
        tp.rectangle(1.0f, 1.0f, urx - llx - 2.0f, ury - lly - 2.0f);
        tp.clip();
        tp.newPath();
        tp.restoreState();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
    }
}
