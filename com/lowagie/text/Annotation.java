package com.lowagie.text;

import java.util.ArrayList;
import java.net.URL;
import java.util.HashMap;

public class Annotation implements Element
{
    public static final int TEXT = 0;
    public static final int URL_NET = 1;
    public static final int URL_AS_STRING = 2;
    public static final int FILE_DEST = 3;
    public static final int FILE_PAGE = 4;
    public static final int NAMED_DEST = 5;
    public static final int LAUNCH = 6;
    public static final int SCREEN = 7;
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String URL = "url";
    public static final String FILE = "file";
    public static final String DESTINATION = "destination";
    public static final String PAGE = "page";
    public static final String NAMED = "named";
    public static final String APPLICATION = "application";
    public static final String PARAMETERS = "parameters";
    public static final String OPERATION = "operation";
    public static final String DEFAULTDIR = "defaultdir";
    public static final String LLX = "llx";
    public static final String LLY = "lly";
    public static final String URX = "urx";
    public static final String URY = "ury";
    public static final String MIMETYPE = "mime";
    protected int annotationtype;
    protected HashMap annotationAttributes;
    protected float llx;
    protected float lly;
    protected float urx;
    protected float ury;
    
    private Annotation(final float llx, final float lly, final float urx, final float ury) {
        this.annotationAttributes = new HashMap();
        this.llx = Float.NaN;
        this.lly = Float.NaN;
        this.urx = Float.NaN;
        this.ury = Float.NaN;
        this.llx = llx;
        this.lly = lly;
        this.urx = urx;
        this.ury = ury;
    }
    
    public Annotation(final Annotation an) {
        this.annotationAttributes = new HashMap();
        this.llx = Float.NaN;
        this.lly = Float.NaN;
        this.urx = Float.NaN;
        this.ury = Float.NaN;
        this.annotationtype = an.annotationtype;
        this.annotationAttributes = an.annotationAttributes;
        this.llx = an.llx;
        this.lly = an.lly;
        this.urx = an.urx;
        this.ury = an.ury;
    }
    
    public Annotation(final String title, final String text) {
        this.annotationAttributes = new HashMap();
        this.llx = Float.NaN;
        this.lly = Float.NaN;
        this.urx = Float.NaN;
        this.ury = Float.NaN;
        this.annotationtype = 0;
        this.annotationAttributes.put("title", title);
        this.annotationAttributes.put("content", text);
    }
    
    public Annotation(final String title, final String text, final float llx, final float lly, final float urx, final float ury) {
        this(llx, lly, urx, ury);
        this.annotationtype = 0;
        this.annotationAttributes.put("title", title);
        this.annotationAttributes.put("content", text);
    }
    
    public Annotation(final float llx, final float lly, final float urx, final float ury, final URL url) {
        this(llx, lly, urx, ury);
        this.annotationtype = 1;
        this.annotationAttributes.put("url", url);
    }
    
    public Annotation(final float llx, final float lly, final float urx, final float ury, final String url) {
        this(llx, lly, urx, ury);
        this.annotationtype = 2;
        this.annotationAttributes.put("file", url);
    }
    
    public Annotation(final float llx, final float lly, final float urx, final float ury, final String file, final String dest) {
        this(llx, lly, urx, ury);
        this.annotationtype = 3;
        this.annotationAttributes.put("file", file);
        this.annotationAttributes.put("destination", dest);
    }
    
    public Annotation(final float llx, final float lly, final float urx, final float ury, final String moviePath, final String mimeType, final boolean showOnDisplay) {
        this(llx, lly, urx, ury);
        this.annotationtype = 7;
        this.annotationAttributes.put("file", moviePath);
        this.annotationAttributes.put("mime", mimeType);
        this.annotationAttributes.put("parameters", new boolean[] { false, showOnDisplay });
    }
    
    public Annotation(final float llx, final float lly, final float urx, final float ury, final String file, final int page) {
        this(llx, lly, urx, ury);
        this.annotationtype = 4;
        this.annotationAttributes.put("file", file);
        this.annotationAttributes.put("page", new Integer(page));
    }
    
    public Annotation(final float llx, final float lly, final float urx, final float ury, final int named) {
        this(llx, lly, urx, ury);
        this.annotationtype = 5;
        this.annotationAttributes.put("named", new Integer(named));
    }
    
    public Annotation(final float llx, final float lly, final float urx, final float ury, final String application, final String parameters, final String operation, final String defaultdir) {
        this(llx, lly, urx, ury);
        this.annotationtype = 6;
        this.annotationAttributes.put("application", application);
        this.annotationAttributes.put("parameters", parameters);
        this.annotationAttributes.put("operation", operation);
        this.annotationAttributes.put("defaultdir", defaultdir);
    }
    
    @Override
    public int type() {
        return 29;
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
    public ArrayList getChunks() {
        return new ArrayList();
    }
    
    public void setDimensions(final float llx, final float lly, final float urx, final float ury) {
        this.llx = llx;
        this.lly = lly;
        this.urx = urx;
        this.ury = ury;
    }
    
    public float llx() {
        return this.llx;
    }
    
    public float lly() {
        return this.lly;
    }
    
    public float urx() {
        return this.urx;
    }
    
    public float ury() {
        return this.ury;
    }
    
    public float llx(final float def) {
        if (Float.isNaN(this.llx)) {
            return def;
        }
        return this.llx;
    }
    
    public float lly(final float def) {
        if (Float.isNaN(this.lly)) {
            return def;
        }
        return this.lly;
    }
    
    public float urx(final float def) {
        if (Float.isNaN(this.urx)) {
            return def;
        }
        return this.urx;
    }
    
    public float ury(final float def) {
        if (Float.isNaN(this.ury)) {
            return def;
        }
        return this.ury;
    }
    
    public int annotationType() {
        return this.annotationtype;
    }
    
    public String title() {
        String s = this.annotationAttributes.get("title");
        if (s == null) {
            s = "";
        }
        return s;
    }
    
    public String content() {
        String s = this.annotationAttributes.get("content");
        if (s == null) {
            s = "";
        }
        return s;
    }
    
    public HashMap attributes() {
        return this.annotationAttributes;
    }
    
    @Override
    public boolean isContent() {
        return true;
    }
    
    @Override
    public boolean isNestable() {
        return true;
    }
}
