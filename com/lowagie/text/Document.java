package com.lowagie.text;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.ArrayList;
import java.util.List;

public class Document implements DocListener
{
    private static final String OPENPDF = "OpenPDF";
    private static final String RELEASE;
    private static final String OPENPDF_VERSION;
    public static boolean compress;
    public static boolean plainRandomAccess;
    public static float wmfFontCorrection;
    private List<DocListener> listeners;
    protected boolean open;
    protected boolean close;
    protected Rectangle pageSize;
    protected float marginLeft;
    protected float marginRight;
    protected float marginTop;
    protected float marginBottom;
    protected boolean marginMirroring;
    protected boolean marginMirroringTopBottom;
    protected String javaScript_onLoad;
    protected String javaScript_onUnLoad;
    protected String htmlStyleClass;
    protected int pageN;
    protected HeaderFooter header;
    protected HeaderFooter footer;
    protected int chapternumber;
    
    public Document() {
        this(PageSize.A4);
    }
    
    public Document(final Rectangle pageSize) {
        this(pageSize, 36.0f, 36.0f, 36.0f, 36.0f);
    }
    
    public Document(final Rectangle pageSize, final float marginLeft, final float marginRight, final float marginTop, final float marginBottom) {
        this.listeners = new ArrayList<DocListener>();
        this.marginLeft = 0.0f;
        this.marginRight = 0.0f;
        this.marginTop = 0.0f;
        this.marginBottom = 0.0f;
        this.marginMirroring = false;
        this.marginMirroringTopBottom = false;
        this.javaScript_onLoad = null;
        this.javaScript_onUnLoad = null;
        this.htmlStyleClass = null;
        this.pageN = 0;
        this.header = null;
        this.footer = null;
        this.chapternumber = 0;
        this.pageSize = pageSize;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
    }
    
    public void addDocListener(final DocListener listener) {
        this.listeners.add(listener);
    }
    
    public void removeDocListener(final DocListener listener) {
        this.listeners.remove(listener);
    }
    
