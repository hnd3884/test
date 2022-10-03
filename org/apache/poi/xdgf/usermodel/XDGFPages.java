package org.apache.poi.xdgf.usermodel;

import java.util.Collections;
import com.microsoft.schemas.office.visio.x2012.main.PageType;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.xdgf.exceptions.XDGFException;
import java.io.IOException;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.ooxml.POIXMLException;
import com.microsoft.schemas.office.visio.x2012.main.PagesDocument;
import org.apache.poi.util.Internal;
import java.util.ArrayList;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.List;
import com.microsoft.schemas.office.visio.x2012.main.PagesType;
import org.apache.poi.xdgf.xml.XDGFXMLDocumentPart;

public class XDGFPages extends XDGFXMLDocumentPart
{
    PagesType _pagesObject;
    List<XDGFPage> _pages;
    
    public XDGFPages(final PackagePart part) {
        super(part);
        this._pages = new ArrayList<XDGFPage>();
    }
    
    @Internal
    PagesType getXmlObject() {
        return this._pagesObject;
    }
    
    @Override
    protected void onDocumentRead() {
        try {
            try {
                this._pagesObject = PagesDocument.Factory.parse(this.getPackagePart().getInputStream()).getPages();
            }
            catch (final XmlException | IOException e) {
                throw new POIXMLException(e);
            }
            for (final PageType pageSettings : this._pagesObject.getPageArray()) {
                final String relId = pageSettings.getRel().getId();
                final POIXMLDocumentPart pageContentsPart = this.getRelationById(relId);
                if (pageContentsPart == null) {
                    throw new POIXMLException("PageSettings relationship for " + relId + " not found");
                }
                if (!(pageContentsPart instanceof XDGFPageContents)) {
                    throw new POIXMLException("Unexpected pages relationship for " + relId + ": " + pageContentsPart);
                }
                final XDGFPageContents contents = (XDGFPageContents)pageContentsPart;
                final XDGFPage page = new XDGFPage(pageSettings, contents, this._document, this);
                contents.onDocumentRead();
                this._pages.add(page);
            }
        }
        catch (final POIXMLException e2) {
            throw XDGFException.wrap(this, e2);
        }
    }
    
    public List<XDGFPage> getPageList() {
        return Collections.unmodifiableList((List<? extends XDGFPage>)this._pages);
    }
}
