package com.lowagie.text.pdf;

import java.util.Map;
import java.util.Iterator;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.ArrayList;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.util.HashMap;
import com.lowagie.text.Rectangle;
import java.awt.Color;

public abstract class BaseField
{
    public static final float BORDER_WIDTH_THIN = 1.0f;
    public static final float BORDER_WIDTH_MEDIUM = 2.0f;
    public static final float BORDER_WIDTH_THICK = 3.0f;
    public static final int VISIBLE = 0;
    public static final int HIDDEN = 1;
    public static final int VISIBLE_BUT_DOES_NOT_PRINT = 2;
    public static final int HIDDEN_BUT_PRINTABLE = 3;
    public static final int READ_ONLY = 1;
    public static final int REQUIRED = 2;
    public static final int MULTILINE = 4096;
    public static final int DO_NOT_SCROLL = 8388608;
    public static final int PASSWORD = 8192;
    public static final int FILE_SELECTION = 1048576;
    public static final int DO_NOT_SPELL_CHECK = 4194304;
    public static final int EDIT = 262144;
    public static final int MULTISELECT = 2097152;
    public static final int COMB = 16777216;
    protected float borderWidth;
    protected int borderStyle;
    protected Color borderColor;
    protected Color backgroundColor;
    protected Color textColor;
    protected BaseFont font;
    protected float fontSize;
    protected int alignment;
    protected PdfWriter writer;
    protected String text;
    protected Rectangle box;
    protected int rotation;
    protected int visibility;
    protected String fieldName;
    protected int options;
    protected int maxCharacterLength;
    private static final HashMap fieldKeys;
    
    public BaseField(final PdfWriter writer, final Rectangle box, final String fieldName) {
        this.borderWidth = 1.0f;
        this.borderStyle = 0;
        this.fontSize = 0.0f;
        this.alignment = 0;
        this.rotation = 0;
        this.writer = writer;
        this.setBox(box);
        this.fieldName = fieldName;
    }
    
    protected BaseFont getRealFont() throws IOException, DocumentException {
        if (this.font == null) {
            return BaseFont.createFont("Helvetica", "Cp1252", false);
        }
        return this.font;
    }
    
