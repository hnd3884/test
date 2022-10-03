package com.lowagie.text;

import com.lowagie.text.pdf.PdfAnnotation;
import java.net.URL;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.HyphenationEvent;
import java.awt.Color;
import java.util.ArrayList;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.draw.DrawInterface;
import java.util.Map;
import java.util.HashMap;

public class Chunk implements Element
{
    public static final String OBJECT_REPLACEMENT_CHARACTER = "\ufffc";
    public static final Chunk NEWLINE;
    public static final Chunk NEXTPAGE;
    protected StringBuffer content;
    protected Font font;
    protected HashMap attributes;
    public static final String SEPARATOR = "SEPARATOR";
    public static final String TAB = "TAB";
    public static final String HSCALE = "HSCALE";
    public static final String UNDERLINE = "UNDERLINE";
    public static final String SUBSUPSCRIPT = "SUBSUPSCRIPT";
    public static final String SKEW = "SKEW";
    public static final String BACKGROUND = "BACKGROUND";
    public static final String TEXTRENDERMODE = "TEXTRENDERMODE";
    public static final String SPLITCHARACTER = "SPLITCHARACTER";
    public static final String HYPHENATION = "HYPHENATION";
    public static final String REMOTEGOTO = "REMOTEGOTO";
    public static final String LOCALGOTO = "LOCALGOTO";
    public static final String LOCALDESTINATION = "LOCALDESTINATION";
    public static final String GENERICTAG = "GENERICTAG";
    public static final String IMAGE = "IMAGE";
    public static final String ACTION = "ACTION";
    public static final String NEWPAGE = "NEWPAGE";
    public static final String PDFANNOTATION = "PDFANNOTATION";
    public static final String COLOR = "COLOR";
    public static final String ENCODING = "ENCODING";
    public static final String CHAR_SPACING = "CHAR_SPACING";
    
    public Chunk() {
        this.content = null;
        this.font = null;
        this.attributes = null;
        this.content = new StringBuffer();
        this.font = new Font();
    }
    
    public Chunk(final Chunk ck) {
        this.content = null;
        this.font = null;
        this.attributes = null;
        if (ck.content != null) {
            this.content = new StringBuffer(ck.content.toString());
        }
        if (ck.font != null) {
            this.font = new Font(ck.font);
        }
        if (ck.attributes != null) {
            this.attributes = new HashMap(ck.attributes);
        }
    }
    
    public Chunk(final String content, final Font font) {
        this.content = null;
        this.font = null;
        this.attributes = null;
        this.content = new StringBuffer(content);
        this.font = font;
    }
    
    public Chunk(final String content) {
        this(content, new Font());
    }
    
    public Chunk(final char c, final Font font) {
        this.content = null;
        this.font = null;
        this.attributes = null;
        (this.content = new StringBuffer()).append(c);
        this.font = font;
    }
    
    public Chunk(final char c) {
        this(c, new Font());
    }
    
    public Chunk(final Image image, final float offsetX, final float offsetY) {
        this("\ufffc", new Font());
        final Image copyImage = Image.getInstance(image);
        copyImage.setAbsolutePosition(Float.NaN, Float.NaN);
        this.setAttribute("IMAGE", new Object[] { copyImage, new Float(offsetX), new Float(offsetY), Boolean.FALSE });
    }
    
    public Chunk(final DrawInterface separator) {
        this(separator, false);
    }
    
    public Chunk(final DrawInterface separator, final boolean vertical) {
        this("\ufffc", new Font());
        this.setAttribute("SEPARATOR", new Object[] { separator, vertical });
    }
    
    public Chunk(final DrawInterface separator, final float tabPosition) {
        this(separator, tabPosition, false);
    }
    
