package org.apache.poi.xdgf.usermodel;

import java.util.Iterator;
import org.apache.poi.xdgf.exceptions.XDGFException;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import java.io.IOException;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.ooxml.POIXMLException;
import com.microsoft.schemas.office.visio.x2012.main.PageContentsDocument;
import java.util.HashMap;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.Map;

public class XDGFPageContents extends XDGFBaseContents
{
    protected Map<Long, XDGFMaster> _masters;
    protected XDGFPage _page;
    
    public XDGFPageContents(final PackagePart part) {
        super(part);
        this._masters = new HashMap<Long, XDGFMaster>();
    }
    
    @Override
    protected void onDocumentRead() {
        try {
            try {
                this._pageContents = PageContentsDocument.Factory.parse(this.getPackagePart().getInputStream()).getPageContents();
            }
            catch (final XmlException | IOException e) {
                throw new POIXMLException(e);
            }
            for (final POIXMLDocumentPart part : this.getRelations()) {
                if (!(part instanceof XDGFMasterContents)) {
                    continue;
                }
                final XDGFMaster master = ((XDGFMasterContents)part).getMaster();
                this._masters.put(master.getID(), master);
            }
            super.onDocumentRead();
            for (final XDGFShape shape : this._shapes.values()) {
                if (shape.isTopmost()) {
                    shape.setupMaster(this, null);
                }
            }
        }
        catch (final POIXMLException e2) {
            throw XDGFException.wrap(this, e2);
        }
    }
    
    public XDGFPage getPage() {
        return this._page;
    }
    
    protected void setPage(final XDGFPage page) {
        this._page = page;
    }
    
    public XDGFMaster getMasterById(final long id) {
        return this._masters.get(id);
    }
}