    protected PdfAppearance getBorderAppearance() {
        final PdfAppearance app = PdfAppearance.createAppearance(this.writer, this.box.getWidth(), this.box.getHeight());
        switch (this.rotation) {
            case 90: {
                app.setMatrix(0.0f, 1.0f, -1.0f, 0.0f, this.box.getHeight(), 0.0f);
                break;
            }
            case 180: {
                app.setMatrix(-1.0f, 0.0f, 0.0f, -1.0f, this.box.getWidth(), this.box.getHeight());
                break;
            }
            case 270: {
                app.setMatrix(0.0f, -1.0f, 1.0f, 0.0f, 0.0f, this.box.getWidth());
                break;
            }
        }
        app.saveState();
        if (this.backgroundColor != null) {
            app.setColorFill(this.backgroundColor);
            app.rectangle(0.0f, 0.0f, this.box.getWidth(), this.box.getHeight());
            app.fill();
        }
        if (this.borderStyle == 4) {
            if (this.borderWidth != 0.0f && this.borderColor != null) {
                app.setColorStroke(this.borderColor);
                app.setLineWidth(this.borderWidth);
                app.moveTo(0.0f, this.borderWidth / 2.0f);
                app.lineTo(this.box.getWidth(), this.borderWidth / 2.0f);
                app.stroke();
            }
        }
        else if (this.borderStyle == 2) {
            if (this.borderWidth != 0.0f && this.borderColor != null) {
                app.setColorStroke(this.borderColor);
                app.setLineWidth(this.borderWidth);
                app.rectangle(this.borderWidth / 2.0f, this.borderWidth / 2.0f, this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
                app.stroke();
            }
            Color actual = this.backgroundColor;
            if (actual == null) {
                actual = Color.white;
            }
            app.setGrayFill(1.0f);
            this.drawTopFrame(app);
            app.setColorFill(actual.darker());
            this.drawBottomFrame(app);
        }
        else if (this.borderStyle == 3) {
            if (this.borderWidth != 0.0f && this.borderColor != null) {
                app.setColorStroke(this.borderColor);
                app.setLineWidth(this.borderWidth);
                app.rectangle(this.borderWidth / 2.0f, this.borderWidth / 2.0f, this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
                app.stroke();
            }
            app.setGrayFill(0.5f);
            this.drawTopFrame(app);
            app.setGrayFill(0.75f);
            this.drawBottomFrame(app);
        }
        else if (this.borderWidth != 0.0f && this.borderColor != null) {
            if (this.borderStyle == 1) {
                app.setLineDash(3.0f, 0.0f);
            }
            app.setColorStroke(this.borderColor);
            app.setLineWidth(this.borderWidth);
            app.rectangle(this.borderWidth / 2.0f, this.borderWidth / 2.0f, this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
            app.stroke();
            if ((this.options & 0x1000000) != 0x0 && this.maxCharacterLength > 1) {
                final float step = this.box.getWidth() / this.maxCharacterLength;
                final float yb = this.borderWidth / 2.0f;
                final float yt = this.box.getHeight() - this.borderWidth / 2.0f;
                for (int k = 1; k < this.maxCharacterLength; ++k) {
                    final float x = step * k;
                    app.moveTo(x, yb);
                    app.lineTo(x, yt);
                }
                app.stroke();
            }
        }
        app.restoreState();
        return app;
    }
    
    protected static ArrayList getHardBreaks(final String text) {
        final ArrayList arr = new ArrayList();
        final char[] cs = text.toCharArray();
        final int len = cs.length;
        StringBuffer buf = new StringBuffer();
        for (int k = 0; k < len; ++k) {
            final char c = cs[k];
            if (c == '\r') {
                if (k + 1 < len && cs[k + 1] == '\n') {
                    ++k;
                }
                arr.add(buf.toString());
                buf = new StringBuffer();
            }
            else if (c == '\n') {
                arr.add(buf.toString());
                buf = new StringBuffer();
            }
            else {
                buf.append(c);
            }
        }
        arr.add(buf.toString());
        return arr;
    }
    
    protected static void trimRight(final StringBuffer buf) {
        int len = buf.length();
        while (len != 0) {
            if (buf.charAt(--len) != ' ') {
                return;
            }
            buf.setLength(len);
        }
    }
    
    protected static ArrayList breakLines(final ArrayList breaks, final BaseFont font, final float fontSize, final float width) {
        final ArrayList lines = new ArrayList();
        final StringBuffer buf = new StringBuffer();
        for (int ck = 0; ck < breaks.size(); ++ck) {
            buf.setLength(0);
            float w = 0.0f;
            final char[] cs = breaks.get(ck).toCharArray();
            final int len = cs.length;
            int state = 0;
            int lastspace = -1;
            char c = '\0';
            int refk = 0;
            for (int k = 0; k < len; ++k) {
                c = cs[k];
                switch (state) {
                    case 0: {
                        w += font.getWidthPoint(c, fontSize);
                        buf.append(c);
                        if (w > width) {
                            w = 0.0f;
                            if (buf.length() > 1) {
                                --k;
                                buf.setLength(buf.length() - 1);
                            }
                            lines.add(buf.toString());
                            buf.setLength(0);
                            refk = k;
                            if (c == ' ') {
                                state = 2;
                                break;
                            }
                            state = 1;
                            break;
                        }
                        else {
                            if (c != ' ') {
                                state = 1;
                                break;
                            }
                            break;
                        }
                        break;
                    }
                    case 1: {
                        w += font.getWidthPoint(c, fontSize);
                        buf.append(c);
                        if (c == ' ') {
                            lastspace = k;
                        }
                        if (w <= width) {
                            break;
                        }
                        w = 0.0f;
                        if (lastspace >= 0) {
                            k = lastspace;
                            buf.setLength(lastspace - refk);
                            trimRight(buf);
                            lines.add(buf.toString());
                            buf.setLength(0);
                            refk = k;
                            lastspace = -1;
                            state = 2;
                            break;
                        }
                        if (buf.length() > 1) {
                            --k;
                            buf.setLength(buf.length() - 1);
                        }
                        lines.add(buf.toString());
                        buf.setLength(0);
                        refk = k;
                        if (c == ' ') {
                            state = 2;
                            break;
                        }
                        break;
                    }
                    case 2: {
                        if (c != ' ') {
                            w = 0.0f;
                            --k;
                            state = 1;
                            break;
                        }
                        break;
                    }
                }
            }
            trimRight(buf);
            lines.add(buf.toString());
        }
        return lines;
    }
    
    private void drawTopFrame(final PdfAppearance app) {
        app.moveTo(this.borderWidth, this.borderWidth);
        app.lineTo(this.borderWidth, this.box.getHeight() - this.borderWidth);
        app.lineTo(this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
        app.lineTo(this.box.getWidth() - 2.0f * this.borderWidth, this.box.getHeight() - 2.0f * this.borderWidth);
        app.lineTo(2.0f * this.borderWidth, this.box.getHeight() - 2.0f * this.borderWidth);
        app.lineTo(2.0f * this.borderWidth, 2.0f * this.borderWidth);
        app.lineTo(this.borderWidth, this.borderWidth);
        app.fill();
    }
    
    private void drawBottomFrame(final PdfAppearance app) {
        app.moveTo(this.borderWidth, this.borderWidth);
        app.lineTo(this.box.getWidth() - this.borderWidth, this.borderWidth);
        app.lineTo(this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
        app.lineTo(this.box.getWidth() - 2.0f * this.borderWidth, this.box.getHeight() - 2.0f * this.borderWidth);
        app.lineTo(this.box.getWidth() - 2.0f * this.borderWidth, 2.0f * this.borderWidth);
        app.lineTo(2.0f * this.borderWidth, 2.0f * this.borderWidth);
        app.lineTo(this.borderWidth, this.borderWidth);
        app.fill();
    }
    
    public float getBorderWidth() {
        return this.borderWidth;
    }
    
    public void setBorderWidth(final float borderWidth) {
        this.borderWidth = borderWidth;
    }
    
    public int getBorderStyle() {
        return this.borderStyle;
    }
    
    public void setBorderStyle(final int borderStyle) {
        this.borderStyle = borderStyle;
    }
    
    public Color getBorderColor() {
        return this.borderColor;
    }
    
    public void setBorderColor(final Color borderColor) {
        this.borderColor = borderColor;
    }
    
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }
    
    public void setBackgroundColor(final Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
    public Color getTextColor() {
        return this.textColor;
    }
    
    public void setTextColor(final Color textColor) {
        this.textColor = textColor;
    }
    
    public BaseFont getFont() {
        return this.font;
    }
    
    public void setFont(final BaseFont font) {
        this.font = font;
    }
    
    public float getFontSize() {
        return this.fontSize;
    }
    
    public void setFontSize(final float fontSize) {
        this.fontSize = fontSize;
    }
    
    public int getAlignment() {
        return this.alignment;
    }
    
    public void setAlignment(final int alignment) {
        this.alignment = alignment;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(final String text) {
        this.text = text;
    }
    
    public Rectangle getBox() {
        return this.box;
    }
    
    public void setBox(final Rectangle box) {
        if (box == null) {
            this.box = null;
        }
        else {
            (this.box = new Rectangle(box)).normalize();
        }
    }
    
    public int getRotation() {
        return this.rotation;
    }
    
    public void setRotation(int rotation) {
        if (rotation % 90 != 0) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("rotation.must.be.a.multiple.of.90"));
        }
        rotation %= 360;
        if (rotation < 0) {
            rotation += 360;
        }
        this.rotation = rotation;
    }
    
    public void setRotationFromPage(final Rectangle page) {
        this.setRotation(page.getRotation());
    }
    
    public int getVisibility() {
        return this.visibility;
    }
    
    public void setVisibility(final int visibility) {
        this.visibility = visibility;
    }
    
    public String getFieldName() {
        return this.fieldName;
    }
    
    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }
    
    public int getOptions() {
        return this.options;
    }
    
    public void setOptions(final int options) {
        this.options = options;
    }
    
    public int getMaxCharacterLength() {
        return this.maxCharacterLength;
    }
    
    public void setMaxCharacterLength(final int maxCharacterLength) {
        this.maxCharacterLength = maxCharacterLength;
    }
    
    public PdfWriter getWriter() {
        return this.writer;
    }
    
    public void setWriter(final PdfWriter writer) {
        this.writer = writer;
    }
    
    public static void moveFields(final PdfDictionary from, final PdfDictionary to) {
        final Iterator i = from.getKeys().iterator();
        while (i.hasNext()) {
            final PdfName key = i.next();
            if (BaseField.fieldKeys.containsKey(key)) {
                if (to != null) {
                    to.put(key, from.get(key));
                }
                i.remove();
            }
        }
    }
    
    static {
        (fieldKeys = new HashMap()).putAll(PdfCopyFieldsImp.fieldKeys);
        BaseField.fieldKeys.put(PdfName.T, new Integer(1));
    }
}
