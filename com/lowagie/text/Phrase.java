package com.lowagie.text;

import com.lowagie.text.error_messages.MessageLocalization;
import java.util.Iterator;
import java.util.Collection;
import com.lowagie.text.pdf.HyphenationEvent;
import java.util.ArrayList;

public class Phrase extends ArrayList implements TextElementArray
{
    private static final long serialVersionUID = 2643594602455068231L;
    protected float leading;
    protected Font font;
    protected HyphenationEvent hyphenation;
    
    public Phrase() {
        this(16.0f);
    }
    
    public Phrase(final Phrase phrase) {
        this.leading = Float.NaN;
        this.hyphenation = null;
        this.addAll(phrase);
        this.leading = phrase.getLeading();
        this.font = phrase.getFont();
        this.setHyphenation(phrase.getHyphenation());
    }
    
    public Phrase(final float leading) {
        this.leading = Float.NaN;
        this.hyphenation = null;
        this.leading = leading;
        this.font = new Font();
    }
    
    public Phrase(final Chunk chunk) {
        this.leading = Float.NaN;
        this.hyphenation = null;
        super.add(chunk);
        this.font = chunk.getFont();
        this.setHyphenation(chunk.getHyphenation());
    }
    
    public Phrase(final float leading, final Chunk chunk) {
        this.leading = Float.NaN;
        this.hyphenation = null;
        this.leading = leading;
        super.add(chunk);
        this.font = chunk.getFont();
        this.setHyphenation(chunk.getHyphenation());
    }
    
    public Phrase(final String string) {
        this(Float.NaN, string, new Font());
    }
    
    public Phrase(final String string, final Font font) {
        this(Float.NaN, string, font);
    }
    
    public Phrase(final float leading, final String string) {
        this(leading, string, new Font());
    }
    
    public Phrase(final float leading, final String string, final Font font) {
        this.leading = Float.NaN;
        this.hyphenation = null;
        this.leading = leading;
        this.font = font;
        if (string != null && string.length() != 0) {
            super.add(new Chunk(string, font));
        }
    }
    
    @Override
    public boolean process(final ElementListener listener) {
        try {
            final Iterator i = this.iterator();
            while (i.hasNext()) {
                listener.add(i.next());
            }
            return true;
        }
        catch (final DocumentException de) {
            return false;
        }
    }
    
    @Override
    public int type() {
        return 11;
    }
    
    @Override
    public ArrayList getChunks() {
        final ArrayList tmp = new ArrayList();
        final Iterator i = this.iterator();
        while (i.hasNext()) {
            tmp.addAll(i.next().getChunks());
        }
        return tmp;
    }
    
    @Override
    public boolean isContent() {
        return true;
    }
    
    @Override
    public boolean isNestable() {
        return true;
    }
    
    @Override
    public void add(final int index, final Object o) {
        if (o == null) {
            return;
        }
        try {
            final Element element = (Element)o;
            if (element.type() == 10) {
                final Chunk chunk = (Chunk)element;
                if (!this.font.isStandardFont()) {
                    chunk.setFont(this.font.difference(chunk.getFont()));
                }
                if (this.hyphenation != null && chunk.getHyphenation() == null && !chunk.isEmpty()) {
                    chunk.setHyphenation(this.hyphenation);
                }
                super.add(index, chunk);
            }
            else {
                if (element.type() != 11 && element.type() != 17 && element.type() != 29 && element.type() != 22 && element.type() != 55 && element.type() != 50) {
                    throw new ClassCastException(String.valueOf(element.type()));
                }
                super.add(index, element);
            }
        }
        catch (final ClassCastException cce) {
            throw new ClassCastException(MessageLocalization.getComposedMessage("insertion.of.illegal.element.1", cce.getMessage()));
        }
    }
    