    @Override
    public boolean add(final Element element) throws DocumentException {
        if (this.close) {
            throw new DocumentException(MessageLocalization.getComposedMessage("the.document.has.been.closed.you.can.t.add.any.elements"));
        }
        if (!this.open && element.isContent()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("the.document.is.not.open.yet.you.can.only.add.meta.information"));
        }
        boolean success = false;
        if (element instanceof ChapterAutoNumber) {
            this.chapternumber = ((ChapterAutoNumber)element).setAutomaticNumber(this.chapternumber);
        }
        for (final DocListener listener : this.listeners) {
            success |= listener.add(element);
        }
        if (element instanceof LargeElement) {
            final LargeElement e = (LargeElement)element;
            if (!e.isComplete()) {
                e.flushContent();
            }
        }
        return success;
    }
    
    @Override
    public void open() {
        if (!this.close) {
            this.open = true;
        }
        for (final DocListener listener : this.listeners) {
            listener.setPageSize(this.pageSize);
            listener.setMargins(this.marginLeft, this.marginRight, this.marginTop, this.marginBottom);
            listener.open();
        }
    }
    
    @Override
    public boolean setPageSize(final Rectangle pageSize) {
        this.pageSize = pageSize;
        for (final DocListener listener : this.listeners) {
            listener.setPageSize(pageSize);
        }
        return true;
    }
    
    @Override
    public boolean setMargins(final float marginLeft, final float marginRight, final float marginTop, final float marginBottom) {
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
        for (final DocListener listener : this.listeners) {
            listener.setMargins(marginLeft, marginRight, marginTop, marginBottom);
        }
        return true;
    }
    
    @Override
    public boolean newPage() {
        if (!this.open || this.close) {
            return false;
        }
        for (final DocListener listener : this.listeners) {
            listener.newPage();
        }
        return true;
    }
    
    @Override
    public void setHeader(final HeaderFooter header) {
        this.header = header;
        for (final DocListener listener : this.listeners) {
            listener.setHeader(header);
        }
    }
    
    @Override
    public void resetHeader() {
        this.header = null;
        for (final DocListener listener : this.listeners) {
            listener.resetHeader();
        }
    }
    
    @Override
    public void setFooter(final HeaderFooter footer) {
        this.footer = footer;
        for (final DocListener listener : this.listeners) {
            listener.setFooter(footer);
        }
    }
    
    @Override
    public void resetFooter() {
        this.footer = null;
        for (final DocListener listener : this.listeners) {
            listener.resetFooter();
        }
    }
    
    @Override
    public void resetPageCount() {
        this.pageN = 0;
        for (final DocListener listener : this.listeners) {
            listener.resetPageCount();
        }
    }
    
    @Override
    public void setPageCount(final int pageN) {
        this.pageN = pageN;
        for (final DocListener listener : this.listeners) {
            listener.setPageCount(pageN);
        }
    }
    
    public int getPageNumber() {
        return this.pageN;
    }
    
    @Override
    public void close() {
        if (!this.close) {
            this.open = false;
            this.close = true;
        }
        for (final DocListener listener : this.listeners) {
            listener.close();
        }
    }
    
    public boolean addHeader(final String name, final String content) {
        try {
            return this.add(new Header(name, content));
        }
        catch (final DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
    
    public boolean addTitle(final String title) {
        try {
            return this.add(new Meta(1, title));
        }
        catch (final DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
    
    public boolean addSubject(final String subject) {
        try {
            return this.add(new Meta(2, subject));
        }
        catch (final DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
    
    public boolean addKeywords(final String keywords) {
        try {
            return this.add(new Meta(3, keywords));
        }
        catch (final DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
    
    public boolean addAuthor(final String author) {
        try {
            return this.add(new Meta(4, author));
        }
        catch (final DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
    
    public boolean addCreator(final String creator) {
        try {
            return this.add(new Meta(7, creator));
        }
        catch (final DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
    
    public boolean addProducer() {
        return this.addProducer(getVersion());
    }
    
    public boolean addProducer(final String producer) {
        return this.add(new Meta(5, producer));
    }
    
    public boolean addCreationDate() {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            return this.add(new Meta(6, sdf.format(new Date())));
        }
        catch (final DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
    
    public float leftMargin() {
        return this.marginLeft;
    }
    
    public float rightMargin() {
        return this.marginRight;
    }
    
    public float topMargin() {
        return this.marginTop;
    }
    
    public float bottomMargin() {
        return this.marginBottom;
    }
    
    public float left() {
        return this.pageSize.getLeft(this.marginLeft);
    }
    
    public float right() {
        return this.pageSize.getRight(this.marginRight);
    }
    
    public float top() {
        return this.pageSize.getTop(this.marginTop);
    }
    
    public float bottom() {
        return this.pageSize.getBottom(this.marginBottom);
    }
    
    public float left(final float margin) {
        return this.pageSize.getLeft(this.marginLeft + margin);
    }
    
    public float right(final float margin) {
        return this.pageSize.getRight(this.marginRight + margin);
    }
    
    public float top(final float margin) {
        return this.pageSize.getTop(this.marginTop + margin);
    }
    
    public float bottom(final float margin) {
        return this.pageSize.getBottom(this.marginBottom + margin);
    }
    
    public Rectangle getPageSize() {
        return this.pageSize;
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    public static String getProduct() {
        return "OpenPDF";
    }
    
    public static String getRelease() {
        return Document.RELEASE;
    }
    
    public static String getVersion() {
        return Document.OPENPDF_VERSION;
    }
    
    public void setJavaScript_onLoad(final String code) {
        this.javaScript_onLoad = code;
    }
    
    public String getJavaScript_onLoad() {
        return this.javaScript_onLoad;
    }
    
    public void setJavaScript_onUnLoad(final String code) {
        this.javaScript_onUnLoad = code;
    }
    
    public String getJavaScript_onUnLoad() {
        return this.javaScript_onUnLoad;
    }
    
    public void setHtmlStyleClass(final String htmlStyleClass) {
        this.htmlStyleClass = htmlStyleClass;
    }
    
    public String getHtmlStyleClass() {
        return this.htmlStyleClass;
    }
    
    @Override
    public boolean setMarginMirroring(final boolean marginMirroring) {
        this.marginMirroring = marginMirroring;
        for (final DocListener listener : this.listeners) {
            listener.setMarginMirroring(marginMirroring);
        }
        return true;
    }
    
    @Override
    public boolean setMarginMirroringTopBottom(final boolean marginMirroringTopBottom) {
        this.marginMirroringTopBottom = marginMirroringTopBottom;
        for (final DocListener listener : this.listeners) {
            listener.setMarginMirroringTopBottom(marginMirroringTopBottom);
        }
        return true;
    }
    
    public boolean isMarginMirroring() {
        return this.marginMirroring;
    }
    
    static {
        RELEASE = VersionBean.VERSION.getImplementationVersion();
        OPENPDF_VERSION = "OpenPDF " + Document.RELEASE;
        Document.compress = true;
        Document.plainRandomAccess = false;
        Document.wmfFontCorrection = 0.86f;
    }
}
