package org.apache.poi.xdgf.usermodel;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.xdgf.geom.Dimension2dDouble;
import org.apache.poi.util.Internal;
import com.microsoft.schemas.office.visio.x2012.main.PageType;

public class XDGFPage
{
    private PageType _page;
    protected XDGFPageContents _content;
    protected XDGFPages _pages;
    protected XDGFSheet _pageSheet;
    
    public XDGFPage(final PageType page, final XDGFPageContents content, final XDGFDocument document, final XDGFPages pages) {
        this._page = page;
        this._content = content;
        this._pages = pages;
        content.setPage(this);
        if (page.isSetPageSheet()) {
            this._pageSheet = new XDGFPageSheet(page.getPageSheet(), document);
        }
    }
    
    @Internal
    protected PageType getXmlObject() {
        return this._page;
    }
    
    public long getID() {
        return this._page.getID();
    }
    
    public String getName() {
        return this._page.getName();
    }
    
    public XDGFPageContents getContent() {
        return this._content;
    }
    
    public XDGFSheet getPageSheet() {
        return this._pageSheet;
    }
    
    public long getPageNumber() {
        return this._pages.getPageList().indexOf(this) + 1;
    }
    
    public Dimension2dDouble getPageSize() {
        final XDGFCell w = this._pageSheet.getCell("PageWidth");
        final XDGFCell h = this._pageSheet.getCell("PageHeight");
        if (w == null || h == null) {
            throw new POIXMLException("Cannot determine page size");
        }
        return new Dimension2dDouble(Double.parseDouble(w.getValue()), Double.parseDouble(h.getValue()));
    }
    
    public Point2D.Double getPageOffset() {
        final XDGFCell xoffcell = this._pageSheet.getCell("XRulerOrigin");
        final XDGFCell yoffcell = this._pageSheet.getCell("YRulerOrigin");
        double xoffset = 0.0;
        double yoffset = 0.0;
        if (xoffcell != null) {
            xoffset = Double.parseDouble(xoffcell.getValue());
        }
        if (yoffcell != null) {
            yoffset = Double.parseDouble(yoffcell.getValue());
        }
        return new Point2D.Double(xoffset, yoffset);
    }
    
    public Rectangle2D getBoundingBox() {
        final Dimension2dDouble sz = this.getPageSize();
        final Point2D.Double offset = this.getPageOffset();
        return new Rectangle2D.Double(-offset.getX(), -offset.getY(), sz.getWidth(), sz.getHeight());
    }
}