    @Override
    public boolean add(final Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof String) {
            return super.add(new Chunk((String)o, this.font));
        }
        if (o instanceof RtfElementInterface) {
            return super.add(o);
        }
        try {
            final Element element = (Element)o;
            switch (element.type()) {
                case 10: {
                    return this.addChunk((Chunk)o);
                }
                case 11:
                case 12: {
                    final Phrase phrase = (Phrase)o;
                    boolean success = true;
                    for (final Element e : phrase) {
                        if (e instanceof Chunk) {
                            success &= this.addChunk((Chunk)e);
                        }
                        else {
                            success &= this.add(e);
                        }
                    }
                    return success;
                }
                case 14:
                case 17:
                case 22:
                case 23:
                case 29:
                case 50:
                case 55:
                case 56: {
                    return super.add(o);
                }
                default: {
                    throw new ClassCastException(String.valueOf(element.type()));
                }
            }
        }
        catch (final ClassCastException cce) {
            throw new ClassCastException(MessageLocalization.getComposedMessage("insertion.of.illegal.element.1", cce.getMessage()));
        }
    }
    
    @Override
    public boolean addAll(final Collection collection) {
        final Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            this.add(iterator.next());
        }
        return true;
    }
    
    protected boolean addChunk(final Chunk chunk) {
        Font f = chunk.getFont();
        final String c = chunk.getContent();
        if (this.font != null && !this.font.isStandardFont()) {
            f = this.font.difference(chunk.getFont());
        }
        if (this.size() > 0 && !chunk.hasAttributes()) {
            try {
                final Chunk previous = this.get(this.size() - 1);
                if (!previous.hasAttributes() && (f == null || f.compareTo(previous.getFont()) == 0) && !"".equals(previous.getContent().trim()) && !"".equals(c.trim())) {
                    previous.append(c);
                    return true;
                }
            }
            catch (final ClassCastException ex) {}
        }
        final Chunk newChunk = new Chunk(c, f);
        newChunk.setAttributes(chunk.getAttributes());
        if (this.hyphenation != null && newChunk.getHyphenation() == null && !newChunk.isEmpty()) {
            newChunk.setHyphenation(this.hyphenation);
        }
        return super.add(newChunk);
    }
    
    protected void addSpecial(final Object object) {
        super.add(object);
    }
    
    public void setLeading(final float leading) {
        this.leading = leading;
    }
    
    public void setFont(final Font font) {
        this.font = font;
    }
    
    public float getLeading() {
        if (Float.isNaN(this.leading) && this.font != null) {
            return this.font.getCalculatedLeading(1.5f);
        }
        return this.leading;
    }
    
    public boolean hasLeading() {
        return !Float.isNaN(this.leading);
    }
    
    public Font getFont() {
        return this.font;
    }
    
    public String getContent() {
        final StringBuffer buf = new StringBuffer();
        final Iterator i = this.getChunks().iterator();
        while (i.hasNext()) {
            buf.append(i.next().toString());
        }
        return buf.toString();
    }
    
    @Override
    public boolean isEmpty() {
        switch (this.size()) {
            case 0: {
                return true;
            }
            case 1: {
                final Element element = this.get(0);
                return element.type() == 10 && ((Chunk)element).isEmpty();
            }
            default: {
                return false;
            }
        }
    }
    
    public HyphenationEvent getHyphenation() {
        return this.hyphenation;
    }
    
    public void setHyphenation(final HyphenationEvent hyphenation) {
        this.hyphenation = hyphenation;
    }
    
    private Phrase(final boolean dummy) {
        this.leading = Float.NaN;
        this.hyphenation = null;
    }
    
    public static final Phrase getInstance(final String string) {
        return getInstance(16, string, new Font());
    }
    
    public static final Phrase getInstance(final int leading, final String string) {
        return getInstance(leading, string, new Font());
    }
    
    public static final Phrase getInstance(final int leading, String string, final Font font) {
        final Phrase p = new Phrase(true);
        p.setLeading((float)leading);
        p.font = font;
        if (font.getFamily() != 3 && font.getFamily() != 4 && font.getBaseFont() == null) {
            int index;
            while ((index = SpecialSymbol.index(string)) > -1) {
                if (index > 0) {
                    final String firstPart = string.substring(0, index);
                    p.add(new Chunk(firstPart, font));
                    string = string.substring(index);
                }
                final Font symbol = new Font(3, font.getSize(), font.getStyle(), font.getColor());
                final StringBuffer buf = new StringBuffer();
                buf.append(SpecialSymbol.getCorrespondingSymbol(string.charAt(0)));
                for (string = string.substring(1); SpecialSymbol.index(string) == 0; string = string.substring(1)) {
                    buf.append(SpecialSymbol.getCorrespondingSymbol(string.charAt(0)));
                }
                p.add(new Chunk(buf.toString(), symbol));
            }
        }
        if (string != null && string.length() != 0) {
            p.add(new Chunk(string, font));
        }
        return p;
    }
}