    public Chunk(final DrawInterface separator, final float tabPosition, final boolean newline) {
        this("\ufffc", new Font());
        if (tabPosition < 0.0f) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("a.tab.position.may.not.be.lower.than.0.yours.is.1", String.valueOf(tabPosition)));
        }
        this.setAttribute("TAB", new Object[] { separator, new Float(tabPosition), newline, new Float(0.0f) });
    }
    
    public Chunk(final Image image, final float offsetX, final float offsetY, final boolean changeLeading) {
        this("\ufffc", new Font());
        this.setAttribute("IMAGE", new Object[] { image, new Float(offsetX), new Float(offsetY), changeLeading });
    }
    
    @Override
    public boolean process(final ElementListener listener) {
        try {
            return listener.add(this);
        }
        catch (final DocumentException de) {
            return false;
        }
    }
    
    @Override
    public int type() {
        return 10;
    }
    
    @Override
    public ArrayList getChunks() {
        final ArrayList tmp = new ArrayList();
        tmp.add(this);
        return tmp;
    }
    
    public StringBuffer append(final String string) {
        return this.content.append(string);
    }
    
    public void setFont(final Font font) {
        this.font = font;
    }
    
    public Font getFont() {
        return this.font;
    }
    
    public String getContent() {
        return this.content.toString();
    }
    
    @Override
    public String toString() {
        return this.getContent();
    }
    
    public boolean isEmpty() {
        return this.content.toString().trim().length() == 0 && this.content.toString().indexOf("\n") == -1 && this.attributes == null;
    }
    
    public float getWidthPoint() {
        if (this.getImage() != null) {
            return this.getImage().getScaledWidth();
        }
        return this.font.getCalculatedBaseFont(true).getWidthPoint(this.getContent(), this.font.getCalculatedSize()) * this.getHorizontalScaling();
    }
    
    public boolean hasAttributes() {
        return this.attributes != null;
    }
    
    public HashMap getAttributes() {
        return this.attributes;
    }
    
    public void setAttributes(final HashMap attributes) {
        this.attributes = attributes;
    }
    
    private Chunk setAttribute(final String name, final Object obj) {
        if (this.attributes == null) {
            this.attributes = new HashMap();
        }
        this.attributes.put(name, obj);
        return this;
    }
    
    public Chunk setHorizontalScaling(final float scale) {
        return this.setAttribute("HSCALE", new Float(scale));
    }
    
    public float getHorizontalScaling() {
        if (this.attributes == null) {
            return 1.0f;
        }
        final Float f = this.attributes.get("HSCALE");
        if (f == null) {
            return 1.0f;
        }
        return f;
    }
    
    public Chunk setUnderline(final float thickness, final float yPosition) {
        return this.setUnderline(null, thickness, 0.0f, yPosition, 0.0f, 0);
    }
    
    public Chunk setUnderline(final Color color, final float thickness, final float thicknessMul, final float yPosition, final float yPositionMul, final int cap) {
        if (this.attributes == null) {
            this.attributes = new HashMap();
        }
        final Object[] obj = { color, { thickness, thicknessMul, yPosition, yPositionMul, (float)cap } };
        final Object[][] unders = Utilities.addToArray(this.attributes.get("UNDERLINE"), obj);
        return this.setAttribute("UNDERLINE", unders);
    }
    
    public Chunk setTextRise(final float rise) {
        return this.setAttribute("SUBSUPSCRIPT", new Float(rise));
    }
    
    public float getTextRise() {
        if (this.attributes != null && this.attributes.containsKey("SUBSUPSCRIPT")) {
            final Float f = this.attributes.get("SUBSUPSCRIPT");
            return f;
        }
        return 0.0f;
    }
    
    public Chunk setSkew(float alpha, float beta) {
        alpha = (float)Math.tan(alpha * 3.141592653589793 / 180.0);
        beta = (float)Math.tan(beta * 3.141592653589793 / 180.0);
        return this.setAttribute("SKEW", new float[] { alpha, beta });
    }
    
    public Chunk setBackground(final Color color) {
        return this.setBackground(color, 0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    public Chunk setBackground(final Color color, final float extraLeft, final float extraBottom, final float extraRight, final float extraTop) {
        return this.setAttribute("BACKGROUND", new Object[] { color, { extraLeft, extraBottom, extraRight, extraTop } });
    }
    
    public Chunk setTextRenderMode(final int mode, final float strokeWidth, final Color strokeColor) {
        return this.setAttribute("TEXTRENDERMODE", new Object[] { new Integer(mode), new Float(strokeWidth), strokeColor });
    }
    
    public Chunk setSplitCharacter(final SplitCharacter splitCharacter) {
        return this.setAttribute("SPLITCHARACTER", splitCharacter);
    }
    
    public Chunk setHyphenation(final HyphenationEvent hyphenation) {
        return this.setAttribute("HYPHENATION", hyphenation);
    }
    
    public Chunk setRemoteGoto(final String filename, final String name) {
        return this.setAttribute("REMOTEGOTO", new Object[] { filename, name });
    }
    
    public Chunk setRemoteGoto(final String filename, final int page) {
        return this.setAttribute("REMOTEGOTO", new Object[] { filename, new Integer(page) });
    }
    
    public Chunk setLocalGoto(final String name) {
        return this.setAttribute("LOCALGOTO", name);
    }
    
    public Chunk setLocalDestination(final String name) {
        return this.setAttribute("LOCALDESTINATION", name);
    }
    
    public Chunk setGenericTag(final String text) {
        return this.setAttribute("GENERICTAG", text);
    }
    
    public Image getImage() {
        if (this.attributes == null) {
            return null;
        }
        final Object[] obj = this.attributes.get("IMAGE");
        if (obj == null) {
            return null;
        }
        return (Image)obj[0];
    }
    
    public Chunk setAction(final PdfAction action) {
        return this.setAttribute("ACTION", action);
    }
    
    public Chunk setAnchor(final URL url) {
        return this.setAttribute("ACTION", new PdfAction(url.toExternalForm()));
    }
    
    public Chunk setAnchor(final String url) {
        return this.setAttribute("ACTION", new PdfAction(url));
    }
    
    public Chunk setNewPage() {
        return this.setAttribute("NEWPAGE", null);
    }
    
    public Chunk setAnnotation(final PdfAnnotation annotation) {
        return this.setAttribute("PDFANNOTATION", annotation);
    }
    
    @Override
    public boolean isContent() {
        return true;
    }
    
    @Override
    public boolean isNestable() {
        return true;
    }
    
    public HyphenationEvent getHyphenation() {
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.get("HYPHENATION");
    }
    
    public Chunk setCharacterSpacing(final float charSpace) {
        return this.setAttribute("CHAR_SPACING", new Float(charSpace));
    }
    
    public float getCharacterSpacing() {
        if (this.attributes != null && this.attributes.containsKey("CHAR_SPACING")) {
            final Float f = this.attributes.get("CHAR_SPACING");
            return f;
        }
        return 0.0f;
    }
    
    static {
        NEWLINE = new Chunk("\n");
        (NEXTPAGE = new Chunk("")).setNewPage();
    }
}
