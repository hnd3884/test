package org.apache.poi.xdgf.usermodel;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ooxml.POIXMLRelation;

public class XDGFRelation extends POIXMLRelation
{
    private static final Map<String, XDGFRelation> _table;
    public static final XDGFRelation DOCUMENT;
    public static final XDGFRelation MASTERS;
    public static final XDGFRelation MASTER;
    public static final XDGFRelation IMAGES;
    public static final XDGFRelation PAGES;
    public static final XDGFRelation PAGE;
    public static final XDGFRelation WINDOW;
    
    private XDGFRelation(final String type, final String rel, final String defaultName, final PackagePartConstructor packagePartConstructor) {
        super(type, rel, defaultName, null, packagePartConstructor, null);
        XDGFRelation._table.put(rel, this);
    }
    
    public static XDGFRelation getInstance(final String rel) {
        return XDGFRelation._table.get(rel);
    }
    
    static {
        _table = new HashMap<String, XDGFRelation>();
        DOCUMENT = new XDGFRelation("application/vnd.ms-visio.drawing.main+xml", "http://schemas.microsoft.com/visio/2010/relationships/document", "/visio/document.xml", null);
        MASTERS = new XDGFRelation("application/vnd.ms-visio.masters+xml", "http://schemas.microsoft.com/visio/2010/relationships/masters", "/visio/masters/masters.xml", XDGFMasters::new);
        MASTER = new XDGFRelation("application/vnd.ms-visio.master+xml", "http://schemas.microsoft.com/visio/2010/relationships/master", "/visio/masters/master#.xml", XDGFMasterContents::new);
        IMAGES = new XDGFRelation(null, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", null, null);
        PAGES = new XDGFRelation("application/vnd.ms-visio.pages+xml", "http://schemas.microsoft.com/visio/2010/relationships/pages", "/visio/pages/pages.xml", XDGFPages::new);
        PAGE = new XDGFRelation("application/vnd.ms-visio.page+xml", "http://schemas.microsoft.com/visio/2010/relationships/page", "/visio/pages/page#.xml", XDGFPageContents::new);
        WINDOW = new XDGFRelation("application/vnd.ms-visio.windows+xml", "http://schemas.microsoft.com/visio/2010/relationships/windows", "/visio/windows.xml", null);
    }
}
