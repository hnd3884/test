package com.lowagie.text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class Anchor extends Phrase
{
    private static final long serialVersionUID = -852278536049236911L;
    protected String name;
    protected String reference;
    
    public Anchor() {
        super(16.0f);
        this.name = null;
        this.reference = null;
    }
    
    public Anchor(final float leading) {
        super(leading);
        this.name = null;
        this.reference = null;
    }
    
    public Anchor(final Chunk chunk) {
        super(chunk);
        this.name = null;
        this.reference = null;
    }
    
    public Anchor(final String string) {
        super(string);
        this.name = null;
        this.reference = null;
    }
    
    public Anchor(final String string, final Font font) {
        super(string, font);
        this.name = null;
        this.reference = null;
    }
    
    public Anchor(final float leading, final Chunk chunk) {
        super(leading, chunk);
        this.name = null;
        this.reference = null;
    }
    
    public Anchor(final float leading, final String string) {
        super(leading, string);
        this.name = null;
        this.reference = null;
    }
    
    public Anchor(final float leading, final String string, final Font font) {
        super(leading, string, font);
        this.name = null;
        this.reference = null;
    }
    
    public Anchor(final Phrase phrase) {
        super(phrase);
        this.name = null;
        this.reference = null;
        if (phrase instanceof Anchor) {
            final Anchor a = (Anchor)phrase;
            this.setName(a.name);
            this.setReference(a.reference);
        }
    }
    
    @Override
    public boolean process(final ElementListener listener) {
        try {
            final Iterator i = this.getChunks().iterator();
            final boolean localDestination = this.reference != null && this.reference.startsWith("#");
            boolean notGotoOK = true;
            while (i.hasNext()) {
                final Chunk chunk = i.next();
                if (this.name != null && notGotoOK && !chunk.isEmpty()) {
                    chunk.setLocalDestination(this.name);
                    notGotoOK = false;
                }
                if (localDestination) {
                    chunk.setLocalGoto(this.reference.substring(1));
                }
                listener.add(chunk);
            }
            return true;
        }
        catch (final DocumentException de) {
            return false;
        }
    }
    
    @Override
    public ArrayList getChunks() {
        final ArrayList tmp = new ArrayList();
        final Iterator i = this.iterator();
        final boolean localDestination = this.reference != null && this.reference.startsWith("#");
        boolean notGotoOK = true;
        while (i.hasNext()) {
            final Chunk chunk = i.next();
            if (this.name != null && notGotoOK && !chunk.isEmpty()) {
                chunk.setLocalDestination(this.name);
                notGotoOK = false;
            }
            if (localDestination) {
                chunk.setLocalGoto(this.reference.substring(1));
            }
            else if (this.reference != null) {
                chunk.setAnchor(this.reference);
            }
            tmp.add(chunk);
        }
        return tmp;
    }
    
    @Override
    public int type() {
        return 17;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setReference(final String reference) {
        this.reference = reference;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getReference() {
        return this.reference;
    }
    
    public URL getUrl() {
        try {
            return new URL(this.reference);
        }
        catch (final MalformedURLException mue) {
            return null;
        }
    }
}
